<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant,
			it.eng.sil.util.*,
			java.math.*,
			java.text.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%	
	//progressivo della convenzione dal quale recupero SEMPRE tutti i dati necessari e indispensabile per le linguette	
	String fieldReadOnlyNoStato = "false";
	String prgConv	  = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCONV");
	if (!prgConv.equals("")) {
		fieldReadOnlyNoStato = "true";	
	}
		
	String  _page  	  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  	PageAttribs attributi = new PageAttribs(user, _page); 

  	boolean canModify = false;
  	boolean canSalvaStato = false;
  
  	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		canModify = attributi.containsButton("aggiorna");
  		canSalvaStato = attributi.containsButton("SALVA-STATO");
  	}

	String prgAziendaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAziendaApp");
	
	String goBackUrl = "";
	String goBackName = "Torna alla lista";
	String pageRitorno = "";
	String comeFrom = (String) sessionContainer.getAttribute("COMEFROM");
	if (comeFrom.equals("CL")) {
		pageRitorno = "CMCONVELISTAPAGE";	
	} else if (comeFrom.equals("CR")) {
		goBackName = "Torna alla ricerca";
	} else if (comeFrom.equals("A")) {
		pageRitorno = "CMLISTAASSUNZIONIPAGE";
	}
	goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_" + pageRitorno);
	

	SourceBean dettConv = (SourceBean) serviceResponse.getAttribute("CM_DETT_CONVENZIONI.ROWS.ROW");
	SourceBean rowDoc = (SourceBean) serviceResponse.getAttribute("CM_LOAD_CONV_DOC.ROWS.ROW");
	
