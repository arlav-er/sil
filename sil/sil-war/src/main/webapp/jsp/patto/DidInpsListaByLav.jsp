<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                it.eng.sil.module.movimenti.InfoLavoratore,
                java.math.BigDecimal" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "listaDidInpsPage");
  boolean canDelete=attributi.containsButton("aggiorna");
%>


<html>
<head>
<title>DID INPS</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/"/>
</head>

<body onload="checkError();">
<af:error/>
<%
    BigDecimal cdnLavoratore = (BigDecimal) serviceResponse.getAttribute("M_DidInps_takeLav.ROWS.ROW.CDNLAVORATORE");
    InfoLavoratore _lav = new InfoLavoratore( cdnLavoratore );
    String nomeLav = _lav.getNome(); 
    String cognomeLav = _lav.getCognome(); 
    String codiceFiscale = _lav.getCodFisc();         
%>
    <center><table class="main">
        <tr>
            <td colspan="4"><%@ include file="../amministrazione/_testata_semplice_lavoratore.inc" %>
            </td>
       </tr>
    </table>
<br>
<H2>DID INPS</H2>
<af:list moduleName="M_DidInpsByLav" canDelete="0" />
</center>
<center><button onclick="window.close()" class="pulsanti">Chiudi</button></center>
	
</body>
</html>