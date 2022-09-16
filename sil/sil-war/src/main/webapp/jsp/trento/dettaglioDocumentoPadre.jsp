<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			com.engiweb.framework.util.JavaScript,
			java.text.SimpleDateFormat,
			java.util.*,
			java.io.*,
			java.math.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.bean.*,
			it.eng.afExt.utils.*,
			it.eng.sil.Values,
			it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil,
			it.eng.sil.module.movimenti.constant.Properties,
			it.eng.sil.module.trento.Consenso"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
	
	<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
String pageToProfile = "ListaStampeParLavPage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

boolean protocollazione = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("STAMPA"));
boolean visualizzaStampa = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("VISUALIZZA"));

boolean checkProtocollaDocumento = serviceResponse.containsAttribute("MProtocollaDocPadre.ECCEZIONEPROTOCOLLA");
String codiceErroreProtocolla = "";
if (checkProtocollaDocumento){
	codiceErroreProtocolla = serviceResponse.getAttribute("MProtocollaDocPadre.ECCEZIONEPROTOCOLLA").toString();
}

boolean checkWarningReport = serviceResponse.containsAttribute("MProtocollaDocPadre.WARNINGREPORT");
ArrayList codiciWarnings = null;
String codiceWarningsString = "";
if (checkWarningReport){
	codiciWarnings = (ArrayList) serviceResponse.getAttribute("MProtocollaDocPadre.WARNINGREPORT");
	codiceWarningsString = codiciWarnings.toString();
}


  String strChiaveTabella = "";
  
  String prAutomatica     = null; 
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  BigDecimal kLockProt    = null;
  String dataOraProt = null;
  String oraProt  = null;
  String dataProt = null;

  String pageDocAssociata = (String) serviceRequest.getAttribute("pageDocAssociata");
  String trasferisci = (String) serviceRequest.getAttribute("trasferisci");
  
  boolean isNewDoc = false;
  SourceBean activeModule = (SourceBean) serviceResponse.getAttribute("DettaglioDocumentoStampaParam");
  
  
  String descTemplate= (String) activeModule.getAttribute("ROWS.ROW.STRDESCRIZIONE");
  Documento doc = (Documento) activeModule.getAttribute("Documento");
  // Info operatore
  BigDecimal cdnUtins = doc.getCdnUtIns();
  String     dtmins   = doc.getDtmIns();
  BigDecimal cdnUtmod = doc.getCdnUtMod();
  String     dtmmod   = doc.getDtmMod();
  String codIO = Utils.notNull(doc.getCodMonoIO());
  String codMotAnnullamentoAtto = doc.getCodMotAnnullamentoAtto();
  
  String codStatoAtto = doc.getCodStatoAtto();
  
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  // NB: leggo i dati da "activeModule" e non dalla request!
  int     cdnfunzione    = SourceBeanUtils.getAttrInt(activeModule, "cdnfunzione");
  String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(activeModule, "cdnLavoratore");
  String prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
  String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
  String  dominioDati  = "";
  if (StringUtils.isFilled(cdnLavoratore)) {
	  dominioDati  = "DL";
  }else if (StringUtils.isFilled(prgAzienda)) {
	  dominioDati  = "DA";
	  
  }
  
  String codMinSap = "";
  Vector vectCodSap = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
  if ((vectCodSap != null) && (vectCodSap.size() > 0)) {
	SourceBean beanLastInsert = (SourceBean) vectCodSap.get(0);
	codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
  }
  
  strChiaveTabella = Utils.notNull(doc.getStrChiaveTabella());
  
  String _page = "DettaglioDocumentoPadreStampParamPage";
  String queryString = "&CDNLAVORATORE="+cdnLavoratore+"&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&DOMINIO="+dominioDati+"&cdnFunzione="+cdnfunzione + "&page="+_page + 
		  	"&CODCPI=" + doc.getCodCpi() + "&PRGDOCUMENTO=" + Utils.notNull(doc.getPrgDocumento()) + 
		  	"&PRGTEMPLATESTAMPA=" + Utils.notNull(doc.getPrgTemplateStampa()) +
		  	"&STRCHIAVETABELLA=" + strChiaveTabella + 
		  	"&CODTIPODOCUMENTO=" + doc.getCodTipoDocumento() + 
		  	"&TIPODOCUMENTO=" + doc.getCodTipoDocumento();
  
  
  // gestione attributi
  PageAttribs attributi = new PageAttribs(user, pageToProfile);

  String btnAnnulla = "Torna alla lista";
  boolean canModify = false;
  boolean canInsert = false;
  boolean canInsertAllegati = attributi.containsButton("INSERISCI_ALLEGATO"); //X TEST
  boolean canGeneraStampa = attributi.containsButton("GENERA_STAMPA"); //X TEST
  boolean canAnnullaDoc = true; ////X TEST
  boolean annullato = !(codStatoAtto.equals("NP") && doc.getNumProtocollo()==null) &&
         		 	   (!codStatoAtto.equals("PR"));
  
  boolean preparaInvio = attributi.containsButton("INVIA_PROTOCOLLO_PI3");
  
  canAnnullaDoc = (!annullato) && canAnnullaDoc;
  canAnnullaDoc = false;
  
  String sysdate = DateUtils.getNow();
  String dataFine = doc.getDatFine();
  dataFine = (dataFine==null || dataFine.equals("")) ? sysdate: dataFine;

  boolean canEditPage  = (canInsert &&   isNewDoc) ||
						 (canModify && ! isNewDoc);

  String  readonly = (!canEditPage ? "true" : "false");
  
  boolean readOnlyStr = readonly.equalsIgnoreCase("true");

  // NUMERO DI PROTOCOLLO
  boolean isNuovoProtocollo = false;
  boolean numProtEditable = false;

  dataOraProt = Utils.notNull(doc.getDatProtocollazione());
  if (doc.getNumProtocollo() != null) {
	numProtV = doc.getNumProtocollo();
  }
  if (doc.getNumAnnoProt() != null) {
	numAnnoProtV = doc.getNumAnnoProt();
  }

  if (dataOraProt != null && !dataOraProt.equals("") && !dataOraProt.equals("null null")) {
  // la protocollazione docarea restituisce solo la data 
  	if (dataOraProt.length()>11) {
		oraProt  = dataOraProt.substring(11,16);
  	}
	else {
		oraProt="00:00";
	}
	dataProt = dataOraProt.substring(0,10);
  }
  
  String strNomeDoc = doc.getStrNomeDoc();
  String strDescrizioneDoc = doc.getStrDescrizione();
  boolean giaEsisteFileInDb = StringUtils.isFilled(strNomeDoc);
  boolean blobVuoto = !doc.getExistBlob();
  BigDecimal prgDocumento = doc.getPrgDocumento();
  
  boolean checkAllegati = false;
  SourceBean moduleCheckAllegati = (SourceBean) serviceResponse.getAttribute("MCheckAllegatiDocPadre");
  if (moduleCheckAllegati != null) {
	if (moduleCheckAllegati.containsAttribute("ALLEGATIOBBL")) {
		checkAllegati = true;	
	}
  }
  
  
  //SourceBean rowsAllegatiTemplateObbl = (SourceBean) serviceResponse.getAttribute("rowsAllegatiTemplateObbl");
  Vector rowsAllegatiTemplateObbl = serviceResponse.getAttributeAsVector("MCheckAllegatiDocPadre.rowsAllegatiTemplateObbl.ROWS.ROW");
  String arrNameTypeStampa[] = null;
  StringBuilder sbNameTypeStampa = new StringBuilder();
  if (rowsAllegatiTemplateObbl.size() > 0) {
	  arrNameTypeStampa = new String[rowsAllegatiTemplateObbl.size()];
   	for(int i=0; i<rowsAllegatiTemplateObbl.size(); i++)  { 
   		SourceBean rowAllegato = (SourceBean) rowsAllegatiTemplateObbl.elementAt(i);
  		String strNameTypeStampa = (String) rowAllegato.getAttribute("NOMETIPOSTAMPA");
  		arrNameTypeStampa[i] = strNameTypeStampa;
  		sbNameTypeStampa.append(strNameTypeStampa + ";");
   	}
  }
  
  	String msgConfermaGeneraStampa = "";
	//boolean checkFirma = true;
	/*
	Commento le parti riguardanti i controlli sulla presenza del consenso.
	Tali controlli andranno ripresi e rivisti quando si lavorerà sull'integrazione
	*/
	boolean checkFirma = false;
	checkFirma = serviceResponse.containsAttribute("MCheckFirmaGrafometrica.FLGFIRMAGRAFO"); 
