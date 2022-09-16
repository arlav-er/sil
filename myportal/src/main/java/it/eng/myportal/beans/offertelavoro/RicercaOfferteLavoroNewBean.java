package it.eng.myportal.beans.offertelavoro;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.AcCandidaturaDatiDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.GenericFiltroDecodeDTO;
import it.eng.myportal.dtos.RvGroupDTO;
import it.eng.myportal.dtos.RvRicercaVacancyDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.VaSegnalazione;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.ejb.InvioNewsletterSync;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaSegnalazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneSolrHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.StatoOccupazionaleException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della pagina che xpermette di cercare tra le offerte di lavoro
 * 
 */
@ManagedBean
@ViewScoped
public class RicercaOfferteLavoroNewBean extends AbstractEditableBean<RvTestataDTO> {
	private static Log log = LogFactory.getLog(RicercaOfferteLavoroBean.class);
	private static final String PATH_GEOSERVER = "&fl=id_va_dati_vacancy,descrizione,comune,longitudine,latitudine";

	private VaDatiVacancyDTO vaDatiVacancyDTO;

	/* elenco delle vacancy risultato della ricerca */
	private List<RvRicercaVacancyDTO> offerteLavoro;

	private List<SelectItem> myCurricula;
	private List<SelectItem> myClicLavoroCurricula;
	private List<SelectItem> myLettere;



	/*
	 * Parametro hidden utilizzata lato pagina, per passare la queryString di SOLR al plugin della mappa
	 */
	private String ParamSOLR;

	private Boolean disableCandidatura;

	private transient AcCandidaturaDTO candidatura;

	DeSezioneInfoDTO info;

	// confermaCandidatura params
	private Integer idVaDatiVacancy;
	private Integer idAziendaInfo;
	private Boolean fromClicLavoro;

	// Filtro usato per il pacchetto Cresco dell'Umbria.
	// Non c'è un'opzione null, o prendo SOLO le offerte Cresco, oppure SOLO quelle non-Cresco.
	private boolean flgCresco;
	private String flgContadino = null;;
	private Boolean controlliCrescoSuperati = null; // Se l'offerta non è CRESCO, è come se fossero superati
	private String controlliCrescoMessage = "";
	private AcCandidaturaDTO dummyCandidaturaDatiSil = null;
	private Boolean filtraDataScadenza = true;

	// liste dei filtri di II livello
//List<DeBpMansione> listSelectedMansioniContadini = new ArrayList<DeBpMansione>();
	List<DeMansione> listSelectedMansioni = new ArrayList<DeMansione>();
	List<DeContratto> listSelectedContratti = new ArrayList<DeContratto>();
	List<DeOrario> listSelectedOrari = new ArrayList<DeOrario>();
	List<DeAttivita> listSelectedAttivita = new ArrayList<DeAttivita>();
	List<DeLingua> listSelectedLingue = new ArrayList<DeLingua>();
	List<DeTitolo> listSelectedTitoli = new ArrayList<DeTitolo>();
	List<DePatente> listSelectedPatenti = new ArrayList<DePatente>();

	// filtri sulle decodifiche SIL
	List<DePatenteSil> listSelectedPatentiSil = new ArrayList<DePatenteSil>();
	List<DeOrarioSil> listSelectedOrariSil = new ArrayList<DeOrarioSil>();
	List<DeContrattoSil> listSelectedContrattiSil = new ArrayList<DeContrattoSil>();

	// lista filtri da visualizzare come button
	List<GenericFiltroDecodeDTO> listFiltriButton = new ArrayList<GenericFiltroDecodeDTO>();

	private Boolean checkButtonClean;

	private String idxAccordionOpen;

	private PaginationHandler paginationHandler;

	// segnalazione candidatura ad un amico
	private String emailAmico;
	private String nominativoAmico;
	private RvRicercaVacancyDTO selVacancy;
	private static final String MAIL_INVIATA = "La segnalazione è stata inviata correttamente";

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeBpMansioneHome deBpMansioneHome;
	
	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	transient RvRicercaVacancyHome rvRicercaVacancyHome;

	@EJB
	transient RvTestataHome rvTestataHome;

	@EJB
	transient VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	transient VaSegnalazioneHome vaSegnalazioneHome;

	@EJB
	transient AcCandidaturaHome acCandidaturaHome;

	@EJB
	transient UtenteInfoHome utenteInfoHome;

	@EJB
	DeComuneSolrHome deComuneSolrHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	private InvioNewsletterSync invioNewsletterSync;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private UploadedFile file;
	
	@ManagedProperty(value = "#{deMansioneBean}")
	protected DeMansioneBean deMansioneBean;

	/**
	 * recupera l'elenco delle Vacancy e dei Template dal DB ed inizializza il Bean
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".postConstruct");
		try {
			super.postConstruct();
			//RestoreParameters ret = super.generateRestoreParams();
			info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_RICERCA_VACANCY_COSA);

			// Controllo se sono arrivato alla pagina tramite la portlet per il pacchetto Cresco (solo Umbria)
			if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.containsKey("flgcresco")) {
				flgCresco = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
						.get("flgcresco").equalsIgnoreCase("true");
				// Solo nel caso del pacchetto Cresco, le province ignorano la data di scadenza.
				if (session.isProvincia()) {
					filtraDataScadenza = null;
				}
			}

			log.debug("Costruito il Bean per visualizzare l'elenco delle Vacancy secondo i criteri di ricerca.");
		} finally {
			jamonMonitor.stop();
		}
	}
	

	
	public String decodeFiltroButtonCutDescrizione(GenericFiltroDecodeDTO filtroButton) {
		UtilsBean utilsBean = new UtilsBean();
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			if (filtroButton.getTipoCodifica().equalsIgnoreCase("orario")) {
				return decodeFiltroButton(filtroButton);
			} else {
				return utilsBean.cutDescrizione(filtroButton.getDescrizione(), 30);
			}
		} else {
			return utilsBean.cutDescrizione(filtroButton.getDescrizione(), 30);
		}
	}
			
	public String decodeFiltroButton(GenericFiltroDecodeDTO filtroButton) {
		String descrizione = "";
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			if (filtroButton.getTipoCodifica().equalsIgnoreCase("orario")) {
				if (filtroButton.getId().equalsIgnoreCase("F")) {
					descrizione = "Full Time";
				} else if (filtroButton.getId().equalsIgnoreCase("P"))
					descrizione = "Part Time";
			} else {
				descrizione = filtroButton.getDescrizione();
			}
		} else {
			descrizione = filtroButton.getDescrizione();
		}
		return descrizione;
	}

	/** Faccio i controlli CRESCO, salvando il risultato in modo da chiamare i WS SIL solo la prima volta */
	public void eseguiControlliCresco() {
		if (controlliCrescoSuperati == null) {
			// Se non sto cercando offerte CRESCO, non ho bisogno di fare i controlli
			if (!flgCresco) {
				controlliCrescoSuperati = true;
			} else {
				// Se sto cercando offerte Cresco, devo anche fare i controlli sullo stato occupazionale.
				try {
					dummyCandidaturaDatiSil = new AcCandidaturaDTO();
					controlliCrescoSuperati = acCandidaturaHome.checkCandidaturaCresco(dummyCandidaturaDatiSil,
							session.getPrincipalId());

					if (!controlliCrescoSuperati) {
						setControlliCrescoMessage("Non hai i requisiti per candidarti ad un'offerta Cre.s.c.o. <br />"
								+ "Per maggiori informazioni rivolgiti al Centro per l'impiego di riferimento");
					}
				} catch (StatoOccupazionaleException e) {
					log.error("Errore durante il controllo stato occupazionale per Cresco: " + e.toString());
					setControlliCrescoMessage("Errore durante la verifica dei requisiti. <br />"
							+ "Per maggiori informazioni rivolgiti al Centro per l'impiego di riferimento");
					controlliCrescoSuperati = false;
				}
			}
		}

		RequestContext.getCurrentInstance().addCallbackParam("crescoSuperati", controlliCrescoSuperati);
	}

