package it.eng.sil.coop.bean.blen.dto;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.bean.blen.constant.ServiziConstant;
import it.eng.sil.coop.bean.blen.input.ricerca.DatiAnagrafici;
import it.eng.sil.coop.bean.blen.input.ricerca.DatiContatto;
import it.eng.sil.coop.utils.UtilityCodifiche;

public class DatiAzienda {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DatiAzienda.class.getName());

	private DatiAnagrafici datiAnagrafici;
	private DatiContatto datiContatto;

	public DatiAzienda(DatiAnagrafici datiAnag, DatiContatto contatti) {
		setDatiAnagrafici(datiAnag);
		setDatiContatto(contatti);
	}

	public BigDecimal getAzienda(TransactionQueryExecutor txExec) throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = getDatiAnagrafici().getCodicefiscale();
		BigDecimal prgAzienda = null;
		SourceBean aziendaSB = (SourceBean) txExec.executeQuery("SELECT_RICERCA_AZIENDA", inputParameters, "SELECT");
		if (aziendaSB != null) {
			SourceBean aziendaDati = aziendaSB.containsAttribute("ROW") ? (SourceBean) aziendaSB.getAttribute("ROW")
					: aziendaSB;
			prgAzienda = aziendaDati.containsAttribute("PRGAZIENDA")
					? (BigDecimal) aziendaDati.getAttribute("PRGAZIENDA")
					: null;
		}
		return prgAzienda;
	}

	public Vector getSediAzienda(BigDecimal prgAzienda, TransactionQueryExecutor txExec) throws EMFInternalError {
		Vector sedi = null;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgAzienda;
		SourceBean sediAziendaSB = (SourceBean) txExec.executeQuery("SELECT_RICERCA_SEDI_AZIENDA", inputParameters,
				"SELECT");
		if (sediAziendaSB != null) {
			sedi = sediAziendaSB.getAttributeAsVector("ROW");
		}
		return sedi;
	}

	/**
	 * 
	 * @param sedi
	 * @return vettore che contiene nella posizione 0 il risultato della ricerca e in particolare 0 se si trova matching
	 *         per comune e indirizzo 1 se si prende l'unica sede in quel comune 2 se si prende la sede legale 3 se si
	 *         prende la prima sede in quel comune 4 se si deve inserire una nuova sede vettore che contiene nella
	 *         posizione 1 l'indice della sede trovata nel vettore sedi (nel caso in cui la ricerca non ha prodotto
	 *         risultati, allora nella posizione 1 viene settato -1)
	 * @throws Exception
	 */
	public Vector<Integer> cercaSedeAzienda(Vector sedi) {
		int unitaPerComune = 0;
		boolean esci = false;
		int indiceOK = -1;
		int indiceSedeLegale = -1;
		int indiceComune = -1;
		Vector<Integer> sedeRisultato = new Vector<Integer>();

		String indirizzoSede = getDatiContatto().getIndirizzo();
		String comuneSede = getDatiContatto().getIdcomune();
		int nSize = sedi.size();

		for (int i = 0; i < nSize && !esci; i++) {
			SourceBean sedeCurr = (SourceBean) sedi.get(i);
			String indirizzo = sedeCurr.getAttribute("strIndirizzo") != null
					? sedeCurr.getAttribute("strIndirizzo").toString()
					: "";
			String comune = sedeCurr.getAttribute("codCom") != null ? sedeCurr.getAttribute("codCom").toString() : "";
			String flgSede = sedeCurr.getAttribute("flgSede") != null ? sedeCurr.getAttribute("flgSede").toString()
					: "";
			if (flgSede.equalsIgnoreCase("S")) {
				indiceSedeLegale = i;
			}
			if (comune.equalsIgnoreCase(comuneSede) && indirizzo.equalsIgnoreCase(indirizzoSede)) {
				esci = true;
				indiceOK = i;
			} else {
				if (comune.equalsIgnoreCase(comuneSede)) {
					if (indiceComune < 0) {
						indiceComune = i;
					}
					unitaPerComune = unitaPerComune + 1;
				}
			}
		}

		if (esci) {
			// Trovato matching per comune e indirizzo
			sedeRisultato.add(0, new Integer(0));
			sedeRisultato.add(1, new Integer(indiceOK));
		} else {
			if (unitaPerComune == 1) {
				// Trovato un unico matching per comune
				sedeRisultato.add(0, new Integer(1));
				sedeRisultato.add(1, new Integer(indiceComune));
			} else {
				if (indiceSedeLegale >= 0) {
					// Trovata sede legale
					sedeRisultato.add(0, new Integer(2));
					sedeRisultato.add(1, new Integer(indiceSedeLegale));
				} else {
					if (unitaPerComune > 1) {
						// Trovato più di un matching per comune e prendo la prima
						sedeRisultato.add(0, new Integer(3));
						sedeRisultato.add(1, new Integer(indiceComune));
					} else {
						// Si deve inserire la sede
						sedeRisultato.add(0, new Integer(4));
						sedeRisultato.add(1, new Integer(-1));
					}
				}
			}
		}
		return sedeRisultato;
	}

	public BigDecimal insertTestata(TransactionQueryExecutor txExec) throws EMFInternalError {
		Object pAzienda[] = new Object[25];
		BigDecimal newPrgAzienda = getNewPrgAzienda(txExec);
		pAzienda[0] = newPrgAzienda; // "prgAzienda"
		pAzienda[1] = getDatiAnagrafici().getCodicefiscale(); // "strCodiceFiscale"
		pAzienda[2] = null; // "strPartitaIva"
		pAzienda[3] = getDatiAnagrafici().getDenominazione(); // "strRagioneSociale"
		pAzienda[4] = null; // "codNatGiuridica"
		pAzienda[5] = ServiziConstant.AZIENDA_PRIVATA; // "codTipoAzienda"
		pAzienda[6] = getDatiAnagrafici().getWeb(); // "strSitoInternet"
		pAzienda[7] = null; // "strDescAttivita"
		pAzienda[8] = null; // "numSoci"
		pAzienda[9] = null; // "numDipendenti"
		pAzienda[10] = null; // "numCollaboratori"
		pAzienda[11] = null; // "numAltraPosizione"
		pAzienda[12] = null; // "datInizio"
		pAzienda[13] = null; // "datFine"
		pAzienda[14] = null; // "DATAGGINFORMAZIONE"
		pAzienda[15] = null; // "strHistory"
		pAzienda[16] = null; // "strNote"
		pAzienda[17] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
		pAzienda[18] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
		pAzienda[19] = null; // "flgdatiok"
		pAzienda[20] = null; // "strNumAlboInterinali"
		pAzienda[21] = null; // "strRepartoInail"
		pAzienda[22] = null; // "strPatInail"
		pAzienda[23] = null; // "flgObbligoL68"
		pAzienda[24] = null; // "strNumAgSomministrazione"

		executeInsert(txExec, "INSERT_TESTATA_AZIENDA", pAzienda);

		return newPrgAzienda;
	}

	public BigDecimal insertSedeAzienda(BigDecimal prgAzienda, TransactionQueryExecutor txExec)
			throws EMFInternalError {

		String indirizzoUnitaAzienda = getDatiContatto().getIndirizzo();
		if (indirizzoUnitaAzienda == null || "".equalsIgnoreCase(indirizzoUnitaAzienda)) {
			indirizzoUnitaAzienda = ServiziConstant.NON_SPECIFICATO;
		}

		Object pUnita[] = new Object[29];
		BigDecimal newPrgUnita = getNewPrgUnita(prgAzienda, txExec);
		pUnita[0] = prgAzienda; // "prgAzienda"
		pUnita[1] = newPrgUnita; // "prgUnita"
		pUnita[2] = indirizzoUnitaAzienda; // "strIndirizzo"
		pUnita[3] = ServiziConstant.FLG_NO_SEDE_LEGALE; // "flgSede"
		pUnita[4] = null; // "strRea"
		pUnita[5] = null; // "strLocalita"
		pUnita[6] = getDatiContatto().getIdcomune(); // "codCom"
		pUnita[7] = getDatiContatto().getCap(); // "strCap"
		pUnita[8] = null; // "flgMezziPub"
		pUnita[9] = ServiziConstant.SEDE_IN_ATTIVITA; // "codAzStato"
		pUnita[10] = null; // "strResponsabile"
		pUnita[11] = null; // "strReferente"
		pUnita[12] = getDatiContatto().getTelefono(); // "strTel"
		pUnita[13] = getDatiContatto().getFax(); // "strFax"
		pUnita[14] = getDatiContatto().getEmail(); // "strEmail"
		pUnita[15] = UtilityCodifiche.getCodAteco(txExec, getDatiAnagrafici().getSettore()); // "codAteco"
		pUnita[16] = ServiziConstant.CODIFICA_NON_DISPONIBILE; // "codCCNL"
		pUnita[17] = null; // "datInizio"
		pUnita[18] = null; // "datFine"
		pUnita[19] = null; // "strNote"
		pUnita[20] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
		pUnita[21] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
		pUnita[22] = null; // "strnumeroinps"
		pUnita[23] = null; // "strNumRegistroCommitt"
		pUnita[24] = null; // "DATREGISTROCOMMIT"
		pUnita[25] = null; // "STRRIFERIMENTOSARE"
		pUnita[26] = null; // "STRREPARTOINPS"
		pUnita[27] = null; // "STRDENOMINAZIONE"
		pUnita[28] = null; // TODO: "strPECemail"

		executeInsert(txExec, "INSERT_UNITA_AZIENDA", pUnita);

		return newPrgUnita;
	}

	private BigDecimal getNewPrgAzienda(TransactionQueryExecutor txExec) throws EMFInternalError {
		SourceBean res = (SourceBean) txExec.executeQuery("SELECT_AN_AZIENDA_SEQUENCE", null, "SELECT");
		BigDecimal prgNewAzienda = res.containsAttribute("ROW") ? (BigDecimal) res.getAttribute("ROW.PRGAZIENDA")
				: (BigDecimal) res.getAttribute("PRGAZIENDA");
		return prgNewAzienda;
	}

	private BigDecimal getNewPrgUnita(BigDecimal prgAzienda, TransactionQueryExecutor txExec) throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgAzienda;
		SourceBean res = (SourceBean) txExec.executeQuery("SELECT_AN_UNITA_AZIENDA_SEQUENCE", inputParameters,
				"SELECT");
		BigDecimal prgUnita = res.containsAttribute("ROW") ? (BigDecimal) res.getAttribute("ROW.PRGUNITA")
				: (BigDecimal) res.getAttribute("PRGUNITA");
		return prgUnita;
	}

	public DatiAnagrafici getDatiAnagrafici() {
		return datiAnagrafici;
	}

	public void setDatiAnagrafici(DatiAnagrafici value) {
		this.datiAnagrafici = value;
	}

	public DatiContatto getDatiContatto() {
		return datiContatto;
	}

	public void setDatiContatto(DatiContatto value) {
		this.datiContatto = value;
	}

	private static boolean isInsertFailed(Object objRes) {
		return objRes == null || !(objRes instanceof Boolean && ((Boolean) objRes).booleanValue() == true);
	}

	private static void executeInsert(TransactionQueryExecutor transExec, String statementName,
			Object[] inputParameters) throws EMFInternalError {
		Object objRes = transExec.executeQuery(statementName, inputParameters, "INSERT");

		if (isInsertFailed(objRes)) {
			_logger.error("Inserimento fallito: " + statementName);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Inserimento fallito: " + statementName);
		}
	}

	public static void setSedeLegaleIfNeeded(TransactionQueryExecutor transExec, BigDecimal prgAzienda,
			BigDecimal prgUnitaAzienda) throws EMFInternalError {

		boolean hasSedeLegale = hasSedeLegale(transExec, prgAzienda);

		if (!hasSedeLegale) {

			Object[] inputParameters = new Object[3];
			inputParameters[0] = ServiziConstant.UTENTE_BLEN;
			inputParameters[1] = prgUnitaAzienda;
			inputParameters[2] = prgAzienda;

			executeUpdate(transExec, "BLEN_SET_SEDE_LEGALE_UNITA_AZIENDA", inputParameters);

		}

	}

	private static boolean hasSedeLegale(TransactionQueryExecutor transExec, BigDecimal prgAzienda)
			throws EMFInternalError {

		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgAzienda;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_EXISTS_SEDE_LEGALE", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private static void executeUpdate(TransactionQueryExecutor transExec, String statementName,
			Object[] inputParameters) throws EMFInternalError {
		Object objRes = transExec.executeQuery(statementName, inputParameters, "UPDATE");

		if (isInsertFailed(objRes)) {
			_logger.error("Aggiornamento fallito: " + statementName);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Aggiornamento fallito: " + statementName);
		}
	}

}
