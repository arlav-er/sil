package it.eng.myportal.entity.home;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.RicercaUtenteDTO;
import it.eng.myportal.dtos.RvGroupDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvDatiPersonali_;
import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.RvTestata;
import it.eng.myportal.entity.RvTestata_;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.UtenteInfo_;
import it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione;
import it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione_;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeGenere;
import it.eng.myportal.entity.decodifiche.DeMotivoPermesso;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.enums.StatoAtipicoEnum;
import it.eng.myportal.enums.TipoAccount;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.enums.TipoRegistrazione;
import it.eng.myportal.enums.TipoRicercaUtente;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.ServiziLavoratoreException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.servizilavoratore.ServiziLavoratore;
import it.eng.sil.coop.webservices.servizilavoratore.ServiziLavoratoreProxy;

/**
 * Home object for domain model class UtenteInfo.
 * 
 * @see it.eng.myportal.entity.UtenteInfo
 * @author Rodi A.
 */
@Stateless
public class UtenteInfoHome extends AbstractUpdatableHome<UtenteInfo, UtenteInfoDTO> {
	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	private DeTitoloSoggiornoHome deTitoloSoggiornoHome;

	@EJB
	private DeGenereHome deGenereHome;

	@EJB
	private DeMotivoPermessoHome deMotivoPermessoHome;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private CvLetteraAccHome cvLetteraAccHome;

	@EJB
	private RvTestataHome rvTestataHome;

	@EJB
	private PtPortletHome ptPortletHome;

	@EJB
	private PtScrivaniaHome ptScrivaniaHome;

	@EJB
	private PfIdentityProviderHome pfIdentityProviderHome;

	@EJB
	private AcCandidaturaHome acCandidaturaHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private RvRicercaVacancyHome rvRicercaVacancyHome;

	public UtenteInfo findById(Integer id) {
		return findById(UtenteInfo.class, id);
	}

	public UtenteCompletoDTO findDTOCompletoById(Integer idPfPrincipal) {
		return toDTOCompleto(findById(idPfPrincipal));
	}

	public UtenteDTO findMiniDTOById(Integer idPfPrincipal) {
		return toMiniDTO(findById(idPfPrincipal));
	}

	/**
	 * Cerca un utente a partire dal suo Username.
	 * 
	 * @param name
	 *            username da ricercare
	 * @return l'utente con lo username specificato, null altrimenti
	 */
	public UtenteInfoDTO findDTOByUsername(String name) {
		return toDTO(findByUsername(name));
	}

	public UtenteDTO findMiniDTOByUsername(String name) {
		return toMiniDTO(findByUsername(name));
	}

	public UtenteCompletoDTO findDTOCompletoByUsername(String username) {
		return toDTOCompleto(findByUsername(username));
	}

	/**
	 * Cerca un utente a partire dall'indirizzo email.
	 * 
	 * @param email
	 *            email da ricercare
	 * @return l'utente con l'indirizzo email specificato, null altrimenti
	 */
	public UtenteDTO findMiniDTOByEmail(String email) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UtenteInfo> query = qb.createQuery(UtenteInfo.class);
		Root<UtenteInfo> utente = query.from(UtenteInfo.class);
		Join<UtenteInfo, PfPrincipal> pfPrincipal = utente.join(UtenteInfo_.pfPrincipal);

		query.where(qb.equal(pfPrincipal.get(PfPrincipal_.email), email));

