<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} 

	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String codMonoStatoProspetto = "";
	String flgCompetenza = "";
	String numSommFT = "";
	String numSommPT = "";
	String numSommPTOrarioOltre50 = "";
	String numSommRiprPTOrarioMeno50 = "";
	String numConv12Bis14FT = "";  
	String numConvPT = "";
	String numConvPTOrarioOltre50 = "";
	String numConvRiprPTOrarioMeno50 = "";
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;	
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgCompetenza = (String)prospetto.getAttribute("flgCompetenza");
		
		numSommFT = prospetto.getAttribute("numSommFT") == null ? "" : ((BigDecimal)prospetto.getAttribute("numSommFT")).toString();
		numConv12Bis14FT = prospetto.getAttribute("numConv12Bis14FT") == null ? "" : ((BigDecimal)prospetto.getAttribute("numConv12Bis14FT")).toString();
		
		cdnUtIns = prospetto.getAttribute("cdnUtIns");
		dtmIns = prospetto.getAttribute("dtmIns");
		cdnUtMod = prospetto.getAttribute("cdnUtMod");
		dtmMod = prospetto.getAttribute("dtmMod");	
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		
		SourceBean sommeTPPersNonDip = (SourceBean) serviceResponse.getAttribute("CMProspSommeDettDisabiliModule.ROWS.ROW");
		if (sommeTPPersNonDip != null) {
			numSommPT = sommeTPPersNonDip.getAttribute("numPersPT_S") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numPersPT_S")).toString();
			numSommPTOrarioOltre50 = sommeTPPersNonDip.getAttribute("numPersPTOltre50_S") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numPersPTOltre50_S")).toString();
			numSommRiprPTOrarioMeno50 = sommeTPPersNonDip.getAttribute("numRiprPTMeno50_S") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numRiprPTMeno50_S")).toString();
			BigDecimal totale = null;
			if (numSommRiprPTOrarioMeno50 != null) {
				double risRiproporz = new Double(numSommRiprPTOrarioMeno50).doubleValue();
				int parteIntera = new BigDecimal(risRiproporz).intValue();
				if (risRiproporz - parteIntera > 0.5) {
					totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
				}
				else {
					totale = new BigDecimal(parteIntera);
				}
				numSommRiprPTOrarioMeno50 = totale.toString();	
			}
			numConvPT = sommeTPPersNonDip.getAttribute("numPersPT_V") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numPersPT_V")).toString();
			numConvPTOrarioOltre50 = sommeTPPersNonDip.getAttribute("numPersPTOltre50_V") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numPersPTOltre50_V")).toString();
			numConvRiprPTOrarioMeno50 = sommeTPPersNonDip.getAttribute("numRiprPTMeno50_V") == null ? "0" : ((BigDecimal)sommeTPPersNonDip.getAttribute("numRiprPTMeno50_V")).toString();
			if (numConvRiprPTOrarioMeno50 != null) {
				double risRiproporz = new Double(numConvRiprPTOrarioMeno50).doubleValue();
				int parteIntera = new BigDecimal(risRiproporz).intValue();
				if (risRiproporz - parteIntera > 0.5) {
					totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
				}
				else {
					totale = new BigDecimal(parteIntera);
				}
				numConvRiprPTOrarioMeno50 = totale.toString();	
			}
		}
	}		
	
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, _page);	
	
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
<title>Personale Non Dipendente</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	

	function calcolaValTempiParziali(codTipoPTDisabile) {
		
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=CMProspTempiParzialiPage";		
	    url += "&MODULE=CMProspTempiParzialiListModule";
	    url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    url += "&CODTIPOPTDISABILE=" + codTipoPTDisabile; 
	    url += "&PAGEOPENER=<%=_page%>";
	    url += "&MODE=LISTA";
	    
	    window.open(url, "", 'toolbar=NO,statusbar=YES,height=350,width=850,scrollbars=YES,resizable=YES');	
	}

	function reloadMe() {
	  	if (isInSubmit()) return;
	  	
	  	queryString = "PAGE=<%=_page%>&CDNFUNZIONE=<%=cdnfunzione%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&PRGPROSPETTOINF=<%=prgProspettoInf%>&CODSTATOATTO=<%=codStatoAtto%>";

	  	window.location = "AdapterHTTP?" + queryString;
	}	
	
</script>
<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 
</head>

<body class="gestione" onload="rinfresca();">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspPersNonDipSaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="<%=_page%>"/>
<input type="hidden" name="MODULE" value="CMProspPersNonDipSaveModule"/>
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>
 
<%out.print(htmlStreamTop);%>

<table class="main" border="0">	
	<tr><td colspan="8">&nbsp;</td></tr>			
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Lavoratori disabili somministrati (art. 34, co. 3, D.Lgs. 81/2015)    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone Tempo Pieno</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSommFT" size="6" value="<%=numSommFT%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone Tempo Pieno - Lavoratori disabili somministrati" />
	    </td>	
	    <td class="etichetta2">N° tot. persone tempo parziale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSommPT" size="6" value="<%=numSommPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° persone con orario >50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSommPTOrarioOltre50" size="6" value="<%=numSommPTOrarioOltre50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('S')" />
		</td>    
	</tr> 	
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° riproporz. con orario <=50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numSommRiprPTOrarioMeno50" size="6" value="<%=numSommRiprPTOrarioMeno50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>
	</tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Lavoratori disabili in convenzione artt. 12-bis e 14    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone Tempo Pieno</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numConv12Bis14FT" size="6" value="<%=numConv12Bis14FT%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone Tempo Pieno - Lavoratori disabili in convenzione" />
	    </td>	
	    <td class="etichetta2">N° tot. persone tempo parziale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numConvPT" size="6" value="<%=numConvPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° persone con orario >50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numConvPTOrarioOltre50" size="6" value="<%=numConvPTOrarioOltre50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('V')" />
		</td>   
	</tr> 	
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° riproporz. con orario <=50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numConvRiprPTOrarioMeno50" size="6" value="<%=numConvRiprPTOrarioMeno50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>	
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"></div>
     	</td>
	</tr>
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr>
		<td colspan="8" >		   		
		    <% 
			if (canModify) {
			%>	
				<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" />			
			<%
			}
			%>
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>

</af:form>

<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>

</body>
</html>