package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.YgAdesioneAziendaSedeDTO;
import it.eng.myportal.entity.YgAdesioneAziendaSede;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class YgAdesioneAziendaSedeHome extends
		AbstractUpdatableHome<YgAdesioneAziendaSede, YgAdesioneAziendaSedeDTO> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	YgAdesioneAziendaHome ygAdesioneAziendaHome;

	@Override
	public YgAdesioneAziendaSede findById(Integer id) {
		return findById(YgAdesioneAziendaSede.class, id);
	}

	@Override
	public YgAdesioneAziendaSedeDTO mergeDTO(
			YgAdesioneAziendaSedeDTO ygAdesioneAziendaSedeDTO,
			Integer idPrincipalMod) {
		return super.mergeDTO(ygAdesioneAziendaSedeDTO, idPrincipalMod);
	}

	@Override
	public YgAdesioneAziendaSedeDTO persistDTO(
			YgAdesioneAziendaSedeDTO ygAdesioneAziendaSedeDTO,
			Integer idPrincipalIns) {
		return super.persistDTO(ygAdesioneAziendaSedeDTO, idPrincipalIns);
	}

	public void deleteDTO(YgAdesioneAziendaSedeDTO ygAdesioneAziendaSedeDTO) {
		YgAdesioneAziendaSede ygAdesioneAziendaSede = findById(ygAdesioneAziendaSedeDTO
				.getId());
		entityManager.remove(ygAdesioneAziendaSede);
		entityManager.clear();
	}

	public YgAdesioneAziendaSede fromDTO(
			YgAdesioneAziendaSedeDTO ygAdesioneAziendaSedeDTO) {
		YgAdesioneAziendaSede ygAdesioneAziendaSede = new YgAdesioneAziendaSede();
		ygAdesioneAziendaSede.setCap(ygAdesioneAziendaSedeDTO.getCap());
		if (ygAdesioneAziendaSedeDTO.getDeComuneDTO() != null
				&& ygAdesioneAziendaSedeDTO.getDeComuneDTO().getId() != null) {
			ygAdesioneAziendaSede
					.setDeComune(deComuneHome.findById(ygAdesioneAziendaSedeDTO
							.getDeComuneDTO().getId()));
		}
		ygAdesioneAziendaSede.setDtmIns(ygAdesioneAziendaSedeDTO.getDtmIns());
		ygAdesioneAziendaSede.setDtmMod(ygAdesioneAziendaSedeDTO.getDtmMod());
		ygAdesioneAziendaSede.setFax(ygAdesioneAziendaSedeDTO.getFax());
		ygAdesioneAziendaSede.setFlgSedeLegale(ygAdesioneAziendaSedeDTO
				.getFlgSedeLegale());
		ygAdesioneAziendaSede.setFlgTirocinio(ygAdesioneAziendaSedeDTO
				.getFlgTirocinio());
		ygAdesioneAziendaSede
				.setIdYgAdesioneAziendaSede(ygAdesioneAziendaSedeDTO.getId());
		ygAdesioneAziendaSede.setIndirizzo(ygAdesioneAziendaSedeDTO
				.getIndirizzo());

		if (ygAdesioneAziendaSedeDTO.getPfPrincipalDTO() != null
				&& ygAdesioneAziendaSedeDTO.getPfPrincipalDTO().getId() != null) {
			ygAdesioneAziendaSede.setPfPrincipal(pfPrincipalHome
					.findById(ygAdesioneAziendaSedeDTO.getPfPrincipalDTO()
							.getId()));
		}
		if (ygAdesioneAziendaSedeDTO.getIdPrincipalIns() != null) {
			ygAdesioneAziendaSede.setPfPrincipalIns(pfPrincipalHome
					.findById(ygAdesioneAziendaSedeDTO.getIdPrincipalIns()));
		}
		if (ygAdesioneAziendaSedeDTO.getIdPrincipalMod() != null) {
			ygAdesioneAziendaSede.setPfPrincipalMod(pfPrincipalHome
					.findById(ygAdesioneAziendaSedeDTO.getIdPrincipalMod()));
		}

		ygAdesioneAziendaSede.setTelefono(ygAdesioneAziendaSedeDTO
				.getTelefono());		

		return ygAdesioneAziendaSede;
	}

	public YgAdesioneAziendaSedeDTO toDTO(
			YgAdesioneAziendaSede ygAdesioneAziendaSede) {
		YgAdesioneAziendaSedeDTO ygAdesioneAziendaSedeDTO = new YgAdesioneAziendaSedeDTO();

		ygAdesioneAziendaSedeDTO.setCap(ygAdesioneAziendaSede.getCap());
		if (ygAdesioneAziendaSede.getDeComune() != null
				&& ygAdesioneAziendaSede.getDeComune().getCodCom() != null) {
			ygAdesioneAziendaSedeDTO.setDeComuneDTO(deComuneHome
					.findDTOById(ygAdesioneAziendaSede.getDeComune()
							.getCodCom()));
		}

		ygAdesioneAziendaSedeDTO.setDtmIns(ygAdesioneAziendaSede.getDtmIns());
		ygAdesioneAziendaSedeDTO.setDtmMod(ygAdesioneAziendaSede.getDtmMod());
		ygAdesioneAziendaSedeDTO.setFax(ygAdesioneAziendaSede.getFax());
		ygAdesioneAziendaSedeDTO.setFlgSedeLegale(ygAdesioneAziendaSede
				.getFlgSedeLegale());
		ygAdesioneAziendaSedeDTO.setFlgTirocinio(ygAdesioneAziendaSede
				.getFlgTirocinio());
		ygAdesioneAziendaSedeDTO.setId(ygAdesioneAziendaSede
				.getIdYgAdesioneAziendaSede());
		ygAdesioneAziendaSedeDTO.setIndirizzo(ygAdesioneAziendaSede
				.getIndirizzo());

		if (ygAdesioneAziendaSede.getPfPrincipal() != null
				&& ygAdesioneAziendaSede.getPfPrincipal().getIdPfPrincipal() != null) {
			ygAdesioneAziendaSedeDTO.setPfPrincipalDTO(pfPrincipalHome
					.findDTOById(ygAdesioneAziendaSede.getPfPrincipal()
							.getIdPfPrincipal()));
		}
		if (ygAdesioneAziendaSede.getPfPrincipalIns() != null) {
			ygAdesioneAziendaSedeDTO.setIdPrincipalIns(ygAdesioneAziendaSede
					.getPfPrincipalIns().getIdPfPrincipal());
		}
		if (ygAdesioneAziendaSede.getPfPrincipalMod() != null) {
			ygAdesioneAziendaSedeDTO.setIdPrincipalMod(ygAdesioneAziendaSede
					.getPfPrincipalMod().getIdPfPrincipal());
		}

		ygAdesioneAziendaSedeDTO.setTelefono(ygAdesioneAziendaSede
				.getTelefono());		

		return ygAdesioneAziendaSedeDTO;
	}

}
