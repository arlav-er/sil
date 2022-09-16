<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="CM_CommonScripts.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%  
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspAutorizPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	      
  	String _page = (String) serviceRequest.getAttribute("PAGE");   
  	    
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");  	
  	String codMonoCategProsp = serviceRequest.getAttribute("CODMONOCATEGPROSP") == null ? "" : (String)serviceRequest.getAttribute("CODMONOCATEGPROSP");
 	String module = serviceRequest.getAttribute("MODULE") == null ? "" : (String)serviceRequest.getAttribute("MODULE");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	
  	InfoProvinciaSingleton provinciaSil = InfoProvinciaSingleton.getInstance(); 
	String codProvinciaSil = provinciaSil.getCodice();
	
	String codProvinciaProspetto = serviceResponse.getAttribute("CMPROSPDETTMODULE.ROWS.ROW.CODPROVINCIA") != null ?
			serviceResponse.getAttribute("CMPROSPDETTMODULE.ROWS.ROW.CODPROVINCIA").toString() : "";
	
	String flgEsonero = serviceResponse.getAttribute("CMPROSPDETTMODULE.ROWS.ROW.FLGESONERO") != null ?
			serviceResponse.getAttribute("CMPROSPDETTMODULE.ROWS.ROW.FLGESONERO").toString() : "";
	
  	String codProvincia = "";
  	String numInteressati = "";
  	String codMonoEccDiff = "";
  	String codMonoCategoria = "";
  	String codFiscCapoGruppo = "";
  	  	  
  	PageAttribs attributi = new PageAttribs(user, "CMProspAutorizPage");	
	
	boolean canModify 		= 	false;
	boolean canInsert 		= 	false;
	boolean readOnlyStr     = 	false; 
				
	canModify =	attributi.containsButton("AGGIORNA");    		
    String fieldReadOnly = canModify ? "false" : "true";  				
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

	SourceBean codTipoAziendaSB = (SourceBean)serviceResponse.getAttribute("M_GetTipoAziendaByProspetto.ROWS.ROW");
    String codTipoAzienda = codTipoAziendaSB.getAttribute("codTipoAzienda").toString();
    
    boolean error = serviceResponse.getAttribute("CMPROSPCOMPENSAZIONESAVEMODULE.ERROR") != null ? true : false;
    
    // lettura parametri in request in caso di inserimento fallito
    
    codProvincia = serviceRequest.getAttribute("codProvincia") == null ? 
    		"" : (String)serviceRequest.getAttribute("codProvincia");
    codMonoEccDiff = serviceRequest.getAttribute("codMonoEccDiff") == null ? 
    		"" : (String)serviceRequest.getAttribute("codMonoEccDiff");
    numInteressati = serviceRequest.getAttribute("numInteressati") == null ? 
    		"" : (String)serviceRequest.getAttribute("numInteressati");
    codMonoCategoria = serviceRequest.getAttribute("codMonoCategoria") == null ? 
    		"" : (String)serviceRequest.getAttribute("codMonoCategoria");
    codFiscCapoGruppo = serviceRequest.getAttribute("strCFAZCapoGruppo") == null ? 
    		"" : (String)serviceRequest.getAttribute("strCFAZCapoGruppo");
    
%>

<html>
<head>
<title>Compensazione</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />  

