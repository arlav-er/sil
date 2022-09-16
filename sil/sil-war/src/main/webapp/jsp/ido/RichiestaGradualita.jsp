<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;

  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata operatoreInfo = null;
  String NUMKLOGRADUALITA = "";

  String  strRagioneSocialeAz = null;
  String  strCodiceFiscaleAz = null;
  String  strPartitaIvaAz = null;


  String CODSTATORICHIESTA = "";
  String prgRichGradualita = "";
  String  prgAzienda = null;  
  String DatFine = "";
  String strNote = "";
        
  String DatRic = "";
  String DatPassaggio = "";
  BigDecimal NumLavDopPas = null;
  //BigDecimal NumDisDaAss = null;

  String htmlStreamTop2 = "";
  String htmlStreamBottom2 = "";

  boolean nuovaRichiesta = !(serviceRequest.getAttribute("nuovaRichiesta")==null || 
                            ((String)serviceRequest.getAttribute("nuovaRichiesta")).length()==0);
                            
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 


  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canModify = false;
  boolean canInsert = false; 
  boolean canSalvaStato = false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  } else {
  		canModify = attributi.containsButton("aggiorna");
  		canInsert = attributi.containsButton("INSERISCI"); 
  		canSalvaStato = attributi.containsButton("SALVA-STATO");
  }

  canInsert = canInsert && canSalvaStato;
  canModify = canModify && canSalvaStato;
  
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true"; 
  boolean disableCodRich = false || readOnlyStr;
  String fieldCodRich = disableCodRich ? "true" : "false";	

  String fromRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"fromRicerca");

  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  prgRichGradualita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgRichGradualita");

  String inserisciNewFromRicerca = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "inserisciNewFromRicerca");

  String listaProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "listaProv");
  String newDettProvPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "newDettProvPage");
  String inserisciDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "inserisciDettProv");  
  String eliminaDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "eliminaDettProv");    
  String dettaglioDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dettaglioDettProv");    
  String aggiornamentoDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "aggiornamentoDettProv");    

  String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");
  String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
  
//----------------------------------------------------------------------------------------------------
  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String prAutomatica     = "S";
  String docInOut         = "I";
  String docRif           = "Documentazione L68";
  String docTipo          = "Richiesta Gradualità";
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String dataOraProt      = "";
  String noButton = "false"; 
  String datProtV     = "";
  String oraProtV     = "";
  Vector rowsProt     = null;
  SourceBean rowProt  = null;

  Vector rowsDoc1     = null;
  SourceBean rowDoc1  = null;

  String CODSTATOATTO_P = null;

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

  boolean isInsert = (StringUtils.getAttributeStrNotNull(serviceRequest,"nuovo")).equals("true") ? true : false;

  if(!nuovaRichiesta){
	  rowDoc = (SourceBean) serviceResponse.getAttribute("M_Load_Doc_Grad.ROWS.ROW");
	  if (rowDoc != null) {
		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numAnnoProt", null);
	 	numProtV = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numProtocollo", null);
	 	dataOraProt = SourceBeanUtils.getAttrStrNotNull(rowDoc, "datProtocollo");
	 	CODSTATOATTO_P = SourceBeanUtils.getAttrStrNotNull(rowDoc, "CODSTATOATTO");
		if (!dataOraProt.equals("")) {
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
  		} 
	  }
  }else{
	  CODSTATOATTO_P = "NP";
  } 
  
