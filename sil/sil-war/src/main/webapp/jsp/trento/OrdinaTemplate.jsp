<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="SezioniATendina.inc" %>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils, it.eng.sil.security.User, it.eng.afExt.utils.*,it.eng.sil.util.*, java.lang.*,
    java.text.*, java.math.*,  java.sql.*,   oracle.sql.*,  java.util.*, it.eng.sil.security.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	
	String pageToProfile = "RicercaTemplatePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	String codAzione = "";
	String txtOut = "";
	
	String STRNOME = (String) serviceRequest.getAttribute("STRNOME");
	String CODTIPODOMINIO = (String) serviceRequest
			.getAttribute("CODTIPODOMINIO");
	String CODAMBITOTEM = (String) serviceRequest
			.getAttribute("CODAMBITOTEM");
	String DATINIZIOVAL = (String) serviceRequest
			.getAttribute("DATINIZIOVAL");
	String DATFINEVAL = (String) serviceRequest
			.getAttribute("DATFINEVAL");
	String DESCCODAMBITOTEM = (String) serviceRequest
			.getAttribute("DESCCODAMBITOTEM");
	String DESCDOMINIO = (String) serviceRequest
			.getAttribute("DESCDOMINIO");
	String PRGTEMPLATESTAMPA = (String) serviceRequest
			.getAttribute("PRGTEMPLATESTAMPA");
	String nomeClassif = (String) serviceRequest.getAttribute("nomeClassif");
	
	String PRGCLASSIF = (String) serviceRequest.getAttribute("PRGCLASSIF");

	sessionContainer.delAttribute("EDITORCACHE");

	String[] fields = {"STRNOME", "CODTIPODOMINIO", "CODAMBITOTEM",
			"DATINIZIOVAL", "DATFINEVAL", "PRGTEMPLATESTAMPA",
			"DESCCODAMBITOTEM", "DESCDOMINIO", "prgClassif", "nomeClassif"};

	NavigationCache formRicercaTemplate = null;
	if (sessionContainer.getAttribute("TEMPLATECACHE") != null) {
		formRicercaTemplate = (NavigationCache) sessionContainer
				.getAttribute("TEMPLATECACHE");
		if (formRicercaTemplate.getField("STRNOME") != null){
			STRNOME = formRicercaTemplate.getField("STRNOME").toString();
		}
		if (formRicercaTemplate.getField("CODTIPODOMINIO") != null){
			CODTIPODOMINIO = formRicercaTemplate.getField("CODTIPODOMINIO").toString();
		}
		if (formRicercaTemplate.getField("CODAMBITOTEM") != null){
			CODAMBITOTEM = formRicercaTemplate.getField("CODAMBITOTEM").toString();
		}
		if (formRicercaTemplate.getField("DATINIZIOVAL") != null){
			DATINIZIOVAL = formRicercaTemplate.getField("DATINIZIOVAL").toString();
		}
		if (formRicercaTemplate.getField("DATFINEVAL") != null){
			DATFINEVAL = formRicercaTemplate.getField("DATFINEVAL").toString();
		}
		if (formRicercaTemplate.getField("DESCCODAMBITOTEM") != null){
			DESCCODAMBITOTEM = formRicercaTemplate.getField("DESCCODAMBITOTEM").toString();
		}
		if (formRicercaTemplate.getField("DESCDOMINIO") != null){
			DESCDOMINIO = formRicercaTemplate.getField("DESCDOMINIO").toString();
		}
		if (formRicercaTemplate.getField("prgClassif") != null){
			PRGCLASSIF= formRicercaTemplate.getField("prgClassif").toString();
		}
		if (formRicercaTemplate.getField("nomeClassif") != null){
			nomeClassif= formRicercaTemplate.getField("nomeClassif").toString();
		}
	} else {
		//salvo in cache
		formRicercaTemplate = new NavigationCache(fields);
		formRicercaTemplate.enable();
		for (int i = 0; i < fields.length; i++)
			formRicercaTemplate.setField(fields[i],
					(String) serviceRequest.getAttribute(fields[i]));
		sessionContainer.setAttribute("TEMPLATECACHE",
				formRicercaTemplate);
	}

	if (STRNOME != null && !STRNOME.equals("") && !STRNOME.equals("null")) {
		txtOut += "template <strong>" + STRNOME + "</strong>; ";
	}
	if (DESCCODAMBITOTEM != null && !DESCCODAMBITOTEM.equals("") && !DESCCODAMBITOTEM.equals("null")) {
		txtOut += "ambito template <strong>" + DESCCODAMBITOTEM
				+ "</strong>; ";
	}
	if (DESCDOMINIO != null && !DESCDOMINIO.equals("") && !DESCDOMINIO.equals("null")) {
		txtOut += "dominio dati <strong>" + DESCDOMINIO + "</strong>; ";
	}
	if (DATINIZIOVAL != null && !DATINIZIOVAL.equals("") && !DATINIZIOVAL.equals("null")) {
		txtOut += "data inizio validita' <strong>" + DATINIZIOVAL
				+ "</strong>; ";
	}
	if (DATFINEVAL != null && !DATFINEVAL.equals("") && !DATFINEVAL.equals("null")) {
		txtOut += "data fine validita' <strong>" + DATFINEVAL
				+ "</strong>; ";
	}
	if (nomeClassif != null && !nomeClassif.equalsIgnoreCase("") && !nomeClassif.equals("null")) {
		txtOut += "classificazione <strong>" + nomeClassif
				+ "</strong>; ";
	}
	String moduleListClassificazione = "M_ST_TEMPLATE_STAMPA_ORD";
	//String moduleListClassificazione = "M_ST_TEMPLATE_STAMPA";
	SourceBean content = null;
	content = (SourceBean) serviceResponse.getAttribute(moduleListClassificazione+".rows");
		
 	 Vector rowsClassificazione = content.getAttributeAsVector("ROW");
	
 	SourceBean rowClassificazioneForSort = null;
 	
 	String goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_GESTTEMPLATEPAGE");
 	goBackUrl = goBackUrl.replaceAll("&OPERAZIONETEMPLATE=ORDINA", "");
