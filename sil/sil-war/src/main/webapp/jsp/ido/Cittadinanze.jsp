<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
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
  com.engiweb.framework.security.*" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  boolean nuovo = true;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  String cdnStatoRich = "";
  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  
  SourceBean rigaTestata = null;
  SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA");
  Vector rows_VectorTestata = null;
  rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorTestata.size()!=0) {
    rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
    cdnStatoRich = rigaTestata.getAttribute("cdnStatoRich").toString();
    prgAzienda = (BigDecimal) rigaTestata.getAttribute("PRGAZIENDA");
    if (prgAzienda != null) {  
      strPrgAziendaMenu = prgAzienda.toString();
    }
    prgUnita = (BigDecimal) rigaTestata.getAttribute("PRGUNITA");
    if (prgUnita != null) {
      strPrgUnitaMenu = prgUnita.toString();
    }
  }
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda(prgAzienda);
  filter.setPrgUnita(prgUnita);
  
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

  if ( !canModify && !canDelete && !canInsert) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
      	canInsert = false;
        canModify = false;
        canDelete = false;
      }
    }

//  String htmlStreamTop = StyleUtils.roundTopTable( canManage );
//  String htmlStreamBottom = StyleUtils.roundBottomTable( canManage );
   
  SourceBean CittadinanzaRows=(SourceBean) serviceResponse.getAttribute("MListaCittadinanzaRichiesta"); 
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  SourceBean riga = null;
  String strCittadinanza="";
  String strMotivazione="";
  String codMotNazionalita = "";
  String displayMotNaz = ""; 
  String strCittadinanzaVisualizza="";
  String strMotivazioneVisualizza="";
  Object prgCittadinanza = null;
  String strRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  BigDecimal prgRichiestaAz = new BigDecimal(strRichiestaAz);
  String moduleName="MInserisciCittadinanzaRichiesta";
  String codCittadinanza="";
  String btnSalva="Inserisci";
  String btnAnnulla="";
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(strRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
 // InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu, strPrgUnitaMenu, strRichiestaAz);
  Testata operatoreInfo=null;  
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";


  if(serviceResponse.containsAttribute("SelectDettaglioCittadinanza")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true;
  }

  if(!nuovo) {
  //Sono in modalità dettaglio
    SourceBean info = (SourceBean)serviceResponse.getAttribute("SelectDettaglioCittadinanza.ROWS.ROW");
      if (info != null) {     
       codCittadinanza    = StringUtils.getAttributeStrNotNull(info, "CODCITTADINANZA");
       codMotNazionalita    = StringUtils.getAttributeStrNotNull(info, "CODMOTNAZIONALITA");
       strMotivazione     = StringUtils.getAttributeStrNotNull(info, "STRMOTIVAZIONE");       
       prgCittadinanza    = info.getAttribute ("PRGCITTADINANZA");
       cdnUtins= info.containsAttribute("cdnUtins") ? info.getAttribute("cdnUtins").toString() : "";
       dtmins=info.containsAttribute("dtmins") ? info.getAttribute("dtmins").toString() : "";
       cdnUtmod=info.containsAttribute("cdnUtmod") ? info.getAttribute("cdnUtmod").toString() : "";
       dtmmod=info.containsAttribute("dtmmod") ? info.getAttribute("dtmmod").toString() : "";
       //Sono in modalità dettaglio
       if (prgCittadinanza != null) {
          operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
       }        
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

  String url_nuovo = "AdapterHTTP?PAGE=CittadinanzePage" + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz ;
%>

<html>
<head>

<%@ include file="_infCorrentiAzienda.inc" %> 
<script type="text/javascript">
  var flagChanged = false;
  function CittadinanzaDelete(prgCittadinanza, cittadinanza, prgRichiestaAz) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    if ( confirm("Eliminare la cittadinanza " + cittadinanza + " ?") ) {
      var s= "AdapterHTTP?PAGE=CittadinanzePage";
      s += "&CANCELLA=Y";
      s += "&PRGCITTADINANZA=" + prgCittadinanza;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + <%= _funzione%>;
      setWindowLocation(s);
    }
  }

function CittadinanzaSelect(prgCittadinanza,prgRichiestaAz) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CittadinanzePage";
    s += "&PRGCITTADINANZA=" + prgCittadinanza;
    s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
    s += "&CDNFUNZIONE=" + <%= _funzione%>;
    s += "&SELDETTAGLIO=1";   
    s += "&APRIDIV=1";
    setWindowLocation(s);
  }

function fieldChanged() {
      flagChanged = true;
  }
 
function AbilitaAltraMotivNaz() {
	if (document.DivForm.codMotNazionalita.value == 'ALT') {
		document.DivForm.Motivazione.disabled = false;
	}
	else {
		document.DivForm.Motivazione.value = "";
		document.DivForm.Motivazione.disabled = true;
	}
}

function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	if('<%=codCittadinanza%>' == scelta){
			combo[0].selected=true;
			return;
	}
	if(comboValue == scelta){
		alert("Scelta non valida");
		for(i=0; i< combo.options.length; i++){
			if(combo[i].value == '<%=codCittadinanza%>'){
			break;
		  }
		}
		combo[i].selected=true;
	
	}
  }
  
  