if (dettConv != null && rowDoc != null) {
	
	String strAnno 				= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "anno", null));
	String strNumero 			= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "numero", null));
	String prgAzienda 			= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "PRGAZIENDA", null));
	String prgUnita             = Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "PRGUNITA", null));
	String ragSociale			= Utils.notNull(dettConv.getAttribute("ragionesociale"));
	String codiceFiscale 		= Utils.notNull(dettConv.getAttribute("cf"));
	String pIva					= Utils.notNull(dettConv.getAttribute("piva")); 
	String indirizzo            = Utils.notNull(dettConv.getAttribute("strIndirizzo"));
	String copertura			= Utils.notNull(dettConv.getAttribute("CODMONOINTERA"));
	String strNumLav			= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "NUMCOINVOLTI", null));	
	String dataConv				= Utils.notNull(dettConv.getAttribute("DATCONVENZIONE"));
	String statoConv			= Utils.notNull(dettConv.getAttribute("CODSTATORICHIESTA"));	
	String dataScad				= Utils.notNull(dettConv.getAttribute("DATSCADENZA"));
	String strDurata			= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "NUMDURATA", null));
	String flgPror				= Utils.notNull(dettConv.getAttribute("FLGPROROGA"));
	String flgModif				= Utils.notNull(dettConv.getAttribute("FLGMODIFICA"));
	String strRinunciaDis		= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "NUMRINUNCIADIS", null));
	String strRinunciaArt18		= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "NUMRINUNCIAART18", null));
	String strNote		        = Utils.notNull(dettConv.getAttribute("STRNOTE"));	
	BigDecimal cdnUtIns 		= SourceBeanUtils.getAttrBigDecimal(dettConv, "CDNUTINS", null);
	String dtmIns 				= Utils.notNull(dettConv.getAttribute("DTMINS"));
	BigDecimal cdnUtMod 		= SourceBeanUtils.getAttrBigDecimal(dettConv, "CDNUTMOD", null);
	String dtmMod 				= Utils.notNull(dettConv.getAttribute("DTMMOD"));
	String numKloConv           = String.valueOf(((BigDecimal)dettConv.getAttribute("NUMKLOCONV")).intValue());
	String codAmbitoTerr 		= Utils.notNull(dettConv.getAttribute("CODPROVINCIA"));
	
	prgConv	= Utils.notNull(SourceBeanUtils.getAttrBigDecimal(dettConv, "PRGCONV", null));
	if (!prgConv.equals("")) {
		fieldReadOnlyNoStato = "true";	
	}
	
	canModify = canModify && canSalvaStato;
	
  	boolean readOnlyStr = !canModify;
  	String fieldReadOnly = canModify ? "false" : "true";
  	boolean disableCodConv = false || readOnlyStr;
  	String fieldCodConv = disableCodConv ? "true" : "false";  	
  	
  	String noButtonSalvaStato  = "false";

	Calendar oggi = Calendar.getInstance();
	String giornoDB = Integer.toString(oggi.get(5));
	if (giornoDB.length() == 1){
		giornoDB = '0' + giornoDB;
	}
	String meseDB = Integer.toString(oggi.get(2)+1);
	if (meseDB.length() == 1){
		meseDB = '0' + meseDB;
	}
	String annoDB = Integer.toString(oggi.get(1));
	String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;

    if(statoConv.equals("AN")) {
		canModify = false;
	  	noButtonSalvaStato = "true";
	  	fieldCodConv = "true";
  	}

	String prAutomatica     = "S";
	String docInOut         = "I";
	String docRif           = "Documentazione L68";
	String docTipo          = "Convenzione";
	BigDecimal numProtV     = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numProtocollo", null);
	BigDecimal numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numAnnoProt", null);
	String dataOraProt      = SourceBeanUtils.getAttrStrNotNull(rowDoc, "datProtocollo");
	String datProtV     = "";
	String oraProtV     = "";
	if (!dataOraProt.equals("")) {
		oraProtV = dataOraProt.substring(11,16);
	  	datProtV = dataOraProt.substring(0,10);
  	} 	
	String CODSTATOATTO = SourceBeanUtils.getAttrStrNotNull(rowDoc, "CODSTATOATTO");
	
    if(CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")) {
  		fieldReadOnly = "true";
  	}
	
	Vector rowsProt     = null;
	SourceBean rowProt  = null;
	
	String totaleEsclusione = "";
	String totaleEsclusioneArt18 = "";
	String codMonoStatoProspetto= "";
	String codMonoCategoria = "";
	String numAnnoRifProspetto= "";
	String codMonocategoria= "";
	String numDipendentiTot= "";
	String numQuotaDisabili= "";
	String numDisabiliNom= "";
	String numDisabiliNum= "";
	String numQuotaArt18= "";
	String numArt18Nom= "";
	String numArt18Num= "";
	String numDisForza= "";
	String numDisForzaRifNomi= "";
	String numDisForzaNomi= "";
	String numDisForzaRifNume= "";
	String numDisForzaNume= "";
	String numArt18Forza= "";
	String numArt18ForzaRifNomi= "";
	String numArt18ForzaNomi= "";
	String numArt18ForzaRifNume= "";
	String numArt18ForzaNume= "";
	String numDisConvNume= "";
	String numDisConvNomi= "";
	String numArt18ConvNume= "";
	String numArt18ConvNomi= "";
	String numDisCompTerrNomi= "";
	String numDisCompTerrNume= "";
	String numArt18CompTerrNomi= "";
	String numArt18CompTerrNume= "";
	String numDisEsonNomi= "";
	String numDisEsonNume= "";
 
	// dati calcolati
	String numBaseComputoBD = "";	
	String numBaseComputoBDArt18 = "";
	BigDecimal numDisForzaNomiTot = new BigDecimal(0);
	BigDecimal numDisForzaNumeTot = new BigDecimal(0);
	String numDisConvTot = "";
	String numDisCompTerrTot = "";
	String numDisEsonTot = "";
	String disScoperturaTot = "";
	BigDecimal disScoperturaTotNomi = new BigDecimal(0);
	BigDecimal disScoperturaTotNume = new BigDecimal(0);
	BigDecimal numArt18ForzaNomiTot = new BigDecimal(0);
	BigDecimal numArt18ForzaNumeTot = new BigDecimal(0);
	String numArt18ConvTot = "";
	String numArt18CompTerrTot = "";
	String art18ScoperturaTot = "";
	BigDecimal art18ScoperturaTotNomi = new BigDecimal(0);
	BigDecimal art18ScoperturaTotNume = new BigDecimal(0);
	Object dtmInsProspetto = null;
	boolean checkProspetto2011 = true;
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CM_GETANDRICALCOLARIEPILOGOPROSP.ROWS.ROW");		
	if (prospetto != null) {
		
		numDipendentiTot= prospetto.getAttribute("numDipendentiTot") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDipendentiTot")).toString();
		totaleEsclusione = (prospetto.getAttribute("totaleEsclusione") == null || ("").equals(prospetto.getAttribute("totaleEsclusione")))? "0" : ((BigDecimal)prospetto.getAttribute("totaleEsclusione")).toString();
		totaleEsclusioneArt18 = (prospetto.getAttribute("totaleEsclusioneArt18") == null || ("").equals(prospetto.getAttribute("totaleEsclusioneArt18"))) ? "0" : ((BigDecimal) prospetto.getAttribute("totaleEsclusioneArt18")).toString();
		codMonoStatoProspetto= (String)prospetto.getAttribute("codMonoStatoProspetto");
		codMonoCategoria = (String)prospetto.getAttribute("codMonoCategoria");
		numAnnoRifProspetto= prospetto.getAttribute("numAnnoRifProspetto") == null? "" : ((BigDecimal)prospetto.getAttribute("numAnnoRifProspetto")).toString();
		numQuotaDisabili= prospetto.getAttribute("numquotadisabili") == null? "0" : ((BigDecimal)prospetto.getAttribute("numquotadisabili")).toString();
		numDisabiliNom= prospetto.getAttribute("numdisabilinom") == null? "0" : ((BigDecimal)prospetto.getAttribute("numdisabilinom")).toString();
		numDisabiliNum= prospetto.getAttribute("numdisabilinum") == null? "0" : ((BigDecimal)prospetto.getAttribute("numdisabilinum")).toString();
		numQuotaArt18= prospetto.getAttribute("numquotaart18") == null? "0" : ((BigDecimal)prospetto.getAttribute("numquotaart18")).toString();
		numArt18Nom= prospetto.getAttribute("numArt18nom") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18nom")).toString();
		numArt18Num= prospetto.getAttribute("numArt18num") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18num")).toString();
		numDisForza= prospetto.getAttribute("numdisforza") == null? "0" : ((BigDecimal)prospetto.getAttribute("numdisforza")).toString();
		numDisForzaRifNomi= prospetto.getAttribute("numDisForzarifnomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisForzarifnomi")).toString();
		numDisForzaNomi= prospetto.getAttribute("numDisForzanomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisForzanomi")).toString();
		numDisForzaRifNume= prospetto.getAttribute("numDisForzarifnume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisForzarifnume")).toString();
		numDisForzaNume= prospetto.getAttribute("numDisForzanume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisForzanume")).toString();
		numArt18Forza= prospetto.getAttribute("numart18forza") == null? "0" : ((BigDecimal)prospetto.getAttribute("numart18forza")).toString();
		numArt18ForzaRifNomi= prospetto.getAttribute("numArt18ForzaRifNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ForzaRifNomi")).toString();
		numArt18ForzaNomi= prospetto.getAttribute("numArt18ForzaNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ForzaNomi")).toString();
		numArt18ForzaRifNume= prospetto.getAttribute("numArt18ForzaRifNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ForzaRifNume")).toString();
		numArt18ForzaNume= prospetto.getAttribute("numArt18ForzaNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ForzaNume")).toString();
		numDisConvNume= prospetto.getAttribute("numDisConvNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisConvNume")).toString();
		numDisConvNomi= prospetto.getAttribute("numDisConvNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisConvNomi")).toString();
		numArt18ConvNume= prospetto.getAttribute("numArt18ConvNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ConvNume")).toString();
		numArt18ConvNomi= prospetto.getAttribute("numArt18ConvNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18ConvNomi")).toString();
		numDisCompTerrNomi= prospetto.getAttribute("numDisCompTerrNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisCompTerrNomi")).toString();
		numDisCompTerrNume= prospetto.getAttribute("numDisCompTerrNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisCompTerrNume")).toString();
		numArt18CompTerrNomi= prospetto.getAttribute("numArt18CompTerrNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18CompTerrNomi")).toString();
		numArt18CompTerrNume= prospetto.getAttribute("numArt18CompTerrNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numArt18CompTerrNume")).toString();
		numDisEsonNomi= prospetto.getAttribute("numDisEsonNomi") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisEsonNomi")).toString();
		numDisEsonNume= prospetto.getAttribute("numDisEsonNume") == null? "0" : ((BigDecimal)prospetto.getAttribute("numDisEsonNume")).toString();
				
	    // calcolo basecomputo = tot numero dipendenti - tot esclusioni	    
	    BigDecimal numDipendentiTotBD = new BigDecimal(numDipendentiTot);
	    
	    BigDecimal totaleEsclusioneBD = new BigDecimal(totaleEsclusione);
	    BigDecimal totaleEsclusioneBDArt18 = new BigDecimal(totaleEsclusioneArt18);
	    
		numBaseComputoBD = (numDipendentiTotBD.subtract(totaleEsclusioneBD)).toString();
		numBaseComputoBDArt18 = (numDipendentiTotBD.subtract(totaleEsclusioneBDArt18)).toString();
		
		// disabili in forza nominativo		
		numDisForzaNomiTot = (new BigDecimal(numDisForzaNomi)).add(new BigDecimal(numDisForzaRifNomi));		
		// disabili in forza numerico
		numDisForzaNumeTot = (new BigDecimal(numDisForzaNume)).add(new BigDecimal(numDisForzaRifNume));
		// art18 in forza nominativo		
		numArt18ForzaNomiTot = (new BigDecimal(numArt18ForzaNomi)).add(new BigDecimal(numArt18ForzaRifNomi));		
		// art18 in forza numerico
		numArt18ForzaNumeTot = (new BigDecimal(numArt18ForzaNume)).add(new BigDecimal(numArt18ForzaRifNume));
		//tot convenzioni disabili
		numDisConvTot = ((new BigDecimal(numDisConvNomi)).add(new BigDecimal(numDisConvNume))).toString();
		//tot convenzione art18 		
		numArt18ConvTot = ((new BigDecimal(numArt18ConvNomi)).add(new BigDecimal(numArt18ConvNume))).toString();		
		//tot comp terr disabili
		numDisCompTerrTot = ((new BigDecimal(numDisCompTerrNomi)).add(new BigDecimal(numDisCompTerrNume))).toString();
		//tot comp terr art18 		
		numArt18CompTerrTot = ((new BigDecimal(numArt18CompTerrNomi)).add(new BigDecimal(numArt18CompTerrNume))).toString();		
		//tot esoneri disabili
		numDisEsonTot = ((new BigDecimal(numDisEsonNomi)).add(new BigDecimal(numDisEsonNume))).toString();
		//scopertura nominativa disabile
		disScoperturaTotNomi = (new BigDecimal(numDisabiliNom)).subtract(numDisForzaNomiTot);
		disScoperturaTotNomi = disScoperturaTotNomi.subtract(new BigDecimal(numDisConvNomi));
		disScoperturaTotNomi = disScoperturaTotNomi.add(new BigDecimal(numDisCompTerrNomi));
		disScoperturaTotNomi = disScoperturaTotNomi.subtract(new BigDecimal(numDisEsonNomi));
		//scopertura nominativa art18	
		art18ScoperturaTotNomi = (new BigDecimal(numArt18Nom)).subtract(numArt18ForzaNomiTot);  
		art18ScoperturaTotNomi = art18ScoperturaTotNomi.subtract(new BigDecimal(numArt18ConvNomi));  
		art18ScoperturaTotNomi = art18ScoperturaTotNomi.add(new BigDecimal(numArt18CompTerrNomi));  				
		//scopertura numerica disabile
		disScoperturaTotNume = (new BigDecimal(numDisabiliNum)).subtract(numDisForzaNumeTot);
		disScoperturaTotNume = disScoperturaTotNume.subtract(new BigDecimal(numDisConvNume));
		disScoperturaTotNume = disScoperturaTotNume.add(new BigDecimal(numDisCompTerrNume));
		disScoperturaTotNume = disScoperturaTotNume.subtract(new BigDecimal(numDisEsonNume));
		//scopertura numerica art18		
		art18ScoperturaTotNume = (new BigDecimal(numArt18Num)).subtract(numArt18ForzaNumeTot);  
		art18ScoperturaTotNume = art18ScoperturaTotNume.subtract(new BigDecimal(numArt18ConvNume));  
		art18ScoperturaTotNume = art18ScoperturaTotNume.add(new BigDecimal(numArt18CompTerrNume));  
		//calcolo scopertura totale disabile
		disScoperturaTot = (disScoperturaTotNomi.add(disScoperturaTotNume)).toString();
		//calcolo scopertura totale art18
		art18ScoperturaTot = (art18ScoperturaTotNomi.add(art18ScoperturaTotNume)).toString();
		
		dtmInsProspetto = prospetto.getAttribute("dtmIns");
		
		if (dtmInsProspetto != null) {	
			String strDataInsProspetto = dtmInsProspetto.toString();
			String dataInsFormattata = strDataInsProspetto.substring(8, 10) + "/" + strDataInsProspetto.substring(5,7) + "/" +
				strDataInsProspetto.substring(0,4);
			if (DateUtils.compare(dataInsFormattata, ProspettiConstant.DATA_CHECK_2011) >= 0) {
				checkProspetto2011 = false;	
			}
		}
	}		
	
	//LINGUETTE
	Linguette l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgConv));
	l.setCodiceItem("PRGCONV");

	//INFORMAZIONI OPERATORE
	Testata operatoreInfo 	= 	null;
	operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	//LAYOUT DI PAGINA
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	String htmlStreamTopAz = StyleUtils.roundTopTable(false);
  	String htmlStreamBottomAz = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Dettaglio Convenzione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	

  function calcolaDurata() {

  	var mesi = "";
  	if (!validateDate('dataConv')) {
  	  document.Frm1.durata.value = mesi;
  	  return false;
  	} 
  	var strData1=document.Frm1.dataConv.value;
  	if (!validateDate('dataScad')) {
  	  document.Frm1.durata.value = mesi;
  	  return false;
  	}
  	var strData2=document.Frm1.dataScad.value;
  	
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
      	document.Frm1.durata.value = mesi;
      	alert("La "+ document.Frm1.dataScad.title +" è precedente alla "+ document.Frm1.dataConv.title);
	    return false;
   	  }
   	  else {
   	  	var mesi;
   	  	var data1time = data1.getTime(); 
  		var data2time = data2.getTime(); 
  		var difftime = Math.abs(data1time-data2time); 
  		mesi = parseInt(difftime/1000/60/60/24/30);
		document.Frm1.durata.value = mesi;
		return true;
   	  }  	
  	}
  	else {
  		document.Frm1.durata.value = mesi;
  		return true;
  	}
  }
  
  function verificaDati() {
	if (calcolaDurata())
		return true;
	else return false;
  }  
  
  function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
	    var url = "AdapterHTTP?";
	    <%if (comeFrom.equals("CR")) {%>
		    url+= "PAGE=CMConveRicercaPage" + "&cdnfunzione=<%=cdnfunzione%>";
			<%if (prgAzienda!=null) {%>
				url+="&prgAzienda=<%=prgAzienda%>";
				url+="&prgUnita=<%=prgUnita%>";
			<%}%>
			setWindowLocation(url);
		<%} else {%>
	    	setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	    <%}%>
  }

  function annullamentoDoc(){
	 var codStatoAtto = "<%=CODSTATOATTO%>";
	 document.Frm1.CODSTATOATTO.value = codStatoAtto;

	 var statoConv = document.Frm1.statoConv.value;
	 var oldStatoConv = "<%=statoConv%>";
	 if(statoConv == "AN"){
	 	document.Frm1.annullamento.value = 1;
	 }
	 if (statoConv == "DE" || oldStatoConv == "DE") {
	 	document.Frm1.ricalcoloProsp.value = 1;
	 }
  }

  function aggiornaDocumento(){
	var f;
	var codStatoAtto = document.Frm1.CODSTATOATTO.value;

	var codStatoAttoInReq = "<%=CODSTATOATTO%>";
 	if (codStatoAttoInReq == codStatoAtto){
		alert("Stato atto non modificato");
		return false;
	}
	
	else {
	
	 	var numAnnoProt = document.Frm1.numAnnoProt.value;
	 	var numProtocollo = document.Frm1.numProtocollo.value;
	 	var dataOraProt = document.Frm1.dataOraProt.value;
		var codTipoDocumento = document.Frm1.codTipoDocumento.value;
	 	var tipoProt = document.Frm1.tipoProt.value;
	 	var FlgcodMonoIO = document.Frm1.FlgcodMonoIO.value;
	 	var codAmbito = document.Frm1.codAmbito.value;
		var oldStatoConv = "<%=statoConv%>"; 	
	 		  		 
	 	f = "AdapterHTTP?PAGE=CMDatiGenConvPage&aggiornaDoc=1";
	 	f = f + "&CDNFUNZIONE=<%=cdnfunzione%>";
		f = f + "&PRGCONV=<%=prgConv%>";
		f = f + "&CODSTATOATTO=" + codStatoAtto;
		f = f + "&numAnnoProt=" + numAnnoProt;		  			  
		f = f + "&numProtocollo=" + numProtocollo;		  			  
		f = f + "&dataOraProt=" + dataOraProt;
		f = f + "&CODSTATORICHIESTA=<%=statoConv%>";
		f = f + "&prgAzienda=<%=prgAzienda%>";
		f = f + "&prgAziendaApp=<%=prgAziendaApp%>";
		f = f + "&prgUnita=<%=prgUnita%>";
		f = f + "&codTipoDocumento=" + codTipoDocumento;
		f = f + "&tipoProt=" + tipoProt;
		f = f + "&FlgcodMonoIO=" + FlgcodMonoIO;
		f = f + "&codAmbito=" + codAmbito;
			 
		if(codStatoAtto == "AN"){
		 	f = f + "&annullamento=1";
		 	f = f + "&NUMKLOCONV=<%=numKloConv%>";
		 	if (oldStatoConv == "DE") {
		 		f = f + "&ricalcoloProsp=1";
		 	}
		} else {
			f = f + "&annullamento=0";
		}
		 		  			  
		document.location = f;
	}
 }
  
