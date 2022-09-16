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

import it.eng.myportal.dtos.YgRisultatoRicercaAdesioneDTO;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.youthGuarantee.dataAdesioneYG.input.Giovane;
import it.eng.myportal.youthGuarantee.dataAdesioneYG.output.Risposta;

@WebService(serviceName = "DataAdesioneYG")
public class DataAdesioneYG {
	
	public static final String XML_RISPOSTA_ERRORE_GENERICO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Risposta><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></Risposta>";
	
	public static final String  COD_OK = "00";
	public static final String DESC_OK = "OK";
	public static final String  COD_KO_ADESIONE_REGIONE   = "01";
	public static final String DESC_KO_ADESIONE_REGIONE   = "KO. Nessuna adesione per la Regione indicata";
	public static final String  COD_KO_NESSUNA_ADESIONE   = "02";
	public static final String DESC_KO_NESSUNA_ADESIONE   = "KO. Nessuna adesione";
	public static final String  COD_KO_VALIDAZIONE_INPUT  = "03";
	public static final String DESC_KO_VALIDAZIONE_INPUT  = "KO. Input XML non valido";
	public static final String  COD_KO_CREDENZIALI = "04";
	public static final String DESC_KO_CREDENZIALI = "KO. Credenziali non valide";
	public static final String  COD_KO_ERRORE_GENERICO    = "99";
	public static final String DESC_KO_ERRORE_GENERICO    = "Errore Generico";
	
	protected final Log log = LogFactory.getLog(this.getClass());	

	@EJB
	WsEndpointHome wsEndpointHome;
	
	@EJB
	YgAdesioneHome ygAdesioneHome;
	
	@WebMethod(operationName="getDataAdesioneYG")
	public String getDataAdesioneYG(String username, String password, String xmlRichiesta) {
		
		log.debug("[WS] DataAdesioneYG.getDataAdesioneYG - input XML:\n" + xmlRichiesta + " -- ");
		
		// Controllo credenziali
		
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione check credenziali \n " + e.getMessage());
			return getRispostaErrore(COD_KO_CREDENZIALI, DESC_KO_CREDENZIALI);
		}
		
		// Validazione input XSD
		
		JAXBContext jaxbContext;
		Giovane giovane = null;
		try {
			jaxbContext = JAXBContext.newInstance(Giovane.class);
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "InputDataAdesioneYG.xsd");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schema);
			giovane = (Giovane) jaxbUnmarshaller.unmarshal(new StringReader(xmlRichiesta));
		} catch (Exception e) {
			log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione validazione input " + e.getMessage());
			return getRispostaErrore(COD_KO_VALIDAZIONE_INPUT, DESC_KO_VALIDAZIONE_INPUT);
		}
		
		// business logic
		
		String codiceFiscale = giovane.getCodiceFiscale();
		String codRegione = giovane.getCodRegione(); // ce lo aspettiamo nel formato ministeriale
		
		List<YgRisultatoRicercaAdesioneDTO> risultatoRicerca = ygAdesioneHome.findByFilter(codiceFiscale, codRegione);
		if (risultatoRicerca != null) {
			if (risultatoRicerca.isEmpty()) {
				if (codRegione != null && !"".equalsIgnoreCase(codRegione)) {
					// ko, regione specificata
					return getRispostaErrore(COD_KO_ADESIONE_REGIONE, DESC_KO_ADESIONE_REGIONE);
				}
				return getRispostaErrore(COD_KO_NESSUNA_ADESIONE, DESC_KO_NESSUNA_ADESIONE);
			}
			
			return getRispostaSuccesso(risultatoRicerca);
			
		}
		
		
		return getRispostaErroreGenerico();
		
	}
	
	private String getRispostaSuccesso(List<YgRisultatoRicercaAdesioneDTO> risultatoRicerca) {
		
		String xmlRisposta = null;
		
		// creazione risposta
		
		Risposta risposta = new Risposta();
		Risposta.Esito esito = new Risposta.Esito();
		esito.setCodice(COD_OK);
		esito.setDescrizione(DESC_OK);
		risposta.setEsito(esito);
		Risposta.Adesioni adesioni = new Risposta.Adesioni();
		for (YgRisultatoRicercaAdesioneDTO risultatoCorrente : risultatoRicerca) {
			Risposta.Adesioni.Adesione adesione = new Risposta.Adesioni.Adesione();
			try {
				adesione.setDataAdesione(Utils.dateToGregorianDate(risultatoCorrente.getDtAdesione()));
			} catch (Exception e) {
				log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione conversione data per xml " + e.getMessage());
				return getRispostaErroreGenerico();
			}
			adesione.setCodRegioneAdesione(risultatoCorrente.getCodRegioneAdesione());
			adesione.setCodProvinciaAssegnazione(risultatoCorrente.getCodProvinciaRif());
			adesione.setCodStatoAdesioneMin(risultatoCorrente.getCodStatoAdesioneMin());
			try {
				adesione.setDataStatoAdesioneMin(Utils.dateToGregorianDate(risultatoCorrente.getDtStatoAdesioneMin()));
			} catch (Exception e) {
				log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione conversione data per xml " + e.getMessage());
				return getRispostaErroreGenerico();
			}
			adesioni.getAdesione().add(adesione); // jaxb
		}
		risposta.setAdesioni(adesioni);
		
		// creazione documento xml
		
		try {
			
			JAXBContext jc = JAXBContext.newInstance(Risposta.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "OutputDataAdesioneYG.xsd");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(risposta, writer);
			xmlRisposta = writer.getBuffer().toString();
			
		} catch (Exception e) {
			log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione generazione risposta ok: " + e.getMessage());
			return getRispostaErroreGenerico(); // non gestito errore specifico in validazione output
		}
		
		return xmlRisposta;
		
	}
	
	private String getRispostaErroreGenerico() {
		return getRispostaErrore(COD_KO_ERRORE_GENERICO, DESC_KO_ERRORE_GENERICO);
	}
	
	private String getRispostaErrore(String codiceErrore, String descrizioneErrore) {
		
		String xmlRisposta = null;
		
		try {
			
			Risposta risposta = new Risposta();
			Risposta.Esito esito = new Risposta.Esito();
			esito.setCodice(codiceErrore);
			esito.setDescrizione(descrizioneErrore);
			risposta.setEsito(esito);
			
			JAXBContext jc = JAXBContext.newInstance(Risposta.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("yg" + File.separator + "OutputDataAdesioneYG.xsd");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(risposta, writer);
			xmlRisposta = writer.getBuffer().toString();
			
		} catch (Exception e) {
			log.error("[WS] DataAdesioneYG.getDataAdesioneYG - eccezione generazione risposta con errore: " + e.getMessage());
			xmlRisposta = XML_RISPOSTA_ERRORE_GENERICO;
		}
		
		return xmlRisposta;
		
	}
	
	private void checkCredenziali(String login, String pwd) throws Exception {
		
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.YG_DATA_ADESIONE);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}
	}
	
}
