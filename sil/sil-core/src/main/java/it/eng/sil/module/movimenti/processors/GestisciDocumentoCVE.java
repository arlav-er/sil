package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class GestisciDocumentoCVE extends InsertDocumento {
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans = null;

	public GestisciDocumentoCVE(BigDecimal user, TransactionQueryExecutor txExecutor) {
		super(user, txExecutor);
		trans = txExecutor;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		try {
			if (record.get("MAPCVE") != null) {
				super.processRecord((Map) record.get("MAPCVE"));
			}
		}

		catch (Exception emf) {
			return ProcessorsUtils.createResponse("", classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DOC), "",
					warnings, null);
		}

		return ProcessorsUtils.createResponse("Inserimento documento avviamento da cessazione", classname, null, null,
				warnings, null);
	}
}