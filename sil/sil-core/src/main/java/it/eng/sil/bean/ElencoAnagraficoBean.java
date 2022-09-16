package it.eng.sil.bean;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class ElencoAnagraficoBean {
	private BigDecimal prgElencoAnagrafico = null;
	private String datInizio = null;
	private String datCan = null;
	private String codTipoCan = null;

	public ElencoAnagraficoBean() {
	}

	public void caricaElenco(BigDecimal cdnlavoratore, MultipleTransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgElencoAnag = null;
		String dataInizio = null;
		Object[] params = new Object[] { cdnlavoratore };
		SourceBean row = (SourceBean) txExec.executeQuery("GET_ULTIMOELANAG_DA_CDNLAV", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgElencoAnag = (BigDecimal) row.getAttribute("prgelencoanagrafico");
			dataInizio = (String) row.getAttribute("datinizio");
			setPrgElencoAnagrafico(prgElencoAnag);
			setDatInizio(dataInizio);
		}
	}

	public boolean insertElencoAnagrafico(BigDecimal cdnlavoratore, BigDecimal userID,
			MultipleTransactionQueryExecutor txExec) throws Exception {
		String dataRichiesta = DateUtils.getNow();
		Object[] params = new Object[] { cdnlavoratore, dataRichiesta, null, null, null, userID, userID };
		Boolean resEA = (Boolean) txExec.executeQuery("INSERT_ELANAG", params, "INSERT");
		return resEA.booleanValue();
	}

	public BigDecimal getPrgElencoAnagrafico() {
		return this.prgElencoAnagrafico;
	}

	public void setPrgElencoAnagrafico(BigDecimal val) {
		this.prgElencoAnagrafico = val;
	}

	public String getDatInizio() {
		return this.datInizio;
	}

	public void setDatInizio(String val) {
		this.datInizio = val;
	}

}
