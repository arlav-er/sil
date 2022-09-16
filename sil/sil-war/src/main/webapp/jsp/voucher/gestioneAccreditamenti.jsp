<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.User,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%

	//flag booleano che discrimina l'inserimento o la modifica
	boolean flag_insert=false;
	String  titolo = "Accreditamenti";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");

	

	

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaPage";
	

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	
	
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String strCodCpi;
	strCodCpi =  user.getCodRif();
	//Solamente per testare ricordare di eliminare la riga
	canModify=true;
	
	
	 String codComuni= StringUtils.getAttributeStrNotNull(serviceRequest, "codComuni");
	 String codfiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "CODICEFISCALE");
	 String codSede= StringUtils.getAttributeStrNotNull(serviceRequest, "CODSEDE");
   	 String denominazione="";
   	 String indirizzo="";
   	 String noteStampa="";
   	 String codazioneMisura="";
   	 String dataDal="";
   	 String dataAl="";
 	String strCap="";
    String strCom="";
    String codCom=""; 
   	 
   	indirizzo=StringUtils.getAttributeStrNotNull(serviceRequest, "INDIRIZZO");
   	denominazione=StringUtils.getAttributeStrNotNull(serviceRequest, "DENOMINAZIONE");
   	strCom=StringUtils.getAttributeStrNotNull(serviceRequest, "COMUNE");
   	noteStampa=StringUtils.getAttributeStrNotNull(serviceRequest, "noteStampa");
   	codCom=StringUtils.getAttributeStrNotNull(serviceRequest, "CODCOM");
   	
	 if (!StringUtils.isEmpty(denominazione)){
		  sessionContainer.setAttribute("denominazione",denominazione);
	 }
	 if (!StringUtils.isEmpty(indirizzo)){
		  sessionContainer.setAttribute("indirizzo",indirizzo);
	 }
	 if (!StringUtils.isEmpty(codCom)){
		  sessionContainer.setAttribute("codiceComune",codCom);
	 }
	 if (!StringUtils.isEmpty(strCom)){
		  sessionContainer.setAttribute("DesComune",strCom);
	 }
   	
   	//Se il codcomune è 
    if (StringUtils.isEmpty(codComuni)){
    	codComuni= StringUtils.getAttributeStrNotNull(serviceRequest, "CODCOM");
    }
   	
	
   	if (_page.equals("UpdateAccreditamentoPage")|| _page.equals("InsertAccreditamentoPage") || _page.equals("MakeSoggettoAccreditatoPage")){
   	  	codComuni= StringUtils.getAttributeStrNotNull(serviceRequest, "codComuneSel");
 	 	codfiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "cfSel");
 	  	codSede= StringUtils.getAttributeStrNotNull(serviceRequest, "codSedeSel");
    	 indirizzo=StringUtils.getAttributeStrNotNull(serviceRequest, "indirizzoSel");
    	denominazione=StringUtils.getAttributeStrNotNull(serviceRequest, "denominazioneSel");
    	strCom=StringUtils.getAttributeStrNotNull(serviceRequest, "descComuneSel");
    	noteStampa=StringUtils.getAttributeStrNotNull(serviceRequest, "noteStampaSel");
    	codazioneMisura=StringUtils.getAttributeStrNotNull(serviceRequest, "PrgAzioniMisuraSel");
    	/* dataDal=StringUtils.getAttributeStrNotNull(serviceRequest, "dataDalSel");
    	dataAl=StringUtils.getAttributeStrNotNull(serviceRequest, "dataAlSel"); */
   	}
	if (_page.equals("DeleteAccreditamentiPage")){
		codfiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "CODICEFISCALE");
		codSede= StringUtils.getAttributeStrNotNull(serviceRequest, "CODSEDE");
		codComuni= StringUtils.getAttributeStrNotNull(serviceRequest, "CODCOMUNESEL");
		if (StringUtils.isEmpty(codComuni)){
			codComuni=(String)sessionContainer.getAttribute("codiceComune").toString();
		}
		indirizzo=StringUtils.getAttributeStrNotNull(serviceRequest, "INDIRIZZO");
		if (StringUtils.isEmpty(indirizzo)){
			indirizzo=(String)sessionContainer.getAttribute("indirizzo").toString();
		}
		
		denominazione=StringUtils.getAttributeStrNotNull(serviceRequest, "DENOMINAZIONE");
		if (StringUtils.isEmpty(denominazione)){
			denominazione=(String)sessionContainer.getAttribute("denominazione").toString();
		}
		strCom=StringUtils.getAttributeStrNotNull(serviceRequest, "DESCCOMUNESEL");
		if (StringUtils.isEmpty(strCom)){
			strCom=(String)sessionContainer.getAttribute("DesComune").toString();
		}
		noteStampa=StringUtils.getAttributeStrNotNull(serviceRequest, "noteStampa");
	}
	
	 String cfSel=" ";
	 String descComuniSel=" ";
	 String denominazioneSel="";
	 String codComune="";
	 String tipoRicerca="";
	 
	   cfSel  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cfSel");
	   if (StringUtils.isEmpty(cfSel)){
		   cfSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "cfSel");
	   }
	   if (!StringUtils.isEmpty(cfSel)){
	   	sessionContainer.setAttribute("cfRicerca",cfSel);
	   }
	   //alert(cpi);
	   descComuniSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descComuniSel");
	   if (StringUtils.isEmpty(descComuniSel)){
		   descComuniSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "descComuniSel");
	   }
	   if (!StringUtils.isEmpty(descComuniSel)){
	   	sessionContainer.setAttribute("descComuniRicerca",descComuniSel);
	   }
	   denominazioneSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "denominazioneSel");
	   if (StringUtils.isEmpty(denominazioneSel)){
		   denominazioneSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "denominazioneSel");
	   }
	   if (!StringUtils.isEmpty(denominazioneSel)){
	   	sessionContainer.setAttribute("denominazioneRicerca",denominazioneSel);
	   }
	   
	   codComune=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codComuneSel");
	   if (StringUtils.isEmpty(codComune)){
		   codComune= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "codComuneSel");
	   }
	   if (!StringUtils.isEmpty(codComune)){
			sessionContainer.setAttribute("codComuneRicerca",codComune);
	   }
	   
	   
	   tipoRicerca=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoRicerca");
	   if (StringUtils.isEmpty(tipoRicerca)){
		   tipoRicerca= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "tipoRicerca");
	   }
	   if (!StringUtils.isEmpty(tipoRicerca)){
			sessionContainer.setAttribute("tipoRicercaRicerca",tipoRicerca);
	   }
	   
	   //**************************************IMPOSTATI IN SESSIONE************
	   
	  if (sessionContainer.getAttribute("cfRicerca")!=null){
		  cfSel=(String)sessionContainer.getAttribute("cfRicerca").toString();
	  }
	  
	  if (sessionContainer.getAttribute("descComuniRicerca")!=null){
		  descComuniSel=(String)sessionContainer.getAttribute("descComuniRicerca").toString();
	  }
	  if (sessionContainer.getAttribute("denominazioneRicerca")!=null){
		  denominazioneSel=(String)sessionContainer.getAttribute("denominazioneRicerca").toString();
	  }
	  if (sessionContainer.getAttribute("codComuneRicerca")!=null){
		  codComune=(String)sessionContainer.getAttribute("codComuneRicerca").toString();
	  }
	  if (sessionContainer.getAttribute("tipoRicercaRicerca")!=null){
		  tipoRicerca=(String)sessionContainer.getAttribute("tipoRicercaRicerca").toString();
	  }
	  

	  
	
	String goBackInf = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla lista";
		goBackUrl = "PAGE=VisualizzaSogAccVoucherPage&cdnfunzione=" + cdnfunzioneStr;
		goBackUrl +="&cfSel="+cfSel;
		goBackUrl +="&denominazioneSel="+denominazioneSel;
		goBackUrl +="&descComuniSel="+descComuniSel;
		goBackUrl +="&codComuneSel="+codComune;
		goBackUrl +="&tipoRicerca="+tipoRicerca;
	}			
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<script type="text/javascript"  src="../../js/script_comuni.js">

