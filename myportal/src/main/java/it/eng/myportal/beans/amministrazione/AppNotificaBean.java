package it.eng.myportal.beans.amministrazione;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.amministrazione.pojo.UtentePojo;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.timer.TimerSendAppNotificaSingleton;
import it.eng.myportal.utils.PaginationHandler;

/**
 * Bean per l'amministratore delle notifiche ad app
 * 
 * @author
 */
@ManagedBean(name = "amministrazioneAppNotificaBean")
@ViewScoped
public class AppNotificaBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(AppNotificaBean.class);
	private static final int ITEMS_PER_PAGE = 10;
	private static final int MAX_ROWS_SELECTED = 20;

	private PaginationHandler paginationHandler;
	private List<UtentePojo> listaUsersApp = new ArrayList<UtentePojo>();
	private TreeSet<Integer> selectedUsersApp = new TreeSet<Integer>();

	private Integer idPfPrincipalRic;
	private String usernameRic;
	private String nomeRic;
	private String cognomeRic;
	private String emailRic;

	private Boolean ricercaEseguita = false;
	private Future<Integer> asyncResult = null;

	@EJB
	private PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	TimerSendAppNotificaSingleton timerSendAppNotificaSingleton;

	/**
	 * Inizializza il bean.
	 */
	@PostConstruct
	public void postConstruct() {
		if (!"amministratore".equals(getSession().getUsername())) {
			redirectPublicIndex();
		}
	}

	/**
	 * Questo metodo viene chiamato quando viene effettuata una ricerca
	 */
	public void cercaUsersApp() {
		Long ricercaCount = pfIdentityDeviceHome.getCountUsersApp(idPfPrincipalRic, usernameRic, nomeRic, cognomeRic,
				emailRic, true /* soloAbilitato */);
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, ricercaCount);
		paginationHandler.setCurrentPage(1);
		ricercaEseguita = true;
		changePage();
	}

	/** Metodo chiamato quando viene cambiata pagina */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		listaUsersApp = pfIdentityDeviceHome.findUsersAppPojo(idPfPrincipalRic, usernameRic, nomeRic, cognomeRic,
				emailRic, true /* soloAbilitato */, startResultsFrom, paginationHandler.getChuckSize());
	}

	/** Azzera il contenuto dei campi di ricerca. */
	public void resetSearchParams() {
		idPfPrincipalRic = null;
		usernameRic = null;
		nomeRic = null;
		cognomeRic = null;
		emailRic = null;
	}

	/** Azzera il contenuto dei campi legati all'esecuzione attività */
	public void resetExecuteParams() {
		selectedUsersApp.clear();
	}

	public void changeListenerSelectedUser(ValueChangeEvent event) {
		Boolean selected = (Boolean) event.getNewValue();
		Integer idPfPrincipal = (Integer) ((UIInput) event.getSource()).getAttributes().get("idPfPrincipal");

		if (selected)
			selectedUsersApp.add(idPfPrincipal);
		else
			selectedUsersApp.remove(idPfPrincipal);
	}

	public int getNumSelected() {
		return selectedUsersApp.size();
	}

	/** Esecuzione attività invio notifiche lavorative */
	public void sendNotificheBatch() {
		try {
			if (selectedUsersApp.size() > MAX_ROWS_SELECTED) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
						"E' possibile selezionare al massimo " + MAX_ROWS_SELECTED + " utenti",
						"E' possibile selezionare al massimo " + MAX_ROWS_SELECTED + " utenti");
				addMessage(null, message);
			} else {
				List<Integer> list = null;
				if (!selectedUsersApp.isEmpty())
					list = new ArrayList<Integer>(selectedUsersApp);

				asyncResult = timerSendAppNotificaSingleton.sendAppNotificheProgrammato(list);

				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Attività avviata correttamente. E' possibile verificare lo stato dell'attività attraverso il bottone di verifica",
						"Attività avviata correttamente. E' possibile verificare lo stato dell'attività attraverso il bottone di verifica");
				addMessage(null, message);

				resetExecuteParams();
				ricercaEseguita = false;
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errore in avvio dell'attività",
					"Errore in avvio dell'attività");
			addMessage(null, message);
		}
	}

	public void checkStatusNotificheBatch() {
		/*
		 * Determina, rispetto al Future il numero di notifiche inviate. Essendo questo bean con scope di tipo view il
		 * refresh della pagina non mantiene l'informazione del future. Essendo una funzione di amministrazione è stato
		 * volutamente implementato cosi.
		 */
		if (asyncResult != null) {
			if (asyncResult.isDone()) {
				Integer num = 0;

				try {
					num = asyncResult.get();
				} catch (ExecutionException | InterruptedException e) {
					e.printStackTrace();
				}

				StringBuffer str = new StringBuffer();
				switch (num) {
				case 0:
					str.append("Non è stata inviata alcuna notifica");
					break;
				case 1:
					str.append("E' stata inviata una notifica");
					break;
				default:
					str.append("Sono state inviate ").append(num).append(" notifiche");
					break;
				}

				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Attività terminata. " + str.toString(), "Attività terminata. " + str.toString());
				addMessage(null, message);
			} else {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Attività in esecuzione",
						"Attività in esecuzione");
				addMessage(null, message);
			}
		} else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Nessuna attività avviata",
					"Nessuna attività presente");
			addMessage(null, message);
		}
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public void setPaginationHandler(PaginationHandler paginationHandler) {
		this.paginationHandler = paginationHandler;
	}

	public List<UtentePojo> getListaUsersApp() {
		return listaUsersApp;
	}

	public Integer getIdPfPrincipalRic() {
		return idPfPrincipalRic;
	}

	public void setIdPfPrincipalRic(Integer idPfPrincipalRic) {
		this.idPfPrincipalRic = idPfPrincipalRic;
	}

	public String getUsernameRic() {
		return usernameRic;
	}

	public void setUsernameRic(String usernameRic) {
		this.usernameRic = usernameRic;
	}

	public String getNomeRic() {
		return nomeRic;
	}

	public void setNomeRic(String nomeRic) {
		this.nomeRic = nomeRic;
	}

	public String getCognomeRic() {
		return cognomeRic;
	}

	public void setCognomeRic(String cognomeRic) {
		this.cognomeRic = cognomeRic;
	}

	public String getEmailRic() {
		return emailRic;
	}

	public void setEmailRic(String emailRic) {
		this.emailRic = emailRic;
	}

	public Boolean getRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(Boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}

}
