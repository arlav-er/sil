<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs, 
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  com.engiweb.framework.error.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.text.DateFormat,
                  java.text.SimpleDateFormat,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  java.math.*" %>
                
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	    
  	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  	
  	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
  	String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
  	String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
  	String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");
  	String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
  	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");  	 	  	
  	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");  	
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	
  	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
  	String message = (String) serviceRequest.getAttribute("MESSAGE");
  	String listPage = (String) serviceRequest.getAttribute("OLD_LIST_PAGE");
  	if (("").equals(listPage) || listPage == null) {  		
  		if (("LIST_FIRST").equalsIgnoreCase(message)) {
  			listPage = "1";
  		}
  		else if (("LIST_LAST").equalsIgnoreCase(message)) {
  			listPage = "-1";
  		}
  		else { 
	  		listPage = "1";
	  	}
  	}  
	
	// POPUP EVIDENZE
	PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),"ASMatchDettGraduatoriaPage");    
	int _funzione=0;
	_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
	String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
	int _fun = 1;
	if(_funzione>0) { _fun = _funzione; }
	EvidenzePopUp jsEvid = null;
	boolean bevid = pageAtts.containsButton("EVIDENZE");
	if(strApriEv.equals("1") && bevid) {
		jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
	}
	  	
%>


<script>
	
	function indietro(){     
		<%
	    if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=ASStoricoDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
  		%>  			  			
	 		window.location="AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&MESSAGE=<%=message%>&LIST_PAGE=<%=listPage%>"; 	
	 	<%
	 	}
	 	%>
	}       

</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">


<%     
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
%>
	
	<af:list moduleName="M_GetASListaAdesioniLavoratoreView" skipNavigationButton="1"/>

<br>       



</body>
</html>