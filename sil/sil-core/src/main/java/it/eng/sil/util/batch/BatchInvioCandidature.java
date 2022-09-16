package it.eng.sil.util.batch;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.CLSender;
import it.eng.sil.coop.webservices.clicLavoro.candidatura.CLCandidaturaData;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

/**
 * Batch per l'invio candidature clicLavoro
 * 
 * @author manuel
 *
 */
public class BatchInvioCandidature implements BatchRunnable {

	private static Logger logger = Logger.getLogger(BatchInvioCandidature.class.getName());
	private String[] parametri;

	private final String GET_CANDIDATURE = "SELECT cand.prgcandidatura, lav.strcodicefiscale CODFISCALE, "
			+ " cand.cdnlavoratore, cand.codstatoinviocl, cand.codtipocomunicazionecl, " + " cand.codcpi CODCPI, "
			+ " TO_CHAR(cand.datinvio, 'dd/MM/yyyy') DTINVIO " + " FROM cl_candidatura cand "
			+ " INNER JOIN an_lavoratore lav " + " ON cand.cdnlavoratore = lav.cdnlavoratore "
			+ " where cand.codstatoinviocl in ('PA', 'VA')";

	private final String UPDATE_CL_CANDIDATURA = " UPDATE cl_candidatura " + " SET CODSTATOINVIOCL  = ?, "
			+ "  datinvio           = sysdate, " + "  numklocandidatura  = numklocandidatura + 1, " + "  cdnutmod = ?, "
			+ "  dtmmod = sysdate " + " WHERE prgcandidatura = ? ";

	private static final String WS_LOGON = "SELECT prgws, struserid, " + "strpassword AS cln_pwd " + "FROM   ts_ws "
			+ " WHERE  codservizio = 'SIL_CLICLAV_MYPORTAL'";

	public static void main(String[] args) {
		BatchInvioCandidature app = null;
		try {
			app = new BatchInvioCandidature();
			app.setParametri(args);
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
		}
	}

	public BatchInvioCandidature() {
	}

