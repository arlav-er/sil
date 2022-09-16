<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant"%>
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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspAutorizPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}  		

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 	
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String message = "";	
	String numKloProspettoInf = "";
	String flgEsonero = "";
	String numPercEsonero = "";
	String numPercGradualita = "";
	String flgGradualita = "";
	String flgCompTerritoriale = "";
	String flgSospensione = "";
	String flgSospensioneMob = "";
	String datFineSospMBNazionale = "";
	String codProvincia = "";
	String codRegioneProspetto = "";
	String codMonoStatoProspetto = "";
	String codMonoCategoria = "";
	String datFineEsonero = "";
	String flgEsonRichProroga = "";
	String datEsonRichProroga = "";
	String datConcGradualita = ""; 
	String datCompTerritoriale = "";
	String numAssGradualita = "";
	String datSospensione = "";
	String statoSosp = "";
	String causaleSosp = "";
	String numLavoratoriSosp = "";
	String flgCompetenza = "S";
	
	String flg15dipendenti = "";
	String flgAutoCertInviata = "";
	String datAutocerteficazione = "";
	String numPercEsonero60xmille = "";
	String numLavoratori60xmille = "";
	String numLavoratoriEsonero60xmille = "";
	String codTipoAzienda = "";
	String fieldReadOnlySezEsonero60 = "false";
	
	SourceBean bean_resultConfigflg15dipendenti = Utils.getConfigValue("PI_FL15");
	String resultConfigflg15dipendenti = (String) bean_resultConfigflg15dipendenti.getAttribute("row.num");
	
	
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
		numKloProspettoInf = prospetto.getAttribute("numKloProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("numKloProspettoInf")).toString();
		flgEsonero = prospetto.getAttribute("flgEsonero") == null? "" : (String)prospetto.getAttribute("flgEsonero");		
		numPercEsonero = prospetto.getAttribute("numPercEsonero") == null? "" : ((BigDecimal)prospetto.getAttribute("numPercEsonero")).toString();
		flgAutoCertInviata = prospetto.getAttribute("FLGESONEROAUTOCERT") == null? "" : (String)prospetto.getAttribute("FLGESONEROAUTOCERT");	
		numPercEsonero60xmille = prospetto.getAttribute("NUMPERCESONEROAUTOCERT") == null? "" : ((BigDecimal)prospetto.getAttribute("NUMPERCESONEROAUTOCERT")).toString();
		datAutocerteficazione = prospetto.getAttribute("DATESONEROAUTOCERT") == null? "" : (String)prospetto.getAttribute("DATESONEROAUTOCERT");
		numLavoratori60xmille = prospetto.getAttribute("NUMLAV60XMILLE") == null? "" : ((BigDecimal)prospetto.getAttribute("NUMLAV60XMILLE")).toString();
		numLavoratoriEsonero60xmille = prospetto.getAttribute("NUMLAVESONEROAUTOCERT") == null? "" : ((BigDecimal)prospetto.getAttribute("NUMLAVESONEROAUTOCERT")).toString();
		flgGradualita = prospetto.getAttribute("flgGradualita") == null? "" : (String)prospetto.getAttribute("flgGradualita");
		numPercGradualita = prospetto.getAttribute("numPercGradualita") == null? "" : ((BigDecimal)prospetto.getAttribute("numPercGradualita")).toString();
		flgCompTerritoriale = prospetto.getAttribute("flgCompTerritoriale") == null? "" : (String)prospetto.getAttribute("flgCompTerritoriale");		
		flgSospensione = prospetto.getAttribute("flgSospensione") == null? "" : (String)prospetto.getAttribute("flgSospensione");
		flgSospensioneMob = prospetto.getAttribute("flgSospensioneMob") == null? "" : (String)prospetto.getAttribute("flgSospensioneMob");
		datFineSospMBNazionale = prospetto.getAttribute("datFineSospMBNazionale") == null? "" : (String)prospetto.getAttribute("datFineSospMBNazionale");
		codProvincia = (prospetto.getAttribute("codprovincia")).toString();
		codRegioneProspetto = StringUtils.getAttributeStrNotNull(prospetto,"codregione");
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		codMonoCategoria = (String)prospetto.getAttribute("codmonocategoria");
		
		datFineEsonero = prospetto.getAttribute("DATFINEESONERO") == null? "" : (String)prospetto.getAttribute("DATFINEESONERO");
		flgEsonRichProroga = prospetto.getAttribute("FLGESONRICHPROROGA") == null? "" : (String)prospetto.getAttribute("FLGESONRICHPROROGA");
		datEsonRichProroga = prospetto.getAttribute("DATESONRICHPROROGA") == null? "" : (String)prospetto.getAttribute("DATESONRICHPROROGA");
		datConcGradualita = prospetto.getAttribute("DATCONCGRADUALITA") == null? "" : (String)prospetto.getAttribute("DATCONCGRADUALITA");
		numAssGradualita = prospetto.getAttribute("NUMASSGRADUALITA") == null? "" : ((BigDecimal)prospetto.getAttribute("NUMASSGRADUALITA")).toString();
		datSospensione = prospetto.getAttribute("DATSOSPENSIONE") == null? "" : (String)prospetto.getAttribute("DATSOSPENSIONE");		
		datCompTerritoriale = prospetto.getAttribute("DATCOMPTERRITORIALE") == null? "" : (String)prospetto.getAttribute("DATCOMPTERRITORIALE");
		
		flg15dipendenti = prospetto.getAttribute("flg15dipendenti") == null? "" : (String)prospetto.getAttribute("flg15dipendenti");
		codTipoAzienda = prospetto.getAttribute("codTipoAzienda") == null? "" : (String)prospetto.getAttribute("codTipoAzienda");
		if (codTipoAzienda.equalsIgnoreCase(ProspettiConstant.AZIENDA_DATORE_LAVORO_PUBBLICO)) {
			fieldReadOnlySezEsonero60 = "true";
			flgAutoCertInviata = ProspettiConstant.FLAG_FALSE;
		}
		statoSosp = prospetto.getAttribute("statoSosp") == null? "" : (String)prospetto.getAttribute("statoSosp");
		causaleSosp = prospetto.getAttribute("causaleSosp") == null? "" : (String)prospetto.getAttribute("causaleSosp");
		numLavoratoriSosp = prospetto.getAttribute("numLavoratoriSosp") == null? "" : ((BigDecimal)prospetto.getAttribute("numLavoratoriSosp")).toString();
		
		flgCompetenza = StringUtils.getAttributeStrNotNull(prospetto,"flgCompetenza");
			        			  	
		cdnUtIns = prospetto.getAttribute("cdnUtIns");
		dtmIns = prospetto.getAttribute("dtmIns");
		cdnUtMod = prospetto.getAttribute("cdnUtMod");
		dtmMod = prospetto.getAttribute("dtmMod");	
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}		
			
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");	
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspAutorizPage");	
	
	boolean canModify = false;
	boolean readOnlyStr = false; 
			
	canModify =	attributi.containsButton("AGGIORNA");  
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		 || ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		 || ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }
	//nel caso di provincia esterna si disabilita tutto
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	String codiceProv = provincia.getCodice();
	String flgPoloReg = provincia.getFlgPoloReg();
	String codiceReg = provincia.getCodiceRegione();
	if (flgPoloReg == null || flgPoloReg.equals("") || flgPoloReg.equalsIgnoreCase("N")) {
		if (!(codiceProv).equalsIgnoreCase(codProvincia)) {
			canModify = false;
		}	
	}
	else {
		if (!(codiceReg).equalsIgnoreCase(codRegioneProspetto)) {
			canModify = false;
		}	
	}	
	
	String fieldReadOnly = canModify ? "false" : "true";
	fieldReadOnlySezEsonero60 = !canModify ? "true" : fieldReadOnlySezEsonero60;
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	SourceBean compRespose = (SourceBean) serviceResponse.getAttribute("CMProspCompensazListModule.ROWS");
	Vector rows = compRespose.getAttributeAsVector("ROW");
	int numCompensazioni = rows.size();
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	
	
	var flagChanged = false;  
	var codTipoAzienda = "<%=codTipoAzienda%>";

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}		


	function flag15lav() {

		var codmonocategoria =  "<%=codMonoCategoria%>" ; 
		
		if (codmonocategoria!="C"&&document.Frm1.flg15dipendenti.value=="S") { 
			alert("Il flag del passaggio a 15 dipendenti nella pagina delle autorizzazioni e la categoria di appartenenza dichiarata nei dati generali devono essere coerenti.");
			document.Frm1.flg15dipendenti.value="";
			return false;
	  	} else {	  		
		    return true;	
		}	
		

	}
	
	function popupCompensazione() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		//if (isInSubmit()) return;
        
    	if (flagChanged) { 
  			alert("I dati sono cambiati.\nSalvarli prima di gestire le compensazioni!");
	  	} else {
        
			var url = "AdapterHTTP?PAGE=CMProspCompensazionePage";		
	    	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    	url += "&CODMONOCATEGPROSP=<%=codMonoCategoria%>";
	    	url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	    	window.open(url, "", 'toolbar=NO,statusbar=YES,height=300,width=500,scrollbars=YES,resizable=YES');	
		}
	}
	
	function refresh() {
		rinfresca();
	}	
	
	function controllaCampi() {		
		var numComp = "<%=numCompensazioni%>";	
		var flgComp = document.Frm1.flgCompTerritoriale.value;
		var flgSospensioneMob = document.Frm1.flgSospensioneMob;
		var flgAutoCertInviata = document.Frm1.flgAutoCertInviata;
			
		if (flgComp != "S") {		
			if (numComp > 0) {
				alert("Attenzione: non è stata indicata la presenza di una compensazione territoriale");
				return false;	
			}
		}

		if (flgSospensioneMob.options[flgSospensioneMob.selectedIndex].value == "S" &&
				 !document.Frm1.datFineSospMBNazionale.value) {
			alert("Attenzione: in caso di sospensione per mobilità nazionale è obbligatorio indicare la data fine sospensione");
			return false;
		}

		if (flgAutoCertInviata.options[flgAutoCertInviata.selectedIndex].value == "S") {
			if (document.Frm1.datAutocerteficazione.value == "") {
				alert("E' necessario indicare la data di autocertificazione dell'esonero parziale autocertificato");
				return false;	
			}
			if (document.Frm1.numLavoratori60xmille.value == "") {
				alert("E' necessario indicare il numero di lavoratori per i quali si paga un tasso di premio ai fini INAIL pari o superiore al 60 per mille");
				return false;
			}	
			if (document.Frm1.numLavoratoriEsonero60xmille.value == "") {
				alert("E' necessario indicare il numero di lavoratori in esonero nella sezione esonero autocertificato");
				return false;
			}
		}
		if (document.Frm1.numLavoratori60xmille.value != "") {
			if (parseInt(document.Frm1.numLavoratori60xmille.value,10) <= 0) {
				alert("Il Numero lavoratori 60 per mille non e' un intero maggiore di zero");
				return false;
			}
		}
		if (document.Frm1.numPercEsonero60xmille.value != "") {
			if (parseInt(document.Frm1.numPercEsonero60xmille.value,10) <= 0) {
				alert("La percentuale nella sezione esonero autocertificato non e' un intero maggiore di zero");
				return false;
			}
		}
		if (document.Frm1.numLavoratoriEsonero60xmille.value != "") {
			if (parseInt(document.Frm1.numLavoratoriEsonero60xmille.value,10) <= 0) {
				alert("Il Numero lavoratori in esonero nella sezione esonero autocertificato non e' un intero maggiore di zero");
				return false;
			}
		}
							
		return true;      
	}
			
