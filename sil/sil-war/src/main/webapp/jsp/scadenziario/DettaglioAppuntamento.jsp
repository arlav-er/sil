<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%
//profilatura
PageAttribs attributi = new PageAttribs(user, "DettaglioAppuntamentoPage");

List sezioni = attributi.getSectionList();

boolean invioSMSCig = sezioni.contains("INVIO_SMS_CIG");

//Quando si proviene dal patto
boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI");
Vector codLstTab = null;
String statoSezioni = "";
String nonFiltrare = "";
if(fromPattoAzioni) {
	codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
	nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");
}
if (codLstTab==null) codLstTab = new Vector(0);
Testata operatoreInfo = null;
String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

Vector rowsEsito = serviceResponse.getAttributeAsVector("COMBO_ESITO_APPUNTAMENTO.ROWS.ROW");

String PRGAPPUNTAMENTO = (String) serviceRequest.getAttribute("PRGAPPUNTAMENTO");
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strCodScadenza = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strStatoValCV = serviceRequest.containsAttribute("statoValCV")? serviceRequest.getAttribute("statoValCV").toString():"";
String strPrgRosa = serviceRequest.containsAttribute("PRGROSA")? serviceRequest.getAttribute("PRGROSA").toString():"";
String strCpiRose = serviceRequest.containsAttribute("CPIROSE")? serviceRequest.getAttribute("CPIROSE").toString():"";
String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ")? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString():"";
String strCognome = serviceRequest.containsAttribute("cognome")? serviceRequest.getAttribute("cognome").toString():"";
String strNome = serviceRequest.containsAttribute("nome")? serviceRequest.getAttribute("nome").toString():"";
String strCF = serviceRequest.containsAttribute("CF")? serviceRequest.getAttribute("CF").toString():"";
String strDidRilasciata = serviceRequest.containsAttribute("didRilasciata")? serviceRequest.getAttribute("didRilasciata").toString():"";
String strCodStataOcc = serviceRequest.containsAttribute("codStatoOcc")? serviceRequest.getAttribute("codStatoOcc").toString():"";
String strViewNonPresentati = serviceRequest.containsAttribute("viewNonPresentati")? serviceRequest.getAttribute("viewNonPresentati").toString():"";
String strNumVol = serviceRequest.containsAttribute("NumVol")? serviceRequest.getAttribute("NumVol").toString():"";
String strDataNP = serviceRequest.containsAttribute("dataNP")? serviceRequest.getAttribute("dataNP").toString():"";
String strCategoria181 = serviceRequest.containsAttribute("categoria181")? serviceRequest.getAttribute("categoria181").toString():"";
String strLegge407_90 = serviceRequest.containsAttribute("legge407_90")? serviceRequest.getAttribute("legge407_90").toString():"";
String strLungaDur = serviceRequest.containsAttribute("lungaDur")? serviceRequest.getAttribute("lungaDur").toString():"";
String strRevRic = serviceRequest.containsAttribute("revRic")? serviceRequest.getAttribute("revRic").toString():"";
String dataDalSlot   = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";
String CodCpiApp = serviceRequest.containsAttribute("CodCpiApp")? serviceRequest.getAttribute("CodCpiApp").toString():"";

String strNomeCig = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.strNome");
String strCognomeCig = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.strCognome");
String strCFCig = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.strCodicefiscale");
String strCellCig = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.strCell");
String esisteCig = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.esisteCig");
String data = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.data");
String ora = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.ora");
String cpi = StringUtils.getAttributeStrNotNull(serviceResponse,"M_getInfoLavoratorePerCig.ROWS.ROW.strDescrizione");

String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";

//Abilitazione invio sms per promemoria CIG
String invioSmsCig = "true";

if(!strCdnLavoratore.equals("")&&esisteCig.equals("S")) invioSmsCig = "false";

//Codice CPI dell'utente collegato
String codCpiOperatore = user.getCodRif();

String strLavoratore = strCognomeCig+" "+strNomeCig+" "+strCFCig;

Vector listaModelli = serviceResponse.getAttributeAsVector("M_getInfoPerInvioSmsCig.ROWS.ROW");

