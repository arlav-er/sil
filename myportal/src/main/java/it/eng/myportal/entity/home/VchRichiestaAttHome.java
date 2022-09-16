package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.dtos.VchRichiestaAttDTO;
import it.eng.myportal.entity.VchRichiestaAtt;
import it.eng.myportal.entity.VchRichiestaAtt_;
import it.eng.myportal.entity.home.decodifiche.DeWsVchEsitoHome;

@Stateless
public class VchRichiestaAttHome extends AbstractUpdatableHome<VchRichiestaAtt, VchRichiestaAttDTO> {
	
	@EJB
	VchEntiAccreditatiHome vchEntiAccreditatiHome;
	@EJB
	DeWsVchEsitoHome deWsVchEsitoHome;
	
	@Override
	public VchRichiestaAtt findById(Integer id) {
		return findById(VchRichiestaAtt.class, id);
	}

	@Override
	public VchRichiestaAttDTO toDTO(VchRichiestaAtt entity) {
		if (entity == null){
			return null;
		}
		VchRichiestaAttDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVchRichiestaAtt());
		dto.setDtRichiesta(entity.getDtRichiesta());
		dto.setDtmIns(entity.getDtmIns());
		dto.setIdPrincipalIns(entity.getPfPrincipalIns().getIdPfPrincipal());
		dto.setDtmMod(entity.getDtmMod());
		dto.setIdPrincipalMod(entity.getPfPrincipalMod().getIdPfPrincipal());
		dto.setCodiceFiscaleCitt(entity.getCodiceFiscaleCitt());
		dto.setVchEntiAccreditati(vchEntiAccreditatiHome.toDTO(entity.getVchEntiAccreditati()));
		dto.setCodAttivazione(entity.getCodAttivazione());
		if(entity.getDeWsVchEsito() != null){
			dto.setDeWsVchEsito(deWsVchEsitoHome.toDTO(entity.getDeWsVchEsito()));
		}
		if(entity.getErroreNonCodificato()!=null){
			dto.setErroreNonCodificato(entity.getErroreNonCodificato());
		}
		return dto;
	}

	@Override
	public VchRichiestaAtt fromDTO(VchRichiestaAttDTO dto) {
		if (dto == null){
			return null;
		}
		VchRichiestaAtt entity = super.fromDTO(dto);
		entity.setIdVchRichiestaAtt(dto.getId());
		entity.setDtRichiesta(dto.getDtRichiesta());
		entity.setCodiceFiscaleCitt(dto.getCodiceFiscaleCitt());
		entity.setVchEntiAccreditati(vchEntiAccreditatiHome.findById(dto.getVchEntiAccreditati().getId()));
		entity.setCodAttivazione(dto.getCodAttivazione());
		if(dto.getDeWsVchEsito()!=null){
			entity.setDeWsVchEsito(deWsVchEsitoHome.findById(dto.getDeWsVchEsito().getId())); 
		}
		if(dto.getErroreNonCodificato()!=null){
			entity.setErroreNonCodificato(dto.getErroreNonCodificato());
		}
		return entity;
	}
	
	
	public List<VchRichiestaAttDTO> findAllDTOByUserIdAndCodeRisultato(Integer idPrincipalMod) {
		log.debug("convert vchRichiestaAtt entity to DTO: " + idPrincipalMod);
		List<VchRichiestaAttDTO> listDTO = new ArrayList<VchRichiestaAttDTO>();
		List<VchRichiestaAtt> listEntity = entityManager
				.createNamedQuery("findVchRichiestaAttByUserIdAndCodRisultato", VchRichiestaAtt.class)				
				.setParameter("userId", idPrincipalMod).getResultList();
		if(!listEntity.isEmpty()){
			for (VchRichiestaAtt currentEntity : listEntity) {
				listDTO.add(toDTO(currentEntity));
			}
		}
		return listDTO;
	}
	
	
	public List<VchRichiestaAttDTO> findAllByFilter(Integer codAttivazione, String codFiscaleCitt, VchEntiAccreditatiDTO vchEntiAccreditatiDTO, Date dataDa, Date dataA){
		log.debug("convert vchRichiestaAtt entity to DTO");
		List<VchRichiestaAttDTO> listDTO = new ArrayList<VchRichiestaAttDTO>();
		//List entity
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VchRichiestaAtt> cq = cb.createQuery(VchRichiestaAtt.class);
		Root<VchRichiestaAtt> vchRichiestaAtt = cq.from(VchRichiestaAtt.class);
		cq.select(vchRichiestaAtt);
		//Predicate
		List<Predicate> criteria = new ArrayList<Predicate>();
		if(codAttivazione!=null){
			criteria.add(cb.equal(vchRichiestaAtt.get(VchRichiestaAtt_.codAttivazione), codAttivazione));
		}
		if(codFiscaleCitt!=null){
			criteria.add(cb.equal(vchRichiestaAtt.get(VchRichiestaAtt_.codiceFiscaleCitt), codFiscaleCitt));
		}
		if(vchEntiAccreditatiHome.fromDTO(vchEntiAccreditatiDTO)!=null){
			criteria.add(cb.equal(vchRichiestaAtt.get(VchRichiestaAtt_.vchEntiAccreditati), vchEntiAccreditatiHome.fromDTO(vchEntiAccreditatiDTO)));		
		}
		if(dataDa!=null){
			criteria.add(cb.greaterThanOrEqualTo(vchRichiestaAtt.get(VchRichiestaAtt_.dtRichiesta), dataDa));
		}
		if(dataA!=null){
			criteria.add(cb.lessThanOrEqualTo(vchRichiestaAtt.get(VchRichiestaAtt_.dtRichiesta), dataA));
		}
		//Where
		cq.where(criteria.toArray(new Predicate[criteria.size()]));
		//Execute query
		TypedQuery<VchRichiestaAtt> q = entityManager.createQuery(cq);
		List<VchRichiestaAtt> listEntity = q.getResultList();
		//List entity
		if(!listEntity.isEmpty()){
			for (VchRichiestaAtt currentEntity : listEntity) {
				listDTO.add(toDTO(currentEntity));
			}
		}
		return listDTO;
	}
	
}
