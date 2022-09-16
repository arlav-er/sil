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
   //prelevo la funzione che devo chiamare per aggiornare la pagina
   String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");
   String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
   String soggetto = (String) serviceRequest.getAttribute("MOV_SOGG");
   String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZ");
   String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUAZ");
   String cdnLav = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAV");
   //Oggetti per l'applicazione dello stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   boolean prosegui = false;
   if ((prgAzienda != null && !prgAzienda.equals("") && prgUnita != null && !prgUnita.equals(""))
        || (cdnLav != null && !cdnLav.equals(""))) {
    prosegui = true;
   }
%>

<html>
	<head>
		<title>Ricerca per <%=soggetto%></title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
   <af:linkScript path="../../js/"/>    
    <script language="Javascript">

    //Prosegue la ricerca se ho già i progressivi dell'azienda o il cdnLavoratore
    function prosegui() {
      var datiOk = controllaFunzTL();
      if (datiOk) { doFormSubmit(document.Form1); }
    }

  function checkCampiObbligatori()
  { 
    <%if (soggetto.equals("Aziende")||soggetto.equals("Testate")) {%>
        if ((document.Form1.ragioneSociale.value.length > 0 ) || (document.Form1.cf.value.length > 0) || 
            (document.Form1.piva.value.length > 0)) {
          return true;
        }
        alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n");
        return false;
      <% } else {%>
        if( ((document.Form1.strCodiceFiscale_ric.value != null) && (document.Form1.strCodiceFiscale_ric.value.length >= 6)) 
            || (document.Form1.strCognome_ric.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
    <% } %>
   }

  </script>

	</head>
	<body class="gestione" onload="<%=(prosegui ? "prosegui();" : "")%>">
		<p class="titolo">Ricerca per <%=soggetto%></p>
		<p align="center">
		<af:form name="Form1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
    <input type="hidden" name="PAGE" value="ProspettiListaSoggettoPage"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>

    <!--Passo l'informazione su che tipo di ricerca dovrò eseguire e sulla funzione
    da chiamare al ritorno-->
    <input type="hidden" name="MOV_SOGG" value="<%=soggetto%>"/>
    <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <input type="hidden" name="CONTESTOPROVENIENZA" value="MOVIMENTI"/>
    <input type="hidden" name="PRGAZ" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUAZ" value="<%=prgUnita%>"/>
    <input type="hidden" name="CDNLAV" value="<%=cdnLav%>"/>
    <%out.print(htmlStreamTop);%>
    <table class="main">
    
    <!--Nel caso che il soggetto sia Azienda mostro la ricerca sulle aziende-->
    <% if (soggetto.equals("Aziende")|| soggetto.equals("Testate")) {%>
            <tr valign="top">
              <td class="etichetta">Tipo azienda</td>
              <td class="campo">
                <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo Azienda" moduleName="M_GETTIPIAZIENDA" addBlank="true" />
              </td>
            </tr>
            <tr valign="top">
              <td class="etichetta">Natura azienda</td>
              <td class="campo">
                <af:comboBox classNameBase="input" name="codNatGiuridica" title="Natura Giuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" />
              </td>
            </tr>
            <tr>
              <td class="etichetta">Partita IVA</td>
              <td class="campo">
                <af:textBox type="text" name="piva" validateOnPost="true" value="" size="30" maxlength="11"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="cf" validateOnPost="true" value="" size="30" maxlength="16"/>
              </td>
            </tr>            
            <tr>
              <td class="etichetta">Ragione Sociale</td>
              <td class="campo">
                <af:textBox type="text" name="ragioneSociale" validateOnPost="true" value="" size="30" maxlength="100"/>                
              </td>
            </tr>
            
    <!--Nel caso che il soggetto sia Lavoratore mostro la ricerca sui lavoratori-->
    <%} else if (soggetto.equals("Lavoratori")) {%>
            <tr>
              <tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
              <tr><td colspan="2"/>&nbsp;</td></tr>
              <td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="strCodiceFiscale_ric" validateOnPost="true" value="" size="30" maxlength="16"/> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Cognome</td>
              <td class="campo">
                <af:textBox type="text" name="strCognome_ric" validateOnPost="true" value="" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Nome</td>
              <td class="campo">
                <af:textBox type="text" name="strNome_ric" validateOnPost="true" value="" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Tipo ricerca</td>
              <td class="campo">
              <table colspacing="0" colpadding="0" border="0">
              <tr>
               <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
               <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
              </tr>
              </table>
              </td>
            </tr>
            <tr><td class="etichetta">&nbsp;</td></tr>
            
    <!--Nel caso che il soggetto sia Mansioni mostro la ricerca sulle mansioni-->
    <%} else if (soggetto.equals("Mansioni")) {%>
            <tr>
              <td class="etichetta">Mansione</td>
              <td class="campo">
                <af:textBox type="text" name="nomeMansione" validateOnPost="true" value="" size="30" maxlength="30"/>                
              </td>
            </tr>   
    <%}%>
    
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
		</center>
	</body>
</html>
