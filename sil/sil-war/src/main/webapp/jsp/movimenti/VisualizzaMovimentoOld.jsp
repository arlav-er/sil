<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  java.math.*,
                  java.io.*,
                  java.util.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String datInizioMov="";
  String codMonoTempo="";
  String datFineMov="";
  String codOrario="";
  String strDescOrario="";
  String numOreSett="";
  String codTipoAss="";
  String descTipoAss="";
  String codNormativa="";
  String strDescNormativa="";
  String codMansione="";
  String strDescMansione="";
  String codAgevolazione="";
  String strDescAgevolazione="";
  String codGrado="";
  String strDescGrado="";
  String codCCNL="";
  String strDescCCNL="";
  String codMotCessazione="";
  String strDescMotCessazione="";
  String prgMovimento="";
  String prgMovimentoRett="";
  String prgMovimentoProtDaRett="";
  String codTipoMov="";
  String codCategoria="";
  String strDescCategoria="";
  String strAziNtNumContratto="";
  String codTipoContratto="";
  String pageRitornoLista ="";
  
  
  String queryString = null;
  
  boolean canModify = false;
  prgMovimento = StringUtils.getAttributeStrNotNull(serviceRequest,"prgMovimento");
  prgMovimentoRett = StringUtils.getAttributeStrNotNull(serviceRequest,"prgMovimentoRett");
  prgMovimentoProtDaRett = StringUtils.getAttributeStrNotNull(serviceRequest,"prgMovimentoProtDaRett");
  codTipoMov = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMOV");
  pageRitornoLista = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGERITORNOLISTA");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String currentcontext = StringUtils.getAttributeStrNotNull(serviceRequest,"CURRENTCONTEXT");
  
  SourceBean rowOld = (SourceBean)serviceResponse.getAttribute("M_MovGetDettMovOld.ROWS.ROW");
  if(rowOld!= null) {
  	datInizioMov = StringUtils.getAttributeStrNotNull(rowOld, "datInizioMov");
  	codMonoTempo = StringUtils.getAttributeStrNotNull(rowOld, "codMonoTempo");
  	datFineMov = StringUtils.getAttributeStrNotNull(rowOld, "datFineMov");
  	codOrario = StringUtils.getAttributeStrNotNull(rowOld, "codOrario");
  	strDescOrario = StringUtils.getAttributeStrNotNull(rowOld, "strDescOrario");
  	numOreSett = StringUtils.getAttributeStrNotNull(rowOld, "numOreSett");
  	codTipoAss = StringUtils.getAttributeStrNotNull(rowOld, "codTipoAss");
  	descTipoAss = StringUtils.getAttributeStrNotNull(rowOld, "STRDESCTIPOASS");
  	codNormativa = StringUtils.getAttributeStrNotNull(rowOld, "codNormativa");
  	strDescNormativa = StringUtils.getAttributeStrNotNull(rowOld, "strDescNormativa");
  	codMansione = StringUtils.getAttributeStrNotNull(rowOld, "codMansione");
  	strDescMansione = StringUtils.getAttributeStrNotNull(rowOld, "strDescMansione");
  	codAgevolazione = StringUtils.getAttributeStrNotNull(rowOld, "codAgevolazione");
  	strDescAgevolazione = StringUtils.getAttributeStrNotNull(rowOld, "strDescAgevolazione");
  	codGrado = StringUtils.getAttributeStrNotNull(rowOld, "codGrado");
  	strDescGrado = StringUtils.getAttributeStrNotNull(rowOld, "strDescGrado");
  	codCCNL = StringUtils.getAttributeStrNotNull(rowOld, "codCCNL");
  	strDescCCNL = StringUtils.getAttributeStrNotNull(rowOld, "strDescCCNL");
  	codMotCessazione = StringUtils.getAttributeStrNotNull(rowOld, "codMotCessazione");
  	strDescMotCessazione = StringUtils.getAttributeStrNotNull(rowOld, "strDescMotCessazione");
  	codCategoria = StringUtils.getAttributeStrNotNull(rowOld, "CODCATEGORIA");
  	strDescCategoria = StringUtils.getAttributeStrNotNull(rowOld, "STRDESCCATEGORIA");
  	strAziNtNumContratto = StringUtils.getAttributeStrNotNull(rowOld, "STRAZINTNUMCONTRATTO");
  	codTipoContratto = StringUtils.getAttributeStrNotNull(rowOld, "CODTIPOCONTRATTO");
  }
  
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
%>

<html>

<head>
<title>Dettaglio movimento salvato</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<script  language="JavaScript">


function tornaMovimenti(){
	var url = "AdapterHTTP?PAGE=MovDettaglioGeneraleConsultaPage&MostraTra=MostraTra";
		url+="&prgMovimento=<%=prgMovimento%>";
		url+="&CDNFUNZIONE=<%=_funzione%>";
		url+="&prgMovimentoRett=<%=prgMovimentoRett%>";
		url+="&PRGMOVIMENTOPROTDARETTIFICA=<%=prgMovimentoProtDaRett%>";
		url+="&CODTIPOMOV=<%=codTipoMov%>";
		url+="&PAGERITORNOLISTA=<%=pageRitornoLista%>";
		url+="&CURRENTCONTEXT=<%=currentcontext%>";
		setWindowLocation(url);
}

