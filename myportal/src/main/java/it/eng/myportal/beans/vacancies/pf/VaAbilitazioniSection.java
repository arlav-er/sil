package it.eng.myportal.beans.vacancies.pf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.home.VaAlboHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaPatenteHome;
import it.eng.myportal.entity.home.VaPatentinoHome;

public class VaAbilitazioniSection extends VacancyBaseFormSection implements IVacancySection {

	private VaPatenteHome vaPatenteHome;
	private VaPatentinoHome vaPatentinoHome;
	private VaAlboHome vaAlboHome;

	private List<DePatenteSil> patentiSelezionate;
	private List<DePatentinoSil> patentiniSelezionati;
	private List<DeAlboSil> albiSelezionati;
	private Integer vacancyId;
	private String concatPatentiReadOnly = "";
	private String concatPatentiniReadOnly = "";
	private String concatAlbiReadOnly = "";
	// Costruttore
	public VaAbilitazioniSection(VacancyFormPfBean vaBean, VaPatenteHome vaPatenteHome, VaPatentinoHome vaPatentinoHome,
			VaAlboHome vaAlbo) {
		super(vaBean);
		this.vaPatenteHome = vaPatenteHome;
		this.vaAlboHome = vaAlbo;
		this.vaPatentinoHome = vaPatentinoHome;
	}

	@Override
	public void initSection() {

		patentiSelezionate = new ArrayList<>();
		vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();

		List<VaPatente> vaPat = vaPatenteHome.findProperByVacancyId(vacancyId);
		for (VaPatente vaPatente : vaPat) {
			patentiSelezionate.add(vaPatente.getDePatenteSil());
		}
		if(vaPat != null && !vaPat.isEmpty()) {
			concatPatentiReadOnly = popolaConcatFieldReadOnly(vaPat);
		}
		
		patentiniSelezionati = new ArrayList<>();
		List<VaPatentino> vaPati = vaPatentinoHome.findProperByVacancyId(vacancyId);
		for (VaPatentino vaPatentino : vaPati) {
			patentiniSelezionati.add(vaPatentino.getDePatentinoSil());
		}
		if(vaPati != null && !vaPati.isEmpty()) {
			concatPatentiniReadOnly = popolaConcatFieldReadOnly(vaPati);
		}

		albiSelezionati = new ArrayList<>();
		List<VaAlbo> vaAlbii = vaAlboHome.findProperByVacancyId(vacancyId);
		for (VaAlbo vaAlbo : vaAlbii) {
			albiSelezionati.add(vaAlbo.getDeAlboSil());
		}
		if(vaAlbii != null && !vaAlbii.isEmpty()) {
			concatAlbiReadOnly = popolaConcatFieldReadOnly(vaAlbii);
		}

	}

