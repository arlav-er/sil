package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.CvVisualizzaLetteraAccDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidatura_;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.CvLetteraAcc_;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaDatiVacancy_;
import it.eng.myportal.exception.MyPortalException;

/**
 * Home object for domain model class CvLetteraAcc.
 * 
 * @see it.eng.myportal.entity.CvLetteraAcc
 * @author Rodi A.
 */
@Stateless
public class CvLetteraAccHome extends AbstractUpdatableHome<CvLetteraAcc, CvLetteraAccDTO> {

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@Override
	public CvLetteraAcc findById(Integer id) {
		return findById(CvLetteraAcc.class, id);
	}

	@Override
	public CvLetteraAccDTO toDTO(CvLetteraAcc entity) {
		if (entity == null) {
			return null;
		}
		CvLetteraAccDTO dto = super.toDTO(entity);

		dto.setBenefici(entity.getBenefici());
		dto.setBrevePresentazione(entity.getBrevePresentazione());
		dto.setId(entity.getIdCvLetteraAcc());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setMotivazObiettivi(entity.getMotivazObiettivi());
		dto.setNome(entity.getNome());
		dto.setPuntiForzaQualita(entity.getPuntiForzaQualita());
		dto.setSaluti(entity.getSaluti());
		dto.setFlagInviato(entity.getFlagInviato());
		dto.setReferenziatoInMsg(entity.getMsgMessaggios().size() > 0);

		return dto;
	}

	@Override
	public CvLetteraAcc fromDTO(CvLetteraAccDTO dto) throws MyPortalException {
		CvLetteraAcc entity = null;
		// 50 e' il limite della colonna sul DB
		if (dto.getNome().length() > 50) {
			throw new MyPortalException("lettere.error.longname");
		} else {
		
			entity = super.fromDTO(dto);
	
			entity.setBenefici(dto.getBenefici());
			entity.setBrevePresentazione(dto.getBrevePresentazione());
			entity.setIdCvLetteraAcc(dto.getId());
			entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
			entity.setMotivazObiettivi(dto.getMotivazObiettivi());
			entity.setNome(dto.getNome());
			entity.setPuntiForzaQualita(dto.getPuntiForzaQualita());
			entity.setSaluti(dto.getSaluti());
			entity.setFlagInviato(dto.getFlagInviato());
		}
		return entity;
	}

	public long getNumLetteraAccByIdPfPrincipal(Integer idPfPrincipal) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<CvLetteraAcc> f = c.from(CvLetteraAcc.class);
		Join<CvLetteraAcc, PfPrincipal> cvJoinUtente = f.join(CvLetteraAcc_.pfPrincipal);
		Predicate equalPredicate = cb.equal(cvJoinUtente.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		c = c.select(cb.count(f));
		c = c.where(equalPredicate);
		Long singleResult = entityManager.createQuery(c).getSingleResult();
		long count = singleResult.longValue();
		return count;
	}

	public CvLetteraAccDTO copyById(Integer pfPrincipalId, Integer id, String nome) {
		return copyById(pfPrincipalId, id, nome, Boolean.FALSE);
	}

	/**
	 * @param id
	 *            della lettera da copiare
	 * @param nome
	 *            nome della nuova lettera
	 * @return id della lettera copiata
	 */
	public CvLetteraAccDTO copyById(Integer pfPrincipalId, Integer id, String nome, Boolean flagInviato) {
		Date now = new Date();
		CvLetteraAcc cvLetteraAcc = findById(id);
		if (nome.length() > 50) {
			throw new MyPortalException("lettere.error.copying.longname");
		}
		if (cvLetteraAcc != null) {
			entityManager.detach(cvLetteraAcc);
			cvLetteraAcc.setIdCvLetteraAcc(null);
			cvLetteraAcc.setNome(nome);
			cvLetteraAcc.setFlagInviato(flagInviato);
			cvLetteraAcc.setAcCandidaturas(new HashSet<AcCandidatura>(0));
			cvLetteraAcc.setDtmIns(now);
			cvLetteraAcc.setDtmMod(now);
			cvLetteraAcc.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvLetteraAcc.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

			persist(cvLetteraAcc);

			return toDTO(cvLetteraAcc);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a cv_lettera_acc con id " + id);
		}
	}

