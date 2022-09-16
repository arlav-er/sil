package it.eng.myportal.ws;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.myportal.utils.Utils;

@WebService(serviceName = "CorsiOrienter")
public class CorsiOrienter {

//	private static final String QUALIFICA_SRQ_NULL = "0000";

	private static final String _04_INPUT_XML_NON_VALIDO = "InputXML non valido";

	private static final String _04 = "04";

	protected final Log log = LogFactory.getLog(this.getClass());

	private static final String _99_ERRORE_GENERICO = "Errore generico";

//	private static final int idAdmin = 0;

	private static final String _99 = "99";

	@EJB
	CorsiOrienterWorker corsiOrienterPersistence; 
	
//	@EJB
//	OrEnteCorsoHome orEnteCorsoHome;
//	
//
//	@EJB
//	OrCorsoHome orCorsoHome;
//
//	@EJB
//	OrEdizioneAvviataHome orEdizioneAvviataHome;
//
//	@EJB
//	OrSedeCorsoHome orSedeCorsoHome;
//
//	@EJB
//	PfPrincipalHome pfPrincipalHome;

	@TransactionAttribute(TransactionAttributeType.NEVER)
	private String putCorsiImpl(String inputXML) throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {

		if (StringUtils.isBlank(inputXML)) {
			// return creaMessaggio(_99, "Errore: dati corsi mancanti");
			return creaMessaggio(_04, _04_INPUT_XML_NON_VALIDO);
		}

		// inserisce/aggiorna i corsi provenienti da ORIENTER
		// valida dati ricevuti
		try {
			Utils.validateXml(inputXML, "servizi" + File.separator + "corsiOrienter.xsd");
		} catch (SAXException e) {
			log.error(e);
			return creaMessaggio(_04, _04_INPUT_XML_NON_VALIDO);
		}

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(inputXML));
		Document document = builder.parse(source);

		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression listCorsiXp = xpath.compile("/SetCorsi/Corso");

		// QName mio = null;
		Object result = listCorsiXp.evaluate(document, XPathConstants.NODESET);
		List<Integer> lIdCorsi = new ArrayList<Integer>();
		if (result instanceof NodeList) {
			NodeList nodeList = (NodeList) result;
			for (int j = 0; j < nodeList.getLength(); j++) {
				Node nCorso = nodeList.item(j);
				XPathExpression IdCorsoXp = xpath.compile("id_corso");
				// recupero i dati delcorso x inserirlo/aggiornarlo/cancellarlo
				Integer idCorso = new Integer(IdCorsoXp.evaluate(nCorso));
				try {
					log.info("Start processNodoCorso idCorso:"+ idCorso);
					corsiOrienterPersistence.processNodoCorso(nCorso, xpath);
					log.info("End processNodoCorso idCorso:"+ idCorso);
				} catch (CorsoNonAllineatoException e) {
					log.error("processNodoCorso idCorso:" + e.getIdCorso() + " - errore: " + e.getMessage());
					lIdCorsi.add(idCorso);
				} catch (Exception e) {
					log.error("processNodoCorso: " + e.getMessage());
					lIdCorsi.add(idCorso);
				}
			}
		}
		if (lIdCorsi != null)
			if (!lIdCorsi.isEmpty()) {
				return creaMessaggio("03", "Errore allineamento corsi", lIdCorsi);
			}
		return creaMessaggio("00", "Ok");
	}

