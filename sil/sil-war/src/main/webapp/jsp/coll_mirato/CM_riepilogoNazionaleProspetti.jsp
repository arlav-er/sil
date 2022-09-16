<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%	
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMRiepilogoNazionale");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String prgAzienda = "";
	String prgUnita = "";
	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String codProvincia = "";
	// in fase di inserimento viene selezionata la prov del SIL
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	codProvincia = provincia.getCodice();
	String prgProspettoInf = "";
	BigDecimal numkloprospettoinf = new BigDecimal("0");
	String categoria = "";	
	String strNote = "";	
	String displayDateAss = "none";
	String visualizza_display 	= "none";	
		  
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "codStatoAtto");
	
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;	
	boolean flag_insert = false;
	String pagina_back = null;
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//campi
	String prgprospettoinf = "";
	String numlavoratoribc = "0";
	String numlavoratoribc3 = "0";
	String numlavoratoribc18 = "0";
	String numquotarisdisabili = "0";
	String numquotariscatprot = "0";
	String numsospensioni = "0";
	String numesoneri = "0";
	String numdisabiliforza = "0";
	String numcatprotforza = "0";
	String numcatprotdisabiliforza = "0";
	String numscoperturadis = "0";
	String numscoperturacatprot = "0";
	String numQuotaEsuberiArt18 = "0";
	String strnote = "";
	String flgSospensione = "";
	String dataRifQ3 = "";
	String msgRiepilogoData = "";
	
	boolean checkProspetto2011 = serviceResponse.containsAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011") && serviceResponse.getAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011").toString().equalsIgnoreCase("TRUE")?true:false;
	
	
	//INFORMAZIONI OPERATORE
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("M_CMRiepilogoNazionale.ROWS.ROW");				
	
	if (dett != null) {	
		//prgProspettoInf = dett.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)dett.getAttribute("prgProspettoInf")).toString();
		//prgAzienda = dett.getAttribute("prgAzienda") == null? "" : ((BigDecimal)dett.getAttribute("prgAzienda")).toString();
		//prgUnita = dett.getAttribute("prgUnita") == null? "" : ((BigDecimal)dett.getAttribute("prgUnita")).toString();
		prgProspettoInf = SourceBeanUtils.getAttrBigDecimal(dett,"PRGPROSPETTOINF").toString();
		if (checkProspetto2011) {
			numcatprotdisabiliforza = SourceBeanUtils.getAttrBigDecimal(dett, "numcatprotdisabiliforza").toString();
			numsospensioni = SourceBeanUtils.getAttrBigDecimal(dett, "numsospensioni").toString();
			numlavoratoribc = SourceBeanUtils.getAttrBigDecimal(dett, "numlavoratoribc").toString();
		}
		numlavoratoribc3 = SourceBeanUtils.getAttrBigDecimal(dett, "numlavoratoribc3").toString();;
		numlavoratoribc18 = SourceBeanUtils.getAttrBigDecimal(dett, "numlavoratoribc18").toString();;
		numquotarisdisabili = SourceBeanUtils.getAttrBigDecimal(dett, "numquotarisdisabili").toString();
		numquotariscatprot = SourceBeanUtils.getAttrBigDecimal(dett, "numquotariscatprot").toString();
		numesoneri = SourceBeanUtils.getAttrBigDecimal(dett, "numesoneri").toString();
		numdisabiliforza = SourceBeanUtils.getAttrBigDecimal(dett, "numdisabiliforza").toString();
		numcatprotforza = SourceBeanUtils.getAttrBigDecimal(dett, "numcatprotforza").toString();
		numscoperturadis = SourceBeanUtils.getAttrBigDecimal(dett, "numscoperturadis").toString();
		numscoperturacatprot = SourceBeanUtils.getAttrBigDecimal(dett, "numscoperturacatprot").toString();
		numQuotaEsuberiArt18 = dett.getAttribute("numQuotaEsuberiArt18") == null ? "0" : ((BigDecimal)dett.getAttribute("numQuotaEsuberiArt18")).toString();
		strnote = StringUtils.getAttributeStrNotNull(dett, "strnote");
		flgSospensione = StringUtils.getAttributeStrNotNull(dett, "flgSospensione");
		dataRifQ3 = StringUtils.getAttributeStrNotNull(dett, "datRifQ3");
		if (!dataRifQ3.equals("")) {
			msgRiepilogoData = "I seguenti dati sono riferiti al " + dataRifQ3;
		}
	} else {
		prgProspettoInf = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgProspettoInf"); 
	}
	
	SourceBean dat_azienda  = (SourceBean) serviceResponse.getAttribute("M_CMPROSP_GETDATAZIENDA.ROWS.ROW");				
	
	if (dat_azienda != null) {	
		prgProspettoInf = dat_azienda.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)dat_azienda.getAttribute("prgProspettoInf")).toString();
		//prgProspettoInf = SourceBeanUtils.getAttrBigDecimal(dat_azienda,"PRGPROSPETTOINF").toString();
		prgAzienda = dat_azienda.getAttribute("prgAzienda") == null? "" : ((BigDecimal)dat_azienda.getAttribute("prgAzienda")).toString();
		prgUnita = dat_azienda.getAttribute("prgUnita") == null? "" : ((BigDecimal)dat_azienda.getAttribute("prgUnita")).toString();
		
	} 
	
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto );
	l.setCodiceItem("PRGPROSPETTOINF");
	
	  		  	
		     	    	
	String url = "";
	String goBackTitle = "";
	
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		
	
	/*if (infCorrentiAzienda.formatBackList(sessionContainer, "CMProspListaPage") == null) {
		url = "PAGE=CMProspRicercaPage&cdnfunzione="+cdnfunzione;
		if (prgAzienda!=null || !("").equals(prgAzienda)) {
			url+="&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita;
		}
		goBackTitle = "Torna alla ricerca";
	} */
	
	PageAttribs attributi = new PageAttribs(user, "CMProspDettPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false;
	String fieldReadOnly = "true";  	
	String strReadOnly = "true";		    
    		
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<head>
<title>Riepilogo Nazionale</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">
<% if (!prgAzienda.equals("") && !prgUnita.equals("")) { %>
	window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<% } %>
</script>



</head>

<body class="gestione" onload="rinfresca()">

<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}	l.show(out);

