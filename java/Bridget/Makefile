all: clean osxclean Bridget.jar Bridget-Applet Bridget-JWS Bridget.app Bridget.exe

clean:
	rm -rf Bridget.jar
	rm -rf Bridget-Applet
	rm -rf Bridget-JWS
	rm -rf Bridget.app
	rm -rf Bridget.exe

osxclean:
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

Bridget.jar: osxclean
	jar cmf dep/MANIFEST.MF Bridget.jar -C bin com/kreative/bridget

Bridget-Applet: Bridget.jar
	mkdir Bridget-Applet
	cp Bridget.jar Bridget-Applet
	cp dep/Bridget.html Bridget-Applet

Bridget-JWS: Bridget.jar
	mkdir Bridget-JWS
	cp Bridget.jar Bridget-JWS
	cp dep/Bridget.gif Bridget-JWS
	cp dep/Bridget.jnlp Bridget-JWS

Bridget.app: Bridget.jar
	mkdir Bridget.app
	mkdir Bridget.app/Contents
	mkdir Bridget.app/Contents/MacOS
	mkdir Bridget.app/Contents/Resources
	mkdir Bridget.app/Contents/Resources/Java
	cp dep/Info.plist Bridget.app/Contents
	cp dep/JavaApplicationStub Bridget.app/Contents/MacOS/Bridget
	cp dep/PkgInfo Bridget.app/Contents
	cp dep/BridgetIcon.icns Bridget.app/Contents/Resources
	cp dep/DocIcon.icns Bridget.app/Contents/Resources
	cp Bridget.jar Bridget.app/Contents/Resources/Java

Bridget.exe: Bridget.jar
ifeq ($(LAUNCH4J_HOME),)
	echo 'Please set $$LAUNCH4J_HOME environment variable to compile exe.'
else
	cd "$(LAUNCH4J_HOME)" ; java -Dlaunch4j.tmpdir=/tmp/ -jar "$(LAUNCH4J_HOME)/launch4j.jar" "$(PWD)/dep/Bridget.xml"
endif

.PHONY: all clean osxclean
