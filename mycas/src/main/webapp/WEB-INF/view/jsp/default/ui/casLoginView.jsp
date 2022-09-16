<%@page import="java.net.URL"%>
<jsp:directive.include file="includes/top.jsp" />
<%
	String regione = System.getProperty("regione");

	boolean isRER = "er".equalsIgnoreCase(regione);
	boolean isUmbria = "umbria".equalsIgnoreCase(regione);
	boolean isTn = "pat".equalsIgnoreCase(regione);
	boolean isVda = "vda".equalsIgnoreCase(regione);
	boolean isCalabria = "calabria".equalsIgnoreCase(regione);
	boolean isPuglia = "puglia".equalsIgnoreCase(regione);

	boolean isSociale = request.getParameter("res") != null || request.getParameter("sociale") != null;
	request.setAttribute("from_social", request.getParameter("autosubmit"));
%>

<c:if test="${not empty from_social}">
	<jsp:directive.include file="includes/from_social.jsp" />
</c:if>

<c:if test="${empty from_social}">

	<%
		boolean isFacebookEnabled = "true".equalsIgnoreCase(System.getProperty("social.facebook"));
			boolean isTwitterEnabled = "true".equalsIgnoreCase(System.getProperty("social.twitter"));
			boolean isGoogleEnabled = "true".equalsIgnoreCase(System.getProperty("social.google"));

			boolean areSocialNetworksEnabled = isFacebookEnabled || isTwitterEnabled || isGoogleEnabled;

			String federaAddress = System.getProperty("auth.federa.address");
			boolean isFederaEnabled = false;
			if (federaAddress != null) {
				isFederaEnabled = !"".equalsIgnoreCase(federaAddress);
			}

			String myportalAddress = System.getProperty("myportal.address");
			String myaccountAddress = System.getProperty("myaccount.address");
	%>

	<%
		if (isSociale) {
	%>
	<jsp:directive.include file="includes/sociale/login.jsp" />
	<%
		} else if (isRER) {
	%>
	<jsp:directive.include file="includes/rer/login.jsp" />
	<%
		} else if (isUmbria) {
	%>
	<jsp:directive.include file="includes/umbria/login.jsp" />
	<%
		} else if (isTn) {
	%>
	<jsp:directive.include file="includes/trentino/login.jsp" />
	<%
		} else if (isVda) {
	%>
	<jsp:directive.include file="includes/vda/login.jsp" />
	<%
		} else if (isCalabria) {
	%>
	<jsp:directive.include file="includes/calabria/login.jsp" />

	<%
		} else if (isPuglia) {
	%>
	<jsp:directive.include file="includes/puglia/login.jsp" />
	<%
		}
	%>

</c:if>

