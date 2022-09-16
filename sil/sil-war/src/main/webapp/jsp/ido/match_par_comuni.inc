<%
PageAttribs attributiMatch = new PageAttribs(user, "GestIncrocioPage");
boolean matchEsatto = attributiMatch.containsButton("MATCH_ESATTO");
boolean matchPesato = attributiMatch.containsButton("MATCH_PESATO");
boolean matchConfidenza = attributiMatch.containsButton("MATCH_CONFIDENZA");
boolean matchSuRose = attributiMatch.containsButton("MATCH_SU_ROSE");
%>
<tr>
  <td colspan="2"><div class="sezione2">Confidenza dei Dati</div></td>
</tr>
<tr>
  <td colspan="2" align="left">
  <%if(matchConfidenza) {%> 
          Il matching effettua la ricerca sui lavoratori il cui curriculum risulta
          <b>completo e validato</b> al momento di esecuzione dell'incrocio o, in alternativa,
          alla data specificata.<br>
          <!--Includi nella ricerca <b>anche</b> quei lavoratori il cui curriculum risulta-->
  <%} else {%>
          Il matching effettua la ricerca sui lavoratori il cui curriculum risulta
          <b>completo e validato</b> al momento di esecuzione dell'incrocio.
  <%}%>
  </td>
</tr>
<!--tr>
  <td class="etichetta">Solo Validato</td>
  <td class="campo"><input type="radio" name="statoCv" value="VAL"></td>
</tr>
<tr>
  <td class="etichetta">Validato oppure Incompleto</td>
  <td class="campo"><input type="radio" name="statoCv" value="PAR"></td>
</tr-->
<tr <%if(!matchConfidenza) {%> style="display: none"<%}%>>
  <td class="etichetta">Data di validit&agrave; del CV</td>
  <td class="campo">
  <af:textBox name="dataCV"
                value=""
                size="11"
                maxlength="10"
                title="Data di Validit&agrave; del CV"
                alt="Data  di Validit&agrave; del CV"
                type="date"
                validateOnPost="true"
   />
  </td>
</tr>
<tr>
  <td colspan="2"><div class="sezione2">Ambito Territoriale</div></td>
</tr>
<tr>
  <td colspan="2" align="left">
  <!--Scelta della banca dati dalla quale effettuare la ricerca.-->
  La ricerca viene eseguita su base regionale.
  </td>
<!--tr>
  <td class="etichetta">CpI</td>
  <td class="campo"><input type="radio" name="db" value="1"></td>
</tr>
<tr>
  <td class="etichetta">Provincia</td>
  <td class="campo"><input type="radio" name="db" value="2"></td>
</tr>
<tr>
  <td class="etichetta">Regione</td>
  <td class="campo"><input type="radio" name="db" value="3" checked></td>
</tr-->

<!--tr><td colspan="2">&nbsp;</td></tr-->
<tr>
  <td colspan="2"><div class="sezione2">Alternativa</div></td>
</tr>

<%if(nroAlternative>1) {%>
  <tr>
    <td class="etichetta">Profili:</td>
    <td class="campo">
    <af:comboBox name="PRGALTERNATIVA" size="1" title="Scelta Alternativa"
                       multiple="false" disabled="false" required="true"
                       focusOn="false" moduleName="MALTERNATIVEINCROCIO"
                       selectedValue="" addBlank="true" blankValue=""/> 
    </td>
  </tr>
<% } else {%>
  <%if(nroAlternative==1) {%>
    <tr>
      <td class="etichetta">Profilo n.</td>
      <%
      SourceBean rowAlt = (SourceBean) altIncr.elementAt(0);
      String prgAlternativa = rowAlt.getAttribute("CODICE").toString();
      %>
      <td class="campo">
        <b><%=prgAlternativa%></b>
        <input type="hidden" name="PRGALTERNATIVA" value="<%=prgAlternativa%>"/>
        </td>
    </tr>
    <%} else {%>
      <tr><td colspan="2" align="left">Nessuna alternativa</td></tr>
    <%}%>
  <%}%>


<%
SourceBean contRep = (SourceBean) serviceResponse.getAttribute("MATCHREPORTROSE");
Vector rowsRep = contRep.getAttributeAsVector("ROWS.ROW");
SourceBean rep = null;
if(rowsRep.size()>0) {
%>
    <tr><td colspan="2"><div class="sezione2">&nbsp;</div></td></tr>

    <%for(i=0; i<rowsRep.size(); i++) {%>
      <%rep = (SourceBean) rowsRep.elementAt(i);%>
      <tr valign="top">
        <td class="etichetta"><b>Profilo n. <%=StringUtils.getAttributeStrNotNull(rep, "PRGALTERNATIVA")%></b></td>
        <td class="campo">
          <table maxwidth="95%" width="90%" align="left">
          <tr>
            <td>Rose Definitive:</td>
            <%if(matchEsatto) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NRODEFESATTO")%></td>
                <td>da&nbsp;Matching Esatto</td>
            <%}%>
            <%if(matchPesato) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NRODEFPESATO")%></td>
                <td>da&nbsp;Matching Pesato</td>
            <%}%>
          </tr>
          <tr>
            <td>Rose Grezze:</td>
            <%if(matchEsatto) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NROGREZZEESATTO")%></td>
                <td>da&nbsp;Matching Esatto</td>
            <%}%>
            <%if(matchPesato) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NROGREZZEPESATO")%></td>
                <td>da&nbsp;Matching Pesato</td>
            <%}%>
          </tr>
          </table>
        </td>
      </tr>
    <%}%>
<%}%>

<!--tr <%if(!matchSuRose) {%> style="display: none"<%}%>-->
<tr style="display: none">
  <td colspan="2"><div class="sezione2">Scelta Rosa</div></td>
</tr>