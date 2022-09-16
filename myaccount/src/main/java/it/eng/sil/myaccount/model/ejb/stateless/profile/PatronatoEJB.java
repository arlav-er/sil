package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.Patronato;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class PatronatoEJB extends AbstractTabellaProfiloEJB<Patronato, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni patronato";
	}

	@Override
	public Class<Patronato> getEntityClass() {
		return Patronato.class;
	}

	public Patronato findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findPatronatoByPfPrincipalId", Patronato.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

}
