<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
				java.util.*,
				it.eng.sil.security.*,
				it.eng.sil.util.*,
				it.eng.afExt.utils.*,
				java.math.*" %>
				
				
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%	
	String     dtmIns             = null; 
    String     dtmMod             = null;    
    BigDecimal cdnUtIns           = null; 
    BigDecimal cdnUtMod           = null;
    SourceBean row				  = null;
    
    String prgNominativo = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
	String daGraduatoria = StringUtils.getAttributeStrNotNull(serviceRequest,"daGraduatoria");
	String pagina = "";
	
	if(daGraduatoria.equals("")) pagina = "CMListaAdesioniPage";
		else pagina = "CMMatchDettGraduatoriaPage"; 
	
	
	Vector rows = serviceResponse.getAttributeAsVector("M_GETPRIORITA.ROWS.ROW");
	row = (SourceBean) rows.elementAt(0); 
	
	dtmIns           = "" +          Utils.notNull(row.getAttribute("DTMINS"));         
	dtmMod           = "" +          Utils.notNull(row.getAttribute("DTMMODCM"));         
	cdnUtIns         = (BigDecimal)  row.getAttribute("CDNUTINS");         
	cdnUtMod         = (BigDecimal)  row.getAttribute("CDNUTMODCM");
	
	String strPriorita = (String)row.getAttribute("STRPRIORITA");
	String strNota = (String)row.getAttribute("STRNOTA");
	String prgTipoRosa = row.getAttribute("PRGTIPOROSA").toString();
	String numklonominativo = row.getAttribute("NUMKLONOMINATIVO").toString();
	boolean canModify = prgTipoRosa.equals("2")? true : false;
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>

<html>
<head>
<title>Dettaglio adesione</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<SCRIPT TYPE="text/javascript">
<!--
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  

  function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
  }

 function goBack() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
  	if (isInSubmit()) return;
	ok=true;
  	if (flagChanged) {
    	if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
        	ok=false;
     	}
  	}
  	if (ok) {
  		<% String token = "_TOKEN_" + pagina;
	   	   String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
		}
	}
</SCRIPT>

<af:linkScript path="../../js/"/>
    
</head>
<body class="gestione" onLoad="rinfresca();">

<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>

<br/>
<af:form name="form1" method="POST" action="AdapterHTTP">
  <center>
    <font color="green">
      <af:showMessages prefix="M_UpdatePriorita"/>
    </font>
  </center>
  <center>
    <font color="red"><af:showErrors /></font>
  </center>
  <p class="titolo">Priorità/Nota adesione</p>
<%= htmlStreamTop %>
<table align="center">
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta">Priorità</td>
		<td class="campo">
			<af:textBox required="true" classNameBase="input" onKeyUp="fieldChanged();" name="strPriorita" title="Priorità" size="25" value="<%=strPriorita%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" />
		</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta">Nota</td>
		<td class="campo">
			<af:textArea required="false" classNameBase="textarea" onKeyUp="CheckLen(100,'strNota' );fieldChanged();" title="Nota" name="strNota" cols="25" value="<%=strNota%>" readonly="<%= String.valueOf(!canModify) %>"/>
		</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</table>
	<input type="hidden" name="PAGE" value="CMPrioritaPage">
	<input type="hidden" name="UPDATE_PRIORITA" value="">
    <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
	<input type="hidden" name="prgNominativo" value="<%= prgNominativo %>"/>
    <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
    <input type="hidden" name="NUMKLONOMINATIVO" value="<%= numklonominativo %>"/>
    <%if(pagina.equals("CMMatchDettGraduatoriaPage")) { %>
    	<input type="hidden" name="daGraduatoria" value="daGraduatoria"/>
    <%}%>

<%if (canModify) { %>
	 <input class="pulsante" type="submit" name="aggiorna" value="Aggiorna"/>
<%}%>
     <input class="pulsante" type="button" name="annulla" value="Torna alla lista" onclick="goBack();">     

<%= htmlStreamBottom %>

<table align="center">
  <tr>
  	<td colspan="2" align="center">
  		<% if (operatoreInfo!=null) operatoreInfo.showHTML(out); %>
    </td>
  </tr>    
</table>

</af:form>

</body>
</html>