</script>
<SCRIPT TYPE="text/javascript">
    
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 
  <%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>
</head>

<body class="gestione" onload="rinfresca()">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<p class="titolo">Dettaglio Autorizzazioni Prospetto</p>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspAutorizSaveModule"/>
		<af:showMessages prefix="CMProspCompensazioneDeleteModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
    

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspAutorizPage"/>
<input type="hidden" name="MODULE" value="CMProspAutorizSaveModule"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>  
<input type="hidden" name="numKloProspettoInf" value="<%=numKloProspettoInf%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>  
<input type="hidden" name="codProvincia" value="<%=codProvincia%>"/>

<%out.print(htmlStreamTop);%>

<table class="main" border="0">				
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>

<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='esoneri'>
        <img id='tendinaEsoneri' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoEsoneri"));'/>
        Esonero parziale AUTORIZZATO art.5,co, 3 L.68/99&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoEsoneri" style="display:inline">
	      	<table class="main" width="100%" border="0">
	          	<tr>
	            	<td class="etichetta">Concesso o richiesto</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgEsonero" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgEsonero) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgEsonero) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgEsonero) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 				        
	              	    &nbsp;&nbsp;
	              	    nella misura del
	              	    &nbsp;&nbsp;
						<af:textBox type="integer" title="Percentuale esonero" name="numPercEsonero" value="<%= String.valueOf(numPercEsonero)%>" size="4" maxlength="3" validateOnPost="true" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" />					
						&nbsp;%
						&nbsp;&nbsp;
						fino al
						&nbsp;&nbsp;
						<af:textBox classNameBase="input" title="Data esonero fino al" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datFineEsonero" size="11" maxlength="10" value="<%= String.valueOf(datFineEsonero)%>"   
							        			readonly="<%=fieldReadOnly%>" />  			
				    </td> 	          						
	          	</tr> 
	          	<tr>
	          		<td class="etichetta"><b>Richiesta Rinnovo Esonero</b></td>
	          		<td>&nbsp;</td>
	          	</tr>
	          	<tr>
	            	<td class="etichetta">Concesso o richiesto</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgEsonRichProroga" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgEsonRichProroga) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgEsonRichProroga) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgEsonRichProroga) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 	
				        &nbsp;&nbsp;			        	              	    
						Data richiesta di proroga
						&nbsp;&nbsp;
						<af:textBox classNameBase="input" title="Data rishiesta di proroga" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datEsonRichProroga" size="11" maxlength="10" value="<%= String.valueOf(datEsonRichProroga)%>"   
							        			readonly="<%=fieldReadOnly%>" />  			
				    </td> 	          						
	          	</tr> 	          	     
	       	</table>
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>
<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='esoneri'>
        <img id='tendinaEsoneri' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoEsoneri60xmille"));'/>
        Esonero parziale AUTOCERTIFICATO 60 per mille art.5, co.3-bis L.68/99&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoEsoneri60xmille" style="display:inline">
	      	<table class="main" width="100%" border="0">
	          	<tr>
	            	<td class="etichetta">Autocertificazione inviata</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" required="true" title="Autocertificazione inviata" 
							name="flgAutoCertInviata" classNameBase="input" disabled="<%=fieldReadOnlySezEsonero60%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgAutoCertInviata) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgAutoCertInviata) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgAutoCertInviata) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 
				    </td>
				 </tr>
				 <tr>
				    <td class="etichetta">Data autocertificazione</td>
				    <td class="campo">
						<af:textBox classNameBase="input" title="Data autocertificazione" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datAutocerteficazione" size="11" maxlength="10" value="<%= String.valueOf(datAutocerteficazione)%>"   
							        			readonly="<%=fieldReadOnlySezEsonero60%>" />  			
				    </td> 	          						
	          	</tr>
	          	<tr>
				    <td class="etichetta">Num. lavoratori 60 per mille</td>
				    <td class="campo">
						<af:textBox classNameBase="input" title="Num. lavoratori 60 per mille" type="integer"
		        			validateOnPost="true" onKeyUp="fieldChanged();" 
		        			name="numLavoratori60xmille" size="5"  maxlength="6" value="<%= String.valueOf(numLavoratori60xmille)%>"   
		        			readonly="<%=fieldReadOnlySezEsonero60%>" />  			
				    </td> 	          						
	          	</tr>
	          	<tr>
				    <td class="etichetta">Percentuale</td>
				    <td class="campo">
						<af:textBox type="integer" title="Percentuale" name="numPercEsonero60xmille" 
							value="<%= String.valueOf(numPercEsonero60xmille)%>" size="4" maxlength="3" validateOnPost="true" onKeyUp="fieldChanged();" 
							readonly="<%=fieldReadOnlySezEsonero60%>" classNameBase="input"/> 			
				    </td> 	          						
	          	</tr>
	          	<tr>
				    <td class="etichetta">Num. lavoratori in esonero</td>
				    <td class="campo">
						<af:textBox classNameBase="input" title="Num. lavoratori in esonero" type="integer"
		        			validateOnPost="true" onKeyUp="fieldChanged();" 
		        			name="numLavoratoriEsonero60xmille" size="5"  maxlength="6" value="<%= String.valueOf(numLavoratoriEsonero60xmille)%>"   
		        			readonly="<%=fieldReadOnlySezEsonero60%>" /> 			
				    </td> 	          						
	          	</tr>     	     
	       	</table>
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>
<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='gradualita'>
        <img id='tendinaGradualita' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoGradualita"));'/>
        Gradualità&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoGradualita" style="display:inline">
	      	<table class="main" width="100%" border="0">
	          	<tr>
	            	<td class="etichetta">Concessa o richiesta</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgGradualita" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgGradualita) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgGradualita) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgGradualita) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 		
				        &nbsp;&nbsp;			        	              	    
						Data Trasformazione
						&nbsp;&nbsp;
						<af:textBox classNameBase="input" title="Data Trasformazione" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datConcGradualita" size="11" maxlength="10" value="<%= String.valueOf(datConcGradualita)%>"   
							        			readonly="<%=fieldReadOnly%>" />
						&nbsp;&nbsp;
						Percentuale
						&nbsp;&nbsp;
						<af:textBox type="integer" title="Percentuale gradualità" name="numPercGradualita" classNameBase="input" value="<%= String.valueOf(numPercGradualita)%>" size="4" maxlength="3" validateOnPost="true" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" />
						
					</td>            						
	          	</tr>          
	       	</table>
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>
<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='compensazione'>
        <img id='tendinaCompensazione' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoCompensazione"));'/>
        Compensazione&nbsp;  
    </div>
  </td>  
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoCompensazione" style="display:inline">
	      	<table class="main" width="100%" border="0">
				<ul><li><b>In caso di appartenenza alla categoria B (da 36 a 50 dipendenti) o C (da 15 a 35 dipendenti),
				quanto indicato viene assunto come autocompensazione territoriale<b></li></ul>       
	          	<tr>
	            	<td class="etichetta">Concessa o richiesta</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgCompTerritoriale" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgCompTerritoriale) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgCompTerritoriale) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgCompTerritoriale) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 	
				        &nbsp;&nbsp;			        	              	    
						Data compensazione 
						&nbsp;&nbsp;
						<af:textBox classNameBase="input" title="Data compensazione" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datCompTerritoriale" size="11" maxlength="10" value="<%= String.valueOf(datCompTerritoriale)%>"   
							        			readonly="<%=fieldReadOnly%>" />  			        	              	  
					</td>            						
	          	</tr>   			                   	          	         
	       	</table>
	       	<table width="100%" border="0" align="left">
	          	<tr>
	            	<td align="left" width="75%">
	            		<af:list moduleName="CMProspCompensazListModule" skipNavigationButton="1" canDelete="<%= canModify ? \"1\" : \"0\" %>" />
	            	</td>	 
	            	<td align="center" valign="top">
	            		<% 
	            		if (canModify) {
	            		%>
		            		<input type="button" class="pulsante" name="nuovaCompensazione" value="Nuova compensazione" onclick="popupCompensazione()" />
		            	<%
		            	}	
		            	%>
	            	</td>	                	          						
	          	</tr> 	          				                   	          	         
	       	</table>		       	       		
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>
<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='sospensione'>
        <img id='tendinaSospensione' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoSospensione"));'/>
        Sospensione a carattere provinciale&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoSospensione" style="display:inline">
	      	<table class="main" width="100%" border="0">
	          	<tr>
	            	<td class="etichetta">Concessa</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgSospensione" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgSospensione) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgSospensione) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgSospensione) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 	
				        &nbsp;&nbsp;			        	              	  
						Data sospensione fino al
						&nbsp;&nbsp;
						<af:textBox classNameBase="input" title="Data sospensione fino al" type="date"
							        			validateOnPost="true" onKeyUp="fieldChanged();" 
							        			name="datSospensione" size="11" maxlength="10" value="<%= String.valueOf(datSospensione)%>"   
							        			readonly="<%=fieldReadOnly%>" />  			
				    </td> 	          						
	          	</tr>
	          	<tr>
	          	<td class="etichetta">Stato atto</td>
	          	<td class="campo">
	          	<af:comboBox onChange="fieldChanged()" title="Stato sospensione" name="statoSosp" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
					<option value=""  <% if ( "".equalsIgnoreCase(statoSosp) )  { %>SELECTED="true"<% } %> ></option>           
		            <option value="E" <% if ( "E".equalsIgnoreCase(statoSosp) )  { %>SELECTED="true"<% } %>>Approvata/Concessa</option>
		            <option value="F" <% if ( "F".equalsIgnoreCase(statoSosp) )  { %>SELECTED="true"<% } %>>Richiesta</option>               	        
				</af:comboBox>
	          	&nbsp;&nbsp;			        	              	  
				Causale sospensione
				&nbsp;&nbsp;
				<af:comboBox name="causaleSosp" moduleName="M_GetCausaleSospensione" selectedValue="<%=causaleSosp%>" addBlank="true"
                  classNameBase="input" title="Causale sospensione" onChange="fieldChanged();" disabled="<%=fieldReadOnly%>" />
				</td>
				</tr>
				<tr>
				<td class="etichetta">Numero lavoratori
				&nbsp;&nbsp;
				<af:textBox classNameBase="input" title="Lavoratori sospesi" type="integer"
	        			validateOnPost="true" onKeyUp="fieldChanged();" 
	        			name="numLavoratoriSosp" size="5" maxlength="5" value="<%= String.valueOf(numLavoratoriSosp)%>"   
	        			readonly="<%=fieldReadOnly%>" />
				</td>
				<td>
				&nbsp;&nbsp;
	        	</td>
	        	</tr>          
	       	</table>
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>
<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='sospensioneMob'>
        <img id='tendinaSospensioneMob' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoSospensioneMob"));'/>
        Sospensione per mobilità nazionale&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoSospensioneMob" style="display:inline">
	      	<table class="main" width="100%" border="0">
	          	<tr>
	            	<td class="etichetta">Sospensione nazionale</td>
	              	<td class="campo">
						<af:comboBox onChange="fieldChanged()" name="flgSospensioneMob" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flgSospensioneMob) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flgSospensioneMob) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flgSospensioneMob) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox>  			
				    </td> 	          						
	          	</tr>          
	          	<tr>
					<td class="etichetta">Data fine sospensione</td>
					<td class="campo">
						<af:textBox classNameBase="input" 
									title="Data fine sospensione" 
									type="date"
						        	validateOnPost="true" 
						        	onKeyUp="fieldChanged();" 
						        	name="datFineSospMBNazionale" size="11" maxlength="10" value="<%= String.valueOf(datFineSospMBNazionale)%>"   
						        	readonly="<%=fieldReadOnly%>" />  			
					</td> 	          						
				</tr>
	       	</table>
     	</div>
   	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>

