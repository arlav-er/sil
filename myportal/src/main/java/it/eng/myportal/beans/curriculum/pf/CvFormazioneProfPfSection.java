package it.eng.myportal.beans.curriculum.pf;

import java.util.List;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaFormazioneHome;
import it.eng.myportal.utils.FormTypeAction;

public class CvFormazioneProfPfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	private List<CvFormazione> cvFormazioneList;
	private CvFormazioneHome cvFormazioneHome;

	private DeAreaFormazioneHome deAreaFormazioneHome;

	private CvFormazione cvFormazione;
	protected FormTypeAction formTypeAction = FormTypeAction.I;

	// Costruttore
	public CvFormazioneProfPfSection(CurriculumVitaePfBean curriculumVitaePfBean, CvFormazioneHome cvHome) {
		super(curriculumVitaePfBean);
		this.cvFormazioneHome = cvHome;
		// initSection();
	}

	@Override
	public void initSection() {
		reloadArticlesList();
		addNewFormazione();
	}

	public void reloadArticlesList() {
		cvFormazioneList = cvFormazioneHome
				.findProperByCurriculumId(getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
	}

	public void cleanValueAnno() {
		if(cvFormazione.getFlgInCorso())
		cvFormazione.setNumAnno(null);

	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();
		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
		//boolean alreadPres = false;
		if (formTypeAction.equals(FormTypeAction.I)) {
			cvFormazioneHome.persist(cvFormazione, idPfPrinc);
			/*
			 * for (CvFormazione cvFormaCheck : cvFormazioneList) { if
			 * (cvFormaCheck.getDeAreaFormazione().equals(cvFormazione.getDeAreaFormazione())) { alreadPres = true;
			 * break; } // ce gia` } if (!alreadPres) cvFormazioneHome.persist(cvFormazione, idPfPrinc);
			 */

		} else

		{
			if (formTypeAction.equals(FormTypeAction.E)) {
				cvFormazioneHome.merge(cvFormazione, idPfPrinc);
			} else { // caso cancellazione istruzione
				cvFormazioneHome.remove(cvFormazione);
			}
		}

		// this should be unnecessary I think
		//getCurriculumVitaePfBean().setCvDatiPersonali(
				//homeEJB.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
		initSection();

	}
	
	

	public CvFormazione getCvFormazione() {
		return cvFormazione;
	}

	public void setCvFormazione(CvFormazione cvFormazione) {
		this.cvFormazione = cvFormazione;
	}

	public List<CvFormazione> getCvFormazioneList() {
		return cvFormazioneList;
	}

	public void setCvFormazioneList(List<CvFormazione> cvFormazioneList) {
		this.cvFormazioneList = cvFormazioneList;
	}

	// aggiunge l'istruzione
	public void addNewFormazione() {
		cvFormazione = new CvFormazione();
		cvFormazione.setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonali());
		formTypeAction = FormTypeAction.I;
	}

	public void cancelEditFormazione() {
		addNewFormazione();
		reloadArticlesList();
	}

	public void editFormazione(CvFormazione ed) {
		this.cvFormazione = ed;
		formTypeAction = FormTypeAction.E;
	}

	public void removeFormazione(CvFormazione rem) {
		cvFormazione = rem;
		formTypeAction = FormTypeAction.R;
		sync();
	}

}
