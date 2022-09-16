package it.eng.myportal.entity.ejb.youthGuarantee;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.axis.holders.DateHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgAdesioneStoriaHome;
import it.eng.myportal.entity.home.YgNotificaFailHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.enums.ErroreNotificaCambioStatoAdesioneYGEnum;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.utils.yg.YgDebugConstants;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioAdesione;
import it.eng.myportal.youthGuarantee.checkUtenteYG.CheckUtenteYG;
import it.eng.myportal.youthGuarantee.utenteYG.UtenteygType;
import it.gov.lavoro.servizi.servizicoap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoap.types.Risposta_invioUtenteYG_TypeEsito;
import it.gov.lavoro.servizi.servizicoap.types.holders.Risposta_checkUtenteYG_TypeEsitoHolder;
import it.gov.lavoro.servizi.servizicoap.types.holders.Risposta_invioUtenteYG_TypeEsitoHolder;
import it.gov.lavoro.servizi.servizicoapAdesioneGet.types.Risposta_getStatoAdesioneYG_TypeEsito;
import it.gov.lavoro.servizi.servizicoapAdesioneGet.types.holders.Risposta_getStatoAdesioneYG_TypeEsitoHolder;
import it.gov.lavoro.servizi.servizicoapAdesioneSet.types.Risposta_setStatoAdesioneYG_TypeEsito;
import it.gov.lavoro.servizi.servizicoapAdesioneSet.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder;

/**
 * Session Bean implementation class ClicLavoroEjb
 */
@Stateless
public class YouthGuaranteeAdesioneEjb {

	protected final Log log = LogFactory.getLog(YouthGuaranteeAdesioneEjb.class);

	private final String GET_STATO_ADESIONE_XSD_FILE = "Rev001_-_GetStatoAdesione_-_Allegato_B_-_Formato_trasmissione.xsd";
	private final String SET_STATO_ADESIONE_XSD_FILE = "Rev001_-_SetStatoAdesione_-_Allegato_B_-_Formato_trasmissione.xsd";
	private final String NOTIFICA_CAMBIO_STATO_ADESIONE_XSD_FILE = "Rev001_-_NotificaCambioStatoAdesione_-_Allegato_B_-_Formato_trasmissione.xsd";

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YgAdesioneStoriaHome ygAdesioneStoriaHome;

	@EJB
	private DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	private YgNotificaFailHome ygNotificaFailHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private DeRegioneHome deRegioneHome;

