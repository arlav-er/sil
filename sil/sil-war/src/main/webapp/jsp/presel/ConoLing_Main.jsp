<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,     
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  com.engiweb.framework.security.*,
  it.eng.sil.security.PageAttribs,  
  it.eng.sil.security.User"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

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
/*
	RequestContainer rc = getRequestContainer(request);	
	SessionContainer sessionContainer;
	
	if ( rc != null ) {
		sessionContainer = rc.getSessionContainer();
	} else {
		rc = (RequestContainer)session.getAttribute("REQUEST_CONTAINER");
		sessionContainer = rc.getSessionContainer();
	}

  //prelevo la response e la request
  ResponseContainer responseContainer=ResponseContainerAccess.getResponseContainer(request);
  SourceBean serviceResponse = responseContainer.getServiceResponse();

  RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request);
  SourceBean serviceRequest= requestContainer.getServiceRequest();
*/
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  String strNome       = (String)serviceRequest.getAttribute("LAV_NOME");
  String strCognome    = (String)serviceRequest.getAttribute("LAV_COGNOME");
  
  String preInsert    = (String)serviceResponse.getAttribute("M_VERIFICAPREINSERTCONOSCENZALING.VERIFICA_OK");
  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTCONOSCENZELING.ROWS.ROW");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String prgLinguaList= "";
  String codLinguaList= "";
  String cdnGradoLettoList    = "";
  String cdnGradoScrittoList  = "";
  String cdnGradoParlatoList  = "";  
  
  String codLingua= "";
  String lingua   = "";
  String strGradoLetto    = "";
  String strGradoScritto  = "";
  String strGradoParlato  = "";  
  String flgCertificato="";
  String flgPrimaLingua="";
  String codModLingua="";
  String strModLingua="";
  String newCdnGradoLetto="";
  String newCdnGradoScritto="";
  String newCdnGradoParlato="";

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //Linguette linguette = new Linguette( _funzione, _page, new BigDecimal(cdnLavoratore));
  Linguette l = new Linguette(user, 1, _page, new BigDecimal(cdnLavoratore));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  /*
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");  
  */
  String fieldReadOnly;
  
 BigDecimal prgLingua=new BigDecimal(0);;
  BigDecimal cdnGradoLetto=new BigDecimal(0);
  BigDecimal cdnGradoScritto=new BigDecimal(0);
  BigDecimal cdnGradoParlato=new BigDecimal(0);
  BigDecimal cdnUtIns=new BigDecimal(0);
  String dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);
  String btnChiudiDettaglio="";
  String dtmMod="";
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
  if(serviceResponse.containsAttribute("M_LOADCONOSCENZALING")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=ConoscenzeLingPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";

if(!nuovo) {
  //Sono in modalità dettaglio
  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADCONOSCENZALING.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgLingua       = (BigDecimal)beanLastInsert.getAttribute("PRGLINGUA");
    codLingua       = (String)beanLastInsert.getAttribute("CODLINGUA");
    cdnGradoLetto   = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOLETTO");
    cdnGradoScritto = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOSCRITTO");
    cdnGradoParlato = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOPARLATO");
    flgCertificato  = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGCERTIFICATO");
    flgPrimaLingua  = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGPRIMALINGUA");    
    codModLingua    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMODLINGUA");
    strModLingua    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRMODLINGUA");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = (String)beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = (String)beanLastInsert.getAttribute("DTMMOD");
  }
}
  if (cdnGradoLetto != null) {
    strGradoLetto = cdnGradoLetto.toString();
  }

  if (cdnGradoScritto != null) {
    strGradoScritto = cdnGradoScritto.toString();
  }

  if (cdnGradoParlato != null) {
    strGradoParlato = cdnGradoParlato.toString();
  }
  

  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

 // boolean canModify= attributi.containsButton("salva");

  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
%>
<%
//String htmlStreamTop = StyleUtils.roundTopTable(canModify);
//String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<html>

<head>
  <title>Conoscenze Linguistiche</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
// *****************************
  // Serve per il menu contestuale
  // *****************************
  function caricaMenuDX(cdnLavoratore, strNome, strCognome){

    var url = "AdapterHTTP?PAGE=MenuCompletoPage"
    url += "&CDNLAVORATORE=" + cdnLavoratore;
    url += "&STRNOME=" + strNome;
    url += "&STRCOGNOME=" + strCognome;
    
    window.top.menu.location = url;
  }


// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;
        
function fieldChanged() 
{ <% if (canModify) { %>
        //alert("CAMBIATO!!");
        flagChanged = true;
  <% } %>
}


function Select(prgLingua) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=ConoscenzeLingPage";
    s += "&MODULE=M_LOADCONOSCENZALING";
      s += "&PRGLINGUA=" + prgLingua;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);
  }

 function Delete(prgLingua, tipo) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere La competenza:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=ConoscenzeLingPage";
      s+= "&MODULE=M_DeleteConoscenzaLing";
      s += "&PRGLINGUA=" + prgLingua;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.Frm1.MODULE.value = "M_InsertConoscenzaLing";
  if(controllaFunzTL())  {
    doFormSubmit(document.Frm1);
    }
  else
    return;
}

function codificaInesistente(combo, scelta){
	if(combo[combo.selectedIndex].value == scelta){
		return true;
	}else{
		return false;
	}
}

function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	if(codificaInesistente(combo, scelta)){
		alert("Scelta non valida");
		if('<%= codLingua %>' == scelta){
			combo[0].selected=true;
			return false;
		}
		for(i=0; i< combo.options.length; i++){
			if(combo[i].value == '<%= codLingua %>'){	
				break;				
			}
		}
		combo[i].selected=true;
		return true;
	}
	return true;
}
    
function Update()
  {
   	if(!inibisciScelta(document.Frm1.CODLINGUA, 'NT')){
   	  	return;
   	}
    var datiOk = controllaFunzTL();     
    if (datiOk) {    
      document.Frm1.MODULE.value = "M_UpdateConoscenzaLing";
      doFormSubmit(document.Frm1);
    }
  }

function InserimentoDuplicato(preInsert)
  {
 if ( preInsert=="FALSE") {
  alert ("Inserimento di una lingua duplicato")
 }
  }
  
  
function chiudiLayer() {

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
     ChiudiDivLayer('divLayerDett');
  }
}

</SCRIPT>


<script language="javascript">
  function ControllaGrado() {
    if (document.Frm1.CDNGRADOSCRITTO.value == '') {
      document.Frm1.CDNGRADOSCRITTO.value = document.Frm1.CDNGRADOLETTO.value;
    }
    if (document.Frm1.CDNGRADOPARLATO.value == '') {
      document.Frm1.CDNGRADOPARLATO.value = document.Frm1.CDNGRADOLETTO.value;
    }
  }
</script>
<%


  if (strGradoLetto != null) {
    newCdnGradoLetto=strGradoLetto.toString();
  }
  if (strGradoScritto != null) {
    newCdnGradoScritto=strGradoScritto.toString();
  }
  if (strGradoParlato != null) {
    newCdnGradoParlato=strGradoParlato.toString();
  }
%>

  <%@ include file="ConoInfo_CommonScripts.inc" %>
  <script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>
<script>
             window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</script>
</head>

<body class="gestione" onload="rinfresca();InserimentoDuplicato('<%= preInsert %>')">


  <%
    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    infCorrentiLav.show(out);    
    l.show(out);
  %>
      
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertConoscenzaLing"/>
          <af:showMessages prefix="M_DeleteConoscenzaLing"/>
          <af:showMessages prefix="M_UpdateConoscenzaLing"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_ListConoscenzeLing" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova conoscenza linguistica"/>   
          
          </p>
      <%}%>

