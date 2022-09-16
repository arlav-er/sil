<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.afExt.utils.StringUtils,
                java.math.*, it.eng.sil.security.* "%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
    int _funzione=0;
    String m_modulo = "";
    boolean aggiornamento=false;
    
  //inizializzo i campi
  //BigDecimal cdnLavoratore=new BigDecimal(0);
  String strCognome="";
  String strNome="";
  String prgNom="";
  BigDecimal prgTipoRosa=new BigDecimal(0);
  //BigDecimal prgRosa=new BigDecimal(0);
  String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
  String cpiRose="";
  BigDecimal numKlo=new BigDecimal(0);
  BigDecimal inc=new BigDecimal(0);
  BigDecimal ver= new BigDecimal(2);
  String strMotiv="";
  //----------
  String prgRichiestaAz="";
  String prgAzienda="";
  String prgUnita="";
  //----------
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
    
    _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    prgNom        = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
    cpiRose        = (String) serviceRequest.getAttribute("CPIROSE");
    //-----------
    prgRichiestaAz  = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
    prgAzienda      = serviceRequest.getAttribute("PRGAZIENDA").toString();
    prgUnita        = serviceRequest.getAttribute("PRGUNITA").toString();
    //-----------
    //m_modulo= (String) serviceRequest.getAttribute("MODULE");
    m_modulo = StringUtils.getAttributeStrNotNull(serviceRequest, "MODULE");
    strMotiv= (String) serviceRequest.getAttribute("STRMOTIVOCANC");
    if (strMotiv == null) {
      strMotiv="";
      }
    if ( (m_modulo != null) && ((m_modulo.compareTo("M_ESCLUDI") == 0 ) || (m_modulo.compareTo("M_ESCL_MASSIVA")==0)) ){
      aggiornamento=true;
    }
    
    SourceBean lavInfo = (SourceBean) serviceResponse.getAttribute("M_GET_NOMINATIVO.ROWS.ROW");
    
    if  ( lavInfo != null )  {
      strCognome       = (String) lavInfo.getAttribute("strCognome");
      strNome       = (String) lavInfo.getAttribute("strNome");
      prgTipoRosa  = (BigDecimal) lavInfo.getAttribute("prgTipoRosa");
      //prgRosa  = (BigDecimal) lavInfo.getAttribute("prgRosa");
      numKlo  = (BigDecimal) lavInfo.getAttribute("numKloNominativo");
      inc = new BigDecimal(numKlo.intValue()+1);
      //cdnLavoratore = (BigDecimal) lavInfo.getAttribute("cdnLavoratore");
    }     
    
    
   // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "EscludiDaRosaPage");
  boolean canModify = attributi.containsButton("rimuovi");
 %>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

String cancMassiva = StringUtils.getAttributeStrNotNull(serviceRequest, "MASSIVA");
String strTitle = "";
if(cancMassiva.equals("")) { strTitle = "Esclusione dalla Rosa"; }
else { strTitle = "Esclusione massiva di candidati dalla Rosa"; }

// Se chiamo la cancellazione massiva devo prendere i pararmetri dalla request
String vPrgNominativo = StringUtils.getAttributeStrNotNull(serviceRequest, "V_PRGNOMINATIVO");
if(!cancMassiva.equals("")) {  
	String strTipoRosa = (String) serviceRequest.getAttribute("PRGTIPOROSA").toString();
	prgTipoRosa = new BigDecimal(strTipoRosa);
}

if(!cancMassiva.equals("") && m_modulo.equals("")) { 
	mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
	listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
	
}
// DOPO una cancellazione massiva devo tornare alla prima pagina
if(m_modulo.equals("M_ESCL_MASSIVA")) { listPage = "1"; }

String openStr = "&PRGROSA=" + prgRosa + "&CPIROSE=" + cpiRose + "&CDNFUNZIONE=" + _funzione;
openStr += "&PRGRICHIESTAAZ=" + prgRichiestaAz + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" +prgUnita;
if(!mess.equals("")) {
	openStr += "&MESSAGE=" + mess;
	if(!listPage.equals("")) { openStr += "&LIST_PAGE=" + listPage; }
}

%>

