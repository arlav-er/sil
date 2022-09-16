<%
	
    SourceBean rowTestata = (SourceBean) serviceResponse.getAttribute("PROF_TESTATA_PROFILO.rows.row");
  
    String strdenominazioneT= (String) rowTestata.getAttribute("strdenominazione");             
    String strflgstandard= (String) rowTestata.getAttribute("strflgstandard");
 	
	%>
       <br>

      <%out.print(htmlStreamTop);%>
      <table class="main">

                      <tr>
			            <td> 
			            Denominazione profilo: <b><%=strdenominazioneT%></b>
			            
			            </td>
			          </tr>

                      <tr>
                      	<td> 
			            Profilo standard: <b><%=strflgstandard%> 
			            </td>

			            
			          </tr>
	
	
	   </table>
  <%out.print(htmlStreamBottom);%>
