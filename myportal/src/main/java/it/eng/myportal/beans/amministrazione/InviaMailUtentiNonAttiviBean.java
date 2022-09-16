package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.utils.Mailer;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;


@ManagedBean
@ViewScoped
public class InviaMailUtentiNonAttiviBean extends AbstractBaseBean {
	@EJB 
	PfPrincipalHome pfPrincipalHome;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public InviaMailUtentiNonAttiviBean() {
		super();
	}

	@PostConstruct
	protected void postConstruct() {		
	}

	public void inviaMail() {   
		try {
			List<PfPrincipal> users = pfPrincipalHome.findUserDaSPilNonAbilitati();								
			for (PfPrincipal pfPrincipal : users) {
				PfPrincipalDTO pfdto = pfPrincipalHome.toDTO(pfPrincipal);
				
				RegisterUtenteDTO dto = new RegisterUtenteDTO();
	
				dto.setNome(pfdto.getNome());
				dto.setCognome(pfdto.getCognome());
				dto.setUsername(pfdto.getUsername());
				dto.setEmail(pfdto.getEmail());
				dto.setPassword("Temporanea123");
				dto.setActivateToken(pfdto.getConfirmationToken());
					
				EmailDTO mail = EmailDTO.buildRegistrationEmailCurriculum(dto);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, mail);
			}
			
			addInfoMessage("data.updated");
		
		} catch (RuntimeException e) {
			addErrorMessage("generic.error", e);
		}
	}
	
}
