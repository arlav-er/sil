<%
  //Gestione oggetto azienda residente in sessione
  NavigationCache testataAzienda = null;
  if (sessionContainer.getAttribute("AZIENDACORRENTE") != null) {
    sessionContainer.delAttribute("AZIENDACORRENTE");
  }  	
  String[] fields = {"strCodiceFiscale", "strRagioneSociale", "strPartitaIva", 
                    "FLGDATIOK", "codNatGiuridica", "codTipoAzienda", "strSitoInternet",
                    "strDescAttivita", "numSoci", "numDipendenti",
                    "numCollaboratori", "numAltraPosizione", "STRPATINAIL1", "STRPATINAIL2",
                    "STRPATINAIL", "strRepartoInail", "strNumAlboInterinali", "datInizio",
                    "datFine", "strNote"};
  testataAzienda = new NavigationCache(fields);
  sessionContainer.setAttribute("AZIENDACORRENTE", testataAzienda);
%>