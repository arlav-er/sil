
<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 

%>

<html>

<head>
  <title>Corsi di studio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>

</head>
	<body class="gestione" onload="rinfresca();">
	
	  <%
		int funzione = Integer.parseInt((String) cdnFunzione);
		Linguette l = new Linguette( user, funzione , _page + "&ID=" + ID, new BigDecimal(cdnLavoratore));
	    InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
	    infCorrentiLav.show(out); 
	    l.show(out);

		String url= (String) serviceResponse.getAttribute("APPESTERNE.ROWS.ROW.strurl");
		BigDecimal idUser = (BigDecimal)sessionContainer.getAttribute("_CDUT_");

		url+="?CDF=" +cdnLavoratore.toString() + "&OP=" +idUser; 	
	  %>
	
	<p align="center">
	<iFRAME src="<%=url%>" height="47%" width="96%"  frameborder=0></iFRAME>
	
	</body>
</html>
