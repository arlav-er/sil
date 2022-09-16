package it.eng.myportal.auth;

import java.io.IOException;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.PfIdentityProvider;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.home.IcarSessioneHome;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet("/ICAR/LoginServlet")
public class ICARservlet extends HttpServlet {
	protected static Log log = LogFactory.getLog(ICARservlet.class);
	private static final long serialVersionUID = 1L;

	
	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;
	
	@EJB
	IcarSessioneHome icarSessioneHome;

	public ICARservlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		HttpSession session = request.getSession();

		// TODO da rimuovere!!!
		
//		String loginTicket=request.getParameter("loginTicket");
//		String flowExecutionKey=request.getParameter("flowExecutionKey");
//
//		session.setAttribute("loginTicket", loginTicket);
//		session.setAttribute("flowExecutionKey", flowExecutionKey);

		// TODO FINE da rimuovere!!!
		
		
		String token = request.getParameter(ConstantsSingleton.Icar.ICARTOKEN);
		
		Map<String, String> map = icarSessioneHome.getSessioneByToken(token);
		map.put(ConstantsSingleton.Auth.TYPE, ConstantsSingleton.Icar.TYPE);
		
		IcarAppSingleton.getInstance().put(map.get(ConstantsSingleton.Icar.UNIQUE_ID),map);
		
        PfPrincipalDTO principal = pfIdentityProviderHome.getAccount(TipoProvider.ICAR, map);
		
		StringBuilder url=null;
		String j_username=null;
		
		if (principal != null) {
			j_username = principal.getUsername();
			
			log.info("L'utente '" + j_username +"' accede tramite " + 
					ConstantsSingleton.Icar.TYPE + 
						" e ha già un account sul sistema. username: " + j_username);
			
		} else {
			
			j_username = it.eng.myportal.utils.Utils.randomString(16);
			while (pfPrincipalHome.findByUsername(j_username) != null) {
				j_username = it.eng.myportal.utils.Utils.randomString(16);
			}

			log.info("L'utente '" + j_username + "' accede tramite " + 
					ConstantsSingleton.Icar.TYPE + 
					" e non ha un account sul sistema.");
			
			
			if(ConstantsSingleton.COD_REGIONE==ConstantsSingleton.COD_REGIONE_VDA
				|| ConstantsSingleton.COD_REGIONE==ConstantsSingleton.COD_REGIONE_UMBRIA
					){
				String cf =(String) map.get("CodiceFiscale");
				UtenteInfo utenteInfoForteByCf = icarSessioneHome.getUtenteInfoForteByCf(cf);
				
				if (utenteInfoForteByCf!=null){
					PfIdentityProvider idp = new PfIdentityProvider();
					idp.setCodTipoProvider(TipoProvider.ICAR );
					pfIdentityProviderHome.createForVda( j_username,
							 utenteInfoForteByCf.getIdPfPrincipal(), 
							 TipoProvider.ICAR, map);
					
				}
			}
			
		}

		url = AuthUtil.preparaUrl(session, j_username, ConstantsSingleton.Icar.TYPE);
		IcarAppSingleton.getInstance().put(j_username, map);
		
		try {
			log.debug("Redirezione verso: " + url.toString());
			response.sendRedirect(url.toString());
		} catch (IOException ex) {
			log.error("Errore nella redirezione verso: " + url.toString());
			throw new ServletException(ex);
		}
       
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
