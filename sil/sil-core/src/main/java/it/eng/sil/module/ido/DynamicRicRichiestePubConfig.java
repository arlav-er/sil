package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicRicRichiestePubConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicRicRichiestePubConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "IdoTestataAziendaPage");
		String strPageTarget = "";
		Object elementListGoTo = null;
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = attributi.getGoToes();

		try {
			SourceBean captionsSB = new SourceBean("CAPTIONS");
			// COLUMNS
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Lista Pubblicazioni");
			configSB.setAttribute("rows", "13");

			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");

			/*
			 * col_1.setAttribute("name", "PRGRICHIESTAAZ"); col_1.setAttribute("label", "Progressivo");
			 */
			col_1.setAttribute("name", "NUMRICHIESTAORIG");
			col_1.setAttribute("label", "Numero Richiesta");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "ANNO");
			col_2.setAttribute("label", "Anno");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRRAGIONESOCIALE");
			col_3.setAttribute("label", "Ragione sociale");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "STRINDIRIZZO");
			col_4.setAttribute("label", "Indirizzo");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "DATPUBBLICAZIONE");
			col_5.setAttribute("label", "Data pubblicazione");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "DATSCADENZAPUBBLICAZIONE");
			col_6.setAttribute("label", "Data scadenza");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "CODMONOCMCATPUBB");
			col_7.setAttribute("label", "Categoria CM");
			colonneSB.setAttribute(col_7);

			configSB.setAttribute(colonneSB);

			for (int iList = 0; iList < myGoToes.size(); iList++) {
				goTo = (GoTo) myGoToes.get(iList);
				strPageTarget = goTo.getTargetPage().toUpperCase();
				cdnFunzTarget = goTo.getTargetFunz();
				if (cdnFunzTarget != null) {
					strCdnFunzTarget = cdnFunzTarget.toString();
				}
				if (strPageTarget.compareTo("IDOTESTATARICHIESTAPAGE") == 0) {
					// CAPTIONS
					SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
					selectCaptionSB.setAttribute("image", "../../img/detail.gif");
					selectCaptionSB.setAttribute("label", "Dettaglio");
					selectCaptionSB.setAttribute("confirm", "false");

					SourceBean parameterSB1 = new SourceBean("PARAMETER");
					parameterSB1.setAttribute("name", "prgRichiestaAZ");
					parameterSB1.setAttribute("scope", "LOCAL");
					parameterSB1.setAttribute("type", "RELATIVE");
					parameterSB1.setAttribute("value", "prgRichiestaAz");

					SourceBean parameterSB2 = new SourceBean("PARAMETER");
					parameterSB2.setAttribute("name", "PAGE");
					parameterSB2.setAttribute("scope", "ABSOLUTE");
					parameterSB2.setAttribute("value", strPageTarget);

					SourceBean parameterSB3 = new SourceBean("PARAMETER");
					parameterSB3.setAttribute("name", "cdnFunzione");
					parameterSB3.setAttribute("scope", "ABSOLUTE");
					parameterSB3.setAttribute("value", strCdnFunzTarget);

					SourceBean parameterSB4 = new SourceBean("PARAMETER");
					parameterSB4.setAttribute("name", "ret");
					parameterSB4.setAttribute("type", "ABSOLUTE");
					parameterSB4.setAttribute("value", "IdoListaPubbPage");

					SourceBean parameterSB5 = new SourceBean("PARAMETER");
					parameterSB5.setAttribute("name", "prgAzienda");
					parameterSB5.setAttribute("scope", "LOCAL");
					parameterSB5.setAttribute("type", "RELATIVE");
					parameterSB5.setAttribute("value", "prgAzienda");

					SourceBean parameterSB6 = new SourceBean("PARAMETER");
					parameterSB6.setAttribute("name", "prgUnita");
					parameterSB6.setAttribute("scope", "LOCAL");
					parameterSB6.setAttribute("type", "RELATIVE");
					parameterSB6.setAttribute("value", "prgUnita");

					selectCaptionSB.setAttribute(parameterSB1);
					selectCaptionSB.setAttribute(parameterSB2);
					selectCaptionSB.setAttribute(parameterSB3);
					selectCaptionSB.setAttribute(parameterSB4);
					selectCaptionSB.setAttribute(parameterSB5);
					selectCaptionSB.setAttribute(parameterSB6);
					captionsSB.setAttribute(selectCaptionSB);

				}
			}

			// CAPTION DETTAGLIO SINTETICO
			SourceBean captionDettaglio = new SourceBean("CAPTION");
			captionDettaglio.setAttribute("image", "../../img/info_soggetto.gif");
			captionDettaglio.setAttribute("label", "Dettaglio Sintetico");
			captionDettaglio.setAttribute("confirm", "false");

			SourceBean parameter1CaptionDettaglio = new SourceBean("PARAMETER");
			parameter1CaptionDettaglio.setAttribute("name", "prgRichiestaAZ");
			parameter1CaptionDettaglio.setAttribute("scope", "LOCAL");
			parameter1CaptionDettaglio.setAttribute("type", "RELATIVE");
			parameter1CaptionDettaglio.setAttribute("value", "prgRichiestaAZ");

			SourceBean parameter2CaptionDettaglio = new SourceBean("PARAMETER");
			parameter2CaptionDettaglio.setAttribute("name", "cdnFunzione");
			parameter2CaptionDettaglio.setAttribute("scope", "SERVICE_REQUEST");
			parameter2CaptionDettaglio.setAttribute("type", "RELATIVE");
			parameter2CaptionDettaglio.setAttribute("value", "cdnFunzione");

			SourceBean parameter3CaptionDettaglio = new SourceBean("PARAMETER");
			parameter3CaptionDettaglio.setAttribute("name", "PAGE");
			parameter3CaptionDettaglio.setAttribute("type", "ABSOLUTE");
			parameter3CaptionDettaglio.setAttribute("value", "IdoDettaglioSinteticoPage");

			captionDettaglio.setAttribute(parameter1CaptionDettaglio);
			captionDettaglio.setAttribute(parameter2CaptionDettaglio);
			captionDettaglio.setAttribute(parameter3CaptionDettaglio);

			captionsSB.setAttribute(captionDettaglio);
			configSB.setAttribute(captionsSB);

		}

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}

		return configSB;
	}
}