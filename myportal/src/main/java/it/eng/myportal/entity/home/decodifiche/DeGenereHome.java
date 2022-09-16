package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.entity.decodifiche.DeGenere;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class DeGenereHome extends AbstractDecodeHome<DeGenere, DeGenereDTO> {

	private static final String QUERY_RICERCA = "select c from DeGenere c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	public DeGenere findById(final String id) {
		return findById(DeGenere.class, id);
	}

	@Override
	public DeGenereDTO toDTO(final DeGenere entity) {
		if (entity == null) return null;
		DeGenereDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodGenere());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
	
	@Override
	public DeGenere fromDTO(final DeGenereDTO dto) {
		if (dto == null) return null;
		DeGenere entity = super.fromDTO(dto);   
		entity.setCodGenere(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
	
	public List<DeGenere> getAll() {		
		TypedQuery<DeGenere> query = entityManager.createQuery(QUERY_RICERCA, DeGenere.class).setHint("org.hibernate.cacheable", true);
		List<DeGenere> resultList = query.getResultList();		
		return resultList;
	}
	
	public List<DeGenereDTO> getAllDTO() {		
		List<DeGenereDTO> deGenereDTOList = new ArrayList<DeGenereDTO>();
		List<DeGenere> deGenereList = getAll();
		for (DeGenere deGenere : deGenereList) {
			deGenereDTOList.add(toDTO(deGenere));
		}
		return deGenereDTOList;
	}
	
}
