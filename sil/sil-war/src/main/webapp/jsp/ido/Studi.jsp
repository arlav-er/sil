<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

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
  String flgIndispensabile="";
  Object prgAzienda=null;
  Object prgUnita=null;
  Testata testata = null;

  String codTitolo = null;
  String tipoTitolo = null;
  String titolo = null;
  String strSpecifica = null;
  String flgConseguito = null;
  Object objFlgIndispensabile = null;
  Object prgStudio = null;
  String cdnUtIns="";
  String dtmIns="";
  String cdnUtMod="";
  String dtmMod="";
  
  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;

  SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW");

   if (contTestata!=null) {
    prgAzienda =  contTestata.getAttribute("PRGAZIENDA");
    prgUnita   =  contTestata.getAttribute("PRGUNITA");
  }

  String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");

  Vector vectListaStudi = serviceResponse.getAttributeAsVector("M_GetStudiRichiesta.ROWS.ROW");
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);

  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

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

  if ( !canModify && !canDelete ) {
      
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
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_GetDettaglioStudioRichiesta")) { 
     nuovo = false; 
  }
  else { 
    nuovo = true;
  }
  
  if(!nuovo) {
    SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GetDettaglioStudioRichiesta.ROWS.ROW");
    if (row != null) {     
      prgStudio = row.getAttribute("PRGSTUDIO");
      codTitolo =row.containsAttribute("codTitolo") ? row.getAttribute("codTitolo").toString() : "";
      tipoTitolo=row.containsAttribute("DESCRIZIONE_P") ? row.getAttribute("DESCRIZIONE_P").toString() : "";
      titolo=row.containsAttribute("DESCRIZIONE") ? row.getAttribute("DESCRIZIONE").toString() : "";
      strSpecifica =row.containsAttribute("SPECIFICA") ? row.getAttribute("SPECIFICA").toString() : "";
      flgConseguito=row.containsAttribute("CONSEGUITO") ? row.getAttribute("CONSEGUITO").toString() : "";
      objFlgIndispensabile=row.getAttribute("INDISPENSABILE");
      if (objFlgIndispensabile != null) {
        flgIndispensabile = objFlgIndispensabile.toString();
      }
      cdnUtIns   = (String) row.getAttribute("CDNUTINS").toString();
      dtmIns     = (String) row.getAttribute("DTMINS");
      cdnUtMod   = (String) row.getAttribute("CDNUTMOD").toString();
      dtmMod     = (String) row.getAttribute("DTMMOD");
      testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    }
  } 
  
    if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	} 
	
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }

  String url_nuovo = "AdapterHTTP?PAGE=IdoStudiPage" + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz ;
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Studi</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <script language="JavaScript" src="../../js/layers.js"></script> 

  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<!--
  function fieldChanged() {
    <%= canModify? "flagChanged = true;":"" %>
  }

  function StudiDelete(prgStudio, prgRichiestaAz, specifica) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere il titolo di studi:\n \'";
    s += specifica + "\' ?";

    if ( confirm(s) ) {
      var s= "AdapterHTTP?PAGE=IdoStudiPage";
      s += "&CANCELLA=Y";
      s += "&prgStudio=" + prgStudio;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + <%= _funzione%>;
      setWindowLocation(s);
    }


    
  }

  function StudioDetail(prgStudio, prgRichiestaAz) {    
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=IdoStudiPage";
    s += "&PRGSTUDIO=" + prgStudio;
    s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
    s += "&CDNFUNZIONE=" + <%= _funzione%>;
    s += "&SELDETTAGLIO=TRUE";   
    s += "&APRIDIV=1";
    setWindowLocation(s);
  }
  
  function clearTitolo() {
    if (Frm1.codTitolo.value=="") {   
        Frm1.codTitoloHid.value=""; 
        Frm1.strTitolo.value=""; 
        Frm1.strTipoTitolo.value=""; 
      }
  }

  function ricercaAvanzataTitoliStudio() {
    var w=800; var l=((screen.availWidth)-w)/2;
  	var h=500; var t=((screen.availHeight)-h)/2;
	//var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
 	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", feat);
    //window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
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
  </SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
</script>
</head>

<body class="gestione" onload="rinfresca()">
<% if(infCorrentiAzienda != null) infCorrentiAzienda.show(out); %>

 <% linguettaAlternativa.show(out); %>


<BR/>
<%
linguette.show(out);
%>  
  <center>
    <af:showErrors/>
    <af:showMessages prefix="M_InsertStudioRichiesta"/>
    <af:showMessages prefix="M_DeleteStudioRichiesta"/>
    <af:showMessages prefix="M_SaveStudioRichiesta"/>
  </center>
    

  <af:list moduleName="M_GetStudiRichiesta" skipNavigationButton="1"
           canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
           jsSelect="StudioDetail" jsDelete="StudiDelete"/>

  <%
  if(canInsert) {
  %>
    <p align="center">
      <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Studio"/>   
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
              Nuovo studio
            <%} else {%>
              Studio
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

<p align="center">
<table class="main">
<tr>
  <td valign="top">
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaTitoloStudio(Frm1.codTitolo.value)">
    <input type="hidden" name="PAGE" value="<%= _page %>"/>
  <% if(!nuovo) { %>
    <input type="hidden" name="PRGSTUDIO" value="<%= prgStudio %>"/>    
  <% } %>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda.toString()%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita.toString()%>"/>
      <table align="center">
      <%@ include file="DettaglioStudioRichiesta.inc" %>
      </table>
      <br>
      <table class="main" width="100%">
      <%if(nuovo && canInsert) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="submit" class="pulsanti" name="inserisci" value="Inserisci">
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza inserire" onClick="ChiudiDivLayer('divLayerDett')">
          </td>
        </tr>
       <%}
       else {%>
         <tr>
          <td colspan="2" align="center">
          <%if(canModify) {%>        
            <input type="submit" class="pulsanti" name="salva" value="Aggiorna">
          <% } %>
          <input type="button" class="pulsanti" name="chiudi" value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" onClick="ChiudiDivLayer('divLayerDett')">
          </td>
        </tr>
        <tr>
          <td colspan="4" align="center">   
            <p align="center">
              <% testata.showHTML(out); %>
            </p>
          </td>
        </tr>
    <%}%>
      </table>      
  </af:form>
  </td>
</tr>
</table>
 <%out.print(divStreamBottom);%>        
</body>
</html>

<% } %>