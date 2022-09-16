<jsp:directive.include file="top.jsp" />

<section id="presentazione-login">
	<div class="section-content container-fluid">
		<div class="row">
			<div class="col-md-5">
				<div class="panel">
					<div class="panel-body">
						<div class="row">
							<div class="col-md-12">
								<a href="https://www.sil.provincia.tn.it/welcomepage">
									<img src="/MyCas/images/trento/presentazione_login_pat.png"
										alt="Immagine di presentazione"
										class="hidden-xs img-responsive">
								</a>
							</div>
							<div class="col-md-12 info info-xs">
								Trentino Lavoro è il portale di servizi della Provincia Autonoma di Trento rivolto a chi
								cerca e chi offre lavoro.
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-offset-1 col-md-5">
				<div class="panel">
					<div class="panel-heading justify-content-center">
						Login
					</div>
					<div class="panel-body">
						<form:form method="post" id="login-form" commandName="${commandName}"
							htmlEscape="true">
							
							<div class="row">
								<div class="col-md-10">
									<div class="form-group input-group">
										<span class="input-group-addon"><i class="fas fa-user"></i></span>
										<form:input id="username"
												accesskey="${userNameAccessKey}" path="username"
												autocomplete="false" htmlEscape="true"
												cssClass="form-control" placeholder="Username"/>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-10">
									<div class="form-group input-group">
											<span class="input-group-addon"><i class="fas fa-key"></i></span>
											<form:password id="password"
												path="password" accesskey="${passwordAccessKey}"
												htmlEscape="true" autocomplete="off"
												cssClass="form-control" placeholder="Password"/>
									</div>
								</div>
							</div>
							
							<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
							<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
			 
							<input type="hidden" name="lt" value="${loginTicket}" />
							<input type="hidden" name="execution" value="${flowExecutionKey}" />
							<input type="hidden" name="_eventId" value="submit" />
							<div class="row">
								<div class="col-md-10">
									<form:errors path="*" id="msg" cssClass="errors" element="div" />
								</div>
								<div class="col-md-10">
									<input id="login_button" class="btn btn-primary pull-right" name="submit"
										accesskey="l"
										value="<spring:message code="screen.welcome.button.login" />" type="submit" />
								</div>
							</div>
						</form:form>
						
						<div id="login-help-link" class="row">
							<div id="accesso-icar" class="col-md-12 info">                                    
	 							<b>
		 							<a href="/trentinolavoro/ICAR/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
		 								Clicca qui per accedere ai servizi on line del sistema pubblico trentino
		 							</a>
		 						</b>
	 						</div>
							<div class="col-md-12 info">
								Non possiedi un account? Registrati come
								<a title="Registrati come cittadino se non possiedi un account"
									href="<%=myaccountAddress%>/register/cittadino"
									target="_parent">cittadino</a>
									o 
									<a title="Registrati come azienda se non possiedi un account"
										href="<%=myaccountAddress%>/register/azienda"
										target="_parent">azienda</a>
							</div>
							<div class="col-md-12 info">
								Non ricordi la password?
								<a title="Recupera la password"
									href="<%=myaccountAddress%>/forgotPassword"
									target="_parent">Recuperala!</a>
							</div>
						
							<div class="col-md-12 info">
								Per eventuali problemi di accesso
								<a href="mailto:help-portale.adl@provincia.tn.it">help-portale.adl@provincia.tn.it</a>
							</div>
							
							<div class="col-md-12 info">
								<a href="https://www.agenzialavoro.tn.it/Istruzioni-per-la-consultazione-di-Trentino-lavoro">Scarica le guide per navigare</a>
							</div>
							
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<section class="only-mobile-show">
	<div class="section-content container-fluid">
		<div class="row">
			<div class="col-md-12">
				<a href="https://www.sil.provincia.tn.it/welcomepage">
					<img src="/MyCas/images/trento/presentazione_login_pat.png"
						alt="Immagine di presentazione">
				</a>
			</div>
		</div>
	</div>
</section>
<section id="servizi">
	<div class="section-content container-fluid">
		<div class="panel col-md-6">
			<div class="panel-heading justify-content-center">Servizi alle persone</div>
			<div class="panel-body">
				<div id="servizi-alle-persone">
					<div class="row">
						<div class="col-md-6 service-item">
							<i class="fas fa-book">
							</i>
								Consulta le offerte
						</div>
						<div class="col-md-6 service-item">
							<i class="far fa-file-alt">
							</i>
								Scrivi il tuo CV
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-comments">
							</i>
								Fatti conoscere dalle aziende
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-cogs">
							</i>
								FormazionexTe
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-question-circle">
							</i>
								Chiedi una consulenza
						</div>
						<div class="col-md-6 service-item">
							<i class="far fa-arrow-alt-circle-right">
							</i>
								Aderisci a Garanzia Giovani
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-paste">
							</i>
								Servizi amministrativi
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="panel col-md-6">
			<div class="panel-heading justify-content-center">Servizi alle aziende</div>
			<div class="panel-body">
				<div id="servizi-alle-aziende">
					<div class="row">
						<div class="col-md-6 service-item">
							<i class="fas fa-bullhorn">
							</i>
								Crea la tua vetrina
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-folder">
							</i>
								Pubblica le tue offerte di lavoro
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-envelope"></i>
								Trasmetti la tua comunicazione obbligatoria con SARE
						</div>
						<div class="col-md-6 service-item">
							<i class="fas fa-user">
							</i>
								Chiedi una Consulenza
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<section id="avvisi">
	<div class="section-content container-fluid">
		<div id="avviso_utenti">
			<div id="avvisi-title" class="col-md-12 title">Avvisi</div>
			<ul id="avvisi-lista" class="col-md-12">
				<%=new NotizieBean().getNotizie()%>
			</ul>
		</div>
	</div>
</section>
     
     
<jsp:directive.include file="bottom.jsp" />


		