package it.eng.myportal.ejb.stateless.kiosk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.soap.SOAPException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xml.sax.SAXException;

import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.rest.kiosk.pojo.StampaPercorsoLavoratore;
import it.eng.myportal.siler.stampaPercorsoLavoratore.input.ModelloStampa;
import it.eng.myportal.siler.stampaPercorsoLavoratore.input.ONOFF;
import it.eng.myportal.siler.stampaPercorsoLavoratore.input.ObjectFactory;
import it.eng.myportal.siler.stampaPercorsoLavoratore.input.PercorsoLavoratore;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreProxy;
import it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreSoapBindingStub;

@Stateless
public class KioskEjb implements Serializable {

	private static final long serialVersionUID = 3173142865193514407L;

	protected static Log log = LogFactory.getLog(KioskEjb.class);

	private static final String XSD_PERCORSO_LAVORATORE = "servizi" + File.separator + "percorsoLavoratore.xsd";

	private final static String RESP_RISPOSTA = "Risposta";
	private final static String RESP_ESITO = "Esito";
	private final static String RESP_CODICE = "codice";
	private final static String RESP_DESCRIZIONE_ESITO = "descrizione";

	private final static String TIPO_INFORMAZIONE = "B_S";
	
	/* Decodifica tipo operazione:
	 * B_S: (B) movimenti che vengono da una comunicazione obbligatoria; (S): visualizzazione della qualifica/mansione;
	 * B_N: (B) movimenti che vengono da una comunicazione obbligatoria; (N): non visualizzazione della qualifica/mansione;
	 * O_S: (O) movimenti dichiarata dal lavoratore; (S): visualizzazione della qualifica/mansione;
	 * O_N: (O) movimenti dichiarata dal lavoratore; (N): non visualizzazione della qualifica/mansione;
	 * 
	 * I possibili valori del primo char sono:
	 * "A", "B_S", "B_N", "O_S", "O_N", "C", "D", "E", "R", "F", "G", "H", "I", "L", "N", "M", "P", "Q", "S";
	 */

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	public List<StampaPercorsoLavoratore> percorsoLavoratore(String codFiscale, String codProvincia, Date dataInizio,
			Date dataFine) throws Exception {

		/*
		 * La logica richiesta è quella di provare la chiamata a tutti i SIL della regione. Con logica specifica il
		 * chiamante deciderà poi se e quale ritornare.
		 */

		// Recupero della regione partendo dalla provincia passata; questa servirà per recuperare tutti gli endpoint di
		// questa regione
		String codRegione = null;

		try {
			codRegione = deProvinciaHome.findById(codProvincia).getDeRegione().getCodRegione();
		} catch (Exception e) {
			throw new Exception("Provincia non valida");
		}

		// Recupero degli Endpoint
		List<WsEndpoint> listaEndpoint = wsEndpointHome.findByTipoServizioAndRegione(TipoServizio.PERCORSO_LAVORATORE,
				codRegione);
		
		List<String> giachiamati = new ArrayList<String>();

		List<StampaPercorsoLavoratore> listPercorsoLavoratore = new ArrayList<>();
		for (WsEndpoint endpoint : listaEndpoint) {
			StampaPercorsoLavoratore stampa = null;
			
			if(giachiamati.contains(endpoint.getAddress()))
				continue;
			
			try {
				stampa = this.getStampaPercorsoLavoratoreLocal(codFiscale, endpoint.getDeProvincia().getCodProvincia(),
						dataInizio, dataFine);	
				giachiamati.add(endpoint.getAddress());
			} catch (Exception e) {
				stampa = new StampaPercorsoLavoratore(endpoint.getDeProvincia().getCodProvincia(), e);
			} finally {
				listPercorsoLavoratore.add(stampa);
			}
		}
		return listPercorsoLavoratore;
	}

	private StampaPercorsoLavoratore getStampaPercorsoLavoratoreLocal(String codFiscale, String codProvincia,
			Date dataInizio, Date dataFine) throws DatatypeConfigurationException, JAXBException, SAXException,
			JSONException, SOAPException, IOException {

		StampaPercorsoLavoratore stampaPercorsoLavoratore = null;

		PercorsoLavoratore percorsoLavoratore = new PercorsoLavoratore();
		percorsoLavoratore.setCodiceFiscale(codFiscale.toUpperCase());

		DatatypeFactory dtf = DatatypeFactory.newInstance();
		GregorianCalendar dataInizioGregCal = new GregorianCalendar();
		dataInizioGregCal.setTime(dataInizio);
		percorsoLavoratore.setDataInizio(dtf.newXMLGregorianCalendar(dataInizioGregCal));

		GregorianCalendar dataFineGregCal = new GregorianCalendar();
		dataFineGregCal.setTime(dataFine);
		percorsoLavoratore.setDataFine(dtf.newXMLGregorianCalendar(dataFineGregCal));

		percorsoLavoratore.setIdProvincia(new BigInteger(codProvincia));
		percorsoLavoratore.setTipologiaInformazione(TIPO_INFORMAZIONE);

		percorsoLavoratore.setStampa(new PercorsoLavoratore.Stampa());
		percorsoLavoratore.getStampa().setInfoStatoOccupazionale(ONOFF.ON);
		percorsoLavoratore.getStampa().setIntestazione(ONOFF.ON);
		percorsoLavoratore.getStampa().setModelloStampa(ModelloStampa.PERCORSO_LAVORATORE_CC_RPT);
		percorsoLavoratore.getStampa().setProtocollazione(ONOFF.ON);

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(percorsoLavoratore, writer);
		String inputRequest = writer.getBuffer().toString();

		Utils.validateXml(inputRequest, XSD_PERCORSO_LAVORATORE);

		GetPercorsoLavoratoreProxy service = new GetPercorsoLavoratoreProxy(
				wsEndpointHome.getPercorsoLavoratoreAddress(codProvincia));

		String ret = service.getStampaPercorsoLavoratore(inputRequest).toString();

		JSONObject resp = XML.toJSONObject(ret);
		JSONObject risposta = resp.getJSONObject(RESP_RISPOSTA);
		JSONObject esito = risposta.getJSONObject(RESP_ESITO);
		Integer codEsito = esito.getInt(RESP_CODICE);
		String descrizione = esito.getString(RESP_DESCRIZIONE_ESITO);

		stampaPercorsoLavoratore = new StampaPercorsoLavoratore(codProvincia, codEsito, descrizione);

		if (stampaPercorsoLavoratore.isEsitoPositivo()) {
			GetPercorsoLavoratoreSoapBindingStub stub = (GetPercorsoLavoratoreSoapBindingStub) service
					.getGetPercorsoLavoratore();
			Object[] rr = stub.getAttachments();
			DataHandler pdf = ((AttachmentPart) rr[0]).getDataHandler();
			InputStream in = pdf.getInputStream();

			stampaPercorsoLavoratore.setPdfContent(org.apache.commons.io.IOUtils.toByteArray(in));

			in.close();
		}

		return stampaPercorsoLavoratore;
	}

}