	@SuppressWarnings("unchecked")
	public void start() {
		logger.debug("=========== Avvio Batch ===========");
		logger.info("INVIO CANDIDATURA: Avvio Batch", null);
		Context ctx;
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		Connection conn = null;
		try {
			ctx = new InitialContext();
			Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
			if (objs instanceof DataSource) {
				DataSource ds = (DataSource) objs;
				conn = ds.getConnection();
				dc = new DataConnection(conn, "2", new OracleSQLMapper());
				dc.initTransaction();
				qExec = BatchInvioCandidature.getQueryExecutorObject(dc);
			}

			// RECUPERO CANDIDATURE DA INVIARE
			qExec.setStatement(this.GET_CANDIDATURE);
			qExec.setType(QueryExecutorObject.SELECT);
			SourceBean candidatureSB = (SourceBean) qExec.exec();
			List<SourceBean> listCandidature = candidatureSB.getAttributeAsVector("ROW");

			int numTotCandidature = listCandidature.size();
			logger.info("INVIO CANDIDATURA: totali da inviare: " + numTotCandidature, null);

			for (int i = 0; i < listCandidature.size(); i++) {

				if (conn == null) {
					dc.initTransaction();
				}

				boolean skipInvio = false;
				if (listCandidature.get(i).getAttribute("PRGCANDIDATURA") != null) {
					String codStatoInvioUpdate = "";
					BigDecimal prgCandidatura = (BigDecimal) listCandidature.get(i).getAttribute("PRGCANDIDATURA");
					String codFiscale = (String) listCandidature.get(i).getAttribute("CODFISCALE").toString();
					BigDecimal cdnLavoratore = (BigDecimal) listCandidature.get(i).getAttribute("CDNLAVORATORE");
					String codCpi = (String) listCandidature.get(i).getAttribute("CODCPI").toString();
					String datInvio = (String) listCandidature.get(i).getAttribute("DTINVIO").toString();
					String codTipoInvio = (String) listCandidature.get(i).getAttribute("CODSTATOINVIOCL");
					String codtipocomunicazionecl = (String) listCandidature.get(i)
							.getAttribute("codtipocomunicazionecl");
					if (("PA").equalsIgnoreCase(codTipoInvio)) {
						codStatoInvioUpdate = "PI";
					} else {
						codStatoInvioUpdate = "VI";
					}

					String strDatiCandidatura = " PRGCANDIDATURA: " + prgCandidatura.toString() + " CODICE FISCALE: "
							+ codFiscale + " CODCPI: " + codCpi + " DATINVIO: " + datInvio;

					int numCandIesima = i;
					numCandIesima = numCandIesima + 1;
					logger.info("INVIO CANDIDATURA: inizio numero " + numCandIesima + " di " + numTotCandidature, null);
					logger.info("INVIO CANDIDATURA: " + strDatiCandidatura, null);

					// GENERO L'XML DELLA CANDIDATURA DA INVIARE
					String xmlGenerato = null;
					try {
						logger.debug("INVIO CANDIDATURA: Creazione e validazione xml", null);
						xmlGenerato = buildCandidatura(cdnLavoratore, codCpi, datInvio, codtipocomunicazionecl, dc);
						if (StringUtils.isFilledNoBlank(xmlGenerato)) {
							xmlGenerato = xmlGenerato.replace("<cliclavoro", "\n<cliclavoro");
							xmlGenerato = xmlGenerato.replace("></", ">\n</");
						}
					} catch (MandatoryFieldException e) {
						logger.error("Errore MandatoryFieldException: " + e.getExceptionMessage());
						Vector<String> param = new Vector<String>();
						param.add(e.getMessageParameter());
						logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_INPUT_ERRATO, param));
						logger.error("ERRORE= " + strDatiCandidatura);
						skipInvio = true;
					} catch (FieldFormatException e) {
						logger.error("Errore FieldFormatException: " + e.getExceptionMessage());
						Vector<String> param = new Vector<String>();
						param.add(e.getMessageParameter());
						logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_INPUT_ERRATO, param));
						logger.error("ERRORE= " + strDatiCandidatura);
						skipInvio = true;
					} catch (EMFUserError e) {
						logger.error("Errore FieldFormatException: " + e);
						Vector<String> param = new Vector<String>();
						logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
						logger.error("ERRORE= " + strDatiCandidatura);
						skipInvio = true;
					}

					logger.debug("INVIO CANDIDATURA: xml= " + xmlGenerato, null);