function stampaMovimentoOld(prgMovimento) {
	apriGestioneDoc('RPT_BACKUP_MOVIMENTI','&prgMovimento='+ prgMovimento,'STMOV');
}

</script>


</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Dettaglio movimento salvato</p>

<font color="red"><af:showErrors/></font>

<af:form name="form1" method="POST" action="AdapterHTTP">

<%= htmlStreamTop %>
<table class="main" border="0" align="left">

<% if(codTipoMov.equals("CES")) { %>
<tr>
	<td class="etichetta">Motivo Cessazione</td>
	<td class="campo" colspan="5">
		<af:textBox title="Codice del motivo cessazione" value="<%=codMotCessazione%>" classNameBase="input" name="codMotCessazione" size="4" maxlength="3" readonly="<%=String.valueOf(!canModify)%>"/>   
		<af:textBox classNameBase="input" name="strDescMotCessazione" value="<%=strDescMotCessazione%>" title="Motivo Cessazione" readonly="<%=String.valueOf(!canModify)%>"/>   
	</td>
</tr>
<% } %>

<tr>
	<td class="etichetta">Data inizio</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" type="date" validateOnPost="true" name="datInizioMov" title="Data Inizio"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>
<tr>
	<td class="etichetta">Tempo</td>
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:comboBox classNameBase="input" name="codMonoTempo" selectedValue="<%=codMonoTempo%>" title="Tempo" disabled="<%=String.valueOf(!canModify)%>" addBlank="true">
						<option value="D" <% if (codMonoTempo.equals("D")) {%>selected="true" <%}%>>Determinato</option>
						<option value="I" <% if (codMonoTempo.equals("I")) {%>selected="true" <%}%>>Indeterminato</option>
					</af:comboBox>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td id="scadenza" style="display: <%=( (codMonoTempo.equals("I") || codMonoTempo.equals("")) ) ? "none" : "" %>" class="etichetta2">Data fine</td>
				<td class="campo2" id="datascadenza" style="display: <%=(codMonoTempo.equals("I") || codMonoTempo.equals("")) ? "none" : "" %>" nowrap="true">
					<af:textBox classNameBase="input" type="date" validateOnPost="true" name="datFineMov" title="scadenza"  size="12" value="<%=datFineMov%>" readonly="<%=String.valueOf(!canModify)%>" />
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta">Orario</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:textBox classNameBase="input" value="<%=strDescOrario%>" title="Orario" size="30" name="codOrario" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
				<td>&nbsp;</td>
				<td id="labelore" style="display:<%=(codOrario.equals("F") || codOrario.equals("TP1") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;" class="etichetta2">Ore settimanali</td>
				<td class="campo2" id="numore" style="display:<%=(codOrario.equals("F") || codOrario.equals("TP1") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" title="\'Ore settimanali\'" maxlength="9" size="12" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta">COD&nbsp;tipo&nbsp;contratto</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="codTipoContratto" value="<%=codTipoContratto%>" name="codTipoContratto" size="8" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">Tipo di Avviamento</td>
	<td class="campo" colspan="5">
		<af:textBox title="Codice del tipo di avviamento" value="<%=codTipoAss%>" classNameBase="input" name="codTipoAss" size="4" maxlength="3" readonly="<%=String.valueOf(!canModify)%>"/> 
        <af:textBox title="Descrizione del tipo di avviamento" value="<%=descTipoAss%>" classNameBase="input" name="descTipoAss" size="50" readonly="<%=String.valueOf(!canModify)%>"/>         
	</td>
</tr>

<tr>
	<td class="etichetta">Normativa/Motivo di utilizzo</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" name="codNormativa" title="Normativa" value="<%=strDescNormativa%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>            
</tr>           
<tr>
	<td class="etichetta">Qualifica</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="Qualifica" name="CODMANSIONE" size="7" maxlength="7" value="<%=codMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
		<af:textBox classNameBase="input" size="60" name="DESCMANSIONE" value="<%=strDescMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
    </td>	
</tr>

<tr>
	<td class="etichetta">Grado</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="Grado" value="<%=strDescGrado%>" name="codGradoVisualizz" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>   
<tr>
	<td class="etichetta">CCNL</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="CCNL" name="codCCNL" value="<%=codCCNL%>" size="4" maxlength="3" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		<af:textBox classNameBase="input" name="strCCNL" value="<%=strDescCCNL%>" size="60" readonly="<%=String.valueOf(!canModify)%>"/>
    </td>
</tr>

<tr>
	<td class="etichetta">Benefici</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="Benefici" value="<%=strDescAgevolazione%>" name="codAgevolazione" size="80" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr> 
<tr>
	<td class="etichetta">Categoria agr.</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="codCategoria" name="codCategoria" value="<%=codCategoria%>" size="4" maxlength="3" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		<af:textBox classNameBase="input" name="strDescCategoria" value="<%=strDescCategoria%>" size="60" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">Num.&nbsp;contratto&nbsp;somm.</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="strAziNtNumContratto" value="<%=strAziNtNumContratto%>" name="strAziNtNumContratto" size="80" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr> 

<tr><td colspan=2>&nbsp;</td></tr>  
<tr>
	<td align="center" colspan="5">
     	<input type="button" class="pulsante" name="Stampa" value="Stampa" onClick="stampaMovimentoOld(<%=prgMovimento%>)"/>
		<input type="button" class="pulsanti" value="Chiudi" onclick="tornaMovimenti()">
	</td>
</tr>
	
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
