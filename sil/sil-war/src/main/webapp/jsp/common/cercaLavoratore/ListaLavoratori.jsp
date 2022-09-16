<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

	//prelevo i parametri di ricerca
	String strCodiceFiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
    String strCognome= StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
    String strNome= StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
    String datnasc=StringUtils.getAttributeStrNotNull(serviceRequest, "datnasc");
    String codComNas=StringUtils.getAttributeStrNotNull(serviceRequest, "codComNas");
    String strComNas=StringUtils.getAttributeStrNotNull(serviceRequest, "strComNas");
    String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");


  //********************************************************************************
  //CAMBIA QUI!!!!!!!!
  // NOTE: Attributi della pagina (pulsanti e link) 
  //DA CAMBIARE!!! ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  PageAttribs attributi = new PageAttribs(user, "AnagRicercaPage");
  boolean canInsert = attributi.containsButton("nuovo");
  boolean canDelete=attributi.containsButton("rimuovi");
  //********************************************************************************
    
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 //prelevo dalla request le funzioni di aggiornamento
 String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");   
 String funzioneChiudi = (String) StringUtils.getAttributeStrNotNull(serviceRequest,"CHIUDI_FUNZ");
 
 
%>

<html>
<head>
  <title>Lista lavoratori</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">

   var isSelected=false;
 
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=CommonCercaLavoratorePage";
     url += "&CDNFUNZIONE="+"<%//=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&datnasc="+"<%=datnasc%>";
     url += "&codComNas="+"<%=codComNas%>";     
	 url += "&strComNas="+"<%=strComNas%>"; 
	 url += "&tipoRicerca="+"<%=tipoRicerca%>";         
     url += "&AGG_FUNZ="+"<%=funzioneaggiornamento%>";
     setWindowLocation(url);
  }
  
   function go(cdnLavoratore){
   	isSelected=true;
   window.opener.<%=funzioneaggiornamento%>(cdnLavoratore);
   window.close();
   }
   
   
   
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()" <%=((funzioneChiudi!=null) && (!(funzioneChiudi.equals("")))?"onUnload=\"if (!isSelected) {window.opener."+funzioneChiudi+"();}\"":"")%>>
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>
<af:form dontValidate="true">

<af:list moduleName="M_CommonCercaLavoratore" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>" jsSelect="go" />

<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
</af:form>
<br/>
</body>
</html>
