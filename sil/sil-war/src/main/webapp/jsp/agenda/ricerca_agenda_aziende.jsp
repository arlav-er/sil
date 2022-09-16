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
		<title>Ricerca sulle Aziende</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/" />
	<script language="Javascript">
	function checkCampiObbligatori()
      { 
        if ((document.forms[0].RagioneSociale.value.length > 0 ) || 
            (document.forms[0].cf.value.length > 0) || 
            (document.forms[0].piva.value.length > 0) ) {
          return true;
        }
        alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n");
        return false;
      }
	</script>
	
	
</head>
<body class="gestione">
		<p class="titolo">Ricerca sulle Aziende</p>
		<%out.print(htmlStreamTop);%>
		<af:form method="POST" action="AdapterHTTP" dontValidate="true" onSubmit="checkCampiObbligatori()">
    <input type="hidden" name="PAGE" value="PListaAziende"/>
    <%if(PROV.equals("CONTATTI")) {%>
      <input type="hidden" name="PRGAPPUNTAMENTO" value="<%=prg%>"/>
    <%} else {%>
      <input type="hidden" name="PRGCONTATTO" value="<%=prg%>"/>
    <%} %>
    <input type="hidden" name="CODCPI" value="<%=codcpi%>"/>
    <input type="hidden" name="PROV" value="<%=PROV%>"/>

    <input name="MOD" type="hidden" value="<%=mod%>"/>
    <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
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
            <input name="strRagSoc" type="hidden" value="<%=strRagSoc%>"/>
            <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
            <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
            <input name="strNome" type="hidden" value="<%=strNome%>"/>
            <input name="mese" type="hidden" value="<%=mese%>"/>
            <input name="anno" type="hidden" value="<%=anno%>"/>
            <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
            <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
      <%}%>
    <%}%>

      <table class="main"> 
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo">
          <input type="text" name="cf" value="" size="20" maxlength="16"/>
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
          <input type="text" name="RagioneSociale" value="" size="20" maxlength="100"/>
        </td>
      </tr>

      <tr>
        <td class="etichetta">Provincia</td>
        <td class="campo">
          <!--<af:comboBox classNameBase="input" title="Tipo di Ricerca" name="tipoRicerca">
            <OPTION value="S">della sede legale</OPTION>
            <OPTION value="U">di tutte le unità</OPTION>
          </af:comboBox>&nbsp; -->
          <af:comboBox classNameBase="input" title="Provincia" name="codProvincia" moduleName="M_GetIDOProvince" addBlank="true" />
        </td>
      </tr>


          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
            <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
            &nbsp;&nbsp;
            <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
            </td>
          </tr>
         
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
          </td>
        </tr>
        </table>
        </af:form>
		<%out.print(htmlStreamBottom);%>
	</body>
</html>

