<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
// Debug
//String qs = QueryString.GetQueryString((SourceBean) serviceRequest);
%>

<%
String giornoDB = "";
String meseDB = "";
String annoDB = "";
String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";
int mod = 2;

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("cod_vista")) { cod_vista = serviceRequest.getAttribute("cod_vista").toString(); }
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }
 
String codCpi  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI");
int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
if (codCpi.equalsIgnoreCase("")) {
	if(cdnTipoGruppo==1) { 
		codCpi =  user.getCodRif(); 
	}
	if(cdnTipoGruppo!=1 || codCpi.equalsIgnoreCase("") || codCpi==null ) { 
		// PAGINA_DI_ERRORE
		//response.sendRedirect("../../servlet/fv/AdapterHTTP?PAGE=SelezionaCPIPage");
	}
}

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>


<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
  <title>Ricerca Appuntamenti</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
    function controllaDate()
    {
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var di = document.formCerca.dataDal.value;
      var df = document.formCerca.dataAl.value;
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
        dataI = parseInt(a + m + g, 10);
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
        dataF = parseInt(a + m + g, 10);
      }
  
      if(ok1 && ok2) {
        if(dataI <= dataF) { return(true); }
        else { 
          alert("La data di inizio (data dal) deve essere minore o uguale alla data di termine (data al)."); 
          return(false);
        }
      } else {
        return(true);
      }
    }
  </script>
  <script type="text/javascript">
  
  function controllaDateRange(di, df, range){
    var dataI = "";
    var dataF = "";
    var ONE_DAY = 1000 * 60 * 60 * 24;

    if (di != "") {
      dataI = new String(di);
      var annoDataDal = dataI.substr(6,4);
      var meseDataDal = dataI.substr(3,2);
      var giornoDataDal = dataI.substr(0,2);
    } else {
    	return false;
    }

    if (df != "") {
      dataF = new String(df);
      var annoDataAl = dataF.substr(6,4);
      var meseDataAl = dataF.substr(3,2);
      var giornoDataAl = dataF.substr(0,2);
    } else {
    	return false;
    }

    var dataDal = new Date(annoDataDal, meseDataDal-1, giornoDataDal);
	var dataAl = new Date(annoDataAl, meseDataAl-1, giornoDataAl);

	var dataDal_ms = dataDal.getTime();
    var dataAl_ms = dataAl.getTime();
    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
    var delta = 1 + Math.round(difference_ms/ONE_DAY);   

	if (delta > range) {
		return false;
	}
	
    return true;
  }
  	
    function controllaParametri()
    {
    	var msg;
	    var sel_op = document.formCerca.sel_operatore.value;
	    var sel_serv = document.formCerca.sel_servizio.value;
	    var lav_cognome = document.formCerca.strCognome.value;
	    var lav_cf = document.formCerca.strCodiceFiscale.value;
	    var az_cf= document.formCerca.strCodiceFiscaleAz.value;
	    var az_piva = document.formCerca.piva.value;
	    var az_ragsoc = document.formCerca.strRagSoc.value;
	    var di = document.formCerca.dataDal.value;
        var df = document.formCerca.dataAl.value;

	    //alert("lav_cognome = " + lav_cognome + "\r\nlav_cf = " + lav_cf +
		//	  "\r\naz_cf = " + az_cf + "\r\naz_piva = " + az_piva + "\r\naz_ragsoc = " + az_ragsoc +
	    //	  "\r\nsel_op = " + sel_op + "\r\nsel_serv = " + sel_serv +
	    //	  "\r\ndi = " + di + "\r\ndf = " + df);

	    // caso 1
	    if ((lav_cognome!="") || (lav_cf!="") || (az_cf!="") || (az_piva!="") || (az_ragsoc!="")) {
	    	return (true);
	    }
	    // caso 2/3
	    if ((sel_op!="") || (sel_serv!="")) {
	    	ok = controllaDateRange(di,df,90);
	    } else { // caso 4
	    	ok = controllaDateRange(di,df,30);
	    }
	    if (ok) return (true);

		msg = "Parametri generici.\n"+
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore (CF o Cognome) / Azienda.\n"+
			"OPZIONE 2 - Tutti i seguenti valori : Operatore / Periodo max 90 gg.\n"+
			"OPZIONE 3 - Tutti i seguenti valori : Servizio / Periodo max 90 gg.\n"+
			"OPZIONE 4 - Periodo max 30 gg.";

	    alert(msg);
	    return (false);
    }
  </script>
  
