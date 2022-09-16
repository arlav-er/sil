<%

 /*****************************************************************
 * Crea l' oggetto degli attributi delle pagine che lo richiedono
 ******************************************************************/
 
PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));


%>