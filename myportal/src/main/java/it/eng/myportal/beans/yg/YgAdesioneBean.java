package it.eng.myportal.beans.yg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgSapHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.utils.yg.YgConstants;
import it.eng.myportal.utils.yg.YgDebugConstants;

@ManagedBean
@ViewScoped
public class YgAdesioneBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(YgAdesioneBean.class);

	private final String MSG_UMBRIA = "<br/>Ti confermiamo che hai scelto Regione Umbria in fase di adesione al programma Garanzia Giovani Nazionale. "
	+ "<br/>Se intendi partecipare al programma Umbriattiva Giovani, devi prenotare un appuntamento utilizzando il pulsante sottostante.";

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	YgAdesioneHome ygAdesioneHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	YgSapHome ygSapHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	YouthGuaranteeAdesioneEjb ygAdesioneEjb;

	@EJB
	DeGenereHome deGenereHome;

	private Date currentDate;
	private UtenteCompletoDTO utente;
	private YgAdesioneDTO ygAdesioneDTO;

	// variabili di stato
	private boolean extraUE;
	private boolean errore;
	private boolean currentAdesioneInviataDaMyPortal;
	private boolean currentAdesionePresaInCarico;

	private boolean erroreServizioNonDisponibile;
	private boolean erroreGenerico;
	private boolean erroreIdentificativoSapNonPresente;
	private boolean erroreAdesioneGiaPresenteLatoMinistero;
	private String descrizioneErroreSpecifico;

	private boolean codiceFiscaleInizialmentePresente;
	private boolean comuneDomicilioFuoriRegione;

	private DeCpiDTO cpiAdesione;

	private List<SelectItem> cpiSelectItems;

	private boolean esisteAdesione;

	private boolean isMsgUmbria = false;
	

	public boolean isMsgUmbria() {
		return isMsgUmbria;
	}

	public void setMsgUmbria(boolean isMsgUmbria) {
		this.isMsgUmbria = isMsgUmbria;
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public String readCodMonoStatoAdesione() {
		// FIXME rivedere 'sto schifo
		if (ygAdesioneDTO.getDeStatoAdesioneMin() == null)
			return "";

		return ygAdesioneDTO.getDeStatoAdesioneMin().getCodMonoAttiva();
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		errore = false;
		extraUE = false;
		erroreServizioNonDisponibile = false;
		erroreGenerico = false;
		erroreIdentificativoSapNonPresente = false;
		erroreAdesioneGiaPresenteLatoMinistero = false;
		codiceFiscaleInizialmentePresente = false;
		currentAdesionePresaInCarico = false;

		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione cittadino.");
			redirectHome();
			return;
		}

		// recupera dati da visualizzare e su cui lavorare

		currentDate = new Date();

		cpiSelectItems = new ArrayList<SelectItem>();

		utente = utenteInfoHome.findDTOCompletoByUsername(session.getUsername());

		if (utente != null) {
			if (utente.getCodiceFiscale() != null && !"".equalsIgnoreCase(utente.getCodiceFiscale())) {
				codiceFiscaleInizialmentePresente = true;
			}
			comuneDomicilioFuoriRegione = isComuneFuoriRegione(utente.getComuneDomicilio());

			if (utente.getProvinciaRiferimento() != null && utente.getProvinciaRiferimento().getId() != null) {
				cpiSelectItems = deCpiHome.getListItemsCpiByProvincia(utente.getProvinciaRiferimento().getId(), true);
			}

		}

		extraUE = isUtenteExtraUE();
		valorizzaCampiMancanti();

		// verifica esistenza adesione su db in locale o in remoto

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		// su db deve esistere il record, ma con flgAdesione = S e data adesione
		// non nulla
		esisteAdesione = ygAdesioneHome.esisteAdesioneSuDb(idPfPrincipalUtente);
		ygAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		currentAdesioneInviataDaMyPortal = isAdesioneInviataDaMyPortal();
		currentAdesionePresaInCarico = isAdesionePresaInCarico();

		if (esisteAdesione) {
			cpiAdesione = ygAdesioneDTO.getDeCpiAdesione();
		}

		if (cpiAdesione == null) {
			cpiAdesione = new DeCpiDTO();
		}
	}

	public boolean getShowMessaggioUmbria() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	public boolean getShowMessaggioRer() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER);
	}

	public String getDataAdesione() {
		String dataAdesioneString = "";
		if (ygAdesioneDTO != null) {
			Date dataAdesione = ygAdesioneDTO.getDtAdesione();
			if (dataAdesione != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				try {
					dataAdesioneString = dateFormat.format(dataAdesione);
				} catch (Exception e) {
					dataAdesioneString = "";
				}
			}
		}
		return dataAdesioneString;
	}

	private boolean isAdesioneInviataDaMyPortal() {
		if (ygAdesioneDTO != null && ygAdesioneDTO.getCodMonoProv() != null
				&& "I".equalsIgnoreCase(ygAdesioneDTO.getCodMonoProv())) {
			return true;
		}
		return false;
	}

	private boolean isAdesionePresaInCarico() {
		// DONA 11/12/2014 #3318
		// if (ygAdesioneDTO != null && ygAdesioneDTO.getDtPresaInCarico() !=
		// null) {
		if (ygAdesioneDTO != null && !("A").equalsIgnoreCase(ygAdesioneDTO.getDeStatoAdesioneMin().getId())) {
			return true;
		}
		return false;
	}

	private void valorizzaCampiMancanti() {

		// cap domicilio

		if (utente.getCapDomicilio() == null) {
			if (utente.getComuneDomicilio() != null) {
				utente.setCapDomicilio(utente.getComuneDomicilio().getCap());
			}
		}

		// genere

		if (utente.getCodiceFiscale() != null && utente.getGenere() == null && utente.getCodiceFiscale().length() == 16) {
			String giornoNascitaString = utente.getCodiceFiscale().substring(9, 11);
			try {
				int giornoNascita = Integer.parseInt(giornoNascitaString);
				DeGenereDTO deGenereDTO = new DeGenereDTO();
				if (giornoNascita > 40) {
					deGenereDTO.setId("F");
					utente.setGenere(deGenereDTO);
				} else {
					deGenereDTO.setId("M");
					utente.setGenere(deGenereDTO);
				}
			} catch (NumberFormatException nfe) {
				log.error("Errore durante la deduzione del genere dell'utente in base al codice fiscale: "
						+ nfe.getMessage());
			}
		}

		if (utente.getGenere() == null) {
			DeGenereDTO deGenereDTO = new DeGenereDTO();
			utente.setGenere(deGenereDTO);
		}

		// motivo permesso vuoto

		if (utente.getMotivoPermesso() == null) {
			DeMotivoPermessoDTO deMotivoPermessoDTO = new DeMotivoPermessoDTO();
			utente.setMotivoPermesso(deMotivoPermessoDTO);
		}

		// cap residenza

		if (utente.getCapResidenza() == null) {
			if (utente.getComuneResidenza() != null) {
				utente.setCapResidenza(utente.getComuneResidenza().getCap());
			}
		}

		// comune residenza

		if (utente.getComuneResidenza() == null) {
			DeComuneDTO deComuneDTO = new DeComuneDTO();
			utente.setComuneResidenza(deComuneDTO);
		}

	}

	public boolean isAdesioneAttiva() {
		if (!esisteAdesione)
			return false;
		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		return ygAdesioneHome.isAdesioneSuDbAttiva(idPfPrincipalUtente);
	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public boolean isUtenteExtraUE() {
		if (utente.getCittadinanza() != null && utente.getCittadinanza().getFlgCee() != null
				&& "S".equalsIgnoreCase(utente.getCittadinanza().getFlgCee())) {
			return false;
		}
		return true;
	}

	public String getTelefonoUtente() {

		String telefonoUtente = utente.getTelCasa();

		if (telefonoUtente == null) {

			telefonoUtente = utente.getUtenteDTO().getPfPrincipalDTO().getTelefonoUtente();

		}

		return telefonoUtente;

	}

	public String inviaAdesione() {
		if (!controlliInvioAdesione()) {
			return "";
		}

		/* pulizia campi permesso soggiorno se cittadinanza UE */
		if (!extraUE) {
			utente.setMotivoPermesso(null);
			//utente.setNumeroDocumento(null);
			//utente.setDataScadenzaDocumento(null);
			utente.setDocumentoSoggiorno(null);
		}

		try {
			// salva i dati dell'utente
			ygAdesioneHome.salvaInformazioniUtente(utente);
		} catch (Exception e) {
			log.error(
					"Errore generico avvenuto durante l'invio dell'adesione per l'utente: " + utente.getCodiceFiscale(),
					e);
			esisteAdesione = false;
			errore = true;

			FacesMessage message = new FacesMessage();
			message.setSummary("Errore generico");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);

			erroreGenerico = true;
			return "";
		}

		// invia adesione
		return invia();
	}

	public String invia() {

		// adesione
		try {
			// chiamata ws checkutente per verificare se è già stato preso in
			// carico
			// dopo aver compilato la form di invio adesione
			if (!esisteAdesione) {
				try {
					esisteAdesione = ygAdesioneHome.checkUtenteYg(currentDate, utente);
				} catch (MyPortalException mye) {
					esisteAdesione = false;
					log.error("Errore durante l'adesione per l'utente: " + utente.getCodiceFiscale() + " - errore: "
							+ mye.getStrMessaggio());
					if (mye.getCodErrore() != null
							&& YgConstants.COD_ERRORE_GENERICO.equalsIgnoreCase(mye.getCodErrore())) {
						erroreGenerico = true;
					}

					if (mye.getCodErrore() != null
							&& YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE.equalsIgnoreCase(mye.getCodErrore())) {
						erroreServizioNonDisponibile = true;
					}

					// su indicazione di Stefania il 07/04/2014
					// si fa in modo che se vi è un errore di servizio non
					// disponibile durante la chiamata al WS
					// si ipotizza che l'adesione non sia stata inviata
					if (erroreServizioNonDisponibile) {
						erroreServizioNonDisponibile = false;
					} else {
						FacesMessage message = new FacesMessage();
						message.setSummary(mye.getStrMessaggio());
						message.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, message);
						errore = true;
					}
					return "";
				}
			} else {
				log.warn("Adesione presente non è chiamata la checkUtenteYG: non dovrebbe succedere!!");
			}

			esisteAdesione = ygAdesioneHome.invioAdesioneYgSAP(currentDate, utente, cpiAdesione);
			errore = false;

			if (YgDebugConstants.IS_DEBUG) {
				if (YgDebugConstants.DEBUG_ADESIONE_ESITO_INVIO_GIA_PRESENTE) {
					esisteAdesione = false;
					YgAdesioneDTO currentYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(utente
							.getUtenteDTO().getPfPrincipalDTO().getId());
					ygAdesioneHome.salvaErroreStrMessAdesione(currentYgAdesioneDTO.getId(),
							YgDebugConstants.DEBUG_ADESIONE_ESITO_INVIO_GIA_PRESENTE_MSG);
				}
			}
			if (!esisteAdesione) {
				Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
				
				String[] retArr = ygAdesioneHome
				.getErroreAdesioneGiaPresenteLatoMinistero(idPfPrincipalUtente);


				String descrizioneErrore =retArr[0];
				this.isMsgUmbria = new Boolean(retArr[1]);

				if (descrizioneErrore != null) {
					errore = true;
					erroreAdesioneGiaPresenteLatoMinistero = true;
					if (this.isMsgUmbria) {
						descrizioneErroreSpecifico = descrizioneErrore + MSG_UMBRIA;
					}else {
						descrizioneErroreSpecifico = descrizioneErrore;
					}
					return "";
				}
			}
		} catch (MyPortalException mye) {
			log.error("Errore durante l'invio dell'adesione per l'utente: " + utente.getCodiceFiscale() + " - errore: "
					+ mye.getStrMessaggio());
			esisteAdesione = false;
			errore = true;

			if (mye.getCodErrore() != null && YgConstants.COD_ERRORE_GENERICO.equalsIgnoreCase(mye.getCodErrore())) {
				erroreGenerico = true;
			}
			if (mye.getCodErrore() != null
					&& YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE.equalsIgnoreCase(mye.getCodErrore())) {
				erroreServizioNonDisponibile = true;
			}
			if (mye.getCodErrore() != null
					&& YgConstants.COD_ERRORE_IDENTIFICATIVO_SAP_NON_PRESENTE.equalsIgnoreCase(mye.getCodErrore())) {
				erroreIdentificativoSapNonPresente = true;
			}
			if (mye.getCodErrore() != null
					&& YgConstants.COD_ERRORE_VALIDAZIONE_XSD.equalsIgnoreCase(mye.getCodErrore())) {
				FacesMessage message = new FacesMessage();
				message.setSummary(mye.getStrMessaggio());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
			if (erroreGenerico || erroreServizioNonDisponibile) {
				FacesMessage message = new FacesMessage();
				message.setSummary(mye.getStrMessaggio());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
			}

			return "";
		} catch (Exception e) {
			log.error(
					"Errore generico avvenuto durante l'invio dell'adesione per l'utente: " + utente.getCodiceFiscale(),
					e);
			esisteAdesione = false;
			errore = true;

			FacesMessage message = new FacesMessage();
			message.setSummary("Errore generico");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);

			erroreGenerico = true;
			return "";
		}

		if (esisteAdesione) {
			return "/secure/utente/yg/esito_adesione_ok.xhtml?faces-redirect=true";
		} else {
			return "/secure/utente/yg/esito_adesione_ko.xhtml?faces-redirect=true";
		}

	}

	private boolean controlliInvioAdesione() {
		/* controlla se è già stata inviata una adesione per lo stesso utente */
		if (ygAdesioneHome.esisteAdesioneInviataByCodiceFiscaleAndCodMonoProv(utente.getCodiceFiscale(), "I")) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Attenzione: l'adesione risulta essere già inviata");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return false;
		}

		/* controllo comune di domicilio e residenza validi */
		Date oggi = Calendar.getInstance().getTime();
		if (utente.getComuneDomicilio() != null) {
			if (oggi.before(utente.getComuneDomicilio().getDtInizioVal())
					|| oggi.after(utente.getComuneDomicilio().getDtFineVal())) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Attenzione: il comune di domicilio non è valido");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return false;
			}
		}
		if (utente.getComuneResidenza() != null) {
			if (oggi.before(utente.getComuneResidenza().getDtInizioVal())
					|| oggi.after(utente.getComuneResidenza().getDtFineVal())) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Attenzione: il comune di residenza non è valido");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return false;
			}
		}

		/* controlla flag invio sms obbligatorio se indicato numero cellulare */
		Boolean flgConsensoSms = utente.getFlgConsensoSms();
		String numeroCellulare = utente.getCellulare();
		if (numeroCellulare != null && !"".equalsIgnoreCase(numeroCellulare)) {
			if (flgConsensoSms == null) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Nel caso si sia inserito un numero di cellulare è obbligatorio specificare se si acconsente a ricevere messaggi SMS di notifica");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return false;
			}
		}

		/* controlla età */
		int age = Utils.getAge(utente.getDataNascita());
		if (age < YgConstants.AGE_MIN || age > YgConstants.AGE_MAX) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Siamo spiacenti, ma per aderire al progetto di Garanzia Giovani devi avere un'età compresa tra i 15 e i 29 anni.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return false;
		}

		/* controlla codice fiscale */
		String erroriCodiceFiscale = Utils.controllocompletocodicefiscale(utente.getCodiceFiscale(), utente.getNome(),
				utente.getCognome(), utente.getGenere().getId(), utente.getComuneNascita(), utente.getDataNascita());
		if (erroriCodiceFiscale != null && !"".equalsIgnoreCase(erroriCodiceFiscale)) {

			FacesMessage message = new FacesMessage();
			message.setSummary(erroriCodiceFiscale);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return false;

		}

		/* controlla presenza campi obbligatori */
		if ((comuneDomicilioFuoriRegione && (utente.getProvinciaRiferimento() == null
				|| utente.getProvinciaRiferimento().getId() == null || cpiAdesione == null
				|| cpiAdesione.getId() == null || "".equalsIgnoreCase(cpiAdesione.getId())))
				|| (extraUE && (utente.getMotivoPermesso() == null || utente.getMotivoPermesso().getId() == null))
				|| utente.getGenere() == null
				|| utente.getGenere().getId() == null
				|| utente.getCapDomicilio() == null
				|| "".equalsIgnoreCase(utente.getCapDomicilio())) {

			FacesMessage message = new FacesMessage();
			message.setSummary("Compilare i campi obbligatori");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return false;

		}

		return true;
	}

	public boolean isEsisteAdesione() {
		return esisteAdesione;
	}

	public void setEsisteAdesione(boolean esisteAdesione) {
		this.esisteAdesione = esisteAdesione;
	}

	public boolean isErrore() {
		return errore;
	}

	public void setErrore(boolean errore) {
		this.errore = errore;
	}

	public boolean isExtraUE() {
		return extraUE;
	}

	public void setExtraUE(boolean extraUE) {
		this.extraUE = extraUE;
	}

	public boolean isCurrentAdesioneInviataDaMyPortal() {
		return currentAdesioneInviataDaMyPortal;
	}

	public void setCurrentAdesioneInviataDaMyPortal(boolean currentAdesioneInviataDaMyPortal) {
		this.currentAdesioneInviataDaMyPortal = currentAdesioneInviataDaMyPortal;
	}

	public boolean isErroreServizioNonDisponibile() {
		return erroreServizioNonDisponibile;
	}

	public void setErroreServizioNonDisponibile(boolean erroreServizioNonDisponibile) {
		this.erroreServizioNonDisponibile = erroreServizioNonDisponibile;
	}

	public boolean isErroreGenerico() {
		return erroreGenerico;
	}

	public void setErroreGenerico(boolean erroreGenerico) {
		this.erroreGenerico = erroreGenerico;
	}

	public boolean isErroreIdentificativoSapNonPresente() {
		return erroreIdentificativoSapNonPresente;
	}

	public void setErroreIdentificativoSapNonPresente(boolean erroreIdentificativoSapNonPresente) {
		this.erroreIdentificativoSapNonPresente = erroreIdentificativoSapNonPresente;
	}

	public void comuneResidenzaChanged() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String codComuneResidenza = map.get("dati_personali_form:comune_residenza:inputHidden");
		DeComuneDTO deComuneDTO = deComuneHome.findDTOById(codComuneResidenza);

		if (deComuneDTO != null) {
			utente.setComuneResidenza(deComuneDTO);
			utente.setCapResidenza(deComuneDTO.getCap());
		}
	}

	public void onChangeGenere() {

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String codGenere = map.get("dati_personali_form:sesso:combobox");

		DeGenereDTO deGenereDTO = deGenereHome.findDTOById(codGenere);

		if (deGenereDTO != null) {
			utente.setGenere(deGenereDTO);
		}

	}

	public void onChangeComuneDomicilio() {

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String codComuneDomicilio = map.get("dati_personali_form:comune_domicilio:inputHidden");

		DeComuneDTO deComuneDTO = deComuneHome.findDTOById(codComuneDomicilio);

		if (deComuneDTO != null) {
			utente.setComuneDomicilio(deComuneDTO);
			utente.setCapDomicilio(deComuneDTO.getCap());
		}

		comuneDomicilioFuoriRegione = isComuneFuoriRegione(deComuneDTO);

	}

	public void onChangeCittadinanza(ValueChangeEvent event) {

		String val = (String) event.getNewValue();
		if (val == null) {
			return;
		}

		List<DeCittadinanzaDTO> lista = deCittadinanzaHome.findByDescription(val);
		if (lista != null && !lista.isEmpty()) {
			utente.setCittadinanza(lista.get(0));
		}

		extraUE = isUtenteExtraUE();

	}

	public void onChangeCpiAdesione() {

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String idCpiAdesione = map.get("dati_personali_form:cpi_adesione:combobox");

		DeCpiDTO deCpiDTO = deCpiHome.findDTOById(idCpiAdesione);

		if (deCpiDTO != null) {
			cpiAdesione = deCpiDTO;
		} else {
			cpiAdesione = new DeCpiDTO();
		}

	}

	public void onChangeProvinciaAdesione() {

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String codProvinciaAdesione = map.get("dati_personali_form:provincia_riferimento:combobox");

		DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(codProvinciaAdesione);

		if (deProvinciaDTO != null) {
			utente.setProvinciaRiferimento(deProvinciaDTO);
			cpiSelectItems = deCpiHome.getListItemsCpiByProvincia(deProvinciaDTO.getId(), true);
		} else {
			cpiSelectItems = new ArrayList<SelectItem>();
		}

		cpiAdesione = new DeCpiDTO();

	}

	private boolean isComuneFuoriRegione(DeComuneDTO deComuneDTO) {

		boolean isComuneFuoriRegione = false;

		if (deComuneDTO != null) {

			DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(deComuneDTO.getIdProvincia());

			if (deComuneDTO != null && deProvinciaDTO.getIdRegione() != null
					&& !deProvinciaDTO.getIdRegione().equalsIgnoreCase(ConstantsSingleton.COD_REGIONE.toString())) {

				isComuneFuoriRegione = true;

			}

		}

		return isComuneFuoriRegione;

	}

	public void copiaDatiDomicilioInResidenza() {

		if (utente.getComuneDomicilio() != null) {
			DeComuneDTO deComuneDTO = deComuneHome.findDTOById(utente.getComuneDomicilio().getId());
			utente.setComuneResidenza(deComuneDTO);
		}

		utente.setCapResidenza(utente.getCapDomicilio());

		String indirizzoResidenza = utente.getIndirizzoDomicilio();
		if (indirizzoResidenza != null && indirizzoResidenza.length() > 100) {
			indirizzoResidenza = indirizzoResidenza.substring(0, 100);
		}
		utente.setIndirizzoResidenza(indirizzoResidenza);

	}

	public void checkViewPage() {

		boolean checkView = true;

		// modifica del 08/05/2014
		// non è più necessario essere abilitati
		// ai servizi amministrativi per
		// poter accedere alla parte di YG

		// boolean checkView = getSession().isAbilitato("_portlet_yg",
		// TipoAbilitazione.VISIBILE);

		if (!checkView) {
			getExternalContext().setResponseStatus(404);
			getFacesContext().responseComplete();
			redirectHome();
			return;
		}

		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione cittadino.");
			redirectHome();
			return;
		}

	}

	public void annullaAdesione() {
		try {
			Integer deleteId = ygAdesioneDTO.getId();
			log.warn("Annullamento manuale richiesto. Id Adesione: " + deleteId);

			ygAdesioneEjb.annullaAdesioneYG(deleteId, session.getConnectedUtente().getId(), "D");
			addInfoMessage("yg.canceled");
			// addErrorMessage("data.error_deleting", e);
		} catch (EJBException e) {
			gestisciErrore(e, "yg.error_canceling");
		} catch (NullPointerException e) {
			log.error("Impossibile trovare il parametro ID dell'adesione da annullare");
			addErrorMessage("yg.error_canceling");
		}
	}

	public boolean isCodiceFiscaleReadOnly() {

		if (getSession().getConnectedUtente().getAbilitatoServizi()) {
			return true;
		}

		if (utente.getCodiceFiscale() == null || "".equalsIgnoreCase(utente.getCodiceFiscale())) {
			return false;
		}

		if (utente.getCodiceFiscale() != null && !"".equalsIgnoreCase(utente.getCodiceFiscale())
				&& !codiceFiscaleInizialmentePresente) {
			return false;
		}

		return true;

	}

	public boolean isComuneDomicilioFuoriRegione() {
		return comuneDomicilioFuoriRegione;
	}

	public void setComuneDomicilioFuoriRegione(boolean comuneDomicilioFuoriRegione) {
		this.comuneDomicilioFuoriRegione = comuneDomicilioFuoriRegione;
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

	public boolean isErroreAdesioneGiaPresenteLatoMinistero() {
		return erroreAdesioneGiaPresenteLatoMinistero;
	}

	public void setErroreAdesioneGiaPresenteLatoMinistero(boolean erroreAdesioneGiaPresenteLatoMinistero) {
		this.erroreAdesioneGiaPresenteLatoMinistero = erroreAdesioneGiaPresenteLatoMinistero;
	}

	public String getDescrizioneErroreSpecifico() {
		return descrizioneErroreSpecifico;
	}

	public void setDescrizioneErroreSpecifico(String descrizioneErroreSpecifico) {
		this.descrizioneErroreSpecifico = descrizioneErroreSpecifico;
	}

	public boolean isCurrentAdesionePresaInCarico() {
		return currentAdesionePresaInCarico;
	}

	public void setCurrentAdesionePresaInCarico(boolean currentAdesionePresaInCarico) {
		this.currentAdesionePresaInCarico = currentAdesionePresaInCarico;
	}

}
