<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css" />
<af:linkScript path="../../js/" />
<script language="Javascript">
	function avvisa(){
			if(document.Frm1.CF.value == "" 
				&& document.Frm1.cognome.value == "" 
				&& document.Frm1.nome.value==""
				&& document.Frm1.datinizioda.value==""
				&& document.Frm1.datinizioa.value==""
				&& document.Frm1.codCMTipoIscr.value==""
				<% if (Sottosistema.CM.isOn()) {%>
				&& document.Frm1.CODSTATOATTO.value=="" 
				<% }%>
				&& document.Frm1.codCMTipoInvalidita.value==""
				&& document.Frm1.numPercInvalildita.value==""
			){
				var msg = "ATTENZIONE!\r\n";
					msg+= "La ricerca potrebbe essere pesante e\r\n";
					msg+= "rallentare l'applicazione.\r\n\r\nVuoi proseguire?";
				return confirm(msg);
			}else{
				return true;
			}
	}
	
	function aggiornaDescrizioni(){
		var descCPI = document.Frm1.descrCPIhid;
		var comboCPI = document.Frm1.CodCPI;
		var descSTATOATTO = document.Frm1.descrSTATOATTOhid;
		var comboSTATOATTO = document.Frm1.CODSTATOATTO;
		var descTipoInvalidita = document.Frm1.descrTipoInvaliditahid;
		var comboTipoInvalidita = document.Frm1.codCMTipoInvalidita;
		var descTipoIscr = document.Frm1.descrTipoIscrhid;
		var comboTipoIscr = document.Frm1.codCMTipoIscr;
		
		if(comboCPI.value != ""){
			descCPI.value = comboCPI.options[comboCPI.selectedIndex].text;	
		}
		
		if(comboSTATOATTO.value != ""){
			descSTATOATTO.value = comboSTATOATTO.options[comboSTATOATTO.selectedIndex].text;
		}
		
		if(comboTipoInvalidita.value != ""){
			descTipoInvalidita.value = comboTipoInvalidita.options[comboTipoInvalidita.selectedIndex].text;
		}
		
		if(comboTipoIscr.value != ""){
			descTipoIscr.value = comboTipoIscr.options[comboTipoIscr.selectedIndex].text;
		}				
		return true;
	}
	
	function getTxt(elem){
		
		var txtSelected = elem.options[elem.selectedIndex].text;
		var provinciaTerrit = document.Frm1.provinciaTerrit;
		provinciaTerrit.value = txtSelected;
	}
	
<%@ include file="../documenti/RicercaCheck.inc" %>
</script>

</head>

<% boolean readOnlyStr = false;
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
   String paramCmIsc1 = null, required = "true";
   SourceBean paramSb = (SourceBean) serviceResponse.getAttribute("M_GetParamCmIsc1.ROWS.ROW");
   if (paramSb != null) {
   	  paramCmIsc1 = paramSb.getAttribute("PARAM_CM_ISC_1").toString();
   	  if (paramCmIsc1 != null && ("2").equals(paramCmIsc1)){
   		  required = "false";
   	  }
   }     
   String cf = StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
   String cognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
   String nome          = StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
   String tipoRicerca          = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
   String dataInizioDa        = StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
   String dataInizioA        = StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
   String tipoIscrizione      = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoIscr");
   String codStatoAtto      = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTO");
   String codCMTipoInvalidita      = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoInvalidita");
   String numPercInvalildita      = StringUtils.getAttributeStrNotNull(serviceRequest,"numPercInvalildita");
   String CodCPI      = StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
   String codGradoOcc      = StringUtils.getAttributeStrNotNull(serviceRequest,"codGradoOcc");
   String PROVINCIA_ISCR      = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");
   
   
   if ("".equals(codStatoAtto)) {codStatoAtto = "PR";};
   if ("".equals(tipoRicerca)) {tipoRicerca = "esatta";};
   
