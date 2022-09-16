package it.eng.myportal.entity.home;

import it.eng.myportal.entity.MaOrario;

import javax.ejb.Stateless;

/**
 * @author Turro
 *
 */
@Stateless
public class MaOrarioHome extends
		AbstractHibernateHome<MaOrario, String> {

	
	public MaOrario findById(final String id) {
		return findById(MaOrario.class, id);
	}

}