<%@ include file="../global/fieldChanged.inc" %>



<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già  in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		goTo("<%= goBackUrl %>");
	}

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

	var flagChanged = false;  
    function fieldChanged() {
	    <% if ((!flag_insert) && (canModify)) { out.print("flagChanged = true;");}%>
	}

	function callInsertAccreditamento()
	{
		 
		 
		var elem = document.form.codazioniMisura;
		
		if(elem.selectedIndex==0){
			alert("Valorizzare il campo Misura");
			 document.form.codazioniMisura.focus();
			return false;
		}
 
 		if (!isRequired('dataDal') || !validateDate('dataDal')){
			return false;
		}
 		if (!isRequired('dataAl') || !validateDate('dataAl')){
			return false;
		}
 
		 var objDataDal = document.form.dataDal;
		 var objDataAl = document.form.dataAl;
 		if(compareDate(objDataDal.value, objDataAl.value)==1){
 			alert(objDataDal.title + " maggiore di " + objDataAl.title);
 	      	document.form.dataDal.focus();
 			return false;
 		}
  
		 
		var    url = "AdapterHTTP?PAGE=InsertAccreditamentoPage" 
			   url += "&cfSel="+document.form.codiceFiscale.value;
		   	   url += "&codSedeSel="+document.form.sede.value;
		   	   url += "&PrgAzioniMisuraSel="+document.form.codazioniMisura.value;
			   url += "&dataDalSel="+document.form.dataDal.value;
			   url += "&dataAlSel="+document.form.dataAl.value;
			   url += "&denominazioneSel="+document.form.denominazione.value;
			   url += "&codComuneSel="+document.form.codComuni.value;
			   url += "&indirizzoSel="+document.form.indirizzo.value;
			   url += "&noteStampaSel="+document.form.noteStampa.value;
			   url += "&descComuneSel="+document.form.strCom.value;
			
		  //alert("Il valore di url "+url);
		  setWindowLocation(url);
	  
			  
	}
	
	function callUpdateAccreditamento()
	{
		 //alert(document.form.codiceFiscale.value);
		 //alert(document.form.codiceFiscaleHidden.value);
		 
		var    url = "AdapterHTTP?PAGE=UpdateAccreditamentoPage" 
			   url += "&cfSel="+document.form.codiceFiscale.value;
		   	   url += "&codSedeSel="+document.form.sede.value;
		   	   url += "&denominazioneSel="+document.form.denominazione.value;
			   url += "&codComuneSel="+document.form.codComuni.value;
			   url += "&indirizzoSel="+document.form.indirizzo.value;
			   url += "&noteStampaSel="+document.form.noteStampa.value;
			   url += "&descComuneSel="+document.form.strCom.value;
			
		  //alert("Il valore di url "+url);
		  setWindowLocation(url);		  
	}

