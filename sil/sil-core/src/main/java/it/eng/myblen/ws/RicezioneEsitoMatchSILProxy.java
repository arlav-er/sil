package it.eng.myblen.ws;

public class RicezioneEsitoMatchSILProxy implements it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType {
	private String _endpoint = null;
	private it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType ricezioneEsitoMatchSIL_PortType = null;

	public RicezioneEsitoMatchSILProxy() {
		_initRicezioneEsitoMatchSILProxy();
	}

	public RicezioneEsitoMatchSILProxy(String endpoint) {
		_endpoint = endpoint;
		_initRicezioneEsitoMatchSILProxy();
	}

	private void _initRicezioneEsitoMatchSILProxy() {
		try {
			ricezioneEsitoMatchSIL_PortType = (new it.eng.myblen.ws.RicezioneEsitoMatchSIL_ServiceLocator())
					.getRicezioneEsitoMatchSILPort();
			if (ricezioneEsitoMatchSIL_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) ricezioneEsitoMatchSIL_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) ricezioneEsitoMatchSIL_PortType)
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
		if (ricezioneEsitoMatchSIL_PortType != null)
			((javax.xml.rpc.Stub) ricezioneEsitoMatchSIL_PortType)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType getRicezioneEsitoMatchSIL_PortType() {
		if (ricezioneEsitoMatchSIL_PortType == null)
			_initRicezioneEsitoMatchSILProxy();
		return ricezioneEsitoMatchSIL_PortType;
	}

	public java.lang.String getEsitoMatching(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2,
			java.lang.String arg3) throws java.rmi.RemoteException {
		if (ricezioneEsitoMatchSIL_PortType == null)
			_initRicezioneEsitoMatchSILProxy();
		return ricezioneEsitoMatchSIL_PortType.getEsitoMatching(arg0, arg1, arg2, arg3);
	}

}