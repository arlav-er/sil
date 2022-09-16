package it.eng.sil.coop.webservices.art16online.istanze;

public class GestioneIstanzeOnlineProxy
		implements it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline gestioneIstanzeOnline = null;

	public GestioneIstanzeOnlineProxy() {
		_initGestioneIstanzeOnlineProxy();
	}

	public GestioneIstanzeOnlineProxy(String endpoint) {
		_endpoint = endpoint;
		_initGestioneIstanzeOnlineProxy();
	}

	private void _initGestioneIstanzeOnlineProxy() {
		try {
			gestioneIstanzeOnline = (new it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnlineLocator())
					.getGetIstanze();
			if (gestioneIstanzeOnline != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) gestioneIstanzeOnline)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) gestioneIstanzeOnline)
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
		if (gestioneIstanzeOnline != null)
			((javax.xml.rpc.Stub) gestioneIstanzeOnline)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline getGestioneIstanzeOnline() {
		if (gestioneIstanzeOnline == null)
			_initGestioneIstanzeOnlineProxy();
		return gestioneIstanzeOnline;
	}

	public it.eng.sil.coop.webservices.art16online.istanze.ResponseIstanzaArt16 getIstanze(
			it.eng.sil.coop.webservices.art16online.istanze.RequestIstanzaArt16 parameters)
			throws java.rmi.RemoteException {
		if (gestioneIstanzeOnline == null)
			_initGestioneIstanzeOnlineProxy();
		return gestioneIstanzeOnline.getIstanze(parameters);
	}

}