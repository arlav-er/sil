<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>
           
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% SourceBean listeSpec_Row= null;
   String cdnLavoratore    = null;
   BigDecimal PRGUNITA     = null;
   BigDecimal PRGAZIENDA   = null;
   String rowRagioneSociale= null;
   String dataInizMov      = null;
   String dataFineMov      = null;
   String rowIndirizzo     = null;
   String rowComune        = null;
   String rowProv		   = null;
   String rowCf			   = null;
   String rowPIva          = null;
   String rowTel           = null;
   String dataInizio       = null;
   String dataFine         = null;
   String dataFineOrig		= null;
   String dataMaxDiff      = null;
   String motScorrDataMaxDiff = null;
   String codMBTipoLetto   = null;
   BigDecimal codMBStato = null;
   String indennita_flg    = null;
   String dataInizioIndenn = null;
   String dataFineIndenn    = null;
   String mans             = null;
   String codMans          = null;
   String codGrado = null;
   String numLivello = null;
   String codCCNL = null;
   String strDescrizioneCCNL = null;
   String datCRT		   = null;
   String numCRT           = null;
   String provCRT		   = null;
   String regCRT 	       = null;
   String strNote          = null;
   String motivoFine	   = null;
   String dtmIns           = null;
   String dtmMod           = null;
   BigDecimal prgMobilita  = null;
   BigDecimal prgMovimento = null;
   BigDecimal cdnUtMod     = null;
   BigDecimal cdnUtIns     = null;
   BigDecimal keyLock      = null;
   boolean flag_insert     = false;
   boolean canModifyNote   = false;
   boolean buttonAnnulla   = false;
   boolean readOnlyStr      = true;
   boolean canDelete = false;
   boolean sceltaMov = false;
   String flagDispo = "";
   String codDomanda = "";
   boolean canViewSchedaDispo = false;
   boolean canPrint = false;
   boolean canUpdate = false;
   boolean canComboStato = false;
   String numOreSett       = null;
   String dataDomanda = null;
   String motivoRiapertura = null;
   String codMonoAttiva = null;
   String nonImprenditore_flg = "";   
   
   boolean canTipoListaPatronato = false;
   boolean canAggiornaMBPatronato = false;
   boolean confirmContinuaMod = false;
   boolean readOnlyStrFlag = false;
   String errorConf = null;
   String msgConferma = null;
   String flgCasoDubbio = null;
   String strDescCasoDubbio = null;
   
   String str_conf_MBDUBBIO = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";

   Vector listeSpecRows= serviceResponse.getAttributeAsVector("M_GETSPECIFMOBILITA.ROWS.ROW");
   Testata operatoreInfo = null;   
   
   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   
   String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
   String _pagelistamob = (String) serviceRequest.getAttribute("PAGE_LISTA_MOB"); 
   
   String codCpi = user.getCodRif();
   String strDescrizione = "strDESC";
   
   	//lettura configurazione mobilità
	String configuarazioneMob = "0";  //configurazione di default
	String labelStatoDomanda = "Stato della richiesta";
	String labelDataCrt = "Data CRT";
	String labelRegioneCrt = "Regione CRT";
	String labelProvinciaCrt = "Provincia CRT";
	String labelNumeroCrt = "Numero CRT";
	if (serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")) {
		configuarazioneMob = serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString();
		if (configuarazioneMob.equals("1")) {
			labelStatoDomanda = "Stato della domanda";
			labelDataCrt = "Data CPM/Delibera Provinciale";
			labelRegioneCrt = "Regione CPM";
			labelProvinciaCrt = "Provincia CPM";
			labelNumeroCrt = "Numero CPM";
		}		
	}
   
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     if ((_pagelistamob != null) && (!_pagelistamob.equals(""))) {
	     testata.setPaginaLista(_pagelistamob);
     }
     else testata.setSkipLista(true);

    
     if(listeSpecRows != null && !listeSpecRows.isEmpty()) {
        listeSpec_Row  = (SourceBean) listeSpecRows.elementAt(0);
        prgMobilita    = (BigDecimal) listeSpec_Row.getAttribute("PRGMOBILITAISCR");
        prgMovimento   = (BigDecimal) listeSpec_Row.getAttribute("PRGMOVIMENTO");
        PRGUNITA       = (BigDecimal) listeSpec_Row.getAttribute("PRGUNITA");
        PRGAZIENDA     = (BigDecimal) listeSpec_Row.getAttribute("PRGAZIENDA");
        if (PRGUNITA == null) PRGUNITA = (BigDecimal) listeSpec_Row.getAttribute("PRGUNITAOLD");
        if (PRGAZIENDA == null) PRGAZIENDA = (BigDecimal) listeSpec_Row.getAttribute("PRGAZIENDAOLD");
        rowRagioneSociale = (String)  listeSpec_Row.getAttribute("STRRAGIONESOCIALE");
        if (rowRagioneSociale == null) rowRagioneSociale = (String)listeSpec_Row.getAttribute("STRRAGIONESOCIALEOLD"); 
        rowIndirizzo   = (String) listeSpec_Row.getAttribute("STRINDIRIZZO");
        if (rowIndirizzo == null) rowIndirizzo = (String)listeSpec_Row.getAttribute("STRINDIRIZZOOLD"); 
        rowComune      = (String)     listeSpec_Row.getAttribute("COMUNE");
        if (rowComune == null) rowComune = (String)listeSpec_Row.getAttribute("COMUNEOLD");

        rowProv      = (String)     listeSpec_Row.getAttribute("STRTARGA");
        if (rowProv == null) rowProv = (String)listeSpec_Row.getAttribute("STRTARGAOLD");
        rowCf        = (String)     listeSpec_Row.getAttribute("STRCODICEFISCALE");
        if (rowCf == null) rowCf = (String)listeSpec_Row.getAttribute("STRCODICEFISCALEOLD");

        rowPIva        = (String)     listeSpec_Row.getAttribute("STRPARTITAIVA");
        if (rowPIva == null) rowPIva = (String)listeSpec_Row.getAttribute("STRPARTITAIVAOLD");
        rowTel         = (String)     listeSpec_Row.getAttribute("STRTEL");
        if (rowTel == null) rowTel = (String)listeSpec_Row.getAttribute("STRTELOLD");
        dataFineMov    = (String)     listeSpec_Row.getAttribute("DATFINEMOV");
        if (dataFineMov == null) dataFineMov = (String)listeSpec_Row.getAttribute("DATFINEMOVOLD");
        dataInizMov    = (String)     listeSpec_Row.getAttribute("DATINIZIOMOV");
      	if (dataInizMov == null) dataInizMov = (String)listeSpec_Row.getAttribute("DATINIZIOMOVOLD");
        dataInizio     = (String)     listeSpec_Row.getAttribute("DATINIZIO"); 
        dataFine       = (String)     listeSpec_Row.getAttribute("DATFINE");
        dataFineOrig   = (String)     listeSpec_Row.getAttribute("DATFINEORIG");
	    dataMaxDiff	   = (String) 	  listeSpec_Row.getAttribute("DATMAXDIFF");
        motScorrDataMaxDiff = (String) listeSpec_Row.getAttribute("CODMOTIVODIFF");
        motivoFine	= (String) listeSpec_Row.getAttribute("CODMOTIVOFINE");
        datCRT=(String) listeSpec_Row.getAttribute("DATCRT");
        numCRT = (String) listeSpec_Row.getAttribute("STRNUMATTO");
        provCRT=(String) listeSpec_Row.getAttribute("PROVCRT");
        regCRT = (String) listeSpec_Row.getAttribute("REGCRT");
        codMBTipoLetto = (String)     listeSpec_Row.getAttribute("CODTIPOMOB");
        codMBStato = (BigDecimal) listeSpec_Row.getAttribute("CDNMBSTATORICH");
        indennita_flg  = (String)     listeSpec_Row.getAttribute("FLGINDENNITA"); 
        dataInizioIndenn = (String) listeSpec_Row.getAttribute("DATINIZIOINDENNITA");
        dataFineIndenn= (String) listeSpec_Row.getAttribute("DATFINEINDENNITA");
        mans           = (String)     listeSpec_Row.getAttribute("MANSIONE");
        codMans = (String)     listeSpec_Row.getAttribute("CODMANSIONE");
        codGrado = (String)     listeSpec_Row.getAttribute("CODGRADO");
        numLivello = (String)     listeSpec_Row.getAttribute("STRLIVELLO");
        codCCNL = (String)     listeSpec_Row.getAttribute("CODCCNL");
        strDescrizioneCCNL = (String) listeSpec_Row.getAttribute("DESCCCNL");
        strNote        = (String)     listeSpec_Row.getAttribute("STRNOTE");
        keyLock        = (BigDecimal) listeSpec_Row.getAttribute("NUMKLOMOBISCR");
        cdnUtIns       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTINS");
        cdnUtMod       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTMOD");
        dtmIns         = (String)     listeSpec_Row.getAttribute("DTMINS");
        dtmMod         = (String)     listeSpec_Row.getAttribute("DTMMOD");
        flagDispo = StringUtils.getAttributeStrNotNull(listeSpec_Row, "FLGSCHEDA");
        codDomanda = StringUtils.getAttributeStrNotNull(listeSpec_Row, "CODDOMANDA");
        dataDomanda = (String)     listeSpec_Row.getAttribute("DATADOMANDA");
        numOreSett = (String)     listeSpec_Row.getAttribute("NUMORESETT");
        motivoRiapertura = (String)listeSpec_Row.getAttribute("CODMVRIAPERTURA");
        codMonoAttiva = StringUtils.getAttributeStrNotNull(listeSpec_Row,"CODMONOATTIVA");
        if(!serviceResponse.containsAttribute("M_AggiornaStatoMobilita")){
       		nonImprenditore_flg = (String) listeSpec_Row.getAttribute("FLGNONIMPRENDITORE");
       		flgCasoDubbio = (String) listeSpec_Row.getAttribute("casoDubbio");
       		strDescCasoDubbio = (String)listeSpec_Row.getAttribute("strDescCasoDubbio");
        }else{
       		nonImprenditore_flg = (String) serviceRequest.getAttribute("FLGNONIMPRENDITORE");
       		flgCasoDubbio = (String) serviceRequest.getAttribute("casoDubbio");
       		strDescCasoDubbio = (String)serviceRequest.getAttribute("strDescCasoDubbio");
        }
     }//else

   String _page = (String) serviceRequest.getAttribute("PAGE");

   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   // NOTE: Attributi della pagina (pulsanti e link) 
   PageAttribs attributi = new PageAttribs(user, "MOBILITAINFOSTORDETTPAGE");	 
   canViewSchedaDispo = attributi.containsButton("DISPONIBILITA");
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   canDelete = attributi.containsButton("ELIMINA");
   canPrint = attributi.containsButton("STAMPA");
   canUpdate = attributi.containsButton("AGGIORNA_STATO");
   canComboStato = canUpdate;
   boolean canInviaDomanda=attributi.containsButton("INVIAMBO");
   
   if(serviceResponse.containsAttribute("M_AggiornaStatoMobilita")){
	   errorConf = (String)serviceResponse.getAttribute("M_AggiornaStatoMobilita.RECORD.RESULT");
   }
   
   if(errorConf!=null && errorConf.equals("ERROR")){
	   confirmContinuaMod = true;
	   msgConferma = "Lo stato della richiesta non è congruente col tipo di lista di mobilità. Si vuole procedere al salvataggio dei dati?";
   }
   
   String codStatoAgg = null;
   
   if(confirmContinuaMod){
	   codStatoAgg = (String)serviceRequest.getAttribute("codStatoMob");
   }

