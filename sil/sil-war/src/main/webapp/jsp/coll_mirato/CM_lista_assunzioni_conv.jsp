<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	//progressivo della convenzione dal quale recupero SEMPRE tutti i dati necessari e indispensabile per le linguette	
	String prgConv	  = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCONV");
		
	String  _page  	  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	
	String prgConvDettaglio   = "";
	String codStato		      = "";
	String numLavoratori      = "1";
    String codMonoCategoria   = "";
    String codMonoTipo		  = "";
    String cdnlavoratore      = "";
	String datScadenza		  = "";
    String codMansione		  = "";
    String strMansione        = "";
    String codContratto       = "";
    String numggProlungProva  = "";
    String strNote            = "";
    String flgInnalzLimiti    = "";
    String codMonoLavProgram  = "";
    String codiceFiscaleLav   = "";
    String cognomeLav         = "";
    String nomeLav            = "";
    Vector tipiAgevolazioni   = null;
    		
    			
	// ------------------------------------------------------------------------------- 

	SourceBean infoAz = (SourceBean) serviceResponse.getAttribute("CM_INFO_AZ_DA_CONV.ROWS.ROW");
	
	String prgAzienda 			= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(infoAz, "PRGAZIENDA", null));
	String prgUnita             = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(infoAz, "PRGUNITA", null));
	String statoConv			= Utils.notNull(infoAz.getAttribute("CODSTATORICHIESTA"));
	String ragSociale			= Utils.notNull(infoAz.getAttribute("ragionesociale"));
	String codiceFiscale 		= Utils.notNull(infoAz.getAttribute("cf"));
	String pIva					= Utils.notNull(infoAz.getAttribute("piva")); 
	String indirizzo            = Utils.notNull(infoAz.getAttribute("strIndirizzo"));
	
	PageAttribs attributi = new PageAttribs(user, _page);
		
	//conttrollo sulla profilatura
	boolean canInsert = attributi.containsButton("INSERISCI");
	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canDelete = attributi.containsButton("RIMUOVI");
	
	String fieldReadOnly = canModify ? "false" : "true";
	String btnChiudiDettaglio = canModify ? "Chiudi senza aggiornare" : "Chiudi";
	String fieldcodMonoLavProgram = "true";
	
	boolean nuovo = true;
  	if(serviceResponse.containsAttribute("CM_LOAD_CONV_ASSUNZIONI")) { 
    	nuovo = false; 
  	}
  	else { 
    	nuovo = true; 
  	}

	String displayReitera = "none";
	String reitera = (String) serviceRequest.getAttribute("REITERA");
	if(reitera == null) { 
		displayReitera = "none"; 
	}
  	else { 
  		displayReitera = "";    
  	} 
	
  	String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  	if(apriDiv == null) { 
  		apriDiv = "none"; 
  	} else { 
  		String paramModule = (String) serviceRequest.getAttribute("MODULE");
  		if(paramModule.equalsIgnoreCase("CM_LISTA_CONV_ASSUNZIONI")) { 
			if (serviceRequest.containsAttribute("url_nuovo")) {
				apriDiv = "";
				serviceRequest.delAttribute("url_nuovo");
			} else {
				apriDiv = "none";
			} 
  		} else {  		
  			apriDiv = ""; 
  		}
  	}  	 	   
    
	if(!nuovo) {
  		//Sono in modalità dettaglio
  		Vector vectConoInfo= serviceResponse.getAttributeAsVector("CM_LOAD_CONV_ASSUNZIONI.ROWS.ROW");
  		if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    		SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

			prgConvDettaglio   = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(beanLastInsert, "PRGCONVDETTAGLIO", null));
			prgConv            = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(beanLastInsert, "PRGCONV", null));
			codStato		   = Utils.notNull(beanLastInsert.getAttribute("CODSTATO"));
			numLavoratori      = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(beanLastInsert, "NUMLAVORATORI", null));
    		codMonoCategoria   = Utils.notNull(beanLastInsert.getAttribute("CODMONOCATEGORIA"));
    		codMonoTipo		   = Utils.notNull(beanLastInsert.getAttribute("CODMONOTIPO"));
    		cdnlavoratore      = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(beanLastInsert, "CDNLAVORATORE", null));
			if (!cdnlavoratore.equals("")) {
				codiceFiscaleLav   = Utils.notNull(beanLastInsert.getAttribute("STRCODICEFISCALE"));
    			cognomeLav         = Utils.notNull(beanLastInsert.getAttribute("STRCOGNOME"));
    			nomeLav            = Utils.notNull(beanLastInsert.getAttribute("STRNOME"));
    			codMonoLavProgram  = Utils.notNull(beanLastInsert.getAttribute("CODMONOLAVPROGRAM"));
    			fieldcodMonoLavProgram = "false";
			}
			datScadenza		   = Utils.notNull(beanLastInsert.getAttribute("DATSCADENZA"));
    		codMansione		   = Utils.notNull(beanLastInsert.getAttribute("CODMANSIONE"));
			if (!codMansione.equals("")) {
				strMansione    = Utils.notNull(beanLastInsert.getAttribute("STRMANSIONE"));
			}    		
    		codContratto       = Utils.notNull(beanLastInsert.getAttribute("CODCONTRATTO"));
    		numggProlungProva  = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(beanLastInsert, "NUMGGPROLUNGPROVA", null));
    		flgInnalzLimiti    = Utils.notNull(beanLastInsert.getAttribute("FLGINNALZAMENTOLIMITI"));
    		strNote            = Utils.notNull(beanLastInsert.getAttribute("STRNOTE"));
    	}
    	tipiAgevolazioni = serviceResponse.getAttributeAsVector("CM_COMBO_AGEVOLAZIONI.ROWS.ROW");
	}
	
	if(statoConv.equals("AN")){
	  	canInsert = false;
	  	canModify = false;
	  	canDelete = false;
	  	fieldReadOnly = "true";
	  	fieldcodMonoLavProgram = "true";
	  	btnChiudiDettaglio = "Chiudi";
	}
	
	String listPage = "1";
	if (serviceRequest.containsAttribute("LIST_PAGE")) {
		listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
	}
	
  	String url_nuovo = "AdapterHTTP?PAGE=CMAssunzConvPage&url_nuovo=true" + 
                     "&MODULE=CM_LISTA_CONV_ASSUNZIONI&MESSAGE=LIST_PAGE&LIST_PAGE=" + listPage +
                     "&PRGCONV=" + prgConv + 
                     "&CDNFUNZIONE=" + cdnfunzione + 
                     "&APRIDIV=1";

	//LINGUETTE
	Linguette l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgConv));
	l.setCodiceItem("PRGCONV");

	//LAYOUT DI PAGINA
	
	String htmlStreamTopAz = StyleUtils.roundTopTable(false);
  	String htmlStreamBottomAz = StyleUtils.roundBottomTable(false);
  	
  	String divStreamTop = StyleUtils.roundLayerTop(canInsert || canModify);
	String divStreamBottom = StyleUtils.roundLayerBottom(canInsert || canModify);

