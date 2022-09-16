package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.VaPubblicazioneDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.home.local.IVaContattoHome;

@Stateless
@LocalBean
public class VaPubblicazioneHome extends AbstractVacancyEntityHome<VaPubblicazione, VaPubblicazioneDTO> implements InoDTOejb<VaPubblicazione>{

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@Override
	public VaPubblicazione findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaPubblicazione();
	}

	@Override
	public void copyById(Integer pfPrincipalId, VaPubblicazione vaPubblicazione, VaDatiVacancy vaDatiVacancy) {
		if (vaPubblicazione == null) {
			String msg = "Impossibile trovare l'entity corrispondente a vaAltreInfo con id "
					+ vaDatiVacancy.getIdVaDatiVacancy();
			log.error(msg);
			return;
		}
		Date now = new Date();
		entityManager.detach(vaPubblicazione);
		vaPubblicazione.setIdVaPubblicazione(null);
		vaPubblicazione.setVaDatiVacancy(vaDatiVacancy);
		vaPubblicazione.setDtmIns(now);
		vaPubblicazione.setDtmMod(now);
		vaPubblicazione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
		vaPubblicazione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
		persist(vaPubblicazione);
	}

	@Override
	public VaPubblicazione findById(Integer id) {
		return findById(VaPubblicazione.class, id);
	}

	public VaPubblicazione fromDTO(VaPubblicazioneDTO dto) {
		if (dto == null)
			return null;
		VaPubblicazione entity = super.fromDTO(dto);
		return fillFromDTO(entity, dto);
	}

	public VaPubblicazione fillFromDTO(VaPubblicazione entity, VaPubblicazioneDTO dto) {
		entity.setIdVaPubblicazione(dto.getId());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setDatiAzienda(dto.getDatiAzienda());
		entity.setContenuto(dto.getContenuto());
		entity.setLuogo(dto.getLuogo());
		entity.setFormazione(dto.getFormazione());
		entity.setContratto(dto.getContratto());
		entity.setConoscenze(dto.getConoscenze());
		entity.setCaratteristiche(dto.getCaratteristiche());
		entity.setOrario(dto.getOrario());
		entity.setCandidatura(dto.getCandidatura());
		return entity;
	}

	public VaPubblicazioneDTO toDTO(VaPubblicazione entity) {
		if (entity == null)
			return null;
		VaPubblicazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaPubblicazione());
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());

		dto.setDatiAzienda(entity.getDatiAzienda());
		dto.setContenuto(entity.getContenuto());
		dto.setLuogo(entity.getLuogo());
		dto.setFormazione(entity.getFormazione());
		dto.setContratto(entity.getContratto());
		dto.setConoscenze(entity.getConoscenze());
		dto.setCaratteristiche(entity.getCaratteristiche());
		dto.setOrario(entity.getOrario());
		dto.setCandidatura(entity.getCandidatura());
		return dto;
	}

	public List<VaPubblicazione> findVaPubblicazioneByVaDatiVacancy(Integer id) {
		List<VaPubblicazione> pubblicazioneList = new ArrayList<VaPubblicazione>();

		TypedQuery<VaPubblicazione> pubblicazioneQuery = entityManager
				.createNamedQuery("findVaPubblicazioneByVaDatiVacancyId", VaPubblicazione.class);
		pubblicazioneQuery.setParameter("idDaVacancy", id);
		pubblicazioneList = pubblicazioneQuery.getResultList();
		return pubblicazioneList;
	}


	@Override
	public VaPubblicazione persist(VaPubblicazione entity, Integer actingUser) {
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
	public void remove(VaPubblicazione entity) {
		VaPubblicazione entityVaPubb = findById(entity.getIdVaPubblicazione());
		super.remove(entityVaPubb);
	}

	@Override
	public VaPubblicazione merge(VaPubblicazione in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}
}