//   SourceBean moduleCheckFirma = (SourceBean) serviceResponse.getAttribute("MCheckFirmaGrafometrica");
//   if (moduleCheckFirma != null) {
// 	if (moduleCheckFirma.containsAttribute("FIRMAGRAFOMETRICA")) {
// 		checkFirma = true;
// 		if (moduleCheckFirma.containsAttribute("FLGFIRMAGRAFO")) {
// 			Consenso consensoLav = (Consenso)sessionContainer.getAttribute("CONSENSO_" + cdnLavoratore);
// 			if (consensoLav != null) {
// 				String codStatoConsenso = consensoLav.getCodice();
// 				if (codStatoConsenso != null) {
// 					if (codStatoConsenso.equalsIgnoreCase(Consenso.REVOCATO)) {
// 						msgConfermaGeneraStampa = "Attenzione, il consenso &egrave; revocato e quindi occorre procedere con la firma autografa del documento";
// 					}
// 					else {
// 						if (codStatoConsenso.equalsIgnoreCase(Consenso.NON_DISPONIBILE)) {
// 							msgConfermaGeneraStampa = "Attenzione, consenso non disponibile, se si desidera procedere, il documento sarà sottoscritto "
// 								+ "con firma autografa";
// 						}
// 					}	
// 				}
// 			}
// 			else {
// 				msgConfermaGeneraStampa = "Attenzione, consenso non disponibile, se si desidera procedere, il documento sarà sottoscritto "
// 						+ "con firma autografa";	
// 			}
// 		}
// 	}
//   }
  
	boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
	boolean isConsensoAttivo = false;
	String ipOperatore=null;
	//System.out.println("isConsenso:"+isConsenso);
	if(isConsenso){
/*	 	ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
		isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") 
				  						&& filterConsenso.canView();*/
	 	isConsensoAttivo= serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
	 	if(isConsensoAttivo){
	 		 ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	 	  }
	 	checkFirma = checkFirma && isConsensoAttivo;
	 	//checkFirma = isConsensoAttivo;
	}

  String htmlStreamTop    = StyleUtils.roundTopTable(! readOnlyStr);
  String htmlStreamBottom = StyleUtils.roundBottomTable(! readOnlyStr);
  String goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_LISTASTAMPEPARLAVPAGE");

