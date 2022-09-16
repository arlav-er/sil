package it.eng.myportal.ws.pattoonline;

public class GestionePattoOnlineProxy implements it.eng.myportal.ws.pattoonline.GestionePattoOnline {
	private String _endpoint = null;
	private it.eng.myportal.ws.pattoonline.GestionePattoOnline gestionePattoOnline = null;

	public GestionePattoOnlineProxy() {
		_initGestionePattoOnlineProxy();
	}

	public GestionePattoOnlineProxy(String endpoint) {
		_endpoint = endpoint;
		_initGestionePattoOnlineProxy();
	}

	private void _initGestionePattoOnlineProxy() {
		try {
			gestionePattoOnline = (new it.eng.myportal.ws.pattoonline.PattoOnlineLocator()).getGestionePattoPort();
			if (gestionePattoOnline != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) gestionePattoOnline)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) gestionePattoOnline)
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
		if (gestionePattoOnline != null)
			((javax.xml.rpc.Stub) gestionePattoOnline)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.myportal.ws.pattoonline.GestionePattoOnline getGestionePattoOnline() {
		if (gestionePattoOnline == null)
			_initGestionePattoOnlineProxy();
		return gestionePattoOnline;
	}

	public void invioPatto(it.eng.myportal.ws.pattoonline.PattoType patto, byte[] PDFPatto,
			javax.xml.rpc.holders.StringHolder esito, javax.xml.rpc.holders.StringHolder descrizione)
			throws java.rmi.RemoteException {
		if (gestionePattoOnline == null)
			_initGestionePattoOnlineProxy();
		gestionePattoOnline.invioPatto(patto, PDFPatto, esito, descrizione);
	}

	public it.eng.myportal.ws.pattoonline.PattoPortaleType richiestaPatto(
			it.eng.myportal.ws.pattoonline.PattoType patto) throws java.rmi.RemoteException {
		if (gestionePattoOnline == null)
			_initGestionePattoOnlineProxy();
		return gestionePattoOnline.richiestaPatto(patto);
	}

}