<jsp:directive.include file="top.jsp" />

<div class="panel-heading cas-heading bg-pacman-icon">
	<div class="title">Benvenuto</div>
</div>
<div class="container panel-body cas-body-vda">

	<div class="row">
		<div class="col-md-3">
			<img src="/MyCas/images/vda/Aosta.png" alt="Logo" style="margin: 10px;" border="0" class="hidden-xs img-responsive">
		</div>

		<div class="col-md-9">
			<p>
				<label>Lavoro per Te è il portale della Regione Autonoma Valle D'Aosta per rendere più semplice l'incontro
					tra chi cerca e chi offre lavoro. <br/>
					 I <a href="http://www.regione.vda.it/lavoro/cittadini/trovare_lavoro/portalelavoroperte_i.aspx">servizi</a> sono accessibili 
					 tramite l'utilizzo di TS/CNS, security card o SPID per i cittadini e a seguito di una registrazione per le imprese 
					 (o per i cittadini senza tessera sanitaria o SPID).
				</label>
			</p>

			<img src="/MyCas/images/vda/BarraVDA.png" style="margin: 20px; width: 90%; height: 10px; border: 1px solid black"
				class="hidden-xs img-responsive">
		</div>

	</div>
	<div class="row">

		<div class="col-md-3">
			<!-- vuota -->
		</div>
		<div class="col-md-7">
			<div class="row" style="color: #454545; border: 5px solid #e3e3e3; border-radius: 5px; margin-left: 20px">

				<div class="col-md-5">
					<p>
						<label>Sei un cittadino?</label>
					</p>
					<p><a href="<%=myportalAddress%>/ICAR/VdaLogin?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">Accedi</a> con la tessera sanitaria (TS/CNS), con la security card o con SPID.</p>
				</div>
				<div class="col-md-3">
					<img src="/MyCas/images/vda/immagine_tessera_sanitaria.jpg"
						style="margin: 20px; width: 70%; padding: 3px; border: 2px solid #e3e3e3;" class="hidden-xs img-responsive">
				</div>
				<div class="col-md-4">
					<img src="/MyCas/images/vda/spid.jpg"
						style="margin: 20px; width: 70%; padding: 3px; border: 2px solid #e3e3e3;" class="hidden-xs img-responsive">
				</div>
			</div>
			<p>
				Non riesci ad accedere con la tessera sanitaria (TS/CNS), con la security card o con SPID?
				<br/> 
				Consulta la sezione	
				<a href="http://www.regione.vda.it/TesseraSanitaria_CartaNazionaledeiServizi/default_i.asp">
					Consulta la sezione Tessera sanitaria/carta nazionale dei servizi/SPID
				</a>
			</p>

		</div>
	</div>
	<!-- terza riga -->
	<div class="row">

		<div class="col-md-3">
			<p>
				<label>Dopo l'iscrizione, nella casella di posta elettronica da te indicata, riceverai una email da
					accountlavoroperte@regione.vda.it contenente il collegamento per confermare la registrazione.</label>
			</p>
		</div>

		<div class="col-md-7">
			<div class="row" style="color: #454545; border: 5px solid #e3e3e3; border-radius: 5px; margin-left: 20px">
				<div class="col-md-12">
					<p>
						<label>Sei un azienda o un cittadino senza tessera sanitaria o SPID?</br> Crea le tue credenziali e accedi al sistema
							USER e PASSWORD
						</label>
					</p>
					<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true" style="margin:20px">

						<form:errors path="*" id="msg" cssClass="errors" element="div" />

						<div class="form-inline">
							<div class="form-group">
								<label for="username" class="fl-label"> <spring:message code="screen.welcome.label.netid" />
								</label>
								<form:input id="username" size="15" tabindex="1" accesskey="${userNameAccessKey}" path="username"
									autocomplete="false" htmlEscape="true" cssClass="form-control left-small-buffer" />
								<span class="left-small-buffer"> Non possiedi un account? Registrati come <a
									title="Registrati come cittadino se non possiedi un account" tabindex="24"
									href="<%=myaccountAddress%>/register/cittadino" target="_parent">cittadino</a> o <a
									title="Registrati come azienda se non possiedi un account" tabindex="24"
									href="<%=myaccountAddress%>/register/azienda" target="_parent">azienda</a>
								</span>
							</div>
						</div>

						<div class="form-inline top-buffer">
							<div class="form-group">
								<label for="password"> <spring:message code="screen.welcome.label.password" />
								</label>
								<form:password id="password" size="15" tabindex="2" path="password" accesskey="${passwordAccessKey}"
									htmlEscape="true" autocomplete="off" cssClass="form-control left-small-buffer" />
								<span class="left-small-buffer"> Non ricordi la password? <a title="Recupera la password" tabindex="25"
									href="<%=myaccountAddress%>/forgotPassword" target="_parent">Recuperala!</a>
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
			</div>
			
						<p>
							Non riesci ad accedere tramite user e password? </br> Invia un'email a <a
								href="mailto:accountlavoroperte@regione.vda.it">accountlavoroperte@regione.vda.it</a>
						</p>

		</div>
	</div>
</div>


<jsp:directive.include file="bottom.jsp" />


