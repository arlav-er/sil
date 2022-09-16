<%@page import="it.eng.sil.dto.StNotiziaDTO"%>
<%@page import="java.util.List"%>
<%@page import="it.eng.sil.mycas.NotizieBean"%>
<jsp:directive.include file="top.jsp" />
<div class="container">
	<div class="row">
		<div class="col-md-12 body-logo">
			<img src="/MyCas/images/rer/logo_lavoroxte_text_only.png" alt="Logo" class="img-responsive">
		</div>
		<div class="col-sm-12 col-md-6 m-t-10">
			<p style="margin-bottom: 40px; font-size: 16px;">Lavoro per Te &egrave; il portale di servizi dell'Agenzia per il
				lavoro della Regione Emilia-Romagna nato per rendere pi&ugrave; semplice l'incontro fra chi offre e chi cerca
				lavoro. I servizi sono rivolti sia ai cittadini che alle imprese e per accedervi &egrave; necessario registrarsi.</p>

			<h2>Help Desk</h2>
			<div class="help-info">
				Non riesci ad accedere ? Contatta l'assistenza tecnica:
				<a href="mailto:accountlavoroxte@regione.emilia-romagna.it">accountlavoroxte@regione.emilia-romagna.it</a>
			</div>
			<div class="help-info">
				Per informazioni sui servizi, scrivi alla redazione:
				<div class="col-sm-12" style="padding: 0px;">
					<a href="mailto:lavoroperte@regione.emilia-romagna.it">lavoroperte@regione.emilia-romagna.it</a>
				</div>

			</div>
		</div>
		<div class="col-sm-12 col-md-6 login-section">
			<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true" cssClass="form-horizontal">
				<form:errors path="*" id="msg" cssClass="errors" element="div" />
				<h3 class="login-section-heading">Area riservata</h3>
				<div class="form-group">
					<label for="username" class="control-label col-sm-2">
						<spring:message code="screen.welcome.label.netid" />
					</label>
					<div class="col-sm-9">
						<form:input id="username" size="15" tabindex="1" accesskey="${userNameAccessKey}" path="username"
							autocomplete="false" htmlEscape="true" cssClass="form-control" />
					</div>
					<div class="col-md-offset-2 col-sm-10">
						Non possiedi un account? Registrati come
						<a title="Registrati come cittadino se non possiedi un account" tabindex="24"
							href="<%=myaccountAddress%>/register/cittadino" target="_parent">cittadino</a>
						o
						<a title="Registrati come azienda se non possiedi un account" tabindex="24"
							href="<%=myaccountAddress%>/register/azienda" target="_parent">azienda</a>
					</div>
				</div>



				<div class="form-group">
					<label for="password" class="control-label col-sm-2">
						<spring:message code="screen.welcome.label.password" />
					</label>
					<div class="col-sm-9">
						<form:password id="password" size="15" tabindex="2" path="password" accesskey="${passwordAccessKey}"
							htmlEscape="true" autocomplete="off" cssClass="form-control" />
					</div>
					<div class="col-md-offset-2 col-md-10">
						Non ricordi la password?
						<a title="Recupera la password" tabindex="25" href="<%=myaccountAddress%>/forgotPassword" target="_parent">Recuperala!</a>
					</div>
				</div>


				<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
				<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />

				<input type="hidden" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<div class="col-md-12 text-center">
					<br />
					<input class="btn btn-primary" name="submit" accesskey="l"
						value="<spring:message code="screen.welcome.button.login" />" type="submit" />
				</div>

			</form:form>

			<div class="row m-t-10">
				<div class="col-md-12 top-buffer">


					<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 text-center">
						<%
							if (isFederaEnabled) {
						%>

						Accedi tramite fedERa
						<div class="top-small-buffer">
							<a target="_parent" class="social"
								href="<%=myportalAddress%>/Federa/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
								<img src="/MyCas/images/services/federa2.png" alt="Accesso tramite SPID" title="Accesso tramite SPID"
									height="70" border=0>
							</a>
						</div>
						<%
							}
						%>

					</div>

					<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 text-center">

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

					</div>
				</div>
			</div>
		</div>
	</div>

</div>

<div class="container avvisi">
	<div class="row">
		<div class="col-md-6">
			<h3 class="avvisi-heading">Avvisi</h3>
		</div>

		<div class="col-md-6 text-right">
			<a class="all-news" href="#">Tutti gli avvisi</a>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12 news-list">
			<%
				List<StNotiziaDTO> notizieFilteredAggIntAvv = new NotizieBean().getStNotiziaDTOs();
			if (!notizieFilteredAggIntAvv.isEmpty()) {
			%>


			<%
				for (int index = 0; index < notizieFilteredAggIntAvv.size(); index++) {
				StNotiziaDTO stNotizia = (StNotiziaDTO) notizieFilteredAggIntAvv.get(index);

				out.println("<div class=" + '"' + "news-item" + '"' + ">"
				+ String.format("<h5>%s - %s</h5><div class='news-item-body'>%s</div>", stNotizia.getOggetto(),
						stNotizia.getDtmPubblicazione(), stNotizia.getContenuto())
				+ "</div>");

			}
			%>

			<%
				}
			%>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$('.news-list').each(function() {
			var $list = $(this);
			$list.find('.news-item:gt(4)').hide();

			if ($('.news-list').find('.news-item').length < 5) {
				$('.all-news').hide();
			}
		});

		$('.all-news').click(function(e) {
			e.preventDefault();
			$('.news-list .news-item:gt(4)').slideToggle();
		});
	});
</script>
<jsp:directive.include file="bottom.jsp" />


