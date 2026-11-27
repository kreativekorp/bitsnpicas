/*
 * nativeJavaApplicationStub — Universal Mach-O Java launcher for Bits'N'Picas apps.
 *
 * MIT License
 * Copyright (c) 2026 Bits'N'Picas contributors
 *
 * Reads Apple-style JavaX keys from Info.plist, finds a JDK, and launches the
 * JVM. Prefers in-process JLI_Launch (keeps CFBundleExecutable identity for TCC
 * / OpenFilesHandler); falls back to execv of bin/java.
 */
#include <CoreFoundation/CoreFoundation.h>

#include <dlfcn.h>
#include <errno.h>
#include <fcntl.h>
#include <libgen.h>
#include <limits.h>
#include <mach-o/dyld.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <unistd.h>

#define EXIT_NO_JAVA 1
#define EXIT_NO_MAINCLASS 2
#define EXIT_BAD_WORKDIR 13
#define EXIT_BAD_PLIST 14
#define EXIT_INTERNAL 20

#define MAX_ARGS 256
#define MAX_ARG_LEN 4096

typedef int (*JLI_Launch_t)(
	int argc, char **argv,
	int jargc, const char **jargv,
	int appclassc, const char **appclassv,
	const char *fullversion,
	const char *dotversion,
	const char *pname,
	const char *lname,
	int javaargs,
	int cpwildcard,
	int javaw,
	int ergo);

static char gExePath[PATH_MAX];
static char gAppPackage[PATH_MAX];
static char gJavaRoot[PATH_MAX];
static char gResources[PATH_MAX];
static char gUserHome[PATH_MAX];

static char *gArgvOwned[MAX_ARGS];
static int gArgcOwned = 0;

static void die_alert(const char *title, const char *message, int code) {
	CFStringRef t = CFStringCreateWithCString(NULL, title ? title : "Error", kCFStringEncodingUTF8);
	CFStringRef m = CFStringCreateWithCString(NULL, message ? message : "Unknown error", kCFStringEncodingUTF8);
	if (t && m) {
		CFUserNotificationDisplayAlert(
			0, kCFUserNotificationStopAlertLevel,
			NULL, NULL, NULL,
			t, m,
			CFSTR("OK"), NULL, NULL,
			NULL);
	}
	if (t) CFRelease(t);
	if (m) CFRelease(m);
	exit(code);
}

static int path_is_exec(const char *path) {
	struct stat st;
	return path && path[0] && stat(path, &st) == 0 && S_ISREG(st.st_mode) && (st.st_mode & S_IXUSR);
}

static int resolve_paths(void) {
	uint32_t size = sizeof(gExePath);
	if (_NSGetExecutablePath(gExePath, &size) != 0) return -1;

	char resolved[PATH_MAX];
	if (!realpath(gExePath, resolved)) {
		strncpy(resolved, gExePath, sizeof(resolved) - 1);
		resolved[sizeof(resolved) - 1] = '\0';
	}
	strncpy(gExePath, resolved, sizeof(gExePath) - 1);
	gExePath[sizeof(gExePath) - 1] = '\0';

	/* .../App.app/Contents/MacOS/Exec -> App.app */
	char tmp[PATH_MAX];
	strncpy(tmp, gExePath, sizeof(tmp) - 1);
	tmp[sizeof(tmp) - 1] = '\0';
	char *macos = dirname(tmp);
	char tmp2[PATH_MAX];
	strncpy(tmp2, macos, sizeof(tmp2) - 1);
	tmp2[sizeof(tmp2) - 1] = '\0';
	char *contents = dirname(tmp2);
	char tmp3[PATH_MAX];
	strncpy(tmp3, contents, sizeof(tmp3) - 1);
	tmp3[sizeof(tmp3) - 1] = '\0';
	char *app = dirname(tmp3);
	strncpy(gAppPackage, app, sizeof(gAppPackage) - 1);
	gAppPackage[sizeof(gAppPackage) - 1] = '\0';

	snprintf(gJavaRoot, sizeof(gJavaRoot), "%s/Contents/Resources/Java", gAppPackage);
	snprintf(gResources, sizeof(gResources), "%s/Contents/Resources", gAppPackage);

	const char *home = getenv("HOME");
	if (!home) home = "";
	strncpy(gUserHome, home, sizeof(gUserHome) - 1);
	gUserHome[sizeof(gUserHome) - 1] = '\0';
	return 0;
}

