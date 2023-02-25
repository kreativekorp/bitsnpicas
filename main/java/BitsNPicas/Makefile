SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

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
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -classpath dep/KSFL.jar:dep/ual.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -classpath dep/KSFL.jar:dep/ual.jar -sourcepath src $(PACKAGES) -d doc

BitsNPicas.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF BitsNPicas.jar -C bin com/kreative/unicode -C bin com/kreative/bitsnpicas
	chmod +x BitsNPicas.jar

BitsNPicas.app: BitsNPicas-Pre10.15.app BitsNPicas-MacOS10.15.app BitsNPicas-MacOS11.0.app

BitsNPicas-Pre10.15.app: dep BitsNPicas.jar
	mkdir -p BitsNPicas-Pre10.15.app/Contents/MacOS
	mkdir -p BitsNPicas-Pre10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo BitsNPicas-Pre10.15.app/Contents
	cp -f dep/Info.plist BitsNPicas-Pre10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-Pre10.15 BitsNPicas-Pre10.15.app/Contents/MacOS/BitsNPicas
	cp -f dep/kbnp*.icns dep/dmov*.icns dep/movr*.icns BitsNPicas-Pre10.15.app/Contents/Resources
	cp -f dep/*.jar BitsNPicas-Pre10.15.app/Contents/Resources/Java
	cp -f BitsNPicas.jar BitsNPicas-Pre10.15.app/Contents/Resources/Java

BitsNPicas-MacOS10.15.app: dep BitsNPicas.jar
	mkdir -p BitsNPicas-MacOS10.15.app/Contents/MacOS
	mkdir -p BitsNPicas-MacOS10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo BitsNPicas-MacOS10.15.app/Contents
	cp -f dep/Info.plist BitsNPicas-MacOS10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS10.15 BitsNPicas-MacOS10.15.app/Contents/MacOS/BitsNPicas
	cp -f dep/kbnp*.icns dep/dmov*.icns dep/movr*.icns BitsNPicas-MacOS10.15.app/Contents/Resources
	cp -f dep/*.jar BitsNPicas-MacOS10.15.app/Contents/Resources/Java
	cp -f BitsNPicas.jar BitsNPicas-MacOS10.15.app/Contents/Resources/Java

BitsNPicas-MacOS11.0.app: dep BitsNPicas.jar
	mkdir -p BitsNPicas-MacOS11.0.app/Contents/MacOS
	mkdir -p BitsNPicas-MacOS11.0.app/Contents/Resources/Java
	cp -f dep/PkgInfo BitsNPicas-MacOS11.0.app/Contents
	cp -f dep/Info.plist BitsNPicas-MacOS11.0.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS11.0 BitsNPicas-MacOS11.0.app/Contents/MacOS/BitsNPicas
	cp -f dep/kbnp*.icns dep/dmov*.icns dep/movr*.icns BitsNPicas-MacOS11.0.app/Contents/Resources
	cp -f dep/*.jar BitsNPicas-MacOS11.0.app/Contents/Resources/Java
	cp -f BitsNPicas.jar BitsNPicas-MacOS11.0.app/Contents/Resources/Java

BitsNPicas.dmg: BitsNPicas.app
	mkdir -p dmgtmp
	cp -R BitsNPicas-Pre10.15.app dmgtmp
	cp -R BitsNPicas-MacOS10.15.app dmgtmp
	cp -R BitsNPicas-MacOS11.0.app dmgtmp
	cp BitsNPicas.jar dmgtmp
	hdiutil create -fs HFS+ -volname "Bits'n'Picas" -srcfolder dmgtmp BitsNPicas.dmg
	rm -rf dmgtmp

BitsNPicas-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf BitsNPicas-src.tgz LICENSE src/com/kreative/unicode src/com/kreative/bitsnpicas

MapEdit.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-ME.MF MapEdit.jar -C bin com/kreative/unicode -C bin com/kreative/mapedit
	chmod +x MapEdit.jar

MapEdit.app: MapEdit-Pre10.15.app MapEdit-MacOS10.15.app MapEdit-MacOS11.0.app

MapEdit-Pre10.15.app: dep MapEdit.jar
	mkdir -p MapEdit-Pre10.15.app/Contents/MacOS
	mkdir -p MapEdit-Pre10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo-ME MapEdit-Pre10.15.app/Contents/PkgInfo
	cp -f dep/Info-ME.plist MapEdit-Pre10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-Pre10.15 MapEdit-Pre10.15.app/Contents/MacOS/MapEdit
	cp -f dep/mapedit*.icns MapEdit-Pre10.15.app/Contents/Resources
	cp -f dep/ual.jar MapEdit-Pre10.15.app/Contents/Resources/Java
	cp -f MapEdit.jar MapEdit-Pre10.15.app/Contents/Resources/Java

MapEdit-MacOS10.15.app: dep MapEdit.jar
	mkdir -p MapEdit-MacOS10.15.app/Contents/MacOS
	mkdir -p MapEdit-MacOS10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo-ME MapEdit-MacOS10.15.app/Contents/PkgInfo
	cp -f dep/Info-ME.plist MapEdit-MacOS10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS10.15 MapEdit-MacOS10.15.app/Contents/MacOS/MapEdit
	cp -f dep/mapedit*.icns MapEdit-MacOS10.15.app/Contents/Resources
	cp -f dep/ual.jar MapEdit-MacOS10.15.app/Contents/Resources/Java
	cp -f MapEdit.jar MapEdit-MacOS10.15.app/Contents/Resources/Java

MapEdit-MacOS11.0.app: dep MapEdit.jar
	mkdir -p MapEdit-MacOS11.0.app/Contents/MacOS
	mkdir -p MapEdit-MacOS11.0.app/Contents/Resources/Java
	cp -f dep/PkgInfo-ME MapEdit-MacOS11.0.app/Contents/PkgInfo
	cp -f dep/Info-ME.plist MapEdit-MacOS11.0.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS11.0 MapEdit-MacOS11.0.app/Contents/MacOS/MapEdit
	cp -f dep/mapedit*.icns MapEdit-MacOS11.0.app/Contents/Resources
	cp -f dep/ual.jar MapEdit-MacOS11.0.app/Contents/Resources/Java
	cp -f MapEdit.jar MapEdit-MacOS11.0.app/Contents/Resources/Java

MapEdit.dmg: MapEdit.app
	mkdir -p dmgtmp
	cp -R MapEdit-Pre10.15.app dmgtmp
	cp -R MapEdit-MacOS10.15.app dmgtmp
	cp -R MapEdit-MacOS11.0.app dmgtmp
	cp MapEdit.jar dmgtmp
	hdiutil create -fs HFS+ -volname "MapEdit" -srcfolder dmgtmp MapEdit.dmg
	rm -rf dmgtmp

MapEdit-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf MapEdit-src.tgz LICENSE src/com/kreative/unicode src/com/kreative/mapedit

KeyEdit.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-KE.MF KeyEdit.jar -C bin com/kreative/unicode -C bin com/kreative/keyedit
	chmod +x KeyEdit.jar

KeyEdit.app: KeyEdit-Pre10.15.app KeyEdit-MacOS10.15.app KeyEdit-MacOS11.0.app

KeyEdit-Pre10.15.app: dep KeyEdit.jar
	mkdir -p KeyEdit-Pre10.15.app/Contents/MacOS
	mkdir -p KeyEdit-Pre10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo-KE KeyEdit-Pre10.15.app/Contents/PkgInfo
	cp -f dep/Info-KE.plist KeyEdit-Pre10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-Pre10.15 KeyEdit-Pre10.15.app/Contents/MacOS/KeyEdit
	cp -f dep/keyedit*.icns KeyEdit-Pre10.15.app/Contents/Resources
	cp -f dep/ual.jar KeyEdit-Pre10.15.app/Contents/Resources/Java
	cp -f KeyEdit.jar KeyEdit-Pre10.15.app/Contents/Resources/Java

KeyEdit-MacOS10.15.app: dep KeyEdit.jar
	mkdir -p KeyEdit-MacOS10.15.app/Contents/MacOS
	mkdir -p KeyEdit-MacOS10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo-KE KeyEdit-MacOS10.15.app/Contents/PkgInfo
	cp -f dep/Info-KE.plist KeyEdit-MacOS10.15.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS10.15 KeyEdit-MacOS10.15.app/Contents/MacOS/KeyEdit
	cp -f dep/keyedit*.icns KeyEdit-MacOS10.15.app/Contents/Resources
	cp -f dep/ual.jar KeyEdit-MacOS10.15.app/Contents/Resources/Java
	cp -f KeyEdit.jar KeyEdit-MacOS10.15.app/Contents/Resources/Java

KeyEdit-MacOS11.0.app: dep KeyEdit.jar
	mkdir -p KeyEdit-MacOS11.0.app/Contents/MacOS
	mkdir -p KeyEdit-MacOS11.0.app/Contents/Resources/Java
	cp -f dep/PkgInfo-KE KeyEdit-MacOS11.0.app/Contents/PkgInfo
	cp -f dep/Info-KE.plist KeyEdit-MacOS11.0.app/Contents/Info.plist
	cp -f dep/universalJavaApplicationStub-MacOS11.0 KeyEdit-MacOS11.0.app/Contents/MacOS/KeyEdit
	cp -f dep/keyedit*.icns KeyEdit-MacOS11.0.app/Contents/Resources
	cp -f dep/ual.jar KeyEdit-MacOS11.0.app/Contents/Resources/Java
	cp -f KeyEdit.jar KeyEdit-MacOS11.0.app/Contents/Resources/Java

KeyEdit.dmg: KeyEdit.app
	mkdir -p dmgtmp
	cp -R KeyEdit-Pre10.15.app dmgtmp
	cp -R KeyEdit-MacOS10.15.app dmgtmp
	cp -R KeyEdit-MacOS11.0.app dmgtmp
	cp KeyEdit.jar dmgtmp
	hdiutil create -fs HFS+ -volname "KeyEdit" -srcfolder dmgtmp KeyEdit.dmg
	rm -rf dmgtmp

KeyEdit-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf KeyEdit-src.tgz LICENSE src/com/kreative/unicode src/com/kreative/keyedit

install: dep BitsNPicas.jar MapEdit.jar KeyEdit.jar
	sudo mkdir -p /usr/local/lib/
	sudo cp -f BitsNPicas.jar /usr/local/lib/bitsnpicas.jar
	sudo cp -f MapEdit.jar /usr/local/lib/mapedit.jar
	sudo cp -f KeyEdit.jar /usr/local/lib/keyedit.jar
	sudo chmod -f 644 /usr/local/lib/bitsnpicas.jar
	sudo chmod -f 644 /usr/local/lib/mapedit.jar
	sudo chmod -f 644 /usr/local/lib/keyedit.jar
	sudo mkdir -p /usr/local/bin/
	sudo cp -f dep/bitsnpicas /usr/local/bin/bitsnpicas
	sudo cp -f dep/mapedit /usr/local/bin/mapedit
	sudo cp -f dep/keyedit /usr/local/bin/keyedit
	sudo chmod -f 755 /usr/local/bin/bitsnpicas
	sudo chmod -f 755 /usr/local/bin/mapedit
	sudo chmod -f 755 /usr/local/bin/keyedit
	sudo mkdir -p /usr/share/icons/hicolor/128x128/apps/
	sudo mkdir -p /usr/share/icons/hicolor/256x256/apps/
	sudo mkdir -p /usr/share/icons/hicolor/512x512/apps/
	sudo cp -f dep/bitsnpicas.png /usr/share/icons/hicolor/128x128/apps/bitsnpicas.png
	sudo cp -f dep/kbnp-icon.png /usr/share/icons/hicolor/512x512/apps/bitsnpicas.png
	sudo cp -f dep/mapedit-icon.png /usr/share/icons/hicolor/512x512/apps/mapedit.png
	sudo cp -f dep/keyedit-icon.png /usr/share/icons/hicolor/256x256/apps/keyedit.png
	sudo chmod -f 644 /usr/share/icons/hicolor/128x128/apps/bitsnpicas.png
	sudo chmod -f 644 /usr/share/icons/hicolor/512x512/apps/bitsnpicas.png
	sudo chmod -f 644 /usr/share/icons/hicolor/512x512/apps/mapedit.png
	sudo chmod -f 644 /usr/share/icons/hicolor/256x256/apps/keyedit.png
	sudo mkdir -p /usr/share/applications/
	sudo cp -f dep/bitsnpicas.desktop /usr/share/applications/bitsnpicas.desktop
	sudo cp -f dep/mapedit.desktop /usr/share/applications/mapedit.desktop
	sudo cp -f dep/keyedit.desktop /usr/share/applications/keyedit.desktop
	sudo chmod -f 644 /usr/share/applications/bitsnpicas.desktop
	sudo chmod -f 644 /usr/share/applications/mapedit.desktop
	sudo chmod -f 644 /usr/share/applications/keyedit.desktop

.PHONY: all eclipseall clean eclipseclean BitsNPicas.app MapEdit.app KeyEdit.app install
