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
  String cdnLavoratore="";
  String _page ="";
  int _funzione;
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  boolean nuovo=true;
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  
  cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  _page = (String) serviceRequest.getAttribute("PAGE"); 
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));
  
  SourceBean lavInfo = (SourceBean) serviceResponse.getAttribute("M_VISIBCURRICULUM.ROWS.ROW");
  if ( lavInfo!=null) {
      nuovo=false;
      if (lavInfo.containsAttribute("cdnUtins")) {
        cdnUtins=lavInfo.getAttribute("cdnUtins").toString();
      }
      if (lavInfo.containsAttribute("dtmins")) {
        dtmins=lavInfo.getAttribute("dtmins").toString();
      }
      if (lavInfo.containsAttribute("cdnUtmod")) {
        cdnUtmod=lavInfo.getAttribute("cdnUtmod").toString();
      }
      if (lavInfo.containsAttribute("dtmmod")) {
        dtmmod=lavInfo.getAttribute("dtmmod").toString();
      }
      if (lavInfo.containsAttribute("codMonoSoggetti")) {
        strSoggetti=lavInfo.getAttribute("codMonoSoggetti").toString();
      }
      if (lavInfo.containsAttribute("codMonoTipo")) {
        strTipo=lavInfo.getAttribute("codMonoTipo").toString();
      }
      if (lavInfo.containsAttribute("strAutorizzazione")) {
        strAutorizzazione=lavInfo.getAttribute("strAutorizzazione").toString();
      }
      if (lavInfo.containsAttribute("datAutorizzazione")) {
        dataAutorizzazione=lavInfo.getAttribute("datAutorizzazione").toString();
      }
  }  
  infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("cancella");
  boolean canModify = attributi.containsButton("salva");

  
  //Testata testata = new Testata(null, null, null, null);                     

/*
  Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
*/  
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <title>Gestione visibilit&agrave; curriculum</title>
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

  var flagChanged = false;

  function controllaCampi(){
    var ris=true;
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
      return ris;
  }

  function Insert(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.Frm1.MODULE.value="M_InsertVisib";
    doFormSubmit(document.Frm1);
  }
  function Update(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.Frm1.MODULE.value="M_SaveVisib";
    doFormSubmit(document.Frm1);
  }
  function Delete(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( confirm("Si desidera procedere con l'eliminazione del record corrente?") ){
      document.Frm1.MODULE.value="M_DeleteVisib";
      doFormSubmit(document.Frm1);
    }
  }
  function fieldChanged() {
    flagChanged = true;
  }
       window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);

</script>
</head>
<body class="gestione">
  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
    <p class="titolo"><b>Gestione visibilit&agrave; curriculum</b></p>
<font color="green">
  <af:showMessages prefix="M_InsertVisib"/>
  <af:showMessages prefix="M_SaveVisib"/>
</font>
<font color="red">
  <af:showErrors/>
</font>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
      
      <input type="hidden" name="PAGE" value="PreselVisibilitaPage">
      <input type="hidden" name="MODULE" value="">
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
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
