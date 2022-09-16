<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.User,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  java.util.GregorianCalendar, 
  java.text.SimpleDateFormat,
  it.eng.sil.security.*,
  it.eng.sil.module.movimenti.constant.Properties

"%>
<%@ include file="../global/noCaching.inc"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	SimpleDateFormat df = new SimpleDateFormat(DateUtils.FORMATO_DATA);

  boolean reiteraRichAz = false;
  boolean flgPubblicabile = true;
  String codRapportoLav = "";
  
  SourceBean richiestaRow=null;
  SourceBean evasioneRow = null;
  Vector mansioneVec = null;
  Vector comuneVec = null;
  Vector provinciaVec = null;


  int numGPubb = 0;

  //Sezione data pubblicazione
  GregorianCalendar dataPubb = new GregorianCalendar();

  SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGgPubb.ROWS.ROW");
  if (numGg != null) {
    numGPubb = Integer.parseInt(numGg.getAttribute("NUM").toString());
  }

	boolean isFlagCandidatura = false;

	String numConfigCandidaturaPaleseVac = serviceResponse
			.containsAttribute("M_CONFIG_CANDID_PALE_VAC.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_CONFIG_CANDID_PALE_VAC.ROWS.ROW.NUM")
			.toString() : Properties.DEFAULT_CONFIG;


	//System.gc();
	//--------------------------
	String prgRichiestaAz = "";
	String prgAzienda = "";
	String prgUnita = "";

	boolean flgPubblica = false;
	String flgPubblicata = "";

	String codEvasione = "";
	String flgPubbWeb = "";
	String flgPubbGiornali = "";
	String flgPubbBacheca = "";
	String flgPubbCresco = "";
  String datUltimoInvioVacancy = "";
  boolean flgCrescoInviata = false;
	String flgNullaOsta = "";
	String flgCandidaturaVacPal = "";

	String strCognomeRiferimento = "";
	String strNomeRiferimento = "";
	String strTelRiferimento = "";
	String strFaxRiferimento = "";
	String strEmailRiferimento = "";

	String datPubblicazione = "";
	String datScadenzaPubblicazione = "";

	String strCognomeRifPubb = "";
	String strNomeRifPubb = "";
	String strTelRifPubb = "";
	String strFaxRifPubb = "";
	String strEmailRifPubb = "";

	String codCPI = "";

	String strDatiAziendaPubb = "";
	String strMansionePubb = "";
	String txtFiguraProfessionale = "";
	String strLuogoLavoro = "";
	String strFormazionePubb = "";
	String txtCondContrattuale = "";
	String strConoscenzePubb = "";
	String txtCaratteristFigProf = "";
	String strNoteOrarioPubb = "";
	String strRifCandidaturaPubb = "";
	String codQualifica = "";

	String codMonoCMcategoria = null;
	String codMonoCMcatPubb = "";

	String cdnUtIns = "";
	String dtmIns = "";
	String cdnUtMod = "";
	String dtmMod = "";

	String flgIncrocio = "";
	String cdnStatoRich = "";

	boolean canInsert = false;
	boolean canModify = false;
	boolean canDelete = false;
	boolean canAnteprimaArt16 = false;
	boolean canInviaFlusso = false;
	boolean canAnteprimaFlusso = false;
	boolean eUnaCopia = false;
	boolean canModifyPubb = false;
	boolean flgPrevalorizza = false;
	boolean flgStatoEvasioneCatCM = false;
	boolean RER = false;
	boolean FLUSSO_VACANCY = false;
	boolean CRESCO = false;
	boolean canModifyCresco = false;
	boolean IS_DTPUBB = false;
	boolean readOnlyModifyDtPubb = false;


	BigDecimal numkloRichiestaAz = null;
	BigDecimal numStorico = new BigDecimal("0");

	Testata operatoreInfo = null;
	// InfCorrentiAzienda infCorrentiAzienda= null;

	//estraggo che tipo di salvataggio è stato effettuato.
	//più avanti controllerò se è stato fatto un aggiornamento di tipo "Prevalorizza".
	//se è il caso, prevalorizzerò la data di scadenza, altrimenti la lascio com'è.
	String tipoOp = StringUtils.getAttributeStrNotNull(serviceRequest,
			"salva");

	String numConfigFlusso = serviceResponse
			.containsAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM")
			.toString() : Properties.DEFAULT_CONFIG;

	if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigFlusso)) {
		FLUSSO_VACANCY = true;
	}
	
	String numConfigDtPubb = serviceResponse
			.containsAttribute("M_CONFIG_DTPUBB_VACANCY.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_CONFIG_DTPUBB_VACANCY.ROWS.ROW.NUM")
			.toString() : Properties.DEFAULT_CONFIG;

	if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigDtPubb)) {
		IS_DTPUBB = true;
	}

	SourceBean rowRegione = (SourceBean) serviceResponse
			.getAttribute("M_GetCodRegione.ROWS.ROW");
	String regione = StringUtils.getAttributeStrNotNull(rowRegione,
			"codregione");

	if ("8".equalsIgnoreCase(regione)) {
		RER = true;
	}

	String numConfigCresco = serviceResponse
			.containsAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM").toString()
			: Properties.DEFAULT_CONFIG;

	if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCresco)) {
		CRESCO = true;
	}

	if (prgRichiestaAz == null || prgRichiestaAz.equals("")) {
		prgRichiestaAz = (String) serviceRequest
				.getAttribute("prgRichiestaAZ");
	}

	richiestaRow = (SourceBean) serviceResponse
			.getAttribute("M_GetTestataRichiestaPubb.ROWS.ROW");
	if (richiestaRow != null) {

		if (prgRichiestaAz == null || prgRichiestaAz.equals("")) {
			prgRichiestaAz = richiestaRow
					.getAttribute("prgRichiestaAZ").toString();
		}
		codRapportoLav = StringUtils.getAttributeStrNotNull(
				richiestaRow, "CODRAPPORTOLAV");
		
		String flgPubblicaDB = (String) richiestaRow.getAttribute("FLGPUBBLICA");
		if (flgPubblicaDB != null) {
			flgPubblica = StringUtils.getAttributeStrNotNull(richiestaRow,
					"FLGPUBBLICA").equalsIgnoreCase("S");
			canModifyCresco = flgPubblica;
		} else if (CRESCO) {
			canModifyCresco = true;
		}
		
		numStorico = (BigDecimal) richiestaRow
				.getAttribute("NUMSTORICO");
		eUnaCopia = (numStorico.intValue() != 0) ? true : false;
		flgPubblicata = StringUtils.getAttributeStrNotNull(
				richiestaRow, "flgPubblicata");
		flgNullaOsta = StringUtils.getAttributeStrNotNull(richiestaRow,
				"flgNullaOsta");
		datPubblicazione = StringUtils.getAttributeStrNotNull(
				richiestaRow, "datPubblicazione");
		if (datPubblicazione != null && datPubblicazione.equals("")) {
			datPubblicazione = DateUtils.getNow();
		}
		datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(
				richiestaRow, "datScadenzaPubblicazione");
		//if (datScadenzaPubblicazione != null && datScadenzaPubblicazione.equals("") && tipoOp.equals("Prevalorizza")) { 
		//controllo se è stato fatto un aggiornamento di tipo "Prevalorizza".
		//se è il caso, prevalorizzo la data di scadenza, altrimenti la lascio com'è.
		//dataPubb=new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6,10)), Integer.parseInt(datPubblicazione.substring(3,5))-1, Integer.parseInt(datPubblicazione.substring(0,2))  );
		//dataPubb.add(Calendar.DATE,numGPubb);
		//Date prova=dataPubb.getTime();
		//datScadenzaPubblicazione = df.format(dataPubb.getTime());
		//}

		prgAzienda = richiestaRow.getAttribute("PRGAZIENDA").toString();
		prgUnita = richiestaRow.getAttribute("PRGUNITA").toString();

		strCognomeRifPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strCognomeRifPubb");
		strNomeRifPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strNomeRifPubb");
		strTelRifPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strTelRifPubb");
		strFaxRifPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strFaxRifPubb");
		strEmailRifPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strEmailRifPubb");

		codCPI = StringUtils.getAttributeStrNotNull(richiestaRow,
				"CODCPI");

		strDatiAziendaPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strDatiAziendaPubb");
		strMansionePubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strMansionePubb");
		txtFiguraProfessionale = StringUtils.getAttributeStrNotNull(
				richiestaRow, "txtFiguraProfessionale");
		strLuogoLavoro = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strLuogoLavoro");
		strFormazionePubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strFormazionePubb");
		txtCondContrattuale = StringUtils.getAttributeStrNotNull(
				richiestaRow, "txtCondContrattuale");
		strConoscenzePubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strConoscenzePubb");
		txtCaratteristFigProf = StringUtils.getAttributeStrNotNull(
				richiestaRow, "txtCaratteristFigProf");
		strNoteOrarioPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strNoteOrarioPubb");
		strRifCandidaturaPubb = StringUtils.getAttributeStrNotNull(
				richiestaRow, "strRifCandidaturaPubb");

		codQualifica = StringUtils.getAttributeStrNotNull(richiestaRow,
				"codQualifica");

		cdnUtIns = richiestaRow.getAttribute("cdnUtIns").toString();
		dtmIns = StringUtils.getAttributeStrNotNull(richiestaRow,
				"dtmIns");
		cdnUtMod = richiestaRow.getAttribute("cdnUtMod").toString();
		dtmMod = StringUtils.getAttributeStrNotNull(richiestaRow,
				"dtmMod");
		flgIncrocio = StringUtils.getAttributeStrNotNull(richiestaRow,
				"FLGINCROCIO");
		numkloRichiestaAz = richiestaRow
				.containsAttribute("numkloRichiestaAz") ? (BigDecimal) richiestaRow
				.getAttribute("numkloRichiestaAz") : null;

		if (richiestaRow.containsAttribute("cdnStatoRich"))
			cdnStatoRich = richiestaRow.getAttribute("cdnStatoRich")
					.toString();

		if (numkloRichiestaAz != null)
			numkloRichiestaAz = numkloRichiestaAz
					.add(new BigDecimal(1));
	}

	codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetTestataRichiestaPubb.ROWS.ROW.codMonoCMcatPubb");

	evasioneRow = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
	if (evasioneRow != null) {
		codEvasione = StringUtils.getAttributeStrNotNull(evasioneRow,"codEvasione");
		flgPubbWeb = StringUtils.getAttributeStrNotNull(evasioneRow,"flgPubbWeb");
		flgPubbGiornali = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbGiornali");
		flgPubbBacheca = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbBacheca");
		flgPubbCresco = StringUtils.getAttributeStrNotNull(evasioneRow,	"flgPubbCresco");
	
		flgCandidaturaVacPal = StringUtils.getAttributeStrNotNull(evasioneRow,"FLG_CANDIDATURA");
		
		datUltimoInvioVacancy = StringUtils.getAttributeStrNotNull(
				evasioneRow, "datUltimoInvioVacancy");
		
		//Marianna: segnalazione redmine #7194
		if(StringUtils.isFilledNoBlank(datUltimoInvioVacancy) && IS_DTPUBB){
			readOnlyModifyDtPubb = true;
		}
		
		flgCrescoInviata = "S".equals(flgPubbCresco) && !"".equals(datUltimoInvioVacancy);

		flgStatoEvasioneCatCM = codEvasione.equalsIgnoreCase("MIR")
				|| codEvasione.equalsIgnoreCase("MPP")
				|| codEvasione.equalsIgnoreCase("MPA");
	}
	
	if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCandidaturaPaleseVac) && (codEvasione.equalsIgnoreCase("DPR") || codEvasione.equalsIgnoreCase("DFD"))) {
		isFlagCandidatura = true;
	}

	SourceBean rowCount = null;
	int conta = 0;
	if (RER) {
		rowCount = (SourceBean) serviceResponse
				.getAttribute("M_CountIdoMansioni.ROWS.ROW");
		if (rowCount != null) {
			conta = Integer.valueOf(
					"" + rowCount.getAttribute("numMansioni"))
					.intValue();
			if (conta <= 0) {
				flgPubblicabile = false;
			}
		}
		if (flgPubblicabile) {
			rowCount = (SourceBean) serviceResponse
					.getAttribute("M_CountIdoComuni.ROWS.ROW");
			if (rowCount != null) {
				conta = Integer.valueOf(
						"" + rowCount.getAttribute("numComuni"))
						.intValue();
				if (conta <= 0) {
					flgPubblicabile = false;
				}
			}
		}
	} else {
		mansioneVec = (Vector) serviceResponse
				.getAttributeAsVector("M_LISTIDOMANSIONIPUBB.ROWS.ROW");
		if (mansioneVec != null && mansioneVec.isEmpty()) {
			flgPubblicabile = false;
		} else {
			comuneVec = (Vector) serviceResponse
					.getAttributeAsVector("MLISTATERRITORICOMUNIRICHIESTA.ROWS.ROW");
			if (comuneVec != null && !comuneVec.isEmpty()) {
			} else {
				provinciaVec = (Vector) serviceResponse
						.getAttributeAsVector("MLISTATERRITORIPROVINCERICHIESTA.ROWS.ROW");
				if (provinciaVec != null && provinciaVec.isEmpty()) {
					flgPubblicabile = false;
				}
			}
		}
	}

	//infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	/*ProfileDataFilter filter = new ProfileDataFilter(user, "IdoTestataAziendaPage");
	boolean canView = false;
	
	if ( !flag_insert ) {
	filter.setPrgAzienda(new BigDecimal(prgAzienda));
	filter.setPrgUnita(new BigDecimal(prgUnita));
	canView=filter.canViewUnitaAzienda();
	} else {
	canView = true; // sono in inserimento, la visibilità dei dati non ha senso
	}
	
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{*/
		
	canModifyCresco	= !eUnaCopia && (cdnStatoRich.compareTo("5") != 0) && canModifyCresco;
	canModifyPubb = !eUnaCopia && flgPubblica
			&& (cdnStatoRich.compareTo("5") != 0);
	PageAttribs attributi = new PageAttribs(user, "PubbRichiestePage");

	canInsert = canModifyPubb;
	canModify = canModifyPubb;
	canDelete = false;
	canAnteprimaArt16 = attributi.containsButton("AVSTART16");
	boolean isEvasione = false;
	if (FLUSSO_VACANCY) {
		isEvasione = (codEvasione.equalsIgnoreCase("DFA")
				|| codEvasione.equalsIgnoreCase("DFD")
				|| codEvasione.equalsIgnoreCase("DPR") || codEvasione
				.equalsIgnoreCase("DRA")) ? true : false;
		canInviaFlusso = attributi.containsButton("INVFLVAC");
		canAnteprimaFlusso = attributi.containsButton("ANTFLVAC");
	}

	/*if ((cdnStatoRich.compareTo("4")!=0) && (cdnStatoRich.compareTo("5")!=0)){
	  canModify= attributi.containsButton("INSERISCI");
	  canDelete= attributi.containsButton("CANCELLA");
	  canAggiorna= attributi.containsButton("AGGIORNA");
	}
	
	if ( !canModify && !canDelete && !canAggiorna) {
	    
	  } else {
	    boolean canEdit = false;
	    if ( !flag_insert ) {
	      canEdit = filter.canEditUnitaAzienda();
	    } else { 
	      canEdit = true;   // sono in inserimento, la visibilità dei dati non ha senso
	    }
	    if ( !canEdit ) {
	      canModify = false;
	      canDelete = false;
	      canAggiorna = false;        
	    }
	  }*/

	int _funzione = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

	String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
	String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

	// controllo in sola lettura se la modalità evasione = AS e flgPubblicata = S

	if (codEvasione.equals("AS") && flgPubblicata.equalsIgnoreCase("S")) {
		canModifyPubb = false;
		canModifyCresco = false;
		htmlStreamTop = StyleUtils.roundTopTable(false);
		htmlStreamBottom = StyleUtils.roundBottomTable(false);
	}
	
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {
		// END-PARTE-TEMP

		// INIT-PARTE-TEMP
	} else {
		// END-PARTE-TEMP	

		if (codEvasione.equals("CMA")
				&& flgPubblicata.equalsIgnoreCase("S")) {
			canModifyPubb = false;
			canModifyCresco = false;
			htmlStreamTop = StyleUtils.roundTopTable(false);
			htmlStreamBottom = StyleUtils.roundBottomTable(false);
		}

		// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
  <%@ include file="_infCorrentiAzienda.inc" %>  
  
<html>

<head>
  <title>Pubblicazione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>

  
<SCRIPT TYPE="text/javascript">

var isFlussoVacancy = <%= FLUSSO_VACANCY %>;

function checkDate(objData1, objData2) {

  strData1=objData1.value;
  strData2=objData2.value;

  //costruisco la data della richiesta
  d1giorno=parseInt(strData1.substr(0,2),10);
  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4),10);
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2),10);
  d2mese=parseInt(strData2.substr(3,2),10)-1;
  d2anno=parseInt(strData2.substr(6,4),10);
  data2=new Date(d2anno, d2mese, d2giorno);
  
  ok=true;
  if (data2 < data1) {
      alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;
   }
  return ok;
}



