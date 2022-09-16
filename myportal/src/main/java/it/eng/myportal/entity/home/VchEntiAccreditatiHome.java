package it.eng.myportal.entity.home;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.entity.VchEntiAccreditati;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

@Stateless
public class VchEntiAccreditatiHome extends AbstractUpdatableHome<VchEntiAccreditati, VchEntiAccreditatiDTO>{

	@EJB
	DeComuneHome deComuneHome;
	
	@Override
	public VchEntiAccreditati findById(Integer id) {
		return findById(VchEntiAccreditati.class, id);
	}
	
	@Override
	public VchEntiAccreditatiDTO toDTO(VchEntiAccreditati entity) {
		if (entity == null){
			return null;
		}
		VchEntiAccreditatiDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVchEntiAccreditati());
		dto.setCodiceFiscaleEnte(entity.getCodiceFiscaleEnte());
		dto.setCodSede(entity.getCodSede());
		if(entity.getDenominazione()!=null){
			dto.setDenominazione(entity.getDenominazione());
		}
		if(entity.getIndirizzo()!=null){
			dto.setIndirizzo(entity.getIndirizzo());
		}
		dto.setComune(deComuneHome.toDTO(entity.getDeComune()));
		return dto;
	}
	
	@Override
	public VchEntiAccreditati fromDTO(VchEntiAccreditatiDTO dto) {
		if (dto == null){
			return null;
		}
		VchEntiAccreditati entity = super.fromDTO(dto);
		entity.setIdVchEntiAccreditati(dto.getId());
		entity.setCodiceFiscaleEnte(dto.getCodiceFiscaleEnte());
		entity.setCodSede(dto.getCodSede());
		if(dto.getDenominazione()!=null){
			entity.setDenominazione(dto.getDenominazione());
		}
		if(dto.getIndirizzo()!=null){
			entity.setIndirizzo(dto.getIndirizzo());
		}
		entity.setDeComune(deComuneHome.findById(dto.getComune().getId()));
		return entity;
	}
}
