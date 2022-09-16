<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.*,java.text.SimpleDateFormat,it.eng.sil.module.movimenti.*,java.text.*,it.eng.afExt.utils.*,it.eng.sil.util.*,it.eng.sil.util.GiorniNL,it.eng.sil.module.agenda.*,java.util.*,java.math.*,it.eng.sil.security.User,java.io.*,com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
	int _funzione = 0;
	InfCorrentiLav infCorrentiLav = null;

	BigDecimal cdnUtIns = null;
	String dtmIns = null;
	BigDecimal cdnUtMod = null;
	String dtmMod = null;
	Testata operatoreInfo = null;
	String NUMKLORICHCOMPUTO = null;

	String STRRAGIONESOCIALE = "";
	String STRCODICEFISCALE = "";
	String STRPARTITAIVA = "";

	String PRGRICHCOMPUTO = null;
	String PROVINCIA_ISCR = null;
	String prgAzienda = null;
	String DATINIZIOCOMP = null;
	String DATASSUNZIONE = null;
	String CODMONOCATEGORIA = "";
	String DECORESETTIMANALI = "";
	String DECORETOTALI = "";
	String CODMOTCOMPUTO = "";
	String PRGMOVIMENTO = "";
	String datInizioMov = "";
	String codStatoAttoMov = "";
	String codFiscaleAzienda = "";
	String codFiscaleLavoratore = "";

	//Configurazione
	SourceBean sb_provvedimento = Utils.getConfigValue("CMCOMP");
	String s_conf_provvedimento = (String) sb_provvedimento
			.getAttribute("row.num");
	
	
	
	//Esiste Provv
	String esisteProvv = "0";
	SourceBean esisteProvvSB = (SourceBean) serviceResponse
			.getAttribute("M_ESISTE_PROVVEDIMENTO.ROWS.ROW");
	if (esisteProvvSB != null) {
		esisteProvv = esisteProvvSB.getAttribute("num_provv")
				.toString();
	}
	
	
	//Stato Del documento Provv
	String stattoDocProvv = "";
	
	
	int num_Prov = Integer.parseInt(esisteProvv);
	if (num_Prov > 0) { 
		SourceBean stattoProvvSB = (SourceBean) serviceResponse
		.getAttribute("M_Stato_Documento_Provv.ROWS.ROW");
		stattoDocProvv = stattoProvvSB.getAttribute("CODSTATOATTO")
		.toString();		
	}

	BigDecimal numAnnoProt = null;
	BigDecimal numProtocollo = null;
	String dataProt = "";

	Calendar oggi = Calendar.getInstance();
	String giornoDB = Integer.toString(oggi.get(5));
	if (giornoDB.length() == 1) {
		giornoDB = '0' + giornoDB;
	}
	String meseDB = Integer.toString(oggi.get(2) + 1);
	if (meseDB.length() == 1) {
		meseDB = '0' + meseDB;
	}
	String annoDB = Integer.toString(oggi.get(1));
	String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;

	boolean nuovoComputo = !(serviceRequest
			.getAttribute("nuovoComputo") == null || ((String) serviceRequest
			.getAttribute("nuovoComputo")).length() == 0);

	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("RIMUOVI");
	boolean canInsert = attributi.containsButton("INSERISCI");
	boolean canSalvaStato = attributi.containsButton("SALVA-STATO");
	boolean canProvvedimento = attributi
	.containsButton("PROVVEDCOMPUTO");
	//boolean canModifyAzLav = true;

	canInsert = canInsert && canSalvaStato;
	canModify = canModify && canSalvaStato;

	boolean StatoModify = true;

	boolean readOnlyStr = !canModify;
	String fieldReadOnly = canModify ? "false" : "true";

	String fromRicerca = StringUtils.getAttributeStrNotNull(
			serviceRequest, "fromRicerca");

	prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,
			"prgAzienda");
	String prgUnita = StringUtils.getAttributeStrNotNull(
			serviceRequest, "PRGUNITA");

	PRGRICHCOMPUTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PRGRICHCOMPUTO");
	
	PROVINCIA_ISCR = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PROVINCIA_ISCR");
	

	String inserisciNewFromRicerca = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "inserisciNewFromRicerca");
	String inserisciNew = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "inserisciNew");

	String listaProv = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "listaProv");
	String newDettProvPage = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "newDettProvPage");
	String inserisciDettProv = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "inserisciDettProv");
	String eliminaDettProv = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "eliminaDettProv");
	String dettaglioDettProv = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "dettaglioDettProv");
	String aggiornamentoDettProv = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "aggiornamentoDettProv");
	String aggiornamento = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "aggiornamento");

	String goBackListPage = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "goBackListPage");

	String movimenti = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "movimenti");
	String associaMovimento = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "associaMovimento");

	String tipoMenu = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"tipoMenu");

	String DatAcqRil = "";
	String DatInizio = "";
	
	//dati lavoratore
	String cdnLavoratore = StringUtils.getAttributeStrNotNull(
			serviceRequest, "CDNLAVORATORE");
	String strCodiceFiscaleLav = "";
	String strNomeLav = "";
	String strCognomeLav = "";

	//----------------------------------------------------------------------------------------------------
	SourceBean rowRich = null;
	SourceBean rowDoc = null;
	Vector rowsDoc = null;
	String prAutomatica = "S";
	String docInOut = "I";
	String docRif = "Documentazione L68";
	String docTipo = "L 68 Computo";
	BigDecimal numProtV = null;
	BigDecimal numAnnoProtV = null;
	String dataOraProt = "";
	boolean noButton = false;
	String datProtV = "";
	String oraProtV = "";
	Vector rowsProt = null;
	SourceBean rowProt = null;

	Vector rowsDoc1 = null;
	SourceBean rowDoc1 = null;

	String codStatoAttoV = "";
	String CODSTATOATTO = "NP";

	boolean isInsert = (StringUtils.getAttributeStrNotNull(
			serviceRequest, "nuovo")).equals("true") ? true : false;

	//if(!nuovoComputo){
	//	canModifyAzLav = true;
	//}
	String fieldReadOnlyNoStato = "false";
	if (!nuovoComputo) {
		CODSTATOATTO = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "CODSTATOATTO");
		SourceBean rowIns = null;
		Vector rowsIns = serviceResponse
				.getAttributeAsVector("M_Load_Doc_Computo.ROWS.ROW");
		if (rowsIns.size() == 1) {
			isInsert = true;
		}

		if (rowsIns != null && !rowsIns.isEmpty()) {
			rowProt = (SourceBean) rowsIns.firstElement();
			CODSTATOATTO = StringUtils.getAttributeStrNotNull(
					rowProt, "CODSTATOATTO");
			
			
			if (CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")) {
				numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt,
						"NUMPROTOCOLLO", null);
				numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(
						rowProt, "numAnnoProt", null);
				dataOraProt = StringUtils.getAttributeStrNotNull(
						rowProt, "DATPROTOCOLLO");
				if (!dataOraProt.equals("")) {
					oraProtV = dataOraProt.substring(11, 16);
					datProtV = dataOraProt.substring(0, 10);
				}
			}

			DatAcqRil = SourceBeanUtils.getAttrStrNotNull(rowProt,
					"DatAcqRil");
			DatInizio = SourceBeanUtils.getAttrStrNotNull(rowProt,
					"DatInizio");

		}
	}

	//----------------------------------------------------------------------------------------------------	  

	if (nuovoComputo) {

		//prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
		strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(
				serviceRequest, "codiceFiscaleLavoratore");
		strNomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,
				"nome");
		strCognomeLav = StringUtils.getAttributeStrNotNull(
				serviceRequest, "cognome");

		DATINIZIOCOMP = dataOdierna;
		DATASSUNZIONE = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "DATASSUNZIONE");
		CODMONOCATEGORIA = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "CODMONOCATEGORIA");

		DECORESETTIMANALI = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "DECORESETTIMANALI");
		DECORETOTALI = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "DECORETOTALI");
		CODMOTCOMPUTO = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "CODMOTCOMPUTO");
		PRGMOVIMENTO = SourceBeanUtils.getAttrStrNotNull(
				serviceRequest, "PRGMOVIMENTO");

		cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer()
				.getSessionContainer().getAttribute("_CDUT_");
		cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer()
				.getSessionContainer().getAttribute("_CDUT_");

	} else {

		SourceBean row = null;
		Vector rows = serviceResponse
				.getAttributeAsVector("M_Load_Computo.ROWS.ROW");
		// siamo in dettaglio per cui avro' al massimo una riga
		if (rows.size() == 1) {
			row = (SourceBean) rows.get(0);

			PRGRICHCOMPUTO = Utils.notNull(row
					.getAttribute("PRGRICHCOMPUTO"));
			prgAzienda = Utils.notNull(row.getAttribute("prgAzienda"));
			 
			cdnLavoratore = Utils.notNull(row
					.getAttribute("CDNLAVORATORE"));
			prgUnita = Utils.notNull(row.getAttribute("PRGUNITA"));
			CODMOTCOMPUTO = Utils.notNull(row
					.getAttribute("CODMOTCOMPUTO"));
			DATINIZIOCOMP = Utils.notNull(row
					.getAttribute("DATINIZIOCOMP"));
			DATASSUNZIONE = Utils.notNull(row
					.getAttribute("DATASSUNZIONE"));
			CODMONOCATEGORIA = Utils.notNull(row
					.getAttribute("CODMONOCATEGORIA"));
			DECORESETTIMANALI = Utils.notNull(row
					.getAttribute("DECORESETTIMANALI"));
			DECORETOTALI = Utils.notNull(row
					.getAttribute("DECORETOTALI"));
			PRGMOVIMENTO = Utils.notNull(row
					.getAttribute("PRGMOVIMENTO"));

			NUMKLORICHCOMPUTO = String.valueOf(((BigDecimal) row
					.getAttribute("NUMKLORICHCOMPUTO")).intValue());

			cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
			dtmIns = (String) row.getAttribute("DTMINS");

			cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");

			dtmMod = (String) row.getAttribute("DTMMOD");
			operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod,
					dtmMod);
			PROVINCIA_ISCR = Utils.notNull(row.getAttribute("PROVINCIA_ISCR"));
			fieldReadOnlyNoStato = "true";
		}

	}

	if (!PRGMOVIMENTO.equals("") && !nuovoComputo) {
		SourceBean rowMov = (SourceBean) serviceResponse
				.getAttribute("M_Load_Mov_Collegato.ROWS.ROW");
		datInizioMov = Utils.notNull(rowMov
				.getAttribute("datiniziomov"));
		codStatoAttoMov = Utils.notNull(rowMov
				.getAttribute("codstatoattomov"));
		codFiscaleAzienda = Utils.notNull(rowMov
				.getAttribute("codfiscaleazienda"));
		codFiscaleLavoratore = Utils.notNull(rowMov
				.getAttribute("codfiscalelavoratore"));
	}

	String strFlgCfOk = "";
	String strFlgDatiOk = "";
	String IndirizzoAzienda = "";
	String descrTipoAz = "";
	String codTipoAz = "";
	String codnatGiurAz = "";

	if (!prgAzienda.equals("") && !prgUnita.equals("")) {
		InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,
				prgUnita);
		STRRAGIONESOCIALE = currAz.getRagioneSociale();
		STRPARTITAIVA = currAz.getPIva();
		STRCODICEFISCALE = currAz.getCodiceFiscale();
		IndirizzoAzienda = currAz.getIndirizzo();
		descrTipoAz = currAz.getDescrTipoAz();
		codTipoAz = currAz.getTipoAz();
		codnatGiurAz = currAz.getCodNatGiurAz();
		strFlgDatiOk = currAz.getFlgDatiOk();
		if (strFlgDatiOk != null) {
			if (strFlgDatiOk.equalsIgnoreCase("S")) {
				strFlgDatiOk = "Si";
			} else if (strFlgDatiOk.equalsIgnoreCase("N")) {
				strFlgDatiOk = "No";
			}
		}
	}

	//Dati lavoratore
	if (!cdnLavoratore.equals("")) {
		//Oggetto per la generazione delle informazioni sul lavoratore
		InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(
				cdnLavoratore));
		strCodiceFiscaleLav = datiLav.getCodFisc();
		strNomeLav = datiLav.getNome();
		strCognomeLav = datiLav.getCognome();
		strFlgCfOk = datiLav.getFlgCfOk();
		if (strFlgCfOk != null) {
			if (strFlgCfOk.equalsIgnoreCase("S")) {
				strFlgCfOk = "Si";
			} else if (strFlgCfOk.equalsIgnoreCase("N")) {
				strFlgCfOk = "No";
			}
		}
	}

	/*------------------------ inserire il parametro cdnFunzione ---------------------------*/
	_funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(
			serviceRequest, "CDNFUNZIONE"));

	String statoAtto = "";
	SourceBean row2 = null;
	if (!nuovoComputo) {
		Vector rows2 = serviceResponse
				.getAttributeAsVector("M_Load_Doc_Computo.ROWS.ROW");
		if (rows2.size() == 1) {
			row2 = (SourceBean) rows2.get(0);
			statoAtto = Utils
					.notNull(row2.getAttribute("CODSTATOATTO"));
		}
		if (statoAtto.equals("AN")) {
			canModify = false;
			fieldReadOnly = "true";
			canDelete = false;
			canInsert = false;
			noButton = true;
			StatoModify = false;
		}
		if (statoAtto.equals("PR")) {
			canModify = false;
			fieldReadOnly = "true";
			canDelete = false;
			canInsert = false;
			noButton = false;
			StatoModify = false;
		}
	}

	//Servono per gestire il layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>

