<!-- @author: Paolo Roccetti - Agosto 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
    String FILTROCFLAV         = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCFLAV");  
    String FILTRONOMELAV       = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRONOMELAV");  
    String FILTROCOGNOMELAV    = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCOGNOMELAV");  
    String FILTRODATAINIZIOASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAINIZIOASS");  
    String FILTRODATAFINEASS   = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAFINEASS");  
    String FILTROCODTIPOASS    = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCODTIPOASS");  
     
%>
<%@ include file="GestioneCacheTrasfRamoAzienda.inc" %>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  	    <af:linkScript path="../../js/"/>
	  	<title>Trasferimento Ramo Aziendale (Filtri Lavoratori)</title>
		
		<script type="text/javascript">
		<!--
    	//Aggiorna la pagina sottostante quando si chiude la pop-up
    	function impostaFiltri() {
    		if (controllaFunzTL()) {
    			cf = document.Frm1.FILTROCFLAV.value;
    			cognome = document.Frm1.FILTROCOGNOMELAV.value;
    			nome = document.Frm1.FILTRONOMELAV.value;
    			datainizioass = document.Frm1.FILTRODATAINIZIOASS.value;
    			datafineass = document.Frm1.FILTRODATAFINEASS.value;
    			codtipoass = document.Frm1.FILTROCODTIPOASS.value;
    			window.opener.updateFiltriLavoratori(cf, cognome, nome, datainizioass, datafineass, codtipoass);
    			window.close();
    		}
    	}


	    //Cerca il tipo di assunzione
	    function cercaTipoAss(criterio){
	    var descr;
	        var f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CRITERIO=" + criterio;
	        f = f + "&codTipoAss=" + document.Frm1.FILTROCODTIPOASS.value;
	        //aggiunto questo controllo perchè la descrizione del codice NU4 ovvero "Numerica riservatari 12%  (ord.)(scaduto)"
 			// contiene il carattere % che crea problemi nella query string 
  			descr = document.Frm1.descrTipoAss.value;
  			descr = descr.replace("%","&amp;");
 			f = f + "&descrTipoAss=" + descr;
	        f = f + "&updateFunctionName=aggiornaTipoAss";
	        f = f + "&provenienza=TrasfRamoAzienda";
	        var t = "_blank";
	        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
	        window.open(f, t, feat);
	    }      
	
	    //Aggiorna il tipo di assunzione e le combo collegate
	    function aggiornaTipoAss(codice, descrizione, codMonoTipo, codContratto) {
	      document.Frm1.FILTROCODTIPOASS.value = codice;
	      document.Frm1.descrTipoAss.value = descrizione;
	    }
	    
		var flagChanged = false;  

		function fieldChanged() {
		    flagChanged = true;  
		}

		-->
	 </script>
	</head>    
	<body class="gestione" onload="rinfresca();<%if(!FILTROCODTIPOASS.equals("")){%>cercaTipoAss('codice');<%}%>">
  		<br/>
  		<p class="titolo">Trasferimento Ramo Aziendale (Filtri Lavoratori)</p>
  		<br/>
		<center>
			<af:form name="Frm1" method="POST" action="AdapterHTTP">
        									    		
      		<%out.print(htmlStreamTop);%>
      		<table class="main" border="0">
          		<tr>
            		<td class="etichetta">Codice Fiscale</td>
            		<td class="campo">
              			<af:textBox classNameBase="input" 
              						type="text" 
              						name="FILTROCFLAV" 
              						value="<%=FILTROCFLAV%>" 
              						size="30" 
              						maxlength="16"/>
            		</td>
          		</tr>
          		<tr>
            		<td class="etichetta">Cognome</td>
            		<td class="campo">
		              	<af:textBox classNameBase="input" 
		              				type="text" 
		              				name="FILTROCOGNOMELAV"
		              				value="<%=FILTROCOGNOMELAV%>" 
		              				size="30" 
		              				maxlength="50"/>
		            </td>
		        </tr>
		        <tr>
					<td class="etichetta">Nome</td>
		            <td class="campo">
		              	<af:textBox classNameBase="input" 
		              				type="text" 
		              				name="FILTRONOMELAV" 
		              				value="<%=FILTRONOMELAV%>" 
		              				size="30" 
		              				maxlength="50"/>
		            </td>
		        </tr>
		        <tr>
		            <td class="etichetta">Data assunzione</td>
		            <td class="campo">da
		              	<af:textBox classNameBase="input" 
		              				type="date" 
		              				name="FILTRODATAINIZIOASS" 
		              				value="<%=FILTRODATAINIZIOASS%>"
		              				validateOnPost="true" 
		              				title="Data assunzione"
		              				size="10" 
		              				maxlength="12"/>&nbsp;a&nbsp;
		              	<af:textBox classNameBase="input" 
		              				type="date" 
		              				name="FILTRODATAFINEASS" 
		              				value="<%=FILTRODATAFINEASS%>"
		              				validateOnPost="true" 
		              				title="Data assunzione"
		              				size="10" 
		              				maxlength="12"/>	
		            </td>
		        </tr>
		        <tr>	
              		<td class="etichetta">Tipo di Avviamento</td>
              		<td class="campo"  nowrap>
                  		<af:textBox title="Codice del tipo di avviamento" 
                  					value="<%=FILTROCODTIPOASS%>" 
                  					classNameBase="input" 
                  					name="FILTROCODTIPOASS" 
                  					size="4" 
                  					onKeyUp="fieldChanged();" />&nbsp;
                  		<a href="javascript:cercaTipoAss('codice');"><img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
                  		<af:textBox title="Descrizione del tipo di avviamento" 
                  					value="" 
                  					classNameBase="input" 
                  					name="descrTipoAss" 
                  					size="30" 
                  					onKeyUp="fieldChanged();" />&nbsp;
                  		<a href="javascript:cercaTipoAss('descrizione');">
                  			<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
                  		</a>              
              		</td>
              	</tr>		          			
      		</table>                   		
      		<%out.print(htmlStreamBottom);%>
			
			<input class="pulsanti" 
					type="button" 
					name="fatto" 
					value="Imposta Filtri" 
					onclick="javascript:impostaFiltri();"/>&nbsp;&nbsp;
			<input class="pulsanti" 
					type="button" 
					name="chiudi" 
					value="Chiudi" 
					onclick="javascript:window.close();"/>      				  
			
			</af:form>
		</center>	
	</body>
</html>
