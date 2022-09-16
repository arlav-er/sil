/*
 * Creato il 12-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

package it.eng.sil.sms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.sil.sms.gateways.GatewayFactory;
import it.eng.sil.sms.gateways.IGateway;

public class Gateway {
	List smsList = null;

	public Gateway() {
		smsList = new ArrayList();

	}

	public void addSms(Sms sms) {
		smsList.add(sms);
	}

	public void send() throws SmsException {

		GatewayFactory gf = new GatewayFactory();
		IGateway g = gf.getGateway();

		if (!g.isReachable())
			throw new SmsException("Server unreachable!");
		g.setSmsList(smsList);
		g.send();

		smsList.clear();

	}

	public void send(DataConnection dataConnection) throws Exception {

		GatewayFactory gf = new GatewayFactory(dataConnection);
		IGateway g = gf.getGateway();

		if (!g.isReachable())
			throw new SmsException("Server unreachable!");
		g.setSmsList(smsList);
		g.send();

		smsList.clear();
	}

	public String toString() {

		String retVal = "";
		Iterator iter = smsList.iterator();

		while (iter.hasNext()) {
			Sms sms = (Sms) iter.next();
			retVal += sms.toString() + "\r\n";

		}
		return retVal;

	}

	// /// Da togliere a regime
	public static void main(String[] args) {

		Gateway gateway = new Gateway();
		try {
			Sms sms = new Sms("2925444", "Primo messaggio", 160);
			gateway.addSms(sms);

			gateway.send();

		} catch (SmsFormatException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		} catch (SmsException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}

	}

}
