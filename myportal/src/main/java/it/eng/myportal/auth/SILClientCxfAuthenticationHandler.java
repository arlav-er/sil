package it.eng.myportal.auth;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.headers.Header;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * Classe di utilità per creare gli header di sicurezza da aggiungere ai messaggi SOAP 
 * nel caso di client generati con CXF. Questo metodo "artigianale" di autenticazione 
 * delle chiamate si è reso necessario per non modificare la configurazione delle 
 * dipendenze del progetto relativamente alle librerie WSS4J-1.5.12 e CXF-2.4.6 in 
 * particolare per la classe org.apache.ws.security.WSPasswordCallback che si trova 
 * in entrambe le lib e va in conflitto. 
 * Nelle versioni successive di CXF questo problema è stato risolto ed è possibile 
 * utilizzare org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor.
 * 
 * @author AntimoV
 *
 */
public class SILClientCxfAuthenticationHandler {



	protected final Log log = LogFactory.getLog(this.getClass());
	
	public static  List<Header> buildSecurityHeader(String username, String password) throws Exception {
		
		List<Header> headersList = new ArrayList<Header>();
		QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security",
				"wsse");
		
		// XML Document builder with a root node
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource inStream = new InputSource();
		inStream.setCharacterStream(new StringReader("<root></root>"));
		Document document = builder.parse(inStream);

		// <wsse:UsernameToken>
		WSSecUsernameToken usernametoken = new WSSecUsernameToken();
		usernametoken.setPasswordType(WSConstants.PASSWORD_TEXT);
		usernametoken.setUserInfo(username, password);

		// DIGEST PASSWORD
//		usernametoken.setPasswordType(WSConstants.PASSWORD_DIGEST);
//		usernametoken.setUserInfo(username, constructPasswordDigest(password));
		
		// <wsse:Security>
		WSSecHeader secHeader = new WSSecHeader();
		secHeader.insertSecurityHeader(document);

		// Generates the Document with <root><Header><wsse:Security>...
		usernametoken.build(document, secHeader);

		// Extract the desired node
		Node securityNode = document.getElementsByTagName("wsse:Security").item(0);

		Header soapHeader = new Header(securityHeader, securityNode);

		headersList.add(soapHeader);
		
		return headersList;
	}

	public String nodeToString(Node node) throws Exception {
		StringWriter sw = new StringWriter();

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));
		return sw.toString();
	}
	
	
	
	//// PASSWORD DIGEST
	
	private static SecureRandom RANDOM;
    private static final int NONCE_SIZE_IN_BYTES = 16;
    private static final String MESSAGE_DIGEST_ALGORITHM_NAME_SHA_1 = "SHA-1";
    private static final String SECURE_RANDOM_ALGORITHM_SHA_1_PRNG = "SHA1PRNG";


    private static byte[] generateNonce() {
        try {
            RANDOM = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM_SHA_1_PRNG);
            RANDOM.setSeed(System.currentTimeMillis());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] nonceBytes = new byte[NONCE_SIZE_IN_BYTES];
        //String nonce = new String(org.apache.commons.codec.binary.Base64.encodeBase64(nonceBytes), "UTF-8");
        RANDOM.nextBytes(nonceBytes);
        return nonceBytes;
    }

    /**
     * @noinspection SameParameterValue
     */
    private static String constructPasswordDigest(String password) {
    	String passwordDigest=null;
        try {
        	byte [] nonceBytes = generateNonce();
        	
        	XMLGregorianCalendar createdDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        	//Base64 ( SHA1 ( nonce + created + password ) )
        	
        	MessageDigest sha1MessageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM_NAME_SHA_1);
            sha1MessageDigest.update(nonceBytes);
            String  createdDateAsString = createdDate.toString();
            sha1MessageDigest.update(createdDateAsString.getBytes("UTF_8"));
            sha1MessageDigest.update(password.getBytes("UTF_8"));
            passwordDigest = new String(Base64.encodeBase64(sha1MessageDigest.digest()), "UTF-8");
            sha1MessageDigest.reset();
            
            return passwordDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (DatatypeConfigurationException e) {
        	throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }

}