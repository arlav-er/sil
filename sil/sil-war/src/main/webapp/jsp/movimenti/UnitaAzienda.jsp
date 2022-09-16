<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*
"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	/* 
		pagina che dovrebbe essere profilata. Per il momento impostiamo le variabili
		sempre a true.
		ATTENZIONE!!!! Soluzione temporanea da aggiustare al più presto.
		Chiedere a Carlo & Franco.
	*/

	//*************************************************************************************
	boolean canInsert = true;
	boolean canModify = true;
	//*************************************************************************************



	//Oggetti per l'applicazione dello stile grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

	//Inizializzazione variabili
	String strCodiceFiscale="";
	String strPartitaIva="";
	String strRagioneSociale="";
	String prgAzienda = "";
	String prgUnita = "";
	String prgUnitaInserito ="";
	String flgSede = "";
	String numRea = "";
	String strIndirizzoUAz = "";
	String strLocalita = "";	
	String strCodComUAz = "";
	String strDescComUAz = "";	
	String strCap = "";
	String codAzStato = "";	
	String strCodAtecoUAz = "";
	String strDesAtecoUAz = "";
	String strDescrTipoAtecoUAz = "";	
	String codCCNL="";
	String desCCNL="";
	String flgMezziPub = "";
	String strTelefono = "";
	String strFax = "";
	String strEmail = "";
	String posInps1 = "";
	String posInps2 = "";
	String posInps = "";
	String strRepartoInps = "";
	String strNumRegistroCommitt = "";
	String datRegistroCommit = "";
	String strRiferimentoSare = "";
	String datInizio = "";
	String datFine = "";
	String strNote = "";
	String prgMovimentoApp = "";
	String prgMobilitaApp = "";
	String descNatGiuridica = "";
	String codTipoAzienda = "";
	String descTipoAzienda = "";
	String strNumAlboInterinali ="";
	int numUnitaConIndirizzoDiverso = 0;
	String diffUnitaIndirizzo = "";
	
	String UgualeUnitaIndirizzo = "false";
	
	String pIvaMov = "";
 	String ragSocMov = "";
  	String codCcnlMov = "";
  	String descrCcnlMov = "";
  	String numIscrAlboIntMov = "";
 	String indirMov = "";
  	String capMov = "";
  	String codAtecoMov = "";
  	String telMov = "";
  	String FaxMov = "";
  	String emailMov = "";
  	String esistonoDifferenze = "false";
	
	int numUnitaConIndirizzoUguale = 0;
	
	String ragSocSommEstera = "";
	String cfSommEstera = "";
	 
	//Mi dice se sto operando per l'azienda principale o per quella utilizzatrice
	String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTESTO");
	String daMovimentiNew = StringUtils.getAttributeStrNotNull(serviceRequest, "daMovimentiNew");
	boolean contestoAzienda = contesto.equalsIgnoreCase("AZIENDA");
	boolean contestoAzUtil = contesto.equalsIgnoreCase("AZUTIL");
	
	prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
	prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOBILITAISCRAPP");
	prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
	String funz_agg = StringUtils.getAttributeStrNotNull(serviceRequest, "FUNZ_AGGIORNAMENTO");
	String context = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTEXT");
	
	//Recupero delle informazioni di testata
	SourceBean aziendaRow = (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW"); 
    if (aziendaRow != null) {
	    strCodiceFiscale=StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
	    strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
	    strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
	    descNatGiuridica=StringUtils.getAttributeStrNotNull(aziendaRow, "descNatGiuridica");
	    codTipoAzienda=StringUtils.getAttributeStrNotNull(aziendaRow, "codTipoAzienda");
	    descTipoAzienda=StringUtils.getAttributeStrNotNull(aziendaRow, "descTipoAzienda");
	    strNumAlboInterinali=StringUtils.getAttributeStrNotNull(aziendaRow, "strNumAlboInterinali");
	}

	//Indica se è riuscito l'inserimento della testata e se devo refreshare 
	//la pagina sottostante del dettaglio movimento
	boolean inserimentoTestataRiuscito = serviceResponse.containsAttribute("M_InsertTestataAzienda");
	//Nel caso di inserimento della testata il prgAzienda arriva dal modulo e non dalla request
	if (inserimentoTestataRiuscito) {
		prgAzienda = serviceResponse.getAttribute("M_InsertTestataAzienda.ROWS.ROW.PRGAZIENDA").toString();
	}
		
	//Indica se è fallito il precedente inserimento dell'unita
	String risultatoInserimento = (String) serviceResponse.getAttribute("M_InsertUnitaAziendaValidazione.INSERT_OK");
	boolean inserimentoUnitaFallito = "false".equalsIgnoreCase(risultatoInserimento);	
	
	//Indica se il precedente inserimento dell'unita è andato a buon fine
	boolean inserimentoUnitaRiuscito = "true".equalsIgnoreCase(risultatoInserimento);
	
	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
			
	//Recupero dati dell'unita 
	if (inserimentoUnitaFallito) {
		flgSede = StringUtils.getAttributeStrNotNull(serviceRequest, "flgSede");
		numRea = StringUtils.getAttributeStrNotNull(serviceRequest, "strREA");
		strIndirizzoUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "strIndirizzo");
		strLocalita = StringUtils.getAttributeStrNotNull(serviceRequest, "strLocalita");
		strCodComUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "codCom");
		strDescComUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "desComune");
		strCap = StringUtils.getAttributeStrNotNull(serviceRequest, "strCap");
		codAzStato = StringUtils.getAttributeStrNotNull(serviceRequest, "codAzStato");	
		strCodAtecoUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "codAteco");
		strDesAtecoUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "strAteco");
		strDescrTipoAtecoUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "strTipoAteco");	
		codCCNL=StringUtils.getAttributeStrNotNull(serviceRequest, "codCCNL");
	    desCCNL=StringUtils.getAttributeStrNotNull(serviceRequest, "desCCNL");
		flgMezziPub = StringUtils.getAttributeStrNotNull(serviceRequest, "flgMezziPub");
		strTelefono = StringUtils.getAttributeStrNotNull(serviceRequest, "strTel");
		strFax = StringUtils.getAttributeStrNotNull(serviceRequest, "strFax");
		strEmail = StringUtils.getAttributeStrNotNull(serviceRequest, "strEmail");
		posInps = StringUtils.getAttributeStrNotNull(serviceRequest, "STRPOSINPS");
		strRepartoInps = StringUtils.getAttributeStrNotNull(serviceRequest, "strRepartoInps");
		strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNUMREGISTROCOMMITT");
		datRegistroCommit = StringUtils.getAttributeStrNotNull(serviceRequest, "datRegistroCommit");
		strRiferimentoSare = StringUtils.getAttributeStrNotNull(serviceRequest, "strRiferimentoSare");
		datInizio = StringUtils.getAttributeStrNotNull(serviceRequest, "datInizio");
		datFine = StringUtils.getAttributeStrNotNull(serviceRequest, "datFine");
		strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "strNote");
		ragSocSommEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "RAGSOCAZESTERA");
		cfSommEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "CODFISCAZESTERA");
	} else if (!inserimentoUnitaRiuscito) {
		//Non ho ancora provato ad inserire, i dati mi arrivano dal movimenti sulla tabella di appoggio del DB
		if (!serviceRequest.containsAttribute("PRGMOBILITAISCRAPP")) {
			if (contestoAzienda && dataOrigin != null) {
				//Recupero quelli dell'azienda principale
				strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
				strCodComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codComuneUAz");
				strDescComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAz");
				strCap = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");	
				strCodAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");
				strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrAtecoUAz");							
				strTelefono = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");
				strFax = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz");
				strEmail = StringUtils.getAttributeStrNotNull(dataOrigin, "strEmailUAz");
				posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
				strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
				datRegistroCommit = StringUtils.getAttributeStrNotNull(dataOrigin, "DATREGISTROCOMMITT");
				strRiferimentoSare = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");	
				codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
				desCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz");
				//Determino il flgSede confrontando gli indirizzi sul movimento
				String strIndirizzoSede = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
				flgSede	= (strIndirizzoSede.equalsIgnoreCase(strIndirizzoUAz) ? "S" : "N");
				ragSocSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin, "RAGSOCAZESTERA");
				cfSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin, "CODFISCAZESTERA");
			} else if (contestoAzUtil && dataOrigin != null) {
				//Recupero quelli dell'azienda utilizzatrice
				strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");
				strCodComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODUAINTCOM");
				strDescComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");
				strCap = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAINTCAP");	
				strCodAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZINTATECO");
				strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrAtecoUAzUtil");
				codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZINTCCNL");
			}
		}
		else {
			dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
			if (contestoAzienda && dataOrigin != null) {
				//Recupero quelli dell'azienda principale
				strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAINDIRIZZO");
				strCodComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODUACOM");
				strDescComUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRDESCRCOMUNE");
				strCap = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUACAP");	
				strCodAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZATECO");
				strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRDESCRATECO");							
				strTelefono = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUATEL");
				strFax = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAFAX");
				strEmail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAEMAIL");
				posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
				strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
				codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZCCNL");
				desCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCCCNL");
				//Determino il flgSede confrontando gli indirizzi sul movimento
				String strIndirizzoSede = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAINDIRIZZO");
				flgSede	= (strIndirizzoSede.equalsIgnoreCase(strIndirizzoUAz) ? "S" : "N");			
			}
		}
	} else if (inserimentoUnitaRiuscito) {
		//Guardo se in sessione ho una scelta dell'utente in merito all'unita aziendale
		NavigationCache sceltaUnitaAzienda = null;
		if (!prgMobilitaApp.equals("")) {
			sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
		}
		else {
			sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA");
		}
		
		prgUnitaInserito = serviceResponse.getAttribute("M_InsertUnitaAziendaValidazione.ROWS.ROW.PRGUNITA").toString();
		
	  if(daMovimentiNew.equals("S")) {
		SourceBean unitaRow = (SourceBean) serviceResponse.getAttribute("M_GetUnitaAzienda.ROWS.ROW"); 
    	if (unitaRow != null) {
    		strIndirizzoUAz = StringUtils.getAttributeStrNotNull(unitaRow, "strIndirizzo");
			strDescComUAz = StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazione");
			strCap = StringUtils.getAttributeStrNotNull(unitaRow, "strCap");
			codAzStato = StringUtils.getAttributeStrNotNull(unitaRow, "codAzStato");	
			strCodAtecoUAz = StringUtils.getAttributeStrNotNull(unitaRow, "codAteco");
			strDesAtecoUAz = StringUtils.getAttributeStrNotNull(unitaRow, "strdesateco");
			strDescrTipoAtecoUAz = StringUtils.getAttributeStrNotNull(unitaRow, "tipo_ateco");	
			codCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "codccnl");
	    	desCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "desCCNL");
			strTelefono = StringUtils.getAttributeStrNotNull(unitaRow, "strTel");
			strFax = StringUtils.getAttributeStrNotNull(unitaRow, "strFax");
			posInps = StringUtils.getAttributeStrNotNull(unitaRow, "strnumeroinps");
			strRepartoInps = StringUtils.getAttributeStrNotNull(unitaRow, "STRREPARTOINPS");
			strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(unitaRow, "strNumRegistroCommitt");
			datRegistroCommit = StringUtils.getAttributeStrNotNull(unitaRow, "DATREGISTROCOMMIT");
			ragSocSommEstera = StringUtils.getAttributeStrNotNull(unitaRow, "RAGSOCAZESTERA");
			cfSommEstera = StringUtils.getAttributeStrNotNull(unitaRow, "CODFISCAZESTERA");
	    }
		
		Vector vectRicercaComuneUnitaAz = null;
		Vector vectorRicercaIndirizzoUnitaAz = new Vector();

	  	vectRicercaComuneUnitaAz = serviceResponse.getAttributeAsVector("M_MovRicercaUnitaAziendaCF.ROWS.ROW");
		
		for (int i = 0; i < vectRicercaComuneUnitaAz.size(); i++) {
			SourceBean unita = (SourceBean) vectRicercaComuneUnitaAz.get(i);
			String indUnita = StringUtils.getAttributeStrNotNull(unita, "strindirizzo");
			if (indUnita.equalsIgnoreCase(strIndirizzoUAz)) {
				vectorRicercaIndirizzoUnitaAz.add(unita);
			}
		}
		
		numUnitaConIndirizzoDiverso = vectRicercaComuneUnitaAz.size();
		numUnitaConIndirizzoUguale = vectorRicercaIndirizzoUnitaAz.size();
		
		if(numUnitaConIndirizzoDiverso == 1) diffUnitaIndirizzo = "true";
		else if(numUnitaConIndirizzoDiverso > 1) diffUnitaIndirizzo = "false";
		
		if(numUnitaConIndirizzoUguale == 1) UgualeUnitaIndirizzo = "true";
		else UgualeUnitaIndirizzo = "false";
		
		pIvaMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
		ragSocMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
		codCcnlMov = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
		descrCcnlMov = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz");
		numIscrAlboIntMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strNumAlboInterinali");
		indirMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
		capMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");
		codAtecoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");
		telMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");
		FaxMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz");
		emailMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strEmailUAz");
		
		int diffCounter = 0;
  		if (!pIvaMov.equals("") && !pIvaMov.equalsIgnoreCase(strPartitaIva)) {diffCounter += 1;}
  		if (!ragSocMov.equals("") && !ragSocMov.equalsIgnoreCase(strRagioneSociale)) {diffCounter += 1;}
  		if (!codCcnlMov.equals("") && !codCcnlMov.equalsIgnoreCase(codCCNL)) {diffCounter += 1;}
  		if (!numIscrAlboIntMov.equals("") && !numIscrAlboIntMov.equalsIgnoreCase(strNumAlboInterinali)) {diffCounter += 1;}
  		if (!indirMov.equals("") && !indirMov.equalsIgnoreCase(strIndirizzoUAz)) {diffCounter += 1;}
  		if (!capMov.equals("") && !capMov.equalsIgnoreCase(strCap)) {diffCounter += 1;}
  		if (!codAtecoMov.equals("") && !codAtecoMov.equalsIgnoreCase(strCodAtecoUAz)){diffCounter += 1;}
  		if (!telMov.equals("") && !telMov.equalsIgnoreCase(strTelefono)){diffCounter += 1;}
  		if (!FaxMov.equals("") && !FaxMov.equalsIgnoreCase(strFax)) {diffCounter += 1;}
  		
  		if (diffCounter > 0) { esistonoDifferenze = "true"; }
  		
	  }//chiudi if daMovimentiNew.equals("S")
		
		if (sceltaUnitaAzienda != null) {
			if (!prgMobilitaApp.equals("")) {
				if (!prgMobilitaApp.equals(sceltaUnitaAzienda.getField("PRGMOBILITAISCRAPP").toString())) {
				  	// cancello e ricreo il nuovo con i dati aggiornati
					sessionContainer.delAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
			    	String[] fields = {"PRGMOBILITAISCRAPP", "PRGAZIENDA", "PRGUNITA"};
			    	sceltaUnitaAzienda = new NavigationCache(fields);
			    	sceltaUnitaAzienda.enable();
			    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB", sceltaUnitaAzienda);			
			    	sceltaUnitaAzienda.setField("PRGMOBILITAISCRAPP", prgMobilitaApp);								    	
				}
			}
			else {
				//Controllo
				
				 //se si riferisce al movimento corrente o se è precedente
				if (prgMovimentoApp.equals(sceltaUnitaAzienda.getField("PRGMOVIMENTOAPP").toString())) {
					sceltaUnitaAzienda.setField("PRGAZIENDAUTIL", prgAzienda);	
					sceltaUnitaAzienda.setField("PRGUNITAUTIL", prgUnitaInserito);							
				} else {
				  // cancello e ricreo il nuovo con i dati aggiornati
				    sessionContainer.delAttribute("SCELTAUNITAAZIENDA");
			    	String[] fields = {"PRGMOVIMENTOAPP", "PRGAZIENDA", "PRGUNITA", "PRGAZIENDAUTIL", "PRGUNITAUTIL"};
			    	sceltaUnitaAzienda = new NavigationCache(fields);
			    	sceltaUnitaAzienda.enable();
			    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA", sceltaUnitaAzienda);			
			    	sceltaUnitaAzienda.setField("PRGMOVIMENTOAPP", prgMovimentoApp);
					sceltaUnitaAzienda.setField("PRGAZIENDAUTIL", prgAzienda);	
					sceltaUnitaAzienda.setField("PRGUNITAUTIL", prgUnitaInserito);								    	
				}
			}
			
		} else {
			// creo il nuovo
			if (!prgMobilitaApp.equals("")) {
				String[] fields = {"PRGMOBILITAISCRAPP", "PRGAZIENDA", "PRGUNITA"};
		    	sceltaUnitaAzienda = new NavigationCache(fields);
		    	sceltaUnitaAzienda.enable();
		    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB", sceltaUnitaAzienda);	
		    	sceltaUnitaAzienda.setField("PRGMOBILITAISCRAPP", prgMobilitaApp);
			}
			else {
		    	String[] fields = {"PRGMOVIMENTOAPP", "PRGAZIENDA", "PRGUNITA", "PRGAZIENDAUTIL", "PRGUNITAUTIL"};
		    	sceltaUnitaAzienda = new NavigationCache(fields);
		    	sceltaUnitaAzienda.enable();
		    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA", sceltaUnitaAzienda);	
		    	sceltaUnitaAzienda.setField("PRGMOVIMENTOAPP", prgMovimentoApp);		
				sceltaUnitaAzienda.setField("PRGAZIENDAUTIL", prgAzienda);	
				sceltaUnitaAzienda.setField("PRGUNITAUTIL", prgUnitaInserito);				
			}									    	
		}
	}

  //Gestione spezzatino posizione Inps
  posInps1 = posInps.substring(0, (posInps.length() >= 2 ? 2 : posInps.length()));
  posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), (posInps.length() >= 15 ? 15 : posInps.length()));

