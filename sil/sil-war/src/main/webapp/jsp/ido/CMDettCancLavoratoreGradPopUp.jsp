<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(true);
    String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  	    
  	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  	String prgNominativo = (String) serviceRequest.getAttribute("prgNominativo");
  	
  	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
  	String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
  	String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
  	String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");
  	String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
  	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");  	 	  	
  	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");  	
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	
  	String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();
  	String prgIncrocio = (String) serviceRequest.getAttribute("prgIncrocio");
  	
  	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
	// modulo dettaglio cancellazione
	SourceBean motivoApertura = (SourceBean) serviceResponse.getAttribute("CMDettaglioCancLavoratoreModule.ROWS");
	
	String prgcancgrad = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.PRGCANCGRAD");
	String prgrosa = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.PRGROSA");
	String cdnlavoratore = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.CDNLAVORATORE");
	String codmotcancgrad = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.CODMOTCANCGRAD");
	String descrmotivocanc = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.DESCRMOTIVOCANC");
	String strspecifica = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.STRSPECIFICA");
	String numordine = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.NUMORDINE");
	String numpunteggio = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.NUMPUNTEGGIO");
	String cdncanc = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.CDNCANC");
	String dtmcanc = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.DTMCANC");			

  	String message = (String) serviceRequest.getAttribute("MESSAGE");
  	String listPage = (String) serviceRequest.getAttribute("OLD_LIST_PAGE");
  	if (("").equals(listPage) || listPage == null) {  		
  		if (("LIST_FIRST").equalsIgnoreCase(message)) {
  			listPage = "1";
  		}
  		else if (("LIST_LAST").equalsIgnoreCase(message)) {
  			listPage = "-1";
  		}
  		else { 
	  		listPage = "1";
	  	}
  	}	  	
  	PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));    

  	boolean canModify = true;
%>


<script>  
	
	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	
	
	function indietro(){   	    
 		window.location="AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&MESSAGE=<%=message%>&LIST_PAGE=<%=listPage%>";
	}
   
</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">
	
<%     
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
%>	
<p class="titolo">Cancellazione lavoratore da graduatoria CM/art.1</p>
<br>
<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>

<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="CMMatchDettGraduatoriaPage">
<input type="hidden" name="MODULE" value="CMCancLavDaGraduatoria">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>">
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>">
<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>">
<input type="hidden" name="PRGNOMINATIVO" value="<%=prgNominativo%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="PRGINCROCIO" value="<%=prgIncrocio%>">
<input type="hidden" name="PRGTIPOINCROCIO" value="<%=prgTipoIncrocio%>">
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="codCpi" value="<%=codCpiApp%>">
<input type="hidden" name="ConcatenaCpi" value="<%=ConcatenaCpi%>">
<input type="hidden" name="LIST_PAGE" value="<%=listPage%>">
	
	<table>
		<tr>	
			<td class="etichetta">Motivo cancellazione</td>		
			<td align="left">
				<af:comboBox name="CODMOTCANCGRAD"
	                     size="1"
	                     title="Motivo cancellazione"
	                     multiple="false"
	                     required="true"
	                     focusOn="false"
	                     moduleName="CMListaMotiviCancGradModule"	                     
	                     addBlank="true"
	                     blankValue=""
	                     selectedValue = "<%=codmotcancgrad%>"					                     
	                     classNameBase="input"					                    
	        />		
			</td>				
		</tr>
		<tr>	
			<td class="etichetta">Note</td>		
			<td align="center">
				<af:textArea classNameBase="textarea" title="Note" name="strSpecifica" value="<%=strspecifica%>"
	                 cols="60" rows="4" maxlength="100" onKeyUp="fieldChanged();" required="false"
	                 readonly="<%=String.valueOf(!canModify)%>"/>
			</td>				
		</tr>
		<tr>
			<td colspan="2">&nbsp;
			</td>
		</tr>
		<% if (canModify){%>
			<tr>			
				<td align="center" colspan="2">					
					<input type="submit" class="pulsanti" value="Cancella">
					&nbsp;&nbsp;
					<input type="button" class="pulsanti" value="Chiudi" onclick="indietro()">
				</td>			
			</tr>
		<%
		}
		%>
		<tr>			
			<td colspan="2">&nbsp;
			</td>
		</tr>
	</table>	

</af:form>     
<%out.print(htmlStreamBottom);%>
<br/>
<br/>
</body>
</html>