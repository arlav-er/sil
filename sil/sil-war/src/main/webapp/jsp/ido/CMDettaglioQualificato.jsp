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
		
	String pagina = "";
	
	pagina = "CMMatchDettGraduatoriaPage"; 
	
	
	Vector rows = serviceResponse.getAttributeAsVector("CMGestQualificatoModule.ROWS.ROW");
	row = (SourceBean) rows.elementAt(0); 
	
	dtmIns           = "" +          Utils.notNull(row.getAttribute("DTMINS"));         
	dtmMod           = "" +          Utils.notNull(row.getAttribute("DTMMODCM"));         
	cdnUtIns         = (BigDecimal)  row.getAttribute("CDNUTINS");         
	cdnUtMod         = (BigDecimal)  row.getAttribute("CDNUTMODCM");
	
	String cdnQualificato = row.getAttribute("CDNQUALIFICATO").toString();
	
	String prgTipoRosa = serviceRequest.getAttribute("PRGTIPOROSA").toString();
	
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
      <af:showMessages prefix="CMSalvaQualificatoModule"/>
    </font>
  </center>
  <center>
    <font color="red"><af:showErrors /></font>
  </center>
  <p class="titolo">Qualificato</p>
<%= htmlStreamTop %>
<table align="center">
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta">Qualifica</td>
		<td class="campo">
			<af:comboBox name="CDNQUALIFICATO"
	                     size="1"
	                     title="Qualifica"
	                     multiple="false"
	                     required="false"
	                     focusOn="false"
	                     moduleName="CM_GET_DE_QUALIFICATO"
	                     addBlank="true"
	                     blankValue=""					                     				                    
	                     classNameBase="input"	
	                     selectedValue="<%= cdnQualificato%>"				                    
	        />			
		</td>
	</tr>	
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</table>
	<input type="hidden" name="PAGE" value="CMQualificatoPage">
	<input type="hidden" name="MODULE" value="CMSalvaQualificatoModule">		
    <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>	
    <input type="hidden" name="prgNominativo" value="<%= prgNominativo %>"/>
    <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
    <input type="hidden" name="prgTipoRosa" value="<%= prgTipoRosa %>"/>    
    
  
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
