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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspEsclusioniPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String codMonoStatoProspetto = "";
	String message = "";
	String numDirigenti = "";
	String numApprendisti = "";
	String numCFL = "";
	String numcigs = "";   
	String numtdmeno9mesi = ""; 
	String numtemporaneo = ""; 
	String numreinserimento = "";
	String numdomicilio = "";
	String numsoci = ""; 
	String numaltri	= "";
	String numLavAcquisiti = "";
	String numPersonaleNonAmm_C3 = "";
	String numPersonaleNonAmm_C4 = "";
	String numPersonaleViaggiante = "";
	String numLSUStabili = "";
	String numLavoratoriOpEstero = "";
	String numLavoratoriEdilizia = "";
	String numLavEmersiNero = "";
	String flgCompetenza = "";
	String numtdmeno6mesi = "";
	String numSomministr = "";
	String numSottoSuolo = "";
	// decreto 2014
	String numImpiantiFune = "";
	String numSettoreAutotrasp = "";
	String numMontaggi = "";
	String numContrInserimento = "";
	String numInail60x1000 = "";
	
	String codStatoAtto = "";
	
	codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;	
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	boolean checkProspetto2011 = serviceResponse.containsAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011") && serviceResponse.getAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011").toString().equalsIgnoreCase("TRUE")?true:false;
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		
		cdnUtIns = prospetto.getAttribute("cdnUtIns");
		dtmIns = prospetto.getAttribute("dtmIns");
		cdnUtMod = prospetto.getAttribute("cdnUtMod");
		dtmMod = prospetto.getAttribute("dtmMod");	
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}		
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspEsclusioniDettModule.ROWS.ROW");				
	if (dett != null) {	
		message = "UPDATE";			
		numDirigenti = dett.getAttribute("numDirigenti") == null ? "" : ((BigDecimal)dett.getAttribute("numDirigenti")).toString();
		numApprendisti = dett.getAttribute("numApprendisti") == null ? "" : ((BigDecimal)dett.getAttribute("numApprendisti")).toString();
		numCFL = dett.getAttribute("numCFL") == null ? "" : ((BigDecimal)dett.getAttribute("numCFL")).toString();
		numcigs = dett.getAttribute("numcigs") == null ? "" : ((BigDecimal)dett.getAttribute("numcigs")).toString();   
		numtdmeno9mesi = dett.getAttribute("numtdmeno9mesi") == null ? "" : ((BigDecimal)dett.getAttribute("numtdmeno9mesi")).toString();
		numtemporaneo = dett.getAttribute("numtemporaneo") == null ? "" : ((BigDecimal)dett.getAttribute("numtemporaneo")).toString();
		numreinserimento = dett.getAttribute("numreinserimento") == null ? "" : ((BigDecimal)dett.getAttribute("numreinserimento")).toString();
		numdomicilio = dett.getAttribute("numdomicilio") == null ? "" : ((BigDecimal)dett.getAttribute("numdomicilio")).toString();
		numsoci = dett.getAttribute("numsoci") == null ? "" : ((BigDecimal)dett.getAttribute("numsoci")).toString();
		numLavAcquisiti = dett.getAttribute("numLavAcquisiti") == null ? "" : ((BigDecimal)dett.getAttribute("numLavAcquisiti")).toString();
		numPersonaleNonAmm_C3 = dett.getAttribute("numPersonaleNonAmm_C3") == null ? "" : ((BigDecimal)dett.getAttribute("numPersonaleNonAmm_C3")).toString();
		numPersonaleNonAmm_C4 = dett.getAttribute("numPersonaleNonAmm_C4") == null ? "" : ((BigDecimal)dett.getAttribute("numPersonaleNonAmm_C4")).toString();
		numPersonaleViaggiante = dett.getAttribute("numPersonaleViaggiante") == null ? "" : ((BigDecimal)dett.getAttribute("numPersonaleViaggiante")).toString();
		numLSUStabili = dett.getAttribute("numLSUStabili") == null ? "" : ((BigDecimal)dett.getAttribute("numLSUStabili")).toString();
		numLavoratoriOpEstero = dett.getAttribute("numLavoratoriOpEstero") == null ? "" : ((BigDecimal)dett.getAttribute("numLavoratoriOpEstero")).toString();
		numLavoratoriEdilizia = dett.getAttribute("numLavoratoriEdilizia") == null ? "" : ((BigDecimal)dett.getAttribute("numLavoratoriEdilizia")).toString();
		numLavEmersiNero = dett.getAttribute("numLavEmersiNero") == null ? "" : ((BigDecimal)dett.getAttribute("numLavEmersiNero")).toString();
		numaltri	= dett.getAttribute("numaltri") == null ? "" : ((BigDecimal)dett.getAttribute("numaltri")).toString();
		flgCompetenza = StringUtils.getAttributeStrNotNull(dett,"flgCompetenza");
		numtdmeno6mesi = dett.getAttribute("numtdmeno6mesi") == null ? "" : ((BigDecimal)dett.getAttribute("numtdmeno6mesi")).toString();
		numSomministr = dett.getAttribute("numSomministr") == null ? "" : ((BigDecimal)dett.getAttribute("numSomministr")).toString();
		numSottoSuolo = dett.getAttribute("numSottoSuolo") == null ? "" : ((BigDecimal)dett.getAttribute("numSottoSuolo")).toString();
		// decreto 2014
		numImpiantiFune = dett.getAttribute("numImpiantiFune") == null ? "" : ((BigDecimal)dett.getAttribute("numImpiantiFune")).toString();
		numSettoreAutotrasp = dett.getAttribute("numSettoreAutotrasp") == null ? "" : ((BigDecimal)dett.getAttribute("numSettoreAutotrasp")).toString();
		numMontaggi = dett.getAttribute("numMontaggi") == null ? "" : ((BigDecimal)dett.getAttribute("numMontaggi")).toString();
		numContrInserimento = dett.getAttribute("numContrInserimento") == null ? "" : ((BigDecimal)dett.getAttribute("numContrInserimento")).toString();
		numInail60x1000 = dett.getAttribute("numInail60x1000") == null ? "" : ((BigDecimal)dett.getAttribute("numInail60x1000")).toString();
	}
	else {
		message = "INSERT";
		flgCompetenza = "S";
		checkProspetto2011 = false;
	}
	
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspEsclusioniPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify     			=	attributi.containsButton("AGGIORNA");    	
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
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
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

