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
    //Cerco il nome del modulo che contiene i dati della combo
    String modulename = StringUtils.getAttributeStrNotNull(serviceRequest, "COMBOMODULENAME");

    //Cerco il nome della combo da riempire
    String comboname = StringUtils.getAttributeStrNotNull(serviceRequest, "COMBONAME");
    
    //Estraggo le opzioni per la combo
    AbstractList options = (AbstractList) serviceResponse.getAttributeAsVector( modulename + ".ROWS.ROW");  
    Iterator i = null;
    boolean emptyCombo = true;
    if (options != null) {
      emptyCombo = false;
      i = options.iterator();  
    }
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
	<af:linkScript path="../../js/" />

    <!--Aggiorna la combo specificata dal chiamante-->
    <script type="text/javascript">
<!--
    function aggiornaCombo() {
      //Ripulisco la combo finchè non sono sicuro che è vuota
      var optionvecchie = 1;
      while (optionvecchie != 0) {
      	optionvecchie = window.opener.clearCombo("<%=comboname%>");
      }

      //Aggiungo l'elemento blank
      window.opener.addOption("<%=comboname%>", 0, "", "", true, true);
<%  
  int counter = 1;
  while ((emptyCombo == false) && i.hasNext()) {
  SourceBean option = (SourceBean) i.next();
%>   
      window.opener.addOption("<%=comboname%>", <%=counter%>, "<%=option.getAttribute("descrizione")%>", "<%=option.getAttribute("codice")%>", false, false);
<%  
  counter = counter + 1;
  }
%>
      window.close();
    }
    -->
    </script>
</head>
<body class="gestione" onload="aggiornaCombo();">
</body>
</html>