static char *cf_to_c(CFStringRef s) {
	if (!s) return NULL;
	CFIndex len = CFStringGetLength(s);
	CFIndex max = CFStringGetMaximumSizeForEncoding(len, kCFStringEncodingUTF8) + 1;
	char *buf = (char *)malloc((size_t)max);
	if (!buf) return NULL;
	if (!CFStringGetCString(s, buf, max, kCFStringEncodingUTF8)) {
		free(buf);
		return NULL;
	}
	return buf;
}

static char *expand_tokens(const char *in) {
	if (!in) return NULL;
	size_t cap = strlen(in) * 2 + PATH_MAX * 2 + 64;
	char *out = (char *)malloc(cap);
	if (!out) return NULL;
	out[0] = '\0';
	const char *p = in;
	while (*p) {
		const char *token = NULL;
		const char *repl = NULL;
		if (strncmp(p, "$JAVAROOT", 9) == 0) { token = "$JAVAROOT"; repl = gJavaRoot; }
		else if (strncmp(p, "$APP_PACKAGE", 12) == 0) { token = "$APP_PACKAGE"; repl = gAppPackage; }
		else if (strncmp(p, "$APP_ROOT", 9) == 0) { token = "$APP_ROOT"; repl = gAppPackage; }
		else if (strncmp(p, "$USER_HOME", 10) == 0) { token = "$USER_HOME"; repl = gUserHome; }

		if (token) {
			size_t need = strlen(out) + strlen(repl) + 1;
			if (need > cap) {
				cap = need * 2;
				char *nbuf = (char *)realloc(out, cap);
				if (!nbuf) { free(out); return NULL; }
				out = nbuf;
			}
			strcat(out, repl);
			p += strlen(token);
		} else {
			size_t len = strlen(out);
			if (len + 2 > cap) {
				cap *= 2;
				char *nbuf = (char *)realloc(out, cap);
				if (!nbuf) { free(out); return NULL; }
				out = nbuf;
			}
			out[len] = *p++;
			out[len + 1] = '\0';
		}
	}
	return out;
}

static int push_arg(const char *s) {
	/* Reserve 2 slots for argv[0] and trailing NULL in jliArgv/execArgv. */
	if (!s || gArgcOwned >= MAX_ARGS - 2) return -1;
	char *copy = strdup(s);
	if (!copy) return -1;
	gArgvOwned[gArgcOwned++] = copy;
	return 0;
}

static void free_args(void) {
	for (int i = 0; i < gArgcOwned; i++) free(gArgvOwned[i]);
	gArgcOwned = 0;
}

static char *plist_string(CFDictionaryRef dict, CFStringRef key) {
	CFTypeRef v = CFDictionaryGetValue(dict, key);
	if (!v || CFGetTypeID(v) != CFStringGetTypeID()) return NULL;
	return cf_to_c((CFStringRef)v);
}

static void add_properties_as_dflags(CFDictionaryRef javax) {
	CFDictionaryRef props = CFDictionaryGetValue(javax, CFSTR("Properties"));
	if (!props || CFGetTypeID(props) != CFDictionaryGetTypeID()) return;

	CFIndex count = CFDictionaryGetCount(props);
	if (count <= 0) return;
	CFStringRef *keys = (CFStringRef *)malloc(sizeof(CFStringRef) * (size_t)count);
	CFStringRef *vals = (CFStringRef *)malloc(sizeof(CFStringRef) * (size_t)count);
	if (!keys || !vals) { free(keys); free(vals); return; }
	CFDictionaryGetKeysAndValues(props, (const void **)keys, (const void **)vals);
	for (CFIndex i = 0; i < count; i++) {
		if (!keys[i] || !vals[i] ||
		    CFGetTypeID(keys[i]) != CFStringGetTypeID() ||
		    CFGetTypeID(vals[i]) != CFStringGetTypeID()) {
			continue;
		}
		char *k = cf_to_c(keys[i]);
		char *v = cf_to_c(vals[i]);
		if (k && v) {
			char buf[MAX_ARG_LEN];
			snprintf(buf, sizeof(buf), "-D%s=%s", k, v);
			push_arg(buf);
		}
		free(k);
		free(v);
	}
	free(keys);
	free(vals);
}