<% if ("1".equals(resultConfigflg15dipendenti)){ %>

<tr>
  <td class="etichetta" colspan="2">
    <div class='sezione2' id='Passaggioa15dipendenti'>
        <img id='tendinaPassaggioa15dipendenti' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("infoPassaggioa15dipendenti"));'/>
        Passaggio a 15 dipendenti&nbsp;  
    </div>
  </td>
</tr>
<tr>
  	<td colspan="2">
    	<div id="infoPassaggioa15dipendenti" style="display:inline">
    	<br>E' possibile dichiarare a sì il passaggio solo se la categoria indicata è C (da 15 a 35 dipendenti).<br><br>
	      	<table class="main" width="100%" border="0">
	      	
	          	<tr align="center">
	          		  
	            	<td class="etichetta"> Passaggio da meno 15 dipendenti a 15? </td>
	              	<td class="campo">
						<af:comboBox onChange="flag15lav();" name="flg15dipendenti" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
							<option value=""  <% if ( "".equalsIgnoreCase(flg15dipendenti) )  { %>SELECTED="true"<% } %> ></option>           
				            <option value="S" <% if ( "S".equalsIgnoreCase(flg15dipendenti) )  { %>SELECTED="true"<% } %>>Si</option>
				            <option value="N" <% if ( "N".equalsIgnoreCase(flg15dipendenti) )  { %>SELECTED="true"<% } %>>No</option>               	        
				        </af:comboBox> 	
				       	
				    </td> 	          						
	          	</tr>          
	       	</table>
     	</div>
   	</td>
