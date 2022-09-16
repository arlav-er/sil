package it.eng.myportal.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.ejb.stateless.messaggi.MsgMessaggioEJB;
import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.MsgMessaggio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyMessaggioModel extends LazyDataModel<MsgMessaggio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8710504780896373553L;
	protected final Log log = LogFactory.getLog(this.getClass());

	private MsgMessaggioEJB msgMessaggioEJB;

	private Integer count;

	public final static int FILTER_SUPPORT = 1;
	public final static int FILTER_CONSULTING = 2;
	public final static int FILTER_NOTIFICATION_RECEIVED = 3;
	public final static int FILTER_NOTIFICATION_SENT = 4;
	public final static int FILTER_CONTACT_RECEIVED = 5;
	public final static int FILTER_CONTACT_SENT = 6;

	// params
	private int currentFilter;
	private Integer pfPrincipalId;

	public LazyMessaggioModel(int currentFilter, Integer pfPrincipalId) {
		this.currentFilter = currentFilter;
		this.pfPrincipalId = pfPrincipalId;
		injectEjb();
	}

	private void injectEjb() {
		try {
			InitialContext ic = new InitialContext();
			msgMessaggioEJB = (MsgMessaggioEJB) ic.lookup("java:app/MyPortal/MsgMessaggioEJB");
		} catch (NamingException e) {
			log.error("Lookup Failed");
			e.printStackTrace();
		}
	}

	@Override
	public List<MsgMessaggio> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<MsgMessaggio> data = new ArrayList<MsgMessaggio>();
		if (currentFilter == 0) {
			this.setRowCount(0);
			return data;
		}
		// data
		if (currentFilter == FILTER_SUPPORT) {
			data = msgMessaggioEJB.findSupportMessages(pfPrincipalId, first, pageSize);
		} else if (currentFilter == FILTER_CONSULTING) {
			data = msgMessaggioEJB.findConsultingMessages(pfPrincipalId, first, pageSize);
		} else if (currentFilter == FILTER_NOTIFICATION_RECEIVED) {
			data = msgMessaggioEJB.findNotificheRicevute(pfPrincipalId, first, pageSize);
		} else if (currentFilter == FILTER_NOTIFICATION_SENT) {
			data = msgMessaggioEJB.findNotificheInviate(pfPrincipalId, first, pageSize);
		} else if (currentFilter == FILTER_CONTACT_RECEIVED) {
			data = msgMessaggioEJB.findContactReceivedMessages(pfPrincipalId, first, pageSize);
		} else if (currentFilter == FILTER_CONTACT_SENT) {
			data = msgMessaggioEJB.findContactSentMessages(pfPrincipalId, first, pageSize);
		}

		// rowCount
		if (count == null) {
			if (currentFilter == FILTER_SUPPORT) {
				count = msgMessaggioEJB.findSupportMessagesCount(pfPrincipalId).intValue();
			} else if (currentFilter == FILTER_CONSULTING) {
				count = msgMessaggioEJB.findConsultingMessagesCount(pfPrincipalId).intValue();
			} else if (currentFilter == FILTER_NOTIFICATION_RECEIVED) {
				count = msgMessaggioEJB.findNotificheRicevuteCount(pfPrincipalId).intValue();
			} else if (currentFilter == FILTER_NOTIFICATION_SENT) {
				count = msgMessaggioEJB.findNotificheInviateCount(pfPrincipalId).intValue();
			} else if (currentFilter == FILTER_CONTACT_RECEIVED) {
				count = msgMessaggioEJB.findContactReceivedMessagesCount(pfPrincipalId).intValue();
			} else if (currentFilter == FILTER_CONTACT_SENT) {
				count = msgMessaggioEJB.findContactSentMessagesCount(pfPrincipalId).intValue();
			}
			this.setRowCount(count);
		}
		return data;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public int getCurrentFilter() {
		return currentFilter;
	}

	public void setCurrentFilter(int currentFilter) {
		this.currentFilter = currentFilter;
	}

}
