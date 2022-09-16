package it.eng.sil.coop.webservices.dataAdesione;

public class DataAdesioneYGProxy implements it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType dataAdesioneYG_PortType = null;

	public DataAdesioneYGProxy() {
		_initDataAdesioneYGProxy();
	}

	public DataAdesioneYGProxy(String endpoint) {
		_endpoint = endpoint;
		_initDataAdesioneYGProxy();
	}

	private void _initDataAdesioneYGProxy() {
		try {
			dataAdesioneYG_PortType = (new it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_ServiceLocator())
					.getDataAdesioneYGPort();
			if (dataAdesioneYG_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) dataAdesioneYG_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) dataAdesioneYG_PortType)
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
		if (dataAdesioneYG_PortType != null)
			((javax.xml.rpc.Stub) dataAdesioneYG_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType getDataAdesioneYG_PortType() {
		if (dataAdesioneYG_PortType == null)
			_initDataAdesioneYGProxy();
		return dataAdesioneYG_PortType;
	}

	public java.lang.String getDataAdesioneYG(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (dataAdesioneYG_PortType == null)
			_initDataAdesioneYGProxy();
		return dataAdesioneYG_PortType.getDataAdesioneYG(arg0, arg1, arg2);
	}

}