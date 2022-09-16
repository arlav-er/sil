package it.eng.sil.module.anag;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.verificaSAP.VerificaSAP;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;

public class VerificaEsistenzaSAP extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1533726811528932058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VerificaEsistenzaSAP.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "VerificaEsistenzaSAP";

	private boolean isCruscottoDid = false;
	private User user;
	private SourceBean anLavoratoreSB;
	private ReportOperationResult reportOperation;
	private String codMinSap;

	public void service(SourceBean request, SourceBean response) {

		if (!isCruscottoDid()) {
			user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			reportOperation = new ReportOperationResult(this, response);
			disableMessageIdFail();
			disableMessageIdSuccess();

		} else {
			user = getUser();
		}
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));

		codMinSap = "";
		TransactionQueryExecutor tex = null;

		try {
			_logger.info("CHIAMATA VERIFICA ESISTENZA SAP, CDNLAVORATORE =" + cdnLavoratore);

			if (!isCruscottoDid) {
				anLavoratoreSB = doSelect(request, response);
			} else {
				anLavoratoreSB = getAnLavoratoreSB();
			}

			String strCodiceFiscale = (String) anLavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String verificaEsistenzaSAPEndPoint = eps.getUrl(END_POINT_NAME);
			ServizicoapWSProxy servizicoapWSProxy = new ServizicoapWSProxy(verificaEsistenzaSAPEndPoint);

			VerificaSAP xmlVerifica = new VerificaSAP();
			xmlVerifica.setCodiceFiscale(strCodiceFiscale);

			codMinSap = servizicoapWSProxy.verificaEsistenzaSAP(convertVerificaSapToString(xmlVerifica));
			setCodMinSap(codMinSap);
			Vector<String> params = new Vector<String>();
			params.add(codMinSap);

			// query su sp_lavoratore
			if (codMinSap != null && !codMinSap.equals("") && !codMinSap.equals("0")
					&& !codMinSap.trim().startsWith("X001")) {
				tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tex.initTransaction();
				SourceBean spLavoratoreSB = (SourceBean) tex.executeQuery("SELECT_SP_LAVORATORE_CDNLAV",
						new Object[] { cdnLavoratore }, "SELECT");
				if (spLavoratoreSB.getAttribute("ROW") instanceof Vector) {
					// PIU' DI UN SP_LAVORATORE TROVATO
					if (!isCruscottoDid) {
						reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
					}
					String errMsg = "VerificaEsistenzaSAP: fallito. Trovata piu' di una riga su SP_LAVORATORE con CDNLAVORATORE = "
							+ cdnLavoratore + " e DATFINEVAL == NULL.";
					_logger.error(errMsg);
					tex.rollBackTransaction();
				} else if (spLavoratoreSB.getAttribute("ROW") instanceof SourceBean) {
					SourceBean spLavoratoreRow = (SourceBean) spLavoratoreSB.getAttribute("ROW");
					boolean chiudiRecordPrec = false;
					// MARIA 16/16/2014
					// chiudere il record precedente solo se è bruciatura
					String codStatoSap = (String) spLavoratoreRow.getAttribute("CODSTATO");
					String codMinSapSpLav = (String) spLavoratoreRow.getAttribute("CODMINSAP");

					if (("02").equalsIgnoreCase(codStatoSap)) {
						chiudiRecordPrec = true;
					} else if (("01").equalsIgnoreCase(codStatoSap) || ("04").equalsIgnoreCase(codStatoSap)) {
						if (!codMinSap.equalsIgnoreCase(codMinSapSpLav)) {
							chiudiRecordPrec = true;
						}
					}

					String codCpiMin = (String) spLavoratoreRow.getAttribute("CODENTETIT");
					String strCodiceFiscaleSil = (String) spLavoratoreRow.getAttribute("STRCODICEFISCALE");
					String datNascita = (String) spLavoratoreRow.getAttribute("DATNASC");

					if (chiudiRecordPrec) {
						// chiudo record precedente sp_lavoratore
						Object[] paramsUpdate = new Object[2];
						paramsUpdate[0] = p_cdnUtente;
						paramsUpdate[1] = cdnLavoratore;
						Object queryResUpdate = tex.executeQuery("UPDATE_SP_LAVORATORE_INVIO_SAP", paramsUpdate,
								"UPDATE");
						if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
								&& ((Boolean) queryResUpdate).booleanValue() == true)) {
							throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
						}

						// inserisco il record in SP_LAVORATORE
						Object[] paramsIns = new Object[8];
						paramsIns[0] = codMinSap;
						paramsIns[1] = codCpiMin;
						paramsIns[2] = datNascita;
						paramsIns[3] = cdnLavoratore;
						paramsIns[4] = p_cdnUtente;
						paramsIns[5] = p_cdnUtente;
						paramsIns[6] = strCodiceFiscaleSil;
						paramsIns[7] = "01"; // codstatosap ATTIVA
						Object queryRes = tex.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP", paramsIns, "INSERT");
						if (queryRes == null
								|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
							throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
						}

						_logger.debug("Lavoratore: " + strCodiceFiscale + " - CODICE SAP Nazionale " + codMinSap
								+ ": aggionamento avvenuto con successo");
					}
					/*
					 * uso reportFailure solo per passare un parametro alla stringa, in realtà' il servizio termina con
					 * successo
					 */

					if (chiudiRecordPrec && !isCruscottoDid) {
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_TROVATO_MODIFICA, "service",
								"SAP trovata", params);
					} else if (!isCruscottoDid) {
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_TROVATO_NO_MODIFICA, "service",
								"SAP trovata", params);
					}

				} else if (spLavoratoreSB.getAttribute("ROW") == null) {

					SourceBean lavoratoreSB = (SourceBean) tex.executeQuery("GET_AN_LAVORATORE_ANAG",
							new Object[] { cdnLavoratore }, "SELECT");
					String strCodiceFiscaleSilLav = (String) lavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");
					String datNascitaLav = (String) lavoratoreSB.getAttribute("ROW.DATNASC");

					SourceBean cpiLavoratoreSB = (SourceBean) tex.executeQuery("GET_CPI_AN_LAVORATORE",
							new Object[] { cdnLavoratore }, "SELECT");
					String codCpiMinLav = (String) cpiLavoratoreSB.getAttribute("ROW.CPICOMPMIN");

					/// inserisco il record in SP_LAVORATORE
					Object[] paramsIns = new Object[8];
					paramsIns[0] = codMinSap;
					paramsIns[1] = codCpiMinLav;
					paramsIns[2] = datNascitaLav;
					paramsIns[3] = cdnLavoratore;
					paramsIns[4] = p_cdnUtente;
					paramsIns[5] = p_cdnUtente;
					paramsIns[6] = strCodiceFiscaleSilLav;
					paramsIns[7] = "01"; // codstatosap ATTIVA
					Object queryRes = tex.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP", paramsIns, "INSERT");
					if (queryRes == null
							|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
						throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
					}

					_logger.debug("Lavoratore: " + strCodiceFiscale + " - CODICE SAP Nazionale " + codMinSap
							+ ": aggionamento avvenuto con successo");
					/*
					 * uso reportFailure solo per passare un parametro alla stringa, in realtà' il servizio termina con
					 * successo
					 */
					if (!isCruscottoDid) {
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_TROVATO_MODIFICA, "service",
								"SAP trovata", params);
					}
				}
				tex.commitTransaction();
			} else {
				_logger.debug("Lavoratore: " + strCodiceFiscale + " - SAP Nazionale inesistente");
				if (codMinSap != null && ("0").equalsIgnoreCase(codMinSap)) {
					boolean risChiudiZero = VerificaEsistenzaSAP.chiudiRecordSapZero(new BigDecimal(cdnLavoratore),
							p_cdnUtente, null);
				}
				if (!isCruscottoDid) {
					if (codMinSap != null && codMinSap.trim().startsWith("X001")) {
						reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
								"Errore ministero SAP", params);
					} else {
						reportOperation.reportSuccess(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
					}
				}
			}
		} catch (Exception ex) {
			if (!isCruscottoDid) {
				reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
			}
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata SAP", ex);
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("VerificaEsistenzaSAP: problema con la rollback", e1);
			}
		}
	}

	public static boolean chiudiRecordSapZero(BigDecimal cdnLavoratore, BigDecimal userid,
			TransactionQueryExecutor transExec) {
		try {
			if (transExec != null) {
				Object[] fielWhere = new Object[1];
				fielWhere[0] = cdnLavoratore;
				SourceBean sbSap = (SourceBean) transExec.executeQuery("GET_SP_LAVORATORE_VERIFICA_SAP_ZERO", fielWhere,
						"SELECT");
				if (sbSap != null) {
					sbSap = sbSap.containsAttribute("ROW") ? (SourceBean) sbSap.getAttribute("ROW") : sbSap;
					BigDecimal prgSpLav = (BigDecimal) sbSap.getAttribute("PRGSPLAV");
					BigDecimal numkloSpLav = (BigDecimal) sbSap.getAttribute("NUMKLOSAP");
					if (prgSpLav != null) {
						Object[] paramsUpdate = new Object[3];
						paramsUpdate[0] = numkloSpLav;
						paramsUpdate[1] = userid;
						paramsUpdate[2] = prgSpLav;
						Object queryResUpdate = transExec.executeQuery("UPDATE_SP_LAVORATORE_VERIFICA_SAP_ZERO",
								paramsUpdate, "UPDATE");
						if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
								&& ((Boolean) queryResUpdate).booleanValue() == true)) {
							return false;
						}
					}
				}
			} else {
				QueryExecutorObject qExec = null;
				DataConnection dc = null;
				try {
					qExec = getQueryExecutorObject();
					dc = qExec.getDataConnection();
					dc.initTransaction();
					qExec.setStatement(SQLStatements.getStatement("GET_SP_LAVORATORE_VERIFICA_SAP_ZERO"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("CDNLAVORATORE", Types.BIGINT, cdnLavoratore));
					qExec.setInputParameters(params);
					SourceBean sbSap = (SourceBean) qExec.exec();
					if (sbSap != null) {
						sbSap = sbSap.containsAttribute("ROW") ? (SourceBean) sbSap.getAttribute("ROW") : sbSap;
						BigDecimal prgSpLav = (BigDecimal) sbSap.getAttribute("PRGSPLAV");
						BigDecimal numkloSpLav = (BigDecimal) sbSap.getAttribute("NUMKLOSAP");
						if (prgSpLav != null) {
							qExec.setStatement(SQLStatements.getStatement("UPDATE_SP_LAVORATORE_VERIFICA_SAP_ZERO"));
							qExec.setType(QueryExecutorObject.UPDATE);
							List<DataField> paramsUpd = new ArrayList<DataField>();
							paramsUpd.add(dc.createDataField("NUMKLOSAP", Types.BIGINT, numkloSpLav));
							paramsUpd.add(dc.createDataField("CDNUTMOD", Types.BIGINT, userid));
							paramsUpd.add(dc.createDataField("PRGSPLAV", Types.BIGINT, prgSpLav));
							qExec.setInputParameters(paramsUpd);
							Object queryResUpdate = qExec.exec();
							if (queryResUpdate == null || !(queryResUpdate instanceof Boolean
									&& ((Boolean) queryResUpdate).booleanValue() == true)) {
								return false;
							}
						}
					}
					dc.commitTransaction();
				} catch (Exception ex) {
					dc.rollBackTransaction();
				} finally {
					com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
					qExec = null;
				}
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static QueryExecutorObject getQueryExecutorObject() throws NamingException, SQLException, EMFInternalError {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = new QueryExecutorObject();

			qExec.setRequestContainer(null);
			qExec.setResponseContainer(null);
			qExec.setDataConnection(dc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setTransactional(true);
			qExec.setDontForgetException(false);
		} else {
			_logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	private String convertVerificaSapToString(VerificaSAP verifica) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(VerificaSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(verifica, writer);
		String xmlVerificaSAP = writer.getBuffer().toString();
		return xmlVerificaSAP;
	}

	public boolean isCruscottoDid() {
		return isCruscottoDid;
	}

	public void setCruscottoDid(boolean isCruscottoDid) {
		this.isCruscottoDid = isCruscottoDid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SourceBean getAnLavoratoreSB() {
		return anLavoratoreSB;
	}

	public void setAnLavoratoreSB(SourceBean anLavoratoreSB) {
		this.anLavoratoreSB = anLavoratoreSB;
	}

	public String getCodMinSap() {
		return codMinSap;
	}

	public void setCodMinSap(String codMinSap) {
		this.codMinSap = codMinSap;
	}

}