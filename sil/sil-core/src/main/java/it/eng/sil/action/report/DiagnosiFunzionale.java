/*
 * Creato il 21-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Engine;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.util.EncryptDecryptUtils;

public class DiagnosiFunzionale extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DiagnosiFunzionale.class.getName());

	private String fileType = Engine.EXPORT_PDF;

	private boolean isValleDaosta = false;

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);
		TransactionQueryExecutor txExec = null;
		DataConnection conn = null;
		DataConnection conn1 = null;
		AccessoSemplificato _db = new AccessoSemplificato(this);
		SourceBean beanRows = null;
		try {
			String parConfigDiagnosiFunzionale = dammiConfigurazione();
			if (parConfigDiagnosiFunzionale.equals("2"))
				isValleDaosta = true;

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("DiagnosiFunzionale." + tipoFile);
				else
					setStrNomeDoc("DiagnosiFunzionale.pdf");

				setStrDescrizione("Diagnosi Funzionale");

				// lA VALLE D'AOSTA NON VUOLE TUTTI I SUBREPORTS, PER FARE MODIFICHE ALLE
				// PAGINE RICORDARSI DI RIPORTARE LE MODIFICHE IN ENTRAMBI GLI RPT
				if (isValleDaosta)
					setReportPath("Diagnosi_Funzionale/DiagnosiFunzionale_VDA_CC.rpt");
				else
					setReportPath("Diagnosi_Funzionale/DiagnosiFunzionale_CC.rpt");

				String prgDiagnosiFunzionale = (String) request.getAttribute("prgDiagnosiFunzionale");
				Map prompts = new HashMap();
				prompts.put("parPrgDiagnosiFunzionale", prgDiagnosiFunzionale);
				prompts.put("parConfigDiagnosiFunzionale", parConfigDiagnosiFunzionale);
				if (isValleDaosta) {
					_db.setSectionQuerySelect("GET_LAV_VDA");
					beanRows = _db.doSelect(request, response, false);
					String cdnlavoratore = (String) beanRows.getAttribute("ROW.cdnlavoratore");
					String cdnLavoratoreDecrypt = EncryptDecryptUtils.decrypt(cdnlavoratore);
					prompts.put("cdnlav", cdnLavoratoreDecrypt);
				}

				// solo se e' richiesta la protocollazione i parametri vengono inseriti nella Map
				// se manca anche solo un parametro il metodo lancia una eccezione.
				try {
					addPromptFieldsProtocollazione(prompts, request);
				} catch (EMFUserError ue) {
					throw ue;
				}
				setPromptFields(prompts);

				com.inet.report.Engine eng = null;

				// BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
				// String annoProt = (String)request.getAttribute("annoProt");
				// String dataProtocollo = (String)request.getAttribute("dataOraProt");
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");

				// Settaggio strchiavetabella
				String strChiaveTabella = (String) request.getAttribute("strChiaveTabella");
				if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
					setStrChiavetabella(strChiaveTabella);
				}

				// per recuperare le informazioni utilizzate per la generazione del report non e' necessario
				// l'utilizzo della transazione. Se necessaria nei passi successivi verra' abilitata.

				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					txExec = new TransactionQueryExecutor(getPool());
					txExec.initTransaction();
					conn = txExec.getDataConnection();
					String codStatoAtto = getDocumento().getCodStatoAtto();
					eng = apri(request, conn.getInternalConnection(), codStatoAtto);// makeEngine(request,
																					// response,_db);
					// dopo aver recuperato le info per generare il report abilito la transazione
					// per l'inserimento del documento e per le eventuali operazioni successive
					if (insertDocument(request, response, txExec, eng)) // se FAIL il metodo ritorna false
						txExec.commitTransaction();
					else
						throw new Exception("Errore nella creazione del documento");
				} else if ((apri != null) && apri.equalsIgnoreCase("true") && !isValleDaosta) { // Fa qualcosa con i
																								// subreport che alla
																								// VDA non interessano
					conn1 = DataConnectionManager.getInstance().getConnection();
					eng = apri(request, conn1.getInternalConnection(), null);
					showDocument(request, response, eng);

				} else if ((apri != null) && apri.equalsIgnoreCase("true") && isValleDaosta) // LA VDA NON vuole i
																								// subreport creati in
																								// apri()
					showDocument(request, response, eng);

			} // else
		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "DiagnosiFunzionale.service()", "");
			// TODO ANDREA : perche' la rollback e' commentata?
			if (txExec != null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Stampa DiagnosiFunzionale fallita. Fallito anche il tentativo successivo di roolback.",
							(Exception) e1);

				}
			}
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn1, null, null);
		}
	}

	private String dammiConfigurazione() throws Exception {
		Connection conn = null;
		String ret = "0";
		try {
			conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
			java.sql.Statement st = conn.createStatement();
			String sql = "SELECT to_char(nvl(ts_config_loc.num, 0)) config from ts_config_loc, ts_generale "
					+ " where ts_generale.codprovinciasil = ts_config_loc.strcodrif and ts_config_loc.codtipoconfig = 'DIAGNFNZ' ";
			java.sql.ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				ret = rs.getString("config");
			}
			conn.close();
			return ret;
		} catch (Exception e) {
			if (conn != null) {
				conn.close();
			}
			throw new Exception("errore recupero provincia");
		}
	}

	private Engine apri(SourceBean request, Connection conn1, String codStatoAtto) throws Exception {

		String prgDiagnosiFunzionale = "" + request.getAttribute("prgDiagnosiFunzionale");

		String tipoFile = (String) request.getAttribute("tipoFile");
		setFileType(tipoFile);
		Engine engine = new Engine(getFileType());

		// ---------------------------------------------------- Attività mentali e relazionali
		// java.sql.Connection conn1 =
		// com.engiweb.framework.dbaccess.DataConnectionManager.getInstance().getConnection().getInternalConnection();
		java.sql.Statement st1 = conn1.createStatement();
		String sql1 = "SELECT DE_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'MR'                                               	";
		java.sql.ResultSet rs1 = st1.executeQuery(sql1);

		// ---------------------------------------------------- Informazione
		java.sql.Statement st2 = conn1.createStatement();
		String sql2 = "SELECT DE_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'IN'                                               	";
		java.sql.ResultSet rs2 = st2.executeQuery(sql2);

		// ---------------------------------------------------- Postura
		java.sql.Statement st3 = conn1.createStatement();
		String sql3 = "SELECT DE_CAPACITA.CODCAPACITA, 													"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'PO'                                               	";
		java.sql.ResultSet rs3 = st3.executeQuery(sql3);

		// ---------------------------------------------------- Locomozione
		java.sql.Statement st4 = conn1.createStatement();
		String sql4 = "SELECT DE_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'LO'                                               	";
		java.sql.ResultSet rs4 = st4.executeQuery(sql4);

		// ---------------------------------------------------- Movimento delle estremità/funzione degli arti
		java.sql.Statement st5 = conn1.createStatement();
		String sql5 = "SELECT DE_CAPACITA.CODCAPACITA, 													"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'MV'                                               	";
		java.sql.ResultSet rs5 = st5.executeQuery(sql5);

		// ---------------------------------------------------- Attività complesse, attività fisica associata a
		// resistenza
		java.sql.Statement st6 = conn1.createStatement();
		String sql6 = "SELECT DE_CAPACITA.CODCAPACITA, 													"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,											"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'AC'                                               	";
		java.sql.ResultSet rs6 = st6.executeQuery(sql6);

		// ---------------------------------------------------- Fattori ambientali
		java.sql.Statement st7 = conn1.createStatement();
		String sql7 = "SELECT DE_CAPACITA.CODCAPACITA, 													"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'FA'                                               	";
		java.sql.ResultSet rs7 = st7.executeQuery(sql7);

		// ---------------------------------------------------- Situazioni lavorative
		java.sql.Statement st8 = conn1.createStatement();
		String sql8 = "SELECT DE_CAPACITA.CODCAPACITA, 													"
				+ "	          		 CM_CAPACITA.CODCAPACITA, 														"
				+ "	          		 CM_CAPACITA.PRGDIAGNOSIFUNZIONALE,                                        		"
				+ "	          		 DE_CAPACITA.STRDESCRIZIONE,                                                	"
				+ "	          		 DE_CAPACITA.CODCAPRAGGR,                                                     	"
				+ "					 CM_CAPACITA.codgradocapacita,													"
				+ "					 DE_GRADO_CAPACITA.CODMONOTIPOGRADO,												"
				+ "  CM_CAPACITA.strDescGrado "
				+ "			  FROM   CM_CAPACITA CM_CAPACITA,                                      		"
				+ "	   				 DE_CAPACITA DE_CAPACITA,                                         	"
				+ "	   				 DE_GRADO_CAPACITA DE_GRADO_CAPACITA                              	"
				+ "			  WHERE                                                                            		"
				+ "	   				 CM_CAPACITA.CODCAPACITA(+)=DE_CAPACITA.CODCAPACITA and                       	"
				+ "       			 CM_CAPACITA.CODGRADOCAPACITA=DE_GRADO_CAPACITA.CODGRADOCAPACITA(+) and    		"
				+ "	   				 CM_CAPACITA.prgdiagnosifunzionale(+) = " + prgDiagnosiFunzionale
				+ "	   				 and                                                                          	"
				+ "	   				 DE_CAPACITA.codCapRaggr = 'SL'                                               	";
		java.sql.ResultSet rs8 = st8.executeQuery(sql8);

		// ---------------------------------------------------- Profilo socio-assistenziale: Servizi
		java.sql.Statement st9 = conn1.createStatement();
		String sql9 = "SELECT distinct CM_PROGETTO_SOCIOASSIST.PRGDIAGNOSIFUNZIONALE,"
				+ "DE_SERVIZIO_SOC.STRDESCRIZIONE " + "FROM CM_PROGETTO_SOCIOASSIST CM_PROGETTO_SOCIOASSIST, "
				+ "DE_SERVIZIO_SOC DE_SERVIZIO_SOC "
				+ "WHERE CM_PROGETTO_SOCIOASSIST.CODSERVIZIO=DE_SERVIZIO_SOC.CODSERVIZIO "
				+ "and CM_PROGETTO_SOCIOASSIST.PRGDIAGNOSIFUNZIONALE = " + prgDiagnosiFunzionale;
		java.sql.ResultSet rs9 = st9.executeQuery(sql9);

		// ---------------------------------------------------- Dati sul lavoratore
		java.sql.Statement stDecriptLav = conn1.createStatement();
		String sqlDecriptLav = " SELECT cdnlavoratore " + " FROM cm_diagnosi_funzionale "
				+ " WHERE prgdiagnosifunzionale = " + prgDiagnosiFunzionale;
		java.sql.ResultSet rsDecriptLav = stDecriptLav.executeQuery(sqlDecriptLav);

		String cdnLavoratore = "";
		while (rsDecriptLav.next()) {
			cdnLavoratore = rsDecriptLav.getString("cdnLavoratore");
		}

		// Decodifica del cdnLavoratore
		String cdnLavoratoreDecrypt = EncryptDecryptUtils.decrypt(cdnLavoratore);

		java.sql.Statement stLav = conn1.createStatement();
		String sqlLav = " SELECT an_lavoratore.STRCOGNOME, " + " an_lavoratore.STRNOME, " + " an_lavoratore.DATNASC, "
				+ " de_comune.STRDENOMINAZIONE, " + " an_lavoratore.CDNLAVORATORE "
				+ " FROM an_lavoratore an_lavoratore, " + " de_comune de_comune "
				+ " WHERE an_lavoratore.CODCOMNAS = de_comune.CODCOM " + " and an_lavoratore.CDNLAVORATORE = "
				+ cdnLavoratoreDecrypt;
		java.sql.ResultSet rsLav = stLav.executeQuery(sqlLav);
		// ----------------------------------------------------
		String path = ConfigSingleton.getRootPath() + "/WEB-INF/report/Diagnosi_Funzionale/DiagnosiFunzionale_CC.rpt";

		File f = new File(path);
		engine.setReportFile("file:" + f.getAbsolutePath());
		// ----------------------------------------------------
		/*
		 * com.inet.report.Engine subreport1 = engine.getSubReport(1); subreport1.setData(rs1);
		 * 
		 * com.inet.report.Engine subreport2 = engine.getSubReport(2); subreport2.setData(rs2);
		 * 
		 * com.inet.report.Engine subreport3 = engine.getSubReport(3); subreport3.setData(rs3);
		 * 
		 * com.inet.report.Engine subreport4 = engine.getSubReport(4); subreport4.setData(rs4);
		 * 
		 * com.inet.report.Engine subreport5 = engine.getSubReport(5); subreport5.setData(rs5);
		 * 
		 * com.inet.report.Engine subreport6 = engine.getSubReport(6); subreport6.setData(rs6);
		 * 
		 * com.inet.report.Engine subreport7 = engine.getSubReport(7); subreport7.setData(rs7);
		 * 
		 * com.inet.report.Engine subreport8 = engine.getSubReport(8); subreport8.setData(rs8);
		 * 
		 * com.inet.report.Engine subreport9 = engine.getSubReport(12); subreport9.setData(rs9);
		 */
		Hashtable rsData = new Hashtable();

		rsData.put("subreport0", rsLav);
		rsData.put("subreport2", rs1);
		rsData.put("subreport3", rs2);
		rsData.put("subreport4", rs3);
		rsData.put("subreport5", rs4);
		rsData.put("subreport6", rs5);
		rsData.put("subreport7", rs6);
		rsData.put("subreport8", rs7);
		rsData.put("subreport9", rs8);
		rsData.put("subreport13", rs9);

		// gli Statement andranno chiusi insert stLav
		rsData.put("statements", new Object[] { st1, st2, st3, st4, st5, st6, st7, st8, st9, stLav });
		DiagnosiFunzionaleResultSet dfrs = new DiagnosiFunzionaleResultSet(rsData);
		// ora i result set verranno impostati nei subreports. Alla fine verranno chiusi sia i result set che gli
		// statement.
		dfrs.setDataTo(engine);

		// nel caso di una protocollazione DOCAREA e la richiesta di invio completo bisogna creare due report.
		// Per il secondo servono result set nuovi, dato che gli altri verranno chiusi dall'engine di Crystal Clear.
		if ("PR".equals(codStatoAtto) && !ProtocolloDocumentoUtil.protocollazioneLocale()
				&& ProtocolloDocumentoUtil.invioCompleto()) {

			st1 = conn1.createStatement();
			st2 = conn1.createStatement();
			st3 = conn1.createStatement();
			st4 = conn1.createStatement();
			st5 = conn1.createStatement();
			st6 = conn1.createStatement();
			st7 = conn1.createStatement();
			st8 = conn1.createStatement();
			st9 = conn1.createStatement();
			stLav = conn1.createStatement();

			rs1 = st1.executeQuery(sql1);
			rs2 = st2.executeQuery(sql2);
			rs3 = st3.executeQuery(sql3);
			rs4 = st4.executeQuery(sql4);
			rs5 = st5.executeQuery(sql5);
			rs6 = st6.executeQuery(sql6);
			rs7 = st7.executeQuery(sql7);
			rs8 = st8.executeQuery(sql8);
			rs9 = st9.executeQuery(sql9);
			rsLav = stLav.executeQuery(sqlLav);

			rsData = new Hashtable();
			rsData.put("subreport0", rsLav);
			rsData.put("subreport2", rs1);
			rsData.put("subreport3", rs2);
			rsData.put("subreport4", rs3);
			rsData.put("subreport5", rs4);
			rsData.put("subreport6", rs5);
			rsData.put("subreport7", rs6);
			rsData.put("subreport8", rs7);
			rsData.put("subreport9", rs8);
			rsData.put("subreport13", rs9);
			// gli Statement andranno chiusi
			rsData.put("statements", new Object[] { st1, st2, st3, st4, st5, st6, st7, st8, st9, stLav });
			dfrs = new DiagnosiFunzionaleResultSet(rsData);
			Hashtable userData = new Hashtable();
			// si passa l'oggetto all'engine che lo utilizzara' prima della creazione del report numero due (SOLO caso
			// DOCAREA)
			userData.put("ReportResultSet", dfrs);
			engine.setUserData(userData);
		}
		return engine;
	}

	private String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

} // class DiagnosiFunzionale