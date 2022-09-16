package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class RiepilogoPacchettoAdulti extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		Object cdnLav = request.getAttribute("CDNLAVORATORE");
		if (cdnLav != null) {
			BigDecimal cdnLavoratore = new BigDecimal(cdnLav.toString());
			PacchettoAdulti bean = new PacchettoAdulti(cdnLavoratore);
			String dataAdesione = bean.caricaDataAdesione();
			if (dataAdesione != null && !dataAdesione.equals("")) {
				response.setAttribute("DATAADESIONEPA", dataAdesione);
				BigDecimal mesiAnzianitaAdesioneDid = null;
				SourceBean row = PattoBean.caricaInfoAnzianitaLavoratore(cdnLavoratore, dataAdesione);
				if (row != null) {
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					mesiAnzianitaAdesioneDid = (BigDecimal) row.getAttribute("MESIANZIANITA");
					if (mesiAnzianitaAdesioneDid != null) {
						response.setAttribute("MESIANZIANITAADESIONEDID", mesiAnzianitaAdesioneDid.intValue());
					}
				}
			}
		}
	}

}