function codificaInesistente(combo, scelta){
	if(combo[combo.selectedIndex].value == scelta){
		return true;
	}else{
		return false;
	}
}

function verificaCampi() {
	if(codificaInesistente(document.DivForm.CODCITTADINANZA, 'NT')){
  		alert("Scelta non valida.")
  		return;
  	}
	if (document.DivForm.codMotNazionalita.value == 'ALT') {
		if (document.DivForm.Motivazione.value == "") {
			alert("Inserire motivazione per la voce selezionata!");
	      	document.DivForm.Motivazione.focus();
			return false;
		}
	}
  return true;
}
 
</script>



  <title>Cittadinanza Richiesta</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <script language="JavaScript" src="../../js/layers.js"></script> 
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
  </script>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
</script>
</head>
<body class="gestione" onload="rinfresca()">
<%
  infCorrentiAzienda.show(out); 
  linguette.show(out);
%>

    <center>
      <af:showErrors/>
      <af:showMessages prefix="MAggiornaCittadinanzaRichiesta"/>
      <af:showMessages prefix="MDeleteCittadinanzaRichiesta"/>
      <af:showMessages prefix="MInserisciCittadinanzaRichiesta"/>
    </center>

    <af:list moduleName="MListaCittadinanzaRichiesta" skipNavigationButton="1"
             canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
             jsSelect="CittadinanzaSelect" jsDelete="CittadinanzaDelete"/>

    <%
    if(canInsert) {
    %>
      <p align="center">
        <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova nazionalità"/>   
      </p>
    <%}%>

    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
       style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
<%
  String divStreamTop = StyleUtils.roundLayerTop(canManage);
  String divStreamBottom = StyleUtils.roundLayerBottom(canManage);
%>
         <%out.print(divStreamTop);%>

        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuova nazionalità
            <%} else {%>
              Nazionalità
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
      <af:form method="POST" action="AdapterHTTP" name="DivForm" onSubmit="verificaCampi()">
      <input type="hidden" name="PAGE" value="CittadinanzePage"/>
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      <input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>"/>
      <input type="hidden" name="PRGCITTADINANZA" value="<%=prgCittadinanza%>"/>

      
        <table align="center">
        <%@ include file="DettaglioCittadinanzaRichiesta.inc" %>
        </table>      
    
      <table class="main" width="100%">
      <%if(nuovo) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="submit" class="pulsanti" name="inserisci" value="Inserisci">
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
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
          <%if (!nuovo) {%>                          
            <p align="center">
              <% operatoreInfo.showHTML(out); %>
            </p>
          <%}%>
          </td>
        </tr>
        <%}%>
        </table>
      </af:form>
    </div>
<%out.print(divStreamBottom);%> 
</body>
</html>

<% } %>