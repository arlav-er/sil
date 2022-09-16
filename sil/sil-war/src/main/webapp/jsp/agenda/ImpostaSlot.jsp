<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest, "giornoDB");
String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest, "meseDB");
String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest, "annoDB");
String nrosDB = StringUtils.getAttributeStrNotNull(serviceRequest, "nrosDB");
String giorno = StringUtils.getAttributeStrNotNull(serviceRequest, "giorno");
String mese = StringUtils.getAttributeStrNotNull(serviceRequest, "mese");
String anno = StringUtils.getAttributeStrNotNull(serviceRequest, "anno");
String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest, "cod_vista");
int mod = 0;
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}

String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript"  src="../../js/script_comuni.js">
  </script>
  <title>Inserimento Multiplo Slot</title>
  <script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        flagChanged = true;
    }
  </script>
  <script type="text/javascript">
function chiudi()
{
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
                document.frm.PAGE.value = "GestSlotPage";
                doFormSubmit(document.frm);
          }
    } else {
          document.frm.PAGE.value = "GestSlotPage";
          doFormSubmit(document.frm);
    }
}


//Controlla che l'orario str sia nel formato hh:mm
function checkTime(str)
{
  var hourPat = /^(\d{1,2})(\:)(\d{1,2})$/;
  var matchArray = str.match(hourPat);

  if(matchArray == null) {
    //alert("Inserire l'orario nel formato hh:mm");
    return false;
  }
	
  var h,m;
  h = matchArray[1];
  m = matchArray[3];
	
  //alert(h);
  //alert(m);
	
  if (h<0 || h > 23) {
    //alert("L'orario " + str + " non è corretto.\nInserire un orario valido.");
    return false;
  }
  if (m<0 || m > 59) {
    //alert("L'orario " + str + " non è corretto.\nInserire un orario valido.");
    return false;
  }
	
  return true;
}
  
