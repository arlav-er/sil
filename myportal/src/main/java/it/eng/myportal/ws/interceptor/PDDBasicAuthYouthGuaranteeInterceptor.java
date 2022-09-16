package it.eng.myportal.ws.interceptor;

import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class PDDBasicAuthYouthGuaranteeInterceptor extends AbstractPhaseInterceptor<Message> {

	public PDDBasicAuthYouthGuaranteeInterceptor() {
		super(Phase.PRE_INVOKE);
		setCredentials();
	}

	public PDDBasicAuthYouthGuaranteeInterceptor(String phase) {
		super(Phase.PRE_INVOKE);
	}

	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String BASIC = "Basic";
	private final static String AUTH_HEADER = "Authorization";

	private String username;
	private String password;
	private String abilitata;

	/*
	 * Modificare tale metodo per prendere le credenziali per l'accesso al web
	 * service memorizzate da qualche parte, ad es. in un qualche file di
	 * properties
	 */
	private void setCredentials() {
		abilitata = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_YOUTHGUARANTEE_ABILITATA;
		username = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_YOUTHGUARANTEE_USERNAME;
		password = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_YOUTHGUARANTEE_PASSWORD;
		log.info("PDD Notifiche Youth Guarantee: Basic Authentication: abilitata=" + abilitata + " - Username="
				+ username + " - Password=" + password);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		TreeMap<String, List<String>> httpHeaders = (TreeMap<String, List<String>>) message
				.get(Message.PROTOCOL_HEADERS);

		if ("Y".equalsIgnoreCase(abilitata)) {
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
	}

	private String encode(String userName, String password) {

		String clearCredentials = userName + COLON + password;
		String b64 = BASIC + SPACE
				+ new String(org.apache.commons.codec.binary.Base64.encodeBase64(clearCredentials.getBytes()));
		return b64;
	}
}