</script>
<script language="Javascript">
<% if (!prgAzienda.equals("") && !prgUnita.equals("")) { %>
	if (window.top.menu != undefined){
		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>";	
	}
<% } %>
</script>
</head>

<body class="gestione" onload="rinfresca()">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="verificaDati()">

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
				</table>
    		</div>
    	</td>
    </tr>	
</table>  
<%out.print(htmlStreamBottomAz);%>
<p class="titolo">Dati generali della Convenzione</p>

<af:showErrors/>
<af:showMessages prefix="CM_INS_CONVENZIONE"/>
<af:showMessages prefix="CM_UPD_DATIGEN_CONVENZIONI"/>
<af:showMessages prefix="CM_SALVA_CONV_DOC"/>
<af:showMessages prefix="M_Annullamento_Conv_E_Doc"/>

<!-- stampa delle linguette -->
<%l.show(out);%>
	
<input type="hidden" name="PAGE" value=	"CMDatiGenConvPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>">
<input type="hidden" name="PRGCONV" value="<%=prgConv%>">
<input type="hidden" name="strChiaveTabella" value="<%=prgConv%>">
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>">
<input type="hidden" name="prgAziendaApp" value="<%=prgAziendaApp%>">
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="NUMKLOCONV" value="<%=numKloConv %>">

