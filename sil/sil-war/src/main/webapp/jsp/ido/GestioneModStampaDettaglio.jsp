<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  BigDecimal prgModelloStampa = null;
  BigDecimal prgTipoModello  = null;
  String strDescrizione  = "";
  String codOggModello   = "";
  String corpoMessaggio  = "";
  String strNote         = "";
  String rifDaContattare = "";
  String datInizioVal    = "";
  String datFineVal      = "";

  boolean flag_insert = serviceRequest.containsAttribute("nuovo");

  SourceBean row= (SourceBean) serviceResponse.getAttribute("M_GETSPECIFICOMODELLO.ROWS.ROW");
  if(row != null)
  { prgModelloStampa= (BigDecimal) row.getAttribute("PRGMODELLOSTAMPA");
    strDescrizione  = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
    codOggModello   = StringUtils.getAttributeStrNotNull(row, "CODOGGETTOMODELLO");
    prgTipoModello  = (BigDecimal) row.getAttribute("PRGTIPOMODELLO");
    corpoMessaggio  = StringUtils.getAttributeStrNotNull(row, "STRCORPOMESSAGGIO");
    strNote         = StringUtils.getAttributeStrNotNull(row, "STRNOTE");
    rifDaContattare = StringUtils.getAttributeStrNotNull(row, "STRRIFERIMENTODACONTATTARE");
    datInizioVal    = StringUtils.getAttributeStrNotNull(row, "DATINIZIOVAL");
    datFineVal      = StringUtils.getAttributeStrNotNull(row, "DATFINEVAL");
  }

  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean readOnly = !attributi.containsButton("AGGIORNA"); 
  String  readOnlyStr = readOnly ? "true" : "false";
  boolean canInsert   = attributi.containsButton("INSERISCI");
  boolean canAggiorna = attributi.containsButton("AGGIORNA");
  boolean canModify = false;
  if ((!flag_insert && canAggiorna) || (flag_insert && canInsert))
  { canModify=true;  
  }



  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>



<html>
<head>
    <title>GestioneModStampaDettaglio.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>


<script language="JavaScript">
<!--
function checkDate(objData1, objData2) {

  strData1=objData1.value;
  strData2=objData2.value;

  //costruisco la data della richiesta
  d1giorno=parseInt(strData1.substr(0,2),10);
  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4),10);
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2),10);
  d2mese=parseInt(strData2.substr(3,2),10)-1;
  d2anno=parseInt(strData2.substr(6,4),10);
  data2=new Date(d2anno, d2mese, d2giorno);
  
  ok=true;
  if (data2 < data1) {
      alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;
   }
  return ok;
}

function cameBack()
{
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    url="AdapterHTTP?PAGE=GestioneModelloStampaPage&cdnFunzione="+<%=_cdnFunz%>;
    setWindowLocation(url);
}
-->
</script>
</head>

<body class="gestione" onload="rinfresca()">
<br/><br/>
<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkDate(document.Frm1.datInizioVal,document.Frm1.datFineVal)">
 <%out.print(htmlStreamTop);%>
 <table class="main">
  <tr><td colspan="2"><p class="titolo">Modello di stampa</p></td></tr>
  <tr><td colspan="2">
        <font color="green">
        <af:showMessages prefix="M_InserisciModello" />
        <af:showMessages prefix="M_SalvaModello"/>
        </font>
        <font color="red"><af:showErrors/></font>
  </td></tr>
  <tr><td><br/></td></tr>
  <tr>
    <td class="etichetta">Modello</td>
    <td class="campo">
         <af:textBox name="strDescrizione" type="text" classNameBase="input" value="<%=strDescrizione%>" readonly="<%=readOnlyStr%>"
                     maxlength="100" size="<%=((int)(strDescrizione.length()*1.2))%>"/>
    </td>
  </tr>

  <tr>
    <td class="etichetta">Tipo modello</td>
    <td class="campo">
      <af:comboBox classNameBase="input" name="prgTipoModello" selectedValue="<%=(prgTipoModello!=null) ? prgTipoModello.toString() : \"\"%>" moduleName="M_GetTipoModello"  addBlank="true"
                   disabled="<%=readOnlyStr%>" required="true" title="Tipo modello"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Oggetto modello</td>
    <td class="campo">
      <af:comboBox classNameBase="input" name="codOggModello" selectedValue="<%=codOggModello%>" moduleName="M_GetOggettoModello" addBlank="true"
                   disabled="<%=readOnlyStr%>" required="true" title="Oggetto modello"/>
    </td>
  </tr>
  
  <tr>
    <td class="etichetta">Corpo messaggio</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="corpoMessaggio" value="<%=corpoMessaggio%>"
                   cols="60" rows="4" maxlength="4000" readonly="<%=readOnlyStr%>"/></td>
    </td>
  </tr>

  <tr>
    <td class="etichetta">Note</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                   cols="60" rows="4" maxlength="2000"  readonly="<%=readOnlyStr%>"/></td>
  </tr>

  <tr>
    <td class="etichetta">Riferimento da contattare</td>
    <td class="campo">
         <af:textBox name="rifDaContattare" classNameBase="input" value="<%=rifDaContattare%>" readonly="<%=readOnlyStr%>"
                     maxlength="500" size="<%=((int)(rifDaContattare.length()*1.2))%>"/>
    </td>
  </tr>

  <tr>
    <td class="etichetta">Inizio validit&agrave</td>
    <td class="campo">
         <af:textBox name="datInizioVal" type="date" classNameBase="input" value="<%=datInizioVal%>" readonly="<%=readOnlyStr%>" validateOnPost="true"
                     size="11" maxlength="10" required="true" title="Inizio data validità"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Fine validit&agrave</td>
    <td class="campo">
         <af:textBox name="datFineVal" type="date" classNameBase="input" value="<%=datFineVal%>" readonly="<%=readOnlyStr%>" validateOnPost="true"
                     size="11" maxlength="10" required="true" title="Fine data validità"/>
    </td>
  </tr>

  <tr><td><br/></td></tr>
  <tr><td><br/></td></tr>
  <tr>
    <td align ="center" colspan="2">
    <%if (canModify) {
        if(!flag_insert) {%>
          <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      <%} else {%>
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci">
      <%}
     }%>
     <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="cameBack()">
    </td>
  </tr>
    
  <tr><td><br/></td></tr>
 </table>
 <%out.print(htmlStreamBottom);%>

<input type="hidden" name="PAGE" value="GestioneModStampaDettaglioPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="prgModelloStampa" value="<%=prgModelloStampa%>"/>

</af:form> 
</p>
</body>
</html>
