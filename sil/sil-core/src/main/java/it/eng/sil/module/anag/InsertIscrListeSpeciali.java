package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InsertIscrListeSpeciali extends AbstractSimpleModule {
	private String className = this.getClass().getName();
	private TransactionQueryExecutor transExec;
	BigDecimal userid, prgDocumento;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			boolean success = false;

			if (request.containsAttribute("inserisciDoc")) {
				String dataIscrLocal = (String) request.getAttribute("DATISCRLISTAPROV");
				setSectionQuerySelect("QUERY_SELECT");
				SourceBean iscrListe = this.doSelect(request, response);
				if (iscrListe.containsAttribute("ROW")) {
					Vector vectorRows = iscrListe.getAttributeAsVector("ROW");
					SourceBean row = (SourceBean) vectorRows.get(0);

					String dataIscrDB = (String) row.getAttribute("DATISCRLISTAPROV");
					String datFine = StringUtils.getAttributeStrNotNull(row, "DATFINE");

					if (!datFine.equals("")) {
						if (DateUtils.compare(dataIscrLocal, datFine) > 0) {
							success = insIscrListe(request, response);
						} else {
							reportOperation.reportFailure(MessageCodes.CollocamentoMirato.ERROR_NO_EXIST_ISCR, null,
									"services()", "insert in transazione");
						}
					} else {
						if (DateUtils.compare(dataIscrLocal, dataIscrDB) > 0 && datFine.equals("")) {
							String prgArtDB = String
									.valueOf(((BigDecimal) iscrListe.getAttribute("ROW.PRGISCRART1")).intValue());
							String NUMKLOCMISCRART1DB = String.valueOf(
									((BigDecimal) iscrListe.getAttribute("ROW.NUMKLOCMISCRART1")).intValue() + 1);
							request.setAttribute("datFineDB", dataIscrLocal);
							request.setAttribute("CODMOTIVOFINEATTODB", "LS2");
							request.setAttribute("NUMKLOCMISCRART1DB", NUMKLOCMISCRART1DB);
							request.setAttribute("prgArtDB", prgArtDB);
							setSectionQuerySelect("QUERY_UPDATE");
							success = this.doUpdate(request, response);
							if (success) {
								success = insIscrListe(request, response);
							}
						} else {
							reportOperation.reportFailure(MessageCodes.CollocamentoMirato.ERROR_NO_EXIST_ISCR, null,
									"services()", "insert in transazione");
						}
					}
				} else {
					success = insIscrListe(request, response);
				}

			} else if (request.containsAttribute("aggiornaDoc")) {

				this.setSectionQuerySelect("QUERY_SELECT_DOC");
				SourceBean rowDoc = doSelect(request, response);

				if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
					prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
				}

				String codStatoAtto = (String) request.getAttribute("CODSTATOATTO");
				String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");
				if (prgDocumento != null) {
					if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
						String numkloDocumento = String
								.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
						request.setAttribute("prgDocumento", prgDocumento);
						request.setAttribute("numkloDocumento", numkloDocumento);

						String prgIscr = (String) request.getAttribute("PRGISCRART1");

						if (codStatoAtto.equals("AN")) {
							this.setSectionQueryUpdate("QUERY_UPDATE_DOC");
							success = doUpdate(request, response);
						} else {
							insDocumento(request);
							success = true;
						}
						response.delAttribute("PRGISCRART1");
						response.setAttribute("PRGISCRART1", prgIscr);
					}
				}
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
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()",
						"insert in transazione");
			} else {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "services()",
						"update in transazione");
			}
		}
	}

	private boolean insIscrListe(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		BigDecimal PrgIscrArt1 = getprgIscrArt1(request, response);
		String CDNUTPUNTEGGIO = "";
		if (PrgIscrArt1 != null) {
			setKeyinRequest(PrgIscrArt1, request);
			this.setSectionQueryInsert("QUERY_INSERT_LISTE_SPECIALI");
			String punteggio = StringUtils.getAttributeStrNotNull(request, "NUMPUNTEGGIO");

			// i campi cdnutpunteggio e datmodifpunteggio in inserimento vanno valirizzati solo se viene impostato il
			// punteggio
			if (!punteggio.equals("")) {
				Date dataNow = new Date();
				String DATMODIFPUNTEGGIO = "";
				if (dataNow != null) {
					// formattatore per trasformare stringhe in date
					SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					DATMODIFPUNTEGGIO = fd.format(dataNow);
				}
				CDNUTPUNTEGGIO = String.valueOf(userid);
				request.setAttribute("DATMODIFPUNTEGGIO", DATMODIFPUNTEGGIO);
				request.setAttribute("CDNUTPUNTEGGIO", CDNUTPUNTEGGIO);
			}

			ret = doInsert(request, response);
			response.delAttribute("PRGISCRART1");
			response.setAttribute("PRGISCRART1", PrgIscrArt1);
			response.delAttribute("CDNUTPUNTEGGIO");
			response.setAttribute("CDNUTPUNTEGGIO", CDNUTPUNTEGGIO);
			if (ret) {
				insDocumento(request);
			}
		}
		return ret;
	}

	private void insDocumento(SourceBean request) throws Exception {
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
		doc.setCdnUtIns(userid);
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

	private BigDecimal getprgIscrArt1(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");

		SourceBean beanprgIscrListeSpec = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgIscrListeSpec.getAttribute("ROW.PRGISCRART1");
	}

	private void setKeyinRequest(BigDecimal PRGISCRART1, SourceBean request) throws Exception {
		if (request.getAttribute("PRGISCRART1") != null) {
			request.delAttribute("PRGISCRART1");
		}

		request.setAttribute("PRGISCRART1", PRGISCRART1);
		request.setAttribute("strChiaveTabella", PRGISCRART1);
	}
}
