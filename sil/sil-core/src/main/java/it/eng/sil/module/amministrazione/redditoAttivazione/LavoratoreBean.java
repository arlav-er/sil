package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class LavoratoreBean {

	private List movimenti;
	private List did;
	private Object cdnLavoratore;
	private List statiOccupazionali;
	private TransactionQueryExecutor transactionQueryExecutor;

	public LavoratoreBean(Object cdnLavoratore, TransactionQueryExecutor transactionQueryExecutor) throws Exception {
		setCdnLavoratore(cdnLavoratore);
		setTransactionQueryExecutor(transactionQueryExecutor);
		List movimenti = MovimentoBean.getMovimenti(getCdnLavoratore(), getTransactionQueryExecutor());
		movimenti = Controlli.togliMovNonProtocollati(new Vector(movimenti));
		movimenti = Controlli.togliMovimentoInDataFutura(new Vector(movimenti));
		setMovimenti(movimenti);
		setDid(DidBean.getDid(getCdnLavoratore(), getTransactionQueryExecutor()));
		setStatiOccupazionali(
				StatoOccupazionaleBean.getStatiOccupazionali(getCdnLavoratore(), getTransactionQueryExecutor()));
	}

	public List getDid() {
		return did;
	}

	public List getMovimenti() {
		return movimenti;
	}

	public List getStatiOccupazionali() {
		return statiOccupazionali;
	}

	public void setCdnLavoratore(Object object) {
		cdnLavoratore = object;
	}

	public void setDid(List list) {
		did = list;
	}

	public void setMovimenti(List list) {
		movimenti = list;
	}

	public void setStatiOccupazionali(List list) {
		statiOccupazionali = list;
	}

	public void setTransactionQueryExecutor(TransactionQueryExecutor executor) {
		transactionQueryExecutor = executor;
	}

	public Object getCdnLavoratore() {
		return cdnLavoratore;
	}

	private TransactionQueryExecutor getTransactionQueryExecutor() {
		return transactionQueryExecutor;
	}

	public SourceBean getInfoLav() throws Exception {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = getCdnLavoratore();
		SourceBean lav = (SourceBean) getTransactionQueryExecutor().executeQuery("SELECT_INFO_LAVORATORE_RESIDENZA_RA",
				inputParameters, "SELECT");
		if (lav == null) {
			throw new Exception("impossibile leggere i dati del lavoratore");
		} else {
			lav = lav.containsAttribute("ROW") ? (SourceBean) lav.getAttribute("ROW") : lav;
		}
		return lav;
	}

	public Vector getLGInfoLav(String dataDichiarazione) throws Exception {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = getCdnLavoratore();
		inputParameters[1] = dataDichiarazione;
		SourceBean res = (SourceBean) getTransactionQueryExecutor()
				.executeQuery("SELECT_LG_INFO_LAVORATORE_RESIDENZA_RA", inputParameters, "SELECT");
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i dati del lavoratore");
	}

}
