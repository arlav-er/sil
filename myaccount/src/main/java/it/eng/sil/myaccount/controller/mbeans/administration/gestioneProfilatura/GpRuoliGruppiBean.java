package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.DeSistemaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;

/**
 * Questo bean è legato alla pagina "Gestione Profilature" del pannello di amministrazione. Da questa pagina
 * l'amministratore può gestire le possibili combinazioni ruolo-gruppo da assegnare poi agli utenti.
 * 
 * Per prima cosa bisogna selezionare un ruolo ed un gruppo; il sistema dirà se esiste la possibile profilatura
 * corrispondente a questa coppia. Se non esiste, sarà possibile crearla. Se esiste, sarà possibile cancellarla SOLO se
 * non è una profilatura di default e non è ancora stata assegnata a nessun utente.
 * 
 * In questa pagina non vengono mai considerati i gruppi astratti, che per definizione non possono essere assegnati ad
 * utenti.
 * 
 * @author gicozza
 */
@ManagedBean(name = "gpRuoliGruppiBean")
@ViewScoped
public class GpRuoliGruppiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 7461986060783805058L;
	private static final int SELECT_ONE_MENU_MAX_RESULTS = 10;

	@EJB
	DeSistemaMyAccountEJB deSistemaEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	private DeSistema selectedSistema;
	private GpDeTipoGruppo selectedTipoGruppo;
	private GpGruppo selectedGruppo;
	private GpRuolo selectedRuolo;
	private GpRuoloGruppo selectedRuoloGruppo;

	private List<DeSistema> sistemiSelectItems;
	private List<GpDeTipoGruppo> tipiGruppoSelectItems;

	private GpDeTipoGruppoEnum modTipoPadre;
	private GpGruppo selectedPadre;

	@Override
	protected void initPostConstruct() {
		sistemiSelectItems = deSistemaEJB.findAllValideWithRuoli(new Date());
		// TODO replace with GpDeTipoGruppoEnum.getSupportedList()
		EnumSet<GpDeTipoGruppoEnum> all = EnumSet.allOf(GpDeTipoGruppoEnum.class);
		List<GpDeTipoGruppoEnum> listGP = new ArrayList<>(all.size());
		for (GpDeTipoGruppoEnum s : all) {
			listGP.add(s);
		}
		tipiGruppoSelectItems = gpDeTipoGruppoEJB.getGpDeTipoGruppoValideByEnumList(listGP, new Date());
	}

	/**
	 * Metodo legato all'elemento Autocomplete che seleziona il ruolo.
	 */
	public List<GpRuolo> autocompleteRuolo(String par) {
		return gpRuoloEJB.findByFilter(par, selectedSistema);
	}

	/**
	 * Metodo legato all'elemento Autocomplete che seleziona il gruppo. Escludo i gruppi astratti, dato che non possono
	 * essere assegnati ad utenti.
	 */
	public List<GpGruppo> autocompleteGruppo(String par) {
		GpGruppoFilter filtro = new GpGruppoFilter().setDescrizione(par).setIncludiGruppiAstratti(false);
		if (selectedTipoGruppo != null)
			filtro.setCodTipoGruppo(selectedTipoGruppo.getCodTipoGruppo());
		if (selectedPadre != null) {
			filtro.setIdPadre(selectedPadre.getIdGpGruppo());
		}
		return gpGruppoEJB.findByFilter(filtro, SELECT_ONE_MENU_MAX_RESULTS);
	}

	/**
	 * Metodo legato al componente Autocomplete che seleziona il gruppo da modificare. Se non specifico un tipo, allora
	 * includo anche i gruppi astratti.
	 */
	public List<GpGruppo> autocompleteModPadre(String par) {
		GpGruppoFilter filtro = new GpGruppoFilter().setDescrizione(par.trim()).setIncludiGruppiAstratti(true);

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
		if (modTipoPadre != null && selectedPadre != null) {
			selectedPadre = null;
		}

	}

	/**
	 * Questo metodo si assicura che il sistema selezionato sia compatibile con il ruolo selezionato.
	 */
	public void onSistemaChange() {
		if (selectedSistema != null && selectedRuolo != null && selectedRuolo.getDeSistema() != null
				&& !selectedSistema.equals(selectedRuolo.getDeSistema())) {
			selectedRuolo = null;
			refreshRuoloGruppo();
		}
	}

	/**
	 * Questo metodo si assicura che il tipoGruppo selezionato sia compatibile con il gruppo selezionato.
	 */
	public void onTipoGruppoChange() {
		if (selectedTipoGruppo != null && selectedGruppo != null && selectedGruppo.getGpDeTipoGruppo() != null
				&& !selectedTipoGruppo.equals(selectedGruppo.getGpDeTipoGruppo())) {
			selectedGruppo = null;
			refreshRuoloGruppo();
		}
	}

	/**
	 * Quando viene selezionato un ruolo o un gruppo, controllo se esiste una possibile profilatura corrispondente alla
	 * coppia ruolo/gruppo attuale.
	 */
	public void refreshRuoloGruppo() {
		if (selectedGruppo != null && selectedRuolo != null) {
			try {
				selectedRuoloGruppo = gpRuoloGruppoEJB.findByGruppoAndRuolo(selectedGruppo.getIdGpGruppo(),
						selectedRuolo.getIdGpRuolo());
			} catch (MyCasNoResultException e) {
				// NON E' UN ERRORE, VUOL DIRE CHE NON ESISTE ANCORA QUESTA POSSIBILE PROFILATURA.
				selectedRuoloGruppo = null;
			} catch (MyCasTooManyResultsException e) {
				// QUESTO INVECE E' UN ERRORE
				log.error("ATTENZIONE : Errore nei dati! Esistono due RuoliGruppo con stesso ruolo e gruppo! " + e);
				addJSDangerMessage("ATTENZIONE: Esiste più di un RuoloGruppo con questi dati!");
				selectedRuoloGruppo = null;
			}
		} else {
			selectedRuoloGruppo = null;
		}
	}

	/**
	 * Crea una nuova possibile profilatura, ovvero una riga nella tabella RuoloGruppo.
	 */
	public void creaProfilatura() {
		try {
			selectedRuoloGruppo = gpRuoloGruppoEJB.add(selectedRuolo, selectedGruppo, false,
					accountInfoBean.getIdPfPrincipal());
			addJSSuccessMessage("Profilatura creata con successo");
		} catch (MyCasException e) {
			log.error("Errore durante la creazione profilatura: " + e.getMessage());
			addJSWarnMessage("Errore durante la creazione profilatura: " + e.getMessage());
		}
	}

	/**
	 * Rimuove una possibile profilatura (ovvero un RuoloGruppo), purchè non sia una profilatura di default. Non si può
	 * rimuovere una profilatura se è stata assegnata a qualche utente.
	 */
	public void rimuoviProfilatura() {
		if (!selectedRuoloGruppo.getFlgDefault()) {
			int profilatureAssegnate = gpProfilaturaEJB
					.findCountByFilter(null, selectedRuoloGruppo.getGpRuolo(), selectedRuoloGruppo.getGpGruppo(), null)
					.intValue();
			if (profilatureAssegnate == 0) {
				try {
					gpRuoloGruppoEJB.remove(selectedRuoloGruppo);
					selectedRuoloGruppo = null;
					addJSSuccessMessage("Profilatura rimossa con successo");
				} catch (MyCasException e) {
					log.error("Errore durante la rimozione di un RuoloGruppo: " + e.getMessage());
					addJSWarnMessage("Errore durante la rimozione della profilatura");
				}
			} else {
				addJSWarnMessage("Non si può rimuovere una profilatura che è stata assegnata a degli utenti.");
			}
		} else {
			addJSWarnMessage("Non si può rimuovere una profilatura di default.");
		}
	}

	public GpGruppo getSelectedGruppo() {
		return selectedGruppo;
	}

	public void setSelectedGruppo(GpGruppo selectedGruppo) {
		this.selectedGruppo = selectedGruppo;
	}

	public GpRuolo getSelectedRuolo() {
		return selectedRuolo;
	}

	public void setSelectedRuolo(GpRuolo selectedRuolo) {
		this.selectedRuolo = selectedRuolo;
	}

	public GpRuoloGruppo getSelectedRuoloGruppo() {
		return selectedRuoloGruppo;
	}

	public void setSelectedRuoloGruppo(GpRuoloGruppo selectedRuoloGruppo) {
		this.selectedRuoloGruppo = selectedRuoloGruppo;
	}

	public DeSistema getSelectedSistema() {
		return selectedSistema;
	}

	public void setSelectedSistema(DeSistema selectedSistema) {
		this.selectedSistema = selectedSistema;
	}

	public GpDeTipoGruppo getSelectedTipoGruppo() {
		return selectedTipoGruppo;
	}

	public void setSelectedTipoGruppo(GpDeTipoGruppo selectedTipoGruppo) {
		this.selectedTipoGruppo = selectedTipoGruppo;
	}

	public List<DeSistema> getSistemiSelectItems() {
		return sistemiSelectItems;
	}

	public void setSistemiSelectItems(List<DeSistema> sistemiSelectItems) {
		this.sistemiSelectItems = sistemiSelectItems;
	}

	public List<GpDeTipoGruppo> getTipiGruppoSelectItems() {
		return tipiGruppoSelectItems;
	}

	public void setTipiGruppoSelectItems(List<GpDeTipoGruppo> tipiGruppoSelectItems) {
		this.tipiGruppoSelectItems = tipiGruppoSelectItems;
	}

	public GpDeTipoGruppoEnum getModTipoPadre() {
		return modTipoPadre;
	}

	public void setModTipoPadre(GpDeTipoGruppoEnum modTipoPadre) {
		this.modTipoPadre = modTipoPadre;
	}

	public GpGruppo getSelectedPadre() {
		return selectedPadre;
	}

	public void setSelectedPadre(GpGruppo selectedPadre) {
		this.selectedPadre = selectedPadre;
	}
}
