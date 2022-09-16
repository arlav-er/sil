package it.eng.myportal.auth;

import java.io.IOException;
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

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.home.IcarSessioneHome;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet("/SpidPuglia/LoginServlet")
public class SpidPugliaServlet extends HttpServlet {
	protected static Log log = LogFactory.getLog(SpidPugliaServlet.class);
	private static final long serialVersionUID = 1L;

	
	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;
	
	@EJB
	IcarSessioneHome icarSessioneHome;

	public SpidPugliaServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		HttpSession session = request.getSession();

		String token = request.getParameter(ConstantsSingleton.Spid.TOKEN);
		
		Map<String, String> map = icarSessioneHome.getSessioneByToken(token);
		map.put(ConstantsSingleton.Auth.TYPE, ConstantsSingleton.Spid.TYPE);
		
		SpidAppSingleton.getInstance().put(map.get(ConstantsSingleton.Spid.UNIQUE_ID),map);
		
        PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.SPID, map);
		
		StringBuilder url=null;
		String j_username=null;
		
		if (principal != null) {
			j_username = principal.getUsername();
			
			log.info("L'utente '" + j_username +"' accede tramite " + 
					ConstantsSingleton.Spid.TYPE + 
						" e ha già un account sul sistema. username: " + j_username);
			
		} else {
			
			j_username = it.eng.myportal.utils.Utils.randomString(16);
			while (pfPrincipalHome.findByUsername(j_username) != null) {
				j_username = it.eng.myportal.utils.Utils.randomString(16);
			}

			log.info("L'utente '" + j_username + "' accede tramite " + 
					ConstantsSingleton.Spid.TYPE + 
					" e non ha un account sul sistema.");
			
		}

		url = AuthUtil.preparaUrl(session, j_username, ConstantsSingleton.Spid.TYPE);
		SpidAppSingleton.getInstance().put(j_username, map);
		
		try {
			log.debug("Redirezione verso: " + url.toString());
			response.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella redirezione verso: " + url.toString());
			throw new ServletException(ex);
		}
       
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
