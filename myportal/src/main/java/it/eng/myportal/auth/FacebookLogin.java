package it.eng.myportal.auth;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scribe.model.Token;

@WebServlet("/Facebook/Login")
public class FacebookLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(FacebookLogin.class);
	private static final long serialVersionUID = 1L;

	public FacebookLogin() {
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
		
		
		String state = UUID.randomUUID().toString();
		session.setAttribute(FacebookAuthenticator.STATE, state);

		FacebookAuthenticator fa = new FacebookAuthenticator();

		String authorizationUrl = fa.getoAuthService().getAuthorizationUrl(Token.empty()) + "&"
				+ FacebookAuthenticator.STATE + "=" + state;

		try {
			resp.sendRedirect(authorizationUrl);
		} catch (IOException e) {
			log.error("Errore nella redirezione a Facebook. URL=" + authorizationUrl + " - errore:" + e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