<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    
    <af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
	
	<%@ include file="MovimentiRicercaSoggetto.inc" %>
	<%@ include file="MovimentiSezioniATendina.inc" %>
	<%@ include file="Common_Function_Mov.inc" %>
	<%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 

    
	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <%if (!readOnlyStr) {%> 
              flagChanged = true;
           <%}%> 
          }

          <%boolean baseComputoChanged = false;%> 
          function baseComputoChanged() {
              <%baseComputoChanged = true;%>
          }
          
          <%boolean percEsoneroChanged = false;%>
          function percEsoneroChanged() {
              <%percEsoneroChanged = true;%>
          }
		  
		  function go(url, alertFlag) {
		  	  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
  
			  var _url = "AdapterHTTP?" + url;
			  if (alertFlag == 'TRUE' ) {
			    if (confirm('Confermi operazione')) {
			      setWindowLocation(_url);
			    }
			  }
			  else {
			    setWindowLocation(_url);
			  }
		  }



		  function provvedimento(){

			    var nuovo=true;
			    var salva=false;
			    var onlyread = false;
				<%num_Prov = Integer.parseInt(esisteProvv);
				  if (num_Prov > 0) { 
				    if(CODSTATOATTO.equals("AN")){%>
				    	onlyread = true;
				    <%}%>				    
				    nuovo=false;
					salva=true;			
				<%}%>
					

	        	url =  "AdapterHTTP?PAGE=ProvvedimentoPage";		
		 		url += "&CDNFUNZIONE=188";		 	
		 		url += "&CDNLAVORATORE=<%=cdnLavoratore%>";		
		 		url += "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value;
		 		url += "&PRGRICHCOMPUTO=<%=PRGRICHCOMPUTO%>";
		 		url += "&tipoMenu=<%=tipoMenu%>";
		 		url += "&PRGUNITA=<%=prgUnita%>";		 		
		 		url += "&nuovo="+nuovo;
		 		url += "&salva="+salva;
				url += "&goBackPage=<%=_page%>";
				url += "&onlyread="+onlyread;
		     
		 		setWindowLocation(url);	 	
		  }
			
		  
		  function AggiornaForm(CODORARIO, NUMORESETT, PRGMOV, DATINIZIOAVV, STATOATTOAVV, CODFISCLAVAVV, CODFISCAZAVV) {
			var f;
		  	window.opener.document.Frm1.DATASSUNZIONE.value = DATINIZIOAVV;
		  	if((CODORARIO.length >= 1 && CODORARIO.substr(0,1) == "P") || CODORARIO == "M"){
				window.opener.document.Frm1.DECORESETTIMANALI.value = NUMORESETT;
		  	}else if(CODORARIO.substr(0,2) == "TP" || CODORARIO == "F"){
				window.opener.document.Frm1.DECORESETTIMANALI.value = "";
			  	window.opener.document.Frm1.DECORETOTALI.value = "";
		  	}
			window.opener.document.Frm1.datInizioMov.value = DATINIZIOAVV; 
			window.opener.document.Frm1.codStatoAttoMov.value = STATOATTOAVV; 
			window.opener.document.Frm1.codFiscaleAzienda.value = CODFISCAZAVV; 
			window.opener.document.Frm1.codFiscaleLavoratore.value = CODFISCLAVAVV;  				
			window.opener.document.Frm1.PRGMOVIMENTO.value = PRGMOV;
		    window.opener.document.getElementById("IMG0").src="../../img/patto_elim.gif";
			window.opener.document.Frm1.assDiss.value="(Dissocia Movimento)";
			window.close();
		  }
	
		 		  
	      function aggiornaAzienda(){
	        document.Frm1.STRRAGIONESOCIALE.value = opened.dati.ragioneSociale;
	        document.Frm1.STRPARTITAIVA.value = opened.dati.partitaIva;
	        document.Frm1.STRCODICEFISCALE.value = opened.dati.codiceFiscaleAzienda;
	        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
	        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;

	        opened.close();
	      }
	      
		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    return urlpage;
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

		  function isNumeric(val){
		  	  return(parseFloat(val,10)==(val*1));
		  }

		  function InsControllaStato() {
		 	var statoDoc = document.Frm1.CODSTATOATTO.value;
		 	if(statoDoc == "AN" || statoDoc == "PR"){
		 		document.Frm1.insDoc.value = 1;
		 	}
		 }
		 
		 function goBack() {
			var f;
			<%String goBackButtonTitle = "";
			boolean goBackIsInList = false;%>
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			<%// Recupero l'eventuale URL generato dalla LISTA precedente
			String token = "_TOKEN_" + goBackListPage;
			String goBackListUrl = (String) sessionContainer.getAttribute(token
					.toUpperCase());
			if (goBackListPage.equals("CMComputiListaPage")) {
				goBackButtonTitle = "Torna alla lista";%>
	      		setWindowLocation("AdapterHTTP?<%=StringUtils.formatValue4Javascript(goBackListUrl)%>");
	      		<%} else {
				goBackButtonTitle = "Torna alla ricerca";%>
	      		f = "AdapterHTTP?PAGE=CMComputoRicPage";
				f = f + "&CDNFUNZIONE=<%=_funzione%>";
				f = f + "&PRGRICHCOMPUTO=<%=PRGRICHCOMPUTO%>";
				f = f + "&tipoMenu=<%=tipoMenu%>";
				<%if (tipoMenu.equals("azienda")) {%>
					f = f + "&PRGAZIENDA=<%=prgAzienda%>";
					f = f + "&PRGUNITA=<%=prgUnita%>";
				<%} else if (tipoMenu.equals("lavoratore")) {%>
					f = f + "&CDNLAVORATORE=<%=cdnLavoratore%>";
	      		<%}%>
	      		document.location = f;
	      		<%}%>
		 }

		
		function aggiornaLavoratore(){
	        document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
	        document.Frm1.cognome.value = opened.dati.cognome;
	        document.Frm1.nome.value = opened.dati.nome;
	        document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
	        opened.close();
         }
		 
		 function controllaNumInter(){
		 	var numInt = document.Frm2.NUMINTERESSATI.value;
		 	var isNum = isNumeric(numInt);
		 	if(!isNum){
		 		alert("Il campo num. deve avere valore numerico");
		 		return false;
		 	}
		 	else{
		 		return true;
			}		 		
		 }

		 function dissociaAziendaLav(azLav,aggAzLav){
	 		 if(document.Frm1.PRGMOVIMENTO.value != ""){
			 	if(!confirm("L'associazione al movimento verrà eliminata, continuare?")) {
					return false;
				}
				document.Frm1.PRGMOVIMENTO.value = ""; 
				document.Frm1.datInizioMov.value = ""; 
				document.Frm1.codStatoAttoMov.value = ""; 
				document.Frm1.codFiscaleAzienda.value = ""; 
				document.Frm1.codFiscaleLavoratore.value = "";				
				document.getElementById("IMG0").src="../../img/patto_ass.gif";
				document.Frm1.assDiss.value="(Associa Movimento)";
			 }
			 apriSelezionaSoggetto(azLav,aggAzLav,'','','');			 	
		 }

		 function associaMovimento(){
	 		 var f;
	 		 var azienda = document.Frm1.STRCODICEFISCALE.value;
	 		 var lavoratore = document.Frm1.codiceFiscaleLavoratore.value;
	 		 var datInizio = document.Frm1.DATINIZIOCOMP.value;
	 		 
	 		 if(document.Frm1.PRGMOVIMENTO.value != ""){
			 	if(confirm("Si desidera cancellare l'associazione?")) {
					dissociaMovimento();
				}
			} else {
				f = "AdapterHTTP?PAGE=CMComputoDettPage&movimenti=1";
	 		 	f = f + "&CDNFUNZIONE=<%=_funzione%>";
	 		 	f = f + "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value;
			 	f = f + "&PRGUNITA=" + document.Frm1.PRGUNITA.value;
			 	f = f + "&CDNLAVORATORE=" + document.Frm1.CDNLAVORATORE.value;
			 	f = f + "&DATINIZCOMP=" + datInizio;
			 	var t = "_blank";
			 	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=500,top=100,left=100";
 		  	 	if(azienda != "" && lavoratore != "" && datInizio != ""){
 		  	 		var opened = window.open(f, t, feat);
    				opened.focus();
 		  	 	}else{
 		  	 		alert("Inserire il Lavoratore, l'Azienda e la Data inizio!");
 		  	 	}
		 	}
		 
		 }
		 
		 function controllaDati(){
		 	 var oreSett = document.Frm1.DECORESETTIMANALI.value;
		 	 var oreTot = document.Frm1.DECORETOTALI.value;
		 	 var numOreSett;
		 	 var numOreTot;
		 	 var isNumOreSett = "true";
		 	 var isNumOreTot = "true";

		 	 if(oreSett != ""){
		 	 	if(isNumeric(oreSett)) isNumOreSett = "true";
		 	 	else isNumOreSett = "false";
		 	 }
		 	 if(oreTot != ""){
		 	 	if(isNumeric(oreTot)) isNumOreTot = "true";
		 	 	else isNumOreTot = "false";		 	 	
		 	 }
		 	 		 	 
		 	 if(isNumOreSett == "true" && isNumOreTot == "true"){
		 	 	if(oreSett != "" && oreTot != ""){
		 	 		numOreSett = parseInt(oreSett, 10);
		 	 		numOreTot = parseInt(oreTot, 10);
		 	 		if(numOreSett > numOreTot) {
		 	 			alert("Il campo Ore lavorate deve essere minore uguale del campo ore totali");
		 	 			return false;		 	 			
		 	 		}
		 	 	}
		 	 }else{
		 	 	alert("I campi Ore lavorate/totali devono avere entrambi valore numerico");
 	 			return false;
		 	 }	
		 	 
			 var datInizioComp = document.Frm1.DATINIZIOCOMP;
 			 var datAssunzione = document.Frm1.DATASSUNZIONE;
    		 if(datInizioComp.value != "" && datAssunzione.value!= ""){
    			if (compareDate(datAssunzione.value,datInizioComp.value) > 0) {
      				alert("La " + datInizioComp.title + " non può essere precedente alla " + datAssunzione.title);
      				return false;
	  			}
	  		 }			 	 
		 	 
		 	 return true;
		 }
		 
		 function dissociaMovimento() {
			document.Frm1.PRGMOVIMENTO.value = ""; 
			document.Frm1.datInizioMov.value = ""; 
			document.Frm1.codStatoAttoMov.value = ""; 
			document.Frm1.codFiscaleAzienda.value = ""; 
			document.Frm1.codFiscaleLavoratore.value = ""; 
			document.getElementById("IMG0").src="../../img/patto_ass.gif";
			document.Frm1.assDiss.value="(Associa Movimento)";		 
		 }
		 
		 function checkMovimentoCollegato() {
		 	 <%if (!nuovoComputo && CODSTATOATTO.equals("NP")) {%>
			 	 var datInizioOld = "<%=DATINIZIOCOMP%>";
			 	 var datInizioNew = document.Frm1.DATINIZIOCOMP.value;
			 	 if(document.Frm1.PRGMOVIMENTO.value != "" && datInizioNew != datInizioOld){
				 	if(confirm("L'associazione al movimento sarà cancellata poichè la data inizio computo è stata modificata.\nSi desidera continuare?")) {
						dissociaMovimento();
					} else {
						document.Frm1.DATINIZIOCOMP.value = datInizioOld;
						return false;
					}
							 	 	
			 	 }	
			 <%}%>
			 return true;
		 }	
		 
		 function ripristinaIconaMovCollegato() {
 		 	document.Frm1.PRGMOVIMENTO.value = '<%=PRGMOVIMENTO%>';
 		 	<%if (PRGMOVIMENTO == "") {%>
 				document.getElementById("IMG0").src="../../img/patto_ass.gif";
 			<%} else if (PRGMOVIMENTO != "") {%>
 				document.getElementById("IMG0").src="../../img/patto_elim.gif";
 			<%}%>		 
		 }	 
		 
	</script>

	<script language="Javascript" src="../../js/docAssocia.js"></script>
		
