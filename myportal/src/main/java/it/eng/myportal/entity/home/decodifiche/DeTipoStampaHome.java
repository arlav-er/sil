package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

import it.eng.myportal.entity.decodifiche.DeTipoStampa;
import it.eng.myportal.entity.home.AbstractHibernateHome;

/**
 * Home per i tipi di stampa
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoStampa
 * @author Rodi A.
 */
@Stateless
public class DeTipoStampaHome extends AbstractHibernateHome<DeTipoStampa, String> {

	
	public DeTipoStampa findById(final String id) {
		return findById(DeTipoStampa.class, id);
	}

}
