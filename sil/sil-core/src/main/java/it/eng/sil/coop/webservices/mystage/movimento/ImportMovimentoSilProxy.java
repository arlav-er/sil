package it.eng.sil.coop.webservices.mystage.movimento;

public class ImportMovimentoSilProxy
		implements it.eng.sil.coop.webservices.mystage.movimento.ImportMovimentoSil_PortType {
	private String _endpoint = null;
	private it.eng.sil.coop.webservices.mystage.movimento.ImportMovimentoSil_PortType importMovimentoSil_PortType = null;

	public ImportMovimentoSilProxy() {
		_initImportMovimentoSilProxy();
	}

	public ImportMovimentoSilProxy(String endpoint) {
		_endpoint = endpoint;
		_initImportMovimentoSilProxy();
	}

	private void _initImportMovimentoSilProxy() {
		try {
			importMovimentoSil_PortType = (new it.eng.sil.coop.webservices.mystage.movimento.ImportMovimentoSil_ServiceLocator())
					.getImportMovimentoSil();
			if (importMovimentoSil_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) importMovimentoSil_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) importMovimentoSil_PortType)
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
		if (importMovimentoSil_PortType != null)
			((javax.xml.rpc.Stub) importMovimentoSil_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.coop.webservices.mystage.movimento.ImportMovimentoSil_PortType getImportMovimentoSil_PortType() {
		if (importMovimentoSil_PortType == null)
			_initImportMovimentoSilProxy();
		return importMovimentoSil_PortType;
	}

	public java.lang.String putMovimento(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException {
		if (importMovimentoSil_PortType == null)
			_initImportMovimentoSilProxy();
		return importMovimentoSil_PortType.putMovimento(arg0, arg1, arg2);
	}

}