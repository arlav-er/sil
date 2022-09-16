package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.OrCorsoDTO;
import it.eng.myportal.dtos.OrEdizioneAvviataDTO;
import it.eng.myportal.dtos.OrSedeCorsoDTO;
import it.eng.myportal.dtos.RicercaCorsoDTO;
import it.eng.myportal.entity.OrCorso;
import it.eng.myportal.entity.OrCorso_;
import it.eng.myportal.entity.OrEdizioneAvviata;
import it.eng.myportal.entity.OrSedeCorso;
import it.eng.myportal.entity.OrSedeCorso_;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeMansione_;
import it.eng.myportal.entity.decodifiche.DeProfessione;
import it.eng.myportal.entity.decodifiche.DeProfessione_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeQualificaSrq;
import it.eng.myportal.entity.decodifiche.DeQualificaSrq_;
import it.eng.myportal.entity.decodifiche.DeTipoFormazione;
import it.eng.myportal.entity.decodifiche.DeTipoFormazione_;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeProfessioneHome;
import it.eng.myportal.entity.home.decodifiche.DeQualificaSrqHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoFormazioneHome;

@Stateless
public class OrCorsoHome extends AbstractUpdatableHome<OrCorso, OrCorsoDTO> {

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	OrSedeCorsoHome orSedeCorsoHome;

	@EJB
	OrEnteCorsoHome orEnteCorsoHome;

	@EJB
	DeTipoFormazioneHome deTipoFormazioneHome;

	@EJB
	OrEdizioneAvviataHome orEdizioneAvviataHome;

	@EJB
	DeProfessioneHome deProfessioneHome;

	@EJB
	DeQualificaSrqHome deQualificaSrqHome;

	@Override
	public OrCorso findById(Integer id) {
		return findById(OrCorso.class, id);
	}

	@Override
	public OrCorsoDTO toDTO(OrCorso entity) {
		if (entity == null)
			return null;
		OrCorsoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdOrCorso());
		dto.setIdCorso(entity.getIdCorso());
		DeMansione deMansione = entity.getDeMansione();
		if (deMansione != null) {
			dto.setCodMansione(deMansione.getCodMansione());
			dto.setStrMansione(deMansione.getDescrizione());
		}
		DeQualificaSrq deQualificaSrq = entity.getDeQualificaSrq();
		if (deQualificaSrq != null) {
			dto.setCodQualificaSrqRilasciata(deQualificaSrq.getCodQualificaSrq());
			dto.setStrQualificaSrqRilasciata(deQualificaSrq.getDescrizione());
		}
		DeProfessione deProfessione = entity.getDeProfessione();
		if (deProfessione != null) {
			dto.setCodProfessione(deProfessione == null ? null : deProfessione.getCodProfessione());
			dto.setStrProfessione(deProfessione == null ? null : deProfessione.getDescrizione());
		}
		dto.setTitoloCorso(entity.getTitoloCorso());
		DeTipoFormazione deTipoFormazione = entity.getDeTipoFormazione();
		if (deTipoFormazione != null) {
			dto.setCodTipoFormazione(deTipoFormazione.getCodTipoFormazione());
			dto.setStrTipoFormazione(deTipoFormazione.getDescrizione());
		}
		dto.setDescrizioneCorso(entity.getDescrizioneCorso());
		dto.setIdOrEnteCorso(entity.getOrEnteCorso().getIdOrEnteCorso());
		dto.setRagioneSocialeEnte(
				orEnteCorsoHome.findById(entity.getOrEnteCorso().getIdOrEnteCorso()).getRagioneSociale());
		dto.setContenutiPercorso(entity.getContenutiPercorso());
		dto.setRequisitiAccesso(entity.getRequisitiAccesso());
		dto.setIscrizione(entity.getIscrizione());
		dto.setCriteriSelezione(entity.getCriteriSelezione());
		dto.setDtAvvio(entity.getDtAvvio());
		dto.setAttestatoRilascio(entity.getAttestatoRilascio());
		dto.setDurata(entity.getDurata());
		dto.setPeriodoSvolgimento(entity.getPeriodoSvolgimento());
		dto.setNumeroPartecipanti(entity.getNumeroPartecipanti());
		dto.setQuotaIscrizioneIndividuale(entity.getQuotaIscrizioneIndividuale());
		dto.setAmministrazioneCompetente(entity.getAmministrazioneCompetente());
		dto.setCodiceIdentificativo(entity.getCodiceIdentificativo());
		dto.setScuolaCapofila(entity.getScuolaCapofila());
		dto.setUniversita(entity.getUniversita());
		dto.setImprese(entity.getImprese());