//	private void processNodoCorso(Node nCorso, XPath xpath) throws XPathExpressionException, ParseException,
//			CorsoNonAllineatoException {
//		XPathExpression TipoOperazioneXp = xpath.compile("TipoOperazione");
//		
//
//		String TipoOperazione = TipoOperazioneXp.evaluate(nCorso);
//		boolean isIns = "I".equals(TipoOperazione);
//		boolean isUpd = "U".equals(TipoOperazione);
//		boolean isInsUpd = isIns || isUpd;
//		boolean isDelete = "D".equals(TipoOperazione);
//		XPathExpression IdCorsoXp = xpath.compile("id_corso");
//		// recupero i dati delcorso x inserirlo/aggiornarlo/cancellarlo
//		Integer idCorso = new Integer(IdCorsoXp.evaluate(nCorso));
//		OrCorsoDTO oc = orCorsoHome.findByIdCorso(idCorso);
//
//		if (isInsUpd) {
//
//			corsiOrienterPersistence.insertUpdateCorso(nCorso, xpath, idCorso, oc);
//
//		} else if (isDelete) {
//			deleteCorso(oc);
//
//		}
//
//	}
//
//    private void insertUpdateCorso(Node nCorso, XPath xpath, Integer idCorso, OrCorsoDTO oc) throws XPathExpressionException,
//            ParseException, CorsoNonAllineatoException {
//        XPathExpression listSediCorsoXp = xpath.compile("SediCorso/Sede");
//        XPathExpression listEdizioniAvviateXp = xpath.compile("EdizioniAvviate/Edizione");
//        boolean isCorsoNew = (oc == null);
//        List<OrEdizioneAvviataDTO> edAvv2del = new ArrayList<OrEdizioneAvviataDTO>();
//        List<OrSedeCorsoDTO> sediCorso2del = new ArrayList<OrSedeCorsoDTO>();
//        if (isCorsoNew) {
//        	oc = new OrCorsoDTO();
//        	oc.setIdCorso(idCorso);
//        } else {
//        	// recupero i record da cancellare
//        	edAvv2del = oc.getOrEdizioneAvviatas();
//        	sediCorso2del = oc.getOrSedeCorsos();
//        }
//        corsiOrienterPersistence.parseCorso(nCorso, xpath, oc);
//
//        OrEnteCorsoDTO ec = parseEnte(nCorso, xpath);
//
//        // QName mio = null;
//        Object result = listSediCorsoXp.evaluate(nCorso, XPathConstants.NODESET);
//        List<OrSedeCorsoDTO> orSedeCorsos = new ArrayList<OrSedeCorsoDTO>();
//        if (result instanceof NodeList) {
//        	NodeList nodeList = (NodeList) result;
//        	for (int j = 0; j < nodeList.getLength(); j++) {
//        		Node nSede = nodeList.item(j);
//        		OrSedeCorsoDTO orSedeCorso = parseSedeCorso(nSede, xpath);
//        		orSedeCorsos.add(orSedeCorso);
//        	}
//        }
//
//        result = listEdizioniAvviateXp.evaluate(nCorso, XPathConstants.NODESET);
//        // Object result = listCorsiXp.evaluate(document,
//        List<OrEdizioneAvviataDTO> orEdizioneAvviatas = new ArrayList<OrEdizioneAvviataDTO>();
//        // XPathConstants.NODESET);
//        if (result instanceof NodeList) {
//        	NodeList nodeList = (NodeList) result;
//        	for (int j = 0; j < nodeList.getLength(); j++) {
//        		Node edizioneAvviata = nodeList.item(j);
//        		OrEdizioneAvviataDTO orEdizioneAvviata = parseEdizioniAvviate(edizioneAvviata, xpath);
//        		orEdizioneAvviatas.add(orEdizioneAvviata);
//        	}
//        }
//
//        // persistenza
//        // log.debug("OrCorso:" + oc);
//        // log.debug("OrEnteCorso:" + ec);
//
//        // devo cancellare i record precedenti
//        if (!isCorsoNew) {
//        	deleteEdAvviateAndSediCorso(edAvv2del, sediCorso2del);
//        }
//
//        Integer idOrCorso = persistCorsoAndCheck(idCorso, oc, ec);
//        
//        log.info("orEdizioneAvviatas 2 persist size:"+ (orEdizioneAvviatas!=null?orEdizioneAvviatas.size():0));
//        for (OrEdizioneAvviataDTO orEdizioneAvviataDTO : orEdizioneAvviatas) {
//        	orEdizioneAvviataDTO.setIdOrCorso(idOrCorso);
//        	orEdizioneAvviataHome.persistDTO(orEdizioneAvviataDTO, idAdmin);
//        }
//        
//        log.info("orSedeCorsos 2 persist size:"+ (orSedeCorsos!=null?orSedeCorsos.size():0));
//        for (OrSedeCorsoDTO orSedeCorso : orSedeCorsos) {
//        	orSedeCorso.setIdOrCorso(idOrCorso);
//        	orSedeCorsoHome.persistDTO(orSedeCorso, idAdmin);
//        }
//    }
//
//    private Integer persistCorsoAndCheck(Integer idCorso, OrCorsoDTO oc, OrEnteCorsoDTO ec)
//            throws CorsoNonAllineatoException {
//        // persistere il corso
//        OrEnteCorsoDTO ecPersist = orEnteCorsoHome.persistDTO(ec, idAdmin);
//        Integer idOrEnteCorso = ecPersist.getId();
//        oc.setIdOrEnteCorso(idOrEnteCorso);
//        OrCorsoDTO ocPersist = orCorsoHome.persistDTO(oc, idAdmin);
//        Integer idOrCorso = ocPersist.getId();
//        // se il codice non ha trovato corrispondenza fra le decodifiche
//        // il corso non è allineato - stringa vuota va ricondotta a null
//        boolean trovatoComune = StringUtils.equals(ecPersist.getCodComune(), StringUtils.trimToNull(ec
//        		.getCodComune()));
//        if (!trovatoComune) {
//        	log.warn("codComune xml:"+ec.getCodComune()+", codComune risolta:"+ecPersist.getCodComune());
//        }
//        boolean trovatoTipoFormazione = StringUtils.equals(ocPersist.getCodTipoFormazione(), StringUtils
//        		.trimToNull(oc.getCodTipoFormazione()));
//        // codQualificaSRQ non obbligatorio, cmq OK il codice inesistente
//        // 0000
//        if (!trovatoTipoFormazione) {
//        	log.warn("codTipoFormazione xml:"+oc.getCodTipoFormazione()+", codTipoFormazione risolta:"+ocPersist.getCodTipoFormazione());
//        }
//        boolean trovataQualificaSrq = QUALIFICA_SRQ_NULL.equalsIgnoreCase(oc.getCodQualificaSrqRilasciata())
//        		|| StringUtils.equals(ocPersist.getCodQualificaSrqRilasciata(), StringUtils.trimToNull(oc
//        				.getCodQualificaSrqRilasciata()));
//        if (!trovataQualificaSrq) {
//        	log.warn("CodQualificaSrq xml:"+oc.getCodQualificaSrqRilasciata()+", CodQualificaSrq risolta:"+ocPersist.getCodQualificaSrqRilasciata());
//        }
//        boolean trovataProfessione = StringUtils.equals(ocPersist.getCodProfessione(), StringUtils.trimToNull(oc
//        		.getCodProfessione()));
//        if (!trovataProfessione) {
//        	log.warn("codProfessione xml:"+oc.getCodProfessione()+", codProfessione risolta:"+ocPersist.getCodProfessione());
//        }
//        boolean trovataMansione = StringUtils.equals(ocPersist.getCodMansione(), StringUtils.trimToNull(oc
//        		.getCodMansione()));
//        if (!trovataMansione) {
//        	log.warn("codMansione xml:"+oc.getCodMansione()+", codMansione risolta:"+ocPersist.getCodMansione());
//        }
//        if (!(trovataMansione && trovataProfessione && trovataQualificaSrq && trovatoTipoFormazione && trovatoComune)) {
//        	throw new CorsoNonAllineatoException(idCorso);
//        }
//        return idOrCorso;
//    }

