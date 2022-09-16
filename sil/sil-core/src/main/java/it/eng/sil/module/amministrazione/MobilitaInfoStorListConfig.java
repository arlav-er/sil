package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class MobilitaInfoStorListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MobilitaInfoStorListConfig.class.getName());

	private String className = this.getClass().getName();

	public MobilitaInfoStorListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
		User user = (User) getSessionContainer().getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "AmministrListeSpecPage");
		String labelStato = "Stato della richiesta";
		String config_Mob = "0";
		boolean config_RiapriMob = false;
		try {
			config_Mob = response.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")
					? response.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString()
					: "0";
			config_RiapriMob = attributi.containsButton("RIAPRI_MOBILITA");
			if (config_Mob.equals("1")) {
				labelStato = "Stato della domanda";
			}
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "DATINIZIO");
			col_1.setAttribute("label", "Data\ninizio");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "DATFINE");
			col_2.setAttribute("label", "Data\nfine");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "stato");
			col_3.setAttribute("label", labelStato);
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "DESCRIZIONE");
			col_4.setAttribute("label", "Tipo lista");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "STRRAGIONESOCIALE");
			col_5.setAttribute("label", "Azienda");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "MANSIONE");
			col_6.setAttribute("label", "Mansione");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "PERIODOLAV");
			col_7.setAttribute("label", "Periodo\nlavorativo");
			colonneSB.setAttribute(col_7);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption

			SourceBean pageDettaglioInfoStor = new SourceBean("PARAMETER");
			pageDettaglioInfoStor.setAttribute("name", "PAGE");
			pageDettaglioInfoStor.setAttribute("type", "ABSOLUTE");
			pageDettaglioInfoStor.setAttribute("value", "MobilitaInfoStorDettPage");
			pageDettaglioInfoStor.setAttribute("scope", "");

			SourceBean module = new SourceBean("PARAMETER");
			module.setAttribute("name", "MODULE");
			module.setAttribute("type", "ABSOLUTE");
			module.setAttribute("value", "M_GetSpecifMobilita");
			module.setAttribute("scope", "");

			SourceBean prgMobilitaIscr = new SourceBean("PARAMETER");
			prgMobilitaIscr.setAttribute("name", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("type", "RELATIVE");
			prgMobilitaIscr.setAttribute("value", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("scope", "LOCAL");

			SourceBean cdnfunzione = new SourceBean("PARAMETER");
			cdnfunzione.setAttribute("name", "CDNFUNZIONE");
			cdnfunzione.setAttribute("type", "RELATIVE");
			cdnfunzione.setAttribute("value", "CDNFUNZIONE");
			cdnfunzione.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean cdnLav = new SourceBean("PARAMETER");
			cdnLav.setAttribute("name", "CDNLAVORATORE");
			cdnLav.setAttribute("type", "RELATIVE");
			cdnLav.setAttribute("value", "CDNLAVORATORE");
			cdnLav.setAttribute("scope", "LOCAL");

			SourceBean pageInfoStor = new SourceBean("PARAMETER");
			pageInfoStor.setAttribute("name", "PAGE");
			pageInfoStor.setAttribute("type", "ABSOLUTE");
			pageInfoStor.setAttribute("value", "MobilitaInfoStorPage");
			pageInfoStor.setAttribute("scope", "");

			SourceBean cancella = new SourceBean("PARAMETER");
			cancella.setAttribute("name", "CANCELLA");
			cancella.setAttribute("type", "ABSOLUTE");
			cancella.setAttribute("value", "cancella");
			cancella.setAttribute("scope", "");

			SourceBean dataInizio = new SourceBean("PARAMETER");
			dataInizio.setAttribute("name", "DATINIZIO");
			dataInizio.setAttribute("type", "RELATIVE");
			dataInizio.setAttribute("value", "DATINIZIO");
			dataInizio.setAttribute("scope", "LOCAL");

			// <SELECT_CAPTION>
			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");
			selectCaptionSB.setAttribute(pageDettaglioInfoStor);
			selectCaptionSB.setAttribute(module);
			selectCaptionSB.setAttribute(prgMobilitaIscr);
			selectCaptionSB.setAttribute(cdnfunzione);
			selectCaptionSB.setAttribute(cdnLav);

			captionsSB.setAttribute(selectCaptionSB);

			// <DELETE_CAPTION>
			SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteCaptionSB.setAttribute("label", "Cancella");
			deleteCaptionSB.setAttribute("confirm", "true");
			deleteCaptionSB.setAttribute(pageInfoStor);
			deleteCaptionSB.setAttribute(prgMobilitaIscr);
			deleteCaptionSB.setAttribute(cdnLav);
			deleteCaptionSB.setAttribute(cancella);
			deleteCaptionSB.setAttribute(dataInizio);

			captionsSB.setAttribute(deleteCaptionSB);

			// <RIAPRI_CAPTION>
			if (config_RiapriMob) {
				SourceBean pageRiapri = new SourceBean("PARAMETER");
				pageRiapri.setAttribute("name", "PAGE");
				pageRiapri.setAttribute("type", "ABSOLUTE");
				pageRiapri.setAttribute("value", "MobilitaRiapriPage");
				pageRiapri.setAttribute("scope", "");
				SourceBean riapriCaptionSB = new SourceBean("CAPTION");
				riapriCaptionSB.setAttribute("image", "../../img/edit.gif");
				riapriCaptionSB.setAttribute("label", "Riapri mobilita'");
				riapriCaptionSB.setAttribute("confirm", "false");
				riapriCaptionSB.setAttribute(pageRiapri);
				riapriCaptionSB.setAttribute(prgMobilitaIscr);
				riapriCaptionSB.setAttribute(cdnLav);
				riapriCaptionSB.setAttribute(cdnfunzione);

				captionsSB.setAttribute(riapriCaptionSB);
			}

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "INFORMAZIONI STORICHE RELATIVE ALLA MOBILITA'");
			configSB.setAttribute("rows", "20");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);
			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
