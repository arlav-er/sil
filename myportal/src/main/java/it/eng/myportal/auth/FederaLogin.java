package it.eng.myportal.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet("/Federa/Login")
public class FederaLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(FederaLogin.class);
	private static final long serialVersionUID = 1L;

	public FederaLogin() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

		
		///////////////////////////////////////////////// 
		// MODIFICHE PER IL CAS
		/////////////////////////////////////////////////
		
		String loginTicket=req.getParameter("loginTicket");
		String flowExecutionKey=req.getParameter("flowExecutionKey");
		
		
		String urlServiziFederati = ConstantsSingleton.Federa.URL_SERVIZI_FEDERATI;
		try {
			urlServiziFederati+= "?lt=" + URLEncoder.encode(loginTicket, "UTF-8") + 
								"&fek=" + URLEncoder.encode(flowExecutionKey, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.error("Errore nella codifica dei token del CAS: " + e1.getMessage());
			return;
		}
		
		try {
			resp.sendRedirect(urlServiziFederati );
		} catch (IOException e) {
			log.error("Errore nella redirezione a Federa. URL=" + urlServiziFederati + " - errore:" + e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
