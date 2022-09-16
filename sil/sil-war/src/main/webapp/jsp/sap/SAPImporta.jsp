<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.error.EMFErrorHandler" %>
<%@ page import="com.engiweb.framework.dispatching.module.AbstractModule" %>
<%@ page import="com.engiweb.framework.util.QueryExecutor" %>
<%@ page import="com.engiweb.framework.security.*" %>
<%@ page import="com.engiweb.framework.base.*" %>
<%@ page import="com.engiweb.framework.configuration.*" %>
<%@ page import="it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO"%>
<%@ page import="it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.*"%>
<%@ page import="it.eng.sil.pojo.yg.sap.*" %>
<%@ page import="it.eng.sil.util.*" %>
<%@ page import="it.eng.sil.security.ProfileDataFilter" %>
<%@ page import="it.eng.sil.security.PageAttribs" %>
<%@ page import="it.eng.sil.security.User" %>
<%@ page import="it.eng.sil.module.sap.InsertSAP" %>
<%@ page import="it.eng.afExt.utils.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.engiweb.framework.error.EMFAbstractError" %>
<%@ page import="com.engiweb.framework.error.EMFSAPError" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
  String cdnLavoratore = Utils.notNull(serviceRequest.getAttribute("CDNLAVORATORE"));
  String _current_page = (String) serviceRequest.getAttribute("PAGE");
  String idSap = (String) serviceRequest.getAttribute("idSap");
  Object codUtenteCorrente = sessionContainer.getAttribute("_CDUT_");  

  PageAttribs attributi = new PageAttribs(user, _current_page);

  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  boolean switchEnable = true; //usato per non stampare sezioni vuote

  SchedaAnagraficaProfessionaleDTO sapPortaleLav = (SchedaAnagraficaProfessionaleDTO) serviceResponse
      .getAttribute("M_SapCallVisualizzaSapPortale.SAPPORTALE");

  // quando entro in una pagina jsp salvo la scheda sap in sessione
  // in questo modo se non la trovo nella response del modulo
  if (sapPortaleLav != null) {
    sessionContainer.setAttribute("sapPortaleLav", sapPortaleLav);
  } else {
    // la leggo dalla sessione
    sapPortaleLav = (SchedaAnagraficaProfessionaleDTO) sessionContainer.getAttribute("sapPortaleLav"); 
  }

  // recupera il codice fiscale dalla lista
  SourceBean rows = (SourceBean) sessionContainer.getAttribute("ROWS");
  Vector vector = rows.getAttributeAsVector("ROW");
  String strCodiceFiscale = "";

  SourceBean row = (SourceBean) vector.get(0);
  strCodiceFiscale = (String) row.getAttribute("strCodiceFiscale");
  
  // calcola il sesso in base al cf
  String strSesso = "";
  try {
    final String MASCHIO = "M";
    final String FEMMINA = "F";
    
    String sGiorno = strCodiceFiscale.substring(9, 11);
    int iGiorno = Integer.parseInt(sGiorno);
    strSesso = iGiorno > 40 ? FEMMINA : MASCHIO;
  }
  catch(Exception e) {
    strSesso = "";
  }
  
  SourceBean beanAnagra = (SourceBean) serviceResponse.getAttribute("M_getLavoratoreAnag.ROWS.ROW");
  if (beanAnagra != null) {
	 cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(beanAnagra, "cdnLavoratore"); 
  }
  
  boolean erroriPresenti = false;
  EMFErrorHandler engErrorHandler = responseContainer.getErrorHandler();
  if (!engErrorHandler.isOK()) {
	  erroriPresenti = true;	  
  }

  boolean openMenuLav = false;
  String insertNewLav = "";
  if (serviceResponse.containsAttribute("M_INSERTSAPLAVORATOREANAGINDIRIZZI.operationResult")) { 
	  insertNewLav = (String) serviceResponse.getAttribute("M_INSERTSAPLAVORATOREANAGINDIRIZZI.operationResult");
	  if (insertNewLav.equalsIgnoreCase("SUCCESS")) {
	  	openMenuLav = true;
  	  }
  }
  
  //controllo configurazione per la cooperazione
  boolean coopAbilitata=(boolean) ((ConfigSingleton.getInstance()).getAttribute("COOP.ABILITATA")).equals("true");
  String operation = "insert_lav";
  if (!cdnLavoratore.equals(""))
	  operation = "update_lav";
  
%>

<html>
<head>
  <title>Visualizza SAP Portale</title>
  <link rel="stylesheet" type="text/css" media="print, screen" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/sap.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/sap.js"></SCRIPT>
</head>

<body class="gestione" onLoad="<%if (openMenuLav) {%>window.opener.caricaDettaglioLavDaOpener(<%=cdnLavoratore%>);<%}%>">

