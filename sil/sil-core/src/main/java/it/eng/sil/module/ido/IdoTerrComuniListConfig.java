package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class IdoTerrComuniListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(IdoTerrComuniListConfig.class.getName());

	private String className = this.getClass().getName();

	public IdoTerrComuniListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
		String config_ClicLavoro = response.containsAttribute("M_GetConfigClicLav.ROWS.ROW.NUM")
				? response.getAttribute("M_GetConfigClicLav.ROWS.ROW.NUM").toString()
				: "0";

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "STRDENOMINAZIONE");
			col_1.setAttribute("label", "Comune");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "PROVINCIA");
			col_2.setAttribute("label", "Provincia");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRINSERIMENTO");
			col_3.setAttribute("label", "Inserimento");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "FLGINVIOCL");
			if (config_ClicLavoro.equals("0")) {
				col_4.setAttribute("label", "ClicLavoro");
			} else {
				col_4.setAttribute("label", "Portale Regionale/Cliclavoro");
			}
			colonneSB.setAttribute(col_4);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// <SELECT_CAPTION>
			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Modifica");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean selectParameterSB1 = new SourceBean("PARAMETER");
			selectParameterSB1.setAttribute("name", "PRGCOMUNE");
			selectParameterSB1.setAttribute("scope", "LOCAL");
			selectParameterSB1.setAttribute("type", "RELATIVE");
			selectParameterSB1.setAttribute("value", "PRGCOMUNE");

			selectCaptionSB.setAttribute(selectParameterSB1);

			captionsSB.setAttribute(selectCaptionSB);

			// <DELETE_CAPTION>
			SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteCaptionSB.setAttribute("label", "Cancella");
			deleteCaptionSB.setAttribute("confirm", "false");

			SourceBean deleteParameterSB1 = new SourceBean("PARAMETER");
			deleteParameterSB1.setAttribute("name", "PRGCOMUNE");
			deleteParameterSB1.setAttribute("scope", "LOCAL");
			deleteParameterSB1.setAttribute("type", "RELATIVE");
			deleteParameterSB1.setAttribute("value", "PRGCOMUNE");

			SourceBean deleteParameterSB2 = new SourceBean("PARAMETER");
			deleteParameterSB2.setAttribute("name", "STRDENOMINAZIONE");
			deleteParameterSB2.setAttribute("scope", "LOCAL");
			deleteParameterSB2.setAttribute("type", "RELATIVE");
			deleteParameterSB2.setAttribute("value", "STRDENOMINAZIONE");

			SourceBean deleteParameterSB3 = new SourceBean("PARAMETER");
			deleteParameterSB3.setAttribute("name", "prgRichiestaAz");
			deleteParameterSB3.setAttribute("scope", "LOCAL");
			deleteParameterSB3.setAttribute("type", "RELATIVE");
			deleteParameterSB3.setAttribute("value", "prgRichiestaAz");

			deleteCaptionSB.setAttribute(deleteParameterSB1);
			deleteCaptionSB.setAttribute(deleteParameterSB2);
			deleteCaptionSB.setAttribute(deleteParameterSB3);

			captionsSB.setAttribute(deleteCaptionSB);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");

			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
