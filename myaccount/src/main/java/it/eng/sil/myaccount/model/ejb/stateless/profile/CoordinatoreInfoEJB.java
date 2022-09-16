package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.CoordinatoreInfo;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class CoordinatoreInfoEJB extends AbstractTabellaProfiloEJB<CoordinatoreInfo, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni coordinatore";
	}

	@Override
	public Class<CoordinatoreInfo> getEntityClass() {
		return CoordinatoreInfo.class;
	}

	public CoordinatoreInfo findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findCoordinatoreInfoByPfPrincipalId", CoordinatoreInfo.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

}
