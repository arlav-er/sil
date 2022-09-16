package it.eng.sil.coop.webservices.seta.ws;

public class Ws_RendicontazioneProxy implements it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType ws_Rendicontazione_PortType = null;

	public Ws_RendicontazioneProxy() {
		_initWs_RendicontazioneProxy();
	}

	public Ws_RendicontazioneProxy(String endpoint) {
		_endpoint = endpoint;
		_initWs_RendicontazioneProxy();
	}

	private void _initWs_RendicontazioneProxy() {
		try {
			ws_Rendicontazione_PortType = (new it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_ServiceLocator())
					.getWs_RendicontazionePort();
			if (ws_Rendicontazione_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) ws_Rendicontazione_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) ws_Rendicontazione_PortType)
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
		if (ws_Rendicontazione_PortType != null)
			((javax.xml.rpc.Stub) ws_Rendicontazione_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType getWs_Rendicontazione_PortType() {
		if (ws_Rendicontazione_PortType == null)
			_initWs_RendicontazioneProxy();
		return ws_Rendicontazione_PortType;
	}

	public it.eng.sil.coop.webservices.seta.ws.XmlResult verifyXml(java.lang.String user, java.lang.String password,
			java.lang.String fileName, java.lang.String file) throws java.rmi.RemoteException {
		if (ws_Rendicontazione_PortType == null)
			_initWs_RendicontazioneProxy();
		return ws_Rendicontazione_PortType.verifyXml(user, password, fileName, file);
	}

}