User userCurr = (User) sessionContainer.getAttribute(User.USERID);
InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
	infCorrentiLav.setSkipLista(true);
}
else {
  infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
}

String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";
  
SourceBean cont = (SourceBean) serviceResponse.getAttribute("SELECT_DETTAGLIO_AGENDA_MOD");
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
String datApp = row.containsAttribute("DATA") ? row.getAttribute("DATA").toString() : "";
String strOraApp = row.containsAttribute("ORARIO") ? row.getAttribute("ORARIO").toString() : "";
BigDecimal prgSpiApp = null, prgSpiAppEff=null;
prgSpiApp = (BigDecimal) row.getAttribute("PRGSPI");
prgSpiAppEff = (BigDecimal) row.getAttribute("PRGSPIEFF");
String strSpiApp = "", strSpiAppEff="";
if (prgSpiApp != null) {
  strSpiApp = prgSpiApp.toString();
}
if (prgSpiAppEff != null) {
  strSpiAppEff = prgSpiAppEff.toString();
}
String codServizioApp = row.containsAttribute("CODSERVIZIO") ? row.getAttribute("CODSERVIZIO").toString() : "";
BigDecimal prgAmbiente = null;
prgAmbiente = (BigDecimal) row.getAttribute("PRGAMBIENTE");
String strAmbiente = "";
if (prgAmbiente != null) {
  strAmbiente = prgAmbiente.toString();
}

BigDecimal nDurataMinuti = null;
nDurataMinuti = (BigDecimal) row.getAttribute("NUMMINUTI");
String strDurata = "";
if (nDurataMinuti != null) {
  strDurata = nDurataMinuti.toString();
}
String txtNoteApp = row.containsAttribute("TXTNOTE") ? row.getAttribute("TXTNOTE").toString() : "";
String CODEFFETTOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODEFFETTOAPPUNT");
String CODESITOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODESITOAPPUNT");
String CODSTATOAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(row, "CODSTATOAPPUNTAMENTO");
String OLD_STATO = CODSTATOAPPUNTAMENTO;
String numKloAgenda = row.getAttribute("NUMKLOAGENDA").toString();

boolean editable = true;
String htmlStreamTop = StyleUtils.roundTopTable(editable);
String htmlStreamBottom = StyleUtils.roundBottomTable(editable);


String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String msgAllineamento = "";
if(serviceResponse.containsAttribute("MALLINEASLOT")) {
  SourceBean slot = (SourceBean) serviceResponse.getAttribute("MALLINEASLOT");
  SourceBean rowAllineamento = (SourceBean) slot.getAttribute("ROW");
  String CodiceRit = "";
  if(rowAllineamento!=null) { CodiceRit = StringUtils.getAttributeStrNotNull(rowAllineamento, "CodiceRit"); }
  if(CodiceRit.equals("0")) { msgAllineamento = "Allineamento Slot avvenuto con successo"; }
  else { 
    if(CodiceRit.equals("-1")) { msgAllineamento = "Errore nell'esecuzione della procedura."; }
    else {  msgAllineamento = "Allineamento Slot non riuscito"; }
  }
}

String tipoGruppo = user.getCodTipo();
boolean canModifyAppuntamento = true;
boolean operatorePatronato = false;
if (tipoGruppo.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
	canModifyAppuntamento = false;
	operatorePatronato = true;
}

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}


//modifiche per invio esito appuntamento ad ANPAL - Febbraio 2020 Marianna Borriello
boolean canInvioAnpal = attributi.containsButton("INVIO_APP_ANPAL_LAV");
String flgInvioEsitoAnpal = StringUtils.getAttributeStrNotNull(row,"FLGINVIOMIN_ESITOAPP");
String idAppuntamentoAnpal = StringUtils.getAttributeStrNotNull(row,"idAnpal");
boolean isAppAnpal = false;
if(StringUtils.isFilledNoBlank(idAppuntamentoAnpal)){
	isAppAnpal = true;
}

