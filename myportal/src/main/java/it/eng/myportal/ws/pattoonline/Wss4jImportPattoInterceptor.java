package it.eng.myportal.ws.pattoonline;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.policy.SPConstants;
import org.apache.cxf.ws.security.policy.model.UsernameToken;
import org.apache.cxf.ws.security.wss4j.UsernameTokenInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
/**
 * per agganciarlo: @org.apache.cxf.interceptor.InInterceptors(interceptors = {
"it.eng.myportal.ws.pattoonline.Wss4jImportPattoInterceptor" })    


 * @author Ale
 *
 */
public class Wss4jImportPattoInterceptor extends UsernameTokenInterceptor {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		try {
			boolean isAuth = false;

			Header h = findSecurityHeader(message, false);
			if (h == null) {
				throw new IOException("(500) CXF WSS4J: Manca sezione sicurezza WS");
			}
			Element el = (Element) h.getObject();
			Element child = DOMUtils.getFirstElement(el);
			while (child != null) {
				if (SPConstants.USERNAME_TOKEN.equals(child.getLocalName())) {
					isAuth = true;
					try {
						// final WSUsernameTokenPrincipal princ = getPrincipal(child, message);
						final WSUsernameTokenPrincipal princ = parseTokenAndCreatePrincipal(child);
						if (princ != null) {
							if (princ.getName() == null) {
								throw new IOException("(500) CXF WSS4J: Username nulla");
							}
							if (princ.getPassword() == null) {
								throw new IOException("(500) CXF WSS4J: Password nulla");
							}

							//String username = princ.getName();

							try {
								checkCredenziali(princ);
								return;
							} catch (Exception e) {
								throw new IOException("(500) WSS4J: Utente invio patto da SIL non corretto");
							}
						} else {
							throw new IOException("(500) CXF WSS4J: Utente invio patto da SIL non corretto");
						}

					} catch (Exception ex) {
						throw new Fault(ex);
					}
				} else if (SPConstants.USERNAME_TOKEN10.equals(child.getLocalName())) {
					isAuth = true;
					throw new IOException("(500) CXF WSS4J: NON SUPPORTATO");
				} else if (SPConstants.USERNAME_TOKEN11.equals(child.getLocalName())) {
					isAuth = true;
					throw new IOException("(500) CXF WSS4J: NON SUPPORTATO");
				}

				child = DOMUtils.getNextElement(child);
			}

			if (!isAuth) {
				throw new IOException("(500) CXF WSS4J: Manca sezione sicurezza WS");
			}
		} catch (IOException e) {
			throw new Fault(e);
		}
	}

	private Header findSecurityHeader(SoapMessage message, boolean create) {
		for (Header h : message.getHeaders()) {
			QName n = h.getName();
			log.info("localpart: " + n.getLocalPart());
			log.info("namespaceURI: " + n.getNamespaceURI());
			if (n.getLocalPart().equals("Security") && (n.getNamespaceURI().equals(WSConstants.WSSE_NS)
					|| n.getNamespaceURI().equals(WSConstants.WSSE11_NS))) {
				return h;
			}
		}
		if (!create) {
			return null;
		}
		Document doc = DOMUtils.createDocument();
		Element el = doc.createElementNS(WSConstants.WSSE_NS, "wsse:Security");
		el.setAttributeNS(WSConstants.XMLNS_NS, "xmlns:wsse", WSConstants.WSSE_NS);
		SoapHeader sh = new SoapHeader(new QName(WSConstants.WSSE_NS, "Security"), el);
		sh.setMustUnderstand(true);
		message.getHeaders().add(sh);
		return sh;
	}

	protected WSUsernameTokenPrincipal parseTokenAndCreatePrincipal(Element tokenElement) throws WSSecurityException {
		org.apache.ws.security.message.token.UsernameToken ut = new org.apache.ws.security.message.token.UsernameToken(
				tokenElement);

		WSUsernameTokenPrincipal principal = new WSUsernameTokenPrincipal(ut.getName(), ut.isHashed());
		principal.setNonce(ut.getNonce());
		principal.setPassword(ut.getPassword());
		principal.setCreatedTime(ut.getCreated());
		principal.setPasswordType(ut.getPasswordType());

		return principal;
	}

	private CallbackHandler getCallback(SoapMessage message) {
		// Then try to get the password from the given callback handler
		Object o = message.getContextualProperty(SecurityConstants.CALLBACK_HANDLER);

		CallbackHandler handler = null;
		if (o instanceof CallbackHandler) {
			handler = (CallbackHandler) o;
		} else if (o instanceof String) {
			try {
				handler = (CallbackHandler) ClassLoaderUtils.loadClass((String) o, this.getClass()).newInstance();
			} catch (Exception e) {
				handler = null;
			}
		}
		return handler;
	}

	public String getPassword(String userName, UsernameToken info, int type, SoapMessage message) {

		CallbackHandler handler = getCallback(message);
		if (handler == null) {
			policyNotAsserted(info, "No callback handler and no password available", message);
			return null;
		}

		WSPasswordCallback[] cb = { new WSPasswordCallback(userName, type) };
		try {
			handler.handle(cb);
		} catch (Exception e) {
			policyNotAsserted(info, e, message);
		}

		// get the password
		return cb[0].getPassword();
	}

	/**
	 * verifica le credenziali di accesso per il WS di autenticazione secondo la tabella del portale ws_endpoint
	 * sarebbe meglio usare TsWsServer, ma qui non c'e`....
	 * 
	 * @param _login
	 * @param _pwd
	 * @throws Exception
	 */
	private void checkCredenziali(WSUsernameTokenPrincipal princ) throws Exception {
		String login = princ.getName();
		String password = princ.getPassword();
		String user[] = null;
		InitialContext ic;
		try {
			ic = new InitialContext();
			WsEndpointHome home = (WsEndpointHome) ic.lookup("java:app/MyPortal/WsEndpointHome");
			user = home.getWebServiceUser(TipoServizio.SIL_PATTO_NON_FIRMATO);
		} catch (Exception e) {
			log.error("recupero endpoint per wss4j " + e);
		}

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password SIL_PATTO_NON_FIRMATO errati");
		}
		
		if(WSConstants.PASSWORD_DIGEST.equals(princ.getPasswordType())) {
			if(!org.apache.ws.security.message.token.UsernameToken
					.doPasswordDigest(princ.getNonce(), princ.getCreatedTime(), pwdLocal).equals(password))
				throw new Exception("Username o Password SIL_PATTO_NON_FIRMATO errati");	
		} else { 
			if (!password.equals(pwdLocal)) {
				throw new Exception("Username o Password cleartext SIL_PATTO_NON_FIRMATO errati");
			}
		}

		/*
		 * if (oggi.compareTo(dataInizioVal) < 0) { throw new Exception("Account non ancora valido"); }
		 * 
		 * if (oggi.compareTo(dataFineVal) > 0) { throw new Exception("Account scaduto"); }
		 */
	}

}
