<%@ page contentType="text/html;charset=utf-8"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, 
                it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*,
                com.engiweb.framework.message.*,
                it.eng.afExt.utils.MessageCodes"%>


<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

	String pageToProfile = "ListaStampeParLavPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	String _page = (String) serviceRequest.getAttribute("PAGE");
	
	int _funzione = Integer.parseInt(serviceRequest.getAttribute("CDNFUNZIONE")==null?"0":(String) serviceRequest.getAttribute("CDNFUNZIONE"));
	

	
	String btnAnnulla = "Indietro";
	//GEstione Attributi
	PageAttribs attributi = new PageAttribs(user, pageToProfile);
	boolean canModify = attributi.containsButton("SALVA_ALLEGATO");
	//boolean canModify = true; //X TEST
	
	
	
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	String  dominioDati  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DOMINIO");	
	String prgDocumentoall = serviceRequest.containsAttribute("prgdocumento") ? serviceRequest.getAttribute("prgdocumento").toString() : "";
	String PRGTEMPLATESTAMPA = serviceRequest.containsAttribute("PRGTEMPLATESTAMPA") ? serviceRequest.getAttribute("PRGTEMPLATESTAMPA").toString() : "";
	String pageBack = serviceRequest.containsAttribute("PAGEBACK") ? serviceRequest.getAttribute("PAGEBACK").toString() : "";
	String tipoDocPrincipale = "";
	String nomeDocPrincipale = "";
	String codCpi = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codCpi");
	
	SourceBean docRow = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOSTAMPAPARAM.ROWS.ROW");
	if (docRow != null && docRow.getAttribute("PRGDOCUMENTO") != null) {
		tipoDocPrincipale = docRow.containsAttribute("desctipodoc") ? docRow.getAttribute("desctipodoc").toString() : ""; 
		nomeDocPrincipale = docRow.containsAttribute("strdescrizione") ? docRow.getAttribute("strdescrizione").toString() : "";
	}
	
	String DATAACQUISIZIONE = "";
	String codIO = "";
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	DATAACQUISIZIONE = formatter.format(new Date());
	String urlRequest = "";
	if (StringUtils.isFilled(prgAzienda)) {
		urlRequest = "AdapterHTTP?PAGE=" + pageBack + "&OPERATIONALLEGATI=SALVAALLEGATI&PRGDOCUMENTO=" + prgDocumentoall + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&PRGTEMPLATESTAMPA=" + PRGTEMPLATESTAMPA + "&CDNFUNZIONE=" + _funzione + "&DOMINIO=DA&codCpi=" + codCpi;
	}else{
		urlRequest = "AdapterHTTP?PAGE=" + pageBack + "&OPERATIONALLEGATI=SALVAALLEGATI&PRGDOCUMENTO=" + prgDocumentoall + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&PRGTEMPLATESTAMPA=" + PRGTEMPLATESTAMPA + "&CDNFUNZIONE=" + _funzione + "&DOMINIO=DL&codCpi=" + codCpi;
		
	}
		
	
	String btnSalva = "Salva";
	
// 	InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
// 	testata.setSkipLista(true);
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
		
%>

<head>
  <%@ include file="../global/fieldChanged.inc" %>
  
  <script type="text/javascript">

	var allegatoTemplate = new Array();
	var pageValue = "DocumentiRicercaAllegatiStampParamPage";
