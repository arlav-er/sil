/*
 * Created on Apr 19, 2007
 *
 */
package it.eng.sil.coop.wsClient.docareaProto;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author savino
 */
public class DocAreaWSConfig {

	public static final String TARGET_NAMESPACE_RESPONSE_TYPE = "PROTOCOLLO.DOCAREA.WS.TARGET_NAMESPACE_RESPONSE_TYPE";
	public static final String TARGET_NAMESPACE = "PROTOCOLLO.DOCAREA.WS.TARGET_NAMESPACE";

	private static final String targetNamespaceResponseType = "http://tempuri.org/";
	private static final String targetNamespace = "http://tempuri.org/";

	/**
	 * Recupera dal configuratore xml (file ProtocolloDocumento.xml) il targetNamaspace <b>del tipo di ritorno</b> del
	 * wsdl usato dal web service che si sta chiamando.<br>
	 * E' possibile che ogni provincia abbia un web service docarea con namespace propri diversi da quelli standard.
	 * 
	 * @return il namespace del tipo di ritorno specificato nel configuratore oppure il default "http://tempuri.org/"
	 */
	public static String getTargetNamespaceResponseType() {
		String s = (String) ConfigSingleton.getInstance().getAttribute(TARGET_NAMESPACE_RESPONSE_TYPE);
		if (s == null)
			s = targetNamespaceResponseType;
		return s;
	}

	/**
	 * Recupera dal configuratore xml (file ProtocolloDocumento.xml) il targetNamaspace del wsdl usato dal web service
	 * che si sta chiamando.<br>
	 * E' possibile che ogni provincia abbia un web service docarea con namespace propri diversi da quelli standard.
	 * 
	 * @return il namespace specificato nel configuratore oppure il default "http://tempuri.org/"
	 */
	public static String getTargetNamespace() {
		String s = (String) ConfigSingleton.getInstance().getAttribute(TARGET_NAMESPACE);
		if (s == null)
			s = targetNamespace;
		return s;
	}

}
