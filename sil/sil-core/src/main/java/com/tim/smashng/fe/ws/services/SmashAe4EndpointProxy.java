package com.tim.smashng.fe.ws.services;

public class SmashAe4EndpointProxy implements com.tim.smashng.fe.ws.services.SmashAe4Endpoint {
  private String _endpoint = null;
  private com.tim.smashng.fe.ws.services.SmashAe4Endpoint smashAe4Endpoint = null;
  
  public SmashAe4EndpointProxy() {
    _initSmashAe4EndpointProxy();
  }
  
  public SmashAe4EndpointProxy(String endpoint) {
    _endpoint = endpoint;
    _initSmashAe4EndpointProxy();
  }
  
  private void _initSmashAe4EndpointProxy() {
    try {
      smashAe4Endpoint = (new com.tim.smashng.fe.ws.services.SmashAe4Locator()).getSmashAe4EndpointPort();
      if (smashAe4Endpoint != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)smashAe4Endpoint)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)smashAe4Endpoint)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (smashAe4Endpoint != null)
      ((javax.xml.rpc.Stub)smashAe4Endpoint)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.tim.smashng.fe.ws.services.SmashAe4Endpoint getSmashAe4Endpoint() {
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint;
  }
  
  public com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse getCreditoResiduo(com.tim.smashng.fe.ws.services.AeGetCreditoResiduoRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.getCreditoResiduo(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeGetInvioResponse getInvio(com.tim.smashng.fe.ws.services.AeGetInvioRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.getInvio(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggi(com.tim.smashng.fe.ws.services.AeInviaMessaggiRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.inviaMessaggi(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeRicercaInviiResponse ricercaInvii(com.tim.smashng.fe.ws.services.AeRicercaInviiRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.ricercaInvii(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse ricercaMessaggiRicevuti(com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.ricercaMessaggiRicevuti(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AePullNotifyResponse pullNotify(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.pullNotify(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AePullNotifyMOResponse pullNotifyMO(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.pullNotifyMO(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteTestuale(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.inviaMessaggiMittenteTestuale(arg0);
  }
  
  public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteNumerico(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.inviaMessaggiMittenteNumerico(arg0);
  }
  
  public java.lang.String getVersion() throws java.rmi.RemoteException{
    if (smashAe4Endpoint == null)
      _initSmashAe4EndpointProxy();
    return smashAe4Endpoint.getVersion();
  }
  
  
}