%>

<html>
<head>
<title>Lista delle assunzioni </title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<SCRIPT language="JavaScript" src="../../js/CommonXMLHTTPRequest.js"></SCRIPT>

<script language="Javascript">
<% if (!prgAzienda.equals("") && !prgUnita.equals("")) { %>
	if (window.top.menu != undefined){
		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>";	
	}
<% } %>

var flagChanged = false;
        
function fieldChanged() 
{ <% if (canModify) { %>
        //alert("CAMBIATO!!");
        flagChanged = true;
  <% } %>
}


function Select(prgConvDettaglio) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CMAssunzConvPage";
    s += "&MODULE=CM_LOAD_CONV_ASSUNZIONI";
    s += "&PRGCONV=<%= prgConv %>";
    s += "&MESSAGE=LIST_PAGE";
    s += "&LIST_PAGE=<%=listPage%>";
    s += "&PRGCONVDETTAGLIO=" + prgConvDettaglio;
    s += "&CDNFUNZIONE=<%= cdnfunzione%>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);
  }

function Delete(prgConvDettaglio) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere tale assunzione ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=CMAssunzConvPage";
      s += "&MODULE=CM_DEL_CONV_ASSUNZIONI";
      s += "&PRGCONV=<%= prgConv %>";
	  s += "&MESSAGE=LIST_PAGE";
	  s += "&LIST_PAGE=<%=listPage%>";      
      s += "&PRGCONVDETTAGLIO=" + prgConvDettaglio;
      s += "&CDNFUNZIONE=<%= cdnfunzione%>";

      setWindowLocation(s);
    }
  }  
  
