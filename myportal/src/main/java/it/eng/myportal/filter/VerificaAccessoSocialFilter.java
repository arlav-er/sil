package it.eng.myportal.filter;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.io.IOException;
import java.security.Principal;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class VerificaAccessoSocialFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		boolean isAResource = false;
		boolean isLogoutPage = false;
		Principal princ = request.getUserPrincipal();
		String fullUsername = princ.getName();

		if (request.getRequestURI().contains("logout")) {
			isLogoutPage = true;
		}

		if (request.getRequestURI().contains("javax.faces.resource")) {
			isAResource = true;
		}

		if (request.getRequestURI().contains("/rest/comuni")) {
			isAResource = true;
		}

		if (!isAResource && !isLogoutPage && fullUsername.startsWith("\\\\")) {
			String username = AuthUtil.removeSocialPrefix(fullUsername);
			InitialContext ic;
			try {
				ic = new InitialContext();
				UtenteInfoHome utenteInfoHome = (UtenteInfoHome) ic.lookup("java:module/UtenteInfoHome");

				UtenteInfo utenteInfo = utenteInfoHome.findByUsernameFromSocial(username);
				if (utenteInfo == null) {
					session.setAttribute("lxt_username", username);

					response.sendRedirect(ConstantsSingleton.BASE_URL + "/faces/public/login/completamento_dati.xhtml");
					return;
				}

			} catch (NamingException e) {
				throw new RuntimeException(e);
			}

		}

		// ///////// IMPORTANTE !!!!
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
