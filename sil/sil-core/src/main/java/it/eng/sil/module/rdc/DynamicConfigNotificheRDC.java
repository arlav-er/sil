package it.eng.sil.module.rdc;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicConfigNotificheRDC extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicConfigNotificheRDC.class.getName());

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
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "STRCODICEFISCALE");
			col_1.setAttribute("label", "Codice Fiscale");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "STRCOGNOME");
			col_2.setAttribute("label", "Cognome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "STRNOME");
			col_3.setAttribute("label", "Nome");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "DESCRCODMONORUOLO");
			col_4.setAttribute("label", "Ruolo");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "STRDATDOMANDA");
			col_5.setAttribute("label", "Data dom.");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "CODSTATOINPS");
			col_6.setAttribute("label", "Stato dom. INPS");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "STRDATRENDICONTAZIONE");
			col_7.setAttribute("label", "Data rendicontazione");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "STRDATRICEZIONESIL");
			col_8.setAttribute("label", "Data ricezione SIL");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "STRTEL");
			col_9.setAttribute("label", "Telefono");
			colonneSB.setAttribute(col_9);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			boolean isFromPatto = false;
			if (request.containsAttribute("PROVENIENZA")) {
				String valore = (String) request.getAttribute("PROVENIENZA");
				if (StringUtils.isFilledNoBlank(valore) && valore.equalsIgnoreCase("Patto")) {
					isFromPatto = true;
				}
			}

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PRGRDC");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "PRGRDC");

			if (!isFromPatto) {
				selectCaptionSB.setAttribute("image", "../../img/detail.gif");
				selectCaptionSB.setAttribute("label", "Dettaglio");
				selectCaptionSB.setAttribute("confirm", "false");

				SourceBean parameterSB2 = new SourceBean("PARAMETER");
				parameterSB2.setAttribute("name", "CDNLAVORATORE");
				parameterSB2.setAttribute("scope", "LOCAL");
				parameterSB2.setAttribute("type", "RELATIVE");
				parameterSB2.setAttribute("value", "CDNLAVORATORE");

				SourceBean parameterSB3 = new SourceBean("PARAMETER");
				parameterSB3.setAttribute("name", "CDNFUNZIONE");
				parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
				parameterSB3.setAttribute("type", "RELATIVE");
				parameterSB3.setAttribute("value", "CDNFUNZIONE");

				SourceBean parameterSB4 = new SourceBean("PARAMETER");
				parameterSB4.setAttribute("name", "PROVENIENZA");
				parameterSB4.setAttribute("type", "ABSOLUTE");
				parameterSB4.setAttribute("value", "RICERCA");

				SourceBean parameterSB5 = new SourceBean("PARAMETER");
				parameterSB5.setAttribute("name", "PAGE");
				parameterSB5.setAttribute("type", "ABSOLUTE");
				parameterSB5.setAttribute("value", "NotificaRDCPage");
				selectCaptionSB.setAttribute(parameterSB1);
				selectCaptionSB.setAttribute(parameterSB2);
				selectCaptionSB.setAttribute(parameterSB3);
				selectCaptionSB.setAttribute(parameterSB4);
				selectCaptionSB.setAttribute(parameterSB5);

			} else {
				selectCaptionSB.setAttribute("image", "../../img/add.gif");
				selectCaptionSB.setAttribute("label", "Scegli");
				selectCaptionSB.setAttribute("confirm", "false");

				SourceBean parameterSB2 = new SourceBean("PARAMETER");
				parameterSB2.setAttribute("name", "STRPROTOCOLLOINPS");
				parameterSB2.setAttribute("scope", "LOCAL");
				parameterSB2.setAttribute("type", "RELATIVE");
				parameterSB2.setAttribute("value", "STRPROTOCOLLOINPS");

				selectCaptionSB.setAttribute(parameterSB1);
				selectCaptionSB.setAttribute(parameterSB2);
			}

			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Notifiche RDC");
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
