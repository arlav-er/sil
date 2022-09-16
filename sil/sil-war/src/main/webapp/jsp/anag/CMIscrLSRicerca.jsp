<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
 
 String datIscrAlboDa = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrAlboDa");
 String datIscrAlboA = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrAlboA");
 String datIscrListProvDa = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrListProvDa");
 String datIscrListProvA = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrListProvA");
 String tipoListaSpec = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoListaSpec");
 String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codCpi");
  	
 String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");
 String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
 
%>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">
	      <% 
	     	//Genera il Javascript che si occuperà di inserire i links nel footer
	       // attributi.showHyperLinks(out, requestContainer,responseContainer,"");
	      %>
		
		function controllaDate() {
			var datIscrAlboDa = document.Frm1.datIscrAlboDa.value;
			var datIscrAlboA =  document.Frm1.datIscrAlboA.value;
			
			var datIscrListProvDa =  document.Frm1.datIscrListProvDa.value;
			var datIscrListProvA =  document.Frm1.datIscrListProvA.value;
				
			if(datIscrAlboDa != "" && datIscrAlboA != ""){
				if (compareDate(datIscrAlboDa, datIscrAlboA)>0) {
					alert("Data iscrizione all'albo nazionale dal maggiore della data iscrizione all'albo nazionale al");
					return false;
				}
			}
				
			if(datIscrListProvDa != "" && datIscrListProvA != ""){
				if (compareDate(datIscrListProvDa, datIscrListProvA)>0) {
					alert("Data iscrizione alla lista provinciale dal maggiore della data iscrizione alla lista provinciale al");
					return false;
				}
			}
			return true;
   		}
		  
		function aggiornaDescrizioni(){
			var descCPI = document.Frm1.descrCPIhid;
			var comboCPI = document.Frm1.codCpi;
			var descTipoLS = document.Frm1.descrTipoLShid;
			var comboTipoLS = document.Frm1.tipoListaSpec;
			
			if(comboCPI.value != ""){
				descCPI.value = comboCPI.options[comboCPI.selectedIndex].text;	
			}
			
			if(comboTipoLS.value != ""){
				descTipoLS.value = comboTipoLS.options[comboTipoLS.selectedIndex].text;
			}
			return true;
		}

		function getTxt(elem){
			
			var txtSelected = elem.options[elem.selectedIndex].text;
			var provinciaTerrit = document.Frm1.provinciaTerrit;
			provinciaTerrit.value = txtSelected;
		}
</script>	    
</head>
<body class="gestione" onload="rinfresca()">
<br/>
<p class="titolo">Ricerca Liste Speciali</p>
<br/>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1" onSubmit="controllaDate() && aggiornaDescrizioni()">
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
		<tr><td colspan="2"/><br/></td></tr>
	    <tr><td colspan="2"/>&nbsp;</td></tr>
	    <tr>
          <td class="etichetta">Ambito Territoriale</td>
          <td class="campo">
    	  	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" addBlank="true"  onChange="getTxt(this)" />
	        	<input type="hidden" name="provinciaTerrit" />
       	  </td>
       	</tr>
	    <tr>
	    	<td class="etichetta">Codice fiscale</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" />
	        </td>
	   </tr>
	   <tr>
	   		<td class="etichetta">Cognome</td>
	    	<td class="campo">
	    		<af:textBox type="text" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" />
	    	</td>
	    </tr>
	    <tr>
	    	<td class="etichetta">Nome</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strNome" value="<%=strNome%>" size="20" maxlength="50" />
	       </td>
	   	</tr>
	   	<tr>
	    	<td class="etichetta">tipo ricerca</td>
	        <td class="campo">
	        	<table colspacing="0" colpadding="0" border="0">
	          		<tr>
	          			<%if (tipoRicerca.equals("iniziaPer")) {%>
	           				<td>
	           					<input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;
	           				</td>
	           				<td>
	           					<input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per
	           				</td>
	          			<%} else {%>
	           				<td>
	           					<input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;
	           				</td>
	           				<td>
	           					<input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per
	           				</td>
	          			<%}%>
	          		</tr>
	          </table>
	      </td>
	  </tr>	        
	  <tr><td class="etichetta">&nbsp;</td></tr>
	  <tr>
	  	   <td class="etichetta">Data iscrizione all'albo nazionale dal</td>
	       <td class="campo">
	          	<af:textBox validateOnPost="true" type="date" name="datIscrAlboDa" value="<%=datIscrAlboDa%>" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	          	al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="datIscrAlboA" value="<%=datIscrAlboA%>" size="10" maxlength="10" />
	       </td>
	  </tr>
	  <tr>
	  	   <td class="etichetta">Data iscrizione alla lista provinciale dal</td>
	       <td class="campo">
	          	<af:textBox validateOnPost="true" type="date" name="datIscrListProvDa" value="<%=datIscrListProvDa%>" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	          	al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="datIscrListProvA" value="<%=datIscrListProvA%>" size="10" maxlength="10" />
	       </td>
	  </tr>
	    <tr>
          <td class="etichetta">Tipo lista speciale</td>
          <td class="campo">
    	  	<af:comboBox classNameBase="input" title="Esito" name="tipoListaSpec" moduleName="M_TipoListSpec" 
    	  		addBlank="true" selectedValue="<%=tipoListaSpec%>" />
       	  	<input name="descrTipoLShid" type="hidden" value=""/>	
       	  </td>
       	</tr>
       	<tr><td colspan="2"><div class="sezione2"></div></td></tr>   
		<tr>
			<td class="etichetta">Centro per l'Impiego competente</td>
			<td class="campo">
				<af:comboBox classNameBase="input" title="Centro per l'Impiego competente" name="codCpi" 
					moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=codCpi%>" />
				<input name="descrCPIhid" type="hidden" value=""/>
			</td>
		</tr> 		
  		<tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Cerca" onClick=""/>
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" />				
	          </td>
	        </tr>
	        <input type="hidden" name="PAGE" value="CMIscrLSListaPage"/>
	        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	        
	      </table>
      </af:form>
      
	<%out.print(htmlStreamBottom);%>
	
	</body>
</html>

