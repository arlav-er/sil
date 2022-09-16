package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.IDTO;

import javax.ejb.Local;

@Local
public interface IDTOHome<DTO extends IDTO> {

   /**
	 * Salva i dati partendo dal DTO.<br/>
	 * Crea una entity, ne esegue la persist su DB<br/>
	 * e quindi restituisce un DTO aggiornato.<br/>
	 * Attenzione! Non vengono aggiornati i dati del DTO passato come parametro!<br/>
	 * E' necessario recuperare il DTO restituito dal metodo ed andarlo a
	 * sostituire al precedente.<br/>
	 * E' inoltre quindi possibile confrontare il DTO 'prima' del salvataggio e
	 * quello 'dopo'
	 * 
	 * @param data DTO
	 * @return DTO
	 */
	DTO persistDTO(DTO data, Integer idPrincipalIns);

	/**
	 * Aggiorna i dati partendo dal DTO
	 * 
	 * @param data DTO
	 * @return DTO
	 */
	DTO mergeDTO(DTO data, Integer idPrincipalMod);

	// abstract List<Entity> findAll();

}
