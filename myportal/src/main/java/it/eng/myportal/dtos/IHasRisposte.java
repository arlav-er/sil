package it.eng.myportal.dtos;

import java.util.List;


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
public interface IHasRisposte extends IDTO{

	List<? super MsgMessaggioDTO> getRisposte();
	
}