//----------------------------------------------------------------------------------------------------	  
  
  if(nuovaRichiesta){ 
	  
	  //prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	  strRagioneSocialeAz = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strRagioneSociale");
	  strCodiceFiscaleAz = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strCodiceFiscale");
	  strPartitaIvaAz = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRPARTITAIVA");
	  CODSTATORICHIESTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODSTATORICHIESTA");
      DatFine = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATFINE");
      strNote = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strNote");
      DatRic = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DatRic");
      DatPassaggio = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DatPassaggio");
	  NumLavDopPas = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "NumLavDopPas", null);

	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  
	  if(!prgAzienda.equals("") && prgAzienda != null){
		  SourceBean rowAz = null;
		  Vector rowsAz= serviceResponse.getAttributeAsVector("M_Info_Azienda.ROWS.ROW");
		  if (rowsAz.size()==1) {
		        rowAz = (SourceBean)rowsAz.get(0);
				
			    strRagioneSocialeAz = Utils.notNull(rowAz.getAttribute("RagioneSociale"));		    
			    strCodiceFiscaleAz = Utils.notNull(rowAz.getAttribute("CodiceFiscale")); 
			    strPartitaIvaAz = Utils.notNull(rowAz.getAttribute("PIVA"));  
		  }	  	
	  }
	
  }else{
	          
	  SourceBean row = null;
	  Vector rows= serviceResponse.getAttributeAsVector("M_Load_RichGrad.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
			
			prgRichGradualita = Utils.notNull(row.getAttribute("prgRichGradualita"));
		    prgAzienda = Utils.notNull(row.getAttribute("prgAzienda")); 
		    
		    CODSTATORICHIESTA = Utils.notNull(row.getAttribute("CODSTATORICHIESTA"));  
		    DatFine = Utils.notNull(row.getAttribute("DATFINE"));  
		    strNote = Utils.notNull(row.getAttribute("strNote")); 
	        
	        DatRic = Utils.notNull(row.getAttribute("DatRic"));
	        DatPassaggio = Utils.notNull(row.getAttribute("DatPassaggio"));
	        NumLavDopPas = SourceBeanUtils.getAttrBigDecimal(row, "NumLavDopPas", null);
	        //NumDisDaAss = SourceBeanUtils.getAttrBigDecimal(row, "NumDisDaAss", null);
	        
        	NUMKLOGRADUALITA = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOGRADUALITA")).intValue());
	        
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");
		    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");		    	    
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

			if(!prgAzienda.equals("") && prgAzienda != null){
			    SourceBean rowAzienda = null;
				Vector rowsAzienda= serviceResponse.getAttributeAsVector("M_Info_Azienda.ROWS.ROW");
				if (rowsAzienda.size()==1) {
				     rowAzienda = (SourceBean)rowsAzienda.get(0);
						
				     strRagioneSocialeAz = Utils.notNull(rowAzienda.getAttribute("RagioneSociale"));		    
				     strCodiceFiscaleAz = Utils.notNull(rowAzienda.getAttribute("CodiceFiscale")); 
				     strPartitaIvaAz = Utils.notNull(rowAzienda.getAttribute("PIVA"));  
				}	  	
			}		   
	  }

  }  
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE"));




  String statoRichiesta = null;
  SourceBean row2 = null;
  if(!nuovaRichiesta){
	  Vector rows2= serviceResponse.getAttributeAsVector("M_Load_RichGrad.ROWS.ROW");
	  if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);			
		    statoRichiesta = Utils.notNull(row2.getAttribute("CODSTATORICHIESTA")); 
	  }
	  if(statoRichiesta.equals("AN") || CODSTATOATTO_P.equals("PR")) {
	  	fieldReadOnly = "true";
	  	if(statoRichiesta.equals("AN")) {		
	  		canModify = false;
	  		canInsert = false;
	  		noButton = "true";
	  		fieldCodRich = "true";
	  	}
	  }
  } else {
		noButton = "true";
		fieldCodRich = "true";
  }

    
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>
 
