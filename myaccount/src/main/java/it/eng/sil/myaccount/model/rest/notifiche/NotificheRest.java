package it.eng.sil.myaccount.model.rest.notifiche;

import it.eng.sil.myaccount.model.ejb.stateless.myportal.NotificationBuilder;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.entity.myportal.MsgMessaggio;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless
@Path("notifica")
public class NotificheRest {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalHome;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	ConstantsSingleton constantsSingleton;

	protected static Log log = LogFactory.getLog(NotificheRest.class);

	private final static String OK = "ok";
	private final static String KO = "ko";
	private final static String STATUS = "status";
	private final static String ERROR = "error";

	@GET
	@Path("/addAnnullaFirma/{tokenSecurity}")
	@Produces("application/json")
	public String addNotificaAnnullamentoFirma(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("obj") String strOggetto) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".addAnnullaFirma");
		try {
			String completeUrl = constantsSingleton.getMyAccountURL() + "/rest/notifica/addAnnullaFirma/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurity(tokenSecurity, completeUrl)) {
					// Date now = new Date();
					JSONObject obj = new JSONObject();
					String strIdPfPrincipal = (Utils.getUserFromTokenUtente(token)).trim();
					log.debug("notifica annulla firma per utente id=" + strIdPfPrincipal);
					PfPrincipal pfPrincipal = pfPrincipalHome.findById(new Integer(strIdPfPrincipal));
					if (pfPrincipal != null) {
						Set<MsgMessaggio> messaggi = notificationBuilder.buildNotificationAnnullamentoFirmaMyStage(
								pfPrincipal, strOggetto);
						notificationBuilder.sendNotification(messaggi);

						log.debug("notifica annulla firma per utente  id=" + strIdPfPrincipal + " OK");

						obj.put(STATUS, OK);
						obj.put("DESC", "notifica inviata correttamente");
					} else {

						log.debug("notifica annulla firma per utente  id=" + strIdPfPrincipal + " KO");

						obj.put(STATUS, KO);
						obj.put(ERROR, "parametri errati");
					}
					return obj.toString();
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} catch (Exception e) {
			log.error("Errore interno" + e);
			JSONObject obj = new JSONObject();
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e.getMessage());
			}
			return obj.toString();
		} finally {
			jamonMonitor.stop();
		}
	}

}
