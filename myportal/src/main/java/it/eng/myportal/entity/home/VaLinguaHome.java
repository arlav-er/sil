package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaLinguaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaLingua;
import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * @see it.eng.myportal.entity.VaLingua
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaLinguaHome extends AbstractVacancyEntityListHome<VaLingua, VaLinguaDTO> implements InoDTOejb<VaLingua> {

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	public VaLingua findById(Integer id) {
		return findById(VaLingua.class, id);
	}

	@Override
	public Set<VaLingua> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaLinguas();
	}

	public List<VaLingua> findByVacancyIdOrdered(int vacancyId) {
		return entityManager.createNamedQuery("findByIdVacancy", VaLingua.class).setParameter("id", vacancyId)
				.getResultList();
	}

	@Override
	public VaLinguaDTO toDTO(VaLingua entity) {
		if (entity == null)
			return null;
		VaLinguaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdVaLingua());

		DeGradoLin gradoLinguaLetto = entity.getDeGradoLinByCodGradoLetto();
		DeGradoLin gradoLinguaScritto = entity.getDeGradoLinByCodGradoScritto();
		DeGradoLin gradoLinguaParlato = entity.getDeGradoLinByCodGradoParlato();
		DeGradoLinSil gradoLinguaSilLetto = entity.getDeGradoSilLetto();
		DeGradoLinSil gradoLinguaSilScritto = entity.getDeGradoSilScritto();
		DeGradoLinSil gradoLinguaSilParlato = entity.getDeGradoSilParlato();
		Boolean madreLingua = entity.getFlagMadrelingua();

		if (madreLingua) {
			dto.setCodGradoLinguaLetto("");
			dto.setStrGradoLinguaLetto("MADRELINGUA");
			dto.setNumGradoLinguaLetto(0);
			dto.setCodGradoLinguaScritto("");
			dto.setStrGradoLinguaScritto("MADRELINGUA");
			dto.setNumGradoLinguaScritto(0);
			dto.setCodGradoLinguaParlato("");
			dto.setStrGradoLinguaParlato("MADRELINGUA");
			dto.setNumGradoLinguaParlato(0);
		} else {
			if (gradoLinguaLetto != null) {
				dto.setCodGradoLinguaLetto(gradoLinguaLetto.getCodGrado());
				dto.setStrGradoLinguaLetto(gradoLinguaLetto.getDescrizione());
				dto.setNumGradoLinguaLetto(gradoLinguaLetto.getNumOrdine());
			} else if (gradoLinguaSilLetto != null) {
				dto.setCodGradoLinguaLetto(gradoLinguaSilLetto.getCodGradoLinSil());
				dto.setStrGradoLinguaLetto(gradoLinguaSilLetto.getDescrizione());
			}

			if (gradoLinguaScritto != null) {
				dto.setCodGradoLinguaScritto(gradoLinguaScritto.getCodGrado());
				dto.setStrGradoLinguaScritto(gradoLinguaScritto.getDescrizione());
				dto.setNumGradoLinguaScritto(gradoLinguaScritto.getNumOrdine());
			} else if (gradoLinguaSilScritto != null) {
				dto.setCodGradoLinguaScritto(gradoLinguaSilScritto.getCodGradoLinSil());
				dto.setStrGradoLinguaScritto(gradoLinguaSilScritto.getDescrizione());
			}

			if (gradoLinguaParlato != null) {
				dto.setCodGradoLinguaParlato(gradoLinguaParlato.getCodGrado());
				dto.setStrGradoLinguaParlato(gradoLinguaParlato.getDescrizione());
				dto.setNumGradoLinguaParlato(gradoLinguaParlato.getNumOrdine());
			} else if (gradoLinguaSilParlato != null) {
				dto.setCodGradoLinguaParlato(gradoLinguaSilParlato.getCodGradoLinSil());
				dto.setStrGradoLinguaParlato(gradoLinguaSilParlato.getDescrizione());
			}
		}

		dto.setMadrelingua(madreLingua);
		dto.setCodLingua(entity.getDeLingua().getCodLingua());
		dto.setStrLingua(entity.getDeLingua().getDenominazione());
		dto.setOpzIndispensabile("Y".equals(entity.getOpzIndispensabile()));
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());

		return dto;
	}

	@Override
	public VaLingua fromDTO(VaLinguaDTO dto) {
		if (dto == null)
			return null;
		VaLingua entity = super.fromDTO(dto);

		String gradoLinguaLetto = dto.getCodGradoLinguaLetto();
		String gradoLinguaScritto = dto.getCodGradoLinguaScritto();
		String gradoLinguaParlato = dto.getCodGradoLinguaParlato();
		Boolean madreLingua = dto.isMadrelingua();

		entity.setIdVaLingua(dto.getId());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setDeLingua(deLinguaHome.findById(dto.getCodLingua()));
		entity.setFlagMadrelingua(madreLingua);
		entity.setOpzIndispensabile((dto.getOpzIndispensabile() != null && dto.getOpzIndispensabile()) ? "Y" : "N");

		if (madreLingua) {
			entity.setDeGradoLinByCodGradoLetto(null);
			entity.setDeGradoLinByCodGradoScritto(null);
			entity.setDeGradoLinByCodGradoParlato(null);
		} else if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			// Se sto usando le decodifiche del SIL
			if (gradoLinguaLetto != null && !gradoLinguaLetto.isEmpty()) {
				entity.setDeGradoSilLetto(deGradoLinSilHome.findById(gradoLinguaLetto));
			} else {
				entity.setDeGradoSilLetto(null);
			}

			if (gradoLinguaScritto != null && !gradoLinguaScritto.isEmpty()) {
				entity.setDeGradoSilScritto(deGradoLinSilHome.findById(gradoLinguaScritto));
			} else {
				entity.setDeGradoSilScritto(null);
			}
			if (gradoLinguaParlato != null && !gradoLinguaParlato.isEmpty()) {
				entity.setDeGradoSilParlato(deGradoLinSilHome.findById(gradoLinguaParlato));
			} else {
				entity.setDeGradoSilParlato(null);
			}
		} else {
			// Se sto usando le decodifiche ministeriali
			if (gradoLinguaLetto != null && !gradoLinguaLetto.isEmpty()) {
				entity.setDeGradoLinByCodGradoLetto(deGradoLinHome.findById(gradoLinguaLetto));
			} else {
				entity.setDeGradoLinByCodGradoLetto(null);
			}

			if (gradoLinguaScritto != null && !gradoLinguaScritto.isEmpty()) {
				entity.setDeGradoLinByCodGradoScritto(deGradoLinHome.findById(gradoLinguaScritto));
			} else {
				entity.setDeGradoLinByCodGradoScritto(null);
			}
			if (gradoLinguaParlato != null && !gradoLinguaParlato.isEmpty()) {
				entity.setDeGradoLinByCodGradoParlato(deGradoLinHome.findById(gradoLinguaParlato));
			} else {
				entity.setDeGradoLinByCodGradoParlato(null);
			}
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaLingua vaLingua, VaDatiVacancy vaDatiVacancy) {
		if (vaLingua != null) {
			Date now = new Date();
			entityManager.detach(vaLingua);
			vaLingua.setIdVaLingua(null);
			vaLingua.setVaDatiVacancy(vaDatiVacancy);
			vaLingua.setDtmIns(now);
			vaLingua.setDtmMod(now);
			vaLingua.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaLingua.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaLingua);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_lingua associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	public List<VaLinguaDTO> findDTOByVacancyIdOrdered(int vacancyId) {
		final List<VaLingua> list = findByVacancyIdOrdered(vacancyId);
		final List<VaLinguaDTO> ret = new ArrayList<VaLinguaDTO>();
		for (VaLingua entity : list) {
			final VaLinguaDTO dto = toDTO(entity);
			ret.add(dto);
		}
		return ret;
	}

	@Override
	public VaLingua persist(VaLingua entity, Integer actingUser) {
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
	public void remove(VaLingua entity) {
		VaLingua entityVaLingua = findById(entity.getIdVaLingua());
		super.remove(entityVaLingua);
	}

	@Override
	public VaLingua merge(VaLingua in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

}
