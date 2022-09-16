<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	    
  	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  	String prgNominativo = (String) serviceRequest.getAttribute("prgNominativo");
  	
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
  	PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));    
%>


<script>  
	
	function indietro(){   
	    <%
	    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=CMStoricoDettGraduatoriaPage&MODULE=CMStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
	    %>  
	 		window.location="AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&MESSAGE=<%=message%>&LIST_PAGE=<%=listPage%>";
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
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">


<%     
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
%>
	
	<af:list moduleName="CMDettaglioPunteggioLavoratoreModule" skipNavigationButton="1"/>

<br>       



</body>
</html>