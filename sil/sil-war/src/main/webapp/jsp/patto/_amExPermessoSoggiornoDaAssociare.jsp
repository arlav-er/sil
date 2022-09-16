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

      
<%--@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" --%>
<%
//SourceBean sb = (SourceBean)serviceResponse.getAttribute("M_GETTABELLENONLEGATEALPATTO");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("AM_EX_PS.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>


<table class=lista_ass width="90%">
    <tr ><td class=lista_ass2  colspan=7 >Documento Extracomumitario</tr>
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
    <tr>
        <td width="25" height=25 valign=middle>            
            <input type="checkbox" id="I_AM_EX_PS<%=i%>"></td>
            <script>inputs.push(new Riga('AM_EX_PS','AM_EX_PS',<%=row.getAttribute("prgpermsogg").toString()%>,document.getElementById("I_AM_EX_PS<%=i%>")));</script>
        
        <td class="etichetta2">Tipologia </td><td class="campo3"><%= Utils.notNull(row.getAttribute("statusDescr"))%></td>
        <td class="etichetta2">Scadenza </td><td class="campo3"><%= Utils.notNull(row.getAttribute("DATSCADENZA"))%></td>
        <td class="etichetta2">Data richiesta </td><td class="campo3"><%= Utils.notNull(row.getAttribute("datrichiesta"))%></td>
    </tr>
<%     // count++;
    }  %>
<% if(rows.size()>0) {%>
<!--
    <tr>
        <td colspan=9><button onclick="tutti('AM_EX_PS')" class=pulsanti>seleziona tutti</button><button class=pulsanti onclick="nessuno('AM_EX_PS')">deseleziona tutti</button></td>
    </tr>
    -->
    <tr><tr><td colspan=7 style="padding-left: 30"><input type="checkbox" onclick="tutti(this,'AM_EX_PS')">Seleziona tutti</td>
        
    </tr>
<%}%>   
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>