<%@page import="it.eng.sil.module.conf.did.ConferimentoUtility"%>
<%@ page
	contentType="text/html;charset=utf-8"
	
	import="javax.xml.datatype.XMLGregorianCalendar,
			com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			it.gov.lavoro.servizi.servizicoap.richiestasap.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Cruscotto Conferimento DID";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
 

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);
	//TODO
	boolean canVerificaSAP = attributi.containsButton("VERIFICA_ESISTENZA_SAP");
 	boolean canConvalidaDid = attributi.containsButton("CONVALIDA_DID_MINISTERIALE");
 	boolean canRevocaDid = attributi.containsButton("REVOCA_DID_MINISTERIALE");
 	boolean canNuovoConferimento = attributi.containsButton("NEW_CONF_DID_MINISTERIALE"); 
 	
	boolean isConvalida=false;
	boolean isRevoca=false;
	boolean isNuovoConf=false;
	boolean conferisciDidSil = false;
	boolean isDettaglioConf = false;
	
	boolean canModify = true;	 
	
	String dataDidSil ="";
	String tipologiaPatto ="";
	String dataPatto = "";
	String profilignPatto ="";
	String dataDichInps = "";
	String dataNotificaInps = "";
	String tipoOperInps = "";
	String confDid_statoInvio ="";
	String confDid_tipoEvento ="";
	Vector rowInfoDid  = serviceResponse.getAttributeAsVector("M_CCD_GET_DID.ROWS.ROW");
	if (rowInfoDid != null && !rowInfoDid.isEmpty()) {
		SourceBean didBean = (SourceBean) rowInfoDid.elementAt(0);
		dataDidSil = StringUtils.getAttributeStrNotNull(didBean, "dataDid");
		conferisciDidSil = true;
	}else{
		isNuovoConf =true;
	}
	Vector rowInfoPatto = serviceResponse.getAttributeAsVector("M_CCD_GET_PATTO.ROWS.ROW");
	if (rowInfoPatto != null && !rowInfoPatto.isEmpty()) {
		SourceBean didPatto = (SourceBean) rowInfoPatto.elementAt(0);
		dataPatto = StringUtils.getAttributeStrNotNull(didPatto, "dataPatto");
		tipologiaPatto =StringUtils.getAttributeStrNotNull(didPatto, "tipoPatto");
		profilignPatto = Utils.notNull(didPatto.getAttribute("profilingPattoNum"));
	}
	Vector rowConferimento = serviceResponse.getAttributeAsVector("M_CCD_GET_Conferimento_Did.ROWS.ROW");
	String prgConferimentoDid = null;
	if (rowConferimento != null && !rowConferimento.isEmpty()) {
		SourceBean confDID = (SourceBean) rowConferimento.elementAt(0);
		prgConferimentoDid = StringUtils.getAttributeStrNotNull(confDID, "strPrgConfDid");
		confDid_statoInvio = StringUtils.getAttributeStrNotNull(confDID, "strCodMonoStatoInvio");
		confDid_tipoEvento =StringUtils.getAttributeStrNotNull(confDID, "Codpftipoevento");
		isDettaglioConf = true;
 	}
	Vector rowInfoDidInps = serviceResponse.getAttributeAsVector("M_CCD_GET_DID_INPS.ROWS.ROW");
	if (rowInfoDidInps != null && !rowInfoDidInps.isEmpty()) {
		SourceBean didInps = (SourceBean) rowInfoDidInps.elementAt(0);
		dataDichInps = StringUtils.getAttributeStrNotNull(didInps, "DATDICHIARAZIONE");
		dataNotificaInps =StringUtils.getAttributeStrNotNull(didInps, "Datricezione");
		tipoOperInps =StringUtils.getAttributeStrNotNull(didInps, "CODMONOTIPOOPERAZIONE"); 			
	}
	boolean existSap = false;
	String servizioDaInvocare = "";
	String codMinSap = "";
	String codstatoSAP = "";
	String statoSAP = "";
	String dtInvioMin = "";
	String dtInizioVal = "";
	String dtFineVal = "";
	
	//LavoratoreType lavT =null;
	ListaDIDType lavT1 = null;
   	if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.SAPWS") && serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS")!=null){
  		//lavT =  (LavoratoreType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
  		lavT1 =  (ListaDIDType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
  		//if (lavT1 != null && lavT.getDatiinvio() != null) {
  		//	codMinSap = lavT.getDatiinvio().getIdentificativosap();
  		//}
    }
   	if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.CODMINSAPWS") && serviceResponse.getAttribute("M_CCD_GetSituazioneSap.CODMINSAPWS")!=null){
   		codMinSap = serviceResponse.getAttribute("M_CCD_GetSituazioneSap.CODMINSAPWS").toString();
   	}
   	if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.ESITO_SAP")){
   		isConvalida = true;
   		//isRevoca = true;
   		isNuovoConf = isNuovoConf && true;
   	}
   	
   	SourceBean rowPresaCarico150 = null;
   	boolean viewPresaCarico150 = serviceResponse.containsAttribute("M_CCD_GET_PRESA_IN_CARICO");
   	if (viewPresaCarico150) {
   		rowPresaCarico150 = (SourceBean)serviceResponse.getAttribute("M_CCD_GET_PRESA_IN_CARICO");
   	}
   	
   	String configRiconvalida = serviceResponse.containsAttribute("M_CONFIG_CONF_DID_RICONVALIDA.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_CONFIG_CONF_DID_RICONVALIDA.ROWS.ROW.NUM").toString():"0";
   	
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf(!canModify);

	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
