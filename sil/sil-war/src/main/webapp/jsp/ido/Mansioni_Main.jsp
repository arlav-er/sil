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


  String prgRichiestaAz = serviceRequest.getAttribute("prgRichiestaAz").toString();
  //String richPubblicataFlag = serviceResponse.containsAttribute("M_GetTestataRichiesta.ROWS.ROW.flgPubblicata") ? serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.flgPubblicata").toString() : "";
  //boolean richPubblicata = richPubblicataFlag.equals("S") ? true : false;
  
  boolean nuovaMansione= true;
  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;

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
         codQualifica="",
         descMansioneList="",
         desTipoMansioneList="",
         flgEsperienzaList="";

  String conf_ClicLav = serviceResponse.containsAttribute("M_GetConfigClicLav.ROWS.ROW.NUM")?
		  serviceResponse.getAttribute("M_GetConfigClicLav.ROWS.ROW.NUM").toString():"0";
	
  Vector vectMansioni=serviceResponse.getAttributeAsVector("M_LISTIDOMANSIONI.ROWS.ROW");
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();
  Object prgAzienda= serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.prgAzienda");  
  Object prgUnita = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.prgUnita");
  String strPrgUnitaMenu = "";
  String flgInvioCL = "";
  if (prgUnita != null) {
    strPrgUnitaMenu = prgUnita.toString();
  }
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  
  // NOTE: Attributi della pagina (pulsanti e link) 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda( new BigDecimal(prgAzienda.toString()));
  filter.setPrgUnita( new BigDecimal(prgUnita.toString()));
  
	boolean canView=filter.canViewUnitaAzienda();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    PageAttribs attributi = new PageAttribs(user, _page);
    if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
      	canInsert= attributi.containsButton("INSERISCI");      
      	canModify= attributi.containsButton("AGGIORNA");
      	canDelete= attributi.containsButton("CANCELLA");
    }

  String codEvasione = "";
  SourceBean rowEvas = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
  if( rowEvas != null ){ 
  	codEvasione = StringUtils.getAttributeStrNotNull(rowEvas,"CODEVASIONE");
  	if(codEvasione.equals("AS")){  //Asta (art.16 L.56/87)
  		canModify = false;
  		canInsert = false;
  		canDelete = false;
  	}
  }


  if ( !canModify && !canDelete && !canInsert) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
      	canInsert = false;
        canModify = false;
        canDelete = false;
      }
    }
    
  PageAttribs attributiProfilo = new PageAttribs(user, "IdoEtaEsperienzaPage");
  boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda,prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
  
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  boolean nuovo = true;
  Testata operatoreInfo=null;  
  
  if(serviceResponse.containsAttribute("M_GetIdoMansioni")) { 
  //Sono in modalità dettaglio
    nuovo = false; 
  }
  else { 
// Sono in modalità inserimento
    nuovo = true;
  }

  if (! nuovo) {
    SourceBean info = (SourceBean)serviceResponse.getAttribute("M_GetIdoMansioni.ROWS.ROW");
    if (info != null) {     
      prgMansione       = info.getAttribute("PRGMANSIONE");
      codMansione       = info.getAttribute("CODMANSIONE");
      descMansione      = StringUtils.getAttributeStrNotNull(info, "DESMANSIONE");
      desTipoMansione   = StringUtils.getAttributeStrNotNull(info, "DESTIPOMANSIONE");
      flgEsperienza     = StringUtils.getAttributeStrNotNull(info, "FLGESPERIENZA");
      flgPubblica       = StringUtils.getAttributeStrNotNull(info, "FLGPUBBLICA");
      codQualifica      = StringUtils.getAttributeStrNotNull(info, "CODQUALIFICA");
      cdnUtIns          = info.getAttribute("CDNUTINS");
      dtmIns            = info.getAttribute("DTMINS");
      cdnUtMod          = info.getAttribute("CDNUTMOD");
      dtmMod            = info.getAttribute("DTMMOD");
      flgInvioCL     	= StringUtils.getAttributeStrNotNull(info, "flgInvioCL");
      operatoreInfo     = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    }
  }
 
    if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
	
  String htmlStreamTop = StyleUtils.roundTopTable(canManage);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canManage);
 
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");

  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }

  String url_nuovo = "AdapterHTTP?PAGE=IdoMansioniMainPage" + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz;
