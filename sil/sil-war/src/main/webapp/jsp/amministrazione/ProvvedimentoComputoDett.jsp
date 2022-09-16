<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.text.SimpleDateFormat,
                  it.eng.sil.module.movimenti.*,
                  java.text.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  it.eng.sil.util.GiorniNL,
                  it.eng.sil.module.agenda.*,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;
  //togliere aaa
  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  
  Testata operatoreInfo = null;
  String NUMKLORICHCOMPUTO = null;

  String  STRRAGIONESOCIALE = "";
  String  STRCODICEFISCALE = "";
  String  STRPARTITAIVA = "";

  String  PRGRICHCOMPUTO = null;  
  String  prgAzienda = null; 
  String  DATINIZIOCOMP = null;
  String  DATASSUNZIONE = null;
  String  CODMONOCATEGORIA = "";
  String  DECORESETTIMANALI = "";
  String  DECORETOTALI = "";
  String  CODMOTCOMPUTO = "";
  String  PRGMOVIMENTO = "";
  String  datInizioMov = "";  	  
  String  codStatoAttoMov = ""; 
  String  codFiscaleAzienda = "";
  String  codFiscaleLavoratore = ""; 
  
  //Configurazione
  SourceBean sb_provvedimento = Utils.getConfigValue("CMCOMP");
  String s_conf_provvedimento = (String) sb_provvedimento.getAttribute("row.num");
  
  
  BigDecimal numAnnoProt = null;
  BigDecimal numProtocollo = null;
  String dataProt = "";
  
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
  
 

  
  boolean nuovoProvv =   Boolean.parseBoolean(StringUtils.getAttributeStrNotNull(serviceRequest, "nuovo"));
  
  
  //Profilatura
                            
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  PageAttribs attributi = new PageAttribs(user, _page); 
  boolean canModify = attributi.containsButton("AGGIORNA");
  boolean canDelete = attributi.containsButton("RIMUOVI");
  boolean canInsert = attributi.containsButton("INSERISCI"); 
  boolean canSalvaStato = attributi.containsButton("SALVA-STATO");
  //boolean canProvvedimento = attributi.containsButton("ProvvedComputo");
  
  //boolean canModifyAzLav = true;

  //canInsert = canInsert && canSalvaStato;
  //canModify = canModify && canSalvaStato;  
  boolean StatoModify = true;  
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true";
  
  boolean onlreadProvv = false;  
  String onlyread = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "onlyread");
  if("true".equals(onlyread)){
	  fieldReadOnly = "true";
	  onlreadProvv = true;
  }
  
  
  
  //dati lavoratore
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  
  //Azienda
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
  prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
  
  
  // Rua
  

		     
  
  String goBackPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackPage");
  String tipoMenu = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoMenu");
  
  //Attribute
  
    

  PRGRICHCOMPUTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgrichcomputo");
  String cdnutins = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnutins");
  String dtmins = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dtmins");  
  String cdnutmod = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnutmod");    
  String dtmmod = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dtmmod");
  String numkloprovvcomputo = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "numkloprovvcomputo");
  String datProvvedimento = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "datprovvedimento");
  String note = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "strnote");   


  


  
