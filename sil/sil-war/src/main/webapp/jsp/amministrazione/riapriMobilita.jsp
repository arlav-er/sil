 <%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.DateUtils"%>
           
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
   String codDomanda = ""; //non utilizzato
   BigDecimal prgMobilita  = null;
   BigDecimal prgMovimento = null;
   BigDecimal cdnUtMod     = null;
   BigDecimal cdnUtIns     = null;
   BigDecimal keyLock      = null;
   String numOreSett       = null;
   String oreSettimanali = null;
   String tipoMansione = null;
   String dataDomanda = null;
   String motivoRiapertura = null;
   boolean readOnlyStr=false;
   boolean confirmContinuaMod = false;
   String errorConf = null;
   String msgConferma = null;
   if(serviceResponse.containsAttribute("M_RiapriMobilitaIscr")){
	 errorConf = (String)serviceResponse.getAttribute("M_RiapriMobilitaIscr.RECORD.RESULT");
   }
   if(errorConf!=null && errorConf.equals("ERRORSAVE")){
	 confirmContinuaMod = true;
     msgConferma = "Lo stato della richiesta non è congruente col tipo di lista di mobilità. Si vuole procedere al salvataggio dei dati?";
   }
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
        dataDomanda = (String)     listeSpec_Row.getAttribute("DATADOMANDA");
        numOreSett = (String)     listeSpec_Row.getAttribute("NUMORESETT");
        motivoRiapertura = (String)listeSpec_Row.getAttribute("CODMVRIAPERTURA");
     }
     
     if (confirmContinuaMod) {
        codMans = (String)serviceRequest.getAttribute("CODMANSIONE");
		tipoMansione = (String)serviceRequest.getAttribute("CODTIPOMANSIONE");
		mans = (String)serviceRequest.getAttribute("DESCMANSIONE");
		motivoFine = (String)serviceRequest.getAttribute("MotDecad");
		motScorrDataMaxDiff = (String)serviceRequest.getAttribute("MotScorrDataMaxDiff");
		codCCNL = (String)serviceRequest.getAttribute("codCCNL");
		codGrado = (String)serviceRequest.getAttribute("codGrado");
		codMBStato = (serviceRequest.getAttribute("codStatoMob") != null &&
					  !serviceRequest.getAttribute("codStatoMob").toString().equals("")) ? 
					new BigDecimal(serviceRequest.getAttribute("codStatoMob").toString()):null;
		codMBTipoLetto = (String)serviceRequest.getAttribute("codTipoMob");
		datCRT = (String)serviceRequest.getAttribute("datCRT");
		dataFine = (String)serviceRequest.getAttribute("datFine");
		dataFineOrig = (String)serviceRequest.getAttribute("datFineOrig");
		dataMaxDiff = (String)serviceRequest.getAttribute("datMaxDiff");
		dataFineIndenn = (String)serviceRequest.getAttribute("dataFineIndenn");
		dataInizioIndenn = (String)serviceRequest.getAttribute("dataInizioIndenn");
		indennita_flg = (String)serviceRequest.getAttribute("flgIndennita");
		numCRT = (String)serviceRequest.getAttribute("numCRT");
		prgMovimento = (serviceRequest.getAttribute("prgMovimento") != null &&
				 	    !serviceRequest.getAttribute("prgMovimento").toString().equals("")) ? 
						new BigDecimal(serviceRequest.getAttribute("prgMovimento").toString()):null;
		provCRT = (String)serviceRequest.getAttribute("provCRT");
		regCRT = (String)serviceRequest.getAttribute("regioneCRT");
		strDescrizioneCCNL = (String)serviceRequest.getAttribute("strCCNL");
		numLivello = (String)serviceRequest.getAttribute("strLivello");
		dataDomanda = (String)serviceRequest.getAttribute("dataDomanda");
		oreSettimanali = (String)serviceRequest.getAttribute("numOreSett");
		motivoRiapertura = (String)serviceRequest.getAttribute("MotRiapertura");
     }
     
     String _page = (String) serviceRequest.getAttribute("PAGE");
     operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
     // NOTE: Attributi della pagina (pulsanti e link) 
     PageAttribs attributi = new PageAttribs(user, "MobilitaInfoStorDettPage");	 
     String htmlStreamTop = StyleUtils.roundTopTable(false);
     String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>

