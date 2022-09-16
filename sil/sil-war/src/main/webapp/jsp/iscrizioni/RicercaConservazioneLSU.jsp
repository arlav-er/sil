<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String _page = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGE"); 

  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }

  String funzione = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnfunzione");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="JavaScript" src="../../js/script_comuni.js"></script>
<script language="JavaScript">
	function controlloDate() {
		var GG_MAX = 365;
    	var datDa = document.form1.datinizioda.value;
		var datA = document.form1.datinizioa.value;
    	var periodoDataIscr = confrontaDate(datDa, datA) + 1;
    	if((confrontaDate(datDa, datA)) < 0 ) {
			alert("Data iscrizione da deve essere minore o uguale a data iscrizione a");
			return false;
		}
    	if(periodoDataIscr <= GG_MAX) {
			return true;				
		}
		else {
			alert("Periodo data iscrizione da... a... con periodo max 365 gg.");
			return false;
		}
  	}
</script>

</head>


<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Conservazione per LSU</p>
<p align="center">
<af:form name="form1" action="AdapterHTTP" method="GET" onSubmit="controlloDate()"> 
  <%out.print(htmlStreamTop);%> 
  <table class="main" border="0">   
    <tr>
		<td class="etichetta" nowrap>Data inizio da</td>
		<td class="campo" >
   		<af:textBox type="date" name="datinizioda" title="Data inizio iscrizione da" validateOnPost="true" size="10" maxlength="10" required="true"/>
		&nbsp;&nbsp;a&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio iscrizione a" validateOnPost="true" size="10" maxlength="10" required="true"/>
		</td>            
 	</tr>
   	
 	<tr>
		<td class="etichetta">CPI competente per il lavoratore</td>
		<td class="campo">
			<af:comboBox classNameBase="input" title="CPI competente per il lavoratore" name="CODCPI" 
				moduleName="M_ELENCOCPI" addBlank="true" />
		</td>
	</tr> 
	
 	<tr><td>&nbsp;</td></tr>
   	<tr><td>&nbsp;</td></tr>
   	<tr>
   		<td colspan="2" align="center">
      		<input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      		&nbsp;&nbsp;
      	<input name="reset" type="reset" class="pulsanti" value="Annulla"/> 
    	</td>
   	</tr>
   	</table>
   	<%out.print(htmlStreamBottom);%>
   	<input type="hidden" name="PAGE" value="ListaConservazioneLSUPage"/>
   	<input type="hidden" name="cdnfunzione" value="<%=funzione%>"/>
</af:form>
</p>
</body>
</html>