function controllaDatePubblicazione(inputName) {
  return checkDate(document.forms[0].datPubblicazione, document.forms[0].datScadenzaPubblicazione);
}

// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
}


function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione'))
    setWindowLocation(_url);
  }
  else
    setWindowLocation(_url);
}



function avvisaFlgPubblicata(){
  var msg = "Lo stato di pubblicazione sta per essere attivato.\r\n";

  if (isInSubmit()) return;


<%
  	if (codEvasione.equals("AS")){  	       	
%>
		if (document.Frm1.flgPubbWeb.value == "S") {
			checkAlert = false;
		}
		else if (document.Frm1.flgPubbGiornali.value == "S") {
			checkAlert = false;
		}		
		else if (document.Frm1.flgPubbBacheca.value == "S") {
			checkAlert = false;	
		}	
		else {
			checkAlert = true;
		}
		
		if (checkAlert)	{
			var s = "Per poter mettere in pubblicazione una richiesta\r\n";
		    s+= "è necessario aver inserito almeno una modalità di pubblicazione\r\n";
			alert(s);
			return;
		}		
<%	
	} 	
	else if (!flgPubblicabile){
		if(!RER) {
%>
			var s = "Per poter mettere in pubblicazione una richiesta\r\n";
	    		s+= "è necessario aver inserito almeno una mansione\r\n";
	    		s+= "e almeno un territorio (comune o provincia).\r\n";    
	    	alert(s);
	    	return;
	    	
<%		} else { %>
			var s = "Per poter mettere in pubblicazione una richiesta\r\n";
		    	s+= "è necessario aver inserito almeno una mansione e almeno un comune.\r\n";
		    	s+= "Inoltre è necessario aver indicato quale mansione e quale\r\n"; 
		    	s+= "comune eventualmente inviare al portale regionale.\r\n"; 
		    	alert(s);
		    	return;
<%		} 
	}
    else {
		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {	
		// END-PARTE-TEMP
		
		// INIT-PARTE-TEMP
		} else {
		// END-PARTE-TEMP
			if ("CMA".equalsIgnoreCase(codEvasione)) {
%>
				if (document.Frm1.flgPubbWeb.value == "S") {
					checkAlert = false;
				}
				else if (document.Frm1.flgPubbGiornali.value == "S") {
					checkAlert = false;
				}		
				else if (document.Frm1.flgPubbBacheca.value == "S") {
					checkAlert = false;	
				}	
				else {
					checkAlert = true;
				}
				
				if (checkAlert)	{
					var s = "Per poter mettere in pubblicazione una richiesta\r\n";
				    s+= "è necessario aver inserito almeno una modalità di pubblicazione\r\n";
					alert(s);
					return;
				}
<%				
			}  
		// INIT-PARTE-TEMP
		} 
		// END-PARTE-TEMP    
    
	}	  
%>

  if (!isFlussoVacancy && document.Frm1.flgPubbWeb.value == "S") {
    msg+= "La richiesta di pubblicazione viene messa immediatamente su Web!\r\n\r\n";
  }
  msg+= "Continuare?";

  if (document.Frm1.flgPubblicata.value!="S") {

    var ok = controllaFunzTL();
    ok = ok && controllaDatePubblicazione() && tagliaCampiLunghi();
  	if (ok) {
	 if (confirm(msg)) {
	   document.Frm1.salva.value="Aggiorna";  
	   document.Frm1.flgPubblicata.value="S";	  
	   doFormSubmit(document.Frm1);
	 }	
  	}
    undoSubmit();
  }
}

