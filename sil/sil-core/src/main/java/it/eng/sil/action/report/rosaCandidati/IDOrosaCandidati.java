package it.eng.sil.action.report.rosaCandidati;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.sms.ContattoSMS;

public class IDOrosaCandidati extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(IDOrosaCandidati.class.getName());
	protected final static String DELETE_OK = "DELETE_OK";
	protected final static String INSERT_OK = "INSERT_OK";
	protected final static String INSERT_FAIL = "INSERT_FAIL";
	protected final static String UPDATE_OK = "UPDATE_OK";
	protected final static String UPDATE_FAIL = "UPDATE_FAIL";

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String datInizioRosa = (String) request.getAttribute("datInvio");
			String prgRichAzienda = (String) request.getAttribute("prgRichAzienda");
			String prgRosa = (String) request.getAttribute("prgRosa");
			String cdnGruppo = (String) request.getAttribute("cdnGruppo");
			String numAnno = (String) request.getAttribute("numAnno");
			String numRichiesta = (String) request.getAttribute("numRichiesta");

			String strInviaSMS = StringUtils.getAttributeStrNotNull(request, "inviaSMS");
			boolean inviaSMS = false;
			if (strInviaSMS.equalsIgnoreCase("true"))
				inviaSMS = true;

			// Trovo l'utente connesso
			RequestContainer requestContainer = this.getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String pCdnUtInsMod = Integer.toString(user.getCodut());
			String pCdnGrpUtMod = String.valueOf(user.getCdnGruppo());
			String tipoFile = (String) request.getAttribute("tipoFile");
			setStrNomeDoc("RosaCandidati." + (StringUtils.isEmpty(tipoFile) ? "pdf" : tipoFile));
			setReportPath("RosaCandidati_IDO/RosaCandidati_CC.rpt");

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			// GG - aggiunti:
			if (tipoDoc != null) {
				// A seconda del tipo di stampa: Rosa dei candidati / Tutte le
				// rose relative alla richiesta
				if (tipoDoc.equalsIgnoreCase("ROS"))
					setStrDescrizione("Rosa dei candidati");
				else if (tipoDoc.equalsIgnoreCase("TROS"))
					setStrDescrizione("Tutte le rose relative alla richiesta");
			}
			setFlgDocAmm(null);
			setFlgAutocertificazione("N");
			setFlgDocIdentifP("N");
			setStrNumDoc(null);
			String codCpi = user.getCodRif();
			setStrEnteRilascio(codCpi);

			// GG 18/2/2005
			String docInOut = SourceBeanUtils.getAttrStr(request, "docInOut", "O");
			setCodMonoIO(docInOut);

			setDatAcqril(datInizioRosa);
			setCodModalitaAcqril(null);
			setCodTipoFile(null);

			// GG: per usare la AM_DOC_COLL
			String pagina = (String) request.getAttribute("pagina");
			if (pagina != null)
				setPagina(pagina);
			String prgOrig = (String) request.getAttribute("strChiaveTabella");
			setStrChiavetabella(prgOrig);
			// setStrChiavetabella("Riferimento alla richiesta");

			// già esistenti:

			BigDecimal parametro = new BigDecimal((String) request.getAttribute("prgAzienda"));
			if (parametro != null)
				this.setPrgAzienda(parametro);

			parametro = new BigDecimal((String) request.getAttribute("prgUnita"));
			if (parametro != null)
				this.setPrgUnita(parametro);

			Vector params = new Vector(5);
			params.add(prgRichAzienda);
			params.add(cdnGruppo);
			params.add(prgRosa);
			params.add(numAnno);
			params.add(numRichiesta);

			/*
			 * BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null); if (numProt != null) {
			 * setNumProtocollo(numProt); params.add(numProt.toString()); }
			 * 
			 * String annoProt = (String) request.getAttribute("annoProt"); if (annoProt != null &&
			 * !annoProt.equals("")) { setNumAnnoProt(new BigDecimal(annoProt)); params.add(annoProt); }
			 */
			String kLock = (String) request.getAttribute("kLockProt");
			if (kLock != null && !kLock.equals("")) {
				setNumKeyLock(new BigDecimal(kLock));
			}

			setParams(params);

			try {
				String tutteLeRose = (!prgRosa.equals("0") ? "0" : "1");

				// boolean upDate =
				boolean ret = aggiornaDataInvio(datInizioRosa, prgRichAzienda, prgRosa, pCdnUtInsMod, tutteLeRose,
						pCdnGrpUtMod, response);
				if (!ret) {
					setOperationFail(request, response);
					return;
				}
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {
					ret = insertDocument(request, response);
					if (!ret)
						return;
				} else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "IDOrosaCandidati::service:", ex);

			}

			// ======================== INVIO SMS
			// =============================================================
			if (inviaSMS) {
				// Se tutto è andato a buon fine invio gli SMS ai candidati
				DataConnectionManager dataConnectionManager;
				DataConnection dataConnection;
				try {
					// Recupero (e costruisco) il testo dell'SMS da inviare
					dataConnectionManager = DataConnectionManager.getInstance();
					dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
					SourceBean result = null;

					String query = " select rich2.strmansionepubb, an.* from do_nominativo r "
							+ " inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE) "
							+ " inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA) "
							+ " inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO) "
							+ " inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ) "
							+ " inner join do_richiesta_az rich2 on (rich1.NUMANNO=rich2.NUMANNO and rich1.NUMRICHIESTA=rich2.NUMRICHIESTA and rich2.NUMSTORICO=0) "
							+ " left outer join do_disponibilita on (rich2.PRGRICHIESTAAZ=do_disponibilita.PRGRICHIESTAAZ and do_disponibilita.CDNLAVORATORE=r.cdnLavoratore) "
							+ " where r.CODTIPOCANC is null "
							+ "  and (do_disponibilita.CODDISPONIBILITAROSA ='A'or do_disponibilita.CODDISPONIBILITAROSA is null)"
							+ "  and do_rosa.PRGTIPOROSA = 3" + "  and rich2.NUMANNO = " + numAnno
							+ "  and rich2.NUMRICHIESTA = " + numRichiesta;

					if (!"".equals(prgRosa) && !"0".equals(prgRosa)) {
						query += " and do_rosa.prgrosa=" + prgRosa;
					}

					QueryExecutorObject queryExecObj = new QueryExecutorObject();
					queryExecObj.setDataConnection(dataConnection);
					queryExecObj.setStatement(query);
					queryExecObj.setType("SELECT");
					queryExecObj.setDontForgetException(false);
					queryExecObj.setDataConnection(dataConnection);
					Object scrollableDataResult = queryExecObj.exec();

					if (scrollableDataResult instanceof ScrollableDataResult) {
						result = ((ScrollableDataResult) scrollableDataResult).getSourceBean();
					} else if (scrollableDataResult instanceof SourceBean) {
						result = (SourceBean) scrollableDataResult;
					} else {
						// TODO gestire il mancato reperimento di una lista.
					}

					ContattoSMS contattoSms = new ContattoSMS();
					SourceBean nonInviati = contattoSms.creaPerRosacandidati(result, user);
					if (nonInviati != null && nonInviati.getContainedAttributes().size() != 0) {
						_logger.warn(nonInviati);
					}

				} catch (EMFInternalError e) {
					_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

				}

			} // ======================== FINE INVIO SMS
				// =============================================================

		} // else

		// Chiudo la richiesta corrispondente se devo
		String chiudi = StringUtils.getAttributeStrNotNull(request, "chiudiRichColl");
		if (chiudi.equalsIgnoreCase("true")) {
			try {
				AccessoSemplificato as = new AccessoSemplificato(this);
				as.setSectionQueryUpdate("QUERY_CLOSE_RICHAZ");
				as.doUpdate(request, response);
				// doUpdate(request, response, "QUERY_CLOSE_RICHAZ");
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"IDOrosaCandidati::service:Chiusura richiesta collegata alla rosa", e);

			}
		}
	}

	/**
	 * 
	 */
	/*
	 * public boolean doUpdate(SourceBean request, SourceBean response, String queryName) throws Exception { String pool
	 * = getPool(); SourceBean statement = getSelectStatement(queryName);
	 * 
	 * java.lang.Boolean ret = Boolean.FALSE; ret = (Boolean)QueryExecutor.executeQuery( getRequestContainer(),
	 * getResponseContainer(), pool, statement, "UPDATE");
	 * 
	 * //ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
	 * LogUtils.logDebug("doUpdate", "", this);
	 * 
	 * response.setAttribute(UPDATE_OK, "TRUE"); } catch (Exception ex) { response.setAttribute(UPDATE_FAIL, "TRUE"); }
	 * 
	 * return ret.booleanValue(); }
	 */
	/**
	 * 
	 */

	/*
	 * protected String getPool() { return (String)getConfig().getAttribute("POOL"); }
	 */
	/**
	 * 
	 */
	/*
	 * protected SourceBean getSelectStatement(String queryName) { SourceBean beanQuery = null; beanQuery =
	 * (SourceBean)getConfig().getAttribute(queryName);
	 * 
	 * return beanQuery; }
	 */
	private boolean aggiornaDataInvio(String datInizioRosa, String prgRichAzienda, String prgRosa, String pCdnUtInsMod,
			String tutteLeRoose, String pCdnGrpUtMod, SourceBean response) throws Exception {
		DataConnection conn = null;
		StoredProcedureCommand command = null;

		try {
			// String pool = getPool();
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("SP_UPDATE_TUTTEROSE");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgRichAzienda", Types.VARCHAR, prgRichAzienda));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgRosa", Types.VARCHAR, prgRosa));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datInizioRosa", Types.VARCHAR, datInizioRosa));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("pCdnUtInsMod", Types.VARCHAR, pCdnUtInsMod));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("tutteLeRoose", Types.VARCHAR, tutteLeRoose));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("pCdnGrpUtMod", Types.VARCHAR, pCdnGrpUtMod));
			command.setAsInputParameters(paramIndex++);
			// Chiamata alla Stored Procedure
			command.execute(parameters);
			_logger.debug("IDOrosaCandidati::execStoredProc():OK");

			return true;

		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "IDOrosaCandidati.execStoredProc()",
					"");
			it.eng.sil.util.TraceWrapper.fatal(_logger, "IDOrosaCandidati:errore nella execStoredProc()", e);

			return false;
		} finally {
			Utils.releaseResources(conn, command, null);
		}

	} // execStoredProc()

} // class IDOrosaCandidati
