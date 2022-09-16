<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*,
                it.eng.afExt.utils.StringUtils,
                java.util.GregorianCalendar,
                java.math.*, it.eng.sil.security.* "%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%    
  //inizializzo i campi
  BigDecimal cdnLavoratore=new BigDecimal(0);
  //----------
  String prgRichiestaAz="";
  String prgAzienda="";
  String prgUnita="";
  //----------
  String prgRosa="";
  String cpiRose="";
  int _funzione=0;
  String prgNominativo="";
  String prgIncrocio="";

  String prgAlternativa="";
  BigDecimal numPesoTitolo=new BigDecimal(0);
  BigDecimal numPesoInfo=new BigDecimal(0);
  BigDecimal numPesoLingua=new BigDecimal(0);
  BigDecimal numPesoMansione=new BigDecimal(0);
  BigDecimal numPesoEsp = new BigDecimal(0);
  BigDecimal numPesoEta=new BigDecimal(0);
  BigDecimal indiceVic=new BigDecimal(0);
  BigDecimal numDa=new BigDecimal(0); //Età min
  BigDecimal numA=new BigDecimal(0); //Età max
  String dataNasc="";
  String appoggio="";
  
  //Inizializzazione sourcebean delle query
  SourceBean informInfo=null;
  SourceBean lavInfo=null;
  SourceBean ricInform=null;
  SourceBean lavStudi=null;
  SourceBean ricStudi=null;
  SourceBean lavLingua=null;
  SourceBean ricLingua=null;
  SourceBean lavMansione=null;
  SourceBean ricMansione=null;
  SourceBean lavTipoPatente=null;
  SourceBean ricTipoPatente=null;
  SourceBean lavAutomunito=null;
  SourceBean ricAutomunito=null;
  
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  boolean ins=true;
  boolean modify=false;
  boolean goBack=false;
  
    //---------------
    
    _funzione      = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    prgNominativo  = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
    prgIncrocio    = (String) serviceRequest.getAttribute("PRGINCROCIO");
    cpiRose       = (String) serviceRequest.getAttribute("CPIROSE");
    prgRosa       = (String) serviceRequest.getAttribute("PRGROSA");

    //-----------
    prgRichiestaAz  = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
    prgAzienda      = serviceRequest.getAttribute("PRGAZIENDA").toString();
    prgUnita        = serviceRequest.getAttribute("PRGUNITA").toString();
    //-----------
    
    //conoscenza informatiche
    Vector conInformVec= serviceResponse.getAttributeAsVector("M_GET_DATI_INFORM.ROWS.ROW");
    /*if ( (conInformVec!=null) && (conInformVec.size()>0) ) { 
        informInfo = (SourceBean)conInformVec.get(0);
    }*/
    //Info del lavoratore
    Vector lavInfoVec= serviceResponse.getAttributeAsVector("M_GET_DATI_LAV.ROWS.ROW");
    if ( (lavInfoVec!=null) && (lavInfoVec.size()>0) ) { 
        lavInfo= (SourceBean)lavInfoVec.get(0);
        cdnLavoratore         = (BigDecimal) lavInfo.getAttribute("cdnLavoratore");
        numPesoTitolo         = (BigDecimal) lavInfo.getAttribute("NUMPESOTITOLO");
        numPesoInfo           = (BigDecimal) lavInfo.getAttribute("NUMPESOINFO");
        numPesoLingua         = (BigDecimal) lavInfo.getAttribute("NUMPESOLINGUA");
        numPesoMansione       = (BigDecimal) lavInfo.getAttribute("NUMPESOMANSIONE");
        numPesoEsp            = (BigDecimal) lavInfo.getAttribute("NUMPESOESP");
        numPesoEta            = (BigDecimal) lavInfo.getAttribute("NUMPESOETA");
        indiceVic            = (BigDecimal) lavInfo.getAttribute("DECINDICEVICINANZA");
        dataNasc              = (String) lavInfo.getAttribute("DATNASC");
    }
    //Info sui dati informatici della richiesta
    Vector ricInformVec= serviceResponse.getAttributeAsVector("M_GET_DATI_INFORMRIC.ROWS.ROW");
      if ( (ricInformVec !=null) && (ricInformVec.size()>0) ) {
        ricInform       = (SourceBean)ricInformVec.get(0);
        prgAlternativa  = ricInform.getAttribute("PRGALTERNATIVA").toString();
      }
    //Info degli studi della richiesta
    Vector ricStudiVec= serviceResponse.getAttributeAsVector("M_GET_DATI_STUDI.ROWS.ROW");
    //Info degli studi del lavoratore
    Vector lavStudiVec= serviceResponse.getAttributeAsVector("M_GET_DATI_STUDI_LAV.ROWS.ROW");

    //Lingua del lavoratore
    Vector lavLinguaVec = serviceResponse.getAttributeAsVector("M_GET_LINGUA_LAV.ROWS.ROW");
    //Lingua della richiesta
    Vector ricLinguaVec = serviceResponse.getAttributeAsVector("M_GET_LINGUA_RIC.ROWS.ROW");

    //Mansione lavoratore
    Vector lavMansioneVec = serviceResponse.getAttributeAsVector("M_GET_MANSIONE_LAV.ROWS.ROW");
    //Mansione richiesta
    Vector ricMansioneVec = serviceResponse.getAttributeAsVector("M_GET_MANSIONE_RIC.ROWS.ROW");

    // Tipo Patente lavoratore
    Vector lavTipoPatenteVec = serviceResponse.getAttributeAsVector("M_GETLAVORATOREABILITAZIONI.ROWS.ROW");
    // Tipo Patente richiesta
    Vector ricTipoPatenteVec = serviceResponse.getAttributeAsVector("M_ListAbilRich.ROWS.ROW");    
    
    // Automunito lavoratore
    Vector lavAutomunitoVec = serviceResponse.getAttributeAsVector("MLISTMOBGEOMANSIONI.ROWS.ROW");
    // Automunito richiesta
    Vector ricAutomunitoVec = serviceResponse.getAttributeAsVector("M_GETTESTATARICHIESTA.ROWS.ROW");

    infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    //operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);    
    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "DettMatchRosaPage");
 %>

