package it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0;

public class ConferimentoDID_PortTypeProxy
		implements it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType {
	private String _endpoint = null;
	private it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType conferimentoDID_PortType = null;

	public ConferimentoDID_PortTypeProxy() {
		_initConferimentoDID_PortTypeProxy();
	}

	public ConferimentoDID_PortTypeProxy(String endpoint) {
		_endpoint = endpoint;
		_initConferimentoDID_PortTypeProxy();
	}

	private void _initConferimentoDID_PortTypeProxy() {
		try {
			conferimentoDID_PortType = (new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingServiceLocator())
					.getgestisciDID();
			if (conferimentoDID_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) conferimentoDID_PortType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) conferimentoDID_PortType)
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
		if (conferimentoDID_PortType != null)
			((javax.xml.rpc.Stub) conferimentoDID_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType getConferimentoDID_PortType() {
		if (conferimentoDID_PortType == null)
			_initConferimentoDID_PortTypeProxy();
		return conferimentoDID_PortType;
	}

	public it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output gestisciDID(
			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input gestisciDID_Input_msg)
			throws java.rmi.RemoteException {
		if (conferimentoDID_PortType == null)
			_initConferimentoDID_PortTypeProxy();
		return conferimentoDID_PortType.gestisciDID(gestisciDID_Input_msg);
	}

}