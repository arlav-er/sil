package it.eng.sil.action.report.agenda;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class AgendaPromemApp extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String raggruppa = (String) request.getAttribute("RAGG");
			if (raggruppa.equalsIgnoreCase("SER")) {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("PromemAppServizio." + tipoFile);
				else
					setStrNomeDoc("PromemAppServizio.rpt");
				setStrDescrizione("Agenda: Promemoria giornaliero");
				setReportPath("Agenda/PromemAppServizio_CC.rpt");
			} else if (raggruppa.equalsIgnoreCase("AMB")) {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("PromemAppAmbiente." + tipoFile);
				else
					setStrNomeDoc("PromemAppAmbiente.rpt");
				setStrDescrizione("Agenda: Promemoria giornaliero");
				setReportPath("Agenda/PromemAppAmbiente_CC.rpt");
			} else if (raggruppa.equalsIgnoreCase("OPE")) {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("PromemAppAmbiente." + tipoFile);
				else
					setStrNomeDoc("PromemAppAmbiente.rpt");
				setStrDescrizione("Agenda: Promemoria giornaliero");
				setReportPath("Agenda/PromemAppOperatore_CC.rpt");
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("PromemAppAmbiente." + tipoFile);
				else
					setStrNomeDoc("PromemeriaAppNoGroup.rpt");
				setStrDescrizione("Agenda: Promemoria giornaliero");
				setReportPath("Agenda/PromemeriaAppNoGroup_CC.rpt");
			}

			Vector params = new Vector(5);

			if (request.getAttribute("PRGSPI") == null || ((String) request.getAttribute("PRGSPI")).trim().equals("")) {
				params.add("0");
			} else {
				params.add(request.getAttribute("PRGSPI"));
			}
			// params.add(request.getAttribute("PRGSPI"));
			params.add(request.getAttribute("DATDAL"));
			params.add(request.getAttribute("DATAL"));
			params.add(request.getAttribute("CODCPI"));
			if (request.getAttribute("CODSERVIZIO") == null
					|| ((String) request.getAttribute("CODSERVIZIO")).trim().equals("")) {
				params.add("");
			} else {
				params.add(request.getAttribute("CODSERVIZIO"));
			}
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
} // class AgendaPromemApp
