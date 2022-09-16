<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  String PROV =  StringUtils.getAttributeStrNotNull(serviceRequest, "PROV");
  String prg = "";

  if(PROV.equals("CONTATTI")) {
    prg = serviceRequest.getAttribute("PRGCONTATTO").toString();
  } else {
    prg = serviceRequest.getAttribute("PRGAPPUNTAMENTO").toString();
  }
  String codcpi=serviceRequest.getAttribute("CODCPI").toString();

  //String azienda=(String)serviceResponse.getAttribute("RICERCA_AGENDA_LAVORATORI_MOD.AZIENDA");
  String azienda = "N";
  String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
%>

<%
    String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
    String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
    if(mod.equals("")) { mod = "0"; }
    
    String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
    String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
    String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
    String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
    String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
    String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");
    
    
    String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String esitoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp");
    String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
    String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
    String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
    String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    String piva = StringUtils.getAttributeStrNotNull(serviceRequest, "piva");
    String strCodiceFiscaleAz= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleAz");
    
    String OLD_STATO = StringUtils.getAttributeStrNotNull(serviceRequest,"OLDSTATO");
    String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
    
    String prgSpi = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGSPI");
    String prgSpiEff = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGSPIEFF");
    String codServizio = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSERVIZIO");
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
		<title>Ricerca Lavoratori</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/" />
	<script language="Javascript">
	function checkCampiObbligatori()
      { if( ((document.forms[0].strCodiceFiscale_ric.value != null) && (document.forms[0].strCodiceFiscale_ric.value.length >= 6))      		 
            || (document.forms[0].strCognome_ric.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
      }
	</script>
	
</head>
<body class="gestione">
		<p class="titolo">Ricerca Lavoratori</p>
		<%out.print(htmlStreamTop);%>
			<af:form method="POST" action="AdapterHTTP" dontValidate="true" onSubmit="checkCampiObbligatori()">

      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
      <input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>
      <input name="PRGSPI" type="hidden" value="<%=prgSpi%>"/>
      <input name="PRGSPIEFF" type="hidden" value="<%=prgSpiEff%>"/>
      <input name="CODSERVIZIO" type="hidden" value="<%=codServizio%>"/>
      <%if(mod.equals("0")) {%>
          <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
          <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
          <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
          <input name="giorno" type="hidden" value="<%=giorno%>"/>
          <input name="mese" type="hidden" value="<%=mese%>"/>
          <input name="anno" type="hidden" value="<%=anno%>"/>
      <%} else {%>
        <%if(mod.equals("2")) {%>
              <input name="sel_operatore" type="hidden" value="<%=sel_operatore%>"/>
              <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
              <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
              <input name="esitoApp" type="hidden" value="<%=esitoApp%>"/>
              <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
              <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
              <input name="strNome" type="hidden" value="<%=strNome%>"/>
              <input name="mese" type="hidden" value="<%=mese%>"/>
              <input name="anno" type="hidden" value="<%=anno%>"/>
              <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
              <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
              <input name="piva" type="hidden" value="<%=piva%>"/>
		      <input name="strCodiceFiscaleAz" type="hidden" value="<%=strCodiceFiscaleAz%>"/>
        <%}%>
      <%}%>

      
      <table class="main"> 
        <%if (azienda=="S"){%>
          <tr><td colspan="2">E' già stata inserita un'azienda</td></tr>
        <%}else{%>       
          <tr>
            <td class="etichetta">Codice Fiscale</td>
            <td class="campo">
              <input type="text" name="strCodiceFiscale_ric" value="" size="20" maxlength="16"/>
            </td>
          </tr>
          <tr>
            <td class="etichetta">Cognome</td>
            <td class="campo"><input type="text" name="strCognome_ric" value="" size="20" maxlength="50"/></td>
          </tr>
          <tr>
            <td class="etichetta">Nome</td>
            <td class="campo"><input type="text" name="strNome_ric" value="" size="20" maxlength="50"/></td>
          </tr>

          <tr>
            <td class="etichetta">tipo ricerca</td>
            <td class="campo">
            <table cellspacing="0" cellpadding="0" border="0">
            <tr>
             <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
             <td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td>
            </tr>
            </table>
            </td>
          </tr>

          <input type="hidden" name="PAGE" value="LISTA_AGENDA_LAVORATORI_PAGE"/>
          <input type="hidden" name="PRGAPPUNTAMENTO" value="<%=prg%>"/>
          <input type="hidden" name="CODCPI" value="<%=codcpi%>"/>
          <input type="hidden" name="OLDSTATO" value="<%=OLD_STATO%>"/>
          <input type="hidden" name="PROV" value="<%=PROV%>"/>
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
            <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
            &nbsp;&nbsp;
            <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
            </td>
          </tr>
          
        <%}%>      
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
          </td>
        </tr>
        </table>
        </af:form>
      <!--
      <br/><br/><br/>
      <af:form method="POST" action="AdapterHTTP" dontValidate="true">
      <input class="pulsanti" type="submit" name="inserisci" value="Inserisci un nuovo iscritto"/>
       <input type="hidden" name="NEW_SESSION" value="TRUE"/>
      <input type="hidden" name="PAGE" value="AnagDettaglioPage"/>
      <input type="hidden" name="flag_insert" value="1"/>
			</af:form>
      -->
		<%out.print(htmlStreamBottom);%>
	</body>
</html>

