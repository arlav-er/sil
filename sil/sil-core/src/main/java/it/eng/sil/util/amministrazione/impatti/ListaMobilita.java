package it.eng.sil.util.amministrazione.impatti;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;

public class ListaMobilita {
	Vector listaMobilita = null;

	public ListaMobilita(Object cdnlavoratore, TransactionQueryExecutor txExecutor) throws Exception {
		recuperaMobilita(cdnlavoratore, txExecutor);
	}

	private void recuperaMobilita(Object cdnlavoratore, TransactionQueryExecutor txExecutor) throws Exception {
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = cdnlavoratore;
		row = (SourceBean) txExecutor.executeQuery("GET_MOBILITA_IMPATTI", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile estrarre la mobilità del lavoratore");
		listaMobilita = row.getAttributeAsVector("ROW");
	}

	public Vector getMobilita() {
		return this.listaMobilita;
	}

}