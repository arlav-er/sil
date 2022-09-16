package it.eng.myportal.beans.vacancies.pf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Base64;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.DecodificheBean;
import it.eng.myportal.beans.session.MansioniSessionBean;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.dtos.VaRapportoDiLavoroDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.ejb.DeBpMansioneTreeEJB;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaAlboHome;
import it.eng.myportal.entity.home.VaAltreInfoHome;
import it.eng.myportal.entity.home.VaCompetenzeTrasvHome;
import it.eng.myportal.entity.home.VaContattoHome;
import it.eng.myportal.entity.home.VaContrattoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaEsperienzeHome;
import it.eng.myportal.entity.home.VaInformaticaHome;
import it.eng.myportal.entity.home.VaIstruzioneHome;
import it.eng.myportal.entity.home.VaLinguaHome;
import it.eng.myportal.entity.home.VaOrarioHome;
import it.eng.myportal.entity.home.VaPatenteHome;
import it.eng.myportal.entity.home.VaPatentinoHome;
import it.eng.myportal.entity.home.VaPubblicazioneHome;
import it.eng.myportal.entity.home.VaRapportoDiLavoroHome;
import it.eng.myportal.entity.home.VaRetribuzioneHome;
import it.eng.myportal.entity.home.VaTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.base.utils.DateUtils;

@ManagedBean(name = "vacancyFormPfBean")
@ViewScoped
public class VacancyFormPfBean extends AbstractBaseBean {
	private static final CharSequence DATI_PRINCIPALI = "datiGenerali";
	private static final CharSequence CODIZIONI_PROPOSTE = "condizioniProposte";
	private static final CharSequence ISTRUZIONI = "istruzioni";
	private static final CharSequence LINGUE = "lingue";
	private static final CharSequence ABILITAZIONI = "abilitazioni";
	private static final CharSequence COMP_TRASV = "competenzeTrasversali";
	private static final CharSequence COMP_DIGITALI = "competenzeDigitali";
	private static final CharSequence CONTATTI = "contatto";
	private static final CharSequence ALTRE_INFO = "altreInfo";

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;
	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	protected VaAltreInfoHome vaAltreInfoHome;
	@EJB
	protected VaRetribuzioneHome vaRetribuzioneHome;
	@EJB
	protected VaOrarioHome vaOrarioHome;
	@EJB
	protected VaIstruzioneHome vaIstruzioneHome;
	@EJB
	protected VaInformaticaHome vaInformaticaHome;
	@EJB
	protected VaCompetenzeTrasvHome vaCompetenzeTrasvHome;
	@EJB
	protected VaRapportoDiLavoroHome vaRapportoDiLavoroHome;
	@EJB
	protected VaContrattoHome vaContrattoHome;
	@EJB
	protected VaTurnoHome vaTurnoHome;
	@EJB
	protected VaLinguaHome valinguaHome;
	@EJB
	protected PfPrincipalHome pfPrincipalHome;
	@EJB
	protected DeGradoLinHome deGradoLinHome;
	@EJB
	protected DeRetribuzioneHome deRetribuzioneHome;
	@EJB
	protected DeModalitaLinguaHome deModalitaLinguaHome;
	@EJB
	protected VaPatenteHome vaPatenteHome;

	@EJB
	VaAlboHome vaAlboHome;

	@EJB
	VaPatentinoHome vaPatentinoHome;

	@EJB
	protected VaContattoHome vaContattoHome;

	@EJB
	protected VaPubblicazioneHome vaPubblicazioneHome;

	@EJB
	protected VaEsperienzeHome vaEsperienzeHome;

	@EJB
	protected DeTrasfertaHome deTrasfertaHome;

	@EJB
	private DeMansioneHome deMansioneHome;

	@ManagedProperty(value = "#{decodificheBean}")
	protected DecodificheBean decodificheBean;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	@EJB
	private AcCandidaturaHome acCandidaturaHome;

