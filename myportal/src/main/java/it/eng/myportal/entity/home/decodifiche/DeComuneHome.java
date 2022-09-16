package it.eng.myportal.entity.home.decodifiche;

// Generated Aug 10, 2011 5:09:38 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

/**
 * Home object for domain model class DeComune.
 * 
 * @see it.eng.myportal.dtos.it.eng.myportal.dtos.decodifiche.DeComuneDTO
 * @author Rodi A., Enrico D'Angelo
 */
@Stateless
public class DeComuneHome extends AbstractSuggestibleHome<DeComune, DeComuneDTO> {

	protected static final int LIMITE_RISULTATI_RICERCA = 10;

	private static final String QUERY_PUNTUALE_SEL_BY_DESCR = " select c from DeComune c where upper(c.denominazione) = upper(:denominazione)";

	private static final String QUERY_SEL_BY_DESCR = " select c from DeComune c where upper(c.denominazione) like :denominazione";

	protected static final String TS_QUERY = " SELECT e.* FROM de_comune e "
			+ " WHERE to_tsvector('italian', upper(e.denominazione)) @@ to_tsquery('italian', upper(:denom)) "
			+ " ORDER BY num_popolazione desc, denominazione LIMIT " + LIMITE_RISULTATI_RICERCA;

	private static final String TS_QUERY_REGIONE = " SELECT e.* FROM de_comune e join de_provincia p on e.cod_provincia = p.cod_provincia"
			+ " WHERE to_tsvector('italian', upper(e.denominazione)) @@ to_tsquery('italian', upper(:denom)) "
			+ " AND p.cod_regione = :cod_regione "
			+ " ORDER BY num_popolazione desc, e.denominazione LIMIT "
			+ LIMITE_RISULTATI_RICERCA;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeRegioneHome deRegioneHome;

	public DeComune findById(String id) {
		return findById(DeComune.class, id);
	}

