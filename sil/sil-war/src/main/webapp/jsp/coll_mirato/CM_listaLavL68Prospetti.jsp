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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspLavoratoriPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String numAnnoRifProspetto = "";
	String codProvincia = "";
	String codRegioneProspetto = "";
	String codMonoStatoProspetto = "";
	String flgCompetenza = "S";
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;  
	Linguette l = null;
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		codProvincia = (prospetto.getAttribute("codprovincia")).toString();
		numAnnoRifProspetto = prospetto.getAttribute("numannorifprospetto") == null? "" : ((BigDecimal)prospetto.getAttribute("numannorifprospetto")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgCompetenza = StringUtils.getAttributeStrNotNull(prospetto,"flgCompetenza");
		codRegioneProspetto = StringUtils.getAttributeStrNotNull(prospetto,"codregione");
	}		
		
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspLavoratoriPage");	
	boolean canModify =	false;
	
	canModify  = attributi.containsButton("AGGIORNA");    
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }
	
	//nel caso di provincia esterna si disabilita tutto
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	String codiceProv = provincia.getCodice();
	String flgPoloReg = provincia.getFlgPoloReg();
	String codiceReg = provincia.getCodiceRegione();
	if (flgPoloReg == null || flgPoloReg.equals("") || flgPoloReg.equalsIgnoreCase("N")) {
		if (!(codiceProv).equalsIgnoreCase(codProvincia)) {
			canModify = false;
		}	
	}
	else {
		if (!(codiceReg).equalsIgnoreCase(codRegioneProspetto)) {
			canModify = false;
		}	
	}
	
	String fieldReadOnly = canModify ? "false" : "true";
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<script>
	   
function nuovo() {
	
	var url = "AdapterHTTP?PAGE=CMProspLavoratoriPage";		
	url += "&MODULE=CMProspLavL68DettModule";
	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	url += "&MESSAGE=INSERT";
		 
	setWindowLocation(url);    
}	
 
function movimentiDisponibili() {
	
	var url = "AdapterHTTP?PAGE=CMProspMovimentiDispPage";		
	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	url += "&PRGAZIENDA=<%=prgAzienda%>";
	url += "&NUMANNORIFPROSPETTO=<%=numAnnoRifProspetto%>";
	url += "&CODPROVINCIA=<%=codProvincia%>";
	url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	
	window.open(url, "", 'toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');	
	
}

function Select(movPage, movModule, cdnLavoratore, prgAzienda, prgUnita, dtIniRapp, prgLav, prgPro) {
  	
      var s= "AdapterHTTP?PAGE=" +movPage+
	     	    "&MODULE="+movModule+
	     	    "&CDNLAVORATORE="+cdnLavoratore+
	    		"&PRGAZIENDA=" + prgAzienda +
	    		"&PRGUNITA=" + prgUnita +
	    		"&DATINIZIORAPP=" +dtIniRapp+    		
	    		"&PRGLAVRISERVA="+prgLav+
	    		"&PRGPROSPETTOINF="+prgPro;

		window.open(s, "", 'toolbar=NO,statusbar=YES,width=700,height=400,top=50,left=100,scrollbars=YES,resizable=YES');	
}

function refresh() {
	rinfresca();
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
		<af:showMessages prefix="CMProspLavL68SaveModule"/>
		<af:showMessages prefix="CMProspLavL68DeleteModule"/>
		<af:showMessages prefix="CMProspMovimentiDispSaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
   
<af:list moduleName="CMProspLavL68ListModule" configProviderClass="it.eng.sil.module.collocamentoMirato.CMProspLavL68ListConfig" jsSelect="Select" canDelete="<%= canModify ? \"1\" : \"0\" %>" />

<% 
if (canModify) {
%>	
	<table class="main">  
		<tr>
			<td>
				<input type="button" class="pulsante" name="nuovo" value="Nuovo" onclick="nuovo()"/>
			    &nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" class="pulsante" name="movimenti" value="Movimenti disponibili" onclick="movimentiDisponibili()" />
			</td>
		</tr>
	</table>
<%
}
%>

<br/>

</body>
</html>