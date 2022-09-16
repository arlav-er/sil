package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CMProspDatiGenIns extends AbstractSimpleModule {

	BigDecimal userid;
	TransactionQueryExecutor transExec = null;
	BigDecimal prgDocumento = null;
	String numkloDocumento = "";

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());
		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			// Controlli sul FLAG CAPOGRUPPO
			String flgCapoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "flgCapoGruppo");
			String strCFCapoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "strCFAZCapoGruppo");
			if (flgCapoGruppo.equals("S") && strCFCapoGruppo.equals("")) {
				throw new Exception(
						"Impossibile inserire il prospetto informativo: valorizzare il codice fiscale capogruppo.");
			}

			if (flgCapoGruppo.equals("N") && !strCFCapoGruppo.equals("")) {
				throw new Exception(
						"Impossibile inserire il prospetto informativo: non bisogna valorizzare il codice fiscale capogruppo.");
			}

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			String codStatoAtto = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
			String codMonoStatoProspetto = StringUtils.getAttributeStrNotNull(serviceRequest, "codMonoStatoProspetto");

			String numannorifprospetto = StringUtils.getAttributeStrNotNull(serviceRequest, "numannorifprospetto");
			String descr = "Prospetto Informativo " + numannorifprospetto;

			serviceRequest.setAttribute("codCpi", user.getCodRif());
			serviceRequest.setAttribute("strDescrizione", descr);

			if (serviceRequest.containsAttribute("inserisciDoc")) {

				// verifica che non esiste un altro prospetto con gli stess: anno,
				// provincia e azienda
				SourceBean rows = null;
				setSectionQuerySelect("CHECK_EXIST_PROSPETTO");
				rows = doSelect(serviceRequest, serviceResponse, false);
				if (rows != null) {
					Vector rowsIn = rows.getContainedAttributes();
					if (rowsIn.size() >= 1) {
						check = false;
						msgCode = MessageCodes.General.ELEMENT_DUPLICATED;
						throw new Exception("Esiste già un prospetto. Operazione interrotta");
					}
				}

				String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
				BigDecimal prgProspettoInfVal = doNextVal(serviceRequest, serviceResponse);
				if (prgProspettoInfVal == null) {
					throw new Exception("Impossibile leggere S_CM_PROSPETTO_INF.NEXTVAL");
				}

				serviceRequest.delAttribute("PRGPROSPETTOINF");
				serviceRequest.delAttribute("STRCHIAVETABELLA");
				serviceRequest.setAttribute("PRGPROSPETTOINF", prgProspettoInfVal.toString());
				serviceRequest.setAttribute("STRCHIAVETABELLA", prgProspettoInfVal.toString());

				this.setSectionQueryInsert("QUERY_INSERT");
				check = doInsert(serviceRequest, serviceResponse);
				if (!check) {
					throw new Exception("Errore durante l'inserimento. Operazione interrotta");
				} else {
					insUpdDocumento(serviceRequest, transExec);
				}
			} else if (serviceRequest.containsAttribute("aggiornaDoc")) {
				this.setSectionQuerySelect("QUERY_SELECT_DOC");
				SourceBean rowDoc = this.doSelect(serviceRequest, serviceResponse);

				if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
					prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
				}

				String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");

				if (prgDocumento != null) {
					if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
						numkloDocumento = String
								.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
						serviceRequest.setAttribute("prgDocumento", prgDocumento);
						serviceRequest.setAttribute("numkloDocumento", numkloDocumento);

						if (codStatoAtto.equals("PR") && codMonoStatoProspetto.equals("A")) {
							msgCode = MessageCodes.CollocamentoMirato.ERROR_PROT_PROSP_ANNO;
							throw new Exception(
									"Impossibile protocollare un prospetto in corso d'anno. Operazione interrotta");
						} else {
							insUpdDocumento(serviceRequest, transExec);
						}
					}
				} else
					throw new Exception();
			}

			requestContainer.setServiceRequest(serviceRequest);

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}
	}

	public void insUpdDocumento(SourceBean request, TransactionQueryExecutor transqueryexec) throws Exception {
		Documento doc = new Documento();

		if (request.containsAttribute("inserisciDoc")) {
			doc = new Documento();
			doc.setPrgDocumento(null);
		} else if (request.containsAttribute("aggiornaDoc")) {
			BigDecimal prgDocumento = (BigDecimal) request.getAttribute("PRGDOCUMENTO");

			if (request.containsAttribute("userid")) {
				userid = (BigDecimal) request.getAttribute("userid");
			}
			String codTipoDocumento = (String) request.getAttribute("codTipoDocumento");
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
			doc.insert(transqueryexec);
		} else if (request.containsAttribute("aggiornaDoc")) {
			doc.update(transqueryexec);
		}
	}

	public String getAttributeAsString(SourceBean request, String param) {
		return SourceBeanUtils.getAttrStrNotNull(request, param);
	}

	public BigDecimal getAttributeAsBigDecimal(SourceBean request, String param) {
		String tmp = SourceBeanUtils.getAttrStrNotNull(request, param);
		if (!tmp.equals("")) {
			return new BigDecimal(tmp);
		}
		return null;
	}

}
