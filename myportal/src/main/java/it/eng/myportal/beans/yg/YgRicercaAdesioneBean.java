package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.YgMiniSapAdesioneDTO;
import it.eng.myportal.dtos.YgRicercaAdesioneDTO;
import it.eng.myportal.dtos.YgRisultatoRicercaAdesioneDTO;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeSapEjb;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.YgAssegnazioneProvincia;
import it.eng.myportal.enums.YgRegioneRifNotifica;
import it.eng.myportal.enums.YgStatoPresaCarico;
import it.eng.myportal.enums.YgTipoAdesione;
import it.eng.myportal.enums.YgTipoRicercaAdesione;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;
import it.eng.myportal.youthGuarantee.sap.Datianagrafici;
import it.eng.myportal.youthGuarantee.sap.LavoratoreType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class YgRicercaAdesioneBean extends AbstractBaseBean {

	private List<SelectItem> tipiRicerca;
	private List<SelectItem> tipiAdesione;
	private List<SelectItem> statiPresaCarico;
	private List<SelectItem> regioniRifNotifica;
	private List<SelectItem> assegnazioniProvincia;

	private YgRicercaAdesioneDTO parametriRicerca = new YgRicercaAdesioneDTO();
	private YgRicercaAdesioneDTO parametriRicercaCSV = new YgRicercaAdesioneDTO();

	private List<YgRisultatoRicercaAdesioneDTO> risultatoRicerca = new ArrayList<YgRisultatoRicercaAdesioneDTO>();

	private static final String PARAMETRI_RICERCA = "parametriRicerca";
	private boolean ricercaEseguita = false;
	private String annullamentoEseguito = "";
	private PaginationHandler paginationHandler;
	private YgMiniSapAdesioneDTO ygMiniSapAdesioneDTO = new YgMiniSapAdesioneDTO();
	private String richiestaSapEseguita = "";
	private List<SelectItem> cpiSelectItems;
	private DeCpiDTO cpiAdesione;
	private String csv;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YouthGuaranteeAdesioneEjb ygAdesioneEjb;

	@EJB
	private YouthGuaranteeSapEjb youthGuaranteeSapEjb;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeProvinciaHome deProviniciaHome;

	@EJB
	private DeCpiHome deCpiHome;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		ProvinciaDTO provincia = session.getConnectedProvincia();

		tipiRicerca = YgTipoRicercaAdesione.asSelectItems();
		tipiAdesione = YgTipoAdesione.asSelectItems();
		statiPresaCarico = YgStatoPresaCarico.asSelectItems();
		regioniRifNotifica = YgRegioneRifNotifica.asSelectItems();
		assegnazioniProvincia = YgAssegnazioneProvincia.asSelectItems();

		cpiSelectItems = deCpiHome.getListItemsCpiByProvincia(provincia.getProvincia().getId(), true);

		if (risultatoRicerca != null && !risultatoRicerca.isEmpty()) {
			ricercaEseguita = true;
		} else {
			ricercaEseguita = false;
		}
	}

	/**
	 * Controlla se la pagina è visibile dall'utente attuale.
	 */
	public void checkViewPage() {
		// boolean checkView = getSession().isAbilitato("_portlet_yg_ricerca",
		// TipoAbilitazione.VISIBILE);

		// modifica del 08/05/2014
		// non è più necessario essere abilitati
		// ai servizi amministrativi per
		// poter accedere alla parte di YG
		boolean checkView = true;

		if (!checkView) {
			getExternalContext().setResponseStatus(404);
			getFacesContext().responseComplete();
			redirectHome();
			return;
		}

		if (!session.isProvincia()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione provincia.");
			redirectHome();
			return;
		}

	}

	/*
	 * pagination event filter the next chuck of records from the database and refresh the result list (risultato) the
	 * max records per page has already been set in the postConstruct() AJAX call
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		parametriRicerca.setStartResultsFrom(startResultsFrom);
		parametriRicerca.setPage(paginationHandler.getCurrentPage());
		loadPage();
	}

	/**
	 * 
	 * @param newSearch
	 *            true se e' una nuova ricerca che fa ripartire la paginazione
	 */
	public void search(boolean nuovaRicerca) {
		// Controllo se l'utente è di tipo provincia.
		if (!session.isProvincia()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione provincia.");
			redirectHome();
			return;
		}

		// Se sto facendo una nuova ricerca, setto la pagina ad 1.
		// Altrimenti, ripristinerò la pagina a cui ero.
		if (nuovaRicerca) {
			parametriRicerca.setPage(1);
		}

		// L'esportazione CSV riesegue la ricerca con MAXRESULTS e STARTRESULT
		// pari a 0 e gli stessi parametri della ricerca normale.
		csv = null;
		parametriRicercaCSV = parametriRicerca.clone();

		// Inizializzo il pagination handler.
		paginationHandler = new PaginationHandler(parametriRicerca.getStartResultsFrom(),
				parametriRicerca.getMaxResults(), ygAdesioneHome.findCountByFilter(parametriRicerca, getSession()
						.getPrincipalId()));
		paginationHandler.setCurrentPage(parametriRicerca.getPage());

		// Carico il risultato della ricerca.
		loadPage();
		ricercaEseguita = true;
	}

	/**
	 * Carica i dati relativi ad una pagina dei risultati di ricerca.
	 */
	private void loadPage() {
		risultatoRicerca = ygAdesioneHome.findByFilter(parametriRicerca, getSession().getPrincipalId());
		putParamsIntoSession();
	}

	public void save() {
		if (!session.isProvincia()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione provincia.");
			redirectHome();
			return;
		}

		List<YgRisultatoRicercaAdesioneDTO> adesioniModificate = new ArrayList<YgRisultatoRicercaAdesioneDTO>();

		for (YgRisultatoRicercaAdesioneDTO adesioneCorrente : risultatoRicerca) {

			boolean isModified = false;

			if ((adesioneCorrente.getDtPresoInCarico() == null && adesioneCorrente.getModificaDtPresoInCarico() != null)
					|| (adesioneCorrente.getDtPresoInCarico() != null
							&& adesioneCorrente.getModificaDtPresoInCarico() != null && !adesioneCorrente
							.getDtPresoInCarico().equals(adesioneCorrente.getModificaDtPresoInCarico()))

					|| (adesioneCorrente.getDtFineStatoAdesione() == null && adesioneCorrente
							.getModificaDtFineStatoAdesione() != null)
					|| (adesioneCorrente.getDtFineStatoAdesione() != null && adesioneCorrente
							.getModificaDtFineStatoAdesione() == null)
					|| (adesioneCorrente.getDtFineStatoAdesione() != null
							&& adesioneCorrente.getModificaDtFineStatoAdesione() != null && !adesioneCorrente
							.getDtFineStatoAdesione().equals(adesioneCorrente.getModificaDtFineStatoAdesione()))

					|| (adesioneCorrente.getNote() == null && adesioneCorrente.getModificaNote() != null)
					|| (adesioneCorrente.getNote() != null && adesioneCorrente.getModificaNote() == null)
					|| (adesioneCorrente.getNote() != null && adesioneCorrente.getModificaNote() != null && !adesioneCorrente
							.getNote().equalsIgnoreCase(adesioneCorrente.getModificaNote()))

					|| (adesioneCorrente.getCodStatoAdesione() == null && adesioneCorrente
							.getModificaCodStatoAdesione() != null)
					|| (adesioneCorrente.getCodStatoAdesione() != null && adesioneCorrente
							.getModificaCodStatoAdesione() == null)
					|| (adesioneCorrente.getCodStatoAdesione() != null
							&& adesioneCorrente.getModificaCodStatoAdesione() != null && !adesioneCorrente
							.getCodStatoAdesione().equalsIgnoreCase(adesioneCorrente.getModificaCodStatoAdesione()))) {
				isModified = true;
			}

			if (isModified) {
				adesioniModificate.add(adesioneCorrente);
			}

		}

		if (!adesioniModificate.isEmpty()) {
			ygAdesioneHome.updateAdesioni(adesioniModificate, getSession().getPrincipalId());
			addInfoMessage("data.updated");
		} else {
			addInfoMessage("data.no_update");
		}

		changePage();
	}

	public void viewDatiSapMin() {
		try {
			ygMiniSapAdesioneDTO = new YgMiniSapAdesioneDTO();
			Map<String, String> map = getRequestParameterMap();
			String idString = map.get("id");
			LavoratoreType lavSap = new LavoratoreType();

			try {
				lavSap = ygAdesioneHome.callRichiestaSap(new Integer(idString));
			} catch (Exception e) {
				richiestaSapEseguita = "KO";
				log.error("chiamata servizio richiesta sap min: " + e.getMessage());
				return;
			}

			if (lavSap == null) {
				richiestaSapEseguita = "NULLA";
			} else {
				Datianagrafici datianagrafici = lavSap.getDatianagrafici();
				if (datianagrafici != null) {
					ygMiniSapAdesioneDTO.setCodiceFiscale(datianagrafici.getDatipersonali().getCodicefiscale());
					ygMiniSapAdesioneDTO.setCognome(datianagrafici.getDatipersonali().getCognome());
					ygMiniSapAdesioneDTO.setNome(datianagrafici.getDatipersonali().getNome());
					ygMiniSapAdesioneDTO.setEmail(datianagrafici.getRecapiti().getEmail());
					ygMiniSapAdesioneDTO.setCellulare(datianagrafici.getRecapiti().getCellulare());
					ygMiniSapAdesioneDTO.setTelefono(datianagrafici.getRecapiti().getTelefono());

					ygMiniSapAdesioneDTO.setIndirizzoDom(datianagrafici.getDomicilio().getIndirizzo());
					ygMiniSapAdesioneDTO.setCapDom(datianagrafici.getDomicilio().getCap());
					String codComuneDom = datianagrafici.getDomicilio().getCodcomune();
					DeComuneDTO deCodComDom = deComuneHome.findDTOById(codComuneDom);
					ygMiniSapAdesioneDTO.setComuneDom(deCodComDom.getDescrizione());
					DeProvinciaDTO deProvCom = deProviniciaHome.findDTOById(deCodComDom.getIdProvincia());
					ygMiniSapAdesioneDTO.setTargaProvDom(deProvCom.getTarga());

					ygMiniSapAdesioneDTO.setIndirizzoRes(datianagrafici.getResidenza().getIndirizzo());
					ygMiniSapAdesioneDTO.setCapRes(datianagrafici.getResidenza().getCap());
					String codComuneRes = datianagrafici.getResidenza().getCodcomune();
					DeComuneDTO deCodComRes = deComuneHome.findDTOById(codComuneRes);
					ygMiniSapAdesioneDTO.setComuneRes(deCodComRes.getDescrizione());
					DeProvinciaDTO deProvRes = deProviniciaHome.findDTOById(deCodComRes.getIdProvincia());
					ygMiniSapAdesioneDTO.setTargaProvRes(deProvRes.getTarga());

					richiestaSapEseguita = "OK";
				} else {
					richiestaSapEseguita = "NULLA";
				}
			}
		} catch (Exception e) {
			richiestaSapEseguita = "KO";
			log.error("chiamata servizio richiesta sap min: " + e.getMessage());
			return;
		}
	}

	public void annullaAdesione() {
		try {
			int idString = Integer.parseInt(getRequestParameterEndsWith("id_delete"));

			log.warn("Annullamento manuale richiesto. Id Adesione: " + idString);
			ygAdesioneEjb.annullaAdesioneYG(idString, session.getConnectedProvincia().getIdPfPrincipal(), "C");
			annullamentoEseguito = "OK";

			// ripeto ricerca
			search(false);
			addInfoMessage("yg.canceled_noreq");
		} catch (MyPortalException me) {
			annullamentoEseguito = "KO";
			gestisciErrore(me, null);
			log.error("errore applicativo Annulla adesione: " + me.getMessage());
			return;
		} catch (Exception e) {
			annullamentoEseguito = "KO";
			addErrorMessage("yg.error_canceling");
			log.error("errore di sistema Annulla adesione Mancanza requisiti:  " + e.getMessage());
			return;
		}
	}

	public void annullaAdesioneMancato() {
		try {
			int idString = Integer.parseInt(getRequestParameterEndsWith("id_mancato"));

			log.warn("Annullamento manuale richiesto. Id Adesione: " + idString);
			ygAdesioneEjb.annullaAdesioneYG(idString, session.getConnectedProvincia().getIdPfPrincipal(), "U");
			annullamentoEseguito = "OK";

			// ripeto ricerca
			search(false);
			addInfoMessage("yg.canceled_noapp");
		} catch (MyPortalException me) {
			annullamentoEseguito = "KO";
			gestisciErrore(me, null);
			log.error("errore applicativo Annulla adesione: " + me.getMessage());
			return;
		} catch (Exception e) {
			annullamentoEseguito = "KO";
			addErrorMessage("yg.error_canceling");
			log.error("errore di sistema Annulla adesione Mancanza requisiti: " + e.getMessage());
			return;
		}
	}

	// Crea e restituisce un file .csv contenente i risultati della ricerca.
	public StreamedContent downloadCSV() {
		// Crea il file.
		parametriRicercaCSV.setStartResultsFrom(0);
		parametriRicercaCSV.setMaxResults(0);
		csv = ygAdesioneHome.findByFilterCSV(parametriRicercaCSV, getSession().getPrincipalId());

		// Prepara lo stream del file e lo restituisce.
		String csvFilename = "Risultato_Ricerca_Adesioni_Garanzia_Giovani.csv";
		byte[] buffer = csv.getBytes(Charset.forName("UTF-8"));
		InputStream stream = new ByteArrayInputStream(buffer);
		StreamedContent file = new DefaultStreamedContent(stream, "text/csv", csvFilename);
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.beans.AbstractBaseBean#generateRestoreParams()
	 * 
	 * metodo per salvarmi i parametri. Viene chiamato da putParamsIntoSession()
	 */
	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(PARAMETRI_RICERCA, parametriRicerca);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.beans.AbstractBaseBean#ricreaStatoDaSessione(it.eng.myportal .beans.RestoreParameters)
	 * 
	 * metodo per recuperare i parametri. Viene chiamato da AbstractBaseBean.postConstruct()
	 */
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		parametriRicerca = (YgRicercaAdesioneDTO) restoreParams.get(PARAMETRI_RICERCA);
		search(false);
	}

	public boolean isStoricoAdesioneEnabled() {
		return ConstantsSingleton.isYgStoricoEnabled();
	}

	public YgRicercaAdesioneDTO getParametriRicerca() {
		return parametriRicerca;
	}

	public void setParametriRicerca(YgRicercaAdesioneDTO parametriRicerca) {
		this.parametriRicerca = parametriRicerca;
	}

	public List<SelectItem> getTipiRicerca() {
		return tipiRicerca;
	}

	public void setTipiRicerca(List<SelectItem> tipiRicerca) {
		this.tipiRicerca = tipiRicerca;
	}

	public List<SelectItem> getTipiAdesione() {
		return tipiAdesione;
	}

	public void setTipiAdesione(List<SelectItem> tipiAdesione) {
		this.tipiAdesione = tipiAdesione;
	}

	public List<SelectItem> getStatiPresaCarico() {
		return statiPresaCarico;
	}

	public void setStatiPresaCarico(List<SelectItem> statiPresaCarico) {
		this.statiPresaCarico = statiPresaCarico;
	}

	public List<YgRisultatoRicercaAdesioneDTO> getRisultatoRicerca() {
		return risultatoRicerca;
	}

	public void setRisultatoRicerca(List<YgRisultatoRicercaAdesioneDTO> risultatoRicerca) {
		this.risultatoRicerca = risultatoRicerca;
	}

	public boolean isRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public void setPaginationHandler(PaginationHandler paginationHandler) {
		this.paginationHandler = paginationHandler;
	}

	public List<SelectItem> getRegioniRifNotifica() {
		return regioniRifNotifica;
	}

	public void setRegioniRifNotifica(List<SelectItem> regioniRifNotifica) {
		this.regioniRifNotifica = regioniRifNotifica;
	}

	public List<SelectItem> getAssegnazioniProvincia() {
		return assegnazioniProvincia;
	}

	public void setAssegnazioniProvincia(List<SelectItem> assegnazioniProvincia) {
		this.assegnazioniProvincia = assegnazioniProvincia;
	}

	public YgMiniSapAdesioneDTO getYgMiniSapAdesioneDTO() {
		return ygMiniSapAdesioneDTO;
	}

	public void setYgMiniSapAdesioneDTO(YgMiniSapAdesioneDTO ygMiniSapAdesioneDTO) {
		this.ygMiniSapAdesioneDTO = ygMiniSapAdesioneDTO;
	}

	public String getRichiestaSapEseguita() {
		return richiestaSapEseguita;
	}

	public void setRichiestaSapEseguita(String richiestaSapEseguita) {
		this.richiestaSapEseguita = richiestaSapEseguita;
	}

	public DeCpiDTO getCpiAdesione() {
		return cpiAdesione;
	}

	public void setCpiAdesione(DeCpiDTO cpiAdesione) {
		this.cpiAdesione = cpiAdesione;
	}

	public List<SelectItem> getCpiSelectItems() {
		return cpiSelectItems;
	}

	public void setCpiSelectItems(List<SelectItem> cpiSelectItems) {
		this.cpiSelectItems = cpiSelectItems;
	}

	public String getAnnullamentoEseguito() {
		return annullamentoEseguito;
	}

	public void setAnnullamentoEseguito(String annullamentoEseguito) {
		this.annullamentoEseguito = annullamentoEseguito;
	}

}
