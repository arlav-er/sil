<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

  String _page = (String) serviceRequest.getAttribute("PAGE");
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);

  String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  
  String  prgVerbaleAcc = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGVERBALEACC");

  String  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");  
  InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  testata.setSkipLista(true);
  testata.show(out);
  
  boolean canInsert = false;
  boolean canDelete = false;
  
  
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
 
	  //Profilatura ------------------------------------------------
	  PageAttribs attributi = new PageAttribs(user, _page);
	  
	  canInsert= attributi.containsButton("INSERISCI");
	  canDelete= attributi.containsButton("CANCELLA");
 
			
	  //Servono per gestire il layout grafico
	  String htmlStreamTop = StyleUtils.roundTopTable(true);
	  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>



<html>
<head>
    <title>CMlistaVerAcc.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>


<script language="JavaScript">
	
	function doDelete(page,prgVerbaleAcc){
		var t="Sicuri di voler rimuovere questo verbale? ";
   		if ( confirm(t) ) {
  			var s = "AdapterHTTP?PAGE=CMListaVerAccPage";
      			s = s + "&PRGVERBALEACC="+prgVerbaleAcc;
      			s = s + "&cdnLavoratore=<%=cdnLavoratore%>";
      			s = s + "&cdnFunzione=<%=cdnFunzione%>";
      			s += "&elimina=1";
      			setWindowLocation(s);
			}
		}

</script>

</head>

<body class="gestione" onload="rinfresca()">

<center>
	<font color="green">
		<af:showMessages prefix="M_Delete_Verbale"/>			
	</font>    
</center>
<center>
	<font color="red"><af:showErrors /></font>	
</center>

<p align="center">


<af:form name="Frm1" method="POST" action="AdapterHTTP">

<input type="hidden" name="PAGE" value="CMVerbaleAccDettPage"/>
<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="nuovoVerbale" value="1"/>


<af:list moduleName="M_GetVerAcc" jsDelete="doDelete" canDelete="<%=canDelete ? \"1\" : \"0\"%>"  />

<%if (canInsert) {%>
	<center><input type="submit" class="pulsanti" name="Nuovo" value="Nuovo Verbale" /></center>
<%}%>

</af:form>
</body>
</html>
<% } %>
