<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.*"%>
            
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
	String Coop= "Invio Cartaceo";
	String imgCoop= "../../img/text.gif";
	String codProvCpi = (String)serviceResponse.getAttribute("M_COMMONGETDESCRCPI.ROWS.ROW.CODPROVCPI");
	String codRegCpi = (String)serviceResponse.getAttribute("M_COMMONGETDESCRCPI.ROWS.ROW.CODREGCPI");
	String coopAbilitata = System.getProperty("cooperazione.enabled");
  	if (coopAbilitata != null && coopAbilitata.equals("true")) {
		if (((String)serviceResponse.getAttribute("M_COOP_CheckProvinciaAttiva.poloInCoop")).equals("true")) {
			Coop= "In cooperazione";
			imgCoop= "../../img/cooperazione.gif";
		}	
	}
%>

<html>
<head>
<title>Check CPI in Cooperazione</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<SCRIPT TYPE="text/javascript">

<!--
function AggiornaFormPadre(Coop, imgCoop, codProvCpi, codRegCpi) {
	  if (codProvCpi != window.opener.opener.document.Frm1.userProvCpi.value &&
	  		codRegCpi == window.opener.opener.document.Frm1.userRegCpi.value) {
	  	window.opener.opener.document.Frm1.Coop.value = Coop;
      	window.opener.opener.document.Frm1.imgCoop.src = imgCoop;
      	window.opener.opener.document.Frm1.isInterProvincia.value = "SI";
	  	window.opener.opener.document.Frm1.isInterRegione.value = "NO";
	  	window.opener.opener.document.Frm1.privacy.style.display = "";
	  	window.opener.opener.document.Frm1.docIdent.style.display = "";
	  	window.opener.opener.document.Frm1.CODMONOTIPOORIG.value = "R";
      } else {
      	if (codProvCpi == window.opener.opener.document.Frm1.userProvCpi.value) {
      		window.opener.opener.document.Frm1.CODCPIORIG.value = "";
	  		window.opener.opener.document.Frm1.DESCRCPIORIG.value = "";
	  		if (window.opener.opener.document.Frm1.msgIRLav.value != "" && window.opener.opener.document.Frm1.msgIRLav.value != " ") {
	  			window.opener.opener.document.Frm1.msgIRLav.value = "Il CPI di provenienza non può essere intraprovinciale";
	  			window.opener.opener.document.Frm1.msgXIR.value = "Procedere selezionando il CPI manualmente";
	  		} else {
	  			window.opener.opener.document.Frm1.msgIRLav.value = "Il CPI di provenienza non può essere intraprovinciale";
	  			window.opener.opener.document.Frm1.msgXIR.value = " ";
	  		}
	  		window.opener.opener.document.Frm1.isInterProvincia.value = "SI";
	  		window.opener.opener.document.Frm1.isInterRegione.value = "NO";
	  		window.opener.opener.document.Frm1.privacy.style.display = "none";
	  		window.opener.opener.document.Frm1.docIdent.style.display = "none";
	  		window.opener.opener.document.Frm1.CODMONOTIPOORIG.value = "I";
      	} else {
      		window.opener.opener.document.Frm1.isInterProvincia.value = "NO";
	  		window.opener.opener.document.Frm1.isInterRegione.value = "SI";
	  		window.opener.opener.document.Frm1.privacy.style.display = "";
	  		window.opener.opener.document.Frm1.docIdent.style.display = "";
	  		window.opener.opener.document.Frm1.CODMONOTIPOORIG.value = "F";
      	}
      	window.opener.opener.document.Frm1.Coop.value = "Invio cartaceo";
      	window.opener.opener.document.Frm1.imgCoop.src = "../../img/text.gif";
      }
      window.opener.opener.document.Frm1.origProvCpi.value = codProvCpi;
      window.opener.opener.document.Frm1.origRegCpi.value = codRegCpi;
	  window.close();
      window.opener.close();
}
-->
 
</SCRIPT>
</head>

<body class="gestione">
<%
	StringBuffer jsCommand = new StringBuffer();
    jsCommand.append("AggiornaFormPadre('");
    jsCommand.append(Coop);
   	jsCommand.append("','");
    jsCommand.append(imgCoop);
    jsCommand.append("','");
    jsCommand.append(codProvCpi);
    jsCommand.append("','");
    jsCommand.append(codRegCpi);        
    jsCommand.append("');");
%>
    <script><%=jsCommand.toString()%></script>

</body>
</html>