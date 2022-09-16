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
  
  Vector rows = serviceResponse.getAttributeAsVector("M_ListConoscenzeInfo.ROWS.ROW");
  boolean infStor = serviceRequest.containsAttribute("INFSTOR");
  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_ListConoscenzeInfo.ROWS.ROW");
  Vector tipiDettInfoRows = null;
  tipiDettInfoRows=serviceResponse.getAttributeAsVector("M_LISTDETTAGLIALLCONOSCENZAINFO.ROWS.ROW");
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




  String strSpecificaList= "";
  String strSpecifica= "";



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
  boolean canDelete= attributi.containsButton("rimuovi");  
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
  if(serviceResponse.containsAttribute("M_LoadConoscenzaInfo")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=ConoscenzeInfoPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  if(!nuovo) {


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
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>

<html>

<head>
  <title>Conoscenze informatiche (Preselezione)</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">
  function ConoscenzaSelect(prgInfo) {

    var s= "AdapterHTTP?PAGE=ConoscenzeInfoPage";
    s += "&MODULE=M_LoadConoscenzaInfo";
    s += "&PRGINFO=" + prgInfo;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";

    setWindowLocation(s);
  }

  function ConoscenzaDelete(prgInfo, tipo, dettaglio) {

    var s="Eliminare la conoscenza informatica\ndi tipo " + tipo;

    if ( (dettaglio != null) && (dettaglio.length > 0) ) {

      s += " - " + dettaglio.replace(/\^/g, '\'');
    }

    s += " ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=ConoscenzeInfoPage";
      s += "&MODULE=M_DeleteConoscenzaInfo";
      s += "&PRGINFO=" + prgInfo;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }




   
     window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

  <%@ include file="ConoInfo_CommonScripts.inc" %>
  <SCRIPT TYPE="text/javascript">

    function Select(prg) {
      
      var s= "AdapterHTTP?PAGE=ConoscenzeInfoPage";
      s += "&MODULE=M_LoadConoscenzaInfo";
      s += "&PRGINFO=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%= _funzione %>";
      s += "&APRIDIV=1";
      
      setWindowLocation(s);
    }

    function Delete(prgInfo, tipo, dettaglio) {

      var s="Eliminare la conoscenza informatica ";

      if ( (dettaglio != null) && (dettaglio.length > 0) ) {

        s += dettaglio.replace(/\^/g, '\'');
      }

      s += " ?";
    
      if ( confirm(s) ) {

        var s= "AdapterHTTP?PAGE=ConoscenzeInfoPage";
        s += "&MODULE=M_DeleteConoscenzaInfo";
        s += "&PRGINFO=" + prgInfo;
        s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
        s += "&CDNFUNZIONE=<%=_funzione%>";

        setWindowLocation(s);
      }
    }
    
    function Insert() {
      document.MainForm.MODULE.value= "M_InsertConoscenzaInfo";
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
          document.MainForm.MODULE.value = "M_UpdateConoscenzaInfo";
          
          doFormSubmit(document.MainForm);
        }
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
      

    var flagChanged = false;
  
    function fieldChanged() {
      flagChanged = true;
    }
  
   function getFormObj() {
     return document.MainForm;
   }
  
  </SCRIPT>
<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
</head>
<%
	//la condizione java inserita sull'attributo onload dell'elemento body esegue il filtro della combo di dettaglio
	//quando viene aperto il layer di dettaglio per la modifica di un elemento già esistente
%>
<body class="gestione" onload="rinfresca();<%=(("".equals(apriDiv) && !nuovo) ? "caricaDettInfo(document.MainForm.CODTIPOINFO.value, document.MainForm.CODICE.value,'');" : "")%>">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

    <input type="hidden" name="PAGE" value="ConoscenzeInfoPage">
    <input type="hidden" name="MODULE" value="M_InsertConoscenzaInfo"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CODDETTINFO" value="<%= codDettInfo %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="PRGINFO" value="<%=prgInfo%>"/>

      
  
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertConoscenzaInfo"/>
          <af:showMessages prefix="M_DeleteConoscenzaInfo"/>
          <af:showMessages prefix="M_UpdateConoscenzaInfo"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_ListConoscenzeInfo" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova conoscenza informatica"/>        
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
          Nuova conoscenza informatica
        <%} else {%>
          Conoscenza informatica
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
    <br>        
          
    <TABLE>
<% if (nuovo){ %>  
        <tr>
        <td class="etichetta">Tipo</td>
        <td class="campo">    
          <af:comboBox name="CODTIPOINFO" size="1" title="Tipo Conoscenza"
               multiple="false" required="true"
               focusOn="false" moduleName="M_ListTipiConoscenzaInfo"
               addBlank="true" blankValue=""
               onChange="javascript:caricaDettInfo(MainForm.CODTIPOINFO.value,'','nuovo');fieldChanged();"
               selectedValue="<%= codTipoInfo.toString() %>"
               disabled="<%= String.valueOf( !canModify ) %>" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Dettaglio</td>
        <td class="campo">
          <af:comboBox multiple="true" title="Dettaglio" name="CODICE" required="true" size="4"/>&nbsp;

      </tr>
<% } else { %> 
      <tr>
        <td class="etichetta">Tipo</td>
        <td class="campo">    
          <af:comboBox name="CODTIPOINFO" size="1" title="Tipo Conoscenza"
               multiple="false" required="true"
               focusOn="false" moduleName="M_ListTipiConoscenzaInfo"
               addBlank="true" blankValue=""
               onChange="javascript:caricaDettInfo(MainForm.CODTIPOINFO.value,'','nuovo');fieldChanged();"
               selectedValue="<%= codTipoInfo.toString() %>"
               disabled="<%= String.valueOf( !canModify ) %>" />
        </td>
  	  </tr>
      <tr>
        <td class="etichetta">Dettaglio</td>
        <td class="campo">
           <af:comboBox title="Dettaglio"
                       name="CODICE"
                       required="true"
                       moduleName="M_LISTDETTAGLIALLCONOSCENZAINFO"
                       classNameBase="input"
                       selectedValue="<%=codDettInfo%>"
                       onChange="fieldChanged();"
                       disabled="<%= String.valueOf( !canModify ) %>"/>&nbsp;
      </tr>
<% } %>        
      <%@ include file="ConoInfo_Elemento.inc" %>                 
   </TABLE>



   <table class="main" width="100%">
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">    
           <%if(nuovo) {%>
                <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
           <%} else {%>

            <%if (canModify) {%>    
              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
              <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
                     onclick="ok=true;
                       			if (flagChanged){
						     			ok= confirm('I dati sono cambiati.\r\nProcedere lo stesso ?');
	                    		}
	                    		if (ok) openPage('<%= _page %>','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_funzione%>'); ">
           <%} else {%>
              <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                              
           <%}%>
        <%}%>
        </td>
      </tr>
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