<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
// La presenza del prgAzienda mi serve per capire se provengo dal menu generale o dal menu contestuale dell'unita azienda.	

	String titolo = "Ricerca Convenzioni";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

	PageAttribs attributi = new PageAttribs(user, _page); 

	boolean canInsert = false; 

 	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
 	} else {
  		canInsert = attributi.containsButton("INSERISCI"); 
 	}
	
  	sessionContainer.delAttribute("COMEFROM");
  	sessionContainer.setAttribute("COMEFROM","CR");	
	
	String prgAziendaApp = (String)serviceRequest.getAttribute("prgAzienda");
	String prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String)serviceRequest.getAttribute("prgUnita");

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	String PROVINCIA_ISCR = serviceRequest.getAttribute("PROVINCIA_ISCR") == null ? "": (String) serviceRequest.getAttribute("PROVINCIA_ISCR");

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca Convenzioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="Javascript">
		
		<% if (prgAzienda!=null && prgUnita!=null) { %>
			window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<% } else { %>
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
		<% }%>	
		
		var imgChiusa = "../../img/chiuso.gif";
		  var imgAperta = "../../img/aperto.gif";	
		  
		  function cambia(immagine, sezione) {
		 	if (sezione.aperta==null) {
				sezione.aperta=true;
			}
			if (document.Frm1.strCodiceFiscale.value!="" || document.Frm1.strPartitaIva.value != "" || 
				document.Frm1.strRagioneSociale.value!= "" ){
					if (sezione.aperta) {
						sezione.style.display="inline";
						sezione.aperta=false;
						immagine.src="../../img/aperto.gif";
					}
					else {
						sezione.style.display="none";
						sezione.aperta=true;
						immagine.src="../../img/chiuso.gif";
					}
			}
		  }	
		function Sezione(sezione, img,aperta){ 
			this.sezione=sezione;
	    	this.sezione.aperta=aperta;
	    	this.img=img;
		}
		
		function apriAzienda() {
    		var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDati&cdnFunzione=<%=cdnfunzione%>";
        	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
					   "width=600,height=500,top=50,left=100";
    		opened = window.open(url, "_blank", feat);
    		opened.focus();
		}
		
		function riempiDati(prgAziendaApp, strCodiceFiscale, strPartitaIva, strRagioneSociale) 
		{
			document.Frm1.prgAziendaApp.value = prgAziendaApp;
			document.Frm1.strCodiceFiscale.value = strCodiceFiscale;
			document.Frm1.strPartitaIva.value = strPartitaIva;
			document.Frm1.strRagioneSociale.value = strRagioneSociale;
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "inline";
			imgV.src=imgAperta;
			opened.close();
        }
		
		function ripristina(){
			var sezione = document.getElementById("aziendaSez");
			var image = document.getElementById("tendinaAzienda");
	   		sezione.style.display="none";
	   		image.src="../../img/chiuso.gif";
   		}
   		
   		function azzeraAzienda (){
   			document.Frm1.prgAziendaApp.value = '';
   			document.Frm1.strRagioneSociale.value = '';
			document.Frm1.strCodiceFiscale.value = '';
			document.Frm1.strPartitaIva.value = '';
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "none";
			imgV.src=imgChiusa;
		}
		
  		function controlloDate(){
  			var objDataDa = document.Frm1.datStipula_Da;
    		var objDataA = document.Frm1.datStipula_A;    
			if ((objDataDa.value != "") && (objDataA.value != "")) {
      			if (compareDate(objDataDa.value,objDataA.value) > 0) {
      				alert(objDataDa.title + " maggiore di " + objDataA.title);
      				objDataDa.focus();
	    			return false;
	  			}	
			}
			objDataDa = document.Frm1.datScad_Da;
    		objDataA = document.Frm1.datScad_A;    
			if ((objDataDa.value != "") && (objDataA.value != "")) {
      			if (compareDate(objDataDa.value,objDataA.value) > 0) {
      				alert(objDataDa.title + " maggiore di " + objDataA.title);
      				objDataDa.focus();
	    			return false;
	  			}	
			}
    		return true;
  		}
  		
</script>	
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors/>

<p class="titolo"><%=titolo%></p>

<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="controlloDate()">
<input type="hidden" name="PAGE"          value="CMConveListaPage" />
<input type="hidden" name="cdnFunzione"   value="<%=cdnfunzione%>" />
<% if (prgAziendaApp != null) {%>
<input type="hidden" name="prgAziendaApp" value="<%=prgAziendaApp%>">
<%} else {%>
<input type="hidden" name="prgAziendaApp" value="">
<%} %>
<% if (prgAzienda != null) {%>
<input type="hidden" name="prgAzienda" 	  value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<%} %>

