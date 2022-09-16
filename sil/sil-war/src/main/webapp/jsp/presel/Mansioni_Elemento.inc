<tr valign="top">
    <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
    <td class="campo">
      <input type="checkbox" disabled="<%= String.valueOf(!(canModify)) %>" name="flgFrequente" value="" checked="true" />
    </td>
</tr>

 <tr>
    <td class="etichetta">Codice mansione</td>
    <td class="campo">
      <af:textBox 
        classNameBase="input" 
        name="CODMANSIONE" 
        size="7" 
        maxlength="7" 
        value="<%= codMansione.toString() %>" 
        readonly="<%= String.valueOf(!canModify) %>" 
      />
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value="<%= codMansione.toString() %>" 
      />
      
      <% if (canModify) { %>
          <a href="javascript:selectMansione_onClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A href="javascript:ricercaAvanzataMansioni();">
              Ricerca avanzata
          </A>
      <%}%>
    </td>
  </tr>           
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione%>" readonly="true" size="48" />
    </td>
  </tr>
<tr>
  <td class="etichetta">Descrizione</td>
  <td class="campo">
      <af:textArea cols="30" 
                   rows="4" 
                   name="DESCMANSIONE" 
                   classNameBase="textarea"
                   readonly="true" 
                   required="true"
                   maxlength="100"
                   value="<%= descMansione %>" />
  </td>
</tr>
<tr>
  <td>&nbsp;</td>
</tr>
<tr>

  <td>

      <% if (_page.equals ("MansioniPage")) { %>
    <table>
      <td class="etichetta">Esperienza ?</td>
      <td class="campo">
        <af:comboBox
          title="Esperienza"
          name="FLGESPERIENZA"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgEsperienza) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
          <option value="N" <% if ( "N".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >No</option>
          
        </af:comboBox>
      </td>
      
      <td class="etichetta">Esperienza formativa ?</td>
      <td class="campo">
        <af:comboBox 
          title="Esperienza formativa"
          name="FLGESPFORM"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgEspForm) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
          <option value="N" <% if ( "N".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td>
    </table>
          <% } else { if (_page.equals ("CurrTirociniMainPage"))  { %>
          
    <table>

      
 
    <td class="etichetta">Esperienza formativa ?</td>
    <td>
          <af:textBox type="hidden" name="FLGESPFORM" value="S" />
          <af:textBox name="TMPFLGESPFORM"     classNameBase="input"  value="Si" readonly="true" size= "4"/>
    </td>
    </table>   
                     <% } else { %>
    <table>
      <td class="etichetta">Esperienza?</td>
      <td class="campo">
         <!-- <af:textBox type="hidden" name="FLGESPERIENZA" value="S" />  --> 
          <%boolean thich = false;
          if(!nuovo) {
             if (tipiMov != null && tipiMov.size() > 0 ) {
            	 //se movimento associato  
            	 for(int i=0; i<tipiMov.size();i++)
                 {
                     if (tipiMov.get(i).toString().equalsIgnoreCase("D") ||
                         tipiMov.get(i).toString().equalsIgnoreCase("O"))
                     {
                         thich = true;
                     } 
                 }//for
             }
          }%>
          <% if (thich){ %>              
               <af:textBox type="hidden" name="FLGESPERIENZA" value="S" />
          	   <af:textBox name="TMPFLGESPERIENZA"     classNameBase="input"  value="Si" readonly="true" size= "4"/>
          <% } else { %>
	          <af:comboBox 
	          title="Disponibile a lavorare con la mansione"
	          name="FLGESPERIENZA"
	          classNameBase="input"
	          disabled="<%= String.valueOf( !canModify ) %>">
		          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
		          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Sì</option>
		          <option value="E" <% if ( "E".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Non Documentata</option>
		       </af:comboBox>
          <%} %>
          
   </td>
      
      <td class="etichetta">Esperienza formativa ?</td>
      <td class="campo">
        <af:comboBox 
          title="Esperienza formativa"
          name="FLGESPFORM"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgEspForm) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
          <option value="N" <% if ( "N".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td>
    </table>    
  <% } }%>


<tr>
  <td colspan="2" >
    <br/>
    <b>&nbsp;&nbsp;Disponibilit&agrave;</b>
    <br/>
    <hr width="90%"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Disponibile a lavorare con la mansione ?</td>
  <td>
    <table>
      <td class="campo">
    <%
    // INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
    %>
        <af:comboBox 
          title="Disponibile a lavorare con la mansione"
          name="FLGDISPONIBILE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()">
          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
    
	<%}else{%>        
        <af:comboBox 
          title="Disponibile a lavorare con la mansione"
          name="FLGDISPONIBILE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()">
          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="P" <% if ( "P".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Preselezione ordinaria</option>
          <option value="L" <% if ( "L".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Legge 68</option>
          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Entrambe</option>
          <option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Nessuna</option>
        </af:comboBox>

    <%}%>

  <!--    <td class="etichetta">Mansione a tempo </td>
      <td class="campo">
        <af:comboBox 
          title="Mansione a tempo"
          name="CODMONOTEMPO"
          classNameBase="combobox"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(codMonoTempo) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="D" <% if ( "D".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Determinato</option>
          <option value="I" <% if ( "I".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Indeterminato</option>
          <option value="E" <% if ( "E".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Determinato / Indeterminato</option>
      </af:comboBox>
      </td> -->
    </table>
  </td>  
</tr>
<tr>
  <td class="etichetta">Disponibile alla formazione professionale?</td>
  <td>
    <table>
      <td class="campo">
        <af:comboBox 
          title="Disponibile alla formazione"
          name="FLGDISPFORMAZIONE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgDispFormazione) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
          <option value="N" <% if ( "N".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td>
   <!--   <td class="etichetta">Disponibile a PIP / tirocinii / stage ?</td>
      <td class="campo">
        <af:comboBox 
          title="Disponibile a PIP / tirocinii / stage"
          name="FLGPIP"
          classNameBase="combobox"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgPianiInsProf) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgPianiInsProf) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
          <option value="N" <% if ( "N".equals(flgPianiInsProf) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td> -->
    </table>
  </td>
</tr>

