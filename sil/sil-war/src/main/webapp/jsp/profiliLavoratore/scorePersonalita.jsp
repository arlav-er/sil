<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.module.anag.profiloLavoratore.*,
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
String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, "ProfiloLavPage");

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

String titlePagina = "Dettaglio Score Personalità";
String amicalita ="";
String cosc = "";
String stabEmo = "";
String extraVer = "";
String apertura = "";
boolean consultaScoreProfilo = false;
 if (serviceResponse.containsAttribute("M_CalcolaProfilo.OK_SCORE_PERS")) {
 	    
	  SourceBean beanScore = null;  
	  if(serviceResponse.containsAttribute("M_CalcolaProfilo.OK_SCORE_PERS")){
	  		beanScore  = (SourceBean) serviceResponse.getAttribute("M_CalcolaProfilo");
	  	}
	  if(beanScore!=null){
		 amicalita =  (String) beanScore.getAttribute(Decodifica.ScorePersonalita.AMICALITA);
		 cosc =  (String) beanScore.getAttribute(Decodifica.ScorePersonalita.COSCIENZOSITA);
		 stabEmo =  (String) beanScore.getAttribute(Decodifica.ScorePersonalita.STAB_EMOTIVA);
		 extraVer =  (String) beanScore.getAttribute(Decodifica.ScorePersonalita.EXTRAVERSIONE);
		 apertura =  (String) beanScore.getAttribute(Decodifica.ScorePersonalita.APERTURA);
		 consultaScoreProfilo = true;
	  }

	}
	
		
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<link rel="stylesheet" type="text/css" href="../../css/progressBar.css" />
	
	<af:linkScript path="../../js/" />
	<script language="Javascript" src="../../js/progressBar.js"></script> 
	<script type="text/Javascript">
	 
		function rinfrescaScoreProfili(){
			rinfresca();
			<% if(consultaScoreProfilo){ %>
				resizeProgressBar('progressDim8', '<%=amicalita%>');
				resizeProgressBar('progressDim9', '<%=cosc%>');
				resizeProgressBar('progressDim10', '<%=stabEmo%>');
				resizeProgressBar('progressDim11', '<%=extraVer%>');
				resizeProgressBar('progressDim12', '<%=apertura%>');
			<% }%>
		} 

	</script>
	
	</head>
	<body class="gestione" onload="rinfrescaScoreProfili();">

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_CalcolaProfilo"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
 
		<% out.print(htmlStreamTop); %>
		<br>
	 	<table>
	 
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Amicalità:&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim8" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="amicalita" type="date" title="Amicalità" readonly="true"
						size="25"  classNameBase="input"	value="<%=amicalita%>" />
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Coscienziosità:&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim9" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="coscienziosita" type="date" title="Coscienziosità" readonly="true"
						size="25"  classNameBase="input"	value="<%=cosc%>" />
		            </td>
				</tr>
			
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Stabilità emotiva:&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim10" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="stabEmo" type="date" title="Stabilità emotiva" readonly="true"
						size="25"  classNameBase="input"	value="<%=stabEmo%>" />
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Extraversione:&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim11" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="extraversione" type="date" title="Extraversione" readonly="true"
						size="25"  classNameBase="input"	value="<%=extraVer%>" />
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Apertura emotiva:&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim12" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="apertura" type="date" title="Apertura emotiva" readonly="true"
						size="25"  classNameBase="input"	value="<%=apertura%>" />
		            </td>
				</tr>
 
		</table>
 
 
              <br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="button" onClick="window.close();" class="pulsanti" value="Chiudi" >
                	</td>
              		</tr>
              	</table>
		<%out.print(htmlStreamBottom); %>
	 
	</body>
	</html>