	/**
	 * chiamata al WS della PDD per inviare l'adesione dell'Utente
	 * 
	 * @param xmlRichiestaAdesione
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RisultatoInvioAdesione inviaAdesione(String xmlRichiestaAdesione) throws RemoteException {

		boolean success = false;
		String strMessaggioErrore = null;

		// effettua l'invio
		String inviaAdesioneAddress = wsEndpointHome.getYouthGuaranteeAdesioneAddress();

		Risposta_invioUtenteYG_TypeEsitoHolder esito = new Risposta_invioUtenteYG_TypeEsitoHolder();
		StringHolder messaggioErrore = new StringHolder();

		ServizicoapWSProxy proxyYG = new ServizicoapWSProxy(inviaAdesioneAddress);
		log.info("Invio adesione PRE");
		proxyYG.invioUtenteYG(xmlRichiestaAdesione, esito, messaggioErrore);
		log.info("Invio adesione POST");

		if (esito.value.getValue().equalsIgnoreCase(Risposta_invioUtenteYG_TypeEsito._OK)) {
			log.info("Invio adesione: OK");
			success = true;
		} else {
			log.info("Invio adesione: KO" + "\nErrore:\n" + messaggioErrore.value + "\nXml:" + xmlRichiestaAdesione);
			success = false;
		}

		// gestione risultato

		if (messaggioErrore != null) {
			strMessaggioErrore = messaggioErrore.value;
		}

		RisultatoInvioAdesione risultatoInvioAdesione = new RisultatoInvioAdesione();
		risultatoInvioAdesione.setSuccess(success);
		risultatoInvioAdesione.setMessaggioErrore(strMessaggioErrore);

		return risultatoInvioAdesione;

	}

	/**
	 * chiamata al WS della PDD per verificare l'esistenza di una adesione per
	 * l'Utente inviato
	 * 
	 * @param xmlRichiestaVerifica
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean checkUtente(String xmlRichiestaVerifica, String codCheckYgStop) throws RemoteException {
		// effettua l'invio
		String checkUtenteAddress = wsEndpointHome.getYouthGuaranteeCheckUtenteAddress();

		Risposta_checkUtenteYG_TypeEsitoHolder esito = new Risposta_checkUtenteYG_TypeEsitoHolder();
		StringHolder messaggioErrore = new StringHolder();

		ServizicoapWSProxy proxyYG = new ServizicoapWSProxy(checkUtenteAddress);
		log.info("Invio check utente PRE");
		proxyYG.checkUtenteYG(xmlRichiestaVerifica, esito, messaggioErrore);
		log.info("Invio check utente POST");

		if (esito.value.getValue().equalsIgnoreCase(codCheckYgStop)) {
			log.info("Invio check utente: " + esito.value.getValue() + ", adesione presente");
			return true;
		} else {
			log.info("Invio check utente: " + esito.value.getValue() + ", adesione non presente");
			return false;
		}
	}

	/**
	 * converte in stringa da spedire alla PDD l'oggetto JAXB Utente (YG)
	 * 
	 * @param utente
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public String convertToString(UtenteygType utenteType) throws JAXBException, SAXException {
		StringWriter stringWriter = new StringWriter();
		JAXBContext jc = JAXBContext.newInstance(UtenteygType.class);
		Marshaller jaxbMarshaller = jc.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		QName qName = new QName("", "sezione0");
		JAXBElement<UtenteygType> root = new JAXBElement<UtenteygType>(qName, UtenteygType.class, utenteType);
		jaxbMarshaller.marshal(root, stringWriter);
		String xmlUtenteYG = stringWriter.toString();
		return xmlUtenteYG;
	}

	/**
	 * converte in oggetto Utente (YG) JAXB la stringa ricevuta
	 * 
	 * @param xmlUtenteYG
	 * @return
	 * @throws JAXBException
	 */
	public UtenteygType convertToUtente(String xmlUtenteYG) throws JAXBException {
		JAXBContext jaxbContext;
		UtenteygType utenteYG = null;
		try {

			jaxbContext = JAXBContext.newInstance(it.eng.myportal.youthGuarantee.utenteYG.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "Rev004_UTENTI-YG.xsd");
			jaxbUnmarshaller.setSchema(schema);
			JAXBElement<UtenteygType> root = (JAXBElement<UtenteygType>) jaxbUnmarshaller.unmarshal(new StringReader(
					xmlUtenteYG));
			utenteYG = root.getValue();

		} catch (Exception e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml: " + e.getMessage());
			utenteYG = null;
		}
		return utenteYG;
	}

