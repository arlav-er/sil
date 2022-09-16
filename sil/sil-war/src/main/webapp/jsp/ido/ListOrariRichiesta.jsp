<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
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

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	Object codiceUtente = sessionContainer.getAttribute("_CDUT_");

	String codOrario="";
	String prgOrario="";   
	String strDescrizioneOrario="";
	String strInserimento="";
	String cdnUtins="";
	String dtmins="";
	String cdnUtmod="";
	String dtmmod="";
	Testata operatoreInfo=null;

  String flgInvioCL = "";
  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_SelectOrarioRichiesta")) { 
     nuovo = false; 
  }
  else { 
    nuovo = true;
  }
  if(!nuovo) {
    SourceBean row = (SourceBean)serviceResponse.getAttribute("M_SelectOrarioRichiesta.ROWS.ROW");
    if (row != null) {     
      prgOrario = (String) row.getAttribute("PRGORARIO").toString();
      codOrario = (String) row.getAttribute("CODORARIO").toString();
      strDescrizioneOrario = (String) row.getAttribute("STRDESCRIZIONE");
      flgInvioCL = StringUtils.getAttributeStrNotNull(row, "flgInvioCL");
      cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
      dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
      cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
      dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
      operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);    
    }
  }
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { 
    apriDiv = "none"; 
  }
  else { 
    apriDiv = ""; 
  }
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  String cdnStatoRich = "";
  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String strRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  
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

    if ( !canInsert && !canModify && !canDelete ) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
    	canInsert = false;
        canModify = false;
        canDelete = false;
      }
    }
    
    if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
  
  String url_nuovo = "AdapterHTTP?PAGE=GestOrariRichiestaPage" + 
                     "&PRGRICHIESTAAZ=" + strRichiestaAz + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
  SourceBean riga = null;
  //BigDecimal prgOrario = null;
  BigDecimal prgRichiestaAz = new BigDecimal(strRichiestaAz);
  String moduleName="MInserisciOrarioRichiesta";
  if (!nuovo) {
	  moduleName = "M_UpdateOrarioRichiesta"; 
  }
  //String codOrario="";
  String strDescrizione="";
  String btnSalva="Inserisci";
  String btnAnnulla="";
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(strRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu,strPrgUnitaMenu,strRichiestaAz);
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>
<head>
<title>Orari Richiesta</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<script type="text/javascript">
  var flagChanged = false;
  function OrariDelete(prgOrario, strDescrizione) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    strDescrizione=strDescrizione.replace('\'', '´');
    if ( confirm("Eliminare orario richiesta " + strDescrizione + " ?") ) {
      document.frmMascheraOrarioRichiesta.PAGE.value = "GestOrariRichiestaPage";
      document.frmMascheraOrarioRichiesta.MODULE.value = "MDeleteOrarioRichiesta";
      document.frmMascheraOrarioRichiesta.PRGORARIO.value = prgOrario;
      doFormSubmit(document.frmMascheraOrarioRichiesta);
    }
  }

  function OrariSelect(prgOrario) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;

      var s= "AdapterHTTP?PAGE=GestOrariRichiestaPage";
      s += "&DETTAGLIO=True";
      s += "&APRIDIV=1";
      s += "&PRGORARIO=" + prgOrario;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&CDNFUNZIONE=<%= _funzione %>";
      setWindowLocation(s);
  }
  
  function settaFlag() {
  	if(document.frmMascheraOrarioRichiesta.INVIA_CLIC_LAVORO.checked) {
  		document.frmMascheraOrarioRichiesta.FLGINVIOCL.value="S";
  	} else {
  		document.frmMascheraOrarioRichiesta.FLGINVIOCL.value="N";
  	}
  }
  
  function controllaFlagCL() {
	    <% if(nuovo) { %>
	    var codSelezionati = 0;
	    if(document.frmMascheraOrarioRichiesta.FLGINVIOCL.value=="S") {
	         for (i=0;i<document.frmMascheraOrarioRichiesta.CODORARIO.length;i++) {
	         	if (document.frmMascheraOrarioRichiesta.CODORARIO[i].selected) {
	            	codSelezionati = codSelezionati + 1;
	             }
	         }    
	         if (codSelezionati > 1) {
	         	alert("E' possibile selezionare solo un codice orario da inviare a Cliclavoro"); 
	            return false;
	         }
	     }
	    <%}%>
	     return true;
	   }
  
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
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
<font color="red">
  <af:showErrors/>
