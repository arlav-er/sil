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
	
	SourceBean motivoApertura = (SourceBean) serviceResponse.getAttribute("ASGestRiapriGraduatorieModule.ROWS");
	
	String strmotivo = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.strmotivo");
	
	Testata operatoreInfo 	= 	null; 
	BigDecimal cdnUtIns	= null;
	String dtmIns = "";
	BigDecimal cdnUtMod = null;
	String dtmMod = "";
	
	if (motivoApertura != null) {
		cdnUtIns = (BigDecimal)motivoApertura.getAttribute("ROW.CDNUTINS");
		dtmIns = StringUtils.getAttributeStrNotNull(motivoApertura, "ROW.DTMINS");		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
	
	int nroMansioni = 0;   
	String prgC1 = "";
	boolean viewPar = false;
	
	String p_codCpi = user.getCodRif();
	String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();
  	String listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
  	if (("").equals(listPage) || listPage==null) {
  		listPage = "1";
  	} 
  	     	    	  	  	  	  
    boolean canModify = true;  	
    boolean gestCopia = false;
    
    if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
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
				
	    if (document.Frm1.MotRiapertura.value.length > 0) {		    
			  
			var t="Confermi l'operazione?";
		    
			if ( confirm(t) ) {
          		return true;
          	}
          	else {
          		return false;
          	}
        }
        alert("Il motivo di riapertura è obbligatorio!");
        return false;
	}
		
	function indietro(){   
		<%
	    if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=ASStoricoDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
	    %>    	 	   
			window.location="AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	     	
		<%
		}
		%>
	}

	function riapriGraduatoria() {
	
		if (isInSubmit()) return;
		  
		var t="Confermi l'operazione?";
	    
		if ( confirm(t) ) {
	
			var s = "AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage";
		  	s += "&MODULE=ASRiapriGraduatoria";
		  	s += "&PRGROSA=<%=prgRosa%>";          
		  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
		  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	 	  	s += "&PRGUNITA=<%=prgUnita%>";
	        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
		  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
		  	s += "&codCpi=<%=codCpiApp%>"; 
		  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";   
		
		  	setWindowLocation(s);
	    }
	  
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
	
<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>

<br>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
<input type="hidden" name="PAGE" value="ASMatchDettGraduatoriaPage">
<input type="hidden" name="MODULE" value="ASRiapriGraduatoria">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>">
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>">
<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="PRGINCROCIO" value="<%=prgIncrocio%>">
<input type="hidden" name="PRGTIPOINCROCIO" value="<%=prgTipoIncrocio%>">
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="codCpi" value="<%=codCpiApp%>">
<input type="hidden" name="ConcatenaCpi" value="<%=ConcatenaCpi%>">

<input type="hidden" name="LIST_PAGE" value="<%=listPage%>">
	<p class="titolo">Motivazione della riapertura</p>
	<table>
		<tr>	
			<td class="etichetta">Motivo riapertura</td>		
			<td align="center">
				<af:textArea classNameBase="textarea" name="MotRiapertura" value="<%=strmotivo%>"
	                 cols="60" rows="4" maxlength="1000" onKeyUp="fieldChanged();"
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
					<input type="submit" class="pulsanti" value=" Riapri " name="btnAggiorna">
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