package it.eng.myportal.beans.vetrinaAzienda;

import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_CHISIAMO;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_DOVESIAMO;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_FORMAZCRESCITA;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_MISSION;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_OGNISEZIONE;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_PROFILIRICHIESTI;
import static it.eng.myportal.utils.ConstantsSingleton.SvSezione.SEZ_STORIA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.SvAziendaInfoDTO;
import it.eng.myportal.dtos.SvImmagineDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.SvAziendaInfoHome;
import it.eng.myportal.entity.home.SvImmagineHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeSvSezioneHome;
import it.eng.myportal.entity.home.decodifiche.DeSvTemplateHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * Questo BackingBean gestisce due pagine che riguardano la vetrina.<br/>
 * La pagina di editing della vetrina è visibile solo dall'utente (azienda) collegato al sistema.<br/>
 * La pagina di visualizzazione della Vetrina è invece visionabile da chiunque.
 * 
 * @author Rodi A.
 * 
 * @opt !operations
 * @opt !attributes
 * 
 */
@ManagedBean
@ViewScoped
public class VetrinaAziendaBean extends AbstractBaseBean {

	private static final String ID_VETRINA = "idVetrina";

	/**
	 * Dati della Vetrina.
	 */
	protected SvAziendaInfoDTO data;

	/**
	 * Dati delle immagini delle Sezioni.
	 */
	protected SvImmagineDTO dataImg;

	/**
	 * Elenco delle sezioni che posseggono immagini
	 */
	private List<SvImmagineDTO> listImg;

	@EJB
	private SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeSvTemplateHome deSvTemplateHome;

	@EJB
	private AcCandidaturaHome acCandidaturaHome;

	@EJB
	private DeSvSezioneHome deSvSezioneHome;

	@EJB
	private SvImmagineHome svImmagineHome;

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	/**
	 * Lista dei possibili Template di presentazione della vetrina
	 */
	private List<SelectItem> templateOptions;

	/**
	 * Lista delle possibili Sezioni della vetrina
	 */
	private List<SelectItem> sezioniOptions;

	/**
	 * Nome dell'immagine utile per recuperarla dal fileSystem
	 */
	private String imgEditedName = "";
	/**
	 * Id della Vetrina
	 */
	private Integer idVetrina;

	/**
	 * Variabile a true se l'utente corrente può modificare la vetrina
	 */
	private boolean canEdit;

	/**
	 * Elenco di CV dell'utente che visualizza la vetrina
	 */
	private List<SelectItem> myCurricula;
	/**
	 * Elenco di Lettere di presentazione dell'utente che visualizza la vetrina
	 */
	private List<SelectItem> myLettere;

	private AcCandidaturaDTO candidatura;

	/**
	 * Elenco delle vacancies dell'azienda
	 */
	
	private UploadedFile allegatoUploaded;

	/**
	 * Questa avariabile viene impostata a 'true' se l'utente ha già inviato un' autocandidatura.
	 */
	private boolean hasCandidatura;

	/**
	 * Questa avariabile viene impostata a 'true' se l'utente ha già inviato un' autocandidatura.
	 */
	private boolean isListCandidaturaPresent;

	/**
	 * Mappa che raccoglie tutte le immagini registrate, divise per sezione
	 */
	private Map<String, ArrayList<Integer>> mapSezioniImg = new HashMap<String, ArrayList<Integer>>();

	private Integer idPfPrincipalAzienda;

	private String idVetrinaDaSess;
	
	private LazyDataModel<VaDatiVacancyDTO> vacanciesDiretteLazy;
	private LazyDataModel<VaDatiVacancyDTO> vacanciesPalesiLazy;

