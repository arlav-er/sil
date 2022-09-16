package it.eng.myportal.beans.offertelavoro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.dtos.RvGroupDTO;
import it.eng.myportal.dtos.RvRicercaVacancyDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean della pagina che xpermette di cercare tra le offerte di lavoro
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class RicercaOfferteLavoroBean extends AbstractEditableBean<RvTestataDTO> {

	private static final String PATH_GEOSERVER = "&fl=id_va_dati_vacancy,descrizione,comune,longitudine,latitudine&wt=json";

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	DeSezioneInfoDTO info;

	/**
	 * Logger per registrare informazioni.
	 */
	private static Log log = LogFactory.getLog(RicercaOfferteLavoroBean.class);

	@EJB
	transient RvRicercaVacancyHome rvRicercaVacancyHome;

	@EJB
	transient RvTestataHome rvTestataHome;

	@EJB
	transient VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	transient AcCandidaturaHome acCandidaturaHome;

	@EJB
	transient UtenteInfoHome utenteInfoHome;

	private VaDatiVacancyDTO vaDatiVacancyDTO;

	private RvGroupDTO rvGroupDTO;

	/* elenco delle vacancy risultato della ricerca */
	private List<RvRicercaVacancyDTO> offerteLavoro;

	private List<SelectItem> myCurricula;
	private List<SelectItem> myClicLavoroCurricula;
	private List<SelectItem> myLettere;

	private List<String> gruppi;
	/*
	 * Parametro hidden utilizzata lato pagina, per passare la queryString di SOLR al plugin della mappa
	 */
	private String ParamSOLR;

	private Boolean disableCandidatura;

	private transient AcCandidaturaDTO candidatura;

	public List<RvRicercaVacancyDTO> getOfferteLavoro() {
		return offerteLavoro;
	}

	public void setOfferteLavoro(List<RvRicercaVacancyDTO> offerteLavoro) {
		this.offerteLavoro = offerteLavoro;
	}

	/**
	 * recupera l'elenco delle Vacancy e dei Template dal DB ed inizializza il Bean
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".postConstruct");
		try {
			super.postConstruct();

			/*
			 * Commento il reset del caricamento perchè ha un bug in caso di torna indietro da una vacancy visualizzata
			 * alla lista dei risultati della ricerca. Inoltre non ho trovato nessun caso in cui sia necessario
			 * resettare i campi.
			 */
			// if (data.getId() == null && data.getCosa() == null &&
			// data.getDove() == null) {
			// resettaCaricamento();
			// }
			gruppi = new ArrayList<String>();
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.MANSIONE);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.CONTRATTO);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.ORARIO);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.ESPERIENZA);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.SETTORE);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.LINGUA);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.DISP_TRASFERTE);
			gruppi.add(ConstantsSingleton.RvRicercaVacancy.PATENTE);

			info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_RICERCA_VACANCY_COSA);

			log.debug("Costruito il Bean per visualizzare l'elenco delle Vacancy secondo i criteri di ricerca.");
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Resetta i contatori al caricamento iniziale
	 */
	private void resettaCaricamento() {
		data.setRowsLoaded(0);
		data.setRowsForLoad(data.getRowsToLoad());
		data.setRowsTotal(0);
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
			} else {
				throw new RuntimeException(
						"Il salvataggio del filtro della ricerca e' previsto solo per gli \"utenti\" al momento.");
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
		} else {
			throw new RuntimeException(
					"Il salvataggio del filtro della ricerca e' previsto solo per gli \"utenti\" al momento.");
		}
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

	@Override
	protected RvTestataDTO retrieveData() {
		// data = null;
		if (data != null && beanParamsSess != null) {
			log.debug("dati recuperati dalla sessione.");

			/* Resetto i contatori */
			// resettaCaricamento();
			// dopo la ricerca il contatore viene incrementato,
			// in caso di ri-caricamento by sessione lo decremento
			data.setRowsForLoad(data.getRowsForLoad() - data.getRowsToLoad());

			// richiamo la ricerca delle vacancy in base a tutti i filtri
			// salvati
			search();
			return data;
		}

		if (session.isUtente() || session.isProvincia()) {
			Map<String, String> map = getRequestParameterMap();
			try {
				rvGroupDTO = new RvGroupDTO();
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
					myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(getSession().getConnectedUtente().getId());
					myClicLavoroCurricula = utenteInfoHome.getAllClicLavoroCurriculaAsSelectItem(getSession()
							.getConnectedUtente().getId());
					myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(getSession().getConnectedUtente().getId());
					candidatura = new AcCandidaturaDTO();
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
		// recupero i gruppi presenti sul DB
		RvGroupDTO rvGroupDTODB = rvTestataHome.loadGruppi(testata);

		/*
		 * Recupero il menu dei gruppi in base ai filtri recuperati dal 'Cosa' e 'Dove'
		 */
		rvRicercaVacancyHome.findGroupByFilter(data, rvGroupDTO, true, usaDecodificheSil());

		// check a true tutti i gruppi recuperati dal DB
		setCheckTrue(rvGroupDTODB.getCheckMansione(), rvGroupDTO.getCheckMansione());
		setCheckTrue(rvGroupDTODB.getCheckContratto(), rvGroupDTO.getCheckContratto());
		setCheckTrue(rvGroupDTODB.getCheckOrario(), rvGroupDTO.getCheckOrario());
		setCheckTrue(rvGroupDTODB.getCheckEsperienza(), rvGroupDTO.getCheckEsperienza());
		setCheckTrue(rvGroupDTODB.getCheckSettore(), rvGroupDTO.getCheckSettore());
		setCheckTrue(rvGroupDTODB.getCheckLingua(), rvGroupDTO.getCheckLingua());
		setCheckTrue(rvGroupDTODB.getCheckTitoloStudio(), rvGroupDTO.getCheckTitoloStudio());
		setCheckTrue(rvGroupDTODB.getCheckDispTrasferte(), rvGroupDTO.getCheckDispTrasferte());
		setCheckTrue(rvGroupDTODB.getCheckPatente(), rvGroupDTO.getCheckPatente());

		/* Resetto i contatori */
		resettaCaricamento();

		// richiamo la ricerca delle vacancy in base a tutti i filtri salvati
		search();

		// Verifico la presenza di nuove Vacancy
		// searchNewVacancy();

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

	/**
	 * Recupera tutti i filtri di secondo livello per salvarli nel DB
	 */
	private void setFiltriGroup() {
		data.setSetMansione(getCheckTrue(rvGroupDTO.getMapMansione(), rvGroupDTO.getCheckMansione()));
		data.setSetContratto(getCheckTrue(rvGroupDTO.getMapContratto(), rvGroupDTO.getCheckContratto()));
		data.setSetOrario(getCheckTrue(rvGroupDTO.getMapOrario(), rvGroupDTO.getCheckOrario()));
		// data.setSetEsperienza(getCheckTrue(rvGroupDTO.getMapEsperienza(),rvGroupDTO.getCheckEsperienza()));
		data.setSetSettore(getCheckTrue(rvGroupDTO.getMapSettore(), rvGroupDTO.getCheckSettore()));
		data.setSetLingua(getCheckTrue(rvGroupDTO.getMapLingua(), rvGroupDTO.getCheckLingua()));
		data.setSetTitoloStudio(getCheckTrue(rvGroupDTO.getMapTitoloStudio(), rvGroupDTO.getCheckTitoloStudio()));
		// data.setSetDispTrasferte(getCheckTrue(rvGroupDTO.getMapDispTrasferte(),rvGroupDTO.getCheckDispTrasferte()));
		data.setSetPatente(getCheckTrue(rvGroupDTO.getMapPatente(), rvGroupDTO.getCheckPatente()));
	}

	/**
	 * Registra l'invio della candidatura relativa alla vacancy selezionata.
	 */
	public void inviaCandidatura() {
		String id = getRequestParameterEndsWith("objectId");
		Integer idPfPrincipalAzienda = null;
		if (id != null) {
			idPfPrincipalAzienda = Integer.parseInt(id);
		}
		String vaId = getRequestParameterEndsWith("vaId");
		Integer idVaDatiVacancy = null;
		if (vaId != null) {
			idVaDatiVacancy = Integer.parseInt(vaId);
		}
		Integer idPrincipal = session.getPrincipalId();

		try {
			Boolean invioCandidatura = acCandidaturaHome.inviaCandidatura(candidatura, idPrincipal,
					idPfPrincipalAzienda, idVaDatiVacancy, null);

			if (invioCandidatura) {
				search();
				addInfoMessage("candidatura.sent");
			} else {
				addErrorMessage("vacancy.cv_obbligatorio");
			}
		} catch (MyPortalException e) {
			gestisciErrore(e, "candidatura.allegato.error");
		}
	}

	/**
	 * Invoca la ricerca resettando i parametri dall'oggetto RvTestataDTO
	 */
	public void resetSearch() {
		data = new RvTestataDTO();
		rvGroupDTO = new RvGroupDTO();
		// startSearch();
		offerteLavoro = null;
	}

	/**
	 * Invoca la ricerca delle vacancy con il RvTestataDTO corrente come filtro della ricerca.
	 */
	public void startSearch() {
		/* Resetto i contatori */
		resettaCaricamento();
		/* Recupero il menu dei raggruppamenti(check) */
		rvRicercaVacancyHome.findGroupByFilter(data, rvGroupDTO, true, usaDecodificheSil());
		/* Effettuo la ricerca */
		search();
	}
	
	/**
	 * Invoca la ricerca delle vacancy dal menu dei gruppi check, con l'oggetto RvTestataDTO passato corrente come
	 * filtro della ricerca.
	 * 
	 * @param menu
	 *            String
	 * @param keyMap
	 *            String
	 * @param map
	 *            Map<String, String>
	 */
	public void searchCheck(String menu, String keyMap, Map<String, String> map) {
		String key = map.get(keyMap);
		/* Resetto i contatori */
		resettaCaricamento();
		/* Effettuo la ricerca */
		if (session.isUtente()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByFilter(data, rvGroupDTO, menu, key, getSession()
					.getConnectedUtente().getId(), usaDecodificheSil());
		} else if (session.isProvincia()) {
			offerteLavoro = rvRicercaVacancyHome
					.findDTOByFilter(data, rvGroupDTO, menu, key, null, usaDecodificheSil());
		}

		urlMappa(key);
		searchNewVacancy();
	}

	/**
	 * Invoca la ricerca delle vacancy con il RvTestataDTO corrente come filtro della ricerca.
	 */
	public void search() {
		urlMappa(null);

		if (session.isUtente()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByFilter(data, rvGroupDTO, getSession().getConnectedUtente()
					.getId(), usaDecodificheSil());
		} else if (session.isProvincia()) {
			offerteLavoro = rvRicercaVacancyHome.findDTOByFilter(data, rvGroupDTO, null, usaDecodificheSil());
		}

		searchNewVacancy();
	}

	/**
	 * Genera l'url da passare alla mappa
	 */
	private void urlMappa(String key) {
		// recupero l'url utile per la mappa
		String paramMappa = rvRicercaVacancyHome.getParam(key, data, rvGroupDTO, usaDecodificheSil());
		// elimino i parametri che non interessano alla query della mappa
		paramMappa = paramMappa.substring(0, paramMappa.lastIndexOf("&wt")) + "&start=0&rows=9999";
		paramMappa += PATH_GEOSERVER;
		// paramMappa = paramMappa.replaceAll("\\+AND\\+", "\\+__AND__\\+");
		// paramMappa = paramMappa.replaceAll("%20OR%20", "%20__OR__%20");
		// setto l'url su questa variabile hidden, per essere gestita lato
		// pagina dal plugin della mappa
		setParamSOLR(paramMappa);
	}

	/**
	 * Verifica che ci sia almeno un check a true
	 * 
	 * @param map
	 *            Map<String, Boolean>
	 * @return boolean
	 */
	public boolean searchCheckTrue(Map<String, Boolean> map) {
		for (Iterator<Boolean> iterator = map.values().iterator(); iterator.hasNext();) {
			if (iterator.next()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recupero un Set<String> per ogni check a true della mappa passata come parametro
	 * 
	 * @param mapKey
	 *            Map<String, String>
	 * @param map
	 *            Map<String, Boolean>
	 * @return Set<String>
	 */
	public Set<String> getCheckTrue(Map<String, String> mapKey, Map<String, Boolean> map) {
		Set<String> setCheck = new HashSet<String>();
		for (Iterator<String> iterator = mapKey.values().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			boolean check = map.get(key);
			if (check) {
				setCheck.add(key);
			}
		}
		return setCheck;
	}

	/**
	 * Set a true tutti i check trovati nel DB
	 * 
	 * @param mapDB
	 *            Map<String, Boolean>
	 * @param map
	 *            Map<String, Boolean>
	 */
	public void setCheckTrue(Map<String, Boolean> mapDB, Map<String, Boolean> map) {
		for (Iterator<String> iterator = mapDB.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (map.containsKey(key)) {
				map.put(key, true);
			}
		}
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

	public RvGroupDTO getRvGroupDTO() {
		return rvGroupDTO;
	}

	public void setRvGroupDTO(RvGroupDTO rvGroupDTO) {
		this.rvGroupDTO = rvGroupDTO;
	}

	public List<String> getGruppi() {
		return gruppi;
	}

	public void setGruppi(List<String> gruppi) {
		this.gruppi = gruppi;
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

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put("data", data);
		ret.put("gruppi", rvGroupDTO);
		ret.put("myCurricula", myCurricula);
		ret.put("myLettere", myLettere);
		return ret;
	}

	@Override
	protected void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		this.data = (RvTestataDTO) restoreParams.get("data");
		this.rvGroupDTO = (RvGroupDTO) restoreParams.get("gruppi");
		this.myCurricula = (List<SelectItem>) restoreParams.get("myCurricula");
		this.myLettere = (List<SelectItem>) restoreParams.get("myLettere");
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

	public boolean usaDecodificheSil() {
		return ConstantsSingleton.usaDecodificheSilPerVacancy();
	}

}