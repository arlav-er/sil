package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.RvGroupDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvAgricolo;
import it.eng.myportal.entity.RvAttivita;
import it.eng.myportal.entity.RvContratto;
import it.eng.myportal.entity.RvLingua;
import it.eng.myportal.entity.RvMansione;
import it.eng.myportal.entity.RvOrario;
import it.eng.myportal.entity.RvPatente;
import it.eng.myportal.entity.RvTestata;
import it.eng.myportal.entity.RvTestata_;
import it.eng.myportal.entity.RvTitolo;
import it.eng.myportal.entity.RvTrasferta;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTrasferta;
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
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * 
 * @author Rodi A
 */
@Stateless
public class RvTestataHome extends AbstractUpdatableHome<RvTestata, RvTestataDTO> {

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeRegioneHome deRegioneHome;

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
	DeTrasfertaHome deTrasfertaHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	public RvTestata findById(Integer id) {
		return findById(RvTestata.class, id);
	}

	@Override
	public RvTestataDTO toDTO(RvTestata entity) {
		if (entity == null)
			return null;
		RvTestataDTO dto = super.toDTO(entity);
		DeComune comune = entity.getDeComune();
		DeProvincia provincia = entity.getDeProvincia();
		DeRegione regione = entity.getDeRegione();
		DeMansione mansione = entity.getDeMansione();

		dto.setDescrizione(entity.getDescrizione());
		dto.setCosa(entity.getCosa());
		dto.setDove(entity.getDove());
		dto.setId(entity.getIdRvTestata());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		if (comune != null) {
			dto.setCodComune(comune.getCodCom());
			dto.setStrComune(comune.getDenominazione());
		}
		if (provincia != null) {
			dto.setCodProvincia(provincia.getCodProvincia());
			dto.setStrProvincia(provincia.getDenominazione());
		}
		if (regione != null) {
			dto.setCodRegione(regione.getCodRegione());
			dto.setStrRegione(regione.getDenominazione());
		}
		if (mansione != null) {
			dto.setCodMansione(mansione.getCodMansione());
			dto.setStrMansione(mansione.getDescrizione());
		}
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setRaggioRicerca(entity.getRaggioRicerca());

		return dto;
	}

	@Override
	public RvTestata fromDTO(RvTestataDTO dto) {
		if (dto == null)
			return null;
		RvTestata entity = super.fromDTO(dto);

		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setIdRvTestata(dto.getId());
		if (StringUtils.isNotBlank(dto.getCodComune())) {
			entity.setDeComune(deComuneHome.findById(dto.getCodComune()));
		}
		if (StringUtils.isNotBlank(dto.getCodProvincia())) {
			entity.setDeProvincia(deProvinciaHome.findById(dto.getCodProvincia()));
		}
		if (StringUtils.isNotBlank(dto.getCodRegione())) {
			entity.setDeRegione(deRegioneHome.findById(dto.getCodRegione()));
		}
		if (StringUtils.isNotBlank(dto.getCodMansione())) {
			entity.setDeMansione(deMansioneHome.findById(dto.getCodMansione()));
		}
		String descrizione = dto.getDescrizione();
		if (StringUtils.isNotBlank(descrizione)) {
			entity.setDescrizione(dto.getDescrizione());
		}
		String cosa = dto.getCosa();
		if(cosa == null || StringUtils.isNotBlank(cosa)) {
			entity.setCosa(dto.getCosa());
		}
		/*if (StringUtils.isNotBlank(cosa)) {
			entity.setCosa(dto.getCosa());
		}*/
		
		String dove = dto.getDove();
		if (StringUtils.isNotBlank(dove)) {
			entity.setDove(dto.getDove());
		}
		entity.setRaggioRicerca(dto.getRaggioRicerca());

		return entity;
	}

	@Override
	public RvTestataDTO persistDTO(RvTestataDTO data, Integer idPrincipalIns) {
		// Effettuo prima la persist della RvTestata per poi agganciare tutti i
		// gruppi
		RvTestataDTO dto = super.persistDTO(data, idPrincipalIns);
		persistGruppi(data, dto);
		return dto;
	}

	@Override
	public RvTestataDTO mergeDTO(RvTestataDTO data, Integer idPrincipalMod) {
		RvTestata entity = fromDTO(data);
		Integer idRvTestata = entity.getIdRvTestata();
		removeGruppi(idRvTestata);

		RvTestataDTO dto = super.mergeDTO(data, idPrincipalMod);

		persistGruppi(data, dto);
		return dto;
	}

