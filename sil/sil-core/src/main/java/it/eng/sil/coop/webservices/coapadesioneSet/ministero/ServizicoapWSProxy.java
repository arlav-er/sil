package it.eng.sil.coop.webservices.coapadesioneSet.ministero;

public class ServizicoapWSProxy implements it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWS {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWS servizicoapWS = null;

	public ServizicoapWSProxy() {
		_initServizicoapWSProxy();
	}

	public ServizicoapWSProxy(String endpoint) {
		_endpoint = endpoint;
		_initServizicoapWSProxy();
	}

	private void _initServizicoapWSProxy() {
		try {
			servizicoapWS = (new it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWSServiceLocator())
					.getservizicoapWS();
			if (servizicoapWS != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) servizicoapWS)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) servizicoapWS)
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
		if (servizicoapWS != null)
			((javax.xml.rpc.Stub) servizicoapWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.coapadesioneSet.ministero.ServizicoapWS getServizicoapWS() {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS;
	}

	public void setStatoAdesioneYG(java.lang.String datiStatoAdesione,
			it.eng.sil.coop.webservices.coapadesioneSet.ministero.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.setStatoAdesioneYG(datiStatoAdesione, esito, messaggioErrore);
	}

}