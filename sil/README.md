# SIL (SISTEMA INFORMATIVO LAVORO)
Il SIL ha lo scopo di automatizzare le procedure rivolte alla rilevazione, l'elaborazione e la diffusione dei dati in tema di collocamento 
e di politiche attive per l'occupazione. 
Nasce per supportare da una parte i Centri per l'impiego e dall'altra i lavoratori, operatori e imprese.


# PREREQUISITI
Per poter compilare ed effettuare il deploy del modulo sono necessarie:
- Java 11;
- Maven 3.6.3;
- EAP 7.2.7;


# INSTALLAZIONE
Il modulo si serve di alcune librerie interne, per poter effettuare la compilazione è necessario caricare su un repository Nexus i jar contenuti nella folder external_lib.

Dopo di che per la compilazione è necessario eseguire il comando maven: 

**mvn clean package**

L'artefatto prodotto va pubblicato nella folder di deployments di EAP 7.2.7.
