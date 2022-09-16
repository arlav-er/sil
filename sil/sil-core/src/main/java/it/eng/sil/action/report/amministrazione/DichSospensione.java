package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.security.User;

/**
 * @author savino
 */
public class DichSospensione extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DichSospensione.class.getName());

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {

			try {
				setStrDescrizione("Dichiarazione di sospensione");

				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("DichSospensione." + tipoFile);
				else
					setStrNomeDoc("DichSospensione.pdf");

				setReportPath("Amministrazione/DichSospensione_CC.rpt");
				// impostare il codice del tipo di
				// documento**********************************
				// *******************************************************

				// impostazione parametri del report
				Map prompts = new HashMap();

				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				String codCpi = user.getCodRif();
				prompts.put("codCpi", codCpi);

				String strChiaveTabella = (String) request.getAttribute("strChiaveTabella");
				prompts.put("prgDichSospensione", strChiaveTabella);

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {
					setStrChiavetabella(strChiaveTabella);
					TransactionQueryExecutor txExecutor = null;
					try {
						String pool = (String) getConfig().getAttribute("POOL");
						txExecutor = new TransactionQueryExecutor(pool);
						txExecutor.initTransaction();
						insert(txExecutor);
						txExecutor.commitTransaction();
						setOperationSuccess(request, response);
					} catch (Exception e) {
						setOperationFail(request, response, e);
						try {
							txExecutor.rollBackTransaction();
						} catch (Exception e1) {
							it.eng.sil.util.TraceWrapper.debug(_logger, "impossibile eseguire la rollback", e1);

						}
					}
				} else if (apri != null && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				}
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "DichSospensione.service()", "");
			}
		}
	}
}