%>

<html>
<head>
<title><%= titolo %></title>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

<script language="Javascript">
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		var divVar = document.getElementById("datiLavoratoreSap");
		<%	// se si proviene dalla pagina di visualizza e confronta, bisogna mostrare i due lavoratori
		// selezionati e confrontati.
		if (serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.ESITO_SAP")) { 
	%>
	  
	 	 divVar.style.display = "";
	<%	
		}else{
	%>
			 divVar.style.display = "none";
	<%	
		} 
	%>
	}
	function conferimentoDidSil()
	{
		 if (isInSubmit()) return;
	      
	      url="AdapterHTTP?PAGE=ConferimentoDIDPage";
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
	      url += "&CODPFTIPOEVENTO="+"<%=ConferimentoUtility.EVENTOINSERIMENTO%>";
	      url += "&DATADICHIARAZIONEDIDSIL="+"<%=dataDidSil%>";

	      setWindowLocation(url);
	}
	function nuovoConferimentoDid()
	  {   
	      // Se la pagina è già in submit, ignoro questo nuovo invio!
	      if (isInSubmit()) return;
	      
	      url="AdapterHTTP?PAGE=ConferimentoDIDPage";
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
	      url += "&CODPFTIPOEVENTO="+"<%=ConferimentoUtility.EVENTOINSERIMENTO%>";
	      setWindowLocation(url);
	  }
	 function dettaglioConferimento()
	  {   
	      // Se la pagina è già in submit, ignoro questo nuovo invio!
	      if (isInSubmit()) return;
	      
	      url="AdapterHTTP?PAGE=ConferimentoDIDPage";
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
	      url += "&prgConferimentoDID="+"<%=prgConferimentoDid%>";     
	      setWindowLocation(url);
	  }		
	

	function settaOperazione(operazione) {
		document.frm1.OPERAZIONECONFERIMENTO.value = operazione;
	}

	function checkControlliAggiuntivi() {
		var operazioneRequest = document.frm1.OPERAZIONECONFERIMENTO.value;
		if (operazioneRequest == 'CONFERMA') {
			var urlProf="AdapterHTTP?PAGE=ConferimentoDIDPage";
			urlProf += "&CDNFUNZIONE="+"<%=_funzione%>";      
			urlProf += "&cdnLavoratore="+"<%=cdnLavoratore%>";
			urlProf += "&CODPFTIPOEVENTO="+"<%=ConferimentoUtility.EVENTOCONVALIDA%>";
			urlProf += "&dataDichSap="+ document.frm1.dataDicSap.value;
			urlProf += "&dataDichNonPresente="+ document.frm1.dataDichNonPresente.value;
			urlProf += "&dataDichSapN00="+ document.frm1.dataPropostaN00.value;
			
		    setWindowLocation(urlProf);
		    return false;
		}else if (operazioneRequest == 'VERIFICA'){
			document.frm1.PAGE.value="SitAttualeConfDIDPage";
			return true;
		}else if (operazioneRequest == 'REVOCA'){
			document.frm1.CODPFTIPOEVENTO.value="<%=ConferimentoUtility.EVENTOREVOCA%>";
			var isDataDichNonPresente = document.frm1.dataDichNonPresente.value;
			var dataConfirm ="";
			if(isDataDichNonPresente.trim() === "SI"){
				dataConfirm = document.frm1.dataPropostaN00.value;
			}else{
				dataConfirm = document.frm1.dataDicSap.value;
			}
			var message = "Si e' sicuri di voler revocare la DID Ministeriale del "+ dataConfirm + "?"
			if(confirm(message)){
				return true;
			}else{
				return false;
			}
		}	
		else {
			return true;
		}
	}

	function callWSSAP() {
	   	if(!confirm("Sicuro di voler invocare la richiesta SAP?")) { 
			return false;
	   	}
	    var urlpage="AdapterHTTP?";
	    urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=SapVisualizzaPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratore%>";
	    window.open(urlpage,'RichiestaSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
	}

	function callInviaSAPTitPresaCarico(dataDidMin) {
		if (isInSubmit()) return;
		
		var dataOdierna = '<%=DateUtils.getNow()%>';
		var f;
      	f = "AdapterHTTP?PAGE=EsitoConferimentoPresaCaricoPage";
      	f = f + "&CDNLAVORATORE=<%=cdnLavoratore%>&CDNFUNZIONE=<%=_funzione%>&trasferisci=true";
      	f = f + "&DATADID=" + dataDidMin;
      	f = f + "&DATATRASFCOMP=" + dataOdierna;
      	
      	setWindowLocation(f);
	}
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">


<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}

