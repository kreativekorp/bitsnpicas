SRCFILES = src/*.java src/com/kreative/*/*.java src/com/kreative/*/*/*.java
PACKAGES = com.kreative.bitsnpicas com.kreative.bitsnpicas.exporter com.kreative.bitsnpicas.importer com.kreative.bitsnpicas.transformer com.kreative.bitsnpicas.truetype

all: clean bin doc osxclean BitsNPicas.jar BitsNPicas-src.tgz

eclipseall: eclipseclean osxclean BitsNPicas.jar BitsNPicas-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz

eclipseclean:
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz

bin:
	mkdir -p bin
	javac -target 1.5 -sourcepath src $(SRCFILES) -d bin

doc:
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	rm -f src/.DS_Store
	rm -f src/*/.DS_Store
	rm -f src/*/*/.DS_Store
	rm -f src/*/*/*/.DS_Store
	rm -f src/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/.DS_Store
	rm -f bin/*/.DS_Store
	rm -f bin/*/*/.DS_Store
	rm -f bin/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/*/.DS_Store

BitsNPicas.jar: osxclean
	jar cmf dep/MANIFEST.MF BitsNPicas.jar -C bin com/kreative

BitsNPicas-src.tgz: osxclean
	tar -czf BitsNPicas-src.tgz src/com/kreative LICENSE

.PHONY: all eclipseall clean eclipseclean osxclean
