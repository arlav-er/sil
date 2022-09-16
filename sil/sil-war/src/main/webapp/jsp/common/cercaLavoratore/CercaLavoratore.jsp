<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/Function_CommonRicercaComune.inc" %>
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
   String strCodiceFiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
   String strCognome= StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
   String strNome= StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
   String datnasc=StringUtils.getAttributeStrNotNull(serviceRequest, "datnasc");
   String codComNas=StringUtils.getAttributeStrNotNull(serviceRequest, "codComNas");
   String strComNas=StringUtils.getAttributeStrNotNull(serviceRequest, "strComNas");  
   String tipoRicerca=(serviceRequest.containsAttribute("tipoRicerca"))? (String) serviceRequest.getAttribute("tipoRicerca") : "esatta";
   
   //Oggetti per l'applicazione dello stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
   
%>

<html>
	<head>
		<title>Ricerca Lavoratore</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
   <af:linkScript path="../../js/"/>    
    <script language="Javascript">

    //Prosegue la ricerca se ho già i progressivi dell'azienda o il cdnLavoratore


  function checkCampiObbligatori()
  { 
        if( ((document.Frm1.strCodiceFiscale.value != null) && (document.Frm1.strCodiceFiscale.value.length >= 6)) 
            || (document.Frm1.strCognome.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
   }

  </script>

	</head>
	<body class="gestione" <%=((funzioneChiudi!=null) && (!(funzioneChiudi.equals("")))?"onUnload=\"window.opener."+funzioneChiudi+"();\"":"")%>>
		<p class="titolo">Ricerca Lavoratore</p>
		<p align="center">
		<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
    <input type="hidden" name="PAGE" value="CommonListaLavoratoriPage"/>
    <!--Passo l'informazione su che tipo di ricerca dovrò eseguire e sulla funzione
    da chiamare al ritorno-->
    <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <input type="hidden" name="CHIUDI_FUNZ" value="<%=funzioneChiudi%>"/>    
    <%out.print(htmlStreamTop);%>

    <table class="main">
    
    <!--Nel caso che il soggetto sia Azienda mostro la ricerca sulle aziende-->
            <tr>
              <tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
              <tr><td colspan="2"/>&nbsp;</td></tr>
              <tr><td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="strCodiceFiscale" validateOnPost="true" value="<%=strCodiceFiscale%>" size="30" maxlength="16"/> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Cognome</td>
              <td class="campo">
                <af:textBox type="text" name="strCognome" validateOnPost="true" value="<%=strCognome%>" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Nome</td>
              <td class="campo">
                <af:textBox type="text" name="strNome" validateOnPost="true" value="<%=strNome%>" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Tipo ricerca</td>
              <td class="campo">
              <table border="0">
              <tr>
               <td><input type="radio" name="tipoRicerca" value="esatta" <%=(tipoRicerca.equals("esatta"))?"checked=\"true\"":""%> /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
               <td><input type="radio" name="tipoRicerca" value="iniziaPer" <%=(tipoRicerca.equals("iniziaPer"))?"checked=\"true\"":""%>/> inizia per</td>
              </tr>
              </table>
              </td>
            </tr>
            <tr><td class="etichetta">&nbsp;</td></tr>
        <tr>
          <td class="etichetta">Data di nascita</td>
          <td class="campo">
          <af:textBox type="date" name="datnasc" value="<%=datnasc%>" size="10" maxlength="10"/>
          </td>
        </tr>
        <tr>
			<td class="etichetta">Comune di nascita&nbsp;</td>
			<td class="campo">
				<af:textBox classNameBase="input"
				title="Comune di nascita"
				onKeyUp="PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'codice');"
				type="text" name="codComNas" value="<%=codComNas%>" size="4" maxlength="4"
				readonly="false"/>&nbsp; 
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per codice"/></a>&nbsp; 
				<af:textBox type="hidden"
							name="codComNasHid" value="<%=codComNas%>" />


				
				<af:textBox type="text"
							classNameBase="input"
							onKeyUp="PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');"
							name="strComNas" value="<%=strComNas%>" size="30" maxlength="50"
							readonly="false"/>
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per descrizione"/></a>&nbsp; 
		   <af:textBox type="hidden"  name="strComNasHid" value="<%=strComNas%>"/>
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
