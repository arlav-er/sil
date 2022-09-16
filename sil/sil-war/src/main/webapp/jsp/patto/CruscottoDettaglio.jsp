<!-- @author: Giovanni Landi - Ottobre 2014 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  it.eng.sil.security.*,
                  it.eng.sil.module.movimenti.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*,
                  it.eng.sil.utils.gg.Properties"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	//Attributi della pagina
  	PageAttribs attributi = new PageAttribs(user, "CRUSCOTTOADESIONEPAGE");
	boolean canGetStato = attributi.containsButton("GET_STATO");
	boolean canSetStato = attributi.containsButton("SET_STATO");
	boolean canVerificaStato = attributi.containsButton("VERIFICA_STATO");
	boolean canInviaSAP = attributi.containsButton("INVIA_SAP");

	//PROFILING GG 2019
	boolean canVerificaProfiling = attributi.containsButton("LAST_PROF_GG");
	boolean canCalcolaProfiling = attributi.containsButton("NEW_PROF_GG");
	boolean canGetProfiling = attributi.containsButton("ALL_PROF_GG");
	boolean esisteAdesione = false;
	String chiamataWsStatoAdesione = null;
	
	//VERIFICA NEET GG 2019
	boolean canVerificaNeet = attributi.containsButton("LAST_PROF_GG");
	boolean canStoricoNeet = attributi.containsButton("ALL_NEET_GG");
	
	
	boolean confermaSetStato = false;
	boolean confermaInvioSap = false;

	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	String strCodiceFiscale = (String)serviceRequest.getAttribute("CF");
	String strRegioneMin = (String)serviceRequest.getAttribute("REGIONE");
	String operazione = StringUtils.getAttributeStrNotNull(serviceRequest, "OPERAZIONE");
	String codNuovoStato = StringUtils.getAttributeStrNotNull(serviceRequest, "nuovoStato");
	String dataAdesioneGG = "";
	String codStatoAdesioneCurr = "";
	String nuovoStatoConferma = "";
	String descStatoConferma = "";
	String testoBtnConferma = "";
	String indiceSvantaggio = "";
	String dataRiferimentoIndice = "";
	
	String indiceSvantaggio2 = "";
	String dataStipulaPatto = "";
	java.util.Date datStipula = null;
	java.util.Date dataConfronto = null;

	String codMonoAttiva = null;
	String datUlitmaAdesioneGGSistema = serviceResponse.containsAttribute("M_GetUltimaAdesioneGGSistema.ROWS.ROW.datLastAdesione")?
			serviceResponse.getAttribute("M_GetUltimaAdesioneGGSistema.ROWS.ROW.datLastAdesione").toString():"";
			
	if (serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG")) {
		dataAdesioneGG = serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_DATA_ADESIONE")?
				serviceResponse.getAttribute("M_GetDatiUltimaAdesioneGG.YG_DATA_ADESIONE").toString():"";
		codStatoAdesioneCurr = serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO")?
				serviceResponse.getAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO").toString():"";	
				codMonoAttiva = serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO_CODMONOATTIVA")?
						serviceResponse.getAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO_CODMONOATTIVA").toString():"";	
		chiamataWsStatoAdesione = serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.ERRORE_CHIAMATA_WS")?
						serviceResponse.getAttribute("M_GetDatiUltimaAdesioneGG.ERRORE_CHIAMATA_WS").toString() : "";
		if(serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_DATA_ADESIONE") || serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO")){
			esisteAdesione = true;
		}
	}
	else {
		dataAdesioneGG = StringUtils.getAttributeStrNotNull(serviceRequest, "datAdesioneGG");
		codStatoAdesioneCurr = StringUtils.getAttributeStrNotNull(serviceRequest, "currStato");	
	}
	
	Vector rowInfoProfiling  = serviceResponse.getAttributeAsVector("M_Get_Profiling_Patto_Cruscotto.ROWS.ROW");
    if (rowInfoProfiling != null && !rowInfoProfiling.isEmpty()) {
    	SourceBean profilingBean  = (SourceBean) rowInfoProfiling.elementAt(0);
    	BigDecimal numIndiceSvantaggioBD = (BigDecimal) profilingBean.getAttribute("NUMINDICESVANTAGGIO");
    	dataRiferimentoIndice = StringUtils.getAttributeStrNotNull(profilingBean, "datriferimento");
    	if (numIndiceSvantaggioBD != null) {
    		indiceSvantaggio = numIndiceSvantaggioBD.toString();
    	} else {
    		indiceSvantaggio = "";
    	}
    	
    	//fs2015 - start
    	BigDecimal numIndiceSvantaggioBD2 = (BigDecimal) profilingBean.getAttribute("NUMINDICESVANTAGGIO2");
    	dataStipulaPatto = StringUtils.getAttributeStrNotNull(profilingBean, "datstipula");
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	datStipula = formatter.parse(dataStipulaPatto);
    	dataConfronto = formatter.parse("01/02/2015");    	
    	if (numIndiceSvantaggioBD2 != null) {
    		indiceSvantaggio2 = numIndiceSvantaggioBD2.toString();
    	} else {
    		indiceSvantaggio2 = "";
    	}
    	//fs2015 - end
    }
	
	Vector messaggi = new Vector();
	
	if (serviceResponse.containsAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK") && 
		serviceResponse.getAttribute("M_CheckStatoPoliticheAttiveGG.ESITO_CHECK").toString().equals("KO")) {
		if (operazione.equals("SET_STATO")) {
			confermaSetStato = true;
			nuovoStatoConferma = StringUtils.getAttributeStrNotNull(serviceRequest, "nuovoStatoConferma");
			Vector statiMin = serviceResponse.getAttributeAsVector("M_GetStatoAdesioneGGMin.ROWS.ROW");
			for (int iStati = 0;iStati < statiMin.size();iStati++) {
				SourceBean rowStato = (SourceBean)statiMin.get(iStati);
				String codice = StringUtils.getAttributeStrNotNull(rowStato, "CODICE");
				if (codice.equalsIgnoreCase(codNuovoStato)) {
					descStatoConferma = StringUtils.getAttributeStrNotNull(rowStato, "DESCRIZIONE");
					testoBtnConferma = "Conferma stato " + descStatoConferma;
				}
			}
		}
		else {
			if (operazione.equals("INVIA_SAP")) {
				confermaInvioSap = true;	
			}
		}
		messaggi = serviceResponse.getAttributeAsVector("M_CheckStatoPoliticheAttiveGG.ERRORS.ERROR");
	}
			
	String dataUltimoInvioSAP = "";
	String codMinSap = "";	
	Vector listaInvioSap = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
	if (listaInvioSap.size() > 0) {
		SourceBean rowSAP = (SourceBean)listaInvioSap.get(0);
		dataUltimoInvioSAP = StringUtils.getAttributeStrNotNull(rowSAP, "DATAINVIOMIN");
		codMinSap = StringUtils.getAttributeStrNotNull(rowSAP, "CODMINSAP");
	}
	
	//SEZIONE NEET
	String strDatRiferimentoNeet = "";
	String strTipoVerifica = "";
	String strEsitoNeet = "";
	String strEsitoNeetMin = "";
	String datRiferimentoNeet = "";
	String tipoVerificaNeet="";
	Vector listaVerificaNeet = serviceResponse.getAttributeAsVector("M_GetVerificaNeetGGSistema.ROWS.ROW");
	boolean isStoricoNeet = false;
	if (listaVerificaNeet.size() > 0) {
		isStoricoNeet = true;
		SourceBean neet = (SourceBean)listaVerificaNeet.get(0);
		strDatRiferimentoNeet = StringUtils.getAttributeStrNotNull(neet, "strDatRif");
		strTipoVerifica =  StringUtils.getAttributeStrNotNull(neet, "strTipoVerificaNeet");
		strEsitoNeet = StringUtils.getAttributeStrNotNull(neet, "strFlgNeet");
		strEsitoNeetMin =  StringUtils.getAttributeStrNotNull(neet, "strEsitoMin");
		
	}
	
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
  	