%>
<%@ include file="SezioniATendina.inc" %>
<%@ include file="../global/fieldChanged.inc" %>
<%@ include file="CommonFunctionStampa.inc" %>
<%@ include file="_apriGestioneDoc.inc"%>

<%
if(visualizzaStampa) {
	%>
	<script language="Javascript">
	  var urlDoc = "AdapterHTTP?";
	  urlDoc += "PAGE=REPORTFRAMESTAMPAPAGE";
	  urlDoc += "&QUERY_STRING="+HTTPrequest;
	  //alert(urlDoc);
	  document.location=urlDoc;
	</script>
	<%	

}else{
%>
<html>
<head>


<title>Dettaglio Stampa Parametrica</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/documenti/docPopup.js"></script>
<script language="Javascript" src="../../js/documenti/dettagliDocumento.js"></script>
<script language="Javascript" src="../../js/documenti/lookEntityDoc.js"></script>
<script language="Javascript" src="../../js/ComboPair.js"></script>

<script language="Javascript">
	
	var _codStatoAtto = "<%=doc.getCodStatoAtto()%>";
	
	function checkAnnullaDoc(ancheSeNonCambiato) {	
		if(ancheSeNonCambiato && document.form.CODSTATOATTO.value==_codStatoAtto){
			alert("Stato atto non modificato");
			return false;
		}		
		if(document.form.CODSTATOATTO.value=="AU"  && 
			!(document.form.codMotAnnullamentoAtto != null && document.form.codMotAnnullamentoAtto.value!="")){
			alert("Motivo di annullamento documento obbligatorio");
			return false;
		}
		if(document.form.CODSTATOATTO.value!="AU"  && 
			(document.form.codMotAnnullamentoAtto != null && document.form.codMotAnnullamentoAtto.value!="")){
			alert("Motivo di annullamento obbligatorio se si annulla il documento");
			document.form.codMotAnnullamentoAtto.focus();
			return false;
		}
		if (document.form.CODSTATOATTO.value=="AU"){
			return confirm("La funzionalità annulla il documento.\n Vuoi proseguire?");
		}
		
		return true;
	}

	
	  function callPi3() {
		  	//alert('Pi3');
		    var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnfunzione%>&PAGE=Pi3StampeHomePage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratore%>&NUMPROT=<%=doc.getNumProtocollo()%>&prgDocumento=<%=prgDocumento%>&pagina=DispoDettaglioPage&strChiaveTabella=<%=strChiaveTabella%>&nomeDoc=<%=strDescrizioneDoc%>&DOCUMENTTYPE=<%=doc.getCodTipoDocumento()%>";
		   
		    window.open(urlpage,'Invio Protocollazione Pi3','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
		  }
	  
	  
	  
	  function tornaAllaLista() {		  
		    if (isInSubmit()) return;

			
			if (flagChanged == true) {
				if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
					return;
				}
			} 
			
			var url = "AdapterHTTP?";
	    	setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
		}
</script>

<script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

</head>

<body class="gestione" onload="onLoad()">
<%
// InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
// testata.show(out);
%>

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.setSkipLista(true);
		testata.show(out);
	}
	
	// TESTATA AZIENDA
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		testata.setSkipLista(true);
		testata.show(out);
	}
	