//----------------------------------------------------------------------------------------------------
  	
  String DatAcqRil = "";
  String DatInizio = ""; 
  
  DatAcqRil = dataOdierna;
  DatInizio = dataOdierna;

  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String prAutomatica     = "S";
  String docInOut         = "O";
  String docRif           = "Documentazione L68";
  String docTipo          = "L68 Provvedimento del Computo";
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String dataOraProt      = "";
  boolean noButton = false;
  String datProtV     = "";
  String oraProtV     = "";
  Vector rowsProt     = null;
  SourceBean rowProt  = null;

  Vector rowsDoc1     = null;
  SourceBean rowDoc1  = null;

  String codStatoAttoV = "";
  String CODSTATOATTO = "NP";
  
 	boolean isInsert = (StringUtils.getAttributeStrNotNull(serviceRequest,"nuovo")).equals("true") ? true : false;

  //if(!nuovoComputo){
  //	canModifyAzLav = true;
  //}
  
  	
  if(!nuovoProvv){
  	  //CODSTATOATTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODSTATOATTO");
	  SourceBean rowIns = null;
	  Vector rowsIns= serviceResponse.getAttributeAsVector("M_Load_Doc_Provvedimento.ROWS.ROW");	  
	  if (rowsIns.size()==1) {
		  isInsert=true;
	  }
 	
 		if(rowsIns != null && !rowsIns.isEmpty() ){
 			rowProt = (SourceBean) rowsIns.firstElement();
 			CODSTATOATTO = StringUtils.getAttributeStrNotNull(
				rowProt, "CODSTATOATTO");
 			if(CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN") ) {
 				numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
	   			numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "numAnnoProt", null); 
	   			dataOraProt = StringUtils.getAttributeStrNotNull(rowProt,"DATPROTOCOLLO");
	 			if (!dataOraProt.equals("")) {
	 				oraProtV = dataOraProt.substring(11,16);
	 				datProtV = dataOraProt.substring(0,10);
	 			}
	 		}
	 	
	 		DatAcqRil = SourceBeanUtils.getAttrStrNotNull(rowProt, "DatAcqRil");
			DatInizio = SourceBeanUtils.getAttrStrNotNull(rowProt, "DatInizio");
		
		}
  }

