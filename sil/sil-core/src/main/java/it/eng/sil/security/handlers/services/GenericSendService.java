package it.eng.sil.security.handlers.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.MessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GenericSendService {
	private SOAPBody bodyMessage = null;
	private String mittente = null;
	private String servizio = null;
	private String codFiscaleReq = null;
	private String codEsito = null;
	private String tipo = null;
	private static final String nomeMittenteDefault = "PORTALE SERVIZI";

	public GenericSendService(MessageContext msgContext, String tpMessage) throws Exception {
		setTipo(tpMessage);
		SOAPMessage message = msgContext.getMessage();
		this.bodyMessage = message.getSOAPBody();
		if (getTipo().equalsIgnoreCase("REQUEST")) {
			if (msgContext.getService() != null) {
				setServizio(msgContext.getService().getName());
			}
		}
	}

	public void setDati() throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();

		if (getTipo().equalsIgnoreCase("REQUEST")) {
			Node getMetodo = bodyMessage.getFirstChild();
			Node getInput = getMetodo.getLastChild();
			NodeList childNodes = getInput.getChildNodes();
			String datiXML = childNodes.item(0).getNodeValue();
			InputStream is = new ByteArrayInputStream(datiXML.getBytes("UTF-8"));
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document docReq = documentBuilder.parse(is);
			Element root = docReq.getDocumentElement();
			String mitt = root.getAttribute("mittente");
			if (mitt == null || mitt.equals("")) {
				mitt = nomeMittenteDefault;
			}
			setMittente(mitt);
			String path = "CodiceFiscale";
			Node currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
			if (currNode != null) {
				Node n = currNode.getFirstChild();
				if (n != null) {
					setCodiceFiscaleReq(n.getNodeValue());
				}
			}
			if (this.codFiscaleReq == null) {
				path = "codiceFiscale";
				currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
				if (currNode != null) {
					Node n = currNode.getFirstChild();
					if (n != null) {
						setCodiceFiscaleReq(n.getNodeValue());
					}
				}
			}
		} else {
			Node getResponse = bodyMessage.getFirstChild();
			Node getReturn = getResponse.getFirstChild();
			NodeList childNodes = getReturn.getChildNodes();

			String datiXML = childNodes.item(0).getNodeValue();

			InputStream is = new ByteArrayInputStream(datiXML.getBytes("UTF-8"));
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document docResp = documentBuilder.parse(is);

			Element root = docResp.getDocumentElement();
			String path = "Esito/codice";
			Node currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
			if (currNode != null) {
				Node n = currNode.getFirstChild();
				if (n != null) {
					setCodEsito(n.getNodeValue());
				}
			}
			path = "*/CodiceFiscale";
			currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
			if (currNode != null) {
				Node n = currNode.getFirstChild();
				if (n != null) {
					setCodiceFiscaleReq(n.getNodeValue());
				}
			}
			if (this.codFiscaleReq == null) {
				path = "*/codiceFiscale";
				currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
				if (currNode != null) {
					Node n = currNode.getFirstChild();
					if (n != null) {
						setCodiceFiscaleReq(n.getNodeValue());
					}
				}
			}
		}
	}

	public void setDati(boolean isAnpal) throws Exception {
		if (getTipo().equalsIgnoreCase("REQUEST")) {

		} else {
			Node getResponse = bodyMessage.getFirstChild();
			Node getReturn = getResponse.getFirstChild();
			NodeList childNodes = getReturn.getChildNodes();

			String datiXML = childNodes.item(0).getNodeValue();

			InputStream is = new ByteArrayInputStream(datiXML.getBytes("UTF-8"));
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document docResp = documentBuilder.parse(is);

			Element root = docResp.getDocumentElement();
			String path = "IdEsito";

			XPath xpath = XPathFactory.newInstance().newXPath();
			Node currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
			if (currNode != null) {
				Node n = currNode.getFirstChild();
				if (n != null) {
					setCodEsito(n.getNodeValue());
				}
			}
		}
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mitt) {
		this.mittente = mitt;
	}

	public String getServizio() {
		return servizio;
	}

	public void setServizio(String service) {
		this.servizio = service;
	}

	public String getCodiceFiscaleReq() {
		return codFiscaleReq;
	}

	public void setCodiceFiscaleReq(String cf) {
		this.codFiscaleReq = cf;
	}

	public String getCodEsito() {
		return codEsito;
	}

	public void setCodEsito(String esito) {
		this.codEsito = esito;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tp) {
		this.tipo = tp;
	}

}
