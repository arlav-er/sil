<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

 String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 String prgAzienda = null;
 String prgUnita = null; 
 String visualizza_display = "none";
 
 String cercaAzienda     = StringUtils.getAttributeStrNotNull(serviceRequest,"cercaAzienda");
 
 String STRRAGIONESOCIALE = "";
 String STRCODICEFISCALE = "";
 String STRPARTITAIVA = "";
 String DATINIZIOSOSP_DAL = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizio"); 
 String DATINIZIOSOSP_AL = StringUtils.getAttributeStrNotNull(serviceRequest,"dataFine");
 String CODSTATORICHIESTA = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATORICHIESTA");   
 String CODSTATOATTORICH = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTORICH");
 
 String  _page  = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGE"); 
 
 ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

 PageAttribs attributi = new PageAttribs(user, _page); 

 boolean canInsert = false; 

 if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
 } else {
  	canInsert = attributi.containsButton("INSERISCI"); 
 }

 String prgRichSospensione = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGRICHSOSPENSIONE");
 
 Vector tipiMotivi = serviceResponse.getAttributeAsVector("M_ComboMotivi.ROWS.ROW"); 
 
 String prgAziendaApp = null;

 prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");

	prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
	if (prgAzienda!=null) {	
		// bisogna nascondere la sezione azienda	
	}
	else {
		// se provengo dalla lista avro' nella request i dati ricercati
		STRCODICEFISCALE  = Utils.notNull(serviceRequest.getAttribute("aziCodFiscale"));
		STRPARTITAIVA     = Utils.notNull(serviceRequest.getAttribute("aziPIva"));
		STRRAGIONESOCIALE = Utils.notNull(serviceRequest.getAttribute("aziRagSociale"));	
	} 
  
 String strFlgDatiOk = "";
 String IndirizzoAzienda = "";
 String descrTipoAz = "";
 String codTipoAz = "";
 String codnatGiurAz = "";
 String strFlgCfOk = "";
 
%>

