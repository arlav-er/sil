package it.eng.sil.myaccount.model.ejb.stateless.decodifiche;

import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class DeComuneMyAccountEJB extends DeComuneEJB {

	@EJB
	private ConstantsSingleton constantsSingleton;

	public boolean comuneDomicilioInRegione(DeComune deComune) {
		return constantsSingleton.getCodRegione().equals(deComune.getDeProvincia().getDeRegione().getCodRegione());
	}

}
