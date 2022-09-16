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

@WebServlet("/Google/Login")
public class GoogleLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(GoogleLogin.class);
	private static final long serialVersionUID = 1L;

	public GoogleLogin() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

		///////////////////////////////////////////////// 
		// MODIFICHE PER IL CAS
		/////////////////////////////////////////////////
		
		String loginTicket=req.getParameter("loginTicket");
		String flowExecutionKey=req.getParameter("flowExecutionKey");
		
		HttpSession session = req.getSession();
		session.setAttribute("loginTicket", loginTicket);
		session.setAttribute("flowExecutionKey", flowExecutionKey);
		
		////////////////////////////////////////////////// 
		
		
		GoogleAuthenticator ga = new GoogleAuthenticator();

		String authorizeUrl = ga.make01treq();
		try {
			resp.sendRedirect(authorizeUrl);
		} catch (IOException e) {
			log.error("Errore di accesso all'URL di autorizzazione di Google. URL: " + authorizeUrl + " - errore: " + e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