					if (!skipInvio) {
						logger.debug("INVIO CANDIDATURA: Invio all'NCR", null);

						CLSender sender = new CLSender();
						try {
							sender.popolaAddressByDs(Values.JDBC_JNDI_NAME);
						} catch (Exception e) {
							logger.error("popolaAddress", e);
							Vector<String> param = new Vector<String>();
							logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
							logger.error("ERRORE= " + strDatiCandidatura);
						}

						// INVIO CANDIDATURA
						try {
							String username = "", password = "";
							PreparedStatement ps = null;
							PreparedStatement psUrl = null;
							String endPoint = null;
							ResultSet rs = null;
							ResultSet rsUrl = null;
							try {
								ps = conn.prepareStatement(WS_LOGON);
								rs = ps.executeQuery();

								if (rs.next()) {
									password = rs.getString("cln_pwd");
									username = rs.getString("struserid");
								}
								rs.close();
								ps.close();

								if (password == null || username == null) {
									logger.info("Username o Password null sendWSMyPortal", null);
									throw new Exception("Record non trovato");
								}
							} catch (Exception e) {
								logger.error("Impossibile trovare in TS_WS username per il servizio: SILMYPORTAL" + e);
							}

							boolean wsResult = sender.sendComunicazioneToNCR(username, password, xmlGenerato);

							logger.debug("INVIO CANDIDATURA: Risposta NCR=" + wsResult, null);

							// SE INVIO A BUON FINE, AGGIORNO CANDIDATURA A INVIATO
							if (wsResult) {
								ArrayList<DataField> inputParameters = new ArrayList<DataField>();
								inputParameters
										.add(dc.createDataField("CODSTATOINVIOCL", Types.VARCHAR, codStatoInvioUpdate));
								inputParameters.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, parametri[0]));
								inputParameters
										.add(dc.createDataField("PRGCANDIDATURA", Types.NUMERIC, prgCandidatura));
								qExec.setStatement(this.UPDATE_CL_CANDIDATURA);
								qExec.setInputParameters(inputParameters);
								qExec.setType(QueryExecutorObject.UPDATE);

								try {
									boolean esitoUpdate = ((Boolean) qExec.exec());

									if (!esitoUpdate) {
										dc.rollBackTransaction();
										logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, null));
									} else {
										dc.commitTransaction();
									}
								} catch (EMFInternalError e) {
									dc.rollBackTransaction();
									logger.error("ERRORE NEL BATCH INVIO CANDIDATURA, IMPOSSIBILE FARE COMMIT", e);
								}
							} else {
								dc.rollBackTransaction();
								logger.error(
										"ERRORE NEL BATCH INVIO CANDIDATURA: errore durante la comunicazione con l'NCR");
							}

						} catch (RemoteException e) {
							logger.error("INVIO CANDIDATURA A NCR", e);
							Vector<String> param = new Vector<String>();
							logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
							logger.error("ERRORE= " + strDatiCandidatura);
						} catch (ServiceException e) {
							logger.error("INVIO CANDIDATURA A NCR", e);
							Vector<String> param = new Vector<String>();
							logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
							logger.error("ERRORE= " + strDatiCandidatura);
						}
					}

					logger.info("INVIO CANDIDATURA: fine numero " + numCandIesima + " di " + numTotCandidature, null);
				}
			}
		} catch (Exception e1) {
			logger.error("ERRORE NEL BATCH INVIO CANDIDATURE", e1);
			Utils.releaseResources(dc, null, null);
		} finally {

			Utils.releaseResources(dc, null, null);
		}

		logger.info("INVIO CANDIDATURA: Fine Batch", null);
	}

	public void setParametri(String[] args) {
		parametri = new String[1];
		parametri[0] = args[0]; // user //Se avviati da .bat impostarlo di default

		logger.info("par 0=" + parametri[0]);
	}

	/**
	 * Recupero del QueryExecutorObject: sono fuori dal contesto SIL, per cui mi tocca usare questo al posto del
	 * TransactionQueryExecutor
	 * 
	 * @param dc
	 *            DataConnection
	 * @return QueryExecutorObject
	 */
	public static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
		logger.info("getQueryExecutorObject(DataConnection) - start");

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setTransactional(true);
		qExec.setDontForgetException(false);

		logger.info("getQueryExecutorObject(DataConnection) - end");
		return qExec;
	}

	public String getMessageLog(int messageCode, Vector<String> params) {
		EMFUserError errorItem;
		if (params == null) {
			errorItem = new EMFUserError(EMFErrorSeverity.ERROR, messageCode);
		} else {
			errorItem = new EMFUserError(EMFErrorSeverity.ERROR, messageCode, params);
		}
		return errorItem.getDescription();
	}

	/**
	 * 
	 * @param cdnLavoratore
	 * @param codiceFiscale
	 * @param codCPI
	 * @param dataInvio
	 * @return
	 * @throws EMFUserError
	 * @throws MandatoryFieldException
	 * @throws FieldFormatException
	 */
	public String buildCandidatura(BigDecimal cdnLavoratore, String codCPI, String dataInvio,
			String tipoComunicazioneCL, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaData cLCandidaturaData = new CLCandidaturaData(codCPI, dataInvio, tipoComunicazioneCL);
		cLCandidaturaData.costruisci(cdnLavoratore, dc);

		return cLCandidaturaData.generaXML();
	}
}
