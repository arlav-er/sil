package it.gov.lavoro.servizi.EventiCondizionalitaRDC;

public class EventiCondizionalitaRDCWSProxy
		implements it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS {
	private String _endpoint = null;
	private it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS eventiCondizionalitaRDCWS = null;

	public EventiCondizionalitaRDCWSProxy() {
		_initEventiCondizionalitaRDCWSProxy();
	}

	public EventiCondizionalitaRDCWSProxy(String endpoint) {
		_endpoint = endpoint;
		_initEventiCondizionalitaRDCWSProxy();
	}

	private void _initEventiCondizionalitaRDCWSProxy() {
		try {
			eventiCondizionalitaRDCWS = (new it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSServiceLocator())
					.getEventiCondizionalitaRDCWS();
			if (eventiCondizionalitaRDCWS != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) eventiCondizionalitaRDCWS)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) eventiCondizionalitaRDCWS)
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
		if (eventiCondizionalitaRDCWS != null)
			((javax.xml.rpc.Stub) eventiCondizionalitaRDCWS)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS getEventiCondizionalitaRDCWS() {
		if (eventiCondizionalitaRDCWS == null)
			_initEventiCondizionalitaRDCWSProxy();
		return eventiCondizionalitaRDCWS;
	}

	public void loadEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Calendar dtt_domanda,
			java.util.Calendar dtt_evento, java.lang.String txt_note, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException {
		if (eventiCondizionalitaRDCWS == null)
			_initEventiCondizionalitaRDCWSProxy();
		eventiCondizionalitaRDCWS.loadEventiCondizionalitaRDC(cod_cpi, cod_evento, cod_fiscale, cod_fiscale_ope,
				cod_protocollo_inps, dtt_domanda, dtt_evento, txt_note, cod_esito, des_esito);
	}

	public void deleteEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Date dtt_domanda,
			java.util.Date dtt_evento, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException {
		if (eventiCondizionalitaRDCWS == null)
			_initEventiCondizionalitaRDCWSProxy();
		eventiCondizionalitaRDCWS.deleteEventiCondizionalitaRDC(cod_cpi, cod_evento, cod_fiscale, cod_fiscale_ope,
				cod_protocollo_inps, dtt_domanda, dtt_evento, cod_esito, des_esito);
	}

}