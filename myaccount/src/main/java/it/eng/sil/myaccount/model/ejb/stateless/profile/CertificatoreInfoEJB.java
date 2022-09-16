package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.CertificatoreInfo;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class CertificatoreInfoEJB extends AbstractTabellaProfiloEJB<CertificatoreInfo, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni certificatore";
	}

	@Override
	public Class<CertificatoreInfo> getEntityClass() {
		return CertificatoreInfo.class;
	}

	public CertificatoreInfo findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findCertificatoreInfoByPfPrincipalId", CertificatoreInfo.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

}
