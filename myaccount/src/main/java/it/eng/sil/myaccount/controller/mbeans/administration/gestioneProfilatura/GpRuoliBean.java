package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.DeSistemaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Questo bean è legato alla pagina "Gestione Ruoli" del pannello di amministrazione della profilatura. Da qui
 * l'amministratore può creare un nuovo ruolo o gestire un ruolo già esistente.
 * 
 * Quando crea un nuovo ruolo, l'amministratore può scegliere un ruolo già esistente da cui copiare tutte le
 * abilitazioni. Se non lo fa, il nuovo ruolo non avrà nessun componente abilitato.
 * 
 * Nella gestione di un ruolo già esistente è possibile cambiare la descrizione, visualizzare tutti le possibili
 * profilature (ovvero i GpRuoloGruppo) per questo ruolo ed eventualmente eliminarne alcune o inserirne di nuove.
 * 
 * @author gicozza
 */
@ManagedBean(name = "gpRuoliBean")
@ViewScoped
public class GpRuoliBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 487353540821693492L;
	private static final int SELECT_ONE_MENU_MAX_RESULTS = 10;

	@EJB
	DeSistemaMyAccountEJB deSistemaEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	private DeSistema modSelectedSistema;
	private GpRuolo modRuolo;
	private List<GpRuoloGruppo> modRuoloProfilature;

	private GpDeTipoGruppo addProfilaturaTipoGruppo;
	private GpGruppo addProfilaturaGruppo;
	private List<GpDeTipoGruppo> addProfilaturaTipoGruppoList;

	private DeSistema creaSelectedSistema;
	private String creaDescrizione;
	private GpRuolo creaCopiaRuolo;

	private List<DeSistema> sistemi;
	private List<DeSistema> modSistemi;

	@Override
	protected void initPostConstruct() {
		sistemi = deSistemaEJB.findAllValide(new Date());
		modSistemi = deSistemaEJB.findAllValideWithRuoli(new Date());
		EnumSet<GpDeTipoGruppoEnum> all = EnumSet.allOf( GpDeTipoGruppoEnum.class);
		List<GpDeTipoGruppoEnum> listGP = new ArrayList<>( all.size());
		for (GpDeTipoGruppoEnum s : all) {
			listGP.add( s);
		}
		addProfilaturaTipoGruppoList = gpDeTipoGruppoEJB.getGpDeTipoGruppoValideByEnumList(listGP, new Date());
		}

	/**
	 * Metodo chiamato per assicurarsi che il sistema selezionato sia compatibile con il ruolo selezionato. Se si
	 * seleziona un sistema diverso da quello del ruolo attuale, il ruolo attuale viene settato a null.
	 */
	public void onCreaSistemaChange() {
		if (creaSelectedSistema != null && creaCopiaRuolo != null
				&& !creaSelectedSistema.equals(creaCopiaRuolo.getDeSistema())) {
			creaCopiaRuolo = null;
		}
	}

	/**
	 * Metodo legato all'autocomplete della selezione del ruolo da cui copiare le abilitazioni iniziali.
	 */
	public List<GpRuolo> autocompleteCreaCopiaRuolo(String par) {
		return gpRuoloEJB.findByFilter(par, creaSelectedSistema);
	}

	/**
	 * Metodo chiamato quando si seleziona un sistema durante la creazione di un nuovo ruolo. Se il ruolo da cui copiare
	 * le abilitazioni non è compatibile, lo mette a null.
	 */
	public void onModSistemaChange() {
		if (modSelectedSistema != null && modRuolo != null && !modSelectedSistema.equals(modRuolo.getDeSistema())) {
			modRuolo = null;
		}
	}

	/**
	 * Metodo legato all'autocomplete della selezione del ruolo da modificare.
	 */
	public List<GpRuolo> autocompleteModRuolo(String par) {
		return gpRuoloEJB.findByFilter(par, modSelectedSistema);
	}

	/**
	 * Conferma la creazione di un nuovo ruolo, inserendo la riga sul DB e creando le abilitazioni iniziali se
	 * necessario.
	 */
	public void confermaCreaRuolo() {
		try {
			gpRuoloEJB.creaNuovoRuolo(creaSelectedSistema, creaDescrizione, creaCopiaRuolo,
					accountInfoBean.getIdPfPrincipal());
			creaCopiaRuolo = null;
			creaDescrizione = null;
			creaSelectedSistema = null;
			addJSSuccessMessage("Ruolo creato con successo");
		} catch (MyCasException e) {
			log.error("Errore durante la creazione del nuovo ruolo: " + e.getMessage());
			addJSWarnMessage("Errore durante la creazione del nuovo ruolo");
		}
	}

	public void onModRuoloSelected() {
		modRuoloProfilature = gpRuoloGruppoEJB.findByFilter(null, modRuolo, null, null, null, null);
	}

	/**
	 * Conferma le modifiche alla descrizione di un ruolo.
	 */
	public void confermaModRuolo() {
		try {
			modRuolo = gpRuoloEJB.merge(accountInfoBean.getIdPfPrincipal(), modRuolo);
			addJSSuccessMessage("Ruolo modificato con successo");
		} catch (MyCasException e) {
			log.error("Errore durante la modifica di un ruolo: " + e.getMessage());
			addJSWarnMessage("Errore durante la modifica del ruolo");
		}
	}

	public void cancellaProfilatura(GpRuoloGruppo ruoloGruppo) {
		int profilatureAssegnate = gpProfilaturaEJB.findCountByFilter(null, ruoloGruppo.getGpRuolo(),
				ruoloGruppo.getGpGruppo(), null).intValue();
		if (profilatureAssegnate == 0) {
			try {
				gpRuoloGruppoEJB.remove(ruoloGruppo.getIdGpRuoloGruppo());
				modRuoloProfilature = gpRuoloGruppoEJB.findByFilter(null, modRuolo, null, null, null, null);
			} catch (MyCasException e) {
				log.error("Errore durante la cancellazione di un RuoloGruppo: " + e);
				addJSWarnMessage("Errore durante la cancellazione");
			}
		} else {
			addJSWarnMessage("Non puoi cancellare una profilatura che è stata assegnata a degli utenti.");
		}
	}

	/**
	 * Conferma la creazione di una nuova possibile profilatura (riga su GpRuoloGruppo) per il ruolo attualmente
	 * visualizzato.
	 */
	public void aggiungiProfilatura() {
		if (addProfilaturaGruppo != null) {
			try {
				gpRuoloGruppoEJB.add(modRuolo, addProfilaturaGruppo, false, getAccountInfoBean().getIdPfPrincipal());
				modRuoloProfilature = gpRuoloGruppoEJB.findByFilter(null, modRuolo, null, null, null, null);
				addProfilaturaTipoGruppo = null;
				addProfilaturaGruppo = null;
			} catch (MyCasException e) {
				log.error("Errore durante la creazione di un RuoloGruppo: " + e);
				addJSWarnMessage("Errore durante la creazione della profilatura");
			}
		} else {
			addJSWarnMessage("Devi selezionare un gruppo per creare la profilatura");
		}
	}

	public List<GpGruppo> addProfilaturaGruppoAutocomplete(String par) {
		return gpGruppoEJB.findGruppiAssegnabiliByRuolo(par, modRuolo, addProfilaturaTipoGruppo,
				SELECT_ONE_MENU_MAX_RESULTS);
	}

	public DeSistema getModSelectedSistema() {
		return modSelectedSistema;
	}

	public void setModSelectedSistema(DeSistema modSelectedSistema) {
		this.modSelectedSistema = modSelectedSistema;
	}

	public GpRuolo getModRuolo() {
		return modRuolo;
	}

	public void setModRuolo(GpRuolo modRuolo) {
		this.modRuolo = modRuolo;
	}

	public DeSistema getCreaSelectedSistema() {
		return creaSelectedSistema;
	}

	public void setCreaSelectedSistema(DeSistema creaSelectedSistema) {
		this.creaSelectedSistema = creaSelectedSistema;
	}

	public String getCreaDescrizione() {
		return creaDescrizione;
	}

	public void setCreaDescrizione(String creaDescrizione) {
		this.creaDescrizione = creaDescrizione;
	}

	public GpRuolo getCreaCopiaRuolo() {
		return creaCopiaRuolo;
	}

	public void setCreaCopiaRuolo(GpRuolo creaCopiaRuolo) {
		this.creaCopiaRuolo = creaCopiaRuolo;
	}

	public List<DeSistema> getSistemi() {
		return sistemi;
	}

	public void setSistemi(List<DeSistema> sistemi) {
		this.sistemi = sistemi;
	}

	public List<DeSistema> getModSistemi() {
		return modSistemi;
	}

	public void setModSistemi(List<DeSistema> modSistemi) {
		this.modSistemi = modSistemi;
	}

	public List<GpRuoloGruppo> getModRuoloProfilature() {
		return modRuoloProfilature;
	}

	public void setModRuoloProfilature(List<GpRuoloGruppo> modRuoloProfilature) {
		this.modRuoloProfilature = modRuoloProfilature;
	}

	public GpDeTipoGruppo getAddProfilaturaTipoGruppo() {
		return addProfilaturaTipoGruppo;
	}

	public void setAddProfilaturaTipoGruppo(GpDeTipoGruppo addProfilaturaTipoGruppo) {
		this.addProfilaturaTipoGruppo = addProfilaturaTipoGruppo;
	}

	public GpGruppo getAddProfilaturaGruppo() {
		return addProfilaturaGruppo;
	}

	public void setAddProfilaturaGruppo(GpGruppo addProfilaturaGruppo) {
		this.addProfilaturaGruppo = addProfilaturaGruppo;
	}

	public List<GpDeTipoGruppo> getAddProfilaturaTipoGruppoList() {
		return addProfilaturaTipoGruppoList;
	}

	public void setAddProfilaturaTipoGruppoList(List<GpDeTipoGruppo> addProfilaturaTipoGruppoList) {
		this.addProfilaturaTipoGruppoList = addProfilaturaTipoGruppoList;
	}

}
