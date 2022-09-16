package it.eng.sil.action.report.promemoria;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;

public class stampaGior extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(stampaGior.class.getName());

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String utente = (String) request.getAttribute("UTENTE");

				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("LetteraConvocazione." + tipoFile);
				} else {
					setStrNomeDoc("LetteraConvocazione.pdf");
				}

				setStrDescrizione("Lettera");

				setReportPath("PROMEMORIA/LTC_APP_CC.rpt");

				String regione = "";
				try {
					// TODO logo europa
					SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery("GET_CODREGIONE", null, "SELECT",
							Values.DB_SIL_DATI);
					if (beanRows != null) {
						beanRows = beanRows.containsAttribute("ROW") ? (SourceBean) beanRows.getAttribute("ROW")
								: beanRows;
						regione = (String) beanRows.getAttribute("CODREGIONE");
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.error(_logger, "stampaGior:service():errore AccessoSemplificato", e);
				}

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("prgModelloStampa", request.getAttribute("prgmodellostampa"));
				prompts.put("codCpi", request.getAttribute("codcpi"));
				prompts.put("cdnLavoratore", request.getAttribute("cdnlavoratore"));
				prompts.put("prgAppuntamento", request.getAttribute("prgappuntamento"));
				prompts.put("regione", regione);

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "stampaGior.service()", "");
			}
		} // else
	}

}