<%out.print(htmlStreamTop);%>
<%@ include file="_protocollazioneconvenzione.inc" %>
			
<script>	
 
 function gestione_Protocollazione() {
	<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
	var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
	if (!protocolloLocale) return;
 
	<% if(CODSTATOATTO.equals("NP")) { %>
		if (document.Frm1.CODSTATOATTO.value == "PR"){
		<%  
		rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
  		if (rowsProt != null && !rowsProt.isEmpty()) { 
			rowProt = (SourceBean) rowsProt.elementAt(0);
	   		numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
   			numAnnoProtV = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
	   		dataOraProt = (String) rowProt.getAttribute("DATAORAPROT");
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
	  	}
  		%>
  		document.Frm1.oraProt.value = "<%=oraProtV%>";
		document.Frm1.dataProt.value= "<%=datProtV%>"; 
		document.Frm1.numProtocollo.value="<%=numProtV%>";  
		document.Frm1.numAnnoProt.value= "<%=numAnnoProtV%>";
		document.Frm1.dataOraProt.value="<%=dataOraProt%>"; 
  	}
  	if (document.Frm1.CODSTATOATTO.value == "AN" || document.Frm1.CODSTATOATTO.value == "NP") {
		document.Frm1.numAnnoProt.value = "";
  		document.Frm1.numProtocollo.value = "";
  		document.Frm1.dataProt.value = "";
  		document.Frm1.oraProt.value = "";
  		document.Frm1.dataOraProt.value = "";
	}
	<%}%>		 	
 }
 