	/**
	 * Aggiorno la dtmMod alla data corrente, per indicare la data ulima di consultazione della vacancy
	 * 
	 * @param rvTestataId
	 *            Integer
	 */
	public void mergeDtmMod(Integer rvTestataId) {
		RvTestata rvTestata = findById(rvTestataId);
		rvTestata.setDtmMod(new Date());
		merge(rvTestata);
	}

	/**
	 * Recupero i gruppi presenti nel DB
	 * 
	 * @param idRvTestata
	 */
	public RvGroupDTO loadGruppi(RvTestataDTO rvTestata) {
		RvGroupDTO dto = new RvGroupDTO();
		Integer idRvTestata = rvTestata.getId();
		List<RvAgricolo> listRvAgricolo = null;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
		  listRvAgricolo = (findByIdRvTestata(RvAgricolo.class, idRvTestata));
		}
		List<RvMansione> listRvMansione = (findByIdRvTestata(RvMansione.class, idRvTestata));
		List<RvContratto> listRvContratto = (findByIdRvTestata(RvContratto.class, idRvTestata));
		List<RvOrario> listRvOrario = (findByIdRvTestata(RvOrario.class, idRvTestata));
		// List<RvEsperienza> listRvEsperienza =
		// (findByIdRvTestata(RvEsperienza.class, entity.getIdRvTestata()));
		List<RvAttivita> listRvAttivita = (findByIdRvTestata(RvAttivita.class, idRvTestata));
		List<RvLingua> listRvLingua = (findByIdRvTestata(RvLingua.class, idRvTestata));
		List<RvTitolo> listRvTitolo = (findByIdRvTestata(RvTitolo.class, idRvTestata));
		// List<RvTrasferta> listRvTrasferta =
		// (findByIdRvTestata(RvTrasferta.class, idRvTestata));
		List<RvPatente> listRvPatente = (findByIdRvTestata(RvPatente.class, idRvTestata));

		for (Iterator<RvMansione> iterator = listRvMansione.iterator(); iterator.hasNext();) {
			String key = ((RvMansione) iterator.next()).getDeMansione().getCodMansione();
			dto.getCheckMansione().put(key, true);
		}

		for (Iterator<RvContratto> iterator = listRvContratto.iterator(); iterator.hasNext();) {
			RvContratto nextContratto = (RvContratto) iterator.next();
			if (nextContratto.getDeContrattoSil() != null) {
				dto.getCheckContratto().put(nextContratto.getDeContrattoSil().getCodContrattoSil(), true);
			} else {
				dto.getCheckContratto().put(nextContratto.getDeContratto().getCodContratto(), true);
			}
		}