</tr>
<%}%>
<tr>
	<td colspan="2">&nbsp;</td>   
</tr>

<tr>
	<td colspan="2">		   
		<% 
		if (canModify) {
		%>		
			<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" />	
		<%
		}	
		%>
	</td>
</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
<br/>
<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>
<script language="javascript">
  if ((document.Frm1.flgEsonero.value == "") && (document.Frm1.flgEsonRichProroga.value == "")){
    	cambia(document.getElementById("tendinaEsoneri"), document.getElementById("infoEsoneri"));
  }  
  
  if ((document.Frm1.flgGradualita.value == "")){
    	cambia(document.getElementById("tendinaGradualita"), document.getElementById("infoGradualita"));
  }
  
  if ((document.Frm1.flgCompTerritoriale.value == "")){
    	cambia(document.getElementById("tendinaCompensazione"), document.getElementById("infoCompensazione"));
  }
  
  if ((document.Frm1.flgSospensione.value == "")){
    	cambia(document.getElementById("tendinaSospensione"), document.getElementById("infoSospensione"));
  }
  if ((document.Frm1.flgSospensioneMob.value == "")){
  		cambia(document.getElementById("tendinaSospensioneMob"), document.getElementById("infoSospensioneMob"));
  }
  <% if ("1".equals(resultConfigflg15dipendenti)){ %>
	  if ((document.Frm1.flg15dipendenti.value == "")){
	  	cambia(document.getElementById("tendinaPassaggioa15dipendenti"), document.getElementById("infoPassaggioa15dipendenti"));
	  }
  <%}%>
</script>
</body>
</html>