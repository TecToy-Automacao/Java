@ECHO OFF

SET DIR_ATUAL=%CD%
ECHO  ** Diretorio atual: %DIR_ATUAL%
ECHO ** JAVA_HOME = %JAVA_HOME%

:find_javac
SET JAVAC=javac.exe
where javac.exe /q
if %ERRORLEVEL% EQU 0 goto :find_jar
SET JAVAC=%JAVA_HOME%\bin\javac.exe
if NOT EXIST "%JAVAC%" goto :erro_javac

:find_jar
ECHO ** javac.exe = %JAVAC%
SET JAR=jar.exe
where jar.exe /q
if %ERRORLEVEL% EQU 0 goto :find_sdk
SET JAR=%JAVA_HOME%\bin\jar.exe
if NOT EXIST "%JAR%" goto :erro_jar

:find_sdk
ECHO ** jar.exe = %JAR%
SET SDK_DIR=%ANDROID_HOME%
if "%SDK_DIR%" == "" SET SDK_DIR=%ANDROID_SDK_ROOT%
if "%SDK_DIR%" == "" SET SDK_DIR=C:\Users\%USERNAME%\AppData\Local\Android\sdk
if NOT EXIST "%SDK_DIR%\" SET SDK_DIR=
if "%SDK_DIR%" == "" goto :erro_sdk
ECHO ** SDK Android em: %SDK_DIR%

:find_android_jar
SET ANDROID_JAR=%SDK_DIR%\platforms\android-32\android.jar
if NOT EXIST "%ANDROID_JAR%" goto erro_android_jar

:clean
ECHO ** Removendo compilaçoes anteriores
rmdir /s/q com\
rmdir /s/q woyou\

:compile
ECHO ** Compilando 
%JAVAC% -verbose -cp %ANDROID_JAR% -d . ICallback.java IWoyouService.java Printer.java
if %ERRORLEVEL% GTR 0 goto :erro_compile
ECHO ** Gerando JAR
%JAR% cvf sunmi_printer.jar com\acbr\sunmi_printer\*.class woyou\aidlservice\jiuiv5\*.class
if %ERRORLEVEL% GTR 0 goto :erro_compile
if NOT EXIST "sunmi_printer.jar" goto erro_compile
ECHO.
ECHO ***** SUCESSO ******
ECHO Arquivo "sunmi_printer.jar" gerado 
goto :fim

:erro_compile
ECHO ***** Erro na Compilação *****
goto :fim_erro

:erro_javac
ECHO ***** Compilador Java não encontrado *****
ECHO.
ECHO A variável de ambiente "JAVA_HOME" aponta para: %JAVA_HOME% 
ECHO Mas não existe o arquivo "bin\javac.exe", nessa pasta... :(
goto :fim_erro

:erro_jar
ECHO ***** Compactador JAR não encontrado *****
ECHO.
ECHO A variável de ambiente "JAVA_HOME" aponta para: %JAVA_HOME% 
ECHO Mas não existe o arquivo "bin\jar.exe", nessa pasta... :(
goto :fim_erro

:erro_sdk
ECHO ***** SDK Android não encontrado *****
ECHO.
ECHO Verifique se o SDK Android foi corretamente instalado
ECHO Voce deve definir "ANDROID_HOME" em: Painel de Controle / Avancado / Variaveis de Ambiente
goto fim_erro

:erro_android_jar
ECHO ***** android.jar não encontrado *****
ECHO.
ECHO O SDK Android foi encontrado em: %SDK_DIR% 
ECHO Mas não existe o arquivo "platforms\android-32\android.jar", nessa pasta... :(
goto :fim_erro

:fim_erro
ECHO.
ECHO ***************** ABORTADO COM ERROS *******************

:fim
ECHO.
cd %DIR_ATUAL%