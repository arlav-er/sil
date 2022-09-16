<jsp:directive.include file="top.jsp" />
<div class="row">


				<div class="panel-heading cas-heading bg-pacman-icon">
					<div class="title">Benvenuto</div>
				</div>

				<div class="panel-body cas-body">


					<div class="col-md-3">

						<img src="/MyCas/images/calabria/logo_calabria.jpg" alt="Logo" style="margin: 10px;" border="0"
							class="hidden-xs img-responsive">

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


						<div class="col-md-12 top-buffer">
							<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12 pull-right">

							</div>

							<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12 pull-right">
								

							</div>
						</div>

					</div>
					

					<div class="col-md-12">
						<div class="description">
							<b>Lavoro per Te</b> è il portale di servizi della Regione Calabria, 
							realizzato in collaborazione con le Province e i Centri per l'Impiego 
							che consente ai cittadini di aderire alla Garanzia Giovani 
							e alle aziende di compilare e gestire le convenzioni e i progetti formativi per l'attivazione dei tirocini.
						    <br>I servizi sono rivolti sia ai cittadini che alle imprese e per accedervi è necessario registrarsi. 
						    <br>
							<br>

						</div>
					</div>
					

				</div>
			</div>






<jsp:directive.include file="bottom.jsp" />


				