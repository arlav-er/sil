/*
 * Creato il 23-mag-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.util.Sottosistema;

/**
 * Classe utilizzata per configurare la lista delle iscrizioni vengono settati: 1) COLONNE 2) PULSANTI
 * 
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CollMiratoRicercaListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CollMiratoRicercaListConfig.class.getName());

	private String className = this.getClass().getName();

	public CollMiratoRicercaListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("M_CollMiratoRicerca");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

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

			col_1.setAttribute("name", "COGNOME");
			col_1.setAttribute("label", "Cognome");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "NOME");
			col_2.setAttribute("label", "Nome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "CF");
			col_3.setAttribute("label", "Codice fiscale");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "DESCRIZIONEISCR");
			col_4.setAttribute("label", "Tipo iscrizione");
			colonneSB.setAttribute(col_4);

			if (Sottosistema.CM.isOn()) {
				col_5.setAttribute("name", "STATOATTO");
				col_5.setAttribute("label", "Stato dell'atto");
				colonneSB.setAttribute(col_5);
			}

			col_6.setAttribute("name", "DESCRIZIONEINV");
			col_6.setAttribute("label", "Invalidità");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "NUMPERCINVALIDITA");
			col_7.setAttribute("label", "%");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "DATINIZIO");
			col_8.setAttribute("label", "Data inizio");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "PROVINCIA_ISCR");
			col_9.setAttribute("label", "Ambito Terr.");
			colonneSB.setAttribute(col_9);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "prgCMIscr");
			parameterSB1.setAttribute("scope", "LOCAL");
			parameterSB1.setAttribute("type", "RELATIVE");
			parameterSB1.setAttribute("value", "prgCMIscr");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "CDNLAVORATORE");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "CDNLAVORATORE");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			if (Sottosistema.CM.isOn()) {
				parameterSB3.setAttribute("name", "CODSTATOATTO");
				parameterSB3.setAttribute("scope", "LOCAL");
				parameterSB3.setAttribute("type", "RELATIVE");
				parameterSB3.setAttribute("value", "CODSTATOATTO");
			}

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "PROVINCIA_ISCR");
			parameterSB4.setAttribute("scope", "LOCAL");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "PROVINCIA_ISCR");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			if (Sottosistema.CM.isOn()) {
				selectCaptionSB.setAttribute(parameterSB3);
			}

			selectCaptionSB.setAttribute(parameterSB4);

			captionsSB.setAttribute(selectCaptionSB);

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