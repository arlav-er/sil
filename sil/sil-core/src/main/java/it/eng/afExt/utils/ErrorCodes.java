package it.eng.afExt.utils;

/**
 * @author Finessi_M
 * 
 *         Classe contenitore dei codici di errore che si trovano nel file ...WEB-INF\classes\messages_it_IT.properties
 */
public class ErrorCodes {

	/**
	 * +++++++++++++++++++++++++++++++++++++++ +++ NON USATA: usare "MessageCodes" +++
	 * +++++++++++++++++++++++++++++++++++++++
	 */

	/*
	 * //valore da aggiungere ai codici di errore SQL del DB specifico public static final int DB_ERROR_CODE_BASE =
	 * 30000;
	 * 
	 * 
	 * 
	 * //ERRORI APPLICATIVI public static final int NO_USER_ID_PROVIDED = 20001; public static final int
	 * NO_PASSWORD_PROVIDED = 20002; public static final int AUTENTICAZIONE_FALLITA = 20003; public static final int
	 * NO_COD_FISC = 20004; public static final int NO_CCIAA_REA = 20005;
	 * 
	 * //ERRORI APPLICATIVI NEL SALVATAGGIO DELLA VISURA public static final int NO_COD_FISC_VIS = 20006; public static
	 * final int NO_NUM_REA_VIS = 20007; public static final int NO_PROV_REA_VIS = 20008; public static final int
	 * NO_RAGIONE_SOCIALE_VIS = 20009; public static final int NO_FORMA_GIURIDICA_VIS = 20010; public static final int
	 * CONN_INFO_FALLITA = 20011; public static final int NO_AZI_FOUND = 20012; public static final int
	 * COD_FISC_DIFFERENT = 20013;
	 * 
	 * //ERRORI USATI DALLA FUNZIONE checkFields di MyDefaultDetailModule public static final int CAMPO_OBBLIGATORIO =
	 * 20014; public static final int DATA_NON_VALIDA = 20015; public static final int NUMERO_NON_VALIDO = 20016; public
	 * static final int PIVA_NON_VALIDA = 20017; public static final int CF_NON_VALIDO = 20018; public static final int
	 * CAMPO_TROPPO_LUNGO = 20019; public static final int FORMATO_NON_VALIDO = 20020; public static final int
	 * DECIMALE_NON_VALIDO = 20037;
	 * 
	 * //ERRORI NELLA VALIDAZIONE AZIENDA public static final int AZ_NO_ISCRITTA_CAA = 20021; public static final int
	 * VALID_DATO_OBBLIGATORIO = 20022; public static final int VALID_DATO_NO_CORRETTO = 20023; public static final int
	 * VALID_DATI_ANAGRAFICI = 20024; public static final int VALID_COESISTENZA = 20025; public static final int
	 * DATI_ITALIANI_OBBLIGATORI = 20026; public static final int NO_TITOLARE_RAPPR = 20027; public static final int
	 * NO_PERS_CORRENTE = 20028; public static final int DATO_OBBLIGATORIO_PERS = 20029; public static final int
	 * DATO_NO_CORRETTO_PERS = 20030; public static final int VALID_DATI_ANAGRAFICI_PERS = 20031; public static final
	 * int VALID_COESISTENZA_PERS = 20032; public static final int DATI_ITALIANI_OBBLIGATORI_PERS = 20033; public static
	 * final int VALID_DATI_ANAGRAFICI_PERS_NASC = 20034; public static final int DATI_ITALIANI_OBBLIGATORI_NASC =
	 * 20035; public static final int VALIDAZIONE_OK = 20036;
	 * 
	 * //ERRORI APPLICATIVI public static final int NO_PROV_CORRETTA = 20038; public static final int NO_COM_CORRETTO =
	 * 20039; public static final int NO_COESISTENZA = 20040; public static final int SEDE_PRINCIPALE = 20041; public
	 * static final int SEDE_PRINCIPALE_ESISTE = 20042; public static final int DATA_INIZIO_UGUALE_FINE = 20043; public
	 * static final int DATA_INIZIO_MAGGIORE_FINE = 20044; public static final int NO_STATO_CORRETTO = 20045; public
	 * static final int DT_CESSAZIONE_NON_VALIDA = 20058; // ERRORI DB public static final int FK_VIOLATED =
	 * DB_ERROR_CODE_BASE + 2292; //(2292 è specifico di Oracle)
	 * 
	 * //ERRORI NELL'INSERIMENTO EVENTI public static final int MIN_NUM_PART_ACCORP = 20046; public static final int
	 * NO_PART_FRA = 20047; public static final int PART_DATO_OBBLIGATORIO = 20048; public static final int
	 * PART_DATO_NN_VALIDO = 20049; public static final int PART_PROV_NN_VALIDA = 20050; public static final int
	 * PART_COM_NN_VALIDO = 20051; public static final int PART_NN_TROVATA = 20052; public static final int PART_CESSATA
	 * = 20053; public static final int PART_NO_FONTE = 20054; public static final int PART_NO_SUP = 20055; public
	 * static final int PART_SUP_ZERO = 20056; public static final int SUP_SUPERA = 20057; public static final int
	 * MODIFICA_PARTICELLA = 20058; public static final int NO_PARTICELLA = 20059; public static final int
	 * EVENTO_INSERITO = 20060; public static final int PARTICELLA_CESSATA = 20061; public static final int
	 * EVENTO_ACCORPAMENTO = 20062; public static final int INSERIMENTO_PARTICELLA = 20063; public static final int
	 * DECURTAZIONE_PARTICELLA = 20064; public static final int EVENTO_FRAZIONAMENTO = 20065; public static final int
	 * MODIFICA_POSSESSO = 20066; public static final int INSERIMENTO_POSSESSO = 20067; public static final int
	 * PART_CESSATA_DATA = 20068; public static final int PARTICELLA_NON_TROVATA = 20069; public static final int
	 * ELIMINA_POSSESSO = 20070; public static final int AREE_PRESENTI = 20071;
	 * 
	 * //ERRORI GENERICI public static final int ERRORE_MODULO = 30000; public static final int ERRORE_QUERY = 30001;
	 * 
	 * //CODICI SIL - da modificare quando sarà decisa l'adozione del file di errori public static final int
	 * SIL_DATI_SALVATI = 90001; public static final int SIL_DATI_NON_SALVATI=90002;
	 * 
	 * 
	 * //--------------------------- Metodi statici ----------------------------------------- / ** @return il codice di
	 * errore ottenuto sommando DB_ERROR_CODE_BASE al codice di errore nativo del DB se l'EMFInternalError passato come
	 * parametro incapsula una SQLException. @return -1 se l'EMFInternalError passato come parametro NON incapsula una
	 * SQLException. / public static int getDBErrorCode(EMFInternalError error){ int toRet=-1; if(error!=null){
	 * Exception e = error.getNativeException(); if (e instanceof SQLException) { SQLException sqlEx = (SQLException) e;
	 * toRet = DB_ERROR_CODE_BASE + sqlEx.getErrorCode(); } } return toRet; }
	 * 
	 */

}
