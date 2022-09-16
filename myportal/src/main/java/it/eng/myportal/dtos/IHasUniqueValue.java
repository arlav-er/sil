package it.eng.myportal.dtos;

/**
 * Questa interfaccia viene implementata dai DTO che contengono
 * un valore che deve essere univoco.
 * Tipicamente il DTO che implementa questa interfaccia è presente in un master/detail
 * che permette di averne una lista. 
 * Quando viene creato un unovo DTO e si cerca di inserirlo all'interno della lista
 * il validatore verifica che non vi siano elementi 'duplicati'. 
 * Per fare ciò confronta l'attributo indicato da questa interfaccia.
 *  
 * @author Rodi A.
 *
 */
public interface IHasUniqueValue extends IDTO{
	
	/**
	 * Restituisce l'attributo del quale
	 * bisogna controllare l'unicità.
	 * 
	 * @return String
	 */
	String getUniqueValue();
}
