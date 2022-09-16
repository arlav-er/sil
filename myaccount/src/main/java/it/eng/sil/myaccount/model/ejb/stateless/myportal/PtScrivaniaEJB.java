package it.eng.sil.myaccount.model.ejb.stateless.myportal;

import it.eng.sil.myaccount.model.entity.myportal.PtPortlet;
import it.eng.sil.myaccount.model.entity.myportal.PtScrivania;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class PtScrivaniaEJB extends AbstractTabellaGestioneEJB<PtScrivania, Integer> {

	@EJB
	PtPortletEJB ptPortletEJB;

	@Override
	public String getFriendlyName() {
		return "Portlet posseduta da un utente";
	}

	@Override
	public Class<PtScrivania> getEntityClass() {
		return PtScrivania.class;
	}

	public List<PtScrivania> findByIdPrincipal(Integer idPfPrincipal) {
		TypedQuery<PtScrivania> query = entityMan.createNamedQuery("findPtScrivaniaByIdPfPrincipal", PtScrivania.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		return query.getResultList();
	}

	public void findAndRemove(Integer idPtPortlet) throws MyCasException {
		PtScrivania entity = findById(idPtPortlet);
		remove(entity);
	}

	public PtScrivania creaPtScrivania(Integer idPfPrincipal, PtPortlet ptPortlet, Integer idPrincipalIns)
			throws MyCasException {
		PtScrivania entity = new PtScrivania();
		entity.setIdPfPrincipal(idPfPrincipal);
		entity.setPtPortlet(ptPortletEJB.findById(ptPortlet.getIdPtPortlet()));
		entity.setFlagVisualizza(true);
		entity.setFlagRidotta(false);
		entity.setOptColonna("L");
		entity.setPosizione(1);
		return persist(idPrincipalIns, entity);
	}

}
