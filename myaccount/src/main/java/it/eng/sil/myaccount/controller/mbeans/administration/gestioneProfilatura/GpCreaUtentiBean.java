package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.ProvinciaEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.RegioneEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.decodifiche.DeRegione;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.entity.profilo.Regione;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeMacroTipoEJB;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Questo bean è collegato alla pagina "Creazione utenti" del pannello di amministrazione della profilatura. Da questa
 * pagina è possibile creare nuovi utenti di macro-tipo "Provincia" e "Regione".
 * 
 * Questa pagina sostituisce (o meglio, andrà a sostituire) le pagine simili del pannello di amministrazione di
 * MyPortal.
 * 
 * @author gicozza
 *
 */
@ManagedBean(name = "gpCreaUtentiBean")
@ViewScoped
public class GpCreaUtentiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 4328309753703769186L;

	@EJB
	GpDeMacroTipoEJB gpDeMacroTipoEJB;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	DeRegioneEJB deRegioneEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@EJB
	DeComuneEJB deComuneEJB;

	@EJB
	RegioneEJB regioneEJB;

	@EJB
	ProvinciaEJB provinciaEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	EmailManager emailManager;

	private PfPrincipal newUtente;
	private Regione newRegione;
	private Provincia newProvincia;
	private AziendaInfo newEntePubblico;
	private String newEnteScrivania;
	private String newProvScrivania;

	@Override
	protected void initPostConstruct() {
		newUtente = new PfPrincipal();
		newProvincia = new Provincia();
		newRegione = new Regione();
		newEntePubblico = new AziendaInfo();
	}

	/**
	 * Metodo collegato all'elemento Autocomplete che fa scegliere una regione qualsiasi.
	 */
	public List<DeRegione> autocompleteRegione(String par) {
		return deRegioneEJB.findValideStartingWith(par, new Date());
	}

	/**
	 * Metodo collegato all'elemento Autocomplete che fa scegliere una provincia qualsiasi.
	 */
	public List<DeProvincia> autocompleteProvincia(String par) {
		return deProvinciaEJB.findValideStartingWith(par, new Date());
	}

	/**
	 * Conferma la creazione di un nuovo utente regionale : crea la riga su PfPrincipal, la riga su Regione, assegna
	 * ruoli e gruppi di default sia per la vecchia profilatura che per la nuova. L'utente è già abilitato.
	 */
	public void confermaCreazioneRegione() {
		log.debug("Registrazione regione : save()");
		try {
			newUtente.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			regioneEJB.register(newUtente, newRegione, getAccountInfoBean().getIdPfPrincipal());
			log.debug("Registrato utente regionale");

			newUtente = new PfPrincipal();
			newRegione = new Regione();
			addJSSuccessMessage("Creazione utente regionale effettuata con successo");
		} catch (MyCasException e) {
			log.error("Errore durante registrazione utente provinciale: " + e);
			addJSWarnMessage("Creazione utente regionale fallita!");
		}
	}

	/**
	 * Conferma la creazione di un nuovo utente provinciale : crea la riga su PfPrincipal, la riga su Provincia, assegna
	 * ruoli e gruppi di default sia per la vecchia profilatura che per la nuova. L'utente è già abilitato.
	 */
	public void confermaCreazioneProvincia() {
		log.debug("Registrazione provincia : save()");
		try {
			newUtente.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			provinciaEJB.register(newUtente, newProvincia, getAccountInfoBean().getIdPfPrincipal(), newProvScrivania);
			log.debug("Registrato utente provinciale");

			newUtente = new PfPrincipal();
			newProvincia = new Provincia();
			addJSSuccessMessage("Creazione utente provinciale effettuata con successo");
		} catch (MyCasException e) {
			log.error("Errore durante registrazione utente provinciale: " + e);
			addJSWarnMessage("Creazione utente provinciale fallita!");
		}
	}

	/**
	 * Conferma la creazione di un nuovo utente "ente pubblico", ovvero utente azienda : crea la riga su PfPrincipal, la
	 * riga su AziendaInfo, assegna ruoli e gruppi di default sia per la vecchia profilatura che per la nuova e manda la
	 * mail di conferma.
	 */
	public void confermaCreazioneEntePubblico() {
		log.debug("Registrazione ente pubblico : save()");
		try {
			// Aggiungo informazioni che sono obbligatorie su DB
			newEntePubblico.setCapSede(newEntePubblico.getDeComuneSede().getCap());
			newEntePubblico.setFlagValida(false);
			newUtente.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

			// Creo il nuovo utente.
			newEntePubblico = aziendaInfoEJB.registerFromPannello(newUtente, newEntePubblico, newEnteScrivania,
					getAccountInfoBean().getIdPfPrincipal());
			log.debug("Registrato utente ente pubblico");

			// Mando la mail di conferma.
			String activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/"
					+ newUtente.getUsername() + "/" + newUtente.getEmail() + "/" + newUtente.getConfirmationToken();
			String codProvinciaRiferimento = newEntePubblico.getDeProvincia() == null ? null : newEntePubblico
					.getDeProvincia().getCodProvincia();
			emailManager.sendRegisterAzienda(newUtente.getNome(), newUtente.getUsername(), activationLink,
					newUtente.getEmail(), codProvinciaRiferimento);

			// Svuoto l'utente visualizzato.
			newUtente = new PfPrincipal();
			newEntePubblico = new AziendaInfo();
			addJSSuccessMessage("Creazione utente ente pubblico effettuata con successo");
		} catch (MyCasException e) {
			log.error("Errore durante registrazione utente ente pubblico: " + e);
			addJSWarnMessage("Creazione utente ente pubblico fallita!");
		}
	}

	public List<DeComune> autocompleteComuneSede(String par) {
		return deComuneEJB.findComuniItaValideStartingWith(par, new Date());
	}

	public PfPrincipal getNewUtente() {
		return newUtente;
	}

	public void setNewUtente(PfPrincipal newUtente) {
		this.newUtente = newUtente;
	}

	public Provincia getNewProvincia() {
		return newProvincia;
	}

	public void setNewProvincia(Provincia newProvincia) {
		this.newProvincia = newProvincia;
	}

	public Regione getNewRegione() {
		return newRegione;
	}

	public void setNewRegione(Regione newRegione) {
		this.newRegione = newRegione;
	}

	public AziendaInfo getNewEntePubblico() {
		return newEntePubblico;
	}

	public void setNewEntePubblico(AziendaInfo newEntePubblico) {
		this.newEntePubblico = newEntePubblico;
	}

	public String getNewEnteScrivania() {
		return newEnteScrivania;
	}

	public void setNewEnteScrivania(String newEnteScrivania) {
		this.newEnteScrivania = newEnteScrivania;
	}

	public String getNewProvScrivania() {
		return newProvScrivania;
	}

	public void setNewProvScrivania(String newProvScrivania) {
		this.newProvScrivania = newProvScrivania;
	}

}