		List<OrSedeCorso> orSedeCorsos = new ArrayList<OrSedeCorso>(entity.getOrSedeCorsos());
		List<OrSedeCorsoDTO> orSedeCorsosDTO = null;
		dto.setNumeroSedi(orSedeCorsos.size());
		orSedeCorsosDTO = new ArrayList<OrSedeCorsoDTO>();
		for (OrSedeCorso orSedeCorso : orSedeCorsos) {
			orSedeCorsosDTO.add(orSedeCorsoHome.toDTO(orSedeCorso));
		}

		dto.setOrSedeCorsos(orSedeCorsosDTO);

		List<OrEdizioneAvviata> orEdizioneAvviatas = new ArrayList<OrEdizioneAvviata>(entity.getOrEdizioneAvviatas());
		List<OrEdizioneAvviataDTO> orEdizioneAvviataDTOs = null;
		if (orEdizioneAvviatas != null) {
			orEdizioneAvviataDTOs = new ArrayList<OrEdizioneAvviataDTO>();
			for (OrEdizioneAvviata orEdizioneAvviata : orEdizioneAvviatas) {
				orEdizioneAvviataDTOs.add(orEdizioneAvviataHome.toDTO(orEdizioneAvviata));
			}
		}
		dto.setOrEdizioneAvviatas(orEdizioneAvviataDTOs);

