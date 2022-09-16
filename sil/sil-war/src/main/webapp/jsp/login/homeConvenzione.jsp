<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ include file="./gitInfo.inc"%>
<%@ page  import="it.eng.sil.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance(); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
<title><%=regione.getNomeSIL()%> - Sistema Informativo Lavoro <%=regione.getNome()%></title>
<LINK REL="SHORTCUT ICON" HREF="../../img/favicon.ico">
<link rel="stylesheet" href="../../css/hp.css" type="text/css">
<script language="javascript">
<!--
function fullwin(){
window.open('http://www.greenteam.it/SinformTutorialSil/home.htm','SinformTutorialSil','location=0,menubar=0,statusbar=0,scrollbars=no,toolbar=0,resizable=0, channelmode') 
} 
--> 
</script>


</head>

<body>

		<!-- 
			Informazioni Git:
			git.branch=<%=gitBranch%>
			git.build.time=<%=gitBuildTime%>
			git.commit.id=<%=gitCommitId%>
		-->

<CENTER>
<TABLE border="0" height="100%" width="95%">
	<TBODY>
		<TR>
			<TD colspan="5" align="center"><IMG border="0" src="../../img/hp/<%=regione.getCodice()%>_perla.jpg"
				width="258" height="112"></TD>
		</TR>

		<TR>
			<TD height="10">&nbsp;
		</TR>

		<TR>
			<TD align="center"></TD>
			<TD align="center" colspan="3">




			<table border="1">
				<tr>
					<td>
					<TABLE border="0">

						<TBODY>
							<TR>
								<TD align="center"><IMG border="0" src="../../img/loghi/<%=provincia.getCodice()%>_provHome.gif"
									width="48" height="53" align="middle"></TD>
								<TD align="left" colspan="3"><FONT size="+1"><B>
								<%=provincia.getDispAccesso()%></B></FONT></TD>
							</TR>
							<TR>
								<TD align="center" colspan="4"><IFRAME name="FRAME5"
									src="../../servlet/fv/AdapterHTTP?PAGE=loginConvenzionePage&NEW_SESSION=TRUE"
									width="418" height="100" frameborder="0" scrolling="no"
									marginwidth="0" marginheight="0"> Login di autenticazione al
								servizio SIL </IFRAME></TD>
							</TR>
						</TBODY>
					</TABLE>


					</td>
				</tr>
			</table>


			</TD>
			<TD align="center"></TD>
		</TR>

		<TR>
			<TD colspan="5" align="center" valign="middle" height="30"></TD>
		</TR>
		<TR>
		<TD colspan="5" align="center">Versione <%=provincia.getVersione()%> (Aggiornamento del <%=gitBuildTime%>)</TD>
		</TR>
		
		<TR>
			<TD height="50">&nbsp;
		</TR>

		<TR>
		<%=regione.getLogoSIL()%>
			<TD align="center" valign="middle" width="19%"><IMG border="0"
				src="../../img/hp/<%=regione.getCodice()%>_siler.gif" width="107" height="47"></TD>
			<TD  align="center" width="19%"><A href="<%=regione.getUrlSito()%>"
				target="_blank"><IMG border="0" src="../../img/hp/<%=regione.getCodice()%>_giunta.gif" width="138"
				height="26"></A></TD>
			<TD width="19%" align="center"><A
				href="http://europa.eu.int/comm/employment_social/esf2000/index.htm"
				target="_blank"><IMG border="0" src="../../img/hp/unione_europea.gif"
				width="99" height="57"></A></TD>
			<TD width="19%" align="right"><A href="http://www.innovazione.gov.it/"
				target="_blank"><IMG border="0" src="../../img/hp/MIT.jpg" width="150"
				height="100"></A></TD>
		</TR>
	</TBODY>
</TABLE>
</CENTER>
</body>
</html>
