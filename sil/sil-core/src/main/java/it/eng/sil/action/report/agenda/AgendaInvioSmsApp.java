package it.eng.sil.action.report.agenda;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class AgendaInvioSmsApp extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("ReportRicercaAppSms." + tipoFile);
			} else {
				setStrNomeDoc("ReportRicercaAppSms.pdf");
			}
			setStrDescrizione("Stampa appuntamenti per invio SMS");
			setReportPath("Agenda/REPORTLISTAAPPSMS_CC.rpt");

			Vector params = null;
			params = new Vector(3);

			params.add(request.getAttribute("CODCPI"));
			params.add(request.getAttribute("DATARICERCA"));
			params.add(request.getAttribute("CODSERVIZIO"));

			setParams(params);

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
}// class