static void add_vmoptions(const char *vmopts) {
	if (!vmopts || !*vmopts) return;
	char *copy = strdup(vmopts);
	if (!copy) return;
	char *save = NULL;
	for (char *tok = strtok_r(copy, " \t\n\r", &save); tok; tok = strtok_r(NULL, " \t\n\r", &save)) {
		push_arg(tok);
	}
	free(copy);
}

static int run_cmd_capture(char *const argv[], char *out, size_t outsz) {
	int pipefd[2];
	if (pipe(pipefd) != 0) return -1;
	pid_t pid = fork();
	if (pid < 0) { close(pipefd[0]); close(pipefd[1]); return -1; }
	if (pid == 0) {
		close(pipefd[0]);
		dup2(pipefd[1], STDOUT_FILENO);
		close(pipefd[1]);
		int devnull = open("/dev/null", O_WRONLY);
		if (devnull >= 0) {
			dup2(devnull, STDERR_FILENO);
			close(devnull);
		}
		execv(argv[0], argv);
		_exit(127);
	}
	close(pipefd[1]);
	size_t total = 0;
	while (total < outsz - 1) {
		ssize_t n = read(pipefd[0], out + total, (outsz - 1) - total);
		if (n < 0) {
			if (errno == EINTR) continue;
			close(pipefd[0]);
			int st = 0;
			waitpid(pid, &st, 0);
			return -1;
		}
		if (n == 0) break;
		total += (size_t)n;
	}
	close(pipefd[0]);
	int status = 0;
	while (waitpid(pid, &status, 0) < 0 && errno == EINTR) {}
	out[total] = '\0';
	while (total > 0 && (out[total - 1] == '\n' || out[total - 1] == '\r')) out[--total] = '\0';
	return (WIFEXITED(status) && WEXITSTATUS(status) == 0) ? 0 : -1;
}

static int native_arch_name(char *buf, size_t buflen) {
#if defined(__arm64__) || defined(__aarch64__)
	snprintf(buf, buflen, "arm64");
#else
	snprintf(buf, buflen, "x86_64");
#endif
	return 0;
}

/* Map plist JVMVersion like "1.5+" to a java_home -v filter. */
static void jvm_version_filter(const char *jvmVersion, char *out, size_t outsz) {
	out[0] = '\0';
	if (!jvmVersion || !*jvmVersion) {
		snprintf(out, outsz, "1.8+");
		return;
	}
	/* Pass through simple forms; treat 1.5+ as 1.8+ for modern macOS. */
	if (strcmp(jvmVersion, "1.5+") == 0 || strcmp(jvmVersion, "1.6+") == 0 ||
	    strcmp(jvmVersion, "1.7+") == 0) {
		snprintf(out, outsz, "1.8+");
		return;
	}
	strncpy(out, jvmVersion, outsz - 1);
	out[outsz - 1] = '\0';
}

static int find_java_home(const char *jvmVersion, char *javaHome, size_t homeSz, char *javaBin, size_t binSz) {
	const char *envHome = getenv("JAVA_HOME");
	if (envHome && envHome[0]) {
		snprintf(javaBin, binSz, "%s/bin/java", envHome);
		if (path_is_exec(javaBin)) {
			strncpy(javaHome, envHome, homeSz - 1);
			javaHome[homeSz - 1] = '\0';
			return 0;
		}
	}

	char ver[64];
	jvm_version_filter(jvmVersion, ver, sizeof(ver));
	char arch[32];
	native_arch_name(arch, sizeof(arch));

	char out[PATH_MAX];
	char *argvArch[] = {
		"/usr/libexec/java_home", "-a", arch, "-F", "-v", ver, NULL
	};
	if (run_cmd_capture(argvArch, out, sizeof(out)) == 0 && out[0]) {
		snprintf(javaBin, binSz, "%s/bin/java", out);
		if (path_is_exec(javaBin)) {
			strncpy(javaHome, out, homeSz - 1);
			javaHome[homeSz - 1] = '\0';
			return 0;
		}
	}

	char *argvAny[] = {
		"/usr/libexec/java_home", "-F", "-v", ver, NULL
	};
	if (run_cmd_capture(argvAny, out, sizeof(out)) == 0 && out[0]) {
		snprintf(javaBin, binSz, "%s/bin/java", out);
		if (path_is_exec(javaBin)) {
			strncpy(javaHome, out, homeSz - 1);
			javaHome[homeSz - 1] = '\0';
			return 0;
		}
	}
	return -1;
}

