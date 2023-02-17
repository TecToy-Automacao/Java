@ECHO OFF

:: **** NOTA ****
:: Você pode definir os Path nas variveis abaixo, 
:: ou se deixar em branco o Script irá tentar encontra-las, em seu Disco
::
:: SET SDK_DIR=C:\Users\Public\Documents\Embarcadero\Studio\20.0\CatalogRepository\AndroidSDK-2525_20.0.36039.7899\
:: SET PLATAFORM=32
SET PLATAFORM=25
SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.362.9-hotspot"

SET DIR_ATUAL=%CD%
ECHO ** Diretorio atual: %DIR_ATUAL%
ECHO ** JAVA_HOME = %JAVA_HOME%

:check_javac
if "%JAVA_HOME%" == "" goto :find_javac
SET JAVAC="%JAVA_HOME%\bin\javac.exe"
ECHO   JAVAC = %JAVAC%
if EXIST %JAVAC% goto :javac_ok

:find_javac
where javac.exe /q
if %ERRORLEVEL% GTR 0 goto :erro_javac
SET JAVAC=javac.exe

:javac_ok
ECHO ** javac.exe = %JAVAC%

:check_jar
if "%JAVA_HOME%" == "" goto :find_jar
SET JAR="%JAVA_HOME%\bin\jar.exe"
ECHO   JAR = %JAR%
if EXIST %JAR% goto :jar_ok

:find_jar
where jar.exe /q
if %ERRORLEVEL% GTR 0 goto :erro_jar
SET JAR=jar.exe

:jar_ok
ECHO ** jar.exe = %JAR%

:find_sdk
if EXIST "%SDK_DIR%" goto :sdk_ok
ECHO ** Procurando SDK_DIR
SET SDK_DIR=%ANDROID_HOME%
if "%SDK_DIR%" == "" SET SDK_DIR=%ANDROID_SDK_ROOT%
if "%SDK_DIR%" == "" SET SDK_DIR=C:\Users\%USERNAME%\AppData\Local\Android\sdk
if NOT EXIST "%SDK_DIR%\" SET SDK_DIR=
if "%SDK_DIR%" == "" goto :erro_sdk

:sdk_ok
ECHO ** SDK Android em: %SDK_DIR%

:find_android_jar
if "%PLATAFORM%" == "" SET PLATAFORM=32
SET ANDROID_JAR=%SDK_DIR%\platforms\android-%PLATAFORM%\android.jar
if NOT EXIST "%ANDROID_JAR%" goto erro_android_jar

:android_jar_ok
ECHO ** android.jar encontrado em: %ANDROID_JAR%

::exit

:clean
ECHO ** Removendo compilaçoes anteriores
if EXIST "tectoy.jar" del tectoy.jar
if EXIST "com\" rmdir /s/q com\
if EXIST "woyou\" rmdir /s/q woyou\

:compile
ECHO ** Compilando 
%JAVAC% -verbose -cp %ANDROID_JAR% -d . SecondScreen.java ICallback.java IWoyouService.java SunmiPrinter.java IStateLamp.java Leds.java play\PlayVideo.java play\BasePresentation.java play\BasePresentationHelper.java play\ScreenManager.java play\SharePreferenceUtil.java play\utils\MPlayer.java play\utils\MPlayerException.java play\utils\IMDisplay.java play\utils\IMPlayer.java play\utils\IMPlayListener.java play\utils\MinimalDisplay.java VideoPlayer.java
if %ERRORLEVEL% GTR 0 goto :erro_compile

ECHO ** Gerando JAR
%JAR% cvf tectoy.jar com\tectoy\secondDisplay\*.class com\tectoy\sunmiprinter\*.class  com\tectoy\leds\*.class woyou\aidlservice\jiuiv5\*.class com\tectoy\play\*.class com\tectoy\play\utils\*.class
if %ERRORLEVEL% GTR 0 goto :erro_compile
if NOT EXIST "tectoy.jar" goto erro_compile
ECHO.
ECHO ***** SUCESSO ******
ECHO Arquivo "tectoy.jar" gerado 
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
ECHO Mas não existe o arquivo "platforms\android-%PLATAFORM%\android.jar", nessa pasta... :(
goto :fim_erro

:fim_erro
ECHO.
ECHO ***************** ABORTADO COM ERROS *******************

:fim
ECHO.
cd %DIR_ATUAL%