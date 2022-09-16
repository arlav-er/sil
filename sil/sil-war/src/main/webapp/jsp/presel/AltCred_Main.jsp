<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,   
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("inserisci");
      canDelete = attributi.containsButton("rimuovi");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
%>

<%  
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  
  Vector rows = serviceResponse.getAttributeAsVector("M_ListAltCred.ROWS.ROW");
  boolean infStor = serviceRequest.containsAttribute("INFSTOR");
  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_ListAltCred.ROWS.ROW");

  BigDecimal prgCreditoList=new BigDecimal(0);
  String strSpecificaList= "";
  String strSpecifica= "";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  Testata operatoreInfo   = null;
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 

  /*
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");  */

  String fieldReadOnly;

  String btnChiudiDettaglio="";
  if (canModify) {
    btnChiudiDettaglio = "Chiudi senza aggiornare";
    fieldReadOnly="false";
  }
  else {
    btnChiudiDettaglio="Chiudi";
    fieldReadOnly="true";
  }
  
  boolean readOnly = new Boolean (fieldReadOnly).booleanValue();
  
  
  // *************
  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_LoadAltCred")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=AltCredPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  if(!nuovo) {
  // Sono in modalità "Dettaglio"
     Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LoadAltCred.ROWS.ROW");
      if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
    
        SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);
        
        prgCreditoList  = (BigDecimal) beanLastInsert.getAttribute("PRGCREDITO");
        strSpecifica    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRSPECIFICA");
        cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
        dtmIns          = beanLastInsert.getAttribute("DTMINS");
        cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
        dtmMod          = beanLastInsert.getAttribute("DTMMOD");
      }
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>

<html>

<head>
  <title>Altri Crediti</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
    var flagChanged = false;

    function Select(prg) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var s= "AdapterHTTP?PAGE=AltCredPage";
      s += "&MODULE=M_LoadAltCred";
      s += "&PRGCREDITO=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%= _funzione %>";
      s += "&APRIDIV=1";
      
      setWindowLocation(s);
    }

    function Delete(prgCredito, tipo, dettaglio) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var s="Eliminare il credito ";

      if ( (dettaglio != null) && (dettaglio.length > 0) ) {

        s += dettaglio.replace(/\^/g, '\'');
      }

      s += " ?";
    
      if ( confirm(s) ) {

        var s= "AdapterHTTP?PAGE=AltCredPage";
        s += "&MODULE=M_DeleteAltCred";
        s += "&PRGCREDITO=" + prgCredito;
        s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
        s += "&CDNFUNZIONE=<%=_funzione%>";

        setWindowLocation(s);
      }
    }
    
    function Insert() {
      document.MainForm.MODULE.value= "M_InsertAltCred";
      if(controllaFunzTL())  {
        doFormSubmit(document.MainForm);
        }
      else
        return;
    }
    
    function Update()
      {
        var datiOk = controllaFunzTL();
        if (datiOk) {
          document.MainForm.MODULE.value = "M_UpdateAltCred";
          
          doFormSubmit(document.MainForm);
        }
      }
      
  function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.MainForm;
   }
  
function chiudiLayer(scriptVar) {

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     } else { 
         // Vogliamo chiudere il layer. 
         // Pongo il flag = false per evitare che mi venga riproposto 
         // un "confirm" quando poi navigo con le linguette nella pagina principale
         flagChanged = false;
     }
     
  }
  if (ok) {
     eval(scriptVar);
     //ChiudiDivLayer('divLayerDett');
  }
}

</SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

      <input type="hidden" name="PAGE" value="AltCredPage">
      <input type="hidden" name="MODULE" value="M_InsertAltCred"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="CDNUTINS" value="<%= strCodiceUtenteCorrente %>"/>
      <input type="hidden" name="CDNUTMOD" value="<%= strCodiceUtenteCorrente %>"/>
      <% if (!nuovo)  {%>
      <input type="hidden" name="PRGCREDITO" value="<%=prgCreditoList%>"/>
      <% } %>
      
 
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertAltCred"/>
          <af:showMessages prefix="M_DeleteAltCred"/>
          <af:showMessages prefix="M_UpdateAltCred"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_ListAltCred" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuovo credito formativo"/>        
          </p>
        <%}
		  String divStreamTop = StyleUtils.roundLayerTop(canModify);
		  String divStreamBottom = StyleUtils.roundLayerBottom(canModify);          
        %>
      
        <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
         style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
    <a name="aLayerIns"></a>
    <%out.print(divStreamTop);%>
        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuovo credito formativo
            <%} else {%>
              Credito formativo
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

		<br>          
	    <table class="main" width="100%">		
          <tr>
           <td colspan="2" align="center">&nbsp;
           </td>
          </tr>
		  <%@ include file="AltCred_Elemento.inc" %>                         
          <tr>
           <td colspan="2" align="center"><br>
           </td>
          </tr>
            

          <tr>
        <%if(nuovo) {%>
            <td colspan="2" align="center">
              <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
              <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer('ChiudiDivLayer(\'divLayerDett\')');">
            </td>
        <%} else {%>
                              
          <%if (canModify) {%>    
             <td width="40%" align="right">
               <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
             </td>
          <%}
        }%>
             <td width="60%" align="left"> 
             	<%if((canModify) && (!nuovo)){%>
                   <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
                          onclick="chiudiLayer('openPage(\'<%= _page %>\',\'&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_funzione%>\')');"> 
                <%} else {
                   if(!canModify) {%>
                   	<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer('ChiudiDivLayer(\'divLayerDett\')');">                                                                
                 <%}
                 }%>
             </td>        
          </tr>
		</TABLE>          
		<br>
		<%if (!nuovo) {%>                          
		<table align="center">
		  <tr colspan="2">
		   <td align="center">
		    <%operatoreInfo.showHTML(out);%>
		   </td>
		  </tr>
		</table>
		<%}%>                             
		
      </div>
 <%out.print(divStreamBottom);%>        
  </af:form>
</body>
</html>