<%  // *** lookup.inc ***
   // (da includere in una FORM: mantiene il valore di "resultfun" passato alla pagina di lookup)
%>
<input type="HIDDEN" name="resultfun" value="<%= (String)serviceRequest.getAttribute("resultfun") %>">