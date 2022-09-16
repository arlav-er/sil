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
	String prgAzienda = null;
	String prgUnita = null;
	String visualizza_display = "none";
	String codiceFiscale = "", pIva="", ragSociale="";
	String statoAttoRich="", dataRichiestaDa="", dataRichiestaA="", statoAttoDocRich="";
	
	String titolo = "Ricerca Richieste di Gradualità";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

  	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  	PageAttribs attributi = new PageAttribs(user, _page); 

  	boolean canInsert = false; 

  	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		canInsert = attributi.containsButton("INSERISCI"); 
  	}

	String prgAziendaApp = (String)serviceRequest.getAttribute("prgAzienda");
	
	prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
	prgUnita = (String)serviceRequest.getAttribute("prgUnita");
	if (prgAzienda!=null) {
		attributi = new PageAttribs(user, _page);	
		// bisogna nascondere la sezione di ricerca del lavoratore		
	}
	else {
	// se provengo dalla lista avro' nella request i dati ricercati
		codiceFiscale = Utils.notNull(serviceRequest.getAttribute("strCodiceFiscale"));
		pIva          = Utils.notNull(serviceRequest.getAttribute("strPartitaIva"));
		ragSociale    = Utils.notNull(serviceRequest.getAttribute("strRagioneSociale"));
	}
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	// CONTROLLO PERMESSI SULLA PAGINA
	// PageAttribs attributi = new PageAttribs(user, _page);

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<af:linkScript path="../../js/"/>
<script>
	
    <% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>


	<% if (prgAzienda!=null && prgUnita!=null) { %>
		window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	<% } %>
	<%-- if (prgAzienda == null) { %>
		if (window.top.menu != undefined){
	  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
		}
	<% } else {%>
		if (window.top.menu != undefined){
				window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=1";
		}
	<% } --%>       
</script>
<script language="Javascript">


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
		
		var opened = "";
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
		
		function ControllaDate(){
 			var datRichiestaDa = document.Frm1.datRichiesta_Da;
 			var datRichiestaA = document.Frm1.datRichiesta_A;
    		if(datRichiestaDa.value != "" && datRichiestaA.value!= ""){
    			if (compareDate(datRichiestaDa.value,datRichiestaA.value) > 0) {
      				alert("La " + datRichiestaA.title + " deve essere maggiore della " + datRichiestaDa.title);
      				return false;
	  			}
	  		}
	  		return true;
	   	}
	  	

</script>	
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors/>

<p class="titolo"><%= titolo %></p>

<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="ControllaDate()">
<input type="hidden" name="PAGE"         value="CMRichGradListaPage" />
<input type="hidden" name="cdnFunzione"  value="<%=cdnfunzione%>" />

<input type="hidden" name="prgAziendaApp" value="<%=prgAziendaApp%>">
<% if (prgAzienda != null) {%>
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<%} %>

 <%= htmlStreamTop %>     
   <table class="main" border="0">
  <% if (prgAzienda == null || prgAzienda.equals("")) { %>  
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
		<table id='aziendaSez' style='display:<%=visualizza_display%>'>   
    	<script>new Sezione(document.getElementById('aziendaSez'),document.getElementById('tendinaAzienda'),false);</script>
			<tr>
				<td class="etichetta">Codice Fiscale</td>
				<td class="campo">
					<input type="text" name="strCodiceFiscale" class="inputView" readonly="true" value="<%=codiceFiscale%>" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="strPartitaIva" class="inputView" readonly="true" value="<%=pIva%>" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="strRagioneSociale" class="inputView" readonly="true" value="<%=ragSociale%>" size="75" maxlength="120"/>
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
		    <td class="etichetta">Stato richiesta</td>
		    <td class="campo">
		      <af:comboBox name="statoAttoRich" moduleName="M_COMBO_STATO_RICH_GRAD" addBlank="true" selectedValue="<%=statoAttoRich%>"/>
		    </td>
		<tr>
		<tr>
		    <td class="etichetta">Stato del documento</td>
		    <td class="campo">
		      <af:comboBox name="statoAttoDocRich" moduleName="COMBO_STATO_ATTO_RICH_GRAD" addBlank="true" selectedValue="<%=statoAttoDocRich%>"/>
		    </td>
		<tr>					
		<tr>
			<td class="etichetta">Data richieste da</td>
			<td class="campo">
				<af:textBox type="date" title="Data richieste da" name="datRichiesta_Da" 
				value="<%=dataRichiestaDa %>" size="12" maxlength="10" validateOnPost="true" />
				&nbsp;&nbsp;a&nbsp;&nbsp;
				<af:textBox type="date" title="Data richieste a" name="datRichiesta_A" 
				value="<%=dataRichiestaA%>" size="12" maxlength="10" validateOnPost="true" />
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
	<input type="hidden" name="PAGE"              	value="CMRichGradPage" />
	<input type="hidden" name="cdnFunzione"       	value="<%= cdnfunzione %>" />
	<input type="hidden" name="nuovo"             	value="true" />
	<input type="hidden" name="salva"             	value="false" />
	<input type="hidden" name="CODSTATOATTO_P"      value="NP" /> <%-- PA --%>
	<input type="hidden" name="CODSTATORICHIESTA"   value="DA" />
	<input type="hidden" name="goBackListPage"      value="CMRichGradRicercaPage" />
	<input type="hidden" name="nuovaRichiesta"      value="1" />	
	<% if (prgAzienda != null){ %>
	<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
	<% } %>
	
	<center><input type="submit" class="pulsanti" name="inserisci" value="Nuova Richiesta" /></center>
</af:form>
<% } %>
<%= htmlStreamBottom %>

</body>
</html>