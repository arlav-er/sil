/**
 * AcquisizioneFMSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.firmagrafometrica.custom.ServiceXmlInputBean;
import it.eng.sil.module.consenso.Consenso;
import it.eng.sil.module.firma.ricevuta.StampaRicevuta;
import it.eng.sil.util.blen.StringUtils;

public class AcquisizioneFMSoapBindingImpl
		implements it.eng.sil.coop.webservices.firmagrafometrica.AcquisizioneFirmaGrafometrica {

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Consenso.class.getName());

	Map<String, Object> bean = new HashMap<String, Object>();

	public it.eng.sil.coop.webservices.firmagrafometrica.XML uploadDocFirmato(
			it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf, java.lang.String in_string_pdfname,
			java.lang.String in_string_xmlparams) throws java.rmi.RemoteException {

		_logger.info("[FirmaGrafometrica WS] -> uploadDocFirmato");

		TransactionQueryExecutor txExec = null;

		XML xmlOutput = new XML();

		if (in_document_pdf == null) {
			return printErrorOutput(xmlOutput, bean, "File PDF ha valore NULL");
		}

		if (in_document_pdf.getBinaryData().length <= 0) {
			return printErrorOutput(xmlOutput, bean, "Bytes del file PDF di lunghezza 0");
		}

		if (StringUtils.isEmpty(in_string_pdfname)) {
			return printErrorOutput(xmlOutput, bean, "Nome del file PDF assente");
		}

		if (StringUtils.isEmpty(in_string_xmlparams)) {
			return printErrorOutput(xmlOutput, bean, "Mancano i parametri XML");
		}

		try {

			_logger.debug("[FirmaGrafometrica WS] -> in_string_xmlparams: " + in_string_xmlparams);
			ServiceXmlInputBean serviceXmlInputBean = getServiceXmlInputBean(in_string_xmlparams);

			int prgDoc = 0;
			BigDecimal prgDocumentoBlob = new BigDecimal(0);

			if (!StringUtils.isEmpty(serviceXmlInputBean.getIdDocumento())) {

				Documento doc = null;
				try {
					doc = new Documento(new BigDecimal(serviceXmlInputBean.getIdDocumento()));
				} catch (Exception eD) {
					_logger.error("[FirmaGrafometrica WS] -> uploadDocFirmato --- Exception: " + eD.getMessage());
					return printErrorOutput(xmlOutput, bean,
							"Il sistema non e' riuscito a caricare il Documento, e' possibile si stia utilizzando un documento PDF non idoneo o con metadati non allineati al sistema");
				}

				/***
				 * 
				 * VERIFICA COERENZA DEL KEYNUMLOCK
				 * 
				 */
				_logger.debug("[FirmaGrafometrica WS] KLOCK DOC     [" + doc.getNumKloDocumento().toString() + "]");
				_logger.debug(
						"[FirmaGrafometrica WS] KLOCK INVIATO [" + serviceXmlInputBean.getNumKloDocumento() + "]");
				if (doc.getNumKloDocumento().toString().equalsIgnoreCase(serviceXmlInputBean.getNumKloDocumento())) {

					txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
					txExec.initTransaction();

					/***
					 * 
					 * UPDATE FLGFIRMATO SU AM_DOCUMENTO_BLOB
					 * 
					 */
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- updating flag FlgFirmato su AM_DOCUMENTO_BLOB...");
					Object[] sqlparamsNumLok = new Object[1];
					sqlparamsNumLok[0] = serviceXmlInputBean.getIdDocumento();

					boolean esito = ((Boolean) txExec.executeQuery("UPDATE_AM_DOCUMENTO_BLOB_FLG_FIRMATO",
							sqlparamsNumLok, "UPDATE")).booleanValue();
					if (!esito) {
						return printErrorOutput(xmlOutput, bean, "durante aggiornamento per FLGFIRMATO");
					} else {
						_logger.info(
								"[FirmaGrafometrica WS] -> uploadDocFirmato --- update flag FlgFirmato effettuato");
					}

					/***
					 * 
					 * UPDATE FILE PDF FIRMATO SU AM_DOCUMENTO_BLOB
					 * 
					 */
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- updating PDF firmato su AM_DOCUMENTO_BLOB...");

					// Set Temp File
					// File dbFile = createFileToBLOB();
					File dbFile = convertBytesArrayToFile(in_document_pdf.getBinaryData(), in_string_pdfname);
					doc.setTempFile(dbFile);

					doc.inserisciBlob(txExec);
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- update PDF firmato su AM_DOCUMENTO_BLOB effettuato");

					/***
					 * 
					 * UPDATE KEY NUM LOCK AM_DOCUMENTO
					 * 
					 */
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- updating KeyNumLock su AM_DOCUMENTO...");
					doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal(1)));
					doc.aggiornaDocumento(txExec);
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- update KeyNumLock su AM_DOCUMENTO effettuato");

					/***
					 * 
					 * INSERT FILE PDF RICEVUTA SU AM_DOCUMENTO_ALLEGATO
					 * 
					 */
					_logger.info("[FirmaGrafometrica WS] -> uploadDocFirmato --- stampa ricevuta...");
					Documento docRicevuta = null;
					StampaRicevuta sr = new StampaRicevuta();
					docRicevuta = sr.getStampaRicevuta(txExec, doc);

					sr.inserisciAllegatoDocumento(docRicevuta, txExec, doc.getPrgDocumento().toString());
					_logger.info(
							"[FirmaGrafometrica WS] -> uploadDocFirmato --- inserimento su AM_DOCUMENTO_ALLEGATO ricevuta creato");

					txExec.commitTransaction();
					_logger.info("[FirmaGrafometrica WS] -> uploadDocFirmato --- successfull");

					bean.put("codiceOutput", "1");
					bean.put("descrizioneOutput", "Documento firmato inviato con successo al Sistema SPIL");

				} else {
					// return printErrorOutput(xmlOutput, bean, "Verifica Coerenza KeyNumLock non riuscita [Nr KLOCK Doc
					// Attuale = " + doc.getNumKloDocumento().toString() + " - Nr KLOCK ARRIVATO = " +
					// serviceXmlInputBean.getNumKloDocumento());
					_logger.error("[FirmaGrafometrica Client WS] -> uploadDocFirmato --- Errore: "
							+ "Verifica Coerenza KeyNumLock non riuscita [Nr KLOCK Doc Attuale = "
							+ doc.getNumKloDocumento().toString() + " - Nr KLOCK ARRIVATO = "
							+ serviceXmlInputBean.getNumKloDocumento());
					return printErrorOutput(xmlOutput, bean,
							"Il documento firmato e' gia' stato inviato al Sistema SPIL");
				}

			} else {
				return printErrorOutput(xmlOutput, bean, "Fra i parametri XML manca il PRGDocumento");
			}

		} catch (Exception e) {

			_logger.error("[FirmaGrafometrica WS] -> uploadDocFirmato --- Exception");

			try {
				txExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				return printErrorOutput(xmlOutput, bean, e1.getMessage());
			}

			return printErrorOutput(xmlOutput, bean, e.getMessage());
		}

		xmlOutput.setDocument(createOutputXml(bean));

		return xmlOutput;
	}

	private ServiceXmlInputBean getServiceXmlInputBean(String inputXML) throws Exception {

		ServiceXmlInputBean bean = new ServiceXmlInputBean();

		if (!StringUtils.isEmpty(inputXML)) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(inputXML)));

			XPath xPath = XPathFactory.newInstance().newXPath();

			Node serviceProviderInput = (Node) xPath.evaluate("//serviceprovider/@value", doc, XPathConstants.NODE);
			if (serviceProviderInput != null) {
				bean.setServiceProvider(serviceProviderInput.getNodeValue());
			}

			Node idDocumentoInput = (Node) xPath.evaluate("//iddocumento/text()", doc, XPathConstants.NODE);
			if (idDocumentoInput != null) {
				bean.setIdDocumento(idDocumentoInput.getNodeValue());
			}

			Node tipoDocumentoInput = (Node) xPath.evaluate("//tipodocumento/text()", doc, XPathConstants.NODE);
			if (tipoDocumentoInput != null) {
				bean.setTipoDocumento(tipoDocumentoInput.getNodeValue());
			}

			Node nomeCittadinoInput = (Node) xPath.evaluate("//cittadino/nome/text()", doc, XPathConstants.NODE);
			if (nomeCittadinoInput != null) {
				bean.setNomeCittadino(nomeCittadinoInput.getNodeValue());
			}

			Node cognomeCittadinoInput = (Node) xPath.evaluate("//cittadino/cognome/text()", doc, XPathConstants.NODE);
			if (cognomeCittadinoInput != null) {
				bean.setCognomeCittadino(cognomeCittadinoInput.getNodeValue());
			}

			Node codiceFiscaleCittadinoInput = (Node) xPath.evaluate("//cittadino/codicefiscale/text()", doc,
					XPathConstants.NODE);
			if (codiceFiscaleCittadinoInput != null) {
				bean.setCodiceFiscaleCittadino(codiceFiscaleCittadinoInput.getNodeValue());
			}

			Node codiceUtenteOperatoreInput = (Node) xPath.evaluate("//postazioneoperatore/codiceutente/text()", doc,
					XPathConstants.NODE);
			if (codiceUtenteOperatoreInput != null) {
				bean.setCodiceUtenteOperatore(codiceUtenteOperatoreInput.getNodeValue());
			}

			Node numKloDocumentoInput = (Node) xPath.evaluate("//numKloDocumento/text()", doc, XPathConstants.NODE);
			if (numKloDocumentoInput != null) {
				bean.setNumKloDocumento(numKloDocumentoInput.getNodeValue());
			}
		}

		return bean;

	}

	private File convertBytesArrayToFile(byte[] fileByteArray, String fileName) {

		FileOutputStream fop = null;
		File tmpFile = null;

		try {
			tmpFile = File.createTempFile("fg_", ".pdf");

			fop = new FileOutputStream(tmpFile);

			fop.write(fileByteArray);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			_logger.error("[FirmaGrafometrica WS] -> uploadDocFirmato -> convertBytesArrayToFile --- Error: "
					+ e.getMessage(), e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				_logger.error("[FirmaGrafometrica WS] -> uploadDocFirmato -> convertBytesArrayToFile --- Error: "
						+ e.getMessage(), e);
			}
		}

		return tmpFile;
	}

	private String createOutputXml(Map<String, Object> bean) {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc;
		String output = "";

		String codiceOutputValue = (String) bean.get("codiceOutput");
		String descrizioneOutputValue = (String) bean.get("descrizioneOutput");

		try {

			docBuilder = docFactory.newDocumentBuilder();

			// root firma_graf_spil elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Output");
			doc.appendChild(rootElement);

			// CodiceOutput elements
			Element CodiceOutputElem = doc.createElement("CodiceOutput");
			CodiceOutputElem.appendChild(doc.createTextNode(codiceOutputValue));
			rootElement.appendChild(CodiceOutputElem);

			// CodiceOutput elements
			Element DescrizioneOutputElem = doc.createElement("DescrizioneOutput");
			DescrizioneOutputElem.appendChild(doc.createTextNode(descrizioneOutputValue));
			rootElement.appendChild(DescrizioneOutputElem);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
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

	private it.eng.sil.coop.webservices.firmagrafometrica.XML printErrorOutput(
			it.eng.sil.coop.webservices.firmagrafometrica.XML xmlOutput, Map<String, Object> beanOutput,
			String errorMessage) {

		_logger.error("[FirmaGrafometrica Client WS] -> uploadDocFirmato --- Errore: " + errorMessage);

		beanOutput.put("codiceOutput", "0");
		beanOutput.put("descrizioneOutput",
				"Nessuna acquisizione di documento firmato nel Sistema SPIL. Errore: " + errorMessage);

		xmlOutput.setDocument(createOutputXml(bean));

		return xmlOutput;
	}

	private File createFileToBLOB() {

		String filePdfInputPath = "C:/temp/SIL/pdf/output/";
		String filePdfInputName = "documentA.pdf";
		File file = new File(filePdfInputPath + filePdfInputName);

		return file;
	}

}
