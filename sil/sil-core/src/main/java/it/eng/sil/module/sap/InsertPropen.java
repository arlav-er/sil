package it.eng.sil.module.sap;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.security.User;

public class InsertPropen extends InsertSAP {
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
			String strNumPropen = ((String) request.getAttribute("numPropen"));
			String check = "";
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			this.disableMessageIdElementDuplicate();

			if ((strNumPropen != null && !strNumPropen.equals(""))) {
				int numPropen = new Integer(strNumPropen).intValue();
				for (int i = 0; i < numPropen; i++) {
					check = (String) request.getAttribute(SAP_SEZ_PROPEN + "_chkImporta_" + i);
					if (check != null && check.equals("on")) {
						try {
							transExec = new TransactionQueryExecutor(getPool(), this);
							this.enableTransactions(transExec);
							transExec.initTransaction();

							String codMansione = StringUtils.getAttributeStrNotNull(request,
									SAP_SEZ_PROPEN + "_codMansione_" + i);
							String strNote = StringUtils.getAttributeStrNotNull(request,
									SAP_SEZ_PROPEN + "_strDescrizione_" + i);

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
								obj[0] = strNote;
								obj[1] = userid;
								obj[2] = prgMansione;
								mansioneOK = ((Boolean) transExec.executeQuery("UPDATE_FLGDISP_S_E_NOTE_MANSIONE", obj,
										"UPDATE")).booleanValue();
							} else {
								prgMansione = DBKeyGenerator.getNextSequence(transExec.getDataConnection(),
										"S_PR_MANSIONE");

								obj = new Object[9];
								obj[0] = prgMansione;
								obj[1] = cdnLavoratore;
								obj[2] = codMansione;
								obj[3] = null;
								obj[4] = null;
								obj[5] = "S";
								obj[6] = strNote;
								obj[7] = userid;
								obj[8] = userid;

								mansioneOK = ((Boolean) transExec.executeQuery("INSERT_SAP_MANSIONE", obj, "INSERT"))
										.booleanValue();
							}

							if (mansioneOK) {

								request.delAttribute("prgMansione");
								request.setAttribute("prgMansione", prgMansione);

								String descrMansione = StringUtils.getAttributeStrNotNull(request,
										SAP_SEZ_PROPEN + "_descrMansione_" + i);

								Vector<String> prms = new Vector<String>(1);
								prms.add(descrMansione);

								reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_MANSIONE_LAV_OK,
										"service", "Disponibilità Mansione correttamente importata", prms);

								if (codMansione.substring(codMansione.length() - 2).equals("00"))
									reportOperation.reportWarning(MessageCodes.ImportaSAP.MANSIONE_NON_SPEC_LAV_OK,
											"service", "La Mansione indicata non è specifica", prms);

								int numOrari = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codOrario_" + i).size();
								for (int j = 0; j < numOrari; j++) {
									String codOrario = StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codOrario_" + i + "_" + j);
									String descrOrario = StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrOrario_" + i + "_" + j);

									Vector<String> params = new Vector<String>(2);
									params.add(descrOrario);
									params.add(descrMansione);

									if (!"".equals(codOrario)) {
										obj = new Object[1];
										obj[0] = codOrario;

										SourceBean infoOrario = ((SourceBean) transExec.executeQuery("GET_DESCR_ORARIO",
												obj, "SELECT"));
										descrOrario = StringUtils.getAttributeStrNotNull(infoOrario,
												"ROW.strdescrizione");

										request.delAttribute("codOrario");
										request.setAttribute("codOrario", codOrario);

										setSectionQuerySelect("QUERY_SELECT_ORARIO");
										setSectionQueryInsert("QUERY_INSERT_ORARIO");

										if (doInsertNoDuplicate(request, response)) {
											reportOperation.reportWarning(
													MessageCodes.ImportaSAP.INSERT_DIS_ORARIO_LAV_OK, "service",
													"Disponibilità Orario correttamente importata", params);
										} else {
											reportOperation.reportWarning(
													MessageCodes.ImportaSAP.INSERT_DIS_ORARIO_LAV_FAIL, "service",
													"Disponibilità Orario già presente", params);
										}
									} else {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_ORARIO_LAV_FAIL_NO_MAP, "service",
												"Disponibilità Orario non importata non essendoci una corrispondenza nel SIL",
												params);
									}
								}

								int numTurni = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codTurno_" + i).size();
								for (int j = 0; j < numTurni; j++) {
									request.delAttribute("codTurno");
									request.setAttribute("codTurno", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codTurno_" + i + "_" + j));

									Vector<String> params = new Vector<String>(2);
									params.add(StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrTurno_" + i + "_" + j));
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_TURNO");
									setSectionQueryInsert("QUERY_INSERT_TURNO");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_TURNO_LAV_OK,
												"service", "Disponibilità Turno correttamente importata", params);
									} else {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_TURNO_LAV_FAIL,
												"service", "Disponibilità Turno già presente", params);
									}
								}

								int numComuni = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codCom_" + i).size();
								for (int j = 0; j < numComuni; j++) {
									request.delAttribute("codCom");
									request.setAttribute("codCom", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codCom_" + i + "_" + j));

									Vector<String> params = new Vector<String>(2);
									params.add(StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrCom_" + i + "_" + j));
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_COMUNE");
									setSectionQueryInsert("QUERY_INSERT_COMUNE");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_COMUNE_LAV_OK,
												"service", "Disponibilità Comune correttamente importata", params);
									} else {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_COMUNE_LAV_FAIL, "service",
												"Disponibilità Comune già presente", params);
									}
								}

