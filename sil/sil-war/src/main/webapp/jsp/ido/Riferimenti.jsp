<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
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
String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector competenzeLavoratoreRows=null;
  competenzeLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATORECOMPETENZE.ROWS.ROW");
  SourceBean row_CompetenzeLavoratore= null;
  
  SourceBean row_competenzeLavoratore= (SourceBean) serviceResponse.getAttribute("M_GETCOMPETENZA_SINGOLA.ROWS.ROW");
  String codTipoCompetenza="";
  String strNote="";
  
//gestione delle codifiche, devono essere gestite in modo "speciale"
  Vector tipiCompetenzeRows=null;
  tipiCompetenzeRows=serviceResponse.getAttributeAsVector("M_GETCOMPETENZE.ROWS.ROW");
  SourceBean row_tipiCompetenze= null;


  BigDecimal prgCompetenza=new BigDecimal(0);
  String codCompetenza="";
  String strDescrizione="";
  
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  Testata operatoreInfo   = null;
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
 // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "CurrCompMainPage");
    boolean canModify = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("rimuovi");
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
  if(serviceResponse.containsAttribute("M_GetCompetenza_singola")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=CurrCompMainPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  if(!nuovo) {
  //Sono in modalità dettaglio
    Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_GetCompetenza_singola.ROWS.ROW");
      if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
        
        SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);
        
        prgCompetenza     = (BigDecimal) beanLastInsert.getAttribute("PRGCOMPETENZA");
        strDescrizione    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
        codTipoCompetenza = StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "tipocompetenza");
        codCompetenza     = StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "codCompetenza");
        strNote           = StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "NOTE");
        cdnUtIns          = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
        dtmIns            = beanLastInsert.getAttribute("DTMINS");
        cdnUtMod          = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
        dtmMod            = beanLastInsert.getAttribute("DTMMOD");
      }
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>
<html>

<head>
  <title>Competenze</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
      window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
  </SCRIPT>

  <SCRIPT TYPE="text/javascript">

<!--
var flagChanged = false;
//dichiaro gli array per riempire la mia supercombo
 var comp_tipo=new Array();
 var comp_cod=new Array();
 var comp_des=new Array();
<%     for(int i=0; i<tipiCompetenzeRows.size(); i++)  { 
            row_tipiCompetenze = (SourceBean) tipiCompetenzeRows.elementAt(i);
            out.print("comp_tipo["+i+"]=\""+ row_tipiCompetenze.getAttribute("TIPO").toString()+"\";\n");
            out.print("comp_cod["+i+"]=\""+ row_tipiCompetenze.getAttribute("CODICE").toString()+"\";\n");
            out.print("comp_des["+i+"]=\""+ row_tipiCompetenze.getAttribute("DESCRIZIONE").toString()+"\";\n");              
      } 
%>

function caricaCompetenze(codTipoCompetenza, modNuovo) {
   i=0;
   j=0;
   maxcombo=15;
   
   while (document.Frm1.codCompetenza.options.length>0) {
        document.Frm1.codCompetenza.options[0]=null;
    }

      for (i=0; i<comp_tipo.length ; i++) {
       if (comp_tipo[i]==codTipoCompetenza) {
          document.Frm1.codCompetenza.options[j]=new Option(comp_des[i], comp_cod[i], false, false);
           j++;
       }
     }
  if (modNuovo) {
    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    document.Frm1.codCompetenza.size=j;
  }
}


function Select(prgCompetenza) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CurrCompMainPage";
    s += "&MODULE=M_GetCompetenza_singola";
    s += "&PRGCOMPETENZA=" + prgCompetenza;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);
  }

 function Delete(prgCompetenza, tipo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere La competenza:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=CurrCompMainPage";
      s+= "&MODULE=M_DelCompetenza";
      s += "&PRGCOMPETENZA=" + prgCompetenza;
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
    
function Update()
  {
    if (controllaFunzTL()) {
      document.Frm1.MODULE.value = "M_UpdateConoscenzaLing";
      
      doFormSubmit(document.Frm1);
    }
  }
      
function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.Frm1;
   }
-->
</SCRIPT>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </script>
</head>

