<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspLavoratoriPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 	
	String prgProspettoInf = ""; 
	String prgAzienda = "";
	String prgUnita = "";
	String numoreccnl = "";
	String codMonoStatoProspetto = "";
	String message = "";
	String prgLavRiserva = "";
	String strCognomeLav = "";
	String strNomeLav = "";
	String strCodiceFiscaleLav = "";
	String cdnLavoratore = "";
	String decOreTotali = "";
	String decOreLavorate = "";
	String strNote = "";
	String decCopertura = "";
	String codContratto = "";
	String codMansione = "";
	String codMonoTipo = "";
	String codMonoCategoria = "";	
	String datInizioRapp = "";
	String datFineRapp = "";
	String flgConvenzione = "";
	String flgbattistoni = "";
	String codAssProtetta = "";
	//Categoria della Azienda (codmonocategoria, d'accordo il numero dipendente) A > 50 - B in (15,50) - C < 15  
	String cat_azienda = "";
	//Configurazione Legge Battistoni
	String conf_battistoni = "";
	String descrMansione = "";	
	String descrTipoMansione = "";
	String fSelLav = "";
	String numKloLavRiserva = "";
	boolean checkLav = false;
	//Nuovo campo - flgdisplim
	String flgdisplim = null;
	String numPercDisabilita = "";
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;	
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		numoreccnl = prospetto.getAttribute("numoreccnl") == null? "" : ((BigDecimal)prospetto.getAttribute("numoreccnl")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
	}		
	
	//Prende la categoria dell'azienda
	SourceBean catAzienda = (SourceBean) serviceResponse.getAttribute("CMProspLavL68GetCategoriaAzienda.ROWS.ROW");
	cat_azienda = catAzienda.getAttribute("cat_azienda") == null? "" : (String)catAzienda.getAttribute("cat_azienda");
	
	//Prende la conf per la legge Battistoni		
	conf_battistoni = serviceResponse.containsAttribute("M_GetConfig_LeggeBattistoni.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_LeggeBattistoni.ROWS.ROW.NUM").toString():"0";
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspLavL68DettModule.ROWS.ROW");
	
	//conf CMPART

	String conf_CMPART = serviceResponse
			.containsAttribute("M_GetConfig_CMPART.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_GetConfig_CMPART.ROWS.ROW.NUM").toString()
			: "0";
	
	
	
	if (dett != null) {	
		message = "UPDATE";
		//cat_azienda = catAzienda.getAttribute("cat_azienda") == null? "" : (String)dett.getAttribute("cat_azienda");
		prgLavRiserva = dett.getAttribute("prgLavRiserva") == null? "" : ((BigDecimal)dett.getAttribute("prgLavRiserva")).toString();
		numKloLavRiserva = dett.getAttribute("numKloLavRiserva") == null? "" : ((BigDecimal)dett.getAttribute("numKloLavRiserva")).toString();
		cdnLavoratore = dett.getAttribute("cdnLavoratore") == null? "" : ((BigDecimal)dett.getAttribute("cdnLavoratore")).toString();
		strCognomeLav = dett.getAttribute("strCognomeLav") == null? "" : (String)dett.getAttribute("strCognomeLav");
		strNomeLav = dett.getAttribute("strNomeLav") == null? "" : (String)dett.getAttribute("strNomeLav");
		strCodiceFiscaleLav = dett.getAttribute("strCodiceFiscaleLav") == null? "" : (String)dett.getAttribute("strCodiceFiscaleLav");
		decOreTotali = dett.getAttribute("decOreTotali") == null? "" : ((BigDecimal)dett.getAttribute("decOreTotali")).toString();
		decOreLavorate = dett.getAttribute("decOreLavorate") == null? "" : ((BigDecimal)dett.getAttribute("decOreLavorate")).toString();
		strNote = dett.getAttribute("strNote") == null? "" : (String)dett.getAttribute("strNote");
		decCopertura = dett.getAttribute("decCopertura") == null? "" : ((BigDecimal)dett.getAttribute("decCopertura")).toString();
		codContratto = dett.getAttribute("codContratto") == null? "" : (String)dett.getAttribute("codContratto");
		codMansione = dett.getAttribute("codMansione") == null? "" : (String)dett.getAttribute("codMansione");
		codMonoTipo = dett.getAttribute("codMonoTipo") == null? "" : (String)dett.getAttribute("codMonoTipo");
		codMonoCategoria = dett.getAttribute("codMonoCategoria") == null? "" : (String)dett.getAttribute("codMonoCategoria");
		datInizioRapp = dett.getAttribute("datInizioRapp") == null? "" : (String)dett.getAttribute("datInizioRapp");
		datFineRapp = dett.getAttribute("datFineRapp") == null? "" : (String)dett.getAttribute("datFineRapp");
		flgConvenzione = dett.getAttribute("flgConvenzione") == null? "" : (String)dett.getAttribute("flgConvenzione");
		flgbattistoni = dett.getAttribute("flgbattistoni") == null? "" : (String)dett.getAttribute("FLGBATTISTONI");
		descrMansione = dett.getAttribute("descrMansione") == null? "" : (String)dett.getAttribute("descrMansione");
		descrTipoMansione = dett.getAttribute("descrTipoMansione") == null? "" : (String)dett.getAttribute("descrTipoMansione");
		flgdisplim = dett.getAttribute("flgdisplim") == null? "" : (String)dett.getAttribute("flgdisplim");
		numPercDisabilita = dett.getAttribute("NUMPERCDISABILITA") == null ? "" : ((BigDecimal)dett.getAttribute("NUMPERCDISABILITA")).toString();
		codAssProtetta = dett.getAttribute("codAssProtetta") == null? "" : (String)dett.getAttribute("codAssProtetta");
		
		if (!("").equals(cdnLavoratore)) {
			checkLav = true;
		}	
		
		cdnUtIns = dett.getAttribute("cdnUtIns");
		dtmIns = dett.getAttribute("dtmIns");
		cdnUtMod = dett.getAttribute("cdnUtMod");
		dtmMod = dett.getAttribute("dtmMod");	
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
	else {
		message = "INSERT";	
		decOreTotali = numoreccnl;
		decOreLavorate = numoreccnl;	
		
	}
	
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspLavoratoriPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify     			=	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)) {
    	canModify = false;
    }	
	String fieldReadOnly = canModify ? "false" : "true";  			
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/CommonXMLHTTPRequest.js"></SCRIPT>