								int numProvince = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codProvincia_" + i)
										.size();
								for (int j = 0; j < numProvince; j++) {
									request.delAttribute("codProvincia");
									request.setAttribute("codProvincia", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codProvincia_" + i + "_" + j));

									Vector<String> params = new Vector<String>(2);
									params.add(StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrProvincia_" + i + "_" + j));
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_PROVINCIA");
									setSectionQueryInsert("QUERY_INSERT_PROVINCIA");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_PROVINCIA_LAV_OK, "service",
												"Disponibilità Provincia correttamente importata", params);
									} else {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_PROVINCIA_LAV_FAIL, "service",
												"Disponibilità Provincia già presente", params);
									}
								}

								int numRegioni = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codRegione_" + i)
										.size();
								for (int j = 0; j < numRegioni; j++) {
									request.delAttribute("codRegione");
									request.setAttribute("codRegione", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codRegione_" + i + "_" + j));

									Vector<String> params = new Vector<String>(2);
									params.add(StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrRegione_" + i + "_" + j));
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_REGIONE");
									setSectionQueryInsert("QUERY_INSERT_REGIONE");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_REGIONE_LAV_OK,
												"service", "Disponibilità Regione correttamente importata", params);
									} else {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_REGIONE_LAV_FAIL, "service",
												"Disponibilità Regione già presente", params);
									}
								}

								int numStati = request.getAttributeAsVector(SAP_SEZ_PROPEN + "_codStato_" + i).size();
								for (int j = 0; j < numStati; j++) {
									request.delAttribute("codStato");
									request.setAttribute("codStato", StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_codStato_" + i + "_" + j));

									Vector<String> params = new Vector<String>(2);
									params.add(StringUtils.getAttributeStrNotNull(request,
											SAP_SEZ_PROPEN + "_descrStato_" + i + "_" + j));
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_STATO");
									setSectionQueryInsert("QUERY_INSERT_STATO");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_STATO_LAV_OK,
												"service", "Disponibilità Stato correttamente importata", params);
									} else {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_STATO_LAV_FAIL,
												"service", "Disponibilità Stato già presente", params);
									}
								}

								String flgDispAuto = StringUtils.getAttributeStrNotNull(request,
										SAP_SEZ_PROPEN + "_flgDispAuto_" + i);
								String flgDispMoto = StringUtils.getAttributeStrNotNull(request,
										SAP_SEZ_PROPEN + "_flgDispMoto_" + i);
								String flgMezziPub = StringUtils.getAttributeStrNotNull(request,
										SAP_SEZ_PROPEN + "_flgMezziPub_" + i);

								if (!"".equals(flgDispAuto) || !"".equals(flgDispMoto) || !"".equals(flgMezziPub)) {
									request.delAttribute("flgDispAuto");
									request.setAttribute("flgDispAuto", "".equals(flgDispAuto) ? "N" : flgDispAuto);

									request.delAttribute("flgDispMoto");
									if (!"".equals(flgDispMoto)) {
										request.setAttribute("flgDispMoto", flgDispMoto);
									}

									request.delAttribute("flgMezziPub");
									if (!"".equals(flgMezziPub)) {
										request.setAttribute("flgMezziPub", flgMezziPub);
									}

									Vector<String> params = new Vector<String>(1);
									params.add(descrMansione);

									setSectionQuerySelect("QUERY_SELECT_MOB_GEO");
									setSectionQueryInsert("QUERY_INSERT_MOB_GEO");

									if (doInsertNoDuplicate(request, response)) {
										reportOperation.reportWarning(MessageCodes.ImportaSAP.INSERT_DIS_MOB_GEO_LAV_OK,
												"service", "Disponibilità Mobilità geografica correttamente importata",
												params);
									} else {
										reportOperation.reportWarning(
												MessageCodes.ImportaSAP.INSERT_DIS_MOB_GEO_LAV_FAIL, "service",
												"Disponibilità Mobilità geografica già presente", params);
									}
								}

								transExec.commitTransaction();
							} else
								throw new Exception("Aggiornamento Mansione non riuscito");
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