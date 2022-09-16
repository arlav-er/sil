package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteAmLavPattoScelta extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteAmLavPattoScelta.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		boolean ret = false;
		boolean erroreAzioneCollegataVoucher = false;
		BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		BigDecimal prgPattoAssociato = request.containsAttribute("PRG_PATTO_LAVORATORE")
				? new BigDecimal(request.getAttribute("PRG_PATTO_LAVORATORE").toString())
				: null;

		disableMessageIdSuccess();
		if (request.getAttribute("PRG_LAV_PATTO_SCELTA") instanceof String) {
			transExec = new TransactionQueryExecutor(getPool(), this);
			transExec.initTransaction();
			Object prgLavPattoscelta = request.getAttribute("PRG_LAV_PATTO_SCELTA");

			try {
				this.setSectionQuerySelect("QUERY_SELECT_INFO");
				SourceBean amLav = doSelect(request, response);
				String strChiaveTabella = "";
				String codLstTab = "";
				if (amLav.containsAttribute("ROWS")) {
					strChiaveTabella = amLav.containsAttribute("ROWS.ROW.STRCHIAVETABELLA")
							? amLav.getAttribute("ROWS.ROW.STRCHIAVETABELLA").toString()
							: "";
					codLstTab = amLav.containsAttribute("ROWS.ROW.CODLSTTAB")
							? amLav.getAttribute("ROWS.ROW.CODLSTTAB").toString()
							: "";
				} else {
					strChiaveTabella = amLav.containsAttribute("ROW.STRCHIAVETABELLA")
							? amLav.getAttribute("ROW.STRCHIAVETABELLA").toString()
							: "";
					codLstTab = amLav.containsAttribute("ROW.CODLSTTAB")
							? amLav.getAttribute("ROW.CODLSTTAB").toString()
							: "";
				}

				if (codLstTab.equals("OR_PER")) {
					if (PattoManager.esisteVoucher(transExec, prgLavPattoscelta)) {
						erroreAzioneCollegataVoucher = true;
						throw new Exception(
								"Errore nella cancellazione dell'associazione al patto:esiste voucher assegnato o attivato o concluso");
					}
				}

				TracciaModifichePatto.cancellazione(getRequestContainer(), amLav, transExec);
				cancellaImpegniAssociati((String) prgLavPattoscelta, request, response, amLav, transExec);

				if (prgPattoAssociato != null && !strChiaveTabella.equals("")) {
					Object paramPercorso[] = new Object[3];
					paramPercorso[0] = prgPattoAssociato;
					paramPercorso[1] = cdnUtente;
					paramPercorso[2] = new BigDecimal(strChiaveTabella);
					Boolean esitoDeAssocia = (Boolean) transExec.executeQuery("UPD_PERCORSO_DEASSOCIA_PATTO",
							paramPercorso, "UPDATE");
					if (!esitoDeAssocia.booleanValue()) {
						throw new Exception("Errore nella cancellazione dell'associazione al patto.");
					}
				}

				Object param[] = new Object[1];
				/*
				 * this.setSectionQueryDelete("QUERY_DELETE"); if(request.getAttribute("PRG_LAV_PATTO_SCELTA")!=null)
				 * request.updAttribute("PRG_LAV_PATTO_SCELTA", prgLavPattoscelta); else
				 * request.setAttribute("PRG_LAV_PATTO_SCELTA", prgLavPattoscelta);
				 */
				// ret = doDelete(request, response);
				param[0] = prgLavPattoscelta;
				Boolean esito = (Boolean) transExec.executeQuery("DEL_LAV_PATTO_SCELTA", param, "DELETE");
				if (!esito.booleanValue()) {
					throw new Exception("Errore nella cancellazione dell'associazione al patto.");
				}
				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
			} catch (Exception e) {
				transExec.rollBackTransaction();
				if (erroreAzioneCollegataVoucher) {
					reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ESISTE_VOUCHER_AZIONE_PATTO);
				} else {
					reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e);
			}
		} else {
			List keys = (List) request.getAttribute("PRG_LAV_PATTO_SCELTA");
			for (int i = 0; i < keys.size(); i++) {
				try {
					transExec = new TransactionQueryExecutor(getPool());
					transExec.initTransaction();
					Object key = keys.get(i);

					request.delAttribute("PRG_LAV_PATTO_SCELTA");
					request.setAttribute("PRG_LAV_PATTO_SCELTA", key);
					this.setSectionQuerySelect("QUERY_SELECT_INFO");
					SourceBean amLav = doSelect(request, response);
					String strChiaveTabella = "";
					String codLstTab = "";
					if (amLav.containsAttribute("ROWS")) {
						strChiaveTabella = amLav.containsAttribute("ROWS.ROW.STRCHIAVETABELLA")
								? amLav.getAttribute("ROWS.ROW.STRCHIAVETABELLA").toString()
								: "";
						codLstTab = amLav.containsAttribute("ROWS.ROW.CODLSTTAB")
								? amLav.getAttribute("ROWS.ROW.CODLSTTAB").toString()
								: "";
					} else {
						strChiaveTabella = amLav.containsAttribute("ROW.STRCHIAVETABELLA")
								? amLav.getAttribute("ROW.STRCHIAVETABELLA").toString()
								: "";
						codLstTab = amLav.containsAttribute("ROW.CODLSTTAB")
								? amLav.getAttribute("ROW.CODLSTTAB").toString()
								: "";
					}

					if (codLstTab.equals("OR_PER")) {
						if (PattoManager.esisteVoucher(transExec, key)) {
							erroreAzioneCollegataVoucher = true;
							throw new Exception(
									"Errore nella cancellazione dell'associazione al patto:esiste voucher assegnato o attivato o concluso");
						}
					}

					TracciaModifichePatto.cancellazione(getRequestContainer(), amLav, transExec);
					cancellaImpegniAssociati((String) key, request, response, amLav, transExec);

					if (prgPattoAssociato != null && !strChiaveTabella.equals("")) {
						Object paramPercorso[] = new Object[3];
						paramPercorso[0] = prgPattoAssociato;
						paramPercorso[1] = cdnUtente;
						paramPercorso[2] = new BigDecimal(strChiaveTabella);
						Boolean esitoDeAssocia = (Boolean) transExec.executeQuery("UPD_PERCORSO_DEASSOCIA_PATTO",
								paramPercorso, "UPDATE");
						if (!esitoDeAssocia.booleanValue()) {
							throw new Exception("Errore nella cancellazione dell'associazione al patto.");
						}
					}

					/*
					 * if(ret){ request.delAttribute("PRG_LAV_PATTO_SCELTA");
					 * request.setAttribute("PRG_LAV_PATTO_SCELTA", key); ret = doDelete(request, response); }
					 */
					Object params[] = new Object[1];
					params[0] = key;
					Boolean esito = (Boolean) transExec.executeQuery("DEL_LAV_PATTO_SCELTA", params, "DELETE");
					if (!esito.booleanValue()) {
						throw new Exception("Errore nella cancellazione dell'associazione al patto.");
					}
					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
				} catch (Exception e) {
					try {
						if (transExec != null) {
							transExec.rollBackTransaction();
						}
					} catch (EMFInternalError ie) {
						reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
						it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

					}
					it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) e);

					if (erroreAzioneCollegataVoucher) {
						reportOperation.reportFailure(MessageCodes.MSGVOUCHER.ERRORE_ESISTE_VOUCHER_AZIONE_PATTO);
					} else {
						reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
					}
					break;
				} // end catch
			} // END for (int i = 0; i < keys.size(); i++) {
		}
	}// end service

	/**
	 * Cancellazioni eventuali impegni associati e memorizzati precedentemente
	 */
	public void cancellaImpegniAssociati(String prg, SourceBean request, SourceBean response, SourceBean amLav,
			TransactionQueryExecutor transExec) throws Exception {
		String codLstTab = "";
		SourceBean tmp = null;
		SourceBean impegniMemo = null;
		Vector impegni = null;
		String prgPattoLavoratore = "";
		boolean canDelete = false;
		boolean nonCancellareImp = false;
		String prgLavPattoSceltaImp = "";
		// Recupero le info sull'associazione da cancellare

		if (amLav == null)
			throw new Exception("Errore nella cancellazione delle associazioni.");

		codLstTab = (String) amLav.getAttribute("ROW.CODLSTTAB");
		if (codLstTab.equals("OR_PER")) {
			// Azioni
			this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_LEGATI_AZIONE");
			// Il prglavpattoscelta è già presente nella request
		} else {
			if (codLstTab.equals("AG_LAV")) {
				// Servizi
				this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_LEGATI_SERVIZIO");
			} else
				nonCancellareImp = true;
		}
		if (!nonCancellareImp) {
			tmp = doSelect(request, response);
			Vector rows = tmp.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				for (int i = 0; i < rows.size(); i++) {
					SourceBean sb = (SourceBean) rows.get(i);
					String imp = sb.getAttribute("CODIMPEGNO").toString();
					prgPattoLavoratore = sb.getAttribute("PRGPATTOLAVORATORE").toString();
					// Per ogni impegno controllo se è memorizzato in
					// am_lav_patto_scelta
					if (request.getAttribute("STRCHIAVETABELLA") != null)
						request.updAttribute("STRCHIAVETABELLA", imp);
					else
						request.setAttribute("STRCHIAVETABELLA", imp);

					if (request.getAttribute("PRGPATTOLAVORATORE") != null)
						request.updAttribute("PRGPATTOLAVORATORE", prgPattoLavoratore);
					else
						request.setAttribute("PRGPATTOLAVORATORE", prgPattoLavoratore);
					this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_MEMO");
					impegniMemo = doSelect(request, response);
					impegni = impegniMemo.getAttributeAsVector("ROW");

					if ((impegni != null) && !impegni.isEmpty()) {
						SourceBean s = (SourceBean) impegni.get(0);
						prgLavPattoSceltaImp = s.getAttribute("PRGLAVPATTOSCELTA").toString();
						if (request.getAttribute("CODIMPEGNO") != null)
							request.updAttribute("CODIMPEGNO", imp);
						else
							request.setAttribute("CODIMPEGNO", imp);

						// Se l'impegno è in am_lav_patto_scelta, verifico a chi
						// altro è collegato
						this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_IN_SERVIZIO");
						SourceBean impServ = doSelect(request, response);
						Vector vec = null;
						if (impServ != null) {
							vec = impServ.getAttributeAsVector("ROW");
						}
						this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_IN_AZIONE");
						SourceBean impAz = doSelect(request, response);
						Vector vecAz = null;
						if (impAz != null) {
							vecAz = impAz.getAttributeAsVector("ROW");
						}
						// Se vi sono altri legami, allora non lo devo
						// cancellare
						if ((vecAz != null) && !vecAz.isEmpty())
							continue;
						if ((vec != null) && !vec.isEmpty())
							continue;
						canDelete = true;
					} else
						continue;

					if (canDelete) {
						/*
						 * this.setSectionQueryDelete("QUERY_DELETE_IMPEGNO_LEGATO");
						 * if(request.getAttribute("PRG_LAV_PATTO_SCELTA_IMPEGNO")!=null)
						 * request.updAttribute("PRG_LAV_PATTO_SCELTA_IMPEGNO", prgLavPattoSceltaImp); else
						 * request.setAttribute("PRG_LAV_PATTO_SCELTA_IMPEGNO", prgLavPattoSceltaImp); boolean ris =
						 * doDelete(request, response); if(!ris) throw new Exception("Errore nella cancellazione delle
						 * associazioni.");
						 */
						Object param[] = new Object[1];
						param[0] = prgLavPattoSceltaImp;
						Boolean ris = (Boolean) transExec.executeQuery("DELETE_IMPEGNO", param, "DELETE");
						if (!ris.booleanValue()) {
							throw new Exception("Errore nella cancellazione dell'impegno associato.");
						}
					} // end if(canDelete){
				} // end for
			} // end if((rows!=null) && !rows.isEmpty()){
		} // end if(!nonCancellareImp){
	}// end cancellaImpegniAssociati

}