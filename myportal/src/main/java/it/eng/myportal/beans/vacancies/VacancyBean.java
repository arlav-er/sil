package it.eng.myportal.beans.vacancies;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.ICheckCliclavoro;
import it.eng.myportal.dtos.DeAttivitaDTO;
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaRapportoDiLavoroDTO;
import it.eng.myportal.dtos.VaVacancyClDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaRapportoDiLavoroHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.local.IVaContattoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.URL;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.utils.DateUtils;

/**
 * Backing Bean associato alla pagina di Editing di una Vacancy.
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere un'azienda.</li>
 * <li>L'azienda deve essere proprietaria della vacancy che sta caricando.</li>
 * </ul>
 * 
 * @author D'Angelo E.
 * 
 */
@ManagedBean
@ViewScoped
public class VacancyBean extends AbstractEditableBean<VaDatiVacancyDTO> implements ICheckCliclavoro {

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(VacancyBean.class);

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	//@EJB(mappedName = "java:module/VaRapportoDiLavoroHome")
	//IVacancyEntityHome<VaRapportoDiLavoroDTO> vaRapportoDiLavoroHome;
	@EJB
	VaRapportoDiLavoroHome vaRapportoDiLavoroHome;

	@EJB
	IVaContattoHome vaContattoHome;

	private int vacancyId;
	private boolean isFromNewVa;
	private boolean flagInvioClPrecedente;
	private boolean publishSession;

	/* filtro per la selezione degli elementi dalla tabella de_attivita_min */
	private String filtroAttivita;
	/* filtro per la selezione degli elementi dalla tabella de_mansione_min */
	private List<String> filtroQualifica = new ArrayList<String>();

	private boolean mostraDatiCliclavoro;
	private boolean usaDecodificheSil;

	private ArrayList<String> messageArguments;

