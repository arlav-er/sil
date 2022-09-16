package com.adobe.idp.services;

public class GestioneConsensi_InserimentoConsensoProxy
		implements com.adobe.idp.services.GestioneConsensi_InserimentoConsenso {
	private String _endpoint = null;
	private com.adobe.idp.services.GestioneConsensi_InserimentoConsenso gestioneConsensi_InserimentoConsenso = null;

	public GestioneConsensi_InserimentoConsensoProxy() {
		_initGestioneConsensi_InserimentoConsensoProxy();
	}

	public GestioneConsensi_InserimentoConsensoProxy(String endpoint) {
		_endpoint = endpoint;
		_initGestioneConsensi_InserimentoConsensoProxy();
	}

	private void _initGestioneConsensi_InserimentoConsensoProxy() {
		try {
			gestioneConsensi_InserimentoConsenso = (new com.adobe.idp.services.GestioneConsensi_InserimentoConsensoServiceLocator())
					.getInserimentoConsenso();
			if (gestioneConsensi_InserimentoConsenso != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) gestioneConsensi_InserimentoConsenso)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) gestioneConsensi_InserimentoConsenso)
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
		if (gestioneConsensi_InserimentoConsenso != null)
			((javax.xml.rpc.Stub) gestioneConsensi_InserimentoConsenso)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.GestioneConsensi_InserimentoConsenso getGestioneConsensi_InserimentoConsenso() {
		if (gestioneConsensi_InserimentoConsenso == null)
			_initGestioneConsensi_InserimentoConsensoProxy();
		return gestioneConsensi_InserimentoConsenso;
	}

	public com.adobe.idp.services.XML invoke(java.lang.String in_string_codicefiscale,
			java.lang.String in_string_operatore, java.lang.String in_string_sistemaOrigine)
			throws java.rmi.RemoteException {
		if (gestioneConsensi_InserimentoConsenso == null)
			_initGestioneConsensi_InserimentoConsensoProxy();
		return gestioneConsensi_InserimentoConsenso.invoke(in_string_codicefiscale, in_string_operatore,
				in_string_sistemaOrigine);
	}

}