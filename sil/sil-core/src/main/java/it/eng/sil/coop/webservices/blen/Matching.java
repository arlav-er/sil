package it.eng.sil.coop.webservices.blen;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.bean.blen.Match;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.InQ;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;

public class Matching {

	private static Logger logger = Logger.getLogger(Matching.class);

	public String addMatching(Match match) {
		String risultato = null;
		String serviceName = "MatchingBlen";
		Map param = new HashMap();

		param.put("servizio", "MatchingBlen");
		param.put("match", match);

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		InQ inQ = new InQ();
		IFaceMessage message = null;
		try {
			String className = "it.eng.sil.coop.messages.jmsmessages." + serviceName + "Message";
			message = (IFaceMessage) Class.forName(className).newInstance();
			message.setDataSourceJndi(dataSourceJndiName);

			message.setServiceParameters(param);

			message.send(inQ);

			RispostaXML risposta = new RispostaXML("101", "Ok", "I");
			risultato = risposta.toXMLString();
		} catch (ClassNotFoundException cnfe) {
			logger.error("MessageReceiver:receive Classe del messaggio " + serviceName + "Message non trovata!!  ",
					cnfe);
			cnfe.printStackTrace();
			RispostaXML risposta = new RispostaXML("999",
					"Impossibile elaborare la richiesta. Motivo:" + cnfe.getMessage(), "E");
			risultato = risposta.toXMLString();
		} catch (Exception e) {
			// classe non trovata
			logger.error("Errore nell'esecuzione della classe del messaggio " + serviceName + "\n", e);
			e.printStackTrace();
			RispostaXML risposta = new RispostaXML("999",
					"Impossibile elaborare la richiesta. Motivo:" + e.getMessage(), "E");
			risultato = risposta.toXMLString();
		}

		return risultato;
	}
}