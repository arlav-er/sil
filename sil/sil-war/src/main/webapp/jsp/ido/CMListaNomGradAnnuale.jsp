<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>
   
<%           	 	  	
	
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi =	new PageAttribs(user, _page);

	boolean rigenera = attributi.containsButton("RIGENERA");
	boolean storicizza = attributi.containsButton("STORICIZZA");
	boolean annulla = attributi.containsButton("ANNULLA");
	boolean stampa = attributi.containsButton("STAMPA");
	
	
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	String annoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"annoGrad");
	String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
	String statoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"statoGrad");
	
	boolean canModify = true;  	
	//boolean gestCopia = false;
	//boolean Storicizza = true;
   
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
    Object prgGraduatoria = serviceResponse.getAttribute("M_RIGENERAGRADANNUALEMODULE.PRGGRADUATORIA"); 
    if (prgGraduatoria == null) {
    	prgGraduatoria = serviceResponse.getAttribute("M_INSERTGRADANNUALEMODULE.PRGGRADUATORIA");
		if (prgGraduatoria == null) {
			prgGraduatoria = serviceRequest.getAttribute("PRGGRADUATORIA");		
    	}     	    	
    }
    serviceRequest.delAttribute("PRGGRADUATORIA");    
    serviceRequest.setAttribute("PRGGRADUATORIA", prgGraduatoria);
    
    SourceBean testataGrad = (SourceBean) serviceResponse.getAttribute("M_GetTestataGraduatoriaAnnuale");
    SourceBean testata = (SourceBean) testataGrad.getAttribute("ROWS.ROW");

    String codStatoGrad = StringUtils.getAttributeStrNotNull(testata, "CODSTATOGRAD");
    Object anno = testata.getAttribute("NUMANNO");
    Object numKloGrad = testata.getAttribute("NUMKLOGRADUATORIA");
    
    String queryString = null;
%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>   

<html>
<head>
<title></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 	<af:linkScript path="../../js/" />
 	<script type="text/Javascript">
  
	  	function fieldChanged() {
	    	<%if (canModify) {out.print("flagChanged = true;");}%>
		}
  
		function tornaAllaLista() {  
			if (isInSubmit()) return;
		 	 
		    url="AdapterHTTP?PAGE=CMListaGradAnnualePage";
		    url += "&CDNFUNZIONE="+"<%=_funzione%>";
		    url += "&annoGrad="+"<%=annoGrad%>";
		    url += "&codMonoTipoGrad="+"<%=codMonoTipoGrad%>";
		    url += "&statoGrad="+"<%=statoGrad%>";
		     
		    setWindowLocation(url);
		}
    	
  		function annullaGradAnnuale() {  
	 		if (isInSubmit()) return;
	     
     		var t="Sicuri di voler annullare la graduatoria per l'anno <%=anno%>?";
    
  	 		if ( confirm(t) ) { 
	     		url="AdapterHTTP?PAGE=CMInsertGradAnnualePage";
	     		url += "&MODULE="+"M_AnnullaGradAnnualeModule";
	     		url += "&PRGGRADUATORIA="+"<%=prgGraduatoria%>"; 
	     		url += "&NUMKLOGRADUATORIA="+"<%=numKloGrad%>"; 
	     		setWindowLocation(url);
	 		}
  		}

  		function rigeneraGradAnnuale() {  
     		if (isInSubmit()) return;
     
     		var t="Sicuri di voler rigenerare la graduatoria per l'anno <%=anno%>?";
    
  	 		if ( confirm(t) ) {  	         		     
     			url="AdapterHTTP?PAGE=CMTmpGradAnnualePage";
     			url += "&MODULE=M_TmpGradAnnualeModule";
     			url += "&FLGRIGENERA=S";  
     			url += "&PRGGRADUATORIA="+"<%=prgGraduatoria%>"; 
     			url += "&CDNFUNZIONE="+"<%=_funzione%>";  
     			setWindowLocation(url);
     		}
  		}
	
    	function stampaGradAnnuale(){
 			apriGestioneDoc('RPT_GRADUATORIA_ANN','&PRGGRADUATORIA=<%=prgGraduatoria%>','ALL68O');
 		}
  </script>
  
</head>

<body class="gestione" onload="rinfresca()">    
	
<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>

<%@ include file="InfoGraduatoriaAnnuale.inc" %>  

<br>

	<af:list moduleName="M_ListaNominativiGradAnnualeModule" configProviderClass="it.eng.sil.module.ido.CMListaNominativiGradAnnualeConfig"/>       
	
	<table align="center">
	
	<tr>			     
		<td align="center">					
			<table>
				<tr align="center">
					<td>
						<%
						if (("1").equalsIgnoreCase(codStatoGrad)) {
						%>
							<center>
							<%if (rigenera) { %><input type="button" class="pulsanti" value="Rigenera graduatoria" onclick="rigeneraGradAnnuale()" >
							&nbsp;&nbsp;&nbsp; 
							<%} %>							
							<%if (annulla) { %><input type="button" class="pulsanti" value="Annulla graduatoria" onclick="annullaGradAnnuale()"> 
							&nbsp;&nbsp;&nbsp;
							<%} %>
							</center>
						<%
						}
						if (stampa) {%>
						<br>
						<input type="button" class="pulsanti" value="Stampa" onclick="stampaGradAnnuale()">
						<%} %>
					</td>	
				</tr>
				<tr align="center">
					<td><input type="button" class="pulsanti" value="Torna alla lista" onclick="tornaAllaLista()" ></td>
				</tr>				
			</table>		
		</td>			
	</tr>         
</table>	
<br/>
</body>
</html>