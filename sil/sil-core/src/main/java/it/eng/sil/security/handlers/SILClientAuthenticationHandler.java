/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package org.apache.axis.handlers ;

package it.eng.sil.security.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

public class SILClientAuthenticationHandler extends BasicHandler {

	/**
	 * Authenticate the user and password from the msgContext
	 */

	private static final Logger log = Logger.getLogger(SILClientAuthenticationHandler.class.getName());
	// private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public void invoke(MessageContext msgContext) throws AxisFault {

		try {
			Properties props = Utility.loadProperties();

			log.debug("SILClientAuthenticationHandler: richiesta da autenticare");
			SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();

			// ricerca operazione
			String operazione = Utility.recuperaOperazione(msgContext);
			String url = (String) msgContext.getProperty("transport.url");

			// Tutte le operaiozni sono con gli handler
			// tranne quelle della protocollazione

			if (!Utility.isOperazioneSenzaHandler(operazione)) {

				boolean datiSensibili = Utility.isOperazioneConDatiSens(operazione);
				String[] mittente = getMittente(props, datiSensibili);

				String msg = new Date() + " Operazione:" + operazione + " url:" + url + " Utente:" + mittente[0];

				if (!"false".equalsIgnoreCase(props.getProperty("log.enabled")))
					// Utility.logInvoke(msg , props.getProperty("log.ws_out"));
					Utility.logInvoke(msg, 'O');

				SOAPElement elem = sb.addChildElement("SILAuthentication", "soapenv");

				SOAPFactory factory = SOAPFactory.newInstance();
				Name username = factory.createName("username", null, null);
				elem.addAttribute(username, mittente[0]);

				Name password = factory.createName("password", null, null);
				elem.addAttribute(password, mittente[1]);
			}

			log.debug("SILClientAuthenticationHandler: richiesta AUTORIZZATA per il servizio [" + operazione + "]");
			return;

		} catch (Exception e) {

			String msg = "SILClientAuthenticationHandler: errore " + e.getMessage();
			log.error(msg, e);
			throw new AxisFault(msg, e);

		}
	}

	/**
	 * 
	 * @return un vettore di due elementi (username, password)
	 */

	private String[] getMittente(Properties props, boolean mittDatiSens) throws AxisFault {

		String mittente = Utility.decrypt(props.getProperty("mittente"));
		log.info("Mittente: " + mittente);

		if (mittDatiSens) {
			mittente += ".ds";
		} else {
			mittente += ".df";
		}

		String username = props.getProperty(mittente + ".username");
		String password = Utility.decrypt(props.getProperty(mittente + ".password"));
		String validFrom = props.getProperty(mittente + ".validita.da");
		String validTo = props.getProperty(mittente + ".validita.a");

		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date from = df.parse(validFrom);
			Date to = df.parse(validTo);
			Date oggi = new Date();

			if (oggi.before(from) || oggi.after(to)) {
				throw new AxisFault("SILClientAuthenticationHandler: account " + username + "scaduto");
			}
			df = null;
		} catch (ParseException e) {
			throw new AxisFault("SILClientAuthenticationHandler: data/e di validità non formattata/e correttamente", e);
		}

		String[] mitt = new String[2];

		mitt[0] = username;
		mitt[1] = password;

		return mitt;
	}

}