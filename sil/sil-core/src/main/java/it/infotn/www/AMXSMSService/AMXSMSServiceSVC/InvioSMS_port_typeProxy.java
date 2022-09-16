package it.infotn.www.AMXSMSService.AMXSMSServiceSVC;

public class InvioSMS_port_typeProxy implements it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type {
  private String _endpoint = null;
  private it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type invioSMS_port_type = null;
  
  public InvioSMS_port_typeProxy() {
    _initInvioSMS_port_typeProxy();
  }
  
  public InvioSMS_port_typeProxy(String endpoint) {
    _endpoint = endpoint;
    _initInvioSMS_port_typeProxy();
  }
  
  private void _initInvioSMS_port_typeProxy() {
    try {
      invioSMS_port_type = (new it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSVCLocator()).getAMXSMSServiceSOAPsvc();
      if (invioSMS_port_type != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)invioSMS_port_type)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)invioSMS_port_type)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (invioSMS_port_type != null)
      ((javax.xml.rpc.Stub)invioSMS_port_type)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type getInvioSMS_port_type() {
    if (invioSMS_port_type == null)
      _initInvioSMS_port_typeProxy();
    return invioSMS_port_type;
  }
  
  public it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type SMSStatus(it.infotn.www.SMS_EAI.InvioSMS_types.SMSForStatusRequest_type request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type{
    if (invioSMS_port_type == null)
      _initInvioSMS_port_typeProxy();
    return invioSMS_port_type.SMSStatus(request);
  }
  
  public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type numSMSSent(it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type{
    if (invioSMS_port_type == null)
      _initInvioSMS_port_typeProxy();
    return invioSMS_port_type.numSMSSent(request);
  }
  
  public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type SMSAvailable(it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type{
    if (invioSMS_port_type == null)
      _initInvioSMS_port_typeProxy();
    return invioSMS_port_type.SMSAvailable(request);
  }
  
  public it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type sendSMS(it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type{
    if (invioSMS_port_type == null)
      _initInvioSMS_port_typeProxy();
    return invioSMS_port_type.sendSMS(request);
  }
  
  
}