	public DeComune findByDenominazione(String denominazione) {
		log.debug("getting DeComune instance with name: " + denominazione);
		denominazione = this.rimuoviTargaProvincia(denominazione);
		DeComune instance = null;
		try {
			TypedQuery<DeComune> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeComune.class);

			// query.setParameter("denominazione", denominazione.replace("'",
			// "''"));
			query.setHint("org.hibernate.cacheable", true).setParameter("denominazione", denominazione.toUpperCase());
			instance = findById(((DeComune) query.getSingleResult()).getCodCom());
			log.debug("get DeComune successful");

		} catch (NoResultException nre) {
			log.error("Non è possibile trovare un'istanza deComune per la seguente query: " + denominazione
					+ " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di recupero dell'istanza deComune: " + re.getMessage());
		}
		return instance;
	}

	public DeComuneDTO findDTOById(String id) {
		DeComune comune = this.findById(id);
		return this.toDTO(comune);
	}

	public DeComuneDTO findDTOByDenominazione(String denominazione) {
		log.debug("getting DeComune instance with name: " + denominazione);
		denominazione = this.rimuoviTargaProvincia(denominazione);
		DeComune instance = null;
		try {
			TypedQuery<DeComune> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeComune.class).setHint(
					"org.hibernate.cacheable", true);

			// query.setParameter("denominazione", denominazione.replace("'",
			// "''"));
			query.setParameter("denominazione", denominazione.toUpperCase());
			instance = findById(((DeComune) query.getSingleResult()).getCodCom());
			log.debug("get DeComune successful");

		} catch (NoResultException nre) {
			log.error("Nessun risultato trovato findDTOByDenominazione(): " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore RuntimeException in findDTOByDenominazione(): " + re.getMessage());
		}
		return toDTO(instance);
	}

	@Override
	public DeComuneDTO toDTO(DeComune entity) {
		if (entity == null)
			return null;

		DeComuneDTO dto = super.toDTO(entity);
		dto.setCap(entity.getCap());
		dto.setId(entity.getCodCom());
		this.valorizzaDescrizioneComuneDTO(dto, entity);
		dto.setIdProvincia(entity.getDeProvincia().getCodProvincia());
		dto.setStato(entity.isStato());
		return dto;
	}

	public void valorizzaDescrizioneComuneDTO(DeComuneDTO dto, DeComune entity) {
		dto.setDescrizione(new StringBuilder(entity.getDenominazione()).append(" (")
				.append(entity.getDeProvincia().getTarga()).append(")").toString());
	}

	@Override
	public DeComune fromDTO(DeComuneDTO dto) {
		if (dto == null)
			return null;
		DeComune entity = super.fromDTO(dto);
		entity.setCap(dto.getCap());
		entity.setCodCom(dto.getId());
		entity.setDenominazione(dto.getDescrizione());
		entity.setDeProvincia(deProvinciaHome.findById(dto.getIdProvincia()));
		return entity;
	}

	@Override
	public String getFieldName() {
		return "denominazione";
	}

	/**
	 * 
	 * Restituisce la provincia di riferimento di un comune a partire dalla sua denominazione.
	 * 
	 * @param denominazione
	 * @return la provincia di riferimento
	 */
	public DeProvinciaDTO findProvinciaDTOByComune(String denominazione) {
		return findProvinciaDTOByComune(denominazione, null);
	}

	/**
	 * Restituisce la provincia in base al comune. Se viene passato anche il codice del comune effettua un controllo in
	 * base alz quale la denominazione e l'id devono corrispondere allo stesso comune.
	 * 
	 * @param denominazione
	 *            denominazione del comune per il quale si cerca la provincia
	 * @param id
	 *            id del comune. lo utilizza solo se presente.
	 * @return la provincia di riferimento
	 */
	public DeProvinciaDTO findProvinciaDTOByComune(String denominazione, String id) {
		denominazione = this.rimuoviTargaProvincia(denominazione);
		DeProvincia provincia = null;
		// se viene passato l'id cerco prima per id (bugfix per comuni
		// duplicati!)
		DeComune comune2 = null;
		if (StringUtils.isNotBlank(id)) {
			comune2 = findById(id);
			// se la denominazione è uguale a quella passata sono a posto
			if (comune2.getDenominazione().toUpperCase().equals(denominazione.toUpperCase())) {
				provincia = comune2.getDeProvincia();
			}
		} else {
			// NON ho il codice trov il comune a partire dalla denominazione
			DeComune comune1 = findByDenominazione(denominazione.toUpperCase());
			if (comune1 != null) {
				provincia = comune1.getDeProvincia();
			}
		}

		if (provincia == null)
			throw new EJBException("Impossibile trovare la provincia.");

		return deProvinciaHome.toDTO(provincia);
	}

	/**
	 * Rimuovo eventuale indicazione della provincia nella descrizione del comune
	 * 
	 * @param descrizioneComune
	 * @return
	 */
	private String rimuoviTargaProvincia(String descrizioneComune) {
		if (StringUtils.isNotBlank(descrizioneComune))
			descrizioneComune = descrizioneComune.replaceAll("\\([A-Z]*\\)?", "").trim();
		return descrizioneComune;
	}

	private DeProvinciaDTO findProvinciaDTOByComuneId(String codComune) {
		return deProvinciaHome.toDTO(this.findProvinciaByCodComune(codComune));
	}

	public DeProvincia findProvinciaByCodComune(String codComune) {
		DeComune comune = findById(codComune);

		DeProvincia deProvincia = comune.getDeProvincia();

		return deProvincia;
	}

	public DeRegioneDTO findRegioneDTOByComuneId(String comuneId) {
		DeComune comune = this.findById(comuneId);
		DeRegioneDTO regioneDTO = deRegioneHome.findDTOById(comune.getDeProvincia().getDeRegione().getCodRegione());
		return regioneDTO;
	}

	/**
	 * @param par
	 *            String termine per la ricerca dei suggerimenti
	 * @return List<DTO> lista suggerimenti per elemento autocomplete
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#findBySuggestion(java.lang.String)
	 */
	@Override
	public List<DeComuneDTO> findBySuggestion(String par) {

		List<DeComune> distinctResultList = findComuneAutocomplete(par);

		List<DeComuneDTO> resultListDTO = new ArrayList<DeComuneDTO>();
		for (DeComune comune : distinctResultList) {
			// TODO: Pezza temporanea: solo il VDA, salto i comuni non validi.
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) {
				if (comune.getDtInizioVal().after(new Date()) || comune.getDtFineVal().before(new Date())) {
					continue;
				}
			}

			resultListDTO.add(toDTO(comune));
		}

		return resultListDTO;
	}

	/**
	 * Query vecchia, return nuova non c'è DTO, invocata dal bean di autocomplete
	 * 
	 * @see AutoCompleteBean
	 * @author pegoraro
	 */
	public List<DeComune> findComuneAutocomplete(String par) {
		String queryString = TS_QUERY;

		List<DeComune> distinctResultList = new ArrayList<DeComune>();

		// Rimuovo tutti i caratteri che verrebbero rifiutati dalla query.
		par = this.rimuoviTargaProvincia(par);
		par = this.checkSqlParameter(par);

		// ricerca puntatuale
		Map<String, DeComune> distinctResultMap = this.ricercaPuntualeComunePerDescrizione(par);
		for (Entry<String, DeComune> entry : distinctResultMap.entrySet()) {
			distinctResultList.add(entry.getValue());
		}
		// ricerca suggerita
		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeComune.class).setHint("org.hibernate.cacheable",
				true);
		query.setParameter("denom", queryPar);

		List<DeComune> tmpList = query.getResultList();
		// elimino eventuali duplicati
		for (DeComune comune : tmpList) {
			if (!distinctResultMap.containsKey(comune.getCodCom())
					&& distinctResultList.size() < LIMITE_RISULTATI_RICERCA)
				distinctResultList.add(comune);
		}

		return distinctResultList;
	}

	public List<DeComuneDTO> findValideBySuggestion(String par) {

		List<DeComune> distinctResultList = findComuneValidiAutocomplete(par);

		List<DeComuneDTO> resultListDTO = new ArrayList<DeComuneDTO>();
		for (DeComune comune : distinctResultList) {
			resultListDTO.add(toDTO(comune));
		}

		return resultListDTO;
	}

	public List<DeComune> findComuneValidiAutocomplete(String par) {
		Date oggi = Calendar.getInstance().getTime();
		String queryString = TS_QUERY;

		List<DeComune> distinctResultList = new ArrayList<DeComune>();

		// Rimuovo tutti i caratteri che verrebbero rifiutati dalla query.
		par = this.rimuoviTargaProvincia(par);
		par = this.checkSqlParameter(par);

		// ricerca puntatuale
		Map<String, DeComune> distinctResultMap = this.ricercaPuntualeComunePerDescrizione(par);
		for (Entry<String, DeComune> entry : distinctResultMap.entrySet()) {
			if ((!oggi.before(entry.getValue().getDtInizioVal())) && (!oggi.after(entry.getValue().getDtFineVal()))) {
				distinctResultList.add(entry.getValue());
			}
		}
		// ricerca suggerita
		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeComune.class).setHint("org.hibernate.cacheable",
				true);
		query.setParameter("denom", queryPar);

		List<DeComune> tmpList = query.getResultList();

		// elimino eventuali duplicati e comuni non validi
		for (DeComune comune : tmpList) {
			if ((!distinctResultMap.containsKey(comune.getCodCom()))
					&& (distinctResultList.size() < LIMITE_RISULTATI_RICERCA)
					&& (!oggi.before(comune.getDtInizioVal())) && (!oggi.after(comune.getDtFineVal())))
				distinctResultList.add(comune);
		}

		return distinctResultList;
	}

	protected Map<String, DeComune> ricercaPuntualeComunePerDescrizione(String par) {
		par = this.rimuoviTargaProvincia(par);
		Map<String, DeComune> distinctResultMap = new HashMap<String, DeComune>();
		TypedQuery<DeComune> queryPuntuale = entityManager.createQuery(QUERY_PUNTUALE_SEL_BY_DESCR, DeComune.class);
		queryPuntuale.setParameter("denominazione", par);
		List<DeComune> queryPuntualeResultLists = queryPuntuale.getResultList();
		for (DeComune comune : queryPuntualeResultLists) {
			distinctResultMap.put(comune.getCodCom(), comune);
		}
		return distinctResultMap;
	}

	/**
	 * @param par
	 *            String termine per la ricerca dei suggerimenti
	 * @return List<DTO> lista suggerimenti per elemento autocomplete
	 * 
	 *         Uguale alla findBySuggestion ma filtra per il codice regione corrente in const.properties
	 */
	@SuppressWarnings("unchecked")
	public List<DeComuneDTO> findBySuggestionRegione(String par) {
		String queryString = TS_QUERY_REGIONE;

		/* Tiro via i ":" perchè carattere speciale */
		par = par.replace(':', ' ');
		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeComune.class);
		query.setParameter("cod_regione", ConstantsSingleton.COD_REGIONE.toString());
		query.setParameter("denom", queryPar);
		query.setHint("org.hibernate.cacheable", true);

		List<DeComune> resultList = query.getResultList();
		List<DeComuneDTO> resultListDTO = new ArrayList<DeComuneDTO>();
		if (resultList != null) {
			for (DeComune comune : resultList) {
				resultListDTO.add(toDTO(comune));
			}
		}

		return resultListDTO;
	}

	/**
	 * Verifica se il comune passatogli come parametro e' in regione EMILIA-ROMAGNA o no. (IN REALTà CONTROLLA SE IL
	 * COMUNE è NELLA REGIONE CONFIGURATA NEL PARAMETRO const.properties.regione.cod)
	 * 
	 * @param comuneDomicilio
	 *            denominazione del comune
	 * @param codComuneDomicilio
	 *            codice del comune
	 * @return true se il comune passato come parametro e' in regione EMILIA-ROMAGNA, false altrimenti
	 */
	public boolean comuneDomicilioInRER(String comuneDomicilio, String codComuneDomicilio) {
		DeProvinciaDTO provinciaDomicilio = null;
		boolean domicilioInRER = false;
		// se c'è il codice cerco il comune direttamente in base al codice (ora
		// nelle descrizionei recuperate dalla combo dei suggerimento c'è anche
		// la targa della provincia!!!)
		if (StringUtils.isNotBlank(codComuneDomicilio)) {
			provinciaDomicilio = this.findProvinciaDTOByComuneId(codComuneDomicilio);
		} else if (StringUtils.isNotBlank(comuneDomicilio)) {
			// altrimenti cerco per descrizione
			provinciaDomicilio = findProvinciaDTOByComune(comuneDomicilio, codComuneDomicilio);
		}
		if (provinciaDomicilio != null) {
			domicilioInRER = ConstantsSingleton.COD_REGIONE.toString().equals(provinciaDomicilio.getIdRegione());
		}
		return domicilioInRER;
	}

	public List<DeComuneDTO> findNazioniValideBySuggestion(String par) {
		TypedQuery<DeComune> query = entityManager.createNamedQuery("findNazioneValidaBySuggestion", DeComune.class)
				.setParameter("par", "%" + par.trim().toUpperCase() + "%").setParameter("data", new Date());
		List<DeComune> entityList = query.getResultList();
		List<DeComuneDTO> dtoList = new ArrayList<DeComuneDTO>(entityList.size());
		for (DeComune entity : entityList) {
			dtoList.add(toDTO(entity));
		}

		return dtoList;
	}

}
