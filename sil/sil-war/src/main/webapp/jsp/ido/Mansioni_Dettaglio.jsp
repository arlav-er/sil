<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String strPrgUnita = (String)serviceRequest.getAttribute("PRGUNITA");  
  String prgRichiestaAz = (String)serviceRequest.getAttribute("prgRichiestaAz");
  Object prgAzienda=serviceRequest.getAttribute("prgAzienda"); 
  boolean richPubblicata= ((String) serviceRequest.getAttribute("richPubblicata")).equals("S")?true:false;
  
  boolean nuovaMansione= false;
  
  Object prgMansione= "",
         codMansione= "",
         cdnUtIns= "",
         dtmIns= "",
         cdnUtMod= "",
         dtmMod= "";
         
  String descMansione= "",
        desTipoMansione="",
         flgEsperienza= "",
         flgPubblica="",
         codQualifica="";

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  
  String conf_ClicLav = serviceResponse.containsAttribute("M_GetConfigClicLav.ROWS.ROW.NUM")?
		  serviceResponse.getAttribute("M_GetConfigClicLav.ROWS.ROW.NUM").toString():"0";
  
  Vector vectMansione= serviceResponse.getAttributeAsVector("M_GETIDOMANSIONI.ROWS.ROW");
  if ( (vectMansione != null) && (vectMansione.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectMansione.get(0);

    prgMansione       = beanLastInsert.getAttribute("PRGMANSIONE");
    codMansione       = beanLastInsert.getAttribute("CODMANSIONE");
    descMansione      = StringUtils.getAttributeStrNotNull(beanLastInsert, "DESMANSIONE");
    desTipoMansione   = StringUtils.getAttributeStrNotNull(beanLastInsert, "DESTIPOMANSIONE");
    flgEsperienza     = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGESPERIENZA");
    flgPubblica       = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGPUBBLICA");
    codQualifica      = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODQUALIFICA");
    cdnUtIns          = beanLastInsert.getAttribute("CDNUTINS");
    dtmIns            = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod          = beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod            = beanLastInsert.getAttribute("DTMMOD");
  }

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "IdoMansioniMainPage");
  boolean canModify= attributi.containsButton("AGGIORNA");
  boolean canManage=canModify;
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, strPrgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canModify));
  Linguette linguette = new Linguette( user, _funzione, "IdoMansioniMainPage", new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,strPrgUnita,prgRichiestaAz);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>

<head>
  <title>Mansione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">

  // NOTE: Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {

    // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
    //alert("field changed !")  
    
    // NOTE: field-check solo se canModify 
    <% if ( canModify ) { %> 
      flagChanged = true;
    <% } %> 
  }


function chiudi() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     }
  }
  if (ok) {
     setWindowLocation('AdapterHTTP?PAGE=IdoMansioniMainPage&prgRichiestaAz=<%=prgRichiestaAz%>&prgAzienda=<%=prgAzienda%>&cdnFunzione=<%=_funzione%>'); 
  }
}

window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=strPrgUnita%>);

</SCRIPT>

  <%@ include file="Mansioni_CommonScripts.inc" %>
  <%@ include file="ControllaMansione.inc" %>
</head>

<body class="gestione" onload="rinfresca()">
<%
  infCorrentiAzienda.show(out); 
  linguettaAlternativa.show(out); 
%>
<br>
<%
  linguette.show(out);
%>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaMansione(Frm1.CODMANSIONE.value)">

    <input type="hidden" name="PAGE" value="IdoMansioniMainPage">
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="PRGMANSIONE" value="<%= prgMansione %>"/>
    <input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>" />
    <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />

    <p class="titolo">Mansione</p>

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">
    <%out.print(htmlStreamTop);%>
    <table class="main" >
      <tr>
        <td/>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_UpdateMansione"/>
            </font>
          </center>
        </td>
      </tr>
      <%@ include file="Mansioni_Elemento.inc" %>
    </table>
    <br/>
    <center>
      <% if ( canModify ) { %>
        <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      <% } %>
      <input 
        class="pulsante" 
        type="button" 
        name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
        onclick="chiudi()">
    </center>
    <%out.print(htmlStreamBottom);%>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
