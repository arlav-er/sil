package it.eng.sil.coop.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

public class ProspettoInformativo {

	private String codComunicazione;
	private String codComunicazioneOrig;
	private BigDecimal prgProspettoInf;
	private BigDecimal prgAzienda;
	private BigDecimal prgUnita;
	private String codProvincia;
	/** data del prospetto in formato dd/mm/yyyy **/
	private String datProspetto;

	public ProspettoInformativo() {
	}

	/**
	 * Costruisce un prospetto informativo a partire da un SourceBean contenente i dati necessari. Non esegue controlli
	 * sui vincoli.
	 * 
	 * @param prospettoBean
	 */
	public ProspettoInformativo(SourceBean prospettoBean) {
		buildFromSourceBean(prospettoBean);
	}

	/**
	 * Costruisce un prospetto informativo a partire da un prgProspettoInf
	 * 
	 * @param prgProspettoInf
	 * @param qExec
	 *            se viene passato null viene iniziata una nuova transazione.
	 * @throws Exception
	 */
	public ProspettoInformativo(BigDecimal prgProspettoInf, TransactionQueryExecutor qExec) throws Exception {

		TransactionQueryExecutor tex = null;
		try {
			if (qExec == null) {
				tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tex.initTransaction();
			} else {
				tex = qExec;
			}

			Object[] param = new Object[1];
			param[0] = prgProspettoInf;
			SourceBean prospettoBean = (SourceBean) tex.executeQuery("CM_GET_PROSPETTO_BY_PRGPROSPETTOINF", param,
					"SELECT");
			Vector<SourceBean> rows = prospettoBean.getAttributeAsVector("ROW");
			if (rows.size() != 1) {
				throw new Exception("Impossibile recuperare il prospetto con prgProspettoInf " + prgProspettoInf
						+ ". Numero risultati:" + rows.size());
			}
			buildFromSourceBean(rows.get(0));

			if (qExec == null)
				tex.commitTransaction();
		} catch (Exception e) {
			if (qExec == null) {
				tex.rollBackTransaction();
			}
		}
	}

	public ProspettoInformativo(BigDecimal prgProspettoInf) throws Exception {
		this(prgProspettoInf, null);
	}