//----------------------------------------------------------------------------------------------------	  

  if(nuovoProvv){ 
	  
	  prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	  strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
	  strNomeLav = StringUtils.getAttributeStrNotNull(serviceRequest, "nome");
	  strCognomeLav = StringUtils.getAttributeStrNotNull(serviceRequest, "cognome");
 	  
	  datProvvedimento = dataOdierna;
	  
	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  
	  cdnutins = cdnUtIns.toString();
	  cdnutmod = cdnUtMod.toString();
	  dtmins = dataOdierna;
	  dtmmod = dataOdierna;
	  
  }else{
	          
	  SourceBean row = null;
	  Vector rows= serviceResponse.getAttributeAsVector("M_Load_Provvedimento.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
	        
			//PRGRICHCOMPUTO = Utils.notNull(row.getAttribute("PRGRICHCOMPUTO"));
		    //prgAzienda = Utils.notNull(row.getAttribute("prgAzienda")); 
		    //cdnLavoratore = Utils.notNull(row.getAttribute("CDNLAVORATORE")); 
		    
		    datProvvedimento = Utils.notNull(row.getAttribute("datprovvedimento"));	  		
		    cdnutins = Utils.notNull(row.getAttribute("cdnutins"));	    
		    dtmins = Utils.notNull(row.getAttribute("dtmins")); 
		    
		    cdnutmod = Utils.notNull(row.getAttribute("cdnutmod")); 
		    dtmmod = Utils.notNull(row.getAttribute("dtmmod"));
		    note = Utils.notNull(row.getAttribute("STRNOTE"));        
		    numkloprovvcomputo = String.valueOf(((BigDecimal)row.getAttribute("numkloprovvcomputo")).intValue());        
        	
        	        
        	/*cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");
		    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");		    	    
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);*/
		    operatoreInfo = new Testata(cdnutins, dtmins, cdnutmod, dtmmod);
	  }

  }  
  
 
  // Dati Azienda
  
  
  String strFlgCfOk = "";
  String strFlgDatiOk = "";
  String IndirizzoAzienda = "";
  String descrTipoAz = "";
  String codTipoAz = "";
  String codnatGiurAz = "";
  
  if (!prgAzienda.equals("") && !prgUnita.equals("")){
    InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
    STRRAGIONESOCIALE = currAz.getRagioneSociale();
    STRPARTITAIVA = currAz.getPIva();
    STRCODICEFISCALE = currAz.getCodiceFiscale();
    IndirizzoAzienda = currAz.getIndirizzo();
    descrTipoAz = currAz.getDescrTipoAz();
    codTipoAz = currAz.getTipoAz();
    codnatGiurAz = currAz.getCodNatGiurAz();
    strFlgDatiOk = currAz.getFlgDatiOk();
    if (strFlgDatiOk!=null) {
        if (strFlgDatiOk.equalsIgnoreCase("S")){
          strFlgDatiOk = "Si";
        }else
            if (strFlgDatiOk.equalsIgnoreCase("N")){
              strFlgDatiOk = "No";
            }
    }
  }
  
  //Dati lavoratore
  if (!cdnLavoratore.equals("")) {
    //Oggetto per la generazione delle informazioni sul lavoratore
    InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
    strCodiceFiscaleLav = datiLav.getCodFisc();
    strNomeLav = datiLav.getNome();
    strCognomeLav = datiLav.getCognome();
    strFlgCfOk = datiLav.getFlgCfOk();
    if (strFlgCfOk!=null){
       if (strFlgCfOk.equalsIgnoreCase("S")) {
       	  strFlgCfOk = "Si";
       }
       else if (strFlgCfOk.equalsIgnoreCase("N")){
          strFlgCfOk = "No";
       }
    }
  }





  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE"));




  String statoAtto = "";
  SourceBean row2 = null;
  if(!nuovoProvv){
	  Vector rows2= serviceResponse.getAttributeAsVector("M_Load_Doc_Provvedimento.ROWS.ROW"); 
	  if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);			
		    statoAtto = Utils.notNull(row2.getAttribute("CODSTATOATTO")); 
	  }
	  if(statoAtto.equals("AN")){
	  		canModify = false;
	  		fieldReadOnly = "true";
	  		canDelete = false;
	  		canInsert = false;
	  		noButton = true;
	  		StatoModify = false;
	  }
	  if(statoAtto.equals("PR")){
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
	

    
	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
          }

          <% boolean baseComputoChanged = false; %> 
          function baseComputoChanged() {
              <% baseComputoChanged = true; %>
          }
          
          <% boolean percEsoneroChanged = false; %>
          function percEsoneroChanged() {
              <% percEsoneroChanged = true; %>
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
			<%
			String goBackButtonTitle = "";
			boolean goBackIsInList = false;
			%>
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			<%
			// Recupero l'eventuale URL generato dalla LISTA precedente
			String token = "_TOKEN_" + goBackPage;
			//String tipoMenu = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoMenu");
			String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());		
			
	      	if(goBackPage.equals("CMComputoDettPage")){
	      		goBackButtonTitle = "Torna al Dettaglio del Computo";
	      		%>
	      		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	      		<%
	      	}else{
	      		goBackButtonTitle = "Torna al Dettaglio del Computo";
	      		%>
	      		f = "AdapterHTTP?PAGE=CMComputoDettPage";
				f = f + "&CDNFUNZIONE="<%=_funzione%>";
				f = f + "&PRGRICHCOMPUTO="<%=PRGRICHCOMPUTO%>";
				f = f + "&tipoMenu="<%=tipoMenu%>";
				<%if(tipoMenu.equals("azienda")){%>
					f = f + "&PRGAZIENDA="<%=prgAzienda%>";
					f = f + "&PRGUNITA="<%=prgUnita%>";
				<%}else if(tipoMenu.equals("lavoratore")){%>
					f = f + "&CDNLAVORATORE="<%=cdnLavoratore%>";
	      		<%}%>
	      		document.location = f;
	      		<%
	      	}
			%>
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


		 
		 
	</script>

	<script language="Javascript" src="../../js/docAssocia.js"></script>
		
</head>


