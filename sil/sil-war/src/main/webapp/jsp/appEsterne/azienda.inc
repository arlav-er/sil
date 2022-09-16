<%
	BigDecimal prgAzienda = null;
	BigDecimal prgUnita   = null;
	SourceBean row = (SourceBean) serviceResponse.getAttribute("M_GetAziendaUnita.ROWS.ROW");
	if( row != null ) 
	{ prgAzienda = (BigDecimal) row.getAttribute("PRGAZIENDA");
	   prgUnita   = (BigDecimal) row.getAttribute("PRGUNITA");
    }

	String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
	String _page = (String) serviceRequest.getAttribute("PAGE");
%>

<html>

	<head>
	  <title>Corsi di studio</title>
	
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  <af:linkScript path="../../js/"/>
	  
	  <script language="Javascript">
	     window.top.menu.caricaMenuAzienda(<%=cdnFunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	  </script>	
	</head>
	
	<body class="gestione" onload="rinfresca();">
	  <%
		InfCorrentiAzienda infCorrentiAzienda = null;
		int funzione = Integer.parseInt((String) cdnFunzione);
		
		if( prgAzienda != null && prgUnita!=null ) {
	    	infCorrentiAzienda = new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita, prgRichiestaAz);	    	
	  	}else{
			infCorrentiAzienda = null;
		}
		
		String pagina_back = (String)sessionContainer.getAttribute("PAGE_RIC_BACK_TO_LIST");
	    
	    if(infCorrentiAzienda != null){
		    if (pagina_back!=null ) {  	
	  			infCorrentiAzienda.setPaginaLista(pagina_back);
	  		}else{ 
			  	infCorrentiAzienda.setSkipLista(true);
			}
			infCorrentiAzienda.show(out);
		}
			
		Linguette l = new Linguette( user, funzione , _page + "&ID=" + ID, new BigDecimal(prgRichiestaAz));
	    l.setCodiceItem("PRGRICHIESTAAZ");
		l.show(out);
	
		String url= (String) serviceResponse.getAttribute("APPESTERNE.ROWS.ROW.strurl");
		BigDecimal idUser = (BigDecimal)sessionContainer.getAttribute("_CDUT_");
	
		url+="?CDR=" + prgRichiestaAz.toString() + "&OP=" +idUser;	
	  %>
		
	  <p align="center">
	  <iFRAME src="<%=url%>" height="60%" width="96%"  frameborder=0></iFRAME>
	</body>

</html>
