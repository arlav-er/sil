package it.eng.myportal.beans.patto;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.curriculum.VisualizzaCurriculumVitaeBean;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.PattoSil;
import it.eng.myportal.entity.PattoSilStorico;
import it.eng.myportal.entity.PfIdentityDevice;
import it.eng.myportal.entity.PfIdentityProvider;
import it.eng.myportal.entity.TipoAccettazioneEnum;
import it.eng.myportal.entity.ejb.OTPClientEJB;
import it.eng.myportal.entity.ejb.TextSingletonEjb;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.entity.home.PattoSilHome;
import it.eng.myportal.entity.home.PattoSilStoricoHome;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfTestiHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.ws.pattoonline.sil.PattoOnlineClientV2EJB;
import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.utils.StringUtils;
import it.eng.sil.coop.webservices.pattoonlinenew.AccettazionePattoTypeTipoAccettazione;
import it.eng.sil.coop.webservices.pattoonlinenew.EsitoTypeEsito;

@ManagedBean
@ViewScoped
public class PattoSilOTPBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(VisualizzaCurriculumVitaeBean.class);

	@EJB
	PattoSilHome pattoSilHome;

	@EJB
	PattoOnlineClientV2EJB pattoOnlineClientV2EJB;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AppNotificaHome appNotificaHome;

	@EJB
	PfTestiHome pfTestiHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	@EJB
	PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	PattoSilStoricoHome pattoSilStoricoHome;

	@EJB
	TextSingletonEjb textSingletonEjb;

	@EJB
	OTPClientEJB oTPClientEJB;

	private String generatedOTP;
	private String reinsertedOTP;
	private String currentOtpStep = "INITIAL";
	private String titoloStorico;

	private List<PattoSil> pattiCittadino;

	private UtenteInfoDTO utente;

	private PattoSil selectedPattoSIl;

	private boolean isRenderedOTP;

	private boolean isRenderedSMS;

	private boolean isRenderedEasyCheck;
	private boolean isRenderedButtonStorico;

	private List<PattoSilStorico> storicoList = new ArrayList<>();

	List<PfIdentityProvider> strongid = new ArrayList<>();
	// a livello di sistema
	private Boolean isOTPPushAvailable;
	private Boolean isOTPSmsAvailable;

	private OTPRequestEnum usedChannel;// serve per tipo_accettazione

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		utente = utenteInfoHome.findDTOByUsername(session.getUsername());
		List<PfIdentityProvider> providers = pfIdentityProviderHome.findByPFPId(utente.getId());

		List<PfIdentityDevice> devices = pfIdentityDeviceHome.findDevice(utente.getId());

		for (PfIdentityProvider provider : providers) {
			TipoProvider fed = provider.getCodTipoProvider();
			if (TipoProvider.FEDERA.equals(fed) || TipoProvider.ICAR.equals(fed) || TipoProvider.SPID.equals(fed))
				strongid.add(provider);
		}

		boolean isFedera = strongid != null && !strongid.isEmpty();
		boolean hasApp = devices != null && !devices.isEmpty();
		boolean hasCertifiedSMS = utente.getCellulareOTP() != null;

		// pilotano quale conferma chiedere al cittadino
		isRenderedOTP = false;
		isRenderedSMS = false;
		isRenderedEasyCheck = false;

		if (isFedera) {
			isRenderedEasyCheck = true;
		} else {
			if (hasApp) {
				isRenderedOTP = true;
				if (!isOTPPushAvailable())
					log.error("GRAVE: MyPortal-app non risulta installata in questa regione");
			}
			if (hasCertifiedSMS) {
				isRenderedSMS = true;
			}
		}

		if (!isRenderedEasyCheck && !isRenderedOTP && !isRenderedSMS) {
			// niente da fare
			log.error("Nessun canale di verifica valido per " + session.getPrincipalId());
		}

		pattiCittadino = pattoSilHome.findByIdPfPrincipal(session.getPrincipalId());

	}

	public boolean isOTPPushAvailable() {
		if (isOTPPushAvailable == null)
			isOTPPushAvailable = oTPClientEJB.isChannelAvailable(OTPRequestEnum.PUSH);
		return isOTPPushAvailable;
	}

	public boolean isOTPSmsAvailable() {
		if (isOTPSmsAvailable == null)
			isOTPSmsAvailable = oTPClientEJB.isChannelAvailable(OTPRequestEnum.SMS);
		return isOTPSmsAvailable;
	}

	public void submitVerificaSimpleRequest(PattoSil patto) {
		selectedPattoSIl = patto;
		RequestContext.getCurrentInstance().addCallbackParam("scadenza", this.getRemainingTime(patto));
	}

	public void submitVerificaPUSHRequest(PattoSil patto) {
		selectedPattoSIl = patto;

		if (isRenderedOTP) {
			try {
				generatedOTP = oTPClientEJB.createOTPRequest(utente.getId(), OTPRequestEnum.PUSH);
				usedChannel = OTPRequestEnum.PUSH;
			} catch (OtpRemoteException e) {
				log.error("ERRORE nella richiesta OTP push: " + e.getMessage());
			}
			log.info("Richiesta OTP PUSH per " + session.getPrincipalId() + ":" + generatedOTP);
		} else {
			log.error("ERRORE GRAVE FLUSSO in submitVerificaPUSHRequest() NON PREVISTO");
		}
		RequestContext.getCurrentInstance().addCallbackParam("scadenza", this.getRemainingTime(patto));
	}

	public void submitVerificaSMSRequest(PattoSil patto) {
		selectedPattoSIl = patto;

		if (isRenderedSMS) {
			try {
				generatedOTP = oTPClientEJB.createOTPRequest(utente.getId(), OTPRequestEnum.SMS);
				usedChannel = OTPRequestEnum.SMS;
			} catch (OtpRemoteException e) {
				log.error("ERRORE nella richiesta OTP push: " + e.getMessage());
			}
			log.info("Richiesta OTP SMS per " + session.getPrincipalId() + ":" + generatedOTP);
		} else {
			log.error("ERRORE GRAVE FLUSSO NON PREVISTO");
		}
		RequestContext.getCurrentInstance().addCallbackParam("scadenza", this.getRemainingTime(patto));
	}

	public void convalida() {
		//se Federa, non serve OTP
		boolean otppassed = isRenderedEasyCheck;

		// mi aspetto che lo reinserica correttamente a front-end in caso PUSH ed SMS
		if ((isRenderedOTP || isRenderedSMS) && (!StringUtils.isEmpty(reinsertedOTP))) {
			// chiamo servizio convalida
			try {
				log.info("Risposta OTP per " + session.getPrincipalId() + ": " + generatedOTP);
				otppassed = oTPClientEJB.replyOTP(generatedOTP, reinsertedOTP);
			} catch (Exception e) {
				log.error("ERRORE nella risposta OTP push: " + e.getMessage());
			}
		}

		if (!otppassed) {//codice inserito non valido
			String params = textSingletonEjb.get("cittadino_patto_online_otp_err").replaceAll("\\?", "");
			RequestContext.getCurrentInstance()
					.execute("showErrMessage('" + StringEscapeUtils.escapeEcmaScript(params) + "')");
			log.info("Risposta OTP ERRATA per " + session.getPrincipalId());
			return;
		}

		try {
			PattoSil renewed = pattoSilHome.hasBeenSigned(selectedPattoSIl, mapTipoAccettazioneProvider());
			log.info("patto ACCETTATO per id_princ: " + session.getPrincipalId() + " prot: " + renewed.getNumProtocollo());
			
			AccettazionePattoTypeTipoAccettazione tipoAccettazioneSil = mapTipoAccettazioneSil(renewed);
			it.eng.sil.coop.webservices.pattoonlinenew.EsitoType ret = pattoOnlineClientV2EJB.inviaPattoFirmato(
					renewed.getIdPattoSil(), utente.getCodServiziAmministrativi(), tipoAccettazioneSil);
			
			if (ret.getEsito().equals(EsitoTypeEsito.OK)) {
				RequestContext.getCurrentInstance().execute("codiceOTPSuccess()");
			} else {
				String toUser = ret.getDescrizione();

				RequestContext.getCurrentInstance()
						.execute("showErrMessage('" + StringEscapeUtils.escapeEcmaScript(toUser) + "')");
			}
			// remote command per refresh lista
			RequestContext.getCurrentInstance().execute("refreshRC()");
		} catch (Exception e) {
			log.error("Errore trasmissione patto firmato: " + e.getMessage());
			String params = textSingletonEjb.get("cittadino_patto_online_err").replaceAll("\\?", "");
			RequestContext.getCurrentInstance()
					.execute("showErrMessage('" + StringEscapeUtils.escapeEcmaScript(params) + "')");
		}
		clearOTP();
	}

	public void requestBlackHole(PattoSil patto) {
		RequestContext.getCurrentInstance().addCallbackParam("scadenza", this.getRemainingTime(patto));
	}

	public void clearOTP() {
		selectedPattoSIl = null;
		reinsertedOTP = "";
		currentOtpStep = "INITIAL";
	}

	private TipoAccettazioneEnum mapTipoAccettazioneProvider() {
		if (usedChannel != null) {
			switch (usedChannel) {
			case PUSH:
				return it.eng.myportal.entity.TipoAccettazioneEnum.OTP;
			case SMS:
				return it.eng.myportal.entity.TipoAccettazioneEnum.SMS;
			default:
				log.error("GRAVE: TipoAccettazioneEnum non previsto: " + usedChannel);
				break;
			}
		}

		for (PfIdentityProvider provider : strongid) {
			switch (provider.getCodTipoProvider()) {
			case FEDERA:
				return TipoAccettazioneEnum.SPID;
			case SPID:
				return TipoAccettazioneEnum.SPID;
			case ICAR:
				return TipoAccettazioneEnum.SPID;
			default:
				log.error("GRAVE: provider non previsto: " + provider.getCodTipoProvider());
			}
		}
		log.error("GRAVE: mapPfProvider() non ha trovato alcun provider");
		return null;
	}

	/**
	 * questa transcodifica dipende mapTipoAccettazioneProvider()
	 * statico per chiarire che non dipende da alcuno stato, potrebbe essere spostato?
	 * 
	 * @param renewed
	 * @return
	 */
	private static AccettazionePattoTypeTipoAccettazione mapTipoAccettazioneSil(PattoSil renewed) {

		if (renewed.getTipoAccettazione() == null) {
			log.error("GRAVE: TipoAccettazioneEnum NULLO");
			return null;
		}
		switch (renewed.getTipoAccettazione()) {// questi sono 1 a 1
		case CIE:
			return AccettazionePattoTypeTipoAccettazione.CIE;
		case OTP:
			return AccettazionePattoTypeTipoAccettazione.OTP;
		case RV:
			return AccettazionePattoTypeTipoAccettazione.RV;
		case SMS:
			return AccettazionePattoTypeTipoAccettazione.SMS;
		case SPID:
			return AccettazionePattoTypeTipoAccettazione.SPID;
		default:
			log.error("GRAVE: TipoAccettazioneEnum sconosciuto: " + renewed.getTipoAccettazione());
			return null;
		}

	}

	public void downloadPatto(PattoSil in) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset();
		ec.setResponseContentType("application/pdf");
		ec.setResponseContentLength(in.getPdfPatto().length);
		ec.setResponseHeader("Content-Disposition",
				"attachment; filename=\"" + in.getNumAnnoProtocollo() + "-" + in.getNumProtocollo() + ".pdf\"");

		try {
			OutputStream output = ec.getResponseOutputStream();
			output.write(in.getPdfPatto());
		} catch (IOException e) {
			log.error("Download del patto fallito: " + e.getMessage());
		}

		pattoSilHome.hasBeenSeen(in);
		fc.responseComplete();
	}

	public String getDisabledMessage(PattoSil patto) {
		pattoSilHome.checkScadenza(patto);
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (patto.getCodStatoAccettazione().equals(StatoAccettazionePattoEnum.A) && patto.getTsAccettazione() != null) {

			return "Accettato e firmato elettronicamente: " + df.format(patto.getTsAccettazione());
		} else if (patto.getCodStatoAccettazione().equals(StatoAccettazionePattoEnum.S)) {

			return "I termini per l'accettazione di questo patto/accordo sono scaduti in data: "
					+ df.format(patto.getDataMaxAccettazione());
		}
		if (patto.getFlgPresaVisione() == null || !patto.getFlgPresaVisione())
			return "E’ necessario visionare il documento prima dell’accettazione";

		return "";
	}

	public long getRemainingTime(PattoSil patto) {
		Date now = new Date();
		Calendar maxT = Calendar.getInstance();
		maxT.setTime(patto.getDataMaxAccettazione());

		if (!maxT.getTime().after(now))
			return 0;// tempo scaduto caz tua

		return (maxT.getTime().getTime());
	}

	public String getStatoPatto(PattoSil pattoSil) {
		return "Stato del patto/accordo: " + pattoSil.getCodStatoAccettazione().getDescrizione();
	}

	public boolean isRenderedOTPCheck() {
		return isRenderedOTP;
	}

	public boolean isRenderedSimpleCheck() {
		return isRenderedEasyCheck;
	}

	public boolean isRenderedSMSCheck() {
		return isRenderedSMS;
	}

	public List<PattoSil> getPattiCittadino() {
		return pattiCittadino;
	}

	public void setPattiCittadino(List<PattoSil> pattiCittadino) {
		this.pattiCittadino = pattiCittadino;
	}

	public String getReinsertedOTP() {
		return reinsertedOTP;
	}

	public void setReinsertedOTP(String reinsertedOTP) {
		this.reinsertedOTP = reinsertedOTP;
	}

	public PattoSil getSelectedPattoSIl() {
		return selectedPattoSIl;
	}

	public void setSelectedPattoSIl(PattoSil selectedPattoSIl) {
		this.selectedPattoSIl = selectedPattoSIl;
		storicoList = pattoSilStoricoHome.findByIdPatto(selectedPattoSIl.getIdPattoSil());
	}

	public String getCurrentOtpStep() {
		return currentOtpStep;
	}

	public void setCurrentOtpStep(String currentOtpStep) {
		this.currentOtpStep = currentOtpStep;
	}

	public String getTitoloStorico() {
		return titoloStorico;
	}

	public void setTitoloStorico(String titoloStorico) {
		this.titoloStorico = titoloStorico;
	}

	public List<PattoSilStorico> getStoricoList() {
		return storicoList;
	}

	public List<PattoSilStorico> pattoStorico(int idPatto) {
		storicoList = pattoSilStoricoHome.findByIdPatto(idPatto);
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		for (PattoSilStorico list : storicoList) {
			if (list.getTsAccettazione() != null)
				setTitoloStorico("Patto/Accordo del " + df.format(list.getTsAccettazione()));
			else
				setTitoloStorico("Patto/Accordo");
		}
		return storicoList;
	}

	public boolean checkButton(int idPatto) {
		storicoList = pattoSilStoricoHome.findByIdPatto(idPatto);
		if (storicoList.isEmpty())
			isRenderedButtonStorico = false;
		else
			isRenderedButtonStorico = true;
		return isRenderedButtonStorico;
	}

}