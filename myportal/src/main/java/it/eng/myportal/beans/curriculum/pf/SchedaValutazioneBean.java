package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.DecodificheBean;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidaturaValutazione;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.VaInformatica;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvPatenteHome;
import it.eng.myportal.entity.home.CvPatentinoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaAlboHome;
import it.eng.myportal.entity.home.VaAltreInfoHome;
import it.eng.myportal.entity.home.VaCompetenzeTrasvHome;
import it.eng.myportal.entity.home.VaContrattoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaEsperienzeHome;
import it.eng.myportal.entity.home.VaInformaticaHome;
import it.eng.myportal.entity.home.VaIstruzioneHome;
import it.eng.myportal.entity.home.VaOrarioHome;
import it.eng.myportal.entity.home.VaPatenteHome;
import it.eng.myportal.entity.home.VaPatentinoHome;
import it.eng.myportal.entity.home.VaPubblicazioneHome;
import it.eng.myportal.entity.home.VaRapportoDiLavoroHome;
import it.eng.myportal.entity.home.VaRetribuzioneHome;
import it.eng.myportal.entity.home.VaTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.nodto.AcCandidaturaValutazioneHome;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "schedaValutazioneBean")
@ViewScoped
public class SchedaValutazioneBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(SchedaValutazioneBean.class);

	@ManagedProperty(value = "#{decodificheBean}")
	protected DecodificheBean decodificheBean;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;
	@EJB
	private CvAltreInfoHome cvAltreInfoHome;

	@EJB
	AcCandidaturaValutazioneHome acCandidaturaValutazioneHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;
	@EJB
	AcCandidaturaHome acCandidaturaHome;
	@EJB
	private CvIstruzioneHome cvIstruzioneHome;
	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;
	@EJB
	private PfPrincipalHome pfPrincipalHome;
	@EJB
	private CvEsperienzeProfHome cvEsperienzeProfHome;
	@EJB
	private CvInformaticaHome cvInformaticaHome;
	@EJB
	protected VaAltreInfoHome vaAltreInfoHome;
	@EJB
	protected VaIstruzioneHome vaIstruzioneHome;
	@EJB
	protected VaInformaticaHome vaInformaticaHome;
	@EJB
	protected VaEsperienzeHome vaEsperienzeHome;
	private int curriculumId;

	private Integer idAcCandidatura;

	private AcCandidatura acCandidatura;

	private AcCandidaturaValutazione acCandidaturaValutazione;
	private AcCandidaturaValutazione acCandidaturaValutazioneOrig;
	private VaDatiVacancy vaDatiVacancy;
	
	private CvDatiPersonali cvDatiPersonali ;
	private PfPrincipal pfPrincipal;
	private List<CvIstruzione> cvIstruzioniSelezionate  = new ArrayList<>();
	private List<CvEsperienzeProf> esperienzeSelezionate = new ArrayList<>();
	private CvInformatica cvInformatica;
	private CvAltreInfo cvAltreInfo;

	private VaInformatica vaInformatica;
	private List<VaIstruzione> vaIstruzioneList;
	private VaAltreInfo vaAltreInfo;
	private VaEsperienze vaEsperienze;
	private Integer idPfPrincipalAzienda;	
    private PfPrincipal pfPrincipalAzienda;
    private int numberCheckOrderedVacancyToCandidate;
    
    @EJB
    private VaPubblicazioneHome vaPubblicazioneHome;
    @EJB
    private VaAlboHome vaAlboHome;
    @EJB
    private VaPatenteHome vaPatenteHome;
    @EJB
    private VaPatentinoHome vaPatentinoHome;
    
    @EJB
    private CvAlboHome cvAlboHome;
    @EJB
    private CvPatenteHome cvPatenteHome;
    @EJB
    private CvPatentinoHome cvPatentinoHome;
    @EJB
    private CvFormazioneHome cvFormazioneHome;
    
    private VaPubblicazione vaPubblicazione;
    private List<VaAlbo> vaAlboSelezionate  = new ArrayList<>();
    private List<VaPatente> vaPatenteSelezionate  = new ArrayList<>();
    private List<VaPatentino> vaPatentinoSelezionate  = new ArrayList<>();
	
    private List<CvFormazione> cvFormazioniSelezionate  = new ArrayList<>();
    private List<CvAlbo> cvAlboSelezionate  = new ArrayList<>();
    private List<CvPatente> cvPatenteSelezionate  = new ArrayList<>();
    private List<CvPatentino> cvPatentinoSelezionate  = new ArrayList<>();
    
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (map.get("idAcCandidatura") == null) {
			log.error("Impossibile caricare candidaturaValutazione senza parametro idAcCandidatura");
			// nmon si fa niente, da gestire
			return;
		}
		// Load candidatura
		try {
			Integer candId = Integer.valueOf(map.get("idAcCandidatura"));
			acCandidatura = acCandidaturaHome.findById(candId);
			setCvDatiPersonali(cvDatiPersonaliHome.findById(acCandidatura.getCvDatiPersonali().getIdCvDatiPersonali()));
			setPfPrincipal(pfPrincipalHome.findById(cvDatiPersonali.getPfPrincipal().getIdPfPrincipal()));
			
		     idPfPrincipalAzienda = session.getPrincipalId();
		     setPfPrincipalAzienda(pfPrincipalHome.findById(idPfPrincipalAzienda));
		     
			 /* sezione curriculum */
			 loadIstruzioni();
			 loadEsperienze();
			 loadInformatica();
			 loadAltreInfo();
			 loadCvAlbo();
			 loadCvPatente();
			 loadCvPatentino();
			 loadCvFormazione();
			
		} catch (Exception e) {
			log.error("Impossibile caricare candidaturaValutazione senza AcCandidatura:" + e.getMessage());
		}
		if(acCandidatura.getVaDatiVacancy()!= null) {
			// la vacancy, se serve, va caricata qui, dalla candidatura darebbe lazy
			vaDatiVacancy = vaDatiVacancyHome.findById(acCandidatura.getVaDatiVacancy().getIdVaDatiVacancy());

			/* estrazione numero condidatura */
			setNumberCheckOrderedVacancyToCandidate(checkNumberOrderedVacancyToCandidate(
					vaDatiVacancy.getIdVaDatiVacancy(), acCandidatura.getIdAcCandidatura()));

			/* sezione Vacancy */
			loadVaIstruzioni();
			loadVaEsperienze();
			loadVaInformatica();
			loadVaAltreInfo();
			
			loadVaAlbo();
            loadVaPatente();
            loadVaPatentino();
            loadPubblicazione();
			

			// acCandidaturaValutazione =
			// acCandidaturaValutazioneHome.findById(acCandidatura.getIdAcCandidatura());

			try {
				acCandidaturaValutazione = acCandidaturaValutazioneHome
						.findByAcCandidaturaId(acCandidatura.getIdAcCandidatura());

			} catch (MyPortalNoResultFoundException e) {
				acCandidaturaValutazione = new AcCandidaturaValutazione();
				acCandidaturaValutazione.setAcCandidatura(acCandidatura);
				// acCa
			} catch (Exception e) {
				log.error("Impossibile caricare candidaturaValutazione, imprevisto:" + e.getMessage());
			}

			acCandidaturaValutazioneOrig = acCandidaturaValutazione;
		}
	}
	
	public boolean checkModifiyAssegnaValutazioneSchedaVal() {
		boolean check = true;
		boolean isProprietarioPalese = false;
		if(vaDatiVacancy != null) {
		 if (acCandidatura != null && vaDatiVacancy.isFromSIL()) {

			boolean isProprietario = getSession().getPrincipalId()
					.equals(vaDatiVacancy.getPfPrincipal().getIdPfPrincipal());
			if (vaDatiVacancy.getPfPrincipalPalese() != null
					&& getSession().getPrincipalId().equals(vaDatiVacancy.getPfPrincipalPalese().getIdPfPrincipal())) {
				isProprietarioPalese = true;
			}
			boolean isCodEvasioneDFD = vaDatiVacancy.getDeEvasioneRich() != null && vaDatiVacancy.getDeEvasioneRich()
					.getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE); // DFD

			if (vaDatiVacancy.getPfPrincipalPalese() != null) { // caso particolare in cui ha senso fare dei controlli
																// --- in tutti gli altri casi vuol dire che l'utente è
																// UNICO propietario e può modificare (in tal caso
																// ovviamente non DFD)
				if (isProprietario && !isProprietarioPalese) { // caso CPI
					if (isCodEvasioneDFD) {
						return false;
					}
				}
			}
		}
	  }
	return check;
	}
	
	
	/*
	public boolean checkModifiyAssegnaValutazioneSchedaVal() {
		boolean check = false;
		boolean isProprietarioPalese = false;
		 if(vaDatiVacancy != null) {
		if (acCandidatura != null && vaDatiVacancy.isFromSIL()) {

			boolean isProprietario = getSession().getPrincipalId()
					.equals(vaDatiVacancy.getPfPrincipal().getIdPfPrincipal());
			if (vaDatiVacancy.getPfPrincipalPalese() != null
					&& getSession().getPrincipalId().equals(vaDatiVacancy.getPfPrincipalPalese().getIdPfPrincipal())) {
				isProprietarioPalese = true;
			}
			boolean isCodEvasioneDFD = vaDatiVacancy.getDeEvasioneRich() != null && vaDatiVacancy.getDeEvasioneRich()
					.getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE); // DFD

			if (vaDatiVacancy.getPfPrincipalPalese() != null) { // caso particolare in cui ha senso fare dei controlli
																// --- in tutti gli altri casi vuol dire che l'utente è
																// UNICO propietario e può modificare (in tal caso
																// ovviamente non DFD)
				if (isProprietario && !isProprietarioPalese) { // caso CPI
					if (!isCodEvasioneDFD) {
						return true;
					}
				} else if (isProprietario || isProprietarioPalese) { // caso Azienda
					if (isCodEvasioneDFD) {
						return true;
					}
				}
			} else {
				check = true;
			}
		} else {
			check = true;
		}
		 }
		return check;
	}
	*/
	
	private void loadCvFormazione() {
		setCvFormazioniSelezionate(cvFormazioneHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali()));
	}
	
	private void loadCvAlbo() {
		setCvAlboSelezionate(cvAlboHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali()));
	}

	private void loadCvPatente() {
		setCvPatenteSelezionate(cvPatenteHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali()));
	}
	
	private void loadCvPatentino() {
		setCvPatentinoSelezionate(cvPatentinoHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali()));
	}

	
	private void loadIstruzioni() {
		setCvIstruzioniSelezionate(cvIstruzioneHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali()));
	}

	private void loadEsperienze() {
		esperienzeSelezionate = cvEsperienzeProfHome.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
	}
	private void loadInformatica() {
		setCvInformatica(cvInformaticaHome.findById(cvDatiPersonali.getIdCvDatiPersonali()));
	}

	private void loadVaAlbo() {
		setVaAlboSelezionate(vaAlboHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));
	}

	private void loadVaPatente() {
		setVaPatenteSelezionate(vaPatenteHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));
	}
	
	private void loadVaPatentino() {
		setVaPatentinoSelezionate(vaPatentinoHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));
	}

	private void loadPubblicazione() {
		setVaPubblicazione(vaPubblicazioneHome.findByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));
	}
	
	private void loadAltreInfo() {
		cvAltreInfo = cvAltreInfoHome.findById(cvDatiPersonali.getIdCvDatiPersonali());
	}
	
	private void loadVaIstruzioni() {
		setVaIstruzioneList(vaIstruzioneHome.findProperByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));				
	}

	private void loadVaEsperienze() {
		setVaEsperienze(vaEsperienzeHome.findByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));
	}
	private void loadVaInformatica() {
		setVaInformatica(vaInformaticaHome.findByVacancyId(vaDatiVacancy.getIdVaDatiVacancy()));			
	}
	private void loadVaAltreInfo() {
		setVaAltreInfo(vaAltreInfoHome.findById(vaDatiVacancy.getIdVaDatiVacancy()));
	}
	
	public int checkNumberOrderedVacancyToCandidate(Integer vacancyId, Integer acCandidaturaId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		for (int j = 0; j < listcandidature.size(); j++) {
			if(listcandidature.get(j).getIdAcCandidatura() == acCandidaturaId.intValue()) {
				i = j;
				break;
			}
		}
		return ++i;
	}

	public void sync() {
		// Integer idPfPrincipalAzienda = session.getPrincipalId();
		// UtenteDTO utente = session.getConnectedUtente();
		log.info("Scheda valutazione inserita");
		if (acCandidaturaValutazione.getDtmIns() == null) {
			acCandidaturaValutazione = acCandidaturaValutazioneHome.persist(acCandidaturaValutazione,
					idPfPrincipalAzienda);
		} else {
			acCandidaturaValutazione = acCandidaturaValutazioneHome.merge(acCandidaturaValutazione,
					idPfPrincipalAzienda);
		}
	}

	public void cancelEdit() {
		acCandidaturaValutazione = acCandidaturaValutazioneOrig;
	}
	
	public DecodificheBean getDecodificheBean() {
		return decodificheBean;
	}

	public void setDecodificheBean(DecodificheBean decodificheBean) {
		this.decodificheBean = decodificheBean;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}

	public int getCurriculumId() {
		return curriculumId;
	}

	public void setCurriculumId(int curriculumId) {
		this.curriculumId = curriculumId;
	}

	public AcCandidaturaValutazione getAcCandidaturaValutazione() {
		return acCandidaturaValutazione;
	}

	public void setAcCandidaturaValutazione(AcCandidaturaValutazione acCandidaturaValutazione) {
		this.acCandidaturaValutazione = acCandidaturaValutazione;
	}

	public VaDatiVacancy getVaDatiVacancy() {
		return vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	public AcCandidatura getAcCandidatura() {
		return acCandidatura;
	}

	public void setAcCandidatura(AcCandidatura acCandidatura) {
		this.acCandidatura = acCandidatura;
	}

	public CvDatiPersonali getCvDatiPersonali() {
		return cvDatiPersonali;
	}

	public void setCvDatiPersonali(CvDatiPersonali cvDatiPersonali) {
		this.cvDatiPersonali = cvDatiPersonali;
	}

	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}


	public List<CvIstruzione> getCvIstruzioniSelezionate() {
		return cvIstruzioniSelezionate;
	}


	public void setCvIstruzioniSelezionate(List<CvIstruzione> cvIstruzioniSelezionate) {
		this.cvIstruzioniSelezionate = cvIstruzioniSelezionate;
	}


	public List<CvEsperienzeProf> getEsperienzeSelezionate() {
		return esperienzeSelezionate;
	}


	public void setEsperienzeSelezionate(List<CvEsperienzeProf> esperienzeSelezionate) {
		this.esperienzeSelezionate = esperienzeSelezionate;
	}


	public CvInformatica getCvInformatica() {
		return cvInformatica;
	}


	public void setCvInformatica(CvInformatica cvInformatica) {
		this.cvInformatica = cvInformatica;
	}
	public CvAltreInfo getCvAltreInfo() {
		return cvAltreInfo;
	}


	public void setCvAltreInfo(CvAltreInfo cvAltreInfo) {
		this.cvAltreInfo = cvAltreInfo;
	}


	public VaInformatica getVaInformatica() {
		return vaInformatica;
	}


	public void setVaInformatica(VaInformatica vaInformatica) {
		this.vaInformatica = vaInformatica;
	}


	public List<VaIstruzione> getVaIstruzioneList() {
		return vaIstruzioneList;
	}


	public void setVaIstruzioneList(List<VaIstruzione> vaIstruzioneList) {
		this.vaIstruzioneList = vaIstruzioneList;
	}


	public VaAltreInfo getVaAltreInfo() {
		return vaAltreInfo;
	}


	public void setVaAltreInfo(VaAltreInfo vaAltreInfo) {
		this.vaAltreInfo = vaAltreInfo;
	}


	public VaEsperienze getVaEsperienze() {
		return vaEsperienze;
	}


	public void setVaEsperienze(VaEsperienze vaEsperienze) {
		this.vaEsperienze = vaEsperienze;
	}


	public PfPrincipal getPfPrincipalAzienda() {
		return pfPrincipalAzienda;
	}


	public void setPfPrincipalAzienda(PfPrincipal pfPrincipalAzienda) {
		this.pfPrincipalAzienda = pfPrincipalAzienda;
	}


	public int getNumberCheckOrderedVacancyToCandidate() {
		return numberCheckOrderedVacancyToCandidate;
	}


	public void setNumberCheckOrderedVacancyToCandidate(int numberCheckOrderedVacancyToCandidate) {
		this.numberCheckOrderedVacancyToCandidate = numberCheckOrderedVacancyToCandidate;
	}

	public VaPubblicazione getVaPubblicazione() {
		return vaPubblicazione;
	}

	public void setVaPubblicazione(VaPubblicazione vaPubblicazione) {
		this.vaPubblicazione = vaPubblicazione;
	}

	public List<VaAlbo> getVaAlboSelezionate() {
		return vaAlboSelezionate;
	}

	public void setVaAlboSelezionate(List<VaAlbo> vaAlboSelezionate) {
		this.vaAlboSelezionate = vaAlboSelezionate;
	}

	public List<VaPatente> getVaPatenteSelezionate() {
		return vaPatenteSelezionate;
	}

	public void setVaPatenteSelezionate(List<VaPatente> vaPatenteSelezionate) {
		this.vaPatenteSelezionate = vaPatenteSelezionate;
	}

	public List<VaPatentino> getVaPatentinoSelezionate() {
		return vaPatentinoSelezionate;
	}

	public void setVaPatentinoSelezionate(List<VaPatentino> vaPatentinoSelezionate) {
		this.vaPatentinoSelezionate = vaPatentinoSelezionate;
	}

	public List<CvFormazione> getCvFormazioniSelezionate() {
		return cvFormazioniSelezionate;
	}

	public void setCvFormazioniSelezionate(List<CvFormazione> cvFormazioniSelezionate) {
		this.cvFormazioniSelezionate = cvFormazioniSelezionate;
	}

	public List<CvAlbo> getCvAlboSelezionate() {
		return cvAlboSelezionate;
	}

	public void setCvAlboSelezionate(List<CvAlbo> cvAlboSelezionate) {
		this.cvAlboSelezionate = cvAlboSelezionate;
	}

	public List<CvPatente> getCvPatenteSelezionate() {
		return cvPatenteSelezionate;
	}

	public void setCvPatenteSelezionate(List<CvPatente> cvPatenteSelezionate) {
		this.cvPatenteSelezionate = cvPatenteSelezionate;
	}

	public List<CvPatentino> getCvPatentinoSelezionate() {
		return cvPatentinoSelezionate;
	}

	public void setCvPatentinoSelezionate(List<CvPatentino> cvPatentinoSelezionate) {
		this.cvPatentinoSelezionate = cvPatentinoSelezionate;
	}
}
