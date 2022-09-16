package it.eng.sil.action.report.voucher;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class StampaAttestazione extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaAttestazione.class.getName());

	private static final long serialVersionUID = 9015433517028506547L;

	private static String reportRelativePath = "Voucher/AttestazionePartecipazioneTDA_CC.rpt";

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);
		Object prgVoucher = request.getAttribute("strchiavetabella");
		String reportOutputFileName = "ATTESTAZIONE" + prgVoucher.toString();
		String reportDescription = "ATTESTAZIONE (" + prgVoucher.toString() + ")";

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {

			try {

				Object[] paramsDoc = new Object[] { request.getAttribute("cdnLavoratore"),
						Properties.TIPO_DOC_TDA_PARTECIPAZIONE, prgVoucher };
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
					Object[] params = new Object[] { prgVoucher };
					row = doSelect("GET_INFO_VOUCHER_STAMPA_ATTESTAZIONE", params);
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					Object datAttivazione = row.getAttribute("datAttivazione");
					Object datConclusione = row.getAttribute("datfineerogazione");
					Object azione = row.getAttribute("strdescrizione");
					String ente = Utils.notNull(row.getAttribute("strdenominazione"));
					String obiettivi = Utils.notNull(row.getAttribute("strnoteobiettivi"));
					String luogoData = Utils.notNull(row.getAttribute("comune")) + " " + DateUtils.getNow();

					float p_numduratacurr = 0;
					String p_tipologiaduratacurr = "";
					String p_tipologiadurataprec = "";
					String p_tipologiaduratafinale = "";
					float p_numduratatotaleeff = 0;
					float p_numduratatotaletipologia = 0;
					float p_contatoretipodurata = 0;
					boolean tipologiamodalitadiverse = false;
					SourceBean rowsMod = doSelect("GET_LISTA_MODALITA_TDA", params);
					if (rowsMod != null) {
						Vector modalitaDurata = rowsMod.getAttributeAsVector("ROW");
						for (int i = 0; i < modalitaDurata.size(); i++) {
							row = (SourceBean) modalitaDurata.get(i);
							if (row.getAttribute("durataEffettiva") != null) {
								p_numduratacurr = new Float(row.getAttribute("durataEffettiva").toString())
										.floatValue();
								p_tipologiaduratacurr = row.getAttribute("codtipologiadurata") != null
										? row.getAttribute("codtipologiadurata").toString()
										: "";
								p_contatoretipodurata = p_contatoretipodurata + 1;

								if (p_contatoretipodurata == 1) {
									p_tipologiaduratafinale = p_tipologiaduratacurr;
									p_tipologiadurataprec = p_tipologiaduratacurr;
									p_numduratatotaletipologia = p_numduratatotaletipologia + p_numduratacurr;
								} else {
									if (p_tipologiadurataprec.equalsIgnoreCase(p_tipologiaduratacurr)) {
										p_numduratatotaletipologia = p_numduratatotaletipologia + p_numduratacurr;
									} else {
										tipologiamodalitadiverse = true;
									}
									p_tipologiadurataprec = p_tipologiaduratacurr;
								}

								if (p_tipologiaduratacurr.equalsIgnoreCase("G")) {
									p_numduratatotaleeff = p_numduratatotaleeff + p_numduratacurr;
								} else {
									if (p_tipologiaduratacurr.equalsIgnoreCase("M")) {
										p_numduratatotaleeff = p_numduratatotaleeff + (p_numduratacurr * 30);
									} else {
										p_numduratatotaleeff = p_numduratatotaleeff + (p_numduratacurr / 8);
									}
								}
							}
						}

						if (!tipologiamodalitadiverse) {
							p_numduratatotaleeff = p_numduratatotaletipologia;
						} else {
							p_tipologiaduratafinale = "G";
						}
					}

					String durata = "";
					if (p_tipologiaduratafinale.equalsIgnoreCase("G")) {
						durata = new Float(p_numduratatotaleeff).toString() + " giorni";
					} else {
						if (p_tipologiaduratafinale.equalsIgnoreCase("O")) {
							durata = new Float(p_numduratatotaleeff).toString() + " ore";
						} else {
							durata = new Float(p_numduratatotaleeff).toString() + " mesi";
						}
					}

					// impostazione parametri input del report
					Map prompts = new HashMap();
					prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
					prompts.put("prgVoucher", prgVoucher);
					prompts.put("datAttivazione", datAttivazione.toString());
					prompts.put("datConclusione", datConclusione.toString());
					prompts.put("azione", azione.toString());
					prompts.put("ente", ente);
					prompts.put("durata", durata);
					prompts.put("obiettivi", obiettivi);
					prompts.put("luogoData", luogoData);
					prompts.put("codCPI", user.getCodRif());

					addPromptFieldsProtocollazione(prompts, request);
					setPromptFields(prompts);

					String salva = (String) request.getAttribute("salvaDB");
					String apri = (String) request.getAttribute("apri");

					if (salva != null && salva.equalsIgnoreCase("true")) {
						getDocumento().setChiaveTabella(prgVoucher.toString());
						getDocumento().setPagina("TDAMainPage");
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