<%
	// Genera il Javascript che si occuperà  di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">



<p class="titolo"><%= titolo %></p>
<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages  prefix="M_CONFIRM_NEW_OPERATION_ACCREDITAMENTO" />
<af:showMessages  prefix="M_CONFIRM_UPDATE_ACCREDITAMENTO" />
<af:showMessages  prefix="M_CONFIRM_DELETE_ACCREDITAMENTO" />
<af:showMessages />
</font>



	<af:form name="form" action="AdapterHTTP" method="POST">

	<input type="hidden" name="PAGE" value=""/>
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>"/>
	<input type="hidden" name="codiceFiscaleHidden" value="<%= codfiscale %>"/>
	<input type="hidden" name="codiceSedeHidden" value="<%= codSede %>"/>
	<input type="hidden" name="codiceComune" value="<%= codComuni %>"/>
	<input type="hidden" name="noteStampa" value="<%= noteStampa %>"/>
	
	
	
	<%= htmlStreamTop %>
	<p align="center">
	<table class="main">
	


	<%--
		Campi  degli accreditamenti 
	--%>
	<%-- ***************************************************************************** --%>
	
	<tr>
			
	<td class="campo">
	
	
		<p align="center">
		<table   border="0" cellspacing="0" cellpadding="1px" align="center"  >
			
			
			 <tr>
		       <td  class="etichetta"  align="center">
					Soggetto Accreditato
			   </td>
			   <td class="etichetta" >&nbsp;</td>
			   <td class="etichetta" >&nbsp;</td>
			   <td class="etichetta" >&nbsp;</td>
			   <td class="etichetta" >&nbsp;</td>
			</tr>
		    <tr>
		      <td  class="etichetta" width="10%" align="right">
					Codice Fiscale
				</td>
			    <td  width="40%" class="campo" align="left">
					<af:textBox classNameBase="input" name="codiceFiscale" type="text" readonly="true" size="16" value="<%=codfiscale%>" />
				 </td>
				 <td  class="etichetta" width="10%" align="right">
					Denominazione
				</td>
				 <td  width="20%" class="campo" >
					<af:textBox classNameBase="input" name="denominazione" maxlength="100" type="text" readonly="false" size="25" value="<%=denominazione%>" />
				</td>
		   </tr>
		   <br/>
		   <tr>
				<td  class="etichetta" width="15%" align="right">
				Sede
				</td>
				<td class="campo" width="10%" align="left">
					<af:textBox classNameBase="input" name="sede" type="text" readonly="true" size="10" value="<%=codSede.toUpperCase()%>" />
				</td>
				<td  class="etichetta" width="15%" align="right">
				Indirizzo
				</td>
				 <td  width="20%" class="etichetta" align="left" >
					<af:textBox classNameBase="input" name="indirizzo" maxlength="60" type="text" readonly="false" size="25" value="<%=indirizzo%>" />
				</td>
		   </tr>
		   <tr>
		   			<td class="etichetta">Comune&nbsp;</td>
   								 <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.form.codComuni, document.form.codComHid, document.form.strCom, document.form.strComHid, document.form.strCap, document.form.strCapHid, 'codice');" 
                                  type="text" name="codComuni" value="<%=codComuni%>" title="codice comune "
                                  size="4" maxlength="4" validateWithFunction="" readonly="false"/>&nbsp;
    
    							<A HREF="javascript:btFindComuneCAP_onclick(document.form.codComuni, 
                                                document.form.strCom, 
                                                document.form.strCap, 
                                                'codice','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                                                   
    							<af:textBox type="hidden" name="codComHid" value="<%=codComuni%>"/>
    				</td>	
    			
    				<td class="campo">	
    										<af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.form.codComuni, document.form.codComHid, document.form.strCom, document.form.strComHid, document.form.strCap, document.form.strCapHid, 'descrizione');"
                							type="text" name="strCom" value="<%=strCom%>" size="50" maxlength="50" title="descrizione comune "
                							inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                							readonly="false"/>
    				</td>
    				<td class="campo">
    							<A HREF="javascript:btFindComuneCAP_onclick(document.form.codComuni, 
                                                document.form.strCom, 
                                                document.form.strCap, 
                                                'descrizione','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
   					</td>
   					<td class="campo">
    							<af:textBox type="hidden" name="strComHid" value="<%=strCom%>" />
								<af:textBox type="hidden" name="strCap" value="<%=strCap%>" />
    				</td>
		   </tr>
		  <tr>
			<td class="etichetta" >&nbsp;</td>
		 </tr>
		   <tr>
				<td  class="etichetta"  width="100%"  colspan="4">
					<af:list moduleName="M_ListaMisureSedeSoggetto"  />
				</td>
		    </tr>
		   		
		  	</table>
		  	</p>
  	
 	</td>
	</tr>
	<tr>
			<td class="etichetta" >&nbsp;</td>
	 </tr>
	<tr>
			<p align="center">
			<table  frame="box" rules="none" align="center" >
			
			<tr>
				<td class="etichetta"  style="text-align: center;" colspan="7" >Nuova Misura</td>
 			</tr>
			<tr>
				<td class="etichetta" colspan="7">&nbsp;</td>
			 </tr>
			<tr>
				<td class="campo" style="width: auto;" >Misura&nbsp;</td>
				<td class="campo" style="width: auto;">
					<af:comboBox classNameBase="input" title="Misura" name="codazioniMisura" moduleName="M_ELENCO_AZIONI" addBlank="true" selectedValue="<%=codazioneMisura%>" />
				</td>
			 
			<td class="campo" style="width: auto;">&nbsp;Valido dal</td>
				 
					<td class="campo" style="width: 15% !important;">	
					<af:textBox classNameBase="input"  name="dataDal" onKeyUp="fieldChanged();" validateOnPost="true" type="date" title="Valido dal" size="10" value="<%=dataDal%>" maxlength="10" />
					</td>
					<td class="campo" style="width: auto;">	&nbsp;al&nbsp;</td>
				<td class="campo" style="width: 15% !important;">
					<af:textBox classNameBase="input" name="dataAl" onKeyUp="fieldChanged();" validateOnPost="true" type="date" title="valido al"  size="10" value="<%=dataAl%>"  maxlength="10" />
				</td>
				<td class="campo" style="width: auto;" >
		 
	            	
					<input type="button" class="pulsante" name="back" value="Nuova Misura" onclick="callInsertAccreditamento()" />
				 
				</td>
				</tr>
				
			 </table>
			  </p>
		 
			<p align="center">
			<table   border="0" cellspacing="0" cellpadding="1px" align="center"  >
			
			 
			
		   </table>
		   </p>
		   
		   <p align="center">
			<table border="0" cellspacing="0" cellpadding="1px" align="center"  >
			
			<tr>		
				<td class="etichetta"  align="right"  width="50%">&nbsp;</td>			
				<td class="campo"  align="left"   width="50%">
					<input type="button" class="pulsante" name="back" value="<%= goBackInf %>"onclick="goBack()" />
	        
					&nbsp;
					<input type="button" class="pulsante" name="back" value="Salva Anagrafica" onclick="callUpdateAccreditamento()" />
				</td>
		   </tr>
		   
			
		   </table>
		   </p>
	
	 
	 </tr>
	
	 </table>
	  </p>
	<%= htmlStreamBottom %>
	
</af:form>
</body>
</html>	



