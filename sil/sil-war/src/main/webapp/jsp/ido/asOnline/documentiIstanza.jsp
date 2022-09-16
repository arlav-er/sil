<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*, java.math.BigDecimal,
                it.eng.sil.security.*" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
      
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	PageAttribs attributi = new PageAttribs(user,_page);

	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String provenienza = (String) serviceRequest.getAttribute("PROVENIENZA");
	 
	 
	String idIstanza = null;
	String numProtIstanza = null;
	String protIstanza = null;
	 
	BigDecimal prgNominativo = null;
	String prgRichiestaAz = (String) serviceRequest.getAttribute("PRGRICHIESTAAZ");
	String rosaDefinitiva = null;
	 
	String candidato = serviceResponse.getAttribute("M_DocumentiIstanzaLav.candidato").toString();
//	SourceBean richiestaRow = (SourceBean) serviceResponse.getAttribute("M_DocumentiIstanzaLav.DOCUMENTI");
 
	 
	String strErrorCode = "";
	String msgConferma = "";
	boolean confirm = false;
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
	String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
	String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
	String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");

	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
	String message = (String) serviceRequest.getAttribute("MESSAGE");
	String listPage = (String) serviceRequest.getAttribute("OLD_LIST_PAGE");
	if (("").equals(listPage) || listPage == null) {
		if (("LIST_FIRST").equalsIgnoreCase(message)) {
			listPage = "1";
		} else if (("LIST_LAST").equalsIgnoreCase(message)) {
			listPage = "-1";
		} else {
			listPage = "1";
		}
	}

	String indietro = "";
	if (("ASListaAdesioniPage").equalsIgnoreCase(p_page)) {
		indietro = "AdapterHTTP?PAGE=ASListaAdesioniPage&CDNFUNZIONE="
				+ cdnFunzione + "&cdnLavoratore=" + cdnLavoratore
				+ "&LIST_PAGE=1";
	} else if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
		indietro = "AdapterHTTP?PAGE=ASStoricoDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria&PRGROSA="
				+ prgRosa
				+ "&PRGTIPOROSA="
				+ prgTipoRosa
				+ "&PRGTIPOINCROCIO="
				+ prgTipoIncrocio
				+ "&PRGRICHIESTAAZ="
				+ prgRichiestaAz
				+ "&PRGAZIENDA="
				+ prgAzienda
				+ "&PRGUNITA="
				+ prgUnita
				+ "&CDNFUNZIONE="
				+ cdnFunzione
				+ "&MESSAGE="
				+ message
				+ "&LIST_PAGE=" + listPage;
	} else {
		indietro = "AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria&PRGROSA="
				+ prgRosa
				+ "&PRGTIPOROSA="
				+ prgTipoRosa
				+ "&PRGTIPOINCROCIO="
				+ prgTipoIncrocio
				+ "&PRGRICHIESTAAZ="
				+ prgRichiestaAz
				+ "&PRGAZIENDA="
				+ prgAzienda
				+ "&PRGUNITA="
				+ prgUnita
				+ "&CDNFUNZIONE="
				+ cdnFunzione
				+ "&MESSAGE="
				+ message
				+ "&LIST_PAGE=" + listPage;
	} 
%>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
  <script>
 
	function indietro(){  	
	 
		window.location="<%=indietro%>"; 	

		
	}
 
</script>
 
 
</head>

<body onload="rinfresca()">

<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
 
<br/>
 
<p class="titolo">Documenti Istanza <%=candidato %></p>
 
	<p>
	 	<font color="green">
	 		<%-- <af:showMessages prefix="M_UltimaNotificaRDC"/> --%>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
 <%out.print(htmlStreamTop);%>
 
	 
    </center>
<br>       
 
<%out.print(htmlStreamBottom);%>
</body>
</html>