String codEsitoMinAppuntamento = StringUtils.getAttributeStrNotNull(row,"CODMINESITOAPP");
String codCpiMin = StringUtils.getAttributeStrNotNull(row,"CODCPIMIN");
String codCpiAnpal = StringUtils.getAttributeStrNotNull(row, "CODCPI");
boolean rigaEsitoAnpalOk = serviceResponse.containsAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_OK");
String msgEsitoAnpal = null;
if(rigaEsitoAnpalOk){
	msgEsitoAnpal = (String) serviceResponse.getAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_OK");
} 
boolean rigaEsitoAnpalKo = serviceResponse.containsAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_KO");
if(rigaEsitoAnpalKo){
	msgEsitoAnpal = (String) serviceResponse.getAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_KO");
}
//Dati ultimo invio anpal
boolean viewUltimoInvioAnpal = false;
String dataUltimoInvioAnpal="";
String esitoNotificaUltimoInvioAnpal = "";
String esitoAppuntamentoUltimoInvioAnpal = "";

if(serviceResponse.containsAttribute("GetAppUltimoInvioAnpal") && serviceResponse.getAttribute("GetAppUltimoInvioAnpal") !=null){
	SourceBean ultimoInvioAnpal = (SourceBean) serviceResponse.getAttribute("GetAppUltimoInvioAnpal.ROWS.ROW");
	if(ultimoInvioAnpal!=null){
		dataUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "dataInvio");
		esitoNotificaUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "esitoNotifica");
		esitoAppuntamentoUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "esitoAppuntamento");
		viewUltimoInvioAnpal = true;
	}
}
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script src="../../js/ComboPair.js"></script>
  
  <title></title>
  <script type="text/javascript">
    var flagChanged = false;  
  	listaModelli = new Array();
  	
    function chiudi () { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frmAppuntamento.PAGE.value = "ScadAppuntamentoPage";
      doFormSubmit(document.frmAppuntamento);
    }
    
    function aggiorna () { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  if(checkObbligatorio("CODSTATOAPPUNTAMENTO")){
	      document.frmAppuntamento.PAGE.value = "DettaglioAppuntamentoPage";
	      document.frmAppuntamento.aggiornaFromLav.value = "1";
	      doFormSubmit(document.frmAppuntamento);
      }
    }    
  	
  	//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
  	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
	
	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=true;
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src=imgChiusa;
	    	immagine.alt="Apri";
		}
		else {
			sezione.style.display="inline";
			sezione.aperta=true;
			immagine.src=imgAperta;
	    	immagine.alt="Chiudi";
		}
	}
	
	function inizializza_modelli(){
		<%if(!isAppAnpal){%>
			document.getElementById("invioAnpal").disabled = true;
       		document.getElementById("invioAnpal").className = "pulsantiDisabled";
       	<%}%>
		<%for(int i=0;i<listaModelli.size();i++){
     		SourceBean modello = (SourceBean)listaModelli.get(i);
     		String codTipoSms = StringUtils.getAttributeStrNotNull(modello,"CODTIPOSMS");
     		
     		String msg1 = StringUtils.getAttributeStrNotNull(modello,"STR30MSG1");
     		String msg2 = StringUtils.getAttributeStrNotNull(modello,"STR30MSG2");
     		String msg3 = StringUtils.getAttributeStrNotNull(modello,"STR30MSG3");
     		String msg4 = StringUtils.getAttributeStrNotNull(modello,"STR30MSG4");
     		
     		String txtcontatto = msg1 + " il giorno " + data
			+ " alle ore " + ora + " presso CPI di "
			+ cpi + " " + msg2 + " " + msg3
			+ " " + msg4;
     		%>
     		
     		listaModelli["<%=codTipoSms%>"] = "<%=txtcontatto%>";
     		
		<%}%>
	}
	
	function cambiaModello(){
		var selIndex = document.frmAppuntamento.COD_TIPO_SMS_CIG.selectedIndex;
		
		var selValue = document.frmAppuntamento.COD_TIPO_SMS_CIG.options[selIndex].value; 
		
		if(selValue=="")
			document.frmAppuntamento.textSms.value = "";
		else document.frmAppuntamento.textSms.value = listaModelli[selValue];
	}
	
	 function inviaSmsCig() { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

	  if(checkObbligatorio("strCell")&&checkObbligatorio("COD_TIPO_SMS_CIG")&&checkObbligatorio("textSms")){
		document.frmAppuntamento.PAGE.value = "DettaglioAppuntamentoPage";
      	document.frmAppuntamento.inviaSms.value = "1";
      	doFormSubmit(document.frmAppuntamento);
      }
      
    }  
    
    function checkObbligatorio(inputName){
	  	var ctrlObj = eval("document.forms[0]." + inputName);
	  	if (ctrlObj.value=="") {
			alert("Il campo " + ctrlObj.title + " è obbligatorio");
			ctrlObj.focus();
			return false;
		}
		return true;
	} //isRequired
	
	
	//ANPAL
	    var map = new Object(); // or var map = {};
		var flgMinistero = '<%=flgInvioEsitoAnpal%>';
		var si = new String("S").valueOf();
		var no = new String("N").valueOf();
	    function mappaEsiti(){
	    	flgMinistero = new String(flgMinistero).valueOf().trim();
	  
	         if(flgMinistero==si){ 
	        	 
	      		  document.getElementById("invioAnpal").disabled = false;
	      		 document.getElementById("invioAnpal").className = "pulsanti";

	        }else  if(flgMinistero==no){ 
	        	 
	        	document.getElementById("invioAnpal").disabled = true;
	       	 document.getElementById("invioAnpal").className = "pulsantiDisabled";
	        } 
	    	
	    	
	     	<%for (Iterator i = rowsEsito.iterator(); i.hasNext(); ){
	    		SourceBean esito = (SourceBean) i.next();
	    		String invioMin = (String) esito.getAttribute("FLGINVIOMIN");
	    		String codice = (String) esito.getAttribute("CODICE");
	    	%>
	    		map['<%=codice%>'] = "<%=invioMin%>";
	    	<%}%>
	    	 
	    }
	    
	    function getMappaEsiti(k) {
	    	return map[k];
		}
	    
	    function fieldChanged() {
	        <%if (canModifyAppuntamento) {out.print("flagChanged = true;");}%>
	    }

		function caricaComboEsito(){
			comboPair.populate();
			document.getElementById("invioAnpal").disabled = true;
	      	 document.getElementById("invioAnpal").className = "pulsantiDisabled";
			fieldChanged();
		}
		
		function controllaEsito(){
			fieldChanged(); 
			var selEsito = document.frmAppuntamento.CODESITOAPPUNT;
			 
			var temp = getMappaEsiti(selEsito.value);
			 
			flgMinistero = new String(temp).valueOf().trim();
			 
			 if(flgMinistero==si){ 
	 
	     		  document.getElementById("invioAnpal").disabled = false;
	     		 document.getElementById("invioAnpal").className = "pulsanti";
	       }else          if(flgMinistero==no){ 
	        	document.getElementById("invioAnpal").disabled = true;
	       	document.getElementById("invioAnpal").className = "pulsantiDisabled";
	       } 
// 05-03-2020 codice commentato per consolidamento lr_ag_anpal_p2_da_3.25.0			       
// 			if(selEsito.value == "CRI" || selEsito.value == "LRI" ){
// 				alert("Attenzione è necessario indicare il nuovo appuntamento");
// 			}
		}
	
		 function conferma(azione){
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
		
		      if (azione=="ANPAL"){
			        if (flagChanged==true){
			         	alert("I dati sono stati modificati. Per poter inviare l'esito è necessario prima salvare le modifiche");
			        	 return;
			        } else {
			        	if (!confirm("ATTENZIONE: l'esito dell'appuntamento verrà inviato ad ANPAL. Si vuole procedere?")) { return; }
			        	else{
			        		 document.frmAppuntamento.ANPAL.value="INVIOANPAL";	
			        		 document.frmAppuntamento.PAGE.value="DettaglioAppuntamentoPage";	
			        		 doFormSubmit(document.frmAppuntamento);
			        	}
			        }
			      }
		 }
		 
		    function storicoInviiAnpal() {
		    	var appuntamentoPrg = document.frmAppuntamento.PRGAPPUNTAMENTO.value;
		    	var f = "AdapterHTTP?PAGE=STORICO_INVIO_APP_ANPAL" + 
		    		"&PRGAPPUNTAMENTO=" + appuntamentoPrg ;
		        var t = "_blank";
		        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=580,height=400,top=100,left=100"
		        window.open(f, t, feat);
		    }
  </script> 
  