//    private void deleteCorso(OrCorsoDTO oc) {
//        if (oc == null) {
//        	// restituisco errore 'corso da cancellare non trovato'
//        	// throw new CorsoNonAllineatoException(idCorso);
//        } else {
//        	List<OrEdizioneAvviataDTO> edAvv2del = oc.getOrEdizioneAvviatas();
//        	List<OrSedeCorsoDTO> sediCorso2del = oc.getOrSedeCorsos();
//        	deleteEdAvviateAndSediCorso(edAvv2del, sediCorso2del);
//        	orCorsoHome.removeById(oc.getId(), idAdmin);
//        }
//    }

//	private void deleteEdAvviateAndSediCorso(List<OrEdizioneAvviataDTO> edAvv2del, List<OrSedeCorsoDTO> sediCorso2del) {
//		if (edAvv2del != null) {
//			log.info("edAvv 2del size:"+ (edAvv2del!=null?edAvv2del.size():0));
//			for (OrEdizioneAvviataDTO orEdizioneAvviata : edAvv2del) {
//				orEdizioneAvviataHome.removeById(orEdizioneAvviata.getId(), idAdmin);
//			}
//		}
//		if (sediCorso2del != null) {
//			log.info("sediCorso 2del size:"+ (sediCorso2del!=null?sediCorso2del.size():0));
//			for (OrSedeCorsoDTO orSedeCorso : sediCorso2del) {
//				orSedeCorsoHome.removeById(orSedeCorso.getId(), idAdmin);
//			}
//		}
//	}

