package it.eng.myportal.beans.azienda.fabbisogno;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.FbCandidaturaAllegatoDTO;
import it.eng.myportal.dtos.FbCandidaturaDTO;
import it.eng.myportal.dtos.FbDatiAziendaDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.FbCandidaturaAllegatoHome;
import it.eng.myportal.entity.home.FbCandidaturaHome;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.entity.home.FbConvenzioneHome;
import it.eng.myportal.entity.home.FbDatiAziendaHome;
import it.eng.myportal.entity.home.FbSchedaFabbisognoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbSchedaHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "fbSchedaFormBean")
@ViewScoped
public class FbSchedaFormBean extends AbstractBaseBean {

	private static final String PATH_SCHEDE_LIST = "/faces/secure/azienda/convenzioni_quadro/scheda_fabbisogno/scheda_fb_list.xhtml";

	@EJB
	private FbChecklistHome fbChecklistHome;

	@EJB
	private FbConvenzioneHome fbConvenzioneHome;

	@EJB
	private FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	@EJB
	private FbDatiAziendaHome fbDatiAziendaHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private DeStatoFbSchedaHome deStatoFbSchedaHome;

	@EJB
	private DeMansioneMinHome deMansioneMinHome;

	@EJB
	private FbCandidaturaHome fbCandidaturaHome;

	@EJB
	private FbCandidaturaAllegatoHome fbCandidaturaAllegatoHome;

	private static final int MAX_CANDIDATURE_PER_POSIZIONE = 10;

	private FbSchedaFabbisognoDTO fbSchedaFabbisogno;
	private AziendaInfoDTO aziendaInfo;
	private DeAttivitaMinDTO deAttivitaMinTemp;
	private FbCandidaturaDTO nuovaCandidatura = new FbCandidaturaDTO();
	private boolean isEnteAccreditatoAttivo;
	private FbCandidaturaDTO eliminaCandidate;
	private String checkCandidate;
	private UploadedFile allegatoUpload;
	private String allegatoFileName;
	private FbCandidaturaAllegatoDTO fbCandidaturaAllegatoDTO;
	private DefaultStreamedContent download;

	@PostConstruct
	protected void postConstruct() {
		// Entrano solo enti ospitanti ed enti accreditati.
		if (!isEnteOspitante() && !isEnteAccreditato() && !session.isRegione()) {
			redirectHome();
		}

		// Se sono un ente accreditato, controllo se sono attivo (ho almeno una convenzione protocollata attiva)
		isEnteAccreditatoAttivo = isEnteAccreditato()
				&& (fbConvenzioneHome.countConvenzioniAttiveByIdPrincipalAndStato(session.getPrincipalId(),
						ConstantsSingleton.DeStatoFbConvenzione.PROTOCOLLATA) > 0);

		// Se mi è stata passata una scheda fabbisogno, la carico.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("idFbSchedaFabbisogno")) {
			try {
				String idFbSchedaParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
						.get("idFbSchedaFabbisogno");
				Integer idFbScheda = Integer.parseInt(idFbSchedaParam);
				fbSchedaFabbisogno = fbSchedaFabbisognoHome.findDTOById(idFbScheda);
				deAttivitaMinTemp = fbSchedaFabbisogno.getFbDatiAzienda().getDeAttivitaMin();

				// Se sono un ente ospitante NON proprietario della scheda, vengo cacciato.
				if (isEnteOspitante() && !session.getPrincipalId().equals(fbSchedaFabbisogno.getIdPrincipalIns())) {
					addCustomErrorMessage("Non puoi accedere alla scheda fabbisogno selezionata");
					redirectHome();
				}
			} catch (Exception e) {
				addErrorMessage("data.error_loading");
			}
		}

