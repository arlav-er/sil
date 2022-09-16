<%
  //Gestione oggetto movimento residente in sessione
  NavigationCache mov = null;
  if (sessionContainer.getAttribute("MOVIMENTOCORRENTE") == null) {
  	//Se non lo trovo lo creo a partire da questo array di campi.
    String[] fields = {"PRGMOVIMENTO", "prgMovimentoPrec", "PRGMOVIMENTOAPP", "PrgMovimentoSucc", "prgAzienda",
    "prgUnita", "cdnLavoratore", "datComunicaz", "datInizioMov", "datInizioMovCes", "datInizioMovTra", "codTipoMov", 
    "strMatricola", "codMonoTempo", "datFineMov", "codOrario", "datFineMovPro", "numOreSett", "codTipoAss", "codNormativa",
    "datCFL", "strNumCFL", "strTipoCFL", "codMansione", "strDesAttivita",
    "codContratto", "flgSocio", "codCcnl", "decRetribuzioneMen" , "decRetribuzioneAnn" , "numLivello", "flgLegge68", "flgAssObbl", "codCatAssObbl",
    "flgLavDomicilio", "codAgevolazione", "codMvCessazione", "CODMVCESSAZIONEPREC", "numLivelloCes",
    "strNote", "codGrado", "flgArtigiana", "CODMONOPROV", "CODSTATOATTO", "strCCNL",
    "cdnUtIns", "dtmIns", "cdnUtMod", "dtmMod", "numKloMov", "strPosInps", "strPatInail",
    "prgAziendaUtiliz", "strLuogoDiLavoro", "FLGINTERASSPROPRIA", "PRGUNITAUTILIZ", "DATAINIZIOAVV",
    "DATFINEMOVEFFETTIVA", "CODMONOTIPOFINE", "CODMONOMOVDICH", "NUMPROROGHE", "CODMONOTEMPOAVV",
    "datFineMovPrec", "strCodiceFiscaleAz", "strPartitaIvaAz", "strRagioneSocialeAz", "codCCNLAz",
    "codTipoAzienda", "codNatGiuridicaAz", "strNumAlboInterinali", "flgDatiOk", "descrCCNLAz",
    "DESCRTIPOAZIENDA", "natGiuridicaAz", "strIndirizzoUAz", "strNumRegistroCommitt",
    "strComuneUAz", "strLocalitaUAz", "strTelUAz", "strFaxUAz", "codAtecoUAz", "strDesAtecoUAz",
    "comProvUAz", "strCapUaz", "strCodiceFiscaleLav", "strNomeLav", "strCognomeLav", "flgCfOk",
    "datNascLav", "strCodiceFiscaleAzUtil", "strPartitaIvaAzUtil", "strRagioneSocialeAzUtil",
    "codTipoAziendaUtil", "DESCRTIPOAZIENDAUTIL", "strIndirizzoUAzUtil", "strComuneUAzUtil",
    "comProvUAzUtil", "CODUAINTCOM", "STRUAINTCAP", "descrCCNLAzUtil", "descrMansione", "descrTipoMansione", "descrTipoAss",
    "numAnnoProt", "STRSTATOATTO", "numProtocollo", "KLOCKPROT", "CODCPI", "CODCPILAV", "STRENTERILASCIO",
    "NUOVODOCUMENTO", "NUMGGTRAMOVCOMUNICAZIONE", "STRAZINTNUMCONTRATTO", "DATAZINTINIZIOCONTRATTO", "DATAZINTFINECONTRATTO",
    "STRAZINTRAP", "NUMAZINTSOGGETTI", "NUMAZINTDIPENDENTI", "strTipoMansioneTutore", "DESCMANSIONE", 
    "STRCOGNOMETUTORE", "STRNOMETUTORE", "STRCODICEFISCALETUTORE", "FLGTITOLARETUTORE", "NUMANNIESPTUTORE", "STRLIVELLOTUTORE", 
    "CODMANSIONETUTORE", "STRMANSIONETUTORE", "STRTIPOMANSIONE", "NUMMESIAPPRENDISTATO", "STRNOTE", "FLGARTIGIANA",
	"DATAUTORIZZAZIONE", "NUMAUTORIZZAZIONE", "STRLIVELLO", "STRLIVELLOFINALE", 
	"CODTIPOCFL", "NUMDURATACFL", "DATFINEVALIDITA", "codMonoTipo", "currentcontext", "DATINIZIOMOVPREC",
	"STRREFERENTE", "oraProt", "dataProt", "tipoProt", "permettiImpatti", "FlgDatiAzIntValidi", "COLLEGATO",
	"CODTIPOTITOLOlav", "NUMGGPREVISTIAGR", "NUMGGEFFETTUATIAGR", "PRGMOVIMENTORETT", "NUMKLOMOVRETT",
	"CODMONOMOVDICHRETT", "CODSTATOATTORETT", "DATCOMUNICAZRETT", "NUMGGTRAMOVCOMUNICAZRETT", "CODMOTANNULLAMENTORETT",
	"DATAINIZIOAVVCEV", "FLGAUTORIZZADURATATD", "codGradoPrec","MODIFICATAAZIENDA","PRGRISULTATO",
	"CODLAVORAZIONE","STRVERSIONETRACCIATO", "CODQUALIFICASRQ", "DESCQUALIFICASRQ",
	"NUMCONVENZIONE", "DATCONVENZIONE", "DATFINESGRAVIO", "DECIMPORTOCONCESSO", "CODTIPOCOMUNIC", "CODCOMUNICAZIONE",
    "CODTIPODOCEX", "STRNUMDOCEX", "CODMOTIVOPERMSOGGEX", "CODTIPOENTEPREV", "STRCODICEENTEPREV", "FLGSOCIO", 
    "CODTIPOTRASF", "FLGTRASFER", "STRCODICEFISCALEAZPREC", "CODCOMAZPREC", "STRINDIRIZZOAZPREC", "CODTIPOCONTRATTO",  
    "STRINDENNITASOM", "DATINIZIORAPLAV", "DATFINERAPLAV", "STRNUMAGSOMMINISTRAZIONE", "CODSOGGETTO", "CODENTE", "DECINDENSOM",
    "LAVORATORECOLLMIRATO", "CODLAVORAZIONECES", "DATFINEDISTACCO", "CODAZINTCCNL", "strRagioneSocialeAzTruncTra" , "strRagioneSocialeAzTra",
    "strIndirizzoUAzTra", "codComuneUAzTra", "strCapUAzTra", "DATSCADENZALav", "STRENTE", "FLGLAVOROAGR", "FLGLAVOROAGRCES", "FLGDISTPARZIALE",
    "FLGDISTAZESTERA", "FLGSISTEMAZIONEALLOGGIATIVA", "FLGPAGAMENTORIMPATRIO", "DATFINEAFFITTORAMO",
    "QUESTURALAV", "COGNOMELEGRAPP", "NOMELEGRAPP", "CODCITTADINANZALEGRAPP", "SESSOLEGRAPP", "DATANASCITALEGRAPP",
	"CODCOMNASCITALEGRAPP", "DATASCADENZAPSLEGRAPP", "CODTIPODOCPSLEGRAPP", "STRNUMDOCPSLEGRAPP", "CODMOTIVOPSLEGRAPP", "QUESTURALEGRAPP",
	"FLGAZESTERA", "CODFISCAZESTERA", "RAGSOCAZESTERA", "CODTIPOENTEPROMOTORE", "DATFINEPF", "FLGCONTRATTOTI", 
	"FLGSOGGINITALIA", "FLGLAVOROINMOBILITA", "FLGLAVOROSTAGIONALE", "STRCODFISCPROMOTORETIR", "FLGPROSECUZIONE",
	"CODVARIAZIONE", "STRDENOMINAZIONETIR", "CODTIPOLOGIATIR", "CODCATEGORIATIR", "CODSOGGPROMOTOREMIN"};
	
    mov = new NavigationCache(fields);
    mov.enable();    

    sessionContainer.setAttribute("MOVIMENTOCORRENTE", mov);
    sessionContainer.delAttribute("SCELTAUNITAAZIENDA");
  } else {
  	//Se c'è già lo prelevo e basta
    mov = (NavigationCache) sessionContainer.getAttribute("MOVIMENTOCORRENTE");
  }
  //Lo aggiorno a partire dalla ServiceRequest
    mov.setFieldsFromSourceBean(serviceRequest);
%>