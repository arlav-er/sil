package it.eng.myportal.dtos;


/**
 * Interfaccia per i DTO.
 * Pua essere implementata da un DTO per definire quale sia la propria chiave primaria.
 * N.B. se ha a disposizione una chiave primaria allora a necessario ridefinire 
 * il metodo equals per tenerne conto e, di conseguenza, anche il metodo hashCode.
 * @see AbstractPrimaryKeyDTO
 * @author Rodi A.
 * 
 *
 */
public interface IHasPrimaryKey<Type> extends IDTO{

	/**
	 * Restituisce l'indice della tabella
	 *   
	 * @return Type
	 */
	Type getId();
	
	/**
	 * Setta l'inidice della tabella 
	 * 
	 * @param id Type
	 */
	void setId(Type id);
	
}
