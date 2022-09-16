package it.eng.myportal.ws.mysap;

import it.eng.myportal.ws.mysap.pojo.CurriculumVitaeFull;
import it.eng.myportal.ws.mysap.pojo.CurriculumVitaeHeader;
import it.eng.myportal.ws.mysap.pojo.MySapWsException;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.InInterceptors;

@InInterceptors(interceptors = { "it.eng.myportal.ws.interceptor.MySapBasicAuthInterceptor" })
@WebService(name = "MySap", targetNamespace = "http://ws.myportal.eng.it/")
public class MySap {

	protected static Log log = LogFactory.getLog(MySap.class);

	@EJB
	MySapEjb mySapEjb;

	@WebMethod
	public List<CurriculumVitaeHeader> getCurriculaVitae(String username) throws MySapWsException {
		return mySapEjb.getCurriculaVitae(username);
	}

	@WebMethod
	public CurriculumVitaeFull getCurriculumVitae(int id) throws MySapWsException {
		return mySapEjb.getCurriculumVitae(id);
	}

}