</script>
		    
<table class="main">
	<tr valign="top">
    	<td class="etichetta2" >Anno</td>
        <td class="campo2" >
            <af:textBox classNameBase="input" type="integer" 
                        name="anno" value="<%=strAnno%>"
                        readonly="true" disabled="true"
                        maxlength="4" size="10"/>
            &nbsp;&nbsp;Numero&nbsp;&nbsp;
            <af:textBox classNameBase="input" type="integer"
		                name="numero" value="<%=strNumero%>"
		                readonly="true" disabled="true"
		                maxlength="10" size="10"/>
        </td>          
    </tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
    <tr>
		<td class="etichetta">Ambito Territoriale</td>
	    <td colspan=3 class="campo">
	    	 <af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=codAmbitoTerr%>"
        				classNameBase="input" addBlank="true" title="Ambito Territoriale" required="true" disabled="<%=fieldReadOnlyNoStato%>" />
	    </td>
	</tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
    <tr valign="top">
      	<td class="etichetta2">Data convenzione</td>
      	<td class="campo2">
	        <af:textBox classNameBase="input" title="Data convenzione" type="date" 
			        	validateOnPost="true" value="<%=dataConv%>" 
			        	name="dataConv" size="11" maxlength="10" 
			        	readonly="<%=fieldReadOnly%>" required="true" />
      		&nbsp;&nbsp;Data Scadenza&nbsp;&nbsp;    
	        <af:textBox classNameBase="input"
	                    title="Data scadenza"
	                    readonly="<%=fieldReadOnly%>"
	                    type="date"
	                    validateOnPost="true"
	                    name="dataScad"
	                    size="11"
	                    maxlength="10"
	                    value="<%=dataScad%>"/>
	    	&nbsp;&nbsp;Durata&nbsp;&nbsp;
	        <af:textBox classNameBase="input" type="integer"
		                readonly="<%=fieldReadOnly%>"
		                name="durata" value="<%=strDurata%>"
		                maxlength="10" size="10"/>
			<%if (fieldReadOnly.equals("false")) {%>
		    <A onClick="calcolaDurata();"><IMG name="image" border="0" src="../../img/calc.gif" title="calcola durata convenzione"/></a>
      		<%} %>
      	</td>
    </tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
	    <td class="etichetta2">Stato convenzione</td>
	    <td class="campo2">
	    	<af:comboBox classNameBase="input" name="statoConv" disabled="<%=fieldCodConv%>" selectedValue="<%=statoConv %>" moduleName="CM_MULTI_LIST_CONVENZIONI"/>
	      	&nbsp;&nbsp;Copertura&nbsp;&nbsp; 
	    	<af:comboBox 
	          name="copertura"
	          classNameBase="input"
	          disabled="<%=fieldReadOnly%>">					  
		          <option value=""  <% if ( "".equalsIgnoreCase(copertura) )  { %>SELECTED="true"<% } %>></option>
		          <option value="I" <% if ( "I".equalsIgnoreCase(copertura) ) { %>SELECTED="true"<% } %>>Copertura dell'intera</option>
		          <option value="P" <% if ( "P".equalsIgnoreCase(copertura) ) { %>SELECTED="true"<% } %>>Quota di riserva o parziale</option>
	        </af:comboBox> 
	        <input type="hidden" name="ricalcoloProsp" value="0">       
	    </td>
    </tr>   
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
	    <td class="etichetta2" >Num. lav. coinvolti</td>
	    <td class="campo2">
            <af:textBox classNameBase="input" type="integer"
		                readonly="<%=fieldReadOnly%>" value="<%=strNumLav%>"
		                name="lavoratori"
		                title="Num. lav. coinvolti"
		                maxlength="10" size="10"
						validateOnPost="true"/>
		    &nbsp;&nbsp;Num. rinuncia disabili&nbsp;&nbsp;            
            <af:textBox classNameBase="input" type="integer"
		                readonly="<%=fieldReadOnly%>" value="<%=strRinunciaDis%>"
		                name="rinunciaDis"
		                title="Num. rinuncia disabili"
		                maxlength="10" size="10"
						validateOnPost="true"/>
	    	&nbsp;&nbsp;Num. rinuncia Art. 18&nbsp;&nbsp;
            <af:textBox classNameBase="input" type="integer"
		                readonly="<%=fieldReadOnly%>" value="<%=strRinunciaArt18%>"
		                name="rinunciaArt18"
		                title="Num. rinuncia Art. 18"
		                maxlength="10" size="10"
						validateOnPost="true"/>
	    </td>
    </tr>    
	<tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
      	<td class="etichetta2">Prorogata</td>
      	<td class="campo2">
	        <af:comboBox 
	          name="proroga"
	          classNameBase="input"
	          disabled="<%=fieldReadOnly%>">					  
		          <option value=""  <% if ( "".equalsIgnoreCase(flgPror) )  { %>SELECTED="true"<% } %> ></option>
		          <option value="S" <% if ( "S".equalsIgnoreCase(flgPror) ) { %>SELECTED="true"<% } %> >Sì</option>
		          <option value="N" <% if ( "N".equalsIgnoreCase(flgPror) ) { %>SELECTED="true"<% } %> >No</option>
	        </af:comboBox>
      		&nbsp;&nbsp;Modificata&nbsp;&nbsp;     	
	        <af:comboBox 
	          name="modifica"
	          classNameBase="input"
	          disabled="<%=fieldReadOnly%>">					  
		          <option value=""  <% if ( "".equalsIgnoreCase(flgModif) )  { %>SELECTED="true"<% } %> ></option>
		          <option value="S" <% if ( "S".equalsIgnoreCase(flgModif) ) { %>SELECTED="true"<% } %> >Sì</option>
		          <option value="N" <% if ( "N".equalsIgnoreCase(flgModif) ) { %>SELECTED="true"<% } %> >No</option>
	        </af:comboBox>	        
      	</td>
    </tr>
	<tr valign="top">
    	<td>&nbsp;</td>
    </tr>    
	<tr valign="top">
		<td class="etichetta2">Note</td>
    	<td class="campo2">
          	<af:textArea cols="70" rows="4" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
              		name="STRNOTE"
               		value="<%=strNote%>" validateOnPost="true" 
                    required="false" title="Note"/>    	
    	</td>
    </tr>
