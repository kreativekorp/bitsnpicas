# nativeJavaApplicationStub

Universal Mach-O launcher for Bits'N'Picas / MapEdit / KeyEdit on macOS 10.15+ and Apple Silicon.

Replaces the archived `shc`-compiled `universalJavaApplicationStub` binaries (x86_64-only) with auditable C source.

## Why Mach-O (not bash)

On macOS 10.15+, a shell script as `CFBundleExecutable` mis-binds TCC so folder prompts may appear as `bash`/`java` instead of the app name, and can break protected-folder UX. See [universalJavaApplicationStub#85](https://github.com/tofi86/universalJavaApplicationStub/issues/85) and Bits'N'Picas [#25](https://github.com/kreativekorp/bitsnpicas/issues/25).

This stub:

1. Prefer **in-process `JLI_Launch`** via `libjli.dylib` (process stays `Contents/MacOS/<App>`).
2. Fall back to **`execv` of `java`** if `JLI_Launch` is unavailable.

## Build (Makefile does this)

```bash
clang -O2 -target x86_64-apple-macos10.15 \
  -framework CoreFoundation \
  -o dep/nativeJavaApplicationStub/nativeJavaApplicationStub-x86_64 \
  dep/nativeJavaApplicationStub/nativeJavaApplicationStub.c
clang -O2 -target arm64-apple-macos11.0 \
  -framework CoreFoundation \
  -o dep/nativeJavaApplicationStub/nativeJavaApplicationStub-arm64 \
  dep/nativeJavaApplicationStub/nativeJavaApplicationStub.c
lipo -create \
  -output dep/nativeJavaApplicationStub/nativeJavaApplicationStub \
  dep/nativeJavaApplicationStub/nativeJavaApplicationStub-x86_64 \
  dep/nativeJavaApplicationStub/nativeJavaApplicationStub-arm64
rm -f dep/nativeJavaApplicationStub/nativeJavaApplicationStub-x86_64 \
  dep/nativeJavaApplicationStub/nativeJavaApplicationStub-arm64
```

The built binary is `dep/nativeJavaApplicationStub/nativeJavaApplicationStub` (same directory as the `.c` source; a file cannot share the directory's path).

Requires **Clang + macOS SDK** (needed for `.app` targets / `make all` on Mac). Jar-only targets do not need Clang.

## Exit codes

| Code | Meaning |
|------|---------|
| 1 | No suitable Java |
| 2 | Missing `JavaX.MainClass` |
| 13 | Bad `WorkingDirectory` |
| 14 | Bad / missing plist |
| 20 | Internal path resolution failure |

## Notes

- Still requires a locally installed JDK (not a bundled JRE).
- **JDK discovery (Finder launches):** does **not** require `JAVA_HOME`. Order: `JAVA_HOME` → `/usr/libexec/java_home -a <native> -F` → `java_home -F` → exact `-v` only when plist asks for an exact version → Homebrew / `/Library/Java` scan. Avoids `java_home -F -v '1.8+'`, which fails on recent macOS with “supports (null)” even when JDKs are installed.
- `JLI_Launch` is a JDK-internal symbol; probed at `lib/libjli.dylib`, `lib/jli/libjli.dylib`, `jre/lib/jli/libjli.dylib`, and `../MacOS/libjli.dylib`.
- Pre-Catalina apps keep `universalJavaApplicationStub-Pre10.15` (bash).

## License

MIT
