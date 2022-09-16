package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class CMListaNominativiGradAnnualeConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMListaNominativiGradAnnualeConfig.class.getName());

	public CMListaNominativiGradAnnualeConfig() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();

		// attributi visibilità
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "CMInsertGradAnnualePage");
		boolean infCorrentiLav = attributi.containsButton("INF_CORR_LAV");
		boolean punteggioCandidato = attributi.containsButton("PUNTEGGIO_CANDIDATO");

		Object prgGraduatoria = response.getAttribute("M_RIGENERAGRADANNUALEMODULE.PRGGRADUATORIA");
		if (prgGraduatoria == null) {
			prgGraduatoria = response.getAttribute("M_INSERTGRADANNUALEMODULE.PRGGRADUATORIA");
			if (prgGraduatoria == null) {
				prgGraduatoria = request.getAttribute("PRGGRADUATORIA");
			}
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
			SourceBean col_10 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "numordine");
			col_1.setAttribute("label", "Pos.");
			colonneSB.setAttribute(col_1);

			// col_2.setAttribute("name", "codFisc");
			// col_2.setAttribute("label", "Cod. Fisc.");
			// colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "strCognomeNome");
			col_3.setAttribute("label", "Cognome e Nome");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "datNasc");
			col_4.setAttribute("label", "Dt.<BR> Nascita");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "Domicilio");
			col_5.setAttribute("label", "Domicilio");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "numpunteggio");
			col_6.setAttribute("label", "Punt.");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "dataCM");
			col_7.setAttribute("label", "Data Anz.<BR> Data Iniz. CM");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "numreddito");
			col_8.setAttribute("label", "Reddito");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "numpersone");
			col_9.setAttribute("label", "Carico");
			colonneSB.setAttribute(col_9);

			col_10.setAttribute("name", "strnota");
			col_10.setAttribute("label", "Nota");
			colonneSB.setAttribute(col_10);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// parametri per le caption
			// Page CMDettaglioPunteggioGradAnnLavoratorePage
			SourceBean pageDettaglio = new SourceBean("PARAMETER");
			pageDettaglio.setAttribute("name", "PAGE");
			pageDettaglio.setAttribute("type", "ABSOLUTE");
			pageDettaglio.setAttribute("value", "CMDettaglioPunteggioGradAnnLavoratorePage");
			pageDettaglio.setAttribute("scope", "");

			// Page AmstrInfoCorrentiPage
			SourceBean infoCorrPage = new SourceBean("PARAMETER");
			infoCorrPage.setAttribute("name", "PAGE");
			infoCorrPage.setAttribute("type", "ABSOLUTE");
			infoCorrPage.setAttribute("value", "AmstrInfoCorrentiPage");
			infoCorrPage.setAttribute("scope", "");

			// Page AmstrInfoCorrentiPage
			SourceBean parPageQuestaLista = new SourceBean("PARAMETER");
			parPageQuestaLista.setAttribute("name", "PAGE");
			parPageQuestaLista.setAttribute("type", "ABSOLUTE");
			parPageQuestaLista.setAttribute("value", pageQuestaLista);
			parPageQuestaLista.setAttribute("scope", "");

			// PRGGRADUATORIA
			SourceBean parameter2 = new SourceBean("PARAMETER");
			parameter2.setAttribute("name", "PRGGRADUATORIA");
			parameter2.setAttribute("type", "RELATIVE");
			parameter2.setAttribute("value", "PRGGRADUATORIA");
			parameter2.setAttribute("scope", "LOCAL");
			// PRGNOMINATIVO
			SourceBean parameter3 = new SourceBean("PARAMETER");
			parameter3.setAttribute("name", "PRGGRADNOMINATIVO");
			parameter3.setAttribute("type", "RELATIVE");
			parameter3.setAttribute("value", "PRGGRADNOMINATIVO");
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
			parameter11.setAttribute("type", "ABSOLUTE");
			parameter11.setAttribute("value", "1");
			parameter11.setAttribute("scope", "");

			// POPUP EVIDENZE
			SourceBean evid = new SourceBean("PARAMETER");
			evid.setAttribute("name", "APRI_EV");
			evid.setAttribute("type", "ABSOLUTE");
			evid.setAttribute("value", "1");
			evid.setAttribute("scope", "");

			// <SELECT_CAPTION>

			// queryString
			String qs = "PAGE=CMInsertGradAnnualePage";
			qs += "&MODULE=M_ListaNominativiGradAnnualeModule";
			qs += "&CDNFUNZIONE=" + StringUtils.getAttributeStrNotNull(request, "CDNFUNZIONE");
			qs += "&PRGGRADUATORIA=" + prgGraduatoria.toString();

			String msg = StringUtils.getAttributeStrNotNull(request, "MESSAGE");
			if (msg.equals("")) {
				msg = "LIST_PAGE";
			}
			qs += "&MESSAGE=" + msg;
			String lp = StringUtils.getAttributeStrNotNull(request, "LIST_PAGE");
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

			// INFORMAZIONI CORRENTI SUL LAVORATORE
			// if (infCorrentiLav) {
			SourceBean caption4 = new SourceBean("SELECT_CAPTION");
			caption4.setAttribute("image", "../../img/info_soggetto.gif");
			caption4.setAttribute("label", "Inf. correnti");
			caption4.setAttribute("confirm", "false");
			caption4.setAttribute(infoCorrPage);
			caption4.setAttribute(parameter4);
			caption4.setAttribute(evid);
			// caption4.setAttribute(parameter2);
			caption4.setAttribute(p4); // Attacco la queryString già calcolata per il CV
			captionsSB.setAttribute(caption4);
			// }

			// <CAPTION> PUNTEGGIO
			SourceBean caption1 = new SourceBean("CAPTION");
			caption1.setAttribute("image", "../../img/pesi.gif");
			caption1.setAttribute("label", "Dettaglio Punteggio");
			caption1.setAttribute("confirm", "false");
			caption1.setAttribute(pageDettaglio);
			caption1.setAttribute(parameter4);
			caption1.setAttribute(parameter3);
			// caption1.setAttribute(parameter2);
			caption1.setAttribute(p4);
			captionsSB.setAttribute(caption1);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Elenco nominativi da graduatoria annuale");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);
		}
		return configSB;
	}

}