function chiudiLayer(divLayer) {

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     } else { 
         // Vogliamo chiudere il layer. 
         // Pongo il flag = false per evitare che mi venga riproposto 
         // un "confirm" quando poi navigo con le linguette nella pagina principale
         flagChanged = false;
     }
     
  }
  if (ok) {
     var objDiv = document.getElementById(divLayer);
	 if (objDiv.id == 'divLayerDett') {
	 	ChiudiDivLayer('divLayerDett');
	 } 
	 else if (objDiv.id == 'divLayerAgeDett') {
     	ChiudiDivLayer('divLayerAgeDett');
     }
     else if (objDiv.id == 'divLayerReitera') {
     	ChiudiDivLayer('divLayerReitera');
     }
  }
}

function selLavIscrCM(context, funzSetLav)
{
	var url = "AdapterHTTP?PAGE=SelLavNOPage&fromWhere=" + context + "&AGG_FUNZ=" + funzSetLav + "&cdnFunzione=<%=cdnfunzione%>";
	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
	     	   "width=600,height=500,top=50,left=100";
	opened = window.open(url, "_blank", feat);
	opened.focus();
}

function riempiDatiLavIscrCM(cdnLavoratore, strCodiceFiscaleLav, strCognome, strNome, flgCfOk) {
  	document.Frm1.CDNLAVORATORE.value = cdnLavoratore;
  	document.Frm1.codiceFiscale.value = strCodiceFiscaleLav;
  	document.Frm1.cognome.value = strCognome;
  	document.Frm1.nome.value = strNome;
  	document.Frm1.CODMONOLAVPROGRAM.disabled = false;
  	opened.close();
}

function azzeraLavIscrCM(){
  	document.Frm1.CDNLAVORATORE.value = "";
  	document.Frm1.codiceFiscale.value = "";
    document.Frm1.cognome.value = "";
    document.Frm1.nome.value = "";
    document.Frm1.CODMONOLAVPROGRAM.value = "";
    document.Frm1.CODMONOLAVPROGRAM.disabled = true;
} 

function checkMansione(nameMansione) {
	var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
	if (cod == "") return true;
	if (cod.substring(4, 6) == '00') {
		if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
			return controllaQualificaOnSubmit(nameMansione);
		}
	} else return controllaQualificaOnSubmit(nameMansione);
}

function controllaQualificaOnSubmit(campoQualifica) {
	var qualifica = new String(eval('document.Frm1.' + campoQualifica + '.value'));
	var exist = false;
	try {
		exist = controllaEsistenzaChiave(qualifica, "CODMANSIONE", "DE_MANSIONE");
	} catch (e) {
		return confirm("Impossibile controllare che la qualifica " + qualifica + " esista, proseguire comunque?");
	}
	if (!exist) {
		alert("Il codice della qualifica " + qualifica + " non esiste");
		return false;
	} else return true;
}

function selectMansionePerDescrizione(desMansione) {
	window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
}

function selectMansioneOnClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
  if (codMansione.value==""){
    descMansione.value="";
    strTipoMansione.value="";      
  }
  else if (codMansione.value!=codMansioneHid.value){
  window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
  }
}

function ControllaNumLav() {
	if (document.Frm1.CDNLAVORATORE.value != "") {
		var numLav = parseInt(document.Frm1.numLavoratori.value);
		if (numLav > 1 || numLav == 0) {
			alert("In caso di assunzione di un determinato lavoratore, il numero deve essere pari ad 1!");
			return false;
		}
	} 
	return true;
}

function checkLavProg() {
<% if (fieldcodMonoLavProgram.equals("true")) { %>
	document.Frm1.CODMONOLAVPROGRAM.disabled = true;
<% } else { %>
	document.Frm1.CODMONOLAVPROGRAM.disabled = false;
<% } %>
}