		// Se devo creare una nuova scheda fabbisogno, ho bisogno che mi sia passata la checklist.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("idFbChecklist")) {
			try {
				String idFbChecklistParam = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("idFbChecklist");
				Integer idFbChecklist = Integer.parseInt(idFbChecklistParam);
				inizializzaNuovaScheda(idFbChecklist);
			} catch (Exception e) {
				addErrorMessage("data.error_loading");
			}
		}

		fbCandidaturaAllegatoDTO = new FbCandidaturaAllegatoDTO();

		if (session.isEntePromotoreCalabria()) {
			aziendaInfo = aziendaInfoHome.findDTOById(fbSchedaFabbisogno.getIdPrincipalIns());
		} else {
			// Carico i dati dell'utente
			aziendaInfo = aziendaInfoHome.findDTOById(session.getPrincipalId());

		}

	}

	/** Inizializzo una nuova scheda FB, recuperando se possibile dei DatiAzienda esistenti */
	private void inizializzaNuovaScheda(Integer idFbChecklist) {
		fbSchedaFabbisogno = new FbSchedaFabbisognoDTO();
		fbSchedaFabbisogno.setIdFbChecklist(idFbChecklist);
		fbSchedaFabbisogno
				.setDeStatoScheda(deStatoFbSchedaHome.findDTOById(ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE));

		// Se ho almeno un elemento in FbDatiAzienda, prendo il più recente e copio tutti i dati nel nuovo.
		FbDatiAziendaDTO latestDatiAzienda = fbDatiAziendaHome.findLatestDTOByIdPfPrincipal(session.getPrincipalId());
		if (latestDatiAzienda != null) {
			fbSchedaFabbisogno.setFbDatiAzienda(latestDatiAzienda);
			// Initalizing defualte value for the new nome_legale and cognome_legale input fields of the scheda
			fbSchedaFabbisogno.getFbDatiAzienda().setNomeLegale(latestDatiAzienda.getNomeLegale());
			fbSchedaFabbisogno.getFbDatiAzienda().setCognomeLegale(latestDatiAzienda.getCognomeLegale());
			if (fbSchedaFabbisogno.getFbDatiAzienda().getDeAttivitaMin() != null) {
				deAttivitaMinTemp = deAttivitaMinHome
						.findDTOById(fbSchedaFabbisogno.getFbDatiAzienda().getDeAttivitaMin().getCodAttivitaPadre());
			}
			fbSchedaFabbisogno.getFbDatiAzienda().setId(null);
		} else {
			fbSchedaFabbisogno.setFbDatiAzienda(new FbDatiAziendaDTO());
		}

	}

	public boolean isEnteOspitante() {
		return session.isAzienda() && session.isEnteOspitanteCalabria();
	}

	public boolean isEnteAccreditato() {
		return session.isAzienda() && session.isEnteAccreditato();
	}

	/**
	 * Posso salvare/modificare/eliminare la scheda se sono l'ente ospitante, se ne sono il proprietario, ed è in
	 * lavorazione
	 */
	public boolean isEditSchedaMode() {

		if (session == null) {
			log.error("session NULL");
			return false;
		}

		if (fbSchedaFabbisogno == null) {
			log.error("fbSchedaFabbisogno NULL");
			return false;
		}

		if (fbSchedaFabbisogno.getDeStatoScheda() == null) {
			log.error("fbSchedaFabbisogno.getDeStatoScheda() NULL");
			return false;
		}

		return ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE.equals(fbSchedaFabbisogno.getDeStatoScheda().getId())
				&& isEnteOspitante() && (fbSchedaFabbisogno.getIdPrincipalIns() == null
						|| fbSchedaFabbisogno.getIdPrincipalIns().equals(session.getPrincipalId())); // Sono il
																										// proprietario
																										// della scheda
	}

	public boolean isPubblicaMode() {
		return ((ConstantsSingleton.DeStatoFbChecklist.PUBBLICATA).equals(fbSchedaFabbisogno.getDeStatoScheda().getId())
				|| ConstantsSingleton.DeStatoFbChecklist.CHIUSA.equals(fbSchedaFabbisogno.getDeStatoScheda().getId()));
	}

	/**
	 * Posso inserire candidature se sono un ente accreditato, se ho almeno una convenzione protocollata non scaduta, e
	 * se la scheda fabbisogno è pubblicata.
	 */
	public boolean isInsCandidaturaMode() {
		return ConstantsSingleton.DeStatoFbScheda.PUBBLICATA.equals(fbSchedaFabbisogno.getDeStatoScheda().getId())
				&& isEnteAccreditatoAttivo();
	}

	public boolean isEnteAccreditatoAttivo() {
		return isEnteAccreditatoAttivo;
	}

	public boolean isInCandidaturaAllegatoMode(Integer idcandidatura) {
		List<FbCandidaturaAllegatoDTO> candidateAllegatoDTO = new ArrayList<FbCandidaturaAllegatoDTO>();
		candidateAllegatoDTO = fbCandidaturaAllegatoHome.findAllegatoById(idcandidatura);
		if (candidateAllegatoDTO.size() > 0) {
			if (session.isEnteOspitanteCalabria() || session.isEntePromotoreCalabria() || session.isRegione())
				return true;
		}
		return false;
	}

	public boolean isOspitanteMode() {
		return ConstantsSingleton.DeStatoFbScheda.PUBBLICATA.equals(fbSchedaFabbisogno.getDeStatoScheda().getId())
				&& session.isEnteOspitanteCalabria();
	}

	public boolean isOspitante() {
		return session.isEnteOspitanteCalabria();
	}

	/**
	 * Posso chiudere la scheda solo se è già stata pubblicata, se sono un ente ospitante e se ne sono il proprietario.
	 */
	public boolean isChiudiEnabled() {
		return (ConstantsSingleton.DeStatoFbScheda.PUBBLICATA.equals(fbSchedaFabbisogno.getDeStatoScheda().getId())
				&& isEnteOspitante() && session.getPrincipalId().equals(fbSchedaFabbisogno.getIdPrincipalIns()));
	}

	public void onComuneSelected() {
		if (fbSchedaFabbisogno.getDeComune() != null) {
			fbSchedaFabbisogno.setCap(fbSchedaFabbisogno.getDeComune().getCap());
		}
	}

	/**
	 * Tipologia soggetto ospitante
	 */
	public void onAttivitaSelected() {
		deAttivitaMinTemp = null;
		fbSchedaFabbisogno.getFbDatiAzienda().setDeAttivitaMin(null);
	}

	public void onAttivitaMinSelected() {
		fbSchedaFabbisogno.getFbDatiAzienda().setDeAttivitaMin(null);
	}

	public void onNaturaGiuridica() {
		fbSchedaFabbisogno.getFbDatiAzienda().setDeNaturaGiuridicaDTO(null);
	}

	public void onMansioneSelected() {
		fbSchedaFabbisogno.setDeMansioneMin(null);
	}

	/** Funzione autocomplete per la seconda combo DeAttivitaMin: solo i padri, in base al CodAttività scelto */
	public List<DeAttivitaMinDTO> completeDeAttivitaMin(String par) {
		if (fbSchedaFabbisogno.getFbDatiAzienda().getDeAttivita() == null) {
			return deAttivitaMinHome.findBySuggestion(par);
		} else {
			return deAttivitaMinHome.findDTOBySuggestionAndCodAttivita(par,
					fbSchedaFabbisogno.getFbDatiAzienda().getDeAttivita().getId());
		}
	}

	/** Funzione autocomplete per la terza combo DeAttivitaMin: solo i figli, in base al padre scelto */
	public List<DeAttivitaMinDTO> completeDeAttivitaMinFiglio(String par) {
		if (deAttivitaMinTemp == null) {
			return deAttivitaMinHome.findBySuggestion(par);
		} else {
			return deAttivitaMinHome.findDTOBySuggestionAndPadre(par, deAttivitaMinTemp.getId());
		}
	}

	/** Funzione autocomplete per la combo DeMansioneMin: tutti gli elementi, in base al codMansione scelto */
	public List<DeMansioneMinDTO> completeDeMansioneMin(String par) {
		if (fbSchedaFabbisogno.getDeMansione() == null) {
			return deMansioneMinHome.findBySuggestion(par);
		} else {
			return deMansioneMinHome.findDTOBySuggestionAndCodMansione(par, fbSchedaFabbisogno.getDeMansione().getId());
		}
	}

	/** Salvo una scheda fabbisogno con relativi dati azienda */
	public void salva() {
		internalSave();
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	private void internalSave() {
		// Per prima cosa, salvo eventuali dati cambiati su AziendaInfo.
		// aziendaInfo = aziendaInfoHome.mergeDTO(aziendaInfo, session.getPrincipalId());

		// Salvo FbSchedaFabbisogno e poi anche FbDatiAzienda
		FbDatiAziendaDTO fbDatiAziendaDaSalvare = fbSchedaFabbisogno.getFbDatiAzienda();

		if (fbSchedaFabbisogno.getId() == null) {
			fbSchedaFabbisogno = fbSchedaFabbisognoHome.persistDTO(fbSchedaFabbisogno, session.getPrincipalId());
		} else {
			fbSchedaFabbisogno = fbSchedaFabbisognoHome.mergeDTO(fbSchedaFabbisogno, session.getPrincipalId());
		}

		fbDatiAziendaDaSalvare.setIdFbSchedaFabbisogno(fbSchedaFabbisogno.getId());
		if (fbDatiAziendaDaSalvare.getId() == null) {
			fbSchedaFabbisogno
					.setFbDatiAzienda(fbDatiAziendaHome.persistDTO(fbDatiAziendaDaSalvare, session.getPrincipalId()));
		} else {
			fbSchedaFabbisogno
					.setFbDatiAzienda(fbDatiAziendaHome.mergeDTO(fbDatiAziendaDaSalvare, session.getPrincipalId()));
		}

		// Parametro per dire alla pagina di mostrare il dialog di successo
	}

	/** Elimino la scheda fabbisogno (con relativo fbDatiAzienda), e rimando alla pagina della lista */
	public void elimina() {
		if (fbSchedaFabbisogno.getFbDatiAzienda() != null && fbSchedaFabbisogno.getFbDatiAzienda().getId() != null) {
			fbDatiAziendaHome.removeById(fbSchedaFabbisogno.getFbDatiAzienda().getId(), session.getPrincipalId());
		}

		if (fbSchedaFabbisogno.getId() != null) {
			fbSchedaFabbisognoHome.removeById(fbSchedaFabbisogno.getId(), session.getPrincipalId());
		}

		redirect(PATH_SCHEDE_LIST + "?idFbChecklist=" + fbSchedaFabbisogno.getIdFbChecklist());
	}

	/** Pubblico una scheda fabbisogno (solo cambio di stato) */
	public void pubblica() {
		internalSave();
		fbSchedaFabbisogno
				.setDeStatoScheda(deStatoFbSchedaHome.findDTOById(ConstantsSingleton.DeStatoFbScheda.PUBBLICATA));
		fbSchedaFabbisogno.setDtPubblicazione(new Date());
		fbSchedaFabbisogno = fbSchedaFabbisognoHome.mergeDTO(fbSchedaFabbisogno, session.getPrincipalId());

		// Quando una scheda fabbisogno viene pubblicata, la checklist relativa viene chiusa.
		fbChecklistHome.chiudiChecklist(fbSchedaFabbisogno.getIdFbChecklist());
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	public void displayPubblicaDialog() {
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/** Chiudo una scheda fabbisogno (solo cambio di stato) */
	public void chiudi() {
		fbSchedaFabbisogno.setDeStatoScheda(deStatoFbSchedaHome.findDTOById(ConstantsSingleton.DeStatoFbScheda.CHIUSA));
		fbSchedaFabbisogno.setFlgChiusuraManuale(true);
		fbSchedaFabbisogno.setDtChiusura(new Date());
		fbSchedaFabbisogno = fbSchedaFabbisognoHome.mergeDTO(fbSchedaFabbisogno, session.getPrincipalId());
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/** Se sono il soggetto ospitante, vedo tutte le candidature. Se sono il soggetto promotore, vedo solo le mie. */
	public List<FbCandidaturaDTO> getCandidatureVisibili() {
		if (session.isEnteOspitanteCalabria() || session.isRegione()) {

			return fbSchedaFabbisogno.getCandidature();
		} else {
			List<FbCandidaturaDTO> candidatureVisibili = new ArrayList<FbCandidaturaDTO>();
			for (FbCandidaturaDTO candidatura : fbSchedaFabbisogno.getCandidature()) {
				if (candidatura.getIdPrincipalIns().equals(session.getPrincipalId())) {
					candidatureVisibili.add(candidatura);
				}
			}
			return candidatureVisibili;
		}
	}

	public void salvaCandidaturaIdoneo() {
		//
		List<FbCandidaturaDTO> candidaturaList = getCandidatureVisibili();
		for (FbCandidaturaDTO candidatura : candidaturaList) {
			// candidatura.setFlagIdoneo(true);

			candidatura = fbCandidaturaHome.mergeDTO(candidatura, session.getPrincipalId());

		}

		fbSchedaFabbisogno = fbSchedaFabbisognoHome.mergeDTO(fbSchedaFabbisogno, session.getPrincipalId());
		// PEZZA X PROD EMERGENZA
		// fbSchedaFabbisogno = fbSchedaFabbisognoHome.findDTOById(fbSchedaFabbisogno.getId());
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/** Uoload Allegato file */
	public void uploadAllegatoFile(FileUploadEvent event) {

		allegatoUpload = event.getFile();
		allegatoFileName = allegatoUpload.getFileName();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Per confermare l'invio, premere sul pulsante Inserisci nuova candidatura"));

	}

	/** download Allegato file */
	public StreamedContent downloadStampaCandidateAllegato(Integer allegatoid) {

		List<FbCandidaturaAllegatoDTO> all = new ArrayList<FbCandidaturaAllegatoDTO>();
		all = fbCandidaturaAllegatoHome.findAllegatoById(allegatoid);

		InputStream inputStream = new ByteArrayInputStream(all.get(0).getDataFile());
		StreamedContent result = new DefaultStreamedContent(inputStream, all.get(0).getMimeFile(),
				all.get(0).getNomeFile());

		return result;

	}

	public void uploadFileReset() {
		allegatoUpload = null;
		RequestContext.getCurrentInstance().execute("PF('fileUploadWidgetVar').reset();");
	}

	/** Il numero di slot candidatura disponibili è pari al MAX meno quelle già presenti */
	public int getSlotCandidaturaDisponibili() {
		int numCandidature = fbSchedaFabbisogno.getCandidature().size();
		if (numCandidature <= getMaxSlotCandidatura()) {
			return getMaxSlotCandidatura() - numCandidature;
		}
		return 0;
	}

	/** Il numero massimo di slot candidatura è 10 per ogni figura professionale richiesta */
	public int getMaxSlotCandidatura() {
		if (fbSchedaFabbisogno.getNumLavoratori() != null) {
			return MAX_CANDIDATURE_PER_POSIZIONE * fbSchedaFabbisogno.getNumLavoratori();
		}
		return 0;
	}

	/** Salvo una candidatura sul DB, se possibile. */
	public void inserisciCandidatura() {

		checkCandidate = null;
		for (FbCandidaturaDTO candidate : getCandidatureVisibili()) {
			if (candidate.getCodiceFiscale().equals(nuovaCandidatura.getCodiceFiscale())) {
				checkCandidate = candidate.getCodiceFiscale();
				break;
			}
		}
		if (this.checkCandidate == null) {
			if (getSlotCandidaturaDisponibili() > 0) {
				if (allegatoUpload != null) {
					nuovaCandidatura.setIdFbSchedaFabbisogno(fbSchedaFabbisogno.getId());
					nuovaCandidatura = fbCandidaturaHome.persistDTO(nuovaCandidatura, session.getPrincipalId());
					fbCandidaturaAllegatoDTO.setMimeFile(allegatoUpload.getContentType());
					fbCandidaturaAllegatoDTO.setNomeFile(allegatoUpload.getFileName());
					fbCandidaturaAllegatoDTO.setDataFile(allegatoUpload.getContents());
					fbCandidaturaAllegatoDTO.setFbCandidaturaDTO(nuovaCandidatura);
					fbCandidaturaAllegatoDTO = fbCandidaturaAllegatoHome.persistDTO(fbCandidaturaAllegatoDTO,
							session.getPrincipalId());

					fbSchedaFabbisogno.getCandidature().add(nuovaCandidatura);
					nuovaCandidatura = new FbCandidaturaDTO();
					allegatoUpload = null;
					fbCandidaturaAllegatoDTO = new FbCandidaturaAllegatoDTO();
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"L' allegato è obbligatorio  ", "L' allegato è obbligatorio "));
				}
			} else {
				addCustomErrorMessage("Spiacente, sono già state inserite " + getMaxSlotCandidatura()
						+ " candidature per questa Scheda Fabbisogno.");
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Utente già esistente " + this.checkCandidate, "Utente già esistente " + this.checkCandidate));
		}
	}

	/** Elimino una delle mie candidature. */
	// public void eliminaCandidatura(FbCandidaturaDTO candidatura) {
	public void eliminaCandidatura() {
		if (eliminaCandidate.getId() != null) {
			List<FbCandidaturaAllegatoDTO> dto = new ArrayList<FbCandidaturaAllegatoDTO>();
			dto = fbCandidaturaAllegatoHome.findAllegatoById(eliminaCandidate.getId());
			if (dto.size() > 0)
				fbCandidaturaAllegatoHome.removeById(dto.get(0).getId(), session.getPrincipalId());
			fbCandidaturaHome.removeById(eliminaCandidate.getId(), session.getPrincipalId());
			fbSchedaFabbisogno.getCandidature().remove(eliminaCandidate);
			allegatoUpload = null;
			eliminaCandidate = null;
			RequestContext.getCurrentInstance().update("candidateData");
		}
	}

	public FbSchedaFabbisognoDTO getFbSchedaFabbisogno() {
		return fbSchedaFabbisogno;
	}

	public String getIndirizzoEffettivo() {
		if (aziendaInfo != null) {
			if (isProfiloLegaleCompleto()) {
				return aziendaInfo.getSedeLegale().getIndirizzo();
			} else {
				return aziendaInfo.getSedeOperativa().getIndirizzo();
			}
		} else
			return null;
	}

	public DeComuneDTO getComuneEffettivo() {
		if (aziendaInfo != null) {
			if (isProfiloLegaleCompleto()) {
				return aziendaInfo.getSedeLegale().getComune();
			} else {
				return aziendaInfo.getSedeOperativa().getComune();
			}
		} else
			return null;
	}

	public String getCapEffettivo() {
		if (aziendaInfo != null) {
			if (isProfiloLegaleCompleto())
				return aziendaInfo.getSedeLegale().getCap();
			else {
				return aziendaInfo.getSedeOperativa().getCap();
			}
		} else
			return null;
	}

	public boolean isProfiloLegaleCompleto() {

		return (aziendaInfo.getSedeLegale().getIndirizzo() != null && aziendaInfo.getSedeLegale().getCap() != null
				&& aziendaInfo.getSedeLegale().getComune().getId() != null);

	}

	public boolean isProfiloOperativoCompleto() {

		return (aziendaInfo.getSedeOperativa().getIndirizzo() != null && aziendaInfo.getSedeOperativa().getCap() != null
				&& aziendaInfo.getSedeOperativa().getComune().getId() != null);

	}

	public void setFbSchedaFabbisogno(FbSchedaFabbisognoDTO fbSchedaFabbisogno) {
		this.fbSchedaFabbisogno = fbSchedaFabbisogno;
	}

	public AziendaInfoDTO getAziendaInfo() {
		return aziendaInfo;
	}

	public void setAziendaInfo(AziendaInfoDTO aziendaInfo) {
		this.aziendaInfo = aziendaInfo;
	}

	public DeAttivitaMinDTO getDeAttivitaMinTemp() {
		return deAttivitaMinTemp;
	}

	public void setDeAttivitaMinTemp(DeAttivitaMinDTO deAttivitaMinTemp) {
		this.deAttivitaMinTemp = deAttivitaMinTemp;
	}

	public FbCandidaturaDTO getNuovaCandidatura() {
		return nuovaCandidatura;
	}

	public void setNuovaCandidatura(FbCandidaturaDTO nuovaCandidatura) {
		this.nuovaCandidatura = nuovaCandidatura;
	}

	public FbCandidaturaDTO getEliminaCandidate() {
		return eliminaCandidate;
	}

	public void setEliminaCandidate(FbCandidaturaDTO eliminaCandidate) {
		this.eliminaCandidate = eliminaCandidate;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('eliminaCandidateDialogWV').show();");
	}

	public String getCheckCandidate() {
		return checkCandidate;
	}

	public void setCheckCandidate(String checkCandidate) {
		this.checkCandidate = checkCandidate;
	}

	public UploadedFile getAllegatoUpload() {
		return allegatoUpload;
	}

	public void setAllegatoUpload(UploadedFile allegatoUpload) {
		this.allegatoUpload = allegatoUpload;
	}

	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

	public FbCandidaturaAllegatoDTO getFbCandidaturaAllegatoDTO() {
		return fbCandidaturaAllegatoDTO;
	}

	public void setFbCandidaturaAllegatoDTO(FbCandidaturaAllegatoDTO fbCandidaturaAllegatoDTO) {
		this.fbCandidaturaAllegatoDTO = fbCandidaturaAllegatoDTO;
	}

}