<%= htmlStreamTop %> 
<table class="main" border="0">    
 <% if (prgAziendaApp == null) { %> 
   <tr class="note">
   	<td colspan="2">
   	<div class="sezione2">
    <img id='tendinaAzienda' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this,document.getElementById("aziendaSez"))'/>&nbsp;&nbsp;&nbsp;Azienda
       																													
         &nbsp;&nbsp;
  		<% if (!StringUtils.isFilled(prgAzienda)) { %> 
			<a href="#" onClick="apriAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
  		<% } %>
 		&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
     </td>
     </tr>
     <tr>
		<td colspan="2">
		<table id='aziendaSez' style='display:none'>   
    	<script>new Sezione(document.getElementById('aziendaSez'),document.getElementById('tendinaAzienda'),false);</script>
			<tr>
				<td class="etichetta">Codice Fiscale</td>
				<td class="campo">
					<input type="text" name="strCodiceFiscale" class="inputView" readonly="true" value="" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="strPartitaIva" class="inputView" readonly="true" value="" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="strRagioneSociale" class="inputView" readonly="true" value="" size="75" maxlength="120"/>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<% } %>
	
	<table class="main" border="0">
	<tr><td colspan="2">&nbsp;</td></tr>
	    <tr><td colspan="2" ><div class="sezione2"/></td></tr>
		<tr><td colspan="2">&nbsp;</td></tr>	    	    	
		<tr>
		    <td class="etichetta">Ambito Territoriale</td>
		    <td colspan=3 class="campo">
		    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
		        	classNameBase="input" addBlank="true" />
		    </td>
		</tr>	
		<tr>
		    <td class="etichetta">Stato Convenzione</td>
		    <td class="campo">
		      <af:comboBox name="statoConv" multiple="true" size="5" moduleName="CM_MULTI_LIST_CONVENZIONI" addBlank="true" selectedValue=""/>
		    </td>
		<tr>
		<tr>
			<td class="etichetta">Data stipula da</td>
			<td class="campo">
				<af:textBox type="date" title="Data stipula da" name="datStipula_Da"
				value="" size="12" maxlength="10" validateOnPost="true" />
				&nbsp;&nbsp;a&nbsp;&nbsp;
				<af:textBox type="date" title="Data stipula a" name="datStipula_A"
				value="" size="12" maxlength="10" validateOnPost="true" />
		    </td>
		</tr>
		<tr>
			<td class="etichetta">Data scadenza da</td>
			<td class="campo">
				<af:textBox type="date" title="Data scadenza da" name="datScad_Da"
				value="" size="12" maxlength="10" validateOnPost="true" />
				&nbsp;&nbsp;a&nbsp;&nbsp;
				<af:textBox type="date" title="Data scadenza a" name="datScad_A"
				value="" size="12" maxlength="10" validateOnPost="true" />
		    </td>
		</tr>
		<tr>
			<td class="etichetta">Numero</td>
			<td class="campo">
				<af:textBox type="integer" title="numero" name="numero"
				value="" size="12" maxlength="10" validateOnPost="true" />
		    </td>
		</tr>
		<tr>
			<td class="etichetta">Anno</td>
			<td class="campo">
				<af:textBox type="integer" title="anno" name="anno"
				value="" size="4" maxlength="4" validateOnPost="true" />	
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Con assunzioni previste&nbsp;
				<input type="checkbox" name="conAssPrev" />
			</td>					    
		</tr>				
		<tr><td colspan=2>&nbsp;</td></tr>
		<tr>
			<td colspan="2">
					<input type="submit" class="pulsanti" name="cerca" value="Cerca" />
					&nbsp;&nbsp;
					<input type="reset" class="pulsanti" name="annulla" value="Annulla" onClick="ripristina()"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">					
			</td>
		</tr>
	</table>
</af:form>
<% if (canInsert) { %>
<af:form name="Frm2" method="POST" action="AdapterHTTP" >
	<input type="hidden" name="PAGE"              	value="CMInsConvenzionePage"/>
	<input type="hidden" name="cdnFunzione"       	value="<%=cdnfunzione%>"/>
	<input type="hidden" name="anno" value=""/>
	<script language="Javascript">
		var miaData = new Date();
		var anno = miaData.getFullYear();
		document.Frm2.anno.value = anno;
	</script>
	<input type="hidden" name="CODSTATOATTO"      	value="NP" />
	<input type="hidden" name="CODSTATOCONV"   value="PO" />
	<input type="hidden" name="goBackRicercaPage"   value="CMConveRicercaPage" />
	<% if (prgAzienda != null){ %>
	<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
	<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
	<% } %>
	
	<center><input type="submit" class="pulsanti" name="nuova" value="Nuova convenzione" /></center>
</af:form>
<% } %>
<%= htmlStreamBottom %>

</body>
</html>