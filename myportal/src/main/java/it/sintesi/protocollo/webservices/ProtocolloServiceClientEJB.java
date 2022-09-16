package it.sintesi.protocollo.webservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.PercorsoLavoratoreElementDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.SintesiProto;
import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.exception.PercorsoLavoratoreException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.utils.StringUtils;
import it.sintesi.getprotocollo.in.GetProtocollo;
import it.sintesi.getprotocollo.in.GetProtocollo.Mittente;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;

/**
 *
 */
@Stateless
public class ProtocolloServiceClientEJB {
	private static final String RESOURCES_REPORT = "/resources/report/";
	private static final String ESITO_POSITIVO = "OK";
	private static final String GENERIC_ERROR_ACCOUNT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GetProtocollo><Esito><codice>99</codice><descrizione>Errore Generico</descrizione></Esito></GetProtocollo>";
	private List<PercorsoLavoratoreElementDTO> listaElementi;
	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	protected ErrorsSingleton errors;

	@EJB
	DeCpiHome deCpiHome;

	@PersistenceContext
	private EntityManager entityManager;

	protected final Log log = LogFactory.getLog(this.getClass());

	public void staccaProtocolloSintesi(SintesiProto nuovaStampaProtocollata, String codProvincia)
			throws MyPortalNoResultFoundException {

		WsEndpoint wsRow = null;
		try {
			wsRow = wsEndpointHome.findByTipoServizioAndProvincia(TipoServizio.SINTESI_PROTO_PUGLIA, codProvincia);
		} catch (Exception e) {
			log.error("GRAVE endpoint non trovato ptonsil per provincia: " + codProvincia);
			return;
		}

		try {

			GetProtocollo in = new GetProtocollo();
			Mittente mitt = new Mittente();
			mitt.setCodiceFiscale(nuovaStampaProtocollata.getCodFisRichiedente());
			mitt.setCognome(nuovaStampaProtocollata.getCognomeRichiedente());
			mitt.setNome(nuovaStampaProtocollata.getNomeRichiedente());
			mitt.setOperatoreCF(nuovaStampaProtocollata.getCodFisRichiedente());
			mitt.setOperatoreUserId(String.valueOf(nuovaStampaProtocollata.getPfPrincipal().getIdPfPrincipal()));
			mitt.setOperatoreUsername(nuovaStampaProtocollata.getPfPrincipal().getUsername());

			in.setMittente(mitt);
			in.setTipologiaRichiesta(mapTipoRichiesta(nuovaStampaProtocollata.getTipologiaRichiesta()));
			String inputXml = marshal(in);
			GetProtocolloServiceLocator loca = new GetProtocolloServiceLocator();
			loca.setGetProtocolloServiceSoapEndpointAddress(wsRow.getAddress());
			GetProtocolloServiceSoap service = loca.getGetProtocolloServiceSoap();
			log.info("INVOCAZIONE PROTOCOLLO SINTESI:  " + inputXml);
			String esito = service.getProtocollo(inputXml);
			// String esito = "<GetProtocollo><Esito><CodEsito>OK</CodEsito><Descrizione
			// /></Esito><TipologiaRichiesta>C2S</TipologiaRichiesta><CodiceFiscaleRichiedente>FRGLNZ32M16F257U</CodiceFiscaleRichiedente><Protocollo><NumeroProtocollo>344972</NumeroProtocollo><DataProtocollo>09/10/2020
			// 14:50:39</DataProtocollo><IdProvincia>074</IdProvincia></Protocollo><StatoOccupazionale><CodCPI_netlabor>164104000</CodCPI_netlabor><CodSTO>A024</CodSTO><DescrizioneSTO>Stato
			// Occupato</DescrizioneSTO><dtDecorrenzaSTO>09/10/2020
			// 14:58:12</dtDecorrenzaSTO></StatoOccupazionale></GetProtocollo>";
			it.sintesi.getprotocollo.out.GetProtocollo outObj = creaGetProtocolloOut(esito);
			nuovaStampaProtocollata.setXmlChiamata(inputXml);
			try {
				if (outObj.getEsito().getCodEsito().toString().equals(ESITO_POSITIVO)) {
					nuovaStampaProtocollata.setNumProtocollo(outObj.getProtocollo().getNumeroProtocollo());
					nuovaStampaProtocollata.setSintesiStatoOccDesc(outObj.getStatoOccupazionale().getDescrizioneSTO());
					nuovaStampaProtocollata.setSintesiCodiceCPIrif(outObj.getStatoOccupazionale().getCodCPINetlabor());
					nuovaStampaProtocollata.setSintesiOperatoreCF(outObj.getCodiceFiscaleRichiedente());
					nuovaStampaProtocollata.setSintesiCodStatoOcc(outObj.getStatoOccupazionale().getCodSTO());
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					if (!outObj.getStatoOccupazionale().getDtDecorrenzaSTO().equals("")) {
						nuovaStampaProtocollata.setSintesiDataDecorrenza(
								df.parse(outObj.getStatoOccupazionale().getDtDecorrenzaSTO()));
					}
					nuovaStampaProtocollata.setDataProtocollo(df.parse(outObj.getProtocollo().getDataProtocollo()));
					nuovaStampaProtocollata
							.setDeProvincia(deProvinciaHome.findById(outObj.getProtocollo().getIdProvincia()));
					if (!outObj.getCodiceFiscaleRichiedente().equals(nuovaStampaProtocollata.getCodFisRichiedente())) {
						log.error("GRAVE: inconsistenza CF out/in staccaProtocolloSintesi : "
								+ outObj.getCodiceFiscaleRichiedente());
						throw new PercorsoLavoratoreException("errore dati: CF out differente CF input");
					}
				} else {
					log.error("Errore WS : Codice esito response: " + outObj.getEsito().getCodEsito());
					throw new PercorsoLavoratoreException("errore esito response" + outObj.getEsito().getDescrizione());
				}

			} catch (PercorsoLavoratoreException e) {
				log.error("ERRORE: Esito servizio WS: " + e.getMessage());
			} catch (ParseException ex) {
				log.warn("ATTENZIONE la data decorrenza da response non corretta: " + ex.getMessage());
			} catch (java.lang.Exception ex) {
				log.warn("ATTENZIONE provincia da response non trovata: " + outObj.getProtocollo().getIdProvincia());
			}

		} catch (java.lang.Exception ex) {
			log.error("ERRORE staccaProtocolloSintesi(): " + ex.getMessage());
			throw new MyPortalNoResultFoundException(ex);
		}
	}

