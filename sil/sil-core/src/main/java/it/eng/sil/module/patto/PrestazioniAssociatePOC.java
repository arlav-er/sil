package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class PrestazioniAssociatePOC extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		BigDecimal prgPattoLavoratore = (BigDecimal) request.getAttribute("prgPattoLavoratore");
		int codiceErrore = -1;
		SourceBean response_rows = new SourceBean("ROWS");
		SourceBean response_row = new SourceBean("ROW");
		BigDecimal numPrestazioni = new BigDecimal(0);
		if (prgPattoLavoratore != null) {
			Vector<String> serviziProgrammi = PattoBean.checkServiziProgrammi(prgPattoLavoratore, null,
					PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE);
			codiceErrore = PattoBean.checkProtocollazionePOC(prgPattoLavoratore, serviziProgrammi);
			if (codiceErrore <= 0) {
				numPrestazioni = new BigDecimal(1);
			}
		}
		response_row.setAttribute("numPrestazioni", numPrestazioni);
		response_rows.setAttribute(response_row);
		response.setAttribute(response_rows);
	}

}
