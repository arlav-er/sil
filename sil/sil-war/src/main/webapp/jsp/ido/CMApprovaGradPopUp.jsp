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
  	String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
	String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
	String prgRosa = serviceRequest.getAttribute("PRGROSA").toString();
	String prgTipoRosa = serviceRequest.getAttribute("PRGTIPOROSA").toString();
	String prgIncrocio = serviceRequest.getAttribute("PRGINCROCIO").toString();
	String prgTipoIncrocio = serviceRequest.getAttribute("PRGTIPOINCROCIO").toString();
	String prgOrig = prgRichiestaAz;
	String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
	String _page = serviceRequest.getAttribute("PAGE").toString();
	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
	
	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
	SourceBean stato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS");
	String cdnStatoRich = stato.getAttribute("ROW.CDNSTATORICH") == null ? "" : (String)stato.getAttribute("ROW.CDNSTATORICH");		
	                     
	SourceBean motivoApertura = (SourceBean) serviceResponse.getAttribute("CMGestApprovaGraduatorieModule.ROWS");
	String prgapprovazionegrad = motivoApertura.getAttribute("ROW.prgapprovazionegrad") == null ? "" : ((BigDecimal)motivoApertura.getAttribute("ROW.prgapprovazionegrad")).toString();	
	String numDetermina = motivoApertura.getAttribute("ROW.numDetermina") == null ? "" : ((BigDecimal)motivoApertura.getAttribute("ROW.numDetermina")).toString();
	String datProtocollazione = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.datProtocollazione");
	String datPubblicazione = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.datPubblicazione");
	
	int nroMansioni = 0;   
	String prgC1 = "";
	boolean viewPar = false;
	
	Testata operatoreInfo 	= 	null; 
	BigDecimal cdnUtIns	= null;
	String dtmIns = "";
	BigDecimal cdnUtMod = null;
	String dtmMod = "";
	
	if (motivoApertura != null) {
		cdnUtIns = (BigDecimal)motivoApertura.getAttribute("ROW.CDNUTINS");
		dtmIns = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.DTMINS");
		cdnUtMod = (BigDecimal)motivoApertura.getAttribute("ROW.CDNUTMOD");
		dtmMod = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.DTMMOD");
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
	
	String p_codCpi = user.getCodRif();
	String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();
  	String listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
  	if (("").equals(listPage) || listPage==null) {
  		listPage = "1";
  	} 
  	     	    	  	  	  	  
    boolean canModify = false;  	
    boolean gestCopia = false;
    
    if (!("4").equals(cdnStatoRich) && !("5").equals(cdnStatoRich)){
    	canModify = true;
    }
    
    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
    	canModify = false;
    }    
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
  	
%>


<script>
		
	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	
	
	
	function checkCampiObbligatori() {
		/*
		if(document.Frm1.numDetermina.value == ""){
			alert("Non è stato valorizzato il campo Numero determina");
		    return false;
		}
		
		if(document.Frm1.datProtocollazione.value == ""){
			alert("Non è stato valorizzato il campo Data protocollazione");
		    return false;
		}		
		
		if(document.Frm1.datPubblicazione.value == ""){
			alert("Non è stato valorizzato il campo Data pubblicazione");
		    return false;
		}		
		*/			    
        return true;
	}
		
	function indietro(){   
		<%
	    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=CMStoricoDettGraduatoriaPage&MODULE=CMStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
	    %>    	 	   
			window.location="AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	     	
		<%
		}
		%>
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

<%@ include file="InfoCorrRichiesta.inc" %>

<p class="titolo">Riferimenti atto di approvazione</p>	
<!-- messaggi di esito delle operazioni applicative -->
<font color="green"> 
 <af:showMessages prefix="CMSalvaApprovaGradModule"/>
</font>
<font color="red"><af:showErrors/></font>

<br>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
<input type="hidden" name="PAGE" value="CMGestApprovaGraduatoriePage">
<input type="hidden" name="MODULE" value="CMSalvaApprovaGradModule">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="prgapprovazionegrad" value="<%=prgapprovazionegrad%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>">
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>">
<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="PRGINCROCIO" value="<%=prgIncrocio%>">
<input type="hidden" name="PRGTIPOINCROCIO" value="<%=prgTipoIncrocio%>">
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="LIST_PAGE" value="<%=listPage%>">
<input type="hidden" name="CDNUTINS" value="<%=p_codCpi%>">
<input type="hidden" name="codCpi" value="<%=codCpiApp%>">
<input type="hidden" name="ConcatenaCpi" value="<%=ConcatenaCpi%>">
	
	<table>
		<tr>
			<td class="etichetta2">Numero di determina</td>
	     	<td class="campo2">
	        	<af:textBox classNameBase="input"
	                    readonly="<%= String.valueOf(!(canModify)) %>"
	                    title="Num. determina"	                    
	                    name="numDetermina"
	                    value="<%=numDetermina%>"
	                    onKeyUp="fieldChanged();"	                   
	                    size="10"
	                    maxlength="38"/>
	        </td>
	    </tr>
	    <tr>
			<td class="etichetta2">Data di protocollazione</td>
	     	<td class="campo2">
	        	<af:textBox classNameBase="input" type="date" validateOnPost="true" onKeyUp="fieldChanged();" name="datProtocollazione" size="12" maxlength="10" value="<%=datProtocollazione%>" readonly="<%= String.valueOf(!canModify) %>" />	        	
	        </td>
	    </tr>
	    <tr>
			<td class="etichetta2">Data di pubblicazione della graduatoria</td>
	     	<td class="campo2">
	     		<af:textBox classNameBase="input" type="date" validateOnPost="true" onKeyUp="fieldChanged();" name="datPubblicazione" size="12" maxlength="10" value="<%=datPubblicazione%>" readonly="<%= String.valueOf(!canModify) %>" />	           	
	        </td>
	    </tr>
		<tr>
			<td colspan="2">&nbsp;
			</td>
		</tr>
		<% if (canModify){%>
			<tr>			
				<td align="center" colspan="2">					
					<input type="submit" class="pulsanti" value=" Approva " name="btnAggiorna">
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
<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>
</body>
</html>