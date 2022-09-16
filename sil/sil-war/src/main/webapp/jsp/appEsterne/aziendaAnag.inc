<%
	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, _page);
%>

<html>

	<head>
	  <title>Corsi di studio</title>
	
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  <af:linkScript path="../../js/"/>
	  
	  <script language="Javascript">
	     window.top.menu.caricaMenuAzienda(<%=cdnFunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	  </script>
	  
	  <script language="Javascript">
	  <!--Contiene il javascript che si occupa di aggiornare i link del footer-->
	  <% 
	       //Genera il Javascript che si occuperà di inserire i links nel footer
	       attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
	  %>
	  </script>	
	</head>
	
	<body class="gestione" onload="rinfresca();">
	  <%
	  	
		String url= (String) serviceResponse.getAttribute("APPESTERNE.ROWS.ROW.strurl");
		BigDecimal idUser = (BigDecimal)sessionContainer.getAttribute("_CDUT_");
	
		url+="?PRGAZIENDA=" + prgAzienda.toString() + "&PRGUNITA=" + prgUnita + "&OP=" +idUser;	
	  %>
		
	  <p align="center">
	  <iFRAME src="<%=url%>" height="100%" width="100%" frameborder=0></iFRAME>
	</body>

</html>