%>
 
    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
    	}
    </script>	
<p>
	<font color="green">
		<af:showMessages prefix="M_CCD_GetSituazioneSap"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>
<af:form name="frm1" action="AdapterHTTP" method="POST" onSubmit="checkControlliAggiuntivi()">
<p class="titolo"><%= titolo %></p>

	<input type="hidden" name="PAGE" value="EsitoConferimentoDIDPage"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="OPERAZIONECONFERIMENTO" value="" />
	<input type="hidden" name="CODPFTIPOEVENTO" value="" />

 
<%= htmlStreamTop %>
<table class="main">

<tr>
  <td colspan="4">
    <div class="sezione">DID SIL</div>
   </td>
</tr>      
<tr> 
    <td class="etichetta" nowrap> 
      Data Dichiarazione
    </td>
    <td class="campo">    
      <af:textBox name="datDidSil" value="<%=Utils.notNull(dataDidSil)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data dichiarazione"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
 <tr>
    <td class="etichetta" nowrap> 
      Patto/Accordo
    </td>
    <td class="campo">    
      <af:textBox name="tipoPatto" value="<%=Utils.notNull(tipologiaPatto)%>" classNameBase="input"  
                  readonly="true" required="false" title="Tipologia Patto"  
                  size="40" maxlength="100" />    
	</td>
    <td class="etichetta" nowrap> 
      Data Stipula
    </td>
    <td class="campo">   
      <af:textBox name="datstipSil" value="<%=Utils.notNull(dataPatto)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data stipula aptto"  
                  size="12" maxlength="10" />    
	</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Indice Profiling
    </td>
    <td class="campo">    
      <af:textBox name="profSil" value="<%=Utils.notNull(profilignPatto)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data dichiarazione"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<%if (viewPresaCarico150){ %>
<tr>
  <td colspan="4">
    <div class="sezione">A02 - PATTO DI ATTIVAZIONE ED EVENTUALE PROFILING - Presa in carico D. Lgs. 150</div>
   </td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      CPI
    </td>
    <td class="campo">    
      <af:textBox name="cpiPresaCarico" value='<%=Utils.notNull((String)rowPresaCarico150.getAttribute("CPI"))%>' classNameBase="input" type="date"  
                  readonly="true" required="false" title="CPI"  
                  size="50" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Data colloquio
    </td>
    <td class="campo">    
      <af:textBox name="datColloquioPresaCarico" value='<%=Utils.notNull((String)rowPresaCarico150.getAttribute("DATCOLLOQUIO"))%>' classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data colloquio"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Data inizio
    </td>
    <td class="campo">    
      <af:textBox name="datInizioPresaCarico" value='<%=Utils.notNull((String)rowPresaCarico150.getAttribute("DATINIZIO"))%>' classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data inizio"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Data fine
    </td>
    <td class="campo">    
      <af:textBox name="datFinePresaCarico" value='<%=Utils.notNull((String)rowPresaCarico150.getAttribute("DATFINE"))%>' classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data fine"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Esito
    </td>
    <td class="campo">    
      <af:textBox name="esitoPresaCarico" value='<%=Utils.notNull((String)rowPresaCarico150.getAttribute("ESITO"))%>' classNameBase="input" type="text"  
                  readonly="true" required="false" title="Esito"  
                  size="20" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<%}%>
