/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sil.myaccount.controller.filters;

import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;
import it.eng.sil.myaccount.controller.mbeans.session.AccountInfoBean;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.model.business.GestioneProfiloClientEJB;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;

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

/**
 *
 * @author hatemalimam
 */
public class VerificaAccessoFilter implements Filter {

	protected static Log log = LogFactory.getLog(VerificaAccessoFilter.class);

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
		boolean isPwdScaduta = false;
		Principal princ = request.getUserPrincipal();
		String username = princ.getName();

		if (request.getRequestURI().contains("logout")) {
			isLogoutPage = true;
		}

		if (!isLogoutPage) {
			InitialContext ic;
			GestioneProfiloClientEJB gestioneProfiloClientEJB;
			ConstantsSingleton constantsSingleton;
			// ConstantsSingleton constantsSingleton;
			try {
				ic = new InitialContext();
				gestioneProfiloClientEJB = (GestioneProfiloClientEJB) ic.lookup("java:module/GestioneProfiloClientEJB");
				constantsSingleton = (ConstantsSingleton) ic.lookup("java:module/ConstantsSingleton");
				// constantsSingleton = (ConstantsSingleton) ic.lookup("java:module/ConstantsSingleton");

				// si controlla se l'utente è loggato esaminando il relativo sessionscoped bean
				AccountInfoBean accountInfoBean = null;
				if (session.getAttribute("accountInfoBean") != null) {
					accountInfoBean = (AccountInfoBean) session.getAttribute("accountInfoBean");
					UserProfiloPOJO userInfo = accountInfoBean.getUserInfo();
					isUserLoggedIn = isLoginAllowed(userInfo);

					isPwdScaduta = checkPasswordScaduta(userInfo);
					if (isPwdScaduta) {
						// bisogna ricaricare i dati dal DB per verificare che l'utente ha effettivamente cambiato la
						// password
						accountInfoBean = new AccountInfoBean(username, gestioneProfiloClientEJB, constantsSingleton);
						session.setAttribute("accountInfoBean", accountInfoBean);
						userInfo = accountInfoBean.getUserInfo();

						Date dataScadenzaPwd = userInfo.getDtScadenza();
						Date now = new Date();
						if (dataScadenzaPwd.before(now)) {
							isPwdScaduta = true;
						} else {
							isPwdScaduta = false;
						}
					}
				} else {
					accountInfoBean = new AccountInfoBean(username, gestioneProfiloClientEJB, constantsSingleton);
					session.setAttribute("accountInfoBean", accountInfoBean);
					UserProfiloPOJO userInfo = accountInfoBean.getUserInfo();
					isUserLoggedIn = isLoginAllowed(userInfo);

					isPwdScaduta = checkPasswordScaduta(userInfo);
				}

				if (request.getRequestURI().contains("error/401")) {
					isErrorPage = true;
				}

				if (request.getRequestURI().contains("javax.faces.resource")) {
					isAResource = true;
				}

				if (!isUserLoggedIn && !isErrorPage && !isAResource && !isLogoutPage) {
					response.sendRedirect(request.getContextPath() + "/error/401");
				} else if (isPwdScaduta && !request.getRequestURI().contains("secure/changePassword")) {
					response.sendRedirect(request.getContextPath() + "/secure/changePassword");
				} else {
					chain.doFilter(request, response);
				}

			} catch (NamingException e) {
				log.error("Errore nel recupero della risorsa JNDI", e);
				throw new RuntimeException("Errore nel recupero della risorsa JNDI nel filtro");
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

	private boolean checkPasswordScaduta(UserProfiloPOJO userInfo) {
		boolean isPwdScaduta = false;
		Date dataScadenzaPwd = userInfo.getDtScadenza();
		Date now = new Date();
		if (dataScadenzaPwd.before(now)) {
			isPwdScaduta = true;
		}

		return isPwdScaduta;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
