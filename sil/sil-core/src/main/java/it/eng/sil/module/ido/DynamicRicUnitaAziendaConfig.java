package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicRicUnitaAziendaConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicRicUnitaAziendaConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String prgAzienda = (String) request.getAttribute("PRGAZIENDA");
		/*
		 * paramentro utilizzato quando dai movimenti inserisco una nuova sede aziendale
		 */
		String funzioneaggiornamentounita = request.containsAttribute("AGG_FUNZ_INS_UNITA")
				? request.getAttribute("AGG_FUNZ_INS_UNITA").toString()
				: "";
		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "IdoTestataAziendaPage");
		String strPageTarget = "";
		Object elementListGoTo = null;
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = attributi.getGoToes();

		boolean canInsert_Unita = attributi.containsButton("INSERISCI_UNITA");
		// Andrea 23/02/2005
		// il pulsante INSERISCI_RICHIESTA e' stato spostato perche' basta la
		// gestione del salto (vedi attributi.getGoToes()).
		// Se l'utente ha i permessi per arrivare alla pagina di inserimento di
		// una nuova richiesta
		// allora avra' il permesso per fare il salto alla funzione relativa.
		// boolean canInsert_Richiesta=
		// attributi.containsButton("INSERISCI_RICHIESTA");

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "desStato");
			col_1.setAttribute("label", "Stato attività");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strIndirizzo");
			col_2.setAttribute("label", "Indirizzo");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "descomune");
			col_3.setAttribute("label", "Comune");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "flgsede");
			col_4.setAttribute("label", "Sede Legale");
			colonneSB.setAttribute(col_4);

			// CHECKBOXES
			SourceBean checkBoxSB = new SourceBean("CHECKBOXES");

			SourceBean cb_1 = new SourceBean("CHECKBOX");

			cb_1.setAttribute("name", "Seleziona");
			cb_1.setAttribute("label", "Seleziona");
			cb_1.setAttribute("jsCheckBoxClick", "SelUnitaClick");
			cb_1.setAttribute("refColumn", "PRGUNITA");

			SourceBean parameterCB1 = new SourceBean("PARAMETER");
			parameterCB1.setAttribute("name", "prgUnita");
			parameterCB1.setAttribute("scope", "LOCAL");
			parameterCB1.setAttribute("type", "RELATIVE");
			parameterCB1.setAttribute("value", "prgUnita");

			cb_1.setAttribute(parameterCB1);
			checkBoxSB.setAttribute(cb_1);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "prgAzienda");
			parameterSB1.setAttribute("scope", "SERVICE_REQUEST");
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
			parameterSB4.setAttribute("name", "PAGE");
			parameterSB4.setAttribute("scope", "ABSOLUTE");
			parameterSB4.setAttribute("value", "IdoUnitaAziendaPage");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "AGG_FUNZ_INS_UNITA");
			parameterSB5.setAttribute("scope", "ABSOLUTE");
			parameterSB5.setAttribute("value", funzioneaggiornamentounita);

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			captionsSB.setAttribute(selectCaptionSB);

			if (funzioneaggiornamentounita.equals("")) {
				// CAPTION 1
				for (int iList = 0; iList < myGoToes.size(); iList++) {
					goTo = (GoTo) myGoToes.get(iList);
					strPageTarget = goTo.getTargetPage().toUpperCase();
					cdnFunzTarget = goTo.getTargetFunz();
					if (cdnFunzTarget != null) {
						strCdnFunzTarget = cdnFunzTarget.toString();
					}
					if (strPageTarget.compareTo("IDOLISTARICHIESTEPAGE") == 0) {
						SourceBean caption1 = new SourceBean("CAPTION");
						caption1.setAttribute("image", "../../img/richieste_az.gif");
						caption1.setAttribute("label", "Lista richieste");
						caption1.setAttribute("confirm", "false");

						SourceBean parameterCaption1 = new SourceBean("PARAMETER");
						parameterCaption1.setAttribute("name", "prgAzienda");
						parameterCaption1.setAttribute("scope", "SERVICE_REQUEST");
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
						if (strPageTarget.compareTo("IDOTESTATARICHIESTAPAGE") == 0) {
							SourceBean caption2 = new SourceBean("CAPTION");
							// if(canInsert_Richiesta) {
							caption2.setAttribute("image", "../../img/add2.gif");
							caption2.setAttribute("label", "Nuova richiesta");
							caption2.setAttribute("confirm", "false");

							SourceBean parameter1Caption2 = new SourceBean("PARAMETER");
							parameter1Caption2.setAttribute("name", "prgAzienda");
							parameter1Caption2.setAttribute("scope", "SERVICE_REQUEST");
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
						}
						// }
					}
				}
			}

			// BUTTONS
			SourceBean buttonsSB = new SourceBean("BUTTONS");

			if (canInsert_Unita) {
				SourceBean button1SB = new SourceBean("INSERT_BUTTON");
				button1SB.setAttribute("name", "inserisci");
				button1SB.setAttribute("label", "Nuova unità");

				SourceBean parameter1Button1 = new SourceBean("PARAMETER");
				parameter1Button1.setAttribute("name", "PAGE");
				parameter1Button1.setAttribute("type", "ABSOLUTE");
				parameter1Button1.setAttribute("value", "IdoUnitaAziendaPage");

				SourceBean parameter2Button1 = new SourceBean("PARAMETER");
				parameter2Button1.setAttribute("name", "flag_insert");
				parameter2Button1.setAttribute("type", "ABSOLUTE");
				parameter2Button1.setAttribute("value", "1");

				SourceBean parameter3Button1 = new SourceBean("PARAMETER");
				parameter3Button1.setAttribute("name", "prgAzienda");
				parameter3Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter3Button1.setAttribute("type", "RELATIVE");
				parameter3Button1.setAttribute("value", "prgAzienda");

				SourceBean parameter4Button1 = new SourceBean("PARAMETER");
				parameter4Button1.setAttribute("name", "ret");
				parameter4Button1.setAttribute("type", "ABSOLUTE");
				parameter4Button1.setAttribute("value", "IdoTestataAziendaPage");

				SourceBean parameter5Button1 = new SourceBean("PARAMETER");
				parameter5Button1.setAttribute("name", "cdnFunzione");
				parameter5Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter5Button1.setAttribute("type", "RELATIVE");
				parameter5Button1.setAttribute("value", "cdnFunzione");

				SourceBean parameter6Button1 = new SourceBean("PARAMETER");
				parameter6Button1.setAttribute("name", "insert_unita");
				parameter6Button1.setAttribute("type", "ABSOLUTE");
				parameter6Button1.setAttribute("value", "y");

				SourceBean parameter7Button1 = new SourceBean("PARAMETER");
				parameter7Button1.setAttribute("name", "AGG_FUNZ_INS_UNITA");
				parameter7Button1.setAttribute("scope", "ABSOLUTE");
				parameter7Button1.setAttribute("value", funzioneaggiornamentounita);

				button1SB.setAttribute(parameter1Button1);
				button1SB.setAttribute(parameter2Button1);
				button1SB.setAttribute(parameter3Button1);
				button1SB.setAttribute(parameter4Button1);
				button1SB.setAttribute(parameter5Button1);
				button1SB.setAttribute(parameter6Button1);
				button1SB.setAttribute(parameter7Button1);

				buttonsSB.setAttribute(button1SB);
			}

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "");
			configSB.setAttribute("ROWS", "-1");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(checkBoxSB);
			configSB.setAttribute(captionsSB);
			if (canInsert_Unita) {
				configSB.setAttribute(buttonsSB);
			}
			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}