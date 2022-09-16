package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/*   
 * Classe utilizzata per configurare la lista dei candidati
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
public class ASStoricoCandidatiGraduatoriaConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASStoricoCandidatiGraduatoriaConfig.class.getName());

	private String className = this.getClass().getName();

	public ASStoricoCandidatiGraduatoriaConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dettRosa = (SourceBean) response.getAttribute("ASDETTAGLIOGRADUATORIA");
		SourceBean row = (SourceBean) dettRosa.getAttribute("ROW");
		String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(row, "PRGTIPOINCROCIO");
		Object prgRosaFiglia = row.getAttribute("PRGROSAFIGLIA");
		String prgTipoRosa = StringUtils.getAttributeStrNotNull(row, "PRGTIPOROSA");
		String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(request, "CDNSTATORICH");
		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "ASStoricoDettGraduatoriaPage");
		boolean infCorrentiLav = true; // attributi.containsButton("CONSULTA_ADESIONI");

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

			col_1.setAttribute("name", "numordine");
			col_1.setAttribute("label", "Pos.");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strCognomeNome");
			col_2.setAttribute("label", "Cognome e Nome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "DatNasc");
			col_3.setAttribute("label", "Dt. nascita");
			colonneSB.setAttribute(col_3);
			if (("8").equalsIgnoreCase(prgTipoIncrocio) || ("9").equalsIgnoreCase(prgTipoIncrocio)
					|| ("13").equalsIgnoreCase(prgTipoIncrocio) || ("14").equalsIgnoreCase(prgTipoIncrocio)) {
				col_4.setAttribute("name", "descrTipoMob");
				col_4.setAttribute("label", "Mobilità");
				colonneSB.setAttribute(col_4);

				col_5.setAttribute("name", "datiniziomov");
				col_5.setAttribute("label", "Dt. assunzione");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "datfine");
				col_6.setAttribute("label", "Dt. fine");
				colonneSB.setAttribute(col_6);

				col_7.setAttribute("name", "descrMansione");
				col_7.setAttribute("label", "Qualifica");
				colonneSB.setAttribute(col_7);
			} else {
				if (!("7").equalsIgnoreCase(prgTipoIncrocio)) {
					col_4.setAttribute("name", "punteggio");
					col_4.setAttribute("label", "Punt.");
					colonneSB.setAttribute(col_4);
				}

				col_5.setAttribute("name", "stato_occup");
				col_5.setAttribute("label", "S.O. congelato / S.O. calcolato / S.O. chiamata");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "titolo_studio");
				col_6.setAttribute("label", "Tit. studio");
				colonneSB.setAttribute(col_6);

				col_7.setAttribute("name", "valore_isee");
				col_7.setAttribute("label", "ISEE");
				colonneSB.setAttribute(col_7);
			}

			col_9.setAttribute("name", "Domicilio");
			col_9.setAttribute("label", "Domicilio");
			colonneSB.setAttribute(col_9);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page DispInsRosaPage
			SourceBean pageConsultaAdesioni = new SourceBean("PARAMETER");
			pageConsultaAdesioni.setAttribute("name", "PAGE");
			pageConsultaAdesioni.setAttribute("type", "ABSOLUTE");
			pageConsultaAdesioni.setAttribute("value", "ASListaAdesioniViewPage");
			pageConsultaAdesioni.setAttribute("scope", "");

			SourceBean pageControllaPunteggio = new SourceBean("PARAMETER");
			pageControllaPunteggio.setAttribute("name", "PAGE");
			pageControllaPunteggio.setAttribute("type", "ABSOLUTE");
			pageControllaPunteggio.setAttribute("value", "ASDettaglioPunteggioLavoratorePage");
			pageControllaPunteggio.setAttribute("scope", "");

			SourceBean pageControllaDatiLSU = new SourceBean("PARAMETER");
			pageControllaDatiLSU.setAttribute("name", "PAGE");
			pageControllaDatiLSU.setAttribute("type", "ABSOLUTE");
			pageControllaDatiLSU.setAttribute("value", "ASDettaglioDatiLSUPage");
			pageControllaDatiLSU.setAttribute("scope", "");

			SourceBean oldPage = new SourceBean("PARAMETER");
			oldPage.setAttribute("name", "OLD_PAGE");
			oldPage.setAttribute("type", "ABSOLUTE");
			oldPage.setAttribute("value", "ASStoricoDettGraduatoriaPage");
			oldPage.setAttribute("scope", "");

			// Page EscludiDaRosaPage
			SourceBean pageEscl = new SourceBean("PARAMETER");
			pageEscl.setAttribute("name", "PAGE");
			pageEscl.setAttribute("type", "ABSOLUTE");
			pageEscl.setAttribute("value", "EscludiDaRosaPage");
			pageEscl.setAttribute("scope", "");
			// Page DettMatchRosaPage
			SourceBean pagePesi = new SourceBean("PARAMETER");
			pagePesi.setAttribute("name", "PAGE");
			pagePesi.setAttribute("type", "ABSOLUTE");
			pagePesi.setAttribute("value", "DettMatchRosaPage");
			pagePesi.setAttribute("scope", "");
			// Page MatchEsitoPage
			SourceBean pageEsito = new SourceBean("PARAMETER");
			pageEsito.setAttribute("name", "PAGE");
			pageEsito.setAttribute("type", "ABSOLUTE");
			pageEsito.setAttribute("value", "MatchEsitoPage");
			pageEsito.setAttribute("scope", "");
			// Parameter retPage
			SourceBean retPage = new SourceBean("PARAMETER");
			retPage.setAttribute("name", "RET_PAGE");
			retPage.setAttribute("type", "ABSOLUTE");
			retPage.setAttribute("value", "MatchDettRosaPage");
			retPage.setAttribute("scope", "");
			// Page AmstrInfoCorrentiPage
			SourceBean infoCorrPage = new SourceBean("PARAMETER");
			infoCorrPage.setAttribute("name", "PAGE");
			infoCorrPage.setAttribute("type", "ABSOLUTE");
			infoCorrPage.setAttribute("value", "AmstrInfoCorrentiPage");
			infoCorrPage.setAttribute("scope", "");
			// Page
			SourceBean parPageQuestaLista = new SourceBean("PARAMETER");
			parPageQuestaLista.setAttribute("name", "PAGE");
			parPageQuestaLista.setAttribute("type", "ABSOLUTE");
			parPageQuestaLista.setAttribute("value", pageQuestaLista);
			parPageQuestaLista.setAttribute("scope", "");
			// CPIROSE
			SourceBean parameter1 = new SourceBean("PARAMETER");
			parameter1.setAttribute("name", "CPIROSE");
			parameter1.setAttribute("type", "RELATIVE");
			parameter1.setAttribute("value", "CPIROSE");
			parameter1.setAttribute("scope", "SERVICE_REQUEST");
			// NONECOGNOME
			SourceBean parameter1_a = new SourceBean("PARAMETER");
			parameter1_a.setAttribute("name", "NOMECOGNOME");
			parameter1_a.setAttribute("type", "RELATIVE");
			parameter1_a.setAttribute("value", "strCognomeNome");
			parameter1_a.setAttribute("scope", "LOCAL");
			// PRGROSA
			SourceBean parameter2 = new SourceBean("PARAMETER");
			parameter2.setAttribute("name", "PRGROSA");
			parameter2.setAttribute("type", "RELATIVE");
			parameter2.setAttribute("value", "PRGROSA");
			parameter2.setAttribute("scope", "SERVICE_REQUEST");
			// PRGTIPOROSA
			SourceBean parameter2_a = new SourceBean("PARAMETER");
			parameter2_a.setAttribute("name", "PRGTIPOROSA");
			parameter2_a.setAttribute("type", "RELATIVE");
			parameter2_a.setAttribute("value", "PRGTIPOROSA");
			parameter2_a.setAttribute("scope", "SERVICE_REQUEST");
			// PRGNOMINATIVO
			SourceBean parameter3 = new SourceBean("PARAMETER");
			parameter3.setAttribute("name", "PRGNOMINATIVO");
			parameter3.setAttribute("type", "RELATIVE");
			parameter3.setAttribute("value", "PRGNOMINATIVO");
			parameter3.setAttribute("scope", "LOCAL");
			// CDNLAVORATORE
			SourceBean parameter4 = new SourceBean("PARAMETER");
			parameter4.setAttribute("name", "CDNLAVORATORE");
			parameter4.setAttribute("type", "RELATIVE");
			parameter4.setAttribute("value", "CDNLAVORATORE");
			parameter4.setAttribute("scope", "LOCAL");
			// CDNFUNZIONE
			SourceBean parameter5 = new SourceBean("PARAMETER");
			parameter5.setAttribute("name", "CDNFUNZIONE");
			parameter5.setAttribute("type", "RELATIVE");
			parameter5.setAttribute("value", "CDNFUNZIONE");
			parameter5.setAttribute("scope", "SERVICE_REQUEST");
			// PRGRICHIESTAAZ
			SourceBean parameter6 = new SourceBean("PARAMETER");
			parameter6.setAttribute("name", "PRGRICHIESTAAZ");
			parameter6.setAttribute("type", "RELATIVE");
			parameter6.setAttribute("value", "PRGRICHIESTAAZ");
			parameter6.setAttribute("scope", "SERVICE_REQUEST");
			// PRGAZIENDA
			SourceBean parameter7 = new SourceBean("PARAMETER");
			parameter7.setAttribute("name", "PRGAZIENDA");
			parameter7.setAttribute("type", "RELATIVE");
			parameter7.setAttribute("value", "PRGAZIENDA");
			parameter7.setAttribute("scope", "SERVICE_REQUEST");
			// PRGUNITA
			SourceBean parameter8 = new SourceBean("PARAMETER");
			parameter8.setAttribute("name", "PRGUNITA");
			parameter8.setAttribute("type", "RELATIVE");
			parameter8.setAttribute("value", "PRGUNITA");
			parameter8.setAttribute("scope", "SERVICE_REQUEST");
			// PRGINCROCIO
			SourceBean parameter9 = new SourceBean("PARAMETER");
			parameter9.setAttribute("name", "PRGINCROCIO");
			parameter9.setAttribute("type", "RELATIVE");
			parameter9.setAttribute("value", "PRGINCROCIO");
			parameter9.setAttribute("scope", "LOCAL");
			// MESSAGE
			/*******************************************************************
			 * non piu' necessari. Il custom tab inserisce le informazioni sul message e list_page anche nelle generic
			 * caption. Pero' la select caption continue a non gestire automaticamente questo parametro di paginazine
			 */
			SourceBean parameter10 = new SourceBean("PARAMETER");
			parameter10.setAttribute("name", "MESSAGE");
			parameter10.setAttribute("type", "RELATIVE");
			parameter10.setAttribute("value", "MESSAGE");
			parameter10.setAttribute("scope", "SERVICE_REQUEST");
			// LIST_PAGE=nro pagina
			SourceBean parameter11 = new SourceBean("PARAMETER");
			parameter11.setAttribute("name", "OLD_LIST_PAGE");
			parameter11.setAttribute("type", "RELATIVE");
			parameter11.setAttribute("value", "LIST_PAGE");
			parameter11.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean parameter12 = new SourceBean("PARAMETER");
			parameter12.setAttribute("name", "LIST_PAGE");
			parameter12.setAttribute("type", "RELATIVE");
			parameter12.setAttribute("value", "LIST_PAGE");
			parameter12.setAttribute("scope", "SERVICE_REQUEST");

			if (!("8").equalsIgnoreCase(prgTipoIncrocio) && !("9").equalsIgnoreCase(prgTipoIncrocio)
					&& !("13").equalsIgnoreCase(prgTipoIncrocio) && !("14").equalsIgnoreCase(prgTipoIncrocio)) {
				// per gli incroci diversi da LSU prgTipoIncrocio != 7
				if (!("7").equalsIgnoreCase(prgTipoIncrocio)) {
					SourceBean controllaPunteggioCaptionSB = new SourceBean("CAPTION");
					controllaPunteggioCaptionSB.setAttribute("image", "../../img/bilancia.gif");
					controllaPunteggioCaptionSB.setAttribute("label", "Dettaglio punteggio");
					controllaPunteggioCaptionSB.setAttribute("confirm", "false");
					controllaPunteggioCaptionSB.setAttribute(pageControllaPunteggio);
					controllaPunteggioCaptionSB.setAttribute(parameter2_a);
					controllaPunteggioCaptionSB.setAttribute(parameter2);
					controllaPunteggioCaptionSB.setAttribute(parameter3);
					controllaPunteggioCaptionSB.setAttribute(parameter4);
					controllaPunteggioCaptionSB.setAttribute(parameter5);
					controllaPunteggioCaptionSB.setAttribute(parameter6);
					controllaPunteggioCaptionSB.setAttribute(parameter7);
					controllaPunteggioCaptionSB.setAttribute(parameter8);
					controllaPunteggioCaptionSB.setAttribute(parameter10);
					controllaPunteggioCaptionSB.setAttribute(parameter11);
					controllaPunteggioCaptionSB.setAttribute(oldPage);

					captionsSB.setAttribute(controllaPunteggioCaptionSB);

				} else {
					// per gli incroci di tipo LSU si visualizza l'icona per la
					// maschera di inserimento dati carichi familiari,
					// professionalità ecc.

					SourceBean controllaDatiLSUCaptionSB = new SourceBean("CAPTION");
					controllaDatiLSUCaptionSB.setAttribute("image", "../../img/datiLSU.gif");
					controllaDatiLSUCaptionSB.setAttribute("label", "Dati LSU");
					controllaDatiLSUCaptionSB.setAttribute("confirm", "false");
					controllaDatiLSUCaptionSB.setAttribute(pageControllaDatiLSU);
					controllaDatiLSUCaptionSB.setAttribute(parameter2_a);
					controllaDatiLSUCaptionSB.setAttribute(parameter2);
					controllaDatiLSUCaptionSB.setAttribute(parameter3);
					controllaDatiLSUCaptionSB.setAttribute(parameter4);
					controllaDatiLSUCaptionSB.setAttribute(parameter5);
					controllaDatiLSUCaptionSB.setAttribute(parameter6);
					controllaDatiLSUCaptionSB.setAttribute(parameter7);
					controllaDatiLSUCaptionSB.setAttribute(parameter8);
					controllaDatiLSUCaptionSB.setAttribute(parameter10);
					controllaDatiLSUCaptionSB.setAttribute(parameter11);
					controllaDatiLSUCaptionSB.setAttribute(oldPage);

					captionsSB.setAttribute(controllaDatiLSUCaptionSB);
				}
			}

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			if (prgTipoRosa.equals("3")) {
				configSB.setAttribute("title", "Graduatoria Definitiva");
			} else {
				configSB.setAttribute("title", "Graduatoria Grezza");
			}
			configSB.setAttribute(colonneSB);

			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}