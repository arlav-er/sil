<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,java.util.Enumeration,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%    
    Vector formazProfRows= (Vector)request.getAttribute("FORMAZIONE_PROFESSIONALE");
    SourceBean row = null;
    String corso         = null;
    String complCorso    = null;
    BigDecimal annoCorso = null;


%>

<tr>
  <td colspan="4"><br/><div class="sezione2">Ultima formazione professionale: precedente o in corso</div>
  </td>
</tr>
<%if(formazProfRows !=null){
  Enumeration _enum = formazProfRows.elements();
  if (_enum.hasMoreElements())
  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");
  }
%><tr>
  <td class="etichetta">Nome del corso</td>
  <td colspan="3"><b><%=Utils.notNull(corso)%></b></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td><td ><b><%=Utils.notNull(annoCorso)%></b></td>
  <td class="etichetta">Percorso completato</td><td ><b><%=Utils.notNull(complCorso)%></b></td>
  </tr>

<%for (; _enum.hasMoreElements() ; )
  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");

%><tr>
  <td class="etichetta">Nome del corso</td>
  <td colspan="3"><b><%=Utils.notNull(corso)%></b></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td><td ><b><%=Utils.notNull(annoCorso)%></b></td>
  <td class="etichetta">Percorso completato</td><td ><b><%=Utils.notNull(complCorso)%></b></td>
  </tr>
<%}//for
}//if%>
