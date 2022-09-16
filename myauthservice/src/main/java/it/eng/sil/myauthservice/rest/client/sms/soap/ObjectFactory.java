
package it.eng.sil.myauthservice.rest.client.sms.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.eng.sil.myauthservice.rest.client.sms.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvioSMSFault_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "InvioSMSFault");
    private final static QName _NumSMSSentMessage_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "NumSMSSentMessage");
    private final static QName _NumSMSSentResponse_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "NumSMSSentResponse");
    private final static QName _SMSAvailableMessage_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SMSAvailableMessage");
    private final static QName _SMSAvailableResponse_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SMSAvailableResponse");
    private final static QName _SendSMSMessage_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SendSMSMessage");
    private final static QName _SendSMSResponse_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SendSMSResponse");
    private final static QName _SentSMSStatusMessage_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SentSMSStatusMessage");
    private final static QName _SentSMSStatusResponse_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "SentSMSStatusResponse");
    private final static QName _UserInfo_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "UserInfo");
    private final static QName _GetSMSMessage_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "getSMSMessage");
    private final static QName _GetSMSResponse_QNAME = new QName("http://www.infotn.it/SMS-EAI/InvioSMS_message", "getSMSResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.eng.sil.myauthservice.rest.client.sms.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendStatusResponseType }
     * 
     */
    public SendStatusResponseType createSendStatusResponseType() {
        return new SendStatusResponseType();
    }

    /**
     * Create an instance of {@link SendStatusResponseType.SMSStatus }
     * 
     */
    public SendStatusResponseType.SMSStatus createSendStatusResponseTypeSMSStatus() {
        return new SendStatusResponseType.SMSStatus();
    }

    /**
     * Create an instance of {@link SMSResultType }
     * 
     */
    public SMSResultType createSMSResultType() {
        return new SMSResultType();
    }

    /**
     * Create an instance of {@link BundleType }
     * 
     */
    public BundleType createBundleType() {
        return new BundleType();
    }

    /**
     * Create an instance of {@link InvioSMSFaultType }
     * 
     */
    public InvioSMSFaultType createInvioSMSFaultType() {
        return new InvioSMSFaultType();
    }

    /**
     * Create an instance of {@link ListBundleType }
     * 
     */
    public ListBundleType createListBundleType() {
        return new ListBundleType();
    }

    /**
     * Create an instance of {@link MessageInfoType }
     * 
     */
    public MessageInfoType createMessageInfoType() {
        return new MessageInfoType();
    }

    /**
     * Create an instance of {@link SMSForStatusRequestType }
     * 
     */
    public SMSForStatusRequestType createSMSForStatusRequestType() {
        return new SMSForStatusRequestType();
    }

    /**
     * Create an instance of {@link SMSMessageType }
     * 
     */
    public SMSMessageType createSMSMessageType() {
        return new SMSMessageType();
    }

    /**
     * Create an instance of {@link SMSReceiverType }
     * 
     */
    public SMSReceiverType createSMSReceiverType() {
        return new SMSReceiverType();
    }

    /**
     * Create an instance of {@link SMSRequestType }
     * 
     */
    public SMSRequestType createSMSRequestType() {
        return new SMSRequestType();
    }

    /**
     * Create an instance of {@link SMSType }
     * 
     */
    public SMSType createSMSType() {
        return new SMSType();
    }

    /**
     * Create an instance of {@link UserInfoType }
     * 
     */
    public UserInfoType createUserInfoType() {
        return new UserInfoType();
    }

    /**
     * Create an instance of {@link NumSMSType }
     * 
     */
    public NumSMSType createNumSMSType() {
        return new NumSMSType();
    }

    /**
     * Create an instance of {@link SendStatusResponseType.SMSStatus.SendStatus }
     * 
     */
    public SendStatusResponseType.SMSStatus.SendStatus createSendStatusResponseTypeSMSStatusSendStatus() {
        return new SendStatusResponseType.SMSStatus.SendStatus();
    }

    /**
     * Create an instance of {@link SMSResultType.SMS }
     * 
     */
    public SMSResultType.SMS createSMSResultTypeSMS() {
        return new SMSResultType.SMS();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvioSMSFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "InvioSMSFault")
    public JAXBElement<InvioSMSFaultType> createInvioSMSFault(InvioSMSFaultType value) {
        return new JAXBElement<InvioSMSFaultType>(_InvioSMSFault_QNAME, InvioSMSFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "NumSMSSentMessage")
    public JAXBElement<UserInfoType> createNumSMSSentMessage(UserInfoType value) {
        return new JAXBElement<UserInfoType>(_NumSMSSentMessage_QNAME, UserInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NumSMSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "NumSMSSentResponse")
    public JAXBElement<NumSMSType> createNumSMSSentResponse(NumSMSType value) {
        return new JAXBElement<NumSMSType>(_NumSMSSentResponse_QNAME, NumSMSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SMSAvailableMessage")
    public JAXBElement<UserInfoType> createSMSAvailableMessage(UserInfoType value) {
        return new JAXBElement<UserInfoType>(_SMSAvailableMessage_QNAME, UserInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListBundleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SMSAvailableResponse")
    public JAXBElement<ListBundleType> createSMSAvailableResponse(ListBundleType value) {
        return new JAXBElement<ListBundleType>(_SMSAvailableResponse_QNAME, ListBundleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SMSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SendSMSMessage")
    public JAXBElement<SMSType> createSendSMSMessage(SMSType value) {
        return new JAXBElement<SMSType>(_SendSMSMessage_QNAME, SMSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SMSResultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SendSMSResponse")
    public JAXBElement<SMSResultType> createSendSMSResponse(SMSResultType value) {
        return new JAXBElement<SMSResultType>(_SendSMSResponse_QNAME, SMSResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SMSForStatusRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SentSMSStatusMessage")
    public JAXBElement<SMSForStatusRequestType> createSentSMSStatusMessage(SMSForStatusRequestType value) {
        return new JAXBElement<SMSForStatusRequestType>(_SentSMSStatusMessage_QNAME, SMSForStatusRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendStatusResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "SentSMSStatusResponse")
    public JAXBElement<SendStatusResponseType> createSentSMSStatusResponse(SendStatusResponseType value) {
        return new JAXBElement<SendStatusResponseType>(_SentSMSStatusResponse_QNAME, SendStatusResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "UserInfo")
    public JAXBElement<UserInfoType> createUserInfo(UserInfoType value) {
        return new JAXBElement<UserInfoType>(_UserInfo_QNAME, UserInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SMSRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "getSMSMessage")
    public JAXBElement<SMSRequestType> createGetSMSMessage(SMSRequestType value) {
        return new JAXBElement<SMSRequestType>(_GetSMSMessage_QNAME, SMSRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SMSReceiverType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.infotn.it/SMS-EAI/InvioSMS_message", name = "getSMSResponse")
    public JAXBElement<SMSReceiverType> createGetSMSResponse(SMSReceiverType value) {
        return new JAXBElement<SMSReceiverType>(_GetSMSResponse_QNAME, SMSReceiverType.class, null, value);
    }

}
