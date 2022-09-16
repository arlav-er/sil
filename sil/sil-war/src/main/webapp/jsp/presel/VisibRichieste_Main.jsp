<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../amministrazione/openPage.inc" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String strSoggetti="";
  String strTipo="";
  String strAutorizzazione="";
  String dataAutorizzazione="";
  String prgRichiestaAz="";
  String _page ="";
  int _funzione;
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  boolean nuovo=true;
 // InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canInsert= false;
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();


 
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  SourceBean rigaTestata = null;
  SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA");
  Vector rows_VectorTestata = null;
  rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorTestata.size()!=0) {
    rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
    prgAzienda = (BigDecimal) rigaTestata.getAttribute("PRGAZIENDA");
    prgUnita = (BigDecimal) rigaTestata.getAttribute("PRGUNITA");
  }
  
 

  prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  _page = (String) serviceRequest.getAttribute("PAGE"); 
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  
  SourceBean AzInfo = (SourceBean) serviceResponse.getAttribute("M_SelectVisibRich.ROWS.ROW");
  if ( AzInfo!=null) {
      nuovo=false;
      if (AzInfo.containsAttribute("cdnUtins")) {
        cdnUtins=AzInfo.getAttribute("cdnUtins").toString();
      }
      if (AzInfo.containsAttribute("dtmins")) {
        dtmins=AzInfo.getAttribute("dtmins").toString();
      }
      if (AzInfo.containsAttribute("cdnUtmod")) {
        cdnUtmod=AzInfo.getAttribute("cdnUtmod").toString();
      }
      if (AzInfo.containsAttribute("dtmmod")) {
        dtmmod=AzInfo.getAttribute("dtmmod").toString();
      }
      if (AzInfo.containsAttribute("codMonoSoggetti")) {
        strSoggetti=AzInfo.getAttribute("codMonoSoggetti").toString();
      }
      if (AzInfo.containsAttribute("codMonoTipo")) {
        strTipo=AzInfo.getAttribute("codMonoTipo").toString();
      }
      if (AzInfo.containsAttribute("strAutorizzazione")) {
        strAutorizzazione=AzInfo.getAttribute("strAutorizzazione").toString();
      }
      if (AzInfo.containsAttribute("datAutorizzazione")) {
        dataAutorizzazione=AzInfo.getAttribute("datAutorizzazione").toString();
      }
  }  
//  infCorrentiLav= new InfCorrentiLav(prgRichiestaAz, user);
  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  if (cdnStatoRich.compareTo("4")!=0){
    canInsert = attributi.containsButton("inserisci");
    canDelete= attributi.containsButton("cancella");
    canModify= attributi.containsButton("aggiorna");
  }

  linguette.setCodiceItem("prgRichiestaAz");
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <title>Gestione visibilit&agrave; richiste</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  
<script language="Javascript">
   <% 
    //Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer,responseContainer,"");
    %>
</script>
<SCRIPT language="javascript">
<!--
  var flagChanged = false;

  function controllaCampi(){
    var ris;
    if ( document.Frm1.codMonoTipo.value == "" ){
      alert ("Il campo 'Tipo di visibilità' non può essere vuoto.");
      ris=false;
      //return false;
    }
    if ( document.Frm1.codMonoSoggetti.value == "" ){
      alert ("Il campo 'Soggetti' non può essere vuoto.");
      ris=false;
    }
    if ( (trim(document.Frm1.DATAUTORIZZAZIONE.value) == "") && (trim(document.Frm1.STRAUTORIZZAZIONE.value) != "") ){
      alert ("Il campo 'Data autorizzazione' non può essere vuoto.");
      ris=false;
    }
    else
      if ( (trim(document.Frm1.DATAUTORIZZAZIONE.value) != "") && (trim(document.Frm1.STRAUTORIZZAZIONE.value) == "") ){
        alert ("Il campo 'Numero autorizzazione' non può essere vuoto.");
        ris=false;
      }  
      else {
        ris=true;
      }
      return ris;
  }

  function Insert(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.Frm1.MODULE.value="M_InsertVisibRich";
    doFormSubmit(document.Frm1);
  }
  function Update(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.Frm1.MODULE.value="M_SaveVisibRich";
    doFormSubmit(document.Frm1);
  }
  function Delete(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( confirm("Si desidera procedere con l'eliminazione del record corrente?") ){
      document.Frm1.MODULE.value="M_DeleteVisibRich";
      doFormSubmit(document.Frm1);
    }
  }
  function fieldChanged() {
    flagChanged = true;
  }
