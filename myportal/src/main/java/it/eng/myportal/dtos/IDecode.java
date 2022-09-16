package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Interfaccia che deve implementare il DTO che 
 * può essere aggiornato inserendovi la data di ultima modifica.
 * 
 * @author Rodi A.
 *
 */
public interface IDecode extends IHasPrimaryKey<String> {
	
	String getDescrizione();
	
	/**
	 * Setta il campo data inizio validità
	 * @param date Date
	 */
	void setDtInizioVal(Date date);
	
	/**
	 * Restituisce il campo data inizio validità
	 * @return Date
	 */
	Date getDtInizioVal();
	
	/**
	 * Setta il campo data fine validità
	 * @param date Date
	 */
	void setDtFineVal(Date date);
	
	/**
	 * Restituisce il campo data fine validità 
	 * @return Date
	 */
	Date getDtFineVal();
	
}
