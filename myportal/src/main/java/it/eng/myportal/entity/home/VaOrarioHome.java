package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.VaOrarioDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;

/**
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaOrarioHome extends AbstractVacancyEntityListHome<VaOrario, VaOrarioDTO> implements InoDTOejb<VaOrario> {

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	public VaOrario findById(Integer id) {
		return findById(VaOrario.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaOrario> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaOrarios();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaOrarioDTO toDTO(VaOrario entity) {
		if (entity == null)
			return null;
		VaOrarioDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaOrario());

		if (entity.getDeOrario() != null) {
			dto.setCodice(entity.getDeOrario().getCodOrario());
			dto.setDescrizione(entity.getDeOrario().getDescrizione());
		} else if (entity.getDeOrarioSil() != null) {
			dto.setCodice(entity.getDeOrarioSil().getCodOrarioSil());
			dto.setDescrizione(entity.getDeOrarioSil().getDescrizione());

		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto
	 *            VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public VaOrario fromDTO(VaOrarioDTO dto) {
		if (dto == null)
			return null;
		VaOrario entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaOrario(dto.getId());

		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDeOrarioSil(deOrarioSilHome.findById(dto.getCodice()));
		} else {
			entity.setDeOrario(deOrarioHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaOrario vaOrario, VaDatiVacancy vaDatiVacancy) {
		if (vaOrario != null) {
			Date now = new Date();
			entityManager.detach(vaOrario);
			vaOrario.setIdVaOrario(null);
			vaOrario.setVaDatiVacancy(vaDatiVacancy);
			vaOrario.setDtmIns(now);
			vaOrario.setDtmMod(now);
			vaOrario.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaOrario.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaOrario);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_orario associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	public List<VaOrario> findProperByVacancyId(Integer vacancyId) {
		List<VaOrario> deQualificaSrqList = entityManager.createNamedQuery("VaOrarioFindByVacancyId", VaOrario.class)
				.setParameter("vaId", vacancyId).getResultList();

		return deQualificaSrqList;
	}

	@Override
	public VaOrario merge(VaOrario in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}
	
	@Override
	public void remove(VaOrario detached) {
		VaOrario persistentInstance = findById(detached.getIdVaOrario());
		super.remove(persistentInstance);
	}

	@Override
	public VaOrario persist(VaOrario entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}
}
