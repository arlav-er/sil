/*
 * Creato il 16-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

/*
 * Classe utilizzata per configurare la lista dei vengono settati: 1) COLONNE 2)
 * PULSANTI 3) CHECKBOX
 * 
 * @author Gritti
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MobilitaListConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaListConfig.class.getName());

	private String className = this.getClass().getName();

	public MobilitaListConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("M_MobilitaRicerca");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

		// String prgTipoIncrocio = row.getAttribute("ROW.PRGTIPOINCROCIO") ==
		// null ? "" :
		// ((BigDecimal)row.getAttribute("ROW.PRGTIPOINCROCIO")).toString();
		// StringUtils.getAttributeStrNotNull(row, "ROW.PRGTIPOINCROCIO");
		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "M_MobilitaRicerca");
		String config_Mob = response.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")
				? response.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString()
				: "0";

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			// SourceBean interessati dai nuovi sviluppi
			SourceBean col_1 = null;
			SourceBean col_2 = null;
			SourceBean col_3 = null;
			SourceBean col_4 = null;
			SourceBean col_5 = null;
			SourceBean col_6 = null;
			SourceBean col_12 = null;

			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
				col_1 = new SourceBean("COLUMN");
				col_2 = new SourceBean("COLUMN");
				col_3 = new SourceBean("COLUMN");
			} else {
				// END-PARTE-TEMP
				col_4 = new SourceBean("COLUMN");
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
			} else {
				// END-PARTE-TEMP
				col_5 = new SourceBean("COLUMN");
				col_6 = new SourceBean("COLUMN");
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_13 = new SourceBean("COLUMN");
			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
			} else {
				// END-PARTE-TEMP
				col_12 = new SourceBean("COLUMN");
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
				col_1.setAttribute("name", "COGNOME");
				col_1.setAttribute("label", "Cognome");
				colonneSB.setAttribute(col_1);

				col_2.setAttribute("name", "NOME");
				col_2.setAttribute("label", "Nome");
				colonneSB.setAttribute(col_2);

				col_3.setAttribute("name", "CF");
				col_3.setAttribute("label", "Codice fiscale");
				colonneSB.setAttribute(col_3);
			} else {
				// END-PARTE-TEMP
				col_4.setAttribute("name", "LAVORATORE");
				col_4.setAttribute("label", "CF \n Cognome/Nome");
				colonneSB.setAttribute(col_4);
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
			} else {
				// END-PARTE-TEMP
				col_5.setAttribute("name", "STRRAGIONESOCIALE");
				col_5.setAttribute("label", "Rag. Soc. \n Azienda");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "STRINDIRIZZO");
				col_6.setAttribute("label", "Ind. \n Azienda");
				colonneSB.setAttribute(col_6);
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP
			col_7.setAttribute("name", "DATES");
			col_7.setAttribute("label", "Dt. inizio \n Dt. fine");
			colonneSB.setAttribute(col_7);

			col_13.setAttribute("name", "stato");
			String labelStato = "Stato della richiesta";
			if (config_Mob.equals("1")) {
				labelStato = "Stato della domanda";
			}
			col_13.setAttribute("label", labelStato);
			colonneSB.setAttribute(col_13);

			/**
			 * col_8.setAttribute("name", "DATFINE"); col_8.setAttribute("label", "Data \n fine");
			 * colonneSB.setAttribute(col_8);
			 */

			col_9.setAttribute("name", "STRDESCRIZIONEMOB");
			col_9.setAttribute("label", "Tipo lista");
			colonneSB.setAttribute(col_9);

			String labelDataApprov = "";
			if (config_Mob.equals("0")) {
				labelDataApprov = "Data CRT \n Data diff.";
			} else {
				labelDataApprov = "Data CPM \n Data diff.";
			}

			// INIT-PARTE-TEMP
			if (Sottosistema.MO.isOff()) {
				col_11.setAttribute("name", "DIFFCRT");
				col_11.setAttribute("label", labelDataApprov);
				colonneSB.setAttribute(col_11);

			} else {
				// END-PARTE-TEMP
				col_11.setAttribute("name", "DIFFCRT");
				col_11.setAttribute("label", labelDataApprov);
				colonneSB.setAttribute(col_11);

				col_12.setAttribute("name", "PROVENIENZA");
				col_12.setAttribute("label", "Prov");
				colonneSB.setAttribute(col_12);
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption

			SourceBean pageAmmistrListeSpec = new SourceBean("PARAMETER");
			pageAmmistrListeSpec.setAttribute("name", "PAGE");
			pageAmmistrListeSpec.setAttribute("type", "ABSOLUTE");
			pageAmmistrListeSpec.setAttribute("value", "AMMINISTRLISTESPECPAGE");
			pageAmmistrListeSpec.setAttribute("scope", "");

			SourceBean pageDettaglioStorico = new SourceBean("PARAMETER");
			pageDettaglioStorico.setAttribute("name", "PAGE_STORICO");
			pageDettaglioStorico.setAttribute("type", "ABSOLUTE");
			pageDettaglioStorico.setAttribute("value", "MobilitaInfoStorDettPage");
			pageDettaglioStorico.setAttribute("scope", "");

			SourceBean prgMobilitaIscr = new SourceBean("PARAMETER");
			prgMobilitaIscr.setAttribute("name", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("type", "RELATIVE");
			prgMobilitaIscr.setAttribute("value", "PRGMOBILITAISCR");
			prgMobilitaIscr.setAttribute("scope", "LOCAL");

			SourceBean module = new SourceBean("PARAMETER");
			module.setAttribute("name", "MODULE");
			module.setAttribute("type", "ABSOLUTE");
			module.setAttribute("value", "M_GetSpecifMobilita");
			module.setAttribute("scope", "");

			SourceBean storicizzato = new SourceBean("PARAMETER");
			storicizzato.setAttribute("name", "STORICIZZATO");
			storicizzato.setAttribute("type", "RELATIVE");
			storicizzato.setAttribute("value", "STORICIZZATO");
			storicizzato.setAttribute("scope", "LOCAL");

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

			SourceBean pageListaMob = new SourceBean("PARAMETER");
			pageListaMob.setAttribute("name", "PAGE_LISTA_MOB");
			pageListaMob.setAttribute("type", "RELATIVE");
			pageListaMob.setAttribute("value", "PAGE");
			pageListaMob.setAttribute("scope", "SERVICE_REQUEST");

			// <SELECT_CAPTION>

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");
			selectCaptionSB.setAttribute(pageAmmistrListeSpec);
			selectCaptionSB.setAttribute(pageDettaglioStorico);
			selectCaptionSB.setAttribute(cdnfunzione);
			selectCaptionSB.setAttribute(cdnLav);
			selectCaptionSB.setAttribute(pageListaMob);

			// /////////////////// AGGIUNTO
			selectCaptionSB.setAttribute(prgMobilitaIscr);
			selectCaptionSB.setAttribute(module);
			selectCaptionSB.setAttribute(storicizzato);

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

			SourceBean pageAggiornaStati = new SourceBean("PARAMETER");
			pageAggiornaStati.setAttribute("name", "PAGE");
			pageAggiornaStati.setAttribute("type", "ABSOLUTE");
			pageAggiornaStati.setAttribute("value", "AggiornaStatiMobPage");
			pageAggiornaStati.setAttribute("scope", "");

			SourceBean objParam = new SourceBean("PARAMETER");
			objParam.setAttribute("name", "this");
			objParam.setAttribute("type", "ABSOLUTE");
			objParam.setAttribute("value", "this");
			objParam.setAttribute("scope", "");

			checkBoxCaption.setAttribute(objParam);
			checkBoxCaption.setAttribute(pageAggiornaStati);
			checkBoxCaption.setAttribute(prgMobilitaIscr);

			captionsSB.setAttribute(selectCaptionSB);

			SourceBean checkBoxes = new SourceBean("CHECKBOXES");
			checkBoxes.setAttribute(checkBoxCaption);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute(colonneSB);

			configSB.setAttribute(captionsSB);

			configSB.setAttribute(checkBoxes);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}
