package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SchedaAnagraficaProfessionaleWSProxy
		implements it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS {
	private String _endpoint = null;
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS schedaAnagraficaProfessionaleWS = null;

	public SchedaAnagraficaProfessionaleWSProxy() {
		_initSchedaAnagraficaProfessionaleWSProxy();
	}

	public SchedaAnagraficaProfessionaleWSProxy(String endpoint) {
		_endpoint = endpoint;
		_initSchedaAnagraficaProfessionaleWSProxy();
	}

	private void _initSchedaAnagraficaProfessionaleWSProxy() {
		try {
			schedaAnagraficaProfessionaleWS = (new it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSServiceLocator())
					.getSchedaAnagraficaProfessionaleWSPort();
			if (schedaAnagraficaProfessionaleWS != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) schedaAnagraficaProfessionaleWS)
							._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) schedaAnagraficaProfessionaleWS)
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
		if (schedaAnagraficaProfessionaleWS != null)
			((javax.xml.rpc.Stub) schedaAnagraficaProfessionaleWS)
					._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS getSchedaAnagraficaProfessionaleWS() {
		if (schedaAnagraficaProfessionaleWS == null)
			_initSchedaAnagraficaProfessionaleWSProxy();
		return schedaAnagraficaProfessionaleWS;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[] getListaSap(
			java.lang.String arg0)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException {
		if (schedaAnagraficaProfessionaleWS == null)
			_initSchedaAnagraficaProfessionaleWSProxy();
		return schedaAnagraficaProfessionaleWS.getListaSap(arg0);
	}

	public it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO getSapUtenteDTO(java.lang.Integer arg0,
			java.lang.String arg1)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException {
		if (schedaAnagraficaProfessionaleWS == null)
			_initSchedaAnagraficaProfessionaleWSProxy();
		return schedaAnagraficaProfessionaleWS.getSapUtenteDTO(arg0, arg1);
	}

}