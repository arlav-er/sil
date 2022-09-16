package it.eng.myportal.entity.home;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.RvGroupDTO;
import it.eng.myportal.dtos.RvRicercaVacancyDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VacancyDaRedazioneDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.SvAziendaInfo;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaDatiVacancy_;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeRegione_;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.helpers.RedazioneSearchParams;
import it.eng.myportal.helpers.VacancyRedazioneSortEnum;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * Classe di Home per la gestione delle ricerche delle Vacancy.
 * 
 * @author Rodi A.
 * 
 */
@Stateless
public class RvRicercaVacancyHome {

	private static final String SOLR = "/core0/select/?";
	private static final String FILTRO_TUTTO = "*%3A*";

	protected final Log log = LogFactory.getLog(this.getClass());

	@PersistenceContext
	EntityManager entityManager;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	/**
	 * Trasforma un entity in DTO e setta di default l'attributo hasCandidatura a false, in caso contrario, bisogna
	 * richiamare il suo overload.
	 * 
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @return RvRicercaVacancyDTO
	 */
	public RvRicercaVacancyDTO toDTO(VaDatiVacancy vaDatiVacancy) {
		return toDTO(vaDatiVacancy, false);
	}

	/**
	 * Trasforma un entity in DTO ed offre la possibilità di definire l'attributo hasCandidatura a true, in caso
	 * contrario, si può richiamare il toDTO di base.
	 * 
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param hasCandidatura
	 *            boolean
	 * @return RvRicercaVacancyDTO
	 */
	public RvRicercaVacancyDTO toDTO(VaDatiVacancy vaDatiVacancy, boolean hasCandidatura) {
		RvRicercaVacancyDTO dto = new RvRicercaVacancyDTO();
		PfPrincipal pfPrincipal = vaDatiVacancy.getPfPrincipal();
		AziendaInfo aziendaInfo = pfPrincipal.getAziendaInfo();
		Integer idSvAzienda = null;
		SvAziendaInfo vetrina = pfPrincipal.getSvAziendaInfo();
		if (vetrina != null) {
			idSvAzienda = pfPrincipal.getIdPfPrincipal();
		}

		dto.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
		dto.setDescrizione(vaDatiVacancy.getAttivitaPrincipale());
		dto.setDatoreLavoro(aziendaInfo.getRagioneSociale());
		dto.setIndirizzoLavoro(vaDatiVacancy.getIndirizzoLavoro());
		dto.setCodComLavoro(vaDatiVacancy.getDeComune().getCodCom());
		dto.setStrComLavoro(vaDatiVacancy.getDeComune().getDenominazione());
		dto.setCodMansione(vaDatiVacancy.getDeMansione().getCodMansione());
		dto.setStrMansione(vaDatiVacancy.getDeMansione().getDescrizione());
		dto.setFlgCandidatura(vaDatiVacancy.getFlgCandidatura());
		dto.setFlagIdo(vaDatiVacancy.getFlagIdo());

		VaContratto contratto = null;
		List<VaContratto> contrattos = vaDatiVacancy.getVaContrattos();
		if (contrattos != null && contrattos.size() > 0)
			contratto = contrattos.iterator().next();

		if (contratto != null) {
			dto.setCodTipoContratto(contratto.getDeContratto().getCodContratto());
			dto.setStrTipoContratto(contratto.getDeContratto().getDescrizione());
		} else {
			dto.setCodTipoContratto("");
			dto.setStrTipoContratto("");
		}
		dto.setIdSvAzienda(idSvAzienda);
		dto.setHasCandidatura(hasCandidatura);
		return dto;
	}

	/**
	 * Restituisce un DTO attraverso i valori contenuti nelle mappe.
	 * 
	 * @param mapStr
	 *            Map<String, String>
	 * @param mapDouble
	 *            Map<String, Double>
	 * @param mapInt
	 *            Map<String, Integer>
	 * @return RvRicercaVacancyDTO
	 */
	/**
	 * @param mapStr
	 *            Map<String, String>
	 * @param mapDouble
	 *            Map<String, Double>
	 * @param mapInt
	 *            Map<String, Integer>
	 * @param idUtente
	 *            Integer
	 * @return RvRicercaVacancyDTO
	 */
	public RvRicercaVacancyDTO toDTO(Map<String, String> mapStr, Map<String, Double> mapDouble,
			Map<String, Integer> mapInt, Map<String, Date> mapDate, Integer idUtente) {
		RvRicercaVacancyDTO dto = new RvRicercaVacancyDTO();
		dto.setIdVaDatiVacancy(mapInt.get("id_va_dati_vacancy"));
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy());

		dto.setIdSvAzienda(null);
		if (vaDatiVacancy != null) {
			AziendaInfo aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();
			if (aziendaInfo != null) {
				Integer idAziendaInfo = aziendaInfo.getIdPfPrincipal();
				dto.setIdAziendaInfo(idAziendaInfo);
				/*
				 * se esiste la vetrina assegno l'id, altrimenti rimane a null come assegnato precedentemente
				 */
				if (svAziendaInfoHome.exists(idAziendaInfo)) {
					dto.setIdSvAzienda(idAziendaInfo);
				}
			}
			dto.setFlgCandidatura(vaDatiVacancy.getFlgCandidatura());
			dto.setFlagIdo(vaDatiVacancy.getFlagIdo());
		}

		dto.setDescrizione(mapStr.get("attivita_descrizione_estesa"));
		dto.setDatoreLavoro(mapStr.get("ragione_sociale"));
		dto.setIndirizzoLavoro("");

		String codComLavoro = mapStr.get("codcomune");
		String strComLavoro = mapStr.get("comune");

		dto.setCodComLavoro(codComLavoro);
		dto.setStrComLavoro(strComLavoro);

		DeProvincia provincia = deComuneHome.findProvinciaByCodComune(codComLavoro);

		if (provincia != null) {
			dto.setStrProvinciaLavoro(provincia.getDenominazione());
		}
		dto.setCodMansione("");
		dto.setStrMansione(mapStr.get("attivita_principale"));
		dto.setCodTipoContratto("");
		dto.setNumCandidati(mapInt.get("numcandidati"));
		dto.setDataModifica(mapDate.get("data_modifica"));
		dto.setDataPubblicaz(mapDate.get("data_pubblicazione"));
		dto.setDataScadPubblicaz(mapDate.get("data_scadenza_pubblicazione"));

		// Se sto usando le decodifiche SIL, devo controllare quale tipo di contratto prendere.
		if (ConstantsSingleton.usaDecodificheSilPerVacancy() && mapStr.containsKey("contrattosil")) {
			dto.setStrTipoContratto(mapStr.get("contrattosil"));
		} else if (mapStr.containsKey("contratto")) {
			dto.setStrTipoContratto(mapStr.get("contratto"));
		}

		dto.setPubblicato(dto.getPubblicato());
		// verificare se ha già la candidatura per quella vacancy

		// nel caso la ricerca viene visualizzata da un cittadino allora
		// verifico la candidatura
		// nel caso la ricerca viene visualizzata da una provincia non è
		// necessario verificare la candidatura
		if (idUtente != null) {
			AcCandidaturaDTO candidaturaUtente = acCandidaturaHome.findDTOByUtenteAndAziendaIdPfPrincipal(idUtente,
					null, mapInt.get("id_va_dati_vacancy"));
			if (candidaturaUtente == null) {
				dto.setHasCandidatura(false);
			} else {
				dto.setHasCandidatura(true);
			}
		} else {
			dto.setHasCandidatura(false);
		}

		dto.setProvenienza(mapStr.get("provenienza"));