	protected static Log log = LogFactory.getLog(VacancyFormPfBean.class);

	private String currentSection;
	protected VaDatiVacancy vaDatiVacancy;
	private AziendaInfo utenteCompletoDTO;
	private String tempVacancyToCopy;
	// SECTIONS
	VaDatiGeneraliSection vaDatiGeneraliSection;
	VaCondizioniProposteSection vaCondizioniProposteSection;
	VaIstruzioniSection vaIstruzioniSection;
	VaLingueSection vaLingueSection;
	VaAbilitazioniSection vaAbilitazioniSection;
	private VaCompetenzeTrasversaliPfSection vaCompetenzeTrasversaliPfSection;
	private VaCompetenzeDigitaliPfSection vaCompetenzeDigitaliPfSection;
	VaContattiSection vaContattiSection;
	VaAltreInfoSection vaAltreInfoSection;

	private Map<Integer, List<DeBpMansione>> deMansioneMap;
	private boolean checkAgricolo;
	@EJB
	DeBpMansioneTreeEJB deBpMansioneTreeEJB;
	private TreeNode deMansioneTreeNode;

	@ManagedProperty(value = "#{mansioniSessionBean}")
	private MansioniSessionBean mansioniSessionBean;

	private boolean checkModifyPubbWithHasCandid;
	private String motivazione;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		utenteCompletoDTO = aziendaInfoHome.findById(session.getPrincipalId());

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (map.get("id") != null) {
			Integer vId = Integer.parseInt(map.get("id"));
			putParamsIntoSession();
			vaDatiVacancy = vaDatiVacancyHome.findById(vId);

			boolean isAziendaSession = getSession().isAzienda();
			if (isAziendaSession) {
				if (!vaDatiVacancy.getPfPrincipal().getIdPfPrincipal().equals(session.getPrincipalId())) {
					log.error("Violazione di sicurezza per manipolazione di parametri");
					log.error("Utente connesso: " + session.getPrincipalId());
					log.error("Proprietario Vacancy:" + vaDatiVacancy.getPfPrincipalIns().getIdPfPrincipal());
					redirectGrave("generic.manipulation_error");
					throw new MyPortalException("user.is_not");
				}
			} else {
				// un cittadino che fa edit vacancy? mmm
				log.error("Violazione di sicurezza per manipolazione di parametri [VacancyId]: "
						+ vaDatiVacancy.getIdVaDatiVacancy());
				redirectGrave("generic.manipulation_error");
				throw new MyPortalException("user.is_not");
			}

		} else {
			// gestione provenienza e richiesta

			vaDatiVacancy = vaDatiVacancyHome.createDefaultVacancy();
			PfPrincipal aziendaInfoUtenteSessione = pfPrincipalHome.findById(session.getPrincipalId());
			vaDatiVacancy.setPfPrincipal(aziendaInfoUtenteSessione);
		}
		checkModifyPubbWithHasCandid = this.isVacancyPubbWithCandidatura();

		vaDatiGeneraliSection = new VaDatiGeneraliSection(this);
		vaCondizioniProposteSection = new VaCondizioniProposteSection(this);
		vaIstruzioniSection = new VaIstruzioniSection(this);
		vaLingueSection = new VaLingueSection(this, valinguaHome, deGradoLinHome, deModalitaLinguaHome);
		vaAbilitazioniSection = new VaAbilitazioniSection(this, vaPatenteHome, vaPatentinoHome, vaAlboHome);
		vaCompetenzeTrasversaliPfSection = new VaCompetenzeTrasversaliPfSection(this);
		vaCompetenzeDigitaliPfSection = new VaCompetenzeDigitaliPfSection(this);
		vaContattiSection = new VaContattiSection(this, vaContattoHome);
		vaAltreInfoSection = new VaAltreInfoSection(this, vaAltreInfoHome, vaEsperienzeHome);