%>

<html>
<head>
<title>Amministrazione - Liste Speciali: mobilita</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<% String queryString = "cdnLavoratore="+cdnLavoratore+"&cdnFunzione="+cdnFunzione + "&page="+_page; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<script  language="JavaScript">
	function caricaGestioneSchedaInformativaUpdateFlag(prg,numKlo) {
		//window.location.reload(true);
		var url = "AdapterHTTP?PAGE=MobGestioneDisponibilitaPage";
		url += "&PRGMOBILITAISCR="+prg;
		url += "&FLGDISPONIBILITA=S";
		url += "&NUMKLOMOBISCR="+numKlo;				
		window.open(url, "Disponibilità", 'toolbar=0, scrollbars=1, height=500, width=550');
	}
	function stampa(){
    	apriGestioneDoc('RPT_DETTAGLIO_MOBILITA', '&prgMobilita=<%=prgMobilita%>&codCpi=<%=codCpi%>&strDescrizione=<%=strDescrizione%>&cdnLavoratore=<%=cdnLavoratore%>&regioneCRT=' + document.Frm1.regioneCRT.value + '&provCRT=' + document.Frm1.provCRT.value,'ALMOBO')
	}
	function delDispo(){
		document.Frm1.datInizMovHid.value = document.Frm1.datInizMov.value;
		document.Frm1.datFineMovHid.value = document.Frm1.datFineMov.value;
		document.Frm1.datInizioHid.value = document.Frm1.datInizio.value;
		document.Frm1.codGradoHid.value = document.Frm1.codGrado.value;
		if (document.Frm1.flagDisponibilita != null) {
			if (document.Frm1.flagDisponibilita.checked) {
				if (confirm("Verranno cancellate le disponibilità inserite, continuare ?")) {
					document.Frm1.flagDisponibilita.value = "";	
					document.Frm1.submit();
				}
			}
		}		
	}
	
	function continuaAggiornamento(valore){
		window.document.Frm1.codStatoMob.value = "<%= codStatoAgg %>";
		window.document.Frm1.CONTINUA_AGGIORNAMENTO_STORICO.value = valore;
		
		doFormSubmit(document.Frm1);
		
	}
	
	
	function conferma() {
		<%
		if (confirmContinuaMod) {
		%>
			if (confirm("<%=msgConferma%>")){ 
				continuaAggiornamento('true');
			}
	<%}%>
	}

	function checkDate(){
		var dataInizio = document.Frm1.datInizio.value;
		var dataFine =   document.Frm1.datFine.value;
		var criterio = compDate(dataInizio,dataFine);
		if (criterio==0){
			return confirm("Attenzione: la data inizio mobilità è uguale alla data fine mobilità.\nProseguire con l'invio della domanda?");
		}else if(criterio<0){
			return true;
		}else if(criterio>0){
			alert("La data inizio non può essere maggiore della data fine.");
			return false;
		}
		
	}
	
	function inviaDomanda() {
		
		if (!checkDate()){
			return false;
		}

		var codTipoMob = '<%=Utils.notNull(codMBTipoLetto)  %>';
		if (codTipoMob==''){
			codTipoMob=document.Frm1.codTipoMob[document.Frm1.codTipoMob.selectedIndex].value;
		}
		

		if (codTipoMob != "S1" && codTipoMob != "S2"){
			alert('ATTENZIONE: è possibile inviare domande di mobilità per i soli lavoratori che si trovano in uno dei seguenti stati:\n'+
					'- SOSPESI MOBILITA\' L.223/91\n'+
					'- SOSPESI MOBILITA\' L.236/93.');
			return false;
		}
		
		var test = <%=codMBStato%>;

		if (test == "2") {
			return true;
		}	
		
		alert('Per essere inviata l\'iscrizione si deve trovare nello stato \"DA APPROVARE\"');
		return false;		
	}	
