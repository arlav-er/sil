<%!
String getQueryString(String qs) {    
	try {
	    StringTokenizer st = new StringTokenizer(qs, "&");
	    StringBuffer sb = new StringBuffer();
	    while (st.hasMoreTokens()) {
	        String par = (String)st.nextToken();
	        int i = par.indexOf("=");
	        if (i<0)continue;
	        sb.append(par.substring(0,i));
	        sb.append("%3D");
	        if (par.length()>i+1)
	            sb.append(par.substring(i+1));
	        sb.append("%26");
	    }
	    return sb.toString();
    }catch(Throwable t) {return null;}
}
%>


<script language="JavaScript">
 <%
	if ((queryString==null) || (queryString.length() == 0))
		queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
 %> 

    var HTTPrequest = "<%=getQueryString(queryString)%>";  





function visualizzaDocumento(rptAction,parametri, prgDocumento) { 

  var urlDoc = "AdapterHTTP?";
  urlDoc += "PAGE=REPORTFRAMEPAGE";
  urlDoc += "&ACTION_REDIRECT="+rptAction;
  urlDoc += parametri;
  urlDoc += "&prgDocumento="+prgDocumento;
  urlDoc +="&apriFileBlob=true";
  urlDoc += "&QUERY_STRING="+HTTPrequest;

  document.location=urlDoc;
}
</script>