</head>
<body class="gestione" onload="javascript:inizializza_modelli();mappaEsiti();">
<%
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav.show(out); 
}
else {
  infCorrentiAzienda.show(out); 
}
%>
<af:form name="frmAppuntamento" action="AdapterHTTP" method="POST">
<input type="hidden" name="ANPAL" value="">
<input type="hidden" name="IDAPPUNTAMENTOCOAP" value="<%=idAppuntamentoAnpal%>">
<input type="hidden" name="CODESITOMINAPP" value="<%=codEsitoMinAppuntamento%>">
<input type="hidden" name="CODCPIMIN" value="<%=codCpiMin%>">
<input type="hidden" name="CODCPIANPAL" value="<%=codCpiAnpal%>">

<input type="hidden" name="CODCPIOPERATORE" value="<%=codCpiOperatore%>">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadSalvaContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="CodCpiApp" value="<%=CodCpiApp%>">
<input type="hidden" name="PRGCONTATTO" value="">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strCodScadenza%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="statoValCV" value="<%=strStatoValCV%>">
<input type="hidden" name="PRGROSA" value="<%=strPrgRosa%>">
<input type="hidden" name="CPIROSE" value="<%=strCpiRose%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=strPrgRichiestaAz%>">
<input type="hidden" name="cognome" value="<%=strCognome%>">
<input type="hidden" name="nome" value="<%=strNome%>">
<input type="hidden" name="CF" value="<%=strCF%>">
<input type="hidden" name="didRilasciata" value="<%=strDidRilasciata%>">
<input type="hidden" name="codStatoOcc" value="<%=strCodStataOcc%>">
<input type="hidden" name="viewNonPresentati" value="<%=strViewNonPresentati%>">
<input type="hidden" name="NumVol" value="<%=strNumVol%>">
<input type="hidden" name="dataNP" value="<%=strDataNP%>">
<input type="hidden" name="categoria181" value="<%=strCategoria181%>">
<input type="hidden" name="legge407_90" value="<%=strLegge407_90%>">
<input type="hidden" name="lungaDur" value="<%=strLungaDur%>">
<input type="hidden" name="revRic" value="<%=strRevRic%>">
<input type="hidden" name="dataDalSlot" value="<%=dataDalSlot%>">
<input type="hidden" name="codServizio" value="<%=codServizio%>">
<input type="hidden" name="aggiornaFromLav" value="0">
<input type="hidden" name="inviaSms" value="0">
<input type="hidden" name="PRGAPPUNTAMENTO" value="<%=PRGAPPUNTAMENTO%>">
<input type="hidden" name="OLDSTATO" value="<%=OLD_STATO%>">
<input type="hidden" name="bParAllineaAppuntamento" value="1">
<input type="hidden" name="NUMKLOAGENDA" value="<%=numKloAgenda%>">
<%if(fromPattoAzioni){%>
  <input type="hidden" name="PATTO_AZIONI" value="true">
  <input type="hidden" name="statoSezioni" value="<%=statoSezioni%>">
  <input type="hidden" name="pageChiamante" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>">
  <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">  
<%}%>
<%for(int i=0;i< codLstTab.size();i++) {%>
<input type="hidden" name="COD_LST_TAB" value="<%=codLstTab.get(i)%>">
<%}%>