	/**
	 * Estrae il DTO per la visualizzazione di una lettera di accompagnamento.
	 * Il metodo è protetto e solo alcuni utenti possono visualizzare le lettere
	 * di accompagnamento.
	 * 
	 * @param idLettera
	 *            identificativo della lettera di accompagnamento
	 * @param pfPrincipalId
	 *            utente che vuole visualizzare la lettera
	 * @return il DTO per visualizzare la lettera di accompagnamento
	 */
	public CvVisualizzaLetteraAccDTO findVisualizzaLetteraAccompagnamentoDTOById(Integer idLettera,
			Integer pfPrincipalId) {

		CvLetteraAcc lettera = findById(idLettera);
		CvLetteraAccDTO cvLetteraAccDTO = toDTO(lettera);
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(pfPrincipalId);
		PfPrincipal pfPrincipal = lettera.getPfPrincipal();
		// se è un utente che richiede il dto per visualizzare la lettera, deve
		// essere la sua!
		if (pfPrincipalRequest.isUtente()) {
			if (!cvLetteraAccDTO.isProprietary(pfPrincipalRequest.getIdPfPrincipal())) {
				throw new MyPortalException("lettere.insufficient_permission");
			}
		}
		// se è un'azienda che richiede di visualizzarla, deve esserle stata
		// inviata!
		else if (pfPrincipalRequest.isAzienda()) {
			AziendaInfo aziendaInfo = pfPrincipalRequest.getAziendaInfo();
			if (!canAziendaViewLettera(aziendaInfo.getIdPfPrincipal(), idLettera)) {
				throw new MyPortalException("lettere.insufficient_permission");
			}
		}
		// la provincia deve essere competente per il lavoratore.
		else if (pfPrincipalRequest.isProvincia()) {
			Provincia provincia = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();
			if (!utenteInfoHome.isProvinciaCompetente(provincia, pfPrincipal.getIdPfPrincipal())) {
				throw new MyPortalException("lettere.insufficient_permission");
			}
		}

		CvVisualizzaLetteraAccDTO cvVisualizzaLetteraAccDTO = new CvVisualizzaLetteraAccDTO();

		cvVisualizzaLetteraAccDTO.setId(lettera.getIdCvLetteraAcc());
		cvVisualizzaLetteraAccDTO.setIdPfPrincipal(lettera.getPfPrincipal().getIdPfPrincipal());
		cvVisualizzaLetteraAccDTO.setNome(lettera.getNome());
		cvVisualizzaLetteraAccDTO.setBrevePresentazione(lettera.getBrevePresentazione());
		cvVisualizzaLetteraAccDTO.setMotivazObiettivi(lettera.getMotivazObiettivi());
		cvVisualizzaLetteraAccDTO.setBenefici(lettera.getBenefici());
		cvVisualizzaLetteraAccDTO.setPuntiForzaQualita(lettera.getPuntiForzaQualita());
		cvVisualizzaLetteraAccDTO.setSaluti(lettera.getSaluti());
		cvVisualizzaLetteraAccDTO.setFlagInviato(lettera.getFlagInviato());

		PfPrincipal principal = lettera.getPfPrincipal();
		cvVisualizzaLetteraAccDTO.setNomeCandidato(principal.getNome());
		cvVisualizzaLetteraAccDTO.setCognomeCandidato(principal.getCognome());

		return cvVisualizzaLetteraAccDTO;
	}

	/**
	 * Determina se un'azienda può visualizzare la lettera in base al fatto se
	 * questa le è stata inviata o meno.
	 * 
	 * @param pfPrincipalRequest
	 * @param idLettera
	 * @return
	 */
	private boolean canAziendaViewLettera(Integer idPfPrincipalAzienda, Integer idLettera) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> c = cb.createQuery(AcCandidatura.class);
		Root<AcCandidatura> acCandidatura = c.from(AcCandidatura.class);

