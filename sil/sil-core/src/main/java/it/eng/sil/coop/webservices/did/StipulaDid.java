package it.eng.sil.coop.webservices.did;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.AttachmentPart;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.webservices.agenda.appuntamento.LavoratoreUtils;
import it.eng.sil.coop.webservices.bean.DidBean;
import it.eng.sil.coop.webservices.bean.LavoratoreBean;
import it.eng.sil.coop.webservices.bean.RichiestaBeanDid;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.coop.webservices.did.xml.DatiDid;
import it.eng.sil.coop.webservices.did.xml.DatiDid.Lavoratore;
import it.eng.sil.coop.webservices.did.xml.SiNo;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.xml.XMLValidator;

public class StipulaDid implements StipulaDidInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StipulaDid.class.getName());
	private static final BigDecimal userSP = new BigDecimal("150");
	private final String SCHEMA_XSD_INPUT = "did_in.xsd";

	public String putCreaDID(String inputXML) throws java.rmi.RemoteException, Exception {
		String outputXML = "";
		Document doc = null;
		String codiceFisc = "";
		String dataDichiarazione = "";
		String tipoDichiarazione = "";
		String intestazioneStampa = "";
		SourceBean row = null;
		BigDecimal cdnlavoratore = null;
		BigDecimal prgElencoAnag = null;
		BigDecimal prgDidApertaPA = null;
		BigDecimal prgDichDispoNew = null;
		String rptStampa = "";
		TransactionQueryExecutor transExec = null;
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		boolean protocolla = false;
		SourceBean request = null;
		SourceBean response = null;
		RichiestaBeanDid checkRichiesta = null;
		UserBean usrSP = null;
		LavoratoreBean lavService = null;
		DidBean didLav = null;
		String flgRischioDisoccupazione = null;
		String datLicenziamento = null;
		boolean cambioCPICompetenza = false;

		_logger.info("Il servizio di stipula did e' stato chiamato");

		try {
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "did" + File.separator + SCHEMA_XSD_INPUT);

			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				outputXML = Utils.createXMLRisposta("99", "Errore generico");
				return outputXML;
			}
			// InputStream is = new ByteArrayInputStream(inputXML.getBytes());
			/**
			 * @author marianna borriello forziamo l'encoding a utf-8 per non avere problemi con le lettere accentate
			 */
			InputSource is = new InputSource(new StringReader(inputXML));
			is.setEncoding("UTF-8");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			doc = documentBuilder.parse(is);
			doc.getDocumentElement().normalize();

			checkRichiesta = new RichiestaBeanDid(doc);
			outputXML = checkRichiesta.getOutputXML();
			if (outputXML != null) {
				return outputXML;
			}
			codiceFisc = checkRichiesta.getCodiceFiscale();
			rptStampa = "patto/" + checkRichiesta.getModelloStampa();
			String protocollazione = checkRichiesta.getProtocollazione();
			if (protocollazione != null && protocollazione.equalsIgnoreCase("ON")) {
				protocolla = true;
			}
			intestazioneStampa = checkRichiesta.getIntestazione();
			if (intestazioneStampa == null || intestazioneStampa.equals("")) {
				intestazioneStampa = "OFF";
			}
			dataDichiarazione = checkRichiesta.getDataDichiarazione();
			tipoDichiarazione = checkRichiesta.getTipoDichiarazione();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:putCreaDID", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			DatiDid didXml = convertToDatiDid(inputXML);
			Lavoratore lav = didXml.getLavoratore();
			UtilsConfig utility = new UtilsConfig("WSDIDLAV");
			String tipoConfig = utility.getConfigurazioneDefault_Custom();
			// a seconda della configurazione gestire l'inserimento del lavoratore
			if (tipoConfig.equals(Properties.DEFAULT_CONFIG)) {
				lavService = new LavoratoreBean(codiceFisc, dataDichiarazione, transExec);
				outputXML = lavService.getOutputXml();
				if (outputXML != null) {
					transExec.rollBackTransaction();
					return outputXML;
				}
			} else {
				// previsto inserimento lavoratore se presente nel tracciato
				if (lav != null) {
					lavService = new LavoratoreBean(codiceFisc, dataDichiarazione, lav, transExec);
				} else {
					lavService = new LavoratoreBean(codiceFisc, dataDichiarazione, transExec);
				}
				outputXML = lavService.getOutputXml();
				if (outputXML != null) {
					transExec.rollBackTransaction();
					return outputXML;
				}
			}

			cdnlavoratore = lavService.getCdnLavoratore();

			usrSP = new UserBean(userSP, cdnlavoratore);
			/*
			 * Controlla sulla competenza commentato - decreto Poletti boolean flgCompetenza = false; if
			 * (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
			 * flgCompetenza = true; } if (!flgCompetenza) { transExec.rollBackTransaction(); outputXML =
			 * Utils.createXMLRisposta("08", "Lavoratore non competente"); return outputXML; }
			 */
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", userSP);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("datDichiarazione", dataDichiarazione);
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			request.setAttribute("intestazioneStampa", intestazioneStampa);
			request.setAttribute("DIDONLINE", "true");
			if (lav != null) {
				SiNo rischioDisoccupazione = lav.getRischiodisoccupazione();
				if (rischioDisoccupazione != null) {
					if (SiNo.SI.value().equalsIgnoreCase(rischioDisoccupazione.value())) {
						flgRischioDisoccupazione = Properties.FLAG_S;
						request.setAttribute("flgRischioDisoccupazione", flgRischioDisoccupazione);
						if (lav.getDatalicenziamento() != null) {
							datLicenziamento = DateUtils.formatXMLGregorian(lav.getDatalicenziamento());
							request.setAttribute("datLicenziamento", datLicenziamento);
						}
					}
				}
			}
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);

			// Controllo esistenza iscrizione elenco anagrafico
			prgElencoAnag = lavService.getElencoAnagrafico(transExec);
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				transExec.rollBackTransaction();
				return outputXML;
			}
			if (prgElencoAnag == null) {
				boolean resEA = lavService.insertElencoAnagrafico(transExec, userSP);
				if (!resEA) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("14", "Errore relativo all'elenco anagrafico");
					return outputXML;
				}
			} else {
				prgDidApertaPA = lavService.getDidApertaPA(transExec, prgElencoAnag);
				outputXML = lavService.getOutputXml();
				if (outputXML != null) {
					transExec.rollBackTransaction();
					return outputXML;
				}
				if (prgDidApertaPA != null) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("15", "DID già presente");
					return outputXML;
				}
			}

			boolean checkControllo = lavService.isDidStipulabile(dataDichiarazione, userSP, requestContainer, request,
					response, transExec);
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				transExec.rollBackTransaction();
				return outputXML;
			}

			if (!checkControllo) {
				transExec.rollBackTransaction();
				outputXML = Utils.createXMLRisposta("19", "DID non stipulabile");
				return outputXML;
			}

			// Controllo esistenza documento di identificazione "VP" - PERVENUTO DA PORTALE
			BigDecimal prgDoc = lavService.getDocumentoVP(transExec);
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				transExec.rollBackTransaction();
				return outputXML;
			}
			if (prgDoc == null) {
				boolean risDoc = lavService.insertDocumentoIdentificazione(transExec, userSP);
				if (!risDoc) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("12",
							"Errore nell'inserimento del documento di identificazione");
					return outputXML;
				}
			}

			// Controllo presa visione della privacy
			BigDecimal prgPrivacy = lavService.getAutorizzazionePrivacy(transExec);
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				transExec.rollBackTransaction();
				return outputXML;
			}
			if (prgPrivacy == null) {
				boolean resPrivacy = lavService.insertPrivacy(transExec, userSP);
				if (!resPrivacy) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("13",
							"Errore nell'inserimento della presa visione della privacy");
					return outputXML;
				}
			}

			row = (SourceBean) transExec.executeQuery("QUERY_NEXTVAL_STATEMENT", null, "SELECT");
			if (row == null) {
				transExec.rollBackTransaction();
				outputXML = Utils.createXMLRisposta("16", "Errore inserimento DID");
				return outputXML;
			}
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			if (row.containsAttribute("DO_NEXTVAL")) {
				String codUltimoContratto = "";
				prgDichDispoNew = (BigDecimal) row.getAttribute("DO_NEXTVAL");
				String codStatoAttoDid = "PA";
				if (protocolla) {
					codStatoAttoDid = "PR";
				}
				Object[] params = new Object[] { cdnlavoratore };
				SourceBean rowUltimoContratto = (SourceBean) transExec.executeQuery("GET_ULTIMO_MOV", params, "SELECT");
				if (rowUltimoContratto != null) {
					rowUltimoContratto = (rowUltimoContratto.containsAttribute("ROW")
							? (SourceBean) rowUltimoContratto.getAttribute("ROW")
							: rowUltimoContratto);
					codUltimoContratto = rowUltimoContratto.getAttribute("CODCONTRATTO") != null
							? rowUltimoContratto.getAttribute("CODCONTRATTO").toString()
							: "";
				}
				didLav = new DidBean(prgDichDispoNew, dataDichiarazione, prgElencoAnag, tipoDichiarazione,
						codStatoAttoDid, userSP);
				if (flgRischioDisoccupazione != null && flgRischioDisoccupazione.equalsIgnoreCase(Properties.FLAG_S)) {
					didLav.setFlgRischioDisoccupazione(Properties.FLAG_S);
					if (datLicenziamento != null) {
						didLav.setDatLicenziamento(datLicenziamento);
					}
				}
				if (!codUltimoContratto.equals("")) {
					didLav.setUltimoContratto(codUltimoContratto);
				}
				boolean resDid = didLav.insertDid(transExec);
				if (!resDid) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("16", "Errore inserimento DID");
					return outputXML;
				}
				BigDecimal prgDocColl = lavService.getDocumentCollDocIdentificazione(transExec);
				resDid = didLav.associaDocumentoIdentificazione(transExec, prgDocColl);
				if (!resDid) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("17", "Errore associazione documento di identificazione");
					return outputXML;
				}

				if (protocolla) {
					int iTentativo = 0;
					lavService.calcolaStatoOccupazionale(transExec, didLav.getDataDichiarazione());
					didLav.aggiornaInfoDid(transExec, cdnlavoratore);

					// verifica aggiornamento competenza quando il domicilio del lavoratore è fuori provincia rispetto
					// alla provincia di riferimento
					// in questo caso si prende il codcpi capoluogo
					boolean settaCpiCapoluogo = false;
					SourceBean rowProvDomicilio = LavoratoreUtils.getProvinciaDomicilioLav(transExec, cdnlavoratore);
					if (rowProvDomicilio != null) {
						rowProvDomicilio = rowProvDomicilio.containsAttribute("ROW")
								? (SourceBean) rowProvDomicilio.getAttribute("ROW")
								: rowProvDomicilio;
						String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowProvDomicilio,
								"CODPROVINCIA");
						String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowProvDomicilio, "CODPROVINCIASIL");
						if (!codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
							settaCpiCapoluogo = true;
						}
					}

					if (settaCpiCapoluogo) {
						SourceBean rowCpi = (SourceBean) transExec.executeQuery("GET_CODCPICAPOLUOGO", null, "SELECT");
						if (rowCpi != null) {
							rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
							String codCpiRif = StringUtils.getAttributeStrNotNull(rowCpi, "RESULT");
							cambioCPICompetenza = it.eng.sil.util.amministrazione.impatti.DidBean
									.aggiornaCPI(cdnlavoratore, codCpiRif, userSP, transExec);
							if (cambioCPICompetenza) {
								lavService.setCodCpi(codCpiRif);
							}
						}
					}

					DataHandler dh = null;
					do {
						iTentativo = iTentativo + 1;
						dh = didLav.getStampaDid(transExec, lavService.getCodCpi(), cdnlavoratore,
								lavService.getCodiceFiscale(), rptStampa, request);
					} while (dh == null && iTentativo < Utils.numMaxTentativiProtocollazione);

					if (dh != null) {
						MessageContext msgContext = MessageContext.getCurrentContext();
						Message rspmsg = msgContext.getResponseMessage();
						_logger.debug("org.apache.axis.attachments.Attachments.SEND_TYPE_MIME : "
								+ org.apache.axis.attachments.Attachments.SEND_TYPE_MIME);
						int inputAttachmentType = rspmsg.getAttachmentsImpl().getSendType();
						rspmsg.getAttachmentsImpl().setSendType(inputAttachmentType);
						AttachmentPart attachPart = rspmsg.createAttachmentPart(dh);
						rspmsg.addAttachmentPart(attachPart);
						outputXML = Utils.createXMLRisposta("00", "OK");
					} else {
						transExec.rollBackTransaction();
						outputXML = Utils.createXMLRisposta("18", "Errore di protocollazione");
						return outputXML;
					}
				} else {
					outputXML = Utils.createXMLRisposta("00", "OK");
				}
			} else {
				transExec.rollBackTransaction();
				outputXML = Utils.createXMLRisposta("16", "Errore inserimento DID");
				return outputXML;
			}

			transExec.commitTransaction();

			if (protocolla) {
				// prenotazione automatica appuntamento da DID online
				try {
					AppuntamentoDid appuntamentoDid = new AppuntamentoDid();
					appuntamentoDid.fissaAppuntamentoAndInviaNotifiche(cdnlavoratore, lavService.getCodCpi());
				} catch (Exception e) {
					_logger.error("Errore in fase di prenotazione automatica appuntamento da DID online");
				}
				// Gestione invio SAP
				ModuleIFace moduleGestioneInvioSAP = null;
				try {
					SourceBean serviceRequest = requestContainer.getServiceRequest();
					serviceRequest.setAttribute("INVIASAPFROMDID", "S");
					DefaultRequestContext drc = new DefaultRequestContext(requestContainer, new ResponseContainer());
					moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
					((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
					moduleGestioneInvioSAP.service(serviceRequest, response);
					it.eng.sil.util.amministrazione.impatti.DidBean.aggiornaNoteDid(usrSP.getUser(), prgDichDispoNew);
				} catch (Exception me) {
					if (!response.containsAttribute("ERRORENOLOG")) {
						it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:errore inviaSAP", me);
					}
				}
			}

			// Aggiorna competenza indice regionale solo per RER
			if (cambioCPICompetenza) {
				try {
					// Gestione configurazione aggiornamento competenza indice regionale (IR solo per RER)
					UtilsConfig utilityIR = new UtilsConfig("AGCOMPIR");
					String configIR = utilityIR.getConfigurazioneDefault_Custom();
					if (configIR.equals(Properties.CUSTOM_CONFIG)) {
						it.eng.sil.util.amministrazione.impatti.DidBean.aggiornaCompetenzaIR(usrSP.getUser(),
								cdnlavoratore);
					}
				} catch (Exception irex) {
					it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:aggiornaCompetenzaIR", irex);
				}
			}

			return outputXML;
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:putCreaDID", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

	}

	DatiDid convertToDatiDid(String xml) throws JAXBException {
		JAXBContext jaxbContext;
		DatiDid datiDid = null;
		try {
			jaxbContext = JAXBContext.newInstance(DatiDid.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			datiDid = (DatiDid) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto DatiDid dall'xml");
		}
		return datiDid;
	}

}