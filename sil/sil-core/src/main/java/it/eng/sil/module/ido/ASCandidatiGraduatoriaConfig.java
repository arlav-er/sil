package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

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
public class ASCandidatiGraduatoriaConfig extends AbstractConfigProvider {

	private static final String ASTA_ART_16 = "AS";

	private static final String GRADUATORIA_GREZZA = "2";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASCandidatiGraduatoriaConfig.class.getName());

	private String className = this.getClass().getName();

	public ASCandidatiGraduatoriaConfig() {
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
		Object prgRosa = row.getAttribute("PRGROSA");
		String prgTipoRosa = StringUtils.getAttributeStrNotNull(row, "PRGTIPOROSA");
		String codEvasione = StringUtils.getAttributeStrNotNull(row, "CODEVASIONE");
		// String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(request,
		// "CDNSTATORICH");

		SourceBean stato = (SourceBean) response.getAttribute("ASMATCHSTATORICHORIG.ROWS");
		String cdnStatoRichOrig = stato.getAttribute("ROW.CDNSTATORICH") == null ? ""
				: (String) stato.getAttribute("ROW.CDNSTATORICH");

		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "ASMatchDettGraduatoriaPage");

		boolean infCorrentiLav = attributi.containsButton("CONSULTA_ADESIONI");
		boolean delLogica = false;
		boolean delFisica = false;
		boolean datiLSU = attributi.containsButton("DATI_LSU");
		boolean rendiDef = attributi.containsButton("RENDI_DEFINITIVA");
		boolean punteggio = attributi.containsButton("PUNTEGGIO");
		boolean calcPunteggio = false;
		boolean posizione = false;
		boolean delMassivaCandidati = true;// attributi.containsButton("DEL_LOGICA_MASSIVA");

		if (cdnStatoRichOrig.compareTo("4") != 0 && cdnStatoRichOrig.compareTo("5") != 0) {
			delFisica = attributi.containsButton("DELETE_FISICA");
			delLogica = attributi.containsButton("DELETE_LOGICA");
			calcPunteggio = attributi.containsButton("CALC_PUNTEGGIO");
			posizione = attributi.containsButton("CALC_POSIZIONE");
		}

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
			// SourceBean col_10 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "numordine");
			col_1.setAttribute("label", "Pos.");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "strCognomeNome");
			col_2.setAttribute("label", "Cognome e Nome");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "DatNasc");
			col_3.setAttribute("label", "Dt. nascita");
			colonneSB.setAttribute(col_3);

			col_9.setAttribute("name", "Domicilio");
			col_9.setAttribute("label", "Domicilio");
			colonneSB.setAttribute(col_9);

			if (("8").equalsIgnoreCase(prgTipoIncrocio) || ("9").equalsIgnoreCase(prgTipoIncrocio)
					|| ("13").equalsIgnoreCase(prgTipoIncrocio) || ("14").equalsIgnoreCase(prgTipoIncrocio)) {

				col_4.setAttribute("name", "descrTipoMob");
				col_4.setAttribute("label", "Mobilità");
				colonneSB.setAttribute(col_4);

				col_5.setAttribute("name", "datiniziomov");
				col_5.setAttribute("label", "Dt. assunzione");
				colonneSB.setAttribute(col_5);

				col_6.setAttribute("name", "datfine");
				col_6.setAttribute("label", "Dt. inizio / fine mobilità");
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
			pageConsultaAdesioni.setAttribute("value", "ASListaAdesioniViewPage");
			pageConsultaAdesioni.setAttribute("scope", "");

			SourceBean cdnStatoRich = new SourceBean("PARAMETER");
			cdnStatoRich.setAttribute("name", "CDNSTATORICH");
			cdnStatoRich.setAttribute("type", "RELATIVE");
			cdnStatoRich.setAttribute("value", "CDNSTATORICH");
			cdnStatoRich.setAttribute("scope", "LOCAL");

			SourceBean pageCalcolaPunteggio = new SourceBean("PARAMETER");
			pageCalcolaPunteggio.setAttribute("name", "PAGE");
			pageCalcolaPunteggio.setAttribute("type", "ABSOLUTE");
			pageCalcolaPunteggio.setAttribute("value", "ASMatchDettGraduatoriaPage");
			pageCalcolaPunteggio.setAttribute("scope", "");

			SourceBean pageIndietroEsclusione = new SourceBean("PARAMETER");
			pageIndietroEsclusione.setAttribute("name", "PAGEINDIETRO");
			pageIndietroEsclusione.setAttribute("type", "ABSOLUTE");
			pageIndietroEsclusione.setAttribute("value", "ASMatchDettGraduatoriaPage");
			pageIndietroEsclusione.setAttribute("scope", "");

			SourceBean moduleCalcolaPunteggio = new SourceBean("PARAMETER");
			moduleCalcolaPunteggio.setAttribute("name", "MODULE");
			moduleCalcolaPunteggio.setAttribute("type", "ABSOLUTE");
			moduleCalcolaPunteggio.setAttribute("value", "ASCalcolaPunteggioModule");
			moduleCalcolaPunteggio.setAttribute("scope", "");

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

			// Marianna Borriello: Page PunteggiDidISee
			SourceBean pagePunteggiDidIsee = new SourceBean("PARAMETER");
			pagePunteggiDidIsee.setAttribute("name", "PAGE");
			pagePunteggiDidIsee.setAttribute("type", "ABSOLUTE");
			pagePunteggiDidIsee.setAttribute("value", "IdoPunteggiLavPage");
			pagePunteggiDidIsee.setAttribute("scope", "");

			SourceBean oldPage = new SourceBean("PARAMETER");
			oldPage.setAttribute("name", "OLD_PAGE");
			oldPage.setAttribute("type", "ABSOLUTE");
			oldPage.setAttribute("value", "ASMatchDettGraduatoriaPage");
			oldPage.setAttribute("scope", "");

			// Page EscludiDaRosaPage
			SourceBean pageEscl = new SourceBean("PARAMETER");
			pageEscl.setAttribute("name", "PAGE");
			pageEscl.setAttribute("type", "ABSOLUTE");
			pageEscl.setAttribute("value", "EscludiDaRosaPage");
			pageEscl.setAttribute("scope", "");

			SourceBean pageEsclLogica = new SourceBean("PARAMETER");
			pageEsclLogica.setAttribute("name", "PAGE");
			pageEsclLogica.setAttribute("type", "ABSOLUTE");
			pageEsclLogica.setAttribute("value", "ASEscludiDaRosaPage");
			pageEsclLogica.setAttribute("scope", "");

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
			// Page AmstrInfoCorrentiPage
			SourceBean infoCorrMobPage = new SourceBean("PARAMETER");
			infoCorrMobPage.setAttribute("name", "PAGE");
			infoCorrMobPage.setAttribute("type", "ABSOLUTE");
			infoCorrMobPage.setAttribute("value", "PercorsoMobilitaInfoStorDettPage");
			infoCorrMobPage.setAttribute("scope", "");
			// Page
			SourceBean parPageQuestaLista = new SourceBean("PARAMETER");
			parPageQuestaLista.setAttribute("name", "PAGE");
			parPageQuestaLista.setAttribute("type", "ABSOLUTE");
			parPageQuestaLista.setAttribute("value", pageQuestaLista);
			parPageQuestaLista.setAttribute("scope", "");
			// Marianna Borriello: Documenti Aste online
			SourceBean pageDocumentiAsOnline = new SourceBean("PARAMETER");
			pageDocumentiAsOnline.setAttribute("name", "PAGE");
			pageDocumentiAsOnline.setAttribute("type", "ABSOLUTE");
			pageDocumentiAsOnline.setAttribute("value", "ASDocumentiIstanzaPage");
			pageDocumentiAsOnline.setAttribute("scope", "");
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

			// LIST_PAGE=nro pagina
			SourceBean parameter13 = new SourceBean("PARAMETER");
			parameter13.setAttribute("name", "LIST_PAGE");
			parameter13.setAttribute("type", "ABSOLUTE");
			parameter13.setAttribute("value", "1");
			parameter13.setAttribute("scope", "");

			// PRGINCROCIO
			// SourceBean parameter14 = new SourceBean("PARAMETER");
			// parameter9.setAttribute("name", "PRGTIPOINCROCIO");
			// parameter9.setAttribute("type", "ABSOLUTE");
			// parameter9.setAttribute("value", "PRGTIPOINCROCIO");
			// parameter9.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean moduleCancellazioneLogica = new SourceBean("PARAMETER");
			moduleCancellazioneLogica.setAttribute("name", "MODULE");
			moduleCancellazioneLogica.setAttribute("type", "ABSOLUTE");
			moduleCancellazioneLogica.setAttribute("value", "ASCandidatiGraduatoria");
			moduleCancellazioneLogica.setAttribute("scope", "");

			// cancellazione fisica del candidato dalla graduatoria
			SourceBean parCancFisica = new SourceBean("PARAMETER");
			parCancFisica.setAttribute("name", "MODULE");
			parCancFisica.setAttribute("type", "ABSOLUTE");
			parCancFisica.setAttribute("value", "ASDelFisicaLavDaGraduatoria");
			parCancFisica.setAttribute("scope", "");

			SourceBean evidenze = new SourceBean("PARAMETER");
			evidenze.setAttribute("name", "APRI_EV");
			evidenze.setAttribute("type", "ABSOLUTE");
			evidenze.setAttribute("value", "1");
			evidenze.setAttribute("scope", "");

			// queryStringRosa
			String qs = "";
			if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(pageQuestaLista)) {
				qs += "PAGE=ASMatchDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria";
			} else {
				qs += "PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria";
			}
			qs += "&PRGROSA=" + StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			qs += "&CDNFUNZIONE=" + StringUtils.getAttributeStrNotNull(request, "CDNFUNZIONE");
			qs += "&PRGRICHIESTAAZ=" + StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			qs += "&PRGAZIENDA=" + StringUtils.getAttributeStrNotNull(request, "PRGAZIENDA");
			qs += "&PRGUNITA=" + StringUtils.getAttributeStrNotNull(request, "PRGUNITA");
			qs += "&CONCATENACPI=" + StringUtils.getAttributeStrNotNull(request, "CONCATENACPI");
			qs += "&CODCPI=" + StringUtils.getAttributeStrNotNull(request, "CODCPI");
			qs += "&PRGTIPOROSA=" + StringUtils.getAttributeStrNotNull(request, "PRGTIPOROSA");
			qs += "&PRGTIPOINCROCIO=" + StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");

			String lp = StringUtils.getAttributeStrNotNull(request, "LIST_PAGE");
			if (!lp.equals("")) {
				qs += "&LIST_PAGE=" + lp;
			} else {
				qs += "&LIST_PAGE=1";
			}

			SourceBean p4 = new SourceBean("PARAMETER");
			p4.setAttribute("name", "QUERY_STRING");
			p4.setAttribute("type", "ABSOLUTE");
			p4.setAttribute("value", qs);
			p4.setAttribute("scope", "");

			// <SELECT_CAPTION>
			if (!("8").equalsIgnoreCase(prgTipoIncrocio) && !("9").equalsIgnoreCase(prgTipoIncrocio)
					&& !("13").equalsIgnoreCase(prgTipoIncrocio) && !("14").equalsIgnoreCase(prgTipoIncrocio)) {
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

					if (punteggio) {
						captionsSB.setAttribute(controllaPunteggioCaptionSB);
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

					// Marianna Borriello: Button Modalità PunteggiDidISee
					// prima leggo la configurazione ASATTRIB se vale 1 allora si visualizza il pulsante altrimenti no
					SourceBean beanConfigRows = (SourceBean) QueryExecutor.executeQuery("AS_CONFIG_PUNTEGGI", null,
							"SELECT", "SIL_DATI");
					String configValue = (String) beanConfigRows.getAttribute("ROW.STRVALORECONFIG");
					BigDecimal numConfigValue = (BigDecimal) beanConfigRows.getAttribute("ROW.NUMVALORECONFIG");

					if (configValue.equalsIgnoreCase("1") || (numConfigValue != null && (numConfigValue.intValue() == 1
							|| numConfigValue.intValue() == 2 || numConfigValue.intValue() == 3))) {
						SourceBean punteggiCaptionSB = new SourceBean("CAPTION");
						punteggiCaptionSB.setAttribute("hiddenColumn", "viewBtnPunteggio");
						punteggiCaptionSB.setAttribute("image", "../../img/confr_punteggi.gif");
						if (numConfigValue != null
								&& (numConfigValue.intValue() == 2 || numConfigValue.intValue() == 3)) {
							punteggiCaptionSB.setAttribute("label", "Modalità di attribuzione dei punteggi art. 16");
						} else {
							punteggiCaptionSB.setAttribute("label", "Modalità di attribuzione dei punteggi DID e ISEE");
						}
						punteggiCaptionSB.setAttribute("confirm", "false");
						punteggiCaptionSB.setAttribute(pagePunteggiDidIsee);
						punteggiCaptionSB.setAttribute(parameter2_a);
						punteggiCaptionSB.setAttribute(parameter2);
						punteggiCaptionSB.setAttribute(parameter3);
						punteggiCaptionSB.setAttribute(parameter4);
						punteggiCaptionSB.setAttribute(parameter5);
						punteggiCaptionSB.setAttribute(parameter6);
						punteggiCaptionSB.setAttribute(parameter7);
						punteggiCaptionSB.setAttribute(parameter8);
						punteggiCaptionSB.setAttribute(parameter10);
						punteggiCaptionSB.setAttribute(parameter11);
						punteggiCaptionSB.setAttribute(oldPage);
						captionsSB.setAttribute(punteggiCaptionSB);
					}

					if (prgTipoRosa.equals(GRADUATORIA_GREZZA) && codEvasione.equals(ASTA_ART_16)) {

						SourceBean iseeWarningCaptionSB = new SourceBean("CAPTION");

						iseeWarningCaptionSB.setAttribute("image", "../../img/warning_trasp.gif");
						iseeWarningCaptionSB.setAttribute("label", "ISEE diverso da ISEE Storico");
						iseeWarningCaptionSB.setAttribute("confirm", "false");
						iseeWarningCaptionSB.setAttribute("hiddenColumn", "HIDDENICONAISEE");
						iseeWarningCaptionSB.setAttribute(pageControllaPunteggio);
						iseeWarningCaptionSB.setAttribute(parameter2_a);
						iseeWarningCaptionSB.setAttribute(parameter2);
						iseeWarningCaptionSB.setAttribute(parameter3);
						iseeWarningCaptionSB.setAttribute(parameter4);
						iseeWarningCaptionSB.setAttribute(parameter5);
						iseeWarningCaptionSB.setAttribute(parameter6);
						iseeWarningCaptionSB.setAttribute(parameter7);
						iseeWarningCaptionSB.setAttribute(parameter8);
						iseeWarningCaptionSB.setAttribute(parameter10);
						iseeWarningCaptionSB.setAttribute(parameter11);

						captionsSB.setAttribute(iseeWarningCaptionSB);
					}

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

					if (datiLSU) {
						captionsSB.setAttribute(controllaDatiLSUCaptionSB);
					}
				}

				// Marianna Borriello: Button documenti istanza (per istanze on line)
				// prima leggo la configurazione ASONLINE se vale 1 allora si visualizza il pulsante altrimenti no
				SourceBean asOnLineConfigRows = (SourceBean) QueryExecutor.executeQuery("AS_CONFIG_ASONLINE", null,
						"SELECT", "SIL_DATI");
				BigDecimal numConfigValue = (BigDecimal) asOnLineConfigRows.getAttribute("ROW.NUMVALORECONFIG");

				// attributi visibilità bottone documenti istanza (per istanze on line)
				boolean canDocumentiAs = attributi.containsButton("AS_DOC_IST");

				if (canDocumentiAs && numConfigValue != null && numConfigValue.intValue() == 1
						&& codEvasione.equals(ASTA_ART_16)) {
					SourceBean documentiIstanzaCaption = new SourceBean("CAPTION");
					documentiIstanzaCaption.setAttribute("hiddenColumn", "viewBtnDocumentiIstanza");
					documentiIstanzaCaption.setAttribute("image", "../../img/confr_punteggi.gif");
					documentiIstanzaCaption.setAttribute("label", "Documenti istanza");
					documentiIstanzaCaption.setAttribute("confirm", "false");
					documentiIstanzaCaption.setAttribute(pageDocumentiAsOnline);
					documentiIstanzaCaption.setAttribute(parameter2_a);
					documentiIstanzaCaption.setAttribute(parameter2);
					documentiIstanzaCaption.setAttribute(parameter3);
					documentiIstanzaCaption.setAttribute(parameter4);
					documentiIstanzaCaption.setAttribute(parameter5);
					documentiIstanzaCaption.setAttribute(parameter6);
					documentiIstanzaCaption.setAttribute(parameter7);
					documentiIstanzaCaption.setAttribute(parameter8);
					documentiIstanzaCaption.setAttribute(parameter10);
					documentiIstanzaCaption.setAttribute(parameter11);
					documentiIstanzaCaption.setAttribute(oldPage);
					captionsSB.setAttribute(documentiIstanzaCaption);
				}

			} else {
				if (infCorrentiLav) {
					SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
					selectCaptionSB.setAttribute("image", "../../img/info_soggetto.gif");
					selectCaptionSB.setAttribute("label", "Inf. correnti");
					selectCaptionSB.setAttribute("confirm", "false");
					selectCaptionSB.setAttribute(infoCorrPage);
					selectCaptionSB.setAttribute(parameter4);
					selectCaptionSB.setAttribute(evidenze);
					selectCaptionSB.setAttribute(p4);

					captionsSB.setAttribute(selectCaptionSB);
				}
			}
			// DELETE FISICA CAPTION (solo rosa nominativa)
			if (delFisica || delLogica) {
				if (!prgTipoRosa.equals("3") && (prgTipoIncrocio.equals("5") || prgTipoIncrocio.equals("6")
						|| prgTipoIncrocio.equals("7") || prgTipoIncrocio.equals("8") || prgTipoIncrocio.equals("9")
						|| prgTipoIncrocio.equals("13") || prgTipoIncrocio.equals("14"))) {
					if (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")) {
						if (delLogica) {
							SourceBean deleteLogicaCaptionSB = new SourceBean("CAPTION");
							deleteLogicaCaptionSB.setAttribute("image", "../../img/del.gif");
							deleteLogicaCaptionSB.setAttribute("label", "Esclusione logica da graduatoria");
							deleteLogicaCaptionSB.setAttribute("confirm", "false");
							deleteLogicaCaptionSB.setAttribute("ONCLICK", "deleteLogica");
							deleteLogicaCaptionSB.setAttribute(pageEsclLogica);
							deleteLogicaCaptionSB.setAttribute(moduleCancellazioneLogica);
							deleteLogicaCaptionSB.setAttribute(parameter3);
							deleteLogicaCaptionSB.setAttribute(parameter2);
							deleteLogicaCaptionSB.setAttribute(parameter4);
							deleteLogicaCaptionSB.setAttribute(parameter5);
							deleteLogicaCaptionSB.setAttribute(parameter6);
							deleteLogicaCaptionSB.setAttribute(parameter7);
							deleteLogicaCaptionSB.setAttribute(parameter8);
							deleteLogicaCaptionSB.setAttribute(parameter9);
							deleteLogicaCaptionSB.setAttribute(parameter10);
							deleteLogicaCaptionSB.setAttribute(parameter2_a);
							deleteLogicaCaptionSB.setAttribute(cdnStatoRich);
							captionsSB.setAttribute(deleteLogicaCaptionSB);
						}

						if (delFisica) {
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
							captionsSB.setAttribute(deleteFisicaCaptionSB);
						}

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