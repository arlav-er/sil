package it.eng.sil.action.report.ido;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.ido.ASCandidatiGraduatoriaUtil;
import it.eng.sil.security.User;
import it.eng.sil.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

public class ASAvviaSelezioneGraduatoria extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASAvviaSelezioneGraduatoria.class
			.getName());

	private String className = this.getClass().getName();
	private Vector vettCdnLavoratore = new Vector();
	private BigDecimal inputPrgRichiestaAz = new BigDecimal(0);
	private BigDecimal inputPrgTipoIncrocio = new BigDecimal(0);
	Vector params = null;

	public void service(SourceBean request, SourceBean response) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();

		super.service(request, response);

		getNominativiSelezionatiVector(request, response);

		boolean success = false;
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String prgTipoIncrocio = (String) request.getAttribute("prgTipoIncrocio");
			boolean isIncrocioCM = prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12");
			
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("AvviaSelezione." + tipoFile);
			} else {
				setStrNomeDoc("AvviaSelezione.pdf");
			}
			setStrDescrizione("Avvia Selezione");
			
			UtilsConfig utility = new UtilsConfig("CM_STAMP");
			String configStampeCustom = utility.getConfigurazioneDefault_Custom();
			if (configStampeCustom != null && configStampeCustom.equals("1") && isIncrocioCM) {
				setReportPath("pubb/AvviaSelezioneRER_CC.rpt");	
			}
			else {
				setReportPath("pubb/AvviaSelezione_CC.rpt");
			}
			
			// la chiave tabella è la quella della richiesta
			String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("PRGRICHIESTAAZ"));
			if (!strChiaveTabella.equals("")) {
				setStrChiavetabella(strChiaveTabella);
			}
			
			BigDecimal prgAzienda = new BigDecimal((String) request.getAttribute("prgAzienda"));
			BigDecimal prgUnita = new BigDecimal((String) request.getAttribute("prgUnita"));
			
			if (isIncrocioCM) {
				setPagina("CMGestGraduatoriePage");
			} else {
				setPagina("ASGestGraduatoriePage");
			}

			setPrgAzienda(prgAzienda);
			setPrgUnita(prgUnita);

			String descrizione = (String) request.getAttribute("ConcatenaCpi");
			/*
			 * if (descrizione != null && !descrizione.equals("")) {
			 * setStrEnteRilascio(descrizione); }
			 */

			String codCpi = (String) request.getAttribute("CodCPI");
			if (codCpi != null && !codCpi.equals("")) {
				setCodCpi(codCpi);
			}

			BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
			String annoProt = (String) request.getAttribute("annoProt");
			String dataProtocollo = (String) request.getAttribute("dataOraProt");
			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			TransactionQueryExecutor txExec = null;
			try {
				AccessoSemplificato _db = new AccessoSemplificato(this);
				// per recuperare le informazioni utilizzate per la generazione
				// del report non e' necessario
				// l'utilizzo della transazione. Se necessaria nei passi
				// successivi verra' abilitata.
				// 16/02/2007 Savino: utilizzo dove possibile la
				// AccessoSemplificato
				// _db.enableSimpleQuery();
				txExec = new TransactionQueryExecutor(_db.getPool());
				txExec.initTransaction();
				_db.enableTransactions(txExec);

				// recupero la descrizione del cpi
				/*
				 * 16/02/2007 Savino: sostituito da _db.doSelect() SourceBean
				 * statement =
				 * (SourceBean)getConfig().getAttribute("GET_DESCRCPI_STAMPA");
				 */
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_DESCRCPI_STAMPA");
				beanRows = _db.doSelect(request, response, false);
				// 16/02/2007 Savino: sostituito da _db.doSelect()
				// beanRows =
				// (SourceBean)txExec.executeQuery(getRequestContainer(),
				// getResponseContainer(), statement, "SELECT");

				String strDescrizione = StringUtils.getAttributeStrNotNull(beanRows, "ROW.STRDESCRIZIONE");
				if (strDescrizione != null && !strDescrizione.equals("")) {
					setStrEnteRilascio(strDescrizione);
				}

				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					// gestione codStatoAtto del documento collegato
					if (request.containsAttribute("dataOraProt")) {
						request.updAttribute("codStatoAtto", "PR");
						getDocumento().setCodStatoAtto("PR");
					}

					// dopo aver recuperato le info per generare il report
					// abilito la transazione
					// per l'inserimento del documento e per le eventuali
					// operazioni successive

					Object p_numCandidati = request.getAttribute("NUM_CANDIDATI");
					if (p_numCandidati == null || ("").equals(p_numCandidati)) {
						// 1)XXX chiamo la stored procedure che mi gestisce
						// l'avvimaneto
						success = avviaSelezioneAS(request, response, txExec);
					} else {
						success = avviaPrimiNCandidati(request, response, txExec);
					}

					params = new Vector(3);

					// 16/02/2007 Savino: aggiunti i parametri della
					// protocollazione. Li passo (tutti) per nome e non per
					// posizione
					/*
					 * if (prgTipoIncrocio != null &&
					 * !prgTipoIncrocio.equals("")) {
					 * params.add(request.getAttribute("prgTipoIncrocio")); }
					 * else{ params.add(""); }
					 */
					String prgRichiestaAz = (String) request.getAttribute("prgRichiestaAz");
					/*
					 * if (prgRichiestaAz != null && !prgRichiestaAz.equals("")) {
					 * params.add(request.getAttribute("prgRichiestaAz")); }
					 * else { params.add(""); }
					 */
					String prgAvvio = ((BigDecimal) request.getAttribute("PRGAVVIO")).toString();
					/*
					 * params.add(request.getAttribute("PRGAVVIO").toString());
					 */
					/*
					 * setParams(params);
					 */

					if (!success) {
						throw new Exception("stampa dell'avviamento fallita");
					} else {
						// 2) gestisco la protocollazione
						// 16/02/2007 Savino: aggiunti i parametri della
						// protocollazione. Li passo (tutti) per nome e non per
						// posizione
						SourceBean beanRegione = (SourceBean) txExec.executeQuery("GET_CODREGIONE", null, TransactionQueryExecutor.SELECT);
						String regione = (String) beanRegione.getAttribute("ROW.CODREGIONE");
												
						Map prompts = new HashMap();
						prompts.put("prgTipoIncrocio", prgTipoIncrocio);
						prompts.put("prgRichestaAz", prgRichiestaAz);
						prompts.put("avviaSel", prgAvvio);
						prompts.put("regione", regione);
						
						if (configStampeCustom != null && configStampeCustom.equals("1") && isIncrocioCM) {		
							// per la gestione del collocamento mirato
							// 0 config. default - 1 config. custom (RER)
							SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
							User user = (User) sessione.getAttribute(User.USERID);
							String cpi = user.getCodRif();
							if (cpi != null &&  !cpi.equals("")) {
								prompts.put("codCpiUser", cpi);           
							}			
							else{
								prompts.put("codCpiUser", "-1");             
							}
							if (configStampeCustom != null &&  !configStampeCustom.equals("")) {
								prompts.put("configStampeCustom", configStampeCustom);
							}			
							else{
								prompts.put("configStampeCustom", "0");           
							}
						}
												
						// solo se e' richiesta la protocollazione i parametri
						// vengono inseriti nella Map
						addPromptFieldsProtocollazione(prompts, request);
						// ora si chiede di usare il passaggio dei parametri per
						// nome e
						// non per posizione (col vettore, passaggio di default)
						setPromptFields(prompts);
						if (insertDocument(request, response, txExec)) {
							if ((numProt != null) && annoProt != null) {
								// 16/02/2007 Savino: recupero il numero di
								// protocollo reale associato al documento
								numProt = this.getDocumento().getNumProtInserito();

								request.setAttribute("NUMPROTOCOLLO", numProt);

								// 3) aggiorno nei candidati avviati il numero
								// di protocollo
								// 16/02/2007 Savino: sostituito da
								// _db.doSelect()
								// boolean check =
								// updateInfoProtocolloAvviamento(request,
								// response, txExec);
								_db.setSectionQueryUpdate("UPDATE_DOC_AVVIO");
								boolean check = _db.doUpdate(request, response);
								if (!check) {
									throw new Exception("stampa dell'avviamento fallita");
								}
							}
						} else {
							throw new Exception("stampa dell'avviamento fallita");
						}
						txExec.commitTransaction();

						String numRichiesta = (String) request.getAttribute("prgRichiestaAz");
						// possibili chiavi che identificano i nominativi
						// selezionati
						String key1 = numRichiesta + "_5";
						String key2 = numRichiesta + "_6";
						String key3 = numRichiesta + "_7";
						String key4 = numRichiesta + "_8";
						// per la graduatoria CM
						String key5 = numRichiesta + "_10";
						String key6 = numRichiesta + "_11";
						String key7 = numRichiesta + "_12";

						// elimino dalla sessione il SB dei candidati
						// selezionati
						session.delAttribute(key1);
						session.delAttribute(key2);
						session.delAttribute(key3);
						session.delAttribute(key4);
						session.delAttribute(key5);
						session.delAttribute(key6);
						session.delAttribute(key7);
					}

				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				}

			} catch (Exception e) {
				if (txExec != null)
					try {
						txExec.rollBackTransaction();
					} catch (EMFInternalError e1) {
						it.eng.sil.util.TraceWrapper.fatal(_logger,
								"Impossibile eseguire la rollBack nella transazione della stampa dell'avviamento",
								(Exception) e1);

					}
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa dell'avviamento", e);

				setOperationFail(request, response, e);
			}
		}
	}

	/**
	 * update del protocollo sull'avviamento
	 */
	/*
	 * 16/02/2007 Savino: medoto sostituito da una chiamata a _db.doUpdate()
	 * private boolean updateInfoProtocolloAvviamento(SourceBean
	 * request,SourceBean response, TransactionQueryExecutor txExecutor) throws
	 * SourceBeanException, EMFInternalError {
	 * 
	 * String pool = (String)getConfig().getAttribute("POOL"); SourceBean
	 * statement = (SourceBean)getConfig().getAttribute("UPDATE_DOC_AVVIO");
	 * 
	 * Boolean beanRows = null; beanRows =
	 * (Boolean)txExecutor.executeQuery(getRequestContainer(),
	 * getResponseContainer(), statement, "UPDATE"); try {
	 * response.setAttribute("UPDATE_OK", "TRUE"); } catch (Exception ex) {
	 * response.setAttribute("UPDATE_FAIL", "TRUE"); }
	 * 
	 * return beanRows.booleanValue();
	 *  }
	 */
	/*
	 * richiama la stored Procedure passandogli come parametro l'Array di tutti
	 * i cdnLavoratori selezionati
	 */
	private boolean avviaSelezioneAS(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor) {

		DataConnection dataConnection = null;
		oracle.jdbc.OracleConnection oracleConn = null;
		try {

			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String p_cdnUtente = String.valueOf(user.getCodut());

			dataConnection = txExecutor.getDataConnection();

			java.sql.Connection connection = dataConnection.getInternalConnection();
			oracleConn = it.eng.sil.util.Utils.getOracleConnection(connection);
			
			ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);

			Object nominativi[] = vettCdnLavoratore.toArray();

			ARRAY arrayCdnLavoratore = new ARRAY(descriptor, oracleConn, nominativi);

			/*
			 * OraclePreparedStatement ps = (OraclePreparedStatement)
			 * oracleConn.prepareStatement( "begin
			 * PG_INCROCIO.ASInsertCandidatiGraduatoria(?, ?); end;");
			 */

			OracleCallableStatement cs = (OracleCallableStatement) oracleConn
					.prepareCall("begin ? := PG_INCROCIO.ASInsertCandidatiGraduatoria(?, ?, ?, ?, ?); end;");

			cs.registerOutParameter(1, java.sql.Types.BIGINT);
			cs.setARRAY(2, arrayCdnLavoratore);
			cs.setBigDecimal(3, inputPrgRichiestaAz);
			cs.setBigDecimal(4, inputPrgTipoIncrocio);
			cs.setBigDecimal(5, new BigDecimal(p_cdnUtente));
			cs.registerOutParameter(6, java.sql.Types.BIGINT);

			cs.execute();

			BigDecimal returnValue = ((OracleCallableStatement) cs).getBigDecimal(1);
			BigDecimal p_prgAvvio = ((OracleCallableStatement) cs).getBigDecimal(6);

			request.setAttribute("PRGAVVIO", p_prgAvvio);

			if (returnValue.compareTo(new BigDecimal(0)) != 0) {
				int msgCode = 0;
				String msg = null;
				switch (returnValue.intValue()) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "";
					//
					break;
				case 1: // errore data pubblicazione
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: la data di pubbllicazione è anteriore a 6 mesi";
					//
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "";
					//
				}

				return false;
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			return false;
		} finally {
			// Chiudo tutto
			// com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection,
			// null, null);
			return true;
		}
	}

	public boolean avviaPrimiNCandidati(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = txExecutor.getDataConnection();
			;

			statementSB = (SourceBean) getConfig().getAttribute("AS_AVVIA_PRIMI_N_CANDIDATI");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_numCandidati = StringUtils.getAttributeStrNotNull(request, "NUM_CANDIDATI");
			String p_prgTipoIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");
			String p_prgRichiesta = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(7);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_numCandidati
			parameters.add(conn
					.createDataField("p_numCandidati", java.sql.Types.BIGINT, new BigInteger(p_numCandidati)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgRichiesta
			parameters.add(conn
					.createDataField("p_prgRichiesta", java.sql.Types.BIGINT, new BigInteger(p_prgRichiesta)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgIncrocio
			parameters.add(conn.createDataField("p_prgTipoIncrocio", java.sql.Types.BIGINT, new BigInteger(
					p_prgTipoIncrocio)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Outputù
			// 6. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_errCode
			parameters.add(conn.createDataField("p_prgAvvio", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// 2. prgAvvio
			PunctualDataResult pdr2 = (PunctualDataResult) outputParams.get(2);
			DataField df2 = pdr2.getPunctualDatafield();
			BigDecimal p_prgAvvio = new BigDecimal(df2.getStringValue());

			request.setAttribute("PRGAVVIO", p_prgAvvio);

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: sqlCode=" + errCode;
				//	
				case 1: // errore data pubblicazione
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: la data di pubblicazione è anteriore a 6 mesi";
				//	
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: errore di ritorno non ammesso. SqlCode=" + errCode;
				//	
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

				return false;
			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				response.setAttribute((SourceBean) row);
			}
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
			return false;
		} finally {
			// com.engiweb.framework.dbaccess.Utils.releaseResources(conn,
			// command, dr);
			return true;
		}

	}

	/*
	 * metodo che si occupa di inserire nel SuorceBean in sessione i candidati
	 * checkati presenti nella ServiceRequest vengono eliminati da SourceBean
	 * quei candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE inoltre viene popolato il vettore vettCdnLavoratore
	 * con tutti i cdnLavoratore presenti in sessione
	 * 
	 */
	public void getNominativiSelezionati(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		SourceBean nominativiSB = (SourceBean) sessionContainer.getAttribute(keySB);
		if (nominativiSB == null) {
			try {
				nominativiSB = new SourceBean("CANDIDATI_SEL");
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					String ckSel = (String) request.getAttribute(keyAttr);

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un sourcebean
						ASCandidatiGraduatoriaUtil.insertNominativoSB(valueCdnLavoratore, nominativiSB);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		SourceBean newNominativiSB = null;
		try {
			newNominativiSB = new SourceBean("CANDIDATI_SEL");
			Vector newRows = ASCandidatiGraduatoriaUtil.deleteNominativoSB(request, nominativiSB);
			for (int i = 0; i < newRows.size(); i++) {
				SourceBean newRow = (SourceBean) newRows.get(i);
				newNominativiSB.setAttribute(newRow);
			}
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setta il SB in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiSB);

		inputPrgRichiestaAz = new BigDecimal(numRichiesta);

		// carico il vettore con i nominativi inseriti nel sourcebean caricsato
		// in session
		Vector vectRows = newNominativiSB.getAttributeAsVector("ROW");

		for (int j = 0; j < vectRows.size(); j++) {
			String prgNom = (String) ((SourceBean) vectRows.get(j)).getAttribute("CDNLAVORATORE");
			vettCdnLavoratore.add(j, prgNom);
		}
	}

	/*
	 * metodo che si occupa di inserire nel vettore in sessione i candidati
	 * checkati presenti nella ServiceRequest vengono eliminati dal Vector quei
	 * candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE inoltre viene popolato il vettore vettCdnLavoratore
	 * con tutti i cdnLavoratore presenti in sessione
	 * 
	 */
	public void getNominativiSelezionatiVector(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		Vector nominativiVector = (Vector) sessionContainer.getAttribute(keySB);
		if (nominativiVector == null) {
			nominativiVector = new Vector();
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					String ckSel = (String) request.getAttribute(keyAttr);

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un sourcebean
						ASCandidatiGraduatoriaUtil.insertNominativoVector(valueCdnLavoratore, nominativiVector);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		Vector newNominativiVector = null;
		newNominativiVector = ASCandidatiGraduatoriaUtil.deleteNominativoVector(request, nominativiVector);

		// setta il SB in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiVector);

		inputPrgRichiestaAz = new BigDecimal(numRichiesta);
		inputPrgTipoIncrocio = new BigDecimal(prgTipoIncrocio);

		// carico il vettore con i nominativi inseriti nel sourcebean caricsato
		// in session
		vettCdnLavoratore = newNominativiVector;

	}
}
