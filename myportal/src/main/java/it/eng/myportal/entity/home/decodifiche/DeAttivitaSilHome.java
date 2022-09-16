package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAttivitaSilDTO;
import it.eng.myportal.entity.decodifiche.sil.DeAttivitaSil;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeMansione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeMansione
 * @author Rodi A.
 */
@Stateless
public class DeAttivitaSilHome extends AbstractDecodeHome<DeAttivitaSil, DeAttivitaSilDTO> {

	public DeAttivitaSil findById(String id) {
		return findById(DeAttivitaSil.class, id);
	}

}
