package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoFbConvenzioneDTO;
import it.eng.myportal.entity.decodifiche.DeStatoFbConvenzione;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class DeStatoFbConvenzioneHome extends AbstractDecodeHome<DeStatoFbConvenzione, DeStatoFbConvenzioneDTO> {
	protected final Log log = LogFactory.getLog(DeStatoFbConvenzioneHome.class);

	public DeStatoFbConvenzione findById(String cod_stato_conv) {
		return findById(DeStatoFbConvenzione.class, cod_stato_conv);
	}

	public DeStatoFbConvenzione fromDTO(DeStatoFbConvenzioneDTO dto) {
		if (dto == null) {
			return null;
		}

		DeStatoFbConvenzione entity = super.fromDTO(dto);
		entity.setCodStatoConv(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}

	public DeStatoFbConvenzioneDTO toDTO(DeStatoFbConvenzione entity) {
		DeStatoFbConvenzioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoConv());
		dto.setDescrizione(entity.getDescrizione());

		return dto;
	}

	/** Restituisce tutti gli stati ricercabili (tutti eccetto IN LAVORAZIONE) */
	public List<DeStatoFbConvenzioneDTO> findRicercabili() {
		TypedQuery<DeStatoFbConvenzione> query = entityManager.createNamedQuery("findDeFbStatoConvenzioneRicercabile",
				DeStatoFbConvenzione.class);
		query.setParameter("codStatoLav", ConstantsSingleton.DeStatoFbConvenzione.IN_LAVORAZIONE);

		List<DeStatoFbConvenzione> entityList = query.getResultList();
		List<DeStatoFbConvenzioneDTO> dtoList = new ArrayList<DeStatoFbConvenzioneDTO>(entityList.size());
		for (DeStatoFbConvenzione entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}
}
