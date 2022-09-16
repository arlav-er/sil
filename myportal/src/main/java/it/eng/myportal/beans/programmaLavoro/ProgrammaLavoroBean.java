package it.eng.myportal.beans.programmaLavoro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.BdAdesioneDTO;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VerificaRequisitiGaranziaOverResponseDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.ejb.WsProgrammaLavoroEJB;
import it.eng.myportal.entity.home.BdAdesioneHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.exception.VerificaRequisitiNotFoundException;
import it.eng.myportal.utils.CfUtils;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

@ManagedBean
@ViewScoped
public class ProgrammaLavoroBean extends AbstractBaseBean {

	@EJB
	private BdAdesioneHome bdAdesioneHome;
	@EJB
	private DeCpiHome deCpiHome;
	@EJB
	private UtenteInfoHome utenteInfoHome;
	@EJB
	private YgGaranziaOverHome ygGaranziaOverHome;
	@EJB
	private YgAdesioneHome ygAdesioneHome;
	@EJB
	private WsProgrammaLavoroEJB wsProgrammaLavoroEJB;
	
	//FORM PROGRAMMA ADESIONE
	@EJB
	private DeProvinciaHome deProvinciaHome;
	@EJB
	private DeCittadinanzaHome deCittadinanzaHome;
	@EJB
	private DeComuneHome deComuneHome;
	@EJB
	private DeTitoloSoggiornoHome deTitoloSoggiornoHome;
	@EJB
	private DeMotivoPermessoHome deMotivoPermessoHome;
	@EJB
	private DeGenereHome deGenereHome;

	protected static Log log = LogFactory.getLog(ProgrammaLavoroBean.class);

	private static final String MSG_YOUNG_CHECK_ADESIONE_GG_DATE = "05/09/2016";
	private static final String MSG_YOUNG_NOT_HAS_ADESIONE_GG = "Se hai meno di 30 anni per aderire a Umbriattiva Tirocini devi essere iscritto al programma Garanzia Giovani. "
			+ "Puoi aderire anche subito a Garanzia Giovani tramite il portale "
			+ "<a href=" + '"' + "http://www.garanziagiovani.gov.it" + '"' + ">http://www.garanziagiovani.gov.it</a>."
			+ "<br/>"
			+ "Le adesioni a Garanzia Giovani nazionale sono trasmesse a Lavoro per Te Umbria giornalmente: se ti iscrivi oggi tramite il sito "
			+ "<a href=" + '"' + "http://www.garanziagiovani.gov.it" + '"' + ">http://www.garanziagiovani.gov.it</a> "
			+ "potrai aderire a Umbriattiva Tirocini il giorno successivo.";
			
	private static final String OVER30 = "OVER30";
	private static final String UNDER30 = "UNDER30";

	private static final String MSG_AGE_OR_CF_NOT_FILLED = "Per poter procedere è necessario valorizzare la data di nascita e il codice fiscale nel tuo profilo";
	private static final String MSG_GENERIC_ERROR_DATE_FORMAT = "Errore generico nel formato della data";

	private static final String MSG_HAS_ALREADY_REIMPIEGO = "Non puoi aderire al programma perchè hai già aderito al programma Reimpiego in data %s";
	private static final String MSG_HAS_ALREADY_YG_GARANZIA_OVER = "Non puoi aderire al programma perchè hai già aderito al precedente programma Adulti in data  %s";

	private static final String MSG_ADESIONE_NON_DISPONIBILE = "Servizio temporaneamente non disponibile. Si invita a riprovare in un momento successivo.";

	private static final String MSG_ADESIONE_NO_REQUISITI = "Siamo spiacenti ma l'iscrizione al programma Umbriattiva Tirocini - Giovani/Adulti non può avvenire per mancanza di requisiti. Ti invitiamo a recarti presso il CpI per verificare la tua posizione.";
	private static final String MSG_ADESIONE_ERRORE_14 = "Il lavoratore non risulta avere una DID valida da almeno 12 mesi.";
	private static final String MSG_ADESIONE_ERRORE_15 = "Il lavoratore non risulta avere una DID valida da almeno 6 mesi.";
	private static final String MSG_ADESIONE_ERRORE_16 = "Il lavoratore non risulta avere una DID valida da almeno 4 mesi.";
	
	
	private UtenteCompletoDTO utenteCompletoDTO;
	
