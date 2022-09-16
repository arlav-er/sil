package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;

public class Cessazione extends MovimentoBean {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Cessazione.class.getName());
	private boolean fineAnticipata;
	private boolean processato;

	public Cessazione(SourceBean m) throws SourceBeanException {
		super(m);
	}

	public boolean fineAnticipata() throws Exception {
		return DateUtils.compare(getDataFineMov(), getDataFineOriginaria()) == 0;
	}

	public String getDataFineMov() {
		return getDataFineEffettiva();
	}

	public String getDataFineOriginaria() {
		return getCollegato().getDataFine();
	}

	public void setMovimentoBack(MovimentoBean mov) {
		super.setMovimentoBack(mov);
		String dataInizioCess = (String) getDataInizio();
		try {
			if (mov.getAttribute(MovimentoBean.DB_COD_MOVIMENTO) != null
					&& mov.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equalsIgnoreCase("TRA")) {
				String dataInizioTra = mov.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
				if (DateUtils.compare(dataInizioCess, dataInizioTra) >= 0) {
					mov.updAttribute(MovimentoBean.DB_DATA_FINE, dataInizioCess);
					mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataInizioCess);
				}
			} else {
				mov.updAttribute(MovimentoBean.DB_DATA_FINE, dataInizioCess);
				mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataInizioCess);
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CessazioneBean:setMovimentoBack():Impossibile aggiornare la data fine del movimento cessato", e);

		}
	}

	public String toString() {
		String s = super.toString();
		s += " , movimento cessato " + getMovimentoBack();
		return s;
	}

	public void movimentoCessato() throws Exception {
		getMovimentoBack().setCessato(true);
	}
}