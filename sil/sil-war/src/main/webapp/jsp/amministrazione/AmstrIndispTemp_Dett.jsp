<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%   
    Vector rows = serviceResponse.getAttributeAsVector("M_GETINDISPTEMPDETT.ROWS.ROW");
    boolean infStor = serviceRequest.containsAttribute("INFSTOR");

    SourceBean row           = null;
    String cdnLavoratore     = null;
    String descrizione       = null;
    String dataInizio        = null;
    String dataFine          = null;
    String codIndTempLetto   = null;
    String strNote           = null;
    String dtmIns            = null;
    String dtmMod            = null;
    String flagDocumentata = null;
    BigDecimal cdnUtMod      = null;
    BigDecimal cdnUtIns      = null;
    BigDecimal keyLock       = null;
    String prgIndispTemp = null;
    Testata operatoreInfo   = null;   
    InfCorrentiLav testata = null;
//////
/*
    String PRG_TAB_DA_ASSOCIARE=null; 
    String COD_LST_TAB="AM_IND_T";
    String PRG_PATTO_LAVORATORE=null;
    String STATO_ATTO=null;
    String DATA_STIPULA=null;
    String TIPOLOGIA=null; 
    String PRG_LAV_PATTO_SCELTA=null;
    */
    String COD_LST_TAB="AM_IND_T";
    String PRG_TAB_DA_ASSOCIARE=null; 
   /////////
    
    cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    prgIndispTemp   = (String) serviceRequest.getAttribute("PRGINDISPTEMP");
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    int prgIndTemp = -1;
    if(rows != null && !rows.isEmpty()) {   
        prgIndTemp = ((BigDecimal)((SourceBean) rows.elementAt(0)).getAttribute("PRGINDISPTEMP")).intValue();
    }
    String htmlStreamTop = StyleUtils.roundTopTable(false);
   	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
     
    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     
     //----------------------
 
     PageAttribs attributi = new PageAttribs(user, "AmstrIndispTempPage");
     boolean readOnlyStr = true; //!attributi.containsButton("AGGIORNA");
     boolean infStorButt =  attributi.containsButton("INF_STOR");
      
    if(rows != null && !rows.isEmpty())  { 
        row  = (SourceBean) rows.elementAt(0);    
        descrizione     = (String)      row.getAttribute("DESCRIZIONE");
        codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
        dataInizio      = (String)      row.getAttribute("DATINIZIO");         
        dataFine        = (String)      row.getAttribute("DATFINE");         
        strNote         = (String)      row.getAttribute("STRNOTE");         
        dtmIns          = (String)      row.getAttribute("DTMINS");         
        dtmMod          = (String)      row.getAttribute("DTMMOD");         
        cdnUtIns        = (BigDecimal)  row.getAttribute("CDNUTINS");         
        cdnUtMod        = (BigDecimal)  row.getAttribute("CDNUTMOD");
        flagDocumentata = (String)row.getAttribute("FLGDOCUMENTATA");
        if (flagDocumentata==null) flagDocumentata = "";
        keyLock         = (BigDecimal)  row.getAttribute("NUMKLOINDISPTEMP");
        PRG_TAB_DA_ASSOCIARE=((BigDecimal)  row.getAttribute("PRGINDISPTEMP")).toString();
        //
        operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    }
%>
<html>
<head>
<title>Amministrazione - Disponibilita Temporanea: Dettaglio</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<script>

<!--
function toCaller(cdnLav, cdnFun) {
    urlpage="AdapterHTTP?";
    urlpage+="cdnLavoratore="+cdnLav+"&";
    urlpage+="PAGE=AmstrIndispTempPage&";
    urlpage+="cdnFunzione="+cdnFun;
    
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
    setWindowLocation(urlpage);
}

function getFormObj() {return document.form1;}
<%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
//-->
</script>
<%@ include file="openPage.inc"%>
<script language="Javascript">
  <%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

</head>

<body class="gestione" onLoad="rinfresca();">

      
<%
    if(!infStor)  { 
        testata.show(out);
        Linguette l  = new Linguette(user,  _cdnFunz, "AmstrIndispTempPage", new BigDecimal(cdnLavoratore));
        l.show(out);
     }
%>
<p align="center">

<br/><!-- Messaggi di controllo-->
<font color="red">
 <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="M_SaveIndispTemp"/>
  <af:showMessages prefix="M_DelLavPattoScelta"/>
</font>

<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto()">
<table class="main">
    <tr><td colspan="4" ><p class="titolo">Condizione</p></td>
    <tr><td colspan=4><br/></td></tr>
</table>
<%out.print(htmlStreamTop);%>
<table class="main">
    <tr>
        <td class="etichetta">Tipo&nbsp;condizione</td>
        <td class="campo"><af:comboBox classNameBase="input" name="codIndTemp" moduleName="M_GETDEINDISPTEMP" selectedValue="<%=codIndTempLetto%>"
                addBlank="true" title="Tipo indisponibilita" required="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
        </td>
        <td class="etichetta">Documentata</td>
        <td class="campo"><af:comboBox classNameBase="input" name="flgDocumentata" 
                addBlank="true" title="Documentata" required="false" disabled="<%=String.valueOf(readOnlyStr)%>">  
                <option value="S" <%=flagDocumentata.equals("S")?"selected='true'":""%>>S</option>
                <option value="N" <%=flagDocumentata.equals("N")?"selected='true'":""%>>N</option>
            </af:comboBox>
        </td>
    </tr>
    <tr>
     <td class="etichetta">Data inizio</td>
     <td colspan="3" align="left"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
     </td> 
    </tr>
    <tr>
     <td class="etichetta">Data fine</td>
     <td colspan="3" align="left"><af:textBox classNameBase="input" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
              required="false" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
     </td>
    </tr>
    <tr>        
        <td class="etichetta">Note<br/></td>
        <td class="campo" colspan=3> 
            <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                     cols="60" rows="4" maxlength="1000"
                     onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
        </td>                        
    </tr>
    <!--///////////////////////////////////////////////////////////////////////////////////////////////////////////////-->
    <tr><%if(!infStor){%>
        <td colspan="4"><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>      
        </td><%}%>
    </tr>
</table>
<%out.print(htmlStreamBottom);%>
<table class="main">
<tr>
    <td colspan=4>
        <center><% operatoreInfo.showHTML(out); %></center>
    </td>
</tr>    
<tr>
  <td colspan="4">
    <table class="main" width="100%">
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td></td>
        <td align="center">
        <%if(!readOnlyStr){
          if (keyLock != null) keyLock= keyLock.add(new BigDecimal(1));%>
          <input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna">
        <%}%></td>
        <td align="center">
         <%if(infStor){%>
          <input type="button" class="pulsanti" name="annulla" value="Torna alla lista"
                 onclick="openPage('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
         <%} else {%>
          <input type="button" class="pulsanti" name="annulla" value="Chiudi senza aggiornare"
                 onclick="openPage('AmstrIndispTempPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
         <%}%>
        </td>
      </tr>
    </table>
    
    <input type="hidden" name="PAGE" value="IndispTempInfStorPage"/>
    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="keyLockIndispTemp" value="<%=Utils.notNull(keyLock)%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
    <input type="hidden" name="PRGINDISPTEMP" value="<%=prgIndispTemp%>"/>
  </td>
</tr>

</table>

</af:form>

</p>
</body>
</html>
