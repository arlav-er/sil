<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  it.eng.sil.util.amministrazione.impatti.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String datInizioCalcolo = "";
	SourceBean sbConfigDataNormativa = (SourceBean) serviceResponse.getAttribute("M_CONFIG_DATA_NORMATIVA_297.rows.row");
	if (sbConfigDataNormativa != null && sbConfigDataNormativa.containsAttribute("strvalore")) {
		datInizioCalcolo = sbConfigDataNormativa.getAttribute("strvalore").toString();
	}
	else {
		datInizioCalcolo = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
	}
	String msgConferma = "";
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	boolean readOnlyStr = false;
	String strCalcola = serviceRequest.containsAttribute("CALCOLA")? serviceRequest.getAttribute("CALCOLA").toString():"";
	String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
	String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
	boolean cancellaSOccManuale = serviceRequest.containsAttribute("CANCELLA_STATO_OCC_MANUALE_IN_RICALCOLO");
	InfCorrentiLav infCorrentiLav= null;
	infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
	infCorrentiLav.setSkipLista(true);
	infCorrentiLav.show(out); 
	PageAttribs attributi = new PageAttribs(user, "AmmCalcolaStatoOccAllaDataPage");
	boolean canModify = attributi.containsButton("CALCOLASTATOOCC");
	String strErrorCode = "";
    boolean confirm = false;
    SourceBean sbError = (SourceBean) serviceResponse.getAttribute("M_CalcolaStatoOccupazionale.RECORD.PROCESSOR.ERROR");
    String forzaRicostruzione = "false";
    String continuaRicalcolo = "false";
    Vector rowsParam = null;
    SourceBean valoriParametri = null;
    if (sbError != null) {
    	strErrorCode = sbError.getAttribute("code").toString();
    	msgConferma = sbError.getAttribute("messagecode").toString();
    	msgConferma = msgConferma + " Vuoi proseguire?";
    	rowsParam = serviceResponse.getAttributeAsVector("M_CalcolaStatoOccupazionale.RECORD.PROCESSOR.CONFIRM.PARAM");
    	confirm = true;
    	if (rowsParam != null && rowsParam.size() > 0) {
    		valoriParametri = (SourceBean) rowsParam.get(0);
	    	forzaRicostruzione = valoriParametri.getAttribute("value").toString();
	    	valoriParametri = (SourceBean) rowsParam.get(1);
	    	continuaRicalcolo = valoriParametri.getAttribute("value").toString();
	   	}
    }
    
    if (confirm) {
    	datInizioCalcolo = serviceRequest.containsAttribute("dataInizioCalcolo")?serviceRequest.getAttribute("dataInizioCalcolo").toString():"";
    }
    
%>
<html>
<head>
<title>Calcola stato occupazionale</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<af:linkScript path="../../js/"/>  
<script language="JavaScript">

function ripetiOperazione() {
	doFormSubmit(document.form1);
}

function calcola(operazione) {
	var ok = true;
	if (operazione == 1) {
		if (confirm("Verrà ricalcolato lo stato occupazionale del lavoratore. Proseguire?")) {
			ok = true;	
		}	
		else {
			ok = false;
		}
	}
	else {
		alert("Le modifiche non saranno riportate in effettivo!");
	}
	document.form1.CALCOLA.value = operazione;
	return ok;
}

</script>

</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Calcola stato occupazionale</p>
 	<%if (!strCalcola.equals("")) {
 		String descWarning = "";
 		String descError = "";
 		String descStatoOcc = "";
 		String msg = "";
	 	SourceBean row_StatoOcc = (SourceBean)serviceResponse.getAttribute("M_CalcolaStatoOccupazionale.ROW");
	 	if (row_StatoOcc != null) {
		 	//Errori
		 	if (row_StatoOcc.containsAttribute("Error")) {
				descError = StringUtils.getAttributeStrNotNull(row_StatoOcc, "Error");
			}
			//Warnings
		 	if (row_StatoOcc.containsAttribute("Warning")) {
		 		descWarning = StringUtils.getAttributeStrNotNull(row_StatoOcc, "Warning");
		 	}
		 	//Descrizione se tutto ok (ricalcolo corretto)
		  	if (row_StatoOcc.containsAttribute("Descrizione")) {
		  		descStatoOcc = StringUtils.getAttributeStrNotNull(row_StatoOcc, "Descrizione");
		  	}
		  	if (descError.equals("")) {
			  	if (descWarning.equals("")) {
				  	msg = "Ricostruita storia del lavoratore\\n\\r";
				  	msg +="Lo stato occupazionale del lavoratore risulta: " + descStatoOcc;
				  	%>
				  	<script language="javascript">
				  		alert ("<%=msg%>");
				  	</script>
				  	<%
				}
				else {
					%>
				  	<script language="javascript">
				  		alert ("<%=descWarning%>");
				  	</script>
				  	<%
				}
			}
			else {
				msg = "Ricalcolo impatti interrotto.\\n\\r";
				msg += descError;
				%>
			  	<script language="javascript">
			  		alert ("<%=msg%>");
			  	</script>
				<%
			}
		}
 	}
  	%>
  	    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=cdnFunzione%>,   <%=cdnLavoratore%>);
    	}
    </script>	
  	
  	
  	<af:form name="form1" action="AdapterHTTP?PAGE=AmmCalcolaStatoOccAllaDataPage" method="POST">
	  <%out.print(htmlStreamTop);%> 
	  <table class="main" border="0">	
	   <tr>
		 <td class="etichetta" nowrap>Data</td>
		 <td class="campo">
		    <af:textBox type="date" name="dataInizioCalcolo" title="Data calcolo stato occupazionale" required="true" validateOnPost="true" value="<%=datInizioCalcolo%>" size="11" maxlength="10"/>
		 </td>            
	   </tr>
	   <tr>
		<td class="etichetta" nowrap>Cancella stati occupazionali manuali</td>
		<td class="campo">
			<input type="checkbox" name="CANCELLA_STATO_OCC_MANUALE_IN_RICALCOLO" value="S" <%=cancellaSOccManuale ? "CHECKED" : ""%>/>
		</td>
		</tr>
	   <tr><td>&nbsp;</td></tr>
	   <% if (canModify) {%>
		   <tr>
		   <td colspan="2" align="center">
			<input class="pulsante" type="submit" name="btnNoCommit" value="Simulazione" onclick="javascript:return calcola(0);"/>
		    <input class="pulsante" type="submit" name="btnCommit" value="Calcolo effettivo" onclick="javascript:return calcola(1);"/>
		    </td>
		   </tr>
	   <%}%>
	  </table>
	  <%out.print(htmlStreamBottom);%>
	  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	  <input type="hidden" name="CALCOLA" value="<%=strCalcola%>">
	  <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
	  <input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
	  <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
	 </af:form>
	 
	<%if (confirm) {%>
	<script language="Javascript">
		if (confirm("<%=msgConferma%>")) { 
			ripetiOperazione();
		}
		else {
			document.form1.FORZA_INSERIMENTO.value = "false";
			document.form1.CONTINUA_CALCOLO_SOCC.value = "false";
		}
	</script>
	<%}%> 
</body>
</html>
