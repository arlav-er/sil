package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public enum ConferimentoDIDEsitoEnum {
E001("Parametri in input non coerenti"),
E002("SAP non presente"),
E003("Esiste una DID attiva"),
E004("Ente promotore non è titolare della SAP"),
E005("Esiste un rapporto di lavoro attivo"),
E006("Evento non trasmesso nei tempi amministrativi previsti"),
E007("Data DID non presente"),
E008("Data DID inviata non è coerente con quella in NCN"),
E009("Evento DID non coerente con ultimo stato DID"),
E010("Presente più di un patto di servizio con data inizio = data DID"),
E011("Patto di servizio attivo presente"),
E012("Presenti più patti di servizio attivi"),
E013("Patto di servizio attivo per il CPI di convalida non presente"),
E014("Per la data DID non è presente alcun patto di servizio attivo"),
E015("Data evento non coerente con la data fine del patto di servizio"),
E016("Operazione non effettuata da regione competenza"),
E018("Data evento non coerente con la data inizio indicata nel patto di servizio"),
E019("Data evento minore della data DID"),
E020("Non è possibile effettuare l'annullamento"),
E021("Attendere SAP in corso di elaborazione"),
E022("La data evento non può essere inferiore alla data inizio della politica A02"),
E023("La data evento non può essere superiore alla data corrente"),
E024("Non deve esistere un patto di servizio aperto con codice CPI diverso da quello indicato"),
E025("La data evento non può essere inferiore alla data proposta della politica A02"),
E026("Codice CPI non valido"),
E100("OK"),
E997("Errore scrittura su log"),
E998("Errore creazione SAP"),
E999("Errore generico"),
E101("Codice fiscale non valido"),
E102("Età inferiore a 15 anni"),
E900("Errore nel calcolo del profiling"),
E103("Nessun risultato trovato"),
E105("Codice fiscale non corrispondente al genere dichiarato"),
E106("Codice fiscale non corrispondente all'età dichiarata");

private String descrizione;

private ConferimentoDIDEsitoEnum(String descrizione) {
	this.descrizione = descrizione;
}

public String getDescrizione() {
	return descrizione;
}
}
