package it.eng.myportal.auth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet("/Federa/LoginServlet")
public class FederaServlet extends HttpServlet {
	private static final String BARRA = "\\";
	private static final String DOPPIA_BARRA = "\\\\";
	protected static Log log = LogFactory.getLog(FederaServlet.class);
	private static final long serialVersionUID = 1L;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	public FederaServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		Map<String, String> headers = new HashMap<String, String>();
		Enumeration<String> e = request.getHeaderNames();

		if (log.isDebugEnabled()) {
			log.debug("--- HEADER DA FEDERA ---");
		}

		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String value = request.getHeader(key);
			if (log.isDebugEnabled()) {
				log.debug(String.format("header('%s')='%s'", key, value));
			}
			headers.put(key, value);

		}

		if (log.isDebugEnabled()) {
			log.debug("--- HEADER DA FEDERA ---");
		}

		// if (!(headers.containsKey("federauserdomain") && headers.containsKey("codicefiscale")
		// && headers.containsKey("policylevel") && headers.containsKey("trustlevel"))) {
		// throw new ServletException("Scavalcato il controllo di sicurezza!");
		//
		// }

		headers.put(ConstantsSingleton.Federa.LOGGED, ConstantsSingleton.Federa.LOGGEDSECURITY);

		String j_username = it.eng.myportal.utils.Utils.randomString(16);
		while (pfPrincipalHome.findByUsername(j_username) != null) {
			j_username = it.eng.myportal.utils.Utils.randomString(16);
		}

		PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.FEDERA, headers);

		// MODIFICHE PER IL CAS
		String loginTicket = (String) request.getParameter("lt");
		String flowExecutionKey = (String) request.getParameter("fek");
		StringBuilder url = new StringBuilder();

		if (principal != null) {
			url = AuthUtil.composeLoginUrl(principal.getUsername(), ConstantsSingleton.Federa.TYPE,
					loginTicket, flowExecutionKey);
			//j_username = principal.getUsername();
			log.info("L'utente '" + j_username + "'  accede tramite Federa e ha già un account sul sistema. username: "
					+ j_username);
		} else {
			url = AuthUtil.composeLoginUrl(j_username, ConstantsSingleton.Federa.TYPE,
					loginTicket, flowExecutionKey);
			//url.append(ConstantsSingleton.MYCAS_URL + "/login?autosubmit=true&");
			log.info("L'utente '" + j_username + "'  accede tramite Federa e non ha un account sul sistema.");
			// fai login come utente federa

		}

		FederaAppSingleton.getInstance()
				.put(j_username, headers);

		log.info("Redirezione verso: " + url.toString());

		if (log.isDebugEnabled()) {
			for (String key : headers.keySet()) {
				log.debug("header( " + key + ")='" + headers.get(key) + "'");
			}
		}

		try {
			response.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella creazione dello SHA1");
			throw new ServletException(ex);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
