package it.eng.myportal.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.auth.FacebookAppSingleton;
import it.eng.myportal.auth.FederaAppSingleton;
import it.eng.myportal.auth.GoogleAppSingleton;
import it.eng.myportal.auth.IcarAppSingleton;
import it.eng.myportal.auth.SpidAppSingleton;
import it.eng.myportal.auth.TwitterAppSingleton;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Federa;

/**
 * BackingBean della pagina di completamento dati. Gestisce la registrazione di nuovi utenti quando questi si
 * autenticano trami IDP.
 * 
 * Pagina di 'atterraggio' per gli utenti che sono passati attraverso la tempesta di redirect e uno tra package
 * it.eng.myportal.auth
 * 
 * @see completamento_dati.xhtml
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CompletamentoDatiBean {
	protected static Log log = LogFactory.getLog(CompletamentoDatiBean.class);

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	private Boolean autenticazioneForte = false;
	private boolean editProvinciaRiferimento;

	private Map<String, ? extends Object> providerParameters;
	private RegisterUtenteDTO data = new RegisterUtenteDTO();
	private TipoProvider tipoProvider;

	@PostConstruct
	public void postConstruct() {
		String username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("lxt_username");

		recuperaProviderEtTipo(username);

		/*
		 * Set parametri comuni per tutti i provider. Se un provider ha delle configurazioni specifiche sarà il metodo
		 * popola* a sovrascriverle.
		 */
		setParametriUtenteComuni(username);

		// Sovrascrittura parametri gia` impostati, casi specifici

		if (tipoProvider == TipoProvider.FEDERA) {
			String trustLevel = Federa.TRUSTBASSO;
			popolaFedera(trustLevel);
		}

		if (tipoProvider == TipoProvider.ICAR) {
			popolaIcarDTO(username);
		}

		if (tipoProvider == TipoProvider.SPID) {
			popolaSpidDTO(username);
		}
	}

	private void recuperaProviderEtTipo(String username) {

		this.providerParameters = FacebookAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.FACEBOOK;
			return;
		}

		providerParameters = TwitterAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.TWITTER;
			return;
		}

		providerParameters = GoogleAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.GOOGLE;
			return;
		}

		providerParameters = FederaAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.FEDERA;
			return;
		}

		providerParameters = IcarAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.ICAR;
			return;
		}

		providerParameters = SpidAppSingleton.getInstance().get(username);
		if (providerParameters != null) {
			this.tipoProvider = TipoProvider.SPID;
			return;
		}

		throw new RuntimeException("Provider non fornito o supportato");

	}

	private void setParametriUtenteComuni(String username) {
		data.setUsername(username);
		data.setPassword("xxx");

		data.setNome((String) providerParameters.get(ConstantsSingleton.Auth.NOME));
		data.setCognome((String) providerParameters.get(ConstantsSingleton.Auth.COGNOME));

		String providedEmail = (String) providerParameters.get(ConstantsSingleton.Auth.EMAIL);
		data.setEmail(providedEmail);
		data.setEmailConfirm(providedEmail);
	}

	private void popolaFedera(String trustLevel) {
		// Da controllare meglio
		if (providerParameters.get(ConstantsSingleton.Federa.TRUSTLEVEL) != null)
			trustLevel = ((String) providerParameters.get(ConstantsSingleton.Federa.TRUSTLEVEL)).toLowerCase();

		if (Federa.TRUSTALTO.equals(trustLevel)) {
			data.setCodiceFiscale((String) providerParameters.get(ConstantsSingleton.Federa.CODICEFISCALE));
			autenticazioneForte = true;
		}
	}

	private void popolaIcarDTO(String username) {

		String trustLevel = ConstantsSingleton.Icar.TRUST_BASSO;

		if (providerParameters.get(ConstantsSingleton.Icar.AUTH_METOD) != null)
			trustLevel = ((String) providerParameters.get(ConstantsSingleton.Icar.AUTH_METOD));

		// Icar mantiene l'email in una diversa entry, si sovrascrive il comportamento generale
		String providedEmail = (String) providerParameters.get(ConstantsSingleton.Icar.EMAIL);
		data.setEmail(providedEmail);
		data.setEmailConfirm(providedEmail);

		if (providerParameters.get(ConstantsSingleton.Icar.CITTA_NASC) != null) {
			DeComuneDTO comuneDTO = deComuneHome
					.findDTOByDenominazione((String) providerParameters.get(ConstantsSingleton.Icar.CITTA_NASC));

			if (comuneDTO != null) {
				data.setComune(comuneDTO);
			}
		}
		if (providerParameters.get(ConstantsSingleton.Icar.CITTA_DOM) != null) {

			DeComuneDTO comuneDTO = deComuneHome
					.findDTOByDenominazione((String) providerParameters.get(ConstantsSingleton.Icar.CITTA_DOM));
			if (comuneDTO != null) {
				data.setDomicilio(comuneDTO);
			}
		}

		if (providerParameters.get(ConstantsSingleton.Icar.INDIRIZZO_DOM) != null) {
			data.setIndirizzo((String) providerParameters.get(ConstantsSingleton.Icar.INDIRIZZO_DOM));
		}

		if (providerParameters.get(ConstantsSingleton.Icar.TEL) != null) {
			data.setTelefono((String) providerParameters.get(ConstantsSingleton.Icar.TEL));
		}

		if (providerParameters.get(ConstantsSingleton.Icar.CELL) != null) {
			data.setCellulare((String) providerParameters.get(ConstantsSingleton.Icar.CELL));
		}

		if (providerParameters.get(ConstantsSingleton.Icar.DATA_NASC) != null) {

			String dtNascita = (String) providerParameters.get(ConstantsSingleton.Icar.DATA_NASC);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date dataNascita = null;
			try {
				dataNascita = df.parse(dtNascita);
			} catch (ParseException e) {
				log.warn("Data di nascita non valida: dataNascita='" + dtNascita + "'");
			}

			data.setDataNascita(dataNascita);
		}

		if (ConstantsSingleton.COD_REGIONE == UtilsBean.VDA) {
			data.setProvincia(deProvinciaHome.findDTOById(String.valueOf(UtilsBean.VDA)));
		} else if (ConstantsSingleton.COD_REGIONE == UtilsBean.TRENTO) {
			data.setProvincia(deProvinciaHome.findDTOById(String.valueOf(UtilsBean.TRENTO)));
		}

		// Per TRENTO si prende il codice fiscale sono se il livello di autenticazione è forte
		if (ConstantsSingleton.Icar.TRUST_ALTO.equals(trustLevel)) {
			data.setCodiceFiscale((String) providerParameters.get(ConstantsSingleton.Icar.CODICEFISCALE));
			autenticazioneForte = true;
		}

		// per la VDA si prende sempre il codice fiscale
		// e si pone sempre TRUST LEVEL ALTO
		if (ConstantsSingleton.COD_REGIONE == UtilsBean.VDA) {
			data.setCodiceFiscale((String) providerParameters.get(ConstantsSingleton.Icar.CODICEFISCALE));
			autenticazioneForte = true;

		}

		// Per l'Umbria il codice fiscale si prende sempre,
		// indipendetemente dal livello di sicurezza.
		if (ConstantsSingleton.COD_REGIONE == UtilsBean.UMBRIA) {
			data.setCodiceFiscale((String) providerParameters.get(ConstantsSingleton.Icar.CODICEFISCALE));
		}

	}

	private void popolaSpidDTO(String username) {

		// for (String key : providerParameters.keySet()) {
		// log.info(String.format("%s = %s ", key, providerParameters.get(key)));
		// }

		// Spid mantiene l'email con una diversa entry, si sovrascrive il comportamento generale
		String providedEmail = (String) providerParameters.get(ConstantsSingleton.Spid.email);
		data.setEmail(providedEmail);
		data.setEmailConfirm(providedEmail);
		// editEmail = providedEmail == null;

		if (providerParameters.get(ConstantsSingleton.Spid.placeofbirth) != null) {
			DeComuneDTO comuneDTO = deComuneHome
					.findDTOById(((String) providerParameters.get(ConstantsSingleton.Spid.placeofbirth)));

			if (comuneDTO != null) {
				data.setComune(comuneDTO);
			}
		}

		if (providerParameters.get(ConstantsSingleton.Spid.mobilephone) != null) {
			data.setCellulare((String) providerParameters.get(ConstantsSingleton.Spid.mobilephone));
		}
		if (providerParameters.get(ConstantsSingleton.Spid.dateofbirth) != null) {

			String dtNascita = (String) providerParameters.get(ConstantsSingleton.Spid.dateofbirth);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dataNascita = null;
			try {
				dataNascita = df.parse(dtNascita);
			} catch (ParseException e) {
				log.warn("Data di nascita non valida: dataNascita='" + dtNascita + "'");
			}

			data.setDataNascita(dataNascita);
		}

		// Spid mantiene il nome e cognome con una diversa chiave, si sovrascrive il comportamento generale
		data.setNome((String) providerParameters.get(ConstantsSingleton.Spid.name));
		data.setCognome((String) providerParameters.get(ConstantsSingleton.Spid.familyname));

		String intFiscalNumber = (String) providerParameters.get(ConstantsSingleton.Spid.fiscalnumber);

		if (intFiscalNumber != null && intFiscalNumber.startsWith("TINIT-")) {
			intFiscalNumber = intFiscalNumber.substring(6);
		} else {
			intFiscalNumber = "";
		}
		data.setCodiceFiscale(intFiscalNumber);
		autenticazioneForte = true;
	}

	public void changeCittadinanza(ValueChangeEvent event) {
		String val = (String) event.getNewValue();
		if (val == null)
			return;

		List<DeCittadinanzaDTO> lista = deCittadinanzaHome.findByDescription(val);
		if (lista != null && !lista.isEmpty()) {
			data.setCittadinanza(lista.get(0));
		}

	}

	/**
	 * Registra un utente al sistema
	 *
	 * @return 'home' se la registrazione è andata a buon fine
	 */
	public String registraUtente() {
		data.setAttivo(true);

		// imposta lo stato su ATTIVO

		data.setAutenticazioneForte(autenticazioneForte);
		utenteInfoHome.register(data, tipoProvider, providerParameters);

		FederaAppSingleton.getInstance().remove(data.getUsername());
		IcarAppSingleton.getInstance().remove(data.getUsername());
		TwitterAppSingleton.getInstance().remove(data.getUsername());
		FacebookAppSingleton.getInstance().remove(data.getUsername());
		GoogleAppSingleton.getInstance().remove(data.getUsername());
		SpidAppSingleton.getInstance().remove(data.getUsername());

		return "/faces/secure/utente/home.xhtml?faces-redirect=true";
	}

	/**
	 * Quando cambia il comune di domicilio è necessario verificare se
	 * 
	 * @param event
	 *            evento di cambio valore del comune di domicilio
	 */
	public void changeComuneDomicilio(AjaxBehaviorEvent event) {
		controlloCambioComuneDomicilio();
	}

	/**
	 * Controlla che il comune di domicilio sia in regione EMILIA-ROMAGNA. Se il controllo fallisce allora bisogna
	 * inserire manualmente la provincia di riferimento (in EMILIA-ROMAGNA)
	 * 
	 * ATTENZIONE: Il metodo e' duplicato in "UserInfoBean" e "RegistrationBean"
	 */
	private void controlloCambioComuneDomicilio() {
		try {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String comuneDomicilio = map.get("dati:comune_domicilio:inputText");
			String codComuneDomicilio = map.get("dati:comune_domicilio:inputHidden");

			if (deComuneHome.comuneDomicilioInRER(comuneDomicilio, codComuneDomicilio)) {
				DeProvinciaDTO provinciaDomicilio = deComuneHome.findProvinciaDTOByComune(comuneDomicilio,
						codComuneDomicilio);
				data.setProvincia(provinciaDomicilio);
				editProvinciaRiferimento = false;
			} else {
				data.setProvincia(new DeProvinciaDTO());
				editProvinciaRiferimento = true;
			}
		} catch (EJBException e) {
			data.setProvincia(new DeProvinciaDTO());
			editProvinciaRiferimento = true;
		}
	}

	public RegisterUtenteDTO getData() {
		return data;
	}

	public void setData(RegisterUtenteDTO data) {
		this.data = data;
	}

	public Boolean getAutenticazioneForte() {
		return autenticazioneForte;
	}

	public void setAutenticazioneForte(Boolean autenticazioneForte) {
		this.autenticazioneForte = autenticazioneForte;
	}

	/**
	 * Modificabile SSE nulla, in ogni caso
	 * 
	 * @return
	 */
	public boolean isEditEmail() {
		return data == null || data.getEmail() == null;
	}

	public boolean isEditProvinciaRiferimento() {
		return editProvinciaRiferimento;
	}

	public void setEditProvinciaRiferimento(boolean editProvinciaRiferimento) {
		this.editProvinciaRiferimento = editProvinciaRiferimento;
	}

	public String getTipoProvider() {
		return tipoProvider.name();
	}

}