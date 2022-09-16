package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.util.Vector;

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

public class SaveIscrCIG extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertDomandaCig.class.getName());
	private final String className = StringUtils.getClassName(this);
	private String prc;

	private TransactionQueryExecutor transExec;
	BigDecimal userid;
	private ReportOperationResult reportOperation;
	private int msgCode;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();

		reportOperation = new ReportOperationResult(this, response);
		String statement = "";
		msgCode = MessageCodes.General.OPERATION_FAIL;
		String msg = null;
		SourceBean result = null;
		boolean statoCigCompatibileVAConcessa = false;
		int msgCodeCigCompatibileVAConcessa = MessageCodes.General.OPERATION_FAIL;
		String msgCigCompatibileVAConcessa = null;

		boolean statoCigCompatibileVANonConcessa = false;
		int msgCodeCigCompatibileVANonConcessa = MessageCodes.General.OPERATION_FAIL;
		String msgCigCompatibileVANonConcessa = null;

		boolean statoCigCompatibile_AN = false;
		int msgCodeCigCompatibile_AN = MessageCodes.General.OPERATION_FAIL;
		String msgCigCompatibile_AN = null;

		SourceBean resultCompatibiliVAConcessa = new SourceBean("ROWS");
		SourceBean resultCompatibiliVANonConcessa_AN = new SourceBean("ROWS");

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;
			String dataInizio = StringUtils.getAttributeStrNotNull(request, "datInizio");
			String dataFine = StringUtils.getAttributeStrNotNull(request, "datFine");
			String cdnlavoratore = StringUtils.getAttributeStrNotNull(request, "cdnlavoratore");
			String prgAzienda = StringUtils.getAttributeStrNotNull(request, "prgAzienda");
			String ggtolleranza = StringUtils.getAttributeStrNotNull(request, "GG_TOLLERANZA");
			String prgAIscr = StringUtils.getAttributeStrNotNull(request, "prgAltraIscr");
			String conferma = StringUtils.getAttributeStrNotNull(request, "conferma");
			String codStato = StringUtils.getAttributeStrNotNull(request, "codStato");
			String codMotChiusuraIscr = StringUtils.getAttributeStrNotNull(request, "codMotChiusuraIscr");
			String prgAccordo = StringUtils.getAttributeStrNotNull(request, "prgAccordo");
			String configurazione = StringUtils.getAttributeStrNotNull(request, "configurazione");

			// gestione configurazione val d'aosta: in questo caso ho un vettore di codtipoiscr.
			// Vedere DomandeCig_Main.jsp
			Vector<String> codTipoIscrVect = new Vector<String>();
			String codTipologiaIscr = "";
			if ("2".equals(configurazione)) {
				codTipoIscrVect.addAll(request.getAttributeAsVector("codtipoiscr"));
			} else {
				codTipologiaIscr = StringUtils.getAttributeStrNotNull(request, "codtipoiscr");
			}

			if (conferma.equals("")) {
				if (request.containsAttribute("inserisci") || prgAccordo.equalsIgnoreCase("")) {
					statement = "select distinct acc.codstatoatto, nvl(acc.codTipoConcessione,'I') as codTipoConcessione, to_char(acc.prgAccordo) prgAccordoCig, "
							+ "		acc.codtipoaccordo codtipoiscrCig, acc.codAccordo codAccordoCig, de_tipo_accordo.strdescrizione strdescrizioneCig, "
							+ " to_char(lav_acc.datiniziocigs, 'DD/MM/YYYY') datiniziocigs, to_char(lav_acc.datfinecigs, 'DD/MM/YYYY') datfinecigs "
							+ "from ci_accordo acc "
							+ "inner join ci_lavoratore lav on (lav.prgaccordo = acc.prgaccordo) "
							+ "inner join ci_lav_accordo lav_acc on (lav_acc.prglavoratore = lav.prglavoratore) "
							+ "inner join ci_rel_accordo_unit_az uni on (uni.prgaccordo = acc.prgaccordo) "
							+ " inner join de_tipo_accordo on (de_tipo_accordo.codtipoaccordo = acc.codtipoaccordo) "
							+ "where lav.cdnlavoratore = " + cdnlavoratore +
							// " and uni.prgazienda = " + prgAzienda +
							"      and acc.prgaccordosucc is null "
							+ "      and abs(trunc(lav_acc.datiniziocigs)-to_date('" + dataInizio
							+ "', 'DD/MM/YYYY')) <= " + ggtolleranza
							+ "      and abs(trunc(lav_acc.datfinecigs)-to_date('" + dataFine + "', 'DD/MM/YYYY')) <= "
							+ ggtolleranza;

					if (!"".equalsIgnoreCase(prgAzienda)) {
						statement = statement + " and uni.prgazienda = " + prgAzienda;
					}

					// gestione configurazione val d'aosta
					if ("2".equals(configurazione)) {

						boolean tipoIscrM = false;
						for (String codTipoIscr : codTipoIscrVect) {
							if (codTipoIscr.equals("M")) {
								tipoIscrM = true;
								break;
							}
						}

						if (tipoIscrM) {
							statement = statement + " and acc.codTipoAccordo = 'M'";
						} else {
							statement = statement + " and acc.codTipoAccordo <> 'M'";
						}

					} else {
						if (codTipologiaIscr.equalsIgnoreCase("M")) {
							statement = statement + " and acc.codTipoAccordo = 'M'";
						} else {
							statement = statement + " and acc.codTipoAccordo <> 'M'";
						}
					}

					result = ProcessorsUtils.executeSelectQuery(statement, transExec);
					Vector vettCigCompatibile = result.getAttributeAsVector("ROW");
					int sizeCigComp = vettCigCompatibile.size();
					for (int i = 0; i < sizeCigComp; i++) {
						SourceBean resultCurr = (SourceBean) vettCigCompatibile.get(i);
						String codStatoAtto = (String) resultCurr.getAttribute("codstatoatto");
						String codTipoConcessione = (String) resultCurr.getAttribute("codTipoConcessione");
						if (codStatoAtto != null && !codStatoAtto.equalsIgnoreCase("")) {
							// Controllo per stato Valido
							if (codStatoAtto.equals("VA")) {
								if (codTipoConcessione != null && !codTipoConcessione.equals("")) {
									if (!codTipoConcessione.equals("N")) {
										statoCigCompatibileVAConcessa = true;
										msgCodeCigCompatibileVAConcessa = MessageCodes.CIG.ERROR_DOMANDA_COMPATIBILE;
										msgCigCompatibileVAConcessa = "Esiste una domanda CIG compatibile";
										resultCompatibiliVAConcessa.setAttribute(resultCurr);
										// response.setAttribute("conferma","S");
									} else {
										if (codTipoConcessione.equals("N")) {
											statoCigCompatibileVANonConcessa = true;
											msgCodeCigCompatibileVANonConcessa = MessageCodes.CIG.ERROR_DOMANDA_NON_APPROVATA;
											msgCigCompatibileVANonConcessa = "Esiste una domanda CIG che non è stata approvata per lo stesso periodo di CIG";
											resultCompatibiliVANonConcessa_AN.setAttribute(resultCurr);
											// response.setAttribute("conferma","S");
										}
									}
								}
							}
							// Controllo per stato Annullato
							if (codStatoAtto.equals("AN")) {
								statoCigCompatibile_AN = true;
								msgCodeCigCompatibile_AN = MessageCodes.CIG.ERROR_DOMANDA_ANNULLATA;
								msgCigCompatibile_AN = "Esiste una domanda CIG annullata per lo stesso periodo di CIG";
								resultCompatibiliVANonConcessa_AN.setAttribute(resultCurr);
								// response.setAttribute("conferma","S");
							}
						}
					}

					if (statoCigCompatibileVAConcessa || statoCigCompatibileVANonConcessa || statoCigCompatibile_AN) {
						if (statoCigCompatibileVAConcessa) {
							msg = msgCigCompatibileVAConcessa;
							msgCode = msgCodeCigCompatibileVAConcessa;
							response.setAttribute(resultCompatibiliVAConcessa);
							response.setAttribute("CIGCOMPATIBILE", "VA_CONCESSA");
						} else {
							if (statoCigCompatibileVANonConcessa) {
								msg = msgCigCompatibileVANonConcessa;
								msgCode = msgCodeCigCompatibileVANonConcessa;
								response.setAttribute(resultCompatibiliVANonConcessa_AN);
								response.setAttribute("CIGCOMPATIBILE", "VA_NON_CONCESSA");
							} else {
								msg = msgCigCompatibile_AN;
								msgCode = msgCodeCigCompatibile_AN;
								response.setAttribute(resultCompatibiliVANonConcessa_AN);
								response.setAttribute("CIGCOMPATIBILE", "AN");
							}
						}
						response.setAttribute("conferma", "S");
						_logger.debug(msg);
						throw new Exception();
					}
				}
			}

			/* 1. per lo stesso lavoratore non possono esserci periodi CIG che si intersecano */

			/*
			 * Commentato 24/03/2010 Giovanni Landi statement = "select count(*) numIscr " + "from am_altra_iscr iscr "
			 * + "where cdnlavoratore = " + cdnlavoratore + "      and ((iscr.DATINIZIO >= to_date('" + dataInizio +
			 * "', 'DD/MM/YYYY') " + "      and iscr.DATINIZIO <= to_date('" + dataFine + "', 'DD/MM/YYYY') ) " +
			 * "      or (iscr.DATFINE >= to_date('" + dataInizio + "', 'DD/MM/YYYY') " +
			 * "      and iscr.DATFINE <= to_date('" + dataFine + "', 'DD/MM/YYYY') ) " +
			 * "      or (iscr.DATINIZIO <= to_date('" + dataInizio + "', 'DD/MM/YYYY') " +
			 * "      and iscr.DATFINE >= to_date('" + dataFine + "', 'DD/MM/YYYY') ) ) ";
			 * 
			 * if(request.containsAttribute("aggiorna")) { statement = statement + " AND iscr.prgaltraiscr NOT IN ('"+
			 * prgAIscr + "')"; }
			 * 
			 * result = ProcessorsUtils.executeSelectQuery(statement, transExec);
			 * 
			 * BigDecimal iscr = (BigDecimal) result.getAttribute("ROW.numIscr"); BigDecimal iscrAperta = null; if
			 * (iscr.intValue() > 0) { msgCode = MessageCodes.CIG.ERROR_PERIODI_NO_COMPATIBILI; msg =
			 * "Esistono iscrizioni con periodi CIG non compatibili."; } else
			 */
			BigDecimal iscrAperta = null;
			if (codStato.equalsIgnoreCase("") && codMotChiusuraIscr.equalsIgnoreCase("")) {

				/*
				 * 2. Non deve esistere più di una iscrizione 'aperta' (stato nullo e motivo di chiusura nullo) per la
				 * stessa azienda, lo stesso lavoratore e stesso riferimento a una domanda CIG (stesso codice
				 * comunicazione accordo)
				 */

				if (!conferma.equals("") && !prgAccordo.equals("")) {
					statement = "select count(*) iscrAperta " + "from am_altra_iscr iscr "
							+ "where iscr.CODSTATO is null and iscr.CODMOTCHIUSURAISCR is null "
							+ "      and iscr.CDNLAVORATORE = " + cdnlavoratore;

					if (!"".equalsIgnoreCase(prgAzienda)) {
						statement = statement + " and iscr.PRGAZIENDA = " + prgAzienda;
					}

					statement += " and iscr.PRGACCORDO = " + prgAccordo;
					if (request.containsAttribute("aggiorna")) {
						statement = statement + " AND iscr.prgaltraiscr NOT IN ('" + prgAIscr + "')";
					}
					result = ProcessorsUtils.executeSelectQuery(statement, transExec);
					iscrAperta = (BigDecimal) result.getAttribute("ROW.iscrAperta");
					if (iscrAperta.intValue() > 0) {
						msgCode = MessageCodes.CIG.ERROR_RIFERIMENTO_DOMANDA_CIG;
						msg = "Esiste più di una iscrizione aperta per lo stesso lavoratore e la stessa azienda.";
					}
				}
			}

			if (iscrAperta != null && iscrAperta.intValue() > 0) {
				_logger.debug(msg);
				throw new Exception();
			}
			if (request.containsAttribute("inserisci")) {
				// gestione configurazione val d'aosta
				if ("2".equals(configurazione)) {
					success = doInsertConfig2(codTipoIscrVect, request, response);
				} else {
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
		} catch (Exception e) {
			reportOperation.reportFailure(msgCode, e, "services()", "Errore generico");
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

	/**
	 * Metodo per l'inserimento in caso di 'configurazione = 2' (val d'aosta). Bisogna prevedere un ciclo su TIPO
	 * ISCRIZIONE.
	 * 
	 * @param codTipoIscrVect
	 * @param request
	 * @param response
	 * @return <code>true</code> se va a buon fine
	 * @throws Exception
	 */
	private boolean doInsertConfig2(Vector<String> codTipoIscrVect, SourceBean request, SourceBean response)
			throws Exception {
		boolean esito = false;

		for (String codTipoIscr : codTipoIscrVect) {
			Object[] params = new Object[18];
			int i = -1;
			params[++i] = getprgAltraIscr(request, response);
			params[++i] = request.getAttribute("CDNLAVORATORE");
			params[++i] = request.getAttribute("PRGAZIENDA");
			params[++i] = request.getAttribute("PRGUNITA");
			params[++i] = request.getAttribute("PRGACCORDO");
			params[++i] = request.getAttribute("CODCOMCOMPETENZA");
			params[++i] = request.getAttribute("CODMONOTIPOCOMPETENZA");
			params[++i] = request.getAttribute("CODMOTCHIUSURAISCR");
			params[++i] = request.getAttribute("CODSTATO");
			params[++i] = codTipoIscr;
			params[++i] = request.getAttribute("DATCHIUSURAISCR");
			params[++i] = request.getAttribute("DATCOMPETENZA");
			params[++i] = request.getAttribute("DATFINE");
			params[++i] = request.getAttribute("DATINIZIO");
			params[++i] = request.getAttribute("STRNOTE");
			params[++i] = request.getAttribute("NUMKLOALTRAISCR");
			params[++i] = userid;
			params[++i] = userid;

			esito = (Boolean) transExec.executeQuery("INS_DOMANDA_CIG", params, "INSERT");
		}

		return esito;
	}
}