<input type="hidden" name="MESSAGE" value="<%=messRosa%>" disabled/>
<input type="hidden" name="LIST_PAGE" value="<%=listPageScad%>" disabled/>
<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>

<input type="hidden" name="DATINIZIOISCR" value="<%=datInizioIscr%>"/>
<input type="hidden" name="DATSTIMATA" value="<%=datStimata%>"/>
<input type="hidden" name="CODESITO" value="<%=codEsito%>"/>
<input type="hidden" name="CODAZIONE" value="<%=codAzione%>"/>



<p class="titolo">Dettaglio Appuntamento</p>
<p>
<%
SourceBean contErr = (SourceBean) serviceResponse.getAttribute("MINCONGRUENZE");
Vector rowsErr = contErr.getAttributeAsVector("ROWS.ROW");
SourceBean rowErr = null;
int j = 0;
String txtErr = "";
if(rowsErr.size()>0) {
%> 
  <img src="../../img/warning.gif">&nbsp;<b>Attenzione, si sono verificate le seguenti incongruenze con altri appuntamenti:</b>
  <ul>
<%
  for(j=0; j<rowsErr.size(); j++) { 
    rowErr = (SourceBean) rowsErr.elementAt(j);
    txtErr = (String) rowErr.getAttribute("STRTIPOERR");
%>
    <li><%=txtErr%></li>
<% }%>
  </ul>
<%}%>
<%if(!msgAllineamento.equals("")) {%>
  <ul><li><%=msgAllineamento%></li></ul>
<%}%>
<%if(rigaEsitoAnpalOk){ %>
	 <font color="green"><ul><li><%=msgEsitoAnpal%></li></ul></font>
<%}else if(rigaEsitoAnpalKo){%>
	<font color="red"><ul><li><%=msgEsitoAnpal%></li></ul></font>
<%}%>

 <af:showMessages prefix="MSALVAAPPUNTAMENTOFROMLAV"/>
 <af:showMessages prefix="M_inviaSmsCig"/>
  
 <font color="red">
  <af:showErrors/>
