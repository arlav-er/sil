package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterCoordinatoreDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.entity.home.CoordinatoreInfoHome;
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

/**
 * BackingBean per la pagina di creazione di un coordinatore
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class RegistraUtenteCoordinatoreBean {

	@EJB
	private CoordinatoreInfoHome coordinatoreInfoHome;

	private RegisterCoordinatoreDTO coordinatore;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public RegistraUtenteCoordinatoreBean() {
		super();
	}

	@PostConstruct
	protected void postConstruct() {
		coordinatore = new RegisterCoordinatoreDTO();
	}

	public void registraUtente() {
		try {
			
			coordinatore.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			coordinatoreInfoHome.register(coordinatore, false);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)coordinatore, true);  
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			coordinatore = new RegisterCoordinatoreDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public RegisterCoordinatoreDTO getCoordinatore() {
		return coordinatore;
	}

	public void setCoordinatore(RegisterCoordinatoreDTO coordinatore) {
		this.coordinatore = coordinatore;
	}

}
