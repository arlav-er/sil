<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,java.util.Enumeration,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%    
    Vector titoloStudioRows= (Vector)request.getAttribute("TITOLI_LAVORATORE");
    SourceBean row = null;
    String titoloStud      = null;
    String titoloStudTipo  = null;
    BigDecimal annoTitStud = null;
    if(titoloStudioRows != null && !titoloStudioRows.isEmpty()) {   
        row  = (SourceBean) titoloStudioRows.elementAt(0);
        titoloStud     = (String)     row.getAttribute("destitolo");
        titoloStudTipo = (String)     row.getAttribute("destipotitolo");
        annoTitStud    = (BigDecimal) row.getAttribute("NUMANNO");
    }
%>
<tr>
    <td colspan="4"><br/><div class="sezione2">Titolo di studio</div>
    </td></tr>
</tr>
<tr>
  <td class="etichetta">Tipo</td>
  <td colspan="3"><b><%=Utils.notNull(titoloStudTipo)%></b></td>
</tr>
<tr>
  <td class="etichetta">Titolo di studio</td>
  <td><b><%=Utils.notNull(titoloStud)%></b></td>
  <td class="etichetta">Anno</td>
  <td><b><%=Utils.notNull(annoTitStud)%></b></td>
</tr>