%>

<html>
<head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
  	<af:linkScript path="../../js/"/>
<!--   	<script language="JavaScript" src="../../js/script_comuni.js"></script>
 -->  	
  	<title>Cruscotto Garanzia Giovani</title>
  
  	<script language="Javascript">

  	function settaOperazione(operazione) {
  		document.adesioneGG.OPERAZIONE.value = operazione;
  		return true;
  	}

  	function verificaStato(operazione) {
  		if (document.adesioneGG.nuovoStato.value == "") {
  	  		alert("Il campo Nuovo Stato è obbligatorio");
  	  		return false;
  	  	}
  		else {
  			document.adesioneGG.OPERAZIONE.value = operazione;
  			return true;
  		}
  	}
  	
  	function inviaNuovoStato(operazione) {
  	  	if (document.adesioneGG.nuovoStato.value == "") {
  	  		alert("Il campo Nuovo Stato è obbligatorio");
  	  		return false;
  	  	}
  	  	else {
  	  		if (confirm("Si vuole procedere all'invio del nuovo stato adesione?")) {
	  	  		document.adesioneGG.OPERAZIONE.value = operazione;
	  	  		document.adesioneGG.nuovoStatoConferma.value = document.adesioneGG.nuovoStato.value;
	  	  		return true;
  	  		}
  	  		else {
  	  			return false;	
  	  		}
  	  	}
  	}
  	
  	function confermaInviaNuovoStato(operazione) {
  		if (confirm("Si vuole procedere all'invio del nuovo stato adesione?")) {
	  	  	document.adesioneGG.OPERAZIONE.value = operazione;
	  	  	return true;
  		}
  		else {
  			return false;
  		}
  	}
  	
  	function inviaSAP(operazione) {
  	  	if (document.adesioneGG.currStato.value == "") {
			if (confirm("Non è disponibile l'adesione del lavoratore, si vuole procedere ugualmente all'invio della SAP?")) {
				document.adesioneGG.OPERAZIONE.value = "CONFERMA_INVIA_SAP";
				if (document.getElementById("btnCruscottoInviaSAP") != null) {
					document.getElementById("btnCruscottoInviaSAP").disabled = true;
				}
				if (document.getElementById("btnCruscottoConfInviaSAP") != null) {
					document.getElementById("btnCruscottoConfInviaSAP").disabled = true;
				}
				return true;
			}
			else {
				return false;
			}
  	  	}
  	  	else {
  	  		if (confirm("Si vuole procedere all'invio della SAP?")) {
	  	  		document.adesioneGG.OPERAZIONE.value = operazione;
		  	  	if (document.getElementById("btnCruscottoInviaSAP") != null) {
		  			document.getElementById("btnCruscottoInviaSAP").disabled = true;
		  		}
		  	  	if (document.getElementById("btnCruscottoConfInviaSAP") != null) {
					document.getElementById("btnCruscottoConfInviaSAP").disabled = true;
				}
	  	  		return true;
  	  		}
  	  		else {
  	  			return false;
  	  		}	
  	  	}
  	}
  	
  	function goProfilingGG(tipoOperazioneGG){
  	
  		document.adesioneGG.OPERAZIONE.value = ''; 
  		var urlpage="AdapterHTTP?";
  		var nomePagina = "";
  		if(tipoOperazioneGG === "ALL_PROF_GG"){
  			nomePagina = "AllProfilingGGPage";
  		}else if(tipoOperazioneGG === "NEW_PROF_GG"){
  			nomePagina = "CalcolaProfilingGGPage";
  		}else if(tipoOperazioneGG === "LAST_PROF_GG"){
  			nomePagina = "DettaglioProfilingGGPage";
  			urlpage +="&OPERAZIONE_GG=VERIFICA&";
  		}
   		urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=";
   		urlpage += nomePagina;
		urlpage +="&CDNLAVORATORE=<%=cdnLavoratore%>";
  		window.open(urlpage,'Profiling GG','toolbar=NO,statusbar=YES,width=900,height=500,top=50,left=100,scrollbars=YES,resizable=YES');
}
  
  	function goNeetGG(tipoOperazioneNeet){
  		var checkVerifica = true;
  		var nomePagina = "";
  		document.adesioneGG.OPERAZIONE.value = ''; 
  		var urlpage="AdapterHTTP?";
  		urlpage +="CDNLAVORATORE=<%=cdnLavoratore%>";
  		urlpage+="&CDNFUNZIONE=<%=_funzione%>&PAGE=";
  		if(tipoOperazioneNeet === "ALL_NEET_GG"){
  			nomePagina = "AllNeetGGPage";
  			urlpage += nomePagina;
  	  		window.open(urlpage,'Verifica Neet GG','toolbar=NO,statusbar=YES,width=900,height=500,top=50,left=100,scrollbars=YES,resizable=YES');	

  		}else if(tipoOperazioneNeet === "VERIFICA_NEET"){
  			if (document.adesioneGG.DATRIFERIMENTONEET.value == "") {
  	  	  		alert("Il campo "+ document.adesioneGG.DATRIFERIMENTONEET.title +" è obbligatorio");
  	  	  		document.adesioneGG.DATRIFERIMENTONEET.focus();
  	  	  		checkVerifica = false;
  	  	  	}
  			if (checkVerifica && document.adesioneGG.verificaNeet.value == "") {
  	  	  		alert("Il campo "+ document.adesioneGG.verificaNeet.title +" è obbligatorio");
  	  	  		document.adesioneGG.verificaNeet.focus();
  	  	  		checkVerifica = false;	
  	  	  	}
  			
  			nomePagina = "DettaglioNeetGGPage";
  			if(checkVerifica){
  	  	   		urlpage += nomePagina;
  	  			urlpage +="&CF=<%=strCodiceFiscale%>";
  	  			urlpage +="&REGIONE=<%=strRegioneMin%>";
  	  			urlpage +="&verificaNeet="+document.adesioneGG.verificaNeet.value;
  	  			urlpage +="&DATRIFERIMENTONEET="+document.adesioneGG.DATRIFERIMENTONEET.value;
  	  			urlpage +="&OPERAZIONE_NEET=OPERAZIONE_NEET";
  	  	  		window.open(urlpage,'Verifica Neet GG','toolbar=NO,statusbar=YES,width=900,height=500,top=50,left=100,scrollbars=YES,resizable=YES');	
  	  		}else{
  	  			return false;
  	  		}
  		}
}
  
  	
  	</script>
  	
  	<script language="Javascript">

   		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);

	</script>
	<script language="Javascript">
  	<%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  	%>
	</script>
  
