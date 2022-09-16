package it.eng.sil.coop.webservices.statooccupazionale;

public class GetStatoOccupazionaleProxy implements it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale {
  private String _endpoint = null;
  private it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale getStatoOccupazionale = null;
  
  public GetStatoOccupazionaleProxy() {
    _initGetStatoOccupazionaleProxy();
  }
  
  public GetStatoOccupazionaleProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetStatoOccupazionaleProxy();
  }
  
  private void _initGetStatoOccupazionaleProxy() {
    try {
      getStatoOccupazionale = (new it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleServiceLocator()).getGetStatoOccupazionale();
      if (getStatoOccupazionale != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getStatoOccupazionale)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getStatoOccupazionale)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getStatoOccupazionale != null)
      ((javax.xml.rpc.Stub)getStatoOccupazionale)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale getGetStatoOccupazionale() {
    if (getStatoOccupazionale == null)
      _initGetStatoOccupazionaleProxy();
    return getStatoOccupazionale;
  }
  
  public java.lang.String getStatoOccupazionale(java.lang.String inputXML) throws java.rmi.RemoteException{
    if (getStatoOccupazionale == null)
      _initGetStatoOccupazionaleProxy();
    return getStatoOccupazionale.getStatoOccupazionale(inputXML);
  }
  
  public java.lang.Object getStampaStatoOccupazionale(java.lang.String inputXML) throws java.rmi.RemoteException, it.eng.sil.coop.webservices.statooccupazionale.InputValidationException, it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleException{
    if (getStatoOccupazionale == null)
      _initGetStatoOccupazionaleProxy();
    return getStatoOccupazionale.getStampaStatoOccupazionale(inputXML);
  }
  
  
}