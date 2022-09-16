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

@WebServlet("/ICAR/UmbriaLogin")
public class IcarUmbriaLogin extends HttpServlet {

	protected static Log log = LogFactory.getLog(IcarUmbriaLogin.class);
	private static final long serialVersionUID = 1L;

	public IcarUmbriaLogin() {
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

		
		
		String urlIcarInf3Servlet = ConstantsSingleton.BASE_URL +  "/../IcarInf3/secure/IcarInf3Servlet";
		try {
			log.info("URL di idm-umbria:" + urlIcarInf3Servlet);
			resp.sendRedirect(urlIcarInf3Servlet );
		} catch (IOException e) {
			log.error("Errore nella redirezione ad IDM-UMBRIA. URL=" + urlIcarInf3Servlet + " - errore: " + e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
