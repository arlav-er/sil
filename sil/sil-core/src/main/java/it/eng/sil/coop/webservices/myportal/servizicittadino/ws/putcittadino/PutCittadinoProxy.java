package it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino;

public class PutCittadinoProxy
		implements it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_PortType putCittadino_PortType = null;

	public PutCittadinoProxy() {
		_initPutCittadinoProxy();
	}

	public PutCittadinoProxy(String endpoint) {
		_endpoint = endpoint;
		_initPutCittadinoProxy();
	}

	private void _initPutCittadinoProxy() {
		try {
			putCittadino_PortType = (new it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_ServiceLocator())
					.getPutCittadino();
			if (putCittadino_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) putCittadino_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) putCittadino_PortType)
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
		if (putCittadino_PortType != null)
			((javax.xml.rpc.Stub) putCittadino_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.myportal.servizicittadino.ws.putcittadino.PutCittadino_PortType getPutCittadino_PortType() {
		if (putCittadino_PortType == null)
			_initPutCittadinoProxy();
		return putCittadino_PortType;
	}

	public java.lang.String reinvioMailAccreditamento(java.lang.String arg0, java.lang.String arg1,
			java.lang.String arg2) throws java.rmi.RemoteException {
		if (putCittadino_PortType == null)
			_initPutCittadinoProxy();
		return putCittadino_PortType.reinvioMailAccreditamento(arg0, arg1, arg2);
	}

	public java.lang.String putAccountCittadino(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (putCittadino_PortType == null)
			_initPutCittadinoProxy();
		return putCittadino_PortType.putAccountCittadino(arg0, arg1, arg2);
	}

}