</script>


<%@ include file="CommonScript.inc"%>
<%@ include file="UnderConstrScript.inc"%>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<script language="Javascript">
<% if ((_pagelistamob != null) && (!_pagelistamob.equals(""))) {  %>
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
 <%}%>
</script>
</head>
<body class="gestione" onload="conferma();controllaCasoDubbio();">
<% testata.show(out);%>
<br/>

<font color="red"><af:showErrors/></font>
<font color="green"><af:showMessages prefix="M_SaveMobilitaIscr"/></font>
<%if(!confirmContinuaMod){ %>
<font color="green"><af:showMessages prefix="M_AggiornaStatoMobilita"/></font>
<% } %>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p align="center">
<table class="main">
<tr><td colspan="2"><p class="titolo">Informazioni storiche relative alla mobilità</p></td></tr>
<tr > <td><br/></td> <td></td> </tr>
</table>
<%out.print(htmlStreamTop);%>
<%@ include file="MobilitaCampiLayOut.inc"%>

<%/*Stampa mobilità storicizzate. Alessandro Pegoraro 28/11/2007*/
if (canPrint) {%>
	<input type="button" class="pulsanti" value="Stampa" onclick="stampa()"/>
  <%}%>
	<input type="button" class="pulsante" name="docuAssociati" value="Documenti associati" 
	onclick="docAssociati(<%=cdnLavoratore%>,'AmministrListeSpecPage',<%=cdnFunzione%>,'',<%=prgMobilita%>)"/>
  <% boolean abilitaInvio = true;
  	abilitaInvio = (codMBStato != null && 	//abilito il pulsante se lo stato è da 'DA APPROVARE'
  					(2 == codMBStato.intValue()) &&
  					"S".equalsIgnoreCase(codMonoAttiva) && //e se codMonoattività è  'S'
  					canInviaDomanda	//e l'utente è abilitato
  					  );
    if(canUpdate){ %>
  	<input class="pulsante" type="submit" name="aggiorna" value="Aggiorna">
 <% } 
  	
  	 if (abilitaInvio) {
  %>
	<input type="submit" class="pulsanti" name="inviaDomandaNCR" value="Invia domanda" onclick="return inviaDomanda()"/>
	<input type="hidden" name="codMBStato" value="<%=codMBStato%>"/>
	<input type="hidden" name="codMonoAttiva" value="<%=codMonoAttiva%>"/>
  <%} %>
