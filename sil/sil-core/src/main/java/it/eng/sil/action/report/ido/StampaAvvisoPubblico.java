/*
 * Creato il 22-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report.ido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

/**
 * @author coppola
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;

public class StampaAvvisoPubblico extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaAvvisoPubblico.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		Map prompts = new HashMap();
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			String modPubblStr = (String) request.getAttribute("modPubblicazione");
			if (tipoFile != null) {
				if (modPubblStr.equals("1")) {
					setStrNomeDoc("AvvisoPubblicoWeb." + tipoFile);
				} else {
					setStrNomeDoc("AvvisoPubblicoBacheca." + tipoFile);
				}
			} else {
				if (modPubblStr.equals("1")) {
					setStrNomeDoc("AvvisoPubblicoWeb.pdf");
				} else {
					setStrNomeDoc("AvvisoPubblicoBacheca.pdf");
				}

			}

			String regione = "";
			try {
				SourceBean righe = (SourceBean) QueryExecutor.executeQuery("GET_INFO_TARGA_PROVINCIA_REGIONE", null,
						"SELECT", Values.DB_SIL_DATI);
				regione = (String) righe.getAttribute("ROW.CODREGIONE");
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, "StampaAvvisoPubblico:service():errore AccessoSemplificato",
						e);
			}

			// modifica richiesta da Umbria
			String reportPath = "";
			boolean isArt16Umb = false;
			if (request.containsAttribute("ACTION_NAME") && request.getAttribute("ACTION_NAME") != null
					&& request.getAttribute("ACTION_NAME").toString().equalsIgnoreCase("RPT_AVVISO_PUBBLICO_ART16")) {
				String prgRichiestaAz = (String) request.getAttribute("prgRichiestaAz");
				String tipoAvvPubb = (String) request.getAttribute("tipoAvvPubb");
				prompts.put("prgRichiestaAz", prgRichiestaAz);
				prompts.put("tipoAvvPubb", tipoAvvPubb);
				prompts.put("regione", regione);
				reportPath = "pubb/AvvisoPubblicoArt16_CC.rpt";
				isArt16Umb = true;
			} else {
				reportPath = "pubb/AvvisoPubblico_CC.rpt";
			}

			setStrDescrizione("Stampa Avviso Pubblico");
			setReportPath(reportPath);

			if (!isArt16Umb) {
				String descrizione = (String) request.getAttribute("descrizione");
				// descrizione = StringUtils.split(descrizione,"-");

				Vector desc = new Vector();
				String descrCpi = "";

				desc = it.eng.afExt.utils.StringUtils.split(descrizione, "-");

				for (int k = 0; k < 2; k++) {
					descrCpi = desc.get(0).toString();
				}

				setStrEnteRilascio(descrCpi);
				String codCpi = (String) request.getAttribute("CodCPI");

				String dataChiamata = (String) request.getAttribute("chiamata");
				setDatAcqril(dataChiamata);
				String tipoAvvPubb = (String) request.getAttribute("tipoAvvPubb");

				prompts.put("codCpi", codCpi);
				prompts.put("chiamata", dataChiamata);
				prompts.put("modPubblStr", modPubblStr);
				prompts.put("tipoAvvPubb", tipoAvvPubb);
				prompts.put("regione", regione);
			}

			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				return;
			}
			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			// gestione transazione per il salvataggio del documento
			String annoProt = (String) request.getAttribute("annoProt");
			TransactionQueryExecutor txExec = null;
			try {
				AccessoSemplificato _db = new AccessoSemplificato(this);
				_db.enableSimpleQuery();
				txExec = new TransactionQueryExecutor(_db.getPool());
				txExec.initTransaction();
				_db.enableTransactions(txExec);

				if (salva != null && salva.equalsIgnoreCase("true")) {
					// 1) salvo/protocollo documento
					boolean checkIns = insertDocument(request, response, txExec);
					if (checkIns) {
						BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
						if ((numProt != null) && annoProt != null) {

							// 2) salvo il riferimento del documento per le
							// richieste trovate - tabella AS_REL_RICH_DOC
							boolean check = insertInfoDocumentoRichiesta(request, response, txExec);

							if (!check) {
								throw new Exception("stampa avviso pubblico fallita");
							}
						}
					} else {
						throw new Exception("stampa dell'avviamento fallita");
					}
					txExec.commitTransaction();
				} else if (apri != null && apri.equalsIgnoreCase("true")) {
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

	public boolean insertInfoDocumentoRichiesta(SourceBean request, SourceBean response,
			TransactionQueryExecutor txExecutor) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		boolean returnValue = true;

		try {
			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = txExecutor.getDataConnection();
			;

			statementSB = (SourceBean) getConfig().getAttribute("AS_INS_REL_DOC_RICH");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			Documento theDocument = (Documento) response.getAttribute("theDocument");
			BigDecimal p_prgDocumento = theDocument.getPrgDocumento();

			String p_dataChiamata = StringUtils.getAttributeStrNotNull(request, "chiamata");
			String p_CodCPI = StringUtils.getAttributeStrNotNull(request, "CodCPI");
			String p_modPubblicazione = StringUtils.getAttributeStrNotNull(request, "modPubblicazione");

			parameters = new ArrayList(6);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. numProt
			parameters.add(conn.createDataField("p_prgDocumento", java.sql.Types.BIGINT, p_prgDocumento));
			command.setAsInputParameters(paramIndex++);
			// 3. p_dataChiamata
			parameters.add(conn.createDataField("p_dataChiamata", java.sql.Types.VARCHAR, p_dataChiamata));
			command.setAsInputParameters(paramIndex++);
			// 4. p_CodCPI
			parameters.add(conn.createDataField("p_CodCPI", java.sql.Types.VARCHAR, p_CodCPI));
			command.setAsInputParameters(paramIndex++);
			// 5. p_modPubblicazione
			parameters.add(conn.createDataField("p_modPubblicazione", java.sql.Types.VARCHAR, p_modPubblicazione));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 6. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
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

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0, debugLevel = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Stampa avviso pubblico: sqlCode=" + errCode;
					// debugLevel = TracerSingleton.CRITICAL;

				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Stampa avviso pubblico: errore di ritorno non ammesso. SqlCode=" + errCode;
					// debugLevel = TracerSingleton.CRITICAL;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

				returnValue = false;
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
			returnValue = false;
		} finally {
			return returnValue;
		}
	}
}
