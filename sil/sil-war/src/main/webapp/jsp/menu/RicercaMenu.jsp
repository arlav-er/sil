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
   String htmlStreamTop = StyleUtils.roundTopTable(false);   
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
   String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
%>



<html>
	<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
	  <script language="javascript">
	      function nuovo(){
	      	// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
	      
	        var url = "AdapterHTTP?PAGE=DettaglioMenuPage&NUOVO=Y&cdnfunzione=<%=cdnFunzione%>";
	        setWindowLocation(url);
	      }
	  </script>    
	</head>
	<body class="gestione" onload="rinfresca()">
  <br/>
	<p class="titolo">Ricerca menu</p>
	
			<af:form method="POST" action="AdapterHTTP">
      <af:textBox type="hidden" name="PAGE" value="ListaMenuPage"/>
      <af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>

      <p align="center">
      <%out.print(htmlStreamTop);%>
      <table class="main">
      
        <tr>
          <td class="etichetta">
            Descrizione
          </td>
          <td class="campo">
            <af:textBox name="strdescrizione"  title="Descrizione" size="50" maxlength="100"/>
          </td>
        </tr>
        
        <%
             // GESTIONE ATTRIBUTI....
        //     String _page = (String) serviceRequest.getAttribute("PAGE");
        //     PageAttribs attributi = new PageAttribs(user, _page);
        
        %>
        
        
       <tr>
            <td colspan="2">
             &nbsp;
            </td>
        </tr>
      
          <tr>
            <td>
             &nbsp;
            </td>
          <td nowrap class="campo">
                <input class="pulsante" type="submit" VALUE="Cerca" />
                &nbsp;&nbsp;
                <input class="pulsante" type="reset"  VALUE="Annulla" />
          </td>
        </tr>
          
         <tr>
            <td>
             &nbsp;
            </td>
        </tr>

	    <tr>
          <td>
           &nbsp;
          </td>    
	      <td nowrap class="campo">
	        <% //if (attributi.containsButton("nuovo")){ %>
	            <input class="pulsante" type="button" VALUE="Nuovo menu" onClick="nuovo()"/>
	        <% //} %>
	      </td>
	    </tr>
        
      </table>

      <%out.print(htmlStreamBottom);%>
      </af:form>
		
	</body>
</html>

