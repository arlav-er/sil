<%

 /*
 * stampa a video l' intestazione delle informazioni utente e 
 * della linguetta di navigazione
 */
String _cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
User _user = (User) sessionContainer.getAttribute(User.USERID);
String _cdnFun = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String) serviceRequest.getAttribute("PAGE"); 
    
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer() ,_cdnLavoratore, _user);
_testata.setPaginaLista("LISTADICHREDPAGE");
 
Linguette _linguetta = new Linguette(_user, Integer.parseInt(_cdnFun), _page, new BigDecimal(_cdnLavoratore));   

_testata.show(out);
_linguetta.show(out);

_cdnLavoratore = null; 
_user=null;
_cdnFun=null;
_page = null;
_testata=null;
_linguetta=null;

%>
