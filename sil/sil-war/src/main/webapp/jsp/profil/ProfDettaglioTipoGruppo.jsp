<%@page import="java.text.DateFormat"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/controlliProfili.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>
 
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	boolean canView = filter.canView();
	if ( !canView ){	
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	BigDecimal cdntipogruppo = (BigDecimal)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.CDNTIPOGRUPPO");
	// Recupero l'eventuale URL generato dalla LISTA precedente (se ce ne è una)
	String token = "_TOKEN_" + "ProfListaTipoGruppoPage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	
	boolean isNew  = serviceRequest.containsAttribute("nuovo_tipo_gruppo");
	boolean errorInsTipoGruppo = false;
	boolean inserimentoNuovo = ( cdntipogruppo != null );
	String title = 	isNew?"Nuovo tipo gruppo":"Dettaglio tipo gruppo";
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	String cdnUtins="";
	String dtmins="";
	String cdnUtmod="";
	String dtmmod="";
	Testata operatoreInfo = null;
	
%>

	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><%=title%></title>	
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<af:linkScript path="../../js/" />
 	<script type="text/javascript">
 	
		
		
		function goBack() {
			if (isInSubmit()) return;
	      	backLocation = "AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>";
	      	setWindowLocation(backLocation);
	    }

		function checkAmbitoGruppo() {
			if (document.form1.CODTIPO.value == 'S') {
				if (document.form1.strCodiceFiscale.value == '') {
					alert("Il campo Codice Fiscale è obbligatorio");
	  				return false;
				}
			}
			return true;
		}
		
	</script>
</head>
<%

	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canInsert = attributi.containsButton("INSERISCI");	
	String ambitoId = "";
	String strCodiceFiscale = "";
	String flagStandard = "N"; // questo è il valore che vogliamo nel caso in cui siamo in inserimento (non modifica)	
	String tipo_gruppo_name = ""; // questo è il valore che vogliamo nel caso in cui siamo in inserimento (non modifica)	
	String notes = "";
	
	
	if (cdntipogruppo != null ){
		tipo_gruppo_name = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.STRDENOMINAZIONE");
		ambitoId = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.CODTIPO");
		strCodiceFiscale = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.STRCODICEFISCALE");
		flagStandard = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.FLGSTANDARD");
		notes = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.NOTE");
		cdnUtins = ((BigDecimal)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.cdnUtins")).toString();
		dtmins = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.dtmins");
		cdnUtmod = ((BigDecimal)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.cdnUtmod")).toString();
		dtmmod = (String)getServiceResponse(request).getAttribute("PROF_GET_SELECTED_TIPO_GRUPPO_MOD.ROWS.ROW.dtmmod");
		
		operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	}
	else {
		if (serviceResponse.containsAttribute("PROF_INSERT_NUOVO_TIPO_GRUPPO_MOD.INSERT_EROR")) {
			errorInsTipoGruppo = true;
			tipo_gruppo_name = StringUtils.getAttributeStrNotNull(serviceRequest, "STRDENOMINAZINE");
			ambitoId = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPO");
			strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
			flagStandard = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGSTANDARD");
			notes = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTA");	
		}
	}
	
	// fields read only, sebbene abbia lo stesso effetto di can modify, 
	// si distingue da canModify perchè si tratta di logica applicativa (non editabilità dei tipo gruppo Standard)
	// mentre canModify riguarda la profilatura
	boolean bFieldsReadOnly = canModify==false || "S".equals(flagStandard); 
	String fieldReadOnly = bFieldsReadOnly ? "true" : "false"; 
	

	
%>

<% 	
	
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify && !bFieldsReadOnly);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify && !bFieldsReadOnly);
	
	
	String  formSubmitURL =  "AdapterHTTP?PAGE=ProfDettaglioTipoGruppoPage";
	if ( cdntipogruppo==null )
		formSubmitURL = formSubmitURL + "&NOME_MODULO=PROF_INSERT_NUOVO_TIPO_GRUPPO_MOD";
	else
		formSubmitURL = formSubmitURL + "&cdntipogruppo=" + cdntipogruppo + "&NOME_MODULO=PROF_UPDATE_TIPO_GRUPPO_MOD";
%>
<body class="gestione" onload="rinfresca();abilitaCF();">
	
<p class="titolo"> <%=title%></p>

<af:showErrors/>
<af:showMessages prefix="PROF_UPDATE_TIPO_GRUPPO_MOD"/>
<af:showMessages prefix="PROF_INSERT_NUOVO_TIPO_GRUPPO_MOD"/>


<%out.print(htmlStreamTop);%>
	</br>
	</br>
		
	
	<af:form name="form1" action="<%=formSubmitURL%>" method="POST" onSubmit="checkAmbitoGruppo()">
	<%-- ________________________________________________ --%>	
	
	<%-- non devo metterlo qui: è già nell url di post
	<input type="hidden" name="cdntipogruppo" value="<%=cdntipogruppo%>"/>
	 --%>
	
	<af:textBox type="hidden" name="CDNUTENTE" value="<%= cdnUtCorrenteStr %>" />
	
	<table class="main" border="0">
 
   <tr><td colspan="2"/>&nbsp;</td></tr>
   <tr>
     <td class="etichetta">Nome del tipo gruppo</td>
     <td class="campo">
     	<%-- <%=template_name%> --%>
     	<af:textBox classNameBase="input" name="STRDENOMINAZINE" type="text" title="Nome del template" validateOnPost="false" value="<%=tipo_gruppo_name%>" size="50" maxlength="100" readonly="<%=fieldReadOnly%>" required="true"/>
     	 <%-- --%>
     </td>
   </tr>
   
   <tr>
     <td class="etichetta">Ambito</td>
     <td class="campo"> 
     	<%-- questo campo è abilitato in fase di inserimento nuovo tipo gruppo --%>
     	<%if (isNew || errorInsTipoGruppo) {%>   	
     		<af:comboBox classNameBase="input" name="CODTIPO" moduleName="PROF_GET_TS_TIPO_CMB_MOD_CONFIG" addBlank="true" required="true" title="Ambito" selectedValue="<%=ambitoId%>" onChange="abilitaCF()" />
     	<%} else {%>
     		<af:comboBox classNameBase="input" name="CODTIPO" disabled="true" moduleName="PROF_GET_TS_TIPO_CMB_MOD" addBlank="true" required="true" title="Ambito" selectedValue="<%=ambitoId%>" />
     	<%}%>
     </td>
   </tr>
   
   <tr>
     <td class="etichetta">Standard</td>
     <td class="campo">
     	<%-- questo non è mai abilitato, è sempre read only, sia in inserimento che in editing ( in inserimento ha sempre valre 'NO')  --%>
		<af:comboBox name="FLGSTANDARD" classNameBase="input" addBlank="false" required="false" title="Standard"  disabled="true"> 
		        <OPTION value=""></OPTION>
		        <OPTION value="S" <%if (flagStandard != null && flagStandard.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
		        <OPTION value="N" <%if (flagStandard != null && flagStandard.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
		    </af:comboBox>
		</td>
   </tr>
   
   <tr>
   	<td id="cfFieldsLbl" style="display:none" class="etichetta">Codice fiscale</td>
    <td id="cfFieldsCampo" style="display:none" class="campo">
     	<af:textBox name="strCodiceFiscale"  value="<%=Utils.notNull(strCodiceFiscale)%>" 
        title="Codice Fiscale" size="18" maxlength="16" 
        classNameBase="input" readonly="<%=fieldReadOnly%>" validateWithFunction="checkCodiceFiscale" />
    </td>
   </tr>
   	
   <tr>
     <td class="etichetta">Note</td>
     <td class="campo">
     	<%-- --%>
     	<af:textArea name="STRNOTA" classNameBase="input" readonly="<%=fieldReadOnly%>" value="<%=notes%>" maxlength="1000" required="false" validateOnPost="true" title="notes" />
     	<%-- --%>
     </td>
   </tr>
          
	   <tr>
	      <td colspan="2" align="center">
	      <br/>
	      <br/>
	      </td>
	   </tr>
       
	       
	   <tr>
	      <td colspan="2" align="center">
	      <% if ( cdntipogruppo == null ){%>
	      	<% if (canInsert) {%>
	      		<input class="pulsanti" type="submit" name="INSERISCI" value="Inserisci"/>
	      	<%} %>
	     <% }else{%>
	      	<% if (canModify && !bFieldsReadOnly) {%>	
	      		<input class="pulsanti" type="submit" name="AGGIORNA" value="Aggiorna"/>
	      	<%} %>
	      <% } %>
	      </td>
	   </tr>
	   
   
	   <tr> 
	   	<td colspan="2" align="center">	   
	   		<input class="pulsanti" type="submit" name="TORNA" value="Torna alla lista" onClick="goBack()"/>
	   	</td>
	   </tr>
    
    
    
	</table>
	</af:form>
	
	
<%out.print(htmlStreamBottom);

if (operatoreInfo != null) {%>
	<center>
  	<table>
  	<tr><td align="center">
	<% 
	operatoreInfo.showHTML(out);
	%>
	</td></tr>
  	</table>
  	</center>
<% } %>

</body>
</html>