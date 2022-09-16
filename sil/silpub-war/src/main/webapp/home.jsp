<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="./gitInfo.inc"%>

<%	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance(); %>

<!DOCTYPE html>
<html lang="ita">
	<head>
    	<title><%=regione.getNomeSIL()%> - Sistema Informativo Lavoro <%=regione.getNome()%></title>
    	<LINK REL="SHORTCUT ICON" HREF="img/favicon.ico">
    	<link rel="stylesheet" href="css/stili.css" type="text/css">
  	</head>
	
	<body class="menu">
	
		<!-- 
			Informazioni Git:
			git.branch=<%=gitBranch%>
			git.build.time=<%=gitBuildTime%>
			git.commit.id=<%=gitCommitId%>
		-->
		
		<br/> 
		<H2 align=center>Portale della <%=regione.getStrAccesso()%></H2>
		<h2>Sezione pubblica del SIL<BR></h2>

		<P align="center"><BR>Versione <%=provincia.getVersione()%> (Aggiornamento del <%=gitBuildTime%>)</p>
		<br/>
		<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebRicercaPubbPage&NEW_SESSION=true">Richieste di Personale</a></P>
		<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaProvPage&NEW_SESSION=true">Griglia Richieste di Personale</a></P>
		<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebRicercaPubbPage&FlagCM=true&NEW_SESSION=true">Richieste di Personale in Collocamento Mirato</a></P>
		<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaASPage&NEW_SESSION=true">Griglia Aste art.16 L.56/87</a></P>
		<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaCMPage&NEW_SESSION=true">Collocamento Mirato Numerico</a></P>
<!-- 	<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaDownloadPage&NEW_SESSION=true&codSezione=L">Modulistica lavoratore</a></P> -->
<!-- 	<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaDownloadPage&NEW_SESSION=true&codSezione=A">Modulistica azienda</a></P> -->
<!-- 	<P align="center"><a href="servlet/fv/AdapterHTTP?PAGE=WebGrigliaDownloadPage&NEW_SESSION=true&codSezione=G">Altra documentazione</a></P> -->

		<table align="center" border="0">
			<tr>
				<td align="center"><A href="http://www.innovazione.gov.it/"
					target="_blank" ><IMG border="0" src="img/MIT_hp.jpg" width="150"
					height="100" alt="Ministro per l'Innovazione e le Tecnologie - Piano Nazionale di eGovernment"></A></td>
			</tr>
		</table>
	</body>
</html>