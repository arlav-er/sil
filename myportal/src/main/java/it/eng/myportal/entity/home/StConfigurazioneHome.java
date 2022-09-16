package it.eng.myportal.entity.home;

import javax.ejb.Stateless;

import it.eng.myportal.entity.StConfigurazione;

/**
 * Classe Home per StConfigurazione
 * 
 * @see it.eng.myportal.entity.StConfigurazione
 * 
 */
@Stateless
@Deprecated//USARE TsGetOpzioni
public class StConfigurazioneHome extends AbstractHibernateHome<StConfigurazione, Integer> {

	public StConfigurazione findById(Integer id) {
		
		return findByIdNoCache(StConfigurazione.class, id);
	}
					
}
