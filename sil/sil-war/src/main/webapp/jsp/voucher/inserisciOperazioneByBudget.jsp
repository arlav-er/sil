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
<%
	
	//flag booleano che discrimina l'inserimento o la modifica
	boolean flag_insert=false;
	String  titolo = "Effettua operazioni sul budget";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");

	

	// CONTROLLO ACCESSO ALLA PAGINA
	/* Commentato per test
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	*/

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	
	 String annoSel=" ";
	 String codiceCpiSel=" ";
	 
	 annoSel  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "AnnoSel");
	 if (StringUtils.isEmpty(annoSel)){
		   annoSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "AnnoSel");
	 }
	  
	 codiceCpiSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel");
	 if (StringUtils.isEmpty(codiceCpiSel)){
		   codiceCpiSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "codiceCPISel");
	  }
	
	 
	 String descrCpi=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descrizioneCpiSel");
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaPage";
	String goBackInf = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla lista";
		goBackUrl = "PAGE=DettaglioTotaliBudgetPage&cdnfunzione=" + cdnfunzioneStr;
		goBackUrl +="&AnnoSel="+annoSel;
		goBackUrl +="&codiceCPISel="+codiceCpiSel;
		goBackUrl +="&descrizioneCpiSel="+descrCpi;
		goBackUrl +="&codcpi="+codiceCpiSel;
		goBackUrl +="&NUMANNOBUDGET="+annoSel;
	}

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
	
	
   //Gestire qui la risposta del modulo 	
   //String dataOperazione=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dataOperazioneoSel");
   //if (StringUtils.isEmpty(dataOperazione)){
	 //  dataOperazione= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "dataOperazioneoSel");
   //}
   
   String dataOperazione="";
   
   /*
   String descrCpi=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descrizioneCpiSel");
   String newDescrCpi = "";
   String cpiTotale="";
   if (descrCpi.endsWith("-")){
		int indice = descrCpi.indexOf("-");
		newDescrCpi = descrCpi.substring(0, descrCpi.length()+1);
	}
   if (!StringUtils.isEmpty(newDescrCpi)){
  	 cpiTotale=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel")+"-"+newDescrCpi;
   }
   else{
	   cpiTotale=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel")+"-"+descrCpi;
   }
   */
  
   
   SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_CONFIRM_NEW_OPERATION_BUDGET");
  
	//a che serve leggere i parametri dell'inserimento????
	/* if (StringUtils.isEmpty(dataOperazione)){
	
		if (cont!=null && cont.getAttribute("dataOperazioneoSel") != null){
			dataOperazione=cont.getAttribute("dataOperazioneoSel").toString();
		}
	 } */
	   
   String cpicodice=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel");
 
   
   if (StringUtils.isEmpty(descrCpi)){
	   if (cont!=null &&  cont.getAttribute("descrizioneCpiSel") != null){
	   		descrCpi=cont.getAttribute("descrizioneCpiSel").toString();
	   }
   }
   
   String anno=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "AnnoSel");
   String importoOperEuro=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "importoOperazioneEuroSel");
   if (StringUtils.isEmpty(importoOperEuro)){
	   importoOperEuro= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "importoOperazioneEuroSel");
	   if (StringUtils.isEmpty(importoOperEuro)){
		   if ( cont!=null && cont.getAttribute("importoOperazioneEuroSel") != null){
		   		importoOperEuro=cont.getAttribute("importoOperazioneEuroSel").toString();
		   }
	   }
   }
   
   String importoOper="";
   String altroCpi="";
   String tipoOperazione=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoOperazioneSel");
 
   

	

%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>



