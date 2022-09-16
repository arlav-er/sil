package it.eng.sil.module.ido;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

public class DynamicRicRichiesteConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicRicRichiesteConfig.class.getName());

	public DynamicRicRichiesteConfig() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		// System.out.println(getSessionContainer().toXML());

		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "IdoListaRichiestePage");

		boolean canInsert_Richiesta = attributi.containsButton("INSERISCI_RICHIESTA");
		boolean canReitera_Richiesta = attributi.containsButton("REITERA");
		boolean canMatch = attributi.containsButton("MATCHING");

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

			col_1.setAttribute("name", "numrich");
			col_1.setAttribute("label", "Numero richiesta");
			colonneSB.setAttribute(col_1);

			// col_2.setAttribute("name", "numanno");
			// col_2.setAttribute("label", "Anno");
			// colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "strRagioneSociale");
			col_3.setAttribute("label", "Azienda");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "strIndirizzo");
			col_4.setAttribute("label", "Via");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "descomune");
			col_5.setAttribute("label", "Comune");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "datRichiesta");
			col_6.setAttribute("label", "Data richiesta");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "datScadenza");
			col_7.setAttribute("label", "Data scadenza");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "viewMatchBtn");
			col_8.setAttribute("label", "");
			col_8.setAttribute("notVisible", "");
			colonneSB.setAttribute(col_8);

			// INIT-PARTE-TEMP
			if (Sottosistema.AS.isOff()) {
				// END-PARTE-TEMP

				// INIT-PARTE-TEMP
			} else {
				SourceBean col_9 = new SourceBean("COLUMN");
				SourceBean col_10 = new SourceBean("COLUMN");

				col_9.setAttribute("name", "EVASIONE");
				col_9.setAttribute("label", "Modalità \n Evasione");
				colonneSB.setAttribute(col_9);

				col_10.setAttribute("name", "STATOEV");
				col_10.setAttribute("label", "Stato \n evasione");
				colonneSB.setAttribute(col_10);
				// END-PARTE-TEMP

				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

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
			parameterSB2.setAttribute("type", "ABSOLUTE");
			parameterSB2.setAttribute("value", "IdoTestataRichiestaPage");
			captionsSB.setAttribute(parameterSB2);

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "cdnFunzione");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "ret");
			parameterSB4.setAttribute("type", "ABSOLUTE");
			parameterSB4.setAttribute("value", "IdoListaRichiestePage");

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

			// CAPTION 1
			SourceBean caption1 = new SourceBean("CAPTION");
			caption1.setAttribute("image", "../../img/Reitera.gif");
			caption1.setAttribute("label", "Reitera");
			caption1.setAttribute("confirm", "false");

			SourceBean parameter1Caption1 = new SourceBean("PARAMETER");
			parameter1Caption1.setAttribute("name", "prgRichiestaAZ");
			parameter1Caption1.setAttribute("scope", "LOCAL");
			parameter1Caption1.setAttribute("type", "RELATIVE");
			parameter1Caption1.setAttribute("value", "prgRichiestaAZ");

			SourceBean parameter2Caption1 = new SourceBean("PARAMETER");
			parameter2Caption1.setAttribute("name", "reiteraRichAz");
			parameter2Caption1.setAttribute("value", "true");

			SourceBean parameter3Caption1 = new SourceBean("PARAMETER");
			parameter3Caption1.setAttribute("name", "cdnFunzione");
			parameter3Caption1.setAttribute("scope", "SERVICE_REQUEST");
			parameter3Caption1.setAttribute("type", "RELATIVE");
			parameter3Caption1.setAttribute("value", "cdnFunzione");

			SourceBean parameter4Caption1 = new SourceBean("PARAMETER");
			parameter4Caption1.setAttribute("name", "PAGE");
			parameter4Caption1.setAttribute("type", "ABSOLUTE");
			parameter4Caption1.setAttribute("value", "IdoTestataRichiestaPage");

			SourceBean parameter5Caption1 = new SourceBean("PARAMETER");
			parameter5Caption1.setAttribute("name", "ret");
			parameter5Caption1.setAttribute("type", "ABSOLUTE");
			parameter5Caption1.setAttribute("value", "IdoListaRichiestePage");

			caption1.setAttribute(parameter1Caption1);
			caption1.setAttribute(parameter2Caption1);
			caption1.setAttribute(parameter3Caption1);
			caption1.setAttribute(parameter4Caption1);
			caption1.setAttribute(parameter5Caption1);

			if (canReitera_Richiesta)
				captionsSB.setAttribute(caption1);

			// CAPTION 2 - MATCHING
			SourceBean caption2 = new SourceBean("CAPTION");
			caption2.setAttribute("image", "../../img/incrocio.gif");
			caption2.setAttribute("label", "Gestione Incrocio");
			caption2.setAttribute("confirm", "false");
			caption2.setAttribute("hiddenColumn", "viewMatchBtn");
			// PAGE
			SourceBean parameter1Caption2 = new SourceBean("PARAMETER");
			parameter1Caption2.setAttribute("name", "PAGE");
			parameter1Caption2.setAttribute("type", "ABSOLUTE");
			parameter1Caption2.setAttribute("value", "GestIncrocioPage");
			// parameter1Caption2.setAttribute("value", "GestIncrocioPage");
			// prgRichiestaAz
			SourceBean parameter2Caption2 = new SourceBean("PARAMETER");
			parameter2Caption2.setAttribute("name", "prgRichiestaAZ");
			parameter2Caption2.setAttribute("scope", "LOCAL");
			parameter2Caption2.setAttribute("type", "RELATIVE");
			parameter2Caption2.setAttribute("value", "prgRichiestaAZ");
			// prgAzienda
			SourceBean parameter3Caption2 = new SourceBean("PARAMETER");
			parameter3Caption2.setAttribute("name", "prgAzienda");
			parameter3Caption2.setAttribute("scope", "LOCAL");
			parameter3Caption2.setAttribute("type", "RELATIVE");
			parameter3Caption2.setAttribute("value", "prgAzienda");
			// prgUnita
			SourceBean parameter4Caption2 = new SourceBean("PARAMETER");
			parameter4Caption2.setAttribute("name", "prgUnita");
			parameter4Caption2.setAttribute("scope", "LOCAL");
			parameter4Caption2.setAttribute("type", "RELATIVE");
			parameter4Caption2.setAttribute("value", "prgUnita");
			// cdnFunzione
			SourceBean parameter5Caption2 = new SourceBean("PARAMETER");
			parameter5Caption2.setAttribute("name", "cdnFunzione");
			parameter5Caption2.setAttribute("scope", "SERVICE_REQUEST");
			parameter5Caption2.setAttribute("type", "RELATIVE");
			parameter5Caption2.setAttribute("value", "cdnFunzione");
			// prgOrig -> uguale a prgRichiestaAz, serve per i moduli seguenti
			SourceBean parameter6Caption2 = new SourceBean("PARAMETER");
			parameter6Caption2.setAttribute("name", "PRGORIG");
			parameter6Caption2.setAttribute("scope", "LOCAL");
			parameter6Caption2.setAttribute("type", "RELATIVE");
			parameter6Caption2.setAttribute("value", "prgRichiestaAZ");
			SourceBean parameter7Caption2 = new SourceBean("PARAMETER");
			parameter7Caption2.setAttribute("name", "StoricizzaRichiesta");
			parameter7Caption2.setAttribute("type", "ABSOLUTE");
			parameter7Caption2.setAttribute("value", "true");

			caption2.setAttribute(parameter1Caption2);
			caption2.setAttribute(parameter2Caption2);
			caption2.setAttribute(parameter3Caption2);
			caption2.setAttribute(parameter4Caption2);
			caption2.setAttribute(parameter5Caption2);
			caption2.setAttribute(parameter6Caption2);
			caption2.setAttribute(parameter7Caption2);
			if (canMatch)
				captionsSB.setAttribute(caption2);

			// BUTTONS
			SourceBean buttonsSB = new SourceBean("BUTTONS");

			SourceBean button1SB = new SourceBean("INSERT_BUTTON");
			button1SB.setAttribute("name", "inserisci");
			button1SB.setAttribute("label", "Nuova richiesta");

			SourceBean parameter1Button1 = new SourceBean("PARAMETER");
			parameter1Button1.setAttribute("name", "PAGE");
			parameter1Button1.setAttribute("type", "ABSOLUTE");
			parameter1Button1.setAttribute("value", "IdoTestataRichiestaPage");

			SourceBean parameter2Button1 = new SourceBean("PARAMETER");
			parameter2Button1.setAttribute("name", "cdnFunzione");
			parameter2Button1.setAttribute("scope", "SERVICE_REQUEST");
			parameter2Button1.setAttribute("type", "RELATIVE");
			parameter2Button1.setAttribute("value", "cdnFunzione");

			SourceBean parameter3Button1 = new SourceBean("PARAMETER");
			parameter3Button1.setAttribute("name", "inserisci");
			parameter3Button1.setAttribute("type", "ABSOLUTE");
			parameter3Button1.setAttribute("value", "inserisci");

			SourceBean parameter4Button1 = new SourceBean("PARAMETER");
			parameter4Button1.setAttribute("name", "prgAzienda");
			parameter4Button1.setAttribute("scope", "SERVICE_REQUEST");
			parameter4Button1.setAttribute("type", "RELATIVE");
			parameter4Button1.setAttribute("value", "prgAzienda");

			SourceBean parameter5Button1 = new SourceBean("PARAMETER");
			parameter5Button1.setAttribute("name", "prgUnita");
			parameter5Button1.setAttribute("scope", "SERVICE_REQUEST");
			parameter5Button1.setAttribute("type", "RELATIVE");
			parameter5Button1.setAttribute("value", "prgUnita");

			button1SB.setAttribute(parameter1Button1);
			button1SB.setAttribute(parameter2Button1);
			button1SB.setAttribute(parameter3Button1);
			button1SB.setAttribute(parameter4Button1);
			button1SB.setAttribute(parameter5Button1);

			buttonsSB.setAttribute(button1SB);

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "");
			configSB.setAttribute("rows", "13");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			if (canInsert_Richiesta)
				configSB.setAttribute(buttonsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}