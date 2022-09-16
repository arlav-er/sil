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

//Vector rows = serviceResponse.getAttributeAsVector("M_GETTABELLENONLEGATEALPATTO.AM_IND_T.ROWS.ROW");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("AM_IND_T.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>

<table class=lista_ass >
    <tr><td class=lista_ass2 colspan=7 >Indisponibilità temporanee</td></tr>    
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
    <tr>
        <td width="25" height=25 valign=center>
<%--            <input type="hidden" name="PRGINDISPTEMP_V" value="<%=row.getAttribute("PRGINDISPTEMP")%>" id="C<%=i%>" >--%>
            <input type="checkbox" id="I_AM_IND_T<%=i%>">
            <script>inputs.push(new Riga('AM_IND_T','AM_IND_T',<%=row.getAttribute("PRGINDISPTEMP").toString()%>,document.getElementById("I_AM_IND_T<%=i%>")));</script>
        
        <td class="etichetta2">Tipo <td class="campo3"><%= Utils.notNull(row.getAttribute("DESCRIZIONE"))%>
        <td class="etichetta2">Data inizio<td class="campo3"><%= Utils.notNull(row.getAttribute("DATINIZIO"))%>
        <td class="etichetta2">Data fine<td class="campo3"><%= Utils.notNull(row.getAttribute("DATFINE"))%>
    </tr>
    
<%      //count++;
    }  %>
<% if(rows.size()>0) {%>
    <tr><td colspan=7><input type="checkbox" onclick="tutti(this,'AM_IND_T')">Seleziona tutti</td>
        
        <%--<button onclick="controlla()">associa</button></td>--%>
    </tr>
<%}%>    
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>