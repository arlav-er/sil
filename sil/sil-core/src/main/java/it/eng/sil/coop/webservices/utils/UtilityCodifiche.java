package it.eng.sil.coop.webservices.utils;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

public class UtilityCodifiche {

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
		return gradoLivello;
	}

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
		System.out.println(UtilityCodifiche.getVoto("76"));
	}

}