</font>
 
 <af:showErrors />

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <af:textBox name="DATAAPP"
              size="11"
              maxlength="10"
              type="date"
              readonly="true"
              classNameBase="input"
              value="<%=datApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="ORARIOAPP"
              size="5"
              maxlength="5"
              type="time"
              title="Orario"
              readonly="true"
              classNameBase="input"
              value="<%=strOraApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Durata</td>
  <td class="campo">
  <af:textBox name="DurataApp"
              size="5"
              maxlength="5"
              type="text"
              title="Durata"
              readonly="true"
              classNameBase="input"
              value="<%=strDurata%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta"><%=labelServizio %></td>
    <td class="campo">
      <af:comboBox name="SERVIZIOAPP" size="1" title="<%=labelServizio %>"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SERVIZIO"
                     selectedValue="<%=codServizioApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPP" size="1" title="Operatore"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI"
                     selectedValue="<%=strSpiApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore effettivo</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPPEFF" size="1" title="Operatore effettivo"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI"
                     selectedValue="<%=strSpiAppEff%>" addBlank="true" blankValue=""/>
    </td>
</tr>

<tr>
  <td class="etichetta">Ambiente</td>
    <td class="campo">
      <af:comboBox name="AMBIENTEAPP" size="1" title="Ambiente"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="<%=strAmbiente%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea name="TXTCONTATTOAPP" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 readonly="true"
                 classNameBase="input"
                 value="<%=txtNoteApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto Appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODEFFETTOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false" disabled="<%=String.valueOf(!canModifyAppuntamento)%>"
                     focusOn="false" moduleName="COMBO_EFFETTO_APPUNTAMENTO"
                     selectedValue="<%=CODEFFETTOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"/>    
    </td>
</tr>
<tr>
  <td class="etichetta">Stato appuntamento</td>
   				 <%
                 
                    ComboPair docComboPair = new ComboPair(rowsEsito, CODESITOAPPUNT, "CODSTATOAPPUNTAMENTO");
 				 	if(StringUtils.isEmptyNoBlank(CODESITOAPPUNT)){
 				 		CODESITOAPPUNT = docComboPair.getCodRef();
 				 	}
                %>
  
  <td class="campo">
      <af:comboBox name="CODSTATOAPPUNTAMENTO" size="1" title="Stato appuntamento"
                     multiple="false" required="true" disabled="<%=String.valueOf(!canModifyAppuntamento)%>"
                     focusOn="false" moduleName="COMBO_STATO_APPUNTAMENTO"
                     selectedValue="<%=CODSTATOAPPUNTAMENTO%>" addBlank="true" blankValue=""
                     classNameBase="input"
                      onChange="caricaComboEsito()"/>        
  </td>
