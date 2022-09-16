package com.adobe.idp.services;

public class GestioneConsensi_RevocaConsensoProxy implements com.adobe.idp.services.GestioneConsensi_RevocaConsenso {
	private String _endpoint = null;
	private com.adobe.idp.services.GestioneConsensi_RevocaConsenso gestioneConsensi_RevocaConsenso = null;

	public GestioneConsensi_RevocaConsensoProxy() {
		_initGestioneConsensi_RevocaConsensoProxy();
	}

	public GestioneConsensi_RevocaConsensoProxy(String endpoint) {
		_endpoint = endpoint;
		_initGestioneConsensi_RevocaConsensoProxy();
	}

	private void _initGestioneConsensi_RevocaConsensoProxy() {
		try {
			gestioneConsensi_RevocaConsenso = (new com.adobe.idp.services.GestioneConsensi_RevocaConsensoServiceLocator())
					.getRevocaConsenso();
			if (gestioneConsensi_RevocaConsenso != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) gestioneConsensi_RevocaConsenso)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) gestioneConsensi_RevocaConsenso)
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
		if (gestioneConsensi_RevocaConsenso != null)
			((javax.xml.rpc.Stub) gestioneConsensi_RevocaConsenso)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.GestioneConsensi_RevocaConsenso getGestioneConsensi_RevocaConsenso() {
		if (gestioneConsensi_RevocaConsenso == null)
			_initGestioneConsensi_RevocaConsensoProxy();
		return gestioneConsensi_RevocaConsenso;
	}

	public com.adobe.idp.services.XML invoke(java.lang.String in_string_codicefiscale) throws java.rmi.RemoteException {
		if (gestioneConsensi_RevocaConsenso == null)
			_initGestioneConsensi_RevocaConsensoProxy();
		return gestioneConsensi_RevocaConsenso.invoke(in_string_codicefiscale);
	}

}