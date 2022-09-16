package it.eng.sil.coop.webservices.percorsolavoratore;

public class GetPercorsoLavoratoreProxy implements it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratore {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratore getPercorsoLavoratore = null;
  
  public GetPercorsoLavoratoreProxy() {
    _initGetPercorsoLavoratoreProxy();
  }
  
  public GetPercorsoLavoratoreProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetPercorsoLavoratoreProxy();
  }
  
  private void _initGetPercorsoLavoratoreProxy() {
    try {
      getPercorsoLavoratore = (new it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreServiceLocator()).getGetPercorsoLavoratore();
      if (getPercorsoLavoratore != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getPercorsoLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getPercorsoLavoratore)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getPercorsoLavoratore != null)
      ((javax.xml.rpc.Stub)getPercorsoLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratore getGetPercorsoLavoratore() {
    if (getPercorsoLavoratore == null)
      _initGetPercorsoLavoratoreProxy();
    return getPercorsoLavoratore;
  }
  
  public java.lang.String getPercorsoLavoratore(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (getPercorsoLavoratore == null)
      _initGetPercorsoLavoratoreProxy();
    return getPercorsoLavoratore.getPercorsoLavoratore(inputXML);
  }
  
  public java.lang.Object getStampaPercorsoLavoratore(java.lang.String inputXML) throws java.rmi.RemoteException, it.eng.sil.coop.webservices.percorsolavoratore.GetPercorsoLavoratoreException, it.eng.sil.coop.webservices.percorsolavoratore.InputValidationException{
    if (getPercorsoLavoratore == null)
      _initGetPercorsoLavoratoreProxy();
    return getPercorsoLavoratore.getStampaPercorsoLavoratore(inputXML);
  }
  
  
}