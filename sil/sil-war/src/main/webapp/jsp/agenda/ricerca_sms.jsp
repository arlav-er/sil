<!-- @author: Giordano Gritti -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                 java.math.*"
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
  <title>Invio SMS di Promemoria: cerca appuntamento</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
    function controllaDate()
    {
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var di = document.frmCerca.dataDal.value;
      var df = document.frmCerca.dataAl.value;
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
  	var isUmbria = "<%=umbriaGestAz%>";
    function controllaParametri()
    {
    	var msg;
	    var sel_op = document.frmCerca.sel_operatore.value;
	    var sel_serv = document.frmCerca.sel_servizio.value;
	    var lav_cognome = document.frmCerca.strCognome.value;
	    var lav_nome = document.frmCerca.strNome.value;
	    var lav_cf = document.frmCerca.strCodiceFiscale.value;    
	    var di = document.frmCerca.dataDal.value;
        var df = document.frmCerca.dataAl.value;

	    //alert("lav_cognome = " + lav_cognome + "\r\nlav_cf = " + lav_cf +
		//	  "\r\naz_cf = " + az_cf + "\r\naz_piva = " + az_piva + "\r\naz_ragsoc = " + az_ragsoc +
	    //	  "\r\nsel_op = " + sel_op + "\r\nsel_serv = " + sel_serv +
	    //	  "\r\ndi = " + di + "\r\ndf = " + df);

	    // caso 1
	    if ((lav_cognome!="") || (lav_cf!="") || (lav_nome!="")) {
	    	return (true);
	    }
	    // caso 2/3
	    if ((sel_op!="") || (sel_serv!="")) {
	    	ok = controllaDateRange(di,df,90);
	    } else { // caso 4
	    	ok = controllaDateRange(di,df,30);
	    }
	    if (ok) return (true);
		var servArea = "Servizio";
		if(isUmbria == '1'){
			servArea = 'Area';
		}
		msg = "Parametri generici.\n"+
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore (CF o Cognome)\n"+
			"OPZIONE 2 - Tutti i seguenti valori : Operatore / Periodo max 90 gg.\n"+
			"OPZIONE 3 - Tutti i seguenti valori : "+servArea+" / Periodo max 90 gg.\n"+
			"OPZIONE 4 - Periodo max 30 gg.";

	    alert(msg);
	    return (false);
    }
  </script>
  
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Invio SMS di Promemoria: cerca appuntamento</p>

<%out.print(htmlStreamTop);%>
  

<af:form action="AdapterHTTP" name="frmCerca" method="GET" onSubmit="controllaDate() && controllaParametri() && controllaDateRange()">

<input name="PAGE" type="hidden" value="AGENDA_SMS_LISTALAVORATORI_PAGE"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="MOD" type="hidden" value="2"/>
<input name="CODCPI" type="hidden" value="<%=codCpi%>"/>


<div class="sezione">Centro per l'Impiego</div>
<table class="main">
<tr>
  <td class="etichetta">Centro per l'Impiego</td>
  <td class="campo">
    <af:comboBox name="sel_cpi"
                 size="1"
                 title="Scelta CPI"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="M_SMS_COMBO_CPI"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
</table>
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
<%String titleServ = "Scelta " + labelServizio; %>
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
    <af:comboBox name="sel_servizio"
                 size="1"
                 title="<%=titleServ %>"
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
                 moduleName="M_SMS_COMBO_AMBIENTE"
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
                 moduleName="M_SMS_COMBO_ESITO_APPUNTAMENTO"
                 addBlank="true"
                 blankValue=""
    />
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

<%out.print(htmlStreamBottom);%>
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