<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

String openStr = "&PRGROSA=" + prgRosa + "&CPIROSE=" + cpiRose + "&CDNFUNZIONE=" + _funzione;
openStr += "&PRGRICHIESTAAZ=" + prgRichiestaAz + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" +prgUnita;
if(!mess.equals("")) {
	openStr += "&MESSAGE=" + mess;
	if(!listPage.equals("")) { openStr += "&LIST_PAGE=" + listPage; }
}

%>
<html>
<head>
<title>Dettaglio matching</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
</head>

<body class="gestione">
<% 
infCorrentiLav.setSkipLista(true);
infCorrentiLav.show(out); 
%>

<p class="titolo"><b>Dettaglio matching</b></p>
    <table width="96%" border="0px" cellspacing="0" cellpadding="0" align="center">
      <!-- top -->
      <tr>
        <td align="left" valign="top" width="6" height="19" class="cal_header"><img src="../../img/angoli/bia1.gif" height="10" width="6"></td>
        <td class="cal_header" align="center" width="20%" valign="middle" >Parametro</td>
        <td class="cal_header" align="center" valign="middle" >Punteggio</td>
        <td class="cal_header" align="center" width="32%" valign="middle" >Lavoratore</td>
        <td class="cal_header" align="center" width="32%" valign="middle" >Richiesta</td>
        <td class="cal_header" align="right" valign="top" height="19" width="6"><img src="../../img/angoli/bia2.gif" width="6" height="10"></td>
      </tr>
      <!-- end top -->
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td class="ido_bordato" colspan="4" align="left">Indice di vicinanza:&nbsp; <b><%= (indiceVic!=null)?indiceVic:new BigDecimal(0) %></b></td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>
      <% if ( !dataNasc.equals("") && (dataNasc !=null) ) { %>
          <!--tr>
            <td class="cal" align="center" valign="middle" width="6"></td>
            <td class="ido_bordato">Età</td>
            <td class="cal_bordato"><%= (numPesoEta!=null)?numPesoEta:new BigDecimal(0) %></td>
            <td align="left" class="ido_bordato">Data di nascita: <%= dataNasc %></td>
            <td align="left" class="ido_bordato"><% if ( !(numDa.equals(new BigDecimal(0))) ) {%>
                                              Età min: <%= numDa %> 
                                              <br/>
                                        <%}%>
                                        <% if (!(numA.equals(new BigDecimal(0)))){%>
                                              Età max: <%= numA %>
                                        <%}%></td>
            <td class="cal" align="center" valign="middle" width="6"></td>
          </tr-->
      <%}%>
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td class="ido_bordato" colspan="4" align="left">Profilo nr. <b><%= prgAlternativa %></b></td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>




      
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td align="left" class="ido_bordato" valign="top">Tipo patente</td>
        <td valign="top" class="cal_bordato" >100</td>
        <td class="ido_bordato" valign="top">
          <%if (lavTipoPatenteVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<lavTipoPatenteVec.size();i++){ %>
                 <% lavTipoPatente = (SourceBean)lavTipoPatenteVec.get(i); %>
                 <li><%= lavTipoPatente.getAttribute("STRDESCRIZIONE") %> <br/>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="ido_bordato" valign="top">
          <%if (ricTipoPatenteVec.size()==0) {%>&nbsp;<%}%>
           <ul>
              <% for (int i=0;i<ricTipoPatenteVec.size();i++){ %>
                 <% ricTipoPatente = (SourceBean)ricTipoPatenteVec.get(i); %>
                 <li><%= ricTipoPatente.getAttribute("STRDESCRIZIONE") %> <br/>
                 </li>
              <%}%>

          </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td align="left" class="ido_bordato" valign="top">Automunito</td>
        <td valign="top" class="cal_bordato" >100</td>
        <td class="ido_bordato" valign="top">
          <%if (lavAutomunitoVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<lavAutomunitoVec.size();i++){ %>
                 <% lavAutomunito = (SourceBean)lavAutomunitoVec.get(i);
                    appoggio = lavAutomunito.getAttribute("STRDESCRIZIONE").toString();%>
                 <li><%=appoggio%> <br/>
                <% if (lavAutomunito.containsAttribute("FLGDISPAUTO")) { %>
                    Disponib. uso auto: <%= lavAutomunito.getAttribute("FLGDISPAUTO")%> <br/> 
                <% } else { %>
                    Disponib. uso auto: Non specificata<br/> 
                <% } %>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="ido_bordato" valign="top">
          <%if (ricAutomunitoVec.size()==0) {%>&nbsp;<%}%>
            <ul>
              <% for (int i=0;i<ricAutomunitoVec.size();i++){ %>
                 <% ricAutomunito = (SourceBean)ricAutomunitoVec.get(i); %>
                 <li>
                  <% if (ricAutomunito.containsAttribute("FLGAUTOMUNITO")) { %>
                      Automunito: <%= ricAutomunito.getAttribute("FLGAUTOMUNITO")%> <br/> 
                  <% } else { %>
                      Automunito: Non richiesto<br/> 
                  <% } %>
                </li>
              <%}%>
          </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>





      
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td align="left" class="ido_bordato" valign="top">Conoscenze linguistiche</td>
        <td valign="top" class="cal_bordato" ><%=(numPesoLingua!=null)?numPesoLingua:new BigDecimal(0)%></td>
        <td class="ido_bordato" valign="top">
          <%if (lavLinguaVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<lavLinguaVec.size();i++){ %>
                 <% lavLingua = (SourceBean)lavLinguaVec.get(i); %>
                 <li><%= lavLingua.getAttribute("STRDENOMINAZIONE") %> <br/>
                    <% if ( lavLingua.containsAttribute("LETTO") ) {
                          appoggio = lavLingua.getAttribute("LETTO").toString();
                          if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                            Livello Letto: <%= appoggio %>
                            <br/>
                         <%}
                        }%>
                    <% if ( lavLingua.containsAttribute("SCRITTO") ){
                         appoggio = lavLingua.getAttribute("SCRITTO").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                              Livello Scritto: <%= appoggio %>
                              <br/>
                         <%}
                       }%>
                    <% if ( lavLingua.containsAttribute("PARLATO") ){
                          appoggio = lavLingua.getAttribute("PARLATO").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%> 
                              Livello Parlato: <%= appoggio %>
                              <br/>
                          <%}
                       }%>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="ido_bordato" valign="top">
            <%if (ricLinguaVec.size()==0) {%>&nbsp;<%}%>
            <ul>
              <% for (int i=0;i<ricLinguaVec.size();i++){ %>
                 <% ricLingua = (SourceBean)ricLinguaVec.get(i); %>
                 <li><%= ricLingua.getAttribute("STRDENOMINAZIONE") %> <br/>
                    <% if ( ricLingua.containsAttribute("LETTO") ) {
                          appoggio = ricLingua.getAttribute("LETTO").toString();
                          if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                            Livello Letto: <%= appoggio %>
                            <br/>
                         <%}
                        }%>
                    <% if ( ricLingua.containsAttribute("SCRITTO") ){
                         appoggio = ricLingua.getAttribute("SCRITTO").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                              Livello Scritto: <%= appoggio %>
                              <br/>
                         <%}
                       }%>
                    <% if ( ricLingua.containsAttribute("PARLATO") ){
                          appoggio = ricLingua.getAttribute("PARLATO").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%> 
                              Livello Parlato: <%= appoggio %>
                              <br/>
                          <%}
                       }%>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td class="ido_bordato" valign="top">Mansioni</td>
        <td valign="top" class="cal_bordato" >
              Mansione: <%=(numPesoMansione!=null)?numPesoMansione:new BigDecimal(0)%><br>
              Esperienza: <%=(numPesoEsp!=null)?numPesoEsp:new BigDecimal(0)%>
        </td>
        <td class="ido_bordato" valign="top" align="left">
          <%if (lavMansioneVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<lavMansioneVec.size();i++){ %>
                 <% lavMansione = (SourceBean)lavMansioneVec.get(i); %>
                 <li><%= lavMansione.getAttribute("STRDESCRIZIONE") %> <br/>
                    <% if ( lavMansione.containsAttribute("FLGDISPONIBILE") ) {
                          appoggio = lavMansione.getAttribute("FLGDISPONIBILE").toString();
                          if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                            Disponibilità: <%= appoggio %>
                            <br/>
                         <%}
                        }%>
                    <% if ( lavMansione.containsAttribute("FLGESPERIENZA") ){
                           appoggio = lavMansione.getAttribute("FLGESPERIENZA").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                              Esperienza Lav.: <%= appoggio %>
                              <br/>
                         <%}
                       }%>
                    <% if ( lavMansione.containsAttribute("FLGESPFORM") ){
                           appoggio = lavMansione.getAttribute("FLGESPFORM").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%> 
                              Esperienza Form.: <%= appoggio %>
                              <br/>
                          <%}
                       }%>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="ido_bordato" valign="top">
          <%if (ricMansioneVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<ricMansioneVec.size();i++){ %>
                 <% ricMansione = (SourceBean)ricMansioneVec.get(i); %>
                 <li><%= ricMansione.getAttribute("STRDESCRIZIONE") %> <br/>
                    <% if ( ricMansione.containsAttribute("FLGDISPONIBILE") ) {
                          appoggio = ricMansione.getAttribute("FLGDISPONIBILE").toString();
                          if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                            Disponibilità: <%= appoggio %>
                            <br/>
                         <%}
                        }%>
                    <% if ( ricMansione.containsAttribute("FLGESPERIENZA") ){
                           appoggio = ricMansione.getAttribute("FLGESPERIENZA").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%>
                              Esperienza Lav.: <%= appoggio %>
                              <br/>
                         <%}
                       }%>
                    <% if ( ricMansione.containsAttribute("FLGFORMAZIONEPROF") ){
                           appoggio = ricMansione.getAttribute("FLGFORMAZIONEPROF").toString();
                           if ( (appoggio != null) && (!appoggio.equals("")) ){%> 
                              Esperienza Form.: <%= appoggio %>
                              <br/>
                          <%}
                       }%>
                 </li>
              <%}%>
          </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>
      <!--tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td class="ido_bordato" colspan="4" align="left">Profilo nr. <%= prgAlternativa %></td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr-->
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td valign="top" class="ido_bordato">Titoli di Studio</td>
        <td valign="top" class="cal_bordato"><%=(numPesoTitolo!=null)?numPesoTitolo:new BigDecimal(0)%></td>
        <td class="ido_bordato">
          <%if (lavStudiVec.size()==0) {%>&nbsp;<%}%>
          <ul>
          <% for (int i=0;i<lavStudiVec.size();i++){ %>
               <% lavStudi = (SourceBean)lavStudiVec.get(i); %>
               <li><%= (lavStudi.getAttribute("DENOMINAZIONE") + " - " + lavStudi.getAttribute("DESCRIZIONE")) %> </li>
            <%}%>
          </ul>
        </td>
        <td valign="top" class="ido_bordato">
          <%if (ricStudiVec.size()==0) {%>&nbsp;<%}%>
          <ul>
            <% for (int i=0;i<ricStudiVec.size();i++){ %>
                <% ricStudi = (SourceBean)ricStudiVec.get(i); %>
                <li><%= (ricStudi.getAttribute("GENERICO") + " - " + ricStudi.getAttribute("SPECIFICO")) %> </li>
             <%}%>
          </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6">&nbsp;</td>
      </tr>
      <tr>
        <td class="cal" align="center" valign="middle" width="6"></td>
        <td valign="top" class="ido_bordato">Conoscenze Informatiche</td>
        <td valign="top" class="cal_bordato"><%=(numPesoInfo!=null)?numPesoInfo:new BigDecimal(0)%></td>
        <td class="ido_bordato" valign="top"> 
            <%if (conInformVec.size()==0) {%>&nbsp;<%}%>
            <ul>
              <% for (int i=0;i<conInformVec.size();i++){%>
                  <% informInfo= (SourceBean)conInformVec.get(i);%>
                  <li><%= informInfo.getAttribute("DESCRIZIONE") + " - Conoscenza: " + informInfo.getAttribute("GRADO") %></li>
              <%}%>
            </ul>
        </td>
        <td valign="top" class="ido_bordato">
          <%if (ricInformVec.size()==0) {%>&nbsp;<%}%>
          <ul>
              <% for (int i=0;i<ricInformVec.size();i++){%>
                  <% ricInform= (SourceBean)ricInformVec.get(i);%>
                  <li><%= ricInform.getAttribute("DESCRIZIONE") + " - Conoscenza: " + ricInform.getAttribute("GRADO") %></li>
              <%}%>
            </ul>
        </td>
        <td class="cal" align="center" valign="middle" width="6"></td>
      </tr>
      <!-- bottom -->
      <tr class="cal">
        <td class="cal_header" valign="bottom" align="left" height="6" width="6"><img src="../../img/angoli/bia4.gif" height="10" width="6"></td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="center" valign="middle">&nbsp;</td>
        <td class="cal_header" align="right" valign="bottom"><img src="../../img/angoli/bia3.gif" height="10" width="6" ></td>
      </tr>
      <!-- end bottom -->
    </table>
<br/>
<table width="100%" class="main">
  <tr>
    <td align="center" colspan="4"><input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="openPage('MatchDettRosaPage','<%=openStr%>')"/></td>
  </tr>
</table>
</body>
</html>