<body class="gestione">

  <%
    
    Linguette l = new Linguette( user,  _funzione, "CurrCompMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  %>
    
    <af:form method="POST" action="AdapterHTTP" name="Frm1">
      
      <input type="hidden" name="PAGE" value="CurrCompMainPage">
      <input type="hidden" name="MODULE" value="M_InsertConoscenzaLing"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="CDNUTINS" value="<%= strCodiceUtenteCorrente %>"/>
      <input type="hidden" name="CDNUTMOD" value="<%= strCodiceUtenteCorrente %>"/>
      
      <% if (!nuovo)  {%>
        <input type="hidden" name="PRGCOMPETENZA" value="<%=prgCompetenza%>"/>
      <% } %>
      
      <p class="titolo">Competenze Inserite</p>
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertConoscenzaLing"/>
          <af:showMessages prefix="M_DelCompetenza"/>
          <af:showMessages prefix="M_UpdateConoscenzaLing"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_GETLAVORATORECOMPETENZE" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova Competenza"/>        
          </p>
      <%}%>
      
        <div id="divLayerDett" name="divLayerDett" class="layerDett"
         style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
    
          <table width="100%">
            <tr width="100%">
              <td width="5%" class="menu"><img src="../../img/move.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
              <td>&nbsp;</td>
              <td width="5%" onClick="ChiudiDivLayer('divLayerDett')" class="menu"><img src="../../img/chiudi_menu.gif" alt="Chiudi"></td>
            </tr>
          </table>
          <%if(nuovo){%>
              <p class="titolo">Nuova Competenza</p>
          <%} else {%>
              <p class="titolo">Competenza</p>
          <%}%>
          <table class="main">
              <tr valign="top">
                <td class="etichetta">Tipo di competenza</td>
                <td class="campo">
                  <% if (nuovo) {%>
                    <af:comboBox title="Tipo di compentenza" name="codTipoCompetenza" moduleName="M_GETTIPICOMPETENZA" addBlank="true" onChange="javascript:caricaCompetenze(Frm1.codTipoCompetenza.value,true)" required="true" /> 
                  <%}
                      else {%>
                        <af:comboBox classNameBase="input" title="Tipo di competenza" name="codTipoCompetenza" moduleName="M_GETTIPICOMPETENZA" addBlank="true" onChange="javascript:caricaCompetenze(Frm1.codTipoCompetenza.value, false);fieldChanged();" selectedValue="<%=codTipoCompetenza%>" required="true" disabled="<%= String.valueOf(!canModify) %>"  /> 
                      <%}%>
                </td>
              </tr>
              <tr>
                <td>
                  <br/>
                </td>
              </tr>
              <tr valign="top">
                 <td class="etichetta">Competenza</td>
                 <td class="campo">
                   <% if (nuovo) {%>
                     <af:comboBox multiple="true" title="Competenza" name="codCompetenza" required="true" disabled="<%= String.valueOf(!canModify) %>">
                     </af:comboBox>
                   <%}
                      else {%>
                        <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Tipo di competenza" name="codCompetenza" required="true" disabled="<%= String.valueOf(!canModify) %>" >
                        <option value=""></option>
                        <%String selected="";
                            for(int i=0; i<tipiCompetenzeRows.size(); i++)  { 
                                  row_tipiCompetenze = (SourceBean) tipiCompetenzeRows.elementAt(i);
                                  selected=row_tipiCompetenze.getAttribute("CODICE").toString().equals(codCompetenza)?"selected=\"true\"":"";
                                  if (row_tipiCompetenze.getAttribute("TIPO").equals(codTipoCompetenza)) {
                                       out.print("<option value=\""+ row_tipiCompetenze.getAttribute("CODICE").toString()+"\" "+selected+" >");
                                       out.print(row_tipiCompetenze.getAttribute("DESCRIZIONE").toString()+"</option>");              
                                  }
                            }
                         %>              
                        </af:comboBox>
                      <%}%>
                  </td>
              </tr>
              <tr>
                <td>
                  <br/>
                </td>
              </tr>
               <tr valign="top">
                 <td class="etichetta">Note</td>
                 <td class="campo">
                   <af:textArea name="strNote" cols="25" value="<%=strNote%>" readonly="<%= String.valueOf(!canModify) %>"/>
                 </td>
               </tr>
          </table>
          <!-- ************** -->
          <td colspan="2">
                      <table class="main" width="100%">
                        <tr><td>&nbsp;</td></tr>
                            <%if(nuovo) {%>
                              <tr>
                                <td colspan="2" align="center">
                                <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
                                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
                                </td>
                              </tr>
                           <%}
                           else {%>
                              <td width="40%" align="right">
                            <%if (canModify) {
                           %>    
                              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
                              </td>
                          <%}
                          }%>
                          <td width="60%" align="left"> 
                           <%if((canModify) && (!nuovo)){%>
                                <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
                                       onclick="openPage('CurrCompMainPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_funzione%>');">
                           <%}
                              else {
                                    if(!canModify) {%>
                                      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                
                                  <%}
                                }%>
                          </td>        
                  </tr>
      </div>
    </TABLE>
  </af:form>
<%if (!nuovo) {%>                          
  <p align="center">
  <%operatoreInfo.showHTML(out);%>
  </p>
<%}%>
</body>
</html>