	private boolean proprietarioVetrina;
	private long countVacanciesDirette;
	private long countVacanciesPalesi;
	/**
	 * recupera l'elenco dei Template e delle Sezioni da DB. E inizializza il Bean
	 */
	@PostConstruct
	@Override
	protected void postConstruct() {
		// Solo per la VDA, controllo anche che l'azienda sia valida.
		if (utils.isVDA() && session.isAzienda()) {
			AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(session.getConnectedAzienda().getId());
			if (!aziendaInfoDTO.getFlagValida()) {
				redirectHome();
			}
		}

		super.postConstruct();
		setTemplateOptions(deSvTemplateHome.getListItems(false));
		setSezioniOptions(deSvSezioneHome.getListItems(false, "codSvSezione"));
		Integer idPfPrincipalLogged = getSession().getPrincipalId();

		try {
			Map<String, String> map = getRequestParameterMap();
			String idVetrinaString = StringUtils.defaultString(map.get("id"), idVetrinaDaSess);
			if (getSession().isAzienda()) {
				/* ho il parametro in sessione? */
				if (idVetrinaString != null) {
					idVetrina = Integer.parseInt(idVetrinaString);
					idPfPrincipalAzienda = idVetrina;
					proprietarioVetrina = idVetrina.equals(idPfPrincipalLogged) ? true : false;
					data = svAziendaInfoHome.findDTOById(idVetrina);
					/* la vetrina esiste? */
					if (data != null) {
						/* popolo con i dati della vetrina */
						putParamsIntoSession();
						popolaByVetrinaId(idVetrina, proprietarioVetrina);
					} else {
						if (proprietarioVetrina) {
							popolaCreaByAziendaId(idPfPrincipalLogged, proprietarioVetrina);
						} else {
							/*
							 * sto cercando di accedere alla vetrina di un'altra azienda che non esiste
							 */
							addErrorMessage("data.error_loading");
							redirectHome();
						}
					}
				} else {
					/*
					 * sono un'azienda e non c'e' l'id vetrina in sessione. Prima provo a recuperare la mia vetrina, se
					 * ce l'ho ne creo una nuova
					 */
					idPfPrincipalAzienda = idPfPrincipalLogged;
					proprietarioVetrina = true;
					popolaCreaByAziendaId(idPfPrincipalLogged, proprietarioVetrina);
				}
			} else {
				/* non sono un'azienda */
				if (idVetrinaString != null) {
					idVetrina = Integer.parseInt(idVetrinaString);
					idPfPrincipalAzienda = idVetrina;
					data = svAziendaInfoHome.findDTOById(idVetrina);
					/* la vetrina esiste? */
					if (data != null) {
						/* popolo con i dati della vetrina */
						putParamsIntoSession();
						popolaByVetrinaId(idVetrina, proprietarioVetrina);
					} else {
						/* ho l'id in sessione ma la vetrina non esiste */
						addErrorMessage("data.error_loading");
						redirectHome();
					}

					/*
					 * se sono un utente mi carico anche i cv, le lettere e le candidature
					 */
					if (getSession().isUtente()) {
						if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
							myCurricula = utenteInfoHome.getAllCurriculaAsSelectItemIdoRER(idPfPrincipalLogged, false);
						} else {
							myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(idPfPrincipalLogged, false);
						}
						myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(idPfPrincipalLogged, false);
						fetchCandidatura();
					}
				} else {
					/* non ho l'id in sessione */
					addErrorMessage("data.error_loading");
					redirectHome();
				}
			}

		} catch (EJBException e) {
			// in caso di errori durante il recupero dei
			// dati ritorna all'HomePage
			addErrorMessage("data.error_loading");
			redirectHome();
		}

		/*
		 * se sono il proprietario della vetrina recupero anche le vacancies scadute
		 */
		countVacanciesDirette = vaDatiVacancyHome.countVacanciesByIdPfPrincipalAzienda(idPfPrincipalAzienda, session.getPrincipalId(), 
				proprietarioVetrina, false, false);
		vacanciesDiretteLazy = new VacancyLazyList(proprietarioVetrina, false, false);
		if(utils.isRER()) {
			//Vacancy palesi DFD
			countVacanciesPalesi = vaDatiVacancyHome.countVacanciesByIdPfPrincipalAzienda(idPfPrincipalAzienda, session.getPrincipalId(), 
					proprietarioVetrina, true, true);
			vacanciesPalesiLazy = new VacancyLazyList(proprietarioVetrina, true, true);
		} else {
			countVacanciesPalesi = vaDatiVacancyHome.countVacanciesByIdPfPrincipalAzienda(idPfPrincipalAzienda, session.getPrincipalId(), 
					proprietarioVetrina, true, false);
			vacanciesPalesiLazy = new VacancyLazyList(proprietarioVetrina, true, false);
		}