</tr>
<tr>
  <td class="etichetta">Esito appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODESITOAPPUNT" size="1" title="Esito appuntamento"
                     multiple="false" required="false" disabled="<%=String.valueOf(!canModifyAppuntamento)%>"
                     focusOn="false" moduleName="COMBO_ESITO_APPUNTAMENTO"
                     selectedValue="<%=CODESITOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input" onChange="controllaEsito()"/>  
           <%if(canInvioAnpal){%>
			 <input type="button" class="pulsanti" name="ANPAL_BTN" value="Invio esito ad ANPAL" id="invioAnpal"  onCLick="javascript:conferma('ANPAL');">  
		 <%}else{%> 
		 	  <input type="hidden" id="invioAnpal" />    
		 <%}%> 		           
                           
  </td>
     <script>
                var arrayFiglio = new Array();                
                <%=docComboPair.makeArrayJSChild()%>
                var comboPair = new ComboPair(document.frmAppuntamento.CODSTATOAPPUNTAMENTO, document.frmAppuntamento.CODESITOAPPUNT, arrayFiglio,true);
                comboPair.populate('<%=docComboPair.getCodRef()%>', '<%=CODESITOAPPUNT%>');
               
  </script>
</tr>

<%if(isAppAnpal){%>
<tr>
  <td class="etichetta">Codice app.to ANPAL</td>
  <td class="campo">
  <af:textBox name="strCodAnpal" value="<%=idAppuntamentoAnpal%>"   classNameBase="input"  readonly="true"/>
  </td>
 </tr>
<%}%>
<%if(viewUltimoInvioAnpal){%>
	<tr>
	  <td class="etichetta">Data e ora invio</td>
	  <td class="campo">
	  <af:textBox name="dataUltimoInvioAnpal" value="<%=dataUltimoInvioAnpal%>"   classNameBase="input"  readonly="true"/>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Esito invio</td>
	  <td class="campo">
	  <af:textBox name="esitoNotificaUltimoInvioAnpal" size="60" value="<%=esitoNotificaUltimoInvioAnpal%>"   classNameBase="input"  readonly="true" />
	  	<%if(canInvioAnpal){%>
			<input type="button" class="pulsanti" name="STORICO_ANPAL_BTN" value="Storico Invii ANPAL" onCLick="javascript:storicoInviiAnpal();"/>
		<%}%>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Esito appuntamento inviato</td>
	  <td class="campo">
	  <af:textBox name="esitoAppuntamentoUltimoInvioAnpal" size="60" value="<%=esitoAppuntamentoUltimoInvioAnpal%>"   classNameBase="input"  readonly="true"/>
	  </td>
	</tr>
<%}%>
<tr><td>&nbsp;</td></tr>
<%if(canModifyAppuntamento) {%>
</table>
<table align="center">
<tr>
  <td align="center">
  <input type="button" class="pulsanti" name="update" value="Aggiorna" onClick="javascript:aggiorna();">
  </td>
</tr>
</table>
<%}%>
<%if(invioSMSCig && !operatorePatronato){%>
<div class='sezione2' id='invioSmsAmbitoCig'>
	<img id='tendinaSMS' alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("invioSmsSez"));'/>&nbsp;&nbsp;&nbsp;Invio SMS in ambito CIG/MB in Deroga (abilitato solo in caso di iscrizione CIG/MB in Deroga attiva)&nbsp;&nbsp;
