package it.eng.sil.action.report.voucher;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.security.User;

public class StampaCodiceAttivazione extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaCodiceAttivazione.class.getName());

	private static final long serialVersionUID = 9015433517028506547L;

	private static String reportRelativePath = "Voucher/AttivazioneTDA_CC.rpt";

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);
		String codAttivazione = (String) request.getAttribute("strchiavetabella");
		String reportOutputFileName = "TDA_" + codAttivazione;
		String reportDescription = "TDA PER LA RICOLLOCAZIONE (" + codAttivazione + ")";

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {

			try {

				Object[] paramsDoc = new Object[] { request.getAttribute("cdnLavoratore"),
						Properties.TIPO_DOC_TDA_ATTIVAZIONE, codAttivazione };
				SourceBean rowExistDoc = doSelect("EXIST_DOCUMENTO_CHIAVE", paramsDoc);
				Vector rows = rowExistDoc.getAttributeAsVector("ROW");
				if (rows != null && !rows.isEmpty()) {
					SourceBean rowDoc = (SourceBean) rows.elementAt(0);
					BigDecimal prgDoc = (BigDecimal) rowDoc.getAttribute("PRGDOCUMENTO");
					this.openDocument(request, response, prgDoc);
				} else {

					setStrDescrizione(reportDescription);

					String tipoFile = (String) request.getAttribute("tipoFile");
					if (tipoFile != null) {
						setStrNomeDoc(reportOutputFileName + "." + tipoFile);
					} else {
						setStrNomeDoc(reportOutputFileName + ".pdf");
					}

					setReportPath(reportRelativePath);

					SourceBean row = null;
					Object[] params = new Object[] { codAttivazione };
					row = doSelect("GET_DATA_ASSEGNAZIONE_CODICE_VOUCHER", params);
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					Object datAssegnazione = row.getAttribute("datAssegnazione");

					row = doSelect("GET_DATA_MAX_ATTIVAZIONE_CODICE_VOUCHER", params);
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					Object datMaxAttivazione = row.getAttribute("datMaxAttivazione");

					Object[] params2 = new Object[] { codAttivazione, codAttivazione };
					row = doSelect("GET_UTENTE_ASSEGNAZIONE_CODICE_VOUCHER", params2);
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					Object utenteAssegnazione = row.getAttribute("operatoreAssegnazione");

					// impostazione parametri input del report
					Map prompts = new HashMap();
					prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
					prompts.put("codCPI", user.getCodRif());
					prompts.put("prgPattoLavoratore", request.getAttribute("prgPattoLavoratore"));
					prompts.put("codAttivazione", codAttivazione);
					prompts.put("datAssegnazione", datAssegnazione.toString());
					prompts.put("datMaxAttivazione", datMaxAttivazione.toString());
					prompts.put("utenteAssegnazione", utenteAssegnazione.toString());

					addPromptFieldsProtocollazione(prompts, request);
					setPromptFields(prompts);

					String salva = (String) request.getAttribute("salvaDB");
					String apri = (String) request.getAttribute("apri");

					if (salva != null && salva.equalsIgnoreCase("true")) {
						getDocumento().setChiaveTabella(codAttivazione);
						getDocumento().setPagina("AssegnaVoucherPage");
						insertDocument(request, response);
					} else if (apri != null && apri.equalsIgnoreCase("true")) {
						showDocument(request, response);
					}
				}

			} catch (Exception e) {

				setOperationFail(request, response, e);
				_logger.error(e.getMessage());

			}
		}

	}

	public SourceBean doSelect(String statement, Object[] params) throws Exception {
		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(statement, params, "SELECT", Values.DB_SIL_DATI);
		return beanRows;
	}

}
