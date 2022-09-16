package it.eng.sil.coop.messages.jmsmessages;

import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.bean.blen.Match;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

public class MatchingBlenMessage extends AbstractMessage implements IFaceMessage {

	private Match matchMessage;
	private final int maxRedeliveries = 10;
	private static final Logger log = Logger.getLogger(MatchingBlenMessage.class.getName());

	public void callWebservice() throws Exception {
		// TODO Auto-generated method stub

	}

	public void setObjectMessage(ObjectMessage msg) throws Exception {
		try {

			testata.setServizio("MatchingBlen");

			setMatchMessage((Match) msg.getObjectProperty("match"));

			testata.setMaxRedeliveries(maxRedeliveries);

		} catch (Exception ex) {
			log.fatal("Errore nella lettura dei parametri.\nMessaggio malformato ", ex);
			ex.printStackTrace();
			throw new Exception("Messaggio malformato");
		}
	}

	public void setServiceParameters(Map param) {
		ServiceParameters serviceParam = new ServiceParameters(param);

		testata.setServizio("MatchingBlen");

		setMatchMessage((Match) serviceParam.get("match"));

	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList<Match> appParam = new ArrayList<Match>();
		appParam.add(getMatchMessage());
		super.send(Q, appParam);
	}

	private Match getMatchMessage() {
		return matchMessage;
	}

	private void setMatchMessage(Match matchMessage) {
		this.matchMessage = matchMessage;
	}

}
