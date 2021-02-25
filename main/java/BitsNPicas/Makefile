SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | sort | uniq | tr / .)

all: clean bin doc BitsNPicas.jar BitsNPicas.app BitsNPicas-src.tgz MapEdit.jar MapEdit.app MapEdit-src.tgz KeyEdit.jar KeyEdit.app KeyEdit-src.tgz

eclipseall: eclipseclean BitsNPicas.jar BitsNPicas.app BitsNPicas-src.tgz MapEdit.jar MapEdit.app MapEdit-src.tgz KeyEdit.jar KeyEdit.app KeyEdit-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz
	rm -rf BitsNPicas*.app
	rm -rf MapEdit*.jar
	rm -rf MapEdit*.tgz
	rm -rf MapEdit*.app
	rm -rf KeyEdit*.jar
	rm -rf KeyEdit*.tgz
	rm -rf KeyEdit*.app

eclipseclean:
	rm -rf BitsNPicas*.jar
	rm -rf BitsNPicas*.tgz
	rm -rf BitsNPicas*.app
	rm -rf MapEdit*.jar
	rm -rf MapEdit*.tgz
	rm -rf MapEdit*.app
	rm -rf KeyEdit*.jar
	rm -rf KeyEdit*.tgz
	rm -rf KeyEdit*.app

bin: src
	mkdir -p bin
	javac -classpath dep/KSFL.jar:dep/ual.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc -classpath dep/KSFL.jar:dep/ual.jar -sourcepath src $(PACKAGES) -d doc

BitsNPicas.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF BitsNPicas.jar -C bin com/kreative/bitsnpicas
	chmod +x BitsNPicas.jar

BitsNPicas.app: dep BitsNPicas.jar
	mkdir -p BitsNPicas.app/Contents/MacOS
	mkdir -p BitsNPicas.app/Contents/Resources/Java
	cp -f dep/PkgInfo BitsNPicas.app/Contents
	cp -f dep/Info.plist BitsNPicas.app/Contents
	cp -f dep/universalJavaApplicationStub BitsNPicas.app/Contents/MacOS/BitsNPicas
	cp -f dep/kbnp*.icns dep/dmov*.icns dep/movr*.icns BitsNPicas.app/Contents/Resources
	cp -f dep/*.jar BitsNPicas.app/Contents/Resources/Java
	cp -f BitsNPicas.jar BitsNPicas.app/Contents/Resources/Java

BitsNPicas.dmg: BitsNPicas.app
	mkdir -p dmgtmp
	cp -R BitsNPicas.app dmgtmp
	cp BitsNPicas.jar dmgtmp
	hdiutil create -fs HFS+ -volname "Bits'n'Picas" -srcfolder dmgtmp BitsNPicas.dmg
	rm -rf dmgtmp

BitsNPicas-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf BitsNPicas-src.tgz src/com/kreative/bitsnpicas LICENSE

MapEdit.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-ME.MF MapEdit.jar -C bin com/kreative/bitsnpicas/unicode -C bin com/kreative/fontmap -C bin com/kreative/mapedit
	chmod +x MapEdit.jar

MapEdit.app: dep MapEdit.jar
	mkdir -p MapEdit.app/Contents/MacOS
	mkdir -p MapEdit.app/Contents/Resources/Java
	cp -f dep/PkgInfo-ME MapEdit.app/Contents/PkgInfo
	cp -f dep/Info-ME.plist MapEdit.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub MapEdit.app/Contents/MacOS/MapEdit
	cp -f dep/mapedit*.icns MapEdit.app/Contents/Resources
	cp -f dep/ual.jar MapEdit.app/Contents/Resources/Java
	cp -f MapEdit.jar MapEdit.app/Contents/Resources/Java

MapEdit.dmg: MapEdit.app
	mkdir -p dmgtmp
	cp -R MapEdit.app dmgtmp
	cp MapEdit.jar dmgtmp
	hdiutil create -fs HFS+ -volname "MapEdit" -srcfolder dmgtmp MapEdit.dmg
	rm -rf dmgtmp

MapEdit-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf MapEdit-src.tgz src/com/kreative/bitsnpicas/unicode src/com/kreative/fontmap src/com/kreative/mapedit LICENSE

KeyEdit.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-KE.MF KeyEdit.jar -C bin com/kreative/bitsnpicas/unicode -C bin com/kreative/fontmap -C bin com/kreative/keyedit
	chmod +x KeyEdit.jar

KeyEdit.app: dep KeyEdit.jar
	mkdir -p KeyEdit.app/Contents/MacOS
	mkdir -p KeyEdit.app/Contents/Resources/Java
	cp -f dep/PkgInfo-KE KeyEdit.app/Contents/PkgInfo
	cp -f dep/Info-KE.plist KeyEdit.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub KeyEdit.app/Contents/MacOS/KeyEdit
	cp -f dep/keyedit*.icns KeyEdit.app/Contents/Resources
	cp -f dep/ual.jar KeyEdit.app/Contents/Resources/Java
	cp -f KeyEdit.jar KeyEdit.app/Contents/Resources/Java

KeyEdit.dmg: KeyEdit.app
	mkdir -p dmgtmp
	cp -R KeyEdit.app dmgtmp
	cp KeyEdit.jar dmgtmp
	hdiutil create -fs HFS+ -volname "KeyEdit" -srcfolder dmgtmp KeyEdit.dmg
	rm -rf dmgtmp

KeyEdit-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf KeyEdit-src.tgz src/com/kreative/bitsnpicas/unicode src/com/kreative/fontmap src/com/kreative/keyedit LICENSE

install: BitsNPicas.jar
	sudo mkdir -p /usr/local/lib/
	sudo cp -f BitsNPicas.jar /usr/local/lib/bitsnpicas.jar
	sudo chmod -f 644 /usr/local/lib/bitsnpicas.jar
	sudo mkdir -p /usr/local/bin/
	sudo cp -f dep/bitsnpicas /usr/local/bin/bitsnpicas
	sudo chmod -f 755 /usr/local/bin/bitsnpicas
	sudo mkdir -p /usr/share/icons/hicolor/128x128/apps/
	sudo cp -f dep/bitsnpicas.png /usr/share/icons/hicolor/128x128/apps/bitsnpicas.png
	sudo chmod -f 644 /usr/share/icons/hicolor/128x128/apps/bitsnpicas.png
	sudo mkdir -p /usr/share/applications/
	sudo cp -f dep/bitsnpicas.desktop /usr/share/applications/bitsnpicas.desktop
	sudo chmod -f 644 /usr/share/applications/bitsnpicas.desktop

.PHONY: all eclipseall clean eclipseclean install
