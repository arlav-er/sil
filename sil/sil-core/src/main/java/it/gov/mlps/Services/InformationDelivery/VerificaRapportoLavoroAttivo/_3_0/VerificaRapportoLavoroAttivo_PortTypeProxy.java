package it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class VerificaRapportoLavoroAttivo_PortTypeProxy implements
		it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType {
	private String _endpoint = null;
	private it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType verificaRapportoLavoroAttivo_PortType = null;

	public VerificaRapportoLavoroAttivo_PortTypeProxy() {
		_initVerificaRapportoLavoroAttivo_PortTypeProxy();
	}

	public VerificaRapportoLavoroAttivo_PortTypeProxy(String endpoint) {
		_endpoint = endpoint;
		_initVerificaRapportoLavoroAttivo_PortTypeProxy();
	}

	private void _initVerificaRapportoLavoroAttivo_PortTypeProxy() {
		try {
			verificaRapportoLavoroAttivo_PortType = (new it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingQSServiceLocator())
					.getVerificaRapportoLavoroAttivo_BindingQSPort();
			if (verificaRapportoLavoroAttivo_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) verificaRapportoLavoroAttivo_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) verificaRapportoLavoroAttivo_PortType)
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
		if (verificaRapportoLavoroAttivo_PortType != null)
			((javax.xml.rpc.Stub) verificaRapportoLavoroAttivo_PortType)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType getVerificaRapportoLavoroAttivo_PortType() {
		if (verificaRapportoLavoroAttivo_PortType == null)
			_initVerificaRapportoLavoroAttivo_PortTypeProxy();
		return verificaRapportoLavoroAttivo_PortType;
	}

	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.GetRapportoLavoroAttivo_Output getRapportiAttivi(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.GetRapportoLavoroAttivo_Input getRapportoLavoroAttivo_Input_msg)
			throws java.rmi.RemoteException {
		if (verificaRapportoLavoroAttivo_PortType == null)
			_initVerificaRapportoLavoroAttivo_PortTypeProxy();
		return verificaRapportoLavoroAttivo_PortType.getRapportiAttivi(getRapportoLavoroAttivo_Input_msg);
	}

}