</head>

<body class="gestione" onload="rinfresca();">
<font color="green">
	<af:showMessages prefix="M_GetDatiUltimaAdesioneGG"/>
  	<af:showMessages prefix="M_SetStatoAdesioneGG"/>
  	<af:showMessages prefix="M_GestioneInvioSap"/>
  	<af:showMessages prefix="M_CheckStatoPoliticheAttiveGG"/>
</font>
<font color="red">
     <af:showErrors/>
</font>
<br>
<p class="titolo">Cruscotto Garanzia Giovani</p>
<br/>
<center>
	<%out.print(htmlStreamTopCoop);%>
	<af:form name="adesioneGG" method="POST" action="AdapterHTTP">
		
		<table class="maincoop">
			<tr>
				<td><div class="sezione2"/>Stato Adesione Attuale da Min</td>
			</tr>
		</table>
		
		<table class="maincoop">
		<tr>
			<td class="etichettacoop" nowrap>Data</td>
			<td class="campocoop">
				<af:textBox type="date" name="datAdesioneGG" title="Data Adesione GG" value="<%=dataAdesioneGG%>" 
					classNameBase="input" readonly="true" size="12"/>
			</td>
		</tr>
		<tr>
			<td class="etichettacoop" nowrap>Stato</td>
			<td class="campocoop">
				<af:comboBox name="currStato" moduleName="M_GetTuttiStatoAdesioneGGMin" selectedValue="<%=codStatoAdesioneCurr%>"
                     classNameBase="input" addBlank="true" disabled="true" />
			</td>
		</tr>
		<%if (serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO_ANOMALIE")) {%>
			<tr>
				<td class="etichettacoop" nowrap><b>Errore</b></td>
          		<td class="campocoop"><b><%=serviceResponse.getAttribute("M_GetDatiUltimaAdesioneGG.YG_STATO_ANOMALIE").toString()%></b>
          		</td>
          	</tr>
		<%}%>
		<tr>
          	<td colspan="2" align="center">
        	<%if (codStatoAdesioneCurr.equals("")) {
        		if (canGetStato) {%>
        			<input class="pulsanti" type="submit" name="btnGetStato" onclick="return settaOperazione('GET_STATO');" value="Richiedi Stato Adesione"/>
        		<%}
        	}%>
         	</td>
        </tr>
        
        <tr>
          	<td colspan="2" align="center">
        	<%if(chiamataWsStatoAdesione!=null && StringUtils.isEmptyNoBlank(chiamataWsStatoAdesione) && codMonoAttiva.equals(Properties.ADESIONE_ATTIVA)){
        		if (canVerificaProfiling) {%>
        			<input class="pulsanti" type="button" name="btnVerificaProfilingGG" onclick="goProfilingGG('LAST_PROF_GG');" value="Richiedi Ultimo Profiling GG"/>
        		<%}
        	}%>        			
          
        	<%if(chiamataWsStatoAdesione!=null && StringUtils.isEmptyNoBlank(chiamataWsStatoAdesione) && codMonoAttiva.equals(Properties.ADESIONE_ATTIVA)){
        		if (canCalcolaProfiling) {%>
        	 		<input class="pulsanti" type="button" name="btnCalcolaProfilingGG" onclick="goProfilingGG('NEW_PROF_GG');" value="Calcola Nuovo Profiling GG"/>
        		<%}
        	}%>
         	 
        	<%if(StringUtils.isEmptyNoBlank(chiamataWsStatoAdesione) && esisteAdesione){
        		if (canGetProfiling) {%>
        			<input class="pulsanti" type="button" name="btnGetProfilingGG" onclick="goProfilingGG('ALL_PROF_GG');" value="Storico Profiling GG"/>
        		<%}
        	}%>        			
         	</td>
        </tr>
        
		<tr><td colspan="2">&nbsp;</td></tr>
		</table>
		
		<%out.print(htmlStreamBottomCoop);%>
		
		<%out.print(htmlStreamTop);%>
		
		<table class="main">
			<tr>
				<td><div class="sezione2"/>Nuovo stato (indica il nuovo stato dell'adesione e invia)</td>
			</tr>
		</table>
		
		<table class="main">
			<tr>
				<td class="etichetta" nowrap>Nuovo Stato</td>
				<td class="campo">
					<%if (!codStatoAdesioneCurr.equals("")) {
						if (!serviceResponse.containsAttribute("M_GetDatiUltimaAdesioneGG")) {%>
							<af:comboBox name="nuovoStato" size="1" title="Nuovo stato"
				                 multiple="false" disabled="false" classNameBase="input"
				                 moduleName="M_GetStatoAdesioneNuovoFromOld" selectedValue="<%=codNuovoStato%>"
				                 addBlank="true" blankValue=""/>
						<%} else {%>
							 <af:comboBox name="nuovoStato" size="1" title="Nuovo stato"
				                 multiple="false" disabled="false" classNameBase="input"
				                 moduleName="M_GetStatoAdesioneNuovoGGMin" selectedValue="<%=codNuovoStato%>"
				                 addBlank="true" blankValue=""/>
				       	<%}
			        } else {%>
			        	<af:comboBox name="nuovoStato" size="1" title="Nuovo stato"
			                 multiple="false" disabled="false" classNameBase="input"
			                 moduleName="M_GetStatoAdesioneGGMin" selectedValue="<%=codNuovoStato%>"
			                 addBlank="true" blankValue=""/>
			        <%}%>
				</td>
			</tr>
			<%if (serviceResponse.containsAttribute("M_SetStatoAdesioneGG.SET_YG_STATO_ANOMALIE")) {%>
				<tr>
					<td class="etichetta" nowrap><b>Errore</b></td>
	          		<td class="campo"><b><%=serviceResponse.getAttribute("M_SetStatoAdesioneGG.SET_YG_STATO_ANOMALIE").toString()%></b>
		          	</td>
	          	</tr>
			<%}%>
			<tr>
      		<td colspan="2" align="center">
      		<%if (canSetStato) {%>
				<input class="pulsante" type="submit" name="btnInviaNuovoStato" value="Invia Nuovo Stato Adesione" onclick="return inviaNuovoStato('SET_STATO');"/>&nbsp;
			<%}
      		if (canVerificaStato){%>
				<input class="pulsante" type="submit" name="btnVerificaNuovoStato" value="Verifica Nuovo Stato Senza Invio" onclick="return verificaStato('VERIFICA_STATO');"/>
			<%}%>
			</td>
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
		</table>
		
		<table class="main">
			<tr>
				<td><div class="sezione2"/>Verifica Condizione NEET</td>
			</tr>
		</table>
		<table>
			<tr>
			<td class="etichetta" width="50%" nowrap>Data rif. ultima verifica</td>
			<td colspan="3" width="50%">
			
			<table>
			<tr>
			<td class="campo" width="33%">
				<af:textBox type="text" name="datUltimaVerNeet" title="Data rif. ultima verifica" value="<%=strDatRiferimentoNeet%>" 
					classNameBase="input" readonly="true" size="20"/>
			</td>
			<td class="etichetta" width="33%" nowrap>Tipo verifica</td>
			<td class="campo" width="33%">
				<af:textBox type="text" name="tipoLastVerificaNeet" title="Tipo verifica" value="<%=strTipoVerifica%>" 
					classNameBase="input" readonly="true" size="20"/>	
			</td>
			</tr>
			</table>
			</td>
			</tr>
			
			<tr>
			<td class="etichetta" width="50%" nowrap>Esito verifica NEET</td>
			<td colspan="3" width="50%">
			
			<table>
			<tr>
			<td class="campo" width="20%">
				<af:textBox name="esitoVerificaNeet" value="<%=strEsitoNeet%>" type="text" readonly="true" classNameBase="input"              
	                 title="Indice GG/Dlgs 150" size="20" maxlength="38"/>
	        </td>

	        
	        <td class="etichetta" width="33%" nowrap>Esito min.</td>
            <td class="campo" width="46%">
              <af:textBox name="esitoVerificaNeetMin" value="<%=strEsitoNeetMin%>" type="date"
		        	readonly="true" classNameBase="input" title="Esito Neet Ministeriale" size="20" maxlength="10"/>    
            </td>
            </tr>
            </table>
        	
        	</td>
        	</tr>
	
			<tr>
				<td class="etichetta" width="50%" nowrap>Data verifica cond. NEET</td>
				<td colspan="3" width="50%">
				
				<table>
				<tr>
				<td class="campo" width="20%">
			       <af:textBox type="date" title="Data verifica cond. NEET" name="DATRIFERIMENTONEET"
			        value="<%=datRiferimentoNeet%>" size="12" maxlength="10" onBlur="checkFormatDate(this);" />
			        
		        </td>
	
		        
		        <td class="etichetta" width="33%" nowrap>Tipo verifica</td>
	            <td class="campo" width="46%">
	                <af:comboBox name="verificaNeet" size="1" title="Tipo verifica"
				                 multiple="false" disabled="false" classNameBase="input"
				                 moduleName="M_ComboVerificaNeet" selectedValue="<%=tipoVerificaNeet%>"
				                 addBlank="true" blankValue=""/>
	            </td>
	            </tr>
	            </table>
	        	
	        	</td>
        	</tr>

			
        </table>
		 
       	<table class="main">
           	
			<tr>
      		<td colspan="2" align="center">
      		<%if (canVerificaNeet) {%>
				<input class="pulsante" type="button" name="btnVerificaNeetGG" value="Verifica condizioni NEET" onclick="goNeetGG('VERIFICA_NEET');"  />
			<%}%>
			</td>
			<td colspan="2" align="center">
      		<%if (canStoricoNeet && isStoricoNeet) {%>
				<input class="pulsante" type="button" name="btnStoricoNeetGG" value="Storico Verifiche cond. NEET" onclick="goNeetGG('ALL_NEET_GG');"  />
			<%}%>
			</td>
			</tr>
		</table>
		
		<table class="main">
			<tr>
				<td><div class="sezione2"/>Invia SAP</td>
			</tr>
		</table>
			
		<table>
			<tr>
			<td class="etichetta" width="50%" nowrap>Data ultimo invio SAP</td>
			<td colspan="3" width="50%">
			
			<table>
			<tr>
			<td class="campo" width="33%">
				<af:textBox type="date" name="datInvioSAP" title="Data invio SAP" value="<%=dataUltimoInvioSAP%>" 
					classNameBase="input" readonly="true" size="20"/>
			</td>
			<td class="etichetta" width="33%" nowrap>Codice Min. SAP</td>
			<td class="campo" width="33%">
				<af:textBox type="text" name="codMinInvioSAP" title="Codice ministeriale SAP" value="<%=codMinSap%>" 
					classNameBase="input" readonly="true" size="20"/>	
			</td>
			</tr>
			</table>
			</td>
			</tr>
			
			<!-- fs2015 aggiunta campo "indice 2 di svantagglio" -->
			<% if(datStipula != null && dataConfronto != null && datStipula.compareTo(dataConfronto)<0) {%>
			<tr>
			<td class="etichetta" width="50%" nowrap>Indice di svantaggio</td>
			<td colspan="3" width="50%">
			
			<table>
			<tr>
			<td class="campo" width="33%">
				<af:textBox name="INDICESVANTAGGIO" value="<%=indiceSvantaggio%>" type="text" readonly="true" classNameBase="input"              
	                 title="Indice di svantaggio" size="20" maxlength="38"/>
	        </td>
			
            </tr>
            </table>
        	
        	</td>
        	</tr>
        	<%} %>
            <!-- fs2015 fine modifica -->
			
			<tr>
			<td class="etichetta" width="50%" nowrap>Indice GG/Dlgs 150</td>
			<td colspan="3" width="50%">
			
			<table>
			<tr>
			<td class="campo" width="33%">
				<af:textBox name="INDICESVANTAGGIO2" value="<%=indiceSvantaggio2%>" type="text" readonly="true" classNameBase="input"              
	                 title="Indice GG/Dlgs 150" size="20" maxlength="38"/>
	        </td>

	        
	        <td class="etichetta" width="33%" nowrap>Data riferimento</td>
            <td class="campo" width="33%">
              <af:textBox name="DATRIFERIMENTOINDICE" value="<%=dataRiferimentoIndice%>" type="date"
		        	readonly="true" classNameBase="input" title="Data riferimento" size="20" maxlength="10"/>    
            </td>
            </tr>
            </table>
        	
        	</td>
        	</tr>


			
        </table>
		 
       	<table class="main">
           	
			<tr>
      		<td colspan="2" align="center">
      		<%if (canInviaSAP) {%>
				<input class="pulsante" type="submit" name="btnInviaSAP" value="Invia SAP" onclick="return inviaSAP('INVIA_SAP');" id="btnCruscottoInviaSAP"/>
			<%}%>
			</td>
			</tr>
		</table>
		
		<table class="main">
			<tr>
				<td><div class="sezione2"/>Lista Azioni</td>
			</tr>
		</table>
		
		<table class="main">
			<tr>
			<td class="etichetta">Data Adesione SIL</td>
			<td class="campo">
				<af:textBox type="date" name="datAdesioneSIL" title="Data adesione SIL" value="<%=datUlitmaAdesioneGGSistema%>" 
					classNameBase="input" readonly="true" size="12"/>
			</td>
			</tr>
		</table>
		
		<p>
			<af:list moduleName="M_InsiemeAzioniCruscotto" skipNavigationButton="1"/>
		</p>
		
		<%if ( (operazione.equals("VERIFICA_STATO") && messaggi.size() > 0) || confermaSetStato || confermaInvioSap) {%>
			<table class="main">
				<tr>
					<td><div class="sezione2"/>Messaggi</td>
				</tr>
			</table>
			
			<table class="main">
				<%for (int iMess = 0;iMess < messaggi.size();iMess++) {
					SourceBean msg = (SourceBean)messaggi.get(iMess);
					String descMessaggio = StringUtils.getAttributeStrNotNull(msg, "DESCRIZIONE");%>
					<tr>
						<td align="left" class="ectichetta"><%=descMessaggio%></td>
					</tr>
					<tr><td>&nbsp;</td></tr>
				<%}%>
			</table>
			
			<table class="main">
			
      		
				<%if (confermaSetStato) {%>
					<tr>
					<td colspan="2" align="center">
					<%if (canSetStato) {%>
						<input class="pulsante" type="submit" name="btnConfermaNuovoStato" value="<%=testoBtnConferma%>" onclick="return confermaInviaNuovoStato('CONFERMA_STATO');"/>
					<%}%>
					</td>
					</tr>
				<%} else {
					if (confermaInvioSap) {%>
						<tr>
						<td colspan="2" align="center">
						<%if (canInviaSAP) {%>
							<input class="pulsante" type="submit" name="btnConfermaInviaSAP" value="Conferma Invia SAP" 
								onclick="return inviaSAP('CONFERMA_INVIA_SAP');" id="btnCruscottoConfInviaSAP"/>
						<%}%>
						</td>
						</tr>
					<%}
				}%>
			</table>
		<%}%>
		
        <input type="hidden" name="PAGE" value="CruscottoDettaglioPage">
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
		<input type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">
		<input type="hidden" name="OPERAZIONE" value="<%=operazione%>">
		<input type="hidden" name="REGIONE" value="<%=strRegioneMin%>">
		<input type="hidden" name="CF" value="<%=strCodiceFiscale%>">
		<input type="hidden" name="nuovoStatoConferma" value="<%=nuovoStatoConferma%>">
        
      </af:form>
    <%out.print(htmlStreamBottom);%>
    <br>
</center>  
</body>
</html>
