package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Interfaccia che deve implementare il DTO che 
 * può essere aggiornato inserendovi la data di ultima modifica.
 * 
 * @author Rodi A.
 *
 */
public interface IUpdatable extends IDTO{
				
	//data ultima modifica
	void setDtmMod(Date date);
	Date getDtmMod();
	
	//data inserimento
	void setDtmIns(Date date);
	Date getDtmIns();
	
	//utente ultima modifica
	void setIdPrincipalMod(Integer idPrincipal);
	Integer getIdPrincipalMod();
	
	//utente inserimento
	void setIdPrincipalIns(Integer idPrincipal);
	Integer getIdPrincipalIns();
}
