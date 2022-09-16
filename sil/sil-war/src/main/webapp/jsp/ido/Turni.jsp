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
  boolean nuovo = true;
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { 
    apriDiv = "none"; 
  }
  else { 
    apriDiv = ""; 
  }
  boolean canModify= false;
  boolean canDelete= false;
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  String cdnStatoRich = "";
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
  
  String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

  // NOTE: Attributi della pagina (pulsanti e link) 
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
  
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu, strPrgUnitaMenu, prgRichiestaAz);
  String url_nuovo = "AdapterHTTP?PAGE=IdoTurniRichiestaPage" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>
<head>
  <title>Turni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
  
  function TurnoDelete(prgTurno, strDescrizione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    strDescrizione=strDescrizione.replace('\'', '´');    
    if ( confirm("Eliminare il turno " + strDescrizione + " ?") ) { 
      document.MainForm.PAGE.value = "IdoTurniRichiestaPage";
      document.MainForm.MODULE.value = "M_DeleteTurnoRichiesta";
      document.MainForm.PRGTURNO.value = prgTurno;
      doFormSubmit(document.MainForm);
    }
  }
  
  <% 
    //Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
  %>
  </SCRIPT>

</head>

<body class="gestione" onload="rinfresca()">
<center>
<%
infCorrentiAzienda.show(out); 
%>
</center>
<%linguette.show(out);%>

<font color="red">
  <af:showErrors/>
</font>

<center>
<font color="green">
  <af:showMessages prefix="M_SaveTurniRichiesta"/>
  <af:showMessages prefix="M_DeleteTurnoRichiesta"/>
</font>
</center> 

<af:form method="POST" action="AdapterHTTP" name="MainForm">  
<p align="center">
  <af:list moduleName="M_GetTurniRichiesta" skipNavigationButton="1"
         canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
         canInsert="<%=canModify ? \"1\" : \"0\"%>" 
         jsDelete="TurnoDelete"/>
</p>
<% 
if(canModify) {
%>
  <p align="center">
    <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Turno"/>
  </p>
<%}%>
  
<input type="hidden" name="PAGE" value="<%= _page %>"/>
<input type="hidden" name="MODULE" value="M_SaveTurniRichiesta"/>
<input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
<input type="hidden" name="PRGTURNO" value=""/>
<%
if (canModify) {
%>
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
              Nuovo turno
            <%} else {%>
              Turno
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
  <br>
  <center>
  <table align="center">
  <tr>
  <td align="center" class="etichetta">Turni</td>
  <td align="center" class="campo">
  <af:comboBox name="CODTURNO"
                   title="Turni"
                   multiple="true" required="true"
                   size="5"
                   moduleName="M_ListTurni"/>
  </td>
  </tr>
  </table>
  </center>
  <br>
  <center>
  <table>
  <tr><td align="center">
  <input class="pulsante" type="submit" name="salva" value="Inserisci">
  </td>
  <td align="center">
  <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">
  </td></tr>  
  </table>
  </center>
  <br>
  </div>
  <%out.print(divStreamBottom);%> 
<%
}
%>
</af:form>
</body>
</html>

<% } %>