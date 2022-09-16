# GESTIONE ACCOUNT
Modulo per la gestione degli account utente, della profilatura e delle pagine di visualizzazione profilo.
Contiene le pagine profilo per ciascun tipo di utente, alcune pagine relative alla gestione dei permessi da
assegnare agli utenti (accreditamento forte per i cittadini, abilitazione SARE per le aziende...) ed il
pannello di amministrazione della profilatura.


# PREREQUISITI
Per poter compilare ed effettuare il deploy del modulo sono necessarie:
- Java 7;
- Maven 3.6.3;
- Jboss 7.1.3.


# INSTALLAZIONE
Il modulo si serve di alcune librerie interne, per poter effettuare la compilazione è necessario caricare su un repository Nexus i jar contenuti nella folder external_lib.

Dopo di che per la compilazione è necessario eseguire il comando maven: 

**mvn clean package**

L'artefatto prodotto va pubblicato nella folder di deployments di Jboss 7.1.3.
