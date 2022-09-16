package it.eng.myportal.beans.vacancies.pf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.VaTurno;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

public class VaCondizioniProposteSection extends VacancyBaseFormSection implements IVacancySection {

	VaContratto vaContratto;
	VaOrario vaOrario;
	VaTurno vaTurno;
	VaRetribuzione vaRetribuzione;
	VaAltreInfo vaAltreInfo;
	
	private String concatContrattiReadOnly = "";
	private String concatOrariReadOnly = "";

	private List<DeOrarioSil> orariSelezionati;
	private List<DeContrattoSil> contrattiSelezionati;

	public VaCondizioniProposteSection(VacancyFormPfBean cvBean) {
		super(cvBean);

	}

	public Date getCurrentDate() {
		return new Date();
	}

	@Override
	public void initSection() {
		orariSelezionati = new ArrayList<>();
		contrattiSelezionati = new ArrayList<>();

		Integer vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		List<VaOrario> vaturniSelezionati = getVacancyFormPfBean().vaOrarioHome.findProperByVacancyId(vacancyId);
		for (VaOrario vaTurno : vaturniSelezionati) {
			orariSelezionati.add(vaTurno.getDeOrarioSil());
		}
		if(vaturniSelezionati != null && !vaturniSelezionati.isEmpty()) {
			   popolaConcatOrariReadOnly(vaturniSelezionati);
	    }
		
		List<VaContratto> vacontrattiSelezionati = getVacancyFormPfBean().vaContrattoHome
				.findProperByVacancyId(vacancyId);
		for (VaContratto vaContratto : vacontrattiSelezionati) {
			contrattiSelezionati.add(vaContratto.getDeContrattoSil());
		}
        if(vacontrattiSelezionati != null && !vacontrattiSelezionati.isEmpty()) {
		   popolaConcatContrattiReadOnly(vacontrattiSelezionati);
        }
		try {
			vaRetribuzione = getVacancyFormPfBean().vaRetribuzioneHome.findProperByVacancyId(vacancyId);
		} catch (MyPortalNoResultFoundException e) {
			vaRetribuzione = new VaRetribuzione();
		}

		List<VaTurno> turni = getVacancyFormPfBean().vaTurnoHome.findProperByVacancyId(vacancyId);
		if (turni.size() > 0)
			vaTurno = turni.iterator().next();// get 0
		else {
			vaTurno = new VaTurno();
			vaTurno.setVaDatiVacancy(getVacancyFormPfBean().getVaDatiVacancy());
		}
		vaAltreInfo = getVacancyFormPfBean().vaAltreInfoHome.findByVacancyId(vacancyId);
		if (vaAltreInfo == null)
			vaAltreInfo = new VaAltreInfo();
		vaAltreInfo.setVaDatiVacancy(getVacancyFormPfBean().getVaDatiVacancy());
	}

	private void popolaConcatContrattiReadOnly(List<VaContratto> listContrattiSelezionati) {
		concatContrattiReadOnly = "";
		for (int i = 0; i < listContrattiSelezionati.size(); i++) {
			concatContrattiReadOnly = concatContrattiReadOnly.concat(((VaContratto)listContrattiSelezionati.get(i)).getDeContrattoSil().getDescrizione());
			if(i < listContrattiSelezionati.size()- 1) {
			  concatContrattiReadOnly = concatContrattiReadOnly.concat( "  -  ");
			}
		}
	}
	private void popolaConcatOrariReadOnly(List<VaOrario> listOrariSelezionati) {
		concatOrariReadOnly = "";
		for (int i = 0; i < listOrariSelezionati.size(); i++) {
			concatOrariReadOnly = concatOrariReadOnly.concat(((VaOrario)listOrariSelezionati.get(i)).getDeOrarioSil().getDescrizioneIdo());
			if(i < listOrariSelezionati.size()- 1) {
				concatOrariReadOnly = concatOrariReadOnly.concat( " - ");
			}
		}
	}
	
