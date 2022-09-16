<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="cal" prefix="cal" %>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String streamDescrCpi = "";
SourceBean content = (SourceBean) serviceResponse.getAttribute("MDESCRCPI");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { streamDescrCpi = (String) row.getAttribute("STRDESCRIZIONE"); }
}

String codCpi = (String) serviceRequest.getAttribute("CODCPI");
/*int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String queryString = null;

String labelServizio = "Servizio";
String labelServizi = "Servizi";

String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
	labelServizi = "Aree";
}
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>scelta_stampa_giornaliera.jsp</title>
  <af:linkScript path="../../js/" />
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>    

  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
    var d_giorni = new Date();
    var vettGiorniMesi=new Array(12);
    vettGiorniMesi[0] = 31;
    annoCorrente = d_giorni.getYear();
    if (annoCorrente%4 == 0) {
      vettGiorniMesi[1] = 29;
    }
    else {
      vettGiorniMesi[1] = 28;
    }
    vettGiorniMesi[2] = 31;
    vettGiorniMesi[3] = 30;
    vettGiorniMesi[4] = 31;
    vettGiorniMesi[5] = 30;
    vettGiorniMesi[6] = 31;
    vettGiorniMesi[7] = 31;
    vettGiorniMesi[8] = 30;
    vettGiorniMesi[9] = 31;
    vettGiorniMesi[10] = 30;
    vettGiorniMesi[11] = 31;
    
    function DisabilitaDate(tipoDisabilitazione) {
      var d = new Date();
      document.Frm1.DATDAL.disabled = true;
      document.Frm1.DATAL.disabled = true;
      switch (tipoDisabilitazione) {
        case 1:
          
          annoOdierno = d.getFullYear().toString();
          meseOdierno = (d.getMonth() + 1).toString();
          if (meseOdierno.length == 1) {
            meseOdierno = '0' + meseOdierno;
          }
          giornoOdierno = d.getDate().toString();
          if (giornoOdierno.length == 1) {
            giornoOdierno = '0' + giornoOdierno;
          }
          document.Frm1.DATDAL.value = giornoOdierno + "/" + meseOdierno + "/" + annoOdierno;
          document.Frm1.DATAL.value = giornoOdierno + "/" + meseOdierno + "/" + annoOdierno;
          break;

        case 2:
          annoOdierno = d.getFullYear();
          meseOdierno = d.getMonth();
          giornoOdierno = d.getDate();
          giornoSettimana = d.getDay();
          
          giorniPrecedenti = giornoSettimana - 1;
          giorniSuccessivi = 7 - giornoSettimana;

          if (giorniSuccessivi == 0) {
            annoFineSett = annoOdierno;
            meseFineSett = meseOdierno + 1;
            giornoFineSett = giornoOdierno;
          }
          else {
          
            giornoFineSett = giornoOdierno + giorniSuccessivi;
          
            if (giornoFineSett > vettGiorniMesi[meseOdierno]) {
              if (meseOdierno == 11) {
                annoFineSett = annoOdierno + 1;
                meseFineSett = 1;
                giornoFineSett = 0 + giorniSuccessivi - (vettGiorniMesi[meseOdierno] - giornoOdierno);
              }
              else {
                annoFineSett = annoOdierno;
                meseFineSett = meseOdierno + 2; //mese va da 0 a 11
                giornoFineSett = 0 + giorniSuccessivi - (vettGiorniMesi[meseOdierno] - giornoOdierno);
              }
            }
            else {
              annoFineSett = annoOdierno;
              meseFineSett = meseOdierno + 1; //mese va da 0 a 11
            }
          }
  
          if (giornoFineSett < 10) {
            strGiornoFineSett = "0" + giornoFineSett.toString();
          }
          else {
            strGiornoFineSett = giornoFineSett.toString();
          }
          
          if (meseFineSett < 10) {
            strMeseFineSett = "0" + meseFineSett.toString();
          }
          else {
            strMeseFineSett = meseFineSett.toString();
          }
  
          document.Frm1.DATAL.value = strGiornoFineSett + "/" + strMeseFineSett + "/" + annoFineSett;

          giornoInizioSett = giornoOdierno - giorniPrecedenti;
          
          if (giornoInizioSett < 1) {
            if (meseOdierno == 0) {
              annoInizioSett = annoOdierno - 1;
              meseInizioSett = 12;
              giornoInizioSett = parseInt(vettGiorniMesi[11],10) - giorniPrecedenti + giornoOdierno;
            }
            else {
              annoInizioSett = annoOdierno;
              meseInizioSett = meseOdierno; //mese va da 0 a 11
              giornoInizioSett = vettGiorniMesi[meseOdierno - 1] - giorniPrecedenti + giornoOdierno;
            }
          }
          else {
            annoInizioSett = annoOdierno;
            meseInizioSett = meseOdierno + 1; //mese va da 0 a 11
          }
  
          if (giornoInizioSett < 10) {
            strGiornoInizioSett = "0" + giornoInizioSett.toString();
          }
          else {
            strGiornoInizioSett = giornoInizioSett.toString();
          }
          
          if (meseInizioSett < 10) {
            strMeseInizioSett = "0" + meseInizioSett.toString();
          }
          else {
            strMeseInizioSett = meseInizioSett.toString();
          }
  
          document.Frm1.DATDAL.value = strGiornoInizioSett + "/" + strMeseInizioSett + "/" + annoInizioSett;

          break;

        case 3:
          annoOdierno = d.getFullYear().toString();
          meseOdierno = (d.getMonth() + 1).toString();
          if (meseOdierno < 10) {
            strMeseOdierno = "0" + meseOdierno.toString();
          }
          else {
            strMeseOdierno = meseOdierno.toString();
          }
          document.Frm1.DATDAL.value = "01/" + strMeseOdierno + "/" + annoOdierno;
          document.Frm1.DATAL.value = vettGiorniMesi[d.getMonth()] + "/" + strMeseOdierno + "/" + annoOdierno;
          break;
      }
    }
  
    function AbilitaCampiData() {
      document.Frm1.DATDAL.disabled = false;
      document.Frm1.DATAL.disabled = false;
    }

        
    function ControllaData() {
      var bReturnOK = false;
      var dataDalCerca = "";
      var dataAlCerca = "";
      var dataDalCercaInt = 0;
      var dataAlCercaInt = 0;

      //var datDal = document.Frm1.DATDAL;
      //var datAl  = document.Frm1.DATAL;
      
      if(checkFormatDate(document.Frm1.DATDAL))
      {
      if (checkFormatDate(document.Frm1.DATAL))      
      { dataDalCerca = new String(document.Frm1.DATDAL.value);
        dataAlCerca  = new String(document.Frm1.DATAL.value);
      
        if (dataDalCerca != "" && dataAlCerca != "") {

          annoDataDal = dataDalCerca.substr(6,4);
          meseDataDal = dataDalCerca.substr(3,2);
          giornoDataDal = dataDalCerca.substr(0,2);
          dataDalCercaInt = parseInt(annoDataDal + meseDataDal + giornoDataDal,10);

          annoDataAl = dataAlCerca.substr(6,4);
          meseDataAl = dataAlCerca.substr(3,2);
          giornoDataAl = dataAlCerca.substr(0,2);
          dataAlCercaInt = parseInt(annoDataAl + meseDataAl + giornoDataAl,10);            

          if (dataDalCercaInt > dataAlCercaInt) {
            alert ("Data Dal deve essere minore o uguale a Data Al");
            bReturnOK = false;
          }
          else {
            bReturnOK = true;
          }
        } 
        else {
          alert ("Inserire entrambi i campi \'Dal giorno\' e \'Al giorno\'");
          bReturnOK = false;
        }

        }
        else 
        { alert("La data in \'Al giorno\' non è corretta");
          bReturnOK = false;
        }
        }
        else
        { alert("La data in \'Dal giorno\' non è corretta");
          bReturnOK = false;
        }

      return bReturnOK;
    }
  
function avviaStampa() {

 var result=checkCampi();
 if( result==false ){
 	return;
 }

 if(ControllaData()) 
  { var prgSpi = document.Frm1.PRGSPI.value;
    var ragg   = document.Frm1.RAGG.value;
    var datDal = document.Frm1.DATDAL.value;
    var datAl  = document.Frm1.DATAL.value;
    var codCpi = document.Frm1.CODCPI.value;
    var codSerivzio = document.Frm1.CODSERVIZIO.value;
    apriGestioneDoc('RPT_AGENDA_PROMEMAPP','&PRGSPI='+prgSpi + '&DATDAL='+datDal + '&DATAL='+datAl + '&RAGG='+ragg + '&CODCPI='+codCpi + '&CODSERVIZIO='+codSerivzio,'PRM');
  }
}

function controllaRagg()
{ var ragg="";
  if(document.Frm1.RAGG.value == "")
  {alert("Il campo \'Raggruppa per\' è obbligatorio");
   return false;
  }
  return true;
}
var isUmbria = <%=umbriaGestAz%>;
function checkCampi(){
	if ( (document.Frm1.PRGSPI.value==null || document.Frm1.PRGSPI.value=="") && 
		 (document.Frm1.CODSERVIZIO.value==null || document.Frm1.CODSERVIZIO.value=="") && 
		 (document.Frm1.RAGG.value==null || document.Frm1.RAGG.value=="") ){
		
		if(isUmbria == '0'){
			alert("Scegliere o un operatore o un servizio o un raggruppamento");
		}else{
			alert("Scegliere o un operatore o un'area o un raggruppamento");
		}
		
		return false;
	}
	return true;
}

  </script>
</head>

<body class="gestione">
<br/><br/>
<p class="titolo">Stampa appuntamento giornaliero</p>
<p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" action="AdapterHTTP" method="POST">
<!--input type="hidden" name="PAGE" value="AGENDA_STAMPA_GIORNALIERA_PAGE"-->
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="DATAEDITABILE" value="false">

<table class="main" align="center">
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPI" size="1" title="Operatore"
                     multiple="false" disabled="false" required="false"
                     focusOn="false" moduleName="COMBO_SPI"
                     selectedValue="" addBlank="true" blankValue=""/>
    </td>
</tr>

<tr>
  <td class="etichetta"><%= labelServizi %></td>
  <td class="campo">
      <af:comboBox name="CODSERVIZIO" size="1" title="<%= labelServizi %>"
                     multiple="false" disabled="false" required="false"
                     focusOn="false" moduleName="COMBO_SERVIZIO"
                     selectedValue="" addBlank="true" blankValue=""/>
    </td>
</tr>

<tr>
  <td class="etichetta">Raggruppa per </td>
  <td class="campo">
      <SELECT name="RAGG">
        <OPTION value=""></OPTION>      
        <OPTION value="AMB">Ambiente</OPTION>
        <OPTION value="SER"><%=labelServizio %></OPTION>        
        <OPTION value="OPE">Operatore</OPTION>
      </SELECT>
  </td>
</tr>

<tr>
  <td class="etichetta"><input type="radio" name="sceltaPeriodo" onclick="DisabilitaDate(1);" checked></td>
  <td class="campo">
    <font type="etichetta">oggi</font>     
  </td>
</tr>

<tr>
  <td class="etichetta"><input type="radio" name="sceltaPeriodo" onclick="DisabilitaDate(2);"></td>
  <td class="campo">
    <font type="etichetta">questa settimana</font>
  </td>
</tr>

<tr>
  <td class="etichetta"><input type="radio" name="sceltaPeriodo" onclick="DisabilitaDate(3);"></td>
  <td class="campo">
    <font type="etichetta">mese corrente</font>
  </td>
</tr>

<tr>
  <td class="etichetta"><input type="radio" name="sceltaPeriodo" onclick="AbilitaCampiData();">&nbsp;
  Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="DATDAL" title="Data dal" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    <font type="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAL" title="Data al" value="" size="12" maxlength="10" validateOnPost="true"/>
  </td>
</tr>

<tr><td>&nbsp</td></tr>

<tr>
  <td colspan="2">
		 <center><input class="pulsante" type="button" name="btnChiudi" value="Annulla" onClick="javascript:window.close();"/>
		 <input class="pulsante" type="button" name="btnInvio" value="Stampa" onClick="avviaStampa()"/></center>     
  </td>
</tr>
</table>

</af:form>
<%out.print(htmlStreamBottom);%>
</body>
<script language="javascript">
  DisabilitaDate(1);
</script>
</html>