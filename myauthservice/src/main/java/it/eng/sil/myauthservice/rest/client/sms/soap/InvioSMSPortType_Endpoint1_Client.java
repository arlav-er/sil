
package it.eng.sil.myauthservice.rest.client.sms.soap;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.7
 * 2020-07-09T15:03:29.721+02:00
 * Generated source version: 3.1.7
 * 
 */
public final class InvioSMSPortType_Endpoint1_Client {

    private static final QName SERVICE_NAME = new QName("http://www.tndigit.it/SMS/service", "SMSService.serviceagent");

    private InvioSMSPortType_Endpoint1_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = SMSServiceServiceagent.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        SMSServiceServiceagent ss = new SMSServiceServiceagent(wsdlURL, SERVICE_NAME);
        InvioSMSPortType port = ss.getEndpoint1();  
        
        {
        System.out.println("Invoking smsAvailable...");
        it.eng.sil.myauthservice.rest.client.sms.soap.UserInfoType _smsAvailable_request = null;
        try {
            it.eng.sil.myauthservice.rest.client.sms.soap.ListBundleType _smsAvailable__return = port.smsAvailable(_smsAvailable_request);
            System.out.println("smsAvailable.result=" + _smsAvailable__return);

        } catch (SendSMSFaultMessage e) { 
            System.out.println("Expected exception: SendSMSFaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking numSMSSent...");
        it.eng.sil.myauthservice.rest.client.sms.soap.UserInfoType _numSMSSent_request = null;
        try {
            it.eng.sil.myauthservice.rest.client.sms.soap.NumSMSType _numSMSSent__return = port.numSMSSent(_numSMSSent_request);
            System.out.println("numSMSSent.result=" + _numSMSSent__return);

        } catch (SendSMSFaultMessage e) { 
            System.out.println("Expected exception: SendSMSFaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking smsStatus...");
        it.eng.sil.myauthservice.rest.client.sms.soap.SMSForStatusRequestType _smsStatus_request = null;
        try {
            it.eng.sil.myauthservice.rest.client.sms.soap.SendStatusResponseType _smsStatus__return = port.smsStatus(_smsStatus_request);
            System.out.println("smsStatus.result=" + _smsStatus__return);

        } catch (SendSMSFaultMessage e) { 
            System.out.println("Expected exception: SendSMSFaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking sendSMS...");
        it.eng.sil.myauthservice.rest.client.sms.soap.SMSType _sendSMS_request = null;
        try {
            it.eng.sil.myauthservice.rest.client.sms.soap.SMSResultType _sendSMS__return = port.sendSMS(_sendSMS_request);
            System.out.println("sendSMS.result=" + _sendSMS__return);

        } catch (SendSMSFaultMessage e) { 
            System.out.println("Expected exception: SendSMSFaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getSMS...");
        it.eng.sil.myauthservice.rest.client.sms.soap.SMSRequestType _getSMS_request = null;
        try {
            it.eng.sil.myauthservice.rest.client.sms.soap.SMSReceiverType _getSMS__return = port.getSMS(_getSMS_request);
            System.out.println("getSMS.result=" + _getSMS__return);

        } catch (SendSMSFaultMessage e) { 
            System.out.println("Expected exception: SendSMSFaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
