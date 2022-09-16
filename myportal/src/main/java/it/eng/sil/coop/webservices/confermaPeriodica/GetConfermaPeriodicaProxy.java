package it.eng.sil.coop.webservices.confermaPeriodica;

public class GetConfermaPeriodicaProxy implements it.eng.sil.coop.webservices.confermaPeriodica.GetConfermaPeriodica {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.confermaPeriodica.GetConfermaPeriodica getConfermaPeriodica = null;
  
  public GetConfermaPeriodicaProxy() {
    _initGetConfermaPeriodicaProxy();
  }
  
  public GetConfermaPeriodicaProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetConfermaPeriodicaProxy();
  }
  
  private void _initGetConfermaPeriodicaProxy() {
    try {
      getConfermaPeriodica = (new it.eng.sil.coop.webservices.confermaPeriodica.GetConfermaPeriodicaServiceLocator()).getGetConfermaPeriodica();
      if (getConfermaPeriodica != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getConfermaPeriodica)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getConfermaPeriodica)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getConfermaPeriodica != null)
      ((javax.xml.rpc.Stub)getConfermaPeriodica)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.confermaPeriodica.GetConfermaPeriodica getGetConfermaPeriodica() {
    if (getConfermaPeriodica == null)
      _initGetConfermaPeriodicaProxy();
    return getConfermaPeriodica;
  }
  
  public java.lang.String getConfermaPeriodica(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (getConfermaPeriodica == null)
      _initGetConfermaPeriodicaProxy();
    return getConfermaPeriodica.getConfermaPeriodica(inputXML);
  }
  
  
}