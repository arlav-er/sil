<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.configuration.ConfigSingleton,
                com.engiweb.framework.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                java.math.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*,
                it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>
			
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "TrasferimentiStoricoElencoPage");
  boolean canModify = false;//attributi.containsButton("aggiorna");
  boolean canDelete = true;//attributi.containsButton("rimuovi");
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  Testata operatoreInfo = null;

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  
  String comuneDom     = "";
  String strCpiOrig    = "";
  String codCpiOrig    = "";
  String strCPItit     = "";
  String codCPItit     = "";
  String codMonoTipoCpi= "";
  String datInizio     = "";
  String datFine       = "";
  String datAnzianDisoc= "";
  String statoOccupaz  = "";
  String strNote       = "";
  BigDecimal cdnUtins  = null;
  String dtmins        = "";
  BigDecimal cdnUtmod  = null;
  String dtmmod        = "";
  String numMesiSosp   = "";
  BigDecimal numAnnoProt   = null;
  BigDecimal numProt       = null;
  String dataProt      = "";
  String oraProt       = "";
 
  Object cdnLavoratore= serviceRequest.getAttribute("CDNLAVORATORE");
 
  SourceBean row = (SourceBean) serviceResponse.getAttribute("M_TRASFERIMENTISTORICODETTAGLIO.ROWS.ROW");
  if(row!=null) 
  { //Dato che arriviamo dallo storico utilizzando un progressivo
    //in teoria row è sempre NON NULLO
	 comuneDom     = StringUtils.getAttributeStrNotNull(row,"COMUNEDOM");
	 strCpiOrig    = StringUtils.getAttributeStrNotNull(row,"CPIORIGINE");
	 codCpiOrig    = StringUtils.getAttributeStrNotNull(row,"CODCPIORIG");
	 strCPItit     = StringUtils.getAttributeStrNotNull(row,"CPITITOLARE");
	 codCPItit     = StringUtils.getAttributeStrNotNull(row,"CODCPITIT");
	 codMonoTipoCpi= StringUtils.getAttributeStrNotNull(row,"CODMONOTIPOCPI");
	 datInizio     = StringUtils.getAttributeStrNotNull(row,"DATINIZIO");
	 datFine       = StringUtils.getAttributeStrNotNull(row,"DATFINE");
	 datAnzianDisoc= StringUtils.getAttributeStrNotNull(row,"DATANZIANITADISOC");
	 statoOccupaz  = StringUtils.getAttributeStrNotNull(row,"STATOOCCUPAZ");
	 strNote       = StringUtils.getAttributeStrNotNull(row,"STRNOTE");
	 cdnUtins      = (BigDecimal) row.getAttribute("CDNUTINS");
	 dtmins        = StringUtils.getAttributeStrNotNull(row,"DTMINS");
	 cdnUtmod      = (BigDecimal) row.getAttribute("CDNUTMOD");
	 dtmmod        = StringUtils.getAttributeStrNotNull(row,"DTMMOD");
	 if (row.containsAttribute("NUMMESISOSP")) numMesiSosp = ((BigDecimal) row.getAttribute("NUMMESISOSP")).toString();
  }

  row = (SourceBean) serviceResponse.getAttribute("M_TrasferimentiStoricoDettaglioProtocollo.ROWS.ROW");
  if(row!=null) 
  { //Dato che arriviamo dallo storico utilizzando un progressivo
    //in teoria row è sempre NON NULLO
     numAnnoProt   = (BigDecimal) row.getAttribute("numAnnoProt");
     numProt       = (BigDecimal) row.getAttribute("numProt");
     dataProt      = StringUtils.getAttributeStrNotNull(row,"dataProt");
     oraProt       = StringUtils.getAttributeStrNotNull(row,"oraProt");
  }
  
  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  String codCpiCompTit=(codMonoTipoCpi.equals("T") && !codCpiOrig.equals(""))?codCpiOrig:codCPItit; //cpi titolare/competente come risulta in locale
  
  boolean coopAbilitata= false;
  boolean poloInCoop= false;
  boolean esisteStampaRichDocTrasf = false;
  String origProvCpi = "";
  if (datFine.equals("")) {
  	String IntraProvinciale = (String) serviceResponse.getAttribute("M_CHECKSTESSAPROVINCIA.IntraProvinciale");
  	if (IntraProvinciale.equals("false")) {
  		if (((String) serviceResponse.getAttribute("M_CHECKSTESSAPROVINCIA.coopAttiva")).equals("true")) {
			if (((String)serviceResponse.getAttribute("M_COOP_CheckProvinciaAttiva.poloInCoop")).equals("true")) {
				poloInCoop = true;
				origProvCpi =  (String) serviceResponse.getAttribute("M_CHECKSTESSAPROVINCIA.ROWS.ROW.codProvOrig");
				if (((String)serviceResponse.getAttribute("M_CheckEsistenzaDocTrasf.stampaRichDocTrasf")).equals("true")) {
					esisteStampaRichDocTrasf = true;
				}
			}
  		}
  	}
  }

  //controllo configurazione per la cooperazione
  String coopAttiva = System.getProperty("cooperazione.enabled");
	if (coopAttiva != null && coopAttiva.equals("true")) {
  		coopAbilitata = true;
  	}