<html>
<head>
<title>Amministrazione - Liste Speciali: mobilita</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<% String queryString = "cdnLavoratore="+cdnLavoratore+"&cdnFunzione="+cdnFunzione + "&page="+_page; %>

<script  language="JavaScript">


<%@ include file="_controlloDate_script.inc"%>

function checkScorrimento() {
	var dataFineIndennita = document.Frm1.dataFineIndenn.value;
	var dataFineIndennitaPrec = document.Frm1.dataFineIndennHid.value;
	if (dataFineIndennita != dataFineIndennitaPrec) {
		if(confirm('Data fine indennizzo modificata, se vuoi effettuare lo scorrimento clicca su OK,\naltrimenti ANNULLA per non effettuare lo scorrimento')) {
			document.Frm1.ABILITASCORRIMENTO.value = "true";
		}
	}
	return true;
}

function continuaModMob(valore) {
	
	  window.document.Frm1.CONTINUA_AGGIORNAMENTO_MOB.value = valore;
	  window.document.Frm1.PRGAZIENDA.value = "<%=PRGAZIENDA %>";
      window.document.Frm1.PRGUNITA.value   = "<%=PRGUNITA %>";
      window.document.Frm1.Ragione.value    = "<%=rowRagioneSociale %>";
      window.document.Frm1.Indirizzo.value  = "<%=rowIndirizzo %>";
      window.document.Frm1.Comune.value     = "<%=rowComune %>";
      window.document.Frm1.PIva.value       = "<%=rowPIva %>";
      window.document.Frm1.CF.value       = "<%=rowCf%>";
	  window.document.Frm1.Prov.value       = "<%=rowProv %>";      
      window.document.Frm1.strTel.value     = "<%=rowTel %>";
      window.document.Frm1.datInizMov.value = "<%=dataInizMov  %>";
      window.document.Frm1.datFineMov.value = "<%=dataFineMov  %>";
      window.document.Frm1.datFine.value      = "<%=dataFine  %>";
      window.document.Frm1.datFineOrig.value  = "<%=dataFineOrig  %>";
      window.document.Frm1.datMaxDiff.value      = "<%=dataMaxDiff  %>";
      window.document.Frm1.flgIndennita.value = "<%=indennita_flg   %>";
      window.document.Frm1.dataInizioIndenn.value = "<%=dataInizioIndenn  %>";
      window.document.Frm1.dataFineIndenn.value = "<%=dataFineIndenn  %>";
      window.document.Frm1.DESCMANSIONE.value = "<%=mans %>";
      window.document.Frm1.CODMANSIONE.value  = "<%=codMans%>";
      window.document.Frm1.codCCNL.value = "<%=codCCNL %>";
      window.document.Frm1.strCCNL.value = "<%=strDescrizioneCCNL  %>";
      window.document.Frm1.codGrado.value = "<%=codGrado %>";
      window.document.Frm1.prgMovimento.value = "<%= prgMovimento%>";
      window.document.Frm1.CODTIPOMANSIONE.value = "<%=tipoMansione %>";
	  window.document.Frm1.MotDecad.value = "<%=motivoFine %>";
	  window.document.Frm1.MotScorrDataMaxDiff.value = "<%=motScorrDataMaxDiff %>";
	  window.document.Frm1.codStatoMob.value = "<%=codMBStato  %>";
	  window.document.Frm1.codTipoMob.value = "<%=codMBTipoLetto  %>";
	  window.document.Frm1.datCRT.value = "<%=datCRT  %>";
	  window.document.Frm1.numCRT.value = "<%=numCRT   %>";
	  window.document.Frm1.provCRT.value = "<%=provCRT  %>";
	  window.document.Frm1.regioneCRT.value = "<%=regCRT %>";
	  window.document.Frm1.strLivello.value = "<%=numLivello %>";
	  window.document.Frm1.dataDomanda.value  = "<%=dataDomanda  %>";
	  window.document.Frm1.numOreSett.value  = "<%=oreSettimanali  %>";
	  window.document.Frm1.MotRiapertura.value  = "<%=motivoRiapertura  %>";
	  window.document.Frm1.datInizioHid.value    = "<%=dataInizio %>";
	  window.document.Frm1.datInizMovHid.value = "<%=dataInizMov  %>";
      window.document.Frm1.datFineMovHid.value = "<%=dataFineMov  %>";
      window.document.Frm1.codGradoHid.value = "<%=codGrado %>";
	  doFormSubmit(document.Frm1);
	}

