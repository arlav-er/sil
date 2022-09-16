package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.base.XMLObject;

/**
 * Contiene le informazioni relative ad una specifica elaborazione del batch
 */
public class LogBean extends SourceBean {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LogBean.class.getName());

	/**
	 * 
	 */
	private Exception error;
	private StatoOccupazionaleBean statoOccupazionale;
	private SourceBean statoOccBean;
	private SourceBean movimentoBean;
	private SourceBean cessazioneBean;
	private SourceBean movimentoAggiornatoBean;
	private SourceBean cessazioneAggiornataBean;

	/**
	 * 
	 */
	public LogBean(String name) throws SourceBeanException {
		super(name);
	}

	public void setError(Exception e) {
		this.error = e;
	}

	public void setStatoOccupazionale(StatoOccupazionaleBean socc) {
		this.statoOccupazionale = socc;
	}

	public void setStatoOccupazionale(SourceBean socc) {
		this.statoOccBean = socc;
	}

	public void setMovimento(SourceBean m) throws Exception {
		if (m != null)
			this.movimentoBean = new SourceBean(m);
	}

	public void setMovimentoAggiornato(SourceBean m) {
		if (m != null)
			this.movimentoAggiornatoBean = m;
	}

	public void setCessazione(SourceBean c) throws Exception {
		if (c != null)
			this.cessazioneBean = new SourceBean(c);
	}

	public void setCessazioneAggiornata(SourceBean c) {
		if (c != null)
			this.cessazioneAggiornataBean = c;
	}

	public String toXML() {
		try {
			SourceBean socc = new SourceBean("STATO_OCCUPAZIONALE");
			socc.setAttribute("prgStatoOccupaz", this.statoOccupazionale.getProgressivoDB());
			socc.setAttribute("codStatoOccupaz",
					StatoOccupazionaleBean.decode(this.statoOccupazionale.getStatoOccupazionale()));
			socc.setAttribute("datInizio", this.statoOccupazionale.getDataInizio());
			if (this.statoOccupazionale.getDataAnzianita() != null)
				socc.setAttribute("datAnzianita", this.statoOccupazionale.getDataAnzianita());
			if (this.statoOccupazionale.getIndennizzato() != null)
				socc.setAttribute("flgIndennizzato", this.statoOccupazionale.getIndennizzato());
			if (this.statoOccupazionale.getPensionato() != null)
				socc.setAttribute("flgIndennizzato", this.statoOccupazionale.getPensionato());
			/** ******************************************************************** */
			socc.setAttribute("CHANGED", String.valueOf(this.statoOccupazionale.ischanged()));
			if (this.statoOccupazionale.ischanged())
				socc.setAttribute("STATO_OCCUPAZIONALE_CAMBIATO",
						this.statoOccupazionale.getStatoOccupazionaleIniziale());
			this.setAttribute(socc);
			/** ******************************************************************** */
			if (this.movimentoBean != null)
				this.setAttribute("MOVIMENTO", this.movimentoBean.getAttribute("ROW"));
			if (this.movimentoAggiornatoBean != null)
				this.setAttribute("MOVIMENTO_AGGIORNATO", this.movimentoAggiornatoBean.getAttribute("ROW"));
			/** ******************************************************************** */
			if (this.cessazioneBean != null)
				this.setAttribute("CESSAZIONE", this.cessazioneBean.getAttribute("ROW"));
			if (this.cessazioneAggiornataBean != null)
				this.setAttribute("CESSAZIONE_AGGIORNATA", this.cessazioneAggiornataBean.getAttribute("ROW"));
			/** ******************************************************************** */
			if (this.error != null)
				this.setAttribute("ERROR", getErrorBean());
			return this.toXML(false, true);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "LogBean: toXML()", e);

		}
		return super.toXML(false, true);
	}

	public XMLObject getErrorBean() throws Exception {
		if (this.error instanceof XMLObject) {
			return (XMLObject) this.error;
		}
		SourceBean sb = new SourceBean("ERROR");
		if (this.error.getMessage() != null)
			sb.setAttribute("MESSAGE", this.error.getMessage());
		this.setAttribute("CLASS", this.error.getClass().getName());
		return sb;
	}

	public void clearBean() {
		this.error = null;
		this.statoOccupazionale = null;
		this.movimentoBean = null;
		this.statoOccBean = null;
		this.cessazioneBean = null;
		this.cessazioneAggiornataBean = null;
		this.movimentoAggiornatoBean = null;
		super.clearBean();
	}
}