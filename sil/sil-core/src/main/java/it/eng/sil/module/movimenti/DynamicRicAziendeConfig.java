package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicRicAziendeConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicRicAziendeConfig.class.getName());

	public DynamicRicAziendeConfig() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "MovimentiListaSoggettoPage");
		String strPageTarget = "";
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = null;
		String cdnFunzioneLocal = request.containsAttribute("CDNFUNZIONE")
				? request.getAttribute("CDNFUNZIONE").toString()
				: "";
		if (cdnFunzioneLocal.equals("52")) {
			myGoToes = attributi.getGoToes();
		}

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

			col_1.setAttribute("name", "STRRAGIONESOCIALE");
			col_1.setAttribute("label", "Ragione Sociale");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "STRCODICEFISCALE");
			col_2.setAttribute("label", "C.F.");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRPARTITAIVA");
			col_3.setAttribute("label", "P. IVA");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "FLGDATIOK");
			col_4.setAttribute("label", "Validità C.F./P. IVA");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "STRINDIRIZZO");
			col_5.setAttribute("label", "Indirizzo");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "COMUNE_AZ");
			col_6.setAttribute("label", "Comune");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "STRTEL");
			col_7.setAttribute("label", "Tel.");
			colonneSB.setAttribute(col_7);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/add.gif");
			selectCaptionSB.setAttribute("label", "Aggiungi Az.");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PAGE");
			parameterSB1.setAttribute("type", "ABSOLUTE");
			parameterSB1.setAttribute("value", "MovimentiRicercaRefreshPage");
			captionsSB.setAttribute(parameterSB1);

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "MOV_SOGG");
			parameterSB2.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "MOV_SOGG");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "AGG_FUNZ");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "AGG_FUNZ");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "STRNUMALBOINTERINALI");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "STRNUMALBOINTERINALI");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "STRNUMREGCOMM");
			parameterSB5.setAttribute("scope", "LOCAL");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "STRNUMREGISTROCOMMITT");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "PRGAZIENDA");
			parameterSB6.setAttribute("scope", "LOCAL");
			parameterSB6.setAttribute("type", "RELATIVE");
			parameterSB6.setAttribute("value", "PRGAZIENDA");

			SourceBean parameterSB7 = new SourceBean("PARAMETER");
			parameterSB7.setAttribute("name", "PRGUNITA");
			parameterSB7.setAttribute("scope", "LOCAL");
			parameterSB7.setAttribute("type", "RELATIVE");
			parameterSB7.setAttribute("value", "PRGUNITA");

			SourceBean parameterSB8 = new SourceBean("PARAMETER");
			parameterSB8.setAttribute("name", "STRRAGIONESOCIALE");
			parameterSB8.setAttribute("scope", "LOCAL");
			parameterSB8.setAttribute("type", "RELATIVE");
			parameterSB8.setAttribute("value", "STRRAGIONESOCIALE");

			SourceBean parameterSB9 = new SourceBean("PARAMETER");
			parameterSB9.setAttribute("name", "STRPARTITAIVA");
			parameterSB9.setAttribute("scope", "LOCAL");
			parameterSB9.setAttribute("type", "RELATIVE");
			parameterSB9.setAttribute("value", "STRPARTITAIVA");

			SourceBean parameterSB10 = new SourceBean("PARAMETER");
			parameterSB10.setAttribute("name", "STRCODICEFISCALEAZIENDA");
			parameterSB10.setAttribute("scope", "LOCAL");
			parameterSB10.setAttribute("type", "RELATIVE");
			parameterSB10.setAttribute("value", "STRCODICEFISCALE");

			SourceBean parameterSB11 = new SourceBean("PARAMETER");
			parameterSB11.setAttribute("name", "STRTEL");
			parameterSB11.setAttribute("scope", "LOCAL");
			parameterSB11.setAttribute("type", "RELATIVE");
			parameterSB11.setAttribute("value", "STRTEL");

			SourceBean parameterSB12 = new SourceBean("PARAMETER");
			parameterSB12.setAttribute("name", "STRINDIRIZZO");
			parameterSB12.setAttribute("scope", "LOCAL");
			parameterSB12.setAttribute("type", "RELATIVE");
			parameterSB12.setAttribute("value", "STRINDIRIZZO");

			SourceBean parameterSB13 = new SourceBean("PARAMETER");
			parameterSB13.setAttribute("name", "COMUNE_AZ");
			parameterSB13.setAttribute("scope", "LOCAL");
			parameterSB13.setAttribute("type", "RELATIVE");
			parameterSB13.setAttribute("value", "COMUNE_AZ");

			SourceBean parameterSB14 = new SourceBean("PARAMETER");
			parameterSB14.setAttribute("name", "prov_az");
			parameterSB14.setAttribute("scope", "LOCAL");
			parameterSB14.setAttribute("type", "RELATIVE");
			parameterSB14.setAttribute("value", "prov_az");

			SourceBean parameterSB15 = new SourceBean("PARAMETER");
			parameterSB15.setAttribute("name", "codTipoAzienda");
			parameterSB15.setAttribute("scope", "LOCAL");
			parameterSB15.setAttribute("type", "RELATIVE");
			parameterSB15.setAttribute("value", "codTipoAzienda");

			SourceBean parameterSB16 = new SourceBean("PARAMETER");
			parameterSB16.setAttribute("name", "natGiurAz");
			parameterSB16.setAttribute("scope", "LOCAL");
			parameterSB16.setAttribute("type", "RELATIVE");
			parameterSB16.setAttribute("value", "natGiurAz");

			SourceBean parameterSB17 = new SourceBean("PARAMETER");
			parameterSB17.setAttribute("name", "descrTipoAz");
			parameterSB17.setAttribute("scope", "LOCAL");
			parameterSB17.setAttribute("type", "RELATIVE");
			parameterSB17.setAttribute("value", "descrTipoAz");

			SourceBean parameterSB18 = new SourceBean("PARAMETER");
			parameterSB18.setAttribute("name", "STRDESCRIZIONECCNL");
			parameterSB18.setAttribute("scope", "LOCAL");
			parameterSB18.setAttribute("type", "RELATIVE");
			parameterSB18.setAttribute("value", "STRDESCRIZIONECCNL");

			SourceBean parameterSB19 = new SourceBean("PARAMETER");
			parameterSB19.setAttribute("name", "CCNLAZ");
			parameterSB19.setAttribute("scope", "LOCAL");
			parameterSB19.setAttribute("type", "RELATIVE");
			parameterSB19.setAttribute("value", "CCNLAZ");

			SourceBean parameterSB20 = new SourceBean("PARAMETER");
			parameterSB20.setAttribute("name", "codAteco");
			parameterSB20.setAttribute("scope", "LOCAL");
			parameterSB20.setAttribute("type", "RELATIVE");
			parameterSB20.setAttribute("value", "codAteco");

			SourceBean parameterSB21 = new SourceBean("PARAMETER");
			parameterSB21.setAttribute("name", "strDesAtecoUAz");
			parameterSB21.setAttribute("scope", "LOCAL");
			parameterSB21.setAttribute("type", "RELATIVE");
			parameterSB21.setAttribute("value", "strDesAtecoUAz");

			SourceBean parameterSB22 = new SourceBean("PARAMETER");
			parameterSB22.setAttribute("name", "strPatInail");
			parameterSB22.setAttribute("scope", "LOCAL");
			parameterSB22.setAttribute("type", "RELATIVE");
			parameterSB22.setAttribute("value", "strPatInail");

			SourceBean parameterSB23 = new SourceBean("PARAMETER");
			parameterSB23.setAttribute("name", "strNumeroInps");
			parameterSB23.setAttribute("scope", "LOCAL");
			parameterSB23.setAttribute("type", "RELATIVE");
			parameterSB23.setAttribute("value", "strNumeroInps");

			SourceBean parameterSB24 = new SourceBean("PARAMETER");
			parameterSB24.setAttribute("name", "FLGDATIOK");
			parameterSB24.setAttribute("scope", "LOCAL");
			parameterSB24.setAttribute("type", "RELATIVE");
			parameterSB24.setAttribute("value", "FLGDATIOK");

			SourceBean parameterSB25 = new SourceBean("PARAMETER");
			parameterSB25.setAttribute("name", "CODNATGIURIDICA");
			parameterSB25.setAttribute("scope", "LOCAL");
			parameterSB25.setAttribute("type", "RELATIVE");
			parameterSB25.setAttribute("value", "CODNATGIURIDICA");

			SourceBean parameterSB26 = new SourceBean("PARAMETER");
			parameterSB26.setAttribute("name", "STRREFERENTE");
			parameterSB26.setAttribute("scope", "LOCAL");
			parameterSB26.setAttribute("type", "RELATIVE");
			parameterSB26.setAttribute("value", "STRREFERENTE");

			SourceBean parameterSB27 = new SourceBean("PARAMETER");
			parameterSB27.setAttribute("name", "CODCPI");
			parameterSB27.setAttribute("scope", "LOCAL");
			parameterSB27.setAttribute("type", "RELATIVE");
			parameterSB27.setAttribute("value", "CODCPI");

			SourceBean parameterSB28 = new SourceBean("PARAMETER");
			parameterSB28.setAttribute("name", "cdnFunzione");
			parameterSB28.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB28.setAttribute("type", "RELATIVE");
			parameterSB28.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB29 = new SourceBean("PARAMETER");
			parameterSB29.setAttribute("name", "tipoAteco");
			parameterSB29.setAttribute("scope", "LOCAL");
			parameterSB29.setAttribute("type", "RELATIVE");
			parameterSB29.setAttribute("value", "tipoAteco");

			SourceBean parameterSB30 = new SourceBean("PARAMETER");
			parameterSB30.setAttribute("name", "codCom");
			parameterSB30.setAttribute("scope", "LOCAL");
			parameterSB30.setAttribute("type", "RELATIVE");
			parameterSB30.setAttribute("value", "codCom");

			SourceBean parameterSB31 = new SourceBean("PARAMETER");
			parameterSB31.setAttribute("name", "STRCAP");
			parameterSB31.setAttribute("scope", "LOCAL");
			parameterSB31.setAttribute("type", "RELATIVE");
			parameterSB31.setAttribute("value", "STRCAP");

			SourceBean parameterSB32 = new SourceBean("PARAMETER");
			parameterSB32.setAttribute("name", "CODREGIONE");
			parameterSB32.setAttribute("scope", "LOCAL");
			parameterSB32.setAttribute("type", "RELATIVE");
			parameterSB32.setAttribute("value", "CODREGIONE");

			SourceBean parameterSB33 = new SourceBean("PARAMETER");
			parameterSB33.setAttribute("name", "CODFISCAZESTERA");
			parameterSB33.setAttribute("scope", "LOCAL");
			parameterSB33.setAttribute("type", "RELATIVE");
			parameterSB33.setAttribute("value", "CODFISCAZESTERA");

			SourceBean parameterSB34 = new SourceBean("PARAMETER");
			parameterSB34.setAttribute("name", "RAGSOCAZESTERA");
			parameterSB34.setAttribute("scope", "LOCAL");
			parameterSB34.setAttribute("type", "RELATIVE");
			parameterSB34.setAttribute("value", "RAGSOCAZESTERA");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			selectCaptionSB.setAttribute(parameterSB7);
			selectCaptionSB.setAttribute(parameterSB8);
			selectCaptionSB.setAttribute(parameterSB9);
			selectCaptionSB.setAttribute(parameterSB10);
			selectCaptionSB.setAttribute(parameterSB11);
			selectCaptionSB.setAttribute(parameterSB12);
			selectCaptionSB.setAttribute(parameterSB13);
			selectCaptionSB.setAttribute(parameterSB14);
			selectCaptionSB.setAttribute(parameterSB15);
			selectCaptionSB.setAttribute(parameterSB16);
			selectCaptionSB.setAttribute(parameterSB17);
			selectCaptionSB.setAttribute(parameterSB18);
			selectCaptionSB.setAttribute(parameterSB19);
			selectCaptionSB.setAttribute(parameterSB20);
			selectCaptionSB.setAttribute(parameterSB21);
			selectCaptionSB.setAttribute(parameterSB22);
			selectCaptionSB.setAttribute(parameterSB23);
			selectCaptionSB.setAttribute(parameterSB24);
			selectCaptionSB.setAttribute(parameterSB25);
			selectCaptionSB.setAttribute(parameterSB26);
			selectCaptionSB.setAttribute(parameterSB27);
			selectCaptionSB.setAttribute(parameterSB28);
			selectCaptionSB.setAttribute(parameterSB29);
			selectCaptionSB.setAttribute(parameterSB30);
			selectCaptionSB.setAttribute(parameterSB31);
			selectCaptionSB.setAttribute(parameterSB32);
			selectCaptionSB.setAttribute(parameterSB33);
			selectCaptionSB.setAttribute(parameterSB34);

			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "Lista Aziende");
			configSB.setAttribute("rows", "20");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			if (myGoToes != null) {
				for (int iList = 0; iList < myGoToes.size(); iList++) {
					goTo = (GoTo) myGoToes.get(iList);
					strPageTarget = goTo.getTargetPage().toUpperCase();
					cdnFunzTarget = goTo.getTargetFunz();
					if (cdnFunzTarget != null) {
						strCdnFunzTarget = cdnFunzTarget.toString();
					}
					String funzioneAggiornamento = null;
					if (request.containsAttribute("AGG_FUNZ")) {
						funzioneAggiornamento = (String) request.getAttribute("AGG_FUNZ");
					}
					if ((strPageTarget.compareTo("IDOUNITAAZIENDAPAGE") == 0)
							&& (!funzioneAggiornamento.equals("aggiornaAziendaUt"))) {
						// CAPTION INSERISCIUNITA
						SourceBean caption1 = new SourceBean("CAPTION");
						caption1.setAttribute("image", "../../img/detail.gif");
						caption1.setAttribute("label", "nuova unità");
						caption1.setAttribute("confirm", "false");

						SourceBean parameter1Caption1 = new SourceBean("PARAMETER");
						parameter1Caption1.setAttribute("name", "PAGE");
						parameter1Caption1.setAttribute("type", "ABSOLUTE");
						parameter1Caption1.setAttribute("value", strPageTarget);

						SourceBean parameter2Caption1 = new SourceBean("PARAMETER");
						parameter2Caption1.setAttribute("name", "cdnFunzione");
						parameter2Caption1.setAttribute("type", "ABSOLUTE");
						parameter2Caption1.setAttribute("value", strCdnFunzTarget);

						SourceBean parameter3Caption1 = new SourceBean("PARAMETER");
						parameter3Caption1.setAttribute("name", "PRGAZIENDA");
						parameter3Caption1.setAttribute("scope", "LOCAL");
						parameter3Caption1.setAttribute("type", "RELATIVE");
						parameter3Caption1.setAttribute("value", "PRGAZIENDA");

						SourceBean parameter4Caption1 = new SourceBean("PARAMETER");
						parameter4Caption1.setAttribute("name", "PRGUNITA");
						parameter4Caption1.setAttribute("scope", "LOCAL");
						parameter4Caption1.setAttribute("type", "RELATIVE");
						parameter4Caption1.setAttribute("value", "PRGUNITA");

						SourceBean parameter5Caption1 = new SourceBean("PARAMETER");
						parameter5Caption1.setAttribute("name", "CONTESTOPROVENIENZA");
						parameter5Caption1.setAttribute("type", "ABSOLUTE");
						parameter5Caption1.setAttribute("value", "MOVIMENTI");

						SourceBean parameter6Caption1 = new SourceBean("PARAMETER");
						parameter6Caption1.setAttribute("name", "AGG_FUNZ_INS_UNITA");
						parameter6Caption1.setAttribute("scope", "SERVICE_REQUEST");
						parameter6Caption1.setAttribute("type", "RELATIVE");
						parameter6Caption1.setAttribute("value", "AGG_FUNZ");

						caption1.setAttribute(parameter1Caption1);
						caption1.setAttribute(parameter2Caption1);
						caption1.setAttribute(parameter3Caption1);
						caption1.setAttribute(parameter4Caption1);
						caption1.setAttribute(parameter5Caption1);
						caption1.setAttribute(parameter6Caption1);

						captionsSB.setAttribute(caption1);
						iList = myGoToes.size();
					}
				}
			}

			it.eng.sil.util.TraceWrapper.debug(_logger, "", configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", ex);
		}
		return configSB;
	}
}