function checkStato() {
	var numLav = parseInt(document.Frm1.numLavoratori.value);
	if (numLav == 1 && document.Frm1.CDNLAVORATORE.value != "") {
		if (document.Frm1.datScadenza.value != "") {
			var f = "AdapterHTTP?PAGE=CMMovAssunzPage&CDNFUNZIONE=" + <%=cdnfunzione%> + 
			"&CDNLAVORATORE=" + document.Frm1.CDNLAVORATORE.value + "&PRGAZIENDA=" + <%=prgAzienda%> + 
			"&DATSCADENZA=" + document.Frm1.datScadenza.value;
 			var t = "_blank";
    		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes," + 
    		"resizable=yes,width=750,height=450,top=30,left=180";
    		window.open(f, t, feat);
    	} else {
    		alert("Per il controllo dello stato è indispensabile indicare la prevista data di assunzione!");
    	}
	} else {
		alert("Per il controllo dello stato è indispensabile indicare un lavoratore!");
	}
}

function aggiungiAgevolazioni(){
  	if (flagChanged) { 
  		alert("I dati sono cambiati.\nSalvarli prima di gestire le agevolazioni!");
  	} else {
		apriNuovoDivLayer(true,'divLayerAgeDett','');
		document.location='#aLayerAgeIns';
	}
}

function inserisciAgevolazioni(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CMAssunzConvPage";
    s += "&MODULE=CM_LOAD_AGE_ASS";
    s += "&PRGCONV=<%= prgConv %>";
	s += "&MESSAGE=LIST_PAGE";
	s += "&LIST_PAGE=<%=listPage%>";    
    s += "&PRGCONVDETTAGLIO=<%= prgConvDettaglio %>";
    s += "&CDNFUNZIONE=<%= cdnfunzione%>";
    s+= "&APRIDIV=1";

	<%
		if (!nuovo) {
			for (int j = 0; j < tipiAgevolazioni.size(); j++) { //ciclo nella query
				SourceBean rowTipiAgevolazioni = (SourceBean)tipiAgevolazioni.get(j);
		
				String codTipiAgevolazioni = (String)rowTipiAgevolazioni.getAttribute("CODICE");
				String dscTipiAgevolazioni = (String)rowTipiAgevolazioni.getAttribute("DESCRIZIONE");
	%>
				var k = 0;
	<%
				for (int i = 1; i <= tipiAgevolazioni.size(); i++) { //ciclo nel javascript
	%>
					k++;
					if(document.Frm1.agevolazioni.options[<%=i%>].value == "<%=codTipiAgevolazioni%>" && document.Frm1.agevolazioni.options[<%=i%>].selected){
						var codice = document.Frm1.agevolazioni.options[<%=i%>].value;
						s += "&codAgevolazione"+k+"="+codice;	
					}	
	<%
				}
			}
	%>
    
    s += "&numRows=<%= tipiAgevolazioni.size()%>";
    <%	} %>
    setWindowLocation(s);
}

function DeleteAgevolazione(codAgevolazione) {
  	if (flagChanged) { 
  		alert("I dati sono cambiati.\nSalvarli prima di gestire le agevolazioni!");
  	} else {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
	    var s="Sicuri di voler rimuovere tale agevolazione ?";
	    
	    if ( confirm(s) ) {
	
	      var s= "AdapterHTTP?PAGE=CMAssunzConvPage";
	      s += "&MODULE=CM_DEL_AGE_ASS";
	      s += "&PRGCONV=<%= prgConv %>";
	      s += "&MESSAGE=LIST_PAGE";
	      s += "&LIST_PAGE=<%=listPage%>";
	      s += "&PRGCONVDETTAGLIO=<%= prgConvDettaglio %>";
	      s += "&CODAGEVOLAZIONE=" + codAgevolazione;
	      s += "&CDNFUNZIONE=<%= cdnfunzione%>";
		  s+= "&APRIDIV=1";
	      
	      setWindowLocation(s);
	    }
    }
} 

function reiteraAssunzioni() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	if (document.Frm1.numRigheReitera.value == '') {
		alert("Attenzione: Il numero di righe da reiterare è obbligatorio!");
	}
	else {
	
	    var s="Sicuri di voler reiterare l'assunzione selezionata?";
	    
	    if ( confirm(s) ) {
			
	    	var s= "AdapterHTTP?PAGE=CMAssunzConvPage";
	    	s += "&MODULE=CM_REITERA_CONV_ASSUNZIONI";
	    	s += "&PRGCONV=<%= prgConv %>";
	    	s += "&MESSAGE=LIST_PAGE";
	      	s += "&LIST_PAGE=<%=listPage%>";
	    	s += "&PRGCONVDETTAGLIO=<%= prgConvDettaglio %>";
	    	s += "&CDNFUNZIONE=<%= cdnfunzione%>";
	    	s += "&NUMRIGHEREITERA=" + document.Frm1.numRigheReitera.value;
	            	
	        setWindowLocation(s);
	    }
	}	   
} 
   