%>
<%
Object strPrgAziendaMenu=prgAzienda;
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Mansioni - IncrocioDomandaOfferta</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <script language="JavaScript" src="../../js/layers.js"></script> 
  <SCRIPT TYPE="text/javascript">

  // Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function MansioneSelect(prgMansione) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    flgFrequente=document.Frm1.flgFrequente;
    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";

    var s= "AdapterHTTP?PAGE=IdoMansioniMainPage";
    s += "&PRGMANSIONE=" + prgMansione;
    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz %>";
    s +="&PRGAZIENDA=<%=prgAzienda%>";
    s +="&PRGUNITA=<%=strPrgUnitaMenu%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&SELDETTAGLIO=1";  
    s += "&APRIDIV=1";
    s += paramFrequente;

    setWindowLocation(s);
  }

  function MansioneDelete(prgMansione, descrizione) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

     <!--var s="Eliminare la mansione\n" + descrizione.replace(/\^/g, '\'') + " ?";-->
     var s="Eliminare la mansione\n" + descrizione + " ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=IdoMansioniMainPage";
      s += "&PRGMANSIONE=" + prgMansione;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz %>";
      s+="&PRGAZIENDA=<%=prgAzienda%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&CANCELLA=yes";

      setWindowLocation(s);
    }
  }


  // NOTE: fieldChanged è presente senza fare nulla.
  // Non fa nulla perchè in questa pagina
  // non è supportata la notifica delle modifiche,
  // presente perchè usata in ConoInfo_CommonScripts
  function fieldChanged() {
  }
 function getFormObj() {return document.Frm1;}

 function settaFlag() {
 	if(document.Frm1.INVIA_CLIC_LAVORO.checked) {
		document.Frm1.FLGINVIOCL.value="S";
	} else {
		document.Frm1.FLGINVIOCL.value="N";
	}
  }
     
  </SCRIPT>



<script language="Javascript">
       window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=strPrgUnitaMenu%>);
 </script>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
 attributi.showHyperLinks(out, requestContainer, responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);  
      %>
</script>
</head>

<%@ include file="Mansioni_CommonScripts.inc" %>
<%@ include file="ControllaMansione.inc" %>


<body class="gestione" onload="rinfresca();toggleVisQualifica();">
<% 
  infCorrentiAzienda.show(out);
  linguettaAlternativa.show(out); 
%>
<BR/>
<%
linguette.show(out);
%>
  <!--input type="hidden" name="PAGE" value="IdoMansioniMainPage">
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
  <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
  <input type="hidden" name="PRGAzienda" value="<%= prgAzienda %>"/>
  <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
  <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/-->

  <center>
    <af:showMessages prefix="M_InsertIDOMansione"/>
    <af:showMessages prefix="M_SaveIdoMansioni"/>
    <af:showMessages prefix="M_DelIdoMansioni"/>
    <af:showMessages prefix="M_UpdateMansione"/>
    <af:showErrors />    
  </center>

  <af:list moduleName="M_ListIdoMansioni" skipNavigationButton="1"
           canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
           jsSelect="MansioneSelect" jsDelete="MansioneDelete"
           configProviderClass="it.eng.sil.module.ido.IdoMansioniListConfig" />

  <%
  if(canInsert) {
  %>
    <p align="center">
      <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova mansione"/>   
    </p>
  <%}
  String divStreamTop = StyleUtils.roundLayerTop(canManage);
  String divStreamBottom = StyleUtils.roundLayerBottom(canManage);
  %>
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett" style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
         <%out.print(divStreamTop);%>

        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuova mansione
            <%} else {%>
              Mansione
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

    <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaMansione(Frm1.CODMANSIONE.value)">

    <input type="hidden" name="PAGE" value="IdoMansioniMainPage">
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="PRGMANSIONE" value="<%= prgMansione %>"/>
    <input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>" />
    <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
     
    <% if (!nuovo) { %>   
        <table align="center">
        <%@ include file="Mansioni_Elemento.inc" %>
        </table>
    <%} else {%>
        <table align="center">
        <%@ include file="Mansioni_Elemento.inc" %>
        </table>
    <%}%>
	<br>
    <table class="main" width="100%">
      <tr><td>
      <center>
      <%if(nuovo) {%> 
        <%if(canInsert) {%>        
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci">
        <%} %>
        <input 
          class="pulsante" 
          type="button" 
          name="annulla" 
          value="<%= canInsert ? "Chiudi senza inserire" : "Chiudi" %>" 
          onClick="ChiudiDivLayer('divLayerDett')">
      <%} else { %>
        <%if(canModify) {%>        
          <input class="pulsante" type="submit" name="salva" value="Aggiorna">
        <%} %>
        <input 
          class="pulsante" 
          type="button" 
          name="annulla" 
          value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
          onClick="ChiudiDivLayer('divLayerDett')">
      <%} %>
      </center>
      </td></tr>
      <% if (!nuovo) { %>
          <tr>
              <td colspan="4" align="center">   
             <%if (!nuovo) {%>                          
               <p align="center">
                 <% operatoreInfo.showHTML(out); %>
               </p>
             <%}%>
            </td>
          </tr>
      <% } %>
    </table>
  </af:form>
  </div>
 <%out.print(divStreamBottom);%>        
</body>
</html>

<% } %>