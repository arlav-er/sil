<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter,
                it.eng.sil.module.coop.GetDatiPersonali,
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%	
	//
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	String cdnLavoratore= "0";//(String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	// Savino 14/12/2006: set inutile e potenzialmente pericoloso
	//filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _current_page);
	boolean canViewButTrasf = false;
	boolean canModify = false;
	boolean canAggiornaCpi = false;
	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}	
    // se in precedenza il lavoratore e' stato trasferito ma la stampa del trasferimento, per qualche motivo,
    // non e' stata fatta allora potra' essere stampata successivamente da questa jsp.
    //boolean stampaTrasferimento = false; 

    //Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}
	    
%>

<%
  //variabili di navigazione


  
  //inizializzo i campi
  //String cdnLavoratore="";
  String queryString = null;
  String strNome="";
  String strCognome="";
  String codComRes="";
  String strComRes="";
  String provRes="";
  String strIndirizzores="";
  String strLocalitares="";
  String strCapRes="";
  String codComdom="";
  String strComdom="";
  String provDom="";
  String strIndirizzodom="";
  String strLocalitadom="";
  String strCapDom="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  String codCpiTit="";
  String strCpiTit="";
  String codMonoTipoCpi = "";
  String codCpiOrig = "";
  String strCpiOrig = "";
  
  String strTelRes="";
  String strTelDom="";
  String strTelAltro="";
  String strCell="";
  String strEmail="";
  String strFax="";
  String flgStampaTrasf = null;
  String flgStampaDoc = null;
  String numKloLavStoriaInf="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  //Controlla se il lavoratore ha uno storico dei trasferimenti e visualizza l'eventale pulsante.
  //boolean storicoTrasf = serviceResponse.containsAttribute("M_TrasferimentiStoricoElenco.ROWS.ROW");
  
  
  String response_xml="M_COOP_AnagIndirizzi_dalla_cache.ROWS.ROW";
    
  try {

    SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);
    //Recapiti
    if (row!=null) {
	    if (row.containsAttribute("STRTELRES")) { strTelRes = row.getAttribute("STRTELRES").toString();}
	    if (row.containsAttribute("STRTELDOM")) { strTelDom = row.getAttribute("STRTELDOM").toString();}
	    if (row.containsAttribute("STRTELALTRO")) { strTelAltro = row.getAttribute("STRTELALTRO").toString();}
	    if (row.containsAttribute("STRCELL")) { strCell = row.getAttribute("STRCELL").toString();}
	    if (row.containsAttribute("STREMAIL")) { strEmail = row.getAttribute("STREMAIL").toString();}
	    if (row.containsAttribute("STRFAX")) { strFax = row.getAttribute("STRFAX").toString();}
	      
	    if (row.containsAttribute("codComRes")) {codComRes=row.getAttribute("codComRes").toString();}
	    if (row.containsAttribute("provRes")) {provRes=row.getAttribute("provRes").toString();}
	    if (row.containsAttribute("strComRes")) {
	       strComRes=row.getAttribute("strComRes").toString()+(!provRes.equals("")?" ("+provRes+")":"");
	    }
	    if (row.containsAttribute("strIndirizzores")) {strIndirizzores=row.getAttribute("strIndirizzores").toString();}
	    if (row.containsAttribute("strLocalitares")) {strLocalitares=row.getAttribute("strLocalitares").toString();}
	    if (row.containsAttribute("strCapRes")) {strCapRes=row.getAttribute("strCapRes").toString();}
	    if (row.containsAttribute("codComdom")) {codComdom=row.getAttribute("codComdom").toString();}
	    if (row.containsAttribute("provDom")) {provDom=row.getAttribute("provDom").toString();}
	    if (row.containsAttribute("strComdom")) {
	        strComdom=row.getAttribute("strComdom").toString()+(!provDom.equals("")?" ("+provDom+")":"");
	    }
	    if (row.containsAttribute("strIndirizzodom")) {strIndirizzodom=row.getAttribute("strIndirizzodom").toString();}
	    if (row.containsAttribute("strLocalitadom")) {strLocalitadom=row.getAttribute("strLocalitadom").toString();}
	    if (row.containsAttribute("strCapDom")) {strCapDom=row.getAttribute("strCapDom").toString();}
	    /* TODO Savino: 10/10/2006 campi non usati
	    if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
	    if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
	    if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
	    if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
	    if (row.containsAttribute("numKloLavoratore")) {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}
	    */
	    if (row.containsAttribute("CODCPITIT")) {codCpiTit=row.getAttribute("CODCPITIT").toString();}
	    if (row.containsAttribute("STRDESCRIZIONE")) {strCpiTit=row.getAttribute("STRDESCRIZIONE").toString();}
	    if (row.containsAttribute("CODMONOTIPOCPI")) {codMonoTipoCpi = (String)row.getAttribute("CODMONOTIPOCPI");}  
	    if (row.containsAttribute("CODCPIORIG")) {codCpiOrig=row.getAttribute("CODCPIORIG").toString();}
	    if (row.containsAttribute("STRDESCRIZIONEORIG")) {strCpiOrig=row.getAttribute("STRDESCRIZIONEORIG").toString();}
	    /* TODO Savino: 10/10/2006 campo non usato
	    if (row.containsAttribute("NumKloLavStoriaInf")) {numKloLavStoriaInf=row.getAttribute("NumKloLavStoriaInf").toString();}
	    */
	    /* TODO Savino: 10/10/2006 campo non usato
	    // recupero le informazioni sul trasferimento del lavoratore/presa atto
	    // e sulla relativa stampa avvenuta con successo o meno
	    flgStampaTrasf = (String)row.getAttribute("FLGSTAMPATRASF");  
	    flgStampaDoc = (String)row.getAttribute("FLGSTAMPADOC");  
	    */
    }
  }
  catch(Exception ex){
    // non faccio niente
  }
 
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  