%>

<html>
<head>
 <title>Dettaglio Storico Trasferimenti</title>
 <!-- ../jsp/anag/TrasferimentiStoricoElenco.jsp -->
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">
 function aggiornaIR() {
	document.location.href = "AdapterHTTP?" + "PAGE=CoopRiAggiornaCompetenzaIRPage&cdnFunzione=<%=_funzione%>&cdnLavoratore=<%=cdnLavoratore%>&DATTRASFERIMENTO=<%=datInizio%>&CODCPI=<%=codCpiCompTit%>&CODMONOTIPOCPI=<%=codMonoTipoCpi%>";
 }
 
 function inviaRichiestaTrasf() {
 	<% if (esisteStampaRichDocTrasf) {%>
 		alert ("Trasferimento effettuato non in cooperazione, quindi non è possibile effettuare il reinoltro");
 	<% } else  {%>
		document.location.href = "AdapterHTTP?" + "PAGE=CoopRiInviaRichiestaTrasfPage&cdnFunzione=<%=_funzione%>&cdnLavoratore=<%=cdnLavoratore%>&origProvCpi=<%=origProvCpi%>";
 	<% } %>
 }

 function customFieldEnabledDisabled(objCampo, enabled) {
	// Cambio lo stile dell'oggetto
	var className = objCampo.className;
	var pos = className.indexOf("Disabled");
	if (enabled) {
		if (pos >= 0) {
			// Rimuovo il "Disabled" (se c'\u00E8)
			className = className.substring(0,pos);
		}
	}
	else {
		if (pos == -1) {
			// Aggiungo il "DIsabled" (se non c'\u00E8 gi\u00E0)
			className += "Disabled";
		}
	}
	objCampo.className = className;
 }
	
 function callVerificaSAP(){
	var f;
   	f = "AdapterHTTP?PAGE=ReportTopPage";
   	f = f + "&PROVENIENZA=STORICOTRASF&CDNLAVORATORE=<%=cdnLavoratore%>&trasferisci=true&DATATRASFCOMP=<%=datInizio%>&TRASFVERIFICASAP=true&INIBISCI_INDIETRO=true";
   	if (document.getElementById("btnIDVerificaSap") != null) {
   		customFieldEnabledDisabled(document.getElementById("btnIDVerificaSap"), false);
	}
  	document.location = f;
}
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>


