package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.PropertiesReport;
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
public class CMCandidatiGraduatoriaConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMCandidatiGraduatoriaConfig.class.getName());

	private String className = this.getClass().getName();

	public CMCandidatiGraduatoriaConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		Object params[] = new Object[0];
		SourceBean res = (SourceBean) com.engiweb.framework.util.QueryExecutor
				.executeQuery("CHECK_VERSIONE_GRADUATORIA", params, "SELECT", Values.DB_SIL_DATI);
		String codmonotipogradcm = StringUtils.getAttributeStrNotNull(res, "ROW.CODMONOTIPOGRADCM");

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dettRosa = (SourceBean) response.getAttribute("CMDETTAGLIOGRADUATORIA");
		SourceBean row = (SourceBean) dettRosa.getAttribute("ROW");
		String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(row, "PRGTIPOINCROCIO");
		Object prgRosaFiglia = row.getAttribute("PRGROSAFIGLIA");
		Object prgRosa = row.getAttribute("PRGROSA");
		String prgTipoRosa = StringUtils.getAttributeStrNotNull(row, "PRGTIPOROSA");

		// String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(request, "CDNSTATORICH");

		SourceBean stato = (SourceBean) response.getAttribute("CMMATCHSTATORICHORIG.ROWS");
		String cdnStatoRichOrig = stato.getAttribute("ROW.CDNSTATORICH") == null ? ""
				: (String) stato.getAttribute("ROW.CDNSTATORICH");

		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "CMMatchDettGraduatoriaPage");

		boolean infCorrentiLav = attributi.containsButton("CONSULTA_ADESIONI");
		boolean delLogica = true;// attributi.containsButton("DEL_LOGICA");
		boolean delFisica = false;
		boolean rendiDef = attributi.containsButton("RENDI_DEFINITIVA");
		boolean punteggio = attributi.containsButton("PUNTEGGIO");
		boolean checkEvidenze = attributi.containsButton("EVIDENZE");
		boolean priorita = attributi.containsButton("PRIORITA");
		boolean calcPunteggio = false;
		boolean posizione = false;
		boolean delMassivaCandidati = true;// attributi.containsButton("DEL_LOGICA_MASSIVA");

		if (cdnStatoRichOrig.compareTo("4") != 0 && cdnStatoRichOrig.compareTo("5") != 0) {
			delFisica = attributi.containsButton("DELETE_FISICA");
			calcPunteggio = attributi.containsButton("CALC_PUNTEGGIO");
			posizione = attributi.containsButton("CALC_POSIZIONE");
		}

		String codRegioneSil = "";
		if (prgTipoRosa.equals("3")) {
			SourceBean rowInfo = (SourceBean) com.engiweb.framework.util.QueryExecutor
					.executeQuery("GET_INFO_TARGA_PROVINCIA_REGIONE", null, "SELECT", Values.DB_SIL_DATI);
			rowInfo = rowInfo.containsAttribute("ROW") ? (SourceBean) rowInfo.getAttribute("ROW") : rowInfo;
			codRegioneSil = SourceBeanUtils.getAttrStrNotNull(rowInfo, "codregione");
		}

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_2a = null;
			if (codRegioneSil.equalsIgnoreCase(PropertiesReport.COD_REGIONE_RER)) {
				col_2a = new SourceBean("COLUMN");
			}
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_4a = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_9a = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_12 = new SourceBean("COLUMN");
			SourceBean col_13 = new SourceBean("COLUMN");
			SourceBean col_14 = new SourceBean("COLUMN");
			SourceBean col_15 = new SourceBean("COLUMN");
			SourceBean col_16 = new SourceBean("COLUMN");
			SourceBean col_17 = new SourceBean("COLUMN");
			SourceBean col_18 = new SourceBean("COLUMN");

			col_17.setAttribute("name", "checkPunteggio");
			col_17.setAttribute("label", "");
			colonneSB.setAttribute(col_17);

			col_1.setAttribute("name", "numordine");
			col_1.setAttribute("label", "Pos.");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strCognomeNome");
			col_2.setAttribute("label", "Cognome e Nome");
			colonneSB.setAttribute(col_2);

			if (codRegioneSil.equalsIgnoreCase(PropertiesReport.COD_REGIONE_RER)) {
				col_2a.setAttribute("name", "codiceAdesione");
				col_2a.setAttribute("label", "Codice Adesione");
				colonneSB.setAttribute(col_2a);
			}

			col_3.setAttribute("name", "DatNasc");
			col_3.setAttribute("label", "Dt. nascita");
			colonneSB.setAttribute(col_3);

			if (!("12").equalsIgnoreCase(prgTipoIncrocio)) {
				col_9.setAttribute("name", "Domicilio");
				col_9.setAttribute("label", "Domicilio");
				colonneSB.setAttribute(col_9);

				col_4.setAttribute("name", "punteggio");
				col_4.setAttribute("label", "Punt. reale");
				colonneSB.setAttribute(col_4);

				if (!prgTipoRosa.equals("3") && prgRosaFiglia == null) {
					col_4a.setAttribute("name", "punteggioPres");
					col_4a.setAttribute("label", "Punt. pres.");
					colonneSB.setAttribute(col_4a);
				}

				/*
				 * col_5.setAttribute("name", "dataIscrCM"); col_5.setAttribute("label", "Data Iscr. CM");
				 * colonneSB.setAttribute(col_5);
				 * 
				 * col_16.setAttribute("name", "dataAnzCM"); col_16.setAttribute("label", "Data Anz. CM");
				 * colonneSB.setAttribute(col_16);
				 */

				col_5.setAttribute("name", "dateCM");
				col_5.setAttribute("label", "Data Anz. <br> Data Iniz. CM");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "reddito");
				if (("4").equals(codmonotipogradcm)) {
					col_6.setAttribute("label", "Reddito ISEE");
				} else {
					col_6.setAttribute("label", "Reddito CM");
				}
				colonneSB.setAttribute(col_6);

				if (("4").equals(codmonotipogradcm) == false) {
					col_7.setAttribute("name", "carico");
					col_7.setAttribute("label", "Ca <br> ri <br> co");
					colonneSB.setAttribute(col_7);
				}

				if (("4").equals(codmonotipogradcm)) {
					col_18.setAttribute("name", "qualificato");
					col_18.setAttribute("label", "Qualificato");
					colonneSB.setAttribute(col_18);
				}

				if (("4").equals(codmonotipogradcm) == false) {
					col_9a.setAttribute("name", "strpriorita");
					col_9a.setAttribute("label", "Prio <br> ri <br> tà");
					colonneSB.setAttribute(col_9a);
				}

			} else {
				col_10.setAttribute("name", "dataAnzArt1");
				col_10.setAttribute("label", "Data Anz. Art.1");
				colonneSB.setAttribute(col_10);

				// col_11.setAttribute("name", "dataIscrArt1");
				col_11.setAttribute("name", "DATAISCRART1");
				col_11.setAttribute("label", "Data Iscr. Art.1");
				colonneSB.setAttribute(col_11);

				// col_12.setAttribute("name", "numpunteggioArt1");
				col_12.setAttribute("name", "NUMPUNTEGGIOART1");
				col_12.setAttribute("label", "Punt.");
				colonneSB.setAttribute(col_12);

				// col_13.setAttribute("name", "numannopunteggioArt1");
				col_13.setAttribute("name", "NUMANNOPUNTEGGIOART1");
				col_13.setAttribute("label", "Anno Punt.");
				colonneSB.setAttribute(col_13);

				col_14.setAttribute("name", "descrProvCpiComp");
				col_14.setAttribute("label", "Prov. competenza");
				colonneSB.setAttribute(col_14);

				col_15.setAttribute("name", "ultMov");
				col_15.setAttribute("label", "Ultimo Mov.");
				colonneSB.setAttribute(col_15);

			}

			// CHECKBOXES AVVIA SELEZIONE
			SourceBean checkSB = new SourceBean("CHECKBOXES");
			SourceBean checkSel = new SourceBean("CHECKBOX");
			checkSel.setAttribute("name", "CK_");
			// checkSel.setAttribute("name", "CK_SEL");
			checkSel.setAttribute("label", "");
			checkSel.setAttribute("refColumn", "CDNLAVORATORE");
			// checkSel.setAttribute("refColumn", "");
			checkSel.setAttribute("jsCheckBoxClick", "setNominativoSB");
			SourceBean checkSelValue = new SourceBean("CHECKBOXVALUE");
			checkSelValue.setAttribute("name", "CDNLAVORATORE");
			checkSelValue.setAttribute("scope", "LOCAL");
			checkSelValue.setAttribute("type", "RELATIVE");
			checkSelValue.setAttribute("value", "CDNLAVORATORE");
			SourceBean checkParam = new SourceBean("PARAMETER");
			checkParam.setAttribute("name", "this");
			checkParam.setAttribute("typeOf", "Object");
			checkParam.setAttribute("scope", "");
			checkParam.setAttribute("type", "ABSOLUTE");
			checkParam.setAttribute("value", "this");
			SourceBean checkParam1 = new SourceBean("PARAMETER");
			checkParam1.setAttribute("name", "CDNLAVORATORE");
			checkParam1.setAttribute("typeOf", "");
			checkParam1.setAttribute("scope", "LOCAL");
			checkParam1.setAttribute("type", "RELATIVE");
			checkParam1.setAttribute("value", "CDNLAVORATORE");
			checkSel.setAttribute(checkSelValue);
			checkSel.setAttribute(checkParam);
			checkSel.setAttribute(checkParam1);

			if (prgTipoRosa.equals("3") && prgRosaFiglia == null) {
				checkSB.setAttribute(checkSel);
			}

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page DispInsRosaPage
			SourceBean pageConsultaAdesioni = new SourceBean("PARAMETER");
			pageConsultaAdesioni.setAttribute("name", "PAGE");
			pageConsultaAdesioni.setAttribute("type", "ABSOLUTE");
			pageConsultaAdesioni.setAttribute("value", "CMListaAdesioniViewPage");
			pageConsultaAdesioni.setAttribute("scope", "");

			SourceBean pageCalcolaPunteggio = new SourceBean("PARAMETER");
			pageCalcolaPunteggio.setAttribute("name", "PAGE");
			pageCalcolaPunteggio.setAttribute("type", "ABSOLUTE");
			pageCalcolaPunteggio.setAttribute("value", "CMMatchDettGraduatoriaPage");
			pageCalcolaPunteggio.setAttribute("scope", "");

			SourceBean moduleCalcolaPunteggio = new SourceBean("PARAMETER");
			moduleCalcolaPunteggio.setAttribute("name", "MODULE");
			moduleCalcolaPunteggio.setAttribute("type", "ABSOLUTE");
			moduleCalcolaPunteggio.setAttribute("value", "CMCalcolaPunteggioModule");
			moduleCalcolaPunteggio.setAttribute("scope", "");

			SourceBean pageControllaPunteggio = new SourceBean("PARAMETER");
			pageControllaPunteggio.setAttribute("name", "PAGE");
			pageControllaPunteggio.setAttribute("type", "ABSOLUTE");
			pageControllaPunteggio.setAttribute("value", "CMDettaglioPunteggioLavoratorePage");
			pageControllaPunteggio.setAttribute("scope", "");

			SourceBean pageControllaPunteggioPresunto = new SourceBean("PARAMETER");
			pageControllaPunteggioPresunto.setAttribute("name", "PAGE");
			pageControllaPunteggioPresunto.setAttribute("type", "ABSOLUTE");
			pageControllaPunteggioPresunto.setAttribute("value", "CMDettaglioPunteggioPresLavoratorePage");
			pageControllaPunteggioPresunto.setAttribute("scope", "");

			SourceBean oldPage = new SourceBean("PARAMETER");
			oldPage.setAttribute("name", "OLD_PAGE");
			oldPage.setAttribute("type", "ABSOLUTE");
			oldPage.setAttribute("value", "CMMatchDettGraduatoriaPage");
			oldPage.setAttribute("scope", "");

			// Page EscludiDaRosaPage
			SourceBean pageEscl = new SourceBean("PARAMETER");
			pageEscl.setAttribute("name", "PAGE");
			pageEscl.setAttribute("type", "ABSOLUTE");
			pageEscl.setAttribute("value", "EscludiDaRosaPage");
			pageEscl.setAttribute("scope", "");
			// Page CancellaLavoratoreDaGradPage
			SourceBean cancLavoratoreGradPage = new SourceBean("PARAMETER");
			cancLavoratoreGradPage.setAttribute("name", "PAGE");
			cancLavoratoreGradPage.setAttribute("type", "ABSOLUTE");
			cancLavoratoreGradPage.setAttribute("value", "CMCancLavoratoreGradPage");
			cancLavoratoreGradPage.setAttribute("scope", "");
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

			// Page priorità adesione
			SourceBean pagePriorita = new SourceBean("PARAMETER");
			pagePriorita.setAttribute("name", "PAGE");
			pagePriorita.setAttribute("type", "ABSOLUTE");
			pagePriorita.setAttribute("value", "CMPrioritaPage");
			pagePriorita.setAttribute("scope", "");

			// Page Gestione Qualificato
			SourceBean pageQualificato = new SourceBean("PARAMETER");
			pageQualificato.setAttribute("name", "PAGE");
			pageQualificato.setAttribute("type", "ABSOLUTE");
			pageQualificato.setAttribute("value", "CMQualificatoPage");
			pageQualificato.setAttribute("scope", "");

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
			/*
			 * SourceBean parameter2 = new SourceBean("PARAMETER"); parameter2.setAttribute("name", "PRGROSA");
			 * parameter2.setAttribute("type", "RELATIVE"); parameter2.setAttribute("value", "PRGROSA");
			 * parameter2.setAttribute("scope", "SERVICE_REQUEST");
			 */
			SourceBean parameter2 = new SourceBean("PARAMETER");
			parameter2.setAttribute("name", "PRGROSA");
			parameter2.setAttribute("type", "ABSOLUTE");
			parameter2.setAttribute("value", String.valueOf(prgRosa));
			parameter2.setAttribute("scope", "");
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

			SourceBean parameterTipoIncrocio = new SourceBean("PARAMETER");
			parameterTipoIncrocio.setAttribute("name", "PRGTIPOINCROCIO");
			parameterTipoIncrocio.setAttribute("type", "ABSOLUTE");
			parameterTipoIncrocio.setAttribute("value", String.valueOf(prgTipoIncrocio));
			parameterTipoIncrocio.setAttribute("scope", "");

			SourceBean parPriorita = new SourceBean("PARAMETER");
			parPriorita.setAttribute("name", "PRIORITA_PAR");
			parPriorita.setAttribute("type", "ABSOLUTE");
			parPriorita.setAttribute("value", "PRIORITA_PAR");
			parPriorita.setAttribute("scope", "");

			// MESSAGE
			/*********************************************************
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

			// LIST_PAGE=nro pagina
			SourceBean parameter13 = new SourceBean("PARAMETER");
			parameter13.setAttribute("name", "LIST_PAGE");
			parameter13.setAttribute("type", "ABSOLUTE");
			parameter13.setAttribute("value", "1");
			parameter13.setAttribute("scope", "");

			// cancellazione fisica del candidato dalla graduatoria
			SourceBean parCancFisica = new SourceBean("PARAMETER");
			parCancFisica.setAttribute("name", "MODULE");
			parCancFisica.setAttribute("type", "ABSOLUTE");
			parCancFisica.setAttribute("value", "CMDelFisicaLavDaGraduatoria");
			parCancFisica.setAttribute("scope", "");

			SourceBean evidenze = new SourceBean("PARAMETER");
			evidenze.setAttribute("name", "APRI_EV");
			evidenze.setAttribute("type", "ABSOLUTE");
			evidenze.setAttribute("value", "1");
			evidenze.setAttribute("scope", "");

			// <SELECT_CAPTION>
			if (!("12").equalsIgnoreCase(prgTipoIncrocio)) {
				if (infCorrentiLav) {
					SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
					selectCaptionSB.setAttribute("image", "../../img/campo.gif");
					selectCaptionSB.setAttribute("label", "Consulta adesioni");
					selectCaptionSB.setAttribute("confirm", "false");
					selectCaptionSB.setAttribute(pageConsultaAdesioni);
					selectCaptionSB.setAttribute(parameter1);
					selectCaptionSB.setAttribute(parameter2);
					selectCaptionSB.setAttribute(parameter3);
					selectCaptionSB.setAttribute(parameter4);
					selectCaptionSB.setAttribute(parameter5);
					selectCaptionSB.setAttribute(parameter6);
					selectCaptionSB.setAttribute(parameter7);
					selectCaptionSB.setAttribute(parameter8);
					selectCaptionSB.setAttribute(parameter10);
					selectCaptionSB.setAttribute(parameter11);
					selectCaptionSB.setAttribute(parameter2_a);
					selectCaptionSB.setAttribute(evidenze);

					captionsSB.setAttribute(selectCaptionSB);
				}
			} else {
				if (checkEvidenze) {
					SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
					selectCaptionSB.setAttribute("image", "../../img/campo.gif");
					selectCaptionSB.setAttribute("label", "Evidenze");
					selectCaptionSB.setAttribute("confirm", "false");
					selectCaptionSB.setAttribute(parameter4);
					selectCaptionSB.setAttribute(parameter5);
					selectCaptionSB.setAttribute(evidenze);

					captionsSB.setAttribute(selectCaptionSB);
				}
			}

			if (!("12").equalsIgnoreCase(prgTipoIncrocio)) {
				// pulsante per il dettaglio del punteggio
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

				if (punteggio) {
					captionsSB.setAttribute(controllaPunteggioCaptionSB);
				}

				// pulsante per il dettaglio del punteggio presunto
				SourceBean controllaPunteggioPresuntoCaptionSB = new SourceBean("CAPTION");
				controllaPunteggioPresuntoCaptionSB.setAttribute("image", "../../img/confr_punteggi.gif");
				controllaPunteggioPresuntoCaptionSB.setAttribute("label", "Confronto punteggi");
				controllaPunteggioPresuntoCaptionSB.setAttribute("confirm", "false");
				controllaPunteggioPresuntoCaptionSB.setAttribute(pageControllaPunteggioPresunto);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter2_a);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter2);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter3);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter4);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter5);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter6);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter7);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter8);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter10);
				controllaPunteggioPresuntoCaptionSB.setAttribute(parameter11);

				if (punteggio) {
					captionsSB.setAttribute(controllaPunteggioPresuntoCaptionSB);
				}

				// pulsante per il calcolo del punteggio
				if (!prgTipoRosa.equals("3") && prgRosaFiglia == null) {

					SourceBean punteggioCaptionSB = new SourceBean("CAPTION");
					punteggioCaptionSB.setAttribute("image", "../../img/calc.gif");
					punteggioCaptionSB.setAttribute("label", "Calcola punteggio");
					punteggioCaptionSB.setAttribute("confirm", "TRUE");
					punteggioCaptionSB.setAttribute(pageCalcolaPunteggio);
					punteggioCaptionSB.setAttribute(moduleCalcolaPunteggio);
					punteggioCaptionSB.setAttribute(parameter2_a);
					punteggioCaptionSB.setAttribute(parameter2);
					punteggioCaptionSB.setAttribute(parameter3);
					punteggioCaptionSB.setAttribute(parameter4);
					punteggioCaptionSB.setAttribute(parameter5);
					punteggioCaptionSB.setAttribute(parameter6);
					punteggioCaptionSB.setAttribute(parameter7);
					punteggioCaptionSB.setAttribute(parameter8);
					punteggioCaptionSB.setAttribute(parameter10);
					punteggioCaptionSB.setAttribute(parameter12);

					if (calcPunteggio) {
						captionsSB.setAttribute(punteggioCaptionSB);
					}
				}

			}

			// PRIORITA
			SourceBean prioritaCaptionSB = new SourceBean("CAPTION");
			prioritaCaptionSB.setAttribute("image", "../../img/priorita.gif");
			prioritaCaptionSB.setAttribute("label", "Priorità Adesione");
			prioritaCaptionSB.setAttribute("confirm", "false");
			prioritaCaptionSB.setAttribute(pagePriorita);
			prioritaCaptionSB.setAttribute(parameter1);
			prioritaCaptionSB.setAttribute(parameter2);
			prioritaCaptionSB.setAttribute(parameter3);
			prioritaCaptionSB.setAttribute(parameter4);
			prioritaCaptionSB.setAttribute(parameter5);
			prioritaCaptionSB.setAttribute(parPriorita);

			if (priorita) {
				captionsSB.setAttribute(prioritaCaptionSB);
			}

			if (("4").equals(codmonotipogradcm)) {
				if (!prgTipoRosa.equals("3") && (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11")
						|| prgTipoIncrocio.equals("12"))) {
					if (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")) {

						SourceBean qualificatoCaptionSB = new SourceBean("CAPTION");
						qualificatoCaptionSB.setAttribute("image", "../../img/validazione.gif");
						qualificatoCaptionSB.setAttribute("label", "Modifica qualificato");
						qualificatoCaptionSB.setAttribute("confirm", "false");
						qualificatoCaptionSB.setAttribute(pageQualificato);
						qualificatoCaptionSB.setAttribute(parameter1);
						qualificatoCaptionSB.setAttribute(parameter2);
						qualificatoCaptionSB.setAttribute(parameter3);
						qualificatoCaptionSB.setAttribute(parameter4);
						qualificatoCaptionSB.setAttribute(parameter5);
						qualificatoCaptionSB.setAttribute(parameter6);
						qualificatoCaptionSB.setAttribute(parameter7);
						qualificatoCaptionSB.setAttribute(parameter8);
						qualificatoCaptionSB.setAttribute(parameter10);
						qualificatoCaptionSB.setAttribute(parameter11);
						qualificatoCaptionSB.setAttribute(parameter2_a);

						captionsSB.setAttribute(qualificatoCaptionSB);
					}
				}
			}

			// CANCELLAZIONE LAVORATORE GRADUATORIA
			if (!prgTipoRosa.equals("3")
					&& (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12"))) {
				if (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")) {

					SourceBean cancLavCaptionSB = new SourceBean("CAPTION");
					cancLavCaptionSB.setAttribute("image", "../../img/del.gif");
					cancLavCaptionSB.setAttribute("label", "Cancella Lavoratore");
					cancLavCaptionSB.setAttribute("confirm", "true");
					cancLavCaptionSB.setAttribute(cancLavoratoreGradPage);
					cancLavCaptionSB.setAttribute(parameter1);
					cancLavCaptionSB.setAttribute(parameter2);
					cancLavCaptionSB.setAttribute(parameter3);
					cancLavCaptionSB.setAttribute(parameter4);
					cancLavCaptionSB.setAttribute(parameter5);
					cancLavCaptionSB.setAttribute(parameter6);
					cancLavCaptionSB.setAttribute(parameter7);
					cancLavCaptionSB.setAttribute(parameter8);
					cancLavCaptionSB.setAttribute(parameter10);
					cancLavCaptionSB.setAttribute(parameter11);
					cancLavCaptionSB.setAttribute(parameter2_a);

					captionsSB.setAttribute(cancLavCaptionSB);
				}
			}

			// DELETE FISICA CAPTION (solo rosa nominativa)
			if (delFisica) {
				if (!prgTipoRosa.equals("3") && (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11")
						|| prgTipoIncrocio.equals("12"))) {
					if (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")) {

						SourceBean deleteFisicaCaptionSB = new SourceBean("DELETE_CAPTION");
						deleteFisicaCaptionSB.setAttribute("image", "../../img/del2.gif");
						deleteFisicaCaptionSB.setAttribute("label", "Escludi da graduatoria");
						deleteFisicaCaptionSB.setAttribute("confirm", "false");
						deleteFisicaCaptionSB.setAttribute(parameter2);
						deleteFisicaCaptionSB.setAttribute(parameter3);
						deleteFisicaCaptionSB.setAttribute(parameter4);
						deleteFisicaCaptionSB.setAttribute(parameter5);
						deleteFisicaCaptionSB.setAttribute(parameter6);
						deleteFisicaCaptionSB.setAttribute(parameter7);
						deleteFisicaCaptionSB.setAttribute(parameter8);
						deleteFisicaCaptionSB.setAttribute(parameter9);
						deleteFisicaCaptionSB.setAttribute(parameter10);
						deleteFisicaCaptionSB.setAttribute(parameter12);
						deleteFisicaCaptionSB.setAttribute(parameter1_a);
						deleteFisicaCaptionSB.setAttribute(parameterTipoIncrocio);

						captionsSB.setAttribute(deleteFisicaCaptionSB);
					}
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

			configSB.setAttribute(checkSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}