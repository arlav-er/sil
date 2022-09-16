package it.eng.myportal.beans.azienda.messaging;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.ejb.stateless.messaggi.MsgMessaggioEJB;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeTemaConsulenza;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.helpers.LazyMessaggioModel;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "aziendaMessagingBean")
@ViewScoped
public class AziendaMessagingBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(AziendaMessagingBean.class);

	private String currentSection;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	MsgMessaggioEJB msgMessaggioEJB;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	private List<CvDatiPersonali> cvDatiPersonaliList;
	private List<CvLetteraAcc> cvLetteraAccList;
	private List<DeTemaConsulenza> deTemaConsulenzaList;

	private Integer pfPrincipalId;

	private MsgMessaggio message;
	private LazyDataModel<MsgMessaggio> messagesList;
	private String reply;

	private List<MsgMessaggio> ticketMessages;

	private boolean enableReply;

	public final String typeSupport = "SUPP";
	public final String typeConsulting = "ESP";

	private String linkRiferimentoMessaggio;
	private String testoLinkRiferimentoMessaggio;
	private Integer idVaDatiVacancy;
	private Integer idCvDatiPersonali;

	@Override
	@PostConstruct
	public void postConstruct() {
		pfPrincipalId = getSession().getPrincipalId();
		messagesList = new LazyMessaggioModel(0, pfPrincipalId);
		ticketMessages = new ArrayList<MsgMessaggio>();
		message = new MsgMessaggio();		
	}

	public String getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}

	public List<CvDatiPersonali> getCvDatiPersonaliList() {
		return cvDatiPersonaliList;
	}

	public void setCvDatiPersonaliList(List<CvDatiPersonali> cvDatiPersonaliList) {
		this.cvDatiPersonaliList = cvDatiPersonaliList;
	}

	public List<CvLetteraAcc> getCvLetteraAccList() {
		return cvLetteraAccList;
	}

	public void setCvLetteraAccList(List<CvLetteraAcc> cvLetteraAccList) {
		this.cvLetteraAccList = cvLetteraAccList;
	}

	public List<DeTemaConsulenza> getDeTemaConsulenzaList() {
		return deTemaConsulenzaList;
	}

	public void setDeTemaConsulenzaList(List<DeTemaConsulenza> deTemaConsulenzaList) {
		this.deTemaConsulenzaList = deTemaConsulenzaList;
	}

	public LazyDataModel<MsgMessaggio> getMessagesList() {
		return messagesList;
	}

	public void setMessagesList(LazyDataModel<MsgMessaggio> messagesList) {
		this.messagesList = messagesList;
	}

	/**
	 * Main navigation - xhtml path passed from here
	 * */
	public void updateSection() {
		currentSection = getRequestParameter("currentSection");
		// ensuring the next section won't hold the previous data
		// till the remoteCommand execute for new ones
		// this helped 6.0 sec less load time
		messagesList = null;
	}

	public MsgMessaggio getMessage() {
		return message;
	}

	public void setMessage(MsgMessaggio message) {
		this.message = message;
	}

	public List<MsgMessaggio> getTicketMessages() {
		return ticketMessages;
	}

	public void setTicketMessages(List<MsgMessaggio> ticketMessages) {
		this.ticketMessages = ticketMessages;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public boolean isEnableReply() {
		return enableReply;
	}

	public void setEnableReply(boolean enableReply) {
		this.enableReply = enableReply;
	}

	public String getTypeConsulting() {
		return typeConsulting;
	}

	public void initConsulting() {
		log.info("initConsulting called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONSULTING, pfPrincipalId);
	}

	public void initNotifiche() {
		log.info("initNotifiche called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_NOTIFICATION_RECEIVED, pfPrincipalId);
	}

	public void initContactReceived() {
		log.info("initContactReceived called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONTACT_RECEIVED, pfPrincipalId);
	}

	public void initContactSent() {
		log.info("initContactSent called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONTACT_SENT, pfPrincipalId);
	}

	public void initRequestConsulting() {
		log.info("initRequestConsulting called: loading tema :D ");
		// TODO deTemaConsulenzaList = find lista
	}

	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		log.info("Photo uploaded: " + file.getFileName());

	}

	public void sendRichiesta(String tipoMessaggio) {
		log.info("sendRichiesta called");
		if (ConstantsSingleton.MsgMessaggio.ESPERTO.equalsIgnoreCase(tipoMessaggio)) {
			msgMessaggioEJB.saveMsgConsulenza(message, pfPrincipalId);
		}
	}

	public void sendReply(String tipoMessaggio) {
		log.info("sendReply called");
		if (ConstantsSingleton.MsgMessaggio.ESPERTO.equalsIgnoreCase(tipoMessaggio)) {
			if (ticketMessages.size() > 0) {
				MsgMessaggio message = ticketMessages.get(ticketMessages.size() - 1);
				msgMessaggioEJB.replyMsgConsulenza(message, reply, pfPrincipalId);
			}
		} else if (ConstantsSingleton.MsgMessaggio.SUPPORTO.equalsIgnoreCase(tipoMessaggio)) {
			if (ticketMessages.size() > 0) {
				MsgMessaggio message = ticketMessages.get(ticketMessages.size() - 1);
				msgMessaggioEJB.replyMsgSupporto(message, reply, pfPrincipalId);
			}
		}
	}

	public void loadMessage() {
		try {
			enableReply = false;
			ticketMessages = msgMessaggioEJB.findMessaggiByTicket(getRequestParameter("ticketId"), pfPrincipalId);

			if (ticketMessages.size() > 0) {
				// recupero l'ultimo messaggio
				MsgMessaggio msg = ticketMessages.get(ticketMessages.size() - 1);
				PfPrincipal princTo = msg.getPfPrincipalTo();
				if (princTo != null) {
					Integer idPfPrincipalTo = princTo.getIdPfPrincipal();
					if (idPfPrincipalTo.compareTo(pfPrincipalId) == 0) {
						enableReply = true;
						RequestContext.getCurrentInstance().addCallbackParam("enableReply", enableReply);
					}
				}
			}
		} catch (Exception ex) {
			log.error("can't find message by ticket: " + ex.getMessage());
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}

		if (ticketMessages == null || ticketMessages.size() == 0 || ticketMessages.isEmpty()) {
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}
	}

	public void loadMessageContattiInviati() {
		try {
			ticketMessages = msgMessaggioEJB.findMessaggiByTicket(getRequestParameter("ticketId"), pfPrincipalId);
			if (ticketMessages.size() > 0) {
				MsgMessaggio msg = ticketMessages.get(ticketMessages.size() - 1);
				MsgContatto msgContatto = msg.getMsgContatto();

				// recupero informazioni del contatto
				AcCandidatura acCandidatura = null;
				VaDatiVacancy vaDatiVacancy = null;
				if (msgContatto.getAcCandidatura() != null) {
					acCandidatura = acCandidaturaHome.findById(msgContatto.getAcCandidatura().getIdAcCandidatura());
				}
				if (msgContatto.getVaDatiVacancy() != null) {
					Integer idVaDatiVacancyContatto = msgContatto.getVaDatiVacancy().getIdVaDatiVacancy();
					if (idVaDatiVacancyContatto != null) {
						vaDatiVacancy = vaDatiVacancyHome.findById(idVaDatiVacancyContatto);
					}
				}

				// se è interno a myportal ho la candidatura
				if (acCandidatura != null) {
					if (acCandidatura.getVaDatiVacancy() != null) {
						idVaDatiVacancy = acCandidatura.getVaDatiVacancy().getIdVaDatiVacancy();
					}
					if (acCandidatura.getCvDatiPersonali() != null) {
						idCvDatiPersonali = acCandidatura.getCvDatiPersonali().getIdCvDatiPersonali();
					}
				}// altrimenti ho direttamente la vacancy
				else if (vaDatiVacancy != null) {
					idVaDatiVacancy = vaDatiVacancy.getIdVaDatiVacancy();
				}

				CvDatiPersonali cvDatiPersonali = msgContatto.getCvDatiPersonali();
				if (cvDatiPersonali != null) {
					idCvDatiPersonali = cvDatiPersonali.getIdCvDatiPersonali();
				}

				PfPrincipal pfPrincipal = utenteInfoHome.findById(pfPrincipalId).getPfPrincipal();
				if (pfPrincipal.isUtente()) {
					// offerta di lavoro
					linkRiferimentoMessaggio = "/" + ConstantsSingleton.CONTESTO_APP
							+ "/faces/secure/azienda/vacancies/visualizza.xhtml";
					testoLinkRiferimentoMessaggio = "Visualizza l'offerta di lavoro";
				} else {
					// cv visualizzato dall'azienda
					linkRiferimentoMessaggio = "/" + ConstantsSingleton.CONTESTO_APP
							+ "/faces/secure/utente/curriculum/view.xhtml";
					testoLinkRiferimentoMessaggio = "Visualizza il CV";
				}
			}
		} catch (Exception ex) {
			log.error("can't find message by ticket: " + ex.getMessage());
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}

		if (ticketMessages == null || ticketMessages.size() == 0 || ticketMessages.isEmpty()) {
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}
	}

	public void loadMessageContattiRicevuti() {
		try {
			ticketMessages = msgMessaggioEJB.findMessaggiByTicket(getRequestParameter("ticketId"), pfPrincipalId);
			if (ticketMessages.size() > 0) {
				MsgMessaggio msg = ticketMessages.get(ticketMessages.size() - 1);
				MsgContatto msgContatto = msg.getMsgContatto();
				// recupero informazioni del contatto
				AcCandidatura acCandidatura = null;
				CvDatiPersonali cvDatiPersonali = null;
				if (msgContatto.getAcCandidatura() != null) {
					acCandidatura = acCandidaturaHome.findById(msgContatto.getAcCandidatura().getIdAcCandidatura());
				}
				if (msgContatto.getCvDatiPersonali() != null) {
					Integer idCvDatiPersonaliContatto = msgContatto.getCvDatiPersonali().getIdCvDatiPersonali();
					if (idCvDatiPersonaliContatto != null) {
						cvDatiPersonali = cvDatiPersonaliHome.findById(idCvDatiPersonaliContatto);
					}
				}

				// se è interno a myportal ho la candidatura
				if (acCandidatura != null) {
					if (acCandidatura.getVaDatiVacancy() != null) {
						idVaDatiVacancy = acCandidatura.getVaDatiVacancy().getIdVaDatiVacancy();
					}
					if (acCandidatura.getCvDatiPersonali() != null) {
						idCvDatiPersonali = acCandidatura.getCvDatiPersonali().getIdCvDatiPersonali();
					}
				}// altrimenti ho direttamente la vacancy
				else if (cvDatiPersonali != null) {
					idCvDatiPersonali = cvDatiPersonali.getIdCvDatiPersonali();
				}

				PfPrincipal pfPrincipal = utenteInfoHome.findById(pfPrincipalId).getPfPrincipal();

				if (pfPrincipal.isUtente()) {
					// offerta di lavoro
					linkRiferimentoMessaggio = "/" + ConstantsSingleton.CONTESTO_APP
							+ "/faces/secure/utente/curriculum/view.xhtml";
					testoLinkRiferimentoMessaggio = "Visualizza il CV a cui l'azienda è interessata";
				} else {
					// cv visualizzato dall'azienda
					linkRiferimentoMessaggio = "/" + ConstantsSingleton.CONTESTO_APP
							+ "/faces/secure/utente/curriculum/view.xhtml";
					testoLinkRiferimentoMessaggio = "Visualizza il CV";
				}
			}
		} catch (Exception ex) {
			log.error("can't find message by ticket: " + ex.getMessage());
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}

		if (ticketMessages == null || ticketMessages.size() == 0 || ticketMessages.isEmpty()) {
			RequestContext.getCurrentInstance().addCallbackParam("notFound", true);
		}
	}

	public String shortBody(String originalBody) {

		if (originalBody == null || originalBody.trim().isEmpty()) {
			return originalBody;
		}

		StringBuffer sb = new StringBuffer(originalBody);
		int actualLength = 40 - 3;
		if (sb.length() > actualLength) {

			int endIndex = sb.indexOf(" ", actualLength);
			return StringEscapeUtils.escapeHtml4(sb.insert(endIndex, "...").substring(0, endIndex + 3));

		}
		return originalBody;
	}

	public String getTestoLinkRiferimentoMessaggio() {
		return testoLinkRiferimentoMessaggio;
	}

	public void setTestoLinkRiferimentoMessaggio(String testoLinkRiferimentoMessaggio) {
		this.testoLinkRiferimentoMessaggio = testoLinkRiferimentoMessaggio;
	}

	public String getLinkRiferimentoMessaggio() {
		return linkRiferimentoMessaggio;
	}

	public void setLinkRiferimentoMessaggio(String linkRiferimentoMessaggio) {
		this.linkRiferimentoMessaggio = linkRiferimentoMessaggio;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}
}
