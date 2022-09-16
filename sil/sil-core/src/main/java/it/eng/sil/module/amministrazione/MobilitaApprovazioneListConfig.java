package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class MobilitaApprovazioneListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MobilitaApprovazioneListConfig.class.getName());

	private String className = this.getClass().getName();

	public MobilitaApprovazioneListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String config_Mob = response.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")
				? response.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString()
				: "0";

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

			col_1.setAttribute("name", "LAVORATORE");
			col_1.setAttribute("label", "CF \n Cognome/Nome");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "STRRAGIONESOCIALE");
			col_2.setAttribute("label", "Rag. Soc. \n Azienda");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRINDIRIZZO");
			col_3.setAttribute("label", "Ind. \n Azienda");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "DATAINIZIO");
			col_4.setAttribute("label", "Data inizio");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "DATAFINE");
			col_5.setAttribute("label", "Data fine");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "stato");
			String labelStato = "Stato della richiesta";
			if (config_Mob.equals("1")) {
				labelStato = "Stato della domanda";
			}
			col_6.setAttribute("label", labelStato);
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "STRDESCRIZIONEMOB");
			col_7.setAttribute("label", "Tipo lista");
			colonneSB.setAttribute(col_7);

			SourceBean checkBoxCaption = new SourceBean("CHECKBOX");
			checkBoxCaption.setAttribute("name", "checkboxmob");
			checkBoxCaption.setAttribute("label", "");
			checkBoxCaption.setAttribute("refColumn", "");

			SourceBean checkValue = new SourceBean("CHECKBOXVALUE");
			checkValue.setAttribute("name", "prgmobilitaiscr");
			checkValue.setAttribute("scope", "LOCAL");
			checkValue.setAttribute("type", "relative");
			checkValue.setAttribute("value", "prgmobilitaiscr");
			checkBoxCaption.setAttribute(checkValue);

			SourceBean objParam = new SourceBean("PARAMETER");
			objParam.setAttribute("name", "this");
			objParam.setAttribute("type", "ABSOLUTE");
			objParam.setAttribute("value", "this");
			objParam.setAttribute("scope", "");

			SourceBean prgMobilitaIscr = new SourceBean("PARAMETER");
			prgMobilitaIscr.setAttribute("name", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("type", "RELATIVE");
			prgMobilitaIscr.setAttribute("value", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("scope", "LOCAL");

			checkBoxCaption.setAttribute(objParam);
			checkBoxCaption.setAttribute(prgMobilitaIscr);

			SourceBean checkBoxes = new SourceBean("CHECKBOXES");
			checkBoxes.setAttribute(checkBoxCaption);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(checkBoxes);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
