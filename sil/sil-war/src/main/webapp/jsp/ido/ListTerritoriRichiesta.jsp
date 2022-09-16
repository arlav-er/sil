<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>

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

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	Object codiceUtente = sessionContainer.getAttribute("_CDUT_");

	String prgComune = "";
	String codComune = "";
	String strDenominazioneComune = "";
	String cdnUtins="";
	String dtmins="";
	String cdnUtmod="";
	String dtmmod="";
	Testata operatoreInfo=null;

	String flgInvioCL = "";
	boolean nuovo = true;
	if(serviceResponse.containsAttribute("M_SelectComuneRichiesta")) { 
		nuovo = false; 
	} else { 
		nuovo = true;
	}
	if(!nuovo) {
		SourceBean row = (SourceBean)serviceResponse.getAttribute("M_SelectComuneRichiesta.ROWS.ROW");
		if (row != null) {     
			prgComune = (String) row.getAttribute("PRGCOMUNE").toString();
			codComune = (String) row.getAttribute("CODCOM").toString();
			strDenominazioneComune = (String) row.getAttribute("STRDENOMINAZIONE");
		  	flgInvioCL = StringUtils.getAttributeStrNotNull(row, "flgInvioCL");
		  	cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
		  	dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
		  	cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
		  	dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
		  	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
		}
	}
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  String cdnStatoRich = "";
  SourceBean rigaTestata = null;
  boolean canInsert = false;
  boolean canModify = false;
  boolean canDelete = false;
  String conf_ClicLav = serviceResponse.containsAttribute("M_GetConfigClicLav.ROWS.ROW.NUM")?
		  serviceResponse.getAttribute("M_GetConfigClicLav.ROWS.ROW.NUM").toString():"0";
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
  String moduleNameProv="MInserisciProvinciaRichiesta";
  String moduleNameCom="MInserisciComuneRichiesta";
  if (!nuovo) {
	  moduleNameCom="M_UpdateComuneRichiesta";
  }
  String _page = (String) serviceRequest.getAttribute("PAGE"); 

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

  if ( !canModify && !canDelete ) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
        canModify = false;
        canDelete = false;
      }
    }
    
  SourceBean comuniRichiestaRows=(SourceBean) serviceResponse.getAttribute("MListaTerritoriComuniRichiesta");
  SourceBean provRichiestaRows=(SourceBean) serviceResponse.getAttribute("MListaTerritoriProvinceRichiesta");
  SourceBean riga = null;
  
  BigDecimal prgProvincia = null;
  String strRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  BigDecimal prgRichiestaAz = new BigDecimal(strRichiestaAz);
  
  String codProvincia="";
  String strDescrizione="";
  String strDescrizioneVisualizza="";
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(strRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu, strPrgUnitaMenu, strRichiestaAz);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  /*boolean nuovo = true;*/
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
 
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  String url_nuovo = "AdapterHTTP?PAGE=GestTerritoriRichiestaPage" + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz;
%>  
<%@ include file="_infCorrentiAzienda.inc" %> 

<script type="text/javascript">
  var flagChanged = false;
  function ControllaCampi(strSwitch) {
    if (strSwitch == 'comune') {
      if (document.Frm1.CODCOMHid.value == '') {
        alert("Inserire comune");
        return false;
      }
      else {
          return true;
      }
    }
    else {
      if (document.frmProvinciaRichiesta.CODPROVINCIA.value == '') {
        alert("Inserire provincia");
        return false;
      }
      else {
        return true;
      }
    }
  }


