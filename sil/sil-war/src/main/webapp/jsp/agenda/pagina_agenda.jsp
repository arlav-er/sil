<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.security.*,
    it.eng.afExt.utils.*,
    it.eng.sil.util.*,
    it.eng.sil.security.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>
<%
    //String FormatoData(Timestamp data_grezza){
    //  return data_grezza.
    //}

    String MODULE_NAME="SELECT_DETTAGLIO_AGENDA";

    Calendar cal=Calendar.getInstance();
    String DataOggi= cal.get(Calendar.DATE) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);

    String mode=(String)serviceResponse.getAttribute(MODULE_NAME + ".MODULE_MODE");
    String message="";
    String pulsante="";

    //out.println("MODULE_MODE " + MODULE_MODE);

    /*if (MODULE_MODE.equalsIgnoreCase("INSERT")){
        message="DETAIL_INSERT";
        pulsante="INSERISCI";
    }
    else{*/
        message="DETAIL_UPDATE";
        pulsante="MODIFICA";        
    //}

    String errori=(String) serviceResponse.getAttribute(MODULE_NAME + ".ESITO");

    String CODCPI=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CODCPI");
    BigDecimal PRGAPPUNTAMENTO=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGAPPUNTAMENTO");
    Timestamp DTMDATAORA=(Timestamp) serviceResponse.getAttribute(MODULE_NAME + ".ROW.DTMDATAORA");
    BigDecimal NUMMINUTI=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.NUMMINUTI");
    String CODSERVIZIO=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CODSERVIZIO");
    BigDecimal PRGSPI=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGSPI");
    CLOB TXTNOTE=(CLOB) serviceResponse.getAttribute(MODULE_NAME + ".ROW.TXTNOTE");
    BigDecimal PRGTIPOPRENOTAZIONE=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGTIPOPRENOTAZIONE");
    String STRTELRIF=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.STRTELRIF");
    String STREMAILRIF=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.STREMAILRIF");
    String STRTELMOBILERIF=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.STRTELMOBILERIF");
    String CODEFFETTOAPPUNT=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CODEFFETTOAPPUNT");
    String CODESITOAPPUNT=(String) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CODESITOAPPUNT");
    BigDecimal CDNUTMOD=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CDNUTMOD");
    Timestamp DTMMOD=(Timestamp) serviceResponse.getAttribute(MODULE_NAME + ".ROW.DTMMOD");
    BigDecimal CDNUTINS=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.CDNUTINS");
    Timestamp DTMINS=(Timestamp) serviceResponse.getAttribute(MODULE_NAME + ".ROW.DTMINS");
    BigDecimal PRGEVENTOAZIENDA=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGEVENTOAZIENDA");    
    BigDecimal NUMKLOAGENDA=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.NUMKLOAGENDA");        
    BigDecimal PRGAZIENDA=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGAZIENDA");    
    BigDecimal PRGUNITA=(BigDecimal) serviceResponse.getAttribute(MODULE_NAME + ".ROW.PRGUNITA");
%>
<html>
<HEAD>
  <af:linkScript path="../../js/" />
</HEAD>
<body onload="checkError();">
<af:error />

<af:form action="AdapterHTTP?PAGE=UPDATE_AGENDA_PAGE" method="POST" dontValidate="true">
<input type="hidden" name="NUMKLOAGENDA" value="<%=NUMKLOAGENDA%>">
<table>
<tr>
  <td>
    <%if (errori!=null) {
        out.println("Si sono verificati i seguenti errori nella insert");
        out.println(errori);        
      }
    %>
  </td>
</tr>

<tr>
  <td>Codice CPI</td>
  <%if (CODCPI!=null){%>
    <td><input type="text" name="CODCPI" value="<%=CODCPI%>"></td>
  <%}else{%>
    <td><input type="text" name="CODCPI" value="1111"></td>  
  <%}%>
</tr>
<tr>
  <td>Progressivo appuntamento</td>
    <td><input type="text" name="PRGAPPUNTAMENTO" value="<%=PRGAPPUNTAMENTO%>"></td>
</tr>
<tr>
  <td>Data appuntamento</td>
  <%if (PRGAPPUNTAMENTO!=null){%>
    <td><input type="text" name="DTMDATAORA" value="<%=DTMDATAORA%>"></td>
   <%}else{%>
    <td><input type="text" name="DTMDATAORA" value="<%=DataOggi%>"></td>
   <%}%>
</tr>
<tr>
  <td>Durata appuntamento</td>
   <%if (NUMMINUTI!=null){%>
    <td><input type="text" name="NUMMINUTI" value="<%=NUMMINUTI%>"></td>
   <%}else{%>
    <td><input type="text" name="NUMMINUTI" value="15"></td>
   <%}%>   
</tr>
<tr>
  <td>Codice servizio</td>
   <%if (CODSERVIZIO!=null){%>  
    <td><input type="text" name="CODSERVIZIO" value="<%=CODSERVIZIO%>"></td>
   <%}else{%>
    <td><input type="text" name="CODSERVIZIO" value="PRESEL"></td>
   <%}%>    
