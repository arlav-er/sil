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

  String codCorso = null;
  String tipoTitolo = null;
  String titolo = null;
  String strSpecifica = null;
  String flgConseguito = null;
  Object objFlgIndispensabile = null;
  Object prgFormProf = null;
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

 // Vector vectListaStudi = serviceResponse.getAttributeAsVector("M_GetStudiRichiesta.ROWS.ROW");

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
      canInsert= attributi.containsButton("INS_FORM_PROF");      
      canModify= attributi.containsButton("AGG_FORM_PROF");
      canDelete= attributi.containsButton("CAN_FORM_PROF");
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
    
  PageAttribs attributiProfilo = new PageAttribs(user, "IdoCorsiPage");
  boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_GetDettaglioFormProf")) { 
     nuovo = false; 
  }
  else { 
    nuovo = true;
  }
   
  if(!nuovo) {
    SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GetDettaglioFormProf.ROWS.ROW");
    if (row != null) {     
      prgFormProf = row.getAttribute("PRGFORMPROF");
      codCorso =row.containsAttribute("codCorso") ? row.getAttribute("codCorso").toString() : "";
      titolo=row.containsAttribute("DESCRIZIONE") ? row.getAttribute("DESCRIZIONE").toString() : "";
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

  String url_nuovo = "AdapterHTTP?PAGE=IdoCorsiPage" + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz ;
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Formazione Professionale</title>

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

  function CorsiDelete(prgFormProf, prgRichiestaAz, specifica) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere la formazione professionale :\n \'";
    s += specifica + "\' ?";

    if ( confirm(s) ) {
      var s= "AdapterHTTP?PAGE=IdoCorsiPage";
      s += "&CANCELLA=Y";
      s += "&PRGFORMPROF=" + prgFormProf;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + <%= _funzione%>;
      setWindowLocation(s);
    }


    
  }

  function CorsiDetail(prgFormProf, prgRichiestaAz) {    
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=IdoCorsiPage";
    s += "&PRGFORMPROF=" + prgFormProf;
    s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
    s += "&CDNFUNZIONE=" + <%= _funzione%>;
    s += "&SELDETTAGLIO=TRUE";   
    s += "&APRIDIV=1";
    setWindowLocation(s);
  }
  
  function clearTitolo() {
    if (Frm1.codCorso.value=="") {   
        Frm1.codCorsoHid.value=""; 
        Frm1.strTitolo.value=""; 
      }
  }

  function ricercaAvanzataCorsi() {
    var w=800; var l=((screen.availWidth)-w)/2;
  	var h=500; var t=((screen.availHeight)-h)/2;
	//var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
 	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	window.open("AdapterHTTP?PAGE=RicercaCorsiAvanzataPage", "Titoli", feat);
    //window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
  }

  function selectCorsi_onClick(codCorso, codCorsoHid, strTitolo) {	

    if (codCorso.value==""){
    	strTitolo.value="";
      
    }
    else if (codCorso.value!=codCorsoHid.value){
      window.open("AdapterHTTP?PAGE=RicercaCorsiPage&codCorso="+codCorso.value, "Corsi", 'toolbar=0, scrollbars=1');
    }
  }

  //lasciata per compatibilita'
  function toggleVisStato(codMonoStato) {  
  }

  function controllaCorsi(codCorso) {
    var strcodCorso = new String(codCorso);
    if (strcodCorso.substring(strcodCorso.length-2,strcodCorso.length) != '00') {
      return true;
    }
    else {
      if (confirm('Non è stato indicato un corso, continuare ?')) {
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
    <af:showMessages prefix="M_InsertFormProf"/>
    <af:showMessages prefix="M_DeleteFormProf"/>
    <af:showMessages prefix="M_SaveFormProf"/>
  </center>
    

  <af:list moduleName="M_GetCorsiRichiesta" skipNavigationButton="1"
           canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
           jsSelect="CorsiDetail" jsDelete="CorsiDelete"/>

  <%
  if(canInsert) {
  %>
    <p align="center">
      <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Corso"/>   
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
              Nuovo Corso
            <%} else {%>
              Corso
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

<p align="center">
<table class="main">
<tr>
  <td valign="top">
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaCorsi(Frm1.codCorso.value)">
    <input type="hidden" name="PAGE" value="<%= _page %>"/>
  <% if(!nuovo) { %>
    <input type="hidden" name="PRGFORMPROF" value="<%= prgFormProf %>"/>    
  <% } %>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda.toString()%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita.toString()%>"/>
      <table align="center">
      <%@ include file="FormazioneProfessionale.inc" %>
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