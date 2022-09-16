package it.eng.myportal.beans.vacancies.pf;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.context.RequestContext;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.utils.FormTypeAction;

public class VaIstruzioniSection extends VacancyBaseFormSection implements IVacancySection {

	private VaIstruzione istruzioneAttiva;
	private ArrayList<DeTitolo> istruzioniCopy;
	private List<DeTitolo> titoliSelezionati;
	private DeTitolo deTitolo;
	private VaDatiVacancy vaDatiVacancy;
	private List<VaIstruzione> listaTitoliIstruzione;
	protected FormTypeAction formTypeAction = FormTypeAction.I;
	private Integer vacancyId;

	public VaIstruzioniSection(VacancyFormPfBean cvBean) {
		super(cvBean);
	}

	@Override
	public void initSection() {
		vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();

		deTitolo = new DeTitolo();
		listaTitoliIstruzione = getVacancyFormPfBean().vaIstruzioneHome.findProperByVacancyId(vacancyId);

		if (listaTitoliIstruzione.isEmpty()) {
			// inizializzo la prima istruzione da inserire
			istruzioneAttiva = new VaIstruzione();
			istruzioneAttiva.setVaDatiVacancy(vaDatiVacancy);
			refactorIstruzioneCopy(this.istruzioneAttiva, true);
		}

	}

	// aggiunge l'istruzione
	public void addNewIstruzione() {
		istruzioneAttiva = new VaIstruzione();
		istruzioneAttiva.setVaDatiVacancy(vaDatiVacancy);
		refactorIstruzioneCopy(this.istruzioneAttiva, true);
		formTypeAction = FormTypeAction.I;
	}

	public void cancelEditIstruzione() {
		addNewIstruzione();
		reloadArticlesList();
	}

	private void reloadArticlesList() {
		setListaTitoliIstruzione(getVacancyFormPfBean().vaIstruzioneHome.findProperByVacancyId(vacancyId));
	}

	// rimuove l'istruzione
	public void removeIstruzione(VaIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
		formTypeAction = FormTypeAction.R;
		sync();
	}

	public void editIstruzione(VaIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
		refactorIstruzioneCopy(this.istruzioneAttiva, false);
		formTypeAction = FormTypeAction.E;
	}

	public void refactorIstruzioneCopy(VaIstruzione istruzioneAttiva, boolean check) {
		titoliSelezionati = new ArrayList<DeTitolo>();
		for (VaIstruzione vaIstruzione : listaTitoliIstruzione) {
			if (check) { // check = true provengo da inserimento
				titoliSelezionati.add(vaIstruzione.getDeTitolo());
			} else {// check = false provengo da modifica
				if (!vaIstruzione.getDeTitolo().getDescrizione()
						.equals(istruzioneAttiva.getDeTitolo().getDescrizione())) {
					titoliSelezionati.add(vaIstruzione.getDeTitolo());
				}
			}
		}
		istruzioniCopy = new ArrayList<>();
		istruzioniCopy.addAll(titoliSelezionati);
	}

	@Override
	public void sync() {
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		if (formTypeAction.equals(FormTypeAction.I) || formTypeAction.equals(FormTypeAction.E)) {
			/*
			if (istruzioniCopy.contains(istruzioneAttiva.getDeTitolo())) {
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				addAlertWarnMessage("Attenzione ",
						"Errore: esiste già un un titolo studio con questo nome associato alla Vacancy. Utilizzare titolo di studio differente.");
				return;
			} else {
				*/
				if (formTypeAction.equals(FormTypeAction.I)) {
					getVacancyFormPfBean().vaIstruzioneHome.persist(istruzioneAttiva, idPfPrinc);
				} else {
					getVacancyFormPfBean().vaIstruzioneHome.merge(istruzioneAttiva, idPfPrinc);
				}
			//}
		} else if (formTypeAction.equals(FormTypeAction.R)) { // caso cancellazione istruzione
			getVacancyFormPfBean().vaIstruzioneHome.remove(istruzioneAttiva);
		}

		setListaTitoliIstruzione(getVacancyFormPfBean().vaIstruzioneHome.findProperByVacancyId(vacancyId));
		// aggiornare VaDatiVacancy
		getVacancyFormPfBean().setVaDatiVacancy(homeEJB.merge(vaDatiVacancy, idPfPrinc));
	}

	public void titoliDiStudioNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		istruzioneAttiva.setDeTitolo((DeTitolo) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('titoliDiStudioWV').selectionComplete();");
	}

	public VaIstruzione getIstruzioneAttiva() {
		return istruzioneAttiva;
	}

	public void setIstruzioneAttiva(VaIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
	}

	public DeTitolo getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	public List<VaIstruzione> getListaTitoliIstruzione() {
		return listaTitoliIstruzione;
	}

	public void setListaTitoliIstruzione(List<VaIstruzione> listaTitoliIstruzione) {
		this.listaTitoliIstruzione = listaTitoliIstruzione;
	}

}
