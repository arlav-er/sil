package it.eng.sil.module.voucher;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetCodiciAttivazionePatto extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		disableMessageIdFail();
		disableMessageIdSuccess();
		SourceBean risultato = new SourceBean("ROWS");
		SourceBean row = doSelect(request, response, false);
		if (row != null) {
			Vector<SourceBean> rowCodici = row.getAttributeAsVector("ROW");
			for (int i = 0; i < rowCodici.size(); i++) {
				SourceBean rowCodice = (SourceBean) rowCodici.get(i);
				String codiceCurr = rowCodice.getAttribute("codattivazione").toString();
				SourceBean risCurr = new SourceBean("ROW");
				risCurr.setAttribute("codice", codiceCurr);
				risCurr.setAttribute("descrizione", codiceCurr);
				risultato.setAttribute(risCurr);
			}
		}
		response.setAttribute(risultato);
	}
}