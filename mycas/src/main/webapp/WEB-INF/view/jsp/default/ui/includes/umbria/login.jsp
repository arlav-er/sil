<%@page import="it.eng.sil.dto.StNotiziaDTO"%>
<%@page import="java.util.List"%>
<%@page import="it.eng.sil.mycas.NotizieBean"%>
<jsp:directive.include file="top.jsp" />
<section class="main-content">
	<div id="content" class="row">

		<h2 class="page-title">Lavoro per Te Umbria</h2>
		<div id="logo_lavoro_per_te" class="col-md-8">
			<div class="paragraph">
				<div>È il portale di ARPAL Umbria, Agenzia Regionale per le Politiche Attive del Lavoro di cui fanno parte
					anche i Centri per l'Impiego per rendere più semplice l'incontro tra chi offre e chi cerca lavoro.</div>
				<div>
					I servizi sono rivolti sia ai cittadini che alle imprese.
					<br />
					L'accesso richiede la registrazione.
				</div>
			</div>

			<h2 class="page-title black m-t-40">Help Desk</h2>

			<div class="paragraph">
				<div>
					Per assistenza relativa ai servizi del portale inviare una e-mail a:
					<br />
					<a href="mailto:lavoroperteumbria@regione.umbria.it" class="red-link">lavoroperteumbria@regione.umbria.it</a>
				</div>
				<div>
					INDICARE:
					<br />
					Tipologia del problema / Nome / Cognome / Codice fiscale / Eventuale recapito
				</div>
			</div>
			<%
				List<StNotiziaDTO> notizieFilteredAggIntAvv = new NotizieBean().getStNotiziaDTOs(NotizieBean.INT,
						NotizieBean.AGG, NotizieBean.AVV);
				if (!notizieFilteredAggIntAvv.isEmpty()) {
			%>
			<div id="info" class="hidden-xs  hidden-sm">
				<h2 class="page-title black m-t-40">Avvisi ed interruzioni di servizio programmate:</h2>

				<div id="myCarousel" class="carousel slide" data-ride="carousel">
					<!-- Indicators -->

					<ol class="carousel-indicators">
						<%
							for (int index = 0; index < notizieFilteredAggIntAvv.size(); index++) {
									if (index == 0) {
										out.println("<li data-target='#myCarousel' data-slide-to=" + '"' + index + '"' + " class="
												+ '"' + "active" + '"' + "></li>");
									} else {
										out.println("<li data-target='#myCarousel' data-slide-to=" + '"' + index + '"' + "></li>");
									}
								}
						%>
					</ol>

					<!-- Wrapper for slides -->
					<div class="carousel-inner">
						<%
							for (int index = 0; index < notizieFilteredAggIntAvv.size(); index++) {
									StNotiziaDTO stNotizia = (StNotiziaDTO) notizieFilteredAggIntAvv.get(index);
									if (index == 0) {
										out.println("<div class="
												+ '"'
												+ "item active"
												+ '"'
												+ ">"
												+ String.format("<b>%s - %s:</b><div>%s</div>", stNotizia.getOggetto(),
														stNotizia.getDtmPubblicazione(), stNotizia.getContenuto()) + "</div>");
									} else {
										out.println("<div class="
												+ '"'
												+ "item"
												+ '"'
												+ ">"
												+ String.format("<b>%s - %s:</b><div>%s</div>", stNotizia.getOggetto(),
														stNotizia.getDtmPubblicazione(), stNotizia.getContenuto()) + "</div>");
									}
								}
						%>
					</div>

					<!-- Left and right controls -->
					<a class="left carousel-control" href="#myCarousel" data-slide="prev">
						<span class="glyphicon glyphicon-chevron-left ">
							<img src="images/umbria/left-arrow.png">
						</span>
						<span class="sr-only">Previous</span>
					</a>
					<a class="right carousel-control" href="#myCarousel" data-slide="next">
						<span class="glyphicon glyphicon-chevron-right ">
							<img src="images/umbria/right-arrow.png">
						</span>
						<span class="sr-only">Next</span>
					</a>
				</div>
			</div>
			<%
				}
			%>
			

		</div>

		<div id="login_form" class="col-md-4">
			<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">

				<form:errors path="*" id="msg" cssClass="errors" element="div" />

				<div class="form-inline">
					<div class="form-group">
						<form:input id="username" size="15" tabindex="1" accesskey="${userNameAccessKey}" path="username"
							autocomplete="false" htmlEscape="true" cssClass="form-control left-small-buffer" placeholder="Username" />

					</div>
				</div>



				<div class="form-inline top-buffer">
					<div class="form-group">
						<form:password id="password" size="15" tabindex="2" path="password" accesskey="${passwordAccessKey}"
							htmlEscape="true" autocomplete="off" cssClass="form-control left-small-buffer" placeholder="Password" />
						<div class="left-small-buffer" style="margin-top: 10px">
							Non ricordi la password?
							<a title="Recupera la password" tabindex="25" href="<%=myaccountAddress%>/forgotPassword" target="_parent">Recuperala!</a>
						</div>
					</div>
				</div>

				<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
				<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />

				<input type="hidden" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<div class="login-continer">
					<br />
					<input id="login_button" class="btn btn-primary" name="submit" accesskey="l"
						value="<spring:message code="screen.welcome.button.login" />" type="submit" />
				</div>
				<div class="left-small-buffer">
					Non possiedi un account? Registrati come
					<a title="Registrati come cittadino se non possiedi un account" tabindex="24"
						href="<%=myaccountAddress%>/register/cittadino" target="_parent">cittadino</a>
					o
					<a title="Registrati come azienda se non possiedi un account" tabindex="24"
						href="<%=myaccountAddress%>/register/azienda" target="_parent">impresa</a>
				</div>
			</form:form>


			<div id="fed-umbria-container" class="top-buffer">
				<div id="fed-umbria">
					<%
						if (areSocialNetworksEnabled) {
					%>
					Accedi tramite i social network
					<div class="top-small-buffer">
						<%
							if (isFacebookEnabled) {
						%>
						<a target="_parent" class="social"
							href="<%=myportalAddress%>/Facebook/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
							<img src="/MyCas/images/services/facebook.png" alt="Accesso tramite Facebook" title="Accesso tramite Facebook"
								height="35" width="35" border=0>
						</a>
						<%
							}
						%>
						<%
							if (isTwitterEnabled) {
						%>
						<a target="_parent" class="social"
							href="<%=myportalAddress%>/Twitter/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
							<img src="/MyCas/images/services/twitter.png" alt="Accesso tramite Twitter" title="Accesso tramite Twitter"
								height="35" width="35" border=0>
						</a>
						<%
							}
						%>
						<%
							if (isGoogleEnabled) {
						%>
						<a target="_parent" class="social"
							href="<%=myportalAddress%>/Google/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
							<img src="/MyCas/images/services/google.png" alt="Accesso tramite Google+" title="Accesso tramite Google+"
								height="35" width="35" border=0>
						</a>
						<%
							}
						%>
					</div>
					<%
						}
					%>

					<div class="top-small-buffer">
						<label>
							<a style="color: #000"
								href="<%=myportalAddress%>/ICAR/UmbriaLogin?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
								Login con FedUmbria / SPID </a>
						</label>
					</div>
				</div>
			</div>
			<div class=" hidden-md hidden-lg hidden-xl">
                <h2 class="page-title black m-t-40">Avvisi ed interruzioni di servizio programmate:</h2>
                <ul>                                    
                    <%
                        for (int index = 0; index < notizieFilteredAggIntAvv.size(); index++) {
                            StNotiziaDTO stNotizia = (StNotiziaDTO) notizieFilteredAggIntAvv.get(index);

                            out.println("<li class="
                                    + '"'
                                    + "item active"
                                    + '"'
                                    + ">"
                                    + String.format("<b>%s - %s:</b><div>%s</div>", stNotizia.getOggetto(),
                                            stNotizia.getDtmPubblicazione(), stNotizia.getContenuto()) + "</li>");

                        }
                    %>
                </ul>
            </div>
			<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12 pull-right"></div>
		</div>





	</div>

</section>
<jsp:directive.include file="bottom.jsp" />


