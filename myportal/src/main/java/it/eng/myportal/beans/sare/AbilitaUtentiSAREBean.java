package it.eng.myportal.beans.sare;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.DeAutorizzazioneSareDTO;
import it.eng.myportal.dtos.DeTipoUtenteSareDTO;
import it.eng.myportal.dtos.RicercaAziendaDTO;
import it.eng.myportal.dtos.UtenteSAREDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAutorizzazioneSareHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoUtenteSareHome;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Pagina di abilitazione utenti al SARE e visualizzazione dati aziende.
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere una provincia.</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AbilitaUtentiSAREBean extends AbstractBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1296084893066188879L;

	private RicercaAziendaDTO params = new RicercaAziendaDTO();
	private RicercaAziendaDTO paramsCSV = new RicercaAziendaDTO();

	private List<UtenteSAREDTO> listaUtentiSARE;

	private Boolean ricercaUtentiEseguita = false;

	private List<SelectItem> statiModificaAutorizzazione;
	private List<SelectItem> tipiUtenteSare;

	Map<String, List<SelectItem>> cod2listTipiUtSare = new HashMap<String, List<SelectItem>>();
	Map<String, List<SelectItem>> cod2listAutSare = new HashMap<String, List<SelectItem>>();

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeTipoUtenteSareHome deTipoUtenteSareHome;

	@EJB
	DeAutorizzazioneSareHome deAutorizzazioneSareHome;

	private int dimPagina = 10;
	private int numPagina = 0;
	private List<SelectItem> statiAutorizzazioneSare;
	private List<SelectItem> statiAutorizzazioneSareRicerca;
	private static String TUTTI_GLI_STATI = "-1";
	private static String PARAMS = "params";
	private String csv;

	public RicercaAziendaDTO getParams() {
		return params;
	}

	public void setParams(RicercaAziendaDTO params) {
		this.params = params;
	}

	public List<UtenteSAREDTO> getListaUtentiSARE() {
		return listaUtentiSARE;
	}

	public void setListaUtentiSARE(List<UtenteSAREDTO> listaUtentiSARE) {
		this.listaUtentiSARE = listaUtentiSARE;
	}

	public Boolean getRicercaUtentiEseguita() {
		return ricercaUtentiEseguita;
	}

	public void setRicercaUtentiEseguita(Boolean ricercaUtentiEseguita) {
		this.ricercaUtentiEseguita = ricercaUtentiEseguita;
	}

	public List<SelectItem> getStatiModificaAutorizzazione() {
		return statiModificaAutorizzazione;
	}

	public void setStatiModificaAutorizzazione(List<SelectItem> statiModificaAutorizzazione) {
		this.statiModificaAutorizzazione = statiModificaAutorizzazione;
	}

	public List<SelectItem> getStatiAutorizzazioneSare() {
		return statiAutorizzazioneSare;
	}

	public List<SelectItem> getStatiAutorizzazioneSareRicerca() {
		return statiAutorizzazioneSareRicerca;
	}

	public void setStatiAutorizzazioneSare(List<SelectItem> statiAutorizzazioneSare) {
		this.statiAutorizzazioneSare = statiAutorizzazioneSare;
	}

	public void setStatiAutorizzazioneSareRicerca(List<SelectItem> statiAutorizzazioneSareRicerca) {
		this.statiAutorizzazioneSareRicerca = statiAutorizzazioneSareRicerca;
	}

	public int getDimPagina() {
		return dimPagina;
	}

	public void setDimPagina(int dimPagina) {
		this.dimPagina = dimPagina;
	}

	public int getNumPagina() {
		return numPagina;
	}

	public void setNumPagina(int numPagina) {
		this.numPagina = numPagina;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		if (!session.isProvincia()) {
			addErrorMessage("provincia.is_not");
			redirectHome();
			return;
		}

		if (params == null) {
			params = new RicercaAziendaDTO();
		}
		params.setIdProvincia(session.getConnectedProvincia().getProvincia().getId());

		statiAutorizzazioneSare = deAutorizzazioneSareHome.getListItems(false);

		statiAutorizzazioneSareRicerca = new ArrayList<SelectItem>();
		statiAutorizzazioneSareRicerca.add(new SelectItem(TUTTI_GLI_STATI, "Tutti gli stati"));
		statiAutorizzazioneSareRicerca.addAll(statiAutorizzazioneSare);

		tipiUtenteSare = new ArrayList<SelectItem>();
		tipiUtenteSare.addAll(deTipoUtenteSareHome.getListItems(true));

		log.debug("Costruito il Bean per la gestione degli utenti SARE.");
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
		RestoreParameters restore = super.generateRestoreParams();
		restore.put(PARAMS, params);
		return restore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.beans.AbstractBaseBean#ricreaStatoDaSessione(it.eng.myportal .beans.RestoreParameters)
	 * 
	 * metodo per recuperare i parametri. Viene chiamato da AbstractBaseBean.postConstruct()
	 */
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		params = (RicercaAziendaDTO) restoreParameters.get(PARAMS);
		cercaUtenti();
	};

	@Override
	public String getToken() {
		return super.getToken() + FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}

	private void emptyResults() {
		listaUtentiSARE = new ArrayList<UtenteSAREDTO>();
	}

	public void validateSearchForm(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		UIInput utente = (UIInput) components.findComponent("utente:inputText");
		String utenteText = (String) utente.getLocalValue();

		UIInput statoRichiesta = (UIInput) components.findComponent("stato_richiesta:combobox");
		String statoRichiestaText = (String) statoRichiesta.getLocalValue();

		UIInput conRettifica = (UIInput) components.findComponent("rettifica:checkbox");
		Boolean conRettificaBoolean = (Boolean) conRettifica.getLocalValue();

		if ((utenteText == null || utenteText.length() < 3)
				&& (TUTTI_GLI_STATI.equals(statoRichiestaText) && !conRettificaBoolean)) {
			FacesMessage msg = new FacesMessage(
					"Validation Failed",
					"Inserire almeno tre caratteri nel campo 'Utente' se non viene "
							+ "indicato uno 'Stato richiesta' o valorizzato il flag 'Utenti con richiesta di rettifica dati'.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(components.getClientId(), msg);
			fc.renderResponse();
		}
	}

	/**
	 * Cerca tutti gli utenti il cui username, ragione sociale o codice fiscale corrisponde alla stringa passata come
	 * input
	 */
	public void cercaUtenti() {
		emptyResults();
		setNumPagina(0);
		cerca(getNumPagina());
		ricercaUtentiEseguita = true;
	}

	public void cerca(int numPagina) {
		listaUtentiSARE = new ArrayList<UtenteSAREDTO>();
		csv = null;

		// salvataggio dei parametri per pulsante "Torna indietro"
		putParamsIntoSession();

		/*
		 * l'esportazione CSV riesegue la ricerca con MAXRESULTS e STARTRESULT pari a 0 e gli stessi parametri della
		 * ricerca normale
		 */
		paramsCSV = params.clone();

		try {
			if (TUTTI_GLI_STATI.equals(params.getStatoRichiesta())) {
				params.setStatoRichiesta(null);
			}
			listaUtentiSARE = aziendaInfoHome.cercaUtentiSARE(params, numPagina, dimPagina);
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public void aggiornaUtenti() {
		try {
			List<UtenteSAREDTO> daAggiornare = new ArrayList<UtenteSAREDTO>();
			// ottengo la lista dei soli utenti con stato AutorizzazioneSARE o
			// TipoUtenteSare da modificare
			for (UtenteSAREDTO utenteS : listaUtentiSARE) {
				boolean hasModifyAuthSare = utenteS.getModificaAutorizzazioneSARE() != null
						&& !TUTTI_GLI_STATI.equals(utenteS.getModificaAutorizzazioneSARE())
						&& !utenteS.getModificaAutorizzazioneSARE().equals(utenteS.getCodAutorizzazioneSare());
				boolean hasModifyCodTipoUtente = utenteS.getModificaCodTipoUtenteSare() != null
						&& !TUTTI_GLI_STATI.equals(utenteS.getModificaCodTipoUtenteSare())
						&& !utenteS.getModificaCodTipoUtenteSare().equals(utenteS.getCodTipoUtenteSare());
				if (hasModifyCodTipoUtente || hasModifyAuthSare) {
					daAggiornare.add(utenteS);
				}
			}
			// se ci sono effettivamente utenti da modificare
			if (!daAggiornare.isEmpty()) {
				aziendaInfoHome.aggiornaUtentiSARE(daAggiornare, getSession().getPrincipalId());
				addInfoMessage("data.updated");
			} else {
				addInfoMessage("data.no_update");
			}
			// ri-eseguo la ricerca - stessi param
			cercaUtenti();
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public void cercaNextPage() {
		cerca(++numPagina);
	}

	public void cercaPrevPage() {
		cerca(--numPagina);
	}

	public boolean getDisablePrevBtn() {
		return numPagina == 0;
	}

	public boolean getDisableNextBtn() {
		// se la lista è di lung max -> ci può essere un'altra pagina
		if (listaUtentiSARE != null) {
			return listaUtentiSARE.size() < dimPagina;
		}
		return false;
	}

	public List<SelectItem> getTipiUtenteSare() {
		return tipiUtenteSare;
	}

	public List<SelectItem> getTipiUtenteSare(String codTipoUtenteSare) {
		if (StringUtils.isBlank(codTipoUtenteSare)) {
			return tipiUtenteSare;
		}
		List<SelectItem> _tipiUtenteSare = cod2listTipiUtSare.get(codTipoUtenteSare);
		if (_tipiUtenteSare == null || _tipiUtenteSare.isEmpty()) {
			_tipiUtenteSare = new ArrayList<SelectItem>();
			for (SelectItem selectItem : tipiUtenteSare) {
				DeTipoUtenteSareDTO value = (DeTipoUtenteSareDTO) selectItem.getValue();
				if (value != null && codTipoUtenteSare.equals(value.getId())) {
					_tipiUtenteSare.add(new SelectItem(value, selectItem.getLabel() + " (Attuale)"));
				} else {
					_tipiUtenteSare.add(new SelectItem(value, selectItem.getLabel()));
				}
			}
			cod2listTipiUtSare.put(codTipoUtenteSare, _tipiUtenteSare);
		}
		return _tipiUtenteSare;
	}

	public void setTipiUtenteSare(List<SelectItem> tipiUtenteSare) {
		this.tipiUtenteSare = tipiUtenteSare;
	}

	public List<SelectItem> getStatiAutorizzazioneSare(String codAutorizzazioneSare) {
		if (StringUtils.isBlank(codAutorizzazioneSare)) {
			return statiAutorizzazioneSare;
		}
		List<SelectItem> _statiAutorizzazioneSare = cod2listAutSare.get(codAutorizzazioneSare);
		if (_statiAutorizzazioneSare == null || _statiAutorizzazioneSare.isEmpty()) {
			_statiAutorizzazioneSare = new ArrayList<SelectItem>();
			for (SelectItem selectItem : statiAutorizzazioneSare) {
				DeAutorizzazioneSareDTO value = (DeAutorizzazioneSareDTO) selectItem.getValue();
				if (value != null && codAutorizzazioneSare.equals(value.getId())) {
					_statiAutorizzazioneSare.add(new SelectItem(value, selectItem.getLabel() + " (Attuale)"));
				} else {
					_statiAutorizzazioneSare.add(new SelectItem(value, selectItem.getLabel()));
				}
			}
			cod2listTipiUtSare.put(codAutorizzazioneSare, _statiAutorizzazioneSare);
		}
		return _statiAutorizzazioneSare;
	}

	public void prepareCSV() {
		csv = aziendaInfoHome.cercaUtentiSARECSV(paramsCSV, 0, 0);
	}

	public StreamedContent downloadCSV() {
		String csvFilename = "Risultato_Ricerca_Utenti_SARE.csv";
		byte[] buffer = csv.getBytes(Charset.forName("UTF-8"));
		InputStream stream = new ByteArrayInputStream(buffer);
		StreamedContent file = new DefaultStreamedContent(stream, "text/csv", csvFilename);

		return file;
	}
}