function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	
	if(comboValue == scelta){
		alert("Scelta non valida");
		combo[0].selected=true;	
	}
  }

	function ComuneSelect(prgComune) {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
	    if (isInSubmit()) return;
	
	    var s= "AdapterHTTP?PAGE=GestTerritoriRichiestaPage";
	    s += "&DETTAGLIO=True";
	    s += "&APRIDIV=1";
	    s += "&PRGCOMUNE=" + prgComune;
	    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	    s += "&CDNFUNZIONE=<%= _funzione %>";
	    setWindowLocation(s);
	}

  function ComuneDelete(prgKey, strDescrizione, prgRichiestaAz) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var strCaption="";
    strCaption="Eliminare il comune \n" 
    + strDescrizione + " ?" ;
    if ( confirm(strCaption ) ) {
      var s= "AdapterHTTP?PAGE=GestTerritoriRichiestaPage";
      s += "&MODULE=MDeleteComuneRichiesta";
      s += "&PRGCOMUNE=" + prgKey;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=<%=_funzione%>";
      setWindowLocation(s);
      }
  }
  
  function ProvinciaDelete(prgKey, strDescrizione, prgRichiestaAz) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    var strCaption="";
    strCaption="Eliminare la provincia \n"
    + strDescrizione + " ?" ;
    if ( confirm(strCaption ) ) {
      var s= "AdapterHTTP?PAGE=GestTerritoriRichiestaPage";
      s += "&MODULE=MDeleteProvinciaRichiesta";
      s += "&PRGPROVINCIA=" + prgKey;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=<%=_funzione%>";
      setWindowLocation(s);
    }
  }

  function settaFlag() {
  	if(document.Frm1.INVIA_CLIC_LAVORO.checked) {
		document.Frm1.FLGINVIOCL.value="S";
	} else {
		document.Frm1.FLGINVIOCL.value="N";
	}
  }


</script>


<html>
<head>
  <title>Territori Richiesta</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <script language="JavaScript" src="../../js/layers.js"></script> 
  <af:linkScript path="../../js/"/>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
  </script>
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
  linguette.show(out);
