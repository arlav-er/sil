package it.eng.sil.myaccount.controller.mbeans.decodifiche;

import it.eng.sil.myaccount.controller.mbeans.AbstractBackingBean;
import it.eng.sil.mycas.model.entity.decodifiche.DeCittadinanza;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean(name = "decodificheAutoComplete")
@ViewScoped
public class DecodificheAutoComplete extends AbstractBackingBean implements Serializable {
	private static final long serialVersionUID = -3046959054383089561L;

	protected static Log log = LogFactory.getLog(DecodificheAutoComplete.class);

	@EJB
	private DeComuneEJB deComuneEJB;

	@EJB
	private DeCittadinanzaEJB deCittadinanzaEJB;

	@Override
	protected void initPostConstruct() {

	}

	public List<DeComune> completeComuneAll(String startsWith) {
		log.debug("Called completeComune: " + startsWith);
		return deComuneEJB.findContaining(startsWith);
	}

	public List<DeComune> completeComune(String startsWith) {
		log.debug("Called completeComune: " + startsWith);
		return deComuneEJB.findComuniItaValideContaining(startsWith, new Date());
	}

	public List<DeCittadinanza> completeCittadinanza(String startsWith) {
		log.debug("Called completeCittadinanza: " + startsWith);
		return deCittadinanzaEJB.findValideContaining(startsWith, new Date());
	}

	/**
	 * Per qualche ragione, la VDA vuole che nell'autocomplete del comune di nascita vengano inclusi SOLO i comuni
	 * validi. Le altre regioni vogliono anche quelli scaduti.
	 */
	public List<DeComune> completeComuneNascita(String startsWith) {
		if (constantsSingleton.isVDA()) {
			return deComuneEJB.findValideContaining(startsWith, new Date());
		} else {
			return deComuneEJB.findContaining(startsWith);
		}
	}

}