function  Riferimento() {
      this.Nome=null;
      this.Cognome=null;
      this.Telefono=null;
      this.Fax=null;
      this.E_mail=null;
      this.copia = copia;
}

function copia(rifer)
{

rifer.Nome = this.Nome;
rifer.Cognome = this.Cognome;
rifer.Telefono = this.Telefono;
rifer.Fax = this.Fax;
rifer.E_mail = this.E_mail;
return (rifer);
}

var lista_riferimenti=new Array();

function CaricaRiferimenti(ArrayRif){

  var len = ArrayRif.length;

  if (len > 0) {
    opzione = new Option("", 0 , false, false);
    document.Frm1.Riferimenti.options[0]=opzione;

    for (k=1; k<ArrayRif.length+1;k++) {
      rifer = new Riferimento ();
      lista_riferimenti[k]=ArrayRif [k-1].copia(rifer);
      opzione = new Option(lista_riferimenti[k].Cognome+" "+lista_riferimenti[k].Nome , k , false, false);
      document.Frm1.Riferimenti.options[k]=opzione;
    }
    if ( (document.Frm1.strCognomeRifPubb.value== "") && (document.Frm1.strNomeRifPubb.value=="") &&
       (document.Frm1.strTelRifPubb.value=="") && (document.Frm1.strFaxRifPubb.value=="") && 
       (document.Frm1.strEmailRifPubb.value=="") ) {
      mostra("comboRiferimenti");
    }
    
  }
}


