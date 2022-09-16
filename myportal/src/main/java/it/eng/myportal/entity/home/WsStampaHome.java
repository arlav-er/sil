package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.WsStampa;
import it.eng.myportal.entity.WsStampa_;
import it.eng.myportal.entity.decodifiche.DeTipoStampa;
import it.eng.myportal.entity.decodifiche.DeTipoStampa_;
import it.eng.myportal.entity.home.decodifiche.DeTipoStampaHome;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 * Classe Home per WsEndpoint
 * 
 * @see it.eng.myportal.entity.WsEndpoint
 * @author Rodi A.
 */
@Stateless
public class WsStampaHome extends AbstractUpdatableHome<WsStampa, WsStampaDTO> {

	@EJB
	DeTipoStampaHome deTipoStampaHome;

	@Override
	public WsStampaDTO toDTO(WsStampa entity) {
		if (entity == null)
			return null;
		WsStampaDTO dto = super.toDTO(entity);
		dto.setCodTipoStampa(entity.getDeTipoStampa().getCodTipoStampa());
		dto.setContenuto(entity.getContenuto());
		dto.setId(entity.getIdWsStampa());
		// Menzi non sa programmare ed è negro
		dto.setFlgDidRischioDisoccupazione(entity.getFlgDidRischioDisoccupazione());
		dto.setDtDidLicenziamento(entity.getDtDidLicenziamento());
		dto.setDtDidLetteraLicenziamento(entity.getDtDidLetteraLicenziamento());

		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		return dto;
	}

	@Override
	public WsStampa fromDTO(WsStampaDTO dto) {
		if (dto == null)
			return null;
		WsStampa entity = super.fromDTO(dto);
		entity.setContenuto(dto.getContenuto());
		entity.setDeTipoStampa(deTipoStampaHome.findById(dto.getCodTipoStampa()));
		entity.setFlgDidRischioDisoccupazione(dto.getFlgDidRischioDisoccupazione());
		entity.setDtDidLicenziamento(dto.getDtDidLicenziamento());
		entity.setDtDidLetteraLicenziamento(dto.getDtDidLetteraLicenziamento());
		entity.setIdWsStampa(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		return entity;
	}

	public WsStampa findById(Integer id) {
		return findById(WsStampa.class, id);
	}

	/**
	 * Restituisce l'ultima stampa, in ordine cronologico, di un determinato tipo per l'utente.
	 * 
	 * @param codTipoStampa
	 *            tipo di stampa
	 * @return la stampa, null se non trova nulla.
	 */
	public WsStampaDTO getLastStampa(String codTipoStampa, Integer idPfPrincipal) {
		List<WsStampa> stampe = getStampeQuery(codTipoStampa, idPfPrincipal).getResultList();
		if (stampe.size() > 0) {
			return toDTO(stampe.get(0));
		} else {
			return null;
		}
	}

	/**
	 * Restituisce tutte le stampe che appartengono ad un utente
	 * 
	 * @param codTipoStampa
	 *            tipo di stampa
	 * @param idPfPrincipal
	 *            utente
	 * @return la lista di stampe
	 */
	public List<WsStampaDTO> getStampeDTO(String codTipoStampa, Integer idPfPrincipal) {
		return findDTOByQuery(getStampeQuery(codTipoStampa, idPfPrincipal));
	}

	/**
	 * Genera una query che restituisce tutte le stampe di un certo tipo che appartengono ad un determinato utente.
	 * 
	 * @param codTipoStampa
	 * @param idPfPrincipal
	 * @return
	 */
	public TypedQuery<WsStampa> getStampeQuery(String codTipoStampa, Integer idPfPrincipal) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsStampa> query = qb.createQuery(WsStampa.class);
		Root<WsStampa> wsStampa = query.from(WsStampa.class);
		Join<WsStampa, PfPrincipal> pfPrincipal = wsStampa.join(WsStampa_.pfPrincipal);
		Join<WsStampa, DeTipoStampa> deTipoStampa = wsStampa.join(WsStampa_.deTipoStampa);

		query.where(qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal),
				qb.equal(deTipoStampa.get(DeTipoStampa_.codTipoStampa), codTipoStampa));
		query.orderBy(qb.desc(wsStampa.get(WsStampa_.dtmIns)));
		return entityManager.createQuery(query);

	}

}
