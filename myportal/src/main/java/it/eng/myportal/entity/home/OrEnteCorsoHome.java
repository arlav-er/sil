package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.OrCorsoDTO;
import it.eng.myportal.dtos.OrEnteCorsoDTO;
import it.eng.myportal.entity.OrCorso;
import it.eng.myportal.entity.OrEnteCorso;
import it.eng.myportal.entity.OrEnteCorso_;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class OrEnteCorsoHome extends AbstractUpdatableHome<OrEnteCorso, OrEnteCorsoDTO> {

	@EJB
	OrCorsoHome orCorsoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@Override
	public OrEnteCorso findById(Integer id) {
		return findById(OrEnteCorso.class, id);
	}
	
	/**
	 * CodiceOrganismo è una alternate key - può esistere al più 1 record
	 * @param co
	 * @return
	 */
	public OrEnteCorsoDTO findByCodiceOrganismo(Integer co) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrEnteCorso> c = cb.createQuery(OrEnteCorso.class);
		Root<OrEnteCorso> f = c.from(OrEnteCorso.class);
		Predicate equalPredicate = cb.equal(
				f.get(OrEnteCorso_.codiceOrganismo), co);
		c = c.where(equalPredicate);
		TypedQuery<OrEnteCorso> createdQuery = entityManager.createQuery(c);
		List<OrEnteCorso> rl = createdQuery.getResultList();
		if (rl != null)
			if (!rl.isEmpty()) {
				return toDTO(rl.get(0));
			}
		return null;
	}

	@Override
	public OrEnteCorsoDTO toDTO(OrEnteCorso entity) {
		if (entity == null)
			return null;
		OrEnteCorsoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdOrEnteCorso());
		DeComune deComune = entity.getDeComune();
		if (deComune != null) {
			dto.setCodComune(deComune.getCodCom());
			dto.setStrComune(deComune.getDenominazione());
			String codProvincia = deComuneHome.findDTOById(deComune.getCodCom()).getIdProvincia();
			String targa = deProvinciaHome.findDTOById(codProvincia).getTarga();
			dto.setTarga(targa);
		}
		dto.setCodiceOrganismo(entity.getCodiceOrganismo());
		dto.setRagioneSociale(entity.getRagioneSociale());
		dto.setIndirizzo(entity.getIndirizzo());
		dto.setCap(entity.getCap());
		dto.setTelefono(entity.getTelefono());
		dto.setFax(entity.getFax());
		dto.setEmail(entity.getEmail());

		Set<OrCorso> orCorsos = entity.getOrCorsos();
		Set<OrCorsoDTO> orCorsosDTO = null;
		if (orCorsos != null) {
			orCorsosDTO = new HashSet<OrCorsoDTO>();
			for (OrCorso orCorso : orCorsos) {
				orCorsosDTO.add(orCorsoHome.toDTO(orCorso));
			}
		}
		dto.setOrCorsos(orCorsosDTO);

		return dto;
	}

	@Override
	public OrEnteCorso fromDTO(OrEnteCorsoDTO dto) {

		if (dto == null)
			return null;
		OrEnteCorso entity = super.fromDTO(dto);

		entity.setIdOrEnteCorso(dto.getId());
		entity.setDeComune(deComuneHome.findById(dto.getCodComune()));
		entity.setCodiceOrganismo(dto.getCodiceOrganismo());
		entity.setRagioneSociale(dto.getRagioneSociale());
		entity.setIndirizzo(dto.getIndirizzo());
		entity.setCap(dto.getCap());
		entity.setTelefono(dto.getTelefono());
		entity.setFax(dto.getFax());
		entity.setEmail(dto.getEmail());

		Set<OrCorsoDTO> orCorsosDTO = dto.getOrCorsos();
		Set<OrCorso> orCorsos = null;
		if (orCorsosDTO != null) {
			orCorsos = new HashSet<OrCorso>();
			for (OrCorsoDTO orCorsoDTO : orCorsosDTO) {
				orCorsos.add(orCorsoHome.findById(orCorsoDTO.getId()));
			}
		}
		entity.setOrCorsos(orCorsos);

		return entity;
	}
}
