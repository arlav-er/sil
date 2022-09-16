package it.eng.sare.secure.services;

public class ServiceMyPortalSoapProxy implements it.eng.sare.secure.services.ServiceMyPortalSoap {
  private String _endpoint = null;
  private it.eng.sare.secure.services.ServiceMyPortalSoap serviceMyPortalSoap = null;
  
  public ServiceMyPortalSoapProxy() {
    _initServiceMyPortalSoapProxy();
  }
  
  public ServiceMyPortalSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceMyPortalSoapProxy();
  }
  
  private void _initServiceMyPortalSoapProxy() {
    try {
      serviceMyPortalSoap = (new it.eng.sare.secure.services.ServiceMyPortalLocator()).getServiceMyPortalSoap();
      if (serviceMyPortalSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviceMyPortalSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviceMyPortalSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviceMyPortalSoap != null)
      ((javax.xml.rpc.Stub)serviceMyPortalSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sare.secure.services.ServiceMyPortalSoap getServiceMyPortalSoap() {
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap;
  }
  
  public java.lang.String registraUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException{
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap.registraUtente(username, password, xmlUtenteAzienda);
  }
  
  public java.lang.String aggiornaUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException{
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap.aggiornaUtente(username, password, xmlUtenteAzienda);
  }
  
  public java.lang.String abilitaUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException{
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap.abilitaUtente(username, password, xmlUtenteAzienda);
  }
  
  public java.lang.String modificaPasswordUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException{
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap.modificaPasswordUtente(username, password, xmlUtenteAzienda);
  }
  
  public java.lang.String modificaTipoUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException{
    if (serviceMyPortalSoap == null)
      _initServiceMyPortalSoapProxy();
    return serviceMyPortalSoap.modificaTipoUtente(username, password, xmlUtenteAzienda);
  }
  
  
}