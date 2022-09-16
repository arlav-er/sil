package it.eng.sil.action.report.promemoria;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;

public class stampaPromemoriaApp extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(stampaPromemoriaApp.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("Promemoria." + tipoFile);
			} else {
				setStrNomeDoc("Promemoria.pdf");
			}
			setStrDescrizione("Promemoria");

			String strCdnLavoratore = request.getAttribute("cdnLavoratore").toString();
			String strPrgAzienda = request.getAttribute("prgAzienda").toString();
			String strPrgUnita = request.getAttribute("prgUnita").toString();

			setReportPath("PROMEMORIA/PRM_APP_CC.rpt");

			// TODO logo europa
			String regione = "";
			try {
				SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery("GET_CODREGIONE", null, "SELECT",
						Values.DB_SIL_DATI);
				if (beanRows != null) {
					beanRows = beanRows.containsAttribute("ROW") ? (SourceBean) beanRows.getAttribute("ROW") : beanRows;
					regione = (String) beanRows.getAttribute("CODREGIONE");
				}
			} catch (Exception e) {
				_logger.error(e);
			}

			Vector params = null;
			params = new Vector(4);
			params.add(request.getAttribute("operatore"));
			params.add(request.getAttribute("prgAppuntamento"));

			if (!strCdnLavoratore.equals("")) {
				params.add(request.getAttribute("cdnLavoratore"));
			} else {
				params.add("-1");
			}

			if (!strPrgAzienda.equals("")) {
				params.add(request.getAttribute("prgAzienda"));
				params.add(request.getAttribute("prgUnita"));
			} else {
				params.add("-1");
				params.add("-1");
			}
			params.add(request.getAttribute("codcpi"));
			params.add(regione);
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