%>
<html>
<head>
  <title>Unità Azienda</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  
    <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>
  
  
<SCRIPT TYPE="text/javascript">
<!--

function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	
  if (codAteco.value == "") {
    strAteco.value = "";
    strTipoAteco.value = "";
  }
  else {
   // if (codAteco.value!=codAtecoHid.value) {
      window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", "toolbar=0, scrollbars=1");
   // }
  }
}

function ricercaAvanzataAteco() {
  window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", "toolbar=0, scrollbars=1");
}

function unificaPosInps() {
  //document.getElementById("STRPOSINPS").value = document.getElementById("STRPOSINPS1").value + document.getElementById("STRPOSINPS2").value;
  document.Frm1.STRPOSINPS.value = document.Frm1.STRPOSINPS1.value + document.Frm1.STRPOSINPS2.value;
}

// <CCNL>
function ricercaAvanzataCCNL() {
  window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", "toolbar=0, scrollbars=1, height=300, width=550");
}

function codCCNLUpperCase(inputName) {
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();");
  return true;
}

// </CCNL>

<% if ( (inserimentoTestataRiuscito || inserimentoUnitaRiuscito) && daMovimentiNew.equals("S") ) {%>
	var datacontainer = new Object();
    window.dati = datacontainer;
    datacontainer.funz_agg = "<%=funz_agg%>";
   	datacontainer.prgAzienda = "<%=prgAzienda%>";
   	datacontainer.prgUnita = "<%=prgUnitaInserito%>";
    datacontainer.strRagioneSociale = "<%=StringUtils.replace(strRagioneSociale,"\"","\\\"")%>";
    datacontainer.strPartitaIva = "<%=strPartitaIva%>";
    datacontainer.strCodiceFiscale = "<%=strCodiceFiscale%>";
   	datacontainer.strIndirizzo = "<%=strIndirizzoUAz%>";
 	datacontainer.desComune = "<%=strDescComUAz%>";
    datacontainer.strCap = "<%=strCap%>";
    datacontainer.codAteco = "<%=strCodAtecoUAz%>";
    datacontainer.strTipoAteco = "<%=strDesAtecoUAz%>";
    datacontainer.natGiuridicaAz = "<%=descNatGiuridica%>";
    datacontainer.CODTIPOAZIENDA = "<%=codTipoAzienda%>";
    datacontainer.DESCRTIPOAZIENDA = "<%=descTipoAzienda%>";
    datacontainer.STRNUMALBOINTERINALI = "<%=strNumAlboInterinali%>";
    datacontainer.diffUnitaIndirizzo = "<%=diffUnitaIndirizzo%>";
    datacontainer.esistonoDifferenze = "<%=esistonoDifferenze%>";
   	datacontainer.UgualeUnitaIndirizzo = "<%=UgualeUnitaIndirizzo%>";
    datacontainer.codCCNL = "<%=codCCNL%>";
    datacontainer.strCCNL = "<%=desCCNL%>";
    datacontainer.strTel = "<%=strTelefono%>";
    datacontainer.strFax = "<%=strFax%>";
    datacontainer.STRNUMREGISTROCOMMITT = "<%=strNumRegistroCommitt%>";
