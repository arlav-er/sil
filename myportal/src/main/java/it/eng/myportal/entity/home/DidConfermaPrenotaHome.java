package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.DidConfermaPrenotaDTO;
import it.eng.myportal.entity.DidConfermaPrenota;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author Enrico
 */
@Stateless
public class DidConfermaPrenotaHome extends AbstractUpdatableHome<DidConfermaPrenota, DidConfermaPrenotaDTO> {

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private DeComuneHome deComuneHome;

	public DidConfermaPrenota findById(Integer id) {
		DidConfermaPrenota obj = findById(DidConfermaPrenota.class, id);
		return obj;
	}

	public DidConfermaPrenota findByIdPfPrincipal(Integer idPfPrincipal) {
		DidConfermaPrenota conf = null;
		List<DidConfermaPrenota> list = entityManager
				.createNamedQuery("findConfermaDidByIfPfPrincipal", DidConfermaPrenota.class)
				.setParameter("id_pf_principal", idPfPrincipal).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			conf = list.get(0);
			return conf;
		}
	}

	@Override
	public DidConfermaPrenotaDTO toDTO(DidConfermaPrenota entity) {
		if (entity == null) {
			return null;
		}
		DidConfermaPrenotaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdDidConfermaPrenota());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setDtConferma(entity.getDtConferma());
		dto.setComuneDomicilio(deComuneHome.findDTOById(entity.getDeComune().getCodCom()));
		dto.setPrincipal(pfPrincipalHome.findDTOById(entity.getPfPrincipal().getIdPfPrincipal()));
		return dto;
	}

	@Override
	public DidConfermaPrenota fromDTO(DidConfermaPrenotaDTO dto) {
		if (dto == null) {
			return null;
		}
		DidConfermaPrenota entity = super.fromDTO(dto);
		entity.setIdDidConfermaPrenota(dto.getId());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setDeComune(deComuneHome.findById(dto.getComuneDomicilio().getId()));
		entity.setDtConferma(dto.getDtConferma());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getPrincipal().getId()));
		return entity;
	}

}
