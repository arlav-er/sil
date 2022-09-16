<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@page import="it.eng.afExt.utils.DateUtils"%>
        
<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,java.util.*,
                  it.eng.afExt.utils.StringUtils" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String presesso = (String)serviceRequest.getAttribute("SESSO");
    
    boolean checkM = presesso.compareTo("M")==0?true:false;
    
    
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    String sysdate = DateUtils.getNow();
%>


<html>
<head>
<af:linkScript path="../../js/"/>
    <% String queryString = null; %>
    
    <%@ include file="../documenti/_apriGestioneDoc.inc"%>
	<title>Lista cancellati</title>
	
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">

function stampaLista() {
	//che bello un for per due sfaccimm di valori
	for (var i=0; i < document.frm1.FLGSESSO.length; i++)
	   {
		//alert(i);
	      if (document.frm1.FLGSESSO[i].checked)
	      {
	       var rad_val = document.frm1.FLGSESSO[i].value;
	      }
	   }
	var datef = document.frm1.dataInDA.value;	
	//apre pop-up di stampa passando i parms necessari
    apriGestioneDoc('RPT_SERVIZIO_LAVORO','&sesso='+rad_val+'&datada='+datef,'ST_TNT');
}


function checkCampiObbligatori()
{
	var ok;
	var msg;
	msg = "Parametro obbligatorio:\n"+
	"Data Obbligatoria\n";
		//Controlla che i campi non siano vuoti
		if (document.frm1.dataInDA.value == "") {
			alert(msg);
			return false;
		}
		else
			stampaLista();
}
</script>

</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Liste Provinciali Iscritti Legge 223/91</p>
<af:form name="frm1" action="AdapterHTTP" onSubmit="checkCampiObbligatori()" method="POST">
<input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">

<% out.print(htmlStreamTop); %> 
  <table>  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  
  <tr>
	 <td class="etichetta">Data </td>
	 <td class="campo" >
	    <af:textBox type="date" name="dataInDA" title="dal" value="<%=sysdate%>" size="10" maxlength="10"/>
	    &nbsp;
	 </td>            
  </tr>
  
  <tr>
    <td class="etichetta">Sesso</td>
    <td class="campo"><input type="radio" name="FLGSESSO" value="M" <%=checkM?"checked":""%>> Uomini<br>        
                      <input type="radio" name="FLGSESSO" value="F" <%=!checkM?"checked":""%>> Donne<br>
          
    	</td>
  </tr>

  <tr>
  <td colspan="2">&nbsp;</td>
  </tr>
  
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti"  name="Stampa" value="Stampa">
    &nbsp;&nbsp;
        
    </td>
  </tr>
  </table>
  <%out.print(htmlStreamBottom);%>
  </af:form>
  
<table>
<tr><td class="campo2">N.B.: La stampa riporter&agrave; tutti gli iscritti alla legge 223/91 con data fine lista minore o uguale a sei mesi dalla data specificata e l'et&agrave; degli uomini maggiore di 50 anni (45 per le donne) ordinati alfabeticamente per cognome e nome.</td></tr>        
</table>

</body>
</html>
