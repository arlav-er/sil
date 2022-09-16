package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicListaSAPConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListaSAPConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "codMinSap");
			col_1.setAttribute("label", "Codice<br>&nbsp;Ministeriale");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "Lavoratore");
			col_2.setAttribute("label", "CF Cognome Nome Lavoratore");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "descStatoSap");
			col_3.setAttribute("label", "Stato Sap");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "CPI");
			col_4.setAttribute("label", "CPI");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "datInizioVal");
			col_5.setAttribute("label", "Data<br>&nbsp;Inizio<br>&nbsp;Validit&agrave;");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "datFineVal");
			col_6.setAttribute("label", "Data<br>&nbsp;Fine<br>&nbsp;Validit&agrave;");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "dtmMod");
			col_7.setAttribute("label", "Data modifica");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "Utente");
			col_8.setAttribute("label", "Utente modifica");
			colonneSB.setAttribute(col_8);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB1 = new SourceBean("SELECT_CAPTION");
			selectCaptionSB1.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB1.setAttribute("label", "Dettaglio");
			selectCaptionSB1.setAttribute("confirm", "false");

			SourceBean parameterSB11 = new SourceBean("PARAMETER");
			parameterSB11.setAttribute("name", "PAGE");
			parameterSB11.setAttribute("scope", "ABSOLUTE");
			parameterSB11.setAttribute("value", "SapGestioneServiziPage");

			SourceBean parameterSB12 = new SourceBean("PARAMETER");
			parameterSB12.setAttribute("name", "cdnLavoratore");
			parameterSB12.setAttribute("scope", "LOCAL");
			parameterSB12.setAttribute("type", "RELATIVE");
			parameterSB12.setAttribute("value", "cdnLavoratore");

			SourceBean parameterSB13 = new SourceBean("PARAMETER");
			parameterSB13.setAttribute("name", "cdnFunzione");
			parameterSB13.setAttribute("scope", "ABSOLUTE");
			parameterSB13.setAttribute("value", "1");

			selectCaptionSB1.setAttribute(parameterSB11);
			selectCaptionSB1.setAttribute(parameterSB12);
			selectCaptionSB1.setAttribute(parameterSB13);
			captionsSB.setAttribute(selectCaptionSB1);

			SourceBean selectCaptionSB2 = new SourceBean("CAPTION");
			selectCaptionSB2.setAttribute("image", "../../img/text.gif");
			selectCaptionSB2.setAttribute("label", "Notifiche");
			selectCaptionSB2.setAttribute("confirm", "false");

			SourceBean parameterSB21 = new SourceBean("PARAMETER");
			parameterSB21.setAttribute("name", "PAGE");
			parameterSB21.setAttribute("scope", "ABSOLUTE");
			parameterSB21.setAttribute("value", "SAPNotificheLavPage");

			SourceBean parameterSB22 = new SourceBean("PARAMETER");
			parameterSB22.setAttribute("name", "cdnFunzione");
			parameterSB22.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB22.setAttribute("type", "RELATIVE");
			parameterSB22.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB23 = new SourceBean("PARAMETER");
			parameterSB23.setAttribute("name", "codMinSap");
			parameterSB23.setAttribute("scope", "LOCAL");
			parameterSB23.setAttribute("type", "RELATIVE");
			parameterSB23.setAttribute("value", "codMinSap");

			selectCaptionSB2.setAttribute(parameterSB21);
			selectCaptionSB2.setAttribute(parameterSB22);
			selectCaptionSB2.setAttribute(parameterSB23);
			captionsSB.setAttribute(selectCaptionSB2);

			SessionContainer session = getSessionContainer();
			User utenteConnesso = (User) session.getAttribute(User.USERID);
			PageAttribs attributi = new PageAttribs(utenteConnesso, request.getAttribute("PAGE").toString());
			boolean canViewXML = attributi.containsButton("VISUALIZZA_XML");
			if (canViewXML) {
				SourceBean selectCaptionSB3 = new SourceBean("CAPTION");
				selectCaptionSB3.setAttribute("hiddenColumn", "prgestrazionehiddencolumn");
				selectCaptionSB3.setAttribute("image", "../../img/giu.gif");
				selectCaptionSB3.setAttribute("label", "Visualizza XML Errore");
				selectCaptionSB3.setAttribute("confirm", "false");

				SourceBean parameterSB31 = new SourceBean("PARAMETER");
				parameterSB31.setAttribute("name", "PAGE");
				parameterSB31.setAttribute("scope", "ABSOLUTE");
				parameterSB31.setAttribute("value", "SAPErroriPage");

				SourceBean parameterSB32 = new SourceBean("PARAMETER");
				parameterSB32.setAttribute("name", "cdnFunzione");
				parameterSB32.setAttribute("scope", "SERVICE_REQUEST");
				parameterSB32.setAttribute("type", "RELATIVE");
				parameterSB32.setAttribute("value", "cdnFunzione");

				SourceBean parameterSB33 = new SourceBean("PARAMETER");
				parameterSB33.setAttribute("name", "cdnLavoratore");
				parameterSB33.setAttribute("scope", "LOCAL");
				parameterSB33.setAttribute("type", "RELATIVE");
				parameterSB33.setAttribute("value", "cdnLavoratore");

				selectCaptionSB3.setAttribute(parameterSB31);
				selectCaptionSB3.setAttribute(parameterSB32);
				selectCaptionSB3.setAttribute(parameterSB33);
				captionsSB.setAttribute(selectCaptionSB3);
			}

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			if (_logger.isDebugEnabled())
				_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}