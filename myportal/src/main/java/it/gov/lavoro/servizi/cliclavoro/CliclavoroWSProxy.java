package it.gov.lavoro.servizi.cliclavoro;

public class CliclavoroWSProxy implements it.gov.lavoro.servizi.cliclavoro.CliclavoroWS {
  private String _endpoint = null;
  private it.gov.lavoro.servizi.cliclavoro.CliclavoroWS cliclavoroWS = null;
  
  public CliclavoroWSProxy() {
    _initCliclavoroWSProxy();
  }
  
  public CliclavoroWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initCliclavoroWSProxy();
  }
  
  private void _initCliclavoroWSProxy() {
    try {
      cliclavoroWS = (new it.gov.lavoro.servizi.cliclavoro.CliclavoroWSServiceLocator()).getcliclavoroWS();
      if (cliclavoroWS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cliclavoroWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cliclavoroWS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cliclavoroWS != null)
      ((javax.xml.rpc.Stub)cliclavoroWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.gov.lavoro.servizi.cliclavoro.CliclavoroWS getCliclavoroWS() {
    if (cliclavoroWS == null)
      _initCliclavoroWSProxy();
    return cliclavoroWS;
  }
  
  public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type invioMessaggio(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type parameters) throws java.rmi.RemoteException{
    if (cliclavoroWS == null)
      _initCliclavoroWSProxy();
    return cliclavoroWS.invioMessaggio(parameters);
  }
  
  public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type invioVacancy(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type parameters) throws java.rmi.RemoteException{
    if (cliclavoroWS == null)
      _initCliclavoroWSProxy();
    return cliclavoroWS.invioVacancy(parameters);
  }
  
  public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type invioCandidatura(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type parameters) throws java.rmi.RemoteException{
    if (cliclavoroWS == null)
      _initCliclavoroWSProxy();
    return cliclavoroWS.invioCandidatura(parameters);
  }
  
  
}