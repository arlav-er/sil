package it.eng.myportal.beans.curriculum.pf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.util.Base64;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.DecodificheBean;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.decodifiche.DeAreaFormazione;
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
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeAbilitazioneGenHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaFormazioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "curriculumVitaePfBean")
@ViewScoped
public class CurriculumVitaePfBean extends AbstractBaseBean {

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(CurriculumVitaePfBean.class);

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeAlboHome deAlboHome;

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

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@ManagedProperty(value = "#{decodificheBean}")
	protected DecodificheBean decodificheBean;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	// testata
	private CvDatiPrincipaliPfSection cvDatiPrincipaliPfSection;

	private CvDatiPersonali cvDatiPersonali;

	// sezioni
	private CvAbilitazioniPfSection cvAbilitazioniPfSection;
	private CvAltreInfoPfSection cvAltreInfoPfSection;
	private CvCompetenzeTrasversaliPfSection cvCompetenzeTrasversaliPfSection;
	private CvIstruzionePfSection cvIstruzionePfSection;
	private CvFormazioneProfPfSection cvFormazioneProfPfSection;
	private CvLinguePfSection cvLinguePfSection;
	private CvEsperienzeProfSection cvEsperienzeProfSection;
	private CvProfessionePfSection cvProfessionePfSection;
	private CvCompetenzeDigitaliPfSection cvCompetenzeDigitaliPfSection;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	private CvAllegatoHome cvAllegatoHome;

	@EJB
	private CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	private CvIstruzioneHome cvIstruzioneHome;

	@EJB
	private CvProfDesiderateHome cvProfessioneHome;

	@EJB
	private CvFormazioneHome cvFormazioneHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private CvLinguaHome cvLinguaHome;

	@EJB
	private DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	private DeModalitaLinguaHome deModalitaLinguaHome;

	@EJB
	private CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	private CvAltreInfoHome cvAltreInfoHome;

	@EJB
	private DeAbilitazioneGenHome deAbilitazioneGenHome;

	@EJB
	private DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	private DeTrasfertaHome deTrasfertaHome;

	@EJB
	private DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	private CvInformaticaHome cvInformaticaHome;

	@EJB
	private DeProvenienzaHome deProvenienzaHome;

	private String currentSection;
	private final String DATI_PRINCIPALI = "dati_principali";
	private final String ESPERIENZE = "esperienze";
	private final String ISTRUZIONI = "istruzioni";
	private final String FORMAZIONE = "formazione";
	private final String LINGUE = "lingue";
	private final String COMPETENZE_DIGITALI = "competenzeDigitali";
	private final String ABILITAZIONI = "abilitazioni";
	private final String COMPETENZE_TRAVERSALI = "competenzeTrasversali";
	private final String ALTRE_INFO = "altreInfo";
	private final String PROFFESIONI_DESIDERATE = "professioni";

	// DA SPOSTARE
	private int curriculumId;
	private long numVacancyCompatibili;

	/**
	 * id curriculum recuperato dalla sessione in caso di 'torna indietro'
	 */
	private String curriculumIdStr;

	private String tempCurriculumNameToCopy;

	private UtenteCompletoDTO utenteCompletoDTO;
	private CvAllegato cvAllegato;

	@EJB
	private DeAreaFormazioneHome deAreaFormazioneHome;

