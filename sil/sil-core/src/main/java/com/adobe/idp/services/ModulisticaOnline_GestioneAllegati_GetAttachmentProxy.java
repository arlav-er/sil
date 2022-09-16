package com.adobe.idp.services;

public class ModulisticaOnline_GestioneAllegati_GetAttachmentProxy
		implements com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachment {
	private String _endpoint = null;
	private com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachment modulisticaOnline_GestioneAllegati_GetAttachment = null;

	public ModulisticaOnline_GestioneAllegati_GetAttachmentProxy() {
		_initModulisticaOnline_GestioneAllegati_GetAttachmentProxy();
	}

	public ModulisticaOnline_GestioneAllegati_GetAttachmentProxy(String endpoint) {
		_endpoint = endpoint;
		_initModulisticaOnline_GestioneAllegati_GetAttachmentProxy();
	}

	private void _initModulisticaOnline_GestioneAllegati_GetAttachmentProxy() {
		try {
			modulisticaOnline_GestioneAllegati_GetAttachment = (new com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentServiceLocator())
					.getGetAttachment();
			if (modulisticaOnline_GestioneAllegati_GetAttachment != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachment)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachment)
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
		if (modulisticaOnline_GestioneAllegati_GetAttachment != null)
			((javax.xml.rpc.Stub) modulisticaOnline_GestioneAllegati_GetAttachment)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachment getModulisticaOnline_GestioneAllegati_GetAttachment() {
		if (modulisticaOnline_GestioneAllegati_GetAttachment == null)
			_initModulisticaOnline_GestioneAllegati_GetAttachmentProxy();
		return modulisticaOnline_GestioneAllegati_GetAttachment;
	}

	public void invoke(java.lang.Integer in_int_guid, java.lang.String in_string_hash,
			com.adobe.idp.services.holders.BLOBHolder out_document,
			com.adobe.idp.services.holders.XMLHolder out_xml_output) throws java.rmi.RemoteException {
		if (modulisticaOnline_GestioneAllegati_GetAttachment == null)
			_initModulisticaOnline_GestioneAllegati_GetAttachmentProxy();
		modulisticaOnline_GestioneAllegati_GetAttachment.invoke(in_int_guid, in_string_hash, out_document,
				out_xml_output);
	}

}