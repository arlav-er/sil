package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.IHasPrimaryKey;

import javax.ejb.Local;

@Local
public interface IHasPrimaryKeyHome<DTO extends IHasPrimaryKey<PKType>, PKType> extends IDTOHome<DTO> {

	/**
	 * Cancella l'istanza dell'Entity da DB partendo dalla PrimaryKey
	 * 
	 * @param id PKType
	 * @param idPfPrincipalMod id del pfprincipal che esegue la modifica
	 * 
	 * nel caso avvengano errori durante la rimozione
	 */
	void removeById(PKType id,Integer idPfPrincipalMod);

	/**
	 * Cerca un elemento su DB a partire dalla chiave primaria e ne restituisce
	 * il DTO
	 * 
	 * @param id chiave primaria
	 * @return il DTO dell'oggetto trovato.
	 *             nel caso avvengano errori durante l'estrazione
	 */
	DTO findDTOById(PKType id);

}
