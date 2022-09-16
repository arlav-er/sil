<%@ page contentType="text/html;charset=utf-8"%>
<%@ page session="false"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                it.eng.sil.security.User" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance(); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">

<html lang="ita">
<head>
	<title><%=regione.getNomeSIL()%> - Sistema Informativo Lavoro <%=regione.getNome()%></title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<script language="JavaScript" src="../../js/browser_detect.js"></script>
	<script language="JavaScript" src="../../js/monitoraggio.js"></script>
	
	<script language="JavaScript">
	
	function check(){
		var okBrowser=true;
		var okDim = true;

		okBrowser= 	is_ie6up   ||
    				is_nav7up ||
		        	is_fx || 
		        	is_moz;
	
		if(screen.width<1024||screen.height<768){
			okDim= false;
		}

		var ok = okBrowser && okDim;
		
		/*if (!ok)	{
	   		 var f="AdapterHTTP?ACTION_NAME=WarningDetectBrowser"
	    	 var t="_blank";
	         var feat="width=400,height=300,screenX=0,screenY=2,left=200,top=100,resizable=yes,toolbar=auto,menubar=no,titlebar=no,alwaysRaised=yes,status=no";
	         hWindow=open(f,t,feat);
		}*/
	} 
	
	</script>
</head>

<frameset onLoad="check()" rows="0px,68px,*" cols="*" frameborder="YES" border="0" framespacing="0" id="fsMain">
	   <frame name="refreshFrame" src="AdapterHTTP?PAGE=messagePage" scrolling="no" frameborder="0" >
	   <frame name="alto" title="Area Loghi SIL" src="AdapterHTTP?PAGE=topPage" longdesc="Area Loghi, utente collegato, percorso" scrolling="no" frameborder="0" >
	   <frameset cols="220,*" rows="*"  border="0" framespacing="0" id="fsMenu">
	   			 <frame name="menu" title="Menu" src="AdapterHTTP?PAGE=menuPage" longdesc="Menu di navigazione Laterale"  scrolling="auto" frameborder="0">
		  		 <frameset frameborder="YES" rows="*,42px"  border="0" framespacing="0" id="fsFooter">
				 		   <frame name="main" title="Gestione" longdesc="Contesto dell'applicativo SIL" src="AdapterHTTP?PAGE=mainPage"  scrolling="auto" frameborder="0" >
						   <frame name="footer" title="Navigazione" longdesc="Menu di navigazione a piede di pagina" src="AdapterHTTP?PAGE=footerPage" scrolling="auto" frameborder="0">
				 </frameset>
		</frameset>
</frameset>

<body>
<noframes><body>
Per visualizzare il sito SIL devi utilizzare un browser che supporti i frames,
come Microsoft Internet Explorer, Netscape Navigator o Mozilla FireFox.
</body></noframes>
</body>
</html>
