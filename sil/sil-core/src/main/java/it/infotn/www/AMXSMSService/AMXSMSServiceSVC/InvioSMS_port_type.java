/**
 * InvioSMS_port_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.AMXSMSService.AMXSMSServiceSVC;

public interface InvioSMS_port_type extends java.rmi.Remote {
    public it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type SMSStatus(it.infotn.www.SMS_EAI.InvioSMS_types.SMSForStatusRequest_type request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type;
    public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type numSMSSent(it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type;
    public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type SMSAvailable(it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type;
    public it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type sendSMS(it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type request) throws java.rmi.RemoteException, it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type;
}
