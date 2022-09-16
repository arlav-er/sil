/**
 *
 */
package it.eng.myportal.beans.vacancies;

import static it.eng.myportal.utils.ConstantsSingleton.NO;
import static it.eng.myportal.utils.ConstantsSingleton.SI;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PoiDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.dtos.VaAltreInfoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaEsperienzeDTO;
import it.eng.myportal.dtos.VaVisualizzaDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaSegnalazione;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PoiHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaSegnalazioneHome;
import it.eng.myportal.exception.StatoOccupazionaleException;
import it.eng.myportal.rest.report.GetCurriculumUtente;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.DeProvenienza;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

/**
 * @author iescone, enrico D'Angelo
 */
@ManagedBean
@ViewScoped
public class VisualizzaVacancyBean extends AbstractBaseBean {
	private static final String VACANCY_ID = "vacancyId";

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(VisualizzaVacancyBean.class);

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	PoiHome poiHome;

	@EJB
	VaSegnalazioneHome vaSegnalazioneHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private VaVisualizzaDTO vaVisualizzaDTO;
	private AcCandidaturaDTO candidatura;

	private Long numCandidature;

	private List<SelectItem> myCurricula;
	private List<SelectItem> myClicLavoroCurricula;
	private List<SelectItem> myLettere;

	private Integer vacancyId;

	// private String mapLink;

	/**
	 * Questa variabile viene impostata a 'true' se l'utente ha giÃ  inviato un' autocandidatura.
	 */
	private boolean hasCandidatura;
	private boolean vacancyScaduta;
	private boolean usaDecodificheSil;
	private boolean controlliCrescoSuperati;
	private String controlliCrescoMessage = "";

	private List<CvDatiPersonaliDTO> liveCurricula;
	private StreamedContent xmlReport;

	private String vacancyIdStr;

	// download file zip con curriculum selezionati
	private StreamedContent zipCurriculumSelected;
	public boolean flagDownload = true;
	@EJB
	GetCurriculumUtente getCurriculumUtente;

	// segnalazione candidatura ad un amico
	private String emailAmico;
	private String nominativoAmico;
	private static final String MAIL_INVIATA = "La segnalazione è stata inviata correttamente";

