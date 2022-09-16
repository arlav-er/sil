<%
	// FASE 1: se è abilitata la lookup, non posso avere "fissata" l'entità corrispondente
	if (lookLavoratore) {
		cdnLavoratore = "";
	}
	if (lookAzienda) {
		prgAzienda = "";
		prgUnita = "";
	}

	// FASE 2: ACCROCCHIO DELL'ULTIMO MINUTO:
	// Il codice in ingresso forza l'uso o meno della lookup relativa,
	// in modo indipendente tra lavoratore e azienda.
	// (PRIMA ERA: se ho in ingresso il cdnLavoratore oppure prgAzienda o entrambi --> lookLav/Azi = F/F
	//   altrimenti (nessuno dei due è definito) --> lookLav/Azi = T/T )
	//
	// SI VEDA IL DOCUMENTO DI ANALISI TECNICA "DOCUMENTI-PROTOCOLLO analisi tecnica.doc"
	lookLavoratore = StringUtils.isEmpty(cdnLavoratore);
	lookAzienda    = StringUtils.isEmpty(prgAzienda);
	// In più: definizione automatica del "contesto", se non definito
	if (StringUtils.isEmpty(contesto)) {
		if (StringUtils.isFilled(cdnLavoratore) && StringUtils.isEmpty(prgAzienda))
			contesto = "L";
		else
			if (StringUtils.isEmpty(cdnLavoratore) && StringUtils.isFilled(prgAzienda))
				contesto = "A";
	}
%>