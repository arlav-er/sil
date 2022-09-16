package it.eng.myportal.ws;

public class DataAdesioneGOProxy implements it.eng.myportal.ws.DataAdesioneGO_PortType {
	private String _endpoint = null;
	private it.eng.myportal.ws.DataAdesioneGO_PortType dataAdesioneGO_PortType = null;

	public DataAdesioneGOProxy() {
		_initDataAdesioneGOProxy();
	}

	public DataAdesioneGOProxy(String endpoint) {
		_endpoint = endpoint;
		_initDataAdesioneGOProxy();
	}

	private void _initDataAdesioneGOProxy() {
		try {
			dataAdesioneGO_PortType = (new it.eng.myportal.ws.DataAdesioneGO_ServiceLocator()).getDataAdesioneGOPort();
			if (dataAdesioneGO_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) dataAdesioneGO_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) dataAdesioneGO_PortType)
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
		if (dataAdesioneGO_PortType != null)
			((javax.xml.rpc.Stub) dataAdesioneGO_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.myportal.ws.DataAdesioneGO_PortType getDataAdesioneGO_PortType() {
		if (dataAdesioneGO_PortType == null)
			_initDataAdesioneGOProxy();
		return dataAdesioneGO_PortType;
	}

	public java.lang.String getDataAdesioneGO(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (dataAdesioneGO_PortType == null)
			_initDataAdesioneGOProxy();
		return dataAdesioneGO_PortType.getDataAdesioneGO(arg0, arg1, arg2);
	}

}