<%
  SourceBean allegatiTemplateStampaParam = (SourceBean) serviceResponse.getAttribute("MAllegatiTemplateStampaParam.rows");
	
	Vector vectSB = allegatiTemplateStampaParam.getAttributeAsVector("ROW");
	Iterator iter = vectSB.iterator();
	
	HashMap allegatoTemplate = new HashMap();
	int index = 0;
	String descrizioneAll = "";
	String codTipoDoc = ""; 
			
	while (iter.hasNext()) {
		SourceBean attuale = (SourceBean) iter.next();
		allegatoTemplate.put(attuale.getAttribute("CODICE"),attuale.getAttribute("IS_TEMPLATE"));
		if(attuale.getAttribute("CODTIPODOCUMENTO")!=null){
			descrizioneAll = attuale.getAttribute("DESCRIZIONE").toString();
			descrizioneAll = descrizioneAll.replaceAll("'", "/'");
			codTipoDoc = attuale.getAttribute("CODTIPODOCUMENTO").toString();
		}
	%>
	
		allegatoTemplate[<%=attuale.getAttribute("CODICE")%>]='<%=attuale.getAttribute("IS_TEMPLATE")%>';
		allegatoTemplate['DESCRIZIONE']="<%=descrizioneAll%>";
		allegatoTemplate['CODTIPODOCUMENTO']="<%=codTipoDoc%>";

	<%
		index++;
	} // while
	%>	
  	
  	
  
	function annulla() {
		
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;

		//document.frmAllegatiBack.PAGE.value = "DettaglioDocumentoPadreStampParamPage";
		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} else {
				document.frmAllegatiBack.OPERATIONALLEGATI.value = "BACK";
				doFormSubmit(document.frmAllegatiBack);
			}
		} else {
			document.frmAllegatiBack.OPERATIONALLEGATI.value = "BACK";
			doFormSubmit(document.frmAllegatiBack);
		}
	}

	function ricerca() {
		
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;

//		alert(document.frmAllegati.presaVisione.checked);
		if(document.frmAllegati.presaVisione.checked)
			document.frmAllegatiBack.presaVisione.value='S';
		if(document.frmAllegati.caricatoSucc.checked)
			document.frmAllegatiBack.caricatoSucc.value='S';
		
		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} else {
				document.frmAllegatiBack.PAGE.value = pageValue;
				doFormSubmit(document.frmAllegatiBack);
			}
		} else {
			document.frmAllegatiBack.PAGE.value = pageValue;
			doFormSubmit(document.frmAllegatiBack);
		}
	}
	
	function checkDateValidita() {
		var objData1 = document.getElementsByName("dataInizio");
		var objData2 = document.getElementsByName("dataFine");

		strData1 = objData1.item(0).value;
		strData2 = objData2.item(0).value;

		//costruisco la data di inizio
		if (strData1 != "") {
			d1giorno = parseInt(strData1.substr(0, 2), 10);
			d1mese = parseInt(strData1.substr(3, 2), 10) - 1; //il conteggio dei mesi parte da zero 
			d1anno = parseInt(strData1.substr(6, 4), 10);
			data1 = new Date(d1anno, d1mese, d1giorno);
		}

		//costruisce la data di fine
		if (strData2 != "") {
			d2giorno = parseInt(strData2.substr(0, 2), 10);
			d2mese = parseInt(strData2.substr(3, 2), 10) - 1;
			d2anno = parseInt(strData2.substr(6, 4), 10);
			data2 = new Date(d2anno, d2mese, d2giorno);
		}
		
		ok = true;
		if (strData1 != "" && strData2 != "") {
			if (data2 < data1) {
				alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
				document.getElementsByName("dataInizio").item(0).focus();
				ok = false;
			}
		}
		return ok;
	}

	function visualizzaTipoAllegato() {
		var sezioneAllegatoTempl = document.getElementById("sezAllegatoTemplate");
		var sezioneAltroAllegato = document.getElementById("sezAltroAllegato");
		if (document.frmAllegati.idAllegato.value == '0') {
			sezioneAltroAllegato.style.display="inline";
		}
		else {
			sezioneAltroAllegato.style.display="none";
			var objData1 = document.getElementsByName("strDescAltroAllegato");
			var objData2 = document.getElementsByName("ambito");
			//alert(objData1.item(0).value);
			//alert(allegatoTemplate['DESCRIZIONE']);
			//alert(objData2.item(0).value);
			//alert(allegatoTemplate['CODTIPODOCUMENTO']);
			objData1.item(0).value=allegatoTemplate['DESCRIZIONE'];
			objData2.item(0).value=allegatoTemplate['CODTIPODOCUMENTO'];
			//alert(objData1.item(0).value);
			//alert(objData2.item(0).value);
//			document.getElementByName("strDescAltroAllegato").item(0).value=allegatoTemplate['DESCRIZIONE'];
//			document.getElementByName("ambito").item(0).value=allegatoTemplate['CODTIPODOCUMENTO'];
		}
		
		var idAllegato = document.frmAllegati.idAllegato.value;
		var nomeAllegato = document.frmAllegati.idAllegato.options[document.frmAllegati.idAllegato.selectedIndex].text;
		var sezNoTemplate = document.getElementById("sezNoTemplate");
		if(allegatoTemplate[idAllegato]==0){
			//alert("Attenzione hai selezionato il template: "+ nomeAllegato);
			
			sezNoTemplate.style.display="none";
			
			document.getElementsByName("dataAcquisizione")[0].disabled="true";
			document.getElementsByName("dataInizio")[0].disabled="true";
			document.getElementsByName("dataFine")[0].disabled="true";
			document.getElementById("salvaButton").disabled="true";
			document.getElementById("salvaButton").classList.add("pulsantiDisabled");
			document.getElementById("messaggioTemplate").style.display="inline";
			pageValue = "ListaDocumentiAllegatiStampParamPage";
// 			document.frmAllegatiBack.PAGEBACK.value = "DettaglioDocumentoPadreStampParamPage";
			document.frmAllegatiBack.TEMPLATESTAMPA.value = ''+nomeAllegato;
			//document.frmAllegatiBack.PRGTEMPLATESTAMPA.value = idAllegato;
		}
		else{
			sezNoTemplate.style.display="inline";
			document.getElementsByName("dataAcquisizione")[0].disabled="";
			document.getElementsByName("dataInizio")[0].disabled="";
			document.getElementsByName("dataFine")[0].disabled="";
			document.getElementById("salvaButton").disabled="";
			document.getElementById("salvaButton").classList.remove("pulsantiDisabled");
			document.getElementById("messaggioTemplate").style.display="none";
			pageValue = "DocumentiRicercaAllegatiStampParamPage";
// 			document.frmAllegatiBack.PAGEBACK.value = pageValue;
			document.frmAllegatiBack.TEMPLATESTAMPA.value = '';
			//OK
		}
	}
		
  </script>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Gestione Allegati</title>
