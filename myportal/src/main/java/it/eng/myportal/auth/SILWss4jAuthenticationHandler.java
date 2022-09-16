package it.eng.myportal.auth;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSPasswordCallback;

import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Axis Handler per settare la password dei servizi MyPortal to SIL che utilizzano la libreria WSS4J
 * 
 * @author coticone
 * 
 *         MANCA IL NAMESPACE
 *
 */
public class SILWss4jAuthenticationHandler implements CallbackHandler {

	protected final Log log = LogFactory.getLog(SILWss4jAuthenticationHandler.class);

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof WSPasswordCallback) {
				WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
				log.info("WSS4J: Utente: " + pc.getIdentifier() + ", tipo autenticazione: " + pc.getUsage());

				MessageContext msgContext = MessageContext.getCurrentContext();
				// try {
				// SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();
				// } catch (SOAPException e) {
				// throw new UnsupportedCallbackException(callbacks[i], "(500) AXIS WSS4J: SOAPBody "+e);
				// }
				String targetServiceName = msgContext.getTargetService();
				// String operazione = msgContext.getOperation().getName();

				String[] utenteWS = null;
				if ("StipulaDid".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.DID);
				} else if ("SanaMovimento".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.SANA_REDDITI);
				} else if ("GetStatoOccupazionale".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.STATO_OCCPUAZIONALE);
				} else if ("GetPercorsoLavoratore".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.PERCORSO_LAVORATORE);
				} else if ("GetUltimoMovimento".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.ELENCO_MOVIMENTI);
				} else if ("GetConfermaPeriodica".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.CONFERMA_PERIODICA);
				} else if ("AppuntamentoService".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.APPUNTAMENTO);
				} else if ("RinnovaPatto".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.RINNOVO_PATTO);
				} else if ("VerificaRequisitiGaranziaOver".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.CHECK_GARANZIA_OVER);
				} else if ("AttivaVoucher".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.ATTIVA_VOUCHER);
				} else if ("EntiAccreditatiPort".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.ENTI_ACCREDITATI);
				} else if ("GetAdesioneReimpiego".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.ADESIONE_REIMPIEGO);
				} else if ("TrasmettiPatto".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.SIL_PATTO_FIRMATO_CLI);
				} else if ("GetProtocolloServiceSoap".equals(targetServiceName)) {
					utenteWS = getMittente(TipoServizio.SINTESI_PROTO_PUGLIA);
				} else {
					log.error("ERRORE GRAVE: servizio sconosciuto: " + targetServiceName);
				}

				if (utenteWS == null) {
					throw new IOException("(500) AXIS WSS4J: Utente non trovato per " + targetServiceName);
				}

				String user = utenteWS[0];
				String pwd = utenteWS[1];

				if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
					if (!user.equals(pc.getIdentifier())) {
						throw new IOException("(500) AXIS WSS4J: Utente non corretto: " + pc.getIdentifier());
					}

					pc.setPassword(pwd);
				} else if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN_UNKNOWN) {
					if (!user.equals(pc.getIdentifier())) {
						throw new IOException("(500) AXIS WSS4J: Utente non corretto: " + pc.getIdentifier());
					}

					if (!pwd.equals(pc.getPassword())) {
						throw new IOException("(500) AXIS WSS4J:  Password errata per l'utente: " + pc.getIdentifier());
					}
				}
			} else {
				throw new UnsupportedCallbackException(callbacks[i], "(500) AXIS WSS4J:  Callback NON SOPPORTATO");
			}
		}
	}

	private String[] getMittente(TipoServizio tipo) throws AxisFault {

		String[] mitt = new String[2];

		InitialContext ic;
		try {
			ic = new InitialContext();
			WsEndpointHome home = (WsEndpointHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "WsEndpointHome");
			mitt = home.getWebServiceUser(tipo);
		} catch (Exception e) {
			log.error("recupero endpoint per wss4j " + e);
		}

		return mitt;
	}

}
