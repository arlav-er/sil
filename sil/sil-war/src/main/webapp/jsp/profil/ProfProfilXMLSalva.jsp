<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.tracing.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  oracle.xdb.XMLType,
			      javax.xml.transform.*,
				  javax.xml.transform.stream.*
	" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

	String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
	String cdnprofilo =  (String) serviceRequest.getAttribute("cdnprofilo");
	
    int cdnFunzione = new Integer ( cdnfunzione).intValue();
    String _page = "ProfProfilaturaXMLPage"; 
 
	 // Attributi della pagina (pulsanti e link) 
     PageAttribs attributi = new PageAttribs(user, _page);
     boolean canSave=attributi.containsButton("salva");


    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);
	
   


     Linguette  l = new Linguette(user, cdnFunzione , _page, new BigDecimal(cdnprofilo));
	   
     l.setCodiceItem("cdnprofilo");

	
%>



<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

 	 <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
            flagChanged = false;
        }
    </script>
	
</head>	
<body class="gestione" onLoad="rinfresca()">

 <%@ include file="./testataProfilo.inc" %>
 <% l.show(out); %>

      <font color="green"><af:showMessages prefix="PROFPROFILXMLSALVA"/></font>
	    <font color="red"><af:showErrors /></font>
	    
	    <br/>
	    
	    <table class="main"> <tr><td>
			 <a href="AdapterHTTP?PAGE=ProfProfilaturaXMLPage&cdnprofilo=<%=cdnprofilo%>&CDNFUNZIONE=<%=cdnfunzione%>">
              <img id="immIndietro" src="../../img/indietro.gif" alt="Torna alla modifica degli attributi" align="absmiddle" />
              Torna alla modifica degli attributi 
              </a>
         </td></tr></table>     
</form>
</body>
</html>

