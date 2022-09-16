package it.eng.myportal.beans.amministrazione;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RecuperoPasswordDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.ejb.GenDecodRandom;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;


@ManagedBean
@SessionScoped
public class AmmSessionBean {
		
	@EJB
	private transient GenDecodRandom  genDecodRandom;
	
	@EJB
	PfPrincipalHome pfPrincipalHome; 
	
	@EJB
	ClicLavoroEjb clicLavoroEjb;
	
	@EJB
	YgAdesioneHome ygAdesioneHome;
	
	@ManagedProperty(value = "#{sessionBean}")
	protected SessionBean session;

	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	
	protected static Log log = LogFactory.getLog(AmmSessionBean.class);
	
	private String emailAddress;
	
	public AmmSessionBean(){
	}
	
	  
	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}
	
	public String logout() {
	//	log.debug("Svuotamento delle collection di supporto");
		genDecodRandom.empty();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(true);
		session.invalidate();
		
		try {
			ec.redirect(ec.getRequestContextPath());
		} catch (IOException e) {
			log.error("Errore durante la redirect: " + e.getMessage());
		}
		return "";
	}
	
	public String testEmail() {
		
		List<EmailDTO> listaEmail = new ArrayList<EmailDTO>();
		
		PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOById(0); 
		listaEmail.add(EmailDTO.buildConfermaAbilitazioneForteEmail(pfPrincipalDTO));
		RegisterAziendaDTO registerAziendaDTO = new RegisterAziendaDTO();
		registerAziendaDTO.setEmail(emailAddress);
		registerAziendaDTO.setUsername("USERNAME");
		registerAziendaDTO.setPassword("PASSWORD");
		registerAziendaDTO.setNome("NOME");
		registerAziendaDTO.setCognome("COGNOME");
		listaEmail.add(EmailDTO.buildConfirmRegistrationeEmail(registerAziendaDTO));
		
		RegisterUtenteDTO registerUtenteDTO = new RegisterUtenteDTO();
		registerUtenteDTO.setEmail(emailAddress);
		registerUtenteDTO.setUsername("USERNAME");  
		registerUtenteDTO.setPassword("PASSWORD");
		registerUtenteDTO.setNome("NOME");
		registerUtenteDTO.setCognome("COGNOME");
		listaEmail.add(EmailDTO.buildConfirmRegistrazioneForteEmail(registerUtenteDTO, null, null));
		listaEmail.add(EmailDTO.buildConfirmRegistrazioneForteEmail(registerUtenteDTO, "TEST_CODICE", "TEST@EMAIL.IT"));
		listaEmail.add(EmailDTO.buildRegistrationEmail(registerAziendaDTO));
		listaEmail.add(EmailDTO.buildRegistrationEmail(registerUtenteDTO));
		listaEmail.add(EmailDTO.buildRegistrationEmailRichiestaPersonale(registerAziendaDTO));
		
		UtenteCompletoDTO utenteCompletoDTO = new UtenteCompletoDTO();
		utenteCompletoDTO.setEmail(emailAddress);
		utenteCompletoDTO.setNome("NOME");
		listaEmail.add(EmailDTO.buildRegistrazioneForteEmail(utenteCompletoDTO, "TOKEN"));
		
		RecuperoPasswordDTO recuperoPasswordDTO = new RecuperoPasswordDTO();
		recuperoPasswordDTO.setCognome("COGNOME");
		recuperoPasswordDTO.setDomanda("DOMANDA");
		recuperoPasswordDTO.setEmail(emailAddress);
		recuperoPasswordDTO.setNome("NOME");
		recuperoPasswordDTO.setPasswordToken("TOKEN");
		recuperoPasswordDTO.setRisposta("RISPOSTA");
		recuperoPasswordDTO.setUserName("USERNAME");
		listaEmail.add(EmailDTO.buildResetPwdEmail(recuperoPasswordDTO));
		
		VaDatiVacancyDTO vaDatiVacancyDTO = new VaDatiVacancyDTO();
		vaDatiVacancyDTO.setAttivitaPrincipale("ATTIVITA' PRINCIPALE");
		listaEmail.add(EmailDTO.buildVacancyImportataEmail(pfPrincipalDTO, vaDatiVacancyDTO, true, "CODICE"));
		listaEmail.add(EmailDTO.buildVacancyImportataEmail(pfPrincipalDTO, vaDatiVacancyDTO, false, "CODICE"));
		
		for (EmailDTO emailDTO : listaEmail) {
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);
		}
		return "";
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
	public void inviaCvCliLavoro() {
		
		clicLavoroEjb.inviaCvCliLavoro();
			
	}
	
	public void inviaVacancyCliLavoro() {
		
		clicLavoroVacancyEjb.inviaVacancyCliLavoro();
		
	}
	
	public void riceviCvCliLavoro() {
		
		clicLavoroEjb.riceviCvCliLavoro();
			
	}
	
	public void riceviVacancyCliLavoro() {
		
		clicLavoroVacancyEjb.riceviVacancyCliLavoro();
		
	}

	
	public void executeBatchAnagAdesioniNoAccount() {
		
		ygAdesioneHome.processBatchRecuperoAnagNoAccount();
		
	}
	
	public void invioNewsletter() {
					
	}

	
	public void checkViewPage() {

		if (!session.isAdmin()) {
			log.warn("Tentativo di accedere alla pagina di admin non essendo Amministratore.");
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(ConstantsSingleton.BASE_URL);
			} catch (IOException e) {
				return;
			}
			return;
		}

	}
}