<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	 <title>Ricerca richieste sospensione</title>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">
     
     <% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
     }%>
	</script>
	<script language="Javascript">

		<% if (prgAzienda!=null) { %>
			window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<% } %>
	
	<%-- if (prgAzienda==null) { %>
	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
	<% } else {%>
		if (window.top.menu != undefined){
  			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=1";
		}
	<% } --%>
	      
	  
			
		  var imgChiusa = "../../img/chiuso.gif";
		  var imgAperta = "../../img/aperto.gif";	
		  
		  function cambia(immagine, sezione) {
		 	if (sezione.aperta==null) {
				sezione.aperta=true;
			}
			if (document.Frm1.aziCodFiscale.value!="" || document.Frm1.aziCodFiscale.value != "" || 
				document.Frm1.aziRagSociale.value!= "" ){
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
		
		function checkDate (strdata1, strdata2) {
			annoVar1 = strdata1.substr(6,4);
			meseVar1 = strdata1.substr(3,2);
			giornoVar1 = strdata1.substr(0,2);
			dataVarInt1 = parseInt(annoVar1 + meseVar1 + giornoVar1, 10);
			annoVar2 = strdata2.substr(6,4);
			meseVar2 = strdata2.substr(3,2);
			giornoVar2 = strdata2.substr(0,2);
			dataVarInt2 = parseInt(annoVar2 + meseVar2 + giornoVar2, 10);
			 
			if (dataVarInt1 < dataVarInt2) {
			     return 1;
			}
			else {
			    if (dataVarInt1 > dataVarInt2) {
			      return 2;
			      }
			      else {
			        return 0;
			      }
			  }
		  }
		  		  
		  function cercaRichiesta(esisteAz){
			var url;
 		    url = "AdapterHTTP?PAGE=CMRichSospListaPage&fromRicerca=1";
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    url = url + "&prgRichSospensione=<%=prgRichSospensione%>";
 		    <% if (prgAzienda != null) { %>
 		    	url = url + "&prgAzienda=<%=prgAzienda%>";
 		    	url = url + "&prgUnita=<%=prgUnita%>";
 		    <%}%>
 		    if(esisteAz!="S"){
				url = url + "&ragioneSoc=" + document.Frm1.aziRagSociale.value;
				url = url + "&CodFisc=" + document.Frm1.aziCodFiscale.value;
				url = url + "&PIVA=" + document.Frm1.aziPIva.value;
			}
 		    url = url + "&DATINIZIOSOSP_DAL=" + document.Frm1.DATINIZIOSOSP_DAL.value;
 		    url = url + "&DATINIZIOSOSP_AL=" + document.Frm1.DATINIZIOSOSP_AL.value;
 		    url = url + "&CODSTATORICHIESTA=" + document.Frm1.CODSTATORICHIESTA.value;
 		    url = url + "&CODSTATOATTORICH=" + document.Frm1.CODSTATOATTORICH.value;
 		    url = url + "&motiviSosp=" + document.Frm1.motiviSosp.value;
 		    //url = url + "&PRGRICHSOSPENSIONE=" + document.Frm1.PRGRICHSOSPENSIONE.value;
			
			var motivoStr = '';
 			var contSelezionati = 0;
 			for (i=0;i<document.Frm1.motivi.length;i++) {
   				if (document.Frm1.motivi[i].selected && document.Frm1.motivi[i].value != '') {
   					contSelezionati = contSelezionati + 1;
  					if (motivoStr == '') {
						motivoStr = motivoStr + document.Frm1.motivi[i].value;
					}
					else {
						motivoStr = motivoStr + ',' + document.Frm1.motivi[i].value;
					}
		 		}
			}
			url = url + "&CODMOTIVOSOSP=" + motivoStr;
		
			// Controllo date
			var datSospDa = document.Frm1.DATINIZIOSOSP_DAL.value;
			var datSospA = document.Frm1.DATINIZIOSOSP_AL.value;
			
			var esitoData = false; 
			
			if((datSospDa != "") && (datSospA != "")){
				var date = checkDate(datSospDa,datSospA);
				if(date==1){
					esitoData = true;
				}
			}

			if((datSospDa == "") && (datSospA == "")){
			 	esitoData = true;
			}

			if(esitoData){
		    	document.location = url;
			}else{
				alert("La " + document.Frm1.DATINIZIOSOSP_AL.title + " deve essere maggiore della " + document.Frm1.DATINIZIOSOSP_DAL.title);
			}			
		  }

		  function nuovaRichiesta(){
			var url;
			url = "AdapterHTTP?PAGE=CMRichSospDettPage&fromRicerca=1&nuovaRichiesta=1";
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    <% if (prgAzienda != null) { %>
 		    	url = url + "&prgAzienda=<%=prgAzienda%>";
 		    	url = url + "&prgUnita=<%=prgUnita%>";
 		    <%}%>
 		   	url = url + "&ragioneSoc=<%=STRRAGIONESOCIALE%>";
			url = url + "&CodFisc=<%=STRCODICEFISCALE%>";
			url = url + "&PIVA=<%=STRPARTITAIVA%>";
			url = url + "&goBackListPage=CMRichSospRicercaPage";
			
			setWindowLocation(url);
		  }
		
		function apriAzienda() {
    		var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDati&cdnFunzione=<%=_funzione%>";
        	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
					   "width=600,height=500,top=50,left=100";
    		opened = window.open(url, "_blank", feat);
    		opened.focus();
		}
		
		function riempiDati(prgAziendaApp, aziCodFiscale, aziPIva, aziRagSociale) 
		{
			document.Frm1.prgAziendaApp.value = prgAziendaApp;
			document.Frm1.aziCodFiscale.value = aziCodFiscale;
			document.Frm1.aziPIva.value = aziPIva;
			document.Frm1.aziRagSociale.value = aziRagSociale;
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
   			document.Frm1.aziRagSociale.value = '';
			document.Frm1.aziCodFiscale.value = '';
			document.Frm1.aziPIva.value = '';
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "none";
			imgV.src=imgChiusa;
		}
</script>

	    
</head>
<body class="gestione" onload="rinfresca()">

  <%if(!cercaAzienda.equals("")){%>
		<br><br>
		<af:list moduleName="M_CercaAzienda"  skipNavigationButton="0" jsSelect="AggiornaForm"/>
		
	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
        </table>
  <%
  }else{
  %>
	
  <p class="titolo">Ricerca richiesta di sospensione</p>
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
  
  <% if (prgAzienda != null) { %>
  <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
   <% } %>
  <input type="hidden" name="prgAziendaApp" value="<%=prgAziendaApp%>">
  <input type="hidden" name="goBackListPage" value="CMRichSospRicercaPage" >
  
	
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
  		<%}%>
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
					<input type="text" name="aziCodFiscale" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="aziPIva" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="aziRagSociale" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<% } %>
	
	<table class="main" border="0">
	<tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	    <tr><td colspan="2" ><div class="sezione2"/></td></tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>	    	    	
		<td class="etichetta">Data inizio sospensione dal</td>
           	  <td class="campo">
          	    <af:textBox validateOnPost="true" type="date" name="DATINIZIOSOSP_DAL" value="<%=DATINIZIOSOSP_DAL%>" title="Data inizio sospensione dal" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	     	    al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="DATINIZIOSOSP_AL" value="<%=DATINIZIOSOSP_AL%>" title="Data inizio sospensione al" size="10" maxlength="10" />
          	  </td>
          	</tr>
			<tr>
			    <td class="etichetta">Stato richiesta</td>
			    <td class="campo">
			      <af:comboBox title="Stato" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_SOSP" addBlank="true" selectedValue="<%=CODSTATORICHIESTA%>"/>
			    </td>
			<tr>
			<tr>
			    <td class="etichetta">Stato del documento</td>
			    <td class="campo">
			      <af:comboBox title="Stato" name="CODSTATOATTORICH" moduleName="COMBO_STATO_ATTO_RICH_CM_SOSP" addBlank="true" selectedValue="<%=CODSTATOATTORICH%>"/>
			    </td>
			<tr>
	    	<tr >
	    		<td class="etichetta">Motivo della sospensione</td>
			    <td class="campo">
			      <input type="hidden" name="motiviSosp"/>
			      <af:comboBox name="motivi" moduleName="M_ComboMotivi" multiple="true" addBlank="true" />
			    </td>	
    		</tr>				        
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr>
	          <td colspan="2" align="center">
	          
	              <%
		          String esisteAz = "";
		          if (prgAzienda == null || prgAzienda.equals("")) { 
		          		esisteAz = "N";
		          } else {
		            	esisteAz = "S";
		          }%> 
		          
		          <input class="pulsanti" type="button" name="cerca" value="Cerca" onClick="cercaRichiesta('<%=esisteAz%>');"/>
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" onClick="ripristina()" />
	          </td>
	        </tr>
	        <% if (canInsert) { %>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr>
	          <td colspan="2">
		        <input type="button" class="pulsanti" value="Nuova richiesta" onClick="nuovaRichiesta();"/>
	          </td>
	        </tr>
	        <% } %>
	      </table>

      </af:form>
 <% }//else %>
	
	<%out.print(htmlStreamBottom);%>
	</body>
</html>

