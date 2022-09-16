package it.eng.myportal.beans.curriculum.pf;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.DecodificheBean;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PrimoContattoDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.ejb.ClicLavoroPrimoContattoEjb;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.CvPatenteHome;
import it.eng.myportal.entity.home.CvPatentinoHome;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeIdoneitaCandidaturaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;

@ManagedBean(name = "curriculumVitaePfViewBean")
@ViewScoped
public class CurriculumVitaePfViewBean extends AbstractBaseBean {

	private static final String CURRICULUM_ID = "curriculumId";

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(CurriculumVitaePfViewBean.class);

	// Refactoring PF - START
	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	@EJB
	CvPatenteHome cvPatenteHome;

	@EJB
	CvAlboHome cvAlboHome;

	@EJB
	CvPatentinoHome cvPatentinoHome;

	@EJB
	private DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	@EJB
	private CvCompetenzeTrasvHome cvCompetenzeTrasvHome;

	@ManagedProperty(value = "#{decodificheBean}")
	protected DecodificheBean decodificheBean;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private CvAltreInfoHome cvAltreInfoHome;

	@EJB
	private CvIstruzioneHome cvIstruzioneHome;

	@EJB
	private CvFormazioneHome cvFormazioneHome;

	@EJB
	private CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	private CvCompetenzeTrasvHome CvCompetenzeTrasvHome;

	@EJB
	private CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	private CvInformaticaHome cvInformaticaHome;

	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DeModalitaLinguaHome deModalitaLinguaHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	DeIdoneitaCandidaturaHome deIdoneitaCandidaturaHome;
	
	@EJB
	ClicLavoroPrimoContattoEjb primoContattoEjb;

	private int curriculumId;
	// used in sendEmail modal
	private String messageTitle;
	private String messageBody;
	/**
	 * id curriculum recuperato dalla sessione in caso di 'torna indietro'
	 */
	private String curriculumIdStr;

	private CvDatiPersonali cvDatiPersonali;
	private CvAltreInfo cvAltreInfo;
	private CvInformatica cvInformatica;
	private CvCompetenzeTrasv cvCompetenzeTrasv;
	// CV VIEW CONTENTS
	private List<DePatenteSil> patentiSelezionate = new ArrayList<>();
	private List<DePatentinoSil> patentiniSelezionati = new ArrayList<>();
	private List<DeAlboSil> albiSelezionati = new ArrayList<>();
	private List<CvIstruzione> cvIstruzioniSelezionate = new ArrayList<>();
	private List<CvFormazione> cvFormazioniSelezionate = new ArrayList<>();
	private List<CvLingua> lingueSelezionate = new ArrayList<>();
	private List<DeGradoLin> gradoLingueList = new ArrayList<>();
	private List<CvProfDesiderate> cvProfessioniSelezionate = new ArrayList<>();
	private List<CvEsperienzeProf> esperienzeSelezionate = new ArrayList<>();
	private CvAllegato allegatoSelezionato;
	private boolean checkAllegatoSelezionato = false;
	private AcCandidatura livelliValutazione;
	private Integer idAcCandidatura;
    private VaDatiVacancy vaVacancy;
	private String codIdoneitaCandidatura;
	private String notaValutazione;
	// per quelle LAZY
	private DeComune comuneNascita;

	private DeComune comuneDomicilio;
	private int numTotalCandidature;
	private int numCandidature;
	private boolean hasCandidatura = false;
	
	/**
	 * Contatto da parte dell'azienda che visualizza il CV
	 */
	private PrimoContattoDTO primoContatto;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// utenteCompletoDTO =
		// utenteInfoHome.findDTOCompletoById(session.getPrincipalId());

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		curriculumId = Integer.parseInt(StringUtils.defaultString(map.get("id"), curriculumIdStr));
		putParamsIntoSession();
		String idAcCandidaturaString = map.get("idAcCandidatura");
		if (!StringUtils.isBlank(idAcCandidaturaString)) {
			idAcCandidatura = Integer.parseInt(idAcCandidaturaString);
		}

		cvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);
		
		if(cvDatiPersonali == null) {
			log.error("Errore durante il caricamento del Curriculum Vitae.[curriculumId]: " + curriculumId);
			redirectGrave("curriculum.error_loading");
			throw new MyPortalException("curriculum.error_loading");
		}
		
		if (cvDatiPersonali.getDeComuneDomicilio() != null) {
			comuneDomicilio = deComuneHome.findById(cvDatiPersonali.getDeComuneDomicilio().getCodCom());
		}
		if (cvDatiPersonali.getDeComuneNascita() != null) {
			comuneNascita = deComuneHome.findById(cvDatiPersonali.getDeComuneNascita().getCodCom());
		}
		
		
		hasCandidatura = idAcCandidatura != null;
		boolean isAziendaSession = getSession().isAzienda();
		boolean isProvinciaSession = getSession().isProvincia();

		if (!isAziendaSession &&  !isProvinciaSession) {
			if (!cvDatiPersonali.getPfPrincipal().getIdPfPrincipal().equals(session.getPrincipalId())) {
				
				if(cvDatiPersonali.getPfPrincipalPalese() == null || ( cvDatiPersonali.getPfPrincipalPalese() != null && !cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal().equals(session.getPrincipalId())) ){
					log.error("Violazione di sicurezza per manipolazione di parametri [curriculumId]");
					log.error("Utente connesso: " + session.getPrincipalId());
					log.error("Proprietario CV:" + cvDatiPersonali.getPfPrincipalIns().getIdPfPrincipal());
					// addErrorMessage("user.is_not");
					redirectGrave("generic.manipulation_error");
					throw new MyPortalException("user.is_not");
				}
			}
		}
		// TODO da completare la gestione della Candidatura

		numTotalCandidature = 0;
		numCandidature = 0;
		
		if (hasCandidatura) {
			livelliValutazione = new AcCandidatura();
			livelliValutazione = acCandidaturaHome.findById(idAcCandidatura);
			livelliValutazione.setCvDatiPersonali(cvDatiPersonali);
            if(livelliValutazione.getDeIdoneitaCandidatura() != null) {
			  codIdoneitaCandidatura = livelliValutazione.getDeIdoneitaCandidatura().getCodIdoneitaCandidatura();
             }
			if(livelliValutazione.getVaDatiVacancy() != null) {
			    numCandidature = checkNumberOrderedVacancyToCandidate(livelliValutazione.getVaDatiVacancy().getIdVaDatiVacancy(), livelliValutazione.getIdAcCandidatura());		
				numTotalCandidature = checkTotalNumberOrderedVacancyToCandidate(livelliValutazione.getVaDatiVacancy().getIdVaDatiVacancy());
				
				vaVacancy = vaDatiVacancyHome.findById(livelliValutazione.getVaDatiVacancy().getIdVaDatiVacancy());
				if (vaVacancy == null) {
					// redirect404();
					return;
				}
				setVaVacancy(vaVacancy);
			}
		}

		loadEsperienze();
		loadAbilitazioni();
		loadIstruzioni();
		loadAltreInfo();
		loadCompetenzeTrasversali();
		loadInformatica();
		loadLingue();
		loadFormazioni();
		loadProfessioni();
		loadAllegati();
		
		
		primoContatto = new PrimoContattoDTO();
		Integer idCvDatiPersonali = cvDatiPersonali.getIdCvDatiPersonali();
		primoContatto.setIdCvDatiPersonali(idCvDatiPersonali);
		primoContatto.setIdPfPrincipalAzienda(getSession().getPrincipalId());
		

	}
		
	public boolean checkAutoCandidatura() {
		boolean check= false;
		if(vaVacancy == null) { // caso normale delle autocandidature
			check = true;
		}
		return check;		
	}
	
	public boolean checkVisibilityAssegnaValutazioneSchedaVal() {
		boolean check= false;
		boolean isProprietarioPalese = false;
		if(!cvDatiPersonali.getFlagInviato()) {
			return false;
		}
		if(vaVacancy != null) {
			if(hasCandidatura && vaVacancy.isFromSIL()) {
				boolean isProprietario = getSession().getPrincipalId().equals(vaVacancy.getPfPrincipal().getIdPfPrincipal());
				if((vaVacancy.getPfPrincipalPalese() != null && getSession().getPrincipalId().equals(vaVacancy.getPfPrincipalPalese().getIdPfPrincipal()) ) ) {
					isProprietarioPalese = true;
				}
				boolean isCodEvasioneDFD = vaVacancy.getDeEvasioneRich() != null && vaVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE) ; // DFD
				
				if (vaVacancy.getPfPrincipalPalese() != null) {
					if (isProprietario && !isProprietarioPalese) { // caso CPI
					  //if(!isCodEvasioneDFD) {
						return true;
					  // }
					} else if (isProprietario || isProprietarioPalese) { // caso Azienda
					  //	if (isCodEvasioneDFD) {
							return true;
					  //	}
					}
				} else {
					return true;
				}
				// se c'è la candidatura e provenienza è SIL :
	            //   1) se azienda ed è proprietaria della vacancy quindi idpfprincipal == idpfprincipal azienda  può visualizzare e modificare 
	            //   2) se azienda non è proprietaria ma il idpfprincipalpalese  = idpfprincipal and cod evasione è DFD potrà visualizzare  e modificare 
				//   3) se CPI quindi  idpfprincipalpalese  <> idpfprincipal and idpfprincipal == idpfprincipal (titolare) and cod evasione è <> DFD potrà visualizzare e modificare
				//   4)  se CPI quindi  idpfprincipalpalese  <> idpfprincipal and idpfprincipal == idpfprincipal (titolare) and cod evasione è == DFD  potrà visualizzare e e non modificare
									
			}else {
				check= true;
			}
		}
		else {
			check=true; // caso autocandidature
		}
		return check;
	}
	
	public boolean checkVisibilityContattaLavoratore() {
		boolean check= true;
		boolean isProprietarioPalese = false;
		if(vaVacancy != null) {
		if(hasCandidatura && vaVacancy.isFromSIL()) {
			
			boolean isProprietario = getSession().getPrincipalId().equals(vaVacancy.getPfPrincipal().getIdPfPrincipal());
			if((vaVacancy.getPfPrincipalPalese() != null && getSession().getPrincipalId().equals(vaVacancy.getPfPrincipalPalese().getIdPfPrincipal()) ) ) {
				isProprietarioPalese = true;
			}
			boolean isCodEvasioneDFD = vaVacancy.getDeEvasioneRich() != null && vaVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE) ; // DFD
			
			if (vaVacancy.getPfPrincipalPalese() != null) {
				if (isProprietario && !isProprietarioPalese) { // caso CPI
				  if(isCodEvasioneDFD) {
					return false;
				   }
				} 
			} 
			// se c'è la candidatura e provenienza è SIL :
            //   1) se azienda ed è proprietaria della vacancy quindi idpfprincipal == idpfprincipal azienda  può visualizzare e modificare 
            //   2) se azienda non è proprietaria ma il idpfprincipalpalese  = idpfprincipal and cod evasione è DFD potrà visualizzare  e modificare 
			//   3) se CPI quindi  idpfprincipalpalese  <> idpfprincipal and idpfprincipal == idpfprincipal (titolare) and cod evasione è <> DFD potrà visualizzare e modificare
			//   4)  se CPI quindi  idpfprincipalpalese  <> idpfprincipal and idpfprincipal == idpfprincipal (titolare) and cod evasione è == DFD  potrà visualizzare e e non modificare
								
		  }
		}else check=true; // caso autocandidature
		return check;		
	}
	public boolean checkModifiyAssegnaValutazioneSchedaVal() {
		boolean check = false;
		boolean isProprietarioPalese = false;
		if(vaVacancy!=null) {
		if (hasCandidatura && vaVacancy.isFromSIL()) {

			boolean isProprietario = getSession().getPrincipalId()
					.equals(vaVacancy.getPfPrincipal().getIdPfPrincipal());
			if (vaVacancy.getPfPrincipalPalese() != null
					&& getSession().getPrincipalId().equals(vaVacancy.getPfPrincipalPalese().getIdPfPrincipal())) {
				isProprietarioPalese = true;
			}
			boolean isCodEvasioneDFD = vaVacancy.getDeEvasioneRich() != null && vaVacancy.getDeEvasioneRich()
					.getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE); // DFD

			if (vaVacancy.getPfPrincipalPalese() != null) { // caso particolare in cui ha senso fare dei controlli
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
			/*
			 * se c'è la candidatura e provenienza è SIL : 1) se azienda ed è proprietaria
			 * della vacancy quindi idpfprincipal == idpfprincipal azienda può visualizzare
			 * e modificare 2) se azienda non è proprietaria ma il idpfprincipalpalese =
			 * idpfprincipal and cod evasione è DFD potrà visualizzare e modificare 3) se
			 * CPI quindi idpfprincipalpalese <> idpfprincipal and idpfprincipal ==
			 * idpfprincipal (titolare) and cod evasione è <> DFD potrà visualizzare e
			 * modificare 4) se CPI quindi idpfprincipalpalese <> idpfprincipal and
			 * idpfprincipal == idpfprincipal (titolare) and cod evasione è == DFD potrà
			 * visualizzare e e non modificare
			 */
		} else {
			check = true;
		}
		}else check=true; // caso autocandidature
		return check;
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
	
	public int checkTotalNumberOrderedVacancyToCandidate(Integer vacancyId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		if(listcandidature != null && !listcandidature.isEmpty()) {
			i= listcandidature.size();
		}
		return i;
	}
	public String getIntestatario() {
		
		boolean isFromSIL = cvDatiPersonali.getProvenienzaCurriculum().getCodProvenienza().contains("SIL");
		Integer idPrinc = null;
		PfPrincipal pfPrinc = null;
		if (isFromSIL && cvDatiPersonali.getPfPrincipalPalese() != null
				&& cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal() != null) {
			idPrinc = cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal();
		} else {
			idPrinc = cvDatiPersonali.getPfPrincipal().getIdPfPrincipal();
		}
		pfPrinc = pfPrincipalHome.findById(idPrinc);
		return pfPrinc.getNome().substring(0, 1).toUpperCase() + pfPrinc.getNome().substring(1) + " "
				+ pfPrinc.getCognome().substring(0, 1).toUpperCase() + pfPrinc.getCognome().substring(1);

	}

	public AcCandidatura getLivelliValutazione() {
		return livelliValutazione;
	}

	public void setLivelliValutazione(AcCandidatura livelliValutazione) {
		this.livelliValutazione = livelliValutazione;
	}

	public void syncValutazione() {
		Integer idPfPrinc = getSession().getConnectedAzienda().getId();
		cvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);

		log.info("Update Livelli di Valutazione getCodiceCandidatura: "
				+ livelliValutazione.getDeIdoneitaCandidatura().getCodIdoneitaCandidatura());
		acCandidaturaHome.merge(livelliValutazione, idPfPrinc);
		codIdoneitaCandidatura = livelliValutazione.getDeIdoneitaCandidatura().getCodIdoneitaCandidatura();
		livelliValutazione.setDeIdoneitaCandidatura(deIdoneitaCandidaturaHome.findById(codIdoneitaCandidatura));
		log.info(
				"Update Livelli di Valutazione da Azienda - idCandidatura: " + livelliValutazione.getIdAcCandidatura());
	}

	private void loadAllegati() {
		allegatoSelezionato = cvAllegatoHome.findEntityByCurriculumId(curriculumId);
		if (allegatoSelezionato != null) {
			checkAllegatoSelezionato = true;
		}
	}

	private void loadEsperienze() {
		esperienzeSelezionate = cvEsperienzeProfHome.findProperByCurriculumId(curriculumId);

	}

	private void loadAbilitazioni() {
		List<CvPatente> cvPat = cvPatenteHome.findProperByCurriculumId(curriculumId);
		for (CvPatente cvPatente : cvPat) {
			patentiSelezionate.add(cvPatente.getDePatenteSil());
		}
		patentiniSelezionati = new ArrayList<>();
		List<CvPatentino> cvPati = cvPatentinoHome.findProperByCurriculumId(curriculumId);
		for (CvPatentino cvPatentino : cvPati) {
			patentiniSelezionati.add(cvPatentino.getDePatentinoSil());
		}

		albiSelezionati = new ArrayList<>();
		List<CvAlbo> cvAlbii = cvAlboHome.findProperByCurriculumId(curriculumId);
		for (CvAlbo cvPatentino : cvAlbii) {
			albiSelezionati.add(cvPatentino.getDeAlboSil());
		}
	}

	private void loadIstruzioni() {
		cvIstruzioniSelezionate = cvIstruzioneHome.findProperByCurriculumId(curriculumId);
	}

	private void loadProfessioni() {
		cvProfessioniSelezionate = cvProfDesiderateHome.findProperByCurriculumId(curriculumId);
	}

	private void loadFormazioni() {
		cvFormazioniSelezionate = cvFormazioneHome.findProperByCurriculumId(curriculumId);
	}

	private void loadAltreInfo() {
		cvAltreInfo = cvAltreInfoHome.findById(curriculumId);
	}

	private void loadCompetenzeTrasversali() {
		List<CvCompetenzeTrasv> cvCompetenzeTrasvS = new ArrayList<CvCompetenzeTrasv>();
		cvCompetenzeTrasvS = cvCompetenzeTrasvHome.findProperByCurriculumId(curriculumId);
		if (!cvCompetenzeTrasvS.isEmpty()) {
			cvCompetenzeTrasv = cvCompetenzeTrasvS.get(0);
		}
	}

	private void loadInformatica() {
		cvInformatica = cvInformaticaHome.findById(curriculumId);
	}

	private void loadLingue() {

		lingueSelezionate = cvLinguaHome.findLinguaByCurriculumId(curriculumId);
	}

	public StreamedContent downloadAllegatoFile() {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(allegatoSelezionato.getContenuto());
			String ext = FilenameUtils.getExtension(allegatoSelezionato.getFilename());
			return new DefaultStreamedContent(is, ext, allegatoSelezionato.getFilename());
		} catch (NullPointerException e) {
			log.error("Errore durante downloadAllegatoFile : " + e.toString());
			return null;
		}
	}

	public boolean isModificaRendered() {
		// #8408
		boolean isProvincia = session.isProvincia();
		boolean flgInvio = !cvDatiPersonali.getFlagInviato();
		// errore il proprietario lo si confronta con id_pf_principal
		//boolean isProprietario = getSession().getPrincipalId().equals(cvDatiPersonali.getPfPrincipalIns().getIdPfPrincipal());
		boolean isProprietario = getSession().getPrincipalId().equals(cvDatiPersonali.getPfPrincipal().getIdPfPrincipal());
		// valutare se lasciare il controllo sul palese !!!
		//boolean isProprietarioPalese = cvDatiPersonali.getPfPrincipalPalese() != null ? getSession().getPrincipalId().equals(cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal()) : false ;
		//boolean isFromSIL = cvDatiPersonali.getProvenienzaCurriculum().getCodProvenienza().equals("SIL");
		boolean isFromSIL = cvDatiPersonali.getProvenienzaCurriculum().getCodProvenienza().contains("SIL");
		//return flgInvio && (isProprietario || isProprietarioPalese) && !isFromSIL;
		return !isProvincia && flgInvio && (isProprietario) && !isFromSIL;
	}

	public String redirectToView() {
		return ConstantsSingleton.EDIT_PF_CV_IDO + "?faces-redirect=true&id=" + cvDatiPersonali.getIdCvDatiPersonali();
	}

	public DecodificheBean getDecodificheBean() {
		return decodificheBean;
	}

	public void setDecodificheBean(DecodificheBean decodificheBean) {
		this.decodificheBean = decodificheBean;
	}

	public CvDatiPersonali getCvDatiPersonali() {
		return cvDatiPersonali;
	}

	public void setCvDatiPersonali(CvDatiPersonali cvDatiPersonali) {
		this.cvDatiPersonali = cvDatiPersonali;
	}

	public CvAltreInfo getCvAltreInfo() {
		return cvAltreInfo;
	}

	public void setCvAltreInfo(CvAltreInfo cvAltreInfo) {
		this.cvAltreInfo = cvAltreInfo;
	}

	public CvInformatica getCvInformatica() {
		return cvInformatica;
	}

	public void setCvInformatica(CvInformatica cvInformatica) {
		this.cvInformatica = cvInformatica;
	}

	public CvCompetenzeTrasv getCvCompetenzeTrasv() {
		return cvCompetenzeTrasv;
	}

	public void setCvCompetenzeTrasv(CvCompetenzeTrasv cvCompetenzeTrasv) {
		this.cvCompetenzeTrasv = cvCompetenzeTrasv;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
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

	public List<CvIstruzione> getCvIstruzioniSelezionate() {
		return cvIstruzioniSelezionate;
	}

	public void setCvIstruzioniSelezionate(List<CvIstruzione> cvIstruzioniSelezionate) {
		this.cvIstruzioniSelezionate = cvIstruzioniSelezionate;
	}

	public List<CvLingua> getLingueSelezionate() {
		return lingueSelezionate;
	}

	public void setLingueSelezionate(List<CvLingua> lingueSelezionate) {
		this.lingueSelezionate = lingueSelezionate;
	}

	public List<DeGradoLin> getGradoLingueList() {
		return gradoLingueList;
	}

	public void setGradoLingueList(List<DeGradoLin> gradoLingueList) {
		this.gradoLingueList = gradoLingueList;
	}

	public List<CvFormazione> getCvFormazioniSelezionate() {
		return cvFormazioniSelezionate;
	}

	public void setCvFormazioniSelezionate(List<CvFormazione> cvFormazioniSelezionate) {
		this.cvFormazioniSelezionate = cvFormazioniSelezionate;
	}

	public List<CvProfDesiderate> getCvProfessioniSelezionate() {
		return cvProfessioniSelezionate;
	}

	public void setCvProfessioniSelezionate(List<CvProfDesiderate> cvProfessioniSelezionate) {
		this.cvProfessioniSelezionate = cvProfessioniSelezionate;
	}

	public List<CvEsperienzeProf> getEsperienzeSelezionate() {
		return esperienzeSelezionate;
	}

	public void setEsperienzeSelezionate(List<CvEsperienzeProf> esperienzeSelezionate) {
		this.esperienzeSelezionate = esperienzeSelezionate;
	}

	public boolean getCheckAllegatoSelezionato() {
		return checkAllegatoSelezionato;
	}

	public void setCheckAllegatoSelezionato(boolean checkAllegatoSelezionato) {
		this.checkAllegatoSelezionato = checkAllegatoSelezionato;
	}

	public CvAllegato getAllegatoSelezionato() {
		return allegatoSelezionato;
	}

	public void setAllegatoSelezionato(CvAllegato allegatoSelezionato) {
		this.allegatoSelezionato = allegatoSelezionato;
	}

	public String getNotaValutazione() {
		return notaValutazione;
	}

	public void setNotaValutazione(String notaValutazione) {
		this.notaValutazione = notaValutazione;
	}

	public String fillLinguaLevel(ConstantsSingleton.LivelloLinguaEnum toBeFilled,
			ConstantsSingleton.LivelloLinguaEnum actualLevel) {
		String fillClass = "fill";

		if (actualLevel == null)
			return "";

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.MEDIO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)) {
			return fillClass;
		}

		if (actualLevel.equals(ConstantsSingleton.LivelloLinguaEnum.SCOLASTICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.ECCELLENTE)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.TECNICO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.OTTIMO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.BUONO)
				&& !toBeFilled.equals(ConstantsSingleton.LivelloLinguaEnum.MEDIO)) {
			return fillClass;
		}

		return "";
	}


	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}	

	public DeComune getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(DeComune comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public DeComune getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(DeComune comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public int getNumTotalCandidature() {
		return numTotalCandidature;
	}

	public void setNumTotalCandidature(int numTotalCandidature) {
		this.numTotalCandidature = numTotalCandidature;
	}

	public int getNumCandidature() {
		return numCandidature;
	}

	public void setNumCandidature(int numCandidature) {
		this.numCandidature = numCandidature;
	}

	public VaDatiVacancy getVaVacancy() {
		return vaVacancy;
	}

	public void setVaVacancy(VaDatiVacancy vaVacancy) {
		this.vaVacancy = vaVacancy;
	}
	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public void sendEmail() {
		
		//if (hasCandidatura) { // non è richiesto la verifica se per il CV c'è una candidatura
		/*  codice commentato perchè la gestione dell contatta lavoratore è diversa rispetto al contatta lavoratore presente nella lista delle candidature ---- in quel caso è un invio massivo di mail verso le candidature selezionate
		    RequestContext.getCurrentInstance().addCallbackParam("errore", false);
			CvDatiPersonali cv = cvDatiPersonaliHome.findById(cvDatiPersonali.getIdCvDatiPersonali());
			inviaMessaggio(cv);
		*/	
			try {
				primoContattoEjb.sendPrimoContatto(primoContatto);
				addInfoMessage("messaggio.primo_contatto_ok");
			} catch (EJBException e) {
				gestisciErrore(e, "generic.error");
			}
			
		//}
	}

	/**
	 * Invia la segnalazione di una candidatura all'email di un amico.
	 *
	 * @param cv
	 */
	private void inviaMessaggio(CvDatiPersonali cv) {

		// Se non inviata eseguo la procedura di invio email e salvataggio
		EmailDTO emailDTO = EmailDTO.buildContattaCandidatoEmail(cv.getEmail(), messageTitle, messageBody);
		//EmailDTO emailDTO = EmailDTO.buildContattaCandidatoEmail("simone.dellorto@eng.it", messageTitle, messageBody);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);
		PfPrincipal princ = pfPrincipalHome.findById(session.getPrincipalId());
		PfPrincipal princDest = pfPrincipalHome.findById(cv.getPfPrincipal().getIdPfPrincipal());
		Date now = new Date();
		try {
			// crea il messaggio di primo contatto
			MsgMessaggio msgPrimoContatto = new MsgMessaggio();
			msgPrimoContatto
					.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO));
			msgPrimoContatto.setOggetto(messageTitle);
			msgPrimoContatto.setPfPrincipalFrom(princ);
			msgPrimoContatto.setPfPrincipalTo(princDest);
			msgPrimoContatto.setCorpo(messageBody);
			msgPrimoContatto.setDtmIns(now);
			msgPrimoContatto.setDtmMod(now);
			msgPrimoContatto.setPfPrincipalIns(princ);
			msgPrimoContatto.setPfPrincipalMod(princ);
			msgMessaggioHome.persist(msgPrimoContatto);
			msgPrimoContatto.setTicket("" + msgPrimoContatto.getIdMsgMessaggio());
			msgMessaggioHome.merge(msgPrimoContatto);
		} catch (Exception e) {
			log.error("Errore in inserimento VaContatto in seguito a invio mail risposta candidatura per vacancyId:"
					+ vaVacancy.getIdVaDatiVacancy());
		}

		log.info("- Mail candidature inviata: {\n" + emailDTO.toString() + "}");
		// FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MAIL_INVIATA));
	}


	
	public PrimoContattoDTO getPrimoContatto() {
		return primoContatto;
	}


	public void setPrimoContatto(PrimoContattoDTO primoContatto) {
		this.primoContatto = primoContatto;
	}

	
	public boolean isCompetenzeTrasversaliRendered() {

		if(cvCompetenzeTrasv == null){

			return false;
		}else
			return (
					cvCompetenzeTrasv.getCapCompRelInterpersonali() != null && !"".equals(cvCompetenzeTrasv.getCapCompRelInterpersonali())
							|| cvCompetenzeTrasv.getCapCompOrganizzative() != null && !"".equals(cvCompetenzeTrasv.getCapCompOrganizzative())
							|| cvCompetenzeTrasv.getCapCompTecniche() != null && !"".equals(cvCompetenzeTrasv.getCapCompTecniche())
							|| cvCompetenzeTrasv.getCapCompAltre() != null && !"".equals(cvCompetenzeTrasv.getCapCompAltre())
			);

	}

}
