<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  	String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
	
    String _page = "ProfVisibilitaXMLPage"; 
    
    String cdnGruppoStr = (String) serviceRequest.getAttribute("cdnGruppo"); 
    BigDecimal  cdnGruppo= new BigDecimal(cdnGruppoStr);

    int cdnfunz = new Integer ( cdnfunzione).intValue();
    
  	Linguette l = new Linguette(user, cdnfunz , _page, cdnGruppo);
   
    l.setCodiceItem("CDNGRUPPO");


   String htmlStreamTop    = StyleUtils.roundTopTable(false);   
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
%>



<html>
	<head>
  <title>Esito modifica visibilta di un gruppo</title>
  <!-- nomeFILE:  ../jsp/profil/ProfVisibGrpXMLSalva.jsp -->
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	    <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>


   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;        
  </script>
  
	</head>
	<body class="gestione" onLoad="rinfresca()">
  
      <% l.show(out); %>
      <br/><br/>
      
	    <p class="titolo">Esito della modifica della visibilit&agrave</p>
      
      <p align="center">
      <%out.print(htmlStreamTop);%>
      <table class="main">
      <tr><td>
        <font color="green"><af:showMessages prefix="ProfVisibilitaXMLSalva"/></font>
        <font color="red"><af:showErrors /></font>
      </td></tr>
      <tr><td><a href="AdapterHTTP?PAGE=ProfVisibilitaXMLPage&CDNGRUPPO=<%=cdnGruppoStr%>&CDNFUNZIONE=<%=cdnfunzione%>&MODE=VIEW">
              <img id="immIndietro" src="../../img/indietro.gif" alt="Torna alla modifica della visibilit&agrave" align="absmiddle" />
              Torna alla modifica della visibilit&agrave
              </a>
      </table>
      <%out.print(htmlStreamBottom);%>

	</body>
</html>

