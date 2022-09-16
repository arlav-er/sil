package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.myaccount.helpers.LazyPfPrincipalModel;
import it.eng.sil.myaccount.helpers.PfPrincipalFilter;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeComuneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.DeSistemaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;

/**
 * Questo bean è legato alla pagina "Gestione Gruppi" del pannello di amministrazione. Da questa pagina l'amministratore
 * può creare nuovi gruppi o gestire gruppi già esistenti. Per il momento non è possibile cancellare un gruppo, neanche
 * logicamente.
 * 
 * Durante la creazione di un gruppo, l'amministratore può assegnargli un padre. Questo padre deve essere O un gruppo
 * astratto, OPPURE un gruppo facente parte dello stesso macro-tipo del gruppo che si sta creando. Per esempio, un
 * gruppo azienda-CPI può avere come padre un gruppo astratto o un gruppo azienda-ASL, ma non un gruppo
 * cittadino-default.
 * 
 * Nella sezione di gestione di gruppi già esistenti, l'amministratore può modificare la descrizione del gruppo, creare
 * nuove possibili profilature (righe in GpRuoloGruppo) per il gruppo o cancellare profilature già esistenti.
 * 
 * @author gicozza
 */
@ManagedBean(name = "gpGruppiBean")
@ViewScoped
public class GpGruppiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = -1666909143304972845L;
	private static final int SELECT_ONE_MENU_MAX_RESULTS = 10;

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	DeSistemaMyAccountEJB deSistemaEJB;

	@EJB
	DeComuneMyAccountEJB deComuneEJB;

	private GpDeTipoGruppoEnum modTipoPadre;
	private GpDeTipoGruppoEnum modTipoGruppo;
	private GpGruppo modPadre;
	private GpGruppo modGruppo;
	private List<GpRuoloGruppo> modProfilatureList;
	private LazyPfPrincipalModel modUtentiList;

	private DeSistema addProfilaturaSistema;
	private GpRuolo addProfilaturaRuolo;
	private List<DeSistema> addProfilaturaSistemaList;
	private DeComune addComune;

	private GpDeTipoGruppo creaTipoGruppo;
	private GpGruppo creaGruppoPadre;
	private String creaDescrizione;
	private DeComune creaComune;

	private List<GpDeTipoGruppo> tipoGruppoList;

	@Override
	protected void initPostConstruct() {
		//tipoGruppoList = gpDeTipoGruppoEJB.findAllValide(new Date());
		//TODO replace with GpDeTipoGruppoEnum.getSupportedList()
		EnumSet<GpDeTipoGruppoEnum> all = EnumSet.allOf( GpDeTipoGruppoEnum.class);
		List<GpDeTipoGruppoEnum> listGP = new ArrayList<>( all.size());
		for (GpDeTipoGruppoEnum s : all) {
			listGP.add( s);
		}
		tipoGruppoList = gpDeTipoGruppoEJB.getGpDeTipoGruppoValideByEnumList(listGP, new Date());
		addProfilaturaSistemaList = deSistemaEJB.findAllValideWithRuoli(new Date());
	}

	/**
	 * Genera la lista dei possibili padri per un gruppo che si sta creando.
	 */
	public List<GpGruppo> autocompleteCreaPadre(String par) {
		return gpGruppoEJB.findPossibiliPadri(par, creaTipoGruppo, null, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Metodo legato all'Autocomplete che filtra per padre il gruppo da modificare.
	 */
	public List<GpGruppo> autocompleteModPadre(String par) {
				
		GpGruppoFilter padriFilter = new GpGruppoFilter()
				.setDescrizione(par.trim())
				.setCodTipoGruppo(modTipoPadre)
				.setIncludiGruppiAstratti(true)
				.setIncludiGruppiConcreti(true);
				return gpGruppoEJB.findByFilter(padriFilter, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Metodo legato al componente Autocomplete che seleziona il gruppo da modificare. Se non specifico un tipo, allora
	 * includo anche i gruppi astratti.
	 */
	public List<GpGruppo> autocompleteModGruppo(String par) {
		GpGruppoFilter filtro = new GpGruppoFilter().setDescrizione(par).setCodTipoGruppo(modTipoGruppo)
				.setIncludiGruppiAstratti(modTipoGruppo == null);

		if (modPadre != null) {
			filtro.setIdPadre(modPadre.getIdGpGruppo());
		}
		return gpGruppoEJB.findByFilter(filtro, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Metodo chiamato per assicurarsi che i parametri di ricerca del gruppo da visualizzare/modificare siano
	 * compatibili tra loro (tipo padre con padre, tipo gruppo con gruppo).
	 */
	public void onModGruppoParamChange() {
		if (modTipoPadre != null && modPadre != null && !modTipoPadre.equals(modPadre.getGpDeTipoGruppo())) {
			modPadre = null;
		}

		if (modTipoGruppo != null && modGruppo != null && !modTipoGruppo.equals(modGruppo.getGpDeTipoGruppo())) {
			modGruppo = null;
		}
	}

	/**
	 * Metodo chiamato per assicurarsi che il tipo del gruppo che si sta creando ed il tipo dell'eventuale gruppo padre
	 * che gli si sta assegnando siano compatibili.
	 */
	public void onCreaGruppoParamChange() {
		if (creaTipoGruppo != null) {
			// Condizione 1: se il gruppo è concreto, il padre deve essere astratto oppure avere lo stesso macro-tipo.
			if (creaGruppoPadre != null
					&& creaGruppoPadre.getGpDeTipoGruppo() != null
					&& !creaTipoGruppo.getGpDeMacroTipo()
							.equals(creaGruppoPadre.getGpDeTipoGruppo().getGpDeMacroTipo())) {
				creaGruppoPadre = null;
			}
		} else {
			// Condizione 2: se il gruppo è astratto, anche il padre deve essere astratto.
			if (creaGruppoPadre != null && creaGruppoPadre.getGpDeTipoGruppo() != null) {
				creaGruppoPadre = null;
			}
		}
	}

	/**
	 * E' obbligatorio inserire un comune solo se si sta creando un gruppo di tipo "comune".
	 */
	public Boolean getCreaComuneRequired() {
		return (creaTipoGruppo != null && creaTipoGruppo.getCodTipoGruppo().equals(GpDeTipoGruppoEnum.COM));
	}

	/**
	 * Metodo dell'autocomplete dei comuni : restituisce solo comuni italiani. Limito il numero di risultati a 10, dato
	 * che l'Autocomplete non ne visualizzerebbe comunque di più.
	 */
	public List<DeComune> autocompleteComune(String par) {
		return deComuneEJB.findComuniItaValideStartingWith(par, new Date(), 10);
	}

	/**
	 * Conferma la creazione di un nuovo gruppo.
	 */
	public void creaNuovoGruppo() {
		try {
			gpGruppoEJB.add(creaTipoGruppo, creaGruppoPadre, creaDescrizione, creaComune,
					accountInfoBean.getIdPfPrincipal());
			addJSSuccessMessage("Gruppo creato con successo");
			creaGruppoPadre = null;
			creaDescrizione = null;
		} catch (MyCasException e) {
			log.error("Errore durante la creazione di un gruppo: " + e);
			addJSWarnMessage("Errore durante la creazione del gruppo: " + e.getMessage());
		}
	}

	/**
	 * Metodo chiamato quando l'utente seleziona un gruppo da visualizzare/modificare. Carico la lista di RuoloGruppo
	 * legate al gruppo e la lista di utenti che hanno almeno una profilatura di quel gruppo.
	 */
	public void modGruppoSelected() {
		modProfilatureList = gpRuoloGruppoEJB.findByFilter(null, null, null, modGruppo, null, null);
		PfPrincipalFilter filtro = new PfPrincipalFilter();
		filtro.setGruppo(modGruppo);
		modUtentiList = new LazyPfPrincipalModel(pfPrincipalEJB, filtro);
	}

	/**
	 * Autocomplete dei ruoli per la creazione di un nuovo RuoloGruppo legato al gruppo attualmente visualizzato.
	 * Escludo tutti i ruoli che hanno già un RuoloGruppo legato a questo gruppo.
	 */
	public List<GpRuolo> addProfilaturaRuoloAutocomplete(String par) {
		return gpRuoloEJB.findRuoliAssegnabiliByGruppo(par, modGruppo, addProfilaturaSistema);
	}

	/**
	 * Conferma le modifiche effettuate ad un gruppo.
	 */
	public void confermaModificheGruppo() {
		try {
			modGruppo = gpGruppoEJB.merge(accountInfoBean.getIdPfPrincipal(), modGruppo);
			addJSSuccessMessage("Gruppo modificato con successo");
		} catch (MyCasException e) {
			log.error("Errore durante la modifica di un gruppo: " + e);
			addJSWarnMessage("Errore durante la merge del gruppo: " + e.getMessage());
		}
	}

	/**
	 * Conferma la creazione di una nuova possibile profilatura (riga su GpRuoloGruppo) per il gruppo attualmente
	 * visualizzato.
	 */
	public void confermaAddProfilatura() {
		if (addProfilaturaRuolo != null) {
			try {
				gpRuoloGruppoEJB.add(addProfilaturaRuolo, modGruppo, false, getAccountInfoBean().getIdPfPrincipal());
				modProfilatureList = gpRuoloGruppoEJB.findByFilter(null, null, null, modGruppo, null, null);
				addProfilaturaRuolo = null;
				addProfilaturaSistema = null;
			} catch (MyCasException e) {
				log.error("Errore durante la creazione di un RuoloGruppo: " + e);
				addJSWarnMessage("Errore durante la creazione della profilatura");
			}
		} else {
			addJSWarnMessage("Devi selezionare un ruolo per creare la profilatura");
		}
	}

	/**
	 * Chiede la cancellazione di una possibile profilatura (riga su GpRuoloGruppo). La cancellazione è possibile solo
	 * se non esistono utenti a cui è stata assegnata questa profilatura.
	 */
	public void cancellaProfilatura(GpRuoloGruppo ruoloGruppo) {
		int profilatureAssegnate = gpProfilaturaEJB.findCountByFilter(null, ruoloGruppo.getGpRuolo(),
				ruoloGruppo.getGpGruppo(), null).intValue();
		if (profilatureAssegnate == 0) {
			try {
				gpRuoloGruppoEJB.remove(ruoloGruppo.getIdGpRuoloGruppo());
				modProfilatureList = gpRuoloGruppoEJB.findByFilter(null, null, null, modGruppo, null, null);
			} catch (MyCasException e) {
				log.error("Errore durante la cancellazione di un RuoloGruppo: " + e);
				addJSWarnMessage("Errore durante la cancellazione");
			}
		} else {
			addJSWarnMessage("Non puoi cancellare una profilatura che è stata assegnata a degli utenti.");
		}
	}

	/**
	 * Cancella uno dei comuni associati al gruppo attualmente visualizzato.
	 */
	public void cancellaComune(DeComune comune) {
		try {
			modGruppo.getComuniAssociatiPerDistretto().remove(comune);
			modGruppo = gpGruppoEJB.merge(getAccountInfoBean().getIdPfPrincipal(), modGruppo);
		} catch (MyCasException e) {
			log.error("Errore durante la cancellazione di un comune associato: " + e);
			addJSWarnMessage("Errore durante la cancellazione");
		}
	}

	/**
	 * Associa il comune attualmente selezionato (nell'autocomplete apposita) al gruppo attualmente visualizzato.
	 */
	public void confermaAddComune() {
		try {
			modGruppo.getComuniAssociatiPerProvincie().add(addComune);
			modGruppo = gpGruppoEJB.merge(getAccountInfoBean().getIdPfPrincipal(), modGruppo);
			addComune = null;
		} catch (MyCasException e) {
			log.error("Errore durante l'inserimento di un comune associato: " + e);
			addJSWarnMessage("Errore durante l'inserimento del comune associato");
		}
	}

	public List<GpDeTipoGruppo> getTipoGruppoList() {
		return tipoGruppoList;
	}

	public void setTipoGruppoList(List<GpDeTipoGruppo> tipoGruppoList) {
		this.tipoGruppoList = tipoGruppoList;
	}

	public GpDeTipoGruppo getCreaTipoGruppo() {
		return creaTipoGruppo;
	}

	public void setCreaTipoGruppo(GpDeTipoGruppo creaTipoGruppo) {
		this.creaTipoGruppo = creaTipoGruppo;
	}

	public String getCreaDescrizione() {
		return creaDescrizione;
	}

	public void setCreaDescrizione(String creaDescrizione) {
		this.creaDescrizione = creaDescrizione;
	}

	public GpGruppo getCreaGruppoPadre() {
		return creaGruppoPadre;
	}

	public void setCreaGruppoPadre(GpGruppo creaGruppoPadre) {
		this.creaGruppoPadre = creaGruppoPadre;
	}

	public GpGruppo getModGruppo() {
		return modGruppo;
	}

	public void setModGruppo(GpGruppo modGruppo) {
		this.modGruppo = modGruppo;
	}

	public List<DeSistema> getAddProfilaturaSistemaList() {
		return addProfilaturaSistemaList;
	}

	public void setAddProfilaturaSistemaList(List<DeSistema> addProfilaturaSistemaList) {
		this.addProfilaturaSistemaList = addProfilaturaSistemaList;
	}

	public DeSistema getAddProfilaturaSistema() {
		return addProfilaturaSistema;
	}

	public void setAddProfilaturaSistema(DeSistema addProfilaturaSistema) {
		this.addProfilaturaSistema = addProfilaturaSistema;
	}

	public GpRuolo getAddProfilaturaRuolo() {
		return addProfilaturaRuolo;
	}

	public void setAddProfilaturaRuolo(GpRuolo addProfilaturaRuolo) {
		this.addProfilaturaRuolo = addProfilaturaRuolo;
	}

	public List<GpRuoloGruppo> getModProfilatureList() {
		return modProfilatureList;
	}

	public void setModProfilatureList(List<GpRuoloGruppo> modProfilatureList) {
		this.modProfilatureList = modProfilatureList;
	}

	public LazyPfPrincipalModel getModUtentiList() {
		return modUtentiList;
	}

	public void setModUtentiList(LazyPfPrincipalModel modUtentiList) {
		this.modUtentiList = modUtentiList;
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

	public DeComune getCreaComune() {
		return creaComune;
	}

	public void setCreaComune(DeComune creaComune) {
		this.creaComune = creaComune;
	}

	public GpDeTipoGruppoEnum getModTipoGruppo() {
		return modTipoGruppo;
	}

	public void setModTipoGruppo(GpDeTipoGruppoEnum modTipoGruppo) {
		this.modTipoGruppo = modTipoGruppo;
	}

	public DeComune getAddComune() {
		return addComune;
	}

	public void setAddComune(DeComune addComune) {
		this.addComune = addComune;
	}

}
