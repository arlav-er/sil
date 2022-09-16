package it.eng.myportal.entity.home;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.eng.myportal.dtos.PattoSilStoricoDTO;
import it.eng.myportal.entity.PattoSilStorico;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

@Stateless
public class PattoSilStoricoHome extends AbstractUpdatableHome<PattoSilStorico, PattoSilStoricoDTO> {

	@EJB
	private DeProvinciaHome deProvinciaHome;
	@EJB
	private PfPrincipalHome PfPrincipalHome;
	@EJB
	private WsEndpointHome wsEndpointHome;

	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public PattoSilStorico findById(Integer id) {
		return findById(PattoSilStorico.class, id);
	}

	public List<PattoSilStorico> findByIdPatto(Integer idPattoSil) {
		return entityManager.createNamedQuery("findPattoSilStoricoById", PattoSilStorico.class)
				.setParameter("idPattoSil", idPattoSil).getResultList();
	}


	@Override
	public PattoSilStoricoDTO toDTO(PattoSilStorico entity) {
		if (entity == null) {
			return null;
		}
		PattoSilStoricoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdPattoSilStorico());
		dto.setIdPatto(entity.getIdPattoSil());
		dto.setDtmIns(entity.getDtmIns());
		dto.setDtmMod(entity.getDtmIns());
		dto.setTipoAccettazione(entity.getTipoAccettazione());
		dto.setTsAccettazione(entity.getTsAccettazione());
		dto.setTsInvio(entity.getTsInvio());
		dto.setCodStatoAccettazione(entity.getCodStatoAccettazione());

		return dto;
	}

	@Override
	public PattoSilStorico fromDTO(PattoSilStoricoDTO dto) {
		if (dto == null) {
			return null;
		}
		PattoSilStorico entity = super.fromDTO(dto);
		entity.setIdPattoSilStorico(dto.getId());
		entity.setIdPattoSil(dto.getIdPatto());
		entity.setTsInvio(dto.getTsInvio());
		entity.setTsAccettazione(dto.getTsAccettazione());
		entity.setTipoAccettazione(dto.getTipoAccettazione());
		entity.setCodStatoAccettazione(dto.getCodStatoAccettazione());
		return entity;
	}

	// public PattoSil findPatto(PfPrincipal targetCittadino, PattoType patto) {
	// PattoSil ptSil = null;
	// String hql = "from PattoSil pt " + " WHERE pt.codFis = :codFis " + " and pt.dtPatto = :dtPatto "
	// + " and pt.deProvincia.codProvincia = :codProvincia " + " and pt.numProtocollo = :numProtocollo "
	// + " and pt.numAnnoProtocollo = :numAnnoProtocollo ";
	// TypedQuery<PattoSil> query = entityManager.createQuery(hql, PattoSil.class);
	// query.setParameter("codFis", patto.getCodiceFiscale().getValue());
	// query.setParameter("dtPatto", patto.getDataPatto().toGregorianCalendar().getTime());
	// query.setParameter("codProvincia", patto.getCodProvinciaProv());
	// query.setParameter("numProtocollo", patto.getNumProtocollo());
	// query.setParameter("numAnnoProtocollo", new Integer(patto.getAnnoProtocollo().intValue()));
	//
	// try {
	// ptSil = (PattoSil) query.getSingleResult();
	// return ptSil;
	// } catch (javax.persistence.NoResultException e) {
	// return null;
	// }
	// }
}
