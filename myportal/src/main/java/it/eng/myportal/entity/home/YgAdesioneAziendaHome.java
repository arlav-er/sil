package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.YgAdesioneAziendaDTO;
import it.eng.myportal.dtos.YgAdesioneAziendaSedeDTO;
import it.eng.myportal.entity.YgAdesioneAzienda;
import it.eng.myportal.entity.YgAdesioneAziendaSede;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeQualificaSrqHome;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class YgAdesioneAziendaHome extends
		AbstractUpdatableHome<YgAdesioneAzienda, YgAdesioneAziendaDTO> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeQualificaSrqHome deQualificaSrqHome;

	@EJB
	YgAdesioneAziendaSedeHome ygAdesioneAziendaSedeHome;

	@Override
	public YgAdesioneAzienda findById(Integer id) {
		return findById(YgAdesioneAzienda.class, id);
	}

	@Override
	public YgAdesioneAziendaDTO mergeDTO(
			YgAdesioneAziendaDTO ygAdesioneAziendaDTO, Integer idPrincipalMod) {
		return super.mergeDTO(ygAdesioneAziendaDTO, idPrincipalMod);
	}

	@Override
	public YgAdesioneAziendaDTO persistDTO(
			YgAdesioneAziendaDTO ygAdesioneAziendaDTO, Integer idPrincipalIns) {
		return super.persistDTO(ygAdesioneAziendaDTO, idPrincipalIns);
	}

	public void deleteDTO(YgAdesioneAziendaDTO ygAdesioneAziendaDTO) {
		YgAdesioneAzienda ygAdesioneAzienda = findById(ygAdesioneAziendaDTO
				.getId());
		if (!ygAdesioneAzienda.getYgAdesioneAziendaSedeList().isEmpty()) {
			for (YgAdesioneAziendaSede aziendaSede : ygAdesioneAzienda
					.getYgAdesioneAziendaSedeList()) {
				entityManager.remove(aziendaSede);
			}
		}
		entityManager.remove(ygAdesioneAzienda);
	}

	public void sync(YgAdesioneAziendaDTO adesioneAziendaDTO,
			Integer idPrincipalIns) {
		if (adesioneAziendaDTO.getId() == null) {
			persistDTO(adesioneAziendaDTO, idPrincipalIns);
		} else {
			YgAdesioneAziendaDTO oldAdesioneAziendaDTO = findDTOById(adesioneAziendaDTO
					.getId());
			mergeDTO(adesioneAziendaDTO, idPrincipalIns);
			List<YgAdesioneAziendaSedeDTO> toDelete = new ArrayList<YgAdesioneAziendaSedeDTO>();
			if (oldAdesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() != null
					&& !oldAdesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
							.isEmpty()) {
				for (YgAdesioneAziendaSedeDTO dto : oldAdesioneAziendaDTO
						.getYgAdesioneAziendaSedeDTOList()) {
					if (!adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
							.contains(dto))
						toDelete.add(dto);
				}
				for (YgAdesioneAziendaSedeDTO dto : toDelete) {
					ygAdesioneAziendaSedeHome.remove(ygAdesioneAziendaSedeHome
							.findById(dto.getId()));
				}
			}
		}
	}

	public YgAdesioneAziendaDTO findDTOByIdPfPrincipal(Integer idPfPrincipal) {

		YgAdesioneAziendaDTO ygAdesioneAziendaDTO = null;
		List<YgAdesioneAzienda> ygAdesioneAziendaList = entityManager
				.createNamedQuery("findYgAdesioneAziendaByPfPrincipal",
						YgAdesioneAzienda.class)
				.setParameter("id_pf_principal", idPfPrincipal).getResultList();

		if (ygAdesioneAziendaList != null && !ygAdesioneAziendaList.isEmpty()) {
			ygAdesioneAziendaDTO = toDTO(ygAdesioneAziendaList.get(0));
		}

		return ygAdesioneAziendaDTO;
	}

	public boolean adesioneExists(Integer idPfPrincipalUtente) {
		boolean exists = false;
		YgAdesioneAziendaDTO ygAdesioneAziendaDTO = findDTOByIdPfPrincipal(idPfPrincipalUtente);

		if (ygAdesioneAziendaDTO != null) {
			exists = true;
		}
		return exists;
	}

	public YgAdesioneAzienda fromDTO(YgAdesioneAziendaDTO ygAdesioneAziendaDTO) {
		YgAdesioneAzienda ygAdesioneAzienda = new YgAdesioneAzienda();

		if (ygAdesioneAziendaDTO.getDeAttivitaMinDTO() != null
				&& ygAdesioneAziendaDTO.getDeAttivitaMinDTO().getId() != null) {
			ygAdesioneAzienda.setDeAttivitaMin(deAttivitaMinHome
					.findById(ygAdesioneAziendaDTO.getDeAttivitaMinDTO()
							.getId()));
		}
		if (ygAdesioneAziendaDTO.getDeQualificaSrqDTO() != null
				&& ygAdesioneAziendaDTO.getDeQualificaSrqDTO().getId() != null) {
			ygAdesioneAzienda.setDeQualificaSrq(deQualificaSrqHome
					.findById(ygAdesioneAziendaDTO.getDeQualificaSrqDTO()
							.getId()));
		}
		ygAdesioneAzienda.setDtAdesione(ygAdesioneAziendaDTO.getDtAdesione());
		ygAdesioneAzienda.setDtmIns(ygAdesioneAziendaDTO.getDtmIns());
		ygAdesioneAzienda.setDtmMod(ygAdesioneAziendaDTO.getDtmMod());
		ygAdesioneAzienda.setFlgAdesione(ygAdesioneAziendaDTO.getFlgAdesione());
		ygAdesioneAzienda.setIdYgAdesioneAzienda(ygAdesioneAziendaDTO.getId());
		
		ygAdesioneAzienda.setEmail1(ygAdesioneAziendaDTO.getEmail1());
		ygAdesioneAzienda.setEmail2(ygAdesioneAziendaDTO.getEmail2());
		ygAdesioneAzienda.setEmail3(ygAdesioneAziendaDTO.getEmail3());

		if (ygAdesioneAziendaDTO.getPfPrincipalDTO() != null
				&& ygAdesioneAziendaDTO.getPfPrincipalDTO().getId() != null) {
			ygAdesioneAzienda
					.setPfPrincipal(pfPrincipalHome
							.findById(ygAdesioneAziendaDTO.getPfPrincipalDTO()
									.getId()));
		}
		if (ygAdesioneAziendaDTO.getIdPrincipalIns() != null) {
			ygAdesioneAzienda.setPfPrincipalIns(pfPrincipalHome
					.findById(ygAdesioneAziendaDTO.getIdPrincipalIns()));
		}
		if (ygAdesioneAziendaDTO.getIdPrincipalMod() != null) {
			ygAdesioneAzienda.setPfPrincipalMod(pfPrincipalHome
					.findById(ygAdesioneAziendaDTO.getIdPrincipalMod()));
		}

		if (ygAdesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() != null
				&& !ygAdesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
						.isEmpty()) {
			List<YgAdesioneAziendaSede> ygAdesioneAziendaSedeList = new ArrayList<YgAdesioneAziendaSede>();
			for (YgAdesioneAziendaSedeDTO ygSedeDTO : ygAdesioneAziendaDTO
					.getYgAdesioneAziendaSedeDTOList()) {
				YgAdesioneAziendaSede temp = ygAdesioneAziendaSedeHome
						.fromDTO(ygSedeDTO);
				temp.setYgAdesioneAzienda(ygAdesioneAzienda);
				ygAdesioneAziendaSedeList.add(temp);
			}
			ygAdesioneAzienda
					.setYgAdesioneAziendaSedeList(ygAdesioneAziendaSedeList);
		}

		return ygAdesioneAzienda;

	}

	public YgAdesioneAziendaDTO toDTO(YgAdesioneAzienda ygAdesioneAzienda) {
		YgAdesioneAziendaDTO ygAdesioneAziendaDTO = new YgAdesioneAziendaDTO();

		if (ygAdesioneAzienda.getDeAttivitaMin() != null
				&& ygAdesioneAzienda.getDeAttivitaMin().getCodAttivitaMin() != null) {
			ygAdesioneAziendaDTO.setdeAttivitaMinDTO(deAttivitaMinHome
					.findDTOById(ygAdesioneAzienda.getDeAttivitaMin().getCodAttivitaMin()));
		}
		if (ygAdesioneAzienda.getDeQualificaSrq() != null
				&& ygAdesioneAzienda.getDeQualificaSrq().getCodQualificaSrq() != null) {
			ygAdesioneAziendaDTO.setDeQualificaSrqDTO(deQualificaSrqHome
					.findDTOById(ygAdesioneAzienda.getDeQualificaSrq()
							.getCodQualificaSrq()));
		}
		ygAdesioneAziendaDTO.setDtAdesione(ygAdesioneAzienda.getDtAdesione());
		ygAdesioneAziendaDTO.setDtmIns(ygAdesioneAzienda.getDtmIns());
		ygAdesioneAziendaDTO.setDtmMod(ygAdesioneAzienda.getDtmMod());
		ygAdesioneAziendaDTO.setFlgAdesione(ygAdesioneAzienda.getFlgAdesione());
		ygAdesioneAziendaDTO.setId(ygAdesioneAzienda.getIdYgAdesioneAzienda());
		
		ygAdesioneAziendaDTO.setEmail1(ygAdesioneAzienda.getEmail1());
		ygAdesioneAziendaDTO.setEmail2(ygAdesioneAzienda.getEmail2());
		ygAdesioneAziendaDTO.setEmail3(ygAdesioneAzienda.getEmail3());
		
		if (ygAdesioneAzienda.getPfPrincipal() != null
				&& ygAdesioneAzienda.getPfPrincipal().getIdPfPrincipal() != null) {
			ygAdesioneAziendaDTO.setPfPrincipalDTO(pfPrincipalHome
					.findDTOById(ygAdesioneAzienda.getPfPrincipal()
							.getIdPfPrincipal()));
		}
		if (ygAdesioneAzienda.getPfPrincipalIns() != null) {
			ygAdesioneAziendaDTO.setIdPrincipalIns(ygAdesioneAzienda
					.getPfPrincipalIns().getIdPfPrincipal());
		}
		if (ygAdesioneAzienda.getPfPrincipalMod() != null) {
			ygAdesioneAziendaDTO.setIdPrincipalMod(ygAdesioneAzienda
					.getPfPrincipalMod().getIdPfPrincipal());
		}

		if (ygAdesioneAzienda.getYgAdesioneAziendaSedeList() != null
				&& !ygAdesioneAzienda.getYgAdesioneAziendaSedeList().isEmpty()) {
			List<YgAdesioneAziendaSedeDTO> ygAdesioneAziendaSedeDTOList = new ArrayList<YgAdesioneAziendaSedeDTO>();
			for (YgAdesioneAziendaSede ygSede : ygAdesioneAzienda
					.getYgAdesioneAziendaSedeList()) {
				YgAdesioneAziendaSedeDTO temp = ygAdesioneAziendaSedeHome
						.toDTO(ygSede);
				temp.setYgAdesioneAziendaDTO(ygAdesioneAziendaDTO);
				ygAdesioneAziendaSedeDTOList.add(temp);
			}
			ygAdesioneAziendaDTO
					.setYgAdesioneAziendaSedeDTOList(ygAdesioneAziendaSedeDTOList);
		}

		return ygAdesioneAziendaDTO;
	}

}
