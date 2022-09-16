package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.BdAdesioneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.entity.BdAdesione;
import it.eng.myportal.entity.BdAdesione_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeBandoProgramma;
import it.eng.myportal.entity.decodifiche.DeBandoProgramma_;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.home.decodifiche.DeBandoProgrammaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class BdAdesioneHome extends AbstractUpdatableHome<BdAdesione, BdAdesioneDTO> {

	@EJB
	private DeCpiHome deCpiHome;
	@EJB
	private DeProvenienzaHome deProvenienzaHome;
	@EJB
	private PfPrincipalHome PfPrincipalHome;
	@EJB
	private DeBandoProgrammaHome deBandoProgrammaHome;
	
	@Override
	public BdAdesione findById(Integer id) {
		return findById(BdAdesione.class, id);
	}
	
	public List<BdAdesione> findByIdPfPrincipal(Integer idPfPrincipal){
		return entityManager.createNamedQuery("findBdAdesioneByIdPrincipal", BdAdesione.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();
	}
	
	public List<BdAdesione> findByCodBandoProgramma(Integer idPfPrincipal, String codBandoProgramma){
		return entityManager.createNamedQuery("findBdAdesioneByCodBandoProgramma", BdAdesione.class)
				.setParameter("idPfPrincipal", idPfPrincipal)
				.setParameter("codBandoProgramma", codBandoProgramma)
				.getResultList();
	}
	public List<BdAdesione> findByCodFiscaleAndCodBandoProgramma(Integer idPfPrincipal, String codBandoProgramma, String codiceFiscale){
		return entityManager.createNamedQuery("findByCodFiscaleAndCodBandoProgramma", BdAdesione.class)
				.setParameter("idPfPrincipal", idPfPrincipal)
				.setParameter("codBandoProgramma", codBandoProgramma)
				.setParameter("codiceFiscale", codiceFiscale.toUpperCase())
				.getResultList();
	}
	
	public List<BdAdesione> findByIdPfPrincipalCodBandoProgrammas(Integer idPfPrincipal, List<String> codBandoProgrammaList){
		if(codBandoProgrammaList==null || codBandoProgrammaList.isEmpty()){
			return findByIdPfPrincipal(idPfPrincipal);
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<BdAdesione> cq = cb.createQuery(BdAdesione.class);
		Root<BdAdesione> bdAdesione = cq.from(BdAdesione.class);
		Join<BdAdesione, DeBandoProgramma> bdAdesioneJoinDeBandoProgramma = bdAdesione.join(BdAdesione_.deBandoProgramma);
		List<Predicate> codBandoProgrammaPredicates = new ArrayList<Predicate>();
		for (String current : codBandoProgrammaList) {
			codBandoProgrammaPredicates.add(cb.equal(bdAdesioneJoinDeBandoProgramma.get(DeBandoProgramma_.codBandoProgramma), current));	
		}
		// Filtro IdPfPrincipal
		Predicate predicate = cb.and(cb.equal(bdAdesione.get(BdAdesione_.pfPrincipalIns),idPfPrincipal));
		
		cq.where(predicate, cb.or(codBandoProgrammaPredicates.toArray(new Predicate[codBandoProgrammaPredicates.size()])));
		
		TypedQuery<BdAdesione> tq = entityManager.createQuery(cq);
		List<BdAdesione> bdAdesioneList = tq.getResultList();
		return bdAdesioneList;
	}
	
	public List<BdAdesioneDTO> findDTOByIdPfPrincipal(Integer idPfPrincipal){
		List<BdAdesione> bdAdesioneList = findByIdPfPrincipal(idPfPrincipal);
		List<BdAdesioneDTO> bdAdesioneDTO = new ArrayList<BdAdesioneDTO>();
		for (BdAdesione current : bdAdesioneList) {
			bdAdesioneDTO.add(toDTO(current));
		}
		return bdAdesioneDTO;
	}
	
	public List<BdAdesioneDTO> findDTOByCodBandoProgramma(Integer idPfPrincipal, String codBandoProgramma){
		List<BdAdesioneDTO> bdAdesioneDTOs= new ArrayList<BdAdesioneDTO>();
		List<BdAdesione> bdAdesiones = findByCodBandoProgramma(idPfPrincipal, codBandoProgramma);
		for (BdAdesione current : bdAdesiones) {
			bdAdesioneDTOs.add(toDTO(current));
		}
		return bdAdesioneDTOs;
	}
	public List<BdAdesioneDTO> findDTOByCodFiscaleAndCodBandoProgramma(Integer idPfPrincipal, String codBandoProgramma, String codiceFiscale){
		List<BdAdesioneDTO> bdAdesioneDTOs= new ArrayList<BdAdesioneDTO>();
		List<BdAdesione> bdAdesiones = findByCodFiscaleAndCodBandoProgramma(idPfPrincipal, codBandoProgramma, codiceFiscale);
		for (BdAdesione current : bdAdesiones) {
			bdAdesioneDTOs.add(toDTO(current));
		}
		return bdAdesioneDTOs;
	}
	
	public List<BdAdesioneDTO> findDTOByIdPfPrincipalCodBandoProgrammas(Integer idPfPrincipal, List<String> codBandoProgrammas){
		List<BdAdesione> bdAdesiones = new ArrayList<BdAdesione>();
		if(codBandoProgrammas==null || codBandoProgrammas.isEmpty()){
			return findDTOByIdPfPrincipal(idPfPrincipal);
		}
		else{
			bdAdesiones = findByIdPfPrincipalCodBandoProgrammas(idPfPrincipal, codBandoProgrammas);
			List<BdAdesioneDTO> bdAdesioneDTOs = new ArrayList<BdAdesioneDTO>();
			for (BdAdesione current : bdAdesiones) {
				bdAdesioneDTOs.add(toDTO(current));
			}
			return bdAdesioneDTOs;
		}
	}
	
	public List<BdAdesione> findByFilter(String nome, String cognome, String codFiscale,
			String codBandoProgramma, Date dtAdesioneDa, Date dtAdesioneA, String tipoDichiarazione, String fasciaEta,
			List<DeCpi> selectedDeCpis){
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<BdAdesione> cq = cb.createQuery(BdAdesione.class);
		Root<BdAdesione> bdAdesioneRoot = cq.from(BdAdesione.class);
		Join<BdAdesione, DeBandoProgramma> bdAdesioneJoinDeBandoProgramma = bdAdesioneRoot.join(BdAdesione_.deBandoProgramma);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		if(nome != null && !"".equals(nome)){
			predicates.add(cb.like(cb.upper(bdAdesioneRoot.get(BdAdesione_.nome)), "%" + nome.toUpperCase() + "%"));
		}
		if(cognome != null && !"".equals(cognome)){
			predicates.add(cb.like(cb.upper(bdAdesioneRoot.get(BdAdesione_.cognome)), "%" + cognome.toUpperCase() + "%"));
		}
		if(codFiscale != null && !"".equals(codFiscale)){
			predicates.add(cb.equal(cb.upper(bdAdesioneRoot.get(BdAdesione_.codiceFiscale)), codFiscale.toUpperCase()));
		}
		if(codBandoProgramma != null && !"".equals(codBandoProgramma)){
			predicates.add(cb.equal(bdAdesioneJoinDeBandoProgramma.get(DeBandoProgramma_.codBandoProgramma), codBandoProgramma));
			if(codBandoProgramma.equals(ConstantsSingleton.DeBandoProgramma.COD_REI) &&
					tipoDichiarazione != null && !"".equals(tipoDichiarazione)){
				predicates.add(cb.equal(cb.upper(bdAdesioneRoot.get(BdAdesione_.dichiarazione)), tipoDichiarazione.toUpperCase()));
			}else if(codBandoProgramma.equals(ConstantsSingleton.DeBandoProgramma.COD_UMBAT) &&
					fasciaEta != null && !"".equals(fasciaEta)){
				predicates.add(cb.equal(cb.upper(bdAdesioneRoot.get(BdAdesione_.dichiarazione)), fasciaEta.toUpperCase()));
			}
		}
		if(dtAdesioneDa != null){
			predicates.add(cb.greaterThanOrEqualTo(bdAdesioneRoot.get(BdAdesione_.dtAdesione), dtAdesioneDa));
		}
		if(dtAdesioneA != null){
			predicates.add(cb.lessThanOrEqualTo(bdAdesioneRoot.get(BdAdesione_.dtAdesione), dtAdesioneA));
		}
		if(selectedDeCpis!=null && !selectedDeCpis.isEmpty()){
			In<DeCpi> inPredicate = null;
			Path<DeCpi> dePath = bdAdesioneRoot.get(BdAdesione_.deCpi);
			inPredicate = cb.in(dePath);
			for(DeCpi current: selectedDeCpis){
				inPredicate.value(current);
			}
			predicates.add(inPredicate);
		}
		
		cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		TypedQuery<BdAdesione> tq = entityManager.createQuery(cq);
		List<BdAdesione> bdAdesiones = tq.getResultList();
		return bdAdesiones;
	}

	public List<BdAdesioneDTO> findDTOByFilter(String nome, String cognome, String codFiscale,
			String codBandoProgramma, Date dtAdesioneDa, Date dtAdesioneA, String tipoDichiarazione, String fasciaEta, List<DeCpiDTO> selectedDeCpiDTOs){
		List<DeCpi> selectedDeCpis = new ArrayList<DeCpi>();
		for(DeCpiDTO current : selectedDeCpiDTOs){
			selectedDeCpis.add(deCpiHome.fromDTO(current));
		}
		List<BdAdesione> bdAdesiones = findByFilter(nome, cognome, codFiscale,
				codBandoProgramma, dtAdesioneDa, dtAdesioneA, tipoDichiarazione, fasciaEta, selectedDeCpis);
		List<BdAdesioneDTO> bdAdesioneDTOs = new ArrayList<BdAdesioneDTO>();
		for (BdAdesione current : bdAdesiones) {
			bdAdesioneDTOs.add(toDTO(current));
		}
		return bdAdesioneDTOs;
	}
	
	@Override
	public BdAdesioneDTO toDTO(BdAdesione entity) {
		if (entity == null){
			return null;
		}
		BdAdesioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdBdAdesione());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setCognome(entity.getCognome());
		dto.setNome(entity.getNome());
		dto.setDtNascita(entity.getDtNascita());
		dto.setCodStatoOccupazionale(entity.getCodStatoOccupazionale());
		dto.setDescStatoOccupazionale(entity.getDescStatoOccupazionale());
		dto.setMesiAnzianita(entity.getMesiAnzianita());
		dto.setDtDid(entity.getDtDid());
		dto.setDtAdesione(entity.getDtAdesione());
		if(entity.getDeCpi()!=null){
			DeCpi deCpi = deCpiHome.findById(entity.getDeCpi().getCodCpi());
			dto.setDeCpiDTO(deCpiHome.toDTO(deCpi));
		}
		dto.setDichiarazione(entity.getDichiarazione());
		if(entity.getDeProvenienza()!=null){
			DeProvenienza deProvenienza = deProvenienzaHome.findById(entity.getDeProvenienza().getCodProvenienza());
			dto.setDeProvenienzaDTO(deProvenienzaHome.toDTO(deProvenienza));
		}
		if(entity.getPfPrincipal()!=null){
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(entity.getPfPrincipal().getIdPfPrincipal());
			dto.setPfPrincipalDTO(pfPrincipalHome.toDTO(pfPrincipal));
		}
		if(entity.getDeBandoProgramma()!=null){
			DeBandoProgramma deBandoProgramma = deBandoProgrammaHome.findById(entity.getDeBandoProgramma().getCodBandoProgramma());
			dto.setDeBandoProgrammaDTO(deBandoProgrammaHome.toDTO(deBandoProgramma));
		}
		return dto;
	}

	
	@Override
	public BdAdesione fromDTO(BdAdesioneDTO dto) {
		if (dto == null){
			return null;
		}
		BdAdesione entity = super.fromDTO(dto);
		entity.setIdBdAdesione(dto.getIdBdAdesione());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setCognome(dto.getCognome());
		entity.setNome(dto.getNome());
		entity.setDtNascita(dto.getDtNascita());
		entity.setCodStatoOccupazionale(dto.getCodStatoOccupazionale());
		entity.setDescStatoOccupazionale(dto.getDescStatoOccupazionale());
		entity.setMesiAnzianita(dto.getMesiAnzianita());
		entity.setDtDid(dto.getDtDid());
		entity.setDtAdesione(dto.getDtAdesione());
		entity.setDeCpi(deCpiHome.fromDTO(dto.getDeCpiDTO()));
		entity.setDichiarazione(dto.getDichiarazione());
		entity.setDeProvenienza(deProvenienzaHome.fromDTO(dto.getDeProvenienzaDTO()));
		entity.setPfPrincipal(pfPrincipalHome.fromDTO(dto.getPfPrincipalDTO()));
		entity.setDeBandoProgramma(deBandoProgrammaHome.fromDTO(dto.getDeBandoProgrammaDTO()));
		return entity;
	}
}
