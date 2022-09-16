<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%
    String dataInizioCM      = null;
    String dataFineCM        = null;
    String TipoIscrDescr     = null;
    String tipoInvaliditaDescr = null;
    BigDecimal percInval     = null;
    Vector collMiratoRows= (Vector)request.getAttribute("COLLOCAMENTO_MIRATO");
    SourceBean row = null;
    if(collMiratoRows != null && !collMiratoRows.isEmpty()) {   
        row  = (SourceBean) collMiratoRows.elementAt(0);
        dataInizioCM     = (String)     row.getAttribute("DATINIZIO"); 
        dataFineCM       = (String)     row.getAttribute("DATFINE");
        TipoIscrDescr    = (String)     row.getAttribute("DESCRIZIONEISCR");
        tipoInvaliditaDescr = (String)  row.getAttribute("DESCRIZIONEINV");
        percInval        = (BigDecimal) row.getAttribute("NUMPERCINVALIDITA");
    }
%>

<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: collocamento mirato</div>
    </td>
</tr>
<tr>
   <td class="etichetta" >Data inizio </td><td colspan="3"><b><%=Utils.notNull(dataInizioCM)%></b></td>
</tr>
<tr>
   <td class="etichetta" >Tipo lista </td>
   <td colspan="3"><b><%=TipoIscrDescr%></b></td>
</tr>
<tr>
   <td class="etichetta" >Tipo invalidità</td>
   <td><b><%=Utils.notNull(tipoInvaliditaDescr)%></b></td>
   <td class="etichetta" >Percentuale invalidità</td>
   <td><b><%=Utils.notNull(percInval)%></b></td>
</tr>
