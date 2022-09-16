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

//Dati Programmi
String     strtitolo         = StringUtils.getAttributeStrNotNull(serviceRequest, "strtitolo");
String     strnote           = StringUtils.getAttributeStrNotNull(serviceRequest, "strnote");
String     strcodiceesterno  = StringUtils.getAttributeStrNotNull(serviceRequest, "strcodiceesterno");
String     datinizio         = StringUtils.getAttributeStrNotNull(serviceRequest, "datinizio");
String     datfine           = StringUtils.getAttributeStrNotNull(serviceRequest, "datfine");
String     codstatoprogramma = StringUtils.getAttributeStrNotNull(serviceRequest, "codstatoprogramma");
String     prgprogrammaq      = StringUtils.getAttributeStrNotNull(serviceRequest, "prgprogrammaq");
String     strRagSocAzienda  = StringUtils.getAttributeStrNotNull(serviceRequest, "STRRAGIONESOCIALE");

String     infoProg =   strtitolo; 

%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
	<af:linkScript path="../../js/" />

    <!--Crea e popola l'oggetto che consente di recuperare i dati della ricerca-->
    <script type="text/javascript">
    var datacontainer = new Object();
    window.dati = datacontainer;
    datacontainer.strtitolo = "<%=strtitolo%>";
    datacontainer.strnote = "<%=strnote%>";

    datacontainer.strcodiceesterno = "<%=strcodiceesterno%>";
    datacontainer.datinizio = "<%=datinizio%>";
    datacontainer.datfine = "<%=datfine%>";
    datacontainer.codstatoprogramma = "<%=codstatoprogramma%>";
    datacontainer.prgprogrammaq = "<%=prgprogrammaq%>";  
    datacontainer.infoProg = "<%=infoProg%>";
    datacontainer.strRagSocAzienda = "<%=strRagSocAzienda%>";
 
    
    //Controlla che la funzione di aggiornamento da chiamare esista nella pagina sottostante,
    //altrimenti non fa nulla

    function richiamaAggiornamento() {
    	if (window.opener != null && window.opener.aggiornaProgramma != null) {
    		window.opener.aggiornaProgramma();
    	} else window.close();
    }
    </script>
</head>

<body class="gestione" onload="javascript:richiamaAggiornamento();">

</body>
</html>