<body class="gestione" onload="rinfresca();">

		<% if(nuovoProvv){ %>
	    	<p class="titolo">Nuovo Provvedimento</p>
		<%}else{%>
	    	<p class="titolo"> Provvedimento</p>
		<%}%>
	
		<af:form method="POST" action="AdapterHTTP" name="Frm1" >
		
			<input type="hidden" name="PAGE" value="ProvvedimentoPage" />		
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>				
			<input type="hidden" name="CDNUTINS" value="<%=cdnutins%>">
			<input type="hidden" name="CDNUTMOD" value="<%=cdnutmod%>">
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
			<input type="hidden" name="PRGRICHCOMPUTO" value="<%=PRGRICHCOMPUTO%>">
			<input type="hidden" name="goBackListPage" value="<%=goBackPage%>" />
			<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" >
			<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
			<input type="hidden" name="PRGMOVIMENTO" value="<%=PRGMOVIMENTO%>">	
			<input type="hidden" name="tipoMenu" value="<%=tipoMenu%>">
			<input type="hidden" name="CODMONOCATEGORIA" value="D">
			<input type="hidden" name="dtmins" value="<%=dtmins%>">
			<input type="hidden" name="dtmmod" value="<%=dtmmod%>">
			
			
			<input type="hidden" name="strChiaveTabella" value="<%=PRGRICHCOMPUTO%>">
		
			
			

			
				
			<%-- Azienda --%>
		    <div id="divLookAzi_look" style="display:">
				<span class="sezioneMinimale">
					Azienda&nbsp;
					<% if((canModify || nuovoProvv) && !tipoMenu.equals("azienda")){%>
					<!--  &nbsp;
					<a href="#" onClick="dissociaAziendaLav('Aziende', 'aggiornaAzienda');"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;*-->
					<% } %>
				</span>
				<%= htmlStreamTop %>
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
				<%= htmlStreamBottom %>
			</div>
			
			
			<%-- Lavoratore --%>
		    <div id="divLookLav_look" style="display:">
				<span class="sezioneMinimale">
					Lavoratore&nbsp;
				    <% if((canModify || nuovoProvv) && !tipoMenu.equals("lavoratore")) { %>				     
				    	<!-- &nbsp;<a href="#" onClick="javascript:dissociaAziendaLav('Lavoratori', 'aggiornaLavoratore');"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;* -->
				    <%}%>
				 </span>
				<%= htmlStreamTop %>
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
				<%= htmlStreamBottom %>
			</div>
			
			<af:showMessages prefix="M_Insert_Provvedimento"/>
			<af:showMessages prefix="M_Save_Provvedimento"/>	
			<af:showMessages prefix="SalvaRichProvDoc"/>	
			<af:showErrors />		
	
			<%= htmlStreamTop %>
				<%@ include file="_protocollazioneProvvedimento.inc" %>
				<table class="main" border="0">
		
			        <tr><td colspan="2">&nbsp;</td></tr>	      
					<tr>
					    <td class="etichetta">Data Provvedimento&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data Provvedimento" validateOnPost="true" disabled="false" type="date" name="datProvvedimento" value="<%=datProvvedimento%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('Data Provvedimento')"; </SCRIPT>
					   </td>
					</tr>	
							          	        	        
