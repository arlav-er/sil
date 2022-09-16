/*
 * Created on 8-ott-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasferimentoRamoAz;

/**
 * @author mancinid
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface TrasferimentoRamoAzXmlConst {

	public static final String PREFIX = "";
	public static final String SUFFIX_AZ_ATTUALE = "AzAttuale";
	public static final String SUFFIX_AZ_PRECEDENTE = "AzPrecedente";

	public static final String VARDATORI = "VarDatori";
	public static final String DATA_COMUNICAZIONE = "dataInvio";
	public static final String TIPO_DELEGATO = "tipoDelegato";
	public static final String DELEGATO = "delegato";
	public static final String CODICE_COMUNICAZIONE = "codiceComunicazione";
	public static final String CODICE_COMUNICAZIONE_PREC = "codiceComunicazionePrec";

	public static final String DATORE_ATTUALE = PREFIX + "DatoreAttuale";
	public static final String DATORE_PRECEDENTE = PREFIX + "DatorePrecedente";
	public static final String CODICE_FISCALE_DATORE = "codiceFiscale";
	public static final String RAGIONE_SOCIALE_DATORE = "denominazione";

	public static final String SETTORE = PREFIX + "Settore";

	public static final String TIPO_COMUNICAZIONE = PREFIX + "TipoComunicazione";

	public static final String TRASFERIMENTO_AZIENDA = PREFIX + "TrasferimentoAzienda";
	public static final String CODICE_TRASFERIMENTO = "codiceTrasferimento";
	public static final String DATA_INIZIO_TRASFERIMENTO = "dataInizio";

	public static final String SEDE_LAVORO = PREFIX + "SedeLavoro";

	public static final String RECAPITI = PREFIX + "Recapiti";

	public static final String LAVORATORI = PREFIX + "Lavoratori";

	public static final String CONTRATTO = PREFIX + "Contratto";

	public static final String DATI_LAVORATORE = PREFIX + "DatiLavoratore";
	// public static final String CODICE_FISCALE_LAVORATORE = PREFIX + "codice-fiscale";

	public static final String ELEMENTI_SEDE[] = { PREFIX + "Comune", PREFIX + "Indirizzo", PREFIX + "cap" };

	public static final String ELEMENTI_RECAPITI[] = { PREFIX + "e-mail", PREFIX + "fax", PREFIX + "telefono" };

	public static final String ELEMENTI_DATILAVORATORE[] = { PREFIX + "cognome", PREFIX + "nome",
			PREFIX + "codice-fiscale", PREFIX + "comuneDomicilio" };

	public static final String ELEMENTI_CONTRATTO[] = { PREFIX + "agevolazioni", PREFIX + "ccnl",
			PREFIX + "livelloInquadramento", PREFIX + "legge68", PREFIX + "tipoOrario",
			PREFIX + "qualificaProfessionale", PREFIX + "RetribuzioneCompenso" };

	public static final String ATTRIBUTI_CONTRATTO[] = { "TipologiaContrattuale", "patINAIL", "entePrevidenziale",
			"dataFine", "tipoLavorazione", "codiceEntePrevidenziale", "dataInizio", "ggLavorativePreviste",
			"socioLavoratore" };

	public static final String ATTRIBUTI_CONTRATTO_LEGGE68[] = { "dataNullaOsta", "numeroAtto" };

	public static final String ATTRIBUTI_CONTRATTO_TIPOORARIO[] = { "codice", "oreSettimanaliMedie" };

	public static final String VALORI_APPRENDISTATO[] = { "A.03.00", "A.03.01", "A.03.02", "A.03.03" };
	public static final String MITTENTE_SARE = "MittenteSare";
}
