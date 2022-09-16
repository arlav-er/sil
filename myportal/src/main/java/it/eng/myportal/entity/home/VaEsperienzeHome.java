package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaEsperienzeDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.home.decodifiche.DeMotivoEtaSilHome;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class VaEsperienzeHome extends AbstractVacancyEntityHome<VaEsperienze, VaEsperienzeDTO> implements InoDTOejb<VaEsperienze> {

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeMotivoEtaSilHome deMotivoEtaSilHome;

	@Override
	public void copyById(Integer pfPrincipalId, VaEsperienze vaEsperienza, VaDatiVacancy vaDatiVacancy) {
		if (vaEsperienza != null) {
			Date now = new Date();
			entityManager.detach(vaEsperienza);
			vaEsperienza.setIdVaEsperienze(null);
			vaEsperienza.setVaDatiVacancy(vaDatiVacancy);
			vaEsperienza.setDtmIns(now);
			vaEsperienza.setDtmMod(now);
			vaEsperienza.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaEsperienza.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaEsperienza);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_esperienza associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	@Override
	public VaEsperienze findById(Integer id) {
		return findById(VaEsperienze.class, id);
	}

	public VaEsperienze fromDTO(VaEsperienzeDTO dto) {
		if (dto == null)
			return null;
		VaEsperienze entity = super.fromDTO(dto);
		return fillFromDTO(entity, dto);
	}

	public VaEsperienze fillFromDTO(VaEsperienze entity, VaEsperienzeDTO dto) {
		if (dto == null || entity == null)
			return null;

		entity.setIdVaEsperienze(dto.getId());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setNumDa(dto.getNumDa());
		entity.setNumA(dto.getNumA());
		entity.setNotaMotivoEta(dto.getNotaMotivoEta());
		entity.setOpzEsperienza(dto.getOpzEsperienza());
		entity.setNumAnniEsperienza(dto.getNumAnniEsperienza());
		entity.setNotaEsperienza(dto.getNotaEsperienza());
		entity.setOpzFormazione(dto.getOpzFormazione());

		if (dto.getDeMotivoEtaSil() != null && dto.getDeMotivoEtaSil().getId() != null) {
			entity.setDeMotivoEtaSil(deMotivoEtaSilHome.findById(dto.getDeMotivoEtaSil().getId()));
		}

		return entity;
	}

	public VaEsperienzeDTO toDTO(VaEsperienze entity) {
		if (entity == null)
			return null;
		VaEsperienzeDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdVaEsperienze());
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());
		dto.setNumDa(entity.getNumDa());
		dto.setNumA(entity.getNumA());
		dto.setNotaMotivoEta(entity.getNotaMotivoEta());
		dto.setOpzEsperienza(entity.getOpzEsperienza());
		dto.setNumAnniEsperienza(entity.getNumAnniEsperienza());
		dto.setNotaEsperienza(entity.getNotaEsperienza());
		dto.setOpzFormazione(entity.getOpzFormazione());
		dto.setDeMotivoEtaSil(deMotivoEtaSilHome.toDTO(entity.getDeMotivoEtaSil()));

		return dto;
	}

	@Override
	public VaEsperienze findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaEsperienze();
	}


	@Override
	public VaEsperienze merge(VaEsperienze entity, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public VaEsperienze persist(VaEsperienze entity, Integer actingUser) {
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
