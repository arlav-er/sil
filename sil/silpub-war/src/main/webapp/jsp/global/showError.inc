<%
if (!seh.isAuthorized()){
%>
  <P class="LISTAERROR">Non si possiede l'autorizzazione</P>
<%
}else{
%>

  <br><br><br><br>

  <%
 //   String mainErr="";
    String altriDettagli="";
  
    Collection errList = seh.getErrorCollection();
    Iterator iter= errList.iterator();
    Exception ex = null;
 //   int i=1;
 //   int len=errList.size();
  
    while (iter.hasNext()){
        ex = (Exception) iter.next();
 //       if (i==len)
 //         mainErr = ex.getMessage();
 //       else
            altriDettagli += "<br>" +  ex.getMessage();
//        i++;    
     }


  
  %>

  <script>
      function onOff(){
          var spunta = document.all("cb");  
          if (spunta.checked){
              dett.style.display="";
          }    
          else {    
              dett.style.display="none";
          }
      }
  </script>


  <FIELDSET width="600">
    <LEGEND>Errore</LEGEND>
      <P>
        <table width="400">
          <tr>
              <td  class="msg">
                  <p class="LISTAERROR">Si è verificato un errore nel sistema.</p>
                  <p>Per segnalare l'anomalia all'amministratore del sistema, utilizzare il seguente indirizzo di posta elettronica:<br> 
                    <a href="mailto:admin-ambiente@regione.emilia-romagna.it?subject=Segnalazione anomalia nell'applicazione dei Siti Inquinati">
                        admin-ambiente@regione.emilia-romagna.it
                    </a>
                  </p>
              </td>
          </tr>

          <tr>
              <td><input type="checkbox" value="N" ID="cb" onClick="onOff()">Visualizza dettagli tecnici</td>
          </tr>
        </table>

        <div id="dett" style="display:none">  
        <table width="400">
          <tr>
              <td>
                  <%=altriDettagli%>
              </td>
          </tr>
           <tr>
              <td>
             
              </td>
          </tr>
           <tr>
              <td>
             
              </td>
          </tr>
        </table>
      </div>
      </P>
      <p></p>
    </LEGEND>
  </FIELDSET>


<%
} // else
%>