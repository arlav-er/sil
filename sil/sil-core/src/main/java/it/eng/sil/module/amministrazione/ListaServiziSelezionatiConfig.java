package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.tags.AbstractConfigProvider;

public class ListaServiziSelezionatiConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CollMiratoRicercaListConfig.class.getName());

	private String className = this.getClass().getName();

	public ListaServiziSelezionatiConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		try {
			SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery("GET_CODREGIONE", null, "SELECT", "SIL_DATI");
			String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");

			if (!regione.equals("2")) {
				col_1.setAttribute("name", "codice");
				col_1.setAttribute("label", "Codice servizio");
				colonneSB.setAttribute(col_1);
			}

			col_2.setAttribute("name", "descrizione");
			col_2.setAttribute("label", "Servizio");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRSPECIFICA");
			col_3.setAttribute("label", "Specifica");
			colonneSB.setAttribute(col_3);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "codServizio");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "codice");

			selectCaptionSB.setAttribute(parameterSB1);
			captionsSB.setAttribute(selectCaptionSB);

			SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteCaptionSB.setAttribute("confirm", "TRUE");

			SourceBean deleteParameterSB1 = new SourceBean("PARAMETER");
			deleteParameterSB1.setAttribute("name", "PAGE");
			deleteParameterSB1.setAttribute("type", "ABSOLUTE");
			deleteParameterSB1.setAttribute("value", "CMServiziPage");
			deleteParameterSB1.setAttribute("scope", "");

			SourceBean deleteParameterSB2 = new SourceBean("PARAMETER");
			deleteParameterSB2.setAttribute("name", "MODULE");
			deleteParameterSB2.setAttribute("type", "ABSOLUTE");
			deleteParameterSB2.setAttribute("value", "M_Delete_Servizio");
			deleteParameterSB2.setAttribute("scope", "");

			SourceBean deleteParameterSB3 = new SourceBean("PARAMETER");
			deleteParameterSB3.setAttribute("name", "eliminaServizio");
			deleteParameterSB3.setAttribute("type", "ABSOLUTE");
			deleteParameterSB3.setAttribute("value", "1");
			deleteParameterSB3.setAttribute("scope", "");

			SourceBean deleteParameterSB4 = new SourceBean("PARAMETER");
			deleteParameterSB4.setAttribute("name", "cdnLavoratore");
			deleteParameterSB4.setAttribute("type", "RELATIVE");
			deleteParameterSB4.setAttribute("value", "cdnLavoratore");
			deleteParameterSB4.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean deleteParameterSB5 = new SourceBean("PARAMETER");
			deleteParameterSB5.setAttribute("name", "cdnFunzione");
			deleteParameterSB5.setAttribute("type", "RELATIVE");
			deleteParameterSB5.setAttribute("value", "cdnFunzione");
			deleteParameterSB5.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean deleteParameterSB6 = new SourceBean("PARAMETER");
			deleteParameterSB6.setAttribute("name", "codServizio");
			deleteParameterSB6.setAttribute("type", "RELATIVE");
			deleteParameterSB6.setAttribute("value", "codice");
			deleteParameterSB6.setAttribute("scope", "LOCAL");

			deleteCaptionSB.setAttribute(deleteParameterSB1);
			deleteCaptionSB.setAttribute(deleteParameterSB2);
			deleteCaptionSB.setAttribute(deleteParameterSB3);
			deleteCaptionSB.setAttribute(deleteParameterSB4);
			deleteCaptionSB.setAttribute(deleteParameterSB5);
			deleteCaptionSB.setAttribute(deleteParameterSB6);

			captionsSB.setAttribute(deleteCaptionSB);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
