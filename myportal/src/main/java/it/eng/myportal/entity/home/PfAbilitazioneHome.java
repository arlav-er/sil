package it.eng.myportal.entity.home;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.PfAbilitazioneDTO;
import it.eng.myportal.entity.PfAbilitazione;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaPfHome;
import it.eng.myportal.entity.home.decodifiche.DeFiltroHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class PfAbilitazione.
 * 
 * @author Rodi A.
 */
@Stateless
public class PfAbilitazioneHome extends AbstractUpdatableHome<PfAbilitazione, PfAbilitazioneDTO> {

	@EJB
	DeAttivitaPfHome deAttivitaPfHome;

	@EJB
	DeFiltroHome deFiltroHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	public PfAbilitazione findById(Integer id) {
		return findById(PfAbilitazione.class, id);
	}

	@Override
	public PfAbilitazioneDTO toDTO(PfAbilitazione entity) {
		if (entity == null)
			return null;
		PfAbilitazioneDTO ret = super.toDTO(entity);
		ret.setId(entity.getIdPfAbilitazione());
		ret.setCodRuoloPortale(entity.getDeRuoloPortale().getCodRuoloPortale());
		ret.setCodAttivitaPf(entity.getDeAttivitaPf().getCodAttivitaPf());
		ret.setCodFiltro(entity.getDeFiltro().getCodFiltro());
		ret.setFlagInserimento(entity.getFlagInserimento());
		ret.setFlagModifica(entity.getFlagModifica());
		ret.setFlagLettura(entity.getFlagLettura());
		ret.setFlagCancellazione(entity.getFlagCancellazione());
		ret.setFlagAmministrazione(entity.getFlagAmministrazione());
		ret.setFlagVisibile(entity.getFlagVisibile());

		return ret;
	}

	@Override
	public PfAbilitazione fromDTO(PfAbilitazioneDTO dto) {
		if (dto == null)
			return null;
		PfAbilitazione ret = super.fromDTO(dto);
		ret.setIdPfAbilitazione(dto.getId());
		ret.setDeRuoloPortale(deRuoloPortaleHome.findById(dto.getCodRuoloPortale()));
		ret.setDeAttivitaPf(deAttivitaPfHome.findById(dto.getCodAttivitaPf()));
		ret.setDeFiltro(deFiltroHome.findById(dto.getCodFiltro()));
		ret.setFlagInserimento(dto.getFlagInserimento());
		ret.setFlagModifica(dto.getFlagModifica());
		ret.setFlagLettura(dto.getFlagLettura());
		ret.setFlagCancellazione(dto.getFlagCancellazione());
		ret.setFlagAmministrazione(dto.getFlagAmministrazione());
		ret.setFlagVisibile(dto.getFlagVisibile());

		return ret;
	}

	public List<PfAbilitazioneDTO> cercaAbilitazioni(String codRuoloPortale) {
		TypedQuery<PfAbilitazione> query = entityManager.createNamedQuery("findPfAbilitazioneByCodRuoloPortale",
				PfAbilitazione.class);
		query.setParameter("codRuoloPortale", codRuoloPortale);
		List<PfAbilitazione> entityList = query.getResultList();
		List<PfAbilitazioneDTO> dtoList = new ArrayList<PfAbilitazioneDTO>();
		for (PfAbilitazione entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

}
