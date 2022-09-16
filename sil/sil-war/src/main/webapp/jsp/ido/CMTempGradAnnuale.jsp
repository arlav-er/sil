<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
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
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

String p_annoGraduatoria = StringUtils.getAttributeStrNotNull(serviceRequest, "ANNOGRAD");
String p_dataRifGrad = StringUtils.getAttributeStrNotNull(serviceRequest, "DATARIFGRAD");
String p_annoRifReddito = StringUtils.getAttributeStrNotNull(serviceRequest, "ANNORIFREDDITO");
String p_codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOTIPOGRAD");
String p_strnote = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTE");
String p_ambTerr = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");

String flgRigenera = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGRIGENERA");
Object p_prgGraduatoria = serviceRequest.getAttribute("PRGGRADUATORIA");
%>
<html>
	<head>
		<title>Inserimento Graduatoria Annuale</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	    <af:linkScript path="../../js/" />
		<script language="Javascript">
		    function procedi()
		    {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
			 
		      doFormSubmit(document.form_match);
		    }
				    
  		</script>
  
	    	
	</head>
	<body class="gestione" onload="rinfresca();procedi()">
		<!-- messaggi di esito delle operazioni applicative -->
		<font color="red"><af:showErrors/></font>
		
		<af:form method="POST" action="AdapterHTTP" name="form_match" >
  		<input type="hidden" name="PAGE" value="CMInsertGradAnnualePage">
	 	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<%
	 	if (("S").equalsIgnoreCase(flgRigenera)) {
%>
			<input type="hidden" name="MODULE" value="M_RigeneraGradAnnualeModule">		
			<input type="hidden" name="PRGGRADUATORIA" value="<%=p_prgGraduatoria%>">	 <% 
	 	}
		else {
%>
			<input type="hidden" name="MODULE" value="M_InsertGradAnnualeModule">	 	
			<input type="hidden" name="annoGrad" value="<%=p_annoGraduatoria%>">		 	
		 	<input type="hidden" name="dataRifGrad" value="<%=p_dataRifGrad%>">		
		 	<input type="hidden" name="annoRifReddito" value="<%=p_annoRifReddito%>">		
		 	<input type="hidden" name="codMonoTipoGrad" value="<%=p_codMonoTipoGrad%>">		
	 		<input type="hidden" name="strnote" value="<%=p_strnote%>">		
	 		<input type="hidden" name="PROVINCIA_ISCR" value="<%=p_ambTerr%>">
	 		
<%
		}
%>
		<div name="loadDiv" id="loadDiv" >
		<br>	
		<%out.print(htmlStreamTop);%>
			<table class="main">
				<tr valign="middle">
					<td align="center">
							<IMG border="0" src="../../img/hourglass.gif" width="32" height="32"><br>
							<h2>Elaborazione in corso ...</h2></td>
				</tr>
			</table>
		<%out.print(htmlStreamBottom);%>
		</div>  
		</af:form>		 
	</body>
</html>
