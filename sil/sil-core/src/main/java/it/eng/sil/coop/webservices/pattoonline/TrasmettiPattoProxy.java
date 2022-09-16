package it.eng.sil.coop.webservices.pattoonline;

public class TrasmettiPattoProxy implements it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto trasmettiPatto = null;

	public TrasmettiPattoProxy() {
		_initTrasmettiPattoProxy();
	}

	public TrasmettiPattoProxy(String endpoint) {
		_endpoint = endpoint;
		_initTrasmettiPattoProxy();
	}

	private void _initTrasmettiPattoProxy() {
		try {
			trasmettiPatto = (new it.eng.sil.coop.webservices.pattoonline.TrasmettiPattoServiceLocator())
					.getTrasmettiPatto();
			if (trasmettiPatto != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) trasmettiPatto)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) trasmettiPatto)
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
		if (trasmettiPatto != null)
			((javax.xml.rpc.Stub) trasmettiPatto)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.pattoonline.TrasmettiPatto getTrasmettiPatto() {
		if (trasmettiPatto == null)
			_initTrasmettiPattoProxy();
		return trasmettiPatto;
	}

	public it.eng.sil.coop.webservices.pattoonline.EsitoType aggiornaPatto(
			it.eng.sil.coop.webservices.pattoonline.PattoAccettatoType richiesta) throws java.rmi.RemoteException {
		if (trasmettiPatto == null)
			_initTrasmettiPattoProxy();
		return trasmettiPatto.aggiornaPatto(richiesta);
	}

}