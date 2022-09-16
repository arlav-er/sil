package it.eng.myportal.ws.interceptor;

import java.util.List;
import java.util.TreeMap;

import javax.naming.InitialContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.ConstantsSingleton;

public class MySapBasicAuthInterceptor extends AbstractPhaseInterceptor<Message> {
	protected final Log log = LogFactory.getLog(MySapBasicAuthInterceptor.class);

	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String BASIC = "Basic";
	private final static String AUTH_HEADER = "Authorization";

	public MySapBasicAuthInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public MySapBasicAuthInterceptor(String phase) {
		super(Phase.PRE_INVOKE);
	}

	private String[] retrieveCredentials() throws Fault {

		String[] credential = null;
		InitialContext ic;
		try {
			ic = new InitialContext();
			WsEndpointHome wsEndpointHome = (WsEndpointHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "WsEndpointHome");
			credential = wsEndpointHome.getWebServiceUser(TipoServizio.MYSAP);

		} catch (Exception e) {
			log.error("recupero credenziali di accesso " + e);
			throw new Fault(new Exception("(500) ERRORE NEL MRECUPERO DELLE CREDENZIALI DAL DB"));
		}
		return credential;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message message) throws Fault {
		String[] credential = retrieveCredentials();

		String username = credential[0];
		String password = credential[1];

		log.info(String.format("Basic Authentication: username='%s' password='%s'", username, password));

		TreeMap<String, List<String>> httpHeaders = (TreeMap<String, List<String>>) message
				.get(Message.PROTOCOL_HEADERS);

		List<String> authoritiations = httpHeaders.get(AUTH_HEADER);

		if (authoritiations != null) {
			for (String headAut : authoritiations) {
				if (headAut.equals(encode(username, password))) {
					log.debug("HEADER PDD: " + headAut);
					return;
				}
			}
		}

		throw new Fault(new Exception("(500) NON AUTORIZZATO!"));

	}

	private String encode(String userName, String password) {

		String clearCredentials = userName + COLON + password;
		String b64 = BASIC + SPACE + new String(Base64.encodeBase64(clearCredentials.getBytes()));
		return b64;
	}
}
