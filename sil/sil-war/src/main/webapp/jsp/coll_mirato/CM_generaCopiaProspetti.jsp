<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspDettPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

  	String _page = (String) serviceRequest.getAttribute("PAGE");   
  	    
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	String codProvincia = "";
	String numannorifprospetto = "";
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
  	  	
  	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {						
		codProvincia = (prospetto.getAttribute("codprovincia")).toString();
		numannorifprospetto = ((BigDecimal)prospetto.getAttribute("numannorifprospetto")).toString();
	}
  	
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<title>Copia Prospetto</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

 <af:linkScript path="../../js/" />
<script language="javascript">			
	
	function generaCopia() {
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";		
		url += "&MODULE=CMGeneraCopiaRiepilogoModule";
	    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    url += "&CODPROVINCIA="+document.Frm1.codProvincia.value;
	    url += "&NUMANNORIFPROSPETTO="+document.Frm1.numannorifprospetto.value;
	    url += "&CDNFUNZIONE=<%=cdnFunzione%>";
	    url += "&codStatoAtto=NP";
	    url += "&CHECKANNULLA=0";
	    url += "&nuovo=true";
	    //setWindowLocation(url);   
	    window.opener.location.replace(url);
	    window.close();
	}
	
</script>	  
</head>

<body onload="rinfresca();">
<br/> 


<%out.print(htmlStreamTop);%>

<p class="titolo"> Copia Prospetto </p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="CMProspRiepilogoPage"/>
<input type="hidden" name="MODULE" value="CMGeneraCopiaRiepilogoModule"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>  
<input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>  
<table class="main">				
    <tr>	
		<td class="etichetta2">Anno</td>
		<td class="campo2">
			<af:textBox type="integer" title="Anno" name="numannorifprospetto" value="<%= numannorifprospetto%>" size="4" maxlength="4" validateOnPost="true" required="true" />
	    </td>	    
	</tr>
	<tr>
		<td class="etichetta2">Provincia</td>
		<td class="campo2">
			<af:comboBox name="codProvincia" multiple="false" moduleName="M_GetIDOProvince" selectedValue="<%=codProvincia%>" required="true" />
	    </td> 	                            
	</tr>    	
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">				   			
			<input type="button" class="pulsante" name="inserisci" value="Genera Copia" onclick="generaCopia();"/>			
			&nbsp;&nbsp;&nbsp;
			<input type="button" class="pulsante" name="chiusi" value="Chiudi" onclick="window.close();" />			
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>