<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnTitStuSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnTitStuSIL', 'tabTitStuSIL');toggle('btnTitStuSAP', 'tabTitStuSAP');return false" />
      <font size="2">Titoli di Studio</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabTitStuSIL">

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>

<%
  Vector vettTitoli = serviceResponse.getAttributeAsVector("M_SelectSAPTitStu.ROWS.ROW");

  String codTitolo = "";
  String desTitolo = "";
  String flgStato = "";
  String numAnnoTitolo = "";
  String strSpecifica = "";
  String flgPrincipale = "";
  String strNomeIstituto = "";
  String strVotazione = "";
  String codComuneDesc = "";
  if (vettTitoli != null) {
    for (int i = 0; i < vettTitoli.size(); i++) {
      SourceBean beanTitolo = (SourceBean) vettTitoli.get(i);
      codTitolo = Utils.notNull(beanTitolo.getAttribute("codTitolo"));
      desTitolo = Utils.notNull(beanTitolo.getAttribute("desTitolo"));
      flgStato = Utils.notNull(beanTitolo.getAttribute("desStato"));
      numAnnoTitolo = Utils.notNull(beanTitolo.getAttribute("numAnno"));
      strSpecifica = Utils.notNull(beanTitolo.getAttribute("strSpecifica"));
      flgPrincipale = Utils.notNull(beanTitolo.getAttribute("flgPrincipale"));
      strNomeIstituto = Utils.notNull(beanTitolo.getAttribute("strIstScolastico"));
      strVotazione = Utils.notNull(beanTitolo.getAttribute("strVoto"));
      codComuneDesc = Utils.notNull(beanTitolo.getAttribute("desComune"));
      
      String desTipoTitolo = "";
      try {
        String codTipoTitolo = InsertSAP.getTipoTitolo(codTitolo);
        desTipoTitolo = InsertSAP.getDescrTitolo(codTipoTitolo);
      } catch (Exception e) {
        SelectSAP.getLogger().error(e.getMessage());
      }
%>

  <tr valign="top">
    <td class="silTitoloSezione" colspan="2"><%=desTitolo%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Tipo</td>
    <td class="campo"><%=desTipoTitolo%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Anno di conseguimento</td>
    <td class="campo"><%=numAnnoTitolo%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Specifica</td>
    <td class="campo"><%=strSpecifica%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Principale</td>
    <td class="campo"><%=flgPrincipale.equals("S") ? "Sì" : "No"%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Stato</td>
    <td class="campo"><%=flgStato%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Istituto scolastico</td>
    <td class="campo"><%=strNomeIstituto%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Comune</td>
    <td class="campo"><%=codComuneDesc%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Voto</td>
    <td class="campo"><%=strVotazione%></td>
  </tr>

    <tr>
      <td class="etichetta">&nbsp;</td>
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
