package it.eng.sil.coop.webservices.firmagrafometrica.custom;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.adobe.idp.services.BLOB;
import com.adobe.idp.services.FirmaGrafometrica_SetSignatureToDocProxy;
import com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfAProxy;
import com.adobe.idp.services.XML;
import com.adobe.idp.services.holders.BLOBHolder;
import com.adobe.idp.services.holders.XMLHolder;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.module.consenso.GConstants;
import it.eng.sil.module.firma.ricevuta.StampaRicevuta;
import it.eng.sil.util.blen.StringUtils;
import it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSProxy;

public class FirmaGrafometrica {

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FirmaGrafometrica.class.getName());

	private String uploadDocFirmato(java.lang.String inputXML) {

		_logger.debug(className + ".uploadDocFirmato() INIZIO");
		TransactionQueryExecutor txExec = null;

		try {
			ServiceXmlInputBean serviceXmlInputBean = getServiceXmlInputBean(inputXML);

			int prgDoc = 0;

			_logger.debug(className + ".uploadDocFirmato(): prelevo idDocumentodal file XML : "
					+ serviceXmlInputBean.getIdDocumento());
			if (!StringUtils.isEmpty(serviceXmlInputBean.getIdDocumento())) {

				txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				txExec.initTransaction();

				Documento doc = new Documento(new BigDecimal(serviceXmlInputBean.getIdDocumento()));

				// Set Temp File
				File dbFile = createFileToBLOB();
				doc.setTempFile(dbFile);
				_logger.debug(className + ".uploadDocFirmato(): Aggiorno il file blob");
				doc.inserisciBlob(txExec);
				doc.aggiornaDocumento(txExec);
				// doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal(1)));
				// doc.aggiornaDocumento(txExec);
				Documento docRicevuta = null;
				_logger.debug(className + ".uploadDocFirmato(): Stampa ricevuta ");
				StampaRicevuta sr = new StampaRicevuta();
				docRicevuta = sr.getStampaRicevuta(txExec, doc);
				// ricevuta da salvare in AM_DOCUMENTO_ALLEGATO
				// connessione
				// prgDocumento del documento padre
				_logger.debug(className + ".uploadDocFirmato() inserimento su am_documento_allegato ricevuta creata ");

				sr.inserisciAllegatoDocumento(docRicevuta, txExec, doc.getPrgDocumento().toString());
				txExec.commitTransaction();

				System.out.println("File -> BLOB... successfull");
			}

		} catch (Exception e) {
			if (txExec != null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					e1.printStackTrace();
				}
			}
			_logger.error("[FirmaGrafometrica WS] -> uploadDocFirmato --- Error: " + e.getMessage(), e);
			e.printStackTrace();
		}
		_logger.debug(className + ".uploadDocFirmato() FINE");

		return null;
	}

	private ServiceXmlInputBean getServiceXmlInputBean(String inputXML) throws Exception {

		_logger.debug("[FirmaGrafometrica] --> getServiceXmlInputBean --> getServiceXmlInputBean: " + inputXML);

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

		_logger.debug("[FirmaGrafometrica] --> getServiceXmlInputBean --> getServiceXmlInputBean [END]");

		return bean;

	}

	public File createFileToBLOB() {

		String filePdfInputPath = "C:/temp/SIL/pdf/output/";
		String filePdfInputName = "documentA.pdf";
		File file = new File(filePdfInputPath + filePdfInputName);

		return file;
	}

	public BlobFG doSignedPDF(File pdfTransformed, HashMap<String, String> beanDatiInput) throws Exception {

		_logger.info("[FirmaGrafometrica Client WS] -> doSignedPDF");

		BlobFG blob = new BlobFG();
		Map<String, Object> beanOutput = new HashMap<String, Object>();

		if (pdfTransformed == null) {
			return printErrorOutput(blob, beanOutput, "File PDF ha valore NULL");
		}

		if (beanDatiInput == null) {
			return printErrorOutput(blob, beanOutput, "Bean Dati ha valore NULL");
		}

		if (StringUtils.isEmpty((String) beanDatiInput.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_ID_DOCUMENTO))) {
			return printErrorOutput(blob, beanOutput, "ID Documento ha valore NULL");
		}

		File file = null;

		// Primo WS... Conversione da PDF 'Normale' (trasformato dal Template di Cristal Clear ai dati)
		SupportPDFG_ProcessConvertToPdfAProxy proxy = new SupportPDFG_ProcessConvertToPdfAProxy();
		proxy.setEndpoint(getWSUrl(GConstants.WS_NAME_URL_FIRMA_GRAFOMETRICA_01_CONVERT_TO_PDF_A));

		Boolean enableValidationPdfA = new Boolean(false);
		BooleanHolder isPdfA = new BooleanHolder(false);
		XMLHolder outputXmlPdfA = new XMLHolder();
		BLOBHolder pdfAFileOutput = new BLOBHolder();

		BLOB pdfFileInput = new BLOB();
		pdfFileInput.setContentType("application/pdf");

		try {

			byte[] pdfBinaryData = convertFileToBytesArray(pdfTransformed);
			pdfFileInput.setBinaryData(pdfBinaryData);

			proxy.invoke(pdfFileInput, enableValidationPdfA, isPdfA, pdfAFileOutput, outputXmlPdfA);
			_logger.info(
					"[FirmaGrafometrica Client WS] -> doSignedPDF -> Primo WS chiamato: F.G. ProcessConvertToPdfA");

			// Print
			if (_logger.isDebugEnabled()) {
				_logger.debug("esitoConversionePdfToPdfA - outputXmlPdfA: " + outputXmlPdfA.toString());
			}

			if (pdfAFileOutput.value != null) {

				if (pdfAFileOutput.value.getBinaryData() != null) {

					BLOB pdfFileInput2 = new BLOB();
					BLOBHolder pdfAFileOutput2 = new BLOBHolder();
					StringHolder numPages = new StringHolder();
					ShortHolder out_berror = new ShortHolder();
					StringHolder out_error = new StringHolder();
					StringHolder t = new StringHolder();

					// Secondo WS... Conversione da PFF 'A' a PDF 'firmabile' (per essere letto dalla Tavoletta Grafica)
					FirmaGrafometrica_SetSignatureToDocProxy proxy2 = new FirmaGrafometrica_SetSignatureToDocProxy();
					proxy2.setEndpoint(getWSUrl(GConstants.WS_NAME_URL_FIRMA_GRAFOMETRICA_02_SIGNATURE_TO_DOC));

					XML inputXmlPdfA = new XML();
					String inputXml = createInputXml(beanDatiInput);
					
					inputXmlPdfA.setDocument(inputXml);

					try {

						// setProxyAndCertificate();

						pdfFileInput2.setContentType("application/pdf");
						pdfFileInput2.setBinaryData(pdfAFileOutput.value.getBinaryData());

						proxy2.invoke(pdfFileInput2, inputXmlPdfA, numPages, out_berror, pdfAFileOutput2, out_error, t);
						_logger.info(
								"[FirmaGrafometrica Client WS] -> doSignedPDF -> Secondo WS chiamato: F.G. SetSignatureToDoc");

						if (pdfAFileOutput2.value != null) {

							if (pdfAFileOutput2.value.getBinaryData() != null) {

								file = convertBytesArrayToFileOS(pdfAFileOutput2.value.getBinaryData(),
										pdfTransformed.getAbsolutePath());

							} else {
								return printErrorOutput(blob, beanOutput, "PDF Firmabile non traformato");
							}

						} else {
							return printErrorOutput(blob, beanOutput, "PDF Firmabile non traformato");
						}

						// Print
						if (_logger.isDebugEnabled()) {
							_logger.debug("esitoConversionePdfAToSign - out_berror: " + out_berror.value);
							_logger.debug("esitoConversionePdfAToSign - out_error: " + out_error.value);
							_logger.debug("esitoConversionePdfAToSign - t: " + t.value);
							_logger.debug("esitoConversionePdfAToSign - pdfAFileOutput -> remoteURL: "
									+ pdfAFileOutput.value.getRemoteURL());
							// _logger.debug("esitoConversionePdfAToSign - pdfAFileOutput -> binaryData: " +
							// pdfAFileOutput.value.getBinaryData().length);
						}

					} catch (RemoteException e) {
						return printErrorOutput(blob, beanOutput, e.getMessage());
					}

				} else {
					return printErrorOutput(blob, beanOutput, "PDF A non traformato");
				}

			} else {
				return printErrorOutput(blob, beanOutput, "PDF A non traformato");
			}

		} catch (Throwable t) {
			return printErrorOutput(blob, beanOutput, t.getMessage());
		}

		beanOutput.put("codiceOutput", "1");
		beanOutput.put("descrizioneOutput", "PDF pronto per ricevere la firma grafometrica");

		blob.setFilePDF(file);
		blob.setXmlOutput(createOutputXml(beanOutput));

		return blob;
	}

	public FirmaGrafometricaXmlOutputBean getXmlBeanOutput(String outputXML) throws Exception {

		FirmaGrafometricaXmlOutputBean bean = new FirmaGrafometricaXmlOutputBean();

		if (!StringUtils.isEmpty(outputXML)) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(outputXML)));

			XPath xPath = XPathFactory.newInstance().newXPath();

			Node codiceOutput = (Node) xPath.evaluate("//CodiceOutput/text()", doc, XPathConstants.NODE);
			if (codiceOutput != null) {
				bean.setCodice(Integer.parseInt(codiceOutput.getNodeValue()));
			}

			Node descrizioneOutput = (Node) xPath.evaluate("//DescrizioneOutput/text()", doc, XPathConstants.NODE);
			if (descrizioneOutput != null) {
				bean.setDescrizione(descrizioneOutput.getNodeValue());
			}

		}

		return bean;

	}

	private String createInputXml(Map<String, String> bean) {

		_logger.debug("[FirmaGrafometrica] --> createInputXml");

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc;
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
		String numKloDocumento = (String) bean.get(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NUM_KLO_DOCUMENTO);

		_logger.debug("[FirmaGrafometrica] --> getServiceXmlInputBean --> numKloDocumento: " + numKloDocumento);

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

			// numKloDocumento elements
			Element numKloDocumentoElem = doc.createElement("numKloDocumento");
			numKloDocumentoElem.appendChild(doc.createTextNode(numKloDocumento));
			serviceproviderElem.appendChild(numKloDocumentoElem);

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

		_logger.debug("[FirmaGrafometrica] --> createInputXml --");

		return output;

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

	private String getWSUrlOld(String urlName) throws Exception {

		String url = "";

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();

		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndiName, urlName);
		url = endPoint.getUrl();

		_logger.debug("Web Service Firma Grafometrica URL: " + url);

		return url;

	}

	private String getWSUrl(String urlName) throws Exception {

		String url = "";

		Object[] inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = urlName;
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("SELECT_TS_ENDPOINT", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		url = (String) ret.getAttribute("ROW.STRURL");

		_logger.debug("Web Service Firma Grafometrica URL: " + url);

		return url;

	}

	private byte[] convertFileToBytesArray(File file) throws Exception {

		byte[] fileBytes;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		FileInputStream fis = new FileInputStream(file);
		// System.out.println(file.exists() + "!!");
		// InputStream in = resource.openStream();

		byte[] buf = new byte[1024];

		for (int readNum; (readNum = fis.read(buf)) != -1;) {
			bos.write(buf, 0, readNum); // no doubt here is 0
			// Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
			// System.out.println("read " + readNum + " bytes,");
		}

		fileBytes = bos.toByteArray();

		return fileBytes;
	}

	private File convertBytesArrayToFileOS(byte[] fileBytes, String fileName) {

		BufferedOutputStream bs = null;
		FileOutputStream fs = null;
		File file = new File(fileName);

		try {

			fs = new FileOutputStream(file);
			bs = new BufferedOutputStream(fs);
			bs.write(fileBytes);
			bs.close();
			bs = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bs != null) {
			try {
				bs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	private BlobFG printErrorOutput(BlobFG blob, Map<String, Object> beanOutput, String errorMessage) {

		_logger.error("[FirmaGrafometrica Client WS] -> doSignedPDF --- Error: " + errorMessage);

		beanOutput.put("codiceOutput", "0");
		beanOutput.put("descrizioneOutput", "Nessuna trasformazione file effettuata. Errore: " + errorMessage);
		String xmlOutput = createOutputXml(beanOutput);

		blob.setFilePDF(null);
		blob.setXmlOutput(xmlOutput);

		return blob;

	}

}
