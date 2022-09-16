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
	<section class="main-content"> 
	<!-- central body ------------------------------------------------------------------------------- -->
		<div class="row">
	
				<div class="panel-heading cas-heading bg-pacman-icon">
					<div class="title">Benvenuto</div>
				</div>

				<div class="panel-body cas-body">

					<div class="col-md-3">
						<a tabindex="19" href="http://sociale.regione.emilia-romagna.it/"> 
						<img src="/MyCas/images/sociale/logo_sociale.svg" alt="Logo" 
							 class="hidden-xs img-responsive">
						</a>
						
					</div>
					
					<div class="col-md-9">

						<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true" cssClass="pull-right">

							<form:errors path="*" id="msg" cssClass="errors" element="div" />

							<div class="form-inline">
								<div class="form-group">
									<label for="username" class="fl-label"> <spring:message code="screen.welcome.label.netid" />
									</label>
									<form:input id="username" size="15" tabindex="1" accesskey="${userNameAccessKey}" path="username"
										autocomplete="false" htmlEscape="true" cssClass="form-control left-small-buffer" />
					
								</div>
							</div>

							<div class="form-inline top-buffer">
								<div class="form-group">
									<label for="password"> <spring:message code="screen.welcome.label.password" />
									</label>
									<form:password id="password" size="15" tabindex="2" path="password" accesskey="${passwordAccessKey}"
										htmlEscape="true" autocomplete="off" cssClass="form-control left-small-buffer" />
									<span class="left-small-buffer"> Non ricordi la password? <a title="Recupera la password" tabindex="25"
										href="<%=myaccountAddress%>/forgotPassword?sociale=0" target="_parent">Recuperala!</a>
									</span>
								</div>
							</div>

							<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
							<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />

							<input type="hidden" name="lt" value="${loginTicket}" />
							<input type="hidden" name="execution" value="${flowExecutionKey}" />
							<input type="hidden" name="_eventId" value="submit" />
							<div class="login-continer">
								<br /> <input class="btn btn-primary" name="submit" accesskey="l"
									value="<spring:message code="screen.welcome.button.login" />" type="submit" />
							</div>

						</form:form>
					
					</div>
			
					<div class="col-md-12">
						<div class="description">
							Il Portale del Sistema informativo RES consente l'acquisizione delle domande di accesso, la gestione nel tempo e il monitoraggio periodico
							dell'andamento di attuazione del Reddito di Solidarietà.
							<br> <br>
							
							
							 Non riesci ad accedere? <a href="mailto:assistenza.res@cup2000.it">assistenza.res@cup2000.it</a>
							<br> <br>
						</div>
					</div>
			
				</div>

		</div>

	</section>
	<div id="container-fluid portalFooter">
		<div class="shadow_wrapper_sx">
			<div class="shadow_wrapper_dx">
				<ul id="portal-siteactions">
					<li id="siteaction-contatti">
						<a href="http://sociale.regione.emilia-romagna.it/contatti" title="Contatti">Contatti</a>
					</li>
					<li id="siteaction-informazioni-sul-sito">
						<a href="http://sociale.regione.emilia-romagna.it/info" title="Informazioni sul sito">Informazioni sul sito</a>
					</li>
					<li id="siteaction-note-legali">
						<a href="http://sociale.regione.emilia-romagna.it/note-legali" title="Note legali">Note legali</a>
					</li>
					<li id="siteaction-privacy">
						<a href="http://sociale.regione.emilia-romagna.it/privacy" title="Privacy">Privacy</a>
					</li>
				</ul>
			</div>
		</div>
		<div id="portal-colophon">
			<div class="colophonWrapper">
				<div class="shadow_wrapper_sx">
					<div class="shadow_wrapper_dx">
					Regione Emilia-Romagna (CF 800.625.903.79) - Viale Aldo Moro 52, 40127 Bologna - Centralino: 051.5271
					<br />
					Ufficio Relazioni con il Pubblico: Numero Verde URP: 800 66.22.00,
						<a href="mailto:urp@regione.emilia-romagna.it">urp@regione.emilia-romagna.it</a>
					,
						<a href="mailto:urp@postacert.regione.emilia-romagna.it">urp@postacert.regione.emilia-romagna.it</a>
					</div>
				</div>
			</div>
		</div>
	<div class="clearfix">
		<!-- -->
	</div>
</div>
	
</body>
</html>