%>




<html>
<head>
<title>Ordina Template</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
<script src="../../js/jqueryui/jquery-ui.min.js"></script>


<script type="text/javascript">
	
	;
	(function ($) {
		function moveSelected(select, up) {
			var $select = $(select);
			//console.log($select);
			var $selected = $(":selected", $select);
			//console.log($selected);
			if (!up) {
				$selected = $($selected.get().reverse());
			}
			$selected.each(function () {
				var $this = $(this);
				//console.log($this);
				if (up) {
					var $before = $this.prev();
					//console.log($before);
				    //alert($before.closest('optgroup').attr('label'));
				    if($before.closest('optgroup').attr('label')!=null) {
				    	//alert("Non è possibile spostare i template all'interno di un'altra classificazione");
				    	return;
				    }
				    
				    var n = $this.val().indexOf(":");
				    
				    if($this.val().substring(0, n) != '0'){
						var indiceUP = parseInt($this.val().substring(0, n)) - parseInt(1);
						var prgTemplateUP = $this.val().substring(n+1, $this.val().length);

						var nB = $before.val().indexOf(":");
						var indiceDOWN = parseInt($before.val().substring(0, nB)) + parseInt(1);
						var prgTemplateDOWN = $before.val().substring(nB+1, $before.val().length);

				    	$this.attr('value', indiceUP + ':' + prgTemplateUP);
		        		$before.attr('value', indiceDOWN + ':' + prgTemplateDOWN);
					    
				    }
				    
	        		if ($before.length > 0 && !$before.is(":selected")) {
						$this.insertBefore($before);
					}
	        		
				} else {
					var $after = $this.next();
					if($after.closest('optgroup').attr('label')!=null) {
				    	//alert("Non è possibile spostare i template all'interno di un'altra classificazione");
				    	return;
				    }
				    
					if($after.val() != null){

						var n = $this.val().indexOf(":");
						var indiceUP = parseInt($this.val().substring(0, n)) + parseInt(1);
						var prgTemplateUP = $this.val().substring(n+1, $this.val().length);

						var nA = $after.val().indexOf(":");
						var indiceDOWN = parseInt($after.val().substring(0, nA)) - parseInt(1);
						var prgTemplateDOWN = $after.val().substring(nA+1, $after.val().length);

						$this.attr('value', indiceUP + ':' + prgTemplateUP);
		        		$after.attr('value', indiceDOWN + ':' + prgTemplateDOWN);
					}
					
	        		if ($after.length > 0 && !$after.is(":selected")) {
						$this.insertAfter($after);
					}
	        		
				}
			});
		}
		
		$.fn.moveSelectedUp = function () {
			return this.each(function () {
				moveSelected(this, true);
			});
		};
		$.fn.moveSelectedDown = function () {
			return this.each(function () {
				moveSelected(this, false);
			});
		};
	})(jQuery);

$(function() {
	$("#up").click(function(){
		$("select").moveSelectedUp();
	});
	$("#down").click(function(){
		$("select").moveSelectedDown();
	});
});
	
</script>


