# Rilasci
- 2.0.08 10/07/2017 Add RES
- 2.0.09 21/07/2017 Add FedUmbria e SPID
- 2.0.10 29/08/2017 Nuovo logo per il RES
- 2.0.11 05/09/2017 Nuova grafica Umbria
- 2.0.12 22/09/2017 Fix security per la RER - logging del remote address IP
- 2.0.13 27/09/2017 Fix header relativamente alla security per la RER - logging del remote address IP
- 2.0.14 13/12/2017 Fix logout per il Trentino e l'Umbria
- 2.0.10.1 05/09/2018 RES: cambiata la mail sulla Home Page
- 2.0.15 16/10/2018 Modifiche per la VDA (logo SPID e altri testi)
- 2.0.16 07/11/2018 Modifiche per l'Umbria (avvisi in HP)
- 2.0.17 23/11/2018 header e footer ER
- 2.0.18 27/11/2018 fix link su ER
- 2.0.19 02/03/2019 rimosso n. agenzia regionale su ER
- 2.0.20 05/07/2019 per TRENTO
- 2.0.21 11/09/2019 per TRENTO
- 2.0.22 11/09/2019 mail sulla home page per l'Umbria
- 2.1.0 14/10/2019 nuovo template per l'Umbria (e la Puglia)
- 2.4.0 06/06/2020 rifacimento front-end ER



# Creazione del pacchetto per TRENTO
Per Trento si usa 
    
    src/main/resources/messages_pat.properties

per cui è necessario ridenominarlo in 

    src/main/resources/messages_it.properties

prima di creare il pacchetto con 

    mvn package

# Istruzione per la configurazione del progetto su Eclipse

Da settembre 2019 molte librerie non sono reperibili sui repository ufficiali per cui le librerie a corredo del progetto sono state inglobate dentro il progetto nella cartella /WEB-INF/lib con lo scope *system*

Requisiti

* siano disponibili sia JDK 7
* sia iinstallato maven 3.5.3 o superiore

Passi da eseguire

- clonare il progetto ed eseguire il checkout al ramo di sviluppo.
- appurare che maven usi jdk 7. Si ottiene col comando: export JAVA_HOME=<home_di_jdk_7>
- eseguire *mvn clean compile package install eclipse:clean eclipse:eclipse -Dwtpversion=2.0*

Se non ci sono errori, si puo' a questo punto importare il progetto in Eclipse

# Informazioni cartella src/main/configuration
I file 
	- cas-log4j.xml
	- cas.properties 
vanno copiati in ${jboss.server.config.dir}

In cas.properties bisogna modificare solo due parametri:
- server.name, 	indicando l'interfaccia locale (che deve essere https) del server;
- host.name, 	che è semplicemente un'etichetta del cluster CAS 



Nelle applicazioni che usano il CAS importare sulla JVM il certificato SSL usato nel connettore SSL.
Il comando e' il seguente:
sudo keytool -import -keystore /etc/ssl/certs/java/cacerts 
		-alias plutone 
		-file cert_plutone_cas.pem

		