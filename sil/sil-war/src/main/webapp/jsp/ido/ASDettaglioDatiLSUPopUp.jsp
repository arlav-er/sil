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
	ProfileDataFilter filter = new ProfileDataFilter(user, "ASDettaglioDatiLSUPage");
    boolean canView = filter.canView();
    if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}    
    
  	 	
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
  	
  	SourceBean stato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS");
	String cdnStatoRich = stato.getAttribute("ROW.CDNSTATORICH") == null ? "" : (String)stato.getAttribute("ROW.CDNSTATORICH");			    
  	   
  	String numAnzianita =  "";
  	String flgProfessionalita = "";
  	String numCaricoFam = "";
  	SourceBean datiLsu = (SourceBean) serviceResponse.getAttribute("ASDettaglioDatiLSUModule.ROWS.ROW");
  	if (datiLsu != null) {
  		numAnzianita = datiLsu.getAttribute("NUMANZIANITALSU") == null ? "" : ((BigDecimal)datiLsu.getAttribute("NUMANZIANITALSU")).toString();
  		numCaricoFam = datiLsu.getAttribute("NUMCARICOFAM") == null ? "" : ((BigDecimal)datiLsu.getAttribute("NUMCARICOFAM")).toString();
  		flgProfessionalita = StringUtils.getAttributeStrNotNull(datiLsu, "FLGPROFESSIONALITA");
  	}
  	
  	boolean readOnlyStr = false;
  	if (("3").equalsIgnoreCase(prgTipoRosa)) {
  		readOnlyStr = true;
  	}
  	    	  	  	
  	PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID), "ASDettaglioDatiLSUPage");
  	boolean canModify = false;	  	  	  	
  	if (!("4").equals(cdnStatoRich) && !("5").equals(cdnStatoRich)) {
    	canModify = pageAtts.containsButton("AGGIORNA");
    }
    
  	if (("3").equalsIgnoreCase(prgTipoRosa))  {    
  		canModify = false;
  	}    
  	  	  	
  	if (!canModify) {
	  	readOnlyStr = true;
  	}
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
  	String indietro = "";
  	if (("ASListaAdesioniPage").equalsIgnoreCase(p_page)) {
  		indietro="AdapterHTTP?PAGE=ASListaAdesioniPage&CDNFUNZIONE="+cdnFunzione+"&cdnLavoratore="+cdnLavoratore+"&LIST_PAGE=1";
  	}
  	else if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
		indietro = "AdapterHTTP?PAGE=ASStoricoDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria&PRGROSA="+prgRosa+"&PRGTIPOROSA="+prgTipoRosa+"&PRGTIPOINCROCIO="+prgTipoIncrocio+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&CDNFUNZIONE="+cdnFunzione+"&MESSAGE="+message+"&LIST_PAGE="+listPage;
  	}
  	else {  		
  		indietro = "AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria&PRGROSA="+prgRosa+"&PRGTIPOROSA="+prgTipoRosa+"&PRGTIPOINCROCIO="+prgTipoIncrocio+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&CDNFUNZIONE="+cdnFunzione+"&MESSAGE="+message+"&LIST_PAGE="+listPage;  		
  	}
%>


<script>
	
	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}
	
	function indietro(){  		
	 	window.location="<%=indietro%>";     	
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

<p class="titolo">Raccolta dati per graduatoria ex LSU</p>		
<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="ASSalvaDatiLSUModule"/>
</font>
<br>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="ASDettaglioDatiLSUPage">
<input type="hidden" name="MODULE" value="ASSalvaDatiLSUModule">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
<input type="hidden" name="PRGNOMINATIVO" value="<%=prgNominativo%>">
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>">
<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="PRGTIPOINCROCIO" value="<%=prgTipoIncrocio%>">
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="OLD_LIST_PAGE" value="<%=listPage%>">
<input type="hidden" name="MESSAGE" value="<%=message%>">

<%
if (("ASListaAdesioniPage").equalsIgnoreCase(p_page)) {
%>
	<input type="hidden" name="OLD_PAGE" value="ASListaAdesioniPage">
<%
}
%>	
	<table>				
		<tr>
	    	<td class="etichetta">Mesi anzianità LSU</td>
	    	<td colspan=3 class="campo">	
				<af:textBox type="integer" classNameBase="input" onKeyUp="fieldChanged();"
	              	name="numAnzianita" size="5" value="<%= numAnzianita%>" 
	              	readonly="<%=String.valueOf(readOnlyStr)%>" maxlength="7" />  
	      	</td>
		</tr> 
		<tr><td colspan="4"></td></tr>
		<tr>
	    	<td class="etichetta">Carico familiare</td>
	    	<td colspan=3 class="campo">	
				<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="integer"
	              	name="numCaricoFam" size="7" value="<%=numCaricoFam %>" 
	              	readonly="<%=String.valueOf(readOnlyStr)%>" maxlength="" />
	      	</td>
		</tr> 
		<tr><td colspan="4"></td></tr>
		<tr>
	    	<td class="etichetta">Professionalità acquisita</td>
	    	<td colspan=3 class="campo">	
				<af:comboBox 				   
	                name="flgProfessionalita"
	                classNameBase="input"
	                onChange="fieldChanged()">
	  
	                <option value=""  <% if ( "".equalsIgnoreCase(flgProfessionalita) )  { %>SELECTED<% } %> ></option>
	                <option value="S" <% if ( "S".equalsIgnoreCase(flgProfessionalita) ) { %>SELECTED<% } %> >Sì</option>
	                <option value="N" <% if ( "N".equalsIgnoreCase(flgProfessionalita) ) { %>SELECTED<% } %> >No</option>               
	              </af:comboBox>
	              <script language="javascript">
				  	if (<%=readOnlyStr%>) {
				    	document.Frm1.flgProfessionalita.disabled = true;
				   	}
				  </script>
	      	</td>
		</tr> 
	</table>

	<% if (readOnlyStr){%>
	<table>
		<tr>
			<td align="center">
				
			</td>
		</tr>
	</table>

	<%}	else {%>
	<table >
		<tr>
			<td>&nbsp;
			</td>
		</tr>
		<%
		if (canModify) {
		%>		
			<tr>			
				<td align="center">
					<input type="submit" class="pulsanti" value="Aggiorna" name="btnAggiorna">
					
				</td>				
			</tr>
		<%
		}   
		%>		
		<tr>			
			<td>&nbsp;
			</td>
		</tr>
	</table>	
	<%}%>

</af:form>     
<%out.print(htmlStreamBottom);%>
</body>
</html>