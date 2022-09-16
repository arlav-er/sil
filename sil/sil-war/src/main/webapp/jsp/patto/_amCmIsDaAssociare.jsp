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
//SourceBean sb = (SourceBean)serviceResponse.getAttribute("M_GETTABELLENONLEGATEALPATTO");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("AM_CM_IS.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>


<table style="lista_ass" width="90%">
    <tr class="lista_ass">
        <td  colspan=9 class="lista_ass" >Collocamento mirato: iscrizione alle liste speciali</td>
</tr>
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
    <tr>
        <td width="25" height=25 valign=middle>            
            <input type="checkbox" id="I_AM_CM_IS<%=i%>"></td>
            <script>inputs.push(new Riga('AM_CM_IS','AM_CM_IS',<%=row.getAttribute("PRGCMISCR").toString()%>,document.getElementById("I_AM_CM_IS<%=i%>")));</script>
        
        <td class="etichetta2">Tipo iscrizione </td><td class="campo3"><%= Utils.notNull(row.getAttribute("DESCRIZIONEISCR"))%></td>
        <td class="etichetta2">Tipo invalidità </td><td class="campo3"><%= Utils.notNull(row.getAttribute("DESCRIZIONEINV"))%></td>
        <td class="etichetta2">Data inizio</td><td class="campo3"><%= Utils.notNull(row.getAttribute("DATINIZIO"))%></td>
        <td class="etichetta2">Data fine</td><td class="campo3"><%= Utils.notNull(row.getAttribute("DATFINE"))%></td>
    </tr>
<%      //count++;
    }  %>
<% if(rows.size()>0) {%>
<!--
    <tr>
        <td colspan=9><button onclick="tutti('AM_CM_IS')" class=pulsanti>seleziona tutti</button><button class=pulsanti onclick="nessuno('AM_CM_IS')">deseleziona tutti</button></td>
    </tr>
    -->
    <tr><tr><td colspan=9><input type="checkbox" onclick="tutti(this,'AM_CM_IS')">Seleziona tutti</td>
        
    </tr>
<%}%>   
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>
