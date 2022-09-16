package it.eng.sil.module.anag;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynRicDidInpsConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynRicDidInpsConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "DidInpsRicercaPage");

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_2b = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "strCodiceFiscalelav");
			col_1.setAttribute("label", "CF");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "datDichiarazione");
			col_2.setAttribute("label", "Data Dichiarazione");
			colonneSB.setAttribute(col_2);

			col_2b.setAttribute("name", "STRDESCRIZIONEMIN");
			col_2b.setAttribute("label", "CPI");
			colonneSB.setAttribute(col_2b);

			col_3.setAttribute("name", "CODMONOTIPOOPERAZIONE");
			col_3.setAttribute("label", "TIPO");
			colonneSB.setAttribute(col_3);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			/*
			 * SourceBean parameterSB1 = new SourceBean("PARAMETER"); parameterSB1.setAttribute("name",
			 * "cdnLavoratore"); parameterSB1.setAttribute("scope", "LOCAL"); parameterSB1.setAttribute("type",
			 * "RELATIVE"); parameterSB1.setAttribute("value", "cdnLavoratore");
			 */

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "cdnFunzione");
			parameterSB2.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "MODULE");
			parameterSB3.setAttribute("scope", "ABSOLUTE");
			parameterSB3.setAttribute("value", "M_GetDidInps");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "PAGE");
			parameterSB4.setAttribute("scope", "ABSOLUTE");
			parameterSB4.setAttribute("value", "DidInpsDettaglioPage");

			// POPUP EVIDENZE
			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "APRI_EV");
			parameterSB5.setAttribute("scope", "ABSOLUTE");
			parameterSB5.setAttribute("value", "1");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "prgDidInps");
			parameterSB6.setAttribute("scope", "LOCAL");
			parameterSB6.setAttribute("type", "RELATIVE");
			parameterSB6.setAttribute("value", "prgDidInps");

			// selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			// POPUP EVIDENZE
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			captionsSB.setAttribute(selectCaptionSB);

			// BUTTONS
			SourceBean buttonsSB = new SourceBean("BUTTONS");

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "LISTA DID INPS");
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