	//INFORMATIVA AND MAIN PAGE VIEW
	LinkedList<BdAdesioneDTO> bdAdesioneReiDTOs = new LinkedList<BdAdesioneDTO>();
	LinkedList<BdAdesioneDTO> bdAdesioneUmbattDTOs = new LinkedList<BdAdesioneDTO>();
	LinkedList<YgGaranziaOverDTO> ygGaranziaOverDTOs = new LinkedList<YgGaranziaOverDTO>();
	LinkedList<YgAdesioneDTO> ygAdesioneDTOWithDateAndStateConstraintDTOs = new LinkedList<YgAdesioneDTO>();
	
	private BdAdesioneDTO latestBdAdesioneDTOReiPerformed;
	private BdAdesioneDTO latestBdAdesioneDTOUmbAttPerformed;
	//Adulti
	private YgGaranziaOverDTO latestYgGaranziaOverDTOPerformed;
	//Giovani
	private YgAdesioneDTO latestYgAdesioneDTOPerformed;

	//FORM PROGRAMMA ADESIONE VIEW
	private DeCpiDTO newAdesioneCpi;
	private List<DeMotivoPermessoDTO> motiviPermessoList = new ArrayList<DeMotivoPermessoDTO>();
	private List<DeProvinciaDTO> provinciaRiferimentoList = new ArrayList<DeProvinciaDTO>();
	private List<DeTitoloSoggiornoDTO> titoloSoggiornoList = new ArrayList<DeTitoloSoggiornoDTO>();
	private List<DeCpiDTO> cpiRiferimentoList = new ArrayList<DeCpiDTO>();
	private VerificaRequisitiGaranziaOverResponseDTO esitoFromSilDTO;
	private boolean newAdesioneAmmortizzatori;

