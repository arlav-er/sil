package it.eng.sil.module.delega;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynRicAnagConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynRicAnagConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "DelegaAnagRicercaPage");
		boolean canInsert = attributi.containsButton("nuovo");
		boolean canDelete = attributi.containsButton("rimuovi");

		boolean canOpenDettaglio = attributi.containsButton("APRI");

		String strPageTarget = "";
		Object elementListGoTo = null;
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = attributi.getGoToes();

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

			col_1.setAttribute("name", "strCognome");
			col_1.setAttribute("label", "Cognome");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strNome");
			col_2.setAttribute("label", "Nome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "strCodiceFiscale");
			col_3.setAttribute("label", "Codice Fiscale");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "datnasc");
			col_4.setAttribute("label", "Data di nascita");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "comNas");
			col_5.setAttribute("label", "Comune di nascita");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "CpiComp");
			col_6.setAttribute("label", "CPI Competente");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "CpiTit");
			col_7.setAttribute("label", "CPI Titolare");
			colonneSB.setAttribute(col_7);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "cdnLavoratore");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "cdnLavoratore");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "cdnFunzione");
			parameterSB2.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "MODULE");
			parameterSB3.setAttribute("scope", "ABSOLUTE");
			parameterSB3.setAttribute("value", "M_GetLavoratoreAnag");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "PAGE");
			parameterSB4.setAttribute("scope", "ABSOLUTE");
			parameterSB4.setAttribute("value", "ListaDeleghePage");

			// POPUP EVIDENZE
			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "APRI_EV");
			parameterSB5.setAttribute("scope", "ABSOLUTE");
			parameterSB5.setAttribute("value", "1");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "CDNGRUPPO");
			parameterSB6.setAttribute("scope", "ABSOLUTE");
			parameterSB6.setAttribute("value", String.valueOf(user.getCdnGruppo()));

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			// POPUP EVIDENZE
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);

			if (canOpenDettaglio) {
				captionsSB.setAttribute(selectCaptionSB);
			}

			if (canDelete) {
				// CAPTION DELETE
				SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
				deleteCaptionSB.setAttribute("image", "../../img/del.gif");
				deleteCaptionSB.setAttribute("label", "Cancella");
				deleteCaptionSB.setAttribute("confirm", "false");

				SourceBean deleteParameterSB1 = new SourceBean("PARAMETER");
				deleteParameterSB1.setAttribute("name", "cdnLavoratore");
				deleteParameterSB1.setAttribute("scope", "LOCAL");
				deleteParameterSB1.setAttribute("type", "RELATIVE");
				deleteParameterSB1.setAttribute("value", "cdnLavoratore");

				SourceBean deleteParameterSB2 = new SourceBean("PARAMETER");
				deleteParameterSB2.setAttribute("name", "cdnFunzione");
				deleteParameterSB2.setAttribute("scope", "SERVICE_REQUEST");
				deleteParameterSB2.setAttribute("type", "RELATIVE");
				deleteParameterSB2.setAttribute("value", "cdnFunzione");

				SourceBean deleteParameterSB3 = new SourceBean("PARAMETER");
				deleteParameterSB3.setAttribute("name", "MODULE");
				deleteParameterSB3.setAttribute("scope", "ABSOLUTE");
				deleteParameterSB3.setAttribute("value", "M_DeleteLavoratore");

				SourceBean deleteParameterSB4 = new SourceBean("PARAMETER");
				deleteParameterSB4.setAttribute("name", "PAGE");
				deleteParameterSB4.setAttribute("scope", "ABSOLUTE");
				deleteParameterSB4.setAttribute("value", "AnagDeletePage");

				deleteCaptionSB.setAttribute(deleteParameterSB1);
				deleteCaptionSB.setAttribute(deleteParameterSB2);
				deleteCaptionSB.setAttribute(deleteParameterSB3);
				deleteCaptionSB.setAttribute(deleteParameterSB4);
				captionsSB.setAttribute(deleteCaptionSB);
			}
			// CAPTION 1
			for (int iList = 0; iList < myGoToes.size(); iList++) {
				goTo = (GoTo) myGoToes.get(iList);
				strPageTarget = goTo.getTargetPage();
				cdnFunzTarget = goTo.getTargetFunz();
				if (cdnFunzTarget != null) {
					strCdnFunzTarget = cdnFunzTarget.toString();
				}
				if (strPageTarget.equalsIgnoreCase("MovDettaglioGeneraleInserisciPage")) {
					SourceBean caption1 = new SourceBean("CAPTION");
					caption1.setAttribute("image", "../../img/ass_movimento.gif");
					caption1.setAttribute("label", "Nuovo movimento");
					caption1.setAttribute("confirm", "false");

					SourceBean parameterCaption1 = new SourceBean("PARAMETER");
					parameterCaption1.setAttribute("name", "cdnLavoratore");
					parameterCaption1.setAttribute("scope", "LOCAL");
					parameterCaption1.setAttribute("type", "RELATIVE");
					parameterCaption1.setAttribute("value", "cdnLavoratore");

					SourceBean parameterCaption2 = new SourceBean("PARAMETER");
					parameterCaption2.setAttribute("name", "CURRENTCONTEXT");
					parameterCaption2.setAttribute("scope", "ABSOLUTE");
					parameterCaption2.setAttribute("value", "inserisci");

					SourceBean parameterCaption3 = new SourceBean("PARAMETER");
					parameterCaption3.setAttribute("name", "cdnFunzione");
					parameterCaption3.setAttribute("scope", "ABSOLUTE");
					parameterCaption3.setAttribute("value", strCdnFunzTarget);

					SourceBean parameterCaption4 = new SourceBean("PARAMETER");
					parameterCaption4.setAttribute("name", "PAGE");
					parameterCaption4.setAttribute("scope", "ABSOLUTE");
					parameterCaption4.setAttribute("value", strPageTarget);

					SourceBean parameterCaption5 = new SourceBean("PARAMETER");
					parameterCaption5.setAttribute("name", "PROVENIENZA");
					parameterCaption5.setAttribute("scope", "ABSOLUTE");
					parameterCaption5.setAttribute("value", "lavoratore");

					SourceBean parameterCaption6 = new SourceBean("PARAMETER");
					parameterCaption6.setAttribute("name", "destinazione");
					parameterCaption6.setAttribute("scope", "ABSOLUTE");
					parameterCaption6.setAttribute("value", "MovDettaglioGeneraleInserisciPage");

					caption1.setAttribute(parameterCaption1);
					caption1.setAttribute(parameterCaption2);
					caption1.setAttribute(parameterCaption3);
					caption1.setAttribute(parameterCaption4);
					caption1.setAttribute(parameterCaption5);
					caption1.setAttribute(parameterCaption6);
					captionsSB.setAttribute(caption1);
				}
			}
			// BUTTONS
			SourceBean buttonsSB = new SourceBean("BUTTONS");
			if (canInsert) {
				SourceBean button1SB = new SourceBean("INSERT_BUTTON");
				button1SB.setAttribute("confirm", "FALSE");
				button1SB.setAttribute("label", "Nuovo lavoratore");

				SourceBean parameter1Button1 = new SourceBean("PARAMETER");
				parameter1Button1.setAttribute("name", "PAGE");
				parameter1Button1.setAttribute("scope", "ABSOLUTE");
				parameter1Button1.setAttribute("value", "AnagDettaglioPageAnagIns");

				SourceBean parameter2Button1 = new SourceBean("PARAMETER");
				parameter2Button1.setAttribute("name", "cdnFunzione");
				parameter2Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter2Button1.setAttribute("type", "RELATIVE");
				parameter2Button1.setAttribute("value", "cdnFunzione");

				SourceBean parameter3Button1 = new SourceBean("PARAMETER");
				parameter3Button1.setAttribute("name", "inserisci");
				parameter3Button1.setAttribute("type", "ABSOLUTE");
				parameter3Button1.setAttribute("value", "1");
				parameter3Button1.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameter4Button1 = new SourceBean("PARAMETER");
				parameter4Button1.setAttribute("name", "strCodiceFiscale");
				parameter4Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter4Button1.setAttribute("type", "RELATIVE");
				parameter4Button1.setAttribute("value", "strCodiceFiscale");

				SourceBean parameter5Button1 = new SourceBean("PARAMETER");
				parameter5Button1.setAttribute("name", "strCognome");
				parameter5Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter5Button1.setAttribute("type", "RELATIVE");
				parameter5Button1.setAttribute("value", "strCognome");

				SourceBean parameter6Button1 = new SourceBean("PARAMETER");
				parameter6Button1.setAttribute("name", "strNome");
				parameter6Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter6Button1.setAttribute("type", "RELATIVE");
				parameter6Button1.setAttribute("value", "strNome");

				SourceBean parameter7Button1 = new SourceBean("PARAMETER");
				parameter7Button1.setAttribute("name", "datnasc");
				parameter7Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter7Button1.setAttribute("type", "RELATIVE");
				parameter7Button1.setAttribute("value", "datnasc");

				SourceBean parameter8Button1 = new SourceBean("PARAMETER");
				parameter8Button1.setAttribute("name", "codComNas");
				parameter8Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter8Button1.setAttribute("type", "RELATIVE");
				parameter8Button1.setAttribute("value", "codComNas");

				SourceBean parameter9Button1 = new SourceBean("PARAMETER");
				parameter9Button1.setAttribute("name", "strComNas");
				parameter9Button1.setAttribute("scope", "SERVICE_REQUEST");
				parameter9Button1.setAttribute("type", "RELATIVE");
				parameter9Button1.setAttribute("value", "strComNas");

				button1SB.setAttribute(parameter1Button1);
				button1SB.setAttribute(parameter2Button1);
				button1SB.setAttribute(parameter3Button1);
				button1SB.setAttribute(parameter4Button1);
				button1SB.setAttribute(parameter5Button1);
				button1SB.setAttribute(parameter6Button1);
				button1SB.setAttribute(parameter7Button1);
				button1SB.setAttribute(parameter8Button1);
				button1SB.setAttribute(parameter9Button1);

				buttonsSB.setAttribute(button1SB);
			}

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "LISTA LAVORATORI");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);
			if (canInsert) {
				configSB.setAttribute(buttonsSB);
			}

			if (_logger.isDebugEnabled())
				_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}// class DynRicSediAziendaCongif
