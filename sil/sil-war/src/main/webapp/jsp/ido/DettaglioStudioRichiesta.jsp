<!-- @author: Cristian Mudadu -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" 
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String prgAzienda="";
  String prgUnita="";
  prgAzienda =  (String)serviceRequest.getAttribute("PRGAZIENDA");
  prgUnita = (String)serviceRequest.getAttribute("PRGUNITA");

  Object prgAlternativa = (Object) sessionContainer.getAttribute("prgAlternativa");
  String _page = "IdoStudiPage";
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("AGGIORNA");
  Testata operatoreInfo=null;

  String prgRichiestaAz = null;
  String codTitolo = null;
  String tipoTitolo = null;
  String titolo = null;
  String specifica = null;
  String conseguito = null;
  
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  //String moduleName="MAggiornaLinguaRichiesta";
  String btnSalva="Salva";
  String btnAnnulla= canModify ? "Chiudi senza salvare" : "Chiudi";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GetDettaglioStudioRichiesta");  
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  Object prgStudio = row.getAttribute("PRGSTUDIO");

  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : ""; 
  
  
  codTitolo =row.containsAttribute("codTitolo") ? row.getAttribute("codTitolo").toString() : "";
  tipoTitolo=row.containsAttribute("DESCRIZIONE") ? row.getAttribute("DESCRIZIONE").toString() : "";
  titolo    =row.containsAttribute("DESCRIZIONE_P") ? row.getAttribute("DESCRIZIONE_P").toString() : "";
  specifica =row.containsAttribute("SPECIFICA") ? row.getAttribute("SPECIFICA").toString() : "";
  conseguito=row.containsAttribute("CONSEGUITO") ? row.getAttribute("CONSEGUITO").toString() : "";

  prgRichiestaAz = (String) serviceRequest.getAttribute("PRGRICHIESTAAZ");

  objFlgIndispensabile=row.getAttribute("INDISPENSABILE");
  if (objFlgIndispensabile != null) {
    strFlgIndispensabile = objFlgIndispensabile.toString();
  }

  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canModify));

  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  
  if (prgStudio != null) {
    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Studio</title>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  </script>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
  <script language="Javascript">
  <!--
    function clearTitolo() {
    if (Frm1.codTitolo.value=="") {   
        Frm1.codTitoloHid.value=""; 
        Frm1.strTitolo.value=""; 
        Frm1.strTipoTitolo.value=""; 
      }
  }

  function ricercaAvanzataTitoliStudio() {
    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
  }

  function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {	

    if (codTitolo.value==""){
      strTitolo.value="";
      strTipoTitolo.value="";
      
    }
    else if (codTitolo.value!=codTitoloHid.value){
      window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
    }
  }

  //lasciata per compatibilita'
  function toggleVisStato(codMonoStato) {  
  }

  function controllaTitoloStudio(codTitolo) {
    var strCodTitolo = new String(codTitolo);
    if (strCodTitolo.substring(strCodTitolo.length-2,strCodTitolo.length) != '00') {
      return true;
    }
    else {
      if (confirm('Non è stato indicato un titolo di studio specifico, continuare ?')) {
        return true;
      }
      else {
        return false;
      }
    }
  }
  -->
  </script>
</head>
<body class="gestione">
<% if(infCorrentiAzienda != null) infCorrentiAzienda.show(out); %>

 <% linguettaAlternativa.show(out); %>
<br/>
<%
  linguette.show(out);
%>
<br>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaTitoloStudio(Frm1.codTitolo.value)">
    <input type="hidden" name="PAGE" value="<%= _page %>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
    <input type="hidden" name="PRGSTUDIO" value="<%= prgStudio %>"/>    
    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>
    <center>
      <font color="green">
        <af:showMessages prefix="M_InsertStudioRichiesta"/>
        <af:showMessages prefix="M_DeleteStudioRichiesta"/>
      </font>
    </center>
    <br/>
    <table align="center" border="0" width="100%">
      <tr><td colspan="2" ><center><b>Dettaglio Studio</b></center></td></tr>
      <tr><td><br/></td></tr>
      <tr>
        <td class="etichetta" nowrap>Profilo n. &nbsp;</td>
        <td class="campo"><af:textBox classNameBase="input" type="number" name="prgAlternativa" readonly="true" value="<%=prgAlternativa.toString()%>" size="2" />
        </td>
      </tr>
      <tr valign="top">
        <td class="etichetta">Codice &nbsp;</td>
        <td class="campo" colspan="3">
            <af:textBox classNameBase="input" readonly="<%= String.valueOf(!canModify) %>" title="Codice del titolo" name="codTitolo" value="<%= codTitolo %>" size="10" maxlength="6" onBlur="clearTitolo();" required="true" />&nbsp;                    

            <af:textBox type="hidden" name="codTitoloHid" value="<%= codTitolo %>"/>
            <% if (canModify) {
            %>
              <A href="javascript:selectTitolo_onClick(Frm1.codTitolo, Frm1.codTitoloHid, Frm1.strTitolo,  Frm1.strTipoTitolo);">
                <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
              <A href="javascript:ricercaAvanzataTitoliStudio();">
                Ricerca avanzata
              </A>
           <%
           }
           %>
        </td>
      </tr>
      <tr valign="top">
        <td class="etichetta">Tipo &nbsp;</td>
        <td class="campo" colspan="3">
          <af:textBox type="hidden" name="flgLaurea" />
          <af:textArea cols="50" rows="3" classNameBase="textarea" title="Tipo del titolo" name="strTipoTitolo" value="<%= tipoTitolo%>" readonly="true"  /> 
        </td>
      </tr>
      <tr>
        <td class="etichetta">Titolo &nbsp;</td>
        <td class="campo" colspan="3">
          <af:textArea cols="50" rows="4"  classNameBase="textarea" name="strTitolo" value="<%= titolo %>" readonly="true" required="true" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Specifica &nbsp;</td>
        <td class="campo" colspan="3">
          <af:textBox size="50" classNameBase="input" name="specifica" value="<%=specifica%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="200"/>&nbsp;</td>
      </tr>
      <tr>
        <td class="etichetta">Conseguito &nbsp;</td>
        <td colspan="3" class="campo">
          <af:comboBox name="conseguito"
                     title="conseguito" required="false"
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canModify) %>"
                     selectedValue="<%= conseguito %>">
          <option value=""  <% if ( "".equals(conseguito) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(conseguito) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(conseguito) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox> 
        </td>
      </tr>
      <tr>
      <td class="etichetta">Indispensabile &nbsp;</td>
        <td class="campo">
        <af:comboBox name="FLGINDISPENSABILE"
                     title="Indispensabile" required="false"
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canModify) %>"
                     selectedValue="<%= strFlgIndispensabile %>">
          <option value=""  <% if ( "".equals(strFlgIndispensabile) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>  
        </td>
      </tr>
    </table>
    <br/>
    <table align="center">
      <tr align="center">
        <%
        if (canModify) {
        %>
          <td align="center">
            <input type="submit" class="pulsanti" name="SALVA" value="Aggiorna">
          </td>
        <%
        }
        %>
        <td align="center">
          <input type="submit" class="pulsanti" name="annulla" value="<%=btnAnnulla%>" onClick="javascript:history.back();">
        </td>
      </tr>
    </table>    
  </af:form>
  <%if (prgStudio != null) operatoreInfo.showHTML(out);%>
</body>
</html>