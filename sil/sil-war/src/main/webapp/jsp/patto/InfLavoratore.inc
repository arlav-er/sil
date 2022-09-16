

    <%

    try{
    
      Vector VettoreLavoratore=null;
      VettoreLavoratore= serviceResponse.getAttributeAsVector("M_Get_Inf_lavoratore.ROWS.ROW");
      String response_xml_Lav="M_Get_Inf_lavoratore.ROWS.ROW";
      SourceBean riga_lav=(SourceBean) serviceResponse.getAttribute(response_xml_Lav);

      String Cognome= (String) riga_lav.getAttribute("Cognome");
      String Nome= (String) riga_lav.getAttribute("Nome");
      String DescCPI= (String) riga_lav.getAttribute("DescCPI");
      String DescStato= (String) riga_lav.getAttribute("DescStato");           
      String DescRagg= (String) riga_lav.getAttribute("DescRagg");
      String dataInizio= (String) riga_lav.getAttribute("datInizio");      

   %>
   <p class="info_lav">  Lavoratore&nbsp;<STRONG><%=Cognome%>&nbsp;<%=Nome%>&nbsp;&nbsp;</STRONG>&nbsp;&nbsp;
        <br><u>Inf. correnti:</u> Elenco Anag. <STRONG><%=dataInizio%></STRONG> CPI Tit.   <STRONG><%=DescCPI%></STRONG>
        Stato occupaz. <STRONG> <%=DescRagg%></STRONG></td>
   </p>
   <%
   }
    catch(Exception ex){
      // non faccio niente
    }    
   %>



