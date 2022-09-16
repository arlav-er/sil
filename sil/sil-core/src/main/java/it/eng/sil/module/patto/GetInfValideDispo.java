package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetInfValideDispo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetInfValideDispo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		try {
			SourceBean row = null;
			SourceBean prgElAn = null;
			SourceBean ret = new SourceBean("ROWS");
			SourceBean ret1 = new SourceBean("ROW");
			ret.setAttribute(ret1);
			// Seleziona oil prgelencoanagrafico a partire dal cdnlavoratore
			setSectionQuerySelect("SELECT_PRGELANAG_DA_CDNLAV_DISPO");
			prgElAn = doSelect(request, response, false);

			if (prgElAn.getAttribute("row.prgelencoanagrafico") != null) {
				request.delAttribute("PRGELENCOANAGRAFICO");
				request.setAttribute("PRGELENCOANAGRAFICO", prgElAn.getAttribute("row.prgelencoanagrafico").toString());

				setSectionQuerySelect("QUERY_EA");
				row = doSelect(request, response, false);

				if (row.getAttribute("row.prgelencoanagrafico") != null) {
					Object prgelencoAnag = row.getAttribute("row.prgelencoanagrafico");
					Object datInizio = row.getAttribute("row.datInizio");
					Object descCPI = row.getAttribute("row.descCPI");
					Object codCpiTit = row.getAttribute("row.CODCPI");
					ret1.setAttribute("prgelencoanagrafico", prgelencoAnag);
					ret1.setAttribute("datInizio", datInizio);
					if (descCPI != null)
						ret1.setAttribute("CPITITOLARE", descCPI);
					if (codCpiTit != null)
						ret1.setAttribute("codCpiTit", codCpiTit);
				}
			}

			setSectionQuerySelect("QUERY_STATO_OCC");
			row = doSelect(request, response, false);

			if (row.getAttribute("row.prgstatoOccupaz") != null) {
				Object prgstatoOccupaz = row.getAttribute("row.prgstatoOccupaz");
				Object descrizione = row.getAttribute("row.descrizionestato");
				Object codstatooccupazragg = row.getAttribute("row.codstatooccupazragg");
				ret1.setAttribute("prgstatoOccupaz", prgstatoOccupaz);
				ret1.setAttribute("descrizionestato", descrizione);
				ret1.setAttribute("codstatooccupazragg", codstatooccupazragg);
			}
			setSectionQuerySelect("QUERY_CM_ISCR");
			row = doSelect(request, response, false);
			if (row != null) {
				Vector vettCM = row.getAttributeAsVector("ROW");
				if (vettCM.size() > 0) {
					SourceBean rowCMLast = (SourceBean) vettCM.get(0);
					if (rowCMLast.getAttribute("PRGCMISCR") != null) {
						Object dataInizio = rowCMLast.getAttribute("datInizio");
						Object codAccertamentoSanitario = rowCMLast.getAttribute("CODACCERTSANITARIO");
						ret1.setAttribute("dataInizioCM", dataInizio);
						if (codAccertamentoSanitario != null)
							ret1.setAttribute("CODACCERTSANITARIO", codAccertamentoSanitario);
					}
				}
			}
			response.setAttribute(ret);
			/*
			 * it.eng.sil.util.TraceWrapper.debug( _logger,className + "::select: rowsSourceBean", rowsSourceBean);
			 * 
			 */
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		}
		// catch (Exception ex)
	}
	// end service
}
// end class GetInfValideDispo
