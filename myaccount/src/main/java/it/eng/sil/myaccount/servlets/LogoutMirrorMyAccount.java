package it.eng.sil.myaccount.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Questa servlet serve a propagare il logout avvenuto su un modulo negli altri
 * 
 * @author pegoraro/vuoto
 *
 */
@WebServlet("/IntrospectionServlet/logout")
public class LogoutMirrorMyAccount extends HttpServlet {
	private static final long serialVersionUID = 7581223022444639993L;
	protected static Log log = LogFactory.getLog(LogoutMirrorMyAccount.class);

	public LogoutMirrorMyAccount() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.getSession().invalidate();
			log.info("Session invalidated by mirror");
		} catch (Exception e) {
			log.info("ERROR Session mirror invalidate: " + e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}