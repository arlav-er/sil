package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

// @author: Stefania Orioli

public class MatchCandidatiRosaConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MatchCandidatiRosaConfig.class.getName());

	public MatchCandidatiRosaConfig() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dettRosa = (SourceBean) response.getAttribute("MDETTAGLIOROSA");
		SourceBean row = (SourceBean) dettRosa.getAttribute("ROW");
		String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(row, "PRGTIPOINCROCIO");
		String prgTipoRosa = StringUtils.getAttributeStrNotNull(row, "PRGTIPOROSA");
		String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(request, "CDNSTATORICHORIG");

		SourceBean dettStatoRichOrig = (SourceBean) response.getAttribute("MatchStatoRichOrig.ROWS");
		String flgpubbcresco = dettStatoRichOrig.getAttribute("ROW.Flgpubbcresco") == null ? ""
				: (String) dettStatoRichOrig.getAttribute("ROW.Flgpubbcresco");

		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "MatchDettRosaPage");
		boolean disponibilita = attributi.containsButton("DISPONIBILITA");
		boolean esito = attributi.containsButton("ESITO");
		boolean stampa = attributi.containsButton("STAMPA");
		boolean stampaCM = attributi.containsButton("STAMPA-CM");
		boolean infCorrentiLav = attributi.containsButton("INF_CORR_LAV");
		boolean pesiCandidato = attributi.containsButton("PESI_CANDIDATO");
		boolean delLogica = attributi.containsButton("DEL_LOGICA");
		boolean delFisica = attributi.containsButton("DEL_FISICA");
		boolean delMassivaCandidati = attributi.containsButton("DEL_LOGICA_MASSIVA");
		// controllo per permettere l'invio degli sms tramite selezione massiva
		boolean invioSMS = attributi.containsButton("INVIOSMS");

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_6_b = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");
			SourceBean col_11 = new SourceBean("COLUMN");
			SourceBean col_12 = new SourceBean("COLUMN");
			SourceBean col_13 = new SourceBean("COLUMN");
			SourceBean col_14 = new SourceBean("COLUMN");
			SourceBean col_15 = new SourceBean("COLUMN");
			SourceBean col_16 = new SourceBean("COLUMN");
			SourceBean col_17 = new SourceBean("COLUMN");
			SourceBean col_18 = new SourceBean("COLUMN");

			col_13.setAttribute("name", "checkgradoocc");
			// col_11.setAttribute("label", "Ultimo Contatto");
			col_13.setAttribute("label", "");
			colonneSB.setAttribute(col_13);

			col_1.setAttribute("name", "STRCOGNOMENOME");
			col_1.setAttribute("label", "Cognome e Nome");
			colonneSB.setAttribute(col_1);

			/*
			 * Savino 08/03/2005 requisito RL15
			 * 
			 * col_2.setAttribute("name", "DATNASC"); col_2.setAttribute("label", "Data di Nascita");
			 * colonneSB.setAttribute(col_2);
			 */
			col_3.setAttribute("name", "COMUNEDOMICILIO");
			col_3.setAttribute("label", "Comune Domicilio");
			// colonneSB.setAttribute(col_3);

			// Al momento questa colonna è stata nascosta.....
			col_4.setAttribute("name", "STATOOCCUPAZ");
			col_4.setAttribute("label", "Stato Occupazionale");
			// colonneSB.setAttribute(col_4);

			col_12.setAttribute("name", "CODSTATOOCCUPAZ");
			// col_12.setAttribute("label", "Stato Occup.");
			col_12.setAttribute("label", "S.O.");
			colonneSB.setAttribute(col_12);

			col_5.setAttribute("name", "DECINDICEVICINANZA");
			// col_5.setAttribute("label", "Indice Vicin.");
			col_5.setAttribute("label", "Peso");
			if (prgTipoIncrocio.equals("2")) {
				colonneSB.setAttribute(col_5);
			}

			// Aggiunto da Paolo Roccetti il 08/09/2004
			SourceBean col_5_b = new SourceBean("COLUMN");
			// col_5_b.setAttribute("name", "condizioni");
			col_5_b.setAttribute("name", "condizioniCC");
			col_5_b.setAttribute("label", "Cnd.");
			col_5_b.setAttribute("PRE", "1");
			colonneSB.setAttribute(col_5_b);
			// FINE - Aggiunto da Paolo Roccetti il 08/09/2004

			// col_6.setAttribute("name", "REPERIB");
			col_6.setAttribute("name", "REPERIBCC");
			col_6.setAttribute("label", "Reperibilit&agrave;");
			col_6.setAttribute("PRE", "1");
			colonneSB.setAttribute(col_6);

			col_6_b.setAttribute("name", "DOMICILIO");
			col_6_b.setAttribute("label", "Domicilio");
			colonneSB.setAttribute(col_6_b);

			// col_7.setAttribute("name", "LISTESPECIALI");
			col_7.setAttribute("name", "LISTESPECIALICC");
			col_7.setAttribute("label", "Liste Speciali");
			col_7.setAttribute("PRE", "1");
			colonneSB.setAttribute(col_7);

			// Al momento questa colonna è stata nascosta.....
			col_8.setAttribute("name", "CPICOMPETENZA");
			col_8.setAttribute("label", "CpI di competenza");
			// colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "DISPONIBILITA");
			col_9.setAttribute("label", "Disp.");
			colonneSB.setAttribute(col_9);

			col_10.setAttribute("name", "PATTO297");
			col_10.setAttribute("label", "Patto 150");
			colonneSB.setAttribute(col_10);

			col_11.setAttribute("name", "DATAULTIMOCONTATTO");
			// col_11.setAttribute("label", "Ultimo Contatto");
			col_11.setAttribute("label", "Ultimo Cont.");
			colonneSB.setAttribute(col_11);

			col_16.setAttribute("name", "DISOCCUPATI");
			col_16.setAttribute("label", "Disocc.");
			if (flgpubbcresco.equals("S")) {
				colonneSB.setAttribute(col_16);
			}

			col_15.setAttribute("name", "INTERMITTENTI");
			col_15.setAttribute("label", "Interm.");
			if (flgpubbcresco.equals("S")) {
				colonneSB.setAttribute(col_15);
			}

			col_17.setAttribute("name", "GARANZIAGIOVANI");
			col_17.setAttribute("label", "Garanzia Giovani");
			if (flgpubbcresco.equals("S")) {
				colonneSB.setAttribute(col_17);
			}

			col_18.setAttribute("name", "PACCHETTOADULTI");
			col_18.setAttribute("label", "Pacchetto Adulti");
			if (flgpubbcresco.equals("S")) {
				colonneSB.setAttribute(col_18);
			}

			// CHECKBOXES
			SourceBean checkSB = new SourceBean("CHECKBOXES");
			SourceBean checkEscl = new SourceBean("CHECKBOX");
			checkEscl.setAttribute("name", "CK_ESCL");
			checkEscl.setAttribute("label", "");
			checkEscl.setAttribute("refColumn", "");
			checkEscl.setAttribute("jsCheckBoxClick", "");
			SourceBean checkEsclValue = new SourceBean("CHECKBOXVALUE");
			checkEsclValue.setAttribute("name", "PRGNOMINATIVO");
			checkEsclValue.setAttribute("scope", "LOCAL");
			checkEsclValue.setAttribute("type", "RELATIVE");
			checkEsclValue.setAttribute("value", "PRGNOMINATIVO");
			checkEscl.setAttribute(checkEsclValue);
			/*
			 * if (!prgTipoRosa.equals("3") && (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5"))) {
			 * checkSB.setAttribute(checkEscl); }
			 */
			// aggiunto controllo invio SMS
			if ((delMassivaCandidati || invioSMS) && ((!cdnStatoRichOrig.equals("5") && !prgTipoRosa.equals("3"))
					&& ((!cdnStatoRichOrig.equals("4") && !prgTipoIncrocio.equals("4"))
							|| (!cdnStatoRichOrig.equals("5") && prgTipoIncrocio.equals("4"))))) {
				checkSB.setAttribute(checkEscl);
			}
			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page DispInsRosaPage
			SourceBean pageDispo = new SourceBean("PARAMETER");
			pageDispo.setAttribute("name", "PAGE");
			pageDispo.setAttribute("type", "ABSOLUTE");
			pageDispo.setAttribute("value", "DispInsRosaPage");
			pageDispo.setAttribute("scope", "");
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
			// Page CMPage
			SourceBean CMPage = new SourceBean("PARAMETER");
			CMPage.setAttribute("name", "PAGE");
			CMPage.setAttribute("type", "ABSOLUTE");
			CMPage.setAttribute("value", "MatchDettRosaPage");
			CMPage.setAttribute("scope", "");
			// Page AmstrInfoCorrentiPage
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
			// PRGROSA
			SourceBean parameter2 = new SourceBean("PARAMETER");
			parameter2.setAttribute("name", "PRGROSA");
			parameter2.setAttribute("type", "RELATIVE");
			parameter2.setAttribute("value", "PRGROSA");
			parameter2.setAttribute("scope", "SERVICE_REQUEST");
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
			// PRGORIGINALE
			SourceBean parameter6_1 = new SourceBean("PARAMETER");
			parameter6_1.setAttribute("name", "PRGORIGINALE");
			parameter6_1.setAttribute("type", "RELATIVE");
			parameter6_1.setAttribute("value", "PRGORIGINALE");
			parameter6_1.setAttribute("scope", "LOCAL");
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
			parameter11.setAttribute("name", "LIST_PAGE");
			parameter11.setAttribute("type", "RELATIVE");
			parameter11.setAttribute("value", "LIST_PAGE");
			parameter11.setAttribute("scope", "SERVICE_REQUEST");

			// CDNSTATORICHORIG
			SourceBean parameter12 = new SourceBean("PARAMETER");
			parameter12.setAttribute("name", "CDNSTATORICHORIG");
			parameter12.setAttribute("type", "RELATIVE");
			parameter12.setAttribute("value", "CDNSTATORICHORIG");
			parameter12.setAttribute("scope", "LOCAL");

			// PRGDIAGNOSI
			SourceBean parameter13 = new SourceBean("PARAMETER");
			parameter13.setAttribute("name", "PRGDIAGNOSI");
			parameter13.setAttribute("type", "RELATIVE");
			parameter13.setAttribute("value", "PRGDIAGNOSI");
			parameter13.setAttribute("scope", "LOCAL");

			// parametro ausiliario per tener traccia che si desidera effettuare una stampa
			SourceBean parameter14 = new SourceBean("PARAMETER");
			parameter14.setAttribute("name", "Stampa");
			parameter14.setAttribute("type", "ABSOLUTE");
			parameter14.setAttribute("value", "StampaDiagnosi");

			// Parameter Stampa CM
			SourceBean parameter15 = new SourceBean("PARAMETER");
			parameter15.setAttribute("name", "isCM");
			parameter15.setAttribute("type", "ABSOLUTE");
			if (stampaCM) {
				parameter15.setAttribute("value", "true");
			} else {
				parameter15.setAttribute("value", "false");
			}
			parameter15.setAttribute("scope", "");

			// PRGSPI
			SourceBean parameterSpi = new SourceBean("PARAMETER");
			parameterSpi.setAttribute("name", "PRGSPI");
			parameterSpi.setAttribute("type", "RELATIVE");
			parameterSpi.setAttribute("value", "PRGSPI");
			parameterSpi.setAttribute("scope", "LOCAL");

			// POPUP EVIDENZE
			SourceBean evid = new SourceBean("PARAMETER");
			evid.setAttribute("name", "APRI_EV");
			evid.setAttribute("type", "ABSOLUTE");
			evid.setAttribute("value", "1");
			evid.setAttribute("scope", "");
			// cancellazione fisica del candidato dalla rosa (solo rosa nomintiva)
			SourceBean parCancFisica = new SourceBean("PARAMETER");
			parCancFisica.setAttribute("name", "MODULE");
			parCancFisica.setAttribute("type", "ABSOLUTE");
			parCancFisica.setAttribute("value", "RNG_DEL_LAV");
			parCancFisica.setAttribute("scope", "");
			// <SELECT_CAPTION>
			if (disponibilita && ((!prgTipoRosa.equals("3")
					&& (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")))
					&& !(!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRichOrig.equals("5"))))) {
				SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
				selectCaptionSB.setAttribute("image", "../../img/disp_lav.gif");
				selectCaptionSB.setAttribute("label", "Disponibilit&agrave; Rosa");
				selectCaptionSB.setAttribute("confirm", "false");
				selectCaptionSB.setAttribute(pageDispo);
				selectCaptionSB.setAttribute(parameter1);
				selectCaptionSB.setAttribute(parameter2);
				selectCaptionSB.setAttribute(parameter3);
				selectCaptionSB.setAttribute(parameter4);
				selectCaptionSB.setAttribute(parameter5);
				selectCaptionSB.setAttribute(parameter6);
				selectCaptionSB.setAttribute(parameter6_1);
				selectCaptionSB.setAttribute(parameter7);
				selectCaptionSB.setAttribute(parameter8);
				// 140/02/2005 Andrea Savino
				// necessari perche' nella select caption non vengono inseriti dal tag.
				// al momento funziona
				selectCaptionSB.setAttribute(parameter10);
				selectCaptionSB.setAttribute(parameter11);
				selectCaptionSB.setAttribute(parameterSpi);
				selectCaptionSB.setAttribute(evid);

				captionsSB.setAttribute(selectCaptionSB);
			}

			// <DELETE_CAPTION>
			if (delLogica && ((!prgTipoRosa.equals("3")
					&& (!cdnStatoRichOrig.equals("4") && !cdnStatoRichOrig.equals("5")))
					|| (!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRichOrig.equals("5"))))) {
				SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
				deleteCaptionSB.setAttribute("image", "../../img/del.gif");
				deleteCaptionSB.setAttribute("label", "Escludi Nominativo");
				deleteCaptionSB.setAttribute("confirm", "false");
				deleteCaptionSB.setAttribute(pageEscl);
				deleteCaptionSB.setAttribute(parameter1);
				deleteCaptionSB.setAttribute(parameter2);
				deleteCaptionSB.setAttribute(parameter3);
				deleteCaptionSB.setAttribute(parameter4);
				deleteCaptionSB.setAttribute(parameter5);
				deleteCaptionSB.setAttribute(parameter6);
				deleteCaptionSB.setAttribute(parameter7);
				deleteCaptionSB.setAttribute(parameter8);
				// 14/02/2005 Andrea Savino
				// il parametro MESSAGE sembra non essere utilizzato
				deleteCaptionSB.setAttribute(parameter10);
				// deleteCaptionSB.setAttribute(parameter11); -- lo mette la tag???

				captionsSB.setAttribute(deleteCaptionSB);
			}
			// <CAPTION> PESI
			if (pesiCandidato && prgTipoIncrocio.equals("2")) {
				SourceBean caption1 = new SourceBean("CAPTION");
				caption1.setAttribute("image", "../../img/pesi.gif");
				caption1.setAttribute("label", "Dettaglio Pesi");
				caption1.setAttribute("confirm", "false");
				caption1.setAttribute(pagePesi);
				caption1.setAttribute(parameter1);
				caption1.setAttribute(parameter2);
				caption1.setAttribute(parameter9);
				caption1.setAttribute(parameter3);
				caption1.setAttribute(parameter4);
				caption1.setAttribute(parameter5);
				caption1.setAttribute(parameter6);
				caption1.setAttribute(parameter7);
				caption1.setAttribute(parameter8);
				// ora vengono messi dal tab (generic caption)
				caption1.setAttribute(parameter10);
				// caption1.setAttribute(parameter11);

				captionsSB.setAttribute(caption1);
			}

			// <CAPTION> ESITO
			if (esito && prgTipoRosa.equals("3")) {
				SourceBean caption2 = new SourceBean("CAPTION");
				caption2.setAttribute("image", "../../img/esito.gif");
				caption2.setAttribute("label", "Esito");
				caption2.setAttribute("confirm", "false");
				caption2.setAttribute(pageEsito);
				caption2.setAttribute(parameter1);
				caption2.setAttribute(parameter2);
				caption2.setAttribute(parameter9);
				caption2.setAttribute(parameter3);
				caption2.setAttribute(parameter4);
				caption2.setAttribute(parameter5);
				caption2.setAttribute(parameter6);
				caption2.setAttribute(parameter6_1);
				caption2.setAttribute(parameter7);
				caption2.setAttribute(parameter8);
				// ora vengono messi dal tab (generic caption)
				// **************************************************************************************
				// IL PARAMETRO 10 E' UTILIZZATO DALLE PAGINE JSP CHIAMATE
				// PER POTER TORNARE INDIETRO CORRETTAMENTE. IN QUESTO CASO E' DUNQUE NECCESSARIO TENERLO
				caption2.setAttribute(parameter10);
				// caption2.setAttribute(parameter11);
				caption2.setAttribute(evid);
				caption2.setAttribute(retPage);
				captionsSB.setAttribute(caption2);
			}

			// PDF CV
			// Page REPORTFRAMEPAGE
			SourceBean pageRep = new SourceBean("PARAMETER");
			pageRep.setAttribute("name", "PAGE");
			pageRep.setAttribute("type", "ABSOLUTE");
			pageRep.setAttribute("value", "REPORTFRAMEPAGE");
			pageRep.setAttribute("scope", "");

			// Action RPT_CV_CANDIDATO_ROSA
			SourceBean actionSB = new SourceBean("PARAMETER");
			actionSB.setAttribute("name", "ACTION_REDIRECT");
			actionSB.setAttribute("type", "ABSOLUTE");
			actionSB.setAttribute("value", "RPT_CURR_DISP");
			actionSB.setAttribute("scope", "");

			// asAttachment
			SourceBean p1 = new SourceBean("PARAMETER");
			p1.setAttribute("name", "asAttachment");
			p1.setAttribute("type", "ABSOLUTE");
			p1.setAttribute("value", "false");
			p1.setAttribute("scope", "");
			// mostraPerLavoratore
			SourceBean p2 = new SourceBean("PARAMETER");
			p2.setAttribute("name", "mostraPerLavoratore");
			p2.setAttribute("type", "ABSOLUTE");
			p2.setAttribute("value", "true");
			p2.setAttribute("scope", "");
			// apri
			SourceBean p3 = new SourceBean("PARAMETER");
			p3.setAttribute("name", "apri");
			p3.setAttribute("type", "ABSOLUTE");
			p3.setAttribute("value", "true");
			p3.setAttribute("scope", "");
			// showNoteCPI
			SourceBean p5 = new SourceBean("PARAMETER");
			p5.setAttribute("name", "showNoteCPI");
			p5.setAttribute("type", "ABSOLUTE");
			p5.setAttribute("value", "true");
			p5.setAttribute("scope", "");

			// queryStringRosa
			String qs = "PAGE=MatchDettRosaPage";
			qs += "&CPIROSE=" + StringUtils.getAttributeStrNotNull(request, "CPIROSE");
			qs += "&PRGROSA=" + StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			qs += "&CDNFUNZIONE=" + StringUtils.getAttributeStrNotNull(request, "CDNFUNZIONE");
			qs += "&PRGRICHIESTAAZ=" + StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			qs += "&PRGAZIENDA=" + StringUtils.getAttributeStrNotNull(request, "PRGAZIENDA");
			qs += "&PRGUNITA=" + StringUtils.getAttributeStrNotNull(request, "PRGUNITA");

			String msg = StringUtils.getAttributeStrNotNull(request, "MESSAGE");
			if (msg.equals("")) {
				msg = "LIST_PAGE";
			}
			qs += "&MESSAGE=" + msg;
			String lp = StringUtils.getAttributeStrNotNull(request, "LIST_PAGE");
			/*
			 * if(lp.equals("")) { lp = "1"; } qs += "&LIST_PAGE=" + lp;
			 */
			if (!lp.equals("")) {
				qs += "&LIST_PAGE=" + lp;
			} else {
				if (msg.equals("LIST_FIRST") || (msg.equals("LIST_PAGE") && lp.equals(""))) {
					qs += "&LIST_PAGE=1";
				}
			}
			SourceBean p4 = new SourceBean("PARAMETER");
			p4.setAttribute("name", "QUERY_STRING");
			p4.setAttribute("type", "ABSOLUTE");
			p4.setAttribute("value", qs);
			p4.setAttribute("scope", "");

			// <CAPTION> - STAMPA SINTETICA CV - CM
			if (stampaCM) {
				SourceBean caption3 = new SourceBean("CAPTION");
				caption3.setAttribute("image", "../../img/stampa.gif");
				caption3.setAttribute("label", "Stampa CV CM");
				caption3.setAttribute("confirm", "false");
				caption3.setAttribute(pageRep);
				caption3.setAttribute(actionSB);
				caption3.setAttribute(p1);
				caption3.setAttribute(p2);
				caption3.setAttribute(p3);
				caption3.setAttribute(parameter4);
				caption3.setAttribute(p4);
				caption3.setAttribute(p5);
				caption3.setAttribute(parameter15);
				// Per nascondere questa icona commentare la riga seguente
				captionsSB.setAttribute(caption3);
			}
			// <CAPTION> - STAMPA SINTETICA CV
			else if (stampa) {
				SourceBean caption3 = new SourceBean("CAPTION");
				caption3.setAttribute("image", "../../img/stampa.gif");
				caption3.setAttribute("label", "Stampa CV");
				caption3.setAttribute("confirm", "false");
				caption3.setAttribute(pageRep);
				caption3.setAttribute(actionSB);
				caption3.setAttribute(p1);
				caption3.setAttribute(p2);
				caption3.setAttribute(p3);
				caption3.setAttribute(parameter4);
				caption3.setAttribute(p4);
				caption3.setAttribute(p5);
				caption3.setAttribute(parameter15);
				// Per nascondere questa icona commentare la riga seguente
				captionsSB.setAttribute(caption3);
			}

			if (stampaCM) {
				if (Sottosistema.CM.isOn() && prgTipoRosa.equals("2")) {
					SourceBean caption5 = new SourceBean("CAPTION");
					caption5.setAttribute("image", "../../img/print.gif");
					caption5.setAttribute("label", "Stampa Diagnosi");
					caption5.setAttribute("confirm", "false");
					caption5.setAttribute(CMPage);
					caption5.setAttribute(parameter1);
					caption5.setAttribute(parameter2);
					caption5.setAttribute(parameter5);
					caption5.setAttribute(parameter6);
					caption5.setAttribute(parameter7);
					caption5.setAttribute(parameter8);
					caption5.setAttribute(parameter12);
					caption5.setAttribute(parameter13);
					caption5.setAttribute(parameter14);
					caption5.setAttribute(parameter4);
					captionsSB.setAttribute(caption5);
				}
			}

			// INFORMAZIONI CORRENTI SUL LAVORATORE
			if (infCorrentiLav) {
				SourceBean caption4 = new SourceBean("CAPTION");
				caption4.setAttribute("image", "../../img/info_soggetto.gif");
				caption4.setAttribute("label", "Inf. correnti");
				caption4.setAttribute("confirm", "false");
				caption4.setAttribute(infoCorrPage);
				caption4.setAttribute(parameter4);
				caption4.setAttribute(evid);
				caption4.setAttribute(p4); // Attacco la queryString già calcolata per il CV
				captionsSB.setAttribute(caption4);
			}
			// DELETE FISICA CAPTION (solo rosa nominativa)
			if (delFisica
					&& (!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRichOrig.equals("5")))) {
				SourceBean deleteFisicaCaptionSB = new SourceBean("CAPTION");
				deleteFisicaCaptionSB.setAttribute("image", "../../img/del2.gif");
				deleteFisicaCaptionSB.setAttribute("label", "Cancella Nominativo");
				deleteFisicaCaptionSB.setAttribute("confirm", "TRUE");
				deleteFisicaCaptionSB.setAttribute(parPageQuestaLista);
				deleteFisicaCaptionSB.setAttribute(parameter1);
				deleteFisicaCaptionSB.setAttribute(parameter2);
				deleteFisicaCaptionSB.setAttribute(parameter3);
				deleteFisicaCaptionSB.setAttribute(parameter4);
				deleteFisicaCaptionSB.setAttribute(parameter5);
				deleteFisicaCaptionSB.setAttribute(parameter6);
				deleteFisicaCaptionSB.setAttribute(parameter6_1);
				deleteFisicaCaptionSB.setAttribute(parameter7);
				deleteFisicaCaptionSB.setAttribute(parameter8);
				// ora vengono messi dal tab (generic caption)
				// deleteFisicaCaptionSB.setAttribute(parameter10);
				// deleteFisicaCaptionSB.setAttribute(parameter11);
				deleteFisicaCaptionSB.setAttribute(parCancFisica);
				// deleteCaptionSB.setAttribute(parameter11); -- lo mette la tag???

				captionsSB.setAttribute(deleteFisicaCaptionSB);
			}
			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			if (prgTipoRosa.equals("3")) {
				configSB.setAttribute("title", "Rosa Definitiva dei Candidati");
			} else {
				configSB.setAttribute("title", "Rosa Grezza dei Candidati");
			}
			// configSB.setAttribute("title", "Rosa dei Candidati");
			// 14/025/2005 Andrea Savino
			// questo parametro e' utilizzato dalla classe del modulo che esegue la query,
			// non dal custom tag
			// configSB.setAttribute("rows", "2");
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