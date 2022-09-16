package it.eng.myportal.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

@WebServlet("/Twitter/Login")
public class TwitterLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(TwitterLogin.class);
	private static final long serialVersionUID = 1L;

	public TwitterLogin() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		// autenticazione al proxy, se necessaria
		new SimpleProxyAuthenticator();

		///////////////////////////////////////////////// 
		// MODIFICHE PER IL CAS
		/////////////////////////////////////////////////
		
		String loginTicket=req.getParameter("loginTicket");
		String flowExecutionKey=req.getParameter("flowExecutionKey");
		
		HttpSession session = req.getSession();
		session.setAttribute("loginTicket", loginTicket);
		session.setAttribute("flowExecutionKey", flowExecutionKey);
		
		////////////////////////////////////////////////// 
		
		
		
		String authorizeUrl = null;

		try {

			TwitterAuthenticator ta = new TwitterAuthenticator();
			OAuthService service = ta.getoAuthService();
			Token requestToken = service.getRequestToken();
			authorizeUrl = ta.getoAuthService().getAuthorizationUrl(requestToken);
			resp.sendRedirect(authorizeUrl);
		} catch (IOException e) {
			log.error("Errore di accesso all'URL di autorizzazione di Twitter. URL: " + authorizeUrl + " - errore: " + e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