<script type="text/javascript">

	$( document ).ready(function() {
		<%
		int indexClassificazione;
		LinkedHashSet gruppi = new LinkedHashSet();
		Iterator iter = rowsClassificazione.iterator();
		
		while (iter.hasNext()) {
			SourceBean attuale = (SourceBean) iter.next();
			if (attuale.containsAttribute("GRUPPO")) {
				String gruppo = (String) attuale.getAttribute("GRUPPO");						
				gruppi.add(gruppo);
			} 
		}
        
        //for(indexClassificazione = 0; indexClassificazione < rowsClassificazione.size(); indexClassificazione++) {
        	
        	
        	//rowClassificazioneForSort = (SourceBean) rowsClassificazione.elementAt(indexClassificazione);
        	
 
        	//String nomeClassificazione = StringUtils.getAttributeStrNotNull(rowClassificazioneForSort, "STRNOME"); 
        	
        	if (gruppi.size() > 0) {
				Iterator iterGruppi = gruppi.iterator();
				while (iterGruppi.hasNext()) {
					Iterator iterRiga = rowsClassificazione.iterator();
					String iterGruppo = (String) iterGruppi.next();
					iterGruppo = iterGruppo.toUpperCase();
					iterGruppo = iterGruppo.replaceAll("'", "\"");
					%>
					
 					$("select").append("<optgroup label='" + '<%=iterGruppo%>' + "''>");
					<%
					int indice =-1;
					while (iterRiga.hasNext()) {
						++indice;
						SourceBean attuale = (SourceBean) iterRiga.next();
						String gruppo = (String) attuale.getAttribute("GRUPPO");
						gruppo = gruppo.toUpperCase();
						String nomeClassificazione = StringUtils.getAttributeStrNotNull(attuale, "STRNOME");
						nomeClassificazione = nomeClassificazione.replaceAll("'", "\"");
						BigDecimal prgClassificazione = (BigDecimal) attuale.getAttribute("PRGTEMPLATESTAMPA");
						//String prgClassificazione = StringUtils.getAttributeStrNotNull(attuale, "PRGTEMPLATESTAMPA");
						if (gruppo.equals(iterGruppo)) { %>

							$("select").append("<option value=" + '<%=indice%>' + ":" + '<%=prgClassificazione%>' + ">" + '<%=nomeClassificazione%>' + "</option>");
						
							
							<%
								
						}
					}
					%>
					$("select").append("</optgroup>");
					<%
				}
			} else {
				Iterator iterRiga = rowsClassificazione.iterator();
				int indice =-1;
				while (iterRiga.hasNext()) {
					++indice;
					SourceBean attuale = (SourceBean) iterRiga.next();
					String nomeClassificazione = StringUtils.getAttributeStrNotNull(attuale, "STRNOME");
					nomeClassificazione = nomeClassificazione.replaceAll("'", "\"");
					BigDecimal prgClassificazione = (BigDecimal) attuale.getAttribute("PRGTEMPLATESTAMPA");
					 %>
					 
					 $("select").append("<option value=" + '<%=indice%>' + ":" + '<%=prgClassificazione%>' + ">" + '<%=nomeClassificazione%>' + "</option>");
						
					
						
						<%
							

				}
      		}
        	%>
		
	});
	
	
	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		var url = "AdapterHTTP?";
	    setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	}	

</script>


<script type="text/javascript">

	function getSelectValues() {
	    var ord_templ = document.getElementById("ord_templ");
		ord_templ.value = "";
		document.frmOrder.OPERAZIONETEMPLATE.value = "ORDINA";
		
		var select = eval('document.frmOrder.mySelect');
		var txt = "";
	    var i;
	    for (i = 0; i < select.options.length; i++) {
	    	txt = txt + select.options[i].value + ";";
	    }
	    //alert(txt);
	    ord_templ.value = txt;
	    console.log(ord_templ.value);
	    return ord_templ.value;
	}

</script>


</head>
<body class="gestione"">
	<font color="red">
  		<af:showErrors/>
	</font>

	<af:form method="POST" action="AdapterHTTP" dontValidate="true" name="frmOrder">
	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td
				style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
				<%
				
					out.print(txtOut);
				%>
			</td>
		</tr>
	</table>
	<br>
	
	<center>

		
		
		<input type="hidden" name="PAGE" value="GestTemplatePage" />
		<input type="hidden" name="MODULE" value="MListaTemplate">
		<input type="hidden" name="OPERAZIONETEMPLATE" value="">
	<p class="titolo">Ordinamento Template</p>
	
	<div style="margin: 0 auto;width: 400px;height:auto" ng-app='myApp' ng-controller="ArrayController" >
		 <af:comboBox name="mySelect" title="Ordine Classificazioni"  multiple="true" size="50"  />
		<br />
	    <input class="pulsanti" type="button" id="up" value="Sposta su" />
	    <input class="pulsanti" type="button" id="down" value="Sposta giu" />
	    <br /><br />
	    
	</div>


			
			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
			<input type="hidden" name="codAmbitoTem" value="<%=CODAMBITOTEM%>" />
			<input type="hidden" name="codTipoDominio" value="<%=CODTIPODOMINIO%>" />
			<input type="hidden" name="STRNOME" value="<%=STRNOME%>" />
			<input type="hidden" name="DATINIZIOVAL" value="<%=DATINIZIOVAL%>" />
			<input type="hidden" name="DATFINEVAL" value="<%=DATFINEVAL%>" />
			<input type="hidden" id="ord_templ" name="ord_templ" value="" />

			
			<table>
			<tr>
				<td align="center">
					<input class="pulsante" type="submit" name="ordina" value="Ordina Template" onclick="getSelectValues()" />
				</td>
				<td align="center">
					<input type="button" class="pulsanti" name="ANNULLA" value="Torna alla lista" onClick="annulla();">
				</td>
			</tr>
		</table>
			
		</af:form>
	
</center>
</body>
</html>