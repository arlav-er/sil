package it.gov.lavoro.servizi.serviceRDC;

public class RDCProxy implements it.gov.lavoro.servizi.serviceRDC.RDC {
	private String _endpoint = null;
	private it.gov.lavoro.servizi.serviceRDC.RDC rDC = null;

	public RDCProxy() {
		_initRDCProxy();
	}

	public RDCProxy(String endpoint) {
		_endpoint = endpoint;
		_initRDCProxy();
	}

	private void _initRDCProxy() {
		try {
			rDC = (new it.gov.lavoro.servizi.serviceRDC.RDCServiceLocator()).getRDC();
			if (rDC != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) rDC)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) rDC)
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
		if (rDC != null)
			((javax.xml.rpc.Stub) rDC)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.gov.lavoro.servizi.serviceRDC.RDC getRDC() {
		if (rDC == null)
			_initRDCProxy();
		return rDC;
	}

	public it.gov.lavoro.servizi.serviceRDC.types.Risposta_servizio_RDC_Type ricevi_RDC_by_codiceFiscale(
			it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_CodiceFiscale parameters)
			throws java.rmi.RemoteException {
		if (rDC == null)
			_initRDCProxy();
		return rDC.ricevi_RDC_by_codiceFiscale(parameters);
	}

	public it.gov.lavoro.servizi.serviceRDC.types.Risposta_servizio_RDC_Type ricevi_RDC_by_codProtocolloInps(
			it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_codProtocolloInps parameters)
			throws java.rmi.RemoteException {
		if (rDC == null)
			_initRDCProxy();
		return rDC.ricevi_RDC_by_codProtocolloInps(parameters);
	}

}