<tr>
  <td colspan="4">
    <div class="sezione">Ultimo Conferimento DID a sistema</div>
   </td>
</tr>  
<tr>
    <td class="etichetta" nowrap> 
      Stato invio
    </td>
    <td class="campo">    
      <af:textBox name="statoInvioConf" value="<%=Utils.notNull(confDid_statoInvio)%>" classNameBase="input" 
                  readonly="true" required="false" title="Stato Invio"  
                  size="20" maxlength="40" />    
	</td>
	 <td class="etichetta" nowrap> 
      Tipo conferimento
    </td>
    <td class="campo">    
     <af:comboBox name="descrEnteSap" classNameBase="input"
							moduleName="M_CCD_COMBO_MN_PF_TIPO_EVENTO" 
							selectedValue="<%=Utils.notNull(confDid_tipoEvento)%>" 
							addBlank="true"
							disabled ="true"
							required="false" />
	</td>
</tr> 
<tr>
			<%if(canVerificaSAP && isDettaglioConf){ %>
			<td colspan="4">
			<center>
				<input type="button" class="pulsanti" name="dettaglioConferimentoSap" value="Dettaglio" onclick="dettaglioConferimento();" />
 			</center>
			</td>
			<%} %>
		</tr>
<tr>
  <td colspan="4">
    <div class="sezione">DID INPS</div>
   </td>
</tr>  
<tr> 
    <td class="etichetta" nowrap> 
      Data Dichiarazione
    </td>
    <td class="campo">    
      <af:textBox name="datInps" value="<%=Utils.notNull(dataDichInps)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data dichiarazione inps"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Data Notifica
    </td>
    <td class="campo">    
      <af:textBox name="datRic" value="<%=Utils.notNull(dataNotificaInps)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Data notifica"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr> 
    <td class="etichetta" nowrap> 
      Tipo operazione
    </td>
    <td class="campo">    
      <af:textBox name="tipoInps" value="<%=Utils.notNull(tipoOperInps)%>" classNameBase="input" type="date"  
                  readonly="true" required="false" title="Tipologia"  
                  size="12" maxlength="10" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
	<%if(canVerificaSAP){ %>
		<tr>
	
			<td colspan="4">
				<center>
				<input type="submit" class="pulsanti" value="Verifica DID nella SAP Ministeriale" onclick="settaOperazione('VERIFICA');"/>			
				</center>
			</td>
		</tr>
	<%} %>
	 
