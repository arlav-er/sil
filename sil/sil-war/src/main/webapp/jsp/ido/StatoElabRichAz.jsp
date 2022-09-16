<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.sil.module.movimenti.constant.Properties,
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	boolean flag_insert = false; //serviceRequest.containsAttribute("inserisci");

String prgRichiestaAz	= "";
String codEvasione   	= "";
String codMonoStatoR 	= "";
String flgSoloDiff 		= "";
String strMotChiusura	= "";
String datChiusura		= "";
String codMotivoChiusura= "";    
String codEsitoRichiesta= "";
BigDecimal cdnStatoRich = null;
String flgPubblicata 	= "";
String datPubblic    	= "";
String datScadPubblic	= "";
String statoPubblic  	= "";
String datInvioVacancy  = "";
boolean flgIsSelEV		= false;
String codMonoCMCategoria = "";
String flgPubbCresco="";


BigDecimal numStorico = null;
boolean    eUnaCopia= false;

BigDecimal cdnUtIns = null;
String     dtmIns   = "";
BigDecimal cdnUtMod = null;
String     dtmMod   = "";
BigDecimal numklo   = null;

SourceBean row      = null;

Testata operatoreInfo=null;
//InfCorrentiAzienda infCorrentiAzienda= null;

  prgRichiestaAz= (String)serviceRequest.getAttribute("prgRichiestaAZ");
  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  /*PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canModify =  attributi.containsButton("AGGIORNA"); 
 

  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);*/
  
  boolean CRESCO = false;
  boolean readOnlyModalitaCresco = false;

  String numConfigCresco = serviceResponse.containsAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM")?
	serviceResponse.getAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
  if(Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCresco)){
  	CRESCO = true;
  }  
  
  
  BigDecimal prgAzienda = null;
  BigDecimal prgUnita   = null;
  row= (SourceBean) serviceResponse.getAttribute("M_GetAziendaUnita.ROWS.ROW");
  if( row != null ) 
  { prgAzienda = (BigDecimal) row.getAttribute("PRGAZIENDA");
    prgUnita   = (BigDecimal) row.getAttribute("PRGUNITA");
  }