<html>
<head>
<title><%=strTitle%></title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<SCRIPT language="javascript">
  function controllaCampo(){
    if (trim(document.Frm1.STRMOTIVOCANC.value) == ""){
      alert ("Il campo Motivazione non può essere vuoto.");
      return false;
    }
    else {
      <%if(cancMassiva.equals("")) {%>
	      document.Frm1.MODULE.value="M_ESCLUDI";
	  <%} else {%>
	      document.Frm1.MODULE.value="M_ESCL_MASSIVA";
	  <%}%>
      return true;
    }
  }
</script>
</head>

<body class="gestione">
<%if ( prgTipoRosa.compareTo(ver) != 0 ) { %>
    <table width="100%" class="main">
        <tr><td><br/></td></tr>
        <tr>
          <td class="etichetta" align="left">
            <ul>
              <LI>Impossibile cancellare l'utente selezionato</LI>
            </ul>
          </td>
        </tr>
    </table>   
  <%}%>
<font color="green">
<%if(cancMassiva.equals("")) {%>
  <af:showMessages prefix="M_ESCLUDI"/>
<%} else {%>
  <af:showMessages prefix="M_ESCL_MASSIVA"/>
<%}%>
</font>
<font color="red">
  <af:showErrors/>
</font>
<p class="titolo"><b><%=strTitle%></b></p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaCampo()">
      
      <input type="hidden" name="PAGE" value="EscludiDaRosaPage">
      <input type="hidden" name="MODULE" value="">
      <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
      <input type="hidden" name="PRGROSA" value="<%= prgRosa %>"/>
      <input type="hidden" name="CPIROSE" value="<%= cpiRose %>"/>
      <input type="hidden" name="PRGTIPOROSA" value="<%= prgTipoRosa %>"/>
      <input type="hidden" name="NUMKLONOMINATIVO" value="<%= inc %>"/>
      
      <input type="hidden" name="PRGNOMINATIVO" value="<%= prgNom %>"/>
      <input type="hidden" name="V_PRGNOMINATIVO" value="<%=vPrgNominativo%>"/>
      

      <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
      <input type="hidden" name="PRGAZIENDA" value="<%= prgAzienda %>"/>
      <input type="hidden" name="PRGUNITA" value="<%= prgUnita %>"/>
      <%if(!mess.equals("")) {%>
			<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
			<%if(!listPage.equals("")) {%>
				<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
	 		<%}%>
	  <%}%>
      <%if(cancMassiva.equals("1")) {%>
      		<input type="hidden" name="MASSIVA" value="<%=cancMassiva%>"/>
      <%}%>
      <table width="100%" class="main">
      <%if(cancMassiva.equals("")) {%>
        <tr>
          <td class="etichetta">Candidato</td>
          <td class="campo">
            <af:textBox classNameBase="input" name="candidato" size="100" value="<%= strCognome + ' ' + strNome %>" readonly="true" />               
          </td>
        </tr>
        <tr><td><br/></td></tr>
        <%}%>
        <tr valign="top">
          <td class="etichetta">Motivazione</td>
          <td class="campo">
            <%if (aggiornamento) { %>
              <af:textArea classNameBase="textarea" name="STRMOTIVOCANC" maxlength="100" cols="50" value="<%= strMotiv %>" readonly="<%= String.valueOf(aggiornamento) %>" title="Motivazione" required="true"/>
            <% } else {%>
              <af:textArea classNameBase="textarea" name="STRMOTIVOCANC" maxlength="100" cols="50" value="" readonly="<%= String.valueOf(!canModify) %>" title="Motivazione" required="true" /> 
              <% } %>
          </td>
        </tr>  
        <tr><td><br/></td></tr>
        <tr>
          <% if ( canModify && (prgTipoRosa.compareTo(ver) == 0) && (!aggiornamento) ) { %>            
            <td colspan="2" align="center">
                <input type="submit" class="pulsanti" name="salva" value="Aggiorna">
                <input type="reset" class="pulsanti" name="annulla" value="Annulla">
            </td>
          <%}%>
        </tr>
        <tr>
          <td colspan="2" align="center">
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="openPage('MatchDettRosaPage','<%=openStr%>');">
          </td>
        </tr>
    </table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
