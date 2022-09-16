package it.eng.myportal.beans;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.sil.base.business.GamificationRemoteClient;
import it.eng.sil.base.exceptions.GamificationException;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean della pagina che gestisce la conferma della registrazione forte.
 * 
 * @author Turrini S.
 * 
 */
@ManagedBean
@ViewScoped
public class ConfermaRegistrazioneForteBean extends AbstractBaseBean implements Serializable {

	private static final long serialVersionUID = 7668358908366984319L;

	private static Log log = LogFactory.getLog(ConfermaRegistrazioneForteBean.class);

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	private String registrazioneForteToken;
	private String codiceRegistrazione;

	private Boolean ok = false;

	public void conferma(ComponentSystemEvent event) throws AbortProcessingException {

		if (registrazioneForteToken == null)
			return;
		UtenteDTO user = session.getConnectedUtente();
		PfPrincipalDTO principalDTO = pfPrincipalHome.confermaAbilitazioneForte(user, registrazioneForteToken);

		if (principalDTO != null) {
			try {
				// Provo ad assegnare il badge, se la gamification è abilitata.
				GamificationRemoteClient gamificationClient = new GamificationRemoteClient();
				if (gamificationClient.isGamificationEnabled(ConstantsSingleton.Gamification.IS_ENABLED_ENDPOINT)) {
					gamificationClient.assignBadge(ConstantsSingleton.Gamification.ASSIGN_BADGE_ENDPOINT, "ABILSERV",
							principalDTO.getId(), 1.0);
				}
			} catch (GamificationException e) {
				log.error("Errore durante la assignBadge in confermaRegistrazioneForteBean: " + e.toString());
			}

			EmailDTO confirmRegistrationEmail = EmailDTO.buildConfermaAbilitazioneForteEmail(principalDTO);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, confirmRegistrationEmail);
			ok = true;
			session.refreshSession();
			addInfoMessage("regforte.confermato");
		} else {
			ok = false;
			addErrorMessage("regforte.errore");
		}

	}

	public void confermaManuale() {

		if (codiceRegistrazione == null)
			return;

		UtenteDTO user = session.getConnectedUtente();
		PfPrincipalDTO principalDTO = pfPrincipalHome.confermaAbilitazioneForte(user, codiceRegistrazione);

		if (principalDTO != null) {
			ok = true;
			session.refreshSession();
			addInfoMessage("regforte.confermato");
			EmailDTO confirmRegistrationEmail = EmailDTO.buildConfermaAbilitazioneForteEmail(principalDTO);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, confirmRegistrationEmail);

		} else {
			ok = false;
			codiceRegistrazione = null;
			addErrorMessage("regforte.coderrato");
		}

	}

	public String getRegistrazioneForteToken() {
		return registrazioneForteToken;
	}

	public void setRegistrazioneForteToken(String registrazioneForteToken) {
		this.registrazioneForteToken = registrazioneForteToken;
	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}

	public String getCodiceRegistrazione() {
		return codiceRegistrazione;
	}

	public void setCodiceRegistrazione(String codiceRegistrazione) {
		this.codiceRegistrazione = codiceRegistrazione;
	}

}