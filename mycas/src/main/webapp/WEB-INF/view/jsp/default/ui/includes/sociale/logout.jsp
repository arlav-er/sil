<!DOCTYPE html>
<html>
<head>
<title>Regione Emilia-Romagna - Lavoro per Te</title>

<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<link rel="shortcut icon" type="image/x-icon" href="/MyCas/favicon.ico" />
<link rel="icon" href="<c:url value="" />" type="image/x-icon" />

<link rel="stylesheet" href="/MyCas/css/services/bootstrap.min.css">
<link rel="stylesheet" href="/MyCas/css/services/mycas.css" />
<link rel="stylesheet" href="/MyCas/css/services/sociale.css">



<script type="text/javascript" src="/MyCas/js/jquery.min.js"></script>
<script type="text/javascript" src="/MyCas/js/bootstrap.min.js"></script>

</head>
<body>
	<div class="container-fluid header hidden-xs">
		<div class="logos">
			<a class="logo" href="http://www.regione.emilia-romagna.it"> Il portale della Regione Emilia-Romagna </a>
			<h1 class="logoHeader">
				<a accesskey="1" href="http://sociale.regione.emilia-romagna.it">Sociale</a>
			</h1>
			<div id="rerLogoContainer" class="hidden-xs">

				<a href="http://www.regione.emilia-romagna.it">
					<div class="rerLogo"></div>
				</a>
			</div>
		</div>

		<div class="headerImg"></div>
	</div>
	<div id="contentCarousel">
		<div id="subsiteTitle">
			<span class="visible-xs"> <img src="/MyCas/images/rer/logo-sm.png"
				style="display: inline; float: left; margin-right: 5px;" alt="Logo"></span>
			<h2>Accesso ai Servizi Online</h2>
		</div>
	</div>
	<div id="msg" class="success" style="margin-right: 20em; margin-top: 3em;">

		<b><spring:message code="screen.logout.header" /></b>
		<p>
			<a href="<%=socialeAddress%>">Riaccedi ai servizi online</a>
		</p>
	</div>
	<div id="container-fluid portalFooter footLogout" style="margin-top: 150px">
		<div class="shadow_wrapper_sx">
			<div class="shadow_wrapper_dx">
				<ul id="portal-siteactions">
					<li id="siteaction-contatti"><a href="http://sociale.regione.emilia-romagna.it/contatti"
						title="Contatti">Contatti</a></li>
					<li id="siteaction-informazioni-sul-sito"><a href="http://sociale.regione.emilia-romagna.it/info"
						title="Informazioni sul sito">Informazioni sul sito</a></li>
					<li id="siteaction-note-legali"><a href="http://sociale.regione.emilia-romagna.it/note-legali"
						title="Note legali">Note legali</a></li>
					<li id="siteaction-privacy"><a href="http://sociale.regione.emilia-romagna.it/privacy"
						title="Privacy">Privacy</a></li>
				</ul>
			</div>
		</div>
		<div id="portal-colophon">
			<div class="colophonWrapper">
				<div class="shadow_wrapper_sx">
					<div class="shadow_wrapper_dx">
						Regione Emilia-Romagna (CF 800.625.903.79) - Viale Aldo Moro 52, 40127 Bologna - Centralino: 051.5271 <br />
						Ufficio Relazioni con il Pubblico: Numero Verde URP: 800 66.22.00, <a href="mailto:urp@regione.emilia-romagna.it">urp@regione.emilia-romagna.it</a>
						, <a href="mailto:urp@postacert.regione.emilia-romagna.it">urp@postacert.regione.emilia-romagna.it</a>
					</div>
				</div>
			</div>
		</div>
		<div class="clearfix">
			<!-- -->
		</div>
	</div>

<%
	ContestiBean contestiBean = new ContestiBean();
	List<String> contexts = null;
	String errore = null;
	try {
		contexts = contestiBean.getContesti();
	} catch (IOException ex) {
		errore = "Errore nel reperimento dei contesti";
	}
%>

<script type="text/javascript">
<%for (String ctx : contexts) {%>$.ajax({url: location.origin + '<%=ctx%>/IntrospectionServlet/logout'
	});
<%}%>
	
<%if (socialeAddress!=null){ %>
			$.ajax({url: "<%=socialeAddress%>" + '/IntrospectionServlet/logout'});
	
<%}%>

	
</script>



</body>
</html>