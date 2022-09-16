package it.eng.sil.module.delega;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;

public class ListaPratichePatronatoDynConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaPratichePatronatoDynConfig.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		String tipoRicerca = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");

		try {

			SourceBean colonneSB = new SourceBean("COLUMNS");

			/////////////
			// COLUMNS //
			/////////////

			if (tipoRicerca.equalsIgnoreCase("ELENCO")) {

				SourceBean col_1 = new SourceBean("COLUMN");
				SourceBean col_2 = new SourceBean("COLUMN");
				SourceBean col_3 = new SourceBean("COLUMN");
				SourceBean col_4 = new SourceBean("COLUMN");
				SourceBean col_5 = new SourceBean("COLUMN");
				SourceBean col_6 = new SourceBean("COLUMN");

				col_1.setAttribute("name", "DATA_PRATICA_STR");
				col_1.setAttribute("label", "DATA PRATICA");
				colonneSB.setAttribute(col_1);

				col_2.setAttribute("name", "TIPO_PRATICA");
				col_2.setAttribute("label", "TIPO PRATICA");
				colonneSB.setAttribute(col_2);

				col_3.setAttribute("name", "PATRONATO");
				col_3.setAttribute("label", "PATRONATO");
				colonneSB.setAttribute(col_3);

				col_4.setAttribute("name", "UFFICIO");
				col_4.setAttribute("label", "UFFICIO");
				colonneSB.setAttribute(col_4);

				col_5.setAttribute("name", "CF_LAVORATORE");
				col_5.setAttribute("label", "CF LAVORATORE");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "LAVORATORE");
				col_6.setAttribute("label", "LAVORATORE");
				colonneSB.setAttribute(col_6);

			} else if (tipoRicerca.equalsIgnoreCase("CONTEGGIO")) {

				SourceBean col_1 = new SourceBean("COLUMN");
				SourceBean col_2 = new SourceBean("COLUMN");
				SourceBean col_3 = new SourceBean("COLUMN");

				col_1.setAttribute("name", "NUMERO_PRATICHE");
				col_1.setAttribute("label", "NUMERO PRATICHE");
				colonneSB.setAttribute(col_1);

				col_2.setAttribute("name", "TIPO_PRATICA");
				col_2.setAttribute("label", "TIPO PRATICA");
				colonneSB.setAttribute(col_2);

				col_3.setAttribute("name", "PATRONATO");
				col_3.setAttribute("label", "PATRONATO");
				colonneSB.setAttribute(col_3);

			}

			////////////
			// CONFIG //
			////////////

			configSB = new SourceBean("CONFIG");
			if (tipoRicerca.equalsIgnoreCase("ELENCO")) {
				configSB.setAttribute("title", "ELENCO PRATICHE PATRONATO");
			}
			if (tipoRicerca.equalsIgnoreCase("CONTEGGIO")) {
				configSB.setAttribute("title", "CONTEGGIO PRATICHE PATRONATO");
			}
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);

			if (_logger.isDebugEnabled()) {
				_logger.debug(configSB.toXML());
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
