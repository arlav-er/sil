package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class DynamicRicAziendeUtilConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicRicAziendeUtilConfig.class.getName());

	public DynamicRicAziendeUtilConfig() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

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
			parameterSB1.setAttribute("name", "PRGAZIENDA");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "PRGAZIENDA");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "PRGUNITA");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGUNITA");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "STRRAGIONESOCIALE");
			parameterSB3.setAttribute("scope", "LOCAL");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "STRRAGIONESOCIALE");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "STRINDIRIZZO");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "STRINDIRIZZO");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "COMUNE_AZ");
			parameterSB5.setAttribute("scope", "LOCAL");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "COMUNE_AZ");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "Lista Aziende");
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