</div>
<div id="invioSmsSez" style="display:1">
 	<table class="main">
		<tr>
     		<td class="etichetta">Lavoratore 
         	</td>
         	<td class="campo">
          		<af:textBox classNameBase="input" type="text" name="comune" readonly="true" value="<%=strLavoratore%>" size="70" maxlength="100"/>
       		</td>
       		<td>
       		</td>
       		<td>
       		</td>
      	</tr>
      	<tr>
      		<td class="etichetta">Cellulare
      		</td>
      		<td class="campo">
      			<af:textBox title="Cellulare" classNameBase="input" type="text" 
      					name="strCell" readonly="false" required="true" 
      					value="<%=strCellCig%>" size="23" maxlength="20" disabled="<%=invioSmsCig%>"/>
      		</td>
      	</tr>
      	<tr>
      		<td class="etichetta">Modello di SMS
         	</td>
      		<td class="campo">
      			<af:comboBox name="COD_TIPO_SMS_CIG" size="1" title="Modello di SMS"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_TIPO_SMS"
                     selectedValue="" addBlank="true" blankValue=""
                     classNameBase="input" onChange="javascript:cambiaModello();" 
                     disabled="<%=invioSmsCig%>"/> 
      		</td>
      	</tr>
      	<tr>
      		<td class="etichetta">Testo
       		</td>
       		<td class="campo">
       			<af:textArea title="Testo" required="true" name="textSms" value="" validateOnPost="true" classNameBase="textarea" cols="50" maxlength="160" disabled="<%=invioSmsCig%>"/>
	       	</td>
      	</tr>
      	<tr>
      		<td>    			
      		</td>
      		<td class="campo">
      			<input type="button" class="pulsanti" name="inputInviaSms" value="Invia SMS" onClick="javascript:inviaSmsCig();" <%if(invioSmsCig.equals("true")){%>disabled<%}%>>
      		</td>
      	</tr>
	</table>
</div>
<%} %>

<%out.print(htmlStreamBottom);%>
<br>
<p class="titolo">Lavoratori</p>
<af:list moduleName="LISTA_LAVORATORI_APPUNTAMENTO_SCAD"/>
<%
String strCodiceFiscale = "";
String strRagSoc = "";
String strTipoAz = "";
String strIndirizzo = "";
String strComune = "";
String strPIva = "";
SourceBean riga = null;
SourceBean contAziendeApp = (SourceBean) serviceResponse.getAttribute("LISTA_AZIENDE_APPUNTAMENTO_SCAD");
Vector rows_VectorAziende = null;
rows_VectorAziende = contAziendeApp.getAttributeAsVector("ROWS.ROW");
if (rows_VectorAziende.size() > 0) {
%>
  <p class="titolo">Aziende</p>
  <TABLE class=lista align=center>
  <TR>
  <TH class=lista nowrap>Tipo&nbsp;</TH>
  <TH class=lista nowrap>Partita Iva&nbsp;</TH>
  <TH class=lista nowrap>Ragione Sociale&nbsp;</TH>
  <TH class=lista nowrap>Codice Fiscale&nbsp;</TH>
  <TH class=lista nowrap>Indirizzo&nbsp;</TH>
  <TH class=lista nowrap>Comune&nbsp;</TH>
  </TR>
<%
  for (int jAziende = 0; jAziende < rows_VectorAziende.size(); jAziende++) {
    riga=(SourceBean) rows_VectorAziende.elementAt(jAziende);
    strCodiceFiscale = riga.getAttribute("STRCODICEFISCALE").toString();
    strRagSoc = riga.getAttribute("STRRAGIONESOCIALE").toString();
    strTipoAz = riga.getAttribute("STRDESCRIZIONE").toString();
    strIndirizzo = riga.getAttribute("STRINDIRIZZO").toString();
    strComune = riga.getAttribute("DESCRIZIONECOMUNE").toString();
    strPIva = riga.getAttribute("STRPARTITAIVA").toString();
  %>
    <TR>
    <td class=lista><%=strTipoAz%>&nbsp;</td>
    <td class=lista><%=strPIva%>&nbsp;</td>
    <td class=lista><%=strRagSoc%>&nbsp;</td>
    <td class=lista><%=strCodiceFiscale%>&nbsp;</td>
    <td class=lista><%=strIndirizzo%>&nbsp;</td>
    <td class=lista><%=strComune%>&nbsp;</td>
    </TR>
   <% 
  }
  %>
  </table>
  <br>
  <%
}
%>
<table align="center">
<tr>
  <td align="center">
  <input type="button" class="pulsanti" name="annulla" value="Chiudi" onClick="javascript:chiudi();">
  </td>
</tr>
</table>
</af:form>
<div align ="center">
<%operatoreInfo.showHTML(out);%>
</div>
</body>
</html>