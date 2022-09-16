package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoAbilitazioneGenDTO;
import it.eng.myportal.entity.decodifiche.DeTipoAbilitazioneGen;

import javax.ejb.Stateless;

/**
 * Classe Home per DeTipoAbilitazioneGen.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoAbilitazioneGen
 * @author Rodi A.
 */
@Stateless
public class DeTipoAbilitazioneGenHome extends AbstractDecodeHome<DeTipoAbilitazioneGen, DeTipoAbilitazioneGenDTO> {

	public DeTipoAbilitazioneGen findById(String id) {
		return findById(DeTipoAbilitazioneGen.class, id);
	}

	@Override
	public DeTipoAbilitazioneGenDTO toDTO(DeTipoAbilitazioneGen tipoAbilitazione) {
		if (tipoAbilitazione == null)
			return null;
		DeTipoAbilitazioneGenDTO ret = super.toDTO(tipoAbilitazione);
		ret.setId(tipoAbilitazione.getCodTipoAbilitazioneGen());
		ret.setDescrizione(tipoAbilitazione.getDescrizione());
		return ret;
	}

	@Override
	public DeTipoAbilitazioneGen fromDTO(DeTipoAbilitazioneGenDTO tipoAbilitazione) {
		if (tipoAbilitazione == null)
			return null;
		DeTipoAbilitazioneGen ret = super.fromDTO(tipoAbilitazione);
		ret.setCodTipoAbilitazioneGen(tipoAbilitazione.getId());
		ret.setDescrizione(tipoAbilitazione.getDescrizione());
		return ret;
	}
}
