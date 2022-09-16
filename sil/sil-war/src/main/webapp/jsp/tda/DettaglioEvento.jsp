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
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, _page);
boolean canModify = false;
String codStatoVch = "";
String btnChiudi = "Chiudi senza inserire";

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	// NOTE: Attributi della pagina (pulsanti e link)
	canModify = attributi.containsButton("SALVA");
}
String titlePagina = "";
Testata operatoreInfo = null;
String codVchEvento = "";
String strNoteEvento = "";
String cdnUtIns = "";
String dtmIns = "";
String dtmEvento = "";
String cdnUtMod = "";
String dtmMod = "";
BigDecimal prgEvento = null;
BigDecimal prgVoucher = null;
BigDecimal numkloEvento = null;

String sPrgVoucher = "";
String sPrgEvento = "";
String sNumkloEvento = "";

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
boolean consultaEvento = serviceRequest.containsAttribute("PRGEVENTO");
titlePagina = "Eventi";
	
if (consultaEvento) {
	codStatoVch = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOVOUCHER");
	SourceBean rowEvento = (SourceBean)serviceResponse.getAttribute("M_DettaglioEventoTDA.ROWS.ROW");
	if (rowEvento != null) {
		prgEvento = (BigDecimal) rowEvento.getAttribute("PRGEVENTO");
		prgVoucher = (BigDecimal) rowEvento.getAttribute("PRGVOUCHER");
		codVchEvento = SourceBeanUtils.getAttrStrNotNull(rowEvento, "CODEVENTO");
		strNoteEvento = SourceBeanUtils.getAttrStrNotNull(rowEvento, "STRNOTEVENTO");
		cdnUtIns = rowEvento.getAttribute("cdnutins").toString();
		dtmIns = rowEvento.getAttribute("dtmins").toString();
		dtmEvento = rowEvento.getAttribute("dtmevento").toString();
		cdnUtMod = rowEvento.getAttribute("cdnutmod").toString();
		dtmMod = rowEvento.getAttribute("dtmmod").toString();
		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		numkloEvento = (BigDecimal) rowEvento.getAttribute("numkloEvento");
		numkloEvento = numkloEvento.add(new BigDecimal(1));
		sPrgVoucher = prgVoucher.toString();
		sPrgEvento = prgEvento.toString();
		sNumkloEvento = numkloEvento.toString();
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
<title>Eventi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	var isEditing = <%=canModify%>;
	
	function settaOperazione(operazione) {
		document.Frm1.OPERAZIONEEVENTO.value = operazione;
		if (operazione == 'CHIUDI') {
			doFormSubmit(document.Frm1);
		} 	
	}
	

	function controllaCampi() {

		if (!isEditing) {
			return true;
		}
		
		var today = new Date();
		// dd/mm/yyyy
		var stringDateEv = document.Frm1.dataEvento.value.split("/");
		// yyyy, mm ,dd
		var dataEv = new Date(stringDateEv[2], stringDateEv[1]-1, stringDateEv[0]);
		if (dataEv > today) {
			alert('Non è possibile utilizzare per gli eventi una data futura');
			return false;
		}

		return true;
	}
</script>

</head>

<body class="gestione" onload="rinfresca();">

<p>
 	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form method="POST" action="AdapterHTTP" name="Frm1"  onSubmit="controllaCampi()">
	<p class="titolo"><%=titlePagina%></p>
	<% out.print(htmlStreamTop); %>
	<br>
	<table class="main">
		<tr>
			<td class="etichetta" nowrap>Tipo Contatto&nbsp;</td>
			<td class="campo">
				<af:comboBox classNameBase="input" name="codEventoTDA" moduleName="M_GetTipoContattoEventoTDA" required="true"
            		addBlank="true" selectedValue="<%=codVchEvento%>" title="Tipo Contatto" disabled="<%=String.valueOf(!canModify)%>" />
            </td>
		</tr>
		
		<tr>
			<td class="etichetta" nowrap>Data&nbsp;</td>
			<td class="campo">
				<af:textBox name="dataEvento" type="date" title="Data Evento" readonly="<%=String.valueOf(!canModify)%>"
				size="11" maxlength="10" classNameBase="input"
				value="<%=dtmEvento%>" required="true"/>
			</td>
		<tr>
		<tr>
			<td class="etichetta" nowrap ></td>
			<td class="campo">
		 
		<textarea class="inputView" title="warning" readonly="true" cols="90" rows="2" style="resize: none;font-weight: bold;border:none !important;background-color: inherit;">ATTENZIONE: in caso di "Colloquio di Lavoro", nel campo note, vanno inseriti i dati dell'azienda presso la quale si &egrave; svolto il colloquio (Ragione sociale - C.F. - recapito telefonico) - in caso contrario il T.D.A. potrebbe non essere corrisposto</textarea>
		</td>
		</tr>
	
		<tr>
			<td class="etichetta" nowrap>Note&nbsp;</td>
			<td class="campo">
				<af:textArea name="noteEvento" cols="90" maxlength="500" rows="15" title="Note Evento" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
						value="<%=strNoteEvento%>"/>
			</td>
		</tr>
	
	</table>
	
	<br>
    <table>
    <tr>
    <td align="center">
    <%
    if (consultaEvento) {
    	if (canModify) {%>
    		<input type="submit" class="pulsanti" name="AGGIORNAEVENTO" value="Aggiorna" onclick="settaOperazione('AGGIORNA');">
    	<%}
    } else {
    	if (canModify) {%>
      		<input type="submit" class="pulsanti" name="NUOVOEVENTO" value="Inserisci" onclick="settaOperazione('INSERISCI');">
      	<%}
    }%>
    </td>
    <td align="center">
    	<input type="button" class="pulsanti" name="CHIUDIEVENTO" value="<%=btnChiudi%>" onclick="settaOperazione('CHIUDI');">
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
	<input type="hidden" name="PAGE" value="TDAEventiPage">
	<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
	<input type="hidden" name="PRGVOUCHER" value="<%=sPrgVoucher%>">
	<input type="hidden" name="PRGEVENTO" value="<%=sPrgEvento%>">
	<input type="hidden" name="NUMKLOEVENTO" value="<%=sNumkloEvento%>">
	<input type="hidden" name="OPERAZIONEEVENTO" value="">
</af:form>

</body>
</html>