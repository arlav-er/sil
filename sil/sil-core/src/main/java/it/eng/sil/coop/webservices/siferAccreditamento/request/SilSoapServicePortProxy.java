package it.eng.sil.coop.webservices.siferAccreditamento.request;

public class SilSoapServicePortProxy
		implements it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort silSoapServicePort = null;

	public SilSoapServicePortProxy() {
		_initSilSoapServicePortProxy();
	}

	public SilSoapServicePortProxy(String endpoint) {
		_endpoint = endpoint;
		_initSilSoapServicePortProxy();
	}

	private void _initSilSoapServicePortProxy() {
		try {
			silSoapServicePort = (new it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceServiceLocator())
					.getSilSoapServicePort();
			if (silSoapServicePort != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) silSoapServicePort)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) silSoapServicePort)
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
		if (silSoapServicePort != null)
			((javax.xml.rpc.Stub) silSoapServicePort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort getSilSoapServicePort() {
		if (silSoapServicePort == null)
			_initSilSoapServicePortProxy();
		return silSoapServicePort;
	}

	public java.lang.Object request(it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante partecipante,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[] patti) throws java.rmi.RemoteException {
		if (silSoapServicePort == null)
			_initSilSoapServicePortProxy();
		return silSoapServicePort.request(partecipante, patti);
	}

}