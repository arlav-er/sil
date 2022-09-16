package it.eng.myportal.beans;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RicercaUtenteDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.enums.TipoAccount;
import it.eng.myportal.enums.TipoRegistrazione;
import it.eng.myportal.enums.TipoRicercaUtente;
import it.eng.myportal.utils.PaginationHandler;
import it.eng.myportal.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * BackingBean della pagina che permette di cercare tra i lavoratori di competenza della provincia
 *
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class RicercaLavoratoriBean extends AbstractBaseBean implements Serializable {

	private static final String PARAMETRI_RICERCA = "parametriRicerca";

	private static final long serialVersionUID = 7668358908366984319L;

	private static Log log = LogFactory.getLog(RicercaLavoratoriBean.class);

	private boolean ricercaEseguita = false;

	private PaginationHandler paginationHandler;

	@EJB
	transient UtenteInfoHome utenteInfoHome;

	@EJB
	transient PfPrincipalHome pfPrincipalHome;

	@EJB
	transient DeProvenienzaHome deProvenienzaHome;

	private List<UtenteCompletoDTO> risultato;

	private RicercaUtenteDTO parametriRicerca;
	private RicercaUtenteDTO parametriRicercaCSV;

	private List<SelectItem> tipiRicerca;
	private List<SelectItem> tipiRegistrazione;
	private List<SelectItem> tipiAccount;
	private List<SelectItem> provenienzeCurriculum;
	private String csv;

	public List<UtenteCompletoDTO> getRisultato() {
		return risultato;
	}

	public void setRisultato(List<UtenteCompletoDTO> results) {
		this.risultato = results;
	}

	public RicercaUtenteDTO getParametriRicerca() {
		return parametriRicerca;
	}

	public void setParametriRicerca(RicercaUtenteDTO parametriRicerca) {
		this.parametriRicerca = parametriRicerca;
	}

	public List<SelectItem> getTipiRicerca() {
		return tipiRicerca;
	}

	public void setTipiRicerca(List<SelectItem> tipiRicerca) {
		this.tipiRicerca = tipiRicerca;
	}

	public List<SelectItem> getTipiRegistrazione() {
		return tipiRegistrazione;
	}

	public void setTipiRegistrazione(List<SelectItem> tipiRegistrazione) {
		this.tipiRegistrazione = tipiRegistrazione;
	}

	public List<SelectItem> getTipiAccount() {
		return tipiAccount;
	}

	public void setTipiAccount(List<SelectItem> tipiAccount) {
		this.tipiAccount = tipiAccount;
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

	public List<SelectItem> getProvenienzeCurriculum() {
		return provenienzeCurriculum;
	}

	public void setProvenienzeCurriculum(List<SelectItem> provenienzeCurriculum) {
		this.provenienzeCurriculum = provenienzeCurriculum;
	}

	/**
	 * Recupera l'elenco delle Vacancy e dei Template dal DB ed inizializza il bean.
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (session.isProvincia() || session.isRegione()) {
			if (parametriRicerca == null) {
				parametriRicerca = new RicercaUtenteDTO();
			}
			if (risultato == null) {
				risultato = new ArrayList<UtenteCompletoDTO>();
			}

			tipiRicerca = TipoRicercaUtente.asSelectItems();
			tipiRegistrazione = TipoRegistrazione.asSelectItems();
			tipiAccount = TipoAccount.asSelectItems();
			provenienzeCurriculum = deProvenienzaHome.getListItems(true);

			log.debug("Costruito il Bean per cercare i lavoratori.");
		} else {
			log.warn("Tentativo di accedere alla ricerca lavoratori non dalla sezione provincia o regione.");
			redirectHome();
		}
	}

	/*
	 * pagination event filter the next chuck of records from the database and refresh the result list (risultato) the
	 * max records per page has already been set in the postConstruct() AJAX call
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		parametriRicerca.setStartResultsFrom(startResultsFrom);
		risultato = utenteInfoHome.findByFilter(parametriRicerca, getSession().getPrincipalId());
	}

	public void search() {
		risultato = new ArrayList<UtenteCompletoDTO>();
		csv = null;

		/* salvo i parametri di ricerca per poter tornare indietro */
		putParamsIntoSession();

		if (session.isProvincia() || session.isRegione()) {
			// sets the default records per page
			parametriRicerca.setMaxResults(5);
			parametriRicerca.setStartResultsFrom(0);

			/*
			 * l'esportazione CSV riesegue la ricerca con MAXRESULTS e STARTRESULT pari a 0 e gli stessi parametri della
			 * ricerca normale
			 */
			parametriRicercaCSV = parametriRicerca.clone();

			risultato = utenteInfoHome.findByFilter(parametriRicerca, getSession().getPrincipalId());
			paginationHandler = new PaginationHandler(parametriRicerca.getStartResultsFrom(),
					parametriRicerca.getMaxResults(), utenteInfoHome.findCountByFilter(parametriRicerca, getSession()
					.getPrincipalId()));
		} else {
			log.warn("Tentativo di accedere alla ricerca lavoratori non dalla sezione provincia o regione.");
			redirectHome();
		}
		ricercaEseguita = true;
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
		parametriRicerca = (RicercaUtenteDTO) restoreParams.get(PARAMETRI_RICERCA);
		search();
	};

	public void abilita() {
		int idLavoratore = Integer.parseInt(getRequestParameterEndsWith("id_lavoratore"));
		String codiceUtente = getRequestParameterEndsWith("codice_utente");

		try {
			// Controllo se la richiesta è già stata fatta
			PfPrincipalDTO principalDTO = pfPrincipalHome.findDTOById(idLavoratore);
			if (principalDTO.getRichiestaRegForteToken() == null
					|| !principalDTO.getRichiestaRegForteToken().equals(codiceUtente)) {
				addErrorMessage("regforte.coderrato");
				return;
			}
			if (principalDTO.getRegistrazioneForteToken() != null) {
				addErrorMessage("regforte.abilitgiarichiesta");
				return;
			}
			// Generazione token di controllo
			String registrazioneForteToken = Utils.randomString(24);
			// Aggiorno la tabella PfPrincipal con il token
			pfPrincipalHome.abilita(idLavoratore, registrazioneForteToken);

			// Aggiorno il DTO del backing bean per non doverlo richiedere al
			// back-end
			UtenteCompletoDTO utenteDaAbilitare = null;
			for (UtenteCompletoDTO utente : risultato) {
				if (utente.getId().intValue() == idLavoratore) {
					utente.setTokenAbilitazioneServizi(registrazioneForteToken);
					utenteDaAbilitare = utente;
					break;
				}
			}

			addInfoMessage("data.updated");

		} catch (EJBException e) {
			addErrorMessage("data.error_saving");
		}
	}

	// Crea e restituisce un file .csv contenente i risultati della ricerca.
	public StreamedContent downloadCSV() {
		// Crea il file
		parametriRicercaCSV.setStartResultsFrom(0);
		parametriRicercaCSV.setMaxResults(0);
		csv = utenteInfoHome.findByFilterCSV(parametriRicercaCSV, getSession().getPrincipalId());

		// Prepara lo stream del file e lo restituisce
		String csvFilename = "Risultato_Ricerca_Lavoratori.csv";
		byte[] buffer = csv.getBytes(Charset.forName("UTF-8"));
		InputStream stream = new ByteArrayInputStream(buffer);
		StreamedContent file = new DefaultStreamedContent(stream, "text/csv", csvFilename);
		return file;
	}

	public boolean isRegistrazioneForte(String tipoRegistrazione){
		if(tipoRegistrazione.equals(TipoRegistrazione.REGISTRAZIONE_FORTE.getLabel()))
			return true;
		else
			return false;

	}


}