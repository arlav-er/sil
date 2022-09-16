package it.eng.sil.coop.webservices.siferPartecipanteTirocini;

public class PortTypeProxy implements it.eng.sil.coop.webservices.siferPartecipanteTirocini.PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.siferPartecipanteTirocini.PortType portType = null;

	public PortTypeProxy() {
		_initPortTypeProxy();
	}

	public PortTypeProxy(String endpoint) {
		_endpoint = endpoint;
		_initPortTypeProxy();
	}

	private void _initPortTypeProxy() {
		try {
			portType = (new it.eng.sil.coop.webservices.siferPartecipanteTirocini.ServiceLocator()).getrequestService();
			if (portType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) portType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) portType)
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
		if (portType != null)
			((javax.xml.rpc.Stub) portType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.siferPartecipanteTirocini.PortType getPortType() {
		if (portType == null)
			_initPortTypeProxy();
		return portType;
	}

	public java.lang.String requestService(it.eng.sil.coop.webservices.siferPartecipanteTirocini.Request request)
			throws java.rmi.RemoteException {
		if (portType == null)
			_initPortTypeProxy();
		return portType.requestService(request);
	}

}