<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%   
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  	PageAttribs attributi = new PageAttribs(user, "ASListaAdesioniPage");
	boolean canInsert = false;	
	
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");

	canInsert=attributi.containsButton("inserisci");	    
	    
	String strErrorCode = "";
	String msgConferma = "";
	boolean confirm = false;   
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);	
  	String datChiamata = StringUtils.getAttributeStrNotNull(serviceRequest,"datChiamata");
  	if (datChiamata=="") {
  		datChiamata = DateUtils.getNow();
  	}    
	String utente = StringUtils.getAttributeStrNotNull(serviceRequest,"utente");
  	if (utente==""){   
    	utente="1";
  	}		
  	  	
%>


<script>

function go(url, alertFlag) {      
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;   
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}
</script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Ricerca aste</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script language="Javascript">
  function tornaAllaLista()
  {   
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
      
      url="AdapterHTTP?PAGE=ASListaAdesioniPage";
      url += "&CDNFUNZIONE="+"<%=cdnFunzione%>";      
      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
     
      setWindowLocation(url);
  }
 </script>

<script language="Javascript">
  function ControllaData() {
    var bReturnOK = false;
    var dataChiamata = "";
    var dataChiamataInt = 0;
    
    if (document.Frm1.datChiamata.value != "") {
      dataChiamata = new String(document.Frm1.datChiamata.value);
      annoData = dataChiamata.substr(6,4);
      meseData = dataChiamata.substr(3,2);
      giornoData = dataChiamata.substr(0,2);
      dataChiamataInt = parseInt(annoData + meseData + giornoData,10);
    }
    
    var d = new Date();
    annoOdierno = d.getFullYear().toString();
    meseOdierno = (d.getMonth() + 1).toString();
    if (meseOdierno.length == 1) {
      meseOdierno = '0' + meseOdierno;
    }
    giornoOdierno = d.getDate().toString();
    if (giornoOdierno.length == 1) {
      giornoOdierno = '0' + giornoOdierno;
    }
    var dataOdierna = parseInt(annoOdierno + meseOdierno + giornoOdierno,10);        
	
    if (dataChiamataInt > dataOdierna) {
    	alert ("Data Chiamata non può essere maggiore della data corrente");
        bReturnOK = false;
    }    
    else {
        bReturnOK = true;
    }
    
    return bReturnOK;
  }
  
</script>	
</head>   
<body class="gestione" onload="rinfresca();">
<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
<p class="titolo">Ricerca aste</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaData()">
<p align="center">
<table class="main" > 
<tr align="center">	
	<td class="etichetta" style="width:50%;">Data chiamata</td>
  	<td class="campo" style="width:50%;">
    	<af:textBox type="date" name="datChiamata" title="Data chiamata" value="<%=datChiamata%>" size="12" maxlength="10" validateOnPost="true" />
  	</td>		
</tr>

<tr ><td colspan="2" ><hr width="90%"/></td></tr>      
<tr ><td colspan="2" align="center">Utente di inserimento</td></tr>      
<tr ><td colspan="2" align="center">
    <input type="radio" name="utente" value="1" <%if(utente.equals("1")){out.print("checked");}%>> Mio cpi
    <input type="radio" name="utente" value=""  <%if(utente.equals("")){out.print("checked");}%>> Provincia
</td></tr>      
</table>

<table class="main">
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr>
    <td colspan="2" align="center">
      <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
      <input type="reset" class="pulsanti" value="Annulla" />
    </td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr>
    <td colspan="2" align="center">
    	<input class="pulsante" type = "button" name="torna" value="Lista Adesioni" onclick="tornaAllaLista()"/>
    </td>
  </tr>
</table>
 <input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
 <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
 <input type="hidden" name="PAGE" value="ASListaAstePage"/>
 <br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>

</html>