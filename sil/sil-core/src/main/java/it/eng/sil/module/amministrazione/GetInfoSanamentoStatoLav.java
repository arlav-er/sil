package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class GetInfoSanamentoStatoLav extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetInfoSanamentoStatoLav.class.getName());

	/*
	 * Query eseguite: GET_DISPO_INF_STORICHE, GET_ULTIMO_AN_DISPO, GET_STATI_OCCUPAZIONALI, GET_ULTIMOPATTOLAV,
	 * GET_PATTO_INF_STORICHE
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean rows = null;
		try {
			String dataDichiarazione = null;
			setSectionQuerySelect("GET_DISPO_VALIDA");
			rows = doSelect(request, response, false);
			// if (rows==null) throw new Exception("impossibile leggere la
			// dichiarazione di immediata disponibilita' valida");
			if (rows != null && rows.containsAttribute("ROW.datdichiarazione")) {
				dataDichiarazione = (String) rows.getAttribute("ROW.datdichiarazione");
				response.setAttribute("GET_DISPO_VALIDA", rows);
			} else {
				setSectionQuerySelect("GET_DISPO_STORICIZZATE");
				rows = doSelect(request, response, false);
				// if (rows==null) throw new Exception("impossibile leggere le
				// dichiarazione di immediata disponibilita' storicizzate");
				if (rows != null) {
					Vector v = rows.getAttributeAsVector("ROW");
					// if (v.size()==0) throw new Exception("il lavoratore non
					// ha mai stipulato una dichiarazione di immediata
					// disponibilita' con cpi");
					// SourceBean row = (SourceBean)v.get(0);

					// dataDichiarazione =
					// (String)row.getAttribute("datdichiarazione");
					dataDichiarazione = "29/01/2003";
					response.setAttribute("GET_DISPO_STORICIZZATE", rows);
				}
			}
			// ora ho la data a partire dalla quale estrarre gli stati
			// occupazionali
			request.updAttribute("datDichiarazione", /* dataDichiarazione */"01/01/1900");
			setSectionQuerySelect("GET_STATI_OCC");
			rows = doSelect(request, response, false);
			// if (rows==null) throw new Exception("impossibile leggere gli
			// stati occupazionali storicizzati da sanare a partire dalla data
			// "+dataDichiarazione);
			if (rows != null)
				response.setAttribute("GET_STATI_OCC", rows);

			// leggo il patto
			setSectionQuerySelect("GET_PATTO_APERTO");
			rows = doSelect(request, response, false);
			// if (rows==null) throw new Exception("impossibile leggere il patto
			// aperto");
			if (rows != null && rows.containsAttribute("row.prgPattoLavoratore")) {
				response.setAttribute("GET_PATTO_APERTO", rows);
			} else {
				setSectionQuerySelect("GET_PATTI_STORICIZZATI");
				rows = doSelect(request, response, false);
				// if (rows==null) throw new Exception("impossibile leggere i
				// patti del lavoratore storicizzati");
				if (rows != null)
					response.setAttribute("GET_PATTI_STORICIZZATI", rows);
			}
			setSectionQuerySelect("GET_MOVIMENTI");
			rows = doSelect(request, response, false);
			// if (rows==null) throw new Exception("impossibile leggere i
			// movimenti del lavoratore");
			if (rows != null)
				response.setAttribute("GET_MOVIMENTI", rows);
		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			// reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL);
			it.eng.sil.util.TraceWrapper.fatal(_logger, "GetInfoSanamentoStatoLav service() ", e);

		}
	}

}