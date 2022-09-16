package it.eng.sil.coop.webservices.adesioneReimpiego;

public class GetAdesioneReimpiegoProxy implements it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego getAdesioneReimpiego = null;
  
  public GetAdesioneReimpiegoProxy() {
    _initGetAdesioneReimpiegoProxy();
  }
  
  public GetAdesioneReimpiegoProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetAdesioneReimpiegoProxy();
  }
  
  private void _initGetAdesioneReimpiegoProxy() {
    try {
      getAdesioneReimpiego = (new it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoServiceLocator()).getGetAdesioneReimpiego();
      if (getAdesioneReimpiego != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getAdesioneReimpiego)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getAdesioneReimpiego)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getAdesioneReimpiego != null)
      ((javax.xml.rpc.Stub)getAdesioneReimpiego)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego getGetAdesioneReimpiego() {
    if (getAdesioneReimpiego == null)
      _initGetAdesioneReimpiegoProxy();
    return getAdesioneReimpiego;
  }
  
  public java.lang.String getAdesioneReimpiego(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (getAdesioneReimpiego == null)
      _initGetAdesioneReimpiegoProxy();
    return getAdesioneReimpiego.getAdesioneReimpiego(inputXML);
  }
  
  
}