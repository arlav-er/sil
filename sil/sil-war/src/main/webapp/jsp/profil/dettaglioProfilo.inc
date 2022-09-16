      <font color="green"><af:showMessages prefix="ProfSalvaProfilo"/></font>
      <font color="green"><af:showMessages prefix="ProfClonaProfilo"/></font>
	    <font color="red"><af:showErrors /></font>
	
			<af:form method="POST" action="AdapterHTTP">
      <af:textBox type="hidden" name="PAGE" value="ProfDettaglioProfiloPage"/>
      <af:textBox type="hidden" name="cdnprofilo" value="<%=Utils.notNull( cdnprofilo )%>"/>
      <af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />
      <p align="center">
      
       <%out.print(htmlStreamTop);%>
      <table class="main">
        
        
        <tr>
          <td class="etichetta">Denominazione</td>
          <td class="campo">
            <af:textBox name="denominazione"  value="<%=Utils.notNull(strdenominazione)%>" 
              title="Denominazione"  required="true"
              size="50" maxlength="100" onKeyUp="fieldChanged()"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
              />
          </td>
        </tr>

        <tr>
          <td class="etichetta">
            Profilo Standard
          </td>
          <td class="campo" nowrap>
          <% if (flgstandard== null) flgstandard=""; %>
          <af:comboBox name="flgstandard" addBlank="false" required="true" 
                  onChange="fieldChanged()"
                  disabled="true"
                  classNameBase="input"
                  >
            <option value=""></option>
            <option value="S"  <%= flgstandard.equalsIgnoreCase("S") ? "SELECTED=\"true\"" : "" %>>Sì</option>
            <option value="N"  <%= flgstandard.equalsIgnoreCase("N") ? "SELECTED=\"true\"" : "" %>>No</option>
          </af:comboBox>

          </td>
        </tr>


       <tr>
          <td class="etichetta">Profilo padre</td>
          <td class="campo">
            <af:textBox type="hidden" name="ComboProfiloPadre" value="<%=Utils.notNull(cdnprofilopadre)%>" />
          
            <af:textBox name="nonImporta" readonly="true"  size="50"  classNameBase="input"
             value="<%=Utils.notNull( profPadre )%>"
           />

          </td>
        </tr>
        
        <tr>
          <td class="etichetta">Nota</td>
          
          <td class="campo"><af:textArea name="nota" value="<%=strnota%>" 
          							rows="5" cols="50" maxlength="100"  
          							disabled="<%=String.valueOf(readonlyStr)%>"/></td>
          
          
          
         
        </tr>
        
           
        
       <tr>
            <td>
             
            </td>
        </tr>
            <% if (nuovoProf && canInsert) {%>
                  <tr><td>&nbsp</td></tr>    
                    <tr>
                      <td colspan="2" align="center" nowrap> 
                      <input class="pulsante" type="submit" name="INSERISCI" VALUE="Inserisci" />
                    </td>
                  </tr>
            <%} else if (canSave && !nuovoProf){%>
               
                    <tr>
                      <td colspan="2" align="center" nowrap> 
                       
                      <input class="pulsante" type="submit" name="SALVA" VALUE="Aggiorna" />
                      	&nbsp;
                      <input class="pulsante" type="reset" VALUE="Annulla" />
                      	&nbsp;
                      	
                      <% if (flgstandard.equalsIgnoreCase("S")){ 
                      	// Si puo' clonare solo un profilo standard
                      %>	
                      	
	                      <input class="pulsante" type="submit" name="CLONA" VALUE="Clona" />
                      <% } %>

                    </td>
                  </tr>
                  <tr><td>&nbsp</td></tr>    
                   
                  
            <%}%>

      </table>
      <%out.print(htmlStreamBottom);%> 
        </p>
      </af:form>
		</center>