	/**
	 * Costruisce un prospetto informativo a partire dal codComunicazione. Utilizzare questo costruttore solo se si è
	 * certi dell'unicità del codComunicazione, altrimenti utilizzare il metodo statico fornito da questa stessa classe
	 * per la restituzione di una lista di prospetti informativi.
	 * 
	 * @param codComunicazione
	 * @param qExec
	 *            se viene passato null viene iniziata una nuova transazione.
	 * @throws Exception
	 *             lancia un'eccezione se a sistema è presente più di un prospetto con il codice comunicazione indicato
	 *             o se non ne è presente nemmeno uno.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ProspettoInformativo(String codComunicazione, TransactionQueryExecutor qExec) throws Exception {

		TransactionQueryExecutor tex = null;
		try {
			if (qExec == null) {
				tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tex.initTransaction();
			} else {
				tex = qExec;
			}

			Object[] param = new Object[1];
			param[0] = codComunicazione;
			SourceBean prospettoBean = (SourceBean) tex.executeQuery("CM_GET_PROSPETTO_BY_CODCOMUNICAZIONE", param,
					"SELECT");
			Vector<SourceBean> rows = prospettoBean.getAttributeAsVector("ROW");
			if (rows.size() != 1) {
				throw new Exception("Impossibile recuperare il prospetto con codice " + codComunicazione
						+ ". Numero risultati:" + rows.size());
			}
			buildFromSourceBean(rows.get(0));

			if (qExec == null)
				tex.commitTransaction();
		} catch (Exception e) {
			if (qExec == null) {
				tex.rollBackTransaction();
			}
		}
	}

	public ProspettoInformativo(String codComunicazione) throws Exception {
		this(codComunicazione, null);
	}

	private void buildFromSourceBean(SourceBean prospettoBean) {
		prgProspettoInf = (BigDecimal) prospettoBean.getAttribute("PRGPROSPETTOINF");
		prgAzienda = (BigDecimal) prospettoBean.getAttribute("PRGAZIENDA");
		prgUnita = (BigDecimal) prospettoBean.getAttribute("PRGUNITA");
		codProvincia = (String) prospettoBean.getAttribute("CODPROVINCIA");
		codComunicazione = (String) prospettoBean.getAttribute("CODCOMUNICAZIONE");
		codComunicazioneOrig = (String) prospettoBean.getAttribute("CODCOMUNICAZIONEORIG");
		datProspetto = (String) prospettoBean.getAttribute("DATPROSPETTO");
	}

	public BigDecimal getPrgProspettoInf() {
		return prgProspettoInf;
	}

	public void setPrgProspettoInf(BigDecimal prgProspetto) {
		this.prgProspettoInf = prgProspetto;
	}

	public BigDecimal getPrgAzienda() {
		return prgAzienda;
	}

	public void setPrgAzienda(BigDecimal prgAzienda) {
		this.prgAzienda = prgAzienda;
	}

	public BigDecimal getPrgUnita() {
		return prgUnita;
	}

	public void setPrgUnita(BigDecimal prgUnita) {
		this.prgUnita = prgUnita;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getDatProspetto() {
		return datProspetto;
	}

	public void setDatProspetto(String datProspetto) {
		this.datProspetto = datProspetto;
	}

	public String getCodComunicazione() {
		return codComunicazione;
	}

	public void setCodComunicazione(String codComunicazione) {
		this.codComunicazione = codComunicazione;
	}

	public String getCodComunicazioneOrig() {
		return codComunicazioneOrig;
	}

	public void setCodComunicazioneOrig(String codComunicazioneOrig) {
		this.codComunicazioneOrig = codComunicazioneOrig;
	}

	/**
	 * Questo metodo permette di ottenere una lista di prospetti, ordinati per data di riferimento decrescente e quindi
	 * per prgProspettoInf decrescente a partire da un codice comunicazione.
	 * 
	 * @param codComunicazione
	 * @param qExec
	 *            se viene passato null viene iniziata una nuova transazione.
	 * @return lista dei prospetti trovati. Tale lista può essere anche vuota ma non restituisce mai null.
	 * @throws EMFInternalError
	 */
	@SuppressWarnings("unchecked")
	public static List<ProspettoInformativo> getProspetti(String codComunicazione, TransactionQueryExecutor qExec)
			throws EMFInternalError {
		List<ProspettoInformativo> ret = new ArrayList<ProspettoInformativo>();

		TransactionQueryExecutor tex = null;
		try {
			if (qExec == null) {
				tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tex.initTransaction();
			} else {
				tex = qExec;
			}

			Object[] param = new Object[1];
			param[0] = codComunicazione;
			SourceBean prospettoBean = (SourceBean) tex.executeQuery("CM_GET_PROSPETTO_BY_CODCOMUNICAZIONE", param,
					"SELECT");
			Vector<SourceBean> rows = prospettoBean.getAttributeAsVector("ROW");
			for (SourceBean sourceBean : rows) {
				ProspettoInformativo prospetto = new ProspettoInformativo(sourceBean);
				ret.add(prospetto);
			}

			if (qExec == null)
				tex.commitTransaction();
		} catch (Exception e) {
			if (qExec == null) {
				tex.rollBackTransaction();
			}
		}
		return ret;
	}

	/**
	 * overloaded
	 * 
	 * @see ProspettoInformativo.getProspetti(String, TransactionQueryExecutor)
	 */
	public static List<ProspettoInformativo> getProspetti(String codComunicazione) throws EMFInternalError {
		return getProspetti(codComunicazione, null);
	}

	public String toString() {
		return "Prospetto Informativo:\n" + "prgProspetto: " + prgProspettoInf + "\n" + "codComunicazione: "
				+ codComunicazione + "\n" + "datProspetto: " + datProspetto + "\n" + "prgAzienda: " + prgAzienda + "\n"
				+ "prgUnita: " + prgUnita + ".";
	}

}
