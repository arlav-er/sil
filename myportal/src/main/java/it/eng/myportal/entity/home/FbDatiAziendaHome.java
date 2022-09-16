package it.eng.myportal.entity.home;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.FbDatiAziendaDTO;
import it.eng.myportal.entity.FbDatiAzienda;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeNaturaGiuridicaHome;
import it.eng.myportal.exception.MyPortalException;

@Stateless
public class FbDatiAziendaHome extends AbstractUpdatableHome<FbDatiAzienda, FbDatiAziendaDTO> {

	@EJB
	FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeNaturaGiuridicaHome deNaturaGiuridicaHome;

	@Override
	public FbDatiAzienda findById(Integer id) {
		return findById(FbDatiAzienda.class, id);
	}

	public FbDatiAziendaDTO toDTO(FbDatiAzienda entity) {
		if (entity == null) {
			return null;
		}

		FbDatiAziendaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdFbDatiAzienda());
		dto.setIdFbSchedaFabbisogno(entity.getFbSchedaFabbisogno().getIdFbSchedaFabbisogno());
		dto.setNumCciaa(entity.getNumCciaa());
		dto.setMatricolaInps(entity.getMatricolaInps());
		dto.setPec(entity.getPec());
		dto.setSitoWeb(entity.getSitoWeb());
		dto.setNumDipendentiTot(entity.getNumDipendentiTot());
		dto.setNumDipendentiIndet(entity.getNumDipendentiIndet());
		dto.setNumDipendentiDet(entity.getNumDipendentiDet());
		dto.setNumDipendentiParttime(entity.getNumDipendentiParttime());
		dto.setFlgTirocinio(entity.getFlgTirocinio());
		dto.setEsitiTirocini(entity.getEsitiTirocini());
		dto.setDeAttivita(deAttivitaHome.toDTO(entity.getDeAttivita()));
		dto.setDeAttivitaMin(deAttivitaMinHome.toDTO(entity.getDeAttivitaMin()));
		dto.setNomeLegale(entity.getNomeLegale());
		dto.setCognomeLegale(entity.getCognomeLegale());
		dto.setFaxRiferimento(entity.getFaxRiferimento());
		dto.setTelRiferimento(entity.getTelRiferimento());
		dto.setCellRiferimento(entity.getCellRiferimento());
		dto.setEmailRiferimento(entity.getEmailRiferimento());
		if (entity.getDeNaturaGiuridica() != null)
			dto.setDeNaturaGiuridicaDTO(deNaturaGiuridicaHome.toDTO(entity.getDeNaturaGiuridica()));
		// dto.setTipoSoggetto(entity.getTipoSoggetto());
		if (entity.getNoteAttivita() != null)
			dto.setNoteAttivita(entity.getNoteAttivita());
		return dto;
	}

	public FbDatiAzienda fromDTO(FbDatiAziendaDTO dto) {
		if (dto == null) {
			return null;
		}

		FbDatiAzienda entity = super.fromDTO(dto);
		entity.setIdFbDatiAzienda(dto.getId());
		entity.setFbSchedaFabbisogno(fbSchedaFabbisognoHome.findById(dto.getIdFbSchedaFabbisogno()));
		entity.setNumCciaa(dto.getNumCciaa());
		entity.setMatricolaInps(dto.getMatricolaInps());
		entity.setPec(dto.getPec());
		entity.setSitoWeb(dto.getSitoWeb());
		entity.setNumDipendentiTot(dto.getNumDipendentiTot());
		entity.setNumDipendentiIndet(dto.getNumDipendentiIndet());
		entity.setNumDipendentiDet(dto.getNumDipendentiDet());
		entity.setNumDipendentiParttime(dto.getNumDipendentiParttime());
		entity.setFlgTirocinio(dto.getFlgTirocinio());
		entity.setEsitiTirocini(dto.getEsitiTirocini());
		entity.setNomeLegale(dto.getNomeLegale());
		entity.setCognomeLegale(dto.getCognomeLegale());
		entity.setDeNaturaGiuridica(deNaturaGiuridicaHome.fromDTO(dto.getDeNaturaGiuridicaDTO()));
		entity.setCellRiferimento(dto.getCellRiferimento());
		entity.setFaxRiferimento(dto.getFaxRiferimento());
		entity.setEmailRiferimento(dto.getEmailRiferimento());
		entity.setTelRiferimento(dto.getTelRiferimento());
		if (dto.getNoteAttivita() != null)
			entity.setNoteAttivita(dto.getNoteAttivita());

		if (dto.getDeAttivita() != null && dto.getDeAttivita().getId() != null) {
			entity.setDeAttivita(deAttivitaHome.findById(dto.getDeAttivita().getId()));
		}

		if (dto.getDeAttivitaMin() != null && dto.getDeAttivitaMin().getId() != null) {
			entity.setDeAttivitaMin(deAttivitaMinHome.findById(dto.getDeAttivitaMin().getId()));
		}

		return entity;
	}

	/**
	 * Trova l'oggetto FbDatiAzienda più recente inserito da un certo utente.
	 */
	public FbDatiAziendaDTO findLatestDTOByIdPfPrincipal(Integer idPfPrincipal) {
		TypedQuery<FbDatiAzienda> query = entityManager.createNamedQuery("findFbDatiAziendaByIdPrincipalOrderByDate",
				FbDatiAzienda.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		List<FbDatiAzienda> entityList = query.getResultList();
		if (entityList == null || entityList.isEmpty()) {
			return null;
		} else {
			return toDTO(entityList.get(0));
		}
	}

	public void removeByFbSchedaFabbisognoId(Integer id, Integer pfPrincipalId) throws MyPortalException {
		FbDatiAzienda todel = findBySchedaFabbisognoId(id);
		try {
			remove(todel);
		} catch (Exception e) {
			throw new MyPortalException("Impossibile eliminare FbDatiAzienda con schede fabb. collegate");
		}

	}

	private FbDatiAzienda findBySchedaFabbisognoId(Integer idSchedaFabbisogno) {
		TypedQuery<FbDatiAzienda> query = entityManager.createNamedQuery("findByFbSchedaFabbisognoId",
				FbDatiAzienda.class);
		query.setParameter("parIdScheda", idSchedaFabbisogno);
		FbDatiAzienda result = query.getSingleResult();
		return result;
	}
}
