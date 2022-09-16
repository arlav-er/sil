package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterCertificatoreDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.entity.CertificatoreImport;
import it.eng.myportal.entity.home.CertificatoreInfoHome;
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
public class RegistraUtenteCertificatoreBean {

	@EJB
	private CertificatoreInfoHome certificatoreInfoHome;

	private RegisterCertificatoreDTO certificatore;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public RegistraUtenteCertificatoreBean() {
		super();
	}

	@PostConstruct
	protected void postConstruct() {
		certificatore = new RegisterCertificatoreDTO();
	}

	public void registraUtente() {
		try {
			
			certificatore.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			certificatoreInfoHome.register(certificatore, false);
			
			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailCertificatore((RegisterDTO)certificatore, true, null);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Inserimento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			certificatore = new RegisterCertificatoreDTO();

		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public RegisterCertificatoreDTO getCertificatore() {
		return certificatore;
	}

	public void setCertificatore(RegisterCertificatoreDTO certificatore) {
		this.certificatore = certificatore;
	}

	
	public void importaUtenti() {
		List<CertificatoreImport> listaUtenti = certificatoreInfoHome.findAllImport();
		for (CertificatoreImport certificatoreImport : listaUtenti) {
			RegisterCertificatoreDTO certificatore = certificatoreInfoHome.portingCertificatoriRER(certificatoreImport);
			
			if (certificatore != null) {
				EmailDTO registrationEmail = EmailDTO.buildRegistrationEmailCertificatore((RegisterDTO)certificatore, true, null);  
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
			}			
		}
	}
}