/* ************************** "nota AA" ********************************
  if( prgAzienda != null && prgUnita!=null ) {
    infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  }
  */

  row= (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
  if( row != null ) 
  { codEvasione   = StringUtils.getAttributeStrNotNull(row,"CODEVASIONE");
    if( !codEvasione.equals("") ) flgIsSelEV = true;
    cdnStatoRich  = (BigDecimal) row.getAttribute("CDNSTATORICH");
    flgSoloDiff = StringUtils.getAttributeStrNotNull(row,"FLGSOLODIFF");
    codMonoStatoR = StringUtils.getAttributeStrNotNull(row,"CODMONOSTATORICH");
    strMotChiusura= StringUtils.getAttributeStrNotNull(row,"STRMOTVOCHIUSURA");
    datChiusura = StringUtils.getAttributeStrNotNull(row,"DATCHIUSURA");
    codMotivoChiusura = StringUtils.getAttributeStrNotNull(row,"CODMOTIVOCHIUSURA");
    flgPubblicata = StringUtils.getAttributeStrNotNull(row,"FLGPUBBLICATA");
    datPubblic    = StringUtils.getAttributeStrNotNull(row,"DATPUBBLICAZIONE");
    datScadPubblic= StringUtils.getAttributeStrNotNull(row,"DATSCADENZAPUBBLICAZIONE");
    statoPubblic  = StringUtils.getAttributeStrNotNull(row,"STATOPUBBLICAZIONE");
    numStorico    = (BigDecimal) row.getAttribute("NUMSTORICO");
    eUnaCopia     = (numStorico.intValue()!=0) ? true : false;
    codEsitoRichiesta = StringUtils.getAttributeStrNotNull(row,"CODESITORICHIESTA");
    codMonoCMCategoria = StringUtils.getAttributeStrNotNull(row,"CODMONOCMCATEGORIA");
    datInvioVacancy = StringUtils.getAttributeStrNotNull(row,"DATULTIMOINVIOVACANCY");
    //**********



    //**********
    dtmIns        = (String)     row.getAttribute("DTMINS");
    dtmMod        = (String)     row.getAttribute("DTMMOD");
    cdnUtIns      = (BigDecimal) row.getAttribute("CDNUTINS");
    cdnUtMod      = (BigDecimal) row.getAttribute("CDNUTMOD");

    operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    //flgPubbCresco = StringUtils.getAttributeStrNotNull(row, "flgPubbCresco");
   	if (CRESCO && it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "flgPubbCresco").equalsIgnoreCase("S")) {
		codEvasione = "DRA";
	   	readOnlyModalitaCresco = true;
	}
} else {
	flag_insert = true;
	if (CRESCO) {
		codEvasione = "DRA";
	}
}

	Vector tipiEvasioneRows = serviceResponse
			.getAttributeAsVector("M_EVASIONERICHAZ.ROWS.ROW");

	Vector tipiStatoRows = serviceResponse
			.getAttributeAsVector("M_StatoEvRichAz.ROWS.ROW");

	Vector tipiMotivoChiusura = serviceResponse
			.getAttributeAsVector("M_IDOGETMOTIVOCHIUSURARICH.ROWS.ROW");

	Vector tipiRelEvasMotivoVec = serviceResponse
			.getAttributeAsVector("M_IdoGetRelEvasMotivo.ROWS.ROW");
	Vector tipiEsitoRichiestaVec = serviceResponse
			.getAttributeAsVector("M_IdoGetEsitoRichiesta.ROWS.ROW");

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setPrgAzienda(prgAzienda);
	filter.setPrgUnita(prgUnita);

	boolean canView = filter.canViewUnitaAzienda();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		PageAttribs attributi = new PageAttribs(user, _page);
		boolean readOnlyStr = !attributi.containsButton("AGGIORNA");
		boolean canModify = attributi.containsButton("AGGIORNA");
		boolean checkVdA = attributi.containsButton("CHECKVDA");

		if (!canModify) {

		} else {
			boolean canEdit = filter.canEditUnitaAzienda();
			if (eUnaCopia)
				canEdit = false;
			if (!canEdit) {

				canModify = false;
				readOnlyStr = true;
			}
		}

		boolean FLUSSO_VACANCY = false;
		String numConfigFlusso = serviceResponse
				.containsAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM") ? serviceResponse
				.getAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM")
				.toString() : Properties.DEFAULT_CONFIG;
		if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigFlusso)) {
			FLUSSO_VACANCY = true;
		}

		String htmlStreamTop = StyleUtils.roundTopTable(canModify);
		String htmlStreamBottom = StyleUtils
				.roundBottomTable(canModify);

		//Variabili che mi dicono se la richiesta è chiusa o totalmente chiusa (e quindi anche chiusa)
		boolean isClosedReq = cdnStatoRich.equals(new BigDecimal(4))
				|| cdnStatoRich.equals(new BigDecimal(5));
		boolean isTotallyClosedReq = cdnStatoRich
				.equals(new BigDecimal(5));

		//filtro il risultato della combo sullo stato evasione per lasciare solo le opzioni che voglio
		//in base al valore corrente del flgsolodiff
		Iterator iter = tipiStatoRows.iterator();
		Vector filteredTipiStatoRows = new Vector();
		while (iter.hasNext()) {
			SourceBean stato = (SourceBean) iter.next();
			BigDecimal currCdnStatoRich = (BigDecimal) stato
					.getAttribute("CODICE");
			String currFlgSoloDiff = (String) stato
					.getAttribute("FLGSOLODIFF");
			if (isTotallyClosedReq) {
				//Imposto il dato in sola lettura e lascio solo l'opzione corrente
				if (currCdnStatoRich.equals(cdnStatoRich)) {
					filteredTipiStatoRows.add(stato);
				}
			} else if (isClosedReq) {
				//Lascio solo le opzioni con flgSoloDiff a D e quella corrente
				if (currCdnStatoRich.equals(cdnStatoRich)
						|| currFlgSoloDiff.equalsIgnoreCase("D")) {
					filteredTipiStatoRows.add(stato);
				}
			} else {
				//Lascio solo le opzioni con flgSoloDiff a S o a D e quella corrente
				if (currCdnStatoRich.equals(cdnStatoRich)
						|| currFlgSoloDiff.equalsIgnoreCase("D")
						|| currFlgSoloDiff.equalsIgnoreCase("S")) {
					filteredTipiStatoRows.add(stato);
				}
			}
		}

		boolean isClosedAsta = false;

		//bottone info storiche per riapertura richiesta
		//**** se la pagina restituisce errore controllare gli interruttori per queste variabili **** 
		boolean btnInfStoriche = false;
		boolean infStorButt = false;
		infStorButt = attributi.containsButton("INF_STORICHE");
		btnInfStoriche = infStorButt && btnInfStoriche;

		//	INIT-PARTE-TEMP
		if (Sottosistema.AS.isOff()) {
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP 

			if ("AS".equalsIgnoreCase(codEvasione)) {
				isClosedAsta = true;
			}

			if (!("").equalsIgnoreCase(codEvasione)
					&& !("AS").equalsIgnoreCase(codEvasione)) {

				for (int i = 0; i < tipiEvasioneRows.size(); i++) {
					row = (SourceBean) tipiEvasioneRows.elementAt(i);
					String codiceEvas = (String) row
							.getAttribute("CODICE");

					if ("AS".equalsIgnoreCase(codiceEvas)) {
						tipiEvasioneRows.removeElementAt(i);
					}
				}
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		//	INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP 

			if ("CMA".equalsIgnoreCase(codEvasione)
					|| "CMG".equalsIgnoreCase(codEvasione)) {
				isClosedAsta = true;
			}

			if (!("").equalsIgnoreCase(codEvasione)
					&& !("CMA").equalsIgnoreCase(codEvasione)
					&& !("CMG").equalsIgnoreCase(codEvasione)) {

				for (int i = 0; i < tipiEvasioneRows.size(); i++) {
					row = (SourceBean) tipiEvasioneRows.elementAt(i);
					String codiceEvas = (String) row
							.getAttribute("CODICE");

					if ("CMA".equalsIgnoreCase(codiceEvas)) {
						tipiEvasioneRows.removeElementAt(i);
					}

					if ("CMG".equalsIgnoreCase(codiceEvas)) {
						tipiEvasioneRows.removeElementAt(i);
					}
				}
			}

			if (!("").equalsIgnoreCase(codEvasione)
					&& !("MIR").equalsIgnoreCase(codEvasione)
					&& !("MPP").equalsIgnoreCase(codEvasione)
					&& !("MPA").equalsIgnoreCase(codEvasione)
					&& !("CMG").equalsIgnoreCase(codEvasione)) {

				for (int i = 0; i < tipiEvasioneRows.size(); i++) {
					row = (SourceBean) tipiEvasioneRows.elementAt(i);
					String codiceEvas = (String) row
							.getAttribute("CODICE");

					if ("MIR".equalsIgnoreCase(codiceEvas)
							|| "MPP".equalsIgnoreCase(codiceEvas)
							|| "MPA".equalsIgnoreCase(codiceEvas)
							|| "CMG".equalsIgnoreCase(codiceEvas)) {

						tipiEvasioneRows.removeElementAt(i);
						i--;

					}

				}
			}

			if (!("").equalsIgnoreCase(codEvasione)
					&& (("MIR").equalsIgnoreCase(codEvasione)
							|| ("MPP").equalsIgnoreCase(codEvasione) || ("MPA")
								.equalsIgnoreCase(codEvasione))) {

				for (int i = 0; i < tipiEvasioneRows.size(); i++) {
					row = (SourceBean) tipiEvasioneRows.elementAt(i);
					String codiceEvas = (String) row
							.getAttribute("CODICE");

					if (!"MIR".equalsIgnoreCase(codiceEvas)
							&& !"MPP".equalsIgnoreCase(codiceEvas)
							&& !"MPA".equalsIgnoreCase(codiceEvas)) {

						tipiEvasioneRows.removeElementAt(i);
						i--;

					}

				}
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<% if( !(prgAzienda != null && prgUnita!=null) ) {
	// replico la condizione originaria . Vedi "nota AA" 
		infCorrentiAzienda = null;
  }%>
<html>

<head>
<title>Stato Elaborazione Richiesta</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<%@ include file="../amministrazione/CommonScript.inc" %>
<script language="JavaScript">
<!--
<%//Genera il Javascript che si occuperà di inserire i links nel footer
  attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
%>

// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;
        
function fieldChanged() {
<% if (!readOnlyStr){ %> 
  flagChanged = true;
<%}%> 
}

function mostraMotivo()
{ 
  var div = document.getElementById("motChisura");
  if(window.document.Frm1.cdnStatoRich.value== 4 || window.document.Frm1.cdnStatoRich.value== 5) {
    div.style.display="inline";
  } else {
  	div.style.display="none";
  	document.Frm1.codMotivoChiusura.value = "";
    document.Frm1.motivoChiusura.value = "";
    document.Frm1.codEsitoRichiesta.value = "";
    <% if (checkVdA) {%>
    	document.Frm1.datChiusura.value = "";
    <% }%>
  }
}

 
function checkStatoRich(inputName) { 
	var ctrlObj = eval("document.forms[0]." + inputName);

  if(ctrlObj.name=="codMotivoChiusura" && 
  		(window.document.forms[0].cdnStatoRich.value==4 || 
  		 window.document.forms[0].cdnStatoRich.value==5 )) 
  {
    if (ctrlObj.value=="") {
      alert("Il campo \"" + ctrlObj.title + "\" è obbligatorio");
      ctrlObj.focus();
      return false;
    }
  }
	return true;
} //checkStatoRich


 var stato_flg = new Array();
 var stato_cod  = new Array();
 var stato_des  = new Array();
<%     for(int i=0; i<filteredTipiStatoRows.size(); i++)  { 
            row = (SourceBean) filteredTipiStatoRows.elementAt(i);
            out.print("stato_flg["+i+"]=\""+ row.getAttribute("FLGSOLODIFF").toString()+"\";\n");
            out.print("stato_cod["+i+"]=\""+ row.getAttribute("CODICE").toString()+"\";\n");
            out.print("stato_des["+i+"]=\""+ row.getAttribute("DESCRIZIONE").toString()+"\";\n");              
      } 
%>

function caricaStatiEvasione() 
{ 
  i=0;
  j=0;
  var flgStatoAggiornato = false;
  var codModEvasione = document.Frm1.codEvasione.options.value;
  var cdnStatoRichSel = document.Frm1.cdnStatoRich.options.value; 
  
  while (document.Frm1.cdnStatoRich.options.length>0) 
  { document.Frm1.cdnStatoRich.options[0]=null;
  }
  
  //alert("OPTION::"+document.Frm1.codEvasione.options.value);

  if(codModEvasione == "PRE" || codModEvasione == "DPR" || codModEvasione == "DRA")
  {  for (i=0; i<stato_cod.length ; i++) 
     { document.Frm1.cdnStatoRich.options[i]=new Option(stato_des[i], stato_cod[i], false, false);
       //alert ("Confronto " + stato_cod[i] + " con " + cdnStatoRichSel);
       if ((!flgStatoAggiornato) && (stato_cod[i] == cdnStatoRichSel)) {
         document.Frm1.cdnStatoRich.options[i].selected="true";
         flgStatoAggiornato= true;
         //alert ("ok");
         }
     }//for
     if (!flgStatoAggiornato) {
       document.Frm1.cdnStatoRich.options[0].selected="true";
     }
  }
  else
  {  for (i=0; i<stato_cod.length ; i++)
     { if (stato_flg[i]=='S' || stato_flg[i]=='D') 
       { 
         document.Frm1.cdnStatoRich.options[j]=new Option(stato_des[i], stato_cod[i], false, false); 
         if ((!flgStatoAggiornato) && (stato_cod[i] == cdnStatoRichSel)) 
         {
           document.Frm1.cdnStatoRich.options[j].selected="true";
           flgStatoAggiornato= true;
         }
         j++;
       }
     }//for
     if (!flgStatoAggiornato) {
       document.Frm1.cdnStatoRich.options[0].selected="true";
     }

  }
}//caricaStatiEvasione()






 function caricaDettInfo(codEvasione, codMotivoChiusura) {
    var dett_evasione=new Array();
    var dett_motivo=new Array();
    var dett_codice=new Array();
    var dett_descrizione=new Array();
    var indiceDett=0;

<%  for(int i=0; i<tipiRelEvasMotivoVec.size(); i++)  { 
      SourceBean tipiRelEvasMotivoRow = (SourceBean) tipiRelEvasMotivoVec.elementAt(i);
      out.print("dett_evasione["+i+"]=\""+ tipiRelEvasMotivoRow.getAttribute("CODEVASIONE").toString()+"\";\n");
      out.print("dett_motivo["+i+"]=\""+ tipiRelEvasMotivoRow.getAttribute("CODMOTIVOCHIUSURARICH").toString()+"\";\n");
      out.print("dett_codice["+i+"]=\""+ tipiRelEvasMotivoRow.getAttribute("CODESITORICHIESTA").toString()+"\";\n");      
      out.print("dett_descrizione["+i+"]=\""+ tipiRelEvasMotivoRow.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>

   maxcombo=15;
   j=1;
   if(document.Frm1.codEsitoRichiesta.options != null){ 
    while (document.Frm1.codEsitoRichiesta.options.length>0) {
          document.Frm1.codEsitoRichiesta.options[0]=null;
    }

    document.Frm1.codEsitoRichiesta.options[0] = new Option("", "" , false, false);
    for (i=0; i<dett_evasione.length ;i++) {
      if ((dett_evasione[i]==codEvasione) && (dett_motivo[i] == codMotivoChiusura)){
        document.Frm1.codEsitoRichiesta.options[j]=new Option(dett_descrizione[i], dett_codice[i], false, false);
        j++;
      }
    }
   }

   if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo

   //if (document.Frm1.cdnStatoRich.value!=5) {
   if (document.Frm1.codEsitoRichiesta.options.length > 1)
    document.Frm1.codEsitoRichiesta.disabled=false;
   else
    document.Frm1.codEsitoRichiesta.disabled=true;   
   //} 
 }


  function azzeraSelEsito() {
    document.Frm1.codEsitoRichiesta.selectedIndex=-1;
  }

<%	
	// INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	
	} else {
	// END-PARTE-TEMP
		
        if (codEvasione.equals("AS") && (cdnStatoRich.intValue() == 4 || cdnStatoRich.intValue() == 5)) {
	%>
		//Script per la comparsa-scomparsa di una sezione
		var showButtonImg = new Image();
		var hideButtonImg = new Image();
		showButtonImg.src=" ../../img/aperto.gif";
		hideButtonImg.src=" ../../img/chiuso.gif"
		
		function onOff(){
		var div1 = document.getElementById("dett");
		var idImm = document.getElementById("imm1");
		 
			if (div1.style.display==""){
			 	nascondi("dett");
			 	idImm.src = hideButtonImg.src
			} else {
				mostra  ("dett");
				idImm.src = showButtonImg.src;
			}
		}
		
		function mostra(id){
		var div = document.getElementById(id);
		div.style.display="";
		}
		
		function nascondi(id){
		var div = document.getElementById(id);
		div.style.display="none";
		}
		
		//Script per il controllo del motivo di riapetura
				
		function controllaMotivazione() {		
		    var codMot = document.Frm2.motApertura.value;
		
		  	if((codMot != null) && (codMot != "")){ 
		  		mostra("motivo");		  		
			} else { 
		  		nascondi("motivo");
		 	}	
			if (codMot == 'ALT') {
				document.Frm2.altroMotivo.disabled = false;
										
			} else {
				codMot = "";
				document.Frm2.altroMotivo.disabled = true;				
			}			
		}
		
		function controllaMotivo() {					    
		    var codMot = document.Frm2.motApertura.value;	
			if (codMot == "ALT") {
   				if (document.Frm2.altroMotivo.value == "") {
	   				alert("Inserire motivazione per la voce selezionata!");
	   				undoSubmit(document.Frm2);
	   			} else {
	   				doFormSubmit(document.Frm2);
	   			}   				   				
   			} else if (codMot == ""){
   				alert("Selezionare il Motivo della riapertura")
   				undoSubmit(document.Frm2);
   			} else {   				
   				doFormSubmit(document.Frm2);
   			}
   		}
	<%	
		}
			
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
	 
%>

-->
</script>

</head>

<body class="gestione" onload="rinfresca();mostraMotivo();">

  <%
      if(infCorrentiAzienda != null) infCorrentiAzienda.show(out); 
      
     // if (!flag_insert) { 
          if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
            Linguette l = new Linguette( user,  _cdnFunz, _page, new BigDecimal(prgRichiestaAz));
            l.setCodiceItem("PRGRICHIESTAAZ");    
            l.show(out);
          }
      //}
  %>
<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
  <center>
  <font color="green">
  <%if (flag_insert) {%> 
    <af:showMessages prefix="M_InserisciStatoRich" />
  <%}else{%>
    <af:showMessages prefix="M_SalvaStatoRich"/>
  <%}%>
  <% 	
   	// INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	
	} else {
	// END-PARTE-TEMP
  %>
   	<af:showMessages prefix="M_SaveRiaperturaRichiesta"/>
  <%// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
  %> 	
  </font>
</center>
<center>
<font color="red"><af:showErrors /></font>
</center>
  <%out.print(htmlStreamTop);%>
  <table class="main">
    <tr>
      <td class="etichetta">Modalit&agrave; evasione</td>
      <td class="campo">      	      
             
      	<af:comboBox classNameBase="input" name="codEvasione" addBlank="<%=String.valueOf(!flgIsSelEV)%>"
      				 onChange="fieldChanged();caricaDettInfo(Frm1.codEvasione.value, Frm1.codMotivoChiusura.value);azzeraSelEsito()"
        			 disabled="<%=String.valueOf(!canModify || isClosedReq || isClosedAsta || readOnlyModalitaCresco)%>" 
        			 >
			<% //Aggiungo le opzioni rimaste nel vettore
			for (int k = 0; k < tipiEvasioneRows.size(); k++ ){
				SourceBean optionEvas = (SourceBean) tipiEvasioneRows.get(k);
				String codeEvas = (String)optionEvas.getAttribute("CODICE");
				String descrEvas = (String)optionEvas.getAttribute("DESCRIZIONE");
			//String codeEvas ="cod";
			//String descrEvas = "descr";
			%>			
				<option value="<%=codeEvas%>" <% if (codEvasione.equals(codeEvas)){%> selected="selected"<%}%>><%=descrEvas%></option>
			<%
			}		
			%>
        </af:comboBox>        
      
      
      </td>
    </tr>
    
    <tr>
      <td class="etichetta">Stato di evasione</td>
      <td class="campo">
        <af:comboBox classNameBase="input" onChange="fieldChanged();mostraMotivo();" name="cdnStatoRich" 
        			 disabled="<%= String.valueOf(!canModify || isTotallyClosedReq)%>" 
        			 title="'Stato di evasione'" required="true">
		<% //Aggiungo le opzioni rimaste nel vettore
			for (int j = 0; j < filteredTipiStatoRows.size(); j ++ ){
			SourceBean option = (SourceBean) filteredTipiStatoRows.get(j);
			BigDecimal code = (BigDecimal) option.getAttribute("CODICE");
			String descr = (String)option.getAttribute("DESCRIZIONE");%>
			<option value="<%=code%>" <% if (cdnStatoRich.equals(code)){%> selected="selected"<%}%>><%=descr%></option>
		<%}%>
        </af:comboBox>
      </td>
    </tr>
    <tr><td colspan="2">
      <div id="motChisura" style="display: none">
      <table cellpadding="0" cellspacing="2" border="0" width="100%">
        <tr>
          <td class="etichetta">Motivo della chiusura</td>
          <td class="campo">
          <% if(!canModify || eUnaCopia) {%>
            <af:comboBox classNameBase="input" name="codMotivoChiusura" selectedValue="<%=codMotivoChiusura%>" 
            			 moduleName="M_IdoGetMotivoChiusuraRich" title="Motivo della chiusura" addBlank="true" disabled="true"/>
          <% } else {%>
            <af:comboBox classNameBase="input" name="codMotivoChiusura" selectedValue="<%=codMotivoChiusura%>" 
            			 moduleName="M_IdoGetMotivoChiusuraRich"  addBlank="true" 
            			 disabled="<%=String.valueOf(!canModify)%>" 
            			 onChange="fieldChanged();caricaDettInfo(Frm1.codEvasione.value, Frm1.codMotivoChiusura.value);azzeraSelEsito()" title="Motivo della chiusura" 
            			 validateWithFunction="checkStatoRich"/>&nbsp;*
          <%}%>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Note</td>
          <td class="campo">
          	<af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="motivoChiusura" 
          				 value="<%=strMotChiusura%>" cols="60" rows="4" maxlength="100" 
          				 readonly="<%=String.valueOf(readOnlyStr)%>" />
          </td>
        </tr>
        <tr>
        <% 
        if (checkVdA) {
        %>
          <td class="etichetta">Data chiusura</td>
          <td class="campo">
          	<INPUT type="text" name="datChiusura" size="11" value="<%=datChiusura%>" maxlength="10" title="Data chiusura" class="inputEdit" onKeyUp="fieldChanged();" />
          	<a href='#' onClick="show_calendar('datChiusura', '',event.screenX,event.screenY);return false"><img src="../../img/show-cal.gif" border="0" title="Mostra calendario"></a>
          </td>
        <%
        }
        else {    
        %>
          <td class="etichetta"></td>
          <td class="campo"><input type="hidden" name="datChiusura" value=""/></td>
        <%
        }
        %>
        </tr>
        <tr>
          <td class="etichetta">Esito della richiesta</td>
          <td class="campo">
          <% if(!canModify || eUnaCopia) {%>
              <af:comboBox classNameBase="input" name="codEsitoRichiesta" title="Esito della richiesta" 
                  moduleName="M_IdoGetEsitoRichiesta" selectedValue="<%=codEsitoRichiesta%>" 
                  disabled="true"/>
          <% } else {%>
            <af:comboBox classNameBase="input" name="codEsitoRichiesta" selectedValue="<%=codEsitoRichiesta%>" 
            			 onChange="fieldChanged()" title="Esito della richiesta">&nbsp;
            </af:comboBox>
            <script language="javascript">
              caricaDettInfo(document.Frm1.codEvasione.value, document.Frm1.codMotivoChiusura.value);
              document.Frm1.codEsitoRichiesta.value = '<%=codEsitoRichiesta%>';
            </script>                                        
          <%}%>
          </td>
        </tr>
      </table>
      </div>
    </td></tr>

    <tr>
      <td class="etichetta">Stato richiesta</td>
      <td class="campo">
        <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codMonoStatoR" 
        			 selectedValue="<%=codMonoStatoR%>" 
        			 disabled="<%= String.valueOf(!canModify || eUnaCopia || isClosedReq)%>" 
        			 moduleName="M_StatoRichAz" addBlank="true" />
        <% if(canModify && !eUnaCopia) {%>
           <script language="javascript">//caricaStatiEvasione()</script>
        <%}%>
      </td>
    </tr>

    <tr>
      <td class="etichetta">Stato pubblicazione</td>
      <td class="campo">
           <af:textBox name="statoPub" type="text" classNameBase="input" value="<%=statoPubblic%>" readonly="true"/>
      </td>
    </tr>
    
    <%if (FLUSSO_VACANCY) {%>
    <tr>
      <td class="etichetta">Data ultimo invio</td>
      <td class="campo">
           <af:textBox name="dtUltimoInvioVacancy" type="text" classNameBase="input" value="<%=datInvioVacancy%>" readonly="true"/>
      </td>
    </tr>
    <%}%>
    
    <!-- ***** -->
    <tr><td><br/></td></tr>
    <tr><td><br/></td></tr>
    <tr>
      <td align ="center" colspan="2">
      <%
      if ((canModify) && !eUnaCopia) {
        if(!flag_insert) {
        %>
          <input class="pulsante" type="submit" name="salva" value="Aggiorna">
        <%
        } 
        else {
        %>
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci">
        <%
        }
      }
      %>
      </td>
	</tr>
	
	
  </table>
  
  <br/>
<%if( !codMonoCMCategoria.equals("") ){%>
	<input name="CODMONOCMCATEGORIAOLD" type="hidden" value="<%=codMonoCMCategoria%>"/>
<%}%>
<input name="CDNUTMOD" type="hidden" value="<%=cdnUtMod%>"/>
<input name="P_CDNUTENTE" type="hidden" value="<%=cdnUtMod%>"/>
<input type="hidden" name="PAGE" value="IdoGestStatoRichiestaPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<%-- <input type="hidden" name="keyLockStatoOcc" value="<%=Utils.notNull(keyLock)%>"/> --%>
<input type="hidden" name="prgRichiestaAZ" value="<%=prgRichiestaAz%>"/>


</af:form>
<af:form name="Frm2" method="POST" action="AdapterHTTP">
<table class="main">
<%  // INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	
	} else {		
	// END-PARTE-TEMP
%>
      <!-- informazioni storiche -->
      <%
      Vector storicoRows 	= serviceResponse.getAttributeAsVector("M_GETMOTIVOAPERTURARICH.ROWS.ROW");
	  SourceBean storico 	= null;
	  
	  String dataIns 		= "";
	  String motIns			= "";
	  String altMotIns		= "";
	  BigDecimal utente		= null;
	  String prgrichAz		= "";
	  
	  for (int i = 0; i < storicoRows.size(); i ++ ){
		prgrichAz	=  StringUtils.getAttributeStrNotNull(serviceRequest,"PRGRICHIESTAAZ");
	  }
	  if (storicoRows.size() > 0){
	  		btnInfStoriche = true;	  		
	  %>		
		<tr>
	      <td colspan="2" align="right" >
	  	  	<input type="button" class="pulsanti<%=((btnInfStoriche)?"":"Disabled")%>" <%=(!btnInfStoriche)?"disabled=\"true\"":""%>" value="Informazioni storiche" name="btnInfStoria"
			onClick="infoStoriche('ASStoricoAperturaPage','&PRGRICHIESTAAZ=<%=prgrichAz%>')">
		  </td>	 
	    </tr>
     <%}%>
<%  
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>	
    
<%  // INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	
	} else {		
	// END-PARTE-TEMP
		if (codEvasione.equals("AS") && (cdnStatoRich.intValue() == 4 || cdnStatoRich.intValue() == 5)) {
			
			Vector motivoRows 	= serviceResponse.getAttributeAsVector("COMBO_MOTIVO_RIAPERTURA.ROWS.ROW");
			SourceBean motivo 	= null;
			String codMotivo 	= "";
			String strAltroMotivo = " ";
			boolean required 	= false;
			for (int i = 0; i < motivoRows.size(); i ++ ){
				motivo 		= (SourceBean) motivoRows.get(i);
				codMotivo 	=  StringUtils.getAttributeStrNotNull(motivo, "CODMOTIVOAPERTURARICH");
			}
			
		
%>
		<tr>
	  		<td colspan="4">
		  		<div class="sezione2">
		    		<img id='imm1' alt="mostra/nascondi" src="../../img/chiuso.gif" onClick="onOff();controllaMotivazione()"/>&nbsp;&nbsp;&nbsp;Riapertura
		  		</div>
	  		</td>
		</tr>
		<tr>
			<td colspan="2" >
				<div id="dett" style="display:none">
					<table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">	
						<tr>
					    	<td class="etichetta">Motivo riapertura</td>
					      	<td class="campo">
					        <af:comboBox classNameBase="input" onChange="fieldChanged();controllaMotivazione()" name="motApertura" 
		        			 selectedValue="<%=codMotivo%>"  		        			  
		        			 moduleName="COMBO_MOTIVO_RIAPERTURA" addBlank="true" />
		        			</td>
		    			</tr>	    			
		    			<tr id="motivo">
		    				<td class="etichetta"></td>
		    				<td class="campo">
		    				<TEXTAREA name="altroMotivo" class="textarea" cols="60" rows="4"></TEXTAREA>		          				          			
		          			</td>
		          		</tr>
		          		<tr>
		          			<td colspan="2" align="center">
		          			<br>
							<input class="pulsante" type="button" onclick="controllaMotivo()" name="riapri" value="Riapri">
		          			<input name="CDNUTMOD" type="hidden" value="<%=cdnUtMod%>"/>
							<input type="hidden" name="PAGE" value="IdoGestStatoRichiestaPage"/>
							<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>	
							<input type="hidden" name="prgRichiestaAZ" value="<%=prgRichiestaAz%>"/>
							<!-- utilizzato per condizionare l'esecuzione del modulo per la riapertura della richiesta-->
							<input type="hidden" name="riapri" value="riapri"/>							
		          			</td>	          									
		          		<tr>
		          	</table>
		        </div>
	        </td>
		</tr>
<%  
		}
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
     
<p align="center">
<% if (!flag_insert) operatoreInfo.showHTML(out);%>
</p>
<br/>

</body>
</html>

<% } %>