package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterPatronatoDTO;
import it.eng.myportal.dtos.RegisterRegioneDTO;
import it.eng.myportal.entity.home.PatronatoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

@ManagedBean
@ViewScoped
public class RegistraUtentePatronatoBean {

	private static final String INDEX_OUTCOME = "index";
	@EJB
	private PatronatoHome patronatoHome;
	
	@EJB
	private DeProvinciaHome deProvinciaHome;

	private RegisterPatronatoDTO utentePatronatoDto;
	private DeProvinciaDTO altraProvincia;
	private UIComponent altraProvinciaComponent;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	public RegistraUtentePatronatoBean() {
		super();
	}
		
	@PostConstruct
	protected void postConstruct() {
		utentePatronatoDto = new RegisterPatronatoDTO();
		altraProvincia = new DeProvinciaDTO();
	}

	public String registraUtente() {
		try {
			utentePatronatoDto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			patronatoHome.register(utentePatronatoDto);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)utentePatronatoDto, true);  
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			utentePatronatoDto = new RegisterPatronatoDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return "";
		}
		return INDEX_OUTCOME;
	}
	
	/**
	 * Aggiunge una provincia di competenza al patronato.
	 * Non si possono aggiungere doppioni nè la provincia principale del patronato.
	 */
	public void addAltraProvincia() {
		altraProvincia = deProvinciaHome.findDTOById(altraProvincia.getId());
		if (altraProvincia == null)
			return;
		
		if (utentePatronatoDto.getAltreProvince().contains(altraProvincia)) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Impossibile aggiungere due volte la stessa provincia.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage
				(altraProvinciaComponent.getClientId() + ":inputText", message);
			return;
		}
		
		if (altraProvincia.equals(utentePatronatoDto.getProvincia())) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Impossibile aggiungere in \"Altre Province\" la provincia principale.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage
				(altraProvinciaComponent.getClientId() + ":inputText", message);
			return;
		}
		
		// Se i due controlli vanno a buon fine, aggiungo la provincia alla lista.
		utentePatronatoDto.getAltreProvince().add(altraProvincia);
		altraProvincia = new DeProvinciaDTO();
	}
	
	public RegisterPatronatoDTO getUtentePatronatoDto() {
		return utentePatronatoDto;
	}

	public void setUtentePatronatoDto(RegisterPatronatoDTO utentePatronatoDto) {
		this.utentePatronatoDto = utentePatronatoDto;
	}
	
	public DeProvinciaDTO getAltraProvincia() {
		return altraProvincia;
	}
	
	public void setAltraProvincia(DeProvinciaDTO altraProvincia) {
		this.altraProvincia = altraProvincia;
	}

	public UIComponent getAltraProvinciaComponent() {
		return altraProvinciaComponent;
	}

	public void setAltraProvinciaComponent(UIComponent altraProvinciaComponent) {
		this.altraProvinciaComponent = altraProvinciaComponent;
	}
}
