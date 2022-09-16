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

<script type="text/javascript">
  
  function ContrattiDelete(prgContratto, prgRichiestaAz,funzione) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    if ( confirm("Confermi operazione" + " ?") ) {
      var s= "AdapterHTTP?PAGE=IdoContrattiPage";
      s += "&CANCELLA=Y";
      s += "&PRGCONTRATTO=" + prgContratto;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + funzione;
      setWindowLocation(s);
    }
  }

</script>
<%
  Vector vectListaContratti = serviceResponse.getAttributeAsVector("M_GETCONTRATTIRICHIESTA.ROWS.ROW");
  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  boolean canModify= false;
  boolean canDelete= false;
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();

  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda(prgAzienda);
  filter.setPrgUnita(prgUnita);
  
  boolean canView=filter.canViewUnitaAzienda();
  if ( !canView ){
    response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
    PageAttribs attributi = new PageAttribs(user, _page);
    if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
      canModify= attributi.containsButton("INSERISCI");
      canDelete= attributi.containsButton("CANCELLA");
    }

    if ( !canModify && !canDelete ) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
        canModify = false;
        canDelete = false;
      }
    }
    
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  PageAttribs attributiProfilo = new PageAttribs(user, "IdoEtaEsperienzaPage");
  boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");
  
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
        boolean nuovo = true;
  // NOTE: Attributi della pagina (pulsanti e link) 

   String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");

  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=IdoContrattiPage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz +                      
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>


 <title>Conoscenze Informatiche</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  </SCRIPT>

  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
  </SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
</script>
 <SCRIPT TYPE="text/javascript">

 function Delete(PRGCONTRATTO, tipo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere la tipologia rapporto:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=IdoContrattiPage";
      s+= "&MODULE=M_DeleteContrattoRichiesta";
      s += "&PRGCONTRATTO=" + PRGCONTRATTO;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.MainForm.MODULE.value = "M_SaveContrattiRichiesta";
  if(controllaFunzTL())  {
    doFormSubmit(document.MainForm);
    }
  else
    return;
}
    

      
function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.MainForm;
   }
   
</SCRIPT>

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
<p align="center">
  <af:form method="POST" action="AdapterHTTP" name="MainForm" onSubmit=""> 
  <input type="hidden" name="PAGE" value="<%= _page %>"/>
  <input type="hidden" name="MODULE" value="M_SaveContrattiRichiesta"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
  <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
  <center>
  <font color="red">
    <af:showErrors/>
  </font>
  </center>
  <center>
  <font color="green">
    <af:showMessages prefix="M_SaveContrattiRichiesta"/>
    <af:showMessages prefix="M_DeleteContrattoRichiesta"/>
  </font>
  </center>

   <p align="center">
          <af:list moduleName="M_GetContrattiRichiesta" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <% if(canModify) {%>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova tipologia di rapporto"/>
          </p>
      <%}%>
   
       <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
       style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
	
	<% 
	   String divStreamTop = StyleUtils.roundLayerTop(canModify);
  	   String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
	%>
         <%out.print(divStreamTop);%>

        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuova contratto
            <%} else {%>
              Contratto
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
        <br>
        <table align="center" width="100%">
        <tr>
        <td class="etichetta" nowrap>Profilo n.&nbsp;</td>
        <td class="campo" nowrap><INPUT type="text" name="prgAlternativa" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
        </tr>
        <tr>
        <td class="etichetta" nowrap>Contratti</td>
        <td class="campo" nowrap>
        <af:comboBox name="CODCONTRATTO"
                         title="Contratti"
                         multiple="true" required="true"
                         size="5"
                         moduleName="M_ListContrattiRichiesta"/>
        </td>
        </tr>
        
	</table>
    <br/>
   <center>
   <table><tr><td align="center">
   	<input class="pulsante" type="submit" name="salva" value="Inserisci">
    <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
    </td></tr>
   </table>
   </center>
   </div>    
   <%out.print(divStreamBottom);%>   
</af:form>      
<% } %>
</body>
</html>

