# Sguardo d'insieme

Il Sistema Informativo Lavoro dell’Agenzia Regionale per il Lavoro della Regione Emilia-Romagna è stato progettato e realizzato con l’obiettivo di attuare la riconversione del processo produttivo dei Servizi per l'Impiego, concepito e organizzato nel passato per produrre atti amministrativi, e oggi invece orientato all'erogazione di servizi ad alto contenuto professionale, integrati con le politiche attive del lavoro, attraverso la promozione e lo sviluppo di interventi di formazione e orientamento, di prevenzione della disoccupazione e di altri azioni mirate per le categorie più deboli e attraverso la riorganizzazione e qualificazione dei servizi per l’impiego decentrati.

Il sistema è stato concepito sulla base delle disposizioni normative ed è costantemente aggiornato e adeguato ai più recenti standard amministrativi (nazionali) e di servizio (regionali). Questo consente agli operatori dei Servizi per l’Impiego di operare su un sistema costantemente aggiornato e fortemente standardizzato che garantisce, anche in fase di monitoraggio, una lettura dei fenomeni del Mercato del lavoro unitaria a livello regionale. Dal punto di vista tecnico, inoltre, il sistema garantisce la coerenza con gli standard e l'interoperabilità con altri sistemi esterni coinvolti.

Il Sistema Informativo Lavoro è composto da diversi strumenti che supportano gli operatori nella gestione degli utenti (lavoratori e aziende). Tale gestione si sviluppa in diversi sottosistemi integrati che agiscono sul fronte amministrativo e di servizio. I due sistemi principali sono il SIL propriamente detto e il Portale LavoroperTe.

Il SIL (Sistema Informativo Lavoro) è uno strumento di supporto per operatori e operatrici dei Centri per l’Impiego nella gestione amministrativa, nell'erogazione delle informazioni e delle politiche attive alle persone e nell’erogazione di servizi alle imprese del proprio territorio.

E’ un sistema modulare in cui si possono logicamente distinguere due componenti applicative strettamente integrate tra loro: la prima dedicata a gestire tutti gli eventi amministrativi, ovvero a presiedere all’applicazione della normativa nazionale e regionale ed alla sua gestione; la seconda componente si occupa della gestione dei servizi erogati dai Centri per l’impiego, quali i servizi di orientamento, di raccolta delle disponibilità e delle competenze dei lavoratori, di raccolta delle offerte di lavoro provenienti dalle aziende e dell’incrocio tra domanda e offerta, e in generale di attivazione di interventi di politica attiva. Viene utilizzato sia per le attività di front-office che per quelle di back-office. Gli automatismi e i controlli del sistema assicurano sicurezza e qualità dei dati, mentre una adeguata parametrizzazione garantisce la necessaria flessibilità verso le diverse possibili impostazioni organizzative dei servizi adottate dalle Province. Sono attivi inoltre scambi informativi con altri sistemi tra cui il sistema regionale della Formazione Professionale

il Portale dei servizi on line per il lavoro, denominato “Lavoro per Te”, si configura come unico «luogo virtuale» dove si raccolgono informazioni e servizi offerti dai diversi canali informatici istituzionali di Regione mettendo a fattore comune esperienze, competenze, informazioni, creando una sinergia che amplifica il raggio di azione e incrementa le potenzialità del servizio
Il portale nasce con l’intento di porsi come strumento informativo e di facilitazione nell’accompagnamento alla ricerca di lavoro e allo sviluppo professionale. 
Si rivolge a persone, aziende e operatori dei servizi, che a vario titolo necessitano di interfacciarsi con i servizi per il lavoro. 
Lavoro per Te è accessibile tramite:

Il Sistema Informativo Lavoro è la rete di applicazioni che offre una gamma di servizi completa rispetto ai processi che sottendono le attività tipiche dei servizi per l’impiego e dei cittadini in ambito del mercato del lavoro.
I servizi sono stati gestiti e implementati in applicazioni dedicate distribuite in base alla tematica trattata.
Ogni modulo eroga dei servizi specifici, comunicano tra di loro tramite lo scambio di informazioni protette e possono colloquiare con l’esterno per l’adempimento di processi amministrativi (cooperazione applicativa).

### Architettura applicativa
Il SIL è costituito da una serie di applicazioni web che girano su **JBoss 7.1.3 su Java 7** con database **Postgres 9+** oppure **EAP 7.2.7 su Java 11** con database **Oracle 12+**.
Ogni applicazione web è un modulo a sè stante ed è autoconsistente nel senso che per la compilazione e l'esecuzione non necessita degli altri moduli alcuni dei quali sono diventano necessari in senso funzionale (MyCas e MyAuthService, che gestiscono l'autenticazione e le autorizzazioni degli utenti sono obbligatori per il corretto funzionamento del sistema).

Nel dettaglio le applicazioni:
  - myaccount
  - myauthservice
  - mycas
  - myportal
  
girano su JBoss 7.1.3 su Java 7 ed usano un DB Postgres i cui script di creazione sono reperibili in **database/postgres**,
mentre le applicazioni
   - sil/sil-war
   - sil/silpub-war
   
 girano su EAP 7.2.7 su Java 11 ed usano un DB Oracle i cui script di creazione sono reperibili in **database/oracle**
 
### Compilazione dei moduli del sistema e creazione dei relativi pacchetti
Si faccia riferimento al **README** pressente in ogni modulo per le istruzioni di compilazione dei sorgenti e per la creazione dei relativi pacchetti.
Relativamente alla compilazione i vari moduli sono indipendenti fra di loro.


 