	//Variabili condivise
	private boolean showProgrammaAdesioneForm;
	
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		
		utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		try {			
			if (checkDtNascitaIsNotNull() && checkCodiceFiscaleIsNotNull()) {
				if(!initAndCheckUmbAttExists()){
					if(!initAndCheckReimpiegoExists()){
						if (checkIsOver30()) {
							//Adulti
							if(!initAndCheckAdultiExists()){
								initFormProgrammaAdesione();
							}
							else{
								String strDate = "";
								try {
									strDate = dateToString(latestYgGaranziaOverDTOPerformed.getDtAdesione());
								} catch (ParseException e) {
									log.error("E' stato impossibile recuperare la data adesione GG");
									addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Non puoi aderire al programma perchè hai già aderito al precedente programma Adulti"));			
									return;
								}
								addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", String.format(MSG_HAS_ALREADY_YG_GARANZIA_OVER, strDate)));			
							}
						} else {
							//Giovani
							if (initAndCheckYgAdesioneDTOExists()) {
								Date dataAdesioneGG = new SimpleDateFormat("dd/MM/yyyy").parse(MSG_YOUNG_CHECK_ADESIONE_GG_DATE);
								//Se la data adesione è >= del 05/09/2016 faccio ulteriori controlli
								if(latestYgAdesioneDTOPerformed.getDtAdesione().compareTo(dataAdesioneGG) >= 0){
									if(ConstantsSingleton.COD_STATO_ADESIONE_MIN_A.equals(latestYgAdesioneDTOPerformed.getDeStatoAdesioneMin().getId())){
										initFormProgrammaAdesione();
									}else{
										log.info("Non esiste nessuna adesione GG valida per parteciare al programma");
										addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", MSG_YOUNG_NOT_HAS_ADESIONE_GG));			
										return;									
									}
								}else{
									initFormProgrammaAdesione();
								}
							}
							else{
								log.info("Non esiste nessuna adesione GG valida per parteciare al programma");
								addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", MSG_YOUNG_NOT_HAS_ADESIONE_GG));			
								return;
							}
						}
					}
					else{
						String strDate = "";
						try {
							strDate = dateToString(latestBdAdesioneDTOReiPerformed.getDtAdesione());
						} catch (ParseException e) {
							log.error("E' stato impossibile recuperare la data adesione Reimpiego");
							addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Non puoi aderire al programma perchè hai già aderito al programma Reimpiego"));			
							return;
						}
						addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", String.format(MSG_HAS_ALREADY_REIMPIEGO, strDate)));			
					}
				}
			}
		}
		catch (ParseException e) {
			log.fatal("GRAVE: errore nel formato della data");
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_GENERIC_ERROR_DATE_FORMAT));
			return;
		}
		catch (Exception e) {
			log.error("ATTENZIONE: " + MSG_AGE_OR_CF_NOT_FILLED);
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_AGE_OR_CF_NOT_FILLED));
			return;
		}
	}

	// Bean's methods
	public void switchToViewForm() {
		showProgrammaAdesioneForm = true;
	}
	
	private String dateToString(Date date) throws ParseException{
		if(date!=null){
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
			String strDate = dateFormat.format(date);
			return strDate;			
		}
		throw new ParseException("Errore data nulla", 1);
	}
	
	//Reimpiego
	private boolean initAndCheckReimpiegoExists(){
		initReimpiego();
		return checkReimpiegoExists();
	}
	private void initReimpiego(){
		bdAdesioneReiDTOs.addAll(
				bdAdesioneHome.findDTOByCodFiscaleAndCodBandoProgramma(
						session.getPrincipalId(), ConstantsSingleton.DeBandoProgramma.COD_REI, utenteCompletoDTO.getCodiceFiscale()));
		if(!bdAdesioneReiDTOs.isEmpty()){
			latestBdAdesioneDTOReiPerformed = bdAdesioneReiDTOs.getLast();
		}
	}
	private boolean checkReimpiegoExists(){
		return !bdAdesioneReiDTOs.isEmpty();
	}
	
	//Umbria attiva
	private boolean initAndCheckUmbAttExists(){
		initUmbAtt();
		return checkUmbAttExists();
	}
	private void initUmbAtt(){
		bdAdesioneUmbattDTOs.addAll(
				bdAdesioneHome.findDTOByCodFiscaleAndCodBandoProgramma(
						session.getPrincipalId(), ConstantsSingleton.DeBandoProgramma.COD_UMBAT, utenteCompletoDTO.getCodiceFiscale()));
		if(!bdAdesioneUmbattDTOs.isEmpty()){
			latestBdAdesioneDTOUmbAttPerformed = bdAdesioneUmbattDTOs.getLast();
		}
	}
	public boolean checkUmbAttExists(){
		return !bdAdesioneUmbattDTOs.isEmpty();
	}
	
	//Vecchio pacchetto adulti
	private boolean initAndCheckAdultiExists(){
		initAdulti();
		return checkAdultiExists();
	}
	private void initAdulti(){
		ygGaranziaOverDTOs.addAll(
				ygGaranziaOverHome.findDTOValideByIdPfPrincipalAndCodFiscale(session.getPrincipalId(), utenteCompletoDTO.getCodiceFiscale()));
		if(!ygGaranziaOverDTOs.isEmpty()){
			latestYgGaranziaOverDTOPerformed = ygGaranziaOverDTOs.getLast();
		}
	}
	private boolean checkAdultiExists(){
		return !ygGaranziaOverDTOs.isEmpty();
	}
	
	//Vecchia adesione giovani
	private boolean initAndCheckYgAdesioneDTOExists(){
		initYgAdesioneDTO();
		return checkYgAdesioneDTOExists();
	}
	private void initYgAdesioneDTO(){
		latestYgAdesioneDTOPerformed = ygAdesioneHome.findLatestDTOByCodiceFiscaleInRegionePortale(utenteCompletoDTO.getCodiceFiscale());
	}
	private boolean checkYgAdesioneDTOExists(){
		return latestYgAdesioneDTOPerformed != null;
	}
	
	//Data nascita
	private boolean checkDtNascitaIsNotNull() throws Exception {
		if(utenteCompletoDTO.getDataNascita() != null){
			return true;
		}
		throw new Exception();
	}
	public boolean checkIsOver30() {
		return Utils.getAge(utenteCompletoDTO.getDataNascita()) >= 30;
	}
	
	//Codice fiscale
	private boolean checkCodiceFiscaleIsNotNull() throws Exception {
		if(utenteCompletoDTO.getCodiceFiscale() != null){
			return true;
		}
		throw new Exception();
	}
	
	public boolean isInformativaRendered(){
		try {
			if (checkDtNascitaIsNotNull() && checkCodiceFiscaleIsNotNull()) {
				if(checkIsOver30()){
					if(!initAndCheckUmbAttExists() &&
						!checkReimpiegoExists() &&
						!checkAdultiExists()){
						return true;
					}
				} else{
					if(!initAndCheckUmbAttExists() &&
						!checkReimpiegoExists() &&
						checkAdesioneGGAttiva()
						){
						return true;
					}
				}
			}
		} catch (Exception e) {
			log.error("ATTENZIONE: " + MSG_AGE_OR_CF_NOT_FILLED);
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_AGE_OR_CF_NOT_FILLED));
			return false;
		}
		return false;
	}
	
	private boolean checkAdesioneGGAttiva(){
		if(checkYgAdesioneDTOExists()){
			Date dataAdesioneGG;
			try {
				dataAdesioneGG = new SimpleDateFormat("dd/MM/yyyy").parse(MSG_YOUNG_CHECK_ADESIONE_GG_DATE);
				//Se la data adesione è >= del 05/09/2016 faccio ulteriori controlli
				if(latestYgAdesioneDTOPerformed.getDtAdesione().compareTo(dataAdesioneGG) >= 0){
					if(ConstantsSingleton.COD_STATO_ADESIONE_MIN_A.equals(latestYgAdesioneDTOPerformed.getDeStatoAdesioneMin().getId())){
						return true;
					}else{
						return false;								
					}
				}else{
					return true;
				}
			} catch (ParseException e) {
				log.fatal("GRAVE: Errore durante il recupero della data adesione GG");
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String getAgendaUrl() {
		return ConstantsSingleton.MYAGENDA_URL;
	}
	
	
	//FORM PROGRAMMA ADESIONE
	//Inizzializzazione form
	private void initFormProgrammaAdesione(){
		motiviPermessoList = deMotivoPermessoHome.findAllDTO();
		provinciaRiferimentoList = deProvinciaHome.findByRegione(ConstantsSingleton.COD_REGIONE.toString());
		titoloSoggiornoList = deTitoloSoggiornoHome.findAllDTO();

		inizializzaUtenteCampiMancanti();
		aggiornaCpiRiferimentoList();
	}
	public void aggiornaCpiRiferimentoList() {
		if (utenteCompletoDTO.getUtenteInfo().getProvinciaRiferimento() != null) {
			cpiRiferimentoList = deCpiHome
					.findDTOValidiByProvincia(utenteCompletoDTO.getUtenteInfo().getProvinciaRiferimento().getId());
		}
	}
	
	private void inizializzaUtenteCampiMancanti() {
		// Se il genere è mancante, provo ad inizializzarlo basandomi sul codice fiscale.
		if (utenteCompletoDTO.getUtenteInfo().getGenere() == null) {
			if (utenteCompletoDTO.getUtenteInfo().getCodiceFiscale() != null) {
				utenteCompletoDTO.getUtenteInfo().setGenere(deGenereHome
						.findDTOById(CfUtils.getSesso(utenteCompletoDTO.getUtenteInfo().getCodiceFiscale())));
			} else {
				utenteCompletoDTO.getUtenteInfo().setGenere(new DeGenereDTO());
			}
		}
		// Se i CAP domicilio e/o residenza mancano, posso inizializzarli basandomi sui rispettivi comuni.
		if (utenteCompletoDTO.getUtenteInfo().getCapDomicilio() == null
				&& utenteCompletoDTO.getUtenteInfo().getComuneDomicilio() != null) {
			utenteCompletoDTO.getUtenteInfo()
					.setCapDomicilio(utenteCompletoDTO.getUtenteInfo().getComuneDomicilio().getCap());
		}
		if (utenteCompletoDTO.getUtenteInfo().getCapResidenza() == null
				&& utenteCompletoDTO.getUtenteInfo().getComuneResidenza() != null) {
			utenteCompletoDTO.getUtenteInfo()
					.setCapResidenza(utenteCompletoDTO.getUtenteInfo().getComuneResidenza().getCap());
		}
	}
	
	//Controlli form
	public boolean isDomicilioFuoriRegione() {
		if (utenteCompletoDTO.getUtenteInfo().getComuneDomicilio() == null) {
			return false;
		} else {
			DeProvinciaDTO provinciaDomicilio = deProvinciaHome
					.findDTOById(utenteCompletoDTO.getUtenteInfo().getComuneDomicilio().getIdProvincia());
			if (provinciaDomicilio.getIdRegione().equalsIgnoreCase(ConstantsSingleton.COD_REGIONE.toString())) {
				return false;
			} else {
				return true;
			}
		}
	}
	public boolean isUtenteExtraUe() {
		if (utenteCompletoDTO.getCittadinanza().getId() == null)
			return false;
		if (utenteCompletoDTO.getCittadinanza().getFlgCee() != null
				&& "S".equalsIgnoreCase(utenteCompletoDTO.getCittadinanza().getFlgCee())) {
			return false;
		}
		return true;
	}
	public List<DeCittadinanzaDTO> completeCittadinanza(String par) {
		return deCittadinanzaHome.findBySuggestion(par);
	}
	public List<DeComuneDTO> completeComuneDomicilio(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}
	public List<DeComuneDTO> completeComuneNascita(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}
	public void copiaDatiDomicilioInResidenza() {
		utenteCompletoDTO.getUtenteInfo().setComuneResidenza(utenteCompletoDTO.getUtenteInfo().getComuneDomicilio());
		utenteCompletoDTO.getUtenteInfo().setCapResidenza(utenteCompletoDTO.getUtenteInfo().getCapDomicilio());

		// Per qualche ragione, l'indirizzo di domicilio può andare fino a 512 caratteri e quello di residenza solo 100.
		String indirizzoDomicilio = utenteCompletoDTO.getUtenteInfo().getIndirizzoDomicilio();
		if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 100)
			indirizzoDomicilio = indirizzoDomicilio.substring(0, 100);
		utenteCompletoDTO.getUtenteInfo().setIndirizzoResidenza(indirizzoDomicilio);
	}
	
	//Adesione Adulti
	public void creaBdAdesioneOver30() {
		creaBdAdesione(OVER30, null, null);
	}
	public void creaBdAdesioneUnder30(){
		creaBdAdesione(UNDER30, latestYgAdesioneDTOPerformed.getDtAdesione(), latestYgAdesioneDTOPerformed.getDeStatoAdesioneMin().getId());
	}
	public void creaBdAdesione(String dichiarazione, Date dtAdesioneGG,
			String codStatoAdesioneMin) {
		try {
			// Salvo eventuali campi del profilo utente inseriti/modificati tramite il form.
			utenteInfoHome.mergeDTOCompletoWithNewTransaction(utenteCompletoDTO, session.getPrincipalId());

			// Se il domicilio non è fuori regione, setto il CPI di riferimento a quello del comune di domicilio.
			if (!isDomicilioFuoriRegione()) {
				newAdesioneCpi = deCpiHome
						.findDTOByCodComune(utenteCompletoDTO.getUtenteInfo().getComuneDomicilio().getId());
			}
			String codProvinciaRiferimento = utenteCompletoDTO.getUtenteInfo().getProvinciaRiferimento().getId();
			esitoFromSilDTO = wsProgrammaLavoroEJB.callWsServiceAndPersistIfOk(
								utenteCompletoDTO, dichiarazione, codProvinciaRiferimento, newAdesioneCpi.getId(), dtAdesioneGG, codStatoAdesioneMin);
			showProgrammaAdesioneForm = false;
			showErrorMessageAfterServiceInvocation();
		} catch (VerificaRequisitiNotFoundException e) {
			log.error("Errore imprevisto: " + e.getMessage());
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_ADESIONE_NON_DISPONIBILE));
		} catch (Exception e) {
			log.error("Errore imprevisto: ", e);
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_ADESIONE_NON_DISPONIBILE));
		}
	}
	
	private void showErrorMessageAfterServiceInvocation(){
		try {
			checkDtNascitaIsNotNull();
			checkCodiceFiscaleIsNotNull();
			if(esitoFromSilDTO != null){
				
				if(0 == esitoFromSilDTO.getCodice()){
					//Ricarica le adesioni ad umbriattiva
					initUmbAtt();
				}else{
					// Se il WS ha restituito errore, salvo il messaggio di errore.
					// Se l'errore è una mancanza di requisti, salvo una versione più breve del messaggio.
					String strError = "";
					// Salvo il messaggio che spiega il tipo di errore (servizio non disponibile o mancanza di requisiti)
					if (1 == esitoFromSilDTO.getCodice() ||
							2 == esitoFromSilDTO.getCodice() ||
							98 == esitoFromSilDTO.getCodice()) {
						strError = MSG_ADESIONE_NON_DISPONIBILE;
					} else {
						strError = MSG_ADESIONE_NO_REQUISITI;
					}
					
					if (14 == esitoFromSilDTO.getCodice()) {
						strError += " - " + MSG_ADESIONE_ERRORE_14;
					} else if (15 == esitoFromSilDTO.getCodice()) {
						strError += " - " + MSG_ADESIONE_ERRORE_15;
					} else if (16 == esitoFromSilDTO.getCodice()) {
						strError += " - " + MSG_ADESIONE_ERRORE_16;
					} else {
						if(!"".equals(esitoFromSilDTO.getDescrizione())){							
							strError += " - " + esitoFromSilDTO.getDescrizione();
						}
					}
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", strError));
					return;
				}
			}
		} catch (Exception e) {
			log.error("ATTENZIONE: " + MSG_AGE_OR_CF_NOT_FILLED);
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", MSG_AGE_OR_CF_NOT_FILLED));
			return;
		}
	}

	public Date dataNewAdesione() {
		return new Date();
	}

	
	
	//Getter and setter informativa and main page view
	public BdAdesioneDTO getLatestBdAdesioneDTOUmbAttPerformed() {
		return latestBdAdesioneDTOUmbAttPerformed;
	}

	public void setLatestBdAdesioneDTOUmbAttPerformed(BdAdesioneDTO latestBdAdesioneDTOUmbAttPerformed) {
		this.latestBdAdesioneDTOUmbAttPerformed = latestBdAdesioneDTOUmbAttPerformed;
	}
	
	
	//Getter and setter Form programma adesione view
	public UtenteCompletoDTO getUtenteCompletoDTO() {
		return utenteCompletoDTO;
	}

	public void setUtenteCompletoDTO(UtenteCompletoDTO utenteCompletoDTO) {
		this.utenteCompletoDTO = utenteCompletoDTO;
	}
	
	public List<DeCpiDTO> getCpiRiferimentoList() {
		return cpiRiferimentoList;
	}
	
	public void setCpiRiferimentoList(List<DeCpiDTO> cpiRiferimentoList) {
		this.cpiRiferimentoList = cpiRiferimentoList;
	}
	
	public List<DeMotivoPermessoDTO> getMotiviPermessoList() {
		return motiviPermessoList;
	}
	
	public void setMotiviPermessoList(List<DeMotivoPermessoDTO> motiviPermessoList) {
		this.motiviPermessoList = motiviPermessoList;
	}
	
	public DeCpiDTO getNewAdesioneCpi() {
		return newAdesioneCpi;
	}
	
	public void setNewAdesioneCpi(DeCpiDTO newAdesioneCpi) {
		this.newAdesioneCpi = newAdesioneCpi;
	}
	
	public List<DeProvinciaDTO> getProvinciaRiferimentoList() {
		return provinciaRiferimentoList;
	}
	
	public void setProvinciaRiferimentoList(List<DeProvinciaDTO> provinciaRiferimentoList) {
		this.provinciaRiferimentoList = provinciaRiferimentoList;
	}
	
	public List<DeTitoloSoggiornoDTO> getTitoloSoggiornoList() {
		return titoloSoggiornoList;
	}
	
	public void setTitoloSoggiornoList(List<DeTitoloSoggiornoDTO> titoloSoggiornoList) {
		this.titoloSoggiornoList = titoloSoggiornoList;
	}
	
	public boolean isNewAdesioneAmmortizzatori() {
		return newAdesioneAmmortizzatori;
	}
	
	public void setNewAdesioneAmmortizzatori(boolean newAdesioneAmmortizzatori) {
		this.newAdesioneAmmortizzatori = newAdesioneAmmortizzatori;
	}
	
	//Getter and setter condivisi
	public boolean isShowProgrammaAdesioneForm() {
		return showProgrammaAdesioneForm;
	}
	
	public void setShowProgrammaAdesioneForm(boolean showProgrammaAdesioneForm) {
		this.showProgrammaAdesioneForm = showProgrammaAdesioneForm;
	}
	
}
