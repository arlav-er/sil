<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
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
   String dataFineOrig     = null;
   String dataMaxDiff      = null;
   String motScorrDataMaxDiff = null;  
   String codMBTipoLetto   = null;
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
   String numOreSett       = null;
   String dataDomanda 	   = null;
   String dtmIns           = null;
   String dtmMod           = null;
   BigDecimal prgMobilita  = null;
   BigDecimal prgMovimento = null;
   BigDecimal cdnUtMod     = null;
   BigDecimal cdnUtIns     = null;
   BigDecimal keyLock      = null;
   boolean flag_insert     = false;
   boolean buttonAnnulla   = false;
   boolean readOnlyStr      = true;
   boolean canModifyNote    = false;
   boolean sceltaMov = false;
   String flagDispo = "";
   String codDomanda = "";	//non utilizzato
   boolean canViewSchedaDispo = false;
   boolean canDelete = false;
   boolean canTipoListaPatronato = false;
   boolean canAggiornaMBPatronato = false;
   String motivoRiapertura = null;
   String nonImprenditore_flg = null;
   String flgCasoDubbio = null;
   String strDescCasoDubbio = null;
   
   String str_conf_MBDUBBIO = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";
   
   BigDecimal codMBStato = null;
   boolean canUpdate = false;
   boolean canComboStato = false;
   boolean readOnlyStrFlag = true;

   String _page = (String) serviceRequest.getAttribute("PAGE");
   
   Vector listeSpecRows= serviceResponse.getAttributeAsVector("M_GETSPECIFMOBILITA.ROWS.ROW");
   Testata operatoreInfo = null;   
   
   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   
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
   
   /*
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setSkipLista(true);
    */
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
        rowIndirizzo   = (String)     listeSpec_Row.getAttribute("STRINDIRIZZO");
        if (rowIndirizzo == null) rowIndirizzo = (String)listeSpec_Row.getAttribute("STRINDIRIZZOOLD"); 
        rowComune      = (String)     listeSpec_Row.getAttribute("COMUNE");
        if (rowComune == null) rowComune = (String)listeSpec_Row.getAttribute("COMUNEOLD");

        rowProv      = "(" + (String)     listeSpec_Row.getAttribute("STRTARGA") + ")";
        if (rowProv == null) rowProv = "(" + (String)listeSpec_Row.getAttribute("STRTARGAOLD") + ")";
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
        codMBStato = (BigDecimal) listeSpec_Row.getAttribute("CDNMBSTATORICH");
        dataDomanda = (String)     listeSpec_Row.getAttribute("DATADOMANDA");
        numOreSett = (String)     listeSpec_Row.getAttribute("NUMORESETT");
        motivoRiapertura = (String)listeSpec_Row.getAttribute("CODMVRIAPERTURA");
        nonImprenditore_flg = (String)listeSpec_Row.getAttribute("FLGNONIMPRENDITORE");
        flgCasoDubbio = (String)listeSpec_Row.getAttribute("casoDubbio");
        strDescCasoDubbio = (String)listeSpec_Row.getAttribute("strDescCasoDubbio");
     }//else

    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

   	//NOTE: Attributi della pagina (pulsanti e link) 
	//PageAttribs attributi = new PageAttribs(user, "AmministrListeSpecPage");
   	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Percorso lavoratore: Mobilità</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>
<%@ include file="UnderConstrScript.inc"%>
</head>
<body class="gestione" onload="controllaCasoDubbio();">
<br/>
<font color="red"><af:showErrors/></font>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p align="center">

<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2"><p class="titolo">Dettaglio mobilit&agrave;</p></td></tr>
<tr > <td><br/></td> <td></td> </tr>
</table>

<%@ include file="MobilitaCampiLayOut.inc"%>

<br/>


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
    <%} else {%><td align="center"><%}%>
        <!--input class="pulsante" type="button" name="lista" value="Torna alla lista"
               onClick="checkChange('MobilitaInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"/-->
    </td>
  </tr>
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="PAGE" value="MobilitaInfoStorDettPage"/>
<input type="hidden" name="cdnLavoratore"   value="<%=Utils.notNull(cdnLavoratore)%>"/>
<input type="hidden" name="cdnUtMod"        value="<%=Utils.notNull(cdnUtMod)%>"/>
<input type="hidden" name="numKloMobIscr"   value="<%=Utils.notNull(keyLock)%>"/>
<input type="hidden" name="prgMobilitaIscr" value="<%=Utils.notNull(prgMobilita)%>"/>
<input type="hidden" name="prgMovimento"    value="<%=Utils.notNull(prgMovimento)%>"/>
  
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>
</af:form>




</body>
</html>
