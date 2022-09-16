package it.sintesi.protocollo.webservices;

public class GetProtocolloServiceSoapProxy implements GetProtocolloServiceSoap {
  private String _endpoint = null;
  private GetProtocolloServiceSoap getProtocolloServiceSoap = null;
  
  public GetProtocolloServiceSoapProxy() {
    _initGetProtocolloServiceSoapProxy();
  }
  
  public GetProtocolloServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetProtocolloServiceSoapProxy();
  }
  
  private void _initGetProtocolloServiceSoapProxy() {
    try {
      getProtocolloServiceSoap = (new GetProtocolloServiceLocator()).getGetProtocolloServiceSoap();
      if (getProtocolloServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getProtocolloServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getProtocolloServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getProtocolloServiceSoap != null)
      ((javax.xml.rpc.Stub)getProtocolloServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public GetProtocolloServiceSoap getGetProtocolloServiceSoap() {
    if (getProtocolloServiceSoap == null)
      _initGetProtocolloServiceSoapProxy();
    return getProtocolloServiceSoap;
  }
  
  public String getProtocollo(String inputXml) throws java.rmi.RemoteException{
    if (getProtocolloServiceSoap == null)
      _initGetProtocolloServiceSoapProxy();
    return getProtocolloServiceSoap.getProtocollo(inputXml);
  }
  
  
}