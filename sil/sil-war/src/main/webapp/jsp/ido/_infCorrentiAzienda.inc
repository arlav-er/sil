  <%
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita, prgRichiestaAz );
  String pagina_back = (String)sessionContainer.getAttribute("PAGE_RIC_BACK_TO_LIST");
  if (pagina_back!=null ) {  	
  	infCorrentiAzienda.setPaginaLista(pagina_back);
  }
  else 
  	infCorrentiAzienda.setSkipLista(true);
  %>