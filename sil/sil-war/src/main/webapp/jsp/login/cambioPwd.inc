<%
int nGiorniPwdScadutaInt    = nGiorniPwdScaduta.intValue();
int nGiorniPwdScadutaIntAbs = java.lang.Math.abs(nGiorniPwdScadutaInt);
%>
         <tr>
          <td colspan="2">
          <div align="center">
          
          <% if (nGiorniPwdScadutaInt >= 1000) { %>
          	Al primo accesso &egrave; necessario cambiare la password assegnata.
          
          <%} else { if (nGiorniPwdScadutaInt >= 0) { %>
		          	La password &egrave; scaduta
		          		<% if (nGiorniPwdScadutaInt == 0) { %>
		          			<b>oggi</b>
		          		<% } else if (nGiorniPwdScadutaInt == 1) { %>
		          			<b>ieri</b>
		          		<% } else { %>
			          		da <b><%= nGiorniPwdScadutaInt %></b> giorni
			          	<% } %>
		          	e, quindi, &egrave; necessario fornire una nuova password.
		          <% } else { %>
		          	La password 
		          		<% if (nGiorniPwdScadutaIntAbs == 1) { %>
		          			scadr&agrave; <b>domani</b>.
		          		<% } else { %>
			          		scadr&agrave; tra <b><%= nGiorniPwdScadutaIntAbs %></b> giorni.
			          	<% } %>
		          <% } %>
          <% } %>

          </div>
          
           <div align="left">
          <br>Si ricorda che la nuova password <b>deve soddisfare i seguenti requisiti<b>:
          	
          	 <ul> 
	          	<li> deve essere lunga non meno di 8 caratteri; </li> 
	          	<li> deve contenere sia caratteri alfabetici sia cifre; </li> 
	          	<li> i caratteri alfabetici devono essere sia maiuscoli sia minuscoli. </li> 
          	  </ul>	
          	</div>	
  
          </td>
        </tr>

          <tr>
          <td>&nbsp;</td>
        </tr>
        
        
        <tr>
          <td class="etichetta">Vecchia password</td>
          <td class="campo">
            <af:textBox type="password"  name="oldPwd"  value="" 
              title="Vecchia password"  required="true"
              size="20" maxlength="20" 
              classNameBase="input"
              />
          </td>
        </tr>



        <tr>
          <td class="etichetta">Nuova password</td>
          <td class="campo">
            <af:textBox type="password" name="newPwd"  value="" 
              title="Nuova password"  required="true"
              size="20" maxlength="20" 
              classNameBase="input"
              />
          </td>
        </tr>




        <tr>
          <td class="etichetta">Conferma nuova password</td>
          <td class="campo">
            <af:textBox type="password" name="newPwd2"  value="" 
              title="Conferma nuova password"  required="true"
              size="20" maxlength="20" 
              classNameBase="input"
              />
          </td>
        </tr>




        

      
     
            

          
   

