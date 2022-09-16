package it.eng.sil.coop.webservices.myportal.ws;

public class RicezioneVacancyProxy implements it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy ricezioneVacancy = null;

	public RicezioneVacancyProxy() {
		_initRicezioneVacancyProxy();
	}

	public RicezioneVacancyProxy(String endpoint) {
		_endpoint = endpoint;
		_initRicezioneVacancyProxy();
	}

	private void _initRicezioneVacancyProxy() {
		try {
			ricezioneVacancy = (new it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyServiceLocator())
					.getRicezioneVacancyPort();
			if (ricezioneVacancy != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) ricezioneVacancy)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) ricezioneVacancy)
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
		if (ricezioneVacancy != null)
			((javax.xml.rpc.Stub) ricezioneVacancy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy getRicezioneVacancy() {
		if (ricezioneVacancy == null)
			_initRicezioneVacancyProxy();
		return ricezioneVacancy;
	}

	public java.lang.String inserisciVacancy(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (ricezioneVacancy == null)
			_initRicezioneVacancyProxy();
		return ricezioneVacancy.inserisciVacancy(arg0, arg1, arg2);
	}

}