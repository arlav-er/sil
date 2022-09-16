<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="getAziendaRichiesta.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  strPrgAziendaMenu = (String) serviceRequest.getAttribute("PRGAZIENDA");
  strPrgUnitaMenu = (String) serviceRequest.getAttribute("PRGUNITA");

  String _page = "GestLingueRichiestaPage";
  PageAttribs attributi = new PageAttribs(user, _page);
  Testata operatoreInfo=null;
  BigDecimal cdnGradoLetto = null;
  BigDecimal cdnGradoScritto = null;
  BigDecimal cdnGradoParlato = null;
  String codLingua="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaLinguaRichiesta";
  String btnSalva="Aggiorna";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";

  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();

  
 // String _page = (String) serviceRequest.getAttribute("PAGE");

  
  //  Testata operatoreInfo   = null;
    boolean nuovo = true;
  //  PageAttribs attributi = new PageAttribs(user, "GestLingueRichiestaPage");
  //  boolean canModify = attributi.containsButton("INSERISCI");

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda(prgAzienda);
  filter.setPrgUnita(prgUnita);
  
	boolean canView=filter.canViewUnitaAzienda();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
    	canInsert= attributi.containsButton("INSERISCI");
      	canDelete= attributi.containsButton("CANCELLA");
      	canModify= attributi.containsButton("AGGIORNA");
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
  
  String btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
    String fieldReadOnly;
    String btnChiudiDettaglio="";
    
  //  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    
    //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
    PageAttribs attributiProfilo = new PageAttribs(user, "IdoEtaEsperienzaPage");
  	boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");
    
    LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda,prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
    Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
    linguette.setCodiceItem("prgRichiestaAz");


    
     String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");

  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=GestLingueRichiestaPage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz +                      
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
                     


    if(serviceResponse.containsAttribute("SelectDettaglioLinguaRichiesta")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

        SourceBean cont = (SourceBean) serviceResponse.getAttribute("SelectDettaglioLinguaRichiesta");



  BigDecimal prgLingua = null;

  if(!nuovo) {
        SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
        prgLingua = (BigDecimal) row.getAttribute("PRGLINGUA");
        String strRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
      //  prgRichiestaAz = new BigDecimal(strRichiestaAz);
        codLingua=row.getAttribute("CODLINGUA").toString();
        cdnGradoLetto=row.containsAttribute("CDNGRADOLETTO") ? (BigDecimal)row.getAttribute("CDNGRADOLETTO") : null;
        cdnGradoScritto=row.containsAttribute("CDNGRADOSCRITTO") ? (BigDecimal)row.getAttribute("CDNGRADOSCRITTO") : null;
        cdnGradoParlato=row.containsAttribute("CDNGRADOPARLATO") ? (BigDecimal)row.getAttribute("CDNGRADOPARLATO") : null;
        objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
        if (objFlgIndispensabile != null) {
          strFlgIndispensabile = objFlgIndispensabile.toString();
        }
       // LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canModify));
       // Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(strRichiestaAz));
        linguette.setCodiceItem("prgRichiestaAz");
      //  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu,strPrgUnitaMenu);

        cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
        dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
        cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
        dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
        //Sono in modalità dettaglio
        if (prgLingua != null) {
                operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
              }        
  }
  
    if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
  
  if (canModify) {
      btnChiudiDettaglio = "Chiudi senza aggiornare";
      fieldReadOnly="false";
    }
    else {
      btnChiudiDettaglio="Chiudi";
      fieldReadOnly="true";
    }
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Conoscenze Linguistiche</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  </SCRIPT>

  <SCRIPT TYPE="text/javascript">

<!--


var flagChanged = false;

function Select(prgLingua) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
   
    var s= "AdapterHTTP?PAGE=GestLingueRichiestaPage";
    s += "&MODULE=SelectDettaglioLinguaRichiesta";
    s += "&prgLingua=" + prgLingua;
    s += "&PRGAZIENDA=<%=prgAzienda%>";
    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
    s += "&PRGUNITA=<%=prgUnita%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);

  }
  
  function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	if(codificaInesistente(combo, scelta)){
		alert("Scelta non valida");
		if('<%= codLingua %>' == scelta){
			combo[0].selected=true;
			return;
		}
		for(i=0; i< combo.options.length; i++){
			if(combo[i].value == '<%= codLingua %>'){	
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


 function Delete(prgLingua, tipo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere la lingua:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=GestLingueRichiestaPage";
      s+= "&MODULE=MDeleteLinguaRichiesta";
      s += "&prgLingua=" + prgLingua;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.frmMascheraLinguaRichiesta.MODULE.value = "MInserisciLinguaRichiesta";
  if(controllaFunzTL())  {
    doFormSubmit(document.frmMascheraLinguaRichiesta);
    }
  else
    return;
}
    
function Update()
  {
  	if(codificaInesistente(document.frmMascheraLinguaRichiesta.CODLINGUA, 'NT')){
  		alert("Scelta non valida.")
  		return;
  	}
    if (controllaFunzTL()) {
      document.frmMascheraLinguaRichiesta.MODULE.value = "MAggiornaLinguaRichiesta";
      doFormSubmit(document.frmMascheraLinguaRichiesta);
    }
  }
      
function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.frmMascheraLinguaRichiesta;
   }
-->
</SCRIPT>

<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 


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
  linguettaAlternativa.show(out); 
%>
<BR/>
<%
linguette.show(out);
%>  

    
  <af:form name="frmMascheraLinguaRichiesta" action="AdapterHTTP" method="POST">
      <input type="hidden" name="PAGE" value="<%=_page%>">
      <input type="hidden" name="MODULE" value="<%=moduleName%>">
      <input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
      <input type="hidden" name="PRGLINGUA" value="<%=prgLingua%>">
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
  
      
 


      <center>
        <font color="green">
          <af:showMessages prefix="MInserisciLinguaRichiesta"/>
          <af:showMessages prefix="MDeleteLinguaRichiesta"/>
          <af:showMessages prefix="MAggiornaLinguaRichiesta"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="MListaLingueRichiesta" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>

      <%
      if(canInsert) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova conoscenza linguistica"/>   
          
          </p>
      <%}%>

<%
  String divStreamTop = StyleUtils.roundLayerTop(canManage);
  String divStreamBottom = StyleUtils.roundLayerBottom(canManage);
%>


        <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
         style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
        <%out.print(divStreamTop);%>

        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuova conoscenza linguistica
            <%} else {%>
              Conoscenza linguistica
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

        <table align="center"  width="100%" border="0"> 
          <%@ include file="DettaglioLinguaRichiesta.inc" %>





            <tr>
              <td colspan="4" align="center">   


            <%if(nuovo && canInsert) {%>
               <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick=" ControllaGrado(); Insert(); ">
               <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
            <%} else { 


              if (canModify) {%>    
                <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="ControllaGrado(); Update(); " >
            <%}
              }%>
            <%if((canModify) && (!nuovo)){%>
               <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
                     onclick="ChiudiDivLayer('divLayerDett');">
            <%} else {
                if(!canModify) {%>
                   <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                
              <%}
              }%>        
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
        </table>
      <%out.print(divStreamBottom);%>
     </af:form>
  </div>
</body>
</html>

<% } %>