%>

<html>
<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
 <af:linkScript path="../../js/"/>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script language="Javascript" src="../../js/docAssocia.js"></script>


<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<SCRIPT TYPE="text/javascript">


// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    
  }
  
function codCPIUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}


function checkCap(inputName){

  var capObj = eval("document.forms[0]." + inputName);
  cap=capObj.value;
  ok=true;
  if ((cap.length>0) && (cap.length<5)) {
    ok=false;
  }

  if (!ok) {
    alert("Il " + capObj.title + " deve essere di 5 cifre");
    capObj.focus();
  }
  return ok;
}
      


function apriCheckMaster(cdnLav) {
		var f = "AdapterHTTP?PAGE=CheckMasterIRPage&CDNLAVORATORE=" + cdnLav;
	    var t = "CPIMaster";	    
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=300,top=75,left=100";
		window.open(f, t, feat);	
	

}


</SCRIPT>


</head>
<body class="gestione" onload="">
 
<%@ include file="_testataLavoratore.inc" %>
<%@ include file="_linguetta.inc" %>

 <font color="red">
     <af:showErrors/>
 </font>
 
<af:form method="POST" action="AdapterHTTP" name="Frm1" >

<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>

<p align="center">
<%out.print(htmlStreamTop);%>
<table  class="main" border="0">
<!-- RESIDENZA ET DOMICILIO -->
<tr>
<td colspan="4" class="titolo"><br/><center><b>Domicilio</b></center></td>
</tr>
<tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input"  title="indirizzo domicilio" type="text" name="strIndirizzodom" value="<%=strIndirizzodom%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input"  type="text" name="strLocalitadom" value="<%=strLocalitadom%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" type="text" name="codComdom" value="<%=codComdom%>" title="codice comune domicilio"            
            size="4" maxlength="4" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>" 
        />&nbsp;                
        <af:textBox classNameBase="input" type="text"  name="strComdom" value="<%=strComdom%>"            
            size="50" maxlength="50" title="comune di domicilio" 
            readonly="<%= String.valueOf(!canModify) %>" 
        />&nbsp;*&nbsp;&nbsp;                
  <td class="etichetta">Cap&nbsp;</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strCapDom" value="<%=strCapDom%>"           
          title="Cap del domicilio" type="text"  size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>"
      />
  </td>
</tr>
<tr>
    <td class="etichetta">Telefono Domicilio&nbsp;</td>
    <td class="campo" colspan="3"><af:textBox classNameBase="input" type="text" name="strTelDom" value="<%=strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/>      
    </td>
</tr>

<tr>
    <td class="etichetta">
      <% if(codMonoTipoCpi.equals("C")){ %>
        Centro per l'impiego ai sensi del D. Lgs 150
      <%} else { 
              if(codMonoTipoCpi.equals("T")){ %>
                Centro per l'impiego titolare
        <%    }
          }%>
    </td>
    <td class="campo">        
        <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=codCpiTit%>"             
            size="10" maxlength="9" 
            readonly="true"
        />&nbsp;
        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiTit%>"            
            size="30" maxlength="50" 
            readonly="true"
        />&nbsp;
        <%-- campo valorizzato dalla funzione javaScript di FindComune.jsp --%>
			<% if(codMonoTipoCpi.equals("T") && !codCpiOrig.equals("")){ %>
				<br/><STRONG>(CPI Competente:&nbsp;&nbsp; <%=codCpiOrig%>&nbsp;<%=strCpiOrig%>)</STRONG>
			<%}%>                
  </td>
  <td colspan="2"></td>
</tr>

<tr>
<td colspan="4" class="titolo"><center><b>Residenza</b></center></td>
</tr>
<tr>
    <td class="etichetta" >Indirizzo&nbsp;</td>
    <td class="campo" ><af:textBox classNameBase="input" title="indirizzo residenza" type="text" name="strIndirizzores" value="<%=strIndirizzores%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strLocalitares" value="<%=strLocalitares%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>

<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" 
                                  type="text" name="codComRes" value="<%=codComRes%>" title="codice comune di residenza"
                                  size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <af:textBox classNameBase="input" 
                type="text" name="strComRes" value="<%=strComRes%>" size="50" maxlength="50" title="comune di residenza"
                readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;
    </td>
    <!-- *********** -->
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>" />
    </td>
</tr>

<tr>
    <td class="etichetta">Telefono Residenza&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strTelRes" value="<%=strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
<tr><td><br/></td></tr>
<tr ><td colspan="4" ><hr width="90%"/></td></tr>
<tr>
    <td class="etichetta">Altro telefono&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strTelAltro" value="<%=strTelAltro%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
  <td class="etichetta" >&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Cellulare&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strCell" value="<%=strCell%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
  <td class="etichetta" >&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Email&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strEmail" value="<%=strEmail%>" size="80" maxlength="80" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
    <td class="etichetta" >&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Fax&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" type="text" name="strFax" value="<%=strFax%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
    <td class="etichetta" >&nbsp;</td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
</table>
<p/>
<center>

<%out.print(htmlStreamBottom);%>
</p>
<br/>

</af:form>
  
</body>
</html>