/* Returns 0 if JLI_Launch was invoked (exit code in *outRc); -1 if libjli could not be loaded. */
static int try_jli_launch(const char *javaHome, const char *progName, int *outRc) {
	const char *relPaths[] = {
		"lib/libjli.dylib",
		"lib/jli/libjli.dylib",
		"jre/lib/jli/libjli.dylib",
		"../MacOS/libjli.dylib",
		NULL
	};
	void *h = NULL;
	for (int i = 0; relPaths[i]; i++) {
		char path[PATH_MAX];
		snprintf(path, sizeof(path), "%s/%s", javaHome, relPaths[i]);
		h = dlopen(path, RTLD_NOW | RTLD_GLOBAL);
		if (h) break;
	}
	if (!h) return -1;

	JLI_Launch_t launch = (JLI_Launch_t)dlsym(h, "JLI_Launch");
	if (!launch) {
		dlclose(h);
		return -1;
	}

	/* argv[0] = stub path; argv[1..] = java options / main / passthrough. */
	char *jliArgv[MAX_ARGS];
	if (gArgcOwned + 1 >= MAX_ARGS) {
		dlclose(h);
		return -1;
	}
	jliArgv[0] = gExePath;
	for (int i = 0; i < gArgcOwned; i++) jliArgv[i + 1] = gArgvOwned[i];
	int jliArgc = gArgcOwned + 1;
	jliArgv[jliArgc] = NULL;

	const char *pname = progName && progName[0] ? progName : "java";
	int rc = launch(
		jliArgc, jliArgv,
		0, NULL,
		0, NULL,
		"",
		"0.0",
		pname,
		pname,
		0,
		0,
		0,
		0);
	/* JLI_Launch normally does not return on success. */
	(void)h;
	if (outRc) *outRc = rc;
	return 0;
}

static void strip_psn_and_collect(int argc, char **argv) {
	for (int i = 1; i < argc; i++) {
		if (strncmp(argv[i], "-psn_", 5) == 0) continue;
		push_arg(argv[i]);
	}
}

