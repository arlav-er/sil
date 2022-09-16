package it.eng.sil.coop.webservices.nuovoRedditoAttivazione;

public class NuovoRedditoAttivazioneProxy
		implements it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazione_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazione_PortType nuovoRedditoAttivazione_PortType = null;

	public NuovoRedditoAttivazioneProxy() {
		_initNuovoRedditoAttivazioneProxy();
	}

	public NuovoRedditoAttivazioneProxy(String endpoint) {
		_endpoint = endpoint;
		_initNuovoRedditoAttivazioneProxy();
	}

	private void _initNuovoRedditoAttivazioneProxy() {
		try {
			nuovoRedditoAttivazione_PortType = (new it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazione_ServiceLocator())
					.getNuovoRedditoAttivazione();
			if (nuovoRedditoAttivazione_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) nuovoRedditoAttivazione_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) nuovoRedditoAttivazione_PortType)
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
		if (nuovoRedditoAttivazione_PortType != null)
			((javax.xml.rpc.Stub) nuovoRedditoAttivazione_PortType)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazione_PortType getNuovoRedditoAttivazione_PortType() {
		if (nuovoRedditoAttivazione_PortType == null)
			_initNuovoRedditoAttivazioneProxy();
		return nuovoRedditoAttivazione_PortType;
	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneDomanda(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ValidazioneDomandaNraType richiesta_RichiestaRispostaSincrona_validazioneDomanda)
			throws java.rmi.RemoteException {
		if (nuovoRedditoAttivazione_PortType == null)
			_initNuovoRedditoAttivazioneProxy();
		return nuovoRedditoAttivazione_PortType
				.validazioneDomanda(richiesta_RichiestaRispostaSincrona_validazioneDomanda);
	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType comunicazioneVariazioneResidenza(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType richiesta_RichiestaRispostaSincrona_comunicazioneVariazioneResidenza)
			throws java.rmi.RemoteException {
		if (nuovoRedditoAttivazione_PortType == null)
			_initNuovoRedditoAttivazioneProxy();
		return nuovoRedditoAttivazione_PortType
				.comunicazioneVariazioneResidenza(richiesta_RichiestaRispostaSincrona_comunicazioneVariazioneResidenza);
	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneComunicazioniSuccessive(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType richiesta_RichiestaRispostaSincrona_validazioneComunicazioniSuccessive)
			throws java.rmi.RemoteException {
		if (nuovoRedditoAttivazione_PortType == null)
			_initNuovoRedditoAttivazioneProxy();
		return nuovoRedditoAttivazione_PortType.validazioneComunicazioniSuccessive(
				richiesta_RichiestaRispostaSincrona_validazioneComunicazioniSuccessive);
	}

}