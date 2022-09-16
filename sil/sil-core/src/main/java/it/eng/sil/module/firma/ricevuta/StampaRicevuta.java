package it.eng.sil.module.firma.ricevuta;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.util.UtilityHash;
import it.eng.sil.util.blen.StringUtils;

public class StampaRicevuta {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaRicevuta.class.getName());
	private String className = this.getClass().getName();

	public StampaRicevuta() {
		_logger.debug(className + ".StampaRicevuta() Invocato il costruttore");

	}

	public Documento getStampaRicevuta(TransactionQueryExecutor transExec, Documento docFirmato) {

		_logger.debug(className + ".getStampaRicevuta() INIZIO");
		// DataHandler dh = null;
		Documento doc = null;
		String codCpi = docFirmato.getCodCpi();
		_logger.debug(className + ".getStampaRicevuta() codCpi: " + codCpi);
		String chiaveTabella = docFirmato.getChiaveTabella();
		_logger.debug(className + ".getStampaRicevuta() chiaveTabella: " + chiaveTabella);
		BigDecimal cdnLavoratore = docFirmato.getCdnLavoratore();
		_logger.debug(className + ".getStampaRicevuta() cdnLavoratore: " + cdnLavoratore);
		// String tipoDoc = "DFG";
		String nomeDoc = "Ricevuta_Firma_Grafometrica.pdf";
		String descrDoc = "Ricevuta Firma Grafometrica";
		// rpt in report/patto
		String ccFile = "patto/ricevuta_firma_grafometrica_CC_2.rpt";
		// String ccFile = "C:/Users/minieri/Desktop/ricevuta_firma_grafometrica_CC_2.rpt";

		try {
			doc = new Documento();
			_logger.debug(className + ".getStampaRicevuta() Inizio creazione del documento di ricevuta ");
			String currentDate = DateUtils.getNow();

			doc.setCrystalClearRelativeReportFile(ccFile);
			doc.setCodTipoDocumento(
					Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO);
			doc.setCodCpi(codCpi);
			doc.setChiaveTabella(chiaveTabella);
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			// doc.setFlgDocAmm("");
			doc.setFlgDocIdentifP("N");
			// data inizio validità documento
			// data documento firmato o current Date?
			doc.setDatInizio(currentDate);
			doc.setStrNumDoc(null);
			doc.setStrEnteRilascio(codCpi);
			doc.setCodMonoIO("I");
			doc.setDatAcqril(currentDate);
			doc.setCodModalitaAcqril(null);
			doc.setCodTipoFile(null);
			doc.setStrNomeDoc(nomeDoc);
			doc.setDatFine(null);
			doc.setStrNote(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA);
			doc.setTipoProt("S");
			doc.setCodStatoAtto("PR");
			// da de_doc_tip
			SourceBean rowTipoDoc = (SourceBean) transExec.executeQuery("GET_TIPO_DOC",
					new Object[] {
							Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO },
					"SELECT");
			_logger.debug(className + ".getStampaRicevuta() rowTipoDoc: " + rowTipoDoc);

			if (rowTipoDoc == null) {
				throw new Exception("impossibile determinare il tipo documento");
			}
			rowTipoDoc = (rowTipoDoc.containsAttribute("ROW") ? (SourceBean) rowTipoDoc.getAttribute("ROW")
					: rowTipoDoc);
			descrDoc = (String) rowTipoDoc.getAttribute("descrizione");

			doc.setStrDescrizione(descrDoc);
			doc.setFlgAutocertificazione("N");
			doc.setPagina(docFirmato.getPagina());
			// chiedere fabiana
			doc.setCdnUtIns(docFirmato.getCdnUtIns());
			doc.setCdnUtMod(docFirmato.getCdnUtMod());

			_logger.debug(className + ".getStampaRicevuta() determinazione del protocollo da assegnare ");

			SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile protocollare il documento di identificazione");
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

			_logger.debug(
					className + ".getStampaRicevuta() protocollo: " + numAnnoProt + numProtocollo + datProtocollazione);
			doc.setNumAnnoProt(numAnnoProt);
			doc.setNumProtocollo(numProtocollo);
			doc.setDatProtocollazione(datProtocollazione);

			SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dataArrivoDoc = fd.format(new Date());
			String dataDoc = dataArrivoDoc.substring(0, 10);
			String oraDoc = dataArrivoDoc.substring(11);

			File fileFirmato = docFirmato.getTempFile();
			_logger.debug(className + ".getStampaRicevuta() prelevo il file firmato e ne calcolo hash");
			String hashDocumentoFirmato = UtilityHash.generaHash(fileFirmato);
			_logger.debug(className + ".getStampaRicevuta() hash: " + hashDocumentoFirmato);
			// parametri per il report
			Map prompts = new HashMap();

			String nomeDocumento = "dati firma grafometrica";
			if (!StringUtils.isEmpty(docFirmato.getStrDescrizione())) {
				nomeDocumento = docFirmato.getStrDescrizione();
			} else {
				if (!StringUtils.isEmpty(docFirmato.getCodTipoDocumento())) {
					SourceBean row = (SourceBean) transExec.executeQuery("GET_TIPODOC",
							new Object[] { docFirmato.getCodTipoDocumento() }, "SELECT");
					if (row != null) {
						String desc = (String) row.getAttribute("row.descrizione");

						if (!StringUtils.isEmpty(desc)) {
							nomeDocumento = desc;
						} else {
							String desc2 = (String) row.getAttribute("descrizione");

							if (!StringUtils.isEmpty(desc2)) {
								nomeDocumento = desc2;
							}
						}
					}
				}
			}

			String numProtDocumValue = "";
			if (docFirmato.getNumAnnoProt() != null && docFirmato.getNumProtocollo() != null) {
				numProtDocumValue = docFirmato.getNumAnnoProt().toString() + "/" + docFirmato.getNumProtocollo();
			}
			_logger.debug(className + ".numProtDocumValue (docFirmato): " + numProtDocumValue);

			prompts.put("dataInvioDocumento", dataDoc);
			prompts.put("oraInvioDocumento", oraDoc);
			prompts.put("oggettoValue", nomeDocumento);
			prompts.put("numProtDocumValue", numProtDocumValue);
			prompts.put("hashDocumentoValue", hashDocumentoFirmato);
			prompts.put("cdnLavoratore", cdnLavoratore.toString());

			doc.setCrystalClearPromptFields(prompts);
			_logger.debug(className + ".getStampaRicevuta() inserimento su am_documento ricevuta creata ");
			doc.insert(transExec);

			// File temp = doc.getTempFile();

			// dh = new DataHandler(new FileDataSource(temp));

		} catch (Exception e) {
			_logger.error(className + "[getStampaRicevuta] -> Error: " + e.getMessage(), e);
			e.printStackTrace();
		}
		_logger.debug(className + ".getStampaRicevuta() FINE");
		return doc;
	}

	public void inserisciAllegatoDocumento(Documento ricevuta, TransactionQueryExecutor txExec, String prgDocPadre)
			throws Exception {
		_logger.debug(className + ".inserisciAllegatoDocumento() INIZIO");
		Object params[] = new Object[6];
		params[0] = prgDocPadre;
		params[1] = ricevuta.getPrgDocumento();
		params[2] = "N";
		params[3] = "N";
		params[4] = ricevuta.getCdnUtIns();
		params[5] = ricevuta.getCdnUtIns();
		_logger.debug("params[0]: " + params[0]);
		_logger.debug("params[1]: " + params[1]);
		_logger.debug("params[2]: " + params[2]);
		_logger.debug("params[3]: " + params[3]);
		_logger.debug("params[4]: " + params[4]);
		_logger.debug("params[5]: " + params[5]);
		_logger.debug(className + ".inserisciAllegatoDocumento() INSERT_DOC_ALLEGATO_STAMPA_PARAM");
		Boolean res = (Boolean) txExec.executeQuery("INSERT_DOC_ALLEGATO_STAMPA_PARAM", params, "INSERT");
		_logger.debug(className
				+ ".inserisciAllegatoDocumento() risultato della query INSERT_DOC_ALLEGATO_STAMPA_PARAM: " + res);
		if (!res.booleanValue())
			throw new Exception("Impossibile inserire il collegamento tra documento padre e allegato");
		_logger.debug(className + ".inserisciAllegatoDocumento() FINE");
	}
}
