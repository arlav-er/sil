:: BATCH DEPRECATO, DA ELIMINARE. IN SOSTITUZIONE REALIZZATO TIMER ValidaMovimentiTimer
@ECHO OFF
:: SETTARE DA QUI IL CONTEXT
SET  context_name=sil

:: ============================================
::  Setto a vuoto la variabile
set hostname=
:: ============================================
::  capisco l'hostname abbasso i .bat viva il sano bash scripting 
for /f %%i in ('hostname') do set hostname=%%i
::se non funziona stampa un errore ed esci
if hostname=='' goto nohost


if defined JBOSS_SERVER_LIB goto start
set log_err=..\log\%~n0.log
set timestamp=%date% %time%
echo. > %log_err%
echo. >> %log_err%
echo. >> %log_err%
echo =============================================================== >> %log_err%
echo ====== Application started on %timestamp% >> %log_err%
echo =============================================================== >> %log_err%
echo %timestamp% - ========= Avvio batch Movimenti Giornalieri ========= >> %log_err%
echo %timestamp% - >> %log_err%
echo %timestamp% - ====== Variabili di configurazione del sistema ====== >> %log_err%
FOR /F "usebackq delims=" %%i IN (`set`) DO @echo %timestamp% - %%i >> %log_err%
echo %timestamp% - ====== Fine sezione variabili di configurazione ====== >> %log_err%
echo %timestamp% >> %log_err%
echo %timestamp% - ************************************************************ >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - *          Variabile JBOSS_SERVER_LIB non definita!!!        * >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - *        Contattare l'amministratore di sistema.           * >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - ************************************************************ >> %log_err%
echo %timestamp% >> %log_err%
echo %timestamp% >> %log_err%
exit 1
:nohost
echo %timestamp% - ************************************************************ >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - *      Impossibile estrarre automaticamente l'hostname     * >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - *             Modificare manualmente lo script.             * >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - ************************************************************ >> %log_err%
exit 1

:start

REM Path delle librerie nella webapp

SET libs=%libs%;..\lib\IRCoop.jar;
SET libs=%libs%;..\lib\SilCoop.jar;
SET libs=%libs%;..\lib\Coop-apis.jar;
SET libs=%libs%;..\lib\commons-fileupload-1.2.1.jar;
SET libs=%libs%;..\lib\commons-io-1.3.2.jar;
SET libs=%libs%;..\lib\commons-lang-2.5.jar;
SET libs=%libs%;..\lib\fckeditor-java-core-2.6.jar;
SET libs=%libs%;..\lib\itextpdf-5.5.9.jar;
SET libs=%libs%;..\lib\jamon.jar;
SET libs=%libs%;..\lib\jdbf.jar;
SET libs=%libs%;..\lib\json.jar;
SET libs=%libs%;..\lib\jspSmartUpload.jar;
SET libs=%libs%;..\lib\junit.jar;
SET libs=%libs%;..\lib\poi-3.5-FINAL-20090928.jar;
SET libs=%libs%;..\lib\poi-contrib-3.5-FINAL-20090928.jar;
SET libs=%libs%;..\lib\poi-ooxml-3.5-FINAL-20090928.jar;
SET libs=%libs%;..\lib\poi-scratchpad-3.5-FINAL-20090928.jar;
SET libs=%libs%;..\lib\slf4j-api-1.5.8.jar;
SET libs=%libs%;..\lib\slf4j-simple-1.5.8.jar;
SET libs=%libs%;..\lib\xmlworker-5.5.9.jar;

REM librerie Jboss

SET libs=%libs%;"%JBOSS_SERVER_LIB%\log4j.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\ojdbc6.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\servlet-api.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\jsp-api.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\CrystalClear_v6.jar"



REM Parametri necessari per il lancio del batch

SET parameters=-DAF_ROOT_PATH=..\.. -DAF_CONFIG_FILE=/WEB-INF/conf/master_batch_valida_movimenti.xml -DCONTEXT_NAME=%context_name%
SET class_name=it.eng.sil.util.batch.BatchValidaMovimenti
SET batch_launcher=it.eng.sil.util.batch.BatchLauncher

CALL ..\conf\custom\%hostname%_%context_name%\global_properties.bat

SET parameters=%parameters% -D_ENCRYPTER_KEY_=%_ENCRYPTER_KEY_%

CALL ..\conf\custom\%hostname%_%context_name%\validazione_movimenti_properties.bat %1%
@ECHO ON

%JAVA_HOME%\bin\java -classpath .;%libs% %parameters% %class_name% %batch_user% 

