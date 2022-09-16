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
	String codTipoDocEx="";
	String strNumDocEx="";
	String codMotivoPermSoggEx="";
	String datScadenza="";
	String flgAlloggio="";
	String flgPagamentoRimpatrio="";
	String strTipoDocEx="";
	String strMotivoPermSoggEx="";
	String questuraLav = "";
	
	SourceBean rowDocExCE = null;
	if (serviceRequest.containsAttribute("PRGMOVIMENTO")) {
		rowDocExCE = (SourceBean)serviceResponse.getAttribute("M_MovGetLavDocEX.ROWS.ROW");
	}
	else {
		if (serviceRequest.containsAttribute("PRGMOVIMENTOAPP")) {
			rowDocExCE = (SourceBean)serviceResponse.getAttribute("M_MovAppGetLavDocEX.ROWS.ROW");	
		}
	}
	
	if (rowDocExCE != null) {
		codTipoDocEx = StringUtils.getAttributeStrNotNull(rowDocExCE, "CODTIPODOCEX");
		strTipoDocEx = StringUtils.getAttributeStrNotNull(rowDocExCE, "STRDESCRTIPODOC");
		strNumDocEx = StringUtils.getAttributeStrNotNull(rowDocExCE, "STRNUMDOCEX");
		codMotivoPermSoggEx = StringUtils.getAttributeStrNotNull(rowDocExCE, "CODMOTIVOPERMSOGGEX");
		strMotivoPermSoggEx = StringUtils.getAttributeStrNotNull(rowDocExCE, "STRDESCRMOTIVOPERM");
		datScadenza = StringUtils.getAttributeStrNotNull(rowDocExCE, "DATSCADENZA");
		flgAlloggio = StringUtils.getAttributeStrNotNull(rowDocExCE, "FLGSISTEMAZIONEALLOGGIATIVA");
		flgPagamentoRimpatrio = StringUtils.getAttributeStrNotNull(rowDocExCE, "FLGPAGAMENTORIMPATRIO");
		questuraLav = StringUtils.getAttributeStrNotNull(rowDocExCE, "STRDESCRQUESTURA");
		if (flgAlloggio.equalsIgnoreCase("S")) {
			flgAlloggio = "Si";	
		}
		else {
			if (flgAlloggio.equalsIgnoreCase("N")) {
				flgAlloggio = "No";
			}
		}
		if (flgPagamentoRimpatrio.equalsIgnoreCase("S")) {
			flgPagamentoRimpatrio = "Si";	
		}
		else {
			if (flgPagamentoRimpatrio.equalsIgnoreCase("N")) {
				flgPagamentoRimpatrio = "No";
			}
		}	
	}
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
 <head>
    <title>Carta/Permesso di soggiorno</title>     
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    
<SCRIPT TYPE="text/javascript">
</script>
</head>
<body>

<br/><br/>
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<br/>
<p class="titolo">Carta/Permesso di soggiorno</p>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
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
		<td class="etichetta" nowrap>Sistemazione alloggiativa</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="FLGALLOGGIO" title="Sistemazione alloggiativa" value="<%=flgAlloggio%>" readonly="true"/>
    	</td>
    	<td class="etichetta" nowrap>Pagamento rimpatrio</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="FLGRIMPATRIO" title="Pagamento rimpatrio" value="<%=flgPagamentoRimpatrio%>" readonly="true"/>
    	</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Questura</td>
  		<td class="campo">
  			<af:textBox classNameBase="input" name="QUESTURALAV" title="Questura" value="<%=questuraLav%>" readonly="true"/>
    	</td>
    	<td colspan="2">&nbsp;</td>
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