package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/*
 * calcola il punteggio presunto per tutti i nominativi della 
 * graduatoria che si sta visualizzando   
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMCalcolaPunteggioPresunto extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMCalcolaPunteggioPresunto.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		// ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();
			String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
			String prgTipoRosa = (String) request.getAttribute("PRGTIPOROSA");

			if (("2").equalsIgnoreCase(prgTipoRosa)) {
				// ciclo su tutti i nominativi della graduatoria
				SourceBean querySelectGraduatoria = (SourceBean) getConfig().getAttribute("CM_ELENCO_CANDIDATI_GRAD");
				SourceBean datiGraduatoria = (SourceBean) transExec.executeQuery(reqCont, resCont,
						querySelectGraduatoria, "SELECT");
				if (datiGraduatoria != null) {
					Vector nominativoVect = datiGraduatoria.getAttributeAsVector("ROW");

					for (int i = 0; i < nominativoVect.size(); i++) {
						SourceBean nominativoSB = (SourceBean) nominativoVect.get(i);
						Object cdnLavoratore = nominativoSB.getAttribute("CDNLAVORATORE");
						Object prgNominativo = nominativoSB.getAttribute("PRGNOMINATIVO");

						request.delAttribute("CDNLAVORATORE");
						request.setAttribute("CDNLAVORATORE", cdnLavoratore);
						request.delAttribute("PRGNOMINATIVO");
						request.setAttribute("PRGNOMINATIVO", prgNominativo);

						// calcolo i mesi di anzianità

						int numMesiAnzianita = 0;
						String dataAnzianitaCM = ""; // datdatainizio
						String dataSospensioneCM = ""; // datsospensione
						String dataPubblicaz = ""; // dataPubblicazioneRichiesta
						String dataChiamataCM = ""; // dataChiamataCM
						int mesiSospCM = 0; // nummesisospesterni
						String dataAnzPregressaOrdinaria = "";

						if (!("12").equalsIgnoreCase(prgTipoIncrocio)) {

							/*
							 * verifica versione graduatoria in ts_generale presenza parametro CODMONOTIPOGRAD 1:
							 * calcolo punteggio normale 2: calcolo punteggio con locomozione e patente
							 */
							String codMonoTipoGrad = "1";
							SourceBean querySelectTipoGrad = (SourceBean) getConfig()
									.getAttribute("CHECK_VERSIONE_GRADUATORIA");
							SourceBean datiTipoGradCM = (SourceBean) transExec.executeQuery(reqCont, resCont,
									querySelectTipoGrad, "SELECT");
							if (datiTipoGradCM != null) {
								Vector rowsTipoGradCM = datiTipoGradCM.getAttributeAsVector("ROW");
								if (rowsTipoGradCM.size() == 1) {
									SourceBean rowtTpo = (SourceBean) rowsTipoGradCM.get(0);
									codMonoTipoGrad = (String) rowtTpo.getAttribute("CODMONOTIPOGRADCM");
								}
							}

							// nuova versione: calcola i mesi di anzianità dalla data di inizio pubblicazione della
							// richiesta
							SourceBean querySelect1 = (SourceBean) getConfig().getAttribute("CM_DATI_ISCRIZIONE_NEW");
							SourceBean datiCM1 = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect1,
									"SELECT");
							if (datiCM1 != null) {
								Vector rowCM1 = datiCM1.getAttributeAsVector("ROW");
								if (rowCM1.size() == 1) {
									SourceBean row1 = (SourceBean) rowCM1.get(0);
									String codMonoTipoRag = (String) row1.getAttribute("CODMONOTIPORAGG");
									dataAnzPregressaOrdinaria = row1.getAttribute("datanzordpregressa") == null ? ""
											: (String) row1.getAttribute("datanzordpregressa");
									if (("D").equalsIgnoreCase(codMonoTipoRag)) {
										dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
												: (String) row1.getAttribute("DATANZIANITA68");
									} else if (("A").equalsIgnoreCase(codMonoTipoRag)) {
										dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
												: (String) row1.getAttribute("DATANZIANITA68");
									} else {
										dataAnzianitaCM = "";
										numMesiAnzianita = -6;
									}
								} else {
									dataAnzianitaCM = "";
									numMesiAnzianita = -6;
								}

								if (numMesiAnzianita >= 0) {
									SourceBean querySelect = (SourceBean) getConfig()
											.getAttribute("CM_DATAPUBBLICAZIONE_RICHIESTA");
									SourceBean datiCM = (SourceBean) transExec.executeQuery(reqCont, resCont,
											querySelect, "SELECT");
									if (datiCM != null) {
										Vector rowCM = datiCM.getAttributeAsVector("ROW");
										if (rowCM.size() == 1) {
											SourceBean row = (SourceBean) rowCM.get(0);
											dataPubblicaz = row.getAttribute("DATPUBBLICAZIONE") == null ? ""
													: (String) row.getAttribute("DATPUBBLICAZIONE");
											dataChiamataCM = row.getAttribute("DATCHIAMATACM") == null ? ""
													: (String) row.getAttribute("DATCHIAMATACM");
											// gestione tipo calcolo punteggio a senconda del parametro nel ts_generale
											if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
												if (dataChiamataCM != null && !("").equalsIgnoreCase(dataChiamataCM)) {
													// calcolo i mesi di anzianità
													numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore,
															dataAnzianitaCM, "", 0, dataChiamataCM,
															dataAnzPregressaOrdinaria);
												} else {
													dataChiamataCM = "";
													numMesiAnzianita = -7;
												}
											} else {
												if (dataPubblicaz != null && !("").equalsIgnoreCase(dataPubblicaz)) {
													// calcolo i mesi di anzianità
													numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore,
															dataAnzianitaCM, "", 0, dataPubblicaz,
															dataAnzPregressaOrdinaria);
												} else {
													dataPubblicaz = "";
													numMesiAnzianita = -5;
												}
											}
										} else {
											dataPubblicaz = "";
											dataChiamataCM = "";
											if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
												numMesiAnzianita = -7;
											} else {
												numMesiAnzianita = -5;
											}
										}

									}
								}
							}
						}

						// calcolo il punteggio presunto
						calcolaPunteggio(request, response, numMesiAnzianita, prgNominativo, cdnLavoratore);
					}
				}

				transExec.commitTransaction();
				// ror.reportSuccess(idSuccess);
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMCalcolaPunteggio::service(): Impossibile calcolare il punteggio!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param cdnLavoratore
	 * @param dataAnzianitaCM
	 * @param dataSospensioneCM
	 * @param mesiSospCM
	 */
	private void calcolaPunteggio(SourceBean request, SourceBean response, int numMesiAnzianita, Object prgNominativo,
			Object cdnLavoratore) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		// ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";

			String numReddito = "";
			String numPersone = "";
			String datdichcarico = "";
			String codcmtipoiscr = "";
			String numpercinvalidita = "";
			String datanzianita68 = "";
			String mesianzianita = "";
			String flgPatente = "";
			String codgradocapacitaloc = "";
			String flgDisocTi = "";
			String codCmAnnota = "";

			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);
			SourceBean row = new SourceBean("ROW");

			// calcolo il punteggio se i mesi di anzianità sono stati calcolati
			// altrimenti setto l'errore sull'iscrizione al collocamento mirato
			if (numMesiAnzianita >= 0) {

				statementSB = (SourceBean) getConfig().getAttribute("CM_CALCOLO_PUNTEGGIO_PRES_CANDIDATO");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String p_prgNominatico = prgNominativo.toString();
				String tipoGraduatoria = "CM";
				String p_cdnUtente = String.valueOf(user.getCodut());
				String cdnLavoratoreDecrypt = cdnLavoratore.toString();
				String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratoreDecrypt);

				parameters = new ArrayList(16);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 2. prgNominativo
				parameters.add(
						conn.createDataField("prgNominativo", java.sql.Types.BIGINT, new BigInteger(p_prgNominatico)));
				command.setAsInputParameters(paramIndex++);
				// 3. p_tipoGraduatoria
				parameters.add(conn.createDataField("tipoGraduatoria", java.sql.Types.VARCHAR, tipoGraduatoria));
				command.setAsInputParameters(paramIndex++);
				// 4. p_mesiAnzianita
				parameters.add(conn.createDataField("p_mesianzianita", java.sql.Types.BIGINT,
						new BigInteger("" + numMesiAnzianita)));
				command.setAsInputParameters(paramIndex++);
				// 5. p_cdnLavoratore
				parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, cdnLavoratoreEncrypt));
				command.setAsInputParameters(paramIndex++);

				// 6 out_p_numReddito
				parameters.add(conn.createDataField("out_p_numReddito", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 7 out_p_numPersone
				parameters.add(conn.createDataField("out_p_numPersone", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 8 out_p_datdichcarico
				parameters.add(conn.createDataField("out_p_datdichcarico", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 9 out_p_codcmtipoiscr
				parameters.add(conn.createDataField("out_p_codcmtipoiscr", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 10 out_p_numpercinvalidita
				parameters.add(conn.createDataField("out_p_numpercinvalidita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 11 out_p_datanzianita68
				parameters.add(conn.createDataField("out_p_datanzianita68", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 12 out_p_mesianzianita
				parameters.add(conn.createDataField("out_p_mesianzianita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 13 out_p_flgpatente
				parameters.add(conn.createDataField("out_p_flgpatente", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 14 out_p_codgradocapacitaloc
				parameters.add(conn.createDataField("out_p_codgradocapacitaloc", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 15 out_p_flgdisocti
				parameters.add(conn.createDataField("out_p_flgdisocti", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 16 out_p_codCmAnnota
				parameters.add(conn.createDataField("out_p_codCmAnnota", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// Chiamata alla Stored Procedure
				dr = command.execute(parameters);

				CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
				List outputParams = cdr.getContainedDataResult();
				PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
				DataField df = pdr.getPunctualDatafield();
				codiceRit = df.getStringValue();
				// 1. numReddito
				pdr = (PunctualDataResult) outputParams.get(1);
				df = pdr.getPunctualDatafield();
				numReddito = df.getStringValue();
				// 2. numPersone
				pdr = (PunctualDataResult) outputParams.get(2);
				df = pdr.getPunctualDatafield();
				numPersone = df.getStringValue();
				// 3. datdichcarico
				pdr = (PunctualDataResult) outputParams.get(3);
				df = pdr.getPunctualDatafield();
				datdichcarico = df.getStringValue();
				// 4. codcmtipoiscr
				pdr = (PunctualDataResult) outputParams.get(4);
				df = pdr.getPunctualDatafield();
				codcmtipoiscr = df.getStringValue();
				// 5. numpercinvalidita
				pdr = (PunctualDataResult) outputParams.get(5);
				df = pdr.getPunctualDatafield();
				numpercinvalidita = df.getStringValue();
				// 6. datanzianita68
				pdr = (PunctualDataResult) outputParams.get(6);
				df = pdr.getPunctualDatafield();
				datanzianita68 = df.getStringValue();
				// 7. mesianzianita
				pdr = (PunctualDataResult) outputParams.get(7);
				df = pdr.getPunctualDatafield();
				mesianzianita = df.getStringValue();
				// 8. flgpatente
				pdr = (PunctualDataResult) outputParams.get(8);
				df = pdr.getPunctualDatafield();
				flgPatente = df.getStringValue();
				// 9. codgradocapacitaloc
				pdr = (PunctualDataResult) outputParams.get(9);
				df = pdr.getPunctualDatafield();
				codgradocapacitaloc = df.getStringValue();
				// 10. disoc TI
				pdr = (PunctualDataResult) outputParams.get(10);
				df = pdr.getPunctualDatafield();
				flgDisocTi = df.getStringValue();
				if (flgDisocTi == null) {
					flgDisocTi = "";
				}
				// 11. codcmannota
				pdr = (PunctualDataResult) outputParams.get(11);
				df = pdr.getPunctualDatafield();
				codCmAnnota = df.getStringValue();
				if (codCmAnnota == null) {
					codCmAnnota = "";
				}

				row.setAttribute("CodiceRit", codiceRit);
				row.setAttribute("numRedditoPres", numReddito);
				row.setAttribute("numPersonePres", numPersone);
				row.setAttribute("datdichcaricoPres", datdichcarico);
				row.setAttribute("codcmtipoiscrPres", codcmtipoiscr);
				row.setAttribute("numpercinvaliditaPres", numpercinvalidita);
				row.setAttribute("datanzianita68Pres", datanzianita68);
				row.setAttribute("mesianzianitaPres", mesianzianita);
				row.setAttribute("flgpatente", flgPatente);
				row.setAttribute("codgradocapacitaloc", codgradocapacitaloc);
				row.setAttribute("flgdisoctiPres", flgDisocTi);
				row.setAttribute("codCmAnnotaPres", codCmAnnota);
			} else {
				codiceRit = "-2";
				row.setAttribute("CodiceRit", codiceRit);
				row.setAttribute("numRedditoPres", numReddito);
				row.setAttribute("numPersonePres", numPersone);
				row.setAttribute("datdichcaricoPres", datdichcarico);
				row.setAttribute("codcmtipoiscrPres", codcmtipoiscr);
				row.setAttribute("numpercinvaliditaPres", numpercinvalidita);
				row.setAttribute("datanzianita68Pres", datanzianita68);
				row.setAttribute("mesianzianitaPres", mesianzianita);
				row.setAttribute("flgpatente", flgPatente);
				row.setAttribute("codgradocapacitaloc", codgradocapacitaloc);
				row.setAttribute("flgdisoctiPres", flgDisocTi);
				row.setAttribute("codCmAnnotaPres", codCmAnnota);
			}

			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure per il calcolo punteggio presunto";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}