	private int completionPercent;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (map.get("id") != null) {
			curriculumId = Integer.parseInt(StringUtils.defaultString(map.get("id"), curriculumIdStr));
			putParamsIntoSession();
			cvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);
			if(cvDatiPersonali == null) {
				log.error("Errore durante il caricamento del Curriculum Vitae.[curriculumId]: " + curriculumId);
				redirectGrave("curriculum.error_loading");
				throw new MyPortalException("curriculum.error_loading");
			}
			boolean isAziendaSession = getSession().isAzienda();
			if (!isAziendaSession) {

				if (!cvDatiPersonali.getPfPrincipal().getIdPfPrincipal().equals(session.getPrincipalId())) {

					log.error("Violazione di sicurezza per manipolazione di parametri [curriculumId]");
					log.error("Utente connesso: " + session.getPrincipalId());
					log.error("Proprietario CV:" + cvDatiPersonali.getPfPrincipalIns().getIdPfPrincipal());
					// addErrorMessage("user.is_not");
					redirectGrave("generic.manipulation_error");
					throw new MyPortalException("user.is_not");
				}
			} else {
				// un'azienda che fa edit CV? mmm
				log.error("Violazione di sicurezza per manipolazione di parametri [curriculumId]: " + curriculumId);
				redirectGrave("generic.manipulation_error");
				throw new MyPortalException("user.is_not");
			}

		} else {
			initEmptyCV();
		}

		cvDatiPrincipaliPfSection = new CvDatiPrincipaliPfSection(this, deComuneHome, deAmbitoDiffusioneHome,
				cvAllegatoHome, cvAltreInfoHome);
		cvEsperienzeProfSection = new CvEsperienzeProfSection(this, cvEsperienzeProfHome, deMansioneHome);
		cvIstruzionePfSection = new CvIstruzionePfSection(this, cvIstruzioneHome);
		cvFormazioneProfPfSection = new CvFormazioneProfPfSection(this, cvFormazioneHome);
		cvLinguePfSection = new CvLinguePfSection(this, cvLinguaHome, deGradoLinSilHome, deModalitaLinguaHome);
		cvCompetenzeDigitaliPfSection = new CvCompetenzeDigitaliPfSection(this, cvInformaticaHome);
		cvAbilitazioniPfSection = new CvAbilitazioniPfSection(this, cvPatenteHome, cvPatentinoHome, cvAlboHome);
		cvCompetenzeTrasversaliPfSection = new CvCompetenzeTrasversaliPfSection(this, cvCompetenzeTrasvHome);
		cvAltreInfoPfSection = new CvAltreInfoPfSection(this, cvAltreInfoHome, deAbilitazioneGenHome,
				deAgevolazioneHome, deTrasfertaHome, deSezioneInfoHome);
		cvProfessionePfSection = new CvProfessionePfSection(this, cvProfessioneHome);
		calcCompletionPercentage();
		log.debug("Costruito il Bean PF per il CurriculuVitae!");
	}

	private void initEmptyCV() {
		cvDatiPersonali = new CvDatiPersonali();
		cvDatiPersonali.setPfPrincipal(pfPrincipalHome.findById(session.getPrincipalId()));
		cvDatiPersonali.setFlagInviato(false);
		cvDatiPersonali.setFlagIdo(true);
		cvDatiPersonali.setDeAmbitoDiffusione(
				deAmbitoDiffusioneHome.findById(ConstantsSingleton.DeAmbitoDiffusione.REGIONALE));
		cvDatiPersonali
				.setProvenienzaCurriculum(deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
		cvDatiPersonali.setFlagEliminato(false);
		cvDatiPersonali.setOpzTipoDecodifiche(CvDatiPersonali.OpzTipoDecodifiche.SIL);
	}

	public String copyCurriculum() {
		List<CvDatiPersonaliDTO> curriculaLive = utenteInfoHome.getAllCurriculaLive(utenteCompletoDTO.getId());
		if (curriculaLive.size() < ConstantsSingleton.CVMAXCURR) {
			try {
				Map<String, String> map = getRequestParameterMap();
				Integer copyId = cvDatiPersonali.getIdCvDatiPersonali();
				String copyDescrizione = tempCurriculumNameToCopy;
				// TODO get these 2

				if (copyId != null && copyDescrizione != null) {
					CvDatiPersonali cvDatiPersonaliCopia;
					if (cvDatiPersonali.getFlagIdo()) {
						cvDatiPersonaliCopia = cvDatiPersonaliHome.copyByIdNoDTO(getSession().getPrincipalId(), copyId,
								copyDescrizione, false);
					} else {
						cvDatiPersonaliCopia = cvDatiPersonaliHome.copyLightNoDTO(getSession().getPrincipalId(), copyId,
								copyDescrizione, false);
					}
					updateDataScadenza(cvDatiPersonaliCopia);
					return ConstantsSingleton.EDIT_PF_CV_IDO + "?faces-redirect=true" + "&" + "id="
							+ cvDatiPersonaliCopia.getIdCvDatiPersonali();
				} else {
					log.error("ERRORE COPIA CV: mancano dati!");
					cvDatiPrincipaliPfSection.addAlertErrorMessage("Errore Copia",
							"Mancano dei dati fondamentali per la copia di questo CV");
				}
			} catch (EJBException e) {
				gestisciErrore(e, "data.error.copying");
			}
		} else {
			cvDatiPrincipaliPfSection.addAlertErrorMessage("Errore Copia",
					"Hai gia` 5 CV, non e` possibile aggiungerne ancora");
		}
		return "#";
	}

	public boolean isReferenziatoInMsg() {
		return cvDatiPersonali.getMsgMessaggios().size() > 0;
	}

	public String deleteCurriculum() {
		cvDatiPersonaliHome.deleteCurriculum(cvDatiPersonali.getIdCvDatiPersonali(), getSession().getPrincipalId());
		return ConstantsSingleton.HOME_UTENTE + "?faces-redirect=true";
	}

	/**
	 * Main navigation - xhtml path passed from here
	 */
	public void updateSection() {
		try {
			String tempSection = new String(Base64.decodeFast(getRequestParameter("currentSection")), "UTF-8");
			currentSection = tempSection;

			prepareSection(tempSection);
		} catch (UnsupportedEncodingException e) {
			log.error("CODICE: 02. Application is broken, porka Vacca");
		}
	}

	private void prepareSection(String section) {
		if (section.contains(DATI_PRINCIPALI)) {
			cvDatiPrincipaliPfSection.initSection();
		} else if (section.contains(ESPERIENZE)) {
			cvEsperienzeProfSection.initSection();
		} else if (section.contains(ISTRUZIONI)) {
			cvIstruzionePfSection.initSection();
		} else if (section.contains(FORMAZIONE)) {
			cvFormazioneProfPfSection.initSection();
		} else if (section.contains(LINGUE)) {
			cvLinguePfSection.initSection();
		} else if (section.contains(COMPETENZE_DIGITALI)) {
			cvCompetenzeDigitaliPfSection.initSection();
		} else if (section.contains(ABILITAZIONI)) {
			cvAbilitazioniPfSection.initSection();
		} else if (section.contains(COMPETENZE_TRAVERSALI)) {
			cvCompetenzeTrasversaliPfSection.initSection();
		} else if (section.contains(ALTRE_INFO)) {
			cvAltreInfoPfSection.initSection();
		} else if (section.contains(PROFFESIONI_DESIDERATE)) {
			cvProfessionePfSection.initSection();
		}
	}

	public void checkProgressValue() {
		RequestContext.getCurrentInstance().addCallbackParam("progressValue", completionPercent);
	}

	public List<DeAreaFormazione> getAreeValide() {
		return deAreaFormazioneHome.findAllValide();
	}

	// :TODO
	public String viewUrl() {
		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		return "";
	}

	// TODO: togliere. Si usano quelle e bona
	public boolean usaDecodificheSil() {
		return true;
	}

	public void handleProfilePicUpload(FileUploadEvent event) {
		try {
			UploadedFile file = event.getFile();
			cvDatiPersonali.setFoto(file.getContents());
			log.debug("Photo uploaded: " + file.getFileName());
			cvDatiPersonaliHome.merge(cvDatiPersonali);
		} catch (Exception e) {
			// TODO: Foto upload KO
		}
		// TODO: Foto upload OK
	}

	public void removeProfilePic() {
		cvDatiPersonali.setFoto(null);
		try {
			cvDatiPersonaliHome.merge(cvDatiPersonali);
		} catch (Exception e) {
			// TODO: Delete foto KO
		}
		// TODO: Delete foto OK
	}

	protected void calcCompletionPercentage() {
		completionPercent = cvDatiPersonaliHome.getCompletedPercentage(cvDatiPersonali);
	}

	public void updateDataScadenza(CvDatiPersonali cvDatiPersonali) {
		Date now = new Date();

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 60));
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 180));
		} else {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 30));
		}

		cvDatiPersonaliHome.merge(cvDatiPersonali, utenteCompletoDTO.getIdUtente());
	}

	public CvAbilitazioniPfSection getCvAbilitazioniPfSection() {
		return cvAbilitazioniPfSection;
	}

	public void setCvAbilitazioniPfSection(CvAbilitazioniPfSection cvAbilitazioniPfSection) {
		this.cvAbilitazioniPfSection = cvAbilitazioniPfSection;
	}

	public CvAltreInfoPfSection getCvAltreInfoPfSection() {
		return cvAltreInfoPfSection;
	}

	public void setCvAltreInfoPfSection(CvAltreInfoPfSection cvAltreInformazioniPfSection) {
		this.cvAltreInfoPfSection = cvAltreInformazioniPfSection;
	}

	public CvDatiPrincipaliPfSection getCvDatiPrincipaliPfSection() {
		return cvDatiPrincipaliPfSection;
	}

	public void setCvDatiPrincipaliSection(CvDatiPrincipaliPfSection cvDatiPrincipaliPfSection) {
		this.cvDatiPrincipaliPfSection = cvDatiPrincipaliPfSection;
	}

	public CvCompetenzeTrasversaliPfSection getCvCompetenzeTrasversaliPfSection() {
		return cvCompetenzeTrasversaliPfSection;
	}

	public void setCvCompetenzeTrasversaliPfSection(CvCompetenzeTrasversaliPfSection cvCompetenzeTrasversaliPfSection) {
		this.cvCompetenzeTrasversaliPfSection = cvCompetenzeTrasversaliPfSection;
	}

	public CvLinguePfSection getCvLinguePfSection() {
		return cvLinguePfSection;
	}

	public void setCvLinguePfSection(CvLinguePfSection cvLinguePfSection) {
		this.cvLinguePfSection = cvLinguePfSection;
	}

	private boolean skip;

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public CvDatiPersonaliHome getCvDatiPersonaliHome() {
		return cvDatiPersonaliHome;
	}

	public void setCvDatiPersonaliHome(CvDatiPersonaliHome cvDatiPersonaliHome) {
		this.cvDatiPersonaliHome = cvDatiPersonaliHome;
	}

	public UtenteCompletoDTO getUtenteCompletoDTO() {
		return utenteCompletoDTO;
	}

	public void setUtenteCompletoDTO(UtenteCompletoDTO utenteCompletoDTO) {
		this.utenteCompletoDTO = utenteCompletoDTO;
	}

	public CvIstruzionePfSection getCvIstruzionePfSection() {
		return cvIstruzionePfSection;
	}

	public void setCvIstruzionePfSection(CvIstruzionePfSection cvIstruzionePfSection) {
		this.cvIstruzionePfSection = cvIstruzionePfSection;
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

	public CvEsperienzeProfSection getCvEsperienzeProfSection() {
		return cvEsperienzeProfSection;
	}

	public void setCvEsperienzeProfSection(CvEsperienzeProfSection cvEsperienzeProfSection) {
		this.cvEsperienzeProfSection = cvEsperienzeProfSection;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}

	public String getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}

	public CvFormazioneProfPfSection getCvFormazioneProfPfSection() {
		return cvFormazioneProfPfSection;
	}

	public void setCvFormazioneProfPfSection(CvFormazioneProfPfSection cvFormazionePfSection) {
		this.cvFormazioneProfPfSection = cvFormazionePfSection;
	}

	public CvProfessionePfSection getCvProfessionePfSection() {
		return cvProfessionePfSection;
	}

	public void setCvProfessionePfSection(CvProfessionePfSection cvProfessionePfSection) {
		this.cvProfessionePfSection = cvProfessionePfSection;
	}

	public CvCompetenzeDigitaliPfSection getCvCompetenzeDigitaliPfSection() {
		return cvCompetenzeDigitaliPfSection;
	}

	public void setCvCompetenzeDigitaliPfSection(CvCompetenzeDigitaliPfSection cvCompetenzeDigitaliPfSection) {
		this.cvCompetenzeDigitaliPfSection = cvCompetenzeDigitaliPfSection;
	}

	public CvAllegato getCvAllegato() {
		return cvAllegato;
	}

	public void setCvAllegato(CvAllegato cvAllegato) {
		this.cvAllegato = cvAllegato;
	}

	public CvAllegatoHome getCvAllegatoHome() {
		return cvAllegatoHome;
	}

	public void setCvAllegatoHome(CvAllegatoHome cvAllegatoHome) {
		this.cvAllegatoHome = cvAllegatoHome;
	}

	public StreamedContent getProfilePic() throws IOException {
		// FacesContext context = FacesContext.getCurrentInstance();
		// if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
		// // So, we're rendering the HTML. Return a stub StreamedContent so that it
		// will generate right URL.
		// return new DefaultStreamedContent();
		// }
		// else {
		return new DefaultStreamedContent(new ByteArrayInputStream(cvDatiPersonali.getFoto()));
		// }
	}

	public String getTempCurriculumNameToCopy() {
		return tempCurriculumNameToCopy;
	}

	public void setTempCurriculumNameToCopy(String tempCurriculumNameToCopy) {
		this.tempCurriculumNameToCopy = tempCurriculumNameToCopy;
	}

}