</script>
</head>

<body class="gestione" onload="rinfresca();checkLavProg();">
<af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaNumLav()">
    
<br>
<%out.print(htmlStreamTopAz);%>
<table class="main" border="0">
	<tr class="note">
    	<td colspan="2">
          <div class="sezione2">
            <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif"/>&nbsp;&nbsp;&nbsp;Azienda&nbsp;&nbsp;
          </div>
         </td>
    <tr>
         <td colspan="2">
            <div id="aziendaSez" style="display:">
              <table class="main" width="100%" border="0">
				      <tr>
				        <td class="etichetta">Codice Fiscale</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="cf" value="<%=codiceFiscale %>"
            				readonly="true" classNameBase="input" size="30" maxlength="16"/>
				        </td>
				      </tr>      
				      <tr>
				        <td class="etichetta">Partita IVA</td>
				        <td class="campo" colspan="3">
				   			<af:textBox type="text" name="piva" value="<%=pIva %>"
            				readonly="true" classNameBase="input" size="30" maxlength="11"/>       
				        </td>
				      </tr>		      
				      <tr>
				        <td class="etichetta">Ragione Sociale</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="RagioneSociale" value="<%=ragSociale %>"
            				readonly="true" classNameBase="input" size="60" maxlength="100"/> 
				        </td>
				      </tr>
				      <tr>
				        <td class="etichetta">Indirizzo (Comune)</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="Indirizzo" value="<%=indirizzo %>"
            				readonly="true" classNameBase="input" size="60" maxlength="100"/> 
				        </td>
				      </tr> 
				      <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
					  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />    
				</table>
    		</div>
    	</td>
    </tr>	
</table>  
<%out.print(htmlStreamBottomAz);%>
<p class="titolo">Lista delle assunzioni</p>

<!-- stampa delle linguette -->
<%l.show(out);%>

      <center>
        <font color="green">
          <af:showMessages prefix="CM_INS_CONV_ASSUNZIONI"/>
          <af:showMessages prefix="CM_UPD_CONV_ASSUNZIONI"/>
          <af:showMessages prefix="CM_DEL_CONV_ASSUNZIONI"/>
          <af:showMessages prefix="CM_REITERA_CONV_ASSUNZIONI"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>

    <af:list moduleName="CM_LISTA_CONV_ASSUNZIONI" 
    		canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
            canInsert="<%=canModify ? \"1\" : \"0\"%>"             
            jsSelect="Select" jsDelete="Delete"/>          
    <% if(canInsert) {%>
	<table class="main" border="0">
    	<tr> 
    		<td align="center">
            	<input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova assunzione"/>   
        	</td>
    	</tr>
    </table>
    <%}%>

