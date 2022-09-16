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
Vector rows = sb.getAttributeAsVector("PR_STU.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>


<table class=lista_ass>
    <tr><td class=lista_ass2 colspan=7>Titoli di studio</td>
    </tr>
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
    <tr>
        <td width="25" height=25 valign=center>            
            <input type="checkbox" id="I_PR_STU<%=i%>" >
            <script>inputs.push(new Riga('PR_STU','PR_STU',<%=row.getAttribute("PRGSTUDIO").toString()%>,document.getElementById("I_PR_STU<%=i%>")));</script>
        
        <td class="etichetta2">Tipo <td class="campo3"><%= Utils.notNull(row.getAttribute("DESTIPOTITOLO"))%>
        <td class="etichetta2">Descrizione <td class="campo3"><%= Utils.notNull(row.getAttribute("DESTITOLO"))%>
        <td class="etichetta2">Anno<td class="campo3"><%= Utils.notNull(row.getAttribute("NUMANNO"))%>
    </tr>
<%      //count++;
    }  %>
<% if(rows.size()>0) {%>
    <tr><td colspan=7><input type="checkbox" onclick="tutti(this,'PR_STU')">Seleziona tutti</td>               
        
    </tr>
<%}%>   
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>