//	/**
//	 * @param nCorso
//	 * @param xpath
//	 * @param oc
//	 * @throws XPathExpressionException
//	 * @throws ParseException
//	 */
//	private void parseCorso(Node nCorso, XPath xpath, OrCorsoDTO oc) throws XPathExpressionException, ParseException {
//
//		oc.setCodiceIdentificativo(xpath.compile("CodiceIdentificativo").evaluate(nCorso));
//		oc.setTitoloCorso(xpath.compile("TitoloCorso").evaluate(nCorso));
//		oc.setDescrizioneCorso(xpath.compile("DescrizioneCorso").evaluate(nCorso));
//
//		oc.setCodMansione(xpath.compile("CodMansione").evaluate(nCorso));
//		oc.setCodTipoFormazione(xpath.compile("CodTipoFormazione").evaluate(nCorso));
//		oc.setCodTipoFormazione(xpath.compile("CodTipoFormazione").evaluate(nCorso));
//		oc.setCodProfessione(xpath.compile("Professione").evaluate(nCorso));
//
//		oc.setContenutiPercorso(xpath.compile("ContenutiPercorso").evaluate(nCorso));
//		oc.setRequisitiAccesso(xpath.compile("RequisitiAccesso").evaluate(nCorso));
//		oc.setIscrizione(xpath.compile("Iscrizione").evaluate(nCorso));
//		oc.setCriteriSelezione(xpath.compile("CriteriSelezione").evaluate(nCorso));
//		String dataAvvio = xpath.compile("DataAvvio").evaluate(nCorso);
//		if (StringUtils.isNotBlank(dataAvvio)) {
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			oc.setDtAvvio(df.parse(dataAvvio));
//		}
//		oc.setAttestatoRilascio(xpath.compile("AttestatoRilascio").evaluate(nCorso));
//		String codQualificaSRQ = xpath.compile("CodQualificaSRQ").evaluate(nCorso);
//		// codQualificaSRQ non è obbligatorio, metto null se c'è il codice
//		// inesistente 0000
//		oc.setCodQualificaSrqRilasciata(codQualificaSRQ);
//		oc.setDurata(xpath.compile("Durata").evaluate(nCorso));
//		oc.setPeriodoSvolgimento(xpath.compile("PeriodoSvolgimento").evaluate(nCorso));
//		oc.setNumeroPartecipanti(Integer.valueOf(xpath.compile("NumeroPartecipanti").evaluate(nCorso), 10));
//		oc.setQuotaIscrizioneIndividuale(new BigDecimal(xpath.compile("QuotaIndividuale").evaluate(nCorso)));
//		oc.setAmministrazioneCompetente(xpath.compile("AmministrazioneCompetente").evaluate(nCorso));
//		oc.setScuolaCapofila(xpath.compile("ScuolaCapofila").evaluate(nCorso));
//		oc.setUniversita(xpath.compile("Universita").evaluate(nCorso));
//		oc.setImprese(xpath.compile("Imprese").evaluate(nCorso));
//
//	}
//
//	private OrEdizioneAvviataDTO parseEdizioniAvviate(Node edizioneAvviata, XPath xpath)
//			throws XPathExpressionException {
//		OrEdizioneAvviataDTO o = new OrEdizioneAvviataDTO();
//		o.setCodComune(xpath.compile("Comune").evaluate(edizioneAvviata));
//		return o;
//	}
//
//	private OrSedeCorsoDTO parseSedeCorso(Node nSede, XPath xpath) throws XPathExpressionException {
//		OrSedeCorsoDTO o = new OrSedeCorsoDTO();
//		o.setCodComune(xpath.compile("Comune").evaluate(nSede));
//		o.setNominativoReferente(xpath.compile("NominativoReferente").evaluate(nSede));
//		o.setTelefonoReferente(xpath.compile("TelefonoReferente").evaluate(nSede));
//		o.setEmailReferente(xpath.compile("EmailReferente").evaluate(nSede));
//		return o;
//	}

