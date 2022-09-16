package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class ControllaCateneMovimenti implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ControllaCateneMovimenti.class.getName());

	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;

	public ControllaCateneMovimenti(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		trans = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList<Warning> warnings = new ArrayList<Warning>();
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, null);
		}
		//
		Object cdnLavoratore = record.get("CDNLAVORATORE");
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean rowCatena = null;

		try {
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_RIPETUTI", params, "SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal numRipetizioni = (BigDecimal) rowCatena.getAttribute("ripetizioni");
				if (numRipetizioni != null && numRipetizioni.intValue() > 0) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_SUCCESSIVI", params, "SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_PRECEDENTI", params, "SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_SUCCESSIVI_APPROFONDITA", params, "SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_PRECEDENTI_APPROFONDITA", params, "SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_NO_SUCCESSIVO_PUNTATO_DA_PREC", params,
					"SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
			//
			rowCatena = (SourceBean) trans.executeQuery("SELECT_MOVIMENTI_NO_PRECEDENTE_PUNTATO_DA_SUCC", params,
					"SELECT");
			if (rowCatena != null) {
				rowCatena = (rowCatena.containsAttribute("ROW") ? (SourceBean) rowCatena.getAttribute("ROW")
						: rowCatena);
				BigDecimal cdnLavCatena = (BigDecimal) rowCatena.getAttribute("cdnlavoratore");
				if (cdnLavCatena != null) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_VALIDAZIONE_CATENE_MOVIMENTI), "", warnings, null);
				}
			}
		} catch (EMFInternalError emfie) {
			Warning w = new Warning(MessageCodes.ImportMov.WAR_GRUPPI_SEC_LIV, "");
			warnings.add(w);
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
		}

		return null;
	}

}
