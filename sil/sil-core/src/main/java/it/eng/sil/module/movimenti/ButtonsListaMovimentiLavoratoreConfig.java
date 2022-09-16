package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class ButtonsListaMovimentiLavoratoreConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ButtonsListaMovimentiLavoratoreConfig.class.getName());

	private String className = this.getClass().getName();

	public ButtonsListaMovimentiLavoratoreConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "MovimentiLavoratore");

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
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			// SourceBean col_11 = new SourceBean("COLUMN");

			col_0.setAttribute("name", "CODSTATOATTO");
			col_0.setAttribute("label", "S<br>t<br>a<br>t<br>o");
			colonneSB.setAttribute(col_0);

			col_1.setAttribute("name", "PROV");
			col_1.setAttribute("label", "Pr<br>ov<br>en<br>ie<br>nza");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "DATAMOV");
			col_2.setAttribute("label", "Data<br>evento");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "codTipoMovVisual");
			col_3.setAttribute("label", "T<br>i<br>p<br>o<br>");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "CODASSCESVISUAL");
			col_4.setAttribute("label", "T.<br>As/<br>Ces");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "CODMONOTEMPO");
			col_5.setAttribute("label", "T<br>e<br>m<br>p<br>o");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "RAGSOCAZ");
			col_6.setAttribute("label", "Rag. Soc.<br>Azienda");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "IndirAzienda");
			col_7.setAttribute("label", "Ind. Azienda");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "CODMONOTIPOFINE");
			col_8.setAttribute("label", "M<br>o<br>v<br>S<br>e<br>g");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "DATFINEMOVEFFETTIVAVIS");
			col_9.setAttribute("label", "Data<br>Fine");
			colonneSB.setAttribute(col_9);

			col_10.setAttribute("name", "MOVRETRIBUZIONE");
			col_10.setAttribute("label", "Redd.<br>Mens<br>&euro;");
			colonneSB.setAttribute(col_10);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PAGE");
			parameterSB1.setAttribute("scope", "");
			parameterSB1.setAttribute("type", "ABSOLUTE");
			parameterSB1.setAttribute("value", "PercorsoMovimentiCollegatiPage");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "prgMovimentoColl");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGMOV");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "cdnFunzione");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "CURRENTCONTEXT");
			parameterSB4.setAttribute("type", "ABSOLUTE");
			parameterSB4.setAttribute("value", "salva");
			parameterSB4.setAttribute("scope", "");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "ACTION");
			parameterSB5.setAttribute("scope", "");
			parameterSB5.setAttribute("type", "ABSOLUTE");
			parameterSB5.setAttribute("value", "naviga");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "PROVENIENZA");
			parameterSB6.setAttribute("scope", "");
			parameterSB6.setAttribute("type", "ABSOLUTE");
			parameterSB6.setAttribute("value", "ListaMov");

			SourceBean parameterSB8 = new SourceBean("PARAMETER");
			parameterSB8.setAttribute("name", "PageRitornoLista");
			parameterSB8.setAttribute("type", "ABSOLUTE");
			parameterSB8.setAttribute("value", "MovimentiLavoratore");
			parameterSB8.setAttribute("scope", "");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			selectCaptionSB.setAttribute(parameterSB8);
			captionsSB.setAttribute(selectCaptionSB);

			// BUTTON
			SourceBean buttonsSB = new SourceBean("BUTTONS");

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Lista Movimenti");
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