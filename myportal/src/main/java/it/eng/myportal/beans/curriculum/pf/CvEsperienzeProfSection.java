package it.eng.myportal.beans.curriculum.pf;

import java.util.Date;
import java.util.List;

import org.primefaces.context.RequestContext;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.utils.FormTypeAction;

public class CvEsperienzeProfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	// the type is unknown yet
	private List<CvEsperienzeProf> esperienzeList;
	private CvEsperienzeProfHome cvEsperienzeProfHome;

	private CvEsperienzeProf esperienzaAttiva;
	protected FormTypeAction formTypeAction = FormTypeAction.I;
	private DeMansioneHome deMansioneHome;
	private DeMansione mansioneTappo;

	public CvEsperienzeProfSection(CurriculumVitaePfBean cvBean, CvEsperienzeProfHome cvEspHome, DeMansioneHome deMansHome) {
		super(cvBean);
		cvEsperienzeProfHome = cvEspHome;
		deMansioneHome = deMansHome;
		// initSection();
	}
	
	public Date getCurrentDate() {
		return new Date();
	}

	@Override
	public void initSection() {
		mansioneTappo = deMansioneHome.findById("NT");
		reloadArticlesList();
		addNewEsperienzaProf();
		
	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();
		if (formTypeAction.equals(FormTypeAction.I)) {
			cvEsperienzeProfHome.persist(esperienzaAttiva, idPfPrinc);
		} else {
			if (formTypeAction.equals(FormTypeAction.E)) {
				esperienzaAttiva = cvEsperienzeProfHome.merge(esperienzaAttiva, idPfPrinc);
			} else { // caso cancellazione istruzione
				cvEsperienzeProfHome.remove(esperienzaAttiva);
			}
		}
		reloadArticlesList();
		
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
	}

	private void reloadArticlesList() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		esperienzeList = cvEsperienzeProfHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
	}

	public void addNewEsperienzaProf() {
		esperienzaAttiva = new CvEsperienzeProf();
		esperienzaAttiva.setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonali());
		esperienzaAttiva.setDeMansione(mansioneTappo);
		formTypeAction = FormTypeAction.I;
	}

	public void editEsperienzaProf(CvEsperienzeProf esperienzaAttiva) {
		this.esperienzaAttiva = esperienzaAttiva;
		formTypeAction = FormTypeAction.E;
	}

	public void cancelEditEsperienzaProf() {
		addNewEsperienzaProf();
		reloadArticlesList();
	}

	public void removeEsperienzaProf(CvEsperienzeProf esperienzaAttiva) {
		this.esperienzaAttiva = esperienzaAttiva;
		formTypeAction = FormTypeAction.R;
		sync();
	}

	public void sortEsperienzeProf() {
		String[] sortedArrayString = curriculumVitaePfBean.getRequestParameter("sortedArray").split(",");

		// TODO save order
	}

    public void cleanDataFine() {
        if(esperienzaAttiva.isFlgInCorso())
            esperienzaAttiva.setA(null);

    }

	public List<CvEsperienzeProf> getEsperienzeList() {
		return esperienzeList;
	}

	public void setEsperienzeList(List<CvEsperienzeProf> esperienzeList) {
		this.esperienzeList = esperienzeList;
	}

	public CvEsperienzeProf getEsperienzaAttiva() {
		return esperienzaAttiva;
	}

	public void setEsperienzaAttiva(CvEsperienzeProf esperienzaAttiva) {
		this.esperienzaAttiva = esperienzaAttiva;
	}

	public void gruppoProfessionaleNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		esperienzaAttiva.setDeBpMansione((DeBpMansione) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('gruppoProfessionaleWV').selectionComplete();");
	}
}
