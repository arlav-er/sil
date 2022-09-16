package it.eng.myportal.entity.home;

import it.eng.myportal.entity.YgImpostazioni;

import javax.ejb.Stateless;

/**
 * Classe Home per StConfigurazione
 * 
 * @see it.eng.myportal.entity.StConfigurazione
 * 
 */
@Stateless
public class YgImpostazioniHome extends AbstractHibernateHome<YgImpostazioni, Integer> {

	public YgImpostazioni findById(Integer id) {
		
		return findByIdNoCache(YgImpostazioni.class, id);
	}

	
	public YgImpostazioni findByCodRegione(String codRegione) {
		YgImpostazioni ygImpostazioni = entityManager.createNamedQuery("findYgImpostazioniByCodRegione", YgImpostazioni.class).setParameter("cod_regione_portale", codRegione).getSingleResult();
		
		return ygImpostazioni;
	}
	
}
