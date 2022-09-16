<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
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

  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector abilitazioniLavoratoreRows=null;
  abilitazioniLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATOREABILITAZIONI.ROWS.ROW");

//gestione delle codifiche, devono essere gestite in modo "speciale"
  Vector tipiAbilitazioniRows=null;
  tipiAbilitazioniRows=serviceResponse.getAttributeAsVector("M_GetTipiAbilitazione.ROWS.ROW");
  SourceBean row_tipiAbilitazione= null;

  SourceBean row_abilitazioniLavoratore= null;
  BigDecimal prgAbilitazione=new BigDecimal(0);
  
  String strDescrizione="";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  Testata operatoreInfo   = null;
  
  String codTipoAbilitazioneGen="";
  String codAbilitazioneGen="";
  String _strNote="";
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();
  
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  // NOTE: Attributi della pagina (pulsanti e link) 
    /*PageAttribs attributi = new PageAttribs(user, "CurrAbilMainPage");
    boolean canModify = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("rimuovi");   
  */
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
  if(serviceResponse.containsAttribute("M_GetAbilitazione")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=CurrAbilMainPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  if(!nuovo) {
  //Sono in modalità DETTAGLIO
    Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_GetAbilitazione.ROWS.ROW");
      if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
        
        SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);
        
        prgAbilitazione  = (BigDecimal) beanLastInsert.getAttribute("PRGABILITAZIONE");
        //strDescrizione    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
        cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
        dtmIns          = beanLastInsert.getAttribute("DTMINS");
        cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
        dtmMod          = beanLastInsert.getAttribute("DTMMOD");
        codTipoAbilitazioneGen=StringUtils.getAttributeStrNotNull(beanLastInsert, "codtipoabilitazionegen");
        codAbilitazioneGen=StringUtils.getAttributeStrNotNull(beanLastInsert, "CODABILITAZIONEGEN");
        _strNote=StringUtils.getAttributeStrNotNull(beanLastInsert, "STRNOTE");
      }
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>
<html>

<head>
  <title>Abilitazioni (preselezione)</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
      window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);  
  </SCRIPT>

<SCRIPT TYPE="text/javascript">
var flagChanged = false;
//dichiaro gli array per riempire la mia supercombo
 var abil_tipo=new Array();
 var abil_cod=new Array();
 var abil_des=new Array();
<%     for(int i=0; i<tipiAbilitazioniRows.size(); i++)  { 
            row_tipiAbilitazione = (SourceBean) tipiAbilitazioniRows.elementAt(i);
            out.print("abil_tipo["+i+"]=\""+ row_tipiAbilitazione.getAttribute("TIPO").toString()+"\";\n");
            out.print("abil_cod["+i+"]=\""+ row_tipiAbilitazione.getAttribute("CODICE").toString()+"\";\n");
            out.print("abil_des["+i+"]=\""+ row_tipiAbilitazione.getAttribute("DESCRIZIONE").toString()+"\";\n");              
      }
%>
function caricaAbilitazioni(codAbilitazioneGen,modNuovo) {
   i=0;
   j=0;
   maxcombo=15;


   while (document.Frm1.tipoAbilitazione.options.length>0) {
        document.Frm1.tipoAbilitazione.options[0]=null;
    }

      for (i=0; i<abil_tipo.length ;i++) {
       if (abil_tipo[i]==codAbilitazioneGen) {
          document.Frm1.tipoAbilitazione.options[j]=new Option(abil_des[i], abil_cod[i], false, false);
           j++;
       }
     }
  if (modNuovo){ //Se è un nuovo inserimento il combo viene visualizzato come una lista
    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    document.Frm1.tipoAbilitazione.size=j;
  }
}

//DettaglioAbilitazione
function Select(prgAbilitazione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CurrAbilMainPage";
    s += "&MODULE=M_GetAbilitazione";
    s += "&PRGABILITAZIONE=" + prgAbilitazione;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&APRIDIV=1";
    setWindowLocation(s);
  }
  
 //DeleteAbilitazione
 function Delete(prgAbilitazione, tipo) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var t="Sicuri di voler rimuovere L'abilitazione:\n \'" + tipo.replace('^','\'');

    t += "\' ?";
    
    if ( confirm(t) ) {

      var s = "AdapterHTTP?PAGE=CurrAbilMainPage";
      s += "&MODULE=M_DelAbilitazione";
      s += "&PRGABILITAZIONE=" + prgAbilitazione;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }
  
  function Insert() {
      document.Frm1.MODULE.value = "M_InsertAbilitazione";
      if(controllaFunzTL())  {
        doFormSubmit(document.Frm1);
        }
      else
        return;
    }
    
    function Update()
      {
        var datiOk = controllaFunzTL();
        if (datiOk) {
          document.Frm1.MODULE.value = "M_SaveAbilitazione";
          
          doFormSubmit(document.Frm1);
        }
      }
      
  function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.Frm1;
   }

</SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

</head>

<body class="gestione" onload="rinfresca()">

  <%

    Linguette l = new Linguette( user,  _funzione, "CurrAbilMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
      
      <input type="hidden" name="PAGE" value="CurrAbilMainPage">
      <input type="hidden" name="MODULE" value="M_InsertAbilitazione"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="CDNUTINS" value="<%= strCodiceUtenteCorrente %>"/>
      <input type="hidden" name="CDNUTMOD" value="<%= strCodiceUtenteCorrente %>"/>
      
      <% if (!nuovo)  {%>
        <input type="hidden" name="PRGABILITAZIONE" value="<%=prgAbilitazione%>"/>
      <% } %>
      
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertAbilitazione"/>
          <af:showMessages prefix="M_DelAbilitazione"/>
          <af:showMessages prefix="M_SaveAbilitazione"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_GETLAVORATOREABILITAZIONI" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova abilitazione"/>        
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
          Nuova abilitazione
        <%} else {%>
          Abilitazione
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
    <br>        
    <table width="80%" cellspacing="2">
      <tr valign="top">
        <td class="etichetta">Tipo di abilitazione</td>
        <td class="campo">
            <% if (nuovo) {%>
              <af:comboBox title="Tipo di abilitazione" name="codTipoAbilitazioneGen" 
                           moduleName="M_GETTIPIGENABILITAZIONE" addBlank="true" 
                           onChange="javascript:caricaAbilitazioni(Frm1.codTipoAbilitazioneGen.value,true);fieldChanged();" 
                           required="true" />
            <%} else {%>
                <af:comboBox classNameBase="input" title="Tipo di abilitazione" name="codTipoAbilitazioneGen" 
                             moduleName="M_GETTIPIGENABILITAZIONE" addBlank="true" selectedValue="<%=codTipoAbilitazioneGen%>" 
                             onChange="javascript:caricaAbilitazioni(Frm1.codTipoAbilitazioneGen.value,false);fieldChanged();" 
                             required="true" disabled="<%= String.valueOf(!canModify) %>"  />
            <%}%>
        </td>
      </tr>
      <tr valign="top">
        <td class="etichetta">Abilitazione</td>
        <td class="campo">
          <% if (nuovo) {%>
            <af:comboBox multiple="true" title="Abilitazione" name="tipoAbilitazione" required="true" onChange="fieldChanged()"/>
            <%} else {%>
                <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Abilitazione" name="tipoAbilitazione" 
                             required="true" disabled="<%= String.valueOf(!canModify) %>"   >
                <OPTION value=""></OPTION>
              <%String selected="";
                for(int i=0; i<tipiAbilitazioniRows.size(); i++)  { 
                      row_tipiAbilitazione = (SourceBean) tipiAbilitazioniRows.elementAt(i);
                      selected=row_tipiAbilitazione.getAttribute("CODICE").toString().equals(codAbilitazioneGen)?"selected=\"true\"":"";
                      if (row_tipiAbilitazione.getAttribute("TIPO").equals(codTipoAbilitazioneGen)) {
                           out.print("<option value=\""+ row_tipiAbilitazione.getAttribute("CODICE").toString()+"\" "+selected+" >");
                           out.print(row_tipiAbilitazione.getAttribute("DESCRIZIONE").toString()+"</option>");              
                      }
                }%>
                </af:comboBox>
            <%}%>
        </td>
      </tr>
      <tr valign="top">
        <td class="etichetta">Note</td>
        <td class="campo">
          <af:textArea name="strNote" cols="25" value="<%=_strNote%>" onKeyUp="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>"/>
        </td>
      </tr>     
	</table>        
	<table class="main" width="100%">
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">
	    <%if(nuovo) {%>
	        <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
	        <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
	   <%} else {%>
		<%if (canModify) { %>    
	        <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
	   	<%}%>
			<input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
				   onclick="ChiudiDivLayer('divLayerDett');">       
	   	</td>
	  </tr>
	 <%}%>
	<%if (!nuovo) {%>                          
	    <tr>
	      <td colspan="4" align="center">   
	        <p align="center">
			  <%operatoreInfo.showHTML(out);%>
	        </p>
	      </td>
	    </tr>
	<%}%>                                   
    </table>
  </div>
 <%out.print(divStreamBottom);%>        
  </af:form>
</body>
</html>