package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.MessageCodes; //di errore:"dati salvati corrrettamente" "..erroneamente" etc.
import it.eng.afExt.utils.ReportOperationResult; //Servono per per gestire i messaggi
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class NullaOsta extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private final String updRichGrad = "Aggiornamento Nulla Osta effettuato con successo";
	private TransactionQueryExecutor transExec;
	BigDecimal userid, nuovoPrgDocumento;
	BigDecimal prgDocumento = null;
	String numkloDocumento = "";

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;

			String codStatoAtto = StringUtils.getAttributeStrNotNull(request, "CODSTATOATTO");
			String prgConv = StringUtils.getAttributeStrNotNull(request, "PRGCONV");

			if (request.containsAttribute("inserisciDoc")) {

				BigDecimal prgNullOsta = getprgNullaOsta(request, response);
				if (prgNullOsta != null) {
					setKeyinRequest(prgNullOsta, request);
					this.setSectionQueryInsert("QUERY_INSERT_NULLA_OSTA");
					success = this.doInsert(request, response);
					if (success) {
						gestisciDocumento(request);

						if (!prgConv.equals("") && codStatoAtto.equals("PR")) {
							request.updAttribute("PRGNULLAOSTA", prgNullOsta.toString());
							success = gestisciConvenzione(request);
						}
					} else
						throw new Exception();
				} else
					throw new Exception();
			} else if (request.containsAttribute("aggiornaDoc")) {

				this.setSectionQuerySelect("QUERY_SELECT_DOC");
				SourceBean rowDoc = this.doSelect(request, response);

				if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
					prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
				}

				String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");

				if (prgDocumento != null) {
					if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
						numkloDocumento = String
								.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
						request.setAttribute("prgDocumento", prgDocumento);
						request.setAttribute("numkloDocumento", numkloDocumento);

						if (codStatoAtto.equals("AN")) {
							this.setSectionQueryUpdate("QUERY_UPDATE_DOC");
							success = this.doUpdate(request, response);
						} else {
							gestisciDocumento(request);
							if (!prgConv.equals("") && codStatoAtto.equals("PR")) {
								success = gestisciConvenzione(request);
							} else
								success = true;
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
			if (request.containsAttribute("inserisciDoc")) {
				reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");
			} else if (request.containsAttribute("aggiornaDoc")) {
				reportOperation.reportFailure(msgCode, e, "services()", "update in transazione");
			}
		}
	}

	private void gestisciDocumento(SourceBean request) throws Exception {
		Documento doc = null;

		if (request.containsAttribute("inserisciDoc")) {
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
		if (request.containsAttribute("inserisciDoc")) {
			doc.setCdnUtIns(userid);
		}
		// 03/04/2007 DOCAREA.
		ProtocolloDocumentoUtil.putInRequest(doc);
		if (request.containsAttribute("inserisciDoc")) {
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

	private BigDecimal getprgNullaOsta(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");

		SourceBean beanprgNullaOsta = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgNullaOsta.getAttribute("ROW.PRGNULLAOSTA");
	}

	private void setKeyinRequest(BigDecimal PRGNULLAOSTA, SourceBean request) throws Exception {
		if (request.getAttribute("PRGNULLAOSTA") != null) {
			request.delAttribute("PRGNULLAOSTA");
		}

		request.setAttribute("PRGNULLAOSTA", PRGNULLAOSTA);
		request.setAttribute("strChiaveTabella", PRGNULLAOSTA.toString());
	}

	private boolean gestisciConvenzione(SourceBean request) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		String pool = (String) getConfig().getAttribute("POOL");
		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";

		int paramIndex = 0;
		ArrayList parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("CM_AGG_LAV_IN_CONV");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		String p_prgNullaOsta = (String) request.getAttribute("PRGNULLAOSTA");

		parameters = new ArrayList(2);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_prgNullaOsta
		parameters.add(conn.createDataField("p_prgNullaOsta", java.sql.Types.BIGINT, new BigInteger(p_prgNullaOsta)));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);
		// Reperisco il valore di output della stored
		// 0. Codice di Ritorno
		PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();

		if (!codiceRit.equals("0"))
			return false;
		else
			return true;

	}

}
