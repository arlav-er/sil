package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class GetPattoLavMultiSelect extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetPattoLavMultiSelect.class.getName());

	// public GetPattoLavMultiSelect(){ }
	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean patto = doSelectWithStatement(request, response, getStatement(request));
		if (patto != null) {
			patto = patto.containsAttribute("row") ? (SourceBean) patto.getAttribute("row") : patto;
			BigDecimal prgPattoLav = (BigDecimal) patto.getAttribute("PRGPATTOLAVORATORE");
			BigDecimal cdnLavoratore = (BigDecimal) patto.getAttribute("CDNLAVORATORE");
			Vector<String> programmi = PattoBean.checkProgrammi(prgPattoLav, null);
			if (programmi != null && !programmi.isEmpty()) {
				response.setAttribute("MISURE_PROGRAMMI", programmi);
			}
			BigDecimal numProgrammiKO = PattoBean.checkProgrammiApertiSenzaAzioni(cdnLavoratore, null);
			if (numProgrammiKO != null && numProgrammiKO.intValue() > 0) {
				response.setAttribute("MISURE_PROGRAMMI_NO_AZIONI", numProgrammiKO);
			}
		}
	}

	private String getStatement(SourceBean request) throws Exception {
		// Solo se si è in duplicazione
		if (request.containsAttribute("DUPLICA")
				&& request.getAttribute("DUPLICA").toString().equalsIgnoreCase("DUPLICA")) {
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
			request.delAttribute("PRGPATTOLAVORATORE");
			if (sessionContainer.getAttribute("PRGPATTOLAVORATORE") != null) {
				request.setAttribute("PRGPATTOLAVORATORE",
						sessionContainer.getAttribute("PRGPATTOLAVORATORE").toString());
				sessionContainer.delAttribute("PRGPATTOLAVORATORE");
			}
		}
		Object o = request.getAttribute("prgPattoLavoratore");

		if (o instanceof BigDecimal) {
			throw new Exception("il prgPattoLavoratore e' un BigDecimal: era richiesta una String");
		}

		boolean soloPatto297 = request.containsAttribute("SCELTASOLOPATTO297");

		if (soloPatto297) {
			if ((o == null) || ((String) o).equals("")) {
				// ricerca in base al cdnLavoratore
				return "QUERIES.SELECT_NO_ACCORDO_FROM_CDNLAVORATORE";
			} else {
				return "QUERIES.SELECT_NO_ACCORDO_FROM_PRGPATTOLAVORATORE";
			}
		} else {
			if ((o == null) || ((String) o).equals("")) {
				// ricerca in base al cdnLavoratore
				return "QUERIES.SELECT_FROM_CDNLAVORATORE";
			} else {
				return "QUERIES.SELECT_FROM_PRGPATTOLAVORATORE";
			}
		}

	}
}
// class GetPattoLavMultiSelect
