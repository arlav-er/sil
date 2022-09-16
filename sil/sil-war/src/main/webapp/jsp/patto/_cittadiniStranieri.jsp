<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,java.util.Enumeration,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%    
    Vector permSoggiornoRows= (Vector)request.getAttribute("PERMESSO_SOGGIORNO");
    SourceBean row = null;
    String datScad         = null;
    String datRichiesta    = null;
    String motivoRilDesc   = null;
    String statoRicDesc    = null;
    if( permSoggiornoRows != null && !permSoggiornoRows.isEmpty())  { 
        row  = (SourceBean) permSoggiornoRows.elementAt(0);
        datScad         = (String)      row.getAttribute("DATSCADENZA");
        datRichiesta    = (String)      row.getAttribute("DATRICHIESTA");
        motivoRilDesc   = (String)      row.getAttribute("DESCRIZIONEMOT");
        statoRicDesc    = (String)      row.getAttribute("DESCRIZIONERICH");
    }
    
%>

<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sui cittadini stranieri</div>
    </td>
</tr>

<tr>
  <td class="etichetta">Scadenza permesso soggiorno</td>
  <td colspan="3"><b><%=Utils.notNull(datScad)%></b></td>
</tr>
<tr>
  <td class="etichetta">Data richiesta/Sanatoria</td>
  <td colspan="3"><b><%=Utils.notNull(datRichiesta)%></b></td>
</tr>
<tr>
  <td class="etichetta">Stato richiesta</td>
  <td colspan="3"><b><%=Utils.notNull(statoRicDesc)%></b></td>
</tr>
<tr>
  <td class="etichetta">Motivo permesso di soggiorno</td>
  <td colspan="3"><b><%=Utils.notNull(motivoRilDesc)%></b></td>
</tr>