%>

  <center>
    <af:showErrors/>
    <af:showMessages prefix="MDeleteComuneRichiesta"/>
    <af:showMessages prefix="MDeleteProvinciaRichiesta"/>
    <af:showMessages prefix="MInserisciComuneRichiesta"/>
    <af:showMessages prefix="MInserisciProvinciaRichiesta"/>
  </center>

  <af:list moduleName="MListaTerritoriComuniRichiesta" skipNavigationButton="1"
  		   canInsert="<%=canInsert ? \"1\" : \"0\"%>"
           canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
           jsSelect="ComuneSelect"
           jsDelete="ComuneDelete"
           configProviderClass="it.eng.sil.module.ido.IdoTerrComuniListConfig"/>

  <af:list moduleName="MListaTerritoriProvinceRichiesta" skipNavigationButton="1"
           canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
           jsDelete="ProvinciaDelete"/>

  <%
  if((!nuovo && canModify) || (nuovo && canInsert)) {
  %>
    <p align="center">
      <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Territorio"/>   
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
              Nuovo territorio
            <%} else {%>
              Territorio
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
        <% if (!nuovo) { %>
        <br><br>
        <% } %>
<table>
	<tr>
		<af:form name="Frm1" method="POST" action="AdapterHTTP"	onSubmit="ControllaCampi('comune')" 
					dontValidate="true">
			<input type="hidden" name="PAGE" value="GestTerritoriRichiestaPage" />
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
			<input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
			<input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
			<td class="etichetta" nowrap>Comune&nbsp;&nbsp;</td>
			<% if (nuovo) { %>
			<td class="campo" nowrap><af:textBox type="text" name="CODCOM"
				value="" size="4" maxlength="4" validateWithFunction="ToUpperCase"
				onKeyUp="javascript:PulisciRicerca(Frm1.CODCOM, Frm1.CODCOMHid, Frm1.STRCOMUNE, Frm1.STRCOMUNEHid, null, null, 'codice');" />&nbsp;
			<A 	HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOM, Frm1.STRCOMUNE, null,'codice','',null,'inserisciComTerrNonScaduto()');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per codice" /></a>&nbsp; <af:textBox type="hidden"
				name="CODCOMHid" value="" /> <af:textBox type="hidden"
				name="STRCOMUNEHid" value="" /> <af:textBox type="text"
				name="STRCOMUNE" value="" size="30" maxlength="50"
				onKeyUp="javascript:PulisciRicerca(Frm1.CODCOM, Frm1.CODCOMHid, Frm1.STRCOMUNE, Frm1.STRCOMUNEHid, null, null, 'descrizione');" />&nbsp;
			<A  HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOM, Frm1.STRCOMUNE, null, 'descrizione','',null,'inserisciComTerrNonScaduto()');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per descrizione" /></a>&nbsp;</td>
			<% } else { %>
			<td class="campo" nowrap>
			<af:textBox type="text" name="CODCOM"
				value="<%=codComune%>" size="4" maxlength="4" validateWithFunction="ToUpperCase" readonly="true" disabled="true" />&nbsp;
			<af:textBox type="hidden"
				name="CODCOMHid" value="<%=codComune%>" /> 
			<af:textBox type="hidden"
				name="STRCOMUNEHid" value="<%=strDenominazioneComune%>" />
			<af:textBox type="text"
				name="STRCOMUNE" value="<%=strDenominazioneComune%>" size="30" maxlength="50" readonly="true" disabled="true" />&nbsp;
			</td>
			<% } %>
	</tr>
	
	<tr>
		<%if (conf_ClicLav.equalsIgnoreCase("0")) {%>
		<td class="etichetta">Invia a Cliclavoro</td>
		<%}else {%>
			<td class="etichetta">Invia a Portale Regionale/Cliclavoro</td>
		<%}%>
		<td class="campo">
			<input type="checkbox" name="INVIA_CLIC_LAVORO" value="" <%=flgInvioCL.equals("S") ? "CHECKED" : ""%> onclick="settaFlag();">
		</td>
		<input type="hidden" name="FLGINVIOCL" value="<%=flgInvioCL%>"/>
	</tr>
	<tr>
		<% if (nuovo) { %>
		<td colspan="2" align="center"><input class="pulsante" type="submit"
			name="inserisci" value="Inserisci">
		<% } else { %>
		<input type="hidden" name="prgcomune" value="<%=prgComune%>"/>
		<td colspan="2" align="center"><input class="pulsante" type="submit"
			name="salva" value="Salva">
		<% } %>
		
		<% if (!nuovo) { %>
		&nbsp;
		<input class="pulsante" type="button"
			name="annulla" value="Chiudi"
			onClick="ChiudiDivLayer('divLayerDett')">
		<% } %>
		</td>
		
	</tr>
	<% if (nuovo) { %>
	<TR>
		<td colspan="2"><B>
		<hr>
		</B></td>
	</TR>
	<% } %>
	<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>" />
	<input type="hidden" name="MODULE" value="<%=moduleNameCom%>" />
	</af:form>
	<af:form name="frmProvinciaRichiesta" method="POST"
			action="AdapterHTTP" onSubmit="ControllaCampi('provincia')"
			dontValidate="true">
	<% if (nuovo) { %>
	<tr>
		
			<input type="hidden" name="PAGE" value="GestTerritoriRichiestaPage" />
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
			<td class="etichetta">Provincia&nbsp;&nbsp;</td>
			<td class="campo"><af:comboBox name="CODPROVINCIA" title="Provincia"
				required="true" multiple="false" size="1" addBlank="true"
				blankValue="" moduleName="M_GetIDOProvince" onChange="inibisciScelta(this,'NT')"/></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><input class="pulsante" type="submit"
			name="inserisci" value="Inserisci"></td>
		
	</tr>
		<TR>
			<td colspan="2"><B>
			<hr>
			</B></td>
		</TR>
	<% } %>
</table>
<% if (nuovo) { %>
<table>
	<tr>
		<td><input type="hidden" name="PRGRICHIESTAAZ"
			value="<%=prgRichiestaAz%>" /> <input type="hidden" name="MODULE"
			value="<%=moduleNameProv%>" /> <input class="pulsante" type="button"
			name="annulla" value="Chiudi"
			onClick="ChiudiDivLayer('divLayerDett')"> </td>
	</tr>
</table>
<% } %>
</af:form>
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
 <% } %>
</body>
</html>

