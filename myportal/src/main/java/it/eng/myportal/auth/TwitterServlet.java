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
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

@WebServlet("/Twitter/LoginServlet")
public class TwitterServlet extends HttpServlet {

	protected static Log log = LogFactory.getLog(TwitterServlet.class);
	private static final long serialVersionUID = 1L;
	private final String TWITTER_VERIFY_CREDENTIALS_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;
	public static final String ERROR_URL_PARAM_NAME = "error";

	public TwitterServlet() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {


		// controllo nel caso in cui non si autorizza
		String denied = req.getParameter("denied");
		if (denied!=null){
			try {
				log.info("Non autorizzato a prendere i dati da Twitter!");
				resp.sendRedirect(ConstantsSingleton.BASE_URL);
				resp.flushBuffer();
				return;
			} catch (IOException ex) {
				log.error("Errore nella redirezione alla Home Page");
				throw new ServletException(ex);
			}
		}
		
		HttpSession session = req.getSession();
		String requestTokenStr = req.getParameter("oauth_token");
		String requestTokenVerifier = req.getParameter("oauth_verifier"); 
		Token requestToken = new Token (requestTokenStr, requestTokenVerifier);
		Verifier verifier = new Verifier(requestTokenVerifier);
		

		TwitterAuthenticator ta = new TwitterAuthenticator();
		OAuthService service = ta.getoAuthService();
		Token accessToken = service.getAccessToken(requestToken, verifier);
		
		
		OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_VERIFY_CREDENTIALS_URL);
	    service.signRequest(accessToken, request);
	    String rispostaJson = request.send().getBody();
		
		String id = null;
		String name = null;
		String surname = "";
		String urlImage = null;

		JSONObject json = null;
		try {
			json = new JSONObject(rispostaJson);
			id = json.getString("id");
		} catch (JSONException e) {
			log.error(e.getMessage());
			throw new ServletException(e);
		}
	    	
		try{	
			name = json.getString("name");
			String[] pezzi = name.split(" ");
			if (pezzi.length>1){
				name = pezzi[0];
				for(int i=1; i<pezzi.length; i++)
					surname += pezzi[1] +" ";
			}
		
			urlImage = json.getString("profile_image_url");
			
		} catch (JSONException e) {
			log.warn(e.getMessage());
		}
	    
		
		byte[] image = { 0x00, 0x00 };
		
		HttpClientHelper helper = new HttpClientHelper();
		try {
			image = helper.retrieveImage(urlImage);
		} catch (IOException e) {
			log.warn(e.getMessage());
		}

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Auth.ID, id);
		headers.put(Auth.COGNOME,surname);
		headers.put(Auth.NOME, name);
		headers.put(Auth.IMAGE, image);

		String nominativo = name + " " + surname;
		PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.TWITTER, headers);
		
		StringBuilder url=null;
		String j_username=null;
		if (principal != null) {
			j_username = principal.getUsername();
			
			log.info("L'utente '" + nominativo +"' accede tramite " + 
						ConstantsSingleton.Twitter.TYPE + 
						" e ha già un account sul sistema. username: " + j_username);
			
		} else {

			log.info("L'utente '" + nominativo + "' accede tramite " + 
						ConstantsSingleton.Twitter.TYPE + 
						" e non ha un account sul sistema.");
			
			j_username = it.eng.myportal.utils.Utils.randomString(16);
			while (pfPrincipalHome.findByUsername(j_username) != null) {
				j_username = it.eng.myportal.utils.Utils.randomString(16);
			}

			
		}

		url = AuthUtil.preparaUrl(session, j_username, ConstantsSingleton.Twitter.TYPE);
		TwitterAppSingleton.getInstance().put(j_username, headers);
		
		
		try {
			log.debug("Redirezione verso: " + url.toString());
			resp.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella redirezione a " + url.toString());
			throw new ServletException(ex);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
