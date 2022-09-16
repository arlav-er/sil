package it.eng.myportal.beans.curriculum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean
@ViewScoped
public class CurriculumOffcanvasBean extends AbstractBaseBean implements Serializable {

	private static final long serialVersionUID = 5537670668031203617L;

	protected static Log log = LogFactory.getLog(CurriculumOffcanvasBean.class);

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	private CvAltreInfoHome cvAltreInfoHome;

	@EJB
	private CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	CvLinguaHome cvLinguaHome;
	
	@EJB
	private CvIstruzioneHome cvIstruzioneHome;
	
	private CvDatiPersonali selectedDatiPersonali;
	private CvAltreInfo selectedCvAltreInfo;
	private List<CvEsperienzeProf> esperienzeSelezionate = new ArrayList<>();
	private List<CvLingua> lingueSelezionate = new ArrayList<>();
	private List<CvIstruzione> cvIstruzioniSelezionate = new ArrayList<>();
	
	private Integer idVaDatiVacancy;
	private Integer idAcCandidatura;

	public void onDatiPersonaliSelected(CvDatiPersonali sCvDatiPersonali) {
		selectedDatiPersonali = sCvDatiPersonali;
		selectedCvAltreInfo = cvAltreInfoHome.findById(selectedDatiPersonali.getIdCvDatiPersonali());
		esperienzeSelezionate = cvEsperienzeProfHome
				.findProperByCurriculumId(selectedDatiPersonali.getIdCvDatiPersonali());
		lingueSelezionate = cvLinguaHome.findLinguaByCurriculumId(selectedDatiPersonali.getIdCvDatiPersonali());
		cvIstruzioniSelezionate = cvIstruzioneHome
				.findProperByCurriculumId(selectedDatiPersonali.getIdCvDatiPersonali());
	}
	
	public void onDatiPersonaliSelectedComplete(CvDatiPersonali sCvDatiPersonali, Integer idVaDatiVacancy, Integer idAcCandidatura) {
	    this.onDatiPersonaliSelected(sCvDatiPersonali);
		if(idVaDatiVacancy != null) {
			this.idVaDatiVacancy = idVaDatiVacancy;
		}
		if(idAcCandidatura != null) {
			this.idAcCandidatura = idAcCandidatura;
		}
	}
	

	public void onDatiPersonaliSelected(Integer curriculumId) {
		CvDatiPersonali selectedCanvasCvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);
		this.onDatiPersonaliSelected(selectedCanvasCvDatiPersonali);
	}
	
	public void onDatiPersonaliSelectedComplete(Integer curriculumId, Integer idVaDatiVacancy, Integer idAcCandidatura) {
		CvDatiPersonali selectedCanvasCvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);
		this.onDatiPersonaliSelectedComplete(selectedCanvasCvDatiPersonali, idVaDatiVacancy, idAcCandidatura);
	}

	public String fillLinguaLevel(ConstantsSingleton.LivelloLinguaEnum toBeFilled,
			ConstantsSingleton.LivelloLinguaEnum actualLevel) {
		String fillClass = "fill";

		if (actualLevel == null)
			return "";

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.MEDIO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.SCOLASTICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.MEDIO)) {
			return fillClass;
		}

		return "";
	}

	public String getIntestatario() {
		Integer idPrinc = selectedDatiPersonali.getPfPrincipal().getIdPfPrincipal();
		PfPrincipal pfPrinc = pfPrincipalHome.findById(idPrinc);

		return pfPrinc.getNome().substring(0, 1).toUpperCase() + pfPrinc.getNome().substring(1) + " "
				+ pfPrinc.getCognome().substring(0, 1).toUpperCase() + pfPrinc.getCognome().substring(1);

	}

	public DeComune getComuneDomicilio() {
		if (selectedDatiPersonali.getDeComuneDomicilio() != null) {
			return deComuneHome.findById(selectedDatiPersonali.getDeComuneDomicilio().getCodCom());
		}

		return null;
	}

	public CvDatiPersonali getSelectedDatiPersonali() {
		return selectedDatiPersonali;
	}

	public void setSelectedDatiPersonali(CvDatiPersonali selectedDatiPersonali) {
		this.selectedDatiPersonali = selectedDatiPersonali;
	}

	public CvAltreInfo getSelectedCvAltreInfo() {
		return selectedCvAltreInfo;
	}

	public void setSelectedCvAltreInfo(CvAltreInfo selectedCvAltreInfo) {
		this.selectedCvAltreInfo = selectedCvAltreInfo;
	}

	public List<CvEsperienzeProf> getEsperienzeSelezionate() {
		return esperienzeSelezionate;
	}

	public void setEsperienzeSelezionate(List<CvEsperienzeProf> esperienzeSelezionate) {
		this.esperienzeSelezionate = esperienzeSelezionate;
	}

	public List<CvLingua> getLingueSelezionate() {
		return lingueSelezionate;
	}

	public void setLingueSelezionate(List<CvLingua> lingueSelezionate) {
		this.lingueSelezionate = lingueSelezionate;
	}

	public List<CvIstruzione> getCvIstruzioniSelezionate() {
		return cvIstruzioniSelezionate;
	}

	public void setCvIstruzioniSelezionate(List<CvIstruzione> cvIstruzioniSelezionate) {
		this.cvIstruzioniSelezionate = cvIstruzioniSelezionate;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}

}
