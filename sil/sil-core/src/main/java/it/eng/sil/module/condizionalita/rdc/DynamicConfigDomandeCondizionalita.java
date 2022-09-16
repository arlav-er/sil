package it.eng.sil.module.condizionalita.rdc;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class DynamicConfigDomandeCondizionalita extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicConfigDomandeCondizionalita.class.getName());

	private String className = this.getClass().getName();

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "protocolloInps");
			col_1.setAttribute("label", "Protocollo INPS");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strDataDomanda");
			col_2.setAttribute("label", "Data Domanda");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "statoDomanda");
			col_3.setAttribute("label", "Stato Domanda");
			colonneSB.setAttribute(col_3);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "chiave");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "chiave");

			selectCaptionSB.setAttribute("image", "../../img/add.gif");
			selectCaptionSB.setAttribute("label", "Scegli");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "protocolloInps");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "protocolloInps");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "dataDomanda");
			parameterSB3.setAttribute("scope", "LOCAL");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "strDataDomanda");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "codiceStatoDomanda");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "codiceStatoDomanda");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "statoDomanda");
			parameterSB5.setAttribute("scope", "LOCAL");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "statoDomanda");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);

			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Elenco Domande Lavoratore");
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
