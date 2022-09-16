<script language="Javascript">
	function openRich_PopUP(prgRich) {
     	window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  	}
</script>
<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null && !urlDiLista.equals("")) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  } else { urlDiLista = ""; }
}

String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

%>



<%
//SourceBean cont = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA");
SourceBean infoRich = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA.ROWS.ROW");
//cont = (SourceBean) serviceResponse.getAttribute("MINFORICHMANSIONI");
Vector infoRichMan = serviceResponse.getAttributeAsVector("MINFORICHMANSIONI.ROWS.ROW");
SourceBean rowMan = null;
int i = 0;
if(infoRichMan.size()>0) { 
	nroMansioni = infoRichMan.size(); 
}
String _indirizzo = "";
String ragSoc = StringUtils.getAttributeStrNotNull(infoRich,"STRRAGIONESOCIALE");
String numRichiesta = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTA");
String numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTAORIG");
String numAnno = StringUtils.getAttributeStrNotNull(infoRich,"NUMANNO");
String dataChiamata = StringUtils.getAttributeStrNotNull(infoRich,"DATCHIAMATA");
String dataPubblicazione = StringUtils.getAttributeStrNotNull(infoRich,"DATPUBBLICAZIONE");
String dataRich = StringUtils.getAttributeStrNotNull(infoRich,"DATRICHIESTA");
String dataScad = StringUtils.getAttributeStrNotNull(infoRich,"DATSCADENZA");
String stato_richiesta = StringUtils.getAttributeStrNotNull(infoRich,"stato_richiesta");
String stato_evasione = StringUtils.getAttributeStrNotNull(infoRich,"stato_evasione");
String codCpi = StringUtils.getAttributeStrNotNull(infoRich,"CODCPI");
String descCpi = StringUtils.getAttributeStrNotNull(infoRich,"desc_cpi");
String indir = StringUtils.getAttributeStrNotNull(infoRich,"STRINDIRIZZO");
String cap = StringUtils.getAttributeStrNotNull(infoRich,"STRCAP");
String loc = StringUtils.getAttributeStrNotNull(infoRich,"STRLOCALITA");
String comune = StringUtils.getAttributeStrNotNull(infoRich,"comune_az");
String targa = StringUtils.getAttributeStrNotNull(infoRich,"STRTARGA");

String utIns = StringUtils.getAttributeStrNotNull(infoRich, "UT_INS");
String utMod = StringUtils.getAttributeStrNotNull(infoRich, "UT_MOD");
if(infoRich!=null) {
  
  if(indir!="") { _indirizzo += indir; }
  if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
  if(cap!="") { _indirizzo += cap; }
  if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
  if(loc!="") { _indirizzo += "- " + loc; }
  if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
  if(comune!="") { _indirizzo += "- " + comune; }
  if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
  if(targa!=null) { _indirizzo += "(" + targa + ")"; }
}
%>

<%if(infoRich!=null) {%>  
  <br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <strong><%=numRichiestaOrig%>/<%=numAnno%></strong>&nbsp;&nbsp;-
  Data Richiesta <strong><%=dataRich%></strong>&nbsp;&nbsp;-
  Data Scadenza <strong><%=dataScad%></strong>
  <br>
  <%
  // INIT-PARTE-TEMP
  if (Sottosistema.AS.isOff()) {	
  // END-PARTE-TEMP  
  %>
  <%
  // INIT-PARTE-TEMP
  } else {
  // END-PARTE-TEMP
  %>
  Data Chiamata <strong><%=dataChiamata%></strong>&nbsp;&nbsp;-
  Data Pubblicazione <strong><%=dataPubblicazione%></strong>
  <br>
  <%
  // INIT-PARTE-TEMP
  }
  // END-PARTE-TEMP
  %>
  CpI di riferimento <strong><%=codCpi%> - <%=descCpi%></strong>&nbsp;&nbsp;
  Stato della Richiesta <strong><%=stato_richiesta%></strong>&nbsp;&nbsp;-
  Stato di Evasione <strong><%=stato_evasione%></strong>&nbsp;&nbsp;
  <br>
  Richiedente <strong><%=ragSoc%></strong>&nbsp;&nbsp;
  <strong><%= _indirizzo%></strong>
  <%if(viewPar) {%>
  	  <br>
  	  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgRichiestaAz%>')"><img src="../../img/copiarich.gif" alt="Inf. Copia"/></a>&nbsp;Copia utilizzata
  	  <%if(prgTipoIncrocio.equals("1")) {%>
      		<a href="#" onClick="openPar_Esatto_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
  	  <%} else {%>
    		<%if(prgTipoIncrocio.equals("2")) {%>
    			<a href="#" onClick="openPar_Pesato_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
    		<%}%>
  	  <%}%>
  <%} else {%>
	  <%if(gestCopia) {%>
	  	<br>
	  	<a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgC1%>')"><img src="../../img/copiarich.gif" alt="Inf. Copia di Lavoro"/></a>
	    &nbsp;<b>Copia di lavoro</b>
	  <%}%>
  <%}%>
  <%if(nroMansioni>0) {%>
    <br>
    <table  class="info" align="left" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top">
      <td align="left" class="info">Mansioni richieste
      <%if(gestCopia) {%>
       nella copia
      <%}%>
      :&nbsp;</td>
      <td align="left">
      <table align="left" maxwidth="85%">
        <%
        String nroAlternativa = "";
        String nroAlt = "";
        int nl = 0;
        %>
        <%for(i=0; i < nroMansioni; i++) {%>
          <%
          rowMan = (SourceBean) infoRichMan.elementAt(i); 
          nroAlternativa = StringUtils.getAttributeStrNotNull(rowMan, "PRGALTERNATIVA");
          if(!nroAlt.equals(nroAlternativa)) {
            if(nl >0 ) {
          %>
                  </ul></td></tr>
            <%}%>
            <tr valign="top"><td><b>Profilo&nbsp;n.&nbsp;<%=nroAlternativa%></b></td><td><ul type="square">
            <%
            nl += 1;
            nroAlt = nroAlternativa;
            %>
          <%}%>
          <li><b><%=StringUtils.getAttributeStrNotNull(rowMan,"MANSIONE")%></b></li>
        <%}%>
      </ul>
      </td></tr>
      </table>
      </td>
    </tr>
    </table>
  <%}%>
  </p>
  <%out.print(htmlStreamBottomInfo);%>
<%}%>