//	private OrEnteCorsoDTO parseEnte(Node nCorso, XPath xpath) throws XPathExpressionException {
//		XPathExpression codiceOrganismoXp = xpath.compile("Ente/CodiceOrganismo");
//		Integer co = new Integer(codiceOrganismoXp.evaluate(nCorso));
//		OrEnteCorsoDTO ec = orEnteCorsoHome.findByCodiceOrganismo(co);
//		Date now = new Date();
//		if (ec == null) {
//			ec = new OrEnteCorsoDTO();
//			ec.setCodiceOrganismo(co);
//			ec.setDtmIns(now);
//		}
//		XPathExpression ragioneSocialeXp = xpath.compile("Ente/RagioneSociale");
//		ec.setRagioneSociale(ragioneSocialeXp.evaluate(nCorso));
//		XPathExpression IndirizzoXp = xpath.compile("Ente/Indirizzo");
//		ec.setIndirizzo(IndirizzoXp.evaluate(nCorso));
//		XPathExpression CapXp = xpath.compile("Ente/Cap");
//		ec.setCap(CapXp.evaluate(nCorso));
//
//		XPathExpression ComuneXp = xpath.compile("Ente/Comune");
//		String codCom = ComuneXp.evaluate(nCorso);
//		ec.setCodComune(codCom);
//
//		XPathExpression TelefonoXp = xpath.compile("Ente/Telefono");
//		ec.setTelefono(TelefonoXp.evaluate(nCorso));
//		XPathExpression FaxXp = xpath.compile("Ente/Fax");
//		ec.setFax(FaxXp.evaluate(nCorso));
//
//		XPathExpression EmailXp = xpath.compile("Ente/Email");
//		ec.setEmail(EmailXp.evaluate(nCorso));
//		return ec;
//	}

	@WebMethod(operationName = "putCorsi")
	public String putCorsi(String inputXML) {
		String errString = "putCorsiImpl inputXML:" + inputXML;
		try {
			String putCorsiOutput = putCorsiImpl(inputXML);
			Utils.validateXml(putCorsiOutput, "servizi" + File.separator + "corsiOrienterOutput.xsd");
			return putCorsiOutput;
		} catch (ParserConfigurationException e) {
			log.error(errString, e);
		} catch (SAXException e) {
			log.error(errString, e);
		} catch (IOException e) {
			log.error(errString, e);
		} catch (XPathExpressionException e) {
			log.error(errString, e);
		} catch (Exception e) {
			log.error(errString, e);
		}
		return creaMessaggio(_99, _99_ERRORE_GENERICO);
	}

	//	private void processNodoCorso(Node nCorso, XPath xpath) throws XPathExpressionException, ParseException,
	//			CorsoNonAllineatoException {
	//		XPathExpression TipoOperazioneXp = xpath.compile("TipoOperazione");
	//		
	//
	//		String TipoOperazione = TipoOperazioneXp.evaluate(nCorso);
	//		boolean isIns = "I".equals(TipoOperazione);
	//		boolean isUpd = "U".equals(TipoOperazione);
	//		boolean isInsUpd = isIns || isUpd;
	//		boolean isDelete = "D".equals(TipoOperazione);
	//		XPathExpression IdCorsoXp = xpath.compile("id_corso");
	//		// recupero i dati delcorso x inserirlo/aggiornarlo/cancellarlo
	//		Integer idCorso = new Integer(IdCorsoXp.evaluate(nCorso));
	//		OrCorsoDTO oc = orCorsoHome.findByIdCorso(idCorso);
	//
	//		if (isInsUpd) {
	//
	//			corsiOrienterPersistence.insertUpdateCorso(nCorso, xpath, idCorso, oc);
	//
	//		} else if (isDelete) {
	//			deleteCorso(oc);
	//
	//		}
	//
	//	}
	//
	//    private void insertUpdateCorso(Node nCorso, XPath xpath, Integer idCorso, OrCorsoDTO oc) throws XPathExpressionException,
	//            ParseException, CorsoNonAllineatoException {
	//        XPathExpression listSediCorsoXp = xpath.compile("SediCorso/Sede");
	//        XPathExpression listEdizioniAvviateXp = xpath.compile("EdizioniAvviate/Edizione");
	//        boolean isCorsoNew = (oc == null);
	//        List<OrEdizioneAvviataDTO> edAvv2del = new ArrayList<OrEdizioneAvviataDTO>();
	//        List<OrSedeCorsoDTO> sediCorso2del = new ArrayList<OrSedeCorsoDTO>();
	//        if (isCorsoNew) {
	//        	oc = new OrCorsoDTO();
	//        	oc.setIdCorso(idCorso);
	//        } else {
	//        	// recupero i record da cancellare
	//        	edAvv2del = oc.getOrEdizioneAvviatas();
	//        	sediCorso2del = oc.getOrSedeCorsos();
	//        }
	//        corsiOrienterPersistence.parseCorso(nCorso, xpath, oc);
	//
	//        OrEnteCorsoDTO ec = parseEnte(nCorso, xpath);
	//
	//        // QName mio = null;
	//        Object result = listSediCorsoXp.evaluate(nCorso, XPathConstants.NODESET);
	//        List<OrSedeCorsoDTO> orSedeCorsos = new ArrayList<OrSedeCorsoDTO>();
	//        if (result instanceof NodeList) {
	//        	NodeList nodeList = (NodeList) result;
	//        	for (int j = 0; j < nodeList.getLength(); j++) {
	//        		Node nSede = nodeList.item(j);
	//        		OrSedeCorsoDTO orSedeCorso = parseSedeCorso(nSede, xpath);
	//        		orSedeCorsos.add(orSedeCorso);
	//        	}
	//        }
	//
	//        result = listEdizioniAvviateXp.evaluate(nCorso, XPathConstants.NODESET);
	//        // Object result = listCorsiXp.evaluate(document,
	//        List<OrEdizioneAvviataDTO> orEdizioneAvviatas = new ArrayList<OrEdizioneAvviataDTO>();
	//        // XPathConstants.NODESET);
	//        if (result instanceof NodeList) {
	//        	NodeList nodeList = (NodeList) result;
	//        	for (int j = 0; j < nodeList.getLength(); j++) {
	//        		Node edizioneAvviata = nodeList.item(j);
	//        		OrEdizioneAvviataDTO orEdizioneAvviata = parseEdizioniAvviate(edizioneAvviata, xpath);
	//        		orEdizioneAvviatas.add(orEdizioneAvviata);
	//        	}
	//        }
	//
	//        // persistenza
	//        // log.debug("OrCorso:" + oc);
	//        // log.debug("OrEnteCorso:" + ec);
	//
	//        // devo cancellare i record precedenti
	//        if (!isCorsoNew) {
	//        	deleteEdAvviateAndSediCorso(edAvv2del, sediCorso2del);
	//        }
	//
	//        Integer idOrCorso = persistCorsoAndCheck(idCorso, oc, ec);
	//        
	//        log.info("orEdizioneAvviatas 2 persist size:"+ (orEdizioneAvviatas!=null?orEdizioneAvviatas.size():0));
	//        for (OrEdizioneAvviataDTO orEdizioneAvviataDTO : orEdizioneAvviatas) {
	//        	orEdizioneAvviataDTO.setIdOrCorso(idOrCorso);
	//        	orEdizioneAvviataHome.persistDTO(orEdizioneAvviataDTO, idAdmin);
	//        }
	//        
	//        log.info("orSedeCorsos 2 persist size:"+ (orSedeCorsos!=null?orSedeCorsos.size():0));
	//        for (OrSedeCorsoDTO orSedeCorso : orSedeCorsos) {
	//        	orSedeCorso.setIdOrCorso(idOrCorso);
	//        	orSedeCorsoHome.persistDTO(orSedeCorso, idAdmin);
	//        }
	//    }
	//
	//    private Integer persistCorsoAndCheck(Integer idCorso, OrCorsoDTO oc, OrEnteCorsoDTO ec)
	//            throws CorsoNonAllineatoException {
	//        // persistere il corso
	//        OrEnteCorsoDTO ecPersist = orEnteCorsoHome.persistDTO(ec, idAdmin);
	//        Integer idOrEnteCorso = ecPersist.getId();
	//        oc.setIdOrEnteCorso(idOrEnteCorso);
	//        OrCorsoDTO ocPersist = orCorsoHome.persistDTO(oc, idAdmin);
	//        Integer idOrCorso = ocPersist.getId();
	//        // se il codice non ha trovato corrispondenza fra le decodifiche
	//        // il corso non è allineato - stringa vuota va ricondotta a null
	//        boolean trovatoComune = StringUtils.equals(ecPersist.getCodComune(), StringUtils.trimToNull(ec
	//        		.getCodComune()));
	//        if (!trovatoComune) {
	//        	log.warn("codComune xml:"+ec.getCodComune()+", codComune risolta:"+ecPersist.getCodComune());
	//        }
	//        boolean trovatoTipoFormazione = StringUtils.equals(ocPersist.getCodTipoFormazione(), StringUtils
	//        		.trimToNull(oc.getCodTipoFormazione()));
	//        // codQualificaSRQ non obbligatorio, cmq OK il codice inesistente
	//        // 0000
	//        if (!trovatoTipoFormazione) {
	//        	log.warn("codTipoFormazione xml:"+oc.getCodTipoFormazione()+", codTipoFormazione risolta:"+ocPersist.getCodTipoFormazione());
	//        }
	//        boolean trovataQualificaSrq = QUALIFICA_SRQ_NULL.equalsIgnoreCase(oc.getCodQualificaSrqRilasciata())
	//        		|| StringUtils.equals(ocPersist.getCodQualificaSrqRilasciata(), StringUtils.trimToNull(oc
	//        				.getCodQualificaSrqRilasciata()));
	//        if (!trovataQualificaSrq) {
	//        	log.warn("CodQualificaSrq xml:"+oc.getCodQualificaSrqRilasciata()+", CodQualificaSrq risolta:"+ocPersist.getCodQualificaSrqRilasciata());
	//        }
	//        boolean trovataProfessione = StringUtils.equals(ocPersist.getCodProfessione(), StringUtils.trimToNull(oc
	//        		.getCodProfessione()));
	//        if (!trovataProfessione) {
	//        	log.warn("codProfessione xml:"+oc.getCodProfessione()+", codProfessione risolta:"+ocPersist.getCodProfessione());
	//        }
	//        boolean trovataMansione = StringUtils.equals(ocPersist.getCodMansione(), StringUtils.trimToNull(oc
	//        		.getCodMansione()));
	//        if (!trovataMansione) {
	//        	log.warn("codMansione xml:"+oc.getCodMansione()+", codMansione risolta:"+ocPersist.getCodMansione());
	//        }
	//        if (!(trovataMansione && trovataProfessione && trovataQualificaSrq && trovatoTipoFormazione && trovatoComune)) {
	//        	throw new CorsoNonAllineatoException(idCorso);
	//        }
	//        return idOrCorso;
	//    }
	
	//    private void deleteCorso(OrCorsoDTO oc) {
	//        if (oc == null) {
	//        	// restituisco errore 'corso da cancellare non trovato'
	//        	// throw new CorsoNonAllineatoException(idCorso);
	//        } else {
	//        	List<OrEdizioneAvviataDTO> edAvv2del = oc.getOrEdizioneAvviatas();
	//        	List<OrSedeCorsoDTO> sediCorso2del = oc.getOrSedeCorsos();
	//        	deleteEdAvviateAndSediCorso(edAvv2del, sediCorso2del);
	//        	orCorsoHome.removeById(oc.getId(), idAdmin);
	//        }
	//    }
	
	//	private void deleteEdAvviateAndSediCorso(List<OrEdizioneAvviataDTO> edAvv2del, List<OrSedeCorsoDTO> sediCorso2del) {
	//		if (edAvv2del != null) {
	//			log.info("edAvv 2del size:"+ (edAvv2del!=null?edAvv2del.size():0));
	//			for (OrEdizioneAvviataDTO orEdizioneAvviata : edAvv2del) {
	//				orEdizioneAvviataHome.removeById(orEdizioneAvviata.getId(), idAdmin);
	//			}
	//		}
	//		if (sediCorso2del != null) {
	//			log.info("sediCorso 2del size:"+ (sediCorso2del!=null?sediCorso2del.size():0));
	//			for (OrSedeCorsoDTO orSedeCorso : sediCorso2del) {
	//				orSedeCorsoHome.removeById(orSedeCorso.getId(), idAdmin);
	//			}
	//		}
	//	}
	
	//	/**
	//	 * @param nCorso
	//	 * @param xpath
	//	 * @param oc
	//	 * @throws XPathExpressionException
	//	 * @throws ParseException
	//	 */
	//	private void parseCorso(Node nCorso, XPath xpath, OrCorsoDTO oc) throws XPathExpressionException, ParseException {
	//
	//		oc.setCodiceIdentificativo(xpath.compile("CodiceIdentificativo").evaluate(nCorso));
	//		oc.setTitoloCorso(xpath.compile("TitoloCorso").evaluate(nCorso));
	//		oc.setDescrizioneCorso(xpath.compile("DescrizioneCorso").evaluate(nCorso));
	//
	//		oc.setCodMansione(xpath.compile("CodMansione").evaluate(nCorso));
	//		oc.setCodTipoFormazione(xpath.compile("CodTipoFormazione").evaluate(nCorso));
	//		oc.setCodTipoFormazione(xpath.compile("CodTipoFormazione").evaluate(nCorso));
	//		oc.setCodProfessione(xpath.compile("Professione").evaluate(nCorso));
	//
	//		oc.setContenutiPercorso(xpath.compile("ContenutiPercorso").evaluate(nCorso));
	//		oc.setRequisitiAccesso(xpath.compile("RequisitiAccesso").evaluate(nCorso));
	//		oc.setIscrizione(xpath.compile("Iscrizione").evaluate(nCorso));
	//		oc.setCriteriSelezione(xpath.compile("CriteriSelezione").evaluate(nCorso));
	//		String dataAvvio = xpath.compile("DataAvvio").evaluate(nCorso);
	//		if (StringUtils.isNotBlank(dataAvvio)) {
	//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	//			oc.setDtAvvio(df.parse(dataAvvio));
	//		}
	//		oc.setAttestatoRilascio(xpath.compile("AttestatoRilascio").evaluate(nCorso));
	//		String codQualificaSRQ = xpath.compile("CodQualificaSRQ").evaluate(nCorso);
	//		// codQualificaSRQ non è obbligatorio, metto null se c'è il codice
	//		// inesistente 0000
	//		oc.setCodQualificaSrqRilasciata(codQualificaSRQ);
	//		oc.setDurata(xpath.compile("Durata").evaluate(nCorso));
	//		oc.setPeriodoSvolgimento(xpath.compile("PeriodoSvolgimento").evaluate(nCorso));
	//		oc.setNumeroPartecipanti(Integer.valueOf(xpath.compile("NumeroPartecipanti").evaluate(nCorso), 10));
	//		oc.setQuotaIscrizioneIndividuale(new BigDecimal(xpath.compile("QuotaIndividuale").evaluate(nCorso)));
	//		oc.setAmministrazioneCompetente(xpath.compile("AmministrazioneCompetente").evaluate(nCorso));
	//		oc.setScuolaCapofila(xpath.compile("ScuolaCapofila").evaluate(nCorso));
	//		oc.setUniversita(xpath.compile("Universita").evaluate(nCorso));
	//		oc.setImprese(xpath.compile("Imprese").evaluate(nCorso));
	//
	//	}
	//
	//	private OrEdizioneAvviataDTO parseEdizioniAvviate(Node edizioneAvviata, XPath xpath)
	//			throws XPathExpressionException {
	//		OrEdizioneAvviataDTO o = new OrEdizioneAvviataDTO();
	//		o.setCodComune(xpath.compile("Comune").evaluate(edizioneAvviata));
	//		return o;
	//	}
	//
	//	private OrSedeCorsoDTO parseSedeCorso(Node nSede, XPath xpath) throws XPathExpressionException {
	//		OrSedeCorsoDTO o = new OrSedeCorsoDTO();
	//		o.setCodComune(xpath.compile("Comune").evaluate(nSede));
	//		o.setNominativoReferente(xpath.compile("NominativoReferente").evaluate(nSede));
	//		o.setTelefonoReferente(xpath.compile("TelefonoReferente").evaluate(nSede));
	//		o.setEmailReferente(xpath.compile("EmailReferente").evaluate(nSede));
	//		return o;
	//	}
	
	//	private OrEnteCorsoDTO parseEnte(Node nCorso, XPath xpath) throws XPathExpressionException {
	//		XPathExpression codiceOrganismoXp = xpath.compile("Ente/CodiceOrganismo");
	//		Integer co = new Integer(codiceOrganismoXp.evaluate(nCorso));
	//		OrEnteCorsoDTO ec = orEnteCorsoHome.findByCodiceOrganismo(co);
	//		Date now = new Date();
	//		if (ec == null) {
	//			ec = new OrEnteCorsoDTO();
	//			ec.setCodiceOrganismo(co);
	//			ec.setDtmIns(now);
	//		}
	//		XPathExpression ragioneSocialeXp = xpath.compile("Ente/RagioneSociale");
	//		ec.setRagioneSociale(ragioneSocialeXp.evaluate(nCorso));
	//		XPathExpression IndirizzoXp = xpath.compile("Ente/Indirizzo");
	//		ec.setIndirizzo(IndirizzoXp.evaluate(nCorso));
	//		XPathExpression CapXp = xpath.compile("Ente/Cap");
	//		ec.setCap(CapXp.evaluate(nCorso));
	//
	//		XPathExpression ComuneXp = xpath.compile("Ente/Comune");
	//		String codCom = ComuneXp.evaluate(nCorso);
	//		ec.setCodComune(codCom);
	//
	//		XPathExpression TelefonoXp = xpath.compile("Ente/Telefono");
	//		ec.setTelefono(TelefonoXp.evaluate(nCorso));
	//		XPathExpression FaxXp = xpath.compile("Ente/Fax");
	//		ec.setFax(FaxXp.evaluate(nCorso));
	//
	//		XPathExpression EmailXp = xpath.compile("Ente/Email");
	//		ec.setEmail(EmailXp.evaluate(nCorso));
	//		return ec;
	//	}
	
	
	
	private String creaMessaggio(String esito, String msg) {
		return creaMessaggio(esito, msg, null);
	}

	private String creaMessaggio(String esito, String msg, List<Integer> lIdCorsi) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<EsitoCorsiOrienter>");
		sb.append("<Esito>");
			sb.append("<codice>");
			sb.append(esito);
			sb.append("</codice>");
			sb.append("<descrizione>");
			sb.append(msg);
			sb.append("</descrizione>");
		sb.append("</Esito>");
		if (lIdCorsi != null)
			if (!lIdCorsi.isEmpty()) {
				sb.append("<CorsiNonAllineati>");
				for (Integer idCorso : lIdCorsi) {
					sb.append("<id_corso>");
					sb.append(idCorso);
					sb.append("</id_corso>");
				}
				sb.append("</CorsiNonAllineati>");
			}
		sb.append("</EsitoCorsiOrienter>");
		return sb.toString();
	}
}