</head>
<body>
<%-- 	<%testata.show(out);%> --%>


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
	</font>

	<p class="titolo">
		Gestione Allegati al documento principale
	</p>
	<%
		out.print(htmlStreamTop);
	%>
	<af:form name="frmAllegati" method="POST" onSubmit="checkDateValidita()" 
		action="<%=urlRequest%>" encType="multipart/form-data">
<%-- 		<p align="center" id="messaggioTemplate" style="display:none"><font color="green"><%=MessageBundle.getMessage(Integer.toString(MessageCodes.Consenso.MSG_STAMPA_PARAMETRICA_TEMPLATE))%> --%>
<!-- 		</font></p> -->
	<center>
	<table  id="messaggioTemplate" style="display:none" align="center">
    <tr><td align="center"><b><%=MessageBundle.getMessage(Integer.toString(MessageCodes.Consenso.MSG_STAMPA_PARAMETRICA_TEMPLATE))%></b></td></tr>
    </table>
    </center>
		<p align="center">
		<table class="main">
			<tr>
				<td class="etichetta">Documento principale</td>
				<td class="campo">
					<af:textBox type="text" name="STRNOMEDOC"
						title="Descrizione" size="50" value="<%=nomeDocPrincipale%>" classNameBase="input"
						readonly="true" maxlength="100" />
				</td>
			</tr>				
			<tr>
				<td class="etichetta">Tipo Documento</td>				
				<td class="campo">
					<af:textBox type="text" name="STRTIPODOC"
						title="Documento principale" size="50" value="<%=tipoDocPrincipale%>" classNameBase="input"
						readonly="true" maxlength="100" />
				</td>
			</tr>		
  			<tr>
			<td colspan="2">
			<div id="sezAllegatoTemplate" style="display:inline">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Allegato</td>
					<td class="campo"><af:comboBox name="idAllegato"
							size="1" title="Allegato" multiple="false"
							required="true" focusOn="false" moduleName="MAllegatiTemplateStampaParam"
							classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
							onChange="fieldChanged()" addBlank="true" blankValue=""
							selectedValue="" onChange="visualizzaTipoAllegato();" />
					
					
					</td>
				</tr>
			</table>
			</div>
			</td>
			</tr>
			
			<tr>
			<td colspan="2">
			<div id="sezAltroAllegato" style="display:none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Descrizione</td>
					<td class="campo"><af:textBox type="text" name="strDescAltroAllegato"
							title="Descrizione" required="true" size="50" value=""
							onKeyUp="fieldChanged();" classNameBase="input"
							readonly="<%=String.valueOf(!canModify)%>" maxlength="100" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta">Tipo allegato</td>
					<td class="campo"><af:comboBox name="ambito"
							size="1" title="Tipo allegato" multiple="false"
							required="true" focusOn="false" moduleName="ComboTipoDocumento"
							classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
							onChange="fieldChanged()"
							selectedValue="" addBlank="true"
							blankValue="" />
					</td>
				</tr>
			</table>
			</div>
			</td>
			</tr>
			
			<tr>
				<td class="etichetta">Data di acquisizione/rilascio da parte del Centro per l'impiego</td>
				<td class="campo">
				     <af:textBox type="date" name="dataAcquisizione" 
							title="Data di acquisizione" required="true" value="<%=DATAACQUISIZIONE%>" size="12" maxlength="10"
							validateOnPost="true" disabled="<%=String.valueOf(!canModify)%>"/> 
				</td>
			</tr>
			
			<tr>
				<td class="etichetta">Data inizio validita'</td>
				<td class="campo">
