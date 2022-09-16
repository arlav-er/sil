package com.engiweb.framework.error;

import com.engiweb.framework.base.CloneableObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

/**
 * Errore di importazione MySAP.
 * 
 * @author Guido Zuccaro
 * @version 1.0
 * @since 18/03/2016
 */
public class EMFSAPError extends EMFAbstractError {

	/**
	 * Sezioni MySAP da importare.
	 */
	public static final String SAP_SEZ_ANAGRA = "frmAnagra";
	public static final String SAP_SEZ_TITOLI = "frmTitStu";
	public static final String SAP_SEZ_FOR_PRO = "frmForPro";
	public static final String SAP_SEZ_ESP_LAV = "frmEspLav";
	public static final String SAP_SEZ_LINGUE = "frmLingue";
	public static final String SAP_SEZ_CON_INF = "frmConInf";
	public static final String SAP_SEZ_ABILITA = "frmAbilita";
	public static final String SAP_SEZ_PROPEN = "frmPropen";
	public static final String SAP_SEZ_ALTRO = "frmAltro";

	/**
	 * TODO: inserire in messages_it_IT.properties 61049=Erore di validazione SAP.
	 */
	public static final int SAP_ERROR = 61049;

	public static final String SAP_ERR_NULL = "Campo non valorizzato.";
	public static final String SAP_ERR_DUPL = "Record duplicato.";

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EMFAbstractError.class.getName());

	private static final long serialVersionUID = 1L;

	// Codice di errore.
	private int codice;
	// Sezione SAP in cui si è verificato l'errore.
	private String sezione;
	// Numero di record.
	private int record;
	// Nome del campo in errore.
	private String campo;
	// Descrizione dell'errore.
	private String errore;

	/**
	 * @param codice
	 *            Codice di errore.
	 * @param sezione
	 *            Sezione SAP in cui si è verificato l'errore (es: titoli di studio, esperienze lavorative).
	 * @param campo
	 *            Nome del campo in errore (es: strNome).
	 * @param errore
	 *            Descrizione dell'errore (es: campo on valorizzato).
	 */
	public EMFSAPError(String sezione, int record, String campo, String errore) {
		this.codice = SAP_ERROR;
		this.sezione = sezione;
		this.record = record;
		this.campo = campo;
		this.errore = errore;
	}

	/**
	 * Aggiunge l'errore al modulo passato come parametro.
	 * 
	 * @param modulo
	 *            Modulo corrente cui aggiungere l'errore.
	 */
	public void addError2Module(DefaultRequestContext modulo) {
		modulo.getErrorHandler().addError(this);
	}

	/**
	 * Restituisce una copia dell'oggetto corrente.
	 */
	@Override
	public CloneableObject cloneObject() {
		return new EMFSAPError(sezione, record, campo, errore);
	}

	/**
	 * Genera un source bean dell'errore.
	 */
	@Override
	public SourceBean getSourceBean() {
		SourceBean bean = null;
		try {
			bean = new SourceBean("SAP_ERROR");
			bean.setAttribute("CODE", codice);
			bean.setAttribute("SEZIONE", sezione);
			bean.setAttribute("RECORD", "" + record);
			bean.setAttribute("CAMPO", campo);
			bean.setAttribute("ERROR", errore);
		} catch (Exception e) {
			logger.error("Impossibile creare il source bean.");
		}
		return bean;
	}

	public String getSezione() {
		return sezione;
	}

	public int getRecord() {
		return record;
	}

	public String getCampo() {
		return campo;
	}

	public String getErrore() {
		return errore;
	}

	@Override
	public String toString() {
		String html = "";
		html += "Errore nell'importazione dei dati da MySAP: ";
		html += ", record " + record;
		html += ", campo " + campo;
		html += ": " + errore + "<br/>";
		return html;
	}
}
