package it.eng.myportal.beans.azienda.tirocini;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.utente.messaging.UtenteMessagingBean;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.ejb.tirocini.DoTirociniEjb;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.helpers.LazyDoTirociniModel;
import it.eng.myportal.utils.Mailer;

@ManagedBean(name = "aziendaTirociniBean")
@ViewScoped
public class AziendaTirociniBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(UtenteMessagingBean.class);

	private String currentSection;
	
	private DoTirocini doTirocini;
	private LazyDataModel<DoTirocini> doTirociniList;
	private PfPrincipal pfPrincipal;

	@EJB
	private DoTirociniEjb doTirociniEjb;
	
	@EJB
	private AziendaInfoHome aziendaInfoHome;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	@ManagedProperty(value="#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;
	
	@PostConstruct
	public void init() {
		doTirocini = new DoTirocini();
		pfPrincipal = aziendaInfoHome.findById(session.getPrincipalId()).getPfPrincipal();
	}
	
	public String getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}
	
	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}
	
	public DoTirocini getDoTirocini() {
		return doTirocini;
	}

	public void setDoTirocini(DoTirocini doTirocini) {
		this.doTirocini = doTirocini;
	}
		
	public LazyDataModel<DoTirocini> getDoTirociniList() {
		return doTirociniList;
	}

	public void setDoTirociniList(LazyDataModel<DoTirocini> doTirociniList) {
		this.doTirociniList = doTirociniList;
	}

	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}
	
	/**
	 * Main navigation - xhtml path passed from here
	 * */
	public void updateSection() {
		currentSection = getRequestParameter("currentSection");
	}	
	
	public List<DeMansioneMin> getMansioneMinList() {
		if(doTirocini.getDeMansione() == null)
			return null;
		
		List<DeMansioneMin> result = autoCompleteBean.completeMansioneMinisterialeByCodMansione(doTirocini.getDeMansione().getCodMansione());
		
		if(result == null) {
			result = new ArrayList<DeMansioneMin>(); 
		}
		
		return result;
	}
	
	public void initCreate() {
		log.debug("initCreate() called");
		doTirocini = new DoTirocini();
	}
	
	public void initEdit() {
		log.debug("initEdit() called");
		try{
			doTirocini = doTirociniEjb.findMineById(Integer.parseInt(getRequestParameter("tirociniId")), pfPrincipal);
		} catch(Exception exception) {
			doTirocini = new DoTirocini();
			log.warn("Impossibile trovare l'istanza doTirocini mediante il dato id: " + getRequestParameter("tirociniId") +
					". Ne creo uno nuovo");
		}
		
	}
	
	public void initView() {
		log.debug("initView() called");
		try{
			doTirocini = doTirociniEjb.findMineById(Integer.parseInt(getRequestParameter("tirociniId")), pfPrincipal);
		} catch(Exception exception) {
			doTirocini = new DoTirocini();
			log.warn("Impossibile trovare l'istanza doTirocini mediante il dato id: " + getRequestParameter("tirociniId") +
					". Ne creo uno nuovo");
		}
		
	}
	
	public void initList() {
		log.debug("initList() called");
		doTirociniList = new LazyDoTirociniModel(LazyDoTirociniModel.FILTER_AZIENDA, session.getPrincipalId());
	}
	
	public void sync() {
		DoTirocini savedDoTirocini = doTirociniEjb.sync(doTirocini,pfPrincipal);
		RequestContext.getCurrentInstance().addCallbackParam("tirociniId", savedDoTirocini.getIdDoTirocinio());
	}
	
	public void invia() {
		log.info("invia() mail CPI called");
		
		String emailCpiSend = doTirocini.getDeCpi().getEmailPortale(); 
		EmailDTO registerEmail = EmailDTO.buildEmailDelegaTirocini(doTirocini, emailCpiSend);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerEmail);
		
	}

}