<p class="titolo">Dettaglio Esclusioni</p>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspEsclusioniSaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="CMProspEsclusioniPage"/>
<input type="hidden" name="MODULE" value="CMProspEsclusioniSaveModule"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>
 
<%out.print(htmlStreamTop);%>

<table class="main" border="0">				
	<tr>
		<td class="etichetta2">Dirigenti</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numDirigenti" size="6" maxlength="6" title="Dirigenti"
						value="<%=numDirigenti%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
		<td class="etichetta2">Apprendisti</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numApprendisti" size="6" maxlength="6" title="Apprendisti"  
						value="<%=numApprendisti%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">Lavoratori CFL</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numCFL" size="6" value="<%=numCFL%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori CFL"/>
	    </td> 
		<td class="etichetta2">CIGS</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numcigs" size="6" value="<%=numcigs%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="CIGS"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>	
	<tr>
		<td class="etichetta2">Lavoratori con contratto a termine &lt; 6 mesi</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numtdmeno6mesi" size="6" value="<%=numtdmeno6mesi%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con contratto a termine &lt; 6 mesi"/>
	    </td>
	    <%if (checkProspetto2011) {%> 
			<td class="etichetta2">Lavoratori con contratto fornitura lavoro temporaneo</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numtemporaneo" size="6" value="<%=numtemporaneo%>" onKeyUp="fieldChanged();" 
							maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con contratto fornitura lavoro temporaneo"/>
		    </td>
	    <%} else {%>
	    	<td>&nbsp;</td>
		 	<td><input type="hidden" name="numtemporaneo" value="<%=numtemporaneo%>"/></td>
		<%}%>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>	
	<tr>
		<td class="etichetta2">Lavoratori con contratto di reinserimento</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numreinserimento" size="6" value="<%=numreinserimento%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con contratto di reinserimento"/>
	    </td>
	    <%if (checkProspetto2011) {%>
			<td class="etichetta2">Lavoratori a domicilio/telelavoro</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numdomicilio" size="6" value="<%=numdomicilio%>" onKeyUp="fieldChanged();" 
							maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori a domicilio/telelavoro"/>
		    </td>
		<%} else {%>
		 	<td class="etichetta2">Lavoratori a domicilio</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numdomicilio" size="6" value="<%=numdomicilio%>" onKeyUp="fieldChanged();" 
							maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori a domicilio"/>
		    </td>
		<%}%>    
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">Soci lavoratori di cooperative produzione lavoro</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numsoci" size="6" value="<%=numsoci%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Numero Soci lavoratori di cooperative produzione lavoro"/>
	    </td> 
		<td class="etichetta2">Lavoratori emersi dal lavoro nero</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavEmersiNero" size="6" value="<%=numLavEmersiNero%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori emersi dal lavoro nero"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">Edilizia: lavoratori occupati in cantiere e addetti al trasporto (L.68/99, art. 5 c. 2)*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavoratoriEdilizia" size="6" value="<%=numLavoratoriEdilizia%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Edilizia: lavoratori occupati in cantiere e addetti al trasporto (L.68/99, art. 5 c. 2)*"/>
	    </td> 
		<td class="etichetta2">Lavoratori acquisiti per passaggio di appalto</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavAcquisiti" size="6" value="<%=numLavAcquisiti%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori acquisiti per passaggio di appalto"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">Personale non amministrativo non tecnico esecutivo (L.68/99 art.3 c. 3)</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numPersonaleNonAmm_C3" size="6" value="<%=numPersonaleNonAmm_C3%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Personale non amministrativo non tecnico esecutivo (L.68/99 art.3 c. 3)"/>
	    </td> 
		<td class="etichetta2">Personale non amministrativo (L.68/99 art.3 c. 4)</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numPersonaleNonAmm_C4" size="6" value="<%=numPersonaleNonAmm_C4%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Personale non amministrativo (L.68/99 art.3 c. 4)"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">Personale viaggiante/navigante (L.68/99, art. 5, c. 2)*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numPersonaleViaggiante" size="6" value="<%=numPersonaleViaggiante%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Personale viaggiante/navigante (L.68/99, art. 5, c. 2)"/>
	    </td> 
		<td class="etichetta2">Lavoratori assunti per attività all'estero</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavoratoriOpEstero" size="6" value="<%=numLavoratoriOpEstero%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori assunti per attività all'estero"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta2">LSU stabilizzati</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLSUStabili" size="6" value="<%=numLSUStabili%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="LSU stabilizzati"/>
	    </td>
		<td class="etichetta2">Altre esclusioni art.3 *</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numaltri" size="6" value="<%=numaltri%>" onKeyUp="fieldChanged();" 
						maxlength="6" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Altre esclusioni"/>
	    </td> 
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Lavoratori in somministrazione presso l'utilizzatore</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSomministr" size="6" value="<%=numSomministr%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori in somministrazione presso l'utilizzatore"/>
	    </td>
	    <td class="etichetta2">Lavoratori del sottosuolo e adibiti a trasporto del minerale (L. 10/11)*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSottoSuolo" size="6" value="<%=numSottoSuolo%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori del sottosuolo e adibiti a trasporto del minerale"/>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Lavoratori degli impianti a fune (L68/99 art. 5, c.2)*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numImpiantiFune" size="6" value="<%=numImpiantiFune%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori degli impianti a fune (L68/99 art. 5, c.2)*"/>
	    </td>
	    <td class="etichetta2">Lavoratori settore autotrasporto iscritti all'albo*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numSettoreAutotrasp" size="6" value="<%=numSettoreAutotrasp%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori settore autotrasporto iscritti all'albo*"/>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Personale operante nei montaggi industriali o impiantistici*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numMontaggi" size="6" value="<%=numMontaggi%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Personale operante nei montaggi industriali o impiantistici*"/>
	    </td>
	    <td class="etichetta2">Lavoratori con contratto di Inserimento</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numContrInserimento" size="6" value="<%=numContrInserimento%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con contratto di Inserimento"/>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr class="note">
   		<td colspan="5">
   			<div class="sezione2"> 
   			Esclusioni scadute    		
   			</div>
     	</td>
	</tr>
	<tr>
		<td class="etichetta2">Lavoratori con contratto a termine &lt; 9 mesi</td>
		<td class="campo2" nowrap>
			<af:textBox classNameBase="input" type="integer" name="numtdmeno9mesi" size="6" value="<%=numtdmeno9mesi%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con contratto a termine &lt; 9 mesi"/>
			&nbsp;scaduta al 31/12/2012
	    </td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Lavoratori con premio Inail pari o sup. al 60xmille*</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numInail60x1000" size="6" value="<%=numInail60x1000%>" maxlength="6" 
						onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>" title="Lavoratori con premio Inail pari o sup. al 60xmille*"/>
			&nbsp;scaduta al 15/04/2016
	    </td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr>
		<td colspan="5" >		   		
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

<table>
<tr>
	<td class="etichetta2">(*) Non è possibile escluderlo dalla base di computo dell'Art. 18</td>
</tr>
</table>

<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>

</body>
</html>