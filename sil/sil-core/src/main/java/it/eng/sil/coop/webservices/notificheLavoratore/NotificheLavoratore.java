/*
 * Creato il 13-Apr-04
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.webservices.notificheLavoratore;

import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.NotificaLavoratoreSILMessage;
import it.eng.sil.coop.queues.InQ;
import it.eng.sil.util.InfoProvinciaSingleton;

/**
 * @author rolfini
 * 
 */
public class NotificheLavoratore {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(NotificheLavoratore.class.getName());

	public String putNotifica(String codiceFiscale, String messaggio, String mittente) {

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			InQ inQ = new InQ();

			InfoProvinciaSingleton infoProvincia = InfoProvinciaSingleton.getInstance();

			TestataMessageTO testataMessaggio = new TestataMessageTO();

			testataMessaggio.setPoloMittente(mittente);

			NotificaLavoratoreSILMessage notificaLavoratoreSILMessage = new NotificaLavoratoreSILMessage();
			notificaLavoratoreSILMessage.setTestata(testataMessaggio);
			notificaLavoratoreSILMessage.setDataSourceJndi(dataSourceJndiName);

			notificaLavoratoreSILMessage.setCodiceFiscale(codiceFiscale);
			notificaLavoratoreSILMessage.setContenutoMessaggio(messaggio);

			notificaLavoratoreSILMessage.send(inQ);

		} catch (Exception exc) {
			// System.out.println("NotificheLavoratore::putNotifica errore
			// nell'inserimento nella coda INQ. ", exc);
			// exc.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"NotificheLavoratore:putNotifica errore nell'inserimento nella coda INQ. ", exc);

		}
		return "0";
	}
}