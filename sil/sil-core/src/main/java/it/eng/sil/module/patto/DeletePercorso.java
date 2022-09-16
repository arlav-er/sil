package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PercorsoConcordatoException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class DeletePercorso extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		Object param[] = new Object[2];
		param[0] = request.getAttribute("PRGPERCORSO");
		param[1] = request.getAttribute("PRGCOLLOQUIO");

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("Get_Politiche_Attive_Coll_Adesione_Delete", param,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			Vector azioniGG = row.getAttributeAsVector("ROW");
			if (azioniGG.size() > 0) {
				reportOperation.reportFailure(MessageCodes.Patto.ADESIONE_COLLEGATA_POLITICHE_ATTIVE);
				return;
			}
		}

		row = (SourceBean) QueryExecutor.executeQuery("GET_Voucher_Azione_Patto_Cancella_Percorso", param, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			Integer countVoucher = new Integer(row.getAttribute("countVoucher").toString());
			if (countVoucher.intValue() > 0) {
				reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ESISTE_VOUCHER_DELETE_PERCORSO);
				return;
			}
		}

		TransactionQueryExecutor transExec = null;
		// Controllo se ci sono iscrizione ai corsi
		if (PattoManager.withPatto(request)) {
			boolean ret = false;
			try {
				controllaIscrizioneCorso(request, response);
				controllaCIG(request, response);
				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();

				// Deassociazione impegni
				AssociazioneImpegni assImp = new AssociazioneImpegni("", "OR_PER", request, transExec);
				assImp.cancellaImpegniAssociati(request.getAttribute("PRG_LAV_PATTO_SCELTA").toString());

				ret = this.doDelete(request, response);
				if (ret) {
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}
				//
				if (ret) {
					Object paramColl[] = new Object[1];
					paramColl[0] = request.getAttribute("PRGCOLLOQUIO");
					SourceBean colloquio = (SourceBean) transExec.executeQuery("GET_INFO_COLLOQUIO", paramColl,
							"SELECT");
					BigDecimal numklocoll = (BigDecimal) colloquio.getAttribute("ROW.NUMKLOCOLLOQUIO");
					String dataFineProgrammaDB = (String) colloquio.getAttribute("ROW.DATAFINEPROGRAMMA");
					numklocoll = numklocoll.add(new BigDecimal(1));
					request.setAttribute("NUMKLOCOLLPROGRAMMA", numklocoll);

					this.setSectionQuerySelect("QUERY_SELECT_AZIONI_IN_CORSO");
					SourceBean rowAzioni = doSelect(request, response, false);
					if (rowAzioni != null) {
						Vector<SourceBean> azioni = rowAzioni.getAttributeAsVector("ROW");
						if (azioni.size() == 0) {
							// non ci sono più azioni
							// prima di riaprire si deve controllare che non esiste un altro programma aperto
							this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
							SourceBean rowsP = doSelect(request, response);
							BigDecimal numCollAperti = (BigDecimal) rowsP.getAttribute("ROW.numProgrammiAperti");
							if (numCollAperti.intValue() > 0) {
								throw new PercorsoConcordatoException(
										MessageCodes.Patto.COLLOQUIO_PROGRAMMA_GIA_APERTO);
							} else {
								this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
								boolean retProgramma = this.doUpdate(request, response);
								if (!retProgramma) {
									throw new Exception("Errore aggiornamento programma.");
								}
							}
						} else {
							boolean isProgrammaConcluso = true;
							for (int j = 0; j < azioni.size(); j++) {
								SourceBean rowAz = azioni.get(j);
								rowAz = rowAz.containsAttribute("ROW") ? (SourceBean) rowAz.getAttribute("ROW") : rowAz;
								String flagStatoEsito = rowAz.getAttribute("flgstatoconcluso") != null
										? rowAz.getAttribute("flgstatoconcluso").toString()
										: "";
								if (!flagStatoEsito.equalsIgnoreCase(Values.FLAG_TRUE)) {
									isProgrammaConcluso = false;
								}
							}
							if (isProgrammaConcluso) {
								// programma chiuso
								this.setSectionQuerySelect("QUERY_GET_MAX_DATA_CONCLUSIONE");
								SourceBean rowAzioniData = doSelect(request, response, false);
								String dataMaxFineProgramma = null;
								if (rowAzioniData != null) {
									Vector<SourceBean> azioniData = rowAzioniData.getAttributeAsVector("ROW");
									if (azioniData.size() > 0) {
										SourceBean rowAzData = azioniData.get(0);
										dataMaxFineProgramma = rowAzData.getAttribute("datConclusione") != null
												? rowAzData.getAttribute("datConclusione").toString()
												: "";
									}
								}
								if (dataMaxFineProgramma == null || dataMaxFineProgramma.equals("")) {
									dataMaxFineProgramma = DateUtils.getNow();
								}
								request.setAttribute("DATAFINECOLLPROGRAMMA", dataMaxFineProgramma);
								this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
								boolean retProgramma = this.doUpdate(request, response);
								if (!retProgramma) {
									throw new Exception("Errore aggiornamento programma.");
								}
							} else {
								// programma non chiuso
								if (dataFineProgrammaDB != null && !dataFineProgrammaDB.equals("")) {
									// prima di riaprire si deve controllare che non esiste un altro programma aperto
									this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
									SourceBean rowsP = doSelect(request, response);
									BigDecimal numCollAperti = (BigDecimal) rowsP
											.getAttribute("ROW.numProgrammiAperti");
									if (numCollAperti.intValue() > 0) {
										throw new PercorsoConcordatoException(
												MessageCodes.Patto.COLLOQUIO_PROGRAMMA_GIA_APERTO);
									} else {
										this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
										boolean retProgramma = this.doUpdate(request, response);
										if (!retProgramma) {
											throw new Exception("Errore aggiornamento programma.");
										}
									}
								}
							}
						}
					}

					transExec.commitTransaction();
					// this.setMessageIdSuccess(idSuccess);
					// this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
				} else {
					throw new Exception();
				}
			} catch (PercorsoConcordatoException e) {
				reportOperation.reportFailure(((PercorsoConcordatoException) e).getMessageIdFail());
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (PercorsoIscrizioneCorsiException e) {
				reportOperation.reportFailure(((PercorsoIscrizioneCorsiException) e).getMessageIdFail());
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		} else {
			boolean retDel = false;
			try {
				controllaIscrizioneCorso(request, response);
				controllaCIG(request, response);

				transExec = new TransactionQueryExecutor(getPool());
				enableTransactions(transExec);
				transExec.initTransaction();

				retDel = this.doDelete(request, response);
				//
				if (retDel) {
					Object paramColl[] = new Object[1];
					paramColl[0] = request.getAttribute("PRGCOLLOQUIO");
					SourceBean colloquio = (SourceBean) transExec.executeQuery("GET_INFO_COLLOQUIO", paramColl,
							"SELECT");
					BigDecimal numklocoll = (BigDecimal) colloquio.getAttribute("ROW.NUMKLOCOLLOQUIO");
					numklocoll = numklocoll.add(new BigDecimal(1));
					String dataFineProgrammaDB = (String) colloquio.getAttribute("ROW.DATAFINEPROGRAMMA");
					request.setAttribute("NUMKLOCOLLPROGRAMMA", numklocoll);

					this.setSectionQuerySelect("QUERY_SELECT_AZIONI_IN_CORSO");
					SourceBean rowAzioni = doSelect(request, response, false);
					if (rowAzioni != null) {
						Vector<SourceBean> azioni = rowAzioni.getAttributeAsVector("ROW");
						if (azioni.size() == 0) {
							// programma non chiuso
							this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
							boolean retProgramma = this.doUpdate(request, response);
							if (!retProgramma) {
								throw new Exception("Errore aggiornamento programma.");
							}
						} else {
							boolean isProgrammaConcluso = true;
							for (int j = 0; j < azioni.size(); j++) {
								SourceBean rowAz = azioni.get(j);
								rowAz = rowAz.containsAttribute("ROW") ? (SourceBean) rowAz.getAttribute("ROW") : rowAz;
								String flagStatoEsito = rowAz.getAttribute("flgstatoconcluso") != null
										? rowAz.getAttribute("flgstatoconcluso").toString()
										: "";
								if (!flagStatoEsito.equalsIgnoreCase(Values.FLAG_TRUE)) {
									isProgrammaConcluso = false;
								}
							}
							if (isProgrammaConcluso) {
								// programma chiuso
								String dataMaxFineProgramma = null;
								this.setSectionQuerySelect("QUERY_GET_MAX_DATA_CONCLUSIONE");
								SourceBean rowAzioniData = doSelect(request, response, false);
								if (rowAzioniData != null) {
									Vector<SourceBean> azioniData = rowAzioniData.getAttributeAsVector("ROW");
									if (azioniData.size() > 0) {
										SourceBean rowAzData = azioniData.get(0);
										dataMaxFineProgramma = rowAzData.getAttribute("datConclusione") != null
												? rowAzData.getAttribute("datConclusione").toString()
												: "";
									}
								}
								if (dataMaxFineProgramma == null || dataMaxFineProgramma.equals("")) {
									dataMaxFineProgramma = DateUtils.getNow();
								}
								request.setAttribute("DATAFINECOLLPROGRAMMA", dataMaxFineProgramma);
								this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
								boolean retProgramma = this.doUpdate(request, response);
								if (!retProgramma) {
									throw new Exception("Errore aggiornamento programma.");
								}
							} else {
								// programma non chiuso
								if (dataFineProgrammaDB != null && !dataFineProgrammaDB.equals("")) {
									this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
									boolean retProgramma = this.doUpdate(request, response);
									if (!retProgramma) {
										throw new Exception("Errore aggiornamento programma.");
									}
								}
							}
						}
					}

					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
				} else {
					throw new Exception("");
				}
			} catch (PercorsoConcordatoException e) {
				reportOperation.reportFailure(((PercorsoConcordatoException) e).getMessageIdFail());
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (PercorsoIscrizioneCorsiException e) {
				reportOperation.reportFailure(((PercorsoIscrizioneCorsiException) e).getMessageIdFail());
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (Exception e) {
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		}
	}

	private void controllaCIG(SourceBean request, SourceBean response) throws PercorsoConcordatoException {
		String checkCIGColl = "false";
		if (request.containsAttribute("checkCIGColl")) {
			checkCIGColl = (String) request.getAttribute("checkCIGColl");
		}
		if ("true".equals(checkCIGColl)) {
			/*
			 * etrae il numero di corsi cig collegati al percorso che si cerca di eliminare. count dei record in
			 * ci_corso
			 */
			this.setSectionQuerySelect("QUERY_SELECT_CORSO_CIG");
			SourceBean rowCorsoCIG = this.doSelect(request, response);
			BigDecimal numCorsoCIG = (BigDecimal) rowCorsoCIG.getAttribute("ROW.corsoCIG");
			/* se vi è almeno un corso cig collegato lancio l'eccezione */
			if (numCorsoCIG.intValue() > 0) {
				throw new PercorsoConcordatoException(MessageCodes.Patto.PRESTAZIONICIG_EXISTS_CORSOCI);
			}

			this.setSectionQuerySelect("QUERY_SELECT_CIG");
			SourceBean rowCIG = this.doSelect(request, response);
			String presaincaricoCIGVal = (String) rowCIG.getAttribute("ROW.presaincaricoCIG");
			/* se il percorso che cerco di eliminare è una PIC ed è stato EROGATO */
			if ("true".equals(presaincaricoCIGVal)) {
				/* conto i percorsi non PIC collegati e già erogati */
				Vector rowsPercorsi = getServiceResponse().getAttributeAsVector("M_ListPercorsi.ROWS.ROW");
				int countNotPCIG = 0;
				if (rowsPercorsi.size() >= 1) {
					for (Iterator iter = rowsPercorsi.iterator(); iter.hasNext();) {
						SourceBean attributeValueSB = (SourceBean) iter.next();
						String attributeValueAz = String
								.valueOf((BigDecimal) attributeValueSB.getAttribute("PRGAZIONI"));
						String attributeCodEsitoRendicont = (String) attributeValueSB.getAttribute("CODESITORENDICONT");
						// azione non PIC erogata
						if (!"151".equals(attributeValueAz) && "E".equals(attributeCodEsitoRendicont)) {
							countNotPCIG += 1;
						}
					}
				}
				/* se vi sono non PIC erogate lancio l'eccezione */
				if (countNotPCIG != 0) {
					throw new PercorsoConcordatoException(MessageCodes.Patto.PERC_CONCORD_CANC_PIN_CIG);
				}
			}
		}
	}

	private void controllaIscrizioneCorso(SourceBean request, SourceBean response)
			throws PercorsoIscrizioneCorsiException {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		this.setSectionQuerySelect("QUERY_SELECT_ISCRIZIONE");
		SourceBean rowCorso = this.doSelect(request, response);
		BigDecimal numCorso = (BigDecimal) rowCorso.getAttribute("ROW.numcorso");

		if (numCorso.intValue() > 0) {
			// throw new Exception(MessageCodes.Patto.ESISTECORSOPROG);
			throw new PercorsoIscrizioneCorsiException(MessageCodes.Patto.ESISTECORSOPROG);
		}

	}
}
