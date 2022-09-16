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
  String queryString = "";
  
  String prAutomatica     = null; 
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  BigDecimal kLockProt    = null;
  String dataOraProt = null;
  String oraProt  = null;
  String dataProt = null;
  String  _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
  String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
  String  prgDocumento = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGDOCUMENTO");
  
  SourceBean activeModule = (SourceBean) serviceResponse.getAttribute("DettagliDocumento");
  
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
  strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(activeModule, "strChiaveTabella");
	
  boolean canModify = false;
  boolean canInsert = false;
  boolean canAnnullaDoc = false;
  
  String sysdate = DateUtils.getNow();
  String dataFine = doc.getDatFine();
  dataFine = (dataFine==null || dataFine.equals("")) ? sysdate: dataFine;

  String  readonly = "true";
  
  boolean readOnlyStr = true;

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
//   System.out.println("inizio con il body");
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
	
	function annulla() {
		window.close();
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
		
		testata.show(out);
	}
	
	// TESTATA AZIENDA
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);

		testata.show(out);
	}
%>
<p class="titolo">Dettaglio Allegato</p>

<%
out.print(htmlStreamTop);
%>

<af:form name="form">
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />

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
			                            value='<%=""+doc.getNumAnnoProt() %>'
			                            title="Anno di protocollazione" classNameBase="input"
			                            size="4" maxlength="4"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="true" />
								</td>
			        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
			        			<td>
			    				 	<af:textBox name="numProtocollo"
			                			value='<%=""+doc.getNumProtocollo() %>'
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
            Nome documento allegato
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="strNomeDoc" value="<%= Utils.notNull(strNomeDoc) %>"
						title="Nome documento allegato"
						classNameBase="input" size="50" maxlength="100" readonly="true" />
        </td>
    </tr>
    
    
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
	<%}%>
    
</table>


<br>
<table>
	<tr>
		<td align="center">
		<input type="button" class="pulsanti" name="btnBack" onClick="annulla();" value="Chiudi">
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
