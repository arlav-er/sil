package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.SoggettoPubblico;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

@Stateless
public class SoggettoPubblicoEJB extends AbstractTabellaProfiloEJB<SoggettoPubblico, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni soggetto pubblico";
	}

	@Override
	public Class<SoggettoPubblico> getEntityClass() {
		return SoggettoPubblico.class;
	}

	public SoggettoPubblico findByPfPrincipalId(Integer id) throws EJBException, MyCasNoResultException {
		return entityMan.createNamedQuery("findSoggettoPubblicoByPfPrincipalId", SoggettoPubblico.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

}
