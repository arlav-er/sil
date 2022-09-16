package it.eng.myportal.entity.ejb.youthGuarantee;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioSap;
import it.eng.myportal.youthGuarantee.richiestaSAP.IDSAP;
import it.eng.myportal.youthGuarantee.sap.LavoratoreType;
import it.eng.myportal.youthGuarantee.verificaSAP.VerificaSAP;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;

@Stateless
public class YouthGuaranteeSapEjb {

	protected final Log log = LogFactory.getLog(YouthGuaranteeSapEjb.class);

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	WsEndpointHome wsEndpointHome;	
	
	/**
	 * chiamata al WS della PDD per inviare la SAP dell'Utente  
	 * 
	 * @param xmlRichiestaAdesione
	 * @throws RemoteException 
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RisultatoInvioSap inviaSap(String xmlSAP) throws RemoteException {		

		boolean success = false;
		String strMessaggioErrore = null;
		String strCodiceSap = null;
		
		// effettua l'invio
		String inviaSapAddress = wsEndpointHome.getYouthGuaranteeInvioSapAddress();
			
		Risposta_invioSAP_TypeEsitoHolder esito = new Risposta_invioSAP_TypeEsitoHolder();
		StringHolder messaggioErrore = new StringHolder();
		StringHolder codiceSAP = new StringHolder();
		
		ServizicoapWSProxy proxySapYG = new ServizicoapWSProxy(inviaSapAddress);
		log.info("Invio SAP PRE");
		proxySapYG.invioSAP(xmlSAP, esito, messaggioErrore, codiceSAP);		
		log.info("Invio SAP POST");
		
		Risposta_invioSAP_TypeEsito esitoRisposta = esito.value;			
		if (esito.value.getValue().equalsIgnoreCase(Risposta_invioSAP_TypeEsito._OK)) {
			log.info("Invio SAP: OK");
			success = true;
		} 
		else {
			log.info("Invio SAP: KO" + "\nErrore:\n" + messaggioErrore.value + "\nXml:\n" + xmlSAP);
			success = false;
		}
		
		// gestione risultato
		
		if (messaggioErrore != null) {
			strMessaggioErrore = messaggioErrore.value;
		}
		
		if (codiceSAP != null) {
			strCodiceSap = codiceSAP.value;
		}
		
		RisultatoInvioSap risultatoInvioSap = new RisultatoInvioSap();
		risultatoInvioSap.setSuccess(success);
		risultatoInvioSap.setMessaggioErrore(strMessaggioErrore);
		risultatoInvioSap.setCodiceSAP(strCodiceSap);
		
		return risultatoInvioSap;
		
	}
	
	/**
	 * chiamata al WS della PDD per verificare l'esistenza di una SAP per l'Utente inviato
	 * 
	 * @param xmlRichiestaVerifica
	 * @return 
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String verificaEsistenzaSap(String xmlCodiceFiscaleCittadino) throws RemoteException {
		String codiceRitorno = "";
		// effettua l'invio
		String checkSapAddress = wsEndpointHome.getYouthGuaranteeCheckSapAddress();
		
		ServizicoapWSProxy proxySapYG = new ServizicoapWSProxy(checkSapAddress);
		log.info("Verifica esistenza SAP PRE");
		codiceRitorno = proxySapYG.verificaEsistenzaSAP(xmlCodiceFiscaleCittadino);
		if (codiceRitorno != null) {
			log.info("Verifica esistenza SAP POST (" + codiceRitorno + ")");
		} else {
			log.info("Verifica esistenza SAP POST ");
		}
							
		return codiceRitorno;
	}
	
	/**
	 * chiamata al WS della PDD per richiedere una SAP tramite identificativo
	 * 
	 * @param identificativoSap
	 * @return 
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public LavoratoreType richiestaSap(String xmlIdentificativoSap) throws RemoteException {
		
		LavoratoreType lavoratoreType = null;
		String sap = "";
		String checkSapAddress = wsEndpointHome.getYouthGuaranteeRichiestaSapAddress();
		
		ServizicoapWSProxy proxySapYG = new ServizicoapWSProxy(checkSapAddress);
		log.info("Richiesta SAP PRE");
		sap = proxySapYG.richiestaSAP(xmlIdentificativoSap);
		log.info("Richiesta SAP POST");
		
		if (sap != null && !("").equalsIgnoreCase(sap)) {
			try {
				lavoratoreType = convertToLavoratoreSAP(sap);
			} catch (JAXBException e) {
				log.error("Errore durante l'elaborazione della risposta della chiamata al servizio richiestaSap: " + e.getMessage());
			}
		}
		else {
			log.info("SAP nulla/non trovata");
		}
		
		return lavoratoreType;
		
	}
	
	/**
	 * converte in stringa da spedire alla PDD l'oggetto JAXB VerificaSAP (YG) 
	 * 
	 * @param verificaSAP
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public String convertVerificaSapToString(VerificaSAP verificaSAP) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(VerificaSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		Schema schema = Utils.getXsdSchema("yg" + File.separator + "Rev001_SAP_Esiste_SchedaAP.xsd");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		marshaller.setSchema(schema);
		StringWriter writer = new StringWriter();
		marshaller.marshal(verificaSAP, writer);
		String xmlVerificaSAP = writer.getBuffer().toString();
		return xmlVerificaSAP;
	}
	
	/**
	 * converte in stringa da spedire alla PDD l'oggetto JAXB RichiestaSAP (YG) 
	 * 
	 * @param richiestaSAP
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public String convertRichiestaSapToString(IDSAP richiestaSAP) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		Schema schema = Utils.getXsdSchema("yg" + File.separator + "Rev001_SAP_Richiesta_SchedaAP.xsd");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		marshaller.setSchema(schema);
		StringWriter writer = new StringWriter();
		marshaller.marshal(richiestaSAP, writer);
		String xmlRichiestaSAP = writer.getBuffer().toString();
		return xmlRichiestaSAP;
	}
	
	/**
	 * converte in oggetto VerificaSAP (YG) JAXB la stringa ricevuta 
	 * 
	 * @param xmlVerificaSAP
	 * @return
	 * @throws JAXBException
	 */
	public VerificaSAP convertToVerificaSAP(String xmlVerificaSAP) throws JAXBException {
		JAXBContext jaxbContext;
		VerificaSAP verificaSap = null;
		try {
			jaxbContext = JAXBContext.newInstance(VerificaSAP.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			verificaSap = (VerificaSAP) jaxbUnmarshaller.unmarshal(new StringReader(xmlVerificaSAP));
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return verificaSap;
	}
	
	/**
	 * converte in stringa da spedire alla PDD l'oggetto JAXB LavoratoreType-SAP (YG) 
	 * 
	 * @param lavoratoreSAP
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public String convertSapToString(LavoratoreType lavoratoreSAP) throws JAXBException, SAXException {
		  StringWriter stringWriter = new StringWriter();
		 
		  JAXBContext jaxbContext = JAXBContext.newInstance(LavoratoreType.class);
		  Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 
		  Schema schema = Utils.getXsdSchema("yg" + File.separator + "Rev005_SAP_trasmissione_SchedaAP.xsd");			
		  jaxbMarshaller.setSchema(schema);
		  
		  // format the XML output
		  jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		  jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		 
		  QName qName = new QName("", "lavoratore");		  
		  JAXBElement<LavoratoreType> root = new JAXBElement<LavoratoreType>(qName, LavoratoreType.class, lavoratoreSAP);
		 
		  jaxbMarshaller.marshal(root, stringWriter);
		 
		  String result = stringWriter.toString();
		  
		  return result;
		 }
	

	/**
	 * converte in oggetto LavoratoreType (YG) JAXB la stringa ricevuta 
	 * 
	 * @param xmlSAP
	 * @return
	 * @throws JAXBException
	 */
	public LavoratoreType convertToLavoratoreSAP(String xmlSAP) throws JAXBException {
		JAXBContext jaxbContext;
		LavoratoreType sap = null;
		try {
				
			jaxbContext = JAXBContext.newInstance(it.eng.myportal.youthGuarantee.sap.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();			
			JAXBElement<LavoratoreType> root = (JAXBElement<LavoratoreType>) jaxbUnmarshaller.unmarshal(new StringReader(xmlSAP));
			sap = root.getValue();
			
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return sap;
	}

		
}
