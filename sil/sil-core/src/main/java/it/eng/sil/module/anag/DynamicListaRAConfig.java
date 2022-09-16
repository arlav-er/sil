package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

public class DynamicListaRAConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListaRAConfig.class.getName());
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
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "strCodiceFiscale");
			col_1.setAttribute("label", "CF");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strCognome");
			col_2.setAttribute("label", "Cognome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "strNome");
			col_3.setAttribute("label", "Nome");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "datInizioPrestazione");
			col_4.setAttribute("label", "Data Inizio Prestazione");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "datFinePrestazione");
			col_5.setAttribute("label", "Data Fine Prestazione");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "decimportolordocomplessivo");
			col_6.setAttribute("label", "Importo Complessivo");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "descrStato");
			col_7.setAttribute("label", "Stato");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "datCaricamentoFile");
			col_8.setAttribute("label", "Data Caricamento");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "nomeFileRa");
			col_9.setAttribute("label", "Nome File");
			colonneSB.setAttribute(col_9);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "cdnFunzione");
			parameterSB1.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "cdnFunzione");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "PAGE");
			parameterSB2.setAttribute("scope", "ABSOLUTE");
			parameterSB2.setAttribute("value", "DettaglioRAPage");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "MODULE");
			parameterSB3.setAttribute("scope", "ABSOLUTE");
			parameterSB3.setAttribute("value", "M_GetDettaglioRA");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "prgRedditoAttivazione");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "prgRedditoAttivazione");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");

			configSB.setAttribute("title", "Lista Redditi di Attivazione");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			if (_logger.isDebugEnabled())
				_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}