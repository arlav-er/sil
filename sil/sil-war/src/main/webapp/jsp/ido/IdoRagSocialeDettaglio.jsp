<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*
                  "%>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
    String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
    String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");


    String moduleName = "M_GetTestataAzienda";
    String pageName = "RagSocialeDettaglioPage";
    SourceBean row = (SourceBean) serviceResponse.getAttribute(moduleName+".ROWS.ROW");

    BigDecimal cdnUtins = (BigDecimal) row.getAttribute("cdnUtins");
    String dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
    BigDecimal cdnUtmod = (BigDecimal) row.getAttribute("cdnUtmod");
    String dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
    Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
    BigDecimal klo = (BigDecimal)row.getAttribute("NUMKLOAZIENDA");
    String numKloAzienda = String.valueOf(klo.intValue()+1);
    String dataCambio = ""; //DateUtils.getNow();
    String dataComunic = ""; //DateUtils.getNow();
    String strRagioneSociale = StringUtils.getAttributeStrNotNull(row, "STRRAGIONESOCIALE");
    String strRagioneSocialeNew = "";
    String strHistory = StringUtils.getAttributeStrNotNull(row, "STRHISTORY");
    String dataCambioNew = StringUtils.getAttributeStrNotNull(row, "datCambioRagSoc");  



    ProfileDataFilter filter = new ProfileDataFilter(user, pageName);
    boolean canView = false;
	if (!prgUnita.equals("")) {
	  filter.setPrgAzienda(new BigDecimal(prgAzienda));
	  filter.setPrgUnita(new BigDecimal(prgUnita));
	  canView=filter.canViewUnitaAzienda();
	} else {
	  canView = true; // sono in inserimento, la visibilità dei dati non ha senso
	}
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    PageAttribs attributi = new PageAttribs(user, pageName);
    //boolean canModify   = attributi.containsButton("aggiorna");
    boolean canModify = true;
    boolean canInsert = true;
    if ( !canModify && !canInsert ) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
        canModify = false;
        canInsert = false;
      }
    }
    String readOnlyString = String.valueOf(!canModify);
    
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

    //}
    

%>
<html>
<head>
<title>Cambio ragione sociale</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>

<af:linkScript path="../../js/"/>

<script language="JavaScript">
	// Rilevazione Modifiche da parte dell'utente
	var flagChanged = false;

	function fieldChanged() {
 	<% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
    	flagChanged = true;
 	<%}%> 
	}
	
</script>

</head>


<body  class="gestione" onload="rinfresca()">

<br>
<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_UpdateRagSociale"/>
</font>
<center><p class="titolo">Cambio ragione sociale</p></center>
<br><br>
<center>
<af:form name="frm" action="AdapterHTTP" method="post" >

<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>">
<input type="hidden" name="prgUnita" value="<%=prgUnita%>">
<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>">
<input type="hidden" name="PAGE" value="<%=pageName%>">
<input type="hidden" name="numKloAzienda" value="<%=numKloAzienda%>">
<input type="hidden" name="strHistory" value="<%=strHistory%>">
<input type="hidden" name="dataCambioNew" value="<%=dataCambioNew%>">

<%out.print(htmlStreamTop);%> 
<table class="main">
    <tr valign="top">
      <td class="etichetta">Attuale ragione sociale </td>
      <td class="campo">
        <af:textBox classNameBase="input" name="strRagioneSociale" size="50" value="<%=strRagioneSociale%>" readonly="true" />
      </td>
    </tr>
    <tr valign="top">
      <td class="etichetta">Nuova ragione sociale</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRagioneSocialeNew" title="Nuova ragione sociale" size="50" value="<%=strRagioneSocialeNew%>" readonly="<%= String.valueOf(!canModify) %>" required="true" />
      </td>
    </tr>

    <tr valign="top">
        <td class="etichetta">Data comunicazione</td>
        <td class="campo">
            <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="date" title="Data comunicazione" name="dataComunic" value="<%=dataComunic%>" validateOnPost="true" required="true"
                          readonly="<%=String.valueOf(!canInsert)%>"  size="11" maxlength="11"/>
        </td>
    </tr>
    <tr valign="top">
        <td class="etichetta">Data cambio ragione sociale</td>
        <td class="campo">
            <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="date" title="Data cambio ragione sociale" name="dataCambio" value="<%=dataCambio%>" validateOnPost="true" required="true"
                          readonly="<%=String.valueOf(!canInsert)%>"  size="11" maxlength="11"/>
        </td>
    </tr>

</table>

<table>
  <tr>
	<td colspan="2" align="center">
	    <input type="submit" class="pulsanti" name="salva" value="Aggiorna">
	    <input type="button" class="pulsanti" value="Annulla" onClick="window.close();">
	</td>
  </tr>
</table>
<%out.print(htmlStreamBottom);
  if (serviceResponse.containsAttribute("M_UPDATERAGSOCIALE")) {%>
<SCRIPT TYPE="text/javascript">
<!--
  window.opener.document.Frm1.numKloAzienda.value = document.frm.numKloAzienda.value;
  window.opener.document.Frm1.strRagioneSociale.value = document.frm.strRagioneSociale.value;
  window.opener.document.Frm1.datCambioRagSoc.value = document.frm.dataCambioNew.value;    
  window.opener.document.Frm1.strHistory.value = document.frm.strHistory.value;
  window.close();
-->
</SCRIPT>
<%}%>

<table>
  <tr><td align="center">
	<%if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
  </td></tr>
</table>

</af:form>
</center>
</body>
</html>

<% } %>