package it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0;

public class VerificaCondizioniNEET_PortTypeProxy implements it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType {
  private String _endpoint = null;
  private it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType verificaCondizioniNEET_PortType = null;
  
  public VerificaCondizioniNEET_PortTypeProxy() {
    _initVerificaCondizioniNEET_PortTypeProxy();
  }
  
  public VerificaCondizioniNEET_PortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initVerificaCondizioniNEET_PortTypeProxy();
  }
  
  private void _initVerificaCondizioniNEET_PortTypeProxy() {
    try {
      verificaCondizioniNEET_PortType = (new it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_BindingServiceLocator()).getverificaCondizioniNEET();
      if (verificaCondizioniNEET_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)verificaCondizioniNEET_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)verificaCondizioniNEET_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (verificaCondizioniNEET_PortType != null)
      ((javax.xml.rpc.Stub)verificaCondizioniNEET_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType getVerificaCondizioniNEET_PortType() {
    if (verificaCondizioniNEET_PortType == null)
      _initVerificaCondizioniNEET_PortTypeProxy();
    return verificaCondizioniNEET_PortType;
  }
  
  public it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_Output openSPCoop_PD(it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_Input verificaCondizioniNEET_Input_msg) throws java.rmi.RemoteException{
    if (verificaCondizioniNEET_PortType == null)
      _initVerificaCondizioniNEET_PortTypeProxy();
    return verificaCondizioniNEET_PortType.openSPCoop_PD(verificaCondizioniNEET_Input_msg);
  }
  
  
}