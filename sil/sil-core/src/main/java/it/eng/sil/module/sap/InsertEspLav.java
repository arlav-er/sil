
package it.eng.sil.module.sap;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.security.User;

/**
 * TODO:
 * 
 * @author gzuccaro
 *
 */
public class InsertEspLav extends InsertSAP {
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertEspLav.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		Object[] obj = null;
		try {
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			BigDecimal cdnLavoratore = (BigDecimal) request.getAttribute("cdnLavoratore");
			String strNumEspLav = ((String) request.getAttribute("numEspLav"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();

			if ((strNumEspLav != null && !strNumEspLav.equals(""))) {
				int numEspLav = new Integer(strNumEspLav).intValue();
				for (int i = 0; i < numEspLav; i++) {
					check = (String) request.getAttribute(SAP_SEZ_ESP_LAV + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {
						try {
							transExec = new TransactionQueryExecutor(getPool(), this);
							this.enableTransactions(transExec);
							transExec.initTransaction();

							String codContratto = StringUtils.getAttributeStrNotNull(request,
									SAP_SEZ_ESP_LAV + "_codContratto_" + i);
							request.delAttribute("codContratto");
							request.setAttribute("codContratto", codContratto);
							obj = new Object[1];
							obj[0] = codContratto;

							SourceBean flagsEsp = ((SourceBean) transExec.executeQuery("SELECT_FLAGS_ESP_DA_CONTRATTO",
									obj, "SELECT"));
							if (flagsEsp != null) {
								String flgEspLav = StringUtils.getAttributeStrNotNull(flagsEsp, "ROW.flgEspLav");
								String flgTirocini = StringUtils.getAttributeStrNotNull(flagsEsp, "ROW.flgTirocini");
								String codMansione = StringUtils.getAttributeStrNotNull(request,
										SAP_SEZ_ESP_LAV + "_codMansione_" + i);

								obj = new Object[2];
								obj[0] = codMansione;
								obj[1] = cdnLavoratore;

								SourceBean mansione = ((SourceBean) transExec.executeQuery("ESISTE_MANSIONE", obj,
										"SELECT"));
								BigDecimal prgMansione = null;
								boolean mansioneOK = false;
								if (mansione != null && mansione.containsAttribute("ROW.prgMansione")) {
									prgMansione = (BigDecimal) mansione.getAttribute("ROW.prgMansione");

									obj = new Object[3];
									String query = "";
									if (flgEspLav.equals("S")) {
										obj[0] = flgEspLav;
										query = "UPDATE_FLGESPLAV_MANSIONE";
									} else if (flgTirocini.equals("S")) {
										obj[0] = flgTirocini;
										query = "UPDATE_FLGESPFORM_MANSIONE";
									}
									obj[1] = userid;
									obj[2] = prgMansione;
									mansioneOK = ((Boolean) transExec.executeQuery(query, obj, "UPDATE"))
											.booleanValue();
								} else {
									prgMansione = DBKeyGenerator.getNextSequence(transExec.getDataConnection(),
											"S_PR_MANSIONE");

									obj = new Object[9];
									obj[0] = prgMansione;
									obj[1] = cdnLavoratore;
									obj[2] = codMansione;
									obj[3] = flgEspLav;
									obj[4] = flgTirocini;
									obj[5] = null;
									obj[6] = null;
									obj[7] = userid;
									obj[8] = userid;

									mansioneOK = ((Boolean) transExec.executeQuery("INSERT_SAP_MANSIONE", obj,
											"INSERT")).booleanValue();
								}

								if (mansioneOK) {
									request.delAttribute("prgMansione");
									request.setAttribute("prgMansione", prgMansione);

									request.delAttribute("strdesattivita");
									request.setAttribute("strdesattivita", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_strDescrAttivita_" + i));
									request.delAttribute("codRapportoLav");
									request.setAttribute("codRapportoLav", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_codContrattoLav_" + i));
									request.delAttribute("strRagSocialeAzienda");
									request.setAttribute("strRagSocialeAzienda", StringUtils.getAttributeStrNotNull(
											request, SAP_SEZ_ESP_LAV + "_strDatoreLavoro_" + i));
									request.delAttribute("codAteco");
									request.setAttribute("codAteco", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_strAttivitaMin_" + i));

									String dataInizio = StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_dtmInizio_" + i);
									BigDecimal meseInizio = new BigDecimal(
											Integer.parseInt(dataInizio.substring(3, 5), 10));
									BigDecimal annoInizio = new BigDecimal(
											Integer.parseInt(dataInizio.substring(6, 10), 10));
									request.delAttribute("meseInizio");
									request.setAttribute("meseInizio", meseInizio);
									request.delAttribute("annoInizio");
									request.setAttribute("annoInizio", annoInizio);

									String dataFine = StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_dtmFine_" + i);
									request.delAttribute("meseFine");
									request.delAttribute("annoFine");
									request.delAttribute("numMesi");
									if (!"".equals(dataFine)) {
										request.setAttribute("meseFine",
												new BigDecimal(Integer.parseInt(dataFine.substring(3, 5), 10)));
										request.setAttribute("annoFine",
												new BigDecimal(Integer.parseInt(dataFine.substring(6, 10), 10)));

										int numMesi = DateUtils.monthsBetween(dataInizio, dataFine);
										request.setAttribute("numMesi", new BigDecimal(numMesi));
									}

									String descrMansione = StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_ESP_LAV + "_descrMansione_" + i);
									Vector<String> params = new Vector<String>(1);
									params.add(descrMansione);

									if (doInsert(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_ESPLAV_LAV_OK,
												"service", "Esperienza Lavorativa correttamente importata", params);

										if (codMansione.substring(codMansione.length() - 2).equals("00"))
											reportOperation.reportWarning(
													MessageCodes.ImportaSAP.MANSIONE_NON_SPEC_LAV_OK, "service",
													"La Mansione indicata non è specifica", params);

										transExec.commitTransaction();
									} else
										throw new Exception("Aggiornamento Esperienza Lavorativa non riuscita");
								} else
									throw new Exception("Aggiornamento Mansione non riuscito");
							} else
								throw new Exception("Contratto non valido per inserimento esperienze lavorative");
						} catch (Exception e) {
							transExec.rollBackTransaction();
							_logger.error(e);
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error(e);
		}
	}
}