<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title> Richiesta Gradualità </title>
    <script language="Javascript">
    
	function selezionaAzienda()
    {
		var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDatiAzienda&cdnFunzione=<%=_funzione%>";
		var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
				"width=600,height=500,top=50,left=100";
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
    }
    
    function riempiDatiAzienda(prgAzienda, aziCodFiscale, aziPIva, aziRagSociale) {

		document.Frm1.prgAzienda.value = prgAzienda;
		document.Frm1.aziCodFiscale.value = aziCodFiscale;
		document.Frm1.aziPIva.value = aziPIva;
		document.Frm1.aziRagSociale.value = aziRagSociale;
		opened.close();
	}
		
	function controllaDatiAzienda_onSubmit() {	
		if (document.Frm1.prgAzienda.value == "") { 
			alert("Caricare i dati relativi all'azienda!");
			return false;
		}
		return true;
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
			String token = "_TOKEN_" + goBackListPage;
			String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());				
      		if(goBackListPage.equals("CMRichGradListaPage")){
      			goBackButtonTitle = "Torna alla lista";
      			%>
      			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
      			<%
      		}else{
      			goBackButtonTitle = "Torna alla ricerca";
      			%>
      			f = "AdapterHTTP?PAGE=CMRichGradRicercaPage";
		 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
				f = f + "&PRGRICHGRADUALITA=<%=prgRichGradualita%>";      			
      			document.location = f;
      			<%
      		}
			%>
						
	 }

	 function aggiornaDocumento(){
		var f; 
 		 
 		var codStatoAttoInReq = "<%=CODSTATOATTO_P%>";
 		var codStatoAtto = document.Frm1.CODSTATOATTO_P.value;

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
			  		 
			f = "AdapterHTTP?PAGE=CMRichGradPage&aggiornaDoc=1";
			f = f + "&CDNFUNZIONE=<%=_funzione%>";
			f = f + "&PRGRICHGRADUALITA=<%=prgRichGradualita%>";
			f = f + "&CODSTATOATTO_P=" + codStatoAtto;
			f = f + "&numAnnoProt=" + numAnnoProt;		  			  
			f = f + "&numProtocollo=" + numProtocollo;		  			  
			f = f + "&dataOraProt=" + dataOraProt;
			f = f + "&CODSTATORICHIESTA=<%=CODSTATORICHIESTA%>";
			f = f + "&goBackListPage=<%=goBackListPage%>";
			f = f + "&prgAzienda=<%=prgAzienda%>";
			f = f + "&codTipoDocumento=" + codTipoDocumento;
			f = f + "&tipoProt=" + tipoProt;
			f = f + "&FlgcodMonoIO=" + FlgcodMonoIO;
			f = f + "&codAmbito=" + codAmbito;
			
			if(codStatoAtto == "AN"){
				f = f + "&annullamento=1";
				f = f + "&NUMKLOGRADUALITA=<%=NUMKLOGRADUALITA%>";
			} else {
				f = f + "&annullamento=0";
			}
			 		  			  
			document.location = f;
		}
	 }

	 function annullamentoDoc(){
	 	var codStatoAtto = "<%=CODSTATOATTO_P%>";
	 	document.Frm1.CODSTATOATTO_P.value = codStatoAtto;

	 	var statoRich = document.Frm1.CODSTATORICHIESTA.value;
	 	if(statoRich == "AN"){
	 		document.Frm1.annullamento.value = 1;
	 	}
	 }
	 
    </script>
	
	<script language="Javascript">
	
	<% if(prgUnita.equals("")) {
	   		SourceBean rowPrg = (SourceBean) serviceResponse.getAttribute("M_GetPrgUnita.ROWS.ROW");
 	   		if(rowPrg != null) prgUnita = SourceBeanUtils.getAttrStrNotNull(rowPrg, "prgUnita");
 	   	}
 	   	
 	   	if (prgAzienda!=null && (prgUnita!=null && !prgUnita.equals("")) ) { %>
			window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<% } %>
	
	</script>

  </head>
 
  <body class="gestione">
  
    <p class="titolo"> Richiesta Gradualità </p>
  
  	<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaDatiAzienda_onSubmit()" >
  	
  	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
  	<input type="hidden" name="PAGE" value="CMRichGradPage" />
  	<input type="hidden" name="nuovo" value="false" />
  	<input type="hidden" name="salva" value="true" />
  	
  	
    <div id="divLookAzi_look" style="display:">
<%
  //Oggetti per l'applicazione dello stile
  htmlStreamTop = StyleUtils.roundTopTable(isInsert);
  htmlStreamBottom = StyleUtils.roundBottomTable(isInsert);
%>
		<span class="sezioneMinimale">
			Azienda&nbsp;&nbsp;
			<% if (prgAzienda == "" ) { %>
			<a href="#" onClick="selezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
			<% } %>&nbsp;*
		</span>
		<%= htmlStreamTop %>
		<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
		<table class="main" width="100%">
			<tr valign="top">
				<td class="etichetta">Codice Fiscale</td>
				<td class="campo">
					<input type="text" name="aziCodFiscale" class="inputView" readonly="true" value="<%=strCodiceFiscaleAz%>" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="aziPIva" class="inputView" readonly="true" value="<%=strPartitaIvaAz%>" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="aziRagSociale" class="inputView" readonly="true" value="<%=strRagioneSocialeAz%>" size="75" maxlength="120"/>
				</td>
			</tr>
		</table>
		<%= htmlStreamBottom %>
	</div>
<%
  htmlStreamTop2 = StyleUtils.roundTopTable(isInsert || fieldReadOnly.equals("false"));
  htmlStreamBottom2 = StyleUtils.roundBottomTable(isInsert || fieldReadOnly.equals("false"));