		TypedQuery<UtenteInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);

		List<UtenteDTO> ret = findMiniDTOByQuery(q);
		if (ret.isEmpty())
			return null;
		return ret.get(0);
	}

	/**
	 * @param query
	 *            la query che deve essere eseguita.
	 * @return List<DTO> lista dei risultati della query trasformati in DTO
	 * 
	 */
	protected List<UtenteDTO> findMiniDTOByQuery(TypedQuery<UtenteInfo> query) {
		List<UtenteInfo> result = null;

		result = query.getResultList();

		final List<UtenteDTO> ret = new ArrayList<UtenteDTO>();
		for (UtenteInfo entity : result) {
			ret.add(toMiniDTO(entity));
		}

		return ret;
	}

	/**
	 * Metodo che restituisce la lista dei CV appartenenti ad un utente.
	 * 
	 * @param idUtente
	 *            id dell'utente del quale si vuole la lista dei CV.
	 * @return la lista dei CV.
	 */
	public List<CvDatiPersonaliDTO> getAllCurricula(int idPfPrincipal) {
		PfPrincipal u = pfPrincipalHome.findById(idPfPrincipal);
		List<CvDatiPersonaliDTO> ret = new ArrayList<CvDatiPersonaliDTO>();
		Set<CvDatiPersonali> datis = u.getCvDatiPersonalis();
		for (CvDatiPersonali cvDatiPersonali : datis) {
			CvDatiPersonaliDTO dto = cvDatiPersonaliHome.toDTO(cvDatiPersonali);
			ret.add(dto);
		}
		return ret;
	}

	/**
	 * Metodo che restituisce la lista dei CV live appartenenti ad un utente.
	 * 
	 * @param idUtente
	 *            id dell'utente del quale si vuole la lista dei CV live.
	 * @return la lista dei CV.
	 */
	public List<CvDatiPersonaliDTO> getAllCurriculaLive(Integer idPfPrincipal) {
		// Logica spostata in CvDatiPersonaliHome
		return cvDatiPersonaliHome.findAllCurriculaLiveDTO(idPfPrincipal);
	}

	public List<CvDatiPersonaliDTO> getAllCurriculaIntermediati(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();

		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvIntermediati", CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(cvDatiPersonaliHome.toDTO(cvDatiPersonali));
		}
		return result;
	}

	public List<CvDatiPersonaliDTO> getAllCurriculaLiveNoScad(Integer idPfPrincipal) {
		// Logica spostata in CvDatiPersonaliHome
		return cvDatiPersonaliHome.getAllCurriculaLiveNoScadDTO(idPfPrincipal);
	}

	public List<CvDatiPersonaliDTO> getAllCurriculaLiveNoScadFlagIdo(Integer idPfPrincipal) {
		// Logica spostata in CvDatiPersonaliHome
		return cvDatiPersonaliHome.getAllCurriculaLiveNoScadDTOFlagIdo(idPfPrincipal);
	}

	
	public List<CvDatiPersonaliDTO> getAllCurriculaIntermediatiNoScad(Integer idPfPrincipal) {
		// Logica spostata in CvDatiPersonaliHome
		return cvDatiPersonaliHome.getAllCurriculaIntermediatiNoScadDTO(idPfPrincipal);
	}

	/**
	 * Restituisce tutti i CV intermediati appartenenti all'utente, esclusi quelli con provenienza MINISTERO e scaduti.
	 * Il metodo viene utilizzato nella visualizzazione dei CV nella homepage dell'utente.
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonaliDTO> getAllCurriculaIntermediatiNoMinScaduti(Integer idPfPrincipal) {
		// Logica spostata in CvDatiPersonaliHome
		return cvDatiPersonaliHome.findAllCurriculaIntermediatiNoMinScadutiDTO(idPfPrincipal);
	}
	
	/**
	 * Restituisce il numero di CV sincronizzati o in fase di sync con CL appartenenti all'utente.
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public Long getNumCurriculaSyncWithCl(Integer idPfPrincipal) {

		TypedQuery<Long> query = entityManager.createNamedQuery("countCvSyncWithCl", Long.class);

		query = query.setParameter("idPrincipal", idPfPrincipal);
		Long result = query.getSingleResult();

		return result;
	}

	/**
	 * Servizio che permette di ottenere la lista dei CV appartenenti ad un utente sotto forma di lista di SelectItem
	 * per una combo.
	 * 
	 * @param idUtente
	 *            id dell'utente del quale si vuole la lista dei CV.
	 * @param onlyClicLavoro
	 *            restituisce solo i curriculum abilitati all'invio verso clicLavoro
	 * @return la lista dei CV.
	 */
	public List<SelectItem> getAllCurriculaAsSelectItem(Integer idPfPrincipal, boolean onlyClicLavoro) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = qb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> cvDatiPersonalis = query.from(CvDatiPersonali.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		/* no CV inviati a candidature */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagInviato), Boolean.FALSE));
		/* no CV in attesa di eliminazione */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE));
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) { 
		  whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagIdo), Boolean.TRUE));
	    }
		// se è solo clic lavoro l'ambito di diffusione deve essere diverso da
		// regionale
		if (onlyClicLavoro) {
			Join<CvDatiPersonali, DeAmbitoDiffusione> ambitoDiffusione = cvDatiPersonalis
					.join(CvDatiPersonali_.deAmbitoDiffusione);
			whereConditions.add(qb.not(qb.equal(ambitoDiffusione.get(DeAmbitoDiffusione_.codAmbitoDiffusione),
					it.eng.myportal.utils.ConstantsSingleton.DeAmbitoDiffusione.REGIONALE)));
		}
		/*
		 * prendo tutti i cv dell'utente, anche quelli intermediati, il cittadino deve essere indicato come pfPrincipal
		 * o come pfPrincipalPalese in CvDatiPersonali
		 */
		Predicate pfPrincipalPredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipal), idPfPrincipal);
		Predicate pfPrincipalPalesePredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipalPalese),
				idPfPrincipal);
		Predicate proprietarioPredicate = qb.or(pfPrincipalPredicate, pfPrincipalPalesePredicate);
		whereConditions.add(proprietarioPredicate);

		/*
		 * prendo tutti i CV per cui dt_scadenza non sia minore della data odierna
		 */
		Predicate dtScadenzaPredicateIsNull = qb.isNull(cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza));
		Predicate dtScadenzaPredicateGreaterThan = qb.greaterThanOrEqualTo(
				cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza), qb.currentDate());
		Predicate dtScadenzaPredicate = qb.or(dtScadenzaPredicateIsNull, dtScadenzaPredicateGreaterThan);
		whereConditions.add(dtScadenzaPredicate);

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<CvDatiPersonali> q = entityManager.createQuery(query);

		List<CvDatiPersonali> datis = q.getResultList();

		List<SelectItem> ret = new ArrayList<SelectItem>();
		for (CvDatiPersonali cvDatiPersonali : datis) {
			SelectItem dto = new SelectItem(cvDatiPersonali.getIdCvDatiPersonali(), cvDatiPersonali.getDescrizione(),
					cvDatiPersonali.getDescrizione());
			ret.add(dto);
		}
		return ret;
	}

	public List<SelectItem> getAllCurriculaAsSelectItemIdoRER(Integer idPfPrincipal, boolean onlyClicLavoro) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = qb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> cvDatiPersonalis = query.from(CvDatiPersonali.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		/* no CV inviati a candidature */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagInviato), Boolean.FALSE));
		/* no CV in attesa di eliminazione */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE));
		
		// Nuovo controllo che mi consente di inviare candidature solo di CV con flgido = true
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagIdo), Boolean.TRUE));

		// se è solo clic lavoro l'ambito di diffusione deve essere diverso da
		// regionale
		if (onlyClicLavoro) {
			Join<CvDatiPersonali, DeAmbitoDiffusione> ambitoDiffusione = cvDatiPersonalis
					.join(CvDatiPersonali_.deAmbitoDiffusione);
			whereConditions.add(qb.not(qb.equal(ambitoDiffusione.get(DeAmbitoDiffusione_.codAmbitoDiffusione),
					it.eng.myportal.utils.ConstantsSingleton.DeAmbitoDiffusione.REGIONALE)));
		}
		/*
		 * prendo tutti i cv dell'utente, anche quelli intermediati, il cittadino deve essere indicato come pfPrincipal
		 * o come pfPrincipalPalese in CvDatiPersonali
		 */
		Predicate pfPrincipalPredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipal), idPfPrincipal);
		Predicate pfPrincipalPalesePredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipalPalese),
				idPfPrincipal);
		Predicate proprietarioPredicate = qb.or(pfPrincipalPredicate, pfPrincipalPalesePredicate);
		whereConditions.add(proprietarioPredicate);

		/*
		 * prendo tutti i CV per cui dt_scadenza non sia minore della data odierna
		 */
		Predicate dtScadenzaPredicateIsNull = qb.isNull(cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza));
		Predicate dtScadenzaPredicateGreaterThan = qb.greaterThanOrEqualTo(
				cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza), qb.currentDate());
		Predicate dtScadenzaPredicate = qb.or(dtScadenzaPredicateIsNull, dtScadenzaPredicateGreaterThan);
		whereConditions.add(dtScadenzaPredicate);

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<CvDatiPersonali> q = entityManager.createQuery(query);

		List<CvDatiPersonali> datis = q.getResultList();

		List<SelectItem> ret = new ArrayList<SelectItem>();
		for (CvDatiPersonali cvDatiPersonali : datis) {
			SelectItem dto = new SelectItem(cvDatiPersonali.getIdCvDatiPersonali(), cvDatiPersonali.getDescrizione(),
					cvDatiPersonali.getDescrizione());
			ret.add(dto);
		}
		return ret;
	}

	
	/**
	 * Metodo che restituisce la lista delle lettere di accompagnamento appartenenti ad un utente.
	 * 
	 * @param id
	 *            Integer
	 * @param copie
	 *            se impostato a true restituisce anche le lettere 'copia'
	 * @return List<CvLetteraAccDTO>
	 */
	public List<CvLetteraAccDTO> getAllLettereAcc(Integer idPfPrincipal) {
		PfPrincipal u = pfPrincipalHome.findById(idPfPrincipal);

		List<CvLetteraAccDTO> ret = new ArrayList<CvLetteraAccDTO>();
		Set<CvLetteraAcc> datis = u.getCvLetteraAccs();
		for (CvLetteraAcc cvDatiPersonali : datis) {
			CvLetteraAccDTO dto = cvLetteraAccHome.toDTO(cvDatiPersonali);
			ret.add(dto);
		}
		return ret;
	}

	/**
	 * Metodo che restituisce la lista delle lettere di accompagnamento live appartenenti ad un utente.
	 * 
	 * @param id
	 *            Integer
	 * @param copie
	 *            se impostato a true restituisce anche le lettere 'copia'
	 * @return List<CvLetteraAccDTO>
	 */
	public List<CvLetteraAccDTO> getAllLiveLettereAcc(Integer idPfPrincipal) {
		PfPrincipal u = pfPrincipalHome.findById(idPfPrincipal);

		List<CvLetteraAccDTO> ret = new ArrayList<CvLetteraAccDTO>();
		Set<CvLetteraAcc> datis = u.getCvLetteraAccs();
		for (CvLetteraAcc cvLetteraAcc : datis) {
			CvLetteraAccDTO dto = cvLetteraAccHome.toDTO(cvLetteraAcc);
			if (!dto.getFlagInviato()) {
				ret.add(dto);
			}
		}
		return ret;
	}

	/**
	 * Restituisce la lista di tutte le lettere di accompagnamento scritte da un utente sotto forma di SleectItem
	 * 
	 * @param id
	 * @param copie
	 *            se impostato a true restituisce anche le lettere 'copia'
	 * @return List<CvLetteraAccDTO>
	 */
	public List<SelectItem> getAllLettereAccAsSelectItem(Integer idPfPrincipal, Boolean copie) {
		PfPrincipal u = pfPrincipalHome.findById(idPfPrincipal);

		List<SelectItem> ret = new ArrayList<SelectItem>();
		Set<CvLetteraAcc> datis = u.getCvLetteraAccs();

		ret.add(0, new SelectItem(null, ""));

		for (CvLetteraAcc cvLetteraAcc : datis) {
			if (!copie && cvLetteraAcc.getFlagInviato())
				continue;
			SelectItem dto = new SelectItem(cvLetteraAcc.getIdCvLetteraAcc(), cvLetteraAcc.getNome(),
					cvLetteraAcc.getNome());
			ret.add(dto);
		}
		return ret;
	}

	public List<CvLetteraAcc> getAllLettereAccNoCopie(Integer idPfPrincipal) {
		PfPrincipal u = pfPrincipalHome.findById(idPfPrincipal);

		List<CvLetteraAcc> ret = new ArrayList<CvLetteraAcc>();
		Set<CvLetteraAcc> datis = u.getCvLetteraAccs();

		for (CvLetteraAcc cvLetteraAcc : datis) {
			if (cvLetteraAcc.getFlagInviato()) {
				continue;
			}
			ret.add(cvLetteraAcc);
		}
		return ret;
	}

	public List<CvDatiPersonali> getAllCurriculaNoCopie(Integer idPfPrincipal) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = qb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> cvDatiPersonalis = query.from(CvDatiPersonali.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		/* no CV inviati a candidature */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagInviato), Boolean.FALSE));
		/* no CV in attesa di eliminazione */
		whereConditions.add(qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE));

		/*
		 * prendo tutti i cv dell'utente, anche quelli intermediati, il cittadino deve essere indicato come pfPrincipal
		 * o come pfPrincipalPalese in CvDatiPersonali
		 */
		Predicate pfPrincipalPredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipal), idPfPrincipal);
		Predicate pfPrincipalPalesePredicate = qb.equal(cvDatiPersonalis.get(CvDatiPersonali_.pfPrincipalPalese),
				idPfPrincipal);
		Predicate proprietarioPredicate = qb.or(pfPrincipalPredicate, pfPrincipalPalesePredicate);
		whereConditions.add(proprietarioPredicate);

		/*
		 * prendo tutti i CV per cui dt_scadenza non sia minore della data odierna
		 */
		Predicate dtScadenzaPredicateIsNull = qb.isNull(cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza));
		Predicate dtScadenzaPredicateGreaterThan = qb.greaterThanOrEqualTo(
				cvDatiPersonalis.get(CvDatiPersonali_.dtScadenza), qb.currentDate());
		Predicate dtScadenzaPredicate = qb.or(dtScadenzaPredicateIsNull, dtScadenzaPredicateGreaterThan);
		whereConditions.add(dtScadenzaPredicate);

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<CvDatiPersonali> q = entityManager.createQuery(query);

		List<CvDatiPersonali> datis = q.getResultList();

		return datis;
	}

	public UtenteDTO toMiniDTO(UtenteInfo entity) {
		if (entity == null) {
			return null;
		}
		UtenteDTO utenteDTO = new UtenteDTO();
		PfPrincipalDTO pfPrincipalDTO;

		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		pfPrincipalDTO = pfPrincipalHome.toDTO(pfPrincipal);

		utenteDTO.setId(entity.getIdPfPrincipal());
		utenteDTO.setPfPrincipalDTO(pfPrincipalDTO);

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
		    utenteDTO.setCurricula(getAllCurriculaLiveNoScadFlagIdo(pfPrincipal.getIdPfPrincipal()));
		}else {
			utenteDTO.setCurricula(getAllCurriculaLiveNoScad(pfPrincipal.getIdPfPrincipal()));
		}	
		
		
		utenteDTO.setCurriculaPalesi(getAllCurriculaIntermediatiNoScad(pfPrincipal.getIdPfPrincipal()));

		utenteDTO.setLettere(getAllLiveLettereAcc(pfPrincipal.getIdPfPrincipal()));

		utenteDTO.setAbilitato(entity.getPfPrincipal().getFlagAbilitato());
		utenteDTO.setAbilitatoServizi(entity.getPfPrincipal().getFlagAbilitatoServizi());

		if (entity.getDeProvincia() != null) {
			utenteDTO.setDescProvincia(entity.getDeProvincia().getDenominazione());
		}

		return utenteDTO;
	}

	public UtenteCompletoDTO toDTOCompleto(UtenteInfo entity) {
		if (entity == null) {
			return null;
		}

		UtenteCompletoDTO dto = new UtenteCompletoDTO(toMiniDTO(entity), toDTO(entity));
		String username = entity.getPfPrincipal().getUsername();
		dto.setUsername(username);
		dto.setAbilitatoServizi(entity.getPfPrincipal().getFlagAbilitatoServizi());
		dto.setAbilitato(entity.getPfPrincipal().getFlagAbilitato());

		/* compongo l'indirizzo completo nella forma: via - comune, targa */
		String indirizzoVia = entity.getIndirizzoDomicilio();
		String indirizzoComune = null;
		String IndirizzoTarga = null;
		if (entity.getDeComuneDomicilio() != null) {
			indirizzoComune = entity.getDeComuneDomicilio().getDenominazione();
			IndirizzoTarga = entity.getDeComuneDomicilio().getDeProvincia().getTarga();
		}
		StringBuffer indirizzoCompletoBuffer = new StringBuffer();
		if (indirizzoVia != null && !indirizzoVia.isEmpty()) {
			indirizzoCompletoBuffer.append(indirizzoVia + " - ");
		}
		if (indirizzoComune != null && !indirizzoComune.isEmpty()) {
			indirizzoCompletoBuffer.append(indirizzoComune + ", ");
		}
		if (IndirizzoTarga != null && !IndirizzoTarga.isEmpty()) {
			indirizzoCompletoBuffer.append(IndirizzoTarga);
		}
		dto.setIndirizzoDomicilioCompleto(indirizzoCompletoBuffer.toString());

		if (entity.getPfPrincipal().getFlagAbilitato()) {
			dto.setTipoAccount(TipoAccount.ACCOUNT_ATTIVO.getLabel());
		} else {
			dto.setTipoAccount(TipoAccount.ACCOUNT_NON_ATTIVO.getLabel());
		}

		if (entity.getPfPrincipal().getFlagAbilitatoServizi()) {
			dto.setTipoRegistrazione(TipoRegistrazione.REGISTRAZIONE_FORTE.getLabel());
		} else {
			dto.setTipoRegistrazione(TipoRegistrazione.REGISTRAZIONE_DEBOLE.getLabel());
		}

		return dto;
	}

	/**
	 * Cerca un utente a partire dal suo Username.
	 * 
	 * @param name
	 *            username da ricercare
	 * @return l'utente con lo username specificato, null altrimenti
	 */
	public UtenteInfo findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getUtenteInfo() == null)
			return null;
		return pf.getUtenteInfo();
	}

	/**
	 * Non deve lanciare eccezione se non trova l'utente
	 * 
	 * @param name
	 * @return
	 */

	public UtenteInfo findByUsernameFromSocial(String name) {
		PfPrincipal pf = pfPrincipalHome.findByUsername(name);
		if (pf == null || pf.getUtenteInfo() == null)
			return null;
		return pf.getUtenteInfo();
	}

	/**
	 * Il codice fiscale lo salviamo sempre UPPERCASED
	 */
	@Override
	public void persist(UtenteInfo transientInstance) {
		String cf = transientInstance.getCodiceFiscale();
		if (cf != null) {
			transientInstance.setCodiceFiscale(cf.toUpperCase());
		}
		super.persist(transientInstance);
	}

	/**
	 * Il codice fiscale lo salviamo sempre UPPERCASED
	 */
	@Override
	public UtenteInfo merge(UtenteInfo detachedInstance) {
		String cf = detachedInstance.getCodiceFiscale();
		if (cf != null) {
			detachedInstance.setCodiceFiscale(cf.toUpperCase());
		}
		return super.merge(detachedInstance);
	}

	/**
	 * @param dto
	 *            UtenteInfoDTO
	 * @return UtenteInfo
	 */
	@Override
	public UtenteInfo fromDTO(UtenteInfoDTO dto) {
		if (dto == null)
			return null;
		UtenteInfo entity = super.fromDTO(dto);
		entity.setCellulare(dto.getCellulare());
		entity.setCellulareOTP(dto.getCellulareOTP());
		entity.setFax(dto.getFax());
		entity.setFoto(dto.getFoto());
		entity.setTelCasa(dto.getTelCasa());
		entity.setTelUfficio(dto.getTelUfficio());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdUtente()));
		entity.setCodiceFiscale(dto.getCodiceFiscale());

		entity.setDeComuneDomicilio(deComuneHome.findById(dto.getComuneDomicilio().getId()));
		entity.setDeComuneNascita(deComuneHome.findById(dto.getComuneNascita().getId()));
		entity.setDeProvincia(deProvinciaHome.findById(dto.getProvinciaRiferimento().getId()));
		entity.setIndirizzoDomicilio(dto.getIndirizzoDomicilio());
		if (dto.getCittadinanza() != null && dto.getCittadinanza().getId() != null) {
			entity.setDeCittadinanza(deCittadinanzaHome.findById(dto.getCittadinanza().getId()));
		}
		entity.setEmailPEC(dto.getIndirizzoPEC());
		entity.setDtNascita(dto.getDataNascita());
		entity.setDocumentoIdentita(dto.getDocumentoIdentita());
		if (dto.getDocumentoSoggiorno() != null) {
			if (dto.getDocumentoSoggiorno().getId() != null) {
				entity.setDocumentoSoggiorno(deTitoloSoggiornoHome.findById(dto.getDocumentoSoggiorno().getId()));
			} else if (dto.getDocumentoSoggiorno().getDescrizione() != null) {
				DeTitoloSoggiornoDTO temp = deTitoloSoggiornoHome.findByDescription(
						dto.getDocumentoSoggiorno().getDescrizione()).get(0);
				entity.setDocumentoSoggiorno(deTitoloSoggiornoHome.fromDTO(temp));
			}
		}
		entity.setNumeroDocumento(dto.getNumeroDocumento());
		entity.setDataScadenzaDocumento(dto.getDataScadenzaDocumento());
		entity.setNumeroAssicurata(dto.getNumeroAssicurata());
		entity.setDataAssicurata(dto.getDataAssicurata());

		entity.setFlgAcceptedInformativaDid(dto.getAcceptedInformativaDid());
		entity.setFlgAcceptedInformativaStatoOcc(dto.getAcceptedInformativaStatoOcc());
		entity.setFlgAcceptedInformativaPercLav(dto.getAcceptedInformativaPercLav());
		entity.setFlgAcceptedInformativaConfermaPeriodica(dto.getAcceptedInformativaConfermaPeriodica());
		entity.setFlgAcceptedInformativaRinnovoPatto(dto.getAcceptedInformativaRinnovoPatto());
		
		//entity.setCodServiziAmministrativi(dto.getCodServiziAmministrativi());

		// Adesione YG
		entity.setCapDomicilio(dto.getCapDomicilio());
		if (dto.getGenere() != null && dto.getGenere().getId() != null && !"".equalsIgnoreCase(dto.getGenere().getId())) {
			entity.setDeGenere(deGenereHome.findById(dto.getGenere().getId()));
		}
		if (dto.getMotivoPermesso() != null && dto.getMotivoPermesso().getId() != null
				&& !"".equalsIgnoreCase(dto.getMotivoPermesso().getId())) {
			entity.setDeMotivoPermesso(deMotivoPermessoHome.findById(dto.getMotivoPermesso().getId()));
		}
		entity.setCapResidenza(dto.getCapResidenza());
		if (dto.getComuneResidenza() != null && dto.getComuneResidenza().getId() != null
				&& !"".equalsIgnoreCase(dto.getComuneResidenza().getId())) {
			entity.setDeComuneResidenza(deComuneHome.findById(dto.getComuneResidenza().getId()));
		}
		entity.setIndirizzoResidenza(dto.getIndirizzoResidenza());
		entity.setFlgConsensoSms(dto.getFlgConsensoSms());

		return entity;
	}

	@Override
	public UtenteInfoDTO toDTO(UtenteInfo entity) {
		if (entity == null) {
			return null;
		}

		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		UtenteInfoDTO dto = super.toDTO(entity);
		dto.setId(pfPrincipal.getIdPfPrincipal());
		dto.setCellulare(entity.getCellulare());
		dto.setCellulareOTP(entity.getCellulareOTP());
		dto.setFax(entity.getFax());
		dto.setFoto(entity.getFoto());
		dto.setTelCasa(entity.getTelCasa());
		dto.setTelUfficio(entity.getTelUfficio());
		dto.setIdUtente(pfPrincipal.getIdPfPrincipal());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setIndirizzoDomicilio(entity.getIndirizzoDomicilio());
		dto.setIndirizzoPEC(entity.getEmailPEC());
		dto.setDataNascita(entity.getDtNascita());
		dto.setCittadinanza(deCittadinanzaHome.toDTO(entity.getDeCittadinanza()));
		dto.setDocumentoIdentita(entity.getDocumentoIdentita());
		dto.setDocumentoSoggiorno(deTitoloSoggiornoHome.toDTO(entity.getDocumentoSoggiorno()));
		dto.setNumeroDocumento(entity.getNumeroDocumento());
		dto.setDataScadenzaDocumento(entity.getDataScadenzaDocumento());
		dto.setNumeroAssicurata(entity.getNumeroAssicurata());
		dto.setDataAssicurata(entity.getDataAssicurata());

		dto.setComuneDomicilio(deComuneHome.toDTO(entity.getDeComuneDomicilio()));
		dto.setComuneNascita(deComuneHome.toDTO(entity.getDeComuneNascita()));
		dto.setProvinciaRiferimento(deProvinciaHome.toDTO(entity.getDeProvincia()));
		dto.setStileSelezionato(pfPrincipal.getStileSelezionato());

		dto.setIndirizzoDomicilio(entity.getIndirizzoDomicilio());
		dto.setDomanda(pfPrincipal.getDomanda());
		dto.setRisposta(pfPrincipal.getRisposta());
		dto.setCodiceRichiestaAutForte(pfPrincipal.getRichiestaRegForteToken());
		dto.setCodServiziAmministrativi(entity.getCodServiziAmministrativi());
		dto.setAcceptedInformativaDid(entity.getFlgAcceptedInformativaDid());
		dto.setAcceptedInformativaPercLav(entity.getFlgAcceptedInformativaPercLav());
		dto.setAcceptedInformativaStatoOcc(entity.getFlgAcceptedInformativaStatoOcc());
		dto.setAcceptedInformativaConfermaPeriodica(entity.getFlgAcceptedInformativaConfermaPeriodica());
		dto.setAcceptedInformativaRinnovoPatto(entity.getFlgAcceptedInformativaRinnovoPatto());
		if (entity.getCodStatoAtipico() == null || entity.getCodStatoAtipico().isEmpty()) {
			dto.setCodStatoAtipico(StatoAtipicoEnum.DISABILITATO.getCodice());
		} else {
			dto.setCodStatoAtipico(StatoAtipicoEnum.getInstance(entity.getCodStatoAtipico()).getCodice());
		}

		dto.setCapDomicilio(entity.getCapDomicilio());
		dto.setGenere(deGenereHome.toDTO(entity.getDeGenere()));
		dto.setMotivoPermesso(deMotivoPermessoHome.toDTO(entity.getDeMotivoPermesso()));
		dto.setCapResidenza(entity.getCapResidenza());
		dto.setComuneResidenza(deComuneHome.toDTO(entity.getDeComuneResidenza()));
		dto.setIndirizzoResidenza(entity.getIndirizzoResidenza());
		dto.setFlgConsensoSms(entity.getFlgConsensoSms());

		return dto;
	}

	@Override
	public UtenteInfoDTO persistDTO(UtenteInfoDTO data, Integer idPrincipalIns) {
		PfPrincipal principal = pfPrincipalHome.findById(idPrincipalIns);
		principal.setDomanda(data.getDomanda());
		principal.setRisposta(data.getRisposta());

		pfPrincipalHome.merge(principal);

		UtenteInfoDTO ret = super.persistDTO(data, idPrincipalIns);
		return ret;
	}

	@Override
	public UtenteInfoDTO mergeDTO(UtenteInfoDTO data, Integer idPrincipalMod) {
		PfPrincipal principal = pfPrincipalHome.findById(idPrincipalMod);
		principal.setDtmMod(new Date());
		principal.setPfPrincipalMod(pfPrincipalHome.findById(idPrincipalMod));
		principal.setDomanda(data.getDomanda());
		principal.setRisposta(data.getRisposta());
		principal.setRichiestaRegForteToken(data.getCodiceRichiestaAutForte());

		pfPrincipalHome.merge(principal);

		return super.mergeDTO(data, idPrincipalMod);
	}

	/**
	 * /** Cerca un utente a partire dall'indirizzo email PEC.
	 * 
	 * @param emailPEC
	 *            email da ricercare
	 * @return l'utente con l'indirizzo email specificato, null altrimenti
	 */
	public UtenteInfoDTO findDTOByEmailPEC(String emailPEC) {
		UtenteInfoDTO result = null;
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UtenteInfo> query = qb.createQuery(UtenteInfo.class);
		Root<UtenteInfo> utenteInfo = query.from(UtenteInfo.class);

		query.where(qb.equal(utenteInfo.get(UtenteInfo_.emailPEC), emailPEC));

		TypedQuery<UtenteInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);

		List<UtenteInfoDTO> ret = findDTOByQuery(q);
		if (!ret.isEmpty()) {
			result = ret.get(0);
		}
		return result;
	}

	/**
	 * Restituisce la lista di n. 5 ricerche salvate dall'utente ordinate per dtmMod desc.
	 * 
	 * @param id
	 *            Integer
	 * @return List<RvTestataDTO>
	 */
	public List<RvTestataDTO> getAllRicercheByIdPfPrincipal(Integer idPfPrincipal) {
		try {
			Class<RvTestata> clazz = (Class<RvTestata>) RvTestata.class;
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RvTestata> cq = cb.createQuery(clazz);
			Root<RvTestata> r = cq.from(clazz);
			Predicate p = cb.conjunction();
			p = cb.and(p, cb.equal(r.get(RvTestata_.pfPrincipal).get(PfPrincipal_.idPfPrincipal), idPfPrincipal));
			cq.select(r).where(p).orderBy(cb.desc(r.get(RvTestata_.dtmMod)));
			TypedQuery<RvTestata> query = entityManager.createQuery(cq);
			query.setFirstResult(0);
			query.setMaxResults(5);
			List<RvTestata> listEntity = query.getResultList();

			// Converto l'oggetto List delle entity in List DTO
			List<RvTestataDTO> listDTO = new ArrayList<RvTestataDTO>();
			for (RvTestata rvTestata : listEntity) {
				RvTestataDTO rvTestataDTO = rvTestataHome.toDTO(rvTestata);
				if (rvTestata != null) {
					int numNew = 0;
					// per recuperare il numero di vacancy nuove
					RvGroupDTO rvGroupDTO = new RvGroupDTO();
					rvGroupDTO = rvTestataHome.loadGruppi(rvTestataDTO);

					// recupera il numero di vacancy modificate tra la data di
					// salvataggio e sysdate
					numNew = rvRicercaVacancyHome.getNumVacancyModificate(rvTestataDTO, rvGroupDTO,
							rvTestataDTO.getDtmMod());

					rvTestataDTO.setRowsNew(numNew);
				}
				listDTO.add(rvTestataDTO);
			}
			return listDTO;
		} catch (Exception e) {
			log.error("Errore durante il recupero delle ricerche salvate: " + e.getMessage());
			throw new EJBException("Errore durante il recupero delle ricerche salvate", e);
		}
	}

	/**
	 * Registra un utente
	 * 
	 * @param register
	 *            dati di registrazione dell'utente
	 * @return id del principal creato
	 */
	public Integer register(RegisterUtenteDTO register, boolean pwdScaduta) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();
		// crea il principal

		PfPrincipal principalUtente = pfPrincipalHome.registerCittadino(register, administrator, now, pwdScaduta);

		// creazione info utente
		UtenteInfo utenteInfo = new UtenteInfo();
		utenteInfo.setPfPrincipal(principalUtente);
		utenteInfo.setIdPfPrincipal(principalUtente.getIdPfPrincipal());
		utenteInfo.setPfPrincipalIns(administrator);
		utenteInfo.setPfPrincipalMod(administrator);
		// Aggiunte per Trento
		utenteInfo.setDtNascita(register.getDataNascita());
		utenteInfo.setCellulare(register.getCellulare());
		utenteInfo.setTelCasa(register.getTelefono());
		utenteInfo.setIndirizzoDomicilio(register.getIndirizzo());

		if (register.getComune() != null && register.getComune().getId() != null) {
			utenteInfo.setDeComuneNascita(deComuneHome.findById(register.getComune().getId()));
		}
		if (register.getDomicilio() != null && register.getDomicilio().getId() != null) {
			utenteInfo.setDeComuneDomicilio(deComuneHome.findById(register.getDomicilio().getId()));
		}
		if (register.getProvincia() != null && register.getProvincia().getId() != null) {
			utenteInfo.setDeProvincia(deProvinciaHome.findById(register.getProvincia().getId()));
		}
		if (register.getCittadinanza() != null && register.getCittadinanza().getId() != null) {
			utenteInfo.setDeCittadinanza(deCittadinanzaHome.findById(register.getCittadinanza().getId()));
		}
		utenteInfo.setCodiceFiscale(register.getCodiceFiscale());
		utenteInfo.setEmailPEC(register.getIndirizzoPEC());
		if (register.getDocumentoSoggiorno() != null && register.getDocumentoSoggiorno().getId() != null) {
			utenteInfo.setDocumentoSoggiorno(deTitoloSoggiornoHome.findById(register.getDocumentoSoggiorno().getId()));
		}
		utenteInfo.setDocumentoIdentita(register.getDocumentoIdentita());
		utenteInfo.setNumeroDocumento(register.getNumeroDocumento());
		utenteInfo.setDataScadenzaDocumento(register.getDataScadenzaDocumento());
		utenteInfo.setNumeroAssicurata(register.getNumeroAssicurata());
		utenteInfo.setDataAssicurata(register.getDataAssicurata());
		// accettazione informative
		utenteInfo.setFlgAcceptedInformativaDid(false);
		utenteInfo.setFlgAcceptedInformativaPercLav(false);
		utenteInfo.setFlgAcceptedInformativaStatoOcc(false);
		utenteInfo.setFlgAcceptedInformativaConfermaPeriodica(false);
		utenteInfo.setFlgAcceptedInformativaRinnovoPatto(false);

		utenteInfo.setDtmIns(now);
		utenteInfo.setDtmMod(now);

		persist(utenteInfo);

		entityManager.flush();

		List<PtPortlet> portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.CITTADINO);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {

				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalUtente);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalUtente);
				iesimaScrivania.setPfPrincipalMod(principalUtente);

				// la portlet di YG deve essere la prima visualizzata
				if (("_portlet_yg").equalsIgnoreCase(iesimaPortlet.getNome())) {
					iesimaScrivania.setPosizione(0);
					iesimaScrivania.setOptColonna("L");
				} else {
					iesimaScrivania.setPosizione((i + 1) % 5);
					iesimaScrivania.setOptColonna(((i + 1) % 2 == 0) ? "L" : "R");
				}
				ptScrivaniaHome.persist(iesimaScrivania);

				ptScrivaniaHome.findPortletsScrivania(principalUtente.getIdPfPrincipal());
			}
		}

		return principalUtente.getIdPfPrincipal();
	}

	/**
	 * Registra un utente che ha accduto tramite identity provider
	 * 
	 * @param data
	 * @param providerParameters
	 */
	public void register(RegisterUtenteDTO data, TipoProvider tipoProvider,
			Map<String, ? extends Object> providerParameters) {

		Date dataFutura = new GregorianCalendar(2050, 01, 01).getTime();

		Integer idPfPrincipal = register(data, false);

		pfIdentityProviderHome.create(idPfPrincipal, tipoProvider, providerParameters);
		// se ha richiesto l'auth forte perchè il trust level lo consente
		// allora lo abilito direttamente
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);

		if (tipoProvider.equals(TipoProvider.TWITTER) || tipoProvider.equals(TipoProvider.FACEBOOK)
				|| tipoProvider.equals(TipoProvider.GOOGLE) || tipoProvider.equals(TipoProvider.LINKEDIN)) {
			entityManager.refresh(pfPrincipal);
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();

			utenteInfo.setFoto((byte[]) providerParameters.get(ConstantsSingleton.Auth.IMAGE));
			entityManager.merge(utenteInfo);

			// Gli account "social" non scadono mai
			pfPrincipal.setDtFineValidita(dataFutura);
			pfPrincipal.setDtScadenza(dataFutura);
		}

		if (tipoProvider.equals(TipoProvider.ICAR) || tipoProvider.equals(TipoProvider.FEDERA)) {
			// Anche gli account da provider esterni non scadono mai
			pfPrincipal.setDtFineValidita(dataFutura);
			pfPrincipal.setDtScadenza(dataFutura);
		}

		pfPrincipal.setFlagAbilitato(true);
		if (data.getAutenticazioneForte()) {
			pfPrincipal.setFlagAbilitatoServizi(true);
		}

		pfPrincipalHome.merge(pfPrincipal);

	}

	/**
	 * TODO eseguire un 'merge' del metodo equivalente in AziendaHome e inserirne uno unico nella PfPrincipalHome. Per
	 * ora manca il campo email dell'azienda.w
	 */
	public Integer confirm(RegisterDTO data) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(data.getUsername());
		if (pfPrincipal == null)
			return ConstantsSingleton.Register.USER_NOT_FOUND;
		if (pfPrincipal.getFlagAbilitato())
			return ConstantsSingleton.Register.USER_ALREADY_ACTIVATED;
		if (data.getActivateToken() == null || !data.getActivateToken().equals(pfPrincipal.getConfirmationToken())) {
			return ConstantsSingleton.Register.WRONG_TOKEN;
		}
		if (data.getEmail() == null || !data.getEmail().equals(pfPrincipal.getEmail())) {
			return ConstantsSingleton.Register.WRONG_EMAIL;
		}
		pfPrincipal.setFlagAbilitato(true); // non è ancora attivo
		pfPrincipalHome.persist(pfPrincipal);
		return ConstantsSingleton.Register.USER_ACTIVATED;
	}

	/**
	 * Restituisce le lettere di accompagnamento di un utente che non sono di tipo 'copia'
	 * 
	 * @param idUtente
	 * @return
	 */
	public List<SelectItem> getAllLettereAccAsSelectItem(Integer idUtente) {
		return getAllLettereAccAsSelectItem(idUtente, false);
	}

	/**
	 * Restituisce tutti i cv di un utente che non sono di tipo 'copia'
	 * 
	 * @param idUtente
	 * @return
	 */
	public List<SelectItem> getAllCurriculaAsSelectItem(Integer idUtente) {
		return getAllCurriculaAsSelectItem(idUtente, false);
	}

	public List<SelectItem> getAllClicLavoroCurriculaAsSelectItem(Integer idUtente) {
		return getAllCurriculaAsSelectItem(idUtente, true);
	}
	
	
	public List<SelectItem> getAllCurriculaAsSelectItemIdoRER(Integer idUtente) {
		return getAllCurriculaAsSelectItemIdoRER(idUtente, false);
	}

	/**
	 * Metodo che restituisce l'elenco delle candidature effettuate dall'utente
	 * 
	 * @param idUtente
	 * @return lista di tutte le candidature
	 */
	public List<AcVisualizzaCandidaturaDTO> getAllCandidature(Integer idPfPrincipal) {
		return acCandidaturaHome.findDtosByIdPfPrincipal(idPfPrincipal);
	}

	/**
	 * Metodo che restituisce l'elenco delle candidature effettuate dall'utente, tenendo conto del flg_pacchetto_cresco.
	 */
	public List<AcVisualizzaCandidaturaDTO> getAllCandidature(Integer idPfPrincipal, boolean flgCresco) {
		return acCandidaturaHome.findDtosByIdPfPrincipalAndFlgCresco(idPfPrincipal, flgCresco);
	}

	/**
	 * Metodo che restituisce le ultime candidature effettuate dall'utente
	 * 
	 * @param idPfPrincipal
	 *            id dell'utente per il quale cerco le candidature
	 * @return lista delle ultime candidature
	 */
	public List<AcVisualizzaCandidaturaDTO> getUltimeCandidature(Integer idPfPrincipal) {
		return acCandidaturaHome.findDtosByIdPfPrincipal(idPfPrincipal, ConstantsSingleton.ACULTIMECANDIDATURE);
	}

	/**
	 * Metodo utilizzato nella pagina di ricerca dei cittadini delle province. Restituisce tutti gli utenti del sito la
	 * cui competenza spetta alle province. Nel caso di regione visualizzo tutti i lavoratori
	 * 
	 * @param parametriRicerca
	 *            parametri di ricerca degli utenti
	 * @param idPfPrincipal
	 *            identificativo dell'utente che esegue la ricerca.
	 * @return la lista degli utenti nche corrispondono ai criteri cercati
	 */
	public List<UtenteCompletoDTO> findByFilter(RicercaUtenteDTO parametriRicerca, Integer idPfPrincipal) {
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(idPfPrincipal);
		if (!pfPrincipalRequest.isProvincia() && !pfPrincipalRequest.isRegione()) {
			throw new EJBException("Devi essere una provincia per eseguire le ricerche.");
		}

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UtenteInfo> query = qb.createQuery(UtenteInfo.class);
		Root<UtenteInfo> utenteInfo = query.from(UtenteInfo.class);
		Join<UtenteInfo, PfPrincipal> pfPrincipal = utenteInfo.join(UtenteInfo_.pfPrincipal);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		String nome = parametriRicerca.getNome();
		if (StringUtils.isNotBlank(nome)) {
			nome = nome.trim().toUpperCase();
		}
		String cognome = parametriRicerca.getCognome();
		if (StringUtils.isNotBlank(cognome)) {
			cognome = cognome.trim().toUpperCase();
		}
		String codiceFiscale = parametriRicerca.getCodiceFiscale();
		if (StringUtils.isNotBlank(codiceFiscale)) {
			codiceFiscale = codiceFiscale.trim().toUpperCase();
		}
		String username = parametriRicerca.getUsername();
		if (StringUtils.isNotBlank(username)) {
			username = username.trim().toUpperCase();
		}
		String email = parametriRicerca.getEmail();
		if (StringUtils.isNotBlank(email)) {
			email = email.trim().toUpperCase();
		}
		String emailPEC = parametriRicerca.getEmailPEC();
		if (StringUtils.isNotBlank(emailPEC)) {
			emailPEC = emailPEC.trim().toUpperCase();
		}

		// se devo fare una ricerca esatta imposto le condizioni corrette
		if (parametriRicerca.getTipo().equals(TipoRicercaUtente.ESATTA)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.equal(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), codiceFiscale));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.username)), username));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.equal(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), emailPEC));
			}
		}
		// ricerca per caratteri iniziali
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.INIZIA_PER)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome + "%"));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome + "%"));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), codiceFiscale + "%"));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.username)), username + "%"));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email + "%"));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), emailPEC + "%"));
			}
		}
		// ricerca caratteri contenuti
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.CONTIENE)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), "%" + nome + "%"));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), "%" + cognome + "%"));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), "%" + codiceFiscale
						+ "%"));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.username)), "%" + username + "%"));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), "%" + email + "%"));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), "%" + emailPEC + "%"));
			}
		}
		if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_ATTIVO)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitato), Boolean.TRUE));
		} else if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_NON_ATTIVO)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitato), Boolean.FALSE));
		}

		if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_FORTE)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitatoServizi), Boolean.TRUE));
		} else if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_DEBOLE)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitatoServizi), Boolean.FALSE));
		}

		// competenza della provincia!
		if (pfPrincipalRequest.isProvincia()) {
			Provincia provincia = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();

			Join<UtenteInfo, DeComune> utenteInfo2 = utenteInfo.join(UtenteInfo_.deComuneDomicilio);
			List<Predicate> competenzaFilters = addCompetenzaFilters(provincia, qb, utenteInfo2);
			whereConditions.addAll(competenzaFilters);
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		Order orderA = qb.asc(pfPrincipal.get(PfPrincipal_.cognome));
		Order orderB = qb.asc(pfPrincipal.get(PfPrincipal_.nome));
		Order orderC = qb.asc(pfPrincipal.get(PfPrincipal_.username));
		List<Order> orderBy = new ArrayList<Order>();
		orderBy.add(orderA);
		orderBy.add(orderB);
		orderBy.add(orderC);

		query.orderBy(orderBy);

		TypedQuery<UtenteInfo> q = entityManager.createQuery(query);

		// these can help in pagination cases
		if (parametriRicerca.getStartResultsFrom() != 0) {
			q.setFirstResult(parametriRicerca.getStartResultsFrom());
		}

		if (parametriRicerca.getMaxResults() != 0) {
			q.setMaxResults(parametriRicerca.getMaxResults());
		}

		List<UtenteInfo> result = q.getResultList();

		final List<UtenteCompletoDTO> ret = new ArrayList<UtenteCompletoDTO>();
		for (UtenteInfo entity : result) {
			ret.add(toDTOCompleto(entity));
		}
		return ret;
	}

	public String findByFilterCSV(RicercaUtenteDTO parametriRicerca, Integer idPfPrincipal) {
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(idPfPrincipal);
		if (!pfPrincipalRequest.isProvincia() && !pfPrincipalRequest.isRegione()) {
			throw new EJBException("Devi essere una provincia per eseguire le ricerche.");
		}

		String nome = parametriRicerca.getNome();
		if (StringUtils.isNotBlank(nome)) {
			nome = nome.trim().toUpperCase();
		}
		String cognome = parametriRicerca.getCognome();
		if (StringUtils.isNotBlank(cognome)) {
			cognome = cognome.trim().toUpperCase();
		}
		String codiceFiscale = parametriRicerca.getCodiceFiscale();
		if (StringUtils.isNotBlank(codiceFiscale)) {
			codiceFiscale = codiceFiscale.trim().toUpperCase();
		}
		String username = parametriRicerca.getUsername();
		if (StringUtils.isNotBlank(username)) {
			username = username.trim().toUpperCase();
		}
		String email = parametriRicerca.getEmail();
		if (StringUtils.isNotBlank(email)) {
			email = email.trim().toUpperCase();
		}
		String emailPEC = parametriRicerca.getEmailPEC();
		if (StringUtils.isNotBlank(emailPEC)) {
			emailPEC = emailPEC.trim().toUpperCase();
		}

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
				.append("SELECT new it.eng.myportal.dtos.UtenteCompletoDTO(p.username, u.codiceFiscale, p.nome, p.cognome, u.dtNascita, p.email, c.descrizione, p.flagAbilitato, p.flagAbilitatoServizi)");
		queryBuilder.append(" FROM UtenteInfo AS u");
		queryBuilder.append(" INNER JOIN u.pfPrincipal AS p");
		queryBuilder.append(" LEFT JOIN u.deCittadinanza AS c");

		// competenza della provincia!
		if (pfPrincipalRequest.isProvincia()) {
			queryBuilder.append(" LEFT JOIN u.deComuneDomicilio AS domicilio");
			queryBuilder.append(" LEFT JOIN domicilio.deProvincia AS provincia");
		}

		// competenza della provincia!
		if (pfPrincipalRequest.isProvincia()) {
			Provincia provincia = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();
			queryBuilder.append(" WHERE provincia.codProvincia = '" + provincia.getDeProvincia().getCodProvincia()
					+ "'");
		} else {
			queryBuilder.append(" WHERE 1 = 1");
		}

		// se devo fare una ricerca esatta imposto le condizioni corrette
		if (parametriRicerca.getTipo().equals(TipoRicercaUtente.ESATTA)) {
			if (StringUtils.isNotBlank(nome)) {
				queryBuilder.append(" AND UPPER(p.nome) = '" + nome + "'");
			}
			if (StringUtils.isNotBlank(cognome)) {
				queryBuilder.append(" AND UPPER(p.cognome) = '" + cognome + "'");
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				queryBuilder.append(" AND UPPER(u.codiceFiscale) = '" + codiceFiscale + "'");
			}
			if (StringUtils.isNotBlank(username)) {
				queryBuilder.append(" AND UPPER(p.username) = '" + username + "'");
			}
			if (StringUtils.isNotBlank(email)) {
				queryBuilder.append(" AND UPPER(p.email) = '" + email + "'");
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				queryBuilder.append(" AND UPPER(u.emailPEC) = '" + emailPEC + "'");
			}
		}
		// ricerca per caratteri iniziali
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.INIZIA_PER)) {
			if (StringUtils.isNotBlank(nome)) {
				queryBuilder.append(" AND UPPER(p.nome) LIKE '" + nome + "%'");
			}
			if (StringUtils.isNotBlank(cognome)) {
				queryBuilder.append(" AND UPPER(p.cognome) LIKE '" + cognome + "%'");
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				queryBuilder.append(" AND UPPER(u.codiceFiscale) LIKE '" + codiceFiscale + "%'");
			}
			if (StringUtils.isNotBlank(username)) {
				queryBuilder.append(" AND UPPER(p.username) LIKE '" + username + "%'");
			}
			if (StringUtils.isNotBlank(email)) {
				queryBuilder.append(" AND UPPER(p.email) LIKE '" + email + "%'");
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				queryBuilder.append(" AND UPPER(u.emailPEC) LIKE '" + emailPEC + "%'");
			}
		}
		// ricerca caratteri contenuti
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.CONTIENE)) {
			if (StringUtils.isNotBlank(nome)) {
				queryBuilder.append(" AND UPPER(p.nome) LIKE '%" + nome + "%'");
			}
			if (StringUtils.isNotBlank(cognome)) {
				queryBuilder.append(" AND UPPER(p.cognome) LIKE '%" + cognome + "%'");
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				queryBuilder.append(" AND UPPER(u.codiceFiscale) LIKE '%" + codiceFiscale + "%'");
			}
			if (StringUtils.isNotBlank(username)) {
				queryBuilder.append(" AND UPPER(p.username) LIKE '%" + username + "%'");
			}
			if (StringUtils.isNotBlank(email)) {
				queryBuilder.append(" AND UPPER(p.email) LIKE '%" + email + "%'");
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				queryBuilder.append(" AND UPPER(u.emailPEC) LIKE '%" + emailPEC + "%'");
			}
		}
		if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_ATTIVO)) {
			queryBuilder.append(" AND p.flagAbilitato = 'Y'");
		} else if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_NON_ATTIVO)) {
			queryBuilder.append(" AND p.flagAbilitato = 'N'");
		}

		if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_FORTE)) {
			queryBuilder.append(" AND p.flagAbilitatoServizi = 'Y'");
		} else if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_DEBOLE)) {
			queryBuilder.append(" AND p.flagAbilitatoServizi = 'N'");
		}

		System.out.println(">>>>>>>>>>>>>>>>>>>>");
		System.out.println(queryBuilder.toString());

		TypedQuery<UtenteCompletoDTO> query2 = entityManager.createQuery(queryBuilder.toString(),
				UtenteCompletoDTO.class);
		List<UtenteCompletoDTO> list = query2.getResultList();

		String FIELD_DELIMITER = "\"";
		String FIELD_SEPARATOR = ";";
		String LINE_SEPARATOR = "\n";
		StringBuilder csvBuilder = new StringBuilder();

		/* riga dei titoli */
		csvBuilder.append(FIELD_DELIMITER + "Username" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Codice Fiscale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Nome" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Cognome" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Data di nasita" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "E-mail" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Cittadinanza" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Stato account" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Tipo registrazione" + FIELD_DELIMITER + LINE_SEPARATOR);

		/* dati */
		for (UtenteCompletoDTO utenteCompletoDTO : list) {
			if (utenteCompletoDTO.getUsername() != null) {
				csvBuilder
						.append(FIELD_DELIMITER + utenteCompletoDTO.getUsername() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getCodiceFiscale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getCodiceFiscale() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getNome() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getNome() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getCognome() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getCognome() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getDataNascita() != null) {
				csvBuilder.append(FIELD_DELIMITER
						+ new SimpleDateFormat("dd/MM/yyyy").format(utenteCompletoDTO.getDataNascita())
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getEmail() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getEmail() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getCittadinanza() != null
					&& utenteCompletoDTO.getCittadinanza().getDescrizione() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getCittadinanza().getDescrizione()
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getTipoAccount() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getTipoAccount() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteCompletoDTO.getTipoRegistrazione() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteCompletoDTO.getTipoRegistrazione() + FIELD_DELIMITER
						+ LINE_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + LINE_SEPARATOR);
			}
		}

		String csv = csvBuilder.toString();
		return csv;
	}

	/*
	 * return the count based on filter Criteria this mainly used in pagination
	 * 
	 * @param parametriRicerca parametri di ricerca degli utenti
	 * 
	 * @param idPfPrincipal identificativo dell'utente che esegue la ricerca.
	 * 
	 * @return la lista degli utenti nche corrispondono ai criteri cercati
	 */
	public Long findCountByFilter(RicercaUtenteDTO parametriRicerca, Integer idPfPrincipal) {
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(idPfPrincipal);
		if (!pfPrincipalRequest.isProvincia() && !pfPrincipalRequest.isRegione()) {
			throw new EJBException("Devi essere una provincia per eseguire le ricerche.");
		}

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);
		Root<UtenteInfo> utenteInfo = query.from(UtenteInfo.class);
		query.select(qb.count(utenteInfo));

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		Join<UtenteInfo, PfPrincipal> pfPrincipal = utenteInfo.join(UtenteInfo_.pfPrincipal);

		if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_ATTIVO)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitato), Boolean.TRUE));
		} else if (parametriRicerca.getTipoAccount().equals(TipoAccount.ACCOUNT_NON_ATTIVO)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitato), Boolean.FALSE));
		}

		if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_FORTE)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitatoServizi), Boolean.TRUE));
		} else if (parametriRicerca.getTipoRegistrazione().equals(TipoRegistrazione.REGISTRAZIONE_DEBOLE)) {
			whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitatoServizi), Boolean.FALSE));
		}

		// se viene impostato il parametro sul tipo di curriculum devo cercare
		// tutti
		// gli utenti che hanno almeno un curriculum del tipo indicato
		/*
		 * if (parametriRicerca.getProvenienzaCurriculum().getId() != null) { Join<PfPrincipal, CvDatiPersonali>
		 * cvDatiPersonali = pfPrincipal.join(PfPrincipal_.cvDatiPersonalis, JoinType.LEFT); Join<CvDatiPersonali,
		 * DeProvenienza> deProvenienza = cvDatiPersonali.join( CvDatiPersonali_.provenienzaCurriculum, JoinType.LEFT);
		 * query.distinct(true); whereConditions.add(qb.equal(deProvenienza .get(DeProvenienza_.codProvenienza),
		 * parametriRicerca .getProvenienzaCurriculum().getId())); }
		 */

		String nome = parametriRicerca.getNome();
		if (StringUtils.isNotBlank(nome)) {
			nome = nome.trim().toUpperCase();
		}
		String cognome = parametriRicerca.getCognome();
		if (StringUtils.isNotBlank(cognome)) {
			cognome = cognome.trim().toUpperCase();
		}
		String codiceFiscale = parametriRicerca.getCodiceFiscale();
		if (StringUtils.isNotBlank(codiceFiscale)) {
			codiceFiscale = codiceFiscale.trim().toUpperCase();
		}
		String username = parametriRicerca.getUsername();
		if (StringUtils.isNotBlank(username)) {
			username = username.trim().toUpperCase();
		}
		String email = parametriRicerca.getEmail();
		if (StringUtils.isNotBlank(email)) {
			email = email.trim().toUpperCase();
		}
		String emailPEC = parametriRicerca.getEmailPEC();
		if (StringUtils.isNotBlank(emailPEC)) {
			emailPEC = emailPEC.trim().toUpperCase();
		}

		// se devo fare una ricerca esatta imposto le condizioni corrette
		if (parametriRicerca.getTipo().equals(TipoRicercaUtente.ESATTA)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.equal(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), codiceFiscale));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.username)), username));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.equal(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), emailPEC));
			}
		}
		// ricerca per caratteri iniziali
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.INIZIA_PER)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome + "%"));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome + "%"));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), codiceFiscale + "%"));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.username)), username + "%"));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email + "%"));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), emailPEC + "%"));
			}
		}
		// ricerca caratteri contenuti
		else if (parametriRicerca.getTipo().equals(TipoRicercaUtente.CONTIENE)) {
			if (StringUtils.isNotBlank(nome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), "%" + nome + "%"));
			}
			if (StringUtils.isNotBlank(cognome)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), "%" + cognome + "%"));
			}
			if (StringUtils.isNotBlank(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), "%" + codiceFiscale
						+ "%"));
			}
			if (StringUtils.isNotBlank(username)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.username)), "%" + username + "%"));
			}
			if (StringUtils.isNotBlank(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), "%" + email + "%"));
			}
			if (StringUtils.isNotBlank(emailPEC)) {
				whereConditions.add(qb.like(qb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), "%" + emailPEC + "%"));
			}
		}

		// competenza della provincia!
		if (pfPrincipalRequest.isProvincia()) {
			Provincia provincia = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();

			Join<UtenteInfo, DeComune> utenteInfo2 = utenteInfo.join(UtenteInfo_.deComuneDomicilio);
			List<Predicate> competenzaFilters = addCompetenzaFilters(provincia, qb, utenteInfo2);
			whereConditions.addAll(competenzaFilters);
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();
	}

	/**
	 * Aggiunge i filtri sulla competenza della provincia. TODO il cod_provincia c'è sempre. bisogna eliminare il
	 * coalesce.
	 * 
	 * @param provincia
	 *            la provincia che deve essere competente
	 * @param qb
	 *            il CriteriaBuilder che viene utilizzato per la query
	 * @param utenteInfo2
	 * @param coordinatore
	 *            la ROOT della query che deve estrarre degli Utenti
	 * @return l'array con le condizioni di where
	 */
	private List<Predicate> addCompetenzaFilters(Provincia provincia, CriteriaBuilder qb, Path<DeComune> utenteInfo2) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		whereConditions.add(qb.equal(utenteInfo2.get(DeComune_.deProvincia), provincia.getDeProvincia()));

		return whereConditions;
	}

	/**
	 * Determina se una provincia è competente per un determinato lavoratore
	 * 
	 * @param provincia
	 *            provincia per la quale si vuole valutare la competenza
	 * @param pfPrincipalUtente
	 *            utente del quale si vuole valutare la competenza
	 * @return true se la provincia è competente per il lavoratore.
	 */
	public boolean isProvinciaCompetente(Provincia provincia, Integer idPfPrincipalUtente) {
		PfPrincipal pfPrincipalUtente = pfPrincipalHome.findById(idPfPrincipalUtente);
		if (pfPrincipalUtente.isUtente()) {
			UtenteInfo utenteInfo = pfPrincipalUtente.getUtenteInfo();
			DeProvincia deProvincia = utenteInfo.getDeProvincia();
			String codProvinciaUtente = deProvincia.getCodProvincia();
			String codProvincia = provincia.getDeProvincia().getCodProvincia();
			return (codProvincia.equals(codProvinciaUtente));
		}
		// proviene da clic lavoro
		else if (pfPrincipalUtente.isProvincia()) {
			return true;
		} else {
			throw new MyPortalException("Impossibile calcolare la competenza", true);
		}
	}

	/**
	 * Restituisce la lista di n. 5 vacancy nuove provenienti dalle ricerche salvate dall'utente ordinate per dtmMod
	 * desc.
	 * 
	 * @param id
	 *            Integer
	 * @return List<RvTestataDTO>
	 */
	public List<VaDatiVacancyDTO> getAllVacancyNew(Integer id) {
		try {
			// TODO
			List<VaDatiVacancyDTO> listDTO = new ArrayList<VaDatiVacancyDTO>();

			return listDTO;
		} catch (Exception e) {
			log.error("Errore durante il recupero delle vacancy nuove: " + e.getMessage());
			throw new EJBException("Errore durante il recupero delle vacancy nuove", e);
		}
	}

	public UtenteCompletoDTO mergeDTOCompleto(UtenteCompletoDTO data, Integer principalId) {

		PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.mergeDTO(data.getUtenteDTO().getPfPrincipalDTO(), principalId);
		data.getUtenteDTO().setPfPrincipalDTO(pfPrincipalDTO);

		UtenteInfoDTO utenteInfo = mergeDTO(data.getUtenteInfo(), principalId);

		data.setUtenteInfo(utenteInfo);

		return data;
	}

	/**
	 * Controlla che la provincia di residenza dell'utente che richiede il codice sia la stessa presente nell'archivio
	 * regionale. Questo per individuare il CPI di riferimento. In caso contrario viene visualizzato un alert all'utente
	 * chiedendo se continuare o meno l'operazione.
	 * 
	 * @return
	 */
	public boolean controlloProvinciaDomicilio(String nome, String cognome, Date dataNascita, String codiceFiscale,
			String codProvinciadomicilio) throws ServiziLavoratoreException {
		boolean esitoConfronto = false;
		String xmlResponse = "";
		String codProvinciaMaster = "";
		try {
			// interroga l'indice regionale
			xmlResponse = invokeService(nome, cognome, dataNascita, codiceFiscale);
			// parsing della risposta
			codProvinciaMaster = parseCodProvinciaMaster(xmlResponse);

			log.info("Risposta IR= cognome:" + cognome + " - provmaster:" + codProvinciaMaster);

		} catch (ServiziLavoratoreIndiceUnavailableException e) {
			// se l'indice regionale non e' disponibile l'esito fallisce ma il
			// processo di autenticazione forte continua
			log.error("Risposta IR errore:" + e);
			return esitoConfronto;
		}

		// controllo provincia di domicilio inserita con provincia cpi di
		// riferimento in archivio
		if (codProvinciadomicilio.equals(codProvinciaMaster)) {
			esitoConfronto = true;
		}
		return esitoConfronto;
	}

	public String getProvinciaDomicilioIR(String codiceFiscale) throws ServiziLavoratoreException {
		String xmlResponse = "";
		String codProvinciaMaster = "";
		try {
			// interroga l'indice regionale
			xmlResponse = invokeServiceIR(codiceFiscale);
			// parsing della risposta
			codProvinciaMaster = parseCodProvinciaMaster(xmlResponse);

			log.info("Risposta IR= provmaster:" + codProvinciaMaster);

		} catch (ServiziLavoratoreIndiceUnavailableException e) {
			// se l'indice regionale non e' disponibile l'esito fallisce ma il
			// processo di autenticazione forte continua
			log.error("Risposta IR errore:" + e);
		}

		return codProvinciaMaster;
	}

	/**
	 * Metodo che genera il codice di autenticazione forte per l'utente.
	 */
	public String generaCodiceRichiestaAutforte() {
		String codice = Utils.randomString(24);
		return codice;
	}

	/**
	 * Invocazione del WS dell'indice regionale.
	 * 
	 * @return rispoata XML ottenuta dal servizio.
	 */
	private String invokeService(String nome, String cognome, Date dataNascita, String codiceFiscale)
			throws ServiziLavoratoreIndiceUnavailableException {

		try {
			String endpoint = wsEndpointHome.getServiziLavoratoreAddress();
			ServiziLavoratore service = new ServiziLavoratoreProxy(endpoint);
			SimpleDateFormat formatter = new SimpleDateFormat(ConstantsSingleton.Date.FORMAT);
			String dataNascitaString = formatter.format(dataNascita);
			String xmlResponse = service.getLavoratoreIR(codiceFiscale, nome, cognome, "'" + dataNascitaString + "'",
					null, "esatta");
			log.debug("IR risposta:" + xmlResponse);

			return xmlResponse;
		} catch (Exception e) {
			throw new ServiziLavoratoreIndiceUnavailableException(e);
		}
	}

	private String invokeServiceIR(String codiceFiscale) throws ServiziLavoratoreIndiceUnavailableException {
		try {
			String endpoint = wsEndpointHome.getServiziLavoratoreAddress();
			ServiziLavoratore service = new ServiziLavoratoreProxy(endpoint);
			String xmlResponse = service.getLavoratoreIR(codiceFiscale, null, null, null, null, "esatta");
			log.debug("IR risposta:" + xmlResponse);

			return xmlResponse;
		} catch (Exception e) {
			throw new ServiziLavoratoreIndiceUnavailableException(e);
		}
	}

	/**
	 * Questo metodo fa il parsing della risposta all'interrogazione dell'indice regionale e restituisce il codProvincia
	 * della prima riga (nodo ROW) ritornata.
	 * 
	 * @param xmlResponse
	 *            risposta ottenuda dopo l'interrogazione dell'indice regionale
	 * @return codProvincia
	 */
	private String parseCodProvinciaMaster(String xmlResponse) throws ServiziLavoratoreException {
		String codProvinciaMaster = "";
		if (!xmlResponse.isEmpty()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			Document document = null;
			try {
				builder = factory.newDocumentBuilder();
				document = builder.parse(new InputSource(new StringReader(xmlResponse)));

				XPath xpath = XPathFactory.newInstance().newXPath();
				XPathExpression codProvinciaMasterExp = xpath.compile("/ROWS/ROW[1]/@CODPROVINCIAMASTER");

				codProvinciaMaster = codProvinciaMasterExp.evaluate(document);
			} catch (ParserConfigurationException e1) {
				throw new ServiziLavoratoreException(e1);
			} catch (SAXException e1) {
				throw new ServiziLavoratoreException(e1);
			} catch (IOException e1) {
				throw new ServiziLavoratoreException(e1);
			} catch (XPathExpressionException e1) {
				throw new ServiziLavoratoreException(e1);
			}
		}
		return codProvinciaMaster;
	}

	/**
	 * Cerca un cittadino
	 * 
	 * @param username
	 * @param federaParameters
	 * @return
	 */
	public UtenteDTO findOrCreateDTOByFedera(String username, HashMap<String, String> federaParameters) {
		throw new MyPortalException("TODO", true);
	}

	public String getCodiceRichiestaAutForte(String username) {
		return findByUsername(username).getPfPrincipal().getRichiestaRegForteToken();
	}

	public void salvaFoto(Integer idUtenteInfo, byte[] foto) {
		UtenteInfo utenteInfo = findById(idUtenteInfo);

		utenteInfo.setFoto(foto);

		merge(utenteInfo);
	}

	// check if this user has been registered on the system by any other methods
	// like (facebook, twitter, etc..)
	public boolean isSocialConnect(String username) {
		return !pfIdentityProviderHome.findByPFPId(findByUsername(username).getPfPrincipal().getIdPfPrincipal())
				.isEmpty();
	}

	/***
	 * 
	 * Utile quando si deve essere sicuri di salvare i dati su database
	 * 
	 * @param data
	 * @param principalId
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UtenteCompletoDTO mergeDTOCompletoWithNewTransaction(UtenteCompletoDTO data, Integer principalId) {
		return mergeDTOCompleto(data, principalId);
	}

	public boolean integraDatiGenereResidenzaPermesso(Integer idPfPrincipal, String codGenere,
			String indirizzoResidenza, String capResidenza, String codComResidenza, String codMotivoPermesso) {

		boolean success = false;

		UtenteInfo utenteInfo = findById(idPfPrincipal);

		if (utenteInfo != null) {

			utenteInfo.setCapResidenza(capResidenza);
			utenteInfo.setIndirizzoResidenza(indirizzoResidenza);

			if (codGenere != null) {
				DeGenere deGenere = deGenereHome.findById(codGenere);
				if (deGenere != null) {
					utenteInfo.setDeGenere(deGenere);
				}
			}

			if (codComResidenza != null) {
				DeComune comuneResidenza = deComuneHome.findById(codComResidenza);
				if (comuneResidenza != null) {
					utenteInfo.setDeComuneResidenza(comuneResidenza);
				}
			}

			if (codMotivoPermesso != null) {
				DeMotivoPermesso deMotivoPermesso = deMotivoPermessoHome.findById(codMotivoPermesso);
				if (deMotivoPermesso != null) {
					utenteInfo.setDeMotivoPermesso(deMotivoPermesso);
				}
			}

			merge(utenteInfo);

			success = true;

		}

		return success;

	}

}
