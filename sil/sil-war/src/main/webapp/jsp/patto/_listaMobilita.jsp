<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%
    Vector mobilitaRows= (Vector)request.getAttribute("MOBILITA");
    SourceBean row = null;
    String dataInizioMob   = null;
    //   String dataFineMob     = null;
    String mobTipoDesc     = null;
    String indennita_flg   = null;

    if(mobilitaRows != null && !mobilitaRows.isEmpty()) { 
        row  = (SourceBean) mobilitaRows.elementAt(0);
        dataInizioMob  = (String)  row.getAttribute("DATINIZIO"); 
        //dataFineMob    = (String)  row.getAttribute("DATFINE");
        mobTipoDesc    = (String)  row.getAttribute("DESCRIZIONE");
        indennita_flg  = (String)  row.getAttribute("FLGINDENNITA");
    }
%>

<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: mobilità</div>
    </td></tr>
</tr>
<tr>
   <td class="etichetta" >Data inizio </td><td><b><%=Utils.notNull(dataInizioMob)%></b></td>
   <td class="etichetta" colspan="2"><%--Data fine </td><td><b><%=Utils.notNull(dataFineMob)%></b>--%></td>
</tr>
<tr>
   <td class="etichetta" >Tipo lista</td>
   <td colspan="3"><b><%=Utils.notNull(mobTipoDesc)%></b></td>
</tr>
<tr>
   <td class="etichetta" >Indennità</td>
   <td colspan="3"><b><%=Utils.notNull(indennita_flg)%></b></td>
</tr>