	public void updateDownload() {
		this.flagDownload = true;
		for (AcVisualizzaCandidaturaDTO candidatura : this.getVaVisualizzaDTO().getListAcCandidaturaDTO()) {
			if (candidatura.isFlagCVSelected()) {
				this.flagDownload = false;
				break;
			}
		}
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();
			if (isRedoBySess()) {
				if (vacancyIdStr == null)
					vacancyId = Integer.parseInt(map.get("id"));
				else {
					vacancyId = Integer.parseInt(vacancyIdStr);
				}
			} else {
				vacancyId = Integer.parseInt(map.get("id"));
				putParamsIntoSession();
			}

			vaVisualizzaDTO = vaDatiVacancyHome.findVisualizzaDTOById(vacancyId, getSession().getPrincipalId());
			usaDecodificheSil = VaDatiVacancy.OpzTipoDecodifiche.SIL
					.equals(vaVisualizzaDTO.getVaDatiVacancyDTO().getOpzTipoDecodifiche());

			/*
			 * Le aziende possono vedere soltanto le proprie vacancy
			 */
			if (session.isAzienda()) {
				if (!vaVisualizzaDTO.getVaDatiVacancyDTO().getProprietary()) {
					log.info("L'utente " + session.getPrincipalId()
							+ " (pfPrincipalId) ha tentato di visualizzare la vacancy non di sua proprieta' con id "
							+ vaVisualizzaDTO.getVaDatiVacancyDTO().getId());
					addErrorMessage("vacancy.wrongPermission");
					redirectHome();
				}
			}

			/* Controllo se la VA e' scaduta (eccetto per le province che guardano vacancy cresco) */
			if (!(session.isProvincia() && vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgPacchettoCresco())) {
				Date now = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);

				if ((now).after(vaVisualizzaDTO.getVaDatiVacancyDTO().getScadenzaPubblicazione())) {
					setVacancyScaduta(true);
					String msgText = errorsBean.getProperty("vacancy.scaduta");
					FacesMessage msg = new FacesMessage(msgText);
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}

			/*
			 * se son un cittadino recupero i miei CV, lettere e autocandidatura all'azienda
			 */
			UtenteDTO connectedUtente = getSession().getConnectedUtente();
			if (connectedUtente != null) {
				Integer idPfPrincipal = connectedUtente.getId();
				myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(idPfPrincipal);
				myClicLavoroCurricula = utenteInfoHome.getAllClicLavoroCurriculaAsSelectItem(idPfPrincipal);
				liveCurricula = new ArrayList<CvDatiPersonaliDTO>();
				// if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
				liveCurricula.addAll(utenteInfoHome.getAllCurriculaLiveNoScad(idPfPrincipal));
				liveCurricula.addAll(utenteInfoHome.getAllCurriculaIntermediatiNoScad(idPfPrincipal));
				// }
				// else {
				// liveCurricula.addAll(utenteInfoHome.getAllCurriculaLive(idPfPrincipal));
				// liveCurricula.addAll(utenteInfoHome.getAllCurriculaIntermediati(idPfPrincipal));
				// }

				myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(idPfPrincipal);
				candidatura = acCandidaturaHome.findDTOByUtenteAndAziendaIdPfPrincipal(idPfPrincipal,
						vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAzienda(), vacancyId);

				/*
				 * Verifico che l'utente non si sia già  candidato alla vacancy in questione
				 */
				if (candidatura == null) {
					hasCandidatura = false;
					candidatura = new AcCandidaturaDTO();
					candidatura
							.setIdPfPrincipalAzienda(vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAzienda());
					candidatura.setIdVaDatiVacancy(vaVisualizzaDTO.getVaDatiVacancyDTO().getId());
				} else {
					hasCandidatura = true;
				}
				numCandidature = acCandidaturaHome.getNumAcCandidaturaByIdVacancy(vacancyId);
			}

			// Se la vacancy Ã¨ CRESCO, controllo se posso inviare una candidatura.
			if (session.isUtente() && !hasCandidatura
					&& vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgPacchettoCresco()) {
				boolean controlliCresco = false;

				try {
					controlliCresco = acCandidaturaHome.checkCandidaturaCresco(candidatura, session.getPrincipalId());

					if (!controlliCresco) {
						setControlliCrescoMessage("Non hai i requisiti per candidarti ad un'offerta Cre.s.c.o.<br />"
								+ "Per maggiori informazioni rivolgiti al Centro per l'impiego di riferimento");
						controlliCrescoSuperati = false;
					} else {
						setControlliCrescoMessage("");
						controlliCrescoSuperati = true;
					}
				} catch (StatoOccupazionaleException e) {
					setControlliCrescoMessage("Errore durante la verifica dei requisiti.<br />"
							+ "Per maggiori informazioni rivolgiti al Centro per l'impiego di riferimento");
					controlliCrescoSuperati = false;
				}
			} else {
				// Se la vacancy non Ã¨ CRESCO, i controlli sono automaticamente ok.
				controlliCrescoSuperati = true;
			}

		} catch (EJBException e) {
			gestisciErrore(e, "vacancy.error_loading");
			redirectHome();
		} catch (NumberFormatException e) {
			log.error("Errore di manipolazione dei parametri in ingresso.");
			redirectGrave("generic.format_error");
		}
		log.debug("Costruito il Bean per visualizzare le Vacancy completo!");
	}

	public Double getMapLon() {
		Integer idPOI = vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPoi();
		if (idPOI != null) {
			PoiDTO poiDTO = poiHome.findDTOById(idPOI);
			return poiDTO.getLon();
		}
		return null;
	}

	public Double getMapLat() {
		Integer idPOI = vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPoi();
		if (idPOI != null) {
			PoiDTO poiDTO = poiHome.findDTOById(idPOI);
			return poiDTO.getLat();
		}
		return null;
	}

	public boolean renderedModifica() {
		
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			if (!vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgIdo()) {
				return false;
			}
		}

		if (vaVisualizzaDTO.getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.ARC))
			return false;
		else
			return true;
	}

	public boolean renderedCercaCompatibili() {
		if (utils.isPAT()) {
			if (vaVisualizzaDTO.getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.LAV)) {
				return false;
			} else {
				return true;
			}
		} else {
			
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				if (!vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgIdo()) {
					return false;
				}
			}

			return true;
		}
	}

	public String getMapZoom() {
		return ConstantsSingleton.OpenStreetMap.ZOOM;
	}

	public boolean isCandidaturaEnabled() {
		return vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgCandidatura();
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
					vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAzienda(), vacancyId, file);
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

	public void addCrescoErrorMessage() {
		addCustomErrorMessage(controlliCrescoMessage);
	}

	/** Mostro la sezione "requisiti aggiuntivi" solo se dentro ho qualcosa da mostrare */
	public boolean mostraRequisitiAggiuntivi() {
		return vaVisualizzaDTO.getVaPubblicazioneDTO() != null
				&& (!Utils.isStringEmpty(vaVisualizzaDTO.getVaPubblicazioneDTO().getCaratteristiche())
						|| !Utils.isStringEmpty(vaVisualizzaDTO.getVaPubblicazioneDTO().getFormazione())
						|| !Utils.isStringEmpty(vaVisualizzaDTO.getVaPubblicazioneDTO().getConoscenze()));
	}

	/** Mostro la sezione "altro" nella tab "rapporto di lavoro" solo se dentro ho qualcosa da mostrare */
	public boolean mostraRapportoLavoroAltro() {
		return vaVisualizzaDTO.getVaPubblicazioneDTO() != null
				&& (!Utils.isStringEmpty(vaVisualizzaDTO.getVaPubblicazioneDTO().getContratto())
						|| !Utils.isStringEmpty(vaVisualizzaDTO.getVaPubblicazioneDTO().getOrario()));
	}

	/** Mostro la sezione "altre infomazioni" solo se dentro ho qualcosa da mostrare */
	public boolean mostraAltreInfo() {
		if (vaVisualizzaDTO.getVaAltreInfoDTO() != null) {
			VaAltreInfoDTO altreInfo = vaVisualizzaDTO.getVaAltreInfoDTO();
			if (!Utils.isStringEmpty(altreInfo.getFuorisede()) || !Utils.isStringEmpty(altreInfo.getAutomunito())
					|| !Utils.isStringEmpty(altreInfo.getMotomunito()) || !Utils.isStringEmpty(altreInfo.getVitto())
					|| !Utils.isStringEmpty(altreInfo.getAlloggio())
					|| !Utils.isStringEmpty(altreInfo.getUlterioriRequisiti())) {
				return true;
			}

			if (altreInfo.getDeGenere() != null && !Utils.isStringEmpty(altreInfo.getDeGenere().getDescrizione())) {
				return true;
			}
		}

		if (vaVisualizzaDTO.getVaEsperienzeDTO() != null) {
			VaEsperienzeDTO esperienze = vaVisualizzaDTO.getVaEsperienzeDTO();
			if (esperienze.getNumDa() != null || esperienze.getNumA() != null
					|| !Utils.isStringEmpty(esperienze.getOpzEsperienza())
					|| !Utils.isStringEmpty(esperienze.getOpzFormazione())) {
				return true;
			}
		}

		if (vaVisualizzaDTO.getListaVaAgevolazioneDTO() != null
				&& !vaVisualizzaDTO.getListaVaAgevolazioneDTO().isEmpty()) {
			return true;
		}

		if (mostraRequisitiAggiuntivi()) {
			return true;
		}

		return false;
	}

	/**
	 * La lista di candidature (se presente) puÃ² essere vista da tutti per le offerte Cresco, e solo dalle aziende per
	 * le offerte non-Cresco.
	 */
	public boolean showListaCandidature() {
		boolean canView = !session.isUtente()
				&& (session.isAzienda() || vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgPacchettoCresco());
		return canView && vaVisualizzaDTO.getListAcCandidaturaDTO() != null
				&& !vaVisualizzaDTO.getListAcCandidaturaDTO().isEmpty();
	}

	/**
	 * Il report Excel sulle candidature puÃ² essere scaricato solo dagli utenti PROVINCIA, solo per le offerte CRESCO.
	 */
	public boolean showReportDownloadButton() {
		return session.isProvincia() && vaVisualizzaDTO.getVaDatiVacancyDTO().getFlgPacchettoCresco()
				&& vaVisualizzaDTO.getListAcCandidaturaDTO() != null
				&& !vaVisualizzaDTO.getListAcCandidaturaDTO().isEmpty();
	}

	/**
	 * Genera e restituisce il report delle candidature.
	 */
	public StreamedContent getXmlReport() {
		if (xmlReport == null) {
			try {
				ByteArrayInputStream stream = acCandidaturaHome.generaXlsListaCandidature(vaVisualizzaDTO);
				xmlReport = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "reportCandidature.xls");
			} catch (IOException e) {
				log.error("Errore durante la generazione del report");
				addCustomErrorMessage("Errore durante la generazione del report");
			}
		}

		return xmlReport;
	}

	public StreamedContent getZipCurriculumSelected() throws IOException {
		ByteArrayInputStream stream = null;
		try {
			stream = this.generaZipListaCandidature();
			zipCurriculumSelected = new DefaultStreamedContent(stream, "application/zip", "zipCurriculum.zip");

		} catch (IOException e) {
			log.error("Errore durante la generazione del file zip");
			addCustomErrorMessage("Errore durante la generazione del file zip");
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return zipCurriculumSelected;
	}

	private ByteArrayInputStream generaZipListaCandidature() throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ByteArrayInputStream bis = null;
		ByteArrayInputStream bisZip = null;
		int number_of_cv = 1;
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		for (AcVisualizzaCandidaturaDTO candidatura : this.getVaVisualizzaDTO().getListAcCandidaturaDTO()) {
			if (candidatura.isFlagCVSelected()) {
				Response response = getCurriculumUtente.getCurriculumUtente(candidatura.getIdCvDatiPersonali(),
						request);
				bis = (ByteArrayInputStream) response.getEntity();
				out.putNextEntry(new ZipEntry("cv_per_candidatura_" + candidatura.getCognomeCandidato() + "_"
						+ candidatura.getNomeCandidato() + "_" + number_of_cv + ".pdf"));
				IOUtils.copy(bis, out);
				bis.close();
				out.flush();
				out.closeEntry();
				number_of_cv++;
			}
		}
		if (out != null) {
			out.finish();
			out.flush();
			IOUtils.closeQuietly(out);
		}
		if (bis != null) {
			bis.close();
		}
		bisZip = new ByteArrayInputStream(baos.toByteArray());

		if (baos != null) {
			baos.close();
		}
		return bisZip;
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(VACANCY_ID, vacancyId);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		vacancyIdStr = ObjectUtils.toString(restoreParameters.get(VACANCY_ID));
	}

	public String getRagioneSocialeLabel() {
		if (vaVisualizzaDTO == null || vaVisualizzaDTO.getVaDatiVacancyDTO() == null
				|| vaVisualizzaDTO.getVaDatiVacancyDTO().getDeProvenienzaVacancyDTO() == null)
			return "";

		if (DeProvenienza.COD_MYPORTAL
				.equalsIgnoreCase(vaVisualizzaDTO.getVaDatiVacancyDTO().getDeProvenienzaVacancyDTO().getId())
				|| vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAziendaPalese() != null) {
			return "Ragione sociale:";
		} else {
			return "CPI di riferimento:";
		}
	}

	public String getEsperienzaRichiestaDesc() {
		if (vaVisualizzaDTO != null) {
			VaDatiVacancyDTO datiVacancy = vaVisualizzaDTO.getVaDatiVacancyDTO();
			String esperienzaRichiesta = datiVacancy.getEsperienzaRichiesta();
			if (SI.equalsIgnoreCase(esperienzaRichiesta)) {
				return "Si";
			}
			if (NO.equalsIgnoreCase(esperienzaRichiesta)) {
				return "No";
			}
		}
		return "Indifferente";
	}

	/**
	 * Invia la segnalazione di una candidatura all'email di un amico.
	 */
	public void segnalaCandidatura() {
		// Controllo segnalazione giÃ  inviata
		VaSegnalazione segnalazione = vaSegnalazioneHome.findSegnalazioneByMailIdVacMitt(emailAmico, vacancyId,
				session.getPrincipalId());
		if (segnalazione != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Attenzione, hai giÃ  inviato la tua segnalazione all'indirizzo email indicato", ""));
			return;
		}

		// Se non inviata eseguo la procedura di invio email e salvataggio
		EmailDTO emailDTO = EmailDTO.buildSegnalaCandidaturaEmail(session.getConnectedUtente(), nominativoAmico,
				emailAmico, vacancyId, vaVisualizzaDTO.getVaDatiVacancyDTO().getAttivitaPrincipale());
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);
		segnalazione = new VaSegnalazione();
		segnalazione.setNominativo(nominativoAmico);
		segnalazione.setEmail(emailAmico);
		segnalazione.setPfPrincipal(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setPfPrincipalIns(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setPfPrincipalMod(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setIdVaDatiVacancy(this.vacancyId);
		Date now = new Date();
		segnalazione.setDataInvio(now);
		segnalazione.setDtmIns(now);
		segnalazione.setDtmMod(now);

		vaSegnalazioneHome.persist(segnalazione);

		log.info("- Mail inviata: {\n" + emailDTO.toString() + "}");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MAIL_INVIATA));
	}

	private static String getTextMailFromTemplate(InputStream is) {
		BufferedReader reader;
		String testo = "";
		try {
			InputStreamReader r = new InputStreamReader(is);
			reader = new BufferedReader(r);

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			testo = sb.toString();
		} catch (Exception e) {
			log.error("errore nel recupero del template mail html");
		}
		return testo;
	}

	private static String substitutorPlaceHolder(InputStream is, Map<String, String> values) {
		String testoMail = getTextMailFromTemplate(is);
		StrSubstitutor sub = new StrSubstitutor(values, "{{", "}}");

		return sub.replace(testoMail);
	}

	public VaVisualizzaDTO getVaVisualizzaDTO() {
		return vaVisualizzaDTO;
	}

	public void setVaVisualizzaDTO(VaVisualizzaDTO vaVisualizzaDTO) {
		this.vaVisualizzaDTO = vaVisualizzaDTO;
	}

	public AcCandidaturaDTO getCandidatura() {
		return candidatura;
	}

	public void setCandidatura(AcCandidaturaDTO candidatura) {
		this.candidatura = candidatura;
	}

	public List<SelectItem> getMyCurricula() {
		return myCurricula;
	}

	public void setMyCurricula(List<SelectItem> myCurricula) {
		this.myCurricula = myCurricula;
	}

	public List<SelectItem> getMyLettere() {
		return myLettere;
	}

	public void setMyLettere(List<SelectItem> myLettere) {
		this.myLettere = myLettere;
	}

	public boolean isHasCandidatura() {
		return hasCandidatura;
	}

	public void setHasCandidatura(boolean hasCandidatura) {
		this.hasCandidatura = hasCandidatura;
	}

	public boolean isVacancyScaduta() {
		return vacancyScaduta;
	}

	public void setVacancyScaduta(boolean vacancyScaduta) {
		this.vacancyScaduta = vacancyScaduta;
	}

	public Long getNumCandidature() {
		return numCandidature;
	}

	public void setNumCandidature(Long numC) {
		this.numCandidature = numC;
	}

	public Integer getVacancyId() {
		return vacancyId;
	}

	public void setVacancyId(Integer vacancyId) {
		this.vacancyId = vacancyId;
	}

	public List<CvDatiPersonaliDTO> getLiveCurricula() {
		return liveCurricula;
	}

	public void setLiveCurricula(List<CvDatiPersonaliDTO> liveCurricula) {
		this.liveCurricula = liveCurricula;
	}

	public List<SelectItem> getMyClicLavoroCurricula() {
		return myClicLavoroCurricula;
	}

	public void setMyClicLavoroCurricula(List<SelectItem> myClicLavoroCurricula) {
		this.myClicLavoroCurricula = myClicLavoroCurricula;
	}

	public boolean usaDecodificheSil() {
		return usaDecodificheSil;
	}

	public boolean isControlliCrescoSuperati() {
		return controlliCrescoSuperati;
	}

	public void setControlliCrescoSuperati(boolean controlliCrescoSuperati) {
		this.controlliCrescoSuperati = controlliCrescoSuperati;
	}

	public String getControlliCrescoMessage() {
		return controlliCrescoMessage;
	}

	public void setControlliCrescoMessage(String controlliCrescoMessage) {
		this.controlliCrescoMessage = controlliCrescoMessage;
	}

	public boolean isFlagDownload() {
		return flagDownload;
	}

	public void setFlagDownload(boolean flagDownload) {
		this.flagDownload = flagDownload;
	}

	public String getEmailAmico() {
		return emailAmico;
	}

	public void setEmailAmico(String emailAmico) {
		this.emailAmico = emailAmico;
	}

	public String getNominativoAmico() {
		return nominativoAmico;
	}

	public void setNominativoAmico(String nominativoAmico) {
		this.nominativoAmico = nominativoAmico;
	}

	@Override
	public String getBackTo() {
		String backTo = super.getBackTo();
		if (utils.isPAT()
				&& CodStatoVacancyEnum.ARC.equals(vaVisualizzaDTO.getVaDatiVacancyDTO().getCodStatoVacancyEnum())
				&& backTo.contains("edit")) {
			String str[] = backTo.split("\\?");
			return str[0].replace("vacancies/edit", "home");
		} else {
			return backTo;
		}
	}

	public boolean isPubblicata() {
		if (vaVisualizzaDTO.getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			return true;
		} else {
			return false;
		}
	}

	public String welcomepageEndpoint() {
		return ConstantsSingleton.getWelcomepageEndpoint() + "/vacancy/view/" + vacancyId;
	}

	public boolean isScadenza() {
		if (vaVisualizzaDTO.getVaDatiVacancyDTO().getScadenzaPubblicazione() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			long diffInMillies, diff = 0;

			try {
				// Date scadezaDate = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(getDtScadenza()));
				Date nowDate = sdf.parse(sdf.format(new Date()));
				diffInMillies = vaVisualizzaDTO.getVaDatiVacancyDTO().getScadenzaPubblicazione().getTime()
						- nowDate.getTime();
				diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (diff >= 0)
				return true;
			else
				return false;
		} else
			return false;
	}
}
