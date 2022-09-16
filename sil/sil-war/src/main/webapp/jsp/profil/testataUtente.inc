<%
	
    SourceBean rowTestata = (SourceBean) serviceResponse.getAttribute("PROF_TESTATA_UTENTE.rows.row");
  
    String strloginT= (String) rowTestata.getAttribute("strlogin");             
    String strcognomeT= (String) rowTestata.getAttribute("strcognome");
    String strnomeT= (String) rowTestata.getAttribute("strnome");                    
	String strabilitatoT=(String) rowTestata.getAttribute("strabilitato");
	String dat_init_valT=(String) rowTestata.getAttribute("dat_init_val");
	String dat_fine_valT=(String) rowTestata.getAttribute("dat_fine_val");
	
	%>
       <br>

      <%out.print(htmlStreamTop);%>
      <table class="main">

                      <tr>
			            <td> 
			            Login: <b><%=strloginT%></b>
			            Cognome e nome: <b><%=strcognomeT%>&nbsp;<%=strnomeT%></b>
			            </td>
			          </tr>

                      <tr>
                      	<td> 
			            <b><%=strabilitatoT%></b>
			            </td>

			            
			          </tr>
	
	
                  	 <tr>
                      	<td> 
			            Periodo di validità: 
			            Dal <b><%=dat_init_valT%></b>
   			            al <b><%=dat_fine_valT%></b>
			            </td>

			            
			          </tr>
	   </table>
  <%out.print(htmlStreamBottom);%>
