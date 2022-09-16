package it.eng.sil.coop.webservices.servizilavoratore;

public class ServiziLavoratoreProxy implements ServiziLavoratore {
  private String _endpoint = null;
  private ServiziLavoratore serviziLavoratore = null;
  
  public ServiziLavoratoreProxy() {
    _initServiziLavoratoreProxy();
  }
  
  public ServiziLavoratoreProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiziLavoratoreProxy();
  }
  
  private void _initServiziLavoratoreProxy() {
    try {
      serviziLavoratore = (new ServiziLavoratoreServiceLocator()).getServiziLavoratore();
      if (serviziLavoratore != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviziLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviziLavoratore)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviziLavoratore != null)
      ((javax.xml.rpc.Stub)serviziLavoratore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ServiziLavoratore getServiziLavoratore() {
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore;
  }
  
  public java.lang.String getLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.getLavoratoreIR(in0, in1, in2, in3, in4, in5);
  }
  
  public java.lang.String putLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.putLavoratoreIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  public java.lang.String aggiornaCompetenzaIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.aggiornaCompetenzaIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  public java.lang.String aggiornaCompExtraRegioneIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.aggiornaCompExtraRegioneIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13, in14);
  }
  
  public CpiMasterLavoratore getCpiMasterIR(java.lang.String in0) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.getCpiMasterIR(in0);
  }
  
  public java.lang.String modificaCPICompIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.modificaCPICompIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  public java.lang.String accorpaLavoratoriIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.accorpaLavoratoriIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13, in14);
  }
  
  public java.lang.String modificaCodiceFiscaleIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13, java.lang.String in14) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.modificaCodiceFiscaleIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13, in14);
  }
  
  public java.lang.String modificaAnagraficaLavoratoreIR(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5, java.lang.String in6, java.lang.String in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, java.lang.String in12, java.lang.String in13) throws java.rmi.RemoteException{
    if (serviziLavoratore == null)
      _initServiziLavoratoreProxy();
    return serviziLavoratore.modificaAnagraficaLavoratoreIR(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  
}