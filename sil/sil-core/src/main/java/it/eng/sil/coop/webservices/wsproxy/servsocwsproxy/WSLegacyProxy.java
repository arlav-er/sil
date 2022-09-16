package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class WSLegacyProxy implements it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacy {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacy wSLegacy = null;

	public WSLegacyProxy() {
		_initWSLegacyProxy();
	}

	public WSLegacyProxy(String endpoint) {
		_endpoint = endpoint;
		_initWSLegacyProxy();
	}

	private void _initWSLegacyProxy() {
		try {
			wSLegacy = (new it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacyServiceLocator())
					.getWSLegacyPort();
			if (wSLegacy != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) wSLegacy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) wSLegacy)
							._getProperty("javax.xml.rpc.service.endpoint.address");
			}

		} catch (javax.xml.rpc.ServiceException serviceException) {
		}
	}

	public String getEndpoint() {
		return _endpoint;
	}

	public void setEndpoint(String endpoint) {
		_endpoint = endpoint;
		if (wSLegacy != null)
			((javax.xml.rpc.Stub) wSLegacy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacy getWSLegacy() {
		if (wSLegacy == null)
			_initWSLegacyProxy();
		return wSLegacy;
	}

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Risposta getInfo(
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Richiesta input) throws java.rmi.RemoteException {
		if (wSLegacy == null)
			_initWSLegacyProxy();
		return wSLegacy.getInfo(input);
	}

	public java.lang.String getVersion() throws java.rmi.RemoteException {
		if (wSLegacy == null)
			_initWSLegacyProxy();
		return wSLegacy.getVersion();
	}

}