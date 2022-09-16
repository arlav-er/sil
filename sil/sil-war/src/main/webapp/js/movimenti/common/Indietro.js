//funzione che consente la navigazione con pulsnate indietro nel footer,
//va inclusa nelle pagine del dettaglio avviamento, trasformazione/proroga
//e cessazione
function indietro() {
  pagina = "";
  if (contesto == "consulta")  { goToNoCheck("MovDettaglioGeneraleConsultaPage");}
  else if (contesto == "inserisci")  { goToNoCheck("MovDettaglioGeneraleInserisciPage");}
  else if (contesto == "valida")  { goToNoCheck("MovValidaDettaglioGeneralePage");}
}