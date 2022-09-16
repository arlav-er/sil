package it.eng.sil.coop.webservices.movimenti;

public class GetUltimoMovimentoProxy implements it.eng.sil.coop.webservices.movimenti.GetUltimoMovimento {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.movimenti.GetUltimoMovimento getUltimoMovimento = null;
  
  public GetUltimoMovimentoProxy() {
    _initGetUltimoMovimentoProxy();
  }
  
  public GetUltimoMovimentoProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetUltimoMovimentoProxy();
  }
  
  private void _initGetUltimoMovimentoProxy() {
    try {
      getUltimoMovimento = (new it.eng.sil.coop.webservices.movimenti.GetUltimoMovimentoServiceLocator()).getGetUltimoMovimento();
      if (getUltimoMovimento != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getUltimoMovimento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getUltimoMovimento)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getUltimoMovimento != null)
      ((javax.xml.rpc.Stub)getUltimoMovimento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.movimenti.GetUltimoMovimento getGetUltimoMovimento() {
    if (getUltimoMovimento == null)
      _initGetUltimoMovimentoProxy();
    return getUltimoMovimento;
  }
  
  public java.lang.String getUltimoMovimento(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (getUltimoMovimento == null)
      _initGetUltimoMovimentoProxy();
    return getUltimoMovimento.getUltimoMovimento(inputXML);
  }
  
  
}