<!-- 				modifica relatica alla segnalazione 4351 eliminare obbligatorietà del campo Data inizio validita' -->
				     <af:textBox type="date" name="dataInizio" value="" size="12" maxlength="10"
							title="Data inizio validita'"  
							validateOnPost="true" required="true" disabled="<%=String.valueOf(!canModify)%>"/>
					
			</tr>
			
			<tr>
				<td class="etichetta">Data fine validita'</td>
				<td class="campo">
					<af:textBox type="date" name="dataFine" required="false" value="" size="12" maxlength="10"
							title="Data fine validita'"
							validateOnPost="true" disabled="<%=String.valueOf(!canModify)%>"/>
				</td>
			</tr>
			
			
			<tr>
			
				<td colspan="2">
				<div id="sezNoTemplate" >
					<table>
		          	<tr>
		            	<td class="etichetta">Seleziona l'allegato
		            	</td>
		            	<td>
		               		<img src="../../img/upload.gif" border="0" />
		               		<input type="FILE" name="FILE1" size="50" onClick="fieldChanged()" />
		            	</td>
		          	</tr>
		          	
		          	<tr>
			          	<td class="etichetta">L'allegato e' stato acquisito e verra' caricato in un secondo momento
			          	</td>
			          	<td class="campo">
							<input type="checkbox" name="caricatoSucc" value="S"/>
						</td>
		          	</tr>
					
					
		          	</table>
		          	</div>
	          	</td>
          	</tr>
          	
          	<tr>
        				<td class="etichetta">Presa visione dell'allegato
				</td>
          		<td class="campo">
					<input type="checkbox" name="presaVisione" value="S"/>
				</td>
			</tr>
		</table>
		
		<br>
		<%if(canModify){ %>
		<table>
			<tr>
			
				<td align="center">
					<input type="submit" id="salvaButton" class="pulsanti" name="btnSalva" value="<%=btnSalva%>">
				</td>
				
				
			</tr>
		</table>
		<%} %>
		</p>
	</af:form>
	
	<af:form name="frmAllegatiBack" action="AdapterHTTP" method="POST">
		<input type="hidden" id="page" name="PAGE" value="<%=pageBack%>">
		<input type="hidden" id="pageBack" name="PAGEBACK" value="<%=pageBack%>">
		<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
		<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>">
		<input type="hidden" name="prgUnita" value="<%=prgUnita%>">
		<input type="hidden" name="codCpi" value="<%= codCpi %>">
		<input type="hidden" name="OPERATIONALLEGATI" value="">
		<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
		<input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumentoall%>">
		<input type="hidden" name="TEMPLATESTAMPA" value="">
		<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=PRGTEMPLATESTAMPA%>">
		<input type="hidden" name="DOMINIO" value="<%=dominioDati%>">
		<input type="hidden" name="presaVisione" value="">
		<input type="hidden" name="caricatoSucc" value="">
		
		<p align="center">
		<table class="main">
			<tr>
				<td align="center">
					<input type="button" class="pulsanti" name="BTNRICERCA" value="Ricerca" onClick="ricerca();">&nbsp;&nbsp;
					<input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
				</td>
			</tr>
		</table>
	  	</p>
	</af:form>
	<%
		out.print(htmlStreamBottom);
	%>
</body>
</html>