package it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino;

public class GetCittadinoProxy
		implements it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_PortType getCittadino_PortType = null;

	public GetCittadinoProxy() {
		_initGetCittadinoProxy();
	}

	public GetCittadinoProxy(String endpoint) {
		_endpoint = endpoint;
		_initGetCittadinoProxy();
	}

	private void _initGetCittadinoProxy() {
		try {
			getCittadino_PortType = (new it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_ServiceLocator())
					.getGetCittadino();
			if (getCittadino_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) getCittadino_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) getCittadino_PortType)
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
		if (getCittadino_PortType != null)
			((javax.xml.rpc.Stub) getCittadino_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.myportal.servizicittadino.ws.getcittadino.GetCittadino_PortType getGetCittadino_PortType() {
		if (getCittadino_PortType == null)
			_initGetCittadinoProxy();
		return getCittadino_PortType;
	}

	public java.lang.String getAccountCittadino(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (getCittadino_PortType == null)
			_initGetCittadinoProxy();
		return getCittadino_PortType.getAccountCittadino(arg0, arg1, arg2);
	}

	public java.lang.String getDettaglioCittadino(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (getCittadino_PortType == null)
			_initGetCittadinoProxy();
		return getCittadino_PortType.getDettaglioCittadino(arg0, arg1, arg2);
	}

}