	@Override
	public void sync() {
		VaDatiVacancy vaDatiVacancyToSave = getVacancyFormPfBean().getVaDatiVacancy();
		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		Date now = new Date();
		now.toString();

		ArrayList<DeOrarioSil> orariCopy = new ArrayList<>();
		if (orariSelezionati != null)
			orariCopy.addAll(orariSelezionati);

		List<VaOrario> fromDbVaOrari = getVacancyFormPfBean().vaOrarioHome
				.findProperByVacancyId(vaDatiVacancyToSave.getIdVaDatiVacancy());
		Set<VaOrario> refreshedSet = new HashSet<VaOrario>();
		for (VaOrario vaOrario : fromDbVaOrari) {
			if (orariCopy.contains(vaOrario.getDeOrarioSil())) {
				refreshedSet.add(getVacancyFormPfBean().vaOrarioHome.merge(vaOrario, idPfPrinc));
				orariCopy.remove(vaOrario.getDeOrarioSil());
			} else {
				// va cancellato
				getVacancyFormPfBean().vaOrarioHome.remove(vaOrario);
			}
		}

		// giro inverso, rimangono quelle da creare
		for (DeOrarioSil dePatNew : orariCopy) {
			VaOrario newPat = new VaOrario();
			newPat.setDeOrarioSil(dePatNew);
			newPat.setVaDatiVacancy(vaDatiVacancyToSave);
			refreshedSet.add(getVacancyFormPfBean().vaOrarioHome.persist(newPat, idPfPrinc));
		}
		// synch mbean's object
		getVacancyFormPfBean().getVaDatiVacancy().setVaOrarios(refreshedSet);

		// CONTRATTI
		ArrayList<DeContrattoSil> contrattiCopy = new ArrayList<>();
		if (contrattiSelezionati != null)
			contrattiCopy.addAll(contrattiSelezionati);

		List<VaContratto> fromDbc = getVacancyFormPfBean().vaContrattoHome
				.findProperByVacancyId(vaDatiVacancyToSave.getIdVaDatiVacancy());
		List<VaContratto> refreshedSetc = new ArrayList<VaContratto>();
		for (VaContratto vaContratto : fromDbc) {
			
			VaContratto test = getVacancyFormPfBean().vaContrattoHome.findById(vaContratto.getIdVaContratto());
			if (test == null)
			{
				//MA CHE COS
			}
			
			if (contrattiCopy.contains(vaContratto.getDeContrattoSil())) {
				refreshedSetc.add(getVacancyFormPfBean().vaContrattoHome.merge(vaContratto, idPfPrinc));
				contrattiCopy.remove(vaContratto.getDeContrattoSil());
			} else {
				// va cancellato
				getVacancyFormPfBean().vaContrattoHome.remove(vaContratto);
			}
		}

		// giro inverso, rimangono quelle da creare
		for (DeContrattoSil dePatNew : contrattiCopy) {
			VaContratto newPat = new VaContratto();
			newPat.setDeContrattoSil(dePatNew);
			newPat.setVaDatiVacancy(vaDatiVacancyToSave);
			refreshedSetc.add(getVacancyFormPfBean().vaContrattoHome.persist(newPat, idPfPrinc));
		}
		getVacancyFormPfBean().getVaDatiVacancy().setVaContrattos(refreshedSetc);

		vaRetribuzione.setVaDatiVacancy(getVacancyFormPfBean().getVaDatiVacancy());
		// default, analisi pag.59
		DeRetribuzione deRetribuzione = getVacancyFormPfBean().deRetribuzioneHome.findById("R00");
		vaRetribuzione.setDeRetribuzione(deRetribuzione);
		if (vaRetribuzione.getDtmIns() == null)
			vaRetribuzione = getVacancyFormPfBean().vaRetribuzioneHome.persist(vaRetribuzione, idPfPrinc);
		else
			vaRetribuzione = getVacancyFormPfBean().vaRetribuzioneHome.merge(vaRetribuzione, idPfPrinc);
		getVacancyFormPfBean().getVaDatiVacancy().setVaRetribuzione(vaRetribuzione);
		
		//ALTRE INFO
		vaAltreInfo.setVaDatiVacancy(getVacancyFormPfBean().getVaDatiVacancy());
		if (vaAltreInfo.getDtmIns() == null)
			vaAltreInfo = getVacancyFormPfBean().vaAltreInfoHome.persist(vaAltreInfo, idPfPrinc);
		else
			vaAltreInfo = getVacancyFormPfBean().vaAltreInfoHome.merge(vaAltreInfo, idPfPrinc);
		getVacancyFormPfBean().getVaDatiVacancy().setVaAltreInfo(vaAltreInfo);
		
		 // aggiornare VaDatiVacancy
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
     	homeEJB.merge(getVacancyFormPfBean().getVaDatiVacancy(), idPfPrinc);
		
	}

