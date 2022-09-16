package it.gov.lavoro.servizi.servizicoapYg;

public class ServizicoapWSProxy implements it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS {
	private String _endpoint = null;
	private it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS servizicoapWS = null;

	public ServizicoapWSProxy() {
		_initServizicoapWSProxy();
	}

	public ServizicoapWSProxy(String endpoint) {
		_endpoint = endpoint;
		_initServizicoapWSProxy();
	}

	private void _initServizicoapWSProxy() {
		try {
			servizicoapWS = (new it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSServiceLocator()).getservizicoapWS();
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

	public it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS getServizicoapWS() {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS;
	}

	public void invioUtenteYG(java.lang.String utenteYG,
			it.gov.lavoro.servizi.servizicoapYg.types.holders.Risposta_invioUtenteYG_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.invioUtenteYG(utenteYG, esito, messaggioErrore);
	}

	public void checkUtenteYG(java.lang.String codiceFiscale,
			it.gov.lavoro.servizi.servizicoapYg.types.holders.Risposta_checkUtenteYG_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.checkUtenteYG(codiceFiscale, esito, messaggioErrore);
	}

}