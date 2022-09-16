<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.util.* " %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String _page=it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(serviceRequest,"page");
  PageAttribs attributi = new PageAttribs(user, _page);

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  String labelStatoRich = "";
  String resultConfigMob = serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString():"0";
  if (resultConfigMob.equals("0")) {
	  labelStatoRich = "Stato della richiesta";  
  }
  else {
	  labelStatoRich = "Stato della domanda";  
  }
  
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String CodTipoLista=StringUtils.getAttributeStrNotNull(serviceRequest,"CodTipoLista");
  String datdomandada=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandada");
  String datdomandaa=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandaa");
  String datfineda=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineda");
  String datfinea=StringUtils.getAttributeStrNotNull(serviceRequest,"datfinea");      
  String datinizioda=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
  String datinizioa=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  boolean readOnlyStr = false;
%>
<html>
<head>
<title>Ricerca Mobilit&agrave;</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">
<% 
   //Genera il Javascript che si occuperà di inserire i links nel footer
   attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>


</script>

<SCRIPT TYPE="text/javascript">
  	function controlloDate() {
	    var objDataDa = document.form1.datinizioda;
	    var objDataA = document.form1.datinizioa;    
		if ((objDataDa.value != "") && (objDataA.value != "")) {
	      if (compareDate(objDataDa.value,objDataA.value) > 0) {
	      	alert(objDataDa.title + " maggiore di " + objDataA.title);
	      	objDataDa.focus();
		    return false;
		  }	
		}
	    objDataDa = document.form1.datfineda;
	    objDataA = document.form1.datfinea;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
	      if (compareDate(objDataDa.value,objDataA.value) > 0) {
	      	alert(objDataDa.title + " maggiore di " + objDataA.title);
	      	objDataDa.focus();
		    return false;
		  }	
		}
		objDataDa = document.form1.datdomandada;
	    objDataA = document.form1.datdomandaa;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
	      if (compareDate(objDataDa.value,objDataA.value) > 0) {
	      	alert(objDataDa.title + " maggiore di " + objDataA.title);
	      	objDataDa.focus();
		    return false;
		  }	
		}
	    return true;
  	}
  
</SCRIPT>

</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca mobilit&agrave; da approvare</p>
<p align="center">
  <af:form name="form1" action="AdapterHTTP" method="GET" onSubmit="controlloDate()"> 
  <%out.print(htmlStreamTop);%> 
  <table class="main" border="0">
   
  <tr><td>&nbsp;</td><tr>
  <tr><td colspan="2" ><div class="sezione2"/>Dati Iscrizione</td></tr>
  
  <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_TIPO_LISTA_SOSPESA" 
		multiple="true"/>     
     </td>
   </tr>
   
   <tr>
	 <td class="etichetta" nowrap>Data domanda da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datdomandada" title="Data domanda da" validateOnPost="true" value="<%=datdomandada%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datdomandaa" title="Data domanda a" validateOnPost="true" value="<%=datdomandaa%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
   
   <tr>
	 <td class="etichetta" nowrap>Data inizio da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" value="<%=datinizioda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" value="<%=datinizioa%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data fine da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datfineda" title="Data fine da" validateOnPost="true" value="<%=datfineda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datfinea" title="Data fine a" validateOnPost="true" value="<%=datfinea%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
   
   <tr> 
     <td class="etichetta"><%=labelStatoRich %></td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodStatoMob" title="<%=labelStatoRich %>" 
		    moduleName="M_MO_STATO" multiple="true"/>     
     </td>
   </tr>
   
   <tr><td colspan="2"><div class="sezione2"></td></tr>
   <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" 
      required="true" addBlank="true" selectedValue="<%=CodCPI%>"/>
    </td>
  	<tr>
   
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla"/>
    </td>
   </tr>
   
   <input type="hidden" name="PAGE" value="MobilitaApprovazioneRisultRicercaPage"/>
   <input type="hidden" name="cdnfunzione" value="<%=_funzione%>"/>
   <input type="hidden" name="OP_APPROVAZIONE" value="false"/>
   
  </table>
</af:form>
</p>
 <%out.print(htmlStreamBottom);%>
</body>
</html>