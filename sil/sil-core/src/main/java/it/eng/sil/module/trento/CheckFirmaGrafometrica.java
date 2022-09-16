package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;

public class CheckFirmaGrafometrica extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean check = true;
		this.setSectionQuerySelect("QUERY_CONFIG_PROTOCOLLO");
		SourceBean rowConfig = doSelect(request, response);
		if (rowConfig != null) {
			String flgFirmaGrafo = rowConfig.containsAttribute("row.flgfirmagrafo")
					? rowConfig.getAttribute("row.flgfirmagrafo").toString()
					: "0";
			if (flgFirmaGrafo.equals(Properties.FLAG_1)) {
				response.setAttribute("FLGFIRMAGRAFO", "1");
				// Verifica dello stato consenso in SESSION
				// String cdnLavoratore =
				// request.containsAttribute("CDNLAVORATORE")?request.getAttribute("CDNLAVORATORE").toString():"";
				// Consenso consensoLav = (Consenso)getRequestContainer().getSessionContainer().getAttribute("CONSENSO_"
				// + cdnLavoratore);
				// if (consensoLav != null) {
				// String codStatoConsenso = consensoLav.getCodice();
				// if (codStatoConsenso.equalsIgnoreCase(Consenso.ASSENTE)) {
				// check = false;
				// }
				// }
			}
		} else {
			check = false;
		}

		if (check) {
			response.setAttribute("FIRMAGRAFOMETRICA", "true");
		}
	}

}
