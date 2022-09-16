package it.eng.sil.coop.webservices.did;

public class StipulaDidProxy implements it.eng.sil.coop.webservices.did.StipulaDid {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.did.StipulaDid stipulaDid = null;
  
  public StipulaDidProxy() {
    _initStipulaDidProxy();
  }
  
  public StipulaDidProxy(String endpoint) {
    _endpoint = endpoint;
    _initStipulaDidProxy();
  }
  
  private void _initStipulaDidProxy() {
    try {
      stipulaDid = (new it.eng.sil.coop.webservices.did.StipulaDidServiceLocator()).getStipulaDid();
      if (stipulaDid != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)stipulaDid)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)stipulaDid)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (stipulaDid != null)
      ((javax.xml.rpc.Stub)stipulaDid)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.did.StipulaDid getStipulaDid() {
    if (stipulaDid == null)
      _initStipulaDidProxy();
    return stipulaDid;
  }
  
  public java.lang.String putCreaDID(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (stipulaDid == null)
      _initStipulaDidProxy();
    return stipulaDid.putCreaDID(inputXML);
  }
  
  
}