		canEdit = proprietarioVetrina;

		log.debug("Costruito il bean VetrinaAzienda!");
	}

	private void popolaCreaByAziendaId(Integer aziendaId, boolean proprietarioVetrina) {
		/*
		 * se sono un'azienda provo a recuperare la mia vetrina, se ce l'ho, altrimenti ne creo una nuova
		 */
		data = svAziendaInfoHome.findDTOById(aziendaId);
		if (data != null) {
			popolaByVetrinaId(data.getId(), proprietarioVetrina);
		} else {
			/* creo una nuova vetrina */
			data = new SvAziendaInfoDTO();
			data.setPubblicabile(true);
		}
	}

	public boolean chekRER() {
		boolean check = false;	
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check= true;
		}
		return check;
	}
	
	/**
	 * Elimina su DB l'immagine indicata
	 */
	public void deleteImg() {
		try {
			Integer id = Integer.valueOf(getRequestParameterEndsWith("objectId"));
			svImmagineHome.removeById(id, session.getPrincipalId());

			for (int i = 0; i < listImg.size(); i += 1) {
				SvImmagineDTO element = listImg.get(i);
				if (element.getId() != null && element.getId().equals(id)) {
					listImg.remove(i);
					break;
				}
			}
		} catch (EJBException e) {
			log.error("Errore durante la cancellazione dell'immagine: " + e.getMessage());
			addMessage(null, new FacesMessage("Errore durante la cancellazione dell'immagine."));
		}
	}

	private void fetchCandidatura() {
		Integer idUtente = getSession().getConnectedUtente().getId();

		candidatura = acCandidaturaHome.findDTOByUtenteAndAziendaIdPfPrincipalAutocandidature(idUtente, data.getId());

		if (candidatura == null) {
			hasCandidatura = false;
			candidatura = new AcCandidaturaDTO();
			candidatura.setIdPfPrincipalAzienda(data.getId());
		} else {
			hasCandidatura = true;
		}
	}

	/**
	 * Azione collegata al bottone 'Invia autocandidatura'. Un cittadino può inviare il proprio CV direttamente
	 * all'azienda.
	 */
	public void inviaAutocandidatura() {
		Integer idPrincipal = session.getPrincipalId();
		Boolean invioCandidatura = acCandidaturaHome.inviaAutoCandidatura(candidatura, idPrincipal, idPfPrincipalAzienda,
				null, allegatoUploaded);
		if (invioCandidatura) {
			fetchCandidatura();
			addInfoMessage("autocandidatura.sent");
		} else {
			addErrorMessage("autocandidatura.error");
		}
	}

	public void inviaAutocandidaturaRER() {
		Integer idPrincipal = session.getPrincipalId();
		Boolean invioCandidatura = acCandidaturaHome.inviaAutoCandidaturaRER(candidatura, idPrincipal, idPfPrincipalAzienda,
				null, allegatoUploaded);
		
		if (invioCandidatura) {
			fetchCandidatura();
			addInfoMessage("autocandidatura.sent");
		} else {
			addErrorMessage("autocandidatura.error");
		}
	}
	
	public void uploadAllegatoFile(FileUploadEvent event) {
		allegatoUploaded = event.getFile();
		log.info("Attachment uploaded: " + allegatoUploaded.getFileName());
	}
	
	public void deleteAllegatoFile(){
		allegatoUploaded = null;
	}
	
	/**
	 * Popolo la vetrina dell'azienda in base all'id della stessa
	 */
	private void popolaByVetrinaId(Integer idVetrina, boolean proprietarioVetrina) {
		setListImg(svImmagineHome.getAllImg(idVetrina));
		mapSezioniImg = new TreeMap<String, ArrayList<Integer>>();
		if (listImg != null) {
			for (SvImmagineDTO svImmagineDTO : listImg) {
				if (SEZ_OGNISEZIONE.equals(svImmagineDTO.getCodSezione())) {
					popolaMapImg(svImmagineDTO.getId(), SEZ_CHISIAMO);
					popolaMapImg(svImmagineDTO.getId(), SEZ_DOVESIAMO);
					popolaMapImg(svImmagineDTO.getId(), SEZ_MISSION);
					popolaMapImg(svImmagineDTO.getId(), SEZ_STORIA);
					popolaMapImg(svImmagineDTO.getId(), SEZ_PROFILIRICHIESTI);
					popolaMapImg(svImmagineDTO.getId(), SEZ_FORMAZCRESCITA);
				} else {
					popolaMapImg(svImmagineDTO.getId(), svImmagineDTO.getCodSezione());
				}
			}
		}
	}

	/**
	 * Popola la mappa delle immagini per sezione
	 * 
	 * @param idImg
	 *            Integer
	 * @param sezione
	 *            String
	 */
	private void popolaMapImg(Integer idImg, String sezione) {
		ArrayList<Integer> list = mapSezioniImg.get(sezione);
		if (list == null) {
			list = new ArrayList<Integer>();
		}
		list.add(idImg);
		mapSezioniImg.put(sezione, list);
	}

	/**
	 * Salva o aggiorna la Vetrina
	 * 
	 * @return String il path relativo all'edit della vacancy
	 */
	public String save() {
		if (canEdit) { // se si è autorizzati a modificare la vetrina
			try {
				Integer idPfPrincipal = getSession().getConnectedAzienda().getId();

				data.setDtmMod(new Date());
				boolean nuovaVetrina = svAziendaInfoHome.findDTOById(idPfPrincipal) == null ? true : false;

				if (nuovaVetrina) {
					data.setId(idPfPrincipal);
					data = homePersist(svAziendaInfoHome, data);
				} else {
					data = homeMerge(svAziendaInfoHome, data);
				}

				if (listImg != null && !listImg.isEmpty()) {
					for (SvImmagineDTO immagine : listImg) {
						svImmagineHome.mergeDTO(immagine, getSession().getPrincipalId());
					}
				}
			} catch (EJBException e) {
				addErrorMessage("data.error_saving");
				return "";
			}
			log.debug("Salvata vetrina azienda" + data.getId() + ".");

			// vai alla visualizzazione della vetrina
			String outcome = "view_new?faces-redirect=true&id=" + data.getId();
			return outcome;
		}
		return "";
	}

	/**
	 * Elimina la Vetrina
	 * 
	 * @return String il path relativo all'edit della vacancy
	 */
	public String delete() {
		if (canEdit) { // se si è autorizzati ad eliminare la vetrina
			try {
				Integer idPfPrincipal = getSession().getConnectedAzienda().getId();

				/* elimino la vetrina */
				svAziendaInfoHome.removeCascadeById(data.getId(), idPfPrincipal);

				/* elimino tutte le immagini presenti */
				for (SvImmagineDTO img : listImg) {
					svImmagineHome.removeById(img.getId(), idPfPrincipal);
				}

				/* setto a null per non visualizzare i vecchi valori sulla pagina */
				data = null;
				listImg = null;
				idVetrinaDaSess = null;

				popolaCreaByAziendaId(idPfPrincipal, true);
			} catch (EJBException e) {
				addErrorMessage("data.error_deleting");
				return "";
			}
			log.debug("Eliminata vetrina azienda" + data.getId() + ".");
		} else {
			addErrorMessage("data.error_deleting");
			redirectHome();
			return "";
		}
		return "";
	}

	/**
	 * Salva su DB l'immagine e la sezione
	 */
	public void saveImg() {
		dataImg = new SvImmagineDTO();
		dataImg.setCodSezione(data.getCodiceSezione());
		dataImg.setIdPfPrincipal(data.getId());
		dataImg.setDtmMod(new Date());
		try {
			if (StringUtils.isNotBlank(imgEditedName)) {
				byte[] imgByte = Utils.fileToByte(ConstantsSingleton.TMP_DIR + File.separator + imgEditedName);
				if (imgByte.length > ConstantsSingleton.FileUpload.MAX_FOTO_SIZE) {
					addErrorMessage("img.tooBig.500");
				} else {
					dataImg.setImmagine(imgByte);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		dataImg = homePersist(svImmagineHome, dataImg);
		setListImg(svImmagineHome.getAllImg(data.getId()));
	}

	/**
	 * Mostra la Vetrina in visualizzazione
	 * 
	 * @return String il path relativo alla view della vacancy
	 */
	public String view() {
		// vai alla visualizzazione della vetrina
		return "view_new?faces-redirect=true&id=" + data.getId();
	}

	public void setCandidatura(AcCandidaturaDTO candidatura) {
		this.candidatura = candidatura;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public void setData(SvAziendaInfoDTO data) {
		this.data = data;
	}

	public void setDataImg(SvImmagineDTO dataImg) {
		this.dataImg = dataImg;
	}

	public void setHasCandidatura(boolean hasCandidatura) {
		this.hasCandidatura = hasCandidatura;
	}

	public void setImgEditedName(String imgEditedName) {
		this.imgEditedName = imgEditedName;
	}

	/**
	 * @param isListCandidaturaPresent
	 *            the isListCandidaturaPresent to set
	 */
	public void setListCandidaturaPresent(boolean isListCandidaturaPresent) {
		this.isListCandidaturaPresent = isListCandidaturaPresent;
	}

	public void setListImg(List<SvImmagineDTO> listImg) {
		this.listImg = listImg;
	}

	public void setMapSezioniImg(TreeMap<String, ArrayList<Integer>> mapSezioniImg) {
		this.mapSezioniImg = mapSezioniImg;
	}

	public void setMyCurricula(List<SelectItem> myCurricula) {
		this.myCurricula = myCurricula;
	}

	public void setMyLettere(List<SelectItem> myLettere) {
		this.myLettere = myLettere;
	}

	public void setSezioniOptions(List<SelectItem> sezioniOptions) {
		this.sezioniOptions = sezioniOptions;
	}

	public void setSvDeSezioneHome(DeSvSezioneHome svDeSezioneHome) {
		this.deSvSezioneHome = svDeSezioneHome;
	}

	public void setTemplateOptions(List<SelectItem> templateOptions) {
		this.templateOptions = templateOptions;
	}

	public AcCandidaturaDTO getCandidatura() {
		return candidatura;
	}

	public SvAziendaInfoDTO getData() {
		return data;
	}

	public SvImmagineDTO getDataImg() {
		return dataImg;
	}

	/**
	 * Restituisce le immagini legate alla sezione indicata come parametro
	 * 
	 * @param codSezione
	 *            String
	 * @return ArrayList
	 */
	public ArrayList<Integer> getImg(String codSezione) {
		return mapSezioniImg.get(codSezione);
	}

	public String getImgEditedName() {
		return imgEditedName;
	}

	public List<SvImmagineDTO> getListImg() {
		return listImg;
	}

	public Map<String, ArrayList<Integer>> getMapSezioniImg() {
		return mapSezioniImg;
	}

	public List<SelectItem> getMyCurricula() {
		return myCurricula;
	}

	public List<SelectItem> getMyLettere() {
		return myLettere;
	}

	public List<SelectItem> getSezioniOptions() {
		return sezioniOptions;
	}

	public DeSvSezioneHome getSvDeSezioneHome() {
		return deSvSezioneHome;
	}

	public List<SelectItem> getTemplateOptions() {
		return templateOptions;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public boolean isHasCandidatura() {
		return hasCandidatura;
	}

	/**
	 * @return the isListCandidaturaPresent
	 */
	public boolean isListCandidaturaPresent() {
		return isListCandidaturaPresent;
	}

	public Integer getIdVetrina() {
		return idVetrina;
	}

	public void setIdVetrina(Integer idVetrina) {
		this.idVetrina = idVetrina;
	}

	public UploadedFile getAllegatoUploaded() {
		return allegatoUploaded;
	}

	public void setAllegatoUploaded(UploadedFile allegatoUploaded) {
		this.allegatoUploaded = allegatoUploaded;
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(ID_VETRINA, idVetrina);
		return ret;
	}

	@Override
	protected void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		idVetrinaDaSess = ObjectUtils.toString(restoreParameters.get(ID_VETRINA));
	};
	
	
	class VacancyLazyList extends LazyDataModel<VaDatiVacancyDTO> {
		
		private static final long serialVersionUID = -8841405936858602430L;
		private boolean proprietarioVetrina;
		private boolean falseDiretteTruePalesi;
		private boolean filterByDFD;
		
		public VacancyLazyList(boolean proprietarioVetrina, boolean falseDiretteTruePalesi, boolean filterByDFD) {
			this.proprietarioVetrina = proprietarioVetrina;
			this.falseDiretteTruePalesi = falseDiretteTruePalesi;
			this.filterByDFD = filterByDFD;
			
			long countLong = vaDatiVacancyHome.countVacanciesByIdPfPrincipalAzienda(idPfPrincipalAzienda, session.getPrincipalId(), 
					proprietarioVetrina, falseDiretteTruePalesi, filterByDFD);
			int countInt = (int)countLong;
			setRowCount(countInt);
		}
		
		@Override
		public List<VaDatiVacancyDTO> load(int startingAt, int maxPerPage, String sortField, SortOrder sortOrder,
				Map<String, Object> filters) {
			
			List<VaDatiVacancyDTO> vacancyDtolist = vaDatiVacancyHome.findLazyVacanciesByIdPfPrincipalAzienda(startingAt, maxPerPage, idPfPrincipalAzienda,
					session.getPrincipalId(), proprietarioVetrina, falseDiretteTruePalesi, filterByDFD);
			
			// set the total of players
	        if(getRowCount() <= 0){
	        	long countLong = vaDatiVacancyHome.countVacanciesByIdPfPrincipalAzienda(idPfPrincipalAzienda, session.getPrincipalId(), 
	        																				proprietarioVetrina, falseDiretteTruePalesi, filterByDFD);
	        	int countInt = (int)countLong;
	        	setRowCount(countInt);
	        }
	 
	        // set the page dize
	        setPageSize(maxPerPage);
	        
			return vacancyDtolist;
			
		}

	}

	public LazyDataModel<VaDatiVacancyDTO> getAllVacanciesDiretteLazy() {
		if(vacanciesDiretteLazy == null){
			vacanciesDiretteLazy = new VacancyLazyList(proprietarioVetrina, false, false);
		}
		return vacanciesDiretteLazy;
	}
	
	public LazyDataModel<VaDatiVacancyDTO> getAllVacanciesPalesiLazy() {
		if(vacanciesPalesiLazy == null){
			if(utils.isRER()) {
				vacanciesPalesiLazy = new VacancyLazyList(proprietarioVetrina, true, true);
			}else {
				vacanciesPalesiLazy = new VacancyLazyList(proprietarioVetrina, true, false);				
			}
		}
		return vacanciesPalesiLazy;
	}
	
	public LazyDataModel<VaDatiVacancyDTO> getVacanciesPalesiLazy() {
		return vacanciesPalesiLazy;
	}

	public LazyDataModel<VaDatiVacancyDTO> getVacanciesDiretteLazy() {
		return vacanciesDiretteLazy;
	}

	public void setVacanciesDiretteLazy(LazyDataModel<VaDatiVacancyDTO> vacanciesDiretteLazy) {
		this.vacanciesDiretteLazy = vacanciesDiretteLazy;
	}

	public void setVacanciesPalesiLazy(LazyDataModel<VaDatiVacancyDTO> vacanciesPalesiLazy) {
		this.vacanciesPalesiLazy = vacanciesPalesiLazy;
	}

	public long getCountVacanciesDirette() {
		return countVacanciesDirette;
	}

	public void setCountVacanciesDirette(long countVacanciesDirette) {
		this.countVacanciesDirette = countVacanciesDirette;
	}

	public long getCountVacanciesPalesi() {
		return countVacanciesPalesi;
	}

	public void setCountVacanciesPalesi(long countVacanciesPalesi) {
		this.countVacanciesPalesi = countVacanciesPalesi;
	}
	
	
}
