<%
	
    SourceBean rowTestata = (SourceBean) serviceResponse.getAttribute("M_ProfTestataGruppo.rows.row");
  
    String strdenominazioneG= (String) rowTestata.getAttribute("strdenominazione");             
    String strtipogruppo= (String) rowTestata.getAttribute("strtipogruppo");
 	
	%>
       <br>

      <%out.print(htmlStreamTop);%>
      <table class="main">

                      <tr>
			            <td> 
			            Denominazione gruppo: <b><%=strdenominazioneG%></b>
			            
			            </td>
			          </tr>

                      <tr>
                      	<td> 
			            Tipo gruppo: <b><%=strtipogruppo%> 
			            </td>
			            
			          </tr>
	
	   </table>
  <%out.print(htmlStreamBottom);%>
