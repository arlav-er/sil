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
String dataInvioDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioDa");  
String dataInvioA			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioA"); 
String cpiCompetente = StringUtils.getAttributeStrNotNull(serviceRequest,"codcpi");

//prevalorizzazione
if(StringUtils.isEmptyNoBlank(cpiCompetente)){
	cpiCompetente = user.getCodRif();
}



String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	<title>Ricerca Errori di Invio Automatico SAP</title>
   	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
   	<af:linkScript path="../../js/" />

  	<script language="Javascript">
   	<% 
    	//Genera il Javascript che si occuperà di inserire i links nel footer
   		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
	
  	function stringToDate(_date,_format,_delimiter)
  	{
  	            var formatLowerCase=_format.toLowerCase();
  	            var formatItems=formatLowerCase.split(_delimiter);
  	            var dateItems=_date.split(_delimiter);
  	            var monthIndex=formatItems.indexOf("mm");
  	            var dayIndex=formatItems.indexOf("dd");
  	            var yearIndex=formatItems.indexOf("yyyy");
  	            var month=parseInt(dateItems[monthIndex]);
  	            month-=1;
  	            var formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);
  	            return formatedDate;
  	}
   
  	function differenzaGiorni(dat1, dat2){
  		
  		var d1 = stringToDate(dat1,"dd/MM/yyyy","/");
  		var d2 = stringToDate(dat2,"dd/MM/yyyy","/");
        var t2 = d2.getTime();
        var t1 = d1.getTime();

        return parseInt((t2-t1)/(24*3600*1000));
  	}
  	
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
		
		var objDataInvioDa=eval("document.forms[0].dataInvioDa");      	
      	var objDataInvioA=eval("document.forms[0].dataInvioA");
     
		dataInvioDa=objDataInvioDa.value;
		dataInvioA=objDataInvioA.value;
 

		if (compDate(dataInvioDa, dataInvioA)==1) {//DA maggiore di A
			alert("Data invio \'da\' maggiore della data invio \'a\'");
			objDataInvioDa.focus();
			ok=false;
		}
		
		if(ok){
			var numeroGiorni = differenzaGiorni(dataInvioDa, dataInvioA);
			if(numeroGiorni > 30){
				alert("E' consentita la ricerca su un periodo massimo di 30 giorni");
				objDataInvioA.focus();
				ok=false;
			}
		}
		
  		return ok;
      
      }
  	 
  	 function clearForm(){
  		document.getElementById("Frm1_").reset();
  		document.Frm1.codcpi.value=""; 
  	 }
   </script>
</head>
    
<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca Errori di Invio Automatico SAP</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" id="Frm1_" onSubmit="controllaDate()">
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
    	<td class="etichetta">Data Invio da</td>
    	  <td class="campo">
       	  <af:textBox type="date" title="Data Invio da" name="dataInvioDa" value="<%=dataInvioDa%>" size="10" maxlength="10" required="true" validateOnPost="true" />
          &nbsp;a&nbsp; <af:textBox type="date" title="Data Invio a" name="dataInvioA" value="<%=dataInvioA%>" size="10" maxlength="10" required="true" validateOnPost="true"/>
          </td>
   	</tr>
  				<tr>
					<td class="etichetta">Centro per l'Impiego competente</td>
				    <td class="campo">
				    	<af:comboBox name="codcpi" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=cpiCompetente%>" required="false" />
				    </td>
				</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
	  	<td colspan="2" align="center">
	  	<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	  	&nbsp;&nbsp;
	  	<input type="reset" class="pulsanti" value="Annulla" onclick="clearForm();"/>
	  	</td>
	</tr>
	<input type="hidden" name="PAGE" value="ListaErroriSapPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>