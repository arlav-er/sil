package it.eng.sil.module.batch;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaProgrammazione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		Object res = null;
		int errorCode = MessageCodes.General.UPDATE_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		String codTipoBatch = "";
		String flgAttivo = "";
		String codCpi = "";
		String nameSectionUpdate = "";
		String nameSectionExistBatch = "";
		int idFailExistSProgrammazione = 0;
		boolean aggiorna = true;
		try {
			codTipoBatch = StringUtils.getAttributeStrNotNull(request, "CODTIPOBATCH");

			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			Integer chiaveBatch = Constants.mapBatch.get(codTipoBatch);
			switch (chiaveBatch.intValue()) {
			case Constants.APPUNTAMENTI: {
				flgAttivo = StringUtils.getAttributeStrNotNull(request, "flgAttivoApp");
				nameSectionUpdate = "QUERY_AGGIORNA_PROGRAMMAZIONE_APP";
				codCpi = StringUtils.getAttributeStrNotNull(request, "codCpiApp");
				if (StringUtils.isEmptyNoBlank(codCpi)) {
					nameSectionExistBatch = "EXISTS_BATCH_APPUNTAMENTI_ATTIVO";
				} else {
					nameSectionExistBatch = "EXISTS_BATCH_APPUNTAMENTI_ATTIVO_CPI";
				}
				idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_APPUNTAMENTI_SERVIZIO;
				break;
			}
			case Constants.AZIONI_PROGRAMMATE: {
				flgAttivo = StringUtils.getAttributeStrNotNull(request, "flgAttivoAzione");
				nameSectionUpdate = "QUERY_AGGIORNA_PROGRAMMAZIONE_AZIONE";
				codCpi = StringUtils.getAttributeStrNotNull(request, "codCpiAzione");
				if (StringUtils.isEmptyNoBlank(codCpi)) {
					nameSectionExistBatch = "EXISTS_BATCH_AZIONE_PROGRAMMATA_ATTIVO";
				} else {
					nameSectionExistBatch = "EXISTS_BATCH_AZIONE_PROGRAMMATA_ATTIVO_CPI";
				}
				idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_AZIONI_AZIONE;
				break;
			}
			case Constants.CONFERMA_DID: {
				flgAttivo = StringUtils.getAttributeStrNotNull(request, "flgAttivoDid");
				nameSectionUpdate = "QUERY_AGGIORNA_PROGRAMMAZIONE_DID";
				codCpi = StringUtils.getAttributeStrNotNull(request, "codCpiDid");
				if (StringUtils.isEmptyNoBlank(codCpi)) {
					nameSectionExistBatch = "EXISTS_BATCH_DID_ATTIVO";
				} else {
					nameSectionExistBatch = "EXISTS_BATCH_DID_ATTIVO_CPI";
				}
				idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_DID_CPI;
				break;
			}
			case Constants.PERDITA_DISOCC: {
				flgAttivo = StringUtils.getAttributeStrNotNull(request, "flgAttivoPerdDisocc");
				nameSectionUpdate = "QUERY_AGGIORNA_PROGRAMMAZIONE_PERDDISOCC";
				codCpi = StringUtils.getAttributeStrNotNull(request, "codCpiPerdDisocc");
				String codMotivoFineAtto = StringUtils.getAttributeStrNotNull(request, "codMotivoFineAttoDid");
				if (StringUtils.isEmptyNoBlank(codCpi) && !codMotivoFineAtto.equals("")) {
					idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_PERDDISOCC_MOTFINEATTO;
					nameSectionExistBatch = "EXISTS_BATCH_PERDDISOCC_ATTIVO";
				} else if (StringUtils.isEmptyNoBlank(codCpi) && codMotivoFineAtto.equals("")) {
					idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_PERDDISOCC_MOTFINEATTO_NULL;
					nameSectionExistBatch = "EXISTS_BATCH_PERDDISOCC_ATTIVO_MOTFINEATTO_NULL";
				} else if (!StringUtils.isEmptyNoBlank(codCpi)) {
					idFailExistSProgrammazione = MessageCodes.ProgrammazioneBatch.ERR_EXISTS_BATCH_PERDDISOCC_CPI;
					nameSectionExistBatch = "EXISTS_BATCH_PERDDISOCC_ATTIVO_CPI";
				}
				break;
			}
			}

			if (flgAttivo.equalsIgnoreCase("S")) {
				setSectionQuerySelect("GET_NUM_MAX_BATCH_ATTIVI");
				SourceBean rowMaxProgrammazione = this.doSelect(request, response);
				if (rowMaxProgrammazione.containsAttribute("ROW.NUMMAXPROGR")) {
					int numMaxProgrammazioni = ((BigDecimal) rowMaxProgrammazione.getAttribute("ROW.NUMMAXPROGR"))
							.intValue();
					setSectionQuerySelect("GET_BATCH_ATTIVI");
					SourceBean rowProgrammazioneAttive = this.doSelect(request, response);
					if (rowProgrammazioneAttive.containsAttribute("ROW.NUMPROGRAMMAZIONIATTIVE")) {
						int numProgrammazioniAttive = ((BigDecimal) rowProgrammazioneAttive
								.getAttribute("ROW.NUMPROGRAMMAZIONIATTIVE")).intValue();
						if (numProgrammazioniAttive >= numMaxProgrammazioni) {
							aggiorna = false;
							switch (chiaveBatch.intValue()) {
							case Constants.APPUNTAMENTI: {
								idFail = MessageCodes.ProgrammazioneBatch.MAX_NUM_BATCH_ATTIVI_APP;
								break;
							}
							case Constants.AZIONI_PROGRAMMATE: {
								idFail = MessageCodes.ProgrammazioneBatch.MAX_NUM_BATCH_ATTIVI_AZIONE;
								break;
							}
							case Constants.CONFERMA_DID: {
								idFail = MessageCodes.ProgrammazioneBatch.MAX_NUM_BATCH_ATTIVI_DID;
								break;
							}
							case Constants.PERDITA_DISOCC: {
								idFail = MessageCodes.ProgrammazioneBatch.MAX_NUM_BATCH_ATTIVI_PERDDISOCC;
								break;
							}
							}
						}
					}
				}

				if (aggiorna && !nameSectionExistBatch.equals("")) {
					setSectionQuerySelect(nameSectionExistBatch);
					SourceBean rowAttiva = this.doSelect(request, response);
					if (rowAttiva.containsAttribute("ROW.NUMPROGRAMMAZIONIATTIVE")) {
						int numAttive = ((BigDecimal) rowAttiva.getAttribute("ROW.NUMPROGRAMMAZIONIATTIVE")).intValue();
						if (numAttive > 0) {
							aggiorna = false;
							idFail = idFailExistSProgrammazione;
						}
					}
				}
			}

			if (aggiorna) {
				setSectionQueryUpdate(nameSectionUpdate);
				res = this.doUpdate(request, response, true);
				ret = ((Boolean) res).booleanValue();
				if (!ret) {
					throw new Exception("impossibile aggiornare la programmazione");
				}
				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			} else {
				transExec.rollBackTransaction();
				reportOperation.reportFailure(idFail);
			}

		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(errorCode);
		}
	}

}