function SettaParametriRiferimenti(sel){

    k=parseInt(sel.value,10);

    if (k!=0) {
      document.Frm1.strCognomeRifPubb.value = lista_riferimenti[k].Cognome;
      document.Frm1.strNomeRifPubb.value    = lista_riferimenti[k].Nome;
      document.Frm1.strTelRifPubb.value     = lista_riferimenti[k].Telefono;
      document.Frm1.strFaxRifPubb.value     = lista_riferimenti[k].Fax;
      document.Frm1.strEmailRifPubb.value   = lista_riferimenti[k].E_mail;
    
    } else {
      document.Frm1.strCognomeRifPubb.value = "";
      document.Frm1.strNomeRifPubb.value    = "";
      document.Frm1.strTelRifPubb.value     = "";
      document.Frm1.strFaxRifPubb.value     = "";
      document.Frm1.strEmailRifPubb.value   = "";
    
    }
    flagChanged = true;
}//SettaParametriRiferimenti(_)

function mostra(id)
{var div = document.getElementById(id);
 div.style.display="";
}

function nascondi(id)
{var div = document.getElementById(id);
 div.style.display="none";
}


function taglia(len, inputName){
  var campo = eval("document.forms[0]." + inputName);
  
  var stringa = new String(campo.value);
  if (stringa.length > len)
  {
   campo.value = stringa.substring(0,len);
  }
 }

function tagliaCampiLunghi() {
  taglia(2000,'strDatiAziendaPubb');
  taglia(2000,'strMansionePubb');
  taglia(2000,'txtFiguraProfessionale');
  taglia(2000,'strLuogoLavoro');
  taglia(2000,'strFormazionePubb');
  taglia(2000,'txtCondContrattuale');
  taglia(2000,'strConoscenzePubb');
  taglia(2000,'txtCaratteristFigProf');
  taglia(2000,'strNoteOrarioPubb');
  taglia(2000,'strRifCandidaturaPubb');
  return true;
}

function salvaCampi(prevalFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  if (prevalFlag == 'TRUE' ) {
    document.Frm1.salva.value="Prevalorizza";
  } else {
    document.Frm1.salva.value="Aggiorna";  
  }

  var ok = controllaFunzTL();
  ok = ok && controllaDatePubblicazione() && tagliaCampiLunghi();
  if (ok) {
    doFormSubmit(document.Frm1);
  } else {
    undoSubmit();
  }
}
window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);



function stampaAdesioniArt16 () {

	var isCodEvasioneAS = <%=codEvasione.equals("AS")%>;
	
	if (!isCodEvasioneAS) {
		
		alert("Anteprima Stampa Avviso Pubblico disponibile solamente per richieste con modalità di evasione 'Asta (art.16 L.56/87)'")
		undoSubmit();
		return false;
		
	} else {
	
		var tipoFile = "ALSEAP"; // Art. 16
		var tipoAvvPubb = "AS";
		var modPubblicazione = "1"; // 1 Web 2 Bacheca
		var prgRichiestaAz = <%=prgRichiestaAz%>;
	
		apriGestioneDoc(
				'RPT_AVVISO_PUBBLICO_ART16',
				'&prgRichiestaAz='+prgRichiestaAz +
				'&tipoAvvPubb='+tipoAvvPubb +
				'&modPubblicazione='+modPubblicazione //+
				, tipoFile);

		undoSubmit();
	
	}
}
var statoEvasioneVac = <%=isEvasione%>;
function apriAnteprimaInvioFlusso() {
	if(!statoEvasioneVac){
		alert("ATTENZIONE: l'invio è consentito solo per le modalità di evasione pari a Pubblicazione Anonima,Pubblicazione Anonima / Preselezione,Pubblicazione Palese,Pubblicazione Palese / Preselezione");
	}
	var prgRichiestaAz = document.Frm1.prgRichiestaAZ.value;
	var opened;
    var f = "AdapterHTTP?PAGE=IdoInvioVacancyAnteprimaPage&prgRichiestaAz="+prgRichiestaAz; 
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=650,top=30,left=180";
    opened = window.open(f, t, feat);
}

function inviaFlussoSilPortal() {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
 
	    document.Frm1.invioFlussoVacancy.value="invioFlussoVacancy";	 
	    doFormSubmit(document.Frm1);
	  
	}

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
</script>

<%@ include file="_apriGestionePubblSpecifica.inc" %>

<%--
	il seguente pezzo di codice è stato preso 
	dal file ../documenti/_apriGestioneDoc.inc
	che non è stato possibile includere in quanto
	ha delle parti comuni con _apriGestionePubblSpecifica.inc
	sopra incluso  
--%>
<script language="JavaScript">
 <%
	if ((queryString==null) || (queryString.length() == 0))
		queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
 %> 

    var HTTPrequest = "<%=getQueryString(queryString)%>";  

