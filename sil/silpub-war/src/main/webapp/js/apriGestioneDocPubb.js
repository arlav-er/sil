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

function apriGestioneReportPubb(rptAction,parametri,tipoDoc)
{
  

  var urlo = "AdapterHTTP?PAGE=GestioneReportPubbPage";
  urlo += "&rptAction="+rptAction;
  urlo += parametri;
  urlo +="&tipoDoc="+tipoDoc;
  urlo += "&QUERY_STRING="+HTTPrequest;

  var titolo = "gestioneDoc";
  var w=750; var l=((screen.availWidth)-w)/2;
  var h=300; var t=((screen.availHeight)-h)/2;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=NO,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  
  window.open(urlo, titolo, feat);
}

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

