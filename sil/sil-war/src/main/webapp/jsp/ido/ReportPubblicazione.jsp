<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.StringUtils,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 String codiceUtente= sessionContainer.getAttribute("_CDUT_").toString();
 String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
 cdnLavoratore="1";
 String queryString = null;
 String htmlStreamTop = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>



<html>
<head>
    <title>ReportPubblicazione.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

    <!-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) -->
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>

    
    <script language="JavaScript">
    
    function impostaNomeReport(nome){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

        document.form1.REPORT.value = nome;
        doFormSubmit(document.form1);
    
    }
    
    function apriStampa(RPT,paramPerReport,tipoDoc)
    { //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
      //paramPerReport: parametri necessari a visualizzare il report 
      //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO

  	  // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
    
      var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
      urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
      urlpage+="&tipoDoc="+tipoDoc;

      if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
      { urlpage+="&salvaDB=true";
      }
      else
      { urlpage+="&salvaDB=false";
      }
    
      setWindowLocation(urlpage);
    
    }//apriStampa()

    function invio(nomeFile,utente){
      if (!controllaFunzTL()) return;
      var datDal=document.form1.DATPUBBLICAZIONEDAL.value;
      var datAl=document.form1.DATPUBBLICAZIONEAL.value;
      var numDal=document.form1.NUMPUBBLICAZIONEDAL.value;
      var numAl=document.form1.NUMPUBBLICAZIONEAL.value;      
      var numanno=document.form1.NUMANNO.value;      
      var utric;
	  var macroqualifiche = "";
	  var valore = '';
		var i = 0;
		for (;i<document.form1.modPubblicazione.length;i++) {
			if (document.form1.modPubblicazione[i].selected) {
				if (valore == '') {
					valore = valore + document.form1.modPubblicazione[i].value;
				}
				else {
					valore = valore + ',' + document.form1.modPubblicazione[i].value;
				}
			}
		}
	  
	  var macrocategoriaObj = document.form1.Macrocategoria;
	  for (j=0;j<macrocategoriaObj.options.length;j++) {
	  	if (macrocategoriaObj.options[j].selected)
	  		macroqualifiche+="&"+macrocategoriaObj.name+"="+macrocategoriaObj.options[j].value;
	  }
      var L=document.form1.UTRIC.length;
      for (var i = 0 ; i< L ; i++)
        { if(document.form1.UTRIC[i].checked) { utric=document.form1.UTRIC[i].value; break; } }
      apriGestioneDoc('RPT_PUBB_MAIN','&cdnLavoratore=<%=cdnLavoratore%>&DATPUBBLICAZIONEDAL=' + datDal + '&DATPUBBLICAZIONEAL=' + datAl + '&NUMPUBBLICAZIONEDAL=' + numDal + '&NUMPUBBLICAZIONEAL=' + numAl + '&ANNO=' + numanno + '&UTRIC=' + utric + '&UTENTE=' + utric + '&CDNUT=' + utente + '&NOMEFILE=' + nomeFile+ macroqualifiche+'&modPubblicazione='+valore,'PUB');

      /* 28/08/2005 DAVIDE
         Riabilito i pulsanti: 
         serve qualora si scelga, nella pagina di gestione dei documenti,
         di salvare senza visualizzare. Tale scelta non apre nessuna pagina e non essendoci
         cambio di contesto i pulsanti rimangono diabilitati impedendo altre operazioni.         
      */
      undoSubmit();
      
    }
    </script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>
<%out.print(htmlStreamTop);%>
<p class="titolo">Ricerca pubblicazioni</p>
<af:form name="form1" method="POST" action="AdapterHTTP" >

<af:textBox type="hidden" name="PAGE" value="stampaReportPage" />
<af:textBox type="hidden" name="REPORT" value="(ND)" />
<af:textBox type="hidden" name="PROMPT0" value="<%=cdnLavoratore%>" />
<af:textBox type="hidden" name="UTENTE" value="<%=codiceUtente%>" />

<table class="main">
<!--<tr><td><br/></td></tr>
<tr><td colspan="2"><p class="titolo">Gestione stampe</p></td></tr>
<tr><td><br/></td></tr>-->

<tr>
  <td class="etichetta">Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="DATPUBBLICAZIONEDAL" classNameBase="input"
    	title="Data richiesta" value="" size="12" maxlength="10" 
    	validateOnPost="true"/>&nbsp;&nbsp;
    Al giorno
    <af:textBox type="date"  classNameBase="input"
    	name="DATPUBBLICAZIONEAL" title="Data richiesta" 
    	value="" size="12" 
    	maxlength="10" validateOnPost="true" />
  </td>
</tr>

<tr>
  <td class="etichetta">Dal numero</td>
  <td class="campo">
    <af:textBox type="integer" name="NUMPUBBLICAZIONEDAL" title="Numero richiesta" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    Al numero
    <af:textBox type="integer" name="NUMPUBBLICAZIONEAL" title="Numero richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
    Anno
    <af:textBox type="integer" name="NUMANNO" title="Anno richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
  </td>
</tr>
<tr><td colspan=2><hr></td><tr>
<tr>
	<td class="etichetta" >Macroqualifiche</td>
	<td class="campo" >
		<af:comboBox multiple="true" title="Macrocategoria" name="Macrocategoria" >
            <OPTION value="1">Annunci Pubblici</OPTION>
            <OPTION value="2">Annunci Riservati</OPTION>
        </af:comboBox>
	</td>
</tr> 
  <tr ><td colspan="2" ><hr width="90%"/></td></tr>      

  <tr>
    <td class="etichetta" valign="top">Modalità pubblicazione</td>
    <td class="campo">
      <af:comboBox multiple="true" title="Modalità Pubblicazione" name="modPubblicazione">
        <OPTION value="1">Web      </OPTION>
        <OPTION value="2">Giornali </OPTION>
        <OPTION value="3">Bacheca  </OPTION>
      </af:comboBox>
    </td>
  </tr>
<tr><td colspan=2><hr></td><tr>
<tr ><td colspan="2" >Utente di inserimento</td></tr>      
<tr ><td colspan="2" >
  <input type="radio" name="UTRIC" value="1" checked> Mie
  <input type="radio" name="UTRIC" value="2"> Mio gruppo
  <input type="radio" name="UTRIC" value=""> Tutte
</td></tr>      

<tr>
  <td colspan="2">
		 <input class="pulsante" type="button" onClick="invio('Pubbl_CC.rpt','<%=codiceUtente%>')" name="btnReport" value="Stampa"/>
  </td>
</tr>

</table>


</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
