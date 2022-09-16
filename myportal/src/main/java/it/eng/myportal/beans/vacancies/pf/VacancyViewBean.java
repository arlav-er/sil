package it.eng.myportal.beans.vacancies.pf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.Poi;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.VaInformatica;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.VaLingua;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PoiHome;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
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
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 *
 * @author hatemalimam
 */
@ManagedBean(name = "vacancyViewBean")
@ViewScoped
public class VacancyViewBean extends AbstractBaseBean implements Serializable {

	private static final long serialVersionUID = -5966936237716860816L;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	// @EJB
	// EmailManagerEJB emailManagerEJB;

	protected VaDatiVacancy vacancy;
	private List<VaLingua> vaLinguaList;
	private VaInformatica vaInformatica;
	private VaRetribuzione vaRetribuzione;

	private List<VaContratto> vaContrattoList;
	private VaPubblicazione vaPubblicazione;
	private List<VaIstruzione> vaIstruzioneList;
	private List<VaPatente> vaPatenteList;
	private List<VaOrario> vaOrarioList;
	private VaContatto vaContatto;
	private VaAltreInfo vaAltreInfo;
	private VaEsperienze vaEsperienze;
	private List<VaPatentino> vaPatentinoList;
	private List<VaAlbo> vaAlboList;
	private VaCompetenzeTrasv vaCompetenzeTrasv;
	// private EmailPOJO emailPojo;

	private UploadedFile cvUploadedFile;
	private UploadedFile letteraUploadedFile;

	private boolean expiredVacancy;

	// map
	private Double centerPositionLat;
	private Double centerPositionLng;
	private String markerTitle;

	// arichiviazione
	private String motivazione;

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
	protected DeProvenienzaHome deProvenienzaHome;
	@EJB
	protected VaCompetenzeTrasvHome vaCompetenzeTrasvHome;
	@EJB
	protected VaRapportoDiLavoroHome vaRapportoDiLavoroHome;
	@EJB
	protected VaContrattoHome vaContrattoHome;
	@EJB
	protected VaTurnoHome vaTurnoHome;
	@EJB
	protected VaEsperienzeHome vaEsperienzeHome;
	@EJB
	protected VaLinguaHome valinguaHome;
	@EJB
	protected DeGradoLinHome deGradoLinHome;
	@EJB
	protected VaPubblicazioneHome vaPubblicazioneHome;
	@EJB
	protected DeRetribuzioneHome deRetribuzioneHome;
	@EJB
	protected DeModalitaLinguaHome deModalitaLinguaHome;
	@EJB
	protected VaPatenteHome vaPatenteHome;
	@EJB
	protected VaAlboHome vaAlboHome;
	@EJB
	protected VaPatentinoHome vaPatentinoHome;
	@EJB
	protected VaContattoHome vaContattoHome;
	@EJB
	private AziendaInfoHome aziendaInfoHome;
	@EJB
	private UtenteInfoHome utenteInfoHome;
	@EJB
	protected AcCandidaturaHome acCandidaturaHome;
	@EJB
	private PoiHome poiHome;
	@EJB
	private PfPrincipalHome pfPrincipalHome;
	@EJB
	private RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	private DeComuneHome deComuneHome;

	private List<SelectItem> myClicLavoroCurricula;
	private List<SelectItem> myLettere;
	private List<SelectItem> myCurricula;
	private List<AcCandidatura> listCandidatura;
	private boolean hasCandidatura = false;
	private Long numCandidature;
	private AcCandidaturaDTO candidatura;
	private VaDatiVacancyDTO vaDatiVacancyDTO;
	private UploadedFile file;
	private Poi poi;
	private String mapZoom;
	private String ragioneSociale;
	private String codiceFiscale;
	private AziendaInfo aziendaInfoRichiedente;

	private int numTotalCandidature;

	@PostConstruct
	public void initPostConstruct() {
		String vacancyId = getRequestParameter("id");
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(new Integer(vacancyId));
		log.info("Carico vacancy: " + vacancyId);
		if (vacancy == null) {
			// redirect404();
			return;
		}

		setVacancy(vacancy);
		// emailPojo = new EmailPOJO();
		Date tHold = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);

