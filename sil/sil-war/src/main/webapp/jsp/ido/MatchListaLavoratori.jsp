<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%String _page = (String)serviceRequest.getAttribute("PAGE");
String cdnFunzione =
	serviceRequest.containsAttribute("CDNFUNZIONE") ? serviceRequest.getAttribute("CDNFUNZIONE").toString() : "";
boolean contattoAutomatico =
	StringUtils.getAttributeStrNotNull(serviceResponse, "M_InfoTsGenerale.rows.row.flgContattoAutomRosa").equals("S");
//contattoAutomatico = true;
String prgSpiUt  = 	Utils.notNull(serviceResponse.getAttribute( "MSPI_UTENTE.rows.row.prgSpi"));
boolean canModify = true;

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<html>
<head>
<title>Lista lavoratori</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />

<script language="JavaScript">
  function selezionaSpi (){
  	document.Frm1.operatoreSpi.value='<%=prgSpiUt%>';
  }  
  function tornaAllaRicerca() {
  	if (isInSubmit()) return;
  	queryString = "CDNFUNZIONE=<%=cdnFunzione%>&PAGE=MatchRicercaLavPage";  	
  	window.location = "AdapterHTTP?" + queryString;
  }
  function doSelect(cdnLavoratore, cdnFunzione)   {
  	var prgTipoContatto = "";
  	var prgSpiContatto = "";
  	<%if (contattoAutomatico) {%> 
    	prgTipoContatto = document.Frm1.contatto.value;
    	prgSpiContatto =  document.Frm1.operatoreSpi.value;
    	if (!controllaFunzTL()) return;
    <%}%>
  	opener.aggiungiLavoratore(cdnLavoratore, prgSpiContatto, prgTipoContatto);
  	// verra' chiamata la close della pop-up dalla pagina opener
  }
  </script>

</head>

<body onload="checkError();<%=(prgSpiUt.equals(""))?"":"selezionaSpi()"%>" class="gestione">
	<br/>
	<af:error />
	<p align="center">
	<%if (contattoAutomatico) {
		out.print(htmlStreamTop); 
	%>
		<div class="sezione2">Creazione automatica contatto</div>
	<af:form name="Frm1" method="POST" action="AdapterHTTP"	dontValidate="true">
		<TABLE>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<td class="etichetta2">Tipo contatto
				<td class="campo2"><af:comboBox moduleName="COMBO_TIPO_CONTATTO_AG" required="true"
					name="contatto" />
			<tr>
				<td class="etichetta2">Operatore
				<td class="campo2"><af:comboBox moduleName="COMBO_SPI" addBlank="TRUE" selectedValue="" 
					required="true" name="operatoreSpi" />
		</TABLE>
	</af:form> 
	<%   
		out.print(htmlStreamBottom);
	}%> 
	<af:list moduleName="M_ListaLavoratoriIncrocio" jsSelect="doSelect" /> 
	<table class="main">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center"><input type="button" class="pulsanti"
				value="Ritorna alla pagina di ricerca" onClick="tornaAllaRicerca()">
			</td>
		</tr>
		<tr>
			<td align="center"><input type="button" class="pulsanti"
				value="Chiudi" onClick="window.close()"></td>
		</tr>
	</table>
	</p>	
</body>
</html>
