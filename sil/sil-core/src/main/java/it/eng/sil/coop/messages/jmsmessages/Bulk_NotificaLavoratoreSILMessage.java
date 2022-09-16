/*
 * Created on 11-May-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages.jmsmessages;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.queues.IFaceQueue;

/**
 * @author rolfini
 *
 *         questo tipo di messaggio NON implementa IFaceMessage perché non corrisponde ad un servizio dedicato esposto
 *         dal SIL, ma si appoggia al servizio NotificaLavoratore, del quale estende le classi.
 *
 */
public class Bulk_NotificaLavoratoreSILMessage extends AbstractNotificaLavoratoreSILMessage {

	private static final Logger log = Logger.getLogger(Bulk_NotificaLavoratoreSILMessage.class.getName());

	public void send(IFaceQueue Q) {

		// prelevo le province
		EndPointSingleton endPointSingleton = EndPointSingleton.getInstance(dataSourceJndi);
		HashSet province = (HashSet) endPointSingleton.getProvinceInCoop();

		// per ogni provincia in cooperazione con il mio sistema
		// invio la notifica impostata
		for (Iterator i = province.iterator(); i.hasNext();) {
			String destinazione = (String) i.next(); // leggo la destinazione

			if (!(destinazione.equals(testata.getPoloMittente()))) { // se la destinazione non è il mio polo

				log.debug("Destinazione selezionata: " + destinazione);

				testata.setDestinazione(destinazione); // imposto la destinazione nella testata;
														// in questo modo il messaggio avrà *questa*
														// destinazione
				try {
					super.send(Q); // mando il messaggio in coda
					log.info("Messaggio verso la destinazione " + destinazione + " inserito con successo in coda");
				} catch (Exception ex) {
					log.fatal("Eccezione nell'invio del messaggio verso la provincia: " + destinazione + "\nCausa: "
							+ ex.getMessage());
					ex.printStackTrace();
				}

			}
		}

	}

}
