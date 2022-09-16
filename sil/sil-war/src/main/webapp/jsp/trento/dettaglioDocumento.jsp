<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			com.engiweb.framework.util.JavaScript,
			java.text.SimpleDateFormat,
			java.util.*,
			java.math.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.bean.*,
			it.eng.afExt.utils.*,
			it.eng.sil.Values,
			it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
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
  String prgDocumentoPadre = serviceRequest.containsAttribute("PRGDOCUMENTOPADRE")? serviceRequest.getAttribute("PRGDOCUMENTOPADRE").toString() : "";
  String prgDocumentoFiglio = serviceRequest.containsAttribute("PRGDOCUMENTO")? serviceRequest.getAttribute("PRGDOCUMENTO").toString() : "";
  String pageBack = serviceRequest.containsAttribute("PAGEBACK") ? serviceRequest.getAttribute("PAGEBACK").toString() : "";
  
  boolean isNewDoc = false;
  SourceBean activeModule = (SourceBean) serviceResponse.getAttribute("DettagliDocumento");
  SourceBean docPadreModule = (SourceBean) serviceResponse.getAttribute("DettagliDocumentoPadre.ROWS.ROW");
  SourceBean docAllegatoModule = (SourceBean) serviceResponse.getAttribute("M_GetInfoDocAllegato.ROWS.ROW");
  
  String flgCaricaSucc = SourceBeanUtils.getAttrStrNotNull(docAllegatoModule, "flgcaricsuccessivo");
  String flgPresaVisione = SourceBeanUtils.getAttrStrNotNull(docAllegatoModule, "flgpresavisione");
  String prgdocAllegato = serviceRequest.containsAttribute("PRGDOCUMENTOALLEGATO")? serviceRequest.getAttribute("PRGDOCUMENTOALLEGATO").toString() : "";

  SourceBean checkStatoPi3Module = (SourceBean) serviceResponse.getAttribute("M_CHECK_AM_PROTOCOLLO_DOCUMENTO_PITRE_STATE.ROWS.ROW");
  String checkPi3 = SourceBeanUtils.getAttrStrNotNull(checkStatoPi3Module, "codstatodoc");
 // System.out.println("checkPi3: "+checkPi3);
  
 
 Documento docPadre = new Documento(new BigDecimal(serviceRequest.getAttribute("PRGDOCUMENTOPADRE").toString()));
 String numeroPratica = docPadre.getNumAnnoProt() + "/" + docPadre.getNumProtocollo();
  boolean inviaAllegatoPi3 = false;
  

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
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	//String prgDocumento = serviceRequest.containsAttribute("prgdocumento") ? serviceRequest.getAttribute("prgdocumento").toString() : "";
	String PRGTEMPLATESTAMPA = serviceRequest.containsAttribute("PRGTEMPLATESTAMPA") ? serviceRequest.getAttribute("PRGTEMPLATESTAMPA").toString() : "";
	
	String  dominioDati  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DOMINIO");	
  strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(activeModule, "strChiaveTabella");
  String queryString = SourceBeanUtils.getAttrStrNotNull(activeModule, "QUERY_STRING");
  String codCpi = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codCpi");
  
  
  // gestione attributi	
  String _page = "DettagliDocumentoStampParamPage";
  PageAttribs attributi = new PageAttribs(user, _page);
  
  
  String urlRequest = "";
  if (StringUtils.isFilled(prgAzienda)) {
		urlRequest = "AdapterHTTP?PAGE=" + pageBack + "&OPERATIONALLEGATI=SALVAALLEGATI&PRGDOCUMENTO=" + prgDocumentoPadre + "&prgDocumentoFiglio=" + prgDocumentoFiglio + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&PRGTEMPLATESTAMPA=" + PRGTEMPLATESTAMPA + "&CDNFUNZIONE=" + cdnfunzione + "&DOMINIO=DA&codCpi=" + codCpi;
	}else{
		urlRequest = "AdapterHTTP?PAGE=" + pageBack + "&OPERATIONALLEGATI=SALVAALLEGATI&PRGDOCUMENTO=" + prgDocumentoPadre + "&prgDocumentoFiglio=" + prgDocumentoFiglio + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&PRGTEMPLATESTAMPA=" + PRGTEMPLATESTAMPA + "&CDNFUNZIONE=" + cdnfunzione + "&DOMINIO=DL&codCpi=" + codCpi;
		
	}
  String btnSalva = "Salva";
  boolean canModify = false;
  boolean canInsert = false;
  boolean canAnnullaDoc = true; ////X TEST
  boolean annullato = !(codStatoAtto.equals("NP") && doc.getNumProtocollo()==null) &&
         		 	   (!codStatoAtto.equals("PR"));
  
  canAnnullaDoc = (!annullato) && canAnnullaDoc;
  canAnnullaDoc = false;
  
  // RESTRIZIONE DEL PERMESSO DI MODIFICA IN BASE ALLO STATO DEL DOCUMENTO (STORICO? PROTOCOLLATO?)
  // e'possibile selezionare un documento storicizzato anche al di fuori della lista delle informazioni storiche.
  // Per esempio con una ricerca specifica sulla data di fine validita' oppure nel momento in cui inseriamo o 
  // modifichiamo il documento: al successivo ricaricamento della pagina e' necessario stabilire se sia o meno 
  // valido. Un documento con data fine validita' = alla data odierna e' un docuemnto ancora valido.
  // Mantengo il flag infStoriche perche' permette di capire se si proviene dalla lista delle inf. storiche
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
  boolean giaEsisteFileInDb = StringUtils.isFilled(strNomeDoc);
  boolean blobVuoto = ! doc.getExistBlob();
  BigDecimal prgDocAllegato = doc.getPrgDocumento();
  
  String htmlStreamTop    = StyleUtils.roundTopTable(! readOnlyStr);
  String htmlStreamBottom = StyleUtils.roundBottomTable(! readOnlyStr);
  
  String codMinSap = "";
  Vector vectCodSap = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
  if ((vectCodSap != null) && (vectCodSap.size() > 0)) {
	SourceBean beanLastInsert = (SourceBean) vectCodSap.get(0);
	codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
  }
  
  
  if(!flgCaricaSucc.equalsIgnoreCase("S")&&!StringUtils.isEmpty(checkPi3)&&checkPi3.equalsIgnoreCase("ACS") && docPadre.getCodStatoAtto().equals("PR")&&!blobVuoto)
	  inviaAllegatoPi3 = true;
  
