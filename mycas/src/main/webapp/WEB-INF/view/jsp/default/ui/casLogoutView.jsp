<jsp:directive.include file="includes/top.jsp" />

<%
	String regione = System.getProperty("regione");
	String myportalAddress = System.getProperty("myportal.address");
	String myaccountAddress = System.getProperty("myaccount.address");
	String socialeAddress = System.getProperty("sociale.address");
	String sareAddress = System.getProperty("sare.address");

	boolean isRER = "er".equalsIgnoreCase(regione);
	boolean isUmbria = "umbria".equalsIgnoreCase(regione);
	boolean isTn = "pat".equalsIgnoreCase(regione);
	boolean isVda = "vda".equalsIgnoreCase(regione);
	boolean isCalabria = "calabria".equalsIgnoreCase(regione);
	boolean isPuglia = "puglia".equalsIgnoreCase(regione);

	boolean isSociale = request.getParameter("res") != null || request.getParameter("sociale") != null;
%>

<script type="text/javascript" src="<c:url value="/js/jquery-3.0.0.min.js" />"></script>

<%
	if (isSociale) {
%>
<jsp:directive.include file="includes/sociale/logout.jsp" />
<%
	} else if (isRER) {
%>
<jsp:directive.include file="includes/rer/logout.jsp" />
<%
	} else if (isUmbria) {
%>
<jsp:directive.include file="includes/umbria/logout.jsp" />
<%
	} else if (isTn) {
%>
<jsp:directive.include file="includes/trentino/logout.jsp" />
<%
	} else if (isVda) {
%>
<jsp:directive.include file="includes/vda/logout.jsp" />
<%
	} else if (isCalabria) {
%>
<jsp:directive.include file="includes/calabria/logout.jsp" />
<%
	} else if (isPuglia) {
%>
<jsp:directive.include file="includes/puglia/logout.jsp" />
<%
	}
%>






