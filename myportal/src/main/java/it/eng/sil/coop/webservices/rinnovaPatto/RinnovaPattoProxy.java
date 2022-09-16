package it.eng.sil.coop.webservices.rinnovaPatto;

public class RinnovaPattoProxy implements it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPatto {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPatto rinnovaPatto = null;
  
  public RinnovaPattoProxy() {
    _initRinnovaPattoProxy();
  }
  
  public RinnovaPattoProxy(String endpoint) {
    _endpoint = endpoint;
    _initRinnovaPattoProxy();
  }
  
  private void _initRinnovaPattoProxy() {
    try {
      rinnovaPatto = (new it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPattoServiceLocator()).getRinnovaPatto();
      if (rinnovaPatto != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)rinnovaPatto)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)rinnovaPatto)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (rinnovaPatto != null)
      ((javax.xml.rpc.Stub)rinnovaPatto)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.rinnovaPatto.RinnovaPatto getRinnovaPatto() {
    if (rinnovaPatto == null)
      _initRinnovaPattoProxy();
    return rinnovaPatto;
  }
  
  public java.lang.String putRinnovaPatto(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (rinnovaPatto == null)
      _initRinnovaPattoProxy();
    return rinnovaPatto.putRinnovaPatto(inputXML);
  }
  
  
}