%>
<html>
<head>
<%@ include file="../global/fieldChanged.inc" %>
<title>Dettaglio documento</title>
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

	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		urlRequest = "";
		if (isInSubmit())
			return;
		
		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} else {
				doFormSubmit(document.form);
			}
		} else {
			doFormSubmit(document.form);
		}
	}

	
	  function callPi3() {
		  	//alert('Pi3');
		    var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnfunzione%>&PAGE=Pi3StampeHomePage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratore%>&NUMPROT=<%=doc.getNumProtocollo()%>&prgDocumento=<%=prgDocumentoFiglio%>&pagina=DispoDettaglioPage&strChiaveTabella=<%=strChiaveTabella%>&nomeDoc=<%=doc.getStrDescrizione()%>&numeroPratica=<%=numeroPratica%>&DOCUMENTTYPE=<%=doc.getCodTipoDocumento()%>&actionPi3=A";
		   
		    window.open(urlpage,'Invio Protocollazione Pi3','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
		  }

</script>

<script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
<%@ include file="_apriGestioneDoc.inc"%>

</head>

<body class="gestione" onload="onLoad()">
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
<p class="titolo">Dettaglio Allegato</p>

<%
out.print(htmlStreamTop);
%>

<af:form name="form" method="POST" action="<%=urlRequest%>" encType="multipart/form-data">

<input type="hidden" id="page" name="PAGE" value="<%=pageBack%>">
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="prgDocumento" value="<%= prgDocumentoPadre %>" />
<input type="hidden" name="prgDocumentoFiglio" value="<%= prgDocumentoFiglio %>" />
<input type="hidden" name="numKloDocumento" value="<%= Utils.notNull(doc.getNumKloDocumento()) %>" />
<input type="hidden" name="OPERATIONALLEGATI" value="BACK" />
<input type="hidden" name="DOMINIO" value="<%=dominioDati%>">
<input type="hidden" name="idAllegato" value="1">
<input type="hidden" name="prgDocumentoAllegato" value="<%=prgdocAllegato%>">
<input type="hidden" name="actionPi3" value="A">
<input type="hidden" name= "numeroPratica" value="<%=numeroPratica %>" />
<input type="hidden" name= "prgTemplateStampa" value="<%=PRGTEMPLATESTAMPA%>" />
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
			        			<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="COMBO_STATO_ATTO_DOC"
				                    title="Stato atto del documento" selectedValue="<%=codStatoAtto%>"
				                    disabled="<%=String.valueOf(!canAnnullaDoc)%>" onChange="fieldChanged();protocollazione_onChange()" />        				
			        			</td>
			        			<td align="right">anno&nbsp;</td>
			        			<td>
			        				<af:textBox name="numAnnoProt" type="integer"
			                            value="<%= doc.getNumAnnoProt().toString() %>"
			                            title="Anno di protocollazione" classNameBase="input"
			                            size="4" maxlength="4"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="true" />
								</td>
			        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
			        			<td>
			    				 	<af:textBox name="numProtocollo"
			                			value="<%= doc.getNumProtocollo().toString() %>"
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
			    				%>
			    				<td nowrap="nowrap">
			     					<af:comboBox classNameBase="input" 
			     					    name="codMotAnnullamentoAtto" moduleName="<%=moduloDecodAnnullamento%>" 
			     					    addBlank="true" selectedValue="<%=codMotAnnullamentoAtto%>" 
			     					    disabled="<%=String.valueOf(!canAnnullaDoc)%>"/>
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
       		Documento principale
        </td>
        <td class="campo" colspan="3">
        	<%
        	String descrizioneDocPadre = SourceBeanUtils.getAttrStrNotNull(docPadreModule, "strdescrizione");
        	%>
           	<af:textBox name="descDocPadre" type="text" value="<%=descrizioneDocPadre%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>        
        
    <tr>
        <td class="etichetta">
       		Tipo Documento
        </td>        
        <td class="campo" colspan="3">
        	<%
        	String descTipoDocumentoPadre = SourceBeanUtils.getAttrStrNotNull(docPadreModule, "desctipodoc");
        	%>
           	<af:textBox name="descTipoDocPadre" type="text" value="<%=descTipoDocumentoPadre%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>

    <tr>
        <td class="etichetta">
       		Nome allegato
        </td>
        <td class="campo" colspan="3">
        	<af:textBox name="strNomeDoc" type="text" value="<%=Utils.notNull(doc.getStrDescrizione())%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>    

    <tr>
        <td class="etichetta">
       		Tipo allegato
        </td>
        <td class="campo" colspan="3">
        	<input type="hidden" name="codTipoDocumento" value="<%=doc.getCodTipoDocumento()%>">
           	<af:textBox name="strTipoDoc" type="text" value="<%=Utils.notNull(doc.getStrTipoDoc())%>" classNameBase="input" size="60" readonly="true" />
	   	</td>
    </tr>
    
    <tr>
        <td class="etichetta">
            Data di acquisizione / rilascio<br/>da parte del Centro per l'impiego
        </td>
        <td class="campo" colspan="3">
            <af:textBox type="date" name="dataAcquisizione"
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
    <%--
     <tr>
        <td class="etichetta">
            Nome documento allegato
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="strNomeDoc" value="<%= Utils.notNull(strNomeDoc) %>"
						title="Nome documento allegato"
						classNameBase="input" size="50" maxlength="100" readonly="true" />
        </td>
    </tr>
     --%>
    
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
		else           myJsA = "javascript:visualizzaDocumento('DOWNLOAD',''," + prgDocAllegato + ")";
		%>
		<a href="<%= myJsA %>" onclick="return canGoAway();"><img src="../../img/text.gif" border="0" alt="Apre il documento" /></a>
		&nbsp;&nbsp;Scarica
		<%
		String myJsS;
		if (blobVuoto) myJsS = myJsBlobVuoto;
		else           myJsS = "AdapterHTTP?ACTION_NAME=DOWNLOAD&PRGDOCUMENTO=" + prgDocAllegato + "&asAttachment=true";
		%>
		<a href="<%= myJsS %>"><img src="../../img/download.gif" border="0" alt="Salva il documento" /></a>
		</td>
		</tr>		
	<%
	}else{ %>
    <%	if(flgCaricaSucc.equalsIgnoreCase("S")) { %>
    <tr>
			
		<td colspan="4">
				<table width="100%">
		          	<tr>
		            	<td class="etichetta">Seleziona l'allegato
		            	</td>
		            	<td>
		               		<img src="../../img/upload.gif" border="0" />
		               		<input type="FILE" name="FILE1" size="50" onClick="fieldChanged()" />
		            	</td>
		          	</tr>
		          	
	          	</table>

		</td>
    </tr>
    <%
    	}
    }
    %>
    <tr>
    	<td class="etichetta">L'allegato e' stato acquisito e verra' caricato in un secondo momento
        </td>
        <td class="campo" colspan="3">
			<input type="checkbox" name="caricatoSucc" disabled="true" <%if(flgCaricaSucc.equalsIgnoreCase("S")) { out.print("CHECKED"); }%>/>
		</td>
   	</tr>
   	
	<tr>
		<td class="etichetta">Presa visione dell'allegato
		</td>
       	<td class="campo" colspan="3">
			<input type="checkbox" name="presaVisione" disabled="true" <%if(flgPresaVisione.equalsIgnoreCase("S")) { out.print("CHECKED"); }%>/>
		</td>
	</tr>
    
</table>


<br>

	
<table>
	<tr>
		<td align="center">
		<%if(!giaEsisteFileInDb) { %>
		<input type="submit" id="salvaButton" class="pulsanti" name="btnSalva" value="<%=btnSalva%>">
		<%} 
		if(inviaAllegatoPi3){
			%>
			<input class="pulsanti" onclick="javascript:callPi3()" type="button" name="protocollazionePi3" value="Protocollazione PiTre">
		<%	
		}
		%>
		<input type="submit" class="pulsanti" name="btnBack" onClick="annulla();" value="Indietro">
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
</body>
</html>
