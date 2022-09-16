package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.AppValutazioneDTO;
import it.eng.myportal.entity.AppValutazione;
import it.eng.myportal.entity.AppValutazione_;
import it.eng.myportal.entity.PfPrincipal;

/**
 * 
 * @author
 */
@Stateless
public class AppValutazioneHome extends AbstractUpdatableHome<AppValutazione, AppValutazioneDTO> {

	public AppValutazione findById(Integer id) {
		return findById(AppValutazione.class, id);
	}

	@Override
	public AppValutazioneDTO toDTO(AppValutazione entity) {
		if (entity == null)
			return null;

		AppValutazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdAppValutazione());
		dto.setNumStelle(entity.getNumStelle());
		dto.setMessaggio(entity.getMessaggio());

		// Per il mittente si ritorna l'id e l'email
		if (entity.getPfPrincipalMitt() != null) {
			dto.setIdPfPrincipalMitt(entity.getPfPrincipalMitt().getIdPfPrincipal());
			dto.setEmail(entity.getPfPrincipalMitt().getEmail());
		}

		return dto;
	}

	@Override
	public AppValutazione fromDTO(AppValutazioneDTO dto) {
		if (dto == null)
			return null;
		AppValutazione entity = super.fromDTO(dto);

		entity.setIdAppValutazione(dto.getId());

		if (dto.getIdPfPrincipalMitt() != null) {
			// Recupero del PfPrincipal attraverso l'id.
			entity.setPfPrincipalMitt(pfPrincipalHome.findById(dto.getIdPfPrincipalMitt()));
		}

		entity.setNumStelle(dto.getNumStelle());

		if (StringUtils.isNotBlank(dto.getMessaggio())) {
			entity.setMessaggio(dto.getMessaggio());
		}

		return entity;
	}

	public void persist(Short numStelle, String messaggio, PfPrincipal pfPrincipalMitt, PfPrincipal pfPrincipal) {
		AppValutazione appValutazione = new AppValutazione();

		Date now = new Date();
		appValutazione.setDtmIns(now);
		appValutazione.setDtmMod(now);
		appValutazione.setMessaggio(StringUtils.abbreviate(messaggio, 1000));
		appValutazione.setNumStelle(numStelle);

		appValutazione.setPfPrincipalMitt(pfPrincipalMitt);
		appValutazione.setPfPrincipalIns(pfPrincipal);
		appValutazione.setPfPrincipalMod(pfPrincipal);

		super.persist(appValutazione);
	}

	public List<AppValutazioneDTO> getValutazioni() {
		return this.getValutazioni(null /* start */, null /* rows */);
	}

	public List<AppValutazioneDTO> getValutazioni(Integer start, Integer rows) {
		TypedQuery<AppValutazione> query = entityManager.createNamedQuery("appValutazione.getValutazioni",
				AppValutazione.class);

		if (start != null && rows != null) {
			query.setFirstResult(start);
			query.setMaxResults(rows);
		}
		return findDTOByQuery(query);
	}

	public List<AppValutazioneDTO> getValutazioni(Short numStelle, boolean escludiAnonime, boolean soloAnonime,
			Date dtaDa, Date dtaA, Integer start, Integer rows) {

		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AppValutazione> query = cb.createQuery(AppValutazione.class);

		// Creo la clausola FROM della query.
		Root<AppValutazione> appValutazione = query.from(AppValutazione.class);

		// Creo la clausola SELECT della query
		query.select(appValutazione);

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(numStelle, escludiAnonime, soloAnonime, dtaDa, dtaA,
				appValutazione);

		// Order by idAppValutazione.
		query.orderBy(cb.desc(appValutazione.get(AppValutazione_.idAppValutazione)));

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<AppValutazione> tquery = entityManager.createQuery(query);

		if (start != null && rows != null) {
			tquery.setFirstResult(start);
			tquery.setMaxResults(rows);
		}
		return findDTOByQuery(tquery);
	}

	public Long getCountValutazioni() {
		TypedQuery<Long> query = entityManager.createNamedQuery("appValutazione.getCountValutazioni", Long.class);

		return query.getSingleResult();
	}

	public Long getCountValutazione(Short numStelle, boolean escludiAnonime, boolean soloAnonime, Date dtaDa,
			Date dtaA) {

		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Creo la clausola FROM della query.
		Root<AppValutazione> appValutazione = query.from(AppValutazione.class);

		// Creo la clausola SELECT della query
		query.select(cb.count(appValutazione));

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(numStelle, escludiAnonime, soloAnonime, dtaDa, dtaA,
				appValutazione);

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);

		// Eseguo la query e restituisco il risultato
		return tquery.getSingleResult();
	}

	private List<Predicate> createWhereConditions(Short numStelle, boolean escludiAnonime, boolean soloAnonime,
			Date dtaDa, Date dtaA, Root<AppValutazione> appValutazione) {

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		if (numStelle != null) {
			Predicate numStelleCondizione = cb.equal(appValutazione.get(AppValutazione_.numStelle), numStelle);
			whereConditions.add(numStelleCondizione);

		}

		if (escludiAnonime) {
			Predicate escludiAnonimeCondizione = cb.isNotNull(appValutazione.get(AppValutazione_.pfPrincipalMitt));
			whereConditions.add(escludiAnonimeCondizione);
		}

		if (soloAnonime) {
			Predicate soloAnonimeCondizione = cb.isNull(appValutazione.get(AppValutazione_.pfPrincipalMitt));
			whereConditions.add(soloAnonimeCondizione);
		}

		// Condizioni sulle date di validita
		if (dtaDa != null) {
			whereConditions.add(cb.greaterThanOrEqualTo(appValutazione.get(AppValutazione_.dtmIns), dtaDa));
		}

		if (dtaA != null) {
			// Incremento di un giorno per la gestione dell'orario
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtaA);
			cal.add(Calendar.DAY_OF_MONTH, 1);

			whereConditions.add(cb.lessThan(appValutazione.get(AppValutazione_.dtmIns), cal.getTime()));
		}

		return whereConditions;
	}
}
