<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

String strCodiceFiscale 	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
String strCognome       	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
String strNome          	= StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
String tipoRicerca      	= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
String dataProfiloDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataProfiloDa");  
String dataProfiloA			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataProfiloA"); 
String indiceProfilo        = StringUtils.getAttributeStrNotNull(serviceRequest,"indiceProfilo");
String statoProfilo			= StringUtils.getAttributeStrNotNull(serviceRequest,"statoProfilo");
String codCPIComp 				= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIComp");
String codCPIProf 				= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIProf");

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	<title>Ricerca Profilo Lavoratore</title>
   	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
   	<af:linkScript path="../../js/" />

  	<script language="Javascript">
   	<% 
    	//Genera il Javascript che si occuperà di inserire i links nel footer
   		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
   
    function compDate(data1,data2) {
    	// Metodo che permette il confronto fra due date
    		
    		if (data1!="" && data2!="") {
    		   var y1,y2,m1,m2,d1,d2;
    		   var tmpdata1,tmpdata2;
    		     d1=data1.substring(0,2);
    		     d2=data2.substring(0,2);
    		     m1=data1.substring(3,5);
    		     m2=data2.substring(3,5);
    		     y1=data1.substring(6);
    		     y2=data2.substring(6);
    		  
    		   tmpdata1=y1+m1+d1;
    		   tmpdata2=y2+m2+d2;
    		  
    		   if(tmpdata1>tmpdata2)
    		      return 1;
    			// Ritorna 1 se la data1 e maggiore di data 2      
    		   if(tmpdata2>tmpdata1)   
    		      return 2;
    		      // Ritorna 2 se la data2 e maggiore di data 1
    		   if(tmpdata1==tmpdata2)   
    		      return 0;
    		      // Ritorna 0 se la data1 e uguale a data 2
    		}
    		return 0;
    	}
  	function controllaDate() {

		ok=true;
		
		var objDataProfiloDa=eval("document.forms[0].dataProfiloDa");      	
      	var objDataProfiloA=eval("document.forms[0].dataProfiloA");
     
		dataProfiloDa=objDataProfiloDa.value;
		dataProfiloA=objDataProfiloA.value;
 

		if (compDate(dataProfiloDa, dataProfiloA)==1) {//DA maggiore di A
			alert("Data profilo \'da\' maggiore della data profilo \'a\'");
			objDataProfiloDa.focus();
			ok=false;
		}
		 
  		return ok;
      
      }
  	 
  	 function clearForm(){
  		document.Frm1.reset();
  	 }
   </script>
</head>
    
<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca Profilo Lavoratore</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDate()">
	<table class="main">
    <tr>
    	<td class="etichetta">Codice fiscale</td>
       	<td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" title="Codice fiscale"/>
        </td>
   	</tr>
    <tr>
    	<td class="etichetta">Cognome</td>
        <td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" title="Cognome"/>
       	</td>
    </tr>
    <tr>
     	<td class="etichetta">Nome</td>
        <td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strNome" value="<%=strNome%>" size="20" maxlength="50" title="Nome"/>
       	</td>
    </tr>
    <tr>
    	<td class="etichetta">tipo ricerca</td>
    	<td class="campo">
    	<table colspacing="0" colpadding="0" border="0">
         	<tr>
         	<%if (tipoRicerca.equalsIgnoreCase("iniziaPer")) {%>
         		<td><input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           	<td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per</td>
	        <%} else {%>
	          	<td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	          	<td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td> 	
	        <%}%>
         	</tr>
    	</table>
   		</td>
  	</tr>
	<tr>
    	<td class="etichetta">Data Profilo da</td>
    	  <td class="campo">
       	  <af:textBox type="date" title="Data profilo da" name="dataProfiloDa" value="<%=dataProfiloDa%>" size="10" maxlength="10" required="false" validateOnPost="true" />
          &nbsp;a&nbsp; <af:textBox type="date" title="Data profilo a" name="dataProfiloA" value="<%=dataProfiloA%>" size="10" maxlength="10" required="false" validateOnPost="true"/>
          </td>
   	</tr>
    <tr>
       	<td class="etichetta">Indice profilatura</td>
        <td class="campo">
       		<af:comboBox classNameBase="input" name="indiceProfilo" moduleName="M_GET_VCH_PROFILING" 
            		addBlank="true" selectedValue="<%=indiceProfilo%>" title="Indice"/>
        </td>
   	</tr>
   	<tr>
       	<td class="etichetta">Stato Profilo</td>
        <td class="campo">
       		<af:comboBox classNameBase="input" name="statoProfilo" moduleName="M_ComboStatoProfilo" 
            		addBlank="true" selectedValue="<%=statoProfilo%>" title="Stato Profilo"/>
        </td>
   	</tr>
   	<tr>
	    <td class="etichetta">Centro per l'Impiego competente</td>
	    <td class="campo">
	      <af:comboBox name="codCPIComp" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" 
	      	classNameBase="input" addBlank="true" selectedValue="<%=codCPIComp%>"/>
	    </td>
  	<tr>
  	<tr>
	    <td class="etichetta">Centro per l'Impiego profilo</td>
	    <td class="campo">
	      <af:comboBox name="codCPIProf" title="Centro per l'Impiego creazione profilo" moduleName="M_ELENCOCPI" 
	      	classNameBase="input" addBlank="true" selectedValue="<%=codCPIProf%>"/>
	    </td>
  	<tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
	  	<td colspan="2" align="center">
	  	<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	  	&nbsp;&nbsp;
	  	<input type="reset" class="pulsanti" value="Annulla" onclick="clearForm();"/>
	  	</td>
	</tr>
	<input type="hidden" name="PAGE" value="ListaProfiliLavOpPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>