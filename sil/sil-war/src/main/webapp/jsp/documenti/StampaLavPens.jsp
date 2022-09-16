<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                 java.math.*"
%>

<%@ taglib uri="aftags" prefix="af" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String _page = (String)serviceRequest.getAttribute("PAGE");

String eta = StringUtils.getAttributeStrNotNull(serviceRequest,"eta");
String etaMaggiore = StringUtils.getAttributeStrNotNull(serviceRequest,"etaMaggiore");
String etaUguale = StringUtils.getAttributeStrNotNull(serviceRequest,"etaUguale");
String codCMTipoIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoIscr");

String dataRichDa = StringUtils.getAttributeStrNotNull(serviceRequest,"dataRichDa");
String dataRichA = StringUtils.getAttributeStrNotNull(serviceRequest,"dataRichA");

String queryString=null;

double calcolaDec = 0;



%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
   <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 
  <script type="text/javascript">
    
  function stampaLavoratori() {
	
	var eta;
	
	var etaMaggiore = document.form1.etaMaggiore.value;
	var etaUguale = document.form1.etaUguale.value;
	var codCMTipoIscr = document.form1.codCMTipoIscr.value;
	
	var calcola=document.form1.eta.length;
    for (var i = 0; i < calcola; i++)
    { 
       	if(document.form1.eta[i].checked) { 
       		eta=document.form1.eta[i].value; 
       		break; 
       	} 
    }
    if(etaMaggiore!="" || etaUguale!=""){
		apriGestioneDoc('RPT_STAMPA_LAVORATORI','&eta='+eta+'&etaMaggiore='+etaMaggiore+'&etaUguale='+etaUguale+'&codCMTipoIscr='+codCMTipoIscr, 'L68_ETA');  
		undoSubmit();
	}else{
		alert("Inserire l'età");
	}
  }

  function abilitaEta(){
	var eta;
	var calcola = document.form1.eta.length;
	var anni;
    for (var i = 0; i < calcola; i++)
    { 
       	if(document.form1.eta[i].checked) { 
       		eta=document.form1.eta[i].value; 
       		break; 
       	} 
    }
    if(eta == "maggiore"){
    	document.form1.etaUguale.value = "";
	  	document.form1.etaUguale.disabled = true;
	  	document.form1.etaMaggiore.disabled = false;
	  	
    }else if(eta == "uguale"){
    	document.form1.etaMaggiore.value = "";
		document.form1.etaUguale.disabled = false;
		document.form1.etaMaggiore.disabled = true;    	
    }
  }
  
</script>
 
</head>

<body class="gestione" onload="abilitaEta()">
<br>
<p class="titolo">Stampa dei lavoratori in età pensionabile</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET">
	
	<table class="main">
		<tr>
	       <td class="etichetta">Età:</td>
	       <td class="campo">
		       <table colspacing="0" colpadding="0" border="0">
			       <tr>
			           <td>
				          	<input type="radio" name="eta" value="maggiore" onClick="javaScript: abilitaEta();"/> maggiore di&nbsp;
				          	<af:textBox type="text" name="etaMaggiore" disabled="false" value="<%=etaMaggiore%>" size="3" maxlength="3"/>				          	
				       </td>				       
				       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				       <td>
				          	<input type="radio" name="eta" value="uguale" onClick="javaScript: abilitaEta();" CHECKED /> uguale a&nbsp;
				          	<af:textBox type="text" name="etaUguale" disabled="false" value="<%=etaUguale%>" size="3" maxlength="3"/>
				       </td>
			       </tr>
		       </table>
	       </td>
	    </tr>
	    <tr colspan="2">&nbsp;</tr>
	    <tr>
	       <td class="etichetta">Tipo iscrizione</td>
		   <td class="campo">
		      <af:comboBox
			      name="codCMTipoIscr"
			      title="tipoIscr"
			      classNameBase="input"
			      disabled="false">
			      <option value=""  <% if ( "".equalsIgnoreCase(codCMTipoIscr) )  { %>SELECTED="true"<% } %> ></option>
			      <option value="D" <% if ( "D".equalsIgnoreCase(codCMTipoIscr) ) { %>SELECTED="true"<% } %> >Disabili</option>
			      <option value="A" <% if ( "A".equalsIgnoreCase(codCMTipoIscr) ) { %>SELECTED="true"<% } %> >Altre categorie protette</option>
		      </af:comboBox>
		   </td>
		</tr>
	</table>
	
	<br/>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampaLavoratori()" /></center>
	<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>