int main(int argc, char **argv) {
	if (resolve_paths() != 0) {
		die_alert("Error", "Could not resolve application bundle path.", EXIT_INTERNAL);
	}

	char plistPath[PATH_MAX];
	snprintf(plistPath, sizeof(plistPath), "%s/Contents/Info.plist", gAppPackage);

	CFURLRef url = CFURLCreateFromFileSystemRepresentation(NULL, (const UInt8 *)plistPath, (CFIndex)strlen(plistPath), false);
	if (!url) die_alert("Error", "Could not open Info.plist.", EXIT_BAD_PLIST);
	CFReadStreamRef stream = CFReadStreamCreateWithFile(NULL, url);
	CFRelease(url);
	if (!stream) die_alert("Error", "Could not open Info.plist stream.", EXIT_BAD_PLIST);
	if (!CFReadStreamOpen(stream)) {
		CFRelease(stream);
		die_alert("Error", "Could not read Info.plist.", EXIT_BAD_PLIST);
	}
	CFMutableDataRef data = CFDataCreateMutable(NULL, 0);
	UInt8 buf[4096];
	CFIndex nread;
	while ((nread = CFReadStreamRead(stream, buf, sizeof(buf))) > 0) {
		CFDataAppendBytes(data, buf, nread);
	}
	CFReadStreamClose(stream);
	CFRelease(stream);
	if (!data || CFDataGetLength(data) == 0) {
		if (data) CFRelease(data);
		die_alert("Error", "Info.plist is empty.", EXIT_BAD_PLIST);
	}
	CFPropertyListRef plist = CFPropertyListCreateWithData(NULL, data, kCFPropertyListImmutable, NULL, NULL);
	CFRelease(data);
	if (!plist || CFGetTypeID(plist) != CFDictionaryGetTypeID()) {
		if (plist) CFRelease(plist);
		die_alert("Error", "Info.plist is not a dictionary.", EXIT_BAD_PLIST);
	}
	CFDictionaryRef root = (CFDictionaryRef)plist;

	char *bundleName = plist_string(root, CFSTR("CFBundleName"));
	char *iconFile = plist_string(root, CFSTR("CFBundleIconFile"));
	CFDictionaryRef javax = CFDictionaryGetValue(root, CFSTR("JavaX"));
	if (!javax || CFGetTypeID(javax) != CFDictionaryGetTypeID()) {
		const char *title = (bundleName && bundleName[0]) ? bundleName : "Error";
		die_alert(title, "Info.plist is missing JavaX.", EXIT_BAD_PLIST);
	}

	char *mainClass = plist_string(javax, CFSTR("MainClass"));
	char *classPathRaw = plist_string(javax, CFSTR("ClassPath"));
	char *vmOptions = plist_string(javax, CFSTR("VMOptions"));
	char *workingDirRaw = plist_string(javax, CFSTR("WorkingDirectory"));
	char *jvmVersion = plist_string(javax, CFSTR("JVMVersion"));

	if (!mainClass || !mainClass[0]) {
		char title[256];
		snprintf(title, sizeof(title), "%s", (bundleName && bundleName[0]) ? bundleName : "Error");
		free(bundleName); free(iconFile); free(mainClass); free(classPathRaw);
		free(vmOptions); free(workingDirRaw); free(jvmVersion); CFRelease(plist);
		die_alert(title, "JavaX.MainClass is missing.", EXIT_NO_MAINCLASS);
	}

	char *classPath = expand_tokens(classPathRaw ? classPathRaw : "");
	char *workingDir = expand_tokens(workingDirRaw ? workingDirRaw : "$APP_PACKAGE/..");

	/* Build java argument list (without argv0): -cp ... -Xdock:... -D... VMOPTS Main args */
	push_arg("-cp");
	push_arg(classPath ? classPath : ".");

	if (iconFile && iconFile[0]) {
		char iconPath[PATH_MAX];
		if (strstr(iconFile, ".icns")) {
			snprintf(iconPath, sizeof(iconPath), "-Xdock:icon=%s/%s", gResources, iconFile);
		} else {
			snprintf(iconPath, sizeof(iconPath), "-Xdock:icon=%s/%s.icns", gResources, iconFile);
		}
		push_arg(iconPath);
	}
	if (bundleName && bundleName[0]) {
		char dockName[MAX_ARG_LEN];
		snprintf(dockName, sizeof(dockName), "-Xdock:name=%s", bundleName);
		push_arg(dockName);
	}

	add_properties_as_dflags(javax);
	add_vmoptions(vmOptions);
	push_arg(mainClass);
	strip_psn_and_collect(argc, argv);

	char javaHome[PATH_MAX];
	char javaBin[PATH_MAX];
	if (find_java_home(jvmVersion, javaHome, sizeof(javaHome), javaBin, sizeof(javaBin)) != 0) {
		char title[256];
		snprintf(title, sizeof(title), "%s", bundleName ? bundleName : "Application");
		die_alert(title, "No suitable Java Runtime was found.\nInstall a JDK (8 or newer) and try again.", EXIT_NO_JAVA);
	}

	if (workingDir && workingDir[0]) {
		if (chdir(workingDir) != 0) {
			char title[256];
			snprintf(title, sizeof(title), "%s", bundleName ? bundleName : "Application");
			die_alert(title, "Could not change to the application working directory.", EXIT_BAD_WORKDIR);
		}
	}

	setenv("CFProcessPath", gExePath, 1);
	setenv("JAVA_HOME", javaHome, 1);

	/* Prefer in-process JLI_Launch for TCC / odoc identity. */
	int jliRc = 0;
	if (try_jli_launch(javaHome, bundleName, &jliRc) == 0) {
		free_args();
		free(bundleName); free(iconFile); free(mainClass); free(classPathRaw);
		free(vmOptions); free(workingDirRaw); free(jvmVersion);
		free(classPath); free(workingDir);
		CFRelease(plist);
		return jliRc;
	}

	/* Fallback: execv java */
	char *execArgv[MAX_ARGS];
	execArgv[0] = javaBin;
	for (int i = 0; i < gArgcOwned; i++) execArgv[i + 1] = gArgvOwned[i];
	execArgv[gArgcOwned + 1] = NULL;
	execv(javaBin, execArgv);

	char title[256];
	snprintf(title, sizeof(title), "%s", bundleName ? bundleName : "Application");
	char msg[512];
	snprintf(msg, sizeof(msg), "Failed to start Java:\n%s\n%s", javaBin, strerror(errno));
	die_alert(title, msg, EXIT_NO_JAVA);
	return EXIT_NO_JAVA;
}
