package it.eng.myportal.filter;

import it.eng.myportal.mbeans.sessione.AccountInfoBean;
import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VerificaAccessoFilter implements Filter {

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		boolean isUserLoggedIn = false; // utente loggato
		boolean isErrorPage = false; // il client chiede di visualizzare una pagina di errore
		boolean isAResource = false;
		boolean isLogoutPage = false;
		Principal princ = request.getUserPrincipal();
		String username = princ.getName();

		if (request.getRequestURI().contains("logout")) {
			isLogoutPage = true;
		}

		if (request.getRequestURI().contains("javax.faces.resource")) {
			isAResource = true;
		}

		if (request.getRequestURI().contains("/rest/comuni")) {
			isAResource = true;
		}

		if (!isLogoutPage && !isAResource) {
			InitialContext ic;
			try {
				ic = new InitialContext();

				// si controlla se l'utente è loggato esaminando il relativo sessionscoped bean
				AccountInfoBean accountInfoBean = null;
				if (session.getAttribute("accountInfoBean") != null) {
					accountInfoBean = (AccountInfoBean) session.getAttribute("accountInfoBean");
					UserProfiloPOJO userInfo = accountInfoBean.getUserInfo();
					isUserLoggedIn = isLoginAllowed(userInfo);
				} else {
					accountInfoBean = new AccountInfoBean(username);
					session.setAttribute("accountInfoBean", accountInfoBean);
					UserProfiloPOJO userInfo = accountInfoBean.getUserInfo();
					isUserLoggedIn = isLoginAllowed(userInfo);
				}

				if (request.getRequestURI().contains("/faces/public/errors.xhtml")) {
					isErrorPage = true;
				}

				if (!isUserLoggedIn && !isErrorPage && !isAResource && !isLogoutPage) {
					response.sendRedirect(request.getContextPath() + "/faces/public/errors.xhtml");
				} else {
					chain.doFilter(request, response);
				}

			} catch (NamingException e) {
				log.error(e.getMessage());
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isLoginAllowed(UserProfiloPOJO userInfo) {
		boolean isUserLoggedIn = false;
		// FIXME aggiungere verifica se scaduto o scadenza password
		if (userInfo.getFlagAbilitato() == null || !userInfo.getFlagAbilitato()) {
			isUserLoggedIn = false;
		} else {
			isUserLoggedIn = true;
		}

		return isUserLoggedIn;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
