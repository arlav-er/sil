package it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0;

public class NotificaEsitoAppuntamentoTypeProxy implements
		it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoType {
	private String _endpoint = null;
	private it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoType notificaEsitoAppuntamentoType = null;

	public NotificaEsitoAppuntamentoTypeProxy() {
		_initNotificaEsitoAppuntamentoTypeProxy();
	}

	public NotificaEsitoAppuntamentoTypeProxy(String endpoint) {
		_endpoint = endpoint;
		_initNotificaEsitoAppuntamentoTypeProxy();
	}

	private void _initNotificaEsitoAppuntamentoTypeProxy() {
		try {
			notificaEsitoAppuntamentoType = (new it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoServiceLocator())
					.getNotificaEsitoAppuntamentoPort();
			if (notificaEsitoAppuntamentoType != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) notificaEsitoAppuntamentoType)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) notificaEsitoAppuntamentoType)
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
		if (notificaEsitoAppuntamentoType != null)
			((javax.xml.rpc.Stub) notificaEsitoAppuntamentoType)._setProperty("javax.xml.rpc.service.endpoint.address",
					_endpoint);

	}

	public it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoType getNotificaEsitoAppuntamentoType() {
		if (notificaEsitoAppuntamentoType == null)
			_initNotificaEsitoAppuntamentoTypeProxy();
		return notificaEsitoAppuntamentoType;
	}

	public it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType notifica(
			it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RichiestaNotificaEsitoType richiesta)
			throws java.rmi.RemoteException {
		if (notificaEsitoAppuntamentoType == null)
			_initNotificaEsitoAppuntamentoTypeProxy();
		return notificaEsitoAppuntamentoType.notifica(richiesta);
	}

}