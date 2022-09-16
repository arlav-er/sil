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
	
	String pageToProfile = "RicercaClassificazionePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

	String codAzione = "";
	String txtOut = "";
	
	String ORDINAMENTO = (String) serviceRequest.getAttribute("ORDINAMENTO");
	String PRGCLASSIF = (String) serviceRequest.getAttribute("PRGCLASSIF");
	String STRNOME = (String) serviceRequest.getAttribute("STRNOME");
	String STRDESCRIZIONE = (String) serviceRequest.getAttribute("STRDESCRIZIONE");
	String NUMKLOCLASSIF = (String) serviceRequest.getAttribute("NUMKLOCLASSIF");
	String prgTipoDominio = (String) serviceRequest.getAttribute("prgTipoDominio");
	String DESCDOMINIO = (String) serviceRequest.getAttribute("DESCDOMINIO");
	

	sessionContainer.delAttribute("CLASSIFICAZIONECACHE");

	String[] fields = {"ORDINAMENTO", "PRGCLASSIF", "STRNOME", "STRDESCRIZIONE", "NUMKLOCLASSIF", "prgTipoDominio", "DESCDOMINIO"};

	NavigationCache formRicercaClassificazione = null;
	if (sessionContainer.getAttribute("CLASSIFICAZIONECACHE") != null) {
		formRicercaClassificazione = (NavigationCache) sessionContainer.getAttribute("CLASSIFICAZIONECACHE");
		
		ORDINAMENTO = formRicercaClassificazione.getField("ORDINAMENTO").toString();
		PRGCLASSIF = formRicercaClassificazione.getField("PRGCLASSIF").toString();
		STRNOME = formRicercaClassificazione.getField("STRNOME").toString();
		STRDESCRIZIONE = formRicercaClassificazione.getField("STRDESCRIZIONE").toString();
		NUMKLOCLASSIF = formRicercaClassificazione.getField("NUMKLOCLASSIF").toString();
		prgTipoDominio = formRicercaClassificazione.getField("prgTipoDominio").toString();
		DESCDOMINIO = formRicercaClassificazione.getField("DESCDOMINIO").toString();
		
	} else {
		//salvo in cache
		formRicercaClassificazione = new NavigationCache(fields);
		formRicercaClassificazione.enable();
		for (int i = 0; i < fields.length; i++)
			formRicercaClassificazione.setField(fields[i], (String) serviceRequest.getAttribute(fields[i]));
		sessionContainer.setAttribute("CLASSIFICAZIONECACHE",formRicercaClassificazione);
	}
	
	String goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_GESTCLASSIFICAZIONEPAGE");
	
	txtOut = "Filtri di ricerca:<br/> " + txtOut;

	if (STRNOME != null && !STRNOME.equals("")) {
		txtOut += "Classificazione <strong>" + STRNOME + "</strong>; ";
	}
	
	if (DESCDOMINIO != null && !DESCDOMINIO.equals("")) {
		txtOut += "dominio dati <strong>" + DESCDOMINIO + "</strong>; ";
	}


	String moduleListClassificazione = "MLISTACLASSIFICAZIONEORDINATA";
	SourceBean content = null;
	content = (SourceBean) serviceResponse.getAttribute(moduleListClassificazione+".rows");
		
 	 Vector rowsClassificazione = content.getAttributeAsVector("ROW");
	
 	SourceBean rowClassificazioneForSort = null;
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
			var $selected = $(":selected", $select);
			if (!up) {
				$selected = $($selected.get().reverse());
			}
			$selected.each(function () {
				var $this = $(this);
				if (up) {
					var $before = $this.prev();
					
					if($this.val() != '0'){
						$this.attr('value', $this.val()-1);
		        		$before.attr('value', parseInt($before.val()) + parseInt(1));
					}
	        		
	        		if ($before.length > 0 && !$before.is(":selected")) {
						$this.insertBefore($before);
					}
	        		
				} else {
					var $after = $this.next();
					if($after.val() != null){
						$this.attr('value', parseInt($this.val()) + parseInt(1));
		        		$after.attr('value', $after.val()-1);
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
        
        for(indexClassificazione = 0; indexClassificazione < rowsClassificazione.size(); indexClassificazione++) {
        	rowClassificazioneForSort = (SourceBean) rowsClassificazione.elementAt(indexClassificazione);
      		String nomeClassificazione = StringUtils.getAttributeStrNotNull(rowClassificazioneForSort, "STRNOME"); %>
			
        	$("select").append("<option value=" + '<%=indexClassificazione%>' + ">" + '<%=nomeClassificazione%>' + "</option>");
        	
	  <%}%>
		
	});
	
	
	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		var url = "AdapterHTTP?";
		//alert('url: ' + url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	    setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	}	

</script>


<script type="text/javascript">

	function getSelectValues() {
	    var ord_classif = document.getElementById("ord_classif");
		ord_classif.value = "";
		
		var select = eval('document.frmOrder.mySelect');
		var txt = "";
	    var i;
	    for (i = 0; i < select.options.length; i++) {
	    	txt = txt + select.options[i].value + ":" + select.options[i].text + ";";
	    }
	    //alert(txt);
	    ord_classif.value = txt;
	    
	    return ord_classif.value;
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

		
		
		<input type="hidden" name="PAGE" value="GestClassificazionePage" />
		<input type="hidden" name="MODULE" value="MListaClassificazione">&nbsp;
	<p class="titolo">Ordinamento Classificazione</p>
	
	<div style="margin: 0 auto;width: 400px;height:auto" ng-app='myApp' ng-controller="ArrayController" >
		 <af:comboBox name="mySelect" title="Ordine Classificazioni" multiple="true" size="20" />
		<br />
	    <input class="pulsanti" type="button" id="up" value="Sposta su" />
	    <input class="pulsanti" type="button" id="down" value="Sposta giu" />
	    <br /><br />
	    

	</div>

			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
			<input type="hidden" name="prgTipoDominio" value="<%=prgTipoDominio%>" />
			<input type="hidden" name="STRNOME" value="<%=STRNOME%>" />
			<input type="hidden" name="DESCRIZIONE" value="<%=STRDESCRIZIONE%>" />
			<input type="hidden" name="NUMKLOCLASSIF" value="<%=NUMKLOCLASSIF%>">
			<input type="hidden" name="DESCDOMINIO" value="<%=DESCDOMINIO%>" />
			<input type="hidden" id="ord_classif" name="ord_classif" value="" />
			<%--
				//if (canOrdina) {
			--%>
			
 				
			<%--
// 			onClick="go('PAGE=OrdinaTemplatePage&cdnFunzione=<%=cdnFunzione%>&codAmbitoTem=<%=CODAMBITOTEM%>&codTipoDominio=<%=CODTIPODOMINIO%>&STRNOME=<%=STRNOME%>&DATINIZIOVAL=<%=DATINIZIOVAL%>&DATFINEVAL=<%=DATFINEVAL%>')" /> 	
			//	}
			--%>
			
			<table>
			<tr>
				<td align="center">
					<input class="pulsante" type="submit" name="ordina" value="Ordina Classificazione" onclick="getSelectValues()" />
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