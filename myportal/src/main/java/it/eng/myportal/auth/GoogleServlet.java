package it.eng.myportal.auth;

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

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Auth;

@WebServlet("/Google/LoginServlet")
public class GoogleServlet extends HttpServlet {

	protected static Log log = LogFactory.getLog(GoogleServlet.class);
	private static final long serialVersionUID = 1L;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;
	
	public static final String ERROR_URL_PARAM_NAME = "error";

	public GoogleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		String code = request.getParameter("code");

		if (code == null) {
			try {
				log.info("Non autorizzato a prendere i dati da Google plus!");
				response.sendRedirect(ConstantsSingleton.BASE_URL);
				response.flushBuffer();
				return;
			} catch (IOException ex) {
				log.error("Errore nella redirezione alla Home Page");
				throw new ServletException(ex);
			}
		}

		GoogleAuthenticator ga = new GoogleAuthenticator();

		String json;
		try {
			String authorizazion_header = ga.make02req(code);
			json = ga.make03req(authorizazion_header);
		} catch (JSONException e1) {

			log.error("Errore nella richiesta di autenticazione da GOOGLE (JSON non previsto): " + e1.getMessage());
			throw new ServletException(e1);
		} catch (IOException e1) {
			log.error("Errore nella richiesta di autenticazione da GOOGLE (I/O): " + e1.getMessage());
			throw new ServletException(e1);
		}

		String email = null;
		String id = null;
		String name = "";
		String surname = "";
		String urlPicture = null;

		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
			id = jo.getString("id");
		} catch (JSONException e) {
			log.error(e.getMessage() + "\nJSON =" + json);
			throw new ServletException(e);
		}

		try {
			email = jo.getString("email");
		} catch (JSONException e) {
			log.warn(e.getMessage() + "\nJSON =" + json);
		}

		try {
			name = jo.getString("given_name");
		} catch (JSONException e) {
			log.warn(e.getMessage() + "\nJSON =" + json);
		}
		try {
			surname = jo.getString("family_name");
		} catch (JSONException e) {
			log.warn(e.getMessage() + "\nJSON =" + json);
		}
		try {
			urlPicture = jo.getString("picture");

		} catch (JSONException e) {
			log.warn(e.getMessage() + "\nJSON =" + json);
		}

		byte[] image = { 0x00, 0x00 };
		if (urlPicture != null) {

			HttpClientHelper helper = new HttpClientHelper();
			try {
				image = helper.retrieveImage(urlPicture);
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}

		String nominativo =  name + " " + surname;
				
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Auth.ID, id);
		headers.put(Auth.EMAIL, email);
		headers.put(Auth.NOME, name);
		headers.put(Auth.COGNOME, surname);
		headers.put(Auth.IMAGE, image);

		PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.GOOGLE, headers);
		
		HttpSession session = request.getSession();

		
		StringBuilder url=null;
		String j_username =null;
		if (principal != null) {
			j_username = principal.getUsername();
			
			log.info("L'utente '" + nominativo +"' accede tramite " + 
						ConstantsSingleton.Google.TYPE + 
						" e ha già un account sul sistema. username: " + j_username);
			
		} else {

			log.info("L'utente '" + nominativo + "' accede tramite " + 
						ConstantsSingleton.Google.TYPE + 
						" e non ha un account sul sistema.");
			
			j_username = it.eng.myportal.utils.Utils.randomString(16);
			while (pfPrincipalHome.findByUsername(j_username) != null) {
				j_username = it.eng.myportal.utils.Utils.randomString(16);
			}

		}
		url = AuthUtil.preparaUrl(session, j_username, ConstantsSingleton.Google.TYPE);
		GoogleAppSingleton.getInstance().put(j_username, headers);
		
		try {
			log.debug("Redirezione verso: " + url.toString());
			response.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella creazione dello SHA1");
			throw new ServletException(ex);
		}

	}

	

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
