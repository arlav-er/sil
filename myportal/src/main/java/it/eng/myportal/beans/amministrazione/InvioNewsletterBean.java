package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.Mailer;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
public class InvioNewsletterBean{

	private String mailFrom;
	private String mailToCustom;
	private String mailTo;
	private String mailOggetto;
	private String mailCorpo;
	
	public InvioNewsletterBean() {
		super();
	}
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@PostConstruct
	protected void postConstruct() {		
	}

	public void inviaNewsletter() {
		try {			
			//String from = mailFrom;
			String tos = mailTo;			
			String[] arrTos = tos.split(";");			
			List<String> listaTo = Arrays.asList(arrTos);
			 									
			EmailDTO invioEmail = EmailDTO.buildInvioNewsletter(mailFrom, mailToCustom, listaTo, mailOggetto, mailCorpo);
			if (invioEmail.getTos().size() < 1) {
				throw new MyPortalException("Errore invio, indirizzi email errati.", true);						
			}
			
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, invioEmail);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Invio effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);			
			
		} 
		catch (MyPortalException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary(e.getStrMessaggio());   
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} 
		catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante l'invio della newsletter. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public String getMailTo() {
		return mailTo;
	}

	public String getMailOggetto() {
		return mailOggetto;
	}

	public String getMailCorpo() {
		return mailCorpo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public void setMailOggetto(String mailOggetto) {
		this.mailOggetto = mailOggetto;
	}

	public void setMailCorpo(String mailCorpo) {
		this.mailCorpo = mailCorpo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailToCustom() {
		return mailToCustom;
	}

	public void setMailToCustom(String mailToCustom) {
		this.mailToCustom = mailToCustom;
	}

}
