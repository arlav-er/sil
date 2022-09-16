package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.PropertiesReport;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/*
 * Classe utilizzata per configurare la lista dei 
 * vengono settati:
 * 1) COLONNE
 * 2) PULSANTI
 * 3) CHECKBOX    
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMListaAdesioniLavoratoreConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMListaAdesioniLavoratoreConfig.class.getName());

	private String className = this.getClass().getName();

	public CMListaAdesioniLavoratoreConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("M_GetCMListaAdesioniLavoratore");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

		// String prgTipoIncrocio = row.getAttribute("ROW.PRGTIPOINCROCIO") == null ? "" :
		// ((BigDecimal)row.getAttribute("ROW.PRGTIPOINCROCIO")).toString();
		// StringUtils.getAttributeStrNotNull(row, "ROW.PRGTIPOINCROCIO");
		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "CMListaAdesioniPage");
		boolean priorita = attributi.containsButton("PRIORITA");

		try {
			String codRegioneSil = "";
			SourceBean rowInfo = (SourceBean) com.engiweb.framework.util.QueryExecutor
					.executeQuery("GET_INFO_TARGA_PROVINCIA_REGIONE", null, "SELECT", Values.DB_SIL_DATI);
			rowInfo = rowInfo.containsAttribute("ROW") ? (SourceBean) rowInfo.getAttribute("ROW") : rowInfo;
			codRegioneSil = SourceBeanUtils.getAttrStrNotNull(rowInfo, "codregione");

			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_7a = null;
			if (codRegioneSil.equalsIgnoreCase(PropertiesReport.COD_REGIONE_RER)) {
				col_7a = new SourceBean("COLUMN");
			}
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_12 = new SourceBean("COLUMN");
			SourceBean col_13 = new SourceBean("COLUMN");
			SourceBean col_14 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "richiesta");
			col_1.setAttribute("label", "Rif.");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "ente");
			col_2.setAttribute("label", "Ente");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "posti");
			col_3.setAttribute("label", "N \n u \n m");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "descrInc");
			col_4.setAttribute("label", "Asta");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "descr_rosa");
			col_5.setAttribute("label", "Stato grad");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "qual");
			col_6.setAttribute("label", "Qualifica");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "rapp");
			col_7.setAttribute("label", "Rapporto");
			colonneSB.setAttribute(col_7);

			if (codRegioneSil.equalsIgnoreCase(PropertiesReport.COD_REGIONE_RER)) {
				col_7a.setAttribute("name", "codiceAdesione");
				col_7a.setAttribute("label", "Codice Adesione");
				colonneSB.setAttribute(col_7a);
			}

			col_8.setAttribute("name", "datChPb");
			col_8.setAttribute("label", "Chiamata \n \\Pubbl.");
			colonneSB.setAttribute(col_8);

			/*
			 * col_8.setAttribute("name", "dataChiam"); col_8.setAttribute("label", "Chiamata");
			 * colonneSB.setAttribute(col_8);
			 * 
			 * col_9.setAttribute("name", "dataPubb"); col_9.setAttribute("label", "Pubbl.");
			 * colonneSB.setAttribute(col_9);
			 */
			col_10.setAttribute("name", "strpriorita");
			col_10.setAttribute("label", "Prio <br> ri <br> tà");
			colonneSB.setAttribute(col_10);

			col_11.setAttribute("name", "cpi");
			col_11.setAttribute("label", "Centro per l'impiego");
			colonneSB.setAttribute(col_11);

			col_12.setAttribute("name", "viewBtn");
			col_12.setAttribute("label", "");
			col_12.setAttribute("notVisible", "");
			colonneSB.setAttribute(col_12);

			col_13.setAttribute("name", "viewBtnDel");
			col_13.setAttribute("label", "");
			col_13.setAttribute("notVisible", "");
			colonneSB.setAttribute(col_13);

			col_14.setAttribute("name", "utente");
			col_14.setAttribute("label", "Inserimento");
			colonneSB.setAttribute(col_14);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page DispInsRosaPage
			SourceBean pageDettaglioSint = new SourceBean("PARAMETER");
			pageDettaglioSint.setAttribute("name", "PAGE");
			pageDettaglioSint.setAttribute("type", "ABSOLUTE");
			pageDettaglioSint.setAttribute("value", "IdoDettaglioSinteticoPage");
			pageDettaglioSint.setAttribute("scope", "");
			// Page stampa adesione
			SourceBean pageDettaglioStampa = new SourceBean("PARAMETER");
			pageDettaglioStampa.setAttribute("name", "PAGE");
			pageDettaglioStampa.setAttribute("type", "ABSOLUTE");
			pageDettaglioStampa.setAttribute("value", "CMListaAdesioniPage");
			pageDettaglioStampa.setAttribute("scope", "");

			// Page priorità adesione
			SourceBean pagePriorita = new SourceBean("PARAMETER");
			pagePriorita.setAttribute("name", "PAGE");
			pagePriorita.setAttribute("type", "ABSOLUTE");
			pagePriorita.setAttribute("value", "CMPrioritaPage");
			pagePriorita.setAttribute("scope", "");

			// Page CMListaAdesioniPage
			SourceBean adesioniPage = new SourceBean("PARAMETER");
			adesioniPage.setAttribute("name", "PAGE");
			adesioniPage.setAttribute("type", "ABSOLUTE");
			adesioniPage.setAttribute("value", "CMListaAdesioniPage");
			adesioniPage.setAttribute("scope", "");

			SourceBean oldPage = new SourceBean("PARAMETER");
			oldPage.setAttribute("name", "OLD_PAGE");
			oldPage.setAttribute("type", "ABSOLUTE");
			oldPage.setAttribute("value", "CMListaAdesioniPage");
			oldPage.setAttribute("scope", "");

			// MODULE DELETE
			SourceBean modDelete = new SourceBean("PARAMETER");
			modDelete.setAttribute("name", "MODULE");
			modDelete.setAttribute("type", "ABSOLUTE");
			modDelete.setAttribute("value", "M_CMDeleteRichiestaAdesione");
			modDelete.setAttribute("scope", "");

			SourceBean parameter1 = new SourceBean("PARAMETER");
			parameter1.setAttribute("name", "PRGRICHIESTAAZ");
			parameter1.setAttribute("type", "RELATIVE");
			parameter1.setAttribute("value", "PRGRICHIESTAAZ");
			parameter1.setAttribute("scope", "LOCAL");

			// PRGNOMINATIVO
			SourceBean parameter2 = new SourceBean("PARAMETER");
			parameter2.setAttribute("name", "PRGNOMINATIVO");
			parameter2.setAttribute("type", "RELATIVE");
			parameter2.setAttribute("value", "PRGNOMINATIVO");
			parameter2.setAttribute("scope", "LOCAL");

			// CDNLAVORATORE
			SourceBean parameter3 = new SourceBean("PARAMETER");
			parameter3.setAttribute("name", "CDNLAVORATORE");
			parameter3.setAttribute("type", "RELATIVE");
			parameter3.setAttribute("value", "CDNLAVORATORE");
			parameter3.setAttribute("scope", "LOCAL");

			// PRGROSA
			SourceBean parameter4 = new SourceBean("PARAMETER");
			parameter4.setAttribute("name", "PRGROSA");
			parameter4.setAttribute("type", "RELATIVE");
			parameter4.setAttribute("value", "PRGROSA");
			parameter4.setAttribute("scope", "LOCAL");

			// CDNFUNZIONE
			SourceBean parameter5 = new SourceBean("PARAMETER");
			parameter5.setAttribute("name", "CDNFUNZIONE");
			parameter5.setAttribute("type", "RELATIVE");
			parameter5.setAttribute("value", "CDNFUNZIONE");
			parameter5.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean parStampa = new SourceBean("PARAMETER");
			parStampa.setAttribute("name", "STAMPA_PAR");
			parStampa.setAttribute("type", "ABSOLUTE");
			parStampa.setAttribute("value", "STAMPA_PAR");
			parStampa.setAttribute("scope", "");

			SourceBean parPriorita = new SourceBean("PARAMETER");
			parPriorita.setAttribute("name", "PRIORITA_PAR");
			parPriorita.setAttribute("type", "ABSOLUTE");
			parPriorita.setAttribute("value", "PRIORITA_PAR");
			parPriorita.setAttribute("scope", "");

			SourceBean parNoStampa = new SourceBean("PARAMETER");
			parNoStampa.setAttribute("name", "STAMPA_PAR");
			parNoStampa.setAttribute("type", "ABSOLUTE");
			parNoStampa.setAttribute("value", "NO_STAMPA");
			parNoStampa.setAttribute("scope", "");

			// <SELECT_CAPTION>

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/info_soggetto.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio Sintetico");
			selectCaptionSB.setAttribute("confirm", "false");
			selectCaptionSB.setAttribute(pageDettaglioSint);
			selectCaptionSB.setAttribute(parameter1);
			selectCaptionSB.setAttribute(parameter3);
			selectCaptionSB.setAttribute(parameter5);
			selectCaptionSB.setAttribute(parNoStampa);

			captionsSB.setAttribute(selectCaptionSB);

			// STAMPA ADESIONE
			SourceBean stampaCaptionSB = new SourceBean("CAPTION");
			stampaCaptionSB.setAttribute("image", "../../img/stampa.gif");
			stampaCaptionSB.setAttribute("label", "Stampa Adesione");
			stampaCaptionSB.setAttribute("confirm", "false");
			stampaCaptionSB.setAttribute(pageDettaglioStampa);
			stampaCaptionSB.setAttribute(parameter1);
			stampaCaptionSB.setAttribute(parameter3);
			stampaCaptionSB.setAttribute(parameter5);
			stampaCaptionSB.setAttribute(parameter2);
			stampaCaptionSB.setAttribute(parStampa);

			captionsSB.setAttribute(stampaCaptionSB);

			// PRIORITA
			SourceBean prioritaCaptionSB = new SourceBean("CAPTION");
			prioritaCaptionSB.setAttribute("image", "../../img/priorita.gif");
			prioritaCaptionSB.setAttribute("label", "Priorità Adesione");
			prioritaCaptionSB.setAttribute("confirm", "false");
			prioritaCaptionSB.setAttribute(pagePriorita);
			prioritaCaptionSB.setAttribute(parameter1);
			prioritaCaptionSB.setAttribute(parameter2);
			prioritaCaptionSB.setAttribute(parameter3);
			prioritaCaptionSB.setAttribute(parameter5);
			prioritaCaptionSB.setAttribute(parPriorita);

			if (priorita) {
				captionsSB.setAttribute(prioritaCaptionSB);
			}

			// DELETE FISICA CAPTION
			SourceBean deleteFisicaCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteFisicaCaptionSB.setAttribute("hiddenColumn", "viewBtnDel");
			deleteFisicaCaptionSB.setAttribute("image", "../../img/del2.gif");
			deleteFisicaCaptionSB.setAttribute("confirm", "TRUE");
			deleteFisicaCaptionSB.setAttribute(adesioniPage);
			deleteFisicaCaptionSB.setAttribute(modDelete);
			deleteFisicaCaptionSB.setAttribute(parameter2);
			deleteFisicaCaptionSB.setAttribute(parameter3);
			deleteFisicaCaptionSB.setAttribute(parameter4);
			deleteFisicaCaptionSB.setAttribute(parameter5);

			captionsSB.setAttribute(deleteFisicaCaptionSB);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Adesioni");
			configSB.setAttribute(colonneSB);

			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}