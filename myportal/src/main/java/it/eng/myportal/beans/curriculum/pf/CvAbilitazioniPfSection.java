package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvPatenteHome;
import it.eng.myportal.entity.home.CvPatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;

public class CvAbilitazioniPfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	DePatenteHome dePatenteHome;

	private List<DePatenteSil> patentiSelezionate;
	private List<DePatentinoSil> patentiniSelezionati;
	private List<DeAlboSil> albiSelezionati;


	private CvPatenteHome cvPatenteHome;
	private CvPatentinoHome cvPatentinoHome;
	private CvAlboHome cvAlboHome;
	//private  CvDatiPersonaliHome cvDatiPersonaliHome;

	// Costruttore
	public CvAbilitazioniPfSection(CurriculumVitaePfBean curriculumVitaePfBean, CvPatenteHome cvPatenteHome,
								   CvPatentinoHome cvPatentinoHome, CvAlboHome cvAlbo) {
		super(curriculumVitaePfBean);

		this.cvPatenteHome = cvPatenteHome;
		this.cvAlboHome = cvAlbo;
		this.cvPatentinoHome = cvPatentinoHome;

		// initSection();
	}

	@Override
	public void initSection() {

		// Load view list from DB
		patentiSelezionate = new ArrayList<>();
		Integer cvId = getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali();
		List<CvPatente> cvPat = cvPatenteHome.findProperByCurriculumId(cvId);
		for (CvPatente cvPatente : cvPat) {
			patentiSelezionate.add(cvPatente.getDePatenteSil());
		}

		patentiniSelezionati = new ArrayList<>();
		List<CvPatentino> cvPati = cvPatentinoHome.findProperByCurriculumId(cvId);
		for (CvPatentino cvPatentino : cvPati) {
			patentiniSelezionati.add(cvPatentino.getDePatentinoSil());
		}

		albiSelezionati = new ArrayList<>();
		List<CvAlbo> cvAlbii = cvAlboHome.findProperByCurriculumId(cvId);
		for (CvAlbo cvPatentino : cvAlbii) {
			albiSelezionati.add(cvPatentino.getDeAlboSil());
		}
	}

	public void syncNote(){
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
	
		getCurriculumVitaePfBean().setCvDatiPersonali(homeEJB.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
	
	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		Integer idPfPrinc = getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente();
		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
		ArrayList<DePatenteSil> patentiCopy = new ArrayList<>();
		if (patentiSelezionate != null)
			patentiCopy.addAll(patentiSelezionate);
		// PATENTI
		Set<CvPatente> refreshedSet = new HashSet<CvPatente>();
		List<CvPatente> cvPatenteDB = cvPatenteHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
		for (CvPatente cvPatente : cvPatenteDB) {
			if (patentiCopy.contains(cvPatente.getDePatenteSil())) {
				refreshedSet.add(cvPatenteHome.merge(cvPatente, idPfPrinc));
				patentiCopy.remove(cvPatente.getDePatenteSil());
			} else {
				// va cancellato
				cvPatenteHome.remove(cvPatente);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DePatenteSil dePatNew : patentiCopy) {
			CvPatente newPat = new CvPatente();
			newPat.setDePatenteSil(dePatNew);
			newPat.setCvDatiPersonali(this.getCurriculumVitaePfBean().getCvDatiPersonali());
			refreshedSet.add(cvPatenteHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getCurriculumVitaePfBean().getCvDatiPersonali().setCvPatentes(refreshedSet);

		// PATENTINI
		ArrayList<DePatentinoSil> patentiniCopy = new ArrayList<>();
		if (patentiniSelezionati != null)
			patentiniCopy.addAll(patentiniSelezionati);
		Set<CvPatentino> refreshedSetPatentini = new HashSet<CvPatentino>();
		List<CvPatentino> cvPatentDB = cvPatentinoHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
		for (CvPatentino cvPatente : cvPatentDB) {
			if (patentiniCopy.contains(cvPatente.getDePatentinoSil())) {
				refreshedSetPatentini.add(cvPatentinoHome.merge(cvPatente, idPfPrinc));
				patentiniCopy.remove(cvPatente.getDePatentinoSil());
			} else {
				// va cancellato
				cvPatentinoHome.remove(cvPatente);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DePatentinoSil dePatNew : patentiniCopy) {
			CvPatentino newPat = new CvPatentino();
			newPat.setDePatentinoSil(dePatNew);
			newPat.setCvDatiPersonali(this.getCurriculumVitaePfBean().getCvDatiPersonali());
			refreshedSetPatentini.add(cvPatentinoHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getCurriculumVitaePfBean().getCvDatiPersonali().setCvPatentinos(refreshedSetPatentini);

		// ALBI
		ArrayList<DeAlboSil> albiCopy = new ArrayList<>();
		if (albiSelezionati != null)
			albiCopy.addAll(albiSelezionati);
		Set<CvAlbo> refreshedSetAlbi = new HashSet<CvAlbo>();
		List<CvAlbo> cvAlbiDB = cvAlboHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
		for (CvAlbo cvPatente : cvAlbiDB) {
			if (albiCopy.contains(cvPatente.getDeAlboSil())) {
				refreshedSetAlbi.add(cvAlboHome.merge(cvPatente, idPfPrinc));
				albiCopy.remove(cvPatente.getDeAlboSil());
			} else {
				// va cancellato
				cvAlboHome.remove(cvPatente);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DeAlboSil deAlboNew : albiCopy) {
			CvAlbo newPat = new CvAlbo();
			newPat.setDeAlboSil(deAlboNew);
			newPat.setCvDatiPersonali(this.getCurriculumVitaePfBean().getCvDatiPersonali());
			refreshedSetAlbi.add(cvAlboHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getCurriculumVitaePfBean().getCvDatiPersonali().setCvAlbos(refreshedSetAlbi);

		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(
				homeEJB.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();

	}

	public void handleChangePatente(SelectEvent event) {
		//DePatenteSil dePatenteAdded = (DePatenteSil) event.getObject();
		//questa sezione salva subito
		sync();
	}

	public void handleChangePatente(UnselectEvent event) {
		//questa sezione salva subito
		sync();
	}

	public void handleChangePatentino(SelectEvent event) {
		//DePatentinoSil dePatenteAdded = (DePatentinoSil) event.getObject();
		//questa sezione salva subito
		sync();
	}

	public void handleChangePatentino(UnselectEvent event) {
		//patentiniSelezionati.remove((DePatentinoSil) event.getObject());
		sync();
	}

	public void handleChangeAlbo(SelectEvent event) {
		//DeAlboSil deAlboAdded = (DeAlboSil) event.getObject();
		//questa sezione salva subito
		sync();
	}

	public void handleChangeAlbo(UnselectEvent event) {
		//albiSelezionati.remove((DeAlboSil) event.getObject());
		sync();
	}

	public List<DePatenteSil> completePatenteSilWithoutSelected(String word) {
		List<DePatenteSil> patenti = this.curriculumVitaePfBean.getAutoCompleteBean().completePatenteSil(word);
		if (patentiSelezionate != null && !patentiSelezionate.isEmpty())
			patenti.removeAll(patentiSelezionate);
		return patenti;
	}

	public List<DePatentinoSil> completePatentinoSilWithoutSelected(String word) {
		List<DePatentinoSil> patentini  = this.curriculumVitaePfBean.getAutoCompleteBean().completePatentinoSil(word);
		if (patentiniSelezionati != null && !patentiniSelezionati.isEmpty())
			patentini.removeAll(patentiniSelezionati);
		return patentini;
	}

	public List<DeAlboSil> completeAlboSilWithoutSelected(String word) {
		List<DeAlboSil> albi  = this.curriculumVitaePfBean.getAutoCompleteBean().completeAlboSil(word);
		if (albiSelezionati != null && !albiSelezionati.isEmpty())
			albi.removeAll(albiSelezionati);
		return albi;
	}

	public List<DePatenteSil> getPatentiSelezionate() {
		return patentiSelezionate;
	}

	public void setPatentiSelezionate(List<DePatenteSil> patenti) {
		this.patentiSelezionate = patenti;
	}

	public List<DePatentinoSil> getPatentiniSelezionati() {
		return patentiniSelezionati;
	}

	public void setPatentiniSelezionati(List<DePatentinoSil> patentini) {
		this.patentiniSelezionati = patentini;
	}

	public List<DeAlboSil> getAlbiSelezionati() {
		return albiSelezionati;
	}

	public void setAlbiSelezionati(List<DeAlboSil> albi) {
		this.albiSelezionati = albi;
	}
}
