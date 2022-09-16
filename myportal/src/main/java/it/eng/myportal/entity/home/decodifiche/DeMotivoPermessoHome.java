package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.entity.decodifiche.DeMotivoPermesso;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class DeMotivoPermessoHome extends AbstractDecodeHome<DeMotivoPermesso, DeMotivoPermessoDTO> {

	private static final String QUERY_RICERCA = "select c from DeMotivoPermesso c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	public DeMotivoPermesso findById(final String id) {
		return findById(DeMotivoPermesso.class, id);
	}

	@Override
	public DeMotivoPermessoDTO toDTO(final DeMotivoPermesso entity) {
		if (entity == null) return null;
		DeMotivoPermessoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMotivoPermesso());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
	
	@Override
	public DeMotivoPermesso fromDTO(final DeMotivoPermessoDTO dto) {
		if (dto == null) return null;
		DeMotivoPermesso entity = super.fromDTO(dto);   
		entity.setCodMotivoPermesso(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
	
	public List<DeMotivoPermesso> getAll() {		
		TypedQuery<DeMotivoPermesso> query = entityManager.createQuery(QUERY_RICERCA, DeMotivoPermesso.class);
		List<DeMotivoPermesso> resultList = query.getResultList();		
		return resultList;
	}
	
	public List<DeMotivoPermessoDTO> getAllDTO() {		
		List<DeMotivoPermessoDTO> deMotivoPermessoDTOList = new ArrayList<DeMotivoPermessoDTO>();
		List<DeMotivoPermesso> deMotivoPermessoList = getAll();
		for (DeMotivoPermesso deMotivoPermesso : deMotivoPermessoList) {
			deMotivoPermessoDTOList.add(toDTO(deMotivoPermesso));
		}
		return deMotivoPermessoDTOList;
	}
	
}
