package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterProvinciaDTO;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

@ManagedBean
@ViewScoped
public class RegistraUtenteProvinciaBean {

	private static final String INDEX_OUTCOME = "index";
	@EJB
	private ProvinciaHome provinciaHome;

	private RegisterProvinciaDTO utenteProvinciaDto;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	public RegistraUtenteProvinciaBean() {
		super();
	}
		
	@PostConstruct
	protected void postConstruct() {
		utenteProvinciaDto = new RegisterProvinciaDTO();
	}

	public String registraUtente() {
		try {

			utenteProvinciaDto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			provinciaHome.register(utenteProvinciaDto, false);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)utenteProvinciaDto, true);  
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			utenteProvinciaDto = new RegisterProvinciaDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return "";
		}
		return INDEX_OUTCOME;
	}

	public RegisterProvinciaDTO getUtenteProvinciaDto() {
		return utenteProvinciaDto;
	}

	public void setUtenteProvinciaDto(RegisterProvinciaDTO utenteProvinciaDto) {
		this.utenteProvinciaDto = utenteProvinciaDto;
	}

}
