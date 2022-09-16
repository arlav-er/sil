package it.eng.sil.module.pi3;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.eng.sil.bean.Documento;

public class Pi3Exception extends Exception {

	private static final long serialVersionUID = -8851335816491114126L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Pi3Exception.class.getName());

	DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	public Pi3Exception(String message, Throwable cause, String typeError, CreaDocumentPi3Bean creaDocumentPi3Bean) {

		super(message, cause);

		_logger.error("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception ---> ERROR: " + message);
		_logger.debug(
				"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception ---> ERROR: PROTOCOLLAZIONE PI3 NON EFFETTUATA. SI PROCEDE AD INSERIRE I DATI IN INPUT SUL DB IN ATTESA DI UNA NUOVA PROTOCOLLAZIONE PI3 O DI UNA GESTIONE MANUALE DELLA PRATICA");

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

		try {

			/************************************************************************************
			 *
			 * Inserisce il documento Pi3 appena protocollato nel DB SPIL (AM_PROTOCOLLO_PITRE)
			 * 
			 ************************************************************************************/
			ProtocolloPi3Bean protocolloPi3Bean = new ProtocolloPi3Bean();

			protocolloPi3Bean.setStrSegnatura(null);
			protocolloPi3Bean.setStridDoc(null);
			protocolloPi3Bean.setDataProt(null);
			protocolloPi3Bean.setStrMittente(creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL());
			protocolloPi3Bean.setStrOggetto(creaDocumentPi3Bean.getDocumentPi3().getObject());
			protocolloPi3Bean.setPrgTitolario(new BigDecimal(creaDocumentPi3Bean.getPrgTitolario()));
			protocolloPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
			protocolloPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
			protocolloPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
			protocolloPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());
			protocolloPi3Bean.setStrNumPratica(creaDocumentPi3Bean.getNrPraticaSPIL());
			protocolloPi3Bean.setStrMittentePi3(null);

			BigDecimal prgDocPi3 = dbManager.insertProtocolloPi3(protocolloPi3Bean);
			_logger.debug(
					"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> insertProtocolloPi3 [MAIN DOCUMENT] [DB] -> isInsertProtocolloPi3OnSil -> prgDocPi3: "
							+ prgDocPi3);

			/***************************************************************************************************
			 *
			 * Inserisce il 'Main Document' Pi3 appena protocollato nel DB SPIL (AM_PROTOCOLLO_DOCUMENTO_PITRE)
			 * 
			 ****************************************************************************************************/
			ProtocolloDocumentoPi3Bean protocolloDocumentoPi3Bean = new ProtocolloDocumentoPi3Bean();

			protocolloDocumentoPi3Bean.setPrgProtPitre(prgDocPi3);
			protocolloDocumentoPi3Bean.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_SI);
			protocolloDocumentoPi3Bean.setPrgDocumento(creaDocumentPi3Bean.getDocumentSil().getPrgDocumento());
			protocolloDocumentoPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
			protocolloDocumentoPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
			protocolloDocumentoPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
			protocolloDocumentoPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());
			protocolloDocumentoPi3Bean.setDatInvio(new Date());

			// protocolloDocumentoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI);
			protocolloDocumentoPi3Bean.setCodStatoInvio(typeError);

			protocolloDocumentoPi3Bean.setFlgNotificaAnnullamento(null);

			boolean isInsertProtocolloDocumentoMainPi3OnSil = dbManager
					.insertProtocolloDocumentoPi3(protocolloDocumentoPi3Bean);
			_logger.debug(
					"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> insertProtocolloDocumentoPi3 [MAIN DOCUMENT] [DB] -> isInsertProtocolloDocumentoMainPi3OnSil: "
							+ isInsertProtocolloDocumentoMainPi3OnSil);

			/******************************************************************************************************************
			 *
			 * Inserisce gli Allegati (Documenti Associati della Pratica SPIL) nel DB SPIL
			 * (AM_PROTOCOLLO_DOCUMENTO_PITRE)
			 * 
			 *******************************************************************************************************************/
			if (creaDocumentPi3Bean.getLstDocumentiAllegati().size() == 0) {
				_logger.debug(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> nessun allegato da inserire nel DB SPIL (AM_PROTOCOLLO_DOCUMENTO_PITRE)");
			}

			int j = 0;
			for (Documento documentoSil : creaDocumentPi3Bean.getLstDocumentiAllegati()) {
				boolean allegatoProtocollatoPi3 = false;

				if (documentoSil.getTempFile().length() == 0) {
					_logger.warn("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> [DB] [allegato nr: "
							+ j + "] -> MAIN DOCUMENT SPIL PRGDOCUMENTO: "
							+ creaDocumentPi3Bean.getDocumentSil().getPrgDocumento().toString()
							+ " - FILE ALLEGATO SPIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
							+ " --> WARNING: ALLEGATO NON INSERITO NEL DB SPIL (AM_PROTOCOLLO_DOCUMENTO_PITRE) POICHE' HA FILE DI LUNGHEZZA ZERO E NON SAREBBE STATO INVIATO COME ALLEGATO NELLA PROTOCOLLAZIONE PI3");
				} else {
					allegatoProtocollatoPi3 = true;
				}

				if (allegatoProtocollatoPi3) {
					// Inserisce il 'File Allegato' Pi3 appena protocollato nel DB SPIL (AM_PROTOCOLLO_DOCUMENTO_PITRE)
					ProtocolloDocumentoPi3Bean protocolloDocumentoAllegatoPi3Bean = new ProtocolloDocumentoPi3Bean();

					protocolloDocumentoAllegatoPi3Bean.setPrgProtPitre(prgDocPi3);
					protocolloDocumentoAllegatoPi3Bean
							.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_NO);
					protocolloDocumentoAllegatoPi3Bean.setPrgDocumento(documentoSil.getPrgDocumento());
					protocolloDocumentoAllegatoPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
					protocolloDocumentoAllegatoPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
					protocolloDocumentoAllegatoPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
					protocolloDocumentoAllegatoPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());
					protocolloDocumentoAllegatoPi3Bean.setDatInvio(new Date());

					// protocolloDocumentoAllegatoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI);
					protocolloDocumentoAllegatoPi3Bean.setCodStatoInvio(typeError);

					protocolloDocumentoAllegatoPi3Bean.setFlgNotificaAnnullamento(null);

					boolean isInsertProtocolloDocumentoAllegatoPi3OnSil = dbManager
							.insertProtocolloDocumentoPi3(protocolloDocumentoAllegatoPi3Bean);

					_logger.debug(
							"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> insertProtocolloDocumentoPi3 [DB] [allegato nr: "
									+ j + "] -> MAIN DOCUMENT SPIL PRGDOCUMENTO: "
									+ creaDocumentPi3Bean.getDocumentSil().getPrgDocumento().toString()
									+ " - FILE ALLEGATO SPIL ID  (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
									+ " --> isInsertProtocolloDocumentoAllegatoPi3OnSil: "
									+ isInsertProtocolloDocumentoAllegatoPi3OnSil);
				}

				j++;
			}

			_logger.debug(
					"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception ---> ERROR: PROTOCOLLAZIONE PI3 NON EFFETTUATA. FINE INSERIMENTO DATI IN INPUT NEL DB SPIL EFFETTUATA");

		} catch (Exception ex) {
			_logger.error(
					"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception ---> ERROR: " + ex.getMessage());

			try {
				throw new Exception(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception ---> ERROR: " + ex.getMessage());
			} catch (Exception e) {
				_logger.error("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Pi3Exception -> Exception --> ERROR: "
						+ ex.getMessage());
			}

		}

	}
}
