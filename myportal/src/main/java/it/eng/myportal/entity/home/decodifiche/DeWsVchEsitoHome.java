package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DeWsVchEsitoDTO;
import it.eng.myportal.entity.DeWsVchEsito;

@Stateless
public class DeWsVchEsitoHome extends AbstractSuggestibleHome<DeWsVchEsito, DeWsVchEsitoDTO> {

	@Override
	public DeWsVchEsito findById(String id) {
		return findById(DeWsVchEsito.class, id);
	}

	@Override
	public DeWsVchEsitoDTO toDTO(DeWsVchEsito deWsVchEsito) {
		if (deWsVchEsito == null)
			return null;
		DeWsVchEsitoDTO ret = super.toDTO(deWsVchEsito);
		ret.setId(deWsVchEsito.getCodRisultato());
		ret.setDescrizione(deWsVchEsito.getDescrizione());
		ret.setMessaggio(deWsVchEsito.getMessaggio());
		return ret;
	}

	@Override
	public DeWsVchEsito fromDTO(DeWsVchEsitoDTO deWsVchEsito) {
		if (deWsVchEsito == null)
			return null;
		DeWsVchEsito ret = super.fromDTO(deWsVchEsito);
		ret.setCodRisultato(deWsVchEsito.getId());
		ret.setDescrizione(deWsVchEsito.getDescrizione());
		ret.setMessaggio(deWsVchEsito.getMessaggio());
		return ret;
	}
	
//	public String getMessaggioById(String id){
//		DeWsVchEsito elem = findById(id);
//		return elem.getMessaggio();
//	}
	
}
