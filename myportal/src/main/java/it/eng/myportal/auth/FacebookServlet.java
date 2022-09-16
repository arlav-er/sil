package it.eng.myportal.auth;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

@WebServlet("/Facebook/LoginServlet")
public class FacebookServlet extends HttpServlet {

	private static final String GRAPH_FACEBOOK_URL = "https://graph.facebook.com/";
	protected static Log log = LogFactory.getLog(FacebookServlet.class);
	private static final long serialVersionUID = 1L;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;
	public static final String ERROR_URL_PARAM_NAME = "error";

	public FacebookServlet() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

		String state = req.getParameter(FacebookAuthenticator.STATE);

		HttpSession session = req.getSession();
		String stateFromSession = (String) session.getAttribute(FacebookAuthenticator.STATE);
		session.removeAttribute(FacebookAuthenticator.STATE);
		if (!state.equals(stateFromSession)) {
			try {
				log.warn("Stato di Facebook non valido. Redirezione alla Home Page per una nuova autenticazione");
				resp.sendRedirect(ConstantsSingleton.BASE_URL);
				resp.flushBuffer();
				return;

			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}

		String code = req.getParameter("code");

		FacebookAuthenticator fa = new FacebookAuthenticator();

		Token accessToken = getAccessToken(fa.getoAuthService(), code);
		Response response = getResponseForProfile(fa.getoAuthService(), accessToken);
		if (!response.isSuccessful()) {
			try {
				log.warn("Qualche problema nel token da Facebook!");
				resp.sendRedirect(ConstantsSingleton.BASE_URL);
				resp.flushBuffer();
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}

		String rispostaJson = response.getBody();

		String id = null;
		String name = "";
		String surname ="";
		JSONObject json = null;

		try {
			json = new JSONObject(rispostaJson);
			log.debug("Risposta JSON da FACEBOOK:" + json);
			id = json.getString("id");
		} catch (JSONException e) {
			log.warn(e.getMessage());
		}

		try {
			name = json.getString("first_name");
		} catch (JSONException e) {
			log.warn(e.getMessage());
		}

		try {
			surname = json.getString("last_name");
		} catch (JSONException e) {
			log.warn(e.getMessage());
		}

		String imageUrl = GRAPH_FACEBOOK_URL + id + "/picture";

		byte[] image = { 0x00, 0x00 };
		HttpClientHelper helper = new HttpClientHelper();
		try {
			image = helper.retrieveImage(imageUrl);
		} catch (IOException e) {
			log.debug(e.getMessage());
			log.warn("Immagine non disponibile");
		}

		String nominativo = name + " " + surname;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Auth.ID, id.toString());
		headers.put(Auth.NOME, name);
		headers.put(Auth.COGNOME, surname);
		headers.put(Auth.IMAGE, image);
		headers.put(ConstantsSingleton.Auth.TYPE, ConstantsSingleton.Facebook.TYPE);

		PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.FACEBOOK, headers);

		
		StringBuilder url=null;
		String j_username=null;
		if (principal != null) {
			j_username = principal.getUsername();
			
			log.info("L'utente '" + nominativo +"' accede tramite " + 
						ConstantsSingleton.Facebook.TYPE + 
						" e ha già un account sul sistema. username: " + j_username);
			
		} else {

			log.info("L'utente '" + nominativo + "' accede tramite " + 
						ConstantsSingleton.Facebook.TYPE + 
						" e non ha un account sul sistema.");
			
			j_username = it.eng.myportal.utils.Utils.randomString(16);
			while (pfPrincipalHome.findByUsername(j_username) != null) {
				j_username = it.eng.myportal.utils.Utils.randomString(16);
			}

			
		}

		url = AuthUtil.preparaUrl(session, j_username, ConstantsSingleton.Facebook.TYPE);
		FacebookAppSingleton.getInstance().put(j_username, headers);
		
		try {
			log.debug("Redirezione verso: " + url.toString());
			resp.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella redirezione verso: " + url.toString());
			throw new ServletException(ex);
		}

	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	private Token getAccessToken(OAuthService oAuthService, String code) {
		Verifier verifier = new Verifier(code);
		return oAuthService.getAccessToken(Token.empty(), verifier);
	}

	private Response getResponseForProfile(OAuthService oAuthService, Token accessToken) {
		OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, GRAPH_FACEBOOK_URL + "me");
		oAuthService.signRequest(accessToken, oauthRequest);
		return oauthRequest.send();
	}

}