<!-- LAYER ASSUNZIONE -->

  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <a name="aLayerIns"></a>
  
  <%out.print(divStreamTop);%>
    
    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuova Assunzione
        <%} else {%>
          Assunzione
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

        <table align="center" width="100%" border="0"> 
           <tr valign="top">
              <td class="etichetta">Numero</td>
			  <td class="campo">
				<af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="integer" title="numero lavoratori" 
					name="numLavoratori" size="10" maxlength="10" validateOnPost="true" required="true" 
					value="<%= numLavoratori %>" readonly="<%= fieldReadOnly %>"/>
	    	  </td>
           </tr>
           
           <tr valign="top" class="note">
    	   		<td colspan="2">
      				<div class="sezione2">&nbsp;&nbsp;Lavoratore&nbsp;&nbsp;
        				<%if (canModify) {%>
        				<a href="#" onBlur="fieldChanged()" onClick="selLavIscrCM('ricerca','riempiDatiLavIscrCM');return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
        				&nbsp;<a href="#" onBlur="fieldChanged()" onClick="javascript:azzeraLavIscrCM();"><img src="../../img/del.gif" alt="Azzera campi lavoratore"></a>
			        	<%}%>
			        </div>
    			</td>
		   </tr>
		   <tr valign="top">
  				<td colspan="2">
    				<div id="lavoratoreSez" style="display:">
      					<table class="main" width="100%" border="0">
          					<tr>
            					<td class="etichetta">Codice Fiscale</td>
            					<td class="campo">
              						<af:textBox classNameBase="input" type="text" name="codiceFiscale" readonly="true"
              						value="<%=codiceFiscaleLav%>" size="30" maxlength="16"/>
              					</td>
              				</tr>
              				<tr>
              					<td class="etichetta">Cognome</td>
            					<td class="campo">              						
              						<af:textBox classNameBase="input" type="text" name="cognome" readonly="true" 
              						value="<%=cognomeLav%>" size="30" maxlength="50"/>
              						&nbsp;&nbsp;Nome&nbsp;&nbsp;
              						<af:textBox classNameBase="input" type="text" name="nome" readonly="true" 
              						value="<%=nomeLav%>" size="30" maxlength="50"/>
            						<input type="hidden" name="CDNLAVORATORE" value="<%=cdnlavoratore%>"/>
          						</td>
          					</tr>
      					</table>
    				</div>
  				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
            <tr valign="top">
              <td class="etichetta">D/A</td>
              <td class="campo">
                <af:comboBox 
                  title="Categoria" 
                  name="CODMONOCATEGORIA"
                  classNameBase="input"
                  required="true"
                  disabled="<%= fieldReadOnly %>"
                  onChange="fieldChanged()">
                  <option value=""  <% if ( "".equals(codMonoCategoria) )  { %>SELECTED="true"<% } %> ></option>              
                  <option value="D" <% if ( "D".equals(codMonoCategoria) ) { %>SELECTED="true"<% } %> >Disabile</option>
                  <option value="A" <% if ( "A".equals(codMonoCategoria) ) { %>SELECTED="true"<% } %> >Altra categoria protetta</option>
                </af:comboBox>
                &nbsp;&nbsp;Nu/No&nbsp;&nbsp;
                <af:comboBox 
                  title="Tipo" 
                  name="codMonoTipo"
                  classNameBase="input"
                  required="true"
                  disabled="<%= fieldReadOnly %>"
                  onChange="fieldChanged()">
                  <option value=""  <% if ( "".equals(codMonoTipo) )  { %>SELECTED="true"<% } %> ></option>              
                  <option value="M" <% if ( "M".equals(codMonoTipo) ) { %>SELECTED="true"<% } %> >Nominativa</option>
                  <option value="R" <% if ( "R".equals(codMonoTipo) ) { %>SELECTED="true"<% } %> >Numerica</option>
                </af:comboBox>
              </td> 
            </tr>
            <tr valign="top">
            	<td class="etichetta">Entro il</td>
              	<td class="campo">
                	<af:textBox classNameBase="input" title="Data scadenza" type="date" 
			        	validateOnPost="true" value="<%=datScadenza %>" 
			        	name="datScadenza" size="11" maxlength="10" 
			        	readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged()" required="true" />
				&nbsp;&nbsp;Stato&nbsp;&nbsp;
                <af:comboBox name="codStato"
                             title="Stato"
                             moduleName="CM_COMBO_STATO_ASS"
                             classNameBase="input"
                             addBlank="true" 
                             required="true" 
                             disabled="<%= fieldReadOnly %>"
                             onChange="fieldChanged()"
                             selectedValue="<%=codStato %>" />
                <%if(canModify && !nuovo && numLavoratori.equals("1")){%>
					<input type="button" class="pulsanti" name="CheckStato" value="Controlla Stato" onClick="checkStato()">
        		<%}%> 
              	</td>
          	</tr>
            <tr valign="top">
				<td class="etichetta" nowrap>Mansione</td>
				<td class="campo" colspan="5">
					<af:textBox classNameBase="input" validateWithFunction='<%= (canModify) ? "checkMansione" : ""%>' onKeyUp="fieldChanged()"
					title="Mansione" name="CODMANSIONE" size="7" maxlength="7" value="<%=codMansione%>" readonly="<%= fieldReadOnly %>"/>
					<%if (canModify) {%>              
					<af:textBox type="hidden" name="codMansioneHid"/>    
					<a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);">
					<img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;
					<%}%>   
       				<af:textBox classNameBase="input" type="text" size="60" name="DESCMANSIONE" value="<%=strMansione%>" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
       				<%if (canModify) {%> 
					<A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
					<img src="../../img/binocolo.gif" alt="Cerca per descrizione"></A>
					<%}%>
					<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
					<af:textBox type="hidden" name="strTipoMansione" value=""/>     
				</td>	
			</tr>
	        <tr valign="top">
	            <td class="etichetta">Contratto</td>
	            <td class="campo">
	            	<af:comboBox name="codContratto"
	                             title="Contratto"
	                             moduleName="CM_COMBO_CONTRATTO_ASS"
	                             classNameBase="input"
	                             addBlank="true"
	                             disabled="<%= fieldReadOnly %>"
	                             onChange="fieldChanged()"
	                             selectedValue="<%= codContratto %>" />	
	            </td>
	        </tr>      
            <tr valign="top">
              <td class="etichetta">Gg prolung periodo prova</td>
			  <td class="campo">
				<af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="integer" 
					title="numero giorni prolungamento periodo prova" 
					name="numggProlungProva" size="10" maxlength="10" validateOnPost="true"  
					value="<%= numggProlungProva %>" readonly="<%= fieldReadOnly %>"/>
                &nbsp;&nbsp;&nbsp;&nbsp;Innalzamento limiti età&nbsp;&nbsp;
                <af:comboBox title="Flag innalzamento limiti eta" 
                			 name="FLGINNALZAMENTOLIMITI"
                  			 classNameBase="input"
                  			 disabled="<%= fieldReadOnly %>"
                  			 onChange="fieldChanged()">              
                  		<option value=""  <% if ( "".equals(flgInnalzLimiti) )  { %>SELECTED="true"<% } %> ></option>
                  		<option value="S" <% if ( "S".equals(flgInnalzLimiti) ) { %>SELECTED="true"<% } %> >Si</option>
                  		<option value="N" <% if ( "N".equals(flgInnalzLimiti) ) { %>SELECTED="true"<% } %> >No</option>
                </af:comboBox>
              </td>            
            </tr>
            <tr valign="top">
              <td class="etichetta">Indicazione lavoratore</td>
			  <td class="campo">
                <af:comboBox title="Indicazione lavoratore" 
                			 name="CODMONOLAVPROGRAM"
                  			 classNameBase="input" 
                  			 disabled="<%= fieldReadOnly %>" 
                  			 onChange="fieldChanged()">              
                  		<option value=""  <% if ( "".equals(codMonoLavProgram) )  { %>SELECTED="true"<% } %> ></option>
                  		<option value="P" <% if ( "P".equals(codMonoLavProgram) ) { %>SELECTED="true"<% } %> >Programmato</option>
                  		<option value="C" <% if ( "C".equals(codMonoLavProgram) ) { %>SELECTED="true"<% } %> >A consuntivo</option>
                </af:comboBox>
              </td>            
            </tr>
			<tr valign="top">
				<td class="etichetta2">Note</td>
		    	<td class="campo2">
		          	<af:textArea cols="50" rows="3" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
		              		name="STRNOTE"
		               		value="<%=strNote%>" validateOnPost="true" 
		                    required="false" title="Note"/>    	
		    	</td>
		    </tr>            
            <tr><td>&nbsp;</td></tr>    
            <%if(!nuovo){%>
            <tr><td colspan="2"><hr></td></tr>
            <tr valign="top">
				<td align="center" colspan="2">
					<div style="height: 100px; overflow: auto;">
						<af:list moduleName="CM_COMBO_AGEV_PRES" skipNavigationButton="1" 
								canDelete="<%= canModify ? \"1\" : \"0\" %>" jsDelete="DeleteAgevolazione"/>
					</div>
				</td>
			</tr>
			<tr valign="top">
				<td align="center" colspan="2">	
							<%if (canModify) { %>				
								<input type="button" value = "Aggiungi agevolazione" class="pulsanti"
									onClick="aggiungiAgevolazioni();">
							<%}%>
				</td>					
		    </tr>
		    <tr><td colspan="2"><hr></td></tr>
            <tr><td>&nbsp;</td></tr>
            <%} %>
            <tr>
              <td colspan="4" align="center">   
                <input type="hidden" name="PAGE" value="CMAssunzConvPage">
                <input type="hidden" name="MODULE" value="CM_INS_CONV_ASSUNZIONI"/>
                <input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>">
                <input type="hidden" name="MESSAGE" value="LIST_PAGE"/>
				<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
                <input type="hidden" name="NUMRIGHEREITERA" value="0"/>    
                <input type="hidden" name="PRGCONV" value="<%= prgConv %>"/>
                <input type="hidden" name="PRGCONVDETTAGLIO" value="<%= prgConvDettaglio %>"/>
            <%if(nuovo) {%>
                <input class="pulsante" type="submit" name="salva" value="Inserisci">
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer('divLayerDett')">
            <%} else { 
              	if (canModify) {%>    
				<script> 
					document.Frm1.MODULE.value = "CM_UPD_CONV_ASSUNZIONI" 
				</script>
                <input type="submit" class="pulsante" name="salva" value="Aggiorna">
            <%	}
              }%>
            <%if((canModify) && (!nuovo)){%>
                <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>" onclick="chiudiLayer('divLayerDett');">
            <%} else {
                if(!canModify) {%>
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer('divLayerDett');">                                                                
              <%}
              }%>        
              </td>
            </tr>  
        </table>
		<!-- Stondature ELEMENTO BOTTOM -->
		<%out.print(divStreamBottom);%>
  	</div>
