<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("salva");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}
      
    }
%>

<%
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector vectTipi        = serviceResponse.getAttributeAsVector("M_LISTTIPICONOSCENZAINFO.ROWS.ROW");
  Vector vectGradi       = serviceResponse.getAttributeAsVector("M_LISTGRADICONOSCENZAINFO.ROWS.ROW");
  Vector vectModi        = serviceResponse.getAttributeAsVector("M_LISTMODICONOSCENZAINFO.ROWS.ROW");
  Vector tipiDettInfoRows=serviceResponse.getAttributeAsVector("M_LISTDETTAGLIALLCONOSCENZAINFO.ROWS.ROW");
  SourceBean row_dettInfo = null;

  Object prgInfo= "",
         codTipoInfo= "", 
         cdnGrado= "", 
         cdnUtIns= "",
         dtmIns= "",
         cdnUtMod= "",
         dtmMod= "";
         
  String codDettInfo= "", 
         strDescInfo= "", 
         codModoInfo= "", 
         strModInfo= "", 
         flgCertificato= "",
         descDettInfo= "";
 

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADCONOSCENZAINFO.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgInfo        = beanLastInsert.getAttribute("PRGINFO");
    codTipoInfo    = beanLastInsert.getAttribute("CODTIPOINFO");
    codDettInfo    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODDETTINFO");
    strDescInfo    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCINFO");
    cdnGrado       = beanLastInsert.getAttribute("CDNGRADO");
    codModoInfo    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMODOINFO");
    strModInfo     = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRMODINFO");
    flgCertificato = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGCERTIFICATO");
    descDettInfo   = StringUtils.getAttributeStrNotNull(beanLastInsert, "DESCDETTINFO");
    cdnUtIns       = beanLastInsert.getAttribute("CDNUTINS");
    dtmIns         = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod       = beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod         = beanLastInsert.getAttribute("DTMMOD");
  }
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  /*PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");
  */

  /* DEBUG: Commentare
  it.eng.sil.util.Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  out.println("Tipi len [" + vectTipi.size() + "]");
  it.eng.sil.util.Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
  it.eng.sil.util.Utils.dumpObject("codTipoInfo", codTipoInfo, out);
  it.eng.sil.util.Utils.dumpObject("codDettInfo", codDettInfo, out);
  it.eng.sil.util.Utils.dumpObject("strDescInfo", strDescInfo, out);
  it.eng.sil.util.Utils.dumpObject("cdnGrado", cdnGrado, out);
  it.eng.sil.util.Utils.dumpObject("codModoInfo", codModoInfo, out);
  it.eng.sil.util.Utils.dumpObject("strModInfo", strModInfo, out);

  it.eng.sil.util.Utils.dumpObject("cdnUtIns", cdnUtIns, out);
  it.eng.sil.util.Utils.dumpObject("dtmIns", dtmIns, out);
  it.eng.sil.util.Utils.dumpObject("cdnUtMod nel record", cdnUtMod, out);
  it.eng.sil.util.Utils.dumpObject("dtmMod nel record", dtmMod, out);

  it.eng.sil.util.Utils.dumpObject("testata", testata, out);

  it.eng.sil.util.Utils.dumpObject("pageAttribs", attributi, out);
  */
%>

<html>

<head>
  <title>Conoscenza Informatica</title>

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

  function caricaDettInfo(codiceTipoInfo,codDettTipo,strProvenienza) {
    var dett_tipo=new Array();
    var dett_cod=new Array();
    var dett_des=new Array();
    var indiceDett=0;
<%  for(int i=0; i<tipiDettInfoRows.size(); i++)  { 
      row_dettInfo = (SourceBean) tipiDettInfoRows.elementAt(i);
      out.print("dett_tipo["+i+"]=\""+ row_dettInfo.getAttribute("CODICETIPO").toString()+"\";\n");
      out.print("dett_cod["+i+"]=\""+ row_dettInfo.getAttribute("CODICE").toString()+"\";\n");
      out.print("dett_des["+i+"]=\""+ row_dettInfo.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>
     i=0;
     j=0;
     maxcombo=15;
     while (document.MainForm.CODICE.options.length>0) {
          document.MainForm.CODICE.options[0]=null;
      }

    for (i=0; i<dett_tipo.length ;i++) {
      if (dett_tipo[i]==codiceTipoInfo) {
        if (dett_cod[i] == codDettTipo) {
          indiceDett=j;
        }
        document.MainForm.CODICE.options[j]=new Option(dett_des[i], dett_cod[i], false, false);
        j++;
      }
    } 

    if (strProvenienza != 'nuovo') {
      document.MainForm.CODICE.options[j]=new Option('', '', false, false);
      j++;
    }
    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    
    if (codDettTipo != '') {
      document.MainForm.CODICE.selectedIndex=indiceDett;
    }
    else {
      document.MainForm.CODICE.selectedIndex=-1;
    }
    
    if (strProvenienza == 'nuovo') {
      document.MainForm.CODICE.size=j;
    }
  }

  function ConoscenzaSelect (prgInfo, prgAzienda, prgRichiestaAz, funzione) {
    var s= "AdapterHTTP?PAGE=IdoDettaglioInfoPage";
    s += "&PRGINFO=" + prgInfo;
    s += "&PRGAZIENDA=" + prgAzienda;
    s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
    s += "&CDNFUNZIONE=" + funzione;
    setWindowLocation(s);
  }
  </SCRIPT>

  <%@ include file="ConoInfo_CommonScripts.inc" %>

</head>

<body class="gestione" onload="rinfresca();caricaDettInfo('<%=codTipoInfo%>','<%=codDettInfo%>','');">

    
  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>

  <af:form action="AdapterHTTP" method="POST" name="MainForm">

    <input type="hidden" name="PAGE" value="ConoscenzeInfoPage">
    <input type="hidden" name="MODULE" value="M_UpdateConoscenzaInfo"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CODDETTINFO" value="<%= codDettInfo %>"/>
    <input type="hidden" name="PRGINFO" value="<%= prgInfo %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <table class="main">
      <tr>
        <td/>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_UpdateConoscenzaInfo"/>
            </font>
          </center>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Tipo</td>
        <td class="campo">    
          <af:comboBox name="CODTIPOINFO" size="1" title="Tipo Conoscenza"
               multiple="false" required="true"
               focusOn="false" moduleName="M_ListTipiConoscenzaInfo"
               addBlank="true" blankValue=""
               classNameBase="input"
               onChange="javascript:caricaDettInfo(MainForm.CODTIPOINFO.value,'','nuovo');"
               selectedValue="<%= codTipoInfo.toString() %>"
               disabled="<%= String.valueOf( !canModify ) %>" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Dettaglio</td>
        <td class="campo">
          <af:comboBox title="Dettaglio"
                       name="CODICE"
                       required="false"
                       moduleName="M_LISTDETTAGLIALLCONOSCENZAINFO"
                       classNameBase="input"
                       selectedValue="<%=codDettInfo%>"
                       disabled="<%= String.valueOf( !canModify ) %>"/>&nbsp;
      </tr>
      <%@ include file="ConoInfo_Elemento.inc" %>
    </table>
    <br/>
      <center>
      <% if ( canModify ) { %>
          <!-- NOTE: Mostra il btn "Aggiorna" solo se canModify
          -->
        <input class="pulsante" type="button" name="salva" value="Aggiorna" onclick="Salva_onClick();">
      <% } %>
        <!-- NOTE: Rinomina il btn in "Chiudi" se not canModify
        -->
        <input 
          class="pulsante" 
          type="button" 
          name="annulla" 
          value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>"
          onclick="GoToMainPage()">
      </center>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
