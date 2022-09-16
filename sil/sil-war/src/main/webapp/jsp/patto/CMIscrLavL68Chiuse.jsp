<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.afExt.utils.*,                  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
	String datAnzianita68 = (String)serviceRequest.getAttribute("DATANZIANITA68");
	String dataOdierna = DateUtils.getNow();

	String codMonoTipoRagg    = "";
	String codCmTipoIscr      = "";
	String codTipoInvalidita  = "";
	String numPercInvalidita  = "";
	String codAccertSanitario = "";
	String datAccertSanitario = "";
	String prgVerbaleAcc      = "";
	
	SourceBean rowOldIscr = (SourceBean) serviceResponse.getAttribute("CM_GET_DETT_ISCR_CHIUSAL68.ROWS.ROW");
    if (rowOldIscr != null) {		
		codMonoTipoRagg = rowOldIscr.getAttribute("codMonoTipoRagg") == null? "" : (String)rowOldIscr.getAttribute("codMonoTipoRagg");
		codCmTipoIscr = rowOldIscr.getAttribute("CODCMTIPOISCR") == null? "" : (String)rowOldIscr.getAttribute("CODCMTIPOISCR");
		codTipoInvalidita = rowOldIscr.getAttribute("CODTIPOINVALIDITA") == null? "" : (String)rowOldIscr.getAttribute("CODTIPOINVALIDITA");
		numPercInvalidita = rowOldIscr.getAttribute("NUMPERCINVALIDITA") == null? "" : ((BigDecimal)rowOldIscr.getAttribute("NUMPERCINVALIDITA")).toString();
		codAccertSanitario = rowOldIscr.getAttribute("CODACCERTSANITARIO") == null? "" : (String)rowOldIscr.getAttribute("CODACCERTSANITARIO");
		datAccertSanitario = rowOldIscr.getAttribute("DATACCERTSANITARIO") == null? "" : (String)rowOldIscr.getAttribute("DATACCERTSANITARIO");
		prgVerbaleAcc = rowOldIscr.getAttribute("PRGVERBALEACC") == null? "" : ((BigDecimal)rowOldIscr.getAttribute("PRGVERBALEACC")).toString();
	}
    
    String _page = (String) serviceRequest.getAttribute("PAGE");
	String from_page = (String) serviceRequest.getAttribute("FROM_PAGE"); 
 	int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
 	
 	String module = StringUtils.getAttributeStrNotNull(serviceRequest,"MODULE");
 	
	boolean reload = false;
	String resultIscrL68 = (String)serviceResponse.getAttribute("CM_INSERT_ISCRL68.esito");
	if (resultIscrL68 != null && resultIscrL68.equals("true")) {
		reload = true;
	} 	

  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);	
		
%> 

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script>
	   
function rinfrescaOpener() {
	
	var reload = <%=reload%>;
	if (reload) {
		var url = "AdapterHTTP?PAGE=<%=from_page%>";
		url += "&CDNLAVORATORE=<%=cdnLavoratore%>";
		url += "&CDNFUNZIONE=<%=_cdnFunz%>";
		window.opener.location.replace(url);			
	} 
}
 		
</script>

</head>
<body class="gestione" onload="rinfresca();rinfrescaOpener();">

<%
    if(!module.equals("CM_INSERT_ISCRL68")) {   
%>

<af:list moduleName="CM_LOAD_ISCRCHIUSEL68"/>    

<table class="main">
<tr>
    <td align="left"><IMG src="../../img/add.gif"/>&nbsp;Scegliere l'iscrizione da utilizzare per reiscrivere il lavoratore.</td>
</tr>
<tr>
    <td>&nbsp;</td>
</tr>

<% 
	} else {
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="CM_INSERT_ISCRL68"/>
</font> 

<%
    	if(reload) {   
%>

<p class="titolo">Iscrizione Collocamento Mirato Legge 68/99</p>

<%= htmlStreamTop %>
<table class="main">
<tr>
    <td class="etichetta">Data ultima iscrizione/reiscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" title="Data ultima iscrizione/reiscrizione" 
    		name="DATULTIMAISCR" value="<%=dataOdierna%>" readonly="true" size="11" maxlength="10" />
    </td>
</tr>
<tr>
    <td class="etichetta">Tipo</td>
	<td colspan=3 class="campo">
		<af:comboBox name="codMonoTipoRagg" title="Tipo" classNameBase="input" disabled="true">					  
			<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipoRagg) )  { %>SELECTED="true"<% } %>></option>
		    <option value="A" <% if ( "A".equalsIgnoreCase(codMonoTipoRagg) ) { %>SELECTED="true"<% } %>>Altre categorie protette</option>
		    <option value="D" <% if ( "D".equalsIgnoreCase(codMonoTipoRagg) ) { %>SELECTED="true"<% } %>>Disabili</option>
	    </af:comboBox>        
	</td>
</tr>
<tr>
    <td class="etichetta">Categoria </td>
    <td colspan=3 class="campo">
    	<af:comboBox name="codCMTipoIscr" moduleName="CM_GET_DE_TIPO_ISCR" selectedValue="<%=codCmTipoIscr%>" 
    		classNameBase="input" title="Categoria iscrizione" disabled="true"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Data anzianità iscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="DATANZIANITA68" title="Data anzianità iscrizione" 
    		value="<%=datAnzianita68%>" readonly="true" size="11" maxlength="10" />
	</td>	
</tr>
<tr>
    <td class="etichetta">Tipo invalidità</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="CODTIPOINVALIDITA" moduleName="CM_GET_DE_TIPO_INVAL" selectedValue="<%=codTipoInvalidita%>"
        	classNameBase="input" title="Tipo invalidità" disabled="true"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Percentuale invalidità</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="NUMPERCINVALIDITA" title="Percentuale invalidità" 
    		value="<%=numPercInvalidita%>" readonly="true" size="4" maxlength="3" />%
    </td>
</tr>
<tr>
    <td class="etichetta">Data accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="DATACCERTSANITARIO" title="Data accertamento sanitario" 
    		value="<%=datAccertSanitario%>" readonly="true" size="11" maxlength="10" />
   	</td>
</tr>
<tr>
    <td class="etichetta">Tipo accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="CODACCERTSANITARIO" moduleName="CM_GET_DE_ACC_SANIT" selectedValue="<%=codAccertSanitario%>"
        	classNameBase="input" title="Tipo accertamento sanitario" disabled="true"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Verbale accertamento</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PRGVERBALEACC" moduleName="GET_VERB_ACC_CM_ISCR" selectedValue="<%=prgVerbaleAcc%>"
        	classNameBase="input" title="Verbale accertamento" disabled="true"/>
    </td>
</tr>

</table>

<% 
		}
	} 
%>

<table class="main">  
	<tr>
		<td>
  			<input type="button" class="pulsanti" name="Chiudi" value="Chiudi" onclick="window.close();"/>
		</td>
	</tr>
</table>

<%
    if(module.equals("CM_INSERT_ISCRL68")) {   
%>

<%= htmlStreamBottom %>        

<% 
	} 
%>

</body>
</html>