</head>


<body class="gestione" onload="rinfresca();">

    <%
    	if (movimenti.equals("1")) {
    %>
		<br>
		<af:list moduleName="M_List_Movimenti" skipNavigationButton="0" jsSelect="AggiornaForm"
	             canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />		

	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
        </table>
    <%
    	} else {
    %>

		<%
			if (nuovoComputo) {
		%>
	    	<p class="titolo">Nuova richiesta di Computo</p>
		<%
			} else {
		%>
	    	<p class="titolo">Richiesta di Computo</p>
		<%
			}
		%>
	
		<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati() && checkMovimentoCollegato()">
		
			<input type="hidden" name="PAGE" value="CMComputoDettPage" />		
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>				
			<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
			<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
			<input type="hidden" name="PRGRICHCOMPUTO" value="<%=PRGRICHCOMPUTO%>">
			<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
			<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" >
			<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
			<input type="hidden" name="PRGMOVIMENTO" value="<%=PRGMOVIMENTO%>">	
			<input type="hidden" name="tipoMenu" value="<%=tipoMenu%>">
			<input type="hidden" name="CODMONOCATEGORIA" value="D">
			
			
			<%-- Azienda --%>
		    <div id="divLookAzi_look" style="display:">
				<span class="sezioneMinimale">
					Azienda&nbsp;
					<%
						if ((canModify || nuovoComputo)
										&& !tipoMenu.equals("azienda")) {
					%>
					&nbsp;
					<a href="#" onClick="dissociaAziendaLav('Aziende', 'aggiornaAzienda');"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;*
					<%
						}
					%>
				</span>
				<%=htmlStreamTop%>
				<table class="main" width="100%">
					<tr valign="top">
						<td class="etichetta">Codice Fiscale</td>
						<td class="campo">
							<input title="Azienda" type="text" name="STRCODICEFISCALE" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
							<SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('STRCODICEFISCALE')"; </SCRIPT>
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Partita IVA</td>
						<td class="campo">
							<input type="text" name="STRPARTITAIVA" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Ragione Sociale</td>
						<td class="campo">
							<input type="text" name="STRRAGIONESOCIALE" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
						</td>
					</tr>
				</table>
				<%=htmlStreamBottom%>
			</div>
			
			
			<%-- Lavoratore --%>
		    <div id="divLookLav_look" style="display:">
				<span class="sezioneMinimale">
					Lavoratore&nbsp;
				    <%
				    	if ((canModify || nuovoComputo)
				    					&& !tipoMenu.equals("lavoratore")) {
				    %>
				    	&nbsp;<a href="#" onClick="javascript:dissociaAziendaLav('Lavoratori', 'aggiornaLavoratore');"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;*
				    <%
				    		}
				    	%>
				 </span>
				<%=htmlStreamTop%>
				<table class="main" width="100%">
		          <tr>
		            <td class="etichetta">Codice Fiscale</td>
		            <td class="campo" valign="bottom">
		              <af:textBox title="Lavoratore" classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" value="<%=strCodiceFiscaleLav%>" size="30" maxlength="16"/>
		              <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('codiceFiscaleLavoratore')"; </SCRIPT>
		            </td>
		          </tr>
		          <tr >
		            <td class="etichetta">Cognome</td>
		            <td class="campo">
		              <af:textBox classNameBase="input" type="text" name="cognome" readonly="true" value="<%=strCognomeLav%>" size="30" maxlength="50"/>
		            </td>
		          </tr>
		          <tr>
		            <td class="etichetta">Nome</td>
		            <td class="campo">
		              <af:textBox classNameBase="input" type="text" name="nome" readonly="true" value="<%=strNomeLav%>" size="30" maxlength="50"/>
		            </td>
		          </tr>
				</table>
				<%=htmlStreamBottom%>
			</div>
			
			<af:showMessages prefix="M_Insert_Computo"/>
			<af:showMessages prefix="M_Save_Computo"/>	
			<af:showMessages prefix="SalvaRichCompDoc"/>	
			<af:showErrors />		
	
			<%=htmlStreamTop%>
				<%@ include file="_protocollazionecomputo.inc" %>
				<table class="main" border="0">
		
		
			        <tr><td colspan="2">&nbsp;</td></tr>	      

<tr>
    <td class="etichetta">Ambito Territoriale</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" addBlank="true" required="true" disabled="<%=fieldReadOnlyNoStato%>"/>
    </td>
</tr>
		
 
			        <tr><td colspan="2">&nbsp;</td></tr>	      
					<tr>
					    <td class="etichetta">Data inizio&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data inizio" validateOnPost="true" disabled="false" type="date" name="DATINIZIOCOMP" value="<%=DATINIZIOCOMP%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%
					      	if (canModify) {
					      %>
					      &nbsp;*&nbsp;
					      <%
					      	}
					      %>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATINIZIOCOMP')"; </SCRIPT>
					   </td>
					<tr>			          	        	        
					<tr>
					    <td class="etichetta">Tipologia</td>
					    <td class="campo">
					      <af:comboBox title="Tipologia" name="CODMOTCOMPUTO" moduleName="M_ComboTipiComputo" addBlank="true" selectedValue="<%=CODMOTCOMPUTO%>" disabled="<%= String.valueOf(!canModify) %>" classNameBase="input" onChange="fieldChanged()"/>
					      <%
					      	if (canModify) {
					      %>
					      &nbsp;*&nbsp;
					      <%
					      	}
					      %>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODMOTCOMPUTO')"; </SCRIPT>
					    </td>
					<tr>
					<tr>
					    <td class="etichetta">Data assunzione&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data assunzione" validateOnPost="true" disabled="false" type="date" name="DATASSUNZIONE" value="<%=DATASSUNZIONE%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%
					      	if (canModify) {
					      %>
					      &nbsp;*&nbsp;
					      <%
					      	}
					      %>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATASSUNZIONE')"; </SCRIPT>
					   </td>
					<tr>
					<tr>
						<td class="etichetta">Ore lavorate/totali</td>
						<td class="campo">
							<af:textBox value="<%=DECORESETTIMANALI%>" classNameBase="input" name="DECORESETTIMANALI" maxlength="3" size="3" readonly="<%=fieldReadOnly%>" />
							/&nbsp;<af:textBox value="<%=DECORETOTALI%>" classNameBase="input" name="DECORETOTALI" maxlength="3" size="3" readonly="<%=fieldReadOnly%>" />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					        
					        <%
					        					        	if (PRGMOVIMENTO == "" && canModify) {
					        					        %>
	 							<a href="#" onClick="javascript:associaMovimento();">
					            <img id='IMG0' src="../../img/patto_ass.gif" alt="Associa movimento"></a>
					           	&nbsp;
					           	<%--(Associa movimento) --%>
					           	<af:textBox name="assDiss" size="30" classNameBase="input" value="(Associa movimento)" readonly="true" />
					            
	 						<%
					            	 							} else if (PRGMOVIMENTO != "" && canModify) {
					            	 						%>
	 							<a href="#" onClick="javascript:associaMovimento();">
					        	<img id='IMG0' src="../../img/patto_elim.gif" alt="Dissocia movimento"></a>
	 							&nbsp;
	 							<%--(Dissocia movimento)--%>
	 							<af:textBox name="assDiss" size="30" classNameBase="input" value="(Dissocia movimento)" readonly="true" />
	 						<%
	 							}
	 						%>
	 					</td>
					</tr>	
					<tr>
			          <td colspan="2">&nbsp;</td>
			        </tr> 					
					<tr>
					  	<td colspan="2">
					   		<div class="sezione2">Movimento associato</div>
					   		<table class="main" width="100%" border="0">
      							<tr>
        							<td>&nbsp;&nbsp;&nbsp;</td>
        							<td class="etichetta">Data&nbsp;inizio</td>
        							<td class="campo">  
          								<af:textBox classNameBase="input" title="Data inizio" type="date" name="datInizioMov" value="<%=datInizioMov%>" size="12" maxlength="12" readonly="true"/>
				      				</td>	
				      				<td class="etichetta">Stato&nbsp;Atto</td>       
				      				<td class="campo">
				      					<af:textBox classNameBase="input" title="Stato Atto" type="text" name="codStatoAttoMov" value="<%=codStatoAttoMov%>" size="4" maxlength="2" readonly="true"/>	     
          							</td>
          							<td class="etichetta">CF&nbsp;Azienda</td>
 	  	         					<td class="campo">
             	 							<af:textBox classNameBase="input" type="text" title="Codice fiscale Azienda" name="codFiscaleAzienda" value="<%=codFiscaleAzienda%>" size="20" maxlength="16" readonly="true"/>
        							</td>
        							<td class="etichetta">CF&nbsp;lavoratore</td>
        							<td class="campo">
          								<af:textBox classNameBase="input" type="text" title="Codice fiscale lavoratore" name="codFiscaleLavoratore" value="<%=codFiscaleLavoratore%>" size="25" maxlength="16" readonly="true"/>
        							</td>
      							</tr>
  							</table>
						</td>
					</tr>					
					<tr>
			          <td colspan="2">&nbsp;</td>
			        </tr>  				
			        <tr>
			          <td colspan="2">&nbsp;</td>
			        </tr>  				
			        <tr>
			          <td colspan="2" align="center">
			          	  <%
			          	  	if (nuovoComputo) {
			          	  %>
				          	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onClick="controllaDatARilascio()">
				          	&nbsp;&nbsp;
				          	<input type="reset" class="pulsanti" value="Annulla" onClick="ripristinaIconaMovCollegato();"/>
				          	&nbsp;&nbsp;			          	
				          	<%
			          					          		if (fromRicerca.equals("1")) {
			          					          	%>
					          	<input type="hidden" name="inserisciNewFromRicerca" value="1">
				          	<%
				          		}
				          	%>
				          	<input type="hidden" name="insDoc" value="0">
				          	<input type="hidden" name="inserisciNew" value="1">
				          	<input type="hidden" name="NUMKLORICHCOMPUTO" value="1">
			          	  <%
			          	  	} else {
			          	  %>
			          	    <%
			          	    	if (!nuovoComputo && CODSTATOATTO.equals("NP")) {
			          	    %>
			          	  	<%--input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc()"--%>
			          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="updateControllaStato()">
				          	&nbsp;&nbsp;
				          	<input type="reset" class="pulsanti" value="Annulla" onClick="ripristinaIconaMovCollegato();"/>
			          	  	<%
			          	  		}
			          	  	%>
			          	  	&nbsp;&nbsp;
			          	  	<%	
			          	      num_Prov = Integer.parseInt(esisteProvv);			          	  	
			          	  	  if ("1".equals(s_conf_provvedimento)
			          	  					&& (CODSTATOATTO.equals("PR") || (CODSTATOATTO
			          	  							.equals("AN") && num_Prov > 0)) && canProvvedimento) {   %>	
			          	  	<input type="button" class="pulsante" name="Provvedimento" value="Provvedimento" onclick="provvedimento()" />
			          	    <%}%>
			          	  	<input type="hidden" name="upDoc" value="0">	          	  		
			          	  	<input type="hidden" name="aggiornamento" value="1">
			          	  	<input type="hidden" name="NUMKLORICHCOMPUTO" value="<%=NUMKLORICHCOMPUTO%>">
			          	  	<input type="hidden" name="annullamentoDocFromRich" value="0">
						  <%
						  	}
						  %>
			          	  	<input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="goBack()" />						  	
			          </td>
			        </tr>				
			      </table>
	              <br><br>
	
		    <%
			    	out.print(htmlStreamBottom);
			    %> 
		    <%
 		    	if (!nuovoComputo) {
 		    %>    
		      <center>
		      	<table>
		      		<tr>
		      			<td align="center">
		      				<%
		      					operatoreInfo.showHTML(out);
		      				%>
		      			</td>
		      		</tr>
		      	</table>
		      </center>
		    <%
		    	}
		    %>	
		

<script language="JavaScript">

function gestisci_Protocollazione(){
		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
		var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil
											.protocollazioneLocale()%>;
		if (!protocolloLocale) return;
	 
		<%if (CODSTATOATTO.equals("NP")) {%>
			if (document.Frm1.CODSTATOATTO.value == "PR"){
			<%rowsProt = serviceResponse
								.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
						if (rowsProt != null && !rowsProt.isEmpty()) {
							rowProt = (SourceBean) rowsProt.elementAt(0);
							numProtV = SourceBeanUtils.getAttrBigDecimal(
									rowProt, "NUMPROTOCOLLO", null);
							numAnnoProtV = (BigDecimal) rowProt
									.getAttribute("NUMANNOPROT");
							dataOraProt = (String) rowProt
									.getAttribute("DATAORAPROT");
							oraProtV = dataOraProt.substring(11, 16);
							datProtV = dataOraProt.substring(0, 10);
						}%>
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
	
	function updateControllaStato() {
		var codStatoAtto = "<%=CODSTATOATTO%>";
	 	document.Frm1.CODSTATOATTO.value = codStatoAtto;
		if (document.Frm1.CODSTATOATTO.value == "NP"){
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
    	}
	}
		 
	function aggiornaDocumento(){

 		var f;
 		var codStatoAtto = "<%=CODSTATOATTO%>";

 		if (document.Frm1.CODSTATOATTO.value == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}

 		var esisteProvvjs = "<%=esisteProvv%>";
 		var conf_provv = "<%=s_conf_provvedimento%>";
 		var codStatoAttoProv = "<%=stattoDocProvv%>";
 		if (document.Frm1.CODSTATOATTO.value == "AN" && codStatoAttoProv != "AN" && conf_provv== "1" && esisteProvvjs > 0){
	 		alert("Per annullare la richiesta è necessario procedere all'annullamento del Provvedimento.");
			return false;
	 	}

	 	
	 	
	 	else {

 			<%if (nuovoComputo) {%>
 		 		controllaDatARilascio();
 		 	<%}%>
 		 	f = "AdapterHTTP?PAGE=CMComputoDettPage&aggiornaDoc=1";
 		 	f = f + "&CDNFUNZIONE=<%=_funzione%>";
		 	f = f + "&PRGRICHCOMPUTO=<%=PRGRICHCOMPUTO%>";
 			f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
 			f = f + "&prgAzienda=<%=prgAzienda%>";
 			f = f + "&prgUnita=<%=prgUnita%>";
 			f = f + "&CODSTATOATTO=" + document.Frm1.CODSTATOATTO.value;
 			f = f + "&NUMKLORICHCOMPUTO=<%=NUMKLORICHCOMPUTO%>";
 			f = f + "&oraProt=" +document.Frm1.oraProt.value;
 			f = f + "&dataProt="+document.Frm1.dataProt.value;
 			f = f + "&numProtocollo="+document.Frm1.numProtocollo.value;
 			f = f + "&numAnnoProt="+document.Frm1.numAnnoProt.value;
 			f = f + "&dataOraProt="+document.Frm1.dataOraProt.value;
 			f = f + "&codTipoDocumento=CMDCOMP";
 			<%if (nuovoComputo) {%>
 		 		f = f + "&DatAcqRil="+document.Frm1.DatAcqRil.value;
 				f = f + "&DatInizio="+document.Frm1.DatInizio.value;
 		 	<%} else {%>
 		 		f = f + "&DatAcqRil=<%=DatAcqRil%>";
 				f = f + "&DatInizio=<%=DatInizio%>";
 			<%}%>
 			f = f + "&tipoProt=S";
 			f = f + "&codAmbito=L68";
 			f = f + "&FlgCodMonoIO=<%=docInOut%>";
 			document.location = f;
	 	}
	 }
	 
	 function controllaDatARilascio(){
	 	if (document.Frm1.CODSTATOATTO.value == "NP"){
			document.Frm1.DatAcqRil.value = "<%=dataOdierna%>";
			document.Frm1.DatInizio.value = "<%=dataOdierna%>";
		}
		else {
			document.Frm1.DatAcqRil.value = "<%=datProtV%>";
			document.Frm1.DatInizio.value = "<%=datProtV%>";
		}
	}

</script>

<input type="hidden" name="dataOdierna" value="<%=dataOdierna%>"/>
<input type="hidden" name="DatAcqRil" value="<%=DatAcqRil%>" />
<input type="hidden" name="DatInizio" value="<%=DatInizio%>" />
			
</af:form>
<%
	}
%>



</body>
</html>