		deMansioneTreeNode = mansioniSessionBean.getDeMansioneTreeNode();

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
			vaDatiGeneraliSection.initSection();
		} else if (section.contains(CODIZIONI_PROPOSTE)) {
			vaCondizioniProposteSection.initSection();
		} else if (section.contains(ISTRUZIONI)) {
			vaIstruzioniSection.initSection();
		} else if (section.contains(LINGUE)) {
			vaLingueSection.initSection();
		} else if (section.contains(ABILITAZIONI)) {
			vaAbilitazioniSection.initSection();
		} else if (section.contains(COMP_TRASV)) {
			vaCompetenzeTrasversaliPfSection.initSection();
		} else if (section.contains(COMP_DIGITALI)) {
			vaCompetenzeDigitaliPfSection.initSection();
		} else if (section.contains(ABILITAZIONI)) {
			vaAbilitazioniSection.initSection();
		} else if (section.contains(CONTATTI)) {
			vaContattiSection.initSection();
		} else if (section.contains(ALTRE_INFO)) {
			vaAltreInfoSection.initSection();
		}
	}

	public void redirectHome() {
		super.redirectHome();
	}

	public void archiviaVacancy() {

		Integer idPfPrincipalMod = session.getPrincipalId();
		vaDatiVacancyHome.archivia(vaDatiVacancy, idPfPrincipalMod, motivazione);
		// deleteVacancySolr(vaDatiVacancy.getIdVaDatiVacancy());

		vaDatiVacancyHome.deleteVacancySolr(vaDatiVacancy.getIdVaDatiVacancy());
		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					base + "/faces/secure/azienda/vacancies/view_pf.xhtml?faces-redirect=true&publish_session=true&id="
							+ vaDatiVacancy.getIdVaDatiVacancy() + "&archived=true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteVacancyNew() {
		try {
			if (vaDatiVacancy != null && vaDatiVacancy.getIdVaDatiVacancy() != null) {
				if (vaDatiVacancyHome.isProprietary(vaDatiVacancy.getIdVaDatiVacancy(),
						getSession().getPrincipalId())) {

					vaDatiVacancyHome.removeCascadeById(vaDatiVacancy.getIdVaDatiVacancy(), session.getPrincipalId());
					// delete di Solr gia` inclusa

					RequestContext.getCurrentInstance().addCallbackParam("deletedSuccessfuly", true);
				} else {
					String title = StringEscapeUtils.escapeEcmaScript("Errore cancellazione vacancy");
					String message = StringEscapeUtils.escapeEcmaScript("Errore di sistema in fase di cancellazione");
					RequestContext.getCurrentInstance().execute("MyPortal.errorAlert('" + title
							+ "','Solo l'utente che ha creato la vacancy puo` eliminarla')");
				}
			}
		} catch (EJBException e) {
			String title = StringEscapeUtils.escapeEcmaScript("Errore cancellazione vacancy");
			String message = StringEscapeUtils.escapeEcmaScript("Errore di sistema in fase di cancellazione");
			RequestContext.getCurrentInstance().execute("MyPortal.errorAlert('" + title + "','" + message + "')");
		}

	}

	public String copyVacancyNew() {

		try {
			if (vaDatiVacancy != null && vaDatiVacancy.getIdVaDatiVacancy() != null && tempVacancyToCopy != null) {
				VaDatiVacancy vaDatiVacancyCopiato;
				if (vaDatiVacancyHome.isProprietary(vaDatiVacancy.getIdVaDatiVacancy(),
						getSession().getPrincipalId())) {
					vaDatiVacancyCopiato = vaDatiVacancyHome.copyByIdNoDTO(getSession().getPrincipalId(),
							vaDatiVacancy.getIdVaDatiVacancy(), tempVacancyToCopy);
					// Da valutare se riattivarlo
					// String message = StringEscapeUtils.escapeEcmaScript("Vacancy copiata");
					// RequestContext.getCurrentInstance().execute("MyPortal.sucessMessage('" + message + "')");
					return ConstantsSingleton.EDIT_PF_VA_IDO + "?faces-redirect=true" + "&" + "id="
							+ vaDatiVacancyCopiato.getIdVaDatiVacancy();

				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			gestisciErrore(e, "vacancy.error_copying");
		}
		return "#";
	}

	public void pubblicaVacancy() {

		// Ottengo i valori da controllare
		VaRapportoDiLavoroDTO vaRapportoDiLavoroDTO = vaRapportoDiLavoroHome
				.findDTOByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		List<IDecode> contrattoList = vaRapportoDiLavoroDTO.getTipologieContratto();
		VaContattoDTO vaContattoDTO = vaContattoHome.findDTOByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		String email = "", fax = "", telefono = "";
		if (vaContattoDTO != null) {
			email = vaContattoDTO.getMail();
			fax = vaContattoDTO.getFaxRiferimento();
			telefono = vaContattoDTO.getTelRiferimento();
		}

		// Eseguo il controllo sui valori (campi non valorizzati)
		// - Tipologia contratto obbligatoria
		// - Almeno un contatto tra email, telefono e fax presente obbligatoro
		// - Data di pubblicazione uguale o maggiore del valore della data odierna
		boolean isOggiMaggioreDiDataPubblicazione = DateUtils.getToday()
				.compareTo(DateUtils.dateWithoutHourMinuteSecond(vaDatiVacancy.getDtPubblicazione())) > 0;

		if (isOggiMaggioreDiDataPubblicazione) {
			log.info("Pubblicazione non avvenuta per errori nei controlli");
			String strMessage = "E` obbligatorio indicare una data di pubblicazione successiva ad oggi";
			String title = StringEscapeUtils.escapeEcmaScript("Errore pubblicazione vacancy");
			String message = StringEscapeUtils.escapeEcmaScript(strMessage);
			RequestContext.getCurrentInstance().execute("MyPortal.errorAlert('" + title + "','" + message + "')");

		} else {
			Integer idPfPrincipalMod = session.getPrincipalId();
			vaDatiVacancyHome.pubblica(vaDatiVacancy, idPfPrincipalMod);

			String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(base
						+ "/faces/secure/azienda/vacancies/view_pf.xhtml?faces-redirect=true&publish_session=true&id="
						+ vaDatiVacancy.getIdVaDatiVacancy() + "&published=true");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isNotScadenzaModify() {
		boolean check = false;
		if (vaDatiVacancy.getCodStatoVacancyEnum() != null
				&& CodStatoVacancyEnum.PUB.equals(vaDatiVacancy.getCodStatoVacancyEnum())) {
			check = true;
		}
		return check;
	}

	public boolean isVacancyPubbWithCandidatura() {
		boolean check = false;
		List<AcCandidatura> listcandidature = null;
		if (vaDatiVacancy.getIdVaDatiVacancy() != null) {
			listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
			if ((vaDatiVacancy.getCodStatoVacancyEnum() != null
					&& CodStatoVacancyEnum.PUB.equals(vaDatiVacancy.getCodStatoVacancyEnum()))
					&& (listcandidature != null && !listcandidature.isEmpty())) { // la vacancy è pubblicata ed ha
																					// almeno una candidatura associata
				check = true;
			}
		}
		return check;
	}

	public boolean isArchivedNoModify() {
		boolean check = false;
		if (vaDatiVacancy.getCodStatoVacancyEnum() != null
				&& CodStatoVacancyEnum.ARC.equals(vaDatiVacancy.getCodStatoVacancyEnum())) {
			check = true;
		}
		return check;
	}

	public boolean isArchiviaRendered() {
		return vaDatiVacancyHome.isArchiviaRendered(vaDatiVacancy, getSession());
	}

	public boolean isProrogaRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)
				&& vaDatiVacancy.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB) && isInScadenza()
				&& vaDatiVacancy.getDeEvasioneRich() == null
				&& vaDatiVacancy.getPfPrincipal().getIdPfPrincipal().equals(session.getPrincipalId())
				&& canDoAnotherProroga() && ConstantsSingleton.DeProvenienza.COD_MYPORTAL
						.equals(vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza()))
			return true;
		else
			return false;
	}

	public boolean isEliminaRendered() {
		if (vaDatiVacancy.getIdVaDatiVacancy() != null) {
			return ConstantsSingleton.DeProvenienza.COD_MYPORTAL
					.equals(vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza())
					&& CodStatoVacancyEnum.LAV.equals(vaDatiVacancy.getCodStatoVacancyEnum())
					&& vaDatiVacancy.isProprietario(vaDatiVacancy.getPfPrincipal().getIdPfPrincipal());
		} else {
			return true; // Vacancy non ancora stata creata, l'id è null
		}
	}

	public boolean isPubblicaRendered() {
		if (vaDatiVacancy.getIdVaDatiVacancy() != null) {
			return ConstantsSingleton.DeProvenienza.COD_MYPORTAL
					.equals(vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza())
					&& CodStatoVacancyEnum.LAV.equals(vaDatiVacancy.getCodStatoVacancyEnum())
					&& vaDatiVacancy.isProprietario(vaDatiVacancy.getPfPrincipal().getIdPfPrincipal());
		} else {
			return true; // Vacancy non ancora stata creata, l'id è null
		}
	}

	public String getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}

	public VaDatiVacancy getVaDatiVacancy() {
		return vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
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

	public VaDatiVacancyHome getVaDatiVacancyHome() {
		return vaDatiVacancyHome;
	}

	public AziendaInfo getUtenteCompletoDTO() {
		return utenteCompletoDTO;
	}

	public VaDatiGeneraliSection getDatiGeneraliVacancySection() {
		return vaDatiGeneraliSection;
	}

	public void setDatiGeneraliVacancySection(VaDatiGeneraliSection vaDatiGeneraliSection) {
		this.vaDatiGeneraliSection = vaDatiGeneraliSection;
	}

	public VaCondizioniProposteSection getVaCondizioniProposteSection() {
		return vaCondizioniProposteSection;
	}

	public void setVaCondizioniProposteSection(VaCondizioniProposteSection vaCondizioniProposteSection) {
		this.vaCondizioniProposteSection = vaCondizioniProposteSection;
	}

	public VaIstruzioniSection getVaIstruzioniSection() {
		return vaIstruzioniSection;
	}

	public void setVaIstruzioniSection(VaIstruzioniSection vaIstruzioniSection) {
		this.vaIstruzioniSection = vaIstruzioniSection;
	}

	public VaLingueSection getVaLingueSection() {
		return vaLingueSection;
	}

	public void setVaLingueSection(VaLingueSection vaLingueSection) {
		this.vaLingueSection = vaLingueSection;
	}

	public VaAbilitazioniSection getVaAbilitazioniSection() {
		return vaAbilitazioniSection;
	}

	public void setVaAbilitazioniSection(VaAbilitazioniSection vaAbilitazioniSection) {
		this.vaAbilitazioniSection = vaAbilitazioniSection;
	}

	public VaCompetenzeTrasversaliPfSection getVaCompetenzeTrasversaliPfSection() {
		return vaCompetenzeTrasversaliPfSection;
	}

	public void setVaCompetenzeTrasversaliPfSection(VaCompetenzeTrasversaliPfSection vaCompetenzeTrasversaliPfSection) {
		this.vaCompetenzeTrasversaliPfSection = vaCompetenzeTrasversaliPfSection;
	}

	public VaCompetenzeDigitaliPfSection getVaCompetenzeDigitaliPfSection() {
		return vaCompetenzeDigitaliPfSection;
	}

	public void setVaCompetenzeDigitaliPfSection(VaCompetenzeDigitaliPfSection vaCompetenzeDigitaliPfSection) {
		this.vaCompetenzeDigitaliPfSection = vaCompetenzeDigitaliPfSection;
	}

	public VaContattiSection getVaContattiSection() {
		return vaContattiSection;
	}

	public void setVaContattiSection(VaContattiSection vaContattiSection) {
		this.vaContattiSection = vaContattiSection;
	}

	public VaAltreInfoSection getVaAltreInfoSection() {
		return vaAltreInfoSection;
	}

	public void setVaAltreInfoSection(VaAltreInfoSection vaAltreInfoSection) {
		this.vaAltreInfoSection = vaAltreInfoSection;

	}

	public String getVacanciesMaxScadenza() {
		return ConstantsSingleton.VACANCIES_MAX_SCADENZAIDO;
	}

	public String getTempVacancyToCopy() {
		return tempVacancyToCopy;
	}

	public void setTempVacancyToCopy(String tempVacancyToCopy) {
		this.tempVacancyToCopy = tempVacancyToCopy;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public boolean isInScadenza() {
		if (vaDatiVacancy.getDtScadenzaPubblicazione() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			long diffInMillies, diff = 0;

			try {
				// Date scadezaDate = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(getDtScadenza()));
				Date nowDate = sdf.parse(sdf.format(new Date()));
				diffInMillies = vaDatiVacancy.getDtScadenzaPubblicazione().getTime() - nowDate.getTime();
				diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (diff <= 5 && diff >= 0)
				return true;
			else {
				return false;
			}

		} else {
			return false;
		}
	}

	public boolean canDoAnotherProroga() {
		Calendar scadenza = Calendar.getInstance();
		long diffInMillies, diff = 0;
		// add 15 days to date of scadenza
		scadenza.setTime(vaDatiVacancy.getDtScadenzaPubblicazione());
		scadenza.add(Calendar.DAY_OF_MONTH, 15);

		Date newScadezaDate = scadenza.getTime();
		Date pubblicaDate = vaDatiVacancy.getDtPubblicazione();
		diffInMillies = newScadezaDate.getTime() - pubblicaDate.getTime();
		diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		if (diff < 30)
			return true;
		else
			return false;
	}

	public void prorogaVacancy() {
		vaDatiVacancyHome.prorogaRER(vaDatiVacancy, session.getPrincipalId());

		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(base + "/faces/secure/azienda/vacancies/edit_pf.xhtml?faces-redirect=true&id="
							+ vaDatiVacancy.getIdVaDatiVacancy() + "&prorogata=true");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isCheckAgricolo() {
		return checkAgricolo;
	}

	public void setCheckAgricolo(boolean checkAgricolo) {
		this.checkAgricolo = checkAgricolo;
	}

	public void handleChangeProfessioni() {
		// ricalcora il map e attenzione al 4only
		if (isCheckAgricolo()) {
			deMansioneTreeNode = mansioniSessionBean.getDeMansioneTreeNodeAgricoli();
		} else {
			deMansioneTreeNode = mansioniSessionBean.getDeMansioneTreeNode();
		}
	}

	public TreeNode getDeMansioneTreeNode() {
		return deMansioneTreeNode;
	}

	public void setDeMansioneTreeNode(TreeNode deMansioneTreeNode) {
		this.deMansioneTreeNode = deMansioneTreeNode;
	}

	public MansioniSessionBean getMansioniSessionBean() {
		return mansioniSessionBean;
	}

	public void setMansioniSessionBean(MansioniSessionBean mansioniSessionBean) {
		this.mansioniSessionBean = mansioniSessionBean;
	}

	public boolean isCheckModifyPubbWithHasCandid() {
		return checkModifyPubbWithHasCandid;
	}

	public void setCheckModifyPubbWithHasCandid(boolean checkModifyPubbWithHasCandid) {
		this.checkModifyPubbWithHasCandid = checkModifyPubbWithHasCandid;
	}
}