<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.module.voucher.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
boolean consultaAttivita = serviceRequest.containsAttribute("PRGATTIVITA");
String codStatoVch = "";
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, _page);
boolean canModify = false;
String btnChiudi = "Chiudi senza inserire";

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	// NOTE: Attributi della pagina (pulsanti e link)
	canModify = attributi.containsButton("SALVA");
}
String titlePagina = "Attività svolta";
Testata operatoreInfo = null;
String codVchAttivita = "";
String strNoteAttivita = "";
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
BigDecimal prgAttivita = null;
BigDecimal prgVoucher = null;
BigDecimal numkloattivita = null;

String sPrgVoucher = "";
String sPrgAttivita = "";
String sNumkloAttivita = "";
	
if (consultaAttivita) {
	codStatoVch = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOVOUCHER");
	SourceBean rowAttivita = (SourceBean)serviceResponse.getAttribute("M_DettaglioAttivitaTDA.ROWS.ROW");
	if (rowAttivita != null) {
		prgAttivita = (BigDecimal) rowAttivita.getAttribute("PRGATTIVITA");
		prgVoucher = (BigDecimal) rowAttivita.getAttribute("PRGVOUCHER");
		codVchAttivita = SourceBeanUtils.getAttrStrNotNull(rowAttivita, "CODATTIVITA");
		strNoteAttivita = SourceBeanUtils.getAttrStrNotNull(rowAttivita, "STRNOTEATTIVITA");
		cdnUtIns = rowAttivita.getAttribute("cdnutins").toString();
		dtmIns = rowAttivita.getAttribute("dtmins").toString();
		cdnUtMod = rowAttivita.getAttribute("cdnutmod").toString();
		dtmMod = rowAttivita.getAttribute("dtmmod").toString();
		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		numkloattivita = (BigDecimal) rowAttivita.getAttribute("numkloattivita");
		numkloattivita = numkloattivita.add(new BigDecimal(1));
		sPrgVoucher = prgVoucher.toString();
		sPrgAttivita = prgAttivita.toString();
		sNumkloAttivita = numkloattivita.toString();
		if (canModify) {
			btnChiudi = "Chiudi senza aggiornare";	
		}
		else {
			btnChiudi = "Torna alla lista";	
		}
	}
}
else {
	sPrgVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGVOUCHER");
	SourceBean rowVoucher = (SourceBean)serviceResponse.getAttribute("M_InfoGeneraliTDA.ROWS.ROW");
  	if (rowVoucher != null) {
  		codStatoVch = StringUtils.getAttributeStrNotNull(rowVoucher,"codstatovoucher");	
  	}
}

if (!codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
	canModify = false;
	btnChiudi = "Torna alla lista";
}
	
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Attività svolta</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
<af:linkScript path="../../js/" />

  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>

  <script type="text/javascript">
    $(function() {
    	$("[name='codAttivitaTDA']").selectBoxIt({
            theme: "default",
            defaultText: "Seleziona un'attività...",
            autoWidth: false
        });
       
    });
    </script>

<script type="text/Javascript">
	
	function settaOperazione(operazione) {
		document.Frm1.OPERAZIONEATTIVITA.value = operazione;
		if (operazione == 'CHIUDI') {
			doFormSubmit(document.Frm1);
		} 	
	}
	
</script>

</head>

<body class="gestione" onload="rinfresca();">

<p>
 	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<p class="titolo"><%=titlePagina%></p>
	<% out.print(htmlStreamTop); %>
	<br>
	<table class="main">
		<tr>
			<td class="etichetta" nowrap>Attività&nbsp;</td>
			<td class="campo">
				<af:comboBox classNameBase="input" name="codAttivitaTDA" moduleName="M_GetCodificaAttivitaTDA" required = "true"
            		addBlank="true" selectedValue="<%=codVchAttivita%>" title="Attività" disabled="<%=String.valueOf(!canModify)%>"/>
            </td>
		</tr>
		
		<tr><td colspan="2">&nbsp;</td><tr>
		
		<tr>
			<td class="etichetta" nowrap>Specifica&nbsp;</td>
			<td class="campo">
				<af:textArea name="noteAttivita" cols="120" maxlength="2000"
						rows="15" title="Specifica" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
						value="<%=strNoteAttivita%>"/>
			</td>
		</tr>
	
	</table>
	
	<br>
    <table>
    <tr>
    <td align="center">
    <%
    if (consultaAttivita) {
    	if (canModify) {%>
    		<input type="submit" class="pulsanti" name="AGGIORNAATTIVITA" value="Aggiorna" onclick="settaOperazione('AGGIORNA');">
    	<%}
    } else {
    	if (canModify) {%>
      		<input type="submit" class="pulsanti" name="NUOVAATTIVITA" value="Inserisci" onclick="settaOperazione('INSERISCI');">
      	<%}
    }%>
    </td>
    <td align="center">
    	<input type="button" class="pulsanti" name="CHIUDIATTIVITA" value="<%=btnChiudi%>" onclick="settaOperazione('CHIUDI');">
    </td>
    </tr>
    </table>
    
	<%if (operatoreInfo != null) {%>
  		<br>
		<center>
		<% 
		operatoreInfo.showHTML(out);
		%>
		</center>
	<%}
	out.print(htmlStreamBottom); 
	%>
	<input type="hidden" name="PAGE" value="TDAAttivitaPage">
	<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
	<input type="hidden" name="PRGVOUCHER" value="<%=sPrgVoucher%>">
	<input type="hidden" name="PRGATTIVITA" value="<%=sPrgAttivita%>">
	<input type="hidden" name="NUMKLOATTIVITA" value="<%=sNumkloAttivita%>">
	<input type="hidden" name="OPERAZIONEATTIVITA" value="">
</af:form>

</body>
</html>