		return dto;
	}

	@Override
	public OrCorso fromDTO(OrCorsoDTO dto) {
		if (dto == null)
			return null;
		OrCorso entity = super.fromDTO(dto);
		entity.setIdOrCorso(dto.getId());
		entity.setIdCorso(dto.getIdCorso());
		entity.setDeMansione(deMansioneHome.findById(dto.getCodMansione()));
		entity.setDeProfessione(deProfessioneHome.findById(dto.getCodProfessione()));
		entity.setDeQualificaSrq(deQualificaSrqHome.findById(dto.getCodQualificaSrqRilasciata()));
		entity.setTitoloCorso(dto.getTitoloCorso());
		entity.setDeTipoFormazione(deTipoFormazioneHome.findById(dto.getCodTipoFormazione()));
		entity.setDescrizioneCorso(dto.getDescrizioneCorso());
		entity.setOrEnteCorso(orEnteCorsoHome.findById(dto.getIdOrEnteCorso()));
		entity.setDeTipoFormazione(deTipoFormazioneHome.findById(dto.getCodTipoFormazione()));
		entity.setContenutiPercorso(dto.getContenutiPercorso());
		entity.setRequisitiAccesso(dto.getRequisitiAccesso());
		entity.setIscrizione(dto.getIscrizione());
		entity.setCriteriSelezione(dto.getCriteriSelezione());
		entity.setDtAvvio(dto.getDtAvvio());
		entity.setAttestatoRilascio(dto.getAttestatoRilascio());
		entity.setDurata(dto.getDurata());
		entity.setPeriodoSvolgimento(dto.getPeriodoSvolgimento());
		entity.setNumeroPartecipanti(dto.getNumeroPartecipanti());
		entity.setQuotaIscrizioneIndividuale(dto.getQuotaIscrizioneIndividuale());
		entity.setAmministrazioneCompetente(dto.getAmministrazioneCompetente());
		entity.setCodiceIdentificativo(dto.getCodiceIdentificativo());
		entity.setScuolaCapofila(dto.getScuolaCapofila());
		entity.setUniversita(dto.getUniversita());
		entity.setImprese(dto.getImprese());

		List<OrSedeCorsoDTO> orSedeCorsosDTO = dto.getOrSedeCorsos();
		List<OrSedeCorso> orSedeCorsos = null;
		if (orSedeCorsosDTO != null) {
			orSedeCorsos = new ArrayList<OrSedeCorso>();
			for (OrSedeCorsoDTO orSedeCorsoDTO : orSedeCorsosDTO) {
				orSedeCorsos.add(orSedeCorsoHome.findById(orSedeCorsoDTO.getId()));
			}
			entity.setOrSedeCorsos(new HashSet<OrSedeCorso>(orSedeCorsos));
		}

		List<OrEdizioneAvviataDTO> orEdizioneAvviataDTOs = dto.getOrEdizioneAvviatas();
		List<OrEdizioneAvviata> orEdizioneAvviatas = null;
		if (orEdizioneAvviataDTOs != null) {
			orEdizioneAvviatas = new ArrayList<OrEdizioneAvviata>();
			for (OrEdizioneAvviataDTO orEdizioneAvviataDTO : orEdizioneAvviataDTOs) {
				orEdizioneAvviatas.add(orEdizioneAvviataHome.findById(orEdizioneAvviataDTO.getId()));
			}
			entity.setOrEdizioneAvviatas(new HashSet<OrEdizioneAvviata>(orEdizioneAvviatas));
		}

		return entity;
	}

	public List<OrCorsoDTO> findByFilter(RicercaCorsoDTO parRic, Integer idPfPrincipal) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrCorso> query = qb.createQuery(OrCorso.class);
		Root<OrCorso> corso = query.from(OrCorso.class);
		Join<OrCorso, DeTipoFormazione> tipi = corso.join(OrCorso_.deTipoFormazione);
		Join<OrCorso, OrSedeCorso> sedi = corso.join(OrCorso_.orSedeCorsos, JoinType.LEFT);
		Join<OrSedeCorso, DeComune> comuni = sedi.join(OrSedeCorso_.deComune, JoinType.LEFT);
		Join<DeComune, DeProvincia> province = comuni.join(DeComune_.deProvincia, JoinType.LEFT);
		Join<OrCorso, DeQualificaSrq> qualifiche = corso.join(OrCorso_.deQualificaSrq, JoinType.LEFT);
		Join<OrCorso, DeMansione> mansioni = corso.join(OrCorso_.deMansione, JoinType.LEFT);
		Join<OrCorso, DeProfessione> professioni = corso.join(OrCorso_.deProfessione, JoinType.LEFT);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		switch (parRic.getTipo()) {
		case RIF_PA:
			String codRifPA = "%" + StringUtils.trimToEmpty(parRic.getCodRifPA()).toUpperCase() + "%";
			whereConditions.add(qb.like(qb.upper(corso.get(OrCorso_.codiceIdentificativo)), codRifPA));
			break;
		case RICERCA_MULTIPLA:
			String codProvincia = StringUtils.trimToEmpty(parRic.getCodProvincia()).toUpperCase();
			if (StringUtils.isNotBlank(codProvincia)) {
				whereConditions.add(qb.equal(qb.upper(province.get(DeProvincia_.codProvincia)), codProvincia));
			}
			String codTipoFormazione = StringUtils.trimToEmpty(parRic.getCodFormazione()).toUpperCase();
			if (StringUtils.isNotBlank(codTipoFormazione)) {
				whereConditions
						.add(qb.equal(qb.upper(tipi.get(DeTipoFormazione_.codTipoFormazione)), codTipoFormazione));
			}
			String codGruppoProfessionale = StringUtils.trimToEmpty(parRic.getCodGruppoProfessionale()).toUpperCase();
			if (StringUtils.isNotBlank(codGruppoProfessionale)) {
				whereConditions.add(qb.equal(qb.upper(mansioni.get(DeMansione_.codMansione)), codGruppoProfessionale));
			}
			break;
		case RICERCA_MULTIPLA_FR:
			whereConditions.add(qb.equal(qb.upper(tipi.get(DeTipoFormazione_.codTipoFormazione)), "FR"));
			String codProvinciaFR = StringUtils.trimToEmpty(parRic.getCodProvinciaFR()).toUpperCase();
			if (StringUtils.isNotBlank(codProvinciaFR)) {
				whereConditions.add(qb.equal(qb.upper(province.get(DeProvincia_.codProvincia)), codProvinciaFR));
			}
			String codProfessione = StringUtils.trimToEmpty(parRic.getCodProfessione()).toUpperCase();
			if (StringUtils.isNotBlank(codProfessione)) {
				whereConditions.add(qb.equal(qb.upper(professioni.get(DeProfessione_.codProfessione)), codProfessione));
			}
			break;
		case RICERCA_LIBERA:
			String ricercaString = StringUtils.trimToEmpty(parRic.getRicercaLibera()).toUpperCase();
			Predicate titoloCorso = qb.like(qb.upper(corso.get(OrCorso_.titoloCorso)), "%" + ricercaString + "%");
			Predicate descrizione = qb.like(qb.upper(corso.get(OrCorso_.descrizioneCorso)), "%" + ricercaString + "%");
			Predicate srq = qb.like(qb.upper(qualifiche.get(DeQualificaSrq_.descrizione)), "%" + ricercaString + "%");

			Predicate tot = qb.or(titoloCorso, descrizione, srq);

			whereConditions.add(tot);
			break;
		default:
			break;
		}

		query.distinct(true);
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<OrCorso> q = entityManager.createQuery(query);
		return findDTOByQuery(q);
	}

	/**
	 * CodiceOrganismo è una alternate key - può esistere al più 1 record
	 * 
	 * @param idCorso
	 * @return
	 */
	public OrCorsoDTO findByIdCorso(Integer idCorso) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrCorso> c = cb.createQuery(OrCorso.class);
		Root<OrCorso> f = c.from(OrCorso.class);
		Predicate equalPredicate = cb.equal(f.get(OrCorso_.idCorso), idCorso);
		c = c.where(equalPredicate);
		TypedQuery<OrCorso> createdQuery = entityManager.createQuery(c);
		List<OrCorso> rl = createdQuery.getResultList();
		if (rl != null)
			if (!rl.isEmpty()) {
				return toDTO(rl.get(0));
			}
		return null;
	}

}
