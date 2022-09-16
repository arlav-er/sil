package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.myaccount.helpers.LazyPfPrincipalModel;
import it.eng.sil.myaccount.helpers.PfPrincipalFilter;
import it.eng.sil.myaccount.model.ejb.stateless.gamification.DeBadgeMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gamification.GpBadgeMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.DeSistemaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.myportal.PtPortletEJB;
import it.eng.sil.myaccount.model.ejb.stateless.myportal.PtScrivaniaEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.entity.myportal.PtPortlet;
import it.eng.sil.myaccount.model.entity.myportal.PtScrivania;
import it.eng.sil.myaccount.model.enums.TipoOrdinamento;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gamification.GpBadge;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilatura.PfIdentityProvider;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeMacroTipoEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfIdentityProviderEJB;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

/**
 * Questo bean è legato alla pagina "Gestione Utenti" del pannello di amministrazione della profilatura. Da questa
 * pagina l'amministratore può cercare un utente tramite vari parametri, e poi effettuare diverse operazioni.
 *
 * L'operazione più importante è la gestione delle profilature dell'utente. L'amministratore vede una lista delle
 * profilature e può aggiungerne e toglierne altre. Non è possibile aggiungere nè togliere profilature di default.
 * Inoltre, tutte le profilature di un utente devono essere legate allo stesso macro-tipo (per retrocompatibilità con
 * gli utenti già esistenti e i metodi precedenti di gestione dei profili).
 *
 * Inoltre, questa pagina contiene anche le funzionalità che prima erano nella pagina "Reset Password" del pannello di
 * amministrazione di MyPortal. L'amministratore può forzare la scadenza della password, resettare la password al valore
 * di default 'Temporanea123', rimandare la mail di attivazione account all'utente, abilitare l'account o cancellare
 * l'account (cancellazione logica, i dati rimangono sul DB).
 *
 * @author gicozza
 *
 */