		Join<AcCandidatura, CvLetteraAcc> cvLetteraAcc = acCandidatura.join(AcCandidatura_.cvLetteraAcc);
		Join<AcCandidatura, PfPrincipal> aziendaInfoVa = acCandidatura.join(AcCandidatura_.pfPrincipal, JoinType.LEFT);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy,
				JoinType.LEFT);

		Join<VaDatiVacancy, PfPrincipal> aziendaInfoCv = vaDatiVacancy.join(VaDatiVacancy_.pfPrincipal, JoinType.LEFT);
		Join<VaDatiVacancy, PfPrincipal> aziendaInfoPalese = vaDatiVacancy.join(VaDatiVacancy_.pfPrincipalPalese,
				JoinType.LEFT);

		Predicate vetrinaCondition = cb.equal(aziendaInfoVa.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda);
		Predicate idAziendaInfoPaleseCondition = cb.equal(aziendaInfoPalese.get(PfPrincipal_.idPfPrincipal),
				idPfPrincipalAzienda);
		Predicate aziendaCondition = cb.or(vetrinaCondition, idAziendaInfoPaleseCondition);
		Predicate vaCondition = cb.equal(aziendaInfoCv.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda);
		Predicate azCondition = cb.or(aziendaCondition, vaCondition);
		Predicate cvCondition = cb.equal(cvLetteraAcc.get(CvLetteraAcc_.idCvLetteraAcc), idLettera);

		c = c.where(azCondition, cvCondition);
		List<AcCandidatura> list = entityManager.createQuery(c).getResultList();
		return list.size() > 0;
	}

	@Override
	public void removeCascadeById(Integer id, Integer idPfPrincipalMod) {
		CvLetteraAcc entity = findById(id);

		removeReferencesFromMessages(entity, id);

		removeById(id, idPfPrincipalMod);
	}

	/*
	 * Controlla se la lettera e' referenziata da qualche messaggio e in caso ne
	 * elimina i riferimenti.
	 */
	private void removeReferencesFromMessages(CvLetteraAcc entity, Integer idCvLetteraAcc) {
		Set<MsgMessaggio> msgMessaggios = entity.getMsgMessaggios();
		for (MsgMessaggio msgMessaggio : msgMessaggios) {
			Set<CvLetteraAcc> lettere = msgMessaggio.getLettere();
			for (CvLetteraAcc lettera : lettere) {
				if (lettera.getIdCvLetteraAcc().equals(idCvLetteraAcc)) {
					msgMessaggioHome.removeLetteraReference(msgMessaggio.getIdMsgMessaggio(), idCvLetteraAcc);
				}
			}
		}
	}

	public List<CvLetteraAcc> findByIdPfPrincipal(Integer idPfPrincipal) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvLetteraAcc> query = cb.createQuery(CvLetteraAcc.class);
		Root<CvLetteraAcc> f = query.from(CvLetteraAcc.class);
		Join<CvLetteraAcc, PfPrincipal> cvJoinPrincipal = f.join(CvLetteraAcc_.pfPrincipal);
		Predicate equalPredicate = cb.equal(cvJoinPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		query.where(equalPredicate);
		query.orderBy(cb.desc(f.get(CvLetteraAcc_.dtmMod)));
		return entityManager.createQuery(query).getResultList();
	}

	public List<CvLetteraAccDTO> findDTOByIdPfPrincipal(Integer idPfPrincipal) {
		List<CvLetteraAccDTO> result = new ArrayList<CvLetteraAccDTO>();

		List<CvLetteraAcc> list = findByIdPfPrincipal(idPfPrincipal);

		for (CvLetteraAcc cvLetteraAcc : list) {
			CvLetteraAccDTO cvLetteraAccDTO = toDTO(cvLetteraAcc);
			result.add(cvLetteraAccDTO);
		}

		return result;
	}

}
