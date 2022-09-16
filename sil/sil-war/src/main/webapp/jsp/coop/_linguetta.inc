<%
String _cdnFunzione = (String)serviceRequest.getAttribute("cdnFunzione");
Linguette _linguette = new Linguette(user, Integer.parseInt(_cdnFunzione), (String)serviceRequest.getAttribute("PAGE"), new BigDecimal(0));
_linguette.show(out); 

%>