@ManagedBean(name = "gpUtentiBean")
@ViewScoped
public class GpUtentiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 6616719117180907063L;
	private static final int SELECT_ONE_MENU_MAX_RESULTS = 10;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	@EJB
	GpDeMacroTipoEJB gpDeMacroTipoEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	DeSistemaMyAccountEJB deSistemaEJB;

	@EJB
	PfIdentityProviderEJB pfIdentityProviderEJB;

	@EJB
	PtPortletEJB ptPortletEJB;

	@EJB
	PtScrivaniaEJB ptScrivaniaEJB;

	@EJB
	EmailManager emailManager;

	@EJB
	GpBadgeEJB gpBadgeEJB;

	@EJB
	DeBadgeMyAccountEJB deBadgeMyAccountEJB;

	@EJB
	GpBadgeMyAccountEJB gpBadgeMyAccountEJB;

	@EJB
	UtenteInfoEJB utenteInfoEJB;

	private PfPrincipalFilter searchParams;
	private LazyPfPrincipalModel searchResults;
	private List<SelectItem> ordinamentoSelectList;

	private PfPrincipal utenteDetail;
	private GpDeMacroTipo tipoUtenteDetail;
	private String socialNetworkDetail;
	private List<GpProfilatura> profilatureDetail;

	private DeSistema addProfilaturaSistema;
	private GpRuolo addProfilaturaRuolo;
	private GpDeTipoGruppo addProfilaturaTipoGruppo;
	private GpGruppo addProfilaturaGruppo;
	private GpRuoloGruppo addProfilaturaRuoloGruppo;
	private GpGruppo modPadre;
	private GpDeTipoGruppoEnum modTipoPadre;

	private List<GpDeMacroTipo> macroTipi;
	private List<DeSistema> deSistemaList;
	private List<GpDeTipoGruppo> addProfilaturaTipoGruppoList;
	private List<GpRuoloGruppo> addProfilaturaRuoloGruppoList;

	private List<PtPortlet> portletDisponibili;
	private List<PtScrivania> portletAttive;
	private PtPortlet portletSelezionata;

	// Sezioni badge
	private List<GpBadge> ownedGpBadges;
	private List<GpBadge> availableGpBadges;

	private UtenteInfo utenteInfo;

	@Override
	protected void initPostConstruct() {
		searchParams = new PfPrincipalFilter();
		searchParams.setLikeSearch(true);
		macroTipi = gpDeMacroTipoEJB.findAllValide(new Date());
		deSistemaList = deSistemaEJB.findAllValideWithRuoli(new Date());

		ordinamentoSelectList = new ArrayList<SelectItem>();
		ordinamentoSelectList.add(new SelectItem(null, " -- "));
		for (TipoOrdinamento ordinamento : TipoOrdinamento.values()) {
			ordinamentoSelectList.add(new SelectItem(ordinamento, ordinamento.toString()));
		}

	}

	/**
	 * Inizializzo la DataList lazy contenente i risultati della ricerca, in base ai parametri inseriti da front-end. Se
	 * era visualizzato un utente nel dettaglio, lo tolgo.
	 */
	public void doSearch() {
		utenteDetail = null;
		tipoUtenteDetail = null;
		profilatureDetail = null;
		socialNetworkDetail = null;
		searchResults = new LazyPfPrincipalModel(pfPrincipalEJB, searchParams, true);
	}

	/**
	 * Carica un singolo utente, di cui verranno visualizzati i dettagli.
	 */
	public void loadDetail(Integer idPfPrincipal) {
		try {
			utenteDetail = pfPrincipalEJB.findById(idPfPrincipal);
		} catch (MyCasException e) {
			addJSWarnMessage("Attenzione: utente non trovato!");
			log.error(e.getMessage());
			utenteDetail = null;
		}
		
		try {
			utenteInfo = utenteInfoEJB.findById(idPfPrincipal);
		} catch (MyCasException e) {
			utenteInfo = null;//ci sta, per tutti i non-cittadini
		}

		// Trovo il Macro-Tipo dell'utente, che deve esistere ed essere solo uno.
		try {
			setTipoUtenteDetail(pfPrincipalEJB.findMacroTipoUtente(idPfPrincipal));
		} catch (MyCasException e) {
			setTipoUtenteDetail(null);
		}

		// Recupero l'eventuale Social Network dell'utente.
		try {
			socialNetworkDetail = null;
			PfIdentityProvider socialNetwork = pfIdentityProviderEJB
					.findByIdPfPrincipal(utenteDetail.getIdPfPrincipal());
			socialNetworkDetail = socialNetwork.getCodTipoProvider().toString();
		} catch (MyCasException e) {
			// Non è un errore, significa che non esiste un social network legato all'utente.
		}
		try {
			// Carico la lista delle profilature dell'utente e preparo il pannello di aggiunta nuova profilatura.
			profilatureDetail = gpProfilaturaEJB.findByFilter(utenteDetail, null, null, null, null);
		} catch (Exception e) {
			addJSDangerMessage("Errore nel caricamento delle profilature: " + e.getMessage());// Non è un errore
		}
		addProfilaturaSistema = null;
		addProfilaturaRuolo = null;
		addProfilaturaTipoGruppo = null;
		addProfilaturaGruppo = null;
		addProfilaturaRuoloGruppo = null;

		// Carico la lista delle portlet disponibili per l'utente E di quelle possedute dall'utente.
		if (utenteDetail != null && tipoUtenteDetail != null) {
			portletDisponibili = ptPortletEJB
					.findByCodRuoloPortale(utenteDetail.getDeRuoloPortale().getCodRuoloPortale());
			portletAttive = ptScrivaniaEJB.findByIdPrincipal(utenteDetail.getIdPfPrincipal());

			for (PtScrivania portletAttiva : portletAttive) {
				portletDisponibili.remove(portletAttiva.getPtPortlet());
			}
		}

		// Gestisco il caso in cui l'utente non sia profilato.
		if (tipoUtenteDetail != null) {
			addProfilaturaTipoGruppoList = gpDeTipoGruppoEJB.findValideByMacroTipo(tipoUtenteDetail.getCodMacroTipo(),
					new Date());
		} else {
			addProfilaturaTipoGruppoList = gpDeTipoGruppoEJB.findAllValide(new Date());
		}
		refreshAddProfilaturaParameters();

		ownedGpBadges = gpBadgeEJB.findByUserId(utenteDetail.getIdPfPrincipal());
		availableGpBadges = deBadgeMyAccountEJB.findVisibleBadgesByUser(utenteDetail.getIdPfPrincipal(),
				new ArrayList<GpBadge>(ownedGpBadges));
		availableGpBadges.removeAll(ownedGpBadges);
	}

	/**
	 * Restituisce il codice fiscale dell'utente visualizzato nel dettaglio.
	 */
	public String getCodiceFiscaleDetail() {
		if (utenteDetail == null || tipoUtenteDetail == null) {
			return "";
		} else if (tipoUtenteDetail.getCodMacroTipo().equals(GpDeMacroTipoEnum.CIT)
				&& utenteDetail.getUtenteInfo() != null) {
			if (ConstantsSingleton.USERID_ADIMIN.equals(utenteDetail.getIdPfPrincipal()))
				return "NA";
			return utenteDetail.getUtenteInfo().getCodiceFiscale();
		} else if (tipoUtenteDetail.getCodMacroTipo().equals(GpDeMacroTipoEnum.AZI)
				&& utenteDetail.getAziendaInfo() != null) {
			return utenteDetail.getAziendaInfo().getCodiceFiscale();
		} else {
			return "";
		}
	}

	/**
	 * Recupera la descrizione della provincia di riferimento dell'utente attualmente visualizzato.
	 *
	 * Cittadino: provincia del comune di domicilio;
	 *
	 * Azienda non abilitata SARE: provincia del comune della sede operativa;
	 *
	 * Azienda abilitata SARE: provincia di riferimento del SARE;
	 *
	 * Tutti gli altri: "Nessuna".
	 *
	 * @return
	 */
	public String getDetailProvinciaRiferimento() {
		if (utenteDetail == null || tipoUtenteDetail == null)
			return "Nessuna";

		// Faccio i controlli SIA sul macro-tipo di utente SIA sulla tabella collegata, perchè possono ancora esserci
		// discrepanze.
		if (tipoUtenteDetail.getCodMacroTipo().equals(GpDeMacroTipoEnum.CIT.toString())
				&& utenteDetail.getUtenteInfo() != null) {
			return utenteDetail.getUtenteInfo().getDeComuneDomicilio().getDeProvincia().getDescrizione();
		} else if (tipoUtenteDetail.getCodMacroTipo().toString().equals(GpDeMacroTipoEnum.AZI.toString())
				&& utenteDetail.getAziendaInfo() != null) {
			if (utenteDetail.getFlagAbilitatoSare() && utenteDetail.getAziendaInfo().getDeProvincia() != null) {
				// provincia di riferimento del SARE
				return utenteDetail.getAziendaInfo().getDeProvincia().getDescrizione();
			} else {
				return utenteDetail.getAziendaInfo().getDeComuneSede().getDeProvincia().getDescrizione();
			}
		} else if (tipoUtenteDetail.getCodMacroTipo().equals(GpDeMacroTipoEnum.PRV.toString())
				&& utenteDetail.getProvinciasForIdPfPrincipal() != null) {
			return utenteDetail.getProvinciasForIdPfPrincipal().getDeProvincia().getDescrizione();
		} else {
			return "Nessuna";
		}
	}

	/**
	 * Setta forzatamente il flgAbilitato dell'utente a true o a false.
	 */
	public void aggiornaAbilitaUtente() {
		try {
			utenteDetail = pfPrincipalEJB.gestioneAbilitaUtente(utenteDetail, getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("Abilitazione utente aggiornata.");
		} catch (MyCasException e) {
			log.error("Errore durante l'abilitazione dell'utente : " + e);
			addJSWarnMessage("Errore durante l'abilitazione dell'utente.");
		}
	}

	/**
	 * Aggiorna abilitazione al SARE azienda (attiva/disattiva)
	 */

	public void aggiornaAbilitaSARE() {

		try {
			utenteDetail = pfPrincipalEJB.gestioneAbilitazioneSARE(utenteDetail,
					getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("abilitazione SARE aggiornata.");
		} catch (MyCasException e) {
			log.error("Errore durante l'aggiornamento dell'abilitazione al SARE : " + e);
			addJSWarnMessage("Errore durante l'aggiornamento dell'abilitazione al SARE.");
		}

	}

	/**
	 * Aggiorna Abilitazione servizi utente (attiva/disattiva)
	 */
	public void aggiornaAbilitaServizi() {
		try {
			utenteDetail = pfPrincipalEJB.gestioneAbilitaServizi(utenteDetail, getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("Abilitazione servizi aggiornata.");
		} catch (MyCasException e) {
			log.error("Errore durante l'aggiornamento dell'abilitazione dei servizi : " + e);
			addJSWarnMessage("Errore durante l'aggiornamento dell' abilitazione dei servizi.");
		}
	}

	/**
	 * Fa scadere forzatamente la password dell'utente visualizzato.
	 */
	public void invalidaPassword() {
		try {
			utenteDetail = pfPrincipalEJB.invalidaPassword(utenteDetail, getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("Password invalidata con successo.");
		} catch (MyCasException e) {
			log.error("Errore durante l'invalidazione password : " + e);
			addJSWarnMessage("Errore durante l'invalidazione della password.");
		}
	}

	/**
	 * Resetta la password dell'utente visualizzato a quella di default.
	 */
	public void resetPassword() {
		try {
			utenteDetail = pfPrincipalEJB.resetDefaultPassword(utenteDetail, getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("Password resettata con successo.");
		} catch (MyCasException e) {
			log.error("Errore durante la reset password : " + e);
			addJSWarnMessage("Errore durante il reset della password.");
		}
	}

	/**
	 * Spedisce una nuova mail di attivazione account all'utente.
	 */
	public void inviaMail() {
		// Caso speciale per lo sviluppo, poi non dovrebbe accadere
		if (tipoUtenteDetail == null)
			return;

		// Decido quale mail spedire in base al macro-tipo dell'utente.
		switch (tipoUtenteDetail.getCodMacroTipo()) {
			case CIT:
			case PRV:
			case REG:
				String codProvinciaRiferimentoCit = (utenteDetail.getUtenteInfo() != null
						&& utenteDetail.getUtenteInfo().getDeProvincia() != null)
						? utenteDetail.getUtenteInfo().getDeProvincia().getCodProvincia()
						: null;
				String activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/"
						+ utenteDetail.getUsername() + "/" + utenteDetail.getEmail() + "/"
						+ utenteDetail.getConfirmationToken();
				emailManager.sendRegisterCittadino(utenteDetail.getNome(), utenteDetail.getUsername(), activationLink,
						utenteDetail.getEmail(), codProvinciaRiferimentoCit);
				break;
			case AZI:
				String codProvinciaRiferimentoAzi = (utenteDetail.getAziendaInfo() != null
						&& utenteDetail.getAziendaInfo().getDeProvincia() != null)
						? utenteDetail.getAziendaInfo().getDeProvincia().getCodProvincia()
						: null;
				activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/" + utenteDetail.getUsername()
						+ "/" + utenteDetail.getEmail() + "/" + utenteDetail.getConfirmationToken();
				emailManager.sendRegisterAzienda(utenteDetail.getNome(), utenteDetail.getUsername(), activationLink,
						utenteDetail.getEmail(), codProvinciaRiferimentoAzi);
				break;
			default:
				log.error("Errore: l'utente ha macro-tipo " + tipoUtenteDetail);
				addJSWarnMessage("Attenzione: l'utente ha un macro-tipo non corretto!");
		}
	}

	/**
	 * Effettua la cancellazione logica dell'utente visualizzato.
	 */
	public void cancellaUtente() {
		try {
			utenteDetail = pfPrincipalEJB.cancellaUtente(utenteDetail, getAccountInfoBean().getIdPfPrincipal());
			addJSSuccessMessage("Utente cancellato con successo.");
		} catch (MyCasException e) {
			log.error("Errore durante la cancellazione dell'utente : " + e);
			addJSWarnMessage("Errore durante la cancellazione dell'utente.");
		}
	}

	/**
	 * Rimuove una profilatura dall'utente attualmente visualizzato, a meno che non sia l'ultima che gli rimane.
	 */
	public void rimuoviProfilatura(Integer idGpProfilatura) {
		if (profilatureDetail != null && profilatureDetail.size() > 1) {
			try {
				gpProfilaturaEJB.remove(idGpProfilatura);
			} catch (MyCasException e) {
				log.error("Errore durante la rimozione della profilatura: " + e.getMessage());
				addJSDangerMessage("Errore durante la rimozione della profilatura");
			}
			profilatureDetail = gpProfilaturaEJB.findForIdPfPrincipal(utenteDetail.getIdPfPrincipal());
		} else {
			addJSDangerMessage("Un utente non può rimanere senza profilature");
		}
	}

	/**
	 * Metodo legato all'elemento Autocomplete che seleziona il gruppo della profilatura da aggiungere. Escludo i gruppi
	 * astratti, dato che non possono essere legati a profilature.
	 */
	public List<GpGruppo> autocompleteGruppo(String par) {
		GpGruppoFilter filtro = new GpGruppoFilter().setDescrizione(par).setIncludiGruppiAstratti(false);
		if (tipoUtenteDetail != null) {
			filtro.setCodMacroTipo(tipoUtenteDetail.getCodMacroTipo());
		}

		if (addProfilaturaTipoGruppo != null) {
			filtro.setCodTipoGruppo(addProfilaturaTipoGruppo.getCodTipoGruppo());
		}

		if (modPadre != null) {
			filtro.setIdPadre(modPadre.getIdGpGruppo());
		}

		return gpGruppoEJB.findByFilter(filtro, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Metodo legato al componente Autocomplete che seleziona il gruppo da modificare. Se non specifico un tipo, allora
	 * includo anche i gruppi astratti.
	 */
	public List<GpGruppo> autocompleteModPadre(String par) {
		GpGruppoFilter filtro = new GpGruppoFilter().setDescrizione(par.trim()).setIncludiGruppiAstratti(true);
		if (tipoUtenteDetail != null) {
			filtro.setCodMacroTipo(tipoUtenteDetail.getCodMacroTipo());
		}

		if (addProfilaturaTipoGruppo != null) {
			filtro.setCodTipoGruppo(addProfilaturaTipoGruppo.getCodTipoGruppo());
		}
		if (modTipoPadre != null) {
			filtro.setCodTipoGruppo(modTipoPadre);
		}
		filtro.setSoloGruppiPadre(true);
		List<GpGruppo> tmpList = gpGruppoEJB.findByFilter(filtro, SELECT_ONE_MENU_MAX_RESULTS);

		Set<GpGruppo> uniques = new HashSet<>();
		uniques.addAll(tmpList);
		tmpList.clear();
		tmpList.addAll(uniques);
		// distinct un po` cosi`

		return tmpList;

	}

	/**
	 * Metodo chiamato per assicurarsi che i parametri di ricerca del gruppo da visualizzare/modificare siano
	 * compatibili tra loro (tipo padre con padre, tipo gruppo con gruppo).
	 */
	public void onModGruppoParamChange() {
		if (modTipoPadre != null && modPadre != null) {
			modPadre = null;
		}

	}

	/**
	 * Metodo legato all'elemento Autocomplete che seleziona il ruolo della profilatura da aggiungere.
	 */
	public List<GpRuolo> autocompleteRuolo(String par) {
		return gpRuoloEJB.findByFilter(par, addProfilaturaSistema);
	}

	/**
	 * Controllo che i parametri selezionati nel pannello "aggiungi profilatura" siano compatibili tra loro.
	 */
	public void refreshAddProfilaturaParameters() {
		// Se è selezionato un ruolo non adatto al sistema, lo deseleziono.
		if (addProfilaturaRuolo != null && addProfilaturaSistema != null
				&& !addProfilaturaRuolo.getDeSistema().equals(addProfilaturaSistema)) {
			addProfilaturaRuolo = null;
		}

		// Se è selezionato un gruppo non adatto al tipoGruppo, lo deseleziono.
		if (addProfilaturaGruppo != null && addProfilaturaTipoGruppo != null
				&& !addProfilaturaTipoGruppo.equals(addProfilaturaGruppo.getGpDeTipoGruppo())) {
			addProfilaturaGruppo = null;
		}

		/*
		 * if (modTipoPadre != null) modTipoPadre = null;
		 */
		// Refresh della lista di profilature selezionabili in basi a tutti i parametri.
		addProfilaturaRuoloGruppoList = gpRuoloGruppoEJB.findByFilter(utenteDetail.getIdPfPrincipal(),
				addProfilaturaRuolo, addProfilaturaSistema, addProfilaturaGruppo, addProfilaturaTipoGruppo,
				tipoUtenteDetail, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Aggiunge una profilatura all'utente attualmente visualizzato.
	 */
	public void addProfilatura() {
		try {
			gpProfilaturaEJB.add(utenteDetail.getIdPfPrincipal(), addProfilaturaRuoloGruppo,
					accountInfoBean.getIdPfPrincipal());
			profilatureDetail = gpProfilaturaEJB.findForIdPfPrincipal(utenteDetail.getIdPfPrincipal());
			addProfilaturaRuoloGruppoList.remove(addProfilaturaRuoloGruppo);
			addJSSuccessMessage("Profilatura aggiunta con successo");
		} catch (MyCasException e) {
			log.error("Errore durante l'assegnazione della profilatura: " + e.getMessage());
			addJSWarnMessage("Errore durante l'assegnazione della profilatura");
		}
	}

	/**
	 * Se l'amministratore, mentre cerca di assegnare una profilatura all'utente visualizzato, seleziona un ruolo ed un
	 * gruppo che NON hanno un ruoloGruppo corrispondente, gli viene data la possibilità di crearli al volo.
	 *
	 * @return
	 */
	public boolean canCreaNuovaProfilatura() {
		if (addProfilaturaGruppo == null || addProfilaturaRuolo == null)
			return false;
		for (GpProfilatura profilaturaAssegnata : profilatureDetail) {
			if (profilaturaAssegnata.getGpRuoloGruppo().getGpRuolo().equals(addProfilaturaRuolo)
					&& profilaturaAssegnata.getGpRuoloGruppo().getGpGruppo().equals(addProfilaturaGruppo)) {
				return false;
			}
		}

		// Ritorno true solo se l'amministratore ha selezionato ruolo e gruppo e NON esiste già una
		// profilatura corrispondente a quel ruolo e quel gruppo tra le profilature dell'utente.
		return true;
	}

	/**
	 * Crea al volo un RuoloGruppo e lo rende disponibile per l'assegnazione all'utente visualizzato.
	 */
	public void creaProfilaturaNonEsistente() {
		try {
			GpRuoloGruppo newRuoloGruppo = gpRuoloGruppoEJB.add(addProfilaturaRuolo, addProfilaturaGruppo, false,
					getAccountInfoBean().getIdPfPrincipal());
			addProfilaturaRuoloGruppoList.add(newRuoloGruppo);
		} catch (MyCasException e) {
			log.error("Errore durante l'inserimento dati: " + e);
			addJSWarnMessage("Errore durante la creazione della profilatura");
		}
	}

	/**
	 * Aggiunge il badge ai badge ottenuti dall'utente
	 *
	 * @param badge
	 *            , badge da aggiungere
	 */
	public void addBadge(GpBadge badge) {
		try {
			GpBadge newBadge = gpBadgeEJB.assignBadge(badge.getDeBadge().getCodBadge(), utenteDetail.getIdPfPrincipal(),
					badge.getValue());
			addJSSuccessMessage("Badge aggiunto con successo");
			// Aggiornamento liste frontEnd
			ownedGpBadges.add(newBadge);
			availableGpBadges.remove(availableGpBadges.indexOf(badge));
		} catch (GamificationException e) {
			log.error("Errore durante assegnazione badge: " + e);
			addJSDangerMessage("Impossibile aggiungere il badge all'utente " + utenteDetail.getNome() + " "
					+ utenteDetail.getCognome());
		}
	}

	/**
	 * Rimuove il badge all'utente
	 *
	 * @param badge
	 */
	public void removeBadge(GpBadge badge) {
		try {
			gpBadgeMyAccountEJB.removeBadgeFromUser(badge.getIdGpBadge());
			addJSSuccessMessage("Badge rimosso con successo");
			// Aggiornamento liste frontEnd
			ownedGpBadges.remove(ownedGpBadges.indexOf(badge));
			badge.setValue(0);
			availableGpBadges.add(badge);
		} catch (MyCasException e) {
			log.error("Errore durante la rimozione badge: " + e);
			addJSDangerMessage("Impossibile rimuovere il badge dall'utente " + utenteDetail.getNome() + " "
					+ utenteDetail.getCognome());
		}
	}

	/**
	 * Tolgo una portlet all'utente attuamente visualizzato.
	 */
	public void rimuoviPortlet(PtScrivania daRimuovere) {
		try {
			ptScrivaniaEJB.findAndRemove(daRimuovere.getIdPtScrivania());
			portletAttive.remove(daRimuovere);
			portletDisponibili.add(daRimuovere.getPtPortlet());
		} catch (MyCasException e) {
			log.error("Errore durante rimozione portlet: " + e.getMessage());
			addJSDangerMessage("Errore nella rimozione della portlet.");
		}
	}

	/**
	 * Aggiungo una portlet all'utente attuamente visualizzato.
	 */
	public void aggiungiPortlet() {
		try {
			PtScrivania nuovaScrivania = ptScrivaniaEJB.creaPtScrivania(utenteDetail.getIdPfPrincipal(),
					portletSelezionata, accountInfoBean.getIdPfPrincipal());
			portletDisponibili.remove(portletSelezionata);
			portletAttive.add(nuovaScrivania);
		} catch (MyCasException e) {
			log.error("Errore durante aggiunta portlet: " + e.getMessage());
			addJSDangerMessage("Errore nell'assegnamento della portlet.");
		}
	}

	public void setAddProfilaturaSistema(DeSistema addProfilaturaSistema) {
		this.addProfilaturaSistema = addProfilaturaSistema;
	}

	public PfPrincipalFilter getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(PfPrincipalFilter searchParams) {
		this.searchParams = searchParams;
	}

	public LazyPfPrincipalModel getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(LazyPfPrincipalModel searchResults) {
		this.searchResults = searchResults;
	}

	public PfPrincipal getUtenteDetail() {
		return utenteDetail;
	}

	public void setUtenteDetail(PfPrincipal utenteDetail) {
		this.utenteDetail = utenteDetail;
	}

	public List<GpProfilatura> getProfilatureDetail() {
		return profilatureDetail;
	}

	public void setProfilatureDetail(List<GpProfilatura> profilatureDetail) {
		this.profilatureDetail = profilatureDetail;
	}

	public DeSistema getAddProfilaturaSistema() {
		return addProfilaturaSistema;
	}

	public List<DeSistema> getDeSistemaList() {
		return deSistemaList;
	}

	public void setDeSistemaList(List<DeSistema> deSistemaList) {
		this.deSistemaList = deSistemaList;
	}

	public GpRuolo getAddProfilaturaRuolo() {
		return addProfilaturaRuolo;
	}

	public GpGruppo getAddProfilaturaGruppo() {
		return addProfilaturaGruppo;
	}

	public void setAddProfilaturaRuolo(GpRuolo addProfilaturaRuolo) {
		this.addProfilaturaRuolo = addProfilaturaRuolo;
	}

	public void setAddProfilaturaGruppo(GpGruppo addProfilaturaGruppo) {
		this.addProfilaturaGruppo = addProfilaturaGruppo;
	}

	public GpRuoloGruppo getAddProfilaturaRuoloGruppo() {
		return addProfilaturaRuoloGruppo;
	}

	public void setAddProfilaturaRuoloGruppo(GpRuoloGruppo addProfilaturaRuoloGruppo) {
		this.addProfilaturaRuoloGruppo = addProfilaturaRuoloGruppo;
	}

	public List<GpRuoloGruppo> getAddProfilaturaRuoloGruppoList() {
		return addProfilaturaRuoloGruppoList;
	}

	public void setAddProfilaturaRuoloGruppoList(List<GpRuoloGruppo> addProfilaturaRuoloGruppoList) {
		this.addProfilaturaRuoloGruppoList = addProfilaturaRuoloGruppoList;
	}

	public GpDeMacroTipo getTipoUtenteDetail() {
		return tipoUtenteDetail;
	}

	public void setTipoUtenteDetail(GpDeMacroTipo tipoUtenteDetail) {
		this.tipoUtenteDetail = tipoUtenteDetail;
	}

	public List<GpDeMacroTipo> getMacroTipi() {
		return macroTipi;
	}

	public void setMacroTipi(List<GpDeMacroTipo> macroTipi) {
		this.macroTipi = macroTipi;
	}

	public List<SelectItem> getOrdinamentoSelectList() {
		return ordinamentoSelectList;
	}

	public void setOrdinamentoSelectList(List<SelectItem> ordinamentoSelectList) {
		this.ordinamentoSelectList = ordinamentoSelectList;
	}

	public GpDeTipoGruppo getAddProfilaturaTipoGruppo() {
		return addProfilaturaTipoGruppo;
	}

	public void setAddProfilaturaTipoGruppo(GpDeTipoGruppo addProfilaturaTipoGruppo) {
		this.addProfilaturaTipoGruppo = addProfilaturaTipoGruppo;
	}

	public List<GpDeTipoGruppo> getAddProfilaturaTipoGruppoList() {
		return addProfilaturaTipoGruppoList;
	}

	public void setAddProfilaturaTipoGruppoList(List<GpDeTipoGruppo> addProfilaturaTipoGruppoList) {
		this.addProfilaturaTipoGruppoList = addProfilaturaTipoGruppoList;
	}

	public String getSocialNetworkDetail() {
		return socialNetworkDetail;
	}

	public void setAvailableGpBadges(List<GpBadge> availableGpBadges) {
		this.availableGpBadges = availableGpBadges;
	}

	public List<GpBadge> getAvailableGpBadges() {
		return this.availableGpBadges;
	}

	public void setOwnedGpBadges(List<GpBadge> ownedGpBadges) {
		this.ownedGpBadges = ownedGpBadges;
	}

	public List<GpBadge> getOwnedGpBadges() {
		return this.ownedGpBadges;
	}

	public List<PtPortlet> getPortletDisponibili() {
		return portletDisponibili;
	}

	public void setPortletDisponibili(List<PtPortlet> portletDisponibili) {
		this.portletDisponibili = portletDisponibili;
	}

	public List<PtScrivania> getPortletAttive() {
		return portletAttive;
	}

	public void setPortletAttive(List<PtScrivania> portletAttive) {
		this.portletAttive = portletAttive;
	}

	public PtPortlet getPortletSelezionata() {
		return portletSelezionata;
	}

	public void setPortletSelezionata(PtPortlet portletSelezionata) {
		this.portletSelezionata = portletSelezionata;
	}

	public GpGruppo getModPadre() {
		return modPadre;
	}

	public void setModPadre(GpGruppo modPadre) {
		this.modPadre = modPadre;
	}

	public GpDeTipoGruppoEnum getModTipoPadre() {
		return modTipoPadre;
	}

	public void setModTipoPadre(GpDeTipoGruppoEnum modTipoPadre) {
		this.modTipoPadre = modTipoPadre;
	}

	public UtenteInfo getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfo utenteInfo) {
		this.utenteInfo = utenteInfo;
	}

	public void updateDeAutorizzazioneSARE() {
		try {
			utenteDetail.setAziendaInfo(
					aziendaInfoEJB.merge(accountInfoBean.getIdPfPrincipal(), utenteDetail.getAziendaInfo()));
		} catch (MyCasException e) {
			log.error("Errore durante l'aggiornamento dello stato SARE: " + e.getMessage());
			addJSDangerMessage("Errore durante l'aggiornamento dello stato SARE");
		}
	}
}
