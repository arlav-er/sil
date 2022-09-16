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
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

String annoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"annoGrad");
String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
String statoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"statoGrad");
String PROVINCIA_ISCR      = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

%>

<html>
	<head>
		<title>Ricerca graduatoria annuale</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
    	<af:linkScript path="../../js/" />
    	
    	<script type="text/Javascript">
			  
			  function nuovaGradAnnuale()
			  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
			     if (isInSubmit()) return;
			     url="AdapterHTTP?PAGE=CMNewGradAnnualePage";
			     url+="&goBackListPage=CMRicercaGradAnnualePage";			   
			     url+= "&CDNFUNZIONE="+"<%=_funzione%>";
			     setWindowLocation(url);
			  }
  			  
  		</script>
    	
    	 	
     
   </head>
   
   <body class="gestione" onload="rinfresca()">
   		<p class="titolo">Ricerca graduatoria annuale</p>
   		 <%out.print(htmlStreamTop);%>
   		 
  		<af:form method="POST" action="AdapterHTTP" name="Frm1" >
      	<table class="main">
	    <tr>
          <td class="etichetta">Ambito Territoriale</td>
          <td class="campo">
    	  	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" title="Ambito Territoriale"  addBlank="true" onChange="getTxt(this)" />
        	<input type="hidden" name="provinciaTerrit" />
       	  </td>
       	</tr>
        <tr>
          <td class="etichetta">Anno</td>
          <td class="campo">
            <af:textBox type="text" name="annoGrad" value="<%=annoGrad%>" maxlength="4" size="4"/>
          </td>
        </tr> 
        <tr>
          <td class="etichetta">Stato graduatoria</td>
          <td class="campo">
            <af:comboBox name="statoGrad" moduleName="M_GetTipoGrad" addBlank="true" selectedValue="<%=statoGrad%>" >
            </af:comboBox>
          </td>
        </tr> 
        <tr>
          <td class="etichetta">Tipo graduatoria</td>
          <td class="campo">
            <af:comboBox name="codMonoTipoGrad" classNameBase="input" disabled="false" >	  	
	   			<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %> ></option>            
	   			<option value="D" <% if ( "D".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.8</option>
	   			<option value="A" <% if ( "A".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.18</option>                					        		
			</af:comboBox>
          </td>
        </tr> 
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
          &nbsp;&nbsp;
          <input type="reset" class="pulsanti" value="Annulla" />
			
          </td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
        	<td colspan="2">
        	<input class="pulsanti" type="button" name="nuovaGrad" value="Nuova graduatoria" onclick="nuovaGradAnnuale()" />
        	</td>
        </tr>
        <input type="hidden" name="PAGE" value="CMListaGradAnnualePage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      </table>
   		</af:form> 
   		 <%out.print(htmlStreamBottom);%>
   </body>
</html>
   
