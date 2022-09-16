package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AtpConsulenzaDTO;
import it.eng.myportal.entity.AtpConsulenza;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Turro
 */
@Stateless
public class AtpConsulenzaHome extends AbstractUpdatableHome<AtpConsulenza, AtpConsulenzaDTO> {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;
	
	public AtpConsulenza findById(Integer id) {
		return findById(AtpConsulenza.class, id);
	}

	@Override
	public AtpConsulenzaDTO toDTO(AtpConsulenza entity) {
		if (entity == null)
			return null;
		AtpConsulenzaDTO dto = super.toDTO(entity);
		dto.setMinuti(entity.getMinuti());
		dto.setNote(entity.getNote());
		

		return dto;
	}

	@Override
	public AtpConsulenza fromDTO(AtpConsulenzaDTO dto) {
		if (dto == null)
			return null;
		AtpConsulenza entity = super.fromDTO(dto);
		entity.setNote(dto.getNote());
		entity.setMinuti(dto.getMinuti());
		
		

		return entity;
	}

	

	
}
