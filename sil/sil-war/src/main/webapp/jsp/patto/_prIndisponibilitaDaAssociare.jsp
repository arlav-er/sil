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
Vector rows = sb.getAttributeAsVector("PR_IND.ROWS.ROW");
SourceBean row = null;
////BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
SourceBean serviceRequest = (SourceBean)request.getAttribute("SERVICE_REQUEST");
SessionContainer sessionContainer= (SessionContainer)request.getAttribute("SESSION_CONTAINER");
PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),"IndispPage");
//int count = countObj.intValue();
boolean canInsert= pageAtts.containsButton("inserisci");

%>
<table width=90% >
    <tr>
        <td>
            <table style="border-collapse:collapse" width="100%">
                <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                    <td style="border: 1 solid;border-right: none"  align=center>Indisponibillità presso aziende</td>
                    <td style="border: 1 solid; border-left:none" align=right>        
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td></td></tr>       
    <tr>
        <td>

    <table width="100%">  
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>
  
        <tr>
            <td width="25" height=25 valign=middle>            
                <input type="checkbox" id="I_PR_IND<%=i%>"></td>
                <script>inputs.push(new Riga('PR_IND','PR_IND',<%=row.getAttribute("prgIndisponibilita").toString()%>,document.getElementById("I_PR_IND<%=i%>")));</script>
        
            <td class="etichetta2">Indisponibilità </td><td class="campo3"><%= Utils.notNull(row.getAttribute("strRagSocialeAzienda"))%></td>        
        </tr>

<%      //count++;
    }  %>
        </table>
    </td>
</tr>

<!--
    <tr>
        <td colspan=9><button onclick="tutti('AM_CM_IS')" class=pulsanti>seleziona tutti</button><button class=pulsanti onclick="nessuno('AM_CM_IS')">deseleziona tutti</button></td>
    </tr>
    -->
    <%--
    <tr><tr><td colspan=3><input type="checkbox" onclick="tutti(this,'PR_IND')">Seleziona tutti</td>
        
    </tr>--%>
    



    <tr>
        <td>
            <table width="100%">
                <tr>
                    <% if(rows.size()>0) {%>
                    <td align=right><input type="checkbox" onclick="tutti(this,'PR_IND')">Seleziona tutti</td> 
                     <%}%>
                    <td width=20></td>
                    <td align=center width="30%">
                    <%if (canInsert) {%><button class=pulsanti onclick="aggiungi('IndispPage')">Aggiungi indisponibilità</button><%} else {%>
                    &nbsp; <%}%>
                    </td>
                </tr>    
            </table>
        </td>
    </tr>

</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>
