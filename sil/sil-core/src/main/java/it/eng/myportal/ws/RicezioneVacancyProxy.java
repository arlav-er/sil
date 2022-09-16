package it.eng.myportal.ws;

public class RicezioneVacancyProxy implements it.eng.myportal.ws.RicezioneVacancy_PortType {
	private String _endpoint = null;
	private it.eng.myportal.ws.RicezioneVacancy_PortType ricezioneVacancy_PortType = null;

	public RicezioneVacancyProxy() {
		_initRicezioneVacancyProxy();
	}

	public RicezioneVacancyProxy(String endpoint) {
		_endpoint = endpoint;
		_initRicezioneVacancyProxy();
	}

	private void _initRicezioneVacancyProxy() {
		try {
			ricezioneVacancy_PortType = (new it.eng.myportal.ws.RicezioneVacancy_ServiceLocator())
					.getRicezioneVacancyPort();
			if (ricezioneVacancy_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) ricezioneVacancy_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) ricezioneVacancy_PortType)
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
		if (ricezioneVacancy_PortType != null)
			((javax.xml.rpc.Stub) ricezioneVacancy_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.myportal.ws.RicezioneVacancy_PortType getRicezioneVacancy_PortType() {
		if (ricezioneVacancy_PortType == null)
			_initRicezioneVacancyProxy();
		return ricezioneVacancy_PortType;
	}

	public java.lang.String inserisciVacancySil(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (ricezioneVacancy_PortType == null)
			_initRicezioneVacancyProxy();
		return ricezioneVacancy_PortType.inserisciVacancySil(arg0, arg1, arg2);
	}

	public java.lang.String inserisciVacancy(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (ricezioneVacancy_PortType == null)
			_initRicezioneVacancyProxy();
		return ricezioneVacancy_PortType.inserisciVacancy(arg0, arg1, arg2);
	}

}