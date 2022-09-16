package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.entity.decodifiche.DeComune;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

@Stateless
public class DeComuneSolrHome extends DeComuneHome {



	@Override
	public String getEntityName() {
		String nomeHome = DeComuneHome.class.getSimpleName();
		return StringUtils.replace(nomeHome, "Home", "");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeComuneDTO> findBySuggestion(String par) {

		String queryString = DeComuneHome.TS_QUERY;
		par = this.checkSqlParameter(par);
		
		List<DeComune> distinctResultList = new ArrayList<DeComune>();
		Set<String> distinctComuneDenominazioneSet = new HashSet<String>();

		/* Tiro via i ":" perchè carattere speciale */
		par = par.replace(':', ' ');
		// ricerca puntatuale
		Map<String, DeComune> distinctResultMap = this.ricercaPuntualeComunePerDescrizione(par);
		for (Entry<String, DeComune> entry : distinctResultMap.entrySet()) {
			if (distinctComuneDenominazioneSet.add(entry.getValue().getDenominazione()))
				distinctResultList.add(entry.getValue());
		}
		// ricerca suggerita
		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeComune.class);
		query.setParameter("denom", queryPar);

		List<DeComune> tmpList = query.getResultList();
		// elimino eventuali duplicati
		for (DeComune comune : tmpList) {
			if (distinctComuneDenominazioneSet.add(comune.getDenominazione())
					&& distinctResultList.size() < DeComuneHome.LIMITE_RISULTATI_RICERCA) {
				distinctResultList.add(comune);
			}
		}

		List<DeComuneDTO> resultListDTO = new ArrayList<DeComuneDTO>();
		for (DeComune comune : distinctResultList) {
			resultListDTO.add(toDTO(comune));
		}

		return resultListDTO;
	}

	@Override
	public void valorizzaDescrizioneComuneDTO(DeComuneDTO dto, DeComune entity) {
		dto.setDescrizione(entity.getDenominazione());
	}
}
