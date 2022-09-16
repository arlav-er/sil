<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.gov.lavoro.servizi.RicercaCO._2_0.*,
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

PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

String titlePagina = "Storico Comunicazioni Obbligatorie";
String dataInizioStorico= null;
String dataFineStorico = null;
COLavoratore_Type coResult = null;
boolean isUnilav = false;
boolean isUniSomm = false;
boolean isVardatori = false;

if(serviceResponse.containsAttribute("M_CONSULTACO.WS_OUTPUT_STORICO_CO") && serviceResponse.getAttribute("M_CONSULTACO.WS_OUTPUT_STORICO_CO")!=null ){ 
	dataInizioStorico =(String) serviceResponse.getAttribute("M_CONSULTACO.DATA_INIZIO");
	dataFineStorico =(String)  serviceResponse.getAttribute("M_CONSULTACO.DATA_FINE");	
	
	coResult = (COLavoratore_Type) serviceResponse.getAttribute("M_CONSULTACO.WS_OUTPUT_STORICO_CO");
	
	if(serviceResponse.containsAttribute("M_CONSULTACO.UNILAV") && serviceResponse.getAttribute("M_CONSULTACO.UNILAV")!=null ){
		isUnilav = true;
	}
	if(serviceResponse.containsAttribute("M_CONSULTACO.UNISOMM") && serviceResponse.getAttribute("M_CONSULTACO.UNISOMM")!=null ){
		isUniSomm = true;
	}
	if(serviceResponse.containsAttribute("M_CONSULTACO.VARDATORI") && serviceResponse.getAttribute("M_CONSULTACO.VARDATORI")!=null ){
		isVardatori= true;
	}
} 


String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
boolean readOnlyStr = true; 
%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
	
	<af:linkScript path="../../js/"/>
	<script language="JavaScript">

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src=" ../../img/aperto.gif";
hideButtonImg.src=" ../../img/chiuso.gif"

function onOff(index)
{	var div1 = document.getElementById("dett"+index);
	var idImm = document.getElementById("imm1"+index);
	if (div1.style.display=="")
  {	nascondi("dett"+index);
		idImm.src = hideButtonImg.src
	} 
	else
  {	mostra  ("dett"+index);
		idImm.src = showButtonImg.src;
	}
}//onOff()

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}


</script>

<style type="text/css">
td.etichetta{
vertical-align: top;
}
input.inputView{
 vertical-align: top;
  width: 49%;
}
td.campo {
 width: 20%;
 vertical-align: top;
}
textarea {

    border: none;
    background: transparent;
    font-weight: bold;
 }
</style>
	
	</head>
	<body class="gestione" >

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_ConsultaCO"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
 	<br/><br/>
 	<%if(isUnilav || isUniSomm || isVardatori){ %>
 	<div align="center">
 	<table class="main">
			<tr>
				<td colspan="4"><p class="titolo"><%= titlePagina %></p></td>
			</tr>
			<tr>
				<td><br /></td>
			</tr>
		</table>
	</div>	
	
	<table margin="0" noshade="" width="94%" cellspacing="0" cellpadding="0" border="0px" align="center">
		<tr>
				<td colspan="4">
	       			Periodo di riferimento: <%= dataInizioStorico %> - <%= dataFineStorico %>
	        	</td>
				 
			</tr>
	</table>
		<% 
	
		if(isUnilav){
			
	%>	
	<div align="center">
 	<table class="main">
			<tr>
				<td colspan="4"><p class="titolo">UNILAV: <%=coResult.getUNILAV().length %></td>
			</tr>
		</table>
	</div>	
 	<%= htmlStreamTopCoop%>
	 <af:form name="Frm1_1" action="AdapterHTTP" method="POST" >
		<div align="center">
			
	<table class="main">
	
	<%-- ********************* UNILAV ******************************* --%>
	<%@ include file="storicoCoUnilav.inc" %>
	<%-- ***************************************************************************** --%>

	</table>

<br/>

</div>

</af:form>
<%= htmlStreamBottomCoop%>
<%}	%>
		<% 
	
		if(isUniSomm){
			
	%>
	<div align="center">
 	<table class="main">
			<tr>
				<td colspan="4"><p class="titolo">UNISOMM: <%=coResult.getUNISOMM().length %></td>
			</tr>
		</table>
	</div>	
 <%= htmlStreamTopCoop%>
	 <af:form name="Frm1_2" action="AdapterHTTP" method="POST" >
		<div align="center">



			
	<table class="main">
	
	<%-- ********************* UNISOMM ******************************* --%>
	<%@ include file="storicoCoUnisomm.inc" %>
	<%-- ***************************************************************************** --%>

	</table>
	
<br/>

</div>

</af:form>
<%= htmlStreamBottomCoop%>
<%}	%>
		<% 
	
		if(isVardatori){
			
	%>
		<div align="center">
 	<table class="main">
			<tr>
				<td colspan="4"><p class="titolo">VARDATORI: <%=coResult.getVARDATORI().length %></td>
			</tr>
		</table>
	</div>
 	<%= htmlStreamTopCoop%>
	 <af:form name="Frm1_3" action="AdapterHTTP" method="POST" >
		<div align="center">



			
	<table class="main">
	
	<%-- ********************* VARDATORI ******************************* --%>
	<%@ include file="storicoCoVardatori.inc" %>
	<%-- ***************************************************************************** --%>

	</table>
	
<br/>

</div>

</af:form>
<%= htmlStreamBottomCoop%>
<%}	%>
<%} %>
<center>
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
	<br>&nbsp;
</center>

</body>
</html>