function conferma() {
	<%if(confirmContinuaMod){ %>
		if (confirm("<%=msgConferma%>")) continuaModMob('true');
	<% } %>
}

function checkDataFineMotivazione(){
		var dataFineObj = eval('document.Frm1.datFine');
		var dataFine = dataFineObj.value;
		var motDecadobj = eval('document.Frm1.MotDecad');
		var motDecad = motDecadobj[motDecadobj.selectedIndex].text;
		if (isFuture(dataFine) && motDecad!=""){
			return confirm('Attenzione, si vuole lasciare\r\n valorizzato il motivo di\r\n decadenza?');
		}else{
			return true;
		}
}

</script>


<%@ include file="CommonScript.inc"%>
<%@ include file="UnderConstrScript.inc"%>
<script language="Javascript">
<% if ((_pagelistamob != null) && (!_pagelistamob.equals(""))) {  %>
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
 <%}%>
</script>
</head>
<body class="gestione" onload="rinfresca();conferma();">
<% testata.show(out);%>
<br/>

<font color="red"><af:showErrors/></font>
<font color="green"><af:showMessages prefix="M_RiapriMobilitaIscr"/></font>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controlloMobilita() && checkScorrimento() && checkDataFineMotivazione()">
<p align="center">
<table class="main">
<tr><td colspan="2"><p class="titolo">Informazioni storiche relative alla mobilità</p></td></tr>
<tr > <td><br/></td> <td></td> </tr>
</table>
<%out.print(htmlStreamTop);%>
<%@ include file="RiaperturaMobilitaCampiLayOut.inc"%>
  
<%out.print(htmlStreamBottom);%>


<br/>
<center><% operatoreInfo.showHTML(out); %></center>

<br/>
<center>
<table class="main">
  <tr>
    <td align="center">
      <%keyLock= keyLock.add(new BigDecimal(1));%>
      <input class="pulsante" type="submit" name="save" value="Riapri mobilità">
      <% if ((_pagelistamob == null) || (_pagelistamob.equals(""))) {%>
      	&nbsp;<input class="pulsante" type="button" name="lista" value="Torna alla lista"
               onClick="checkChange('MobilitaInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"/>
    </td>
    <%}%>
  </tr>
  
</table>
</center>

<input type="hidden" name="PAGE" value="MobilitaRiapriPage"/>
<input type="hidden" name="cdnLavoratore"   value="<%=Utils.notNull(cdnLavoratore)%>"/>
<input type="hidden" name="cdnUtMod"        value="<%=Utils.notNull(cdnUtMod)%>"/>
<input type="hidden" name="numKloMobIscr"   value="<%=Utils.notNull(keyLock)%>"/>
<input type="hidden" name="prgMobilitaIscr" value="<%=Utils.notNull(prgMobilita)%>"/>
<input type="hidden" name="prgMovimento"    value="<%=Utils.notNull(prgMovimento)%>"/>
<input type="hidden" name="ABILITASCORRIMENTO" value="false"/>
<input type="hidden" name="CONTINUA_AGGIORNAMENTO_MOB" value="false"/>
<input type="hidden" name="datInizMovHid" value=""/>
<input type="hidden" name="datFineMovHid"    value=""/>
<input type="hidden" name="datInizioHid" value=""/>
<input type="hidden" name="codGradoHid" value=""/>

</af:form>

</body>
</html>
