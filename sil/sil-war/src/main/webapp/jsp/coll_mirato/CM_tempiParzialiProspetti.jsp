<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.module.collocamentoMirato.constant.*, it.eng.sil.util.*,java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
	String _pageOpener = (String) serviceRequest.getAttribute("PAGEOPENER");
	ProfileDataFilter filter = new ProfileDataFilter(user, _pageOpener);
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String _page = (String) serviceRequest.getAttribute("PAGE");
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String prgDettPTDisabile = serviceRequest.getAttribute("prgDettPTDisabile") == null ? "" : (String)serviceRequest.getAttribute("prgDettPTDisabile");
  	String codTipoPTDisabile = serviceRequest.getAttribute("codTipoPTDisabile") == null ? "" : (String)serviceRequest.getAttribute("codTipoPTDisabile");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	String message = "";
 	
  	String numLavoratori = "";
  	String decorariosettsvolto = "";
  	String decorariosettcontrattuale = "";  	
  	
  	String codMonoStatoProspetto = "";
  	String flgCompetenza = "";
  	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgCompetenza = (String)prospetto.getAttribute("flgCompetenza");
	}
	
	SourceBean tipoDisabile = (SourceBean) serviceResponse.getAttribute("CMProspDescrTipoDisabile.ROWS.ROW");
	String descrTipoDisabile = "";
	if (tipoDisabile != null) {
		String codiceDisabile = (String)tipoDisabile.getAttribute("codice");
		if (codiceDisabile != null && codiceDisabile.equalsIgnoreCase(ProspettiConstant.CATEGORIA_DISABILE_TELELAVORO)) {
			descrTipoDisabile = "Dettaglio telelavoro per quota parte dell'orario contrattuale";
		}
		else {
			descrTipoDisabile = (String)tipoDisabile.getAttribute("descrizione");
			descrTipoDisabile = "Dettaglio tempo parziale " + descrTipoDisabile;
		}
	}
	
  	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspTempiParzialiDettModule.ROWS.ROW");
	if (dett != null) {	
		message = "UPDATE";					
		numLavoratori = dett.getAttribute("numLavoratori") == null ? "" : ((BigDecimal)dett.getAttribute("numLavoratori")).toString();
		decorariosettsvolto = dett.getAttribute("decorariosettsvolto") == null ? "" : ((BigDecimal)dett.getAttribute("decorariosettsvolto")).toString();
		decorariosettcontrattuale = dett.getAttribute("decorariosettcontrattuale") == null ? "" : ((BigDecimal)dett.getAttribute("decorariosettcontrattuale")).toString();
	}
	else {
		message = "INSERT";			
	}
	
	String token = "_TOKEN_" + _page.toUpperCase();
	String urlDiLista = (String)sessionContainer.getAttribute(token);	
	
	PageAttribs attributi = new PageAttribs(user, _pageOpener);	
	
	boolean canModify =	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }	
	String fieldReadOnly = canModify ? "false" : "true";
  	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify); 	
	
%>

<html>
<head>
<title><%=descrTipoDisabile%></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

 <af:linkScript path="../../js/" />

<script language="javascript">

    function controllaCampi() {		
		
		if (!controllaFixedFloat('decorariosettsvolto', 7, 3)) {			
			return false;
		}
		
		if (!controllaFixedFloat('decorariosettcontrattuale', 7, 3)) {			
			return false;
		}	

		var decorariosettsvolto = parseFloat(document.Frm1.decorariosettsvolto.value);	
		var decorariosettcontrattuale = parseFloat(document.Frm1.decorariosettcontrattuale.value);	

		if (decorariosettcontrattuale <= decorariosettsvolto) {
			alert("Le " + document.Frm1.decorariosettcontrattuale.title + " devono essere maggiori delle " + document.Frm1.decorariosettsvolto.title);
			return false;			
		}

		return true;      
	}
	
	function tornaAllaLista() {
		var f;
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(urlDiLista) %>");							
    }	

</script>

</head>

<body onload="rinfresca();">
<br/>


<%out.print(htmlStreamTop);%>

<p class="titolo"><%=descrTipoDisabile%></p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGEOPENER" value="<%=_pageOpener%>"/>
<input type="hidden" name="PAGE" value="<%=_page%>"/>
<input type="hidden" name="MODULE" value="CMProspTempiParzialiSaveModule"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="MODE" value="LISTA"/> 
<input type="hidden" name="codTipoPTDisabile" value="<%=codTipoPTDisabile%>"/> 
<input type="hidden" name="prgDettPTDisabile" value="<%=prgDettPTDisabile%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>  

<table class="main">				
	<tr>
		<td class="etichetta2" width="50%">Numero Lav.</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavoratori" validateOnPost="true" 
						size="6" value="<%=numLavoratori%>" maxlength="6" readonly="<%=fieldReadOnly%>" 
						required="true" title="Numero Lav."/>
	    </td> 
	</tr>
	<tr>
		<td class="etichetta2" width="50%">Ore lavorate</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="decorariosettsvolto" size="5" maxlength="11"
						value="<%=decorariosettsvolto%>" readonly="<%=fieldReadOnly%>" required="true" title="Ore lavorate"/>
	    </td> 
	</tr>
	<tr>
		<td class="etichetta2" width="50%">Ore Totali</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="decorariosettcontrattuale" size="5" maxlength="11" 
						value="<%=decorariosettcontrattuale%>" readonly="<%=fieldReadOnly%>" required="true" title="Ore Totali"/>
	    </td> 
	</tr>		
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">				   		
			<%
			if (canModify) {
				if (("UPDATE").equalsIgnoreCase(message)) {
			%>
					<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" />	
			<%
				}
				else {
			%>		
					<input type="submit" class="pulsante" name="inserisci" value="Inserisci" />			
			<%
				}
			}
			%>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" class="pulsante" name="torna" value="Torna alla lista" onclick="tornaAllaLista();" />
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>