<%
  //Gestione cache per trasferimento ramo aziendale
  NavigationCache cache = (NavigationCache) sessionContainer.getAttribute("TRASFRAMOAZIENDACACHE");
  if (cache != null && serviceRequest.containsAttribute("UPDATETRASFRAMOAZIENDACACHE")) {
	  //Lo aggiorno a partire dalla ServiceRequest
	    cache.setFieldsFromSourceBean(serviceRequest);
  }
%>