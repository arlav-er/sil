package it.eng.myportal.beans;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RecuperoPasswordDTO;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.home.PfPrincipalHome;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class RecuperoPasswordBean {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	protected ErrorsSingleton errorsBean;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private static final Log log = LogFactory.getLog(RecuperoPasswordBean.class);

	private Integer stato;

	private PfPrincipalDTO pfPrincipal;

	private RecuperoPasswordDTO recuperoData;

	public RecuperoPasswordBean() {
		super();

	}

	@PostConstruct
	public void postConstruct() {
		recuperoData = new RecuperoPasswordDTO();
		stato = 1;
		// FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		// //bugfix
	}

	public void recuperaUserOrEmail() {
		if (recuperoData.getUserOrEmail() != null && !recuperoData.getUserOrEmail().isEmpty()) {
			pfPrincipal = pfPrincipalHome.findDTOByUsername(recuperoData.getUserOrEmail());
			

			if (pfPrincipal != null) {
				// trovato tramite username
				recuperoData.setUserName(pfPrincipal.getUsername());
			} else {
				// cerco per email
				List<PfPrincipalDTO> pfPrincipalDTOs = pfPrincipalHome.findDTOByEmail(recuperoData
						.getUserOrEmail());
				if (pfPrincipalDTOs.size() > 1) {
					// c'e' piu' di un utente con la stessa mail
					addErrorMessage("recuperopassword.caso_particolare");
					stato = 1;
					return;
				} else if (pfPrincipalDTOs.size() == 1) {
					// c'e' un solo utente con la questa mail
					pfPrincipal = pfPrincipalDTOs.get(0);
					
				}
			}

			if (pfPrincipal != null) {
				//ho trovato l'utente
				recuperoData.setUserName(pfPrincipal.getUsername());
				recuperoData.setNome(pfPrincipal.getNome());
				recuperoData.setCognome(pfPrincipal.getCognome());
				recuperoData.setEmail(pfPrincipal.getEmail());
				recuperoData.setDomanda(pfPrincipal.getDomanda());
				recuperoData.setRisposta(null);

				stato = 2;
			} else {
				// non ho trovato l'utente ne con lo username ne con la password
				recuperoData.setRisposta(null);
				addErrorMessage("recuperopassword.utenteno");
			}
		}
	}

	public boolean rispondi() {

		String risposta = pfPrincipal.getRisposta();
		if (!recuperoData.getRisposta().equalsIgnoreCase(risposta)) {
			addErrorMessage("recuperopassword.rispostano");
			// stato = 1;
			return false;
		}
		String token = Utils.randomString(ConstantsSingleton.TOKEN_LENGTH);
		recuperoData.setPasswordToken(token);
		pfPrincipal = pfPrincipalHome.findDTOById(pfPrincipal.getId());
		pfPrincipal.setRecuperoPasswordToken(token);
		pfPrincipalHome.mergeDTO(pfPrincipal, pfPrincipal.getIdPrincipalIns());

		EmailDTO resetPwdEmail = EmailDTO.buildResetPwdEmail(recuperoData);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, resetPwdEmail);
		stato = 3;
		return false;
	}

	public RecuperoPasswordDTO getRecuperoData() {
		return recuperoData;
	}

	public void setRecuperoData(RecuperoPasswordDTO recuperoData) {
		this.recuperoData = recuperoData;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	private void addErrorMessage(String codErrore) {
		String msgText = errorsBean.getProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText);
		addMessage(null, message);
	}

	private void addMessage(String id, FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(id, message);
	}

	private void addInfoMessage(String codMessaggio) {
		String msgText = errorsBean.getProperty(codMessaggio);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
		addMessage(null, message);
	}
}
