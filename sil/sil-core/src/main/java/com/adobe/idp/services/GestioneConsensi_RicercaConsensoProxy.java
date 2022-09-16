package com.adobe.idp.services;

public class GestioneConsensi_RicercaConsensoProxy implements com.adobe.idp.services.GestioneConsensi_RicercaConsenso {
	private String _endpoint = null;
	private com.adobe.idp.services.GestioneConsensi_RicercaConsenso gestioneConsensi_RicercaConsenso = null;

	public GestioneConsensi_RicercaConsensoProxy() {
		_initGestioneConsensi_RicercaConsensoProxy();
	}

	public GestioneConsensi_RicercaConsensoProxy(String endpoint) {
		_endpoint = endpoint;
		_initGestioneConsensi_RicercaConsensoProxy();
	}

	private void _initGestioneConsensi_RicercaConsensoProxy() {
		try {
			gestioneConsensi_RicercaConsenso = (new com.adobe.idp.services.GestioneConsensi_RicercaConsensoServiceLocator())
					.getRicercaConsenso();
			if (gestioneConsensi_RicercaConsenso != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) gestioneConsensi_RicercaConsenso)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) gestioneConsensi_RicercaConsenso)
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
		if (gestioneConsensi_RicercaConsenso != null)
			((javax.xml.rpc.Stub) gestioneConsensi_RicercaConsenso)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.GestioneConsensi_RicercaConsenso getGestioneConsensi_RicercaConsenso() {
		if (gestioneConsensi_RicercaConsenso == null)
			_initGestioneConsensi_RicercaConsensoProxy();
		return gestioneConsensi_RicercaConsenso;
	}

	public com.adobe.idp.services.XML invoke(java.lang.String in_string_codicefiscale) throws java.rmi.RemoteException {
		if (gestioneConsensi_RicercaConsenso == null)
			_initGestioneConsensi_RicercaConsensoProxy();
		return gestioneConsensi_RicercaConsenso.invoke(in_string_codicefiscale);
	}

}