%>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Collocamento Mirato</p>
<p align="center">
	<af:form action="AdapterHTTP" method="POST" name="Frm1" onSubmit="avvisa() && aggiornaDescrizioni()">
	<%out.print(htmlStreamTop);%>
	<table class="main">
		<tbody>
			
			<tr>
				<td colspan="2" />&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			
	    <tr>
          <td class="etichetta">Ambito Territoriale</td>
          <td class="campo">
    	  	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" title="Ambito Territoriale"  addBlank="true" onChange="getTxt(this)" />
        	<input type="hidden" name="provinciaTerrit" />
       	  </td>
       	</tr>
			<tr>
				<td class="etichetta">Codice fiscale</td>
				<td class="campo"><af:textBox type="text" name="CF" value="<%=cf%>"
					maxlength="16" /></td>
			</tr>

			<tr>
				<td class="etichetta">Cognome</td>
				<td class="campo"><af:textBox type="text" name="cognome" value="<%=cognome%>"
					maxlength="100" /></td>
			</tr>
			<tr>
				<td class="etichetta">Nome</td>
				<td class="campo"><af:textBox type="text" name="nome" value="<%=nome%>"
					maxlength="100" /></td>
			</tr>

			<tr>
				<td class="etichetta">tipo ricerca</td>
				<td class="campo">
				<table cellspacing="0" cellpadding="0" border="0">
					<tbody>
						<tr>
							<td><input type="radio" name="tipoRicerca" value="esatta" <%if ("esatta".equals(tipoRicerca)){%>checked <%}%> />
							esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><input type="radio" name="tipoRicerca" value="iniziaPer" <%if ("iniziaPer".equals(tipoRicerca)){%>checked <%}%>/>
							inizia per</td>
						</tr>
					</tbody>
				</table>
				</td>
			</tr>

			<tr>
				<td>&nbsp;</td>
			</tr>
			<TR>
				<td class="etichetta" nowrap>Data inizio da</td>
					 <td class="campo" >
					    <af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" value="<%=dataInizioDa%>" size="10" maxlength="10" />
					    &nbsp;&nbsp;a&nbsp;&nbsp;
					    <af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" value="<%=dataInizioA%>" size="10" maxlength="10"/>
				 </td>         
			</TR>
			<tr>
				<td class="etichetta">Tipo iscrizione</td>
				<td class="campo">
				<af:comboBox name="codCMTipoIscr" moduleName="M_GETDECMTIPOISCR" classNameBase="input" selectedValue="<%=tipoIscrizione%>"
					addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
				<input name="descrTipoIscrhid" type="hidden" value=""/>
				</td>
			</tr>
			<% if (Sottosistema.CM.isOn()) {%>
			<tr>
			   	<td class="etichetta">Stato dell'atto</td>
			   	<td class="campo">
			   	<af:comboBox  classNameBase="input" title="Stato dell'atto" name="CODSTATOATTO" selectedValue="<%=codStatoAtto%>" 
			   		moduleName="M_GETSTATOATTOISCR" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
      			<input name="descrSTATOATTOhid" type="hidden" value="PROTOCOLLATO"/>
      			</td> 
      		</tr>
      		<% }%>				
			<tr>
				<td class="etichetta">Tipo invalidità</td>
				<td class="campo">
				<af:comboBox name="codCMTipoInvalidita" moduleName="M_GETDECMTIPOINVALIDITA" classNameBase="input" selectedValue="<%=codCMTipoInvalidita%>"
					addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
				<input name="descrTipoInvaliditahid" type="hidden" value=""/>	
				</td>
			</tr>
			<tr>
				<td class="etichetta">Percentuale invalidità</td>
				<td class="campo"><af:textBox classNameBase="input" type="integer" value="<%=numPercInvalildita%>"
					name="numPercInvalildita"
					readonly="<%=String.valueOf(readOnlyStr)%>" size="4" maxlength="3" />%</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
				<div class="sezione2"></div>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Centro per l'Impiego competente</td>
				<td class="campo">
		
					<af:comboBox classNameBase="input" title="Centro per l'Impiego competente" name="CodCPI" selectedValue="<%=CodCPI%>"
						moduleName="M_ELENCOCPI" addBlank="true" required="<%=required%>" />
					<input name="descrCPIhid" type="hidden" value=""/>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
				<div class="sezione2">Ulteriori parameri disponibilità CV</div>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Grado di occupabilità</td>
				<td class="campo">
					<af:comboBox 
						classNameBase="input" 
						title="Grado di occupabilità" 
						name="codGradoOcc"
						moduleName="M_GRADO_OCCUPABILITA" 
						selectedValue="<%=codGradoOcc%>"
						addBlank="true"
						required="false"/>													
				</td>
			</tr>
			<tr>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center"> 
					<input name="cerca" type="submit" class="pulsanti" value="Cerca" /> &nbsp;&nbsp; 
					<input name="reset"	type="reset" class="pulsanti" value="Annulla">
				</td>
			</tr>
		</tbody>
	</table>
	<%out.print(htmlStreamBottom);%>
	<input type="hidden" name="PAGE" value="CollMiratoRisultRicercaPage"/>
	<input type="hidden" name="cdnfunzione" value='<%=((String) serviceRequest.getAttribute("CDNFUNZIONE"))%>' />		
</af:form></p>

</body>
</html>
