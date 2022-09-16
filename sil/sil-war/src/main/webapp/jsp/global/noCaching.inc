<%  // *** noCaching.inc ***
	//DISABILITAZIONE CACHE
	response.setHeader("Cache-Control","no-cache");  //HTTP 1.1
	response.setHeader("Pragma","no-cache"); 	//HTTP 1.0
    response.setHeader("cache-control","private");
	response.setDateHeader ("Expires", -1); 					 //prevents caching at the proxy server
%>