package it.eng.sil.module.conf.did;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.gov.lavoro.servizi.servizicoap.richiestasap.ListaDIDType;

public class GetPresaCarico extends AbstractSimpleModule {

	private static final long serialVersionUID = 6500495854828802216L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetPresaCarico.class.getName());

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "cdnLavoratore");
		String dataDid = "";
		if (request.containsAttribute("datDidSil")) {
			dataDid = StringUtils.getAttributeStrNotNull(request, "datDidSil");
		}
		if (dataDid.equals("")) {
			// LavoratoreType lavT = null;
			ListaDIDType lavT1 = null;
			SourceBean serviceResponse = getResponseContainer().getServiceResponse();
			if (serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.SAPWS")
					&& serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS") != null) {
				// lavT = (LavoratoreType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
				lavT1 = (ListaDIDType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
				if (lavT1 != null && lavT1.getDisponibilita() != null) {
					dataDid = DateUtils.formatXMLGregorian(lavT1.getDisponibilita());
				}
				/*
				 * if (lavT != null && lavT.getDatiamministrativi() != null &&
				 * lavT.getDatiamministrativi().getStatoinanagrafe() != null &&
				 * lavT.getDatiamministrativi().getStatoinanagrafe().getDisponibilita() != null) { dataDid =
				 * DateUtils.formatXMLGregorian(lavT.getDatiamministrativi().getStatoinanagrafe().getDisponibilita()); }
				 */
			}
		}
		if (!dataDid.equals("")) {
			Object params[] = new Object[3];
			params[0] = new BigDecimal(cdnLavoratore);
			params[1] = dataDid;
			params[2] = dataDid;
			SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CCD_AZIONE_PRESA_CARICO_150", params,
					"SELECT", Values.DB_SIL_DATI);

			if (row != null) {
				row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
				String dataColloquio = (String) row.getAttribute("datcolloquio");
				String dataInizio = (String) row.getAttribute("datavvioazione");
				String dataFine = (String) row.getAttribute("dateffettiva");
				String esito = (String) row.getAttribute("descresito");
				String cpi = (String) row.getAttribute("descrcpi");
				if (dataColloquio != null) {
					response.setAttribute("DATCOLLOQUIO", dataColloquio);
				}
				if (dataInizio != null) {
					response.setAttribute("DATINIZIO", dataInizio);
				}
				if (dataFine != null) {
					response.setAttribute("DATFINE", dataFine);
				}
				if (esito != null) {
					response.setAttribute("ESITO", esito);
				}
				if (cpi != null) {
					response.setAttribute("CPI", cpi);
				}
			}
		}
	}

}
