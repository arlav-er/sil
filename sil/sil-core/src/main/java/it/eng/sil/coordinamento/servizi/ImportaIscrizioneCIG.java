package it.eng.sil.coordinamento.servizi;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.queues.InQ;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;

/**
 * @author Esposito
 */

public class ImportaIscrizioneCIG implements ServizioSoap {

	private static Logger logger = Logger.getLogger(ImportaIscrizioneCIG.class);

	public String elabora(Execute parametri) {
		String risultato = null;
		TransactionQueryExecutor tex = null;
		String serviceName = "RegistraIscrizioneCIG";
		String iscrizioneCIGXML = parametri.getDati();
		Map param = new HashMap();

		param.put("servizio", "RegistraIscrizioneCIG");
		param.put("dati_xml", iscrizioneCIGXML);

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
			logger.error("Errore nell'esecuzione della classe del messaggio " + serviceName + "\n" + iscrizioneCIGXML,
					e);
			e.printStackTrace();
			RispostaXML risposta = new RispostaXML("999",
					"Impossibile elaborare la richiesta. Motivo:" + e.getMessage(), "E");
			risultato = risposta.toXMLString();
		}

		return risultato;
	}
}
