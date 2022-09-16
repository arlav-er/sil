package it.eng.myportal.beans.prov;

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
import org.primefaces.model.LazyDataModel;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.ejb.stateless.messaggi.MsgMessaggioEJB;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.helpers.LazyMessaggioModel;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "provMessagingBean")
@ViewScoped
public class ProvMessagingBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(ProvMessagingBean.class);

	private String currentSection;

	@EJB
	ProvinciaHome provinciaHome;

	@EJB
	MsgMessaggioEJB msgMessaggioEJB;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	private Integer pfPrincipalId;

	private MsgMessaggio message;
	private LazyDataModel<MsgMessaggio> messagesList;
	private String reply;

	private List<MsgMessaggio> ticketMessages;

	private boolean enableReply;

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

	public LazyDataModel<MsgMessaggio> getMessagesList() {
		return messagesList;
	}

	public void setMessagesList(LazyDataModel<MsgMessaggio> messagesList) {
		this.messagesList = messagesList;
	}

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

	public void initSupport() {
		log.info("initSupport called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_SUPPORT, pfPrincipalId);
	}

	public void initConsulting() {
		log.info("initConsulting called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONSULTING, pfPrincipalId);
	}

	public void initNotificheReceived() {
		log.info("initNotifiche called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_NOTIFICATION_RECEIVED, pfPrincipalId);
	}

	public void initNotificheSent() {
		log.info("initNotifiche called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_NOTIFICATION_SENT, pfPrincipalId);
	}

	public void initSendNotification() {
		log.info("initSendNotification called");
	}

	public void initContactReceived() {
		log.info("initContactReceived called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONTACT_RECEIVED, pfPrincipalId);
	}

	public void initContactSent() {
		log.info("initContactSent called");
		messagesList = new LazyMessaggioModel(LazyMessaggioModel.FILTER_CONTACT_SENT, pfPrincipalId);
	}

	public void sendNotification() {
		log.info("sendNotification called");
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
			log.error("Errore durante la ricerca del messaggio mediante ticket in loadMessage(): " + ex.getMessage());
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
}
