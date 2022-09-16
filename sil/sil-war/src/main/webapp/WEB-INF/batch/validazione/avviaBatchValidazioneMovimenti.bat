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
echo %timestamp% - ========= Avvio batch movimenti da validare ========= >> %log_err%
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

set nome_JBOSS_config=SIL_20
set PORTA_JNDI=21099
set DESCRIZIONE_SERVIZIO=JBoss_silValidazioneClient_20k


REM Path delle librerie nella webapp

SET libs=..\lib\silValidazioneClient.jar
SET libs=%libs%;..\lib\silValidazioneBean.jar


REM librerie Jboss

SET libs=%libs%;"%JBOSS_SERVER_LIB%\log4j.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\ojdbc6.jar"
SET libs=%libs%;"%JBOSS_SERVER_LIB%\jsp-api.jar"

set libs=%libs%;"%JBOSS_SERVER_LIB%\..\..\..\client\jbossall-client.jar"
set JAVA_DLL=%JAVA_HOME%\jre\bin\client\jvm.dll

CALL ..\conf\custom\%hostname%_%context_name%\validazione_movimenti_properties.bat

set ctx=-Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory 
set ctx=%ctx%-Djava.naming.provider.url=jnp://%COMPUTERNAME%:%PORTA_JNDI% 
set ctx=%ctx%-Djava.naming.factory.url.pkgs=org.jnp.interfaces.NamingContextFactory 
set ctx=%ctx%-D_ENCRYPTER_KEY_=%_ENCRYPTER_KEY_%

@echo on

"%JAVA_HOME%"\bin\java.exe %ctx% -cp %libs% it.eng.sil.batch.ValidazioneBatch %batch_user%