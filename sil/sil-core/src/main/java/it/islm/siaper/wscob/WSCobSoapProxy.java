package it.islm.siaper.wscob;

public class WSCobSoapProxy implements it.islm.siaper.wscob.WSCobSoap {
  private String _endpoint = null;
  private it.islm.siaper.wscob.WSCobSoap wSCobSoap = null;
  
  public WSCobSoapProxy() {
    _initWSCobSoapProxy();
  }
  
  public WSCobSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSCobSoapProxy();
  }
  
  private void _initWSCobSoapProxy() {
    try {
      wSCobSoap = (new it.islm.siaper.wscob.WSCobLocator()).getWSCobSoap();
      if (wSCobSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSCobSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSCobSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSCobSoap != null)
      ((javax.xml.rpc.Stub)wSCobSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.islm.siaper.wscob.WSCobSoap getWSCobSoap() {
    if (wSCobSoap == null)
      _initWSCobSoapProxy();
    return wSCobSoap;
  }
  
  public java.lang.String save(byte[] charSequence, boolean useCripting) throws java.rmi.RemoteException{
    if (wSCobSoap == null)
      _initWSCobSoapProxy();
    return wSCobSoap.save(charSequence, useCripting);
  }
  
  
}