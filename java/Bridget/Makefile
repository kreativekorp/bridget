SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc Bridget.jar Bridget-Applet Bridget-JWS Bridget.app Bridget.exe

eclipseall: eclipseclean Bridget.jar Bridget-Applet Bridget-JWS Bridget.app Bridget.exe

clean:
	rm -rf bin
	rm -rf doc
	rm -rf Bridget.jar
	rm -rf Bridget-Applet
	rm -rf Bridget-JWS
	rm -rf Bridget.app
	rm -rf Bridget.exe

eclipseclean:
	rm -rf Bridget.jar
	rm -rf Bridget-Applet
	rm -rf Bridget-JWS
	rm -rf Bridget.app
	rm -rf Bridget.exe

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -classpath dep/ual.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -classpath dep/ual.jar -sourcepath src $(PACKAGES) -d doc

Bridget.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF Bridget.jar -C bin com/kreative/bridget
	chmod +x Bridget.jar

Bridget-Applet: dep Bridget.jar
	mkdir Bridget-Applet
	cp Bridget.jar Bridget-Applet
	cp dep/Bridget.html Bridget-Applet

Bridget-JWS: dep Bridget.jar
	mkdir Bridget-JWS
	cp Bridget.jar Bridget-JWS
	cp dep/Bridget.gif Bridget-JWS
	cp dep/Bridget.jnlp Bridget-JWS

Bridget.app: Bridget-Pre10.15.app Bridget-MacOS10.15.app Bridget-MacOS11.0.app

Bridget-Pre10.15.app: dep Bridget.jar
	mkdir -p Bridget-Pre10.15.app/Contents/MacOS
	mkdir -p Bridget-Pre10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo Bridget-Pre10.15.app/Contents
	cp -f dep/Info.plist Bridget-Pre10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-Pre10.15 Bridget-Pre10.15.app/Contents/MacOS/Bridget
	cp -f dep/BridgetIcon.icns Bridget-Pre10.15.app/Contents/Resources
	cp -f dep/DocIcon.icns Bridget-Pre10.15.app/Contents/Resources
	cp -f dep/ual.jar Bridget-Pre10.15.app/Contents/Resources/Java
	cp -f Bridget.jar Bridget-Pre10.15.app/Contents/Resources/Java

Bridget-MacOS10.15.app: dep Bridget.jar
	mkdir -p Bridget-MacOS10.15.app/Contents/MacOS
	mkdir -p Bridget-MacOS10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo Bridget-MacOS10.15.app/Contents
	cp -f dep/Info.plist Bridget-MacOS10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS10.15 Bridget-MacOS10.15.app/Contents/MacOS/Bridget
	cp -f dep/BridgetIcon.icns Bridget-MacOS10.15.app/Contents/Resources
	cp -f dep/DocIcon.icns Bridget-MacOS10.15.app/Contents/Resources
	cp -f dep/ual.jar Bridget-MacOS10.15.app/Contents/Resources/Java
	cp -f Bridget.jar Bridget-MacOS10.15.app/Contents/Resources/Java

Bridget-MacOS11.0.app: dep Bridget.jar
	mkdir -p Bridget-MacOS11.0.app/Contents/MacOS
	mkdir -p Bridget-MacOS11.0.app/Contents/Resources/Java
	cp -f dep/PkgInfo Bridget-MacOS11.0.app/Contents
	cp -f dep/Info.plist Bridget-MacOS11.0.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS11.0 Bridget-MacOS11.0.app/Contents/MacOS/Bridget
	cp -f dep/BridgetIcon.icns Bridget-MacOS11.0.app/Contents/Resources
	cp -f dep/DocIcon.icns Bridget-MacOS11.0.app/Contents/Resources
	cp -f dep/ual.jar Bridget-MacOS11.0.app/Contents/Resources/Java
	cp -f Bridget.jar Bridget-MacOS11.0.app/Contents/Resources/Java

Bridget.exe: dep Bridget.jar
ifeq ($(LAUNCH4J_HOME),)
	echo 'Please set $$LAUNCH4J_HOME environment variable to compile exe.'
else
	cd "$(LAUNCH4J_HOME)" ; java -Dlaunch4j.tmpdir=/tmp/ -jar "$(LAUNCH4J_HOME)/launch4j.jar" "$(PWD)/dep/Bridget.xml"
endif

.PHONY: all eclipseall clean eclipseclean Bridget.app