</tr>
<tr>
  <td>Progressivo SPI</td>
   <%if (PRGSPI!=null){%>  
    <td><input type="text" name="PRGSPI" value="<%=PRGSPI%>"></td>
   <%}else{%>
    <td><input type="text" name="PRGSPI" value="1"></td>
   <%}%>      
</tr>
<tr>
  <td>Area Note</td>
  <td><textarea><%=TXTNOTE %></textarea></td>
</tr>
<tr>
  <td>Tipo di prenotazione</td>
  <%if (PRGTIPOPRENOTAZIONE!=null){%> 
    <td><input type="text" name="PRGTIPOPRENOTAZIONE" value="<%=PRGTIPOPRENOTAZIONE%>"></td>
   <%}else{%>
    <td><input type="text" name="PRGTIPOPRENOTAZIONE" value="1"></td>
   <%}%>          
</tr>
<tr>
  <td>Telefono</td>
  <%if (STRTELRIF!=null){%> 
    <td><input type="text" name="STRTELRIF" value="<%=STRTELRIF%>"></td>
   <%}else{%>
    <td><input type="text" name="STRTELRIF" value="1234"></td>
   <%}%>      
</tr>
<tr>
  <td>E-mail</td>
  <%if (STREMAILRIF!=null){%> 
    <td><input type="text" name="STREMAILRIF" value="<%=STREMAILRIF%>"></td>
   <%}else{%>
    <td><input type="text" name="STREMAILRIF" value="a@a.a"></td>
   <%}%>   
</tr>
<tr>
  <td>Cellulare</td>
  <%if (STRTELMOBILERIF!=null){%> 
    <td><input type="text" name="STRTELMOBILERIF" value="<%=STRTELMOBILERIF%>"></td>
   <%}else{%>
    <td><input type="text" name="STRTELMOBILERIF" value="3456"></td>
   <%}%>     
</tr>
<tr>
  <td>Effetto appuntamento</td>
  <%if (CODEFFETTOAPPUNT!=null){%> 
    <td><input type="text" name="CODEFFETTOAPPUNT" value="<%=CODEFFETTOAPPUNT%>"></td>
   <%}else{%>
    <td><input type="text" name="CODEFFETTOAPPUNT" value="SOLDI"></td>
   <%}%>       
</tr>
<tr>
  <td>Esito appuntamento</td>
  <%if (CODESITOAPPUNT!=null){%> 
    <td><input type="text" name="CODESITOAPPUNT" value="<%=CODESITOAPPUNT%>"></td>
   <%}else{%>
    <td><input type="text" name="CODESITOAPPUNT" value="BENE"></td>
   <%}%>         
</tr>
<tr>
  <td>Codice utente modificatore</td>
  <%if (CDNUTMOD!=null){%> 
    <td><input type="text" name="CDNUTMOD" value="<%=CDNUTMOD%>"></td>
   <%}else{%>
    <td><input type="text" name="CDNUTMOD" value="1"></td>
   <%}%>
</tr>
<tr>
  <td>Data modifica</td>
  <%if (DTMMOD!=null){%> 
    <td><input type="text" name="DTMMOD" value="<%=DTMMOD%>"></td>
   <%}else{%>
    <td><input type="text" name="DTMMOD" value="<%=DataOggi%>"></td>
   <%}%>
</tr>
<tr>
  <td>Codice utente inseritore</td>
  <%if (CDNUTINS!=null){%> 
    <td><input type="text" name="CDNUTINS" value="<%=CDNUTINS%>"></td>
   <%}else{%>
    <td><input type="text" name="CDNUTINS" value="1"></td>
   <%}%>
</tr>
<tr>
  <td>Data inserimento</td>
  <%if (DTMINS!=null){%> 
    <td><input type="text" name="DTMINS" value="<%=DTMINS%>"></td>
   <%}else{%>
    <td><input type="text" name="DTMINS" value="<%=DataOggi%>"></td>
   <%}%>
</tr>
<tr>
  <td>Progressivo evento azienda</td>
  <%if (PRGEVENTOAZIENDA!=null){%> 
    <td><input type="text" name="PRGEVENTOAZIENDA" value="<%=PRGEVENTOAZIENDA%>"></td>
   <%}else{%>
    <td><input type="text" name="PRGEVENTOAZIENDA" value=""></td>
   <%}%>
</tr>
<tr>
  <td>Progressivo azienda</td>
  <%if (PRGAZIENDA!=null){%> 
    <td><input type="text" name="PRGAZIENDA" value="<%=PRGAZIENDA%>"></td>
   <%}else{%>
    <td><input type="text" name="PRGAZIENDA" value="1"></td>
   <%}%>
</tr>
<tr>
  <td>Progressivo unità</td>
  <%if (PRGUNITA!=null){%> 
    <td><input type="text" name="PRGUNITA" value="<%=PRGUNITA%>"></td>
   <%}else{%>
    <td><input type="text" name="PRGUNITA" value="1"></td>
   <%}%>
</tr>

<tr>
  <td colspan="2"><input type="submit" name="INVIO" value="<%=pulsante%>"></td>
</tr>
</table>
</af:form>
<!--
-->
<%
  //String resp=(String)serviceResponse.getAttribute("AGENDA.MODULE_MODE");
  //out.println("CODICE"+ codcpi);
%>
</body>
</html>
