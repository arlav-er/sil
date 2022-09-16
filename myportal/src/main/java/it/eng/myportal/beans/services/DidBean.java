package it.eng.myportal.beans.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.home.DidHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.enums.TipoStampa;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.base.business.GamificationRemoteClient;
import it.eng.sil.base.exceptions.GamificationException;

/**
 * BackingBean per la pagina relativa ai servizi DID.
 * 
 * 
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class DidBean extends AbstractServiceBaseBean {
	private static final String DATA_LICENZIAMENTO_OBBLIGATORIA = "La data di licenziamento è obbligatoria";
	private static final String DATA_LETTERA_OBBLIGATORIA = "La data della lettera di licenziamento è obbligatoria";

	private boolean canLoadService = false;
	private String dataFineDisabilitazioneMsgApp;
	private String provinciaServiziAmm = "";
	private boolean rischioDisoccupazione;
	private Date dataLicenziamento;
	private Date dataLetteraLicenziamento;
	private String script;

	private SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
	private List<WsStampaDTO> stampeDid = new ArrayList<WsStampaDTO>();

	@EJB
	private WsStampaHome wsStampaHome;

	@EJB
	private TsGetOpzioniEJB tsGetOpzioniEJB;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DidHome didHome;

	/**
	 * Nell'inizializzazione della pagina, carico la lista delle DID eventualmente già effettuate e inizializzo le
	 * informazioni dell'utente.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!utenteInfo.getAcceptedInformativaDid()) {
			addErrorMessage("services.denied.did");
			redirectHome();
		} else {
			Date dtFineDidApp = tsGetOpzioniEJB.getdtFineDisabilDidApp();
			dataFineDisabilitazioneMsgApp = formatter1.format(dtFineDidApp);

			UtenteInfo utente = utenteInfoHome.findByUsername(session.getUsername());
			if (utente.getDeProvincia() != null && utente.getDeProvincia().getCodProvincia() != null) {
				setProvinciaServiziAmm(utente.getDeProvincia().getCodProvincia());
			}

			canLoadService = true;
			stampeDid = wsStampaHome.getStampeDTO(TipoStampa.DID.getCodice(), getSession().getPrincipalId());
		}
	}

	/**
	 * Restituisce il flag "rischio disoccupazione" sotto forma di stringa, come verrà poi messo nell'XML di input per
	 * il webservice del SIL.
	 * 
	 * @return
	 */
	public String getRischioDisoccupazioneString() {
		return (rischioDisoccupazione) ? "SI" : "NO";
	}

	/**
	 * Crea lo script che chiama la funzione javascript stipulaNuovaDid (contenuta nel file services.js), che a sua
	 * volta chiamerà il servizio rest StipulaDid, che a sua volta chiamerà il webservice SIL che stipula effettivamente
	 * la did.
	 */
	public void updateScript() {
		List<WsStampaDTO> stampeDid = wsStampaHome.getStampeDTO("did", utenteInfo.getId());
		String showEarnedBadge = "false";

		try {
			GamificationRemoteClient gamificationClient = new GamificationRemoteClient();
			if (gamificationClient.isGamificationEnabled(ConstantsSingleton.Gamification.IS_ENABLED_ENDPOINT)
					&& (stampeDid == null || stampeDid.isEmpty())) {
				showEarnedBadge = "true";
			}
		} catch (GamificationException e) {
			log.error("Errore durante la isGamificationEnabled in DidBean: " + e.toString());
		}

		if (rischioDisoccupazione) {
			// Se il lavoratore è a rischio di disoccupazione, la data licenziamento e data lettera
			// DEVONO essere valorizzate.
			if (dataLicenziamento == null) {
				script = "";
				FacesContext.getCurrentInstance().addMessage("richiediDIDForm:dataLicenziamentoInput",
						new FacesMessage(DATA_LETTERA_OBBLIGATORIA));
			}

			if (dataLetteraLicenziamento == null) {
				script = "";
				FacesContext.getCurrentInstance().addMessage("richiediDIDForm:dataLetteraLicenziamentoInput",
						new FacesMessage(DATA_LICENZIAMENTO_OBBLIGATORIA));
			}

			if (dataLicenziamento != null && dataLetteraLicenziamento != null) {
				script = "stipulaNuovaDid( " + session.getPrincipalId() + ", '" + getRischioDisoccupazioneString()
						+ "', '" + formatter1.format(dataLicenziamento) + "', '"
						+ formatter1.format(dataLetteraLicenziamento) + "', " + ConstantsSingleton.COD_REGIONE + ", "
						+ getProvinciaServiziAmm() + ", '" + getDataFineDisabilitazioneMsgApp() + "', "
						+ showEarnedBadge + ");";
			}
		} else {
			script = "stipulaNuovaDid( " + session.getPrincipalId() + ", '" + getRischioDisoccupazioneString()
					+ "', null, null, " + ConstantsSingleton.COD_REGIONE + ", " + getProvinciaServiziAmm() + ", '"
					+ getDataFineDisabilitazioneMsgApp() + "', " + showEarnedBadge + ");";
		}
	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}

	public List<WsStampaDTO> getStampeDid() {
		return stampeDid;
	}

	public void setStampeDid(List<WsStampaDTO> stampeDid) {
		this.stampeDid = stampeDid;
	}

	public String getDataFineDisabilitazioneMsgApp() {
		return dataFineDisabilitazioneMsgApp;
	}

	public void setDataFineDisabilitazioneMsgApp(String dataFineDisabilitazioneMsgApp) {
		this.dataFineDisabilitazioneMsgApp = dataFineDisabilitazioneMsgApp;
	}

	public String getProvinciaServiziAmm() {
		return provinciaServiziAmm;
	}

	public void setProvinciaServiziAmm(String provinciaServiziAmm) {
		this.provinciaServiziAmm = provinciaServiziAmm;
	}

	public Boolean getRischioDisoccupazione() {
		return rischioDisoccupazione;
	}

	public void setRischioDisoccupazione(Boolean rischioDisoccupazione) {
		this.rischioDisoccupazione = rischioDisoccupazione;
	}

	public Date getDataLicenziamento() {
		return dataLicenziamento;
	}

	public void setDataLicenziamento(Date dataLicenziamento) {
		this.dataLicenziamento = dataLicenziamento;
	}

	public Date getDataLetteraLicenziamento() {
		return dataLetteraLicenziamento;
	}

	public void setDataLetteraLicenziamento(Date dataLetteraLicenziamento) {
		this.dataLetteraLicenziamento = dataLetteraLicenziamento;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
