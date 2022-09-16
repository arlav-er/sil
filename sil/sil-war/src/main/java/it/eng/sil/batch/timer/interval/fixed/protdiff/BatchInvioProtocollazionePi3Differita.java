package it.eng.sil.batch.timer.interval.fixed.protdiff;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.pi3.BatchProtocollazioneDifferitaBean;
import it.eng.sil.module.pi3.CreaDocumentPi3Bean;
import it.eng.sil.module.pi3.InvioProtocollazioneDifferitaBean;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.module.pi3.ProtocolloPi3Manager;
import it.eng.sil.module.pi3.ProtocolloPi3Utility;
import it.eng.sil.module.pi3.UtentePi3Bean;

@Singleton
public class BatchInvioProtocollazionePi3Differita extends FixedTimerBatch {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(BatchInvioProtocollazionePi3Differita.class.getName());

	DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY);
	DateFormat formatHourMinutes = new SimpleDateFormat("HH:mm");

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "18", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {

			Date arg0 = new Date();

			_logger.info("[BatchInvioProtocollazionePi3Differita] ---> start PERFORM <---");
			_logger.info("[BatchInvioProtocollazionePi3Differita] ---> DATE: " + format.format(arg0) + " <---");

			// Managers...
			ProtocolloPi3Manager protocolloPi3Manager = new ProtocolloPi3Manager();
			ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

			try {

				// Il Batch verifica se e' stato abilitato da sistema per essere lanciato...
				BatchProtocollazioneDifferitaBean batchProtocollazioneDifferitaBean = dbManager
						.getBatchProtocollazioneDifferitaByNomeBatch(
								Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_NOME_BATCH);

				_logger.debug("[BatchInvioProtocollazionePi3Differita] ---> FlgProtDiff: "
						+ batchProtocollazioneDifferitaBean.getFlgProtDiff());
				if (!StringUtils.isEmpty(batchProtocollazioneDifferitaBean.getFlgProtDiff())) {

					/*******************************************************************
					 * MODALITA' INVIO IN DIFFERITA CON DESERIALIZZAZIONE DEL BEAN
					 *******************************************************************/
					if (batchProtocollazioneDifferitaBean.getFlgProtDiff()
							.equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_SI)) {

						if (checkBatchAmongHours(arg0, batchProtocollazioneDifferitaBean.getNumOraInizio(),
								batchProtocollazioneDifferitaBean.getNumOraFine())) {

							// Recupero record di Pratiche da Inviare alla Protocollazione Pi3
							ArrayList<InvioProtocollazioneDifferitaBean> lstDaElaborare = dbManager
									.getListaDaElabInvioProtocollazioneDifferitaByCodStato(
											Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_DA_ELABORARE);

							_logger.debug("[BatchInvioProtocollazionePi3Differita] Nr. pratiche da elaborare: "
									+ lstDaElaborare.size());
							int i = 0;
							for (InvioProtocollazioneDifferitaBean invioProtocollazioneDifferitaBean : lstDaElaborare) {
								_logger.info("[BatchInvioProtocollazionePi3Differita] [" + i
										+ "] Nr.  Pratica Da Elaborare: "
										+ invioProtocollazioneDifferitaBean.getStrNumPratica());
								_logger.info("[BatchInvioProtocollazionePi3Differita] [" + i
										+ "] Data Pratica in preparazione: "
										+ format.format(invioProtocollazioneDifferitaBean.getDatIns()));

								File fileBlob = null;

								try {

									// Si recupera separatamente il file BLOB (l'oggetto contenente il bean dei dati in
									// INPUT in forma serializzata)
									fileBlob = dbManager
											.readBLOBean(invioProtocollazioneDifferitaBean.getPrgProtDifferita());
									_logger.debug("[BatchInvioProtocollazionePi3Differita] ---> fileBlob: "
											+ fileBlob.getAbsolutePath() + "[" + fileBlob.length() + "]");

									if (fileBlob != null) {
										if (fileBlob.length() > 0) {
											CreaDocumentPi3Bean beanDataInput = ProtocolloPi3Utility
													.deserializePi3Bean(fileBlob);
											_logger.debug(
													"[BatchInvioProtocollazionePi3Differita] CreaDocumentPi3Bean: "
															+ beanDataInput.getNrPraticaSPIL());

											// Logger Bean Info...
											printDebugBeanInfo(i, beanDataInput);

											boolean blnPraticaInviataPi3 = protocolloPi3Manager
													.inviaProtocolloPi3(beanDataInput);
											_logger.info("[BatchInvioProtocollazionePi3Differita] [" + i
													+ "] Pratica Elaborata: " + blnPraticaInviataPi3);

											invioProtocollazioneDifferitaBean.setDatInvio(new Date());
											invioProtocollazioneDifferitaBean.setCodStato(
													Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_ELABORATA);

											// AGGIORNA IL RECORD NELLA TABELLA 'TS_INVIO_PROTOCOLLO_DIFFERITA' RENDENDO
											// LA
											// PRATICA 'ELABORATA' E...
											dbManager.updateInvioProtocollazioneDifferita(
													invioProtocollazioneDifferitaBean);
											_logger.debug(
													"[BatchInvioProtocollazionePi3Differita] updateInvioProtocollazioneDifferita");

											// ... ANNULLANDO IL BEAN SERIALIZZATO NEL CAMPO BLOB LIBERANDO MEMORIA
											dbManager.setNullBlobInvioProtocollazioneDifferita(
													invioProtocollazioneDifferitaBean);
											_logger.debug(
													"[BatchInvioProtocollazionePi3Differita] setNullBlobInvioProtocollazioneDifferita");

											if (!blnPraticaInviataPi3) {
												_logger.warn("[BatchInvioProtocollazionePi3Differita] [" + i
														+ "] Pratica elaborata ma processata con errori. Il Batch di re-invio delle pratiche provvedera' ad inoltrarla nuovamente");
											}

											// Si provvede all'eliminazione del File Bean Temporaneo dal filesystem...
											if (fileBlob.delete()) {
												_logger.debug("[BatchInvioProtocollazionePi3Differita] --> fileBlob: "
														+ fileBlob.getAbsolutePath() + "[" + fileBlob.length()
														+ "] eliminato");
											} else {
												_logger.warn("[BatchInvioProtocollazionePi3Differita] --> fileBlob: "
														+ fileBlob.getAbsolutePath() + "[" + fileBlob.length()
														+ "] non e' stato eliminato");
											}

										} else {
											_logger.debug(
													"[BatchInvioProtocollazionePi3Differita] ---> fileBlob is EMPTY");
										}

									} else {
										_logger.debug("[BatchInvioProtocollazionePi3Differita]  ---> fileBlob is NULL");
									}

								} catch (Exception e) {
									_logger.error(
											"[BatchInvioProtocollazionePi3Differita] ---> ERRORE DURANTE PRATICA [" + i
													+ "] IN ELABORAZIONE <---");
									_logger.error("[BatchInvioProtocollazionePi3Differita] ---> EXCEPTION: "
											+ e.getMessage());

									// Si provvede all'eliminazione del File Bean Temporaneo dal filesystem...
									if (fileBlob != null) {
										if (fileBlob.length() > 0) {
											if (fileBlob.delete()) {
												_logger.debug("[BatchInvioProtocollazionePi3Differita] --> fileBlob: "
														+ fileBlob.getAbsolutePath() + "[" + fileBlob.length()
														+ "] eliminato");
											} else {
												_logger.warn("[BatchInvioProtocollazionePi3Differita] --> fileBlob: "
														+ fileBlob.getAbsolutePath() + "[" + fileBlob.length()
														+ "] non e' stato eliminato");
											}
										}
									}

									if (e.getMessage().equalsIgnoreCase("Pratica gia' inviata in precedenza")) {
										invioProtocollazioneDifferitaBean.setDatInvio(new Date());
										invioProtocollazioneDifferitaBean.setCodStato(
												Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_ELABORATA);

										// AGGIORNA IL RECORD NELLA TABELLA 'TS_INVIO_PROTOCOLLO_DIFFERITA' RENDENDO LA
										// PRATICA 'ELABORATA' E...
										dbManager
												.updateInvioProtocollazioneDifferita(invioProtocollazioneDifferitaBean);
										_logger.debug(
												"[BatchInvioProtocollazionePi3Differita] updateInvioProtocollazioneDifferita");

										// ... ANNULLANDO IL BEAN SERIALIZZATO NEL CAMPO BLOB LIBERANDO MEMORIA
										dbManager.setNullBlobInvioProtocollazioneDifferita(
												invioProtocollazioneDifferitaBean);
										_logger.debug(
												"[BatchInvioProtocollazionePi3Differita] setNullBlobInvioProtocollazioneDifferita");
									}
								}

								i++;

							}
						} else {
							_logger.warn(
									"[BatchInvioProtocollazionePi3Differita] ---> WARN: Batch di Protocollazione Pi3 non effettuata poiche' l'attuale periodo ("
											+ format.format(arg0) + ") non rientra negli orari stabiliti (dall'ora "
											+ batchProtocollazioneDifferitaBean.getNumOraInizio() + " all'ora "
											+ batchProtocollazioneDifferitaBean.getNumOraFine() + ")");
						}

					} else {
						_logger.warn(
								"[BatchInvioProtocollazionePi3Differita] ---> WARN: La modalita' di invio Protocollazione Pi3 e' stata impostata in tempo reale e non verra' gestita da questo batch");
					}

				} else {
					_logger.warn(
							"[BatchInvioProtocollazionePi3Differita] ---> Non e' stata impostata nel sistema la modalita' di invio della Protocollazione Pi3: differita o tempo reale");
					throw new Exception(
							"Non e' stata impostata nel sistema la modalita' di invio della Protocollazione Pi3: differita o tempo reale");
				}

			} catch (Throwable e) {
				_logger.error("[BatchInvioProtocollazionePi3Differita] ---> ERROR <---", e);
				_logger.error("[BatchInvioProtocollazionePi3Differita] ---> EXCEPTION: " + e.getMessage());
			}

			_logger.info("[BatchInvioProtocollazionePi3Differita] ---> end PERFORM <---");

		} else {
			// Timer non abilitato su questo nodo
			_logger.warn(
					"[BatchInvioProtocollazionePi3Differita] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}

	}

	private boolean checkBatchAmongHours(Date dateStartBatch, String numOraInizio, String numOraFine) throws Exception {
		boolean batchCanDo = false;

		_logger.debug("[BatchInvioProtocollazionePi3Differita] checkBatchAmongHours");

		Date hourInizio = formatHourMinutes.parse(numOraInizio);
		Date hourFine = formatHourMinutes.parse(numOraFine);
		int oraInizio = hourInizio.getHours();
		int minutiInizio = hourInizio.getMinutes();
		int oraFine = hourFine.getHours();
		int minutiFine = hourFine.getMinutes();

		Calendar calInizio = Calendar.getInstance();
		Calendar calFine = Calendar.getInstance();

		calInizio.set(Calendar.HOUR_OF_DAY, oraInizio);
		calInizio.set(Calendar.MINUTE, minutiInizio);

		calFine.set(Calendar.HOUR_OF_DAY, oraFine);
		calFine.set(Calendar.MINUTE, minutiFine);

		_logger.debug("[BatchInvioProtocollazionePi3Differita] dateStartBatch: " + format.format(dateStartBatch));
		_logger.debug("[BatchInvioProtocollazionePi3Differita] calInizio: " + format.format(calInizio.getTime()));
		_logger.debug("[BatchInvioProtocollazionePi3Differita] calFine: " + format.format(calFine.getTime()));

		batchCanDo = dateStartBatch.after(calInizio.getTime()) && dateStartBatch.before(calFine.getTime());
		_logger.debug("[BatchInvioProtocollazionePi3Differita] checkBatchAmongHours: " + batchCanDo);

		return batchCanDo;
	}

	private void printDebugBeanInfo(int i, CreaDocumentPi3Bean beanDataInput) {

		_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
				+ "] beanDataInput serializzato --> NR. PRATICA SPIL: " + beanDataInput.getNrPraticaSPIL());
		_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
				+ "] beanDataInput serializzato --> DOCUMENT TYPE: " + beanDataInput.getDocumentType());

		if (beanDataInput.getUtenteMittente() != null) {
			_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
					+ "] beanDataInput serializzato --> UTENTE MITTENTE: " + beanDataInput.getUtenteMittente().getNome()
					+ " " + beanDataInput.getUtenteMittente().getCognome() + " ["
					+ beanDataInput.getUtenteMittente().getIdUtenteSPIL() + "]");
		}

		if (beanDataInput.getLstUtentiDestinatari() != null) {
			int k = 0;
			if (beanDataInput.getLstUtentiDestinatari().size() > 0) {
				for (UtentePi3Bean utenteDestinatario : beanDataInput.getLstUtentiDestinatari()) {
					_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + k
							+ "] beanDataInput serializzato --> UTENTE DESTINATARIO: " + utenteDestinatario.getNome()
							+ " " + utenteDestinatario.getCognome() + " [" + utenteDestinatario.getIdUtenteSPIL()
							+ "]");
				}
			}
		}

		_logger.debug(
				"[BatchInvioProtocollazionePi3Differita] [" + i + "] beanDataInput serializzato --> PRG DOCUMENTO: "
						+ beanDataInput.getDocumentSil().getPrgDocumento());

		if (beanDataInput.isDocInEntrata()) {
			_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
					+ "] beanDataInput serializzato --> DOCUMENTO IN ENTRATA");
		} else if (beanDataInput.isDocInUscita()) {
			_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
					+ "] beanDataInput serializzato --> DOCUMENTO IN USCITA");
		} else if (beanDataInput.isDocRepertoriato()) {
			_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
					+ "] beanDataInput serializzato --> DOCUMENTO REPERTORIATO");
		}

		_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
				+ "] beanDataInput serializzato --> FIRMA GRAFOMETRICA PREVISTA DA CONFIGURAZIONE: "
				+ beanDataInput.isDocumentoFirmabile());
		_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
				+ "] beanDataInput serializzato --> MAIN DOCUMENT FIRMATO GRAFOMETRICAMENTE: "
				+ beanDataInput.isDocumentoFirmato());
		_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + i
				+ "] beanDataInput serializzato --> CONSENSO FIRMA GRAFOMETRICA: " + beanDataInput.isConsensoAttivo());

		if (beanDataInput.getLstDocumentiAllegati() != null) {
			int j = 0;
			if (beanDataInput.getLstDocumentiAllegati().size() > 0) {
				for (Documento allegato : beanDataInput.getLstDocumentiAllegati()) {
					_logger.debug("[BatchInvioProtocollazionePi3Differita] [" + j
							+ "] beanDataInput serializzato --> DOCUMENTO ALLEGATO [PRG: " + allegato.getPrgDocumento()
							+ "] DESCRIZIONE: " + allegato.getStrNomeDoc());
				}
			}
		}
	}

}
