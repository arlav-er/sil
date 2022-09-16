package it.eng.sil.action.report.situazioneLav;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;

public class SituazioneLavPatronato extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileB" + "" + "lob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("SituazioneLavPatronato." + tipoFile);
				else
					setStrNomeDoc("SituazioneLavPatronato.pdf");
				setStrDescrizione("Situazione Lavorativa Patronato");

				AccessoSemplificato _db = new AccessoSemplificato(this);
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_CODREGIONE");
				beanRows = _db.doSelect(request, response, false);

				String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

				setReportPath("Situazione_Lavorativa/SituazioneLavPatronato_CC.rpt");
				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("codCpi", request.getAttribute("codCPI"));
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));

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
				reportFailure(ue, "SituazioneLavPatronato.service()", "");
			}
		} // else
	}
}