function apriGestioneDoc(rptAction,parametri,tipoDoc, pageDaChiamare, forzaProtocollazione, rptInPopup)
{
  var urlo = "AdapterHTTP?PAGE=GestioneStatoDocPage";
  urlo += "&rptAction="+rptAction;
  urlo += parametri;
  urlo +="&tipoDoc="+tipoDoc;
  urlo += "&QUERY_STRING="+HTTPrequest;
  if ((typeof pageDaChiamare)!= undefined && pageDaChiamare!=null )
  	urlo +="&PAGE_DA_CHIAMARE="+pageDaChiamare;
  if ((typeof forzaProtocollazione)!= undefined && forzaProtocollazione!=null )
  	urlo +="&FORZA_PROTOCOLLAZIONE="+forzaProtocollazione;
  if ((typeof rptInPopup)!= undefined && rptInPopup!=null )
  	urlo +="&RPT_IN_POPUP="+rptInPopup; // true|false
  var titolo = "gestioneDoc";
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=350; var t=((screen.availHeight)-h)/2;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  var opened = window.open(urlo, titolo, feat);
  opened.focus();
}
</script>

</head>

<body class="gestione" onload="rinfresca();">
<%
    if(infCorrentiAzienda != null) {%> 
       <div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
  <%}
      
    if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
      Linguette l = new Linguette( user,  _funzione, _page, new BigDecimal(prgRichiestaAz));
      l.setCodiceItem("PRGRICHIESTAAZ");    
      l.show(out);
    }