%>

<p class="titolo">Riepilogo Nazionale</p>

<center>
	<font color="green">
	
		<!--<af:showMessages prefix="M_CMRiepilogoNazionale"/> -->
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  

<input type="hidden" name="PAGE" value="CMRiepilogoProvinciale"/>
<input type="hidden" name="prgAziendaApp" value="" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="numkloprospettoinf" value="<%=numkloprospettoinf%>"/>  
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>

  

<%out.print(htmlStreamTop);%>
<%if (!msgRiepilogoData.equals("")) {%>
<br><p class="titolo"><%=msgRiepilogoData%></p>
<%}%>
<table class="main" border="0">
<tr>
	<td colspan="6">&nbsp;</td>
</tr>
	<%if (checkProspetto2011) {%>
		<tr>
			<td class="etichetta2">Base Computo</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numlavoratoribc" size="6" maxlength="6" title="Base Computo"
							value="<%=numlavoratoribc%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
		    </td> 
			<td class="etichetta2" colspan=4></td>
		</tr>
	<%} else {%>
		<tr>
			<td class="etichetta2">Base Computo Art.3</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numlavoratoribc" size="6" maxlength="6" title="Base Computo Art.3"
							value="<%=numlavoratoribc3%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
		    </td>
		    <td class="etichetta2">Base Computo Art.18</td>
		    <td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numlavoratoribc18" size="6" maxlength="6" title="Base Computo Art.18"
							value="<%=numlavoratoribc18%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
		    </td>
			<td class="etichetta2" colspan=2></td>
		</tr>
	<%}%>
	<tr> </tr> 
	<tr>
		<td class="etichetta2">Quota di riserva disabili</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numquotarisdisabili" size="6" maxlength="6" title="quota di riserva Art. 3"
						value="<%=numquotarisdisabili%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
		<td class="etichetta2">Quota di riserva Art. 18</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numquotariscatprot" size="6" maxlength="6" title="quota di riserva Art. 18"
						value="<%=numquotariscatprot%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr> </tr>
	<tr>
		<%if (checkProspetto2011) { %>
		<td class="etichetta2">Numero Lavoratoti in sospensione</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numsospensioni" size="6" maxlength="6" title="lavoratoti in sospensione"
						value="<%=numsospensioni%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
	    <%} else {%>
	    	<input type="hidden" name="numsospensioni" value=""/>
	    	<td class="etichetta2">Sospensione in corso</td>
	    	<td class="campo2">
	    	<af:comboBox name="flgSospensione" classNameBase="input" disabled="true">
				<option value="N" <%if ("".equalsIgnoreCase(flgSospensione) || "N".equalsIgnoreCase(flgSospensione)) {%>
					SELECTED="true" <%}%>>No</option>
				<option value="S" <%if ("S".equalsIgnoreCase(flgSospensione)) {%>
					SELECTED="true" <%}%>>Sì</option>
			</af:comboBox>
	    <%}%>
		<td class="etichetta2">Numero posizioni esonerate</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numesoneri" size="6" maxlength="6" title="posizioni esonerate"
						value="<%=numesoneri%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr> </tr>
	<tr>
		<td class="etichetta2">Numero dei disabili in forza</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numdisabiliforza" size="6" maxlength="6" title="disabili in forza"
						value="<%=numdisabiliforza%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
		<td class="etichetta2">Numero categorie protette in forza</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numcatprotforza" size="6" maxlength="6" title="categorie protette in forza"
						value="<%=numcatprotforza%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <%if (checkProspetto2011) { %>
		  	<td class="etichetta2">Categorie protette in forza Conteggiate come disabili</td>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" name="numcatprotdisabiliforza" size="6" maxlength="6" title="categorie protette in forza Conteggiate come disabili"
							value="<%=numcatprotdisabiliforza%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
		    </td>
	    <%} else {%>
	    	<td colspan="2"><input type="hidden" name="numcatprotdisabiliforza" value=""/></td>	
	    <%}%>
	</tr>
	<tr> </tr>
	<tr>
		<td class="etichetta2">Numero scoperture disabili</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numscoperturadis" size="6" maxlength="6" title="scoperture disabili"
						value="<%=numscoperturadis%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td> 
		<td class="etichetta2">Numero scoperture art. 18</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numscoperturacatprot" size="6" maxlength="6" title="scoperture cat.protette"
						value="<%=numscoperturacatprot%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	  	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<%if (!checkProspetto2011) { %>
	<tr>
		<td class="etichetta2">Quota esuberi art. 18</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numQuotaEsuberiArt18" size="6" maxlength="6" title="quota esuberi art. 18"
						value="<%=numQuotaEsuberiArt18%>" onKeyUp="fieldChanged();" validateOnPost="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td colspan="4">&nbsp;</td>
	</tr>
	<%} else {%>
		<input type="hidden" name="numQuotaEsuberiArt18" value=""/>
	<%}%>
	
</table>
<br>
<table class="main" border="0">			
 	<tr>
		   <td class="etichetta">Note</td>
		   <td class="campo">
		        <af:textArea cols="70" rows="4" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
              		 validateOnPost="true" required="false" disabled="false" title="Note" name="strnote" value="<%=strnote%>"  />
		
			</td>
					
	</tr>
</table>
<%out.print(htmlStreamBottom);%>

</body>
</html>