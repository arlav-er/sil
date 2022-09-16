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

import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet("/SPID/PugliaLogin")
public class SpidPugliaLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(SpidPugliaLogin.class);
	private static final long serialVersionUID = 1L;

	public SpidPugliaLogin() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

		
		///////////////////////////////////////////////// 
		// MODIFICHE PER IL CAS
		/////////////////////////////////////////////////
		
		String loginTicket=req.getParameter("loginTicket");
		String flowExecutionKey=req.getParameter("flowExecutionKey");

		HttpSession session = req.getSession();
		session.setAttribute("loginTicket", loginTicket);
		session.setAttribute("flowExecutionKey", flowExecutionKey);

		
		
		String spidSso = ConstantsSingleton.BASE_URL +  "/../sso-lxt/secure/spid";
		try {
			log.info("URL di spid-sso:" + spidSso);
			resp.sendRedirect(spidSso );
		} catch (IOException e) {
			log.error("Errore nella redirezione a spid-sso. URL=" + spidSso + " - errore: " + e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
