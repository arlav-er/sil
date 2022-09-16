package it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0;

public class YG_Profiling_PortTypeProxy
		implements it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0.YG_Profiling_PortType {
	private String _endpoint = null;
	private it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0.YG_Profiling_PortType yG_Profiling_PortType = null;

	public YG_Profiling_PortTypeProxy() {
		_initYG_Profiling_PortTypeProxy();
	}

	public YG_Profiling_PortTypeProxy(String endpoint) {
		_endpoint = endpoint;
		_initYG_Profiling_PortTypeProxy();
	}

	private void _initYG_Profiling_PortTypeProxy() {
		try {
			yG_Profiling_PortType = (new it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0.YG_Profiling_BindingQSServiceLocator())
					.getYG_Profiling_BindingQSPort();
			if (yG_Profiling_PortType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) yG_Profiling_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) yG_Profiling_PortType)
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
		if (yG_Profiling_PortType != null)
			((javax.xml.rpc.Stub) yG_Profiling_PortType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_0.YG_Profiling_PortType getYG_Profiling_PortType() {
		if (yG_Profiling_PortType == null)
			_initYG_Profiling_PortTypeProxy();
		return yG_Profiling_PortType;
	}

	public it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.YG_Profiling[] insert(
			it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.Insert_Input insert_Input_msg)
			throws java.rmi.RemoteException {
		if (yG_Profiling_PortType == null)
			_initYG_Profiling_PortTypeProxy();
		return yG_Profiling_PortType.insert(insert_Input_msg);
	}

	public it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.YG_Profiling[] select(
			it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0.Select_Input select_Input_msg)
			throws java.rmi.RemoteException {
		if (yG_Profiling_PortType == null)
			_initYG_Profiling_PortTypeProxy();
		return yG_Profiling_PortType.select(select_Input_msg);
	}

}