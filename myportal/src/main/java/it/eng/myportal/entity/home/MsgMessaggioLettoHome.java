package it.eng.myportal.entity.home;

import it.eng.myportal.entity.MsgMessaggioLetto;

import javax.ejb.Stateless;

/**
 * @author Rodi A.
 */
@Stateless
public class MsgMessaggioLettoHome extends AbstractHibernateHome<MsgMessaggioLetto, Integer> {

	@Override
	public MsgMessaggioLetto findById(Integer id) {		
		return findById(MsgMessaggioLetto.class, id);
	}

}