</table>
<%= htmlStreamBottom %>
		 
	<div id="datiLavoratoreSap" style="display: none">
	<%out.print(htmlStreamTopCoop);%>
	<table class="maincoop">
		<tr>
			<td colspan="2"><div class="sezione2"/>Situazione SAP</td>
		</tr>
		<%if(lavT1!=null){ %>
	 	<tr>
			<td class="etichettacoop" nowrap>Ente Titolare SAP</td>
			<td class="campocoop">
				<b><%=lavT1.getCodiceentetit()%>&nbsp;-&nbsp;</b>
				<af:comboBox name="descrEnteSap" classNameBase="input"
							moduleName="COMBO_ENTETIT" 
							selectedValue="<%=lavT1.getCodiceentetit()%>" 
							addBlank="true"
							disabled ="true"
							required="false" />
			</td>
		</tr>
	 	
	 		<% 
	 		XMLGregorianCalendar dataDichSap = null;
	 		XMLGregorianCalendar dataEventoDidSap = null;
	 		String indiceProfilingSta = null;
			//Statoinanagrafe statoAn = null;
			
			dataDichSap = lavT1.getDisponibilita();
			if(dataDichSap!=null){
 				isConvalida = isConvalida && true;
	 	   		isNuovoConf = isNuovoConf && false;
	 	   		conferisciDidSil = conferisciDidSil && false; 
	 	   	//	isRevoca = isRevoca && true;
 			}else{
 				isConvalida = isConvalida && false;
 				isNuovoConf = isNuovoConf && true;
 				conferisciDidSil = conferisciDidSil && true;
 				//isRevoca = isRevoca && false;
 			}
			dataEventoDidSap = lavT1.getDataevento();
			if (lavT1.getIndiceprofiling() != null) {
				indiceProfilingSta = lavT1.getIndiceprofiling().setScale(2, BigDecimal.ROUND_DOWN).toString();	
			}
// 			if(lavT.getDatiamministrativi()!=null && lavT.getDatiamministrativi().getStatoinanagrafe()!=null){
// 	 			statoAn= lavT.getDatiamministrativi().getStatoinanagrafe();
// 	 			dataDichSap = 	statoAn.getDisponibilita();
// 	 			if(dataDichSap!=null){
// 	 				isConvalida = isConvalida && true;
// 		 	   		isNuovoConf = isNuovoConf && false;
// 		 	   		conferisciDidSil = conferisciDidSil && false; 
// 		 	   	//	isRevoca = isRevoca && true;
// 	 			}else{
// 	 				isConvalida = isConvalida && false;
// 	 				isNuovoConf = isNuovoConf && true;
// 	 				conferisciDidSil = conferisciDidSil && true;
// 	 				//isRevoca = isRevoca && false;
// 	 			}
// 	 			dataEventoDidSap = statoAn.getDataevento();
// 	 			if (statoAn.getIndiceprofiling() != null) {
// 					indiceProfilingSta = statoAn.getIndiceprofiling().setScale(2, BigDecimal.ROUND_DOWN).toString();	
// 				}
// 	 		}else{
// 	 			isConvalida = isConvalida && false;
// 	 		}
	 		%>
	 		
			<tr>
				<td class="etichettacoop" nowrap>Data dichiarazione</td>
				<td class="campocoop">
					<af:textBox name="dataDicSap" type="date" classNameBase="input"
								value="<%=DateUtils.formatXMLGregorian(dataDichSap)%>"
								size="12" maxlength="10"
								validateOnPost="true"
								readonly="true" 
								required="false" />
				</td>
			</tr>
			<tr>
				<td class="etichettacoop" nowrap>Indice profiling</td>
				<td class="campocoop">
					<af:textBox name="indiceProfDatiAmm" type="text" classNameBase="input"
								value="<%=Utils.notNull(indiceProfilingSta)%>"
								size="40" maxlength="101"
								readonly="true" 
								required="false" />
	
				</td>
			</tr>
		<tr>
			<td class="etichettacoop" nowrap>Data Ultimo Evento DID</td>
			<td class="campocoop">
	 			<af:textBox name="datEvDidSap" type="date" classNameBase="input"
							value="<%=DateUtils.formatXMLGregorian(dataEventoDidSap)%>"
							size="12" maxlength="10"
							validateOnPost="true"
							readonly="true" 
							required="false" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<%
		boolean switchEnable = false;
// 		PoliticheAttiveLst allListaP = lavT.getPoliticheAttiveLst();
// 		List listaP = null;
// 		if(allListaP!= null){
// 			listaP = allListaP.getPoliticheAttive();
// 		}else{
// 			listaP = new ArrayList();
// 		}
//		PoliticheAttive itemN00 = null;
		PoliticaAttiva itemN00 = null;
		PoliticaAttiva item = null;
		item = lavT1.getN00();
		String strDataPropN00 = null;
		boolean dataDichNonPresente = false;
		if(dataDichSap==null){
			dataDichNonPresente = true;
		}
		if(item != null){
			String strDataProp = DateUtils.formatXMLGregorian(item.getDataProposta());
			strDataPropN00 = strDataProp;
			if(dataDichSap!=null){
				String strDataDichSap = DateUtils.formatXMLGregorian(dataDichSap);
				if(strDataProp.equalsIgnoreCase(strDataDichSap)){
					itemN00 = item;
					switchEnable = true;
				}
			}else if(dataEventoDidSap!=null){ //controllo se esiste N00 con dataUltimoEvento = dataEventoDid
				String strDataFine = DateUtils.formatXMLGregorian(item.getUltimoEvento().getDataEvento());
				String strDataEventoDid = DateUtils.formatXMLGregorian(dataEventoDidSap);
				if(strDataFine.equalsIgnoreCase(strDataEventoDid)){
					itemN00 = item;
					switchEnable = true;
				}
			}
		}
		
// 		for (int j = 0; j < listaP.size(); j++) {
// 			PoliticheAttive item = (PoliticheAttive) listaP.get(j);
// 			if(item.getTipoAttivita().equalsIgnoreCase("N00")){
// 				if(dataDichSap!=null){
// 					String strDataProp = DateUtils.formatXMLGregorian(item.getDataProposta());
// 					String strDataDichSap = DateUtils.formatXMLGregorian(dataDichSap);
// 					if(strDataProp.equalsIgnoreCase(strDataDichSap)){
// 						itemN00 = item;
// 						switchEnable = true;
// 						break;
// 					}
// 				}else if(dataEventoDidSap!=null){ //controllo se esiste N00 con dataUltimoEvento = dataEventoDid
// 					String strDataFine = DateUtils.formatXMLGregorian(item.getDataFine());
// 					String strDataEventoDid = DateUtils.formatXMLGregorian(dataEventoDidSap);
// 					if(strDataFine.equalsIgnoreCase(strDataEventoDid)){
// 						itemN00 = item;
// 						switchEnable = true;
// 						break;
// 					}
// 				}
// 			}
// 		}
		if(dataDichNonPresente){
		%>
			<input type="hidden" name="dataDichNonPresente" value="SI"/>
		<%
		}else{
		%>
			<input type="hidden" name="dataDichNonPresente" value="NO"/>
		<%
		}
		
		if(switchEnable){
		%>
		<tr>
			<td class="etichettacoop" nowrap>Attivit&agrave;</td>
			<td class="campocoop">
				<b><%=itemN00.getTipoAttivita()%>&nbsp;-&nbsp;</b>
				<af:comboBox name="descrattSap" classNameBase="input"
							moduleName="COMBO_ATTIVITA_POLATT" 
							selectedValue="<%=itemN00.getTipoAttivita()%>" 
							addBlank="true"
							disabled ="true"
							required="false" />
			</td>
		</tr>		
		<tr>
			<td class="etichettacoop" nowrap>Descrizione</td>
			<td class="campocoop">
				<af:textBox name="descrSap" type="text" classNameBase="input"
							value="<%=itemN00.getDescrizione()%>"
							readonly="true" 
							size="100" maxlength="101"
							required="false" />
			</td>
		</tr>		
		<tr>
			<td class="etichettacoop" nowrap>Titolo progetto</td>
			<td class="campocoop">
				<b><%=Utils.notNull(itemN00.getTitoloProgetto())%>&nbsp;-&nbsp;</b>
				<af:comboBox name="descrTitSap" classNameBase="input"
							moduleName="COMBO_TITOLOPROGETTO" 
							selectedValue="<%=Utils.notNull(itemN00.getTitoloProgetto())%>" 
							addBlank="true"
							disabled ="true"
							required="false" />
			</td>
		</tr>	
		<tr>
			<td class="etichettacoop" nowrap>Codice Ente Promotore</td>
			<td class="campocoop">
				<b><%=Utils.notNull(itemN00.getCodiceEntePromotore())%>&nbsp;-&nbsp;</b>
				<af:comboBox name="descrEnteSap" classNameBase="input"
							moduleName="COMBO_ENTETIT" 
							selectedValue="<%=Utils.notNull(itemN00.getCodiceEntePromotore())%>" 
							addBlank="true"
							disabled ="true"
							required="false" />
			</td>
		</tr>	
<%	if(itemN00.getUltimoEvento()!=null){
				String indiceProf = null;
				if(itemN00.getIndiceProfiling()!=null){
					indiceProf = itemN00.getIndiceProfiling().setScale(2, BigDecimal.ROUND_DOWN).toString();	
				}
 				DatiEventoType datiEvento = itemN00.getUltimoEvento();	
 				if(StringUtils.isFilledNoBlank(datiEvento.getEvento())){
 					if(datiEvento.getEvento().equals("01") || datiEvento.getEvento().equals("02") || datiEvento.getEvento().equals("09")){
 						
 						//modifica gennaio 2020
 						if(!dataDichNonPresente){
 	 						isConvalida = isConvalida && true;
 	 						isRevoca = true;
 						}else if(dataDichNonPresente && datiEvento.getEvento().equals("09") && dataEventoDidSap!=null){
 							isConvalida = isConvalida && true;
 	 						isRevoca = true;
 							
 						} else{
 							isRevoca = false;
 							isConvalida = isConvalida && false;
 						}
 						isNuovoConf = isNuovoConf && false;
 						/* old
 						if(dataDichSap!=null){
							isRevoca = true;
						}
 						if(datiEvento.getEvento().equals("01") || datiEvento.getEvento().equals("09")){
 							isConvalida = isConvalida && true;
 						}else{
 							isConvalida = isConvalida && false;
 						} */
 						//fine modifica gennaio 2020
 						
 					}else{
 						isConvalida = isConvalida && false;
 						isRevoca = isRevoca && false;
 						isNuovoConf = isNuovoConf && true;
 					}
 				}
				
			%>
			<tr>
				<td class="etichettacoop" nowrap>Ultimo Evento</td>
				<td class="campocoop">
					<b><%=Utils.notNull(datiEvento.getEvento())%> &nbsp;-&nbsp;</b>				
					<af:comboBox name="descrUlEvSap" classNameBase="input"
								moduleName="COMBO_EVENTO_MINISTERO" 
								selectedValue="<%=Utils.notNull(datiEvento.getEvento())%>" 
								addBlank="true"
								disabled ="true"
								required="false" />
				</td>
			</tr>
			<tr>
				<td class="etichettacoop" nowrap>Data Ultimo Evento</td>
				<td class="campocoop">
				<% String datUlEvsap= null;
					if(datiEvento.getDataEvento()!=null){
						datUlEvsap = DateUtils.formatXMLGregorian(datiEvento.getDataEvento());
					}
				%>
					<af:textBox name="datUlEvsap" type="date" classNameBase="input"
								value="<%=Utils.notNull(datUlEvsap)%>"
								readonly="true" 
								size="12" maxlength="10"
								validateOnPost="true"
								required="false" />
				</td>
			</tr>		
			<tr>
				<td class="etichettacoop" nowrap>Indice Profiling</td>
				<td class="campocoop">
					<af:textBox name="inProfSap" type="text" classNameBase="input"
								value="<%=Utils.notNull(indiceProf)%>"
								readonly="true" 
								size="40" maxlength="101"
								required="false" />
				</td>
			</tr>	
			<tr>
				<td class="etichettacoop" nowrap>Data proposta</td>
				<td class="campocoop">
				<% String datPropN00= null;
					if(itemN00.getDataProposta()!=null){
						datPropN00 = DateUtils.formatXMLGregorian(itemN00.getDataProposta());
					}
				%>
					<af:textBox name="datPropN00" type="date" classNameBase="input"
								value="<%=Utils.notNull(datPropN00)%>"
								readonly="true" 
								size="12" maxlength="10"
								validateOnPost="true"
								required="false" />
				</td>
			</tr>	
			<tr>
				<td class="etichettacoop" nowrap>Data inizio</td>
				<td class="campocoop">
				<% String dataInizioN00= null;
					if(itemN00.getData()!=null){
						dataInizioN00 = DateUtils.formatXMLGregorian(itemN00.getData());
					}
				%>
					<af:textBox name="dataInizioN00" type="date" classNameBase="input"
								value="<%=Utils.notNull(dataInizioN00)%>"
								readonly="true" 
								size="12" maxlength="10"
								validateOnPost="true"
								required="false" />
				</td>
			</tr>	
			<tr>
				<td class="etichettacoop" nowrap>Data fine</td>
				<td class="campocoop">
				<% String dataFineN00= null;
					if(itemN00.getDataFine()!=null){
						dataFineN00 = DateUtils.formatXMLGregorian(itemN00.getDataFine());
					}
				%>
					<af:textBox name="dataFineN00" type="date" classNameBase="input"
								value="<%=Utils.notNull(dataFineN00)%>"
								readonly="true" 
								size="12" maxlength="10"
								validateOnPost="true"
								required="false" />
				</td>
			</tr>			
		<%		}
			}else{
				//politica N00 non è presente - modifica al campo isConvalida segnalazione redmine #6441
				if(!dataDichNonPresente){
					isConvalida = isConvalida && true;
					//modifica gennaio 2020
					isRevoca = true;				
					/*if(dataDichSap!=null){
						isRevoca = true;
					}*/
				}
				
			}
		%>
		<input type="hidden" name="dataPropostaN00" value="<%= strDataPropN00 %>" />
		<%if (serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.VISUALIZZA_A2")) {%>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
			<tr>
				<td class="etichettacoop">Attività</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ATTIVITA"))%>
	                    - <af:comboBox name="attivitaInCarico" multiple="false"
	                        moduleName="COMBO_ATTIVITA_POLATT" disabled="true"
	                        classNameBase="input"
	                        selectedValue='<%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ATTIVITA"))%>' /></b>
	          	</td>
			</tr>
			<tr>
				<td class="etichettacoop">Descrizione</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DESCRIZIONE"))%></b>
				</td>
			</tr>
			<tr>
				<td class="etichettacoop">Titolo Progetto</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.TITOLOPROGETTO"))%>
						- <af:comboBox name="progettoInCarico" multiple="false"
							moduleName="COMBO_TITOLOPROGETTO" disabled="true"
							classNameBase="input"
							selectedValue='<%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.TITOLOPROGETTO"))%>' /></b></td>
			</tr>
			<tr>
				<td class="etichettacoop">Codice Ente Promotore</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ENTEPROMOTORE"))%></b></td>			
			</tr>
			<tr>
				<td class="etichettacoop">Denominazione</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DENOMINAZIONE"))%></b>
				</td>
			</tr>
			<tr>
				<td class="etichettacoop" nowrap>Ultimo Evento</td>
				<td class="campocoop">
				<%
					String codiceUltimoEventoA02 =(String) serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ULTIMOEVENTO");
					 
				
				%>
					<b><%=Utils.notNull(codiceUltimoEventoA02)%> &nbsp;-&nbsp;</b>				
					<af:comboBox name="descrUlEvA02" classNameBase="input"
								moduleName="COMBO_EVENTO_MINISTERO" 
								selectedValue="<%=codiceUltimoEventoA02%>" 
								addBlank="true"
								disabled ="true"
								required="false" />
				</td>
			</tr>
			<tr>
				<td class="etichettacoop">Data Ultimo Evento</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATA_ULTIMOEVENTO"))%></b></td>
			</tr>
			 
			<tr>
				<td class="etichettacoop" nowrap>Indice Profiling</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.INDICE_PROFILING"))%></b></td>
			</tr>	
			
			<tr>
				<td class="etichettacoop">Data proposta</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAPROPOSTA"))%></b></td>
			</tr>
			<tr>
				<td class="etichettacoop">Data inizio</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAINIZIO"))%></b>
				</td>
			</tr>
			<tr>
				<td class="etichettacoop">Data fine</td>
				<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAFINE"))%></b></td>
			</tr>

	<%}%>
		<tr>
			<td colspan="2">
			<%if (serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.PRENDIINCARICO")) {%>
				<input type="button" class="pulsanti" name="btnTitolaritaDid" value="Prendi in carico per trasferimento" 
					onClick="callInviaSAPTitPresaCarico('<%=DateUtils.formatXMLGregorian(dataDichSap)%>');" id="btnIDTitolarietaDid"/>
			<%}%>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		</table>
		<center>
		<table class="maincoop">
		<tr>
			<%if(canConvalidaDid && isConvalida){ %>
			<td> 
				<input type="submit" class="pulsanti" name="buttConvalidaDID" value="Conferma DID Ministeriale" onclick="settaOperazione('CONFERMA');"/>
			</td>
			<%} %>
			<td>
			<%if(canRevocaDid && isRevoca){ %>
				<input type="submit" class="pulsanti" name="buttRevocaDID" value="Revoca DID Ministeriale" onclick="settaOperazione('REVOCA');"/>
			<%} %>
			
			<%if(isRevoca && canConvalidaDid && !isConvalida && configRiconvalida.equalsIgnoreCase("1")){ %>
				&nbsp;&nbsp;<input type="submit" class="pulsanti" name="buttRiConvalidaDID" value="Effettua nuova conferma" onclick="settaOperazione('CONFERMA');"/>
			<%} %>
			</td>
		</tr>
	 
		<%if(canNuovoConferimento && (isNuovoConf || conferisciDidSil)){ %>
			<%if(isNuovoConf){ %>
				<tr>
	            <td colspan="2"> 
	                <input type="button" class="pulsanti" name="buttNuovoConfDID" value="Nuovo Conferimento DID" onclick="nuovoConferimentoDid();"/>
	             </td>
	        	</tr>
	        <%} %>
	        <%if(conferisciDidSil){ %>
				<tr>
	            <td colspan="2"> 
	                <input type="button" class="pulsanti" name="buttConfDIDSil" value="Conferimento DID SIL" onclick="conferimentoDidSil();"/>
	             </td>
	        	</tr>
	        <%} %>
        <%} %>
		<%if(canVerificaSAP){ %>
			<tr>
            <td colspan="2">&nbsp;
            </td>
            </tr>
			<tr>
            <td colspan="2"> 
                <input type="button" class="pulsanti" name="buttRichiestaSAP" value="Richiesta SAP" onclick="callWSSAP();"/>
             </td>
        	</tr>
        <%}%>
	<%} %>
	</table>
	</center>
	<%out.print(htmlStreamBottomCoop);%>
	</div>

</af:form>

</body>
</html>
