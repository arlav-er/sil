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
	String message = "";
	String numdisabili = "";
	String numDisPT = "";
	String numDisPTOrarioOltre50 = "";
	String numDisRiprPTOrarioMeno50 = "";
	String numprotette = "";
	String numCentNonVedentiForza = "";
	String numCentPT = "";
	String numCentPTOrarioOltre50 = "";
	String numCentRiprPTOrarioMeno50 = "";
	String numMassofisioterapistiForza = "";
	String numMassoPT = "";
	String numMassoPTOrarioOltre50 = "";
	String numMassoRiprPTOrarioMeno50 = "";
	String numTeleLavFT = "";
	String numTeleLavPT = "";
	String numTeleLavRiprPT = "";
	
	BigDecimal partTime = new BigDecimal(0);
	boolean checkPartTime = false;
	String numPartTime = "";
	
	boolean checkIntermittenti = false;
	String numIntermittenti = "";
	BigDecimal nIntermittenti = new BigDecimal(0);
	
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
		
		numCentNonVedentiForza = prospetto.getAttribute("numCentNonVedentiForza") == null ? "" : ((BigDecimal)prospetto.getAttribute("numCentNonVedentiForza")).toString();
		numMassofisioterapistiForza = prospetto.getAttribute("numMassofisioterapistiForza") == null ? "" : ((BigDecimal)prospetto.getAttribute("numMassofisioterapistiForza")).toString();
		numTeleLavFT = prospetto.getAttribute("numTeleLavFT") == null ? "" : ((BigDecimal)prospetto.getAttribute("numTeleLavFT")).toString();
		
		cdnUtIns = prospetto.getAttribute("cdnUtIns");
		dtmIns = prospetto.getAttribute("dtmIns");
		cdnUtMod = prospetto.getAttribute("cdnUtMod");
		dtmMod = prospetto.getAttribute("dtmMod");	
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}		
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspEsclusioniDettModule.ROWS.ROW");				
	if (dett != null) {	
		message = "UPDATE";			
		numPartTime = dett.getAttribute("numPartTime") == null ? "" : ((BigDecimal)dett.getAttribute("numPartTime")).toString();
		partTime = (BigDecimal)dett.getAttribute("checkPartTime");
		if (partTime.compareTo(new BigDecimal(0)) > 0) {		
			checkPartTime = true;
		}
		numdisabili = dett.getAttribute("numdisabili") == null ? "0" : ((BigDecimal)dett.getAttribute("numdisabili")).toString();
		numprotette = dett.getAttribute("numprotette") == null ? "0" : ((BigDecimal)dett.getAttribute("numprotette")).toString();
		numIntermittenti = dett.getAttribute("numIntermittenti") == null ? "" : ((BigDecimal)dett.getAttribute("numIntermittenti")).toString();
		nIntermittenti = (BigDecimal)dett.getAttribute("checkIntermittenti");
		if (nIntermittenti.compareTo(new BigDecimal(0)) > 0) {		
			checkIntermittenti = true;
		}
		flgCompetenza = StringUtils.getAttributeStrNotNull(dett,"flgCompetenza");
	}
	else {
		message = "INSERT";
		flgCompetenza = "S";
	}
	
	SourceBean sommeTPPersDip = (SourceBean) serviceResponse.getAttribute("CMProspSommeDettDisabiliModule.ROWS.ROW");
	if (sommeTPPersDip != null) {
		numDisPT = sommeTPPersDip.getAttribute("numPersPT_D") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPT_D")).toString();
		numDisPTOrarioOltre50 = sommeTPPersDip.getAttribute("numPersPTOltre50_D") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPTOltre50_D")).toString();
		numDisRiprPTOrarioMeno50 = sommeTPPersDip.getAttribute("numRiprPTMeno50_D") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numRiprPTMeno50_D")).toString();
		BigDecimal totale = null;
		if (numDisRiprPTOrarioMeno50 != null) {
			double risRiproporz = new Double(numDisRiprPTOrarioMeno50).doubleValue();
			int parteIntera = new BigDecimal(risRiproporz).intValue();
			if (risRiproporz - parteIntera > 0.5) {
				totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
			}
			else {
				totale = new BigDecimal(parteIntera);
			}
			numDisRiprPTOrarioMeno50 = totale.toString();	
		}
		numCentPT = sommeTPPersDip.getAttribute("numPersPT_C") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPT_C")).toString();
		numCentPTOrarioOltre50 = sommeTPPersDip.getAttribute("numPersPTOltre50_C") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPTOltre50_C")).toString();
		numCentRiprPTOrarioMeno50 = sommeTPPersDip.getAttribute("numRiprPTMeno50_C") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numRiprPTMeno50_C")).toString();
		if (numCentRiprPTOrarioMeno50 != null) {
			double risRiproporz = new Double(numCentRiprPTOrarioMeno50).doubleValue();
			int parteIntera = new BigDecimal(risRiproporz).intValue();
			if (risRiproporz - parteIntera > 0.5) {
				totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
			}
			else {
				totale = new BigDecimal(parteIntera);
			}
			numCentRiprPTOrarioMeno50 = totale.toString();	
		}
		numMassoPT = sommeTPPersDip.getAttribute("numPersPT_M") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPT_M")).toString();
		numMassoPTOrarioOltre50 = sommeTPPersDip.getAttribute("numPersPTOltre50_M") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPTOltre50_M")).toString();
		numMassoRiprPTOrarioMeno50 = sommeTPPersDip.getAttribute("numRiprPTMeno50_M") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numRiprPTMeno50_M")).toString();
		if (numMassoRiprPTOrarioMeno50 != null) {
			double risRiproporz = new Double(numMassoRiprPTOrarioMeno50).doubleValue();
			int parteIntera = new BigDecimal(risRiproporz).intValue();
			if (risRiproporz - parteIntera > 0.5) {
				totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
			}
			else {
				totale = new BigDecimal(parteIntera);
			}
			numMassoRiprPTOrarioMeno50 = totale.toString();	
		}
		numTeleLavPT = sommeTPPersDip.getAttribute("numPersPT_T") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numPersPT_T")).toString();
		numTeleLavRiprPT = sommeTPPersDip.getAttribute("numRiprPT_T") == null ? "0" : ((BigDecimal)sommeTPPersDip.getAttribute("numRiprPT_T")).toString();
		if (numTeleLavRiprPT != null) {
			double risRiproporz = new Double(numTeleLavRiprPT).doubleValue();
			int parteIntera = new BigDecimal(risRiproporz).intValue();
			if (risRiproporz - parteIntera > 0.5) {
				totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
			}
			else {
				totale = new BigDecimal(parteIntera);
			}
			numTeleLavRiprPT = totale.toString();
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
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }	
	String fieldReadOnly = canModify ? "false" : "true";  				
	
	String fieldPTReadOnly = checkPartTime ? "true" : fieldReadOnly;
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	

	function controllaCampi() {		
		
		if (document.Frm1.tipoPartTime[0].checked == true) {
			<%
			if (checkPartTime) {
			%>
				return confirm("Attenzione: verranno cancellati \r\ntutti i dati dei part-time lavoratori!");
			<%
			}
			%>
		}
		
		return true;      
	}
	
	function getPulsante() {
		document.Frm1.calcolaPartTime.style.display = "";	
		document.Frm1.numPartTime.className = "inputView";
		document.Frm1.numPartTime.readOnly = true;
		if (document.Frm1.numPartTimeCalc.value == "") {
			<%
			if (checkPartTime) {
			%>
				document.Frm1.numPartTime.value = "<%=numPartTime%>";
			<%
			} else {
			%>
				document.Frm1.numPartTime.value = "0";
			<%
			} 
			%>
		} else {
			document.Frm1.numPartTime.value = document.Frm1.numPartTimeCalc.value;
		}
	}
	
	function nascondiPulsante() {
		document.Frm1.calcolaPartTime.style.display ="none";
		document.Frm1.numPartTime.className = "inputEdit";	
		document.Frm1.numPartTime.readOnly = false;
		if (document.Frm1.numPartTimeCalc.value == "") {
			document.Frm1.numPartTime.value = "<%=numPartTime%>";
		}
	}	

	function nascondiPulsanteIntermittenti() {
		document.Frm1.calcolaIntermittenti.style.display ="none";
		document.Frm1.numIntermittenti.className = "inputEdit";	
		document.Frm1.numIntermittenti.readOnly = false;
		if (document.Frm1.numIntermittentiCalc.value == "") {
			document.Frm1.numIntermittenti.value = "<%=numIntermittenti%>";
		}
	}

	function getPulsanteIntermittenti() {
		document.Frm1.calcolaIntermittenti.style.display = "";	
		document.Frm1.numIntermittenti.className = "inputView";
		document.Frm1.numIntermittenti.readOnly = true;
		if (document.Frm1.numIntermittentiCalc.value == "") {
			<%
			if (checkIntermittenti) {
			%>
				document.Frm1.numIntermittenti.value = "<%=numIntermittenti%>";
			<%
			} else {
			%>
				document.Frm1.numIntermittenti.value = "0";
			<%
			} 
			%>
		} else {
			document.Frm1.numIntermittenti.value = document.Frm1.numIntermittentiCalc.value;
		}
	}
	
	
	function calcola() {
	
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=CMProspPartTimePage";		
	    url += "&MODULE=CMProspPartTimeListModule";
	    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	    url += "&MESSAGE_ESCLUSIONE=<%=message%>";
	    url += "&MODE=LISTA";  
	    
	    window.open(url, "", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');	
	}

	function calcolaValIntermittenti() {
		
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=CMProspIntermittentiPage";		
	    url += "&MODULE=CMProspIntermittentiListModule";
	    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	    url += "&MESSAGE_ESCLUSIONE=<%=message%>";
	    url += "&MODE=LISTA";  
	    
	    window.open(url, "", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');	
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
		<af:showMessages prefix="CMProspPersDipSaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="<%=_page%>"/>
<input type="hidden" name="MODULE" value="CMProspPersDipSaveModule"/>
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
   			Disabili in forza L. 68/99    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone Tempo Pieno</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numdisabili" size="6" value="<%=numdisabili%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone Tempo Pieno - Disabili in forza" />
	    </td>	
	    <td class="etichetta2">N° tot. persone tempo parziale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numDisPT" size="6" value="<%=numDisPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° persone con orario >50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numDisPTOrarioOltre50" size="6" value="<%=numDisPTOrarioOltre50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('D')" />
		</td>    
	</tr> 	
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° riproporz. con orario <=50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numDisRiprPTOrarioMeno50" size="6" value="<%=numDisRiprPTOrarioMeno50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Centralinisti telefonici non vedenti    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone Tempo Pieno</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numCentNonVedentiForza" size="6" value="<%=numCentNonVedentiForza%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone Tempo Pieno - Centralinisti telefonici non vedenti" />
	    </td>	
	    <td class="etichetta2">N° tot. persone tempo parziale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numCentPT" size="6" value="<%=numCentPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° persone con orario >50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numCentPTOrarioOltre50" size="6" value="<%=numCentPTOrarioOltre50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('C')" />
		</td>    
	</tr> 	
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° riproporz. con orario <=50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numCentRiprPTOrarioMeno50" size="6" value="<%=numCentRiprPTOrarioMeno50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Terapisti della riabilitazione e massofisioterapisti non vedenti (L. 29/94)    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone Tempo Pieno</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numMassofisioterapistiForza" size="6" value="<%=numMassofisioterapistiForza%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone Tempo Pieno - Terapisti della riabilitazione e massofisioterapisti non vedenti" />
	    </td>	
	    <td class="etichetta2">N° tot. persone tempo parziale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numMassoPT" size="6" value="<%=numMassoPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° persone con orario >50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numMassoPTOrarioOltre50" size="6" value="<%=numMassoPTOrarioOltre50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('M')" />
		</td>    
	</tr> 	
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° riproporz. con orario <=50%</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numMassoRiprPTOrarioMeno50" size="6" value="<%=numMassoRiprPTOrarioMeno50%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Telelavoro (art. 23, D.Lgs. 80/2015)    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° persone per intero orario contrattuale</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numTeleLavFT" size="6" value="<%=numTeleLavFT%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° persone per intero orario contrattuale - Telelavoro" />
	    </td>	
	    <td class="etichetta2">N° tot. persone per quota parte orario contr.</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numTeleLavPT" size="6" value="<%=numTeleLavPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="3">&nbsp;</td>
		<td class="campo2">
			<input align="center" type="button" class="pulsante" id="calcolaTempiParziali" name=calcolaTempiParziali" 
				value="Calcola Tempi Parziali" onclick="calcolaValTempiParziali('T')" />
		</td>
	</tr>   
	<tr>
		<td colspan="2">&nbsp;</td>	
	    <td class="etichetta2">N° telelavoro riproporz.</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="float" name="numTeleLavRiprPT" size="6" value="<%=numTeleLavRiprPT%>" maxlength="6" readonly="true" />
	    </td>	
	    <td colspan="4">&nbsp;</td>	    
	</tr>	
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Altre categorie protette    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">N° in forza</td>	
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numprotette" size="6" value="<%=numprotette%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="N° in forza - Altre categorie protette" />
	    </td>	
	    <td colspan="6">&nbsp;</td>
	</tr>   
	<tr><td colspan="8">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="8">
   			<div class="sezione2"> 
   			Altri Part-Time e Intermittenti    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">Part-Time</td>
		<td class="campo2" colspan="2">
			<af:textBox classNameBase="input" type="integer" name="numPartTime" size="6" value="<%=numPartTime%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldPTReadOnly%>" title="Part-Time"/>
	        <input type="hidden" name="numPartTimeCalc" value="" onKeyUp="fieldChanged();" />
	        &nbsp;&nbsp;&nbsp;
	        Immissione:
	   		&nbsp;&nbsp;	   		
	   		<input type="radio" name="tipoPartTime" value="diretta" onclick="nascondiPulsante()" onKeyUp="fieldChanged();" <%if (!checkPartTime) {%>CHECKED<%}%> <%if (!canModify) {%>disabled="disabled"<%}%> /> diretta&nbsp;&nbsp;&nbsp;
       		<input type="radio" name="tipoPartTime" value="calcolata" onclick="getPulsante()" onKeyUp="fieldChanged();" <%if (checkPartTime) {%>CHECKED<%}%> <%if (!canModify) {%>disabled="disabled"<%}%> />calcolata                      	    	
	    </td> 	   
		<td colspan="5">
			<input style="display:<%if (!checkPartTime) {%>none<%}%>" align="center" type="button" class="pulsante" id="calcolaPartTime" name=calcolaPartTime" value="Calcola Part-Time" onclick="calcola()" />
		</td>
	</tr>
	<tr>
		<td class="etichetta2">Intermittenti</td>
		<td class="campo2" colspan="2">
			<af:textBox classNameBase="input" type="integer" name="numIntermittenti" size="6" value="<%=numIntermittenti %>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldPTReadOnly%>" title="Intermittenti"/>
	        <input type="hidden" name="numIntermittentiCalc" value="" onKeyUp="fieldChanged();" />
	        &nbsp;&nbsp;&nbsp;
	        Immissione:
	   		&nbsp;&nbsp;	   		
	   		<input type="radio" name="tipoIntermittenti" value="diretta" onclick="nascondiPulsanteIntermittenti()" onKeyUp="fieldChanged();" <%if (!checkIntermittenti) {%>CHECKED<%}%> <%if (!canModify) {%>disabled="disabled"<%}%> /> diretta&nbsp;&nbsp;&nbsp;
       		<input type="radio" name="tipoIntermittenti" value="calcolata" onclick="getPulsanteIntermittenti()" onKeyUp="fieldChanged();" <%if (checkIntermittenti) {%>CHECKED<%}%> <%if (!canModify) {%>disabled="disabled"<%}%> />calcolata                      	    	
	    </td> 	   
		<td colspan="5">
			<input style="display:<%if (!checkIntermittenti) {%>none<%}%>" align="center" type="button" class="pulsante" id="calcolaIntermittenti" name=calcolaIntermittenti" 
				value="Calcola Intermittenti" onclick="calcolaValIntermittenti()" />
		</td>
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