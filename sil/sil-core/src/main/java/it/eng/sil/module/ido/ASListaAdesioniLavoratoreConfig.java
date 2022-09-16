package it.eng.sil.module.ido;

import java.math.BigDecimal;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

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
public class ASListaAdesioniLavoratoreConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASListaAdesioniLavoratoreConfig.class
			.getName());

	private String className = this.getClass().getName();

	public ASListaAdesioniLavoratoreConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("M_GetASListaAdesioniLavoratore");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

		// String prgTipoIncrocio = row.getAttribute("ROW.PRGTIPOINCROCIO") ==
		// null ? "" :
		// ((BigDecimal)row.getAttribute("ROW.PRGTIPOINCROCIO")).toString();
		// StringUtils.getAttributeStrNotNull(row, "ROW.PRGTIPOINCROCIO");
		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "M_GetASListaAdesioniLavoratore");

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
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_12 = new SourceBean("COLUMN");
			SourceBean col_13 = new SourceBean("COLUMN");

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

			col_8.setAttribute("name", "datChPb");
			col_8.setAttribute("label", "Chiamata \n \\Pubbl.");
			colonneSB.setAttribute(col_8);

			/*
			 * col_8.setAttribute("name", "dataChiam");
			 * col_8.setAttribute("label", "Chiamata");
			 * colonneSB.setAttribute(col_8);
			 * 
			 * col_9.setAttribute("name", "dataPubb");
			 * col_9.setAttribute("label", "Pubbl.");
			 * colonneSB.setAttribute(col_9);
			 */
			col_10.setAttribute("name", "cpi");
			col_10.setAttribute("label", "Centro per l'impiego");
			colonneSB.setAttribute(col_10);

			col_11.setAttribute("name", "viewBtn");
			col_11.setAttribute("label", "");
			col_11.setAttribute("notVisible", "");
			colonneSB.setAttribute(col_11);

			col_12.setAttribute("name", "viewBtnDel");
			col_12.setAttribute("label", "");
			col_12.setAttribute("notVisible", "");
			colonneSB.setAttribute(col_12);

			col_13.setAttribute("name", "utente");
			col_13.setAttribute("label", "Inserimento");
			colonneSB.setAttribute(col_13);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page DispInsRosaPage
			SourceBean pageDettaglioSint = new SourceBean("PARAMETER");
			pageDettaglioSint.setAttribute("name", "PAGE");
			pageDettaglioSint.setAttribute("type", "ABSOLUTE");
			pageDettaglioSint.setAttribute("value", "IdoDettaglioSinteticoPage");
			pageDettaglioSint.setAttribute("scope", "");
			
			// Page PunteggiDidISee
			SourceBean pagePunteggiDidIsee = new SourceBean("PARAMETER");
			pagePunteggiDidIsee.setAttribute("name", "PAGE");
			pagePunteggiDidIsee.setAttribute("type", "ABSOLUTE");
			pagePunteggiDidIsee.setAttribute("value", "IdoPunteggiLavPage");
			pagePunteggiDidIsee.setAttribute("scope", "");

			SourceBean pageControllaDatiLSU = new SourceBean("PARAMETER");
			pageControllaDatiLSU.setAttribute("name", "PAGE");
			pageControllaDatiLSU.setAttribute("type", "ABSOLUTE");
			pageControllaDatiLSU.setAttribute("value", "ASDettaglioDatiLSUPage");
			pageControllaDatiLSU.setAttribute("scope", "");

			// Page ASListaAdesioniPage
			SourceBean adesioniPage = new SourceBean("PARAMETER");
			adesioniPage.setAttribute("name", "PAGE");
			adesioniPage.setAttribute("type", "ABSOLUTE");
			adesioniPage.setAttribute("value", "ASListaAdesioniPage");
			adesioniPage.setAttribute("scope", "");

			SourceBean oldPage = new SourceBean("PARAMETER");
			oldPage.setAttribute("name", "OLD_PAGE");
			oldPage.setAttribute("type", "ABSOLUTE");
			oldPage.setAttribute("value", "ASListaAdesioniPage");
			oldPage.setAttribute("scope", "");

			// MODULE DELETE
			SourceBean modDelete = new SourceBean("PARAMETER");
			modDelete.setAttribute("name", "MODULE");
			modDelete.setAttribute("type", "ABSOLUTE");
			modDelete.setAttribute("value", "M_ASDeleteRichiestaAdesione");
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

			// <SELECT_CAPTION>

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/info_soggetto.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio Sintetico");
			selectCaptionSB.setAttribute("confirm", "false");
			selectCaptionSB.setAttribute(pageDettaglioSint);
			selectCaptionSB.setAttribute(parameter1);

			captionsSB.setAttribute(selectCaptionSB);
			
			//Marianna Borriello: Button Modalità PunteggiDidISee
			SourceBean parameterPunteggiProv = new SourceBean("PARAMETER");
			parameterPunteggiProv.setAttribute("name", "PROVENIENZA");
			parameterPunteggiProv.setAttribute("type", "ABSOLUTE");
			parameterPunteggiProv.setAttribute("value", "LAVORATORE");
			parameterPunteggiProv.setAttribute("scope", "");

			//prima leggo la configurazione ASATTRIB se è diverso da 0  allora si visualizza il pulsante altrimenti no
			SourceBean beanConfigRows = (SourceBean) QueryExecutor.executeQuery("AS_CONFIG_PUNTEGGI",null,"SELECT","SIL_DATI");
			String configValue = (String) beanConfigRows.getAttribute("ROW.STRVALORECONFIG");	
			BigDecimal numConfigValue = (BigDecimal) beanConfigRows.getAttribute("ROW.NUMVALORECONFIG");	
			
			if(configValue.equalsIgnoreCase("1") || (numConfigValue!=null
					&& (numConfigValue.intValue() == 1 || numConfigValue.intValue() == 2 || numConfigValue.intValue() == 3))){
				SourceBean punteggiCaptionSB = new SourceBean("CAPTION");
				punteggiCaptionSB.setAttribute("hiddenColumn", "viewBtnPunteggio");
				punteggiCaptionSB.setAttribute("image", "../../img/confr_punteggi.gif");
				if(numConfigValue!=null &&  ( numConfigValue.intValue() == 2 || numConfigValue.intValue() == 3)){
					punteggiCaptionSB.setAttribute("label", "Modalità di attribuzione dei punteggi art. 16");	
				}else{
					punteggiCaptionSB.setAttribute("label", "Modalità di attribuzione dei punteggi DID e ISEE");
				}
				punteggiCaptionSB.setAttribute("confirm", "false");
				punteggiCaptionSB.setAttribute(pagePunteggiDidIsee);
				punteggiCaptionSB.setAttribute(parameter1);
				punteggiCaptionSB.setAttribute(parameter2);
				punteggiCaptionSB.setAttribute(parameter3);
				punteggiCaptionSB.setAttribute(parameter4);
				punteggiCaptionSB.setAttribute(parameterPunteggiProv);
				punteggiCaptionSB.setAttribute(oldPage);
				captionsSB.setAttribute(punteggiCaptionSB);
			}
			// per gli incroci di tipo LSU prgTipoIncrocio = 7
			// if (("7").equalsIgnoreCase(prgTipoIncrocio)) {

			// per gli incroci di tipo LSU si visualizza l'icona per la
			// maschera di inserimento dati carichi familiari, professionalità
			// ecc.

			SourceBean controllaDatiLSUCaptionSB = new SourceBean("CAPTION");
			controllaDatiLSUCaptionSB.setAttribute("hiddenColumn", "viewBtn");
			controllaDatiLSUCaptionSB.setAttribute("image", "../../img/datiLSU.gif");
			controllaDatiLSUCaptionSB.setAttribute("label", "Dati LSU");

			controllaDatiLSUCaptionSB.setAttribute("confirm", "false");
			controllaDatiLSUCaptionSB.setAttribute(pageControllaDatiLSU);
			controllaDatiLSUCaptionSB.setAttribute(parameter1);
			controllaDatiLSUCaptionSB.setAttribute(parameter2);
			controllaDatiLSUCaptionSB.setAttribute(parameter3);
			controllaDatiLSUCaptionSB.setAttribute(parameter4);
			controllaDatiLSUCaptionSB.setAttribute(oldPage);

			captionsSB.setAttribute(controllaDatiLSUCaptionSB);
			// }

			// DELETE FISICA CAPTION
			SourceBean deleteFisicaCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteFisicaCaptionSB.setAttribute("hiddenColumn", "viewBtnDel");
			deleteFisicaCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteFisicaCaptionSB.setAttribute("confirm", "TRUE");
			deleteFisicaCaptionSB.setAttribute(adesioniPage);
			deleteFisicaCaptionSB.setAttribute(modDelete);
			deleteFisicaCaptionSB.setAttribute(parameter2);
			deleteFisicaCaptionSB.setAttribute(parameter3);
			deleteFisicaCaptionSB.setAttribute(parameter4);

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