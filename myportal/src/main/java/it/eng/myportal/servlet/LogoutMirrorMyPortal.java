package it.eng.myportal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Questa servlet serve a propagare il logout avvenuto su un modulo negli altri
 * 
 * @author pegoraro/vuoto
 *
 */
@WebServlet("/IntrospectionServlet/logout")
public class LogoutMirrorMyPortal extends HttpServlet {
	private static final long serialVersionUID = 7581223022444639993L;
	protected static Log log = LogFactory.getLog(LogoutMirrorMyPortal.class);

	public LogoutMirrorMyPortal() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		/*
		 * https://www.owasp.org/index.php/Logout 
		 */
		
		
		/*
		 * First step : Invalidate user session
		 */
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			log.info("Session invalidated by mirror");
		}

		/*
		 * Second step : Invalidate all cookies by, for each cookie received,
		 * overwriting value and instructing browser to deletes it
		 */
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				cookie.setValue("-");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}