		vaLinguaList = valinguaHome.findByVacancyIdOrdered(vacancy.getIdVaDatiVacancy());
		vaInformatica = vaInformaticaHome.findByVacancyId(vacancy.getIdVaDatiVacancy());
		vaContrattoList = vaContrattoHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		vaIstruzioneList = vaIstruzioneHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		vaPatenteList = vaPatenteHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		vaPatentinoList = vaPatentinoHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		vaAlboList = vaAlboHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		try {
			vaContatto = vaContattoHome.findPrincByVacancyId(vacancy.getIdVaDatiVacancy());
		} catch (MyPortalNoResultFoundException e) {
			// TODO Vince e qua?
		}
		vaOrarioList = vaOrarioHome.findProperByVacancyId(vacancy.getIdVaDatiVacancy());
		vaPubblicazione = vaPubblicazioneHome.findByVacancyId(vacancy.getIdVaDatiVacancy());
		vaEsperienze = vaEsperienzeHome.findByVacancyId(vacancy.getIdVaDatiVacancy());
		vaRetribuzione = vaRetribuzioneHome.findById(vacancy.getIdVaDatiVacancy());
		vaAltreInfo = vaAltreInfoHome.findById(vacancy.getIdVaDatiVacancy());
		vaCompetenzeTrasv = vaCompetenzeTrasvHome.findByVacancyId(vacancy.getIdVaDatiVacancy());
		ragioneSociale = rvRicercaVacancyHome.searchNomeAzienda(vacancy);
		codiceFiscale = rvRicercaVacancyHome.searchCodiceFiscale(vacancy);
		aziendaInfoRichiedente = getAziendaInfoRichiedente();

		// TODO implement this
		// expiredVacancy = vacancy.isVacancyScaduta(tHold);
		expiredVacancy = false;

		buildMapModel();

		numTotalCandidature = (int) ((long) acCandidaturaHome
				.findCandidatureCountByVacancyId(vacancy.getIdVaDatiVacancy()));

		/*
		 * se son un cittadino recupero i miei CV, lettere e autocandidatura all'azienda
		 */
		UtenteDTO connectedUtente = getSession().getConnectedUtente();
		if (connectedUtente != null) {
			Integer idPfPrincipal = connectedUtente.getId();

			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				myCurricula = utenteInfoHome
						.getAllCurriculaAsSelectItemIdoRER(getSession().getConnectedUtente().getId());
			} else {
				myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(getSession().getConnectedUtente().getId());
			}

			myClicLavoroCurricula = utenteInfoHome.getAllClicLavoroCurriculaAsSelectItem(idPfPrincipal);
			/*
			 * liveCurricula = new ArrayList<CvDatiPersonaliDTO>();
			 * liveCurricula.addAll(utenteInfoHome.getAllCurriculaLiveNoScad(idPfPrincipal));
			 * liveCurricula.addAll(utenteInfoHome.getAllCurriculaIntermediatiNoScad(idPfPrincipal));
			 */
			myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(idPfPrincipal);

			listCandidatura = acCandidaturaHome.findByUtenteAndAziendaIdPfPrincipal(idPfPrincipal,
					vacancy.getPfPrincipal().getIdPfPrincipal(), Integer.parseInt(vacancyId));
			candidatura = new AcCandidaturaDTO();
			/*
			 * Verifico che l'utente non si sia giÃ  candidato alla vacancy in questione
			 */
			if (listCandidatura == null || (listCandidatura != null && listCandidatura.isEmpty())) {

				hasCandidatura = false;
				candidatura = new AcCandidaturaDTO();
				candidatura.setIdPfPrincipalAzienda(vacancy.getPfPrincipal().getIdPfPrincipal());
				candidatura.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
				if (chekRER()) {
					candidatura.setCommento(ConstantsSingleton.Candidatura.TXT_MSG_INVIA_CANDIDATURA);
				}

			} else {
				hasCandidatura = true;
			}

			if (vacancyId != null && (listCandidatura != null && !listCandidatura.isEmpty())) { // in questo caso non
																								// sto lavorando con le
																								// autocandidature
				numCandidature = Long.valueOf(acCandidaturaHome.checkNumberOrderedVacancyToCandidate(
						Integer.parseInt(vacancyId), listCandidatura.get(0).getIdAcCandidatura()));
			}

