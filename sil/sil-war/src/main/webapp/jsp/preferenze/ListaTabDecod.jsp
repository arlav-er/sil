<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.ga.db.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String tableName       = (String) serviceRequest.getAttribute("TABLE_NAME");
  String showKeysStr     = (String) serviceResponse.getAttribute("LISTATABDECOD.SHOW_KEYS");
  String skipCommentsStr = (String) serviceResponse.getAttribute("LISTATABDECOD.SKIP_COMMENTS");
  //String errore = (String) serviceResponse.getAttribute("CANCELLATABDECOD.ERROR");

  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");

  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

  
  //Profilatura ------------------------------------------------
  //PageAttribs attributi = new PageAttribs(user, "ListaTabDecodPage");
  String _page = (String) serviceRequest.getAttribute("PAGE");
  String tab = (String) serviceRequest.getAttribute("TABLE_NAME");
  PageAttribs attributi = new PageAttribs(user, _page  + "&TABLE_NAME=" + tableName + "&SHOW_KEYS=" +showKeysStr + "&SKIP_COMMENTS=" + skipCommentsStr);
  boolean canModify= attributi.containsButton("INSERISCI");
  boolean canDelete= attributi.containsButton("CANCELLA");
  //boolean canAggiorna= attributi.containsButton("AGGIORNA");
  
  String labelButton="";
  if(tableName.equalsIgnoreCase("DE_GIORNALE_PUBB")){
  	labelButton = "Nuovo giornale";
  }
  else if(tableName.equalsIgnoreCase("DE_TIPO_MODELLO")){
  	labelButton = "Nuovo tipo modello";
  }
  else if(tableName.equalsIgnoreCase("DE_OGGETTO_MODELLO")){
  	labelButton = "Nuovo oggetto";
  }
  else{
  	labelButton= "Nuovo";
  } 
		
%>


<html>
	<head>
		<title>Selezione tabella </title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
     <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>
 <script language="javascript">
      function nuovo(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
      	if (isInSubmit()) return;
      
      	var _url ="AdapterHTTP?PAGE=DettaglioTabDecodPage&NUOVO=true";
      	_url +="&TABLE_NAME=<%=tableName%>";
      	_url +="&cdnFunzione=<%=cdnFunzione%>";
      	_url +="&SKIP_COMMENTS=<%=skipCommentsStr%>";
      	_url +="&SHOW_KEYS=<%=showKeysStr%>";
      
        setWindowLocation(_url);
      }
  </script>
	</head>
	<body class="gestione" onload="rinfresca()">
    <br/>
  <%if(serviceResponse.containsAttribute("CANCELLATABDECOD")){%>
    <p align="center">
      <%out.print(htmlStreamTop);%>
      <table class="main">
        <%--<tr><td class="etichetta">Impossibilie cancellare il record</td>
            <td class="campo"><strong><%=errore%></strong></td>
        </tr> --%>
	    <tr><td>
	        <font color="green"><af:showMessages prefix="CANCELLATABDECOD"/></font>
	        <font color="red"><af:showErrors /></font>
	    </td></tr>
      </table>
      <%out.print(htmlStreamBottom);%>
    </p>
  <%}%>
  <af:form dontValidate="true">

  <af:list moduleName="ListaTabDecod" configProviderClass="it.eng.sil.module.preferenze.DynamicTabDecodConfig" 
           canDelete="<%= canDelete ? \"1\" : \"0\" %>"/>

  <%if (canModify) {%>
    <center><input class="pulsante" type="button" VALUE="<%=labelButton%>" onClick="nuovo()"/></center>
  <%}%>
	<p>&nbsp;</p>
	
  </af:form>
		
</body>
</html>

