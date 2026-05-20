@echo off

REM ** On 64 bit Windows we need to run the 32 bit Java because there is no
REM ** 64 bit library included in the Bluecove stack so far...

IF %PROCESSOR_ARCHITECTURE% == AMD64 GOTO ONSIXTYFOUR
"C:\Program Files\java\jre6\bin\java" -cp nokicert.jar;win32\swt.jar;bluecove.jar net.tuxed.nokicert.NokiCertGUI
GOTO END
:ONSIXTYFOUR
"C:\Program Files (x86)\Java\jre6\bin\java" -cp nokicert.jar;win32\swt.jar;bluecove.jar net.tuxed.nokicert.NokiCertGUI
:END

