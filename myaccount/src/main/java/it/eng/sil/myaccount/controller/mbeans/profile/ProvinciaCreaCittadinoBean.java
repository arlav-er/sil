package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeComuneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean(name = "provinciaCreaCittadinoBean")
public class ProvinciaCreaCittadinoBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 8795358941315163621L;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	UtenteInfoEJB utenteInfoEJB;

	@EJB
	DeComuneMyAccountEJB deComuneEJB;

	private UtenteInfo newCittadino;

	@Override
	protected void initPostConstruct() {
		newCittadino = new UtenteInfo();
		newCittadino.setPfPrincipal(new PfPrincipal());
	}

	/**
	 * Se il comune di domicilio è all'interno della regione del portale, setto automaticamente la sua provincia come
	 * provincia di riferimento.
	 */
	public void comuneDomicilioChanged() {
		if (deComuneEJB.comuneDomicilioInRegione(newCittadino.getDeComuneDomicilio())) {
			newCittadino.setDeProvincia(newCittadino.getDeComuneDomicilio().getDeProvincia());
		}
	}

	public void confermaCreazione() {
		try {
			newCittadino = utenteInfoEJB.registerFromPannello(newCittadino, accountInfoBean.getIdPfPrincipal(), true);
			addJSSuccessMessage("Registrato utente " + newCittadino.getPfPrincipal().getUsername() + " con ID "
					+ newCittadino.getPfPrincipal().getIdPfPrincipal());
			newCittadino = new UtenteInfo();
			newCittadino.setPfPrincipal(new PfPrincipal());
		} catch (Exception e) {
			log.error("Errore durante la creazione di nuovo cittadino da parte di utente provincia: " + e.toString());
			addJSDangerMessage("Errore durante la creazione del nuovo utente");
		}
	}

	public String currentRegione() {
		return constantsSingleton.getCodRegione();
	}

	public UtenteInfo getNewCittadino() {
		return newCittadino;
	}

	public void setNewCittadino(UtenteInfo newCittadino) {
		this.newCittadino = newCittadino;
	}

}
