package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.DeSistemaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpAbiAttributoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpAbiComponenteMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpAttributoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpComponenteMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpFunzioneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpFunzione;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Questo bean è legato alla pagina "Gestione Componenti" del pannello di amministrazione. Da questa pagina
 * l'amministratore può scegliere un ruolo ed abilitare o disabilitare componenti e attributi per quel ruolo.
 * 
 * @author gicozza
 */
@ManagedBean(name = "gpComponentiBean")
@ViewScoped
public class GpComponentiBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = -1728938354007970787L;

	@EJB
	DeSistemaMyAccountEJB deSistemaEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpComponenteMyAccountEJB gpComponenteEJB;

	@EJB
	GpAttributoMyAccountEJB gpAttributoEJB;

	@EJB
	GpAbiComponenteMyAccountEJB gpAbiComponenteEJB;

	@EJB
	GpAbiAttributoMyAccountEJB gpAbiAttributoEJB;

	@EJB
	GpFunzioneMyAccountEJB gpFunzioneEJB;

	private DeSistema selectedSistema;
	private GpRuolo selectedRuolo;
	private GpFunzione selectedFunzione;

	private List<DeSistema> sistemiList;
	private List<GpFunzione> funzioniList;

	private List<GpComponente> componenti;
	private Set<Integer> componentiAbilitatiSet;
	private Set<Integer> attributiAbilitatiSet;
	private boolean statoErroreFunzioniEsclusive;

	// Questi array contengono informazioni duplicate, ma servono a JSF per poter visualizzare le checkbox.
	private Boolean[] componentiAbilitati;
	private Boolean[][] attributiAbilitati;

	@Override
	protected void initPostConstruct() {
		sistemiList = deSistemaEJB.findAllValideWithRuoli(new Date());
	}

	/**
	 * Quando l'utente seleziona un sistema diverso, controllo che il ruolo attualmente selezionato sia compatibile.
	 */
	public void onSistemaChange() {
		if (selectedRuolo != null && selectedSistema != null && !selectedSistema.equals(selectedRuolo.getDeSistema())) {
			selectedRuolo = null;
		}
	}

	public List<GpRuolo> autocompleteRuolo(String par) {
		return gpRuoloEJB.findByFilter(par, selectedSistema);
	}

	/**
	 * Questo metodo carica tutti i componenti disponibili per il sistema selezionato, poi crea degli HashSet in cui
	 * inserisce gli ID di tutti i componenti abilitati per il ruolo selezionato. In questo modo è possibile sapere in
	 * O(1) se un certo componente è abilitato o no.
	 */
	public void confermaRuoloSelected() {
		if (selectedRuolo != null) {
			selectedSistema = selectedRuolo.getDeSistema();
			selectedFunzione = null;
			try {
				// Carico la lista di tutti i componenti (con relativi attributi) e funzioni disponibili per il sistema.
				componenti = gpComponenteEJB.findByCodSistema(selectedSistema);
				funzioniList = gpFunzioneEJB.findBySistema(selectedSistema);

				// Creo due HashSet contenenti gli ID di componenti e attributi abilitati per il ruolo selezionato.
				List<GpComponente> componentiAbilitatiList = gpComponenteEJB
						.findComponentiAbilitatiByRuolo(selectedRuolo);
				componentiAbilitatiSet = new HashSet<Integer>();
				for (GpComponente componente : componentiAbilitatiList) {
					componentiAbilitatiSet.add(componente.getIdGpComponente());
				}

				List<GpAttributo> attributiAbilitatiList = gpAttributoEJB.findAttributiAbilitatiByRuolo(selectedRuolo);
				attributiAbilitatiSet = new HashSet<Integer>();
				for (GpAttributo attributo : attributiAbilitatiList) {
					attributiAbilitatiSet.add(attributo.getIdGpAttributo());
				}

				// Creo due array di Boolean corrispondenti alla lista di componenti e di attributi,
				// e collegati all'abilitazione di ciascuno di essi.
				componentiAbilitati = new Boolean[componenti.size()];
				attributiAbilitati = new Boolean[componenti.size()][];
				for (int i = 0; i < componenti.size(); i++) {
					GpComponente componente = componenti.get(i);
					componentiAbilitati[i] = componentiAbilitatiSet.contains(componente.getIdGpComponente());
					attributiAbilitati[i] = new Boolean[componente.getAttributi().size()];
					for (int j = 0; j < componente.getAttributi().size(); j++) {
						GpAttributo attributo = componente.getAttributi().get(j);
						attributiAbilitati[i][j] = attributiAbilitatiSet.contains(attributo.getIdGpAttributo());
					}
				}

				// Faccio un controllo per verificare la presenza di eventuali situazioni "sbagliate".
				// Ovvero, controllo se esiste un componente collegato a due funzioni incompatibili tra loro.
				statoErroreFunzioniEsclusive = false;
				for (GpComponente componenteDaControllare : componenti) {
					Set<String> codEsclusivitaSet = new HashSet<String>();
					for (GpFunzione funzioneDaControllare : componenteDaControllare.getFunzioniComponente()) {
						if (codEsclusivitaSet.contains(funzioneDaControllare.getCodEsclusione())) {
							statoErroreFunzioniEsclusive = true;
						} else {
							codEsclusivitaSet.add(funzioneDaControllare.getCodEsclusione());
						}
					}
				}

				// Se ho rilevato una situazione "sbagliata", visualizzo un messaggio di errore.
				if (statoErroreFunzioniEsclusive) {
					addJSDangerMessage("Attenzione: almeno un componente è collegato a più funzioni incompatibili tra loro."
							+ "Correggere questa situazione prima di abilitare componenti.");
				}
			} catch (MyCasException e) {
				log.error("Si è spaccato tutto durante il caricamento componenti/abilitazioni" + e.getMessage());
			}
		}
	}

	/**
	 * Questo metodo controlla se un componente fa parte della funzione attualmente selezionata.
	 */
	public Boolean filtraComponente(GpComponente componente) {
		if (selectedFunzione == null) {
			return true;
		} else {
			return componente.getFunzioniComponente().contains(selectedFunzione);
		}
	}

	/**
	 * Questo metodo controlla se la checkbox relativa ad un componente è disabilitata.
	 * 
	 * Una checkbox viene disabilitata se il componente relativo non è abilitato E abilitarlo porterebbe ad una
	 * situazione di errore (ovvero è abilitato un componente incompatibile).
	 */
	public Boolean isComponenteCheckboxDisabled(GpComponente componente, Integer componenteIndex) {
		// Se sono in stato di errore per via di funzioni esclusive nello stesso componente,
		// TUTTE le checkbox sono disabilitate.
		if (statoErroreFunzioniEsclusive) {
			return true;
		}

		// Se il componente è abilitato, è sempre possibile disabilitarlo.
		if (componentiAbilitati[componenteIndex]) {
			return false;
		}

		// Altrimenti, per ogni funzione di questo componente che abbia un codEsclusione devo controllare
		// TUTTE le funzioni di TUTTI i componenti già abilitati in cerca di conflitti.
		for (GpFunzione funzioneDaAttivare : componente.getFunzioniComponente()) {
			if (funzioneDaAttivare.getCodEsclusione() != null) {
				for (GpComponente componenteCheck : componenti) {
					if (componentiAbilitatiSet.contains(componenteCheck.getIdGpComponente())) {
						for (GpFunzione funzioneCheck : componenteCheck.getFunzioniComponente()) {
							// Il conflitto esiste se trovo una funzione DIVERSA da quella che sto attivando,
							// ma con lo stesso codEsclusione.
							if (!funzioneCheck.equals(funzioneDaAttivare) && funzioneCheck.getCodEsclusione() != null
									&& funzioneCheck.getCodEsclusione().equals(funzioneDaAttivare.getCodEsclusione())) {
								return true;
							}
						}
					}
				}
			}
		}

		// Se non ho rilevato nessun conflitto, non disabilito la checkbox.
		return false;
	}

	/**
	 * Metodo chiamato quando l'utente clicca sulla checkbox corrispondente ad un componente. Se il componente è
	 * abilitato per il ruolo corrente, lo disabilito. Se non è abilitato, lo abilito.
	 */
	public void toggleComponenteAbilitato(GpComponente componente) {
		try {
			gpAbiComponenteEJB.toggle(selectedRuolo, componente, accountInfoBean.getIdPfPrincipal());

			// Aggiorno anche l'HashSet delle abilitazioni di componenti.
			if (componentiAbilitatiSet.contains(componente.getIdGpComponente())) {
				componentiAbilitatiSet.remove(componente.getIdGpComponente());
			} else {
				componentiAbilitatiSet.add(componente.getIdGpComponente());
			}
		} catch (MyCasException e) {
			log.error("Errore durante la toggle del componente " + componente.getDescrizione() + " : " + e.getMessage());
			addJSWarnMessage("Errore durante la modifica dei dati.");
		}
	}

	/**
	 * Metodo chiamato quando l'utente clicca sulla checkbox corrispondente ad un attributo. Se l'attributo è abilitato
	 * per il ruolo corrente, lo disabilito. Se non è abilitato, lo abilito.
	 */
	public void toggleAttributoAbilitato(GpAttributo attributo) {
		try {
			gpAbiAttributoEJB.toggle(selectedRuolo, attributo, accountInfoBean.getIdPfPrincipal());

			// Aggiorno anche l'HashSet delle abilitazioni di attributi.
			if (attributiAbilitatiSet.contains(attributo.getIdGpAttributo())) {
				attributiAbilitatiSet.remove(attributo.getIdGpAttributo());
			} else {
				attributiAbilitatiSet.add(attributo.getIdGpAttributo());
			}
		} catch (MyCasException e) {
			log.error("Errore durante la toggle dell'attributo " + attributo.getDescrizione() + " : " + e.getMessage());
			addJSWarnMessage("Errore durante la modifica dei dati.");
		}
	}

	public DeSistema getSelectedSistema() {
		return selectedSistema;
	}

	public void setSelectedSistema(DeSistema selectedSistema) {
		this.selectedSistema = selectedSistema;
	}

	public List<DeSistema> getSistemiList() {
		return sistemiList;
	}

	public void setSistemiList(List<DeSistema> sistemiList) {
		this.sistemiList = sistemiList;
	}

	public GpRuolo getSelectedRuolo() {
		return selectedRuolo;
	}

	public void setSelectedRuolo(GpRuolo selectedRuolo) {
		this.selectedRuolo = selectedRuolo;
	}

	public List<GpComponente> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<GpComponente> componenti) {
		this.componenti = componenti;
	}

	public GpFunzione getSelectedFunzione() {
		return selectedFunzione;
	}

	public void setSelectedFunzione(GpFunzione selectedFunzione) {
		this.selectedFunzione = selectedFunzione;
	}

	public List<GpFunzione> getFunzioniList() {
		return funzioniList;
	}

	public void setFunzioniList(List<GpFunzione> funzioniList) {
		this.funzioniList = funzioniList;
	}

	public Boolean[] getComponentiAbilitati() {
		return componentiAbilitati;
	}

	public void setComponentiAbilitati(Boolean[] componentiAbilitati) {
		this.componentiAbilitati = componentiAbilitati;
	}

	public Boolean[][] getAttributiAbilitati() {
		return attributiAbilitati;
	}

	public void setAttributiAbilitati(Boolean[][] attributiAbilitati) {
		this.attributiAbilitati = attributiAbilitati;
	}

}
