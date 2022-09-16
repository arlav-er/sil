<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                it.eng.sil.module.collocamentoMirato.constant.*,
                java.math.*"
%>

<%@ taglib uri="aftags" prefix="af" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
//Prendo la config per Non Vedente e Masso Fisioterapista
String resultConfigNonVedenti_MassoFisioterapista = serviceResponse.containsAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM").toString():"0";


String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String chiamata =StringUtils.getAttributeStrNotNull(serviceRequest,"chiamata");
String CodCPI =StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");

String queryString = null;

Calendar oggi = Calendar.getInstance();
String anno = Integer.toString(oggi.get(1));

%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
  function stampa() {
	if (!controllaFunzTL()) return;
	
	var anno = document.form1.anno.value;
	var codMonoStatoProspetto = document.form1.codMonoStatoProspetto.value;
	var tolleranza = document.form1.tolleranza.value;
	var codMonoCategoria = document.form1.codMonoCategoria.value;
	var flgConvenzione = document.form1.FlgConvenzione.value;
	var codMonoProv = document.form1.codMonoProv.value;
	var flgSospNazMob = document.form1.flgSospensioneMob.value;
	var flgCapoGruppo = document.form1.flgCapoGruppo.value;
	var codFisCapoGruppo = document.form1.strCFAZCapogruppo.value;
	var flgCompetenza = document.form1.flgCompetenzaProsp.value;
	
	<% if ("1".equals(resultConfigNonVedenti_MassoFisioterapista)){%>
		var flagScoperturaCentral_N_Vedente_o_Masso_Fisiot = document.form1.flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.value;
		apriGestioneDoc('RPT_STAMPA_PROSPETTI','&anno='+anno+'&codMonoStatoProspetto='+codMonoStatoProspetto+'&tolleranza='+tolleranza+'&numNved_Masso='+<%=resultConfigNonVedenti_MassoFisioterapista%>+
                '&codMonoCategoria='+codMonoCategoria+'&flgConvenzione='+flgConvenzione+'&flagScoperturaCentral_N_Vedente_o_Masso_Fisiot='+flagScoperturaCentral_N_Vedente_o_Masso_Fisiot+
                '&codMonoProv='+codMonoProv+'&flgSospensioneMob='+flgSospNazMob+'&flgCapoGruppo='+flgCapoGruppo+'&strCFAZCapogruppo='+codFisCapoGruppo+
                '&flgCompetenzaProsp='+flgCompetenza, 'L68_AZI');
		
	<%}else{%>
	
			apriGestioneDoc('RPT_STAMPA_PROSPETTI','&anno='+anno+'&codMonoStatoProspetto='+codMonoStatoProspetto+'&tolleranza='+tolleranza+
	                '&codMonoCategoria='+codMonoCategoria+'&flgConvenzione='+flgConvenzione+
	                '&codMonoProv='+codMonoProv+'&flgSospensioneMob='+flgSospNazMob+'&flgCapoGruppo='+flgCapoGruppo+'&strCFAZCapogruppo='+codFisCapoGruppo+
	                '&flgCompetenzaProsp='+flgCompetenza, 'L68_AZI');
	<%}%>
	  undoSubmit();
	}
 
</script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Stampa lista aziende che hanno presentato un prospetto</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET">

