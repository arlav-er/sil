package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterConsulenteDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.entity.home.ConsulenteInfoHome;
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
 * BackingBean per la pagina di creazione di un consulente
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class RegistraUtenteConsulenteBean {

	@EJB
	private ConsulenteInfoHome consulenteInfoHome;

	private RegisterConsulenteDTO consulente;

	public RegistraUtenteConsulenteBean() {
		super();
	}
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@PostConstruct
	protected void postConstruct() {
		consulente = new RegisterConsulenteDTO();
	}

	public void registraUtente() {
		try {
			consulente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			consulenteInfoHome.register(consulente, false);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)consulente, true);  
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			consulente = new RegisterConsulenteDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public RegisterConsulenteDTO getConsulente() {
		return consulente;
	}

	public void setConsulente(RegisterConsulenteDTO consulente) {
		this.consulente = consulente;
	}

}