	@Override
	public void sync() {

		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		VaDatiVacancy vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();

		ArrayList<DePatenteSil> patentiCopy = new ArrayList<>();
		if (patentiSelezionate != null)
			patentiCopy.addAll(patentiSelezionate);
		// PATENTI
		Set<VaPatente> refreshedSet = new HashSet<VaPatente>();
		List<VaPatente> vaPatenteDB = vaPatenteHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		for (VaPatente vaPatente : vaPatenteDB) {
			if (patentiCopy.contains(vaPatente.getDePatenteSil())) {
				refreshedSet.add(vaPatenteHome.merge(vaPatente, idPfPrinc));
				patentiCopy.remove(vaPatente.getDePatenteSil());
			} else {
				// va cancellato
				vaPatenteHome.remove(vaPatente);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DePatenteSil dePatNew : patentiCopy) {
			VaPatente newPat = new VaPatente();
			newPat.setDePatenteSil(dePatNew);
			newPat.setVaDatiVacancy(this.getVacancyFormPfBean().getVaDatiVacancy());
			refreshedSet.add(vaPatenteHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getVacancyFormPfBean().getVaDatiVacancy().setVaPatentes(refreshedSet);

		// PATENTINI
		ArrayList<DePatentinoSil> patentiniCopy = new ArrayList<>();
		if (patentiniSelezionati != null)
			patentiniCopy.addAll(patentiniSelezionati);
		Set<VaPatentino> refreshedSetPatentini = new HashSet<VaPatentino>();
		List<VaPatentino> vaPatentinoDB = vaPatentinoHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		for (VaPatentino vaPatentino : vaPatentinoDB) {
			if (patentiniCopy.contains(vaPatentino.getDePatentinoSil())) {
				refreshedSetPatentini.add(vaPatentinoHome.merge(vaPatentino, idPfPrinc));
				patentiniCopy.remove(vaPatentino.getDePatentinoSil());
			} else {
				// va cancellato
				vaPatentinoHome.remove(vaPatentino);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DePatentinoSil dePatNew : patentiniCopy) {
			VaPatentino newPat = new VaPatentino();
			newPat.setDePatentinoSil(dePatNew);
			newPat.setVaDatiVacancy(this.getVacancyFormPfBean().getVaDatiVacancy());
			refreshedSetPatentini.add(vaPatentinoHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getVacancyFormPfBean().getVaDatiVacancy().setVaPatentinos(refreshedSetPatentini);

		// ALBI
		ArrayList<DeAlboSil> albiCopy = new ArrayList<>();
		if (albiSelezionati != null)
			albiCopy.addAll(albiSelezionati);

		List<VaAlbo> refreshedSetAlbi = new ArrayList<VaAlbo>();

		List<VaAlbo> vaAlbiDB = vaAlboHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		for (VaAlbo vaAlbo : vaAlbiDB) {
			if (albiCopy.contains(vaAlbo.getDeAlboSil())) {
				refreshedSetAlbi.add(vaAlboHome.merge(vaAlbo, idPfPrinc));
				albiCopy.remove(vaAlbo.getDeAlboSil());
			} else {
				// va cancellato
				vaAlboHome.remove(vaAlbo);
			}
		}
		// giro inverso, rimangono quelle da creare
		for (DeAlboSil deAlboNew : albiCopy) {
			VaAlbo newPat = new VaAlbo();
			newPat.setDeAlboSil(deAlboNew);
			newPat.setVaDatiVacancy(this.getVacancyFormPfBean().getVaDatiVacancy());
			refreshedSetAlbi.add(vaAlboHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		this.getVacancyFormPfBean().getVaDatiVacancy().setVaAlbos(refreshedSetAlbi);
        // Aggiorno VaDatiVacancy
		homeEJB.merge(this.getVacancyFormPfBean().getVaDatiVacancy(), idPfPrinc);

	}

	/*
	 * Concatena le descrizioni delle Patenti, Patentini, Albi associati ad una
	 * vacancy - utilizzato se la vacancy è pubblicata ed ha una candidatura per cui
	 * nessun campo associato alla vacancy deve essere modificabile
	 */
	private String popolaConcatFieldReadOnly(List<?> listSelezionati) {
		String daConcat = "";
		Object selezionato = null;
		for (int i = 0; i < listSelezionati.size(); i++) {
			selezionato = listSelezionati.get(i);
			if (selezionato instanceof VaPatente) {
				daConcat = daConcat.concat(((VaPatente) selezionato).getDePatenteSil().getDescrizione());
			}
			if (selezionato instanceof VaPatentino) {
				daConcat = daConcat.concat(((VaPatentino) selezionato).getDePatentinoSil().getDescrizione());
			}
			if (selezionato instanceof VaAlbo) {
				daConcat = daConcat.concat(((VaAlbo) selezionato).getDeAlboSil().getDescrizione());
			}
			if (i < listSelezionati.size() - 1) {
				daConcat = daConcat.concat("  -  ");
			}
		}
		return daConcat;
	}
	
	public void syncNote() {

		VaDatiVacancy vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();

		getVacancyFormPfBean().setVaDatiVacancy(
					homeEJB.merge(vaDatiVacancy, getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal()));
	}

	public void handleChangePatente(SelectEvent event) {
		sync();
	}

	public void handleChangePatente(UnselectEvent event) {
		sync();
	}

	public void handleChangePatentino(SelectEvent event) {
		sync();
	}

	public void handleChangePatentino(UnselectEvent event) {
		sync();
	}

	public void handleChangeAlbo(SelectEvent event) {
		sync();
	}

	public void handleChangeAlbo(UnselectEvent event) {
		sync();
	}

	public List<DePatenteSil> completePatenteSilWithoutSelected(String word) {
		List<DePatenteSil> patenti = this.vacancyFormPfBean.getAutoCompleteBean().completePatenteSil(word);
		if (patentiSelezionate != null && !patentiSelezionate.isEmpty())
			patenti.removeAll(patentiSelezionate);
		return patenti;
	}

	public List<DePatentinoSil> completePatentinoSilWithoutSelected(String word) {
		List<DePatentinoSil> patentini  = this.vacancyFormPfBean.getAutoCompleteBean().completePatentinoSil(word);
		if (patentiniSelezionati != null && !patentiniSelezionati.isEmpty())
			patentini.removeAll(patentiniSelezionati);
		return patentini;
	}

	public List<DeAlboSil> completeAlboSilWithoutSelected(String word) {
		List<DeAlboSil> albi  = this.vacancyFormPfBean.getAutoCompleteBean().completeAlboSil(word);
		if (albiSelezionati != null && !albiSelezionati.isEmpty())
			albi.removeAll(albiSelezionati);
		return albi;
	}

	public List<DePatenteSil> getPatentiSelezionate() {
		return patentiSelezionate;
	}

	public void setPatentiSelezionate(List<DePatenteSil> patentiSelezionate) {
		this.patentiSelezionate = patentiSelezionate;
	}

	public List<DePatentinoSil> getPatentiniSelezionati() {
		return patentiniSelezionati;
	}

	public void setPatentiniSelezionati(List<DePatentinoSil> patentiniSelezionati) {
		this.patentiniSelezionati = patentiniSelezionati;
	}

	public List<DeAlboSil> getAlbiSelezionati() {
		return albiSelezionati;
	}

	public void setAlbiSelezionati(List<DeAlboSil> albiSelezionati) {
		this.albiSelezionati = albiSelezionati;
	}

	public String getConcatPatentiReadOnly() {
		return concatPatentiReadOnly;
	}

	public void setConcatPatentiReadOnly(String concatPatentiReadOnly) {
		this.concatPatentiReadOnly = concatPatentiReadOnly;
	}

	public String getConcatPatentiniReadOnly() {
		return concatPatentiniReadOnly;
	}

	public void setConcatPatentiniReadOnly(String concatPatentiniReadOnly) {
		this.concatPatentiniReadOnly = concatPatentiniReadOnly;
	}

	public String getConcatAlbiReadOnly() {
		return concatAlbiReadOnly;
	}

	public void setConcatAlbiReadOnly(String concatAlbiReadOnly) {
		this.concatAlbiReadOnly = concatAlbiReadOnly;
	}

}
