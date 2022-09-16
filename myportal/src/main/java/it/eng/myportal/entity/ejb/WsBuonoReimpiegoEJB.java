package it.eng.myportal.entity.ejb;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.BdAdesione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeBandoProgramma;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.home.BdAdesioneHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeBandoProgrammaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.ws.adesioneReimpiego.input.DichiarazioneCheck;
import it.eng.myportal.ws.adesioneReimpiego.output.Risposta;
import it.eng.sil.base.utils.DateUtils;
import it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoProxy;

@Stateless
public class WsBuonoReimpiegoEJB {
	
	@EJB
	WsEndpointHome wsEndpointHome;
	@EJB
	BdAdesioneHome bdAdesioneHome;
	@EJB
	DeCpiHome deCpiHome;
	@EJB
	PfPrincipalHome pfPrincipalHome;
	@EJB
	DeProvenienzaHome deProvenienzaHome;
	@EJB
	DeBandoProgrammaHome deBandoProgrammaHome;
	
	private static Log log = LogFactory.getLog(WsBuonoReimpiegoEJB.class.getName());
	
	private static final String COD_ESITO_00 = "00";
	
	public boolean callServiceAdesioneReimpiego(UtenteCompletoDTO utenteCompletoDTO, DichiarazioneCheck tipoDichiarazione) throws Exception{
		String codiceFiscale = utenteCompletoDTO.getCodiceFiscale();
		//Data riferimento
		Date dataRiferimento = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dataRiferimentoStr = df.format(dataRiferimento);
		XMLGregorianCalendar xmlDataRiferimento = DateUtils.toXMLGregorianCalendarFormatted(dataRiferimento, "yyyy-MM-dd");
		log.info("Configurazione parametri di input del servizio:\n"
				+ "- codiceFiscale: " + codiceFiscale + ";\n"
				+ "- dataRiferimento: " + dataRiferimentoStr + ";\n"
				+ "- tipoDichiarazione: " + tipoDichiarazione.name());

		String inputXML = marshallInputXML(codiceFiscale, xmlDataRiferimento, tipoDichiarazione);
		try {
			Utils.validateXml(inputXML, "buonoReimpiego" + File.separator + "buonoReimpiego_in.xsd");
		} catch (SAXException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		Map<String, Risposta> rispostaFromSilMap = new HashMap<String, Risposta>();
		List<Exception> exceptionList = new ArrayList<Exception>();
		//Perugia
		log.info("Invocazione del servizio di reimpiego del SIL di Perugia...");
		try{
			Risposta rispostaPerugia = invocaServizioReimpiego(ConstantsSingleton.COD_PROVINCIA_PERUGIA, inputXML);
			rispostaFromSilMap.put(ConstantsSingleton.COD_PROVINCIA_PERUGIA, rispostaPerugia);
		}catch(Exception e){
			//Colleziona gli errori di comunicazione col server (es: endpoint errato)
			exceptionList.add(e);
		}
		//Terni
		log.info("Invocazione del servizio di reimpiego del SIL di Terni...");
		try{
			Risposta rispostaTerni = invocaServizioReimpiego(ConstantsSingleton.COD_PROVINCIA_TERNI, inputXML);
			rispostaFromSilMap.put(ConstantsSingleton.COD_PROVINCIA_TERNI, rispostaTerni);
		}catch(Exception e){
			//Colleziona gli errori di comunicazione col server (es: endpoint errato)
			exceptionList.add(e);
		}
		//Se produce un'eccezione allora entrambe le chiamate Perugia e Terni non sono andate a buon fine
		checkIfThrowExceptionForServerError(exceptionList);

		if(rispostaFromSilMap.isEmpty()){
			log.error("GRAVE: Non è stata ottenuta alcuna risposta dal SIL");
			throw new Exception();
		}else{
			if(!isRispostaFromSilOk(rispostaFromSilMap, ConstantsSingleton.COD_PROVINCIA_PERUGIA) && 
					!isRispostaFromSilOk(rispostaFromSilMap, ConstantsSingleton.COD_PROVINCIA_TERNI)){
				return false;
			}
			if(isRispostaFromSilOk(rispostaFromSilMap, ConstantsSingleton.COD_PROVINCIA_PERUGIA)){
				BdAdesione bdAdesione = fillEntityToPersist(rispostaFromSilMap.get(ConstantsSingleton.COD_PROVINCIA_PERUGIA), 
						utenteCompletoDTO, 
						ConstantsSingleton.DeProvenienza.COD_SILPG, 
						ConstantsSingleton.DeBandoProgramma.COD_REI,
						tipoDichiarazione);
				bdAdesioneHome.persist(bdAdesione);
			}
			if(isRispostaFromSilOk(rispostaFromSilMap, ConstantsSingleton.COD_PROVINCIA_TERNI)){
				BdAdesione bdAdesione = fillEntityToPersist(rispostaFromSilMap.get(ConstantsSingleton.COD_PROVINCIA_TERNI), 
						utenteCompletoDTO, 
						ConstantsSingleton.DeProvenienza.COD_SILTR,
						ConstantsSingleton.DeBandoProgramma.COD_REI,
						tipoDichiarazione);
				bdAdesioneHome.persist(bdAdesione);
			}
		}
		return true;
	}
	
	private void checkIfThrowExceptionForServerError(List<Exception> exceptionList) throws Exception{
		if(exceptionList.size() == 2){
			throw new Exception();
		}
	}
	
	private BdAdesione fillEntityToPersist(Risposta risposta, UtenteCompletoDTO utenteCompletoDTO, String codProvenienza, String codBandoProgramma, DichiarazioneCheck tipoDichiarazione){
		BdAdesione bdAdesione = new BdAdesione();
		if(risposta.getDatiStatoOccupazionale()!=null){
			bdAdesione.setCodiceFiscale(risposta.getDatiStatoOccupazionale().getCodiceFiscale());
			bdAdesione.setCodStatoOccupazionale(risposta.getDatiStatoOccupazionale().getStatoOccupazionale().getCodiceSO());
			bdAdesione.setCognome(risposta.getDatiStatoOccupazionale().getCognome());			
			DeCpi deCpi = deCpiHome.findById(risposta.getDatiStatoOccupazionale().getDatiCPI().getCodiceCPI());
			bdAdesione.setDeCpi(deCpi);
			bdAdesione.setDescStatoOccupazionale(risposta.getDatiStatoOccupazionale().getStatoOccupazionale().getDescrizioneSO());
			bdAdesione.setDtDid(DateUtils.toDate(risposta.getDatiStatoOccupazionale().getStatoOccupazionale().getDataDid()));
			bdAdesione.setDtNascita(DateUtils.toDate(risposta.getDatiStatoOccupazionale().getDataNascita()));
			bdAdesione.setMesiAnzianita(risposta.getDatiStatoOccupazionale().getStatoOccupazionale().getMesiAnzianita());
			bdAdesione.setNome(risposta.getDatiStatoOccupazionale().getNome());
		}
		DeBandoProgramma deBandoProgramma = deBandoProgrammaHome.findById(codBandoProgramma);
		bdAdesione.setDeBandoProgramma(deBandoProgramma);
		DeProvenienza deProvenienza = deProvenienzaHome.findById(codProvenienza);
		bdAdesione.setDeProvenienza(deProvenienza);
		bdAdesione.setDichiarazione(tipoDichiarazione.name());
		bdAdesione.setDtAdesione(new Date());
		bdAdesione.setDtmIns(new Date());
		bdAdesione.setDtmMod(new Date());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(utenteCompletoDTO.getUtenteDTO().getPfPrincipalDTO().getId());
		bdAdesione.setPfPrincipal(pfPrincipal);
		bdAdesione.setPfPrincipalIns(pfPrincipal);
		bdAdesione.setPfPrincipalMod(pfPrincipal);
		return bdAdesione;
	}
	
	private Risposta invocaServizioReimpiego(String codProvincia, String inputXML) throws Exception{
		String wsAddress = wsEndpointHome.getAdesioneReimpiegoAddress(codProvincia);
		log.info("E' in corso il settaggio dell'endpoint con il seguente wsAddress: " + wsAddress);
		GetAdesioneReimpiegoProxy proxy= new GetAdesioneReimpiegoProxy(wsAddress);
		try {
			log.info("Invocazione del servizio di reimpiego del SIL con codice provincia: " + codProvincia);
			String rispostaXML = proxy.getAdesioneReimpiego(inputXML);
			Risposta risposta= unmarshallOutputXML(rispostaXML);
			return risposta;
		} catch (RemoteException e1) {
			log.error("Errore nell'invocazione del servizio di reimpiego del SIL con codice provincia: " + codProvincia + "\n Con errore:" + e1.getMessage());
		}
		throw new Exception();
	}
	
	private boolean isRispostaFromSilOk(Map<String, Risposta> rispostaFromSilMap, String codProvincia){
		boolean isEsitoOK = false;
		Risposta risposta = rispostaFromSilMap.get(codProvincia);
		if(risposta!=null){
			if(COD_ESITO_00.equals(risposta.getEsito().getCodice())){
				isEsitoOK = true;
				return isEsitoOK;
			}
		}
		return isEsitoOK;
	}
	
	private String marshallInputXML(String codiceFiscale, XMLGregorianCalendar xmlDataRiferimento, DichiarazioneCheck tipoDichiarazione) {
		it.eng.myportal.ws.adesioneReimpiego.input.ObjectFactory objectFactory = new it.eng.myportal.ws.adesioneReimpiego.input.ObjectFactory();
		it.eng.myportal.ws.adesioneReimpiego.input.Reimpiego reimpiego = objectFactory.createReimpiego();
		reimpiego.setCodiceFiscale(codiceFiscale);
		reimpiego.setDataRiferimento(xmlDataRiferimento);
		reimpiego.setDichiarazione(tipoDichiarazione);

		String inputXML = "";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.myportal.ws.adesioneReimpiego.input.Reimpiego.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Schema schema = Utils.getXsdSchema("buonoReimpiego" + File.separator + "buonoReimpiego_in.xsd");
			jaxbMarshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(reimpiego, writer);
			inputXML = writer.getBuffer().toString();
		} catch (JAXBException e) {
			log.error(e);
		} catch (SAXException e) {
			log.error(e);
		}

		return inputXML;
	}
	
	private it.eng.myportal.ws.adesioneReimpiego.output.Risposta unmarshallOutputXML(String outputXML) {
		it.eng.myportal.ws.adesioneReimpiego.output.Risposta risposta = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.myportal.ws.adesioneReimpiego.output.Risposta.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.ws.adesioneReimpiego.output.Risposta> root = unmarshaller.unmarshal(new StreamSource(
					new StringReader(outputXML)), it.eng.myportal.ws.adesioneReimpiego.output.Risposta.class);
			risposta = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return risposta;
	}
	
}
