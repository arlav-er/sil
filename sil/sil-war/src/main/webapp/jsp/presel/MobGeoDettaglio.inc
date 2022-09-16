     <tr class="note">
        <td class="etichetta">Disponibilit&agrave; utilizzo<br>Automobile</td>
        <td class="campo">
          <table>
          <tr>
            <td class="campo2">
                <af:comboBox name="flgDispAuto"
                             size="1"
                             title="Utilizzo Automobile"
                             multiple="false"
                             addBlank="true"
                             blankValue=""
                             classNameBase="combobox"
                             disabled="<%= String.valueOf( !canModify ) %>"
                             onChange="fieldChanged()">
                    <OPTION value="S" <%=(flgAutoD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                    <OPTION value="N" <%=(flgAutoD.equals("N")?"SELECTED":"")%>>No</OPTION>
              </af:comboBox>
                
            </td>
            <td class="etichetta2">&nbsp;</td>
            <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Motoveicolo</td>
            <td class="campo2">
              <af:comboBox name="flgDispMoto"
                             size="1"
                             title="Utilizzo Motoveicolo"
                             multiple="false"
                             addBlank="true"
                             blankValue=""
                             classNameBase="combobox"
                             disabled="<%= String.valueOf( !canModify ) %>"
                             onChange="fieldChanged()">
                    <OPTION value="S" <%=(flgMotoD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                    <OPTION value="N" <%=(flgMotoD.equals("N")?"SELECTED":"")%>>No</OPTION>              
              </af:comboBox>
            </td>
          </tr>
          </table>
        </td>
     </tr>
      <tr>
            <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Mezzi pubblici</td>
            <td class="campo2">
              <af:comboBox name="flgMezziPub"
                             size="1"
                             title="Utilizzo Mezzi pubblici"
                             multiple="false"
                             addBlank="true"
                             blankValue=""
                             classNameBase="combobox"
                             disabled="<%= String.valueOf( !canModify ) %>"
                             onChange="fieldChanged()">
                    <OPTION value="S" <%=(flgMezziPub.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                    <OPTION value="N" <%=(flgMezziPub.equals("N")?"SELECTED":"")%>>No</OPTION>              
              </af:comboBox>
               
      </td>
    </tr>
     <tr class="note">
        <td class="etichetta">Pendolarismo<br>Giornaliero</td>
        <td class="campo">
        <table>
        <tr>
            <td class="campo2">
              <af:comboBox name="flgPendolarismo"
                       size="1"
                       title="Pendolarismo Giornaliero"
                       multiple="false"
                       addBlank="true"
                       blankValue=""
                       classNameBase="combobox"
                       disabled="<%= String.valueOf( !canModify ) %>"
                       onChange="fieldChanged()">
                    <OPTION value="S" <%=(flgPendoD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                    <OPTION value="N" <%=(flgPendoD.equals("N")?"SELECTED":"")%>>No</OPTION>              
              </af:comboBox>
                       
            </td>
            <td class="etichetta2">&nbsp;</td>
            <td class="etichetta2">Durata di Percorrenza Max. in Ore</td>
            <td class="campo2">
              <af:textBox name="numOrePerc"
                      value="<%=maxOreD%>"
                      size="3"
                      maxlength="2"
                      title="Durata di Percorrenza"
                      type="integer"
                      validateOnPost="true"
                      classNameBase="textarea"
                      readonly="<%= String.valueOf( !canModify ) %>"
                      onKeyUp="fieldChanged()"
              />
            </td>
        </tr>
        </table>
        </td>
     </tr>
     <tr>
        <td class="etichetta">Mobilit&agrave;<br>Settimanale</td>
        <td class="campo">
          <af:comboBox name="flgMobSett"
                   size="1"
                   title="Mobilità Settimanale"
                   multiple="false"
                   selectedValue="<%=flgMobSettD%>"
                   addBlank="true"
                   blankValue=""
                   classNameBase="combobox"
                   disabled="<%= String.valueOf( !canModify ) %>"
                   onChange="fieldChanged()">
                    <OPTION value="S" <%=(flgMobSettD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                    <OPTION value="N" <%=(flgMobSettD.equals("N")?"SELECTED":"")%>>No</OPTION>
              </af:comboBox>
                   
        </td>
      </tr>
      <tr>
        <td class="etichetta">Tipo di Trasferta</td>
        <td class="campo">
          <af:comboBox name="codTrasferta"
                       size="1"
                       title="Tipo di Trasferta"
                       multiple="false"
                       moduleName="MListTrasferte"
                       selectedValue="<%=codTrasfD%>"
                       addBlank="true"
                       blankValue=""
                       required="true"
                       classNameBase="combobox"
                       disabled="<%= String.valueOf( !canModify ) %>"
                       onChange="fieldChanged()"
          />
        </td>
      </tr>
      <tr class="note">
        <td class="etichetta">Note</td>
        <td class="campo">
          <af:textArea name="strNote" 
            rows="4" 
            cols="60"
            classNameBase="textarea"
            readonly="<%= String.valueOf( !canModify ) %>"
            onKeyUp="fieldChanged()"
            value="<%= strNoteD %>" />
        </td>
      </tr>
      <tr><td colspan="2">&nbsp;</td></tr>