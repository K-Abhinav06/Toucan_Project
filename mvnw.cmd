@echo off
@setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

if not "%JAVA_HOME%"=="" goto OkJHome

rem Auto-detect JDK 25 / JDK 17
if exist "C:\Program Files\Java\jdk-25.0.2\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
  goto OkJHome
)
if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Java\jdk-17"
  goto OkJHome
)

where java.exe >NUL 2>&1
if %ERRORLEVEL% EQU 0 (
  set MAVEN_JAVA_EXE=java.exe
  goto run
)

echo Error: JAVA_HOME is not set in the environment.
exit /b 1

:OkJHome
set "MAVEN_JAVA_EXE=%JAVA_HOME%\bin\java.exe"

:run
rem Suppress Jansi / Java 21+ restricted native access warnings
if "%MAVEN_OPTS%"=="" (
  set "MAVEN_OPTS=--enable-native-access=ALL-UNNAMED"
) else (
  set "MAVEN_OPTS=%MAVEN_OPTS% --enable-native-access=ALL-UNNAMED"
)

where mvn.cmd >NUL 2>&1
if %ERRORLEVEL% EQU 0 (
  mvn.cmd %*
  exit /b %ERRORLEVEL%
)

where mvn >NUL 2>&1
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
if exist %WRAPPER_JAR% (
  "%MAVEN_JAVA_EXE%" -classpath %WRAPPER_JAR% "-Dmaven.home=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" org.apache.maven.wrapper.MavenWrapperMain %*
  exit /b %ERRORLEVEL%
)

echo Failed to find system Maven or Maven wrapper jar.
exit /b 1
