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
echo %timestamp% - ========= Avvio batch Estrazioni movimenti da validare ========= >> %log_err%
echo %timestamp% - >> %log_err%
echo %timestamp% - ====== Variabili di configurazione del sistema ====== >> %log_err%
FOR /F "usebackq delims=" %%i IN (`set`) DO @echo %timestamp% - %%i >> %log_err%
echo %timestamp% - ====== Fine sezione variabili di configurazione ====== >> %log_err%
echo %timestamp% >> %log_err%
echo %timestamp% - ************************************************************ >> %log_err%
echo %timestamp% - *                                                          * >> %log_err%
echo %timestamp% - *        Variabile JBOSS_SERVER_LIB non definita!!!        * >> %log_err%
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

SET libs=%libs%;..\lib\jamon.jar;

REM librerie Jboss

SET libs=%libs%;"%JBOSS_SERVER_LIB%\log4j.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\ojdbc6.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\jsp-api.jar"

REM Parametri necessari per il lancio del batch

SET parameters=-DAF_ROOT_PATH=..\.. -DAF_CONFIG_FILE=/WEB-INF/conf/master_batch_valida_movimenti.xml -DCONTEXT_NAME=sil
SET class_name=it.eng.sil.util.batch.BatchEstraiMovimenti
SET numero_mov_feriale_festivo=500 1000

CALL ..\conf\custom\%hostname%_%context_name%\global_properties.bat

SET parameters=%parameters% -D_ENCRYPTER_KEY_=%_ENCRYPTER_KEY_%

@ECHO ON

%JAVA_HOME%\bin\java -cp .;%libs% %parameters% %class_name% %batch_user% %numero_mov_feriale_festivo%