<!-- LAYER ASSUNZIONE - END -->

<!-- LAYER AGEVOLAZIONE -->

  <div id="divLayerAgeDett" name="divLayerAgeDett" class="t_layerDett"
   style="position:absolute; width:70%; left:100; top:440px; z-index:6; display:none;">
  <!-- Stondature ELEMENTO TOP -->
  <a name="aLayerAgeIns"></a>
  <%out.print(divStreamTop);%>
  
   <table width="100%">
     <tr>
        <td height="16" class="azzurro_bianco">Agevolazioni selezionabili</td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerAgeDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
     </tr>
  </table>
  <table align="center" border="0">
	<tr>
		<td colspan="2" align="center">
			<af:comboBox name="agevolazioni" title="agevolazioni" moduleName="CM_COMBO_AGEVOLAZIONI" 
						classNameBase="input" onChange="fieldChanged()" addBlank="true" multiple="true" />
		</td>	
	</tr>
	<tr>
		<td colspan="2" align="center">&nbsp;</td>	
	</tr>
	<tr>
		<td align="center">
			<input type="button" class="pulsanti" value="Inserisci" onClick="inserisciAgevolazioni()">
		</td>	
		<td align="center">
	  		<input type="button" class="pulsanti" value="Chiudi" onClick="chiudiLayer('divLayerAgeDett');">
	    </td>	
	</tr>    
  </table>
  
  <!-- Stondature ELEMENTO BOTTOM -->
  <%out.print(divStreamBottom);%>
  </div>
