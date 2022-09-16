<jsp:directive.include file="top.jsp" />

<section id="logout">
	<div class="section-content container-fluid">
		<div class="col-md-12">
			<p class="title">
				<spring:message code="screen.logout.header" />
			</p>
			<div class="info">
				<a href="<%=myportalAddress%>">Riaccedi al Portale</a>
			</div>
		</div>
		<div id="accesso-icar" class="col-md-12 info">                                    
			<b>
				<a href="/trentinolavoro/ICAR/Login?loginTicket=${loginTicket}&flowExecutionKey=${flowExecutionKey}">
					Clicca qui per accedere ai servizi on line del sistema pubblico trentino
				</a>
			</b>
		</div>
		<div class="col-md-12 info">
			Per eventuali problemi di accesso
			<a href="mailto:help-portale.adl@provincia.tn.it">help-portale.adl@provincia.tn.it</a>
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
	
</script>


<jsp:directive.include file="bottom.jsp" />


