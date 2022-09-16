<%
  //definisce i due boolean che indicano lo stato della validazione massiva
  //Controllo se in sessione ci sono dei risultati
  boolean risultatiInSessione = false;
  if (sessionContainer.getAttribute("RISULTATICORRENTI") != null) {risultatiInSessione = true;}
  //controllo se in sessione è in atto una validazione massiva
  boolean validazioneInCorso = false;
  if (sessionContainer.getAttribute("VALIDATOREMASSIVOCORRENTE") != null) {validazioneInCorso = true;}  
%>