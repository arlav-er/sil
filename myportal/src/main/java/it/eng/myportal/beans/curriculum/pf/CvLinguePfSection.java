package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.primefaces.context.RequestContext;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.utils.FormTypeAction;

public class CvLinguePfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	DeModalitaLinguaHome deModalitaLinguaHome;

	/*
	 * private DeGradoLin deGradoLinLetto; private DeGradoLin deGradoLinScritto;
	 * private DeGradoLin deGradoLinParlato;
	 */
	protected Boolean madrelingua;
	private CvLingua cvLinguaAttiva;
	private List<DeLingua> lingueSelezionate;
	// private DeLingua deLingua;
	private List<CvLingua> listaLingue;
	private CvDatiPersonali cvDatiPersonali;

	protected FormTypeAction formTypeAction = FormTypeAction.I;

	public CvLinguePfSection(CurriculumVitaePfBean cvBean, CvLinguaHome cvLingua, DeGradoLinSilHome deGradoLinSilHome,
			DeModalitaLinguaHome deModalitaLinguaHome) {
		super(cvBean);
		this.cvLinguaHome = cvLingua;
		this.deGradoLinSilHome = deGradoLinSilHome;
		this.deModalitaLinguaHome = deModalitaLinguaHome;
		listaLingue = new ArrayList<>();
		// initSection();
	}

	@Override
	public void initSection() {

		cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		reloadArticlesList();

		cvLinguaAttiva = new CvLingua();
		cvLinguaAttiva.setCvDatiPersonali(cvDatiPersonali);
		refactorLingueCopy(cvLinguaAttiva, true);
	}

	public List<CvLingua> getListaLingue() {
		return listaLingue;
	}

	public void setListaLingue(List<CvLingua> listaLingue) {
		this.listaLingue = listaLingue;
	}

	public CvLingua getCvLinguaAttiva() {
		return cvLinguaAttiva;
	}

	public void setCvLinguaAttiva(CvLingua cvLinguaAttiva) {
		this.cvLinguaAttiva = cvLinguaAttiva;
	}

	public void editLingue(CvLingua cvLinguaAttiva) {
		this.cvLinguaAttiva = cvLinguaAttiva;
		refactorLingueCopy(cvLinguaAttiva, false);
		formTypeAction = FormTypeAction.E;
	}

	public void addNewLingua() {
		cvLinguaAttiva = new CvLingua();
		cvLinguaAttiva.setCvDatiPersonali(cvDatiPersonali);
		refactorLingueCopy(cvLinguaAttiva, true);
		formTypeAction = FormTypeAction.I;
	}

	public void sortLingue() {
		// String[] sortedArrayString =
		// curriculumVitaePfBean.getRequestParameter("sortedArray").split(",");
		// TODO save order
	}

	public void cancelEditLingue() {
		addNewLingua();
		reloadArticlesList();
	}

	private void reloadArticlesList() {
		listaLingue = cvLinguaHome
				.findLinguaByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali());
	}

	public void removeLingua(CvLingua cvLinguaAttiva) {
		this.cvLinguaAttiva = cvLinguaAttiva;
		formTypeAction = FormTypeAction.R;
		sync();
	}

	public void handleChangeMadreLingua() {
		if (cvLinguaAttiva.getFlagMadrelingua()) {
			cvLinguaAttiva.setDeGradoLinSilLetto(null);
			cvLinguaAttiva.setDeGradoLinSilParlato(null);
			cvLinguaAttiva.setDeGradoLinSilScritto(null);
		}

	}

	public void refactorLingueCopy(CvLingua cvLinguaAttiva, boolean check) {
		lingueSelezionate = new ArrayList<DeLingua>();
		for (CvLingua cvLingua : listaLingue) {
			if (check) { // check = true provengo da inserimento
				lingueSelezionate.add(cvLingua.getDeLingua());
			} else {// check = false provengo da modifica
				if (!cvLingua.getDeLingua().getDenominazione()
						.equals(cvLinguaAttiva.getDeLingua().getDenominazione())) {
					lingueSelezionate.add(cvLingua.getDeLingua());
				}
			}
		}
	}

	@Override
	public void sync() {
		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();
		/*
		 * if (formTypeAction.equals(FormTypeAction.I)) {
		 * cvLinguaHome.persist(cvLinguaAttiva, idPfPrinc); } else if
		 * (formTypeAction.equals(FormTypeAction.E)) { // già presente
		 * cvLinguaHome.merge(cvLinguaAttiva, idPfPrinc); } else {
		 * cvLinguaHome.remove(cvLinguaAttiva); }
		 * 
		 * setListaLingue(cvLinguaHome
		 * .findLinguaByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().
		 * getIdCvDatiPersonali()));
		 */

		/*
		 * CONTROLLO DUPLICATE lingueSelezionate = new ArrayList<DeLingua>(); for
		 * (CvLingua cvLingua: listaLingue){
		 * lingueSelezionate.add(cvLingua.getDeLingua()); }
		 */

		if (formTypeAction.equals(FormTypeAction.I)) {
			if (lingueSelezionate.contains(cvLinguaAttiva.getDeLingua())) {
				log.error("Errore Lingua Duplicata:" + cvLinguaAttiva.getDeLingua().getDenominazione());
				addAlertWarnMessage("Attenzione ",
						"Errore: esiste già una Lingua con questo nome associata al CV. Scegliere una lingua differente.");
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				return;
			} else {
				cvLinguaHome.persist(cvLinguaAttiva, idPfPrinc);
				log.info("Inserimento nuova Lingua:" + cvLinguaAttiva.getDeLingua().getDenominazione());
			}
		} else {
			if (formTypeAction.equals(FormTypeAction.E)) {
				if (lingueSelezionate.contains(cvLinguaAttiva.getDeLingua())) {
					log.error("Errore Lingua Duplicata:" + cvLinguaAttiva.getDeLingua().getDenominazione());
					addAlertWarnMessage("Attenzione ",
							"Errore: esiste già una Lingua con questo nome associata al CV. Scegliere una lingua differente.");
					RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
					return;
				} else {
					cvLinguaHome.merge(cvLinguaAttiva, idPfPrinc);
					log.info("Update Lingua:" + cvLinguaAttiva.getDeLingua().getDenominazione());
				}
			} else {
				cvLinguaHome.remove(cvLinguaAttiva);
			}
		}

		setListaLingue(cvLinguaHome
				.findLinguaByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali()));

		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
	}

}
