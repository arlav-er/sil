package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynRicSediAziendaCongif extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynRicSediAziendaCongif.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		// String prgAzienda = (String)request.getAttribute("PRGAZIENDA");
		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		// PageAttribs attributi = new PageAttribs(user,
		// "IdoListaRichiestePage");
		PageAttribs attributi = new PageAttribs(user, "IdoListaAziendePage");
		String strPageTarget = "";
		// Object elementListGoTo = null;
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = attributi.getGoToes();

		boolean canInsert_Azienda = attributi.containsButton("INSERISCI");

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

			col_1.setAttribute("name", "desTipoAzienda");
			col_1.setAttribute("label", "Tipo");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strCodiceFiscale");
			col_2.setAttribute("label", "CF");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "strPartitaIva");
			col_3.setAttribute("label", "Partita Iva");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "strRagioneSociale");
			col_4.setAttribute("label", "Ragione Sociale");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "strIndirizzo");
			col_5.setAttribute("label", "Indirizzo");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "desStato");
			col_6.setAttribute("label", "Stato Attività");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "flgsede");
			col_7.setAttribute("label", "Sede Legale");
			colonneSB.setAttribute(col_7);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "prgAzienda");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "prgAzienda");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "prgUnita");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "prgUnita");
			captionsSB.setAttribute(parameterSB2);

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "cdnFunzione");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "ret");
			parameterSB4.setAttribute("scope", "ABSOLUTE");
			parameterSB4.setAttribute("value", "IdoListaAziendePage");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "PAGE");
			parameterSB5.setAttribute("scope", "ABSOLUTE");
			parameterSB5.setAttribute("value", "IdoUnitaAziendaPage");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			captionsSB.setAttribute(selectCaptionSB);

			// CAPTION 1
			for (int iList = 0; iList < myGoToes.size(); iList++) {
				goTo = (GoTo) myGoToes.get(iList);
				strPageTarget = goTo.getTargetPage();
				cdnFunzTarget = goTo.getTargetFunz();
				if (cdnFunzTarget != null) {
					strCdnFunzTarget = cdnFunzTarget.toString();
				}
				if (strPageTarget.equalsIgnoreCase("IDOLISTARICHIESTEPAGE")) {
					SourceBean caption1 = new SourceBean("CAPTION");
					caption1.setAttribute("image", "../../img/richieste_az.gif");
					caption1.setAttribute("label", "Lista richieste");
					caption1.setAttribute("confirm", "false");

					SourceBean parameterCaption1 = new SourceBean("PARAMETER");
					parameterCaption1.setAttribute("name", "prgAzienda");
					parameterCaption1.setAttribute("scope", "LOCAL");
					parameterCaption1.setAttribute("type", "RELATIVE");
					parameterCaption1.setAttribute("value", "prgAzienda");

					SourceBean parameterCaption2 = new SourceBean("PARAMETER");
					parameterCaption2.setAttribute("name", "prgUnita");
					parameterCaption2.setAttribute("scope", "LOCAL");
					parameterCaption2.setAttribute("type", "RELATIVE");
					parameterCaption2.setAttribute("value", "prgUnita");

					SourceBean parameterCaption3 = new SourceBean("PARAMETER");
					parameterCaption3.setAttribute("name", "cdnFunzione");
					parameterCaption3.setAttribute("scope", "ABSOLUTE");
					parameterCaption3.setAttribute("value", strCdnFunzTarget);

					SourceBean parameterCaption4 = new SourceBean("PARAMETER");
					parameterCaption4.setAttribute("name", "PAGE");
					parameterCaption4.setAttribute("scope", "ABSOLUTE");
					parameterCaption4.setAttribute("value", strPageTarget);

					SourceBean parameterCaption5 = new SourceBean("PARAMETER");
					parameterCaption5.setAttribute("name", "cerca");
					parameterCaption5.setAttribute("scope", "ABSOLUTE");
					parameterCaption5.setAttribute("value", "cerca");

					caption1.setAttribute(parameterCaption1);
					caption1.setAttribute(parameterCaption2);
					caption1.setAttribute(parameterCaption3);
					caption1.setAttribute(parameterCaption4);
					caption1.setAttribute(parameterCaption5);

					captionsSB.setAttribute(caption1);
				} else {
					if (strPageTarget.equalsIgnoreCase("IDOTESTATARICHIESTAPAGE")) {
						SourceBean caption2 = new SourceBean("CAPTION");
						caption2.setAttribute("image", "../../img/add2.gif");
						caption2.setAttribute("label", "Nuova richiesta");
						caption2.setAttribute("confirm", "false");

						SourceBean parameter1Caption2 = new SourceBean("PARAMETER");
						parameter1Caption2.setAttribute("name", "prgAzienda");
						parameter1Caption2.setAttribute("scope", "LOCAL");
						parameter1Caption2.setAttribute("type", "RELATIVE");
						parameter1Caption2.setAttribute("value", "prgAzienda");

						SourceBean parameter2Caption2 = new SourceBean("PARAMETER");
						parameter2Caption2.setAttribute("name", "prgUnita");
						parameter2Caption2.setAttribute("scope", "LOCAL");
						parameter2Caption2.setAttribute("type", "RELATIVE");
						parameter2Caption2.setAttribute("value", "prgUnita");

						SourceBean parameter3Caption2 = new SourceBean("PARAMETER");
						parameter3Caption2.setAttribute("name", "cdnFunzione");
						parameter3Caption2.setAttribute("scope", "ABSOLUTE");
						parameter3Caption2.setAttribute("value", strCdnFunzTarget);

						SourceBean parameter4Caption2 = new SourceBean("PARAMETER");
						parameter4Caption2.setAttribute("name", "inserisci");
						parameter4Caption2.setAttribute("scope", "ABSOLUTE");
						parameter4Caption2.setAttribute("value", "Inserisci");

						SourceBean parameter5Caption2 = new SourceBean("PARAMETER");
						parameter5Caption2.setAttribute("name", "PAGE");
						parameter5Caption2.setAttribute("scope", "ABSOLUTE");
						parameter5Caption2.setAttribute("value", strPageTarget);

						caption2.setAttribute(parameter1Caption2);
						caption2.setAttribute(parameter2Caption2);
						caption2.setAttribute(parameter3Caption2);
						caption2.setAttribute(parameter4Caption2);
						caption2.setAttribute(parameter5Caption2);

						captionsSB.setAttribute(caption2);
					} else if (strPageTarget.equalsIgnoreCase("MovDettaglioGeneraleInserisciPage")) {
						// Caption per l'aggiunta di un movimento
						SourceBean caption3 = new SourceBean("CAPTION");
						caption3.setAttribute("image", "../../img/ass_movimento.gif");
						caption3.setAttribute("label", "Nuovo movimento");
						caption3.setAttribute("confirm", "false");

						SourceBean parameter1Caption3 = new SourceBean("PARAMETER");
						parameter1Caption3.setAttribute("name", "prgAzienda");
						parameter1Caption3.setAttribute("scope", "LOCAL");
						parameter1Caption3.setAttribute("type", "RELATIVE");
						parameter1Caption3.setAttribute("value", "prgAzienda");

						SourceBean parameter2Caption3 = new SourceBean("PARAMETER");
						parameter2Caption3.setAttribute("name", "prgUnita");
						parameter2Caption3.setAttribute("scope", "LOCAL");
						parameter2Caption3.setAttribute("type", "RELATIVE");
						parameter2Caption3.setAttribute("value", "prgUnita");

						SourceBean parameter3Caption3 = new SourceBean("PARAMETER");
						parameter3Caption3.setAttribute("name", "cdnFunzione");
						parameter3Caption3.setAttribute("scope", "ABSOLUTE");
						parameter3Caption3.setAttribute("value", strCdnFunzTarget);

						SourceBean parameter4Caption3 = new SourceBean("PARAMETER");
						parameter4Caption3.setAttribute("name", "CURRENTCONTEXT");
						parameter4Caption3.setAttribute("type", "ABSOLUTE");
						parameter4Caption3.setAttribute("value", "inserisci");

						SourceBean parameter5Caption3 = new SourceBean("PARAMETER");
						parameter5Caption3.setAttribute("name", "PAGE");
						parameter5Caption3.setAttribute("scope", "ABSOLUTE");
						parameter5Caption3.setAttribute("value", strPageTarget);

						SourceBean parameter6Caption3 = new SourceBean("PARAMETER");
						parameter6Caption3.setAttribute("name", "PROVENIENZA");
						parameter6Caption3.setAttribute("type", "ABSOLUTE");
						parameter6Caption3.setAttribute("value", "azienda");

						SourceBean parameter7Caption3 = new SourceBean("PARAMETER");
						parameter7Caption3.setAttribute("name", "destinazione");
						parameter7Caption3.setAttribute("type", "ABSOLUTE");
						parameter7Caption3.setAttribute("value", "MovDettaglioGeneraleInserisciPage");

						caption3.setAttribute(parameter1Caption3);
						caption3.setAttribute(parameter2Caption3);
						caption3.setAttribute(parameter3Caption3);
						caption3.setAttribute(parameter4Caption3);
						caption3.setAttribute(parameter5Caption3);
						caption3.setAttribute(parameter6Caption3);
						caption3.setAttribute(parameter7Caption3);
						captionsSB.setAttribute(caption3);
					} else if (strPageTarget.equalsIgnoreCase("IdoTestataAziendaPage")) {
						// Caption per l'aggiunta di un movimento
						SourceBean caption4 = new SourceBean("CAPTION");
						caption4.setAttribute("image", "../../img/coop_app.gif");
						caption4.setAttribute("label", "Accorpamento");
						caption4.setAttribute("confirm", "false");

						SourceBean parameter1Caption4 = new SourceBean("PARAMETER");
						parameter1Caption4.setAttribute("name", "prgAzienda");
						parameter1Caption4.setAttribute("scope", "LOCAL");
						parameter1Caption4.setAttribute("type", "RELATIVE");
						parameter1Caption4.setAttribute("value", "prgAzienda");

						SourceBean parameter2Caption4 = new SourceBean("PARAMETER");
						parameter2Caption4.setAttribute("name", "prgUnita");
						parameter2Caption4.setAttribute("scope", "LOCAL");
						parameter2Caption4.setAttribute("type", "RELATIVE");
						parameter2Caption4.setAttribute("value", "prgUnita");

						SourceBean parameter3Caption4 = new SourceBean("PARAMETER");
						parameter3Caption4.setAttribute("name", "cdnFunzione");
						parameter3Caption4.setAttribute("scope", "ABSOLUTE");
						parameter3Caption4.setAttribute("value", strCdnFunzTarget);

						SourceBean parameter4Caption4 = new SourceBean("PARAMETER");
						parameter4Caption4.setAttribute("name", "PAGE");
						parameter4Caption4.setAttribute("scope", "ABSOLUTE");
						parameter4Caption4.setAttribute("value", "IdoTestataAziendaPage");

						SourceBean parameter5Caption4 = new SourceBean("PARAMETER");
						parameter5Caption4.setAttribute("name", "ACCORPAMENTO");
						parameter5Caption4.setAttribute("scope", "ABSOLUTE");
						parameter5Caption4.setAttribute("value", "TRUE");

						caption4.setAttribute(parameter1Caption4);
						caption4.setAttribute(parameter2Caption4);
						caption4.setAttribute(parameter3Caption4);
						caption4.setAttribute(parameter4Caption4);
						caption4.setAttribute(parameter5Caption4);
						captionsSB.setAttribute(caption4);
					}
				}
			}

			// BUTTONS
			SourceBean buttonsSB = new SourceBean("BUTTONS");

			if (canInsert_Azienda) {
				SourceBean button1SB = new SourceBean("INSERT_BUTTON");
				button1SB.setAttribute("name", "inserisci");
				button1SB.setAttribute("label", "Nuova azienda");

				SourceBean parameter1Button1 = new SourceBean("PARAMETER");
				parameter1Button1.setAttribute("name", "PAGE");
				parameter1Button1.setAttribute("type", "ABSOLUTE");
				parameter1Button1.setAttribute("value", "IdoTestataAziendaPage");

				SourceBean parameter2Button1 = new SourceBean("PARAMETER");
				parameter2Button1.setAttribute("name", "cdnFunzione");
				parameter2Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter2Button1.setAttribute("type", "RELATIVE");
				parameter2Button1.setAttribute("value", "cdnFunzione");

				SourceBean parameter3Button1 = new SourceBean("PARAMETER");
				parameter3Button1.setAttribute("name", "inserisci");
				parameter3Button1.setAttribute("type", "ABSOLUTE");
				parameter3Button1.setAttribute("value", "Inserisci");

				button1SB.setAttribute(parameter1Button1);
				button1SB.setAttribute(parameter2Button1);
				button1SB.setAttribute(parameter3Button1);

				buttonsSB.setAttribute(button1SB);
			}

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "LISTA AZIENDE");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);
			if (canInsert_Azienda) {
				configSB.setAttribute(buttonsSB);
			}
			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}// class DynRicSediAziendaCongif
