package it.eng.sil.myaccount.controller.mbeans.accreditamentoForte;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.LazyAccreditamentoForteUtentiModel;
import it.eng.sil.myaccount.model.ejb.stateless.auth.GamificationClientMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "accreditamentoForteUtentiBean")
@ViewScoped
public class AccreditamentoForteUtentiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 1676843613398609021L;

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	private GamificationClientMyAccountEJB gamificationClientEJB;

	// Search form params
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String abilitazionePec;

	// Search results
	private LazyDataModel<UtenteInfo> lazyDataModel;
	private String tokenRichiesta;
	private UtenteInfo utenteDaAbilitareConPec;

	@Override
	protected void initPostConstruct() {
		nome = "";
		cognome = "";
		codiceFiscale = "";
		tokenRichiesta = "";
		abilitazionePec = null;
	}

	public void doSearch() {
		// Trasformo in Boolean il valore del pulsante "tutti/solo pec/no pec".
		Boolean booleanAbilitazionePec = null;
		if (abilitazionePec != null && abilitazionePec.equals("Y")) {
			booleanAbilitazionePec = true;
		} else if (abilitazionePec != null && abilitazionePec.equals("N")) {
			booleanAbilitazionePec = false;
		}

		// Recupero la provincia dell'utente che fa la ricerca.
		String codProvinciaRichiedente = null;
		if (getAccountInfoBean().getUserInfo().isProvincia()) {
			codProvinciaRichiedente = getAccountInfoBean().getUserInfo().getProvinciaPOJO().getCodProvincia();
		}

		lazyDataModel = new LazyAccreditamentoForteUtentiModel(utenteInfoEJB, nome, cognome, codiceFiscale,
				codProvinciaRichiedente, booleanAbilitazionePec);
	}

	/**
	 * Dà l'abilitazione forte ad un utente e gli invia una mail.
	 */
	public void abilitaUtenteNoPec(UtenteInfo daAbilitare) {
		try {
			pfPrincipalMyAccountEJB.setUtenteAbilitatoForteNoPec(daAbilitare.getPfPrincipal().getIdPfPrincipal(),
					accountInfoBean.getUserInfo().getIdPfPrincipal());

			// Aggiungo il badge 'Servizi Amministrativi Abilitati'
			gamificationClientEJB.assignServiziAmministrativiBadge(daAbilitare.getIdPfPrincipal(),
					ConstantsSingleton.Gamification.BADGE_COMPLETE);

			String msgok = utils.getUiProperty("data.updated");
			addJSSuccessMessage(msgok);
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}
	}

	/**
	 * Controlla se il token di richiesta registrazione forte è giustok, e genera il token da mandare in risposta
	 * all'utente per confermare la registrazione.
	 */
	public void abilitaUtentePec() {
		if (utenteDaAbilitareConPec.getPfPrincipal().getRichiestaRegForteToken() == null
				|| !utenteDaAbilitareConPec.getPfPrincipal().getRichiestaRegForteToken().equals(tokenRichiesta)) {
			String msgko = utils.getUiProperty("accreditamentoForte.wrongToken");
			addJSDangerMessage(msgko);
			return;
		}

		try {
			pfPrincipalMyAccountEJB.setUtenteAbilitatoForteConPec(utenteDaAbilitareConPec.getIdPfPrincipal(),
					accountInfoBean.getUserInfo().getIdPfPrincipal(), tokenRichiesta,
					utenteDaAbilitareConPec.getEmailPEC());
			String msgok = utils.getUiProperty("data.updated");
			addJSSuccessMessage(msgok);
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public LazyDataModel<UtenteInfo> getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(LazyDataModel<UtenteInfo> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public String getAbilitazionePec() {
		return abilitazionePec;
	}

	public void setAbilitazionePec(String abilitazionePec) {
		this.abilitazionePec = abilitazionePec;
	}

	public String getTokenRichiesta() {
		return tokenRichiesta;
	}

	public void setTokenRichiesta(String tokenRichiesta) {
		this.tokenRichiesta = tokenRichiesta;
	}

	public UtenteInfo getUtenteDaAbilitareConPec() {
		return utenteDaAbilitareConPec;
	}

	public void setUtenteDaAbilitareConPec(UtenteInfo utenteDaAbilitareConPec) {
		this.utenteDaAbilitareConPec = utenteDaAbilitareConPec;
	}
}