	/** L'opzione di mostrare le vacancy scadute è solo per le province, nel caso di ricerche CRESCO */
	public boolean mostraFiltraScaduteSelect() {
		return session.isProvincia() && flgCresco;
	}

	/**
	 * Salva i Filtri di ricerca
	 */
	public void saveFilter() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".saveFilter");
		try {
			if (session.isUtente()) {
				data.setIdPfPrincipal(session.getConnectedUtente().getId());

				/*
				 * Annullo l'id del filtro in modo che gliene venga dato uno nuovo
				 */
				data.setId(null);
				saveData();

				addInfoMessage("data.created");

			} else {
				addErrorMessage("data.error_saving");
			}
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Aggiorna i Filtri di ricerca
	 */
	public void updateFilter() {
		if (session.isUtente()) {
			data.setIdPfPrincipal(session.getConnectedUtente().getId());

			updateData();
			addInfoMessage("data.created");
		} else {
			addErrorMessage("data.error_saving");
		}
	}

	public void controlloEsistenzaSegnalazione() {

		VaSegnalazione segnalazione = vaSegnalazioneHome.findSegnalazioneByMailIdVacMitt(emailAmico,
				this.getIdVaDatiVacancy(), session.getPrincipalId());
		if (segnalazione == null) {

			RequestContext.getCurrentInstance().addCallbackParam("segnalaSuperato", true);
		} else {

			RequestContext.getCurrentInstance().addCallbackParam("segnalaSuperato", false);
			addErrorMessage("Attenzione, hai già inviato la tua segnalazione all'indirizzo email indicato");
			RequestContext.getCurrentInstance().update("globalMessages");
		}

	}

	private void resettaCaricamento() {
		data.setRowsLoaded(0);
		data.setRowsForLoad(data.getRowsToLoad());
		data.setRowsTotal(0);
		data.setCurrentPage(1);
	}

	@Override
	protected void saveData() {
		setFiltriGroup();
		data = homePersist(rvTestataHome, data);
	}

	@Override
	protected void updateData() {
		setFiltriGroup();
		data = homeMerge(rvTestataHome, data);
	}

	@Override
	protected RvTestataDTO buildNewDataIstance() {
		return new RvTestataDTO();
	}

	private List<AcCandidaturaDatiDTO> getNuovaCandidaturaCrescoDati() {
		List<AcCandidaturaDatiDTO> result = new ArrayList<AcCandidaturaDatiDTO>();
		for (AcCandidaturaDatiDTO acCandDatiDummy : dummyCandidaturaDatiSil.getAcCandidaturaDatiList()) {
			AcCandidaturaDatiDTO acCandDati = new AcCandidaturaDatiDTO();
			acCandDati.setCodStatoOccupazionale(acCandDatiDummy.getCodStatoOccupazionale());
			acCandDati.setDescStatoOccupazionale(acCandDatiDummy.getDescStatoOccupazionale());
			acCandDati.setDeProvenienzaDTO(acCandDatiDummy.getDeProvenienzaDTO());
			acCandDati.setDataDichiarazione(acCandDatiDummy.getDataDichiarazione());
			acCandDati.setFlagDisabile(acCandDatiDummy.getFlagDisabile());
			acCandDati.setFlagIntermittente(acCandDatiDummy.getFlagIntermittente());
			acCandDati.setListeSpeciali(acCandDatiDummy.getListeSpeciali());
			result.add(acCandDati);
		}
		return result;
	}

	@Override
	protected RvTestataDTO retrieveData() {
/*
		if(beanParamsSess != null){
			if(beanParamsSess.containsKey("data")){
				Object obj = beanParamsSess.get("data");
				data  = (RvTestataDTO) obj;
			}
		}
*/
		if (data != null && beanParamsSess != null) {
			log.debug("dati recuperati dalla sessione.");

			// dopo la ricerca il contatore viene incrementato,
			// in caso di ri-caricamento by sessione lo decremento
			data.setRowsForLoad(data.getRowsForLoad() - data.getRowsToLoad());

			if (session.isUtente()) {
				data.setIdPfPrincipal(session.getConnectedUtente().getId());
				if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
					myCurricula = utenteInfoHome.getAllCurriculaAsSelectItemIdoRER(getSession().getConnectedUtente().getId());
				} else {
					myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(getSession().getConnectedUtente().getId());
				}
				myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(getSession().getConnectedUtente().getId());
				candidatura = new AcCandidaturaDTO();				
				if(chekRER()) {
		        	candidatura.setCommento(ConstantsSingleton.Candidatura.TXT_MSG_INVIA_CANDIDATURA);
		        }
			} else if (session.isProvincia()) {
				data.setIdPfPrincipal(session.getConnectedProvincia().getId());
			}

			// richiamo la ricerca delle vacancy in base a tutti i filtri
			// salvati
			search();
			return data;
		}

		if (session.isUtente() || session.isProvincia()) {
			Map<String, String> map = getRequestParameterMap();
			try {
				String idString = map.get("id");
				if (idString != null) {
					/*
					 * se mi viene passato l'id di un filtro utilizzo quello
					 */
					int rvTestataId = Integer.parseInt(idString);
					log.debug("dati recuperati con retrieve data.");
					data = rvTestataHome.findDTOById(rvTestataId);

					init(data);

					// aggiorno la dtmMod dell'entity, utile a tener traccia
					// dell'ultima consultazione
					rvTestataHome.mergeDtmMod(rvTestataId);

					putParamsIntoSession();
				} else {
					data = new RvTestataDTO();
				}
				if (session.isUtente()) {
					data.setIdPfPrincipal(session.getConnectedUtente().getId());
					if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
						myCurricula = utenteInfoHome.getAllCurriculaAsSelectItemIdoRER(getSession().getConnectedUtente().getId());
					}else {
						myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(getSession().getConnectedUtente().getId());	
					}

					myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(getSession().getConnectedUtente().getId());
					candidatura = new AcCandidaturaDTO();
					if(chekRER()) {
			        	candidatura.setCommento(ConstantsSingleton.Candidatura.TXT_MSG_INVIA_CANDIDATURA);
			        }
				} else if (session.isProvincia()) {
					data.setIdPfPrincipal(session.getConnectedProvincia().getId());
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("Id non corretto");
			}
		} else {
			log.warn("Tentativo di accedere alla sezione utente.");
			redirectHome();
		}

		return data;
	}

	public void init(RvTestataDTO testata) {

		/* Resetto i contatori */
		resettaCaricamento();

		RvGroupDTO rvGroupDTODB = rvTestataHome.loadGruppi(testata);

		Map<String, Boolean> mansioniMap = rvGroupDTODB.getCheckMansione();
		Set<String> mansioni = mansioniMap.keySet();
		for (Iterator<String> iterator = mansioni.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();

			DeMansione mans = deMansioneHome.findById(id);
			listSelectedMansioni.add(mans);
			GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(mans.getCodMansione(),
					deMansioneHome.findById(mans.getCodMansione()).getDescrizione(), "mansione");
			listFiltriButton.add(decodeDto);
		}

		Map<String, Boolean> contrattiMap = rvGroupDTODB.getCheckContratto();
		Set<String> contratti = contrattiMap.keySet();
		for (Iterator<String> iterator = contratti.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				DeContrattoSil cont = deContrattoSilHome.findById(id);
				listSelectedContrattiSil.add(cont);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(cont.getCodContrattoSil(),
						deContrattoSilHome.findById(cont.getCodContrattoSil()).getDescrizione(), "contratto");
				listFiltriButton.add(decodeDto);
			} else {
				DeContratto cont = deContrattoHome.findById(id);
				listSelectedContratti.add(cont);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(cont.getCodContratto(),
						deContrattoHome.findById(cont.getCodContratto()).getDescrizione(), "contratto");
				listFiltriButton.add(decodeDto);
			}
		}

		Map<String, Boolean> orarioMap = rvGroupDTODB.getCheckOrario();
		Set<String> orari = orarioMap.keySet();
		for (Iterator<String> iterator = orari.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				DeOrarioSil ora = deOrarioSilHome.findById(id);
				listSelectedOrariSil.add(ora);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(ora.getCodOrarioSil(),
						deOrarioSilHome.findById(ora.getCodOrarioSil()).getDescrizione(), "orario");
				listFiltriButton.add(decodeDto);
			} else {
				DeOrario ora = deOrarioHome.findById(id);
				listSelectedOrari.add(ora);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(ora.getCodOrario(),
						deOrarioHome.findById(ora.getCodOrario()).getDescrizione(), "orario");
				listFiltriButton.add(decodeDto);
			}
		}

		Map<String, Boolean> attivitaMap = rvGroupDTODB.getCheckSettore();
		Set<String> attivita = attivitaMap.keySet();
		for (Iterator<String> iterator = attivita.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			DeAttivita att = deAttivitaHome.findById(id);
			listSelectedAttivita.add(att);
			GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(att.getCodAteco(),
					deAttivitaHome.findById(att.getCodAteco()).getDescrizione(), "attivita");
			listFiltriButton.add(decodeDto);
		}

		Map<String, Boolean> linguaMap = rvGroupDTODB.getCheckLingua();
		Set<String> lingua = linguaMap.keySet();
		for (Iterator<String> iterator = lingua.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			DeLingua lin = deLinguaHome.findById(id);
			listSelectedLingue.add(lin);
			GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(lin.getCodLingua(),
					deLinguaHome.findById(lin.getCodLingua()).getDenominazione(), "lingua");
			listFiltriButton.add(decodeDto);
		}

		Map<String, Boolean> titoloMap = rvGroupDTODB.getCheckTitoloStudio();
		Set<String> titolo = titoloMap.keySet();
		for (Iterator<String> iterator = titolo.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			DeTitolo tit = deTitoloHome.findById(id);
			listSelectedTitoli.add(tit);
			GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(tit.getCodTitolo(),
					deTitoloHome.findById(tit.getCodTitolo()).getDescrizioneParlante(), "titolo");
			listFiltriButton.add(decodeDto);
		}

		Map<String, Boolean> patenteMap = rvGroupDTODB.getCheckPatente();
		Set<String> patente = patenteMap.keySet();
		for (Iterator<String> iterator = patente.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				DePatenteSil pat = dePatenteSilHome.findById(id);
				listSelectedPatentiSil.add(pat);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatenteSil(),
						// errore ereditato!!!!
						//dePatenteHome.findById(pat.getCodPatenteSil()).getDescrizione(), "patente");
				        dePatenteSilHome.findById(pat.getCodPatenteSil()).getDescrizione(), "patente");
				listFiltriButton.add(decodeDto);
			} else {
				DePatente pat = dePatenteHome.findById(id);
				listSelectedPatenti.add(pat);
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatente(),
						dePatenteHome.findById(pat.getCodPatente()).getDescrizione(), "patente");
				listFiltriButton.add(decodeDto);
			}
		}
		if (utils.isRER()) {
			Map<String, Boolean> agricoloMap = rvGroupDTODB.getCheckAgricolo();
			Set<String> agricolo = agricoloMap.keySet();
			for (Iterator<String> iterator = agricolo.iterator(); iterator.hasNext();) {
				String id = (String) iterator.next();
				GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO("Y", id, "contadino");
				flgContadino = "Y";
				listFiltriButton.add(decodeDto);
			}
		}
		// richiamo la ricerca delle vacancy in base a tutti i filtri salvati
		searchVacancyFilter();
	}

	/**
	 * Costruisce la Vacancy partendo dall'ID
	 */
	public void viewDatiAziendaVacancy() {
		Map<String, String> map = getRequestParameterMap();
		String idString = map.get("id");
		setVaDatiVacancyDTO(vaDatiVacancyHome.findDTOById(Integer.parseInt(idString)));
		// Setto i campi relativi alla candidatura
		candidatura.setIdVaDatiVacancy(getVaDatiVacancyDTO().getId());
	}

	public void handleFileUpload(FileUploadEvent event) {
		this.setFile(event.getFile());
	}

	public void deleteFile() {
		this.setFile(null);
	}

	
	
	/**
	 * Registra l'invio della candidatura relativa alla vacancy selezionata.
	 */
	public void inviaCandidatura() {
		setVaDatiVacancyDTO(vaDatiVacancyHome.findDTOById(getIdVaDatiVacancy()));
		// Setto i campi opzionali per CRESCO
		if (flgCresco && dummyCandidaturaDatiSil != null) {
			candidatura.setAcCandidaturaDatiList(getNuovaCandidaturaCrescoDati());
		}

		if (!getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			addErrorMessage("vacancy.error_not_present");
			return;
		}

		// Setto i campi relativi alla candidatura
		candidatura.setIdVaDatiVacancy(getVaDatiVacancyDTO().getId());
		candidatura.setFlagClicLavoro(getVaDatiVacancyDTO().getFlagInvioCl());

		Integer idPfPrincipalAzienda = getIdAziendaInfo();
		Integer idVaDatiVacancy = getIdVaDatiVacancy();
		Integer idPrincipal = session.getPrincipalId();
		byte[] file = null;
		if (getFile() != null) {
			candidatura.setAllegatoFileName(getFile().getFileName());
			file = getFile().getContents();
		}

		try {
			Boolean invioCandidatura = acCandidaturaHome.inviaCandidatura(candidatura, idPrincipal,
					idPfPrincipalAzienda, idVaDatiVacancy, file);

			if (invioCandidatura) {
				searchVacancyFilter();
				addInfoMessage("candidatura.sent");
			} else {
				addErrorMessage("vacancy.cv_obbligatorio");
			}
		} catch (MyPortalException e) {
			gestisciErrore(e, "candidatura.allegato.error");
		}
	}
	
	
	/**
	 * Registra l'invio della candidatura relativa alla vacancy selezionata.
	 */
	public void inviaCandidaturaNoDTO() {
		setVaDatiVacancyDTO(vaDatiVacancyHome.findDTOById(getIdVaDatiVacancy()));
		// Setto i campi opzionali per CRESCO
		if (flgCresco && dummyCandidaturaDatiSil != null) {
			candidatura.setAcCandidaturaDatiList(getNuovaCandidaturaCrescoDati());
		}
		
		if (!getVaDatiVacancyDTO().getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			addErrorMessage("vacancy.error_not_present");
			return;
		}

		// Setto i campi relativi alla candidatura
		candidatura.setIdVaDatiVacancy(getVaDatiVacancyDTO().getId());
		candidatura.setFlagClicLavoro(getVaDatiVacancyDTO().getFlagInvioCl());
		
		Integer idPfPrincipalAzienda = getIdAziendaInfo();
		Integer idVaDatiVacancy = getIdVaDatiVacancy();
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
				searchVacancyFilter();
			
				    numberCheckOrderedVacancyToCandidate = checkNumberOrderedVacancyToCandidate(getIdVaDatiVacancy(), candidaturaInviata.getIdAcCandidatura());				   
				    addInfoMessageWithParam("candidatura.sentRER",String.valueOf(numberCheckOrderedVacancyToCandidate));
				
			} else {
				addErrorMessage("vacancy.cv_obbligatorio");
			}
		} catch (MyPortalException e) {
			gestisciErrore(e, "candidatura.allegato.error");
		}
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check = true;
		}
		return check;
	}

	/**
	 * Invia la segnalazione di una candidatura all'email di un amico.
	 */
	public void segnalaCandidatura() {
		// Controllo segnalazione già inviata
		VaSegnalazione segnalazione = vaSegnalazioneHome.findSegnalazioneByMailIdVacMitt(emailAmico,
				selVacancy.getIdVaDatiVacancy(), session.getPrincipalId());
		if (segnalazione != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Attenzione, hai già inviato la tua segnalazione all'indirizzo email indicato", ""));
			return;
		}
		// Se non inviata eseguo la procedura di invio email e salvataggio
		EmailDTO emailDTO = EmailDTO.buildSegnalaCandidaturaEmail(session.getConnectedUtente(), nominativoAmico,
				emailAmico, selVacancy.getIdVaDatiVacancy(), selVacancy.getStrMansione());
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);

		segnalazione = new VaSegnalazione();
		segnalazione.setNominativo(nominativoAmico);
		segnalazione.setEmail(emailAmico);
		segnalazione.setPfPrincipal(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setPfPrincipalIns(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setPfPrincipalMod(pfPrincipalHome.findById(session.getPrincipalId()));
		segnalazione.setIdVaDatiVacancy(selVacancy.getIdVaDatiVacancy());
		Date now = new Date();
		segnalazione.setDataInvio(now);
		segnalazione.setDtmIns(now);
		segnalazione.setDtmMod(now);
		vaSegnalazioneHome.persist(segnalazione);

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('segnalaCandidaturaDialogWv').hide();");

		log.info("- Mail inviata: {\n" + emailDTO.toString() + "}");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MAIL_INVIATA));
	}

	/**
	 * Invoca la ricerca resettando i parametri dall'oggetto RvTestataDTO
	 */
	public void resetSearch() {
		data = new RvTestataDTO();
		// startSearch();
		offerteLavoro = null;
	}

	public void search() {
		/* Effettuo la ricerca */
		searchVacancyFilter();
	}

	/**
	 * funzionalita` eccezionale 04/2020
	 */
	public void startSearchContadini() {
		/* Resetto i contatori */
		resettaCaricamento();
		
		svuotaListeDaSessione();
		
		
		// (34+OR+35+OR+36+OR+37+OR+38+Or+39+)
		DeMansione m34 = deMansioneHome.findById("34");
		listSelectedMansioni.add(m34);
		DeMansione m35 = deMansioneHome.findById("35");
		listSelectedMansioni.add(m35);
		DeMansione m36 = deMansioneHome.findById("36");
		listSelectedMansioni.add(m36);
		DeMansione m37 = deMansioneHome.findById("37");
		listSelectedMansioni.add(m37);
		DeMansione m38 = deMansioneHome.findById("38");
		listSelectedMansioni.add(m38);
		DeMansione m39 = deMansioneHome.findById("39");
		listSelectedMansioni.add(m39);
		
		
		setFiltriGroup();
		
		
		listFiltriButton.clear();
		GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(m34.getCodMansione(),
				deMansioneHome.findById(m34.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto35 = new GenericFiltroDecodeDTO(m35.getCodMansione(),
				deMansioneHome.findById(m35.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto36 = new GenericFiltroDecodeDTO(m36.getCodMansione(),
				deMansioneHome.findById(m36.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto37 = new GenericFiltroDecodeDTO(m37.getCodMansione(),
				deMansioneHome.findById(m37.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto38 = new GenericFiltroDecodeDTO(m38.getCodMansione(),
				deMansioneHome.findById(m38.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto39 = new GenericFiltroDecodeDTO(m39.getCodMansione(),
				deMansioneHome.findById(m39.getCodMansione()).getDescrizione(), "mansione");
		
		listFiltriButton.add(decodeDto);
		listFiltriButton.add(decodeDto35);
		listFiltriButton.add(decodeDto36);
		listFiltriButton.add(decodeDto37);
		listFiltriButton.add(decodeDto38);
		listFiltriButton.add(decodeDto39);
		
		DeMansione[] tmp = new DeMansione[0];
		deMansioneBean.setSelectedMansioni(listSelectedMansioni.toArray(tmp));
	    		
		searchVacancyFilter();
	}

	public void startSearchContadiniNewVersion() {
		/* Resetto i contatori */
		resettaCaricamento();
		
		svuotaListeDaSessione();
		/*
		// (34+OR+35+OR+36+OR+37+OR+38+Or+39+)
		DeMansione m34 = deMansioneHome.findById("34");
		listSelectedMansioni.add(m34);
		DeMansione m35 = deMansioneHome.findById("35");
		listSelectedMansioni.add(m35);
		DeMansione m36 = deMansioneHome.findById("36");
		listSelectedMansioni.add(m36);
		DeMansione m37 = deMansioneHome.findById("37");
		listSelectedMansioni.add(m37);
		DeMansione m38 = deMansioneHome.findById("38");
		listSelectedMansioni.add(m38);
		DeMansione m39 = deMansioneHome.findById("39");
		listSelectedMansioni.add(m39);
		*/
		//'2313','3221','3222','3141','3142','3183','6411','6412','6413','6414','6421','6422','6423','6424','6425','6426','6429','6431','6441','6451','6452','6453','8311','8312','8321','8323','8322'
		/*
		DeBpMansione m2313 = deBpMansioneHome.findById("2313");
		listSelectedMansioniContadini.add(m2313);
		DeBpMansione m3221 = deBpMansioneHome.findById("3221");
		listSelectedMansioniContadini.add(m3221);
		DeBpMansione m3222 = deBpMansioneHome.findById("3222");
		listSelectedMansioniContadini.add(m3222);
		DeBpMansione m3141 = deBpMansioneHome.findById("3141");
		listSelectedMansioniContadini.add(m3141);
		DeBpMansione m3142 = deBpMansioneHome.findById("3142");
		listSelectedMansioniContadini.add(m3142);
		DeBpMansione m3183 = deBpMansioneHome.findById("3183");
		listSelectedMansioniContadini.add(m3183);
		DeBpMansione m6411 = deBpMansioneHome.findById("6411");
		listSelectedMansioniContadini.add(m6411);
		DeBpMansione m6412 = deBpMansioneHome.findById("6412");
		listSelectedMansioniContadini.add(m6412);
		DeBpMansione m6413 = deBpMansioneHome.findById("6413");
		listSelectedMansioniContadini.add(m6413);
		DeBpMansione m6414 = deBpMansioneHome.findById("6414");
		listSelectedMansioniContadini.add(m6414);
		DeBpMansione m6421 = deBpMansioneHome.findById("6421");
		listSelectedMansioniContadini.add(m6421);
		DeBpMansione m6422 = deBpMansioneHome.findById("6422");
		listSelectedMansioniContadini.add(m6422);
		DeBpMansione m6423 = deBpMansioneHome.findById("6423");
		listSelectedMansioniContadini.add(m6423);
		DeBpMansione m6424 = deBpMansioneHome.findById("6424");
		listSelectedMansioniContadini.add(m6424);
		DeBpMansione m6425 = deBpMansioneHome.findById("6425");
		listSelectedMansioniContadini.add(m6425);
		DeBpMansione m6426 = deBpMansioneHome.findById("6426");
		listSelectedMansioniContadini.add(m6426);
		DeBpMansione m6429 = deBpMansioneHome.findById("6429");
		listSelectedMansioniContadini.add(m6429);
		DeBpMansione m6431 = deBpMansioneHome.findById("6431");
		listSelectedMansioniContadini.add(m6431);
		DeBpMansione m6441 = deBpMansioneHome.findById("6441");
		listSelectedMansioniContadini.add(m6441);
		DeBpMansione m6451 = deBpMansioneHome.findById("6451");
		listSelectedMansioniContadini.add(m6451);
		DeBpMansione m6452= deBpMansioneHome.findById("6452");
		listSelectedMansioniContadini.add(m6452);
		DeBpMansione m6453 = deBpMansioneHome.findById("6453");
		listSelectedMansioniContadini.add(m6453);
		DeBpMansione m8311 = deBpMansioneHome.findById("8311");
		listSelectedMansioniContadini.add(m8311);
		DeBpMansione m8312 = deBpMansioneHome.findById("8312");
		listSelectedMansioniContadini.add(m8312);
		DeBpMansione m8321 = deBpMansioneHome.findById("8321");
		listSelectedMansioniContadini.add(m8321);
		DeBpMansione m8323 = deBpMansioneHome.findById("8323");
		listSelectedMansioniContadini.add(m2313);
		DeBpMansione m8322 = deBpMansioneHome.findById("8322");
		listSelectedMansioniContadini.add(m8322);
		
		*/
		
		setFiltriGroup();
		
		
		listFiltriButton.clear();
		
		GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO("Y",
				"Settore Agricoli", "contadino");
		/*
		GenericFiltroDecodeDTO decodeDto = new GenericFiltroDecodeDTO(m34.getCodMansione(),
				deMansioneHome.findById(m34.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto35 = new GenericFiltroDecodeDTO(m35.getCodMansione(),
				deMansioneHome.findById(m35.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto36 = new GenericFiltroDecodeDTO(m36.getCodMansione(),
				deMansioneHome.findById(m36.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto37 = new GenericFiltroDecodeDTO(m37.getCodMansione(),
				deMansioneHome.findById(m37.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto38 = new GenericFiltroDecodeDTO(m38.getCodMansione(),
				deMansioneHome.findById(m38.getCodMansione()).getDescrizione(), "mansione");
		
		GenericFiltroDecodeDTO decodeDto39 = new GenericFiltroDecodeDTO(m39.getCodMansione(),
				deMansioneHome.findById(m39.getCodMansione()).getDescrizione(), "mansione");
		
		listFiltriButton.add(decodeDto);
		listFiltriButton.add(decodeDto35);
		listFiltriButton.add(decodeDto36);
		listFiltriButton.add(decodeDto37);
		listFiltriButton.add(decodeDto38);
		listFiltriButton.add(decodeDto39);
		
		DeMansione[] tmp = new DeMansione[0];
		deMansioneBean.setSelectedMansioni(listSelectedMansioni.toArray(tmp));
		*/
		listFiltriButton.add(decodeDto);
		flgContadino = "Y";
		searchVacancyFilter();
	}
	
	/**
	 * Invoca la ricerca delle vacancy con il RvTestataDTO corrente come filtro della ricerca.
	 */
	public void startSearch() {
		/* Resetto i contatori */
		resettaCaricamento();
		/* Effettuo la ricerca */
		searchVacancyFilter();
	}

	/**
	 * Genera l'url da passare alla mappa
	 */
	private void urlMappa(String key) {
		// recupero l'url utile per la mappa
		String paramMappa = rvRicercaVacancyHome.getParamByPrimefaces(key, data, listSelectedMansioni,
				listSelectedContratti, listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli,
				listSelectedPatenti, listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil,
				filtraDataScadenza, false);
		// elimino i parametri che non interessano alla query della mappa
		paramMappa = paramMappa.substring(0, paramMappa.lastIndexOf("&wt")) + "&start=0&rows=50000";
		paramMappa += PATH_GEOSERVER;

		// Eventuale ordinamento per distanza crescente
		String mapCenter = utils.getMapCenter();
		if (mapCenter != null) {
			String[] center = mapCenter.split(",");
			String lat = center[0];
			String lon = center[1];
			paramMappa += it.eng.myportal.utils.URL.escape("&sort=geodist(punto," + lat + "," + lon + ") asc");
		}

		// setto l'url su questa variabile hidden, per essere gestita lato
		// pagina dal plugin della mappa
		setParamSOLR(paramMappa);
	}

	/**
	 * Verifica la presenza di nuove vacancy e ne setta l'apposito attributo
	 * 
	 */
	public void searchNewVacancy() {
		// nelle ricerche non salvate il RvTestaDTO non effettua il controllo
		// delle new
		if (data.getDtmMod() != null) {
			for (Iterator<RvRicercaVacancyDTO> iterator = offerteLavoro.iterator(); iterator.hasNext();) {
				RvRicercaVacancyDTO dto = iterator.next();
				if (dto.getDataModifica().after(data.getDtmMod())) {
					dto.setNewVacancy(true);
				}
			}
		}

		putParamsIntoSession();
	}

	public VaDatiVacancyDTO getVaDatiVacancyDTO() {
		return vaDatiVacancyDTO;
	}

	public void setVaDatiVacancyDTO(VaDatiVacancyDTO vaDatiVacancyDTO) {
		this.vaDatiVacancyDTO = vaDatiVacancyDTO;
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

	public AcCandidaturaDTO getCandidatura() {
		return candidatura;
	}

	public void setCandidatura(AcCandidaturaDTO candidatura) {
		this.candidatura = candidatura;
	}

	public AcCandidaturaHome getAcCandidaturaHome() {
		return acCandidaturaHome;
	}

	public void setAcCandidaturaHome(AcCandidaturaHome acCandidaturaHome) {
		this.acCandidaturaHome = acCandidaturaHome;
	}

	public int getRowsLoaded() {
		return data.getRowsLoaded();
	}

	public int getRowsForLoad() {
		return data.getRowsForLoad();
	}

	public int getRowsToLoad() {
		return data.getRowsToLoad();
	}

	public int getRowsTotal() {
		return data.getRowsTotal();
	}

	public String getParamSOLR() {
		return ParamSOLR;
	}

	public void setParamSOLR(String paramSOLR) {
		ParamSOLR = paramSOLR;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getIdAziendaInfo() {
		return idAziendaInfo;
	}

	public void setIdAziendaInfo(Integer idAziendaInfo) {
		this.idAziendaInfo = idAziendaInfo;
	}

	public Boolean getFromClicLavoro() {
		return fromClicLavoro;
	}

	public void setFromClicLavoro(Boolean fromClicLavoro) {
		this.fromClicLavoro = fromClicLavoro;
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put("data", data);

		ret.put("listSelectedMansioni", listSelectedMansioni);
		ret.put("listSelectedContratti", listSelectedContratti);
		ret.put("listSelectedOrari", listSelectedOrari);
		ret.put("listSelectedAttivita", listSelectedAttivita);
		ret.put("listSelectedLingue", listSelectedLingue);
		ret.put("listSelectedTitoli", listSelectedTitoli);
		ret.put("listSelectedPatenti", listSelectedPatenti);
		ret.put("listSelectedContrattiSil", listSelectedContrattiSil);
		ret.put("listSelectedOrariSil", listSelectedOrariSil);
		ret.put("listSelectedPatentiSil", listSelectedPatentiSil);
		ret.put("listFiltriButton", listFiltriButton);

		ret.put("myCurricula", myCurricula);
		ret.put("myLettere", myLettere);
		return ret;
	}

	@Override
	protected void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		this.data = (RvTestataDTO) restoreParams.get("data");

		this.listSelectedMansioni = (List<DeMansione>) restoreParams.get("listSelectedMansioni");
		this.listSelectedContratti = (List<DeContratto>) restoreParams.get("listSelectedContratti");
		this.listSelectedOrari = (List<DeOrario>) restoreParams.get("listSelectedOrari");
		this.listSelectedAttivita = (List<DeAttivita>) restoreParams.get("listSelectedAttivita");
		this.listSelectedLingue = (List<DeLingua>) restoreParams.get("listSelectedLingue");
		this.listSelectedTitoli = (List<DeTitolo>) restoreParams.get("listSelectedTitoli");
		this.listSelectedPatenti = (List<DePatente>) restoreParams.get("listSelectedPatenti");
		this.listSelectedOrariSil = (List<DeOrarioSil>) restoreParams.get("listSelectedOrariSil");
		this.listSelectedContrattiSil = (List<DeContrattoSil>) restoreParams.get("listSelectedContrattiSil");
		this.listSelectedPatentiSil = (List<DePatenteSil>) restoreParams.get("listSelectedPatentiSil");
		this.listFiltriButton = (List<GenericFiltroDecodeDTO>) restoreParams.get("listFiltriButton");

		this.myCurricula = (List<SelectItem>) restoreParams.get("myCurricula");
		this.myLettere = (List<SelectItem>) restoreParams.get("myLettere");
	}

	protected void svuotaListeDaSessione() {
		//this.listSelectedMansioniContadini = new ArrayList<DeBpMansione>();
		this.listSelectedMansioni = new ArrayList<DeMansione>();
		this.listSelectedContratti = new ArrayList<DeContratto>();
		this.listSelectedOrari = new ArrayList<DeOrario>();
		this.listSelectedAttivita = new ArrayList<DeAttivita>();
		this.listSelectedLingue = new ArrayList<DeLingua>();
		this.listSelectedTitoli = new ArrayList<DeTitolo>();
		this.listSelectedPatenti = new ArrayList<DePatente>();
		this.listSelectedContrattiSil = new ArrayList<DeContrattoSil>();
		this.listSelectedOrariSil = new ArrayList<DeOrarioSil>();
		this.listSelectedPatentiSil = new ArrayList<DePatenteSil>();
	}

	public Boolean getDisableCandidatura() {
		if (session.isUtente()) {
			disableCandidatura = false;
		} else if (session.isProvincia()) {
			disableCandidatura = true;
		}

		return disableCandidatura;
	}

	public void setDisableCandidatura(Boolean disableCandidatura) {
		this.disableCandidatura = disableCandidatura;
	}

	public List<SelectItem> getMyClicLavoroCurricula() {
		return myClicLavoroCurricula;
	}

	public void setMyClicLavoroCurricula(List<SelectItem> myClicLavoroCurricula) {
		this.myClicLavoroCurricula = myClicLavoroCurricula;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	private void setFiltriGroup() {
		//newIDO 4.3.9.2 DA RIVEDERE
		/*
		 * inserire filtro agricoli */
		if(utils.isRER()) {
			Set<String> setAgricolo = null;
			if(flgContadino != null) {
			  setAgricolo = new HashSet<String>();
			//for (int i = 0; i < listSelectedMansioni.size(); i++) {
			  setAgricolo.add("Settore Agricoli");
			//}
			}
			data.setSetAgricolo(setAgricolo);
		}
		
		Set<String> setMansione = new HashSet<String>();
		for (int i = 0; i < listSelectedMansioni.size(); i++) {
			setMansione.add((listSelectedMansioni.get(i)).getCodMansione());
		}
		data.setSetMansione(setMansione);

		Set<String> setContratto = new HashSet<String>();
		if (usaDecodificheSil()) {
			for (int i = 0; i < listSelectedContrattiSil.size(); i++) {
				setContratto.add((listSelectedContrattiSil.get(i)).getCodContrattoSil());
			}
		} else {
			for (int i = 0; i < listSelectedContratti.size(); i++) {
				setContratto.add((listSelectedContratti.get(i)).getCodContratto());
			}
		}
		data.setSetContratto(setContratto);

		Set<String> setOrario = new HashSet<String>();
		if (usaDecodificheSil()) {
			for (int i = 0; i < listSelectedOrariSil.size(); i++) {
				setOrario.add((listSelectedOrariSil.get(i)).getCodOrarioSil());
			}
		} else {
			for (int i = 0; i < listSelectedOrari.size(); i++) {
				setOrario.add((listSelectedOrari.get(i)).getCodOrario());
			}
		}
		data.setSetOrario(setOrario);

		Set<String> setSettore = new HashSet<String>();
		for (int i = 0; i < listSelectedAttivita.size(); i++) {
			setSettore.add((listSelectedAttivita.get(i)).getCodAteco());
		}
		data.setSetSettore(setSettore);

		Set<String> setLingua = new HashSet<String>();
		for (int i = 0; i < listSelectedLingue.size(); i++) {
			setLingua.add((listSelectedLingue.get(i)).getCodLingua());
		}
		data.setSetLingua(setLingua);

		Set<String> setTitolo = new HashSet<String>();
		for (int i = 0; i < listSelectedTitoli.size(); i++) {
			setTitolo.add((listSelectedTitoli.get(i)).getCodTitolo());
		}
		data.setSetTitoloStudio(setTitolo);

		Set<String> setPatente = new HashSet<String>();
		if (usaDecodificheSil()) {
			for (int i = 0; i < listSelectedPatentiSil.size(); i++) {
				setPatente.add((listSelectedPatentiSil.get(i)).getCodPatenteSil());
			}
		} else {
			for (int i = 0; i < listSelectedPatenti.size(); i++) {
				setPatente.add((listSelectedPatenti.get(i)).getCodPatente());
			}
		}
		data.setSetPatente(setPatente);
	}

	public void addRowSelected(org.primefaces.event.SelectEvent selectEvent) {
		/* Resetto i contatori */
		resettaCaricamento();

		GenericFiltroDecodeDTO decodeDto = null;
		if (selectEvent.getObject() instanceof DeMansione) {
			DeMansione mans = (DeMansione) selectEvent.getObject();
			listSelectedMansioni.add(mans);
			decodeDto = new GenericFiltroDecodeDTO(mans.getCodMansione(),
					deMansioneHome.findById(mans.getCodMansione()).getDescrizione(), "mansione");
		} else if (selectEvent.getObject() instanceof DeContratto) {
			DeContratto cont = (DeContratto) selectEvent.getObject();
			listSelectedContratti.add(cont);
			decodeDto = new GenericFiltroDecodeDTO(cont.getCodContratto(),
					deContrattoHome.findById(cont.getCodContratto()).getDescrizione(), "contratto");
		} else if (selectEvent.getObject() instanceof DeOrario) {
			DeOrario ora = (DeOrario) selectEvent.getObject();
			listSelectedOrari.add(ora);
			decodeDto = new GenericFiltroDecodeDTO(ora.getCodOrario(),
					deOrarioHome.findById(ora.getCodOrario()).getDescrizione(), "orario");
		} else if (selectEvent.getObject() instanceof DeAttivita) {
			DeAttivita att = (DeAttivita) selectEvent.getObject();
			listSelectedAttivita.add(att);
			decodeDto = new GenericFiltroDecodeDTO(att.getCodAteco(),
					deAttivitaHome.findById(att.getCodAteco()).getDescrizione(), "attivita");
		} else if (selectEvent.getObject() instanceof DeLingua) {
			DeLingua lin = (DeLingua) selectEvent.getObject();
			listSelectedLingue.add(lin);
			decodeDto = new GenericFiltroDecodeDTO(lin.getCodLingua(),
					deLinguaHome.findById(lin.getCodLingua()).getDenominazione(), "lingua");
		} else if (selectEvent.getObject() instanceof DeTitolo) {
			DeTitolo tit = (DeTitolo) selectEvent.getObject();
			listSelectedTitoli.add(tit);
			decodeDto = new GenericFiltroDecodeDTO(tit.getCodTitolo(),
					deTitoloHome.findById(tit.getCodTitolo()).getDescrizione(), "titolo");
		} else if (selectEvent.getObject() instanceof DePatente) {
			DePatente pat = (DePatente) selectEvent.getObject();
			listSelectedPatenti.add(pat);
			decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatente(),
					dePatenteHome.findById(pat.getCodPatente()).getDescrizione(), "patente");
		} else if (selectEvent.getObject() instanceof DePatenteSil) {
			DePatenteSil pat = (DePatenteSil) selectEvent.getObject();
			listSelectedPatentiSil.add(pat);
			decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatenteSil(),
					dePatenteSilHome.findById(pat.getCodPatenteSil()).getDescrizione(), "patente");
		} else if (selectEvent.getObject() instanceof DeContrattoSil) {
			DeContrattoSil contr = (DeContrattoSil) selectEvent.getObject();
			listSelectedContrattiSil.add(contr);
			decodeDto = new GenericFiltroDecodeDTO(contr.getCodContrattoSil(),
					deContrattoSilHome.findById(contr.getCodContrattoSil()).getDescrizione(), "contratto");
		} else if (selectEvent.getObject() instanceof DeOrarioSil) {
			DeOrarioSil orario = (DeOrarioSil) selectEvent.getObject();
			listSelectedOrariSil.add(orario);
			decodeDto = new GenericFiltroDecodeDTO(orario.getCodOrarioSil(),
					deOrarioSilHome.findById(orario.getCodOrarioSil()).getDescrizione(), "orario");
		}

		listFiltriButton.add(decodeDto);

		searchVacancyFilter();
	}

	public void removeRowSelected(org.primefaces.event.UnselectEvent unselectEvent) {
		/* Resetto i contatori */
		resettaCaricamento();

		GenericFiltroDecodeDTO decodeDto = null;
		if (unselectEvent.getObject() instanceof DeMansione) {
			DeMansione mans = (DeMansione) unselectEvent.getObject();
			for (int i = 0; i < listSelectedMansioni.size(); i++) {
				String codMansione = ((DeMansione) listSelectedMansioni.get(i)).getCodMansione();
				if (codMansione.equalsIgnoreCase(mans.getCodMansione())) {
					listSelectedMansioni.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(mans.getCodMansione(), mans.getDescrizione(), "mansione");
		} else if (unselectEvent.getObject() instanceof DeContratto) {
			DeContratto cont = (DeContratto) unselectEvent.getObject();
			for (int i = 0; i < listSelectedContratti.size(); i++) {
				String cod = ((DeContratto) listSelectedContratti.get(i)).getCodContratto();
				if (cod.equalsIgnoreCase(cont.getCodContratto())) {
					listSelectedContratti.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(cont.getCodContratto(), cont.getDescrizione(), "contratto");
		} else if (unselectEvent.getObject() instanceof DeOrario) {
			DeOrario ora = (DeOrario) unselectEvent.getObject();
			for (int i = 0; i < listSelectedOrari.size(); i++) {
				String cod = ((DeOrario) listSelectedOrari.get(i)).getCodOrario();
				if (cod.equalsIgnoreCase(ora.getCodOrario())) {
					listSelectedOrari.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(ora.getCodOrario(), ora.getDescrizione(), "orario");
		} else if (unselectEvent.getObject() instanceof DeAttivita) {
			DeAttivita att = (DeAttivita) unselectEvent.getObject();
			for (int i = 0; i < listSelectedAttivita.size(); i++) {
				String cod = ((DeAttivita) listSelectedAttivita.get(i)).getCodAteco();
				if (cod.equalsIgnoreCase(att.getCodAteco())) {
					listSelectedAttivita.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(att.getCodAteco(), att.getDescrizione(), "attivita");
		} else if (unselectEvent.getObject() instanceof DeLingua) {
			DeLingua lin = (DeLingua) unselectEvent.getObject();
			for (int i = 0; i < listSelectedLingue.size(); i++) {
				String cod = ((DeLingua) listSelectedLingue.get(i)).getCodLingua();
				if (cod.equalsIgnoreCase(lin.getCodLingua())) {
					listSelectedLingue.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(lin.getCodLingua(), lin.getDenominazione(), "lingua");
		} else if (unselectEvent.getObject() instanceof DeTitolo) {
			DeTitolo tit = (DeTitolo) unselectEvent.getObject();
			for (int i = 0; i < listSelectedTitoli.size(); i++) {
				String cod = ((DeTitolo) listSelectedTitoli.get(i)).getCodTitolo();
				if (cod.equalsIgnoreCase(tit.getCodTitolo())) {
					listSelectedTitoli.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(tit.getCodTitolo(), tit.getDescrizione(), "titolo");
		} else if (unselectEvent.getObject() instanceof DePatente) {
			DePatente pat = (DePatente) unselectEvent.getObject();
			for (int i = 0; i < listSelectedPatenti.size(); i++) {
				String cod = ((DePatente) listSelectedPatenti.get(i)).getCodPatente();
				if (cod.equalsIgnoreCase(pat.getCodPatente())) {
					listSelectedPatenti.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatente(), pat.getDescrizione(), "patente");
		} else if (unselectEvent.getObject() instanceof DePatenteSil) {
			DePatenteSil pat = (DePatenteSil) unselectEvent.getObject();
			for (int i = 0; i < listSelectedPatentiSil.size(); i++) {
				String cod = ((DePatenteSil) listSelectedPatentiSil.get(i)).getCodPatenteSil();
				if (cod.equalsIgnoreCase(pat.getCodPatenteSil())) {
					listSelectedPatentiSil.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(pat.getCodPatenteSil(), pat.getDescrizione(), "patente");
		} else if (unselectEvent.getObject() instanceof DeContrattoSil) {
			DeContrattoSil contr = (DeContrattoSil) unselectEvent.getObject();
			for (int i = 0; i < listSelectedContrattiSil.size(); i++) {
				String cod = ((DeContrattoSil) listSelectedContrattiSil.get(i)).getCodContrattoSil();
				if (cod.equalsIgnoreCase(contr.getCodContrattoSil())) {
					listSelectedContrattiSil.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(contr.getCodContrattoSil(), contr.getDescrizione(), "contratto");
		} else if (unselectEvent.getObject() instanceof DeOrarioSil) {
			DeOrarioSil orario = (DeOrarioSil) unselectEvent.getObject();
			for (int i = 0; i < listSelectedOrariSil.size(); i++) {
				String cod = ((DeOrarioSil) listSelectedOrariSil.get(i)).getCodOrarioSil();
				if (cod.equalsIgnoreCase(orario.getCodOrarioSil())) {
					listSelectedOrariSil.remove(i);
				}
			}
			decodeDto = new GenericFiltroDecodeDTO(orario.getCodOrarioSil(), orario.getDescrizione(), "orario");
		}

		listFiltriButton.remove(decodeDto);

		searchVacancyFilter();
	}

	/**
	 * elimina i filtri selezionati attraverso i pulsanti
	 */
	public void removeFiltroButton(String idFiltroButton) {
		/* Resetto i contatori */
		resettaCaricamento();

		log.debug(idFiltroButton);
		for (int i = 0; i < listFiltriButton.size(); i++) {
			GenericFiltroDecodeDTO filtro = listFiltriButton.get(i);
			if (filtro.getId().equalsIgnoreCase(idFiltroButton)) {
				if(utils.isRER()) {
					if(filtro.getDescrizione().equalsIgnoreCase("Settore Agricoli")) {
						flgContadino = null;
					}
				}
				listFiltriButton.remove(filtro);
				removeCheckboxByFiltroButton(idFiltroButton, filtro.getTipoCodifica());
				break;
			}
			
		}

		searchVacancyFilter();
	}

	private void removeCheckboxByFiltroButton(String idFiltroButton, String tipoCodifica) {
		/* Resetto i contatori */
		resettaCaricamento();

		if ("mansione".equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < listSelectedMansioni.size(); i++) {
				DeMansione mans = listSelectedMansioni.get(i);
				if (mans.getCodMansione().equalsIgnoreCase(idFiltroButton)) {
					listSelectedMansioni.remove(i);
				}
			}
		} else if ("contratto".equalsIgnoreCase(tipoCodifica)) {
			if (usaDecodificheSil()) {
				for (int i = 0; i < listSelectedContrattiSil.size(); i++) {
					DeContrattoSil con = listSelectedContrattiSil.get(i);
					if (con.getCodContrattoSil().equalsIgnoreCase(idFiltroButton)) {
						listSelectedContrattiSil.remove(i);
					}
				}
			} else {
				for (int i = 0; i < listSelectedContratti.size(); i++) {
					DeContratto con = listSelectedContratti.get(i);
					if (con.getCodContratto().equalsIgnoreCase(idFiltroButton)) {
						listSelectedContratti.remove(i);
					}
				}
			}
		} else if ("orario".equalsIgnoreCase(tipoCodifica)) {
			if (usaDecodificheSil()) {
				for (int i = 0; i < listSelectedOrariSil.size(); i++) {
					DeOrarioSil ora = listSelectedOrariSil.get(i);
					if (ora.getCodOrarioSil().equalsIgnoreCase(idFiltroButton)) {
						listSelectedOrariSil.remove(i);
					}
				}
			} else {
				for (int i = 0; i < listSelectedOrari.size(); i++) {
					DeOrario ora = listSelectedOrari.get(i);
					if (ora.getCodOrario().equalsIgnoreCase(idFiltroButton)) {
						listSelectedOrari.remove(i);
					}
				}
			}
		} else if ("attivita".equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < listSelectedAttivita.size(); i++) {
				DeAttivita att = listSelectedAttivita.get(i);
				if (att.getCodAteco().equalsIgnoreCase(idFiltroButton)) {
					listSelectedAttivita.remove(i);
				}
			}
		} else if ("lingua".equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < listSelectedLingue.size(); i++) {
				DeLingua lin = listSelectedLingue.get(i);
				if (lin.getCodLingua().equalsIgnoreCase(idFiltroButton)) {
					listSelectedLingue.remove(i);
				}
			}
		} else if ("titolo".equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < listSelectedTitoli.size(); i++) {
				DeTitolo tit = listSelectedTitoli.get(i);
				if (tit.getCodTitolo().equalsIgnoreCase(idFiltroButton)) {
					listSelectedTitoli.remove(i);
				}
			}
		} else if ("patente".equalsIgnoreCase(tipoCodifica)) {
			if (usaDecodificheSil()) {
				for (int i = 0; i < listSelectedPatentiSil.size(); i++) {
					DePatenteSil pat = listSelectedPatentiSil.get(i);
					if (pat.getCodPatenteSil().equalsIgnoreCase(idFiltroButton)) {
						listSelectedPatentiSil.remove(i);
					}
				}
			} else {
				for (int i = 0; i < listSelectedPatenti.size(); i++) {
					DePatente pat = listSelectedPatenti.get(i);
					if (pat.getCodPatente().equalsIgnoreCase(idFiltroButton)) {
						listSelectedPatenti.remove(i);
					}
				}
			}
		}
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
	
	public void searchVacancyFilter() {
		String key = "";
		/* Effettuo la ricerca */
		if (session.isUtente()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByPrimefaces(data, listSelectedMansioni, listSelectedContratti,
					listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli,
					listSelectedPatenti, listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil,
					flgCresco, filtraDataScadenza, getSession().getConnectedUtente().getId(), 0,flgContadino);
		} else if (session.isProvincia()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByPrimefaces(data, listSelectedMansioni, listSelectedContratti,
					listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli,
					listSelectedPatenti, listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil,
					flgCresco, filtraDataScadenza, null, 0,flgContadino);
		}

		paginationHandler = new PaginationHandler(0, data.getRowsToLoad(), new Long(data.getRowsTotal()));

		urlMappa(key);
		searchNewVacancy();
	}

	public void changePage() {
		data.setCurrentPage(paginationHandler.getCurrentPage());
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());

		if (session.isUtente()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByPrimefaces(data, listSelectedMansioni, listSelectedContratti,
					listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli,
					listSelectedPatenti, listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil,
					flgCresco, filtraDataScadenza, getSession().getConnectedUtente().getId(), startResultsFrom, flgContadino);
		} else if (session.isProvincia()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByPrimefaces(data, listSelectedMansioni, listSelectedContratti,
					listSelectedOrari, listSelectedAttivita, listSelectedLingue, listSelectedTitoli,
					listSelectedPatenti, listSelectedContrattiSil, listSelectedOrariSil, listSelectedPatentiSil,
					flgCresco, filtraDataScadenza, null, startResultsFrom,flgContadino);
		}
		searchNewVacancy();
	}

	public List<GenericFiltroDecodeDTO> getListFiltriButton() {
		return listFiltriButton;
	}

	public void setListFiltriButton(List<GenericFiltroDecodeDTO> listFiltriButton) {
		this.listFiltriButton = listFiltriButton;
	}

	public Boolean getCheckButtonClean() {
		if (listFiltriButton.size() > 1) {
			checkButtonClean = true;
		} else {
			checkButtonClean = false;
		}
		return checkButtonClean;
	}

	/**
	 * elimina tutti i filtri selezionati
	 */
	public void removeAllFiltroButton(String cosa, String dove) {
		resettaCaricamento();

		listFiltriButton.clear();
		listSelectedMansioni.clear();
		listSelectedContratti.clear();
		listSelectedOrari.clear();
		listSelectedAttivita.clear();
		listSelectedLingue.clear();
		listSelectedTitoli.clear();
		listSelectedPatenti.clear();
		listSelectedContrattiSil.clear();
		listSelectedOrariSil.clear();
		listSelectedPatentiSil.clear();
		
		if(utils.isRER()) {
			flgContadino = null; // ripristino filtro estraggo tutti tranne i contadini
		}

		searchVacancyFilter();
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public void setPaginationHandler(PaginationHandler paginationHandler) {
		this.paginationHandler = paginationHandler;
	}

	public String getIdxAccordionOpen() {
		idxAccordionOpen = "";
		if (!listSelectedMansioni.isEmpty()) {
			idxAccordionOpen += "0,";
		}
		if (!listSelectedContratti.isEmpty() || !listSelectedContrattiSil.isEmpty()) {
			idxAccordionOpen += "1,";
		}
		if (!listSelectedOrari.isEmpty() || !listSelectedOrariSil.isEmpty()) {
			idxAccordionOpen += "2,";
		}
		if (!listSelectedAttivita.isEmpty()) {
			idxAccordionOpen += "3,";
		}
		if (!listSelectedLingue.isEmpty()) {
			idxAccordionOpen += "4,";
		}
		if (!listSelectedTitoli.isEmpty()) {
			idxAccordionOpen += "5,";
		}
		if (!listSelectedPatenti.isEmpty() || !listSelectedPatentiSil.isEmpty()) {
			idxAccordionOpen += "6,";
		}

		return idxAccordionOpen;
	}

	public void setIdxAccordionOpen(String idxAccordionOpen) {
		this.idxAccordionOpen = idxAccordionOpen;
	}

	public boolean usaDecodificheSil() {
		return ConstantsSingleton.usaDecodificheSilPerVacancy();
	}

	public List<RvRicercaVacancyDTO> getOfferteLavoro() {
		return offerteLavoro;
	}

	public void setOfferteLavoro(List<RvRicercaVacancyDTO> offerteLavoro) {
		this.offerteLavoro = offerteLavoro;
	}

	public String getControlliCrescoMessage() {
		return controlliCrescoMessage;
	}

	public void setControlliCrescoMessage(String controlliCrescoMessage) {
		this.controlliCrescoMessage = controlliCrescoMessage;
	}

	public Boolean getFiltraDataScadenza() {
		return filtraDataScadenza;
	}

	public void setFiltraDataScadenza(Boolean filtraDataScadenza) {
		this.filtraDataScadenza = filtraDataScadenza;
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

	public RvRicercaVacancyDTO getSelVacancy() {
		return selVacancy;
	}

	public void setSelVacancy(RvRicercaVacancyDTO selVacancy) {
		this.selVacancy = selVacancy;
	}
	
	public DeMansioneBean getDeMansioneBean() {
		return deMansioneBean;
	}

	public void setDeMansioneBean(DeMansioneBean deMansioneBean) {
		this.deMansioneBean = deMansioneBean;
	}
}