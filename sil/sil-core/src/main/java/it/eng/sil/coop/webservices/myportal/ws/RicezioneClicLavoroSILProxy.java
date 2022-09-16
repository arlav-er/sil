package it.eng.sil.coop.webservices.myportal.ws;

public class RicezioneClicLavoroSILProxy
		implements it.eng.sil.coop.webservices.myportal.ws.RicezioneClicLavoroSIL_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.myportal.ws.RicezioneClicLavoroSIL_PortType ricezioneClicLavoroSIL_PortType = null;

	public RicezioneClicLavoroSILProxy() {
		_initRicezioneClicLavoroSILProxy();
	}

	public RicezioneClicLavoroSILProxy(String endpoint) {
		_endpoint = endpoint;
		_initRicezioneClicLavoroSILProxy();
	}

	private void _initRicezioneClicLavoroSILProxy() {
		try {
			ricezioneClicLavoroSIL_PortType = (new it.eng.sil.coop.webservices.myportal.ws.RicezioneClicLavoroSIL_ServiceLocator())
					.getRicezioneClicLavoroSILPort();
			if (ricezioneClicLavoroSIL_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) ricezioneClicLavoroSIL_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) ricezioneClicLavoroSIL_PortType)
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
		if (ricezioneClicLavoroSIL_PortType != null)
			((javax.xml.rpc.Stub) ricezioneClicLavoroSIL_PortType)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.myportal.ws.RicezioneClicLavoroSIL_PortType getRicezioneClicLavoroSIL_PortType() {
		if (ricezioneClicLavoroSIL_PortType == null)
			_initRicezioneClicLavoroSILProxy();
		return ricezioneClicLavoroSIL_PortType;
	}

	public java.lang.String inserisciCandidatura(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (ricezioneClicLavoroSIL_PortType == null)
			_initRicezioneClicLavoroSILProxy();
		return ricezioneClicLavoroSIL_PortType.inserisciCandidatura(arg0, arg1, arg2);
	}

	public java.lang.String inserisciVacancy(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (ricezioneClicLavoroSIL_PortType == null)
			_initRicezioneClicLavoroSILProxy();
		return ricezioneClicLavoroSIL_PortType.inserisciVacancy(arg0, arg1, arg2);
	}

}