		return dto;
	}

	/**
	 * Restituisce una lista di RvRicercaVacancyDTO attraverso il recupero di tutti i record dal DB
	 * 
	 * @return List<RvRicercaVacancyDTO>
	 */
	public List<RvRicercaVacancyDTO> findAllDTO() {
		// TODO qui chiamo la findAll di DatiVacncy, poi mi prendo anche tutti
		// gli altri dati
		List<VaDatiVacancyDTO> allVaDatiVacancyDTO = vaDatiVacancyHome.findAllDTO();

		List<RvRicercaVacancyDTO> allVaRicercaVacancyDTO = new ArrayList<RvRicercaVacancyDTO>();
		for (VaDatiVacancyDTO vaDatiVacancyDTO : allVaDatiVacancyDTO) {
			VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.fromDTO(vaDatiVacancyDTO);
			RvRicercaVacancyDTO vaRicercaVacancyDTO = toDTO(vaDatiVacancy);
			allVaRicercaVacancyDTO.add(vaRicercaVacancyDTO);
		}

		return allVaRicercaVacancyDTO;
	}

	/**
	 * Servizio che permette di ricercare le Vacancy presenti a sistema suddivise per Gruppo. Prende in input un filtro
	 * di ricerca e popola un DTO contenente le mappe di raggruppamento.
	 * 
	 * @param filter
	 *            filtro di ricerca testata
	 * @param rvGroupDTO
	 *            filtro di ricerca gruppi
	 */
	public void findGroupByFilter(RvTestataDTO filter, RvGroupDTO rvGroupDTO, boolean cleanCheck,
			boolean usaDecodificheSil) {
		if (cleanCheck) {
			rvGroupDTO.setCheckMansione(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckContratto(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckOrario(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckEsperienza(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckSettore(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckLingua(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckTitoloStudio(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckDispTrasferte(new HashMap<String, Boolean>());
			rvGroupDTO.setCheckPatente(new HashMap<String, Boolean>());
		}

		rvGroupDTO.setMapMansione(new HashMap<String, String>());
		rvGroupDTO.setListMansione(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.MANSIONE, filter, rvGroupDTO,
				rvGroupDTO.getMapMansione(), rvGroupDTO.getCheckMansione(), usaDecodificheSil));

		rvGroupDTO.setMapContratto(new HashMap<String, String>());
		rvGroupDTO.setListContratto(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.CONTRATTO, filter, rvGroupDTO,
				rvGroupDTO.getMapContratto(), rvGroupDTO.getCheckContratto(), usaDecodificheSil));

		rvGroupDTO.setMapOrario(new HashMap<String, String>());
		rvGroupDTO.setListOrario(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.ORARIO, filter, rvGroupDTO,
				rvGroupDTO.getMapOrario(), rvGroupDTO.getCheckOrario(), usaDecodificheSil));

		rvGroupDTO.setMapPatente(new HashMap<String, String>());
		rvGroupDTO.setListPatente(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.PATENTE, filter, rvGroupDTO,
				rvGroupDTO.getMapPatente(), rvGroupDTO.getCheckPatente(), usaDecodificheSil));

		/*
		 * rvGroupDTO.setMapEsperienza(new HashMap<String, String>()); rvGroupDTO
		 * .setListEsperienza(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy .ESPERIENZA, filter, rvGroupDTO,
		 * rvGroupDTO.getMapEsperienza(), rvGroupDTO.getCheckEsperienza()));
		 */
		rvGroupDTO.setMapSettore(new HashMap<String, String>());
		rvGroupDTO.setListSettore(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.SETTORE, filter, rvGroupDTO,
				rvGroupDTO.getMapSettore(), rvGroupDTO.getCheckSettore(), usaDecodificheSil));

		rvGroupDTO.setMapLingua(new HashMap<String, String>());
		rvGroupDTO.setListLingua(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.LINGUA, filter, rvGroupDTO,
				rvGroupDTO.getMapLingua(), rvGroupDTO.getCheckLingua(), usaDecodificheSil));

		rvGroupDTO.setMapTitoloStudio(new HashMap<String, String>());
		rvGroupDTO.setListTitoloStudio(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO, filter,
				rvGroupDTO, rvGroupDTO.getMapTitoloStudio(), rvGroupDTO.getCheckTitoloStudio(), usaDecodificheSil));

		/*
		 * rvGroupDTO.setMapDispTrasferte(new HashMap<String, String>()); rvGroupDTO
		 * .setListDispTrasferte(recuperaGruppo(ConstantsSingleton.RvRicercaVacancy .DISP_TRASFERTE, filter, rvGroupDTO,
		 * rvGroupDTO.getMapDispTrasferte(), rvGroupDTO.getCheckDispTrasferte()));
		 */

		return;
	}

	/**
	 * Crea e restituisce i parametri
	 * 
	 * @param nomeGruppo
	 *            String
	 * @param filter
	 *            filtro di ricerca testata
	 * @param rvGroupDTO
	 *            filtro di ricerca gruppi
	 * @return String
	 */
	public String getParam(String nomeGruppo, RvTestataDTO filter, RvGroupDTO rvGroupDTO, boolean usaDecodificheSil) {
		String filtro = calcolaFiltro(filter, rvGroupDTO, usaDecodificheSil) + "&indent=on&wt=xml";
		if (nomeGruppo != null) {
			// filtro+= "&rows=9999&group=true&group.field=" + nomeGruppo;
			filtro += "&rows=0&facet=true&facet.mincount=1&facet.field=" + nomeGruppo;
		}
		return filtro;
	}

	/**
	 * Recupera una lista di raggruppamenti come FACET
	 * 
	 * @param nomeGruppo
	 *            String
	 * @param filter
	 *            RvTestataDTO filtro di ricerca testata
	 * @param rvGroupDTO
	 *            RvGroupDTO filtro di ricerca gruppi
	 * @param mapGruppo
	 *            Map<String, String>
	 * @param mapCheck
	 *            Map<String, Boolean>
	 * @return Set<String>
	 */
	public List<String> recuperaGruppo(String nomeGruppo, RvTestataDTO filter, RvGroupDTO rvGroupDTO,
			Map<String, String> mapGruppo, Map<String, Boolean> mapCheck, boolean usaDecodificheSil) {
		try {
			List<String> listGruppo = new ArrayList<String>();
			// String desGruppo = "";
			String url = ConstantsSingleton.getSolrUrl() + SOLR
					+ getParam(nomeGruppo, filter, rvGroupDTO, usaDecodificheSil);
			log.debug("SOLR URL:" + url);
			Document document = Utils.documentSOLR(url);
			XPath xpath = XPathFactory.newInstance().newXPath();
			if (document != null) {
				Map<String, String> mapStr = new HashMap<String, String>();
				XPathExpression exprConteggi = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int");
				Object resultConteggi = exprConteggi.evaluate(document, XPathConstants.NODESET);
				NodeList nodesConteggi = (NodeList) resultConteggi;
				for (int i = 1; i <= nodesConteggi.getLength(); i++) {
					XPathExpression exprTot = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int[" + i + "]");
					Object totale = exprTot.evaluate(document, XPathConstants.STRING);
					String value = totale.toString();
					Integer totaleInt = new Integer(value);

					XPathExpression exprCodice = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int[" + i + "]/@name");
					Object codice = exprCodice.evaluate(document, XPathConstants.STRING);
					// String name = codice.toString();
					String descrizioneDecodifica = (codice.toString()).toUpperCase();
					if (totaleInt > 0) {
						String codiceDecodifica = getCodiceDecodifica(nomeGruppo, descrizioneDecodifica,
								usaDecodificheSil);

						mapStr.put(codiceDecodifica, value);
						String gruppo = descrizioneDecodifica + " (" + value + ")";

						// non vengono visualizzati i gruppi/totali nulli
						listGruppo.add(gruppo);
						mapCheck.put(codiceDecodifica, false);
						mapGruppo.put(gruppo, codiceDecodifica);
					}
				}
			}
			return listGruppo;

		} catch (Exception e) {
			log.error("Errore nel recupero dei gruppi delle vacancy da SOLR " + e);
		}
		return null;

	}

	// veniva recuperato prendendo come query SOLR group=true
	public List<String> recuperaGruppoOld(String nomeGruppo, RvTestataDTO filter, RvGroupDTO rvGroupDTO,
			Map<String, String> mapGruppo, Map<String, Boolean> mapCheck, boolean usaDecodificheSil) {
		List<String> listGruppo = new ArrayList<String>();
		String desGruppo = "";
		String url = ConstantsSingleton.getSolrUrl() + SOLR
				+ getParam(nomeGruppo, filter, rvGroupDTO, usaDecodificheSil);
		log.debug("SOLR URL:" + url);
		Document document = Utils.documentSOLR(url);
		if (document != null) {
			NodeList nodeResult = document.getElementsByTagName("result");
			NodeList nodeList = document.getElementsByTagName("doc");
			Map<String, String> mapStr = new HashMap<String, String>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node result = (Node) nodeResult.item(i);
				String totalGroup = result.getAttributes().item(1).getNodeValue();
				Node doc = (Node) nodeList.item(i);
				Element elementDoc = (Element) doc.getChildNodes();
				NodeList nodeListStr = elementDoc.getElementsByTagName("str");
				for (int y = 0; y < nodeListStr.getLength(); y++) {
					Node node = nodeListStr.item(y);
					String name = "", value = "";
					if (node.hasAttributes()) {
						name = nodeListStr.item(y).getAttributes().item(0).getNodeValue();
					} else {
						name = nodeListStr.item(y).getParentNode().getAttributes().item(0).getNodeValue();
					}
					if (nodeListStr.item(y).getFirstChild() != null) {
						value = nodeListStr.item(y).getFirstChild().getNodeValue();
					}
					mapStr.put(name, value);
					if (desGruppo.length() == 0 && name.equals(nomeGruppo)) {
						// recupero il campo desGruppo utile per recuperare la
						// descrizione del codGruppo all'interno del file xml
						desGruppo = nomeGruppo.substring(3);

					} else if (name.equals(desGruppo)) {
						break;
					}
				}
				String strDecodeGruppo = mapStr.get(desGruppo);
				String gruppo = strDecodeGruppo + " (" + totalGroup + ")";
				// non vengono visualizzati i gruppi/totali nulli
				if (strDecodeGruppo != null && !("").equalsIgnoreCase(strDecodeGruppo)
						&& !("null").equalsIgnoreCase(strDecodeGruppo)) {
					listGruppo.add(gruppo);
					mapCheck.put(mapStr.get(nomeGruppo), false);
					mapGruppo.put(gruppo, mapStr.get(nomeGruppo));
				}
			}
		}
		return listGruppo;
	}

	/**
	 * Servizio che permette di ricercare le Vacancy presenti a sistema. Prende in input un filtro di ricerca e
	 * restituisce la lista delle offerte di lavoro che corrispondo ai parametri.
	 * 
	 * @param filter
	 *            RvTestataDTO filtro di ricerca testata
	 * @param rvGroupDTO
	 *            RvGroupDTO filtro di ricerca gruppi
	 * @param menu
	 *            String
	 * @param key
	 *            String
	 * @param idUtente
	 *            Integer
	 * @return la lista dei risultati.
	 */
	public List<RvRicercaVacancyDTO> findDTOByFilter(RvTestataDTO filter, RvGroupDTO rvGroupDTO, String menu,
			String key, Integer idUtente, boolean usaDecodificheSil) {
		// modifico il valore del menu
		if (menu.equals(ConstantsSingleton.RvRicercaVacancy.MANSIONE)) {
			rvGroupDTO.getCheckMansione().put(key, !rvGroupDTO.getCheckMansione().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.CONTRATTO)) {
			rvGroupDTO.getCheckContratto().put(key, !rvGroupDTO.getCheckContratto().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.ORARIO)) {
			rvGroupDTO.getCheckOrario().put(key, !rvGroupDTO.getCheckOrario().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.ESPERIENZA)) {
			rvGroupDTO.getCheckEsperienza().put(key, !rvGroupDTO.getCheckEsperienza().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.SETTORE)) {
			rvGroupDTO.getCheckSettore().put(key, !rvGroupDTO.getCheckSettore().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.LINGUA)) {
			rvGroupDTO.getCheckLingua().put(key, !rvGroupDTO.getCheckLingua().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO)) {
			rvGroupDTO.getCheckTitoloStudio().put(key, !rvGroupDTO.getCheckTitoloStudio().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.DISP_TRASFERTE)) {
			rvGroupDTO.getCheckDispTrasferte().put(key, !rvGroupDTO.getCheckDispTrasferte().get(key));

		} else if (menu.equals(ConstantsSingleton.RvRicercaVacancy.PATENTE)) {
			rvGroupDTO.getCheckPatente().put(key, !rvGroupDTO.getCheckPatente().get(key));
		}

		return findDTOByFilter(filter, rvGroupDTO, idUtente, usaDecodificheSil);
	}

	/**
	 * Servizio che permette di ricercare le Vacancy presenti a sistema. Prende in input un filtro di ricerca e
	 * restituisce la lista delle offerte di lavoro che corrispondo ai parametri.
	 * 
	 * @param filter
	 *            filtro di ricerca testata
	 * @param rvGroupDTO
	 *            filtro di ricerca gruppi
	 * @param idUtente
	 *            Integer
	 * @return la lista dei risultati.
	 */
	public List<RvRicercaVacancyDTO> findDTOByFilter(RvTestataDTO filter, RvGroupDTO rvGroupDTO, Integer idUtente,
			boolean usaDecodificheSil) {
		// Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName()
		// + ".findDTOByFilter (calling great SOLR)");
		String filtro = calcolaFiltro(filter, rvGroupDTO, usaDecodificheSil);
		String url = ConstantsSingleton.getSolrUrl() + SOLR + filtro + "&start=0&rows=" + filter.getRowsForLoad()
				+ "&indent=on&wt=xml&sort=data_modifica%20desc";
		log.debug("SOLR URL:" + url);
		List<RvRicercaVacancyDTO> ret = new ArrayList<RvRicercaVacancyDTO>();
		try {
			Document document = Utils.documentSOLR(url);
			if (document != null) {
				// log.debug("VACANCY SOLR XML:" + Utils.domToString(document));
				NodeList nodeResult = document.getElementsByTagName("result");
				Node result = (Node) nodeResult.item(0);
				filter.setRowsTotal(Integer.parseInt(result.getAttributes().item(1).getNodeValue()));
				NodeList nodeList = document.getElementsByTagName("doc");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node doc = (Node) nodeList.item(i);
					Element elementDoc = (Element) doc.getChildNodes();
					NodeList nodeListStr = elementDoc.getElementsByTagName("str"),
							nodeListDouble = elementDoc.getElementsByTagName("double"),
							nodeListInt = elementDoc.getElementsByTagName("int"),
							nodeListDate = elementDoc.getElementsByTagName("date");
					// Aggiungo il contenuto delle stringhe
					Map<String, String> mapStr = new HashMap<String, String>();
					for (int y = 0; y < nodeListStr.getLength(); y++) {
						Node node = nodeListStr.item(y);
						String name = "", value = "";
						if (node.hasAttributes()) {
							name = nodeListStr.item(y).getAttributes().item(0).getNodeValue();
						} else {
							name = nodeListStr.item(y).getParentNode().getAttributes().item(0).getNodeValue();
						}
						if (nodeListStr.item(y).getFirstChild() != null) {
							value = nodeListStr.item(y).getFirstChild().getNodeValue();
						}
						mapStr.put(name, value);
					}
					Map<String, Date> mapDate = popolaMapDate(nodeListDate);
					Map<String, Double> mapDouble = popolaMapDouble(nodeListDouble);
					Map<String, Integer> mapInt = popolaMapInt(nodeListInt);
					ret.add(toDTO(mapStr, mapDouble, mapInt, mapDate, idUtente));
				}
			}

		} catch (Exception e) {
			log.error("Errore nel recupero delle vacancy da SOLR: " + e.getMessage());
		}
		// incremento del contatore dopo il caricamento
		filter.setRowsForLoad(filter.getRowsForLoad() + filter.getRowsToLoad());
		// recupero il numero effettivo dellle righe caricate
		filter.setRowsLoaded(ret.size());

		if (filter.getRowsLoaded() == 0) {
			filter.setRowsForLoad(filter.getRowsToLoad());
		}
		log.debug("Ho recuperato " + filter.getRowsLoaded() + " risultati");
		return ret;
	}

	/**
	 * Popola tutti i double presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListDouble
	 * @return Map<String, Double>
	 */
	private Map<String, Double> popolaMapDouble(NodeList nodeListDouble) {
		// Aggiungo il contenuto dei double
		Map<String, Double> mapDouble = new HashMap<String, Double>();
		for (int y = 0; y < nodeListDouble.getLength(); y++) {
			String name = nodeListDouble.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListDouble.item(y).getFirstChild() != null) {
				value = nodeListDouble.item(y).getFirstChild().getNodeValue();
			}
			mapDouble.put(name, Double.valueOf(value));
		}
		return mapDouble;
	}

	/**
	 * Popola tutti gli Integer presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListInt
	 * @return Map<String, Integer>
	 */
	private Map<String, Integer> popolaMapInt(NodeList nodeListInt) {
		// Aggiungo il contenuto degli int
		Map<String, Integer> mapInt = new HashMap<String, Integer>();
		for (int y = 0; y < nodeListInt.getLength(); y++) {
			String name = nodeListInt.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListInt.item(y).getFirstChild() != null) {
				value = nodeListInt.item(y).getFirstChild().getNodeValue();
			}
			mapInt.put(name, Integer.valueOf(value));
		}
		return mapInt;
	}

	/**
	 * Popola tutte le date presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListDouble
	 * @return Map<String, Double>
	 */
	private Map<String, Date> popolaMapDate(NodeList nodeListDate) {
		// Aggiungo il contenuto dei double
		Map<String, Date> mapDate = new HashMap<String, Date>();
		for (int y = 0; y < nodeListDate.getLength(); y++) {
			String name = nodeListDate.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListDate.item(y).getFirstChild() != null) {
				value = nodeListDate.item(y).getFirstChild().getNodeValue();
			}
			String dateStr = value.substring(0, value.indexOf("T")),
					timeStr = value.substring(value.indexOf("T") + 1, value.indexOf("T") + 9);
			Date date = new Date(Date.valueOf(dateStr).getTime() + (Time.valueOf(timeStr).getTime() + 3600000));
			mapDate.put(name, date);
		}
		return mapDate;
	}

	/**
	 * Restituisce la queryString da passare a SOLR relativa ai filtri di 1° e 2° livello
	 * 
	 * @param filter
	 * @param rvGroupDTO
	 * @return
	 */
	private String calcolaFiltro(RvTestataDTO filter, RvGroupDTO rvGroupDTO, boolean usaDecodificheSil) {
		String cosa = filter.getCosa();
		String dove = filter.getDove();
		String filtro = "q=" + FILTRO_TUTTO;
		// Creo il filtro a partire dal Cosa e/o Dove
		if (cosa != null && cosa.trim().length() > 0) {

			// la query su solr deve essere del tipo q=cosa:(*azienda* +
			// *progetto*)
			String[] parole = cosa.trim().toLowerCase().split(" ");
			String cosaCercare = "";
			for (int i = 0; i < parole.length; i++) {
				if (i > 0) {
					cosaCercare = cosaCercare + " + ";
				}
				cosaCercare = cosaCercare + "*" + parole[i] + "*";
			}
			filtro = "q=cosa%3A(" + cosaCercare + ")*";
			if (filtro.equals("*"))
				filtro = "q=" + FILTRO_TUTTO;
		}
		if (dove != null) {

			String[] paroleDove = dove.trim().toLowerCase().split(" ");
			String doveCercare = "";
			for (int i = 0; i < paroleDove.length; i++) {
				if (i > 0) {
					doveCercare = doveCercare + " + ";
				}
				doveCercare = doveCercare + "*" + paroleDove[i] + "*";
			}

			filtro = dove.length() > 0 ? (filtro + "+AND+dove%3A(" + doveCercare + ")") : filtro;
		}

		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckMansione(), rvGroupDTO.getMapMansione(),
				rvGroupDTO.getListMansione(), ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckEsperienza(), rvGroupDTO.getMapEsperienza(),
				rvGroupDTO.getListEsperienza(), ConstantsSingleton.RvRicercaVacancy.CODESPERIENZA);
		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckSettore(), rvGroupDTO.getMapSettore(),
				rvGroupDTO.getListSettore(), ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckLingua(), rvGroupDTO.getMapLingua(), rvGroupDTO.getListLingua(),
				ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckTitoloStudio(), rvGroupDTO.getMapTitoloStudio(),
				rvGroupDTO.getListTitoloStudio(), ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		filtro += calcolaFiltroCheck(rvGroupDTO.getCheckDispTrasferte(), rvGroupDTO.getMapDispTrasferte(),
				rvGroupDTO.getListDispTrasferte(), ConstantsSingleton.RvRicercaVacancy.CODDISP_TRASFERTE);

		if (usaDecodificheSil) {
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckContratto(), rvGroupDTO.getMapContratto(),
					rvGroupDTO.getListContratto(), ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL);
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckOrario(), rvGroupDTO.getMapOrario(),
					rvGroupDTO.getListOrario(), ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL);
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckPatente(), rvGroupDTO.getMapPatente(),
					rvGroupDTO.getListPatente(), ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL);
		} else {
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckContratto(), rvGroupDTO.getMapContratto(),
					rvGroupDTO.getListContratto(), ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO);
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckOrario(), rvGroupDTO.getMapOrario(),
					rvGroupDTO.getListOrario(), ConstantsSingleton.RvRicercaVacancy.CODORARIO);
			filtro += calcolaFiltroCheck(rvGroupDTO.getCheckPatente(), rvGroupDTO.getMapPatente(),
					rvGroupDTO.getListPatente(), ConstantsSingleton.RvRicercaVacancy.CODPATENTE);
		}

		/**
		 * Aggiungo filtro sulla data di scadenza Le vacancy sono valide se la data scadenza > oggi.
		 * 
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		/*
		 * UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA if
		 * (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) c.add(Calendar.DATE, +1);
		 */
		String dataInizio = dateFormat.format(c.getTime());
		c.add(Calendar.YEAR, 100);
		String dataFine = dateFormat.format(c.getTime());
		filtro += "+AND+data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO " + dataFine + "T00:00:00Z]";

		filtro = it.eng.myportal.utils.URL.escapeChrUrl(filtro);
		return it.eng.myportal.utils.URL.escape(filtro);
	}

	/**
	 * @param Map
	 *            <String, Boolean> mappa dei valori check
	 * @param Set
	 *            <String> lista dei menu check
	 * @return String filtro da appendere ai precedenti
	 */
	private String calcolaFiltroCheck(Map<String, Boolean> mapCheck, Map<String, String> mapGruppo,
			List<String> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (mapCheck != null && mapGruppo != null && !mapCheck.isEmpty() && !mapGruppo.isEmpty()) {
			List<String> listKey = new ArrayList<String>();
			for (int i = 0; i < listGruppo.size(); i++) {
				String key = listGruppo.get(i);
				String keyGruppo = mapGruppo.get(key);
				Boolean value = mapCheck.get(keyGruppo);
				if (value) {
					listKey.add(keyGruppo);
				}
			}
			if (!listKey.isEmpty()) {
				sb.append("+AND+" + param + "%3A(");
				for (Iterator<String> iterator = listKey.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					sb.append(key + " OR ");
				}
				str = sb.toString();
				str = str.substring(0, str.lastIndexOf("OR")) + ")";
			}
		}
		return str;
	}

	/**
	 * Restituisce la queryString da passare a SOLR relativa ai filtri di 1° e 2° livello
	 * 
	 * @param filter
	 * @param rvGroupDTO
	 * @return
	 */
	private String calcolaFiltroRicercaNew(RvTestataDTO filter, RvGroupDTO rvGroupDTO, boolean usaDecodificheSil) {
		String cosa = filter.getCosa();
		String dove = filter.getDove();
		String filtro = "q=" + FILTRO_TUTTO;
		// Creo il filtro a partire dal Cosa e/o Dove
		if (cosa != null && cosa.trim().length() > 0) {

			// la query su solr deve essere del tipo q=cosa:(*azienda* +
			// *progetto*)
			String[] parole = cosa.trim().toLowerCase().split(" ");
			String cosaCercare = "";
			for (int i = 0; i < parole.length; i++) {
				if (i > 0) {
					cosaCercare = cosaCercare + " + ";
				}
				cosaCercare = cosaCercare + "*" + parole[i] + "*";
			}
			filtro = "q=cosa%3A(" + cosaCercare + ")*";
			if (filtro.equals("*"))
				filtro = "q=" + FILTRO_TUTTO;
		}
		if (dove != null) {

			String[] paroleDove = dove.trim().toLowerCase().split(" ");
			String doveCercare = "";
			for (int i = 0; i < paroleDove.length; i++) {
				if (i > 0) {
					doveCercare = doveCercare + " + ";
				}
				doveCercare = doveCercare + "*" + paroleDove[i] + "*";
			}

			filtro = dove.length() > 0 ? (filtro + "+AND+dove%3A(" + doveCercare + ")") : filtro;
		}

		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckMansione(),
				ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckEsperienza(),
				ConstantsSingleton.RvRicercaVacancy.CODESPERIENZA);
		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckSettore(), ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckLingua(), ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckTitoloStudio(),
				ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckDispTrasferte(),
				ConstantsSingleton.RvRicercaVacancy.CODDISP_TRASFERTE);

		if (usaDecodificheSil) {
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckContratto(),
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL);
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckOrario(),
					ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL);
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckPatente(),
					ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL);
		} else {
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckContratto(),
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO);
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckOrario(),
					ConstantsSingleton.RvRicercaVacancy.CODORARIO);
			filtro += calcolaFiltroRicercaNew(rvGroupDTO.getCheckPatente(),
					ConstantsSingleton.RvRicercaVacancy.CODPATENTE);
		}
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			filtro += calcolaFiltroRicercaNewAgricolo(rvGroupDTO.getCheckAgricolo());
		}

		/**
		 * Aggiungo filtro sulla data di scadenza Le vacancy sono valide se la data scadenza > oggi.
		 * 
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		/*
		 * UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA if
		 * (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) c.add(Calendar.DATE, +1);
		 */
		String dataInizio = dateFormat.format(c.getTime());
		c.add(Calendar.YEAR, 100);
		String dataFine = dateFormat.format(c.getTime());
		filtro += "+AND+data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO " + dataFine + "T00:00:00Z]";

		filtro = it.eng.myportal.utils.URL.escapeChrUrl(filtro);
		return it.eng.myportal.utils.URL.escape(filtro);
	}

	private String calcolaFiltroRicercaNew(Map<String, Boolean> mapCheck, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		// List<String> listKey = new ArrayList<String>();

		if (mapCheck != null && !mapCheck.isEmpty()) {
			Set<String> val = mapCheck.keySet();

			sb.append("+AND+" + param + "%3A(");
			for (Iterator<String> iterator = val.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				sb.append(key + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";

		}
		return str;
	}

	// private String calcolaFiltroRicercaNewAgricolo(Map<String, Boolean> mapCheck, String param) {
	private String calcolaFiltroRicercaNewAgricolo(Map<String, Boolean> mapCheck) {
		StringBuilder sb = new StringBuilder();
		String str = "";

		if (mapCheck != null && !mapCheck.isEmpty()) {
			sb.append("+AND+flg_agricoltura:Y");
			/*
			 * Set<String> val = mapCheck.keySet();
			 * 
			 * sb.append("+AND+" + param + "%3A("); for (Iterator<String> iterator = val.iterator();
			 * iterator.hasNext();) { String key = iterator.next(); sb.append(key + " OR "); }
			 */
			str = sb.toString();
			// str = str.substring(0, str.lastIndexOf("OR")) + ")";

		}
		return str;
	}

	private String getDescrizioneDecodifica(String nomeGruppo, String idDecodifica) {

		if (("codmansione").equalsIgnoreCase(nomeGruppo)) {
			return deMansioneHome.findById(idDecodifica).getDescrizione();
		} else if (("codcontratto").equalsIgnoreCase(nomeGruppo)) {
			return deContrattoHome.findById(idDecodifica).getDescrizione();
		} else if (("codorario").equalsIgnoreCase(nomeGruppo)) {
			return deOrarioHome.findById(idDecodifica).getDescrizione();
		} else if (("codesperienza").equalsIgnoreCase(nomeGruppo)) {
			return idDecodifica;
		} else if (("codsettore").equalsIgnoreCase(nomeGruppo)) {
			return deAttivitaHome.findById(idDecodifica).getDescrizione();
		} else if (("codlingua").equalsIgnoreCase(nomeGruppo)) {
			return deLinguaHome.findById(idDecodifica).getDenominazione();
		} else if (("codtitolo").equalsIgnoreCase(nomeGruppo)) {
			return deTitoloHome.findById(idDecodifica).getDescrizione();
		} else if (("codtrasferta").equalsIgnoreCase(nomeGruppo)) {
			return idDecodifica;
		} else if (("codpatente").equalsIgnoreCase(nomeGruppo)) {
			return dePatenteHome.findById(idDecodifica).getDescrizione();
		} else if (("codcontrattosil").equalsIgnoreCase(nomeGruppo)) {
			return deContrattoSilHome.findById(idDecodifica).getDescrizione();
		} else if (("codorariosil").equalsIgnoreCase(nomeGruppo)) {
			return deOrarioSilHome.findById(idDecodifica).getDescrizione();
		} else if (("codpatentesil").equalsIgnoreCase(nomeGruppo)) {
			return dePatenteSilHome.findById(idDecodifica).getDescrizione();
		}

		return idDecodifica;
	}

	/**
	 * viene utilizzato il metodo findByDescriptionAll delle suggestion che recupera tutti i codici anche quelli scaduti
	 * dalle descrizioni
	 * 
	 * @param nomeGruppo
	 * @param idDecodifica
	 * @return
	 */
	private String getCodiceDecodifica(String nomeGruppo, String idDecodifica, boolean usaDecodificheSil) {

		if (("mansione").equalsIgnoreCase(nomeGruppo)) {
			return deMansioneHome.findByDescriptionAll(idDecodifica).get(0).getId();
		} else if (("contratto").equalsIgnoreCase(nomeGruppo)) {
			if (usaDecodificheSil)
				return deContrattoSilHome.findByDescriptionAll(idDecodifica).get(0).getId();
			else
				return deContrattoHome.findByDescriptionAll(idDecodifica).get(0).getId();
		} else if (("orario").equalsIgnoreCase(nomeGruppo)) {
			// implementato nella home: non verifica se le codifiche scadute
			if (usaDecodificheSil)
				return deOrarioSilHome.findByDescrizione(idDecodifica).getCodOrarioSil();
			else
				return deOrarioHome.findByDescrizione(idDecodifica).getCodOrario();
		} else if (("esperienza").equalsIgnoreCase(nomeGruppo)) {
			return idDecodifica;
		} else if (("settore").equalsIgnoreCase(nomeGruppo)) {
			return deAttivitaHome.findByDescriptionAll(idDecodifica).get(0).getId();
		} else if (("lingua").equalsIgnoreCase(nomeGruppo)) {
			return deLinguaHome.findByDescriptionAll(idDecodifica).get(0).getId();
		} else if (("titolo").equalsIgnoreCase(nomeGruppo)) {
			return deTitoloHome.findByDescriptionAll(idDecodifica).get(0).getId();
		} else if (("trasferta").equalsIgnoreCase(nomeGruppo)) {
			return idDecodifica;
		} else if (("patente").equalsIgnoreCase(nomeGruppo)) {
			// implementato nella home: non verifica se le codifiche scadute
			if (usaDecodificheSil)
				return dePatenteSilHome.findByDescrizione(idDecodifica).getCodPatenteSil();
			else
				return dePatenteHome.findByDescrizione(idDecodifica).getCodPatente();
		}

		return idDecodifica;
	}

	public AcCandidaturaHome getAcCandidaturaHome() {
		return acCandidaturaHome;
	}

	public void setAcCandidaturaHome(AcCandidaturaHome acCandidaturaHome) {
		this.acCandidaturaHome = acCandidaturaHome;
	}

	/**
	 * @return the vaDatiVacancyHome
	 */
	public VaDatiVacancyHome getVaDatiVacancyHome() {
		return vaDatiVacancyHome;
	}

	/**
	 * @param vaDatiVacancyHome
	 *            the vaDatiVacancyHome to set
	 */
	public void setVaDatiVacancyHome(VaDatiVacancyHome vaDatiVacancyHome) {
		this.vaDatiVacancyHome = vaDatiVacancyHome;
	}

	public int getNumVacancyModificate(RvTestataDTO filter, RvGroupDTO rvGroupDTO, java.util.Date date) {
		return getNumVacancyModificate(filter, rvGroupDTO, date, true);
	}

	public int getNumVacancyModificate(RvTestataDTO filter, RvGroupDTO rvGroupDTO, java.util.Date date,
			boolean usaDecodificheSil) {
		int numeroVacancyMod = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormatOra = new SimpleDateFormat("HH:mm");
		java.util.Date now = new java.util.Date();
		java.util.Date domani = DateUtils.addDays(now, 1);
		String dataFineFormattata = dateFormat.format(domani);

		// data in formato UTC come richiesto da SOLR
		String dataInizioFormattata = DateFormatUtils.formatUTC(date, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());

		// Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName()
		// + ".getNumVacancyModificate (calling great SOLR)");
		String filtro = calcolaFiltroRicercaNew(filter, rvGroupDTO, usaDecodificheSil);

		String filtroVacancyModificate = it.eng.myportal.utils.URL
				.escape(" AND data_modifica:[" + dataInizioFormattata + "Z TO " + dataFineFormattata + "T00:00:00Z]");
		String url = ConstantsSingleton.getSolrUrl() + SOLR + filtro + filtroVacancyModificate
				+ "&start=0&rows=1&indent=on&wt=xml";

		filter.setUrlFiltroSolr(filtro);

		log.debug("SOLR URL:" + url);
		// String num;
		try {
			Document document = Utils.documentSOLR(url);
			if (document != null) {
				// log.debug("VACANCY SOLR XML:" + Utils.domToString(document));
				NodeList nodeResult = document.getElementsByTagName("result");
				Node result = (Node) nodeResult.item(0);
				NamedNodeMap attrs = result.getAttributes();
				Node numFound = attrs.getNamedItem("numFound");
				numeroVacancyMod = new Integer(numFound.getNodeValue());

			}

		} catch (Exception e) {
			log.error("Errore nel recupero delle vacancy da SOLR " + e);
		}
		return numeroVacancyMod;
	}

	/**************************************************************************
	 * nuova ricerca
	 * 
	 **************************************************************************/
	public List<RvRicercaVacancyDTO> findDTOByPrimefaces(RvTestataDTO filter, List<DeMansione> listSelectedMansioni,
			List<DeContratto> listSelectedContratti, List<DeOrario> listSelectedOrari,
			List<DeAttivita> listSelectedAttivita, List<DeLingua> listSelectedLingue, List<DeTitolo> listSelectedTitoli,
			List<DePatente> listSelectedPatenti, List<DeContrattoSil> listSelectedContrattiSil,
			List<DeOrarioSil> listSelectedOrariSil, List<DePatenteSil> listSelectedPatentiSil, boolean flgCresco,
			Boolean filtraDataScadenza, Integer idUtente, int startResultsFrom, String flgContadino) {
		// Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() +
		// ".findDTOByPrimefaces (calling great SOLR)");
		String filtro = calcolaFiltroByPrimefaces(filter, listSelectedMansioni, listSelectedContratti,
				listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli, listSelectedPatenti,
				listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil, filtraDataScadenza);
		String flgCrescoString = flgCresco ? "Y" : "N";
		String flgContadinoString = flgContadino == null ? "" : "+AND+flg_agricoltura:Y";
		String url = ConstantsSingleton.getSolrUrl() + SOLR + filtro + "+AND+flg_pacchetto_cresco:" + flgCrescoString
				+ flgContadinoString + "&start=" + startResultsFrom + "&rows=" + filter.getRowsToLoad()
				+ "&indent=on&wt=xml&sort=data_modifica%20desc";
		log.info("SOLR URL:" + url);
		List<RvRicercaVacancyDTO> ret = new ArrayList<RvRicercaVacancyDTO>();
		try {
			Document document = Utils.documentSOLR(url);
			if (document != null) {
				// log.debug("VACANCY SOLR XML:" + Utils.domToString(document));
				NodeList nodeResult = document.getElementsByTagName("result");
				Node result = (Node) nodeResult.item(0);
				filter.setRowsTotal(Integer.parseInt(result.getAttributes().item(1).getNodeValue()));
				NodeList nodeList = document.getElementsByTagName("doc");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node doc = (Node) nodeList.item(i);
					Element elementDoc = (Element) doc.getChildNodes();
					NodeList nodeListStr = elementDoc.getElementsByTagName("str"),
							nodeListDouble = elementDoc.getElementsByTagName("double"),
							nodeListInt = elementDoc.getElementsByTagName("int"),
							nodeListDate = elementDoc.getElementsByTagName("date");
					// Aggiungo il contenuto delle stringhe
					Map<String, String> mapStr = new HashMap<String, String>();
					for (int y = 0; y < nodeListStr.getLength(); y++) {
						Node node = nodeListStr.item(y);
						String name = "", value = "";
						if (node.hasAttributes()) {
							name = nodeListStr.item(y).getAttributes().item(0).getNodeValue();
						} else {
							name = nodeListStr.item(y).getParentNode().getAttributes().item(0).getNodeValue();
						}
						if (nodeListStr.item(y).getFirstChild() != null) {
							value = nodeListStr.item(y).getFirstChild().getNodeValue();
						}
						mapStr.put(name, value);
					}
					Map<String, Date> mapDate = popolaMapDate(nodeListDate);
					Map<String, Double> mapDouble = popolaMapDouble(nodeListDouble);
					Map<String, Integer> mapInt = popolaMapInt(nodeListInt);
					ret.add(toDTO(mapStr, mapDouble, mapInt, mapDate, idUtente));
				}
			}

		} catch (Exception e) {
			log.error("Errore nel recupero delle vacancy da SOLR: " + e.getMessage());
		}
		// incremento del contatore dopo il caricamento
		filter.setRowsForLoad(filter.getRowsForLoad() + filter.getRowsToLoad());
		// recupero il numero effettivo dellle righe caricate
		filter.setRowsLoaded(ret.size());

		if (filter.getRowsLoaded() == 0) {
			filter.setRowsForLoad(filter.getRowsToLoad());
		}
		log.debug("Ho recuperato " + filter.getRowsLoaded() + " risultati");
		return ret;
	}

	private String calcolaFiltroByPrimefaces(RvTestataDTO filter, List<DeMansione> listSelectedMansioni,
			List<DeContratto> listSelectedContratti, List<DeOrario> listSelectedOrari,
			List<DeAttivita> listSelectedAttivita, List<DeLingua> listSelectedLingue, List<DeTitolo> listSelectedTitoli,
			List<DePatente> listSelectedPatenti, List<DeContrattoSil> listSelectedContrattiSil,
			List<DeOrarioSil> listSelectedOrariSil, List<DePatenteSil> listSelectedPatentiSil,
			Boolean filtraDataScadenza) {
		String cosa = filter.getCosa();
		String dove = filter.getDove();
		String filtro = "q=" + FILTRO_TUTTO;
		// Creo il filtro a partire dal Cosa e/o Dove
		if (cosa != null && cosa.trim().length() > 0) {

			// la query su solr deve essere del tipo q=cosa:(*azienda* +
			// *progetto*)
			String[] parole = cosa.trim().toLowerCase().split(" ");
			String cosaCercare = "";
			for (int i = 0; i < parole.length; i++) {
				if (i > 0) {
					cosaCercare = cosaCercare + " + ";
				}
				cosaCercare = cosaCercare + "*" + parole[i] + "*";
			}
			filtro = "q=cosa%3A(" + cosaCercare + ")*";
			if (filtro.equals("*"))
				filtro = "q=" + FILTRO_TUTTO;
		}
		if (dove != null) {

			String[] paroleDove = dove.trim().toLowerCase().split(" ");
			String doveCercare = "";
			for (int i = 0; i < paroleDove.length; i++) {
				if (i > 0) {
					doveCercare = doveCercare + " + ";
				}
				doveCercare = doveCercare + "*" + paroleDove[i] + "*";
			}

			filtro = dove.length() > 0 ? (filtro + "+AND+dove%3A(" + doveCercare + ")") : filtro;
		}

		filtro += calcolaFiltroCheckDeMansione(listSelectedMansioni, ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		filtro += calcolaFiltroCheckDeContratto(listSelectedContratti,
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO);
		filtro += calcolaFiltroCheckDeOrario(listSelectedOrari, ConstantsSingleton.RvRicercaVacancy.CODORARIO);
		filtro += calcolaFiltroCheckDeAttivita(listSelectedAttivita, ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		filtro += calcolaFiltroCheckDeLingua(listSelectedLingue, ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		filtro += calcolaFiltroCheckDeTitolo(listSelectedTitoli, ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		filtro += calcolaFiltroCheckDePatente(listSelectedPatenti, ConstantsSingleton.RvRicercaVacancy.CODPATENTE);
		filtro += calcolaFiltroCheckDeContrattoSil(listSelectedContrattiSil,
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL);
		filtro += calcolaFiltroCheckDeOrarioSil(listSelectedOrariSil, ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL);
		filtro += calcolaFiltroCheckDePatenteSil(listSelectedPatentiSil,
				ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL);

		/**
		 * Aggiungo filtro sulla data di scadenza Le vacancy sono valide se la data scadenza > oggi.
		 * 
		 */
		if (filtraDataScadenza != null) {
			// Prendo solo vacancy non scadute
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			/*
			 * UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA if
			 * (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) c.add(Calendar.DATE, +1);
			 */
			String dataPresente = dateFormat.format(c.getTime());
			c.add(Calendar.YEAR, 100);
			String dataFutura = dateFormat.format(c.getTime());
			c.add(Calendar.YEAR, -200);
			String dataPassata = dateFormat.format(c.getTime());

			if (filtraDataScadenza) {
				// Prendo solo vacancy non scadute
				filtro += "+AND+data_scadenza_pubblicazione:[" + dataPresente + "T00:00:00Z TO " + dataFutura
						+ "T00:00:00Z]";
			} else {
				// Prendo solo vacancy scadute
				filtro += "+AND+data_scadenza_pubblicazione:[" + dataPassata + "T00:00:00Z TO " + dataPresente
						+ "T00:00:00Z]";
			}
		}

		filtro = it.eng.myportal.utils.URL.escapeChrUrl(filtro);
		return it.eng.myportal.utils.URL.escape(filtro);
	}

	private String calcolaFiltroCheckDeMansione(List<DeMansione> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeMansione key = listGruppo.get(i);
				sb.append(key.getCodMansione() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeContratto(List<DeContratto> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeContratto key = listGruppo.get(i);
				sb.append(key.getCodContratto() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeOrario(List<DeOrario> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeOrario key = listGruppo.get(i);
				sb.append(key.getCodOrario() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeAttivita(List<DeAttivita> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeAttivita key = listGruppo.get(i);
				sb.append(key.getCodAteco() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeLingua(List<DeLingua> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeLingua key = listGruppo.get(i);
				sb.append(key.getCodLingua() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeTitolo(List<DeTitolo> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeTitolo key = listGruppo.get(i);
				sb.append(key.getCodTitolo() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDePatente(List<DePatente> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DePatente key = listGruppo.get(i);
				sb.append(key.getCodPatente() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDePatenteSil(List<DePatenteSil> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DePatenteSil key = listGruppo.get(i);
				sb.append(key.getCodPatenteSil() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeContrattoSil(List<DeContrattoSil> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeContrattoSil key = listGruppo.get(i);
				sb.append(key.getCodContrattoSil() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	private String calcolaFiltroCheckDeOrarioSil(List<DeOrarioSil> listGruppo, String param) {
		StringBuilder sb = new StringBuilder();
		String str = "";
		/* verifico la presenza dei filtri check == true */
		if (!listGruppo.isEmpty()) {
			sb.append("+AND+" + param + "%3A(");
			for (int i = 0; i < listGruppo.size(); i++) {
				DeOrarioSil key = listGruppo.get(i);
				sb.append(key.getCodOrarioSil() + " OR ");
			}
			str = sb.toString();
			str = str.substring(0, str.lastIndexOf("OR")) + ")";
		}
		return str;
	}

	public String getParamByPrimefaces(String nomeGruppo, RvTestataDTO filter, List<DeMansione> listSelectedMansioni,
			List<DeContratto> listSelectedContratti, List<DeOrario> listSelectedOrari,
			List<DeAttivita> listSelectedAttivita, List<DeLingua> listSelectedLingue, List<DeTitolo> listSelectedTitoli,
			List<DePatente> listSelectedPatenti, List<DeContrattoSil> listSelectedContrattiSil,
			List<DeOrarioSil> listSelectedOrariSil, List<DePatenteSil> listSelectedPatentiSil,
			Boolean filtraDataScadenza, boolean indentResult) {
		String filtro = calcolaFiltroByPrimefaces(filter, listSelectedMansioni, listSelectedContratti,
				listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli, listSelectedPatenti,
				listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil, filtraDataScadenza);

		if (indentResult)
			filtro += "&indent=on";

		filtro += "&wt=xml";

		if (nomeGruppo != null) {
			// filtro+= "&rows=9999&group=true&group.field=" + nomeGruppo;
			filtro += "&rows=0&facet=true&facet.mincount=1&facet.field=" + nomeGruppo;
		}
		return filtro;
	}

	/**
	 * recupera le occorrenze per gruppo
	 * 
	 * @param nomeGruppo
	 * @param filter
	 * @param rvGroupDTO
	 * @param mapGruppo
	 * @param mapCheck
	 * @return
	 */
	public Map<String, String> recuperaGruppoByPrimefaces(String nomeGruppo, RvTestataDTO filter,
			Object listSelectedObj) {
		Map<String, String> mapStr = new HashMap<String, String>();
		try {

			String url = ConstantsSingleton.getSolrUrl() + SOLR
					+ getParamGruppoByPrimefaces(nomeGruppo, filter, listSelectedObj);
			log.debug("SOLR GRUPPI URL:" + url);
			Document document = Utils.documentSOLR(url);
			XPath xpath = XPathFactory.newInstance().newXPath();
			if (document != null) {
				XPathExpression exprConteggi = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int");
				Object resultConteggi = exprConteggi.evaluate(document, XPathConstants.NODESET);
				NodeList nodesConteggi = (NodeList) resultConteggi;
				for (int i = 1; i <= nodesConteggi.getLength(); i++) {
					XPathExpression exprTot = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int[" + i + "]");
					Object totale = exprTot.evaluate(document, XPathConstants.STRING);
					String value = totale.toString();
					Integer totaleInt = new Integer(value);

					XPathExpression exprCodice = xpath.compile("/response/lst[2]/lst[2]/lst[1]/int[" + i + "]/@name");
					Object codice = exprCodice.evaluate(document, XPathConstants.STRING);
					if (totaleInt > 0) {
						mapStr.put(codice.toString(), value);
					}
				}
			}

		} catch (Exception e) {
			log.error("Errore nel recupero dei gruppiByPrimefaces delle vacancy da SOLR " + e);
		}
		return mapStr;

	}

	public String getParamGruppoByPrimefaces(String nomeGruppo, RvTestataDTO filter, Object listSelectedObj) {
		String filtro = calcolaGruppoByPrimefaces(nomeGruppo, filter, listSelectedObj) + "&indent=on&wt=xml";
		filtro += "&rows=0&facet=true&facet.mincount=1&facet.field=" + nomeGruppo;

		return filtro;
	}

	private String calcolaGruppoByPrimefaces(String nomeGruppo, RvTestataDTO filter, Object listSelectedObj) {
		String cosa = filter.getCosa();
		String dove = filter.getDove();
		String filtro = "q=" + FILTRO_TUTTO;
		// Creo il filtro a partire dal Cosa e/o Dove
		if (cosa != null && cosa.trim().length() > 0) {

			// la query su solr deve essere del tipo q=cosa:(*azienda* +
			// *progetto*)
			String[] parole = cosa.trim().toLowerCase().split(" ");
			String cosaCercare = "";
			for (int i = 0; i < parole.length; i++) {
				if (i > 0) {
					cosaCercare = cosaCercare + " + ";
				}
				cosaCercare = cosaCercare + "*" + parole[i] + "*";
			}
			filtro = "q=cosa%3A(" + cosaCercare + ")*";
			if (filtro.equals("*"))
				filtro = "q=" + FILTRO_TUTTO;
		}
		if (dove != null) {

			String[] paroleDove = dove.trim().toLowerCase().split(" ");
			String doveCercare = "";
			for (int i = 0; i < paroleDove.length; i++) {
				if (i > 0) {
					doveCercare = doveCercare + " + ";
				}
				doveCercare = doveCercare + "*" + paroleDove[i] + "*";
			}

			filtro = dove.length() > 0 ? (filtro + "+AND+dove%3A(" + doveCercare + ")") : filtro;
		}

		if (ConstantsSingleton.RvRicercaVacancy.CODMANSIONE.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeMansione((List<DeMansione>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeContratto((List<DeContratto>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODORARIO.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeOrario((List<DeOrario>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODORARIO);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODSETTORE.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeAttivita((List<DeAttivita>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODLINGUA.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeLingua((List<DeLingua>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeTitolo((List<DeTitolo>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODPATENTE.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDePatente((List<DePatente>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODPATENTE);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeContrattoSil((List<DeContrattoSil>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDeOrarioSil((List<DeOrarioSil>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL);
		} else if (ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL.equalsIgnoreCase(nomeGruppo)) {
			filtro += calcolaFiltroCheckDePatenteSil((List<DePatenteSil>) listSelectedObj,
					ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL);
		}

		/**
		 * Aggiungo filtro sulla data di scadenza Le vacancy sono valide se la data scadenza > oggi.
		 * 
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		/*
		 * UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA if
		 * (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) c.add(Calendar.DATE, +1);
		 */
		String dataInizio = dateFormat.format(c.getTime());
		c.add(Calendar.YEAR, 100);
		String dataFine = dateFormat.format(c.getTime());
		filtro += "+AND+data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO " + dataFine + "T00:00:00Z]";

		filtro = it.eng.myportal.utils.URL.escapeChrUrl(filtro);
		return it.eng.myportal.utils.URL.escape(filtro);
	}

	public Long getVacancydaRedazioneCount(RedazioneSearchParams param) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);
		c.select(cb.countDistinct(f));

		List<Predicate> whereConditions = vacancyWhereCondition(cb, f, param);
		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<Long> typedQuery = entityManager.createQuery(c);
		Long result = typedQuery.getSingleResult();
		return result;
	}

	public List<VacancyDaRedazioneDTO> searchVacancyDaRedazione(RedazioneSearchParams param) {

		List<VacancyDaRedazioneDTO> vacancyDaRedazioneDto = new ArrayList<VacancyDaRedazioneDTO>();

		List<VaDatiVacancy> vacancies = getVacancyByFilters(param);

		for (VaDatiVacancy current : vacancies) {
			VacancyDaRedazioneDTO model = new VacancyDaRedazioneDTO();
			model.setIdVaDatiVacancy(current.getIdVaDatiVacancy());
			model.setRiferimento(current.getNumRichiesta() + " / " + current.getNumAnno() + " - "
					+ current.getDeProvenienzaVacancy().getDescrizione());
			model.setDtPubblicazione(current.getDtPubblicazione());
			String nomeAzienda = searchNomeAzienda(current);
			model.setNomeAzienda(nomeAzienda);
			model.setCodStatoVacancyEnum(current.getCodStatoVacancyEnum());

			vacancyDaRedazioneDto.add(model);
		}

		// qua ci va ordinamento pezza
		if (VacancyRedazioneSortEnum.AZIENDA.equals(param.getSortOrder())) {
			// Collections.sort(vacancies);
		}

		return vacancyDaRedazioneDto;
	}

	public String searchNomeAzienda(VaDatiVacancy vacancy) {
		DeProvenienza deProvenienza = vacancy.getDeProvenienzaVacancy();
		String nomeAzienda = "";
		if (deProvenienza != null) {
			if (ConstantsSingleton.DeProvenienza.COD_MYPORTAL.equals(deProvenienza.getCodProvenienza())) { // Vancancy
																											// da
																											// MYPORTAL
				AziendaInfo aziendaInfo = aziendaInfoHome
						.findByIdPfPrincipal(vacancy.getPfPrincipal().getIdPfPrincipal());
				nomeAzienda = aziendaInfo.getRagioneSociale();
			} else if (deProvenienza.getCodProvenienza().startsWith("SIL")) { // Vacancy da SIL
				if (vacancy.getPfPrincipalPalese() != null) { // Vacancy pubblica
					AziendaInfo aziendaInfo = aziendaInfoHome
							.findByIdPfPrincipal(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
					nomeAzienda = aziendaInfo.getRagioneSociale();
				} else { // Vacancy anonima
					nomeAzienda = vacancy.getRagioneSociale();
				}
			} else {
				log.warn("ATTENZIONE: provenienza non prevista!");
			}
		}
		return nomeAzienda;
	}
	
	public String searchCodiceFiscale(VaDatiVacancy vacancy) {
		DeProvenienza deProvenienza = vacancy.getDeProvenienzaVacancy();
		String codiceFiscale = "";
		if (deProvenienza != null) {
			if (ConstantsSingleton.DeProvenienza.COD_MYPORTAL.equals(deProvenienza.getCodProvenienza())) { // Vancancy
																											// da
																											// MYPORTAL
				AziendaInfo aziendaInfo = aziendaInfoHome
						.findByIdPfPrincipal(vacancy.getPfPrincipal().getIdPfPrincipal());
				codiceFiscale = aziendaInfo.getCodiceFiscale();
			} else if (deProvenienza.getCodProvenienza().startsWith("SIL")) { // Vacancy da SIL
				if (vacancy.getPfPrincipalPalese() != null) { // Vacancy pubblica
					AziendaInfo aziendaInfo = aziendaInfoHome
							.findByIdPfPrincipal(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
					codiceFiscale = aziendaInfo.getCodiceFiscale();
				} else { // Vacancy anonima
					codiceFiscale = vacancy.getCodiceFiscale();
				}
			} else {
				log.warn("ATTENZIONE: provenienza non prevista!");
			}
		}
		return codiceFiscale;
	}

	private List<Predicate> vacancyWhereCondition(CriteriaBuilder cb, 
													Root<VaDatiVacancy> root,
													RedazioneSearchParams param) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Integer numAnno = param.getNumAnno();
		if (numAnno != null) {
			whereConditions.add(cb.equal(root.get(VaDatiVacancy_.numAnno), numAnno));
		}
		Integer numRichiesta = param.getNumRichiesta();
		if (numRichiesta != null) {
			whereConditions.add(cb.equal(root.get(VaDatiVacancy_.numRichiesta), numRichiesta));
		}
		if (param.getDeProvenienzas() != null) {
			DeProvenienza codProvenienza = param.getDeProvenienzas();
			whereConditions.add(cb.equal(root.get(VaDatiVacancy_.deProvenienzaVacancy), codProvenienza));
		}
		String attivitaPrincipale = param.getTitoloAnnuncio();
		if (StringUtils.isNotBlank(attivitaPrincipale)) {
			attivitaPrincipale = attivitaPrincipale.trim().toUpperCase();
			whereConditions.add(
					cb.like(cb.upper(root.get(VaDatiVacancy_.attivitaPrincipale)), "%" + attivitaPrincipale + "%"));
		}
		String operatoreCPI = param.getOperatoreCPI();
		if (StringUtils.isNotBlank(operatoreCPI)) {
			operatoreCPI = operatoreCPI.trim().toUpperCase();
			whereConditions.add(cb.or(
					cb.like(cb.upper(root.get(VaDatiVacancy_.cognomeOperatore)), "%" + operatoreCPI + "%"),
					cb.like(cb.upper(root.get(VaDatiVacancy_.nomeOperatore)), "%" + operatoreCPI + "%")));
		}
		CodStatoVacancyEnum statoVacancy = param.getStatoVacancy();
		if (statoVacancy != null && !statoVacancy.getDescrizione().isEmpty()) {
			if (CodStatoVacancyEnum.ARC.equals(statoVacancy)) {
				whereConditions.add(cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.ARC));
			} else if (CodStatoVacancyEnum.PUB.equals(statoVacancy)) {
				whereConditions.add(cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB));
			}
		} else {
			whereConditions.add(cb.or(cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.ARC),
					cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB)));
		}
		String nomeAzienda = param.getAziendaRichiedente();
		if (StringUtils.isNotBlank(nomeAzienda)) {
			nomeAzienda = nomeAzienda.trim().toUpperCase();
			
			Join<VaDatiVacancy, PfPrincipal> azPf = root.join(VaDatiVacancy_.pfPrincipal, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfo = azPf.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			
			Join<VaDatiVacancy, PfPrincipal> azPfPalese = root.join(VaDatiVacancy_.pfPrincipalPalese, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfoPalese = azPfPalese.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			
			whereConditions.add(cb.or(
					cb.like(cb.upper(root.get(VaDatiVacancy_.ragioneSociale)), "%" + nomeAzienda + "%"),
					cb.like(cb.upper(root.get(VaDatiVacancy_.codiceFiscale)), "%" + nomeAzienda + "%"),
					cb.like(cb.upper(azInfo.get(AziendaInfo_.codiceFiscale)), "%" + nomeAzienda + "%"),
					cb.like(cb.upper(azInfo.get(AziendaInfo_.ragioneSociale)), "%" + nomeAzienda + "%"),
					cb.like(cb.upper(azInfoPalese.get(AziendaInfo_.codiceFiscale)), "%" + nomeAzienda + "%"),
					cb.like(cb.upper(azInfoPalese.get(AziendaInfo_.ragioneSociale)), "%" + nomeAzienda + "%")));
		}
		if (param.getEvasioneRich() != null) {
			DeEvasioneRich codEvasione = param.getEvasioneRich();
			whereConditions.add(cb.equal(root.get(VaDatiVacancy_.deEvasioneRich), codEvasione));
		}
		if (param.getComuneSedeAziendale() != null) {
			Join<VaDatiVacancy, PfPrincipal> azPf = root.join(VaDatiVacancy_.pfPrincipal, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfo = azPf.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			
			Join<VaDatiVacancy, PfPrincipal> azPfPalese = root.join(VaDatiVacancy_.pfPrincipalPalese, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfoPalese = azPfPalese.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			
			whereConditions.add(
					cb.or(cb.equal(azInfo.get(AziendaInfo_.deComuneSede), param.getComuneSedeAziendale()),
							cb.equal(azInfoPalese.get(AziendaInfo_.deComuneSede), param.getComuneSedeAziendale())));
		}
		if (param.getComuneSedeLavorativa() != null) {
			DeComune cod_com_lavoro = param.getComuneSedeLavorativa();
			whereConditions.add(cb.equal(root.get(VaDatiVacancy_.deComune), cod_com_lavoro));
		}
		if(param.getValidaScaduta() != null){
			if (param.getValidaScaduta().equals("scadute")){
				whereConditions.add(cb.lessThan(root.get(VaDatiVacancy_.dtScadenzaPubblicazione), new java.util.Date()));
			}else {
				whereConditions.add(cb.greaterThanOrEqualTo(root.get(VaDatiVacancy_.dtScadenzaPubblicazione), new java.util.Date()));
			}
		}

		if (param.getProvinciaLuogoLavoro() != null) {
			Join<VaDatiVacancy, DeComune> deComuneJoin = root.join(VaDatiVacancy_.deComune);
			DeProvincia codProvLavoro = param.getProvinciaLuogoLavoro();
			whereConditions.add(cb.equal(deComuneJoin.get(DeComune_.deProvincia), codProvLavoro));
		}
		
		if (param.getProvinciaSedeAzienda() != null) {
			Join<VaDatiVacancy, PfPrincipal> azPf = root.join(VaDatiVacancy_.pfPrincipal, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfo = azPf.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			Join<AziendaInfo, DeComune> azInfoDeComuneJoin = azInfo.join(AziendaInfo_.deComuneSede, JoinType.LEFT);

			Join<VaDatiVacancy, PfPrincipal> azPfPalese = root.join(VaDatiVacancy_.pfPrincipalPalese, JoinType.LEFT);
			Join<PfPrincipal, AziendaInfo> azInfoPalese = azPfPalese.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			Join<AziendaInfo, DeComune> azInfoPaleseDeComuneJoin = azInfoPalese.join(AziendaInfo_.deComuneSede, JoinType.LEFT);
			
			//Join<VaDatiVacancy, DeComune> deComuneLavoroJoin = root.join(VaDatiVacancy_.deComune);
			whereConditions.add(cb.or(cb.equal(azInfoDeComuneJoin.get(DeComune_.deProvincia), param.getProvinciaSedeAzienda()),
					cb.equal(azInfoPaleseDeComuneJoin.get(DeComune_.deProvincia), param.getProvinciaSedeAzienda())));
		}
		if(param.getDataPubblicazioneDa()!=null) {
			whereConditions.add(cb.greaterThanOrEqualTo(root.get(VaDatiVacancy_.dtPubblicazione), param.getDataPubblicazioneDa()));
		}
		if(param.getDataPubblicazioneA()!=null) {
			whereConditions.add(cb.lessThanOrEqualTo(root.get(VaDatiVacancy_.dtPubblicazione), param.getDataPubblicazioneA()));
		}
		
		if(param.isFlgVacancyNotRer()) {
			Join<VaDatiVacancy, DeComune> aziendaComuneJoin = root.join(VaDatiVacancy_.deComune);
			Join<DeComune, DeProvincia> comuneProvJoin = aziendaComuneJoin.join(DeComune_.deProvincia);
			Join<DeProvincia, DeRegione> provRegioneJoin = comuneProvJoin.join(DeProvincia_.deRegione);	
			whereConditions.add(cb.notEqual(provRegioneJoin.get(DeRegione_.codRegione), "8"));
		}
		
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.flagIdo), Boolean.TRUE));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.flagEliminata), Boolean.FALSE));
		return whereConditions;
	}

	public List<VaDatiVacancy> getVacancyByFilters(RedazioneSearchParams param) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> c = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);

		Join<VaDatiVacancy, PfPrincipal> azPf = f.join(VaDatiVacancy_.pfPrincipal, JoinType.LEFT);
		Join<PfPrincipal, AziendaInfo> azInfo = azPf.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);

		Join<VaDatiVacancy, PfPrincipal> azPfPalese = f.join(VaDatiVacancy_.pfPrincipalPalese, JoinType.LEFT);
		Join<PfPrincipal, AziendaInfo> azInfoPalese = azPfPalese.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);

		List<Predicate> whereConditions = vacancyWhereCondition(cb, f, param);

		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		if (param.getSortOrder() != null) {

			switch (param.getSortOrder()) {
			case AZIENDA:
				if (param.isAscending()) {
					c.orderBy(cb.asc(f.get(VaDatiVacancy_.ragioneSociale)));
					c.orderBy(cb.asc(azInfo.get(AziendaInfo_.ragioneSociale)));
					c.orderBy(cb.asc(azInfoPalese.get(AziendaInfo_.ragioneSociale)));
				} else {
					c.orderBy(cb.desc(f.get(VaDatiVacancy_.ragioneSociale)));
					c.orderBy(cb.desc(azInfo.get(AziendaInfo_.ragioneSociale)));
					c.orderBy(cb.desc(azInfoPalese.get(AziendaInfo_.ragioneSociale)));
				}
				break;
			case DATA_PUBBLICAZIONE:
				if (param.isAscending())
					c.orderBy(cb.asc(f.get(VaDatiVacancy_.dtPubblicazione)));
				else
					c.orderBy(cb.desc(f.get(VaDatiVacancy_.dtPubblicazione)));
				break;
			case STATO_ANNUNCIO:
				if (param.isAscending())
					c.orderBy(cb.asc(f.get(VaDatiVacancy_.codStatoVacancyEnum)));
				else
					c.orderBy(cb.desc(f.get(VaDatiVacancy_.codStatoVacancyEnum)));
				break;
			case RIFERIMENTO:
				if (param.isAscending()) {
					c.orderBy(cb.asc(f.get(VaDatiVacancy_.numRichiesta)), cb.asc(f.get(VaDatiVacancy_.numAnno)),
							cb.asc(f.get(VaDatiVacancy_.deProvenienzaVacancy)));
				} else {
					c.orderBy(cb.desc(f.get(VaDatiVacancy_.numRichiesta)), cb.desc(f.get(VaDatiVacancy_.numAnno)),
							cb.desc(f.get(VaDatiVacancy_.deProvenienzaVacancy)));
				}
				break;
			default:
				break;
			}

		} else {
			c.orderBy(cb.desc(f.get(VaDatiVacancy_.dtPubblicazione)));
		}

		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(c);

		if (param.getStartFrom() > 0) {
			typedQuery.setFirstResult(param.getStartFrom());
		}
		if (param.getChunkSize() > 0) {
			typedQuery.setMaxResults(param.getChunkSize());
		}

		List<VaDatiVacancy> lista = typedQuery.getResultList();
		return lista;
	}
}