</head>

<body class="gestione">
<br>
<p class="titolo">Ricerca Appuntamento</p>

<%out.print(htmlStreamTop);%>
  <p align="justify">
  <b>Attenzione: gli eventuali filtri attivi non verrano utilizzati nella ricerca.</b>
  </p>

<af:form action="AdapterHTTP" name="formCerca" method="GET" onSubmit="controllaDate() && controllaParametri()">

<input name="PAGE" type="hidden" value="PCalendario"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="MOD" type="hidden" value="2"/>
<input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

<div class="sezione">Lavoratore</div>
<table class="main">
<tr>
  <td class="etichetta">Codice Fiscale</td>
  <td class="campo">
    <input type="text" name="strCodiceFiscale" value="" size="20" maxlength="16"/>
  </td>
</tr>
<tr>
    <td class="etichetta">Cognome</td>
    <td class="campo"><input type="text" name="strCognome" value="" size="20" maxlength="50"/></td>
</tr>
<tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><input type="text" name="strNome" value="" size="20" maxlength="50"/></td>
</tr>
</table>
<div class="sezione">Azienda</div>
<table class="main">
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo">
          <input type="text" name="strCodiceFiscaleAz" value="" size="20" maxlength="16"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Partita IVA</td>
        <td class="campo">
          <input type="text" name="piva" value="" size="20" maxlength="11"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ragione Sociale</td>
        <td class="campo">
          <input type="text" name="strRagSoc" value="" size="20" maxlength="50"/>
        </td>
      </tr>
</table>
<div class="sezione">Parametri</div>
<table class="main">
<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo">
    <af:comboBox name="sel_operatore"
                 size="1"
                 title="Scelta Operatore"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SPI"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr>
	<%String titleServizio = "Scelta "+labelServizio; %>
	
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
    <af:comboBox name="sel_servizio"
                 size="1"
                 title="<%= titleServizio %>"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SERVIZIO"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Ambiente/Aula</td>
  <td class="campo">
    <af:comboBox name="sel_aula"
                 size="1"
                 title="Scelta Ambiente/Aula"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_AMBIENTE"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Esito</td>
  <td class="campo">
    <af:comboBox name="esitoApp"
                 size="1"
                 title="Esito appuntamento"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_ESITO_APPUNTAMENTO"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Appuntamento prenotato da ANPAL</td>
  <td class="campo">
     <input type="checkbox" name="strIdCoap" value="si">
</td>	
</tr>
</table>
<div class="sezione">Periodo</div>
<table class="main">
<tr>
  <td class="etichetta">Data Dal</td>
  <td class="campo">
  <af:textBox name="dataDal" title="Data Dal"
              type="date"
              size="10"
              maxlength="10"
              required="false"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Data Al</td>
  <td class="campo">
  <af:textBox name="dataAl" title="Data Al"
              type="date"
              size="10"
              maxlength="10"
              required="false"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="submit" class="pulsanti" value="Cerca">
  &nbsp;&nbsp;
  <input type="reset" class="pulsanti" value="Annulla">
  </td>
</tr>
</table>
</af:form>



<af:form name="formBack" action="AdapterHTTP" method="POST" dontValidate="true">
  <input name="PAGE" type="hidden" value="PCalendario"/>
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
              <input name="esitoApp" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp")%>"/>
              <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
              <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
              <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
              <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
              <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
              <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
              <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
            <%} else  { %>
               <%if(mod==3) {%>
	              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
	              <input name="sel_tipo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_tipo")%>"/>
	              <input name="STRIO" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"STRIO")%>"/>
	              <input name="sel_motivo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_motivo")%>"/>
	              <input name="effettoCon" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"effettoCon")%>"/>
	              <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
	              <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
	              <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
                  <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
                  <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
	              <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
	              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
	              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
                  <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
 			   <% } %>
 		   <% } %>
        <% } %>
  <% } %>
  <input name="giorno" type="hidden" value="<%=giorno%>"/>
  <input name="mese" type="hidden" value="<%=mese%>"/>
  <input name="anno" type="hidden" value="<%=anno%>"/>
  <input name="MOD" type="hidden" value="<%=mod%>"/>
  <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
  <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>
  <p align="center"><input type="submit" class="pulsanti" value="Chiudi"/></p>
</af:form>
<%out.print(htmlStreamBottom);%>
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
