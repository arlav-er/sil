package com.adobe.idp.services;

public class ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy
		implements com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo {
	private String _endpoint = null;
	private com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo = null;

	public ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy() {
		_initModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy();
	}

	public ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy(String endpoint) {
		_endpoint = endpoint;
		_initModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy();
	}

	private void _initModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy() {
		try {
			modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo = (new com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator())
					.getGetAttachmentsListByProtocollo();
			if (modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo)
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
		if (modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo != null)
			((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo getModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo() {
		if (modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo == null)
			_initModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy();
		return modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo;
	}

	public void invoke(java.lang.String in_string_annoprotocollo, java.lang.String in_string_formid,
			java.lang.String in_string_nprotocollo, com.adobe.idp.services.holders.XMLHolder out_xml_data,
			com.adobe.idp.services.holders.XMLHolder out_xml_output) throws java.rmi.RemoteException {
		if (modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo == null)
			_initModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy();
		modulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo.invoke(in_string_annoprotocollo,
				in_string_formid, in_string_nprotocollo, out_xml_data, out_xml_output);
	}

}