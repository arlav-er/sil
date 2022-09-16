package it.eng.myportal.rest.app;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.CvLetteraAccHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per recuperare la lista delle lettere od accompagnamento dell'utente collegato
 * 
 * @author
 *
 */
@Stateless
@Path("/nocas/app/lettere")
public class LettereUtente {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	CvLetteraAccHome cvLetteraAccHome;

	protected static Log log = LogFactory.getLog(CurriculumUtente.class);

	private final static String OK = "ok";
	private final static String KO = "ko";
	private final static String STATUS = "status";
	private final static String ERROR = "error";

	private final static String LISTALETTERE = "listaLettere";

	private final static String ID = "id";
	private final static String NOME = "nome";
	private final static String DATAMODIFICA = "dataModifica";
	private final static String BREVEPRES = "brevePresentazione";
	private final static String MOTIVAZIONE = "motivazObiettivi";
	private final static String PUNTIFORZA = "puntiForzaQualita";
	private final static String SALUTI = "saluti";
	private final static String BENEFICI = "benefici";

	@GET
	@Path("/list/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String index(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".index");
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/lettere/list/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					JSONObject obj = new JSONObject();
					String username = Utils.getUsernameFromTokenUtente(token);
					PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
					if (pfPrincipal != null) {
						List<CvLetteraAcc> listaLettAcc = cvLetteraAccHome
								.findByIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
						obj.put(STATUS, OK);
						JSONArray arrAcc = new JSONArray();
						for (CvLetteraAcc cvLetteraAcc : listaLettAcc) {
							if (!cvLetteraAcc.getFlagInviato()) {
								JSONObject letteraIesimo = new JSONObject();
								letteraIesimo.put(ID, cvLetteraAcc.getIdCvLetteraAcc());
								letteraIesimo.put(DATAMODIFICA, cvLetteraAcc.getDtmMod());
								letteraIesimo.put("flgInviato", cvLetteraAcc.getFlagInviato());
								letteraIesimo.put(NOME, cvLetteraAcc.getNome());
								letteraIesimo.put(BREVEPRES, cvLetteraAcc.getBrevePresentazione());
								letteraIesimo.put(MOTIVAZIONE, cvLetteraAcc.getMotivazObiettivi());
								letteraIesimo.put(PUNTIFORZA, cvLetteraAcc.getPuntiForzaQualita());
								letteraIesimo.put(SALUTI, cvLetteraAcc.getSaluti());
								letteraIesimo.put(BENEFICI, cvLetteraAcc.getBenefici());

								arrAcc.put(letteraIesimo);
							}
						}
						obj.putOpt(LISTALETTERE, arrAcc);
					} else {
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
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			log.error("Errore recupero lettere accompagnamento.");
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

	@GET
	@Path("/dettaglio/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getDettaglio(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("id") String idLettera) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName());
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();
			// IdLettera
			validator.setParam(idLettera);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/lettere/dettaglio/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					JSONObject obj = new JSONObject();
					String username = Utils.getUsernameFromTokenUtente(token);
					PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
					if (pfPrincipal != null) {
						CvLetteraAcc cvLetteraAcc = cvLetteraAccHome.findById(new Integer(idLettera));
						if (letteraPresente(cvLetteraAcc, pfPrincipal)) {
							obj.put(STATUS, OK);
							JSONArray arrAcc = new JSONArray();

							JSONObject letteraIesimo = new JSONObject();
							letteraIesimo.put(ID, cvLetteraAcc.getIdCvLetteraAcc());
							letteraIesimo.put(DATAMODIFICA, cvLetteraAcc.getDtmMod());
							letteraIesimo.put("flgInviato", cvLetteraAcc.getFlagInviato());
							letteraIesimo.put(NOME, cvLetteraAcc.getNome());
							letteraIesimo.put(BREVEPRES, cvLetteraAcc.getBrevePresentazione());
							letteraIesimo.put(MOTIVAZIONE, cvLetteraAcc.getMotivazObiettivi());
							letteraIesimo.put(PUNTIFORZA, cvLetteraAcc.getPuntiForzaQualita());
							letteraIesimo.put(SALUTI, cvLetteraAcc.getSaluti());
							letteraIesimo.put(BENEFICI, cvLetteraAcc.getBenefici());

							arrAcc.put(letteraIesimo);
							obj.putOpt(LISTALETTERE, arrAcc);
						} else {
							obj.put(STATUS, KO);
							obj.put(ERROR, "lettera non presente");
						}

					} else {
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
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			log.error("Errore recupero lettere accompagnamento.");
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

	private boolean letteraPresente(CvLetteraAcc cvLetteraAcc, PfPrincipal pfPrincipal) {
		boolean ret = false;

		if (cvLetteraAcc != null && cvLetteraAcc.getPfPrincipal() != null
				&& cvLetteraAcc.getPfPrincipal().getIdPfPrincipal().equals(pfPrincipal.getIdPfPrincipal())) {
			// Deve trattarsi di una lettera dell'utente loggato...
			ret = true;
		}
		return ret;
	}

}
