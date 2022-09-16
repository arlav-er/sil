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
      String strCognome="";
      String strNome="";
      String codRuoloAz="";
      String strTelefono="";
      String strFax="";
      String strEmail="";
      Testata operatoreInfo   = null;
      boolean nuovo = true;

    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    String prgAzienda= (String)serviceRequest.getAttribute("PRGAZIENDA");
    String prgUnita= (String)serviceRequest.getAttribute("PRGUNITA");

    ProfileDataFilter filter = new ProfileDataFilter(user, "IdoReferentiPage");
    filter.setPrgAzienda(new BigDecimal(prgAzienda));
    filter.setPrgUnita(new BigDecimal(prgUnita));
  
    boolean canView=filter.canViewUnitaAzienda();
    if ( !canView ){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    }else{
      PageAttribs attributi = new PageAttribs(user, "IdoReferentiPage");
      boolean canModify = attributi.containsButton("INSERISCI");
      boolean canDelete = attributi.containsButton("CANCELLA");

      if ( !canModify && !canDelete ) {
      
      } else {
        boolean canEdit = filter.canEditUnitaAzienda();
        if ( !canEdit ) {
          canModify = false;
          canDelete = false;
        }
      }
    
    String fieldReadOnly;
    String btnChiudiDettaglio="";
    
    /*int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    String prgAzienda= (String)serviceRequest.getAttribute("PRGAZIENDA");
    String prgUnita= (String)serviceRequest.getAttribute("PRGUNITA");*/
    
     String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=IdoReferentiPage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
                     
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  BigDecimal prgReferenza=new BigDecimal(0);

    if(serviceResponse.containsAttribute("M_GetReferenza_singola")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

  if(!nuovo) {
  //Sono in modalità dettaglio
    Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_GetReferenza_singola.ROWS.ROW");
      if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
        
        SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);
        
        
        prgReferenza     = (BigDecimal) beanLastInsert.getAttribute("PRGAZREFERENTE");
        strCognome     =  StringUtils.getAttributeStrNotNull(beanLastInsert, "strCognome");
        strNome    = StringUtils.getAttributeStrNotNull(beanLastInsert, "strNome");
        codRuoloAz = StringUtils.getAttributeStrNotNull(beanLastInsert, "codRuoloAz");
        strTelefono     = StringUtils.getAttributeStrNotNull(beanLastInsert, "strTelefono");
        strFax           = StringUtils.getAttributeStrNotNull(beanLastInsert, "strFax");
        strEmail           = StringUtils.getAttributeStrNotNull(beanLastInsert, "strEmail");

      }
  }
  
  if (canModify) {
      btnChiudiDettaglio = "Chiudi senza aggiornare";
      fieldReadOnly="false";
    }
    else {
      btnChiudiDettaglio="Chiudi";
      fieldReadOnly="true";
    }
    
  operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>
<html>

<head>
  <title>Referenti</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  </SCRIPT>

  <SCRIPT TYPE="text/javascript">

<!--

var flagChanged = false;


function Select(prgAzReferente) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=IdoReferentiPage";
    s += "&MODULE=M_GetReferenza_singola";
    s += "&prgAzReferente=" + prgAzReferente;
    s += "&PRGAZIENDA=<%=prgAzienda%>";
    s += "&PRGUNITA=<%=prgUnita%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);
  }

 function Delete(prgAzReferente, tipo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere La Referenza:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=IdoReferentiPage";
      s+= "&MODULE=M_DelReferenza";
      s += "&prgAzReferente=" + prgAzReferente;
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.Frm1.MODULE.value = "M_InsertReferenza";
  if(controllaFunzTL())  {
    doFormSubmit(document.Frm1);
    }
  else
    return;
}
    
function Update()
  {
    if (controllaFunzTL()) {
      document.Frm1.MODULE.value = "M_SaveReferenza";
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
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
  </script>
</head>

<body class="gestione" onload="rinfresca()">


    
    <af:form method="POST" action="AdapterHTTP" name="Frm1">
      
      <input type="hidden" name="PAGE" value="IdoReferentiPage">
      <input type="hidden" name="MODULE" value="M_InsertReferenza"/>
      <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>   
      <input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
  
      
      <% if (!nuovo)  {%>
        <input type="hidden" name="PRGAZREFERENTE" value="<%=prgReferenza%>"/>
      <% } %>
      
      <p class="titolo">Referenti Inseriti</p>
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertReferenza"/>
          <af:showMessages prefix="M_DelReferenza"/>
          <af:showMessages prefix="M_SaveReferenza"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_GetReferenti" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Referente"/>   
          
          </p>
      <%}%>
        <%
		  String divStreamTop = StyleUtils.roundLayerTop(canModify);
		  String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
		%>
    <p align="center">
       <input type="button" class="pulsanti" onClick="openPage('IdoUnitaAziendaPage','&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>&cdnfunzione=<%=_funzione%>');" value="Torna alla scheda unità aziendale"/>         
    </p>    
        <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
         style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
         <%out.print(divStreamTop);%>
    	  <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuovo referente
            <%} else {%>
              Referente
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
          <table width="100%">
           <tr valign="campo">
              <td class="etichetta">Cognome</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strCognome" title="Cognome" size="20" value="<%=strCognome%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="40" required="true" />
              </td>
            </tr>

            <tr valign="campo">
              <td class="etichetta">Nome</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNome" title="Nome" size="20" value="<%=strNome%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="40" />
              </td>
            </tr>
            
             <tr valign="campo">
                  <td class="etichetta">Ruolo</td>
                  <td class="campo">
                    <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codRuoloAz" title="Ruolo" selectedValue="<%=codRuoloAz%>" disabled="<%= String.valueOf(!canModify) %>" moduleName="M_GetRuoli" addBlank="true" required="true"/>
                  </td>
              </tr>

            <tr valign="campo">
              <td class="etichetta">Telefono</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strTelefono" title="Telefono" size="30" value="<%=strTelefono%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="50"  required="true"/>
              </td>
            </tr>

            <tr valign="campo">
              <td class="etichetta">Fax</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFax" title="Fax" size="20" value="<%=strFax%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" />
              </td>
            </tr>

            <tr valign="campo">
              <td class="etichetta">e-mail</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strEmail" title="e-mail" size="30" value="<%=strEmail%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="80" />
              </td>
            </tr>

          </table>
                
          <!-- ************** -->
          <table>
              <tr>
                 <td colspan="2">
                      <table class="main" width="100%">
                        <tr>
                           <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <%if(nuovo) {%>
                              
                                <td colspan="2" align="center">
                                <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
                                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
                                </td>                              
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
                                onclick="ChiudiDivLayer('divLayerDett');">
                           <%}
                              else {
                                    if(!canModify) {%>
                                      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                
                                  <%}
                                }%>
                          </td>
                        </tr>        
                     </td>
                  </tr>           
             </TABLE>
          </td>
         </tr>
      </table>
    <%out.print(divStreamBottom);%>    
  </af:form>
  </div>
<%if (!nuovo) {%>                          
  <p align="center">
  <%operatoreInfo.showHTML(out);%>
  </p>
<%}%>
</body>
</html>

<% } %>