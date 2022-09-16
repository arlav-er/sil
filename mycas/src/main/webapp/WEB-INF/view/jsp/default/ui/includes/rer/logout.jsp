<jsp:directive.include file="top.jsp" />




<div id="msg" class="success"
	style="margin-right: 20em; margin-top: 3em;">

	<b><spring:message code="screen.logout.header" /></b>
	<p>
		<a href="<%=myportalAddress%>">Riaccedi al Portale</a>
	</p>
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


<jsp:directive.include file="bottom.jsp" />


