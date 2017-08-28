SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell (find src -name \*.png ; find src -name \*.ttf ; find src -name \*.txt) | sed s/^src\\///)
PACKAGES := $(shell find src/com/kreative/bitsnpicas -type d \! -path \*/.\* | sed s/^src\\/// | tr / .)

all: clean bin doc BitsNPicas.jar BitsNPicas.app BitsNPicas-src.tgz

eclipseall: eclipseclean BitsNPicas.jar BitsNPicas.app BitsNPicas-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz

eclipseclean:
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz

bin: src
	mkdir -p bin
	javac -classpath dep/KSFL.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc -classpath dep/KSFL.jar -sourcepath src $(PACKAGES) -d doc

BitsNPicas.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF BitsNPicas.jar -C bin com/kreative/bitsnpicas

BitsNPicas.app: dep BitsNPicas.jar
	mkdir -p BitsNPicas.app/Contents/MacOS
	mkdir -p BitsNPicas.app/Contents/Resources/Java
	cp -f dep/PkgInfo BitsNPicas.app/Contents
	cp -f dep/Info.plist BitsNPicas.app/Contents
	cp -f dep/JavaApplicationStub BitsNPicas.app/Contents/MacOS/BitsNPicas
	cp -f dep/*.icns BitsNPicas.app/Contents/Resources
	cp -f dep/*.jar BitsNPicas.app/Contents/Resources/Java
	cp -f BitsNPicas.jar BitsNPicas.app/Contents/Resources/Java

BitsNPicas-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf BitsNPicas-src.tgz src/com/kreative/bitsnpicas LICENSE

.PHONY: all eclipseall clean eclipseclean