	@Override
	protected VaDatiVacancyDTO buildNewDataIstance() {
		data = new VaDatiVacancyDTO();

		return data;
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		setEditing(false);
		Map<String, String> map = getExternalContext().getRequestParameterMap();
		isFromNewVa = Boolean.TRUE.toString().equalsIgnoreCase(map.get("fromNew"));
		publishSession = Boolean.TRUE.toString().equals(map.get("publish_session"));

		log.debug("Costruito il Bean per la Vacancy!");

		if (data.getCodAteco() != null) {
			filtroAttivita = deAttivitaMinHome.getCodAttivitaPadreByCodAteco(data.getCodAteco());
		}
		if (data.getCodMansione() != null) {
			filtroQualifica = deMansioneMinHome.getCodMansionePadreByCodMansione(data.getCodMansione());
		}

		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(data.getOpzTipoDecodifiche())) {
			usaDecodificheSil = true;
		}
	}

	@Override
	protected VaDatiVacancyDTO retrieveData() {
		data = null;

		if (session.isAzienda()) {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			try {
				vacancyId = Integer.parseInt(map.get("id"));
			} catch (NumberFormatException e) {
				redirectGrave("generic.manipulation_error");
				return null;
			}

			refreshData();

			// /////////////////////////////////////////////////////////////////////////
			// *** Controllo di sicurezza ***
			// La Vacancy richiesta o una sua sezione devono appartenere
			// all'azienda loggata
			// /////////////////////////////////////////////////////////////////////////

			if (data.getIdPfPrincipalAzienda().intValue() != session.getConnectedAzienda().getId()) {
				redirectGrave("generic.manipulation_error");
				return null;
			}
			// /////////////////////////////////////////////////////////////////////////
		} else {
			addErrorMessage("azienda.is_not");
			redirectHome();
			return null;
		}

		return data;
	}

	public void pubblicaVacancy() {
		// Ottengo i valori da controllare
		VaRapportoDiLavoroDTO vaRapportoDiLavoroDTO = vaRapportoDiLavoroHome.findDTOByVacancyId(data.getId());
		List<IDecode> contrattoList = vaRapportoDiLavoroDTO.getTipologieContratto();
		VaContattoDTO vaContattoDTO = vaContattoHome.findDTOByVacancyId(data.getId());
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
		boolean isTipologiaContrattoVuota = contrattoList.isEmpty();
		boolean isContattiVuoti = (Utils.isStringEmpty(email) && Utils.isStringEmpty(fax)
				&& Utils.isStringEmpty(telefono));
		boolean isOggiMinoreDiDataPubblicazione = DateUtils.getToday()
				.compareTo(DateUtils.dateWithoutHourMinuteSecond(data.getDataPubblicazione())) > 0;
		if (isTipologiaContrattoVuota || isContattiVuoti || isOggiMinoreDiDataPubblicazione) {
			log.info("Pubblicazione non avvenuta per mancanza di campi non compilati");

			String strMessage = "<p>ATTENZIONE</p>" + "<p>E' necessario compilare i seguenti campi:</p>"
					+ "<ul style='list-style-type: none; margin-left:500px; text-align: left'>";
			if (isTipologiaContrattoVuota) {
				strMessage += "<li>- \"Tipologia di contratto\" nella sezione \"Rapporto di lavoro\"; </li>";
			}
			if (isContattiVuoti) {
				strMessage += "<li>- Almeno uno tra i campi \"Telefono\", \"Fax\" o \"E-mail\" nella sezione \"Contatto\";</li>";
			}
			if (isOggiMinoreDiDataPubblicazione) {
				strMessage += "<li>- La data pubblicazione non può essere passata;</li>";
			}
			strMessage += "</ul>";

			FacesMessage message = new FacesMessage();
			message.setSummary(strMessage);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} else {
			Integer idPfPrincipalMod = session.getPrincipalId();
			vaDatiVacancyHome.pubblica(data, idPfPrincipalMod);
			String msgText = "Aggiornamento avvenuto con successo";
			FacesMessage message = new FacesMessage();
			message.setSummary(msgText);
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			messages.addMessage(message);
			String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(
						base + "/faces/secure/azienda/vacancies/edit.xhtml?faces-redirect=true&publish_session=true&id="
								+ data.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String archiviaVacancy() {
		Integer idPfPrincipalMod = session.getPrincipalId();
		vaDatiVacancyHome.archiviaDTO(data, idPfPrincipalMod);
		deleteVacancySolr(data.getId());
		String msgText = "Aggiornamento avvenuto con successo";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
		messages.addMessage(message);
		return "visualizza?faces-redirect=true&id=" + data.getId();
	}

	/**
	 * cancella la vacancy da eliminare dall'indicizzazione di SOLR
	 * 
	 * @param idVaDatiVacancy
	 */
	public void deleteVacancySolr(Integer idVaDatiVacancy) {
		String baseDominio = ConstantsSingleton.getSolrUrl();
		String strParamUrl = URL.escapeSolr("/core0/update?stream.body=<delete><query>id_va_dati_vacancy:"
				+ idVaDatiVacancy + "</query></delete>&commit=true");
		log.info(strParamUrl + " invoked to delete vacancy");
		Utils.documentSOLR(baseDominio + strParamUrl);
	}

	public void prorogaVacancy() {
		vaDatiVacancyHome.proroga(data, session.getPrincipalId());
		addSuccessMessage();
	}

	public void addSuccessMessage() {
		String msgText = "Aggiornamento avvenuto con successo";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public boolean isModificaRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			if (!isArchiviata() && !editing) {
				return true;
			}
			return false;
		} else {
			if (editing) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean isPubblicaRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO) && isLavorazione() && !editing)
			return true;
		else
			return false;
	}

	public boolean isArchiviaRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)
				&& data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB) && !editing
				&& ConstantsSingleton.DeProvenienza.COD_MYPORTAL
						.equals(data.getDeProvenienzaVacancyDTO().getId()))
			return true;
		else
			return false;
	}

	public boolean isProrogaRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)
				&& data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB) && data.isInScadenza()
				&& data.canDoAnotherProroga() && ConstantsSingleton.DeProvenienza.COD_MYPORTAL
						.equals(data.getDeProvenienzaVacancyDTO().getId()))
			return true;
		else
			return false;
	}

	public boolean isScadenza() {
		if (data.getScadenzaPubblicazione() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			long diffInMillies, diff = 0;

			try {
				// Date scadezaDate = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(getDtScadenza()));
				Date nowDate = sdf.parse(sdf.format(new Date()));
				diffInMillies = data.getScadenzaPubblicazione().getTime() - nowDate.getTime();
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

	private void refreshData() {
		data = vaDatiVacancyHome.findDTOById(vacancyId);
		mostraDatiCliclavoro = data.isSincronizzataCliclavoro();
		flagInvioClPrecedente = data.getFlagInvioCl();
	}

	@Override
	protected void saveData() {
		data = homePersist(vaDatiVacancyHome, data);
		addInfoMessage("data.created");
		log.debug("Vacancy " + data.getId() + " salvata.");
	}

	@Override
	protected void updateData() {
		try {
			/* aggiorno la vacancy, escluso il flag di invio a cliclavoro */
			boolean flagInvioClNuovo = data.getFlagInvioCl();
			
			if(utils.isPAT()){
				data.setFlagInvioCl(flagInvioClPrecedente);
			}

			log.debug("==Aggiorno la Vacancy " + data.getDescrizione() + "==");
			log.debug("flag invio cl precedente: " + flagInvioClPrecedente);
			log.debug("flag invio cl nuovo: " + flagInvioClNuovo);

			data.setAttivitaPrincipale(data.getAttivitaPrincipale().toUpperCase());
			data = homeMerge(vaDatiVacancyHome, data);

			/*
			 * eseguo il metodo mergeCliclavoro solo se devo comunicare con cliclavoro
			 */
			boolean needsCliclavoro = vaDatiVacancyHome.needsMergeCliclavoro(vacancyId, flagInvioClPrecedente,
					flagInvioClNuovo);
			if (needsCliclavoro) {
				VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.mergeClicLavoro(session.getPrincipalId(),
						data.getId(), flagInvioClPrecedente, flagInvioClNuovo);
				homeMerge(vaDatiVacancyHome, vaDatiVacancyDTO);

				/* avvisi riguardanti la sincronizzazione con cliclavoro */
				if (!flagInvioClPrecedente == flagInvioClNuovo) {
					/* l'ambito di diffusione e' cambiato */
					boolean isPrecSincClicLavoro = flagInvioClPrecedente;
					boolean isNuovoSincClicLavoro = flagInvioClNuovo;
					if (isPrecSincClicLavoro && !isNuovoSincClicLavoro) {
						VaVacancyClDTO vaVacancyClDTO = vaVacancyClHome.findDTOById(data.getId());
						boolean isGiaComunicatoCliclavoro = vaVacancyClDTO != null && deStatoInvioClHome
								.giaComunicatoCliclavoro(vaVacancyClDTO.getDeStatoInvioCl().getId());
						if (isGiaComunicatoCliclavoro) {
							addWarnMessage("cliclavoro.va.warn.disattiva_sincronizzazione");
						} else {
							addWarnMessage("cliclavoro.va.warn.annulla_sincronizzazione");
						}
					}
					if (!isPrecSincClicLavoro && isNuovoSincClicLavoro) {
						addWarnMessage("cliclavoro.va.warn.nuova_sincronizzazione");
					}
				}
			}
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "data.retrieving");
		} finally {
			/* recupero i DTO aggiornati */
			refreshData();
		}
	}

	/**
	 * @see it.eng.myportal.beans.AbstractEditableBean#dontedit()
	 * 
	 *      La pressione di un pulsante implica sempre il post dei valori attualmente presenti nella form;<br>
	 *      per implementare la funzionalità di annullamento delle modifiche<br>
	 *      vengono ricaricati i dati.
	 * 
	 */
	@Override
	public void dontedit() {
		editing = false;
		log.debug("");
		try {
			data = vaDatiVacancyHome.findDTOById(vacancyId);
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
	}

	@Override
	public String getBackTo() {

		if (isFromNewVa && !isRedoBySess() || publishSession) {
			return getExternalContext().getRequestContextPath() + getHomeBySession();
		}

		return super.getBackTo();
	}

	public String getEsperienzaRichiestaDesc() {
		if (ConstantsSingleton.SI.equalsIgnoreCase(data.getEsperienzaRichiesta())) {
			return "Si";
		}
		if (ConstantsSingleton.NO.equalsIgnoreCase(data.getEsperienzaRichiesta())) {
			return "No";
		}
		return "Indifferente";
	}

	public String getFlagInvioClDesc() {
		if (data.getFlagInvioCl()) {
			return "Nazionale";
		} else {
			return "Regionale";
		}
	}

	public String getFiltroAttivita() {
		return filtroAttivita;
	}

	public void setFiltroAttivita(String filtroAttivita) {
		this.filtroAttivita = filtroAttivita;
	}

	public List<String> getFiltroQualifica() {
		return filtroQualifica;
	}

	public void setFiltroQualifica(List<String> filtroQualifica) {
		this.filtroQualifica = filtroQualifica;
	}

	public void setAttivitaFilter(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeAttivitaDTO> lista = deAttivitaHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista.size() > 0) {
			String codAteco = lista.get(0).getId();

			String codAttivitaPadre = deAttivitaMinHome.getCodAttivitaPadreByCodAteco(codAteco);

			this.filtroAttivita = codAttivitaPadre;
			this.data.setAttivitaMin(new DeAttivitaMinDTO());
		} else {
			// se non seleziono un elemento valido
			this.filtroAttivita = null;
			this.data.setStrAteco(null);
			this.data.setCodAteco(null);
			this.data.setAttivitaMin(new DeAttivitaMinDTO());
		}

		return;
	}

	public void setQualificaFilter(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeMansioneDTO> lista = deMansioneHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista.size() > 0) {
			String codMansione = lista.get(0).getId();

			List<String> listCodMansionePadre = deMansioneMinHome.getCodMansionePadreByCodMansione(codMansione);

			this.filtroQualifica = listCodMansionePadre;
			this.data.setMansioneMin(new DeMansioneMinDTO());
		} else {
			// se non seleziono un elemento valido
			this.filtroQualifica = new ArrayList<String>();
			this.data.setStrMansione(null);
			this.data.setCodMansione(null);
			this.data.setMansioneMin(new DeMansioneMinDTO());
		}

		return;
	}

	public void checkDatiCliclavoroListener(ComponentSystemEvent event) {
		checkDatiCliclavoro(event, this);
	}

	public void checkDatiCliclavoro(ComponentSystemEvent event, ICheckCliclavoro vacancy) {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);
		VaVacancyClDTO vaVacancyClDTO = vaVacancyClHome.findDTOById(vacancyId);

		String codStatoInvioCl = "";
		if (vaVacancyClDTO != null) {
			codStatoInvioCl = vaVacancyClDTO.getDeStatoInvioCl().getId();
		}
		boolean syncWithCLicLavoro = vaDatiVacancyDTO.isSincronizzataCliclavoro();
		boolean erroreChiusura = deStatoInvioClHome.isErroreChiusura(codStatoInvioCl);
		boolean inAttesaChiusura = deStatoInvioClHome.isInAttesaChiusura(codStatoInvioCl);

		/*
		 * se sono sincronizzato con cliclavoro non posso eliminare l'ultima tipologia di contratto
		 */
		if (syncWithCLicLavoro || erroreChiusura || inAttesaChiusura) {
			if (!vacancy.checkDatiCliclavoroSpecifico(event)) {
				/*
				 * faccio fallire la validazione e visualizzo il messaggio di wartning
				 */
				addWarnMessage(vacancy.getCheckMessageCliclavoro());
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.renderResponse();
			}
		}
	}

	@Override
	public boolean checkDatiCliclavoroSpecifico(ComponentSystemEvent event) {
		boolean result = true;
		messageArguments = new ArrayList<String>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		/*
		 * ritorna true se ho cliccato sul pulsante "Cancella". Fa schifo, lo so!
		 */
		boolean updateAction = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getParameterMap()
				.get("javax.faces.partial.execute")[0].contains("updateButton");

		UIComponent components = event.getComponent();

		UIInput codAtecoComponent = (UIInput) components.findComponent("cappello:settore:inputHidden");
		String codAteco = (String) codAtecoComponent.getLocalValue();

		UIInput codAttivitaMinComponent = (UIInput) components.findComponent("cappello:attivita_ateco:inputHidden");
		String codAttivitaMin = (String) codAttivitaMinComponent.getLocalValue();

		UIInput attivitaDescrizioneEstesaComponent = (UIInput) components
				.findComponent("cappello:descrizione:inputTextarea");
		String attivitaDescrizioneEstesa = (String) attivitaDescrizioneEstesaComponent.getLocalValue();

		UIInput codMansioneMinComponent = (UIInput) components
				.findComponent("cappello:qualifica_richiesta:inputHidden");
		String codMansioneMin = (String) codMansioneMinComponent.getLocalValue();

		if (updateAction) {
			if (codAteco == null || codAteco.isEmpty()) {
				result = false;
				messageArguments.add("Settore");
			}
			if (codAttivitaMin == null || codAttivitaMin.isEmpty()) {
				result = false;
				messageArguments.add("Attività ATECO");
			}
			if (attivitaDescrizioneEstesa == null || attivitaDescrizioneEstesa.isEmpty()) {
				result = false;
				messageArguments.add("Descrizione Attività");
			}
			if (codMansioneMin == null || codMansioneMin.isEmpty()) {
				result = false;
				messageArguments.add("Professione richiesta");
			}
		}

		return result;
	}

	@Override
	public String getCheckMessageCliclavoro() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String messageArgument : messageArguments) {
			stringBuilder.append(messageArgument + ", ");
		}
		String param = stringBuilder.toString();
		param = param.substring(0, param.lastIndexOf(","));

		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(errorsBean.getProperty("cliclavoro.va.warn.dati_testata"));

		Object[] paramObject = { param };
		String output = formatter.format(paramObject);

		return output;
	}

	public boolean isMostraDatiCliclavoro() {
		return mostraDatiCliclavoro;
	}

	public void setMostraDatiCliclavoro(boolean mostraDatiCliclavoro) {
		this.mostraDatiCliclavoro = mostraDatiCliclavoro;
	}

	public boolean usaDecodificheSil() {
		return usaDecodificheSil;
	}

	public String getVacanciesMaxScadenza() {
		if (utils.isPAT()) {
			return ConstantsSingleton.VACANCIES_PAT_MAX_SCADENZA;
		} else {
			return ConstantsSingleton.VACANCIES_MAX_SCADENZA;
		}
	}

	public boolean isArchiviata() {
		if (data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.ARC)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isLavorazione() {
		if (data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.LAV)) {
			return true;
		} else {
			return false;
		}
	}

	public String welcomepageEndpoint() {
		return ConstantsSingleton.getWelcomepageEndpoint() + "/vacancy/view/" + vacancyId;
	}

	public boolean isWelcomePageLinkRendered() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)
				&& data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB) && session.isAzienda() && isScadenza())
			return true;
		else
			return false;
	}
}