<%out.print(htmlStreamBottom);%>


<br/>
<center><% operatoreInfo.showHTML(out); %></center>

<br/>
<table class="main">
  <tr>
    <%if(!readOnlyStr){%>
    <td width="33%">&nbsp;</td>
    <td  width="34%" align="center">
      <%keyLock= keyLock.add(new BigDecimal(1));%>
      <input class="pulsante" type="submit" name="save" value="Aggiorna">
    </td>
    <td width="33%" align="center">
    <%} else if ((_pagelistamob == null) || (_pagelistamob.equals(""))) {%><td align="center">
        <input class="pulsante" type="button" name="lista" value="Torna alla lista"
               onClick="checkChange('MobilitaInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>&CDNFUNZIONE=<%=cdnFunzione%>')"/> 
    </td>
    <%}%>
  </tr>
  
</table>

<input type="hidden" name="PAGE" value="MobilitaInfoStorDettPage"/>
<input type="hidden" name="cdnLavoratore"   value="<%=Utils.notNull(cdnLavoratore)%>"/>
<input type="hidden" name="cdnUtMod"        value="<%=Utils.notNull(cdnUtMod)%>"/>
<input type="hidden" name="numKloMobIscr"   value="<%=Utils.notNull(keyLock)%>"/>
<input type="hidden" name="prgMobilitaIscr" value="<%=Utils.notNull(prgMobilita)%>"/>
<input type="hidden" name="prgMovimento"    value="<%=Utils.notNull(prgMovimento)%>"/>
<input type="hidden" name="CDNFUNZIONE"    value="<%=Utils.notNull(cdnFunzione)%>"/>
  

</af:form>




</body>
</html>
