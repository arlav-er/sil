package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

import it.eng.myportal.entity.decodifiche.DeSistemaMyCas;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.sil.base.enums.DeSistemaEnum;

@Stateless
public class DeSistemaMyCasHome extends AbstractHibernateHome<DeSistemaMyCas, String> {

	public DeSistemaMyCas findById(String id) {
		return entityManager.createNamedQuery("findSistemaById", DeSistemaMyCas.class)
				.setHint("org.hibernate.cacheable", true).setParameter("codSitemaParam", id).getSingleResult();
	}

	/**
	 * Esegue una update di DeSistema della versione pom, in base al cod_sistema
	 * (Constants.DeSistema.NomeSistema)
	 * 
	 * Questo facilita la lettura di informazioni sul DB, "allineando" parte web e
	 * back-end. Aggiorna anche il flg_installato per essere certi che sia true
	 * 
	 * @param codSistema
	 * @param pomVersionIn
	 * @return
	 * @throws MyCasException
	 */
	public DeSistemaMyCas synchVersionePom(String codSistema, String pomVersionIn) {

		log.warn("Aggiornamento VERSIONE_POM automatico a: " + pomVersionIn);
		DeSistemaMyCas deSistemaMyCas = null;
		deSistemaMyCas = findById(codSistema);
		deSistemaMyCas.setVersionePom(pomVersionIn);
		deSistemaMyCas.setFlgInstallato(true);
		deSistemaMyCas = entityManager.merge(deSistemaMyCas);
		return deSistemaMyCas;
	}

	public DeSistemaMyCas findByEnum(DeSistemaEnum enu) {
		return findById(enu.toString());
	}
}