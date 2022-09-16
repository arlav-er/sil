<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

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


   //prelevo la funzione che devo chiamare per aggiornare la pagina
   String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");
   String funzioneChiudi = (String) StringUtils.getAttributeStrNotNull(serviceRequest,"CHIUDI_FUNZ");
   
   //prelevo gli eventuali parametri presenti  (tasto: torna alla ricerca)
   String codTipoAzienda= StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoAzienda");
   String codNatGiuridica= StringUtils.getAttributeStrNotNull(serviceRequest, "codNatGiuridica");
   String strPartitaIva= StringUtils.getAttributeStrNotNull(serviceRequest, "piva");
   String strCodiceFiscale=StringUtils.getAttributeStrNotNull(serviceRequest, "cf");
   String strRagioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSociale");


   //Oggetti per l'applicazione dello stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
	<head>
		<title>Ricerca Azienda</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
   <af:linkScript path="../../js/"/>    
    <script language="Javascript">

    //Prosegue la ricerca se ho già i progressivi dell'azienda o il cdnLavoratore


  function checkCampiObbligatori()
  { 
        if( ((document.Form1.cf.value != null) && (document.Form1.cf.value.length >= 6)) 
            || (document.Form1.ragioneSociale.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri della ragione sociale");
        return false;
   }

  </script>

	</head>
	<body class="gestione" <%=((funzioneChiudi!=null) && (!(funzioneChiudi.equals("")))?"onUnload=\"window.opener."+funzioneChiudi+"();\"":"")%>>
		<p class="titolo">Ricerca Azienda</p>
		<p align="center">
		<af:form name="Form1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
    <input type="hidden" name="PAGE" value="CommonListaUnitaAziendePage"/>
    <!--Passo l'informazione su che tipo di ricerca dovrò eseguire e sulla funzione
    da chiamare al ritorno-->
    <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <input type="hidden" name="CHIUDI_FUNZ" value="<%=funzioneChiudi%>"/>    
    <%out.print(htmlStreamTop);%>

    <table class="main">
    
    <!--Nel caso che il soggetto sia Azienda mostro la ricerca sulle aziende-->
            <tr valign="top">
              <td class="etichetta">Tipo azienda</td>
              <td class="campo">
                <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo Azienda" moduleName="M_GETTIPIAZIENDA" addBlank="true" selectedValue="<%=codTipoAzienda%>"/>
              </td>
            </tr>
            <tr valign="top">
              <td class="etichetta">Natura azienda</td>
              <td class="campo">
                <af:comboBox classNameBase="input" name="codNatGiuridica" title="Natura Giuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" selectedValue="<%=codNatGiuridica%>" />
              </td>
            </tr>
            <tr>
              <td class="etichetta">Partita IVA</td>
              <td class="campo">
                <af:textBox type="text" name="piva" validateOnPost="true" value="<%=strPartitaIva%>" size="30" maxlength="11"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="cf" validateOnPost="true" value="<%=strCodiceFiscale%>" size="30" maxlength="16"/>
              </td>
            </tr>            
            <tr>
              <td class="etichetta">Ragione Sociale</td>
              <td class="campo">
                <af:textBox type="text" name="ragioneSociale" validateOnPost="true" value="<%=strRagioneSociale%>" size="30" maxlength="100"/>                
              </td>
            </tr>
            <tr><td class="etichetta">&nbsp;</td></tr>
    

    <!--Inserisco i pulsanti necessari-->
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
    <%out.print(htmlStreamBottom);%>
    </af:form>

	</body>
</html>
