package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class CMProspLavL68ListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMProspLavL68ListConfig.class.getName());

	private String className = this.getClass().getName();

	public CMProspLavL68ListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("CMProspLavL68ListModule");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("ST_GetConfig_LeggeBattistoni", null,
				"SELECT", "SIL_DATI");
		String conf_battistoni = (String) rowsSourceBean.getAttribute("ROW.NUM");
		if (conf_battistoni == null) {
			conf_battistoni = "0";
		}

		// Object cdnLav = row.getAttribute("CDNLAVORATORE");

		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "CMProspLavoratoriPage");

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
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "lavoratore");
			col_1.setAttribute("label", "Lavoratore");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strcodicefiscalelav");
			col_2.setAttribute("label", "Codice Fiscale");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "categoria");
			col_3.setAttribute("label", "D/A");
			colonneSB.setAttribute(col_3);

			if (!conf_battistoni.equals("0")) {
				col_11.setAttribute("name", "FLGBATTISTONI");
				col_11.setAttribute("label", "L. Battistoni");
				colonneSB.setAttribute(col_11);
			}

			col_4.setAttribute("name", "tipo");
			col_4.setAttribute("label", "Nu/No");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "descrMansione");
			col_5.setAttribute("label", "Mansione");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "datiniziorapp");
			col_6.setAttribute("label", "Data Inizio");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "datfinerapp");
			col_7.setAttribute("label", "Data Fine");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "deccopertura");
			col_8.setAttribute("label", "Copertura");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "descrContratto");
			col_9.setAttribute("label", "Contratto");
			colonneSB.setAttribute(col_9);

			col_10.setAttribute("name", "convenzione");
			col_10.setAttribute("label", "Conv.");
			colonneSB.setAttribute(col_10);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean lavPage = new SourceBean("PARAMETER");
			lavPage.setAttribute("name", "PAGE");
			lavPage.setAttribute("type", "ABSOLUTE");
			lavPage.setAttribute("value", "CMProspLavoratoriPage");
			lavPage.setAttribute("scope", "");

			SourceBean delLavModule = new SourceBean("PARAMETER");
			delLavModule.setAttribute("name", "MODULE");
			delLavModule.setAttribute("type", "ABSOLUTE");
			delLavModule.setAttribute("value", "CMProspLavL68DeleteModule");
			delLavModule.setAttribute("scope", "");

			SourceBean selLavModule = new SourceBean("PARAMETER");
			selLavModule.setAttribute("name", "MODULE");
			selLavModule.setAttribute("type", "ABSOLUTE");
			selLavModule.setAttribute("value", "CMProspLavL68DettModule");
			selLavModule.setAttribute("scope", "");

			SourceBean mode = new SourceBean("PARAMETER");
			mode.setAttribute("name", "MODE");
			mode.setAttribute("type", "ABSOLUTE");
			mode.setAttribute("value", "LISTA");
			mode.setAttribute("scope", "");

			SourceBean cdnfunz = new SourceBean("PARAMETER");
			cdnfunz.setAttribute("name", "cdnFunzione");
			cdnfunz.setAttribute("type", "RELATIVE");
			cdnfunz.setAttribute("value", "cdnFunzione");
			cdnfunz.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean prgLav = new SourceBean("PARAMETER");
			prgLav.setAttribute("name", "PRGLAVRISERVA");
			prgLav.setAttribute("type", "RELATIVE");
			prgLav.setAttribute("value", "PRGLAVRISERVA");
			prgLav.setAttribute("scope", "LOCAL");

			SourceBean prgPro = new SourceBean("PARAMETER");
			prgPro.setAttribute("name", "PRGPROSPETTOINF");
			prgPro.setAttribute("type", "RELATIVE");
			prgPro.setAttribute("value", "PRGPROSPETTOINF");
			prgPro.setAttribute("scope", "LOCAL");

			SourceBean msg = new SourceBean("PARAMETER");
			msg.setAttribute("name", "MESSAGE");
			msg.setAttribute("type", "ABSOLUTE");
			msg.setAttribute("value", "UPDATE");
			msg.setAttribute("scope", "");

			SourceBean movPage = new SourceBean("PARAMETER");
			movPage.setAttribute("name", "PAGE");
			movPage.setAttribute("type", "ABSOLUTE");
			movPage.setAttribute("value", "CMProspMovLavL68ListPage");
			movPage.setAttribute("scope", "");

			SourceBean movModule = new SourceBean("PARAMETER");
			movModule.setAttribute("name", "MODULE");
			movModule.setAttribute("type", "ABSOLUTE");
			movModule.setAttribute("value", "CMProspMovLavL68ListModule");
			movModule.setAttribute("scope", "");

			SourceBean cdnLavoratore = new SourceBean("PARAMETER");
			cdnLavoratore.setAttribute("name", "CDNLAVORATORE");
			cdnLavoratore.setAttribute("type", "RELATIVE");
			cdnLavoratore.setAttribute("value", "CDNLAVORATORE");
			cdnLavoratore.setAttribute("scope", "LOCAL");

			SourceBean prgAz = new SourceBean("PARAMETER");
			prgAz.setAttribute("name", "PRGAZIENDA");
			prgAz.setAttribute("type", "RELATIVE");
			prgAz.setAttribute("value", "PRGAZIENDA");
			prgAz.setAttribute("scope", "LOCAL");

			SourceBean prgUnit = new SourceBean("PARAMETER");
			prgUnit.setAttribute("name", "PRGUNITA");
			prgUnit.setAttribute("type", "RELATIVE");
			prgUnit.setAttribute("value", "PRGUNITA");
			prgUnit.setAttribute("scope", "LOCAL");

			SourceBean dtIniRapp = new SourceBean("PARAMETER");
			dtIniRapp.setAttribute("name", "DATINIZIORAPP");
			dtIniRapp.setAttribute("type", "RELATIVE");
			dtIniRapp.setAttribute("value", "DATINIZIORAPP");
			dtIniRapp.setAttribute("scope", "LOCAL");

			SourceBean codStatoAtto = new SourceBean("PARAMETER");
			codStatoAtto.setAttribute("name", "codStatoAtto");
			codStatoAtto.setAttribute("type", "RELATIVE");
			codStatoAtto.setAttribute("value", "codStatoAtto");
			codStatoAtto.setAttribute("scope", "SERVICE_REQUEST");

			// SELECT DETTAGLIO LAV L68
			SourceBean selectCaptionSB = new SourceBean("CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");
			selectCaptionSB.setAttribute(lavPage);
			selectCaptionSB.setAttribute(selLavModule);
			selectCaptionSB.setAttribute(cdnfunz);
			selectCaptionSB.setAttribute(msg);
			selectCaptionSB.setAttribute(prgLav);
			selectCaptionSB.setAttribute(prgPro);
			selectCaptionSB.setAttribute(codStatoAtto);

			captionsSB.setAttribute(selectCaptionSB);

			SourceBean listaMovimenti = new SourceBean("SELECT_CAPTION");
			listaMovimenti.setAttribute("image", "../../img/campo.gif");
			listaMovimenti.setAttribute("label", "Lista movimenti");
			listaMovimenti.setAttribute("confirm", "false");
			listaMovimenti.setAttribute("hiddenColumn", "viewBtn");
			listaMovimenti.setAttribute(movPage);
			listaMovimenti.setAttribute(movModule);
			listaMovimenti.setAttribute(cdnLavoratore);
			listaMovimenti.setAttribute(prgAz);
			listaMovimenti.setAttribute(prgUnit);
			listaMovimenti.setAttribute(dtIniRapp);
			listaMovimenti.setAttribute(prgLav);
			listaMovimenti.setAttribute(prgPro);
			listaMovimenti.setAttribute(codStatoAtto);

			captionsSB.setAttribute(listaMovimenti);

			// DELETE FISICA CAPTION
			SourceBean deleteFisicaCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteFisicaCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteFisicaCaptionSB.setAttribute("confirm", "TRUE");
			deleteFisicaCaptionSB.setAttribute(lavPage);
			deleteFisicaCaptionSB.setAttribute(delLavModule);
			deleteFisicaCaptionSB.setAttribute(mode);
			deleteFisicaCaptionSB.setAttribute(cdnfunz);
			deleteFisicaCaptionSB.setAttribute(prgLav);
			deleteFisicaCaptionSB.setAttribute(prgPro);
			deleteFisicaCaptionSB.setAttribute(codStatoAtto);

			captionsSB.setAttribute(deleteFisicaCaptionSB);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Lavoratori in servizio computabili");
			configSB.setAttribute(colonneSB);

			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}