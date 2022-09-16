package it.eng.sms.clientproxy;

public class InfoTIMProxy implements it.eng.sms.clientproxy.InfoTIM {
  private String _endpoint = null;
  private it.eng.sms.clientproxy.InfoTIM infoTIM = null;
  
  public InfoTIMProxy() {
    _initInfoTIMProxy();
  }
  
  public InfoTIMProxy(String endpoint) {
    _endpoint = endpoint;
    _initInfoTIMProxy();
  }
  
  private void _initInfoTIMProxy() {
    try {
      infoTIM = (new it.eng.sms.clientproxy.InfoTIMServiceLocator()).getInfoTIM();
      if (infoTIM != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)infoTIM)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)infoTIM)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (infoTIM != null)
      ((javax.xml.rpc.Stub)infoTIM)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sms.clientproxy.InfoTIM getInfoTIM() {
    if (infoTIM == null)
      _initInfoTIMProxy();
    return infoTIM;
  }
  
  public java.lang.String invioSingoloSMS(java.lang.String idMittente, java.lang.String contenuto, java.lang.String destinatario, boolean notificaRicezione) throws java.rmi.RemoteException{
    if (infoTIM == null)
      _initInfoTIMProxy();
    return infoTIM.invioSingoloSMS(idMittente, contenuto, destinatario, notificaRicezione);
  }
  
  
}