			numTotalCandidature = checkTotalNumberOrderedVacancyToCandidate(Integer.parseInt(vacancyId));
		}
		/* BLOCCO CONTROLLI CRESCO DA INSERIRE O SOLO PER UMBRIA ?? */

	}

	public void buildMapModel() {
		try {
			poi = poiHome.findByIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
			mapZoom = "13";
			centerPositionLat = poi.getLat();
			centerPositionLng = poi.getLon();
		} catch (MyPortalNoResultFoundException e) {
			log.warn("Il campo poi non è valorizzato per la vacancy con id " + vacancy.getIdVaDatiVacancy());
		}
		markerTitle = vacancy.getIndirizzoLavoro();
		if (poi != null) {
			markerTitle = poi.getDescrizione();
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		this.setFile(event.getFile());
	}

	public void deleteFile() {
		this.setFile(null);
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check = true;
		}
		return check;
	}

	/**
	 * Registra l'invio della candidatura relativa alla vacancy selezionata.
	 */
	public void inviaCandidaturaNoDTO() {
		setVaDatiVacancyDTO(vaDatiVacancyHome.findDTOById(vacancy.getIdVaDatiVacancy()));
		// Setto i campi opzionali per CRESCO
		/*
		 * CRESCO NON E' IMPLEMENTATO PER RER if (flgCresco && dummyCandidaturaDatiSil != null) {
		 * candidatura.setAcCandidaturaDatiList(getNuovaCandidaturaCrescoDati()); }
		 */
		if (!getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			addErrorMessage("vacancy.error_not_present");
			return;
		}

		// Setto i campi relativi alla candidatura
		candidatura.setIdVaDatiVacancy(getVaDatiVacancyDTO().getId());
		candidatura.setFlagClicLavoro(getVaDatiVacancyDTO().getFlagInvioCl());

		Integer idPfPrincipalAzienda = getVaDatiVacancyDTO().getIdPfPrincipalAzienda();
		Integer idVaDatiVacancy = vacancy.getIdVaDatiVacancy();
		Integer idPrincipal = session.getPrincipalId();
		byte[] file = null;
		if (getFile() != null) {
			candidatura.setAllegatoFileName(getFile().getFileName());
			file = getFile().getContents();
		}

		try {
			AcCandidatura candidaturaInviata = acCandidaturaHome.inviaCandidaturaNoDTO(candidatura, idPrincipal,
					idPfPrincipalAzienda, idVaDatiVacancy, file);

			int numberCheckOrderedVacancyToCandidate = 0;
			if (candidaturaInviata.getDtmIns() != null) {
				hasCandidatura = true;
				numberCheckOrderedVacancyToCandidate = checkNumberOrderedVacancyToCandidate(idVaDatiVacancy,
						candidaturaInviata.getIdAcCandidatura());
				RequestContext.getCurrentInstance().addCallbackParam("number", numberCheckOrderedVacancyToCandidate);

				// addInfoMessageWithParam("candidatura.sentRER",String.valueOf(numberCheckOrderedVacancyToCandidate));
			} else {
				addErrorMessage("vacancy.cv_obbligatorio");
			}
			/*
			 * String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath(); try {
			 * FacesContext.getCurrentInstance().getExternalContext().redirect( base +
			 * "/faces/secure/azienda/vacancies/view_pf.xhtml?faces-redirect=true&id=" + idVaDatiVacancy); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */
		} catch (MyPortalException e) {
			gestisciErrore(e, "candidatura.allegato.error");
		}
	}

	public void cancelEditConfermaCandidatura() {

		hasCandidatura = false;
		candidatura = new AcCandidaturaDTO();
		candidatura.setIdPfPrincipalAzienda(vacancy.getPfPrincipal().getIdPfPrincipal());
		candidatura.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
		file = null;
		if (chekRER()) {
			candidatura.setCommento(ConstantsSingleton.Candidatura.TXT_MSG_INVIA_CANDIDATURA);
		}
	}

	/**
	 * Registra l'invio della candidatura relativa alla vacancy selezionata.
	 */
	public void inviaCandidatura() {
		Integer idPrincipal = session.getPrincipalId();

		try {
			byte[] file = null;
			String nameFileTmp = candidatura.getAllegatoFileNameTmp();
			if (nameFileTmp != null && !("").equalsIgnoreCase(nameFileTmp)) {
				String baseDir = ConstantsSingleton.TMP_DIR;
				File file2send = new File(baseDir + File.separator + nameFileTmp);
				file = Utils.fileToByte(file2send.getAbsolutePath());
			}

			Boolean invioCandidatura = acCandidaturaHome.inviaCandidatura(candidatura, idPrincipal,
					getVaDatiVacancyDTO().getIdPfPrincipalAzienda(), vacancy.getIdVaDatiVacancy(), file);
			if (invioCandidatura) {
				hasCandidatura = true;
				addInfoMessage("candidatura.sent");
			} else {
				addErrorMessage("vacancy.cv_obbligatorio");
			}
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		} catch (FileNotFoundException e) {
			addErrorMessage("generic.error");
		} catch (IOException e) {
			addErrorMessage("generic.error");
		}
	}

	public int checkNumberOrderedVacancyToCandidate(Integer vacancyId, Integer acCandidaturaId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		for (int j = 0; j < listcandidature.size(); j++) {
			if (listcandidature.get(j).getIdAcCandidatura() == acCandidaturaId.intValue()) {
				i = j;
				break;
			}
		}
		return ++i;
	}

	public int checkTotalNumberOrderedVacancyToCandidate(Integer vacancyId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		if (listcandidature != null && !listcandidature.isEmpty()) {
			i = listcandidature.size();
		}
		return i;
	}

	public void handleCvAllegato(FileUploadEvent event) {
		cvUploadedFile = event.getFile();
		// emailPojo.setCvAllegato(cvUploadedFile.getContents());
		log.info("uploaded a file with the name : " + event.getFile().getFileName());
	}

	public StreamedContent handleCvAllegatoDownload() {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cvUploadedFile.getContents());
			return new DefaultStreamedContent(is, cvUploadedFile.getContentType(), cvUploadedFile.getFileName());
		} catch (NullPointerException e) {
			log.error("Errore durante handleFileDownload " + e.toString());
			return null;
		}
	}

	public boolean isCandidaturaEnabled() {
		return vacancy.getFlgCandidatura();
	}

	public void removeCvAllegato() {
		cvUploadedFile = null;
		// emailPojo.setCvAllegato(null);
	}

	public void handleLetteraAllegato(FileUploadEvent event) {
		letteraUploadedFile = event.getFile();
		// emailPojo.setLetteraAllegato(letteraUploadedFile.getContents());
		log.info("uploaded a file with the name : " + event.getFile().getFileName());
	}

	public StreamedContent handleLetteraAllegatoDownload() {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(letteraUploadedFile.getContents());
			return new DefaultStreamedContent(is, letteraUploadedFile.getContentType(),
					letteraUploadedFile.getFileName());
		} catch (NullPointerException e) {
			log.error("Errore durante handleFileDownload " + e.toString());
			return null;
		}
	}

	public void removeLetteraAllegato() {
		letteraUploadedFile = null;
		// emailPojo.setLetteraAllegato(null);
	}

	/**
	 * spedisce la mail all'azienda
	 *
	 */
	public void sendMail() {
		// if (vacancy.getMailRiferimentoPub() != null) {
		// setVacancy(dettaglioVacancyPubEJB.findPOJOById(vacancy.getIdVaDatiVacancy()));
		//
		// if (uploadRequiredCheckFailed() || canSendEmail()) {
		// return;
		// }
		//
		// emailPojo.setTo(vacancy.getMailRiferimentoPub().trim());
		// emailPojo.setAnno(vacancy.getAnno());
		// emailPojo.setNumero(vacancy.getNumero());
		// emailPojo.setIdVacancy("" + vacancy.getIdVaDatiVacancy());
		//
		// if (emailManagerEJB.sendEmailCandidatura(emailPojo)) {
		// RequestContext.getCurrentInstance().addCallbackParam("inviaSuccess", true);
		// log.info("Mail candidatura inviata correttamente");
		// } else {
		// log.error("Errore invio mail candidatura");
		// }
		// } else {
		// log.error("Impossibile inviare la mail, email contatto vacancy nullo");
		// }
	}

	/**
	 * se cod_provenienza_vacancy = MYPORTAL --> va_dati_vacancy.id_pf_principal.azienda_info.ragione_sociale se invece
	 * cod_provenieenza_vacancy = SIL --> fare distinzione se annuncio anonimo/palese se anonimo
	 * (va_dati_vacancy.id_pf_principal_palese is null) --> mostrare ragione sociale del cpi =
	 * va_dati_vacancy.id_pf_principal.azienda_info.ragione_sociale se palese(va_dati_vacancy.id_pf_principal_palese is
	 * not null ) allora mostrare i dati dell'azienda palese -->
	 * va_dati_vacancy.id_pf_principal_palese.azienda_info.ragione_sociale
	 * 
	 * @return
	 */
	public String getAziendaRichiedente() {
		DeProvenienza provenSIL = deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC);
		DeProvenienza proven = vacancy.getDeProvenienzaVacancy();
		AziendaInfo azi = aziendaInfoHome.findById(vacancy.getPfPrincipal().getIdPfPrincipal());
		if (proven.equals(provenSIL)) {
			// SIL
			if (vacancy.getPfPrincipalPalese() == null) {
				return azi.getRagioneSociale();
			} else {
				azi = aziendaInfoHome.findById(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
			}
		}
		return azi.getRagioneSociale();
	}

	public AziendaInfo getAziendaInfoRichiedente() {
		DeProvenienza deProvenienza = vacancy.getDeProvenienzaVacancy();
		AziendaInfo aziendaInfo = new AziendaInfo();
		if (deProvenienza != null) {
			if (ConstantsSingleton.DeProvenienza.COD_MYPORTAL.equals(deProvenienza.getCodProvenienza())) { // Vancancy
																											// da
																											// MYPORTAL
				log.info("Recupero le info dell'azienda con codProvenienza : MYPORTAL ");
				aziendaInfo = aziendaInfoHome.findByIdPfPrincipal(vacancy.getPfPrincipal().getIdPfPrincipal());
				DeComune deComune = deComuneHome.findById(aziendaInfo.getDeComuneSede().getCodCom());
				aziendaInfo.setDeComuneSede(deComune);
				return aziendaInfo;
			}
			if (deProvenienza.getCodProvenienza().startsWith("SIL")) { // Vacancy da SIL
				log.info("Recupero le info dell'azienda con codProvenienza LIKE: SIL ");
				if (vacancy.getPfPrincipalPalese() != null) { // Vacancy pubblica
					aziendaInfo = aziendaInfoHome
							.findByIdPfPrincipal(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
					DeComune deComune = deComuneHome.findById(aziendaInfo.getDeComuneSede().getCodCom());
					aziendaInfo.setDeComuneSede(deComune);
					return aziendaInfo;
				}
			}
		}
		return aziendaInfo;
	}

	public String getAziendaRichiedentePaleseDFD() {
		DeProvenienza proven = vacancy.getDeProvenienzaVacancy();
		DeEvasioneRich deEvasioneRich = vacancy.getDeEvasioneRich();
		List<PfPrincipal> pfPrincipalPalese = pfPrincipalHome.findPaleseByIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());

		boolean condizioneCodProvenienzaLikeSIL = proven != null
				&& proven.getCodProvenienza().contains(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC);
		boolean condizionePaleseNotNull = !pfPrincipalPalese.isEmpty() && pfPrincipalPalese.get(0) != null;
		boolean condizioneEvasioneDFD = deEvasioneRich != null
				&& deEvasioneRich.getCodEvasione().equals(ConstantsSingleton.Evasione.PUBB_PALESE);

		if (condizioneCodProvenienzaLikeSIL && condizionePaleseNotNull && condizioneEvasioneDFD) {
			AziendaInfo azi = aziendaInfoHome.findById(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
			return azi.getRagioneSociale();
		}
		return "";
	}

	public boolean canSendEmail() {
		boolean validationFailed = false;
		if (!renderContattaDatoreLav()) {

			getFacesContext().validationFailed();
			getFacesContext().addMessage("contattaForm:cvAllegato", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Errore invio mail candidatura", "La vacancy è stata archiviata"));
			validationFailed = true;
		}

		return validationFailed;
	}

	public boolean renderContattaDatoreLav() {
		if (vacancy == null) {
			return false;
		}

		return vacancy.getFlgCandidatura() && vacancy.isPubblicata();
	}

	public UploadedFile getCvUploadedFile() {
		return cvUploadedFile;
	}

	public void setCvUploadedFile(UploadedFile cvUploadedFile) {
		this.cvUploadedFile = cvUploadedFile;
	}

	public UploadedFile getLetteraUploadedFile() {
		return letteraUploadedFile;
	}

	public void setLetteraUploadedFile(UploadedFile letteraUploadedFile) {
		this.letteraUploadedFile = letteraUploadedFile;
	}

	public VaDatiVacancy getVacancy() {
		return vacancy;
	}

	public void setVacancy(VaDatiVacancy vacancy) {
		this.vacancy = vacancy;
	}

	// public EmailPOJO getEmailPojo() {
	// return emailPojo;
	// }
	//
	// public void setEmailPojo(EmailPOJO emailPojo) {
	// this.emailPojo = emailPojo;
	// }

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	// Questo wrapper andrebbe rimosso
	public boolean isNonModificabile() {
		return vacancy.isNonModificabile();
	}

	public boolean renderedModifica() {
		return vaDatiVacancyHome.isModificaRendered(vacancy.getIdVaDatiVacancy(), getSession());
	}

	public boolean isExpiredVacancy() {
		return expiredVacancy;
	}

	public void setExpiredVacancy(boolean expiredVacancy) {
		this.expiredVacancy = expiredVacancy;
	}

	public Double getCenterPositionLat() {
		return centerPositionLat;
	}

	public void setCenterPositionLat(Double centerPositionLat) {
		this.centerPositionLat = centerPositionLat;
	}

	public Double getCenterPositionLng() {
		return centerPositionLng;
	}

	public void setCenterPositionLng(Double centerPositionLng) {
		this.centerPositionLng = centerPositionLng;
	}

	public String getMarkerTitle() {
		if (markerTitle != null && !markerTitle.isEmpty()) {
			markerTitle = markerTitle.replace("\n", "").replace("\r", "").trim();
			return StringEscapeUtils.escapeEcmaScript(markerTitle);
		}

		return null;
	}

	public void setMarkerTitle(String markerTitle) {
		this.markerTitle = markerTitle;
	}

	public List<VaLingua> getVaLinguaList() {
		return vaLinguaList;
	}

	public void setVaLinguaList(List<VaLingua> vaLinguaList) {
		this.vaLinguaList = vaLinguaList;
	}

	public VaInformatica getVaInformatica() {
		return vaInformatica;
	}

	public void setVaInformatica(VaInformatica vaInformatica) {
		this.vaInformatica = vaInformatica;
	}

	public List<VaContratto> getVaContrattoList() {
		return vaContrattoList;
	}

	public void setVaContrattoList(List<VaContratto> vaContrattoList) {
		this.vaContrattoList = vaContrattoList;
	}

	public List<VaIstruzione> getVaIstruzioneList() {
		return vaIstruzioneList;
	}

	public void setVaIstruzioneList(List<VaIstruzione> vaIstruzioneList) {
		this.vaIstruzioneList = vaIstruzioneList;
	}

	public List<VaPatente> getVaPatenteList() {
		return vaPatenteList;
	}

	public void setVaPatenteList(List<VaPatente> vaPatenteList) {
		this.vaPatenteList = vaPatenteList;
	}

	public VaContatto getVaContatto() {
		return vaContatto;
	}

	public void setVaContatto(VaContatto vaContatto) {
		this.vaContatto = vaContatto;
	}

	public VaPubblicazione getVaPubblicazione() {
		return vaPubblicazione;
	}

	public void setVaPubblicazione(VaPubblicazione vaPubblicazione) {
		this.vaPubblicazione = vaPubblicazione;
	}

	public List<VaOrario> getVaOrarioList() {
		return vaOrarioList;
	}

	public void setVaOrarioList(List<VaOrario> vaOrarioList) {
		this.vaOrarioList = vaOrarioList;
	}

	public VaRetribuzione getVaRetribuzione() {
		return vaRetribuzione;
	}

	public void setVaRetribuzione(VaRetribuzione vaRetribuzione) {
		this.vaRetribuzione = vaRetribuzione;
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

	public List<VaPatentino> getVaPatentinoList() {
		return vaPatentinoList;
	}

	public void setVaPatentinoList(List<VaPatentino> vaPatentinoList) {
		this.vaPatentinoList = vaPatentinoList;
	}

	public List<VaAlbo> getVaAlboList() {
		return vaAlboList;
	}

	public void setVaAlboList(List<VaAlbo> vaAlboList) {
		this.vaAlboList = vaAlboList;
	}

	public VaCompetenzeTrasv getVaCompetenzeTrasv() {
		return vaCompetenzeTrasv;
	}

	public void setVaCompetenzeTrasv(VaCompetenzeTrasv vaCompetenzeTrasv) {
		this.vaCompetenzeTrasv = vaCompetenzeTrasv;
	}

	public List<AcCandidatura> getListCandidatura() {
		return listCandidatura;
	}

	public void setListCandidatura(List<AcCandidatura> listCandidatura) {
		this.listCandidatura = listCandidatura;
	}

	public boolean getHasCandidatura() {
		return hasCandidatura;
	}

	public void setHasCandidatura(boolean hasCandidatura) {
		this.hasCandidatura = hasCandidatura;
	}

	public Long getNumCandidature() {
		return numCandidature;
	}

	public void setNumCandidature(Long numCandidature) {
		this.numCandidature = numCandidature;
	}

	public List<SelectItem> getMyCurricula() {
		return myCurricula;
	}

	public void setMyCurricula(List<SelectItem> myCurricula) {
		this.myCurricula = myCurricula;
	}

	public List<SelectItem> getMyClicLavoroCurricula() {
		return myClicLavoroCurricula;
	}

	public void setMyClicLavoroCurricula(List<SelectItem> myClicLavoroCurricula) {
		this.myClicLavoroCurricula = myClicLavoroCurricula;
	}

	public List<SelectItem> getMyLettere() {
		return myLettere;
	}

	public void setMyLettere(List<SelectItem> myLettere) {
		this.myLettere = myLettere;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public AcCandidaturaDTO getCandidatura() {
		return candidatura;
	}

	public void setCandidatura(AcCandidaturaDTO candidatura) {
		this.candidatura = candidatura;
	}

	public VaDatiVacancyDTO getVaDatiVacancyDTO() {
		return vaDatiVacancyDTO;
	}

	public void setVaDatiVacancyDTO(VaDatiVacancyDTO vaDatiVacancyDTO) {
		this.vaDatiVacancyDTO = vaDatiVacancyDTO;
	}

	public String getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(String mapZoom) {
		this.mapZoom = mapZoom;
	}

	public Poi getPoi() {
		return poi;
	}

	public void setPoi(Poi poi) {
		this.poi = poi;
	}

	public int getNumTotalCandidature() {
		return numTotalCandidature;
	}

	public void setNumTotalCandidature(int numTotalCandidature) {
		this.numTotalCandidature = numTotalCandidature;
	}

	public Integer getIdSvAziendaInfo() {
		return vaDatiVacancyHome.getIdPfPrincipalVetrinaAzienda(vacancy);
	}

	public boolean isUlterioriInformazioniRendered() {
		return ((vacancy.getVaRetribuzione() != null && (vacancy.getVaRetribuzione().getBenefit() != null
				&& !vacancy.getVaRetribuzione().getBenefit().isEmpty()))
				|| (vacancy.getVaRetribuzione() != null && vacancy.getVaRetribuzione().getRetribuzione() != null)
				|| (vacancy.getVaRetribuzione() != null && (vacancy.getVaRetribuzione().getCcnl() != null
						&& !vacancy.getVaRetribuzione().getCcnl().isEmpty()))
				|| (vacancy.getVaAltreInfo() != null && vacancy.getVaAltreInfo().getDataPresuntaAssunzione() != null)
				|| (vacancy.getVaPubblicazione() != null && (vacancy.getVaPubblicazione().getLuogo() != null
						&& !vacancy.getVaPubblicazione().getLuogo().isEmpty()))
				|| (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getFlagMezzoPubblico() != null
						&& vacancy.getVaAltreInfo().getFlagMezzoPubblico()))
				// || (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzFuorisede() != null &&
				// vacancy.getVaAltreInfo().getOpzFuorisede().equals("S")))
				|| (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzTrasferta() != null
						&& vacancy.getVaAltreInfo().getOpzTrasferta().equals("Y")))
				// || (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzAutomunito() != null &&
				// vacancy.getVaAltreInfo().getOpzAutomunito().equals("S")))
				// || (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzMotomunito() != null &&
				// vacancy.getVaAltreInfo().getOpzMotomunito().equals("S")))
				|| (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzAutomunito() != null
						&& vacancy.getVaAltreInfo().getOpzAutomunito().equals("Y")))
				|| (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getOpzMotomunito() != null
						&& vacancy.getVaAltreInfo().getOpzMotomunito().equals("Y")))
				|| (vacancy.getVaPubblicazione() != null && (vacancy.getVaPubblicazione().getContratto() != null
						&& !vacancy.getVaPubblicazione().getContratto().isEmpty()))
				|| (vacancy.getVaPubblicazione() != null && (vacancy.getVaPubblicazione().getOrario() != null
						&& !vacancy.getVaPubblicazione().getOrario().isEmpty()))
				|| (vacancy.getVaAltreInfo() != null && (vacancy.getVaAltreInfo().getTurni() != null
						&& !vacancy.getVaAltreInfo().getTurni().isEmpty()))
				|| (vacancy.getVaPubblicazione() != null && (vacancy.getVaPubblicazione().getCandidatura() != null
						&& !vacancy.getVaPubblicazione().getCandidatura().isEmpty())));
	}

	public Long getNumCandidatureCalc() {
		return acCandidaturaHome.getNumAcCandidaturaByIdVacancy(vacancy.getIdVaDatiVacancy());
	}

	public Long getNumCandidatureCorrente() {
		if (vacancy.getIdVaDatiVacancy() != null && (listCandidatura != null && !listCandidatura.isEmpty())) {
			numCandidature = Long.valueOf(acCandidaturaHome.checkNumberOrderedVacancyToCandidate(
					vacancy.getIdVaDatiVacancy(), listCandidatura.get(0).getIdAcCandidatura()));
		}
		return numCandidature;
	}

	public Integer getNumTotalCandidatureCalc() {
		return numTotalCandidature = checkTotalNumberOrderedVacancyToCandidate(vacancy.getIdVaDatiVacancy());
	}

	public boolean checkOwner() {
		boolean check;

		if (getSession().getPrincipalId().equals(vacancy.getPfPrincipal().getIdPfPrincipal())
				|| (vacancy.getPfPrincipalPalese() != null
						&& getSession().getPrincipalId().equals(vacancy.getPfPrincipalPalese().getIdPfPrincipal()))) {
			check = true;
		} else
			check = false;

		return check;
	}

	public void refresh() {

		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(base
					+ "/faces/secure/azienda/vacancies/view_pf.xhtml?id=" + vacancy.getIdVaDatiVacancy().intValue());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isArchiviaRendered() {
		return vaDatiVacancyHome.isArchiviaRendered(vacancy, getSession());
	}

	public void archiviaVacancy() {
		Integer idPfPrincipalMod = session.getPrincipalId();
		vaDatiVacancyHome.archivia(vacancy, idPfPrincipalMod, motivazione);
		vaDatiVacancyHome.deleteVacancySolr(vacancy.getIdVaDatiVacancy());
		vacancy = vaDatiVacancyHome.findById(vacancy.getIdVaDatiVacancy());
		RequestContext.getCurrentInstance().addCallbackParam("archived", true);
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public boolean isArchiviata() {
		return vacancy.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.ARC);
	}

	public void setAziendaInfoRichiedente(AziendaInfo aziendaInfoRichiedente) {
		this.aziendaInfoRichiedente = aziendaInfoRichiedente;
	}
	
	public boolean isAziendaRichiedenteRendered() {
		if(isIndirizzoSedeRendered()
				|| isComuneSedeRendered()
				|| isTelefonoSedeRendered()
				|| isFaxSedeRendered()) {
			return true;
		}
		return false;
	}
	
	public boolean isIndirizzoSedeRendered() {
		if(aziendaInfoRichiedente.getIndirizzoSede() != null) {
			return true;
		}
		return false;
	}
	public boolean isComuneSedeRendered() {
		if(aziendaInfoRichiedente.getDeComuneSede() != null) {
			return true;
		}
		return false;
	}
	public boolean isTelefonoSedeRendered() {
		if(aziendaInfoRichiedente.getTelefonoSede() != null 
				&& !"0".equals(aziendaInfoRichiedente.getTelefonoSede())) {
			return true;
		}
		return false;
	}
	public boolean isFaxSedeRendered() {
		if(aziendaInfoRichiedente.getFaxSede() != null 
				&& !"0".equals(aziendaInfoRichiedente.getFaxSede())) {
			return true;
		}
		return false;
	}
	public boolean isTelAndFaxSedeRendered() {
		if(isTelefonoSedeRendered() || isFaxSedeRendered()) {
			return true;
		}
		return false;
	}
	
	public boolean isAccountUtenteAziendaRendered() {
		if(isCognomeRichiedenteRendered()
				|| isNomeRichiedenteRendered()
				|| isEmailRichiedenteRendered()) {
			return true;
		}
		return false;
	}
	public boolean isCognomeRichiedenteRendered() {
		if(aziendaInfoRichiedente.getCognomeRic()!= null) {
			return true;
		}
		return false;
	}
	public boolean isNomeRichiedenteRendered() {
		if(aziendaInfoRichiedente.getNomeRic()!=null) {
			return true;
		}
		return false;
	}
	public boolean isEmailRichiedenteRendered() {
		if(aziendaInfoRichiedente.getEmailRic()!=null) {
			return true;
		}
		return false;
	}
}