</table>
<br>
<br>
<% if (prospetto == null) { %>
<ul>
<li class="top">Prospetto non presente.</li>
</ul>
<% } %>
<table>
	<tr><td><div class="sezione2">Dati Prospetto</div></td></tr>
	<tr>
    	<td colspan="6" class="cal_bordato">
			<table>					
				<tr>	
					<td class="etichetta2">Stato</td>
					<td class="campo2">
						<af:comboBox name="codMonoStatoProspetto" classNameBase="input" disabled="true">	  	
			    			<option value=""  <% if ( "".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %> ></option>            
	            			<option value="A" <% if ( "A".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>In corso d'anno</option>
				            <option value="S" <% if ( "S".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>Storicizzato</option>
				            <option value="U" <% if ( "U".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>Storicizzato:uscita dall'obbligo</option>              
				        </af:comboBox> 
	    			</td>
					<td class="etichetta2">Anno</td>
					<td class="campo2">
						<af:textBox type="fixdecimal" title="Anno" name="numannorifprospetto" value="<%= numAnnoRifProspetto%>" size="4" maxlength="4" readonly="true"/>
	    			</td>
				</tr>
				
				
				
				<tr>    
					<td class="etichetta2">Fascia</td>
					<td class="campo2">
						<af:comboBox name="codMonoCategoria" classNameBase="input" disabled="true">	  
				            <option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>></option>
				            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>più di 50 dipendenti</option>
				           	<option value="B" <% if ( "B".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 36 a 50 dipendenti</option>               
				           	<option value="C" <% if ( "C".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 15 a 35 dipendenti</option> 
				        </af:comboBox> 
				    </td>
				    <%if (checkProspetto2011) {%>
					    <td class="etichetta2">Base di computo</td>
						<td class="campo2">
							<af:textBox type="fixdecimal" title="Base di computo" className="viewRiepilogo" name="numbasecomputo" value="<%= numBaseComputoBD%>" size="4" maxlength="4" readonly="true" />
					    </td>
					 
					 <%} 
					 else {%>
					 	<td class="etichetta2">Base di computo Art.3&nbsp;
							<af:textBox type="fixdecimal" title="Base di computo" className="viewRiepilogo" name="numbasecomputo" value="<%= numBaseComputoBD%>" size="4" maxlength="4" readonly="true" />
						</td>
						<td class="etichetta2">Base di computo Art.18&nbsp;
							<af:textBox type="fixdecimal" title="Base di computo" className="viewRiepilogo" name="numbasecomputoart18" value="<%= numBaseComputoBDArt18%>" size="4" maxlength="4" readonly="true" />
					    </td>
					 <%}%>  
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>  
					<td colspan="4">  
						<table class="main">
							<tr>
								<td>&nbsp;</td>
								<td class="campo_readFree">Disabili</td>
								<td class="campo_readFree">Art. 18</td>					
							</tr>
							<tr>
								<td width="33%">
									<table>
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga1" value="&nbsp;" readonly="true"/></td></tr>
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga2" value="Quota" readonly="true"/></td></tr>
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga3" value="In forza" readonly="true"/></td></tr>
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga4" value="In Convenzione" readonly="true"/></td></tr>
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga5" value="Comp. Terr." readonly="true"/></td></tr>								
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga6" value="Esonero" readonly="true"/></td></tr>	
										<tr><td style="padding: 3px;"><af:textBox type="text" className="viewRiepilogoTrasparent" title="" name="riga7" value="Scopertura" readonly="true"/></td></tr>							
									</table>
								</td>
								<td>
									<table class="appuntamenti">
										<tr>
											<td class="campocentrato">&nbsp;</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Quota totale" name="numQuotaDisabili" value="<%= numQuotaDisabili%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>	  
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="In forza" name="numDisForza" value="<%= numDisForza%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>	
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="In convenzione" name="numDisConvTot" value="<%= numDisConvTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>	
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Competenze territoriali" name="numDisCompTerrTot" value="<%= numDisCompTerrTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Esonero" name="numDisEsonTot" value="<%= numDisEsonTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Scopertura" name="disScoperturaTot" value="<%= disScoperturaTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>					
									</table>
								</td>
								<td>
									<table class="appuntamenti">
										<tr>
											<td class="campocentrato">&nbsp;</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Quota totale" name="numQuotaArt18" value="<%= numQuotaArt18%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>	
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="In forza" name="numArt18Forza" value="<%= numArt18Forza%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>	
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="In convenzione" name="numArt18ConvTot" value="<%= numArt18ConvTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Esonero" name="numArt18CompTerrTot" value="<%= numArt18CompTerrTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>
										<tr>
											<td class="campocentrato" style="padding: 3px;">									
												<af:textBox type="fixdecimal" className="viewRiepilogoTrasparent" title="" name="vuoto" value="" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>
										<tr>
											<td class="campocentrato">									
												<af:textBox type="fixdecimal" className="viewRiepilogo" title="Scopertura" name="art18ScoperturaTot" value="<%= art18ScoperturaTot%>" size="2" maxlength="4" readonly="true"/>
											</td>
										</tr>					
									</table>
								</td>					
							</tr>
						</table>		
					</td>
				</tr>
			</table>
		</td> 				    
	</tr>
</table>
<br>
<br>
<table>	
	<%if (canModify && !CODSTATOATTO.equals("AN")) {%>
	<tr>
	  	<td align="right" colspan="2">
			<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc()"/>
			<input type="hidden" name="annullamento" value="0">
			&nbsp;&nbsp;&nbsp;&nbsp; 
			<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<%} %>	
	<tr>
	  	<td align="center">
			<input type="button" class="pulsante" name="back" value="<%=goBackName%>" onclick="goBack()" />
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
</body>
</html>
<%} else { %>
<html>
<head>
<script language="Javascript">
		var f;	   
		var miaData = new Date();
		var anno = miaData.getFullYear();
		f = "AdapterHTTP?PAGE=CMInsConvenzionePage";
 		f = f + "&CDNFUNZIONE=<%=cdnfunzione%>";
		f = f + "&anno=" + anno;
		f = f + "&CODSTATOATTO=NP";
		f = f + "&CODSTATOCONV=PO";
		f = f + "&messageError=1";
		<%if (!prgAziendaApp.equals("")) {
			String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
			String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita"); %>
			f = f + "&prgAzienda=<%=prgAzienda%>";
			f = f + "&prgUnita=<%=prgUnita%>";
		<%}%>
		f = f + "&goBackListaPage=<%=pageRitorno%>";
		 		
		document.location = f;
</script>
</head>
<body>
</body>
</html>
<%} %> 