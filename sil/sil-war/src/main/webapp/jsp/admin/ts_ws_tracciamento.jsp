<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.security.PageAttribs,
  java.math.*,
  it.eng.sil.security.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
String _page = (String) serviceRequest.getAttribute("PAGE"); 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>

 <script type="text/Javascript">
 
  
  function viewRecordDetail(prgwstracciamento) {
	//funzionalità disattivata (per il momento)
	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
	url="AdapterHTTP?Page=WS_TRACCIAMENTO_RECORD_PAGE";
  	url+="&prgwstracciamento="+prgwstracciamento;
  	
  	var w=800; var l=((screen.availWidth)-w)/1.2;
    var h=600; var t=((screen.availHeight)-h)/1.2;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var titolo = "_blank";
  	var opened = window.open(url, titolo, feat);
  	opened.focus();
  }
  
 </script>

<title>Lista TS_WS_TRACCIAMENTO</title>
</head>


<%

		String prgWsTracciamento = StringUtils.getAttributeStrNotNull(serviceRequest,"prgwstracciamento");
		String strOperazione = StringUtils.getAttributeStrNotNull(serviceRequest,"STROPERAZIONE");
		String strUrl = StringUtils.getAttributeStrNotNull(serviceRequest,"strurl");
		String strVerso = StringUtils.getAttributeStrNotNull(serviceRequest,"STRVERSO");
		String strTipo = StringUtils.getAttributeStrNotNull(serviceRequest,"strtipo");
		String dataDa = StringUtils.getAttributeStrNotNull(serviceRequest,"datada");
		String dataA = StringUtils.getAttributeStrNotNull(serviceRequest,"dataa");
		String testoC = StringUtils.getAttributeStrNotNull(serviceRequest,"testo");
		

%>

<body class="gestione">
<p class="titolo">Risultati della ricerca su TS_WS_TRACCIAMENTO</p>
<af:form>
	<af:list  moduleName="LISTA_TS_WS_TRACCIAMENTO" jsSelect="viewRecordDetail" skipNavigationButton="false" />
	
	<input type="hidden" name="PAGE" value="WS_TRACCIAMENTO_RICERCA_PAGE"></input>
	<input type="hidden" name="prgwstracciamento" value="<%=prgWsTracciamento%>"></input>
	<input type="hidden" name="stroperazione" value="<%=strOperazione%>"></input>
	<input type="hidden" name="strurl" value="<%=strUrl%>"></input>
	<input type="hidden" name="strverso" value="<%=strVerso%>"></input>
	<input type="hidden" name="strtipo" value="<%=strTipo%>"></input>
	<input type="hidden" name="datada" value="<%=dataDa%>"></input>
	<input type="hidden" name="dataa" value="<%=dataA%>"></input>
	<input type="hidden" name="testo" value="<%=testoC%>"></input>
	
	<hr/>
	<center>
	<input type="submit" value="Torna alla ricerca"></input>
	</center>
	
</af:form>		
</body>
</html>