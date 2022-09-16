package ejb.webservices.smash;

public class SmashaePortTypeProxy implements ejb.webservices.smash.SmashaePortType {
  private String _endpoint = null;
  private ejb.webservices.smash.SmashaePortType smashaePortType = null;
  
  public SmashaePortTypeProxy() {
    _initSmashaePortTypeProxy();
  }
  
  public SmashaePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initSmashaePortTypeProxy();
  }
  
  private void _initSmashaePortTypeProxy() {
    try {
      smashaePortType = (new ejb.webservices.smash.SmashaeLocator()).getSmashaePort();
      if (smashaePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)smashaePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)smashaePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (smashaePortType != null)
      ((javax.xml.rpc.Stub)smashaePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ejb.webservices.smash.SmashaePortType getSmashaePortType() {
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType;
  }
  
  public java.lang.String getVersion() throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.getVersion();
  }
  
  public bean.webservices.smash.RisultatoArchivioBean ricercaArchivio(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6, java.lang.String arg7) throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.ricercaArchivio(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
  }
  
  public bean.webservices.smash.PacchettoBean pb() throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.pb();
  }
  
  /**
   * L'invio é indirizzato a una Lista Libera di Utenti; 
   * il Testo del Messaggio é Libero. 
   * La risposta include l'esito (OK,NOK), l'eventuale ragione del fallimento ed, in caso di successo, il credito residuo.
   * 
   * @param testo_sms Testo del messaggio da inviare, lungo al massimo 640 caratteri; 
   * I caratteri ammessi sono: A-Z, a-z, 0-9, ,!, ", £, $, %, &, (, ), /, =, ?, *, è, é, §, _, :, ; , <singolo apicetto>, ì, ò, à, ù, +, ,, ., -, @, #, <, >
   * 
   * @param lista_numeri Lista degli MSISDN destinatari separati dal carattere ";"
   * 
   * @param contract_code Codice del contratto;alfanumerico lungo al massimo 32 caratteri
   * 
   * @param msisdn_ref MSISDN del Referente; lungo al massimo 20 caratteri
   * @param tipo_referente Attualmente sono accettati i tipi: USER_ACCESS, MASTER_ACCESS e STS_ACCESS
   * @param nome Nome del Referente
   * @param cognome Cognome del Referente
   * @param password Password UNI-TIM del Referente
   * @return risultato dell'invio
   */
  public bean.webservices.smash.RisultatoInvioBean invioSpot(String testo_sms, String lista_numeri, String contract_code, String msisdn_ref, String tipo_referente, String nome, String cognome, String password) throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.invioSpot(testo_sms, lista_numeri, contract_code, msisdn_ref, tipo_referente, nome, cognome, password);
  }
  
  public bean.webservices.smash.ArchivioBean ab() throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.ab();
  }
  
  public bean.webservices.smash.RisultatoCreditoBean creditoResiduo(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5) throws java.rmi.RemoteException{
    if (smashaePortType == null)
      _initSmashaePortTypeProxy();
    return smashaePortType.creditoResiduo(arg0, arg1, arg2, arg3, arg4, arg5);
  }
  
  
}