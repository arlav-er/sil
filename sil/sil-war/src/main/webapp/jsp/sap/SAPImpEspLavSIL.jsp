<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnEspLavSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnEspLavSIL', 'tabEspLavSIL');toggle('btnEspLavSAP', 'tabEspLavSAP');return false" />
      <font size="2">Esperienze Lavorative</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabEspLavSIL">

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>
  
<%
  Vector vettEsperienze = serviceResponse.getAttributeAsVector("M_SelectSAPEspLav.ROWS.ROW");
  if (vettEsperienze != null) {
  
    String desMansione = "";
    String strDataInizio = "";
    String strDataFine = "";
    String desContratto = "";
    String strRagSocAzienda = "";
    String desAttivitaAzienda = "";
    String desAttivitaLavoratore = "";
   
    for (int i = 0; i < vettEsperienze.size(); i++) {
      SourceBean beanEsperienza = (SourceBean) vettEsperienze.get(i);
    
      
      desMansione = Utils.notNull(beanEsperienza.getAttribute("desMansione"));
     
      strRagSocAzienda = Utils.notNull(beanEsperienza.getAttribute("strRagSocialeAzienda"));
      // recupera anno e mese
      String numAnnoInizio = Utils.notNull(beanEsperienza.getAttribute("numAnnoInizio"));
      String numMeseInizio = Utils.notNull(beanEsperienza.getAttribute("numMeseInizio"));
      String numAnnoFine = Utils.notNull(beanEsperienza.getAttribute("numAnnoFine"));
      String numMeseFine = Utils.notNull(beanEsperienza.getAttribute("numMeseFine"));
      
      // normalizza il mese
      if (numMeseInizio.length() == 1) 
        numMeseInizio = "0" + numMeseInizio;
      if (numMeseFine.length() == 1) 
        numMeseFine = "0" + numMeseFine;
      
      // controlla se l'anno e' valorizzato
      if (numAnnoInizio.length() > 0)
        strDataInizio = numMeseInizio + "/" + numAnnoInizio;
      if (numAnnoFine.length() > 0)
        strDataFine = numMeseInizio + "/" + numAnnoFine;
      
      desContratto = Utils.notNull(beanEsperienza.getAttribute("desContratto"));
      desAttivitaAzienda = Utils.notNull(beanEsperienza.getAttribute("desAttivita"));
      desAttivitaLavoratore = Utils.notNull(beanEsperienza.getAttribute("strDesAttivita"));
%>
    <tr>
      <td class="silTitoloSezione" colspan="2"><%=desMansione%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Descrizione delle attivit&agrave; svolte</td>
      <td class="inputView"><%=desAttivitaLavoratore%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Tipo di rapporto</td>
      <td class="inputView"><%=desContratto%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Ragione sociale</td>
      <td class="inputView"><%=strRagSocAzienda%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Attivit&agrave;</td>
      <td class="inputView"><%=desAttivitaAzienda%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Mese/anno di inizio</td>
      <td class="inputView"><%=strDataInizio%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Mese/anno di fine</td>
      <td class="inputView"><%=strDataFine%></td>
    </tr>

    <tr>
      <td class="etichetta, grassetto, indenta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>

<%
   }
  }
%>

</table>
<%
}
%>