<%}%>


-->

</SCRIPT>

</head>
<body class="gestione" onload="<%=((inserimentoTestataRiuscito || inserimentoUnitaRiuscito) ? "window.opener." + funz_agg + "();" : "")%><%=(inserimentoUnitaRiuscito ? "window.close();" : "")%>">

<h2>Unità Azienda</h2>


<p>
  <font color="green"><af:showMessages prefix="M_InsertUnitaAziendaValidazione"/></font>
  <font color="red"><af:showErrors /></font>
</p>

  <p align="center">
  <af:form name="Frm1" method="POST" action="AdapterHTTP">
  <%out.print(htmlStreamTop);%>
  <table class="main">
  <tr>
    <td colspan="2" class="campo">
      Azienda: <b><%=strRagioneSociale%></b><br/>
      Partita Iva: <b><%=strPartitaIva%></b><br/>
      Codice Fiscale: <b><%=strCodiceFiscale%></b><br/>
      <br/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Sede legale </td>
    <td class="campo">
      <af:comboBox classNameBase="input" name="flgSede" title="Sede" selectedValue="<%=flgSede%>">
        <OPTION value=""></OPTION>
        <OPTION value="S">Sì</OPTION>
        <OPTION value="N">No</OPTION>
      </af:comboBox>
    </td>
  </tr>
  
  <%if (!cfSommEstera.equals("")) {%>
   	<tr valign="top">
     <td class="etichetta">C.F. Estera</td>
     <td class="campo">
       <af:textBox classNameBase="input" name="CODFISCAZESTERA" size="50" maxlength="100" value="<%=cfSommEstera%>" readonly="true" />
     </td>
  	</tr>
  	<tr valign="top">
     <td class="etichetta">Rag. Soc. Estera</td>
     <td class="campo">
       <af:textBox classNameBase="input" name="RAGSOCAZESTERA" size="50" maxlength="100" value="<%=ragSocSommEstera%>" readonly="true" />
     </td>
  	</tr>
  <%}%>
  
  <tr valign="top">
    <td class="etichetta">Numero REA</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strREA" title="Numero REA" size="12" value="<%=numRea%>" maxlength="11" />
    </td>
  </tr>
      
  <tr valign="top">
    <td class="etichetta">Indirizzo </td>
    <td class="campo">
      <af:textBox classNameBase="input" title="Indirizzo unità azienda" name="strIndirizzo" size="50" maxlength="60" value="<%=strIndirizzoUAz%>" required="true"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Localita</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strLocalita" size="50" value="<%=strLocalita%>" maxlength="100" />
    </td>
  </tr>
    
  <tr>
    <td class="etichetta">Comune</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid,document.Frm1.strCap, document.Frm1.strCapHid , 'codice');" type="text" title="comune unità azienda" name="codCom" value="<%=strCodComUAz%>" size="4" maxlength="4" required="true"/>&nbsp;
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, document.Frm1.strCap, 'codice','',null,'inserisciComUANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <af:textBox type="hidden" name="codComHid" value="<%=strCodComUAz%>"/>
    <af:textBox classNameBase="input" onKeyUp="PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, document.Frm1.strCap, document.Frm1.strCapHid, 'descrizione');" type="text" name="desComune" value="<%=strDescComUAz%>" size="30" maxlength="50" inline="
        onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\""/>&nbsp;
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, document.Frm1.strCap, 'descrizione', '',null,'inserisciComUANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <af:textBox type="hidden" name="desComuneHid" value="<%=strDescComUAz%>"/>&nbsp;
    </td>                
  </tr>   

  <tr valign="top">
    <td class="etichetta">Cap</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strCap" size="5" value="<%=strCap%>" maxlength="5"/>
      <af:textBox type="hidden" name="strCapHid" value="<%=strCap%>"/>&nbsp;
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Stato azienda</td>
    <td class="campo">
    <% codAzStato = !"".equals(codAzStato) ? codAzStato : "1"; %>
      <af:comboBox classNameBase="input" title="Stato azienda" name="codAzStato" moduleName="M_GetStatiAzienda" selectedValue="<%=codAzStato%>" addBlank="true" required="true"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Codice Attività</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="codAteco" title="Codice di Attività" size="6" maxlength="6" value="<%=strCodAtecoUAz%>" required="true"/>
      <af:textBox type="hidden" name="codAtecoHid" value="<%=strCodAtecoUAz%>"/>
        <a href="javascript:selectATECO_onClick(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strAteco,  document.Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
        <A href="javascript:ricercaAvanzataAteco();">Ricerca avanzata</A>
    </td>
  </tr> 

  <tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strTipoAteco" value="<%=strDescrTipoAtecoUAz%>" readonly="true" size="48" maxlength="50" />
    </td>
  </tr>

  <tr>
    <td class="etichetta">Attività</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strAteco" size="48" value="<%=strDesAtecoUAz%>" readonly="true" maxlength="100"/>
    </td>
  </tr>
      <tr>
        <td class="etichetta">Codice CCNL</td>
        <td class="campo" >
          <af:textBox classNameBase="input" title="Codice CCNL" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" size="5" maxlength="4" validateWithFunction="codCCNLUpperCase"  value="<%=codCCNL%>"  
          			  readonly="<%= String.valueOf(!canModify) %>"  required="true" />&nbsp;
          <% if(canInsert) {%>
          <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
          <%}%>
          <af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>" />
        </td>
	    </tr>
      <tr>
      	<td class="etichetta">Tipo CCNL</td>
      	<td class="campo">
  	          <af:textBox type="text" classNameBase="input" name="strCCNL" value="<%=desCCNL%>" size="48" maxlength="50"
  	           title="CCNL" readonly="true" />
          <af:textBox type="hidden" name="strCCNLHid" value="" />  	           
      	</td>
      </tr>    
  <tr valign="top">
    <td class="etichetta">Raggiungibile con mezzi pubblici</td>
    <td class="campo">
    <af:comboBox classNameBase="input" name="flgMezziPub" title="Raggiungibile con mezzi pubblici" selectedValue="<%=flgMezziPub%>">
        <OPTION value=""></OPTION>
        <OPTION value="S">Sì</OPTION>
        <OPTION value="N">No</OPTION>
      </af:comboBox>
    </td>
  </tr>
      
  <tr valign="top">
    <td class="etichetta">Telefono</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strTel" size="50" value="<%=strTelefono%>" maxlength="20"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Fax</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strFax" size="50" value="<%=strFax%>" maxlength="20"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Email</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strEmail" size="50" value="<%=strEmail%>" maxlength="80"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta" width="15%">Posizione INPS</td>
    <td class="campo" width="30%">
      <af:textBox classNameBase="input" onKeyUp="unificaPosInps();" name="STRPOSINPS1" title="Posizione Inps1" size="2" maxlength="2" value="<%=posInps1%>"/> - 
      <af:textBox classNameBase="input" onKeyUp="unificaPosInps();" name="STRPOSINPS2" title="Posizione Inps2" size="15" maxlength="13" value="<%=posInps2%>"/>
      <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>
    </td>
  </tr>
  
  <tr>
    <td class="etichetta">Iscr. Reg. Committenti</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="STRNUMREGISTROCOMMITT" title="Numero di iscrizione al registro committenti" size="10" maxlength="8" value="<%=strNumRegistroCommitt%>"/>
    </td>
  </tr>
  
  <tr>
  <td class="etichetta">Data Iscr. Reg. Committenti</td>
    <td class="campo">
      <af:textBox classNameBase="input" type="date" validateOnPost="true" title="Data Iscr. Reg. Committenti" name="datRegistroCommit" size="10" maxlength="10" value="<%=datRegistroCommit%>"/>
    </td>
  </tr>
  
  <tr>
    <td class="etichetta">Riferimento per pratica amm.</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strRiferimentoSare" title="Riferimento per pratica amministrativa" size="50" maxlength="100" value="<%=strRiferimentoSare%>"/>
    </td>
  </tr>
  
  <tr>
    <td class="etichetta">Reparto INPS</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strRepartoInps" title="Reparto INPS" size="50" maxlength="100" value="<%=strRepartoInps%>"/>
    </td>
  </tr>
  </table>

  <table class="main">
    <tr valign="top">
      <td class="etichetta">Data di inizio dell'attività</td>
      <td class="campo">
        <af:textBox classNameBase="input" type="date" validateOnPost="true" name="datInizio" size="10" maxlength="10" value="<%=datInizio%>"/>
      </td>
      <td class="etichetta">Data di fine dell'attività</td>
      <td class="campo">
        <af:textBox classNameBase="input" type="date" validateOnPost="true" name="datFine" size="10" maxlength="10" value="<%=datFine%>"/>
      </td>
      </tr>
          
  </table>
  
  <table class="main">
    <tr valign="top">
      <td class="etichetta">Note</td>
      <td class="campo">
        <af:textArea classNameBase="textarea" name="strNote" cols="50" value="<%=strNote%>" maxlength="1000" />
      </td>
    </tr>
  </table>
  <br>
  	<input class="pulsante" type="submit" name="inserisciUnita" value="Inserisci Unita"/>
  <%out.print(htmlStreamBottom);%>
	<center>
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
	</center>
    <input type="hidden" name="PAGE" value="MovimentiUnitaAziendaPage"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
    <input type="hidden" name="strRagioneSociale" value="<%=strRagioneSociale%>">
    <input type="hidden" name="strPartitaIva" value="<%=strPartitaIva%>">
    <input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>">
    <input type="hidden" name="diffUnitaIndirizzo" value="">
    <input type="hidden" name="UgualeUnitaIndirizzo" value="">
    <input type="hidden" name="esistonoDifferenze" value="">
    <input type="hidden" name="codComune" value="<%=strCodComUAz%>">
    <%if (!prgMovimentoApp.equals("")) {%>
	<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>">
	<input type="hidden" name="CONTEXT" value="<%=context%>"/>
	<%}
	else {%>
	<input type="hidden" name="PRGMOBILITAISCRAPP" value="<%=prgMobilitaApp%>">
	<%}%>  
	<input type="hidden" name="CONTESTO" value="<%=contesto%>">
	<input type="hidden" name="daMovimentiNew" value="<%=daMovimentiNew%>">
	<input type="hidden" name="FUNZ_AGGIORNAMENTO" value="<%=funz_agg%>">	
  </af:form>
</body>
</html>