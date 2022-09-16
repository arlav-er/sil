package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFUserError;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertDocumento implements RecordProcessor {
	private String className;
	private String prc;
	// PrgMovimento appena inserito

	private BigDecimal numProtPrecedente; // Num di protocollo inserito nel
											// documento precedente (utilizzato
											// in caso, protocollazione massiva)
	private BigDecimal numProtDiPart; // Num di protocollo iniziale
										// (utilizzato in caso, protocollazione
										// massiva)
	boolean primoDocDaProt = true;

	private String keyTable;
	private BigDecimal userId;
	private TransactionQueryExecutor transExecutor;
	private int numeroDocumenti = 1;

	public InsertDocumento(BigDecimal user, TransactionQueryExecutor txExecutor) {
		className = this.getClass().getName();
		this.transExecutor = txExecutor;
		prc = "Inserimento documento";
		userId = user;
	}

	public InsertDocumento(BigDecimal user, TransactionQueryExecutor txExecutor, int numeroDocumenti) {
		this(user, txExecutor);
		setNumeroDocumenti(numeroDocumenti);

	}

	/*
	 * public InsertDocumento(BigDecimal user, TransactionQueryExecutor txExecutor, BigDecimal numProtIniziale){
	 * className = this.getClass().getName(); this.transExecutor = txExecutor; prc = "Inserimento documento"; userId =
	 * user; this.numProtDiPart = numProtIniziale; protMassiva = true; }
	 */

	public SourceBean processRecord(Map record) throws SourceBeanException {
		Monitor monitor;
		monitor = MonitorFactory.start("Processor InsertDocumento");

		ArrayList warnings = new ArrayList();
		boolean validazioneMass = record.containsKey("numProtIniziale");

		// Solo se sto inserendo un nuovo movimento e non nella validazione
		/*
		 * if(record.get("INSERISCI") != null && record.get("INSERISCI").toString().equals("Inserisci")){
		 */

		// La condizione precedente è stata cambiata per consentire la
		// protocollazione anche in caso di validazione
		// (per il momento validazione singola, per la massiva si vedrà). Davide
		// 02/07/2004
		// In caso di vaidazione massiva la variabile
		// CONTEXT="validazioneMassiva" viene settata nella calsse
		// it.eng.sil.module.movimenti.AppoggioExtractor
		// Ora le seguenti righe di controllo valgono anche in caso di prot.
		// massiva. Davide 21/07/2004
		if (record.get("CONTEXT") != null && (record.get("CONTEXT").toString().equalsIgnoreCase("Inserisci")
				|| record.get("CONTEXT").toString().equalsIgnoreCase("valida")
				|| record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva"))) {
			for (int i = 0; i < numeroDocumenti; i++) {

				// Inserimento documento da eseguire solo se è andato a buon
				// fine l'inserimento del movimento
				Documento doc = new Documento();
				doc = settaDocumento(record);
				keyTable = ((BigDecimal) record.get("PRGMOVIMENTO")).toString();
				if (keyTable != null) {
					doc.setCdnUtMod(userId);
					doc.setCdnUtIns(userId);
					doc.setChiaveTabella(keyTable);
					try {
						if (validazioneMass || "valida".equalsIgnoreCase((String) record.get("CONTEXT"))) {
							// (Commento precedente il 27/04/2007) Siamo in
							// validazione massiva (+ documenti in serie)
							// 27/04/2007: Con la modifica della protocollazione
							// la gestione precedenre della valorizzazione del
							// numero di protocollo
							// diventa inutile (ma di fatto lo era anche prima:
							// infatti in ogni caso nel metodo lock...() di
							// Documento il numero di protocollo
							// veniva letto comunque)
							/*
							 * ----------- Codice commentato in data 27/04/2007 -------------- if ( primoDocDaProt ) {
							 * // GG 1-3-05 - num protocollo è ora NUMERICO BigDecimal numProIni = null; Object
							 * numProIniObj = record.get("numProtIniziale"); if (numProIniObj != null) { if
							 * (numProIniObj instanceof BigDecimal) numProIni = (BigDecimal)numProIniObj; else if
							 * (numProIniObj instanceof Number) numProIni = new
							 * BigDecimal(((Number)numProIniObj).doubleValue()); else numProIni = new
							 * BigDecimal(numProIniObj.toString()); } numProtPrecedente = numProIni; primoDocDaProt =
							 * false; doc.setNumProtocollo(numProtPrecedente); } else {
							 * 
							 * BigDecimal numProPiu1 = numProtPrecedente.add( new BigDecimal(1) ); // numPrPre :=
							 * numPrPre + 1 doc.setNumProtocollo(numProPiu1); }
							 */
							// 27/04/2007: da notare che in caso "remoto" di
							// protocollazione manuale la validazione massiva
							// non potrebbe essere eseguita.
							// (al massimo si riuscirebbe a validare il primo
							// movimento)
							// 19/06/2007 Savino: ATTENZIONE E' NECESSARIO
							// PASSARE IL NUMERO DI PROTOCOLLO, PER ATTIVARE LA
							// CONDIZIONE DI LETTURA DEL N. DAL DB
							if (ProtocolloDocumentoUtil.protocollazioneLocale()) {
								if (validazioneMass) {
									Object numProIniObj = record.get("numProtIniziale");
									BigDecimal numProIni = null;
									if (numProIniObj != null) {
										if (numProIniObj instanceof BigDecimal)
											numProIni = (BigDecimal) numProIniObj;
										else if (numProIniObj instanceof Number)
											numProIni = new BigDecimal(((Number) numProIniObj).doubleValue());
										else
											numProIni = new BigDecimal(numProIniObj.toString());
									}
									doc.setNumProtocollo(numProIni);
								} else {
									Object numProObj = record.get("NUMPROTOCOLLO");
									BigDecimal numPro = null;
									if (numProObj != null) {
										if (numProObj instanceof BigDecimal)
											numPro = (BigDecimal) numProObj;
										else if (numProObj instanceof Number)
											numPro = new BigDecimal(((Number) numProObj).doubleValue());
										else
											numPro = new BigDecimal(numProObj.toString());
									}
									doc.setNumProtocollo(numPro);
								}
							}
							// DOCAREA 02/04/2007: si inserisce il documento
							// nella serviseResponse in modo che il Validator,
							// una volta committata la
							// transazione, possa recuperarlo e cancellare il
							// file temporaneo inviato a docarea(solo e solo
							// se e' abilitata questa protocollazione)
							ProtocolloDocumentoUtil.putInRequest(doc);
							// 30/04/2007: se si sta inserendo l'avviamento da
							// cessazione veloce bisogna chiamare la funzione di
							// protocollazione e non
							// utilizzare il numero di protocollo letto dalla
							// tabella am_movimento_appoggio
							boolean docXAvvCVE = GestisciDocumentoCVE.class.equals(getClass());
							// non protocollo solo se si tratta di un documento
							// da movimento pervenuto da sare, in tutti gli
							// altri casi bisogna chiamare la funzione di
							// protocollazione
							if (!docXAvvCVE && !ProtocolloDocumentoUtil.protocollazioneLocale()
									&& ProtocolloDocumentoUtil.importDocProtocollati()) {
								// 24/04/2007
								// il documento da validare e' pervenuto con i
								// dati di protocollo gia' valorizzati: dato che
								// e' gia' stato protocollato
								// in un sistema centralizzato non BISOGNA
								// PROTOCOLLARLO UNA SECONDA VOLTA.
								doc.setProtocollatoEsternamente(true);
								BigDecimal numProtocollo = (BigDecimal) record.get("NUMPROTESTERNO");
								BigDecimal annoProtocollo = (BigDecimal) record.get("NUMANNOPROTESTERNO");
								// Il tracciato sare non prevede la data di
								// protocollo: USO QUELLA DI SISTEMA (POSSIBILE
								// PROBLEMA: validazione di movimento pervenuti
								// a fine anno ma validati ad inizio anno =>
								// anno 2007 data di prot. 05/1/2007 !!)
								String dataProtocollo = DateUtils.getNow();
								// Attenzione: il n. di prot. con cui il
								// documento verra' inserito nel db e'
								// "NumProtInserito" e non "NumProtocollo"
								// (leggere javadoc)
								doc.setNumProtInserito(numProtocollo);
								doc.setNumAnnoProt(annoProtocollo);
								doc.setDatProtocollazione(dataProtocollo);
							}

							doc.insert(this.transExecutor);
							// codice commentato il 27/04/2007
							// numProtPrecedente = doc.getNumProtInserito();
						} // validazioneMass e validazione manuale
						else { // Stiamo validando il singolo movimento
								// 27/04/2007: il caso della VALIDAZIONE DEL SINGOLO
								// MOVIMENTO e' stato riportato nel blocco di codice
								// superiore.
								// Questo e' il caso del normale inserimento di un
								// documento
								// DOCAREA 02/04/2007: si inserisce il documento
								// nella serviseResponse in modo che il Validator,
								// una volta committata la
								// transazione, possa recuperarlo e cancellare il
								// file temporaneo inviato a docarea(solo e solo se
								// e' abilitata questa protocollazione)
							ProtocolloDocumentoUtil.putInRequest(doc);
							doc.insert(this.transExecutor);
						}
						// 30/03/2007: nel caso di docarea bisogna cancellare il
						// file inviato (per i movimenti si tratta di un file
						// rtf vuoto)
						// ATTENZIONE: questa operazione deve avvenire subito
						// dopo la commit. Quindi qui non va bene. Va fatta nel
						// Validator.java
						// ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
					} // try
					catch (ProtocollaException ex) {
						return ProcessorsUtils.createResponse(prc, className, new Integer(ex.getMessageIdFail()), "",
								warnings, null);
					} catch (EMFUserError ue) {
						// non posso restituire esattamente il codice di errore
						// ricevuto perche' il processor non e' in grado
						// di creare un messaggio parametrico. Quindi utilizzo
						// il messaggio generico, e poi estraggo il messaggio
						// completo.
						String msg = ue.getDescription();
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DOC), msg, warnings, null);
					} catch (Exception emf) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DOC), "", warnings, null);
					} finally {
						monitor.stop();
					}

				}
			}
		}
		return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
	}

	private Documento settaDocumento(Map req) {
		Documento doc = new Documento();

		if (req.get("CODCPI") != null)
			doc.setCodCpi(req.get("CODCPI").toString());
		if (req.get("CDNLAVORATORE") != null)
			doc.setCdnLavoratore(new BigDecimal(req.get("CDNLAVORATORE").toString()));
		if (req.get("PRGAZIENDA") != null)
			doc.setPrgAzienda(new BigDecimal(req.get("PRGAZIENDA").toString()));
		if (req.get("PRGUNITA") != null && req.get("PRGUNITA").toString() != "") {
			doc.setPrgUnita(new BigDecimal(req.get("PRGUNITA").toString()));
		} else {
			if (req.get("PRGUNITAPRODUTTIVA") != null)
				doc.setPrgUnita(new BigDecimal(req.get("PRGUNITAPRODUTTIVA").toString()));
		}

		Object CODTIPOMOV = req.get("CODTIPOMOV");
		if (CODTIPOMOV != null) {
			String CODTIPOMOVstr = CODTIPOMOV.toString();
			if (CODTIPOMOVstr.equalsIgnoreCase("AVV")) {
				doc.setCodTipoDocumento("MVAVV");
			}
			if (CODTIPOMOVstr.equalsIgnoreCase("CES")) {
				doc.setCodTipoDocumento("MVCES");
			}
			if (CODTIPOMOVstr.equalsIgnoreCase("TRA")) {
				doc.setCodTipoDocumento("MVTRA");
			}
			if (CODTIPOMOVstr.equalsIgnoreCase("PRO")) {
				doc.setCodTipoDocumento("MVPRO");
			}
		}

		if (req.get("FLGAUTOCERTIFICAZIONE") != null)
			doc.setFlgAutocertificazione(req.get("FLGAUTOCERTIFICAZIONE").toString());
		if (req.get("STRDESCRIZIONE") != null)
			doc.setStrDescrizione(req.get("STRDESCRIZIONE").toString());
		if (req.get("DATINIZIOMOV") != null)
			doc.setDatInizio(req.get("DATINIZIOMOV").toString());
		if (req.get("STRENTERILASCIO") != null)
			doc.setStrEnteRilascio(req.get("STRENTERILASCIO").toString());
		if (req.get("FLGCODMONOIO") != null)
			doc.setCodMonoIO(req.get("FLGCODMONOIO").toString());
		if (req.get("DATCOMUNICAZ") != null)
			doc.setDatAcqril(req.get("DATCOMUNICAZ").toString());

		if (req.get("NUMANNOPROT") != null)
			doc.setNumAnnoProt(new BigDecimal(req.get("NUMANNOPROT").toString()));

		// GG 1-3-05 - num protocollo è ora NUMERICO
		Object numPRobj = req.get("NUMPROTOCOLLO");
		if (numPRobj != null) {
			BigDecimal numPR = null;
			if (numPRobj instanceof BigDecimal)
				numPR = (BigDecimal) numPRobj;
			else if (numPRobj instanceof Number)
				numPR = new BigDecimal(((Number) numPRobj).doubleValue());
			else
				numPR = new BigDecimal(numPRobj.toString());

			numPR = numPR.subtract(new BigDecimal(1)); // numPR := numPR - 1
			doc.setNumProtocollo(numPR);
		}

		if (req.get("DATAPROT") != null) {
			if (req.get("ORAPROT") != null) {
				String dataOraProt = req.get("DATAPROT").toString() + " " + req.get("ORAPROT").toString();
				doc.setDatProtocollazione(dataOraProt);
			}
		}
		if (req.get("TIPOPROT") != null)
			doc.setTipoProt(req.get("TIPOPROT").toString());

		if (req.get("STRNOTE") != null)
			doc.setStrNote(req.get("STRNOTE").toString());

		if (req.get("KLOCKPROT") != null)
			doc.setNumKeyLock(new BigDecimal(req.get("KLOCKPROT").toString()));

		doc.setCodMonoIO("I");
		doc.setPagina("MovDettaglioGeneraleConsultaPage");

		return doc;
	}

	/**
	 * @param i
	 */
	public void setNumeroDocumenti(int i) {
		numeroDocumenti = i;
	}

}