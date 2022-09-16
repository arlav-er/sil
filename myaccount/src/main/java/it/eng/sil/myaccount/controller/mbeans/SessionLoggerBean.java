package it.eng.sil.myaccount.controller.mbeans;

import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;
import it.eng.sil.myaccount.controller.mbeans.session.AccountInfoBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "sessionLogger")
@SessionScoped
public class SessionLoggerBean extends AbstractBackingBean implements Serializable {
	private static final long serialVersionUID = -3427129642114349428L;

	public static final int AMMINISTRATORE_ACCESS = 0;
	public static final int UTENTE_ACCESS = 1;
	public static final int AZIENDA_ACCESS = 2;
	public static final int PROVINCIA_ACCESS = 3;
	public static final int REGIONE_ACCESS = 4;
	public static final int CERTIFICATORE_ACCESS = 5;
	public static final int CONSULENTE_ACCESS = 6;
	public static final int COORDINATORE_ACCESS = 7;
	public static final int PATRONATO_ACCESS = 8;
	public static final int SOGGPUBB_ACCESS = 9;

	private AziendaInfo aziendaInfo;

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

	private AccountInfoBean accountInfoBean;
	private UserProfiloPOJO userInfoPOJO;

	@Override
	protected void initPostConstruct() {
		loadData();
	}

	private void loadData() {
		accountInfoBean = (AccountInfoBean) getSessionObject("accountInfoBean");

		if (accountInfoBean != null) {
			userInfoPOJO = accountInfoBean.getUserInfo();
		}
	}

	public Boolean isUserLoggedIn() {
		return accountInfoBean != null;
	}

	public Object getSessionObject(String objName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> sessionMap = extCtx.getSessionMap();
		return sessionMap.get(objName);
	}

	public Boolean isAzienda() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			if (aziendaInfo != null) {
				return true;
			}
			try {
				if (userInfoPOJO.isAzienda()) {
					aziendaInfo = aziendaInfoEJB.findById(userInfoPOJO.getIdPfPrincipal());
					return true;
				}
			} catch (MyCasNoResultException e) {
				// non si deve far nulla! DC docet!
			}
			return false;
		} else {
			return false;
		}
	}

	public Boolean isUtente() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return !userInfoPOJO.isAzienda() && !userInfoPOJO.isProvincia();
		} else {
			return false;
		}
	}

	public Boolean isProvincia() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isProvincia();
		} else {
			return false;
		}
	}

	public Boolean isRegione() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isRegione();
		} else {
			return false;
		}
	}

	public Boolean isCertificatore() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isCertificatore();
		} else {
			return false;
		}
	}

	public Boolean isConsulente() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isConsulente();
		} else {
			return false;
		}
	}

	public Boolean isCoordinatore() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isCoordinatore();
		} else {
			return false;
		}
	}

	public Boolean isPatronato() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isPatronato();
		} else {
			return false;
		}
	}

	public Boolean isSoggettoPubblico() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isSoggettoPubblico();
		} else {
			return false;
		}
	}

	public Boolean isAmministratore() {
		// try to reload the data, insuring session consistency
		if (accountInfoBean == null)
			loadData();

		if (isUserLoggedIn()) {
			return userInfoPOJO.isAdmin();
		} else {
			return false;
		}
	}

	public AziendaInfo getAziendaInfo() {
		return aziendaInfo;
	}

	public void setAziendaInfo(AziendaInfo aziendaInfo) {
		this.aziendaInfo = aziendaInfo;
	}

	public void grantAccessFor(int accessType) {
		boolean accessGranted = false;

		if (accessType == AMMINISTRATORE_ACCESS) {
			if (isAmministratore()) {
				accessGranted = true;
				log.debug("access granted for amministratore di sistema");
			}
		}

		if (accessType == UTENTE_ACCESS) {
			if (isUtente()) {
				accessGranted = true;
				log.debug("access granted for utente");
			}
		}

		if (accessType == AZIENDA_ACCESS) {
			if (isAzienda()) {
				accessGranted = true;
				log.debug("access granted for azienda");
			}
		}

		if (accessType == PROVINCIA_ACCESS) {
			if (isProvincia()) {
				accessGranted = true;
				log.debug("access granted for provinica");
			}
		}

		if (accessType == REGIONE_ACCESS) {
			if (isRegione()) {
				accessGranted = true;
				log.debug("access granted for regione");
			}
		}

		if (accessType == CERTIFICATORE_ACCESS) {
			if (isCertificatore()) {
				accessGranted = true;
				log.debug("access granted for regione");
			}
		}

		if (accessType == CONSULENTE_ACCESS) {
			if (isConsulente()) {
				accessGranted = true;
				log.debug("access granted for regione");
			}
		}

		if (accessType == COORDINATORE_ACCESS) {
			if (isCoordinatore()) {
				accessGranted = true;
				log.debug("access granted for regione");
			}
		}

		if (accessType == SOGGPUBB_ACCESS) {
			if (isSoggettoPubblico()) {
				accessGranted = true;
				log.debug("access granted for soggetto");
			}
		}

		if (accessType == PATRONATO_ACCESS) {
			if (isPatronato()) {
				accessGranted = true;
				log.debug("access granted for patronato");
			}
		}

		if (!accessGranted) {
			redirect404();
			log.debug("unauthorized access for accessType " + accessType);
		}

	}

	public static int getAmministratoreAccess() {
		return AMMINISTRATORE_ACCESS;
	}

	public static int getUtenteAccess() {
		return UTENTE_ACCESS;
	}

	public static int getAziendaAccess() {
		return AZIENDA_ACCESS;
	}

	public static int getProvinciaAccess() {
		return PROVINCIA_ACCESS;
	}

	public static int getRegioneAccess() {
		return REGIONE_ACCESS;
	}

	public static int getCertificatoreAccess() {
		return CERTIFICATORE_ACCESS;
	}

	public static int getConsulenteAccess() {
		return CONSULENTE_ACCESS;
	}

	public static int getCoordinatoreAccess() {
		return COORDINATORE_ACCESS;
	}

	public static int getPatronatoAccess() {
		return PATRONATO_ACCESS;
	}

	public static int getSoggettoPubblicoAccess() {
		return SOGGPUBB_ACCESS;
	}
}