</font>
  
<font color="green">
  <af:showMessages prefix="M_UpdateOrarioRichiesta"/>
  <af:showMessages prefix="MInserisciOrarioRichiesta"/>
  <af:showMessages prefix="MDeleteOrarioRichiesta"/>
</font>

<af:form method="POST" action="AdapterHTTP" name="frmMascheraOrarioRichiesta" onSubmit="controllaFlagCL()">
<p align="center">
  <af:list moduleName="MListaOrariRichiesta" skipNavigationButton="1"
         canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
         canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
         jsSelect="OrariSelect"
         jsDelete="OrariDelete"/>
</p>
<%
if(canInsert) {
%>
  <p align="center">
    <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Orario"/>
  </p>
<%}%>

<input type="hidden" name="PAGE" value="GestOrariRichiestaPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>"/>
<input type="hidden" name="MODULE" value="<%=moduleName%>"/>
<input type="hidden" name="PRGORARIO" value="<%=prgOrario%>"/>
<input type="hidden" name="FLGINVIOCL" value="<%=flgInvioCL%>"/>
<input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
<input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>

<%
if ((canModify && !nuovo) || (canInsert && nuovo)) {
%>
	<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
		style="position: absolute; width: 80%; left: 50; top: 200px; z-index: 6; display: <%=apriDiv%>;">
<%
  String divStreamTop = StyleUtils.roundLayerTop(canModify);
  String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
	<%out.print(divStreamTop);%>

	<table width="100%">
		<tr>
			<td width="16" height="16" class="azzurro_bianco"><img
				src="../../img/move_layer.gif" onClick="return false"
				onMouseDown="engager(event,'divLayerDett');return false"></td>
			<td height="16" class="azzurro_bianco"><%if (nuovo) {%> Nuovo orario
			<%} else {%> Orario <%}%></td>
			<td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')"
				class="azzurro_bianco"><img src="../../img/chiudi_layer.gif"
				alt="Chiudi"></td>
		</tr>
	</table>

	<br>
	<center>
	<table align="center">
		<tr>
			<td class="etichetta" align="center" nowrap>Orari</td>
			<% if (nuovo) { %>
			<td class="campo" align="center" nowrap><af:comboBox name="CODORARIO"
				title="Orario" required="true" multiple="true" size="6"
				moduleName="M_ListOrari" /></td>
			<% } else { %>
			<td class="campo" align="center" nowrap>
				<af:comboBox 	classNameBase="input"
								name="CODORARIO"
								title="Orario" 
								required="false" 
								multiple="false" 
								size="1" 
								selectedValue="<%=codOrario%>"
								moduleName="M_ListOrari" 
								disabled="true" /></td>
							
			<% } %>
		</tr>
		<tr>
			<td class="etichetta">Invia a Cliclavoro</td>
			<td class="campo">
				<input type="checkbox" name="INVIA_CLIC_LAVORO" value="" <%=flgInvioCL.equals("S") ? "CHECKED" : ""%>  onclick="settaFlag();">
			</td>
  		</tr>
	</table>
	</center>
	<br>
	<center>
	<table>
		<tr>
			<% if (!nuovo) { %>
			<td align="center"><input class="pulsante" type="submit" name="salva"
				value="Salva"></td>
			<% } else { %>
			<td align="center"><input class="pulsante" type="submit" name="inserisci"
				value="Inserisci"></td>
			<% } %>
			<td align="center"><input class="pulsante" type="button"
				name="chiudi" value="Chiudi"
				onClick="ChiudiDivLayer('divLayerDett');"></td>
		</tr>
	</table>
	</center>
	<br>
	<% if (!nuovo) {%>                          
    <table>
    <tr><td colspan="4" align="center">   
      <p align="center">
        <% operatoreInfo.showHTML(out); %>
      </p>
    </td></tr>
    </table>
  <%}%> 
	</div>
<%out.print(divStreamBottom);%> 
	<%
}
%>
</af:form>
</body>
</html>

<% } %>