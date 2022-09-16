<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String strTitolo = "";
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strScadenziarioCig = (String) serviceRequest.getAttribute("SCADENZIARIO");
int nCodScadenza = 0;
if (strScadenziarioCig.compareTo("CIG1") == 0) {
  nCodScadenza = 1;
}
else {
  if (strScadenziarioCig.compareTo("CIG2") == 0) {
    nCodScadenza = 2;
  }
  else {
    if (strScadenziarioCig.compareTo("CIG3") == 0) {
      nCodScadenza = 3;
    }
    else {  
      if (strScadenziarioCig.compareTo("CIG4") == 0) {
        nCodScadenza = 4;
      }
   	}
  }
}

switch (nCodScadenza) {
  case 1:
 	strTitolo = "Lavoratori prima presa in carico";
    break;
  case 2:
    strTitolo = "Mancata presentazione CIG/MB in Deroga";
    break;
  case 3:
    strTitolo = "Due esiti negativi per la stessa azione CIG/MB in Deroga";
    break;
  case 4:
    strTitolo = "Assenza corsi";
    break;
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript">
</script>
</head>

<body class="gestione">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="">
<p class="titolo"><%=strTitolo%></p>
<%out.print(htmlStreamTop);%>
<p align="center">
<table class="main"> 
<tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" />
    </td>
  </tr>
<%
switch (nCodScadenza) {
	case 1:
%>
	<tr>
    	<td class="etichetta">Data iscrizione prima del</td>
    	<td class="campo">
      		<af:textBox type="date" name="DATINIZIOISCR" title="Data iscrizione prima del" value="" 
      			  		size="12" maxlength="10" validateOnPost="true" 
      			  		required="false"/>&nbsp;&nbsp;
     	</td>
    </tr>

<%
    break;
   	case 2:
    %>
    <tr>
    	<td class="etichetta">Data di mancata presentazione</td>
    	<td class="campo">
      		<af:textBox type="date" name="DATSTIMATA" title="Data di mancata presentazione" 
      			  		value="" size="12" maxlength="10" validateOnPost="true" 
      			  		required="false"/>&nbsp;&nbsp;
    	</td>
    </tr>
    <tr>
    	<td class="etichetta">Esiti</td>
    	<td class="campo">
    		<af:comboBox name="CODESITO" size="1" title="Esiti"
                 		 disabled="false" required="true" moduleName="M_GetEsitoCig" addBlank="true" />
    	</td>
    </tr>
    
  <%
    break;
    
  case 3:
  %>
    <tr>
    	<td class="etichetta">Azione</td>
    	<td class="campo" nowrap>
    		<af:comboBox name="CODAZIONE" size="1" title="Azione"
                 		 disabled="false" required="true" moduleName="M_GetAzioniCig" addBlank="true" />
    	</td>
    </tr>
  <%
    break;
    
  case 4:
  %>
    <tr>
    	<td class="etichetta">Esiti</td>
    	<td class="campo">
    		<af:comboBox name="CODESITO" size="1" title="Esiti"
                 		 disabled="false" required="true" moduleName="M_GetEsitoCig" addBlank="true" />
    	</td>
    </tr>
  
  <%
    break; 
  } %>

</table>
<br>
<center>
	<table align="center">
		<tr>
			<td align="center">
				<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
				<input type="reset" class="pulsanti" value="Annulla"/>
			</td>
		</tr>
</table>
<%out.print(htmlStreamBottom);%>

<% switch (nCodScadenza) {
   	case 1:
%>
	<table>
		<tr>
			<td class="campo2">Seleziona i lavoratori CIG/MB in Deroga attivi, con data inizio precedente a quella indicata come parametro, per i quali non è stata fatta nessuna presa in carico (sui colloqui) 
								e per i quali ad oggi non risulta prevista nessuna azione di presa in carico con data successiva ad oggi.
			</td>
		</tr>	    
	</table>
<%
    break; 
   	case 2:
%>
	<table width="100%">
	<tr><td colspan=3 style="campo2">Seleziona i lavoratori CIG/MB in Deroga attivi, a seconda del parametro selezionato si avranno liste di lavoratori selezionati con le seguenti regole:</td></tr>
    <tr><td colspan=3 style="campo2">MANCATA PRESENTAZIONE 1: Lista di lavoratori in CIG/MB in Deroga che non si sono presentati alla prima convocazione per la "presa in carico" con esito "mancata presentazione 1". 
							   		 Sono esclusi dalla rilevazione quelli che hanno una successiva presa in carico con esito positivo o con data stimata successiva o uguale ad oggi.</td></tr>
    <tr><td colspan=3 style="campo2">MANCATA PRESENTAZIONE 2: Lista di lavoratori in CIG/MB in Deroga che non si sono presentati alla seconda  convocazione per la "presa in carico" con esito "mancata presentazione 2". 
							   		 Sono esclusi dalla rilevazione quelli che hanno una successiva presa in carico con esito positivo o con data stimata successiva o uguale ad oggi.</td></tr>
    <tr><td colspan=3 style="campo2">ASSENZA GIUSTIFICATA: Lista dei lavoratori in CIG/MB in Deroga che vanno riconttatati perchè l'esito dell'ultima azione è di "assenza giustificata".</td></tr>
  </table>

<% break; 

} %>

</center>

<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="PAGE" value="ScadListaCigPage"/>
<input type="hidden" name="PAGEPROVENIENZA" value="ScadListaCigPage">
<input type="hidden" name="SCADENZIARIO" value="<%=strScadenziarioCig%>"/>
</af:form>
</body>
</html>
