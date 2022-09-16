package it.eng.sil.module.consenso;

public interface GConstants {

	public final String CONSENSO_ASSENTE_CODICE = "AS";
	public final String CONSENSO_ASSENTE = "NON PRESENTE";

	public final String CONSENSO_REVOCATO_CODICE = "RE";
	public final String CONSENSO_REVOCATO = "REVOCATO";

	public final String CONSENSO_ATTIVO_CODICE = "AT";
	public final String CONSENSO_ATTIVO = "ATTIVO";

	public final String CONSENSO_NON_DISPONIBILE_CODICE = "ND";
	public final String CONSENSO_NON_DISPONIBILE = "NON DISPONIBILE";

	public final String CONSENSO_SISTEMA_DI_ORIGINE = "SPIL";

	public final String WS_NAME_URL_RICERCA_CONSENSO_LAVORATORE = "RicercaConsensoLavoratore";

	public final String WS_NAME_URL_INSERIMENTO_CONSENSO_LAVORATORE = "InserimentoConsensoLavoratore";

	public final String WS_NAME_URL_REVOCA_CONSENSO_LAVORATORE = "RevocaConsensoLavoratore";

	public final String WS_NAME_URL_FIRMA_GRAFOMETRICA_01_CONVERT_TO_PDF_A = "FGProcessConvertToPdfA";

	public final String WS_NAME_URL_FIRMA_GRAFOMETRICA_02_SIGNATURE_TO_DOC = "FGSetSignatureToDoc";

	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER = "serviceprovider";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER_VALUE = "SPIL";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_DOCUMENTO = "nomedocumento";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_ID_DOCUMENTO = "iddocumento";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO = "tipodocumento";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_HASH_DOCUMENTO = "hashdocumento";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO_VALUE = "SPIL-TIPOLOGIA DOC";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_LAVORATORE = "nomeCittadino";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_COGNOME_LAVORATORE = "cognomeCittadino";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_FISCALE_LAVORATORE = "codicefiscaleCittadino";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_IP_OPERATORE = "ipOperatore";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_OPERATORE = "codiceOperatore";
	public final String WS_FIRMA_GRAFOMETRICA_XML_PARAM_NUM_KLO_DOCUMENTO = "numKloDocumento";

}
