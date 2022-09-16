package it.gov.lavoro.servizi.RicercaCO._2_0;

public class IRicercaCOProxy implements it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO {
	private String _endpoint = null;
	private it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO iRicercaCO = null;

	public IRicercaCOProxy() {
		_initIRicercaCOProxy();
	}

	public IRicercaCOProxy(String endpoint) {
		_endpoint = endpoint;
		_initIRicercaCOProxy();
	}

	private void _initIRicercaCOProxy() {
		try {
			iRicercaCO = (new it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsQSServiceLocator())
					.getbasicHttpBindingSettingsQSPort();
			if (iRicercaCO != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) iRicercaCO)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) iRicercaCO)
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
		if (iRicercaCO != null)
			((javax.xml.rpc.Stub) iRicercaCO)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO getIRicercaCO() {
		if (iRicercaCO == null)
			_initIRicercaCOProxy();
		return iRicercaCO;
	}

	public it.gov.lavoro.servizi.RicercaCO._2_0.COPerLavoratoreResponseCOPerLavoratoreResult COPerLavoratore(
			it.gov.lavoro.servizi.RicercaCO._2_0.CodiceFiscaleSoggettoFisico_Type codiceFiscale,
			java.util.Date dataInizio, java.util.Date dataFine) throws java.rmi.RemoteException {
		if (iRicercaCO == null)
			_initIRicercaCOProxy();
		return iRicercaCO.COPerLavoratore(codiceFiscale, dataInizio, dataFine);
	}

}