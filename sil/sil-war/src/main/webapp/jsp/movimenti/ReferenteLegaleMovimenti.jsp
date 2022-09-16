<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String strTipoDocEx = "";
	String strNumDocEx = "";
	String datScadenza = "";
	String strMotivoPermSoggEx = "";
	String questuraLav = "";
	String strCognomeLegRapp = "";
	String strNomeLegRapp = "";
	String flgSoggInItalia = "";
	SourceBean rowPermSoggLegRapp = null;
	
	if (serviceRequest.containsAttribute("PRGMOVIMENTOAPP")) {
		rowPermSoggLegRapp = (SourceBean)serviceResponse.getAttribute("Mov_App_GetInfoPermSoggEX.ROWS.ROW");	
	}
	else {
		if (serviceRequest.containsAttribute("PRGMOVIMENTO")) {
			rowPermSoggLegRapp = (SourceBean)serviceResponse.getAttribute("Mov_GetInfoPermSoggEX.ROWS.ROW");	
		}
		else {
			if (serviceRequest.containsAttribute("PRGMOVIMENTORETT")) {
				rowPermSoggLegRapp = (SourceBean)serviceResponse.getAttribute("Mov_Rett_GetInfoPermSoggEX.ROWS.ROW");	
			}	
		}
	}
	
	if(rowPermSoggLegRapp != null) {
		strTipoDocEx = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRDESCRTIPODOCEX");
		strNumDocEx = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRNUMDOCEXLEGRAPP");
		datScadenza = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "DATSCADENZALEGRAPP");
		strMotivoPermSoggEx = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRDESCRMOTRIL");
		questuraLav = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRDESCRQUESTURA");
		strCognomeLegRapp = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRCOGNOMELEGRAPP");
		strNomeLegRapp = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "STRNOMELEGRAPP");	
		flgSoggInItalia = StringUtils.getAttributeStrNotNull(rowPermSoggLegRapp, "FLGSOGGINITALIA");
		
	}
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
 <head>
    <title>Legale rappresentante</title>     
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    
<SCRIPT TYPE="text/javascript">
</script>
</head>
<body>

<br/><br/>
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<br/>
<p class="titolo">Legale rappresentante</p>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
	<tr>
  		<td class="etichetta">Cognome</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="strCognomeLegRapp" title="Cognome Referente" value="<%=strCognomeLegRapp%>" readonly="true"/>
  		</td>
  		<td class="etichetta">Nome</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="strNomeLegRapp" title="Nome referente" value="<%=strNomeLegRapp%>" readonly="true"/>
  		</td>
  	</tr>
	<tr>
  		<td class="etichetta">Documento</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="strTipoDocEx" title="Documento" value="<%=strTipoDocEx%>" readonly="true"/>
  		</td>
  		<td class="etichetta">Numero&nbsp;documento</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="strNumDocEx" title="Numero documento" value="<%=strNumDocEx%>" readonly="true"/>
  		</td>
  	</tr>
	<tr>
		<td class="etichetta" nowrap>Motivo della carta/documento</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="strMotivoPermSoggEx" title="Motivo della carta/documento" size="50" value="<%=strMotivoPermSoggEx%>" readonly="true"/>
    	</td>
    	<td class="etichetta">Scadenza in data</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="datScadenza" title="Scadenza in data" value="<%=datScadenza%>" readonly="true"/>
    	</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Questura</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="questuraPermSoggEx" title="Questura" value="<%=questuraLav%>" readonly="true"/>
    	</td>
    	<td class="etichetta" nowrap>Soggiornante in Italia</td>
    	<td class="campo">
    		<input id="flgSoggInItalia" type="checkbox" title="Soggiornante in Italia" name="flgSoggInItaliaCheck" value="" <%=flgSoggInItalia.equals("S") ? "CHECKED" : ""%> 
    		readonly="true" disabled="true"/>
    		<input type="hidden" name="flgSoggInItalia" value="<%=flgSoggInItalia%>"/>
    	</td>
	</tr>
</table>
<%out.print(htmlStreamBottom);%>
<table class="main" border="0">
	<tr>
		<td colspan="2">
    		<input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="javascript:window.close()"/>
        </td>
    </tr>
</table>
</af:form>
</body>
</html>