<!-- 				<tr>
					    <td class="etichetta">Utenza di Inserimento</td>
					    <td class="campo">
					            <af:textBox title="Utenza di Inserimento" validateOnPost="true" disabled="false" type="text" name="cdnutins" value="<%=cdnutins%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('cdnutins')"; </SCRIPT>
					    </td>
					</tr>
					<tr>
					    <td class="etichetta">Data Inserimento&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data Inserimento" validateOnPost="true" disabled="false" type="date" name="dtmins" value="<%=dtmins%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('dtmins')"; </SCRIPT>
					   </td>
					</tr>
					
					<tr>
					    <td class="etichetta">Utenza di Modifica</td>
					    <td class="campo">
					            <af:textBox title="Utenza di Modifica" validateOnPost="true" disabled="false" type="text"  name="cdnutmod" value="<%=cdnutmod%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('cdnutmod')"; </SCRIPT>
					    </td>
					</tr>
					<tr>
					    <td class="etichetta">Data Modifica&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data Modifica" validateOnPost="true" disabled="false" type="date" name="dtmmod" value="<%=dtmmod%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('dtmmod')"; </SCRIPT>
					   </td>
					</tr>  -->
					
					
					
					
					<tr>
					    <td class="etichetta">Note</td>
					    <td class="campo">
					          	<af:textArea cols="70" rows="4" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
              		 validateOnPost="true" required="false" disabled="false" title="Note" name="strnote" value="<%=note%>"  />
					      <%if(canModify){%>
					      &nbsp;*&nbsp;
					      <%}%>
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('note')"; </SCRIPT>
					    </td>
					</tr>
					
			
					
					
					
					
					
					<tr>
			          <td colspan="2">&nbsp;</td>
			        </tr> 					
					
					<tr>
			          <td colspan="2">&nbsp;</td>
			        </tr>  				
			        <tr>
			          <td colspan="2">&nbsp;</td>
			        </tr>  				
			        <tr>
			          <td colspan="2" align="center">
			          	  <% if(!onlreadProvv){
			          	  if(nuovoProvv){
			          	  %>
				          	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onClick="controllaDatARilascio()">
				          	&nbsp;&nbsp;    	          	
				         
				          	<input type="hidden" name="insDoc" value="0">
				          	<input type="hidden" name="inserisciNew" value="1">
				          	<input type="hidden" name="numkloprovvcomputo" value="1">
			          	  <%
			          	  }else{
			          	  %>
			          	  	<% if(!statoAtto.equals("AN")){%>
			          	  	
			          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="updateControllaStato()">
				         
			          	  	
			          	  	<input type="hidden" name="upDoc" value="0">	          	  		
			          	  	<input type="hidden" name="aggiornamento" value="1">
			          	  	<input type="hidden" name="numkloprovvcomputo" value="<%=NUMKLORICHCOMPUTO%>">
			          	  	<input type="hidden" name="annullamentoDocFromRich" value="0">
						  <%}}}%>
						  
			          	  <input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="tornaDettComputo()" />
			          	 
			          	  							  	
			          </td>
			        </tr>				
			      </table>
	              <br><br>
	
		    <%out.print(htmlStreamBottom);%> 
		    <%
		    if(!nuovoProvv){%>    
		      <center>
		      	<table>
		      		<tr>
		      			<td align="center">
		      				<% operatoreInfo.showHTML(out); %>
		      			</td>
		      		</tr>
		      	</table>
		      </center>
		    <%}%>	
		

<script language="JavaScript">

function gestisci_Protocollazione(){
		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
		var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()%>;
		if (!protocolloLocale) return;
	 
		<% if(CODSTATOATTO.equals("NP")) { %>
			if (document.Frm1.CODSTATOATTO.value == "PR"){
			<%  
			rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	  		if(rowsProt != null && !rowsProt.isEmpty()) { 
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

	  function tornaDettComputo() {  
		    
		  	url =  "AdapterHTTP?PAGE=CMComputoDettPage";		
	 		url += "&CDNFUNZIONE=<%=_funzione%>";		 	
	 		url += "&CDNLAVORATORE=<%=cdnLavoratore%>";		
	 		url += "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value;
	 		url += "&PRGRICHCOMPUTO=<%=PRGRICHCOMPUTO%>";
	 		url += "&tipoMenu=<%=tipoMenu%>";
	 		url += "&PRGUNITA=<%=prgUnita%>";				 						 		
	 		url += "&nuovo="+false;
	 		url += "&salva="+true;
			url += "&GOBACKLISTPAGE=CMComputiListaPage";	        
	 		setWindowLocation(url);	 	


     }

	 
	function aggiornaDocumento(){

 		var f;
 		var codStatoAtto = "<%=CODSTATOATTO%>";

 		if (document.Frm1.CODSTATOATTO.value == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}
	 	
	 	else {

 			<% if (nuovoProvv) { %>
 		 		controllaDatARilascio();
 		 	<%}%>
 		 	f = "AdapterHTTP?PAGE=ProvvedimentoPage&aggiornaDoc=1";
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
 			f = f + "&codTipoDocumento=CMPCOMP";
 			<% if (nuovoProvv) { %>
 		 		f = f + "&DatAcqRil="+document.Frm1.DatAcqRil.value;
 				f = f + "&DatInizio="+document.Frm1.DatInizio.value;
 		 	<%} else {%>
 		 		f = f + "&DatAcqRil=<%=DatAcqRil%>";
 				f = f + "&DatInizio=<%=DatInizio%>";
 			<% } %>
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
	
			<input type="hidden" name="DatAcqRil" value="<%=DatAcqRil%>" />
			<input type="hidden" name="DatInizio" value="<%=DatInizio%>" />			
			<input type="hidden" name="dataOdierna" value="<%=dataOdierna%>"/>

</af:form>




</body>
</html>
