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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMPubblicaSelezionePage");
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
	String codRegione = "";
	String numSaldoDisabili = "";
	String numSaldoExArt18 = "";
	String strNote = "";
	String numKlo = "";
	String prgPbSelezione = "";
	String operazione = "";
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
	}		
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspPBSelezioneDettModule.ROWS.ROW");				
	if (dett != null) {	
		message = "UPDATE";
		operazione = "AGGIORNA";
		cdnUtIns = dett.getAttribute("cdnUtIns");
		dtmIns = dett.getAttribute("dtmIns");
		cdnUtMod = dett.getAttribute("cdnUtMod");
		dtmMod = dett.getAttribute("dtmMod");
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		prgPbSelezione = dett.getAttribute("PRGASSPUBSELEZIONE") == null? "" : ((BigDecimal)dett.getAttribute("PRGASSPUBSELEZIONE")).toString();
		numSaldoExArt18 = dett.getAttribute("numSaldoExArt18") == null? "" : ((BigDecimal)dett.getAttribute("numSaldoExArt18")).toString();		
		numSaldoDisabili = dett.getAttribute("numSaldoDisabili") == null? "" : ((BigDecimal)dett.getAttribute("numSaldoDisabili")).toString();
		strNote = dett.getAttribute("strNote") == null? "" : (String)dett.getAttribute("strNote");
		codRegione = dett.getAttribute("codRegione") == null? "" : (String)dett.getAttribute("codRegione");
		numKlo = dett.getAttribute("NUMKLOASSPBSELEZIONE") == null? "" : ((BigDecimal)dett.getAttribute("NUMKLOASSPBSELEZIONE")).toString();
	}
	else {
		message = "INSERT";
		operazione = "INSERISCI";
	}
	
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMPubblicaSelezionePage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)) {
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
<SCRIPT language="JavaScript" src="../../js/CommonXMLHTTPRequest.js"></SCRIPT>

<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	

	function tornaLista() {
		var s= "AdapterHTTP?PAGE=CMPubblicaSelezionePage";
	    s += "&CDNFUNZIONE=<%= cdnfunzione%>";
	    s += "&PRGPROSPETTOINF=<%= prgProspettoInf%>";
	    s += "&codStatoAtto=<%= codStatoAtto%>";
	    setWindowLocation(s);
	}

	function controllaCampi() {				
		return true;      
	}
			
</script>

<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 
</head>

<body class="gestione" onload="rinfresca()">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<p class="titolo">Dettaglio Pubblica Selezione</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMPubblicaSelezionePage"/>
<input type="hidden" name="OPERAZIONE" value="<%=operazione%>"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>  
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>
<%if (operazione.equals("AGGIORNA")) {%>
	<input type="hidden" name="NUMKLOASSPBSELEZIONE" value="<%=numKlo%>"/>
	<input type="hidden" name="PRGASSPBSELEZIONE" value="<%=prgPbSelezione%>"/>
<%}%>
<%out.print(htmlStreamTop);%>

<table class="main" border="0">				
<tr>
	<td colspan="2"/>&nbsp;</td>   
</tr>
<tr>
    <td class="etichetta2">Regione</td>
 	<td class="campo2">
		<af:comboBox name="CODREGIONE" moduleName="M_GETREGIONIMIG" title="Regione" required="true" selectedValue="<%=codRegione%>"
     				classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>	  
</tr>	
<tr>
	<td class="etichetta2">Numero saldo disabili</td>
	<td class="campo2">
	<af:textBox classNameBase="input" name="NUMSALDODISABILI" value="<%=numSaldoDisabili%>" title="Numero saldo disabili" 
		size="7" required="true" readonly="<%=fieldReadOnly%>" type="integer" validateOnPost="true"/>
    </td>   
</tr>
<tr>
	<td class="etichetta2">Numero saldo art. 18</td>
	<td class="campo2">
	<af:textBox classNameBase="input" name="NUMSALDOEXART18" value="<%=numSaldoExArt18%>" title="Numero saldo art. 18" 
		size="7" required="true" readonly="<%=fieldReadOnly%>" type="integer" validateOnPost="true"/>
    </td>   
</tr>


<tr>
	<td class="etichetta2">Note</td>
   	<td class="campo2">
         	<af:textArea cols="70" rows="4" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
             	name="STRNOTE" value="<%=strNote%>" title="Note"/>    	
   	</td>   
</tr>


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
		<input type="button" class="pulsante" name="torna" value="Torna alla lista" onclick="tornaLista()" />
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