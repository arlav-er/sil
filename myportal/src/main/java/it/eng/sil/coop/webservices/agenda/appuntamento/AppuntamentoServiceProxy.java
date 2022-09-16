package it.eng.sil.coop.webservices.agenda.appuntamento;

public class AppuntamentoServiceProxy implements it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoService {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoService appuntamentoService = null;
  
  public AppuntamentoServiceProxy() {
    _initAppuntamentoServiceProxy();
  }
  
  public AppuntamentoServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initAppuntamentoServiceProxy();
  }
  
  private void _initAppuntamentoServiceProxy() {
    try {
      appuntamentoService = (new it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoServiceServiceLocator()).getAppuntamentoService();
      if (appuntamentoService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)appuntamentoService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)appuntamentoService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (appuntamentoService != null)
      ((javax.xml.rpc.Stub)appuntamentoService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoService getAppuntamentoService() {
    if (appuntamentoService == null)
      _initAppuntamentoServiceProxy();
    return appuntamentoService;
  }
  
  public java.lang.String fissaAppuntamento(java.lang.String inputXml) throws java.rmi.RemoteException{
    if (appuntamentoService == null)
      _initAppuntamentoServiceProxy();
    return appuntamentoService.fissaAppuntamento(inputXml);
  }
  
  public java.lang.String getDispAppuntamento(java.lang.String inputXml) throws java.rmi.RemoteException{
    if (appuntamentoService == null)
      _initAppuntamentoServiceProxy();
    return appuntamentoService.getDispAppuntamento(inputXml);
  }
  
  
}