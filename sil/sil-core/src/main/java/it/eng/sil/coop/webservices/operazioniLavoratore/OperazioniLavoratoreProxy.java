package it.eng.sil.coop.webservices.operazioniLavoratore;

public class OperazioniLavoratoreProxy
		implements it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore operazioniLavoratore = null;

	public OperazioniLavoratoreProxy() {
		_initOperazioniLavoratoreProxy();
	}

	public OperazioniLavoratoreProxy(String endpoint) {
		_endpoint = endpoint;
		_initOperazioniLavoratoreProxy();
	}

	private void _initOperazioniLavoratoreProxy() {
		try {
			operazioniLavoratore = (new it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreServiceLocator())
					.getOperazioniLavoratore();
			if (operazioniLavoratore != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) operazioniLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) operazioniLavoratore)
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
		if (operazioniLavoratore != null)
			((javax.xml.rpc.Stub) operazioniLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore getOperazioniLavoratore() {
		if (operazioniLavoratore == null)
			_initOperazioniLavoratoreProxy();
		return operazioniLavoratore;
	}

	public java.lang.String putNotificaAccorpamento(java.lang.String srcxml) throws java.rmi.RemoteException {
		if (operazioniLavoratore == null)
			_initOperazioniLavoratoreProxy();
		return operazioniLavoratore.putNotificaAccorpamento(srcxml);
	}

	public java.lang.String putNotificaIscrizione(java.lang.String srcxml) throws java.rmi.RemoteException {
		if (operazioniLavoratore == null)
			_initOperazioniLavoratoreProxy();
		return operazioniLavoratore.putNotificaIscrizione(srcxml);
	}

}