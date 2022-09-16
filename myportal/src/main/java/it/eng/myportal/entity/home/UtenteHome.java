package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.Utente;

/**
 * Home object for domain model class Utente.
 * 
 * @see it.eng.myportal.entity.Utente
 * @author Rodi A.
 * @deprecated
 */

public class UtenteHome extends AbstractUpdatableHome<Utente, UtenteDTO> {

	@Override
	public Utente findById(Integer id) {
		return null;
	}

}
