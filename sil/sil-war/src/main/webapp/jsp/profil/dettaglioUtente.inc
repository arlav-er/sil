
        
        <tr>
          <td class="etichetta">Login</td>
          <td class="campo">
            <af:textBox name="strlogin"  value="<%=Utils.notNull(strlogin)%>" 
              title="Login"  required="true"
              size="18" maxlength="18" onKeyUp="fieldChanged()"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
              />
          </td>
        </tr>

	<%
		String readOnlyPwd ="";
		String requiredPwd ="";
		String msgPwd="";
		
		if (isNuovo){
			readOnlyPwd ="false";
			requiredPwd ="true";
		}else{
			if(readonly){
				readOnlyPwd ="true";
			}else{
				readOnlyPwd ="false";
				msgPwd="Lasciare vuoti i campi di password<br>se non si vuole modificare la password corrente";
			}
			requiredPwd ="false";
		}
	
	%>


      	
        <tr>
          <td class="etichetta">Password</td>
          <td class="campo">
            <af:textBox name="password"  type="password" value="" 
              title="Password"  required="<%=requiredPwd%>"
              size="18" maxlength="18" onKeyUp="fieldChanged()" 
              classNameBase="input"
              readonly="<%=readOnlyPwd%>"
            />
          </td>
        </tr>


  		<tr>
            <td class="etichetta">Conferma password</td>
          <td class="campo"><span>
            <af:textBox name="password2"  type="password" value="" 
              title="Password"  required="<%=requiredPwd%>"
              size="18" maxlength="18" onKeyUp="fieldChanged()" 
              classNameBase="input"
              readonly="<%=readOnlyPwd%>"
            />  </span>
          </td>
         
        </tr>


		<tr>
    	      <td class="etichetta"></td>
	          <td class="campo">
              <%=msgPwd%>
              </td>
        </tr>

		<tr>
    	      <td class="etichetta"></td>
	          <td class="campo">&nbsp;
              </td>
        </tr>


	  <tr>
          <td class="etichetta">Codice fiscale</td>
          <td class="campo">
            <af:textBox name="strCodiceFiscale"  value="<%=Utils.notNull(strCodiceFiscale)%>" 
              title="Codice Fiscale" onKeyUp="fieldChanged()"
              size="18" maxlength="16" 
              classNameBase="input"
              readonly="<%=readonlyStr%>"
              validateWithFunction="checkCF"
            />
          </td>
        </tr>
       



        <tr>
          <td class="etichetta">Cognome</td>
          <td class="campo">
            <af:textBox name="strCognome"  value="<%=Utils.notNull(strCognome)%>" 
              title="Cognome" onKeyUp="fieldChanged()"
              size="40" maxlength="40" 
              classNameBase="input"
              readonly="<%=readonlyStr%>"
            />
          </td>
        </tr>
        
        <tr>
          <td class="etichetta">
            Nome
          </td>
          <td class="campo">
            <af:textBox name="strNome"  value="<%=Utils.notNull(strNome)%>" 
              title="nome"  onKeyUp="fieldChanged()"
              size="40" maxlength="40"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
            />

          </td>
        </tr>
        
        <tr>
          <td class="etichetta">
            Data di nascita
          </td>
          <td class="campo">
            <af:textBox name="datNasc"  value="<%=Utils.notNull(datNasc)%>" 
              title="Data di nascita"  onKeyUp="fieldChanged()"
              size="12" maxlength="10"
              classNameBase="input"
              type="date"
              readonly="<%=readonlyStr%>"
            />

          </td>
        </tr>
        

        <tr>
		    <td class="etichetta">
		    	Sesso
		    </td>
		    <td class="campo">
		    	<af:comboBox 
		    			classNameBase="input" 
		    			name="strSesso" 
		    			title="Sesso" 
		    			onChange="fieldChanged()" 
		    			disabled="<%=readonlyStr%>">
		    			<OPTION value=""></OPTION>
        			  	<OPTION value="M" <%if ((strSesso!=null) && strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
			          	<OPTION value="F" <%if ((strSesso!=null) && strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
			          <!--<OPTION value="N" >Non so</OPTION>-->			          
		        </af:comboBox>
		    </td>
		</tr>
        
        
         
        <tr>
          <td class="etichetta">Telefono</td>
          <td class="campo">
              <af:textBox name="Telefono"  value="<%=Utils.notNull(strtelefono)%>" 
                title="Telefono"  onKeyUp="fieldChanged()"
                size="20" maxlength="20"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
            />
                
          </td>
        </tr>



        <tr>
          <td class="etichetta">Fax</td>
          <td class="campo">
              <af:textBox name="Fax"  value="<%=Utils.notNull(strfax)%>" 
                title="Fax"  onKeyUp="fieldChanged()"
                size="20" maxlength="20"
                classNameBase="input"
                readonly="<%=readonlyStr%>"
            />
                
          </td>
        </tr>


        <tr>
          <td class="etichetta">E-mail</td>
          <td class="campo">
              <af:textBox name="mail"  value="<%=Utils.notNull(stremail)%>" 
                title="E-mail"  onKeyUp="fieldChanged()"
                size="50" maxlength="50"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
            />
          </td>
        </tr>

        <tr>
          <td class="etichetta">Luogo di riferimento</td>
          <td class="campo">
              <af:textBox name="luogorif"  value="<%=Utils.notNull(strluogorif)%>" 
                title="Luogo di riferimento"  onKeyUp="fieldChanged()"
                size="50" maxlength="100"
              classNameBase="input"
              readonly="<%=readonlyStr%>"
            />
                
          </td>
        </tr>


        <tr>
          <td class="etichetta">
            Abilitato
          </td>
          
          <td class="campo" nowrap>
            <% if (flgabilitato== null) flgabilitato=""; %>
            <af:comboBox name="flgAbilitato" addBlank="false" required="true" title="Abilitato"
              onChange="fieldChanged()"
              classNameBase="input"
              disabled="<%=readonlyStr%>">
              <option value=""></option>
              <option value="S"  <%= flgabilitato.equalsIgnoreCase("S") ? "SELECTED=\"true\"" : "" %>>Sì</option>
              <option value="N"  <%= flgabilitato.equalsIgnoreCase("N") ? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
          </td>
        </tr>
        
        <tr>
          <td class="etichetta">
            Convenzionato
          </td>
          
          <td class="campo" nowrap>
            <% if (flgUtConvenzione== null) flgUtConvenzione=""; %>
            <af:comboBox name="flgUtConvenzione" addBlank="false" required="true" title="Convenzionato"
              onChange="fieldChanged()"
              classNameBase="input"
              disabled="<%=readonlyStr%>">
              <option value=""></option>
              <option value="S"  <%= flgUtConvenzione.equalsIgnoreCase("S") ? "SELECTED=\"true\"" : "" %>>Sì</option>
              <option value="N"  <%= flgUtConvenzione.equalsIgnoreCase("N") ? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
          </td>
        </tr>
        <tr>
          <td class="etichetta">
            Collegato
          </td>
          
          <td class="campo" nowrap>
            <% if (flglogged== null) flglogged=""; %>
            <af:comboBox name="flglogged" addBlank="false" title="Collegato"
              classNameBase="input"
              disabled="true">
              <option value=""></option>
              <option value="S"  <%= flglogged.equalsIgnoreCase("S") ? "SELECTED=\"true\"" : "" %>>Sì</option>
              <option value="N"  <%= flglogged.equalsIgnoreCase("N") ? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
          </td>
        </tr>

 		<tr>
          <td class="etichetta">Numero tentativi</td>
          <td class="campo">
              <af:textBox name="numtentativi" type="text" value="<%=Utils.notNull(numtentativi)%>" 
                title="Numero tentativi" 
                size="11" maxlength="10"
                classNameBase="input"
                readonly="true"
            />
                
          </td>
        </tr>
        
        <tr>
          <td class="etichetta">Inizio validità</td>
          <td class="campo">
              <af:textBox name="datinizioval" type="date" value="<%=Utils.notNull(datinizioval)%>" 
                title="Inizio validità" required="true"   validateOnPost="true" onKeyUp="fieldChanged()"
                size="11" maxlength="10"
                classNameBase="input"
                readonly="<%=readonlyStr%>"
            />
                
          </td>
        </tr>

        <tr>
          <td class="etichetta">Fine validità</td>
          <td class="campo">
              <af:textBox name="datfineval" type="date" value="<%=Utils.notNull(datfineval)%>" 
                title="Fine validità"  required="true" validateOnPost="true" onKeyUp="fieldChanged()"
                size="11" maxlength="10"
                classNameBase="input"
                readonly="<%=readonlyStr%>"
            />
                
          </td>
        </tr>

       <tr>
          <td class="etichetta">Note</td>
          <td class="campo">
            <%if (readonlyStr.equals("true")){%>
          
              <textarea name="note" 
                rows="4" cols="50" 
                class="textareaView"
                readonly
                ><%=Utils.notNull(strnota)%></textarea>

             <%} else {%>   
                
              <textarea name="note" 
                rows="4" cols="50" 
                class="textareaEdit"
                onKeyUp="fieldChanged()"><%=Utils.notNull(strnota)%></textarea>

             <%} // end textarea %>   
                
          </td>
        </tr>
        
        

      
     
            

          
   

