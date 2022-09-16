package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class M_MovGetDettaglio extends AbstractSimpleModule {
	public M_MovGetDettaglio() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		doSelect(request, response);
		String codTipoMov = response.getAttribute("ROWS.ROW.codTipoMov") != null
				? response.getAttribute("ROWS.ROW.codTipoMov").toString()
				: "";
		Object prgMovPrec = response.getAttribute("ROWS.ROW.prgMovimentoPrec");
		if (codTipoMov.equalsIgnoreCase("CES") && prgMovPrec != null) {
			Object[] params = new Object[] { prgMovPrec };
			SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_MV_CESSAZIONE_PREC", params, "SELECT",
					Values.DB_SIL_DATI);
			if (row != null && row.containsAttribute("ROW")) {
				if (row.getAttribute("ROW.CODMVCESSAZIONE") != null) {
					response.setAttribute("ROWS.ROW.CODMVCESSAZIONEPREC",
							row.getAttribute("ROW.CODMVCESSAZIONE").toString());
				}
			} else {
				if (row != null && row.getAttribute("CODMVCESSAZIONE") != null) {
					response.setAttribute("ROWS.ROW.CODMVCESSAZIONEPREC",
							row.getAttribute("CODMVCESSAZIONE").toString());
				}
			}
		}
	}
}