	private it.sintesi.getprotocollo.in.TipoServizio mapTipoRichiesta(String tipologiaRichiesta) {

		switch (tipologiaRichiesta) {
		case "C2S":
			return it.sintesi.getprotocollo.in.TipoServizio.C_2_S;
		default:
			break;
		}

		log.warn("ATTENZIONE mapTipoRichiesta() torna valore di DEFAULT");
		return it.sintesi.getprotocollo.in.TipoServizio.C_2_S;
	}

	private it.sintesi.getprotocollo.out.GetProtocollo creaGetProtocolloOut(String xmlAccountCittadino) {
		JAXBContext jaxbContext;
		it.sintesi.getprotocollo.out.GetProtocollo account = null;
		try {
			jaxbContext = JAXBContext.newInstance(it.sintesi.getprotocollo.out.GetProtocollo.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			account = (it.sintesi.getprotocollo.out.GetProtocollo) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlAccountCittadino));
		} catch (JAXBException e) {
			throw new MyPortalException("02", "InputXML GetProtocollo non valido");
		}
		return account;
	}

	private String marshal(GetProtocollo account) {
		try {
			JAXBContext jc = JAXBContext.newInstance(GetProtocollo.class);
			Marshaller marshaller = jc.createMarshaller();
			Schema schema = Utils.getXsdSchema("sinprot" + File.separator + "getproto_in.xsd");

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			return xmlRichiesta;
		} catch (PropertyException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (SAXException e) {
			log.error("creaXMLoutAccount: " + e);
			return GENERIC_ERROR_ACCOUNT;
		} catch (JAXBException e) {
			log.error("creaXMLoutDettaglio: " + e);
			return GENERIC_ERROR_ACCOUNT;
		}
	}

	public ByteArrayInputStream generaPdfPercorsoLav(UtenteCompletoDTO utenteDTO, SintesiProto nuovaStampaProtocollata)
			throws Exception {
		ByteArrayInputStream bis = null;
		String indirizzoCPI = null;
		String telefonoCPI = null;
		String centroImpiegoCpi = null; //TK: ESL4SIL-1415
		// 3 Lancio stampa JASPER
		// costruisco il jasperReport
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String relativeWebPath = RESOURCES_REPORT;
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			String absoluteDiskPath = request.getServletContext().getRealPath(relativeWebPath);

			Integer idSintesiProto = nuovaStampaProtocollata.getIdSintesiProto();
			String jsonPercLavResponse = nuovaStampaProtocollata.getJsonPercLavResponse();

			File file = new File(absoluteDiskPath + File.separator + "Puglia_C2Storico.jrxml");
			FileInputStream fis = new FileInputStream(file);
			JasperReport jasperReport = JasperCompileManager.compileReport(fis);
			List<PercorsoLavoratoreElementDTO> listaElementiLav = getListaElementi(jsonPercLavResponse);
			// Recupero di il Codice Comune Residenza per eseguire la ricerca per CPI
			DeComuneDTO deComuneDTO = utenteDTO.getComuneResidenza();
			DeCpiDTO deCpi;
			if (StringUtils.isFilledNoBlank(nuovaStampaProtocollata.getSintesiCodiceCPIrif())) {
				// cerco il CPI con in codice passato dalla response
				log.info("Eseguo la ricerca CPI con il codice CPI di Riferimento: "
						+ nuovaStampaProtocollata.getSintesiCodiceCPIrif());
				deCpi = deCpiHome.findDTOById(nuovaStampaProtocollata.getSintesiCodiceCPIrif());
			} else {
				// cerco il CPI con il codice Comune del Cittadino;
				log.info("Eseguo la ricerca CPI con il codice comune del cittadino");
				deCpi = deCpiHome.findDTOByCodComune(deComuneDTO.getId());
			}
			if (deCpi == null) {
				log.error(
						"GRAVE: protocollo sintesi non trova CPI: " + nuovaStampaProtocollata.getSintesiCodiceCPIrif());
			}

			if (StringUtils.isFilledNoBlank(deCpi.getIndirizzo())) {
				indirizzoCPI = deCpi.getIndirizzo();
			}
			if (StringUtils.isFilledNoBlank(deCpi.getTel())) {
				telefonoCPI = deCpi.getTel();
			}	//TK: ESL4SIL-1415
			 if(StringUtils.isFilledNoBlank(deCpi.getDescrizione())){
				centroImpiegoCpi =deCpi.getDescrizione();
		}

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, entityManager);
			parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);
			parameters.put("idProtocollo", idSintesiProto);
			parameters.put("listaElementi", listaElementiLav);
			parameters.put("indirizzoDomicilio", utenteDTO.getIndirizzoDomicilio());
			parameters.put("indirizzoResidenza", utenteDTO.getIndirizzoResidenza());
			parameters.put("indirizzoCpi", indirizzoCPI);
			parameters.put("telefonoCpi", telefonoCPI);
			//parameters.put("statoOccupazionale", nuovaStampaProtocollata.getSintesiStatoOccDesc()); //Da commentare nel file Puglia_C2Storico.jrxml -  TK: ESL4SIL-1415
			parameters.put("statoOccupazionale", "");
			parameters.put("centroImpiegoCPI", centroImpiegoCpi); //Da aggiungere al file Puglia_C2Storico.jrxml   TK: ESL4SIL-1415

			parameters.put(ConstantsSingleton.REPORT_DIR, absoluteDiskPath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
			bis = new ByteArrayInputStream(bos.toByteArray());

		} catch (IOException e) {
			log.error("Errore durante la generazione del file pdf" + e.getMessage());
		} catch (Exception ex) {
			log.error("Errore in Download percorso (puglia): " + ex.getMessage());
			throw new Exception("ERRORE di Sistema. ");
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		return bis;
	}

	private List<PercorsoLavoratoreElementDTO> getListaElementi(String list) throws PercorsoLavoratoreException {
		if (!list.isEmpty()) {
			try {
				JSONObject object = new JSONObject(list);
				JSONObject risposta = object.getJSONObject(ConstantsSingleton.RESP_RISPOSTA);
				JSONObject esito = risposta.getJSONObject(ConstantsSingleton.RESP_ESITO);
				int codEsito = esito.getInt(ConstantsSingleton.RESP_CODICE);

				JSONArray elementsArrayLav = new JSONArray();
				if (risposta.has(ConstantsSingleton.RESP_ELEMENT)) {
					Object elements = risposta.get(ConstantsSingleton.RESP_ELEMENT);
					if (elements instanceof JSONArray) {
						elementsArrayLav = (JSONArray) elements;
					} else {
						elementsArrayLav.put(elements);
					}
				}
				listaElementi = new ArrayList<PercorsoLavoratoreElementDTO>();
				for (int i = 0; i < elementsArrayLav.length(); i++) {
					JSONObject element = (JSONObject) elementsArrayLav.get(i);
					PercorsoLavoratoreElementDTO elemento = new PercorsoLavoratoreElementDTO();
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

					try {
						elemento.setDataDa(df.parse(element.getString(ConstantsSingleton.RESP_DATADA)));
					} catch (ParseException e) {
						log.warn("formato data errato: " + e.getMessage());
					}
					try {
						String dataA = element.getString(ConstantsSingleton.RESP_DATAA).replace("{}", "");
						if (!dataA.equals(""))
							elemento.setDataA(
									df.parse(element.getString(ConstantsSingleton.RESP_DATAA).replace("{}", "")));
						else {
							log.warn("La formato dataA null o vuoto ");
						}

					} catch (ParseException e) {
						log.warn("formato data errato: " + e.getMessage());
					}
					elemento.setTipo(element.getString(ConstantsSingleton.RESP_TIPO));
					elemento.setDescrizione(element.getString(ConstantsSingleton.RESP_DESCRIZIONE).replace("{}", ""));
					listaElementi.add(elemento);
				}

				if (codEsito != ConstantsSingleton.ESITO_POSITIVO) {
					throw new PercorsoLavoratoreException(esito.getString(ConstantsSingleton.RESP_DESCRIZIONE_ESITO));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return listaElementi;
	}
}
