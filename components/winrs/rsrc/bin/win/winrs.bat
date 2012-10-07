@REM Copyright (c) 2011 jOVAL.org.  All rights reserved.
@REM This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt
@ECHO OFF

SET INSTALL_DIR=%~dp0
SET LIB=%INSTALL_DIR%lib
IF NOT DEFINED JAVA_HOME (
    echo "JAVA_HOME environment variable must be set to run winrs."
) ELSE (
    "%JAVA_HOME%\bin\java.exe" -cp "%LIB%\*;" jwsmv.winrs.Client %*
)