	public CheckUtenteYG convertToCheckUtente(String xmlCheckUtenteYG) throws JAXBException {
		JAXBContext jaxbContext;
		CheckUtenteYG checkUtenteYG = null;
		try {
			jaxbContext = JAXBContext.newInstance(CheckUtenteYG.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			checkUtenteYG = (CheckUtenteYG) jaxbUnmarshaller.unmarshal(new StringReader(xmlCheckUtenteYG));
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return checkUtenteYG;
	}

	public String convertCheckUtenteYGToString(CheckUtenteYG checkUtenteYG) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(CheckUtenteYG.class);
		Marshaller marshaller = jc.createMarshaller();
		// Schema schema = Utils.getXsdSchema("yg" + File.separator +
		// "Rev001_CodiceFiscale_UTENTI-YG.xsd");
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		// marshaller.setSchema(schema);
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(checkUtenteYG, writer);
		String xmlCheckUtenteYG = writer.getBuffer().toString();
		return xmlCheckUtenteYG;
	}

	public void getStatoAdesioneYG(String xml, Risposta_getStatoAdesioneYG_TypeEsitoHolder esitoHolder,
			StringHolder messaggioErroreHolder, StringHolder statoAdesioneHolder, DateHolder dataAdesioneHolder,
			CalendarHolder dataStatoAdesioneHolder, Integer idPfPrincipalIns) {
		log.info("inizio getStatoAdesioneYG: datiStatoAdesione =\n" + xml);

		try {
			parseGetStatoAdesioneInput(xml);
		} catch (Exception e) {
			/* errore parsing */
			esitoHolder.value = Risposta_getStatoAdesioneYG_TypeEsito
					.fromString(Risposta_getStatoAdesioneYG_TypeEsito._KO);
			messaggioErroreHolder.value = "Errore di validazione XML";
			log.error("getStatoAdesione: XML mal formattato\n" + xml + "\n" + e.getMessage());
			ygNotificaFailHome.logErroreParsing(xml, "P", idPfPrincipalIns);
			return;
		}

		/* da XML a JAXB + recupero adesione di riferiemnto */
		it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = unmarshallDatiStatoAdesioneGet(xml);
		String codiceFiscale = datiStatoAdesione.getCodiceFiscale();
		Date dataAdesione = null;
		String codRegioneMin = StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0");
		DeRegione deRegione = deRegioneHome.findByCodRegioneMin(codRegioneMin);
		if (deRegione == null) {
			/* errore parsing */
			esitoHolder.value = Risposta_getStatoAdesioneYG_TypeEsito
					.fromString(Risposta_getStatoAdesioneYG_TypeEsito._KO);
			messaggioErroreHolder.value = "Errore di validazione XML";
			log.error("getStatoAdesione: Il codice regione ministeriale non esiste\n" + xml);
			ygNotificaFailHome.logErroreParsing(xml, "P", idPfPrincipalIns);
			return;
		}
		String codRegione = deRegione.getCodRegione();
		YgAdesione adesione = null;

		if (YgDebugConstants.IS_DEBUG) {
			String codiceFiscaleDEBUG = datiStatoAdesione.getCodiceFiscale();
			// in debug fa come se il ministero tornasse l'ultima adesione
			// attiva presente su DB
			YgAdesione adesioneDEBUG = ygAdesioneHome.findLatestByCodiceFiscaleInRegionePortale(codiceFiscaleDEBUG);
			if (adesioneDEBUG != null) {
				adesione = adesioneDEBUG;
				esitoHolder.value = Risposta_getStatoAdesioneYG_TypeEsito
						.fromString(Risposta_getStatoAdesioneYG_TypeEsito._OK);
				messaggioErroreHolder.value = "Adesione trovata";
				dataAdesioneHolder.value = adesioneDEBUG.getDtAdesione();
				// statoAdesioneHolder.value =
				// adesioneDEBUG.getDeStatoAdesioneMin().getCodStatoAdesioneMin();
				statoAdesioneHolder.value = "P";
				Calendar dsa = GregorianCalendar.getInstance();
				dsa.setTime(adesioneDEBUG.getDtStatoAdesioneMin());
				dataStatoAdesioneHolder.value = dsa;
			} else {
				esitoHolder.value = Risposta_getStatoAdesioneYG_TypeEsito
						.fromString(Risposta_getStatoAdesioneYG_TypeEsito._KO);
				messaggioErroreHolder.value = "Adesione non trovata";
			}
		} else {
			it.gov.lavoro.servizi.servizicoapAdesioneGet.ServizicoapWSProxy service = new it.gov.lavoro.servizi.servizicoapAdesioneGet.ServizicoapWSProxy(
					wsEndpointHome.getYGGetStatoAdesioneMin());
			try {
				service.getStatoAdesioneYG(xml, esitoHolder, messaggioErroreHolder, dataAdesioneHolder,
						statoAdesioneHolder, dataStatoAdesioneHolder);
				// dataAdesioneHolder.valuee' valorizzato SOLO dopo la chiamata
				// al ministero!
				dataAdesione = dataAdesioneHolder.value;
				adesione = ygAdesioneHome.findByCodiceFiscaleDataRegioneAdesione(codiceFiscale, dataAdesione,
						codRegione);
			} catch (RemoteException e) {
				/* errore in invio */
				esitoHolder.value = Risposta_getStatoAdesioneYG_TypeEsito
						.fromString(Risposta_getStatoAdesioneYG_TypeEsito._KO);
				String msgErr = "Errore durante la comunicazione con il ministero";
				messaggioErroreHolder.value = msgErr + ": " + e.getMessage();
				log.error("getStatoAdesione: " + msgErr + "\n" + xml + "\n" + e.getMessage());
				ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, null,
						ErroreNotificaCambioStatoAdesioneYGEnum.E.getCodice(), msgErr, "P", idPfPrincipalIns);
				return;
			}
		}

		if (esitoHolder.value.getValue().equals(Risposta_setStatoAdesioneYG_TypeEsito._KO)) {
			/* errore dal ministero */
			return;
		}

		if (adesione != null
				&& !adesione.getDeStatoAdesioneMin().getCodStatoAdesioneMin().equals(statoAdesioneHolder.value)) {
			/* ho l'adesione con uno stato differente */

			/* storicizzo lo stato dell'adesione */
			ygAdesioneStoriaHome.storicizza(adesione, idPfPrincipalIns);

			/* aggiorno i dati dell'adesione */
			adesione.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(statoAdesioneHolder.value));
			adesione.setDtStatoAdesioneMin(dataStatoAdesioneHolder.value.getTime());
			ygAdesioneHome.merge(adesione);
		}