%>	
	<af:showErrors/>
    <af:showMessages prefix="M_Insert_RichGrad"/>
    <af:showMessages prefix="M_Save_RichGrad"/>
    <af:showMessages prefix="SalvaRichGradDoc"/>
    <af:showMessages prefix="M_Annullamento_RichGrad_E_Doc"/>
	<%= htmlStreamTop2 %>
	<%@ include file="_protocollazionerichiesta.inc" %>

	<script>	
	 
	 function gestione_Protocollazione() {
	<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
	var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
	if (!protocolloLocale) return;

		<% if(!nuovaRichiesta && CODSTATOATTO_P.equals("NP")) { %>
		if (document.Frm1.CODSTATOATTO_P.value == "PR"){
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
	  	if (document.Frm1.CODSTATOATTO_P.value == "AN" || document.Frm1.CODSTATOATTO_P.value == "NP") {
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
		}
		<%}%>		 	
	 }
	 
    </script>

    <table class="main" width="100%">

<%-- ********************************************************* --%>
    	<tr>
	        <td class="etichetta"> Stato richiesta </td>
        	<td class="campo" colspan="3">
		        <af:comboBox title="Stato richiesta" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_GRAD" addBlank="false" selectedValue="<%=CODSTATORICHIESTA%>" disabled="<%= fieldCodRich %>" classNameBase="input"/>&nbsp;*&nbsp;
		        <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODSTATORICHIESTA')"; </SCRIPT>
        	</td>      	
    	</tr>
<%-- ********************************************************* --%>

    	 
    	<tr>
	        <td class="etichetta"> Data richiesta </td>
        	<td class="campo" colspan="3">
            	<af:textBox type="date" name="DatRic" value="<%=DatRic%>"
            			title="Data Richiesta"  classNameBase="input"
            			size="12" maxlength="10" readonly="<%=fieldReadOnly%>" validateOnPost="true"/>
        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Data passaggio&nbsp;&nbsp;
             	<af:textBox type="date" name="DatPassaggio" value="<%=DatPassaggio%>"
						title="Data Passaggio" classNameBase="input" size="12" maxlength="10"
						validateOnPost="true" readonly="<%=fieldReadOnly%>" />
        	</td>      	
    	</tr> 
    	<tr>
	        <td class="etichetta"> Num. Lav. assunti dopo passaggio </td>
        	<td class="campo">
        		<af:textBox type="integer" name="NumLavDopPas" value="<%=Utils.notNull(NumLavDopPas)%>"
					title="Num. Lav. assunti dopo passaggio" validateOnPost="true"
					classNameBase="input" size="5" maxlength="4" readonly="<%=fieldReadOnly%>" />
        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Data fine&nbsp;&nbsp; 
        		<af:textBox type="date" name="DatFine" value="<%=DatFine%>"
						title="Data Fine" classNameBase="input" size="12" maxlength="10"
						validateOnPost="true" readonly="<%=fieldReadOnly%>" />
        	</td>         	
    	</tr>
    	<tr>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>		
		</tr>
	    <tr>
        	<td class="etichetta"> Note </td>
        	<td class="campo" colspan="3">
            	<af:textArea name="strNote"
						maxlength="1000" classNameBase="textarea" 
						title="Note" value="<%=strNote%>"
						readonly="<%=fieldReadOnly%>" />
        	</td>
    	</tr>
	</table>
	<table> 
		<tr>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>		
		</tr>
		<input type="hidden" name="PRGRICHGRADUALITA" value="<%=prgRichGradualita%>" />
		<input type="hidden" name="strChiaveTabella" value="<%=prgRichGradualita%>" />
		<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
		<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
		<%if (canInsert) {%>
		<%  if(nuovaRichiesta){%> 
			<tr>
				<td>
					<input type="submit" name="Inserisci" class="pulsanti" value="Inserisci"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
					<input type="hidden" name="inserisciNew" value="1">
					<input type="hidden" name="NUMKLOGRADUALITA" value="1">
			</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>		
			</tr>
		<% 	} %> 
		<%}   %>
		<%if (canModify) {%>
		<%  if(!nuovaRichiesta){ %> 
			<tr>
				<td>
  					<%if (!CODSTATOATTO_P.equals("AN")) {%>
					<input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna" onClick="annullamentoDoc()"/>
					&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
					<input type="hidden" name="annullamento" value="0">
					<input type="hidden" name="aggiornamento" value="1">
					<input type="hidden" name="NUMKLOGRADUALITA" value="<%= NUMKLOGRADUALITA %>">
					<%}%>
				</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>		
			</tr>
		<% 	} %> 
		<%}   %>
		<tr>
			<td>
				<center>
					<input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="goBack()" />
				</center>
			</td>
		</tr>
	</table>
	<br><br>
	<%= htmlStreamBottom2 %>
	<%if(!nuovaRichiesta){%>    
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
    </af:form>  
 </body>
</html>