<script language="javascript">	

	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	


	
	function tornaLista() {
		var s= "AdapterHTTP?PAGE=CMProspLavoratoriPage";
	    s += "&CDNFUNZIONE=<%= cdnfunzione%>";
	    s += "&PRGPROSPETTOINF=<%= prgProspettoInf%>";
	    s += "&codStatoAtto=<%= codStatoAtto%>";
	    setWindowLocation(s);
	}
	
	function isNumeric(val){	
		return(parseFloat(val,10)==(val*1));
    }
    
    function controllaCampi() {	

    	if(document.Frm1.flgbattistoni.value == "S"){
    		if(document.Frm1.codMonoCategoria.value != "A" ){  
  				alert("La categoria deve esser 'Altra Categoria Protetta.'");    
				return false; 
  			}
    		<%if(!("A".equals(cat_azienda))) { %>
    			alert("La Azienda deve essere fascia A ( Numero di dipendente maggiore di 50 )");    
				return false;  		
			<%}%>
    	}
		if (!controllaFixedFloat('decOreLavorate', 7, 3)) {			
			return false;
		}
		
		if (!controllaFixedFloat('decOreTotali', 7, 3)) {			
			return false;
		}
		
		var decCopertura = parseFloat(document.Frm1.decCopertura.value);		
		var newCopertura = 0;		

		if (document.Frm1.decCopertura.value != "" && document.Frm1.decCopertura.value != null) {
			if (decCopertura > 1) {
				alert("ERRORE: La copertura non può essere maggiore di 1");
				return false;
			}
			else {			
				if(!isFloat(decCopertura)){
					alert("La copertura deve avere un valore numerico");
					return false;
				}
			}
		}	
		
		var strData1=document.Frm1.datInizioRapp.value;	  	
	  	var strData2=document.Frm1.datFineRapp.value;
	  	
	  	if(strData2 != "" && strData1 != "") {
		  
			//costruisco la data inizio
	  	  	var d1giorno=parseInt(strData1.substr(0,2),10);
	  	  	var d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
	     	var d1anno=parseInt(strData1.substr(6,4),10);
	  	  	var data1=new Date(d1anno, d1mese, d1giorno);
		  
		  	//costruisce la data di scadenza
	  	  	var d2giorno=parseInt(strData2.substr(0,2),10);
	   	  	var d2mese=parseInt(strData2.substr(3,2),10)-1;
	  	  	var d2anno=parseInt(strData2.substr(6,4),10);
	      	var data2=new Date(d2anno, d2mese, d2giorno);
	      	
	      	if (data2 < data1) {	      
	      		alert("La data fine rapporto è precedente alla data inizio rapporto");
		    	return false;
	   	  	}
		}
		
		var tipoRapporto = document.Frm1.CODCONTRATTO.value;	  	
	  	if(tipoRapporto == "TI") {
	  		var flgConv = document.Frm1.flgConvenzione.value;		
			if(flgConv == "N" || flgConv == "") {
				alert("Un lavoratore assunto con contratto di Tirocinio è computabile solamente se è stato assunto in convenzione");
		    	return false;
			}
		}

	  	if (document.Frm1.codMonoCategoria.value != "D" ) {
	  		document.Frm1.numPercDisabilita.value = "";	
		}
									
		return true;      
	}
	
	var opened;
    
    function apriSelezionaSoggetto(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {
        var f = "AdapterHTTP?PAGE=ProspettiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=<%=cdnfunzione%>";
        if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
        if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        opened = window.open(f, t, feat);
    }	
	
	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
	//funzione che funge da costruttore per gli oggetti sezione
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.sezione.aperta=aperta;
	    this.img=img;
	}
	
	//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
	function cambia(immagine, sezione) {
		if (sezione.style.display == 'inline') {
			sezione.style.display = 'none';
			sezione.aperta = false;
			immagine.src = imgChiusa;
	    	immagine.alt = 'Apri';
		}
		else if (sezione.style.display == "none") {
			sezione.style.display = "inline";
			sezione.aperta = true;
			immagine.src = imgAperta;
	    	immagine.alt = "Chiudi";
		}
	}
	
	function cambiaLavMC(elem,stato){
	  divVar = document.getElementById(elem);
	  divVar.style.display = stato;
	}
	
	//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa per l'azienda, lav e mov
	function cambiaTendina(immagine,sezione,campo) {
	  if (immagine.alt == "Apri"){		
	   //if ((campo.value != "")){
	      //apri
	      cambiaLavMC(sezione,"inline");
	    //}
	    immagine.src=imgAperta;
	    immagine.alt="Chiudi";
	  } else {
	    //chiudi
	    cambiaLavMC(sezione,"none");
	    immagine.src=imgChiusa;
	    immagine.alt="Apri";
	  }
	}
	
	function cambiaTendinaLavPatchPerIE() {
	    cambiaLavMC("lavoratoreSez","inline");
	    document.getElementById("tendinaLav").src=imgAperta;
	    document.getElementById("tendinaLav").alt="Chiudi";		
	}
	
	function aggiornaLavoratore(){
        document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
        document.Frm1.cognome.value = opened.dati.cognome;
        document.Frm1.nome.value = opened.dati.nome;
        document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;                   
        opened.close();
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","inline");
        imgV.src=imgAperta;
        cdnLavoratore = opened.dati.cdnLavoratore; 
        provenienza = "lavoratore";     
    }
		
	function getPulsante() {
		if (confirm("Attenzione: verranno cancellati tutti i dati del lavoratore!")) {
			document.getElementById("cercaLav").style.display="";	
			document.Frm1.codiceFiscaleLavoratore.value = "";
	        document.Frm1.cognome.value = "";
	        document.Frm1.nome.value = "";
	        document.Frm1.CDNLAVORATORE.value = "";
	        document.Frm1.codiceFiscaleLavoratore.readOnly = true;
	        document.Frm1.cognome.readOnly = true;
	        document.Frm1.nome.readOnly = true;
	        document.Frm1.codiceFiscaleLavoratore.className = "inputView";                
	        document.Frm1.cognome.className = "inputView"; 
	        document.Frm1.nome.className = "inputView"; 
        } else {
			document.getElementById("manuale").checked = true;
			document.getElementById("anagrafica").checked = false;        
        }
	}
	
	function nascondiPulsante() { 
		if (confirm("Attenzione: verranno cancellati tutti i dati del lavoratore!")) {
			document.getElementById("cercaLav").style.display="none";	
			document.Frm1.codiceFiscaleLavoratore.value = "";
	        document.Frm1.cognome.value = "";
	        document.Frm1.nome.value = "";
	        document.Frm1.CDNLAVORATORE.value = "";
			document.Frm1.codiceFiscaleLavoratore.readOnly = false;
			document.Frm1.cognome.readOnly = false;
	        document.Frm1.nome.readOnly = false;
	        document.Frm1.codiceFiscaleLavoratore.className = "input"; 
	        document.Frm1.cognome.className = "input"; 
	        document.Frm1.nome.className = "input"; 
        } else {
        	document.getElementById("manuale").checked = false;
			document.getElementById("anagrafica").checked = true;
        }
	}
	
	function checkLavoratore() {
		<%
		if (canModify) {
			if (message.equals("UPDATE"))  {
				if (!checkLav) { %>
					document.getElementById("cercaLav").display = 'none';
					document.getElementById("manuale").checked = true;
					document.Frm1.codiceFiscaleLavoratore.readOnly = false;
					document.Frm1.cognome.readOnly = false;
			        document.Frm1.nome.readOnly = false;
			        document.Frm1.codiceFiscaleLavoratore.className = "input"; 
			        document.Frm1.cognome.className = "input"; 
			        document.Frm1.nome.className = "input";
				<%
				} else if (checkLav) { %>
					document.getElementById("cercaLav").display = '';
					document.getElementById("anagrafica").checked = true;
					document.Frm1.codiceFiscaleLavoratore.readOnly = true;
			        document.Frm1.cognome.readOnly = true;
			        document.Frm1.nome.readOnly = true;
			        document.Frm1.codiceFiscaleLavoratore.className = "inputView";                
			        document.Frm1.cognome.className = "inputView"; 
			        document.Frm1.nome.className = "inputView";			
				<%}
			}
			else if (message.equals("INSERT")) { %>
					document.getElementById("cercaLav").display = '';
					document.getElementById("anagrafica").checked = true;
					document.Frm1.codiceFiscaleLavoratore.readOnly = true;
			        document.Frm1.cognome.readOnly = true;
			        document.Frm1.nome.readOnly = true;
			        document.Frm1.codiceFiscaleLavoratore.className = "inputView";                
			        document.Frm1.cognome.className = "inputView"; 
			        document.Frm1.nome.className = "inputView";				
			<%
			}
		}
		%>
	}
	
	function checkMansione(nameMansione) {
		var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
		if (cod == "") return true;
		if (cod.substring(cod.length-2,cod.length) == '00') {
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
	
</script>
<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 
<%@ include file="Mansioni_CommonScripts.inc" %>
<%@ include file="ControllaMansione.inc" %>
</head>

<body class="gestione" onload="rinfresca();checkLavoratore()">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<p class="titolo">Dettaglio Lavoratore in Servizio Computabile</p>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspLavL68SaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspLavoratoriPage"/>
<input type="hidden" name="MODULE" value="CMProspLavL68SaveModule"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="prgLavRiserva" value="<%=prgLavRiserva%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>  
<input type="hidden" name="numKloLavRiserva" value="<%=numKloLavRiserva%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>   

<%out.print(htmlStreamTop);%>

<table class="main" border="0">				
<tr>
	<td colspan="4"/>&nbsp;</td>   
</tr>
<tr class="note">
	<td colspan="4">
		<div class="sezione2">
	    	<img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.Frm1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
	    	&nbsp;&nbsp;
	    	<% 
			if (canModify) {
			%>	    	    		      
		        <a href="#" name="cercaLav" id="cercaLav" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore');<%=fSelLav%>"><img src="../../img/binocolo.gif" alt="Cerca"></a>		       
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		    
		    	<input type="radio" name="tipoLavoratore" id="manuale" value="manuale" onclick="nascondiPulsante()" /> Manuale&nbsp;&nbsp;&nbsp;
	        	<input type="radio" name="tipoLavoratore" id="anagrafica" value="anagrafica" onclick="getPulsante()" />Anagrafica                      		           
	        <%
			}
			%>
	 	</div>
	</td>
</tr>
<tr>
  	<td colspan="4">
    	<div id="lavoratoreSez" style="display: none;">
      		<table class="main" width="100%" border="0">
          		<tr>
            		<td class="etichetta2">Codice Fiscale</td>
            		<td class="campo2" valign="bottom">  
              			<af:textBox classNameBase="input" type="text" title="Codice Fiscale" name="codiceFiscaleLavoratore" value="<%=strCodiceFiscaleLav%>" required="true" readonly="<%=fieldReadOnly%>" size="30" maxlength="16"/>
            		</td>	            
          		</tr>
          		<tr>
            		<td class="etichetta2">Nome</td>
 	  	         	<td class="campo2">
             	 		<af:textBox classNameBase="input" type="text" title="Nome" name="nome" value="<%=strNomeLav%>" required="true" size="30" maxlength="50" readonly="<%=fieldReadOnly%>"/>
            		</td>
            	</tr>
            	<tr>
            		<td class="etichetta2">Cognome</td>
            		<td class="campo2">
              			<af:textBox classNameBase="input" type="text" title="Cognome" name="cognome" value="<%=strCognomeLav%>" required="true" size="30" maxlength="50" readonly="<%=fieldReadOnly%>"/>
            		</td>
          		</tr>
      		</table>
    	</div>
	</td>
</tr>
<tr>
   	<td colspan="4"><hr width="90%"/></td>
</tr>
<tr>
	   <%if ("1".equals(conf_CMPART)) {	%>


		<tr>
			<td class="etichetta2">Disponibilità/Limitazione a part-time ai
			fini della copertura
			</td>
			
			<td class="campo2">			
				<af:comboBox name="flgdisplim" classNameBase="input"  disabled="<%=fieldReadOnly%>">	  	
		    		<option value=""  <% if ( "".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %> ></option>            
            		<option value="S" <% if ( "S".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %>>Si</option>
            		<option value="N" <% if ( "N".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %>>No</option>               
            	</af:comboBox> 
			</td>
		</tr>


		<%}	%>


    <td class="etichetta2">Ore lavorate (part-time)</td>
 	<td class="campo2">
		<af:textBox type="number" title="Ore lavorate" name="decOreLavorate" value="<%= String.valueOf(decOreLavorate)%>" required="true" size="5" maxlength="11" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
    </td>	  
    <td class="etichetta2">Ore totali</td>
	<td class="campo2">
		<af:textBox type="number" title="Ore totali" name="decOreTotali" value="<%= String.valueOf(decOreTotali)%>" required="true" size="5" maxlength="11" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
    </td>   
</tr>	
<tr>
    <td class="etichetta2">Copertura</td>
 	<td class="campo2" colspan="3">
		<af:textBox type="float" classNameBase="input" title="Copertura" name="decCopertura" value="<%= String.valueOf(decCopertura)%>" size="5" maxlength="10" readonly="true"/>
    </td>	  
</tr>	
<tr>
	<td class="etichetta2">Convenzione</td>
	<td class="campo2" colspan="3">
		<af:comboBox name="flgConvenzione" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	
		    <option value=""  <% if ( "".equalsIgnoreCase(flgConvenzione) )  { %>SELECTED="true"<% } %> ></option>            
            <option value="S" <% if ( "S".equalsIgnoreCase(flgConvenzione) )  { %>SELECTED="true"<% } %>>Si</option>
            <option value="N" <% if ( "N".equalsIgnoreCase(flgConvenzione) )  { %>SELECTED="true"<% } %>>No</option>               
        </af:comboBox> 
    </td> 
</tr>
<tr>	
	<td class="etichetta2">Data inizio rapporto</td>
	<td class="campo2">
		<af:textBox classNameBase="input" title="Data inizio rapporto" type="date" 
				        			validateOnPost="true" onKeyUp="fieldChanged();" 
				        			name="datInizioRapp" size="11" maxlength="10" value="<%= datInizioRapp%>"   
				        			readonly="<%=fieldReadOnly%>" required="true" /> 
    </td>
    <td class="etichetta2">Data fine rapporto</td>
	<td class="campo2">
		<af:textBox classNameBase="input" title="Data fine rapporto" type="date" 
				        			validateOnPost="true" onKeyUp="fieldChanged();" 
				        			name="datFineRapp" size="11" maxlength="10" value="<%= datFineRapp%>"   
				        			readonly="<%=fieldReadOnly%>" />  
    </td>
</tr>
<tr>
	<td class="etichetta2">Categoria</td>
	<td class="campo2">
		<af:comboBox name="codMonoCategoria" classNameBase="input" required="true" disabled="<%=fieldReadOnly%>">	  	
		    <option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %> ></option>            
            <option value="D" <% if ( "D".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Disabili</option>
            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Altra categoria protetta</option>               
        </af:comboBox> 
    </td>    
    <td class="etichetta2">Tipo assunzione</td>
	<td class="campo2">
		<af:comboBox name="codMonoTipo" classNameBase="input" title="Tipo assunzione" required="true" disabled="<%=fieldReadOnly%>">	  	 
			<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %> ></option>           
            <option value="M" <% if ( "M".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %>>Nominativa</option>
            <option value="R" <% if ( "R".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %>>Numerica</option>               	        
        </af:comboBox> 
    </td>	  
</tr>

<% if ("1".equals(conf_battistoni)||"2".equals(conf_battistoni)){%>
<tr>
	<td class="etichetta2">Art. 18 L. Battistoni</td>
	<td class="campo2" colspan="3">
		<af:comboBox name="flgbattistoni" classNameBase="input"  disabled="<%=fieldReadOnly%>">	  	
		    <option value=""  <% if ( "".equalsIgnoreCase(flgbattistoni) )  { %>SELECTED="true"<% } %> ></option>            
            <option value="S" <% if ( "S".equalsIgnoreCase(flgbattistoni) )  { %>SELECTED="true"<% } %>>Si</option>
            <option value="N" <% if ( "N".equalsIgnoreCase(flgbattistoni) )  { %>SELECTED="true"<% } %>>No</option>               
        </af:comboBox> 
    </td> 
</tr>
<%}%>

<tr>
	<td class="etichetta">Codice mansione</td>
    <td class="campo" colspan="3">    
    	<af:textBox classNameBase="input" name="CODMANSIONE" validateWithFunction='<%= (canModify) ? "checkMansione" : ""%>' value="<%=codMansione%>" title="Codice Mansione" size="7" maxlength="7" readonly="<%=fieldReadOnly%>"/>
        <af:textBox type="hidden" name="codMansioneHid" />  
        <input type="hidden" name="flgFrequente" value="true"/>  
        <% if (canModify) { %>
        	<a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
        	<a href="javascript:ricercaAvanzataMansioni();">
            Ricerca avanzata
        	</a>
    	<%}%>
  </td>
</tr> 
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo" colspan="3">
      	<af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      	<af:textBox classNameBase="input" name="strTipoMansione" value="<%=descrTipoMansione%>" readonly="true" size="48" />
	</td>
</tr>
<tr>
  	<td class="etichetta">Descrizione</td>
  	<td class="campo" colspan="3">
      	<af:textArea cols="30" rows="2" name="DESCMANSIONE" classNameBase="textarea" readonly="true" maxlength="100" value="<%= descrMansione %>" />
  	</td>
</tr>
<tr>
    <td class="etichetta">Tipi di rapporti</td>
	<td class="campo" colspan="3">
    	<af:comboBox addBlank="true" name="CODCONTRATTO" title="Tipi" selectedValue="<%=codContratto%>" moduleName="M_ListContratti" disabled="<%=fieldReadOnly%>"/>
	</td>
</tr>
<tr>
    <td class="etichetta">Percentuale disabilità</td>
	<td class="campo" colspan="3">
		<af:textBox type="integer" title="Percentuale disabilità" name="numPercDisabilita" value="<%=numPercDisabilita%>" size="3" maxlength="3" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
	</td>
</tr>
<tr>
    <td class="etichetta">Tipo assunzione protetta</td>
	<td class="campo" colspan="3">
    	<af:comboBox addBlank="true" name="CODASSPROTETTA" title="Tipo assunzione protetta" 
    		selectedValue="<%=codAssProtetta%>" moduleName="CMProspettiTipoAssProtetta" disabled="<%=fieldReadOnly%>"/>
	</td>
</tr>
<tr>  
  	<td class="etichetta">Note</td>
  	<td class="campo" colspan="3">
      	<af:textArea cols="50" rows="5" name="strNote" classNameBase="textarea" maxlength="1000" value="<%= strNote %>" readonly="<%=fieldReadOnly%>" />
  	</td>
</tr>
<tr>
	<td colspan="4">		
		<% 
		if (canModify) {
			if (("UPDATE").equalsIgnoreCase(message)) {
		%>
				<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" onclick="cambiaTendinaLavPatchPerIE();"/>	
		<%
			}
			else {
		%>		
				<input type="submit" class="pulsante" name="inserisci" value="Inserisci" onclick="cambiaTendinaLavPatchPerIE();"/>			
		<%
			}
		}
		%>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" class="pulsante" name="torna" value="Torna alla lista" onclick="tornaLista()" />
	</td>
</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>
<script language="javascript">	
	  var imgV = document.getElementById("tendinaLav");
	  <%if (!strNomeLav.equals("")) {%>
	    cambiaLavMC("lavoratoreSez","inline");
	    imgV.src = imgAperta;
	    imgV.alt="Chiudi";
	  <%} else {%>
	    cambiaTendina(imgV,"lavoratoreSez",document.Frm1.nome);
	  <%}%>
</script>
</body>
</html>