<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <a name="aLayerIns"></a>
  
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

    <af:form  name="Frm1" method="POST" action="AdapterHTTP">
        <table align="center"  width="100%" border="0"> 
           <tr valign="top">
              <td class="etichetta">Lingua</td>
              <td class="campo">
                  <af:comboBox name="CODLINGUA"
                               title="Lingua"
                               required="true"
                               moduleName="M_ListLingue"
                               addBlank="true"
                               classNameBase="input"
                               disabled="<%= fieldReadOnly %>"
                               onChange="inibisciScelta(this, 'NT');fieldChanged()"
                               selectedValue="<%= codLingua %>" />
              </td>
           </tr>
           <tr valign="top">
             <td colspan="3"><br/><div class="sezione2">Livello di conoscenza</div>
           </tr>
           <tr valign="top">
              <td class="etichetta">Letto</td>
              <td class="campo">
                <af:comboBox name="CDNGRADOLETTO"
                             title="Grado conoscenza letto"
                             moduleName="M_ListGradoLingue"
                             classNameBase="input"
                             addBlank="true" 
                             required="true" 
                             disabled="<%= fieldReadOnly %>"
                             onChange="fieldChanged()"
                             selectedValue="<%= newCdnGradoLetto %>" />
              </td>
           </tr>
           <tr valign="top">
              <td class="etichetta">Scritto</td>
              <td class="campo">
                <af:comboBox name="CDNGRADOSCRITTO"
                             title="Grado conoscenza scritto"
                             moduleName="M_ListGradoLingue"
                             classNameBase="input"
                             addBlank="true"
                             disabled="<%= fieldReadOnly %>"
                             onChange="fieldChanged()"
                             selectedValue="<%= newCdnGradoScritto %>" />
              </td>
           </tr>          
           <tr valign="top">
              <td class="etichetta">Parlato</td>
              <td class="campo">
                <af:comboBox name="CDNGRADOPARLATO"
                             title="Grado conoscenza scritto"
                             moduleName="M_ListGradoLingue"
                             classNameBase="input"
                             addBlank="true"
                             disabled="<%= fieldReadOnly %>"
                             onChange="fieldChanged()"
                             selectedValue="<%= newCdnGradoParlato %>" />
              </td>
           </tr>
           <tr valign="top">
              <td colspan="3"><HR></td>
           </tr>
           <tr>
              <td class="etichetta">Modalità di Acquisizione</td>
              <td class="campo">
                <af:comboBox name="CODMODLINGUA"
                             title="Modalità di acquisizione"
                             moduleName="M_ListModLingue"
                             classNameBase="input"
                             addBlank="true"
                             disabled="<%= fieldReadOnly %>"
                             onChange="fieldChanged()"
                             selectedValue="<%= codModLingua %>" />
                &nbsp;&nbsp;Conoscenza certificata ?&nbsp;
                <af:comboBox 
                  title="Conoscenza certificata" 
                  name="FLGCERTIFICATO"
                  classNameBase="input"
                  disabled="<%= fieldReadOnly %>"
                  onChange="fieldChanged()">              
                  <option value=""  <% if ( "".equals(flgCertificato) )  { %>SELECTED="true"<% } %> ></option>
                  <option value="S" <% if ( "S".equals(flgCertificato) ) { %>SELECTED="true"<% } %> >Si</option>
                  <option value="N" <% if ( "N".equals(flgCertificato) ) { %>SELECTED="true"<% } %> >No</option>
                </af:comboBox>
              </td>            
            </tr>
            <tr valign="top">
              <td class="etichetta">Altra Modalità</td>
              <td class="campo">
                <af:textBox name="STRMODLINGUA" classNameBase="input" maxlength="100" title="Altra Modalità" value="<%= strModLingua %>" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
              </td>
            </tr>
            <tr>
              <td colspan="4" align="center">   
                <input type="hidden" name="PAGE" value="ConoscenzeLingPage">
                <input type="hidden" name="MODULE" value="M_VerificaPreInsertConoscenzaLing"/>
                <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
                <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>    
                <input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
                <input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
                <input type="hidden" name="strNome" value="<%= strNome %>" />
                <input type="hidden" name="strCognome" value="<%= strCognome %>" />
                <input type="hidden" name="PRGLINGUA" value="<%= prgLingua %>"/>
            <%if(nuovo) {%>
                <input class="pulsante" type="submit" name="salva" value="Inserisci" onclick="javascript:ControllaGrado();">
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
            <%} else { 
              if (canModify) {%>    
                <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="ControllaGrado();Update();">
            <%}
              }%>
            <%if((canModify) && (!nuovo)){%>
                <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>" onclick="chiudiLayer();">
            <%} else {
                if(!canModify) {%>
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer();">                                                                
              <%}
              }%>        
              </td>
            </tr>
          <%if (!nuovo) {%>    
            <tr><td colspan="2" width="70%" align="left"><%testata.showHTML(out);%></td></tr>      
          <%}%>  
        </table>
    </af:form>
    
<!-- Stondature ELEMENTO BOTTOM -->
<%out.print(divStreamBottom);%>
  </div>
<!-- LAYER - END -->

</body>
</html>