		log.info("fine getStatoAdesioneYG");
	}

	public void setStatoAdesioneYG(String codiceFiscale, XMLGregorianCalendar dataAdesione, String codRegioneMin,
			String codStatoAdesioneMin, Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder,
			StringHolder messaggioErroreHolder, Integer idPfPrincipalIns) {
		String datiStatoAdesione = marshallDatiStatoAdesioneSet(codiceFiscale, dataAdesione, codRegioneMin,
				codStatoAdesioneMin);
		setStatoAdesioneYG(datiStatoAdesione, esitoHolder, messaggioErroreHolder, idPfPrincipalIns);
	}

	/**
	 * Metodo da chiamare per il cambio di stato di un'adesione. Controlla che
	 * il cambiamento di stato avvenga tra stati congrui (cioe' che lo stato
	 * attuale cambi in uno stato valido secondo le specifiche ministeriali) e
	 * chiama il WS ministeriale per comunicare il nuovo stato.
	 */
	public void setStatoAdesioneYG(String xml, Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder,
			StringHolder messaggioErroreHolder, Integer idPfPrincipalIns) {
		log.info("inizio setStatoAdesioneYG: datiStatoAdesione =\n" + xml);

		try {
			parseSetStatoAdesioneInput(xml);
		} catch (Exception e) {
			/* errore parsing */
			esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
					.fromString(Risposta_setStatoAdesioneYG_TypeEsito._KO);
			messaggioErroreHolder.value = "Errore di validazione XML";
			log.error("setStatoAdesione: XML mal formattato\n" + xml + "\n" + e.getMessage());
			ygNotificaFailHome.logErroreParsing(xml, "P", idPfPrincipalIns);
			return;
		}

		/* da XML a JAXB + recupero adesione di riferiemnto */
		it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = unmarshallDatiStatoAdesioneSet(xml);
		String codiceFiscale = datiStatoAdesione.getCodiceFiscale();
		Date dataAdesione = null;
		try {
			dataAdesione = Utils.gregorianDateToDate(datiStatoAdesione.getDataAdesione());
		} catch (Exception e) {
			esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
					.fromString(Risposta_setStatoAdesioneYG_TypeEsito._KO);
			String msgErr = "Errore nel parsing della data adesione";
			messaggioErroreHolder.value = msgErr;
			log.error("setStatoAdesione: " + msgErr + "\n" + xml + "\n" + e.getMessage());
			ygNotificaFailHome.logErroreParsing(xml, "P", idPfPrincipalIns);
			return;
		}
		String codRegioneMin = StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0");
		DeRegione deRegione = deRegioneHome.findByCodRegioneMin(codRegioneMin);
		String codRegione = deRegione.getCodRegione();
		YgAdesione adesione = ygAdesioneHome.findByCodiceFiscaleDataRegioneAdesione(codiceFiscale, dataAdesione,
				codRegione);

		Date dtStatoAdesioneMin = Calendar.getInstance().getTime();

		if (YgDebugConstants.IS_DEBUG) {
			esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
					.fromString(Risposta_setStatoAdesioneYG_TypeEsito._OK);
			messaggioErroreHolder.value = "MESSAGGIO DI ERRORE DI TEST";
		} else {
			it.gov.lavoro.servizi.servizicoapAdesioneSet.ServizicoapWSProxy service = new it.gov.lavoro.servizi.servizicoapAdesioneSet.ServizicoapWSProxy(
					wsEndpointHome.getYGSetStatoAdesioneMin());
			try {
				service.setStatoAdesioneYG(xml, esitoHolder, messaggioErroreHolder);
			} catch (RemoteException e) {
				/* errore in invio */
				esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
						.fromString(Risposta_setStatoAdesioneYG_TypeEsito._KO);
				String msgErr = "Errore durante la comunicazione con il ministero";
				messaggioErroreHolder.value = msgErr;
				log.error("setStatoAdesione: " + msgErr + "\n" + xml + "\n" + e.getMessage());
				ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, dtStatoAdesioneMin,
						ErroreNotificaCambioStatoAdesioneYGEnum.E.getCodice(), msgErr, "P", idPfPrincipalIns);
				return;
			}
		}

		if (esitoHolder.value.getValue().equals(Risposta_setStatoAdesioneYG_TypeEsito._KO)) {
			/* errore dal ministero */
			log.error("setStatoAdesione: " + messaggioErroreHolder.value + "\n" + xml);
			ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, dtStatoAdesioneMin,
					ErroreNotificaCambioStatoAdesioneYGEnum.EM.getCodice(), messaggioErroreHolder.value, "P",
					idPfPrincipalIns);
			return;
		}

		if (adesione == null) {
			/* adesione non presente */
			esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
					.fromString(Risposta_setStatoAdesioneYG_TypeEsito._KO);
			messaggioErroreHolder.value = ErroreNotificaCambioStatoAdesioneYGEnum.NP.getMessaggio();
			log.error("setStatoAdesione: " + ErroreNotificaCambioStatoAdesioneYGEnum.NP.getMessaggio() + "\n" + xml);
			ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, dtStatoAdesioneMin,
					ErroreNotificaCambioStatoAdesioneYGEnum.NP.getCodice(),
					ErroreNotificaCambioStatoAdesioneYGEnum.NP.getMessaggio(), "P", idPfPrincipalIns);
			return;
		}

		if (!adesione.getDeStatoAdesioneMin().getCodStatoAdesioneMin().equals("A")
				&& (datiStatoAdesione.getStatoAdesione().equals("N")
						|| datiStatoAdesione.getStatoAdesione().equals("D") || datiStatoAdesione.getStatoAdesione()
						.equals("U"))) {
			/* stato adesione non congruo */
			esitoHolder.value = Risposta_setStatoAdesioneYG_TypeEsito
					.fromString(Risposta_setStatoAdesioneYG_TypeEsito._KO);
			messaggioErroreHolder.value = ErroreNotificaCambioStatoAdesioneYGEnum.ST.getMessaggio();
			log.error("setStatoAdesione: " + ErroreNotificaCambioStatoAdesioneYGEnum.ST.getMessaggio() + "\n" + xml);
			ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, dtStatoAdesioneMin,
					ErroreNotificaCambioStatoAdesioneYGEnum.ST.getCodice(),
					ErroreNotificaCambioStatoAdesioneYGEnum.ST.getMessaggio(), "P", idPfPrincipalIns);
			return;
		}

		/* storicizzo lo stato dell'adesione */
		ygAdesioneStoriaHome.storicizza(adesione, idPfPrincipalIns);

		/* aggiorno i dati dell'adesione */
		adesione.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(datiStatoAdesione.getStatoAdesione()));
		adesione.setDtStatoAdesioneMin(dtStatoAdesioneMin);
		ygAdesioneHome.merge(adesione);

		log.info("fine setStatoAdesioneYG");
	}

	public void parseGetStatoAdesioneInput(String xml) throws SAXException, IOException {
		Utils.validateXml(xml, "adesione" + File.separator + GET_STATO_ADESIONE_XSD_FILE);
	}

	public void parseSetStatoAdesioneInput(String xml) throws SAXException, IOException {
		Utils.validateXml(xml, "adesione" + File.separator + SET_STATO_ADESIONE_XSD_FILE);
	}

	public void parseNotificaCambioStatoAdesioneInput(String xml) throws SAXException, IOException {
		Utils.validateXml(xml, "adesione" + File.separator + NOTIFICA_CAMBIO_STATO_ADESIONE_XSD_FILE);
	}

	public it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione unmarshallDatiStatoAdesioneGet(
			String xml) {
		it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione> root = unmarshaller
					.unmarshal(new StreamSource(new StringReader(xml)),
							it.eng.myportal.youthGuarantee.getStatoAdesione.input.DatiStatoAdesione.class);
			datiStatoAdesione = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return datiStatoAdesione;
	}

	public it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione unmarshallDatiStatoAdesioneSet(
			String xml) {
		it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione> root = unmarshaller
					.unmarshal(new StreamSource(new StringReader(xml)),
							it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione.class);
			datiStatoAdesione = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return datiStatoAdesione;
	}

	public it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione unmarshallDatiStatoAdesioneNotifica(
			String xml) {
		it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione> root = unmarshaller
					.unmarshal(new StreamSource(new StringReader(xml)),
							it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione.class);
			datiStatoAdesione = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return datiStatoAdesione;
	}

	public String marshallDatiStatoAdesioneSet(String codiceFiscale, XMLGregorianCalendar dataAdesione,
			String codRegioneMin, String codStatoAdesioneMin) {
		it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = new it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione();
		datiStatoAdesione.setCodiceFiscale(codiceFiscale);
		datiStatoAdesione.setDataAdesione(dataAdesione);
		datiStatoAdesione.setRegioneAdesione(codRegioneMin);
		datiStatoAdesione.setStatoAdesione(codStatoAdesioneMin);
		String xml = null;

		try {
			JAXBContext jc = JAXBContext
					.newInstance(it.eng.myportal.youthGuarantee.setStatoAdesione.input.DatiStatoAdesione.class);
			Marshaller marshaller = jc.createMarshaller();
			// Schema schema = Utils.getXsdSchema("adesione" + File.separator +
			// SET_STATO_ADESIONE_XSD_FILE);
			// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			// marshaller.setSchema(schema);
			marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			StringWriter writer = new StringWriter();
			marshaller.marshal(datiStatoAdesione, writer);
			xml = writer.getBuffer().toString();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return xml;
	}

	/**
	 * Annullamento dell'adesione da parte del lavoratore. Invocata dalla
	 * portlet nell'homepage del lavoratore.
	 * 
	 * @param idYgAdesione
	 */
	public void annullaAdesioneYG(Integer idYgAdesione, Integer idPfPrincipalIns, String codAnnullamento) {
		YgAdesione adesione = ygAdesioneHome.findById(idYgAdesione);

		Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder = new Risposta_setStatoAdesioneYG_TypeEsitoHolder();
		StringHolder messaggioErroreHolder = new StringHolder();

		try {
			XMLGregorianCalendar xgc = Utils.dateToGregorianDate(adesione.getDtAdesione());
			setStatoAdesioneYG(adesione.getCodiceFiscale(), xgc, adesione.getDeRegione().getCodMin(),
					codAnnullamento, esitoHolder, messaggioErroreHolder, idPfPrincipalIns);
		} catch (Exception e) {
			log.error("annullaAdesione: Impossibile annullare l'adesione, idYgAdesione = " + idYgAdesione + ": "
					+ e.getMessage());
			throw new MyPortalException(e.getMessage(), true);
		}

		if (esitoHolder.value.getValue().equals(Risposta_setStatoAdesioneYG_TypeEsito._KO)) {
			log.error("annullaAdesione: errore di sistema nell'annullamento, risposta KO");
			throw new MyPortalException(messaggioErroreHolder.value, true);
		}

		return;
	}
}
