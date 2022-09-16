package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.ConsulenteInfo;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConsulenteInfoEJB extends AbstractTabellaProfiloEJB<ConsulenteInfo, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni consulente";
	}

	@Override
	public Class<ConsulenteInfo> getEntityClass() {
		return ConsulenteInfo.class;
	}

	public ConsulenteInfo findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findConsulenteInfoByPfPrincipalId", ConsulenteInfo.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}
}
