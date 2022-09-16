package it.eng.sil.module.cig;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class ListaScadenzeCigConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListaScadenzeCigConfig.class.getName());

	private String className = this.getClass().getName();

	public ListaScadenzeCigConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "");

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_0 = new SourceBean("COLUMN");
			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");

			col_0.setAttribute("name", "STRCOGNOME");
			col_0.setAttribute("label", "Cognome");
			colonneSB.setAttribute(col_0);

			col_1.setAttribute("name", "STRNOME");
			col_1.setAttribute("label", "Nome");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "STRCODICEFISCALE");
			col_2.setAttribute("label", "Codice Fiscale");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "DATNASC");
			col_3.setAttribute("label", "Data Nascita");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "ISCRIZIONE");
			col_4.setAttribute("label", "Iscrizione");
			colonneSB.setAttribute(col_4);

			col_7.setAttribute("name", "tipoIscr");
			col_7.setAttribute("label", "Tipo Iscr");
			colonneSB.setAttribute(col_7);

			col_5.setAttribute("name", "App");
			col_5.setAttribute("label", "App.to");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "Con");
			col_6.setAttribute("label", "Con.to");
			colonneSB.setAttribute(col_6);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "cdnlavoratore");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "cdnlavoratore");

			selectCaptionSB.setAttribute(parameterSB2);

			captionsSB.setAttribute(selectCaptionSB);

			// Caption Appuntamento
			/*
			 * SourceBean addCaptionSB = new SourceBean("SELECT_CAPTION"); addCaptionSB.setAttribute("image", "");
			 * addCaptionSB.setAttribute("label", "App.to"); addCaptionSB.setAttribute("confirm", "false");
			 * 
			 * SourceBean addParameterSB1 = new SourceBean("PARAMETER"); addParameterSB1.setAttribute("name", "PAGE");
			 * addParameterSB1.setAttribute("scope", ""); addParameterSB1.setAttribute("type", "ABSOLUTE");
			 * addParameterSB1.setAttribute("value", "ScadAppuntamentoPage");
			 * 
			 * SourceBean addParameterSB2 = new SourceBean("PARAMETER"); addParameterSB2.setAttribute("name",
			 * "CDNLAVORATORE"); addParameterSB2.setAttribute("scope", "LOCAL"); addParameterSB2.setAttribute("type",
			 * "RELATIVE"); addParameterSB2.setAttribute("value", "CDNLAVORATORE");
			 */

			// Caption Contatto
			/*
			 * SourceBean addCaptionSB3 = new SourceBean("DELETE_CAPTION"); addCaptionSB3.setAttribute("image",
			 * "../../img/contatti.gif"); addCaptionSB3.setAttribute("label", "Con.to");
			 * addCaptionSB3.setAttribute("confirm", "false");
			 * 
			 * SourceBean addParameterSB4 = new SourceBean("PARAMETER"); addParameterSB4.setAttribute("name", "PAGE");
			 * addParameterSB4.setAttribute("scope", ""); addParameterSB4.setAttribute("type", "ABSOLUTE");
			 * addParameterSB4.setAttribute("value", "ScadContattoPage");
			 * 
			 * SourceBean addParameterSB5 = new SourceBean("PARAMETER"); addParameterSB5.setAttribute("name",
			 * "CDNLAVORATORE"); addParameterSB5.setAttribute("scope", "LOCAL"); addParameterSB5.setAttribute("type",
			 * "RELATIVE"); addParameterSB5.setAttribute("value", "CDNLAVORATORE");
			 * 
			 * addCaptionSB.setAttribute(addParameterSB1); addCaptionSB.setAttribute(addParameterSB2);
			 * addCaptionSB.setAttribute(addCaptionSB3); addCaptionSB.setAttribute(addParameterSB4);
			 * addCaptionSB.setAttribute(addParameterSB5); captionsSB.setAttribute(addCaptionSB);
			 */

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Scadenze CIG/MB in Deroga");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}

		return configSB;
	}
}
