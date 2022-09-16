<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.*,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ taglib uri="aftags" prefix="af"%>
<%  
  String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
  String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
  String cdnLavoratore = serviceRequest.getAttribute("cdnLavoratore").toString();
  String codTipoMov = serviceRequest.getAttribute("CODTIPOMOV").toString();
  String funzAgg = (String)serviceRequest.getAttribute("FUNZ_GEST");
  String contesto = (String)serviceRequest.getAttribute("CONTESTO");
  boolean inserisci = false;
  boolean valida = false;
  if(contesto.equals("inserisci")){
    inserisci = true;
  }
  if(contesto.startsWith("valida")){
    valida = true;
  }
  //Variabili di appoggio
  boolean consulta = false;
  String pageAvviamento ="";    
  String pageTrasfPro ="";
  String pageCessazione ="";
%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>

<af:linkScript path="../../js/" />

<!-- Gestione profilatura -->
<%@ include file="common/include/_gestioneProfili.inc" %>
<script language="javascript">
  function aggiorna(){
      window.opener.document.Frm1.permettiImpatti.value = '<%=canEditLav%>';
      window.opener.document.Frm1.CANVIEW.value = '<%=canView%>';
      window.opener.document.Frm1.CANEDITLAV.value = '<%=canEditLav%>';
      window.opener.document.Frm1.CANEDITAZ.value = '<%=canEditAz%>';
      window.close();
      window.opener.<%=funzAgg%>('<%=codTipoMov%>');
  }
</script>
</head>
<body class="gestione" onload="javascript:gestioneProfilo('<%=codTipoMov%>');aggiorna();">

</body>
</html>