function calcola(n)
{
  var orarioPat = /^(\d{1,2})(\:)(\d{1,2})$/;
  var matchArray = null;

  var dalle = document.frm.STRORADALLE.value;
  var alle = document.frm.STRORAALLE.value;
  var min = document.frm.NUMMINUTI.value;
  var qta = document.frm.NUMQTA.value;

  var ok = true;
  var msg = "Per il calcolo è necessario inserire correttamente tutti gli altri dati";
  var c, h, m;
    
  switch(n) {
    case 1:
            // click su "alle"
            if(dalle!="" && checkTime(dalle) && !isNaN(min) && !isNaN(qta)) {
              alle = "";
              c = qta * min;
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m + c;
              h = Math.floor(c/60);
              m = c % 60;
              if(h < 10) { alle = "0"; }
              alle += h + ":";
              if(m < 10) { alle += "0"; }
              alle += m;
              document.frm.STRORAALLE.value = alle;
            } else {
              ok = false;
            }
            break;
    case 2:
            // click su "minuti"
            if(dalle!="" && checkTime(dalle) && alle!="" && checkTime(alle) && !isNaN(qta) && qta!=0) {
              // min = (alle - dalle) / qta;
              min = "";
              matchArray = alle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m; // alle
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = c - ( (h*60) + m);
              c = Math.floor(c / qta);
              document.frm.NUMMINUTI.value = c;
            } else {
              ok=false;
            }
            break;
    case 3:
            // click su "qta"
            if(dalle!="" && checkTime(dalle) && alle!="" && checkTime(alle) && !isNaN(min) && min!=0) {
              // qta = (alle - dalle) / min
              matchArray = alle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m; // alle
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = c - ( (h*60) + m);
              c = Math.floor(c / min);
              document.frm.NUMQTA.value = c;
            } else {
              ok = false;
            }
            break;
  } // switch(n)
  if(!ok) { alert(msg); }
  else { fieldChanged(); }
  }

  function controllaDate()
    {
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var di = document.frm.dataParInizio.value;
      var df = document.frm.dataParFine.value;
      var ok1, ok2;
      var s, g, m, a;
      var dataI, dataF;
  
      var matchArray = di.match(datePat);
      if(matchArray == null) { 
        ok1 = false;
        dataI = "";
      } else { 
        ok1 = true;
        s = matchArray[2];
        var tmp1 = di.split(s);
        g = tmp1[0];
        m = tmp1[1];
        a = tmp1[2];
        dataI = parseInt(a + m + g,10);
      }

      matchArray = df.match(datePat);
      if(matchArray == null) { 
        ok2 = false;
        dataF = "";
      } else { 
        ok2 = true;
        s = matchArray[2];
        var tmp2 = df.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataF = parseInt(a + m + g,10);
      }
  
		if(ok1 && ok2) {
			diff = confrontaDate(di,df);
			if(diff < 0) { 
				alert("La data di inizio (data dal) deve essere minore o uguale alla data di termine (data al)."); 
          		return false;
        	}
        	//14/12/2010 rodi - il periodo può essere al massimo di 6 mesi o si rischia di inserire milioni di miliardi di record... 
        	else if (diff > 180) {
        		alert("E' consentito inserire slot per un periodo massimo di 6 mesi.");
          		return false;
        	}
        	else { 
				return true; 
			}
      	} 
      	else {
        	return true;
      	}
	}
	
	function controllaAzLav()
	{
      var numLavoratori = document.frm.NUMLAVORATORI.value;
      
      if(document.frm.CHECKAZ.checked) {
        return(true);
      } else {
        if(isNaN(numLavoratori) || numLavoratori=="") {
        	alert("Selezionare il check azienda oppure inserire il numero max. di lavoratori");
        	return(false);
        } else {
        	if(numLavoratori > 0)  {
        		return(true);
        	} else {
        		alert("Il numero max. di lavoratori deve essere maggiore o uguale a zero");
        		return(false);
        	}
        }
      }
	}
	
	function controllaForm()
	{
		var okDate = controllaDate();
		var okAzLav = controllaAzLav();
		
		if(okDate && okAzLav) { return(true); }
		else { return(false); }
	}
	
	
	function set_num_aziende() {
      if(document.frm.CHECKAZ.checked) {
        document.frm.NUMAZIENDE.value = 1;
      } else {
        document.frm.NUMAZIENDE.value = 0;
      }
    }
    
    function listaAssegnazione() {    	
    	codServizio = document.getElementsByName("CODSERVIZIO");
    	prgAmbiente = document.getElementsByName("PRGAMBIENTE");
    	codCpi =      document.getElementsByName("CODCPI");
    	prgSpi =      document.getElementsByName("PRGSPI");
    	var f = "AdapterHTTP?PAGE=ListaAssegnazioniPage" + 
    		"&PRGAMBIENTE=" +   prgAmbiente[0].value + 
    		"&CODSERVIZIO=" +   codServizio[0].value + 
    		"&PRGSPI=" +        prgSpi[0].value + 
        	"&AGENDA_CODCPI=" + codCpi[0].value;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        openedWindow = window.open(f, t, feat);
    }
    function assegnazioneScelta(prgSpi, codServizio, prgAmbiente){
    	document.frm.PRGSPI.value=prgSpi;
    	document.frm.CODSERVIZIO.value = codServizio;
    	document.frm.PRGAMBIENTE.value = prgAmbiente;
    	flagChanged = true;
    	openedWindow.close();
    }        
   </script>
</head>

<body class="gestione"  onload="rinfresca();">
<br>
<p class="titolo">Inserimento Multiplo Slot</p>



<%
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>


<%out.print(htmlStreamTop);%>

<af:form action="AdapterHTTP" name="frm" method="POST" onSubmit="controllaForm()">
<input type="hidden" name="PAGE" value="InsNSlotPage">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="CDNPARUTENTE" value="<%=cdnParUtente%>">
<% if(mod==0) {%>
    <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
    <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
    <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
<% } else { %>
	<% if(mod==1) { %>
    	<input name="nrosDB" type="hidden" value="<%=nrosDB%>"/>
        <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
    <% } else {%>
        <%if(mod==2) { // Ricerca precedente%>
        	<input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
            <input name="sel_servizio" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio")%>"/>
            <input name="sel_aula" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula")%>"/>
            <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDa")%>"/>
            <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
        <%}%>
    <% } %>
<% } %>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>

