package it.eng.sil.myaccount.controller.mbeans;

import it.eng.sil.myaccount.controller.mbeans.session.AccountInfoBean;
import it.eng.sil.myaccount.controller.mbeans.session.ProfilaturaBean;

import javax.faces.bean.ManagedProperty;

public abstract class SecureBackingBean extends AbstractBackingBean {

	/**
	 * Collegamento al SessionBean che contiene informazioni sulla sessione dell'utente che ha eseguito login.
	 */
	@ManagedProperty(value = "#{accountInfoBean}")
	protected AccountInfoBean accountInfoBean;

	/**
	 * Collegamento al ProfilaturaBean che contiene informazioni sulla profilatura dell'utente che ha eseguito login.
	 */
	@ManagedProperty(value = "#{profilatura}")
	protected ProfilaturaBean profilatura;

	public SecureBackingBean() {
		if (accountInfoBean == null) {
			if (getExternalContext().getSessionMap().containsKey("accountInfoBean")) {
				accountInfoBean = (AccountInfoBean) getExternalContext().getSessionMap().get("accountInfoBean");
			} else {
				String completePath = getExternalContext().getRequestContextPath() + "error/500";
				this.redirect(completePath);
			}
		}
	}

	@Override
	protected void postConstruct() {
		if (profilatura == null) {
			profilatura = new ProfilaturaBean();
		}

		if (getExternalContext().getSessionMap().containsKey("accountInfoBean")) {
			accountInfoBean = (AccountInfoBean) getExternalContext().getSessionMap().get("accountInfoBean");
		}

		log.debug("Secure Abstract Bean postConstruct");
		initPostConstruct();
	}

	public void checkAbilitatoViewPage(String codFunzione, String codOggetto) {
		if (!getProfilatura().isAbilitato(codFunzione, codOggetto)) {
			getExternalContext().setResponseStatus(403);
			redirect403();
			getFacesContext().responseComplete();
			return;
		}
	}

	/**
	 * Restituisce la sessione con tutte le informazioni ivi contenute.
	 *
	 * @return SessionBean
	 */
	public AccountInfoBean getAccountInfoBean() {
		return accountInfoBean;
	}

	public void setAccountInfoBean(AccountInfoBean sessione) {
		this.accountInfoBean = sessione;
	}

	public ProfilaturaBean getProfilatura() {
		if (profilatura == null) {
			profilatura = new ProfilaturaBean();
		}
		return profilatura;
	}

	public void setProfilatura(ProfilaturaBean profilatura) {
		this.profilatura = profilatura;
	}
}
