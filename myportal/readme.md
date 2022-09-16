# MYPORTAL
Il portale nasce come centro servizi al cittadino per l'agenzia lavoro


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
