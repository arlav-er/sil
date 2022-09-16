package com.adobe.idp.services;

public class FirmaGrafometrica_SetSignatureToDocProxy
		implements com.adobe.idp.services.FirmaGrafometrica_SetSignatureToDoc {
	private String _endpoint = null;
	private com.adobe.idp.services.FirmaGrafometrica_SetSignatureToDoc firmaGrafometrica_SetSignatureToDoc = null;

	public FirmaGrafometrica_SetSignatureToDocProxy() {
		_initFirmaGrafometrica_SetSignatureToDocProxy();
	}

	public FirmaGrafometrica_SetSignatureToDocProxy(String endpoint) {
		_endpoint = endpoint;
		_initFirmaGrafometrica_SetSignatureToDocProxy();
	}

	private void _initFirmaGrafometrica_SetSignatureToDocProxy() {
		try {
			firmaGrafometrica_SetSignatureToDoc = (new com.adobe.idp.services.FirmaGrafometrica_SetSignatureToDocServiceLocator())
					.getSetSignatureToDoc();
			if (firmaGrafometrica_SetSignatureToDoc != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) firmaGrafometrica_SetSignatureToDoc)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) firmaGrafometrica_SetSignatureToDoc)
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
		if (firmaGrafometrica_SetSignatureToDoc != null)
			((javax.xml.rpc.Stub) firmaGrafometrica_SetSignatureToDoc)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.FirmaGrafometrica_SetSignatureToDoc getFirmaGrafometrica_SetSignatureToDoc() {
		if (firmaGrafometrica_SetSignatureToDoc == null)
			_initFirmaGrafometrica_SetSignatureToDocProxy();
		return firmaGrafometrica_SetSignatureToDoc;
	}

	public void invoke(com.adobe.idp.services.BLOB in_doc, com.adobe.idp.services.XML in_xml_dati,
			javax.xml.rpc.holders.StringHolder numPages, javax.xml.rpc.holders.ShortHolder out_berror,
			com.adobe.idp.services.holders.BLOBHolder out_doc_pdf, javax.xml.rpc.holders.StringHolder out_error,
			javax.xml.rpc.holders.StringHolder t) throws java.rmi.RemoteException {
		if (firmaGrafometrica_SetSignatureToDoc == null)
			_initFirmaGrafometrica_SetSignatureToDocProxy();
		firmaGrafometrica_SetSignatureToDoc.invoke(in_doc, in_xml_dati, numPages, out_berror, out_doc_pdf, out_error,
				t);
	}

}