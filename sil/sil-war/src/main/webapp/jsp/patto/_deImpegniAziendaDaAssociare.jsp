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
Vector rows = sb.getAttributeAsVector("DE_IMPE_AZ.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

%>

<table width=90% >
    <tr>
        <td>
            <table style="border-collapse:collapse" width="100%">
                <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                    <td style="border: 1 solid;border-right: none"  align=center>Impegni dell'azienda</td>
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
                    <td width="25" height=25 valign=center>
                        <input type="checkbox" id="I_DE_IMPE_AZ<%=i%>">
                        <script>inputs.push(new Riga('DE_IMPE','DE_IMPE_L',<%=row.getAttribute("CODIMPEGNO").toString()%>,document.getElementById("I_DE_IMPE_AZ<%=i%>")));</script>
                    </td>
                    <td class="etichetta2">Descrizione</td>
                    <td class="campo3"><%= Utils.notNull(row.getAttribute("STRDESCRIZIONE"))%></td>
                </tr>
    
<%      //count++;
    }  %>
            </table>
        </td>
    </tr>
    <tr>
            <td>
                <table width="100%">
                    <tr>
                        <% if(rows.size()>0) {%>
                        <td align=right><input type="checkbox" onclick="tutti(this,'DE_IMPE_L')">Seleziona tutti</td> 
                         <%}%>
                        <td width=20></td>
                        <td align=center width="30%">&nbsp;</td>
                    </tr>    
                </table>
            </td>
        </tr>

</table>
    

<%
  //  request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>