<script language="javascript">
	
	var codTipoAzienda = "<%=codTipoAzienda%>";
	var mapProvinciaRegione = [];
	<%
	SourceBean row = null;
	String codProvinciaTemp, codRegioneTemp;
    Vector rows = serviceResponse.getAttributeAsVector("M_GetMapProvinciaRegione.ROWS.ROW");
    for (int i = 0; i < rows.size(); i++) {
    	row = (SourceBean) rows.get(i);
    	codProvinciaTemp = (String) row.getAttribute("CODPROVINCIA");
    	codRegioneTemp = (String) row.getAttribute("CODREGIONE");
    	%>mapProvinciaRegione["<%=codProvinciaTemp%>"] = "<%=codRegioneTemp%>";<%
    }
	%>


	function controllaCampi() {
		
		//var codProvSil = "<%=codProvinciaSil%>";
		var codProvinciaProspetto = "<%=codProvinciaProspetto%>";
		var flgEsonero = "<%=flgEsonero%>";
		var codProv = document.Frm1.codProvincia.value;
		var eccOrRid = document.Frm1.codMonoEccDiff.value; 
		var codCateg = document.Frm1.codMonoCategoria.value;
		var codMonoCategProsp = document.Frm1.CODMONOCATEGPROSP.value;
		var codFiscaleAzGruppo = document.Frm1.strCFAZCapoGruppo.value;

		/*
		if (codMonoCategProsp == 'B' || codMonoCategProsp == 'C' || !codFiscaleAzGruppo) {
			if (codProv == codProvSil ) {
				alert("L'autocompensazione non può essere riferita alla stessa provincia del prospetto"); 
				return false;
			}			

			//dona/dome 05/09/2012 
			//if (eccOrRid == 'D') {		
			//	alert("L'autocompensazione non può essere in Riduzione");
			//	return false;	
			//} 
			
			if (codCateg == 'A') {		
				alert("L'autocompensazione non può riguardare Altre categorie protette");
				return false;	
			}

			if (codFiscaleAzGruppo != "") {
				if (checkCFAz("strCFAZCapoGruppo") == false) {
					return false;
				}	
			}
		}	
		*/
		
		if (!codFiscaleAzGruppo && codProv == codProvinciaProspetto ) {
			alert("La compensazione non può essere riferita alla stessa provincia del prospetto"); 
			return false;
		}
		
		if (codTipoAzienda == "PA") {
			if (mapProvinciaRegione[codProv] != mapProvinciaRegione[codProvinciaProspetto]) {
				alert("Per la Pubblica Amministrazione la compensazione deve essere riferita a una provincia nella stessa regione di appartenenza della provincia del prospetto"); 
				return false;
			}
		}

		if (flgEsonero == "S" && 
				eccOrRid == "E" && 
				codProvinciaProspetto == codProv) {

			alert("Non è possibile avere compensazioni in eccedenza per la stessa provincia di compilazione del prospetto se è stato indicato di avere un esonero");
			return;
			
		}
		
		return true;
		
	}
	
	function ripristina() {
				    	        
		<%if (!error && !module.equals("") && module.equals("CMProspCompensazioneSaveModule")) { %>

			var url = "AdapterHTTP?PAGE=CMProspAutorizPage";		
		    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
		    url += "&CDNFUNZIONE=<%=cdnFunzione%>";	
	    
			window.opener.location.replace(url);	// Refresh finestra genitore
			
			window.close();
		<%} %>
	}

</script>
</head>

<body onload="rinfresca();ripristina();">
<br/>

<af:showErrors showUserError="true" />
<af:showMessages prefix="CMProspCompensazioneSaveModule"/>

<%out.print(htmlStreamTop);%>

<p class="titolo">Nuova compensazione</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspCompensazionePage"/>
<input type="hidden" name="MODULE" value="CMProspCompensazioneSaveModule"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>   
<input type="hidden" name="CODMONOCATEGPROSP" value="<%=codMonoCategProsp%>"/>
<input type="hidden" name="CODPROVINCIAPROSPETTO" value="<%=codProvinciaProspetto%>"/> 
<input type="hidden" name="FLGESONERO" value="<%=flgEsonero%>"/>
<input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>

<table class="main">	
	<tr>
    	<td class="etichetta2">Sede</td>
		<td class="campo" colspan="3">
    		<af:comboBox addBlank="true" name="codProvincia" title="Sede" selectedValue="<%=codProvincia%>" moduleName="M_COMBO_PROVINCIA" required="true" disabled="<%=fieldReadOnly%>"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta2">In eccedenza/Riduzione</td>
		<td class="campo2">
			<af:comboBox name="codMonoEccDiff" title="In eccedenza/Riduzione" classNameBase="input" required="true" disabled="<%=fieldReadOnly%>">
		    	<option value=""  <% if ( "".equalsIgnoreCase(codMonoEccDiff) )  { %>SELECTED="true"<% } %> ></option>
            	<option value="E" <% if ( "E".equalsIgnoreCase(codMonoEccDiff) )  { %>SELECTED="true"<% } %>>Eccedenza</option>
            	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoEccDiff) )  { %>SELECTED="true"<% } %>>Riduzione</option>
        	</af:comboBox>
    	</td>   
	</tr>			
	<tr>
		<td class="etichetta2" width="50%">Num.</td>
		<td class="campo2">
			<af:textBox classNameBase="input" title="Num." type="integer" name="numInteressati" size="5" value="<%=numInteressati%>" maxlength="6" required="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
	</tr>
	<tr>
		<td class="etichetta2">Categoria</td>
		<td class="campo2">
			<af:comboBox name="codMonoCategoria" title="Categoria" classNameBase="input" required="true" disabled="<%=fieldReadOnly%>">	  	
		    	<option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %> ></option>               
            	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Disabili</option>
            	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Altre categorie protette</option>               
        	</af:comboBox>    
    	</td>   
	</tr>
	<tr>
	<td class="etichetta2">Codice fiscale azienda appartenente al gruppo</td>
		<td class="campo2">
			<af:textBox classNameBase="input" title="Codice fiscale azienda appartenente al gruppo" type="text" name="strCFAZCapoGruppo" 
				value="<%=codFiscCapoGruppo%>" size="21" maxlength="16" readonly="<%=fieldReadOnly%>" />
		</td>
	</tr>	
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">		
			<% 
			if (canModify) {
			%>	
				<input type="submit" class="pulsante" name="inserisci" value="Inserisci"/>
			<%
			}
			%>
			<input type="button" class="pulsante" name="torna" value="Chiudi" onclick="window.close();" />
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>