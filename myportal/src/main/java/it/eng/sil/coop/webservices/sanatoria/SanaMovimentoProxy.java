package it.eng.sil.coop.webservices.sanatoria;

public class SanaMovimentoProxy implements it.eng.sil.coop.webservices.sanatoria.SanaMovimento {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.sanatoria.SanaMovimento sanaMovimento = null;
  
  public SanaMovimentoProxy() {
    _initSanaMovimentoProxy();
  }
  
  public SanaMovimentoProxy(String endpoint) {
    _endpoint = endpoint;
    _initSanaMovimentoProxy();
  }
  
  private void _initSanaMovimentoProxy() {
    try {
      sanaMovimento = (new it.eng.sil.coop.webservices.sanatoria.SanaMovimentoServiceLocator()).getSanaMovimento();
      if (sanaMovimento != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sanaMovimento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sanaMovimento)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sanaMovimento != null)
      ((javax.xml.rpc.Stub)sanaMovimento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.sanatoria.SanaMovimento getSanaMovimento() {
    if (sanaMovimento == null)
      _initSanaMovimentoProxy();
    return sanaMovimento;
  }
  
  public java.lang.String putSanatoriaReddito(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (sanaMovimento == null)
      _initSanaMovimentoProxy();
    return sanaMovimento.putSanatoriaReddito(inputXML);
  }
  
  
}