<table class="main">
<tr>
  <td class="etichetta">Data Dal</td>
  <td class="campo">
  <af:textBox name="dataParInizio" title="Data Dal"
              type="date"
              size="10"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Data Al</td>
  <td class="campo">
  <af:textBox name="dataParFine" title="Data Al"
              type="date"
              size="10"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo">
  <af:comboBox name="PRGSPI"
               size="1"
               title="Operatore"
               multiple="false"
               required="true"
               moduleName="COMBO_SPI_SCAD"
               selectedValue=""
               addBlank="true"
               blankValue=""
               classNameBase="input"
               onChange="fieldChanged()"
  />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="pulsanti"  value="Assegnazione" onclick="listaAssegnazione()"/>
  </td>
</tr>
<tr>
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
  <af:comboBox name="CODSERVIZIO"
               size="1"
               title="<%=labelServizio %>"
               multiple="false"
               required="true"
               moduleName="COMBO_SERVIZIO_SCAD"
               selectedValue=""
               addBlank="true"
               blankValue=""
               classNameBase="input"
               onChange="fieldChanged()"
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Ambiente/Aula</td>
    <td class="campo">
      <af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente/Aula"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="" addBlank="true" blankValue=""
                     classNameBase="input"
                     onChange="fieldChanged()"/>
    </td>
</tr>
<tr>
  <td class="etichetta">Azienda</td>
  <td class="campo">
    &nbsp;
    <input type="checkbox" name="CHECKAZ" onClick="set_num_aziende()">
    <input type="hidden" name="NUMAZIENDE" value="0"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Numero max. Lavoratori</td>
  <td class="campo">
    <af:textBox name="NUMLAVORATORI" 
                value=""
                size="4"
                title="Numero max. Lavoratori"
                disabled="false"
                required="false"
                type="integer"
                validateOnPost="true"
                maxlength="3"
                onKeyUp="fieldChanged();"
                classNameBase="input"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Prenotabile da utenti azienda e lavoratore</td>
  <td class="campo">
  <af:comboBox name="flgPubblico"
               size="1"
               title="Prenotabile da utenti azienda e lavoratore"
               required="true"
               moduleName=""
               selectedValue=""
               addBlank="true"
               classNameBase="input"
               onChange="fieldChanged()"
               blankValue="">
    <option value="S">S&igrave;</option>
    <option value="N">No</option>
  </af:comboBox>
  </td>
</tr>
<tr>
  <td class="etichetta">Dalle</td>
  <td class="campo">
  <af:textBox name="STRORADALLE"
              value=""
              size="6"
              maxlength="5"
              title="Dalle"
              type="time"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              required="true"
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Fino Alle</td>
  <td class="campo">
  <af:textBox name="STRORAALLE"
              value=""
              size="6"
              maxlength="5"
              title="Alle"
              type="time"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              required="true"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Alle" onClick="calcola(1)">
  </td>
</tr>
<tr>
  <td class="etichetta">Minuti</td>
  <td class="campo">
  <af:textBox name="NUMMINUTI"
              value=""
              size="6"
              maxlength="3"
              title="Minuti"
              type="integer"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              required="true"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Minuti" onClick="calcola(2)">
  </td>
</tr>
<tr>
  <td class="etichetta">Quantit&agrave;</td>
  <td class="campo">
  <af:textBox name="NUMQTA"
              value=""
              size="6"
              maxlength="3"
              title="Quantità"
              type="integer"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              required="true"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Quantit&agrave;" onClick="calcola(3)">
  </td>
</tr>
<tr>
  <td class="etichetta">Stato Slot</td>
  <td class="campo">
    <af:comboBox name="codStatoSlot"
                 size="1"
                 title="Stato iniziale degli Slot"
                 multiple="false"
                 required="true"
                 focusOn="false"
                 moduleName="COMBO_STATO_SLOT_SETTIMANA_TIPO"
                 addBlank="true"
                 blankValue=""
    /> 
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  	<input type="submit" class="pulsanti" value="Inserisci">
    &nbsp;&nbsp;
	<input type="button" class="pulsanti" value="Chiudi senza inserire" onClick="chiudi()">
  </td>
</tr>
</af:form>
</table>
<%out.print(htmlStreamBottom);%>



</body>
</html>

