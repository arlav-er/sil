<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*, 
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, it.eng.sil.security.PageAttribs" %>

      
  
<%
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("PR_ESP_L.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>


<table class=lista_ass >
    <tr><td class=lista_ass2 colspan=9 >Esperienze lavorative</td>
    </tr>
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
    <tr>
        <td width="25" height=25 valign=center>            
            <input type="checkbox" id="I_PR_ESP_L<%=i%>">
            <script>inputs.push(new Riga('PR_ESP_L','PR_ESP_L',<%=row.getAttribute("PRGESPLAVORO").toString()%>,document.getElementById("I_PR_ESP_L<%=i%>")));</script>
        
        <td class="etichetta2">Mansione <td class="campo3"><%= Utils.notNull(row.getAttribute("DESCRIZIONEMANS"))%>
        <td class="etichetta2">Contratto <td class="campo3"><%= Utils.notNull(row.getAttribute("DESCRIZIONECONTR"))%>
        <td class="etichetta2">Inizio <td class="campo3"><%= Utils.notNull(row.getAttribute("NUMMESEINIZIO"))%>&nbsp;<%= Utils.notNull(row.getAttribute("NUMANNOINIZIO"))%>
        <td class="etichetta2">Fine <td class="campo3"><%= Utils.notNull(row.getAttribute("NUMMESEFINE"))%>&nbsp;<%= Utils.notNull(row.getAttribute("NUMANNOFINE"))%>
    </tr>
<%     // count++;
    }  %>
<% if(rows.size()>0) {%>
    <tr><td colspan=9><input type="checkbox" onclick="tutti(this,'PR_ESP_L')">Seleziona tutti</td>               
        
    </tr>
<%}%>   
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>
