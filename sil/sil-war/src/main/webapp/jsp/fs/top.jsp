<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance(); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
<title>Loghi SIL</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<script language="Javascript" src="../../js/utili.js"
	type="text/javascript"></script>
<script language="Javascript" type="text/javascript">

  var collapsed=false;

  function chiudi(){
    if (confirm ("Sei sicuro di voler uscire dall'applicazione?")) {
    	 window.top.location="AdapterHTTP?PAGE=LOGOUTPAGE";
    	 //window.top.close();	
    }     
  }
  
   function cambiaProfilo(){
    if (confirm ("Sei sicuro di voler cambiare profilo?")){
      window.top.main.location="AdapterHTTP?PAGE=mainPage";
    }  
  }
  
   function goHome(){
      window.top.location="AdapterHTTP?PAGE=framesetPage";
  }
  
  function toggle_menu(){
     
	var fs = window.top.document.getElementById('fsMenu');
	if (fs) {
		if (!collapsed){
			  fs.cols = '0,*';
			  collapsed=true;
	   	}else{
			  fs.cols = '220,*';
		      collapsed=false;
	   }
	}
  }
  
  
  </script>

</head>

<body class="loghi">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td valign="middle" rowspan="2" class="loghi" align="center"
				width="45px"><img src="../../img/loghi/<%=regione.getCodice()%>_logoSILfull3.gif" alt="SIL"
				border="0" align="middle" width="230" height="28" hspace="2"
				vspace="2" usemap="#logoSILfull" border="0">
				<map name="logoSILfull">
					<area shape="RECT" coords="38,6,182,22"
					href="<%=regione.getUrlSito()%>"
					alt="Regione <%=regione.getNome()%>" title="Regione <%=regione.getNome()%>"
					target="_blank">
				</map>
			</td>
			
			<td width="140" valign="middle" rowspan="2" class="loghi" align="center" nowrap>
				<a href="<%=provincia.getUrlSitoCpi()%>" target="_blank">
				<img
				src="../../img/loghi/<%=provincia.getCodice()%>_CPI.gif" border="0" alt="Centro per l'Impiego"
				title="Centro per l'Impiego" align="middle"></a>
				</td>
			<td valign="middle" rowspan="2" class="loghi" align="center"><a
				href="http://www.innovazione.gov.it" target="_blank"> <img
				src="../../img/loghi/mini_logoDit.jpg"
				alt="Ministero per l'Innovazione e le Tecnologie"
				title="Ministero per l'Innovazione e le Tecnologie" border="0"
				align="middle" width="68" height="45"> </a></td>
			<td rowspan="2"><!-- Modifiche per le stondature S.O. 11/03/2004 - Elemento TOP-->
			<table align="center" cellpadding="0" cellspacing="0">
				<tbody>
					<tr>
						<td valign="top" align="left" width="6" height="6"><img
							src="../../img/angoli/gri1.gif" width="6" height="6"></td>
						<td class="tdGrigioTop" height="6">&nbsp;</td>
						<td valign="top" align="right" width="6" height="6"><img
							src="../../img/angoli/gri2.gif" width="6" height="6"></td>
					</tr>
					<tr>
						<td class="tdGrigioLeft" width="6">&nbsp;</td>
						<td align="center" bgcolor="#ffffff">
						<table class="main" cellpadding="0" cellspacing="0">
							<tbody>
								<tr valign="top">
									<td class="tdUtente" rowspan="2">&nbsp;</td>
									<td class="tdUtente">Utente:</td>
									<td class="tdUtente" rowspan="2">&nbsp;</td>
									<td class="tdProfilo" nowrap><% if (user.getPrgProfilo()!=0){%>
									Profilo: <b><%=user.getDescProfilo()%></b> <% } else {%> &nbsp;
									<% }%></td>

									<td class="tdProfilo" width="8px" rowspan="2">&nbsp;</td>
								</tr>

								<tr>
									<td class="tdUtente"><b><%=Utils.notNull(user.getNome()) + " " + Utils.notNull(user.getCognome())%></b></td>
									<td class="tdProfilo"><% if (user.getCdnGruppo()!=0){%> Org.: <b><%=user.getDescGruppo()%></b>
									<%} else {%> &nbsp; <%}%></td>



								</tr>
							</tbody>
						</table>
						<!-- Modifiche per le stondature S.O. 11/03/2004 - Elemento BOTTOM-->
						</td>
						<td class="tdGrigioRight" width="6">&nbsp;</td>
					</tr>
					<tr valign="bottom">
						<td valign="bottom" align="left" width="6" height="6"><img
							src="../../img/angoli/gri4.gif" width="6" height="6"></td>
						<td class="tdGrigioBottom" height="6">&nbsp;</td>
						<td valign="bottom" align="right" width="6" height="6"><img
							src="../../img/angoli/gri3.gif" width="6" height="6"></td>
					</tr>
				</tbody>
			</table>
			</td>
			<td class="loghi" align="right" colspan="2"><b> <script
				language="JavaScript" type="text/javascript"><!--
        data_estesa()
        --></script> </b></td>
		</tr>
		<tr>
			<td class="loghi" align="right"><% if (sessionContainer.getAttribute("MANY_PROFS")!= null){%>
			<a class="loghi" href="#" onclick="cambiaProfilo()">Cambia Profilo</a>
			<%}
         // sessionContainer.delAttribute("ONE_PROF");
      %></td>
			<td align="right" class="loghi"><b><a href="#" onclick="chiudi()"
				class="loghi">LogOff</a>&nbsp;&nbsp;</b></td>
		</tr>
	</tbody>
</table>
<table width="100%" border="0">
	<tbody>
		<tr valign="top">
			<td class="loghi2" width="18"><a href="javascript:toggle_menu()"><IMG
				border="0" src="../../img/toggle_menu.gif" width="16" height="16" title="Mostra/nascondi menu" alt="Mostra/nascondi menu"></a></td>
			<td width="80%" id="str_percorso" class="loghi2">
			<div id="id_percorso_nav"></div>
			</td>
			<td align="center" class="loghi3">&nbsp;</td>
			<td align="center" width="10" class="loghi3"><!-- <a href="#" onClick="avviso();" class="loghi">Mappa</a>-->&nbsp;&nbsp;</td>
			<td align="right" width="10" class="loghi3"><a href="#"
				onclick="goHome()" class="loghi"><img src="../../img/home.gif"
				alt="Home" width="16" height="16"></a>&nbsp;&nbsp;</td>
			<td align="right" width="10" class="loghi3"><a href="#"
				onclick="avviso();" class="loghi"><img src="../../img/help.gif"
				alt="Help" width="16" height="16"></a>&nbsp;&nbsp;</td>
		</tr>
	</tbody>
</table>

</body>
</html>
