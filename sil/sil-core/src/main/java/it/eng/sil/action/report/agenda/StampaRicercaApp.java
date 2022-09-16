package it.eng.sil.action.report.agenda;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.Utils;

public class StampaRicercaApp extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("ReportRicercaApp." + tipoFile);
			} else {
				setStrNomeDoc("ReportRicercaApp.pdf");
			}
			setStrDescrizione("Stampa risultati della ricerca appuntamenti");
			setReportPath("Agenda/REPORTLISTAAPP_CC.rpt");
			/*
			 * Vector params = null; params = new Vector(11); String sdsre =
			 * request.getAttribute("codicefiscale").toString();
			 * 
			 * params.add(request.getAttribute("codcpi")); params.add(request.getAttribute("codicefiscale"));
			 * params.add(request.getAttribute("cognome")); params.add(request.getAttribute("nome"));
			 * params.add(request.getAttribute("ragionesociale")); String sel_operatore =
			 * request.getAttribute("operatore").toString(); String sel_ambiente =
			 * request.getAttribute("ambiente").toString(); if (!sel_operatore.equals("")) { params.add(sel_operatore);
			 * } else { params.add ("-1"); } params.add(request.getAttribute("servizio")); if (!sel_ambiente.equals(""))
			 * { params.add(sel_ambiente); } else { params.add ("-1"); } params.add(request.getAttribute("esito"));
			 * params.add(request.getAttribute("dataDal")); params.add(request.getAttribute("dataAl")); // Aggiunti il
			 * 17/05/2005 - Stefy params.add(request.getAttribute("piva"));
			 * params.add(request.getAttribute("strCodiceFiscaleAz"));
			 * 
			 * setParams(params);
			 */

			String sel_ambiente = request.getAttribute("ambiente").toString();
			if (sel_ambiente.equals(""))
				sel_ambiente = "-1";
			String sel_operatore = request.getAttribute("operatore").toString();
			if (sel_operatore.equals(""))
				sel_operatore = "-1";
			Map prompts = new HashMap();

			prompts.put("codicefiscale", request.getAttribute("codicefiscale"));
			prompts.put("codcpi", request.getAttribute("codcpi"));
			prompts.put("cognome", Utils.notNull(request.getAttribute("cognome")));
			prompts.put("nome", request.getAttribute("nome"));
			prompts.put("ragionesociale", request.getAttribute("ragionesociale"));
			prompts.put("ambiente", sel_ambiente);
			prompts.put("operatore", sel_operatore);
			prompts.put("servizio", Utils.notNull(request.getAttribute("servizio")));
			prompts.put("esito", request.getAttribute("esito"));
			prompts.put("dataDal", request.getAttribute("dataDal"));
			prompts.put("dataAl", request.getAttribute("dataAl"));
			prompts.put("piva", request.getAttribute("piva"));
			prompts.put("codiceFiscaleAz", Utils.notNull(request.getAttribute("strCodiceFiscaleAz")));

			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			// addPromptFieldsProtocollazione(prompts, request);

			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true"))
				insertDocument(request, response);
			else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);
		} // else
	}
} // class
