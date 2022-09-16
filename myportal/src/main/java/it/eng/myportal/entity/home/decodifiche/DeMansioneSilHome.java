package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeMansioneSilDTO;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeMansione.
 * @see it.eng.myportal.entity.decodifiche.DeMansione
 * @author Rodi A.
 */
@Stateless
public class DeMansioneSilHome extends AbstractDecodeHome<DeMansioneSil, DeMansioneSilDTO> {
		
	public DeMansioneSil findById(String id) {
		return findById(DeMansioneSil.class, id);			
	}

}