<!-- LAYER AGEVOLAZIONE - END -->

<!-- LAYER REITERA RIGA ASSUNZIONE -->

  <div id="divLayerReitera" name="divLayerReitera" class="t_layerDett"
   style="position:absolute; width:60%; left:30; top:250px; z-index:6; display:<%=displayReitera%>;">
  <!-- Stondature ELEMENTO TOP -->
  <a name="aLayerReitera"></a>
  <%out.print(divStreamTop);%>
  
  <table width="100%">
  	<tr>
    	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerReitera');return false"></td>
        <td height="16" class="azzurro_bianco">
        Reitera Assunzione
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerReitera')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
  	</tr>
  </table>
  <table align="center" border="0">
	<tr valign="top">
      <td class="etichetta">Numero di righe da reiterare</td>
	  <td class="campo">
		<af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="integer" title="numero righe" 
			name="numRigheReitera" size="10" maxlength="10"
			value="" readonly="<%= fieldReadOnly %>"/>&nbsp;*
	  </td>
   </tr>
	<tr>
		<td colspan="2" align="center">&nbsp;</td>	
	</tr>
	<tr>
		<td align="center" colspan="2"> 
			<%
			if(canModify) {
			%>                          
				<input type="button" class="pulsanti" value="Reitera" onClick="reiteraAssunzioni()">	
				&nbsp;&nbsp;&nbsp;
			 <%
			 }
			 %>
	  		<input type="button" class="pulsanti" value="Chiudi" onClick="chiudiLayer('divLayerReitera');">
	    </td>	
	</tr>    
  </table>
  
  <!-- Stondature ELEMENTO BOTTOM -->
  <%out.print(divStreamBottom);%>
  </div>
<!-- LAYER REITERA RIGA ASSUNZIONE - END -->

</af:form>
    
</body>
</html>