	public void handleChangeContratto(SelectEvent event) {
		DeContrattoSil dePatenteAdded = (DeContrattoSil) event.getObject();
		sync();
	}

	public void handleChangeOrario(SelectEvent event) {
		DeOrarioSil dePatenteAdded = (DeOrarioSil) event.getObject();
		sync();
	}

	public void handleChangeOrario(UnselectEvent event) {
		sync();
	}

	public void handleChangeContratto(UnselectEvent event) {
		sync();
	}

	public List<DeContrattoSil> completeContrattoSilWithoutSelected(String word) {
		List<DeContrattoSil> tipi = this.vacancyFormPfBean.getAutoCompleteBean().completeContrattoSil(word);
		if (contrattiSelezionati != null && !contrattiSelezionati.isEmpty())
			tipi.removeAll(contrattiSelezionati);
		return tipi;
	}
	
	public List<DeContrattoSil> completeContrattoSilFlagIdoTrueWithoutSelected(String word) {
		List<DeContrattoSil> tipi = this.vacancyFormPfBean.getAutoCompleteBean().completeContrattoSilFlagIdoTrue(word);
		if (contrattiSelezionati != null && !contrattiSelezionati.isEmpty())
			tipi.removeAll(contrattiSelezionati);
		return tipi;
	}

	public List<DeOrarioSil> completeOrarioSilWithoutSelected(String word) {
		List<DeOrarioSil> tipi = this.vacancyFormPfBean.getAutoCompleteBean().completeOrarioSil(word);
		if (orariSelezionati != null && !orariSelezionati.isEmpty())
			tipi.removeAll(orariSelezionati);
		return tipi;
	}
	
	public List<DeOrarioSil> completeOrarioSilFlagIdoTrueWithoutSelected(String word) {
		List<DeOrarioSil> tipi = this.vacancyFormPfBean.getAutoCompleteBean().completeOrarioSilFlagIdoTrue(word);
		if (orariSelezionati != null && !orariSelezionati.isEmpty())
			tipi.removeAll(orariSelezionati);
		return tipi;
	}

	public VaRetribuzione getVaRetribuzione() {
		return vaRetribuzione;
	}

	public void setVaRetribuzione(VaRetribuzione vaRetribuzione) {
		this.vaRetribuzione = vaRetribuzione;
	}

	public List<DeOrarioSil> getOrariSelezionati() {
		return orariSelezionati;
	}

	public void setOrariSelezionati(List<DeOrarioSil> orariSelezionati) {
		this.orariSelezionati = orariSelezionati;
	}

	public List<DeContrattoSil> getContrattiSelezionati() {
		return contrattiSelezionati;
	}

	public void setContrattiSelezionati(List<DeContrattoSil> or) {
		this.contrattiSelezionati = or;
	}

	public VaTurno getVaTurno() {
		return vaTurno;
	}

	public void setVaTurno(VaTurno vaTurno) {
		this.vaTurno = vaTurno;
	}

	public VaOrario getVaOrario() {
		return vaOrario;
	}

	public void setVaOrario(VaOrario vaOrario) {
		this.vaOrario = vaOrario;
	}

	public VaAltreInfo getVaAltreInfo() {
		return vaAltreInfo;
	}

	public void setVaAltreInfo(VaAltreInfo vaAltreInfo) {
		this.vaAltreInfo = vaAltreInfo;
	}

	public String getConcatContrattiReadOnly() {
		return concatContrattiReadOnly;
	}

	public void setConcatContrattiReadOnly(String concatContrattiReadOnly) {
		this.concatContrattiReadOnly = concatContrattiReadOnly;
	}

	public String getConcatOrariReadOnly() {
		return concatOrariReadOnly;
	}

	public void setConcatOrariReadOnly(String concatOrariReadOnly) {
		this.concatOrariReadOnly = concatOrariReadOnly;
	}

}
