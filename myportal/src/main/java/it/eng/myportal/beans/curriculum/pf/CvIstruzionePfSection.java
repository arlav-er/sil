package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.utils.FormTypeAction;

public class CvIstruzionePfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	private CvIstruzione istruzioneAttiva;
	private List<DeTitolo> titoliSelezionati;
	private ArrayList<DeTitolo> istruzioniCopy;
	TreeNode istruzioneTree;
	TreeNode istruzioneTreeSelectedNode;
	private DeTitolo deTitolo;

	@EJB
	private CvIstruzioneHome cvIstruzioneHome;
	private List<CvIstruzione> listaTitoliIstruzione;
	private CvDatiPersonali cvDatiPersonali;
	protected FormTypeAction formTypeAction = FormTypeAction.I;

	public CvIstruzionePfSection(CurriculumVitaePfBean cvBean, CvIstruzioneHome cvIstr) {
		super(cvBean);
		cvIstruzioneHome = cvIstr;
		// initSection();
	}

	@Override
	public void initSection() {
		cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();

		deTitolo = new DeTitolo();

		listaTitoliIstruzione = cvIstruzioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali());
		if (listaTitoliIstruzione.isEmpty()) {
			// inizializzo la prima istruzione da inserire
			istruzioneAttiva = new CvIstruzione();
			istruzioneAttiva.setCvDatiPersonali(cvDatiPersonali);
			refactorIstruzioneCopy(this.istruzioneAttiva, true);
		}

		// RequestContext.getCurrentInstance().execute("PF('titoliDiStudioWV').cleanState();");
	}

	// aggiunge l'istruzione
	public void addNewIstruzione() {
		istruzioneAttiva = new CvIstruzione();
		istruzioneAttiva.setCvDatiPersonali(cvDatiPersonali);
		refactorIstruzioneCopy(this.istruzioneAttiva, true);
		formTypeAction = FormTypeAction.I;
	}

	public void cancelEditIstruzione() {
		addNewIstruzione();
		reloadArticlesList();
	}

	private void reloadArticlesList() {
		setListaTitoliIstruzione(cvIstruzioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali()));
	}

	// rimuove l'istruzione
	public void removeIstruzione(CvIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
		formTypeAction = FormTypeAction.R;
		sync();

	}

	public void editIstruzione(CvIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
		refactorIstruzioneCopy(this.istruzioneAttiva, false);
		formTypeAction = FormTypeAction.E;
	}

	public void refactorIstruzioneCopy(CvIstruzione istruzioneAttiva, boolean check) {
		titoliSelezionati = new ArrayList<DeTitolo>();
		for (CvIstruzione cvIstruzione : listaTitoliIstruzione) {
			if (check) { // check = true provengo da inserimento
				titoliSelezionati.add(cvIstruzione.getDeTitolo());
			} else {// check = false provengo da modifica
				if (!cvIstruzione.getDeTitolo().getDescrizione()
						.equals(istruzioneAttiva.getDeTitolo().getDescrizione())) {
					titoliSelezionati.add(cvIstruzione.getDeTitolo());
				}
			}
		}
		istruzioniCopy = new ArrayList<>();
		istruzioniCopy.addAll(titoliSelezionati);
	}

	public void titoliDiStudioNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		istruzioneAttiva.setDeTitolo((DeTitolo) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('titoliDiStudioWV').selectionComplete();");
	}

	public void cleanAnnoConseguimento() {
		if (istruzioneAttiva.getFlgInCorso())
			istruzioneAttiva.setNumAnno(null);
		istruzioneAttiva.setVotazione(null);
	}

	@Override
	public void sync() {

		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();

		if (formTypeAction.equals(FormTypeAction.I) || formTypeAction.equals(FormTypeAction.E)) {
			if (istruzioniCopy.contains(istruzioneAttiva.getDeTitolo())) {
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				addAlertWarnMessage("Attenzione ",
						"Errore: esiste già un un titolo studio con questo nome associato al CV. Utilizzare titolo di studio differente.");
				return;
			} else {
				if (formTypeAction.equals(FormTypeAction.I)) {
					cvIstruzioneHome.persist(istruzioneAttiva, idPfPrinc);
				} else {
					cvIstruzioneHome.merge(istruzioneAttiva, idPfPrinc);
				}
			}
		} else if (formTypeAction.equals(FormTypeAction.R)) { // caso cancellazione istruzione
			cvIstruzioneHome.remove(istruzioneAttiva);
		}

		setListaTitoliIstruzione(cvIstruzioneHome
				.findProperByCurriculumId(this.curriculumVitaePfBean.getCvDatiPersonali().getIdCvDatiPersonali()));

		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();

	}

	public DeTitolo getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	public List<CvIstruzione> getListaTitoliIstruzione() {
		return listaTitoliIstruzione;
	}

	public void setListaTitoliIstruzione(List<CvIstruzione> listaTitoliIstruzione) {
		this.listaTitoliIstruzione = listaTitoliIstruzione;
	}

	public CvIstruzione getIstruzioneAttiva() {
		return istruzioneAttiva;
	}

	public void setIstruzioneAttiva(CvIstruzione istruzioneAttiva) {
		this.istruzioneAttiva = istruzioneAttiva;
	}

}