<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
  <tr><td colspan="2"><p class="titolo">Dettaglio del trasferimento</p></td>
  <tr><td>&nbsp;</td></tr>
  <tr><td class="etichetta">Anno</td>
      <td>
        <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr><td class="campo2">
                <af:textBox name="numAnnoProt"
                            type="integer"
                            validateOnPost="true" 
                            title="Anno di protocollazione"
                            value="<%= Utils.notNull(numAnnoProt) %>"
                            classNameBase="input"
                            size="4"
                            maxlength="4"
                            onKeyUp="fieldChanged()"
                            required="false"
                            readonly="true" />
            </td>
            <td class="etichetta2">
                Num
            </td>
            <td class="campo2">
                <af:textBox name="numProtocollo"
                            title="Numero di protocollo"
                            value="<%= Utils.notNull(numProt) %>"
                            classNameBase="input"
                            size="10"
                            maxlength="100"
                            onKeyUp="fieldChanged()"
                            required="false"
                            readonly="true" />
            </td>    
            <td class="etichetta2">data</td>
            <td class="campo2">
               <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
                    title="data di protocollazione"  classNameBase="input" validateOnPost="true" 
                    readonly="true" required="false" trim ="false"
                    onKeyUp="cambiAnnoProt(this,numAnnoProt)" onBlur="checkFormatDate(this)"
                /></td>
       		<% if (ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
            <td class="etichetta2">ora</td>
            <td class="campo2">
               <af:textBox name="oraProt" type="date" value="<%=oraProt%>" size="6" maxlength="5"
                    title="ora di protocollazione"  classNameBase="input" validateOnPost="false" 
                    readonly="true" required="false" trim ="false"
                />
            <% } else { %>
            <td><td><input type="hidden" name="oraProt" value="00:00">
            <% } %>
        </tr>
        </table></td>
    </tr>
  <tr><td>&nbsp;</td></tr>
    <tr><td colspan="2"><div class="sezione2"/></td></tr>
    <tr><td colspan="2">  
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr><td class="etichetta">Trasferito dal CPI</td>
      <% if(codMonoTipoCpi.equals("T")) {%>
           <td class="campo2"><af:textBox type="text" classNameBase="input" name="strCPIprec"  value="<%=strCPItit%>" readonly="true" size="<%=(strCPItit.length()+5)%>"/></td>
      <% } else { %>
           <td class="campo2"><af:textBox name="strCpiPrec" value="<%=strCpiOrig%>" type="text" classNameBase="input" readonly="true" size="<%=(strCpiOrig.length()+5)%>"/></td>
      <% } %>
          <td class="etichetta2">dal</td>
          <td class="campo2"><af:textBox name="" value="<%=datInizio%>" type="text" classNameBase="input" readonly="true"/></td>
      </tr>
      <tr><td class="etichetta">al CPI</td>
	  <% if(codMonoTipoCpi.equals("T") && !codCpiOrig.equals("")){ %>
	      <td class="campo2"><af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiOrig%>" readonly="true" size="<%=(strCpiOrig.length()+5)%>"/>
	  <% } else {%>                
	      <td class="campo2"><af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCPItit%>" readonly="true" size="<%=(strCPItit.length()+5)%>"/>
	  <% }%>
	      </td>
	  <%if (!datFine.equals("")) {%>
	      <td class="etichetta2">fino al</td>
	      <td class="campo2"><af:textBox name="" value="<%=datFine%>" type="text" classNameBase="input" readonly="true"/></td>
	  <%} else {%>
	      <td class="etichetta2">fino a</td>
	      <td class="campo2"><af:textBox name="" value="oggi" type="text" classNameBase="input" readonly="true"/></td>
	  <%} %>
	      </tr>
	      </table>
      </td>
  </tr>
  <tr><td class="etichetta">Con domicilio in</td>
      <td class="campo"><af:textBox name="comuneDom" value="<%=comuneDom%>" type="text" classNameBase="input" readonly="true" size="<%=(comuneDom.length()+5)%>"/></td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr><td colspan="2"><div class="sezione2"/></td></tr>
  <tr><td class="etichetta">Anzianit&agrave;</td>
      <td class="campo"><af:textBox name="datAnzianDisoc" value="<%=datAnzianDisoc%>" type="text" classNameBase="input" readonly="true"/></td>
  </tr>
  <tr><td class="etichetta">Mesi di sospensione</td>
      <td class="campo"><af:textBox name="" value="<%=numMesiSosp%>" type="text" classNameBase="input" readonly="true"/></td>
  </tr>
  <tr><td class="etichetta">Stato occupazionale</td>
      <td class="campo"><af:textBox name="statoOccupaz" value="<%=statoOccupaz%>" type="text" classNameBase="input" readonly="true" size="<%=(statoOccupaz.length()+5)%>"/></td>
  </tr>
  <tr><td class="etichetta">Note</td>
      <td class="campo"><af:textArea name="strNote" value="<%=strNote%>" classNameBase="textarea" cols="50" rows="4" readonly="true" maxlength="1000"/></td>
  </tr>
  <tr><td>&nbsp;</td></tr>
<%if (sessionContainer!=null){
      String token = "_TOKEN_TRASFERIMENTISTORICOELENCOPAGE";
      String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());

      if (urlDiLista!=null){
       %> 
        <tr><td colspan="2"><a href="#" onClick="goTo('<%=urlDiLista%>');"><img src="../../img/rit_lista.gif" border="0"></a></td></tr>
        <%--La seguente sessione e a PROVA DI ANGELA;) Se il pulsante non viene reputato standard... scommentare e otteniamo un pulsante standard
        <input class="pulsante" type="button" VALUE="Torna alla lista" onClick="javascript:cameBack('AdapterHTTP?<%=urlDiLista%>');" />
        --%>
    <%}
  }
%>
</table>
<%out.print(htmlStreamBottom);%>

<%operatoreInfo.showHTML(out);%>
<%if (coopAbilitata && poloInCoop) { //permettiamo di reinoltrare la richiesta di trasferimento lavoratore nel caso NON-intraprovinciale%>
	<p	align="center"><input class="pulsante" type = "button" name="ReinoltroTrasf" value="Reinoltra Richiesta di Trasferimento" onclick="inviaRichiestaTrasf();"/></p>
<%}%>
<%if (coopAbilitata && (datFine.equals("") && codCpiCompTit.equals(user.getCodRif()))) { //permettiamo di richiedere all'IR l'allineamento solo se
																	  //il record è quello attuale															  //e il cpi titolare/competente risultante sul polo è il proprio%>
	<p align="center"><input class="pulsante" type = "button" name="AggiornaIR" value="Chiedi nuovamente l'aggiornamento sull'Indice Regionale" onclick="aggiornaIR();"/>&nbsp;&nbsp;
	<input class="pulsanti" type="button" name="btnVerificaSap" value="Verifica SAP" onClick="callVerificaSAP();" id="btnIDVerificaSap"/>
	</p>
<%} else {%>
	<p align="center"><input class="pulsanti" type="button" name="btnVerificaSap" value="Verifica SAP" onClick="callVerificaSAP();" id="btnIDVerificaSap"/></p>
<%}%>
<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