<script language="Javascript">

	

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("<%= goBackUrl %>");
	}

	function callConfirmOperBudgetCPI()
	{  
		 
	   if(document.form.dataOperazione.value=='' || document.form.importoOperEuro.value=='' || document.form.tipoOperazione.value==''){
		   alert("Per confermare l'operazione è necessario aver indicato almeno la Data Operazione, l'importo e il tipo di operazione.");
		   return false;
	   }
	   if(!isRequired('dataOperazione') || !validateDate('dataOperazione'))  {
			 return false;
		}
  		if( !isRequired('importoOperEuro') || !validateInteger('importoOperEuro')){
			  return false;				  	
		}
		if(!isRequired('tipoOperazione')){
			return false;
		}
  		//alert("sto chiamando i totali");
  		var url="AdapterHTTP?PAGE=InsertConfermaOperazioneBudgetPage";
  	 	url += "&AnnoSel="+document.form.anno.value;
   		url += "&descrCPISel="+document.form.cpi.value;
   		url += "&codiceCPISel="+document.form.codiceCPISel.value;
   		url += "&dataOperazionSel="+document.form.dataOperazione.value;
   		url += "&importoOperEuroSel="+document.form.importoOperEuro.value;
   		url += "&descrizioneCpiSel="+document.form.cpi.value;
		url += "&codOperazioneSel="+document.form.tipoOperazione.value;
   		url += "&altriCpieSel="+document.form.altriCpi.value;
   		//alert("Il valore di url "+url);
   		setWindowLocation(url);
	}
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}
	
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
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
<af:showMessages  prefix="M_CONFIRM_NEW_OPERATION_BUDGET" />
</font>


	<%= htmlStreamTop %>
	<af:form name="form" action="AdapterHTTP" method="POST"   >

	<af:textBox type="hidden" name="PAGE" value="CercaBudgetPerCpiPage" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />	
	<af:textBox type="hidden" name="codiceCPISel" value="<%= cpicodice %>" />
	
	<table class="main">
	
	<tr>
	<td align="left">
	
		<p align="center">
		<table align="center" border="0" cellspacing="0" cellpadding="1px"  >
		
	    <tr>
	       <td   class="etichetta"  align="right" >
				CPI Coinvolto:
		   </td>
		   <td   class="campo"  align="right" >
				<af:textBox classNameBase="input" name="cpi" type="text" readonly="true" size="20" value="<%=descrCpi%>" />
		   </td>
		    <td   class="etichetta"   align="left" >
				Anno:
		   </td>
			  <td   class="campo"   align="right" >
				<af:textBox classNameBase="input" name="anno" type="text" readonly="true" size="4" value="<%=anno%>" />
			 </td>
			
	   </tr>
	    <tr>
	    	<td   class="etichetta"  colspan="2" align="center" >
				&nbsp;
			</td>
	    </tr>
	   <tr>
			<td  class="etichetta" >Data Operazione:</td>
			<td class="campo" width="25%"   align="right">
				<af:textBox title="Data Operazione" classNameBase="input"  onKeyUp="fieldChanged();" validateOnPost="true" type="date"  name="dataOperazione"  size="10" value="<%=dataOperazione%>"  maxlength="10"  />
			</td>
			<td   class="etichetta"     align="left" >
				&nbsp;
			</td>
	  </tr>
	   <tr>
	    	<td   class="etichetta"   colspan="2" align="center" >
				&nbsp;
			</td>
	   </tr>
	   <tr>
	   		
			<td  class="etichetta"   align="left" >
				Importo Operazione Euro:
			</td>
			<td  class="campo"    align="right">
				<af:textBox title="Importo Operazione Euro" classNameBase="input" name="importoOperEuro" type="number" readonly="false" size="10" value="" maxlength="10" />
				&nbsp;
				<af:comboBox 
		 				 classNameBase="input"  
		 				 title="Tipo Operazione"
		  				 name="tipoOperazione" 
		 			 	 moduleName="M_IMP_OPERAZ"  
		 				 selectedValue="<%=tipoOperazione%>" 
		  				 addBlank="true"
		  		/>	
			</td>
			
			
			
	    </tr>
	   
	  	</table>
  		</p>
	 
	 </td>
	</tr>
	
	<%-- ***************************************************************************** --%>
	<p align="center">
	<table  align="center" border="0" >
	

	<tr>
			
			<td class="campo">
					<div style="width:1000px; height:220px; background:#e8f3ff; border:1px solid black;">
						<br>
						<table  align="center"  border="0" >
							<tr>
								<td   class="campo" align="right"  colspan="2" >
									Selezionando "Altro CPI Coinvolto nell'Operazione",   a parità di anno, per questo CPI viene creata un'operazione complementare a quella principale.
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table  align="center"  border="0" >
							<tr>
								<td width="50%" class="campo" align="right"  colspan="2">
									Questo consente di effettuare uno storno tra i budget dei CPI.
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table  align="center"  border="0" >
							<tr>
								<td  class="campo" align="right"   colspan="2">
								  Altro CPI Coinvolto nell'operazione &nbsp;
								  <af:comboBox classNameBase="input" title="altro cpi" name="altriCpi" 
									moduleName="M_ALTRI_CPU"  addBlank="true" selectedValue="<%=altroCpi%>" />
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
					</div>
			</td>
	</tr>
	<tr>
		<td class="campo">&nbsp;</td>
	</tr>
	
	</table>
	</p>

	
	<table  align="center"  border="0" >
	

	
		<tr>
			<td colspan="2" align="center">
				<input type="button" class="pulsante" name="back" value="<%= goBackInf %>" onclick="goBack()" />
            	&nbsp;
				<input type="button" class="pulsante" name="confermaOpe" value="Conferma Operazione"
						onclick="callConfirmOperBudgetCPI()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	

	</table>
	
 </table>
</af:form>
<%=htmlStreamBottom%>
</body>
</html>	
