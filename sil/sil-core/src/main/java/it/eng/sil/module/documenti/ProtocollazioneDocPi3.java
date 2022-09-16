package it.eng.sil.module.documenti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;
import com.nttdata._2012.Pi3.Document;
import com.nttdata._2012.Pi3.Note;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.consenso.Consenso;
import it.eng.sil.module.consenso.ConsensoFirmaBean;
import it.eng.sil.module.consenso.GConstants;
import it.eng.sil.module.pi3.BatchProtocollazioneDifferitaBean;
import it.eng.sil.module.pi3.CreaDocumentPi3Bean;
import it.eng.sil.module.pi3.InvioProtocollazioneDifferitaBean;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloDocumentoPi3Bean;
import it.eng.sil.module.pi3.ProtocolloPi3Bean;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.module.pi3.ProtocolloPi3Manager;
import it.eng.sil.module.pi3.ProtocolloPi3Utility;
import it.eng.sil.module.pi3.UtentePi3Bean;
import it.eng.sil.security.User;
import it.eng.sil.util.UtilityHash;

public class ProtocollazioneDocPi3 extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocollazioneDocPi3.class.getName());

	private final String className = StringUtils.getClassName(this);

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug(className + ".service() INIZIO");

		final String FORMATO_DATA = "dd/MM/yyyy";
		SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

		ProtocolloPi3Manager protocolloPi3Manager = new ProtocolloPi3Manager();
		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
		ArrayList<Documento> lstDocAllegati = new ArrayList<Documento>();

		try {

			// Parametri non recuperabili in altro modo nella jsp
			// per via del multipart...

			_logger.debug("RECUPERA IL DOCUMENTO PRINCIPALE");
			// RECUPERA IL DOCUMENTO PRINCIPALE DELLA DID
			BigDecimal prgDocumento = new BigDecimal((String) serviceRequest.getAttribute("prgDocumento"));
			_logger.debug("prgDocumento : " + prgDocumento);
			Documento documento = new Documento(prgDocumento);

			// LEGGE IL FILE E LO METTE IN TEMPFILE DI DOCUMENTO
			documento = setTempPdfFileToDocument(documento);

			// RECUPERA LA TIPOLOGIA DEL DOCUMENTO 'in ENTRATA = 'A', in USCITA = 'P', INTERNO NON DEFINITO = 'I'
			// BigDecimal prgTemplateStampa = dbManager.getPrgTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_DID);

			String documentType = (String) serviceRequest.getAttribute("DOCUMENTTYPE");
			_logger.debug("docType: " + documentType);
			BigDecimal prgTemplateStampa = new BigDecimal(0);

			if (!StringUtils.isEmpty(documentType))
				prgTemplateStampa = dbManager
						.getPrgTemplateStampa((String) serviceRequest.getAttribute("DOCUMENTTYPE"));

			_logger.warn("prgTemplateStampa : " + prgTemplateStampa);
			// throw new Exception();

			String descrizioneTrattamento = null;
			String descrizioneTipoProt = null;
			String codeTipoDelTrattamento = null;
			String docType = null;
			if (prgTemplateStampa.compareTo(new BigDecimal(0)) != 0) {
				_logger.debug("prgTemplateStampa : " + prgTemplateStampa);
				docType = dbManager.getDocumentType(prgTemplateStampa.toString());

				// RECUPERA LA DESCRIZIONE DEL TRATTAMENTO
				codeTipoDelTrattamento = dbManager.getCodeTipoTrattamento(prgTemplateStampa.toString());
				descrizioneTipoProt = dbManager.getDescTypeProt(prgTemplateStampa.toString());
				descrizioneTrattamento = dbManager.getDescTipoTrattamento(prgTemplateStampa.toString());

				boolean isDocumentoFirmato = dbManager.isAllegatoDocumentoFirmato(prgDocumento.toString());
				// cablo il docType
				// docType = "G";
				// Verifica se l'operatore ha inviato la richiesta della Protocollazione Pi3
				String actionPi3 = (String) serviceRequest.getAttribute("actionPi3");
				_logger.debug("actionPi3 : " + actionPi3);
				boolean isDocumentFirmabile = dbManager.isDocumentTypeGraphSignature(prgTemplateStampa.toString());
				boolean isConsensoAttivo = false;

				_logger.debug("isDocumentFirmabile : " + isDocumentFirmabile);

				String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

				Consenso consenso = new Consenso(null);
				ConsensoFirmaBean cfb = consenso.getConsensoFirma(cdnLavoratore);

				String codiceConsenso = cfb.getCodiceStatoConsenso();
				if (StringUtils.isEmpty(codiceConsenso)) {
					codiceConsenso = GConstants.CONSENSO_ASSENTE_CODICE;
				}

				String statoConsenso = consenso.getStatoConsensoFirma(codiceConsenso);

				serviceResponse.setAttribute("statoConsenso", statoConsenso);

				if (!StringUtils.isEmpty("CONSENSO"))
					isConsensoAttivo = Boolean.parseBoolean("" + serviceRequest.getAttribute("CONSENSO"));
				_logger.debug("isConsensoAttivo : " + isConsensoAttivo);

				serviceResponse.setAttribute("docType", docType);
				serviceResponse.setAttribute("descrizioneTipoProt", descrizioneTipoProt);
				serviceResponse.setAttribute("CODTIPOTRATTAMENTO", docType);
				if (StringUtils.isEmpty(descrizioneTrattamento))
					serviceResponse.setAttribute("DESCRIZIONETRATTAMENTO", "");
				else
					serviceResponse.setAttribute("DESCRIZIONETRATTAMENTO", descrizioneTrattamento);
				serviceResponse.setAttribute("cdnLavoratore", serviceRequest.getAttribute("cdnLavoratore"));

				serviceResponse.setAttribute("dataacqril", documento.getDatAcqril());
				serviceResponse.setAttribute("oggetto", documento.getStrDescrizione());
				serviceResponse.setAttribute("documentoSIL", documento);

				serviceResponse.setAttribute("IS_DOC_FIRMATO", isDocumentoFirmato);
				serviceResponse.setAttribute("IS_DOC_FIRMABILE", isDocumentFirmabile);
				String codiceFromTitolario = null;

				if (StringUtils.isEmpty(actionPi3)) {

					// serviceResponse.setAttribute("PROTOCOLLAZIONE_PI3", "FALSE" );

					// RECUPERA IL NUMERO PRATICA PER VERIFICARE SE LA PRATICA E' STATA GIA' INVIATA ALLA
					// PROTOCOLLAZIONE PI3
					String nrPratica = documento.getNumAnnoProt() + "/" + documento.getNumProtocollo();

					boolean isPraticaAlreadyProcessed = dbManager.isPraticaAlreadyProcessedIntoPi3(nrPratica);
					String isPraticaAlreadyProcessedStr = "FALSE";
					String dataInvio = "";
					String dataProtocollazione = "";
					ProtocolloPi3Bean protocolloPi3Bean = null;
					ProtocolloDocumentoPi3Bean protocolloDocumentoPi3Bean = null;
					String segnature = "";
					String statoInvioDocumento = "";

					if (isPraticaAlreadyProcessed) {
						isPraticaAlreadyProcessedStr = "TRUE";

						protocolloPi3Bean = dbManager.getProtocolloPi3ByNrPratica(nrPratica);
						protocolloDocumentoPi3Bean = dbManager
								.getProtocolloMainDocumentoPi3ByPrgDocumento(prgDocumento.toString());

						if (protocolloPi3Bean.getDataProt() != null) {
							dataProtocollazione = df.format(protocolloPi3Bean.getDataProt());
						}

						if (protocolloDocumentoPi3Bean.getDatInvio() != null) {
							dataInvio = df.format(protocolloDocumentoPi3Bean.getDatInvio());
						}

						if (!StringUtils.isEmpty(protocolloPi3Bean.getStrSegnatura())) {
							segnature = protocolloPi3Bean.getStrSegnatura();
						}

						statoInvioDocumento = decodificaStatoInvioDocumento(
								protocolloDocumentoPi3Bean.getCodStatoInvio());

					}
					serviceResponse.setAttribute("isPraticaAlreadyProcessed", isPraticaAlreadyProcessedStr);
					serviceResponse.setAttribute("numeroProtocollo", segnature);
					serviceResponse.setAttribute("dataProtocollo", dataProtocollazione);
					serviceResponse.setAttribute("dataInvio", dataInvio);
					serviceResponse.setAttribute("statoDocInviato", statoInvioDocumento);

					// RECUPERA IL CODICE DI CLASSIFICAZIONE DEL TITOLARIO DA AM_PROTOCOLLO_PI3
					// EXIST_DOCUMENT_PI3
					codiceFromTitolario = dbManager
							.getCodiceFromPi3((String) serviceRequest.getAttribute("annoprotNum"));
					if (StringUtils.isEmpty(codiceFromTitolario)) {
						// RECUPERA IL CODICE DI CLASSIFICAZIONE DEL TITOLARIO
						BigDecimal prgConfigProtocollo = dbManager.getPrgConfigProtocolloFromTemplateStampa(
								(String) serviceRequest.getAttribute("DOCUMENTTYPE"));
						// BigDecimal prgConfigProtocollo =
						// dbManager.getPrgConfigProtocolloFromTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_DID);

						if (prgConfigProtocollo != null)
							codiceFromTitolario = dbManager.getCodiceFromTitolario(prgConfigProtocollo.toString());
					}
					if (!StringUtils.isEmpty(codiceFromTitolario))
						serviceResponse.setAttribute("codicePAT", codiceFromTitolario);

				} else {

					/*******************************************************************
					 *
					 * RECUPERO DATI INPUT PER PROTOCOLLAZIONE PI3
					 *
					 *******************************************************************/

					// String numeroProtocollPraticaSil = (String) serviceRequest.getAttribute("NUMPROT");
					String codicePAT = (String) serviceRequest.getAttribute("codicePAT");
					_logger.debug("codicePAT : " + codicePAT);
					// BigDecimal prgDocumentoAction = new
					// BigDecimal((String)serviceRequest.getAttribute("prgDocumento"));
					// Documento documentoAction = new Documento(prgDocumentoAction);
					BigDecimal numeroAnnoProtocolloSil = documento.getNumAnnoProt();
					BigDecimal numeroProtocolloSil = documento.getNumProtocollo();
					_logger.debug("numeroAnnoProtocolloSil : " + numeroAnnoProtocolloSil);
					_logger.debug("numeroProtocolloSil : " + numeroProtocolloSil);
					// RECUPERA L'UTENTE MITTENTE (SIA ESSO UN LAVORATORE/AZIENDA/UNITA AZIENDA/CPI)
					// --- serve recuperare i seguenti campi obbligatori:
					// NOME
					// COGNOME
					// CODICE RUBRICA PI3 (cndLavoratore/prgAzienda/prgUnita/codCpi)
					// DESCRIZIONE PI3 (Nome + Cognome o Ragione Sociale)
					// TYPE PI3 ('P' = persona o 'U' unita organizzativa)
					// String typeUser = getTypeUser(serviceRequest, documento);
					String idUser = getIdUser(serviceRequest, documento);
					// RECUPERA GLI UTENTI DESTINATARI (NESSUN DESTINATARIO PER LA DID)
					// ...

					ArrayList<UtentePi3Bean> lstUtentiDestinatari = null;
					String typeUserDest = null;
					String typeUser = null;
					if (documentType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_RICHIESTA_DOCUMENTO)) {
						typeUserDest = Pi3Constants.PI3_SPIL_UTENTE_BEAN_CPI;
						// idUser = documento.getCodCpi();
						idUser = getCodeCPI(cdnLavoratore);
					} else if (documentType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_SICUREZZA_LAVORO)) {
						typeUserDest = Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE;
					} else if (documentType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_SI68)) {
						typeUserDest = Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE;
					} else if (documentType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_DID_ANNUALE)) {
						typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE;
					} else if (documentType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_RICHIESTA_TRASFERIMENTO)) {
						typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE;
					}

					UtentePi3Bean utenteDestinatario = null;
					lstUtentiDestinatari = new ArrayList<UtentePi3Bean>();
					if (typeUserDest != null) {
						// utenteDestinatario = getUtentePi3Bean(typeUserDest, idUser, documento.getCodCpi());
						utenteDestinatario = getUtentePi3Bean(typeUserDest, idUser, getCodeCPI(cdnLavoratore));
						utenteDestinatario.setFlgDestinatarioPrincipale("S");

					}
					UtentePi3Bean utenteMittente = null;
					if (typeUser != null)
						// utenteMittente = getUtentePi3Bean(typeUser, idUser, documento.getCodCpi());
						utenteMittente = getUtentePi3Bean(typeUser, idUser, getCodeCPI(cdnLavoratore));
					_logger.debug("utenteMittente : " + utenteMittente);

					if (utenteDestinatario != null)
						lstUtentiDestinatari.add(utenteDestinatario);

					// RECUPERA I DOCUMENTI ALLEGATI (SENZA INCLUDERE IL 'MAIN DOCUMENT')
					Vector<SourceBean> lstAllegati = getResponseContainer().getServiceResponse()
							.getAttributeAsVector("M_GetDocAssociatiPi3.ROWS.ROW");
					Iterator<SourceBean> iRows = lstAllegati.iterator();
					while (iRows.hasNext()) {
						SourceBean sourceBean = (SourceBean) iRows.next();
						BigDecimal prgDocAllegato = (BigDecimal) sourceBean.getAttribute("PRGDOCUMENTO");
						String strPrgDocAllegato = prgDocAllegato.toString();

						if (!strPrgDocAllegato.equalsIgnoreCase(prgDocumento.toString())) {
							Documento docAllegato = new Documento(prgDocAllegato);
							// LEGGE IL FILE E LO METTE IN TEMPFILE DI DOCUMENTO
							docAllegato = setTempPdfFileToDocument(docAllegato);
							lstDocAllegati.add(docAllegato);
						}
					}

					// RECUPERA I DOCUMENTI RELATIVI AL LAVORATORE (ALLEGATI E DID COMPRESA)
					// getRequestContainer().getServiceRequest().setAttribute("strChiaveTabella",
					// documento.getStrChiaveTabella());
					// ArrayList<Documento> listaDocumentiDiD = dbManager.getListaDocumentiDiD(getRequestContainer(),
					// (String) serviceRequest.getAttribute("cdnLavoratore"));

					/*******************************************************************
					 *
					 * INIZIALIZZA IL BEAN DA INVIARE PER LA PROTOCOLLAZIONE PI3
					 *
					 *******************************************************************/

					// INIZIALIZZA IL BEAN DA INVIARE PER LA PROTOCOLLAZIONE PI3
					CreaDocumentPi3Bean creaDocumentPi3Bean = new CreaDocumentPi3Bean();

					// 01. IMPOSTA NR PRATICA SPIL
					creaDocumentPi3Bean.setNrPraticaSPIL(
							numeroAnnoProtocolloSil.toString() + "/" + numeroProtocolloSil.toString());

					// 02. IMPOSTA IL MITTENTE
					creaDocumentPi3Bean.setUtenteMittente(utenteMittente);

					// 03. IMPOSTA DESTINATARIO/I (NESSUN DESTINATARIO PER LA DID)
					creaDocumentPi3Bean.setLstUtentiDestinatari(lstUtentiDestinatari);

					// 04. IMPOSTA IL DOCUMENTO SIL
					creaDocumentPi3Bean.setDocumentSil(documento);

					// 06. IMPOSTA I VARI CdnUtins, CdnUtMod, DtmIns e DtmMod
					creaDocumentPi3Bean.setCdnUtins(documento.getCdnUtIns());
					creaDocumentPi3Bean.setCdnUtMod(documento.getCdnUtMod());
					if (documento.getDtmIns() != null) {
						creaDocumentPi3Bean.setDtMins(df.parse(documento.getDtmIns()));
					}
					if (documento.getDtmMod() != null) {
						creaDocumentPi3Bean.setDtmMod(df.parse(documento.getDtmMod()));
					}

					// 07. INIZIALIZZA UN DOCUMENT PI3 VUOTO
					creaDocumentPi3Bean.setDocumentPi3(new Document());

					// 07.1 IMPOSTA IL DOCUMENT TYPE
					creaDocumentPi3Bean.getDocumentPi3().setDocumentType(docType);

					// 07.2 IMPOSTA IL DOCUMENT OBJECT
					creaDocumentPi3Bean.getDocumentPi3()
							.setObject(documento.getStrDescrizione() + " - id SPIL " + documento.getPrgDocumento());

					/*
					 * // 07.3a FILTRA EVENTUALMENTE DALLA LISTA ALLEGATI LA RICEVUTA DELLA FIRMA GRAFOMETRICA if
					 * (!ProtocolloPi3Utility.isMainDocumentFullSignature(creaDocumentPi3Bean)){ for (Documento
					 * allegato: lstDocAllegati) { if(allegato.getCodTipoDocumento().equalsIgnoreCase(Pi3Constants.
					 * PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO)){
					 * lstDocAllegati.remove(allegato); } } }
					 */

					// 07.3 IMPOSTA IL DOCUMENT NOTE
					String numPraticaSPIL = numeroAnnoProtocolloSil.toString() + "/" + numeroProtocolloSil.toString();
					Note[] note = new Note[1];
					note[0] = new Note();
					String noteDescription = "Numero Pratica SPIL: " + numPraticaSPIL;

					int k = 0;
					for (Documento allegato : lstDocAllegati) {

						if (allegato.getTempFile().length() == 0) {
							k++;
							if (k == 1) {
								noteDescription = noteDescription + ". \n Elenco Allegati:";
							}

							noteDescription = noteDescription + ". \n Presa visione Allegato (" + k + "): "
									+ allegato.getStrTipoDoc();
						}

					}
					note[0].setDescription(noteDescription);
					creaDocumentPi3Bean.getDocumentPi3().setNote(note);

					// 08. IMPOSTA IL CODE/VALUE PROJECT/TITOLARIO
					if (StringUtils.isEmpty(codicePAT)) {
						codicePAT = Pi3Constants.PI3_CODE_PAT_FASCICOLO_TITOLARIO_DEFAULT_VALUE;
					}
					creaDocumentPi3Bean.setCodeProject(codicePAT);

					// 09. IMPOSTA L'ID/PRG DEL TITOLARIO
					BigDecimal prgTitolario = dbManager.getPrgFromTitolario(codicePAT);
					creaDocumentPi3Bean.setPrgTitolario(prgTitolario.toString());

					// 10. IMPOSTA SE IL DOCUMENTO E' FIRMABILE O NO (NEL CASO PREDISPORRE 'PREDISPOSED' NELLA
					// PROTOCOLLAZIONE SENZA INVIARE IL FILE PDF)
					creaDocumentPi3Bean.setDocumentoFirmabile(isDocumentFirmabile);
					creaDocumentPi3Bean.setConsensoAttivo(isConsensoAttivo);
					creaDocumentPi3Bean.setDocumentoFirmato(isDocumentoFirmato);

					// 11. IMPOSTA IL TIPO DI DOCUMENTO
					creaDocumentPi3Bean.setDocumentType((String) serviceRequest.getAttribute("DOCUMENTTYPE"));
					// creaDocumentPi3Bean.getDocumentPi3().setPredisposed(false);

					// 11b. IMPOSTA IL TIPO DEL TRATTAMENTO
					creaDocumentPi3Bean.setTipoDelTrattamento(codeTipoDelTrattamento);

					// 11c. IMPOSTA IL TIPO DI DOCUMENTO ('IN ENTRATA', 'IN USCITA' o 'REPERTORIATO')
					// ('IN ENTRATA' : codTipoProtocollazione = 'A' and codTipoTrattamento = 'P')
					// ('IN USCITA' : codTipoProtocollazione = 'P' and codTipoTrattamento = 'P')
					// ('REPERTORIO' : codTipoProtocollazione = 'G' and codTipoTrattamento = 'R')
					if (docType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_ENTRATA)
							&& codeTipoDelTrattamento.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_PROTOCOLLATO)) {
						creaDocumentPi3Bean.setDocInEntrata(true);
					}

					if (docType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_USCITA)
							&& codeTipoDelTrattamento.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_PROTOCOLLATO)) {
						creaDocumentPi3Bean.setDocInUscita(true);
					}

					// 12. IMPOSTA I DATI IN XML IN UN FILE E LO AGGIUNGE ALLA LISTA ALLEGATI
					if (ProtocolloPi3Utility.isMainDocumentFullSignature(creaDocumentPi3Bean)) {
						HashMap<String, String> beanDatiInput = new HashMap<String, String>();
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER,
								GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER_VALUE);
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO,
								documento.getCodTipoDocumento());
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_ID_DOCUMENTO,
								documento.getPrgDocumento().toString());

						String nomeDocumento = documento.getStrDescrizione();
						if (StringUtils.isEmpty(nomeDocumento)) {
							nomeDocumento = dbManager.getDescTipoDocumento(prgTemplateStampa.toString());
						}
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_DOCUMENTO, nomeDocumento);

						if (documento.getTempFile() != null) {
							String hashDocumentoFirmato = UtilityHash.generaHash(documento.getTempFile());
							beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_HASH_DOCUMENTO,
									hashDocumentoFirmato);
						} else {
							beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_HASH_DOCUMENTO, "no hash");
						}

						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_LAVORATORE,
								utenteMittente.getNome());
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_COGNOME_LAVORATORE,
								utenteMittente.getCognome());
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_FISCALE_LAVORATORE,
								utenteMittente.getCodiceFiscale());

						User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_OPERATORE,
								user.getUsername());
						beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_IP_OPERATORE, getIpOperatore());

						String xmlInputData = createInputXml(beanDatiInput);

						File fileXml = new File("temp-xml.xml");
						FileOutputStream fop = new FileOutputStream(fileXml);
						if (!fileXml.exists()) {
							fileXml.createNewFile();
						}
						fop.write(xmlInputData.getBytes());
						fop.flush();
						fop.close();

						Documento docAllegatoXML = new Documento();
						docAllegatoXML.setPrgDocumento(documento.getPrgDocumento()); // PRG_DOCUMENT DEL MAIN DOCUMENT
						docAllegatoXML.setTempFile(fileXml);
						docAllegatoXML.setStrNomeDoc(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML);
						docAllegatoXML.setStrNote(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML); // PROPRIETA' DI
																										// 'APPOGGIO'
																										// PER
																										// IDENTIFICARE
																										// IL FILE
																										// ALLEGATO COME
																										// XML

						lstDocAllegati.add(docAllegatoXML);
					}

					// 12b. IMPOSTA IL TIPO DI DOCUMENTO ('IN ENTRATA', 'IN USCITA' o 'REPERTORIATO')
					// ('IN ENTRATA' : codTipoProtocollazione = 'A' and codTipoTrattamento = 'P')
					// ('IN USCITA' : codTipoProtocollazione = 'P' and codTipoTrattamento = 'P')
					// ('REPERTORIO' : codTipoProtocollazione = 'G' and codTipoTrattamento = 'R')
					if (docType.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_NON_PROTOCOLLATO)
							&& codeTipoDelTrattamento.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_REPERTORIO)) {
						creaDocumentPi3Bean.setDocRepertoriato(true);

						// solo sul patto
						// 12c IMPOSTA IL DOCUMENT TEMPLATEDOC
						creaDocumentPi3Bean.setTemplateDoc(true);
					}

					// 13. IMPOSTA I VARI DOCUMENTI ALLEGATI
					creaDocumentPi3Bean.setLstDocumentiAllegati(lstDocAllegati);

					/*******************************************************************
					 * PARTE IL PROCESSO DI PROTOCOLLAZIONE PI3 (WEB SERVICES & DB)
					 *******************************************************************/
					// 13 bis VERIFICA SE LA PRATICA DEVE ESSERE PREPARATA PER L'INVIO IN DIFFERITA O IN TEMPO REALE
					BatchProtocollazioneDifferitaBean batchProtocollazioneDifferitaBean = dbManager
							.getBatchProtocollazioneDifferitaByNomeBatch(
									Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_NOME_BATCH);
					if (!StringUtils.isEmpty(batchProtocollazioneDifferitaBean.getFlgProtDiff())) {

						/*******************************************************************
						 * MODALITA' INVIO IN DIFFERITA CON SERIALIZZAZIONE DEL BEAN
						 *******************************************************************/
						if (batchProtocollazioneDifferitaBean.getFlgProtDiff()
								.equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_SI)) {

							// 13.1 VERIFICA SE LA PRATICA E' PRESENTE NELLA TABELLA 'TS_INVIO_PROTOCOLLO_DIFFERITA'
							InvioProtocollazioneDifferitaBean invioProtocollazioneDifferitaBeanTemp = new InvioProtocollazioneDifferitaBean();
							invioProtocollazioneDifferitaBeanTemp = dbManager
									.getInvioProtocollazioneDifferitaByNrPratica(numPraticaSPIL);

							if (!StringUtils.isEmpty(invioProtocollazioneDifferitaBeanTemp.getStrNumPratica())) {
								if (invioProtocollazioneDifferitaBeanTemp.getStrNumPratica()
										.equalsIgnoreCase(numPraticaSPIL)) {
									throw new Exception("La Pratica [" + numPraticaSPIL
											+ "] e' stata gia' preparata all'invio in precedenza");
								}
							}

							// 13.2 SERIALIZZA IL BEAN 'creaDocumentPi3Bean' PER L'INVIO DEL PROTOCOLLO IN VERSIONE
							// DIFFERITA
							File pi3BeanSerializated = ProtocolloPi3Utility.serializePi3Bean(creaDocumentPi3Bean);

							// 13.3 INSERISCE UN NUOVO RECORD DELLA PRATICA NELLA TABELLA
							// 'TS_INVIO_PROTOCOLLO_DIFFERITA'
							InvioProtocollazioneDifferitaBean invioProtocollazioneDifferitaBean = new InvioProtocollazioneDifferitaBean();
							invioProtocollazioneDifferitaBean.setStrNumPratica(numPraticaSPIL);
							invioProtocollazioneDifferitaBean
									.setCodStato(Pi3Constants.PI3_INVIO_PROTOCOLLAZIONE_DIFFERITA_STATE_DA_ELABORARE);
							dbManager.insertInvioProtocollazioneDifferita(invioProtocollazioneDifferitaBean);

							// 12.4 RECUPERA IL RECORD DEL BEAN SERIALIZZATO APPENA INSERITO NELLA TABELLA
							// 'TS_INVIO_PROTOCOLLO_DIFFERITA'
							invioProtocollazioneDifferitaBean = dbManager
									.getInvioProtocollazioneDifferitaByNrPratica(numPraticaSPIL);

							// 13.5 AGGIORNA IL CAMPO BLOB - DEL BEAN SERIALIZZATO - DEL RECORD APPENA INSERITO NELLA
							// TABELLA 'TS_INVIO_PROTOCOLLO_DIFFERITA'
							boolean isBLOBwrited = dbManager.writeBLOBean(
									invioProtocollazioneDifferitaBean.getPrgProtDifferita(), pi3BeanSerializated);

							// 13.6 Prova...
							// BatchInvioProtocollazionePi3Differita invioProtocollazionePi3Differita = new
							// BatchInvioProtocollazionePi3Differita();
							// invioProtocollazionePi3Differita.perform(new Date(), 0);

						} else {

							/*******************************************************************
							 * MODALITA' INVIO IN TEMPO REALE DEL PROCESSO
							 *******************************************************************/
							protocolloPi3Manager.inviaProtocolloPi3(creaDocumentPi3Bean);

						}

						// VALORI 'VUOTI' DA IMPOSTARE ALLA PAGINA JSP
						setEmptyDataToJsp(serviceResponse);

					} else {
						throw new Exception(
								"Non e' stata impostata nel sistema la modalita' di invio della Protocollazione Pi3: differita o tempo reale ");
					}
					// protocolloPi3Manager.inviaProtocolloPi3(creaDocumentPi3Bean);

					// RISPOSTA 'OK' ALLA PAGINA JSP
					serviceResponse.setAttribute("PROTOCOLLAZIONE_PI3", "TRUE");
					// VALORI 'VUOTI' DA IMPOSTARE ALLA PAGINA JSP
					setEmptyDataToJsp(serviceResponse);
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			// RISPOSTA 'KO' ALLA PAGINA JSP

			serviceResponse.setAttribute("PROTOCOLLAZIONE_PI3", "ERROR");
			serviceResponse.setAttribute("PROTOCOLLAZIONE_PI3_ERROR", e.getMessage());
		}

		_logger.debug(className + ".service() FINE");

	}

	private void setEmptyDataToJsp(SourceBean serviceResponse) throws Exception {
		// serviceResponse.setAttribute("isPraticaAlreadyProcessed", "" );
		serviceResponse.setAttribute("numeroProtocollo", "");
		serviceResponse.setAttribute("dataProtocollo", "");
		serviceResponse.setAttribute("dataInvio", "");
		serviceResponse.setAttribute("statoDocInviato", "");
	}

	private String getTypeUser(SourceBean serviceRequest, Documento documento) throws Exception {
		String typeUser = null;

		String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
		String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
		String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
		// String codCPI = documento.getCodCpi();
		String codCPI = getCodeCPI(cdnLavoratore);

		if (!StringUtils.isEmpty(cdnLavoratore)) {
			typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE;
		} else if (!StringUtils.isEmpty(prgAzienda)) {
			typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_AZIENDA;
		} else if (!StringUtils.isEmpty(prgUnita)) {
			typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_UNITA_AZIENDA;
		} else if (!StringUtils.isEmpty(codCPI)) {
			typeUser = Pi3Constants.PI3_SPIL_UTENTE_BEAN_CPI;
		}

		return typeUser;
	}

	private String getIdUser(SourceBean serviceRequest, Documento documento) throws Exception {
		String idUser = null;

		String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
		String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
		String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
		// String codCPI = documento.getCodCpi();
		String codCPI = getCodeCPI(cdnLavoratore);

		if (!StringUtils.isEmpty(cdnLavoratore)) {
			idUser = cdnLavoratore;
		} else if (!StringUtils.isEmpty(prgAzienda)) {
			idUser = prgAzienda;
		} else if (!StringUtils.isEmpty(prgUnita)) {
			idUser = prgUnita;
		} else if (!StringUtils.isEmpty(codCPI)) {
			idUser = codCPI;
		}

		return idUser;
	}

	private UtentePi3Bean getUtentePi3Bean(String typeUtente, String codeUtente, String codeUtente2) throws Exception {

		UtentePi3Bean utentePi3Bean = null;
		String codeUtenteValue = codeUtente;
		String codeUtenteValue2 = codeUtente2;

		if (StringUtils.isEmpty(codeUtenteValue)) {
			throw new Exception("codeUtente (cdnLavoratore/prgAzienda/prgUnita/codCPI) e' NULL");
		}
		if (StringUtils.isEmpty(typeUtente)) {
			throw new Exception("typeUtente e' obbligatorio");
		}
		if (StringUtils.isEmpty(codeUtenteValue2)
				&& typeUtente.equalsIgnoreCase(Pi3Constants.PI3_SPIL_UTENTE_BEAN_UNITA_AZIENDA)) {
			throw new Exception("codeUtente2 (prgUnita) per Azienda Unita e' NULL");
		}

		TransactionQueryExecutor t = null;
		try {
			t = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			t.initTransaction();
			utentePi3Bean = new UtentePi3Bean();
			if (typeUtente.equalsIgnoreCase(Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE)) {

				// AN_LAVORATORE
				Object[] inputParametersUtente = new Object[1];
				inputParametersUtente[0] = codeUtenteValue;
				SourceBean retUtenteMain = (SourceBean) t.executeQuery("GET_AN_LAVORATORE_ANAG", inputParametersUtente,
						TransactionQueryExecutor.SELECT);

				String cognome = (String) retUtenteMain.getAttribute("ROW.STRCOGNOME");
				String nome = (String) retUtenteMain.getAttribute("ROW.STRNOME");
				String codiceFiscale = (String) retUtenteMain.getAttribute("ROW.STRCODICEFISCALE");
				String sesso = (String) retUtenteMain.getAttribute("ROW.STRSESSO");
				String data_nascita = (String) retUtenteMain.getAttribute("ROW.DATNASC");
				String descrizioneComune = (String) retUtenteMain.getAttribute("ROW.STRCOMNAS");
				String ragioneSociale = cognome + " " + nome;
				// boolean checkCittadinanza = retUtenteMain.containsAttribute("ROW.CODCITTADINANZA");
				// boolean checkComuneDomicilio = retUtenteMain.containsAttribute("ROW.CODCOMDOM");

				utentePi3Bean.setIdUtenteSPIL(codeUtenteValue);
				utentePi3Bean.setTypeUtente(Pi3Constants.PI3_SPIL_UTENTE_BEAN_LAVORATORE);

				utentePi3Bean.setNome(nome);
				utentePi3Bean.setCognome(cognome);
				utentePi3Bean.setCodiceFiscale(codiceFiscale);
				utentePi3Bean.setDataNascita(data_nascita);
				utentePi3Bean.setRagioneSociale(ragioneSociale);
				utentePi3Bean.setSesso(sesso);

				SourceBean retUtenteRecapiti = (SourceBean) t.executeQuery("GET_AN_LAVORATORE_INDIRIZZI",
						inputParametersUtente, TransactionQueryExecutor.SELECT);

				String indirizzoResidenza = (String) retUtenteRecapiti.getAttribute("ROW.STRINDIRIZZORES");
				String localitaResidenza = (String) retUtenteRecapiti.getAttribute("ROW.STRLOCALITARES");
				String cittaResidenza = (String) retUtenteRecapiti.getAttribute("ROW.STRCOMDOM");
				String email = (String) retUtenteRecapiti.getAttribute("ROW.STREMAIL");
				String telefono = (String) retUtenteRecapiti.getAttribute("ROW.STRTELDOM");
				String fax = (String) retUtenteRecapiti.getAttribute("ROW.STRFAX");

				utentePi3Bean.setIndirizzoResidenza(indirizzoResidenza);
				utentePi3Bean.setLocalitaResidenza(localitaResidenza);
				utentePi3Bean.setCittaResidenza(cittaResidenza);
				utentePi3Bean.setEmail(email);
				utentePi3Bean.setTelefono(telefono);
				utentePi3Bean.setFax(fax);

				utentePi3Bean.setDescriptionCorrespondentINFTRENT(nome + " " + cognome);
				utentePi3Bean.setCorrespondentTypeINFTRENT(Pi3Constants.PI3_SPIL_CORRESPONDENT_TYPE_PERSONA);
				utentePi3Bean.setCodeCorrespondentINFTRENT(codeUtenteValue);

			} else if (typeUtente.equalsIgnoreCase(Pi3Constants.PI3_SPIL_UTENTE_BEAN_UNITA_AZIENDA)) {

				// AN_AZIENDA_UNITA
				Object[] inputParametersUtente = new Object[2];
				inputParametersUtente[0] = codeUtenteValue;
				inputParametersUtente[1] = codeUtenteValue2;
				SourceBean retUtenteMain = (SourceBean) t.executeQuery("VALIDA_MOV_GET_UNITA_AZIENDALE",
						inputParametersUtente, TransactionQueryExecutor.SELECT);

				String ragioneSociale = (String) retUtenteMain.getAttribute("ROW.STRRAGIONESOCIALE");
				String partitaIva = (String) retUtenteMain.getAttribute("ROW.strpartitaiva");
				String codiceFiscale = (String) retUtenteMain.getAttribute("ROW.CodiceFiscale");

				utentePi3Bean.setIdUtenteSPIL(codeUtenteValue);
				utentePi3Bean.setTypeUtente(Pi3Constants.PI3_SPIL_UTENTE_BEAN_UNITA_AZIENDA);
				utentePi3Bean.setNome(ragioneSociale);
				utentePi3Bean.setCognome(ragioneSociale);
				utentePi3Bean.setRagioneSociale(ragioneSociale);
				utentePi3Bean.setPartitaIva(partitaIva);
				utentePi3Bean.setCodiceFiscale(codiceFiscale);

				utentePi3Bean.setDescriptionCorrespondentINFTRENT(ragioneSociale);
				utentePi3Bean
						.setCorrespondentTypeINFTRENT(Pi3Constants.PI3_SPIL_CORRESPONDENT_TYPE_UNITA_ORGANIZZATIVA);
				utentePi3Bean.setCodeCorrespondentINFTRENT(codeUtenteValue2);

			} else if (typeUtente.equalsIgnoreCase(Pi3Constants.PI3_SPIL_UTENTE_BEAN_AZIENDA)) {

				// AN_AZIENDA
				Object[] inputParametersUtente = new Object[1];
				inputParametersUtente[0] = codeUtenteValue;
				SourceBean retUtenteMain = (SourceBean) t.executeQuery("CARICA_DATI_AZIENDA", inputParametersUtente,
						TransactionQueryExecutor.SELECT);

				String ragioneSociale = (String) retUtenteMain.getAttribute("ROW.RagSociale");
				String partitaIva = (String) retUtenteMain.getAttribute("ROW.PartitaIva");
				String codiceFiscale = (String) retUtenteMain.getAttribute("ROW.CodiceFiscale");

				utentePi3Bean.setIdUtenteSPIL(codeUtenteValue);
				utentePi3Bean.setTypeUtente(Pi3Constants.PI3_SPIL_UTENTE_BEAN_AZIENDA);
				utentePi3Bean.setNome(ragioneSociale);
				utentePi3Bean.setCognome(ragioneSociale);
				utentePi3Bean.setRagioneSociale(ragioneSociale);
				utentePi3Bean.setPartitaIva(partitaIva);
				utentePi3Bean.setCodiceFiscale(codiceFiscale);

				utentePi3Bean.setDescriptionCorrespondentINFTRENT(ragioneSociale);
				utentePi3Bean
						.setCorrespondentTypeINFTRENT(Pi3Constants.PI3_SPIL_CORRESPONDENT_TYPE_UNITA_ORGANIZZATIVA);
				utentePi3Bean.setCodeCorrespondentINFTRENT(codeUtenteValue);

			} else if (typeUtente.equalsIgnoreCase(Pi3Constants.PI3_SPIL_UTENTE_BEAN_CPI)) {

				// CPI (Centro per l'Impiego)
				Object[] inputParametersUtente = new Object[1];
				inputParametersUtente[0] = codeUtenteValue;
				SourceBean retUtenteMain = (SourceBean) t.executeQuery("GET_CPI_DOCUMENTO", inputParametersUtente,
						TransactionQueryExecutor.SELECT);

				// TODO
				String ragioneSociale = (String) retUtenteMain.getAttribute("ROW.STRDESCRIZIONE");
				String indirizzoResidenza = (String) retUtenteMain.getAttribute("ROW.STRINDIRIZZO");

				utentePi3Bean.setIdUtenteSPIL(codeUtenteValue);
				utentePi3Bean.setTypeUtente(Pi3Constants.PI3_SPIL_UTENTE_BEAN_CPI);
				utentePi3Bean.setNome(ragioneSociale);
				utentePi3Bean.setCognome(ragioneSociale);
				utentePi3Bean.setRagioneSociale(ragioneSociale);
				utentePi3Bean.setIndirizzoResidenza(indirizzoResidenza);

				utentePi3Bean.setDescriptionCorrespondentINFTRENT(ragioneSociale);
				utentePi3Bean
						.setCorrespondentTypeINFTRENT(Pi3Constants.PI3_SPIL_CORRESPONDENT_TYPE_UNITA_ORGANIZZATIVA);
				utentePi3Bean.setCodeCorrespondentINFTRENT(codeUtenteValue);

			}
			t.commitTransaction();
		} catch (Exception e) {
			if (t != null) {
				t.rollBackTransaction();
			}
			e.printStackTrace();
		}

		return utentePi3Bean;
	}

	private Documento setTempPdfFileToDocument(Documento documento) throws Exception {

		boolean successRename = false;

		DataConnection dc = null;
		String pool = (String) getConfig().getAttribute("POOL");
		DataConnectionManager dcm = DataConnectionManager.getInstance();
		dc = dcm.getConnection(pool);

		documento.readBLOB(dc);

		File outFile = documento.getTempFile();

		if (outFile.exists()) {
			int index = outFile.getAbsolutePath().lastIndexOf(".");
			String nameFileOut = outFile.getAbsolutePath().substring(0, index);
			File filePdf = new File(nameFileOut + ".pdf");
			successRename = outFile.renameTo(filePdf);

			if (successRename) {
				documento.setTempFile(filePdf);
			}
		}

		dc.close();

		return documento;

	}

	private String createInputXml(Map<String, String> bean) {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		org.w3c.dom.Document doc;
		String output = "";

		String serviceprovider = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER);
		String iddocumento = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_ID_DOCUMENTO);
		String tipodocumento = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO);
		String nomeCittadino = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_LAVORATORE);
		String cognomeCittadino = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_COGNOME_LAVORATORE);
		String codicefiscaleCittadino = (String) bean
				.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_FISCALE_LAVORATORE);
		String ipOperatore = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_IP_OPERATORE);
		String codiceOperatore = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_OPERATORE);

		SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataArrivoDoc = fd.format(new Date());
		String datadocumento = dataArrivoDoc.substring(0, 10);
		String oradocumento = dataArrivoDoc.substring(11);
		String nomedocumento = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_DOCUMENTO);
		String hashdocumento = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_HASH_DOCUMENTO);

		try {

			docBuilder = docFactory.newDocumentBuilder();

			// root firma_graf_spil elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("firma_graf_spil");
			doc.appendChild(rootElement);

			// serviceproviderElem elements
			Element serviceproviderElem = doc.createElement("serviceprovider");
			rootElement.appendChild(serviceproviderElem);

			// set attribute to serviceproviderElem element
			Attr attr = doc.createAttribute("value");
			attr.setValue(serviceprovider);
			serviceproviderElem.setAttributeNode(attr);

			// iddocumento elements
			Element iddocumentoElem = doc.createElement("iddocumento");
			iddocumentoElem.appendChild(doc.createTextNode(iddocumento));
			serviceproviderElem.appendChild(iddocumentoElem);

			// tipodocumento elements
			Element tipodocumentoElem = doc.createElement("tipodocumento");
			tipodocumentoElem.appendChild(doc.createTextNode(tipodocumento));
			serviceproviderElem.appendChild(tipodocumentoElem);

			// datadocumento elements
			Element datadocumentoElem = doc.createElement("datadocumento");
			datadocumentoElem.appendChild(doc.createTextNode(datadocumento));
			serviceproviderElem.appendChild(datadocumentoElem);

			// oradocumento elements
			Element oradocumentoElem = doc.createElement("oradocumento");
			oradocumentoElem.appendChild(doc.createTextNode(oradocumento));
			serviceproviderElem.appendChild(oradocumentoElem);

			// nomedocumento elements
			Element nomedocumentoElem = doc.createElement("nomedocumento");
			nomedocumentoElem.appendChild(doc.createTextNode(nomedocumento));
			serviceproviderElem.appendChild(nomedocumentoElem);

			// hashdocumento elements
			Element hashdocumentoElem = doc.createElement("hashdocumento");
			hashdocumentoElem.appendChild(doc.createTextNode(hashdocumento));
			serviceproviderElem.appendChild(hashdocumentoElem);

			// cittadino elements
			Element cittadinoElem = doc.createElement("cittadino");
			serviceproviderElem.appendChild(cittadinoElem);

			// nome (cittadino) elements
			Element nomeCittadinoElem = doc.createElement("nome");
			nomeCittadinoElem.appendChild(doc.createTextNode(nomeCittadino));
			cittadinoElem.appendChild(nomeCittadinoElem);

			// cognome (cittadino) elements
			Element cognomeCittadinoElem = doc.createElement("cognome");
			cognomeCittadinoElem.appendChild(doc.createTextNode(cognomeCittadino));
			cittadinoElem.appendChild(cognomeCittadinoElem);

			// codicefiscale (cittadino) elements
			Element codicefiscaleCittadinoElem = doc.createElement("codicefiscale");
			codicefiscaleCittadinoElem.appendChild(doc.createTextNode(codicefiscaleCittadino));
			cittadinoElem.appendChild(codicefiscaleCittadinoElem);

			// postazioneoperatore elements
			Element postazioneoperatoreElem = doc.createElement("postazioneoperatore");
			serviceproviderElem.appendChild(postazioneoperatoreElem);

			// ipOperatore (operatore) elements
			Element indirizzoipElem = doc.createElement("indirizzoip");
			indirizzoipElem.appendChild(doc.createTextNode(ipOperatore));
			postazioneoperatoreElem.appendChild(indirizzoipElem);

			// codiceutente (operatore) elements
			Element codiceOperatoreElem = doc.createElement("codiceutente");
			codiceOperatoreElem.appendChild(doc.createTextNode(codiceOperatore));
			postazioneoperatoreElem.appendChild(codiceOperatoreElem);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return output;

	}

	private String getIpOperatore() {
		String address = "127.0.1.1";

		HttpServletRequest request = (HttpServletRequest) getRequestContainer().getAttribute(Constants.HTTP_REQUEST);

		String remoteHost = request.getRemoteHost();
		String ipRemoteHost = request.getRemoteAddr();

		if (!StringUtils.isEmpty(ipRemoteHost)) {
			address = ipRemoteHost;
		}

		if (!StringUtils.isEmpty(remoteHost)) {
			if (!ipRemoteHost.equalsIgnoreCase(remoteHost)) {
				address += "/" + remoteHost;
			}
		}

		return address;
	}

	private String decodificaStatoInvioDocumento(String codStatoInvio) {

		String descrizioneDecodificata = "";

		if (!StringUtils.isEmpty(codStatoInvio)) {

			if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO_DESCRIZIONE;

			} else if (codStatoInvio
					.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_IN_ATTESA_DI_PROTOCOLLAZIONE)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_IN_ATTESA_DI_PROTOCOLLAZIONE_DESCRIZIONE;

			} else if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO_DESCRIZIONE;

			} else if (codStatoInvio
					.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO_SENZA_DOC_FIRMATO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO_SENZA_DOC_FIRMATO_DESCRIZIONE;

			} else if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_MANUALE)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_MANUALE_DESCRIZIONE;

			} else if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO_DESCRIZIONE;

			} else if (codStatoInvio
					.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO_DESCRIZIONE;

			} else if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI_DESCRIZIONE;

			} else if (codStatoInvio.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PREDISPOSED)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_PREDISPOSED_DESCRIZIONE;

			} else if (codStatoInvio
					.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE_DESCRIZIONE;

			} else if (codStatoInvio
					.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO)) {
				descrizioneDecodificata = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO_DESCRIZIONE;

			}

		}

		return descrizioneDecodificata;
	}

	public String getCodeCPI(String cdnLavoratore) throws Exception {

		String codCpiOrig = null;

		_logger.debug("ProtocollazionePi3 [TRENTO] - getCodeCPI");

		Object[] sqlparams = new Object[1];
		sqlparams[0] = cdnLavoratore;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE",
				sqlparams, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			codCpiOrig = (String) row.getAttribute("CODCPIORIG");
		}

		return codCpiOrig;
	}

}