%>

<font color="red">
	<af:showErrors/>
</font>
<font color="green">
  	<af:showMessages prefix="MProtocollaDocPadre"/>
  	<af:showMessages prefix="MSalvaAllegatoStampaParam"/>
  	<af:showMessages prefix="MSalvaDocumentoAllegatoStampaParam"/>
	<%if(isConsenso){%>
 	  <af:showMessages prefix="M_VerificaAmConsensoFirma"/>
	<%}%>
</font>

<%if (protocollazione) {%>
<script language="Javascript">
visualizzaDocumento('DOWNLOAD','&ErroreProtocollazione=<%=codiceErroreProtocolla%>&warnings=<%=codiceWarningsString%>','<%=prgDocumento%>');
</script>
<%} else {


%>

<p class="titolo">Dettaglio Stampa Parametrica</p>

<%
out.print(htmlStreamTop);
%>

<af:form name="form" action="AdapterHTTP" method="POST">
<input type="hidden" id="page" name="PAGE" value="">
<input type="hidden" id="pageBack" name="PAGEBACK" value="DettaglioDocumentoPadreStampParamPage">
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="prgDocumento" value="<%= Utils.notNull(prgDocumento) %>" />
<input type="hidden" name="numKloDocumento" value="<%= Utils.notNull(doc.getNumKloDocumento()) %>" />
<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=Utils.notNull(doc.getPrgTemplateStampa())%>" />
<input type="hidden" name="TIPODOCUMENTO" value="<%=doc.getCodTipoDocumento()%>" />
<input type="hidden" name="CODTIPODOCUMENTO" value="<%=doc.getCodTipoDocumento()%>" />
<input type="hidden" name="DOMINIO" value="<%=dominioDati%>" />
<input type="hidden" name="FIRMAGRAFOMETRICA" value="<%=checkFirma%>" />
<input type="hidden" name="IPOPERATORE" value="<%=ipOperatore%>" />

<p align="center">
<table class="main">
    <tr>
        <td  colspan="4" class="azzurro_bianco">
	        <table id="sezioneProt" style="display:"  border="0" width="100%">
	        	<tr>
			    	<td class="etichetta2">Stato atto</td>
			        <td>
			        	<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        		<tr>
			        			<td>
			        			<%
			        			//modifica segnalazione 4351 punto 2 rendere modificabile stato atto se documento protocollato
			        			//disableModify in luogo di !canAnnullaDoc
			        			boolean disableModify = true;
			        			if(codStatoAtto.equalsIgnoreCase(Properties.STATO_ATTO_PROTOC))
			        				disableModify = false;
			        			%>
			        			
			        			<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="COMBO_STATO_ATTO_DOC"
				                    title="Stato atto del documento" selectedValue="<%=codStatoAtto%>"
				                    disabled="<%=String.valueOf(disableModify)%>" onChange="fieldChanged();protocollazione_onChange()" />        				
			        			</td>
			        			<td align="right">anno&nbsp;</td>
			        			<td>
			        				<af:textBox name="numAnnoProt" type="integer"
			                            value="<%= Utils.notNull(doc.getNumAnnoProt()) %>"
			                            title="Anno di protocollazione" classNameBase="input"
			                            size="4" maxlength="4"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="true" />
								</td>
			        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
			        			<td>
			    				 	<af:textBox name="numProtocollo"
			                			value="<%=  Utils.notNull(doc.getNumProtocollo()) %>"
			                            title="Numero di protocollo" classNameBase="input"
			                            size="10" maxlength="100"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="true" />
			                    </td>
			                    <td class="etichetta2">data
			        		        <af:textBox name="dataProt" type="date"
			               				value="<%=dataProt%>"
			               				title="data di protocollazione" classNameBase="input"
			               				size="20" maxlength="10"
			               				required="false" trim="false"
			               				readonly="true" />
			            		</td>
			            	</tr>
			            </table>
			          </td>
			       </tr>
			       
			       <tr><%-- se nuovo doc. elimino una colonna spostando a sx tutta la tabella --%>
					<td class="etichetta2">Motivo Annull. Atto</td>
			    	<td>
			    		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        		<tr>
			    				<%
			    					String moduloDecodAnnullamento = (canAnnullaDoc) ? 
									                                     "M_getMotivoAnnullamentoDocFiltrata" :
									                                     "M_getTuttiMotiviAnnullamentoDoc";
			    				
			    				//modifica segnalazione 4351 punto 2 rendere modificabile stato atto se documento protocollato
			    				//disableModify in luogo di !canAnnullaDoc
			    				%>
			    				<td nowrap="nowrap">
			     					<af:comboBox classNameBase="input" 
			     					    name="codMotAnnullamentoAtto" moduleName="<%=moduloDecodAnnullamento%>" 
			     					    addBlank="true" selectedValue="<%=codMotAnnullamentoAtto%>" 
			     					    disabled="<%=String.valueOf(disableModify)%>"/>
			        			</td>	
			    				<td class="etichetta2">Doc. di</td>
			    				<td class="campo2" nowrap="nowrap">
					               <af:comboBox name="FlgCodMonoIO" selectedValue="<%=codIO%>" addBlank="true"
 			                         title="Documento di Input /Output" classNameBase="input"  			                         
 			                         disabled="<%=String.valueOf(readonly)%>" 
 			                         required="true" 
 			                         onChange="fieldChanged()"> 
 										<option value="I" <%=(codIO.equalsIgnoreCase("I"))? "SELECTED=\"true\"" : "" %>>Input</option> 
 										<option value="O" <%=(codIO.equalsIgnoreCase("O"))? "SELECTED=\"true\"" : "" %>>Output</option> 
 		              			   </af:comboBox> 
					            </td>
			    				
			    				 
					             <% if(canAnnullaDoc){ %>
					             <td>
					             	<input type="submit" class="pulsanti" name="AnnullaDoc" value="Salva" 
					             		onclick="return checkAnnullaDoc(true);">
					             </td>
					             <% } %>
			        		</tr>
			       		</table>
			    	</td>
				 </tr>
	        </table>
	    </td>
    </tr>
    
    <tr>
        <td class="etichetta">
       		Tipo documento
        </td>
        <td class="campo" colspan="3">
        	<input type="hidden" name="codTipoDocumento" value="<%=doc.getCodTipoDocumento()%>">
           	<af:textBox name="strTipoDoc" type="text" value="<%=Utils.notNull(doc.getStrTipoDoc())%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>
    
    <tr>
        <td class="etichetta">
       		Descrizione
        </td>
        <td class="campo" colspan="3">
           	<af:textBox name="strDoc" type="text" value="<%=Utils.notNull(doc.getStrDescrizione())%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>
    
    <tr>
        <td class="etichetta">
            Data di acquisizione / rilascio<br/>da parte del Centro per l'impiego
        </td>
        <td class="campo" colspan="3">
            <af:textBox type="date" name="DatAcqril"
			            value="<%= Utils.notNull(doc.getDatAcqril()) %>"
            			title="Data di acquisizione / rilascio"  classNameBase="input"
            			size="12" maxlength="10" required="true" readonly="true" />
        </td>
    </tr>
    
    <tr>
        <td class="etichetta">
            Data inizio validit&agrave;
        </td>
        <td class="campo2" nowrap="nowrap">
            <af:textBox name="DatInizio" type="date"
                        value="<%= Utils.notNull(doc.getDatInizio()) %>"
                        title="Data inizio validit&agrave;" classNameBase="input"
                        size="12" maxlength="10" required="true" readonly="true" />
        </td>
        <td class="etichetta" nowrap="nowrap">
            Data di fine validit&agrave;
        </td>
        <td class="campo2" nowrap="nowrap">
            <af:textBox type="date"
                    name="datFine" title="Data di fine validit&agrave;" value="<%= doc.getDatFine() %>"
                    classNameBase="input" size="12" maxlength="10" readonly="true" />
        </td>
    </tr>
    
    <tr>
		<td class="etichetta">
			CPI di riferimento
		</td>
		<td class="campo" colspan="3">
			<% 
			String codCpiDoc = doc.getCodCpi();
			String cpiDescr  = doc.getDescCpi() + " - " + codCpiDoc;
			%>
			<input type="hidden" name="codCpi" value="<%= codCpiDoc %>" />
			<af:textBox name="cpiDescr" value="<%= cpiDescr %>" classNameBase="input" size="60" maxlength="100" readonly="true" />
		</td>
	</tr>
    
     <tr>
        <td class="etichetta">
            Nome documento
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="strNomeDoc" value="<%= Utils.notNull(strNomeDoc) %>"
						title="Nome documento allegato"
						classNameBase="input" size="50" maxlength="100" readonly="true" />
        </td>
    </tr>
    
    <tr>
		<td class="etichetta">Note</td>
		<td class="campo">    
			<af:textArea name="note" cols="60" rows="4" title="Note" readonly="true" classNameBase="input"
			value="<%=doc.getStrNote()%>" maxlength="1000" />
		</td>
	</tr>
	
	</table>
	
	<br>
	
	<table class="main">
    
    <% if (giaEsisteFileInDb) { %>
       <tr>
         <td class="etichetta">
           Doc. archiviato
         </td>
         <td class="campo" nowrap="nowrap">
		<%
		String myJsBlobVuoto = "javascript:blobVuotoWarning('" + JavaScript.escape(strNomeDoc) + "')";
		%>
		Apri
		<%
		String myJsA;
		if (blobVuoto) myJsA = myJsBlobVuoto;
		else           myJsA = "javascript:visualizzaDocumento('DOWNLOAD',''," + prgDocumento + ")";
		%>
		<a href="<%= myJsA %>" onclick="return canGoAway();"><img src="../../img/text.gif" border="0" alt="Apre il documento" /></a>
		&nbsp;&nbsp;Scarica
		<%
		String myJsS;
		if (blobVuoto) myJsS = myJsBlobVuoto;
		else           myJsS = "AdapterHTTP?ACTION_NAME=DOWNLOAD&PRGDOCUMENTO=" + prgDocumento + "&asAttachment=true";
		%>
		<a href="<%= myJsS %>"><img src="../../img/download.gif" border="0" alt="Salva il documento" /></a>
		</td>
		</tr>		
	<%}%>
	
	<%
		if(preparaInvio && codStatoAtto.equals("PR")){
 %>
		<tr>
			<td colspan="2" align="center">
			<input class="pulsanti<%=((preparaInvio)?"":"Disabled")%>" onclick="javascript:callPi3()" type="button" name="protocollazionePi3" value="Protocollazione PiTre" />
			</td>
		</tr>
		<%} %>

	
	<%if (canInsertAllegati) {%>
			
		<tr>
         	<td class="etichetta" colspan="2">
           	<div class='sezione2' id='UlteInfoAllegati'>
              	 <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSezAllegati"));'/>
               	Allegati&nbsp;  
           	</div>
         	</td>
       	</tr>
		
		<tr>
         	<td colspan="2">
           	<div id="altreInfoSezAllegati" style="display:inline">
			<table  width="100%" cellspacing="0" cellpadding="3" border="0">
				<tr>
				<td colspan="2">
				<af:list moduleName="MListaAllegatiDocStampaParam" skipNavigationButton="1" />
				</td>
				</tr>
			</table>
			</div>
		</td>
		</tr>
	<%}%>
	
	</table>
	

	<br>
	
	<table>
		<tr>
			<td align="center">
			<%if (!codStatoAtto.equalsIgnoreCase(Properties.STATO_ATTO_PROTOC) && canGeneraStampa) {%> 
				<%if (canInsertAllegati) {%>
					<input type="button" class="pulsanti" name="addAllegati" value="Inserisci Allegato" onClick="gestioneAllegatiDocumento();">&nbsp;
				<%}%>
				
				<%
// 				String onClick = "";
// 		onClick = "apriGestioneDoc('RPT_STAMPA_PARAM','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + doc.getCodCpi() + "&prgDocumento=" + Utils.notNull(prgDocumento) + "','" + doc.getCodTipoDocumento() + "')";
 		%> 
		
<%-- 				<input type="button" class="pulsanti" name="btnGeneraStampa" value="Genera Stampa" onClick="<%=onClick%>"> --%>
				<input type="button" class="pulsanti" name="btnGeneraStampa" value="Genera Stampa" onClick="generaStampa('<%=sbNameTypeStampa%>');">
				
<!-- 				<input type="button" class="pulsanti" name="btnPiTre" value="Protocollazione PiTre">&nbsp; -->
<!-- 				<input type="button" class="pulsanti" name="btnRicevuta" value="Stampa Ricevuta"> -->
			<%}%>
			
							
				
<%-- 			<%if (canInsertAllegati) {%> --%>
			
<!-- 				<input type="button" class="pulsanti" name="addAllegati" value="Inserisci Allegato" onClick="gestioneAllegatiDocumento();">&nbsp; -->
<%-- 			<%} --%>
<%-- 			if (codStatoAtto.equalsIgnoreCase(Properties.STATO_ATTO_PROTOC)) {%> --%>
			
<!-- 				<input type="button" class="pulsanti" name="btnPiTre" value="Protocollazione PiTre">&nbsp; -->
<!-- 				<input type="button" class="pulsanti" name="btnRicevuta" value="Stampa Ricevuta"> -->
<%-- 			<%} else {%> --%>
<!-- 				<input type="button" class="pulsanti" name="btnGeneraStampa" value="Genera Stampa" onClick="generaStampa();"> -->
<%-- 			<%}%> --%>
			</td>
		</tr>
		<tr>
			<td align="center"><input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="tornaAllaLista();">
			</td>
		</tr>
	</table>
		
</p>
<div align="center">
<%
operatoreInfo.showHTML(out);
%>
</div>
<%
out.print(htmlStreamBottom);
%>
</af:form>
<%}%>
</body>
</html>
<%}%>