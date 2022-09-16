package it.eng.sil.module.condizionalita.rdc;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class DynamicConfigAzioniCondizionalita extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicConfigAzioniCondizionalita.class.getName());

	private String className = this.getClass().getName();

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
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

			col_1.setAttribute("name", "dataInizioProg");
			col_1.setAttribute("label", "Data programma");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "descrAttivita");
			col_2.setAttribute("label", "Attività");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "dataAvvio");
			col_3.setAttribute("label", "Data avvio");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "dataEffettiva");
			col_4.setAttribute("label", "Data conclusione effettiva/prevista");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "descrPrestazione");
			col_5.setAttribute("label", "Prestazione");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "descrEsito");
			col_6.setAttribute("label", "Esito");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "descrEvento");
			col_7.setAttribute("label", "Eventi condizionalità");
			colonneSB.setAttribute(col_7);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PRGCOLLOQUIO");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "PRGCOLLOQUIO");

			selectCaptionSB.setAttribute("image", "../../img/add.gif");
			selectCaptionSB.setAttribute("label", "Scegli");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "PRGPERCORSO");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGPERCORSO");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "dataInizioProg");
			parameterSB3.setAttribute("scope", "LOCAL");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "dataInizioProg");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "dataFineProg");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "dataFineProg");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "descrProgramma");
			parameterSB5.setAttribute("scope", "LOCAL");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "descrProgramma");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "descrAttivita");
			parameterSB6.setAttribute("scope", "LOCAL");
			parameterSB6.setAttribute("type", "RELATIVE");
			parameterSB6.setAttribute("value", "descrAttivita");

			SourceBean parameterSB7 = new SourceBean("PARAMETER");
			parameterSB7.setAttribute("name", "dataAvvio");
			parameterSB7.setAttribute("scope", "LOCAL");
			parameterSB7.setAttribute("type", "RELATIVE");
			parameterSB7.setAttribute("value", "dataAvvio");

			SourceBean parameterSB8 = new SourceBean("PARAMETER");
			parameterSB8.setAttribute("name", "dataEffettiva");
			parameterSB8.setAttribute("scope", "LOCAL");
			parameterSB8.setAttribute("type", "RELATIVE");
			parameterSB8.setAttribute("value", "dataEffettiva");

			SourceBean parameterSB9 = new SourceBean("PARAMETER");
			parameterSB9.setAttribute("name", "descrEsito");
			parameterSB9.setAttribute("scope", "LOCAL");
			parameterSB9.setAttribute("type", "RELATIVE");
			parameterSB9.setAttribute("value", "descrEsito");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			selectCaptionSB.setAttribute(parameterSB7);
			selectCaptionSB.setAttribute(parameterSB8);
			selectCaptionSB.setAttribute(parameterSB9);

			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute("rows", "20");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
