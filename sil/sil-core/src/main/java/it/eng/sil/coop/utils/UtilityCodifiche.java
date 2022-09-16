package it.eng.sil.coop.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

public class UtilityCodifiche {

	private static Map<String, String> MAP_CODRAPPORTOLAV_TO_CODCONTRATTO = new HashMap<String, String>();
	private static BigDecimal LIVELLO_CONOSCENZA_LINGUA_NON_NOTO = new BigDecimal(100000);
	static {
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A00", "LP");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A01", "LT");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A02", "AP");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A03", "TI");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A04", "LA");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A05", "PI");
		MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.put("A06", "CO");
	}

	public static String[] getConoscenzaInformatica(TransactionQueryExecutor transExec, String descrizioneMinisteriale)
			throws EMFInternalError {

		String[] conoscenzaInformatica = null;
		String codTipoInfo = null;
		String codDettInfo = null;

		Object[] inputParameters = new Object[1];
		inputParameters[0] = descrizioneMinisteriale;

		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_SELECT_CODTIPOINFO_CODDETTINFO", inputParameters,
				"SELECT");

		if (sb != null) {
			codTipoInfo = sb.containsAttribute("ROW") ? (String) sb.getAttribute("ROW.CODTIPOINFO")
					: (String) sb.getAttribute("CODTIPOINFO");
			codDettInfo = sb.containsAttribute("ROW") ? (String) sb.getAttribute("ROW.CODDETTINFO")
					: (String) sb.getAttribute("CODDETTINFO");

			if (codTipoInfo == null || codDettInfo == null) {
				return null;
			}

			conoscenzaInformatica = new String[2];
			conoscenzaInformatica[0] = codTipoInfo;
			conoscenzaInformatica[1] = codDettInfo;
		}

		return conoscenzaInformatica;

	}

	public static String getCodCorso(TransactionQueryExecutor transExec, String descrizioneMinisteriale)
			throws EMFInternalError {

		String codCorso = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = descrizioneMinisteriale;

		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_SELECT_CODCORSO", inputParameters, "SELECT");
		codCorso = sb.containsAttribute("ROW") ? (String) sb.getAttribute("ROW.CODCORSO")
				: (String) sb.getAttribute("CODCORSO");

		return codCorso;

	}

	public static String getCodTipoCertificato(TransactionQueryExecutor transExec, String codiceMinisteriale)
			throws EMFInternalError {

		if (codiceMinisteriale == null) {
			return null;
		}

		String codTipoCertificato = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceMinisteriale;
		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_SELECT_CODTIPOCERTIFICATO", inputParameters,
				"SELECT");
		if (sb != null) {
			codTipoCertificato = sb.containsAttribute("ROW") ? (String) sb.getAttribute("ROW.CODTIPOCERTIFICATO")
					: (String) sb.getAttribute("CODTIPOCERTIFICATO");
		}
		return codTipoCertificato;

	}

	public static BigDecimal getPrgMansioneLavoratore(TransactionQueryExecutor transExec, BigDecimal cdnlavoratore,
			String codmansione) throws EMFInternalError {
		BigDecimal prgMansione = null;
		Object[] inputParameters = new Object[2];
		inputParameters[0] = cdnlavoratore;
		inputParameters[1] = codmansione;
		SourceBean mansione = (SourceBean) transExec.executeQuery("SELECT_PRGMANSIONE_DA_CODMANSIONE_E_LAVORATORE",
				inputParameters, "SELECT");
		if (mansione != null) {
			prgMansione = mansione.containsAttribute("ROW") ? (BigDecimal) mansione.getAttribute("ROW.PRGMANSIONE")
					: (BigDecimal) mansione.getAttribute("PRGMANSIONE");
		}
		return prgMansione;
	}

	public static String getCodOrarioSil(TransactionQueryExecutor transExec, String idmodalitalavoro)
			throws EMFInternalError {
		// VEDI DE_ORARIO
		if (idmodalitalavoro != null && !"".equals(idmodalitalavoro)) {
			if ("PT".equalsIgnoreCase(idmodalitalavoro)) {
				return "P";
			}
			if ("FT".equalsIgnoreCase(idmodalitalavoro)) {
				return "F";
			}
		}
		return null;
	}

	public static String getMansioneSil(TransactionQueryExecutor transExec, String codice) throws EMFInternalError {
		String codiceMansione = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean mansione = (SourceBean) transExec.executeQuery("SELECT_MANSIONE_SIL_DA_MANSIONE_MIN",
				inputParameters, "SELECT");
		if (mansione != null) {
			codiceMansione = mansione.containsAttribute("ROW") ? (String) mansione.getAttribute("ROW.CODMANSIONE")
					: (String) mansione.getAttribute("CODMANSIONE");
		}
		return codiceMansione;
	}

	public static String getAbilitazioneSil(TransactionQueryExecutor transExec, String codice) throws EMFInternalError {
		String codAbilitazioneGen = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean abilitaz = (SourceBean) transExec.executeQuery("SELECT_ABILIT_SIL_DA_ABILIT_MIN", inputParameters,
				"SELECT");
		if (abilitaz != null) {
			codAbilitazioneGen = abilitaz.containsAttribute("ROW")
					? (String) abilitaz.getAttribute("ROW.CODABILITAZIONEGEN")
					: (String) abilitaz.getAttribute("CODABILITAZIONEGEN");
		}
		return codAbilitazioneGen;
	}

	public static String getTitoloStudioSil(TransactionQueryExecutor transExec, String codice) throws EMFInternalError {
		String codiceTitolo = null;
		Object[] inputParameters = new Object[3];
		inputParameters[0] = codice;
		inputParameters[1] = codice;
		inputParameters[2] = codice;
		SourceBean titolo = (SourceBean) transExec.executeQuery("SELECT_TITOLO_SIL_DA_TITOLO_MIN", inputParameters,
				"SELECT");
		if (titolo != null) {
			codiceTitolo = titolo.containsAttribute("ROW") ? (String) titolo.getAttribute("ROW.CODTITOLO")
					: (String) titolo.getAttribute("CODTITOLO");
		}
		return codiceTitolo;
	}

	public static BigDecimal getLivelloConoscenzaLinSil(TransactionQueryExecutor transExec, String codice)
			throws EMFInternalError {
		BigDecimal gradoLivello = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean livello = (SourceBean) transExec.executeQuery("SELECT_GRADOCONOSC_SIL_DA_GRADOCONOSC_MIN",
				inputParameters, "SELECT");
		if (livello != null) {
			gradoLivello = livello.containsAttribute("ROW") ? (BigDecimal) livello.getAttribute("ROW.CDNGRADO")
					: (BigDecimal) livello.getAttribute("CDNGRADO");
		}
		if (gradoLivello == null) {
			gradoLivello = LIVELLO_CONOSCENZA_LINGUA_NON_NOTO;
		}
		return gradoLivello;
	}

	public static String getCodiceContratto(String codrapportolav) throws EMFInternalError {
		if (MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.containsKey(codrapportolav)) {
			return MAP_CODRAPPORTOLAV_TO_CODCONTRATTO.get(codrapportolav);
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR,
				"Errore nel recupero del codice contratto lavoro a partire dal codice rapporto lavoro");
	}

	/**
	 * @param transExec
	 * @param codice
	 * @return
	 * @throws EMFInternalError
	 */
	public static Vector getCodiceContratto(TransactionQueryExecutor transExec, String codice) throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean contrattoSB = (SourceBean) transExec.executeQuery("SELECT_CODCONTRATTO_DA_RAPPORTO_MIN",
				inputParameters, "SELECT");
		if (contrattoSB != null) {
			return contrattoSB.getAttributeAsVector("ROW");
		} else {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nel recupero della tipologia rapporto");
		}
	}

	public static String getCPIFromComune(TransactionQueryExecutor transExec, String codCom) throws EMFInternalError {
		String codCPI = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codCom; // codCom;
		SourceBean cpiSB = (SourceBean) transExec.executeQuery("SELECT_CPI_COMUNE", inputParameters, "SELECT");
		if (cpiSB != null) {
			codCPI = cpiSB.containsAttribute("ROW") ? (String) cpiSB.getAttribute("ROW.CODCPI")
					: (String) cpiSB.getAttribute("CODCPI");
		} else {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nel reperimnento del CPI del comune");
		}
		return codCPI;
	}

	public static String getOrarioSil(String codiceMinisteriale) throws EMFInternalError {
		String codiceSil = ("FT".equals(codiceMinisteriale) ? "F" : ("PT".equals(codiceMinisteriale) ? "P" : null));
		return codiceSil;
	}

	public static String getCodAteco(TransactionQueryExecutor transExec, String codice) throws EMFInternalError {
		String codiceMansione = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean codAteco = (SourceBean) transExec.executeQuery("SELECT_CODATECO_DA_CODATECODOT", inputParameters,
				"SELECT");
		if (codAteco != null) {
			codiceMansione = codAteco.containsAttribute("ROW") ? (String) codAteco.getAttribute("ROW.CODATECO")
					: (String) codAteco.getAttribute("CODATECO");
		}
		return codiceMansione;
	}

	public static String getCodCpiDaIntermediario(TransactionQueryExecutor transExec, String codice)
			throws EMFInternalError {
		String codiceCpi = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codice;
		SourceBean codCpiSB = (SourceBean) transExec.executeQuery("SELECT_CPI_DA_INTERMEIARIO", inputParameters,
				"SELECT");
		if (codCpiSB != null) {
			codiceCpi = codCpiSB.containsAttribute("ROW") ? (String) codCpiSB.getAttribute("ROW.CODCPI")
					: (String) codCpiSB.getAttribute("CODCPI");
		}
		return codiceCpi;
	}

	public static String getVoto(String votazione) {
		String voto = null;
		if (votazione != null) {
			if (votazione.indexOf("/") > 0) {
				voto = votazione.substring(0, votazione.indexOf("/"));
			} else {
				voto = votazione;
			}
			if (voto.length() > 10) {
				voto = voto.substring(0, 10);
			}
		}
		return voto;
	}

	public static String getEsimi(String votazione) {
		String esimi = null;
		if (votazione != null) {
			if (votazione.indexOf("/") > 0) {
				esimi = votazione.substring(votazione.indexOf("/") + 1);
			}
		}
		return esimi;
	}

	public static void main(String[] args) {

	}

}
