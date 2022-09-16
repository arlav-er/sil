package com.adobe.idp.services;

public class SupportPDFG_ProcessConvertToPdfAProxy implements com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA {
	private String _endpoint = null;
	private com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA supportPDFG_ProcessConvertToPdfA = null;

	public SupportPDFG_ProcessConvertToPdfAProxy() {
		_initSupportPDFG_ProcessConvertToPdfAProxy();
	}

	public SupportPDFG_ProcessConvertToPdfAProxy(String endpoint) {
		_endpoint = endpoint;
		_initSupportPDFG_ProcessConvertToPdfAProxy();
	}

	private void _initSupportPDFG_ProcessConvertToPdfAProxy() {
		try {
			supportPDFG_ProcessConvertToPdfA = (new com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfAServiceLocator())
					.getProcessConvertToPdfA();
			if (supportPDFG_ProcessConvertToPdfA != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) supportPDFG_ProcessConvertToPdfA)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) supportPDFG_ProcessConvertToPdfA)
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
		if (supportPDFG_ProcessConvertToPdfA != null)
			((javax.xml.rpc.Stub) supportPDFG_ProcessConvertToPdfA)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA getSupportPDFG_ProcessConvertToPdfA() {
		if (supportPDFG_ProcessConvertToPdfA == null)
			_initSupportPDFG_ProcessConvertToPdfAProxy();
		return supportPDFG_ProcessConvertToPdfA;
	}

	public void invoke(com.adobe.idp.services.BLOB inDoc, java.lang.Boolean in_bool_enableValidationPdfA,
			javax.xml.rpc.holders.BooleanHolder isPDFA, com.adobe.idp.services.holders.BLOBHolder outDoc,
			com.adobe.idp.services.holders.XMLHolder out_xml_output) throws java.rmi.RemoteException {
		if (supportPDFG_ProcessConvertToPdfA == null)
			_initSupportPDFG_ProcessConvertToPdfAProxy();
		supportPDFG_ProcessConvertToPdfA.invoke(inDoc, in_bool_enableValidationPdfA, isPDFA, outDoc, out_xml_output);
	}

}