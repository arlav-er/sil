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
	
	String  titolo = "Dettaglio dei Budget";
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
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaPage";
	String goBackInf = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla lista";
		goBackUrl = "PAGE=VisualizzaTotaliBudgetPage&cdnfunzione=" + cdnfunzioneStr;
		goBackUrl +="&AnnoSel="+annoSel;
		goBackUrl +="&codiceCPISel="+codiceCpiSel;
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
   
   String cpi=" ";
   String anno=" ";
   String totBudgetEuro;
   String importoImpEuro;
   String importoSpeso;
   String importoResiduoEuro;
   
   
   cpi  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODCPI");
   if (StringUtils.isEmpty(cpi)){
	   cpi= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "codCpi");
	   if (StringUtils.isEmpty(cpi)){
		   cpi=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel");
	   }
   }
   //alert(cpi);
   anno=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "NUMANNOBUDGET");
   if (StringUtils.isEmpty(anno)){
	   anno= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "NUMANNOBUDGET");
	   if (StringUtils.isEmpty(anno)){
		   anno= SourceBeanUtils.getAttrStrNotNull(serviceRequest, "AnnoSel");
	   }
   }
   //alert(anno);
   SourceBean totaliDettaglio = (SourceBean) serviceResponse.getAttribute("M_CampiDettaglioTotaliBudget.ROWS.ROW");
   importoImpEuro=SourceBeanUtils.getAttrStrNotNull(totaliDettaglio, "impegnatoCpi");
   importoSpeso=SourceBeanUtils.getAttrStrNotNull(totaliDettaglio, "spesoCpi");
   importoResiduoEuro=SourceBeanUtils.getAttrStrNotNull(totaliDettaglio, "residuoCpi");
   totBudgetEuro=SourceBeanUtils.getAttrStrNotNull(totaliDettaglio, "totBudgetCpi");
    
   
  /*  importoImpEuro=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "importoTot");
   BigDecimal impegnatoTotBD= new BigDecimal((String)sessionContainer.getAttribute("impegnatoTot"));
   importoImpEuro=impegnatoTotBD.toString();
   if (StringUtils.isEmpty(importoImpEuro)){
	   importoImpEuro=SourceBeanUtils.getAttrStrNotNull(serviceResponse, "importoTot");
   }
   
   importoSpeso=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "speso");
   BigDecimal importoSpesoBd=new BigDecimal((String)sessionContainer.getAttribute("spesoTot"));
   importoSpeso=importoSpesoBd.toString();
   if (StringUtils.isEmpty(importoSpeso)){
	   importoSpeso=SourceBeanUtils.getAttrStrNotNull(serviceResponse, "speso");
   }
   
   importoResiduoEuro=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "residuo");
   BigDecimal importoResiduoEurBD=new BigDecimal((String)sessionContainer.getAttribute("residuoTot"));
   importoResiduoEuro=importoResiduoEurBD.toString();
   if (StringUtils.isEmpty(importoResiduoEuro)){
	   importoResiduoEuro=SourceBeanUtils.getAttrStrNotNull(serviceResponse, "residuo");
   }
   
   totBudgetEuro=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "TotBudget");
   BigDecimal totBudgetEuroBD=new BigDecimal((String)sessionContainer.getAttribute("TotBudget"));
   totBudgetEuro=totBudgetEuroBD.toString();
   if (StringUtils.isEmpty(totBudgetEuro)){
	   totBudgetEuro=SourceBeanUtils.getAttrStrNotNull(serviceResponse, "TotBudget");
   } */
   
   //alert(importoResiduoEuro);
    String dataOperazione="";
    String tipoOperazione="";
    String importoOperEuro="";
    String descrizioneCPI="";
    
    descrizioneCPI=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DESCRIZIONECPI");
    if (StringUtils.isEmpty(descrizioneCPI)){
    	descrizioneCPI=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descrizioneCpiSel");
    	//STRDESCRIZIONE
    	 if (StringUtils.isEmpty(descrizioneCPI)){
    		 descrizioneCPI=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "STRDESCRIZIONE");
    	 }
    }
    
    SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_LISTADETTAGLIOTOTALIBUDGET");
   	 Vector vectListaDett= serviceResponse.getAttributeAsVector("M_LISTADETTAGLIOTOTALIBUDGET.ROWS.ROW");
	 if ( (vectListaDett != null) && (vectListaDett.size() > 0) ) {
	 	
		 
		 	//SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_LISTADETTAGLIOTOTALIBUDGET");
		 	
		 	for(int i=0;i<vectListaDett.size();i++){
			
		 		SourceBean row = (SourceBean)vectListaDett.get(i);
			
			
			
				if (row!=null && row.getAttribute("DATOPERAZIONE") != null){
					dataOperazione=row.getAttribute("DATOPERAZIONE").toString();
				}
				
				
				if (row!=null && row.getAttribute("OPERDESCR") != null){
					tipoOperazione=row.getAttribute("OPERDESCR").toString();
				}
				
				if (row!=null && row.getAttribute("DECIMPORTO") != null){
					importoOperEuro=row.getAttribute("DECIMPORTO").toString();
				}
				
				
				
			
		 	}
	 
	 }
   
   
	
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>



