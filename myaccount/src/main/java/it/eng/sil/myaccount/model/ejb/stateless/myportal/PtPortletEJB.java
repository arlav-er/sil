package it.eng.sil.myaccount.model.ejb.stateless.myportal;

import it.eng.sil.myaccount.model.entity.myportal.PtPortlet;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class PtPortletEJB extends AbstractTabellaGestioneEJB<PtPortlet, Integer> {

	@Override
	public String getFriendlyName() {
		return "Portlet";
	}

	@Override
	public Class<PtPortlet> getEntityClass() {
		return PtPortlet.class;
	}

	public List<PtPortlet> findByCodRuoloPortale(String codRuoloPortale) {
		TypedQuery<PtPortlet> query = entityMan.createNamedQuery("findPtPortletByCodRuoloPortale", PtPortlet.class);
		query.setParameter("codRuoloPortale", codRuoloPortale);
		return query.getResultList();
	}

}
