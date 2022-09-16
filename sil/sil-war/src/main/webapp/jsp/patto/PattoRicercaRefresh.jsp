<!-- @author: Paolo Roccetti -->
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ taglib uri="aftags" prefix="af"%>

<%  
    //prelevo i dati della ricerca
    String funzioneaggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
    String codiceSede = serviceRequest.containsAttribute("ENTECODSEDE")?serviceRequest.getAttribute("ENTECODSEDE").toString():"";
    String ragioneSociale = serviceRequest.containsAttribute("ENTERAGSOC")?serviceRequest.getAttribute("ENTERAGSOC").toString():"";
    String codiceFiscaleAzienda = serviceRequest.containsAttribute("ENTECF")?serviceRequest.getAttribute("ENTECF").toString():"";
    String strIndirizzoAzienda = serviceRequest.containsAttribute("ENTEINDIRIZZO")?serviceRequest.getAttribute("ENTEINDIRIZZO").toString():"";
    String strTelAzienda = serviceRequest.containsAttribute("ENTETEL")?serviceRequest.getAttribute("ENTETEL").toString():"";
    String comuneAzienda = serviceRequest.containsAttribute("ENTECOMUNE")?serviceRequest.getAttribute("ENTECOMUNE").toString():"";
    String provAzienda = serviceRequest.containsAttribute("ENTETARGA")?serviceRequest.getAttribute("ENTETARGA").toString():"";
    comuneAzienda = comuneAzienda + " (" + provAzienda + ")";

%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
	<af:linkScript path="../../js/" />

    <!--Crea e popola l'oggetto che consente di recuperare i dati della ricerca-->
    <script type="text/javascript">
    	var datacontainer = new Object();
   		window.dati = datacontainer;
   	 	datacontainer.funzioneaggiornamento = "<%=funzioneaggiornamento%>";
 
        datacontainer.ragioneSociale = "<%=StringUtils.replace(ragioneSociale,"\"","\\\"")%>";
        datacontainer.codiceFiscaleEnte = "<%=codiceFiscaleAzienda%>";
        datacontainer.strIndirizzoEnte = "<%=strIndirizzoAzienda%>";
        datacontainer.strTelEnte = "<%=strTelAzienda%>";
        datacontainer.comuneEnte = "<%=comuneAzienda%>";
        datacontainer.codSoggAcc = "<%=codiceSede%>";
 
    //Controlla che la funzione di aggiornamento da chiamare esista nella pagina sottostante,
    //altrimenti non fa nulla
    function richiamaAggiornamento() {
    	if (window.opener != null && window.opener.<%=funzioneaggiornamento%> != null) {
    		window.opener.<%=funzioneaggiornamento%>();    		
    	} else window.close();
    }
    </script>
</head>

<body class="gestione" onload="javascript:richiamaAggiornamento();">

</body>
</html>