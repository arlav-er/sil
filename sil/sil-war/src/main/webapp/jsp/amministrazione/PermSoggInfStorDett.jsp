<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
   Vector permSoggiornoRows = serviceResponse.getAttributeAsVector("M_GETSPECIFPERMSOGG.ROWS.ROW");
   SourceBean row_exPermSogg  = null;
   String cdnLavoratore   = null;
   String datScad         = null;
   String dataFine        = null;
   String datRichiesta    = null;
   String codMotivoRil    = null;
   String codStatus   	  = null;
   String codStatoRic     = null;
   String strNumDocumento  = null;
   String notePermSogg    = null;
   String dtmIns          = null;
   String dtmMod          = null;
   BigDecimal cdnUtIns    = null;
   BigDecimal cdnUtMod    = null;
   BigDecimal keyLock     = null;
   BigDecimal progres     = null;
   boolean flag_insert    = false;
   boolean readOnlyStr    = false;
   Testata operatoreInfo = null;   

   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);

   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");  
   InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setSkipLista(true);
   testata.setMaxLenStatoOcc(80);
    
     if(serviceRequest.containsAttribute("newRecord"))
     {flag_insert = true;}
     else
     { if( permSoggiornoRows != null && !permSoggiornoRows.isEmpty()) 
       { row_exPermSogg  = (SourceBean) permSoggiornoRows.elementAt(0);
         progres         = (BigDecimal) row_exPermSogg.getAttribute("PRGPERMSOGG");
         datScad         = (String)     row_exPermSogg.getAttribute("DATSCADENZA");
         dataFine        = (String)     row_exPermSogg.getAttribute("DATFINE");
         datRichiesta    = (String)     row_exPermSogg.getAttribute("DATRICHIESTA");
         codMotivoRil    = (String)     row_exPermSogg.getAttribute("CODMOTIVORIL");
         codStatus    	 = (String)     row_exPermSogg.getAttribute("CODSTATUS");
         codStatoRic     = (String)     row_exPermSogg.getAttribute("CODSTATORICHIESTA");
         notePermSogg    = (String)     row_exPermSogg.getAttribute("STRNOTE");
         dtmIns          = (String)     row_exPermSogg.getAttribute("DTMINS");
         dtmMod          = (String)     row_exPermSogg.getAttribute("DTMMOD");
         cdnUtIns        = (BigDecimal) row_exPermSogg.getAttribute("CDNUTINS");
         cdnUtMod        = (BigDecimal) row_exPermSogg.getAttribute("CDNUTMOD");
         keyLock         = (BigDecimal) row_exPermSogg.getAttribute("NUMKLOPERMSOGG");
         strNumDocumento = (String) row_exPermSogg.getAttribute("strNumDocumento");
       }//if
       else
       { flag_insert = true;
       }
   }//else
   
   String display0 = (datScad!=null && datScad.length()>0) ? "inline":"none";
   String img0 = (datScad!=null && datScad.length()>0) ? " ../../img/aperto.gif": " ../../img/chiuso.gif";

   String display1 = "inline";
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   
   // NOTE: Attributi della pagina (pulsanti e link) 
   PageAttribs attributi = new PageAttribs(user, "AmministrPermSoggPage");
   readOnlyStr = true;

%>
<html>
<head>
<title>Inf. storiche Cittadini Stranieri</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>
<script>
<%@ include file="../patto/_sezioneDinamica_script.inc" %>
</script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 98%;
	left: 1%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
</head>
<body class="gestione"> <!-- onLoad="javascript:showIfsetVar()" -->
<% 
 testata.show(out);
%>
 <font color="red">
     <af:showErrors/>
 </font>

<font color="green">
 <af:showMessages prefix="M_SavePermSoggiorno"/>
 <af:showMessages prefix="M_InsertPermSoggiorno"/>
</font>

<br/>
<af:form name="form1" method="POST" action="AdapterHTTP">
<p align="center">
<table class="main">
<tr><td colspan="2"><center><p class="titolo">Informazioni storiche relative i cittadini stranieri</p></center></td></tr>
<tr><td><br/></td></tr>
</table>
<%out.print(htmlStreamTop);%>
<%@ include file="PermSoggCampiLayOut.inc"%>
<%out.print(htmlStreamBottom);%>
<br>
<table class="main">
<tr>
<td align="center">
  <input class="pulsante" type="button" name="lista" value="Torna alla lista"
       onClick="checkChange('PermSoggInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"/>
</td>
</tr>
</table>
<br>
<center>
  <% operatoreInfo.showHTML(out); %>
</center>


  <input type="hidden" name="PAGE" value="PermSoggInfStorDettPage"/>
  <input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>"/>
  <input type="hidden" name="numKloPermSogg" value="<%=Utils.notNull(keyLock)%>"/>
  <input type="hidden" name="prgPermSogg" value="<%=Utils.notNull(progres)%>"/>
    
</af:form>

</body>
</html>
