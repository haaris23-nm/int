@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __ MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_SAVE_ERRORLEVEL__=
@SET __MVNW_SAVE_ERRORLEVEL__=%ERRORLEVEL%

@SETLOCAL
@SET DIRNAME=%~dp0
@IF "%DIRNAME%"=="" SET DIRNAME=.

@SET MAVEN_PROJECTBASEDIR=%DIRNAME%

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

@IF NOT EXIST %WRAPPER_JAR% (
    ECHO Downloading Maven wrapper...
    powershell -Command "&{[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%WRAPPER_JAR%'}"
)

@SET MAVEN_OPTS=%MAVEN_OPTS% -Xmx512m

FOR /F "usebackq tokens=*" %%i IN (`powershell -Command "&{$p=('%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties' | Get-Content) -match '^distributionUrl='; $url=$p -replace 'distributionUrl=',''; $url.Trim()}"`) DO SET DISTRIBUTION_URL=%%i

FOR /F "usebackq delims=" %%i IN (`powershell -Command "[Environment]::GetFolderPath('UserProfile')"`) DO SET USERPROFILE_DIR=%%i
SET M2_DIR=%USERPROFILE_DIR%\.m2\wrapper\dists

IF NOT EXIST "%M2_DIR%" MKDIR "%M2_DIR%"

java -jar %WRAPPER_JAR% %*