-->
</script>
</head>
<body class="gestione">
  <%
    infCorrentiAzienda.show(out);
    linguette.show(out);
  %>
    <p class="titolo"><b>Gestione visibilit&agrave; richiesta</b></p>
<font color="green">
  <af:showMessages prefix="M_InsertVisibRich"/>
  <af:showMessages prefix="M_SaveVisibRich"/>
    <af:showMessages prefix="M_DeleteVisibRich"/>
</font>
<font color="red">
  <af:showErrors/>
</font>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
      
      <input type="hidden" name="PAGE" value="VisibilitaRichiestePage">
      <input type="hidden" name="MODULE" value="">
      <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
     
      <table width="100%" class="main">
        <tr>
          <td class="etichetta">Tipo di visibilit&agrave;</td>
          <td class="campo">
              <af:comboBox 
                name="codMonoTipo"
                classNameBase="input"
                required="true"
                disabled="<%= String.valueOf(!canModify) %>"
                onChange="fieldChanged()">
                <option value=""  <% if ( "".equalsIgnoreCase(strTipo) )  { %>SELECTED="true"<% } %> ></option>
                <option value="P" <% if ( "P".equalsIgnoreCase(strTipo) ) { %>SELECTED="true"<% } %> >Anonima</option>
                <option value="A" <% if ( "A".equalsIgnoreCase(strTipo) ) { %>SELECTED="true"<% } %> >Palese</option>
              </af:comboBox>                           
          </td>
        </tr>
        <tr>
          <td class="etichetta">Soggetti</td>
          <td class="campo">
              <af:comboBox 
                name="codMonoSoggetti"
                classNameBase="input"
                disabled="<%= String.valueOf(!canModify) %>"
                required="true"
                onChange="fieldChanged()">
                <option value=""  <% if ( "".equalsIgnoreCase(strSoggetti) )  { %>SELECTED="true"<% } %> ></option>
                <option value="P" <% if ( "P".equalsIgnoreCase(strSoggetti) ) { %>SELECTED="true"<% } %> >Enti Pubblici</option>
                <option value="C" <% if ( "C".equalsIgnoreCase(strSoggetti) ) { %>SELECTED="true"<% } %> >Enti pubblici e Soggetti accreditati/convenzionati</option>
                <option value="A" <% if ( "A".equalsIgnoreCase(strSoggetti) ) { %>SELECTED="true"<% } %> >Enti pubblici, Soggetti accreditati e Soggetti Autorizzati</option>
              </af:comboBox>              
          </td>
        </tr>
        <tr>
          <td class="etichetta">Data autorizzazione</td>
          <td class="campo">
            <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Data autorizzazione" type="date" name="DATAUTORIZZAZIONE" value="<%= dataAutorizzazione %>" size="11" maxlength="10" validateOnPost="true" readonly="<%= String.valueOf(!canModify) %>"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Numero autorizzazione</td>
          <td class="campo">
            <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Numero autorizzazione" name="STRAUTORIZZAZIONE" value="<%= strAutorizzazione %>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>"/>
          </td>
        </tr>
        <tr><td><br/></td></tr>
        <tr>
          <td colspan="2">
            <% if ( nuovo ) {
                  if ( canInsert ) {%>
                    <input type="button" class="pulsanti" name="salva" value="Inserisci" onclick="javascript:if(controllaCampi()){Insert()};">              
            <%    } 
               } else {
                    if ( canModify ){%>
                          <input type="button" class="pulsanti" name="aggiorna" value="Aggiorna" onclick="javascript:if(controllaCampi()){Update()};">                    
                     <%}
                }%>
            <% if ( !nuovo ) {
                  if ( canDelete ) {%>
                      <input type="button" class="pulsanti" name="cancella" value="Cancella il record" onclick="javascript:Delete();">
            <%    } 
               } else {
                    if ( canModify ){%>
                      <input type="reset" class="pulsanti" name="annulla" value="Annulla">
                  <%}
                 }%>
          </td>
        </tr>
      </table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
<p align="center">
  <%operatoreInfo.showHTML(out);%>
</p>
</html>
