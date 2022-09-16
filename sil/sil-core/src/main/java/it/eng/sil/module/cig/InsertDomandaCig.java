package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.User;

public class InsertDomandaCig extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertDomandaCig.class.getName());
	private final String className = StringUtils.getClassName(this);
	private String prc;

	private TransactionQueryExecutor transExec;
	BigDecimal userid;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());
		ArrayList warnings = new ArrayList();

		int idSuccess = this.disableMessageIdSuccess();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String statement = "";
		int msgCode = MessageCodes.General.OPERATION_FAIL;
		String msg = null;
		SourceBean result = null;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;
			String dataInizio = StringUtils.getAttributeStrNotNull(request, "datInizio");
			String dataFine = StringUtils.getAttributeStrNotNull(request, "datFine");
			String cdnlavoratore = StringUtils.getAttributeStrNotNull(request, "cdnlavoratore");
			String prgAzienda = StringUtils.getAttributeStrNotNull(request, "prgAzienda");
			String prgUnita = StringUtils.getAttributeStrNotNull(request, "prgUnita");
			String ggtolleranza = StringUtils.getAttributeStrNotNull(request, "GG_TOLLERANZA");
			String conferma = StringUtils.getAttributeStrNotNull(request, "conferma");

			if (conferma.equals("")) {
				statement = "select to_char(iscr.prgaltraiscr) prgaltraiscr, acc.codstatoatto, acc.codTipoConcessione, to_char(iscr.prgAccordo) prgAccordoCig, "
						+ "       iscr.codTipoIscr codtipoiscrCig, acc.codAccordo codAccordoCig "
						+ "from am_altra_iscr iscr " + "left join ci_accordo acc on (acc.prgaccordo = iscr.prgaccordo) "
						+ "where cdnlavoratore = '" + cdnlavoratore + "'" + "      and prgazienda = '" + prgAzienda
						+ "'" + "      and acc.prgaccordosucc is null " + "      and abs(iscr.datinizio-to_date('"
						+ dataInizio + "', 'DD/MM/YYYY') ) <= '" + ggtolleranza + "'"
						+ "      and abs(iscr.datfine-to_date('" + dataFine + "', 'DD/MM/YYYY')) <= '" + ggtolleranza
						+ "'";

				try {
					result = ProcessorsUtils.executeSelectQuery(statement, transExec);
				} catch (Exception e) {
					reportOperation.reportFailure(msgCode, e, "services()", "Errore generico");
				}

				if (result.containsAttribute("ROW") && conferma.equals("")) {
					String codStatoAtto = (String) result.getAttribute("ROW.codstatoatto");
					String codTipoConcessione = (String) result.getAttribute("ROW.codTipoConcessione");
					String prgAccordoCig = (String) result.getAttribute("ROW.prgAccordoCig");

					if (codStatoAtto.equals("AN")) {
						msgCode = MessageCodes.CIG.ERROR_DOMANDA_ANNULLATA;
						msg = "Esiste una domanda CIG annullata per lo stesso periodo di CIG";
					}
					if (codStatoAtto.equals("VA")) {
						if (codTipoConcessione.equals("N")) {
							msgCode = MessageCodes.CIG.ERROR_DOMANDA_NON_APPROVATA;
							msg = "Esiste una domanda CIG che non è stata approvata";
						}
						if (codTipoConcessione.equals("S") || codTipoConcessione.equals("P")) {
							msgCode = MessageCodes.CIG.ERROR_DOMANDA_COMPATIBILE;
							msg = "Esiste una domanda CIG compatibile";
							response.setAttribute("conferma", "S");
						}
					}
					response.setAttribute(result);
					reportOperation.reportFailure(msgCode);
					_logger.debug(msg);

				} else {
					/* 1. per lo stesso lavoratore non possono esserci periodi CIG che si intersecano */
					statement = "select count(*) numIscr " + "from am_altra_iscr iscr " + "where cdnlavoratore = '"
							+ cdnlavoratore + "'" + "      and ((iscr.DATINIZIO >= to_date('" + dataInizio
							+ "', 'DD/MM/YYYY') " + "      and iscr.DATINIZIO <= to_date('" + dataFine
							+ "', 'DD/MM/YYYY') ) " + "      or (iscr.DATFINE >= to_date('" + dataInizio
							+ "', 'DD/MM/YYYY') " + "      and iscr.DATFINE <= to_date('" + dataFine
							+ "', 'DD/MM/YYYY') ) " + "      or (iscr.DATINIZIO <= to_date('" + dataInizio
							+ "', 'DD/MM/YYYY') " + "      and iscr.DATFINE >= to_date('" + dataFine
							+ "', 'DD/MM/YYYY') ) ) ";
					try {
						result = ProcessorsUtils.executeSelectQuery(statement, transExec);
					} catch (Exception e) {
						it.eng.sil.util.TraceWrapper.fatal(_logger, "", e);
					}

					BigDecimal iscr = (BigDecimal) result.getAttribute("ROW.numIscr");
					BigDecimal iscrAperta = null;
					if (iscr.intValue() > 0) {
						msgCode = MessageCodes.CIG.ERROR_PERIODI_NO_COMPATIBILI;
					} else {
						/*
						 * 2. Non deve esistere più di una iscrizione aperta (stato nullo e motivo di chiusura nullo)
						 * per la stessa azienda, lo stesso lavoratore e senza riferimento alla domanda o stesso
						 * riferimento a una domanda CIG (stesso codice comunicazione accordo)
						 */

						statement = "select count(*) iscrAperta " + "from am_altra_iscr iscr "
								+ "where iscr.CODSTATO is null and iscr.CODMOTCHIUSURAISCR is null "
								+ "      and iscr.CDNLAVORATORE = '" + cdnlavoratore + "'"
								+ "      and iscr.PRGAZIENDA = '" + prgAzienda + "'"
								+ "      and (iscr.PRGACCORDO is null "
								+ "      or iscr.PRGACCORDO IN (select acc.prgaccordo "
								+ "						      from am_altra_iscr iscr2"
								+ "							  left join ci_accordo acc on (acc.prgaccordo = iscr2.prgaccordo)) )";
						try {
							result = ProcessorsUtils.executeSelectQuery(statement, transExec);
						} catch (Exception e) {
							it.eng.sil.util.TraceWrapper.fatal(_logger, "", e);
						}

						iscrAperta = (BigDecimal) result.getAttribute("ROW.iscrAperta");
						if (iscrAperta.intValue() > 1) {
							msgCode = MessageCodes.CIG.ERROR_RIFERIMENTO_DOMANDA_CIG;
							msg = "Impossibile inserire più di una iscrizione senza riferimento o con lo stesso a una domanda CIG.";
						}
					}
					if (iscr.intValue() > 0 || iscrAperta.intValue() > 1) {
						response.setAttribute(result);
						reportOperation.reportFailure(msgCode);
						_logger.debug(msg);
					} else {
						if (request.containsAttribute("inserisci")) {
							BigDecimal prgAltraIscr = getprgAltraIscr(request, response);
							if (prgAltraIscr != null) {
								setKeyinRequest(prgAltraIscr, request);
								this.setSectionQueryInsert("QUERY_INSERT_DOMANDA_CIG");
								success = this.doInsert(request, response);
							} else
								throw new Exception();
						}
					}
					if (request.containsAttribute("aggiorna")) {
						request.delAttribute("NUMKLOALTRAISCR");
						this.setSectionQuerySelect("QUERY_SELECT");
						SourceBean row = doSelect(request, response);
						String NUMKLOALTRAISCR = String
								.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLOALTRAISCR")).intValue());
						request.setAttribute("NUMKLOALTRAISCR", NUMKLOALTRAISCR);

						this.setSectionQueryUpdate("QUERY_UPDATE_ISCR_CIG");
						success = this.doUpdate(request, response);
					}

					if (success) {
						transExec.commitTransaction();
						reportOperation.reportSuccess(idSuccess);
					} else {
						transExec.rollBackTransaction();
					}
				}
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
		}
	}

	private BigDecimal getprgAltraIscr(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");

		SourceBean beanprgAltraIscr = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgAltraIscr.getAttribute("ROW.PRGALTRAISCR");
	}

	private void setKeyinRequest(BigDecimal PRGALTRAISCR, SourceBean request) throws Exception {
		if (request.getAttribute("PRGALTRAISCR") != null) {
			request.delAttribute("PRGALTRAISCR");
		}

		request.setAttribute("PRGALTRAISCR", PRGALTRAISCR);
		request.setAttribute("strChiaveTabella", PRGALTRAISCR.toString());
	}
}
