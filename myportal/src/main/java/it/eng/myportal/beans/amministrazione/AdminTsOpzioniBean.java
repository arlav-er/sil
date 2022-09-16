package it.eng.myportal.beans.amministrazione;

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

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.ts.TsOpzioniEJB;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.ts.TsOpzioni;

/**
 * @author pegoraro
 */
@ManagedBean
@ViewScoped
public class AdminTsOpzioniBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(AdminTsOpzioniBean.class);

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;
	
	@EJB
	TsOpzioniEJB tsOpzioniEJB;

	List<TsOpzioni> opzioniSistema;
	
	
	TsOpzioni opzioneSelezionata;

	/**
	 * Inizializza il bean.
	 */
	@PostConstruct
	public void postConstruct() {
		if ("amministratore".equals(getSession().getUsername())) {
			opzioniSistema = tsGetOpzioniEJB.getAllOptions();
		} else {
			redirectPublicIndex();
		}
	}

	public List<TsOpzioni> getOpzioniSistema() {
		return opzioniSistema;
	}

	public void setOpzioniSistema(List<TsOpzioni> opzioniSistema) {
		this.opzioniSistema = opzioniSistema;
	}

	public TsOpzioni getOpzioneSelezionata() {
		return opzioneSelezionata;
	}

	public void setOpzioneSelezionata(TsOpzioni opzioneSelezionata) {
		this.opzioneSelezionata = opzioneSelezionata;
	}
	

	/**
	 * Metodo chiamato all'edit di una config.
	 */
	public void sync() {
        FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (opzioneSelezionata.getIdOpzione() != null) {
				opzioneSelezionata = tsOpzioniEJB.merge(opzioneSelezionata);
				context.addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Opzione salvata."));
			} else {
				log.error("GRAVE, inserimento nuove opzioni non previsto");
			}
		} catch (Exception e) {
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errore", "Errore durante l'inserimento."));
		}
	}

}