		for (Iterator<RvOrario> iterator = listRvOrario.iterator(); iterator.hasNext();) {
			RvOrario nextOrario = (RvOrario) iterator.next();
			if (nextOrario.getDeOrarioSil() != null) {
				dto.getCheckOrario().put(nextOrario.getDeOrarioSil().getCodOrarioSil(), true);
			} else {
				dto.getCheckOrario().put(nextOrario.getDeOrario().getCodOrario(), true);
			}
		}
		/*
		 * for (Iterator<RvEsperienza> iterator = listRvEsperienza.iterator(); iterator.hasNext();) { String key =
		 * ((RvEsperienza)iterator.next()).getDeEsperienza().getCodEsperienza(); dto.getCheckEsperienza().put(key,
		 * true); }
		 */
		for (Iterator<RvAttivita> iterator = listRvAttivita.iterator(); iterator.hasNext();) {
			String key = ((RvAttivita) iterator.next()).getDeAttivita().getCodAteco();
			dto.getCheckSettore().put(key, true);
		}
		for (Iterator<RvLingua> iterator = listRvLingua.iterator(); iterator.hasNext();) {
			String key = ((RvLingua) iterator.next()).getDeLingua().getCodLingua();
			dto.getCheckLingua().put(key, true);
		}
		for (Iterator<RvTitolo> iterator = listRvTitolo.iterator(); iterator.hasNext();) {
			String key = ((RvTitolo) iterator.next()).getDeTitolo().getCodTitolo();
			dto.getCheckTitoloStudio().put(key, true);
		}
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			for (Iterator<RvAgricolo> iterator = listRvAgricolo.iterator(); iterator.hasNext();) {
				String key = ((RvAgricolo) iterator.next()).getDescrizione();
				dto.getCheckAgricolo().put(key, true);
			}
		}
		/*
		 * for (Iterator<RvTrasferta> iterator = listRvTrasferta.iterator(); iterator.hasNext();) { String key =
		 * ((RvTrasferta)iterator.next()).getDeTrasferta().getCodTrasferta(); dto.getCheckDispTrasferte().put(key,
		 * true); }
		 */
		for (Iterator<RvPatente> iterator = listRvPatente.iterator(); iterator.hasNext();) {
			RvPatente nextPatente = (RvPatente) iterator.next();
			if (nextPatente.getDePatenteSil() != null) {
				dto.getCheckPatente().put(nextPatente.getDePatenteSil().getCodPatenteSil(), true);
			} else {
				dto.getCheckPatente().put(nextPatente.getDePatente().getCodPatente(), true);
			}
		}
		return dto;
	}

	/**
	 * Rimuovo i gruppi presenti nel DB
	 * 
	 * @param idRvTestata
	 */
	private void removeGruppi(Integer idRvTestata) {
		deleteFiltriGroup(findByIdRvTestata(RvMansione.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvContratto.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvOrario.class, idRvTestata));
		// deleteFiltriGroup(findByIdRvTestata(RvEsperienza.class,
		// entity.getIdRvTestata()));
		deleteFiltriGroup(findByIdRvTestata(RvAttivita.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvLingua.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvTitolo.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvTrasferta.class, idRvTestata));
		deleteFiltriGroup(findByIdRvTestata(RvPatente.class, idRvTestata));
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
		  deleteFiltriGroup(findByIdRvTestata(RvAgricolo.class, idRvTestata));
		}
	}

	/**
	 * Inserimento dei gruppi nel DB col flag a true lato pagina
	 * 
	 * @param data
	 * @param dto
	 */
	private void persistGruppi(RvTestataDTO data, RvTestataDTO dto) {
		persistSetMansione(dto, data.getSetMansione());
		persistSetContratto(dto, data.getSetContratto());
		persistSetOrario(dto, data.getSetOrario());
		persistSetEsperienza(dto, data.getSetEsperienza());
		persistSetSettore(dto, data.getSetSettore());
		persistSetLingua(dto, data.getSetLingua());
		persistSetTitolo(dto, data.getSetTitoloStudio());
		persistSetDispTrasferte(dto, data.getSetDispTrasferte());
		persistSetPatente(dto, data.getSetPatente());
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
		  persistSetAgricolo(dto, data.getSetAgricolo());
		}
	}

	/**
	 * Elimina le entity dei gruppi contenuti all'interno della lista
	 * 
	 * @param entity
	 */
	private void deleteFiltriGroup(List list) {
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			entityManager.remove(iterator.next());
		}
	};

	/**
	 * Persisto la collection dell'oggetto RvMansione
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetMansione(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);

		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvMansione rvMansione = new RvMansione();
				DeMansione deMansione = deMansioneHome.findById(str);
				rvMansione.setDeMansione(deMansione);
				rvMansione.setDtmIns(entity.getDtmIns());
				rvMansione.setDtmMod(entity.getDtmMod());
				rvMansione.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvMansione.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvMansione.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvMansione);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvContratto
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetContratto(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);

		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvContratto rvContratto = new RvContratto();
				if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
					rvContratto.setDeContrattoSil(deContrattoSilHome.findById(str));
				} else {
					rvContratto.setDeContratto(deContrattoHome.findById(str));
				}
				rvContratto.setDtmIns(entity.getDtmIns());
				rvContratto.setDtmMod(entity.getDtmMod());
				rvContratto.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvContratto.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvContratto.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvContratto);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvOrario
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetOrario(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);

		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvOrario rvOrario = new RvOrario();
				if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
					rvOrario.setDeOrarioSil(deOrarioSilHome.findById(str));
				} else {
					rvOrario.setDeOrario(deOrarioHome.findById(str));
				}
				rvOrario.setDtmIns(entity.getDtmIns());
				rvOrario.setDtmMod(entity.getDtmMod());
				rvOrario.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvOrario.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvOrario.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvOrario);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvEsperienza
	 * 
	 * @deprecated
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetEsperienza(RvTestataDTO dto, Set<String> list) {
		// RvTestata entity = fromDTO(dto);
		// if (list != null && !list.isEmpty()) {
		// for (Iterator<String> iterator = list.iterator();
		// iterator.hasNext();) {
		// String str = iterator.next();
		// RvEsperienza rvEsperienza = new RvEsperienza();
		// DeEsperienza deEsperienza = deEsperienzaHome.findById(str);
		// rvEsperienza.setDeEsperienza(deEsperienza);
		// rvEsperienza.setDtmIns(entity.getDtmIns());
		// rvEsperienza.setDtmMod(entity.getDtmMod());
		// rvEsperienza.setPfPrincipalIns(entity.getPfPrincipalIns());
		// rvEsperienza.setPfPrincipalMod(entity.getPfPrincipalMod());
		// rvEsperienza.setRvTestata(entity);
		// //effettuo la persist sul DB
		// entityManager.persist(rvEsperienza);
		// }
		// }
	}

	/**
	 * Persisto la collection dell'oggetto RvAttivita
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetSettore(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);
		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvAttivita rvAttivita = new RvAttivita();
				DeAttivita deAttivita = deAttivitaHome.findById(str);
				rvAttivita.setDeAttivita(deAttivita);
				rvAttivita.setDtmIns(entity.getDtmIns());
				rvAttivita.setDtmMod(entity.getDtmMod());
				rvAttivita.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvAttivita.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvAttivita.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvAttivita);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvLingua
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetLingua(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);
		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvLingua rvLingua = new RvLingua();
				DeLingua deLingua = deLinguaHome.findById(str);
				rvLingua.setDeLingua(deLingua);
				rvLingua.setDtmIns(entity.getDtmIns());
				rvLingua.setDtmMod(entity.getDtmMod());
				rvLingua.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvLingua.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvLingua.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvLingua);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvTitolo
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetTitolo(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);
		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvTitolo rvTitolo = new RvTitolo();
				DeTitolo deTitolo = deTitoloHome.findById(str);
				rvTitolo.setDeTitolo(deTitolo);
				rvTitolo.setDtmIns(entity.getDtmIns());
				rvTitolo.setDtmMod(entity.getDtmMod());
				rvTitolo.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvTitolo.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvTitolo.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvTitolo);
			}
		}
	}

	private void persistSetAgricolo(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);
		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String descrizione = iterator.next();
				RvAgricolo rvAgricolo = new RvAgricolo();				
				rvAgricolo.setDescrizione(descrizione);
				rvAgricolo.setDtmIns(entity.getDtmIns());
				rvAgricolo.setDtmMod(entity.getDtmMod());
				rvAgricolo.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvAgricolo.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvAgricolo.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvAgricolo);
			}
		}
	}
	
	/**
	 * Persisto la collection dell'oggetto RvTrasferta
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetDispTrasferte(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);
		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvTrasferta rvTrasferta = new RvTrasferta();
				DeTrasferta deTrasferta = deTrasfertaHome.findById(str);
				rvTrasferta.setDeTrasferta(deTrasferta);
				rvTrasferta.setDtmIns(entity.getDtmIns());
				rvTrasferta.setDtmMod(entity.getDtmMod());
				rvTrasferta.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvTrasferta.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvTrasferta.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvTrasferta);
			}
		}
	}

	/**
	 * Persisto la collection dell'oggetto RvPatente
	 * 
	 * @param dto
	 *            RvTestataDTO
	 */
	private void persistSetPatente(RvTestataDTO dto, Set<String> list) {
		RvTestata entity = fromDTO(dto);

		if (list != null && !list.isEmpty()) {
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				RvPatente rvPatente = new RvPatente();
				if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
					rvPatente.setDePatenteSil(dePatenteSilHome.findById(str));
				} else {
					rvPatente.setDePatente(dePatenteHome.findById(str));
				}
				rvPatente.setDtmIns(entity.getDtmIns());
				rvPatente.setDtmMod(entity.getDtmMod());
				rvPatente.setPfPrincipalIns(entity.getPfPrincipalIns());
				rvPatente.setPfPrincipalMod(entity.getPfPrincipalMod());
				rvPatente.setRvTestata(entity);
				// effettuo la persist sul DB
				entityManager.persist(rvPatente);
			}
		}
	}

	/**
	 * Recupero un Set di record presenti al DB
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <Entity> List<Entity> findByIdRvTestata(Class<Entity> clazz, Integer id) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Entity> cq = cb.createQuery(clazz);
			Root<Entity> r = cq.from(clazz);
			Predicate p = cb.equal(r.get("rvTestata").get(RvTestata_.idRvTestata.getName()), id);
			cq.select(r).where(p);
			TypedQuery<Entity> query = entityManager.createQuery(cq);
			return query.getResultList();
		} catch (Exception e) {
			throw new EJBException("Erore durante l'utilizzo della findByIdRvTestata", e);
		}
	}

	@Override
	public void removeCascadeById(Integer id, Integer idPfPrincipalMod) {
		RvTestata t = findById(id);
		purgeParameters(t.getRvAttivitas());
		purgeParameters(t.getRvContrattos());
		purgeParameters(t.getRvLinguas());
		purgeParameters(t.getRvMansiones());
		purgeParameters(t.getRvOrarios());
		purgeParameters(t.getRvPatentes());
		purgeParameters(t.getRvTitolos());
		purgeParameters(t.getRvTrasfertas());
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
		  purgeParameters(t.getRvAgricolos());
		}
		super.removeCascadeById(id, idPfPrincipalMod);
	}

	private void purgeParameters(Set<? extends Object> list) {
		if (list != null) {
			for (Object object : list) {
				entityManager.remove(object);
			}
		}
	}

}
