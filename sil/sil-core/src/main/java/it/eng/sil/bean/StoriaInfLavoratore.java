/*
 * Created on May 22, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.bean;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class StoriaInfLavoratore {
	private String datInizio;
	private String cdnLavoratore;
	private String codCpiTit;
	private String codMonoTipoCpi;
	private String strCognome;
	private String strNome;
	private String strCodiceFiscale;

	public StoriaInfLavoratore(Object cdnLavoratore) throws EMFUserError {
		load(cdnLavoratore);
	}

	private void load(Object cdnLavoratore) throws EMFUserError {
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE_X_IR", new Object[] { cdnLavoratore },
				"SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.GET_ROW_FAIL);
		setDatInizio((String) row.getAttribute("row.datInizioStoriaAperta"));
		setCdnLavoratore(row.getAttribute("row.cdnlavoratore"));
		setCodCpiTit((String) row.getAttribute("row.codcpitit"));
		setCodMonoTipoCpi((String) row.getAttribute("row.codmonotipocpi"));
		setStrCognome((String) row.getAttribute("row.strcognome"));
		setStrNome((String) row.getAttribute("row.strnome"));
		setStrCodiceFiscale((String) row.getAttribute("row.strcodicefiscale"));
	}

	/**
	 * @return datInizioStoriaAperta
	 */
	public String getDatInizio() {
		return datInizio;
	}

	/**
	 * @param string
	 */
	public void setDatInizio(String dataInizio) {
		datInizio = dataInizio;
	}

	/**
	 * @return
	 */
	public String getCdnLavoratore() {
		return cdnLavoratore;
	}

	/**
	 * @return
	 */
	public String getCodCpiTit() {
		return codCpiTit;
	}

	/**
	 * @return
	 */
	public String getCodMonoTipoCpi() {
		return codMonoTipoCpi;
	}

	/**
	 * @return
	 */
	public String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	/**
	 * @return
	 */
	public String getStrCognome() {
		return strCognome;
	}

	/**
	 * @return
	 */
	public String getStrNome() {
		return strNome;
	}

	/**
	 * @param string
	 */
	public void setCdnLavoratore(Object cdnLavoratore) {
		cdnLavoratore = Utils.notNull(cdnLavoratore);
	}

	/**
	 * @param string
	 */
	public void setCodCpiTit(String string) {
		codCpiTit = string;
	}

	/**
	 * @param string
	 */
	public void setCodMonoTipoCpi(String string) {
		codMonoTipoCpi = string;
	}

	/**
	 * @param string
	 */
	public void setStrCodiceFiscale(String string) {
		strCodiceFiscale = string;
	}

	/**
	 * @param string
	 */
	public void setStrCognome(String string) {
		strCognome = string;
	}

	/**
	 * @param string
	 */
	public void setStrNome(String string) {
		strNome = string;
	}

}
