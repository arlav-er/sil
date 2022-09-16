package it.eng.myportal.ws;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.youthGuarantee.dataAdesioneGO.input.Cittadino;
import it.eng.myportal.youthGuarantee.dataAdesioneGO.output.Risposta;

@WebService(serviceName = "DataAdesioneGO")
public class DataAdesioneGO {
	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String COD_OK = "00";
	public static final String DESC_OK = "E' stata trovata la data di adesione per il CF richiesto.";
	public static final String COD_KO_NESSUNA_ADESIONE = "01";
	public static final String DESC_KO_NESSUNA_ADESIONE = "Non è stata trovata l'adesione per il CF richiesto.";
	public static final String COD_KO_VALIDAZIONE_INPUT = "03";
	public static final String DESC_KO_VALIDAZIONE_INPUT = "Errore nel formato di input di richiesta al servizio.";
	public static final String COD_KO_CREDENZIALI = "04";
	public static final String DESC_KO_CREDENZIALI = "Errore nelle credenziali usate per accedere al servizio.";
	public static final String COD_KO_ERRORE_GENERICO = "99";
	public static final String DESC_KO_ERRORE_GENERICO = "Errore generico.";
	public static final String XML_RISPOSTA_ERRORE_GENERICO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Risposta><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></Risposta>";

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	YgGaranziaOverHome ygGaranziaOverHome;

	@WebMethod(operationName = "getDataAdesioneGO")
	public String getDataAdesioneGO(String username, String password, String xmlRichiesta) {

		// Controllo credenziali
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("[WS] DataAdesioneGO.getDataAdesioneGO - eccezione check credenziali \n " + e.getMessage());
			return getRispostaErrore(COD_KO_CREDENZIALI, DESC_KO_CREDENZIALI);
		}

		// Validazione input XSD
		JAXBContext jaxbContext;
		Cittadino cittadino = null;
		try {
			jaxbContext = JAXBContext.newInstance(Cittadino.class);
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "InputDataAdesioneGO.xsd");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schema);
			cittadino = (Cittadino) jaxbUnmarshaller.unmarshal(new StringReader(xmlRichiesta));
		} catch (Exception e) {
			log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione validazione input: " + e.getMessage());
			return getRispostaErrore(COD_KO_VALIDAZIONE_INPUT, DESC_KO_VALIDAZIONE_INPUT);
		}

		// Logica del WS: cerco le adesioni per questo codice fiscale e restituisco le date.
		List<YgGaranziaOverDTO> adesioniLavoratore = ygGaranziaOverHome.findDTOValideByCodFiscale(cittadino
				.getCodiceFiscale());
		if (adesioniLavoratore != null && !adesioniLavoratore.isEmpty()) {
			return getRispostaSuccesso(adesioniLavoratore);
		} else {
			return getRispostaErrore(COD_KO_NESSUNA_ADESIONE, DESC_KO_NESSUNA_ADESIONE);
		}
	}

	/**
	 * Questo metodo controlla che username e password inviate dal chiamante del WS corrispondano a quelle salvate nella
	 * tabella WS_ENDPOINT per questo servizio.
	 */
	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.GO_DATA_ADESIONE);
		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}
	}

	/**
	 * Costruisce l'XML di risposta per un certo errore.
	 */
	private String getRispostaErrore(String codiceErrore, String descrizioneErrore) {
		String xmlRisposta = null;

		// Costruisco una risposta con codice e messaggio di errore appropriati,
		// poi la trasformo in XML e la restituisco.
		try {
			Risposta risposta = new Risposta();
			Risposta.Esito esito = new Risposta.Esito();
			esito.setCodice(codiceErrore);
			esito.setDescrizione(descrizioneErrore);
			risposta.setEsito(esito);

			JAXBContext jc = JAXBContext.newInstance(Risposta.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "OutputDataAdesioneGO.xsd");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(risposta, writer);
			xmlRisposta = writer.getBuffer().toString();
		} catch (Exception e) {
			log.error("[WS] DataAdesioneGO.getDataAdesioneGO - eccezione generazione risposta con errore: " + e.getMessage());
			xmlRisposta = XML_RISPOSTA_ERRORE_GENERICO;
		}

		return xmlRisposta;
	}

	/**
	 * Costruisce l'output del servizio nel caso in cui questo sia andato a buon fine.
	 */
	private String getRispostaSuccesso(List<YgGaranziaOverDTO> adesioniLavoratore) {
		String xmlRisposta = null;

		// Creo la risposta con esito 00 (OK).
		Risposta risposta = new Risposta();
		Risposta.Esito esito = new Risposta.Esito();
		esito.setCodice(COD_OK);
		esito.setDescrizione(DESC_OK);
		risposta.setEsito(esito);
		Risposta.Adesioni adesioni = new Risposta.Adesioni();

		// Aggiungo, per ogni adesione corrispondente al CF di input, la sua data di adesione.
		for (YgGaranziaOverDTO adesioneGO : adesioniLavoratore) {
			Risposta.Adesioni.Adesione adesione = new Risposta.Adesioni.Adesione();
			try {
				adesione.setDataAdesione(Utils.dateToGregorianDate(adesioneGO.getDtAdesione()));
			} catch (Exception e) {
				log.error("[WS] DataAdesioneGO.getDataAdesioneGO - eccezione conversione data per xml: " + e.getMessage());
				return getRispostaErrore(COD_KO_ERRORE_GENERICO, DESC_KO_ERRORE_GENERICO);
			}
			adesioni.getAdesione().add(adesione); // jaxb
		}
		risposta.setAdesioni(adesioni);

		// Creo l'XML di risposta, lo valido e lo restituisco.
		try {
			JAXBContext jc = JAXBContext.newInstance(Risposta.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "OutputDataAdesioneGO.xsd");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(risposta, writer);
			xmlRisposta = writer.getBuffer().toString();
		} catch (Exception e) {
			log.error("[WS] DataAdesioneGO.getDataAdesioneGO - eccezione generazione risposta ok: " + e.getMessage());
			// non gestito errore specifico in validazione output
			return getRispostaErrore(COD_KO_ERRORE_GENERICO, DESC_KO_ERRORE_GENERICO);
		}
		return xmlRisposta;
	}
}
