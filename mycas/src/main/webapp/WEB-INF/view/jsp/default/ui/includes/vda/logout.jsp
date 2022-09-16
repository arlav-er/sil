<jsp:directive.include file="top.jsp" />

<div class="panel-heading cas-heading bg-pacman-icon">
	<div class="title">Arrivederci</div>
</div>
<div class="container panel-body cas-body-vda">

	<div class="row">
		<div class="col-md-3">
			<img src="/MyCas/images/vda/Aosta.png" alt="Logo" style="margin: 10px;" border="0" class="hidden-xs img-responsive">

		</div>

		<div class="col-md-9">
			

<div id="msg" class="success"
	style="margin-right: 20em; margin-top: 3em;">

	<b><spring:message code="screen.logout.header" /></b>
	<p>
		<a href="<%=myportalAddress%>">Riaccedi al Portale</a>
	</p>
</div>



<div id="msg" class="success"
	style="margin-right: 20em; margin-top: 3em;">

	Per eseguire il logout anche dalla infrastruttura regionale, premere <a
		href="<%=myportalAddress%>/../IcarInf3/LogoutInitiatorServlet"> qui<br />
					<img src="/MyCas/images/vda/immagine_tessera_sanitaria.jpg"
						style="margin: 20px; padding: 3px; border: 2px solid #e3e3e3;" class="hidden-xs img-responsive">
			</a><br>


</div>


	
		</div>

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
	
</script>

<jsp:directive.include file="bottom.jsp" />