<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("<%= goBackUrl %>");
	}
	
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

	function goCallInserisciOperazione(){
		//alert("Sono dentro callInserisci Operazione");
		//alert(document.form.descrizioneCpi.value);
		 var url = "AdapterHTTP?PAGE=CallInsertOperationPage" 
			 url += "&AnnoSel="+document.form.anno.value;
		   	 url += "&codiceCPISel="+document.form.CODCPI.value;
		     url += "&dataOperazioneoSel="+document.form.dataOperazione.value;
		     url += "&tipoOperazioneSel="+document.form.tipoOperazione.value;
		     url += "&importoOperazioneEuroSel="+document.form.importoOperEuro.value;
		     url += "&descrizioneCpiSel="+document.form.descrizioneCpi.value;
		  // alert(url);
		   setWindowLocation(url);
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
<af:showMessages />
</font>


	<%= htmlStreamTop %>
	<af:form name="form" action="AdapterHTTP" method="POST">

	<af:textBox type="hidden" name="PAGE" value="CercaBudgetPerCpiPage" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />	
	<af:textBox type="hidden" name="ANNO" value="<%= anno %>" />
	<af:textBox type="hidden" name="CODCPI" value="<%= cpi %>" />
	<%-- <af:textBox type="hidden" name="IMPEGNATO" value="<%= importoImpEuro %>" />
	<af:textBox type="hidden" name="IMPORTOTOT" value="<%= totBudgetEuro %>" />
	<af:textBox type="hidden" name="RESIDUO" value="<%= importoResiduoEuro %>" />
	<af:textBox type="hidden" name="SPESO" value="<%= importoSpeso %>" /> --%>
	<af:textBox type="hidden" name="dataOperazione" value="<%= dataOperazione %>" />
	<af:textBox type="hidden" name="tipoOperazione" value="<%= tipoOperazione %>" />
	<af:textBox type="hidden" name="importoOperEuro" value="<%= importoOperEuro %>" />
	<af:textBox type="hidden" name="descrizioneCpi" value="<%= descrizioneCPI %>" />
	
	<table class="main">
	
	<tr>
	<td align="left">
	
	<div style="width:900px; height:100px; background:#e8f3ff; border:1px solid black;">
	<p align="center">
		<table  align="center"  border="0" cellspacing="0" cellpadding="1px" align="center" maxwidth="55%" >
		
	    <tr>
	       <td   class="etichetta" >
				CPI:
				<af:textBox classNameBase="input" name="descrizionedelCpi" type="text" readonly="true" size="20" value="<%=descrizioneCPI%>" />
		   </td>
		    <td  class="etichetta" >
			Anno:
			&nbsp;
			<af:textBox classNameBase="input" name="anno" type="text" readonly="true" size="5" value="<%=anno%>" />
			 </td>
			 <td   class="etichetta" >
				Totale Budget Euro:
				&nbsp;
				<af:textBox classNameBase="input" name="totBudgetEuro" type="text" readonly="true" size="10" value="<%=totBudgetEuro%>" />
			</td>
	   </tr>
	   <br/>
	   <tr>
			<td  class="etichetta"  >
			Importo Impegnato Euro:
			<af:textBox classNameBase="input" name="importoImpEuro" type="text" readonly="true" size="10" value="<%=importoImpEuro%>" />
			</td>
			<td  class="etichetta"  >
			Importo Speso:
			&nbsp;
			<af:textBox classNameBase="input" name="importoSpeso" type="text" readonly="true" size="10" value="<%=importoSpeso%>" />
			</td>
			<td  class="etichetta"    colspan="2">Importo Residuo Euro:
			&nbsp;
			<af:textBox classNameBase="input" name="importoResiduoEuro" type="text" readonly="true" size="10" value="<%=importoResiduoEuro%>" />
			</td>
	   </tr>
	   
	   		
	  	</table>
	  	</p>
  	<br/>
 	 </div>
	 
	 </td>
	</tr>
	
	<%-- ***************************************************************************** --%>
	

	<tr>
			
			<td class="campo">
					<af:list moduleName="M_ListaDettaglioTotaliBudget"  />
			</td>
	</tr>
	<tr>
		<td class="campo">&nbsp;</td>
	</tr>
	
	

	
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="<%=goBackInf%>" onclick="goBack()" />
            	&nbsp;
				<input type="button" class="pulsante" name="nuovaOpe" value="Nuova Operazone"
						onclick="goCallInserisciOperazione()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	

	
	
 </table>
</af:form>
<%=htmlStreamBottom%>
</body>
</html>	