%>
<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
  <center>
    <font color="green">
      <af:showMessages prefix="M_SaveTestataRichiestaPubb"/>
      <af:showMessages prefix="M_CHECK_INVIO_VACANCY_SIL"/>
      <af:showMessages prefix="InviaVacancySil"/>
    </font>
  </center>
  <center>
    <font color="red"><af:showErrors /></font>
  </center>
  
  <%out.print(htmlStreamTop);%>

  <table class="main" border="0">
    <tr><td colspan="4" align="left">
    </td></tr><tr><td>&nbsp;</td></tr>
    <tr valign="top">
      <td class="etichetta2">Data</td>
      <td class="campo2">
        <af:textBox name="datPubblicazione" value="<%=datPubblicazione%>" title="Data pubblicazione" 
             			  type="date" classNameBase="input" validateOnPost="true" 
                 	  onKeyUp="fieldChanged();" 
                 	  readonly="<%= String.valueOf(!canModifyPubb || flgCrescoInviata || readOnlyModifyDtPubb)%>" size="11" 
                 	  maxlength="10"/>&nbsp;
      </td>
	  
	  <td class="etichetta2">Scadenza</td>
      <td class="campo2">
      <af:textBox name="datScadenzaPubblicazione" value="<%=datScadenzaPubblicazione%>" 
	  	title="Data scadenza pubblicazione" type="date" classNameBase="input"
	    validateOnPost="true" 
	    readonly="<%=String.valueOf(!canModifyPubb || flgCrescoInviata)%>" size="11" 
	    maxlength="10"/>
	  </td>   		
   </tr>
   <tr><td colspan=4><br/></td></tr>
   <tr valign="top">
     <td class="etichetta2" >In pubblicazione</td>
     <td class="campo2" colspan=3>
       <af:textBox name="flgPubblicata_TMP" type="text" classNameBase="input" value="" readonly="true"/>
       <input type="hidden" name="flgPubblicata" value="<%=flgPubblicata%>"/>
       <script language="javascript">
          <% if (flgPubblicata.equalsIgnoreCase("S")) { %>
            document.Frm1.flgPubblicata_TMP.value = "Sì";
          <%} else {%>
            document.Frm1.flgPubblicata_TMP.value = "No";
          <%}%>
      </script>
     </td>
   </tr>
   <tr valign="top">
    <td class="etichetta2">Categoria</td>
    <td class="campo2">
      <af:comboBox title="Categoria" classNameBase="input" name="codQualifica" selectedValue="<%=codQualifica%>" 
            			 moduleName="M_GetIdoTipiQualificaPub"  addBlank="true" onChange="fieldChanged();" required="true"
            			 disabled="<%= String.valueOf(!canModifyCresco) %>"
            			 />
                 
    </td>
   </tr>
   <tr><td colspan=4><br/></td></tr>
   <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
   <tr ><td colspan="4" width="100%"><div class="sezione">Modalit&agrave; pubblicazione</div></td></tr>
      <TR>
      <td class="etichetta2">Web</td>
      <td class="campo2">         
				<af:comboBox 
		           name="flgPubbWeb"
		           classNameBase="input" 
		           onChange="fieldChanged();">
		           <option value=""  <% if ( "".equalsIgnoreCase(flgPubbWeb) )  { out.print("SELECTED=\"true\""); } %> ></option>
		           <option value="S" <% if ( "S".equalsIgnoreCase(flgPubbWeb) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
		           <option value="N" <% if ( "N".equalsIgnoreCase(flgPubbWeb) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        		</af:comboBox>
         		<script language="javascript">
		           if (<%=String.valueOf(!canModifyPubb)%>) {
		             document.Frm1.flgPubbWeb.disabled = true;
		           } else 
		             document.Frm1.flgPubbWeb.disabled = false;
		         </script>         
      </td>
    </TR>
    <TR>
      <td class="etichetta2">Giornali</td> 
      <td class="campo2">
       	 <%
       	 		boolean strIf = false;
       	 		// INIT-PARTE-TEMP
				if (Sottosistema.CM.isOff()) {	
				// END-PARTE-TEMP
					strIf = codEvasione.equals("AS");
				// INIT-PARTE-TEMP
				} else {
				// END-PARTE-TEMP
         			strIf = (codEvasione.equals("AS") || "CMA".equalsIgnoreCase(codEvasione));    
         		// INIT-PARTE-TEMP
				} 
				// END-PARTE-TEMP
				
				if (strIf){
          %>
	         	 <af:comboBox 
		           name="flgPubbGiornali"
		           classNameBase="input"
		           onChange="fieldChanged();">
		           <option value=""  <% if ( "".equalsIgnoreCase(flgPubbGiornali) )  { %>SELECTED<% } %> ></option>
		           <option value="S" <% if ( "S".equalsIgnoreCase(flgPubbGiornali) ) { %>SELECTED<% } %> >Sì</option>
		           <option value="N" <% if ( "N".equalsIgnoreCase(flgPubbGiornali) ) { %>SELECTED<% } %> >No</option>
		         </af:comboBox>
		         <script language="javascript">
		           if (<%=String.valueOf(!canModifyPubb)%>) {
		             document.Frm1.flgPubbGiornali.disabled = true;
		           } else
		             document.Frm1.flgPubbGiornali.disabled = true;
		         </script>
		      	<%} else {%>
		      		<af:comboBox 
			           name="flgPubbGiornali"
			           classNameBase="input"
			           onChange="fieldChanged();">
			           <option value=""  <% if ( "".equalsIgnoreCase(flgPubbGiornali) )  { %>SELECTED<% } %> ></option>
			           <option value="S" <% if ( "S".equalsIgnoreCase(flgPubbGiornali) ) { %>SELECTED<% } %> >Sì</option>
			           <option value="N" <% if ( "N".equalsIgnoreCase(flgPubbGiornali) ) { %>SELECTED<% } %> >No</option>
			         </af:comboBox>
			         <script language="javascript">
			           if (<%=String.valueOf(!canModifyPubb)%>) {
			             document.Frm1.flgPubbGiornali.disabled = true;
			           } else
			             document.Frm1.flgPubbGiornali.disabled = false;
			             
			           
			         </script>	      
		      	<%}%>

      </td>
    </TR>
    <TR>
      <td class="etichetta2">Bacheca</td>
      <td class="campo2">
         <af:comboBox 
           name="flgPubbBacheca"
           classNameBase="input"
           onChange="fieldChanged();">
           <option value=""  <% if ( "".equalsIgnoreCase(flgPubbBacheca) )  { out.print("SELECTED=\"true\""); } %> ></option>
           <option value="S" <% if ( "S".equalsIgnoreCase(flgPubbBacheca) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
           <option value="N" <% if ( "N".equalsIgnoreCase(flgPubbBacheca) ) { out.print("SELECTED=\"true\""); } %> >No</option>
         </af:comboBox>
         <script language="javascript">
           if (<%=String.valueOf(!canModifyPubb)%>) {
             document.Frm1.flgPubbBacheca.disabled = true;
           } else
             document.Frm1.flgPubbBacheca.disabled = false;
         </script>
      </td>
     </tr>
     <%if(CRESCO){ %>
     <TR>
      <td class="etichetta2">Cresco</td>
      <td class="campo2">
         <af:comboBox 
           name="flgPubbCresco"
           classNameBase="input"
           onChange="fieldChanged();">
           <option value=""  <% if ( "".equalsIgnoreCase(flgPubbCresco) )  { out.print("SELECTED=\"true\""); } %> ></option>
           <option value="S" <% if ( "S".equalsIgnoreCase(flgPubbCresco) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
           <option value="N" <% if ( "N".equalsIgnoreCase(flgPubbCresco) ) { out.print("SELECTED=\"true\""); } %> >No</option>
         </af:comboBox>
         <script language="javascript">
           if (<%=String.valueOf(!canModifyCresco)%>) {
             document.Frm1.flgPubbCresco.disabled = true;
           } else
             document.Frm1.flgPubbCresco.disabled = false;
         </script>
      </td>
     </tr>
      <%} %>
     <%if(isFlagCandidatura){ %>
     <TR>
      <td class="etichetta2">Abilita Candidatura</td>
      <td class="campo2">
         <af:comboBox 
           name="flgCandidaturaVacPal"
           classNameBase="input"
           onChange="fieldChanged();">
           <option value=""  <% if ( "".equalsIgnoreCase(flgCandidaturaVacPal) )  { out.print("SELECTED=\"true\""); } %> ></option>
           <option value="S" <% if ( "S".equalsIgnoreCase(flgCandidaturaVacPal) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
           <option value="N" <% if ( "N".equalsIgnoreCase(flgCandidaturaVacPal) ) { out.print("SELECTED=\"true\""); } %> >No</option>
         </af:comboBox>
         <script language="javascript">
           if (<%=String.valueOf(!canModifyPubb)%>) {
             document.Frm1.flgCandidaturaVacPal.disabled = true;
           } else
             document.Frm1.flgCandidaturaVacPal.disabled = false;
         </script>
      </td>
     </tr>
      <%} %>
     <tr>
      <td colspan="4">&nbsp;</td>	
     </tr>
     <tr>
      <td colspan="4">
      NB: per l'incrocio mirato i campi Giornali e Bacheca sono utilizzabili esclusivamente ai fini delle ricerche
      </td>
    </TR>
    <tr><td colspan="4"><br/></td></tr>
  </table>
  <br>
  <table id="sezione_riferimento" width="100%" border="0">

     <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
     <tr ><td colspan="4" width="100%"><div class="sezione">Riferimento</div></td></tr>

     <tr><td colspan="4"><div id="comboRiferimenti" style="display:none">
     <table cellpadding="0" cellspacing="0" border="0" width="100%">
     <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
       <tr><td class="etichetta2">Riferimenti</td>
           <td class="campo2" colspan="3">
              <af:comboBox classNameBase="input" name="Riferimenti" onChange="SettaParametriRiferimenti(this);"
                           addBlank="true" disabled="<%=String.valueOf(!canModify) %>">
              </af:comboBox>
           </td>
     </tr>
    <%
    SourceBean riga = null;
    Vector RiferimentiAzienda = serviceResponse.getAttributeAsVector("M_GetReferentiAziendaRichiestaPubb.ROWS.ROW");
    %>
    <SCRIPT TYPE="text/javascript">
      var ArrayRif = new Array ();
      <%for (int i = 0; i < RiferimentiAzienda.size(); i++) {
          riga=(SourceBean) RiferimentiAzienda.elementAt(i);
          strCognomeRiferimento = StringUtils.getAttributeStrNotNull(riga, "STRCOGNOME");
          strNomeRiferimento    = StringUtils.getAttributeStrNotNull(riga, "STRNOME");
          strTelRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRTELEFONO");
          strFaxRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRFAX");
          strEmailRiferimento   = StringUtils.getAttributeStrNotNull(riga, "STREMAIL");
      %>
          var riferimenti = new Riferimento();
          riferimenti.Cognome="<%=strCognomeRiferimento%>";
          riferimenti.Nome="<%=strNomeRiferimento%>";
          riferimenti.Telefono="<%=strTelRiferimento%>";
          riferimenti.Fax="<%=strFaxRiferimento%>";
          riferimenti.E_mail="<%=strEmailRiferimento%>";
          ArrayRif.push(riferimenti);
     <% }//for %>
      </SCRIPT>
     </table></div></td></tr>
<%//}%>

<%
	boolean strIf2 = false;
   	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
		strIf2 = (codEvasione.equals("AS") && flgPubblicata.equalsIgnoreCase("S"));
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
    	strIf2 = ((codEvasione.equals("AS") || "CMA".equalsIgnoreCase(codEvasione)) && flgPubblicata.equalsIgnoreCase("S"));    
    // INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
			
	if (strIf2){
	
  	//if (codEvasione.equals("AS") && flgPubblicata.equalsIgnoreCase("S")) {
  		canModifyPubb = false;
  		// 06/02/2007 Savino: tolti gli attributi disabled alle textarea
  		   
%>
  		   <tr valign="top">
            <td class="etichetta2">Cognome</td>
            <td class="campo2">
              <af:textBox classNameBase="input" title="Cognome nel riferimento" onKeyUp="fieldChanged();" name="strCognomeRifPubb" value="<%=strCognomeRifPubb%>" readonly="<%= String.valueOf(!canModifyPubb) %>"    maxlength="40" />
            </td>
            <td class="etichetta2">Nome</td>
            <td class="campo2">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNomeRifPubb" size="20" value="<%=strNomeRifPubb%>" readonly="<%= String.valueOf(!canModifyPubb) %>" maxlength="40" />
            </td>
          </tr>

          <tr valign="top">
            <td class="etichetta2">Telefono</td>
            <td class="campo2">
              <af:textBox classNameBase="input" title="Telefono nel riferimento" onKeyUp="fieldChanged();" name="strTelRifPubb" size="20" value="<%=strTelRifPubb%>" readonly="<%= String.valueOf(!canModifyPubb) %>" maxlength="50" />
            </td>
            <td class="etichetta2">Fax</td>
            <td class="campo2">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFaxRifPubb" size="20" value="<%=strFaxRifPubb%>" readonly="<%= String.valueOf(!canModifyPubb) %>" maxlength="20" />
            </td>
          </tr>

          <tr valign="top">
            <td class="etichetta2">Email</td>
            <td class="campo2" colspan="3">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strEmailRifPubb" size="<%=strEmailRifPubb.length()%>" value="<%=strEmailRifPubb%>" readonly="<%= String.valueOf(!canModifyPubb) %>" maxlength="80" />
            </td>
          </tr>
          
<% if((canModify || canModifyPubb)){%><script TYPE="text/javascript">CaricaRiferimenti(ArrayRif);</script><%}%>
          <tr valign="top">
            <td colspan="4">&nbsp;</td>
          </tr>
          <tr valign="top">
            <td colspan="4"><div class="sezione">Categoria CM</div></td>
          </tr>
          <tr valign="top">
          	<td class="etichetta2">Categoria CM</td>
            <td class="campo2" colspan="3">
				<af:comboBox classNameBase="input" 
              				onChange="fieldChanged();" 
              				name="codMonoCMcatPubb" 
              				title="Categoria CM"
              				required="false"
              				disabled="<%= String.valueOf(!(canModify&&flgStatoEvasioneCatCM)) %>">
              	<option value="" <% if ( "".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>></option>
              	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %> >Disabili</option> 
              	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Categoria protetta ex. Art. 18</option> 
              	<option value="E" <% if ( "E".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Entrambi</option> 
              </af:comboBox>
			</td>
          </tr>
          <tr><td colspan=4><br></td></tr>
          <tr ><td colspan="4" width="100%"><hr width="90%"/></td></tr>
          <tr valign="top">
            <td class="etichetta2">Dati azienda</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strDatiAziendaPubb" 
              			   cols="50" value="<%=strDatiAziendaPubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>"  />
            </td>
          </tr>
          
          <tr valign="top">
            <td class="etichetta2">Mansione</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strMansionePubb" 
              			   cols="50" value="<%=strMansionePubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          
          <tr valign="top">
            <td class="etichetta2">Contenuto e contesto del lavoro</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtFiguraProfessionale" 
              			   rows="6" cols="50" value="<%=txtFiguraProfessionale%>" maxlength="4000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Luogo di lavoro</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strLuogoLavoro" 
              			   cols="50" value="<%=strLuogoLavoro%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>"  />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Formazione</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strFormazionePubb" 
              			   cols="50" value="<%=strFormazionePubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>"  />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Contratto</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtCondContrattuale" 
              			   cols="50" value="<%=txtCondContrattuale%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>"   />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Conoscenze</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strConoscenzePubb" 
              			   cols="50" value="<%=strConoscenzePubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>"   />
            </td>
          </tr>       
          <tr valign="top">
            <td class="etichetta2">Caratteristiche del candidato</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtCaratteristFigProf" 
              			   cols="50" value="<%=txtCaratteristFigProf%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>"   />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Orario</td> 
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNoteOrarioPubb" 
              			   cols="50" value="<%=strNoteOrarioPubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Per candidarsi</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strRifCandidaturaPubb" 
              			   cols="50" value="<%=strRifCandidaturaPubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>"  />
            </td>
          </tr>
  
<%
    } else {
%>
    	<tr valign="top">
            <td class="etichetta2">Cognome</td>
            <td class="campo2">
              <af:textBox classNameBase="input" title="Cognome nel riferimento" onKeyUp="fieldChanged();" name="strCognomeRifPubb" value="<%=strCognomeRifPubb%>" readonly="<%= String.valueOf(!canModify) %>"  maxlength="40" />
            </td>
            <td class="etichetta2">Nome</td>
            <td class="campo2">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNomeRifPubb" size="20" value="<%=strNomeRifPubb%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="40" />
            </td>
          </tr>

          <tr valign="top">
            <td class="etichetta2">Telefono</td>
            <td class="campo2">
              <af:textBox classNameBase="input" title="Telefono nel riferimento" onKeyUp="fieldChanged();" name="strTelRifPubb" size="20" value="<%=strTelRifPubb%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="50" />
            </td>
            <td class="etichetta2">Fax</td>
            <td class="campo2">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFaxRifPubb" size="20" value="<%=strFaxRifPubb%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" />
            </td>
          </tr>

          <tr valign="top">
            <td class="etichetta2">Email</td>
            <td class="campo2" colspan="3">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strEmailRifPubb" size="<%=strEmailRifPubb.length()%>" value="<%=strEmailRifPubb%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="80" />
            </td>
          </tr>
          
<%if((canModify || canModifyPubb)){%><script TYPE="text/javascript">CaricaRiferimenti(ArrayRif);</script><%}%>
          <tr valign="top">
            <td colspan="4">&nbsp;</td>
          </tr>
          <tr valign="top">
            <td colspan="4"><div class="sezione">Categoria CM</div></td>
          </tr>
          <tr valign="top">
          	<td class="etichetta2">Categoria CM</td>
            <td class="campo2" colspan="3">
				<af:comboBox classNameBase="input" 
              				onChange="fieldChanged();" 
              				name="codMonoCMcatPubb" 
              				title="Categoria CM"
              				required="false"
              				disabled="<%= String.valueOf(!(canModifyPubb&&flgStatoEvasioneCatCM)) %>">
              	<option value="" <% if ( "".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>></option>
              	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %> >Disabili</option> 
              	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Categoria protetta ex. Art. 18</option> 
              	<option value="E" <% if ( "E".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Entrambi</option> 
              </af:comboBox>
			</td>
          </tr>
          
          
          <tr valign="top">
            <td colspan="4">&nbsp;</td>
          </tr>
          <tr valign="top">
            <td colspan="4"><div class="sezione">Altre Informazioni per invio a Cliclavoro</div></td>
          </tr>
          <tr valign="top">
          	<td class="etichetta2">Rapporto Lavoro</td>
            <td class="campo2">
				<af:comboBox 
					name="CODRAPPORTOLAV"
					title="Rapporto di lavoro"
					moduleName="M_ComboRapportoLav" 
					selectedValue="<%=codRapportoLav%>"
					addBlank="true"
					required="false"
					disabled="<%= String.valueOf(!canModifyPubb) %>"
					classNameBase="input"
					onChange="" />
			</td>
			
			   
	      <td class="etichetta2">Nulla osta ai sensi dell'art.9 comma 8 del d.l. 76/2013</td>
	      <td class="campo2">
	         <af:comboBox 
	           name="flgNullaOsta"
	           classNameBase="input"
	           disabled="<%= String.valueOf(!canModifyPubb) %>"
	           onChange="fieldChanged();">
	           <option value=""  <% if ( "".equalsIgnoreCase(flgNullaOsta) )  { out.print("SELECTED=\"true\""); } %> ></option>
	           <option value="S" <% if ( "S".equalsIgnoreCase(flgNullaOsta) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
	           <option value="N" <% if ( "N".equalsIgnoreCase(flgNullaOsta) ) { out.print("SELECTED=\"true\""); } %> >No</option>
	         </af:comboBox>
	      </td>
          </tr>
          
          <tr><td colspan=4><br></td></tr>
          <tr ><td colspan="4" width="100%"><hr width="90%"/></td></tr>
          <tr valign="top">
            <td class="etichetta2">Dati azienda</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strDatiAziendaPubb" 
              			   cols="50" value="<%=strDatiAziendaPubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          
          <tr valign="top">
            <td class="etichetta2">Mansione</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strMansionePubb" 
              			   cols="50" value="<%=strMansionePubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          
          <tr valign="top">
            <td class="etichetta2">Contenuto e contesto del lavoro</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtFiguraProfessionale" 
              			   rows="6" cols="50" value="<%=txtFiguraProfessionale%>" maxlength="4000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>"/>
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Luogo di lavoro</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strLuogoLavoro" 
              			   cols="50" value="<%=strLuogoLavoro%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Formazione</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strFormazionePubb" 
              			   cols="50" value="<%=strFormazionePubb%>" maxlength="2000"
              			   readonly="<%= String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Contratto</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtCondContrattuale" 
              			   cols="50" value="<%=txtCondContrattuale%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Conoscenze</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strConoscenzePubb" 
              			   cols="50" value="<%=strConoscenzePubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>       
          <tr valign="top">
            <td class="etichetta2">Caratteristiche del candidato</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="txtCaratteristFigProf" 
              			   cols="50" value="<%=txtCaratteristFigProf%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Orario</td> 
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNoteOrarioPubb" 
              			   cols="50" value="<%=strNoteOrarioPubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta2">Per candidarsi</td>
            <td class="campo2" colspan="3">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strRifCandidaturaPubb" 
              			   cols="50" value="<%=strRifCandidaturaPubb%>" maxlength="2000"
              			   readonly="<%=String.valueOf(!canModifyPubb)%>" />
            </td>
          </tr>		
    <%}%>

  </table> 
  <br/>
<input type="hidden" name="prgRichiestaAZ" value="<%= (prgRichiestaAz == null) ? "" : prgRichiestaAz%>" />  
<input type="hidden" name="prgAzienda" value="<%= (prgAzienda == null) ? "" : prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%= (prgUnita==null) ? "" : prgUnita %>" />
<input type="hidden" name="cdnUtMod" value="<%= cdnUtMod %>"/>
<input type="hidden" name="numkloRichiestaAz" value="<%= numkloRichiestaAz %>"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="PAGE" value="PubbRichiestePage"/>
<input type="hidden" name="salva" value=""/>
<input type="hidden" name="codEvasione" value="<%=codEvasione%>"/>
<input type="hidden" name="codCPI" value="<%=codCPI%>"/>
<input type="hidden" name="prgAlternativa" value="1"/>
<input type="hidden" name="configFlussoVacancy" value="<%=numConfigFlusso%>"/>
<input type="hidden" name="invioFlussoVacancy" value="NO"/>
<input type="hidden" name="strcheckRER" value="<%= (RER) ? "1" : "0" %>"/>

<%	
    if(strIf2) {
  	//if (codEvasione.equals("AS") && flgPubblicata.equalsIgnoreCase("S")) {
  		canModifyPubb = false;
  	// 	non visualizzo alcun bottone
  	} else {    
%>
		<%if (canModify || canModifyPubb) { %>
		  <input class="pulsante<%=(("S".equalsIgnoreCase(flgPubblicata))?"Disabled":"")%>" type="button" name="pubblica" value="Pubblica/Aggiorna" onClick="avvisaFlgPubblicata();" 
		        <% if ( "S".equalsIgnoreCase(flgPubblicata) ) { out.print("DISABLED=\"true\""); } %>/>    
		  <input class="pulsante" type="button" name="prevalorizza" value="Prevalorizza/Aggiorna" onClick="salvaCampi('TRUE')"/>
		  <!--<input class="pulsante" type="button" name="aggiorna" value="Aggiorna" onClick="salvaCampi('FALSE')"/>-->
		<% } %>
		
		<!-- se lo stato di evasione è Asta Art.16 l'anteprima di stampa non deve essere visibile -->		 
		<%
		boolean strIf3 = false;
	   	// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {	
		// END-PARTE-TEMP
			strIf3 = !codEvasione.equals("AS");
		// INIT-PARTE-TEMP
		} else {
		// END-PARTE-TEMP
	    	strIf3 = (!codEvasione.equals("AS") || !"CMA".equalsIgnoreCase(codEvasione));    
	    // INIT-PARTE-TEMP
		} 
		// END-PARTE-TEMP		
		
		if (strIf3) {
			if(flgPubblica){%>
				<input class="pulsante" type="button" name="anteprimaStampa" value="Anteprima di stampa"
		       		onClick="apriGestionePubblSpecifica('RPT_PUBB_SPEC','&prgRichAzienda=<%=prgRichiestaAz%>&NOMEFILE=PubbRichOp_CC.rpt','PUB')" />
		<%
			}
		}
		%>
		<%if (canAnteprimaFlusso) { %>
 		  		<input type="button" class="pulsante" name="anteprimaFlusso" value="Anteprima Invio" onclick="apriAnteprimaInvioFlusso();"/>
		  
		<%}
		  if (canInviaFlusso) {  %>
		  <input class="pulsante<%=(("S".equalsIgnoreCase(flgPubblicata) && isEvasione)?"":"Disabled")%>" type="button" name="inviaflusso" value="Invia/Sincronizza" onclick="inviaFlussoSilPortal();"
		        <% if ( !("S".equalsIgnoreCase(flgPubblicata)) ) { out.print("DISABLED=\"true\""); } %>/>    
		<% } %>
		
		
		
		<%if (canModify || canModifyPubb || canModifyCresco) { %>
		<br/><br/>
		<input class="pulsante" type="button" name="aggiorna" value="Aggiorna" onClick="salvaCampi('FALSE')"/>
		<% } %>
	<% } %>
	
	<% if (canAnteprimaArt16) { %>
	<input 
		class="pulsanti" type="button" 
		onclick="stampaAdesioniArt16()" 
		value="Anteprima Stampa Avviso Pubblico" />
	<% } %>
	
</af:form>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">
<% operatoreInfo.showHTML(out); %>
</p>
<br/>

</body>
</html>

<% //} %>