package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.local.IVaContattoHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

/**
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaContattoHome extends AbstractVacancyEntityHome<VaContatto, VaContattoDTO>
		implements IVaContattoHome, InoDTOejb<VaContatto> {

	@EJB
	DeAlboHome deContattoHome;

	public VaContatto findById(Integer id) {
		return findById(VaContatto.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id della vacancy Restituisce il contatto primario.
	 *
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public VaContatto findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaContattoPrinc();
	}

	/**
	 * Recupera i dati partendo dall'id della vacancy Restituisce il contatto primario.
	 *
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public VaContatto findPrincByVacancyId(int vacancyId) throws MyPortalNoResultFoundException {

		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(vacancyId);
		if (vacancy.getVaContattoPrinc() == null)
			throw new MyPortalNoResultFoundException();
		return findById(vacancy.getVaContattoPrinc().getIdVaContatto());
	}

	@Override
	public VaContattoDTO findAltDTOByVacancyId(Integer vacancyId) {
		VaContattoDTO dto = null;
		VaContatto entity = null;
		try {
			entity = findAltByVacancyId(vacancyId);
		} catch (MyPortalNoResultFoundException e) {
			log.warn("Attenzione: vacancy mancante del contatto alternativo vacancy con id: " + vacancyId);
		}

		if (entity != null) {
			dto = toDTO(entity);
		}

		if (dto != null) { // se esiste gia' un contatto principale allora questo e' attivo
			if (findByVacancyId(vacancyId) != null) {
				dto.setActive(true);
			}
		}

		return dto;
	}

	/**
	 * Recupera i dati partendo dall'id della vacancy Restituisce il contatto alternativo.
	 *
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 * @throws MyPortalNoResultFoundException
	 */
	public VaContatto findAltByVacancyId(int vacancyId) throws MyPortalNoResultFoundException {
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(vacancyId);
		if (vacancy.getVaContattoAlt() == null)
			throw new MyPortalNoResultFoundException();
		return findById(vacancy.getVaContattoAlt().getIdVaContatto());
	}

	/**
	 * Converte un'Entity in un DTO.
	 *
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaContattoDTO toDTO(VaContatto entity) {
		if (entity == null)
			return null;
		VaContattoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaContatto());

		Set<VaDatiVacancy> princs = entity.getVaDatiVacanciesForIdVaContattoPrinc();
		if (!princs.isEmpty()) {
			dto.setIdVaDatiVacancy(princs.iterator().next().getIdVaDatiVacancy());
		} else {
			Set<VaDatiVacancy> secs = entity.getVaDatiVacanciesForIdVaContattoAlt();
			if (!secs.isEmpty()) {
				dto.setIdVaDatiVacancy(secs.iterator().next().getIdVaDatiVacancy());
			}
		}

		dto.setCognomeRiferimento(entity.getCognomeRiferimentoPub());
		dto.setFaxRiferimento(entity.getFaxRiferimentoPub());
		dto.setMail(entity.getMail());
		dto.setNomeRiferimento(entity.getNomeRiferimentoPub());
		dto.setTelRiferimento(entity.getTelRiferimentoPub());

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
	public VaContatto fromDTO(VaContattoDTO dto) {
		if (dto == null)
			return null;
		VaContatto entity = super.fromDTO(dto);
		entity.setIdVaContatto(dto.getId());
		entity.setCognomeRiferimentoPub(dto.getCognomeRiferimento());
		entity.setFaxRiferimentoPub(dto.getFaxRiferimento());
		entity.setMail(dto.getMail());
		entity.setNomeRiferimentoPub(dto.getNomeRiferimento());
		entity.setTelRiferimentoPub(dto.getTelRiferimento());

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaContatto vaContatto, VaDatiVacancy vaDatiVacancy) {
		if (vaContatto != null) {
			Date now = new Date();

			// sono il contatto principale per la vacancy originale o sono quello
			// secondario?
			boolean principale = (!vaContatto.getVaDatiVacanciesForIdVaContattoPrinc().isEmpty());

			entityManager.detach(vaContatto);

			vaContatto.setIdVaContatto(null);
			vaContatto.setDtmIns(now);
			vaContatto.setDtmMod(now);
			vaContatto.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaContatto.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

			if (principale) {
				vaDatiVacancy.setVaContattoPrinc(vaContatto);
			} else {
				vaDatiVacancy.setVaContattoAlt(vaContatto);
			}

			persist(vaContatto);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a va_contatto con id "
					+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	@Override
	public VaContattoDTO persistDTO(VaContattoDTO data, Integer idPrincipalIns) {
		VaContattoDTO contattoDTO = super.persistDTO(data, idPrincipalIns);
		VaContatto contatto = findById(contattoDTO.getId());

		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());

		if (!data.getActive()) {
			vaDatiVacancy.setVaContattoPrinc(contatto);
		} else {
			vaDatiVacancy.setVaContattoAlt(contatto);
		}
		vaDatiVacancyHome.persist(vaDatiVacancy);

		return contattoDTO;
	}

	@Override
	public void removeById(Integer deleteId, Integer idPfPrincipal) {

		VaContatto contatto = findById(deleteId);

		Set<VaDatiVacancy> datiVacancyAlt = contatto.getVaDatiVacanciesForIdVaContattoAlt();
		for (Iterator<VaDatiVacancy> iterator = datiVacancyAlt.iterator(); iterator.hasNext();) {
			VaDatiVacancy vaDatiVacancy = iterator.next();
			vaDatiVacancy.setVaContattoAlt(null);
			entityManager.merge(vaDatiVacancy);

			entityManager.remove(contatto);
		}

		Set<VaDatiVacancy> datiVacancyPric = contatto.getVaDatiVacanciesForIdVaContattoPrinc();
		for (Iterator<VaDatiVacancy> iterator = datiVacancyPric.iterator(); iterator.hasNext();) {
			VaDatiVacancy vaDatiVacancy = iterator.next();

			VaContatto contattoAlt = vaDatiVacancy.getVaContattoAlt();
			if (contattoAlt != null) {
				throw new MyPortalException("data.error_deleting_contatto_princ");
			}

			vaDatiVacancy.setVaContattoPrinc(null);
			entityManager.merge(vaDatiVacancy);

			entityManager.remove(contatto);
		}

	}

	@Override
	public VaContatto persist(VaContatto entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

	@Override
	public void remove(VaContatto entity) {
		VaContatto entityVaContatto = findById(entity.getIdVaContatto());
		super.remove(entityVaContatto);
	}

	@Override
	public VaContatto merge(VaContatto in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

}
