package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterSoggettoPubblicoDTO;
import it.eng.myportal.entity.SoggettoPubblicoImport;
import it.eng.myportal.entity.home.SoggettoPubblicoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import java.util.List;

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
public class RegistraUtenteSoggettoPubblicoBean {

	@EJB
	private SoggettoPubblicoHome soggettoPubblicoHome;

	private RegisterSoggettoPubblicoDTO soggettoPubblico;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public RegistraUtenteSoggettoPubblicoBean() {
		super();
	}

	@PostConstruct
	protected void postConstruct() {
		soggettoPubblico = new RegisterSoggettoPubblicoDTO();
	}

	public void registraUtente() {   
		try {
			
			soggettoPubblico.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			soggettoPubblicoHome.register(soggettoPubblico, false);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)soggettoPubblico, true, null);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			soggettoPubblico = new RegisterSoggettoPubblicoDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	
	public RegisterSoggettoPubblicoDTO getSoggettoPubblico() {
		return soggettoPubblico;
	}

	public void setSoggettoPubblico(RegisterSoggettoPubblicoDTO soggettoPubblico) {
		this.soggettoPubblico = soggettoPubblico;
	}
	
	public void importaUtenti() {
		List<SoggettoPubblicoImport> listaUtenti = soggettoPubblicoHome.findAllImport();
		for (SoggettoPubblicoImport soggImport : listaUtenti) {
			RegisterSoggettoPubblicoDTO soggetto = soggettoPubblicoHome.portingSoggettoPubblicoRER(soggImport);
			
			if (soggetto != null) {
				EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailAtipici((RegisterDTO)soggetto, true, null);  
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			}			
		}
	}
	
}
