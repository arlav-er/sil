package it.eng.myportal.dtos;

/**
 * Interfaccia che deve implementare il DTO che può essere utilizzato come suggerimento
 * di un campo di autocomplete.
 * Un suggerimento è composto da id e label.
 * 
 * @author Rodi A.
 *
 */
public interface ISuggestible extends IDecode {
		
	/**
	 * Restituisce la descrizione associata al suggerimento.
	 * 
	 * @return descrizione
	 */
	String getDescrizione();
}
