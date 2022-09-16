<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%
    Vector statoOccRows= (Vector)request.getAttribute("OBBLIGO_FORMATIVO");
    SourceBean row = null;
    String obblForma_flg      =null; 
    String obblScolastico_flg =null; 
    String descMod            =null;
    if(statoOccRows != null && !statoOccRows.isEmpty()) { 
        row   = (SourceBean) statoOccRows.elementAt(0);
        obblForma_flg      = (String) row.getAttribute("FLGOBBLIGOFORMATIVO");
        obblScolastico_flg = (String) row.getAttribute("FLGOBBLIGOSCOLASTICO");
        descMod            = (String) row.getAttribute("DESCRIZIONE");
    }
%>

<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sull'assolvimento dell'obbligo formativo</div>
    </td></tr>
<tr>
    <td class="etichetta" >Obbligo&nbsp;formativo assolto </td><td><b><%=Utils.notNull(obblForma_flg)%></b></td>
    <td class="etichetta" >Obbligo&nbsp;scolastico assolto </td><td><b><%=Utils.notNull(obblScolastico_flg)%></b></td>
</tr>
<tr>
    <td class="etichetta" >Modalità di assolvimento obbligo </td><td colspan="3"><b><%=Utils.notNull(descMod)%></b></td>
</tr>

