<%@ page contentType="text/html;charset=utf-8"%>
 
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

	 //flag che indica se siamo in ambito contestuale o menu generale
	  boolean isContestuale= serviceRequest.containsAttribute("contestuale") && serviceRequest.getAttribute("contestuale").equals("true");
	  
	String cdnLavoratore=(String) serviceRequest.getAttribute("cdnLavoratore");
	

  //********************************************************************************
  //CAMBIA QUI!!!!!!!!
  // NOTE: Attributi della pagina (pulsanti e link) 
  //DA CAMBIARE!!! ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  PageAttribs attributi = new PageAttribs(user, "AnagRicercaPage");
  boolean canInsert = attributi.containsButton("nuovo");
  boolean canDelete=attributi.containsButton("rimuovi");
  //********************************************************************************
    
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 
 //se sono in contestuale, prelevo anche la testata del lavoratore
  InfCorrentiLav infCorrentiLav= null;
	if (isContestuale) {
      infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
    }
    
%>

<html>
<head>
  <title>Lista Dichiarazioni di Sospensione Storiche</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">

   function tornaAllaSchedaAnagrafica(){
   	// Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
   }
 
   
   
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()">
<% if (isContestuale) {
    infCorrentiLav.show(out); 
 }
%>
<center>
<font color="green">
  <af:showMessages prefix="M_AmmDelDichSosp"/>
</font>
<font color="red">
     <af:showErrors/>
</font></center>

<af:list moduleName="M_Amm_GetListaDichSospStorico" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>"  />
<af:form dontValidate="true">
<table border="0" width="100%">
	<tr>
		<td align="center">
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td align="center">
			<input class="pulsante" type = "button" name="Chiudo" value="Chiudi" onclick="window.close()"/>
		</td>
	</tr>
</table>
</af:form>
<br/>
</body>
</html>
