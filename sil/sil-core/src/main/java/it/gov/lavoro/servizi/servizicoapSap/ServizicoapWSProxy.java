package it.gov.lavoro.servizi.servizicoapSap;

public class ServizicoapWSProxy implements it.gov.lavoro.servizi.servizicoapSap.ServizicoapWS {
	private String _endpoint = null;
	private it.gov.lavoro.servizi.servizicoapSap.ServizicoapWS servizicoapWS = null;

	public ServizicoapWSProxy() {
		_initServizicoapWSProxy();
	}

	public ServizicoapWSProxy(String endpoint) {
		_endpoint = endpoint;
		_initServizicoapWSProxy();
	}

	private void _initServizicoapWSProxy() {
		try {
			servizicoapWS = (new it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSServiceLocator()).getservizicoapWS();
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

	public it.gov.lavoro.servizi.servizicoapSap.ServizicoapWS getServizicoapWS() {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS;
	}

	public void invioSAP(java.lang.String SAP,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore, javax.xml.rpc.holders.StringHolder codiceSAP)
			throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.invioSAP(SAP, esito, messaggioErrore, codiceSAP);
	}

	public java.lang.String richiestaSAP(java.lang.String codiceSAP) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS.richiestaSAP(codiceSAP);
	}

	public java.lang.String[] recuperaListaSAPNonAttive(java.lang.String parametri) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS.recuperaListaSAPNonAttive(parametri);
	}

	public java.lang.String verificaEsistenzaSAP(java.lang.String codiceFiscale) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		return servizicoapWS.verificaEsistenzaSAP(codiceFiscale);
	}

	public void annullaSAP(java.lang.String codiceSAP,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_annullaSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.annullaSAP(codiceSAP, esito, messaggioErrore);
	}

	public void notificaSAP(java.lang.String codiceSAP, java.lang.String motivoNotifica,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_notificaSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (servizicoapWS == null)
			_initServizicoapWSProxy();
		servizicoapWS.notificaSAP(codiceSAP, motivoNotifica, esito, messaggioErrore);
	}

}