<af:form id="frmSAP" method="POST" action="AdapterHTTP">
<%if (!"".equals(insertNewLav) || erroriPresenti) {%>
	<table align="center" cellspacing="0" margin="0" cellpadding="0" border="0px" width="98%" noshade>
		<tr>
			<td align="left" valign="top" width="6" height="6" class="prof_ro"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
			<td class="prof_ro" align="center" valign="middle" cellpadding="2px"></td>
			<td class="prof_ro" align="right" valign="top" width="6" height="6"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
		</tr>
		<tr>
			<td colspan="3" class="silTitoloSezione" align="center" valign="middle" cellpadding="2px">Risultati Importazione</td>
		</tr>
		<tr>
			<td class="prof_ro" align="center" valign="middle" cellpadding="2px" colspan="3">
				<af:showMessages prefix="M_InsertSAPLavoratoreAnagIndirizzi"/>
				<af:showErrors/>
			</td>
		</tr>	
		<tr>
			<td class="prof_ro" align="left" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia4.gif"></td>
			<td class="prof_ro" height="6" align="center" valign="middle"&nbsp;</td>
			<td class="prof_ro" align="right" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia3.gif"></td>
		</tr>
	</table><br>
<%}%>

  <input type="hidden" name="PAGE" value="SapPortaleImportaPage">
  <input type="hidden" name="coopAbilitata" value="<%=coopAbilitata%>"/>
  <input type="hidden" name="operation" value="<%=operation%>"/>
  <input type="hidden" name="manage" value="allFrm"/>
  <%if (operation.equals("update_lav")) {%>
  	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  <%}%>
  <input type="hidden" name="cdnUtIns" value="<%=codUtenteCorrente%>"/>
  <input type="hidden" name="cdnUtMod" value="<%=codUtenteCorrente%>"/>

<%
  if (sapPortaleLav != null) {
%>
  <!-- nuovo -->
<table width="100%">
    <tr width="100%">
      <td class="sapTitolo" style="width: 49%">Scheda Anagrafica Portale</td>
      <td bgcolor="white" style="width: 2%">
      <td class="silTitolo" style="width: 49%">Scheda Anagrafica SIL</td>
    </tr>
  
  	<tr width="100%">
    <td align="right" class="sapImporta" colspan="1" style="width: 49%">
        <input id="chkAnagra" type="checkbox" onClick="selectAllCheck('chkAnagra','');"/>
        Seleziona/Deseleziona tutti&nbsp;&nbsp;
        <input type="image" src="../../img/avanti.gif" alt="Submit" onClick="return validateAllSections()"/>
        Importa selezionati
    </td>
    <td bgcolor="white" style="width: 2%">
    <td valign="top" class="silTitoloSezione" colspan="1" style="width: 49%"> &nbsp; </td>
  </tr>

  <tr width="100%">
    <td bgcolor="#ffdead" class="etichetta, grassetto, indenta" valign=top style="width: 49%"> <font id="errImporta" color="red">&nbsp;</font> </td>
    <td bgcolor="white" style="width: 2%">
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> &nbsp; </td>
  </tr>  

</table>    
<table width="100%"> 
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%;"> <%@include file="SAPImpAnagraSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%"> &nbsp; </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpAnagraSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpTitStuSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapTitoloStudioList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmTitStu', 'errTitStu', NOT_NULL_TIT_STU)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpTitStuSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpForProSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapFormazioneList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmForPro', 'errForPro', NOT_NULL_FOR_PRO)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpForProSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpLingueSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapLinguaList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmLingue', 'errLingue', NOT_NULL_LINGUE)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpLingueSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpConInfSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapConoscenzeInfoList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmConInf', 'errConInf', NOT_NULL_CON_INF)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpConInfSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpAbilitaSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if ((sapPortaleLav.getSapPatenteList() != null || sapPortaleLav.getSapPatentinoList() != null || sapPortaleLav.getSapAlboList() != null) && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmAbilita', 'errAbilita', NOT_01_PATENTI)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpAbilitaSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpEspLavSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapEsperienzaLavList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmEspLav', 'errEspLav', NOT_NULL_ESP_LAV)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpEspLavSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpPropenSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%">
    <%if (sapPortaleLav.getSapPropensioneList() != null && beanAnagra != null) {%>
    	<input type="image" src="../../img/avanti.gif" onClick="return validateSection('frmPropen', 'errPropen', NOT_NULL_PROPEN)">
    <%}%>
    </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpPropenSIL.jsp" %> </td>
  </tr>
  <tr width="100%">
    <td bgcolor="#ffdead" valign=top style="width: 49%"> <%@include file="SAPImpAltroSAP.jsp" %> </td>
    <td bgcolor="white" style="width: 2%"> &nbsp; </td>
    <td bgcolor="#e8f3ff" valign=top style="width: 49%"> <%@include file="SAPImpAltroSIL.jsp" %> </td>
  </tr>
  
  <tr width="100%">
	<td align="right" class="sapImporta" colspan="1" style="width: 49%">
        <input id="chkAnagra2" type="checkbox" onClick="selectAllCheck('chkAnagra2','');"/>
        Seleziona/Deseleziona tutti&nbsp;&nbsp;
        <input type="image" src="../../img/avanti.gif" alt="Submit" onClick="return validateAllSections()"/>
        Importa selezionati
    </td>
    <td bgcolor="white" style="width: 2%">
    <td valign=top class="silTitoloSezione" colspan="1" style="width: 49%"> &nbsp; </td>
  </tr>
</table>
  <%
    } else {
%>
  <h4 color=red>Non &egrave; stato possibile recuperare i dati dal WS MySAP.</h4>
<%   
    }
  %>

  <center>
    <input style="display: ''" class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="window.close()" /> 
  </center>

</af:form>
  
</body>
</html>