<table class="main">
	<tr>
		<td class="etichetta">Anno</td>
		<td class="campo">
			<af:textBox name="anno" title="Anno" type="text" size="5" maxlength="4" required="true" validateOnPost="true" value="<%=anno%>"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Stato prospetto</td>
		<td class="campo" >
			<af:comboBox title="Stato prospetto" name="codMonoStatoProspetto" addBlank="true">
            	<OPTION value="A" selected="true">In corso d'anno</OPTION>
            	<OPTION value="S">Storicizzato</OPTION>
            	<OPTION value="V">SARE:Storicizzato</OPTION>
            	<OPTION value="U">Storicizzato: uscita dall'obbligo</OPTION>
            </af:comboBox>
		</td>
	</tr>  
	<tr>
		<td class="etichetta">Parametro di tolleranza della scopertura</td>
		<td class="campo" >
			<af:comboBox title="Parametro di tolleranza della scopertura" name="tolleranza" addBlank="true" required="true">
            	<OPTION value="1" selected="true">1</OPTION>
            	<OPTION value="0">0</OPTION>
            </af:comboBox>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Scopertura con convenzione</td>
		<td class="campo">
			<af:comboBox name="FlgConvenzione" title="Scopertura con convenzione" classNameBase="input"
            			  addBlank="true" required="true">
     						<OPTION value="S">Sì</OPTION>
        					<OPTION value="N">No</OPTION>
    		</af:comboBox>
    	</td>
    </tr>   
	<tr>
		<td class="etichetta">Fascia di appartenenza dell'azienda</td>
		<td class="campo">
			<af:comboBox name="codMonoCategoria" classNameBase="input">	  
	        	<option value="" ></option>
	        	<option value="<%=ProspettiConstant.CATEGORIA_NULLA%>">Categoria nulla</option>
	            <option value="A">più di 50 dipendenti</option>
	            <option value="B">da 36 a 50 dipendenti</option>               
	            <option value="C">da 15 a 35 dipendenti</option> 
	        </af:comboBox>  
		</td>   
	</tr>
	<tr>
		<td class="etichetta">Provenienza</td>
		<td class="campo">
			<af:comboBox name="codMonoProv" title="Provenienza" classNameBase="input" addBlank="true">
     			<OPTION value="S">Sare</OPTION>
        		<OPTION value="M">Manuale</OPTION>
    		</af:comboBox>
    	</td>
    </tr>   
    
    <% if ("1".equals(resultConfigNonVedenti_MassoFisioterapista)){%>
    <tr>
		<td class="etichetta">Scopertura Central. Non Vedente o Masso Fisiot.</td>
		<td class="campo">
			<af:comboBox name="flagScoperturaCentral_N_Vedente_o_Masso_Fisiot" title="Scopertura Central Non Vedente o Masso Fisioterapista" classNameBase="input" addBlank="true">
     			<OPTION value="S">Si</OPTION>
        		<OPTION value="N">No</OPTION>
    		</af:comboBox>
    	</td>
    </tr>
	
	<%}%>
    
    <tr>
	    <td class="etichetta">Sospensione nazionale</td>
	    <td class="campo">
	    	<af:comboBox name="flgSospensioneMob" title="Sospensione nazionale" classNameBase="input" addBlank="true">
                <option value="S">Si</option>
            	<option value="N">No</option>  
        	</af:comboBox>  
	    </td>   
	</tr>
	
	<tr>
	    <td class="etichetta">Prospetto presentato dalla Capogruppo</td>
	    <td class="campo">
	    	<af:comboBox name="flgCapoGruppo" title="Prospetto presentato dalla Capogruppo" classNameBase="input" addBlank="true">
                <option value="S">Si</option>
            	<option value="N">No</option>  
        	</af:comboBox>  
	    </td>   
	</tr>
	
	<tr>
	    <td class="etichetta">Codice fiscale capogruppo</td>
	    <td class="campo">
           	<af:textBox type="text" name="strCFAZCapogruppo" value="" size="50" maxlength="100"/>
         	</td> 
	</tr>
	
	<tr>
	    <td class="etichetta">Prospetto di competenza</td>
	    <td class="campo">
	    	<af:comboBox name="flgCompetenzaProsp" title="Prospetto di competenza" classNameBase="input" addBlank="true">
                <option value="S" selected="true">Si</option>
            	<option value="N">No</option>  
        	</af:comboBox>  
	    </td> 
	</tr>
	
</table>
	
<br/>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampa()" /></center>
<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>

