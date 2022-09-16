package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.primefaces.context.RequestContext;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.utils.FormTypeAction;

public class CvProfessionePfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	private CvProfDesiderate professioneAttiva;

	private List<DeBpMansione> mansioniSelezionate;

//	TreeNode istruzioneTree;
//	TreeNode istruzioneTreeSelectedNode;

	private DeTitolo deTitolo;

	@EJB
	private CvProfDesiderateHome cvProfessioneHome;
	private List<CvProfDesiderate> listaProfessioni;

	private CvDatiPersonali cvDatiPersonali;

	private DeMansioneMin deMansioneMin;

	protected FormTypeAction formTypeAction = FormTypeAction.I;

	@Override
	public void initSection() {

		cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();

		/* gestione DeMansioneMin campo not null */
		deMansioneMin = new DeMansioneMin();
		deMansioneMin.setCodMansioneMin("NT");

		listaProfessioni = cvProfessioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali());

		if (listaProfessioni.isEmpty()) { // inizializzo la prima istruzione da
			professioneAttiva = new CvProfDesiderate();
			professioneAttiva.setCvDatiPersonali(cvDatiPersonali);
			professioneAttiva.setDeMansioneMin(deMansioneMin);
			refactorProfessioneCopy(professioneAttiva, true);
		}
		// RequestContext.getCurrentInstance().execute("PF('titoliDiStudioWV').cleanState();");
	}

	public CvProfessionePfSection(CurriculumVitaePfBean cvBean, CvProfDesiderateHome cvProf) {
		super(cvBean);
		cvProfessioneHome = cvProf;
		// initSection();
	}

	@Override
	public void sync() {

		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();

		if (formTypeAction.equals(FormTypeAction.I) || formTypeAction.equals(FormTypeAction.E)) {
			/* eliminato controllo che consente d'inserire una sola professione desiderata 
			if (mansioniSelezionate.contains(professioneAttiva.getDeBpMansione())) {
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				addAlertWarnMessage("Attenzione ",
						"Errore: esiste già una professione associata al CV come la professione selezionata. Utilizzare una professione differente.");
				return;
			} else {
			*/
				if (formTypeAction.equals(FormTypeAction.I)) {
					cvProfessioneHome.persist(professioneAttiva, idPfPrinc);
				} else {
					cvProfessioneHome.merge(professioneAttiva, idPfPrinc);
				}
			//}
		} else if (formTypeAction.equals(FormTypeAction.R)) { // caso cancellazione istruzione
			cvProfessioneHome.remove(professioneAttiva);
		}

		setListaProfessioni(cvProfessioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali()));
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
	}

	public List<CvProfDesiderate> getListaProfessioni() {
		return listaProfessioni;
	}

	public void setListaProfessioni(List<CvProfDesiderate> listaProfessioni) {
		this.listaProfessioni = listaProfessioni;
	}

	public void cancelEditProfessione() {
		addNewProfessione();
		reloadArticlesList();
	}

	// rimuove la professione
	public void removeProfessione(CvProfDesiderate professioneAttiva) {
		this.professioneAttiva = professioneAttiva;
		formTypeAction = FormTypeAction.R;
		sync();

	}

	public void editProfessione(CvProfDesiderate professioneAttiva) {
		this.professioneAttiva = professioneAttiva;
		refactorProfessioneCopy(professioneAttiva, false);
		formTypeAction = FormTypeAction.E;
	}

	private void reloadArticlesList() {
		setListaProfessioni(cvProfessioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali()));
	}

	// aggiunge la professione
	public void addNewProfessione() {
		professioneAttiva = new CvProfDesiderate();
		professioneAttiva.setCvDatiPersonali(cvDatiPersonali);
		professioneAttiva.setDeMansioneMin(deMansioneMin);
		refactorProfessioneCopy(professioneAttiva, true);
		formTypeAction = FormTypeAction.I;
	}

	public void refactorProfessioneCopy(CvProfDesiderate cvProfessione, boolean check) {
		mansioniSelezionate = new ArrayList<DeBpMansione>();
		for (CvProfDesiderate cvProf : listaProfessioni) {
			if (check) { // check = true provengo da inserimento
				mansioniSelezionate.add(cvProf.getDeBpMansione());
			} else {// check = false provengo da modifica
				if (!cvProf.getDeBpMansione().getCodMansione()
						.equals(cvProfessione.getDeBpMansione().getCodMansione())) {
					mansioniSelezionate.add(cvProf.getDeBpMansione());
				}
			}
		}
	}

	public CvProfDesiderate getProfessioneAttiva() {
		return professioneAttiva;
	}

	public void setProfessioneAttiva(CvProfDesiderate professioneAttiva) {
		this.professioneAttiva = professioneAttiva;
	}

	public void gruppoProfessionaleNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		professioneAttiva.setDeBpMansione((DeBpMansione) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('gruppoProfessionaleWV').selectionComplete();");
	}

}
