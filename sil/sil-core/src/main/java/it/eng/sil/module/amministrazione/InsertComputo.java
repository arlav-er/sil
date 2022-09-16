package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class InsertComputo extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private final String gestisciComputo = "Aggiornamento Computo effettuato con successo";
	private TransactionQueryExecutor transExec;
	BigDecimal userid, prgDocumento;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Configurazione
		SourceBean sb_provvedimento = Utils.getConfigValue("CMCOMP");
		String s_conf_provvedimento = (String) sb_provvedimento.getAttribute("row.num");

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;
			String codStatoAtto = (String) request.getAttribute("CODSTATOATTO");

			if (request.containsAttribute("inserisciNew")) {
				BigDecimal PRGRICHCOMPUTO = getPRGRICHCOMPUTO(request, response);

				if (PRGRICHCOMPUTO != null) {
					setKeyinRequest(PRGRICHCOMPUTO, request);
					this.setSectionQueryInsert("QUERY_INSERT_RICH_COMPUTO");

					success = this.doInsert(request, response);
					if (success) {
						gestisciDocumento(request);

						if (codStatoAtto.equals("PR") && "0".equals(s_conf_provvedimento)) {
							request.updAttribute("PRGRICHCOMPUTO", PRGRICHCOMPUTO.toString());
							success = gestisciComputo(request, reportOperation);
						}

					} else
						throw new Exception();
				} else
					throw new Exception();
			}

			else if (request.containsAttribute("aggiornaDoc")) {

				this.setSectionQuerySelect("QUERY_SELECT_DOC");
				SourceBean rowDoc = this.doSelect(request, response);

				if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
					prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");

				}

				String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");

				if (prgDocumento != null) {
					if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
						String numkloDocumento = String
								.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
						request.setAttribute("prgDocumento", prgDocumento);
						request.setAttribute("numkloDocumento", numkloDocumento);

						if (codStatoAtto.equals("AN")) {
							this.setSectionQueryUpdate("QUERY_UPDATE_DOC");
							success = this.doUpdate(request, response);
						} else {
							gestisciDocumento(request);
							success = true;
							if (codStatoAtto.equals("PR") && "0".equals(s_conf_provvedimento)) {
								success = gestisciComputo(request, reportOperation);
							}

						}
					}
				} else
					throw new Exception();
			}

			if (success) {
				transExec.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception e) {
			transExec.rollBackTransaction();
			if (request.containsAttribute("inserisciNew")) {
				reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");
			} else if (request.containsAttribute("aggiornaDoc")) {
				reportOperation.reportFailure(msgCode, e, "services()", "update in transazione");
			}
		}
	}

	private void gestisciDocumento(SourceBean request) throws Exception {
		Documento doc = null;

		if (request.containsAttribute("inserisciNew")) {
			doc = new Documento();
			doc.setPrgDocumento(null);
		} else if (request.containsAttribute("aggiornaDoc")) {
			doc = new Documento(prgDocumento);
			doc.setPrgDocumento(prgDocumento);
			doc.setNumKloDocumento(getAttributeAsBigDecimal(request, "numkloDocumento"));
		}

		doc.setCodCpi(getAttributeAsString(request, "codCpi"));
		doc.setCdnLavoratore(getAttributeAsBigDecimal(request, "cdnLavoratore"));
		doc.setPrgAzienda(getAttributeAsBigDecimal(request, "prgAzienda"));
		doc.setPrgUnita(getAttributeAsBigDecimal(request, "prgUnita"));
		doc.setCodTipoDocumento(getAttributeAsString(request, "codTipoDocumento"));
		doc.setFlgAutocertificazione(getAttributeAsString(request, "flgAutocertificazione"));
		doc.setStrDescrizione(getAttributeAsString(request, "strDescrizione"));
		doc.setFlgDocAmm(getAttributeAsString(request, "flgDocAmm"));
		doc.setFlgDocIdentifP(getAttributeAsString(request, "flgDocIdentifP"));
		doc.setDatInizio(getAttributeAsString(request, "DatInizio"));
		doc.setStrNumDoc(getAttributeAsString(request, "StrNumDoc"));
		doc.setStrEnteRilascio(getAttributeAsString(request, "StrEnteRilascio"));
		doc.setCodMonoIO(getAttributeAsString(request, "FlgCodMonoIO"));
		doc.setDatAcqril(getAttributeAsString(request, "DatAcqril"));
		doc.setCodModalitaAcqril(getAttributeAsString(request, "codModalitaAcqril"));
		doc.setCodTipoFile(getAttributeAsString(request, "codTipoFile"));
		doc.setStrNomeDoc(getAttributeAsString(request, "strNomeDoc"));
		doc.setDatFine(getAttributeAsString(request, "dataFine"));
		doc.setNumAnnoProt(getAttributeAsBigDecimal(request, "numAnnoProt"));
		doc.setNumProtocollo(getAttributeAsBigDecimal(request, "numProtocollo"));
		doc.setDatProtocollazione(getAttributeAsString(request, "dataOraProt"));
		doc.setTipoProt(getAttributeAsString(request, "tipoProt"));
		doc.setStrNote(getAttributeAsString(request, "strNote"));
		doc.setNumKeyLock(getAttributeAsBigDecimal(request, "KLOCKPROT"));
		doc.setChiaveTabella(getAttributeAsString(request, "strChiaveTabella"));
		doc.setCodMotAnnullamentoAtto(getAttributeAsString(request, "codMotAnnullamentoAtto"));
		doc.setCodStatoAtto(getAttributeAsString(request, "CODSTATOATTO"));

		String pagina = getAttributeAsString(request, "PAGE");
		if (!pagina.equalsIgnoreCase("null"))
			doc.setPagina(pagina);
		doc.setCdnUtMod(userid);
		if (request.containsAttribute("inserisciNew")) {
			doc.setCdnUtIns(userid);
		}
		// 03/04/2007 DOCAREA.
		ProtocolloDocumentoUtil.putInRequest(doc);
		if (request.containsAttribute("inserisciNew")) {
			doc.insert(transExec);
		} else if (request.containsAttribute("aggiornaDoc")) {
			doc.update(transExec);
		}
	}

	private String getAttributeAsString(SourceBean request, String param) {
		return SourceBeanUtils.getAttrStrNotNull(request, param);
	}

	private BigDecimal getAttributeAsBigDecimal(SourceBean request, String param) {
		String tmp = SourceBeanUtils.getAttrStrNotNull(request, param);
		if (!tmp.equals("")) {
			return new BigDecimal(tmp);
		}
		return null;
	}

	private BigDecimal getPRGRICHCOMPUTO(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");

		SourceBean beanprgRichGrad = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgRichGrad.getAttribute("ROW.do_nextval");
	}

	private void setKeyinRequest(BigDecimal PRGRICHCOMPUTO, SourceBean request) throws Exception {
		if (request.getAttribute("PRGRICHCOMPUTO") != null) {
			request.delAttribute("PRGRICHCOMPUTO");
		}
		if (request.getAttribute("strChiaveTabella") != null) {
			request.delAttribute("strChiaveTabella");
		}

		request.updAttribute("PRGRICHCOMPUTO", PRGRICHCOMPUTO);
		request.updAttribute("strChiaveTabella", PRGRICHCOMPUTO.toString());
	}

	/**
	 * Automatismo per l'aggiornamento dei lavoratori L68 del prospetto associato al computo (ultimo prospetto in corso
	 * d'anno per la stessa azienda del computo)
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private boolean gestisciComputo(SourceBean request, ReportOperationResult reportOperation) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		String pool = (String) getConfig().getAttribute("POOL");
		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";

		int paramIndex = 0;
		ArrayList parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("CM_AGG_LAV_DA_COMPUTO");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		String p_prgRichComputo = (String) request.getAttribute("PRGRICHCOMPUTO");
		String p_cdnUtente = String.valueOf(user.getCodut());

		String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");

		parameters = new ArrayList(5);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_prgNullaOsta
		parameters
				.add(conn.createDataField("p_prgRichComputo", java.sql.Types.BIGINT, new BigInteger(p_prgRichComputo)));
		command.setAsInputParameters(paramIndex++);
		// 3. p_cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
		command.setAsInputParameters(paramIndex++);
		// 4. p_key
		parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
		command.setAsInputParameters(paramIndex++);
		// 5.out_annoProspetto
		parameters.add(conn.createDataField("out_annoProsp", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);
		// Reperisco il valore di output della stored
		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();

		// 1. p_out_annoProspetto
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		String outAnnoProspetto = df.getStringValue();
		if (outAnnoProspetto == null) {
			outAnnoProspetto = "";
		}
		Vector paramErrore = new Vector();
		paramErrore.add(outAnnoProspetto);

		if (("-1").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGGIORNAMENTO_L68_PROSPETTO, className,
					"gestione aggiornamento prospetto da computo");
			return true;
		} else if (("-2").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGG_L68_PROSPETTO_MOV_INESISTENTE,
					className, "gestione aggiornamento prospetto da computo");
			return true;
		} else if (("-3").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGGIORNAMENTO_L68_PROSPETTO, className,
					"gestione aggiornamento prospetto da computo");
			return true;
		} else if (("-4").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGG_L68_PROSPETTO_INESISTENTE,
					className, "gestione aggiornamento prospetto da computo");
			return true;
		} else {
			// viene aggiunto il messaggio di avvenuto aggiornamento prospetto
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.WARNING_L68_PROSPETTO_AGGIORNATO, className,
					"gestione aggiornamento prospetto da computo", paramErrore);
			return true;
		}

	}

}
