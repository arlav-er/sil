<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% Vector rows = serviceResponse.getAttributeAsVector("M_GETINDISPTEMP.ROWS.ROW");

   SourceBean row           = null;
   String cdnLavoratore     = null;
   String descrizione       = null;
   String dataInizio        = null;
   String dataFine          = null;
   String codIndTempLetto   = null;
   String strNote           = null;
   String dtmIns            = null;
   String dtmMod            = null;
   BigDecimal cdnUtMod      = null;
   BigDecimal cdnUtIns      = null;
   BigDecimal kloListeSpec  = null;
   BigDecimal prgIndispTemp = null; 

   
//    String pkLavPatto = null;
    boolean almenoUNO        =false;

    String COD_LST_TAB="AM_IND_T";
    

     int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
     String _page = (String) serviceRequest.getAttribute("PAGE"); 
     cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

   PageAttribs attributi = new PageAttribs(user, "AmstrIndispTempPage");
   boolean readOnlyStr = !attributi.containsButton("AGGIORNA");
   boolean canInsert   =  attributi.containsButton("INSERISCI");
   boolean canDelete   =  attributi.containsButton("RIMUOVI");
   boolean infStorButt =  attributi.containsButton("INF_STOR");
      
%>
<html>
<head>
<title>Amministrazione - Disponibilita Temporanea</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<script  language="JavaScript">

function getFormObj() {return document.form1;}
 <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
 
function ConoscenzaDelete(prgInfo, tipo) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s="Eliminare l\'indiponibilita\' temporanea\ndi tipo " + tipo;
  s += " ?";
  if ( confirm(s) ) {

    var s= "AdapterHTTP?PAGE=AmstrIndispTempPage";
    s += "&delIndispTemp=true";
    s += "&PRGINDISPTEMP=" + prgInfo;
    s += "&CDNLAVORATORE=<%=cdnLavoratore%>";
    s += "&CDNFUNZIONE=<%=_cdnFunz%>";
    s+="&";
    s+=getParametersForPatto(prgInfo);
    setWindowLocation(s);
  }
}

</script>
<%@ include file="CommonScript.inc"%>
</head>


<body class="gestione" onLoad="rinfresca();"> 
<p align="center">
<font color="green"><af:showMessages prefix="M_SaveIndispTemp"/></font>

<af:form dontValidate="true">

<table class="main">
  <%-- <tr><td colspan="2"><center><b>Indisponibiltà temporanee</b></center></td></tr> --%>
    <%
    for(Iterator record = rows.iterator(); record.hasNext(); )
    { almenoUNO = true;
      row  = (SourceBean) record.next();
      prgIndispTemp   = (BigDecimal) row.getAttribute("PRGINDISPTEMP");      
      descrizione     = (String)     row.getAttribute("DESCRIZIONE");
      codIndTempLetto = (String)     row.getAttribute("CODINDISPTEMP");         
      dataInizio      = (String)     row.getAttribute("DATINIZIO");        
      dataFine        = (String)     row.getAttribute("DATFINE");         
      dtmIns          = (String)     row.getAttribute("DTMINS");        
      dtmMod          = (String)     row.getAttribute("DTMMOD");         
      cdnUtIns        = (BigDecimal) row.getAttribute("CDNUTINS");         
      cdnUtMod        = (BigDecimal) row.getAttribute("CDNUTMOD");    
      %>

      <tr>
        <td colspan="2">
          <a href="AdapterHTTP?PAGE=AmstrIndTempDettPage&PRGINDISPTEMP=<%=prgIndispTemp%>&CDNLAVORATORE=<%=cdnLavoratore%>&CDNFUNZIONE=<%=_cdnFunz%>">
          <img name="dettImg" alt="Mostra Indiponibilita Temporanea" src=" ../../img/detail.gif"></a>
          <%if(canDelete){%>
          <a HREF="javascript:ConoscenzaDelete(<%=prgIndispTemp%>, '<%= descrizione %>');">
          <img name="delImg" alt="cancella" src=" ../../img/del.gif"></a>
          <%}%>
          <br/>Tipo Indisp. <b><%=descrizione%></b>
        </td>
      </tr>
      
      <tr>
        <td colspan=2>dal&nbsp;&nbsp;<b><%=dataInizio%></b></td>
        </tr>
        <tr>
        <td colspan=2>al&nbsp;&nbsp;&nbsp;&nbsp;<b><%if (dataFine!=null) out.print(dataFine);%></b></td>
      </tr>
      
      <tr><td colspan="2">
      	<pt:associatoAl row="<%= row %>" key="<%= prgIndispTemp.toString() %>" />
      	</td></tr>
      <tr><td colspan="2" valign="top"><hr/></td>
      </tr>
      <%
    }//for%>
    <%--<tr><td colspan="2" align="center">
      <%if(!almenoUNO){%><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><%}%>
      <%if(infStorButt){%><br/>
        <input type="button" name="infSotriche" class="pulsanti" value="Informazioni storiche"
               onClick="infoStoriche('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
      <%}%>
     </td>
    </tr> --%>
</table>

</af:form>

</body>
</html>
